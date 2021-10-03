package in.nmaloth.NonAuthProcessor.dataService;

import in.nmaloth.entity.network.NetworkMessages;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface NetworkMessagesDataService {

    Mono<Optional<NetworkMessages>> fetchNetworkMessages(String messageId);
    Mono<NetworkMessages> updateNetworkMessages(NetworkMessages networkMessages);
}
