package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditData {
    private Double montoAprobado;
    private Double saldoPendiente;
    private Integer cuotasPagadas;
    private Integer cuotasTotales;
}
