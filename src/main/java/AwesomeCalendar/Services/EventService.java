package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
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
