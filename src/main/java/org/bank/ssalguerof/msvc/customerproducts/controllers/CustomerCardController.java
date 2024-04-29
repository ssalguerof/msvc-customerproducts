package org.bank.ssalguerof.msvc.customerproducts.controllers;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerCard;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.TransactionCard;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportCardBalance;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportCardMovements;
import org.bank.ssalguerof.msvc.customerproducts.models.services.CustomerCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/customerCard")
public class CustomerCardController {
  @Autowired
  private CustomerCardService customerCardService;

  @GetMapping
  public Flux<CustomerCard> findAll(){
    return customerCardService.findAll();
  }

  @PostMapping
  public Mono<CustomerCard> save(@RequestBody CustomerCard customerCard){
    return customerCardService.save(customerCard);
  }

  @PostMapping("/transaction/{idCard}")
  public Mono<CustomerCard> addTransactionCard(@PathVariable String idCard, @RequestBody TransactionCard transactionCard){
    return customerCardService.addTransactionCard(idCard, transactionCard);
  }

  @GetMapping("/generateReportCardMovements/{idCard}")
  public Mono<ReportCardMovements> generateReportCardMovements(@PathVariable String idCard) {
    return customerCardService.generateReportCardMovements(idCard);
  }

  @GetMapping("/generateReportCardBalance/{idCard}")
  public Mono<ReportCardBalance> generateReportCardBalance(@PathVariable String idCard) {
    return customerCardService.generateReportCardBalance(idCard);
  }

}
