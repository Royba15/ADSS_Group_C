package domain;

public enum TruckType {
    SMALL("B"),
    HEAVY("C");

    private String requiredLicenseType;

    TruckType(String requiredLicenseType) {
        this.requiredLicenseType = requiredLicenseType;
    }

    public String getRequiredLicenseType() {
        return requiredLicenseType;
    }
}
