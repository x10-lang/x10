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

package x10.runtime.impl.java;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A utility for pre-loading a given class and all non-system classes that
 * it references.
 * The entrypoint is a static <code>preLoad()</code> method.  Calling
 * <code>preLoad(c, true)</code> will also intern all strings in the
 * classes' constant pool.
 */
public class PreLoader {
	
	// WIP XTENLANG-2722
	public static final boolean supportExecutableJar = false;
	
	public static void main(String[] args) {
//		try {
//			String name = getClassFile(PreLoader.class);
//			if (args.length > 0)
//				name = args[0];
//			InputStream in = getClassAsStream(name, PreLoader.class);
//			byte[][] cp = getConstantPool(in);
//			String[] ref_classes = getReferencedClasses(name, cp);
//			for (int i = 0; i < ref_classes.length; i++)
//				System.err.println(i+": "+ref_classes[i]);
//		} catch (IOException e) { e.printStackTrace(); }
		if (args.length > 0)
			preLoad(args[0], PreLoader.class);
		else
			preLoad(PreLoader.class);
	}
	private static final String TRUE = "true";
	private static final Map<String, String> inited = new HashMap<String, String>();
	private static final ClassLoader bootstrap = Object.class.getClassLoader();
	private static boolean isSystemClass(Class<?> c) {

		// N.B. we must return false for all x10 classes since they need preloading to make static initialization work properly
		boolean isSystemClass = false;
		
        boolean precisePreload = Boolean.getBoolean("x10.PRECISE_PRELOAD_TEST");
           	        
		if (!precisePreload && !supportExecutableJar) {
			
	    isSystemClass = c.getClassLoader() == bootstrap;
	    
		} else {
			
	    java.lang.reflect.Method rttMethod = null;
	    try {
			rttMethod = c.getMethod("$getRTT", (Class<?>[]) null);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	    java.lang.reflect.Field rttField = null;
		try {
			// N.B. this is needed because interface does not have $getRTT method but it has $RTT field
			rttField = c.getField("$RTT");
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		}
	    isSystemClass = rttMethod == null && rttField == null;
	    
		}
		
	    return isSystemClass;
	}
	/**
	 * Recursively pre-load the given class and all the classes it statically
	 * references.
	 * @param c the class to pre-load
	 */
	public static void preLoad(Class<?> c) {
		preLoad(getClassFile(c), c);
	}
	/**
	 * Recursively pre-load the given class and all the classes it statically
	 * references, optionally interning string constants.
	 * @param c the class to pre-load
	 * @param intern whether to intern string constants
	 */
        public static void preLoad(Class<?> c, boolean intern) {
            preLoad(c, null, intern);
        }
        /**
         * Recursively pre-load the given class and all the classes it statically
         * references, optionally interning string constants.
         * @param c the class to pre-load
         * @param extraClasses the extra classes to pre-load
         * @param intern whether to intern string constants
         */
        public static void preLoad(Class<?> c, List<String> extraClasses, boolean intern) {
            if (c != null && isSystemClass(c)) return;
            // We need to load all X10 classes so that all statics can be initialized at boot time
            loadX10Classes();
            if (c != null) {
            	preLoad(getClassFile(c), c, intern);
            }
            if (extraClasses != null) {
                // preload extra classes to make static initialization work properly with reflection
                for (String name : extraClasses) {
                    try {
                        Class<?> extraClass = Class.forName(name);
                        preLoad(getClassFile(extraClass), extraClass, intern);
                    } catch (ClassNotFoundException e) {
                    }
                }
            }
        }
	private static void preLoad(String name, Class<?> c) {
		preLoad(name, c, true);
	}
	private static void preLoad(String name, Class<?> c, boolean intern) {
		if (inited.get(name) != null) return;
		inited.put(name, TRUE);
//		System.err.println("Pre-loading '"+name+"'");
		try {
			InputStream in = getClassAsStream(name, c);
			byte[][] cp = getConstantPool(name, in);
//			for (int j = 1; j < cp.length; j++)
//				printConstantPoolEntry(cp, j, System.err);
			if (intern) internStrings(cp);
			String[] ref_classes = getReferencedClasses(name, cp);
			for (int i = 0; i < ref_classes.length; i++) {
				String nm = toClassName(ref_classes[i]);
				try {
					Class<?> u = Class.forName(nm);
//					System.err.println(i+": "+nm+" -> "+u);
					// Skip arrays
					if (nm.charAt(0) == '[') continue;
					// Skip system classes
					if (isSystemClass(u)) continue;
					preLoad(toFileName(nm), c, intern);
				} catch (ClassNotFoundException e) {
//					System.err.println(i+": "+nm+" not found");
				} catch (NoClassDefFoundError e) {
					System.out.println("Preloader: could not load " + e.getMessage() + " (ignored NoClassDefFoundError).");
				} catch (java.lang.ExceptionInInitializerError e) {
					System.out.println("Preloader: could not load " + e.getMessage() + " (ignored ExceptionInInitializerError).");
				} catch (java.lang.UnsatisfiedLinkError e) {
					System.out.println("Preloader: could not load " + e.getMessage() + " (ignored UnsatisfiedLinkError).");
				}
			}
		} catch (IOException e) { e.printStackTrace(System.err); assert false; }
	}

    private static void loadX10Classes() {
        try {
            ClassLoader cl = java.lang.Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = cl.getResources("x10");
            List<File> dirs = new ArrayList<File>();
            List<JarFile> jars = new ArrayList<JarFile>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if ("jar".equals(resource.getProtocol())) {
                    String path = resource.getPath();
                    int i = path.indexOf("!");
                    int j = path.indexOf(":");
                    String jarFileName = path.substring(j + 1, i);
                    jars.add(new JarFile(jarFileName));
                } else {
                    dirs.add(new File(resource.getFile()));
                }
            }
            for (File directory : dirs) {
                findClasses(directory, "x10");
            }
            for (JarFile jarFile: jars) {
                findClasses(jarFile, "x10");
            }
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    private static void findClasses(File directory, String packageName) throws ClassNotFoundException {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        findClasses(file, packageName + "." + file.getName());
                    } else if (file.getName().endsWith(".class")) {
                        Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                    }
                }
            }
        }
    }

	private static void findClasses(JarFile jarFile, String packageName) throws ClassNotFoundException {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith(packageName) && name.endsWith(".class")) {
                String className = name.replaceAll("/", ".");
                Class.forName(className.substring(0, className.length() - 6));
            }
        }
    }

	private static InputStream getClassAsStream(Class<?> c) throws IOException {
		return getClassAsStream(getClassFile(c), c);
	}
	private static InputStream getClassAsStream(String name, Class<?> c) throws IOException {
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
	private static String getClassFile(Class<?> c) {
		return toFileName(c.getName());
	}
	private static byte[] getClassBytes(String name, Class<?> c) throws IOException {
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
//				System.err.println("Interned '"+s+"'");
			} catch (UnsupportedEncodingException e) { }
		}
	}
	private static String[] getReferencedClasses(String name, byte[][] cp) {
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
				throw new Error("Class entry "+i+" refers to non-utf ("+cp[name_idx][0]+") entry "+name_idx+" in "+name);
			int name_len = (cp[name_idx][1] & 0xFF) << 8 | (cp[name_idx][2] & 0xFF);
			try {
				ref_classes[j++] = new String(cp[name_idx], 3, name_len, "UTF-8").intern();
			} catch (UnsupportedEncodingException e) { }
		}
		return ref_classes;
	}
	private static byte[][] getConstantPool(String name, byte[] classbytes) {
		try {
			return getConstantPool(name, new ByteArrayInputStream(classbytes));
		} catch (IOException e) {
			throw new Error("Unable to read from an array of bytes");
		}
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
	private static final byte[] EMPTY = new byte[] { CONSTANT_Empty };
	private static byte[][] getConstantPool(String name, InputStream in) throws IOException {
		for (int i = 0; i < 8; i++) in.read();
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
//			printConstantPoolEntry(cp, i-1, System.err);
//			printConstantPoolEntry(cp, i, System.err);
		}
		return cp;
	}
	private static final String[] TAGS = { "EMPTY", "UTF8", null, "INTEGER",
		"FLOAT", "LONG", "DOUBLE", "CLASS", "STRING", "FIELDREF", "METHODREF",
		"IMETHOD", "NAME/TYPE" };
	private static void printConstantPoolEntry(byte[][] cp, int i, PrintStream out) {
		byte[] entry = cp[i];
		byte tag = entry[0];
		int tmp1, tmp2;
		out.print(i+": "+TAGS[tag]);
		switch (tag) {
			case CONSTANT_Empty:
				out.println();
				break;
			case CONSTANT_Class:
			case CONSTANT_String:
				tmp1 = (entry[1] & 0xFF) << 8 | (entry[2] & 0xFF);
				out.println("["+tmp1+"]");
//				out.print(" -> ");
//				printConstantPoolEntry(cp, tmp1, out);
				break;
			case CONSTANT_Fieldref:
			case CONSTANT_Methodref:
			case CONSTANT_InterfaceMethodref:
				tmp1 = (entry[1] & 0xFF) << 8 | (entry[2] & 0xFF);
				tmp2 = (entry[3] & 0xFF) << 8 | (entry[4] & 0xFF);
				out.println("["+tmp1+","+tmp2+"]");
//				out.print(" -> ");
//				printConstantPoolEntry(cp, tmp1, out);
//				out.print(" -> ");
//				printConstantPoolEntry(cp, tmp2, out);
				break;
			case CONSTANT_Integer:
			case CONSTANT_Float:
				tmp1 = ((entry[1] & 0xFF) << 8 | (entry[2] & 0xFF)) << 16 |
					   ((entry[3] & 0xFF) << 8 | (entry[4] & 0xFF));
				if (tag == CONSTANT_Float)
					out.println(" = "+toFloat(tmp1));
				else
					out.println(" = "+tmp1);
				break;
			case CONSTANT_Long:
			case CONSTANT_Double:
				tmp1 = ((entry[1] & 0xFF) << 8 | (entry[2] & 0xFF)) << 16 |
					   ((entry[3] & 0xFF) << 8 | (entry[4] & 0xFF));
				tmp2 = ((entry[5] & 0xFF) << 8 | (entry[6] & 0xFF)) << 16 |
					   ((entry[7] & 0xFF) << 8 | (entry[8] & 0xFF));
				if (tag == CONSTANT_Double)
					out.println(" = "+toDouble(tmp1,tmp2));
				else
					out.println(" = "+toLong(tmp1,tmp2));
				break;
			case CONSTANT_NameAndType:
				tmp1 = (entry[1] & 0xFF) << 8 | (entry[2] & 0xFF);
				tmp2 = (entry[3] & 0xFF) << 8 | (entry[4] & 0xFF);
				out.println("["+tmp1+","+tmp2+"]");
//				out.print(" -> ");
//				printConstantPoolEntry(cp, tmp1, out);
//				out.print(" -> ");
//				printConstantPoolEntry(cp, tmp2, out);
				break;
			case CONSTANT_Utf8:
				tmp1 = (entry[1] & 0xFF) << 8 | (entry[2] & 0xFF);
				try {
					out.println(" = \""+new String(entry, 3, tmp1, "UTF-8")+"\"");
				} catch (UnsupportedEncodingException e) { }
				break;
			default:
				throw new Error("Unknown constant pool tag ("+tag+") at entry "+i);
		}
	}
	private static float toFloat(int v) {
		return Float.intBitsToFloat(v);
	}
	private static long toLong(int h, int l) {
		long r = h;
		return r << 32 + l;
	}
	private static double toDouble(int h, int l) {
		long r = h;
		return Double.longBitsToDouble(r << 32 + l);
	}
}

