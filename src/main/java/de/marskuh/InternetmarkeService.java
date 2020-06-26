package de.marskuh;

import de.printbird3d.internetmarke.Address;
import de.printbird3d.internetmarke.AddressBinding;
import de.printbird3d.internetmarke.AuthenticateUserException_Exception;
import de.printbird3d.internetmarke.AuthenticateUserRequestType;
import de.printbird3d.internetmarke.AuthenticateUserResponseType;
import de.printbird3d.internetmarke.CompanyName;
import de.printbird3d.internetmarke.CreateShopOrderIdRequest;
import de.printbird3d.internetmarke.CreateShopOrderIdResponse;
import de.printbird3d.internetmarke.GalleryItem;
import de.printbird3d.internetmarke.IdentifyException_Exception;
import de.printbird3d.internetmarke.InvalidMotiveException_Exception;
import de.printbird3d.internetmarke.InvalidProductException_Exception;
import de.printbird3d.internetmarke.Name;
import de.printbird3d.internetmarke.NamedAddress;
import de.printbird3d.internetmarke.OneClickForAppPortTypeV3;
import de.printbird3d.internetmarke.OneClickForAppServiceV3;
import de.printbird3d.internetmarke.Orientation;
import de.printbird3d.internetmarke.RetrieveContractProductsRequestType;
import de.printbird3d.internetmarke.RetrievePageFormatsRequestType;
import de.printbird3d.internetmarke.RetrievePageFormatsResponseType;
import de.printbird3d.internetmarke.RetrievePreviewVoucherPNGRequestType;
import de.printbird3d.internetmarke.RetrievePreviewVoucherResponseType;
import de.printbird3d.internetmarke.RetrievePublicGalleryRequestType;
import de.printbird3d.internetmarke.RetrievePublicGalleryResponseType;
import de.printbird3d.internetmarke.ShoppingCartPDFPosition;
import de.printbird3d.internetmarke.ShoppingCartPDFRequestType;
import de.printbird3d.internetmarke.ShoppingCartResponseType;
import de.printbird3d.internetmarke.ShoppingCartValidationException_Exception;
import de.printbird3d.internetmarke.VoucherLayout;
import de.printbird3d.internetmarke.VoucherPosition;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InternetmarkeService {

    private final InternetmarkeServiceConfiguration config;
    private final OneClickForAppPortTypeV3 port;

    private LoginHandle loginHandle = new LoginHandle();

    public InternetmarkeService(InternetmarkeServiceConfiguration config) {
        final OneClickForAppServiceV3 pcfOneClickService = new OneClickForAppServiceV3();
        this.port = pcfOneClickService.getOneClickForAppPortV3();
        this.config = Objects.requireNonNull(config);

        final BindingProvider bindingProvider = (BindingProvider) port;
        final Binding binding = bindingProvider.getBinding();
        final List<Handler> handlerList = binding.getHandlerChain();

        handlerList.add(new InternetmarkeHeaderSOAPHandler(config.getPartnerId(), config.getPartnerSignature()));
        if (config.isLogSoapMessages()) {
            handlerList.add(new SOAPMessageLogger());
        }
        binding.setHandlerChain(handlerList);
    }

    // TODO MVR return public gallery (-:
    public void initialise() {
        final RetrievePublicGalleryRequestType request = new RetrievePublicGalleryRequestType();
        final RetrievePublicGalleryResponseType response = port.retrievePublicGallery(request);
        for (GalleryItem eachItem : response.getItems()) {
            System.out.println(eachItem.getCategory() + " - " + eachItem.getCategoryDescription());
        }
    }

    protected void authenticate() throws AuthenticateUserException_Exception {
        final AuthenticateUserRequestType request = new AuthenticateUserRequestType();
        request.setUsername(config.getPortokasseUsername());
        request.setPassword(config.getPortokassePassword());
        final AuthenticateUserResponseType response = port.authenticateUser(request);
        this.loginHandle.setLoginTimestamp(System.currentTimeMillis());
        this.loginHandle.setUserToken(response.getUserToken());
        this.loginHandle.setWalletBalance(response.getWalletBalance());
    }

    public LoginHandle getLoginHandle() throws AuthenticateUserException_Exception {
        requireAuthenticated();
        return this.loginHandle;
    }

    protected boolean isAuthenticated() {
        return loginHandle.isAuthenticated();
    }

    protected void requireAuthenticated() throws AuthenticateUserException_Exception {
        if (!isAuthenticated()) {
            authenticate();
        }
    }

    public void printContractProducts() throws IdentifyException_Exception, AuthenticateUserException_Exception {
        requireAuthenticated();
        final RetrieveContractProductsRequestType request = new RetrieveContractProductsRequestType();
        request.setUserToken(loginHandle.getUserToken());

        port.retrieveContractProducts(request).getProducts().stream().forEach(p -> System.out.println(p.getProductCode() + " " + p.getPrice()));
    }

    private String getNewOrderId() throws AuthenticateUserException_Exception, IdentifyException_Exception {
        requireAuthenticated();
        final CreateShopOrderIdRequest request = new CreateShopOrderIdRequest();
        request.setUserToken(loginHandle.getUserToken());

        final CreateShopOrderIdResponse response = port.createShopOrderId(request);
        return response.getShopOrderId();
    }

    public List<PageFormat> getPageFormats() {
        final RetrievePageFormatsRequestType request = new RetrievePageFormatsRequestType();
        final RetrievePageFormatsResponseType response = port.retrievePageFormats(request);
        return response.getPageFormat().stream().map(p -> {
            final de.marskuh.PageFormat pageFormat = new de.marskuh.PageFormat();
            pageFormat.setId(p.getId());
            pageFormat.setName(p.getName());
            pageFormat.setDescription(p.getDescription());
            if (p.getPageLayout().getOrientation() == Orientation.LANDSCAPE) {
                pageFormat.setOrientation(PageFormat.Orientation.Landscape);
            } else {
                pageFormat.setOrientation(PageFormat.Orientation.Portrait);
            }
            pageFormat.setWidth((float) p.getPageLayout().getSize().getX());
            pageFormat.setHeight((float) p.getPageLayout().getSize().getY());
            pageFormat.setMargins(Margins.builder()
                    .top((float)p.getPageLayout().getMargin().getTop())
                    .bottom((float)p.getPageLayout().getMargin().getBottom())
                    .right((float)p.getPageLayout().getMargin().getRight())
                    .left((float)p.getPageLayout().getMargin().getLeft())
                    .build());
            pageFormat.setXLabelCount(p.getPageLayout().getLabelCount().getLabelX());
            pageFormat.setYLabelCount(p.getPageLayout().getLabelCount().getLabelY());
            return pageFormat;
        })
        .collect(Collectors.toList());
    }

    public String generatePreviewLink(int productId) throws InvalidProductException_Exception, InvalidMotiveException_Exception {
        final RetrievePreviewVoucherPNGRequestType previewRequest = new RetrievePreviewVoucherPNGRequestType();
        previewRequest.setVoucherLayout(VoucherLayout.ADDRESS_ZONE);
        previewRequest.setProductCode(productId);
        final RetrievePreviewVoucherResponseType previewResponse = port.retrievePreviewVoucherPNG(previewRequest);
        return previewResponse.getLink();
    }

    public Receipt placeSingleOrder(int productCode, int pageFormatId, int price, AddressDTO senderAddress, AddressDTO receipiantAddress) throws AuthenticateUserException_Exception, IdentifyException_Exception, ShoppingCartValidationException_Exception {
        requireAuthenticated();
        if (price <= 0) {
            throw new IllegalArgumentException("price must be > 0");
        }
        if (loginHandle.getWalletBalance() < price) {
            throw new IllegalArgumentException("Balance is too low");
        }

        final String orderId = getNewOrderId();
        final ShoppingCartPDFRequestType request = new ShoppingCartPDFRequestType();
        request.setUserToken(this.loginHandle.getUserToken());
        request.setShopOrderId(orderId);
        request.setPageFormatId(pageFormatId);

        final ShoppingCartPDFPosition position = new ShoppingCartPDFPosition();
        position.setProductCode(productCode);

        final NamedAddress sender = createNamedAddress(senderAddress);
        final NamedAddress receiver = createNamedAddress(receipiantAddress);

        final AddressBinding addressBinding = new AddressBinding();
        addressBinding.setSender(sender);
        addressBinding.setReceiver(receiver);
        position.setAddress(addressBinding);

        final VoucherPosition voucherPosition = new VoucherPosition();
        voucherPosition.setPage(1);
        voucherPosition.setLabelX(1);
        voucherPosition.setLabelY(1);
        position.setPosition(voucherPosition);
        position.setVoucherLayout(VoucherLayout.ADDRESS_ZONE);

        request.getPositions().add(position);
        request.setTotal(price);

        final ShoppingCartResponseType response = port.checkoutShoppingCartPDF(request);
        this.loginHandle.setWalletBalance(response.getWalletBallance());

        final Receipt receipt = new Receipt();
        receipt.setLink(response.getLink());
        receipt.setManifestLink(response.getManifestLink());
        receipt.setWalletBalance(response.getWalletBallance());
        receipt.setVoucherId(response.getShoppingCart().getVoucherList().getVoucher().get(0).getVoucherId());
        receipt.setTrackingId(response.getShoppingCart().getVoucherList().getVoucher().get(0).getTrackId());
        receipt.setOrderId(response.getShoppingCart().getShopOrderId());

        return receipt;
    }

    private static NamedAddress createNamedAddress(AddressDTO addressDTO) {
        final Address address = new Address();
        address.setCity(addressDTO.getCity());
        address.setZip(addressDTO.getZip());
        address.setCountry(addressDTO.getCountryCode());
        address.setStreet(addressDTO.getStreet());
        address.setAdditional(addressDTO.getAdditional());
        address.setHouseNo(addressDTO.getHouseNo());

        // We use the CompanyName, as otherwise we have to know first and last name,
        // however that is not always known, and gets more complicated when addressing a company
        // Therefore we always use company name
        final CompanyName companyName = new CompanyName();
        companyName.setCompany(addressDTO.getName());

        final Name name = new Name();
        name.setCompanyName(companyName);

        final NamedAddress namedAddress = new NamedAddress();
        namedAddress.setName(name);
        namedAddress.setAddress(address);
        return namedAddress;
    }
}



