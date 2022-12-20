package AwesomeCalendar.Repositories;

import AwesomeCalendar.Entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepo extends JpaRepository<Event, Long> {
    Optional<List<Event>> getAllEvents(String userEmail);
}
