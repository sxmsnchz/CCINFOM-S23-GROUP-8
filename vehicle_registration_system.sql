CREATE DATABASE vehicle_registration_system;
USE vehicle_registration_system;

/*research abt actual LTO process/info needed*/

/*(SAM) add table and at least 10 records for owner. refer to eerd*/
CREATE TABLE Owner (
	owner_id INT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    street VARCHAR(100),
    barangay VARCHAR(100),
    city VARCHAR(50),
    province VARCHAR(50),
    region VARCHAR(50),
    postal_code VARCHAR(10),
    password VARCHAR(50),
    license_number VARCHAR(13)
);
    
INSERT INTO Owner VALUES
('0001', 'Samantha', 'Sanchez', 'Jennys Avenue', 'Maybunga', 'Pasig City', 'Metro Manila', 'NCR', '1607', 'password123', 'N03-24-013583'),
('0002', 'Myrna', 'Sanchez', 'Jennys Avenue', 'Maybunga', 'Pasig City', 'Metro Manila', 'NCR', '1607', 'P@SSWORD', 'D16-97-169064'),
('0003', 'Patrick', 'Perez', 'Dona Lucia St.', 'Commonwealth', 'Quezon City', 'Metro Manila', 'NCR', '1121', '123password!', 'N25-23-011859'),
('0004', 'Fiona', 'Tano', 'M. Vicente St.', 'Malamig', 'Mandaluyong City', 'Metro Manila', 'NCR', '1550', 'password!!!', 'N01-24-023812'),
('0005', 'Jeck', 'Sanchez', 'Arguelles St.', 'Pio Del Pilar', 'Makati City', 'Metro Manila', 'NCR', '1230', 'passwordkohehe', 'D37-23-004584'),
('0006', 'Gabriel', 'Avila', 'J. Rizal St.', 'Poblacion', 'Pateros', 'Metro Manila', 'NCR', '1620', 'thisismypassword', 'N03-18-024438'),
('0007', 'Megan', 'Dasal', 'Alice Crisostomo St.', 'Talon Dos', 'Las Pinas City', 'Metro Manila', 'NCR', '1747', 'p@ssword12345!!', 'N03-12-123456'),
('0008', 'Alexa', 'Pleyto', '3rd St.', 'St. Ignatius', 'Quezon City', 'Metro Manila', 'NCR', '1110', 'pass123word', 'N02-25-026327'),
('0009', 'Lara', 'Turk', 'Rome St.', 'B.F. International Village', 'Las Pinas City', 'Metro Manila', 'NCR', '1740', 'passw0rd123', 'N04-10-021355'),
('0010', 'Rohann', 'Dizon', 'Leon Guinto St.', 'Malate', 'Manila', 'Metro Manila', 'NCR', '1004', 'p4ssw0rd123!!', 'N01-12-123456');


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
CREATE TABLE Violation (
	violation_id INT PRIMARY KEY,
    violation_type VARCHAR(150),
    status VARCHAR(20),
    fine_amount DECIMAL(10,2),
    violation_date DATE
);

INSERT INTO Violation VALUES
('120', 'Expired License', 'Cleared', '3000.00', '2025-10-27'),
('121', 'Reckless Driving', 'Unpaid', '2000.00', '2025-09-22'),
('122', 'Unregistered Motor Vehicle', 'Cleared', '10000.00', '2025-04-15'),
('123', 'Smoke Belching', 'Cleared', '2000.00', '2025-02-13'),
('124', 'Unauthorized Modification', 'Unpaid', '5000.00', '2025-10-01'),
('125', 'Defective Parts', 'Cleared', '5000.00', '2025-08-23'),
('126', 'Expired Registration', 'Unpaid', '2000.00', '2025-04-15'),
('127', 'No Seatbelt', 'Cleared', '1000.00', '2025-01-29'),
('128', 'Unregistered Motor Vehicle', 'Unpaid', '10000.00', '2025-07-31'),
('129', 'Expired License', 'Unpaid', '3000.00', '2025-06-05'),
('130', 'Defective Parts', 'Unpaid', '5000.00', '2025-03-04');


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






