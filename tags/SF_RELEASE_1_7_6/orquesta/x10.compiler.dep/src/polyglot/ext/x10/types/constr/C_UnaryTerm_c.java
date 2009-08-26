/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import polyglot.types.Type;

public class C_UnaryTerm_c extends C_Term_c implements C_UnaryTerm {

	public final String op;
	public final C_Term arg;
	public C_UnaryTerm_c(String op, C_Term arg, Type type) {
		super(type);
		this.arg = arg;
		this.op = op;
		assert arg.type() != null;
		assert type != null;
	}
	public String op() { return op;}
	public C_Term arg() { return arg;}
	
	public C_Term copy() {
            return new C_UnaryTerm_c(op, arg.copy(), type);
        }
        
        public C_Term substitute(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
            return new C_UnaryTerm_c(op, arg.substitute(y, x, propagate, visited), substituteType(y, x, propagate, visited));
        }
        
	public void collectVars(List<C_Var> accum) {
	    arg.collectVars(accum);
        }
        
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(op);
	    sb.append(" ");
	    if (!(arg instanceof C_Var))
	        sb.append("(");
	    sb.append(arg.toString());
	    if (!(arg instanceof C_Var))
	        sb.append(")");
	    return sb.toString();
	}

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
