-- Sample data for PIM Tool

-- 1. Employees
INSERT INTO EMPLOYEE (VISA, FIRST_NAME, LAST_NAME, BIRTH_DATE, VERSION) VALUES
('DTH', 'Duc Thinh', 'Ha', '1990-01-15', 0),
('BHU', 'Bao Huy', 'Nguyen', '1992-05-20', 0),
('JHV', 'Jean-Henri', 'Vu', '1988-11-10', 0),
('NNQ', 'Nhu Quoc', 'Nguyen', '1995-04-11', 0),
('PL1', 'Project', 'Leader 1', '1985-08-25', 0),
('PL2', 'Project', 'Leader 2', '1987-03-30', 0),
('TVA', 'Van Anh', 'Tran', '1993-07-14', 0),
('LTM', 'Thi Mai', 'Le', '1994-09-22', 0),
('NVA', 'Van An', 'Nguyen', '1991-12-05', 0);

-- 2. Groups (note: table name escaped with double quotes for SQL reserved keyword)
INSERT INTO "group" (GROUP_LEADER_ID, VERSION) VALUES
(5, 0),
(6, 0);

-- 3. Projects (Total 15 projects for multi-page pagination testing)
INSERT INTO PROJECT (PROJECT_NUMBER, NAME, CUSTOMER, STATUS, START_DATE, END_DATE, GROUP_ID, VERSION) VALUES
(1001, 'EFV Core Banking', 'EFV', 'NEW', '2021-01-01', '2021-12-31', 1, 0),
(1002, 'CXTRANET Portal', 'ELCA', 'PLA', '2021-02-01', '2021-10-30', 1, 0),
(1003, 'CRYSTAL BALL Analytics', 'Secutix', 'INP', '2021-03-15', NULL, 2, 0),
(1004, 'IOC Client Extranet', 'IOC', 'FIN', '2020-05-01', '2020-12-31', 2, 0),
(1005, 'TRADEECO Marketplace', 'TradeEco', 'NEW', '2021-06-01', '2022-06-01', 1, 0),
(1006, 'Securitas Identity Access', 'Securitas Direct', 'NEW', '2021-07-01', '2022-03-31', 1, 0),
(1007, 'Swisscom Cloud Migration', 'Swisscom AG', 'PLA', '2021-08-15', '2022-12-31', 2, 0),
(1008, 'Nestle Supply Chain ERP', 'Nestle Global', 'INP', '2021-09-01', '2023-06-30', 1, 0),
(1009, 'Roche Clinical Analytics', 'Roche Holding', 'FIN', '2020-02-10', '2021-05-20', 2, 0),
(1010, 'Zurich Insurance Telematics', 'Zurich Insurance', 'NEW', '2021-10-01', NULL, 1, 0),
(1011, 'Novartis Bio-Informatics', 'Novartis Pharma', 'PLA', '2021-11-15', '2022-08-30', 2, 0),
(1012, 'SBB Railway Timetable V2', 'SBB CFF FFS', 'INP', '2021-12-01', '2023-01-15', 1, 0),
(1013, 'UBS Wealth Management API', 'UBS Bank', 'FIN', '2019-11-01', '2020-10-31', 2, 0),
(1014, 'Logitech Peripheral Firmware', 'Logitech Intl', 'NEW', '2022-01-10', '2022-09-30', 1, 0),
(1015, 'PostFinance Payment Gateway', 'PostFinance', 'INP', '2022-02-01', '2022-11-30', 2, 0);

-- 4. Project Members (PROJECT_EMPLOYEE)
INSERT INTO PROJECT_EMPLOYEE (PROJECT_ID, EMPLOYEE_ID) VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3),
(3, 1),
(3, 4),
(5, 4),
(6, 1),
(6, 7),
(7, 2),
(7, 3),
(7, 8),
(8, 4),
(8, 9),
(9, 1),
(9, 3),
(10, 2),
(10, 4),
(11, 7),
(11, 8),
(12, 1),
(12, 9),
(13, 3),
(13, 4),
(14, 2),
(14, 7),
(15, 8),
(15, 9);