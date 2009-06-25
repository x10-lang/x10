/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.types.constr;

import java.util.HashSet;

import polyglot.types.LocalDef;
import polyglot.types.Type;

/**
 * @author vj
 *
 */
public class C_EQV_c extends C_Local_c implements C_EQV {

	/**
	 * @param li
	 */
	public C_EQV_c(LocalDef li) {
		super(li);
		// TODO Auto-generated constructor stub
	}
	
	protected C_EQV_c(Type t, LocalDef li, boolean isSelfVar) {
            super(t, li, isSelfVar);
        }
	public C_Term copy() {
            return new C_EQV_c(type, li, isSelfVar);
        }
        public C_Term substitute(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
            if (x.equals(this)) {
                return y;
            }
            else if (propagate) {
                return new C_EQV_c(substituteType(y, x, propagate, visited), li, isSelfVar);
            }
            return this;
        }
	
	/**
	 * @param li
	 * @param isSelfVar
	 */
	public C_EQV_c(LocalDef li, boolean isSelfVar) {
		this(li, isSelfVar, true);
	}
	public C_EQV_c(LocalDef li, boolean isSelfVar, boolean hidden) {
		super(li, isSelfVar);
		this.hidden = hidden;
		
	}
	
	boolean hidden;
	public boolean isEQV() { return hidden;}

}
