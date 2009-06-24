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

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Globals;
import polyglot.frontend.GoalSet;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
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

	public DepCast dep(DepParameterExpr dep) {
	    DepCast_c n = (DepCast_c) copy();
	    n.dep = dep;
	    return n;
	}
	
	public boolean isDepTypeCheckingNeeded() {
		return depTypeCheckingNeeded;
	}
	
	  public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
	        // Override to disambiguate the base type and rebuild the node before type checking the dep clause.

	        TypeSystem ts = tc.typeSystem();
	        NodeFactory nf = tc.nodeFactory();

	        DepCast_c n = this;

	        TypeChecker childtc = (TypeChecker) tc.enter(parent, n);
	        
	        TypeNode tn = (TypeNode) n.visitChild(n.castType, childtc);
	        n = (DepCast_c) n.castType(tn);
	        
	        if (tn.type() instanceof UnknownType) {
	            return n;
	        }
	        
	        DepParameterExpr dep = (DepParameterExpr) n.visitChild(n.dep, childtc);
	        n = (DepCast_c) n.dep(dep);
	        
	        X10Type t = (X10Type) tn.type();

	        if (dep != null) {
	            t = X10TypeMixin.depClause(t, dep.constraint());
	        }
	        
	        Expr e = (Expr) n.visitChild(n.expr, childtc);
	        n = (DepCast_c) n.expr(e);

	        n.depTypeCheckingNeeded = false;
	        n.notVisited = true;
	        
	        // check if the cast is statically valid
	        n = (DepCast_c) n.superTypeCheck(tc);

	        // cast is either statically valid or requires a runtime check
	        Type toType = n.castType.type();
	        Type fromType = n.expr.type();
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
	                if (!n.expr.isConstant()) {
	                    if (n.notVisited) {
	                        Globals.Compiler().errorQueue().enqueue(ErrorInfo.WARNING, "Primitive Cast from " + fromType
	                                      + " to " + toType + " is unsafe at line "
	                                      + n.position + ".");
	                        n.notVisited = false;
	                    }
	                    n.depTypeCheckingNeeded = true;
	                } 
	                if (toType.isClass()) {
	                    // NOTE: Current release only allow casting to
	                    // x10.lang.Object, which is unlikely to be constrained
	                    // NOTE: If casting to some constrained wrapper type are
            		    // allowed in the future a dynamic check would be needed.
            		    // Class <-- Primitive (Boxing Operation)
            		    // this.dynamicCheckNeeded = true;
            		}
            		// else constant had been promoted to deptype and checking
            		// occurred previously in isCastValid
	            }
	            else {
	                // cast is valid if toType or fromType have constraints,
	                // checks them at runtime
	                if (n.notVisited) {
	                    Globals.Compiler().errorQueue().enqueue(ErrorInfo.WARNING, "Cast from " + fromType + " to "
	                                                            + toType + " is unsafe at line " + n.position
	                                                            + ".");
	                    n.notVisited = false;
	                }
	                n.depTypeCheckingNeeded = true;

	                if ((fromType.isClass()) && (toType.isPrimitive())) {
	                    // Primitive <-- Class (UnBoxing Operation)
	                    n.primitiveType = true;
	                }
	                // else type are equal, we do not perform the cast
	            }
	        }
	        
	        return n;
	    }
	  
	  public Node superTypeCheck(TypeChecker tc) throws SemanticException {
	      return super.typeCheck(tc);
	  }
	  
	@Override
	public Context enterChildScope(Node child, Context c) {
		if (child == this.dep) {
			TypeSystem ts = c.typeSystem();
			c = ((X10Context) c).pushDepType(castType.typeRef());
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}
    
    public DepParameterExpr depParameterExpr() {
		return (DepParameterExpr) this.dep.copy();
	}
    
	/** Reconstruct the expression. */
    protected DepCast_c reconstruct(TypeNode castType, Expr expr, DepParameterExpr dep) {
    	DepCast_c superCopy = (DepCast_c) super.reconstruct(castType, expr);
    	if (dep != superCopy.dep) {
    	    return (DepCast_c) superCopy.dep(dep);
    	}
    	
    	return superCopy;
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
