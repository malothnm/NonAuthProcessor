package in.nmaloth.NonAuthProcessor.bootstrap;


import in.nmaloth.NonAuthProcessor.handler.NonAuthClientHandler;
import in.nmaloth.NonAuthProcessor.service.DispatcherServiceNonAuth;
import in.nmaloth.rsocketServices.config.model.NodeInfo;
import in.nmaloth.rsocketServices.config.model.ServiceEvent;
import in.nmaloth.rsocketServices.service.ClientService;
import in.nmaloth.rsocketServices.service.ServerService;
import in.nmaloth.rsocketServices.service.ServiceEventsService;
import in.nmaloth.rsocketServices.service.ServiceTracker;
import in.nmaloth.zookeepercoordinator.service.CoordinationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.ConnectableFlux;

@Slf4j
@Component
public class Startup implements CommandLineRunner {


    private final ServerService serverService;
    private final DispatcherServiceNonAuth dispatcherService;
    private final ServiceTracker serviceTracker;
    private final ClientService clientService;
    private final NodeInfo nodeInfo;
    private final ConnectableFlux<ServiceEvent> connectableFlux;
    private final ServiceEventsService serviceEventsService;
    private final CoordinationService coordinationService;


    public Startup(ServerService serverService,
                   DispatcherServiceNonAuth dispatcherService,
                   ServiceTracker serviceTracker,
                   ClientService clientService,
                   NodeInfo nodeInfo,
                   ConnectableFlux<ServiceEvent> connectableFlux,
                   ServiceEventsService serviceEventsService,
                   CoordinationService coordinationService) {

        this.serverService = serverService;
        this.dispatcherService = dispatcherService;
        this.serviceTracker = serviceTracker;
        this.clientService = clientService;
        this.nodeInfo = nodeInfo;
        this.connectableFlux = connectableFlux;
        this.serviceEventsService = serviceEventsService;
        this.coordinationService = coordinationService;
    }


    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {

        log.info(" Updating dispatcher Service");
        serviceTracker.updateDispatcherService(dispatcherService);
        clientService.updateClientHandler(new NonAuthClientHandler(nodeInfo,serviceTracker,dispatcherService));
        serverService.createServer();
        serviceEventsService.fluxSubscriptions(connectableFlux);
        coordinationService.houseKeeping();
        coordinationService.setWatchers();


    }
}
