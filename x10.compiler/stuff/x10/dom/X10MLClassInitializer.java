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

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import x10.dom.X10Dom.ConstructorInstanceRefLens;
import x10.dom.X10Dom.FieldInstanceRefLens;
import x10.dom.X10Dom.ListLens;
import x10.dom.X10Dom.MethodInstanceRefLens;
import x10.dom.X10Dom.TypeRefLens;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeSystem;
import polyglot.types.ConstructorInstance;
import polyglot.types.DeserializedClassInitializer;
import polyglot.types.FieldInstance;
import polyglot.types.LazyClassInitializer;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.Type;
import polyglot.util.Position;

public class X10MLClassInitializer implements LazyClassInitializer {
	
	X10TypeSystem ts;
	X10ParsedClassType ct;
	X10Dom dom;
	DomReader v;
	Element e;
	
	public X10MLClassInitializer(X10TypeSystem ts, X10Dom dom, DomReader v, Element e) {
		this.ts = ts;
		this.dom = dom;
		this.v = v;
		this.e = e;
	}
	
	public void setClass(ParsedClassType ct) {
		this.ct = (X10ParsedClassType) ct;
	}
	
	public boolean fromClassFile() {
		return false;
	}
	
	public void initTypeObject() {
		if (ct.isMember() && ct.outer() instanceof ParsedClassType) {
			ParsedClassType outer = (ParsedClassType) ct.outer();
			outer.addMemberClass(ct);
		}
		for (Iterator i = ct.memberClasses().iterator(); i.hasNext(); ) {
			ParsedClassType ct = (ParsedClassType) i.next();
			ct.initializer().initTypeObject();
		}
		this.init = true;
	}
	
	public boolean isTypeObjectInitialized() {
		return this.init;
	}
	
	protected boolean init;
	protected boolean constructorsInitialized;
	protected boolean fieldsInitialized;
	protected boolean interfacesInitialized;
	protected boolean memberClassesInitialized;
	protected boolean methodsInitialized;
	protected boolean superclassInitialized;
	
	public void initSuperclass() {
		if (superclassInitialized) {
			return;
		}

		Type superclass = dom.get(dom.new TypeRefLens(), e, "superclass", v);
		ct.superType(superclass);
	
		superclassInitialized = true;
		
		if (superclassInitialized && interfacesInitialized) {
			ct.setSupertypesResolved(true);
		}
		
		if (initialized()) {
			v = null;
			e = null;
			dom = null;
		}
	}
	
	public void initInterfaces() {
		if (interfacesInitialized) {
			return;
		}
		
		List<Type> interfaces = dom.get(dom.new ListLens<Type>(dom.new TypeRefLens()), e, "interfaces", v);
		ct.setInterfaces(interfaces);
		
		interfacesInitialized = true;
		
		if (superclassInitialized && interfacesInitialized) {
			ct.setSupertypesResolved(true);
		}
		
		if (initialized()) {
			v = null;
			e = null;
			dom = null;
		}
	}
	
	public void initMemberClasses() {
		if (memberClassesInitialized) {
			return;
		}
		
		List memberClasses = dom.get(dom.new ListLens<Type>(dom.new TypeRefLens()), e, "memberClasses", v);
		ct.setMemberClasses(memberClasses);
		
		memberClassesInitialized = true;
		
		if (initialized()) {
			v = null;
			e = null;
			dom = null;
		}
	}
	
	public void canonicalFields() {
		initFields();
	}
	
	public void canonicalMethods() {
		initMethods();
	}
	
	public void canonicalConstructors() {
		initConstructors();
	}
	
	public void initFields() {
		if (fieldsInitialized) {
			return;
		}
		
		List<FieldInstance> fields = dom.get(dom.new ListLens<FieldInstance>(dom.new FieldInstanceRefLens()), e, "fields", v);
		ct.setFields(fields);
		
		
		fieldsInitialized = true;
		
		if (initialized()) {
			v = null;
			e = null;
			dom = null;
		}
	}
	
	public void initMethods() {
		if (methodsInitialized) {
			return;
		}
		
		List<MethodInstance> methods = dom.get(dom.new ListLens<MethodInstance>(dom.new MethodInstanceRefLens()), e, "methods", v);
		ct.setMethods(methods);
		
		methodsInitialized = true;
		
		if (initialized()) {
			v = null;
			e = null;
			dom = null;
		}
	}
	
	public void initConstructors() {
		if (constructorsInitialized) {
			return;
		}
		
		List<ConstructorInstance> constructors = dom.get(dom.new ListLens<ConstructorInstance>(dom.new ConstructorInstanceRefLens()), e, "constructors", v);
		ct.setConstructors(constructors);
		
		constructorsInitialized = true;
		
		if (initialized()) {
			v = null;
			e = null;
			dom = null;
		}
	}
	
	protected boolean initialized() {
		return superclassInitialized && interfacesInitialized
		&& memberClassesInitialized && methodsInitialized
		&& fieldsInitialized && constructorsInitialized;
	}
	
}
