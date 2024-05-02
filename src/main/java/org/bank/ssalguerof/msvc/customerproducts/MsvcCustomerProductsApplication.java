package org.bank.ssalguerof.msvc.customerproducts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * Clase principal de la aplicación que inicia el servicio de gestión de productos para clientes.
 */
@SpringBootApplication
public class MsvcCustomerProductsApplication  {
  private static final Logger log = LoggerFactory.getLogger(MsvcCustomerProductsApplication.class);
  /**
   * Método principal que inicia la aplicación Spring Boot.
   *
   * @param args los argumentos de línea de comandos
   */
  public static void main(String[] args) {
    SpringApplication.run(MsvcCustomerProductsApplication.class, args);
  }

}
