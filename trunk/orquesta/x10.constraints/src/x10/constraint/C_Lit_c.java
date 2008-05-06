/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.HashMap;
import java.util.List;

public class C_Lit_c extends C_Term_c implements C_Lit {
	Object val;

	public C_Lit_c(Object l) {
		val = l;
	}

	public Object val() {
		return val;
	}

	public String toString() {
		if (val == null)
			return "null";
		return val.toString();
	}

	public int hashCode() {
		return ((val == null) ? 0 : val.hashCode());
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof C_Lit_c))
			return false;
		C_Lit_c other = (C_Lit_c) o;
		return val == null ? o == null : val.equals(other.val);
	}

	// methods from Promise
	public Promise intern(C_Var[] vars, int index) throws Failure {
		return intern(vars, index, null);
	}

	public Promise intern(C_Var[] vars, int index, Promise last) throws Failure {
		if (index != vars.length) {
			throw new Failure("Cannot extend path " + vars + "index=" + index + " beyond the literal " + this + ".");
		}
		return this;
	}

	public Promise lookup(C_Var[] vars, int index) throws Failure {
		if (index != vars.length) {
			throw new Failure("Cannot extend path " + vars + "index=" + index + " beyond the literal " + this + ".");
		}
		return this;
	}

	public Promise lookup(C_Name s) {
		return null;
	}

	public Promise lookup() {
		return this;
	}

	public boolean forwarded() {
		return false;
	}

	public boolean hasChildren() {
		return false;
	}

	public boolean bind(Promise target) throws Failure {
		if (target.term().equals(this))
			return true;
		if (target.term() instanceof C_Lit) {
			throw new Failure("Cannot bind literal " + this + " to " + target);
		}
		if (target.term() instanceof C_Var) {
			return target.bind(this);
		}
		if (!equals(target))
			throw new Failure("Cannot bind literal " + this + " to " + target);
		return false;
	}

	public boolean canReach(Promise other) {
		return equals(other);
	}

	public C_Var term() {
		return this;
	}

	public void dump(List<C_Term> result, C_Term prefix) { /* nothing to dump */}

	public void dump(List<C_Term> result, C_Term prefix, C_Var newSelf) {
	// nothing to dump.
	}

	public void addIn(C_Name s, Promise orphan) throws Failure {
		throw new Failure("Cannot add an " + s + " child " + orphan + " to a literal, " + this + ".");
	}

	public void setTerm(C_Var term) { /* ignore */}

	public String instance() {
		return toString();
	}

	/** In case this is a field selection x.f1...fn, return x, x.f1, x.f1.f2, ... x.f1.f2...fn */
	public C_Var[] vars() {
		return new C_Var[0];
	}

	/** In case this is a field selection x.f1...fn, return x, else this. */
	public C_Var rootVar() {
		return this;
	}

	public void replaceDescendant(Promise y, Promise x) {
	// nothing to do.
	}

	public Promise value() {
		return null;
	}

	public HashMap<C_Name, Promise> fields() {
		return null;
	}

	public Promise cloneRecursively(HashMap<Promise, Promise> env) {
		return this;
	}

	public C_Var substitute(C_Var x, C_Var y) {
		return this;
	}

	public void variables(List<C_Var> result) {}

	public Promise internIntoConstraint(Constraint constraint, Promise last) throws Failure {
		throw new Failure("Internal error -- should not be called.");
	}
}
