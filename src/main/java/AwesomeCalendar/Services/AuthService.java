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

/**
 A service for handling authentication tasks such as login and token validation.

 @implNote The service uses a repository to store and retrieve user data and a map to store and validate session tokens. The service provides methods for

 adding a new user, logging in an existing user, and validating a session token. The service also has a constructor that initializes the map of session tokens

 and a method for checking the validity of function arguments.
 */
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

    /**
     Attempts to log in a user by checking their email and password against the repository.
     @param user the user object containing the email and password to be checked
     @return a pair containing the session token and the user object if the login is successful, or an IllegalArgumentException if the login fails
     @implNote The method first checks that the user object and its email and password fields are not null. It then retrieves the user object from the repository
     by email. If the user object is not found, an IllegalArgumentException is thrown. If the user object is found, the method checks if the provided password
     matches the one stored in the repository. If the passwords do not match, an IllegalArgumentException is thrown. If the passwords match, the method generates
     a session token and stores it in a map along with the user's email. The method returns a pair containing the session token and the user object.
     */
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

    /**
     Checks the validity of a session token by checking if it is stored in a map.
     @param token the session token to be checked
     @return the user object corresponding to the session token if the token is valid, or an IllegalArgumentException if the token is invalid
     @implNote The method first checks if the token is stored in a map. If it is not, an IllegalArgumentException is thrown. If it is, the method retrieves the
     user object corresponding to the token by email and returns it.
     */
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
