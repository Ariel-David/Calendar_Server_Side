package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SharingService {

    private static final Logger logger = LogManager.getLogger(SharingService.class);

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
            logger.debug("error sharing calendar - User cant be null");
            throw new IllegalArgumentException("User cant be null");
        }
        User sharedWith = userRepository.findByEmail(sharedWithEmail);
        if (sharedWith == null) {
            logger.debug("error sharing calendar - email does not correspond to a valid user");
            throw new IllegalArgumentException("Invalid user email");
        }
        if (sharedWith.getSharedWithMeCalendars().contains(user)) {
            logger.debug("error sharing calendar - user:" + user.getId() + " already shared their calendar with user:" + sharedWith.getId());
            throw new IllegalArgumentException("Calendar already shared with this user!");
        }
        logger.info("user:" + user.getId() + " shared their calendar with user:" + sharedWith.getId());
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
            logger.debug("error checking shared emails - user cant be null");
            throw new IllegalArgumentException("user cant be null");
        }
        if (usersEmail == null) {
            logger.debug("error checking shared emails - emails list cant be null");
            throw new IllegalArgumentException("usersEmail cant be null");
        }
        List<User> usersCalendars = userRepository.findByEmailIn(usersEmail);
        boolean containsAll;
        logger.info("starting checking if user has permission to calendars for email list");
        if (usersCalendars.contains(user)) {
            usersCalendars.remove(user);
            containsAll = user.getSharedWithMeCalendars().containsAll(usersCalendars);
            usersCalendars.add(user);
        } else {
            containsAll = user.getSharedWithMeCalendars().containsAll(usersCalendars);
        }
        if (!containsAll) {
            logger.debug("error checking shared emails - user doesn't have access to all emails in list");
            throw new IllegalArgumentException("calendar not shared with user");
        }
        logger.info("finished checking if user has permission to calendars for email list");
        return usersCalendars;
    }
}
