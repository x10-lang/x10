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
import polyglot.types.Type;

public class C_Region_c extends C_Term_c implements C_Region {

	public final List<C_Term> args;
	public C_Region_c(List<C_Term> args, Type type) {
		super(type);
		this.args = args;
	}
	
	public C_Term copy() {
		List<C_Term> args2 = new ArrayList<C_Term>();
		for (C_Term t : args) {
			args2.add(t.copy());
		}
	    return new C_Region_c(args2, type);
	}
	
	public C_Term substitute(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
		List<C_Term> args2 = new ArrayList<C_Term>();
		for (C_Term t : args) {
			args2.add(t.substitute(y, x, propagate, visited));
		}
	    return new C_Region_c(args2, substituteType(y, x, propagate, visited));
	}
	
        public void collectVars(List<C_Var> accum) {
        	for (C_Term t : args) {
        		t.collectVars(accum);
        	}
        }

	public List<C_Term> args() { return args; }

	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    boolean first = true;
	    for (C_Term t: args) {
	    	if (first) {
	    		first = false;
	    	}
	    	else {
	    		if (t.type().isIntOrLess()) {
	    			sb.append(":");
	    		}
	    		else {
	    			sb.append(", ");
	    		}
	    	}
	    	sb.append(t);
	    }
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
		if (! (o instanceof C_Region_c)) return false;
		if (o == null) return false;
		C_Region_c other = (C_Region_c) o;
		return args.equals(other.args);
	}
	public Promise toPromise() {
		throw new Error("Not implemented yet.");
	}
	public boolean rootVarIsSelf() { return false;}
	public boolean rootVarIsThis() { return false;}
	public boolean isEQV() { return false;}
	public boolean prefixes(C_Term term) { return false;}
}
