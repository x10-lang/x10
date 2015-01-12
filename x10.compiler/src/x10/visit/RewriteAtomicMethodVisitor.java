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

package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Atomic;
import x10.ast.X10MethodDecl;

import polyglot.types.TypeSystem;

public class RewriteAtomicMethodVisitor extends ContextVisitor {

	public RewriteAtomicMethodVisitor(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

	<T extends Node> T check(T n) throws SemanticException {
		return (T) n.del().disambiguate(this).del().typeCheck(this).del().checkConstants(this);
	}

	@Override
	public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
		n = super.leaveCall(old, n, v);

		if (parent instanceof X10MethodDecl && n == ((X10MethodDecl) parent).body()) {
			X10MethodDecl md = (X10MethodDecl) parent;
			Flags flags = md.flags().flags();
			if (flags.isAtomic()) {
				Block b = (Block) n;
				Position pos = b.position();

				Expr here = nf.Here(pos);
				RewriteAtomicMethodVisitor ramw = (RewriteAtomicMethodVisitor) v;
				here = ramw.check(here);

				Stmt atomic = nf.Atomic(pos, here, b);
				atomic = ramw.check(atomic);

				b = nf.Block(pos, Collections.singletonList(atomic));
				b = ramw.check(b);

				return b;
			}
		}

		return n;
	}
}
