package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

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

    public String login(User user) {
        try {
//            logger.debug(checkIfExistsAlready);
            if (userRepository.findByEmail(user.getEmail()) == null) {
//                logger.error(loginFailedMessage);
                throw new IllegalArgumentException("failed login");
            }
//            User dbUser = User.dbUser(userRepository.findByEmail(user.getEmail()));

//            logger.debug(checkPassword);
//            BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
//            if (!bEncoder.matches(user.getPassword(), dbUser.getPassword())) {
//                logger.error(loginFailedMessage);
//                throw new IllegalArgumentException(loginFailedMessage);
//            }
//            logger.info(createToken);
//            logger.info(userLogged);
//            String sessionToken = randomString();
//            keyTokensValEmails.put(sessionToken, dbUser.getEmail());
//            keyEmailsValTokens.put(dbUser.getEmail(), sessionToken);
//            dbUser.setUserStatus(UserStatuses.ONLINE);
//            return userRepository.save(dbUser);
            User byEmail = userRepository.findByEmail(user.getEmail());
            if (!Objects.equals(byEmail.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("password incorrect");
            }
            return "token";
        } catch (RuntimeException e) {
//            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
