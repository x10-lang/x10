package x10.constraint.bapat;

/**
 * An "int option" type.
 */
public class IntOption {
	
	private boolean hasIntValue;
	private int value;
	
	private IntOption(boolean iV, int v) {
		hasIntValue = iV;
		value = v;
	}
	
	private static final IntOption noIntValue = new IntOption(false, 0);
	
	public static IntOption noIntValue() {
		return noIntValue;
	}
	
	public static IntOption intValue(int v) {
		return new IntOption(true, v);
	}
	
	public boolean hasIntValue() {
		return hasIntValue;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		if (hasIntValue)
			return value + "";
		else
			return "none";
	}

}
