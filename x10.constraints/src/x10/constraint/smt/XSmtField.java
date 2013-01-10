package x10.constraint.smt;

import x10.constraint.XConstraintSystem;
import x10.constraint.XDef;
import x10.constraint.XField;
import x10.constraint.XLabeledOp;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XType;

public class XSmtField<T extends XType, F extends XDef<T>> extends XSmtExpr<T> implements XField<T,F> {

	public XSmtField(F field, XSmtTerm<T> receiver, boolean hidden) {
		super(XOp.makeLabelOp(field), hidden, receiver);
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
	public String toSmtString() {
		if (field() instanceof XType) {
			// this is a qualified variable
			return field().toString() + "." + receiver().toSmtString(); 
		}
		return receiver().toSmtString() + "." + op().prettyPrint(); 
	}
	@SuppressWarnings("unchecked")
	@Override
	public XSmtTerm<T> subst(XConstraintSystem<T> sys, XTerm<T> t1, XTerm<T> t2) {
		if (this.equals(t2))
			return (XSmtTerm<T>)t1; 
		
		XSmtTerm<T> new_receiver = receiver().subst(sys, t1, t2);
		if (new_receiver != receiver()) {
			return (XSmtTerm<T>) sys.makeField(new_receiver, field(), isHidden());
		}
		return this;
	}
}
