package org.bank.ssalguerof.msvc.customerproducts.models.services;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerProductService {
    public Flux<CustomerProduct> findAll();
    public Mono<CustomerProduct> findbyId(String id);
    public Mono<CustomerProduct> save(CustomerProduct customerProduct);
    public Mono<Void> delete(CustomerProduct customerProduct);
}
