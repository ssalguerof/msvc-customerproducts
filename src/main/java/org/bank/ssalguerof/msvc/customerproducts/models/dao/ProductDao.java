package org.bank.ssalguerof.msvc.customerproducts.models.dao;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Interfaz DAO que define operaciones para administrar la información de productos en MongoDB.
 */
public interface ProductDao extends ReactiveMongoRepository<Product, String> {

}
