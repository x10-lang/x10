package x10.runtime;

public interface Type {
	boolean isSubtypeOf(Type putativeSupertype);
	
	boolean isBoolean();
	boolean isByte();
	boolean isShort();
	boolean isChar();
	boolean isInt();
	boolean isLong();
	boolean isFloat();
	boolean isDouble();
	boolean isRef();
	boolean isValue();
}
