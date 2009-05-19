/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedFloat<T> extends Box<Float> {
	public BoxedFloat(float v) {
		super(Types.FLOAT, v);
	}
	
    public String toHexString() {
    	return java.lang.Float.toHexString(this.value);
    }
    public String toString() {
    	return java.lang.Float.toString(this.value);
    }
    public Boolean isNaN() {
    	return java.lang.Float.isNaN(this.value);
    }
    public Boolean isInfinite() {
    	return java.lang.Float.isInfinite(this.value);
    }
        
    public Integer toIntBits() {
    	return java.lang.Float.floatToIntBits(this.value);
    }
    public Integer toRawIntBits() {
    	return java.lang.Float.floatToRawIntBits(this.value);
    }
}