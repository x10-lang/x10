/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ast.IntLit.Kind;
import polyglot.ast.IntLit_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * An immutable representation of an int lit, modified from JL 
 * to support a self-clause in the dep type.
 * @author vj
 *
 */
public class X10IntLit_c extends IntLit_c {

	/**
	 * @param pos
	 * @param kind
	 * @param value
	 */
	public X10IntLit_c(Position pos, Kind kind, long value) {
		super(pos, kind, value);
	
	}
	 /** Type check the expression. */
	  public Node typeCheck(TypeChecker tc) throws SemanticException {
		  TypeSystem ts = tc.typeSystem();
		  X10Type Type = (X10Type) (kind==INT ? ts.Int() : ts.Long());
		 
			C_Lit_c literal = new C_Lit_c(constantValue(), Type);
			Constraint c = Constraint_c.addSelfBinding(literal,null);
		  X10Type newType  = Type.makeVariant(c, null);
	    return type(newType);
	  }
}
