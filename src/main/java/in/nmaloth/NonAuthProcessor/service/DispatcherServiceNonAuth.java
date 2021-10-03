package in.nmaloth.NonAuthProcessor.service;

import in.nmaloth.NonAuthProcessor.model.proto.IncomingMessageOuterClass;
import in.nmaloth.rsocketServices.service.DispatcherService;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;

public interface DispatcherServiceNonAuth extends DispatcherService {

    Flux<IncomingMessageOuterClass.IncomingMessage> getOutgoingVisaStream(RSocketRequester rSocketRequester, String serviceInstance);
}
