package org.bank.ssalguerof.msvc.customerproducts.models.dao;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductDao extends ReactiveMongoRepository<Product, String> {

}
