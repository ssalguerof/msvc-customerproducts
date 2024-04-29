package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tarjetas")
public class CustomerCard {
  @Id
  private String id;
  private String clienteId;
  private String codTipoCliente;
  private String numTarjeta;
  private String codTipoTarjeta;
  private String descTipoTarjeta;
  private String ccv;
  private Date fecVencimiento;
  private String numCuentaPrincipal;
  private List<String> cuentasSecundarias;
  private List<TransactionCard> transactionCardList;
}
