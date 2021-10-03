package in.nmaloth.NonAuthProcessor.model.dto;

import in.nmaloth.payments.constants.schemeDatabase.ExceptionActionCodes;
import in.nmaloth.payments.constants.schemeDatabase.FileUpdateActions;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardHolderSchemeDto {

    private LocalDateTime localDateTime;
    private Integer traceNumber;
    private FileUpdateActions fileUpdateActions;
    private String instrument;
    private String replacementInstrument;
    private LocalDateTime replacementDate;

}
