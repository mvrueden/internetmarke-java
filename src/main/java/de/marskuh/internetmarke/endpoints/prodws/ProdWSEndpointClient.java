package de.marskuh.internetmarke.endpoints.prodws;

import de.marskuh.SOAPMessageLogger;
import de.marskuh.Settings;
import de.printbird3d.prodws.GetProductListRequestType;
import de.printbird3d.prodws.GetProductListResponse;
import de.printbird3d.prodws.GetProductListResponseType;
import de.printbird3d.prodws.ProdWSPortType;
import de.printbird3d.prodws.ProdWSService;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

// TODO MVR add caching for getProductList()
public class ProdWSEndpointClient {

    private final ProdWSPortType port;
    private final ProdWSConfiguration config;

    public ProdWSEndpointClient(ProdWSConfiguration config) {
        final ProdWSService service = new ProdWSService();
        final ProdWSPortType port = service.getProdWSPort();
        this.port = port;
        this.config = Objects.requireNonNull(config);

        final BindingProvider bindingProvider = (BindingProvider) port;
        final Binding binding = bindingProvider.getBinding();

        final List<Handler> handlerList = binding.getHandlerChain();
        handlerList.add(new WSSecurityHeaderSOAPHandler(config.getUsername(), config.getPassword()));
        if (config.isLogSoapMessages()) {
            handlerList.add(new SOAPMessageLogger());
        }
        binding.setHandlerChain(handlerList);
    }

    public GetProductListResponseType getProductList() {
        final GetProductListRequestType prodListType = new GetProductListRequestType();
        prodListType.setDedicatedProducts(true);
        prodListType.setMandantID(config.getMandantId());
        prodListType.setResponseMode(new BigInteger("0"));

        final GetProductListResponse productList = port.getProductList(prodListType);
        if (!productList.isSuccess()) {
            throw new RuntimeException("Could not read product list."); // TODO MVR proper exception handling
        }
        return productList.getResponse();

    }
}
