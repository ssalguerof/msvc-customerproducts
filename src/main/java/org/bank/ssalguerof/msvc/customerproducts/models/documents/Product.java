package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clase que representa un producto bancario asociado a la colección "productos".
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "productos")
public class Product {
  @Id
  private String id;
  private String codProducto;
  private String nombre;
  private String descripcion;
  private String codTipoProducto;
  private String descTipoProducto;
  private String indComision;
  private Double valorComision;
  private String indLimMovimiento;
  private Integer cantMovimiento;
  private Date fecRegistro;

}
