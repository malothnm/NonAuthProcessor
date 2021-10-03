package in.nmaloth.NonAuthProcessor.dataService;

import in.nmaloth.NonAuthProcessor.repositories.AuthSnapShotRepository;
import in.nmaloth.entity.logs.AuthSnapShot;
import in.nmaloth.entity.logs.SnapshotKey;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthSnapShotDataServiceImpl implements AuthSnapShotDataService{

    private final AuthSnapShotRepository authSnapShotRepository;


    public AuthSnapShotDataServiceImpl(AuthSnapShotRepository authSnapShotRepository) {
        this.authSnapShotRepository = authSnapShotRepository;
    }

    @Override
    public Mono<Optional<AuthSnapShot>> findSnapShot(SnapshotKey key) {

        CompletableFuture<Optional<AuthSnapShot>> completableFuture = CompletableFuture
                .supplyAsync(()-> authSnapShotRepository.findById(key));

        return Mono.fromFuture(completableFuture);
    }

    @Override
    public Mono<AuthSnapShot> updateSnapShot(AuthSnapShot authSnapShot) {

        CompletableFuture<AuthSnapShot> completableFuture = CompletableFuture
                .supplyAsync(() -> authSnapShotRepository.save(authSnapShot));

        return Mono.fromFuture(completableFuture);
    }


}
