package in.nmaloth.NonAuthProcessor.service;

import com.nithin.iso8583.iso.elementdef.common.interfaces.IntElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.LongElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.StringElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.SubElementLengthValue;
import com.nithin.iso8583.iso.elementdef.element.Element;
import com.nithin.iso8583.iso.elementdef.element.ISOProtoElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.ISOProtoSubElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElementMap;
import com.nithin.iso8583.iso.message.Header.Header;
import com.nithin.iso8583.iso.message.Header.visa.VisaHeader;
import com.nithin.iso8583.iso.message.Header.visa.VisaHeaderComplete;
import com.nithin.iso8583.iso.message.Header.visa.VisaHeaderCompleteImpl;
import com.nithin.iso8583.iso.message.Header.visa.VisaProtoHeader;
import com.nithin.iso8583.iso.message.ISOMessageFactory;
import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.dataService.AuthSnapShotDataService;
import in.nmaloth.NonAuthProcessor.model.NonAuthOutgoingMessage;
import in.nmaloth.NonAuthProcessor.response.constants.ResponseTypes;
import in.nmaloth.entity.logs.AuthSnapShot;
import in.nmaloth.entity.logs.ReversalStatus;
import in.nmaloth.entity.logs.SnapshotKey;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class VisaResponseService implements SchemeResponseService{

    private  final ISOMessageFactory isoMessageFactory;
    private  final AuthSnapShotDataService authSnapShotDataService;



    private static final String TRACK_DELIMITER_2 = "D";
    private static final String TRACK_DELIMITER_1 = "\\^";


    public VisaResponseService(ISOMessageFactory isoMessageFactory,
                               AuthSnapShotDataService authSnapShotDataService) {
        this.isoMessageFactory = isoMessageFactory;
        this.authSnapShotDataService = authSnapShotDataService;
    }


    @Override
    public ResponseTypes identifyResponseTypes(Message message) {
        switch (message.getMessageTypeIdentifier()){
            case 800:{
                return ResponseTypes.NETWORK;
            }
            case 120: {
                return ResponseTypes.ADVICES;
            }
            case 400: {
                return ResponseTypes.REVERSAL;
            }
            case 420: {
                return ResponseTypes.REVERSAL_ADVICE;
            }
            case 620:
            case 600: {
                return ResponseTypes.TOKEN;
            }

        }
        throw  new RuntimeException("Invalid Response Types");
    }

    @Override
    public int getResponseMti(int messageTypeIdentifier) {

        switch (messageTypeIdentifier){
            case 120:
                return 130;
            case 400:
                return 410;
            case 420:
                return 430;
            case 600:
                return 610;
            case 620:
                return 630;
            case 800:
                return 810;
            case 820:
                return 830;
            case 100:
            case 101:
                return 110;
        }
        return 0;
    }

    @Override
    public Message createNetworkMessageResponseCode(Message message, String responseCode) {

        message.getBitMap().set(38);
        Element element39 = isoMessageFactory.getIsoProtoElementMap().get(39).createNewISOElement(responseCode);
        message.getDataElements().put(39,element39);
        return message;    }

    @Override
    public Header getResponseHeader(Message message) {

        VisaHeader visaHeader = ((VisaHeaderComplete)message.getHeader()).getHeader();
        VisaHeaderComplete visaHeaderComplete = new VisaHeaderCompleteImpl();
        visaHeaderComplete.setHeader(visaHeader.createResponseHeader());
        return visaHeaderComplete;
    }

    @Override
    public Element populateCustomFieldLogic(ISOProtoElement isoProtoElement, int dataElement, Message message) {
        return null;
    }

    @Override
    public SubElement populateCustomSubElementFieldLogic(ISOProtoSubElement isoProtoSubElement, Message message, int dataElement) {
        return null;
    }

    @Override
    public Mono<Optional<AuthSnapShot>> getOriginalAuthSnapShot(Message message) {

        String instrument = null;
        if(message.getBitMap().get(1)){
            instrument = ((StringElementLengthValue)message.getDataElements().get(2).getElementLengthValue())
                    .getValue();
        } else if(message.getBitMap().get(34)){
            instrument = getInstrumentTrack2(((StringElementLengthValue)message.getDataElements().get(35).getElementLengthValue())
                    .getValue());
        } else if(message.getBitMap().get(44)){
            instrument = getInstrumentTrack1(((StringElementLengthValue)message.getDataElements().get(45).getElementLengthValue())
                    .getValue());
        }

        if(instrument == null){
            return Mono.just(Optional.empty());
        }
        String keyExt = null;
        if(message.getBitMap().get(89)){
            SubElementMap subElementMap = ((SubElementLengthValue)message.getDataElements().get(90).getElementLengthValue()).getValue();
            Integer originalMti = ((IntElementLengthValue)subElementMap.getSubElementMap().get("DE090S001").getElementLengthValue()).getValue();
            Integer originalTraceNumber = ((IntElementLengthValue)subElementMap.getSubElementMap().get("DE090S002").getElementLengthValue()).getValue();
            String originalDE007 = ((StringElementLengthValue)subElementMap.getSubElementMap().get("DE090S003").getElementLengthValue()).getValue();

            keyExt = new StringBuilder()
                    .append(originalMti.toString())
                    .append(originalTraceNumber.toString())
                    .append(originalDE007)
                    .toString();
        } else {
            return Mono.just(Optional.empty());
        }

        SnapshotKey snapshotKey = SnapshotKey.builder()
                .instrument(instrument)
                .keyExtension(keyExt)
                .build();

        return authSnapShotDataService.findSnapShot(snapshotKey);
    }

    @Override
    public NonAuthOutgoingMessage updateResponseFieldsReversals(Message message, Optional<AuthSnapShot> authSnapShotOptional) {

        NonAuthOutgoingMessage.NonAuthOutgoingMessageBuilder builder = NonAuthOutgoingMessage
                .builder()
                .message(message)
                ;


        if(authSnapShotOptional.isEmpty()){
            populateResponseCode(message,"05");
            return builder.build();
        }

        AuthSnapShot authSnapShot = authSnapShotOptional.get();
        if(authSnapShot.getReversalStatus().equals(ReversalStatus.REVERSED)){
            populateResponseCode(message,"05");
            return builder.build();
        }

        if(!authSnapShot.getResponseCode().equals("00")){
            populateResponseCode(message,"05");
            return builder.build();

        }

        long transactionAmount = ((LongElementLengthValue)message.getDataElements().get(4).getElementLengthValue()).getValue();

        if(transactionAmount > authSnapShot.getTransactionAmount()){
            populateResponseCode(message,"05");
            return builder.build();
        }

        if(authSnapShot.getBillingAmount() > 0  && authSnapShot.getTransactionAmount() > 0){
            populateBillingFields(message,authSnapShot);
        }
        populateResponseCode(message,"00");
        builder.authSnapShot(authSnapShot)

        ;



        return builder.build();
    }

    @Override
    public Message updateAdviceMessages(Message message) {

        if(message.getBitMap().get(38)){
            Element element = message.getDataElements().get(39);
            ((StringElementLengthValue)element.getElementLengthValue()).setValue("00");
        } else {
            populateResponseCode(message,"00");
        }

        return message;
    }

    @Override
    public Message updateAdminMessages(Message message) {
         populateResponseCode(message,"00");
         return message;
    }

    @Override
    public void updateHeader(byte[] messageBytes) {

        ((VisaProtoHeader)isoMessageFactory.getProtoHeader()).updateHeaderLength(messageBytes);
    }

    private void populateBillingFields(Message message, AuthSnapShot authSnapShot) {

        long transactionAmount = ((LongElementLengthValue)message.getDataElements().get(4).getElementLengthValue()).getValue();

        long reverseBillAmount = 0;
        double conversionRate = (double) authSnapShot.getBillingAmount()/authSnapShot.getTransactionAmount();
        if(transactionAmount == authSnapShot.getTransactionAmount()){
            reverseBillAmount = authSnapShot.getBillingAmount();
        } else {
            reverseBillAmount = (long) (transactionAmount * conversionRate);
        }
        String doubleString = Double.toString(conversionRate);
        int indexOfDecimal = doubleString.indexOf(".");
        Integer nod = 7 - indexOfDecimal;
        String integerPart = doubleString.substring(0,indexOfDecimal);
        String decimalPart = doubleString.substring(indexOfDecimal + 1);

        String conversionRateString = new StringBuilder()
                .append(nod.toString())
                .append(integerPart)
                .append(decimalPart)
                .toString();

        if(conversionRateString.length() > 8 ){
            conversionRateString = conversionRateString.substring(0,8);
        } else if(conversionRateString.length() < 8){
            conversionRateString = new StringBuilder()
                    .append(conversionRateString)
                    .append("0".repeat(8 - conversionRateString.length()))
                    .toString();

        }
        Element element06 = isoMessageFactory.getIsoProtoElementMap().get(6).createNewISOElement(reverseBillAmount);
        Element element51 = isoMessageFactory.getIsoProtoElementMap().get(51).createNewISOElement(Integer.parseInt(authSnapShot.getBillingCurrencyCode()));
        Element element10 = isoMessageFactory.getIsoProtoElementMap().get(10).createNewISOElement(conversionRateString);
        message.getBitMap().set(5);
        message.getBitMap().set(9);
        message.getBitMap().set(50);
        message.getDataElements().put(6,element06);
        message.getDataElements().put(10,element10);
        message.getDataElements().put(51,element51);

    }

    private void populateResponseCode(Message message, String responseCode) {
        Element element = isoMessageFactory.getIsoProtoElementMap().get(39).createNewISOElement(responseCode);
        message.getBitMap().set(38);
        message.getDataElements().put(39,element);
    }


    private String getInstrumentTrack1(String trackData){

        String delimiter = TRACK_DELIMITER_1;
        if(trackData.contains(TRACK_DELIMITER_2)){
            delimiter = TRACK_DELIMITER_2;
        }
        return trackData.substring(1, trackData.length()).split(TRACK_DELIMITER_1)[0];

    }
    private String getInstrumentTrack2(String trackData){

        String delimiter = TRACK_DELIMITER_1;
        if(trackData.contains(TRACK_DELIMITER_2)){
            delimiter = TRACK_DELIMITER_2;
        }

        return trackData.split(delimiter)[0];


    }
}
