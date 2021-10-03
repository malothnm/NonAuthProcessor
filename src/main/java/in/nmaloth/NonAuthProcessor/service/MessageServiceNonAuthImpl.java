package in.nmaloth.NonAuthProcessor.service;

import in.nmaloth.NonAuthProcessor.constants.BeanConfigNames;
import in.nmaloth.NonAuthProcessor.model.proto.IncomingMessageOuterClass;
import in.nmaloth.NonAuthProcessor.model.proto.OutgoingMessageOuterClass;
import in.nmaloth.payments.constants.ids.ServiceNamesConstant;
import in.nmaloth.rsocketServices.listeners.MessageListener;
import in.nmaloth.rsocketServices.listeners.MessageListenerImpl;
import in.nmaloth.rsocketServices.model.proto.ServerRegistrationOuterClass;
import in.nmaloth.rsocketServices.processor.EventIncomingProcessor;
import in.nmaloth.rsocketServices.processor.EventOutGoingProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class MessageServiceNonAuthImpl implements MessageServiceNonAuth {

    public static final String CANCELLING_FLUX_FOR_SERVICE = "Cancelling flux for service {}";
    public static final String TERMINATING_FLUX_FOR_SERVICE = "Terminating  flux for service {}";

    private final EventOutGoingProcessor<IncomingMessageOuterClass.IncomingMessage> inMessageEventOutGoingProcessor;
    private final EventIncomingProcessor<OutgoingMessageOuterClass.OutgoingMessage> incomingEventProcessor;


    public MessageServiceNonAuthImpl(@Qualifier(BeanConfigNames.CONNECTOR_OUTGOING_PROCESSOR) EventOutGoingProcessor<IncomingMessageOuterClass.IncomingMessage> inMessageEventOutGoingProcessor,
                                     @Qualifier(BeanConfigNames.INCOMING_PROCESSOR) EventIncomingProcessor<OutgoingMessageOuterClass.OutgoingMessage> incomingEventProcessor){
        this.inMessageEventOutGoingProcessor = inMessageEventOutGoingProcessor;
        this.incomingEventProcessor = incomingEventProcessor;
    }


    @Override
    public Flux requestForStreamMessageProcessorIn(RSocketRequester rSocketRequester, String route, ServerRegistrationOuterClass.ServerRegistration registration,String serviceName) {

        switch (serviceName){

            case ServiceNamesConstant.CONNECTOR: {
                return rSocketRequester.route(route)
                        .data(registration)
                        .retrieveFlux(OutgoingMessageOuterClass.OutgoingMessage.class)
                        .doOnNext(outgoingMessage -> incomingEventProcessor.processMessage(outgoingMessage))
                        ;
            }

            default: {
                throw  new RuntimeException("Invalid Service Name " + registration.getServiceName());
            }
        }

    }




    @Override
    public Flux<IncomingMessageOuterClass.IncomingMessage> createOutgoingFlux(RSocketRequester rSocketRequester, String serviceInstance) {

        MessageListener<IncomingMessageOuterClass.IncomingMessage> messageListener = new MessageListenerImpl<>();
        Flux<IncomingMessageOuterClass.IncomingMessage> flux = Flux.create(messageListener::setFluxSink);
        inMessageEventOutGoingProcessor.registerFluxListeners(flux,rSocketRequester,messageListener,serviceInstance);


        return flux
                .onBackpressureBuffer()
                .onErrorContinue((throwable, o) -> logErrors(throwable,o))
                .doOnCancel(() -> cancelProcessing(rSocketRequester, inMessageEventOutGoingProcessor, ServiceNamesConstant.CONNECTOR ))
                .doOnTerminate(() -> terminateProcessing(rSocketRequester, inMessageEventOutGoingProcessor, ServiceNamesConstant.CONNECTOR))

                ;
    }

    private void terminateProcessing(RSocketRequester rSocketRequester,
                                     EventOutGoingProcessor eventOutGoingProcessor, String serviceName) {

        log.error(TERMINATING_FLUX_FOR_SERVICE, serviceName);
        eventOutGoingProcessor.removeRegisteredFluxListener(rSocketRequester);
    }

    private void cancelProcessing(RSocketRequester rSocketRequester, EventOutGoingProcessor eventOutGoingProcessor,
                                  String serviceName) {

        log.error(CANCELLING_FLUX_FOR_SERVICE, serviceName);
        eventOutGoingProcessor.removeRegisteredFluxListener(rSocketRequester);

    }


    private void logErrors(Throwable throwable, Object o) {

        throwable.printStackTrace();
        log.error(o.toString());
    }
}
