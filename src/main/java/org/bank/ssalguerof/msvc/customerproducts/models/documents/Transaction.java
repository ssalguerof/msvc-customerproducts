package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que contiene información de movimientos en los productos del cliente.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
  private String codTipoMovimiento;
  private String descTipoMovimiento;
  private Double monto;
  private Date fecRegistro;
}
