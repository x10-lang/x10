/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class C_Formula_c extends C_Term_c implements C_Formula {
	public final C_Name op;
	public final List<C_Term> arguments;

	public C_Formula_c(C_Name op, List<C_Term> args) {
		this.op = op;
		this.arguments = args;
	}
	public C_Formula_c(C_Name op, C_Term... args) {
		this.op = op;
		this.arguments = new ArrayList<C_Term>(args.length);
		for (C_Term arg : args) {
			this.arguments.add(arg);
		}
	}
	
	public C_Name operator() {
		return op;
	}
	
	public boolean isUnary() {
		return arguments.size() == 1;
	}
	public boolean isBinary() {
		return arguments.size() == 2;
	}
	public C_Term unaryArg() {
		if (isUnary())
			return arguments.get(0);
		return null;
	}
	public C_Term left() {
		if (isBinary())
			return arguments.get(0);
		return null;
	}
	public C_Term right() {
		if (isBinary())
			return arguments.get(1);
		return null;
	}
	
	public List<C_Term> arguments() { return arguments; }

	public C_Term substitute(C_Var value, C_Var var) {
		List<C_Term> l = new ArrayList<C_Term>(arguments.size());
		boolean changed = false;
		for (C_Term arg : arguments) {
			C_Term n = arg.substitute(value, var);
			if (n != arg)
				changed = true;			
		}
		if (!changed)
			return this;
		C_Formula_c newTerm = new C_Formula_c(op, l);
		if (isAtomicFormula)
			newTerm.markAsAtomicFormula();
		return newTerm;
	}

	public Promise internIntoConstraint(Constraint c, Promise last) throws Failure {
		assert last==null;
		Promise result = c.lookup(this);
		if (result != null) // this term has already been interned.
			return result;
		HashMap<C_Name,Promise> fields = new HashMap<C_Name,Promise>();
		for (int i = 0; i < arguments.size(); i++) {
			C_Term arg = arguments.get(i);
			Promise child = c.intern(arg);
			fields.put(new C_NameWrapper<Integer>(i), child);
		}
//		C_Local_c v = new C_Local_c(op);
		C_Term v = this;
//		fields.put(new C_NameWrapper<Integer>(-1), c.intern(v));
		// create a new promise and return it.
		Promise p = new Promise_c(fields, v);
		c.addPromise(v, p);
		result = p;
		return result;
	}
	
	public void variables(List<C_Var> vars) {
		for (C_Term arg: arguments) {
			arg.variables(vars);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(op);
		sb.append("(");
		String sep = "";
		for (C_Term arg : arguments) {
			sb.append(sep);
			sep = ", ";
			sb.append(arg);
		}
		sb.append(")");
		return sb.toString();
	}
	
	public int hashCode() {
		int hash = 29;
		for (C_Term arg: arguments) {
			hash += arg.hashCode();
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if (this==o) return true;
		if (o instanceof C_Formula) {
			C_Formula c = (C_Formula) o;
			if (c.arguments().size() == arguments().size()) {
				for (int i = 0; i < arguments().size(); i++) {
					C_Term ti = arguments().get(i);
					C_Term ci = c.arguments().get(i);
					if (! ti.equals(ci))
						return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public Promise toPromise() {
		throw new Error("Not implemented yet.");
	}
}
