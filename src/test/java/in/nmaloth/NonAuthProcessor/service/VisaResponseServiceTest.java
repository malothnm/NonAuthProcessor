package in.nmaloth.NonAuthProcessor.service;

import com.nithin.iso8583.iso.elementdef.common.interfaces.IntElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.LongElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.StringElementLengthValue;
import com.nithin.iso8583.iso.elementdef.util.Utils;
import com.nithin.iso8583.iso.message.BasicElementMessage;
import com.nithin.iso8583.iso.message.ISOMessageFactory;
import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.model.NonAuthOutgoingMessage;
import in.nmaloth.NonAuthProcessor.repositories.AuthSnapShotRepository;
import in.nmaloth.entity.logs.AuthSnapShot;
import in.nmaloth.entity.logs.ReversalStatus;
import in.nmaloth.entity.logs.SnapshotKey;
import in.nmaloth.payments.constants.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.swing.plaf.UIResource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VisaResponseServiceTest {

    @Autowired
    private AuthSnapShotRepository authSnapShotRepository;

    @Autowired
    private ISOMessageFactory isoMessageFactory;

    @Autowired
    private ResponseService responseService;

    @Autowired
    private SchemeResponseService schemeResponseService;




    SetupData setupData = new SetupData();

    @BeforeEach
    void setSetupData(){

        authSnapShotRepository.findAll()
                .forEach(authSnapShot -> authSnapShotRepository.delete(authSnapShot));
//        String instrument = "1234567890123456";
//        Integer traceNumber = Utils.hextoInteger(setupData.de011);
//        String de07 = Utils.hexToString(setupData.getDE007());
//
//        String keyExt = new StringBuilder()
//                .append(100)
//                .append(traceNumber.toString())
//                .append(de07)
//                .toString();
//
//        SnapshotKey snapshotKey = SnapshotKey.builder()
//                .instrument(instrument)
//                .keyExtension(keyExt)
//                .build();
//
//        Optional<AuthSnapShot> authSnapShotOptional = authSnapShotRepository.findById(snapshotKey);
//        if(authSnapShotOptional.isPresent()){
//            authSnapShotRepository.delete(authSnapShotOptional.get());
//        }

    }

    @Test
    void testSnapShotVerification() throws Exception {

        String instrument = "1234567890123456";
        Integer traceNumber = Utils.hextoInteger(setupData.de011);
        String de07 = Utils.hexToString(setupData.getDE007());
        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",0,1000,"484","484",ReversalStatus.NOT_REVERSED);
        authSnapShotRepository.save(authSnapShot);


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        Mono<Optional<AuthSnapShot>> optionalSnapShotMono = schemeResponseService.getOriginalAuthSnapShot(responseMessage);

        StepVerifier.create(optionalSnapShotMono)
                .consumeNextWith(authSnapShotOptional -> {
                    assertAll(
                            ()-> assertTrue(authSnapShotOptional.isPresent())
                    );
                });

    }

    @Test
    void testSnapShotVerificationFailure() throws Exception {

        String instrument = "1234567890123456";
//        Integer traceNumber = Utils.hextoInteger(setupData.de011);
//        String de07 = Utils.hexToString(setupData.getDE007());
//        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",0,1000,"484","484",ReversalStatus.NOT_REVERSED);
//        authSnapShotRepository.save(authSnapShot);


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        Mono<Optional<AuthSnapShot>> optionalSnapShotMono = schemeResponseService.getOriginalAuthSnapShot(responseMessage);

        StepVerifier.create(optionalSnapShotMono)
                .consumeNextWith(authSnapShotOptional -> {
                    assertAll(
                            ()-> assertFalse(authSnapShotOptional.isPresent())
                    );
                });

    }


    private AuthSnapShot createAuthSnapShot(String instrument, Integer traceNumber,
                                            Integer mti , String de007,
                                            String responseCode, long billAmount, long transactionAmount,
                                            String billCurrency, String txnCurrency,ReversalStatus reversalStatus){


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

    @Test
    void testSnapShotReversalMessage() throws Exception {

        String instrument = "1234567890123456";
        Integer traceNumber = Utils.hextoInteger(setupData.de011);
        String de07 = Utils.hexToString(setupData.getDE007());
        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",0,1000,"484","484",ReversalStatus.NOT_REVERSED);
//        authSnapShotRepository.save(authSnapShot);


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        NonAuthOutgoingMessage nonAuthOutgoingMessage = schemeResponseService.updateResponseFieldsReversals(responseMessage,Optional.of(authSnapShot));


        assertAll(
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(38)),
                ()-> assertEquals("00", ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(39).getElementLengthValue()).getValue())
        );


    }


    @Test
    void testSnapShotReversalMessage1() throws Exception {

        String instrument = "1234567890123456";
        Integer traceNumber = Utils.hextoInteger(setupData.de011);
        String de07 = Utils.hexToString(setupData.getDE007());
        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",500,1000,"840","484",ReversalStatus.NOT_REVERSED);
//        authSnapShotRepository.save(authSnapShot);


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        NonAuthOutgoingMessage nonAuthOutgoingMessage = schemeResponseService.updateResponseFieldsReversals(responseMessage,Optional.of(authSnapShot));


        assertAll(
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(38)),
                ()-> assertEquals("00", ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(39).getElementLengthValue()).getValue()),
                ()-> assertEquals(500,((LongElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(6).getElementLengthValue()).getValue()),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(5)),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(50)),
                ()-> assertEquals(840,((IntElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(51).getElementLengthValue()).getValue()),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(9)),
                ()-> assertEquals("60500000",((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(10).getElementLengthValue()).getValue())


                );


    }

    @Test
    void testSnapShotReversalMessage2() throws Exception {

        String instrument = "1234567890123456";
        Integer traceNumber = Utils.hextoInteger(setupData.de011);
        String de07 = Utils.hexToString(setupData.getDE007());
        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",1500000,1000,"840","484",ReversalStatus.NOT_REVERSED);
//        authSnapShotRepository.save(authSnapShot);


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        NonAuthOutgoingMessage nonAuthOutgoingMessage = schemeResponseService.updateResponseFieldsReversals(responseMessage,Optional.of(authSnapShot));


        assertAll(
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(38)),
                ()-> assertEquals("00", ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(39).getElementLengthValue()).getValue()),
                ()-> assertEquals(1500000,((LongElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(6).getElementLengthValue()).getValue()),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(5)),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(50)),
                ()-> assertEquals(840,((IntElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(51).getElementLengthValue()).getValue()),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(9)),
                ()-> assertEquals("31500000",((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(10).getElementLengthValue()).getValue())


        );


    }

    @Test
    void testSnapShotReversalMessage3() throws Exception {

        String instrument = "1234567890123456";
        Integer traceNumber = Utils.hextoInteger(setupData.de011);
        String de07 = Utils.hexToString(setupData.getDE007());
        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",1500000,2000,"840","484",ReversalStatus.NOT_REVERSED);
//        authSnapShotRepository.save(authSnapShot);


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        NonAuthOutgoingMessage nonAuthOutgoingMessage = schemeResponseService.updateResponseFieldsReversals(responseMessage,Optional.of(authSnapShot));


        assertAll(
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(38)),
                ()-> assertEquals("00", ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(39).getElementLengthValue()).getValue()),
                ()-> assertEquals(750000,((LongElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(6).getElementLengthValue()).getValue()),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(5)),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(50)),
                ()-> assertEquals(840,((IntElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(51).getElementLengthValue()).getValue()),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(9)),
                ()-> assertEquals("47500000",((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(10).getElementLengthValue()).getValue())

        );


    }

    @Test
    void testSnapShotReversalMessage4() throws Exception {

        String instrument = "1234567890123456";
        Integer traceNumber = Utils.hextoInteger(setupData.de011);
        String de07 = Utils.hexToString(setupData.getDE007());
        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",2014,1003,"840","484",ReversalStatus.NOT_REVERSED);
//        authSnapShotRepository.save(authSnapShot);


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        NonAuthOutgoingMessage nonAuthOutgoingMessage = schemeResponseService.updateResponseFieldsReversals(responseMessage,Optional.of(authSnapShot));


        assertAll(
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(38)),
                ()-> assertEquals("00", ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(39).getElementLengthValue()).getValue()),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(5)),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(50)),
                ()-> assertEquals(840,((IntElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(51).getElementLengthValue()).getValue()),
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(9)),
                ()-> assertEquals("62007976",((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(10).getElementLengthValue()).getValue())

        );


    }

    @Test
    void testSnapShotReversalMessage5() throws Exception {

        String instrument = "1234567890123456";
        Integer traceNumber = Utils.hextoInteger(setupData.de011);
        String de07 = Utils.hexToString(setupData.getDE007());
        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",2014,990,"840","484",ReversalStatus.NOT_REVERSED);
//        authSnapShotRepository.save(authSnapShot);


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        NonAuthOutgoingMessage nonAuthOutgoingMessage = schemeResponseService.updateResponseFieldsReversals(responseMessage,Optional.of(authSnapShot));


        assertAll(
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(38)),
                ()-> assertEquals("05", ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(39).getElementLengthValue()).getValue()),
                ()-> assertFalse(nonAuthOutgoingMessage.getMessage().getBitMap().get(5)),
                ()-> assertFalse(nonAuthOutgoingMessage.getMessage().getBitMap().get(50))

        );


    }

    @Test
    void testSnapShotReversalMessage6() throws Exception {

        String instrument = "1234567890123456";
        Integer traceNumber = Utils.hextoInteger(setupData.de011);
        String de07 = Utils.hexToString(setupData.getDE007());
        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",2014,1000,"840","484",ReversalStatus.NOT_REVERSED);
//        authSnapShotRepository.save(authSnapShot);


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        NonAuthOutgoingMessage nonAuthOutgoingMessage = schemeResponseService.updateResponseFieldsReversals(responseMessage,Optional.empty());


        assertAll(
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(38)),
                ()-> assertEquals("05", ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(39).getElementLengthValue()).getValue()),
                ()-> assertFalse(nonAuthOutgoingMessage.getMessage().getBitMap().get(5)),
                ()-> assertFalse(nonAuthOutgoingMessage.getMessage().getBitMap().get(50))

        );


    }

    @Test
    void testSnapShotReversalMessage7() throws Exception {

        String instrument = "1234567890123456";
        Integer traceNumber = Utils.hextoInteger(setupData.de011);
        String de07 = Utils.hexToString(setupData.getDE007());
        AuthSnapShot authSnapShot = createAuthSnapShot(instrument,traceNumber,100,de07,"00",2014,1000,"840","484",ReversalStatus.REVERSED);
//        authSnapShotRepository.save(authSnapShot);


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        NonAuthOutgoingMessage nonAuthOutgoingMessage = schemeResponseService.updateResponseFieldsReversals(responseMessage,Optional.of(authSnapShot));


        assertAll(
                ()-> assertTrue(nonAuthOutgoingMessage.getMessage().getBitMap().get(38)),
                ()-> assertEquals("05", ((StringElementLengthValue)nonAuthOutgoingMessage.getMessage().getDataElements().get(39).getElementLengthValue()).getValue()),
                ()-> assertFalse(nonAuthOutgoingMessage.getMessage().getBitMap().get(5)),
                ()-> assertFalse(nonAuthOutgoingMessage.getMessage().getBitMap().get(50))

        );


    }

    @Test
    void testAdviceMessage1() throws Exception {


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,120);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        Message message1 = schemeResponseService.updateAdviceMessages(responseMessage);


        assertAll(
                ()-> assertTrue(message1.getBitMap().get(38)),
                ()-> assertEquals("00", ((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue())


        );


    }

    @Test
    void testAdviceMessageReversal() throws Exception {


        Integer[] integers = new Integer[]{2,3,4,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,420);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        Message message1 = schemeResponseService.updateAdviceMessages(responseMessage);


        assertAll(
                ()-> assertTrue(message1.getBitMap().get(38)),
                ()-> assertEquals("00", ((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue())


        );


    }

    @Test
    void tokenNotificationAdviceTest() throws Exception {

        Integer[] integers = new Integer[]{2,7,11,14,33,37,39,63,70,101,120,123,125};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.TOKEN_EVENT_NOTIFICATION, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,620);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message responseMessage = responseService.populateResponseFieldsMessage(message);
        Message message1 = schemeResponseService.updateAdviceMessages(responseMessage);

        assertAll(
                ()-> assertTrue(message1.getBitMap().get(38)),
                ()-> assertEquals("00", ((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue())
      );

    }

}