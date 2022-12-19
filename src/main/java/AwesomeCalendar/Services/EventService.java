package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepo eventRepo;

    private static final Logger logger = LogManager.getLogger(EventService.class.getName());

    public Event createEvent(Event event) {
        return eventRepo.save(event);
    }
    public Event updateEvent(Event event) {
        // filtr permissions
        Optional<Event> eventInDB = eventRepo.findById(event.getId());
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        if (event.getEventAccess() != null && !event.getEventAccess().equals("")) {
            eventInDB.get().setEventAccess(event.getEventAccess());
        }
        if (event.getTime() != null && !event.getTime().equals("")) {
            eventInDB.get().setTime(event.getTime());
        }
        if (event.getDate() != null && !event.getDate().equals("")) {
            eventInDB.get().setDate(event.getDate());
        }
        if (event.getDuration() != null && !event.getDuration().equals("")) {
            eventInDB.get().setDuration(event.getDuration());
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
        eventRepo.save(eventInDB.get());
        return eventInDB.get();
    }

    public Event deleteEvent(Event event) {
        logger.debug("Check if the event exist in DB");
        Optional<Event> eventInDB = eventRepo.findById(event.getId());
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        logger.debug("Delete the event from DB");
        eventRepo.delete(eventInDB.get());
        return eventInDB.get();
    }

    public Event getEvent(Long id){
        logger.debug("Check if the event exist in DB");
        Optional<Event> eventInDB = eventRepo.findById(id);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        logger.debug("Found the event");
        eventRepo.delete(eventInDB.get());
        return eventInDB.get();
    }
}
