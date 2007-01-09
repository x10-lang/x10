/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import polyglot.ast.Variable;
import polyglot.main.Report;
import polyglot.types.Type;

public class C_BinaryTerm_c extends C_Term_c implements C_BinaryTerm {

	public final C_Term left;
	public final C_Term right;
	public final String op;
	public C_BinaryTerm_c(String op, C_Term left, C_Term right, Type type) {
		super(type);
		this.left = left;
		this.right = right;
		this.op = op;
	}

	public String op() { return op;}
	public C_Term left() { return left; }
	public C_Term right() { return right;}
	public String toString() { return left.toString() + " " + op + " " + right.toString();}
	
	public int hashCode() {
		return ((left == null) ? 0 : left.hashCode()) 
		+ (right==null ?  0 : right.hashCode())
		+ (op==null ? 0 : op.hashCode());
	}
	public boolean equals(Object o) {
		if (! (o instanceof C_BinaryTerm_c)) return false;
		if (o == null) return false;
		C_BinaryTerm_c other = (C_BinaryTerm_c) o;
		return left.equals(other.left) && right.equals(other.right) && op.equals(other.op);
	}
	public Promise toPromise() {
		throw new Error("Not implemented yet.");
	}
	public boolean rootVarIsSelf() { return false;}
	public boolean rootVarIsThis() { return false;}
	public boolean isEQV() { return false;}
	public boolean prefixes(C_Term term) { return false;}
}
