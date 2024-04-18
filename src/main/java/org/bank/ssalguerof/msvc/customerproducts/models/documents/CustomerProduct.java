package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clase que representa la información de los productos de los clientes del banco.
 * Esta clase representa un documento en la colección "productoscliente" de la base de datos.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "productoscliente")
public class CustomerProduct {
  @Id
  private String id;
  private String numCuenta;
  private String numTarjeta;
  private String clienteId;
  private String codTipoCliente;
  private String codProducto;
  private String nomProducto;
  private String codTipoProducto;
  private String descTipoProducto;
  private Date fecRegistroProducto;

  // Datos específicos para cada tipo de producto
  private AccountData datosCuentaAhorro;       // Datos para Cuenta de Ahorro
  private AccountData datosCuentaCorriente;     // Datos para Cuenta Corriente
  private CertificateDepositData datosPlazoFijo; // Datos para Certificado de Depósito a Plazo Fijo
  private CreditData datosCreditoEmpresarial;   // Datos para Crédito Empresarial
  private CreditData datosCreditoPersonal;      // Datos para Crédito Personal
  private CreditCardData datosTarjetaCredito;   // Datos para Tarjeta de Crédito

  private List<Customer> listaTitulares;        // Lista de titulares asociados al producto
  private List<Transaction> listaTransactions;  // Lista de transacciones asociadas al producto


}
