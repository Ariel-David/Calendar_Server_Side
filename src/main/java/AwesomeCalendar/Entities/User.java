package AwesomeCalendar.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static AwesomeCalendar.Utilities.Utility.encryptPassword;


@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, length = 45)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<User> sharedWithMeCalendars;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private NotificationsSettings notificationsSettings;

    public User() {
        sharedWithMeCalendars = new ArrayList<>();
        notificationsSettings = new NotificationsSettings();
    }

    public List<User> getSharedWithMeCalendars() {
        return sharedWithMeCalendars;
    }

    public void addSharedCalendar(User user) {
        this.sharedWithMeCalendars.add(user);
    }

    public User(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
        sharedWithMeCalendars = new ArrayList<>();
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        sharedWithMeCalendars = new ArrayList<>();
    }

    public static User registeredUser(User user) {
        User currUser = new User();
        currUser.setEmail(user.getEmail());
        currUser.setPassword(encryptPassword(user.getPassword()));
        return currUser;
    }

    public NotificationsSettings getNotificationsSettings() {
        return notificationsSettings;
    }

    public void setNotificationsSettings(NotificationsSettings notificationsSettings) {
        this.notificationsSettings = notificationsSettings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(id, user.id)) return false;
        if (!Objects.equals(email, user.email)) return false;
        return Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
