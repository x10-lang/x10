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
import polyglot.ast.Instanceof;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Globals;
import polyglot.frontend.GoalSet;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;
import x10.runtime.Report;

/**
 * @author vj
 *
 */
public class DepInstanceof_c extends X10Instanceof_c implements DepInstanceof,
		X10DepCastInfo {

	protected boolean depTypeCheckingNeeded = false;
	protected DepParameterExpr dep;
	Type lookaheadType = null;

	public DepInstanceof_c(Position pos, TypeNode compareType,
			DepParameterExpr d, Expr expr) {
			super(pos, expr,compareType);
			this.dep = d;
			
		    }
	  

	public DepParameterExpr dep() {
		return dep;
	}
	
	public boolean isDepTypeCheckingNeeded() {
		return this.depTypeCheckingNeeded;
	}
	
	public Context enterChildScope(Node child, Context c) {
		if (child == this.dep) {
			TypeSystem ts = c.typeSystem();
			c = ((X10Context) c).pushDepType(compareType.typeRef());
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}
	
        public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
            // Override to disambiguate the base type and rebuild the node before type checking the dep clause.

            TypeSystem ts = tc.typeSystem();
            NodeFactory nf = tc.nodeFactory();

            DepInstanceof_c n = this;

            TypeChecker childtc = (TypeChecker) tc.enter(parent, n);
            
            Expr e = (Expr) n.visitChild(n.expr, childtc);
            n = (DepInstanceof_c) n.expr(e);

            TypeNode tn = (TypeNode) n.visitChild(n.compareType, childtc);
            n = (DepInstanceof_c) n.compareType(tn);
            
            if (tn.type() instanceof UnknownType) {
                return n;
            }
            
            DepParameterExpr dep = (DepParameterExpr) n.visitChild(n.dep, childtc);
            n = (DepInstanceof_c) n.dep(dep);
            
            X10Type t = (X10Type) tn.type();

            if (dep != null) {
                t = X10TypeMixin.depClause(t, dep.constraint());
            }

            n.depTypeCheckingNeeded = false;
;
            // check if the cast is statically valid
            n = (DepInstanceof_c) n.superTypeCheck(tc);

            // cast is either statically valid or requires a runtime check
            Type toType = n.compareType.type();
            Type fromType = n.expr.type();
            X10Type x10ToType = (X10Type) toType;
            X10Type x10FromType = (X10Type) fromType;
            X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();

            // if toType is nullable and is constrained then expr must be not null
            this.notNullRequired = this.isToTypeNullable() && xts.isTypeConstrained(toType);
            
            n.depTypeCheckingNeeded = xts.isTypeConstrained(toType) && !xts.isTypeConstrained(fromType);
            
            return n;
        }
      
      public Node superTypeCheck(TypeChecker tc) throws SemanticException {
          return super.typeCheck(tc);
      }

	public DepParameterExpr depParameterExpr() {
		return  this.dep;
}
	
	public DepInstanceof dep(DepParameterExpr dep) {
	    DepInstanceof_c n = (DepInstanceof_c) copy();
	    n.dep = dep;
	    return n;
	}
	
    /** Reconstruct the expression. */
    protected DepInstanceof_c reconstruct(Expr expr, TypeNode compareType, DepParameterExpr dep) {
    	DepInstanceof_c superCopy = (DepInstanceof_c) super.reconstruct(expr, compareType);
    	if (dep != this.dep) {
    		DepInstanceof_c copy = (DepInstanceof_c) superCopy.copy();
    		copy.dep = dep;
    		return copy;
    	}
    	return (superCopy == this) ? this : superCopy;
    }

    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
    	//Report.report(1, "DepInstanceof_c: Visiting this.compareType=" + this.compareType + " with visitor " + v);
    	TypeNode compareType = (TypeNode) visitChild(this.compareType, v);
    	//Report.report(1, "DepInstanceof_c: yields " + compareType);
    	//Report.report(1, "DepInstanceof_c: Visiting this.expr=" + this.dep + " with visitor " + v);
    	DepParameterExpr depExpr = (DepParameterExpr) visitChild(this.dep, v);
    	//Report.report(1, "DepInstanceof_c: yields " + depExpr);
		Expr expr = (Expr) visitChild(this.expr, v)  ;
	
		return reconstruct(expr, compareType, depExpr);
    }
    public String toString() {
    	return "/*depInstanceof*/" + super.toString();
    }
}
