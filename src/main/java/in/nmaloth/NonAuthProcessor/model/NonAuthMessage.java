package in.nmaloth.NonAuthProcessor.model;

import com.nithin.iso8583.iso.message.Message;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NonAuthMessage {

    private String messageId;
    private String messageTypeId;
    private String containerId;
    private String channelId;
    private Message message;

}
