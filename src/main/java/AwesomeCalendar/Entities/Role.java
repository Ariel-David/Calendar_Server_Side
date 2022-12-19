package AwesomeCalendar.Entities;

import javax.persistence.*;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Event event;

    @JoinColumn(nullable = false)
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private RoleType roleType;

    @Column(nullable = false)
    private StatusType statusType;

    public Role() {
    }

    public Role(Event event, User user, RoleType roleType, StatusType statusType) {
        this.event = event;
        this.user = user;
        this.roleType = roleType;
        this.statusType = statusType;
    }

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public enum RoleType {
        ORGANIZER, ADMIN, GUEST
    }

    public enum StatusType {
        APPROVED, REJECTED, TENTATIVE
    }
}
