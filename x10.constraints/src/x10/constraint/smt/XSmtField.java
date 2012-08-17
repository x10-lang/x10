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
	public XSmtTerm<T> receiver() {
		return get(0);
	}
	@Override
	public String prettyPrint() {
		if (field() instanceof XType) {
			// this is a qualified variable
			return field().toString() + "." + receiver().prettyPrint(); 
		}
		return receiver().prettyPrint() + "." + op().prettyPrint(); 
	}
	@Override
	public XSmtTerm<T> subst(XTerm<T> t1, XTerm<T> t2) {
		if (this.equals(t2))
			return (XSmtTerm<T>)t1; 
		
		XSmtTerm<T> new_receiver = receiver().subst(t1, t2);
		if (new_receiver != receiver()) {
			return new XSmtField<T,F>(field(), new_receiver, type());
		}
		return this;
	}
}
