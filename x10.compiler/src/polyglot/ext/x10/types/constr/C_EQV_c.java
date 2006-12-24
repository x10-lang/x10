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

import polyglot.types.LocalInstance;

/**
 * @author vj
 *
 */
public class C_EQV_c extends C_Local_c implements C_EQV {

	/**
	 * @param li
	 */
	public C_EQV_c(LocalInstance li) {
		super(li);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param li
	 * @param isSelfVar
	 */
	public C_EQV_c(LocalInstance li, boolean isSelfVar) {
		this(li, isSelfVar, true);
	}
	public C_EQV_c(LocalInstance li, boolean isSelfVar, boolean hidden) {
		super(li, isSelfVar);
		this.hidden = hidden;
		
	}
	
	boolean hidden;
	public boolean isEQV() { return hidden;}

}
