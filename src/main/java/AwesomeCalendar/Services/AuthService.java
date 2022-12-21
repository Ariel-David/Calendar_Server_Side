package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public Pair<String, User> login(User user) {
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
//            Map<User, String> m = new HashMap<>();
//            m.put(dbUser, sessionToken);
            return Pair.of(sessionToken, dbUser);
//            return Pair.of(1"")
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public User checkToken(String token) {
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
