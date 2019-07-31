package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.Swaps2Order;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface Swaps2OrderRepository extends Repository<Swaps2Order, Integer> {
    Swaps2Order findByOrderId(@Param("orderId") String orderId);
}
