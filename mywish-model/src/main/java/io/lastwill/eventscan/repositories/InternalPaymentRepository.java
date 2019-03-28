package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.InternalPayment;
import io.lastwill.eventscan.model.Site;
import io.lastwill.eventscan.model.User;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InternalPaymentRepository extends Repository<InternalPayment, Integer> {
    List<InternalPayment> findAllByUserAndSite(@Param("user") User user, @Param("site") Site site);
}
