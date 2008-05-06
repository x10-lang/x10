/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.List;

public class C_Self_c extends C_Var_c implements C_Self {
	public C_Self_c() {}

	public boolean rootVarIsSelf() {
		return true;
	}

	public C_Var rootVar() {
		return this;
	}

	public C_Var[] vars() {
		return new C_Var[] { this };
	}

	public String name() {
		return "self";
	}

	public int hashCode() {
		return 0;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		return o instanceof C_Self_c;
	}

	public boolean prefixes(C_Term t) {
		if (equals(t))
			return true;
		if (!(t instanceof C_Var))
			return false;
		C_Term[] vars = ((C_Var) t).vars();
		return equals(vars[0]);
	}

	public String toString() {
		return "self";
	}

	public void variables(List<C_Var> result) {
		result.add(this);
	}
}
