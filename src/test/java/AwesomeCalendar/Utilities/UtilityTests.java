package AwesomeCalendar.Utilities;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UtilityTests {
    @Test
    void generateToken_TenTokens_returnsUniqueTokens() {
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 10; i++) {
           tokens.add(Utility.generateToken());
        }
        assertEquals(10, tokens.size());
    }

    @Test
    void encryptPassword_NullString_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Utility.encryptPassword(null));
    }

    @Test
    void encryptPassword_String_returnsEncryptedString() {
        String encrypted = Utility.encryptPassword("qazwsx123");
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        assertTrue(bCryptPasswordEncoder.matches("qazwsx123", encrypted));
    }

    @Test
    void matchPassword_FirstArgNullString_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Utility.matchesPasswords(null, "qazwsx123"));
    }

    @Test
    void matchPassword_SecondArgNullString_returnsFalse() {
        assertFalse(Utility.matchesPasswords("qazwsx123", null));
    }

    @Test
    void matchPassword_MismatchedString_returnsFalse() {
        String encrypted = Utility.encryptPassword("qazwsx123");
        assertFalse(Utility.matchesPasswords("nogood", encrypted));
    }

    @Test
    void matchPassword_GoodStrings_returnsTrue() {
        String encrypted = Utility.encryptPassword("qazwsx123");
        assertTrue(Utility.matchesPasswords("qazwsx123", encrypted));
    }
}
