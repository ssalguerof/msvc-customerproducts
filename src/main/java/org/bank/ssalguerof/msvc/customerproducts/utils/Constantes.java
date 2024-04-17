package org.bank.ssalguerof.msvc.customerproducts.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Clase que contiene constantes utilizadas en el proyecto para representar tipos de clientes,
 * tipos de productos y transacciones.
 */
public class Constantes {
  // Tipos de cliente: PERSONAL (1) / EMPRESARIAL (2)
  public static final String COD_TIPOPERSONA_PERS = "1";
  public static final String COD_TIPOPERSONA_EMPR = "2";

  // Tipos de productos: ACTIVO (A) / PASIVO (P)
  public static final String COD_TIPOPRODUCTO_ACTIVO = "A";
  public static final String COD_TIPOPRODUCTO_PASIVO = "P";

  // Códigos de productos
  public static final String COD_CTAAHORRO = "CTAAHO";
  public static final String COD_CTACORRIENTE = "CTACOR";
  public static final String COD_CTAPLAZOFIJO = "CTAPLZ";
  public static final String COD_CTOPERSONAL = "CREPER";
  public static final String COD_CTOEMPRESARIAL = "CREEMP";
  public static final String COD_TARJCREDITO = "CRETAR";

  // Listas de productos por tipo de cliente
  public static List<String> PRODUCTS_CLIENTE_PERSONAL = Arrays.asList("CTAAHO", "CTACOR",
                                                        "CTAPLZ", "CREPER", "CRETAR");
  public static List<String> PRODUCTS_CTA_PERSONAL = Arrays.asList("CTAAHO", "CTACOR", "CTAPLZ");
  public static List<String> PRODUCTS_CLIENTE_EMPRESARIAL = Arrays.asList("CTACOR", "CREEMP",
                                                            "CRETAR");

  // Mapeo de transacciones permitidas por cada tipo de producto

  public static List<String> TRANSACTIONS_CTAAHO_CTACOR_CTAPLZ = Arrays.asList("DEPCTA", "RETCTA");
  public static List<String> TRANSACTIONS_CREPER_CREEMP_CRETAR = Arrays.asList("PAGOCR");
  public static List<String> TRANSACTIONS_CRETAR = Arrays.asList("CARGAC");

  // Códigos de transacciones
  public static final String COD_TRANS_DEPOCTA = "DEPCTA";
  public static final String COD_TRANS_RETICTA = "RETCTA";
  public static final String COD_TRANS_PAGOCREDITO = "PAGOCR";
  public static final String COD_TRANS_CARGACONSUMO = "CARGAC";
}
