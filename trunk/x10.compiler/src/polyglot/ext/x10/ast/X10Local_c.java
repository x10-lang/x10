package polyglot.ext.x10.ast;

/**
 * Immutable representation of a local variable access. 
 * Introduced to add X10 specific type checks. A local variable accessed
 * in a deptype must be final.
 * 
 * @author vj
 */
import polyglot.ast.Node;
import polyglot.ext.jl.ast.Local_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class X10Local_c extends Local_c {

	public X10Local_c(Position pos, String name) {
		super(pos, name);
		
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10Local_c result = (X10Local_c) super.typeCheck(tc);
		X10Context xtc = (X10Context) tc.context();
		if (xtc.inDepType()) {
			LocalInstance li = result.localInstance();
			if (! li.flags().isFinal()) {
				throw new SemanticException("Local variable " + li.name() 
						+ " must be final in a depclause.", 
						position());
			}
		}
		
	    return result;
	}
}
