package org.api_sync.services.afip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.Authentication;
import org.api_sync.adapter.outbound.entities.Certificado;
import org.api_sync.adapter.outbound.repository.AuthenticationRepository;
import org.api_sync.adapter.outbound.repository.CertificadosRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;


@Slf4j
@Service
@RequiredArgsConstructor
public class AfipAuthentificationClient {

    private final CertificadosRepository certificadosRepository;
    private final AuthenticationRepository authenticationRepository;
    private final AfipLoginClient afipLoginClient;

    public Authentication getAuthentication(String cuit, Integer puntoVenta) throws Exception {

        
        Optional<Authentication> authenticationOptional =
                authenticationRepository.findTopByCuitAndPuntoVentaOrderByExpirationTime(cuit, puntoVenta);
                
        if (authenticationOptional.isPresent() && !authenticationOptional.get().expired() && authenticationOptional.get().isValid()) {
            return authenticationOptional.get();
        } else { //tengo que obtener nuevas llaves y borrar las que estan en la bd ya que no sirven mas
     
            return generateNewToken(cuit, puntoVenta,
                    authenticationOptional.isPresent() ? authenticationOptional.get().getId() : null);
        }

    }

    private Authentication generateNewToken(String cuit, Integer puntoVenta, Long authenticationId) throws Exception {
    
    
        String loginTicketResponse = null;
    
        String service = "wsfe";
        String dstDN = "cn=wsaa,o=afip,c=ar,serialNumber=CUIT 33693450239"; //ambiente de homologacion
        String endpoint = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    
        String signer = "facu";
    
        // Get token & sign from LoginTicketResponse
    
    
        String token = EMPTY;
        String sign = EMPTY;
        String expirationTime = EMPTY;
        
        Optional<Certificado> certificado
                = certificadosRepository.findTopByCuitAndPuntoVentaOrderByFechaCreadoDesc(cuit, puntoVenta);
    
        if (!certificado.isPresent()) {
            throw new RuntimeException("No hay certificado para el pv: " + puntoVenta);
        }
    
        // Create LoginTicketRequest_xml_cms
        byte[] loginTicketRequest_xml_cms = afipLoginClient.create_cms(
                certificado.get().getArchivo(),
                certificado.get().getPassword(),
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
        
            Authentication authentication = Authentication.builder()
                                                    .id(authenticationId) //El id es para actualizar el valor
                                                    // existente y no estar sumaando nuevas filas a la base de datos,
                                                    // hay que ahorrar
                                                    .cuit(cuit)
                                                    .puntoVenta(puntoVenta)
                                                    .token(token)
                                                    .sign(sign)
                                                    .expirationTime(expirationTime)
                                                    .build();
            
            writeKeys(authentication);
            return authentication;
        }
        
        throw new Exception("Token invalido");
    }
    
    private void writeKeys(Authentication authentication) {
        authenticationRepository.save(authentication);
    }

}
