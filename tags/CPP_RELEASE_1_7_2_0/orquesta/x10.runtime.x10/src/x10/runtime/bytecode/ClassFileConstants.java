package x10.runtime.bytecode;

public interface ClassFileConstants {
	public static final int CONSTANT_Class = 7;
	public static final int CONSTANT_Fieldref = 9;
	public static final int CONSTANT_Methodref = 10;
	public static final int CONSTANT_InterfaceMethodref = 11;
	public static final int CONSTANT_String = 8;
	public static final int CONSTANT_Integer = 3;
	public static final int CONSTANT_Float = 4;
	public static final int CONSTANT_Long = 5;
	public static final int CONSTANT_Double = 6;
	public static final int CONSTANT_NameAndType = 12;
	public static final int CONSTANT_Utf8 = 1;

	public static final String INIT = "<init>";

	public static final int ACC_STATIC = 0x0008;
}
