package org.bank.ssalguerof.msvc.customerproducts.models.dao;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerCard;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerCardDao extends ReactiveMongoRepository<CustomerCard, String> {
    Mono<CustomerCard> findByNumTarjeta(String numTarjeta);
}
