/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package x10c.smap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import polyglot.util.InternalCompilerError;

import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.ClassReader;
import com.ibm.wala.shrikeCT.ClassWriter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.shrikeCT.SourceDebugExtensionWriter;
import com.ibm.wala.shrikeCT.ClassReader.AttrIterator;

public class Main {
    public static boolean debug= false;

    /**
	 * @param args
	 */
	public static void main(String[] args) {
	    ArrayList<String> x10_srcs = new ArrayList<String>();
	    String x10dir = "";
	    String javadir = "";
	    String classdir = "";
	    boolean exhaustive  = false;
	    boolean verbose = false;
	    int idx = 0;
	    while (idx < args.length) {
	        String cur = args[idx];
	        if (cur.equals("-x10_dir")) {
	            x10dir = args[++idx];
	        } else if (cur.equals("-java_dir")) {
	            javadir = args[++idx];
	        } else if (cur.equals("-class_dir")) {
	            classdir = args[++idx];
	        } else if (cur.equals("-exhaustive")) {
	            exhaustive = true;
            } else if (cur.equals("-verbose")) {
                verbose = true;
	        } else {
	            x10_srcs.add(cur);
	        }
	        idx++;
	    }

	    if (exhaustive) {
	        File root = new File(x10dir);
	        Stack<File> dirs = new Stack<File>();
	        dirs.push(root);
	        while (!dirs.isEmpty()) {
	            File dir = dirs.pop();
	            assert dir.isDirectory();
	            for (File f : dir.listFiles()) {
	                if (f.isDirectory()) {
	                    dirs.push(f);
	                } else {
	                    if (f.getName().endsWith(".x10")) {
	                        String fname = f.getPath();
	                        x10_srcs.add(fname.substring(x10dir.length()+File.separator.length(), fname.length())); 
	                    }
	                }
	            }
	        }
	    }
	    
	    int numModified = 0;
	    for (String x10FileName : x10_srcs) { /*a/b/XYZ.x10*/
            int sepIndex = x10FileName.lastIndexOf(File.separator); /*3*/
            boolean hasPackage = sepIndex > 0;
            String relPathPrefix = sepIndex > 0 ? x10FileName.substring(0, sepIndex) : ""; /*a/b*/
            final String className = x10FileName.substring(hasPackage ? relPathPrefix.length()+1 : 0, x10FileName.length()-4/*.x10*/); /*XYZ*/
	        String javaFileName = javadir+File.separator+(x10FileName.substring(0, x10FileName.length()-4/*.x10*/))+".java"; /*src-java/gen/a/b/XYZ.java*/
	        File javaFile = new File(javaFileName);
	        if (javaFile.exists() && javaFile.isFile()) {
	            File classDir = new File(classdir+File.separator+relPathPrefix); /*classes/a/b*/
	            if (classDir.exists() && classDir.isDirectory()) {
	                File[] classFiles = classDir.listFiles(new FilenameFilter(){
	                    @Override
                        public boolean accept(File arg0, String arg1) {
                            if (!arg1.endsWith(".class")) return false;
                            if (arg1.equals(className+".class")) return true; /*XYZ.class*/
                            return arg1.startsWith(className+"$"); /*XYZ$*class*/
                        }});
	                if (classFiles != null && classFiles.length > 0) {
	                    if (verbose) {
	                        System.out.println("Smapify "+classFiles.length+" class files for " +x10FileName+" ("+javaFileName+")");
	                    }
	                    numModified += smapify(x10FileName, null, javaFileName, classFiles);
	                }
	            }
	        }
	    }
	    System.out.println("SMAPIFY: Examined "+x10_srcs.size()+" X10 files and edited "+numModified+" class files.");
	}

	/**
	 * Processes the given output file to add an appropriate SMAP attribute
	 * indicating the line mapping from the original source file to the
	 * generated .java source file.
	 * @param filename the name of the file, possibly with path information
	 * @param relPathPrefix the portion of the filename we wish to conserve
	 * @param javaFile the path to the corresponding generated java file
	 * @param classFileName path to the output file (usually a .class file)
	 * <br>E.g.:
	 * <ul>
	 * <li>filename = C:/foo/bar/bla.x
	 * <li>relPathPrefix = bar  
	 * <li>outputfile = C:/foo/bin/bar/bla.class
	 * </ul>
	 * information in SMAP becomes: bar/bla.x
	 */
	public static int smapify(final String filename, final String relPathPrefix, final String javaFile, String classFileName) {
	    String prefix = removeExt(filename);
        String inputName = (classFileName == null) ? prefix + ".class" : classFileName;
	    return smapify(filename, relPathPrefix, javaFile, new File[]{ new File(inputName) });
	}
	    
