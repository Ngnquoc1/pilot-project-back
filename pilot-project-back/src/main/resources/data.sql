-- Sample data for PIM Tool

-- 1. Employees
INSERT INTO EMPLOYEE (VISA, FIRST_NAME, LAST_NAME, BIRTH_DATE, VERSION) VALUES
('DTH', 'Duc Thinh', 'Ha', '1990-01-15', 0),
('BHU', 'Bao Huy', 'Nguyen', '1992-05-20', 0),
('JHV', 'Jean-Henri', 'Vu', '1988-11-10', 0),
('NNQ', 'Nhu Quoc', 'Nguyen', '1995-04-11', 0),
('PL1', 'Project', 'Leader 1', '1985-08-25', 0),
('PL2', 'Project', 'Leader 2', '1987-03-30', 0);

-- 2. Groups (note: table name escaped with backticks for SQL reserved keyword)
INSERT INTO "group" (GROUP_LEADER_ID, VERSION) VALUES
(5, 0),
(6, 0);

-- 3. Projects
INSERT INTO PROJECT (PROJECT_NUMBER, NAME, CUSTOMER, STATUS, START_DATE, END_DATE, GROUP_ID, VERSION) VALUES
(1001, 'EFV Core Banking', 'EFV', 'NEW', '2021-01-01', '2021-12-31', 1, 0),
(1002, 'CXTRANET Portal', 'ELCA', 'PLA', '2021-02-01', '2021-10-30', 1, 0),
(1003, 'CRYSTAL BALL Analytics', 'Secutix', 'INP', '2021-03-15', NULL, 2, 0),
(1004, 'IOC Client Extranet', 'IOC', 'FIN', '2020-05-01', '2020-12-31', 2, 0),
(1005, 'TRADEECO Marketplace', 'TradeEco', 'NEW', '2021-06-01', '2022-06-01', 1, 0);

-- 4. Project Members (PROJECT_EMPLOYEE)
INSERT INTO PROJECT_EMPLOYEE (PROJECT_ID, EMPLOYEE_ID) VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3),
(3, 1),
(3, 4),
(5, 4);