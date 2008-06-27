package x10.runtime.bytecode;

import java.util.ArrayList;

public class ClassFileUtil {
	/** Order matters! */
	public static final String[] PRIMITIVES = {
		"byte", "char", "double", "float", "int", "long", "short", "void", "boolean"
	};
	/** Order matters! */
	public static final String[] PRIM_SIGS = { "B", "C", "D", "F", "I", "J", "S", "V", "Z" };
	public static String typeToSignature(String string) {
		int bkt_idx = string.lastIndexOf("[]");
		if (bkt_idx != -1)
			return "[" + typeToSignature(string.substring(0, bkt_idx));
		assert (PRIMITIVES.length == PRIM_SIGS.length);
		for (int i = 0; i < PRIMITIVES.length; i++) {
			if (string.equals(PRIMITIVES[i]))
				return PRIM_SIGS[i];
		}
		return "L" + string.replace('.', '/') + ";";
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
			res.name = signature.substring(0, lpr_idx);
			res.returnType = signature.substring(rpr_idx+1);
			ArrayList<String> args = new ArrayList<String>();
			for (int i = lpr_idx+1; i < rpr_idx; ) {
				int j = nextTypePos(signature, i);
				args.add(signature.substring(i, j));
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
}
