package x10.types.constraints.xnative;

import polyglot.types.Type;
import x10.constraint.xnative.XNativeField;
import x10.constraint.xnative.XNativeTerm;
import x10.types.constraints.CQualifiedVar;
import x10.types.constraints.QualifierDef;

public class CNativeQualifiedVar extends XNativeField<Type,QualifierDef> implements CQualifiedVar {
    private static final long serialVersionUID = -407228981450822754L;

   	CNativeQualifiedVar(QualifierDef qualifier, XNativeTerm<Type> var, boolean hidden) {
		super(var, qualifier, hidden);
	}
 
	public CNativeQualifiedVar(CNativeQualifiedVar other) {
		super(other);
	}

	
	@Override
	public CNativeQualifiedVar copy() {
		return new CNativeQualifiedVar(this); 
	}	

}
