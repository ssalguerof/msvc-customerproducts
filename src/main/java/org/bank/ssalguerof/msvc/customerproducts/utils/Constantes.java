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
  public static final String COD_CTAAHORROVIP = "CTAVIP";
  public static final String COD_CTAMYPE = "CTAMYP";


  // Listas de productos por tipo de cliente
  public static List<String> PRODUCTS_CLIENTE_PERSONAL = Arrays.asList("CTAAHO", "CTACOR",
                                          "CTAPLZ", "CREPER", "CRETAR", "CTAVIP", "CTAVIP");
  public static List<String> PRODUCTS_CTA_PERSONAL = Arrays.asList("CTAAHO", "CTACOR", "CTAPLZ",
                                                      "CTAVIP");
  public static List<String> PRODUCTS_CLIENTE_EMPRESARIAL = Arrays.asList("CTACOR", "CREEMP",
                                                            "CRETAR", "CTAMYP", "CTAMYP");

  // Mapeo de transacciones permitidas por cada tipo de producto

  public static List<String> TRANSACTIONS_CTAAHO_CTACOR_CTAPLZ = Arrays.asList("DEPCTA", "RETCTA");
  public static List<String> TRANSACTIONS_CREPER_CREEMP_CRETAR = Arrays.asList("PAGOCR");
  public static List<String> TRANSACTIONS_CRETAR = Arrays.asList("CARGAC");


  // Códigos de movimientos
  public static final String COD_MOV_DEPOCTA = "DEPCTA";
  public static final String COD_MOV_RETICTA = "RETCTA";
  public static final String COD_MOV_PAGOCREDITO = "PAGOCR";
  public static final String COD_MOV_CARGACONSUMO = "CARGAC";

  // Descripción de movimientos
  public static final String DESC_MOV_DEPOCTA = "DEPOSITO CUENTA";
  public static final String DESC_MOV_RETICTA = "RETIRO CUENTA";
  public static final String DESC_MOV_PAGOCREDITO = "PAGO CREDITO";
  public static final String DESC_MOV_CARGACONSUMO = "CARGA CONSUMO";

  //Códigos de Transferencias
  public static final String COD_TRANS_DEPOSITO_BANCO = "TRDEPBAN";
  public static final String COD_TRANS_DEPOSITO_CAJERO = "TRDEPCAJ";
  public static final String COD_TRANS_PAGO_SERVICIOS = "TRPAGSER";
  public static final String COD_TRANS_BANCARIA_CUENTAS = "TRBANCTA";
  public static final String COD_TRANS_BANCARIA_TERCEROS = "TRBANTER";
  public static final String COD_TRANS_COBRO_COMISION = "TRCOBCOM";

  //Códigos de Transferencias
  public static final String DES_TRANS_DEPOSITO_BANCO = "Depósito en banco";
  public static final String DES_TRANS_DEPOSITO_CAJERO = "Depósito en cajero automático";
  public static final String DES_TRANS_PAGO_SERVICIOS = "Pago de servicios";
  public static final String DES_TRANS_BANCARIA_CUENTAS = "Transferencia bancaria entre cuentas";
  public static final String DES_TRANS_BANCARIA_TERCEROS = "Transferencia bancaria terceros";
  public static final String DES_TRANS_COBRO_COMISION = "Cobro de comisión";

  //Lista de Transacciones entre cuentas
  public static List<String> TRANSACTIONS_CTAS = Arrays.asList("TRBANCTA", "TRBANTER");

  public static Integer CANT_MAX_TRANS = 3;
  public static Double MONTO_COMISION = 9.5;
}
