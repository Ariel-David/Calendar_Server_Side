package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SharingService {

    @Autowired
    private UserRepo userRepository;

    public User shareCalendar(User user, String sharedWithEmail) {
        User SharedWith = userRepository.findByEmail(sharedWithEmail);
        if (SharedWith == null) {
            throw new IllegalArgumentException("Invalid user email");
        }
        if (user.getSharedWithMeCalendars().contains(SharedWith)) {
            throw new IllegalArgumentException("Calendar already shared with this user!");
        }
        SharedWith.addSharedCalendar(user);
        userRepository.save(SharedWith);
        return user;
    }
}
