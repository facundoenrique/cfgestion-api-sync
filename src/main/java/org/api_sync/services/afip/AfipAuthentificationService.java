package org.api_sync.services.afip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.api_sync.adapter.outbound.entities.Authentication;
import org.api_sync.adapter.outbound.entities.Certificado;
import org.api_sync.adapter.outbound.repository.AuthenticationRepository;
import org.api_sync.adapter.outbound.repository.CertificadosRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import static org.apache.commons.lang3.StringUtils.EMPTY;


@Slf4j
@Service
@RequiredArgsConstructor
public class AfipAuthentificationService {

    private final CertificadosRepository certificadosRepository;
    private final AuthenticationRepository authenticationRepository;
    private final AfipLoginClient afipLoginClient;

    public Authentication getAuthentication(Long empresaId, Integer puntoVenta) throws Exception {

        String loginTicketResponse = null;
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "80");

        String endpoint = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
        String service = "wsfe";
        String dstDN = "cn=wsaa,o=afip,c=ar,serialNumber=CUIT 33693450239"; //ambiente de homologacion
        endpoint = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
        
        String signer = "facu";
        String p12pass = "mastermix";
        
        
        // Get token & sign from LoginTicketResponse


        String token = EMPTY;
        String sign = EMPTY;
        String expirationTime = EMPTY;
        
        Authentication authentication = authenticationRepository.findByEmpresaAndPuntoVenta(empresaId, puntoVenta);
        
        if (authentication.expired() && authentication.isValid()) {
            return authentication;
        } else { //tengo que obtener nuevas llaves
    
            // Invoke AFIP wsaa and get LoginTicketResponse
            
            Certificado certificado = certificadosRepository.findByEmpresaAndPuntoVenta(empresaId, puntoVenta);
            
            // Create LoginTicketRequest_xml_cms
            byte[] loginTicketRequest_xml_cms = afipLoginClient.create_cms(
                    certificado.getArchivo(),
                    certificado.getPassword(),
                    signer, dstDN, service);
            
            log.info("Llaves vencidas, creando nuevas");
            loginTicketResponse = afipLoginClient.invoke_wsaa(loginTicketRequest_xml_cms, endpoint);
            
            if (loginTicketResponse != null) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new org.xml.sax.InputSource(new StringReader(loginTicketResponse)));
    
                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
    
                token = xpath.evaluate("/loginTicketResponse/credentials/token", document);
                sign = xpath.evaluate("/loginTicketResponse/credentials/sign", document);
                expirationTime = xpath.evaluate("/loginTicketResponse/header/expirationTime", document);
    
                log.debug("Token: " + token);
                log.debug("Sign: " + sign);
                log.debug("ExpirationTime: " + expirationTime);
                
                writeKeys(Authentication.builder()
                                  .token(token)
                                  .sign(sign)
                                  .expirationTime(expirationTime)
                                  .build());
            }
        }

        if (StringUtils.isNotBlank(token)) {
            return Authentication.builder()
                           .token(token)
                           .sign(sign)
                           .expirationTime(expirationTime)
                           .build();
        }

        throw new Exception("Token invalido");

    }

    private void writeKeys(Authentication authentication) {

        authenticationRepository.save(authentication);

    }

}
