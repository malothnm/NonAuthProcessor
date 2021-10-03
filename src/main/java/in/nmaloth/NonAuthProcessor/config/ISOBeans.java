package in.nmaloth.NonAuthProcessor.config;

import com.nithin.iso8583.iso.config.LoadingConfig;
import com.nithin.iso8583.iso.elementdef.element.ISOProtoElement;
import com.nithin.iso8583.iso.message.Header.visa.VisaProtoHeaderImpl;
import com.nithin.iso8583.iso.message.ISOMessageFactory;
import com.nithin.iso8583.iso.message.ISOMessageFactoryImpl;
import in.nmaloth.NonAuthProcessor.constants.BeanConfigNames;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("visa")
public class ISOBeans {

    @Bean
    public ISOMessageFactory getISOMessageFactory() throws IOException {

        ISOMessageFactory isoMessageFactory = new ISOMessageFactoryImpl();
        isoMessageFactory.loadElementProto("/isomessage/deLoad/DELoad.yml",
                "/isomessage",new VisaProtoHeaderImpl("ascii"));
        return isoMessageFactory;
    }

    @Bean(BeanConfigNames.ISO_EXCEPTION_FILE_UPDATE)
    public ISOProtoElement createExceptionUpdateProto(){

        LoadingConfig loadingConfig = new LoadingConfig();
        return loadingConfig.createProtoElement(127, "/isomessage/DE127/exceptionFile");

    }

    @Bean(BeanConfigNames.ISO_FILE_MAINTENANCE)
    public ISOProtoElement createFileMaintenanceProto(){
        LoadingConfig loadingConfig = new LoadingConfig();
        return loadingConfig.createProtoElement(127,"/isomessage/DE127/pan-maintenance");
    }


}
