/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;
import polyglot.visit.TypeChecker;

/**
 * An immutable representation of an X10 for loop: for (i : D) S
 *
 * @author vj Dec 9, 2004
 */
public class ForLoop_c extends X10Loop_c implements Loop {

	/**
	 * @param pos
	 */
	public ForLoop_c(Position pos) {
		super(pos);
	}

	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param body
	 */
	public ForLoop_c(Position pos, Formal formal, Expr domain, Stmt body) {
		super(pos, formal, domain, body);
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
		v.visitCFG(formal, domain.entry());
		v.visitCFG(domain, FlowGraph.EDGE_KEY_TRUE, body.entry(), 
						   FlowGraph.EDGE_KEY_FALSE, this);
		v.push(this).visitCFG(body, continueTarget());
		return succs;
	}

	/** Type check the statement. */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		ForLoop_c n = (ForLoop_c) super.typeCheck(tc);
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		Expr newDomain = n.domain;
		X10Type type = (X10Type) newDomain.type();
		if (ts.isDistribution(type))
			newDomain = (Expr) tc.nodeFactory().Field(n.position(), newDomain, "region").typeCheck(tc);
		return n.domain(newDomain);
	}

	public Term continueTarget() {
		return formal.entry();
	}

	public Expr cond() { return null; }
	public boolean condIsConstant() { return false; }
	public boolean condIsConstantTrue() { return false; }
}

