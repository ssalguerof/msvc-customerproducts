package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa la información asociada a una tarjeta de crédito.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardData {
  // Atributos de la tarjeta de crédito
  public Double limiteCredito;      // Límite de crédito disponible en la tarjeta
  public Double saldoUtilizado;     // Saldo actualmente utilizado en la tarjeta
  public Date fechaCorte;           // Fecha de corte para el ciclo de facturación
  public Date fechaVencimiento;     // Fecha de vencimiento de la tarjeta
  private String indVencido;
  // Métodos getter y setter generados automáticamente por Lombok

}
