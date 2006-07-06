/*
 * Created by vj on May 18, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 * @author vj May 18, 2005
 * 
 */
public class PlaceCast_c extends Expr_c implements PlaceCast {

	Expr place;
	Expr expr;
	/**
	 * @param pos
	 */
	public PlaceCast_c(Position pos, Expr place, Expr expr) {
		super(pos);
		this.expr = expr;
		this.place= place;
	}

	/** Reconstruct the expression. */
	protected PlaceCast_c reconstruct(Expr place, Expr expr) {
		if (place != this.place || expr != this.expr) {
			PlaceCast_c n = (PlaceCast_c) copy();
			n.place = place;
			n.expr = expr;
			return n;
		}
		
		return this;
	}
	
	/** Visit the children of the expression. */
	public Node visitChildren(NodeVisitor v) {
		Expr place = (Expr) visitChild(this.place, v);
		Expr expr = (Expr) visitChild(this.expr, v);
		return reconstruct(place, expr);
	}
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	
	 public Term entry() {
        return expr.entry();
    }
	

	 /** Get the precedence of the expression. */
	 public Precedence precedence() {
	 	return Precedence.CAST;
	 }
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	 public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(place, expr.entry());
        v.visitCFG(expr, this);
        return succs;
    }


	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.PlaceCast#placeCastType()
	 */
	public Expr placeCastType() {
		return place;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.PlaceCast#castType(polyglot.ast.Expr)
	 */
	public PlaceCast placeCastType(Expr place) {
		PlaceCast_c n = (PlaceCast_c) copy();
		n.place = place;
		return n;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.PlaceCast#expr()
	 */
	public Expr expr() {
		return expr;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.PlaceCast#expr(polyglot.ast.Expr)
	 */
	public PlaceCast expr(Expr expr) {
		PlaceCast_c n = (PlaceCast_c) copy();
		n.expr = expr;
		return n;
	}
	 public Node typeCheck(TypeChecker tc) throws SemanticException
	    {
	        TypeSystem ts = tc.typeSystem();

	        
		return type(expr.type());
	    }

}
