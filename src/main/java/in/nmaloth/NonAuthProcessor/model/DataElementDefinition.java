package in.nmaloth.NonAuthProcessor.model;

import in.nmaloth.NonAuthProcessor.response.constants.ResponseFieldPresent;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataElementDefinition {

    private int dataElement;
    private String dataSet;
    private String subElement;
    private ResponseFieldPresent responseFieldPresent;

}
