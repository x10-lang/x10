/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedFloat<T> extends Box<Float> {
	public BoxedFloat(float v) {
		super(Types.FLOAT, v);
	}
}