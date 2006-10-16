package polyglot.ext.x10.types.constr;

import polyglot.ast.Field;
import polyglot.main.Report;
import polyglot.types.FieldInstance;

public class C_Field_c extends C_Var_c implements C_Field {
	public final String name;
	public final FieldInstance fi;
	public final C_Receiver r;
	public  String name() { return name;}
	public FieldInstance fieldInstance() { return fi;}
	public C_Receiver receiver() { return r;}
	
	public C_Field_c(Field f, C_Receiver receiver) {
		this(f.fieldInstance(), receiver);
	}
	public C_Field_c(FieldInstance fi, C_Receiver receiver) {
		super(fi.type());
		this.name = fi.name();
		this.fi=fi;
		this.r = receiver;
	}
	public int hashCode() {
		return ((name == null) ? 0 : name.hashCode()) + (r==null ?  0 : r.hashCode());
	}
	public boolean equals(Object o) {
		
		if (! (o instanceof C_Field_c))
			return false;
		if (o == null) return false;
		C_Field_c other = (C_Field_c) o;
		
		boolean val = name.equals(other.name) && r.equals(other.r);
	
		return val;
	}
	public String toString() { return (r==null? "" : r.toString() + ".") + name;}
}
