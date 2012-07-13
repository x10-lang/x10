package x10.constraint.xsmt;

import x10.constraint.XEQV;
import x10.constraint.XVar;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class XSmtEQV extends XSmtVar implements XEQV, SmtVariable {
	private static final long serialVersionUID = -7899427868473643564L;
	private final int num;
	private String smtName; 
	public XSmtEQV(int n) {this.num=n; smtName = null;}
	
	@Override
	public boolean hasEQV() { return true; }
	
	@Override public String toString() {return "eqv#" + num;}

	@Override
	public XSmtKind getKind() {
		return XSmtKind.VARIABLE;
	}

	@Override
	public String getName() {
		if (smtName == null)
			smtName = SmtUtil.mangle(toString());
		return smtName;
	}
	@Override
	public boolean hasVar(XVar v) {
		return equals(v); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XSmtEQV other = (XSmtEQV) obj;
		if (num != other.num)
			return false;
		return true;
	}
	
	@Override
	public String toSmt2() {
		return getName(); 
	}

}
