package org.bank.ssalguerof.msvc.customerproducts.models.documents;


import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Quota {
  private Double monto;                  // Monto de la cuota
  private Date fechaVencimiento;         // Fecha de vencimiento de la cuota
  private boolean pagada;                // Indica si la cuota ha sido pagada
}
