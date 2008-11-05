/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedChar<T> extends Box<Character> {
	public BoxedChar(char v) {
		super(Types.CHAR, v);
	}
}