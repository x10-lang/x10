/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.HashMap;

import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;

public class C_Here_c extends C_Lit_c implements Promise {

//	public static final C_Here_c here = new C_Here_c();

    	/**
    	 * Clients should not call this constructor; you can instead obtain the singleton instance from the X10TypeSystem.
    	 */
	public C_Here_c(X10TypeSystem x10ts) {
	    this(x10ts.place());
	}
	public C_Here_c(Type t) {
	    super(null, t);
	}
	public C_Term copy() {
	    return new C_Here_c(type);
	}
	public String toString() { return "C_here"; }
	public boolean equals(Object o) { 
		if (o == this) return true;
		if (! (o instanceof C_Here_c)) return false;
		return true;
	}
	public Promise intern(C_Var[] vars, int index) {
		return intern(vars, index, null);
	}
	public Promise intern(C_Var[] vars, int index, Promise last) {
		if (index != vars.length) {
			throw new InternalCompilerError("Cannot extend path " + vars + "index=" 
					+ index + " beyond the literal " + this  + ".");
		}
		return this;
	}
	public boolean forwarded() { return false;}
	public boolean hasChildren() { return false;}
	public boolean  bind(Promise target) throws Failure {
		if (! equals(target))
		throw new Failure("Cannot bind here to " + target);
		return false;
	}
	public boolean canReach(Promise other ) {
		return equals(other);
	}
	public C_Var term() {
		return this;
	}
	public void dump(HashMap<C_Var, C_Var> result, C_Term prefix) {
		// nothing to dump.
	}
	public void dump(HashMap<C_Var,C_Var> result, C_Term prefix, C_Term newSelf, C_Term newThis) {
		// nothing to dump.
	}
	public Promise lookup( C_Var[] vars, int index) {
		if (index==vars.length) 
			return this;
		return null;
	}
	public Promise lookup(String s) { return null; }
	 public Promise lookup() { return this; }
	public void addIn(String s, Promise orphan) {
		throw new InternalCompilerError("Cannot add an " + s + " child "  + orphan + 
				" to here.");
	}
	public boolean rootVarIsSelf() { return false;}
	public boolean rootVarIsThis() { return false;}
	public boolean isEQV() { return false;}
	public boolean prefixes(C_Term term) {return false;}
	public void setTerm(C_Var term) { /* ignore */}
	public void replaceDescendant(Promise y, Promise x) {
		// nothing to do.
	}
	public Promise value() { return null; }
	public HashMap<String,Promise> fields() { return null;}
	public Promise cloneRecursively(HashMap<Promise,Promise> env) {
		return this;
	}
}
