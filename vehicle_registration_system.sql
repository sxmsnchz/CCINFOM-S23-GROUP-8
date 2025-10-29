CREATE DATABASE vehicle_registration_system;
USE vehicle_registration_system;

/*research abt actual LTO process/info needed*/

/*(SAM) add table and at least 10 records for owner. refer to eerd*/

/*(AYA) add vehicle and at least 10 records. refer to eerd*/

/*(JP) add officer and at least 10 records. refer to eerd*/

CREATE TABLE Branch (
    branch_id INT PRIMARY KEY,
    branch_name VARCHAR(100),
    street VARCHAR(100),
    barangay VARCHAR(100),
    city VARCHAR(50),
    province VARCHAR(50),
    postal_code VARCHAR(10),
    region VARCHAR(50)
);

INSERT INTO Branch VALUES
('1001', 'LTO Paranaque District Office', 'Olivarez Plaza', 'San Dionisio', 'Paranaque', 'Metro Manila', '1700', 'NCR'),
('1002', 'LTO Las Pinas District Office', 'Alabang Zapote', 'Talon Uno', 'Las Pinas', 'Metro Manila', '1747', 'NCR'),
('1003', 'LTO San Juan District Office', 'North Domingo', 'Rivera', 'San Juan', 'Metro Manila', '1500', 'NCR'),
('1004', 'LTO Pateros Extension Office', 'M. Amelda', 'San Roque', 'Pateros', 'Metro Manila', '1620', 'NCR'),
('1005', 'LTO Quezon City District Office', 'East Ave.', 'Pinyahan', 'Quezon City', 'Metro Manila', '1100', 'NCR'),
('1006', 'LTO Taguig Extension Office', 'Radiant St.', 'Western Bicutan', 'Taguig', 'Metro Manila', '1630', 'NCR'),
('1007', 'LTO Cebu City District Office', 'General Maxilom Ave.', 'Carreta', 'Cebu City', 'Cebu', '6000', 'Region VII'),
('1008', 'LTO Mandaue City District Office', 'Fortuna St.', 'Bakilid', 'Mandaue', 'Cebu', '6014', 'Region VII'),
('1009', 'LTO Baguio Office', 'Polo Field', 'Pacdal', 'Baguio', 'Benguet', '2600', 'CAR'),
('1010', 'LTO Regional Office XII - Koronadal City', 'Yellowbell', 'Sta. Cruz', 'Koronadal', 'South Cotabato', '9506', 'Region XII');


/*(JP AND AYA) create registration table with at least 10 records (refer to EERD) */



/*(SAM) create violation table with at least 10 records (refer to EERD & research on actual violation & costs)*/



CREATE TABLE Payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    violation_id INT,
    officer_id INT,
    branch_id INT,
    amount_paid DECIMAL(10,2),
    date_paid DATE,
    receipt_number VARCHAR(20),
    status VARCHAR(20),
    FOREIGN KEY (violation_id) REFERENCES Violation(violation_id),
    FOREIGN KEY (officer_id) REFERENCES Officer(officer_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
);

INSERT INTO Payment
(violation_id, officer_id, branch_id, amount_paid, date_paid, receipt_number, status) VALUES
(0001, 4, 1003, 5000.00, '2025-07-08', 'R003', 'Cleared'),
(0002, 4, 1003, 3000.00, '2025-07-20', 'R004', 'Cleared'),
(0213, 2, 1001, 3000.00, '2025-08-03', 'R005', 'Cleared'),
(0204, 2, 1001, 1000.00, '2025-08-04', 'R006', 'Cleared'),
(0008, 3, 1002, 2000.00, '2025-08-10', 'R007', 'Cleared'),
(0067, 5, 1005, 3000.00, '2025-09-12', 'R008', 'Cleared'),
(0192, 5, 1009, 2000.00, '2025-09-14', 'R009', 'Cleared'),
(0109, 1, 1002, 10000.00, '2025-09-20', 'R010', 'Cleared'),
(0490, 4, 1005, 1000.00, '2025-10-05', 'R011', 'Cleared'),
(0004, 3, 1003, 2000.00, '2025-10-08', 'R012', 'Cleared');






