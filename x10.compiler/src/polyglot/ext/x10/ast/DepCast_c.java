/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * @author vj
 *
 */
public class DepCast_c extends X10Cast_c implements DepCast {
	protected DepParameterExpr dep;
	 public DepCast_c(Position pos, TypeNode castType, DepParameterExpr d, Expr expr) {
         super(pos, castType, expr);
         this.dep = d;
 }
	 
	 public DepParameterExpr dep() {
		 return dep;
	 }
	 
	 public Node typeCheck(TypeChecker tc) throws SemanticException {
		 Report.report(1, "DepCast_c: typeCheck called on  " + this + " with dep=" + dep());
		 return super.typeCheck(tc);
	 }

}
