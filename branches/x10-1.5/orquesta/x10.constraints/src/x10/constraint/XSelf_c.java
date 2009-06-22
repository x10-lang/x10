/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.ArrayList;
import java.util.List;

public class XSelf_c extends XVar_c implements XSelf {
	public XSelf_c() {}

	public boolean rootVarIsSelf() {
		return true;
	}

	public XVar rootVar() {
		return this;
	}

	public XVar[] vars() {
		return new XVar[] { this };
	}

	public String name() {
		return "self";
	}

	public int hashCode() {
		return 0;
	}
	
	public boolean hasVar(XVar v) {
		return equals(v);
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		return o instanceof XSelf_c;
	}

	public boolean prefixes(XTerm t) {
		if (equals(t))
			return true;
		if (!(t instanceof XVar))
			return false;
		XTerm[] vars = ((XVar) t).vars();
		return equals(vars[0]);
	}

	public String toString() {
		return "self";
	}

	public void variables(List<XVar> result) {
		result.add(this);
	}
}
