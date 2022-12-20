package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Repositories.EventRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepo eventRepository;

    private static final Logger logger = LogManager.getLogger(EventService.class.getName());

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }
    public Event updateEvent(Event event) {
        Optional<Event> eventInDB = eventRepository.findById(event.getId());
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        if (event.getEventAccess() != null && !event.getEventAccess().equals("")) {
            eventInDB.get().setEventAccess(event.getEventAccess());
        }
        if (event.getStart() != null) {
            eventInDB.get().setStart(event.getStart());
        }
        if (event.getEnd() != null) {
            eventInDB.get().setEnd(event.getEnd());
        }
        if (event.getLocation() != null && !event.getLocation().equals("")) {
            eventInDB.get().setLocation(event.getLocation());
        }
        if (event.getTitle() != null && !event.getTitle().equals("")) {
            eventInDB.get().setTitle(event.getTitle());
        }
        if (event.getDescription() != null && !event.getDescription().equals("")) {
            eventInDB.get().setDescription(event.getDescription());
        }
        eventRepository.save(eventInDB.get());
        return eventInDB.get();
    }

    public Event deleteEvent(Event event) {
        logger.debug("Check if the event exist in DB");
        Optional<Event> eventInDB = eventRepository.findById(event.getId());
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        logger.debug("Delete the event from DB");
        eventRepository.delete(eventInDB.get());
        return eventInDB.get();
    }

    public Optional<Event> getEvent(Long id){
        logger.debug("Check if the event exist in DB");
        Optional<Event> eventInDB = eventRepository.findById(id);
        logger.debug("Found the event");
        return eventInDB;
    }
    public List<Event> getEventsBetweenDates(LocalDate startDate , LocalDate endDate){
        return eventRepository.findEventByStartBetween(startDate , endDate);
    }
}
