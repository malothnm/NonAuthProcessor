package in.nmaloth.NonAuthProcessor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class InitializeElements {

    private int dataElement;
    private String value;
    private int maxSubElementNumber;
    private List<InitializeSubElements> subElements;

}
