package polyglot.dispatch;

import java.lang.reflect.*;
import java.util.*;

import polyglot.util.InternalCompilerError;

public class Dispatch {
    /**
     * Create a dynamic multiple dispatch proxy object. T must be an interface
     * type.
     */
    public static <T> T proxy(Class<? super T> c, T target) {
	return (T) java.lang.reflect.Proxy.newProxyInstance(c.getClassLoader(), new Class[] { c }, new DispatchProxy(target));
    }

    public static <T> T proxy(T target) {
	Object p = null;
	// cache.get(target);
	if (p == null) {
	    Class<?> c = target.getClass();
	    ArrayList<Class<?>> interfaces = new ArrayList<Class<?>>();
	    for (Class<?> s = c; s != null; s = s.getSuperclass()) {
		for (Class<?> ci : s.getInterfaces()) {
		    interfaces.add(ci);
		}
	    }
	    p = java.lang.reflect.Proxy.newProxyInstance(c.getClassLoader(), interfaces.toArray(new Class[0]), new DispatchProxy(target));
	    // cache.put(p, target);
	}
	return (T) p;
    }

    static class DispatchProxy<T> implements InvocationHandler {
	T target;

	DispatchProxy(T target) {
	    this.target = target;
	}

	static private boolean compatible(Class[] actuals, Class[] formals) {
	    if (actuals.length != formals.length)
		return false;
	    for (int i = 0; i < actuals.length; i++) {
		if (actuals[i] == null) {
		    if (formals[i].isPrimitive())
			return false;
		}
		else if (!formals[i].isAssignableFrom(actuals[i]))
		    return false;
	    }
	    return true;
	}

	static private void findMethods(Class<?> c, String name, Class retType, Class[] argTypes, List<Method> matched) throws NoSuchMethodException {
	    if (c == null)
		return;

//	    Method[] methods = c.getDeclaredMethods();
	    Method[] methods = c.getMethods();  // finds only public methods

	    for (int i = 0; i < methods.length; i++) {
		Method m = methods[i];
		if (name.equals(m.getName())) {
		    if (compatible(argTypes, m.getParameterTypes()) && (retType == null || retType.isAssignableFrom(m.getReturnType()))) {
			matched.add(m);
		    }
		}
	    }

//	     not needed, getMethods returns them all
//	     findMethods(c.getSuperclass(), name, retType, argTypes, matched);
	}

	static enum Result {
	    LESS, GREATER, EQUAL, UNKNOWN, INCOMPARABLE;
	}

	static Result compareMethods(Method m1, Method m2) {
	    if (m1 == m2)
		return Result.EQUAL;

	    Result r;
	    if (0 < m1.getParameterTypes().length) {
		{
		    int i = 0;
		    Class t1 = m1.getParameterTypes()[i];
		    Class t2 = m2.getParameterTypes()[i];
		    if (t1.equals(t2))
			r = Result.EQUAL;
		    else if (t2.isAssignableFrom(t1))
			r = Result.LESS;
		    else if (t1.isAssignableFrom(t2))
			r = Result.GREATER;
		    else
			r = Result.INCOMPARABLE;
		}

		for (int i = 1; i < m1.getParameterTypes().length && r != Result.INCOMPARABLE; i++) {
		    Class t1 = m1.getParameterTypes()[i];
		    Class t2 = m2.getParameterTypes()[i];
		    if (t1.equals(t2))
			; // no change
		    else if (t2.isAssignableFrom(t1))
			r = r == Result.GREATER ? Result.INCOMPARABLE : Result.LESS;
		    else if (t1.isAssignableFrom(t2))
			r = r == Result.LESS ? Result.INCOMPARABLE : Result.GREATER;
		    else
			r = Result.INCOMPARABLE;
		}

		if (r == Result.INCOMPARABLE) {
		    // Subclass wins
		    if (m1.getDeclaringClass().isAssignableFrom(m2.getDeclaringClass())) {
			r = Result.LESS;
		    }
		    if (m2.getDeclaringClass().isAssignableFrom(m1.getDeclaringClass())) {
			r = Result.GREATER;
		    }
		}

		return r;
	    }
	    return Result.EQUAL;
	}

