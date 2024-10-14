package com.example.thirsttap.AddressesPage;

public class BuildingAddress {
    private String street;
    private String buildingName;
    private String unitNumber;
    private String additionalInfo;
    private String city;
    private String postalCode;
    private boolean isDefault;
    private int addressId; // Use address_id
    private String barangay;

    // Constructor
    public BuildingAddress(String street, String buildingName, String unitNumber,
                           String additionalInfo, String city, String postalCode,
                           boolean isDefault, int addressId, String barangay) {
        this.street = street;
        this.buildingName = buildingName;
        this.unitNumber = unitNumber;
        this.additionalInfo = additionalInfo;
        this.city = city;
        this.postalCode = postalCode;
        this.isDefault = isDefault;
        this.addressId = addressId;
        this.barangay = barangay;
    }


    // Getters
    public int getAddressId() { return addressId; }
    public String getStreet() { return street; }
    public String getBuildingName() { return buildingName; }
    public String getUnitNumber() { return unitNumber; }
    public String getAdditionalInfo() { return additionalInfo; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public boolean isDefault() { return isDefault; }
    public String getBarangay() { return barangay; }

    // Setters
    public void setStreet(String street) { this.street = street; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public void setUnitNumber(String unitNumber) { this.unitNumber = unitNumber; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
    public void setCity(String city) { this.city = city; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    public void setBarangay(String barangay) { this.barangay = barangay; }

    @Override
    public String toString() {
        return "BuildingAddress{" +
                "street='" + street + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", unitNumber='" + unitNumber + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
