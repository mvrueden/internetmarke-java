package de.marskuh.internetmarke.endpoints.prodws;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdWSConfiguration {
    private boolean logSoapMessages;
    private String username;
    private String password;
    private String mandantId;
}
