CREATE DATABASE CCINFOM_S23_08_sql;
USE CCINFOM_S23_08_sql;

/*=======================================
				VEHICLE TABLE
=========================================
*/
CREATE TABLE Vehicle (
    vehicle_id INT AUTO_INCREMENT,
    plate_number VARCHAR(7) NOT NULL UNIQUE,
	manufacture_date DATE NOT NULL,
    mv_file_no BIGINT NOT NULL UNIQUE,
    chassis_no VARCHAR(17) NOT NULL UNIQUE,
    engine_no VARCHAR(12) NOT NULL UNIQUE,
    make VARCHAR(15) NOT NULL,
    series VARCHAR(15) NOT NULL,
    color VARCHAR(15) NOT NULL,
    PRIMARY KEY (vehicle_id)
) AUTO_INCREMENT = 101;

/*=======================================
				OWNER TABLE
=========================================
*/CREATE TABLE Owner (
	owner_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    street VARCHAR(100) NOT NULL,
    barangay VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    province VARCHAR(50) NOT NULL,
    region VARCHAR(50) NOT NULL,
    postal_code INT NOT NULL,
    password VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    license_number VARCHAR(13) UNIQUE NOT NULL,
	CHECK (CHAR_LENGTH(password) >= 8),
	CHECK (license_number REGEXP '^[A-Z][0-9]{2}-[0-9]{2}-[0-9]{6}$'),
	CHECK (postal_code REGEXP '^[0-9]{4}$'),
    CHECK (first_name REGEXP '^[A-Za-z ]+$'),
    CHECK (last_name REGEXP '^[A-Za-z ]+$'),
    CHECK (city REGEXP '^[A-Za-z ]+$'),
    CHECK (province REGEXP '^[A-Za-z ]+$'),
    CHECK (region REGEXP '^[A-Za-z0-9 ]+$')
) AUTO_INCREMENT = 123450;

/*=======================================
				BRANCH TABLE
=========================================
*/
CREATE TABLE Branch (
    branch_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_name VARCHAR(100) NOT NULL UNIQUE,
    street VARCHAR(100) NOT NULL,
    barangay VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    province VARCHAR(50) NOT NULL,
    postal_code INT NOT NULL,
    region VARCHAR(50) NOT NULL,
	contact_number VARCHAR(15) NOT NULL,
	CHECK (contact_number REGEXP '^\\+63[0-9]{10}$'),
	CHECK (postal_code REGEXP '^[0-9]{4}$'),
	CHECK (city REGEXP '^[A-Za-z ]+$'),
    CHECK (province REGEXP '^[A-Za-z ]+$'),
    CHECK (region REGEXP '^[A-Za-z0-9 ]+$')
) AUTO_INCREMENT = 1001;

/*=======================================
				OFFICER TABLE 
=========================================
*/
CREATE TABLE Officer (
    officer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    branch_id INT,
    password VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    FOREIGN KEY (branch_id) REFERENCES branch(branch_id),
	CHECK (CHAR_LENGTH(password) >= 8)
) AUTO_INCREMENT = 11100001;

/* =======================================================
   PAYMENT TABLE
   ======================================================= */
CREATE TABLE Payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
	owner_id INT NOT NULL,
    officer_id INT NOT NULL,
    branch_id INT NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    date_paid DATE NOT NULL,
	payment_type ENUM('Violation', 'Registration', 'Renewal') NOT NULL,
	FOREIGN KEY (owner_id) REFERENCES Owner(owner_id),
    FOREIGN KEY (officer_id) REFERENCES Officer(officer_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id),
	CHECK (amount_paid > 0)
) AUTO_INCREMENT = 1;

CREATE TABLE Receipt (
    receipt_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_id INT NOT NULL UNIQUE,
    receipt_number VARCHAR(20) NOT NULL UNIQUE,
    issue_date DATE NOT NULL,
    printed_by VARCHAR(100) NOT NULL,
    FOREIGN KEY (payment_id) REFERENCES Payment(payment_id)
) AUTO_INCREMENT = 1;

