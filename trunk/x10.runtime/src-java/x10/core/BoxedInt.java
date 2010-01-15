/**
 * 
 */
package x10.core;

import x10.rtt.Types;

public class BoxedInt<T> extends Box<Integer> {
	public BoxedInt(int v) {
		super(Types.INT, v);
	}

	public String toString(Integer radix) {
		return Integer.toString(this.value, radix);
	}
	
	public String toHexString() {
		return Integer.toHexString(this.value);
	}
	
	public String toOctalString() {
		return Integer.toOctalString(this.value);
	}
	
	public String toBinaryString() {
		return Integer.toBinaryString(this.value);
	}
	
	public String toString() {
		return Integer.toString(this.value);
	}
	
	public Integer highestOneBit() {
		return Integer.highestOneBit(this.value);
	}
	
	public Integer lowestOneBit() {
		return Integer.lowestOneBit(this.value);
	}

	public Integer numberOfLeadingZeros() {
		return Integer.numberOfLeadingZeros(this.value);
	}
	
	public Integer numberOfTrailingZeros() {
		return Integer.numberOfTrailingZeros(this.value);
	}

	public Integer bitCount() {
		return Integer.bitCount(this.value);
	}
	
	public Integer rotateLeft(Integer bits) {
		return Integer.rotateLeft(this.value, bits);
	}
	
	public Integer rotateRight(Integer bits) {
		return Integer.rotateRight(this.value, bits);
	}
	
	public Integer reverse() {
		return Integer.reverse(this.value);
	}
	
	public Integer signum() {
		return Integer.signum(this.value);
	}
	
	public Integer reverseBytes() {
		return Integer.reverseBytes(this.value);
	}
}