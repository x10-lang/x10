package polyglot.ext.x10.types.constr;

import java.util.HashMap;

import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10Type;
import polyglot.main.Report;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;

public class C_Lit_c extends C_Term_c implements C_Lit {
	
	
	Object val;
	public C_Lit_c(boolean b) {
		super(Constraint_c.typeSystem.Boolean());
		val = new Boolean(b);
		
	}
	public C_Lit_c(Object l, Type t) {
		super(t);
		val = l;
	}
	private C_Lit type(X10Type t) {
		return new C_Lit_c(val, t);
	}
	
	public Object val() {
		return val;
	}
	public String toString() {
                if (val == null) return "null";
                if (type().isLong()) return val.toString() + "L";
                if (type().isFloat()) return val.toString() + "F";
                return val.toString();
        }
	public int hashCode() {
		return ((val == null) ? 0 : val.hashCode());
	}
	public C_Lit not() {
		TypeSystem ts = type().typeSystem();
		assert (type().equals(ts.Boolean()));
		return equals(C_Lit.TRUE) ? C_Lit.FALSE : C_Lit.TRUE;
	}
	public boolean equals(Object o) {
		if (this==o) return true;
		if (! (o instanceof C_Lit_c)) return false;
		C_Lit_c other = (C_Lit_c) o;
		return  val.equals(other.val);
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
		if (target.term() instanceof C_Var) {
			return target.bind(this);
		}
		if (! equals(target))
			throw new Failure("Cannot bind literal " + this + " to " + target);
		return false;
	}
	public boolean canReach(Promise other ) { return equals(other); }
	public C_Term term() { return this; }
	public void dump(HashMap<C_Term,C_Term> result, C_Term prefix) { 	/* nothing to dump */ }
	public void dump(HashMap<C_Term, C_Term> result, C_Term prefix, C_Term newSelf, C_Term newThis) {
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
	
}
