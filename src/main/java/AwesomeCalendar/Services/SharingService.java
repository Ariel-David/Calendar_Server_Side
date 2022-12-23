package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SharingService {

    @Autowired
    private UserRepo userRepository;

    @Transactional
    public User shareCalendar(User user, String sharedWithEmail) {
        User SharedWith = userRepository.findByEmail(sharedWithEmail);
        if (SharedWith == null) {
            throw new IllegalArgumentException("Invalid user email");
        }
        SharedWith.getSharedWithMeCalendars();
        user.AddSharedCalendar(SharedWith);
        userRepository.save(user);
        return user;
    }
}
