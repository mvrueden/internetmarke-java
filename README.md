# Internetmarke Java

This project provides helper methods/classes to talk to two of the Deutsche Post's API:
 - ProdWS: lists all available product data
 - Internertmarke aka PCF-1Click: buy voucher's
 
## Requirements

You need to sign up on their website (https://www.deutschepost.de/de/i/internetmarke-porto-drucken/geschaeftskunden.html).
After you sent in their form, you will get the required details as well as test accounts.

In production you will also need to set up a Post Portokasse Account (https://portokasse.deutschepost.de/portokasse/#!/).


## Examples

```Java
final InternetmarkeConfiguration config = new InternetmarkeConfiguration();
// ... setup config

final InternetmarkeService internetmarkeService = new InternetmarkeService(config);
final Receipt receipt = internetmarkeService.placeSingleOrder(orderRequest.getProductCode(),
                        orderRequest.getPageFormatId(),
                        orderRequest.getPrice(),
                        orderRequest.getSenderAddress(),
                        orderRequest.getReceiverAddress());
                        
final ProdWSConfiguration config = new ProdWSConfiguration();
// ... setup config

final ProductService productService = new ProductService(config);
return productService.getProducts();
```

The ProdWS endpoint takes some time to respond and as the data does not change very often I would recommend to add a proxy which caches the calles to `productService.getProducts()` and evicts the data every 8-24 hours, maybe even re-initializes. Should be fairly easy to achieve with Guava's cache or basic interface proxying.

## How to integrate

I am using this in a spring boot application.
My configuration looks something like this

```Java
@Configuration
public class InternetmarkeConfiguration {

    @Bean
    @Autowired
    public ProductService createProductService(ProdWSConfiguration config) {
        Objects.requireNonNull(config);
        return new ProductService(config);
    }

    @Bean
    public ProdWSConfiguration createProdWSConfiguration(
            @Value("${internetmarke.soap.logging}") boolean logSoapMessages,
            @Value("${internetmarke.prodws.username}") String username,
            @Value("${internetmarke.prodws.password}") String password,
            @Value("${internetmarke.prodws.mandantId}") String mandantId
    ) {
        final ProdWSConfiguration config = new ProdWSConfiguration();
        config.setLogSoapMessages(logSoapMessages);
        config.setMandantId(mandantId);
        config.setPassword(password);
        config.setUsername(username);
        return config;
    }

    @Bean
    @Autowired
    public InternetmarkeService createInternetmarkeService(InternetmarkeServiceConfiguration config) {
        Objects.requireNonNull(config);
        return new InternetmarkeService(config);
    }

    @Bean
    public InternetmarkeServiceConfiguration createInternetmarkeConfiguration(
            @Value("${internetmarke.soap.logging}") boolean logSoapMessages,
            @Value("${internetmarke.prodws.partnerId}") String partnerId,
            @Value("${internetmarke.prodws.partnerSignature}") String partnerSignature,
            @Value("${internetmarke.portokasse.username}") String portokasseUsername,
            @Value("${internetmarke.portokasse.password}") String portokassePassword
    ) {
        final InternetmarkeServiceConfiguration config = new InternetmarkeServiceConfiguration();
        config.setLogSoapMessages(logSoapMessages);
        config.setPartnerId(partnerId);
        config.setPartnerSignature(partnerSignature);
        config.setPortokassePassword(portokassePassword);
        config.setPortokasseUsername(portokasseUsername);
        return config;
    }
}
```

The according properties look something like this:

```properties
internetmarke.portokasse.username=DEFINE_ME
internetmarke.portokasse.password=DEFINE_ME
internetmarke.prodws.mandantId=DEFINE_ME
internetmarke.prodws.username=DEFINE_ME
internetmarke.prodws.password=DEFINE_ME
internetmarke.prodws.partnerId=DEFINE_ME
internetmarke.prodws.partnerSignature=DEFINE_ME
internetmarke.soap.logging=true
```


## Disclaimer

This is not production ready yet.
It certainly gets the job done, but under the hood lacks a lot of hardening, so please DO NOT USE this in production.

## License

See License => GPL v3.

## Contribution

Contribution welcome, just open a PR or send me a message
