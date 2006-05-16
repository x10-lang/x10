package x10.runtime;

import java.io.*;
import java.util.*;

/**
 * A utility for pre-loading a given class and all non-system classes that
 * it references.
 * The entrypoint is a static <code>preLoad()</code> method.  Calling
 * <code>preLoad(c, true)</code> will also intern all strings in the
 * classes' constant pool.
 *
 * @author Igor Peshansky
 */
public class PreLoader {
	public static void main(String[] args) {
//		try {
//			String name = getClassName(PreLoader.class);
//			if (args.length > 0)
//				name = args[0];
//			InputStream in = getClassAsStream(name, PreLoader.class);
//			byte[][] cp = getConstantPool(in);
//			String[] ref_classes = getReferencedClasses(cp);
//			for (int i = 0; i < ref_classes.length; i++)
//				System.out.println(i+": "+ref_classes[i]);
//		} catch (IOException e) { e.printStackTrace(); }
		if (args.length > 0)
			preLoad(args[0], PreLoader.class);
		else
			preLoad(PreLoader.class);
	}
	private static final String TRUE = "true";
	private static final Map inited = new HashMap();
	private static final ClassLoader bootstrap = Object.class.getClassLoader();
	public static void preLoad(Class c) {
		preLoad(getClassName(c), c);
	}
	public static void preLoad(Class c, boolean intern) {
		preLoad(getClassName(c), c, intern);
	}
	public static void preLoad(String name, Class c) {
		preLoad(name, c, true);
	}
	public static void preLoad(String name, Class c, boolean intern) {
		if (inited.get(name) != null) return;
		inited.put(name, TRUE);
//		System.out.println("Pre-loading '"+name+"'");
		try {
			InputStream in = getClassAsStream(name, c);
			byte[][] cp = getConstantPool(in);
			if (intern) internStrings(cp);
			String[] ref_classes = getReferencedClasses(cp);
			for (int i = 0; i < ref_classes.length; i++) {
				String nm = toClassName(ref_classes[i]);
				try {
					Class u = Class.forName(nm);
//					System.out.println(i+": "+nm+" -> "+u);
					if (u.getClassLoader() == bootstrap) continue;
					preLoad(toFileName(nm), c, intern);
				} catch (ClassNotFoundException e) {
//					System.out.println(i+": "+nm+" not found");
				}
			}
		} catch (IOException e) { assert false; }
	}
	private static InputStream getClassAsStream(Class c) throws IOException {
		return getClassAsStream(getClassName(c), c);
	}
	private static InputStream getClassAsStream(String name, Class c) throws IOException {
		InputStream cin = c.getClassLoader().getResourceAsStream(name);
		if (cin == null)
			throw new IOException("Class file "+name+" cannot be found");
		return cin;
	}
	private static String toClassName(String n) {
		return n.replace('/','.');
	}
	private static String toFileName(String n) {
		return n.replace('.','/')+".class";
	}
	private static String getClassName(Class c) {
		return toFileName(c.getName());
	}
	private static byte[] getClassBytes(String name, Class c) throws IOException {
		InputStream cin = getClassAsStream(name, c);
		byte[] classbytes = new byte[cin.available()];
		if (cin.read(classbytes) != classbytes.length)
			throw new IOException("Could not read class");
		return classbytes;
	}
	private static void internStrings(byte[][] cp) {
		for (int i = 1; i < cp.length; i++) {
			if (cp[i][0] != CONSTANT_Utf8) continue;
			int len = (cp[i][1] & 0xFF) << 8 | (cp[i][2] & 0xFF);
			try {
				String s = new String(cp[i], 3, len, "UTF-8").intern();
//				System.out.println("Interned '"+s+"'");
			} catch (UnsupportedEncodingException e) { }
		}
	}
	private static String[] getReferencedClasses(byte[][] cp) {
		int num_classes = 0;
		for (int i = 1; i < cp.length; i++)
			if (cp[i][0] == CONSTANT_Class)
				num_classes++;
		String[] ref_classes = new String[num_classes];
		int j = 0;
		for (int i = 1; i < cp.length; i++) {
			if (cp[i][0] != CONSTANT_Class) continue;
			int name_idx = (cp[i][1] & 0xFF) << 8 | (cp[i][2] & 0xFF);
			if (cp[name_idx][0] != CONSTANT_Utf8)
				throw new Error("Class entry "+i+" refers to non-utf entry "+name_idx);
			int name_len = (cp[name_idx][1] & 0xFF) << 8 | (cp[name_idx][2] & 0xFF);
			try {
				ref_classes[j++] = new String(cp[name_idx], 3, name_len, "UTF-8").intern();
			} catch (UnsupportedEncodingException e) { }
		}
		return ref_classes;
	}
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
	private static byte[][] getConstantPool(InputStream in) throws IOException {
		for (int i = 0; i < 8; i++) in.read();
		int cp_size = ((in.read() & 0xFF) << 8 | (in.read() & 0xFF));
		byte[][] cp = new byte[cp_size][];
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
						in.read(cp[i], 3, len);
					}
					break;
				default:
					throw new Error("Unknown constant pool tag: "+tag);
			}
		}
		return cp;
	}
	private static byte[][] getConstantPool(byte[] classbytes) {
		try {
			return getConstantPool(new ByteArrayInputStream(classbytes));
		} catch (IOException e) {
			throw new Error("Unable to read from an array of bytes");
		}
	}
}

