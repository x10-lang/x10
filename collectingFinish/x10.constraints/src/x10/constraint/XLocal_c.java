/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.constraint;

import java.util.Collections;
import java.util.List;

/**
 * The representation of a local variable reference in the constraint system.
 * 
 * @author vj
 * 
 */
public class XLocal_c extends XVar_c implements XLocal {
	public final XName name;

	public XLocal_c(XName name) {
		this.name = name;
	}
	public XTermKind kind() { return XTermKind.LOCAL;}
	public XVar[] vars() {
		return new XVar[] { this };
	}
	
	public List<XEQV> eqvs() {
	    return Collections.EMPTY_LIST;
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
		String s = name.toString();
		if (s.startsWith("self"))
			return "self";
		if (s.startsWith("this"))
			return "this";
		return s;
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
