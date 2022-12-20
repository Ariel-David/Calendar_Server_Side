package AwesomeCalendar.Repositories;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> getByEventIdAndUserEmail(Long eventId, String userEmail);
    Role findByEventAndUser(Event event, User user);

    @Transactional
    List<Role> deleteByEventId(Long eventId);
}
