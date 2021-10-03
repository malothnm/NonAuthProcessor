package in.nmaloth.NonAuthProcessor.service;

import com.nithin.iso8583.iso.elementdef.common.interfaces.IntElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.LongElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.StringElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.SubElementLengthValue;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElementMap;
import com.nithin.iso8583.iso.elementdef.subelement.bitmap.SubElementBitmapMap;
import com.nithin.iso8583.iso.elementdef.util.Utils;
import com.nithin.iso8583.iso.message.BasicElementMessage;
import com.nithin.iso8583.iso.message.Header.visa.VisaHeaderComplete;
import com.nithin.iso8583.iso.message.Header.visa.VisaHeaderType;
import com.nithin.iso8583.iso.message.ISOMessageFactory;
import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.model.NonAuthMessage;
import in.nmaloth.NonAuthProcessor.model.NonAuthOutgoingMessage;
import in.nmaloth.NonAuthProcessor.repositories.AuthSnapShotRepository;
import in.nmaloth.entity.logs.AuthSnapShot;
import in.nmaloth.entity.logs.ReversalStatus;
import in.nmaloth.entity.logs.SnapshotKey;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.network.NetworkAdviceInit;
import in.nmaloth.payments.constants.network.NetworkKeyExchange;
import in.nmaloth.payments.constants.network.NetworkMessageType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ResponseServiceImplTest {

    SetupData setupData = new SetupData();

    @Autowired
    ISOMessageFactory isoMessageFactory;

    @Autowired
    private AuthSnapShotRepository authSnapShotRepository;



    @Autowired
    private ResponseService responseService;

    @Test
    void populateResponseFieldsMessageNetworkMessages() throws Exception {

        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.SIGN_ON, NetworkAdviceInit.NONE, NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message message1 = responseService.populateResponseFieldsMessage(message);

        assertAll(
                ()-> assertNotNull(message1),
                ()-> assertEquals(810,message1.getMessageTypeIdentifier()),
                ()-> assertEquals(0x16,((VisaHeaderComplete)message1.getHeader()).getHeader().getHeaderLength()),
                ()-> assertEquals(VisaHeaderType.NORMAL_HEADER,((VisaHeaderComplete)message1.getHeader()).getHeader().getVisaHeaderType()),
                ()-> assertEquals(0x01,((VisaHeaderComplete)message1.getHeader()).getHeader().getHeaderFormat()),
                ()-> assertEquals(0x0C,((VisaHeaderComplete)message1.getHeader()).getHeader().getTextFormat()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message1.getHeader()).getHeader().getTotalMessageLength()),
                ()-> assertArrayEquals(((VisaHeaderComplete)message.getHeader()).getHeader().getSourceStationId(),((VisaHeaderComplete)message1.getHeader()).getHeader().getDestinationStationId()),
                ()-> assertArrayEquals(((VisaHeaderComplete)message.getHeader()).getHeader().getDestinationStationId(),((VisaHeaderComplete)message1.getHeader()).getHeader().getSourceStationId()),
                ()-> assertEquals(0x23,((VisaHeaderComplete)message1.getHeader()).getHeader().getRoundTripControlInfo()),
                ()-> assertArrayEquals(new byte[]{0x12,0x23},((VisaHeaderComplete)message1.getHeader()).getHeader().getBase1Flags()),
                ()-> assertArrayEquals(new byte[]{0x01,0x02,0x03},((VisaHeaderComplete)message1.getHeader()).getHeader().getMessageStatusFlags()),
                ()-> assertEquals(0x0A,((VisaHeaderComplete)message.getHeader()).getHeader().getBatchNumber()),
                ()-> assertArrayEquals(new byte[]{0x04,0x05,0x06},((VisaHeaderComplete)message1.getHeader()).getHeader().getReserved()),
                ()-> assertEquals(0x0B,((VisaHeaderComplete)message1.getHeader()).getHeader().getUserInfo()),
                ()-> assertTrue(message1.getBitMap().get(0)),
                ()-> assertTrue(message1.getBitMap().get(6)),
                ()-> assertTrue(message1.getBitMap().get(10)),
                ()-> assertTrue(message1.getBitMap().get(36)),
                ()-> assertTrue(message1.getBitMap().get(69)),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(7).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(11).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(37).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(70).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(70).getElementLengthValue()).getValue())
        );

    }

    @Test
    void testAdvices() throws Exception {

        Integer[] integers = new Integer[]{2,3,4,6,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,51,55,59,60,62,63};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",120);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message message1 = responseService.populateResponseFieldsMessage(message);

        SubElementMap subElementMap = ((SubElementLengthValue) message1.getDataElements().get(3).getElementLengthValue()).getValue();
        int processingCode = ((IntElementLengthValue)subElementMap.getSubElementMap().get("DE003S001").getElementLengthValue()).getValue();
        assertAll(
                ()-> assertNotNull(message1),
                ()-> assertEquals(130,message1.getMessageTypeIdentifier()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(2).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(2).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S001").getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S001").getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S002").getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S002").getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S003").getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S003").getElementLengthValue()).getValue()),
                ()-> assertEquals(((LongElementLengthValue)message.getDataElements().get(4).getElementLengthValue()).getValue(),
                        ((LongElementLengthValue)message1.getDataElements().get(4).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(7).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(11).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(19).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(19).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(23).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(23).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(25).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(25).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(32).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(32).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(37).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(41).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(41).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(42).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(42).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(49).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(49).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)((SubElementLengthValue)message.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S002").getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S002").getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)((SubElementLengthValue)message.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S025").getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S025").getElementLengthValue()).getValue()),
                ()-> assertTrue(((SubElementBitmapMap)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(1)),
                ()-> assertFalse(((SubElementBitmapMap)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(0)),
                ()-> assertTrue(((SubElementBitmapMap)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(24)),
                ()-> assertFalse(message1.getBitMap().get(0))


                );

    }

    @Test
    void testAdvices1() throws Exception {

        Integer[] integers = new Integer[]{2,3,4,6,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,51,55,59,60,62,63};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",1,120);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message message1 = responseService.populateResponseFieldsMessage(message);

        SubElementMap subElementMap = ((SubElementLengthValue) message1.getDataElements().get(3).getElementLengthValue()).getValue();
        int processingCode = ((IntElementLengthValue)subElementMap.getSubElementMap().get("DE003S001").getElementLengthValue()).getValue();
        assertAll(
                ()-> assertNotNull(message1),
                ()-> assertEquals(130,message1.getMessageTypeIdentifier()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(2).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(2).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S001").getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S001").getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S002").getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S002").getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S003").getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S003").getElementLengthValue()).getValue()),
                ()-> assertEquals(((LongElementLengthValue)message.getDataElements().get(4).getElementLengthValue()).getValue(),
                        ((LongElementLengthValue)message1.getDataElements().get(4).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(7).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(11).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(19).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(19).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(23).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(23).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(25).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(25).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(32).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(32).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(37).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(41).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(41).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(42).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(42).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(49).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(49).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)((SubElementLengthValue)message.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S002").getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S002").getElementLengthValue()).getValue()),
                ()-> assertNull(((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S025")),
                ()-> assertTrue(((SubElementBitmapMap)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(1)),
                ()-> assertFalse(((SubElementBitmapMap)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(0)),
                ()-> assertFalse(((SubElementBitmapMap)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(24)),
                ()-> assertFalse(message1.getBitMap().get(0))


        );

    }

    @Test
    void testReversals() throws Exception {

        Integer[] integers = new Integer[]{2,3,4,6,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,51,55,59,60,62,63,90,126};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",1,400);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Message message1 = responseService.populateResponseFieldsMessage(message);

        SubElementMap subElementMap = ((SubElementLengthValue) message1.getDataElements().get(3).getElementLengthValue()).getValue();
        int processingCode = ((IntElementLengthValue)subElementMap.getSubElementMap().get("DE003S001").getElementLengthValue()).getValue();
        assertAll(
                ()-> assertNotNull(message1),
                ()-> assertEquals(410,message1.getMessageTypeIdentifier()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(2).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(2).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S001").getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S001").getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S002").getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S002").getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)((SubElementLengthValue)message.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S003").getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(3).getElementLengthValue()).getValue().getSubElementMap().get("DE003S003").getElementLengthValue()).getValue()),
                ()-> assertEquals(((LongElementLengthValue)message.getDataElements().get(4).getElementLengthValue()).getValue(),
                        ((LongElementLengthValue)message1.getDataElements().get(4).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(7).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(11).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(19).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(19).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(23).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(23).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(25).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(25).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(32).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(32).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(37).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(41).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(41).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(42).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(42).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(49).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(49).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)((SubElementLengthValue)message.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S002").getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S002").getElementLengthValue()).getValue()),
                ()-> assertNull(((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue().getSubElementMap().get("DE062S025")),
                ()-> assertTrue(((SubElementBitmapMap)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(1)),
                ()-> assertFalse(((SubElementBitmapMap)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(0)),
                ()-> assertFalse(((SubElementBitmapMap)((SubElementLengthValue)message1.getDataElements().get(62).getElementLengthValue()).getValue()).getBitSet().get(24)),
                ()-> assertTrue(message1.getBitMap().get(0)),
                ()-> assertEquals(((StringElementLengthValue)((SubElementLengthValue)message.getDataElements().get(126).getElementLengthValue()).getValue().getSubElementMap().get("DE126S012").getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)((SubElementLengthValue)message1.getDataElements().get(126).getElementLengthValue()).getValue().getSubElementMap().get("DE126S012").getElementLengthValue()).getValue())

        );

    }

    @Test
    void createNetworkMessageResponse() throws Exception {
        byte[] messageBytes = setupData.createNetworkMessages(NetworkMessageType.ECHO,NetworkAdviceInit.NONE,NetworkKeyExchange.NO_KEY_EXCHANGE);
        BasicElementMessage basicMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicMessage);
        Message message1 = responseService.createNetworkMessageResponse(message,"00");

        assertAll(
                ()-> assertEquals(810,message1.getMessageTypeIdentifier()),
                ()-> assertEquals(0x16,((VisaHeaderComplete)message1.getHeader()).getHeader().getHeaderLength()),
                ()-> assertEquals(VisaHeaderType.NORMAL_HEADER,((VisaHeaderComplete)message1.getHeader()).getHeader().getVisaHeaderType()),
                ()-> assertEquals(0x01,((VisaHeaderComplete)message1.getHeader()).getHeader().getHeaderFormat()),
                ()-> assertEquals(0x0C,((VisaHeaderComplete)message1.getHeader()).getHeader().getTextFormat()),
                ()-> assertArrayEquals(new byte[]{0x00,0x00},((VisaHeaderComplete)message1.getHeader()).getHeader().getTotalMessageLength()),
                ()-> assertArrayEquals(((VisaHeaderComplete)message.getHeader()).getHeader().getSourceStationId(),((VisaHeaderComplete)message1.getHeader()).getHeader().getDestinationStationId()),
                ()-> assertArrayEquals(((VisaHeaderComplete)message.getHeader()).getHeader().getDestinationStationId(),((VisaHeaderComplete)message1.getHeader()).getHeader().getSourceStationId()),
                ()-> assertEquals(0x23,((VisaHeaderComplete)message1.getHeader()).getHeader().getRoundTripControlInfo()),
                ()-> assertArrayEquals(new byte[]{0x12,0x23},((VisaHeaderComplete)message1.getHeader()).getHeader().getBase1Flags()),
                ()-> assertArrayEquals(new byte[]{0x01,0x02,0x03},((VisaHeaderComplete)message1.getHeader()).getHeader().getMessageStatusFlags()),
                ()-> assertEquals(0x0A,((VisaHeaderComplete)message.getHeader()).getHeader().getBatchNumber()),
                ()-> assertArrayEquals(new byte[]{0x04,0x05,0x06},((VisaHeaderComplete)message1.getHeader()).getHeader().getReserved()),
                ()-> assertEquals(0x0B,((VisaHeaderComplete)message1.getHeader()).getHeader().getUserInfo()),
                ()-> assertTrue(message1.getBitMap().get(0)),
                ()-> assertTrue(message1.getBitMap().get(6)),
                ()-> assertTrue(message1.getBitMap().get(10)),
                ()-> assertTrue(message1.getBitMap().get(36)),
                ()-> assertTrue(message1.getBitMap().get(38)),
                ()-> assertTrue(message1.getBitMap().get(69)),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(7).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(7).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(11).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(11).getElementLengthValue()).getValue()),
                ()-> assertEquals(((StringElementLengthValue)message.getDataElements().get(37).getElementLengthValue()).getValue(),
                        ((StringElementLengthValue)message1.getDataElements().get(37).getElementLengthValue()).getValue()),
                ()-> assertEquals(((IntElementLengthValue)message.getDataElements().get(70).getElementLengthValue()).getValue(),
                        ((IntElementLengthValue)message1.getDataElements().get(70).getElementLengthValue()).getValue()),
                ()-> assertEquals("00",((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue()))
        ;
    }

    @Test
    void processReversalResponse() throws Exception {

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
        NonAuthMessage nonAuthMessage = NonAuthMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageTypeId("400")
                .message(message)
                .channelId(UUID.randomUUID().toString())
                .containerId(UUID.randomUUID().toString())
                .build();

        Mono<NonAuthOutgoingMessage> nonAuthOutgoingMessageMono = responseService.processReversalResponse(nonAuthMessage);

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
    void processForAdviceResponse() throws Exception {

        Integer[] integers = new Integer[]{2,3,4,6,7,11,14,18,19,22,23,25,32,33,37,38,41,42,43,49,51,55,59,60,62,63};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.PURCHASE, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS_NOT_PRESENT, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"840","484",120);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Mono<Message> messageMono = responseService.processForAdviceResponse(message);

        StepVerifier.create(messageMono)
                .consumeNextWith(message1 -> {
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
                                    ()-> assertEquals("00", ((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue())
                            );


                        }


                    );

    }

    @Test
    void processForTokenAdviceResponse() throws Exception {

        Integer[] integers = new Integer[]{2,7,11,14,33,37,39,63,70,101,120,123,125};
        byte[] messageBytes = setupData.createAuthMessage(integers, TransactionType.TOKEN_EVENT_NOTIFICATION, TerminalPinCapability.CANNOT_ACCEPT_PIN,
                EntryMode.ICC, AVSType.AVS, RecurringTrans.NOT_RECURRING_TRANS, PinEntryMode.ONLINE_PIN,null,"484","484",1,620);

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        Message message = isoMessageFactory.createFinalMessage(basicElementMessage);
        Mono<Message> messageMono = responseService.processForAdviceResponse(message);

        StepVerifier.create(messageMono)
                .consumeNextWith(message1 -> {
                            assertAll(
                                    () -> assertNotNull(message1),
                                    () -> assertEquals(630, message1.getMessageTypeIdentifier()),
                                    () -> assertEquals(((StringElementLengthValue) message.getDataElements().get(2).getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) message1.getDataElements().get(2).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((StringElementLengthValue) message.getDataElements().get(7).getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) message1.getDataElements().get(7).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((IntElementLengthValue) message.getDataElements().get(11).getElementLengthValue()).getValue(),
                                            ((IntElementLengthValue) message1.getDataElements().get(11).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((StringElementLengthValue) message.getDataElements().get(37).getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) message1.getDataElements().get(37).getElementLengthValue()).getValue()),
                                    () -> assertEquals(((StringElementLengthValue) ((SubElementLengthValue) message.getDataElements().get(63).getElementLengthValue()).getValue().getSubElementMap().get("DE063S001").getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) ((SubElementLengthValue) message1.getDataElements().get(63).getElementLengthValue()).getValue().getSubElementMap().get("DE063S001").getElementLengthValue()).getValue()),
                                    () -> assertTrue(((SubElementBitmapMap) ((SubElementLengthValue) message1.getDataElements().get(63).getElementLengthValue()).getValue()).getBitSet().get(0)),
                                    () -> assertEquals(((StringElementLengthValue) message.getDataElements().get(70).getElementLengthValue()).getValue(),
                                            ((StringElementLengthValue) message1.getDataElements().get(70).getElementLengthValue()).getValue()),
                                    () -> assertFalse(message1.getBitMap().get(0)),
                                    ()-> assertTrue(message1.getBitMap().get(38)),
                                    ()-> assertEquals("00", ((StringElementLengthValue)message1.getDataElements().get(39).getElementLengthValue()).getValue())
                            );


                        }


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