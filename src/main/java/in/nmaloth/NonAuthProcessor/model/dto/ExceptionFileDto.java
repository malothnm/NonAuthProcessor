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
public class ExceptionFileDto {

    private LocalDateTime localDateTime;
    private Integer traceNumber;
    private ExceptionActionCodes exceptionActionCodes;
    private FileUpdateActions fileUpdateActions;
    private String region;
    private String instrument;

}
