package org.api_sync.services.afip.config;

public final class AfipConstants {
    private AfipConstants() {
        // Prevent instantiation
    }

    // SOAP Actions
    public static final String SOAP_ACTION_FECAE_SOLICITAR = "http://ar.gov.afip.dif.FEV1/FECAESolicitar";
    public static final String SOAP_ACTION_FE_COMP_CONSULTAR = "http://ar.gov.afip.dif.FEV1/FECompConsultar";
    public static final String SOAP_ACTION_FE_COMP_ULTIMO_AUTORIZADO = "http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado";

    // Endpoints
    public static final String SOAP_ENDPOINT_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx?WSDL";

    // Timeouts
    public static final int CONNECTION_TIMEOUT = 5000;
    public static final int READ_TIMEOUT = 5000;

    // XML Namespaces
    public static final String SOAP_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String AFIP_NAMESPACE = "http://ar.gov.afip.dif.FEV1/";

    // Authentication Elements
    public static final String ELEMENT_AUTH = "Auth";
    public static final String ELEMENT_TOKEN = "Token";
    public static final String ELEMENT_SIGN = "Sign";
    public static final String ELEMENT_CUIT = "Cuit";

    // Error Elements
    public static final String ELEMENT_ERROR = "Err";

    // Comprobante Elements
    public static final String ELEMENT_CBTE_TIPO = "CbteTipo";
    public static final String ELEMENT_CBTE_PTO_VTA = "PtoVta";
    public static final String ELEMENT_CBTE_NRO = "CbteNro";
    public static final String ELEMENT_CBTE_FCH = "CbteFch";
    public static final String ELEMENT_CONCEPTO = "Concepto";
    public static final String ELEMENT_DOC_TIPO = "DocTipo";
    public static final String ELEMENT_DOC_NRO = "DocNro";
    public static final String ELEMENT_IMP_TOTAL = "ImpTotal";
    public static final String ELEMENT_IMP_NETO = "ImpNeto";
    public static final String ELEMENT_IMP_IVA = "ImpIVA";
    public static final String ELEMENT_IMP_TRIB = "ImpTrib";
    public static final String ELEMENT_FCH_SERVICIO = "FchServicio";
    public static final String ELEMENT_FCH_VTO_PAGO = "FchVtoPago";
    public static final String ELEMENT_MON_ID = "MonId";
    public static final String ELEMENT_MON_COTIZ = "MonCotiz";
    public static final String ELEMENT_FCH_PROCESO = "FchProceso";
    public static final String ELEMENT_COD_AUTORIZACION = "CAE";
    public static final String ELEMENT_FCH_VTO = "CAEFchVto";
} 