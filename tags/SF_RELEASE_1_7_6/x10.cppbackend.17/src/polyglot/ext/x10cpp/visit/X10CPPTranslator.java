/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10cpp.visit;

import java.io.File;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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

import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.X10ClassDecl;

import polyglot.ext.x10cpp.Configuration;
import polyglot.ext.x10cpp.X10CPPCompilerOptions;
import polyglot.ext.x10cpp.debug.LineNumberMap;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
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
import polyglot.util.QuotedStringTokenizer;
import polyglot.util.StdErrorQueue;
import polyglot.util.StringUtil;
import polyglot.visit.Translator;
import x10c.util.ClassifiedStream;
import x10c.util.StreamWrapper;
import x10c.util.WriterStreams;
import static polyglot.ext.x10cpp.visit.SharedVarsMethods.*;

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
			int line = n.position().line();
			String file = n.position().file();
			w.write("//#line " + line + " \"" + file + "\"");
			w.newline();
			if (polyglot.ext.x10.Configuration.DEBUG) {
				X10CPPContext_c c = (X10CPPContext_c)context;
				HashMap<String, LineNumberMap> fileToLineNumberMap =
					(HashMap<String, LineNumberMap>) c.findData(FILE_TO_LINE_NUMBER_MAP);
				LineNumberMap lineNumberMap = fileToLineNumberMap.get(w.getStreamName(w.currentStream().ext));
				// [DC] avoid NPE when writing to .cu files
				if (lineNumberMap!=null) {
    				int outputLine = w.currentStream().getLineNumber();
    				// FIXME: Debugger HACK: adjust for loops
    				if (n instanceof For || n instanceof ForLoop)
    					outputLine++;
    				lineNumberMap.put(outputLine, file, line);
    				if (n instanceof MethodDecl) {
    					lineNumberMap.addMethodMapping(((MethodDecl) n).methodDef());
    				}
    				if (n instanceof ConstructorDecl) {
    					lineNumberMap.addMethodMapping(((ConstructorDecl) n).constructorDef());
    				}
                }
			}
		}

		// FIXME: [IP] Some nodes have no del() -- warn in that case
		super.print(parent, n, w_);

		if (n != null && n.position().endLine() > 0 && (n instanceof Block))
		{
			if (polyglot.ext.x10.Configuration.DEBUG) {
				X10CPPContext_c c = (X10CPPContext_c)context;
				HashMap<String, LineNumberMap> fileToLineNumberMap =
					(HashMap<String, LineNumberMap>) c.findData(FILE_TO_LINE_NUMBER_MAP);
				LineNumberMap lineNumberMap = fileToLineNumberMap.get(w.getStreamName(w.currentStream().ext));
                // [DC] avoid NPE when writing to .cu files
                if (lineNumberMap!=null) {
    				int endLine = n.position().endLine();
    				String file = n.position().file();
    				int outputLine = w.currentStream().getLineNumber();
    				lineNumberMap.put(outputLine, file, endLine);
                }
			}
		}
	}

	/**
	 * Only for the backend -- use with care!
	 * FIXME: HACK!!! HACK!!! HACK!!!
	 */
	void setContext(Context c) {
		context = c;
	}

	/* (non-Javadoc)
	 * @see polyglot.visit.Translator#translateSource(polyglot.ast.SourceFile)
	 */
	protected boolean translateSource(SourceFile sfn) {
		DelegateTargetFactory tf = (DelegateTargetFactory) this.tf;

		int outputWidth = job.compiler().outputWidth();
		Collection outputFiles = job.compiler().outputFiles();

		try {
			String opfPath;

			String pkg = "";
			if (sfn.package_() != null) {
				Package p = sfn.package_().package_().get();
				pkg = p.fullName().toString();
			}

			X10CPPContext_c c = (X10CPPContext_c) context;
			if (polyglot.ext.x10.Configuration.DEBUG)
				c.addData(FILE_TO_LINE_NUMBER_MAP, new HashMap<String, LineNumberMap>());
			WriterStreams wstreams = null;
			StreamWrapper sw = null;
			// Use the class name to derive a default output file name.
			for (Iterator i = sfn.decls().iterator(); i.hasNext(); ) {
				TopLevelDecl decl = (TopLevelDecl) i.next();
				if (!(decl instanceof X10ClassDecl))
					continue;
				X10ClassDecl cd = (X10ClassDecl) decl;
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
				if (!opfPath.endsWith("$")) outputFiles.add(opfPath);
				if (polyglot.ext.x10.Configuration.DEBUG) {
					HashMap<String, LineNumberMap> fileToLineNumberMap = (HashMap<String, LineNumberMap>)c.getData(FILE_TO_LINE_NUMBER_MAP);
					String closures = wstreams.getStreamName(StreamWrapper.Closures);
					fileToLineNumberMap.put(closures, new LineNumberMap(closures));
					String cc = wstreams.getStreamName(StreamWrapper.CC);
					fileToLineNumberMap.put(cc, new LineNumberMap(cc));
					String header = wstreams.getStreamName(StreamWrapper.Header);
					fileToLineNumberMap.put(header, new LineNumberMap(header));
				}
				translateTopLevelDecl(sw, sfn, decl);
				if (polyglot.ext.x10.Configuration.DEBUG) {
					HashMap<String, LineNumberMap> fileToLineNumberMap = (HashMap<String, LineNumberMap>)c.getData(FILE_TO_LINE_NUMBER_MAP);
					sw.pushCurrentStream(sw.getNewStream(StreamWrapper.Closures, false));
					printLineNumberMap(sw, pkg, className, StreamWrapper.Closures, fileToLineNumberMap);
					sw.popCurrentStream();
					sw.pushCurrentStream(sw.getNewStream(StreamWrapper.CC, false));
					printLineNumberMap(sw, pkg, className, StreamWrapper.CC, fileToLineNumberMap);
					sw.popCurrentStream();
					sw.pushCurrentStream(sw.getNewStream(StreamWrapper.CC, false));
					printLineNumberMap(sw, pkg, className, StreamWrapper.Header, fileToLineNumberMap);
					sw.popCurrentStream();
				}
				if (i.hasNext())
					wstreams.commitStreams();
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

			wstreams.commitStreams();

			return true;
		}
		catch (IOException e) {
			job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR,
					"I/O error while translating: " + e.getMessage());
			return false;
		}
	}

	private void printLineNumberMap(StreamWrapper sw, String pkg, String className, String ext, HashMap<String, LineNumberMap> fileToLineNumberMap) {
		String fName = sw.getStreamName(ext);
		LineNumberMap map = fileToLineNumberMap.get(fName);
		if (map.isEmpty())
			return;
		sw.forceNewline();
		String lnmName = Emitter.translate_mangled_FQN(pkg).replace("::","_")+"_"+Emitter.mangled_non_method_name(className);
//		sw.write("struct LNMAP_"+lnmName+"_"+ext+" { static const char* map; };");
//		sw.newline();
//		sw.write("const char* LNMAP_"+lnmName+"_"+ext+"::map = \"");
		sw.write("extern \"C\" const char* LNMAP_"+lnmName+"_"+ext+" = \"");
		sw.write(StringUtil.escape(map.exportMap()));
//		String v = map.exportMap();
//		LineNumberMap m = LineNumberMap.importMap(v);
		sw.write("\";");
		sw.newline();
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

    private static class CXXCommandBuilder {
        public static final String DUMMY = "-U___DUMMY___";

        public static final String X10LANG = System.getenv("X10LANG")==null?"../../../x10.runtime.17/src-cpp":System.getenv("X10LANG").replace(File.separatorChar, '/');
        public static final String X10LIB = System.getenv("X10LIB")==null?"../../../pgas2/common/work":System.getenv("X10LIB").replace(File.separatorChar, '/');
        public static final String X10GC = System.getenv("X10GC")==null?"../../../x10.dist":System.getenv("X10GC").replace(File.separatorChar, '/');
        public static final String TRANSPORT = System.getenv("X10RT_TRANSPORT")==null?DEFAULT_TRANSPORT:System.getenv("X10RT_TRANSPORT");
        public static final boolean USE_XLC = PLATFORM.startsWith("aix_") && System.getenv("USE_GCC")==null;
        public static final boolean USE_BFD = System.getenv("USE_BFD")!=null;

        public static final String MANIFEST = "libx10.mft";
        public static final String[] MANIFEST_LOCATIONS = new String[] {
            X10LANG,
            X10LANG+"/lib",
        };
        /** These go before the files */
        public static final String[] preArgs = new String[] {
            "-g",
            "-I"+X10LIB+"/include",
            "-I"+X10LANG,
            "-I"+X10LANG+"/gen", // FIXME: development option
            "-I"+X10LANG+"/include", // dist
            "-I.",
            "-DTRANSPORT="+TRANSPORT,
        };
        /** These go after the files */
        public static final String[] postArgs = new String[] {
            "-L"+X10LIB+"/lib",
            "-L"+X10LANG,
            "-L"+X10LANG+"/lib", // dist
            "-lx10",
            "-lupcrts_"+TRANSPORT,
            "-ldl",
            "-lm",
            "-lpthread",
        };
        /** These go before the files if gcEnabled is true */
        public static final String[] preArgsGC = new String[] {
            "-DX10_USE_BDWGC",
            "-I"+X10GC+"/include",
        };
        /** These go after the files if gcEnabled is true */
        public static final String[] postArgsGC = new String[] {
            X10GC+"/lib/libgc.a",
        };

        /** These go before the files if optimize is true */
        public static final String[] preArgsOptimize = new String[] {
            "-O2",
            USE_XLC ? "-qinline" : "-finline-functions",
            USE_XLC ? "-qhot" : DUMMY,
            "-DNDEBUG"
        };

        private final X10CPPCompilerOptions options;

        public CXXCommandBuilder(Options options) {
            assert (options != null);
            assert (options.post_compiler != null);
            this.options = (X10CPPCompilerOptions) options;
        }

        /** Is GC enabled on this platform? */
        protected boolean gcEnabled() { return false; }

        protected String defaultPostCompiler() { return "g++"; }

        /** Add the arguments that go before the output files */
        protected void addPreArgs(ArrayList<String> cxxCmd) {
            for (int i = 0; i < preArgs.length; i++) {
                cxxCmd.add(preArgs[i]);
            }
            if (!Configuration.DISABLE_GC && gcEnabled()) {
                for (int i = 0; i < preArgsGC.length; i++) {
                    cxxCmd.add(preArgsGC[i]);
                }
            }
            if (polyglot.ext.x10.Configuration.OPTIMIZE) {
              for (String arg : preArgsOptimize) {
                cxxCmd.add(arg);
              }
            }
        }

        /** Add the arguments that go after the output files */
        protected void addPostArgs(ArrayList<String> cxxCmd) {
            if (!Configuration.DISABLE_GC && gcEnabled()) {
                for (int i = 0; i < postArgsGC.length; i++) {
                    cxxCmd.add(postArgsGC[i]);
                }
            }
            for (int i = 0; i < postArgs.length; i++) {
                cxxCmd.add(postArgs[i]);
            }
        }

        protected void addExecutablePath(ArrayList<String> cxxCmd) {
            File exe = null;
            if (options.executable_path != null)
                exe = new File(options.executable_path);
            else if (Configuration.MAIN_CLASS != null)
                exe = new File(options.output_directory, Configuration.MAIN_CLASS);
            else
                return;
            cxxCmd.add("-o");
            cxxCmd.add(exe.getAbsolutePath().replace(File.separatorChar,'/'));
        }

        /** Construct the C++ compilation command */
        public final String[] buildCXXCommandLine(Collection<String> outputFiles) {
            String post_compiler = options.post_compiler;
            if (post_compiler.contains("javac"))
                post_compiler = defaultPostCompiler();

            QuotedStringTokenizer st = new QuotedStringTokenizer(post_compiler);
            int pc_size = st.countTokens();
            ArrayList<String> cxxCmd = new ArrayList<String>();
            String token = "";
            for (int i = 0; i < pc_size; i++) {
                token = st.nextToken();
                // The first '#' marks the place where the filenames go
                if (token.equals("#") || token.equals("%")) {
                    break;
                }
                cxxCmd.add(token);
            }

            boolean skipArgs = token.equals("%");
            if (!skipArgs) {
                addPreArgs(cxxCmd);
                addExecutablePath(cxxCmd);
            }

            HashSet<String> exclude = new HashSet<String>();
            try {
                String manifest = Configuration.MANIFEST;
                if (manifest == null) {
                    for (int i = 0; i < MANIFEST_LOCATIONS.length; i++) {
                        File x10lang_m = new File(MANIFEST_LOCATIONS[i]+"/"+MANIFEST);
                        if (!x10lang_m.exists())
                            continue;
                        manifest = x10lang_m.getPath();
                    }
                }
                if (manifest != null) {
                    FileReader fr = new FileReader(manifest);
                    BufferedReader br = new BufferedReader(fr);
                    String file = "";
                    while ((file = br.readLine()) != null)
                        exclude.add(file);
                }
            } catch (IOException e) { }

            Iterator iter = outputFiles.iterator();
            for (; iter.hasNext(); ) {
                String file = (String) iter.next();
                file = file.replace(File.separatorChar,'/');
                if (exclude.contains(file))
                    continue;
                cxxCmd.add(file);
            }

            while (st.hasMoreTokens()) {
                token = st.nextToken();
                // The second '#' delimits the libraries that have to go at the end
                if (token.equals("#") || token.equals("%")) {
                    break;
                }
                cxxCmd.add(token);
            }

            boolean skipLibs = token.equals("%");
            if (!skipLibs) {
                addPostArgs(cxxCmd);
            }

            while (st.hasMoreTokens()) {
                cxxCmd.add(st.nextToken());
            }

            return cxxCmd.toArray(new String[cxxCmd.size()]);
        }
    }

    private static class Cygwin_CXXCommandBuilder extends CXXCommandBuilder {
        /** These go before the files */
        public static final String[] preArgsCygwin = new String[] {
            "-Wno-long-long",
            "-Wno-unused-parameter",
            "-msse2",
            "-mfpmath=sse",
        };
        /** These go after the files */
        public static final String[] postArgsCygwin = new String[] {
        };

        public Cygwin_CXXCommandBuilder(Options options) {
            super(options);
            assert (PLATFORM.startsWith("win32_"));
        }

        protected boolean gcEnabled() { return false; }

        protected void addPreArgs(ArrayList<String> cxxCmd) {
            super.addPreArgs(cxxCmd);
            for (int i = 0; i < preArgsCygwin.length; i++) {
                cxxCmd.add(preArgsCygwin[i]);
            }
        }

        protected void addPostArgs(ArrayList<String> cxxCmd) {
            super.addPostArgs(cxxCmd);
            for (int i = 0; i < postArgsCygwin.length; i++) {
                cxxCmd.add(postArgsCygwin[i]);
            }
        }
    }

    private static class Linux_CXXCommandBuilder extends CXXCommandBuilder {
        public static final boolean USE_X86 = PLATFORM.endsWith("_x86");
        /** These go before the files */
        public static final String[] preArgsLinux = new String[] {
            "-Wno-long-long",
            "-Wno-unused-parameter",
            "-pthread",
            USE_X86 ? "-msse2" : DUMMY,
            USE_X86 ? "-mfpmath=sse" : DUMMY,
        };
        /** These go after the files */
        public static final String[] postArgsLinux = new String[] {
            "-Wl,-export-dynamic",
            USE_BFD ? "-lbfd" : DUMMY,
            "-lrt",
        };

        public Linux_CXXCommandBuilder(Options options) {
            super(options);
            assert (PLATFORM.startsWith("linux_"));
        }

        protected boolean gcEnabled() { return true; }

        protected void addPreArgs(ArrayList<String> cxxCmd) {
            super.addPreArgs(cxxCmd);
            for (int i = 0; i < preArgsLinux.length; i++) {
                cxxCmd.add(preArgsLinux[i]);
            }
        }

        protected void addPostArgs(ArrayList<String> cxxCmd) {
            super.addPostArgs(cxxCmd);
            for (int i = 0; i < postArgsLinux.length; i++) {
                cxxCmd.add(postArgsLinux[i]);
            }
        }
    }

    private static class AIX_CXXCommandBuilder extends CXXCommandBuilder {
        //"mpCC_r -q64 -qrtti=all -qarch=pwr5 -O3 -qtune=pwr5 -qhot -qinline"
        //"mpCC_r -q64 -qrtti=all"
        public static final String XLC_EXTRA_FLAGS = System.getenv("XLC_EXTRA_FLAGS");
        /** These go before the files */
        public static final String[] preArgsAIX = new String[] {
            USE_XLC ? DUMMY : "-Wno-long-long",
            USE_XLC ? "-qsuppress=1540-0809:1500-029" : "-Wno-unused-parameter",
            USE_XLC ? "-q64" : "-maix64", // Assume 64-bit
            USE_XLC ? "-qrtti=all" : DUMMY,
            //USE_XLC ? DUMMY : "-pipe", // TODO: is this needed?
            USE_XLC && XLC_EXTRA_FLAGS!=null ? XLC_EXTRA_FLAGS : DUMMY,
        };
        /** These go after the files */
        public static final String[] postArgsAIX = new String[] {
            USE_XLC ? "-bbigtoc" : "-Wl,-bbigtoc",
            USE_XLC ? "-lptools_ptr" : "-Wl,-lptools_ptr",
            USE_XLC || !TRANSPORT.equals("lapi") ? DUMMY : "-Wl,-binitfini:poe_remote_main",
            USE_XLC || !TRANSPORT.equals("lapi") ? DUMMY : "-L/usr/lpp/ppe.poe/lib",
            USE_XLC || !TRANSPORT.equals("lapi") ? DUMMY : "-lmpi_r",
            USE_XLC || !TRANSPORT.equals("lapi") ? DUMMY : "-lvtd_r",
            USE_XLC || !TRANSPORT.equals("lapi") ? DUMMY : "-llapi_r",
        };

        public AIX_CXXCommandBuilder(Options options) {
            super(options);
            assert (PLATFORM.startsWith("aix_"));
        }

        protected boolean gcEnabled() { return false; }

        protected String defaultPostCompiler() {
            if (USE_XLC)
                return "mpCC_r";
            return "g++";
        }

        protected void addPreArgs(ArrayList<String> cxxCmd) {
            super.addPreArgs(cxxCmd);
            for (int i = 0; i < preArgsAIX.length; i++) {
                cxxCmd.add(preArgsAIX[i]);
            }
        }

        protected void addPostArgs(ArrayList<String> cxxCmd) {
            super.addPostArgs(cxxCmd);
            for (int i = 0; i < postArgsAIX.length; i++) {
                cxxCmd.add(postArgsAIX[i]);
            }
        }
    }

    private static class SunOS_CXXCommandBuilder extends CXXCommandBuilder {
        /** These go before the files */
        public static final String[] preArgsSunOS = new String[] {
            "-Wno-long-long",
            "-Wno-unused-parameter",
        };
        /** These go after the files */
        public static final String[] postArgsSunOS = new String[] {
            "-lresolv",
            "-lnsl",
            "-lsocket",
            "-lrt",
        };

        public SunOS_CXXCommandBuilder(Options options) {
            super(options);
            assert (PLATFORM.startsWith("sunos_"));
        }

        protected boolean gcEnabled() { return false; }

        protected void addPreArgs(ArrayList<String> cxxCmd) {
            super.addPreArgs(cxxCmd);
            for (int i = 0; i < preArgsSunOS.length; i++) {
                cxxCmd.add(preArgsSunOS[i]);
            }
        }

        protected void addPostArgs(ArrayList<String> cxxCmd) {
            super.addPostArgs(cxxCmd);
            for (int i = 0; i < postArgsSunOS.length; i++) {
                cxxCmd.add(postArgsSunOS[i]);
            }
        }
    }

    public static final String PLATFORM = System.getenv("X10_PLATFORM")==null?"unknown":System.getenv("X10_PLATFORM");
    public static final String DEFAULT_TRANSPORT = PLATFORM.startsWith("aix_")?"lapi":"sockets";

    private static CXXCommandBuilder getCXXCommandBuilder(Options options, ErrorQueue eq) {
        if (PLATFORM.startsWith("win32_"))
            return new Cygwin_CXXCommandBuilder(options);
        if (PLATFORM.startsWith("linux_"))
            return new Linux_CXXCommandBuilder(options);
        if (PLATFORM.startsWith("aix_"))
            return new AIX_CXXCommandBuilder(options);
        if (PLATFORM.startsWith("sunos_"))
            return new SunOS_CXXCommandBuilder(options);
        eq.enqueue(ErrorInfo.WARNING,
                "Unknown platform '"+PLATFORM+"'; using the default post-compiler (g++)");
        return new CXXCommandBuilder(options);
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
            Collection<String> outputFiles = compiler.outputFiles();
            String[] cxxCmd = getCXXCommandBuilder(options, eq).buildCXXCommandLine(outputFiles);

			if (Report.should_report(postcompile, 1)) {
				StringBuffer cmdStr = new StringBuffer();
				for (int i = 0; i < cxxCmd.length; i++)
					cmdStr.append(cxxCmd[i]+" ");
				Report.report(1, "Executing post-compiler " + cmdStr);
			}

			try {
                Runtime runtime = Runtime.getRuntime();
				Process proc = runtime.exec(cxxCmd, null, options.output_directory);

				InputStreamReader err = new InputStreamReader(proc.getErrorStream());

				try {
					char[] c = new char[72];
					int len;
					StringBuffer sb = new StringBuffer();
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

				proc.waitFor();

				if (!options.keep_output_files) {
					String[] rmCmd = new String[1+outputFiles.size()];
					rmCmd[0] = "rm";
					Iterator iter = outputFiles.iterator();
					for (int i = 1; iter.hasNext(); i++)
						rmCmd[i] = (String) iter.next();
					runtime.exec(rmCmd);
				}

				if (proc.exitValue() > 0) {
					eq.enqueue(ErrorInfo.POST_COMPILER_ERROR,
							"Non-zero return code: " + proc.exitValue());
					return false;
				}
			}
			catch(Exception e) {
				eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, e.getMessage());
				return false;
			}
			// FIXME: [IP] HACK: Prevent the java post-compiler from running
			options.post_compiler = null;
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
