package in.nmaloth.NonAuthProcessor.response.model;

import in.nmaloth.NonAuthProcessor.response.constants.ResponseTypes;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseSummary {

    private ResponseTypes responseTypes;
    private ResponseField[] responseFields;


}
