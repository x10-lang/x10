package x10.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Runtime {
        public static Runtime runtime;
        
	public static void main(String[] args) {
	    // TEMPORARY: just make the field non-null.  We never use it.  Modify Main.xcd appropriately later.
	        runtime = new Runtime();
		
	        if (args.length == 0) {
	            System.err.println("usage: java x10.runtime.Runtime MainClass [args]");
	            System.exit(1);
	        }
	        
	        String klass = args[0];
	        String[] newArgs = new String[args.length-1];
	        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
	        
	        X10RuntimeClassloader cl = new X10RuntimeClassloader();
		try {
			Class<?> k = cl.loadClass(klass);
			Method m = k.getMethod("main", String[].class);
			m.setAccessible(true);
			m.invoke(null, (Object)newArgs);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
//	        X10RuntimeClassloader cl = new X10RuntimeClassloader();
//		try {
//			cl.loadClass("x10.lang.Runtime").getMethod("main", String[].class).invoke(null, (Object)args);
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
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
	public static byte recoverB(Object o) { return 0; }
	public static char recoverC(Object o) { return 0; }
	public static double recoverD(Object o) { return 0; }
	public static float recoverF(Object o) { return 0; }
	public static int recoverI(Object o) { return 0; }
	public static long recoverJ(Object o) { return 0; }
	public static short recoverS(Object o) { return 0; }
	public static boolean recoverZ(Object o) { return false; }
	public static Object recoverL(Object o) { return null; }

	public static boolean instanceof$(boolean r, Class c) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e, Class f) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e, Class f, Class g) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e, Class f, Class g, Class h) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e, Class f, Class g, Class h, Class i) { return r; }
	public static boolean instanceof$(boolean r, Class c, Class d, Class e, Class f, Class g, Class h, Class i, Class... j) { return r; }
	public static Object cast$(Object o, Class c) { return o; }
	public static Object cast$(Object o, Class c, Class d) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e, Class f) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e, Class f, Class g) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e, Class f, Class g, Class h) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e, Class f, Class g, Class h, Class i) { return o; }
	public static Object cast$(Object o, Class c, Class d, Class e, Class f, Class g, Class h, Class i, Class... j) { return o; }

	public static Object newarray$(Object o, Class c) { return o; }
	public static Object newarray$(Object o, Class c, Class d) { return o; }
	public static Object newarray$(Object o, Class c, Class d, Class e) { return o; }
	public static Object newarray$(Object o, Class c, Class d, Class e, Class f) { return o; }
	public static Object newarray$(Object o, Class c, Class d, Class e, Class f, Class g) { return o; }
	public static Object newarray$(Object o, Class c, Class d, Class e, Class f, Class g, Class h) { return o; }
	public static Object newarray$(Object o, Class c, Class d, Class e, Class f, Class g, Class h, Class i) { return o; }
	public static Object newarray$(Object o, Class c, Class d, Class e, Class f, Class g, Class h, Class i, Class... j) { return o; }
	
	public static boolean equalsequals(boolean a, boolean b) { return a == b; }
	public static boolean equalsequals(byte a, byte b) { return a == b; }
	public static boolean equalsequals(short a, short b) { return a == b; }
	public static boolean equalsequals(char a, char b) { return a == b; }
	public static boolean equalsequals(int a, int b) { return a == b; }
	public static boolean equalsequals(long a, long b) { return a == b; }
	public static boolean equalsequals(float a, float b) { return a == b; }
	public static boolean equalsequals(double a, double b) { return a == b; }
	public static boolean equalsequals(Object a, Object b) {
	    if (a == null) return b == null;
	    if (b == null) return false;
	    if (a instanceof Ref) return a == b;
	    if (b instanceof Ref) return false;
	    if (a instanceof Value) return a.equals(b);
	    return false;
	}
	
	public static Type runtimeType(Class c) {
	    return new XType(c);
	}
	
	public static class XType implements Type {
	    Class c;
	    XType(Class c) { this.c = c; }
	    public boolean isBoolean() {
	        return c == Boolean.TYPE;
	    }
	    public boolean isByte() {
	        return c == Byte.TYPE;
	    }
	    public boolean isShort() {
	        return c == Short.TYPE;
	    }
	    public boolean isChar() {
	        return c == Character.TYPE;
	    }
	    public boolean isInt() {
	        return c == Integer.TYPE;
	    }
	    public boolean isLong() {
	        return c == Long.TYPE;
	    }
	    public boolean isFloat() {
	        return c == Float.TYPE;
	    }
	    public boolean isDouble() {
	        return c == Double.TYPE;
	    }
	    public boolean isRef() {
	        return x10.runtime.Ref.class.isAssignableFrom(c);
	    }
	    public boolean isValue() {
	        return x10.runtime.Value.class.isAssignableFrom(c) || c == java.lang.String.class || isBoolean() || isByte() || isShort() || isChar() || isInt() || isLong() || isFloat() || isDouble();
	    }
	    public boolean isSubtypeOf(Type putativeSupertype) {
	        if (putativeSupertype instanceof XType) {
	            XType that = (XType) putativeSupertype;
	            return that.c.isAssignableFrom(c);
	        }
	        return false;
	    }
	}
	
	public static Object location(Object o) { return null; }
	public static Object here() { return null; }
	public static Object hereCheck(Object o) { return o; }
	
	public static <T> ValRail<T> makeValRail(Type T, int length, Fun_0_1<Integer,T> init) {
	    ValRail<T> array = new ValRail<T>(T, length);
	    for (int i = 0; i < length; i++) {
	        array.set(i, init.apply(i));
	    }
	    return array;
	}
	
	public static <T> Rail<T> makeVarRail(Type T, int length, Fun_0_1<Integer,T> init) {
            Rail<T> array = new Rail<T>(T, length);
            for (int i = 0; i < length; i++) {
                array.set(i, init.apply(i));
            }
            return array;
	}
	
	public static <T> Rail<T> makeRailFromJavaArray(Object array) {
	    if (array instanceof boolean[]) {
	        return new Rail<T>(new XType(Boolean.TYPE), ((boolean[]) array).length, array);
	    }
	    if (array instanceof byte[]) {
	        return new Rail<T>(new XType(Byte.TYPE), ((byte[]) array).length, array);
	    }
	    if (array instanceof short[]) {
	        return new Rail<T>(new XType(Short.TYPE), ((short[]) array).length, array);
	    }
	    if (array instanceof char[]) {
	        return new Rail<T>(new XType(Character.TYPE), ((char[]) array).length, array);
	    }
	    if (array instanceof int[]) {
	        return new Rail<T>(new XType(Integer.TYPE), ((int[]) array).length, array);
	    }
	    if (array instanceof long[]) {
	        return new Rail<T>(new XType(Long.TYPE), ((long[]) array).length, array);
	    }
	    if (array instanceof float[]) {
	        return new Rail<T>(new XType(Float.TYPE), ((float[]) array).length, array);
	    }
	    if (array instanceof double[]) {
	        return new Rail<T>(new XType(Double.TYPE), ((double[]) array).length, array);
	    }
	    if (array instanceof String[]) {
	        return new Rail<T>(new XType(String.class), ((String[]) array).length, array);
	    }
            return new Rail<T>(new XType(array.getClass().getComponentType()), ((Object[]) array).length, array);
	}
}
