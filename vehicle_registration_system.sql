CREATE DATABASE vehicle_registration_system;
USE vehicle_registration_system;

/*=======================================
				VEHICLE TABLE
=========================================
*/
CREATE TABLE Vehicle (
	vehicle_id INT,
    plate_no VARCHAR(7) NOT NULL,
	manufacture_date DATE,
    mv_file_no BIGINT NOT NULL,
    chassis_no VARCHAR(17) NOT NULL,
    engine_no VARCHAR(12) NOT NULL,
    make VARCHAR(15),
    series VARCHAR(15),
    color VARCHAR(15),
    PRIMARY KEY (vehicle_id),
    UNIQUE(plate_no, mv_file_no, chassis_no, engine_no)
);

INSERT INTO Vehicle VALUES
(0001, 'NAA1234', '2018-06-15', 230145678912345, 'MA3EKEB1S00567891', 'ENG123456789', 'Toyota', 'Vios', 'Silver'),
(0002, 'BAE4567', '2020-03-22', 230198765412345, 'PA2BLFB1T00891234', 'ENG234567890', 'Honda', 'City', 'White'),
(0003, 'CAZ7890', '2019-11-05', 230123498712345, 'NB1ZDFB1S00234567', 'ENG345678901', 'Mitsubishi', 'Mirage G4', 'Red'),
(0004, 'DAN2345', '2017-08-17', 230145612378945, 'MA3HDEB1S00789123', 'ENG456789012', 'Hyundai', 'Accent', 'Blue'),
(0005, 'EAP5678', '2021-01-10', 230167894512376, 'PA1JKLB1T00456289', 'ENG567890123', 'Nissan', 'Almera', 'Gray'),
(0006, 'FAA9012', '2022-04-25', 230198754612347, 'MA2GHEB1S00123849', 'ENG678901234', 'Suzuki', 'Dzire', 'White'),
(0007, 'GAB3456', '2015-09-12', 230187965412398, 'PA3LKEB1T00345678', 'ENG789012345', 'Ford', 'EcoSport', 'Black'),
(0008, 'HAI6789', '2016-05-30', 230134987512398, 'MA2QWEB1S00478912', 'ENG890123456', 'Chevrolet', 'Spark', 'Yellow'),
(0009, 'JAX0123', '2023-02-11', 230176543219084, 'NB3UIEB1S00981234', 'ENG901234567', 'Kia', 'Soluto', 'White'),
(0010, 'KAM4567', '2018-10-21', 230145987654321, 'PA2TREB1T00567891', 'ENG012345678', 'Toyota', 'Corolla Altis', 'Gray');


/*=======================================
				OWNER TABLE
=========================================
*/
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
(123450, 'Samantha', 'Sanchez', 'Jennys Avenue', 'Maybunga', 'Pasig City', 'Metro Manila', 'NCR', 1607, 'password123', 'N03-24-013583'),
(123451, 'Myrna', 'Sanchez', 'Juan St.', 'Bakilid', 'Mandaue City', 'Cebu', 'Region VII', 6014, 'P@SSWORD', 'D16-97-169064'),
(123452, 'Patrick', 'Perez', 'Dona Lucia St.', 'Commonwealth', 'Quezon City', 'Metro Manila', 'NCR', 1121, '123password!', 'N25-23-011859'),
(123453, 'Fiona', 'Tano', 'M. Vicente St.', 'Bakilid', 'Mandaue City', 'Cebu', 'Region VII', 6014, 'password!!!', 'N01-24-023812'),
(123454, 'Jeck', 'Sanchez', 'Arguelles St.', 'Carreta', 'Cebu City', 'Cebu', 'Region VII', 6000, 'passwordkohehe', 'D37-23-004584'),
(123455, 'Gabriel', 'Avila', 'J. Rizal St.', 'Poblacion', 'Pateros', 'Metro Manila', 'NCR', 1620, 'thisismypassword', 'N03-18-024438'),
(123456, 'Megan', 'Dasal', 'Alice Crisostomo St.', 'Pacdal', 'Baguio City', 'Benguet', 'CAR', 2600, 'p@ssword12345!!', 'N03-12-123456'),
(123457, 'Alexa', 'Pleyto', '3rd St.', 'St. Ignatius', 'Quezon City', 'Metro Manila', 'NCR', 1110, 'pass123word', 'N02-25-026327'),
(123458, 'Lara', 'Turk', 'Rome St.', 'B.F. International Village', 'Las Pinas City', 'Metro Manila', 'NCR', 1740, 'passw0rd123', 'N04-10-021355'),
(123459, 'Rohann', 'Dizon', 'Leon St.', 'Sta. Cruz', 'Koranadal City', 'South Cotabato', 'Region XII', 9506, 'p4ssw0rd123!!', 'N01-12-123456');

