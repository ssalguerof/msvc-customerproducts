package org.bank.ssalguerof.msvc.customerproducts.models.services;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.Transaction;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportAvailableProducts;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportProducts;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Clase service encargado de interactuar con la interfaz CustomerProductDao.
 */
public interface CustomerProductService {
  public Flux<CustomerProduct> findAll();

  public Mono<CustomerProduct> findbyId(String id);

  public Mono<CustomerProduct> findbyNumCuenta(String numCuenta);

  public Flux<CustomerProduct> findbyClienteId(String clienteId);

  public Mono<CustomerProduct> save(CustomerProduct customerProduct);

  public Mono<Void> delete(CustomerProduct customerProduct);

  public Mono<CustomerProduct> updateProductTransaction(String idCustomerProd,
                                                        Transaction transaction);

  public Mono<CustomerProduct> transferProductTransaction(String numCtaOrigen,
                                              String numCtaDestino, Transaction transaction);

  public Mono<ReportAvailableProducts> generateReportProductsCustomer(String clienteId, String codTipoCliente);
}
