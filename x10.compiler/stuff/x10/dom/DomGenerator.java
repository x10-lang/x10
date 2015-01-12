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

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import polyglot.ast.Node;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.util.Copy;
import polyglot.util.IdentityKey;
import polyglot.util.InternalCompilerError;

public class DomGenerator implements Copy {
	public DomGenerator() {
		super();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation domImpl = builder.getDOMImplementation();
			scratchPad = domImpl.createDocument(null, "x10ml", null);
		}
		catch (FactoryConfigurationError e) {
			throw new InternalCompilerError("Could not configure factory.", e);
		}
		catch (ParserConfigurationException e) {
			throw new InternalCompilerError("Could not configure parser.", e);
		}
		catch (Exception e) {
			throw new InternalCompilerError(e);
		}
		
		typesMap = CollectionFactory.newHashMap();
	}
	
	public Element gen(X10Dom dom, Node n) {
		root = createElement(null, "X10Source");
		parent = root;
		types = createElement(root, "TypeSystem");
		dom.gen(this, "AbstractSyntaxTree", n);
		return root;
	}
	
	Element root;
	Element types;
	Element parent;
	Map<IdentityKey,String> typesMap;
	Document scratchPad;
	
	public Element get() {
		return parent;
	}
	
	public Object copy() {
		try {
			return (DomGenerator) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new InternalCompilerError("Java clone() weirdness");
		}
	}
	
	public Element createElement(Element parent, String tag) {
		Element e = scratchPad.createElement(tag);
		if (parent != null)
			parent.appendChild(e);
		return e;
	}
	
	public void createAttribute(Element parent, String tag, String value) {
		assert(parent != null);
		parent.setAttribute(tag, value);
	}
	
	public void createText(Element parent, String content) {
		assert(parent != null);
		Document doc = parent.getOwnerDocument();
		Text t = doc.createTextNode(content);
		parent.appendChild(t);
	}
	
	public void createPI(Element parent, String target, String content) {
		assert(parent != null);
		Document doc = parent.getOwnerDocument();
		ProcessingInstruction t = doc.createProcessingInstruction(target,content);
		parent.appendChild(t);
	}
	
	public void createComment(Element parent, String content) {
		assert(parent != null);
		Document doc = parent.getOwnerDocument();
		Comment t = doc.createComment(content);
		parent.appendChild(t);
	}
	
	public Element setAttribute(Element elem, String attrName, Object value) {
		elem.setAttribute(attrName, value.toString());
		return elem;
	}
	
	/**
	 *
	 * @param elem
	 * @param newChild
	 * @return elem
	 */
	public void appendChild(Element elem, org.w3c.dom.Node newChild) {
		elem.appendChild(elem.getOwnerDocument().importNode(newChild, true));
	}
	
	/**
	 *
	 * @param elem
	 * @param newChild
	 * @return elem
	 */
	public void appendChildren(Element elem, Object newTextChild) {
		elem.appendChild(scratchPad.createTextNode(newTextChild.toString()));
	}
	
	public void appendChildren(Element elem, List list) {
		Document d = elem.getOwnerDocument();
		for (Iterator i = list.iterator(); i.hasNext();) {
			org.w3c.dom.Node n = (org.w3c.dom.Node) i.next();
			elem.appendChild(d.importNode(n, true));
		}
	}
	
	/**
	 *
	 */
	public void insertBefore(Element parent, Element ref, Element newChild) {
		parent.insertBefore(newChild, ref);
	}
	
	/**
	 *
	 */
	public void insertAfter(Element parent, Element ref, Element newChild) {
		org.w3c.dom.Node next = ref.getNextSibling();
		if (next == null) {
			parent.appendChild(newChild);
		}
		else {
			parent.insertBefore(newChild, next);
		}
	}
	
	/**
	 * @param elem
	 * @param newChildren an array of elements
	 * @return elem
	 */
	public void appendChildren(Element elem, Element[] newChildren) {
		for (int i = 0; i < newChildren.length; i++) {
			elem.appendChild(newChildren[i]);
		}
	}
	
	public DomGenerator tag(String tag) {
		DomGenerator v = (DomGenerator) copy();
		v.parent = createElement(parent, tag);
		return v;
	}
	
	public <T> DomGenerator gen(String tag, T n, X10Dom.Lens<T> lens) {
		DomGenerator v = tag(tag);
		lens.toXML(v, n);
		return this;
	}
}
