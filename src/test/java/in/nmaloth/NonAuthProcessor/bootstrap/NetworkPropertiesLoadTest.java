package in.nmaloth.NonAuthProcessor.bootstrap;

import in.nmaloth.NonAuthProcessor.repositories.NetworkPropertyRepository;
import in.nmaloth.entity.network.IPProp;
import in.nmaloth.entity.network.NetworkProperties;
import in.nmaloth.entity.network.SignOnStatus;
import in.nmaloth.payments.constants.network.NetworkType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NetworkPropertiesLoadTest {

    @Autowired
    private Map<NetworkType,NetworkProperties> networkPropertiesMap;

    @Autowired
    private NetworkPropertyRepository networkPropertyRepository;

    @Test
    void loadNetwork() throws InterruptedException {

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_SMS,
                "435123","123456", new ArrayList<>(),SignOnStatus.SIGN_OFF);
        networkPropertyRepository.save(networkProperties);

        Thread.sleep(500);

        NetworkProperties networkProperties1 = networkPropertiesMap.get(NetworkType.VISA_SMS);
        assertAll(

                ()-> assertNotNull(networkProperties1),
                ()-> assertEquals(NetworkType.VISA_SMS,networkProperties1.getNetworkType()),
                ()-> assertEquals("435123",networkProperties1.getIca()),
                ()-> assertEquals("123456",networkProperties1.getStationId()),
                ()-> assertEquals(SignOnStatus.SIGN_OFF,networkProperties1.getSignOnStatus())
        );

    }

    @Test
    void loadNetwork1() throws InterruptedException {

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_SMS,
                "435123","123456", new ArrayList<>(),SignOnStatus.SIGN_ON);
        networkPropertyRepository.save(networkProperties);

        Thread.sleep(500);

        NetworkProperties networkProperties1 = networkPropertiesMap.get(NetworkType.VISA_SMS);
        assertAll(

                ()-> assertNotNull(networkProperties1),
                ()-> assertEquals(NetworkType.VISA_SMS,networkProperties1.getNetworkType()),
                ()-> assertEquals("435123",networkProperties1.getIca()),
                ()-> assertEquals("123456",networkProperties1.getStationId()),
                ()-> assertEquals(SignOnStatus.SIGN_ON,networkProperties1.getSignOnStatus())
        );

    }

    @Test
    void networkPropertiesChange() throws InterruptedException {

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_SMS,
                "435123","123456", new ArrayList<>(),SignOnStatus.SIGN_ON);
        networkPropertyRepository.save(networkProperties);

        Thread.sleep(500);
        networkPropertyRepository.deleteById(networkProperties.getNetworkType());
        Thread.sleep(500);
        NetworkProperties networkProperties1 = networkPropertiesMap.get(networkProperties.getNetworkType());

        assertNull(networkProperties1);
    }

    private NetworkProperties createNetworkProperties(NetworkType networkType, String ica,
                                                      String stationId,
                                                      List<IPProp> ipPropList,SignOnStatus signOnStatus){

        return NetworkProperties.builder()
                .networkType(networkType)
                .ica(ica)
                .stationId(stationId)
                .signOnStatus(signOnStatus)
                .ipProps(ipPropList)
                .build();
    }
}