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
	public static final String SIG_String = "Ljava/lang/String;";
	public static final String SIG_Class = "Ljava/lang/Class;";

	/** Order matters! */
	public static final String[] PRIMITIVES = {
		"byte", "char", "double", "float", "int", "long", "short", "void", "boolean"
	};
	/** Order matters! */
	public static final String[] PRIM_SIGS = { SIG_byte, SIG_char, SIG_double, SIG_float, SIG_int, SIG_long, SIG_short, SIG_void, SIG_boolean };
	public static String typeToSignature(String string) {
		int bkt_idx = string.lastIndexOf("[]");
		if (bkt_idx != -1)
			return "[" + typeToSignature(string.substring(0, bkt_idx));
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
	public static int nextTypePos(String signature, int index) {
		switch (signature.charAt(index)) {
		case 'B': case 'C': case 'D': case 'F': case 'I':
		case 'J': case 'S': case 'V': case 'Z':
			return index+1;
		case '[':
			return nextTypePos(signature, index+1) + 1;
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
		case '[': case 'L':
			return false;
		}
		throw new IllegalArgumentException(type.substring(0, 1));
	}
	public static boolean isArray(String type) {
		if (type.charAt(0) != '[')
			return false;
		return true;
	}
	public static boolean isArrayOf(String type, String base) {
		if (type.charAt(0) != '[')
			return false;
		if (isPrimitive(base))
			return type.substring(1).equals(base);
		// TODO: check inheritance
		return type.substring(1).equals(base);
	}
	public static boolean isWidePrimitive(char t) {
		return t == 'D' || t == 'J';
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
	}

	public static final int ACC_STATIC = 0x0008;

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
}
