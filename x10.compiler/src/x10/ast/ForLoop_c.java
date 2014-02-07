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

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.FlowGraph;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import x10.ast.X10Loop.LoopKind;
import x10.errors.Errors;
import polyglot.types.Context;

import x10.types.MethodInstance;
import polyglot.types.TypeSystem;
import x10.types.checker.Checker;
import x10.types.checker.PlaceChecker;

/**
 * An immutable representation of an X10 for loop: for (i : D) S
 *
 * @author vj Dec 9, 2004
 */
public class ForLoop_c extends X10Loop_c implements ForLoop {

	/**
	 * @param pos
	 */
	public ForLoop_c(Position pos) {
		super(pos);
		loopKind=LoopKind.FOR;
	}

	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param body
	 */
	public ForLoop_c(Position pos, Formal formal, Expr domain, Stmt body) {
		super(pos, formal, domain, body);
		loopKind=LoopKind.FOR;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		v.visitCFG(formal, domain, ENTRY);
		v.visitCFG(domain, FlowGraph.EDGE_KEY_TRUE, body,
						   ENTRY, FlowGraph.EDGE_KEY_FALSE, this, EXIT);
		v.push(this).visitCFG(body, continueTarget(), ENTRY);
		return succs;
	}

	private static final Name ITERATOR = Name.make("iterator");
	public Node typeCheck(ContextVisitor tc) {
	    X10Loop result = (X10Loop) super.typeCheck(tc);
	    TypeSystem xts = (TypeSystem) tc.typeSystem();
	    // TODO: generate a cast if STATIC_CHECKS is off
	    MethodInstance mi = null;
	    Expr domain = result.domain();
	    mi = Checker.findAppropriateMethod(tc, domain.type(), ITERATOR, Collections.<Type>emptyList(), Collections.<Type>emptyList());
	    assert (mi != null);
	    domain = (Expr) PlaceChecker.makeReceiverLocalIfNecessary(tc, domain, mi.flags());
	    if (domain != null) {
	        if (domain != result.domain()) result = result.domain(domain);
	    } else if (!xts.isUnknown(result.domain().type())) {
	        Errors.issue(tc.job(),
	                new Errors.DomainIteratedForLoopMustBeLocal(result.domain().position()));
	    }
	    return result;
	}

	public boolean condIsConstant() { return false; }
	public boolean condIsConstantTrue() { return false; }

	public String toString() {
		return "for (" + formal + " in " + domain + ")" + body;
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("for(");
		printBlock(formal, w, tr);
		if (formal instanceof X10Formal) {
			X10Formal f = (X10Formal) formal;
			w.write("[");
			for (int i=0 ; i<f.vars().size() ; ++i) {
				if (i>0) w.write(",");
				printBlock(f.vars().get(i), w, tr);
			}
			w.write("]");
		}
		w.write(" in ");
		printBlock(domain, w, tr);
		w.write(") ");
		printSubStmt(body, w, tr);
	}
}

