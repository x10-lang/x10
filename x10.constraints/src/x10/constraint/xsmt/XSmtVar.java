package x10.constraint.xsmt;

import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public abstract class XSmtVar extends XSmtTerm implements XVar, SmtTerm {
	private static final long serialVersionUID = 7059442798324288755L;
	
	@Override
	public boolean isAtomicFormula() {
		return false;
	}

	@Override
	public boolean hasEQV() {
		return false;
	}


	@Override
	public XSmtTerm subst(XTerm x, XVar v) {
		if (equals(x))
			return (XSmtTerm)v;
		
		return this; 
	}
	@Override
	public XSmtVar[] vars() {
		return new XSmtVar[] { this };
	}

}
