package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionCard {
  private String numTransaccion;
  private Date fecha;
  //descripción de la transacción que indica el comercio o servicio donde se realizó la transacción.
  private String descripcion;
  private String codTipoTransaccion;
  private String codEstadoTransaccion;
  private Double monto;
}
