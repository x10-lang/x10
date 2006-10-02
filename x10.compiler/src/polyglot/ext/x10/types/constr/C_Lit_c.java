package polyglot.ext.x10.types.constr;

import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10Type;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

public class C_Lit_c extends C_Term_c implements C_Lit {
	
	
	Object val;
	public static final transient C_Lit_c FALSE = new C_Lit_c(false);
	public static final transient C_Lit_c TRUE = new C_Lit_c(true);
	
	
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
	public String toString() { return val.toString();}
	public int hashCode() {
		return ((val == null) ? 0 : val.hashCode());
	}
	public C_Lit not() {
		TypeSystem ts = type().typeSystem();
		assert (type().equals(ts.Boolean()));
		return equals(C_Lit_c.TRUE) ? C_Lit_c.FALSE : C_Lit_c.TRUE;
	}
	public boolean equals(Object o) {
		if (this==o) return true;
		if (! (o instanceof C_Lit_c)) return false;
		C_Lit_c other = (C_Lit_c) o;
		return  val.equals(other.val);
	}
}
