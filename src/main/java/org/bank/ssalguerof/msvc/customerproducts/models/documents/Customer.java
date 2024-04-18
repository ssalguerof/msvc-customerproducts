package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clase que representa la información de un cliente bancario.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "clientes")
public class Customer {
  @Id
  private String id;                  // Identificador único del cliente
  private String codTipoCliente;      // Código que indica el tipo de cliente
  private String descTipoCliente;     // Descripción del tipo de cliente
  private String nombre;              // Nombre del cliente
  private String apePaterno;          // Apellido paterno del cliente
  private String apeMaterno;          // Apellido materno del cliente
  private String codTipoDocumento;    // Código que indica el tipo de documento de identidad
  private String descTipoDocumento;   // Descripción del tipo de documento de identidad
  private String numDocumento;        // Número de documento de identidad
  private String email;               // Correo electrónico del cliente
  private String telefono;            // Número de teléfono del cliente
  private Date fecRegistro;           // Fecha de registro del cliente
  private String indFirmanteAuto;     // Indicador de si el cliente es firmante automático

  // Métodos getter y setter generados automáticamente por Lombok
}
