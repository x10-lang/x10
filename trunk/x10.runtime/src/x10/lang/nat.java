/*
 * Created by vj on Jan 1, 2005
 *
 * 
 */
package x10.lang;

import x10.base.TypeArgument;

/**
 * @author vj Jan 1, 2005
 * 
 */
public /*valu*/ class nat extends Object {
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
