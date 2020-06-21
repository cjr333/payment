DROP TABLE IF EXISTS CreditCard;
DROP TABLE IF EXISTS Payment;
DROP TABLE IF EXISTS PayTransaction;

CREATE TABLE CreditCard (
  cardNum VARCHAR(16) PRIMARY KEY,
  encCardInfo VARBINARY(64) NOT NULL,
  lastPayDateMs BIGINT NOT NULL,
  version INT NOT NULL
);

CREATE TABLE Payment (
  paymentId CHAR(20) PRIMARY KEY,
  cardNum VARCHAR(16) NOT NULL,
  payDateMs BIGINT NOT NULL,
  installment INT NOT NULL,
  remainAmount BIGINT NOT NULL,
  remainTax BIGINT NOT NULL,
  orgAmount BIGINT NOT NULL,
  orgTax BIGINT NOT NULL,
  version INT NOT NULL
);

CREATE TABLE PayTransaction (
  transactionId CHAR(20) PRIMARY KEY,
  paymentId CHAR(20) NOT NULL,
  transactionType VARCHAR(20) NOT NULL,
  transactionDateMs BIGINT NOT NULL,
  installment INT NOT NULL,
  amount BIGINT NOT NULL,
  tax BIGINT NOT NULL,
  remainAmount BIGINT NOT NULL,
  remainTax BIGINT NOT NULL
);