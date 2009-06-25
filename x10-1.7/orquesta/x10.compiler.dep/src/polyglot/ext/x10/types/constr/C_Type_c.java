/**
 * 
 */
package polyglot.ext.x10.types.constr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Type;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;

/**
 * @author VijaySaraswat
 *
 */
public class C_Type_c extends C_Term_c implements C_Type {

	/**
	 * @param t
	 */
	public C_Type_c(Type t) {
		super(t);
		
	}

	public C_Type_c(TypeNode tn) {
		this(tn.type());
	}
	
	public C_Term copy() {
	    return new C_Type_c(type);
	}
	
	public C_Term substitute(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
	    return new C_Type_c(substituteType(y, x, propagate, visited));
	}

	public String toString() {
		
		return type.toString();
	}
	public int hashCode() {
		return type.hashCode();
	}
	
	public boolean equals(Object o) {
		if (this==o) return true;
		if (! (o instanceof C_Type_c)) return false;
		C_Type_c other = (C_Type_c) o;
		return  type.typeEquals(other.type);
	}
	// methods from Promise
	public Promise intern(C_Var[] vars, int index) {
		return intern(vars, index, null);
	}
	public Promise intern(C_Var[] vars, int index, Promise last) {
		if (index != vars.length) {
			throw new InternalCompilerError("Cannot extend path " + vars + "index=" 
					+ index + " beyond the type " + this  + ".");
		}
		return this;
	}
	public Promise lookup(C_Var[] vars, int index) {
		if (index != vars.length) {
			throw new InternalCompilerError("Cannot extend path " + vars + "index=" 
					+ index + " beyond the type" + this  + ".");
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
		
		if (! equals(target))
			throw new Failure("Cannot bind type " + this + " to " + target);
		return false;
	}
	public boolean canReach(Promise other ) { return equals(other); }
	public C_Var term() { return this; }
	public void dump(HashMap<C_Var,C_Var> result, C_Term prefix) { 	/* nothing to dump */ }
	public void dump(HashMap<C_Var, C_Var> result, C_Term prefix, C_Var newSelf, C_Var newThis) {
		// nothing to dump.
	}
	public void addIn(String s, Promise orphan) {
		throw new InternalCompilerError("Cannot add an " + s + " child "  + orphan + 
				" to a type, " + this + ".");
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
	public C_Var substitute(C_Var y, C_Var x) {
		return this;
	}
	

}
