package de.marskuh.internetmarke.endpoints.prodws;

import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecUsernameToken;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;

public class WSSecurityHeaderSOAPHandler implements SOAPHandler<SOAPMessageContext> {

    private final String usernameText;
    private final String passwordText;

    public WSSecurityHeaderSOAPHandler(String usernameText, String passwordText) {
        this.usernameText = usernameText;
        this.passwordText = passwordText;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outboundProperty.booleanValue()) {

            try {
                SOAPMessage soapMessage = context.getMessage();
                soapMessage.removeAllAttachments();

                SOAPPart soappart = soapMessage.getSOAPPart();
                WSSecHeader wsSecHeader = new WSSecHeader(soappart.getEnvelope().getHeader().getOwnerDocument());
                wsSecHeader.insertSecurityHeader();
                WSSecUsernameToken token = new WSSecUsernameToken(wsSecHeader);
                token.setPasswordsAreEncoded(true);
                token.setPasswordType(WSConstants.PASSWORD_TEXT);
                token.setUserInfo(usernameText, passwordText);
                token.build();
                soapMessage.saveChanges();
            } catch (Exception e) {
                throw new RuntimeException("Error on wsSecurityHandler: " + e.getMessage());
            }

        }

        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {

    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}
