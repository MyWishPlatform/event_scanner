package io.lastwill.eventscan.events.model.wishbnbswap;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class ProfileStorage {

    @Autowired
    private List<EthBnbProfile> ethBnbProfiles;
}
