package in.nmaloth.NonAuthProcessor.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import in.nmaloth.entity.network.NetworkProperties;
import in.nmaloth.payments.constants.network.NetworkType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration

public class CacheConfig {

    @Bean
    public Map<NetworkType, NetworkProperties> buildProperties(){
        return new HashMap<>();
    }
}
