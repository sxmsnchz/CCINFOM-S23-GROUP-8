CREATE DATABASE vehicle_registration_system;
USE vehicle_registration_system;

/*research abt actual LTO process/info needed*/

/*(AYA) add table and at least 10 records. refer to eerd*/
CREATE TABLE Vehicle (
	vehicle_id INT,
    plate_no VARCHAR(7) UNIQUE NOT NULL,
	manufacture_date DATE,
    mv_file_no INT(15) UNIQUE NOT NULL,
    chassis_no VARCHAR(17) UNIQUE NOT NULL,
    engine_no VARCHAR(12) UNIQUE NOT NULL,
    make VARCHAR(15),
    series VARCHAR(15),
    color VARCHAR(15),
    PRIMARY KEY (vehicle_id)
);

INSERT INTO Vehicle VALUES
(1001, 123450, 'NAA1234', '2018-06-15', 230145678912345, 'MA3EKEB1S00567891', 'ENG123456789', 'Toyota', 'Vios', 'Silver'),
(1002, 123451, 'BAE4567', '2020-03-22', 230198765412345, 'PA2BLFB1T00891234', 'ENG234567890', 'Honda', 'City', 'White'),
(1003, 123452, 'CAZ7890', '2019-11-05', 230123498712345, 'NB1ZDFB1S00234567', 'ENG345678901', 'Mitsubishi', 'Mirage G4', 'Red'),
(1004, 123453, 'DAN2345', '2017-08-17', 230145612378945, 'MA3HDEB1S00789123', 'ENG456789012', 'Hyundai', 'Accent', 'Blue'),
(1005, 123454, 'EAP5678', '2021-01-10', 230167894512376, 'PA1JKLB1T00456289', 'ENG567890123', 'Nissan', 'Almera', 'Gray'),
(1006, 123455, 'FAA9012', '2022-04-25', 230198754612347, 'MA2GHEB1S00123849', 'ENG678901234', 'Suzuki', 'Dzire', 'White'),
(1007, 123456, 'GAB3456', '2015-09-12', 230187965412398, 'PA3LKEB1T00345678', 'ENG789012345', 'Ford', 'EcoSport', 'Black'),
(1008, 123457, 'HAI6789', '2016-05-30', 230134987512398, 'MA2QWEB1S00478912', 'ENG890123456', 'Chevrolet', 'Spark', 'Yellow'),
(1009, 123458, 'JAX0123', '2023-02-11', 230176543219084, 'NB3UIEB1S00981234', 'ENG901234567', 'Kia', 'Soluto', 'White'),
(1010, 123459, 'KAM4567', '2018-10-21', 230145987654321, 'PA2TREB1T00567891', 'ENG012345678', 'Toyota', 'Corolla Altis', 'Gray');


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
('123450', 'Samantha', 'Sanchez', 'Jennys Avenue', 'Maybunga', 'Pasig City', 'Metro Manila', 'NCR', '1607', 'password123', 'N03-24-013583'),
('123451', 'Myrna', 'Sanchez', 'Jennys Avenue', 'Maybunga', 'Pasig City', 'Metro Manila', 'NCR', '1607', 'P@SSWORD', 'D16-97-169064'),
('123452', 'Patrick', 'Perez', 'Dona Lucia St.', 'Commonwealth', 'Quezon City', 'Metro Manila', 'NCR', '1121', '123password!', 'N25-23-011859'),
('123453', 'Fiona', 'Tano', 'M. Vicente St.', 'Malamig', 'Mandaluyong City', 'Metro Manila', 'NCR', '1550', 'password!!!', 'N01-24-023812'),
('123454', 'Jeck', 'Sanchez', 'Arguelles St.', 'Pio Del Pilar', 'Makati City', 'Metro Manila', 'NCR', '1230', 'passwordkohehe', 'D37-23-004584'),
('123455', 'Gabriel', 'Avila', 'J. Rizal St.', 'Poblacion', 'Pateros', 'Metro Manila', 'NCR', '1620', 'thisismypassword', 'N03-18-024438'),
('123456', 'Megan', 'Dasal', 'Alice Crisostomo St.', 'Talon Dos', 'Las Pinas City', 'Metro Manila', 'NCR', '1747', 'p@ssword12345!!', 'N03-12-123456'),
('123457', 'Alexa', 'Pleyto', '3rd St.', 'St. Ignatius', 'Quezon City', 'Metro Manila', 'NCR', '1110', 'pass123word', 'N02-25-026327'),
('123458', 'Lara', 'Turk', 'Rome St.', 'B.F. International Village', 'Las Pinas City', 'Metro Manila', 'NCR', '1740', 'passw0rd123', 'N04-10-021355'),
('123459', 'Rohann', 'Dizon', 'Leon Guinto St.', 'Malate', 'Manila', 'Metro Manila', 'NCR', '1004', 'p4ssw0rd123!!', 'N01-12-123456');


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

/*(JP) add officer and at least 10 records. refer to eerd*/

/*(JP AND AYA) create registration table with at least 10 records (refer to EERD) */
/*Note: we add payment_id to registration right? or not?*/
CREATE TABLE registration (
	registration_id INT,
    vehicle_id INT,
    owner_id INT, 
    payment_id INT,
    branch_id INT,
    first_date_registered DATE,
    current_date_registered DATE,
    expiry_date DATE,
    status VARCHAR(20),
    PRIMARY KEY (registration_id, vehicle_id, owner_id, payment_id, branch_id),
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id),
    FOREIGN KEY (owner_id) REFERENCES Owner(owner_id),
	FOREIGN KEY (payment_id) REFERENCES Payment(payment_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
);


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






