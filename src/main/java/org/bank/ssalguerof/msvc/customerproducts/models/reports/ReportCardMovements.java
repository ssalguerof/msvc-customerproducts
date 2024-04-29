package org.bank.ssalguerof.msvc.customerproducts.models.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.TransactionCard;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportCardMovements {
  private String id;
  private String clienteId;
  private String codTipoCliente;
  private String numTarjeta;
  private String codTipoTarjeta;
  private String descTipoTarjeta;
  private String numCuentaPrincipal;
  private List<String> cuentasSecundarias;
  private List<TransactionCard> transactionCardList;
}