/*=======================================
				REGISTRATION TABLE
=========================================
*/
CREATE TABLE Registration (
	registration_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id INT NOT NULL,
    owner_id INT NOT NULL,
    payment_id INT,
    branch_id INT NOT NULL,
    officer_id INT NOT NULL,
    first_date_registered DATE, 
    current_date_registered DATE,
    expiry_date DATE,
    status ENUM('ACTIVE', 'INACTIVE', 'EXPIRED') DEFAULT 'INACTIVE',
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id),
    FOREIGN KEY (owner_id) REFERENCES Owner(owner_id),
	FOREIGN KEY (payment_id) REFERENCES Payment(payment_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id),
    FOREIGN KEY (officer_id) REFERENCES Officer(officer_id),
	CHECK (expiry_date IS NULL OR expiry_date > current_date_registered)
) AUTO_INCREMENT = 10001;

CREATE TABLE Renewal (
    renewal_id INT AUTO_INCREMENT PRIMARY KEY,
    registration_id INT NOT NULL,
    payment_id INT,
    branch_id INT NOT NULL,
    officer_id INT NOT NULL,
    last_renewal_date DATE,
    FOREIGN KEY (registration_id) REFERENCES Registration(registration_id),
    FOREIGN KEY (payment_id) REFERENCES Payment(payment_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id),
    FOREIGN KEY (officer_id) REFERENCES Officer(officer_id)
) AUTO_INCREMENT = 20001;

/*=======================================
				VIOLATION TABLE
=========================================
*/
CREATE TABLE Violation (
	violation_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    officer_id INT NOT NULL, 
    branch_id INT NOT NULL,
    violation_type VARCHAR(150) NOT NULL,
    fine_amount DECIMAL(10,2) NOT NULL,
    violation_date DATE NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'Unpaid',
    payment_id INT,
    FOREIGN KEY (owner_id) REFERENCES Owner(owner_id),
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id),
    FOREIGN KEY (officer_id) REFERENCES Officer(officer_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id),
    FOREIGN KEY (payment_id) REFERENCES Payment(payment_id),
	CHECK (fine_amount > 0)
) AUTO_INCREMENT = 1;

