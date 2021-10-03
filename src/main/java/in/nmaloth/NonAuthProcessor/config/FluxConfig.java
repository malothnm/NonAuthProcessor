package in.nmaloth.NonAuthProcessor.config;

import in.nmaloth.NonAuthProcessor.constants.BeanConfigNames;
import in.nmaloth.NonAuthProcessor.constants.SchedulerNames;
import in.nmaloth.NonAuthProcessor.model.NonAuthMessage;
import in.nmaloth.NonAuthProcessor.model.proto.OutgoingMessageOuterClass;
import in.nmaloth.NonAuthProcessor.service.NonAuthMessageService;
import in.nmaloth.rsocketServices.listeners.MessageListener;
import in.nmaloth.rsocketServices.listeners.MessageListenerImpl;
import in.nmaloth.rsocketServices.processor.EventIncomingProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Configuration
@Slf4j
public class FluxConfig {

    private final NonAuthMessageService nonAuthMessageService;

    public FluxConfig(NonAuthMessageService nonAuthMessageService) {
        this.nonAuthMessageService = nonAuthMessageService;
    }


    @Bean(BeanConfigNames.INCOMING_FLUX)
    public ConnectableFlux<NonAuthMessage> getConnectFlux(@Qualifier(BeanConfigNames.INCOMING_PROCESSOR) EventIncomingProcessor<OutgoingMessageOuterClass.OutgoingMessage> eventIncomingProcessor){

        MessageListener<OutgoingMessageOuterClass.OutgoingMessage> messageListener = new MessageListenerImpl<>();
        Flux<OutgoingMessageOuterClass.OutgoingMessage> flux = Flux.create(messageListener::setFluxSink);
        eventIncomingProcessor.registerFluxListeners(messageListener);

        return flux.publishOn(Schedulers.newParallel(SchedulerNames.INCOMING_SCHEDULER))
                .doOnNext(outgoingMessage -> log.info(" ##############%%%%%%Entered the Flux") )
                .map(outgoingMessage -> nonAuthMessageService.createNonAuthMessage(outgoingMessage))
                .onErrorContinue((throwable, o) -> logErrors(throwable,o))
                .publish();


    }

    private void logErrors(Throwable throwable, Object o) {

        throwable.printStackTrace();
        log.error(o.toString());
    }
}
