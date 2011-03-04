/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 */
package x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import x10.ast.X10Loop.LoopKind;
import x10.types.X10MethodInstance;
import x10.types.X10Type;
import x10.types.X10TypeSystem;

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
	public List acceptCFG(CFGBuilder v, List succs) {
		v.visitCFG(formal, domain, ENTRY);
		v.visitCFG(domain, FlowGraph.EDGE_KEY_TRUE, body,
						   ENTRY, FlowGraph.EDGE_KEY_FALSE, this, EXIT);
		v.push(this).visitCFG(body, continueTarget(), ENTRY);
		return succs;
	}

	public Node typeCheck(TypeChecker tc) throws SemanticException {
		return super.typeCheck(tc);
	}
	/** Type check the statement. */
//	public Node typeCheck(TypeChecker tc) throws SemanticException {
//		ForLoop_c n = (ForLoop_c) super.typeCheck(tc);
//		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
//		Expr newDomain = n.domain;
//		X10Type type = (X10Type) newDomain.type();
//		// FIXME: [IP] remove desugaring
//		if (ts.isDistribution(type))
//			newDomain = (Expr) tc.nodeFactory().Field(n.position(), newDomain, tc.nodeFactory().Id(n.position(), "region")).del().typeCheck(tc);
//		return n.domain(newDomain);
//	}

	public boolean condIsConstant() { return false; }
	public boolean condIsConstantTrue() { return false; }

	public String toString() {
		return "for (" + formal + " in " + domain + ")" + body;
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("for(");
		printBlock(formal, w, tr);
		w.write(" : ");
		printBlock(domain, w, tr);
		w.write(") ");
		printSubStmt(body, w, tr);
	}
}

