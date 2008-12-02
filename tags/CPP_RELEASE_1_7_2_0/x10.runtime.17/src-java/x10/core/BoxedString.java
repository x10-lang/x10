/**
 * 
 */
package x10.core;

import x10.types.RuntimeType;

public class BoxedString<T> extends Box<String> {
	public BoxedString(String v) {
		super(new RuntimeType<String>(String.class), v);
	}
	
	public Integer length() {
		return this.value.length();
	}
	
	public Character apply(Integer i) {
		return this.value.charAt(i);
	}
	
	public Character charAt(Integer i) {
		return this.value.charAt(i);
	}
    
	public String substring(Integer fromIndex, Integer toIndex) {
		return this.value.substring(fromIndex, toIndex);
	}
}