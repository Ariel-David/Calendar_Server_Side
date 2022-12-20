package AwesomeCalendar.CustomEntities;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;

import java.util.Objects;

public class RoleDTO {
    private Long id;
    private Event event;
    private User user;
    private Role.RoleType roleType;
    private Role.StatusType statusType;

    private RoleDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Role.RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(Role.RoleType roleType) {
        this.roleType = roleType;
    }

    public Role.StatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(Role.StatusType statusType) {
        this.statusType = statusType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleDTO roleDTO = (RoleDTO) o;

        if (!Objects.equals(id, roleDTO.id)) return false;
        if (!Objects.equals(event, roleDTO.event)) return false;
        if (!Objects.equals(user, roleDTO.user)) return false;
        if (roleType != roleDTO.roleType) return false;
        return statusType == roleDTO.statusType;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (event != null ? event.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (roleType != null ? roleType.hashCode() : 0);
        result = 31 * result + (statusType != null ? statusType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", event=" + event +
                ", user=" + user +
                ", roleType=" + roleType +
                ", statusType=" + statusType +
                '}';
    }
}
