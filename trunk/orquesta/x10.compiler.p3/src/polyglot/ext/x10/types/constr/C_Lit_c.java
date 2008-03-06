/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;

public class C_Lit_c extends C_Term_c implements C_Lit {
	
	
	Object val;
	public C_Lit_c(boolean b, TypeSystem ts) {
		super(ts.Boolean());
		val = new Boolean(b);
		
	}
	public C_Lit_c(Object l, Type t) {
		super(t);
		val = l;
	}
	private C_Lit type(X10Type t) {
		return new C_Lit_c(val, t);
	}
	
	public C_Term copy() {
	    return new C_Lit_c(val, type);
	}
	
	public C_Term substitute(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
	    if (!propagate)
	        return this;
	    return new C_Lit_c(val, substituteType(y, x, propagate, visited));
	}

	public Object val() {
		return val;
	}
	public String toString() {
                if (val == null) return "null";
                if (type().isLong()) return val.toString() + "L";
                if (type().isFloat()) return val.toString() + "F";
                if (type().typeEquals(type().typeSystem().String())) return "\"" + val + "\"";
                return val.toString();
        }
	public int hashCode() {
		return ((val == null) ? 0 : val.hashCode());
	}
	public C_Lit not() {
		X10TypeSystem xts = (X10TypeSystem) type().typeSystem();
		assert (type().typeEquals(xts.Boolean()));
		return equals(xts.TRUE()) ? xts.FALSE() : xts.TRUE();
	}
	public C_Lit neg() {
		X10TypeSystem ts = (X10TypeSystem) type().typeSystem();
		Type type = type();
		if (ts.typeBaseEquals(type, ts.Int())) {
			return new C_Lit_c(new Integer(- ((Integer) val).intValue()), ts.Int());
		}
		if (ts.typeBaseEquals(type, ts.Long())) {
			return new C_Lit_c(new Long(- ((Long) val).longValue()), ts.Long());
		}
		if (ts.typeBaseEquals(type, ts.Short())) {
			short s = ((Short) val).shortValue();
			return new C_Lit_c(new Short((short) -s), ts.Short());
		}
		if (ts.typeBaseEquals(type, ts.Float())) {
			return new C_Lit_c(new Float(- ((Float) val).floatValue()), ts.Float());
		}
		if (ts.typeBaseEquals(type, ts.Double())) {
			return new C_Lit_c(new Double(- ((Double) val).doubleValue()), ts.Double());
		}
		assert false;
		return this;
	}
	public boolean equals(Object o) {
	
		if (this==o) return true;
		if (! (o instanceof C_Lit_c)) return false;
		C_Lit_c other = (C_Lit_c) o;
		return  val == null ? o==null : val.equals(other.val);
	}
	// methods from Promise
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
	public Promise lookup(C_Var[] vars, int index) {
		if (index != vars.length) {
			throw new InternalCompilerError("Cannot extend path " + vars + "index=" 
					+ index + " beyond the literal " + this  + ".");
		}
		return this;
	}
	public boolean prefixes(C_Term t) { return false; }
	public Promise lookup(String s) { 	return null;  }
	public Promise lookup() {  return this;  }
	public boolean forwarded() { return false;}
	public boolean hasChildren() { return false;}
	public boolean bind(Promise target) throws Failure {
		if (target.term().equals(this))
			return true;
		if (target.term() instanceof C_Lit) {
			throw new Failure("Cannot bind literal " + this + " to " + target);
		}
		if (target.term() instanceof C_Var) {
			return target.bind(this);
		}
		if (! equals(target))
			throw new Failure("Cannot bind literal " + this + " to " + target);
		return false;
	}
	public boolean canReach(Promise other ) { return equals(other); }
	public C_Var term() { return this; }
	public void dump(HashMap<C_Var, C_Var> result, C_Term prefix) { 	/* nothing to dump */ }
	public void dump(HashMap<C_Var, C_Var> result, C_Term prefix, C_Var newSelf, C_Var newThis) {
		// nothing to dump.
	}
	public void addIn(String s, Promise orphan) {
		throw new InternalCompilerError("Cannot add an " + s + " child "  + orphan + 
				" to a literal, " + this + ".");
	}
	public boolean rootVarIsSelf() { return false;}
	public boolean rootVarIsThis() { return false;}
	public boolean isEQV() { return false;}
	public void setTerm(C_Var term) { /* ignore */}
	
	public String name() {
		return toString();
	}
	
	/** In case this is a field selection x.f1...fn, return x, x.f1, x.f1.f2, ... x.f1.f2...fn */
	public C_Var[] vars() {
		return new C_Var[0];
	}
	
	public void collectVars(List<C_Var> accum) {
	    accum.add(this);
	}

	/** In case this is a field selection x.f1...fn, return x, else this. */
	public C_Var rootVar() {
		return this;
	}
	
	/** In case this is a field selection x.f1...fn, return the path f1...fn, else null. */
	public Path path() {
		return null;
	}
	public void replaceDescendant(Promise y, Promise x) {
		// nothing to do.
	}
	public Promise value() { return null; }
	public HashMap<String,Promise> fields() { return null;}
	public Promise cloneRecursively(HashMap<Promise,Promise> env) {
		return this;
	}
	public C_Var substitute(C_Var x, C_Var y) {
		return this;
	}
	
}
