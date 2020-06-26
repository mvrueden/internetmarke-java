package de.marskuh;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Data;

@Data
public class LoginHandle {
    public static final long DEFAULT_TOKEN_VALIDITY_TIME = 60 * 1000;

    private final long tokenValidityTime;
    private String userToken;
    private int walletBalance = 0;
    private long loginTimestamp;

    public LoginHandle() {
        this(DEFAULT_TOKEN_VALIDITY_TIME);
    }

    public LoginHandle(long tokenValidityTime) {
        Preconditions.checkArgument(tokenValidityTime > 0, "Validty time must be > 0");
        this.tokenValidityTime = tokenValidityTime;
    }

    public boolean isAuthenticated() {
        if (Strings.isNullOrEmpty(userToken) || loginTimestamp == 0) {
            return false;
        }
        if (loginTimestamp == 0 || loginTimestamp + tokenValidityTime < System.currentTimeMillis()) {
            return false;
        }
        return true;
    }
}
