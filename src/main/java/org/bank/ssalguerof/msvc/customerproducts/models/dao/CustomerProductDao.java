package org.bank.ssalguerof.msvc.customerproducts.models.dao;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CustomerProductDao extends ReactiveMongoRepository<CustomerProduct, String> {
    Flux<CustomerProduct> findByClienteId(String clienteId);
    Flux<CustomerProduct> findByClienteIdAndCodProducto(String clienteId, String codProducto);
    Flux<CustomerProduct> findByClienteIdAndCodTipoClienteAndCodProductoIn(String clienteId, String codTipoCliente, List<String> codProductos);

}
