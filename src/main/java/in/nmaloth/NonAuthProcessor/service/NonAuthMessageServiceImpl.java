package in.nmaloth.NonAuthProcessor.service;

import com.google.protobuf.ByteString;
import com.nithin.iso8583.iso.elementdef.common.interfaces.IntElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.StringElementLengthValue;
import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.dataService.NetworkMessagesDataService;
import in.nmaloth.NonAuthProcessor.dataService.NetworkPropertyDataService;
import in.nmaloth.NonAuthProcessor.model.NonAuthMessage;
import in.nmaloth.NonAuthProcessor.model.NonAuthOutgoingMessage;
import in.nmaloth.NonAuthProcessor.model.dto.NetworkMessageDto;
import in.nmaloth.NonAuthProcessor.model.proto.IncomingMessageOuterClass;
import in.nmaloth.NonAuthProcessor.model.proto.OutgoingMessageOuterClass;
import in.nmaloth.entity.network.MessageStatus;
import in.nmaloth.entity.network.NetworkMessages;
import in.nmaloth.entity.network.NetworkProperties;
import in.nmaloth.entity.network.SignOnStatus;
import in.nmaloth.payments.constants.network.NetworkAdviceInit;
import in.nmaloth.payments.constants.network.NetworkKeyExchange;
import in.nmaloth.payments.constants.network.NetworkMessageType;
import in.nmaloth.payments.constants.network.NetworkType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class NonAuthMessageServiceImpl implements NonAuthMessageService {

    private final NetworkMessagesDataService networkMessagesDataService;
    private final SchemeNonAuthService schemeNonAuthService;
    private final Map<NetworkType, NetworkProperties> networkPropertiesMap;
    private final NetworkPropertyDataService networkPropertyDataService;
    private final ResponseService responseService;



    public NonAuthMessageServiceImpl(
            NetworkMessagesDataService networkMessagesDataService,
            SchemeNonAuthService schemeNonAuthService,
            Map<NetworkType, NetworkProperties> networkPropertiesMap,
            NetworkPropertyDataService networkPropertyDataService,
            ResponseService responseService) {
        this.networkMessagesDataService = networkMessagesDataService;
        this.schemeNonAuthService = schemeNonAuthService;
        this.networkPropertiesMap = networkPropertiesMap;
        this.networkPropertyDataService = networkPropertyDataService;
        this.responseService = responseService;
    }


    @Override
    public NonAuthMessage createNonAuthMessage(OutgoingMessageOuterClass.OutgoingMessage outgoingMessage) {

        try {
            Message message = schemeNonAuthService.createNonAuthMessage(outgoingMessage.getMessage().toByteArray());
            return NonAuthMessage.builder()
                    .messageId(outgoingMessage.getMessageId())
                    .messageTypeId(outgoingMessage.getMessageTypeId())
                    .channelId(outgoingMessage.getChannelId())
                    .containerId(outgoingMessage.getContainerId())
                    .message(message)
                    .build()
                    ;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }

    }

    @Override
    public Mono<NonAuthOutgoingMessage> createNetworkMessages(NetworkMessageDto networkMessageDto) {

        NetworkProperties networkProperties = networkPropertiesMap.get(networkMessageDto.getNetworkType());

        try {
            checkForNetworkPropertiesStatus(networkProperties, networkMessageDto);
        } catch (Exception ex) {
            return Mono.error(new RuntimeException(ex.getMessage()));
        }

        return createMessage(networkMessageDto, networkProperties)
                .map(message -> NonAuthOutgoingMessage.builder()
                        .messageId(UUID.randomUUID().toString())
                        .messageTypeId("0800")
                        .message(message)
                        .build()

                )
                ;

    }

    @Override
    public Mono<Message> processNetworkMessageRequests(Message message) {

        NetworkMessageType networkMessageType = schemeNonAuthService.identifyNetworkMessageType(message);
        if (networkMessageType.equals(NetworkMessageType.GROUP_SIGN_OFF) || networkMessageType.equals(NetworkMessageType.GROUP_SIGN_OFF)) {
            log.info(" ++++ Network Message Request Processed for {}", findMessageString(networkMessageType));

            return updateNetworkProperties(networkMessageType)

                    .map(networkProperties -> responseService.createNetworkMessageResponse(message, "00"))
                    .onErrorReturn(responseService.createNetworkMessageResponse(message, "05"))

                    ;
        }
        log.info(" ++++ Network Message Request Processed for {}", findMessageString(networkMessageType));
        return Mono.just(responseService.createNetworkMessageResponse(message, "00"));


    }

    @Override
    public Mono<NonAuthOutgoingMessage> processNetworkMessageRequests(NonAuthMessage nonAuthMessage) {
        return processNetworkMessageRequests(nonAuthMessage.getMessage())
                .map(message -> NonAuthOutgoingMessage.builder()
                        .messageId(nonAuthMessage.getMessageId())
                        .containerId(nonAuthMessage.getContainerId())
                        .channelId(nonAuthMessage.getChannelId())
                        .messageTypeId(nonAuthMessage.getMessageTypeId())
                        .message(message)
                        .build()

                )

                ;
    }

    private String findMessageString(NetworkMessageType networkMessageType) {

        switch (networkMessageType) {
            case SIGN_OFF: {
                return "Sign Off Message";
            }
            case ECHO: {
                return "Echo Message";
            }
            case GROUP_SIGN_OFF: {
                return "Group Sign Off Message";
            }
            case SIGN_ON: {
                return "Sign On Message";
            }
            case GROUP_SIGN_ON: {
                return "Group Sign On Message";
            }
            default: {
                return "Unidentified Network";
            }
        }
    }

    @Override
    public Mono<Void> processNetworkResponse(Message message) {
        NetworkMessageType networkMessageType = schemeNonAuthService.identifyNetworkMessageType(message);

        String responseCode = "NA";
        if(message.getBitMap().get(38)){
            responseCode = ((StringElementLengthValue)message.getDataElements().get(39).getElementLengthValue()).getValue();
        }

        if (responseCode.equalsIgnoreCase("00")) {

            if (networkMessageType.equals(NetworkMessageType.SIGN_ON) || networkMessageType.equals(NetworkMessageType.SIGN_OFF)) {

                log.info(" ++++ Network Message Response Processing for {}", findMessageString(networkMessageType));

                return networkPropertyDataService.fetchNetworkProperty(schemeNonAuthService.findNetworkType())
                        .map(networkPropertiesOptional -> {
                            if (networkPropertiesOptional.isEmpty()) {
                                throw new RuntimeException("Invalid NetworkType");
                            }
                            return networkPropertiesOptional.get();
                        })
                        .map(networkProperties -> {
                            if (networkMessageType.equals(NetworkMessageType.SIGN_ON)) {
                                networkProperties.setSignOnStatus(SignOnStatus.SIGN_ON);
                            } else {
                                networkProperties.setSignOnStatus(SignOnStatus.SIGN_OFF);
                            }
                            return networkProperties;
                        })
                        .flatMap(networkProperties -> networkPropertyDataService.updateNetworkProperties(networkProperties))
                        .then()


                        ;
            }

            log.info(" ++++ Network Message Response Processing for {} with response Code 00", findMessageString(networkMessageType));
        }

        log.info(" ++++ Network Message Response Processing for {} with response Code {}", findMessageString(networkMessageType),responseCode);
        return Mono.empty().then();
    }

    @Override
    public Mono<Void> processFileUpdateResponse(Message message) {
        return Mono.empty().then();
    }

    @Override
    public Mono<NonAuthOutgoingMessage> processReversals(NonAuthMessage nonAuthMessage) {

        return responseService.processReversalResponse(nonAuthMessage);
    }

    @Override
    public Mono<NonAuthOutgoingMessage> processAdvices(NonAuthMessage nonAuthMessage) {

        String responseCode = "  ";
        if(nonAuthMessage.getMessage().getBitMap().get(39)){
            responseCode = ((StringElementLengthValue)(nonAuthMessage.getMessage().getDataElements().get(39).getElementLengthValue())).getValue();
        }

        String finalResponseCode = responseCode;
        return responseService.processForAdviceResponse(nonAuthMessage.getMessage())
                .map(responseMessage -> NonAuthOutgoingMessage.builder()
                        .message(responseMessage)
                        .messageTypeId(nonAuthMessage.getMessageTypeId())
                        .messageId(nonAuthMessage.getMessageId())
                        .containerId(nonAuthMessage.getContainerId())
                        .channelId(nonAuthMessage.getChannelId())
                        .originalResponseCode(finalResponseCode)
                        .build())
                ;


    }

    @Override
    public Mono<NonAuthOutgoingMessage> processAdminMessages(NonAuthMessage nonAuthMessage) {

        return responseService.processForAdminResponse(nonAuthMessage.getMessage())
                .map(responseMessage -> NonAuthOutgoingMessage.builder()
                        .message(responseMessage)
                        .messageTypeId(nonAuthMessage.getMessageTypeId())
                        .messageId(nonAuthMessage.getMessageId())
                        .containerId(nonAuthMessage.getContainerId())
                        .channelId(nonAuthMessage.getChannelId())
                        .build())
                ;
    }

    @Override
    public boolean isMessageRequest(NonAuthMessage nonAuthMessage) {

       return schemeNonAuthService.isMessageRequest(nonAuthMessage);
    }

    @Override
    public Mono<NonAuthOutgoingMessage> processRequests(NonAuthMessage nonAuthMessage) {
        switch (schemeNonAuthService.getMessageType(nonAuthMessage.getMessage())){
            case AUTH_ADVICE:
            case FINANCIAL_ADVICE:
            case REVERSAL_ADVICE:
            case TOKEN_ADVICE_REQUEST:{
                return processAdvices(nonAuthMessage);
            }
            case REVERSAL_REQUEST:{
                return processReversals(nonAuthMessage);
            }
            case NETWORK_MESSAGE:
            case NETWORK_ADVICE:{
                return processNetworkMessageRequests(nonAuthMessage);
            }
            case ADMIN_MESSAGES:
            case TOKEN_REQUEST:{
                return processAdminMessages(nonAuthMessage);
            }
            default:{
                throw new RuntimeException("Invalid Message Type for Requests");
            }
        }
    }



    @Override
    public Mono<Void> processResponse(NonAuthMessage nonAuthMessage) {

        switch (schemeNonAuthService.getMessageType(nonAuthMessage.getMessage())){
            case NETWORK_ADVICE_RESPONSE:
            case NETWORK_MESSAGE_RESPONSE:
            {
                return processNetworkResponse(nonAuthMessage.getMessage());
            }
            case UPDATE_REQUEST_ISSUER:{
                return processFileUpdateResponse(nonAuthMessage.getMessage());
            }
            default:{
                throw new RuntimeException("Invalid Incoming message Type " + nonAuthMessage.getMessageId());
            }
        }
    }

    @Override
    public IncomingMessageOuterClass.IncomingMessage createIncomingMessage(NonAuthOutgoingMessage nonAuthOutgoingMessage)  {

        try {

            byte[] messageBytes = responseService.convertMessageToBytes(nonAuthOutgoingMessage.getMessage());
            IncomingMessageOuterClass.IncomingMessage.Builder builder = IncomingMessageOuterClass.IncomingMessage.newBuilder()
                    .setMessage(ByteString.copyFrom(messageBytes))
                    .setMessageId(nonAuthOutgoingMessage.getMessageId())
                    .setMessageTypeId(nonAuthOutgoingMessage.getMessageTypeId());

            if(nonAuthOutgoingMessage.getContainerId() != null){
                builder.setContainerId(nonAuthOutgoingMessage.getContainerId());
            }
            if(nonAuthOutgoingMessage.getChannelId() != null){
                builder.setChannelId(nonAuthOutgoingMessage.getChannelId());
            }

            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Compression Error");
        }

    }

    private Mono<NetworkProperties> updateNetworkProperties(NetworkMessageType networkMessageType) {

        return networkPropertyDataService.fetchNetworkProperty(schemeNonAuthService.findNetworkType())
                .map(networkPropertiesOptional -> {
                    if (networkPropertiesOptional.isEmpty()) {
                        log.error("++++ Invalid Network Type. Rejecting Message for {}", schemeNonAuthService.findNetwork());
                        throw new RuntimeException("Invalid NetworkType");
                    }
                    return networkPropertiesOptional.get();
                })
                .map(networkProperties -> {
                    if (networkMessageType.equals(NetworkMessageType.SIGN_ON)) {
                        networkProperties.setSignOnStatus(SignOnStatus.SIGN_ON);
                    } else {
                        networkProperties.setSignOnStatus(SignOnStatus.SIGN_OFF);
                    }
                    return networkProperties;
                })
                .flatMap(networkProperties -> networkPropertyDataService.updateNetworkProperties(networkProperties));


    }

    private Mono<Message> createMessage(NetworkMessageDto networkMessageDto,
                                        NetworkProperties networkProperties) {

        String time = networkMessageDto.getLocalDateTime().format(DateTimeFormatter.ofPattern("MMddHHmmss"));
        String messageType = identifyMessageType(networkMessageDto.getNetworkMessageType(),
                networkMessageDto.getNetworkAdviceInit(), networkMessageDto.getNetworkKeyExchange());

        Message message = schemeNonAuthService.convertToMessage(networkMessageDto, networkProperties);
        Integer traceNumber = ((IntElementLengthValue) message.getDataElements().get(11).getElementLengthValue()).getValue();

        NetworkMessages networkMessages = NetworkMessages.builder()
                .incomingMessageId(networkMessageDto.getMessageId())
                .messageStatus(MessageStatus.SEND)
                .messageId(new StringBuilder()
                        .append(messageType)
                        .append(traceNumber.toString())
                        .append(time).toString())
                .build();

        return networkMessagesDataService
                .updateNetworkMessages(networkMessages)
                .map(networkMessages1 -> message)
                ;
    }

    private String identifyMessageType(NetworkMessageType networkMessageType,
                                       NetworkAdviceInit networkAdviceInit,
                                       NetworkKeyExchange networkKeyExchange) {

        switch (networkMessageType) {
            case SIGN_ON:
            case GROUP_SIGN_ON: {
                return "SIGN_ON";
            }
            case SIGN_OFF:
            case GROUP_SIGN_OFF: {
                return "SIGN_OFF";
            }
            case ECHO: {
                return "ECHO";
            }
            default: {
                if (networkAdviceInit.equals(NetworkAdviceInit.START_ADVICE)) {
                    return "ADVICE_START";
                } else if (networkAdviceInit.equals(NetworkAdviceInit.STOP_ADVICE)) {
                    return "ADVICE_STOP";
                } else if (!networkKeyExchange.equals(NetworkKeyExchange.NO_KEY_EXCHANGE)) {
                    return "KEY_EXCHANGE";
                } else {
                    return "NO_MESSAGE";
                }

            }
        }

    }

    private NetworkProperties checkForNetworkPropertiesStatus(NetworkProperties networkProperties,
                                                              NetworkMessageDto networkMessageDto) {

        if (networkProperties == null) {
            throw new RuntimeException("Invalid Network Type");
        }

        switch (networkMessageDto.getNetworkMessageType()) {
            case ECHO:
            case NO_MESSAGE: {
                if (networkProperties.getSignOnStatus().equals(SignOnStatus.SIGN_OFF)) {
                    throw new RuntimeException("Not yet Signed On.. Cannot Send Message");
                }
                break;
            }
        }

        return networkProperties;

    }

}
