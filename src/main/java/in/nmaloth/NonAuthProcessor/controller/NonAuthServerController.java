package in.nmaloth.NonAuthProcessor.controller;



import in.nmaloth.NonAuthProcessor.constants.RouteConstants;
import in.nmaloth.NonAuthProcessor.model.proto.IncomingMessageOuterClass;
import in.nmaloth.NonAuthProcessor.service.DispatcherServiceNonAuth;
import in.nmaloth.rsocketServices.config.model.NodeInfo;
import in.nmaloth.rsocketServices.controller.ServerMessageController;
import in.nmaloth.rsocketServices.model.proto.ServerRegistrationOuterClass;
import in.nmaloth.rsocketServices.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@Slf4j
public class NonAuthServerController extends ServerMessageController {


    private static final String STREAMING_REQUEST_FOR_SERVICE = "######## Streaming request for {} Service from Instance {}";


    private final DispatcherServiceNonAuth dispatcherServiceNonAuth;


    public NonAuthServerController(DispatcherServiceNonAuth dispatcherServiceNonAuth, NodeInfo nodeInfo, ServerService serverService) {
        super(nodeInfo,serverService);
        this.dispatcherServiceNonAuth = dispatcherServiceNonAuth;
    }


    @MessageMapping(RouteConstants.CONNECTOR)
    public Flux<IncomingMessageOuterClass.IncomingMessage> getIncomingMessageFlux(RSocketRequester rSocketRequester,
                                                                                  ServerRegistrationOuterClass.ServerRegistration registration){

        log.info(STREAMING_REQUEST_FOR_SERVICE,registration.getServiceName(),
                registration.getServiceInstance());

        return dispatcherServiceNonAuth.getOutgoingVisaStream(rSocketRequester,registration.getServiceInstance());
    }



}
