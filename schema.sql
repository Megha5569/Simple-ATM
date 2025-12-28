
CREATE DATABASE atmdb;
USE atmdb;
CREATE TABLE accounts (
    acc_no INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    pin VARCHAR(10) UNIQUE NOT NULL,   
    balance DOUBLE DEFAULT 0.00
);
INSERT INTO accounts (name, pin, balance) 
VALUES ('Meghana', '1234', 5000.00);