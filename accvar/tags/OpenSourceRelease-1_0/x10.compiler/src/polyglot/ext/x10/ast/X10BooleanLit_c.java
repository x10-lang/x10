/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ast.BooleanLit_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * @author vj
 *
 */
public class X10BooleanLit_c extends BooleanLit_c {

	/**
	 * @param pos
	 * @param value
	 */
	public X10BooleanLit_c(Position pos, boolean value) {
		super(pos, value);
		
	}
	 /** Type check the expression. */
	  public Node typeCheck(TypeChecker tc) throws SemanticException {
		  X10Type Boolean = (X10Type) tc.typeSystem().Boolean();
		 
			C_Lit_c literal = value ? C_Lit.TRUE : C_Lit.FALSE;
			Constraint c = Constraint_c.addSelfBinding(literal,null);
		  X10Type newType  = Boolean.makeVariant(c, null);
	    return type(newType);
	  }
}
