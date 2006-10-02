package polyglot.ext.x10.types.constr;

import polyglot.types.Type;

public class C_Here_c extends C_Term_c {

	public static final C_Here_c here = new C_Here_c();
	private C_Here_c() {
		super(Constraint_c.typeSystem.place());
	}
	public String toString() { return "here"; }
	public boolean equals(Object o) { 
		if (o == this) return true;
		if (! (o instanceof C_Here_c)) return false;
		return true;
	}
}
