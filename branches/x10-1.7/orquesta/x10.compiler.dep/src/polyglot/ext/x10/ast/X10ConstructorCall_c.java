/**
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.ConstructorCall.Kind;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * A call to this(...) or super(...) in the body of a constructor.
 * (The call new C(...) is represented by an X10New_c.)
 * @author vj
 *
 */
public class X10ConstructorCall_c extends ConstructorCall_c {

	/**
	 * @param pos
	 * @param kind
	 * @param qualifier
	 * @param arguments
	 */
	public X10ConstructorCall_c(Position pos, Kind kind, Expr qualifier,
			List arguments) {
		super(pos, kind, qualifier, arguments);
		
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10ConstructorCall_c n = (X10ConstructorCall_c) super.typeCheck(tc);
		if (kind().equals(ConstructorCall.SUPER)) {
			Context ctx = tc.context();
			if (! (ctx.inCode()) || ! (ctx.currentCode() instanceof X10ConstructorDef))
				throw new SemanticException("A call to super must occur only in the body of a constructor.",
						position());
			
			//	The constructorinstance for this super call.
			
			X10ConstructorInstance ci = (X10ConstructorInstance) n.constructorInstance();
			X10Type type = (X10Type) ci.returnType();
			
			
			// Now construct from this generic return type the instance obtained by substituting 
			// the actual parameters for the formals.
			X10Type retType = X10New_c.instantiateType(ci, type, arguments);
			Constraint c = retType.realClause();
			
			// The constructor *within which this super call happens*.
			X10ConstructorDef thisConstructor = (X10ConstructorDef) ctx.currentCode();
			thisConstructor.setSupClause(Types.ref(c));
		}
	
		return n;
	}
	 public String toString() {
			return (qualifier != null ? qualifier + "." : "") + kind + arguments;
		    }

}
