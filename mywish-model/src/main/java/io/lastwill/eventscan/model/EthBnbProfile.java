package io.lastwill.eventscan.model;

import lombok.Getter;


@Getter
public abstract class EthBnbProfile {

    private String ethLinkAddress;

    private String ethBurnerAddress;

    private String ethTokenAddress;

    private String transferSymbol;

    CryptoCurrency eth;

    CryptoCurrency bnb;



    public EthBnbProfile(String ethLinkAddress,
                         String ethBurnerAddress,
                         String ethTokenAddress,
                         String transferSymbol,
                         CryptoCurrency eth,
                         CryptoCurrency bnb) {

        this.ethLinkAddress = ethLinkAddress;
        this.ethBurnerAddress = ethBurnerAddress;
        this.ethTokenAddress = ethTokenAddress;
        this.transferSymbol = transferSymbol;
        this.eth = eth;
        this.bnb = bnb;

    }

}
