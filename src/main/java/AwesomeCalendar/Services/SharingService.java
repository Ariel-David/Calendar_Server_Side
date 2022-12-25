package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SharingService {

    @Autowired
    private UserRepo userRepository;

    /**
     * share a calendar with another user.
     * @param user the user that wants to share their calendar.
     * @param sharedWithEmail the email of the user they want to share their calendar with.
     * @return the user that we shared the calendar with.
     * @throws IllegalArgumentException if the user is null,
     * if the email doesn't correspond to a user,
     * or if the user is already shared the calendar
     */
    public User shareCalendar(User user, String sharedWithEmail) {
        if (user == null) {
            throw new IllegalArgumentException("User cant be null");
        }
        User sharedWith = userRepository.findByEmail(sharedWithEmail);
        if (sharedWith == null) {
            throw new IllegalArgumentException("Invalid user email");
        }
        if (sharedWith.getSharedWithMeCalendars().contains(user)) {
            throw new IllegalArgumentException("Calendar already shared with this user!");
        }
        sharedWith.addSharedCalendar(user);
        userRepository.save(sharedWith);
        return sharedWith;
    }

    /**
     * checks if the list of users have shared their calendars with the user.
     * @param user the user to check if he has access
     * @param usersEmail the emails of the users to check against.
     * @return all the users corresponding to the emails.
     * @throws IllegalArgumentException if one or more of the users from the email list didn't
     * share their calendar with him.
     */
    public List<User> isShared(User user, List<String> usersEmail) {
        if (user == null) {
            throw new IllegalArgumentException("user cant be null");
        }
        if (usersEmail == null) {
            throw new IllegalArgumentException("usersEmail cant be null");
        }
        List<User> usersCalendars = new ArrayList<>();
        for (String email : usersEmail) {
            User byEmail = userRepository.findByEmail(email);
            if (byEmail == null) {
                throw new IllegalArgumentException("user doesn't exist");
            }
            usersCalendars.add(byEmail);
        }
        boolean containsAll;
        if (usersCalendars.contains(user)) {
            usersCalendars.remove(user);
            containsAll = user.getSharedWithMeCalendars().containsAll(usersCalendars);
            usersCalendars.add(user);
        } else {
            containsAll = user.getSharedWithMeCalendars().containsAll(usersCalendars);
        }
        if (!containsAll) {
            throw new IllegalArgumentException("calendar not shared with user");
        }
        return usersCalendars;
    }
}
