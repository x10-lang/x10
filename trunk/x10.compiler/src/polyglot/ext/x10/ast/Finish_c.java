/*
 * Created by vj on Dec 20, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Stmt_c;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.PrettyPrinter;
import polyglot.ast.Stmt;

/** An immutable class representing the X10 finish S construct.
 *  No special type-checking rules. 
 * @author vj Dec 20, 2004
 * 
 */
public class Finish_c extends Stmt_c implements Finish {

	 protected Stmt body;
	 
	/**
	 * @param pos
	 */
	public Finish_c(Position pos) {
		super(pos);
		// TODO Auto-generated constructor stub
	}

	public Finish_c(Position pos, Stmt body) {
		super(pos);
		this.body = body;
		// TODO Auto-generated constructor stub
	}


	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.Finish#body()
	 */
	public Stmt body() {
		// TODO Auto-generated method stub
		return this.body;
	}
	
	
	    /** Set the body of the statement. */
	    public Finish body(Block body) {
		Finish_c n = (Finish_c) copy();
		n.body = body;
		return n;
	    }

	

	    public String toString() {
		return "finish  { ... }";
	    }

	    /** Write the statement to an output file. */
	    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("finish ");
		printSubStmt(body, w, tr);
	    }

	    /**
	     * Return the first (sub)term performed when evaluating this
	     * term.
	     */
	    public Term entry() {
	         return (body != null ? body.entry() : this);
	    }

	    /**
	     * Visit this term in evaluation order.
	     */
	    public List acceptCFG(CFGBuilder v, List succs) {
	        v.visitCFG(body, this);
	        return succs;
	    }

}
