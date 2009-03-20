/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;


/**
 * @author vj
 *
 */
public class XEQV_c extends XLocal_c implements XEQV {
	boolean hidden;

	public XEQV_c(XName name) {
		this(name, true);
	}
	
	public XEQV_c(XName name, boolean hidden) {
		super(name);
		this.hidden = hidden;
	}
	
	public boolean isEQV() {
		return hidden;
	}
}