/*=======================================
				BRANCH TABLE
=========================================
*/
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
(1001, 'LTO Paranaque District Office', 'Irasan', 'San Dionisio', 'Paranaque City', 'Metro Manila', 1700, 'NCR'),
(1002, 'LTO Las Pinas District Office', 'Alabang Zapote Rd', 'Talon Uno', 'Las Pinas City', 'Metro Manila', 1747, 'NCR'),
(1003, 'LTO San Juan District Office', 'North Domingo', 'Rivera', 'San Juan City', 'Metro Manila', 1500, 'NCR'),
(1004, 'LTO Muntinlupa Office', 'Theater Dr', 'Ayala Alabang', 'Muntinlupa City', 'Metro Manila', 1780, 'NCR'),
(1005, 'LTO Quezon City District Office', 'East Ave.', 'Pinyahan', 'Quezon City', 'Metro Manila', 1100, 'NCR'),
(1006, 'LTO Taguig Extension Office', 'Radiant St.', 'Western Bicutan', 'Taguig City', 'Metro Manila', 1630, 'NCR'),
(1007, 'LTO Cebu City District Office', 'General Maxilom Ave.', 'Carreta', 'Cebu City', 'Cebu', 6000, 'Region VII'),
(1008, 'LTO Mandaue City District Office', 'Fortuna St.', 'Bakilid', 'Mandaue City', 'Cebu', 6014, 'Region VII'),
(1009, 'LTO Baguio Office', 'Polo Field', 'Pacdal', 'Baguio City', 'Benguet', 2600, 'CAR'),
(1010, 'LTO Regional Office XII - Koronadal', 'Yellowbell', 'Sta. Cruz', 'Koronadal City', 'South Cotabato', 9506, 'Region XII');

/*=======================================
				OFFICER TABLE 
=========================================
*/
CREATE TABLE Officer (
    officer_id INT PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    branch_id INT,
    password VARCHAR(50),
    FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

INSERT INTO Officer VALUES
(11100001, 'Maria', 'Santos', 1001, 'LtoMaria!23'),
(11100002, 'Jose', 'Reyes', 1002, 'Jr_2025pass'),
(11100003, 'Anna', 'Cruz', 1003, 'RegA_Cruz#12'),
(11100004, 'Mark', 'Lopez', 1004, 'Viol8Mark*22'),
(11100005, 'Ramon', 'Garcia', 1005, 'EncRamon_45'),
(11100006, 'Elena', 'Torres', 1006, 'CashElen@78'),
(11100007, 'Luis', 'Fernandez', 1007, 'LuisReg!98'),
(11100008, 'Patricia', 'Mendoza', 1008, 'PattMend#77'),
(11100009, 'Daniel', 'Ramos', 1009, 'InspectDR09!'),
(11100010, 'Sophia', 'Castillo', 1010, 'CSophia*65');

/* =======================================================
   PAYMENT TABLE
   ======================================================= */
CREATE TABLE Payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    officer_id INT,
    branch_id INT,
    amount_paid DECIMAL(10,2),
    date_paid DATE,
    receipt_number VARCHAR(20) UNIQUE NOT NULL,
    FOREIGN KEY (officer_id) REFERENCES Officer(officer_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
);

INSERT INTO Payment (officer_id, branch_id, amount_paid, date_paid, receipt_number) VALUES
(11100006, 1006, 5000.00, '2025-07-08', 'V001'),
(11100009, 1009, 2000.00, '2025-07-20', 'V002'),
(11100003, 1003, 3000.00, '2025-08-03', 'V003'),
(11100006, 1006, 10000.00, '2025-08-04', 'V004'),
(11100003, 1003, 1500.00, '2025-08-03', 'R001'),
(11100008, 1008, 7410.00, '2025-09-12', 'R002'),
(11100008, 1008, 7410.00, '2024-11-05', 'R003'),
(11100006, 1006, 1500.00, '2024-03-20', 'R004'),
(11100009, 1009, 1500.00, '2024-09-12', 'R005'),
(11100005, 1005, 1500.00, '2025-10-08', 'R006');


/*=======================================
				REGISTRATION TABLE
=========================================
*/
CREATE TABLE Registration (
	registration_id INT,
    vehicle_id INT,
    owner_id INT, 
    payment_id INT,
    branch_id INT,
    officer_id INT,
    first_date_registered DATE,
    current_date_registered DATE,
    expiry_date DATE,
    status VARCHAR(20),
    PRIMARY KEY (registration_id),
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id),
    FOREIGN KEY (owner_id) REFERENCES Owner(owner_id),
	FOREIGN KEY (payment_id) REFERENCES Payment(payment_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id),
    FOREIGN KEY (officer_id) REFERENCES Officer(officer_id)
);

