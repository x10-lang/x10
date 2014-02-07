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

package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Call_c;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Expr_c;
import polyglot.ast.FieldDecl;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Import_c;
import polyglot.ast.Initializer_c;
import polyglot.ast.LocalDecl;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Local_c;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode_c;
import polyglot.ast.SourceFile;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import x10.ExtensionInfo;
import x10.ast.AnnotationNode;
import x10.ast.X10ConstructorDecl;
import x10.extension.X10Ext;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalInstance;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10Type;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PruningVisitor;

public class PropagateAnnotationsVisitor extends ContextVisitor {
	public PropagateAnnotationsVisitor(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}
	
//	public NodeVisitor begin() {
//		// Disable the pass if there are no plugins.
//		ExtensionInfo extInfo = (ExtensionInfo) ts.extensionInfo();
//		if (extInfo.plugins().isEmpty()) {
//			return new PruningVisitor();
//		}
//		return super.begin();
//	}

	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
		n = super.leaveCall(old, n, v);
		
		new PropagateDependentAnnotationsVisitor(job, ts, nf).leave(old, n, v);
		
		List<AnnotationNode> as = null;
		
		if (n.ext() != null) {
			as = ((X10Ext) n.ext()).annotations();
		}
		
		if (as == null)
			as = Collections.<AnnotationNode>emptyList();
		
		List<X10ClassType> newAs = new ArrayList<X10ClassType>(as.size());
		for (Iterator<AnnotationNode> i = as.iterator(); i.hasNext(); ) {
			AnnotationNode a = i.next();
			newAs.add(a.annotationInterface());
		}

		if (n instanceof CanonicalTypeNode) {
			CanonicalTypeNode tn = (CanonicalTypeNode) n;
			if (tn.type() instanceof X10Type) {
				X10Type t = (X10Type) tn.type();
				t = (X10Type) t.annotations(newAs);
				return tn.type(t);
			}
		}
		if (n instanceof FieldDecl) {
			FieldDecl fd = (FieldDecl) n;
			X10FieldInstance fi = (X10FieldInstance) fd.fieldInstance();
			fi.setDefAnnotations(newAs);
			fi.setType(fd.type().type());
		}
		if (n instanceof Formal) {
			Formal f = (Formal) n;
			X10LocalInstance li = (X10LocalInstance) f.localDef();
			li.setDefAnnotations(newAs);
			li.setType(f.type().type());
		}
		if (n instanceof LocalDecl) {
			LocalDecl ld = (LocalDecl) n;
			X10LocalInstance li = (X10LocalInstance) ld.localDef();
			li.setDefAnnotations(newAs);
			li.setType(ld.type().type());
		}
		if (n instanceof MethodDecl) {
			MethodDecl md = (MethodDecl) n;
			X10MethodInstance mi = (X10MethodInstance) md.methodInstance();
			mi.setDefAnnotations(newAs);
			mi.setReturnType(md.returnType().type());
			List<Type> newFormals = new ArrayList<Type>(md.formals().size());
			for (Iterator<Formal> i = md.formals().iterator(); i.hasNext(); ) {
				Formal f = i.next();
				newFormals.add(f.type().type());
			}
			mi.setFormalTypes(newFormals);
			List<Type> newThrows = new ArrayList<Type>(md.formals().size());
			for (Iterator<TypeNode> i = md.throwTypes().iterator(); i.hasNext(); ) {
				TypeNode tn = i.next();
				newThrows.add(tn.type());
			}
			mi.setThrowTypes(newThrows);
		}
		if (n instanceof X10ConstructorDecl) {
			X10ConstructorDecl cd = (X10ConstructorDecl) n;
			X10ConstructorInstance ci = (X10ConstructorInstance) cd.constructorInstance();
			ci.setDefAnnotations(newAs);
			ci.setReturnType((X10Type) cd.returnType().type());
			List<Type> newFormals = new ArrayList<Type>(cd.formals().size());
			for (Iterator<Formal> i = cd.formals().iterator(); i.hasNext(); ) {
				Formal f = i.next();
				newFormals.add(f.type().type());
			}
			ci.setFormalTypes(newFormals);
			List<Type> newThrows = new ArrayList<Type>(cd.formals().size());
			for (Iterator<TypeNode> i = cd.throwTypes().iterator(); i.hasNext(); ) {
				TypeNode tn = i.next();
				newThrows.add(tn.type());
			}
			ci.setThrowTypes(newThrows);
		}
		if (n instanceof ClassDecl) {
			ClassDecl cd = (ClassDecl) n;
			X10ParsedClassType ct = (X10ParsedClassType) cd.type();
			ct.setDefAnnotations(Collections.EMPTY_LIST);
			ct.setClassAnnotations(newAs);
			if (cd.superClass() != null)
				ct.superType(cd.superClass().type());
			List<Type> newInterfaces = new ArrayList<Type>(cd.interfaces().size());
			for (Iterator<TypeNode> i = cd.interfaces().iterator(); i.hasNext(); ) {
				TypeNode tn = i.next();
				newInterfaces.add(tn.type());
			}
			ct.setInterfaces(newInterfaces);
			
			// Force annotations to be set for the default constructor
			for (Iterator i = ct.constructors().iterator(); i.hasNext(); ) {
				X10ConstructorInstance ci = (X10ConstructorInstance) i.next();
				if (! ci.annotationsSet())
					ci.setDefAnnotations(Collections.EMPTY_LIST);
			}
		}
		if (n instanceof New) {
			New nw = (New) n;
			X10ParsedClassType ct = (X10ParsedClassType) nw.anonType();
			if (ct != null) {
				ct.setDefAnnotations(Collections.EMPTY_LIST);
				ct.setClassAnnotations(Collections.EMPTY_LIST);
				ClassType superType = (ClassType) nw.objectType().type();
				if (! superType.flags().isInterface()) {
                    ct.superType(superType);
                }
                else {
                    ct.setInterfaces(Collections.singletonList(superType));
                }
			}
		}
			
		return n;
	}
}
