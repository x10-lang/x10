/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 20, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Stmt_c;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;

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
    }

    public Finish_c(Position pos, Stmt body) {
	super(pos);
	this.body = body;
    }


    /* (non-Javadoc)
     * @see polyglot.ext.x10.ast.Finish#body()
     */
    public Stmt body() {
	return this.body;
    }
	
    /** Set the body of the statement. */
    public Finish body(Stmt body) {
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
    public Term firstChild() {
	return (body != null ? body : null);
    }

    /**
     * Visit this term in evaluation order.
     */
    public List acceptCFG(CFGBuilder v, List succs) {
	v.visitCFG(body, this, EXIT);
	return succs;
    }
    /** Visit the children of the statement. */
    public Node visitChildren( NodeVisitor v ) {
	Stmt body = (Stmt) visitChild(this.body, v);
	return body(body);
    }

}
