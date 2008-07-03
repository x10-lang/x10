package x10.runtime;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
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
	private final Class<? extends Annotation> PARAMETERS_CLASS;
	public X10RuntimeClassloader() {
		Class<? extends Annotation> pc = null; 
		try {
			pc = (Class<? extends Annotation>)loadClass(PARAMETERS);
		} catch (ClassNotFoundException e) { assert (false); }
		PARAMETERS_CLASS = pc;
	}

	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (name.equals(this.getClass().getName()))
			return this.getClass(); // short-circuit this class
	
		try {
			Class<?> c = findLoadedClass(name);
			if (c == null) {
				InputStream in = openClassFile(name);
				if (in == null)
					throw new IOException("Not found");
				byte[] contents = new byte[in.available()];
				in.read(contents);
				c = defineClass(name, contents);
				if (VERBOSE)
					System.out.println("Loaded "+c.getName()+" with "+c.getClassLoader()+" from "+this);
			}
			if (resolve)
				resolveClass(c);
			return c;
		} catch (Exception e) {
			// Oops, not found or not supposed to muck with this
			return super.loadClass(name, resolve);
		} catch (NoClassDefFoundError e) {
			// Oops, not found or not supposed to muck with this
			return super.loadClass(name, resolve);
		}
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		int dollar = name.indexOf('$');
		if (dollar == -1)
			return super.findClass(name);
		String base = name.substring(0, dollar);
		String[] actuals = demangle(name);
		InputStream in = openClassFile(base);
		if (in == null)
			throw new ClassNotFoundException(base);
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
				FileOutputStream fo = new FileOutputStream("cache/"+name.replace('.','_')+".class");
				fo.write(result);
				fo.close();
			}
			return defineClass(mangle(base, actuals), result);
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
			name += "$" + actuals[i].replace('.', '_').replaceAll("\\[\\]", "\\$\\$\\$_");
		return name;
	}
	private static String[] demangle(String type) {
		String thisType = type.replaceAll("\\$\\$\\$_", "[]").replace('_','.'); // FIXME: encoding
		int dollar = thisType.indexOf('$');
		return thisType.substring(dollar+1).split("\\$");
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
	private static final String CAST_SIG = "("+SIG_Object+SIG_String+")"+SIG_Object;
	private static final String COERCE_METHOD = "coerce";

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
		if (VERBOSE)
			System.out.println("   ->"+newName);
		// TODO: instantiate superclass and superinterfaces
		String[] formals = getFormalParameters(cf, typeName);
		assert (formals != null);
		assert (formals.length == actuals.length);
	
		for (int i = 0; i < cf.fields.length; i++) {
			ClassFile.Field f = cf.fields[i];
			String name = cf.getString(f.name_index);
			String signature = cf.getString(f.descriptor_index);
			if (VERBOSE)
				System.out.println("\tGot field "+name+" with signature "+signature);
			String newSignature = instantiateType(signature, formals, actuals);
			short new_index = createString(cf, newSignature);
			if (new_index != f.descriptor_index) {
				changedSignatures[f.descriptor_index] = new_index;
				if (VERBOSE)
					System.out.println("\t   ->" + newSignature);
				f.descriptor_index = new_index;
			}
		}

		String container = typeToSignature(newName);
		// FIXME: watch out for overloaded methods! (e.g., foo(int) vs. foo(T))
		for (int i = 0; i < cf.methods.length; i++) {
			ClassFile.Method m = cf.methods[i];
			String signature = cf.getString(m.descriptor_index);
			String name = cf.getString(m.name_index);
			byte[] code = cf.getMethodCode(m);
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
			MethodSig newSig = instantiateSignature(sig, formals, actuals);
			computeRemapping(sig, newSig, isStatic, remap);
			newSignature = newSig.toSignature();
			new_index = createString(cf, newSignature);
			if (new_index != m.descriptor_index) {
				changedSignatures[m.descriptor_index] = new_index;
				if (VERBOSE)
					System.out.println("\t   ->" + newSignature);
				m.descriptor_index = new_index;
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
		}
		// TODO: attributes
		// TODO: local variable tables
		cf.this_class = newTypeIndex;
	}

	public final Class<?> getClass(String name) {
		try {
			return loadClass(name, false);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	private HashMap<String, String[]> cache = new HashMap<String, String[]>();
	private String[] getFormals(int class_index, String[] formals, ClassFile cf, short[] changedSignatures) {
		if (cf.isInstance(class_index))
			return formals;
		String container = cf.getString(cf.getNameIndex(class_index));
		if (cache.containsKey(container))
			return cache.get(container);
		String[] parms = null;
		Class<?> containerClass = getClass(container.replace('/','.'));
		if (containerClass != null) {
			x10.generics.Parameters parameters = (x10.generics.Parameters)containerClass.getAnnotation(PARAMETERS_CLASS);
			if (parameters != null) {
				parms = parameters.value();
			}
		}
		cache.put(container, parms);
		return parms;
	}

	private static String instantiateType(String type, String[] formals, String[] actuals) {
		if (type.charAt(0) == '[')
			return "["+instantiateType(type.substring(1), formals, actuals);
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
		protected final short getExceptionHandler(int i) {
			return handlers[i];
		}
		protected final short getCaughtExceptionIndex(int i) {
			return catches[i];
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
			assert (index == SIG_int);
			push(type);
		}
		protected void process_aaload() {
			String index = pop();
			String array = pop();
			assert (isArray(array));
			assert (index == SIG_int);
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
			assert (index == SIG_int);
			assert (isArrayOf(array, type));
		}
		protected void process_aastore() {
			String val = pop();
			String index = pop();
			String array = pop();
			assert (isArray(array));
			assert (index == SIG_int);
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
			assert (locals[arg] == SIG_int);
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
			assert (val == SIG_int || val == SIG_boolean);
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
		protected void process_acmpC(int offset) {
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
				case 'Z': // FIXME: is this ok for booleans?
					putByte(code, o, BC_if_icmpeq + c - BC_if_acmpeq);
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
			assert (val == SIG_int);
			// TODO: set stack depth
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
			assert (val == SIG_int);
			// TODO: set stack depth
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
		private final String processFieldAccess(short field_ref_index, String receiver, String[] formals, String[] actuals, short[] changedSignatures) {
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
			formals = getFormals(class_index, formals, cf, changedSignatures);
			boolean isParameterized = formals != null && formals.length > 0;
			if (isParameterized) {
				if (receiver != null) { // get the type from the stack
					actuals = demangle(typeFromSignature(receiver));
				} else {
					// FIXME: what to do about static fields, e.g., List[T].f?
				}
				newContainer = mangle(container, actuals);
				newDescriptor = instantiateType(descriptor, formals, actuals);
				short new_field_ref = createFieldRef(cf, newContainer, name, newDescriptor);
				if (new_field_ref != field_ref_index) {
					short new_descriptor_index = cf.getDescriptorIndex(cf.getNameAndTypeIndex(new_field_ref));
					if (new_descriptor_index != descriptor_index) {
						if (VERBOSE)
							System.out.println("\t   ->" + newDescriptor);
						changedSignatures[descriptor_index] = new_descriptor_index;
					}
					if (cf.isInstance(class_index))
						cf.addInstance(cf.getClassIndex(new_field_ref));
					putShort(code, o+1, new_field_ref);
					changedSignatures[field_ref_index] = new_field_ref;
				}
			}
			return newDescriptor;
		}
		protected void process_getF(short field_ref_index, boolean isStatic, String signature, String[] actuals, String[] formals, short[] changedSignatures) {
			String receiver = null;
			if (!isStatic)
				receiver = pop(); // TODO: check type?
			String descriptor = processFieldAccess(field_ref_index, receiver, formals, actuals, changedSignatures);
			push(descriptor);
		}
		protected void process_putF(short field_ref_index, boolean isStatic, String signature, String[] actuals, String[] formals, short[] changedSignatures) {
			String val = pop();
			String receiver = null;
			if (!isStatic)
				receiver = pop(); // TODO: check type?
			// TODO: check value type?
			String descriptor = processFieldAccess(field_ref_index, receiver, formals, actuals, changedSignatures);
		}
		private int extractClassParameters(int x, String[] formals, String[] actuals, String[] result) {
			for (int i = 0; i < formals.length; i++) {
				int cload = getByte(code, o+x);
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
					signature = instantiateType(newSignature, formals, actuals);
				}
				result[i] = typeFromSignature(signature);
				int l = getBytecodeLength(o+x);
				// TODO: clear type arguments off the stack (need to also rewrite method signatures)
				//for (int j = 0; j < l; j++) putByte(code, o+x+j, BC_nop); // Clear assignment
				x += l;
			}
			return x;
		}
		// FIXME: what about static methods in parameterized types (e.g., List[T].create)?
		protected void process_invoke(short method_ref_index, String[] formals, String[] actuals, short[] changedSignatures, int c) {
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
			// FIXME: HACK -- special case StringBuilder.append
			if (!isInterface && !isStatic &&
					container.equals("java/lang/StringBuilder") && name.equals("append") &&
					signature.equals("(Ljava/lang/Object;)"+SIG_StringBuilder))
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
					   signature.equals("("+args[0]+")"+SIG_Object))
			{
				int x = 3;
				int cc = getByte(code, o+x);
				short cr = getShort(code, o+x+1);
				short cni = cf.getNameIndex(cr);
				assert (cc == BC_checkcast &&
						getFormal(typeToSignature(cf.getString(cni)), formals) != -1);
				for (int j = 0; j < x+3; j++) putByte(code, o+j, BC_nop); // Clear assignment
				push(args[0]);
				reinterpret();
				return;
			}
			formals = getFormals(class_index, formals, cf, changedSignatures);
			boolean isParameterized = formals != null && formals.length > 0;
			if (isParameterized) {
				// TODO: retrieve the annotations and make sure the method is annotated Synthetic
				if (isStatic && name.equals(INSTANCEOF_METHOD) && signature.equals(INSTANCEOF_SIG)) {
					int x = 3;
					String[] params = new String[formals.length];
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
						String[] params = new String[formals.length];
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
				} else if (!name.equals(INSTANCEOF_METHOD) && // already mangled
						   container_index == cf.getNameIndex(cf.this_class)) {
					String[] params = actuals;
					if (!isStatic) { // get the type from the stack
						params = demangle(typeFromSignature(args[0]));
					} else {
						// FIXME: what to do about static methods, e.g., List[T].create?
					}
					newContainer = mangle(container, params);
					MethodSig sig = MethodSig.fromSignature(signature);
					MethodSig newSig = instantiateSignature(sig, formals, params);
					newSignature = newSig.toSignature();
					short new_method_ref = createMethodRef(cf, newContainer, name, newSignature, isInterface);
					if (new_method_ref != method_ref_index) {
						short new_descriptor_index = cf.getDescriptorIndex(cf.getNameAndTypeIndex(new_method_ref));
						if (new_descriptor_index != descriptor_index) {
							if (VERBOSE)
								System.out.println("\t   ->" + newSignature);
							changedSignatures[descriptor_index] = new_descriptor_index;
						}
						if (cf.isInstance(class_index))
							cf.addInstance(cf.getClassIndex(new_method_ref));
						putShort(code, o+1, new_method_ref);
						changedSignatures[method_ref_index] = new_method_ref;
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
		protected void process_new(int type_index, String[] formals, String[] actuals, short[] changedSignatures) {
			short name_index = cf.getNameIndex(type_index);
			String type = cf.getString(name_index);
			formals = getFormals(type_index, formals, cf, changedSignatures);
			boolean isParameterized = formals != null && formals.length > 0;
			if (VERBOSE)
				System.out.println("Creating instance of "+type+(isParameterized?" (parameterized)":""));
			if (isParameterized) {
				String[] params = new String[formals.length];
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
			assert (count == SIG_int);
			push(arrayOf(PRIM_ARRAY_TO_JAVA_TYPE[type]));
		}
		protected void process_anewarray(int base_type_index, String[] formals, String[] actuals, short[] changedSignatures) {
			short name_index = cf.getNameIndex(base_type_index);
			String base = cf.getString(name_index);
			if (VERBOSE)
				System.out.println("Array of "+base);
			String count = pop();
			assert (count == SIG_int);
			String signature = typeToSignature(base);
			String newSignature = instantiateType(signature, formals, actuals);
			if (isPrimitive(newSignature)) { // cannot be the same
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
		protected void process_multianewarray(int type_index, int dimensions, String[] formals, String[] actuals, short[] changedSignatures) {
			short name_index = cf.getNameIndex(type_index);
			String type = cf.getString(name_index);
			for (int i = 0; i < dimensions; i++) {
				String count = pop();
				assert (count == SIG_int);
			}
			String signature = typeToSignature(type);
			String newSignature = instantiateType(signature, formals, actuals);
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
		protected void process_checkcast(short type_ref_index, String[] formals, String[] actuals, short[] changedSignatures) {
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
		protected void process_instanceof(short type_ref_index, String[] formals, String[] actuals, short[] changedSignatures) {
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
			for (o = off; o < off + len; o++) {
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
				case BC_if_acmpne:      process_acmpC(getShort(code, o+1)); break;
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
				case BC_getstatic:      process_getF(getShort(code, o+1), c == BC_getstatic, signature, actuals, formals, changedSignatures); break;
				case BC_putfield:
				case BC_putstatic:      process_putF(getShort(code, o+1), c == BC_putstatic, signature, actuals, formals, changedSignatures); break;
				case BC_invokevirtual:
				case BC_invokeinterface:
				case BC_invokespecial:
				case BC_invokestatic:   process_invoke(getShort(code, o+1), formals, actuals, changedSignatures, c); break;
				case BC_new:            process_new(getShort(code, o+1), formals, actuals, changedSignatures); break;
				case BC_newarray:       process_newarray(getByte(code, o+1)); break;
				case BC_anewarray:      process_anewarray(getShort(code, o+1), formals, actuals, changedSignatures); break;
				case BC_multianewarray: process_multianewarray(getShort(code, o+1), getByte(code, o+3), formals, actuals, changedSignatures); break;
				case BC_arraylength:    process_arraylength(); break;
				case BC_athrow:         process_athrow(); break;
				case BC_monitorenter:
				case BC_monitorexit:    process_monitorOP(); break;
				case BC_checkcast:      process_checkcast(getShort(code, o+1), formals, actuals, changedSignatures); break;
				case BC_instanceof:     process_instanceof(getShort(code, o+1), formals, actuals, changedSignatures); break;
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
					x = pad + 12 + (hi-lo+1)*4;
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
		ClassFile.Annotation a = cf.findAnnotation(typeToSignature(PARAMETERS));
		if (a == null)
			return null;
		if (VERBOSE)
			System.out.println("Got annotation: "+PARAMETERS);
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
			if (VERBOSE)
				System.out.println("\t"+res[j]);
		}
		return res;
	}
}
