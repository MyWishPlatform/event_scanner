package io.lastwill.eventscan.services;

import io.lastwill.eventscan.model.EventConfirmation;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.EventConfirmationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

public class EventConfirmationDbPersister implements EventConfirmationPersister {

    @Autowired
    private EventConfirmationRepository eventConfirmationRepository;

    @PostConstruct
    protected void init() {
       // log.info("Loaded {} chats.", getCount());
    }


    @Override
    public synchronized boolean tryAdd(String hash, Long blockNumber, NetworkType network) {
        if (eventConfirmationRepository.findByTxHash(hash)) {
            return false;
        }

        eventConfirmationRepository.save(new EventConfirmation(hash, blockNumber, network));
        return true;
    }

    @Override
    public Iterable<EventConfirmation> getAllByNetwork(NetworkType network) {
        return eventConfirmationRepository.findAllByNetwork(network)
                .stream()
                //.map(EventConfirmation::)
                //map(EventConfirmation::getBlockNumber)
                .collect(Collectors.toSet());
    }

    @Override
    public Iterable<EventConfirmation> getAllPairs() {
        return eventConfirmationRepository.findAll()
                .stream()
                //.map(EventConfirmation::"")
                .collect(Collectors.toSet());
    }

    @Override
    public int getCount() {
        return (int) eventConfirmationRepository.count();
    }

    @Override
    public void remove(String hash) {
        eventConfirmationRepository.deleteByTxHash(hash);
    }




}
