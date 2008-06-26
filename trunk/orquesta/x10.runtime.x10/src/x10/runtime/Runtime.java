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
}
