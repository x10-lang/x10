package msat.core;

public class Boolean {
	public static final Boolean TRUE = new Boolean((char) 1), 
		FALSE = new Boolean((char)-1),
		UNDEF = new Boolean((char) 0);
	
	char value;
	public Boolean(char v) { value = v;}
	public Boolean(boolean x) { value = (char) ((x?1:0)*2-1);}
	public boolean equals(Object o) {
		if (! (o instanceof Boolean)) return false;
		Boolean other = (Boolean) o;
		return value == other.value;
	}
	// check
	public Boolean not(boolean b) { 
		return new Boolean(b? (char) -value : value);
	}
}
