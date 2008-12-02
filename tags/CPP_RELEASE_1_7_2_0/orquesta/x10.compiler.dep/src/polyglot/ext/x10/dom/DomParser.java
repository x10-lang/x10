/*
 *
 * (C) Copyright IBM Corporation 2007
 *
 *  This file is part of X10 Compiler.
 *
 */
package polyglot.ext.x10.dom;

import java.io.IOException;
import java.io.Reader;

import org.w3c.dom.Element;

import polyglot.ast.Node;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.FileSource;
import polyglot.frontend.Parser;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;

public class DomParser implements Parser {

	protected Reader reader;
	protected X10TypeSystem ts;
	protected X10NodeFactory nf;
	protected FileSource source;
	protected ErrorQueue eq;

	public DomParser(Reader reader, X10TypeSystem ts, X10NodeFactory nf, FileSource source, ErrorQueue eq) {
		super();
		this.reader = reader;
		this.ts = ts;
		this.nf = nf;
		this.source = source;
		this.eq = eq;
	}

	public Node parse() {
		Element e;
		try {
			XMLReader r = new XMLReader(reader);
			e = r.readElement();
			r.close();
		}
		catch (IOException ex) {
			eq.enqueue(ErrorInfo.IO_ERROR, ex.getMessage());
			return null;
		}
		
		X10Dom dom = new X10Dom(ts, nf);
		DomReader ungen = new DomReader(ts, nf, source);
		return ungen.fromXML(dom, e);
	}
}
