package org.api_sync.services.afip.soap;
import org.api_sync.services.afip.model.AfipErrorResponse;
import org.api_sync.services.afip.model.AfipEventResponse;
import org.api_sync.services.afip.model.AfipResponseDetails;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.StringReader;
import org.xml.sax.InputSource;
import java.util.ArrayList;
import java.util.List;

public class AfipSoapParser {

	public static AfipResponseDetails extractErrors(String xml) {
		List<AfipErrorResponse> errors = new ArrayList<>();
		List<AfipEventResponse> events = new ArrayList<>();
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document doc = builder.parse(new InputSource(new StringReader(xml)));
			
			// Extraer errores
			NodeList errNodes = doc.getElementsByTagName("Err");
			for (int i = 0; i < errNodes.getLength(); i++) {
				Element err = (Element) errNodes.item(i);
				int code = Integer.parseInt(err.getElementsByTagName("Code").item(0).getTextContent());
				String msg = err.getElementsByTagName("Msg").item(0).getTextContent();
				errors.add(new AfipErrorResponse(code, msg));
			}
			
			// Extraer eventos
			NodeList evtNodes = doc.getElementsByTagName("Evt");
			for (int i = 0; i < evtNodes.getLength(); i++) {
				Element evt = (Element) evtNodes.item(i);
				int code = Integer.parseInt(evt.getElementsByTagName("Code").item(0).getTextContent());
				String msg = evt.getElementsByTagName("Msg").item(0).getTextContent();
				events.add(new AfipEventResponse(code, msg));
			}
			
		} catch (Exception e) {
			e.printStackTrace(); // Podés registrar este error en logs
		}
		
		return new AfipResponseDetails(errors, events);
	}
}
