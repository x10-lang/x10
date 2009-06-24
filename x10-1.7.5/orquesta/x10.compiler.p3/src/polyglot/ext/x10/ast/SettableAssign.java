package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.types.Type;
import polyglot.util.TypedList;

public interface SettableAssign extends Assign {
    /** Get the array of the expression. */
	public Expr array();
	
	/** Set the array of the expression. */
	public SettableAssign array(Expr array);
	
	/** Get the index of the expression. */
	public List<Expr> index();
	
	/** Set the index of the expression. */
	public SettableAssign index(List<Expr> index) ;
}
