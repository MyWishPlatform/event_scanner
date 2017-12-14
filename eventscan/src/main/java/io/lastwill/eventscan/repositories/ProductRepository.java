package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    @Transactional
    @Modifying
    @Query("update Product set balance = :balanceWei where id = :id")
    void updateBalance(@Param("id") int id, @Param("balanceWei") BigInteger balanceWei);
}
