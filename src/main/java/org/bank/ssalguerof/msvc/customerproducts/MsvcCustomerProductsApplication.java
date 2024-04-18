package org.bank.ssalguerof.msvc.customerproducts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación que inicia el servicio de gestión de productos para clientes.
 */
@SpringBootApplication
public class MsvcCustomerProductsApplication {

  /**
   * Método principal que inicia la aplicación Spring Boot.
   *
   * @param args los argumentos de línea de comandos
   */
  public static void main(String[] args) {
    SpringApplication.run(MsvcCustomerProductsApplication.class, args);
  }

}
