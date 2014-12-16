/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10c.visit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.main.Options;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.visit.NodeVisitor;
import x10.ast.X10ClassDecl;
import x10.emitter.Emitter;
import x10.types.X10ClassDef;
import x10.util.FileUtils;


public class NativeClassVisitor extends x10.visit.NativeClassVisitor {

    public NativeClassVisitor(Job job, TypeSystem ts, NodeFactory nf, String theLanguage) {
        super(job, ts, nf, theLanguage);
    }

    private static boolean findAndCopySourceFile(Options options, String cpackage, String cname, File sourceDirOrJarFile) throws IOException {
        String sourceDirOrJarFilePath = sourceDirOrJarFile.getAbsolutePath();
        if (sourceDirOrJarFile.isDirectory()) {
            if (cpackage != null) {
                sourceDirOrJarFilePath += File.separator + cpackage.replace('.', File.separatorChar);
            }
            File sourceDir = new File(sourceDirOrJarFilePath);
            File sourceFile = new File(sourceDir, cname + ".java");
            if (sourceFile.isFile()) { // found java source
                // copy
                String targetDirpath = options.output_directory.getAbsolutePath();
                if (cpackage != null) {
                    targetDirpath += File.separator + cpackage.replace('.', File.separatorChar);
                }
                File targetDir = new File(targetDirpath);
                targetDir.mkdirs();
                File targetFile = new File(targetDir, cname + ".java");
                FileUtils.copyFile(sourceFile, targetFile);
                return true;
            }
        } else if (sourceDirOrJarFile.isFile() && (sourceDirOrJarFilePath.endsWith(".jar") || sourceDirOrJarFilePath.endsWith(".zip"))) {
            String sourceFilePathInJarFile = cpackage != null ? (cpackage.replace('.', '/') + '/') : "";
            sourceFilePathInJarFile += cname + ".java";
            JarFile jarFile = new JarFile(sourceDirOrJarFile);
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.equals(sourceFilePathInJarFile)) { // found java source
                    // copy
                    String targetDirpath = options.output_directory.getAbsolutePath();
                    if (cpackage != null) {
                        targetDirpath += File.separator + cpackage.replace('.', File.separatorChar);
                    }
                    File targetDir = new File(targetDirpath);
                    targetDir.mkdirs();
                    File targetFile = new File(targetDir, cname + ".java");
                    InputStream sourceInputStream = jarFile.getInputStream(jarEntry);
                    long sourceSize = jarEntry.getSize();
                    FileUtils.copyFile(sourceInputStream, sourceSize, targetFile);
                    return true;
                }
            }

        }
        return false;
    }

    /*
     * copy source files of Java classes that are referenced by @NativeClass or @NativeRep annotation from sourcepath (or classpath) to output_directory
     */
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {

        if (n instanceof X10ClassDecl) {

            X10ClassDecl cdecl = (X10ClassDecl) n;
            X10ClassDef cdef = (X10ClassDef) cdecl.classDef();

            String cname = null;
            String cpackage = null;

            if (getNativeClassName(cdef) != null) {
                cname = getNativeClassName(cdef);
                cpackage = getNativeClassPackage(cdef);
            } else if (Emitter.getJavaRep(cdef) != null) {
                cname = Emitter.getJavaRep(cdef);
                int index = cname.lastIndexOf('.');
                if (index >= 0) {
                    cpackage = cname.substring(0, index);
                    cname = cname.substring(index + 1);
                }
            }

            if (cname != null) {
                Options options = ts.extensionInfo().getOptions();
                try {
                    if (options.source_path != null && !options.source_path.isEmpty()) {
                        for (File sourceDirOrJarFile : options.source_path) {
                            boolean copied = findAndCopySourceFile(options, cpackage, cname, sourceDirOrJarFile);
                            if (copied) break;
                        }
                    } else {
                        for (String sourceDirOrJarFilePath : options.constructPostCompilerClasspath().split(File.pathSeparator)) {
                            File sourceDirOrJarFile = new File(sourceDirOrJarFilePath);
                            boolean copied = findAndCopySourceFile(options, cpackage, cname, sourceDirOrJarFile);
                            if (copied) break;
                        }
                    }
                } catch (IOException e) {
                    job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR, "I/O error copying Java source file: " + e.getMessage());
                }
            }

        }

        return super.leaveCall(parent, old, n, v);
    }

}
