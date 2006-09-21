package polyglot.ext.x10.types.constr;

import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

public class C_Lit_c extends C_Term_c implements C_Lit {
	
	
	Object val;
	public static final  transient X10TypeSystem typeSystem = X10TypeSystem_c.getTypeSystem();
	public static final transient C_Lit_c FALSE 
	= new C_Lit_c(new Boolean(false), typeSystem.Boolean());
	public static final transient C_Lit_c TRUE 
	= new C_Lit_c(new Boolean(true), typeSystem.Boolean());
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
		TypeSystem ts = type().typeSystem();
		assert (type().equals(ts.Boolean()));
		return equals(C_Lit_c.TRUE) ? C_Lit_c.FALSE : C_Lit_c.TRUE;
	}
	public boolean equals(Object o) {
		if (! (o instanceof C_Lit_c)) return false;
		C_Lit_c other = (C_Lit_c) o;
		return  val.equals(other.val);
	}
}
