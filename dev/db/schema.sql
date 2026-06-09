PRAGMA foreign_keys = ON;

-- Stores company branches.
-- Each employee belongs to exactly one branch.
CREATE TABLE IF NOT EXISTS branches (
    id TEXT PRIMARY KEY
);

-- Stores the base details of every employee in the system.
-- Driver-specific data is stored separately in the drivers table.
CREATE TABLE IF NOT EXISTS employees (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    start_date TEXT NOT NULL,
    salary REAL NOT NULL,
    employment_conditions TEXT NOT NULL,
    bank_account TEXT NOT NULL,
    branch_id TEXT NOT NULL,
    is_active INTEGER NOT NULL DEFAULT 1,
    employee_type TEXT NOT NULL DEFAULT 'EMPLOYEE',
    FOREIGN KEY (branch_id) REFERENCES branches(id)
);

-- Stores extra details for employees who are drivers.
-- employee_id is also the primary key, because every driver is exactly one employee.
CREATE TABLE IF NOT EXISTS drivers (
    employee_id TEXT PRIMARY KEY,
    license_type TEXT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- Stores all role names supported by the system.
-- Default roles are inserted at the end of this file.
CREATE TABLE IF NOT EXISTS roles (
    name TEXT PRIMARY KEY
);

-- Connects employees to their roles.
-- An employee can have multiple roles, and each role can belong to many employees.
CREATE TABLE IF NOT EXISTS employee_roles (
    employee_id TEXT NOT NULL,
    role_name TEXT NOT NULL,
    PRIMARY KEY (employee_id, role_name),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (role_name) REFERENCES roles(name)
);

-- Stores an employee availability submission.
-- The selected shift slots for the submission are stored in availability_shift_slots.
CREATE TABLE IF NOT EXISTS availability_submissions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- Stores the shift slots included in an availability submission.
-- Each row represents one day and shift type the employee can work.
CREATE TABLE IF NOT EXISTS availability_shift_slots (
    submission_id INTEGER NOT NULL,
    day_of_week TEXT NOT NULL,
    shift_type TEXT NOT NULL,
    PRIMARY KEY (submission_id, day_of_week, shift_type),
    FOREIGN KEY (submission_id) REFERENCES availability_submissions(id) ON DELETE CASCADE
);

-- Stores weekly schedules.
-- Includes the weekly availability submission deadline.
CREATE TABLE IF NOT EXISTS weekly_schedules (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    week_start_date TEXT,
    submission_day TEXT NOT NULL,
    submission_time TEXT NOT NULL
);

-- Stores which weekly schedule is currently active in the system.
-- There is only one current schedule at a time.
CREATE TABLE IF NOT EXISTS current_schedule (
    id INTEGER PRIMARY KEY CHECK (id = 1),
    weekly_schedule_id INTEGER NOT NULL,
    FOREIGN KEY (weekly_schedule_id) REFERENCES weekly_schedules(id) ON DELETE CASCADE
);

-- Stores the actual shifts that belong to a weekly schedule.
-- Each shift has a day, shift type, optional manager, and blocked status.
CREATE TABLE IF NOT EXISTS shifts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    weekly_schedule_id INTEGER,
    day_of_week TEXT NOT NULL,
    shift_type TEXT NOT NULL,
    manager_id TEXT,
    blocked INTEGER NOT NULL DEFAULT 0,
    UNIQUE (weekly_schedule_id, day_of_week, shift_type),
    FOREIGN KEY (weekly_schedule_id) REFERENCES weekly_schedules(id) ON DELETE CASCADE,
    FOREIGN KEY (manager_id) REFERENCES employees(id)
);

-- Stores the required number of employees per role for each shift.
-- For example: one CASHIER, one STOCKER, or one DRIVER when there is a delivery.
CREATE TABLE IF NOT EXISTS staffing_requirements (
    shift_id INTEGER NOT NULL,
    role_name TEXT NOT NULL,
    required_amount INTEGER NOT NULL,
    PRIMARY KEY (shift_id, role_name),
    FOREIGN KEY (shift_id) REFERENCES shifts(id) ON DELETE CASCADE,
    FOREIGN KEY (role_name) REFERENCES roles(name)
);

-- Stores employees assigned to shifts.
-- Each assignment records the employee and the role they perform in that shift.
CREATE TABLE IF NOT EXISTS shift_assignments (
    shift_id INTEGER NOT NULL,
    employee_id TEXT NOT NULL,
    role_name TEXT NOT NULL,
    PRIMARY KEY (shift_id, employee_id),
    FOREIGN KEY (shift_id) REFERENCES shifts(id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (role_name) REFERENCES roles(name)
);

-- Stores truck types and the license required to drive each type.
-- For example: SMALL requires B, HEAVY requires C.
CREATE TABLE IF NOT EXISTS truck_types (
    name TEXT PRIMARY KEY,
    required_license_type TEXT NOT NULL
);

-- Stores deliveries attached to shifts.
-- A delivery creates a driver requirement for the shift and has a truck type.
CREATE TABLE IF NOT EXISTS deliveries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shift_id INTEGER NOT NULL,
    truck_type TEXT NOT NULL,
    FOREIGN KEY (shift_id) REFERENCES shifts(id) ON DELETE CASCADE,
    FOREIGN KEY (truck_type) REFERENCES truck_types(name)
);

-- Stores closed weekly schedules by their start date.
-- Used for viewing previous shift schedules.
CREATE TABLE IF NOT EXISTS shift_history (
    start_of_week TEXT PRIMARY KEY,
    weekly_schedule_id INTEGER NOT NULL,
    FOREIGN KEY (weekly_schedule_id) REFERENCES weekly_schedules(id)
);

-- Default system roles.
INSERT OR IGNORE INTO roles(name) VALUES ('CASHIER');
INSERT OR IGNORE INTO roles(name) VALUES ('STOCKER');
INSERT OR IGNORE INTO roles(name) VALUES ('DRIVER');
INSERT OR IGNORE INTO roles(name) VALUES ('SHI FT_MANAGER');

-- Default truck types used by deliveries.
INSERT OR IGNORE INTO truck_types(name, required_license_type) VALUES ('SMALL', 'B');
INSERT OR IGNORE INTO truck_types(name, required_license_type) VALUES ('HEAVY', 'C');
