package x10.constraint.smt;

import x10.constraint.XField;
import x10.constraint.XLabeledOp;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XType;

public class XSmtField<T extends XType, F> extends XSmtExpr<T> implements XField<T,F> {
	protected XSmtField(F field, XSmtTerm<T> receiver, T type) {
		super(XOp.makeLabelOp(field,type), false, receiver);
	}
	
	protected XSmtField(F field, XSmtTerm<T> receiver, T type, boolean hidden) {
		super(XOp.makeLabelOp(field,type), hidden, receiver);
	}
	
	protected XSmtField(XSmtField<T,F> other) {
		super(other);
	}

	
	@Override
	public XSmtField<T,F> copy() {
		return new XSmtField<T,F>(this); 
	}

	@SuppressWarnings("unchecked")
	@Override
	public F field() {
		return ((XLabeledOp<T,F>)op()).getLabel(); 
	}

	@Override
	public XTerm<T> receiver() {
		return get(0);
	}

}
