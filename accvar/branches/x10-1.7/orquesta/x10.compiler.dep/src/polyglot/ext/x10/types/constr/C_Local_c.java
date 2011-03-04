/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.HashSet;
import java.util.List;

import polyglot.ext.x10.types.X10LocalDef;
import polyglot.types.LocalDef;
import polyglot.types.Type;

/**
 * The representation of a local variable reference.
 * @author vj
 *
 */
public class C_Local_c extends C_Var_c implements C_Local {
	public final LocalDef li;
	public boolean isSelfVar;
	public C_Local_c(LocalDef li) {
		this(li, false);
	}
	public C_Local_c(LocalDef li, boolean isSelfVar) {
		this(li.type().get(), li, isSelfVar);
	}
	protected C_Local_c(Type t, LocalDef li, boolean isSelfVar) {
	    super(t, false, false);
	    this.li = li;
	    this.isSelfVar=isSelfVar;
	}
	public C_Term copy() {
	    return new C_Local_c(type, li, isSelfVar);
	}
	public C_Term substitute(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
            if (x.equals(this)) {
                return y;
            }
            else if (propagate) {
                return new C_Local_c(substituteType(y, x, propagate, visited), li, isSelfVar);
            }
            return this;
        }
	public static final C_Local makeSelfVar(LocalDef li) {
		return new C_Local_c(li, true);
	}
	public X10LocalDef localDef() {
		return (X10LocalDef) li;
	}
	protected Path path = new Path(new String[0]);
	public Path path() {
		return path;
	}
	protected C_Var[] vars = new C_Var[] { this };
	public C_Var[] vars() { return vars; }
	public C_Var rootVar() { return this; }
	public boolean isEQV() { return false;}
	public int hashCode() {
		String name = name();
		return ((name == null) ? 0 : name.hashCode()) + (li==null ?  0 : li.hashCode());
	}
	public boolean equals(Object o) {
		
		if (o==null) return false;
	//	Report.report(1, "C_Local_c: " + this + " equals? " +  o + o.getClass());
		if (this==o) return true;
		if (! (o instanceof C_Local_c)) 
			return false;
		C_Local_c other = (C_Local_c) o;
		
		boolean val = // isSelfVar? other.isSelfVar && li.toString().equals(other.toString()) 
			    name().equals(other.name())
				&& li.typeSystem().equals(li, other.li);
		return val;
	}
	
 	public boolean isSelfVar() { return isSelfVar;}
	public String name() { return li.name();}
	public String toString() { return  name();}
	public boolean prefixes(C_Term t) {
		if (equals(t)) return true;
		if (! (t instanceof C_Var)) return false;
		C_Term[] vars = ((C_Var) t).vars();
		
		return vars.length > 0 && equals(vars[0]);
	}
}
