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

	public static boolean instanceof$(boolean r, Class c) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e, Class f) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e, Class f, Class g) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e, Class f, Class g, Class h) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e, Class f, Class g, Class h, Class i) { return r; }
	public static Object cast$(Object o, Class c) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e, Class f) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e, Class f, Class g) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e, Class f, Class g, Class h) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e, Class f, Class g, Class h, Class i) { return o; }
}
