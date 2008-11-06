/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedByte<T> extends Box<Byte> {
	public BoxedByte(byte v) {
		super(Types.BYTE, v);
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
		return java.lang.Byte.toString(this.value);
	}
}