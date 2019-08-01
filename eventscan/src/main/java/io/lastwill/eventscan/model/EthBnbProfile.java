package io.lastwill.eventscan.model;

import io.lastwill.eventscan.services.Sender;
import lombok.Getter;


@Getter
public abstract class EthBnbProfile {

    private String ethLinkAddress;

    private String ethBurnerAddress;

    private String ethTokenAddress;

    private String transferSymbol;

    Sender sender;

    CryptoCurrency eth;

    CryptoCurrency bnb;



    public EthBnbProfile(String ethLinkAddress,
                         String ethBurnerAddress,
                         String ethTokenAddress,
                         String transferSymbol,
                         Sender sender,
                         CryptoCurrency eth,
                         CryptoCurrency bnb) {

        this.ethLinkAddress = ethLinkAddress;
        this.ethBurnerAddress = ethBurnerAddress;
        this.ethTokenAddress = ethTokenAddress;
        this.transferSymbol = transferSymbol;
        this.sender = sender;
        this.eth = eth;
        this.bnb = bnb;

    }

}
