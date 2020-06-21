package org.example.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment.model.PayTransaction;
import org.example.payment.model.request.CancelRequest;
import org.example.payment.model.request.PayRequest;
import org.example.payment.service.PaymentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/payment")
public class PaymentController {
  private final PaymentService paymentService;

  @PostMapping("/pay")
  public PayTransaction pay(@Valid @RequestBody PayRequest payRequest) {
    return paymentService.pay(payRequest);
  }

  @PostMapping("/cancel")
  public PayTransaction cancel(@Valid @RequestBody CancelRequest cancelRequest) {
    return paymentService.cancel(cancelRequest);
  }

  @GetMapping("/transaction")
  public PayTransaction findTransaction(@RequestParam @NotEmpty String transactionId) {
    return paymentService.findTransaction(transactionId);
  }
}
