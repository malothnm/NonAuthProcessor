package in.nmaloth.NonAuthProcessor.config;


import in.nmaloth.NonAuthProcessor.constants.BeanConfigNames;
import in.nmaloth.NonAuthProcessor.model.proto.IncomingMessageOuterClass;
import in.nmaloth.NonAuthProcessor.model.proto.OutgoingMessageOuterClass;
import in.nmaloth.rsocketServices.processor.EventIncomingProcessor;
import in.nmaloth.rsocketServices.processor.EventIncomingProcessorImpl;
import in.nmaloth.rsocketServices.processor.EventOutGoingProcessor;
import in.nmaloth.rsocketServices.processor.EventOutGoingProcessorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class EventProcessorConfig {

    @Bean(BeanConfigNames.INCOMING_PROCESSOR)
    public EventIncomingProcessor<OutgoingMessageOuterClass.OutgoingMessage> getVisaIncomingProcessor(){
        return new EventIncomingProcessorImpl<>();
    }


    @Bean(BeanConfigNames.CONNECTOR_OUTGOING_PROCESSOR)
    public EventOutGoingProcessor<IncomingMessageOuterClass.IncomingMessage> getOutgoingConnectorProcessor(){
        return new EventOutGoingProcessorImpl<>();
    }


}
