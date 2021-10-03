package in.nmaloth.NonAuthProcessor.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.nmaloth.NonAuthProcessor.constants.BeanConfigNames;
import in.nmaloth.NonAuthProcessor.constants.ResourceConstants;
import in.nmaloth.NonAuthProcessor.model.InitializeFields;
import in.nmaloth.NonAuthProcessor.model.InitializeFixedSubElements;
import in.nmaloth.NonAuthProcessor.model.ResponseCodeMapItem;
import in.nmaloth.NonAuthProcessor.model.ResponseCodes;
import in.nmaloth.NonAuthProcessor.response.constants.ResponseTypes;
import in.nmaloth.NonAuthProcessor.response.model.ResponseSummary;
import in.nmaloth.payments.constants.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class LoadingConfigImpl {

    private final ResourceLoader resourceLoader;

    public LoadingConfigImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @Bean(BeanConfigNames.INITIALIZE_VALUES)

    public List<InitializeFixedSubElements> loadInitializeValues(){
        List<InitializeFixedSubElements> initializeFixedSubElementsList = new ArrayList<>();
        Resource resource = resourceLoader.getResource(ResourceConstants.INITIALIZE_VALUES);

        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = resource.getInputStream()){
            Yaml yaml = new Yaml(new SafeConstructor());
            yaml.loadAll(inputStream)
                    .forEach(o ->{
                        InitializeFields initializeFields = objectMapper.convertValue(o, InitializeFields.class);
                        initializeFields.getInitializeElements()
                                .forEach(initializeElement -> {

                                    InitializeFixedSubElements initializeFixedSubElements = InitializeFixedSubElements.builder()
                                            .dataElement(initializeElement.getDataElement())
                                            .maxSubElementNumber(initializeElement.getMaxSubElementNumber())
                                            .subElementsMap(new HashMap<>())
                                            .build();

                                    initializeFixedSubElementsList.add(initializeFixedSubElements);

                                    initializeElement.getSubElements()
                                            .forEach(initializeSubElements -> initializeFixedSubElements.getSubElementsMap().put(initializeSubElements.getSubElement(),initializeSubElements));

                                });
                    });
        } catch ( Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }

        log.info("#### Loading Initialization Fields .. total number loaded is {}",initializeFixedSubElementsList.size() );


        return initializeFixedSubElementsList;

    }


    @Bean(BeanConfigNames.RESPONSE_CODE_MAP)
    public Map<ServiceResponse, ResponseCodeMapItem> loadResponseCodes(){

        Map<ServiceResponse,ResponseCodeMapItem> responseResponseCodeMapItemMap = new HashMap<>();
        Resource resource = resourceLoader.getResource(ResourceConstants.RESPONSE_CODE_YML);
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = resource.getInputStream()){
            Yaml yaml = new Yaml(new SafeConstructor());
            yaml.loadAll(inputStream)
                    .forEach(o ->{
                       ResponseCodes responseCodes = objectMapper.convertValue(o, ResponseCodes.class);
                        responseCodes.getResponseCodeMapItems()
                                .forEach(responseCodeMapItem -> responseResponseCodeMapItemMap.put(ServiceResponse.valueOf(responseCodeMapItem.getServiceResponse()),responseCodeMapItem));


                    });
        } catch ( Exception ex){
            ex.printStackTrace();
            throw  new RuntimeException(ex.getMessage());
        }

        log.info("#### Loading Response Codes .. total number loaded is {}",responseResponseCodeMapItemMap.size() );


        return responseResponseCodeMapItemMap;

    }


    @Bean(BeanConfigNames.RESPONSE_MAP)
    public Map<ResponseTypes, ResponseSummary> loadConfig( ) {

        Map<ResponseTypes, ResponseSummary> responseFieldsMap = new HashMap<>();
        Resource resource = resourceLoader.getResource(ResourceConstants.AUTH_RESPONSE_YML);
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = resource.getInputStream()){
            Yaml yaml = new Yaml(new SafeConstructor());
            yaml.loadAll(inputStream)
                    .forEach(o ->{
                        ResponseSummary responseSummary = objectMapper.convertValue(o, ResponseSummary.class);
                        responseFieldsMap.put(responseSummary.getResponseTypes(), responseSummary);
                    });
        } catch ( Exception ex){
            ex.printStackTrace();
        }

        log.info("################ {}", responseFieldsMap.size());

        return responseFieldsMap;
    }
}
