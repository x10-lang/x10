/*
 *
 * (C) Copyright IBM Corporation 2006
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
public class C_Local_c extends C_Var_c implements C_Local {
	public final C_Name name;

	public C_Local_c(C_Name name) {
		this.name = name;
	}

	protected C_Var[] vars = new C_Var[] { this };

	public C_Var[] vars() {
		return vars;
	}

	public C_Var rootVar() {
		return this;
	}

	public boolean isEQV() {
		return false;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof C_Local_c) {
			C_Local_c other = (C_Local_c) o;
			return name.equals(other.name());
		}
		return false;
	}

	public C_Name name() {
		return name;
	}

	public String toString() {
		return name.toString();
	}

	public boolean prefixes(C_Term t) {
		if (equals(t))
			return true;
		if (!(t instanceof C_Var))
			return false;
		C_Term[] vars = ((C_Var) t).vars();

		return vars.length > 0 && equals(vars[0]);
	}

	public void variables(List<C_Var> vars) {
		vars.add(this);
	}
}
