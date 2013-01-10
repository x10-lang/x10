package x10.types.constraints.xnative;

import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Qualifier;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.VarDef;
import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.smt.XSmtVar;
import x10.constraint.xnative.XNativeField;
import x10.constraint.xnative.XNativeVar;
import x10.constraint.xnative.XNativeTerm;
import x10.types.X10ClassDef;
import x10.types.X10FieldDef;
import x10.types.constraints.CQualifiedVar;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.QualifierDef;
import x10.types.constraints.smt.CSmtQualifiedVar;

public class QualifiedVar extends XNativeField<Type,QualifierDef> implements CQualifiedVar {
    private static final long serialVersionUID = -407228981450822754L;

   	QualifiedVar(QualifierDef qualifier, XNativeTerm<Type> var, boolean hidden) {
   		// this.F  -- the qualifier is the field name, the var is 'this' usually
		super(var, qualifier, hidden);
	}
 
	public QualifiedVar(QualifiedVar other) {
		super(other);
	}

	//@Override
	//public XNativeLocal<Type> var() {
	//	return (XNativeLocal<Type>)get(0); 
	//}
	
	@Override
	public QualifiedVar copy() {
		return new QualifiedVar(this); 
	}	
	
	@Override
	public String toString() {
		return field().getName() + "." + get(0);
	}
}
