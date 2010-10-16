/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.visit;

import polyglot.frontend.Job;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Call;
import x10.ast.ClassDecl;
import x10.ast.ConstructorCall;
import x10.ast.ConstructorDecl;
import x10.ast.Expr;
import x10.ast.Field;
import x10.ast.FieldDecl;
import x10.ast.MethodDecl;
import x10.ast.New;
import x10.ast.Node;
import x10.ast.NodeFactory;
import x10.ast.SourceFile;
import x10.ast.TypeNode;
import x10.ast.X10MLSourceFile;
import x10.types.SemanticException;
import x10.types.TypeSystem;
import x10.types.UnknownType;

/**
 * @author nystrom
 *
 */
public class X10MLVerifier extends ContextVisitor {

	/**
	 * @param job
	 * @param ts
	 * @param nf
	 */
	public X10MLVerifier(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}
	
	public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
		// We should redo type-checking, but it's currently not idempotent,
		// so will fail.
		
		// For now, we just check a few easy things.
		
		if (n.position() == null) {
			n = n.position(Position.COMPILER_GENERATED);
		}
		
		// Just check that we're dealing with an XML file.
		if (n instanceof SourceFile) {
			if (! (n instanceof X10MLSourceFile)) {
				throw new InternalCompilerError("Non-XML file loaded via XML parser.", n.position());
			}
			return n;
		}
		
		if (n instanceof TypeNode) {
			TypeNode tn = (TypeNode) n;
			if (tn.type() == null || tn.type() instanceof UnknownType) {
				throw new SemanticException("Missing or invalid type " + tn, n.position());
			}
		}
		
		if (n instanceof Expr) {
			Expr e = (Expr) n;
			if (e.type() == null || e.type() instanceof UnknownType) {
				throw new SemanticException("Missing or invalid type for expression " + e, n.position());
			}
		}
		
		if (n instanceof ClassDecl) {
			ClassDecl fd = (ClassDecl) n;
			if (fd.classDef() == null) {
				throw new SemanticException("Missing type for class declaration " + fd, n.position());
			}
		}
		
		if (n instanceof Call) {
			Call f = (Call) n;
			if (f.methodInstance() == null) {
				throw new SemanticException("Missing or invalid MethodInstance for call " + f, n.position());
			}
		}
		
		if (n instanceof FieldDecl) {
			FieldDecl fd = (FieldDecl) n;
			if (fd.fieldDef() == null) {
				throw new SemanticException("Missing or invalid FieldInstance for declaration " + fd, n.position());
			}
		}
		
		if (n instanceof Field) {
			Field f = (Field) n;
			if (f.fieldInstance() == null) {
				throw new SemanticException("Missing or invalid FieldInstance for field access " + f, n.position());
			}
		}
		
		if (n instanceof MethodDecl) {
			MethodDecl fd = (MethodDecl) n;
			if (fd.methodDef() == null) {
				throw new SemanticException("Missing or invalid MethodInstance for declaration " + fd, n.position());
			}
		}
		
		if (n instanceof Call) {
			Call f = (Call) n;
			if (f.methodInstance() == null) {
				throw new SemanticException("Missing or invalid MethodInstance for call " + f, n.position());
			}
		}
		
		if (n instanceof ConstructorDecl) {
			ConstructorDecl fd = (ConstructorDecl) n;
			if (fd.constructorDef() == null) {
				throw new SemanticException("Missing or invalid ConstructorInstance for declaration " + fd, n.position());
			}
		}
		
		if (n instanceof ConstructorCall) {
			ConstructorCall f = (ConstructorCall) n;
			if (f.constructorInstance() == null) {
				throw new SemanticException("Missing or invalid ConstructorInstance for constructor call " + f, n.position());
			}
		}
		
		if (n instanceof New) {
			New f = (New) n;
			if (f.constructorInstance() == null) {
				throw new SemanticException("Missing or invalid ConstructorInstance for new expression " + f, n.position());
			}
			if (f.body() != null && f.anonType() == null) {
			    throw new SemanticException("Missing or invalid anonymous type ClassDef for new expression " + f, n.position());
			}
		}
		
		return n;
	}

}
