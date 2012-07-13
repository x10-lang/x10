package x10.types.constraints.xsmt;

import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeLit;
import x10.types.constraints.XTypeLit;
import x10.types.matcher.Subst;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;

/**
 * A subclass of XLit that represents a Type literal.
 * @author vijay
 *
 */

public class XSmtTypeLit extends XNativeLit implements XTypeLit {
	private static final long serialVersionUID = -1222245257474719757L;

	public XSmtTypeLit(Type l) {
		super(l);
	}
	@Override
	public Type type() {
		return (Type) val;
	}

	@Override
	public boolean hasVar(XVar v) {
		return Types.hasVar(type(), v);
	}

	@Override
	public XSmtTypeLit subst(XTerm y, XVar x, boolean propagate) {
		XSmtTypeLit n = (XSmtTypeLit) super.subst(y, x, propagate);
		Type newVal = n.type();
		try {
			newVal = Subst.subst(type(), y, x);
		} catch (SemanticException e) { }
		if (newVal == n.type())
			return n;
		return new XSmtTypeLit(newVal);
	}

}
