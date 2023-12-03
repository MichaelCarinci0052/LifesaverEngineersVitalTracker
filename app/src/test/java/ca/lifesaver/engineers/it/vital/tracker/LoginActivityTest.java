package ca.lifesaver.engineers.it.vital.tracker;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LoginActivityTest {

    private LoginActivity loginActivity;

    @Before
    public void setUp() {
        loginActivity = new LoginActivity();
        // Initialize necessary Android components here, if required
    }

    @Test
    public void testIsValidEmail_withValidEmail() {
        assertTrue(loginActivity.isValidEmail("test@example.com"));
    }

    @Test
    public void testIsValidEmail_withInvalidEmail() {
        assertFalse(loginActivity.isValidEmail("invalidemail"));
    }

    @Test
    public void testIsValidPassword_withValidPassword() {
        assertTrue(loginActivity.isValidPassword("123456"));
    }

    @Test
    public void testIsValidPassword_withShortPassword() {
        assertFalse(loginActivity.isValidPassword("123"));
    }

    @Test
    public void testIsValidPassword_withEmptyPassword() {
        assertFalse(loginActivity.isValidPassword(""));
    }

    // Add more test cases as needed
}
