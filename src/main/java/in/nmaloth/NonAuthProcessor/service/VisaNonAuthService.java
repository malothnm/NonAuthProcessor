package in.nmaloth.NonAuthProcessor.service;

import com.nithin.iso8583.iso.elementdef.common.interfaces.IntElementLengthValue;
import com.nithin.iso8583.iso.elementdef.element.BasicElement;
import com.nithin.iso8583.iso.elementdef.element.Element;
import com.nithin.iso8583.iso.elementdef.element.ISOProtoElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElementMap;
import com.nithin.iso8583.iso.message.BasicElementMessage;
import com.nithin.iso8583.iso.message.Header.Header;
import com.nithin.iso8583.iso.message.Header.visa.VisaProtoHeader;
import com.nithin.iso8583.iso.message.ISOMessageFactory;
import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.constants.BeanConfigNames;
import in.nmaloth.NonAuthProcessor.model.MessageType;
import in.nmaloth.NonAuthProcessor.model.NonAuthMessage;
import in.nmaloth.NonAuthProcessor.model.dto.CardHolderSchemeDto;
import in.nmaloth.NonAuthProcessor.model.dto.ExceptionFileDto;
import in.nmaloth.NonAuthProcessor.model.dto.NetworkMessageDto;
import in.nmaloth.NonAuthProcessor.model.dto.StopPaymentDto;
import in.nmaloth.entity.network.NetworkProperties;
import in.nmaloth.payments.constants.network.NetworkAdviceInit;
import in.nmaloth.payments.constants.network.NetworkKeyExchange;
import in.nmaloth.payments.constants.network.NetworkMessageType;
import in.nmaloth.payments.constants.network.NetworkType;
import in.nmaloth.payments.constants.schemeDatabase.ExceptionActionCodes;
import in.nmaloth.payments.constants.schemeDatabase.FileUpdateActions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

@Service
public class VisaNonAuthService implements SchemeNonAuthService {

    private final ISOMessageFactory isoMessageFactory;
    private final ISOProtoElement isoProtoElementEx;
    private final ISOProtoElement isoProtoElementPanUpdate;




    private final Random random = new Random();


    public VisaNonAuthService(ISOMessageFactory isoMessageFactory,
                              @Qualifier(BeanConfigNames.ISO_EXCEPTION_FILE_UPDATE) ISOProtoElement isoProtoElementEx,
                              @Qualifier(BeanConfigNames.ISO_FILE_MAINTENANCE) ISOProtoElement isoProtoElementPanUpdate
                              ) {

        this.isoMessageFactory = isoMessageFactory;
        this.isoProtoElementEx = isoProtoElementEx;
        this.isoProtoElementPanUpdate = isoProtoElementPanUpdate;
    }

    @Override
    public Message convertToMessage(NetworkMessageDto networkMessageDto,NetworkProperties networkProperties) {

        Message message = initialiseMessage(800,networkMessageDto.getLocalDateTime(),networkMessageDto.getTraceNumber(),networkProperties);

       int networkCodes = 0 ;
       switch (networkMessageDto.getNetworkMessageType()){
           case GROUP_SIGN_ON:
           case SIGN_ON:{
                networkCodes = 71;
                break;
           }
           case SIGN_OFF:
           case GROUP_SIGN_OFF:{
               networkCodes = 72;
               break;
           }
           case ECHO: {
               networkCodes = 301;
           }
           default:{
               if(networkMessageDto.getNetworkAdviceInit().equals(NetworkAdviceInit.START_ADVICE)){
                   networkCodes = 78;
               } else if(networkMessageDto.getNetworkAdviceInit().equals(NetworkAdviceInit.STOP_ADVICE)){
                   networkCodes = 79;
               }

           }
       }

       Element element70 = isoMessageFactory.getIsoProtoElementMap().get(70).createNewISOElement(networkCodes);
       message.getBitMap().set(69);
       message.getDataElements().put(70,element70);
        return message;
    }


    private Message initialiseMessage(int mti,LocalDateTime localDateTime, Integer traceNumber,NetworkProperties networkProperties){

        Message message = new Message();
        Header header = ((VisaProtoHeader)isoMessageFactory.getProtoHeader()).createNewVisaHeader(networkProperties.getStationId());
        message.setHeader(header);
        message.setMessageTypeIdentifier(mti);
        message.setBitMap(new BitSet(128));
        message.getBitMap().set(0);
        String time = createTimeGMT(localDateTime);
        Element element7 = isoMessageFactory.getIsoProtoElementMap().get(7).createNewISOElement(time);
        message.getDataElements().put(7,element7);
        message.getBitMap().set(6);

        Element element11 = isoMessageFactory.getIsoProtoElementMap().get(11).createNewISOElement(traceNumber);
        message.getDataElements().put(11,element11);
        message.getBitMap().set(10);


        String retrievalReferenceNumber = createRetrievalReferenceNumber(localDateTime,traceNumber);
        Element element37 = isoMessageFactory.getIsoProtoElementMap().get(37).createNewISOElement(retrievalReferenceNumber);
        message.getBitMap().set(36);
        message.getDataElements().put(37,element37);
        return message;
    }

