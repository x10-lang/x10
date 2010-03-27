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

package x10cpp.visit;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import polyglot.ast.Block;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.For;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceCollection;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.TopLevelDecl;


import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.frontend.TargetFactory;

import polyglot.main.Options;
import polyglot.main.Report;

import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Name;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.StdErrorQueue;
import polyglot.util.StringUtil;
import polyglot.visit.Translator;
import x10.ast.ForLoop;
import x10.ast.X10ClassDecl;
import x10.types.X10ClassDef;
import x10.util.ClassifiedStream;
import x10.util.StreamWrapper;
import x10.util.WriterStreams;
import x10cpp.X10CPPCompilerOptions;
import x10cpp.debug.LineNumberMap;
import x10cpp.postcompiler.AIX_CXXCommandBuilder;
import x10cpp.postcompiler.CXXCommandBuilder;
import x10cpp.postcompiler.Cygwin_CXXCommandBuilder;
import x10cpp.postcompiler.Linux_CXXCommandBuilder;
import x10cpp.postcompiler.SunOS_CXXCommandBuilder;
import x10cpp.types.X10CPPContext_c;
import static x10cpp.visit.ASTQuery.getCppRep;
import static x10cpp.visit.SharedVarsMethods.*;

public class X10CPPTranslator extends Translator {
    public static final class DelegateTargetFactory extends TargetFactory {
		protected String outputHeaderExtension;

		public DelegateTargetFactory(File dir, String ext, String hExt, boolean so) {
			super(dir, ext, so);
			if (outputDirectory == null)
				throw new InternalCompilerError("Output directory not set.");
			this.outputHeaderExtension = hExt;
		}

		public static String extractName(Source source) {
			String name = new File(source.name()).getName();
			return name.substring(0, name.lastIndexOf('.'));
		}

		/* (non-Javadoc)
		 * @see polyglot.frontend.TargetFactory#outputFile(java.lang.String, polyglot.frontend.Source)
		 */
		public File outputFile(String packageName, Source source) {
			return outputFile(packageName, extractName(source), source);
		}

		public File outputAsyncFile(String packageName, Source source) {
			return outputAsyncFile(packageName, extractName(source), source);
		}
		public File outputHeaderFile(String packageName, Source source) {
			return outputHeaderFile(packageName, extractName(source), source);
		}

		public String outputName(String packageName, Source source) {
			return outputName(packageName, extractName(source));
		}
		public String outputAsyncName(String packageName, Source source) {
			return outputAsyncName(packageName, extractName(source));
		}
		public String outputHeaderName(String packageName, Source source) {
			return outputHeaderName(packageName, extractName(source));
		}

		/* (non-Javadoc)
		 * @see polyglot.frontend.TargetFactory#outputWriter(java.lang.String, java.lang.String, polyglot.frontend.Source)
		 */
		public Writer outputWriter(String packageName, String className, Source source) throws IOException {
			// TODO Auto-generated method stub
			assert (false);
			return super.outputWriter(QName.make(packageName), Name.make(className), source);
		}

		/* (non-Javadoc)
		 * @see polyglot.frontend.TargetFactory#outputCodeWriter(java.io.File, int)
		 */
		public CodeWriter outputCodeWriter(File f, int width) throws IOException {
			// TODO Auto-generated method stub
			assert (false);
			return super.outputCodeWriter(f, width);
		}

		private String packagePath(String packageName) {
			if (packageName == null || packageName.equals(""))
				return "";
			return packageName.replace('.', '/') + '/';
		}

		private String mangleClassName(String className) {
			return Emitter.mangled_non_method_name(className);
		}

		public String outputName(String packageName, String className) {
			return packagePath(packageName) + mangleClassName(className) + "." + outputExtension;
		}

		/* (non-Javadoc)
		 * @see polyglot.frontend.TargetFactory#outputFile(java.lang.String, java.lang.String, polyglot.frontend.Source)
		 */
		public File outputFile(String packageName, String className, Source source) {
			File outputFile = new File(outputDirectory, outputName(packageName, className));

			if (source != null && outputFile.getPath().equals(source.path()))
				throw new InternalCompilerError("The output file is the same as the source file");

			return outputFile;
		}

		public String outputAsyncName(String packageName, String className) {
			return packagePath(packageName) + className + "." + outputExtension + "_int_";
//			return packagePath(packageName) + className + asyncExtension + "." + "inc";
		}

