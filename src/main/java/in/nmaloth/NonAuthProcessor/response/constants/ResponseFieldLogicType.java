package in.nmaloth.NonAuthProcessor.response.constants;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResponseFieldLogicType {

    @JsonProperty("R")
    REPLAY_FIELD("R"),

    @JsonProperty("S")
    SERVICE_FIELD("S"),

    @JsonProperty("C")
    CUSTOM_FIELD("C"),

    @JsonProperty("N")
    NEED_FIELD("N")

    ;


    private String responseFieldLogicType;

    ResponseFieldLogicType(String responseFieldLogicType){
        this.responseFieldLogicType = responseFieldLogicType;
    }

    public String getResponseFieldLogicType() {
        return responseFieldLogicType;
    }
}
