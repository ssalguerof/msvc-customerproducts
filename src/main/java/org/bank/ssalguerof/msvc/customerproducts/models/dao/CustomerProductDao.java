package org.bank.ssalguerof.msvc.customerproducts.models.dao;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CustomerProductDao extends ReactiveMongoRepository<CustomerProduct, String> {

}