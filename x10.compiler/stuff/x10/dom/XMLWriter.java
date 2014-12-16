/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.dom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import polyglot.util.InternalCompilerError;

public class XMLWriter {
	
	FileOutputStream w;

	public XMLWriter(String name) throws IOException {
		super();
		File f = new File(name);
		File dir = f.getParentFile();
		if (! dir.exists()) {
			dir.mkdirs();
		}
		w = new FileOutputStream(f);
	}

	public void writeElement(Element n) throws IOException {
		DOMSource source = new DOMSource(n);
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer format = transFactory.newTransformer();
			format.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			format.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			format.setOutputProperty(OutputKeys.INDENT, "yes");
			format.transform(source, new StreamResult(w));
		}
		catch (TransformerException e) {
			throw new InternalCompilerError("Error in serializing data to stream");
		}
	}
	
	public void close() throws IOException {
		w.close();
	}

}
