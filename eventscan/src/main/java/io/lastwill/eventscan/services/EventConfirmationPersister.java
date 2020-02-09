package io.lastwill.eventscan.services;

import io.lastwill.eventscan.model.EventConfirmation;
import io.lastwill.eventscan.model.NetworkType;

public interface EventConfirmationPersister {

     boolean tryAdd(String hash, Long blockNumber, NetworkType network);

    Iterable<EventConfirmation> getAllByNetwork (NetworkType network);


   // boolean getByTxHash(String hash);

    Iterable<EventConfirmation> getAllPairs();

    int getCount();


    void remove(String hash);
}
