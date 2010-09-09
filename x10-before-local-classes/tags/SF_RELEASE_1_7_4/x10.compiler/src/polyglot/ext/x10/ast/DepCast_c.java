/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 * @author vj
 * 
 */
public class DepCast_c extends X10Cast_c implements DepCast, X10DepCastInfo {

	protected boolean depTypeCheckingNeeded = false;
	
	// This dep must be typechecked in a context in which castType.type() has been
	// pushed, as would be done by e.g. X10CanonicalTypeNode_c.
	protected boolean notVisited = true;
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
	
	Type lookaheadType = null;
	
	@Override
	public NodeVisitor disambiguateEnter(AmbiguityRemover sc) throws SemanticException {
		X10TypeNode tn = (X10TypeNode)((X10TypeNode) castType).disambiguateBase(sc);
		lookaheadType = (tn instanceof NullableNode) ? ((NullableNode) tn).base().type() : tn.type();
		return sc;
	}
	@Override
	public NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException {
		X10TypeNode tn = (X10TypeNode)((X10TypeNode) castType).typeCheckBase(tc);
		lookaheadType = (tn instanceof NullableNode) ? ((NullableNode) tn).base().type() : tn.type();
		return tc;
	}
	@Override
	public Context enterChildScope(Node child, Context c) {
		if (child == this.dep) {
			TypeSystem ts = c.typeSystem();
			//Report.report(1, "X10CanonicalType: enterChildScope " + this + " pushing " + lookaheadType + "| child==" + child);
			if (lookaheadType instanceof X10NamedType) {
				c = ((X10Context) c).pushDepType((X10NamedType) lookaheadType);
			}
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}
	@Override
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		// Report.report(1, "DepCast_c: typeCheck called on  " + this + " with dep=" + dep());

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
					if (notVisited) {
						Report.report(1, "Warning! Primitive Cast from " + fromType
								+ " to " + toType + " is unsafe at line "
								+ this.position + ".");
						notVisited = false;
					}
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
					// cast is valid if toType or fromType have constraints,
					// checks them at runtime
					if (this.notVisited) {
						Report.report(1, "Warning! Cast from " + fromType + " to "
								+ toType + " is unsafe at line " + this.position
								+ ".");
						this.notVisited = false;
					}
					this.depTypeCheckingNeeded = true;

				if ((fromType.isClass()) && (toType.isPrimitive())) {
					// Primitive <-- Class (UnBoxing Operation)
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
    	//Report.report(1, "DepCast_c: Visiting this.expr=" + this.dep + " with visitor " + v);
    	DepParameterExpr depExpr = (DepParameterExpr) visitChild(this.dep, v);
    	//Report.report(1, "DepCast_c: yields " + depExpr);
    	Expr expr = (Expr) visitChild(this.expr, v);
    	
    	return reconstruct(castType, expr, depExpr);
    }
    public String toString() {
    	return "/*depCast*/" + super.toString();
        }
}
