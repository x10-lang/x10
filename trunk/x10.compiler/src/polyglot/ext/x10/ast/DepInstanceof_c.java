/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Instanceof_c;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;
import x10.runtime.Report;

/**
 * @author VijaySaraswat
 *
 */
public class DepInstanceof_c extends Instanceof_c implements DepInstanceof {

	protected DepParameterExpr dep;
	  public DepInstanceof_c(Position pos, TypeNode compareType, DepParameterExpr d, Expr expr) {
			super(pos, expr,compareType);
			this.dep = d;
			
		    }
	  
	  public Node typeCheck(TypeChecker tc) throws SemanticException {
		  Report.report(1, "DepInstance_of: " + this + " dep=" + dep);
		  return super.typeCheck(tc);
	  }
}
