package in.nmaloth.NonAuthProcessor.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseCodeMapItem {

    private String serviceResponse;
    private String responseCode;
    private Integer priority;

}
