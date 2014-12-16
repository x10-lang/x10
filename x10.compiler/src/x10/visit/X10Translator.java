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

package x10.visit;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.JavaFileObject;

import polyglot.ast.Block;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.TopLevelDecl;
import polyglot.frontend.Compiler;
import polyglot.frontend.Job;
import polyglot.frontend.TargetFactory;
import polyglot.main.Reporter;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.QuotedStringTokenizer;
import polyglot.visit.Translator;
import x10.X10CompilerOptions;
import x10.ast.TypeDecl;
import x10.emitter.Emitter;
import x10.util.FileUtils;
import x10c.X10CCompilerOptions;

public class X10Translator extends Translator {
	
    private boolean inInnerClass;

    public X10Translator(Job job, TypeSystem ts, NodeFactory nf, TargetFactory tf) {
           super(job, ts, nf, tf);
           inInnerClass = false;
    }
    
    private static String escapePath(String path) {
        return path.replace('\\', '/');
    }

    /**
     * @param position
     * @return
     */
    private String inlineIndicator(Position position) {
        StringBuilder sb = new StringBuilder();
        if (null != position.outer()) {
            sb.append(' ');
            while (null != position.outer()) {
                sb.append('.');
                position = position.outer();
            }
        }
        return sb.toString();
    }
    
    @Override
    public void print(Node parent, Node n, CodeWriter w) {
    	assert n != null;
    	int line = n.position().line();
    	String file = n.position().file();
        if (line > 0 &&
                ((n instanceof Stmt && !(n instanceof Block)) ||
                 (n instanceof FieldDecl) ||
                 (n instanceof MethodDecl) ||
                 (n instanceof ConstructorDecl) ||
                 (n instanceof ClassDecl)))
        {
            w.newline();
//          w.writeln("//#line " + line);
//          w.writeln("//#line " + line + " \"" + escapePath(file) + "\"");
            w.writeln("//#line " + line + inlineIndicator(n.position()) + " \"" + escapePath(file) + "\"");
        }

        super.print(parent, n, w);
    }

    public boolean inInnerClass() {
        return inInnerClass;
    }

    public X10Translator inInnerClass(boolean inInnerClass) {
        if (inInnerClass == this.inInnerClass) return this;
        X10Translator tr = (X10Translator) shallowCopy();
        tr.inInnerClass = inInnerClass;
        return tr;
    }

