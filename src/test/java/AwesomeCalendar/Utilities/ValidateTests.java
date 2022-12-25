package AwesomeCalendar.Utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidateTests {

    @Test
    void email_NullEmail_returnsFalse() {
        assertFalse(Validate.email(null));
    }

    @Test
    void email_BadFormat_returnsFalse() {
        assertFalse(Validate.email("hi"));
    }

    @Test
    void email_GoodFormat_returnsTrue() {
        assertTrue(Validate.email("test@test.com"));
    }

    @Test
    void password_NullPassword_returnsFalse() {
        assertFalse(Validate.password(null));
    }

    @Test
    void password_BadFormat_returnsFalse() {
        assertFalse(Validate.password("qqqqq"));
    }

    @Test
    void password_GoodFormat_returnsTrue() {
        assertTrue(Validate.password("123456789"));
    }
}
