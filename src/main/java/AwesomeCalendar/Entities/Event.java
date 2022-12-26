package AwesomeCalendar.Entities;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<Role> userRoles;

    public List<Role> getUserRoles() {
        return userRoles;
    }

    public Optional<Role> getUserRole(User user) {
        return this.userRoles.stream().filter((role) -> role.getUser().equals(user)).findFirst();
    }

    public void AddUserRole(Role userRole) {
        this.userRoles.add(userRole);
    }

    public void removeUserRole(Role role) {
        this.userRoles.remove(role);
    }

    public Event() {
        this.userRoles = new ArrayList<>();
    }

    public Event(Long id,EventAccess eventAccess, ZonedDateTime start, ZonedDateTime end, String location, String title, String description) {
        this.id = id;
        this.eventAccess = eventAccess;
        this.start = start;
        this.end = end;
        this.location = location;
        this.title = title;
        this.description = description;
        this.userRoles = new ArrayList<>();
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
