/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedDouble<T> extends Box<Double> {
	public BoxedDouble(double v) {
		super(Types.DOUBLE, v);
	}
}