INSERT INTO Vehicle VALUES
(101, 'NAA1234', '2018-06-15', 230145678912345, 'MA3EKEB1S00567891', 'ENG123456789', 'Toyota', 'Vios', 'Silver'),
(102, 'BAE4567', '2020-03-22', 230198765412345, 'PA2BLFB1T00891234', 'ENG234567890', 'Honda', 'City', 'White'),
(103, 'CAZ7890', '2024-11-05', 230123498712345, 'NB1ZDFB1S00234567', 'ENG345678901', 'Mitsubishi', 'Mirage G4', 'Red'),
(104, 'DAN2345', '2017-08-17', 230145612378945, 'MA3HDEB1S00789123', 'ENG456789012', 'Hyundai', 'Accent', 'Blue'),
(105, 'EAP5678', '2025-01-10', 230167894512376, 'PA1JKLB1T00456289', 'ENG567890123', 'Nissan', 'Almera', 'Gray'),
(106, 'FAA9012', '2022-02-25', 230198754612347, 'MA2GHEB1S00123849', 'ENG678901234', 'Suzuki', 'Dzire', 'White'),
(107, 'GAB3456', '2015-09-12', 230187965412398, 'PA3LKEB1T00345678', 'ENG789012345', 'Ford', 'EcoSport', 'Black'),
(108, 'HAI6789', '2016-05-30', 230134987512398, 'MA2QWEB1S00478912', 'ENG890123456', 'Chevrolet', 'Spark', 'Yellow'),
(109, 'JAX0123', '2023-02-11', 230176543219084, 'NB3UIEB1S00981234', 'ENG901234567', 'Kia', 'Soluto', 'White'),
(110, 'KAM4567', '2018-10-21', 230145987654321, 'PA2TREB1T00567891', 'ENG012345678', 'Toyota', 'Corolla Altis', 'Gray'),
(111, 'LAM6789', '2020-06-10', 230198765432123, 'PA3KTEB1T00678912', 'ENG098765432', 'Mazda', '3', 'Red'),
(112, 'MAN1234', '2021-04-18', 230145678954321, 'NB2LWEB1S00789124', 'ENG109876543', 'Toyota', 'Wigo', 'Orange'),
(113, 'NAY5678', '2019-02-12', 230112345698765, 'MA2HYEB1S00345658', 'ENG210987654', 'Honda', 'Brio', 'Yellow'),
(114, 'OAK4321', '2017-09-21', 230145612345678, 'PA1RTLB1T00456789', 'ENG321098765', 'Mitsubishi', 'Xpander', 'White'),
(115, 'PEN8765', '2018-01-05', 230176543219876, 'NB3UIEB1S00123456', 'ENG432109876', 'Hyundai', 'Tucson', 'Gray'),
(116, 'QAZ2345', '2022-05-10', 230178965412309, 'MA3HDEB1S00987654', 'ENG543210987', 'Ford', 'Ranger', 'Blue'),
(117, 'RAS7890', '2020-12-11', 230165432198754, 'PA2TREB1T00765432', 'ENG654321098', 'Toyota', 'Innova', 'Silver'),
(118, 'SAW4567', '2021-12-20', 230143298765431, 'NB1ZDFB1S00456789', 'ENG765432109', 'Isuzu', 'D-Max', 'White'),
(119, 'TAY9087', '2019-08-22', 230134987523409, 'PA3LKEB1T00891234', 'ENG876543210', 'Kia', 'Picanto', 'Black'),
(120, 'UAN3456', '2022-03-15', 230112349875612, 'ML2BHEB1S00234567', 'ENG987654321', 'Nissan', 'Terra', 'Brown'),
(121, 'VAP1234', '2024-10-12', 230189765412390, 'PA3MNEB1T00789234', 'ENG112233445', 'Toyota', 'Raize', 'White'),
(122, 'WEN5678', '2024-10-25', 230198765412391, 'MN2YWEB1S00456123', 'ENG223344556', 'Honda', 'HR-V', 'Silver'),
(123, 'XAO2345', '2024-11-03', 230178965412392, 'NB1ZDEB1S00678945', 'ENG334455667', 'Mitsubishi', 'Xpander Cross', 'Gray'),
(124, 'YAK9087', '2024-11-15', 230167894512393, 'PT1TREB1T00981234', 'ENG445566778', 'Hyundai', 'Creta', 'Blue'),
(125, 'ZIN3456', '2024-11-28', 230176543219394, 'MR3KLEB1S00123456', 'ENG556677889', 'Nissan', 'Kicks', 'Orange'),
(126, 'ABC6789', '2024-12-03', 230145678912395, 'NB2UIEB1S00789012', 'ENG667788990', 'Mazda', 'CX-5', 'Red'),
(127, 'DEF2341', '2024-12-09', 230198765432396, 'PA3TREB1T00456789', 'ENG778899001', 'Ford', 'Everest', 'Black'),
(128, 'GHI6784', '2024-12-15', 230167894512397, 'MA2HDEB1S00234567', 'ENG889900112', 'Suzuki', 'Jimny', 'Green'),
(129, 'JKL4569', '2024-12-21', 230145612345398, 'NB3UIEB1S00987654', 'ENG990011223', 'Isuzu', 'MUX', 'White'),
(130, 'MNO7893', '2024-12-29', 230198754612399, 'PA2LWEB1T00567891', 'ENG001122334', 'Kia', 'Seltos', 'Yellow'),
(131, 'NOP1234', '2021-10-05', 230178965412400, 'MA2ZDEB1S00456123', 'ENG112233446', 'Toyota', 'Fortuner', 'Black'),
(132, 'QER5678', '2021-10-18', 230189765412401, 'NB3UIEB1S00789456', 'ENG223344557', 'Honda', 'Civic', 'White'),
(133, 'RST2345', '2021-11-02', 230198754612402, 'PA2TREB1T00891234', 'ENG334455668', 'Mitsubishi', 'Strada', 'Silver'),
(134, 'UVW9087', '2021-11-14', 230165432198403, 'MA3HDEB1S00127893', 'ENG445566779', 'Hyundai', 'Elantra', 'Gray'),
(135, 'XYZ3456', '2021-11-26', 230176543219404, 'NB2LWEB1S00789123', 'ENG556677880', 'Nissan', 'Navara', 'Red'),
(136, 'BAH6789', '2021-12-03', 230187965412405, 'PA3LKEB1T00981234', 'ENG667788991', 'Mazda', 'CX-3', 'Blue'),
(137, 'CDE2341', '2021-12-10', 230198765432406, 'MA2GHEB1S00234567', 'ENG778899002', 'Ford', 'Territory', 'White'),
(138, 'FGH6784', '2021-12-18', 230145678954407, 'NG1ZDFB1S00456789', 'ENG889900113', 'Suzuki', 'Ertiga', 'Gray'),
(139, 'IJK4569', '2021-12-22', 230112345698408, 'PT1RTLB1T00567891', 'ENG990011224', 'Isuzu', 'D-Max', 'Silver'),
(140, 'LMN7893', '2021-12-30', 230145612378409, 'MD3EKEB1S00789123', 'ENG001122335', 'Kia', 'Stonic', 'Yellow');

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

