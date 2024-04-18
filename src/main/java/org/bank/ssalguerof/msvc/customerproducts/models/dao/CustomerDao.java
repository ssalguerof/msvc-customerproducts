package org.bank.ssalguerof.msvc.customerproducts.models.dao;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Interfaz DAO para administrar la información de clientes en la base de datos.
 * Esta interfaz extiende ReactiveMongoRepository para proporcionar operaciones CRUD
 * reactivas para la entidad Customer utilizando el tipo de identificador String.
 */
public interface CustomerDao extends ReactiveMongoRepository<Customer, String> {
  /**
   * Encuentra un cliente por su número de documento.
   */
  Mono<Customer> findByNumDocumento(String numDocumento);
}
