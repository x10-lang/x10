import java.lang.reflect.InvocationTargetException;

import x10.generics.Parameters;
import x10.generics.Synthetic;
import x10.generics.ParametricMethod;
import x10.generics.InstantiateClass;
import x10.runtime.X10RuntimeClassloader;
import x10.runtime.Runtime;

public class Math implements Runnable {
	// static def A first[A,B extends A](A x, B y) = { x; }
	@Synthetic
	@Parameters({"A","B"})
	public static class first {
		public first() { }
		@Synthetic public first(Class A, Class B) { this(); }
		public static class A { };
		public static class B { };
		public A apply(A x, B y) { System.out.println("Math::first("+x+","+y+")"); return x; }
		@Synthetic
		public A apply(Class A, Class B, A x, B y) { return apply(x, y); } // We might only need one
	}
	@Synthetic
	@ParametricMethod({"A","B"})
	public static Object make$first(Class A, Class B) { assert(false); return null; }
	@Synthetic
	public static Object make$first(Class A, Class B, boolean ignored) {
		Object retval = null;
		try {
			X10RuntimeClassloader cl = (X10RuntimeClassloader)Math.class.getClassLoader();
			//Class<?> c = cl.getClass("first$$"+A.getName()+"$$"+B.getName());
			Class<?> c = cl.instantiate(first.class, A, B);
			retval = c.getDeclaredConstructor(new Class[] { }).newInstance();
		}
		catch (IllegalAccessException e) { }
		catch (NoSuchMethodException e) { }
		catch (InstantiationException e) { }
		catch (InvocationTargetException e) { }
		return retval;
	}
	// def T second[T](T x, T y) = { y; }
	@Synthetic
	@Parameters("T")
	public static class second {
		public Math m;
		public second(Math m) { this.m = m; }
		@Synthetic public second(Class T, Math m) { this(m); }
		public static class T { };
		public T apply(T x, T y) { System.out.println("Math::second("+x+","+y+")"); return y; }
		@Synthetic
		public T apply(Class T, T x, T y) { return apply(x, y); } // We might only need one
	}
	static Class last$second$T; // 1-element cache
	static Object last$second;  // 1-element cache
	@Synthetic
	@ParametricMethod("T")
	public Object make$second(Class T) { assert(false); return null; }
	@Synthetic
	public Object make$second(Class T, boolean ignored) {
		if (T == last$second$T) return last$second;
		last$second$T = T;
		try {
			X10RuntimeClassloader cl = (X10RuntimeClassloader)Math.class.getClassLoader();
			//Class<?> c = cl.getClass("second$$"+T.getName());
			Class<?> c = cl.instantiate(second.class, T);
			last$second = c.getDeclaredConstructor(new Class[] { Math.class }).newInstance(this);
		}
		catch (IllegalAccessException e) { }
		catch (NoSuchMethodException e) { }
		catch (InstantiationException e) { }
		catch (InvocationTargetException e) { }
		return last$second;
	}
	public void run() {
		// Look what happened to an innocent Math.first(3, 4)
		((Math.first)(Object)(Math.first)Math.make$first(int.class, int.class)).apply(int.class, int.class, (Math.first.A)Runtime.coerce((int)3), (Math.first.B)Runtime.coerce((int)4));
		// Rewritten to
		//Math.make$first(int.class, int.class, true).apply(3, 4)

		Math m = new Math();
		// Look what happened to an innocent new m.second(3, 4)
		((Math.second)(Object)(Math.second)m.make$second(int.class)).apply(int.class, (Math.second.T)Runtime.coerce((int)3), (Math.second.T)Runtime.coerce((int)4));
		// Rewritten to
		//m.make$second(int.class, true).apply(3, 4)

		m = new BadMath();
		// Look what happened to an innocent new m.second(3, 4)
		((Math.second)(Object)(Math.second)m.make$second(int.class)).apply(int.class, (Math.second.T)Runtime.coerce((int)3), (Math.second.T)Runtime.coerce((int)4));
		// Rewritten to
		//m.make$second(int.class, true).apply(3, 4)
	}
}
class BadMath extends Math {
	// def T second[T](T x, T y) = { x; }
	@Synthetic
	@InstantiateClass("BadMath$second$T")
	@Parameters("T")
	public static class second extends Math.second/*[T]*/ {
		public second(BadMath m) { super(m); }
		@Synthetic
		public second(Class T, BadMath m) { this(m); }
		public static class T { };
		public T apply(T x, T y) { System.out.println("BadMath::second("+x+","+y+")"); return x; }
		@Synthetic
		public T apply(Class T, T x, T y) { return apply(x, y); } // We might only need one
	}
	@Synthetic
	@ParametricMethod("T")
	public Object make$second(Class T) { assert(false); return null; }
	@Synthetic
	public Object make$second(Class T, boolean ignored) {
		Object retval = null;
		try {
			X10RuntimeClassloader cl = (X10RuntimeClassloader)BadMath.class.getClassLoader();
			//Class<?> c = cl.getClass("second$$"+T.getName());
			Class<?> c = cl.instantiate(second.class, T);
			retval = c.getDeclaredConstructor(new Class[] { BadMath.class }).newInstance(this);
		}
		catch (IllegalAccessException e) { }
		catch (NoSuchMethodException e) { }
		catch (InstantiationException e) { }
		catch (InvocationTargetException e) { }
		return retval;
	}
	// static class foo extends List[int] { }
}
