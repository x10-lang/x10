package polyglot.ext.x10.plugin;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import polyglot.util.InternalCompilerError;

public class XMLReader {
	Document doc;

	public XMLReader(String name) throws IOException {
		super();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(name);
		}
		catch (ParserConfigurationException e) {
			throw new InternalCompilerError(e.getMessage());
		}
		catch (SAXException e) {
			throw new InternalCompilerError(e.getMessage());
		}
	}

	public Element readElement() throws IOException {
		return doc.getDocumentElement();
	}
	
	public void close() throws IOException { }
}
