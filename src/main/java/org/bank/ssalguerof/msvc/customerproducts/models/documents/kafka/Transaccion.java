package org.bank.ssalguerof.msvc.customerproducts.models.documents.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaccion {
    String codTipoRecarga;
    private Double monto;
    private Date fecha;

}
