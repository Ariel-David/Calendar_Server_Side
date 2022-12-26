package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

import static AwesomeCalendar.Utilities.Utility.*;

@Service
public class AuthService {
    @Autowired
    private UserRepo userRepository;

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private Map<String, String> keyTokensValEmails;

    String clientId = "2298388bcf5985aa7bcb";
    String clientSecret = "50b29b012b0b535aa7d2f20627b8ebf790b390a";

    /**
     * AuthService constructor
     * Initializes keyTokensValEmails new Map
     * Initializes keyEmailsValTokens new Map
     */
    AuthService() {
        logger.info("starting auth service");
        this.keyTokensValEmails = getTokensInstance();
    }

    /**
     * Initializes the keyTokensValEmails if the keyTokensValEmails is null
     */
    private Map<String, String> getTokensInstance() {
        if (this.keyTokensValEmails == null)
            this.keyTokensValEmails = new HashMap<>();
        return this.keyTokensValEmails;
    }

    /**
     * Adds a user to the database if it has a unique email
     *
     * @param user - the user's data
     * @return a saved user
     * @throws IllegalArgumentException when the provided email already exists
     */
    public User addUser(User user) {
        checkArgsNotNull(user);
        checkArgsNotNull(user.getEmail(), user.getPassword());
        logger.info("adding user:" + user);
        if (userRepository.findByEmail(user.getEmail()) != null) {
            logger.debug("cant create user - email already exist:" + user.getEmail());
            throw new IllegalArgumentException("email exist");
        }
        User registeredUser = User.registeredUser(user);
        return userRepository.save(registeredUser);
    }

    public Pair<String, User> login(User user) {
        checkArgsNotNull(user);
        checkArgsNotNull(user.getEmail(), user.getPassword());
        logger.info("logging in - user:" + user);
        User dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser == null) {
            throw new IllegalArgumentException("failed login");
        }
        if (!matchesPasswords(user.getPassword(), dbUser.getPassword())) {
            throw new IllegalArgumentException("wrong password");
        }
        String sessionToken = generateToken();
        keyTokensValEmails.put(sessionToken, dbUser.getEmail());
        return Pair.of(sessionToken, dbUser);
    }

    public User checkToken(String token) {
        logger.info("checking token:" + token);
        if (!keyTokensValEmails.containsKey(token)) {
            throw new IllegalArgumentException("invalid token");
        }
        return userRepository.findByEmail(keyTokensValEmails.get(token));
    }

    /**
     * gets the KeyTokensValEmails Map
     */
    public Map<String, String> getKeyTokensValEmails() {
        return this.keyTokensValEmails;
    }
}
