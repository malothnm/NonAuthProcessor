package in.nmaloth.NonAuthProcessor.service;

import com.nithin.iso8583.iso.message.Message;
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

public interface SchemeNonAuthService {


    Message convertToMessage(NetworkMessageDto networkMessageDto, NetworkProperties networkProperties);
    Message createNonAuthMessage(byte[] messageBytes) throws Exception;
    NetworkMessageType identifyNetworkMessageType(Message message);
    NetworkAdviceInit identifyAdviceInit(Message message);
    NetworkKeyExchange identifyKeyExchangeMessages(Message message);
    NetworkType findNetworkType();
    String findNetwork();
    Message createExceptionFileUpdateMessage(ExceptionFileDto exceptionFileDto,NetworkProperties networkProperties) throws Exception;
    Message createFileMaintenanceMessage(CardHolderSchemeDto cardHolderSchemeDto, NetworkProperties networkProperties);
    Message createPaymentOrderMessage(StopPaymentDto stopPaymentDto,NetworkProperties networkProperties);
    MessageType getMessageType(Message message);

    boolean isMessageRequest(NonAuthMessage nonAuthMessage);
}
