/*
 * Created by vj on Feb 4, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.ForInit;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Eval_c;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import polyglot.ast.Expr;

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
    public Node typeCheck(TypeChecker tc) throws SemanticException {  	
    	return type(expr.type());
    }

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term entry() {
		return expr.entry();
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
	    v.visitCFG( expr, this);
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
