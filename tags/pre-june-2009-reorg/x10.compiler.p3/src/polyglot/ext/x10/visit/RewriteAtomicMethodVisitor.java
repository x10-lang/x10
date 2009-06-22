/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.extension.X10ClassBodyExt_c;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

public class RewriteAtomicMethodVisitor extends ContextVisitor {

	public RewriteAtomicMethodVisitor(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

	<T extends Node> T check(T n) throws SemanticException {
		return (T) n.del().disambiguate(this).del().typeCheck(this).del().checkConstants(this);
	}

	@Override
	public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
		n = super.leaveCall(old, n, v);

		if (n instanceof X10MethodDecl) {
			X10MethodDecl md = (X10MethodDecl) n;
			X10Flags flags = X10Flags.toX10Flags(md.flags().flags());
			if (flags.isAtomic()) {
				Block b = md.body();
				X10NodeFactory nf = (X10NodeFactory) this.nf;
				Position pos = b.position();

				Expr here = nf.Here(pos);
				here = check(here);

				Stmt atomic = nf.Atomic(pos, here, b);
				atomic = check(atomic);

				b = nf.Block(pos, Collections.singletonList(atomic));
				b = check(b);
				
				return md.body(b);
			}
		}

		return n;
	}
}
