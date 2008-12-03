/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedShort<T> extends Box<Short> {
	public BoxedShort(short v) {
		super(Types.SHORT, v);
	}
	
	public String toString(Integer radix) {
		return java.lang.Integer.toString(this.value, radix);
	}
	
	public String toHexString() {
		return java.lang.Integer.toHexString(this.value);
	}
	
	public String toOctalString() {
		return java.lang.Integer.toOctalString(this.value);
	}
	
	public String toBinaryString() {
		return java.lang.Integer.toBinaryString(this.value);
	}
	
	public String toString() {
		return java.lang.Short.toString(this.value);
	}

	public Short reverseBytes() {
		return java.lang.Short.reverseBytes(this.value);
	}
}