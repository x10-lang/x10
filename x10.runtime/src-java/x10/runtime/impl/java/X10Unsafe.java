package x10.runtime.impl.java;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class X10Unsafe {
	
    private static Unsafe unsafe = getUnsafe();
    
    private static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            Class<Unsafe> uc = Unsafe.class;
            Field[] fields = uc.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals("theUnsafe")) {
                    fields[i].setAccessible(true);
                    unsafe = (Unsafe) fields[i].get(uc);
                    break;
                }
            }
        } catch (Exception ignore) {
        }
        return unsafe;
    }

	public static Object allocateInstance(Class<?> clazz) throws InstantiationException {
    	return unsafe.allocateInstance(clazz);
    }
	
	public static void throwException(Throwable e) {
            unsafe.throwException(e);
	}
}
