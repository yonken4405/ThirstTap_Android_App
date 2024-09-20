package com.example.thirsttap.AddressesPage;

public class HouseAddress {
    private String street;
    private String houseNumber;
    private String additionalInfo;
    private String city;
    private String postalCode;
    private boolean isDefault;
    private int addressId; // Use address_id

    // Constructor
    public HouseAddress(String street, String houseNumber, String additionalInfo,
                        String city, String postalCode, boolean isDefault, int addressId) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.additionalInfo = additionalInfo;
        this.city = city;
        this.postalCode = postalCode;
        this.isDefault = isDefault;
        this.addressId = addressId;
    }

    // Getters
    public int getAddressId() { return addressId; }
    public String getStreet() { return street; }
    public String getHouseNumber() { return houseNumber; }
    public String getAdditionalInfo() { return additionalInfo; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public boolean isDefault() { return isDefault; }

    // Setters
    public void setStreet(String street) { this.street = street; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
    public void setCity(String city) { this.city = city; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    @Override
    public String toString() {
        return "HouseAddress{" +
                "street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
