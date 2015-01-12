/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.dom;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import polyglot.util.InternalCompilerError;

public class XMLReader {
	protected Document doc;
	protected DocumentBuilder builder;
	
	public XMLReader() {
		super();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setValidating(false);
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new InternalCompilerError(e.getMessage());
		}
	}
	
	public XMLReader(Reader reader) throws IOException {
		this();
		try {
			doc = builder.parse(new ReaderInputStream(reader));
		}
		catch (SAXException e) {
			throw new InternalCompilerError(e.getMessage());
		}
	}
	
	public XMLReader(String name) throws IOException {
		this();
		try {
			doc = builder.parse(name);
		}
		catch (SAXException e) {
			throw new InternalCompilerError(e.getMessage());
		}
	}

	public Element readElement() throws IOException {
		return doc.getDocumentElement();
	}
	
	public void close() throws IOException {
	}
	
	/** Wrap a Reader in an InputStream.  java.io should have this. */
	protected static class ReaderInputStream extends InputStream {
		private Reader r;
		
		public ReaderInputStream(Reader r) {
			super();
			this.r = r;
		}
		
		public int read() throws IOException {
			return r.read();
		}
	}
}
