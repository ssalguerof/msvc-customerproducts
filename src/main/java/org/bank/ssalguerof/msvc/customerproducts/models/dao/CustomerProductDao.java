package org.bank.ssalguerof.msvc.customerproducts.models.dao;

import java.util.List;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz que define métodos para consultar y manipular la información de CustomerProduct
 * en MongoDB de forma reactiva.
 * Esta interfaz extiende ReactiveMongoRepository, lo que permite realizar operaciones CRUD
 * y consultas personalizadas sobre la colección de CustomerProduct en la base de datos MongoDB.
 */
public interface CustomerProductDao extends ReactiveMongoRepository<CustomerProduct, String> {
  Flux<CustomerProduct> findByClienteId(String clienteId);

  Flux<CustomerProduct> findByClienteIdAndCodProducto(String clienteId, String codProducto);

  Flux<CustomerProduct> findByClienteIdAndCodTipoClienteAndCodProductoIn(
                        String clienteId, String codTipoCliente, List<String> codProductos);

  Mono<CustomerProduct> findByNumCuenta(String numCuenta);
}
