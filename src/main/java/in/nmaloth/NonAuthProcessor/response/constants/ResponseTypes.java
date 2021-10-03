package in.nmaloth.NonAuthProcessor.response.constants;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResponseTypes {

    @JsonProperty("REV")
    REVERSAL("REV"),
    @JsonProperty("RADV")
    REVERSAL_ADVICE("RADV"),
    @JsonProperty("ADV")
    ADVICES("ADV"),
    @JsonProperty("NTW")
    NETWORK("NTW"),
    @JsonProperty("TKN")
    TOKEN("TKN"),
    @JsonProperty("KEX")
    KEY_EXCHANGE("KEX"),
    @JsonProperty("ADMIN")
    ADMIN("ADMIN")

    ;


    private String responseTypes;

    ResponseTypes(String responseTypes){
        this.responseTypes = responseTypes;
    }

}
