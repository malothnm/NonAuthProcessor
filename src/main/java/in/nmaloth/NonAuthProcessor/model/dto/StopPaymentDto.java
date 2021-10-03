package in.nmaloth.NonAuthProcessor.model.dto;

import in.nmaloth.payments.constants.schemeDatabase.ExceptionActionCodes;
import in.nmaloth.payments.constants.schemeDatabase.FileUpdateActions;
import in.nmaloth.payments.constants.schemeDatabase.StopOrder;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class StopPaymentDto {

    private LocalDateTime localDateTime;
    private Integer traceNumber;
    private FileUpdateActions fileUpdateActions;
    private String instrument;
    private String merchantName;
    private String merchantId;
    private String merchantVV;
    private StopOrder stopOrder;


}
