package ca.lifesaver.engineers.it.vital.tracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.lifesaver.engineers.it.vital.tracker.LoginActivity;

@RunWith(MockitoJUnitRunner.class)
public class LoginActivityTest {

    private LoginActivity loginActivity;

    @Before
    public void setUp() {
        loginActivity = Mockito.spy(new LoginActivity());
        // Mock any necessary Android dependencies here
    }

    @Test
    public void testValidEmail() {
        assertTrue(loginActivity.isValidEmail("valid.email@example.com"));
    }

    @Test
    public void testInvalidEmail() {
        assertFalse(loginActivity.isValidEmail("invalid-email"));
    }

    @Test
    public void testEmailFieldEmpty() {
        assertFalse(loginActivity.isValidEmail(""));
    }

    @Test
    public void testValidPassword() {
        assertTrue(loginActivity.isValidPassword("123456"));
    }

    @Test
    public void testInvalidPasswordTooShort() {
        assertFalse(loginActivity.isValidPassword("12345"));
    }
}

