package x10.types.constraints.smt;

import polyglot.types.Type;
import x10.constraint.XLabeledOp;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.smt.XSmtExpr;
import x10.constraint.smt.XSmtField;
import x10.constraint.smt.XSmtVar;
import x10.types.constraints.CQualifiedVar;

public class CSmtQualifiedVar extends XSmtField<Type, Type> implements CQualifiedVar{
	CSmtQualifiedVar(Type type, Type qualifier, XSmtVar<Type> var) {
		super(qualifier, var, type);
	}
 
	public CSmtQualifiedVar(CSmtQualifiedVar other) {
		super(other);
	}

	@Override
	public Type qualifier() {
		return field(); 
	}

	@Override
	public XSmtVar<Type> var() {
		return (XSmtVar<Type>)get(0); 
	}
	
	@Override
	public CSmtQualifiedVar copy() {
		return new CSmtQualifiedVar(this); 
	}
}