		public File outputAsyncFile(String packageName, String className, Source source) {
			File outputFile = new File(outputDirectory, outputAsyncName(packageName, className));

			if (source != null && outputFile.getPath().equals(source.path()))
				throw new InternalCompilerError("The Async file is the same as the source file");

			return outputFile;
		}

		public String outputHeaderName(String packageName, String className) {
			return packagePath(packageName) + mangleClassName(className) + "." + outputHeaderExtension;
		}

		public File outputHeaderFile(String packageName, String className, Source source) {
			File outputFile = new File(outputDirectory, outputHeaderName(packageName, className));

			if (source != null && outputFile.getPath().equals(source.path()))
				throw new InternalCompilerError("The header file is the same as the source file");

			return outputFile;
		}

		public String integratedOutputName(String packageName, String className, String ext) {
			return packagePath(packageName) + mangleClassName(className) + "." + ext;
		}

		public File integratedOutputFile(String packageName, String className, Source source, String ext) {
			File outputFile = new File(outputDirectory,
			                           integratedOutputName(packageName, className, ext));

			if (source != null && outputFile.getPath().equals(source.path()))
				throw new InternalCompilerError("The header file is the same as the source file");

			return outputFile;
		}

		/* (non-Javadoc)
		 * @see polyglot.frontend.TargetFactory#outputWriter(java.io.File)
		 */
		public Writer outputWriter(File outputFile) throws IOException {
			// TODO Auto-generated method stub
			if (Report.should_report(Report.frontend, 2))
				Report.report(2, "Opening " + outputFile + " for output.");

			if (outputStdout)
				return new PrintWriter(System.out);

			if (!outputFile.getParentFile().exists()) {
				File parent = outputFile.getParentFile();
				parent.mkdirs();
			}

			return new FileWriter(outputFile);
		}
	}

	// FIXME: [IP] HACK - override creation of target factory in ExtensionInfo instead
	public X10CPPTranslator(Job job, TypeSystem ts, NodeFactory nf, TargetFactory tf) {
		super(job, ts, nf, createTargetFactory(job));
	}

	private static TargetFactory createTargetFactory(Job job) {
		Options options = job.extensionInfo().getOptions();
		return new DelegateTargetFactory(options.output_directory,
		                                 "cc", "h", options.output_stdout);
	}

	public DelegateTargetFactory targetFactory() {
	    return (DelegateTargetFactory) tf;
	}

	private static int adjustLNForNode(int outputLine, Node n) {
	    // FIXME: Debugger HACK: adjust for loops
	    if (n instanceof For || n instanceof ForLoop)
	        return outputLine + 1;
	    return outputLine;
	}

	private static final String FILE_TO_LINE_NUMBER_MAP = "FileToLineNumberMap";

