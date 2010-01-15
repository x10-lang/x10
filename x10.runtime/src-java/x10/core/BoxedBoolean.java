/**
 * 
 */
package x10.core;

import x10.rtt.Types;

public class BoxedBoolean<T> extends Box<Boolean> {
	public BoxedBoolean(boolean v) {
		super(Types.BOOLEAN, v);
	}
}