	private static Method findMethod(Class<?> c, String name, Class<?> retType, Class[] argTypes) throws NoSuchMethodException {
	    List<Method> matched = new ArrayList<Method>(1);
	    findMethods(c, name, retType, argTypes, matched);
	    if (matched.isEmpty())
		throw new NoSuchMethodException(c + "." + name + Arrays.asList(argTypes));
	    Method mostSpecific = matched.get(0);
	    for (int i = 1; i < matched.size(); i++) {
		Method mi = matched.get(i);
		Result r = compareMethods(mi, mostSpecific);
		if (r == Result.LESS) {
		    mostSpecific = mi;
		}
	    }
	    for (int i = 0; i < matched.size(); i++) {
		Method mi = matched.get(i);
		Result r = compareMethods(mi, mostSpecific);
		if (r == Result.INCOMPARABLE) {
		    mostSpecific = null;
		}
	    }
	    if (mostSpecific == null)
		throw new NoSuchMethodException("Ambiguous call " + matched);
	    return mostSpecific;
	}

	public static <T> Object invoke(T target, String name, Object[] args) throws java.lang.Throwable {
	    // Find the most specific method with the same name as m.
	    Class<?> c = target.getClass();

	    args = flatten(args);

	    Class[] argTypes = new Class[args.length];
	    for (int i = 0; i < args.length; i++) {
		argTypes[i] = args[i] != null ? args[i].getClass() : null;
	    }

	    Method m;

	    try {
		m = findMethod(c, name, null, argTypes);
		// This finds the method with exactly formal parameter types ==
		// argTypes.
		// m = c.getMethod(name, argTypes);
	    }
	    catch (NoSuchMethodException e) {
		throw e;
	    }

	    try {
		return m.invoke(target, args);
	    }
	    catch (IllegalArgumentException e) {
		throw e;
	    }
	    catch (IllegalAccessException e) {
		throw e;
	    }
	    catch (InvocationTargetException e) {
		throw e.getTargetException();
	    }
	}

	private static Object[] flatten(Object[] args) {
	    int n = 0;
	    boolean flatten = false;
	    for (int i = 0; i < args.length; i++) {
		if (args[i] instanceof Object[]) {
		    Object[] a = (Object[]) args[i];
		    n += a.length;
		    flatten = true;
		}
		else {
		    n++;
		}
	    }
	    
	    if (! flatten)
		return args;

	    Object[] newArgs = new Object[n];
	    for (int i = 0, j = 0; i < args.length; i++) {
		if (args[i] instanceof Object[]) {
		    Object[] a = (Object[]) args[i];
		    for (int k = 0; k < a.length; k++) {
			newArgs[j++] = a[k];
		    }
		}
		else {
		    newArgs[j++] = args[i];
		}
	    }
	    return newArgs;
	}

	public Object invoke(Object proxy, java.lang.reflect.Method m, Object[] args) throws java.lang.Throwable {
	    return invoke(target, m.getName(), args);
	}
    }

    public static class Dispatcher {
	String name;

	public Dispatcher(String name) {
	    this.name = name;
	}

	public Object invoke(Object a, Object... b) {
	    try {
		return DispatchProxy.invoke(a, name, b);
	    }
	    catch (Error e) {
		throw e;
	    }
	    catch (RuntimeException e) {
		e.printStackTrace();
		throw e;
	    }
	    catch (NoSuchMethodException e) {
		throw new InternalCompilerError(e);
	    }
	    catch (Exception e) {
		throw new PassthruError(e);
	    }
	    catch (Throwable e) {
		throw new InternalCompilerError(e);
	    }
	}
    }
}
