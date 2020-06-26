package de.marskuh;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternetmarkeServiceConfiguration {
    private boolean logSoapMessages;
    private String portokasseUsername;
    private String portokassePassword;
    private String partnerId;
    private String partnerSignature;
}
