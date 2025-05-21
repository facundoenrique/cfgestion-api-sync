package org.api_sync.services.afip.model;

import lombok.*;
import org.api_sync.adapter.outbound.entities.DatosComprobante;

@Data
public class ComprobanteRequest {
    private int ptoVta;
    private int cantReg;//cantidad de registros que se envian. voy a usar siempre 1
    private int ctoVta;
    private int cbteTipo; //tipo de factura
    private int concepto;
    private int docTipo;
    private long docNro;
    private int condicionIVAReceptorId;
    private int cbteDesde;
    private int cbteHasta;
    private String cbteFch;
    private double impTotal;
    private double impTotConc;
    private double impNeto;
    private double impOpEx;
    private double impTrib;
    private double impIVA;
    private String fchServDesde; //optativo
    private String fchServHasta; //optativo
    private String fchVtoPago;
    private String monId; //=PES
    private int monCotiz; //=1
    private int idTributo; //=99
    private String descTributo;
    private double baseImpTributo;
    private double alicTributo;
    private double importeTributo;
    
    private int idIva; // de estos puedo tener varios,3 en mi caso.
    private double baseImp;
    private double importeIva;
    private int idIva2; // de estos puedo tener varios,3 en mi caso.
    private double baseImp2;
    private double importeIva2;
    private int idIva3; // de estos puedo tener varios,3 en mi caso.
    private double baseImp3;
    private double importeIva3;
    
    private String tributos;
    private String iva;
    private String datosFact;
    
    private DatosComprobante compAsociado;
} 