package in.nmaloth.NonAuthProcessor.service;

import in.nmaloth.NonAuthProcessor.model.proto.IncomingMessageOuterClass;
import in.nmaloth.rsocketServices.service.MessageServices;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;

public interface MessageServiceNonAuth extends MessageServices {

    Flux<IncomingMessageOuterClass.IncomingMessage> createOutgoingFlux(RSocketRequester rSocketRequester, String serviceInstance);

}
