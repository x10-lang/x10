package x10.types.constraints.smt;

import java.util.List;

import polyglot.types.Def;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.XLabeledOp;
import x10.constraint.XOp;
import x10.constraint.XVar;
import x10.constraint.XDef;
import x10.constraint.smt.XSmtExpr;
import x10.constraint.smt.XSmtField;
import x10.constraint.smt.XSmtTerm;
import x10.types.X10ClassDef;
import x10.types.X10FieldDef;
import x10.types.constraints.CField;

public class CSmtField<D extends XDef<Type>> extends XSmtField<Type, D> implements CField<D> {
	CSmtField(D d, XSmtTerm<Type> receiver) {
		super(d, receiver,d.resultType());
	}
	CSmtField(D d, XSmtTerm<Type> receiver, boolean hidden) {
		super(d, receiver,d.resultType(), hidden);
	}
	
	@Override
	public D def() {
		return field(); 
	}

	@Override
	public XVar<Type> thisVar() {
        if (def() instanceof X10FieldDef)
            return ((X10ClassDef) Types.get(((X10FieldDef) def()).container()).toClass().def()).thisVar();
        return null;
	}
	
	@Override
	public String toString() {
		return get(0) + "." + def();
	}

}