INSERT INTO Branch VALUES
(1001, 'LTO Paranaque District Office', 'Irasan', 'San Dionisio', 'Paranaque City', 'Metro Manila', 1700, 'NCR', '+639171234567'),
(1002, 'LTO Las Pinas District Office', 'Alabang Zapote Rd', 'Talon Uno', 'Las Pinas City', 'Metro Manila', 1747, 'NCR', '+639568902314'),
(1003, 'LTO San Juan District Office', 'North Domingo', 'Rivera', 'San Juan City', 'Metro Manila', 1500, 'NCR', '+639084561239'),
(1004, 'LTO Muntinlupa Office', 'Theater Dr', 'Ayala Alabang', 'Muntinlupa City', 'Metro Manila', 1780, 'NCR', '+639772341890'),
(1005, 'LTO Quezon City District Office', 'East Ave.', 'Pinyahan', 'Quezon City', 'Metro Manila', 1100, 'NCR', '+639632208745'),
(1006, 'LTO Taguig Extension Office', 'Radiant St.', 'Western Bicutan', 'Taguig City', 'Metro Manila', 1630, 'NCR', '+639958674210'),
(1007, 'LTO Cebu City District Office', 'General Maxilom Ave.', 'Carreta', 'Cebu City', 'Cebu', 6000, 'Region VII', '+639215439876'),
(1008, 'LTO Mandaue City District Office', 'Fortuna St.', 'Bakilid', 'Mandaue City', 'Cebu', 6014, 'Region VII', '+639484502361'),
(1009, 'LTO Baguio Office', 'Polo Field', 'Pacdal', 'Baguio City', 'Benguet', 2600, 'CAR', '+639671122908'),
(1010, 'LTO Regional Office XII - Koronadal', 'Yellowbell', 'Sta. Cruz', 'Koronadal City', 'South Cotabato', 9506, 'Region XII', '+639958901432');

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

