package io.lastwill.eventscan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@RequiredArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = false)
@Setter
public class Swap2Json {

    private String status;
    private String contractId;

}
