package com.example.thirsttap.AddressesPage;

public class Address {
    private String barangay;
    private String street;
    private String building;
    private String unit;
    private String houseNum;
    private String additional;
    private String city;
    private String state;
    private String postal;

    // Constructor
    public Address(String barangay, String street, String building, String unit,
                   String houseNum, String additional, String city, String state, String postal) {
        this.barangay = barangay;
        this.street = street;
        this.building = building;
        this.unit = unit;
        this.houseNum = houseNum;
        this.additional = additional;
        this.city = city;
        this.state = state;
        this.postal = postal;
    }

    // Getters
    public String getBarangay() {
        return barangay;
    }

    public String getStreet() {
        return street;
    }

    public String getBuilding() {
        return building;
    }

    public String getUnit() {
        return unit;
    }

    public String getHouseNum() {
        return houseNum;
    }

    public String getAdditional() {
        return additional;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostal() {
        return postal;
    }

    public static String formatAddress(Address address) {
        if (address.getBuilding() == null || address.getBuilding().isEmpty()) {
            // Format for house
            return (address.getHouseNum() != null ? address.getHouseNum() + " " : "") +
                    (address.getStreet() != null ? address.getStreet() + ", " : "") +
                    (address.getBarangay() != null ? address.getBarangay() + ", " : "") +
                    (address.getCity() != null ? address.getCity() + ", " : "") +
                    (address.getState() != null ? address.getState() + ", " : "") +
                    (address.getPostal() != null ? address.getPostal() + " " : "") +
                    (address.getAdditional() != null ? address.getAdditional() : "");
        } else {
            // Format for building
            return (address.getBuilding() != null ? address.getBuilding() + " " : "") +
                    (address.getUnit() != null ? address.getUnit() + ", " : "") +
                    (address.getStreet() != null ? address.getStreet() + ", " : "") +
                    (address.getBarangay() != null ? address.getBarangay() + ", " : "") +
                    (address.getCity() != null ? address.getCity() + ", " : "") +
                    (address.getState() != null ? address.getState() + ", " : "") +
                    (address.getPostal() != null ? address.getPostal() + " " : "") +
                    (address.getAdditional() != null ? address.getAdditional() : "");
        }
    }

    public static String defaultAddress(Address address) {
        if (address.getBuilding() == null || address.getBuilding().isEmpty()) {
            // Format for house
            return (address.getHouseNum() != null ? address.getHouseNum() + " " : "") +
                    (address.getStreet() != null ? address.getStreet() + ", " : "") +
                    (address.getBarangay() != null ? address.getBarangay() + ", " : "") +
                    (address.getCity() != null ? address.getCity() + ", " : "") +
                    (address.getPostal() != null ? address.getPostal() + " " : "");
        } else {
            // Format for building
            return (address.getBuilding() != null ? address.getBuilding() + " " : "") +
                    (address.getUnit() != null ? address.getUnit() + ", " : "") +
                    (address.getStreet() != null ? address.getStreet() + ", " : "") +
                    (address.getBarangay() != null ? address.getBarangay() + ", " : "") +
                    (address.getCity() != null ? address.getCity() + ", " : "") +
                    (address.getPostal() != null ? address.getPostal() + " " : "");
        }
    }
}
