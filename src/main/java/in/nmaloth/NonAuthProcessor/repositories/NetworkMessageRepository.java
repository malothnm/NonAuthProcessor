package in.nmaloth.NonAuthProcessor.repositories;

import in.nmaloth.entity.network.NetworkMessages;
import org.springframework.data.repository.CrudRepository;

public interface NetworkMessageRepository extends CrudRepository<NetworkMessages,String> {
}
