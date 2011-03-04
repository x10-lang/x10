/**
 * 
 */
package x10.core;

import x10.types.Types;

public class BoxedChar<T> extends Box<Character> {
	public BoxedChar(char v) {
		super(Types.CHAR, v);
	}
	
	public Boolean isLowerCase() {
		return java.lang.Character.isLowerCase(this.value);
	}
	public Boolean isUpperCase() {
		return java.lang.Character.isUpperCase(this.value);
	}
	public Boolean isTitleCase() {
		return java.lang.Character.isTitleCase(this.value);
	}
	public Boolean isDigit() {
		return java.lang.Character.isDigit(this.value);
	}
	public Boolean isLetter() {
		return java.lang.Character.isLetter(this.value);
	}
	public Boolean isLetterOrDigit() {
		return java.lang.Character.isLetterOrDigit(this.value);
	}
	public Boolean isUnicodeIdentifierStart() {
		return java.lang.Character.isUnicodeIdentifierStart(this.value);
	}
	public Boolean isUnicodeIdentifierPart() {
		return java.lang.Character.isUnicodeIdentifierPart(this.value);
	}
	public Boolean isIdentifierIgnorable() {
		return java.lang.Character.isIdentifierIgnorable(this.value);
	}
	public Boolean isSpace() {
		return java.lang.Character.isSpace(this.value);
	}
	public Boolean isSpaceChar() {
		return java.lang.Character.isSpaceChar(this.value);
	}
	public Boolean isWhitespace() {
		return java.lang.Character.isWhitespace(this.value);
	}
	public Boolean isISOControl() {
		return java.lang.Character.isISOControl(this.value);
	}
	public Character toLowerCase() {
		return java.lang.Character.toLowerCase(this.value);
	}
	public Character toUpperCase() {
		return java.lang.Character.toUpperCase(this.value);
	}
	public Character toTitleCase() {
		return java.lang.Character.toTitleCase(this.value);
	}
	public Integer ord() {
		return (Integer) ((int) this.value);
	}
	public Character reverseBytes() {
		return java.lang.Character.reverseBytes(this.value);
	}
}