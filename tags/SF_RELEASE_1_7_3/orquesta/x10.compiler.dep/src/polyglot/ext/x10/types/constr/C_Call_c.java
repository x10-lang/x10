/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import polyglot.ast.Binary;
import polyglot.ast.Variable;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.main.Report;
import polyglot.types.MethodInstance;
import polyglot.types.Type;

public class C_Call_c extends C_Term_c implements C_Call {

	public final C_Term receiver;
	public final List<C_Term> args;
	public final MethodInstance mi;
	public C_Call_c(C_Term receiver, List<C_Term> args, Type type, MethodInstance mi) {
		super(type);
		this.mi = mi;
		this.receiver = receiver;
		this.args = args;
	}
	public C_Call_c(C_Term receiver, List<C_Term> args, MethodInstance mi) {
		this(receiver, args, mi.returnType(), mi);
	}
	
	public C_Term copy() {
		List<C_Term> args2 = new ArrayList<C_Term>();
		for (C_Term t : args) {
			args2.add(t.copy());
		}
	    return new C_Call_c(receiver.copy(), args2, mi);
	}
	
	public C_Term substitute(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
		List<C_Term> args2 = new ArrayList<C_Term>();
		for (C_Term t : args) {
			args2.add(t.substitute(y, x, propagate, visited));
		}
	    return new C_Call_c(receiver.substitute(y, x, propagate, visited), args2, substituteType(y, x, propagate, visited), mi);
	}
	
        public void collectVars(List<C_Var> accum) {
        	receiver.collectVars(accum);
        	for (C_Term t : args) {
        		t.collectVars(accum);
        	}
        }

        public C_Term receiver() { return receiver; }
	public List<C_Term> args() { return args; }

	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    boolean first = true;
	    sb.append(receiver);
	    sb.append(".");
	    sb.append(mi.name());
	    sb.append("(");
	    for (C_Term t: args) {
	    	if (first) {
	    		first = false;
	    	}
	    	else {
	    		sb.append(", ");
	    	}
	    	sb.append(t);
	    }
	    sb.append(")");
	    return sb.toString();
	}
	
	public int hashCode() {
		int h = 41;
		for (C_Term t: args) {
			h += t.hashCode();
		}
		return h;
	}
	public boolean equals(Object o) {
		if (! (o instanceof C_Call_c)) return false;
		if (o == null) return false;
		C_Call_c other = (C_Call_c) o;
		return receiver.equals(other.receiver) && mi.equals(other.mi) && args.equals(other.args);
	}
	public Promise toPromise() {
		throw new Error("Not implemented yet.");
	}
	public boolean rootVarIsSelf() { return false;}
	public boolean rootVarIsThis() { return false;}
	public boolean isEQV() { return false;}
	public boolean prefixes(C_Term term) { return false;}
	public MethodInstance mi() {
		return mi;
	}
}