INSERT INTO Payment VALUES
(1, 123452, 11100002, 1002, 7410.00, '2021-02-12', 'Registration'),   
(2, 123456, 11100009, 1009, 7410.00, '2021-09-12', 'Registration'),    
(3, 123453, 11100008, 1008, 7410.00, '2021-10-21', 'Registration'),    
(4, 123456, 11100009, 1009, 7410.00, '2021-11-11', 'Registration'),    
(5, 123454, 11100007, 1007, 7410.00, '2022-01-05', 'Registration'),    
(6, 123450, 11100003, 1003, 7410.00, '2022-02-01', 'Registration'),   
(7, 123452, 11100002, 1002, 1500.00, '2024-02-12', 'Renewal'),     
(8, 123459, 11100010, 1010, 7410.00, '2022-02-19', 'Registration'),     
(9, 123455, 11100006, 1006, 7410.00, '2022-04-20', 'Registration'),  
(10, 123456, 11100009, 1009, 2000.00, '2024-03-23', 'Violation'),       
(11, 123451, 11100001, 1001, 7410.00, '2022-05-22', 'Registration'),    
(12, 123454, 11100007, 1007, 7410.00, '2022-02-15', 'Registration'),    
(13, 123459, 11100010, 1010, 7410.00, '2022-02-10', 'Registration'),    
(14, 123458, 11100005, 1005, 7410.00, '2022-08-22', 'Registration'),    
(15, 123456, 11100009, 1009, 1500.00, '2024-09-12', 'Renewal'),       
(16, 123453, 11100008, 1008, 1500.00, '2024-09-21', 'Renewal'),     
(17, 123453, 11100008, 1008, 7410.00, '2022-2-05', 'Registration'),    
(18, 123456, 11100009, 1009, 1500.00, '2024-11-11', 'Renewal'),     
(19, 123457, 11100004, 1004, 7410.00, '2024-12-20', 'Registration'),
(20, 123454, 11100007, 1007, 1500.00, '2025-01-05', 'Renewal'),        
(21, 123455, 11100006, 1006, 1500.00, '2025-03-20', 'Renewal'),         
(22, 123451, 11100001, 1001, 1500.00, '2025-04-18', 'Renewal'),         
(23, 123455, 11100007, 1007, 1500.00, '2025-05-15', 'Renewal'),        
(24, 123459, 11100010, 1010, 1500.00, '2025-06-10', 'Renewal'),         
(25, 123455, 11100006, 1006, 5000.00, '2025-07-14', 'Violation'),       
(26, 123455, 11100006, 1006, 10000.00, '2022-04-20', 'Violation'),     
(27, 123450, 11100003, 1003, 3000.00, '2025-08-03', 'Violation'),    
(28, 123450, 11100003, 1003, 1500.00, '2025-08-03', 'Renewal'),  
(29, 123458, 11100005, 1005, 7410.00, '2025-08-11', 'Registration'),     
(30, 123451, 11100001, 1001, 7410.00, '2025-02-12', 'Registration'),
(31, 123450, 11100003, 1003, 7410.00, '2025-09-02', 'Registration'),
(32, 123451, 11100001, 1001, 7410.00, '2025-09-04', 'Registration'),
(33, 123452, 11100002, 1002, 7410.00, '2025-09-06', 'Registration'),
(34, 123453, 11100008, 1008, 7410.00, '2025-09-08', 'Registration'),
(35, 123454, 11100007, 1007, 7410.00, '2025-09-10', 'Registration'),
(36, 123455, 11100006, 1006, 7410.00, '2025-09-12', 'Registration'),
(37, 123456, 11100009, 1009, 7410.00, '2025-09-14', 'Registration'),
(38, 123457, 11100004, 1004, 7410.00, '2025-09-16', 'Registration'),
(39, 123458, 11100005, 1005, 7410.00, '2025-09-18', 'Registration'),
(40, 123459, 11100010, 1010, 7410.00, '2025-09-20', 'Registration'),
(41, 123450, 11100003, 1003, 7410.00, '2022-02-02', 'Registration'),
(42, 123451, 11100001, 1001, 7410.00, '2022-02-04', 'Registration'),
(43, 123452, 11100002, 1002, 7410.00, '2022-02-06', 'Registration'),
(44, 123453, 11100008, 1008, 7410.00, '2022-02-08', 'Registration'),
(45, 123454, 11100007, 1007, 7410.00, '2022-02-10', 'Registration'),
(46, 123455, 11100006, 1006, 7410.00, '2022-02-12', 'Registration'),
(47, 123456, 11100009, 1009, 7410.00, '2022-02-14', 'Registration'),
(48, 123457, 11100004, 1004, 7410.00, '2022-02-16', 'Registration'),
(49, 123458, 11100005, 1005, 7410.00, '2022-02-18', 'Registration'),
(50, 123459, 11100010, 1010, 7410.00, '2022-02-20', 'Registration');

