package web.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddPropRequest {
    private final String key;
    private final String value;


    @JsonCreator
    public AddPropRequest(@JsonProperty("key") String key,@JsonProperty("value") String value) {
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }


    public String getValue() {
        return value;
    }
}
