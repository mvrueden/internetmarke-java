package de.marskuh.internetmarke.endpoints.prodws;

import com.google.common.collect.Lists;
import de.marskuh.Price;
import de.marskuh.Product;
import de.printbird3d.prodws.GetProductListResponseType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Service to get all available products
public class ProductService {

    private final ProdWSConfiguration config;

    public ProductService(ProdWSConfiguration configuration) {
        this.config = Objects.requireNonNull(configuration);
    }

    public List<Product> getProducts() {
        final GetProductListResponseType productList = new ProdWSEndpointClient(config).getProductList();
        if (productList.getSalesProductList() != null && productList.getSalesProductList().getSalesProduct() != null) {
            final List<Product> theList = productList.getSalesProductList().getSalesProduct().stream().map(prodWsProduct -> {
                final Product product = new Product();
                product.setProdWSId(prodWsProduct.getExtendedIdentifier().getProdWSID());
                product.setDestination(prodWsProduct.getExtendedIdentifier().getDestination());
                product.setName(prodWsProduct.getExtendedIdentifier().getName());
                product.setDescription(prodWsProduct.getExtendedIdentifier().getDescription());
                product.setAnnotation(prodWsProduct.getExtendedIdentifier().getAnnotation());
                product.setBranche(prodWsProduct.getExtendedIdentifier().getBranche());
                product.setTransport(prodWsProduct.getExtendedIdentifier().getTransport());
                product.setType(prodWsProduct.getExtendedIdentifier().getType());
                product.setState(prodWsProduct.getExtendedIdentifier().getState());

                if(prodWsProduct.getDimensionList() != null) {
                    product.setMinLength(prodWsProduct.getDimensionList().getLength().getMinValue() + prodWsProduct.getDimensionList().getLength().getUnit());
                    product.setMaxLength(prodWsProduct.getDimensionList().getLength().getMaxValue() + prodWsProduct.getDimensionList().getLength().getUnit());
                    product.setMinHeight(prodWsProduct.getDimensionList().getHeight().getMinValue() + prodWsProduct.getDimensionList().getLength().getUnit());
                    product.setMaxHeight(prodWsProduct.getDimensionList().getHeight().getMaxValue() + prodWsProduct.getDimensionList().getLength().getUnit());
                    product.setMinWidth(prodWsProduct.getDimensionList().getWidth().getMinValue() + prodWsProduct.getDimensionList().getLength().getUnit());
                    product.setMaxWidth(prodWsProduct.getDimensionList().getWidth().getMaxValue() + prodWsProduct.getDimensionList().getLength().getUnit());
                }

                if(prodWsProduct.getWeight() != null) {
                    product.setMinWeight(prodWsProduct.getWeight().getMinValue().toString() + prodWsProduct.getWeight().getUnit());
                    product.setMaxWeight(prodWsProduct.getWeight().getMaxValue().toString() + prodWsProduct.getWeight().getUnit());
                }

                if(prodWsProduct.getPropertyList() != null) {
                    prodWsProduct.getPropertyList().getProperty().forEach(propertyType -> {
                        if (propertyType.getName().equals("extProp_Sendungsverfolgung")) {
                            product.setTrackingPossible(propertyType.getPropertyValue().isBooleanValue());
                        }
                        if (propertyType.getName().equals("AllowedForm")) {
                            product.setAllowedForm(propertyType.getPropertyValue().getAlphanumericValue().getFixValue());
                        }
                        if (propertyType.getName().equals("extProp_Nachsenden")) {
                            product.setNachsendenPossible(propertyType.getPropertyValue().isBooleanValue());
                        }
                        if (propertyType.getName().equals("MinRatio")) {
                            product.setMinRatio(propertyType.getPropertyValue().getNumericValue().getFixValue().floatValue());
                        }
                        if (propertyType.getName().equals("extProp_Haftung")) {
                            product.setHaftungPossible(propertyType.getPropertyValue().isBooleanValue());
                        }
                    });
                }
                if(!prodWsProduct.getExtendedIdentifier().getExternIdentifier().isEmpty()) {
                    product.setId(prodWsProduct.getExtendedIdentifier().getExternIdentifier().get(0).getId());
                }
                final Price price = new Price();
                price.setValue(prodWsProduct.getPriceDefinition().getPrice().getCalculatedGrossPrice().getValue().floatValue());
                price.setCurrency(prodWsProduct.getPriceDefinition().getPrice().getCalculatedGrossPrice().getCurrency());
                product.setPrice(price);

                return product;
            })
            .collect(Collectors.toList());
            return theList;
        }
        return Lists.newArrayList();
    }
}
