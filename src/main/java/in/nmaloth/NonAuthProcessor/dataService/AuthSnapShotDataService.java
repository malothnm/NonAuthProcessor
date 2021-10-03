package in.nmaloth.NonAuthProcessor.dataService;

import in.nmaloth.entity.logs.AuthSnapShot;
import in.nmaloth.entity.logs.SnapshotKey;
import org.apache.zookeeper.server.persistence.SnapShot;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface AuthSnapShotDataService {

    Mono<Optional<AuthSnapShot>> findSnapShot(SnapshotKey snapshotKey);
    Mono<AuthSnapShot> updateSnapShot(AuthSnapShot authSnapShot);
}
