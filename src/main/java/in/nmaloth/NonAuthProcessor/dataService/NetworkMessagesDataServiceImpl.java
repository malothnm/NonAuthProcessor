package in.nmaloth.NonAuthProcessor.dataService;

import in.nmaloth.NonAuthProcessor.repositories.NetworkMessageRepository;
import in.nmaloth.entity.network.NetworkMessages;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class NetworkMessagesDataServiceImpl implements NetworkMessagesDataService{

    private final NetworkMessageRepository networkMessageRepository;

    public NetworkMessagesDataServiceImpl(NetworkMessageRepository networkMessageRepository) {
        this.networkMessageRepository = networkMessageRepository;
    }

    @Override
    public Mono<Optional<NetworkMessages>> fetchNetworkMessages(String messageId) {

        CompletableFuture<Optional<NetworkMessages>> completableFuture =
                CompletableFuture.supplyAsync(() -> networkMessageRepository.findById(messageId));

        return Mono.fromFuture(completableFuture);
    }

    @Override
    public Mono<NetworkMessages> updateNetworkMessages(NetworkMessages networkMessages) {

        CompletableFuture<NetworkMessages> completableFuture =
                CompletableFuture.supplyAsync(() -> networkMessageRepository.save(networkMessages));

        return Mono.fromFuture(completableFuture);
    }
}
