package AwesomeCalendar.Repositories;

import AwesomeCalendar.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> getByEventIdAndUserEmail(Long eventId, String userEmail);
}
