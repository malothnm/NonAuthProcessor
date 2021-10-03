package in.nmaloth.NonAuthProcessor.service;

import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.model.NonAuthMessage;
import in.nmaloth.NonAuthProcessor.model.NonAuthOutgoingMessage;
import in.nmaloth.NonAuthProcessor.model.dto.NetworkMessageDto;
import in.nmaloth.NonAuthProcessor.model.proto.IncomingMessageOuterClass;
import in.nmaloth.NonAuthProcessor.model.proto.OutgoingMessageOuterClass;
import reactor.core.publisher.Mono;

public interface NonAuthMessageService {

    NonAuthMessage createNonAuthMessage(OutgoingMessageOuterClass.OutgoingMessage outgoingMessage);
    Mono<NonAuthOutgoingMessage> createNetworkMessages(NetworkMessageDto networkMessageDto);
    Mono<Message> processNetworkMessageRequests(Message message);
    Mono<NonAuthOutgoingMessage> processNetworkMessageRequests(NonAuthMessage nonAuthMessage);
    Mono<Void> processNetworkResponse(Message message);
    Mono<Void> processFileUpdateResponse(Message message);
    Mono<NonAuthOutgoingMessage> processReversals(NonAuthMessage nonAuthMessage);
    Mono<NonAuthOutgoingMessage> processAdvices(NonAuthMessage nonAuthMessage);
    Mono<NonAuthOutgoingMessage> processAdminMessages(NonAuthMessage nonAuthMessage);
    boolean isMessageRequest(NonAuthMessage nonAuthMessage);
    Mono<NonAuthOutgoingMessage> processRequests(NonAuthMessage nonAuthMessage);
    Mono<Void> processResponse(NonAuthMessage nonAuthMessage);
    IncomingMessageOuterClass.IncomingMessage createIncomingMessage(NonAuthOutgoingMessage nonAuthOutgoingMessage);

}
