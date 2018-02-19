package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.User;
import io.lastwill.eventscan.model.UserStatistics;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    @Query(name = "User.userStatistics")
    List<UserStatistics> getUserStatistics();
}
