CREATE DATABASE vehicle_registration_system;
USE vehicle_registration_system;

/*research abt actual LTO process/info needed*/

/*(AYA) add table and at least 10 records. refer to eerd*/

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
(123450, 'Samantha', 'Sanchez', 'Jennys Avenue', 'Maybunga', 'Pasig City', 'Metro Manila', 'NCR', 1607, 'password123', 'N03-24-013583'),
(123451, 'Myrna', 'Sanchez', 'Jennys Avenue', 'Maybunga', 'Pasig City', 'Metro Manila', 'NCR', 1607, 'P@SSWORD', 'D16-97-169064'),
(123452, 'Patrick', 'Perez', 'Dona Lucia St.', 'Commonwealth', 'Quezon City', 'Metro Manila', 'NCR', 1121, '123password!', 'N25-23-011859'),
(123453, 'Fiona', 'Tano', 'M. Vicente St.', 'Malamig', 'Mandaluyong City', 'Metro Manila', 'NCR', 1550, 'password!!!', 'N01-24-023812'),
(123454, 'Jeck', 'Sanchez', 'Arguelles St.', 'Pio Del Pilar', 'Makati City', 'Metro Manila', 'NCR', 1230, 'passwordkohehe', 'D37-23-004584'),
(123455, 'Gabriel', 'Avila', 'J. Rizal St.', 'Poblacion', 'Pateros', 'Metro Manila', 'NCR', 1620, 'thisismypassword', 'N03-18-024438'),
(123456, 'Megan', 'Dasal', 'Alice Crisostomo St.', 'Talon Dos', 'Las Pinas City', 'Metro Manila', 'NCR', 1747, 'p@ssword12345!!', 'N03-12-123456'),
(123457, 'Alexa', 'Pleyto', '3rd St.', 'St. Ignatius', 'Quezon City', 'Metro Manila', 'NCR', 1110, 'pass123word', 'N02-25-026327'),
(123458, 'Lara', 'Turk', 'Rome St.', 'B.F. International Village', 'Las Pinas City', 'Metro Manila', 'NCR', 1740, 'passw0rd123', 'N04-10-021355'),
(123459, 'Rohann', 'Dizon', 'Leon Guinto St.', 'Malate', 'Manila', 'Metro Manila', 'NCR', 1004, 'p4ssw0rd123!!', 'N01-12-123456');

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
(1001, 'LTO Paranaque District Office', 'Olivarez Plaza', 'San Dionisio', 'Paranaque', 'Metro Manila', 1700, 'NCR'),
(1002, 'LTO Las Pinas District Office', 'Alabang Zapote', 'Talon Uno', 'Las Pinas', 'Metro Manila', 1747, 'NCR'),
(1003, 'LTO San Juan District Office', 'North Domingo', 'Rivera', 'San Juan', 'Metro Manila', 1500, 'NCR'),
(1004, 'LTO Pateros Extension Office', 'M. Amelda', 'San Roque', 'Pateros', 'Metro Manila', 1620, 'NCR'),
(1005, 'LTO Quezon City District Office', 'East Ave.', 'Pinyahan', 'Quezon City', 'Metro Manila', 1100, 'NCR'),
(1006, 'LTO Taguig Extension Office', 'Radiant St.', 'Western Bicutan', 'Taguig', 'Metro Manila', 1630, 'NCR'),
(1007, 'LTO Cebu City District Office', 'General Maxilom Ave.', 'Carreta', 'Cebu City', 'Cebu', 6000, 'Region VII'),
(1008, 'LTO Mandaue City District Office', 'Fortuna St.', 'Bakilid', 'Mandaue', 'Cebu', 6014, 'Region VII'),
(1009, 'LTO Baguio Office', 'Polo Field', 'Pacdal', 'Baguio', 'Benguet', 2600, 'CAR'),
(1010, 'LTO Regional Office XII - Koronadal City', 'Yellowbell', 'Sta. Cruz', 'Koronadal', 'South Cotabato', 9506, 'Region XII');

