package AwesomeCalendar.Entities;

import AwesomeCalendar.enums.NotificationsTiming;

import javax.persistence.*;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /*@JoinColumn(nullable = false)
    @ManyToOne
    private Event event;*/

    @JoinColumn(nullable = false)
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private RoleType roleType;

    @Column(nullable = false)
    private StatusType statusType;

    @Column()
    NotificationsTiming notificationsTiming;

    public Role() {
    }

    public Role(User user, RoleType roleType, StatusType statusType) {
        this.user = user;
        this.roleType = roleType;
        this.statusType = statusType;
    }

    public Long getId() {
        return id;
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

    public NotificationsTiming getNotificationsTiming() {
        return notificationsTiming;
    }

    public void setNotificationsTiming(NotificationsTiming notificationsTiming) {
        this.notificationsTiming = notificationsTiming;
    }

    public enum RoleType {
        ORGANIZER, ADMIN, GUEST
    }

    public enum StatusType {
        APPROVED, REJECTED, TENTATIVE
    }
}
