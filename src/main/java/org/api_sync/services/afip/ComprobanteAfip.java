package org.api_sync.services.afip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteAfip {
    private int punto_venta;
    private int nroComp;
    private int tipoComp;
    private String cbteFch;
    private double impTotal;
    private double impNeto;
    private double impIvas; //importe total todos los ivas
    private double impTributos; //importe total todos los tributos
    private String fechaProc;
    private long CAE;

    private List<ComprobanteAfipIva> iva;


    public ComprobanteAfip(int punto_venta, int nroComp, int tipoComp) {
        this.setPunto_venta(punto_venta);
        this.nroComp = nroComp;
        this.tipoComp = tipoComp;
    }

}
