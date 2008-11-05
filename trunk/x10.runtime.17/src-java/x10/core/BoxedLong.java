/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedLong<T> extends Box<Long> {
	public BoxedLong(long v) {
		super(Types.LONG, v);
	}
}