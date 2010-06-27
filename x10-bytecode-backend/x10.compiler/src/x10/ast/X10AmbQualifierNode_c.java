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

package x10.ast;

import polyglot.ast.AmbQualifierNode_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.Prefix;
import polyglot.ast.QualifierNode;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ast.TypeCheckTypeGoal;
import polyglot.frontend.SetResolverGoal;
import polyglot.types.LazyRef;
import polyglot.types.Qualifier;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.visit.X10TypeChecker;

/**
 * An <code>X10AmbQualifierNode</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a type qualifier.
 */
public class X10AmbQualifierNode_c extends AmbQualifierNode_c implements X10AmbQualifierNode {
	protected LazyRef<Qualifier> qualifier;

	public X10AmbQualifierNode_c(Position pos, Prefix qual, Id name) {
		super(pos, qual, name);
		assert (name != null); // qual may be null
	}

	public LazyRef<? extends Qualifier> qualifierRef() {
		return this.qualifier;
	}

	public Prefix prefix() {
		return this.qual;
	}

	public X10AmbQualifierNode prefix(Prefix qual) {
		X10AmbQualifierNode_c n = (X10AmbQualifierNode_c) copy();
		n.qual = qual;
		return n;
	}

	public X10AmbQualifierNode qualifier(LazyRef<Qualifier> qualifier) {
		X10AmbQualifierNode_c n = (X10AmbQualifierNode_c) copy();
		n.qualifier = qualifier;
		return n;
	}

	protected X10AmbQualifierNode_c reconstruct(Prefix qual, Id name) {
		if (qual != this.qual || name != this.name) {
			X10AmbQualifierNode_c n = (X10AmbQualifierNode_c) copy();
			n.qual = qual;
			return n;
		}

		return this;
	}

	public Node visitChildren(NodeVisitor v) {
		Id name = (Id) visitChild(this.name, v);
		Prefix qual = (Prefix) visitChild(this.qual, v);
		return reconstruct(qual, name);
	}

	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		TypeSystem ts = tb.typeSystem();
		LazyRef<Qualifier> sym = Types.<Qualifier> lazyRef(ts.unknownQualifier(position()), new SetResolverGoal(tb.job()));
		return qualifier(sym);
	}

	public Qualifier qualifier() {
		return qualifierRef().get();
	}

	public void setResolver(Node parent, final TypeCheckPreparer v) {
		final LazyRef<Qualifier> r = (LazyRef<Qualifier>) qualifierRef();
		TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
		tc = (TypeChecker) tc.context(v.context().freeze());
		r.setResolver(new TypeCheckTypeGoal(parent, this, tc, r, false));
	}

	public Node disambiguate(ContextVisitor ar) throws SemanticException {
		SemanticException ex;

		try {
			Node n = ar.nodeFactory().disamb().disambiguate(this, ar, position(), qual, name);

			if (n instanceof QualifierNode) {
				QualifierNode qn = (QualifierNode) n;
				Qualifier q = qn.qualifierRef().get();
				qualifier.update(q);
				return n;
			}

			ex = new SemanticException("Could not find type or package \""
					+ (qual == null ? name.toString() : qual.toString() + "." + name.toString()) + "\".", position());
		}
		catch (SemanticException e) {
			ex = e;
		}

		// Mark the type as an error, so we don't try looking it up again.
		LazyRef<Qualifier> sym = (LazyRef<Qualifier>) qualifier;
		sym.update(ar.typeSystem().unknownQualifier(position()));

		throw ex;
	}

	public Node exceptionCheck(ExceptionChecker ec) throws SemanticException {
		throw new InternalCompilerError(position(), "Cannot exception check ambiguous node " + this + ".");
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		if (qual != null) {
			print(qual, w, tr);
			w.write(".");
			w.allowBreak(2, 3, "", 0);
		}

		tr.print(this, name, w);
	}

	public String toString() {
		return (qual == null ? name.toString() : qual.toString() + "." + name.toString()) + "{amb}";
	}
}
