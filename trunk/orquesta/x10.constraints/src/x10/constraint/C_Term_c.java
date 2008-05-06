/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.ArrayList;
import java.util.List;

public abstract class C_Term_c implements C_Term {
	public C_Term_c() {
		super();
	}

	public boolean rootVarIsSelf() {
		return false;
	}

	public boolean isEQV() {
		return false;
	}

	public boolean prefixes(C_Term term) {
		return false;
	}

	public boolean prefersBeingBound() {
		return isEQV() || this instanceof C_Self;
	}

	protected boolean isAtomicFormula = false;

	public boolean isAtomicFormula() {
		return isAtomicFormula;
	}

	public void markAsAtomicFormula() {
		isAtomicFormula = true;
	}

	public List<C_Var> variables() {
		List<C_Var> result = new ArrayList<C_Var>();
		this.variables(result);
		return result;
	}
}
