package in.nmaloth.NonAuthProcessor.service;

import com.google.protobuf.ByteString;
import com.nithin.iso8583.iso.elementdef.common.interfaces.IntElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.LongElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.StringElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.SubElementLengthValue;
import com.nithin.iso8583.iso.elementdef.subelement.bitmap.SubElementBitmapMap;
import com.nithin.iso8583.iso.elementdef.util.Utils;
import com.nithin.iso8583.iso.message.BasicElementMessage;
import com.nithin.iso8583.iso.message.ISOMessageFactory;
import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.model.NonAuthMessage;
import in.nmaloth.NonAuthProcessor.model.NonAuthOutgoingMessage;
import in.nmaloth.NonAuthProcessor.model.dto.NetworkMessageDto;
import in.nmaloth.NonAuthProcessor.model.proto.OutgoingMessageOuterClass;
import in.nmaloth.NonAuthProcessor.repositories.AuthSnapShotRepository;
import in.nmaloth.NonAuthProcessor.repositories.NetworkMessageRepository;
import in.nmaloth.NonAuthProcessor.repositories.NetworkPropertyRepository;
import in.nmaloth.entity.logs.AuthSnapShot;
import in.nmaloth.entity.logs.ReversalStatus;
import in.nmaloth.entity.logs.SnapshotKey;
import in.nmaloth.entity.network.IPProp;
import in.nmaloth.entity.network.NetworkMessages;
import in.nmaloth.entity.network.NetworkProperties;
import in.nmaloth.entity.network.SignOnStatus;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.network.NetworkAdviceInit;
import in.nmaloth.payments.constants.network.NetworkKeyExchange;
import in.nmaloth.payments.constants.network.NetworkMessageType;
import in.nmaloth.payments.constants.network.NetworkType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.context.Theme;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NonAuthMessageServiceImplTest {

    @Autowired
    private NonAuthMessageService nonAuthMessageService;

    @Autowired
    private Map<NetworkType, NetworkProperties> networkPropertiesMap;

    @Autowired
    private NetworkMessageRepository networkMessageRepository;

    @Autowired
    private NetworkPropertyRepository networkPropertyRepository;

    @Autowired
    private ISOMessageFactory isoMessageFactory;

    @Autowired
    private AuthSnapShotRepository authSnapShotRepository;



    private SetupData setupData = new SetupData();


    @BeforeEach
    void setUp() throws InterruptedException {

        NetworkProperties networkPropertiesVisa = networkPropertiesMap.get(NetworkType.VISA_VIP);
        if(networkPropertiesVisa == null){
            NetworkProperties networkProperties1 = createNetworkProperties(NetworkType.VISA_VIP,
                    "435123","123456", new ArrayList<>(), SignOnStatus.SIGN_OFF);
            networkPropertiesMap.put(networkProperties1.getNetworkType(),networkProperties1);
        }

        NetworkProperties networkPropertiesVisaSms = networkPropertiesMap.get(NetworkType.VISA_SMS);
        if(networkPropertiesVisaSms == null){
            NetworkProperties networkProperties1 = createNetworkProperties(NetworkType.VISA_SMS,
                    "435124","123457", new ArrayList<>(), SignOnStatus.SIGN_ON);
            networkPropertiesMap.put(networkProperties1.getNetworkType(),networkProperties1);
        }

        NetworkProperties networkPropertiesMC = networkPropertiesMap.get(NetworkType.MASTERCARD);
        if(networkPropertiesMC == null){
            NetworkProperties networkProperties1 = createNetworkProperties(NetworkType.MASTERCARD,
                    "435125","123458", new ArrayList<>(), SignOnStatus.SIGN_OFF);
            networkPropertiesMap.put(networkProperties1.getNetworkType(),networkProperties1);
        }

        if(networkPropertiesMC == null || networkPropertiesVisaSms == null || networkPropertiesVisa == null){
            Thread.sleep(500);
        }
    }

    @Test
    void createAuthMessageSignOn() {

        byte[] bytes = setupData.createNetworkMessages(NetworkMessageType.GROUP_SIGN_ON, NetworkAdviceInit.NONE, NetworkKeyExchange.NO_KEY_EXCHANGE);
        OutgoingMessageOuterClass.OutgoingMessage outgoingMessage = OutgoingMessageOuterClass.OutgoingMessage.newBuilder()
                .setMessageId(UUID.randomUUID().toString())
                .setMessageTypeId("0800")
                .setContainerId(UUID.randomUUID().toString())
                .setChannelId(UUID.randomUUID().toString())
                .setMessage(ByteString.copyFrom(bytes))
                .build();

        NonAuthMessage nonAuthMessage = nonAuthMessageService.createNonAuthMessage(outgoingMessage);
        assertAll(
                ()-> assertEquals(nonAuthMessage.getMessageId(),outgoingMessage.getMessageId()),
                ()-> assertEquals(nonAuthMessage.getMessageTypeId(),outgoingMessage.getMessageTypeId()),
                ()-> assertEquals(nonAuthMessage.getContainerId(),outgoingMessage.getContainerId()),
                ()-> assertTrue(nonAuthMessage.getMessage().getBitMap().get(0)),
                ()-> assertTrue(nonAuthMessage.getMessage().getBitMap().get(6)),
                ()-> assertTrue(nonAuthMessage.getMessage().getBitMap().get(10)),
                ()-> assertTrue(nonAuthMessage.getMessage().getBitMap().get(36)),
                ()-> assertTrue(nonAuthMessage.getMessage().getBitMap().get(69)),
                ()-> assertEquals("0311185911",((StringElementLengthValue) nonAuthMessage.getMessage().getDataElements().get(7).getElementLengthValue()).getValue()),
                ()-> assertEquals(31118,((IntElementLengthValue) nonAuthMessage.getMessage().getDataElements().get(11).getElementLengthValue()).getValue()),
                ()-> assertEquals("123456789012",((StringElementLengthValue) nonAuthMessage.getMessage().getDataElements().get(37).getElementLengthValue()).getValue()),
                ()-> assertEquals(71,((IntElementLengthValue) nonAuthMessage.getMessage().getDataElements().get(70).getElementLengthValue()).getValue())

        );

    }

    @Test
    void createAuthMessageSignOff() {

        byte[] bytes = setupData.createNetworkMessages(NetworkMessageType.GROUP_SIGN_OFF, NetworkAdviceInit.NONE, NetworkKeyExchange.NO_KEY_EXCHANGE);
        OutgoingMessageOuterClass.OutgoingMessage outgoingMessage = OutgoingMessageOuterClass.OutgoingMessage.newBuilder()
                .setMessageId(UUID.randomUUID().toString())
                .setMessageTypeId("0800")
                .setContainerId(UUID.randomUUID().toString())
                .setChannelId(UUID.randomUUID().toString())
                .setMessage(ByteString.copyFrom(bytes))
                .build();

        NonAuthMessage nonAuthMessage = nonAuthMessageService.createNonAuthMessage(outgoingMessage);
        assertAll(
                ()-> assertEquals(nonAuthMessage.getMessageId(),outgoingMessage.getMessageId()),
                ()-> assertEquals(nonAuthMessage.getMessageTypeId(),outgoingMessage.getMessageTypeId()),
                ()-> assertEquals(nonAuthMessage.getContainerId(),outgoingMessage.getContainerId()),
                ()-> assertTrue(nonAuthMessage.getMessage().getBitMap().get(0)),
                ()-> assertTrue(nonAuthMessage.getMessage().getBitMap().get(6)),
                ()-> assertTrue(nonAuthMessage.getMessage().getBitMap().get(10)),
                ()-> assertTrue(nonAuthMessage.getMessage().getBitMap().get(36)),
                ()-> assertTrue(nonAuthMessage.getMessage().getBitMap().get(69)),
                ()-> assertEquals("0311185911",((StringElementLengthValue) nonAuthMessage.getMessage().getDataElements().get(7).getElementLengthValue()).getValue()),
                ()-> assertEquals(31118,((IntElementLengthValue) nonAuthMessage.getMessage().getDataElements().get(11).getElementLengthValue()).getValue()),
                ()-> assertEquals("123456789012",((StringElementLengthValue) nonAuthMessage.getMessage().getDataElements().get(37).getElementLengthValue()).getValue()),
                ()-> assertEquals(72,((IntElementLengthValue) nonAuthMessage.getMessage().getDataElements().get(70).getElementLengthValue()).getValue())

        );

    }

    @Test
    public void createSignOnMessage(){

        NetworkProperties networkProperties = networkPropertiesMap.get(NetworkType.VISA_VIP);
        networkProperties.setSignOnStatus(SignOnStatus.SIGN_OFF);

        NetworkMessageDto networkMessageDto = NetworkMessageDto.builder()
                .messageId(UUID.randomUUID().toString().replace("-",""))
                .localDateTime(LocalDateTime.of(2021,10,20,15,12,11,200))
                .networkAdviceInit(NetworkAdviceInit.NONE)
                .networkMessageType(NetworkMessageType.SIGN_ON)
                .networkKeyExchange(NetworkKeyExchange.NO_KEY_EXCHANGE)
                .networkType(NetworkType.VISA_VIP)
                .traceNumber(123456)
                .build();

        Mono<NonAuthOutgoingMessage> messageMono = nonAuthMessageService.createNetworkMessages(networkMessageDto);

        StepVerifier.create(messageMono)
                .consumeNextWith(nonAuthOutgoingMessage -> {
                    Message message = nonAuthOutgoingMessage.getMessage();
                    Integer trace = ((IntElementLengthValue) message.getDataElements().get(11).getElementLengthValue()).getValue();

                    String id = new StringBuilder()
                            .append("SIGN_ON")
                            .append(trace.toString())
                            .append("1020151211")
                            .toString();

                    Optional<NetworkMessages> networkMessagesOptional = networkMessageRepository.findById(id);

                    assertAll(
                            ()-> assertTrue(networkMessagesOptional.isPresent())

                    );
                })
                .verifyComplete()
                ;
    }

    @Test
    public void createEchoMessage(){

        NetworkProperties networkProperties = networkPropertiesMap.get(NetworkType.MASTERCARD);
        networkProperties.setSignOnStatus(SignOnStatus.SIGN_OFF);

        NetworkMessageDto networkMessageDto = NetworkMessageDto.builder()
                .messageId(UUID.randomUUID().toString().replace("-",""))
                .localDateTime(LocalDateTime.of(2021,10,20,15,12,11,200))
                .networkAdviceInit(NetworkAdviceInit.NONE)
                .networkMessageType(NetworkMessageType.ECHO)
                .networkKeyExchange(NetworkKeyExchange.NO_KEY_EXCHANGE)
                .networkType(NetworkType.MASTERCARD)
                .traceNumber(123456)
                .build();

        Mono<NonAuthOutgoingMessage> nonAuthOutgoingMessageMono = nonAuthMessageService.createNetworkMessages(networkMessageDto);

        StepVerifier.create(nonAuthOutgoingMessageMono)
                .expectError()
                .verify()
        ;
    }

    @Test
    public void createSignOnErrorMessage(){

        NetworkMessageDto networkMessageDto = NetworkMessageDto.builder()
                .messageId(UUID.randomUUID().toString().replace("-",""))
                .localDateTime(LocalDateTime.of(2021,10,20,15,12,11,200))
                .networkAdviceInit(NetworkAdviceInit.NONE)
                .networkMessageType(NetworkMessageType.SIGN_ON)
                .networkKeyExchange(NetworkKeyExchange.NO_KEY_EXCHANGE)
                .networkType(NetworkType.AMEX_ATM)
                .build();

        Mono<NonAuthOutgoingMessage> nonAuthOutgoingMessageMono = nonAuthMessageService.createNetworkMessages(networkMessageDto);

        StepVerifier.create(nonAuthOutgoingMessageMono)
                .expectError()
                .verify()
        ;
    }

    @Test
    void testNetworkRequestSignOn() throws Exception {

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_VIP,
                "435123","123456", new ArrayList<>(), SignOnStatus.SIGN_OFF);
        networkPropertyRepository.save(networkProperties);
        Thread.sleep(500);

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.GROUP_SIGN_ON,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        Mono<Message> messageMono = nonAuthMessageService.processNetworkMessageRequests(message);
        StepVerifier.create(messageMono)
                .consumeNextWith(message1 -> {

                    try {
                        Thread.sleep(200);
                        NetworkProperties networkProperties1 = networkPropertiesMap.get(NetworkType.VISA_VIP);


                        assertAll(
                                ()-> assertEquals(SignOnStatus.SIGN_ON,networkProperties1.getSignOnStatus()),
                                ()-> assertEquals(810,message1.getMessageTypeIdentifier()),
                                ()-> assertEquals("00",((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue())
                        );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                });

    }

    @Test
    void testNetworkResponseSignOn() throws Exception {

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_VIP,
                "435123","123456", new ArrayList<>(), SignOnStatus.SIGN_OFF);
        networkPropertyRepository.save(networkProperties);
        Thread.sleep(500);

        byte[] messageBytes = setupData.createNetworkMessagesResponse(NetworkMessageType.GROUP_SIGN_ON,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE,"00");
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        Mono<Void> voidMono = nonAuthMessageService.processNetworkResponse(message);
        StepVerifier.create(voidMono)
                .consumeNextWith(unused ->  {

                    try {
                        Thread.sleep(200);
                        NetworkProperties networkProperties1 = networkPropertiesMap.get(NetworkType.VISA_VIP);


                        assertAll(
                                ()-> assertEquals(SignOnStatus.SIGN_ON,networkProperties1.getSignOnStatus())
                        );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                });

    }


    @Test
    void testNetworkResponseSignOnReject() throws Exception {

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_VIP,
                "435123","123456", new ArrayList<>(), SignOnStatus.SIGN_OFF);
        networkPropertyRepository.save(networkProperties);
        Thread.sleep(500);

        byte[] messageBytes = setupData.createNetworkMessagesResponse(NetworkMessageType.GROUP_SIGN_ON,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE,"05");
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        Mono<Void> voidMono = nonAuthMessageService.processNetworkResponse(message);
        StepVerifier.create(voidMono)
                .consumeNextWith(unused ->  {

                    try {
                        Thread.sleep(200);
                        NetworkProperties networkProperties1 = networkPropertiesMap.get(NetworkType.VISA_VIP);


                        assertAll(
                                ()-> assertEquals(SignOnStatus.SIGN_OFF,networkProperties1.getSignOnStatus())
                        );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                });

    }

    @Test
    void testNetworkResponseSignOff() throws Exception {

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_VIP,
                "435123","123456", new ArrayList<>(), SignOnStatus.SIGN_ON);
        networkPropertyRepository.save(networkProperties);
        Thread.sleep(500);

        byte[] messageBytes = setupData.createNetworkMessagesResponse(NetworkMessageType.GROUP_SIGN_OFF,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE,"00");
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        Mono<Void> voidMono = nonAuthMessageService.processNetworkResponse(message);
        StepVerifier.create(voidMono)
                .consumeNextWith(unused ->  {

                    try {
                        Thread.sleep(200);
                        NetworkProperties networkProperties1 = networkPropertiesMap.get(NetworkType.VISA_VIP);


                        assertAll(
                                ()-> assertEquals(SignOnStatus.SIGN_OFF,networkProperties1.getSignOnStatus())
                        );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                });

    }

    @Test
    void testNetworkRequestSignOnReject() throws Exception {

        networkPropertyRepository.deleteById(NetworkType.VISA_VIP);
        Thread.sleep(500);

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.GROUP_SIGN_ON,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        Mono<Message> messageMono = nonAuthMessageService.processNetworkMessageRequests(message);
        StepVerifier.create(messageMono)
                .consumeNextWith(message1 -> {

                    try {
                        Thread.sleep(200);


                        assertAll(
                                ()-> assertEquals(810,message1.getMessageTypeIdentifier()),
                                ()-> assertEquals("05",((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue())
                        );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                });

    }


    @Test
    void testNetworkRequestSignOff() throws Exception {

        NetworkProperties networkProperties = createNetworkProperties(NetworkType.VISA_VIP,
                "435123","123456", new ArrayList<>(), SignOnStatus.SIGN_ON);
        networkPropertyRepository.save(networkProperties);
        Thread.sleep(500);

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.GROUP_SIGN_OFF,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        Mono<Message> messageMono = nonAuthMessageService.processNetworkMessageRequests(message);
        StepVerifier.create(messageMono)
                .consumeNextWith(message1 -> {

                    try {
                        Thread.sleep(200);
                        NetworkProperties networkProperties1 = networkPropertiesMap.get(NetworkType.VISA_VIP);


                        assertAll(
                                ()-> assertEquals(SignOnStatus.SIGN_OFF,networkProperties1.getSignOnStatus()),
                                ()-> assertEquals(810,message1.getMessageTypeIdentifier()),
                                ()-> assertEquals("00",((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue())
                        );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                });

    }

    @Test
    void testNetworkRequestEcho() throws Exception {


        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.ECHO,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        Mono<Message> messageMono = nonAuthMessageService.processNetworkMessageRequests(message);
        StepVerifier.create(messageMono)
                .consumeNextWith(message1 -> {

                    try {
                        Thread.sleep(200);


                        assertAll(
                                ()-> assertEquals(810,message1.getMessageTypeIdentifier()),
                                ()-> assertEquals("00",((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue())
                        );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                });

    }

    @Test
    void testNetworkResponseEcho() throws Exception {


        byte[] messageBytes = setupData.createNetworkMessagesResponse(NetworkMessageType.ECHO,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE,"00");
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        Mono<Void> messageMono = nonAuthMessageService.processNetworkResponse(message);
        StepVerifier.create(messageMono)
                .expectComplete();

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

    @Test
    void processReversals() throws Exception {
        String instrument = "1234567890123456";
        Integer traceNumber = Utils.hextoInteger(setupData.de011);
        String de07 = Utils.hexToString(setupData.getDE007());
        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",0,1000,"484","484", ReversalStatus.NOT_REVERSED);
        authSnapShotRepository.save(authSnapShot);

        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        NonAuthMessage nonAuthMessage = NonAuthMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageTypeId("400")
                .message(message)
                .channelId(UUID.randomUUID().toString())
                .containerId(UUID.randomUUID().toString())
                .build();

        Mono<NonAuthOutgoingMessage> nonAuthOutgoingMessageMono = nonAuthMessageService.processReversals(nonAuthMessage);

        StepVerifier.create(nonAuthOutgoingMessageMono)
                .consumeNextWith(nonAuthOutgoingMessage -> {

                    assertAll(
                            ()-> assertEquals(nonAuthMessage.getMessageId(),nonAuthOutgoingMessage.getMessageId()),
                            ()-> assertEquals(nonAuthMessage.getMessageTypeId(),nonAuthOutgoingMessage.getMessageTypeId()),
                            ()-> assertEquals(nonAuthMessage.getChannelId(),nonAuthOutgoingMessage.getChannelId()),
                            ()-> assertEquals(nonAuthMessage.getContainerId(),nonAuthOutgoingMessage.getContainerId()),
                            ()-> assertEquals(410,nonAuthOutgoingMessage.getMessage().getMessageTypeIdentifier()),
                            ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(2).getElementLengthValue()).getValue(),
                                    ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(2).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S001").getElementLengthValue()).getValue(),
                                    ((IntElementLengthValue)((SubElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S001").getElementLengthValue()).getValue()),
                            ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S002").getElementLengthValue()).getValue(),
                                    ((IntElementLengthValue)((SubElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S002").getElementLengthValue()).getValue()),
                            ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S003").getElementLengthValue()).getValue(),
                                    ((IntElementLengthValue)((SubElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S003").getElementLengthValue()).getValue()),
                            ()-> assertEquals(((LongElementLengthValue)message.getDataElements().get(4).getElementLengthValue()).getValue(),
                                    ((LongElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(4).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue(),
                                    ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(7).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue(),
                                    ((IntElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(11).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(19).getElementLengthValue()).getValue(),
                                    ((IntElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(19).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(23).getElementLengthValue()).getValue(),
                                    ((IntElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(23).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(25).getElementLengthValue()).getValue(),
                                    ((IntElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(25).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(32).getElementLengthValue()).getValue(),
                                    ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(32).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue(),
                                    ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(37).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(41).getElementLengthValue()).getValue(),
                                    ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(41).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(42).getElementLengthValue()).getValue(),
                                    ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(42).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(49).getElementLengthValue()).getValue(),
                                    ((IntElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(49).getElementLengthValue()).getValue()),
                            ()-> assertEquals(((StringElementLengthValue)((SubElementLengthValue)message.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S002").getElementLengthValue()).getValue(),
                                    ((StringElementLengthValue)((SubElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S002").getElementLengthValue()).getValue()),
                            ()-> assertNull(((SubElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S025")),
                            ()-> assertTrue(((SubElementBitmapMap)((SubElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(1)),
                            ()-> assertFalse(((SubElementBitmapMap)((SubElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(0)),
                            ()-> assertFalse(((SubElementBitmapMap)((SubElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(24)),
                            ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(0)),
                            ()-> assertEquals(((StringElementLengthValue)((SubElementLengthValue)message.getDataElements().get(126).getElementLengthValue()).getValue().getSubElementMap().get("DE126S012").getElementLengthValue()).getValue(),
                                    ((StringElementLengthValue)((SubElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(126).getElementLengthValue()).getValue().getSubElementMap().get("DE126S012").getElementLengthValue()).getValue()),
                            ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(38)),
                            ()-> assertEquals("00", ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(39).getElementLengthValue()).getValue())

                    );


                });

    }

    @Test
    void processAdvices() throws Exception {

        Integer[] integers = new Integer[]{2,3,4,6,7,11,14,18,19,22,23,25,32,33,37,39,38,41,42,43,49,51,55,59,60,62,63};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",120);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);

        NonAuthMessage nonAuthMessage = NonAuthMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageTypeId("120")
                .message(message)
                .channelId(UUID.randomUUID().toString())
                .containerId(UUID.randomUUID().toString())
                .build();

        Mono<NonAuthOutgoingMessage> nonAuthOutgoingMessageMono = nonAuthMessageService.processAdvices(nonAuthMessage);

        StepVerifier.create(nonAuthOutgoingMessageMono)
                .consumeNextWith(nonAuthOutgoingMessage -> {
                    Message message1 = nonAuthOutgoingMessage.getMessage();
                            assertAll(
                                    () -> assertNotNull(message1),
                                    () -> assertEquals(130, message1.getMessageTypeIdentifier()),
                                    () -> assertEquals(((StringElementLengthValue) message.getDataElements().get(2).getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) message1.getDataElements().get(2).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((IntElementLengthValue) ((SubElementLengthValue) message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S001").getElementLengthValue()).getValue(),
                                            ((IntElementLengthValue) ((SubElementLengthValue) message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S001").getElementLengthValue()).getValue()),
                                    () -> assertEquals(((IntElementLengthValue) ((SubElementLengthValue) message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S002").getElementLengthValue()).getValue(),
                                            ((IntElementLengthValue) ((SubElementLengthValue) message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S002").getElementLengthValue()).getValue()),
                                    () -> assertEquals(((IntElementLengthValue) ((SubElementLengthValue) message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S003").getElementLengthValue()).getValue(),
                                            ((IntElementLengthValue) ((SubElementLengthValue) message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S003").getElementLengthValue()).getValue()),
                                    () -> assertEquals(((LongElementLengthValue) message.getDataElements().get(4).getElementLengthValue()).getValue(),
                                            ((LongElementLengthValue) message1.getDataElements().get(4).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((StringElementLengthValue) message.getDataElements().get(7).getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) message1.getDataElements().get(7).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((IntElementLengthValue) message.getDataElements().get(11).getElementLengthValue()).getValue(),
                                            ((IntElementLengthValue) message1.getDataElements().get(11).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((IntElementLengthValue) message.getDataElements().get(19).getElementLengthValue()).getValue(),
                                            ((IntElementLengthValue) message1.getDataElements().get(19).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((IntElementLengthValue) message.getDataElements().get(23).getElementLengthValue()).getValue(),
                                            ((IntElementLengthValue) message1.getDataElements().get(23).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((IntElementLengthValue) message.getDataElements().get(25).getElementLengthValue()).getValue(),
                                            ((IntElementLengthValue) message1.getDataElements().get(25).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((StringElementLengthValue) message.getDataElements().get(32).getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) message1.getDataElements().get(32).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((StringElementLengthValue) message.getDataElements().get(37).getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) message1.getDataElements().get(37).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((StringElementLengthValue) message.getDataElements().get(41).getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) message1.getDataElements().get(41).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((StringElementLengthValue) message.getDataElements().get(42).getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) message1.getDataElements().get(42).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((IntElementLengthValue) message.getDataElements().get(49).getElementLengthValue()).getValue(),
                                            ((IntElementLengthValue) message1.getDataElements().get(49).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((StringElementLengthValue) ((SubElementLengthValue) message.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S002").getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) ((SubElementLengthValue) message1.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S002").getElementLengthValue()).getValue()),
                                    () -> assertEquals(((StringElementLengthValue) ((SubElementLengthValue) message.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S025").getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) ((SubElementLengthValue) message1.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S025").getElementLengthValue()).getValue()),
                                    () -> assertTrue(((SubElementBitmapMap) ((SubElementLengthValue) message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(1)),
                                    () -> assertFalse(((SubElementBitmapMap) ((SubElementLengthValue) message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(0)),
                                    () -> assertTrue(((SubElementBitmapMap) ((SubElementLengthValue) message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(24)),
                                    () -> assertFalse(message1.getBitMap().get(0)),
                                    ()-> assertTrue(message1.getBitMap().get(38)),
                                    ()-> assertEquals("00", ((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue()),
                                    ()-> assertEquals(nonAuthMessage.getMessageId(),nonAuthOutgoingMessage.getMessageId()),
                                    ()-> assertEquals(nonAuthMessage.getMessageTypeId(),nonAuthOutgoingMessage.getMessageTypeId()),
                                    ()-> assertEquals(nonAuthMessage.getChannelId(),nonAuthOutgoingMessage.getChannelId()),
                                    ()-> assertEquals(nonAuthMessage.getContainerId(),nonAuthOutgoingMessage.getContainerId()),
                                    ()-> assertEquals("05",nonAuthOutgoingMessage.getOriginalResponseCode())
                            );


                        }


                );

    }

    @Test
    void isRequest() throws Exception {

        Integer[] integers = new Integer[]{2,3,4,6,7,11,14,18,19,22,23,25,32,33,37,39,38,41,42,43,49,51,55,59,60,62,63};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",120);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);

        NonAuthMessage nonAuthMessage120 = NonAuthMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageTypeId("120")
                .message(message)
                .channelId(UUID.randomUUID().toString())
                .containerId(UUID.randomUUID().toString())
                .build();

        byte[] messageBytes400 = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",400);

        basicElementMessage = isoMessageFactory.createElementMessage(messageBytes400);
        message = isoMessageFactory.createFinalMessage(basicElementMessage);

        NonAuthMessage nonAuthMessage400 = NonAuthMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageTypeId("400")
                .message(message)
                .channelId(UUID.randomUUID().toString())
                .containerId(UUID.randomUUID().toString())
                .build();

        byte[] messageBytes420 = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",420);

        basicElementMessage = isoMessageFactory.createElementMessage(messageBytes420);
        message = isoMessageFactory.createFinalMessage(basicElementMessage);

        NonAuthMessage nonAuthMessage420 = NonAuthMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageTypeId("420")
                .message(message)
                .channelId(UUID.randomUUID().toString())
                .containerId(UUID.randomUUID().toString())
                .build();

        byte[] messageBytes600 = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",600);

        basicElementMessage = isoMessageFactory.createElementMessage(messageBytes600);
        message = isoMessageFactory.createFinalMessage(basicElementMessage);

        NonAuthMessage nonAuthMessage620 = NonAuthMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageTypeId("620")
                .message(message)
                .channelId(UUID.randomUUID().toString())
                .containerId(UUID.randomUUID().toString())
                .build();


        byte[] messageBytes312 = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",312);

        basicElementMessage = isoMessageFactory.createElementMessage(messageBytes312);
        message = isoMessageFactory.createFinalMessage(basicElementMessage);

        NonAuthMessage nonAuthMessage312 = NonAuthMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageTypeId("312")
                .message(message)
                .channelId(UUID.randomUUID().toString())
                .containerId(UUID.randomUUID().toString())
                .build();

        byte[] messageBytes800 = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",800);

        basicElementMessage = isoMessageFactory.createElementMessage(messageBytes800);
        message = isoMessageFactory.createFinalMessage(basicElementMessage);

        NonAuthMessage nonAuthMessage800 = NonAuthMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageTypeId("800")
                .message(message)
                .channelId(UUID.randomUUID().toString())
                .containerId(UUID.randomUUID().toString())
                .build();

        byte[] messageBytes810 = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",810);

        basicElementMessage = isoMessageFactory.createElementMessage(messageBytes810);
        message = isoMessageFactory.createFinalMessage(basicElementMessage);

        NonAuthMessage nonAuthMessage810 = NonAuthMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageTypeId("810")
                .message(message)
                .channelId(UUID.randomUUID().toString())
                .containerId(UUID.randomUUID().toString())
                .build();

        assertAll(
                ()->assertTrue(nonAuthMessageService.isMessageRequest(nonAuthMessage120)),
                ()-> assertTrue(nonAuthMessageService.isMessageRequest(nonAuthMessage400)),
                ()-> assertTrue(nonAuthMessageService.isMessageRequest(nonAuthMessage420)),
                ()-> assertTrue(nonAuthMessageService.isMessageRequest(nonAuthMessage620)),
                ()-> assertTrue(nonAuthMessageService.isMessageRequest(nonAuthMessage800)),
                ()-> assertFalse(nonAuthMessageService.isMessageRequest(nonAuthMessage312)),
                ()-> assertFalse(nonAuthMessageService.isMessageRequest(nonAuthMessage810))


                );
    }


    private AuthSnapShot createAuthSnapShot(String instrument, Integer traceNumber,
                                            Integer mti , String de007,
                                            String responseCode, long billAmount, long transactionAmount,
                                            String billCurrency, String txnCurrency, ReversalStatus reversalStatus){


        String keyExt = new StringBuilder()
                .append(mti.toString())
                .append(traceNumber.toString())
                .append(de007)
                .toString();

        SnapshotKey snapshotKey = SnapshotKey.builder()
                .instrument(instrument)
                .keyExtension(keyExt)
                .build();

        return AuthSnapShot.builder()
                .authCode("123456")
                .responseCode(responseCode)
                .authorizationTime(LocalDateTime.now())
                .balanceTypes(new ArrayList<>())
                .billingAmount(billAmount)
                .preAuthTime(0)
                .billingCurrencyCode(billCurrency)
                .reversalStatus(reversalStatus)
                .traceNumber(traceNumber)
                .transactionAmount(transactionAmount)
                .transactionCurrencyCode(txnCurrency)
                .de007(de007)
                .limitTypes(new ArrayList<>())
                .snapshotKey(snapshotKey)
                .build()
                ;

    }
}