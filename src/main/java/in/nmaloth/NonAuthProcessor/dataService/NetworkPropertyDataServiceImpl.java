package in.nmaloth.NonAuthProcessor.dataService;


import in.nmaloth.NonAuthProcessor.repositories.NetworkPropertyRepository;
import in.nmaloth.entity.network.NetworkProperties;
import in.nmaloth.payments.constants.network.NetworkType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class NetworkPropertyDataServiceImpl implements NetworkPropertyDataService {

    private final NetworkPropertyRepository networkPropertyRepository;

    public NetworkPropertyDataServiceImpl(NetworkPropertyRepository networkPropertyRepository) {
        this.networkPropertyRepository = networkPropertyRepository;
    }


    @Override
    public Mono<Optional<NetworkProperties>> fetchNetworkProperty(NetworkType networkType) {

        CompletableFuture<Optional<NetworkProperties>> completableFuture =
                CompletableFuture.supplyAsync(() -> networkPropertyRepository.findById(networkType));
        return Mono.fromFuture(completableFuture);
    }

    @Override
    public Mono<NetworkProperties> updateNetworkProperties(NetworkProperties networkProperties) {

        CompletableFuture<NetworkProperties> completableFuture =
                CompletableFuture.supplyAsync(() -> networkPropertyRepository.save(networkProperties));

        return Mono.fromFuture(completableFuture);
    }
}
