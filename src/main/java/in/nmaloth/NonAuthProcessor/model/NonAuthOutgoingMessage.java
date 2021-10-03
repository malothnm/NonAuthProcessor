package in.nmaloth.NonAuthProcessor.model;


import com.nithin.iso8583.iso.message.Message;
import in.nmaloth.entity.logs.AuthSnapShot;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NonAuthOutgoingMessage {

    private String messageId;
    private String messageTypeId;
    private String containerId;
    private String channelId;
    private Message message;
    private AuthSnapShot authSnapShot;
    private String originalResponseCode;

}
