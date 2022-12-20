package AwesomeCalendar.CustomEntities;

import AwesomeCalendar.Entities.Event;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Objects;

public class EventDTO {
    private Long id;
    private Event.EventAccess eventAccess;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private String location;
    private String title;
    private String description;

    private EventDTO() {
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
        return Objects.equals(description, eventDTO.description);
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
                '}';
    }
}