    private String createTimeGMT(LocalDateTime localDateTime) {

        return localDateTime.format(DateTimeFormatter.ofPattern("MMddHHmmss"));
    }


    @Override
    public Message createNonAuthMessage(byte[] messageBytes) throws Exception {

        BasicElementMessage basicElementMessage = isoMessageFactory.createElementMessage(messageBytes);
        return isoMessageFactory.createFinalMessage(basicElementMessage);
    }


    @Override
    public NetworkMessageType identifyNetworkMessageType(Message message) {
        int networkCode = ((IntElementLengthValue)message.getDataElements().get(70).getElementLengthValue()).getValue();

        switch (networkCode){
            case 71 : return NetworkMessageType.GROUP_SIGN_ON;
            case 72: return NetworkMessageType.GROUP_SIGN_OFF;
            case 301: return NetworkMessageType.ECHO;
            default: return NetworkMessageType.NO_MESSAGE;
        }
    }

    @Override
    public NetworkAdviceInit identifyAdviceInit(Message message) {
        int networkCode = ((IntElementLengthValue)message.getDataElements().get(70).getElementLengthValue()).getValue();

        switch (networkCode){
            case 78: return NetworkAdviceInit.START_ADVICE;
            case 79: return NetworkAdviceInit.STOP_ADVICE;
            default: return NetworkAdviceInit.NONE;
        }
    }

    @Override
    public NetworkKeyExchange identifyKeyExchangeMessages(Message message) {
        return NetworkKeyExchange.NO_KEY_EXCHANGE;
    }

    @Override
    public NetworkType findNetworkType() {
        return NetworkType.VISA_VIP;
    }

    @Override
    public String findNetwork() {
        return "Visa VIP";
    }



    @Override
    public Message createExceptionFileUpdateMessage(ExceptionFileDto exceptionFileDto,NetworkProperties networkProperties) throws Exception {
        Message message = initialiseMessage(302,exceptionFileDto.getLocalDateTime(),exceptionFileDto.getTraceNumber(),networkProperties);

        populateInstrumentInMessage(message,exceptionFileDto.getInstrument());
        populateFileAction(message,exceptionFileDto.getFileUpdateActions());
        populateActionDate(message,exceptionFileDto.getLocalDateTime());
        populateFileName(message,"E2");
        populateD127ForExceptionFileUpdate(exceptionFileDto, message);


        return message;
    }

    private void populateD127ForExceptionFileUpdate(ExceptionFileDto exceptionFileDto, Message message) throws Exception {
        String region = exceptionFileDto.getRegion();
        if(region.length() < 9){
            region = new StringBuilder()
                    .append(region)
                    .append(" ".repeat(9 - region.length()))
                    .toString();
        } else if(region.length() > 9){
            region = region.substring(0,9);
        }

        SubElement subElement01 = isoProtoElementEx.getIsoProtoSubElementMap()
                .getSubElement("DE127S001").createNewISOSubElement(getActionCodes(exceptionFileDto.getExceptionActionCodes()));
        SubElement subElement02 = isoProtoElementEx.getIsoProtoSubElementMap()
                .getSubElement("DE127S002").createNewISOSubElement(region);

        List<SubElement> subElementList = new ArrayList<>();
        subElementList.add(subElement01);
        subElementList.add(subElement02);
        SubElementMap subElementMap = isoProtoElementEx.getIsoProtoSubElementMap().createNewSubElementMap(subElementList);
        Element element = isoProtoElementEx.createNewISOElement(subElementMap);
        BasicElement basicElement = isoProtoElementEx.createBasicElement(element);

        Element element127 = isoMessageFactory.getIsoProtoElementMap().get(127)
                                .createNewISOElement(basicElement.getElementValue());

        message.getBitMap().set(126);
        message.getDataElements().put(127,element127);
    }

    private void populateFileName(Message message, String fileName){
        Element element = isoMessageFactory.getIsoProtoElementMap().get(101).createNewISOElement(fileName);
        message.getBitMap().set(100);
        message.getDataElements().put(101,element);
    }

    private void populateFileAction(Message message,FileUpdateActions fileUpdateActions){
        Element element = isoMessageFactory.getIsoProtoElementMap().get(91).createNewISOElement(evaluateFileUpdateActions(fileUpdateActions));
        message.getBitMap().set(90);
        message.getDataElements().put(91,element);

    }

