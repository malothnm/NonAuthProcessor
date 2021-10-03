package in.nmaloth.NonAuthProcessor.service;

import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.model.NonAuthMessage;
import in.nmaloth.NonAuthProcessor.model.NonAuthOutgoingMessage;
import reactor.core.publisher.Mono;

public interface ResponseService {


    Message populateResponseFieldsMessage(Message message);
    Message createNetworkMessageResponse(Message message, String responseCode);
    Mono<NonAuthOutgoingMessage> processReversalResponse(NonAuthMessage nonAuthMessage);
    Mono<Message> processForAdviceResponse(Message message);
    Mono<Message> processForAdminResponse(Message message);
    byte[] convertMessageToBytes(Message message) throws Exception;


}
