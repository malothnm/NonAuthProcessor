package in.nmaloth.NonAuthProcessor.model;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitializeFixedSubElements {

    private int dataElement;
    private int maxSubElementNumber;
    private Map<String, InitializeSubElements> subElementsMap;
}
