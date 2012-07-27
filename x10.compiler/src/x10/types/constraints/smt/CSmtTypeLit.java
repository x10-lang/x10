package x10.types.constraints.smt;

import polyglot.types.Type;
import x10.constraint.smt.XSmtLit;
import x10.types.constraints.XTypeLit;

public class CSmtTypeLit extends XSmtLit<Type, Type> implements XTypeLit {

	CSmtTypeLit(Type t, Type val) {
		super(t, val);
	}

}
