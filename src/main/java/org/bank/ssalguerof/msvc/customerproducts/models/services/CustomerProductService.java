package org.bank.ssalguerof.msvc.customerproducts.models.services;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Clase service encargado de interactuar con la interfaz CustomerProductDao.
 */
public interface CustomerProductService {
  public Flux<CustomerProduct> findAll();

  public Mono<CustomerProduct> findbyId(String id);

  public Mono<CustomerProduct> save(CustomerProduct customerProduct);

  public Mono<Void> delete(CustomerProduct customerProduct);

  public Mono<CustomerProduct> updateProductTransaction(String idCustomerProd,
                                                        Transaction transaction);
}
