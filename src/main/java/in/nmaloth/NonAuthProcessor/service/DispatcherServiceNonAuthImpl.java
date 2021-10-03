package in.nmaloth.NonAuthProcessor.service;

import in.nmaloth.NonAuthProcessor.model.proto.IncomingMessageOuterClass;
import in.nmaloth.rsocketServices.config.model.NodeInfo;
import in.nmaloth.rsocketServices.model.proto.ServerRegistrationOuterClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class DispatcherServiceNonAuthImpl implements DispatcherServiceNonAuth {

    private final MessageServiceNonAuth messageServiceNonAuth;
    private final NodeInfo nodeInfo;


    public DispatcherServiceNonAuthImpl(MessageServiceNonAuth messageServiceNonAuth, NodeInfo nodeInfo) {
        this.messageServiceNonAuth = messageServiceNonAuth;
        this.nodeInfo = nodeInfo;
    }


    @Override
    public Flux requestForStreamForIncoming(String serviceName, String serviceInstance, String route, RSocketRequester rSocketRequester) {

        ServerRegistrationOuterClass.ServerRegistration registration = ServerRegistrationOuterClass.ServerRegistration.newBuilder()
                .setServiceName(nodeInfo.getAppName())
                .setServiceInstance(serviceInstance)
                .setStatusReady(true)
                .build();

        return messageServiceNonAuth.requestForStreamMessageProcessorIn(rSocketRequester,route,registration,serviceName);
    }

    @Override
    public Flux<IncomingMessageOuterClass.IncomingMessage> getOutgoingVisaStream(RSocketRequester rSocketRequester, String serviceInstance) {
        return messageServiceNonAuth.createOutgoingFlux(rSocketRequester,serviceInstance);
    }
}
