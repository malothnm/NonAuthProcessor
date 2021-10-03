package in.nmaloth.NonAuthProcessor.service;


import com.nithin.iso8583.iso.elementdef.element.Element;
import com.nithin.iso8583.iso.elementdef.element.ISOProtoElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.ISOProtoSubElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElement;
import com.nithin.iso8583.iso.message.Header.Header;
import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.NonAuthProcessor.model.NonAuthMessage;
import in.nmaloth.NonAuthProcessor.model.NonAuthOutgoingMessage;
import in.nmaloth.NonAuthProcessor.response.constants.ResponseTypes;
import in.nmaloth.entity.logs.AuthSnapShot;
import reactor.core.publisher.Mono;

import java.util.Optional;


public interface SchemeResponseService {


    ResponseTypes identifyResponseTypes(Message message);

    int getResponseMti(int messageTypeIdentifier);

    Message createNetworkMessageResponseCode(Message message,String responseCode);

    Header getResponseHeader(Message message);

    Element populateCustomFieldLogic(ISOProtoElement isoProtoElement, int dataElement, Message message);

    SubElement populateCustomSubElementFieldLogic(ISOProtoSubElement isoProtoSubElement, Message message, int dataElement);

    Mono<Optional<AuthSnapShot>> getOriginalAuthSnapShot(Message message);

    NonAuthOutgoingMessage updateResponseFieldsReversals(Message message, Optional<AuthSnapShot> authSnapShotOptional);
    Message updateAdviceMessages(Message message);
    Message updateAdminMessages(Message message);


    void updateHeader(byte[] messageBytes);
}
