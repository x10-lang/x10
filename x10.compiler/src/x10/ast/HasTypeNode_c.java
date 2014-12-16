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

package x10.ast;

import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Ext;
import polyglot.ast.JL;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.Context;
import polyglot.types.Qualifier;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SubtypeSet;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;

public class HasTypeNode_c extends TypeNode_c implements TypeNode {

	TypeNode tn;
	public HasTypeNode_c(TypeNode tn) {
		super(tn.position());
		this.tn = tn;
		// TODO Auto-generated constructor stub
	}
	public TypeNode typeNode() { return tn;}
	public Type type() { return tn.type();}
	@Override
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
         tn.prettyPrint(w, tr);
	}
	
}
