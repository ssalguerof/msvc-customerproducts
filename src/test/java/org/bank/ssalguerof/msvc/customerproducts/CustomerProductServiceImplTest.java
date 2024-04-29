package org.bank.ssalguerof.msvc.customerproducts;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bank.ssalguerof.msvc.customerproducts.models.dao.CustomerProductDao;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.bank.ssalguerof.msvc.customerproducts.models.services.CustomerProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;

public class CustomerProductServiceImplTest {
  @Mock
  CustomerProductDao customerProductDao;

  @InjectMocks
  CustomerProductServiceImpl customerProductService;

  @BeforeEach
  public void setUp(){
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void whenFindByIdOk() throws IOException {
    // Arrange
    String customerId = "6626c66066190f56fa0d543b";
    // Ruta al archivo JSON
    String jsonFilePath = "src/test/resources/customerProducts/customerProducts.json";

    // Crear ObjectMapper
    ObjectMapper objectMapper = new ObjectMapper();

    // Leer el archivo JSON y convertirlo a objeto CustomerProduct
    CustomerProduct customerProduct = objectMapper.readValue(new File(jsonFilePath), CustomerProduct.class);

    Mockito.when(customerProductDao.findById(Mockito.anyString())).thenReturn(Mono.just(customerProduct));

    Mono<CustomerProduct> resultMono = customerProductService.findbyId(customerId);

    StepVerifier.create(resultMono)
      .assertNext(cp-> {
        assertNotNull(cp);
        assertEquals(customerProduct.getId(),cp.getId());
      })
      .expectComplete()
      .verify();
  }


}
