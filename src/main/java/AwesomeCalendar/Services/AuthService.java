package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static AwesomeCalendar.Utilities.Utility.*;

@Service
public class AuthService {
    @Autowired
    private UserRepo userRepository;

    private Map<String, String> keyTokensValEmails;

    /**
     * AuthService constructor
     * Initializes keyTokensValEmails new Map
     * Initializes keyEmailsValTokens new Map
     */
    AuthService() {
        this.keyTokensValEmails = getTokensInstance();
    }

    /**
     * Initializes the keyTokensValEmails if the keyTokensValEmails is null
     */
    Map<String, String> getTokensInstance() {
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
        try {
            if (userRepository.findByEmail(user.getEmail()) != null) {
                throw new IllegalArgumentException("email exist");
            }
            User registeredUser = User.registeredUser(user);
            return userRepository.save(registeredUser);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String login(User user) {
        try {
            User dbUser = userRepository.findByEmail(user.getEmail());
            if (dbUser == null) {
                throw new IllegalArgumentException("failed login");
            }
            if (!matchesPasswords(user.getPassword(), dbUser.getPassword())) {
                throw new IllegalArgumentException("wrong password");
            }
            String sessionToken = generateToken();
            keyTokensValEmails.put(sessionToken, dbUser.getEmail());
            return sessionToken;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
