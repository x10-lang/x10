package polyglot.ext.x10.types.constr;

import polyglot.ast.Field;
import polyglot.main.Report;
import polyglot.types.FieldInstance;

public class C_Field_c extends C_Var_c implements C_Field {
	public final String name;
	public final FieldInstance fi;
	public final C_Var r;
	public  String name() { return name;}
	public FieldInstance fieldInstance() { return fi;}
	public C_Var receiver() { return r;}
	
	public C_Field_c(Field f, C_Var receiver) {
		this(f.fieldInstance(), receiver);
	}
	public C_Field_c(FieldInstance fi, C_Var receiver) {
		super(fi.type());
		this.name = fi.name();
		this.fi=fi;
		this.r = receiver;
	}
	public C_Var findRootVar() {
		return  r==null? this : r.findRootVar();
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
	
	// If var is a C_Var, then value must be a C_Var too.
	public C_Term substitute(C_Term value, C_Term var) {
		if (equals(var))
			return value;
		C_Var r1 = (C_Var) r.substitute(value, var);
		if (r1.equals(r)) 
			return this;
		return new C_Field_c(fi, r1);
		
		}
	

}
