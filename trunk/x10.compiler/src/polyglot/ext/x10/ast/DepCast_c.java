/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Cast_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 * @author vj
 * 
 */
public class DepCast_c extends X10Cast_c implements DepCast, X10DepCastInfo {

	protected boolean depTypeCheckingNeeded = false;

	protected DepParameterExpr dep;

	public DepCast_c(Position pos, TypeNode castType, DepParameterExpr d,
			Expr expr) {
		super(pos, castType, expr);
		this.dep = d;
	}

	public DepParameterExpr dep() {
		return dep;
	}

	public boolean isDepTypeCheckingNeeded() {
		return depTypeCheckingNeeded;
	}

	public Node typeCheck(TypeChecker tc) throws SemanticException {
		Report.report(1, "DepCast_c: typeCheck called on  " + this
				+ " with dep=" + dep());

		this.depTypeCheckingNeeded = false;

		// check if the cast is statically valid
		super.typeCheck(tc);

		// cast is either statically valid or requires a runtime check
		Type toType = castType.type();
		Type fromType = expr.type();
		X10Type x10ToType = (X10Type) toType;
		X10Type x10FromType = (X10Type) fromType;
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		
		// we generate deptype constraint code checking only if targetType is
		// constrained and fromType not.
		// Otherwise the previous isCastValid has already check type
		// compatibility
		if (xts.isTypeConstrained(x10ToType)
				&& !xts.isTypeConstrained(x10FromType)) {		
			if (fromType.isPrimitive()) {
				if (!expr.isConstant()) {
					Report.report(1, "Warning! Primitive Cast from " + fromType
							+ " to " + toType + " is unsafe at line "
							+ this.position + ".");
					this.primitiveType = true;
					this.depTypeCheckingNeeded = true;
				}
				if (toType.isClass()) {
					// NOTE: Current release only allow casting to
					// x10.lang.Object, which is unlikely to be constrained
					// NOTE: If casting to some constrainted wrapper type are
					// allowed in the future a dynamic check would be needed.
					// Class <-- Primitive (Boxing Operation)
					// this.dynamicCheckNeeded = true;
				}
				// else constant had been promoted to deptype and checking
				// occured previously in isCastValid
			} else {
				if (!x10ToType.equalsImpl(x10FromType)) {
					// cast is valid if toType or fromType have constraints,
					// checks them at runtime
					Report.report(1, "Warning! Cast from " + fromType + " to "
							+ toType + " is unsafe at line " + this.position
							+ ".");
					this.depTypeCheckingNeeded = true;
				}

				if ((fromType.isClass()) && (toType.isPrimitive())) {
					// Primitive <-- Class (UnBoxing Operation)
					this.depTypeCheckingNeeded = true;
					this.primitiveType = true;
				}
				// else type are equals, we do not perform the cast
			}
		}
		return type(toType);
	}
    
    public DepParameterExpr depParameterExpr() {
		return (DepParameterExpr) this.dep.copy();
	}
    
	/** Reconstruct the expression. */
    protected DepCast_c reconstruct(TypeNode castType, Expr expr, DepParameterExpr dep) {
    	DepCast_c superCopy = (DepCast_c) super.reconstruct(castType, expr);
    	if (dep != superCopy.dep) {
    		DepCast_c copy = (DepCast_c) superCopy.copy();
    		copy.dep = dep;
    		return copy;
    	}
    	
    	return (superCopy == this) ? this : superCopy;
    }
    
    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
    	TypeNode castType = (TypeNode) visitChild(this.castType, v);
    	Expr expr = (Expr) visitChild(this.expr, v);
//    	DepParameterExpr depExpr = (DepParameterExpr) visitChild(this.dep, v);
    	DepParameterExpr depExpr = (DepParameterExpr) dep.copy();
    	return reconstruct(castType, expr, depExpr);
    }
}
