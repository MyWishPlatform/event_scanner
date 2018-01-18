package io.mywish.joule.repositories;

import io.mywish.joule.model.JouleRegistration;
import io.mywish.joule.model.JouleRegistrationState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface JouleRegistrationRepository extends CrudRepository<JouleRegistration, Integer> {
    //    @Query("select j from JouleRegistration j where j.state = :created order by j.id")
//    JouleRegistration findFirstReadyToRegister(@Param("created") JouleRegistrationState created);
    Stream<JouleRegistration> findByStateOrderByIdAsc(JouleRegistrationState state);

    @Modifying
    @Query("update JouleRegistration j set state = :newState where j = :registration and state = :requiredState")
    int updateState(@Param("registration") JouleRegistration registration,
                    @Param("newState") JouleRegistrationState newState,
                    @Param("requiredState") JouleRegistrationState requiredState);

    @Modifying
    @Query("update JouleRegistration j set txHash = :txHash, state = :newState where j = :registration and state = :requiredState")
    int updateTxHashAndState(@Param("registration") JouleRegistration registration,
                      @Param("txHash") String txHash,
                      @Param("newState") JouleRegistrationState newState,
                      @Param("requiredState") JouleRegistrationState requiredState);

}
