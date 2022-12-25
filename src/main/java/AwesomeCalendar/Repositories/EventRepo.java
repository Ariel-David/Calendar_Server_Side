package AwesomeCalendar.Repositories;

import AwesomeCalendar.Entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
   List<Event> findEventByStartBetween(ZonedDateTime startDate, ZonedDateTime endDate);
}

