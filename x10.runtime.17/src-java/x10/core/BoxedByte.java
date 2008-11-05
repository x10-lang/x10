/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedByte<T> extends Box<Byte> {
	public BoxedByte(byte v) {
		super(Types.BYTE, v);
	}
}