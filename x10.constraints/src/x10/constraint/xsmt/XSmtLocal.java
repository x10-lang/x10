package x10.constraint.xsmt;

import x10.constraint.XLocal;
import x10.constraint.XVar;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class XSmtLocal<T> extends XSmtVar implements XLocal<T>, SmtVariable {
	private static final long serialVersionUID = -2874744903322007713L;
	public final T name; 
	String smtName; 
	public XSmtLocal(T name) {
		this.name = name;
		smtName = null;
	}
	
	@Override
	public T name() {
		return name; 
	}
	
	@Override
	public XSmtKind getKind() {
		return XSmtKind.VARIABLE; 
	}

	@Override
	public String getName() {
		if (smtName == null)
			smtName = SmtUtil.mangle(name.toString()); 
		return smtName; 
	}

	@Override
	public XSmtVar[] vars() {
		XSmtVar[] v = {this};
		return v; 
	}

	@Override
	public boolean hasVar(XVar v) {
		return this.equals(v); 
	}

	@Override
	public SmtType getType() {
		return SmtType.USort();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		XSmtLocal<?> other = (XSmtLocal<?>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString() {
		String s = name.toString();
		// This should not belong here.
		if (s.startsWith("self")) return "self";
		if (s.startsWith("this")) return "this";
		return s;
	}

	@Override
	public String toSmt2() {
		return getName();
	}


}
