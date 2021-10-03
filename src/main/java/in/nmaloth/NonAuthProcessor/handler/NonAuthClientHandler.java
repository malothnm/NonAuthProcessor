package in.nmaloth.NonAuthProcessor.handler;


import in.nmaloth.rsocketServices.config.model.NodeInfo;
import in.nmaloth.rsocketServices.handler.ClientHandler;
import in.nmaloth.rsocketServices.service.DispatcherService;
import in.nmaloth.rsocketServices.service.ServiceTracker;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NonAuthClientHandler extends ClientHandler {



    public NonAuthClientHandler(NodeInfo nodeInfo, ServiceTracker serviceTracker, DispatcherService dispatcherService) {
        super(nodeInfo, serviceTracker, dispatcherService);

    }

}
