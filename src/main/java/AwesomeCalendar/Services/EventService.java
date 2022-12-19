package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    @Autowired
    private EventRepo eventRepo;

    public Event createEvent(Event event) {
        return eventRepo.save(event);
    }
    public Event updateEvent(Event event) {
        return eventRepo.save(event);
    }
}
