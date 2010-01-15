/**
 * 
 */
package x10.core;

import x10.rtt.Types;

public class BoxedDouble<T> extends Box<Double> {
	public BoxedDouble(double v) {
		super(Types.DOUBLE, v);
	}
	
    public String toHexString() {
    	return java.lang.Double.toHexString(this.value);
    }
    public String toString() {
    	return java.lang.Double.toString(this.value);
    }
    public Boolean isNaN() {
    	return java.lang.Double.isNaN(this.value);
    }
    public Boolean isInfinite() {
    	return java.lang.Double.isInfinite(this.value);
    }
        
    public Long toIntBits() {
    	return java.lang.Double.doubleToLongBits(this.value);
    }
    public Long toRawIntBits() {
    	return java.lang.Double.doubleToRawLongBits(this.value);
    }

}