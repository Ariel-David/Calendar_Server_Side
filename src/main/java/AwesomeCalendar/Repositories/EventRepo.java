package AwesomeCalendar.Repositories;

import AwesomeCalendar.Entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
   List<Event> findEventBetween(LocalDate startDate, LocalDate endDate);
}

