package in.nmaloth.NonAuthProcessor.config;

import in.nmaloth.entity.logs.AuthSnapShot;
import in.nmaloth.entity.network.NetworkMessages;
import in.nmaloth.entity.network.NetworkProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;

@Configuration
@ClientCacheApplication
@EnableClusterConfiguration
@ComponentScan(basePackageClasses = {NetworkMessages.class, NetworkProperties.class, AuthSnapShot.class})
@EnableEntityDefinedRegions(basePackageClasses = {NetworkMessages.class,NetworkProperties.class,AuthSnapShot.class})
public class GeodeConfig {
}
