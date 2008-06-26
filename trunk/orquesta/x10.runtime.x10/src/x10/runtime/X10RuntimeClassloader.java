package x10.runtime;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class X10RuntimeClassloader extends ClassLoader {
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

	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		try {
			Class<?> c = findLoadedClass(name);
			if (c == null) {
				InputStream in = openClassFile(name);
				if (in == null)
					throw new IOException("Not found");
				byte[] contents = new byte[in.available()];
				in.read(contents);
				c = defineClass(name, contents);
				System.out.println("Loaded "+c.getName()+" with "+c.getClassLoader()+" from "+this);
			}
			if (resolve)
				resolveClass(c);
			return c;
		} catch (Exception e) {
			// Oops, not found or not supposed to muck with this
			return super.loadClass(name, resolve);
		}
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		int dollar = name.indexOf('$');
		if (dollar == -1)
			return super.findClass(name);
		String base = name.substring(0, dollar);
		String[] actuals = name.substring(dollar+1).split("\\$");
		InputStream in = openClassFile(base);
		if (in == null)
			throw new ClassNotFoundException(base);
		try {
			byte[] contents = new byte[in.available()];
			in.read(contents);
			in = new ByteArrayInputStream(contents);
			ClassFile cf = parseClass(base, in);
			instantiate(cf, actuals);
			transform(cf);
			byte[] result = cf.toByteArray();
//			assert (result.length == contents.length);
//			for (int i = 0; i < result.length; i++)
//				assert (result[i] == contents[i]);
			// temporary
			FileOutputStream fo = new FileOutputStream("cache/"+name.replace('.','_')+".class");
			fo.write(result);
			fo.close();
			return defineClass(name, result);
		} catch (IOException e) {
			throw new ClassNotFoundException(base+"$"+actuals[0], e);
		}
	}

	private InputStream openClassFile(String className) {
		return this.getResourceAsStream((className.replace('.','/')+".class"));
	}

	private Class<?> defineClass(String name, byte[] b) {
		return defineClass(name, b, 0, b.length);
	}

	private static String mangle(String name, String[] actuals) {
		for (int i = 0; i < actuals.length; i++)
			name += "$" + actuals[i];
		return name;
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

	/** Order matters! */
	private static final String[] PRIMITIVES = {
		"byte", "char", "double", "float", "int", "long", "short", "void", "boolean"
	};
	/** Order matters! */
	private static final String[] PRIM_SIGS = { "B", "C", "D", "F", "I", "J", "S", "V", "Z" };
	private static String typeToSignature(String string) {
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
	private static String typeFromSignature(String signature) {
		if (signature.charAt(0) == '[')
			return typeFromSignature(signature.substring(1)) + "[]";
		for (int i = 0; i < PRIM_SIGS.length; i++)
			if (signature.equals(PRIM_SIGS[i]))
				return PRIMITIVES[i];
		assert (signature.charAt(0) == 'L');
		return signature.substring(1, signature.length()-1).replace('/', '.');
	}
	private static int nextTypePos(String signature, int index) {
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
	private static boolean isPrimitive(String type) {
		switch (type.charAt(0)) {
		case 'B': case 'C': case 'D': case 'F': case 'I':
		case 'J': case 'S': case 'V': case 'Z':
			return true;
		case '[': case 'L':
			return false;
		}
		throw new IllegalArgumentException(type.substring(0, 1));
	}
	/** Represents a signature of a method split into parts */
	private static class MethodSig {
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

	/**
	 * Rename the class appropriately.
	 * Replace all occurrences of each formal parameter by the corresponding
	 * actual parameter.  The possible occurrences are: type of a field,
	 * type of a local, method parameter, instanceof test, instantiation
	 * of another generic type.
	 * TODO: what did I miss here?
	 * Also change all aloads, areturns, and compares to appropriate primitive
	 * loads, returns, and compares.
	 * FIXME: Adjust the local counts accordingly (for longs and doubles).
	 * TODO: also remap arrays and method calls -- how?  we need to do type inference
	 * FIXME: what to do about non-type and function parameters?
	 */
	private void instantiate(ClassFile cf, String[] actuals) {
		short nameIndex = cf.getNameIndex(cf.this_class);
		String typeName = cf.getString(nameIndex);
		System.out.println("Got class "+typeName);
		String newName = mangle(typeName, actuals);
		cf.setString(nameIndex, newName);
		System.out.println("   ->"+newName);
		String[] formals = getFormalParameters(cf, typeName);
		assert (formals != null);
		assert (formals.length == actuals.length);
		for (int i = 0; i < cf.fields.length; i++) {
			ClassFile.Field f = cf.fields[i];
			String signature = cf.getString(f.descriptor_index);
			String name = cf.getString(f.name_index);
			System.out.println("\tGot field "+name+" with signature "+signature);
			int fi = getFormalIndex(signature, formals);
			if (fi != -1) {
				String newSignature = typeToSignature(actuals[fi]);
				System.out.println("\t   ->" + newSignature);
				cf.setString(f.descriptor_index, newSignature);
			}
		}
		// FIXME: watch out for overloaded methods!
		for (int i = 0; i < cf.methods.length; i++) {
			ClassFile.Method m = cf.methods[i];
			String signature = cf.getString(m.descriptor_index);
			String name = cf.getString(m.name_index);
			System.out.println("\tGot method "+name+" with signature "+signature);
			MethodSig sig = MethodSig.fromSignature(signature);
			int adjust = (m.access_flags & ACC_STATIC) == 0 ? 1 : 0;
			char[] remap = new char[adjust+sig.argTypes.length+1]; // args + return type
			boolean changed = false;
			int fi = getFormalIndex(sig.returnType, formals);
			if (fi != -1) {
				sig.returnType = typeToSignature(actuals[fi]);
				if (isPrimitive(sig.returnType))
					remap[adjust+sig.argTypes.length] = sig.returnType.charAt(0);
				changed = true;
			}
			for (int j = 0; j < sig.argTypes.length; j++) {
				fi = getFormalIndex(sig.argTypes[j], formals);
				if (fi != -1) {
					sig.argTypes[j] = typeToSignature(actuals[fi]);
					if (isPrimitive(sig.argTypes[j]))
						remap[adjust+j] = sig.argTypes[j].charAt(0);
					changed = true;
				}
			}
			if (changed) {
				String newSignature = sig.toSignature();
				System.out.println("\t   ->" + newSignature);
				cf.setString(m.descriptor_index, newSignature);
			}
			byte[] code = cf.getMethodCode(m);
			int off = cf.getMethodCodeOffset(code);
			int len = cf.getMethodCodeLength(code);
			int max_stack = cf.getMethodStack(code);
			int max_locals = cf.getMethodLocals(code);
			short[] reassignLocals = new short[max_locals];
			short nextLocal = 0;
			for (int j = 0; j < reassignLocals.length; j++, nextLocal++) {
				reassignLocals[j] = nextLocal;
				if (j < remap.length-1 && (remap[j] == 'D' || remap[j] == 'J')) {
					nextLocal++;
				}
			}
			if (nextLocal != max_locals) {
				cf.setMethodLocals(code, (short)nextLocal);
			}
			if (nextLocal != max_locals || remap[remap.length-1] == 'D' || remap[remap.length-1] == 'J') {
				cf.setMethodStack(code, (short)(max_stack*2));
			}
			for (int o = off; o < off + len; o++) {
				int c = code[o] & 0xFF;
				switch (c) {
				case BC_aload: case BC_astore:
				{
					int arg = code[o+1] & 0xFF;
					if (remap[arg] != '\0')
						code[o] -= atopDelta(remap[arg]);
					if (reassignLocals[arg] != arg)
						code[o+1] = (byte)reassignLocals[arg];
					break;
				}
				case BC_areturn:
				{
					int arg = adjust+sig.argTypes.length;
					if (remap[arg] != '\0')
						code[o] -= atopDelta(remap[arg]);
					break;
				}
				case BC_aload_0: case BC_aload_1: case BC_aload_2: case BC_aload_3:
				{
					int arg = c - BC_aload_0;
					if (remap[arg] != '\0')
						code[o] -= atopDelta(remap[arg]) * 4;
					if (reassignLocals[arg] != arg) {
						if (reassignLocals[arg] < 3)
							code[o] = (byte)(BC_aload_0 + reassignLocals[arg]);
						else
							throw new IllegalArgumentException("bytecode change!!!");
					}
					break;
				}
				}
				o += getBytecodeLength(code, o)-1;
			}
		}
		// TODO: attributes
	}

	/**
	 * Replace all parameterized class references by their mangled names.
	 */
	private void transform(ClassFile cf) {
		// TODO Auto-generated method stub
		
	}

	/** The delta between the object op and the corresponding primitive opcode */
	private static int atopDelta(char c) {
		switch (c) {
		case 'B': case 'C': case 'S': case 'I': assert (BC_areturn-BC_ireturn==4); return 4;
		case 'J': assert (BC_areturn-BC_lreturn==3); return 3;
		case 'F': assert (BC_areturn-BC_freturn==2); return 2;
		case 'D': assert (BC_areturn-BC_dreturn==1); return 1;
		}
		return 0;
	}

	public static final int ACC_STATIC = 0x0008; 

	public static final int BC_nop = 0;
	public static final int BC_aconst_null = 1;
	public static final int BC_iconst_m1 = 2;
	public static final int BC_iconst_0 = 3;
	public static final int BC_iconst_1 = 4;
	public static final int BC_iconst_2 = 5;
	public static final int BC_iconst_3 = 6;
	public static final int BC_iconst_4 = 7;
	public static final int BC_iconst_5 = 8;
	public static final int BC_lconst_0 = 9;
	public static final int BC_lconst_1 = 10;
	public static final int BC_fconst_0 = 11;
	public static final int BC_fconst_1 = 12;
	public static final int BC_fconst_2 = 13;
	public static final int BC_dconst_0 = 14;
	public static final int BC_dconst_1 = 15;
	public static final int BC_bipush = 16;
	public static final int BC_sipush = 17;
	public static final int BC_ldc = 18;
	public static final int BC_ldc_w = 19;
	public static final int BC_ldc2_w = 20;
	public static final int BC_iload = 21;
	public static final int BC_lload = 22;
	public static final int BC_fload = 23;
	public static final int BC_dload = 24;
	public static final int BC_aload = 25;
	public static final int BC_iload_0 = 26;
	public static final int BC_iload_1 = 27;
	public static final int BC_iload_2 = 28;
	public static final int BC_iload_3 = 29;
	public static final int BC_lload_0 = 30;
	public static final int BC_lload_1 = 31;
	public static final int BC_lload_2 = 32;
	public static final int BC_lload_3 = 33;
	public static final int BC_fload_0 = 34;
	public static final int BC_fload_1 = 35;
	public static final int BC_fload_2 = 36;
	public static final int BC_fload_3 = 37;
	public static final int BC_dload_0 = 38;
	public static final int BC_dload_1 = 39;
	public static final int BC_dload_2 = 40;
	public static final int BC_dload_3 = 41;
	public static final int BC_aload_0 = 42;
	public static final int BC_aload_1 = 43;
	public static final int BC_aload_2 = 44;
	public static final int BC_aload_3 = 45;
	public static final int BC_iaload = 46;
	public static final int BC_laload = 47;
	public static final int BC_faload = 48;
	public static final int BC_daload = 49;
	public static final int BC_aaload = 50;
	public static final int BC_baload = 51;
	public static final int BC_caload = 52;
	public static final int BC_saload = 53;
	public static final int BC_istore = 54;
	public static final int BC_lstore = 55;
	public static final int BC_fstore = 56;
	public static final int BC_dstore = 57;
	public static final int BC_astore = 58;
	public static final int BC_istore_0 = 59;
	public static final int BC_istore_1 = 60;
	public static final int BC_istore_2 = 61;
	public static final int BC_istore_3 = 62;
	public static final int BC_lstore_0 = 63;
	public static final int BC_lstore_1 = 64;
	public static final int BC_lstore_2 = 65;
	public static final int BC_lstore_3 = 66;
	public static final int BC_fstore_0 = 67;
	public static final int BC_fstore_1 = 68;
	public static final int BC_fstore_2 = 69;
	public static final int BC_fstore_3 = 70;
	public static final int BC_dstore_0 = 71;
	public static final int BC_dstore_1 = 72;
	public static final int BC_dstore_2 = 73;
	public static final int BC_dstore_3 = 74;
	public static final int BC_astore_0 = 75;
	public static final int BC_astore_1 = 76;
	public static final int BC_astore_2 = 77;
	public static final int BC_astore_3 = 78;
	public static final int BC_iastore = 79;
	public static final int BC_lastore = 80;
	public static final int BC_fastore = 81;
	public static final int BC_dastore = 82;
	public static final int BC_aastore = 83;
	public static final int BC_bastore = 84;
	public static final int BC_castore = 85;
	public static final int BC_sastore = 86;
	public static final int BC_pop = 87;
	public static final int BC_pop2 = 88;
	public static final int BC_dup = 89;
	public static final int BC_dup_x1 = 90;
	public static final int BC_dup_x2 = 91;
	public static final int BC_dup2 = 92;
	public static final int BC_dup2_x1 = 93;
	public static final int BC_dup2_x2 = 94;
	public static final int BC_swap = 95;
	public static final int BC_iadd = 96;
	public static final int BC_ladd = 97;
	public static final int BC_fadd = 98;
	public static final int BC_dadd = 99;
	public static final int BC_isub = 100;
	public static final int BC_lsub = 101;
	public static final int BC_fsub = 102;
	public static final int BC_dsub = 103;
	public static final int BC_imul = 104;
	public static final int BC_lmul = 105;
	public static final int BC_fmul = 106;
	public static final int BC_dmul = 107;
	public static final int BC_idiv = 108;
	public static final int BC_ldiv = 109;
	public static final int BC_fdiv = 110;
	public static final int BC_ddiv = 111;
	public static final int BC_irem = 112;
	public static final int BC_lrem = 113;
	public static final int BC_frem = 114;
	public static final int BC_drem = 115;
	public static final int BC_ineg = 116;
	public static final int BC_lneg = 117;
	public static final int BC_fneg = 118;
	public static final int BC_dneg = 119;
	public static final int BC_ishl = 120;
	public static final int BC_lshl = 121;
	public static final int BC_ishr = 122;
	public static final int BC_lshr = 123;
	public static final int BC_iushr = 124;
	public static final int BC_lushr = 125;
	public static final int BC_iand = 126;
	public static final int BC_land = 127;
	public static final int BC_ior = 128;
	public static final int BC_lor = 129;
	public static final int BC_ixor = 130;
	public static final int BC_lxor = 131;
	public static final int BC_iinc = 132;
	public static final int BC_i2l = 133;
	public static final int BC_i2f = 134;
	public static final int BC_i2d = 135;
	public static final int BC_l2i = 136;
	public static final int BC_l2f = 137;
	public static final int BC_l2d = 138;
	public static final int BC_f2i = 139;
	public static final int BC_f2l = 140;
	public static final int BC_f2d = 141;
	public static final int BC_d2i = 142;
	public static final int BC_d2l = 143;
	public static final int BC_d2f = 144;
	public static final int BC_i2b = 145;
	public static final int BC_i2c = 146;
	public static final int BC_i2s = 147;
	public static final int BC_lcmp = 148;
	public static final int BC_fcmpl = 149;
	public static final int BC_fcmpg = 150;
	public static final int BC_dcmpl = 151;
	public static final int BC_dcmpg = 152;
	public static final int BC_ifeq = 153;
	public static final int BC_ifne = 154;
	public static final int BC_iflt = 155;
	public static final int BC_ifge = 156;
	public static final int BC_ifgt = 157;
	public static final int BC_ifle = 158;
	public static final int BC_if_icmpeq = 159;
	public static final int BC_if_icmpne = 160;
	public static final int BC_if_icmplt = 161;
	public static final int BC_if_icmpge = 162;
	public static final int BC_if_icmpgt = 163;
	public static final int BC_if_icmple = 164;
	public static final int BC_if_acmpeq = 165;
	public static final int BC_if_acmpne = 166;
	public static final int BC_goto = 167;
	public static final int BC_jsr = 168;
	public static final int BC_ret = 169;
	public static final int BC_tableswitch = 170;
	public static final int BC_lookupswitch = 171;
	public static final int BC_ireturn = 172;
	public static final int BC_lreturn = 173;
	public static final int BC_freturn = 174;
	public static final int BC_dreturn = 175;
	public static final int BC_areturn = 176;
	public static final int BC_return = 177;
	public static final int BC_getstatic = 178;
	public static final int BC_putstatic = 179;
	public static final int BC_getfield = 180;
	public static final int BC_putfield = 181;
	public static final int BC_invokevirtual = 182;
	public static final int BC_invokespecial = 183;
	public static final int BC_invokestatic = 184;
	public static final int BC_invokeinterface = 185;
	public static final int BC_xxxunusedxxx1 = 186;
	public static final int BC_new = 187;
	public static final int BC_newarray = 188;
	public static final int BC_anewarray = 189;
	public static final int BC_arraylength = 190;
	public static final int BC_athrow = 191;
	public static final int BC_checkcast = 192;
	public static final int BC_instanceof = 193;
	public static final int BC_monitorenter = 194;
	public static final int BC_monitorexit = 195;
	public static final int BC_wide = 196;
	public static final int BC_multianewarray = 197;
	public static final int BC_ifnull = 198;
	public static final int BC_ifnonnull = 199;
	public static final int BC_goto_w = 200;
	public static final int BC_jsr_w = 201;
	private static final int[] BCLENGTH = {
		1, //   0 (0x00) nop
		1, //   1 (0x01) aconst_null
		1, //   2 (0x02) iconst_m1
		1, //   3 (0x03) iconst_0
		1, //   4 (0x04) iconst_1
		1, //   5 (0x05) iconst_2
		1, //   6 (0x06) iconst_3
		1, //   7 (0x07) iconst_4
		1, //   8 (0x08) iconst_5
		1, //   9 (0x09) lconst_0
		1, //  10 (0x0a) lconst_1
		1, //  11 (0x0b) fconst_0
		1, //  12 (0x0c) fconst_1
		1, //  13 (0x0d) fconst_2
		1, //  14 (0x0e) dconst_0
		1, //  15 (0x0f) dconst_1
		2, //  16 (0x10) bipush
		3, //  17 (0x11) sipush
		2, //  18 (0x12) ldc
		3, //  19 (0x13) ldc_w
		3, //  20 (0x14) ldc2_w
		2, //  21 (0x15) iload
		2, //  22 (0x16) lload
		2, //  23 (0x17) fload
		2, //  24 (0x18) dload
		2, //  25 (0x19) aload
		1, //  26 (0x1a) iload_0
		1, //  27 (0x1b) iload_1
		1, //  28 (0x1c) iload_2
		1, //  29 (0x1d) iload_3
		1, //  30 (0x1e) lload_0
		1, //  31 (0x1f) lload_1
		1, //  32 (0x20) lload_2
		1, //  33 (0x21) lload_3
		1, //  34 (0x22) fload_0
		1, //  35 (0x23) fload_1
		1, //  36 (0x24) fload_2
		1, //  37 (0x25) fload_3
		1, //  38 (0x26) dload_0
		1, //  39 (0x27) dload_1
		1, //  40 (0x28) dload_2
		1, //  41 (0x29) dload_3
		1, //  42 (0x2a) aload_0
		1, //  43 (0x2b) aload_1
		1, //  44 (0x2c) aload_2
		1, //  45 (0x2d) aload_3
		1, //  46 (0x2e) iaload
		1, //  47 (0x2f) laload
		1, //  48 (0x30) faload
		1, //  49 (0x31) daload
		1, //  50 (0x32) aaload
		1, //  51 (0x33) baload
		1, //  52 (0x34) caload
		1, //  53 (0x35) saload
		2, //  54 (0x36) istore
		2, //  55 (0x37) lstore
		2, //  56 (0x38) fstore
		2, //  57 (0x39) dstore
		2, //  58 (0x3a) astore
		1, //  59 (0x3b) istore_0
		1, //  60 (0x3c) istore_1
		1, //  61 (0x3d) istore_2
		1, //  62 (0x3e) istore_3
		1, //  63 (0x3f) lstore_0
		1, //  64 (0x40) lstore_1
		1, //  65 (0x41) lstore_2
		1, //  66 (0x42) lstore_3
		1, //  67 (0x43) fstore_0
		1, //  68 (0x44) fstore_1
		1, //  69 (0x45) fstore_2
		1, //  70 (0x46) fstore_3
		1, //  71 (0x47) dstore_0
		1, //  72 (0x48) dstore_1
		1, //  73 (0x49) dstore_2
		1, //  74 (0x4a) dstore_3
		1, //  75 (0x4b) astore_0
		1, //  76 (0x4c) astore_1
		1, //  77 (0x4d) astore_2
		1, //  78 (0x4e) astore_3
		1, //  79 (0x4f) iastore
		1, //  80 (0x50) lastore
		1, //  81 (0x51) fastore
		1, //  82 (0x52) dastore
		1, //  83 (0x53) aastore
		1, //  84 (0x54) bastore
		1, //  85 (0x55) castore
		1, //  86 (0x56) sastore
		1, //  87 (0x57) pop
		1, //  88 (0x58) pop2
		1, //  89 (0x59) dup
		1, //  90 (0x5a) dup_x1
		1, //  91 (0x5b) dup_x2
		1, //  92 (0x5c) dup2
		1, //  93 (0x5d) dup2_x1
		1, //  94 (0x5e) dup2_x2
		1, //  95 (0x5f) swap
		1, //  96 (0x60) iadd
		1, //  97 (0x61) ladd
		1, //  98 (0x62) fadd
		1, //  99 (0x63) dadd
		1, // 100 (0x64) isub
		1, // 101 (0x65) lsub
		1, // 102 (0x66) fsub
		1, // 103 (0x67) dsub
		1, // 104 (0x68) imul
		1, // 105 (0x69) lmul
		1, // 106 (0x6a) fmul
		1, // 107 (0x6b) dmul
		1, // 108 (0x6c) idiv
		1, // 109 (0x6d) ldiv
		1, // 110 (0x6e) fdiv
		1, // 111 (0x6f) ddiv
		1, // 112 (0x70) irem
		1, // 113 (0x71) lrem
		1, // 114 (0x72) frem
		1, // 115 (0x73) drem
		1, // 116 (0x74) ineg
		1, // 117 (0x75) lneg
		1, // 118 (0x76) fneg
		1, // 119 (0x77) dneg
		1, // 120 (0x78) ishl
		1, // 121 (0x79) lshl
		1, // 122 (0x7a) ishr
		1, // 123 (0x7b) lshr
		1, // 124 (0x7c) iushr
		1, // 125 (0x7d) lushr
		1, // 126 (0x7e) iand
		1, // 127 (0x7f) land
		1, // 128 (0x80) ior
		1, // 129 (0x81) lor
		1, // 130 (0x82) ixor
		1, // 131 (0x83) lxor
		3, // 132 (0x84) iinc
		1, // 133 (0x85) i2l
		1, // 134 (0x86) i2f
		1, // 135 (0x87) i2d
		1, // 136 (0x88) l2i
		1, // 137 (0x89) l2f
		1, // 138 (0x8a) l2d
		1, // 139 (0x8b) f2i
		1, // 140 (0x8c) f2l
		1, // 141 (0x8d) f2d
		1, // 142 (0x8e) d2i
		1, // 143 (0x8f) d2l
		1, // 144 (0x90) d2f
		1, // 145 (0x91) i2b
		1, // 146 (0x92) i2c
		1, // 147 (0x93) i2s
		1, // 148 (0x94) lcmp
		1, // 149 (0x95) fcmpl
		1, // 150 (0x96) fcmpg
		1, // 151 (0x97) dcmpl
		1, // 152 (0x98) dcmpg
		3, // 153 (0x99) ifeq
		3, // 154 (0x9a) ifne
		3, // 155 (0x9b) iflt
		3, // 156 (0x9c) ifge
		3, // 157 (0x9d) ifgt
		3, // 158 (0x9e) ifle
		3, // 159 (0x9f) if_icmpeq
		3, // 160 (0xa0) if_icmpne
		3, // 161 (0xa1) if_icmplt
		3, // 162 (0xa2) if_icmpge
		3, // 163 (0xa3) if_icmpgt
		3, // 164 (0xa4) if_icmple
		3, // 165 (0xa5) if_acmpeq
		3, // 166 (0xa6) if_acmpne
		3, // 167 (0xa7) goto
		3, // 168 (0xa8) jsr
		2, // 169 (0xa9) ret
		-1, // 170 (0xaa) tableswitch
		-1, // 171 (0xab) lookupswitch
		1, // 172 (0xac) ireturn
		1, // 173 (0xad) lreturn
		1, // 174 (0xae) freturn
		1, // 175 (0xaf) dreturn
		1, // 176 (0xb0) areturn
		1, // 177 (0xb1) return
		3, // 178 (0xb2) getstatic
		3, // 179 (0xb3) putstatic
		3, // 180 (0xb4) getfield
		3, // 181 (0xb5) putfield
		3, // 182 (0xb6) invokevirtual
		3, // 183 (0xb7) invokespecial
		3, // 184 (0xb8) invokestatic
		5, // 185 (0xb9) invokeinterface
		1, // 186 (0xba) xxxunusedxxx1
		3, // 187 (0xbb) new
		2, // 188 (0xbc) newarray
		3, // 189 (0xbd) anewarray
		1, // 190 (0xbe) arraylength
		1, // 191 (0xbf) athrow
		3, // 192 (0xc0) checkcast
		3, // 193 (0xc1) instanceof
		1, // 194 (0xc2) monitorenter
		1, // 195 (0xc3) monitorexit
		-1, // 196 (0xc4) wide
		4, // 197 (0xc5) multianewarray
		3, // 198 (0xc6) ifnull
		3, // 199 (0xc7) ifnonnull
		5, // 200 (0xc8) goto_w
		5, // 201 (0xc9) jsr_w
	};
	private static int getBytecodeLength(byte[] code, int o) {
		int c = code[o] & 0xFF;
		int x = BCLENGTH[c];
		if (x < 0) { // Variable-length bytecode
			int pad = 3 - (o - ClassFile.MethodCodeOffset)%4;
			switch (c) {
			case BC_tableswitch:
			{
				int lo = getInt(code, o+pad+4);
				int hi = getInt(code, o+pad+8);
				x = pad + 12 + (hi-lo+1);
				break;
			}
			case BC_lookupswitch:
			{
				int n = getInt(code, o+pad+4);
				x = pad + 8 + n*8;
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

	private static final String PARAMETERS = "Lx10/generics/Parameters;";
	private static String[] getFormalParameters(ClassFile cf, String typeName) {
		ClassFile.Annotation[] annot = cf.parseAnnotations();
		for (int i = 0; i < annot.length; i++) {
			ClassFile.Annotation a = annot[i];
			String name = cf.getString(a.type_index);
			System.out.println("Got annotation: "+name);
			if (name.equals(PARAMETERS)) {
				assert (a.element_value_pairs.length == 1);
				assert (cf.getString(a.element_value_pairs[0].element_name_index).equals("value"));
				assert (a.element_value_pairs[0].value.tag == ClassFile.Annotation.TAG_array);
				ClassFile.Annotation.ArrayValue val =
					(ClassFile.Annotation.ArrayValue) a.element_value_pairs[0].value;
				String[] res = new String[val.values.length];
				for (int j = 0; j < val.values.length; j++) {
					assert (val.values[j].tag == ClassFile.Annotation.TAG_String);
					ClassFile.Annotation.ConstValue c = (ClassFile.Annotation.ConstValue) val.values[j];
					res[j] = typeToSignature(typeName+"$"+cf.getString(c.const_value_index));
					System.out.println("\t"+res[j]);
				}
				return res;
			}
		}
		return null;
	}

	private static class ClassFile {
		public int magic;
		public short minor;
		public short major;
		public List<byte[]> constant_pool;
		public short access_flags;
		public short this_class;
		public short super_class;
		public short interfaces[];
		public Field fields[];
		public Method methods[];
		public Attribute attributes[];

		public short getNameIndex(int val_idx) {
			byte[] entry = constant_pool.get(val_idx);
			assert (entry[0] == CONSTANT_Class);
			return getShort(entry, 1);
		}
		public void setNameIndex(int val_idx, short val) {
			byte[] entry = new byte[3];
			entry[0] = CONSTANT_Class;
			dumpShort(entry, 1, val);
			assert (constant_pool.get(val_idx)[0] == CONSTANT_Class);
			constant_pool.set(val_idx, entry);
		}
		private static final String UTF8 = "UTF-8";
		public String getString(int name_idx) {
			try {
				byte[] entry = constant_pool.get(name_idx);
				assert (entry[0] == CONSTANT_Utf8);
				int name_len = getShort(entry, 1);
				return new String(entry, 3, name_len, UTF8).intern();
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		public void setString(int name_idx, String val) {
			try {
				byte[] utf8 = val.getBytes(UTF8);
				byte[] entry = new byte[utf8.length + 3];
				entry[0] = CONSTANT_Utf8;
				dumpShort(entry, 1, (short)utf8.length);
				System.arraycopy(utf8, 0, entry, 3, utf8.length);
				assert (constant_pool.get(name_idx)[0] == CONSTANT_Utf8);
				constant_pool.set(name_idx, entry);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private static final String CODE = "Code";
		public static final int MethodCodeOffset = 8; // skip max_stack, max_locals, and code length
		public byte[] getMethodCode(Method m) {
			Attribute code = findAttribute(CODE, m.attributes);
			if (code == null) return null;
			return code.info;
		}
		// Can be static, but that causes a warning...
		public int getMethodStack(byte[] b) {
			return getShort(b, 0);
		}
		// Can be static, but that causes a warning...
		public void setMethodStack(byte[] b, short newVal) {
			dumpShort(b, 0, newVal);
		}
		// Can be static, but that causes a warning...
		public int getMethodLocals(byte[] b) {
			return getShort(b, 2); // skip max_stack
		}
		// Can be static, but that causes a warning...
		public void setMethodLocals(byte[] b, short newVal) {
			dumpShort(b, 2, newVal); // skip max_stack
		}
		// Can be static, but that causes a warning...
		public int getMethodCodeOffset(byte[] b) {
			return MethodCodeOffset;
		}
		// Can be static, but that causes a warning...
		public int getMethodCodeLength(byte[] b) {
			return getInt(b, 4); // skip max_stack and max_locals
		}
		// Can be static, but that causes a warning...
		public int getMethodExceptionTableOffset(byte[] b) {
			int len = getInt(b, 4); // skip max_stack and max_locals
			return 8 + len + 2; // also skip code length and bytecodes and ET length
		}
		// Can be static, but that causes a warning...
		public int getMethodExceptionTableLength(byte[] b) {
			int len = getInt(b, 4); // skip max_stack and max_locals
			return getShort(b, 8 + len) * 8; // skip code length and bytecodes; each entry is 8 bytes
		}

		public Attribute findAttribute(String name, Attribute[] attributes) {
			for (int i = 0; i < attributes.length; i++) {
				Attribute a = attributes[i];
				if (getString(a.attribute_name_index).equals(name))
					return a;
			}
			return null;
		}

		private static class Attribute {
			public short attribute_name_index;
			public byte info[];
		}
		// FIXME: Field and Method are essentially the same.  Combine?
		private static class Field {
			public short access_flags;
			public short name_index;
			public short descriptor_index;
			public Attribute attributes[];
		}
		// FIXME: Field and Method are essentially the same.  Combine?
		private static class Method {
			public short access_flags;
			public short name_index;
			public short descriptor_index;
			public Attribute attributes[];
		}

		private static class Annotation {
			public short type_index;
			public ElementValuePair[] element_value_pairs;

			private static class ElementValuePair {
				public short element_name_index;
				public ElementValue value;
			}
			public static final byte TAG_byte = 'B';
			public static final byte TAG_char = 'C';
			public static final byte TAG_double = 'D';
			public static final byte TAG_float = 'F';
			public static final byte TAG_int = 'I';
			public static final byte TAG_long = 'J';
			public static final byte TAG_short = 'S';
			public static final byte TAG_boolean = 'Z';
			public static final byte TAG_String = 's';
			public static final byte TAG_Enum = 'e';
			public static final byte TAG_Class = 'c';
			public static final byte TAG_Annotation = '@';
			public static final byte TAG_array = '[';
			private static abstract class ElementValue {
				public byte tag;
			}
			private static class ConstValue extends ElementValue {
				public short const_value_index;
			}
			private static class EnumValue extends ElementValue {
				public short type_name_index;
				public short const_name_index;
			}
			private static class ClassInfo extends ElementValue {
				public short class_info_index;
			}
			private static class AnnotationValue extends ElementValue {
				public Annotation annotation_value;
			}
			private static class ArrayValue extends ElementValue {
				public ElementValue[] values;
			}
		}
		public Annotation[] parseAnnotations() {
			return parseAnnotations(attributes);
		}
		public Annotation[] parseAnnotations(Field f) {
			return parseAnnotations(f.attributes);
		}
		public Annotation[] parseAnnotations(Method m) {
			return parseAnnotations(m.attributes);
		}
		private static final String RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations";
		private static final String RUNTIME_INVISIBLE_ANNOTATIONS = "RuntimeInvisibleAnnotations";
		private static final String RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS = "RuntimeVisibleParameterAnnotations";
		private static final String RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS = "RuntimeInvisibleParameterAnnotations";
		private static final String[] ANNOTATION_ATTRIBUTES = {
			RUNTIME_VISIBLE_ANNOTATIONS,
			RUNTIME_INVISIBLE_ANNOTATIONS,
			RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS,
			RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS,
		};
		private Annotation[] parseAnnotations(Attribute[] attributes) {
			int num_annotations = 0;
			Attribute[] annotAttrs = new Attribute[ANNOTATION_ATTRIBUTES.length];
			for (int i = 0; i < ANNOTATION_ATTRIBUTES.length; i++) {
				Attribute a = findAttribute(ANNOTATION_ATTRIBUTES[i], attributes);
				if (a != null)
					num_annotations += getShort(a.info, 0);
				annotAttrs[i] = a;
			}
			Annotation[] annotations = new Annotation[num_annotations];
			int offset = 0;
			for (int i = 0; i < annotAttrs.length; i++) {
				Attribute a = annotAttrs[i];
				if (a != null)
					offset = extractAnnotations(a.info, annotations, offset);
			}
			assert (offset == num_annotations);
			return annotations;
		}
		private int extractAnnotations(byte[] b, Annotation[] annotations, int offset) {
			short num = getShort(b, 0);
			int o = 2;
			for (int j = 0; j < num; j++)
				o = extractAnnotation(b, o, annotations, offset + j);
			assert (o == b.length);
			return offset + num;
		}
		private int extractAnnotation(byte[] b, int o, Annotation[] a, int j) {
			Annotation r = new Annotation();
			r.type_index = getShort(b, o);
			o += 2;
			short num_pairs = getShort(b, o);
			o += 2;
			Annotation.ElementValuePair[] evp = new Annotation.ElementValuePair[num_pairs];
			r.element_value_pairs = evp;
			for (int k = 0; k < num_pairs; k++) {
				evp[k] = new Annotation.ElementValuePair();
				evp[k].element_name_index = getShort(b, o);
				o += 2;
				Annotation.ElementValue[] v = new Annotation.ElementValue[1];
				int l = 0;
				o = extractElementValue(b, o, v, l);
				evp[k].value = v[0];
			}
			a[j] = r;
			return o;
		}
		private int extractElementValue(byte[] b, int o, Annotation.ElementValue[] v, int k) {
			byte tag = getByte(b, o);
			o += 1;
			switch (tag) {
			case Annotation.TAG_byte:
			case Annotation.TAG_char:
			case Annotation.TAG_double:
			case Annotation.TAG_float:
			case Annotation.TAG_int:
			case Annotation.TAG_long:
			case Annotation.TAG_short:
			case Annotation.TAG_boolean:
			case Annotation.TAG_String:
				Annotation.ConstValue constVal = new Annotation.ConstValue();
				constVal.tag = tag;
				constVal.const_value_index = getShort(b, o);
				o += 2;
				v[k] = constVal;
				break;
			case Annotation.TAG_Enum:
				Annotation.EnumValue enumVal = new Annotation.EnumValue();
				enumVal.tag = tag;
				enumVal.type_name_index = getShort(b, o);
				o += 2;
				enumVal.const_name_index = getShort(b, o);
				o += 2;
				v[k] = enumVal;
				break;
			case Annotation.TAG_Class:
				Annotation.ClassInfo classVal = new Annotation.ClassInfo();
				classVal.tag = tag;
				classVal.class_info_index = getShort(b, o);
				o += 2;
				v[k] = classVal;
				break;
			case Annotation.TAG_Annotation:
				Annotation.AnnotationValue annotVal = new Annotation.AnnotationValue();
				annotVal.tag = tag;
				Annotation[] h = new Annotation[1];
				o = extractAnnotation(b, o, h, 0);
				annotVal.annotation_value = h[0];
				v[k] = annotVal;
				break;
			case Annotation.TAG_array:
				Annotation.ArrayValue arrayVal = new Annotation.ArrayValue();
				arrayVal.tag = tag;
				short num_values = getShort(b, o);
				o += 2;
				arrayVal.values = new Annotation.ElementValue[num_values];
				for (int i = 0; i < num_values; i++)
					o = extractElementValue(b, o, arrayVal.values, i);
				v[k] = arrayVal;
				break;
			default:
				assert (false);
			}
			return o;
		}

		private int computeLength() {
			return 4 /* magic */
			     + 2 /* minor */
			     + 2 /* major */
			     + computeConstantPoolLength(constant_pool)
			     + 2 /* access_flags */
			     + 2 /* this_class */
			     + 2 /* super_class */
			     + 2 + 2 * interfaces.length /* interfaces */
			     + computeFieldsLength(fields)
			     + computeMethodsLength(methods)
			     + computeAttributesLength(attributes);
		}
		private static int computeConstantPoolLength(List<byte[]> cp) {
			int total = 2;
			int i = 0;
			for (byte[] e : cp) {
				if (i++ == 0) continue; // Skip first entry
				total += e.length;
			}
			return total;
		}
		private static int computeFieldsLength(Field[] f) {
			int total = 2;
			for (int i = 0; i < f.length; i++) {
				total += 2 /* access_flags */
				       + 2 /* name_index */
				       + 2 /* descriptor_index */
				       + computeAttributesLength(f[i].attributes);
			}
			return total;
		}
		private static int computeMethodsLength(Method[] m) {
			int total = 2;
			for (int i = 0; i < m.length; i++) {
				total += 2 /* access_flags */
				       + 2 /* name_index */
				       + 2 /* descriptor_index */
				       + computeAttributesLength(m[i].attributes);
			}
			return total;
		}
		private static int computeAttributesLength(Attribute[] a) {
			int total = 2;
			for (int i = 0; i < a.length; i++) {
				total += 2 /* attribute_name_index */
				       + 4 + a[i].info.length;
			}
			return total;
		}
		private static int dumpShort(byte[] b, int o, short v) {
			b[o]   = (byte)((v >> 8) & 0xFF);
			b[o+1] = (byte)(v & 0xFF);
			return o+2;
		}
		private static int dumpInt(byte[] b, int o, int v) {
			b[o]   = (byte)((v >> 24) & 0xFF);
			b[o+1] = (byte)((v >> 16) & 0xFF);
			b[o+2] = (byte)((v >> 8) & 0xFF);
			b[o+3] = (byte)(v & 0xFF);
			return o+4;
		}
		private static int dumpConstantPool(byte[] b, int o, List<byte[]> cp) {
			o = dumpShort(b, o, (short)cp.size());
			int i = 0;
			for (byte[] e : cp) {
				if (i++ == 0) continue; // Skip first entry
				System.arraycopy(e, 0, b, o, e.length);
				o += e.length;
			}
			return o;
		}
		private static int dumpFields(byte[] b, int o, Field[] f) {
			o = dumpShort(b, o, (short)f.length);
			for (int i = 0; i < f.length; i++) {
				o = dumpShort(b, o, f[i].access_flags);
				o = dumpShort(b, o, f[i].name_index);
				o = dumpShort(b, o, f[i].descriptor_index);
				o = dumpAttributes(b, o, f[i].attributes);
			}
			return o;
		}
		private static int dumpMethods(byte[] b, int o, Method[] m) {
			o = dumpShort(b, o, (short)m.length);
			for (int i = 0; i < m.length; i++) {
				o = dumpShort(b, o, m[i].access_flags);
				o = dumpShort(b, o, m[i].name_index);
				o = dumpShort(b, o, m[i].descriptor_index);
				o = dumpAttributes(b, o, m[i].attributes);
			}
			return o;
		}
		private static int dumpAttributes(byte[] b, int o, Attribute[] a) {
			o = dumpShort(b, o, (short)a.length);
			for (int i = 0; i < a.length; i++) {
				o = dumpShort(b, o, a[i].attribute_name_index);
				o = dumpInt(b, o, a[i].info.length);
				System.arraycopy(a[i].info, 0, b, o, a[i].info.length);
				o += a[i].info.length;
			}
			return o;
		}
		public byte[] toByteArray() {
			byte[] b = new byte[computeLength()];
			int o = 0;
			o = dumpInt(b, o, magic);
			o = dumpShort(b, o, major);
			o = dumpShort(b, o, minor);
			o = dumpConstantPool(b, o, constant_pool);
			o = dumpShort(b, o, access_flags);
			o = dumpShort(b, o, this_class);
			o = dumpShort(b, o, super_class);
			o = dumpShort(b, o, (short)interfaces.length);
			for (int i = 0; i < interfaces.length; i++) {
				o = dumpShort(b, o, interfaces[i]);
			}
			o = dumpFields(b, o, fields);
			o = dumpMethods(b, o, methods);
			o = dumpAttributes(b, o, attributes);
			assert (o == b.length);
			return b;
		}
	}

	// FIXME: signed
	private static byte getByte(byte[] b, int o) {
		return (byte)(b[o] & 0xFF);
	}
	// FIXME: signed
	private static short getShort(byte[] b, int o) {
		return (short)(((b[o] & 0xFF) << 8) + (b[o+1] & 0xFF));
	}
	// FIXME: signed
	private static int getInt(byte[] b, int o) {
		return ((b[o] & 0xFF) << 24) + ((b[o+1] & 0xFF) << 16) + ((b[o+2] & 0xFF) << 8) + (b[o+3] & 0xFF);
	}

	// FIXME: signed
	private static byte readByte(InputStream in) throws IOException {
		return (byte)(in.read() & 0xFF);
	}
	// FIXME: signed
	private static short readShort(InputStream in) throws IOException {
		return (short)((in.read() & 0xFF) << 8 | (in.read() & 0xFF));
	}
	// FIXME: signed
	private static int readInt(InputStream in) throws IOException {
		return (int)((in.read() & 0xFF) << 24 | (in.read() & 0xFF) << 16 |
				     (in.read() & 0xFF) << 8 | (in.read() & 0xFF));
	}
	private static ClassFile parseClass(String name, InputStream in) throws IOException {
//		int total = in.available();
		ClassFile cf = new ClassFile();
		cf.magic = readInt(in);
		cf.major = readShort(in);
		cf.minor = readShort(in);
//		System.out.println("Before CP: "+(total-in.available()));
		cf.constant_pool = readConstantPool(name, in);
//		System.out.println("After CP: "+(total-in.available()));
		cf.access_flags = readShort(in);
		cf.this_class = readShort(in);
		cf.super_class = readShort(in);
//		System.out.println("Before IF: "+(total-in.available()));
		short interfaces_count = readShort(in);
		cf.interfaces = new short[interfaces_count];
		for (int i = 0; i < interfaces_count; i++)
			cf.interfaces[i] = readShort(in);
//		System.out.println("After IF: "+(total-in.available()));
//		System.out.println("Before FL: "+(total-in.available()));
		short fields_count = readShort(in);
		cf.fields = new ClassFile.Field[fields_count];
		for (int i = 0; i < fields_count; i++)
			cf.fields[i] = readField(in);
//		System.out.println("After FL: "+(total-in.available()));
//		System.out.println("Before MT: "+(total-in.available()));
		short methods_count = readShort(in);
		cf.methods = new ClassFile.Method[methods_count];
		for (int i = 0; i < methods_count; i++)
			cf.methods[i] = readMethod(in);
//		System.out.println("After MT: "+(total-in.available()));
//		System.out.println("Before AT: "+(total-in.available()));
		short attributes_count = readShort(in);
		cf.attributes = new ClassFile.Attribute[attributes_count];
		for (int i = 0; i < attributes_count; i++)
			cf.attributes[i] = readAttribute(in);
//		System.out.println("After AT: "+(total-in.available()));
		return cf;
	}
	private static ClassFile.Field readField(InputStream in) throws IOException {
		ClassFile.Field f = new ClassFile.Field();
		f.access_flags = readShort(in);
		f.name_index = readShort(in);
		f.descriptor_index = readShort(in);
		short attributes_count = readShort(in);
		f.attributes = new ClassFile.Attribute[attributes_count];
		for (int i = 0; i < attributes_count; i++)
			f.attributes[i] = readAttribute(in);
		return f;
	}
	private static ClassFile.Method readMethod(InputStream in) throws IOException {
		int total = in.available();
		ClassFile.Method m = new ClassFile.Method();
		m.access_flags = readShort(in);
		m.name_index = readShort(in);
		m.descriptor_index = readShort(in);
//		System.out.println("\tBefore AT: "+(total-in.available()));
		short attributes_count = readShort(in);
		m.attributes = new ClassFile.Attribute[attributes_count];
		for (int i = 0; i < attributes_count; i++) {
			m.attributes[i] = readAttribute(in);
//			System.out.println("\tAfter AT["+i+"]: "+(total-in.available()));
		}
//		System.out.println("\tAfter AT: "+(total-in.available()));
		return m;
	}
	private static ClassFile.Attribute readAttribute(InputStream in) throws IOException {
		ClassFile.Attribute a = new ClassFile.Attribute();
		a.attribute_name_index = readShort(in);
		int attribute_length = readInt(in);
		a.info = new byte[attribute_length];
		in.read(a.info);
		return a;
	}
	private static final int CONSTANT_Empty = 0;
	private static final int CONSTANT_Class = 7;
	private static final int CONSTANT_Fieldref = 9;
	private static final int CONSTANT_Methodref = 10;
	private static final int CONSTANT_InterfaceMethodref = 11;
	private static final int CONSTANT_String = 8;
	private static final int CONSTANT_Integer = 3;
	private static final int CONSTANT_Float = 4;
	private static final int CONSTANT_Long = 5;
	private static final int CONSTANT_Double = 6;
	private static final int CONSTANT_NameAndType = 12;
	private static final int CONSTANT_Utf8 = 1;
	private static final byte[] EMPTY = { CONSTANT_Empty };
	private static List<byte[]> readConstantPool(String name, InputStream in) throws IOException {
		int cp_size = ((in.read() & 0xFF) << 8 | (in.read() & 0xFF));
		byte[][] cp = new byte[cp_size][];
		cp[0] = EMPTY;
		for (int i = 1; i < cp_size; i++) {
			byte tag = (byte)in.read();
			switch (tag) {
				case CONSTANT_Class:
				case CONSTANT_String:
					cp[i] = new byte[] { tag, (byte)in.read(), (byte)in.read() };
					break;
				case CONSTANT_Fieldref:
				case CONSTANT_Methodref:
				case CONSTANT_InterfaceMethodref:
					cp[i] = new byte[] { tag, (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read() };
					break;
				case CONSTANT_Integer:
				case CONSTANT_Float:
					cp[i] = new byte[] { tag, (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read() };
					break;
				case CONSTANT_Long:
				case CONSTANT_Double:
					cp[i] = new byte[] { tag, (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read() };
					cp[++i] = EMPTY;
					break;
				case CONSTANT_NameAndType:
					cp[i] = new byte[] { tag, (byte)in.read(), (byte)in.read(),
											  (byte)in.read(), (byte)in.read() };
					break;
				case CONSTANT_Utf8:
					{
						byte l_h = (byte)in.read();
						byte l_l = (byte)in.read();
						int len = (l_h & 0xFF) << 8 | (l_l & 0xFF);
						cp[i] = new byte[len+3];
						cp[i][0] = tag; cp[i][1] = l_h; cp[i][2] = l_l;
						int off = 3;
						int n;
						while ((n = in.read(cp[i], off, len)) != len) {
							len -= n;
							off += n;
						}
					}
					break;
				default:
					throw new Error("Unknown constant pool tag ("+tag+") at entry "+i+" in "+name);
			}
		}
		return Arrays.asList(cp);
	}
}
