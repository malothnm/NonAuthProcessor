package in.nmaloth.NonAuthProcessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(
        basePackages = {"in.nmaloth.rsocketServices","in.nmaloth.zookeepercoordinator"},
        basePackageClasses = {NonAuthProcessor.class}
)
@SpringBootApplication
public class NonAuthProcessor {

    public static void main(String[] args) {
        SpringApplication.run(NonAuthProcessor.class,args);
    }
}
