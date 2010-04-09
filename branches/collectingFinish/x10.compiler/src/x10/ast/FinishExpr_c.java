package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

public class FinishExpr_c extends Expr_c  implements FinishExpr {

	Expr reducer;
	Stmt body;
	/**
	 * @param pos
	 */
	public FinishExpr_c(Position pos, Expr r, Stmt body) {
		super(pos);
		this.reducer=r;
		this.body = body;
		
	}
	public Expr reducer() {
		return reducer;
	}

	public Stmt body() {
		return body;
	}
	/* (non-Javadoc)
	 * @see polyglot.ast.Term_c#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	@Override
	public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#firstChild()
	 */
	public Term firstChild() {
		// TODO Auto-generated method stub
		return null;
	}

}
