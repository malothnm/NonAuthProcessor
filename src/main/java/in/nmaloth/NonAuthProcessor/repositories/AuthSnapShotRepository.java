package in.nmaloth.NonAuthProcessor.repositories;

import in.nmaloth.entity.logs.AuthSnapShot;
import in.nmaloth.entity.logs.SnapshotKey;
import org.springframework.data.repository.CrudRepository;

public interface AuthSnapShotRepository extends CrudRepository<AuthSnapShot, SnapshotKey> {
}
