package AwesomeCalendar.CustomEntities;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventDTO {
    private Long id;
    private Event.EventAccess eventAccess;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private String location;
    private String title;
    private String description;
    private List<Role> userRoles;

    private EventDTO() {
    }

    public static EventDTO convertEventToEventDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setEventAccess(event.getEventAccess());
        eventDTO.setStart(event.getStart());
        eventDTO.setEnd(event.getEnd());
        eventDTO.setLocation(event.getLocation());
        eventDTO.setTitle(event.getTitle());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setUserRoles(event.getUserRoles());

        return eventDTO;
    }

    public static List<EventDTO> convertEventListToEventDTOList(List<Event> events) {
        List<EventDTO> listEvents = new ArrayList<>();
        for (Event event : events) {
            EventDTO eventDTO = EventDTO.convertEventToEventDTO(event);
            listEvents.add(eventDTO);
        }
        return listEvents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event.EventAccess getEventAccess() {
        return eventAccess;
    }

    public void setEventAccess(Event.EventAccess eventAccess) {
        this.eventAccess = eventAccess;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<Role> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventDTO eventDTO = (EventDTO) o;

        if (!Objects.equals(id, eventDTO.id)) return false;
        if (eventAccess != eventDTO.eventAccess) return false;
        if (!Objects.equals(start, eventDTO.start)) return false;
        if (!Objects.equals(end, eventDTO.end)) return false;
        if (!Objects.equals(location, eventDTO.location)) return false;
        if (!Objects.equals(title, eventDTO.title)) return false;
        if (!Objects.equals(description, eventDTO.description))
            return false;
        return Objects.equals(userRoles, eventDTO.userRoles);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (eventAccess != null ? eventAccess.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (userRoles != null ? userRoles.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "id=" + id +
                ", eventAccess=" + eventAccess +
                ", start=" + start +
                ", end=" + end +
                ", location='" + location + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userRoles=" + userRoles +
                '}';
    }
}
