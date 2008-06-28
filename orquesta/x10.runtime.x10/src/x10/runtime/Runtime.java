package x10.runtime;

import java.lang.reflect.InvocationTargetException;

public class Runtime {
	public static void main(String[] args) {
		X10RuntimeClassloader cl = new X10RuntimeClassloader();
		try {
			cl.loadClass("x10.lang.Runtime").getMethod("main", String[].class).invoke(null, (Object)args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static Object coerce(byte b) { return null; }
	public static Object coerce(char c) { return null; }
	public static Object coerce(double d) { return null; }
	public static Object coerce(float f) { return null; }
	public static Object coerce(int i) { return null; }
	public static Object coerce(long l) { return null; }
	public static Object coerce(short s) { return null; }
	public static Object coerce(boolean b) { return null; }
	public static Object coerce(Object o) { return null; }
}
