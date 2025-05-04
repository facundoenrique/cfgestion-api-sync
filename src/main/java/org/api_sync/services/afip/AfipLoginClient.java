package org.api_sync.services.afip;

// El Departamento de Seguridad Informatica de la AFIP (DeSeIn/AFIP), pone a disposicion
// el siguiente codigo para su utilizacion con el WebService de Autenticacion y Autorizacion
// de la AFIP.
//
// El mismo no puede ser re-distribuido, publicado o descargado en forma total o parcial, ya sea
// en forma electronica, mecanica u optica, sin la autorizacion de DeSeIn/AFIP. El uso no
// autorizado del mismo esta prohibido.
//
// DeSeIn/AFIP no asume ninguna responsabilidad de los errores que pueda contener el codigo ni la
// obligacion de subsanar dichos errores o informar de la existencia de los mismos.
//
// DeSeIn/AFIP no asume ninguna responsabilidad que surja de la utilizacion del codigo, ya sea por
// utilizacion ilegal de patentes, perdida de beneficios, perdida de informacion o cualquier otro
// inconveniente.
//
// Bajo ninguna circunstancia DeSeIn/AFIP podra ser indicada como responsable por consecuencias y/o
// incidentes ya sean directos o indirectos que puedan surgir de la utilizacion del codigo.
//
// DeSeIn/AFIP no da ninguna garantia, expresa o implicita, de la utilidad del codigo, si el mismo es
// correcto, o si cumple con los requerimientos de algun proposito en particular.
//
// DeSeIn/AFIP puede realizar cambios en cualquier momento en el codigo sin previo aviso.
//
// El codigo debera ser evaluado, verificado, corregido y/o adaptado por personal tecnico calificado
// de las entidades que lo utilicen.
//
// EL SIGUIENTE CODIGO ES DISTRIBUIDO PARA EVALUACION, CON TODOS SUS ERRORES Y OMISIONES. LA
// RESPONSABILIDAD DEL CORRECTO FUNCIONAMIENTO DEL MISMO YA SEA POR SI SOLO O COMO PARTE DE
// OTRA APLICACION, QUEDA A CARGO DE LAS ENTIDADES QUE LO UTILICEN. LA UTILIZACION DEL CODIGO
// SIGNIFICA LA ACEPTACION DE TODOS LOS TERMINOS Y CONDICIONES MENCIONADAS ANTERIORMENTE.
//
// Version 1.0
// gp/rg/OF.G. DeSeIn-AFIP
//

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.Base64;
import org.apache.axis.encoding.XMLType;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Repository;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.rpc.ParameterMode;
import java.io.*;
import java.security.*;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
@NoArgsConstructor
@Repository
public class AfipLoginClient {

    public String invoke_wsaa(byte[] LoginTicketRequest_xml_cms, String endpoint) throws Exception {

        String LoginTicketResponse = null;


        Service service = new Service();
        Call call = (Call) service.createCall();

        //
        // Prepare the call for the Web service
        //
        call.setTargetEndpointAddress(new java.net.URL(endpoint));
        call.setOperationName("loginCms");
        call.addParameter("request", XMLType.XSD_STRING, ParameterMode.IN);
        call.setReturnType(XMLType.XSD_STRING);

        //
        // Make the actual call and assign the answer to a String
        //
        LoginTicketResponse = (String) call.invoke(new Object[]{
                Base64.encode(LoginTicketRequest_xml_cms)});


        return (LoginTicketResponse);
    }

    //
    // Create the CMS Message
    //
    public byte[] create_cms(byte[] p12file, String p12pass, String signer, String dstDN, String service) {

        if (p12file == null || p12file.length==0) {
            throw new RuntimeException("Certificado no valido");
        }

        PrivateKey pKey = null;
        X509Certificate pCertificate = null;
        byte[] asn1_cms = null;
        CertStore cstore = null;
        String LoginTicketRequest_xml;
        String SignerDN = null;

        // Manage Keys & Certificates
        //
        try {
            // Create a keystore using keys from the pkcs#12 p12file
            KeyStore ks = KeyStore.getInstance("pkcs12");
            InputStream p12stream = new ByteArrayInputStream(p12file);
            ks.load(p12stream, p12pass.toCharArray());
            p12stream.close();

            // Get Certificate & Private key from KeyStore

            pKey = (PrivateKey) ks.getKey(signer, p12pass.toCharArray());
            pCertificate = (X509Certificate) ks.getCertificate(signer);

            SignerDN = pCertificate.getSubjectDN().toString();

            // Create a list of Certificates to include in the final CMS
            ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>();
            certList.add(pCertificate);

            if (Security.getProvider("BC") == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
            cstore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), "BC");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        //
        // Create XML Message
        // 
        LoginTicketRequest_xml = create_LoginTicketRequest(SignerDN, dstDN, service);

        //
        // Create CMS Message
        //
        try {
            // Create a new empty CMS Message
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

            // Add a Signer to the Message
            gen.addSigner(pKey, pCertificate, CMSSignedDataGenerator.DIGEST_SHA1);

            // Add the Certificate to the Message
            gen.addCertificatesAndCRLs(cstore);

            // Add the data (XML) to the Message
            CMSProcessable data = new CMSProcessableByteArray(LoginTicketRequest_xml.getBytes());

            // Add a Sign of the Data to the Message
            CMSSignedData signed = gen.generate(data, true, "BC");

            // 
            asn1_cms = signed.getEncoded();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return (asn1_cms);
    }

    //
    // Create XML Message for AFIP wsaa
    // 	
    private String create_LoginTicketRequest(String SignerDN, String dstDN, String service) {
    
        String LoginTicketRequest_xml;
    
    
        TimeFetcher timeFetcher = new TimeFetcher();
        Date genTime = timeFetcher.getServerTime();
    
        long ticketTime = 3600000; // 1 hour in ms
    
        String uniqueId = Long.toString(genTime.getTime() / 1000);
    
        // Calculate expTime manually without plusMillis
        Date expTime = new Date(genTime.getTime() + ticketTime);
    
        try {
        
            XMLGregorianCalendar xmlGenTime = toXmlGregorianCalendar(genTime);
            XMLGregorianCalendar xmlExpTime = toXmlGregorianCalendar(expTime);
        
            LoginTicketRequest_xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                                             + "<loginTicketRequest version=\"1.0\">"
                                             + "<header>"
                                             + "<source>" + SignerDN + "</source>"
                                             + "<destination>" + dstDN + "</destination>"
                                             + "<uniqueId>" + uniqueId + "</uniqueId>"
                                             + "<generationTime>" + xmlGenTime + "</generationTime>"
                                             + "<expirationTime>" + xmlExpTime + "</expirationTime>"
                                             + "</header>"
                                             + "<service>" + service + "</service>"
                                             + "</loginTicketRequest>";
        
            return LoginTicketRequest_xml;
        
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return EMPTY;
    }

    private static XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws Exception {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    }

}
