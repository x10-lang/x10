/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ext.jl.ast.StringLit_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * @author vj
 *
 */
public class X10StringLit_c extends StringLit_c {

	/**
	 * @param pos
	 * @param value
	 */
	public X10StringLit_c(Position pos, String value) {
		super(pos, value);
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		  X10Type Type = (X10Type) tc.typeSystem().String();
		 
			C_Lit_c literal = new C_Lit_c(value, Type);
			Constraint c = Constraint_c.addSelfBinding(literal,null);
		  X10Type newType  = Type.makeVariant(c, null);
	    return type(newType);
	  }

}
