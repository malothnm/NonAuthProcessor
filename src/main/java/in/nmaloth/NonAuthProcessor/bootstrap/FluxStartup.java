package in.nmaloth.NonAuthProcessor.bootstrap;

import in.nmaloth.NonAuthProcessor.constants.BeanConfigNames;
import in.nmaloth.NonAuthProcessor.constants.SchedulerNames;
import in.nmaloth.NonAuthProcessor.model.NonAuthMessage;
import in.nmaloth.NonAuthProcessor.model.proto.IncomingMessageOuterClass;
import in.nmaloth.NonAuthProcessor.service.NonAuthMessageService;
import in.nmaloth.rsocketServices.processor.EventOutGoingProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j

public class FluxStartup implements CommandLineRunner {

    private final ConnectableFlux<NonAuthMessage> connectableFlux;
    private final EventOutGoingProcessor<IncomingMessageOuterClass.IncomingMessage> eventOutGoingProcessor;

    private final NonAuthMessageService nonAuthMessageService;



    public FluxStartup(ConnectableFlux<NonAuthMessage> connectableFlux,
                       @Qualifier(BeanConfigNames.CONNECTOR_OUTGOING_PROCESSOR) EventOutGoingProcessor<IncomingMessageOuterClass.IncomingMessage> eventOutGoingProcessor,
                       NonAuthMessageService nonAuthMessageService) {
        this.connectableFlux = connectableFlux;
        this.eventOutGoingProcessor = eventOutGoingProcessor;
        this.nonAuthMessageService = nonAuthMessageService;
    }

    @Override
    public void run(String... args) throws Exception {

        subscribeRequest();
        subscribeResponse();

        connectableFlux.connect();

    }

    private void subscribeRequest() {

        connectableFlux
                .publishOn(Schedulers.newParallel(SchedulerNames.REQUEST_SCHEDULER))
                .filter(nonAuthMessage -> nonAuthMessageService.isMessageRequest(nonAuthMessage))
                .flatMap(nonAuthMessage -> nonAuthMessageService.processRequests(nonAuthMessage))
                .map(nonAuthOutgoingMessage -> nonAuthMessageService.createIncomingMessage(nonAuthOutgoingMessage))
                .doOnNext(incomingMessage -> {
                    if(incomingMessage.hasContainerId()){
                        eventOutGoingProcessor.processMessage(incomingMessage,incomingMessage.getContainerId());
                    } else {
                        eventOutGoingProcessor.processMessage(incomingMessage);
                    }
                }).subscribe();

    }

    private void subscribeResponse() {

        connectableFlux
                .publishOn(Schedulers.newParallel(SchedulerNames.RESPONSE_SCHEDULER))
                .filter(nonAuthMessage -> !nonAuthMessageService.isMessageRequest(nonAuthMessage))
                .doOnNext(nonAuthMessage -> nonAuthMessageService.processResponse(nonAuthMessage))
                .subscribe()
                ;
    }


}