    // XTENLANG-3170: X10DT requires Java file to make sure there is no compiler bug.
    private static final boolean alwaysGenerateJavaFile = true;
    private static boolean generateJavaFile(TopLevelDecl decl) {
    	if (alwaysGenerateJavaFile) return true;
        if (decl instanceof TypeDecl) return false;  // public type Int(b:Int) = Int{self==b};
//        assert decl instanceof ClassDecl;
        if (!(decl instanceof ClassDecl)) return true; // for safety
        if (Emitter.getJavaRep(((ClassDecl) decl).classDef()) == null) return true; // not @NativeRep'ed
        return false;
    }
    private static boolean generateJavaFile(SourceFile sfn) {
    	if (alwaysGenerateJavaFile) return true;
        for (TopLevelDecl decl : sfn.decls()) {
            if (generateJavaFile(decl)) return true;
        }
        return false;
    }
    /** Override to not open a new file for each declaration. */
    @Override
    protected boolean translateSource(SourceFile sfn) {
        TypeSystem ts = typeSystem();
        NodeFactory nf = nodeFactory();
        TargetFactory tf = this.tf;
        int outputWidth = job.compiler().outputWidth();
        CodeWriter w= null;

        try {
            QName pkg = null;

            if (sfn.package_() != null) {
                Package p = sfn.package_().package_().get();
                pkg = p.fullName();
            }

            // if all toplevel decls are @NativeRep'ed, stop generating Java file
            if (generateJavaFile(sfn)) {

            // Use the source name to derive a default output file name.
            File of = tf.outputFile(pkg, sfn.source());

            String opfPath = of.getPath();
            if (!opfPath.endsWith("$")) job.compiler().addOutputFile(sfn, of.getPath());
            w = tf.outputCodeWriter(of, outputWidth);

            writeHeader(sfn, w);

            for (TopLevelDecl decl : sfn.decls()) {

                if (!generateJavaFile(decl)) continue;

                translateTopLevelDecl(w, sfn, decl);

                w.newline();
            }

            w.flush();

            }

            X10CompilerOptions options = (X10CompilerOptions) ts.extensionInfo().getOptions();
            if (options.post_compiler != null && !options.output_stdout && options.executable_path != null) {
                // copy *.x10 to output_directory in order to add them in a jar file
                File sourceFile = null; 
                File targetFile = null;
                try {
                    String sourceFilepath = sfn.source().toString();
                    sourceFile = new File(sourceFilepath);
                    if (sourceFile.isFile()) {
                        String targetDirpath = options.output_directory.getAbsolutePath();
                        if (pkg != null) {
                            targetDirpath += File.separator + pkg.toString().replace('.', File.separatorChar);
                        }
                        File targetDir = new File(targetDirpath);
//                        targetDir.mkdirs();
                        targetFile = new File(targetDir, sfn.source().name());
                        FileUtils.copyFile(sourceFile, targetFile);
                    }
                } finally {
                    if (sourceFile != null && targetFile != null) { 
                        targetFile.setLastModified(sourceFile.lastModified());
                    }
                }
            }

            return true;

        } catch (IOException e) {
            job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR, "I/O error while translating: " + e.getMessage());
            return false;
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR, "I/O error while closing output file: " + e.getMessage());
                }
            }
        }
    }
    
    /*
     * Delete file or directory. In case of directory, delete it recursively.
     */
    private static void deleteFile(File file) {
    	if (!file.exists()) return;
    	if (file.isDirectory()) {
    		for (File childFile : file.listFiles()) deleteFile(childFile);
    	}
		file.delete();
    }
    
    
    private static String toJarCompatiblePath(File file) throws IOException {
    	String path = file.getCanonicalPath().replace('\\', '/');
        if (file.isDirectory() && !path.endsWith("/"))
        	path += "/";
        return path;
    }
  
    private static void addFileToJar(File file, String basePath, JarOutputStream jarOutputStream, boolean symbols) throws IOException {
        BufferedInputStream is = null;
        try {
            String path = toJarCompatiblePath(file);
            
            // change path relative to basePath
            if (basePath != null) {
            	assert path.startsWith(basePath);
            	path = path.substring(basePath.length());
            }
            
            if (file.isDirectory()) {
                if (!path.isEmpty()) {
                    JarEntry jarEntry = new JarEntry(path);
                    jarEntry.setTime(file.lastModified());
                    jarOutputStream.putNextEntry(jarEntry);
                    jarOutputStream.closeEntry();
                }
                for (File childFile: file.listFiles())
                    addFileToJar(childFile, basePath, jarOutputStream, symbols);
                return;
            }

            if (!symbols) {
                // if no symbols are needed, no source files are needed
                if (path.endsWith(".x10") || path.endsWith(".java")) return;
            }

            JarEntry jarEntry = new JarEntry(path);
            jarEntry.setTime(file.lastModified());
            jarOutputStream.putNextEntry(jarEntry);
            
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            while (true) {
                int count = is.read(buffer);
                if (count == -1)
                    break;
                jarOutputStream.write(buffer, 0, count);
            }
            
            jarOutputStream.closeEntry();
        }
        finally {
            if (is != null)
                is.close();
        }
    }
    
    /*
     * equivalent to "jar cmf ${manifest_file} ${jar_file} -C ${base_dir} ."
     */
    private static void createJarFile(File jarFile, Manifest manifest, File baseDir, boolean symbols) throws IOException {
    	JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile), manifest);
        String basePath = toJarCompatiblePath(baseDir);
        addFileToJar(baseDir, basePath, jarOutputStream, symbols);
    	jarOutputStream.close();
    }


    public static final String postcompile = "postcompile";

    public static boolean postCompile(X10CompilerOptions options, Compiler compiler, ErrorQueue eq) {
        if (eq.hasErrors())
            return false;

        if (options.post_compiler != null && !options.output_stdout) {
            Runtime runtime = Runtime.getRuntime();
            java.util.ArrayList<String> javacCmd = new java.util.ArrayList<String>();
            String[] strarray = new String[0];
            QuotedStringTokenizer st = new QuotedStringTokenizer(options.post_compiler, '?');
            while (st.hasMoreTokens()) {
                javacCmd.add(st.nextToken());
            }

            int javacOptionsStart = javacCmd.size();
            javacCmd.add("-source");
            javacCmd.add("1.6");

            javacCmd.add("-target");
            javacCmd.add("1.6");

            javacCmd.add("-nowarn");

            javacCmd.add("-classpath");
            javacCmd.add(options.constructPostCompilerClasspath());

            javacCmd.add("-d");
            javacCmd.add(options.output_directory.toString());

            javacCmd.add("-encoding");
            javacCmd.add("utf-8");

            //            javacCmd.add("-warn:+boxing");	// only for ecj

            if (options.x10_config.DEBUG) {
                javacCmd.add("-g");                
            }

            int javacSourcesStart = javacCmd.size();
            for (Collection<String> files : compiler.outputFiles().values()) {
                javacCmd.addAll(files);
            }

            Reporter reporter = options.reporter;
            if (reporter.should_report(postcompile, 1)) {
                StringBuilder cmdStr = new StringBuilder();                
                for (int i = 0; i < javacCmd.size(); i++)
                    cmdStr.append(javacCmd.get(i)+" ");
                reporter.report(1, "Executing post-compiler " + cmdStr);
            }

            try {
                /*
            	// invoke ecj as an external process
                Process proc = runtime.exec(javacCmd.toArray(strarray));
                InputStreamReader err = new InputStreamReader(proc.getErrorStream());
                try {
                    char[] c = new char[72];
                    int len;
                    StringBuilder sb = new StringBuilder();
                    while((len = err.read(c)) > 0) {
                        sb.append(String.valueOf(c, 0, len));
                    }
                    if (sb.length() != 0) {
                        eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, sb.toString());
                    }
                }
                finally {
                    err.close();
                }
                int procExitValue = proc.waitFor();
                 */


                // invoke ecj with Java Compiler API (JSR 199)
                javax.tools.JavaCompiler javac = null;
                if (((X10CCompilerOptions) options).preferSystemJavaCompiler) {
                    // look up system java compiler (javac)
                    javac = javax.tools.ToolProvider.getSystemJavaCompiler();
                    System.out.println("x10c: Use system Java compiler for post compilation.");
                }
                if (javac == null) {
                    // look up user-specified java compiler from classpath and ${x10.dist}/lib/ecj.jar
                    String ecj_path = ((X10CCompilerOptions) options).x10_dist + File.separator + "lib" + File.separator + ((X10CCompilerOptions) options).ecj_jar;
                    java.net.URL ecj_url = new java.io.File(ecj_path).toURI().toURL();
                    ClassLoader cl = new java.net.URLClassLoader(new java.net.URL[] { ecj_url });
                    java.util.Iterator<javax.tools.JavaCompiler> iter = java.util.ServiceLoader.load(javax.tools.JavaCompiler.class, cl).iterator();
                    while (iter.hasNext()) {
                        try {
                            javac = iter.next();
                            assert javac != null;
                            break;
                        } catch (Throwable e) { }
                    }
                    //                if (javac == null) {
                    //                    // look up system java compiler (javac)
                    //                    javac = javax.tools.ToolProvider.getSystemJavaCompiler();
                    //                }
                }
                if (javac == null) {
                    eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, "Cannot find post java compiler.");
                    return false;
                }
                javax.tools.DiagnosticCollector<javax.tools.JavaFileObject> diagCollector = new javax.tools.DiagnosticCollector<javax.tools.JavaFileObject>();
                javax.tools.StandardJavaFileManager fileManager = javac.getStandardFileManager(null, null, null);
                javax.tools.JavaCompiler.CompilationTask task = javac.getTask(null, null,
                                                                              diagCollector,
                                                                              javacCmd.subList(javacOptionsStart, javacSourcesStart),
                                                                              null,
                                                                              fileManager.getJavaFileObjectsFromStrings(javacCmd.subList(javacSourcesStart, javacCmd.size()))
                        );
                int procExitValue = task.call() ? 0 : 1;
                /*
                for (javax.tools.Diagnostic<? extends javax.tools.JavaFileObject> diag : diagCollector.getDiagnostics()) {
                    String message = diag.getMessage(null);
                    int type = diag.getKind() == javax.tools.Diagnostic.Kind.ERROR ? ErrorInfo.POST_COMPILER_ERROR : ErrorInfo.WARNING;
                    eq.enqueue(type, message);
                }
                 */
                               
                fileManager.close();
                               
                if (options.x10_config.DEBUG) {
                    try {
                        for (Entry<String, Collection<String>> es : compiler.outputFiles().entrySet()) {
                            String x10_src = es.getKey();
                            int endPack = x10_src.lastIndexOf(File.separator);
                            boolean hasPackage = endPack > 0;
                            String pack = hasPackage ? x10_src.substring(0, endPack) : "";
                            for (String java_src : es.getValue()) {
                                // FIXME: need to look in file system for all derived class files, not just the primary one
                                String class_file = java_src.substring(0, java_src.length()-5) + ".class";
                                File cf = new File(class_file);
                                if (cf.exists()) {
                                    File dir = cf.getParentFile();
                                    if (dir.exists() && dir.isDirectory()) {
                                        final String baseName = x10_src.substring(hasPackage ? endPack+1 : 0, x10_src.length());
                                        File[] classFiles = dir.listFiles(new FilenameFilter(){
                                            public boolean accept(File arg0, String arg1) {
                                                if (!arg1.endsWith(".class")) return false;
                                                if (arg1.equals(baseName+".class")) return true;
                                                return arg1.startsWith(baseName+"$");
                                            }});
                                        if (classFiles != null && classFiles.length > 0) {
                                            if (reporter.should_report(postcompile, 1)) {
                                                reporter.report(1, "\tSmapify "+classFiles.length+" class files for " +x10_src+" ("+java_src+")");
                                            }
                                            x10c.smap.Main.smapify(x10_src+".x10", pack, java_src, classFiles);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (InternalCompilerError e) {
                        eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, e.getMessage());
                    }
                }

                if (!options.keep_output_files) {
                    for (Collection<String> files : compiler.outputFiles().values()) {
                        for (String file : files) {
                            deleteFile(new File(file));
                        }
                    }
                }

                if (procExitValue > 0) {
                    eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, "Non-zero return code: " + procExitValue);
                    return false;
                }

                if (options.executable_path != null) {  // -o executable_path
                    // create jar file

                    // create Main-Class attribute from main (= first) source name if MAIN_CLASS is not specified
                    String main_class = options.x10_config.MAIN_CLASS;
                    // Fix for XTENLANG-2410
                    // Guessing main_class from the first .x10 file is incorrect, because
                    // 1) main_source may not be relative path from currect directory
                    // 2) the first .x10 file may not have $Main class.
                    /*
                    if (main_class == null) {
                        String main_source = ((X10CCompilerOptions) options).main_source;
                        if (main_source != null) {
                            main_class = main_source.substring(0, main_source.length() - ".x10".length());
                        }
                    }
                     */

                    // create manifest file
                    Manifest mf = new Manifest();
                    Attributes attributes = mf.getMainAttributes();
                    attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
                    if (main_class != null) {
                        // add Main-Class attribute for executable jar
                        attributes.putValue("Main-Class", main_class + "$" + X10PrettyPrinterVisitor.MAIN_CLASS);
                        String x10_jar = ((X10CCompilerOptions) options).x10_jar;
                        String math_jar = ((X10CCompilerOptions) options).math_jar;
                        String log_jar = ((X10CCompilerOptions) options).log_jar;
                        // XTENLANG-2722
                        // need a new preloading mechanism which does not use classloader to determine system classes
                        attributes.putValue("Class-Path", x10_jar + " " + math_jar + " " + log_jar);
                    }
                    attributes.putValue("Created-By", compiler.sourceExtension().compilerName() + " version " + compiler.sourceExtension().version());

                    // create directory for jar file
                    File jarFile = new File(options.executable_path);
                    File directoryHoldingJarFile = jarFile.getParentFile();
                    if (directoryHoldingJarFile != null) {
                        directoryHoldingJarFile.mkdirs();
                    } else {
                        directoryHoldingJarFile = new File(".");
                    }

                    // execute "jar cmf ${manifest_file} ${executable_path} -C ${output_directory} ."
                    createJarFile(jarFile, mf, options.output_directory, options.symbols); // -d output_directory

                    // pre XTENLANG-3199
                    //                    if (options.buildX10Lib != null) {  // ignore lib from -buildx10lib <lib>
                    //                    	// generate property file for use as "x10c -x10lib foo.properties ..."
                    //                    	String jarFileName = jarFile.getName(); // foo.jar
                    //                    	String propFileName = jarFileName.substring(0, jarFileName.length() - ".jar".length()) + ".properties"; // foo.properties
                    //                    	File propFile = new File(directoryHoldingJarFile, propFileName);
                    //                    	PrintWriter propFileWriter = new PrintWriter(new FileWriter(propFile));
                    //                    	propFileWriter.println("X10LIB_TIMESTAMP=" + String.format("%tc", Calendar.getInstance()));
                    //                    	propFileWriter.println("X10LIB_SRC_JAR=" + jarFileName);
                    //                    	propFileWriter.close();
                    //                    }
                    // post XTENLANG-3199
                    if (options.buildX10Lib != null) {	// "-buildx10lib <dir> -o foo.jar" generates <dir>/foo.properties
                        File propDir = new File(options.buildX10Lib);
                        //                    	System.out.println("buildx10lib = " + options.buildX10Lib);

                        // ensure propDir exists and is a directory
                        if (propDir.exists()) {
                            if (!propDir.isDirectory()) {
                                eq.enqueue(ErrorInfo.SEMANTIC_ERROR, "-buildx10lib <dir> only accepts directory name. property file was not generated.");
                                return false;
                            }
                        } else {
                            propDir.mkdirs();
                        }

                        String jarDirPath; // either absolute or relative
                        if (directoryHoldingJarFile.isAbsolute()) {
                            // When jar file is specified with absolute path, refer it with absolute path.
                            jarDirPath = directoryHoldingJarFile.getCanonicalPath();
                            if (!jarDirPath.endsWith("/")) jarDirPath += "/";
                        } else {
                            // Otherwise, refer the jar file with relative path from prop file.
                            File f;

                            List<File> listPropDir = new ArrayList<File>();
                            f = propDir.getCanonicalFile(); // "/usr/local/bin"
                            do {
                                listPropDir.add(f);
                                f = f.getParentFile();
                            } while (f != null);
                            Collections.reverse(listPropDir); // [ "/", "/usr", "/usr/local", "/usr/local/bin" ]
                            //                    		System.out.println("listPropDir = " + listPropDir);

                            List<File> listJarDir = new ArrayList<File>();
                            File jarDir = directoryHoldingJarFile;
                            f = jarDir.getCanonicalFile(); // "/usr/bin"
                            do {
                                listJarDir.add(f);
                                f = f.getParentFile();                        	
                            } while (f != null);
                            Collections.reverse(listJarDir); // [ "/", "/usr", "/usr/bin" ]
                            // System.out.println("listJarDir = " + listJarDir);

                            // compute relative path from propDir /usr/local/bin to jarDir /usr/bin

                            // first compute the same header length
                            int i = 0;
                            while (i < listPropDir.size() && i < listJarDir.size() && listPropDir.get(i).equals(listJarDir.get(i))) {
                                ++i;
                            }
                            // i == 2

                            // compute relative path from propDir to list{Prop,Jar}Dir(i)
                            StringBuilder sb = new StringBuilder();
                            for (int j = i; j < listPropDir.size(); ++j) {
                                sb.append(".." + "/");
                            }

                            // compute relative path from list{Prop,Jar}Dir(i) to jarDir
                            for (int j = i; j < listJarDir.size(); ++j) {
                                sb.append(listJarDir.get(j).getName() + "/");
                            }

                            jarDirPath = sb.toString();
                        }
                        //                    	System.out.println("jarDirPath = " + jarDirPath);

                        // generate property file for use as "x10c -x10lib foo.properties ..."
                        String jarFileName = jarFile.getName(); // foo.jar
                        Properties props = new Properties();
                        props.setProperty("X10LIB_TIMESTAMP", String.format("%tc", Calendar.getInstance()));
                        props.setProperty("X10LIB_SRC_JAR", jarDirPath + jarFileName);
                        String propFileName = jarFileName.substring(0, jarFileName.length() - ".jar".length()) + ".properties"; // foo.properties
                        File propFile = new File(propDir, propFileName);
                        FileWriter propFileWriter = new FileWriter(propFile);
                        props.store(propFileWriter, "Created by " + compiler.sourceExtension().compilerName() + " version " + compiler.sourceExtension().version());
                        propFileWriter.close();
                    }
                }
                // XTENLANG-2126
                if (!options.keep_output_files) {
                    deleteFile(options.output_directory); // N.B. output_directory is a temporary directory
                }
            }
            catch(Exception e) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                e.printStackTrace(new PrintWriter(baos, true));
                eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, baos.toString());
                return false;
            }
        }
        return true;
    }
}
