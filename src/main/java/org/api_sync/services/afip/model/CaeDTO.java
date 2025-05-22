package org.api_sync.services.afip.model;

import lombok.Data;

@Data
public class CaeDTO {
    private String cae;
    private String caeFchVto;
    private String cbteFch;
    private String cbteNro;
    private String cbteTipo;
    private String ptoVta;
    private String impTotal;
    private String impNeto;
    private String impIva;
    private String impTrib;
    private String fchServicio;
    private String fchVtoPago;
    private String monId;
    private String monCotiz;
    private AfipResponseDetails afipResponseDetails;
} 