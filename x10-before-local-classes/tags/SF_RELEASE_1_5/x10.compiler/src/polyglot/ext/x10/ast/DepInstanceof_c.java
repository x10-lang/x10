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
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
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
	
	public NodeVisitor disambiguateEnter(AmbiguityRemover sc) throws SemanticException {
		X10TypeNode type = (X10TypeNode) compareType.copy();
		X10TypeNode tn = (X10TypeNode) type.disambiguateBase(sc);
		lookaheadType = (tn instanceof NullableNode) ? ((NullableNode) tn).base().type() : tn.type();
		return sc;
	}
	public NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException {
		X10TypeNode type = (X10TypeNode) compareType.copy();
		X10TypeNode tn = (X10TypeNode) type.typeCheckBase(tc);
		lookaheadType = (tn instanceof NullableNode) ? ((NullableNode) tn).base().type() : tn.type();
		return tc;
	}
	
	public Context enterChildScope(Node child, Context c) {
		if (child == this.dep) {
			TypeSystem ts = c.typeSystem();
			if (lookaheadType instanceof X10NamedType) {
				c = ((X10Context) c).pushDepType((X10NamedType) lookaheadType);
			}
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}
	  public Node typeCheck(TypeChecker tc) throws SemanticException {
		//  Report.report(1, "DepInstance_of: " + this + " dep=" + dep);
		super.typeCheck(tc);
		Instanceof n = (Instanceof) node();
		Type toType = n.compareType().type();
		Type fromType = n.expr().type();
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		
		this.depTypeCheckingNeeded = false;

		// if toType is nullable and is constrained then expr must be not null
		this.notNullRequired = this.isToTypeNullable() && xts.isTypeConstrained(toType);
		
		this.depTypeCheckingNeeded = 
			xts.isTypeConstrained(toType) && !xts.isTypeConstrained(fromType);
		
		return n.type(tc.typeSystem().Boolean());
	  }

	public DepParameterExpr depParameterExpr() {
		return  this.dep;
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
