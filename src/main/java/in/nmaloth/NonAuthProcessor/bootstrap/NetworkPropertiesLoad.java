package in.nmaloth.NonAuthProcessor.bootstrap;

import com.google.common.cache.Cache;
import in.nmaloth.NonAuthProcessor.repositories.NetworkPropertyRepository;
import in.nmaloth.entity.network.NetworkProperties;
import in.nmaloth.entity.product.ProductDef;
import in.nmaloth.entity.product.ProductId;
import in.nmaloth.payments.constants.network.NetworkType;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.query.CqEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class NetworkPropertiesLoad implements CommandLineRunner {

    private final NetworkPropertyRepository networkPropertyRepository;
    private final Map<NetworkType, NetworkProperties> cacheNetwork;

    public NetworkPropertiesLoad(NetworkPropertyRepository networkPropertyRepository,
                                 Map<NetworkType, NetworkProperties> cacheNetwork) {
        this.networkPropertyRepository = networkPropertyRepository;
        this.cacheNetwork = cacheNetwork;
    }

    @Override
    public void run(String... args) throws Exception {
            networkPropertyRepository.findAll()
                    .forEach(networkProperties -> cacheNetwork.put(networkProperties.getNetworkType(),networkProperties));
    }

    @ContinuousQuery(name = "networkPropertiesChangeHandler",query = "select * from /networkProperties")
    public void networkPropertiesChange(CqEvent cqEvent){

        if(cqEvent.getBaseOperation().isCreate() || cqEvent.getBaseOperation().isUpdate()) {

            NetworkType networkType = (NetworkType) cqEvent.getKey();
            NetworkProperties networkProperties = (NetworkProperties) cqEvent.getNewValue();
            cacheNetwork.put(networkType,networkProperties);
        }

        if(cqEvent.getBaseOperation().isDestroy() || cqEvent.getBaseOperation().isEviction() ||
                cqEvent.getBaseOperation().isExpiration() || cqEvent.getBaseOperation().isInvalidate()){

            cacheNetwork.remove(cqEvent.getKey());
        }


    }

}
