/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedInt<T> extends Box<Integer> {
	public BoxedInt(int v) {
		super(Types.INT, v);
	}
}