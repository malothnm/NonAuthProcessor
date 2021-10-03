package in.nmaloth.NonAuthProcessor.model;

import in.nmaloth.payments.constants.ServiceResponse;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationResponse {

    private String serviceId;
    private ServiceResponse serviceResponse;
    private Map<String,String> responseFields;

}
