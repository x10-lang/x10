/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedLong<T> extends Box<Long> {
	public BoxedLong(long v) {
		super(Types.LONG, v);
	}

	public String toString(Integer radix) {
		return Long.toString(this.value, radix);
	}
	
	public String toHexString() {
		return Long.toHexString(this.value);
	}
	
	public String toOctalString() {
		return Long.toOctalString(this.value);
	}
	
	public String toBinaryString() {
		return Long.toBinaryString(this.value);
	}
	
	public String toString() {
		return Long.toString(this.value);
	}
	
	public Long highestOneBit() {
		return Long.highestOneBit(this.value);
	}
	
	public Long lowestOneBit() {
		return Long.lowestOneBit(this.value);
	}

	public Integer numberOfLeadingZeros() {
		return Long.numberOfLeadingZeros(this.value);
	}
	
	public Integer numberOfTrailingZeros() {
		return Long.numberOfTrailingZeros(this.value);
	}

	public Integer bitCount() {
		return Long.bitCount(this.value);
	}
	
	public Long rotateLeft(Integer bits) {
		return Long.rotateLeft(this.value, bits);
	}
	
	public Long rotateRight(Integer bits) {
		return Long.rotateRight(this.value, bits);
	}
	
	public Long reverse() {
		return Long.reverse(this.value);
	}
	
	public Integer signum() {
		return Long.signum(this.value);
	}
	
	public Long reverseBytes() {
		return Long.reverseBytes(this.value);
	}

}