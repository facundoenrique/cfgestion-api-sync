package org.api_sync.services.afip;

import lombok.extern.slf4j.Slf4j;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static org.apache.commons.lang3.StringUtils.EMPTY;


@Slf4j
public class PSOAPClientSAAJ {

    private static final String soapActionFECAESolicitar = "http://ar.gov.afip.dif.FEV1/FECAESolicitar";
    private static final String soapEndpointUrl = "https://servicios1.afip.gov.ar/wsfev1/service.asmx?WSDL";
    private final String token;
    private final String sign;
    private final String cuit;
    private String message;


    public PSOAPClientSAAJ(String token, String sign, String cuit) {
        this.token = token;
        this.sign = sign;
        this.cuit = cuit;
    }

    // SAAJ - SOAP Client Testing
    public CaeDTO getCae(ComprobanteRequest comprobanteRequest) {
        log.info("Ejecutando pegada a FECAESolicitar");

        return callSoapWebService(soapEndpointUrl, soapActionFECAESolicitar, comprobanteRequest);
    }

    public Integer searchUltimaFacturaElectronica(int punto_venta, int tipoComprobante) {
        String soapAction = "http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado";

        return callSoapWebServiceUltimaFE(soapEndpointUrl, soapAction, punto_venta, tipoComprobante);
    }


    private Integer callSoapWebServiceUltimaFE(String soapEndpointUrl, String soapAction, int punto_venta, int tipoComprobante) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequestUltimaFE(soapAction, punto_venta, tipoComprobante), soapEndpointUrl);

            // Print the SOAP Response
            soapResponse.writeTo(System.out);

            // CON ESTAS 3 LINEAS OBTENGO EL XML EN FORMATO STRING. ? AL PEDO ?
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());
            message = strMsg;

            String cbteNro = strMsg.substring(strMsg.indexOf("<CbteNro>") + 9, strMsg.indexOf("</CbteNro>"));

            System.out.println("Ultimo comprobante: " + cbteNro);

            if (!cbteNro.equals("0")) {
                String cae = strMsg.substring(strMsg.indexOf("<CbteNro>") + 9, strMsg.indexOf("</CbteNro>"));
                System.out.println("cae: " + cae);
            }

            soapConnection.close();
            return Integer.parseInt(cbteNro);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
        }
        return 0;
    }


    private SOAPMessage createSOAPRequestUltimaFE(String soapAction, int punto_venta, int tipoComprobante) throws Exception {

    	
    	/*
    	 MessageFactory messageFactory = MessageFactory.newInstance();
         SOAPMessage soapMessage = messageFactory.createMessage();
         createSoapEnvelope(soapMessage);
    	 */

        //cambie las 3 lineas anteriores por la siguiente.
        SOAPMessage soapMessage = createMesaggeUltimaFE(punto_venta, tipoComprobante);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");


        return soapMessage;
    }


    private SOAPMessage createMesaggeUltimaFE(int punto_venta, int tipoComprobante) {


        String soapMessageWithLeadingComment =
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\"><SOAP-ENV:Header/><SOAP-ENV:Body>"

                + "<FECompUltimoAutorizado xmlns=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<Auth>"
                + "<Token>" + token + "</Token>"
                + "<Sign>" + sign + "</Sign>"
                + "<Cuit>" + cuit + "</Cuit>"
                + "</Auth>"
                + "<PtoVta>" + punto_venta + "</PtoVta>"
                + "<CbteTipo>" + tipoComprobante + "</CbteTipo>"
                + "</FECompUltimoAutorizado>"
                + "</SOAP-ENV:Body>"
                + "</SOAP-ENV:Envelope>";


        SOAPMessage soapMessage = null;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(
                            soapMessageWithLeadingComment.getBytes()));
            SOAPPart part = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
        } catch (SOAPException | IOException e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");

        }
        return soapMessage;

    }


    public ComprobanteAfip llamarFECompConsultar(int punto_venta, int cbteTipo, int cbteNro) {

        //produccion
        String soapAction = "http://ar.gov.afip.dif.FEV1/FECompConsultar";
        return callSoapWebServiceFECompConsultar(soapEndpointUrl, soapAction, punto_venta, cbteTipo, cbteNro);
    }

    private ComprobanteAfip callSoapWebServiceFECompConsultar(String soapEndpointUrl, String soapAction, int punto_venta, int cbteTipo, int cbteNro) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            //desde aca arranca el intento de poner un timeout
            URL endpoint = buildURL(soapEndpointUrl);


            SOAPMessage soapResponse = soapConnection.call(createSOAPRequestFECompConsultar(soapAction, punto_venta, cbteNro, cbteTipo), endpoint);
            //aca finaliza el intento de poner un timeout


            // Send SOAP Message to SOAP Server
