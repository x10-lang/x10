/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 1, 2005
 *
 * 
 */
package x10.lang;

/**
 * @author vj Jan 1, 2005
 * 
 */
public /*valu*/ class nat extends Object implements ValueType {
	public static final nat one = new nat(1);
	public static final nat zero = new nat(0);
	public final long data;
	/**
	 * 
	 */
	public nat(long data ) {
		super();
		this.data = data;
	}

	public nat add(nat arg) {
		return new nat(data+arg.data);
	}

}
