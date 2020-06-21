# Simple Payment Example

카드 정보를 입력받아 결제 및 결제취소가 가능한 결제시스템을 개발한다.

## 개발명세

- 각 결제 건은 관리번호(20자리 unique id)로 관리된다.
- 관리번호로 결제 취소가 가능하며 부분취소 또한 가능하다.
- 결제 및 결제취소 각 건은 결제 트랜잭션으로 표현되며 관리번호로 조회 시 최근 트랜잭션 1건만 조회한다.
- 카드사와의 통신은 embedded database 의 저장으로 대체하며 정해진 형태의 규약(450자리 문자열)에 따른다.
- 부가가치세는 옵션 정보이며 미입력 시 결제금액 / 11 (소수점이하 반올림) 으로 자동계산한다.
- 부가가치세는 결제금액보다 클 수 없다.
- 카드정보는 암호화하여 저장하며 필요한 경우 복호화하여 사용한다.

## 개발 프레임워크

- SpringBoot 2.3.1
- Spring-Web
- H2 Database
- Jpa with Hibernate

## 테이블 설계

- CreditCard(PK: cardNum)
- Payment(PK: paymentId, FK: cardNum)
- PayTransaction(PK: seqNo(AI), FK: paymentId)
- PayApproval(PK: paymentId)
- 자세한 테이블 스키마는 /resources/data.sql 참고

## 문제해결 전략

- PaymentId/TransactionId 는 8자리의 timestamp hex string + 12 자리의 Alphanumeric random string 으로 구성된다.
- 카드정보 암호화는 AES256 를 사용한다.
- StringFormat 은 FormattedStringBuilder 로 구현하고 해당 Builder 를 이용하여 카드사와의 규약문자열을 생성하도록 한다.

### 동시성 제어
- 동시성 제어는 Multi Thread/Multi Server 를 모두 고려하여 Table 의 version 필드를 이용하여 달성한다.
- 하나의 카드번호로 동시 결제를 제어하기 위해 CreditCard 에 version 필드를 둔다.
- 하나의 결제 건에 대한 동시 취소를 제어하기 위해 Payment 에 version 필드를 둔다.

### 트랜잭션
- 결제는 CreditCard 의 정보 변경(version/lastPayDate..) 과 Payment 및 PayTransaction 의 데이터 생성을 하나의 트랜잭션으로 처리한다.
- 결제취소는 Payment 의 정보 변경(version/amount..) 과 PayTransaction 의 데이터 생성을 하나의 트랜잭션으로 처리한다.

## 빌드 및 실행 방법

- 빌드 툴은 Gradle 을 사용하며 Gradle Wrapper 를 이용한다.
- 실행 방법(아래 방법 중 택 1)
	- IDE 에서 Application 의 main 메서드를 실행
	- 프로젝트 폴더에서 gradlew(또는 gradlew.bat) 를 이용하여 bootJar 태스크를 실행(gradlew bootJar) -> build/libs 에 생성된 payment.jar 를 실행(java -jar payment.jar)
- 참고) H2 database 콘솔은 http://localhost/h2-console 로 접속 가능하며 JDBC URL 은 "jdbc:h2:mem:testdb" 로 입력하여 연결한다.