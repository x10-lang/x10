package x10.constraint.xsmt;

import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class XSmtUQV extends XSmtVar implements XUQV, SmtVariable {
	private static final long serialVersionUID = -7899427868473643564L;
	private final int num;
	private final String str; 
	private String smtName; 
	public XSmtUQV(int n) {this.num=n; str = null; smtName = null;}
	public XSmtUQV(String s, int n) {str = s; this.num=n; smtName = null; }
	
	@Override 
	public String toString() {return str == null? "uqv#" + num : str+num;}
	
	@Override
	public XSmtKind getKind() {
		return XSmtKind.VARIABLE;
	}
	
	@Override
	public String getName() {
		if(smtName == null)
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
		XSmtUQV other = (XSmtUQV) obj;
		if (num != other.num)
			return false;
		return true;
	}
	@Override
	public String toSmt2() {
		return getName(); 
	}

	
}