INSERT INTO Registration 
(registration_id, vehicle_id, owner_id, payment_id, branch_id, officer_id, 
 first_date_registered, current_date_registered, expiry_date, status) VALUES
(10001, 0001, 123450, 5, 1003, 11100003, '2024-02-01', '2025-08-03', '2026-08-03', 'ACTIVE'),
(10002, 0002, 123451, 6, 1008, 11100008, '2025-09-12', '2025-09-12', '2026-09-12', 'ACTIVE'),
(10003, 0003, 123452, NULL, 1002, 11100002, '2025-10-30', NULL, NULL, 'INACTIVE'),
(10004, 0004, 123453, 7, 1008, 11100008, '2024-11-05', '2024-11-05', '2025-11-05', 'EXPIRED'),
(10005, 0005, 123454, NULL, 1007, 11100007, '2025-11-07', NULL, NULL, 'INACTIVE'),
(10006, 0006, 123455, 8, 1006, 11100006, '2024-03-20', '2025-03-20', '2026-03-20', 'ACTIVE'),
(10007, 0007, 123456, 9, 1009, 11100009, '2015-09-12', '2024-09-12', '2025-09-12', 'EXPIRED'),
(10008, 0008, 123457, NULL, 1004, 11100004, '2025-11-18', NULL, NULL, 'INACTIVE'),
(10009, 0009, 123458, 10, 1005, 1110005, '2023-02-11', '2025-10-08', '2026-10-08', 'ACTIVE'),
(10010, 0010, 123459, NULL, 1010, 1110010, '2025-10-21', NULL, NULL, 'INACTIVE');

/*=======================================
				VIOLATION TABLE
=========================================
*/
CREATE TABLE Violation (
	violation_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT,
    vehicle_id INT,
    officer_id INT,
    branch_id INT,
    violation_type VARCHAR(150),
    fine_amount DECIMAL(10,2),
    violation_date DATE,
    status VARCHAR(20) DEFAULT 'Unpaid',
    payment_id INT,
    FOREIGN KEY (owner_id) REFERENCES Owner(owner_id),
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id),
    FOREIGN KEY (officer_id) REFERENCES Officer(officer_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id),
    FOREIGN KEY (payment_id) REFERENCES Payment(payment_id)
);

INSERT INTO Violation 
(owner_id, vehicle_id, officer_id, branch_id, violation_type, fine_amount, violation_date, status, payment_id) VALUES
(123450, 0001, 11100003, 1003, 'Expired Registration', 3000.00, '2025-08-01', 'Cleared', 3),
(123450, 0001, 11100003, 1003, 'Reckless Driving', 2000.00, '2025-11-17', 'Unpaid', NULL),
(123455, 0006, 11100006, 1006, 'Unregistered Motor Vehicle', 10000.00, '2025-07-28', 'Cleared', 4),
(123456, 0007, 11100009, 1009, 'Smoke Belching', 2000.00, '2025-07-15', 'Cleared', 2),
(123458, 0009, 11100005, 1005, 'Unauthorized Modification', 5000.00, '2025-10-01', 'Unpaid', NULL),
(123455, 0006, 11100006, 1006, 'Defective Parts', 5000.00, '2025-07-07', 'Cleared', 1),
(123456, 0007, 11100004, 1004, 'Expired Registration', 2000.00, '2025-09-12', 'Unpaid', NULL),
(123453, 0004, 11100008, 1008, 'No Seatbelt', 1000.00, '2025-01-29', 'Unpaid', NULL),
(123453, 0004, 11100008, 1008, 'Unregistered Motor Vehicle', 10000.00, '2025-07-31', 'Unpaid', NULL),
(123453, 0004, 11100008, 1008, 'Expired Registration', 3000.00, '2025-11-05', 'Unpaid', NULL);
