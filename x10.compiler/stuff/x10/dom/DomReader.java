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

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import polyglot.ast.Node;
import x10.ast.X10NodeFactory;
import x10.dom.X10Dom.Lens;
import x10.dom.X10Dom.NodeLens;
import x10.dom.X10Dom.TypeObjectLens;
import x10.types.X10TypeSystem;
import polyglot.frontend.Source;
import polyglot.types.TypeObject;

public class DomReader {
	X10TypeSystem ts;
	X10NodeFactory nf;
	Source source;
	Map<String,LazyTypeObject> typeMap;
	
	public DomReader(X10TypeSystem ts, X10NodeFactory nf, Source source) {
		super();
		this.ts = ts;
		this.nf = nf;
		this.source = source;
	}
	
	public Source source() {
		return source;
	}
	
	public Node fromXML(X10Dom dom, Element e) {
		Map<String,TypeObject> typeMap;
		Element types = dom.getChild(e, "TypeSystem");
		buildTypeMap(dom, types);
		Element ast = dom.getChild(e, "AbstractSyntaxTree");
		ast = dom.getFirstElement(ast);
		Node n = dom.new NodeLens().fromXML(this, ast);
		return n;
	}
	
	class LazyTypeObject {
		Object o;
		Element e;
		TypeObjectLens lens;
		
		LazyTypeObject(Element e) { }
		
		LazyTypeObject(Element e, TypeObjectLens lens) {
			this.e = e;
			this.lens = lens;
			lens.lto = this;
		}
		
		Object force(X10Dom dom) {
			if (o != null)
				return o;
			return o = dom.get(lens, e, "value", DomReader.this);
		}
	}
	
	void buildTypeMap(X10Dom dom, Element e) {
		typeMap = CollectionFactory.newHashMap();
		for (org.w3c.dom.Node child = e.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child instanceof org.w3c.dom.Text) {
				continue;
			}
			Element x = (Element) child;
			Element name = dom.getChild(x, "key");
			String key = dom.new StringLens().fromXML(this, name);
			typeMap.put(key, new LazyTypeObject(x, dom.new TypeObjectLens()));
		}
	}

}
