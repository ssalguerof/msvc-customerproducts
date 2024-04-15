package org.bank.ssalguerof.msvc.customerproducts.models.dao;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerDao extends ReactiveMongoRepository<Customer, String> {
    Mono<Customer> findByNumDocumento(String numDocumento);
}
