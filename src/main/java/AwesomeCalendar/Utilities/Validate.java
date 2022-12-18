package AwesomeCalendar.Utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

public class Validate {
    private static Logger logger = LogManager.getLogger(Validate.class.getName());
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_PASSWORD_REGEX =
            Pattern.compile("^[0-9]{5,10}$", Pattern.CASE_INSENSITIVE);

    public static boolean email(String email) {

        logger.info("email validation has begun: ");
        boolean status = VALID_EMAIL_ADDRESS_REGEX.matcher(email).find();

        if (status) {
            logger.info("email is valid");
        } else {
            logger.warn("email is not valid");
        }
        return status;
    }

    public static boolean password(String password) {
        logger.info("password validation has begun: ");
        boolean status = VALID_PASSWORD_REGEX.matcher(password).find();

        if (status) {
            logger.info("password is valid");
        } else {
            logger.warn("password is not valid");
        }
        return status;
    }
}