INSERT INTO Receipt VALUES
(1,  1,  'R001', '2021-02-12', 'Reyes, Jose'),
(2,  2,  'R002', '2021-09-12', 'Ramos, Daniel'),
(3,  3,  'R003', '2021-10-21', 'Mendoza, Patricia'),
(4,  4,  'R004', '2021-11-11', 'Ramos, Daniel'),
(5,  5,  'R005', '2022-01-05', 'Fernandez, Luis'),
(6,  6,  'R006', '2022-02-01', 'Cruz, Anna'),
(7,  7,  'R007', '2024-02-12', 'Reyes, Jose'),
(8,  8,  'R008', '2022-02-19', 'Castillo, Sophia'),
(9,  9,  'R009', '2022-04-20', 'Torres, Elena'),
(10, 10, 'V001', '2024-03-23', 'Ramos, Daniel'),
(11, 11, 'R010', '2022-05-22', 'Santos, Maria'),
(12, 12, 'R011', '2022-02-15', 'Fernandez, Luis'),
(13, 13, 'R012', '2022-02-10', 'Castillo, Sophia'),
(14, 14, 'R013', '2022-08-22', 'Garcia, Ramon'),
(15, 15, 'R014', '2024-09-12', 'Ramos, Daniel'),
(16, 16, 'R015', '2024-09-21', 'Mendoza, Patricia'),
(17, 17, 'R016', '2022-2-05', 'Mendoza, Patricia'),
(18, 18, 'R017', '2024-11-11', 'Ramos, Daniel'),
(19, 19, 'R018', '2024-12-20', 'Lopez, Mark'),
(20, 20, 'R019', '2025-01-05', 'Fernandez, Luis'),
(21, 21, 'R020', '2025-03-20', 'Torres, Elena'),
(22, 22, 'R021', '2025-04-18', 'Santos, Maria'),
(23, 23, 'R022', '2025-05-15', 'Fernandez, Luis'),
(24, 24, 'R023', '2025-06-10', 'Castillo, Sophia'),
(25, 25, 'V002', '2025-07-14', 'Torres, Elena'),
(26, 26, 'V003', '2022-04-20', 'Torres, Elena'),
(27, 27, 'V004', '2025-08-01', 'Cruz, Anna'),
(28, 28, 'R024', '2025-08-03', 'Cruz, Anna'),
(29, 29, 'R025', '2025-08-11', 'Garcia, Ramon'),
(30, 30, 'R027', '2025-02-12', 'Santos, Maria'),
(31, 31, 'R028', '2025-09-02', 'Cruz, Anna'),
(32, 32, 'R029', '2025-09-04', 'Santos, Maria'),
(33, 33, 'R030', '2025-09-06', 'Reyes, Jose'),
(34, 34, 'R031', '2025-09-08', 'Mendoza, Patricia'),
(35, 35, 'R032', '2025-09-10', 'Fernandez, Luis'),
(36, 36, 'R033', '2025-09-12', 'Torres, Elena'),
(37, 37, 'R034', '2025-09-14', 'Ramos, Daniel'),
(38, 38, 'R035', '2025-09-16', 'Lopez, Mark'),
(39, 39, 'R036', '2025-09-18', 'Garcia, Ramon'),
(40, 40, 'R037', '2025-09-20', 'Castillo, Sophia'),
(41, 41, 'R038', '2022-02-02', 'Cruz, Anna'),
(42, 42, 'R039', '2022-02-04', 'Santos, Maria'),
(43, 43, 'R040', '2022-02-06', 'Reyes, Jose'),
(44, 44, 'R041', '2022-02-08', 'Mendoza, Patricia'),
(45, 45, 'R042', '2022-02-10', 'Fernandez, Luis'),
(46, 46, 'R043', '2022-02-12', 'Torres, Elena'),
(47, 47, 'R044', '2022-02-14', 'Ramos, Daniel'),
(48, 48, 'R045', '2022-02-16', 'Lopez, Mark'),
(49, 49, 'R046', '2022-02-18', 'Garcia, Ramon'),
(50, 50, 'R047', '2022-02-20', 'Castillo, Sophia');

