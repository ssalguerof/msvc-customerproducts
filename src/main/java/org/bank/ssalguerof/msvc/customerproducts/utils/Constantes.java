package org.bank.ssalguerof.msvc.customerproducts.utils;

import java.util.Arrays;
import java.util.List;

public class Constantes {
    //TIPO DE CLIENTE PERSONAL/EMPRESARIAL
    public static final String COD_TIPOPERSONA_PERS="1";
    public static final String COD_TIPOPERSONA_EMPR="2";

    //TIPO DE PRODUCTOS ACTIVO/PASIVO
    public static final String COD_TIPOPRODUCTO_ACTIVO="A";
    public static final String COD_TIPOPRODUCTO_PASIVO="P";

    //PRODUCTOS

    public static final String COD_CTAAHORRO="CTAAHO";
    public static final String COD_CTACORRIENTE="CTACOR";
    public static final String COD_CTAPLAZOFIJO="CTAPLZ";
    public static final String COD_CTOPERSONAL="CREPER";
    public static final String COD_CTOEMPRESARIAL="CREEMP";
    public static final String COD_TARJCREDITO="CRETAR";

    public static List<String> PRODUCTS_CLIENTE_PERSONAL = Arrays.asList("CTAAHO","CTACOR","CTAPLZ","CREPER","CRETAR");
    public static List<String> PRODUCTS_CTA_PERSONAL = Arrays.asList("CTAAHO", "CTACOR", "CTAPLZ");
    public static List<String> PRODUCTS_CLIENTE_EMPRESARIAL = Arrays.asList("CTACOR","CREEMP","CRETAR");

    //mapeamos los movimimientos permitidos por cada producto

    public static List<String> TRANSACTIONS_CTAAHO_CTACOR_CTAPLZ = Arrays.asList("DEPCTA","RETCTA");
    public static List<String> TRANSACTIONS_CREPER_CREEMP_CRETAR = Arrays.asList("PAGOCR");
    public static List<String> TRANSACTIONS_CRETAR = Arrays.asList("CARGAC");

    public static final String COD_TRANS_DEPOCTA="DEPCTA";
    public static final String COD_TRANS_RETICTA="RETCTA";
    public static final String COD_TRANS_PAGOCREDITO="PAGOCR";
    public static final String COD_TRANS_CARGACONSUMO="CARGAC";
}
