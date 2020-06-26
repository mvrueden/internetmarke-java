package de.marskuh;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;

public class InternetmarkeHeaderSOAPHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String PREFIX = "v3";
    private static final String URI = "http://oneclickforapp.dpag.de/V3";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Settings.DATE_FORMAT);

    private final String partnerId;
    private final String partnerSignature;

    public InternetmarkeHeaderSOAPHandler(String partnerId, String partnerSignature) {
        this.partnerId = Objects.requireNonNull(partnerId);
        this.partnerSignature = Objects.requireNonNull(partnerSignature);
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        final Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outboundProperty.booleanValue()) {
            try {
                final SOAPMessage message = context.getMessage();
                final SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
                SOAPHeader soapHeader = envelope.getHeader();
                if (soapHeader == null) {
                    soapHeader = envelope.addHeader();
                }
                final ZonedDateTime requestedTime = Instant.now().atZone(Settings.ZONE_ID);
                final String formatedRequestedTime = formatter.format(requestedTime);

                addHeader(soapHeader, "PARTNER_ID", partnerId);
                addHeader(soapHeader, "REQUEST_TIMESTAMP", formatedRequestedTime);
                addHeader(soapHeader, "KEY_PHASE", Settings.KEY_PHASE);
                addHeader(soapHeader, "SIGNATURE_ALGORITHM", "sha-256");
                addHeader(soapHeader, "PARTNER_SIGNATURE", calculateInternetmarkeSignature(formatedRequestedTime));

                message.saveChanges();
            } catch (Exception e) {
                throw new RuntimeException("Error on wsSecurityHandler: " + e.getMessage(), e);
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
    public String calculateInternetmarkeSignature(String timestamp) {
        Signature signature = Signature.builder()
                .keyPhase(Settings.KEY_PHASE)
                .partnerId(partnerId)
                .timestamp(timestamp)
                .partnerSignature(partnerSignature).build();
        return signature.asSha256();
    }

    private static void addHeader(final SOAPHeader header, String key, String value) throws SOAPException {
        Objects.requireNonNull(header);
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        final SOAPElement headerElement = header.addChildElement(key, PREFIX, URI);
        headerElement.setTextContent(value);
    }
}
