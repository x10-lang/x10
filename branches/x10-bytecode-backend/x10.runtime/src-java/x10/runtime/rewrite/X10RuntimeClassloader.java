/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.runtime.rewrite;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import x10.runtime.bytecode.ClassFile;
import x10.runtime.bytecode.ClassFileUtil.MethodSig;

import static x10.runtime.bytecode.ByteArrayUtil.*;
import static x10.runtime.bytecode.BytecodeConstants.*;
import static x10.runtime.bytecode.ClassFileUtil.*;
import static x10.runtime.bytecode.ClassFileConstants.*;

public class X10RuntimeClassloader extends ClassLoader {
	private static final boolean DUMP_GENERATED_CLASSES = true;
	private static final boolean VERBOSE = false;
	// for testing
	public static void main(String[] args) {
		try {
			X10RuntimeClassloader cl = new X10RuntimeClassloader();
			for (int i = 0; i < args.length; i++) {
				((Runnable)cl.loadClass(args[i]).newInstance()).run();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private static final String PARAMETERS = "x10.generics.Parameters";
	private final Method PARAMETERS_VALUE;
	private static final String INSTANTIATION = "x10.generics.Instantiation";
	private final Method INSTANTIATION_VALUE;
	private static final String PARAMETRIC_METHOD = "x10.generics.ParametricMethod";
	private final Method PARAMETRIC_METHOD_VALUE;
	private static final String INSTANTIATE_SUPERCLASS = "x10.generics.InstantiateClass";
	private final Method INSTANTIATE_SUPERCLASS_VALUE;
	private static final String INSTANTIATE_INTERFACES = "x10.generics.InstantiateInterfaces";
	private final Method INSTANTIATE_INTERFACES_VALUE;
	private static final String INSTANTIATE_TYPE = "x10.generics.InstantiateType";
	private static final Class<?>[] NO_PARAMETERS = { };
	private Method getValueMethod(String container) {
		try {
			Class<? extends Annotation> pc = (Class<? extends Annotation>)loadClass(container);
			return pc.getDeclaredMethod("value", NO_PARAMETERS);
		}
		catch (SecurityException e) { assert(false); return null; }
		catch (NoSuchMethodException e) { assert(false); return null; }
		catch (ClassNotFoundException e) { assert(false); return null; }
	}
	public X10RuntimeClassloader() {
		PARAMETERS_VALUE = getValueMethod(PARAMETERS);
		INSTANTIATION_VALUE = getValueMethod(INSTANTIATION);
		PARAMETRIC_METHOD_VALUE = getValueMethod(PARAMETRIC_METHOD);
		INSTANTIATE_SUPERCLASS_VALUE = getValueMethod(INSTANTIATE_SUPERCLASS);
		INSTANTIATE_INTERFACES_VALUE = getValueMethod(INSTANTIATE_INTERFACES);
	}

	public static final String[] NO_TYPE_ARGS = { };
	public static final String JAVA_LANG = "java.lang.";
	public static final String JAVA_IO = "java.io.";
	private HashMap<String, ClassFile> classFileCache = new HashMap<String, ClassFile>();
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (name.equals(this.getClass().getName()))
			return this.getClass(); // short-circuit this class
		try {
			Class<?> c = findLoadedClass(name);
			if (c == null) {
				ClassFile cf = parseClass(name);
				if (cf == null)
					throw new NoClassDefFoundError(name);
				String[] parms = getFormalParameters(cf, name);
				if (parms == null || parms.length == 0) {
					// TODO: new ClassFileRewriter.
					instantiate(cf, NO_TYPE_ARGS);
				}
				byte[] result = cf.toByteArray();
				if (DUMP_GENERATED_CLASSES) {
					// assumes the existence of a subdirectory "cache" in the current directory
					FileOutputStream fo = new FileOutputStream("cache/"+name+".class");
					fo.write(result);
					fo.close();
				}
				c = defineClass(name, result);
				if (VERBOSE)
					System.out.println("Loaded "+c.getName()+" with "+c.getClassLoader()+" from "+this);
			}
			if (resolve)
				resolveClass(c);
			return c;
		} catch (Exception e) {
			// Oops, not found or not supposed to muck with this
			if (VERBOSE && !(e instanceof SecurityException))
				e.printStackTrace(System.out);
			return super.loadClass(name, resolve);
		} catch (NoClassDefFoundError e) {
			// Oops, not found or not supposed to muck with this
			return super.loadClass(name, resolve);
		}
	}
	protected ClassFile parseClass(String name) {
		ClassFile cf = classFileCache.get(name);
		if (name.startsWith(JAVA_LANG) || name.startsWith(JAVA_IO))
			return null;
		if (cf == null) {
			InputStream in = openClassFile(name);
			if (in == null)
				return null;
			try {
				byte[] contents = new byte[in.available()];
				in.read(contents);
				in = new ByteArrayInputStream(contents);
				cf = ClassFile.parseClass(name, in);
			} catch (IOException e) { }
			classFileCache.put(name, cf);
		}
		return cf;
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		int separator = name.indexOf(PARAM_SEPARATOR);
		if (separator == -1)
			return super.findClass(name);
		String base = name.substring(0, separator);
		String[] actuals = demangle(name);
		return findClass(base, actuals);
	}

	protected Class<?> findClass(String base, String[] actuals) throws ClassNotFoundException {
		InputStream in = openClassFile(base);
		if (in == null)
			throw new ClassNotFoundException(base);
		String name = mangle(base, actuals);
		try {
			byte[] contents = new byte[in.available()];
			in.read(contents);
			in = new ByteArrayInputStream(contents);
			ClassFile cf = ClassFile.parseClass(base, in);
			// TODO: new ClassFileRewriter.
			instantiate(cf, actuals);
			byte[] result = cf.toByteArray();
			if (DUMP_GENERATED_CLASSES) {
				// assumes the existence of a subdirectory "cache" in the current directory
				FileOutputStream fo = new FileOutputStream("cache/"+name+".class");
				fo.write(result);
				fo.close();
			}
			return defineClass(name, result);
		} catch (IOException e) {
			throw new ClassNotFoundException(name, e);
		}
	}

	private InputStream openClassFile(String className) {
		return this.getResourceAsStream((className.replace('.','/')+".class"));
	}

	private Class<?> defineClass(String name, byte[] b) {
		return defineClass(name, b, 0, b.length);
	}

	public static final String PARAM_SEPARATOR = "$$";
	public static final String DOT_MANGLING = "$_P";
	public static final String DOLLAR_MANGLING = "$_D";
	public static final String BRACKETS_MANGLING = "$$$_";
	private static String RE(String s) {
		return s.replaceAll("([\\$\\[\\]\\.\\(\\)])", "\\\\$1");
	}
	private static String mangle(String name, String[] actuals) {
		int bkt_idx = name.lastIndexOf("[]");
		if (bkt_idx != -1)
			return mangle(name.substring(0, bkt_idx), actuals) + "[]";
		for (int i = 0; i < actuals.length; i++)
			name += PARAM_SEPARATOR +
			        actuals[i].replaceAll(RE("$"), RE(DOLLAR_MANGLING))
			                  .replaceAll(RE("."), RE(DOT_MANGLING))
			                  .replaceAll(RE("[]"), RE(BRACKETS_MANGLING));
		return name;
	}
	private static String[] demangle(String type) {
		int separator = type.indexOf(PARAM_SEPARATOR);
		if (separator == -1)
			return NO_TYPE_ARGS;
		type = type.substring(separator+PARAM_SEPARATOR.length());
		String thisType = type.replaceAll(RE(BRACKETS_MANGLING), "[]")
		                      .replaceAll(RE(DOT_MANGLING), "."); // FIXME: encoding
		String[] result = thisType.split(RE(PARAM_SEPARATOR));
		for (int i = 0; i < result.length; i++)
			result[i] = result[i].replaceAll(RE(DOLLAR_MANGLING), RE("$")); // FIXME: encoding
		return result;
	}
	private static int getFormal(String f, String[] formals) {
		for (int i = 0; i < formals.length; i++)
			if (formals[i].equals(f))
				return i;
		return -1;
	}
	private static int getFormalIndex(String sigType, String[] formals) {
		if (!sigType.startsWith("L"))
			return -1;
		return getFormal(sigType, formals);
	}

	private static final String INSTANCEOF_METHOD = "instanceof$";
	private static final String INSTANCEOF_SIG = "("+SIG_Object+SIG_String+")"+SIG_boolean;
	private static final String INSTANCEOF1_SIG = "("+SIG_Object+SIG_String+SIG_boolean+")"+SIG_boolean;
	private static final String CAST_METHOD = "cast$";
	private static final String NEWARRAY_METHOD = "newarray$";
	private static final String CAST_SIG = "("+SIG_Object+SIG_String+")"+SIG_Object;
	private static final String COERCE_METHOD = "coerce";
	private static final String RECOVER_PREFIX = "recover";
	private static final String PARAMETRIC_METHOD_PREFIX = "make$";

	/**
	 * Rename the class appropriately.
	 * Replace all occurrences of each formal parameter by the corresponding
	 * actual parameter.  The possible occurrences are: type of a field,
	 * type of a local, method parameter, instanceof test, instantiation
	 * of another generic type.
	 * TODO: what did I miss here?
	 * Also change all aloads, areturns, and compares to appropriate primitive
	 * loads, returns, and compares.
	 * FIXME: what to do about non-type (e.g. integer) and function parameters?
	 */
	protected void instantiate(ClassFile cf, String[] actuals) {
		short nameIndex = cf.getNameIndex(cf.this_class);
		String typeName = cf.getString(nameIndex);
		if (VERBOSE)
			System.out.println("Got class "+typeName);
		short[] changedSignatures = new short[cf.constantPoolSize()];
		String newName = mangle(typeName, actuals);
		short newTypeIndex = createClassRef(cf, newName);
		short newNameIndex = cf.getNameIndex(newTypeIndex);
		changedSignatures[nameIndex] = newNameIndex;
		changedSignatures[cf.this_class] = newTypeIndex;
		cf.addInstance(newTypeIndex);
		//c.this_class = newTypeIndex;
		if (VERBOSE)
			System.out.println("   ->"+newName);
		String[] formals = getFormalParameters(cf, typeName);
		assert (formals != null);
		assert (formals.length == actuals.length);
		// instantiate superclass and superinterfaces
		String superType = cf.getString(cf.getNameIndex(cf.super_class));
		String[] superParms = getSuperParms(cf.this_class, cf);
		if (superParms != null) {
			String[] superActuals = new String[superParms.length];
			for (int i = 0; i < superParms.length; i++)
				superActuals[i] = typeFromSignature(instantiateType(typeToSignature(superParms[i]), formals, actuals));
			String[] superFormals = getFormals(cf.super_class, cf, formals);
			assert (superActuals.length == superFormals.length);
			short new_super_index = createClassRef(cf, mangle(superType, superActuals));
			if (new_super_index != cf.super_class)
				cf.super_class = new_super_index;
		}
		String[][] interfaceParms = getInterfaceParms(cf.this_class, cf);
		if (interfaceParms != null) {
			assert (cf.interfaces.length == interfaceParms.length);
			for (int i = 0; i < cf.interfaces.length; i++) {
				String interfaceType = cf.getString(cf.getNameIndex(cf.interfaces[i]));
				if (interfaceParms[i] != null) {
					String[] interfaceActuals = new String[interfaceParms[i].length];
					for (int j = 0; j < interfaceParms[i].length; j++)
						interfaceActuals[j] = instantiateType(interfaceParms[i][j], formals, actuals);
					String[] interfaceFormals = getFormals(cf.interfaces[i], cf, formals);
					assert (interfaceActuals.length == interfaceFormals.length);
					short new_interface_index = createClassRef(cf, mangle(interfaceType, interfaceActuals));
					if (new_interface_index != cf.interfaces[i])
						cf.interfaces[i] = new_interface_index;
				}
			}
		}

		// instantiate fields
		for (int i = 0; i < cf.fields.length; i++) {
			ClassFile.Field f = cf.fields[i];
			String name = cf.getString(f.name_index);
			String signature = cf.getString(f.descriptor_index);
			if (VERBOSE)
				System.out.println("\tGot field "+name+" with signature "+signature);
			String newSignature = signature;
			String[] fieldActuals = getTypeParms(f, cf.this_class, cf);
			if (fieldActuals != null) {
				String[] fieldFormals = getFormals(extractType(newSignature));
				assert (fieldFormals.length == fieldActuals.length);
				newSignature = typeToSignature(mangle(typeFromSignature(newSignature), fieldActuals));
			}
			newSignature = instantiateType(newSignature, formals, actuals);
			short new_index = createString(cf, newSignature);
			if (new_index != f.descriptor_index) {
				changedSignatures[f.descriptor_index] = new_index;
				if (VERBOSE)
					System.out.println("\t   ->" + newSignature);
				//f.descriptor_index = new_index;
			}
			// TODO: instantiate attributes
		}

		// instantiate methods
		String container = typeToSignature(newName);
		// FIXME: watch out for overloaded methods! (e.g., foo(int) vs. foo(T))
		for (int i = 0; i < cf.methods.length; i++) {
			ClassFile.Method m = cf.methods[i];
			String signature = cf.getString(m.descriptor_index);
			String name = cf.getString(m.name_index);
			byte[] code = cf.getMethodCode(m);
			if (code == null)
				continue;
			int max_stack = cf.getMethodStack(code);
			int max_locals = cf.getMethodLocals(code);
			boolean isStatic = (m.access_flags & ACC_STATIC) != 0;
			int adjust = isStatic ? 0 : 1;
			char[] remap = new char[1+max_locals]; // return type + locals
			if (VERBOSE)
				System.out.println("\tGot method "+name+" with signature "+signature);
			String newSignature = signature;
			short new_index;
			MethodSig sig = MethodSig.fromSignature(signature);
			assert (max_locals >= adjust+sig.argTypes.length);
			String[] methodActuals = getTypeParms(m, cf.this_class, cf);
			if (methodActuals != null) {
				String[] methodFormals = getFormals(extractType(sig.returnType));
				assert (methodFormals.length == methodActuals.length);
				sig.returnType = typeToSignature(mangle(typeFromSignature(sig.returnType), methodActuals));
			}
			for (int p = 0; p < sig.argTypes.length; p++) {
				String[] methodParmActuals = getTypeParms(m, cf.this_class, cf, p);
				if (methodParmActuals != null) {
					String[] parmFormals = getFormals(extractType(sig.argTypes[p]));
					assert (parmFormals.length == methodParmActuals.length);
					sig.argTypes[p] = typeToSignature(mangle(typeFromSignature(sig.argTypes[p]), methodParmActuals));
				}
			}
			MethodSig newSig = instantiateSignature(sig, formals, actuals);
			computeRemapping(sig, newSig, isStatic, remap);
			newSignature = newSig.toSignature();
			new_index = createString(cf, newSignature);
			if (new_index != m.descriptor_index) {
				changedSignatures[m.descriptor_index] = new_index;
				if (VERBOSE)
					System.out.println("\t   ->" + newSignature);
				//m.descriptor_index = new_index;
			}
			short[] reassignLocals = new short[max_locals];
			short nextLocal = 0;
			for (int j = 0; j < reassignLocals.length; j++, nextLocal++) {
				reassignLocals[j] = nextLocal;
				if (isWidePrimitive(remap[1+j]))
					nextLocal++;
			}
			BytecodeInterpreter bcInt = new BytecodeInterpreter(cf, container, newSig.argTypes,
			                                                    isStatic, code);
			bcInt.interpret(signature, actuals, formals, changedSignatures, remap, reassignLocals);
			short numLocals = 0;
			for (int j = 0; j < max_locals; j++, numLocals++) {
				if (isWidePrimitive(remap[1+j]))
					numLocals++;
			}
			if (numLocals != max_locals || isWidePrimitive(remap[0])) {
				max_stack *= 2;
				cf.setMethodStack(code, (short)max_stack);
			}
			if (numLocals != max_locals) {
				max_locals = numLocals;
				cf.setMethodLocals(code, (short)max_locals);
			}
			// TODO: instantiate attributes
		}
		// TODO: instantiate attributes

		// Update all indexes
		cf.this_class = newTypeIndex;
		for (int i = 0; i < cf.fields.length; i++) {
			ClassFile.Field f = cf.fields[i];
			if (changedSignatures[f.descriptor_index] != 0)
				f.descriptor_index = changedSignatures[f.descriptor_index];
		}
		for (int i = 0; i < cf.methods.length; i++) {
			ClassFile.Method m = cf.methods[i];
			if (changedSignatures[m.descriptor_index] != 0)
				m.descriptor_index = changedSignatures[m.descriptor_index];
		}
	}

	private String extractType(String type) {
		if (isArray(type))
			return extractType(elementType(type));
		if (isPrimitive(type))
			return null;
		assert (type.charAt(0) == 'L' && type.charAt(type.length()-1) == ';');
		return type.substring(1, type.length()-1);
	}

	public final Class<?> instantiate(Class<?> c, Class<?>[] p) {
		try {
			String base = c.getName();
			String[] formals = extractParameters(c, PARAMETERS_VALUE);
			assert (formals != null && formals.length == p.length);
			String[] actuals = new String[p.length];
			for (int i = 0; i < p.length; i++) {
				actuals[i] = p[i].getName();
			}
			return findClass(base, actuals);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public final Class<?> instantiate(Class<?> c, Class<?> p) {
		return instantiate(c, new Class[] { p });
	}

	public final Class<?> instantiate(Class<?> c, Class<?> p, Class<?> q) {
		return instantiate(c, new Class[] { p, q });
	}

	public final Class<?> instantiate(Class<?> c, Class<?> p, Class<?> q, Class<?> r) {
		return instantiate(c, new Class[] { p, q, r });
	}

	public final Class<?> instantiate(Class<?> c, Class<?> p, Class<?> q, Class<?> r, Class<?> s) {
		return instantiate(c, new Class[] { p, q, r, s });
	}

	public final Class<?> getClass(String name) {
		try {
			if (isPrimitive(typeToSignature(name)))
				return getPrimitiveClass(typeToSignature(name));
			return loadClass(name, false);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	private Class<?> getPrimitiveClass(String signature) {
		switch (signature.charAt(0)) {
		case 'B': return boolean.class;
		case 'C': return char.class;
		case 'D': return double.class;
		case 'F': return float.class;
		case 'I': return int.class;
		case 'J': return long.class;
		case 'S': return short.class;
		case 'V': return void.class;
		case 'Z': return boolean.class;
		default:
			throw new IllegalArgumentException(signature);
		}
	}
	private String[] extractParameters(Class<?> containerClass, Method VALUE_METHOD) {
		String[] parms = null;
		Class<? extends Annotation> ANNOTATION_CLASS =
			(Class<? extends Annotation>) VALUE_METHOD.getDeclaringClass();
		Annotation parameters = containerClass.getAnnotation(ANNOTATION_CLASS);
		if (parameters != null) {
			try {
				parms = (String[]) VALUE_METHOD.invoke(parameters);
			} catch (IllegalAccessException e) { } catch (InvocationTargetException e) { }
			if (VERBOSE)
				System.out.println(ANNOTATION_CLASS.getName()+" annotation = "+parameters.getClass().getName()+
						" of type "+parameters.annotationType().getName()+" = "+Arrays.toString(parms));
		}
		return parms;
	}
	private String[] extractParameters(Field f, Method VALUE_METHOD) {
		String[] parms = null;
		Class<? extends Annotation> ANNOTATION_CLASS =
			(Class<? extends Annotation>) VALUE_METHOD.getDeclaringClass();
		Annotation parameters = f.getAnnotation(ANNOTATION_CLASS);
		if (parameters != null) {
			try {
				parms = (String[]) VALUE_METHOD.invoke(parameters);
			} catch (IllegalAccessException e) { } catch (InvocationTargetException e) { }
			if (VERBOSE)
				System.out.println(ANNOTATION_CLASS.getName()+" annotation = "+parameters.getClass().getName()+
						" of type "+parameters.annotationType().getName()+" = "+Arrays.toString(parms));
		}
		return parms;
	}
	private String[] extractParameters(Method m, Method VALUE_METHOD) {
		String[] parms = null;
		Class<? extends Annotation> ANNOTATION_CLASS =
			(Class<? extends Annotation>) VALUE_METHOD.getDeclaringClass();
		Annotation parameters = m.getAnnotation(ANNOTATION_CLASS);
		if (parameters != null) {
			try {
				parms = (String[]) VALUE_METHOD.invoke(parameters);
			} catch (IllegalAccessException e) { } catch (InvocationTargetException e) { }
			if (VERBOSE)
				System.out.println(ANNOTATION_CLASS.getName()+" annotation = "+parameters.getClass().getName()+
						" of type "+parameters.annotationType().getName()+" = "+Arrays.toString(parms));
		}
		return parms;
	}
	private Annotation getAnnotation(Annotation[] a, Class<? extends Annotation> c) {
		if (a == null)
			return null;
		for (int i = 0; i < a.length; i++)
			if (c.isInstance(a[i]))
				return a[i];
		return null;
	}
	private String[] extractParameters(Method m, int p, Method VALUE_METHOD) {
		String[] parms = null;
		Class<? extends Annotation> ANNOTATION_CLASS =
			(Class<? extends Annotation>) VALUE_METHOD.getDeclaringClass();
		Annotation[][] paramAnnot = m.getParameterAnnotations();
		if (paramAnnot == null)
			return parms;
		Annotation parameters = getAnnotation(paramAnnot[p], ANNOTATION_CLASS);
		if (parameters != null) {
			try {
				parms = (String[]) VALUE_METHOD.invoke(parameters);
			} catch (IllegalAccessException e) { } catch (InvocationTargetException e) { }
			if (VERBOSE)
				System.out.println(ANNOTATION_CLASS.getName()+" annotation = "+parameters.getClass().getName()+
						" of type "+parameters.annotationType().getName()+" = "+Arrays.toString(parms));
		}
		return parms;
	}
	private HashMap<String, String[]> formalsCache = new HashMap<String, String[]>();
	private String[] getFormals(int class_index, ClassFile cf, String[] formals) {
		if (cf.isInstance(class_index))
			return formals;
		String container = cf.getString(cf.getNameIndex(class_index));
		return getFormals(container);
	}
	private String[] getFormals(String container) {
		if (container == null)
			return NO_TYPE_ARGS;
		if (formalsCache.containsKey(container))
			return formalsCache.get(container);
		String[] parms = NO_TYPE_ARGS;
		ClassFile cf = parseClass(container.replace('/','.'));
		if (cf != null)
			parms = getFormalParameters(cf, container);
		else { // if all else fails, use reflection
			Class<?> c = getClass(container.replace('/','.'));
			if (c != null) {
				String[] strings = extractParameters(c, PARAMETERS_VALUE);
				if (strings == null)
					parms = NO_TYPE_ARGS;
				else {
					parms = new String[strings.length];
					for (int i = 0; i < strings.length; i++)
						parms[i] = typeToSignature(createNestedType(container, strings[i]));
				}
			}
		}
		formalsCache.put(container, parms);
		return parms;
	}
	private HashMap<String, String[]> remapCache = new HashMap<String, String[]>();
	private String[] getRemapType(int class_index, ClassFile cf) {
		String container = cf.getString(cf.getNameIndex(class_index));
		if (remapCache.containsKey(container))
			return remapCache.get(container);
		String[] type = null;
		if (!cf.isInstance(class_index))
			cf = parseClass(container.replace('/','.'));
		if (cf != null) {
			ClassFile.Annotation a = cf.findAnnotation(typeToSignature(INSTANTIATION));
			if (a != null)
				type = getStringsValue(cf, a);
		} else { // if all else fails, use reflection
			Class<?> c = getClass(container.replace('/','.'));
			if (c != null)
				type = extractParameters(c, INSTANTIATION_VALUE);
		}
		remapCache.put(container, type);
		return type;
	}
	private HashMap<String, String[]> methodFormalsCache = new HashMap<String, String[]>();
	private String[] getMethodFormals(int method_index, ClassFile cf) {
		short class_index = cf.getClassIndex(method_index);
		short name_and_type_index = cf.getNameAndTypeIndex(method_index);
		String container = cf.getString(cf.getNameIndex(class_index));
		String name = cf.getString(cf.getNameIndex(name_and_type_index));
		String signature = cf.getString(cf.getDescriptorIndex(name_and_type_index));
		if (!name.startsWith(PARAMETRIC_METHOD_PREFIX))
			return null;
		String method = container+"."+name+signature;
		if (methodFormalsCache.containsKey(method))
			return methodFormalsCache.get(method);
		String[] parms = NO_TYPE_ARGS;
		if (!cf.isInstance(class_index))
			cf = parseClass(name.replace('/','.'));
		if (cf != null) {
			ClassFile.Method m = cf.findMethod(name, signature);
			ClassFile.Annotation a = cf.findAnnotation(m, typeToSignature(PARAMETRIC_METHOD));
			if (a != null) {
				if (VERBOSE)
					System.out.println("Got annotation: "+PARAMETRIC_METHOD);
				parms = getStringsValue(cf, a);
			}
		} else { // if all else fails, use reflection
			Class<?> c = getClass(name.replace('/','.'));
			if (c != null) {
				parms = extractParameters(c, PARAMETRIC_METHOD_VALUE);
				if (parms == null)
					parms = NO_TYPE_ARGS;
			}
		}
		methodFormalsCache.put(method, parms);
		return parms;
	}
	private HashMap<String, String[]> superParmsCache = new HashMap<String, String[]>();
	private String[] getSuperParms(int class_index, ClassFile cf) {
		String container = cf.getString(cf.getNameIndex(class_index));
		if (superParmsCache.containsKey(container))
			return superParmsCache.get(container);
		String[] parms = null;
		if (!cf.isInstance(class_index))
			cf = parseClass(container.replace('/','.'));
		if (cf != null) {
			ClassFile.Annotation a = cf.findAnnotation(typeToSignature(INSTANTIATE_SUPERCLASS));
			if (a != null)
				parms = getStringsValue(cf, a);
		} else { // if all else fails, use reflection
			Class<?> c = getClass(container.replace('/','.'));
			if (c != null)
				parms = extractParameters(c, INSTANTIATE_SUPERCLASS_VALUE);
		}
		superParmsCache.put(container, parms);
		return parms;
	}
	private HashMap<String, String[][]> interfaceParmsCache = new HashMap<String, String[][]>();
	private String[][] getInterfaceParms(int class_index, ClassFile cf) {
		String container = cf.getString(cf.getNameIndex(class_index));
		if (interfaceParmsCache.containsKey(container))
			return interfaceParmsCache.get(container);
		String[][] values = null;
		if (!cf.isInstance(class_index))
			cf = parseClass(container.replace('/','.'));
		if (cf != null) {
			ClassFile.Annotation a = cf.findAnnotation(typeToSignature(INSTANTIATE_INTERFACES));
			if (a != null) {
				assert (a.element_value_pairs.length == 1);
				assert (cf.getString(a.element_value_pairs[0].element_name_index).equals("value"));
				assert (a.element_value_pairs[0].value.tag == ClassFile.Annotation.TAG_array);
				ClassFile.Annotation.ArrayValue val =
					(ClassFile.Annotation.ArrayValue) a.element_value_pairs[0].value;
				values = new String[val.values.length][];
				for (int j = 0; j < val.values.length; j++) {
					assert (val.values[j].tag == ClassFile.Annotation.TAG_Annotation);
					ClassFile.Annotation v = ((ClassFile.Annotation.AnnotationValue) val.values[j]).annotation_value;
					assert (v.element_value_pairs.length == 1);
					assert (cf.getString(v.element_value_pairs[0].element_name_index).equals("value"));
					assert (v.element_value_pairs[0].value.tag == ClassFile.Annotation.TAG_array);
					ClassFile.Annotation.ArrayValue r =
						(ClassFile.Annotation.ArrayValue) v.element_value_pairs[0].value;
					values[j] = new String[r.values.length];
					for (int j1 = 0; j1 < r.values.length; j1++) {
						assert (r.values[j1].tag == ClassFile.Annotation.TAG_String);
						ClassFile.Annotation.ConstValue c = (ClassFile.Annotation.ConstValue) r.values[j1];
						values[j][j1] = cf.getString(c.const_value_index);
					}
				}
			}
		} else { // if all else fails, use reflection
			Class<?> c = getClass(container.replace('/','.'));
			if (c != null) {
				Class<? extends Annotation> INSTANTIATE_INTERFACES_CLASS =
					(Class<? extends Annotation>) INSTANTIATE_INTERFACES_VALUE.getDeclaringClass();
				Annotation parameters = c.getAnnotation(INSTANTIATE_INTERFACES_CLASS);
				if (parameters != null) {
					try {
						Annotation[] components = (Annotation[]) INSTANTIATE_INTERFACES_VALUE.invoke(parameters);
						values = new String[components.length][];
						for (int i = 0; i < components.length; i++)
							values[i] = (String[]) INSTANTIATE_SUPERCLASS_VALUE.invoke(components[i]);
					} catch (IllegalAccessException e) { } catch (InvocationTargetException e) { }
					if (VERBOSE)
						System.out.println(INSTANTIATE_INTERFACES_CLASS.getName()+" annotation = "+parameters.getClass().getName()+
								" of type "+parameters.annotationType().getName()+" = "+Arrays.toString(values));
				}
			}
		}
		String[][] parms = values;
		// TODO
//		if (values != null) {
//			parms = new String[values.length][];
//			for (int i = 0; i < values.length; i++) {
//				parms[i] = new String[values[i].length];
//				for (int j = 0; j < values[i].length; j++)
//					parms[i][j] = values[i][j];
//			}
//		}
		interfaceParmsCache.put(container, parms);
		return parms;
	}
	private HashMap<String, String[]> typeParmsCache = new HashMap<String, String[]>();
	/** Get type parameters of a field in the current class */
	private String[] getTypeParms(ClassFile.Field f, int class_index, ClassFile cf) {
		String container = cf.getString(cf.getNameIndex(class_index));
		String name = cf.getString(f.name_index);
		String descriptor = cf.getString(f.descriptor_index);
		String field = container+" "+name+" "+descriptor;
		if (typeParmsCache.containsKey(field))
			return typeParmsCache.get(field);
		String[] parms = null;
		ClassFile.Annotation a = cf.findAnnotation(f, typeToSignature(INSTANTIATE_SUPERCLASS));
		if (a != null)
			parms = getStringsValue(cf, a);
		typeParmsCache.put(field, parms);
		return parms;
	}
	private String[] getFieldTypeParms(int field_ref_index, ClassFile cf) {
		int class_index = cf.getClassIndex(field_ref_index);
		int container_index = cf.getNameIndex(class_index);
		int name_and_type_index = cf.getNameAndTypeIndex(field_ref_index);
		int name_index = cf.getNameIndex(name_and_type_index);
		int descriptor_index = cf.getDescriptorIndex(name_and_type_index);
		String container = cf.getString(container_index);
		String name = cf.getString(name_index);
		String descriptor = cf.getString(descriptor_index);
		String field = container+" "+name;
		if (typeParmsCache.containsKey(field))
			return typeParmsCache.get(field);
		String[] parms = null;
		if (!cf.isInstance(class_index))
			cf = parseClass(container.replace('/','.'));
		if (cf != null) {
			ClassFile.Field f = cf.findField(name, descriptor);
			ClassFile.Annotation a = cf.findAnnotation(f, typeToSignature(INSTANTIATE_SUPERCLASS));
			if (a != null)
				parms = getStringsValue(cf, a);
		} else { // if all else fails, use reflection
			try {
				Class<?> c = getClass(container.replace('/','.'));
				if (c != null)
					parms = extractParameters(c.getDeclaredField(name), INSTANTIATE_SUPERCLASS_VALUE);
			} catch (NoSuchFieldException e) { }
		}
		typeParmsCache.put(field, parms);
		return parms;
	}
	private String[] getTypeParms(ClassFile.Method m, int class_index, ClassFile cf) {
		String container = cf.getString(cf.getNameIndex(class_index));
		String name = cf.getString(m.name_index);
		String signature = cf.getString(m.descriptor_index);
		String method = container+" "+name+signature;
		if (typeParmsCache.containsKey(method))
			return typeParmsCache.get(method);
		String[] parms = null;
		ClassFile.Annotation a = cf.findAnnotation(m, typeToSignature(INSTANTIATE_SUPERCLASS));
		if (a != null)
			parms = getStringsValue(cf, a);
		typeParmsCache.put(method, parms);
		return parms;
	}
	private String[] getMethodTypeParms(int method_ref_index, ClassFile cf) {
		short class_index = cf.getClassIndex(method_ref_index);
		int container_index = cf.getNameIndex(class_index);
		int name_and_type_index = cf.getNameAndTypeIndex(method_ref_index);
		int name_index = cf.getNameIndex(name_and_type_index);
		int descriptor_index = cf.getDescriptorIndex(name_and_type_index);
		String container = cf.getString(container_index);
		String name = cf.getString(name_index);
		String signature = cf.getString(descriptor_index);
		String method = container+" "+name+signature;
		if (typeParmsCache.containsKey(method))
			return typeParmsCache.get(method);
		String[] parms = null;
		if (!cf.isInstance(class_index))
			cf = parseClass(container.replace('/','.'));
		if (cf != null) {
			ClassFile.Method m = cf.findMethod(name, signature);
			ClassFile.Annotation a = cf.findAnnotation(m, typeToSignature(INSTANTIATE_SUPERCLASS));
			if (a != null)
				parms = getStringsValue(cf, a);
		} else { // if all else fails, use reflection
			try {
				Class<?> c = getClass(container.replace('/','.'));
				if (c != null) {
					MethodSig sig = MethodSig.fromSignature(signature);
					Class<?>[] argTypes = new Class[sig.argTypes.length];
					for (int i = 0; i < argTypes.length; i++) {
						argTypes[i] = getClass(typeFromSignature(sig.argTypes[i]));
					}
					parms = extractParameters(c.getDeclaredMethod(name, argTypes), INSTANTIATE_SUPERCLASS_VALUE);
				}
			} catch (NoSuchMethodException e) { }
		}
		typeParmsCache.put(method, parms);
		return parms;
	}
	private String[] getTypeParms(ClassFile.Method m, int class_index, ClassFile cf, int p) {
		String container = cf.getString(cf.getNameIndex(class_index));
		String name = cf.getString(m.name_index);
		String signature = cf.getString(m.descriptor_index);
		String method = container+" "+name+signature+"["+p+"]";
		if (typeParmsCache.containsKey(method))
			return typeParmsCache.get(method);
		String[] parms = null;
		ClassFile.Annotation a = cf.findAnnotation(m, typeToSignature(INSTANTIATE_SUPERCLASS), p);
		if (a != null)
			parms = getStringsValue(cf, a);
		typeParmsCache.put(method, parms);
		return parms;
	}
	private String[] getMethodParamTypeParms(int method_ref_index, int p, ClassFile cf) {
		short class_index = cf.getClassIndex(method_ref_index);
		int container_index = cf.getNameIndex(class_index);
		int name_and_type_index = cf.getNameAndTypeIndex(method_ref_index);
		int name_index = cf.getNameIndex(name_and_type_index);
		int descriptor_index = cf.getDescriptorIndex(name_and_type_index);
		String container = cf.getString(container_index);
		String name = cf.getString(name_index);
		String signature = cf.getString(descriptor_index);
		String param = container+" "+name+signature+"["+p+"]";
		if (typeParmsCache.containsKey(param))
			return typeParmsCache.get(param);
		String[] parms = null;
		if (!cf.isInstance(class_index))
			cf = parseClass(container.replace('/','.'));
		if (cf != null) {
			ClassFile.Method m = cf.findMethod(name, signature);
			ClassFile.Annotation a = cf.findAnnotation(m, typeToSignature(INSTANTIATE_SUPERCLASS), p);
			if (a != null)
				parms = getStringsValue(cf, a);
		} else { // if all else fails, use reflection
			try {
				Class<?> c = getClass(container.replace('/','.'));
				if (c != null) {
					MethodSig sig = MethodSig.fromSignature(signature);
					Class<?>[] argTypes = new Class[sig.argTypes.length];
					for (int i = 0; i < argTypes.length; i++)
						argTypes[i] = getClass(typeFromSignature(sig.argTypes[i]));
					parms = extractParameters(c.getDeclaredMethod(name, argTypes), p, INSTANTIATE_SUPERCLASS_VALUE);
				}
			} catch (NoSuchMethodException e) { }
		}
		typeParmsCache.put(param, parms);
		return parms;
	}

	private static String instantiateType(String type, String[] formals, String[] actuals) {
		if (type.charAt(0) == '[')
			return "["+instantiateType(type.substring(1), formals, actuals);
		String newType = typeFromSignature(type);
		int separator = newType.indexOf(PARAM_SEPARATOR);
		if (separator != -1) {
			String base = newType.substring(0, separator);
			String[] parms = demangle(newType);
			assert (parms.length != 0);
			for (int i = 0; i < parms.length; i++)
				parms[i] = typeFromSignature(instantiateType(typeToSignature(parms[i]), formals, actuals));
			return typeToSignature(mangle(base, parms));
		}
		int fi = getFormalIndex(type, formals);
		if (fi == -1)
			return type;
		return typeToSignature(actuals[fi]);
	}
	private static MethodSig instantiateSignature(MethodSig sig, String[] formals, String[] actuals) {
		MethodSig res = new MethodSig();
		res.name = sig.name;
		res.returnType = instantiateType(sig.returnType, formals, actuals);
		res.argTypes = new String[sig.argTypes.length];
		for (int j = 0; j < sig.argTypes.length; j++)
			res.argTypes[j] = instantiateType(sig.argTypes[j], formals, actuals);
		return res;
	}
	private static void computeRemapping(MethodSig sig, MethodSig newSig, boolean isStatic, char[] remap) {
		if (isPrimitive(newSig.returnType) && !isPrimitive(sig.returnType))
			remap[0] = newSig.returnType.charAt(0);
		int adjust = isStatic ? 0 : 1;
		for (int j = 0; j < sig.argTypes.length; j++) {
			if (isPrimitive(newSig.argTypes[j]) && !isPrimitive(sig.argTypes[j]))
				remap[1+adjust+j] = newSig.argTypes[j].charAt(0);
		}
	}

	// TODO: factor out the rewriting parts into a subclass
	public class BytecodeInterpreter {
		private final ClassFile cf;
		private final byte[] code;
		private final int off;
		private final int len;
		private final int max_stack;
		private final int max_locals;
		private final String[] stack;
		private int sp;
		private final String[] locals;
		private final int[] depth;
		private final short[] handlers;
		private final short[] catches;
		private int o;
		public BytecodeInterpreter(ClassFile cf, String container, String[] argTypes, boolean isStatic, byte[] code) {
			this.cf = cf;
			this.code = code;
			this.off = cf.getMethodCodeOffset(code);
			this.len = cf.getMethodCodeLength(code);
			this.max_stack = cf.getMethodStack(code);
			this.max_locals = cf.getMethodLocals(code);
			this.stack = new String[max_stack*2+1];
			int adjust = isStatic ? 0 : 1;
			this.locals = new String[max_locals];
			if (!isStatic)
				locals[0] = container;
			for (int i = 0; i < argTypes.length; i++)
				locals[i+adjust] = argTypes[i];
			this.depth = new int[len];
			int exc_off = cf.getMethodExceptionTableOffset(code);
			int exc_len = cf.getMethodExceptionTableLength(code) / 8;
			this.handlers = new short[exc_len];
			this.catches = new short[exc_len];
			for (int i = 0; i < exc_len; i++) {
				int e = exc_off+i*8;
				short start = getShort(code, e);
				short end = getShort(code, e+2);
				handlers[i] = getShort(code, e+4);
				catches[i] = getShort(code, e+6);
				depth[handlers[i]] = 1; // will push the exception type separately
			}
			this.sp = 0;
			this.o = off;
		}

		protected final int getByte(byte[] code, int o) { return code[o] & 0xFF; }
		protected final void putByte(byte[] code, int o, int b) { code[o] = (byte) b; }

		protected final void push(String type) { stack[++sp] = type; }
		protected final String peek() { return peek(0); }
		protected final String peek(int depth) { assert(sp >= depth); return stack[sp-depth]; }
		protected final String pop() { assert(sp >= 1); String type = stack[sp]; stack[sp--] = null; return type; }

		protected final int findExceptionHandler(int o) {
			// Assume sorted
			return Arrays.binarySearch(handlers, (short) o);
		}
		protected final int getExceptionHandlerCount() {
			return handlers.length;
		}
		protected final short getExceptionHandler(int i) {
			return handlers[i];
		}
		protected final short getCaughtExceptionIndex(int i) {
			return catches[i];
		}
		protected final void setCaughtExceptionIndex(int i, short v) {
			int exc_off = cf.getMethodExceptionTableOffset(code);
			int e = exc_off+i*8;
			putShort(code, e+6, v);
		}

		private boolean reinterpret = false;
		/**
		 * Causes the operator at current bytecode position to be reinterpreted.
		 * Called after a significant change to the bytecode.
		 * The stack must restored to the state before the current instruction.
		 */
		protected void reinterpret() {
			reinterpret = true;
		}

		// bytecode processing methods
		protected void process_nop() { }
		protected void process_aconst_null() { push(SIG_null); }
		protected void process_iconst_N(int val) { push(SIG_int); }
		protected void process_lconst_N(long val) { push(SIG_long); }
		protected void process_fconst_N(float val) { push(SIG_float); }
		protected void process_dconst_N(double val) { push(SIG_double); }
		protected void process_bipush(int val) { push(SIG_byte); }
		protected void process_sipush(int val) { push(SIG_short); }
		protected void process_ldc(int index, short[] changedSignatures) {
			// FIXME: this might not work correctly for class literals -- check
			if (index < changedSignatures.length && changedSignatures[index] != 0) {
				putShort(code, o+1, changedSignatures[index]);
				index = changedSignatures[index];
			}
			String type = constantPoolType(cf, index);
			push(type);
		}
		protected void process_ldc2(int index, short[] changedSignatures) {
			process_ldc(index, changedSignatures);
		}
		protected void process_Tload(String type, int arg) {
			assert (locals[arg] == type || (type == SIG_int && isInteger(locals[arg])));
			push(locals[arg]);
		}
		protected void process_aload(int arg, char[] remap, short[] reassignLocals) {
			if (reassignLocals[arg] != arg)
				putByte(code, o+1, reassignLocals[arg]);
			if (remap[1+arg] != '\0') {
				code[o] -= atopDelta(remap[1+arg], false);
				assert (locals[arg].charAt(0) == remap[1+arg]);
			}
			push(locals[arg]);
		}
		protected void process_aload_X(int arg, char[] remap, short[] reassignLocals) {
			if (reassignLocals[arg] != arg) {
				if (reassignLocals[arg] <= 3)
					putByte(code, o, BC_aload_0 + reassignLocals[arg]);
				else // FIXME: either use padding or allow inserting bytecodes
					throw new IllegalArgumentException("bytecode size change!!!");
			}
			if (remap[1+arg] != '\0') {
				code[o] -= atopDelta(remap[1+arg], false) * 4;
				assert (locals[arg].charAt(0) == remap[1+arg]);
			}
			push(locals[arg]);
		}
		protected void process_Taload(String type) {
			String index = pop();
			String array = pop();
			assert (isArrayOf(array, type));
			assert (isInteger(index));
			push(type);
		}
		protected void process_aaload() {
			String index = pop();
			String array = pop();
			assert (isArray(array));
			assert (isInteger(index));
			String base = elementType(array);
			if (isPrimitiveArray(array)) {
				code[o] -= atopDelta(base.charAt(0), true);
				push(base);
				return;
			}
			push(base);
		}
		protected void process_Tstore(String type, int arg) {
			String val = pop();
			assert (val == type || (type == SIG_int && isInteger(val)));
			locals[arg] = val;
		}
		protected void process_astore(int arg, char[] remap, short[] reassignLocals) {
			String val = pop();
			if (reassignLocals[arg] != arg)
				putByte(code, o+1, reassignLocals[arg]);
			if (isPrimitive(val)) {
				if (remap[1+arg] == '\0')
					remap[1+arg] = val.charAt(0);
				else // make sure we remap to the same type
					assert (remap[1+arg] == val.charAt(0));
			}
			locals[arg] = val;
			if (remap[1+arg] != '\0')
				code[o] -= atopDelta(remap[1+arg], false);
		}
		protected void process_astore_X(int arg, char[] remap, short[] reassignLocals) {
			String val = pop();
			if (reassignLocals[arg] != arg) {
				if (reassignLocals[arg] <= 3)
					putByte(code, o, BC_astore_0 + reassignLocals[arg]);
				else // FIXME: either use padding or allow inserting bytecodes
					throw new IllegalArgumentException("bytecode size change!!!");
			}
			if (isPrimitive(val)) {
				if (remap[1+arg] == '\0')
					remap[1+arg] = val.charAt(0);
				else
					assert (remap[1+arg] == val.charAt(0));
			}
			locals[arg] = val;
			if (remap[1+arg] != '\0')
				code[o] -= atopDelta(remap[1+arg], false) * 4;
		}
		protected void process_Tastore(String type) {
			String val = pop();
			String index = pop();
			String array = pop();
			assert (val == type);
			assert (isInteger(index));
			assert (isArrayOf(array, type));
		}
		protected void process_aastore() {
			String val = pop();
			String index = pop();
			String array = pop();
			assert (isArray(array));
			assert (isInteger(index));
			if (isPrimitiveArray(array)) {
				assert (isArrayOf(array, val));
				String base = elementType(array);
				code[o] -= atopDelta(base.charAt(0), true);
				return;
			}
			// TODO: do we need to do anything here?
			//assert (!isPrimitive(val));
			//assert (isArrayOf(array, val));
		}
		protected void process_pop() {
			pop();
		}
		protected void process_dup() {
			push(peek());
		}
		protected void process_dup_x1() {
			push(peek(1));
		}
		protected void process_dup_x2() {
			push(peek(2));
		}
		protected void process_swap() {
			String top = pop();
			String top1 = pop();
			push(top);
			push(top1);
		}
		protected void process_binary(String type) {
			assert (isPrimitive(type));
			String a2 = pop();
			String a1 = pop();
			assert (a1 == type && a2 == type);
			push(type);
		}
		protected void process_unary(String type) {
			assert (isPrimitive(type));
			String val = pop();
			assert (val == type);
			push(type);
		}
		protected void process_iinc(int arg, int inc) {
			assert (isInteger(locals[arg]));
		}
		protected void process_T2U(String from, String to) {
			assert (isPrimitive(from) && isPrimitive(to));
			String val = pop();
			assert (val == from);
			push(to);
		}
		protected void process_Tcmp(String type) {
			String v2 = pop();
			String v1 = pop();
			assert ((v1 == type && v2 == type) || (type == SIG_int && isInteger(v1) && isInteger(v2)));
			push(SIG_int);
		}
		protected void process_ifC(int offset) {
			String val = pop();
			assert (isInteger(val));
			assert (depth[o - off + offset] == 0 || sp == depth[o - off + offset]);
			depth[o - off + offset] = sp;
		}
		protected void process_if_icmpC(int offset) {
			String v2 = pop();
			String v1 = pop();
			assert (isInteger(v1) && isInteger(v2));
			assert (depth[o - off + offset] == 0 || sp == depth[o - off + offset]);
			depth[o - off + offset] = sp;
		}
		protected void process_if_acmpC(int offset) {
			int c = getByte(code, o);
			// FIXME: HACK: Add padding instructions by casting the expression to
			// boolean (e.g., "if ((boolean)(expr))" and hope javac doesn't
			// optimize it away.
			// With javac, that adds 8 bytes (only need 1 for lcmp/fcmp*/dcmp*).
			// This worked on Windows with Sun 1.5, IBM 1.5, IBM 1.6.
			// TODO: test on a Mac, Linux, and AIX (should be same, it's Java).
			// TODO: test jikes.
			String v1 = pop();
			String v2 = pop();
			char r = '\0';
			if (isPrimitive(v1)) {
				assert (v2 == v1);
				r = v1.charAt(0);
			}
			if (r != '\0') {
				assert ((code[o+3] & 0xFF) == BC_iconst_1
				     && (code[o+4] & 0xFF) == BC_goto
				     && (code[o+7] & 0xFF) == BC_iconst_0
				     && (code[o+8] & 0xFF) == BC_ifeq);
				switch (r) {
				case 'I':
				case 'B':
				case 'C':
				case 'S':
				case 'Z':
					putByte(code, o, BC_if_icmpeq + c - BC_if_acmpeq);
					assert (depth[o - off + offset] == 0 || sp == depth[o - off + offset]);
					depth[o - off + offset] = sp;
					return;
				case 'D':
					putByte(code, o, BC_dcmpl);
					break;
				case 'F':
					putByte(code, o, BC_fcmpl);
					break;
				case 'J':
					putByte(code, o, BC_lcmp);
					break;
				}
				for (int x = 1; x < 8; x++) putByte(code, o+x, BC_nop);
				putByte(code, o+8, BC_ifeq + c - BC_if_acmpeq);
				push(v1);
				push(v2);
				reinterpret();
				return;
			}
			assert (depth[o - off + offset] == 0 || sp == depth[o - off + offset]);
			depth[o - off + offset] = sp;
		}
		protected void process_goto(int offset) {
			int l = getBytecodeLength(o);
			assert (depth[o - off + offset] == 0 || sp == depth[o - off + offset]);
			depth[o - off + offset] = sp; 
			sp = depth[o - off + l];
		}
		protected void process_return() {
			int l = getBytecodeLength(o);
			if (o < len-1)
				sp = depth[o - off + l];
		}
		protected void process_ifCnull(int offset) {
			String val = pop();
			// TODO: check object type?
			assert (!isPrimitive(val));
			assert (depth[o - off + offset] == 0 || sp == depth[o - off + offset]);
			depth[o - off + offset] = sp;
		}
		/**
		 * Returns the array of offsets for the tableswitch instruction.
		 * The first 3 elements are, respectively, default offset, low, and high.
		 */
		private final int[] getTableswitchOffsets(byte[] code, int o) {
			int pad = 3 - (o - 1 - off)%4;
			int df = getInt(code, o+pad);
			int lo = getInt(code, o+pad+4);
			int hi = getInt(code, o+pad+8);
			int[] offsets = new int[3+hi-lo+1];
			offsets[0] = df;
			offsets[1] = lo;
			offsets[2] = hi;
			for (int i = 3; i < offsets.length; i++)
				offsets[i] = getInt(code, o+pad+12+(i-3)*4);
			return offsets;
		}
		protected void process_tableswitch(int[] offsets) {
			String val = pop();
			assert (isInteger(val));
			assert (depth[o - off + offsets[0]] == 0 || sp == depth[o - off + offsets[0]]);
			depth[o - off + offsets[0]] = sp;
			for (int i = 3; i < offsets.length; i++) {
				assert (depth[o - off + offsets[i]] == 0 || sp == depth[o - off + offsets[i]]);
				depth[o - off + offsets[i]] = sp;
			}
			int l = getBytecodeLength(o);
			sp = depth[o - off + l];
		}
		/**
		 * Returns the array of pairs for the lookupswitch instruction.
		 * The first element is the default offset.
		 */
		private final int[] getLookupswitchPairs(byte[] code, int o) {
			int pad = 3 - (o - 1 - off)%4;
			int df = getInt(code, o+pad);
			int num = getInt(code, o+pad+4);
			int[] pairs = new int[1+num*2];
			pairs[0] = df;
			for (int i = 1; i < pairs.length; i+=2) {
				pairs[i] = getInt(code, o+pad+8+(i-1)*4);
				pairs[i+1] = getInt(code, o+pad+8+i*4);
			}
			return pairs;
		}
		protected void process_lookupswitch(int[] pairs) {
			String val = pop();
			assert (isInteger(val));
			assert (depth[o - off + pairs[0]] == 0 || sp == depth[o - off + pairs[0]]);
			depth[o - off + pairs[0]] = sp;
			for (int i = 1; i < pairs.length; i+=2) {
				assert (depth[o - off + pairs[i+1]] == 0 || sp == depth[o - off + pairs[i+1]]);
				depth[o - off + pairs[i+1]] = sp;
			}
			int l = getBytecodeLength(o);
			sp = depth[o - off + l];
		}
		protected void process_Treturn(String type) {
			String val = pop();
			assert (val == type || (type == SIG_int && isInteger(val)));
			int l = getBytecodeLength(o);
			if (o < len-1)
				sp = depth[o - off + l];
		}
		protected void process_areturn(char[] remap) {
			String val = pop();
			if (remap[0] != '\0')
				code[o] -= atopDelta(remap[0], false);
			int l = getBytecodeLength(o);
			if (o < len-1)
				sp = depth[o - off + l];
		}
		// FIXME: what about static fields in parameterized types (e.g., List[T].f)?
		private final String processFieldAccess(short field_ref_index, String receiver, String[] formals, String[] actuals) {
			short class_index = cf.getClassIndex(field_ref_index);
			short container_index = cf.getNameIndex(class_index);
			short name_and_type_index = cf.getNameAndTypeIndex(field_ref_index);
			short name_index = cf.getNameIndex(name_and_type_index);
			short descriptor_index = cf.getDescriptorIndex(name_and_type_index);
			String container = cf.getString(container_index);
			String name = cf.getString(name_index);
			String descriptor = cf.getString(descriptor_index);
			if (VERBOSE)
				System.out.println("Access to "+container+"."+name+" "+descriptor);
			String newContainer = container;
			String newDescriptor = descriptor;
			String[] newFormals = getFormals(class_index, cf, formals);
			boolean isParameterized = newFormals != null && newFormals.length > 0;
			if (isParameterized) {
				String[] params = actuals;
				if (receiver != null) { // get the type from the stack
					if (!isNull(receiver))
						params = demangle(typeFromSignature(receiver));
				} else {
					// FIXME: what to do about static fields, e.g., List[T].f?
					assert (cf.isInstance(class_index) || newFormals == null || newFormals.length == 0);
				}
				newContainer = mangle(container, actuals);
				String[] fieldActuals = getFieldTypeParms(field_ref_index, cf);
				if (fieldActuals != null) {
					String[] fieldFormals = getFormals(class_index, cf, formals);
					assert (fieldFormals.length == fieldActuals.length);
					newDescriptor = typeToSignature(mangle(typeFromSignature(newDescriptor), fieldActuals));
				}
				newDescriptor = instantiateType(newDescriptor, newFormals, params);
				short new_field_ref = createFieldRef(cf, newContainer, name, newDescriptor);
				if (new_field_ref != field_ref_index) {
					short new_descriptor_index = cf.getDescriptorIndex(cf.getNameAndTypeIndex(new_field_ref));
					if (new_descriptor_index != descriptor_index) {
						if (VERBOSE)
							System.out.println("\t   ->" + newDescriptor);
					}
					if (cf.isInstance(class_index))
						cf.addInstance(cf.getClassIndex(new_field_ref));
					putShort(code, o+1, new_field_ref);
				}
			}
			return newDescriptor;
		}
		protected void process_getF(short field_ref_index, boolean isStatic, String signature, String[] actuals, String[] formals) {
			String receiver = null;
			if (!isStatic)
				receiver = pop(); // TODO: check type?
			String descriptor = processFieldAccess(field_ref_index, receiver, formals, actuals);
			push(descriptor);
		}
		protected void process_putF(short field_ref_index, boolean isStatic, String signature, String[] actuals, String[] formals) {
			String val = pop();
			String receiver = null;
			if (!isStatic)
				receiver = pop(); // TODO: check type?
			// TODO: check value type?
			String descriptor = processFieldAccess(field_ref_index, receiver, formals, actuals);
		}
		private int extractClassParameters(int x, String[] formals, String[] actuals, String[] result) {
			// TODO: handle >7 parameters as a Class[] constructor
			for (int i = 0; i < result.length; i++) {
				int cload = getByte(code, o+x);
				int l = getBytecodeLength(o+x);
				assert (cload == BC_ldc || cload == BC_ldc_w || cload == BC_getstatic);
				String signature = null;
				if (cload == BC_getstatic) { // primitive type
					int field_ref_index = getShort(code, o+x+1);
					short container_index = cf.getNameIndex(cf.getClassIndex(field_ref_index));
					short name_and_type_index = cf.getNameAndTypeIndex(field_ref_index);
					short id_index = cf.getNameIndex(name_and_type_index);
					short descriptor_index = cf.getDescriptorIndex(name_and_type_index);
					String container = cf.getString(container_index);
					String id = cf.getString(id_index);
					String descriptor = cf.getString(descriptor_index);
					assert (id.equals("TYPE") && descriptor.equals(SIG_Class));
					signature = wrapperToSignature(container);
					assert (isPrimitive(signature));
				} else { // must be ldc or ldc_w -- a class
					int class_index = (cload == BC_ldc) ? getByte(code, o+x+1) : getShort(code, o+x+1);
					String newSignature = typeToSignature(cf.getString(cf.getNameIndex(class_index)));
					int c = getByte(code, o+x+l);
					if (c == BC_invokevirtual) {
						int method_ref_index = getShort(code, o+x+l+1);
						int name_and_type_index = cf.getNameAndTypeIndex(method_ref_index);
						short cont_index = cf.getNameIndex(cf.getClassIndex(method_ref_index));
						assert (cf.getString(cont_index).equals("java/lang/Class"));
						assert (cf.getString(cf.getNameIndex(name_and_type_index)).equals("getClassLoader")
						     && cf.getString(cf.getDescriptorIndex(name_and_type_index)).equals("()Ljava/lang/ClassLoader;"));
						int cc = getByte(code, o+x+l+3);
						int type_index = getShort(code, o+x+l+4);
						assert (cc == BC_checkcast && cf.getString(cf.getNameIndex(type_index)).equals("x10/runtime/X10RuntimeClassloader"));
						int ld = getByte(code, o+x+l+6);
						assert (ld == BC_ldc || ld == BC_ldc_w);
						int str_idx = (ld == BC_ldc) ? getByte(code, o+x+l+7) : getShort(code, o+x+l+7);
						int ln = getBytecodeLength(o+x+l+6);
						int iv = getByte(code, o+x+l+6+ln);
						int meth_ref_idx = getShort(code, o+x+l+7+ln);
						int cont_idx = cf.getNameIndex(cf.getClassIndex(meth_ref_idx));
						int name_type_idx = cf.getNameAndTypeIndex(meth_ref_idx);
						assert (iv == BC_invokevirtual
						     && cf.getString(cont_idx).equals("x10/runtime/X10RuntimeClassloader")
						     && cf.getString(cf.getNameIndex(name_type_idx)).equals("getClass")
						     && cf.getString(cf.getDescriptorIndex(name_type_idx)).equals("("+SIG_String+")"+SIG_Class));
						newSignature = typeToSignature(cf.getString(cf.getNameIndex(str_idx)));
						for (int j = l; j < l+9+ln; j++) putByte(code, o+x+j, BC_nop); // Clear assignment
						x += 6+ln;
					}
					signature = instantiateType(newSignature, formals, actuals);
				}
				result[i] = typeFromSignature(signature);
				// TODO: clear type arguments off the stack (need to also rewrite method signatures)
				//for (int j = 0; j < l; j++) putByte(code, o+x+j, BC_nop); // Clear assignment
				x += l;
			}
			return x;
		}
		// FIXME: what about static methods in parameterized types (e.g., List[T].create)?
		protected void process_invoke(short method_ref_index, String[] formals, String[] actuals, int c) {
			boolean isStatic = c == BC_invokestatic;
			boolean isInterface = c == BC_invokeinterface;
			short class_index = cf.getClassIndex(method_ref_index);
			short container_index = cf.getNameIndex(class_index);
			short name_and_type_index = cf.getNameAndTypeIndex(method_ref_index);
			short name_index = cf.getNameIndex(name_and_type_index);
			short descriptor_index = cf.getDescriptorIndex(name_and_type_index);
			String container = cf.getString(container_index);
			String name = cf.getString(name_index);
			String signature = cf.getString(descriptor_index);
			if (VERBOSE)
				System.out.println("Call to "+container+"."+name+signature);
			String[] types = methodArguments(container, signature, isStatic);
			String[] args = new String[types.length];
			for (int i = args.length-1; i >= 0; i--)
				args[i] = pop();
			// TODO: check arg types?
			String newContainer = container;
			String newSignature = signature;
			String t;
			String[] newFormals;
			// FIXME: HACK -- special case StringBuilder.append
			if (!isInterface && !isStatic &&
					container.equals("java/lang/StringBuilder") && name.equals("append") &&
					signature.equals("("+SIG_Object+")"+SIG_StringBuilder))
			{
				assert(args.length == 2);
				if (isPrimitive(args[1])) {
					newSignature = "(" + args[1] + ")"+SIG_StringBuilder;
					short new_method_ref = createMethodRef(cf, container, name, newSignature, isInterface);
					putShort(code, o+1, new_method_ref);
					push(SIG_StringBuilder);
					return;
				}
			} else if (isStatic && container.equals("x10/runtime/Runtime") &&
					   name.equals(COERCE_METHOD) &&
					   (signature.equals("("+args[0]+")"+SIG_Object) ||
						signature.equals("("+SIG_Object+")"+SIG_Object)))
			{
				int x = 3;
				int cc = getByte(code, o+x);
				short cr = getShort(code, o+x+1);
				short cni = cf.getNameIndex(cr);
				assert (cc == BC_checkcast/* && (formals.length == 0 ||
						getFormal(typeToSignature(cf.getString(cni)), formals) != -1)*/);
				for (int j = 0; j < x+3; j++) putByte(code, o+j, BC_nop); // Clear assignment
				push(args[0]);
				reinterpret();
				return;
			} else if (isStatic && container.equals("x10/runtime/Runtime") &&
					   name.startsWith(RECOVER_PREFIX) &&
					   isPrimitive(t = name.substring(RECOVER_PREFIX.length())) &&
					   signature.equals("("+SIG_Object+")"+t))
			{
				int x = 3;
				for (int j = 0; j < x; j++) putByte(code, o+j, BC_nop); // Clear assignment
				push(args[0]);
				reinterpret();
				return;
			} else if (!isInterface && name.startsWith(PARAMETRIC_METHOD_PREFIX) &&
			           (newFormals = getMethodFormals(method_ref_index, cf)) != null &&
			           signature.equals("("+replicate(SIG_Class,newFormals.length)+")"+SIG_Object))
			{
				// FIXME: HACK expect 2 checkcasts -- rewrite one, but leave the other
				int x = 3;
				int cc = getByte(code, o+x);
				short cr = getShort(code, o+x+1);
				short cni = cf.getNameIndex(cr);
				String type = cf.getString(cni);
				assert (cc == BC_checkcast &&
						(createNestedType(container, name.substring(PARAMETRIC_METHOD_PREFIX.length()))).equals(type));
				x += 3;
				int cc1 = getByte(code, o+x);
				short cr1 = getShort(code, o+x+1);
				short cni1 = cf.getNameIndex(cr1);
				String type1 = cf.getString(cni1);
				assert (cc1 == BC_checkcast &&
						(createNestedType(container, name.substring(PARAMETRIC_METHOD_PREFIX.length()))).equals(type1));
				assert (cr1 == cr && type1.equals(type));
				x += 3;
				String[] params = new String[newFormals.length];
				x = extractClassParameters(x, formals, actuals, params);
				newSignature = "("+replicate(SIG_Class,params.length)+"Z)"+SIG_Object;
				short new_method_ref = createMethodRef(cf, container, name, newSignature, isInterface);
				assert (new_method_ref != method_ref_index);
				String newType = mangle(type, params);
				short new_type_ref = createClassRef(cf, newType);
				putByte(code, o, BC_iconst_1);
				putByte(code, o+1, c);
				putShort(code, o+2, new_method_ref);
				for (int j = 4; j < 6; j++) putByte(code, o+j, BC_nop); // Clear checkcast
				putShort(code, o+7, new_type_ref);
				for (int i = 0; i < args.length; i++)
					push(args[i]);
				reinterpret();
				return;
			}
			newFormals = getFormals(class_index, cf, formals);
			boolean isParameterized = newFormals != null && newFormals.length > 0;
			if (isParameterized) {
				// TODO: retrieve the annotations and make sure the method is annotated Synthetic
				if (isStatic && name.equals(INSTANCEOF_METHOD) && signature.equals(INSTANCEOF_SIG)) {
					int x = 3;
					String[] params = new String[newFormals.length];
					x = extractClassParameters(x, formals, actuals, params);
					newContainer = mangle(container, params);
					newSignature = INSTANCEOF1_SIG; // Add a boolean arg
					short new_method_ref = createMethodRef(cf, newContainer, name, newSignature, isInterface);
					assert (new_method_ref != method_ref_index);
					if (cf.isInstance(class_index))
						cf.addInstance(cf.getClassIndex(new_method_ref));
					int is = getByte(code, o+x);
					short mr = getShort(code, o+x+1);
					short ci = cf.getNameIndex(cf.getClassIndex(mr));
					short nati = cf.getNameAndTypeIndex(mr);
					short ni = cf.getNameIndex(nati);
					short di = cf.getDescriptorIndex(nati);
					assert (is == BC_invokestatic &&
							cf.getString(ci).equals("x10/runtime/Runtime") &&
							cf.getString(ni).equals(INSTANCEOF_METHOD) &&
							cf.getString(di).equals("("+SIG_boolean+replicate(SIG_Class,params.length)+")"+SIG_boolean));
					putByte(code, o, BC_swap);
					putByte(code, o+1, BC_dup_x1);
					putByte(code, o+2, BC_instanceof);
					short new_class_index = createClassRef(cf, newContainer);
					putShort(code, o+3, new_class_index);
					putByte(code, o+5, BC_invokestatic);
					putShort(code, o+6, new_method_ref);
					for (int j = 8; j < x+3; j++) putByte(code, o+j, BC_nop); // Clear assignment
					push(args[0]);
					push(args[1]);
					reinterpret();
					return;
				} else if (isStatic && name.equals(CAST_METHOD) && signature.equals(CAST_SIG)) {
					int x = 3;
					if (getByte(code, o+x) != BC_nop) { // already processed
						String[] params = new String[newFormals.length];
						x = extractClassParameters(x, formals, actuals, params);
						newContainer = mangle(container, params);
						short new_method_ref = createMethodRef(cf, newContainer, name, signature, isInterface);
						assert (new_method_ref != method_ref_index);
						if (cf.isInstance(class_index))
							cf.addInstance(cf.getClassIndex(new_method_ref));
						putShort(code, o+1, new_method_ref);
						int is = getByte(code, o+x);
						short mr = getShort(code, o+x+1);
						short ci = cf.getNameIndex(cf.getClassIndex(mr));
						short nati = cf.getNameAndTypeIndex(mr);
						short ni = cf.getNameIndex(nati);
						short di = cf.getDescriptorIndex(nati);
						assert (is == BC_invokestatic &&
								cf.getString(ci).equals("x10/runtime/Runtime") &&
								cf.getString(ni).equals(CAST_METHOD) &&
								cf.getString(di).equals("("+SIG_Object+replicate(SIG_Class,params.length)+")"+SIG_Object));
						int cc = getByte(code, o+x+3);
						short cr = getShort(code, o+x+4);
						short cni = cf.getNameIndex(cr);
						assert (cc == BC_checkcast &&
								cf.getString(cni).equals(container));
						for (int j = 3; j < x+6; j++) putByte(code, o+j, BC_nop); // Clear assignment
						push(args[0]);
						push(args[1]);
						reinterpret();
						return;
					}
				} else if (!name.equals(INSTANCEOF_METHOD) && !name.startsWith(PARAMETRIC_METHOD_PREFIX))
				{ // FIXME: else already mangled
					String[] params = actuals;
					if (!isStatic) { // get the type from the stack
						if (!isNull(args[0]))
							params = demangle(typeFromSignature(args[0]));
					} else {
						// FIXME: what to do about static methods, e.g., List[T].create?
						assert (cf.isInstance(class_index) || newFormals == null || newFormals.length == 0);
					}
					newContainer = mangle(container, params);
					MethodSig sig = MethodSig.fromSignature(signature);
					MethodSig newSig = sig;
					newSig = instantiateSignature(newSig, newFormals, params);
					String[] methodActuals = getMethodTypeParms(method_ref_index, cf);
					if (methodActuals != null) {
						String[] methodFormals = getMethodFormals(method_ref_index, cf);
						assert (methodFormals.length == methodActuals.length);
						newSig.returnType = typeToSignature(mangle(typeFromSignature(newSig.returnType), methodActuals));
					}
					int adjust = isStatic ? 0 : 1;
					for (int p = adjust; p < args.length; p++) {
						String[] methodParmActuals = null;
						if (isNull(args[p])) {
							methodParmActuals = getMethodParamTypeParms(method_ref_index, p, cf);
							if (methodParmActuals != null) {
								String[] parmFormals = getFormals(method_ref_index, cf, formals);
								assert (parmFormals.length == methodParmActuals.length);
								newSig.argTypes[p - adjust] = typeToSignature(mangle(typeFromSignature(newSig.argTypes[p - adjust]), methodParmActuals));
							}
						} else {
							methodParmActuals = demangle(typeFromSignature(args[p]));
						}
						newSig.argTypes[p-adjust] = typeToSignature(mangle(typeFromSignature(newSig.argTypes[p-adjust]), methodParmActuals));
					}
					// FIXME: instantiate twice because we've already changed the method
					newSig = instantiateSignature(newSig, newFormals, params);
					newSignature = newSig.toSignature();
					short new_method_ref = createMethodRef(cf, newContainer, name, newSignature, isInterface);
					if (new_method_ref != method_ref_index) {
						short new_descriptor_index = cf.getDescriptorIndex(cf.getNameAndTypeIndex(new_method_ref));
						if (new_descriptor_index != descriptor_index) {
							if (VERBOSE)
								System.out.println("\t   ->" + newSignature);
						}
						if (cf.isInstance(class_index))
							cf.addInstance(cf.getClassIndex(new_method_ref));
						putShort(code, o+1, new_method_ref);
					}
				}
				// TODO: do we need to do this separately?
//				if (!isInterface && !isStatic && name.equals(INIT)) { // Constructor
//					String[] params = demangle(typeFromSignature(args[0]));
//					String newContainer = mangle(container, params);
//					// FIXME: rewrite signatures if clearing type args off the stack
//					//int syn_idx = newSignature.indexOf(SIG_Class);
//					//assert (syn_idx != -1);
//					//int end_idx = syn_idx+SIG_Class.length();
//					//for (int f = end_idx;
//					//	 (f = newSignature.indexOf(SIG_Class, f)) != -1;
//					//	 end_idx = f+SIG_Class.length(), f = end_idx+1)
//					//	;
//					//assert (end_idx != -1);
//					//newSignature = newSignature.substring(0, syn_idx)+newSignature.substring(end_idx);
//					short new_method_ref = createMethodRef(cf, newContainer, name, newSignature, isInterface);
//					if (new_method_ref != method_ref_index)
//						putShort(code, o+1, new_method_ref);
//				}
			}
			String retType = methodReturnType(newContainer, newSignature, isStatic);
			if (retType != SIG_void)
				push(retType);
		}
		protected void process_new(int type_index, String[] formals, String[] actuals) {
			short name_index = cf.getNameIndex(type_index);
			String type = cf.getString(name_index);
			String[] newFormals = getFormals(type_index, cf, formals);
			boolean isParameterized = newFormals != null && newFormals.length > 0;
			if (VERBOSE)
				System.out.println("Creating instance of "+type+(isParameterized?" (parameterized)":""));
			if (isParameterized) {
				String[] params = new String[newFormals.length];
				assert (getByte(code, o+3) == BC_dup);
				int x = 4;
				x = extractClassParameters(x, formals, actuals, params);
				type = mangle(type, params);
				if (VERBOSE)
					System.out.println("\t   -> "+type);
				short new_class_ref = createClassRef(cf, type);
				if (new_class_ref != type_index) {
					putShort(code, o+1, new_class_ref);
					if (cf.isInstance(type_index))
						cf.addInstance(new_class_ref);
				}
			}
			push(typeToSignature(type));
		}
		protected void process_newarray(int type) {
			String count = pop();
			assert (isInteger(count));
			push(arrayOf(PRIM_ARRAY_TO_JAVA_TYPE[type]));
		}
		protected void process_anewarray(int base_type_index, String[] formals, String[] actuals) {
			short name_index = cf.getNameIndex(base_type_index);
			String base = cf.getString(name_index);
			if (VERBOSE)
				System.out.println("Array of "+base);
			String count = pop();
			assert (isInteger(count));
			String signature = typeToSignature(base);
			String newSignature = signature;
			String[] newFormals = getFormals(base_type_index, cf, formals);
			boolean isParameterized = newFormals != null && newFormals.length > 0;
			if (isParameterized) {
				int x = 3;
				String[] params = new String[newFormals.length];
				x = extractClassParameters(x, formals, actuals, params);
				String newBase = mangle(base, params);
				newSignature = typeToSignature(newBase);
//				short new_class_ref = createClassRef(cf, newBase);
//				assert (new_class_ref != base_type_index);
//				putShort(code, o+1, new_class_ref);
				int is = getByte(code, o+x);
				short mr = getShort(code, o+x+1);
				short ci = cf.getNameIndex(cf.getClassIndex(mr));
				short nati = cf.getNameAndTypeIndex(mr);
				short ni = cf.getNameIndex(nati);
				short di = cf.getDescriptorIndex(nati);
				assert (is == BC_invokestatic &&
						cf.getString(ci).equals("x10/runtime/Runtime") &&
						cf.getString(ni).equals(NEWARRAY_METHOD) &&
						cf.getString(di).equals("("+SIG_Object+replicate(SIG_Class,params.length)+")"+SIG_Object));
				int cc = getByte(code, o+x+3);
				short cr = getShort(code, o+x+4);
				short cni = cf.getNameIndex(cr);
				assert (cc == BC_checkcast &&
						cf.getString(cni).equals(arrayOf(typeToSignature(base))));
				int cc1 = getByte(code, o+x+6);
				short cr1 = getShort(code, o+x+7);
				short cni1 = cf.getNameIndex(cr1);
				assert (cc1 == BC_checkcast &&
						cf.getString(cni1).equals(arrayOf(typeToSignature(base))));
				for (int j = 3; j < x+9; j++) putByte(code, o+j, BC_nop); // Clear assignment
			}
			newSignature = instantiateType(newSignature, formals, actuals);
			if (isPrimitive(newSignature)) { // cannot be the same
				if (VERBOSE)
					System.out.println("\t   -> "+typeFromSignature(newSignature)+"[]");
				putByte(code, o, BC_newarray);
				byte array = JavaTypeToPrimArray(newSignature);
				assert (array != 0);
				putByte(code, o+1, array);
				putByte(code, o+2, BC_nop);
				push(count);
				reinterpret();
				return;
			} else {
				String newBase = constantPoolTypeFromSignature(newSignature);
				short new_type_index = createClassRef(cf, newBase);
				if (new_type_index != base_type_index) {
					if (VERBOSE)
						System.out.println("\t   -> "+newBase+"[]");
					putShort(code, o+1, new_type_index);
				}
			}
			push(arrayOf(newSignature));
		}
		protected void process_multianewarray(int type_index, int dimensions, String[] formals, String[] actuals) {
			short name_index = cf.getNameIndex(type_index);
			String type = cf.getString(name_index);
			for (int i = 0; i < dimensions; i++) {
				String count = pop();
				assert (isInteger(count));
			}
			if (VERBOSE)
				System.out.println("Multi-dimensional array of "+type);
			String signature = typeToSignature(type);
			String newSignature = signature;
			String base = extractType(signature);
			String[] newFormals = getFormals(base);
			boolean isParameterized = newFormals != null && newFormals.length > 0;
			if (isParameterized) {
				int x = 4;
				String[] params = new String[newFormals.length];
				x = extractClassParameters(x, formals, actuals, params);
				String newBase = mangle(base, params);
				newSignature = typeToSignature(newBase);
				for (int i = 0; i < signature.length() && signature.charAt(i) == '['; i++)
					newSignature = "[" + newSignature;
//				short new_class_ref = createClassRef(cf, newBase);
//				assert (new_class_ref != type_index);
//				putShort(code, o+1, new_class_ref);
				int is = getByte(code, o+x);
				short mr = getShort(code, o+x+1);
				short ci = cf.getNameIndex(cf.getClassIndex(mr));
				short nati = cf.getNameAndTypeIndex(mr);
				short ni = cf.getNameIndex(nati);
				short di = cf.getDescriptorIndex(nati);
				assert (is == BC_invokestatic &&
						cf.getString(ci).equals("x10/runtime/Runtime") &&
						cf.getString(ni).equals(NEWARRAY_METHOD) &&
						cf.getString(di).equals("("+SIG_Object+replicate(SIG_Class,params.length)+")"+SIG_Object));
				int cc = getByte(code, o+x+3);
				short cr = getShort(code, o+x+4);
				short cni = cf.getNameIndex(cr);
				assert (cc == BC_checkcast &&
						cf.getString(cni).equals(signature));
				short cr1 = getShort(code, o+x+7);
				short cni1 = cf.getNameIndex(cr1);
				assert (cc == BC_checkcast &&
						cf.getString(cni1).equals(signature));
				for (int j = 4; j < x+9; j++) putByte(code, o+j, BC_nop); // Clear assignment
			}
			newSignature = instantiateType(newSignature, formals, actuals);
			assert (!isPrimitive(newSignature));
			String newType = constantPoolTypeFromSignature(newSignature);
			short new_type_index = createClassRef(cf, newType);
			if (new_type_index != type_index) {
				if (VERBOSE)
					System.out.println("\t   -> "+newType);
				putShort(code, o+1, new_type_index);
			}
			push(type);
		}
		protected void process_arraylength() {
			String val = pop();
			// TODO: check array type
			assert (isArray(val));
			push(SIG_int);
		}
		protected void process_athrow() {
			String val = pop();
			// TODO: check exception type
			int l = getBytecodeLength(o);
			if (o < len-1)
				sp = depth[o - off + l];
		}
		protected void process_monitorOP() {
			String val = pop();
			assert (!isPrimitive(val));
		}
		protected void process_checkcast(short type_ref_index, String[] formals, String[] actuals) {
			short type_index = cf.getNameIndex(type_ref_index);
			String type = cf.getString(type_index);
			if (VERBOSE)
				System.out.println("Checkcast to "+type);
			if (type_ref_index == cf.this_class) {
				// Raw reference to current type -- all others would have been done via calls
				if (VERBOSE)
					System.out.println("\t   (raw)");
				type = mangle(type, actuals);
				if (VERBOSE)
					System.out.println("\t   -> "+type);
				short new_type_ref = createClassRef(cf, type);
				if (new_type_ref != type_ref_index) {
					putShort(code, o+1, new_type_ref);
					assert (cf.isInstance(new_type_ref));
				}
			}
			String val = pop();
			// TODO: check type?
			push(typeToSignature(type));
		}
		protected void process_instanceof(short type_ref_index, String[] formals, String[] actuals) {
			short type_index = cf.getNameIndex(type_ref_index);
			String type = cf.getString(type_index);
			if (VERBOSE)
				System.out.println("Instanceof test for "+type);
			if (type_index == cf.this_class) {
				// Raw reference to current type -- all others would have been done via calls
				if (VERBOSE)
					System.out.println("\t   (raw)");
				type = mangle(type, actuals);
				if (VERBOSE)
					System.out.println("\t   -> "+type);
				short new_type_ref = createClassRef(cf, type);
				if (new_type_ref != type_index) {
					putShort(code, o+1, new_type_ref);
					assert (cf.isInstance(new_type_ref));
				}
			}
			String val = pop();
			// TODO: check type?
			push(SIG_int);
		}

		public void interpret(String signature, String[] actuals, String[] formals, short[] changedSignatures, char[] remap, short[] reassignLocals) {
			// Process bytecodes
			for (o = off; o < off + len; o++) {
				assert (depth[o - off] == 0 || sp == depth[o - off]);
				int h = findExceptionHandler(o - off);
				if (h >= 0) {
					assert (sp == 1);
					pop();
					// FIXME: what about parameterized exceptions (e.g., catch (MyError[T] e))?
					push(typeToSignature(cf.getString(cf.getNameIndex(getCaughtExceptionIndex(h)))));
				}
				int c = getByte(code, o);
				switch (c) {
				/* Ignored instructions */
				case BC_nop:            process_nop(); break;
				case BC_goto:           process_goto(getShort(code, o+1)); break;
				case BC_goto_w:         process_goto(getInt(code, o+1)); break;
				case BC_return:         process_return(); break;
				/* Constants */
				case BC_aconst_null:    process_aconst_null(); break;
				case BC_iconst_m1:
				case BC_iconst_0:
				case BC_iconst_1:
				case BC_iconst_2:
				case BC_iconst_3:
				case BC_iconst_4:
				case BC_iconst_5:       process_iconst_N(c - BC_iconst_0); break;
				case BC_lconst_0:
				case BC_lconst_1:       process_lconst_N(c - BC_lconst_0); break;
				case BC_fconst_0:
				case BC_fconst_1:
				case BC_fconst_2:       process_fconst_N(c - BC_fconst_0); break;
				case BC_dconst_0:
				case BC_dconst_1:       process_dconst_N(c - BC_dconst_0); break;
				case BC_bipush:         process_bipush(getByte(code, o+1)); break;
				case BC_sipush:         process_sipush(getShort(code, o+1)); break;
				case BC_ldc:            process_ldc(getByte(code, o+1), changedSignatures); break;
				case BC_ldc_w:          process_ldc(getShort(code, o+1), changedSignatures); break;
				case BC_ldc2_w:         process_ldc2(getShort(code, o+1), changedSignatures); break;
				/* Local loads */
				case BC_iload:          process_Tload(SIG_int, getByte(code, o+1)); break;
				case BC_iload_0:
				case BC_iload_1:
				case BC_iload_2:
				case BC_iload_3:        process_Tload(SIG_int, c - BC_iload_0); break;
				case BC_lload:          process_Tload(SIG_long, getByte(code, o+1)); break;
				case BC_lload_0:
				case BC_lload_1:
				case BC_lload_2:
				case BC_lload_3:        process_Tload(SIG_long, c - BC_lload_0); break;
				case BC_fload:          process_Tload(SIG_float, getByte(code, o+1)); break;
				case BC_fload_0:
				case BC_fload_1:
				case BC_fload_2:
				case BC_fload_3:        process_Tload(SIG_float, c - BC_fload_0); break;
				case BC_dload:          process_Tload(SIG_double, getByte(code, o+1)); break;
				case BC_dload_0:
				case BC_dload_1:
				case BC_dload_2:
				case BC_dload_3:        process_Tload(SIG_double, c - BC_dload_0); break;
				case BC_aload:          process_aload(getByte(code, o+1), remap, reassignLocals); break;
				case BC_aload_0:
				case BC_aload_1:
				case BC_aload_2:
				case BC_aload_3:        process_aload_X(c - BC_aload_0, remap, reassignLocals); break;
				/* Array loads */
				case BC_iaload:         process_Taload(SIG_int); break;
				case BC_laload:         process_Taload(SIG_long); break;
				case BC_faload:         process_Taload(SIG_float); break;
				case BC_daload:         process_Taload(SIG_double); break;
				case BC_baload:         process_Taload(SIG_byte); break;
				case BC_caload:         process_Taload(SIG_char); break;
				case BC_saload:         process_Taload(SIG_short); break;
				case BC_aaload:         process_aaload(); break;
				/* Local stores */
				case BC_istore:         process_Tstore(SIG_int, getByte(code, o+1)); break;
				case BC_istore_0:
				case BC_istore_1:
				case BC_istore_2:
				case BC_istore_3:       process_Tstore(SIG_int, c - BC_istore_0); break;
				case BC_lstore:         process_Tstore(SIG_long, getByte(code, o+1)); break;
				case BC_lstore_0:
				case BC_lstore_1:
				case BC_lstore_2:
				case BC_lstore_3:       process_Tstore(SIG_long, c - BC_lstore_0); break;
				case BC_fstore:         process_Tstore(SIG_float, getByte(code, o+1)); break;
				case BC_fstore_0:
				case BC_fstore_1:
				case BC_fstore_2:
				case BC_fstore_3:       process_Tstore(SIG_float, c - BC_fstore_0); break;
				case BC_dstore:         process_Tstore(SIG_double, getByte(code, o+1)); break;
				case BC_dstore_0:
				case BC_dstore_1:
				case BC_dstore_2:
				case BC_dstore_3:       process_Tstore(SIG_double, c - BC_dstore_0); break;
				case BC_astore:         process_astore(getByte(code, o+1), remap, reassignLocals); break;
				case BC_astore_0:
				case BC_astore_1:
				case BC_astore_2:
				case BC_astore_3:       process_astore_X(c - BC_astore_0, remap, reassignLocals); break;
				/* Array stores */
				case BC_iastore:        process_Tastore(SIG_int); break;
				case BC_lastore:        process_Tastore(SIG_long); break;
				case BC_fastore:        process_Tastore(SIG_float); break;
				case BC_dastore:        process_Tastore(SIG_double); break;
				case BC_bastore:        process_Tastore(SIG_byte); break;
				case BC_castore:        process_Tastore(SIG_char); break;
				case BC_sastore:        process_Tastore(SIG_short); break;
				case BC_aastore:        process_aastore(); break;
				/* Stack manipulation */
				case BC_pop:
				case BC_pop2:           process_pop(); break;
				case BC_dup:
				case BC_dup2:           process_dup(); break;
				case BC_dup_x1:
				case BC_dup2_x1:        process_dup_x1(); break;
				case BC_dup_x2:
				case BC_dup2_x2:        process_dup_x2(); break;
				case BC_swap:           process_swap(); break;
				/* Arithmetic */
				case BC_iadd:
				case BC_isub:
				case BC_imul:
				case BC_idiv:
				case BC_irem:
				case BC_ishl:
				case BC_ishr:
				case BC_iushr:
				case BC_ior:
				case BC_ixor:           process_binary(SIG_int); break;
				case BC_ineg:           process_unary(SIG_int); break;
				case BC_ladd:
				case BC_lsub:
				case BC_lmul:
				case BC_ldiv:
				case BC_lrem:
				case BC_lshl:
				case BC_lshr:
				case BC_lushr:
				case BC_lor:
				case BC_lxor:           process_binary(SIG_long); break;
				case BC_lneg:           process_unary(SIG_long); break;
				case BC_fadd:
				case BC_fsub:
				case BC_fmul:
				case BC_fdiv:
				case BC_frem:           process_binary(SIG_float); break;
				case BC_fneg:           process_unary(SIG_float); break;
				case BC_dadd:
				case BC_dsub:
				case BC_dmul:
				case BC_ddiv:
				case BC_drem:           process_binary(SIG_double); break;
				case BC_dneg:           process_unary(SIG_double); break;
				case BC_iinc:           process_iinc(getByte(code, o+1), getByte(code, o+2)); break;
				/* Conversions */
				case BC_i2l:            process_T2U(SIG_int, SIG_long); break;
				case BC_i2f:            process_T2U(SIG_int, SIG_float); break;
				case BC_i2d:            process_T2U(SIG_int, SIG_double); break;
				case BC_l2i:            process_T2U(SIG_long, SIG_int); break;
				case BC_l2f:            process_T2U(SIG_long, SIG_float); break;
				case BC_l2d:            process_T2U(SIG_long, SIG_double); break;
				case BC_f2i:            process_T2U(SIG_float, SIG_int); break;
				case BC_f2l:            process_T2U(SIG_float, SIG_long); break;
				case BC_f2d:            process_T2U(SIG_float, SIG_double); break;
				case BC_d2i:            process_T2U(SIG_double, SIG_int); break;
				case BC_d2l:            process_T2U(SIG_double, SIG_long); break;
				case BC_d2f:            process_T2U(SIG_double, SIG_float); break;
				case BC_i2b:            process_T2U(SIG_int, SIG_byte); break;
				case BC_i2c:            process_T2U(SIG_int, SIG_char); break;
				case BC_i2s:            process_T2U(SIG_int, SIG_short); break;
				/* Comparisons and conditionals */
				case BC_lcmp:           process_Tcmp(SIG_long); break;
				case BC_fcmpl:
				case BC_fcmpg:          process_Tcmp(SIG_float); break;
				case BC_dcmpl:
				case BC_dcmpg:          process_Tcmp(SIG_double); break;
				case BC_ifeq:
				case BC_ifne:
				case BC_iflt:
				case BC_ifge:
				case BC_ifgt:
				case BC_ifle:           process_ifC(getShort(code, o+1)); break;
				case BC_if_icmpeq:
				case BC_if_icmpne:
				case BC_if_icmplt:
				case BC_if_icmpge:
				case BC_if_icmpgt:
				case BC_if_icmple:      process_if_icmpC(getShort(code, o+1)); break;
				case BC_if_acmpeq:
				case BC_if_acmpne:      process_if_acmpC(getShort(code, o+1)); break;
				case BC_ifnull:
				case BC_ifnonnull:      process_ifCnull(getShort(code, o+1)); break;
				case BC_tableswitch:    process_tableswitch(getTableswitchOffsets(code, o+1)); break;
				case BC_lookupswitch:   process_lookupswitch(getLookupswitchPairs(code, o+1)); break;
				case BC_ireturn:        process_Treturn(SIG_int); break;
				case BC_lreturn:        process_Treturn(SIG_long); break;
				case BC_freturn:        process_Treturn(SIG_float); break;
				case BC_dreturn:        process_Treturn(SIG_double); break;
				case BC_areturn:        process_areturn(remap); break;
				case BC_getfield:
				case BC_getstatic:      process_getF(getShort(code, o+1), c == BC_getstatic, signature, actuals, formals); break;
				case BC_putfield:
				case BC_putstatic:      process_putF(getShort(code, o+1), c == BC_putstatic, signature, actuals, formals); break;
				case BC_invokevirtual:
				case BC_invokeinterface:
				case BC_invokespecial:
				case BC_invokestatic:   process_invoke(getShort(code, o+1), formals, actuals, c); break;
				case BC_new:            process_new(getShort(code, o+1), formals, actuals); break;
				case BC_newarray:       process_newarray(getByte(code, o+1)); break;
				case BC_anewarray:      process_anewarray(getShort(code, o+1), formals, actuals); break;
				case BC_multianewarray: process_multianewarray(getShort(code, o+1), getByte(code, o+3), formals, actuals); break;
				case BC_arraylength:    process_arraylength(); break;
				case BC_athrow:         process_athrow(); break;
				case BC_monitorenter:
				case BC_monitorexit:    process_monitorOP(); break;
				case BC_checkcast:      process_checkcast(getShort(code, o+1), formals, actuals); break;
				case BC_instanceof:     process_instanceof(getShort(code, o+1), formals, actuals); break;
				case BC_wide:
				{
					int c1 = code[o+1] & 0xFF;
					switch (c1) {
					case BC_iload:      process_Tload(SIG_int, getShort(code, o+2)); break;
					case BC_lload:      process_Tload(SIG_long, getShort(code, o+2)); break;
					case BC_fload:      process_Tload(SIG_float, getShort(code, o+2)); break;
					case BC_dload:      process_Tload(SIG_double, getShort(code, o+2)); break;
					case BC_aload:      process_aload(getShort(code, o+2), remap, reassignLocals); break;
					case BC_istore:     process_Tstore(SIG_int, getShort(code, o+2)); break;
					case BC_lstore:     process_Tstore(SIG_long, getShort(code, o+2)); break;
					case BC_fstore:     process_Tstore(SIG_float, getShort(code, o+2)); break;
					case BC_dstore:     process_Tstore(SIG_double, getShort(code, o+2)); break;
					case BC_astore:     process_astore(getShort(code, o+2), remap, reassignLocals); break;
					case BC_iinc:       process_iinc(getShort(code, o+2), getShort(code, o+4));
					case BC_ret:
						throw new IllegalArgumentException("Found bad instruction: "+c+","+c1);
					}
					int x = (c1 == BC_iinc) ? 6 : 4;
					break;
				}
				case BC_jsr: case BC_jsr_w: case BC_ret: case BC_xxxunusedxxx1:
					throw new IllegalArgumentException("Found bad instruction: "+c);
				default:
					throw new IllegalArgumentException("Unknown instruction: "+c);
				}
				if (reinterpret) {
					o--;
					reinterpret = false;
				} else
					o += getBytecodeLength(o)-1;
			}
			// Process exception table
			for (int j = 0; j < getExceptionHandlerCount(); j++) {
				short exc_index = getCaughtExceptionIndex(j);
				String excName = cf.getString(cf.getNameIndex(exc_index));
				if (VERBOSE)
					System.out.println("Catch block for "+excName);
				String[] remapType = getRemapType(exc_index, cf);
				if (remapType != null) {
					String[] params = new String[remapType.length - 1];
					for (int i = 0; i < params.length; i++)
						params[i] = typeFromSignature(instantiateType(typeToSignature(remapType[i+1]), formals, actuals));
					String newName = constantPoolTypeFromSignature(typeToSignature(mangle(remapType[0], params)));
					short new_exc_index = createClassRef(cf, newName);
					if (new_exc_index != exc_index) {
						setCaughtExceptionIndex(j, new_exc_index);
						if (VERBOSE)
							System.out.println("\t   ->"+newName);
					}
				}
				
			}
			// TODO: local variable table
		}

		protected final int getBytecodeLength(int o) {
			int c = getByte(code, o);
			int x = BCLENGTH[c];
			if (x < 0) { // Variable-length bytecode
				int pad = 3 - (o - off)%4;
				switch (c) {
				case BC_tableswitch:
				{
					int lo = getInt(code, o+pad+5);
					int hi = getInt(code, o+pad+9);
					x = 1 + pad + 12 + (hi-lo+1)*4;
					break;
				}
				case BC_lookupswitch:
				{
					int n = getInt(code, o+pad+5);
					x = 1 + pad + 8 + n*8;
					break;
				}
				case BC_wide:
				{
					int c1 = code[o+1] & 0xFF;
					x = (c1 == BC_iinc) ? 6 : 4;
					break;
				}
				}
			}
			return x;
		}
	}

	private static short createNameAndType(ClassFile cf, String name, String descriptor) {
		short name_and_type_index = findNameAndType(cf, name, descriptor);
		if (name_and_type_index != 0)
			return name_and_type_index;
		short name_index = createString(cf, name);
		short descriptor_index = createString(cf, descriptor);
		return cf.addNameAndType(name_index, descriptor_index);
	}
	private static short findNameAndType(ClassFile cf, String name, String descriptor) {
		short s = -1;
		while ((s = cf.findEntry(ClassFile.CONSTANT_NameAndType, s+1)) != -1) {
			short name_index = cf.getNameIndex(s);
			short descriptor_index = cf.getDescriptorIndex(s);
			String id = cf.getString(name_index);
			String signature = cf.getString(descriptor_index);
			if (id.equals(name) && signature.equals(descriptor))
				return s;
		}
		return 0;
	}
	private static short createFieldRef(ClassFile cf, String declarer, String name, String signature) {
		short new_field_ref = findFieldRef(cf, declarer, name, signature);
		if (new_field_ref != 0)
			return new_field_ref;
		short new_class_index = createClassRef(cf, declarer);
		short new_name_and_type = createNameAndType(cf, name, signature);
		return cf.addFieldRef(new_class_index, new_name_and_type);
	}
	private static short findFieldRef(ClassFile cf, String declarer, String name, String signature) {
		short s = -1;
		while ((s = cf.findEntry(ClassFile.CONSTANT_Fieldref, s+1)) != -1) {
			short container_index = cf.getNameIndex(cf.getClassIndex(s));
			short name_and_type_index = cf.getNameAndTypeIndex(s);
			short name_index = cf.getNameIndex(name_and_type_index);
			short descriptor_index = cf.getDescriptorIndex(name_and_type_index);
			String container = cf.getString(container_index);
			String id = cf.getString(name_index);
			String descriptor = cf.getString(descriptor_index);
			if (container.equals(declarer) && id.equals(name) && descriptor.equals(signature))
				return s;
		}
		return 0;
	}
	private static short createMethodRef(ClassFile cf, String declarer, String name, String signature, boolean isInterface) {
		short new_method_ref = findMethodRef(cf, declarer, name, signature, isInterface);
		if (new_method_ref != 0)
			return new_method_ref;
		short new_class_index = createClassRef(cf, declarer);
		short new_name_and_type = createNameAndType(cf, name, signature);
		return cf.addMethodRef(new_class_index, new_name_and_type, false);
	}
	private static short findMethodRef(ClassFile cf, String declarer, String name, String signature, boolean isInterface) {
		short s = -1;
		int entryType = isInterface ? ClassFile.CONSTANT_InterfaceMethodref : ClassFile.CONSTANT_Methodref;
		while ((s = cf.findEntry(entryType, s+1)) != -1) {
			short container_index = cf.getNameIndex(cf.getClassIndex(s));
			short name_and_type_index = cf.getNameAndTypeIndex(s);
			short name_index = cf.getNameIndex(name_and_type_index);
			short descriptor_index = cf.getDescriptorIndex(name_and_type_index);
			String container = cf.getString(container_index);
			String id = cf.getString(name_index);
			String descriptor = cf.getString(descriptor_index);
			if (container.equals(declarer) && id.equals(name) && descriptor.equals(signature))
				return s;
		}
		return 0;
	}
	private static short createClassRef(ClassFile cf, String name) {
		short class_ref = findClassRef(cf, name);
		if (class_ref != 0)
			return class_ref;
		short name_ref = createString(cf, name);
		return cf.addClassRef(name_ref);
	}
	private static short findClassRef(ClassFile cf, String name) {
		short s = -1;
		while ((s = cf.findEntry(ClassFile.CONSTANT_Class, s+1)) != -1) {
			short name_index = cf.getNameIndex(s);
			String id = cf.getString(name_index);
			if (id.equals(name))
				return s;
		}
		return 0;
	}
	private static short createString(ClassFile cf, String val) {
		short s = findString(cf, val);
		if (s != 0)
			return s;
		return cf.addString(val);
	}
	private static short findString(ClassFile cf, String val) {
		short s = -1;
		while ((s = cf.findEntry(ClassFile.CONSTANT_Utf8, s+1)) != -1) {
			String id = cf.getString(s);
			if (id.equals(val))
				return s;
		}
		return 0;
	}

	/** The delta between the object op and the corresponding primitive opcode */
	private static int atopDelta(char c, boolean array) {
		switch (c) {
		case 'B': if (array) { assert (BC_aastore-BC_bastore==-1); return -1; }
		case 'C': if (array) { assert (BC_aastore-BC_castore==-2); return -2; }
		case 'S': if (array) { assert (BC_aastore-BC_sastore==-3); return -3; }
		case 'I': assert (BC_areturn-BC_ireturn==4); return 4;
		case 'J': assert (BC_areturn-BC_lreturn==3); return 3;
		case 'F': assert (BC_areturn-BC_freturn==2); return 2;
		case 'D': assert (BC_areturn-BC_dreturn==1); return 1;
		}
		return 0;
	}

	private static String[] getFormalParameters(ClassFile cf, String typeName) {
		String annotationType = PARAMETERS;
		ClassFile.Annotation a = cf.findAnnotation(typeToSignature(annotationType));
		if (a == null)
			return NO_TYPE_ARGS;
		if (VERBOSE)
			System.out.println("Got annotation: "+annotationType);
		String[] values = getStringsValue(cf, a);
		String[] res = new String[values.length];
		for (int j = 0; j < values.length; j++) {
			res[j] = typeToSignature(createNestedType(typeName, values[j]));
			if (VERBOSE)
				System.out.println("\t"+values[j]);
		}
		return res;
	}
	private static String[] getStringsValue(ClassFile cf, ClassFile.Annotation a) {
		assert (a.element_value_pairs.length == 1);
		assert (cf.getString(a.element_value_pairs[0].element_name_index).equals("value"));
		assert (a.element_value_pairs[0].value.tag == ClassFile.Annotation.TAG_array);
		ClassFile.Annotation.ArrayValue val =
			(ClassFile.Annotation.ArrayValue) a.element_value_pairs[0].value;
		String[] value = new String[val.values.length];
		for (int j = 0; j < val.values.length; j++) {
			assert (val.values[j].tag == ClassFile.Annotation.TAG_String);
			ClassFile.Annotation.ConstValue c = (ClassFile.Annotation.ConstValue) val.values[j];
			value[j] = cf.getString(c.const_value_index);
		}
		return value;
	}
}
