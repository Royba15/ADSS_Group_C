# ADSS Assignment 2

Name: Roy Barak
ID: 211467246

Name: Tomer Tirosh
ID: 211604319

Name: Yuval Asulin
ID: 323840728

Name: Shir Stolero
ID: 206692360

## Project Description

This project was implemented as part of ADSS Assignment 2.

The project models a supermarket management system, focusing on two main domains:

* Inventory management model
* Employee management model

The system represents supermarket entities, their attributes, and the relationships between them according to the assignment requirements.

## Modeling Tool

The system models and diagrams were created using draw.io.

## Project Structure

* `dev/src/` - contains the Java source files of the project.
* `README.md` - contains general information about the project, the tools used, and instructions for running it.
* Modeling files / diagrams - contain the supermarket system models created with draw.io.

## How to Run

Workers Module:

SuperLi System – User Guide
Overview
SuperLi is a console-based system for managing employees, roles, availability submissions, and weekly shift assignments.

The system supports two user types:

HR Manager
Employee
Starting the System
Run the Main class.

At startup, the system asks whether to load sample data:

1 - Yes – load predefined sample data
2 - No – start with an empty system
Main Menu
After startup, the following menu is displayed:

1 - HR Manager
2 - Employee
3 - Exit System
HR Manager Functions
To enter HR mode, select HR Manager and enter the password.

Default password:

1234
The HR Manager can:

change the availability submission deadline
add new employees
view all employees
manage roles
manage shifts
assign employees and shift managers
change staffing requirements
block shifts
view the current weekly schedule
view shift history
close the current week and start a new one
mark employees as inactive
Shift Assignment Rules
When assigning an employee, the system checks that the employee:

exists
is active
is qualified for the selected role
is available for the shift
is not being assigned to a blocked shift
For drivers, the system also verifies that the driving license matches the required delivery truck type.

Employee Functions
To enter employee mode, select Employee and enter the employee ID.

An employee can:

submit availability for shifts
view the latest weekly schedule
Availability submission is allowed only before the deadline and only for valid, unblocked shifts.

Additional Notes
Saturday is not a working day in the system.
Blocked shifts are treated as closed.
Roles can be added dynamically by the HR Manager.
Weekly schedules are saved and previous weeks can be viewed through the history menu.
System data is stored persistently between runs unless the database is cleared.
Exiting the System
To close the system completely, return to the main menu and select:

3 - Exit System

Inventory Module:

SuperLi System – Inventory User Guide
Overview
The Inventory Module manages supermarket stock, defective items, product categories, promotions, and supplier orders. It tracks both shelf and warehouse quantities and supports automatic reordering when stock drops below minimum thresholds.

The system interacts directly with a local SQLite database (`inventory.db`) to ensure all data is persistent between runs.

Starting the System
Run the `InventoryMenu` (or start via the Main module).
At startup, the system asks whether to initialize the database:
1 - Use existing data (or load default seed data if the database is empty)
2 - Clear database and start fresh (Warning: This deletes all inventory records)

Main Menu
After startup, the following Inventory Management menu is displayed:

1. Update Inventory
2. View Product by ID
3. Alerts
4. Reports
5. Apply Discount
6. Report Defective Product
7. Add New Category
8. Add New Product
9. Delete Product
10. Create Supplier Order
11. Receive Shipment
0. Exit

Inventory Functions
An authorized user can:
* **Manage Stock:** Adjust shelf and warehouse quantities for existing products (Option 1).
* **View Alerts:** Check for products that have dropped below their defined minimum quantity threshold (Option 3).
* **Generate Reports:** Access categorical inventory reports, defective item logs, and supplier order histories (Option 4).
* **Manage Promotions:** Apply discount percentages to products for specific date ranges (Option 5).
* **Report Defective Items:** Log damaged or expired products, removing them from active inventory (Option 6).

Supplier Orders & Automation
* **Manual Orders:** Create immediate or scheduled orders (Once, Weekly, Monthly) from specific suppliers (Option 10).
* **Automatic Orders:** The system routinely checks inventory levels. If a product drops below its threshold, the module automatically queries the Supplier System for the best supplier and generates a `PENDING` order.
* **Receive Shipment:** When physical stock arrives, mark pending orders as received to automatically update the warehouse quantities (Option 11).

Additional Notes
* Products are categorized using a 3-level hierarchy (Main, Sub, Sub-Sub).
* To close the inventory system and return to the main wrapper, select `0 - Exit`.

## Requirements

* Java
* IntelliJ IDEA or another Java-supporting IDE
* draw.io for viewing or editing the modeling diagrams, if needed

## Notes

* The project was written in Java.
* The project was developed and tested using IntelliJ IDEA.
* The models and diagrams were created using draw.io.
* No external Java libraries are required unless specified in the assignment instructions.
* All source files should remain in their original project structure.
