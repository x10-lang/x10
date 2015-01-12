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

package x10.ast;

import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.TypeNode;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import x10.errors.Errors;
import x10.types.X10ClassType;
import polyglot.types.Context;
import polyglot.types.TypeSystem;

/**
 * A node representing an annotation.  Every X10 Node has an associated list of AnnotationNodes.
 * An annotation is simply an interface type.
 * @author nystrom
 */
public class AnnotationNode_c extends Node_c implements AnnotationNode {
	TypeNode tn;
	
    /**
	 * 
	 */
	public AnnotationNode_c(Position pos, TypeNode tn) {
		super(pos);
		this.tn = tn;
	}

	public TypeNode annotationType() {
		return tn;
	}
	
	public AnnotationNode annotationType(TypeNode tn) {
		AnnotationNode_c n = (AnnotationNode_c) copy();
		n.tn = tn;
		return n;
	}
	
	public X10ClassType annotationInterface() {
		return (X10ClassType) annotationType().type().toClass();
	}
	
	@Override
	public Node visitChildren(NodeVisitor v) {
		TypeNode tn = (TypeNode) this.visitChild(this.tn, v);
		if (tn != this.tn) {
			return annotationType(tn);
		}
		return this;
	}
	
	@Override
	public Context enterChildScope(Node child, Context c) {
		c = c.pushBlock();
		((Context) c).setAnnotation();
		return super.enterChildScope(child, c);
	}
	
//	public Node disambiguateOverride(AmbiguityRemover ar) throws SemanticException {
//		if (ar.job().extensionInfo().scheduler().currentGoal() instanceof SignaturesDisambiguated) {
//			return this;
//		}
//		if (ar.job().extensionInfo().scheduler().currentGoal() instanceof SupertypesDisambiguated) {
//			return this;
//		}
//		return super.disambiguate(ar);
//	}
	
	@Override
	public Node typeCheck(ContextVisitor tc) {
		//System.out.println("Type checking " + this);
		TypeSystem xts = (TypeSystem) tc.typeSystem();
		if (!xts.hasUnknown(tn.type()) && Types.error(tn.type())==null && !(tn.type().isClass() && tn.type().toClass().flags().isInterface())) {
			Errors.issue(tc.job(), new Errors.AnnotationMustBeInterfacetype(position()));
		}
		
		return this;
	}
	
	@Override
	public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
		w.write("@");
		print(tn, w, pp);
	}

	@Override
	public void translate(CodeWriter w, Translator tr) {
		/** Do nothing! */
	}
	
	@Override
	public String toString() {
		return "@" + tn.toString();
	}
}
