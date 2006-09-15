package polyglot.ext.x10.types.constr;

import polyglot.ast.Local;
import polyglot.types.LocalInstance;

/**
 * The representation of a local variable reference.
 * @author vj
 *
 */
public class C_Local_c extends C_Var_c implements C_Local {
	public final String name;
	public final LocalInstance li;

		public String name() { return name;}
	public C_Local_c(Local t) {
		super(t.type());
		this.name = t.name();
		this.li = t.localInstance();
	}
	
	public int hashCode() {
		return ((name == null) ? 0 : name.hashCode()) + (li==null ?  0 : li.hashCode());
	}
	public boolean equals(Object o) {
		if (! (o instanceof C_Local_c))
			return false;
		if (o == null) return false;
		C_Local_c other = (C_Local_c) o;
		boolean val = name.equals(other.name) && li.equals(other.li);
		return val;
	}

}
