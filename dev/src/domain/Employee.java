package domain;

import java.time.LocalDate;
import java.util.*;

public class Employee {

    // Unique identifier of the employee (e.g., ID number)
    private String id;

    // Full name of the employee
    private String name;

    // The date the employee started working in the company
    private LocalDate startDate;

    // Employee salary
    private double salary;

    // Employment terms (e.g., full-time, part-time, contract details)
    private String employmentConditions;

    // Bank account details used for salary payments
    private String bankAccount;

    // The branch this employee belongs to
    private Branch branch;

    // List of roles the employee is qualified to perform (e.g., CASHIER, STOCKER)
    private Set<String> roles;

    // Availability submission for the current week
    private AvailabilitySubmission availabilitySubmission;

    // Indicates whether the employee is currently active
    private boolean isActive;

    public Employee(String id, String name, LocalDate startDate,
                    double salary, String employmentConditions, String bankAccount,
                    Branch branch) {

        this.id = id;
        this.name = name;
        this.startDate = startDate;

        this.salary = salary;
        this.employmentConditions = employmentConditions;
        this.bankAccount = bankAccount;
        this.branch = branch;

        // Initialize lists to avoid null references
        this.roles = new LinkedHashSet<>();
        this.availabilitySubmission = null;
        this.isActive = true; // default: employee is active
    }
    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public double getSalary() {
        return salary;
    }
    public String getEmploymentConditions() {
        return employmentConditions;
    }
    public String getBankAccount() {
        return bankAccount;
    }
    public Branch getBranch() {
        return branch;
    }
    public void setBranch(Branch branch) {
        this.branch = branch;
    }
    // Returns the list of roles the employee can perform
    public Set<String> getRoles() {
        return roles;
    }

    // Returns the availability submission entity
    public AvailabilitySubmission getAvailabilitySubmission() {
        return availabilitySubmission;
    }

    // Add a role to the employee
    public void addRole(String role) {
        roles.add(role.toUpperCase());
    }

    // Add an available shift for the employee
    public void addAvailableShift(ShiftSlot slot) {
        if (availabilitySubmission == null) {
            availabilitySubmission = new AvailabilitySubmission();
        }

        availabilitySubmission.addAvailableShift(slot);
    }
    // Returns whether the employee is active
    public boolean isActive() {
        return isActive;
    }
    // Activate or deactivate employee
    public void setActive(boolean active) {
        this.isActive = active;
    }
    public boolean hasSubmittedAvailability() {
        return availabilitySubmission != null;
    }
    public void setSubmittedAvailability(boolean submittedAvailability) {
        if (submittedAvailability && availabilitySubmission == null) {
            availabilitySubmission = new AvailabilitySubmission();
        }

        if (!submittedAvailability) {
            availabilitySubmission = null;
        }
    }

    public boolean isAvailableFor(ShiftSlot slot) {
        return !hasSubmittedAvailability() || availabilitySubmission.isAvailableFor(slot);
    }

    public void clearAvailabilitySubmission() {
        availabilitySubmission = null;
    }
}
