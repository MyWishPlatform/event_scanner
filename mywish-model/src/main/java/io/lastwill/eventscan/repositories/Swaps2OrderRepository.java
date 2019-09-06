package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.Swaps2Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface Swaps2OrderRepository extends CrudRepository <Swaps2Order, Integer> {
    Swaps2Order findByOrderId(@Param("orderId") String orderId);
}
