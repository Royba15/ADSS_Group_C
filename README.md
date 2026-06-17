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
 # add here  

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
