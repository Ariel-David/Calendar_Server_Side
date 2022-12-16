package AwesomeCalendar.Repositories;

import AwesomeCalendar.Entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepo extends JpaRepository<Event, Long> {
}