//            SOAPMessage soapResponse = soapConnection.call(createSOAPRequestFECompConsultar(soapAction, punto_venta, cbteNro, cbteTipo), soapEndpointUrl);

            // Print the SOAP Response
            soapResponse.writeTo(System.out);


            // CON ESTAS 3 LINEAS OBTENGO EL XML EN FORMATO STRING. ? AL PEDO ?
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());
            String error = EMPTY;
            log.debug(strMsg);
            if (strMsg.contains("<Err>"))
                error = strMsg.substring(strMsg.indexOf("<Err>") + 7, strMsg.indexOf("</Err>"));
            if (error.length() > 0) {
                System.out.println("datos mal ingresados");
                soapConnection.close();
                return null;

            } else {
                String cbteFecha = strMsg.substring(strMsg.indexOf("<CbteFch>") + 9, strMsg.indexOf("</CbteFch>"));
                String impTotal = strMsg.substring(strMsg.indexOf("<ImpTotal>") + 10, strMsg.indexOf("</ImpTotal>"));
                String impNeto = strMsg.substring(strMsg.indexOf("<ImpNeto>") + 9, strMsg.indexOf("</ImpNeto>"));
                String impIvas = strMsg.substring(strMsg.indexOf("<ImpIVA>") + 8, strMsg.indexOf("</ImpIVA>"));
                String impTributos = strMsg.substring(strMsg.indexOf("<ImpTrib>") + 9, strMsg.indexOf("</ImpTrib>"));
                String fechaProceso = strMsg.substring(strMsg.indexOf("<FchProceso>") + 12, strMsg.indexOf("</FchProceso>"));
                String cae = strMsg.substring(strMsg.indexOf("<CodAutorizacion>") + 17, strMsg.indexOf("</CodAutorizacion>"));

                ComprobanteAfip c = new ComprobanteAfip(punto_venta, cbteNro, cbteTipo);
                c.setImpIvas(Double.parseDouble(impIvas));
                c.setImpTotal(Double.parseDouble(impTotal));
                c.setImpNeto(Double.parseDouble(impNeto));
                c.setImpTributos(Double.parseDouble(impTributos));
                c.setFechaProc(fechaProceso);
                c.setCAE(Long.parseLong(cae));
                c.setCbteFch(cbteFecha);
                soapConnection.close();

                return c;
            }


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
        }
        return null;
    }

    private SOAPMessage createSOAPRequestFECompConsultar(String soapAction, int punto_venta, int nroComp, int cbteTipo) throws Exception {

    	
    	/*
    	 MessageFactory messageFactory = MessageFactory.newInstance();
         SOAPMessage soapMessage = messageFactory.createMessage();
         createSoapEnvelope(soapMessage);
    	 */

        //cambie las 3 lineas anteriores por la siguiente.
        SOAPMessage soapMessage = createMesaggeFECompConsultar(punto_venta, nroComp, cbteTipo);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");


        return soapMessage;
    }


    private SOAPMessage createMesaggeFECompConsultar(int punto_venta, int nroComp, int cbteTipo) {

        String soapMessageWithLeadingComment =
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\"><SOAP-ENV:Header/><SOAP-ENV:Body>"

                + "<FECompConsultar xmlns=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<Auth>"
                + "<Token>" + token + "</Token>"
                + "<Sign>" + sign + "</Sign>"
                + "<Cuit>" + cuit + "</Cuit>"
                + "</Auth>"


                + "<ar:FeCompConsReq>"
                + "<ar:CbteTipo>" + cbteTipo + "</ar:CbteTipo>"
                + "<ar:CbteNro>" + nroComp + "</ar:CbteNro>"
                + "<ar:PtoVta>" + punto_venta + "</ar:PtoVta>"
                + "</ar:FeCompConsReq>"

                + "</FECompConsultar>"
                + "</SOAP-ENV:Body>"
                + "</SOAP-ENV:Envelope>";


        SOAPMessage soapMessage = null;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(
                            soapMessageWithLeadingComment.getBytes()));
            SOAPPart part = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
        } catch (SOAPException | IOException e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
        }
        return soapMessage;

    }


    public int llamarFECompUltimoAutorizado(int punto_venta, int cbteTipo) {
        //produccion
        String soapAction = "http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado";
        return callSoapWebServiceCompUltimoAutorizado(soapEndpointUrl, soapAction, punto_venta, cbteTipo);
    }


    private int callSoapWebServiceCompUltimoAutorizado(String soapEndpointUrl, String soapAction, int punto_venta, int cbteTipo) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequestCompUltimoAutorizado(soapAction, punto_venta, cbteTipo), soapEndpointUrl);

            // Print the SOAP Response
            soapResponse.writeTo(System.out);

            // CON ESTAS 3 LINEAS OBTENGO EL XML EN FORMATO STRING. ? AL PEDO ?
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());


            String CbteNro = strMsg.substring(strMsg.indexOf("<CbteNro>") + 9, strMsg.indexOf("</CbteNro>"));


            System.out.println("CbteNro: " + CbteNro);


            int nro = Integer.parseInt(CbteNro);

            soapConnection.close();
            return nro;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
        }
        return 0;
    }


    private SOAPMessage createSOAPRequestCompUltimoAutorizado(String soapAction, int punto_venta, int cbteTipo) throws Exception {

    	
    	/*
    	 MessageFactory messageFactory = MessageFactory.newInstance();
         SOAPMessage soapMessage = messageFactory.createMessage();
         createSoapEnvelope(soapMessage);
    	 */

        //cambie las 3 lineas anteriores por la siguiente.
        SOAPMessage soapMessage = createMesaggeCompUltimoAutorizado(punto_venta, cbteTipo);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");


        return soapMessage;
    }


    private SOAPMessage createMesaggeCompUltimoAutorizado(int punto_venta, int cbteTipo) {


        String soapMessageWithLeadingComment =
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\"><SOAP-ENV:Header/><SOAP-ENV:Body>"

                + "<FECompUltimoAutorizado xmlns=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<Auth>"
                + "<Token>" + token + "</Token>"
                + "<Sign>" + sign + "</Sign>"
                + "<Cuit>" + cuit + "</Cuit>"
                + "</Auth>"
                + "<PtoVta>" + punto_venta + "</PtoVta>"
                + "<CbteTipo>" + cbteTipo + "</CbteTipo>"
                + "</FECompUltimoAutorizado>"
                + "</SOAP-ENV:Body>"
                + "</SOAP-ENV:Envelope>";


        SOAPMessage soapMessage = null;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(
                            soapMessageWithLeadingComment.getBytes()));
            System.out.println(soapMessage.toString());
            SOAPPart part = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
        } catch (SOAPException | IOException e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
        }
        return soapMessage;

    }


    private CaeDTO callSoapWebService(String soapEndpointUrl, String soapAction, ComprobanteRequest comprobanteRequest) {
        String resultado = EMPTY;
        String codeError = EMPTY;
        String mesaggeError = EMPTY;
        message = EMPTY;
        String cae = EMPTY;
        String caeFchVto = EMPTY;
        try {
            // Create SOAP Connection

            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            URL endpoint = buildURL(soapEndpointUrl);

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction, comprobanteRequest), endpoint);

            // Print the SOAP Response
            soapResponse.writeTo(System.out);


            // CON ESTAS 3 LINEAS OBTENGO EL XML EN FORMATO STRING. ? AL PEDO ?
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());
            message = strMsg;

            resultado = strMsg.substring(strMsg.indexOf("<Resultado>") + 11, strMsg.indexOf("</Resultado>"));

            if (resultado.equals("A")) {
                cae = strMsg.substring(strMsg.indexOf("<CAE>") + 5, strMsg.indexOf("</CAE>"));
                caeFchVto = strMsg.substring(strMsg.indexOf("<CAEFchVto>") + 11, strMsg.indexOf("</CAEFchVto>"));
            } else {
                codeError = strMsg.substring(strMsg.indexOf("<Code>") + 6, strMsg.indexOf("</Code>"));
                mesaggeError = strMsg.substring(strMsg.indexOf("<Msg>") + 5, strMsg.indexOf("</Msg>"));
            }

            soapConnection.close();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            return null;

        }
        return CaeDTO.builder()
                       .cae(cae)
                       .caeFechaVto(caeFchVto)
                       .codeError(codeError)
                       .messageError(mesaggeError)
                       .build();
    }

    private SOAPMessage createSOAPRequest(String soapAction, ComprobanteRequest comprobanteRequest) throws Exception {

    	
    	/*
    	 MessageFactory messageFactory = MessageFactory.newInstance();
         SOAPMessage soapMessage = messageFactory.createMessage();
         createSoapEnvelope(soapMessage);
    	 */

        //cambie las 3 lineas anteriores por la siguiente.
        SOAPMessage soapMessage = createMesaggeFECAESolicitar(comprobanteRequest);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        soapMessage.writeTo(System.out);


        return soapMessage;
    }


    private SOAPMessage createMesaggeFECAESolicitar(ComprobanteRequest comprobanteRequest) {

        String soapMessageWithLeadingComment =
                        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\"><SOAP-ENV:Header/><SOAP-ENV:Body>"
                        + "<ar:FECAESolicitar>"

                        + "<ar:Auth>"
                        + "<ar:Token>" + token + "</ar:Token>"
                        + "<ar:Sign>" + sign + "</ar:Sign>"
                        + "<ar:Cuit>" + cuit + "</ar:Cuit>"
                        + "</ar:Auth>"
                        + "<ar:FeCAEReq>"
                        + "<ar:FeCabReq>"
                        + "<ar:CantReg>1</ar:CantReg>"
                        + "<ar:PtoVta>" + comprobanteRequest.getPtoVta() + "</ar:PtoVta>"
                        + "<ar:CbteTipo>" + comprobanteRequest.getCbteTipo() + "</ar:CbteTipo>"
                        + "</ar:FeCabReq>"
                        + "<ar:FeDetReq>"
                        + "<ar:FECAEDetRequest>"
                        + generarDatosFact(comprobanteRequest)
                        + generarCompAsociados(comprobanteRequest)
                        + generarDatosTributos(comprobanteRequest)
                        + generarDatosIva(comprobanteRequest)
                        + "</ar:FECAEDetRequest>"
                        + "</ar:FeDetReq>"
                        + "</ar:FeCAEReq>"
                        + "</ar:FECAESolicitar>"
                        + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";


        log.debug(soapMessageWithLeadingComment);
        SOAPMessage soapMessage = null;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(
                            soapMessageWithLeadingComment.getBytes()));
            SOAPPart part = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
        } catch (SOAPException | IOException e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
        }
        return soapMessage;

    }

    public String generarDatosFact(ComprobanteRequest comprobanteRequest) {

        String ret =
                "<ar:Concepto>" + comprobanteRequest.getConcepto() + "</ar:Concepto> "
                + "<ar:DocTipo>" + comprobanteRequest.getDocTipo() + "</ar:DocTipo>"
                + "<ar:DocNro>" + comprobanteRequest.getDocNro() + "</ar:DocNro>"
                + "<ar:CbteDesde>" + comprobanteRequest.getCbteDesde() + "</ar:CbteDesde>"
                + "<ar:CbteHasta>" + comprobanteRequest.getCbteHasta() + "</ar:CbteHasta>"
                + "<ar:CbteFch>" + comprobanteRequest.getCbteFch() + "</ar:CbteFch>"
                + "<ar:ImpTotal>" + comprobanteRequest.getImpTotal() + "</ar:ImpTotal> "
                + " <ar:ImpTotConc>" + comprobanteRequest.getImpTotConc() + "</ar:ImpTotConc>"
                + "<ar:ImpNeto>" + comprobanteRequest.getImpNeto() + "</ar:ImpNeto>"
                + " <ar:ImpOpEx>" + comprobanteRequest.getImpOpEx() + "</ar:ImpOpEx>"
                + "<ar:ImpTrib>" + comprobanteRequest.getImpTrib() + "</ar:ImpTrib>"
                + " <ar:ImpIVA>" + comprobanteRequest.getImpIVA() + "</ar:ImpIVA>"
                + "<ar:FchServDesde></ar:FchServDesde>"
                + "<ar:FchServHasta></ar:FchServHasta>"
                + "<ar:FchVtoPago></ar:FchVtoPago>"
                + "<ar:MonId>PES</ar:MonId>"
                + (comprobanteRequest.getCondicionIVAReceptorId() > 0 ? "<ar:CondicionIVAReceptorId>int</ar" +
                                                                               ":CondicionIVAReceptorId>" : "")
                + "<ar:MonCotiz>" + comprobanteRequest.getMonCotiz() + "</ar:MonCotiz>";

        return ret;

    }

    private String generarDatosTributos(ComprobanteRequest comprobanteRequest) {
        if (comprobanteRequest.getIdTributo() == 0) {
            return EMPTY;
        }
        String ret =
                "<ar:Tributos>"
                + "<ar:Tributo>"
                + "<ar:Id>" + comprobanteRequest.getIdTributo() + "</ar:Id>"
                + "<ar:Desc>" + comprobanteRequest.getDescTributo() + "</ar:Desc>"
                + "<ar:BaseImp>" + comprobanteRequest.getBaseImpTributo() + "</ar:BaseImp>"
                // +"<ar:Alic>"+alicTributo+"</ar:Alic>"
                + "<ar:Importe>" + comprobanteRequest.getImporteTributo() + "</ar:Importe>"
                + "</ar:Tributo>"
                + "</ar:Tributos>";

        return ret;
    }

    private String generarCompAsociados(ComprobanteRequest comprobanteRequest) {
        if (comprobanteRequest.getCompAsociado() == null) {
            return EMPTY;
        }
        log.debug("comprobante asociado {}", comprobanteRequest.getCompAsociado() );

        int tipo = 0;
        if (comprobanteRequest.getCompAsociado() .getTipo_comprobante() == 1) {
            tipo = 1;
        } else if ((comprobanteRequest.getCompAsociado() .getTipo_comprobante() == 5)) { // factura B de epyme
            tipo = 6;// factura b de webservice,
        } else if ((comprobanteRequest.getCompAsociado() .getTipo_comprobante() == 14)) {
            tipo = 11;// factura c, no se si es correcto ese valor
        }

        String cbteFecha = comprobanteRequest.getCompAsociado() .getFecha_comprobante().replaceAll("-", EMPTY);

        String ret =
                "<ar:CbtesAsoc>" +
                        " <ar:CbteAsoc>" +
                        " <ar:Tipo>" + tipo + "</ar:Tipo>" +
                        " <ar:PtoVta>" + comprobanteRequest.getCompAsociado() .getPunto_venta() + "</ar:PtoVta>" +
                        " <ar:Nro>" + comprobanteRequest.getCompAsociado() .getNumero() + "</ar:Nro>" +
                        "<ar:Cuit>" + cuit + "</ar:Cuit>" +
                        "<ar:CbteFch>" + cbteFecha + "</ar:CbteFch> " +
                        "</ar:CbteAsoc>" +
                        " </ar:CbtesAsoc>";
        return ret;


    }

    private String generarDatosIva(ComprobanteRequest comprobanteRequest) {
        //que pasa si no tengo que enviar datos de iva ?


        String ret;
        if (comprobanteRequest.getImpNeto() > 0) {
            ret = "<ar:Iva>";
            if (comprobanteRequest.getIdIva() != 0) {
                ret = ret

                        + "<ar:AlicIva>"
                        + "<ar:Id>" + comprobanteRequest.getIdIva() + "</ar:Id>"
                        + "<ar:BaseImp>" + comprobanteRequest.getBaseImp() + "</ar:BaseImp>"
                        + "<ar:Importe>" + comprobanteRequest.getImporteIva() + "</ar:Importe>"
                        + "</ar:AlicIva>";
            }
            if (comprobanteRequest.getIdIva2() != 0) {
                ret = ret
                        + "<ar:AlicIva>"
                        + "<ar:Id>" + comprobanteRequest.getIdIva2() + "</ar:Id>"
                        + "<ar:BaseImp>" + comprobanteRequest.getBaseImp2()  + "</ar:BaseImp>"
                        + "<ar:Importe>" + comprobanteRequest.getImporteIva2() + "</ar:Importe>"
                        + "</ar:AlicIva>";
            }
            if (comprobanteRequest.getIdIva3() != 0) {
                ret = ret
                        + "<ar:AlicIva>"
                        + "<ar:Id>" + comprobanteRequest.getIdIva3() + "</ar:Id>"
                        + "<ar:BaseImp>" + comprobanteRequest.getBaseImp3()  + "</ar:BaseImp>"
                        + "<ar:Importe>" + comprobanteRequest.getImporteIva3() + "</ar:Importe>"
                        + "</ar:AlicIva>";
            }

            ret = ret + "</ar:Iva>";
        } else ret = EMPTY;
        return ret;
    }


    private URL buildURL(String soapEndpointUrl) throws MalformedURLException {
        return new URL(new URL(soapEndpointUrl),
                EMPTY,
                new URLStreamHandler() {
                    @Override
                    protected URLConnection openConnection(URL url) throws IOException {
                        URL target = new URL(url.toString());
                        URLConnection connection = target.openConnection();
                        // Connection settings
                        connection.setConnectTimeout(5000); // 5 sec
                        connection.setReadTimeout(5000); // 5
                        return(connection);
                    }
                });
    }



    public String getMessage() {
        return message;
    }

}