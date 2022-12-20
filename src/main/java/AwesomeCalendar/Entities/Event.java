package AwesomeCalendar.Entities;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private EventAccess eventAccess;

    @Column(nullable = false)
    private ZonedDateTime start;

    @Column(nullable = false)
    private ZonedDateTime end;

    @Column
    private String location;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

//    @Column
//    private List<File> attachments;

    public Event() {
    }

    public Long getId() {
        return id;
    }

    public EventAccess getEventAccess() {
        return eventAccess;
    }

    public void setEventAccess(EventAccess eventAccess) {
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

//    public List<File> getAttachments() {
//        return attachments;
//    }
//
//    public void setAttachments(List<File> attachments) {
//        this.attachments = attachments;
//    }

    public enum EventAccess {
        PUBLIC, PRIVATE
    }
}
