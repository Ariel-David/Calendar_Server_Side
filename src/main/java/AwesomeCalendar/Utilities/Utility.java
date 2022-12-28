package AwesomeCalendar.Utilities;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

public class Utility {

    /**
     * Random string: generate random string
     *
     * @return true if valid emailAddress else false
     */
    public static String generateToken() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("_", "");
    }

    /**
     * encrypt: encrypt password
     * @param passwordToEncrypt - the password to encrypt
     * @return the value encrypted
     */
    public static String encryptPassword(String passwordToEncrypt) {
        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
        return bEncoder.encode(passwordToEncrypt);
    }
    /**
     * encrypt: encrypt password
     * @param userPassword - the user password
     * @param dbUserPassword - the db user password
     * @return true if both passwords are equals
     */
    public static boolean matchesPasswords(String userPassword, String dbUserPassword) {
        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
        return bEncoder.matches(userPassword, dbUserPassword);
    }

    public static void checkArgsNotNull(Object... args) {
        Objects.requireNonNull(args);
        for (Object o : args) {
            if (o == null) {
                throw new IllegalArgumentException("Arguments must be non null");
            }
        }
    }
}
