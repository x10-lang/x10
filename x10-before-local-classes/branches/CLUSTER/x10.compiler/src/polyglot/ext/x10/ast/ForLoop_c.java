/*
 * Created by vj on Dec 9, 2004
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Loop;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;

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

	public Term continueTarget() {
		return formal.entry();
	}

	public Expr cond() { return null; }
	public boolean condIsConstant() { return false; }
	public boolean condIsConstantTrue() { return false; }
}

