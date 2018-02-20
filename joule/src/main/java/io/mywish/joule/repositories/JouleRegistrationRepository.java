package io.mywish.joule.repositories;

import io.mywish.joule.model.JouleRegistration;
import io.mywish.joule.model.JouleRegistrationState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JouleRegistrationRepository extends CrudRepository<JouleRegistration, Integer> {
    //    @Query("select j from JouleRegistration j where j.state = :created order by j.id")
//    JouleRegistration findFirstReadyToRegister(@Param("created") JouleRegistrationState created);
    List<JouleRegistration> findByStateOrderByIdAsc(JouleRegistrationState state);

    List<JouleRegistration> findByContractAddressAndState(String contractAddress, JouleRegistrationState state);

    @Transactional
    @Modifying
    @Query("update JouleRegistration j set state = :newState where j = :registration and state = :requiredState")
    int updateState(@Param("registration") JouleRegistration registration,
                    @Param("requiredState") JouleRegistrationState requiredState,
                    @Param("newState") JouleRegistrationState newState);

    @Transactional
    @Modifying
    @Query("update JouleRegistration j set txHash = :txHash, state = :newState where j = :registration and state = :requiredState")
    int updateTxHashAndState(@Param("registration") JouleRegistration registration,
                             @Param("txHash") String txHash,
                             @Param("requiredState") JouleRegistrationState requiredState,
                             @Param("newState") JouleRegistrationState newState);
}
