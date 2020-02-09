//package io.lastwill.eventscan.events.model.utility;
//
//import io.lastwill.eventscan.events.model.BaseEvent;
//import io.lastwill.eventscan.events.model.UserPaymentEvent;
//import io.lastwill.eventscan.model.CryptoCurrency;
//import io.lastwill.eventscan.model.NetworkType;
//import io.lastwill.eventscan.model.UserSiteBalance;
//import io.mywish.blockchain.WrapperTransaction;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//
//import java.math.BigInteger;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class EventConfirmation extends BaseEvent {
//
//    private final ConcurrentHashMap<NetworkType, LastEvent> lastBlockEvents = new ConcurrentHashMap<>();
//
//    public int blocksConfirmed;
//
//    @Autowired
//    private EventPublisher eventPublisher;
//
//
//    public EventConfirmation(NetworkType networkType , int blocksConfirmed) {
//        super(networkType);
//        this.blocksConfirmed = blocksConfirmed;
//    }
//
//    @EventListener
//    private void newBlockEvent(NewBlockEvent event) {
//        lastBlockEvents.put(
//                event.getNetworkType(),
//                new LastEvent(
//                        LocalDateTime.now(ZoneOffset.UTC),
//                        event.getBlock().getTimestamp(),
//                        event.getBlock().getNumber()
//                )
//        );
//    }
//
//}
