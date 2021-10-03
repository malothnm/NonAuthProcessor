package in.nmaloth.NonAuthProcessor.response.model;


import in.nmaloth.NonAuthProcessor.response.constants.ResponseFieldLogicType;
import in.nmaloth.NonAuthProcessor.response.constants.ResponseFieldPresent;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DataSetField {

    private String dataSetId;
    private SubElementField[] subElementFields;

    private ResponseFieldLogicType responseFieldLogicType;
    private ResponseFieldPresent responseFieldPresent;

}
