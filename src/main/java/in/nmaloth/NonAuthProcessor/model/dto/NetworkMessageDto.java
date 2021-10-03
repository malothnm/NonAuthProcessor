package in.nmaloth.NonAuthProcessor.model.dto;

import in.nmaloth.payments.constants.network.NetworkAdviceInit;
import in.nmaloth.payments.constants.network.NetworkKeyExchange;
import in.nmaloth.payments.constants.network.NetworkMessageType;
import in.nmaloth.payments.constants.network.NetworkType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkMessageDto {

    private String messageId;
    private LocalDateTime localDateTime;
    private NetworkMessageType networkMessageType;
    private NetworkType networkType;
    private NetworkAdviceInit networkAdviceInit;
    private NetworkKeyExchange networkKeyExchange;
    private Integer traceNumber;




}
