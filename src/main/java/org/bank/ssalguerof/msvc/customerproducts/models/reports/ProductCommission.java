package org.bank.ssalguerof.msvc.customerproducts.models.reports;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa las comisiones aplicadas a un producto.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductCommission {
  private String codProducto;
  private String nomProducto;
  private String codTipoProducto;
  private String descTipoProducto;
  private List<Commission> commissionList;
}
