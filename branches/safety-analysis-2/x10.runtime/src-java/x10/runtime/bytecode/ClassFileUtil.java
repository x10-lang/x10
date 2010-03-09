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

package x10.runtime.bytecode;

import java.util.ArrayList;

import static x10.runtime.bytecode.ClassFileConstants.*;

public class ClassFileUtil {
	public static final String SIG_byte = "B";
	public static final String SIG_char = "C";
	public static final String SIG_double = "D";
	public static final String SIG_float = "F";
	public static final String SIG_int = "I";
	public static final String SIG_long = "J";
	public static final String SIG_short = "S";
	public static final String SIG_void = "V";
	public static final String SIG_boolean = "Z";
	public static final String SIG_null = "N";
	public static final String SIG_Object = "Ljava/lang/Object;";
	public static final String SIG_String = "Ljava/lang/String;";
	public static final String SIG_StringBuilder = "Ljava/lang/StringBuilder;";
	public static final String SIG_Class = "Ljava/lang/Class;";

	/** Order matters! */
	public static final String[] PRIMITIVES = {
		"byte", "char", "double", "float", "int", "long", "short", "void", "boolean"
	};
	/** Order matters! */
	public static final String[] PRIM_SIGS = { SIG_byte, SIG_char, SIG_double, SIG_float, SIG_int, SIG_long, SIG_short, SIG_void, SIG_boolean };
	public static String typeToSignature(String string) {
		if (string.charAt(0) == '[') // Java stores array types as signatures
			return string;
		int bkt_idx = string.lastIndexOf("[]");
		if (bkt_idx != -1)
			return ("[" + typeToSignature(string.substring(0, bkt_idx))).intern();
		assert (PRIMITIVES.length == PRIM_SIGS.length);
		for (int i = 0; i < PRIMITIVES.length; i++) {
			if (string.equals(PRIMITIVES[i]))
				return PRIM_SIGS[i];
		}
		return ("L" + string.replace('.', '/') + ";").intern();
	}
	public static String typeFromSignature(String signature) {
		if (signature.charAt(0) == '[')
			return typeFromSignature(signature.substring(1)) + "[]";
		for (int i = 0; i < PRIM_SIGS.length; i++)
			if (signature.equals(PRIM_SIGS[i]))
				return PRIMITIVES[i];
		assert (signature.charAt(0) == 'L');
		return signature.substring(1, signature.length()-1).replace('/', '.');
	}
	public static String constantPoolTypeFromSignature(String signature) {
		if (signature.charAt(0) == '[')
			return signature; // Java stores arrays as signatures
		for (int i = 0; i < PRIM_SIGS.length; i++)
			if (signature.equals(PRIM_SIGS[i]))
				return PRIMITIVES[i];
		assert (signature.charAt(0) == 'L');
		return signature.substring(1, signature.length()-1);
	}
	public static String wrapperToSignature(String wrapper) {
		assert (wrapper.startsWith("java/lang/"));
		switch (wrapper.charAt(10)) {
		case 'B':
			if (wrapper.charAt(11) == 'o') {
				assert (wrapper.equals("java/lang/Boolean"));
				return SIG_boolean;
			} else {
				assert (wrapper.equals("java/lang/Byte"));
				return SIG_byte;
			}
		case 'C':
			assert (wrapper.equals("java/lang/Character"));
			return SIG_char;
		case 'D':
			assert (wrapper.equals("java/lang/Double"));
			return SIG_double;
		case 'F':
			assert (wrapper.equals("java/lang/Float"));
			return SIG_float;
		case 'I':
			assert (wrapper.equals("java/lang/Integer"));
			return SIG_int;
		case 'L':
			assert (wrapper.equals("java/lang/Long"));
			return SIG_long;
		case 'S':
			assert (wrapper.equals("java/lang/Short"));
			return SIG_short;
		case 'V':
			assert (wrapper.equals("java/lang/Void"));
			return SIG_void;
		}
		assert (false);
		return null;
	}
	public static int nextTypePos(String signature, int index) {
		switch (signature.charAt(index)) {
		case 'B': case 'C': case 'D': case 'F': case 'I':
		case 'J': case 'S': case 'V': case 'Z':
			return index+1;
		case '[':
			return nextTypePos(signature, index+1);
		case 'L':
			return signature.indexOf(';', index+1) + 1;
		}
		throw new IllegalArgumentException(signature.substring(index, index+1));
	}
	public static boolean isPrimitive(String type) {
		switch (type.charAt(0)) {
		case 'B': case 'C': case 'D': case 'F': case 'I':
		case 'J': case 'S': case 'V': case 'Z':
			return true;
		case '[': case 'L': case 'N':
			return false;
		}
		throw new IllegalArgumentException(type.substring(0, 1));
	}
	public static boolean isArray(String type) {
		if (type.charAt(0) != '[')
			return false;
		return true;
	}
	public static boolean isPrimitiveArray(String type) {
		assert (isArray(type));
		return isPrimitive(type.substring(1));
	}
	public static boolean isArrayOf(String type, String base) {
		if (type.charAt(0) != '[')
			return false;
		if (isPrimitive(base))
			return elementType(type).equals(base);
		// TODO: check inheritance
		return elementType(type).equals(base);
	}
	public static String arrayOf(String base) {
		return arrayOf(base, 1);
	}
	public static String arrayOf(String base, int dim) {
		return (replicate("[", dim)+base).intern();
	}
	public static String elementType(String array) {
		assert (isArray(array));
		return array.substring(1).intern();
	}
	public static boolean isInteger(String type) {
		switch (type.charAt(0)) {
		case 'B': case 'C': case 'I': case 'S': case 'Z':
			return true;
		case 'D': case 'F': case 'J': case 'V': case '[': case 'L': case 'N':
			return false;
		}
		throw new IllegalArgumentException(type.substring(0, 1));
	}
	public static boolean isWidePrimitive(char t) {
		return t == 'D' || t == 'J';
	}
	public static boolean isNull(String type) {
		return type.charAt(0) == 'N';
	}
	public static final String[] PRIM_ARRAY_TO_JAVA_TYPE = {
		null, null, null, null,
		SIG_boolean, SIG_char, SIG_float, SIG_double, SIG_byte, SIG_short, SIG_int, SIG_long,
	};
	public static final byte JavaTypeToPrimArray(String type) {
		assert (isPrimitive(type));
		for (int i = 0; i < PRIM_ARRAY_TO_JAVA_TYPE.length; i++)
			if (PRIM_ARRAY_TO_JAVA_TYPE[i] == type)
				return (byte) i;
		return 0;
	}
	public static String createNestedType(String container, String name) {
		return container + "$" + name;
	}

