/**
 * 
 */
package polyglot.ext.x10.visit;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import polyglot.ast.Node;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.visit.X10Dom.NodeLens;
import polyglot.types.TypeObject;

public class DomReader {
	X10TypeSystem ts;
	X10NodeFactory nf;
	Map<String,LazyTypeObject> typeMap;
	
	public DomReader(X10TypeSystem ts, X10NodeFactory nf) {
		super();
		this.ts = ts;
		this.nf = nf;
	}
	
	public Node fromXML(X10Dom dom, Element e) {
		Map<String,TypeObject> typeMap;
		Element types = dom.getChild(e, "types");
		buildTypeMap(dom, types);
		Element ast = dom.getChild(e, "ast");
		ast = dom.getFirstElement(ast);
		Node n = dom.new NodeLens().fromXML(this, ast);
		return n;
	}
	
	class LazyTypeObject {
		TypeObject o;
		Element e;
		
		LazyTypeObject(Element e) {
			this.e = e;
		}
		
		TypeObject force(X10Dom dom) {
			if (o != null)
				return o;
			return dom.get(dom.new TypeObjectLens(), e, "value", DomReader.this);
		}
	}
	
	void buildTypeMap(X10Dom dom, Element e) {
		typeMap = new HashMap<String,LazyTypeObject>();
		for (org.w3c.dom.Node child = e.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child instanceof org.w3c.dom.Text) {
				continue;
			}
			Element x = (Element) child;
			Element name = dom.getChild(x, "key");
//			Element obj = dom.getChild(x, "value");
			String key = dom.new StringLens().fromXML(this, name);
			typeMap.put(key, new LazyTypeObject(x));
		}
	}

}