/*=======================================
				OFFICER TABLE 
=========================================
*/
CREATE TABLE officer (
    officer_id INT PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    position VARCHAR(50),
    branch_id INT,
    password VARCHAR(50),
    FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

INSERT INTO officer (officer_id, first_name, last_name, position, branch_id, password) VALUES
(11100001, 'Maria', 'Santos', 'Branch Manager', 1001, 'LtoMaria!23'),
(11100002, 'Jose', 'Reyes', 'Assistant Manager', 1002, 'Jr_2025pass'),
(11100003, 'Anna', 'Cruz', 'Registration Officer', 1002, 'RegA_Cruz#12'),
(11100004, 'Mark', 'Lopez', 'Violation Officer', 1002, 'Viol8Mark*22'),
(11100005, 'Ramon', 'Garcia', 'Data Encoder', 1003, 'EncRamon_45'),
(11100006, 'Elena', 'Torres', 'Cashier', 1003, 'CashElen@78'),
(11100007, 'Luis', 'Fernandez', 'Registration Officer', 1004, 'LuisReg!98'),
(11100008, 'Patricia', 'Mendoza', 'Branch Manager', 1004, 'PattMend#77'),
(11100009, 'Daniel', 'Ramos', 'Inspector', 1005, 'InspectDR09!'),
(11100010, 'Sophia', 'Castillo', 'Customer Service Officer', 1005, 'CSophia*65');

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
(11100004, 1003, 5000.00, '2025-07-08', 'V001'),
(11100006, 1004, 2000.00, '2025-07-20', 'V002'),
(11100005, 1003, 3000.00, '2025-08-03', 'V003'),
(11100001, 1001, 10000.00, '2025-08-04', 'V004'),
(11100002, 1002, 7410.00, '2025-08-10', 'R001'),
(11100008, 1005, 7410.00, '2025-09-12', 'R002'),
(11100009, 1005, 7410.00, '2025-09-14', 'R003'),
(11100003, 1002, 1500.00, '2025-09-20', 'R004'),
(11100007, 1004, 1500.00, '2025-10-05', 'R005'),
(11100010, 1006, 7410.00, '2025-10-08', 'R006');


/*(JP AND AYA) create registration table with at least 10 records (refer to EERD) */
/*Note: we add payment_id to registration right? or not?*/

/*=======================================
				REGISTRATION TABLE
=========================================
*/
CREATE TABLE registration (
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

INSERT INTO registration 
(registration_id, vehicle_id, owner_id, payment_id, branch_id, officer_id, 
 first_date_registered, current_date_registered, expiry_date, status) VALUES
(10001, 0001, 123450, 5, 1001, 11100001, '2018-06-15', '2025-06-15', '2026-06-15', 'ACTIVE'),
(10002, 0002, 123451, 6, 1002, 11100003, '2020-03-22', '2025-03-22', '2026-03-22', 'ACTIVE'),
(10003, 0003, 123452, 4, 1003, 11100002, '2019-11-05', '2024-11-05', '2025-11-05', 'ACTIVE'),
(10004, 0004, 123453, 2, 1004, 11100004, '2017-08-17', '2024-08-17', '2025-08-17', 'EXPIRED'),
(10011, 0005, 123454, NULL, 1005, 11100006, '2025-01-10', NULL, NULL, 'INACTIVE'),
(10006, 0006, 123455, 8, 1006, 11100005, '2022-04-25', '2025-04-25', '2026-04-25', 'ACTIVE'),
(10007, 0007, 123456, 9, 1007, 11100007, '2015-09-12', '2024-09-12', '2025-09-12', 'EXPIRED'),
(10008, 0008, 123457, 3, 1008, 11100006, '2016-05-30', '2024-05-30', '2025-05-30', 'EXPIRED'),
(10009, 0009, 123458, 10, 1009, 11100008, '2023-02-11', '2025-02-11', '2026-02-11', 'ACTIVE'),
(10010, 0010, 123459, 1, 1010, 11100009, '2018-10-21', '2025-10-21', '2026-10-21', 'ACTIVE');

/* add variations for table values. some status EXPIRED, ACTIVE, etc. */


/*=======================================
				VIOLATION TABLE
=========================================
*/
/*(SAM) create violation table with at least 10 records (refer to EERD & research on actual violation & costs)*/
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
(123450, 0001, 11100005, 1003, 'Expired License', 3000.00, '2025-08-01', 'Cleared', 3),
(123450, 0001, 11100010, 1002, 'Reckless Driving', 2000.00, '2025-09-22', 'Unpaid', NULL),
(123452, 0003, 11100002, 1002, 'Unregistered Motor Vehicle', 10000.00, '2025-07-28', 'Cleared', 4),
(123453, 0004, 11100004, 1004, 'Smoke Belching', 2000.00, '2025-07-15', 'Cleared', 2),
(123454, 0005, 11100003, 1005, 'Unauthorized Modification', 5000.00, '2025-10-01', 'Unpaid', NULL),
(123455, 0010, 11100004, 1002, 'Defective Parts', 5000.00, '2025-07-07', 'Cleared', 1),
(123456, 0007, 11100004, 1002, 'Expired Registration', 2000.00, '2025-04-15', 'Unpaid', NULL),
(123457, 0008, 11100007, 1004, 'No Seatbelt', 1000.00, '2025-01-29', 'Unpaid', NULL),
(123457, 0008, 11100005, 1003, 'Unregistered Motor Vehicle', 10000.00, '2025-07-31', 'Unpaid', NULL),
(123457, 0008, 11100009, 1005, 'Expired License', 3000.00, '2025-06-05', 'Unpaid', NULL);









