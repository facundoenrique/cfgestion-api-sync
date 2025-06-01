package org.api_sync.services.afip.soap;

import org.api_sync.services.afip.model.AfipErrorResponse;
import org.api_sync.services.afip.model.AfipEventResponse;
import org.api_sync.services.afip.model.AfipResponseDetails;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class AfipSoapParserTest {

    @Test
    public void testExtractErrors() {
        // XML de ejemplo
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<soap:Header><FEHeaderInfo xmlns=\"http://ar.gov.afip.dif.FEV1/\"><ambiente>Produccion - Se2</ambiente><fecha>2025-06-01T14:20:36.0173725-03:00</fecha><id>6.0.1.0</id></FEHeaderInfo></soap:Header>" +
                "<soap:Body><FECAESolicitarResponse xmlns=\"http://ar.gov.afip.dif.FEV1/\"><FECAESolicitarResult>" +
                "<FeCabResp><Cuit>20252155857</Cuit><PtoVta>22</PtoVta><CbteTipo>1</CbteTipo><FchProceso>20250601142036</FchProceso><CantReg>1</CantReg><Resultado>R</Resultado><Reproceso>N</Reproceso></FeCabResp>" +
                "<FeDetResp><FECAEDetResponse><Concepto>1</Concepto><DocTipo>80</DocTipo><DocNro>27308096845</DocNro><CbteDesde>7050</CbteDesde><CbteHasta>7050</CbteHasta><CbteFch>20250518</CbteFch><Resultado>R</Resultado>" +
                "<Observaciones><Obs><Code>10016</Code><Msg>Campo CbteFch Debe estar comprendido  en el  rango  N-5 y N+5 siendo N la fecha de envio del pedido  de autorizacion para 1 - Productos</Msg></Obs></Observaciones>" +
                "<CAE/><CAEFchVto/></FECAEDetResponse></FeDetResp>" +
                "<Events><Evt><Code>39</Code><Msg>IMPORTANTE: El dia 6 de abril de 2025, se actualizo la version del Web Service (WS) que permite enviar, de forma opcional, el campo Condicion Frente al IVA del receptor. Cabe destacar que la Resolucion General Nro 5616 indica que ese dato debe enviarse de manera obligatoria a partir del 15/04/2025. No obstante, se mantendra como un dato no excluyente hasta el 30/06/2025, inclusive. A partir del 1/07/2025 se rechazaran las solicitudes de emision de comprobantes sin este dato. Para mas informacion, consultar el manual en: https://www.arca.gob.ar/fe/ayuda/webservice.asp, https://www.arca.gob.ar/ws/documentacion/ws-factura-electronica.asp</Msg></Evt></Events>" +
                "</FECAESolicitarResult></FECAESolicitarResponse></soap:Body></soap:Envelope>";

        AfipResponseDetails response = AfipSoapParser.extractErrors(xml);

        // Verificar que se extrajo la observación como error
        List<AfipErrorResponse> errors = response.getErrors();
        assertEquals(1, errors.size(), "Debería haber 1 error/observación");
        
        AfipErrorResponse error = errors.get(0);
        assertEquals(10016, error.getCode(), "El código del error debería ser 10016");
        assertTrue(error.getMessage().contains("Campo CbteFch Debe estar comprendido"), 
                "El mensaje debería contener el texto de la observación");

        // Verificar que se extrajo el evento
        List<AfipEventResponse> events = response.getEvents();
        assertEquals(1, events.size(), "Debería haber 1 evento");
        
        AfipEventResponse event = events.get(0);
        assertEquals(39, event.getCode(), "El código del evento debería ser 39");
        assertTrue(event.getMessage().contains("IMPORTANTE: El dia 6 de abril de 2025"), 
                "El mensaje debería contener el texto del evento");
    }
} 