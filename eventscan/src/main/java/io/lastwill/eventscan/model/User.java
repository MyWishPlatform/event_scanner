package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "auth_user")
@SqlResultSetMapping(
        name = "userStatistics",
        classes = @ConstructorResult(
                targetClass = UserStatistics.class,
                columns = {
                        @ColumnResult(name = "user_count", type = Long.class),
                        @ColumnResult(name = "registered", type = Boolean.class),
                }
        )
)
@NamedNativeQuery(
        name = "User.userStatistics",
        query = "SELECT count(id) user_count, (email IS NOT NULL AND email != '') registered\n" +
                "FROM auth_user\n" +
                "WHERE NOT is_staff AND NOT is_superuser AND is_active\n" +
                "GROUP BY email IS NOT NULL AND email != '' \n",
        resultSetMapping = "userStatistics"
)

@Getter
public class User {
    @Id
    private int id;
//    private OffsetDateTime dateJoined;
    @Column(name = "is_staff")
    private boolean staff;
    @Column(name = "is_active")
    private boolean active;
    @Column(name = "is_superuser")
    private boolean superuser;
    private String email;
}
