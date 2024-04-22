package org.bank.ssalguerof.msvc.customerproducts.models.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa una comisión aplicada a una transacción.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Commission {

  private String codTipoMovimiento;
  private String descTipoMovimiento;
  private Double monto;
  private String codTransferencia;
  private String desTransferencia;
}
