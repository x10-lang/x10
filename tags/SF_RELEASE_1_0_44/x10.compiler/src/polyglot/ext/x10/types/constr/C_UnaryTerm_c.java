/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import polyglot.types.Type;

public class C_UnaryTerm_c extends C_Term_c implements C_UnaryTerm {

	public final String op;
	public final C_Term arg;
	public C_UnaryTerm_c(String op, C_Term arg, Type type) {
		super(type);
		this.arg = arg;
		this.op = op;
	}
	public String op() { return op;}
	public C_Term arg() { return arg;}
	
	public String toString() { return op + " " + arg.toString();}
	public int hashCode() {
		return ((op == null) ? 0 : op.hashCode()) + (arg==null ?  0 : arg.hashCode());
	}
	public boolean equals(Object o) {
		if (this==o) return true;
		if (! (o instanceof C_UnaryTerm_c)) return false;
		C_UnaryTerm_c other = (C_UnaryTerm_c) o;
		return op.equals(other.op) && arg.equals(other.arg);
	}
	public Promise toPromise() {
		throw new Error("Not implemented yet.");
	}
	public boolean rootVarIsSelf() { return false;}
	public boolean rootVarIsThis() { return false;}
	public boolean isEQV() { return false;}
	public boolean prefixes(C_Term t) { return false;}
}
