package AwesomeCalendar.Entities;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetTime;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private OffsetTime time;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Duration duration;

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

    public OffsetTime getTime() {
        return time;
    }

    public void setTime(OffsetTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
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
}
