package org.bank.ssalguerof.msvc.customerproducts.models.services;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerCard;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.TransactionCard;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportCardBalance;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportCardMovements;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerCardService {
  public Flux<CustomerCard> findAll();
  public Mono<CustomerCard> save(CustomerCard customerCard);

  public Mono<CustomerCard> addTransactionCard(String idCard, TransactionCard transactionCard);

  public Mono<ReportCardMovements> generateReportCardMovements(String idCard);

  public Mono<ReportCardBalance> generateReportCardBalance(String idCard);
}
