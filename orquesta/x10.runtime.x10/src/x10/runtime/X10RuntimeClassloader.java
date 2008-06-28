package x10.runtime;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import x10.runtime.bytecode.ClassFile;

import static x10.runtime.bytecode.ByteArrayUtil.*;
import static x10.runtime.bytecode.BytecodeConstants.*;
import static x10.runtime.bytecode.BytecodeUtil.getBytecodeLength;
import static x10.runtime.bytecode.ClassFileUtil.*;

public class X10RuntimeClassloader extends ClassLoader {
	private static final boolean DUMP_GENERATED_CLASSES = true;
	private static final boolean VERBOSE = true;
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
				if (VERBOSE)
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
			ClassFile cf = ClassFile.parseClass(base, in);
			instantiate(cf, actuals);
			transform(cf);
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
			name += "$" + actuals[i].replace('.', '_');
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
		if (VERBOSE)
			System.out.println("Got class "+typeName);
		short[] changedSignatures = new short[cf.constantPoolSize()];
		String newName = mangle(typeName, actuals);
		nameIndex = cf.addString(newName);
		cf.setNameIndex(cf.this_class, nameIndex);
		if (VERBOSE)
			System.out.println("   ->"+newName);
		String[] formals = getFormalParameters(cf, typeName);
		assert (formals != null);
		assert (formals.length == actuals.length);
		for (int i = 0; i < cf.fields.length; i++) {
			ClassFile.Field f = cf.fields[i];
			String name = cf.getString(f.name_index);
			String signature = cf.getString(f.descriptor_index);
			if (VERBOSE)
				System.out.println("\tGot field "+name+" with signature "+signature);
			short new_index = f.descriptor_index < changedSignatures.length
							? changedSignatures[f.descriptor_index]
							: f.descriptor_index;
			if (new_index == 0) {
				int fi = getFormalIndex(signature, formals);
				if (fi != -1) {
					String newSignature = typeToSignature(actuals[fi]);
					new_index = cf.addString(newSignature);
					changedSignatures[f.descriptor_index] = new_index;
				}
			}
			if (new_index != 0 && new_index != f.descriptor_index) {
				if (VERBOSE)
					System.out.println("\t   ->" + cf.getString(new_index));
				f.descriptor_index = new_index;
			}
		}
		// FIXME: watch out for overloaded methods!
		for (int i = 0; i < cf.methods.length; i++) {
			ClassFile.Method m = cf.methods[i];
			String signature = cf.getString(m.descriptor_index);
			String name = cf.getString(m.name_index);
			byte[] code = cf.getMethodCode(m);
			int off = cf.getMethodCodeOffset(code);
			int len = cf.getMethodCodeLength(code);
			int max_stack = cf.getMethodStack(code);
			int max_locals = cf.getMethodLocals(code);
			int adjust = (m.access_flags & ACC_STATIC) == 0 ? 1 : 0;
			char[] remap = new char[1+max_locals]; // return type + locals
			if (VERBOSE)
				System.out.println("\tGot method "+name+" with signature "+signature);
			short new_index = m.descriptor_index < changedSignatures.length
							? changedSignatures[m.descriptor_index]
							: m.descriptor_index;
			if (new_index == 0) {
				MethodSig sig = MethodSig.fromSignature(signature);
				assert (max_locals >= adjust+sig.argTypes.length);
				boolean changed = false;
				int fi = getFormalIndex(sig.returnType, formals);
				if (fi != -1) {
					sig.returnType = typeToSignature(actuals[fi]);
					if (isPrimitive(sig.returnType))
						remap[0] = sig.returnType.charAt(0);
					changed = true;
				}
				for (int j = 0; j < sig.argTypes.length; j++) {
					fi = getFormalIndex(sig.argTypes[j], formals);
					if (fi != -1) {
						sig.argTypes[j] = typeToSignature(actuals[fi]);
						if (isPrimitive(sig.argTypes[j]))
							remap[1+adjust+j] = sig.argTypes[j].charAt(0);
						changed = true;
					}
				}
				if (changed) {
					String newSignature = sig.toSignature();
					new_index = cf.addString(newSignature);
					changedSignatures[m.descriptor_index] = new_index;
				}
			}
			if (new_index != 0 && new_index != m.descriptor_index) {
				if (VERBOSE)
					System.out.println("\t   ->" + cf.getString(new_index));
				m.descriptor_index = new_index;
			}
			short[] reassignLocals = new short[max_locals];
			short nextLocal = 0;
			for (int j = 0; j < reassignLocals.length; j++, nextLocal++) {
				reassignLocals[j] = nextLocal;
				if (isWidePrimitive(remap[1+j]))
					nextLocal++;
			}
			// FIXME: Ugh, partial abstract interpretation
			// TODO: arrays
			char[] stack = new char[max_stack*2+1];
			int sp = 0;
			for (int o = off; o < off + len; o++) {
				int c = code[o] & 0xFF;
				switch (c) {
				case BC_aload: case BC_astore:
				{
					int arg = code[o+1] & 0xFF;
					if (c == BC_astore && sp > 0 && stack[sp] != '\0' && stack[sp] != 'L' && stack[sp] != '[') {
						if (remap[1+arg] == '\0')
							remap[1+arg] = stack[sp];
						sp--;
					}
					if (remap[1+arg] != '\0') {
						code[o] -= atopDelta(remap[1+arg]);
						if (c == BC_aload)
							stack[++sp] = remap[1+arg];
					}
					if (reassignLocals[arg] != arg)
						code[o+1] = (byte)reassignLocals[arg];
					break;
				}
				case BC_areturn:
				{
					if (remap[0] != '\0')
						code[o] -= atopDelta(remap[0]);
					break;
				}
				case BC_aload_0: case BC_aload_1: case BC_aload_2: case BC_aload_3:
				{
					int arg = c - BC_aload_0;
					if (reassignLocals[arg] != arg) {
						if (reassignLocals[arg] <= 3)
							code[o] = (byte)(BC_aload_0 + reassignLocals[arg]);
						else // FIXME
							throw new IllegalArgumentException("bytecode size change!!!");
					}
					if (remap[1+arg] != '\0') {
						code[o] -= atopDelta(remap[1+arg]) * 4;
						stack[++sp] = remap[1+arg];
					}
					break;
				}
				case BC_astore_0: case BC_astore_1: case BC_astore_2: case BC_astore_3:
				{
					int arg = c - BC_astore_0;
					if (reassignLocals[arg] != arg) {
						if (reassignLocals[arg] <= 3)
							code[o] = (byte)(BC_astore_0 + reassignLocals[arg]);
						else // FIXME
							throw new IllegalArgumentException("bytecode size change!!!");
					}
					if (sp > 0 && stack[sp] != '\0' && stack[sp] != 'L' && stack[sp] != '[') {
						if (remap[1+arg] == '\0')
							remap[1+arg] = stack[sp];
						sp--;
					}
					if (remap[1+arg] != '\0')
						code[o] -= atopDelta(remap[1+arg]) * 4;
					break;
				}
				case BC_if_acmpeq: case BC_if_acmpne:
				{
					// HACK: Add padding instructions by casting the expression to boolean
					// (e.g., "if ((boolean)(expr))" and hope javac doesn't optimize it away.
					// With javac, that adds 8 bytes (only need 1 for lcmp/fcmp*/dcmp*).
					// This worked on Windows with Sun 1.5, IBM 1.5, IBM 1.6.
					// TODO: test on a Mac, Linux, and AIX (should be same, it's Java).
					// TODO: test jikes.
					char r = '\0';
					if (sp > 0 && stack[sp] != '\0' && stack[sp] != 'L' && stack[sp] != '[') {
						assert (stack[sp-1] == stack[sp]);
						r = stack[sp];
						sp -= 2;
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
						case 'Z': // FIXME: is this ok?
							code[o] = (byte)(BC_if_icmpeq + c - BC_if_acmpeq);
							break;
						case 'D':
							code[o] = (byte)BC_dcmpl;
							for (int x = 1; x < 8; x++) code[o+x] = BC_nop;
							code[o+8] = (byte)(BC_ifeq + c - BC_if_acmpeq);
							break;
						case 'F':
							code[o] = (byte)BC_fcmpl;
							for (int x = 1; x < 8; x++) code[o+x] = BC_nop;
							code[o+8] = (byte)(BC_ifeq + c - BC_if_acmpeq);
							break;
						case 'J':
							code[o] = (byte)BC_lcmp;
							for (int x = 1; x < 8; x++) code[o+x] = BC_nop;
							code[o+8] = (byte)(BC_ifeq + c - BC_if_acmpeq);
							break;
						}
					}
					break;
				}
				case BC_invokevirtual: case BC_invokeinterface:
				case BC_invokespecial: case BC_invokestatic:
				{
					short method_ref_index = getShort(code, o+1);
					short class_index = cf.getClassIndex(method_ref_index);
					short container_index = cf.getNameIndex(class_index);
					short name_and_type_index = cf.getNameAndTypeIndex(method_ref_index);
					short name_index = cf.getNameIndex(name_and_type_index);
					short descriptor_index = cf.getDescriptorIndex(name_and_type_index);
					String container = cf.getString(container_index);
					String id = cf.getString(name_index);
					String descriptor = cf.getString(descriptor_index);
					if (VERBOSE)
						System.out.println("Call to "+container+"."+id+descriptor);
					// FIXME: Hack -- special case StringBuilder.append
					if (container.equals("java/lang/StringBuilder") && id.equals("append") &&
							descriptor.equals("(Ljava/lang/Object;)Ljava/lang/StringBuilder;"))
					{
						if (sp > 0 && stack[sp] != '\0' && stack[sp] != 'L' && stack[sp] != '[') {
							String newSignature = "(" + stack[sp] + ")Ljava/lang/StringBuilder;" ;
							sp--;
							short new_method_ref = findMethodRef(cf, container, id, newSignature);
							if (new_method_ref == 0) {
								// Build a whole new method reference (share container and name)
								short new_descriptor = cf.addString(newSignature);
								short new_name_and_type = cf.addNameAndType(name_index, new_descriptor);
								new_method_ref = cf.addMethodRef(class_index, new_name_and_type); 
							}
							putShort(code, o+1, new_method_ref);
							break;
						}
					}
					short new_descriptor = descriptor_index < changedSignatures.length
										 ? changedSignatures[descriptor_index]
					                     : descriptor_index;
					if (new_descriptor == 0) {
						MethodSig sig = MethodSig.fromSignature(signature);
						boolean changed = false;
						int fi = getFormalIndex(sig.returnType, formals);
						if (fi != -1) {
							sig.returnType = typeToSignature(actuals[fi]);
							changed = true;
						}
						for (int j = 0; j < sig.argTypes.length; j++) {
							fi = getFormalIndex(sig.argTypes[j], formals);
							if (fi != -1) {
								sig.argTypes[j] = typeToSignature(actuals[fi]);
								changed = true;
							}
						}
						if (changed) {
							String newSignature = sig.toSignature();
							new_descriptor = cf.addString(newSignature);
							changedSignatures[descriptor_index] = new_descriptor;
						}
					}
					sp = 0;
					if (new_descriptor != 0 && new_descriptor != descriptor_index) {
						String newSignature = cf.getString(new_descriptor);
						if (VERBOSE)
							System.out.println("\t   ->" + newSignature);
						stack[++sp] = newSignature.charAt(newSignature.indexOf(')')+1);
						cf.setDescriptorIndex(name_and_type_index, new_descriptor);
					}
					break;
				}
				case BC_getfield: case BC_putfield:
				case BC_getstatic: case BC_putstatic:
				{
					short field_ref_index = getShort(code, o+1);
					short container_index = cf.getNameIndex(cf.getClassIndex(field_ref_index));
					short name_and_type_index = cf.getNameAndTypeIndex(field_ref_index);
					short name_index = cf.getNameIndex(name_and_type_index);
					short descriptor_index = cf.getDescriptorIndex(name_and_type_index);
					String container = cf.getString(container_index);
					String id = cf.getString(name_index);
					String descriptor = cf.getString(descriptor_index);
					if (VERBOSE)
						System.out.println("Access to "+container+"."+id+" "+descriptor);
					short new_descriptor = descriptor_index < changedSignatures.length
										 ? changedSignatures[descriptor_index]
										 : descriptor_index;
					if (new_descriptor == 0) {
						int fi = getFormalIndex(signature, formals);
						if (fi != -1) {
							String newSignature = typeToSignature(actuals[fi]);
							new_descriptor = cf.addString(newSignature);
							changedSignatures[descriptor_index] = new_descriptor;
						}
					}
					if (new_descriptor != 0 && new_descriptor != descriptor_index) {
						String newSignature = cf.getString(new_descriptor);
						if (VERBOSE)
							System.out.println("\t   ->" + newSignature);
						stack[++sp] = newSignature.charAt(0);
						cf.setDescriptorIndex(name_and_type_index, new_descriptor);
					}
					break;
				}
				default:
					// FIXME: clearing the stack here: won't work for fancy bytecode, but should in most cases
					sp = 0;
				}
				o += getBytecodeLength(code, o, ClassFile.MethodCodeOffset)-1;
			}
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
	}

	private short findMethodRef(ClassFile cf, String declarer, String name, String signature) {
		short s = -1;
		while ((s = cf.findEntry(ClassFile.CONSTANT_Methodref, s+1)) != -1) {
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

	private static final String PARAMETERS = "Lx10/generics/Parameters;";
	private static String[] getFormalParameters(ClassFile cf, String typeName) {
		ClassFile.Annotation a = cf.findAnnotation(PARAMETERS);
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
