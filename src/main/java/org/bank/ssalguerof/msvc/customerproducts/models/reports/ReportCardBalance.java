package org.bank.ssalguerof.msvc.customerproducts.models.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportCardBalance {
  private String id;
  private String clienteId;
  private String numTarjeta;
  private String codTipoTarjeta;
  private String descTipoTarjeta;
  private String numCuentaPrincipal;
  private String codProducto;
  private String nomProducto;
  private Double saldoCuentaPrincipal;
}
