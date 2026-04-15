package domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

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

    // List of roles the employee is qualified to perform (e.g., CASHIER, STOCKER)
    private Set<String> roles;

    // List of shift slots the employee is available to work
    private List<ShiftSlot> availableShifts;

    // Indicates whether the employee is currently active
    private boolean isActive;
    private boolean submittedAvailability = false;

    public Employee(String id, String name, LocalDate startDate,
                    double salary, String employmentConditions, String bankAccount) {

        this.id = id;
        this.name = name;
        this.startDate = startDate;

        this.salary = salary;
        this.employmentConditions = employmentConditions;
        this.bankAccount = bankAccount;

        // Initialize lists to avoid null references
        this.roles = new HashSet<>();
        this.availableShifts = new ArrayList<>();
        this.isActive = true; // default: employee is active
    }
    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    // Returns the list of roles the employee can perform
    public Set<String> getRoles() {
        return roles;
    }

    // Returns the list of shifts the employee is available for
    public List<ShiftSlot> getAvailableShifts() {
        return availableShifts;
    }

    // Add a role to the employee
    public void addRole(String role) {
        roles.add(role.toUpperCase());
    }

    // Add an available shift for the employee
    public void addAvailableShift(ShiftSlot slot) {
        availableShifts.add(slot);
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
        return submittedAvailability;
    }
    public void setSubmittedAvailability(boolean submittedAvailability) {
        this.submittedAvailability = submittedAvailability;
    }
}