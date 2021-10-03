package in.nmaloth.NonAuthProcessor.response.constants;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResponseFieldPresent {

    @JsonProperty("M")
    MANDATORY("M"),
    @JsonProperty("C")
    CONDITIONAL("C"),
    @JsonProperty("O")
    OPTIONAL("O"),
    @JsonProperty("CP")
    CONDITIONAL_IF_PRESENT("CP")
    ;

    private String responseFieldPresent;
    ResponseFieldPresent(String responseFieldPresent){
        this.responseFieldPresent = responseFieldPresent;
    }

    public String getResponseFieldPresent() {
        return responseFieldPresent;
    }
}
