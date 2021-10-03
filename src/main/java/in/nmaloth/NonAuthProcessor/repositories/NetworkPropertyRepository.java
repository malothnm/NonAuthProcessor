package in.nmaloth.NonAuthProcessor.repositories;

import in.nmaloth.entity.network.NetworkProperties;
import in.nmaloth.payments.constants.network.NetworkType;
import org.springframework.data.repository.CrudRepository;

public interface NetworkPropertyRepository extends CrudRepository<NetworkProperties, NetworkType> {
}
