package HR.domain;

import java.time.LocalDate;

public class Driver extends Employee {

    private String licenseType;

    public Driver(String id, String name, LocalDate startDate,
                  double salary, String employmentConditions, String bankAccount,
                  Branch branch, String licenseType) {

        super(id, name, startDate, salary, employmentConditions, bankAccount, branch);
        this.licenseType = licenseType;
        addRole("DRIVER");
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }
}