	public static int smapify(final String filename, final String relPathPrefix, final String javaFile, File[] classFiles) {
		String prefix = removeExt(filename);
		String origExten = filename.substring(filename.lastIndexOf('.')+1);
		int numModified = 0;

		if (debug) {
			System.out.println("origExten=" + origExten);
			System.out.println("smapify filename: " + filename);
			System.out.println("with pathPrefix: " + relPathPrefix);
		}
		
		LineMapBuilder lmb = new LineMapBuilder(removeExt(javaFile));
		String smap = SMAPCreator.get(prefix, relPathPrefix, lmb.get(), origExten);

		if (debug)
		    System.out.println(smap);

		FileOutputStream fw = null;
		OfflineInstrumenter oi = null;

		for (File classFile : classFiles) {
		    if (debug) {
		          System.out.println("with classfile: " + classFile.getPath());
		    }
		    try {
		        oi = new OfflineInstrumenter();
		        oi.addInputClass(classFile);
		        oi.beginTraversal();

		        ClassInstrumenter ci = oi.nextClass();
		        ClassReader cr = ci.getReader();
		        
		        // Check to see if there is already a SMAP attribute in the class file.
		        ClassReader.AttrIterator iter = new ClassReader.AttrIterator();
		        cr.initClassAttributeIterator(iter);
		        boolean alreadyHasSMAP = false;
		        for (; iter.isValid(); iter.advance()) {
		            alreadyHasSMAP |= iter.getName().equals("SourceDebugExtension");
		        }
		        if (alreadyHasSMAP) {
		            if (debug) {
		                System.out.println("\talready has SMAP attribute; skipping");
		            }
		        } else {
		            ClassWriter w = new ClassWriter();

		            copyClassProperties(cr, w);
		            copyMembersAndAttributes(cr, w);
		            addSMAPAttribute(smap, w);

		            fw = new FileOutputStream(classFile);
		            fw.write(w.makeBytes());
		            numModified++;
		        }
		    } catch (Exception e) {
		        throw new InternalCompilerError("Exception encountered while rewriting .class file '" + classFile.getPath() + "'", e);
		    } finally {
		        try {
		            if (fw != null) {
		                fw.close();
		            }
		            if (oi != null) {
		                oi.close();
		            }
		        } catch (IOException e) {
		            throw new InternalCompilerError("Exception encountered while rewriting .class file '" + classFile.getPath() + "'", e);
		        }
		    }
		}
		return numModified;
	}

    private static void copyClassProperties(ClassReader cr, ClassWriter w) throws InvalidClassFileException {
        w.setRawCP(cr.getCP(), true);
        w.setMajorVersion(cr.getMajorVersion());
        w.setMinorVersion(cr.getMinorVersion());
        w.setAccessFlags(cr.getAccessFlags());
        w.setName(cr.getName());
        w.setSuperName(cr.getSuperName());
        w.setInterfaceNames(cr.getInterfaceNames());
    }

    private static void copyMembersAndAttributes(ClassReader cr, ClassWriter w) throws InvalidClassFileException, Exception {
        ClassReader.AttrIterator iter = new ClassReader.AttrIterator();

        int fieldCount = cr.getFieldCount();	
        for (int i = 0; i < fieldCount; i++) {
        	cr.initFieldAttributeIterator(i, iter);
        	w.addField(cr.getFieldAccessFlags(i), cr.getFieldName(i), cr.getFieldType(i), collectAttributes(cr, iter)); 
        }

        int methodCount = cr.getMethodCount();
        for (int i = 0; i < methodCount; i++) {
        	cr.initMethodAttributeIterator(i, iter);
        	w.addMethod(cr.getMethodAccessFlags(i), cr.getMethodName(i), cr.getMethodType(i), collectAttributes(cr, iter));
        }

        cr.initClassAttributeIterator(iter);
        for (; iter.isValid(); iter.advance()) {
        	w.addClassAttribute(getRawAttribute(cr, iter));
        }
    }

    private static void addSMAPAttribute(String smap, ClassWriter w) {
        SourceDebugExtensionWriter sw = new SourceDebugExtensionWriter(w);
        sw.setDebugInfo(smap);
        w.addClassAttribute(sw);
    }

	private static String removeExt(String filename){
		int i = filename.lastIndexOf(".");
		return filename.substring(0,i);
	}

	private static ClassWriter.Element[] collectAttributes(ClassReader cr, AttrIterator iter) throws Exception {
		ClassWriter.Element[] elems = new ClassWriter.Element[iter.getRemainingAttributesCount()];
		for (int i = 0; i < elems.length; i++) {			
		    elems[i] = getRawAttribute(cr, iter);
			iter.advance();
		}
		return elems;
	}

	private static ClassWriter.Element getRawAttribute(ClassReader cr, AttrIterator iter) {
		int offset = iter.getRawOffset();
		int end = offset + iter.getRawSize();
		return new ClassWriter.RawElement(cr.getBytes(), offset, end - offset);
	}
}
