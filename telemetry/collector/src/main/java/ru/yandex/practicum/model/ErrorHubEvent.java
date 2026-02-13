package ru.yandex.practicum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorHubEvent extends HubEvent {
    private String type;
    private Map<String,Object> unknownFields;

    @Override
    public HubEventType getType() {
        return HubEventType.UNKNOWN;
    }
}