	public void print(Node parent, Node n, CodeWriter w_) {
		if (w_ == null)
			return; // FIXME HACK
		StreamWrapper w = (StreamWrapper) w_;
		if (n != null && n.position().line() > 0 &&
				((n instanceof Stmt && !(n instanceof Block)) ||
				 (n instanceof FieldDecl) ||
				 (n instanceof MethodDecl) ||
				 (n instanceof ConstructorDecl) ||
				 (n instanceof ClassDecl)))
		{
			w.forceNewline(0);
			final int line = n.position().line();
			final String file = n.position().file();
			w.write("//#line " + line + " \"" + file + "\": "+n.getClass().getName());
			w.newline();
			if (x10.Configuration.DEBUG) {
				X10CPPContext_c c = (X10CPPContext_c)context;
				HashMap<String, LineNumberMap> fileToLineNumberMap =
					(HashMap<String, LineNumberMap>) c.findData(FILE_TO_LINE_NUMBER_MAP);
                if (fileToLineNumberMap!=null) {
                    String key = w.getStreamName(StreamWrapper.CC);
                    final LineNumberMap lineNumberMap = fileToLineNumberMap.get(key);
    				// [DC] avoid NPE when writing to .cu files
    				if (lineNumberMap!=null) {
    				    final String cppFile = w.getStreamName(w.currentStream().ext);
        				if (n instanceof MethodDecl) {
        					lineNumberMap.addMethodMapping(((MethodDecl) n).methodDef());
        				}
        				if (n instanceof ConstructorDecl) {
        					lineNumberMap.addMethodMapping(((ConstructorDecl) n).constructorDef());
        				}
        				final int outputLine = adjustLNForNode(w.currentStream().getStreamLineNumber(), n);
        				w.currentStream().registerCommitListener(new ClassifiedStream.CommitListener() {
        				    public void run(ClassifiedStream s) {
        				        int cppLine = s.getStartLineOffset()+outputLine;
//        				        System.out.println("Adding line number entry: "+lineNumberMap.file()+":"+cppLine+"->"+file+":"+line);
        				        lineNumberMap.put(cppFile, cppLine, file, line);
        				    }
        				});
    				}
                }
			}
		}

		// FIXME: [IP] Some nodes have no del() -- warn in that case
		super.print(parent, n, w_);

		if (n != null && n.position().endLine() > 0 && (n instanceof Block))
		{
			if (x10.Configuration.DEBUG) {
				X10CPPContext_c c = (X10CPPContext_c)context;
				HashMap<String, LineNumberMap> fileToLineNumberMap =
					(HashMap<String, LineNumberMap>) c.findData(FILE_TO_LINE_NUMBER_MAP);
                if (fileToLineNumberMap!=null) {
                    final String key = w.getStreamName(StreamWrapper.CC);
                    final LineNumberMap lineNumberMap = fileToLineNumberMap.get(key);
                    // [DC] avoid NPE when writing to .cu files
                    if (lineNumberMap!=null) {
                        final String cppFile = w.getStreamName(w.currentStream().ext);
        				final int line = n.position().endLine();
        				final String file = n.position().file();
        				final int outputLine = w.currentStream().getStreamLineNumber();
        				w.currentStream().registerCommitListener(new ClassifiedStream.CommitListener() {
        				    public void run(ClassifiedStream s) {
        				        int cppLine = s.getStartLineOffset()+outputLine;
//        				        System.out.println("Adding block line number entry: "+lineNumberMap.file()+":"+cppLine+"->"+file+":"+line);
        				        lineNumberMap.put(cppFile, cppLine, file, line);
        				    }
        				});
                    }
                }
			}
		}
	}

	/**
	 * Only for the backend -- use with care!
	 * FIXME: HACK!!! HACK!!! HACK!!!
	 */
	public void setContext(Context c) {
		context = c;
	}

