package de.marskuh;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static de.marskuh.LoginHandle.DEFAULT_TOKEN_VALIDITY_TIME;
import static org.hamcrest.MatcherAssert.assertThat;

class LoginHandleTest {

    @Test
    public void verifyIsValid() {
        final LoginHandle loginHandle = new LoginHandle();
        assertThat(loginHandle.isAuthenticated(), Matchers.is(false));

        loginHandle.setUserToken("test");
        assertThat(loginHandle.isAuthenticated(), Matchers.is(false));

        loginHandle.setUserToken(null);
        loginHandle.setLoginTimestamp(0);
        assertThat(loginHandle.isAuthenticated(), Matchers.is(false));

        loginHandle.setUserToken("test");
        loginHandle.setLoginTimestamp(System.currentTimeMillis());
        assertThat(loginHandle.isAuthenticated(), Matchers.is(true));

        loginHandle.setLoginTimestamp(System.currentTimeMillis() - DEFAULT_TOKEN_VALIDITY_TIME - 10);
        assertThat(loginHandle.isAuthenticated(), Matchers.is(false));
    }

}