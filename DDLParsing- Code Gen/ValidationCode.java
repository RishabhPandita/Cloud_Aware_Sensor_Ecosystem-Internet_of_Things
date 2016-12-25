import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

public class ValidationCode {

	public static void main(String[] args) {
		boolean flag = true;
		
		try {
			validate("Input.xsd", "Input.xml");
			validate("ADCInput.xsd", "ADCInput.xml");
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		}
		System.out.println("XML file is valid :" + flag);
	}

	public static void validate(String xsdFile, String xmlFile) throws SAXException, IOException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.newSchema(new File(xsdFile)).newValidator().validate(new StreamSource(new File(xmlFile)));

	}

}
