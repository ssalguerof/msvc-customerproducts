package org.bank.ssalguerof.msvc.customerproducts.models.reports;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * Clase que representa un reporte de comisiones aplicadas a los productos de un cliente.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ReportProductCommission {

  private String periodo;
  private String clienteId;
  private List<ProductCommission> productCommissionList;
}
