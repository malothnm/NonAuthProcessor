package in.nmaloth.NonAuthProcessor.service;

import com.nithin.iso8583.iso.elementdef.common.impl.NoTypeElementImpl;
import com.nithin.iso8583.iso.elementdef.common.interfaces.IntElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.StringElementLengthValue;
import com.nithin.iso8583.iso.message.BasicElementMessage;
import com.nithin.iso8583.iso.message.Header.visa.VisaHeaderComplete;
import com.nithin.iso8583.iso.message.Header.visa.VisaHeaderType;
import com.nithin.iso8583.iso.message.ISOMessageFactory;
import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.model.dto.ExceptionFileDto;
import in.nmaloth.NonAuthProcessor.model.dto.NetworkMessageDto;
import in.nmaloth.entity.network.IPProp;
import in.nmaloth.entity.network.NetworkProperties;
import in.nmaloth.entity.network.SignOnStatus;
import in.nmaloth.payments.constants.network.NetworkAdviceInit;
import in.nmaloth.payments.constants.network.NetworkKeyExchange;
import in.nmaloth.payments.constants.network.NetworkMessageType;
import in.nmaloth.payments.constants.network.NetworkType;
import in.nmaloth.payments.constants.schemeDatabase.ExceptionActionCodes;
import in.nmaloth.payments.constants.schemeDatabase.FileUpdateActions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VisaNonAuthServiceTest {

    @Autowired
    private SchemeNonAuthService schemeNonAuthService;

    @Autowired
    private ISOMessageFactory isoMessageFactory;

    private SetupData setupData = new SetupData();

    @Test
    public void createSignOnMessage(){

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_SMS,
                "435123","123456", new ArrayList<>(),SignOnStatus.SIGN_OFF);

        LocalDateTime localDateTime = LocalDateTime.of(2021,9,11,15,23,22);

        NetworkMessageDto networkMessageDto = NetworkMessageDto.builder()
                .messageId(UUID.randomUUID().toString().replace("-",""))
                .localDateTime(localDateTime)
                .networkAdviceInit(NetworkAdviceInit.NONE)
                .networkMessageType(NetworkMessageType.SIGN_ON)
                .networkKeyExchange(NetworkKeyExchange.NO_KEY_EXCHANGE)
                .networkType(NetworkType.VISA_VIP)
                .traceNumber(123456)
                .build();


        Message message = schemeNonAuthService.convertToMessage(networkMessageDto,networkProperties);
        Integer traceNumber = ((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue();
        String date = ((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue();
        String referenceNumber = ((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue();

        assertAll(

                ()-> assertEquals(800,message.getMessageTypeIdentifier()),
                ()-> assertEquals(0x16,((VisaHeaderComplete)message.getHeader()).getHeader().getHeaderLength()),
                ()-> assertEquals(VisaHeaderType.NORMAL_HEADER,((VisaHeaderComplete)message.getHeader()).getHeader().getVisaHeaderType()),
                ()-> assertEquals(0x01,((VisaHeaderComplete)message.getHeader()).getHeader().getHeaderFormat()),
                ()-> assertEquals(0x1A,((VisaHeaderComplete)message.getHeader()).getHeader().getTextFormat()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getTotalMessageLength()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getDestinationStationId()),
                ()-> assertArrayEquals(new byte[]{0x12,0x34,0x56},((VisaHeaderComplete)message.getHeader()).getHeader().getSourceStationId()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getRoundTripControlInfo()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getBase1Flags()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getMessageStatusFlags()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getBatchNumber()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getReserved()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getUserInfo()),
                ()-> assertTrue(message.getBitMap().get(0)),
                ()-> assertTrue(message.getBitMap().get(6)),
                ()-> assertTrue(message.getBitMap().get(10)),
                ()-> assertTrue(message.getBitMap().get(36)),
                ()-> assertTrue(message.getBitMap().get(69)),
                ()-> assertTrue(0!= traceNumber),
                ()-> assertEquals("0911152322",date),
                ()-> assertEquals("125415",referenceNumber.substring(0,6)),
                ()-> assertEquals(71,((IntElementLengthValue)message.getDataElements().get(70).getElementLengthValue()).getValue())

                );

    }

    @Test
    public void createSignOffMessage(){

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_SMS,
                "435123","123456", new ArrayList<>(),SignOnStatus.SIGN_ON);

        LocalDateTime localDateTime = LocalDateTime.of(2021,9,11,15,23,22);

        NetworkMessageDto networkMessageDto = NetworkMessageDto.builder()
                .messageId(UUID.randomUUID().toString().replace("-",""))
                .localDateTime(localDateTime)
                .networkAdviceInit(NetworkAdviceInit.NONE)
                .networkMessageType(NetworkMessageType.SIGN_OFF)
                .networkKeyExchange(NetworkKeyExchange.NO_KEY_EXCHANGE)
                .traceNumber(123456)
                .networkType(NetworkType.VISA_VIP)
                .build();


        Message message = schemeNonAuthService.convertToMessage(networkMessageDto,networkProperties);
        Integer traceNumber = ((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue();
        String date = ((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue();
        String referenceNumber = ((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue();

        assertAll(

                ()-> assertEquals(800,message.getMessageTypeIdentifier()),
                ()-> assertEquals(0x16,((VisaHeaderComplete)message.getHeader()).getHeader().getHeaderLength()),
                ()-> assertEquals(VisaHeaderType.NORMAL_HEADER,((VisaHeaderComplete)message.getHeader()).getHeader().getVisaHeaderType()),
                ()-> assertEquals(0x01,((VisaHeaderComplete)message.getHeader()).getHeader().getHeaderFormat()),
                ()-> assertEquals(0x1A,((VisaHeaderComplete)message.getHeader()).getHeader().getTextFormat()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getTotalMessageLength()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getDestinationStationId()),
                ()-> assertArrayEquals(new byte[]{0x12,0x34,0x56},((VisaHeaderComplete)message.getHeader()).getHeader().getSourceStationId()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getRoundTripControlInfo()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getBase1Flags()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getMessageStatusFlags()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getBatchNumber()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getReserved()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getUserInfo()),
                ()-> assertTrue(message.getBitMap().get(0)),
                ()-> assertTrue(message.getBitMap().get(6)),
                ()-> assertTrue(message.getBitMap().get(10)),
                ()-> assertTrue(message.getBitMap().get(36)),
                ()-> assertTrue(message.getBitMap().get(69)),
                ()-> assertTrue(0!= traceNumber),
                ()-> assertEquals("0911152322",date),
                ()-> assertEquals("125415",referenceNumber.substring(0,6)),
                ()-> assertEquals(72,((IntElementLengthValue)message.getDataElements().get(70).getElementLengthValue()).getValue())

        );

    }

    @Test
    public void createAdviceStartMessage(){

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_SMS,
                "435123","123456", new ArrayList<>(),SignOnStatus.SIGN_ON);

        LocalDateTime localDateTime = LocalDateTime.of(2021,9,11,15,23,22);

        NetworkMessageDto networkMessageDto = NetworkMessageDto.builder()
                .messageId(UUID.randomUUID().toString().replace("-",""))
                .localDateTime(localDateTime)
                .networkAdviceInit(NetworkAdviceInit.START_ADVICE)
                .networkMessageType(NetworkMessageType.NO_MESSAGE)
                .networkKeyExchange(NetworkKeyExchange.NO_KEY_EXCHANGE)
                .networkType(NetworkType.VISA_VIP)
                .traceNumber(123456)
                .build();


        Message message = schemeNonAuthService.convertToMessage(networkMessageDto,networkProperties);
        Integer traceNumber = ((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue();
        String date = ((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue();
        String referenceNumber = ((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue();

        assertAll(

                ()-> assertEquals(800,message.getMessageTypeIdentifier()),
                ()-> assertEquals(0x16,((VisaHeaderComplete)message.getHeader()).getHeader().getHeaderLength()),
                ()-> assertEquals(VisaHeaderType.NORMAL_HEADER,((VisaHeaderComplete)message.getHeader()).getHeader().getVisaHeaderType()),
                ()-> assertEquals(0x01,((VisaHeaderComplete)message.getHeader()).getHeader().getHeaderFormat()),
                ()-> assertEquals(0x1A,((VisaHeaderComplete)message.getHeader()).getHeader().getTextFormat()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getTotalMessageLength()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getDestinationStationId()),
                ()-> assertArrayEquals(new byte[]{0x12,0x34,0x56},((VisaHeaderComplete)message.getHeader()).getHeader().getSourceStationId()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getRoundTripControlInfo()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getBase1Flags()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getMessageStatusFlags()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getBatchNumber()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getReserved()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getUserInfo()),
                ()-> assertTrue(message.getBitMap().get(0)),
                ()-> assertTrue(message.getBitMap().get(6)),
                ()-> assertTrue(message.getBitMap().get(10)),
                ()-> assertTrue(message.getBitMap().get(36)),
                ()-> assertTrue(message.getBitMap().get(69)),
                ()-> assertTrue(0!= traceNumber),
                ()-> assertEquals("0911152322",date),
                ()-> assertEquals("125415",referenceNumber.substring(0,6)),
                ()-> assertEquals(78,((IntElementLengthValue)message.getDataElements().get(70).getElementLengthValue()).getValue())

        );

    }


    @Test
    public void createAdviceStopMessage(){

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_SMS,
                "435123","123456", new ArrayList<>(),SignOnStatus.SIGN_ON);

        LocalDateTime localDateTime = LocalDateTime.of(2021,9,11,15,23,22);

        NetworkMessageDto networkMessageDto = NetworkMessageDto.builder()
                .messageId(UUID.randomUUID().toString().replace("-",""))
                .localDateTime(localDateTime)
                .networkAdviceInit(NetworkAdviceInit.STOP_ADVICE)
                .networkMessageType(NetworkMessageType.NO_MESSAGE)
                .networkKeyExchange(NetworkKeyExchange.NO_KEY_EXCHANGE)
                .networkType(NetworkType.VISA_VIP)
                .traceNumber(123456)
                .build();


        Message message = schemeNonAuthService.convertToMessage(networkMessageDto,networkProperties);
        Integer traceNumber = ((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue();
        String date = ((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue();
        String referenceNumber = ((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue();

        assertAll(

                ()-> assertEquals(800,message.getMessageTypeIdentifier()),
                ()-> assertEquals(0x16,((VisaHeaderComplete)message.getHeader()).getHeader().getHeaderLength()),
                ()-> assertEquals(VisaHeaderType.NORMAL_HEADER,((VisaHeaderComplete)message.getHeader()).getHeader().getVisaHeaderType()),
                ()-> assertEquals(0x01,((VisaHeaderComplete)message.getHeader()).getHeader().getHeaderFormat()),
                ()-> assertEquals(0x1A,((VisaHeaderComplete)message.getHeader()).getHeader().getTextFormat()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getTotalMessageLength()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getDestinationStationId()),
                ()-> assertArrayEquals(new byte[]{0x12,0x34,0x56},((VisaHeaderComplete)message.getHeader()).getHeader().getSourceStationId()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getRoundTripControlInfo()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getBase1Flags()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getMessageStatusFlags()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getBatchNumber()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getReserved()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getUserInfo()),
                ()-> assertTrue(message.getBitMap().get(0)),
                ()-> assertTrue(message.getBitMap().get(6)),
                ()-> assertTrue(message.getBitMap().get(10)),
                ()-> assertTrue(message.getBitMap().get(36)),
                ()-> assertTrue(message.getBitMap().get(69)),
                ()-> assertTrue(0!= traceNumber),
                ()-> assertEquals("0911152322",date),
                ()-> assertEquals("125415",referenceNumber.substring(0,6)),
                ()-> assertEquals(79,((IntElementLengthValue)message.getDataElements().get(70).getElementLengthValue()).getValue())

        );

    }

    @Test
    public void createEchoMessage(){

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_SMS,
                "435123","123456", new ArrayList<>(),SignOnStatus.SIGN_ON);

        LocalDateTime localDateTime = LocalDateTime.of(2021,9,11,15,23,22);

        NetworkMessageDto networkMessageDto = NetworkMessageDto.builder()
                .messageId(UUID.randomUUID().toString().replace("-",""))
                .localDateTime(localDateTime)
                .networkAdviceInit(NetworkAdviceInit.NONE)
                .networkMessageType(NetworkMessageType.ECHO)
                .networkKeyExchange(NetworkKeyExchange.NO_KEY_EXCHANGE)
                .networkType(NetworkType.VISA_VIP)
                .traceNumber(123456)
                .build();


        Message message = schemeNonAuthService.convertToMessage(networkMessageDto,networkProperties);
        Integer traceNumber = ((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue();
        String date = ((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue();
        String referenceNumber = ((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue();

        assertAll(

                ()-> assertEquals(800,message.getMessageTypeIdentifier()),
                ()-> assertEquals(0x16,((VisaHeaderComplete)message.getHeader()).getHeader().getHeaderLength()),
                ()-> assertEquals(VisaHeaderType.NORMAL_HEADER,((VisaHeaderComplete)message.getHeader()).getHeader().getVisaHeaderType()),
                ()-> assertEquals(0x01,((VisaHeaderComplete)message.getHeader()).getHeader().getHeaderFormat()),
                ()-> assertEquals(0x1A,((VisaHeaderComplete)message.getHeader()).getHeader().getTextFormat()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getTotalMessageLength()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getDestinationStationId()),
                ()-> assertArrayEquals(new byte[]{0x12,0x34,0x56},((VisaHeaderComplete)message.getHeader()).getHeader().getSourceStationId()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getRoundTripControlInfo()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getBase1Flags()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getMessageStatusFlags()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getBatchNumber()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00,0x00},((VisaHeaderComplete)message.getHeader()).getHeader().getReserved()),
                ()-> assertEquals(0x00,((VisaHeaderComplete)message.getHeader()).getHeader().getUserInfo()),
                ()-> assertTrue(message.getBitMap().get(0)),
                ()-> assertTrue(message.getBitMap().get(6)),
                ()-> assertTrue(message.getBitMap().get(10)),
                ()-> assertTrue(message.getBitMap().get(36)),
                ()-> assertTrue(message.getBitMap().get(69)),
                ()-> assertTrue(0!= traceNumber),
                ()-> assertEquals("0911152322",date),
                ()-> assertEquals("125415",referenceNumber.substring(0,6)),
                ()-> assertEquals(301,((IntElementLengthValue)message.getDataElements().get(70).getElementLengthValue()).getValue())

        );

    }



    @Test
    void identifyNetworkMessageTypeEcho() throws Exception {

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.ECHO,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        NetworkMessageType networkMessageType = schemeNonAuthService.identifyNetworkMessageType(message);
        assertEquals(NetworkMessageType.ECHO,networkMessageType);
    }

    @Test
    void identifyNetworkMessageTypeGroupSignOn() throws Exception {

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.GROUP_SIGN_ON,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        NetworkMessageType networkMessageType = schemeNonAuthService.identifyNetworkMessageType(message);
        assertEquals(NetworkMessageType.GROUP_SIGN_ON,networkMessageType);
    }

    @Test
    void identifyNetworkMessageTypeGroupSignoff() throws Exception {

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.GROUP_SIGN_OFF,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        NetworkMessageType networkMessageType = schemeNonAuthService.identifyNetworkMessageType(message);
        assertEquals(NetworkMessageType.GROUP_SIGN_OFF,networkMessageType);
    }

    @Test
    void identifyNetworkMessageTypeSignOn() throws Exception {

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.SIGN_ON,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        NetworkMessageType networkMessageType = schemeNonAuthService.identifyNetworkMessageType(message);
        assertEquals(NetworkMessageType.NO_MESSAGE,networkMessageType);
    }

    @Test
    void identifyNetworkMessageTypeSignoff() throws Exception {

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.SIGN_OFF,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        NetworkMessageType networkMessageType = schemeNonAuthService.identifyNetworkMessageType(message);
        assertEquals(NetworkMessageType.NO_MESSAGE,networkMessageType);
    }

    @Test
    void identifyNetworkMessageNetworkAdvicesStart() throws Exception {

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.NO_MESSAGE,NetworkAdviceInit.START_ADVICE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        NetworkAdviceInit networkAdviceInit = schemeNonAuthService.identifyAdviceInit(message);
        assertEquals(NetworkAdviceInit.START_ADVICE,networkAdviceInit);
    }

    @Test
    void identifyNetworkMessageNetworkAdvicesStop() throws Exception {

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.NO_MESSAGE,NetworkAdviceInit.STOP_ADVICE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        NetworkAdviceInit networkAdviceInit = schemeNonAuthService.identifyAdviceInit(message);
        assertEquals(NetworkAdviceInit.STOP_ADVICE,networkAdviceInit);
    }

    @Test
    void identifyNetworkMessageNetworkAdvicesNone() throws Exception {

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.SIGN_ON,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        NetworkAdviceInit networkAdviceInit = schemeNonAuthService.identifyAdviceInit(message);
        assertEquals(NetworkAdviceInit.NONE,networkAdviceInit);
    }

    @Test
    void identifyNetworkMessageNetworkAdvicesKeyExchange() throws Exception {

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.NO_MESSAGE,NetworkAdviceInit.NONE,NetworkKeyExchange.KEY_CHANGE_REQUEST);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        NetworkKeyExchange networkKeyExchange = schemeNonAuthService.identifyKeyExchangeMessages(message);
        assertEquals(NetworkKeyExchange.NO_KEY_EXCHANGE,networkKeyExchange);
    }

    @Test
    void createExceptionFileUpdateMessage() throws Exception {

        ExceptionFileDto exceptionFileDto = ExceptionFileDto.builder()
                .localDateTime(LocalDateTime.of(2021,8,22,13,24,11))
                .traceNumber(123456)
                .region("A")
                .fileUpdateActions(FileUpdateActions.ADD)
                .exceptionActionCodes(ExceptionActionCodes.CLOSED_ACCOUNT)
                .instrument("1234567890123456")
                .build();

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_SMS,
                "435123","123456", new ArrayList<>(),SignOnStatus.SIGN_ON);

        Message message = schemeNonAuthService.createExceptionFileUpdateMessage(exceptionFileDto,networkProperties);

        assertAll(
                ()-> assertEquals(302,message.getMessageTypeIdentifier()),
                ()-> assertTrue(message.getBitMap().get(1)),
                ()-> assertTrue(message.getBitMap().get(6)),
                ()-> assertTrue(message.getBitMap().get(10)),
                ()-> assertTrue(message.getBitMap().get(36)),
                ()-> assertTrue(message.getBitMap().get(72)),
                ()-> assertTrue(message.getBitMap().get(90)),
                ()-> assertTrue(message.getBitMap().get(100)),
                ()-> assertTrue(message.getBitMap().get(126)),
                ()-> assertEquals("1234567890123456",((StringElementLengthValue)message.getDataElements().get(2).getElementLengthValue()).getValue()),
                ()-> assertEquals("0822132411",((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue()),
                ()-> assertEquals(123456,((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue()),
                ()-> assertEquals("123413123456",((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue()),
                ()-> assertEquals("210822",((StringElementLengthValue)message.getDataElements().get(73).getElementLengthValue()).getValue()),
                ()-> assertEquals("1",((StringElementLengthValue)message.getDataElements().get(91).getElementLengthValue()).getValue()),
                ()-> assertEquals("E2",((StringElementLengthValue)message.getDataElements().get(101).getElementLengthValue()).getValue()),
                ()-> assertArrayEquals("14A        ".getBytes(StandardCharsets.UTF_8),((NoTypeElementImpl)message.getDataElements().get(127).getElementLengthValue()).getValue())


                );

    }


    private NetworkProperties createNetworkProperties(NetworkType networkType, String ica,
                                                      String stationId,
                                                      List<IPProp> ipPropList, SignOnStatus signOnStatus) {

        return NetworkProperties.builder()
                .networkType(networkType)
                .ica(ica)
                .stationId(stationId)
                .signOnStatus(signOnStatus)
                .ipProps(ipPropList)
                .build();
    }
}