package in.nmaloth.NonAuthProcessor.response.model;


import in.nmaloth.NonAuthProcessor.response.constants.ResponseFieldLogicType;
import in.nmaloth.NonAuthProcessor.response.constants.ResponseFieldPresent;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseField {

    private int dataElement;
    private DataSetField[] dataSets;
    private SubElementField[] subElements;

    private ResponseFieldLogicType responseFieldLogicType;
    private ResponseFieldPresent responseFieldPresent;

    private String responseService;
    private String responseIdentifier;
    private String initialValue;

}
