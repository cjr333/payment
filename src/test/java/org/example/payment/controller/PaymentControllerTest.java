package org.example.payment.controller;

import org.example.payment.constant.ErrorCode;
import org.example.payment.constant.TransactionType;
import org.example.payment.model.ErrorResponse;
import org.example.payment.model.PayTransaction;
import org.example.payment.model.request.CancelRequest;
import org.example.payment.model.request.PayRequest;
import org.example.payment.service.PaymentService;
import org.example.payment.util.SampleGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.example.payment.constant.ErrorCode.ILLEGAL_ARGUMENT;
import static org.example.payment.util.ObjectUtils.setField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest {
  @Autowired TestRestTemplate testRestTemplate;
  @MockBean PaymentService paymentService;

  private final static String PAY_URI = "/v1/payment/pay";
  private final static String CANCEL_URI = "/v1/payment/cancel";
  private final static String FIND_TRANSACTION_URI = "/v1/payment/transaction";

  @Test
  void successPay() {
    PayRequest payRequest = SampleGenerator.genPayRequest();
    PayTransaction payTransaction = SampleGenerator.genPayTransaction(TransactionType.PAYMENT, false);

    // mock
    when(paymentService.pay(any())).thenReturn(payTransaction);

    PayTransaction responseBody = testRestTemplate.postForEntity(PAY_URI, payRequest, PayTransaction.class).getBody();

    // verify
    assertEquals(payTransaction, responseBody);
  }

  @Test
  void failPay() {
    // Not null violation
    notNullValidate(PAY_URI, SampleGenerator::genPayRequest, Arrays.asList("cardNum", "validThru", "cvc", "installment", "amount"));

    // Size violation
    List<Object> requests = new SizeMap(SampleGenerator::genPayRequest)
        .putString("cardNum", 10, 16)
        .putString("validThru", 4, 4)
        .putString("cvc", 3, 3)
        .putInt("installment", 0, 12)
        .putLong("amount", 100L, 1000000000L)
        .toList();
    requests.forEach(request -> assertPostError(PAY_URI, request, HttpStatus.BAD_REQUEST, ILLEGAL_ARGUMENT));

    // biz violation
    requests = new ArrayList<>();
    PayRequest payRequest = SampleGenerator.genPayRequest();
    payRequest.setCardNum("1234567890a");    // CardNum => Number Format
    requests.add(payRequest);
    payRequest = SampleGenerator.genPayRequest();
    payRequest.setValidThru("123a");    // validThru => Number Format
    requests.add(payRequest);
    payRequest = SampleGenerator.genPayRequest();
    payRequest.setValidThru("9999");    // validThru => mmyy
    requests.add(payRequest);
    payRequest = SampleGenerator.genPayRequest();
    payRequest.setCvc("12a");    // cvc => Number Format
    requests.add(payRequest);
    payRequest = SampleGenerator.genPayRequest();
    payRequest.setTax(payRequest.getAmount() + 1);    // tax <= amount
    requests.add(payRequest);
    requests.forEach(request -> assertPostError(PAY_URI, request, HttpStatus.BAD_REQUEST, ILLEGAL_ARGUMENT));
  }

  @Test
  void successCancel() {
    CancelRequest cancelRequest = SampleGenerator.genCancelRequest();
    PayTransaction payTransaction = SampleGenerator.genPayTransaction(TransactionType.CANCEL, false);

    // mock
    when(paymentService.cancel(any())).thenReturn(payTransaction);

    PayTransaction responseBody = testRestTemplate.postForEntity(CANCEL_URI, cancelRequest, PayTransaction.class).getBody();

    // verify
    assertEquals(payTransaction, responseBody);
  }

  @Test
  void failCancel() {
    // Not null violation
    notNullValidate(CANCEL_URI, SampleGenerator::genCancelRequest, Arrays.asList("paymentId", "amount"));

    // Size violation
    List<Object> requests = new SizeMap(SampleGenerator::genCancelRequest)
        .putString("paymentId", 20, 20)
        .putLong("amount", 100L, 1000000000L)
        .toList();
    requests.forEach(request -> assertPostError(CANCEL_URI, request, HttpStatus.BAD_REQUEST, ILLEGAL_ARGUMENT));
  }

  @Test
  void successFindTransaction() {
    PayTransaction payTransaction = SampleGenerator.genPayTransaction(TransactionType.PAYMENT, true);

    // mock
    when(paymentService.findTransaction(any())).thenReturn(payTransaction);

    URI uri = UriComponentsBuilder.fromUriString(FIND_TRANSACTION_URI)
        .queryParam("transactionId", "tid-1")
        .build().toUri();
    PayTransaction responseBody = testRestTemplate.getForEntity(uri, PayTransaction.class).getBody();

    // verify
    assertEquals(payTransaction, responseBody);
  }

  @Test
  void failFindTransaction() {
    // verify
    assertGetError(FIND_TRANSACTION_URI, HttpStatus.BAD_REQUEST, ILLEGAL_ARGUMENT);
  }

  private void notNullValidate(String uri, Supplier<Object> requestSupplier, List<String> notNullFields) {
    Object sample = requestSupplier.get();
    for (Field field: sample.getClass().getDeclaredFields()) {
      Object request = requestSupplier.get();
      setField(request, field.getName(), null);
      if (notNullFields.contains(field.getName())) {
        assertPostError(uri, request, HttpStatus.BAD_REQUEST, ILLEGAL_ARGUMENT);
      } else {
        ResponseEntity<PayTransaction> responseEntity = testRestTemplate.postForEntity(uri, request, PayTransaction.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      }
    }
  }

  private void assertPostError(String uri, Object request, HttpStatus httpStatus, ErrorCode errorCode) {
    ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.postForEntity(uri, request, ErrorResponse.class);
    assertEquals(httpStatus, responseEntity.getStatusCode());
    assertEquals(errorCode.getCode(), responseEntity.getBody().getErrorCode());
  }

  private void assertGetError(String uri, HttpStatus httpStatus, ErrorCode errorCode) {
    ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.getForEntity(uri, ErrorResponse.class);
    assertEquals(httpStatus, responseEntity.getStatusCode());
    assertEquals(errorCode.getCode(), responseEntity.getBody().getErrorCode());
  }
}