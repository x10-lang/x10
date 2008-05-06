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
package x10.constraint;


/**
 * @author vj
 *
 */
public class C_EQV_c extends C_Local_c implements C_EQV {
	boolean hidden;

	public C_EQV_c(C_Name name) {
		this(name, true);
	}
	
	public C_EQV_c(C_Name name, boolean hidden) {
		super(name);
		this.hidden = hidden;
	}
	
	public boolean isEQV() {
		return hidden;
	}
}
