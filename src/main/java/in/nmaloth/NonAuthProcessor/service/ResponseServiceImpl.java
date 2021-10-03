package in.nmaloth.NonAuthProcessor.service;

import com.google.common.io.BaseEncoding;
import com.nithin.iso8583.iso.elementdef.common.constants.ElementType;
import com.nithin.iso8583.iso.elementdef.common.interfaces.SubElementLengthValue;
import com.nithin.iso8583.iso.elementdef.element.Element;
import com.nithin.iso8583.iso.elementdef.element.ISOProtoElement;
import com.nithin.iso8583.iso.elementdef.subelement.SubElementType;
import com.nithin.iso8583.iso.elementdef.subelement.base.ISOProtoSubElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.ISOProtoSubElementMap;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElementMap;
import com.nithin.iso8583.iso.elementdef.subelement.dataset.ISOProtoSubElementDataSet;
import com.nithin.iso8583.iso.elementdef.subelement.dataset.ISOProtoSubElementDataSetMap;
import com.nithin.iso8583.iso.elementdef.subelement.dataset.SubElementDataSet;
import com.nithin.iso8583.iso.elementdef.util.Utils;
import com.nithin.iso8583.iso.message.BasicElementMessage;
import com.nithin.iso8583.iso.message.ISOMessageFactory;
import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.constants.BeanConfigNames;
import in.nmaloth.NonAuthProcessor.model.InitializeFixedSubElements;
import in.nmaloth.NonAuthProcessor.model.InitializeSubElements;
import in.nmaloth.NonAuthProcessor.model.NonAuthMessage;
import in.nmaloth.NonAuthProcessor.model.NonAuthOutgoingMessage;
import in.nmaloth.NonAuthProcessor.response.constants.ResponseFieldLogicType;
import in.nmaloth.NonAuthProcessor.response.constants.ResponseTypes;
import in.nmaloth.NonAuthProcessor.response.model.DataSetField;
import in.nmaloth.NonAuthProcessor.response.model.ResponseField;
import in.nmaloth.NonAuthProcessor.response.model.ResponseSummary;
import in.nmaloth.NonAuthProcessor.response.model.SubElementField;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class ResponseServiceImpl implements ResponseService {


    private final ISOMessageFactory isoMessageFactory;
    private final List<InitializeFixedSubElements> initializeElementsList;
    private final Map<ResponseTypes, ResponseSummary> responseSummaryMap;


    private final SchemeResponseService schemeResponseService;


    public ResponseServiceImpl(ISOMessageFactory isoMessageFactory,
                               @Qualifier(BeanConfigNames.RESPONSE_MAP) Map<ResponseTypes, ResponseSummary> responseSummaryMap,
                               List<InitializeFixedSubElements> initializeElementsList,
                               SchemeResponseService schemeResponseService) {

        this.isoMessageFactory = isoMessageFactory;
        this.responseSummaryMap = responseSummaryMap;
        this.initializeElementsList = initializeElementsList;

        this.schemeResponseService = schemeResponseService;
    }


    @Override
    public Message populateResponseFieldsMessage(Message message) {


        ResponseTypes responseTypes = schemeResponseService.identifyResponseTypes(message);

        try {
            return populateResponseElements(responseTypes, message);
        } catch (Exception e) {
            e.printStackTrace();
            return message;
        }

    }

    @Override
    public Message createNetworkMessageResponse(Message message, String responseCode) {

        Message responseMessage = populateResponseFieldsMessage(message);
        return schemeResponseService.createNetworkMessageResponseCode(responseMessage, responseCode);
    }

    @Override
    public Mono<NonAuthOutgoingMessage> processReversalResponse(NonAuthMessage nonAuthMessage)
    {
        Message responseMessage = populateResponseFieldsMessage(nonAuthMessage.getMessage());
        return schemeResponseService.getOriginalAuthSnapShot(responseMessage)
                .map(authSnapShotOptional -> schemeResponseService.updateResponseFieldsReversals(nonAuthMessage.getMessage(),authSnapShotOptional))
                .map(nonAuthOutgoingMessage -> {

                    nonAuthOutgoingMessage.setMessageId(nonAuthMessage.getMessageId());
                    nonAuthOutgoingMessage.setChannelId(nonAuthMessage.getChannelId());
                    nonAuthOutgoingMessage.setContainerId(nonAuthMessage.getContainerId());
                    nonAuthOutgoingMessage.setMessageTypeId(nonAuthMessage.getMessageTypeId());
                    return nonAuthOutgoingMessage;
                });
    }

    @Override
    public Mono<Message> processForAdviceResponse(Message message) {
        Message responseMessage = populateResponseFieldsMessage(message);
        return Mono.just(schemeResponseService.updateAdviceMessages(responseMessage));
    }

    @Override
    public Mono<Message> processForAdminResponse(Message message) {
        Message responseMessage = populateResponseFieldsMessage(message);
        return Mono.just(schemeResponseService.updateAdminMessages(responseMessage));
    }

    @Override
    public byte[] convertMessageToBytes(Message message) throws Exception {
        BasicElementMessage basicElementMessage = isoMessageFactory.compressMessages(message);
        byte[] messageBytes = isoMessageFactory.compressBasicMessages(basicElementMessage);
        schemeResponseService.updateHeader(messageBytes);
        return messageBytes;
    }

    private void initializeDataElement(Element element, InitializeFixedSubElements initializeFixedSubElements, ISOProtoElement isoProtoElement) {

        if (isoProtoElement.getIsoProtoSubElementMap().getSubElementType().equals(SubElementType.FIXED)) {
            if (isoProtoElement.getElementType().equals(ElementType.VARIABLE)) {
                initializeMissingElementsVariable(isoProtoElement, initializeFixedSubElements, element);
            } else {
                initializeMissingElementFixed(isoProtoElement, initializeFixedSubElements, element);
            }
        }
    }

    private void initializeMissingElementFixed(ISOProtoElement isoProtoElement, InitializeFixedSubElements initializeFixedSubElements, Element element) {

        isoProtoElement.getIsoProtoSubElementMap()
                .getAllSubElements()
                .forEach(isoProtoSubElement -> {

                    SubElementMap subElementMap = ((SubElementLengthValue) element.getElementLengthValue()).getValue();
                    String subElementId = isoProtoSubElement.getId();
                    SubElement subElement = subElementMap.getSubElement(subElementId);
                    if (subElement == null) {
                        InitializeSubElements initializeSubElement = initializeFixedSubElements.getSubElementsMap().get(subElementId);
                        createNewSubElement(isoProtoSubElement, initializeSubElement, subElementMap);
                    }
                });

    }

    private void initializeMissingElementsVariable(ISOProtoElement isoProtoElement, InitializeFixedSubElements initializeElement, Element element) {

        SubElementMap subElementMap = ((SubElementLengthValue) element.getElementLengthValue()).getValue();

        int maxsubElement = initializeElement.getMaxSubElementNumber();

        Integer dataElement = initializeElement.getDataElement();
        String dataElementString = dataElement.toString();

        String dataElementPart = new StringBuilder()
                .append("DE")
                .append("0".repeat(3 - dataElementString.length()))
                .append(dataElementString)
                .append("S").toString();

        boolean initializeSwitch = false;

        for (int subElementNumber = maxsubElement; subElementNumber > 0; subElementNumber--) {

            String subElementString = Integer.toString(subElementNumber);
            String subElementId = new StringBuilder()
                    .append(dataElementPart)
                    .append("0".repeat(3 - subElementString.length()))
                    .append(subElementString)
                    .toString();

            SubElement subElement = subElementMap.getSubElement(subElementId);
            if (subElement == null) {

                if (initializeSwitch) {
                    ISOProtoSubElement isoProtoSubElement = isoProtoElement.getIsoProtoSubElementMap().getSubElement(subElementId);
                    InitializeSubElements initializeSubElement = initializeElement.getSubElementsMap().get(subElementId);
                    createNewSubElement(isoProtoSubElement, initializeSubElement, subElementMap);
                }
            } else {
                initializeSwitch = true;
            }

        }

    }


    private void createNewSubElement(ISOProtoSubElement isoProtoSubElement, InitializeSubElements initializeSubElement, SubElementMap subElementMap) {

        switch (isoProtoSubElement.getIsoTargetType()) {
            case INT: {
                createIntegerSubElement
                        (isoProtoSubElement, subElementMap, initializeSubElement);
                break;
            }
            case LONG: {
                createLongSubElement
                        (isoProtoSubElement, subElementMap, initializeSubElement);
                break;

            }
            case STRING: {

                createStringSubElement
                        (isoProtoSubElement, subElementMap, initializeSubElement);
                break;
            }
            case UNKNOWN: {

                createBinarySubElement
                        (isoProtoSubElement, subElementMap, initializeSubElement);

                break;
            }
        }
    }

    private void createBinarySubElement(ISOProtoSubElement isoProtoSubElement, SubElementMap subElementMap, InitializeSubElements initializeSubElement) {

        try {
            byte[] byteValue = Utils.stringToHex(initializeSubElement.getValue(), isoProtoSubElement.getMaxLength(), isoProtoSubElement.getPadValue(), isoProtoSubElement.getPaddingType());
            SubElement subElementNew = isoProtoSubElement.createNewISOSubElement(byteValue);
            subElementMap.getSubElementMap().put(subElementNew.getId(), subElementNew);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createStringSubElement(ISOProtoSubElement isoProtoSubElement, SubElementMap subElementMap, InitializeSubElements initializeSubElement) {

        try {
            SubElement subElementNew = isoProtoSubElement.createNewISOSubElement(initializeSubElement.getValue());
            subElementMap.getSubElementMap().put(subElementNew.getId(), subElementNew);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void createLongSubElement(ISOProtoSubElement isoProtoSubElement, SubElementMap subElementMap, InitializeSubElements initializeSubElement) {

        try {
            SubElement subElementNew = isoProtoSubElement.createNewISOSubElement(Long.parseLong(initializeSubElement.getValue()));
            subElementMap.getSubElementMap().put(subElementNew.getId(), subElementNew);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createIntegerSubElement(ISOProtoSubElement isoProtoSubElement, SubElementMap subElementMap, InitializeSubElements initializeSubElement) {

        try {
            SubElement subElementNew = isoProtoSubElement.createNewISOSubElement(Integer.parseInt(initializeSubElement.getValue()));
            subElementMap.getSubElementMap().put(subElementNew.getId(), subElementNew);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Message populateResponseElements(ResponseTypes responseTypes, Message message) throws Exception {

        Message responseMessage = new Message();
        responseMessage.setMessageTypeIdentifier(schemeResponseService.getResponseMti(message.getMessageTypeIdentifier()));
        responseMessage.setHeader(schemeResponseService.getResponseHeader(message));
        responseMessage.setBitMap(new BitSet(128));
        ResponseSummary responseSummary = responseSummaryMap.get(responseTypes);


        for (ResponseField responseField : responseSummary.getResponseFields()) {

            Integer dataElement = responseField.getDataElement();
            if (responseField.getResponseFieldLogicType() != null) {
                if (responseField.getResponseFieldLogicType().equals(ResponseFieldLogicType.REPLAY_FIELD)) {
                    Element element = message.getDataElements().get(dataElement);
                    if(element != null){
                        responseMessage.getBitMap().set(dataElement - 1);
                        responseMessage.getDataElements().put(dataElement, element);
                    }

                } else {
                    schemeResponseService.populateCustomFieldLogic(isoMessageFactory.getIsoProtoElementMap().get(dataElement),
                            dataElement, message);
                }
            } else {
                ISOProtoElement isoProtoElement = isoMessageFactory.getIsoProtoElementMap().get(dataElement);

                switch (isoProtoElement.getIsoTargetType()) {
                    case INT:
                    case STRING:
                    case UNKNOWN:
                    case LONG: {
                        populateResponseField(isoProtoElement, responseField, message, dataElement, responseMessage);
                        break;
                    }
                    case SUB_ELEMENT: {

                        switch (isoProtoElement.getIsoProtoSubElementMap().getSubElementType()) {

                            case FIXED:
                            case TLV:
                            case BITMAP: {

                                List<SubElement> subElementList = new ArrayList<>();
                                Element elementIncoming = message.getDataElements().get(dataElement);
                                if (elementIncoming != null) {
                                    SubElementMap subElementMap = ((SubElementLengthValue) elementIncoming.getElementLengthValue()).getValue();
                                    Optional<SubElementMap> subElementMapOptional = populateResponseFieldSubElement(isoProtoElement.getIsoProtoSubElementMap(), responseField.getSubElements(),
                                            subElementList, dataElement, subElementMap.getSubElementMap(), message);
                                    if (subElementMapOptional.isPresent()) {
                                        responseMessage.getBitMap().set(dataElement - 1);
                                        Element element = isoProtoElement.createNewISOElement(subElementMapOptional.get());
                                        responseMessage.getDataElements().put(dataElement, element);
                                    }

                                }
                                break;

                            }
                            case DATA_SET: {

                                List<SubElement> dataSetSubElementList = new ArrayList<>();

                                Element element = message.getDataElements().get(dataElement);

                                if(element != null){
                                    Optional<SubElementMap> subElementMapOptional = populateResponseFieldDataSet(isoProtoElement.getIsoProtoSubElementMap(), responseField.getDataSets(),
                                            dataSetSubElementList, dataElement, element, message);

                                    if (subElementMapOptional.isPresent()) {
                                        responseMessage.getBitMap().set(dataElement - 1);
                                        element = isoProtoElement.createNewISOElement(subElementMapOptional.get());
                                        responseMessage.getDataElements().put(dataElement, element);
                                    }
                                }


                                break;
                            }
                            default: {
                                break;
                            }
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }


            }
        }

        if (responseMessage.getBitMap().nextSetBit(65) != -1) {
            responseMessage.getBitMap().set(0);
        }
        return responseMessage;
    }

    private Optional<SubElementMap> populateResponseFieldDataSet(ISOProtoSubElementMap isoProtoSubElementMap, DataSetField[] dataSets,
                                                                 List<SubElement> dataSetSubElementList,
                                                                 int dataElement, Element element, Message message) throws Exception {

        SubElementMap subElementMap = ((SubElementLengthValue) element.getElementLengthValue()).getValue();

        for (DataSetField dataSetField : dataSets) {
            List<SubElement> subElementList = new ArrayList<>();
            ISOProtoSubElementDataSetMap isoProtoSubElementDataSetMap = (ISOProtoSubElementDataSetMap) isoProtoSubElementMap;
            ISOProtoSubElementDataSet isoProtoSubElementDataSet = (ISOProtoSubElementDataSet) isoProtoSubElementDataSetMap.getSubElement(dataSetField.getDataSetId());

            SubElement subElement = subElementMap.getSubElement(dataSetField.getDataSetId());
            if(subElement != null ) {

                Optional<SubElementMap> subElementMapOptional = populateResponseFieldSubElement(isoProtoSubElementDataSet.getIsoProtoSubElementTLVMap(), dataSetField.getSubElementFields(),
                        subElementList, dataElement, ((SubElementDataSet)subElement).getSubElementMap().getSubElementMap(), message);

                if (subElementMapOptional.isPresent()) {
                    subElement = isoProtoSubElementDataSet.createNewISOSubElement(subElementMapOptional.get());
                    dataSetSubElementList.add(subElement);
                }
            }

        }

        if (dataSetSubElementList.size() > 0) {
            return Optional.of(isoProtoSubElementMap.createNewSubElementMap(dataSetSubElementList));

        }
        return Optional.empty();
    }


    private Optional<SubElementMap> populateResponseFieldSubElement(ISOProtoSubElementMap isoProtoSubElementMap, SubElementField[] subElements,
                                                                    List<SubElement> subElementList, int dataElement, Map<String, SubElement> subElementMap, Message message) throws Exception {


        for (SubElementField subElementField : subElements) {
            ISOProtoSubElement isoProtoSubElement = isoProtoSubElementMap.getSubElement(subElementField.getSubElement());
            if (isoProtoSubElement != null) {
                SubElement subElement = null;
                switch (subElementField.getResponseFieldLogicType()) {
                    case REPLAY_FIELD: {
                        subElement = subElementMap.get(subElementField.getSubElement());
                        break;
                    }
                    case SERVICE_FIELD: {
                        break;
                    }
                    case CUSTOM_FIELD: {
                        subElement = schemeResponseService.populateCustomSubElementFieldLogic(isoProtoSubElement, message, dataElement);
                    }
                }

                if (subElement != null) {
                    subElementList.add(subElement);
                }
            }
        }
        if (subElementList.size() > 0) {
            return Optional.of(isoProtoSubElementMap.createNewSubElementMap(subElementList));
        }
        return Optional.empty();
    }


    private void populateResponseField(ISOProtoElement isoProtoElement, ResponseField responseField,
                                       Message message, int dataElement, Message responseMessage) {

        Element element = null;
        switch (responseField.getResponseFieldLogicType()) {
            case REPLAY_FIELD: {
                element = message.getDataElements().get(responseField.getDataElement());
                break;
            }
            case SERVICE_FIELD: {
                break;
            }
            case CUSTOM_FIELD: {
                element = schemeResponseService.populateCustomFieldLogic(isoProtoElement, dataElement, message);
                break;
            }
        }

        if (element != null) {
            responseMessage.getBitMap().set(dataElement - 1);
            responseMessage.getDataElements().put(dataElement, element);

        }

    }

    private Element getElement(String responseValue, ISOProtoElement isoProtoElement) {

        switch (isoProtoElement.getIsoTargetType()) {
            case INT:
                return isoProtoElement.createNewISOElement(Integer.parseInt(responseValue));
            case LONG:
                return isoProtoElement.createNewISOElement(Long.parseLong(responseValue));
            case STRING:
                return isoProtoElement.createNewISOElement(responseValue);
            default: {
                byte[] bitResponse = BaseEncoding.base16().decode(responseValue);
                return isoProtoElement.createNewISOElement(bitResponse);
            }
        }

    }

    private SubElement getSubElement(String responseValue, ISOProtoSubElement isoProtoSubElement) throws Exception {

        switch (isoProtoSubElement.getIsoTargetType()) {
            case INT:
                return isoProtoSubElement.createNewISOSubElement(Integer.parseInt(responseValue));
            case LONG:
                return isoProtoSubElement.createNewISOSubElement(Long.parseLong(responseValue));
            case STRING:
                return isoProtoSubElement.createNewISOSubElement(responseValue);
            default: {
                byte[] bitResponse = BaseEncoding.base16().decode(responseValue);
                return isoProtoSubElement.createNewISOSubElement(bitResponse);
            }
        }

    }
}
