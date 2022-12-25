package AwesomeCalendar.Repositories;

import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByEmail(String email);
}