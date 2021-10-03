package in.nmaloth.NonAuthProcessor.dataService;

import in.nmaloth.entity.network.NetworkProperties;
import in.nmaloth.payments.constants.network.NetworkType;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface NetworkPropertyDataService {

    Mono<Optional<NetworkProperties>> fetchNetworkProperty(NetworkType networkType);
    Mono<NetworkProperties> updateNetworkProperties(NetworkProperties networkProperties);
}
