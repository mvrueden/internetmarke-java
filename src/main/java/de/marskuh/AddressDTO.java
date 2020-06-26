package de.marskuh;

import lombok.Data;

@Data
public class AddressDTO {
    private String name;
    private String city;
    private String houseNo;
    private String countryCode;
    private String street;
    private String zip;
    private String additional;

}