	/* (non-Javadoc)
	 * @see polyglot.visit.Translator#translateSource(polyglot.ast.SourceFile)
	 */
	protected boolean translateSource(SourceFile sfn) {
		DelegateTargetFactory tf = (DelegateTargetFactory) this.tf;

		int outputWidth = job.compiler().outputWidth();
		Collection<String> outputFiles = job.compiler().outputFiles();

		try {
			String opfPath;

			String pkg = "";
			if (sfn.package_() != null) {
				Package p = sfn.package_().package_().get();
				pkg = p.fullName().toString();
			}

			X10CPPContext_c c = (X10CPPContext_c) context;
			if (x10.Configuration.DEBUG)
				c.addData(FILE_TO_LINE_NUMBER_MAP, new HashMap<String, LineNumberMap>());
			WriterStreams wstreams = null;
			StreamWrapper sw = null;
			// Use the class name to derive a default output file name.
			for (Iterator i = sfn.decls().iterator(); i.hasNext(); ) {
				TopLevelDecl decl = (TopLevelDecl) i.next();
				if (!(decl instanceof X10ClassDecl))
					continue;
				X10ClassDecl cd = (X10ClassDecl) decl;
				// Skip output of all files for a native rep class.
				if (getCppRep((X10ClassDef)cd.classDef()) != null) {
					continue;
				}
				String className = cd.classDef().name().toString();
				wstreams = new WriterStreams(className, pkg, tf, job);
				sw = new StreamWrapper(wstreams, outputWidth);
				// [DC] TODO: This hack is to ensure the .inc is always generated.
				sw.getNewStream(StreamWrapper.Closures, true);
				// [IP] FIXME: This hack is to ensure the .cc is always generated.
				sw.getNewStream(StreamWrapper.CC, true);
                // [DC] TODO: This hack is to ensure the .h is always generated.
                sw.getNewStream(StreamWrapper.Header, true);
				opfPath = tf.outputName(pkg, decl.name().toString());
				assert (!opfPath.endsWith("$"));
				X10CPPCompilerOptions opts = (X10CPPCompilerOptions) job.extensionInfo().getOptions();
				if (!opfPath.endsWith("$")) outputFiles.add(opts.output_directory + File.separator + opfPath);
				if (x10.Configuration.DEBUG) {
					HashMap<String, LineNumberMap> fileToLineNumberMap = (HashMap<String, LineNumberMap>)c.getData(FILE_TO_LINE_NUMBER_MAP);
					String closures = wstreams.getStreamName(StreamWrapper.Closures);
					fileToLineNumberMap.put(closures, new LineNumberMap());
					String cc = wstreams.getStreamName(StreamWrapper.CC);
					fileToLineNumberMap.put(cc, new LineNumberMap());
					String header = wstreams.getStreamName(StreamWrapper.Header);
					fileToLineNumberMap.put(header, new LineNumberMap());
				}
				translateTopLevelDecl(sw, sfn, decl);
				if (x10.Configuration.DEBUG) {
					HashMap<String, LineNumberMap> fileToLineNumberMap = (HashMap<String, LineNumberMap>)c.getData(FILE_TO_LINE_NUMBER_MAP);
//					sw.pushCurrentStream(sw.getNewStream(StreamWrapper.Closures, false));
//					printLineNumberMap(sw, pkg, className, StreamWrapper.Closures, fileToLineNumberMap);
//					sw.popCurrentStream();
//					sw.pushCurrentStream(sw.getNewStream(StreamWrapper.CC, false));
//					printLineNumberMap(sw, pkg, className, StreamWrapper.CC, fileToLineNumberMap);
//					sw.popCurrentStream();
//					sw.pushCurrentStream(sw.getNewStream(StreamWrapper.CC, false));
//					printLineNumberMap(sw, pkg, className, StreamWrapper.Header, fileToLineNumberMap);
//					sw.popCurrentStream();
					sw.pushCurrentStream(sw.getNewStream(StreamWrapper.CC, false));
					printLineNumberMapForCPPDebugger(sw, fileToLineNumberMap);
					sw.popCurrentStream();
				}
				if (i.hasNext()) {
					wstreams.commitStreams();
					wstreams = null;
				}
			}

			Iterator t = job().extensionInfo().scheduler().commandLineJobs().iterator();
			// FIXME: [IP] The following does the same as the prior code below.  Why the change?
			//	while (t.hasNext() && !t.next().equals(job()))
			//		;
			//	if (!t.hasNext())
			boolean filefound = false;
			while (t.hasNext()) {
				if (t.next().equals(job())) {
					filefound = true;
					break;
				}
			}

			if (wstreams != null) {
				// wstreams will be null when the source file contains a NativeRep class
				wstreams.commitStreams();
			}

			return true;
		}
		catch (IOException e) {
			job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR,
					"I/O error while translating: " + e.getMessage());
			return false;
		}
	}

	private void printLineNumberMapForCPPDebugger(StreamWrapper sw, HashMap<String, LineNumberMap> fileToLineNumberMap) {
	    final LineNumberMap map = fileToLineNumberMap.get(sw.getStreamName(StreamWrapper.CC));
	    sw.currentStream().registerCommitListener(new ClassifiedStream.CommitListener() {
	        public void run(ClassifiedStream s) {
//	            if (map.isEmpty())
//	                return;
	            s.forceNewline();
	            LineNumberMap.exportForCPPDebugger(s, map);
	        }
	    });
	}

	private void printLineNumberMap(StreamWrapper sw, String pkg, String className, final String ext, HashMap<String, LineNumberMap> fileToLineNumberMap) {
		String fName = sw.getStreamName(ext);
		final LineNumberMap map = fileToLineNumberMap.get(fName);
		final String lnmName = Emitter.translate_mangled_FQN(pkg, "_")+"_"+Emitter.mangled_non_method_name(className);
		sw.currentStream().registerCommitListener(new ClassifiedStream.CommitListener() {
		    public void run(ClassifiedStream s) {
		        if (map.isEmpty())
		            return;
		        s.forceNewline();
//		        sw.write("struct LNMAP_"+lnmName+"_"+ext+" { static const char* map; };");
//		        sw.newline();
//		        sw.write("const char* LNMAP_"+lnmName+"_"+ext+"::map = \"");
		        s.write("extern \"C\" { const char* LNMAP_"+lnmName+"_"+ext+" = \"");
		        s.write(StringUtil.escape(map.exportMap()));
//		        String v = map.exportMap();
//		        LineNumberMap m = LineNumberMap.importMap(v);
		        s.write("\"; }");
		        s.newline();
		    }
		});
	}

	/* (non-Javadoc)
	 * @see polyglot.visit.Translator#translate(polyglot.ast.Node)
	 */
	public boolean translate(Node ast) {
		if (ast instanceof SourceFile) {
			SourceFile sfn = (SourceFile) ast;
			boolean okay = translateSource(sfn);
			final ExtensionInfo ext = job.extensionInfo();
			final Options options = ext.getOptions();
			final ErrorQueue eq = new StdErrorQueue(System.err,
					options.error_count,
					ext.compilerName());
			if (!okay)
				return false;
			return true;
		}
		else if (ast instanceof SourceCollection) {
			SourceCollection sc = (SourceCollection) ast;
			// TODO: [IP] separate compilation
//			if (true) throw new InternalCompilerError("Source collections not supported");
			return translateSourceCollection(sc);
		}
		else {
			throw new InternalCompilerError("AST root must be a SourceFile; " +
			                                "found a " + ast.getClass().getName());
		}
	}

	public DelegateTargetFactory getTargetFactory() {
		return (DelegateTargetFactory) this.tf;
	}

	public static final String postcompile = "postcompile";


    /**
	 * The post-compiler option has the following structure:
	 * "[pre-command with options (usually g++)] [(#|%) [post-options (usually extra files)] [(#|%) [library options]]]".
	 * Using '%' instead of '#' to delimit a section will cause the default values in that section to be omitted.
	 */
	public static boolean postCompile(Options options, Compiler compiler, ErrorQueue eq) {
		if (eq.hasErrors())
			return false;

		if (options.post_compiler != null && !options.output_stdout) {
			// use set to avoid duplicates
            Set<String> outputFiles = new HashSet<String>(compiler.outputFiles());
            CXXCommandBuilder ccb = CXXCommandBuilder.getCXXCommandBuilder(options, eq);
            String[] cxxCmd = ccb.buildCXXCommandLine(outputFiles);

			if (!doPostCompile(options, eq, outputFiles, cxxCmd)) return false;
            
			// FIXME: [IP] HACK: Prevent the java post-compiler from running
			options.post_compiler = null;
		}
		return true;
	}

    public static boolean doPostCompile(Options options, ErrorQueue eq, Collection<String> outputFiles, String[] cxxCmd) {
        if (Report.should_report(postcompile, 1)) {
        	StringBuffer cmdStr = new StringBuffer();
        	for (int i = 0; i < cxxCmd.length; i++)
        		cmdStr.append(cxxCmd[i]+" ");
        	Report.report(1, "Executing post-compiler " + cmdStr);
        }

        try {
            Runtime runtime = Runtime.getRuntime();
        	Process proc = runtime.exec(cxxCmd, null, null);

        	InputStreamReader err = new InputStreamReader(proc.getErrorStream());

        	String output = null;
        	try {
        		char[] c = new char[72];
        		int len;
        		StringBuffer sb = new StringBuffer();
        		while((len = err.read(c)) > 0) {
        			sb.append(String.valueOf(c, 0, len));
        		}

        		if (sb.length() != 0) {
        			output = sb.toString();
        		}
        	}
        	finally {
        		err.close();
        	}

        	proc.waitFor();

        	if (!options.keep_output_files) {
        		String[] rmCmd = new String[1+outputFiles.size()];
        		rmCmd[0] = "rm";
        		Iterator<String> iter = outputFiles.iterator();
        		for (int i = 1; iter.hasNext(); i++)
        			rmCmd[i] = iter.next();
        		runtime.exec(rmCmd);
        	}

        	if (output != null)
        		eq.enqueue(proc.exitValue() > 0 ? ErrorInfo.POST_COMPILER_ERROR : ErrorInfo.WARNING, output);
        	if (proc.exitValue() > 0) {
        		eq.enqueue(ErrorInfo.POST_COMPILER_ERROR,
        				"Non-zero return code: " + proc.exitValue());
        		return false;
        	}
        }
        catch(Exception e) {
        	eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, e.getMessage() != null ? e.getMessage() : e.toString());
        	return false;
        }
        return true;
    }

	private boolean translateSourceCollection(SourceCollection sc) {
		boolean okay = true;

		for (Iterator i = sc.sources().iterator(); i.hasNext(); ) {
			SourceFile sfn = (SourceFile) i.next();
			okay &= translateSource(sfn);
		}

		if (true)
			throw new InternalCompilerError("Don't yet know how to translate source collections");
		return okay;
	}
}
