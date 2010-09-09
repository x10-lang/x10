/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.List;

/**
 * The representation of a local variable reference.
 * 
 * @author vj
 * 
 */
public class XLocal_c extends XVar_c implements XLocal {
	public final XName name;

	public XLocal_c(XName name) {
		this.name = name;
	}

	public XVar[] vars() {
		return new XVar[] { this };
	}

	public XVar rootVar() {
		return this;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean hasVar(XVar v) {
		return equals(v);
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof XLocal) {
			XLocal other = (XLocal) o;
			return name.equals(other.name());
		}
		return false;
	}

	public XName name() {
		return name;
	}

	public String toString() {
		return name.toString();
	}

	public boolean prefixes(XTerm t) {
		if (equals(t))
			return true;
		if (!(t instanceof XVar))
			return false;
		XTerm[] vars = ((XVar) t).vars();

		return vars.length > 0 && equals(vars[0]);
	}

	public void variables(List<XVar> vars) {
		vars.add(this);
	}
}
