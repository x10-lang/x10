/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Feb 4, 2005
 *
 * 
 */
package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.Expr_c;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;

/**
 * @author vj Feb 4, 2005
 * 
 */
public class ParExpr_c extends Expr_c implements ParExpr {

	Expr expr;
	/**
	 * @param pos
	 */
	public ParExpr_c(Position pos, Expr expr) {
		super(pos);
		this.expr = expr;
		
	}
	public Expr expr() {
		return expr;
	}
	public ParExpr expr( Expr expr) {
		ParExpr_c n = (ParExpr_c) copy();
		n.expr = expr;
		return n;
	}

	 /** Reconstruct the statement. */
    protected ParExpr_c reconstruct(Expr expr) {
	if (expr != this.expr) {
	    ParExpr_c n = (ParExpr_c) copy();
	    n.expr = expr;
	    return n;
	}

	return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
    	Expr expr = (Expr) visitChild(this.expr, v);
    	return reconstruct(expr);
    }
    
    /** Type check the statement. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {  	
    	return type(expr.type());
    }
    
    public boolean isConstant() {
    	return expr.isConstant();
    }

    public Object constantValue() {
        if (! isConstant()) {
        	return null;
        }
        
    	return expr.constantValue();
    }

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term firstChild() {
		return expr;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
	    v.visitCFG( expr, this, EXIT);
		return succs;
	}
	public String toString() {
		return "(" + expr.toString() + ")";
	}
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("(");
		printBlock(expr, w, tr);
		w.write(")");
	}

}
