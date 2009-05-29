/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.List;

import polyglot.types.Type;

public abstract class C_Var_c extends C_Term_c implements C_Var {

	final boolean rootVarIsThis;
	final boolean rootVarIsSelf;
	public C_Var_c(Type t) {
		this(t, false, false);
		
	}
	public C_Var_c(Type type, boolean t, boolean s) {
		super(type);
		rootVarIsThis = t;
		rootVarIsSelf = s;
	}
	public boolean rootVarIsThis() {
		return rootVarIsThis;
	}
	public boolean rootVarIsSelf() {
		return rootVarIsSelf;
	}
	public C_Var substitute(C_Var value, C_Var var) {
		 return equals(var) ? value : this;
		}
	
	public void collectVars(List<C_Var> accum) {
	    accum.add(this);
	}

}
