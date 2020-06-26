package de.marskuh;

import lombok.Data;

@Data
public class Receipt {
    private String voucherId;
    private String trackingId;
    private String orderId;
    private String link;
    private String manifestLink;
    private int walletBalance;
}
