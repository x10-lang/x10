package x10.runtime.util;

public abstract class Util {
	// Helper function to convert any expression to valid Java statement by swalloing its value
	// Fast implementation without boxing primitives
    public static void eval(Object expr) {}
    public static void eval(boolean expr) {}
    public static void eval(char expr) {}
    public static void eval(byte expr) {}
    public static void eval(short expr) {}
    public static void eval(int expr) {}
    public static void eval(long expr) {}
    public static void eval(float expr) {}
    public static void eval(double expr) {}
}
