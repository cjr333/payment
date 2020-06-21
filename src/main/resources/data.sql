DROP TABLE IF EXISTS PayTransaction;
DROP TABLE IF EXISTS Payment;
DROP TABLE IF EXISTS CreditCard;
DROP TABLE IF EXISTS PayApproval;

-- 신용카드 정보
CREATE TABLE CreditCard (
  cardNum VARCHAR(16) PRIMARY KEY,
  encCardInfo VARCHAR(300) NOT NULL,
  lastPayDateMs BIGINT NOT NULL,
  version INT NOT NULL
);

-- 결제 건에 대한 정보
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

-- 결제 건에 대한 변경 정보
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

-- 카드사에 요청하는 승인 정보
CREATE TABLE PayApproval (
  paymentId CHAR(20) PRIMARY KEY,
  approvalInfo VARCHAR(512) NOT NULL
);