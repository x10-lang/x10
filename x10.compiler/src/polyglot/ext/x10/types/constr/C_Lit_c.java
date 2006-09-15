package polyglot.ext.x10.types.constr;

import polyglot.types.Type;
import polyglot.ext.x10.types.X10TypeSystem_c;

public class C_Lit_c extends C_Term_c implements C_Lit {
	
	
	Object val;
	public C_Lit_c(Object l, Type t) {
		super(t);
		val = l;
	}
	
	public Object val() {
		
		return val;
	}
	public String toString() { return val.toString();}
	public int hashCode() {
		return ((val == null) ? 0 : val.hashCode());
	}
	public C_Lit not() {
		assert (type().equals(C_Term.typeSystem.Boolean()));
		return equals(C_Lit.TRUE) ? C_Lit.FALSE : C_Lit.TRUE;
	}
	public boolean equals(Object o) {
		if (! (o instanceof C_Lit_c)) return false;
		C_Lit_c other = (C_Lit_c) o;
		return  val.equals(other.val);
	}
}
