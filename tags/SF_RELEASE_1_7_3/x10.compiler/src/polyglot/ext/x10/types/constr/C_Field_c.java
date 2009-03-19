/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
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
		super(fi.type(), receiver.rootVarIsThis(), receiver.rootVarIsSelf());
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
	public boolean isEQV() { return receiver().isEQV();}
	
	// If var is a C_Var, then value must be a C_Var too.
	public C_Var substitute(C_Var value, C_Var var) {
		if (equals(var))
			return value;
		C_Var r1 = (C_Var) r.substitute(value, var);
		if (r1.equals(r)) 
			return this;
		return new C_Field_c(fi, r1);
		
		}
	
	// memoize rootVar and path.
	protected Path path;
	protected C_Var[] vars;
	public C_Var[] vars() { 
		if (vars == null) initVars();
		return vars;
	}
	public C_Var rootVar() { 
		if (vars == null) initVars();
		return vars[0];
	}
	public Path path() {
		if (vars == null) initVars();
		return path;
	}
	public boolean prefixes(C_Term t) {
		if (equals(t)) return true;
		if (! (t instanceof C_Var)) return false;
		C_Var[] vars = ((C_Var) t).vars();
		boolean result = false;
		for (int i=0; (! result) && i < vars.length; i++) {
			result = equals(vars[i]);
		}
		return result;
	}
	protected void initVars() {
		int count=0;
		C_Var source  = this;
		for (; source instanceof C_Field; source = ((C_Field) source).receiver())
			count++;
	//	Report.report(1, "C_Field_c: Count for " + this + " is " + count + ".");
		vars = new C_Var[count+1];
		C_Var f = this;
		for (int i =count; i >= 0; i--) {
		//	Report.report(1, "C_Field_c.initVars setting vars["+i+"] to " + f +  ".");
			vars[i]= f;
			if (i > 0) 
				f = ((C_Field) f).receiver();
		}
		
	}
	
}