INSERT INTO Registration VALUES
(10001, 101, 123450, 6, 1003, 11100003, '2022-02-01', '2025-08-03', '2026-08-03', 'ACTIVE'), 
(10002, 102, 123451, 30, 1001, 11100001, '2025-02-12', '2025-02-12', '2028-02-12', 'ACTIVE'), 
(10003, 103, 123452, NULL, 1002, 11100002, NULL, NULL, NULL, 'INACTIVE'),
(10004, 104, 123453, 17, 1008, 11100008, '2022-2-05', '2022-2-05', '2025-2-05', 'EXPIRED'), 
(10005, 105, 123454, NULL, 1007, 11100007, NULL, NULL, NULL, 'INACTIVE'),
(10006, 106, 123455, 9, 1006, 11100006, '2022-04-20', '2025-03-20', '2026-03-20', 'ACTIVE'),
(10007, 107, 123456, 2, 1009, 11100009, '2021-09-12', '2024-09-12', '2025-09-12', 'EXPIRED'), 
(10008, 108, 123457, NULL, 1004, 11100004, NULL, NULL, NULL, 'INACTIVE'),
(10009, 109, 123458, 29, 1005, 11100005, '2025-08-11', '2025-08-11', '2028-08-11', 'ACTIVE'), 
(10010, 110, 123459, NULL, 1010, 11100010, NULL, NULL, NULL, 'INACTIVE'),
(10011, 111, 123459, 13, 1010, 11100010, '2022-02-10', '2025-02-10', '2026-02-10', 'ACTIVE'), 
(10012, 112, 123451, 11, 1001, 11100001, '2022-05-22', '2025-04-18', '2026-04-18', 'ACTIVE'), 
(10013, 113, 123452, 1, 1002, 11100002, '2021-02-12', '2024-02-12', '2025-02-12', 'EXPIRED'), 
(10014, 114, 123453, 3, 1008, 11100008, '2021-10-21', '2024-09-21', '2025-09-21', 'EXPIRED'),
(10015, 115, 123454, 5, 1007, 11100007, '2022-01-05', '2025-01-05', '2026-01-05', 'ACTIVE'), 
(10016, 116, 123454, 12, 1007, 11100007, '2022-02-15', '2025-02-15', '2026-02-15', 'ACTIVE'), 
(10017, 117, 123456, 4, 1009, 11100009, '2021-11-11', '2024-11-11', '2025-11-11', 'EXPIRED'), 
(10018, 118, 123457, 19, 1004, 11100004, '2024-12-20', '2024-12-20', '2027-12-20', 'ACTIVE'),
(10019, 119, 123458, 14, 1005, 11100005, '2022-08-22', '2022-08-22', '2025-08-22', 'EXPIRED'), 
(10020, 120, 123459, 8, 1010, 11100010, '2022-02-19', '2022-02-19', '2025-02-19', 'EXPIRED'),
(10021, 121, 123450, 31, 1003, 11100003, '2025-09-02', '2025-09-02', '2028-09-02', 'ACTIVE'),
(10022, 122, 123450, 32, 1003, 11100003, '2025-09-04', '2025-09-04', '2028-09-04', 'ACTIVE'),
(10023, 123, 123452, 33, 1002, 11100002, '2025-09-06', '2025-09-06', '2028-09-06', 'ACTIVE'),
(10024, 124, 123453, 34, 1008, 11100008, '2025-09-08', '2025-09-08', '2028-09-08', 'ACTIVE'),
(10025, 125, 123454, 35, 1007, 11100007, '2025-09-10', '2025-09-10', '2028-09-10', 'ACTIVE'),
(10026, 126, 123455, 36, 1006, 11100006, '2025-09-12', '2025-09-12', '2028-09-12', 'ACTIVE'),
(10027, 127, 123456, 37, 1009, 11100009, '2025-09-14', '2025-09-14', '2028-09-14', 'ACTIVE'),
(10028, 128, 123457, 38, 1004, 11100004, '2025-09-16', '2025-09-16', '2028-09-16', 'ACTIVE'),
(10029, 129, 123458, 39, 1005, 11100005, '2025-09-18', '2025-09-18', '2028-09-18', 'ACTIVE'),
(10030, 130, 123459, 40, 1010, 11100010, '2025-09-20', '2025-09-20', '2028-09-20', 'ACTIVE'),
(10031, 131, 123450, 41, 1003, 11100003, '2022-02-02', '2022-02-02', '2025-02-02', 'EXPIRED'),
(10032, 132, 123451, 42, 1001, 11100001, '2022-02-04', '2022-02-04', '2025-02-04', 'EXPIRED'),
(10033, 133, 123452, 43, 1002, 11100002, '2022-02-06', '2022-02-06', '2025-02-06', 'EXPIRED'),
(10034, 134, 123453, 44, 1008, 11100008, '2022-02-08', '2022-02-08', '2025-02-08', 'EXPIRED'),
(10035, 135, 123454, 45, 1007, 11100007, '2022-02-10', '2022-02-10', '2025-02-10', 'EXPIRED'),
(10036, 136, 123455, 46, 1006, 11100006, '2022-02-12', '2022-02-12', '2025-02-12', 'EXPIRED'),
(10037, 137, 123456, 47, 1009, 11100009, '2022-02-14', '2022-02-14', '2025-02-14', 'EXPIRED'),
(10038, 138, 123457, 48, 1004, 11100004, '2022-02-16', '2022-02-16', '2025-02-16', 'EXPIRED'),
(10039, 139, 123458, 49, 1005, 11100005, '2022-02-18', '2022-02-18', '2025-02-18', 'EXPIRED'),
(10040, 140, 123459, 50, 1010, 11100010, '2022-02-20', '2022-02-20', '2025-02-20', 'EXPIRED');
	
