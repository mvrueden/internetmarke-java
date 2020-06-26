package de.marskuh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.util.Set;

public class SOAPMessageLogger implements SOAPHandler<SOAPMessageContext> {

    private static final Logger LOG =  LoggerFactory.getLogger(SOAPMessageLogger.class);

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        final Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        final SOAPMessage message = smc.getMessage();
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()){
            message.writeTo(bout);
            LOG.info("SOAP {}: {}", outboundProperty.booleanValue() ? "Response" : "Request", bout.toString());
        } catch (Exception e) {
            throw new RuntimeException("Error handling SOAPMessage: " + e.getMessage(), e);
        }
        return outboundProperty;
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