	public static String replicate(String s, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++)
			sb.append(s);
		return sb.toString();
	}

	/** Represents a signature of a method split into parts */
	public static class MethodSig {
		public String returnType;
		public String name;
		public String[] argTypes;
		public static MethodSig fromSignature(String signature) {
			int lpr_idx = signature.indexOf('(');
			int rpr_idx = signature.indexOf(')');
			assert (lpr_idx != -1 && rpr_idx != -1);
			MethodSig res = new MethodSig();
			res.name = signature.substring(0, lpr_idx).intern();
			res.returnType = signature.substring(rpr_idx+1).intern();
			ArrayList<String> args = new ArrayList<String>();
			for (int i = lpr_idx+1; i < rpr_idx; ) {
				int j = nextTypePos(signature, i);
				args.add(signature.substring(i, j).intern());
				i = j;
			}
			res.argTypes = args.toArray(new String[args.size()]);
			return res;
		}
		public String toSignature() {
			StringBuilder sb = new StringBuilder();
			sb.append(name).append("(");
			for (int i = 0; i < argTypes.length; i++)
				sb.append(argTypes[i]);
			sb.append(")").append(returnType);
			return sb.toString();
		}
		public String toString() { return "MethodSig: "+toSignature(); }
	}

	public static String constantPoolType(ClassFile cf, int index) {
		byte[] entry = cf.constant_pool.get(index);
		String type = null;
		switch (entry[0]) {
		case CONSTANT_Double:
			type = SIG_double;
			break;
		case CONSTANT_Float:
			type = SIG_float;
			break;
		case CONSTANT_Integer:
			type = SIG_int;
			break;
		case CONSTANT_Long:
			type = SIG_long;
			break;
		case CONSTANT_String:
			type = SIG_String;
			break;
		case CONSTANT_Class:
			type = SIG_Class;
			break;
		case CONSTANT_Fieldref:
		case CONSTANT_Methodref:
		case CONSTANT_InterfaceMethodref:
		case CONSTANT_NameAndType:
		case CONSTANT_Utf8:
			throw new IllegalArgumentException("Bad ldc type");
		}
		return type;
	}

	public static String[] methodArguments(String container, String signature, boolean isStatic) {
		MethodSig sig = MethodSig.fromSignature(signature);
		String[] argTypes = isStatic ? sig.argTypes : new String[1+sig.argTypes.length];
		if (!isStatic) {
			argTypes[0] = typeToSignature(container);
			System.arraycopy(sig.argTypes, 0, argTypes, 1, sig.argTypes.length);
		}
		return argTypes;
	}
	public static String methodReturnType(String container, String signature, boolean isStatic) {
		return signature.substring(signature.indexOf(')')+1).intern();
//		MethodSig sig = MethodSig.fromSignature(signature);
//		return sig.returnType;
	}

}
