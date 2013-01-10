package x10.types.constraints.smt;

import polyglot.types.Type;
import x10.constraint.smt.XSmtField;
import x10.constraint.smt.XSmtTerm;
import x10.types.constraints.CQualifiedVar;
import x10.types.constraints.QualifierDef;

public class CSmtQualifiedVar extends XSmtField<Type, QualifierDef> implements CQualifiedVar {
	CSmtQualifiedVar(QualifierDef qualifier, XSmtTerm<Type> var) {
		super(qualifier, var, false);
	}
 
	public CSmtQualifiedVar(CSmtQualifiedVar other) {
		super(other);
	}


	//@Override
	//public XSmtVar<Type> var() {
	//	return (XSmtVar<Type>)get(0); 
	//}
	
	@Override
	public CSmtQualifiedVar copy() {
		return new CSmtQualifiedVar(this); 
	}
}
