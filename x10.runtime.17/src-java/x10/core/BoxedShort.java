/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedShort<T> extends Box<Short> {
	public BoxedShort(short v) {
		super(Types.SHORT, v);
	}
}