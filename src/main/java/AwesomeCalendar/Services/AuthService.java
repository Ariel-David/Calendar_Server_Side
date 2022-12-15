package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private UserRepo userRepository;

    private Map<String, String> keyTokensValEmails;
    /**
     * Adds a user to the database if it has a unique email
     *
     * @param user - the user's data
     * @return a saved user
     * @throws IllegalArgumentException when the provided email already exists
     */
    public User addUser(User user) {
        try {
//            logger.debug(checkIfExistsAlready);
            if (userRepository.findByEmail(user.getEmail()) != null) {
//                logger.error(emailExistsInSystemMessage(user.getEmail()));
                throw new IllegalArgumentException("null");
            }
//            logger.info(userValid);
//            User registeredUser = User.registeredUser(user);
//            logger.info(saveInDbWaitToActivate);
            return userRepository.save(user);
        } catch (RuntimeException e) {
//            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
