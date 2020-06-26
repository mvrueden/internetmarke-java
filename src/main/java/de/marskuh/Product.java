package de.marskuh;

import lombok.Data;

@Data
public class Product {

    private String id;
    private String prodWSId;
    private String destination;
    private String name;
    private String description;
    private String annotation;
    private String branche;
    private String transport;
    private String type;
    private String state;
    private String minLength;
    private String maxLength;
    private String minHeight;
    private String maxHeight;
    private String minWidth;
    private String maxWidth;
    private String minWeight;
    private String maxWeight;
    private boolean trackingPossible;
    private String allowedForm;
    private boolean nachsendenPossible;
    private boolean haftungPossible;
    private float minRatio;
    private Price price;

}