    private void populateActionDate(Message message,LocalDateTime localDateTime){

        String dateString = localDateTime.format(DateTimeFormatter.ofPattern("YYMMdd"));
        Element element73 = isoMessageFactory.getIsoProtoElementMap().get(73).createNewISOElement(dateString);
        message.getDataElements().put(73,element73);
        message.getBitMap().set(72);

    }
    private void populateInstrumentInMessage(Message message, String instrument){

        Element element02 = isoMessageFactory.getIsoProtoElementMap().get(2).createNewISOElement(instrument);
        message.getBitMap().set(1);
        message.getDataElements().put(2,element02);

    }

    @Override
    public Message createFileMaintenanceMessage(CardHolderSchemeDto cardHolderSchemeDto, NetworkProperties networkProperties) {
        return null;
    }

    @Override
    public Message createPaymentOrderMessage(StopPaymentDto stopPaymentDto, NetworkProperties networkProperties) {
        return null;
    }

    @Override
    public MessageType getMessageType(Message message) {

        switch (message.getMessageTypeIdentifier()){

            case 120: return MessageType.AUTH_ADVICE;
            case 300: return MessageType.UPDATE_REQUEST_ACQUIRER;
            case 302: return MessageType.UPDATE_REQUEST_ISSUER;
            case 400: return MessageType.REVERSAL_REQUEST;
            case 420: return MessageType.REVERSAL_ADVICE;
            case 600: return MessageType.TOKEN_REQUEST;
            case 800: return MessageType.NETWORK_MESSAGE;
            case 312: return MessageType.UPDATE_RESPONSE_ISSUER;
            case 130: return MessageType.AUTH_ADVICE_RESPONSE;
            case 310: return MessageType.UPDATE_RESPONSE_ACQUIRER;
            case 410: return MessageType.REVERSAL_RESPONSE;
            case 430: return MessageType.REVERSAL_ADVICE_RESPONSE;
            case 100: return MessageType.AUTH_REQUEST;
            case 110: return MessageType.AUTH_RESPONSE;
            case 620: return MessageType.TOKEN_ADVICE_REQUEST;
            case 630: return MessageType.TOKEN_ADVICE_RESPONSE;
            case 610: return MessageType.TOKEN_RESPONSE;
            case 810: return MessageType.NETWORK_MESSAGE_RESPONSE;
            case 820: return MessageType.NETWORK_ADVICE;
            case 830: return MessageType.NETWORK_ADVICE_RESPONSE;
            default:{
                throw  new RuntimeException("MTI not supported");
            }
        }
    }

    @Override
    public boolean isMessageRequest(NonAuthMessage nonAuthMessage) {
        switch (nonAuthMessage.getMessage().getMessageTypeIdentifier()){
            case 120:
            case 400:
            case 420:
            case 800:
            case 600:
            case 620:
            case 820:
            case 302:
            case 100:
                return true;
            default:
                return false;
        }
    }

    private String evaluateFileUpdateActions(FileUpdateActions fileUpdateActions){

        switch (fileUpdateActions){
            case ADD:
                return "1";
            case CHANGE:
                return "2";
            case DELETE:
                return "3";
            case REPLACE:
                return "4";
            default:
                return "5";
        }
    }

    private String getActionCodes(ExceptionActionCodes exceptionActionCodes){
        switch (exceptionActionCodes){
            case REFER_TO_ISSUER:
                return "01";
            case PICK_UP:
                return "04";
            case DO_NOT_HONOUR:
                return "05";
            case PICK_UP_SPECIAL:
                return "07";
            case VIP_APPROVAL:
                return "11";
            case CLOSED_ACCOUNT:
                return "14";
            case LOST_CARD:
                return "41";
            case STOLEN_CARD:
                return "43";
            case EXPIRED_CARD:
                return "54";
            case CUSTOM_1:
                return "A1";
            case CUSTOM_2:
                return "A2";
            case CUSTOM_3:
                return "A3";
            case CUSTOM_4:
                return "A4";
            case CUSTOM_5:
                return "A5";
            case CUSTOM_6:
                return "A6";
            case CUSTOM_7:
                return "A7";
            case CUSTOM_8:
                return "A8";
            case CUSTOM_9:
                return "A9";
            case CUSTOM_A:
                return "XA";
            case CUSTOM_B:
                return "XC";
            default:
                return "XD";
        }
    }

    private String createRetrievalReferenceNumber(LocalDateTime localDateTime,Integer traceNumber){

        String date = localDateTime.format(DateTimeFormatter.ofPattern("YDDHH"));
        return new StringBuilder()
                .append(date.substring(3))
                .append("0".repeat(6 - traceNumber.toString().length()))
                .append(traceNumber.toString())
                .toString()
                ;

    }

    private int createTraceNumber(){

        return random.nextInt(1000000);
    }
}