INSERT INTO Renewal VALUES 
(20001, 10001, 28, 1003, 11100003, '2025-08-03'), 
(20002, 10006, 21, 1006, 11100006, '2025-03-20'),
(20003, 10007, 15, 1009, 11100009, '2024-09-12'),
(20004, 10011, 24, 1010, 11100010, '2025-06-10'),
(20005, 10012, 22, 1001, 11100001, '2025-04-18'),
(20006, 10013, 7, 1002, 11100002, '2024-02-12'),
(20007, 10014, 16, 1008, 11100008, '2024-09-21'),
(20008, 10015, 20, 1007, 11100007, '2025-01-05'),
(20009, 10016, 23, 1007, 11100007, '2025-05-15'),
(20010, 10017, 18, 1009, 11100009, '2024-11-11');

INSERT INTO Violation VALUES
(1, 123450, 101, 11100003, 1003, 'Expired Registration', 3000.00, '2025-08-01', 'Cleared', 27),
(2, 123450, 101, 11100003, 1003, 'Reckless Driving', 2000.00, '2025-11-17', 'Unpaid', NULL),
(3, 123455, 106, 11100006, 1006, 'Unregistered Motor Vehicle', 10000.00, '2022-04-20', 'Cleared', 26),
(4, 123456, 107, 11100009, 1009, 'Smoke Belching', 2000.00, '2024-03-21', 'Cleared', 10),
(5, 123458, 109, 11100005, 1005, 'Unauthorized Modification', 5000.00, '2025-10-01', 'Unpaid', NULL),
(6, 123455, 106, 11100006, 1006, 'Defective Parts', 5000.00, '2025-07-07', 'Cleared', 25),
(7, 123456, 107, 11100004, 1004, 'Expired Registration', 2000.00, '2025-09-12', 'Unpaid', NULL),
(8, 123453, 104, 11100008, 1008, 'No Seatbelt', 1000.00, '2025-01-29', 'Unpaid', NULL),
(9, 123453, 104, 11100008, 1008, 'Unregistered Motor Vehicle', 10000.00, '2025-07-31', 'Unpaid', NULL),
(10, 123453, 104, 11100008, 1008, 'Expired Registration', 3000.00, '2025-11-05', 'Unpaid', NULL);
