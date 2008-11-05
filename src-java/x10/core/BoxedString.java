/**
 * 
 */
package x10.core;

import x10.types.RuntimeType;

public class BoxedString<T> extends Box<String> {
	public BoxedString(String v) {
		super(new RuntimeType<String>(String.class), v);
	}
}