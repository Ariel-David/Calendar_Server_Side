package AwesomeCalendar.Repositories;

import AwesomeCalendar.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
}
