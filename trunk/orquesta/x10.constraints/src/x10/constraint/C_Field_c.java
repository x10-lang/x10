/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.List;

public class C_Field_c extends C_Var_c implements C_Field {
	public C_Var receiver;
	public C_Name field;

	public C_Field_c(C_Var receiver, C_Name field) {
		super();
		this.receiver = receiver;
		this.field = field;
	}

	public C_Name field() {
		return field;
	}

	public String name() {
		return field.toString();
	}

	public C_Var receiver() {
		return receiver;
	}

	public int hashCode() {
		return receiver.hashCode() + field.hashCode();
	}

	public boolean equals(Object o) {
		if (o instanceof C_Field_c) {
			C_Field_c other = (C_Field_c) o;
			return receiver.equals(other.receiver) && field.equals(other.field);
		}
		return false;
	}

	public String toString() {
		return (receiver == null ? "" : receiver.toString() + ".") + field;
	}

	public boolean isEQV() {
		return receiver().isEQV();
	}

	// If var is a C_Var, then value must be a C_Var too.
	public C_Var substitute(C_Var value, C_Var var) {
		if (equals(var))
			return value;
		C_Var r1 = (C_Var) receiver.substitute(value, var);
		if (r1.equals(receiver))
			return this;
		return new C_Field_c(r1, field);

	}

	// memoize rootVar and path.
	protected C_Var[] vars;

	public C_Var[] vars() {
		if (vars == null)
			initVars();
		return vars;
	}

	public C_Var rootVar() {
		if (vars == null)
			initVars();
		return vars[0];
	}

	public boolean prefixes(C_Term t) {
		if (equals(t))
			return true;
		if (!(t instanceof C_Var))
			return false;
		C_Var[] vars = ((C_Var) t).vars();
		boolean result = false;
		for (int i = 0; (!result) && i < vars.length; i++) {
			result = equals(vars[i]);
		}
		return result;
	}

	protected void initVars() {
		int count = 0;
		for (C_Var source = this; source instanceof C_Field; source = ((C_Field) source).receiver())
			count++;
		// Report.report(1, "C_Field_c: Count for " + this + " is " + count + ".");
		vars = new C_Var[count + 1];
		C_Var f = this;
		for (int i = count; i >= 0; i--) {
			// Report.report(1, "C_Field_c.initVars setting vars["+i+"] to " + f
			// + ".");
			vars[i] = f;
			if (i > 0)
				f = ((C_Field) f).receiver();
		}

	}

	public void variables(List<C_Var> result) {
		receiver().variables(result);
		result.add(this);
	}

}
