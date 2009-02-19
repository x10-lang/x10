/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10cpp.visit;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Import;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceCollection;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.TopLevelDecl;

import polyglot.ext.x10.visit.X10InnerClassRemover;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.frontend.TargetFactory;

import polyglot.main.Options;
import polyglot.main.Report;

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
import polyglot.visit.InnerClassRemover;
import polyglot.visit.Translator;
import x10c.util.ClassifiedStream;
import x10c.util.StreamWrapper;
import x10c.util.WriterStreams;
import static polyglot.ext.x10cpp.visit.SharedVarsMethods.*;

public class X10CPPTranslator extends Translator {
	public static final class DelegateTargetFactory extends TargetFactory {
		protected String outputHeaderExtension;

		private DelegateTargetFactory(File dir, String ext, String hExt, boolean so) {
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

		public String outputName(String packageName, String className) {
			return packagePath(packageName) + className + "." + outputExtension;
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
			return packagePath(packageName) + className + "." + outputHeaderExtension;
		}

		public File outputHeaderFile(String packageName, String className, Source source) {
			File outputFile = new File(outputDirectory, outputHeaderName(packageName, className));

			if (source != null && outputFile.getPath().equals(source.path()))
				throw new InternalCompilerError("The header file is the same as the source file");

			return outputFile;
		}

		public File integratedOutputFile(String packageName, String className, Source source, String ext) {
			File outputFile = new File(outputDirectory, 
					      packagePath(packageName) + className + "." + ext);

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

	public void print(Node parent, Node n, CodeWriter w_) {
		if (w_ == null)
			return; // FIXME HACK
		ClassifiedStream w = ((StreamWrapper) w_).cs;
		if (n != null && n.position().line() > 0 &&
				((n instanceof Stmt && !(n instanceof Block)) ||
				 (n instanceof FieldDecl) ||
				 (n instanceof MethodDecl) ||
				 (n instanceof ConstructorDecl) ||
				 (n instanceof ClassDecl)))
		{
			w.forceNewline(0);
			w.write("//#line " + n.position().line() + " \"" + n.position().file() + "\"");
			w.newline();
		}

		// FIXME: [IP] Some nodes have no del() -- warn in that case
		super.print(parent, n, w_);
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
		TypeSystem ts = typeSystem();
		NodeFactory nf = nodeFactory();
		DelegateTargetFactory tf = (DelegateTargetFactory) this.tf;

		InnerClassRemover v = (InnerClassRemover) new  X10InnerClassRemover(job, ts, nf).begin();
		Context c = sfn.del().enterScope(v.context());
		v = (InnerClassRemover) v.context(c);
		sfn = (SourceFile) sfn.visit(v);

		int outputWidth = job.compiler().outputWidth();
		Collection outputFiles = job.compiler().outputFiles();

		// Find the public declarations in the file.  We'll use these to
		// derive the names of the target files.  There will be one
		// target file per public declaration.  If there are no public
		// declarations, we'll use the source file name to derive the
		// target file name.
		List exports = exports(sfn);

		try {
			String opfPath;
			CodeWriter w;

			String pkg = "";

			if (sfn.package_() != null) {
				Package p = sfn.package_().package_().get();
				pkg = p.fullName().toString();
			}
			
			 // Use the source name to derive a default output file name.
		    opfPath = tf.outputName(pkg, sfn.source());
		    if (!opfPath.endsWith("$")) outputFiles.add(opfPath);
		    StreamWrapper lastsw = null;
			for (Iterator i = sfn.decls().iterator(); i.hasNext(); ) {
				TopLevelDecl decl = (TopLevelDecl) i.next();
				WriterStreams wstreams = new WriterStreams(sfn, pkg, tf, exports, job);
				StreamWrapper sw = new StreamWrapper(wstreams.getNewStream(WriterStreams.StreamClass.CC), 
						outputWidth, wstreams);
				writeHeader(sfn, sw);
			  opfPath = tf.outputName(pkg, decl.name().toString());
			  if (!opfPath.endsWith("$")) outputFiles.add(opfPath);
			  translateTopLevelDecl(sw, sfn, decl); 
				writeFooter(sfn, sw);
			  if (i.hasNext())
				wstreams.commitStreams();
			  else 
				lastsw = sw;
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
			if (filefound && !t.hasNext())
				generateGlobalSwitch(lastsw, this);

			lastsw.ws.commitStreams();
			return true;
		}
		catch (IOException e) {
			job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR,
					"I/O error while translating: " + e.getMessage());
			return false;
		}
	}

	public static void generateGlobalSwitch(StreamWrapper sw, X10CPPTranslator tr) {
		ClassifiedStream w = sw.cs;
		WriterStreams  wstreams = sw.ws;
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		// TODO: extern "C"
		w.write("void* ArrayCopySwitch(x10_async_handler_t h, void* __arg) {");
		w.newline(4); w.begin(0);
		w.write("switch (h) {");
		w.newline(4); w.begin(0);
		// FIXME: Replace with Java 5 loop. 
		for (Iterator k = context.classesWithArrayCopySwitches.keySet().iterator(); k.hasNext(); ) {
			String className = (String) k.next();
			int[] async_ids = (int[]) context.classesWithArrayCopySwitches.get(className);
			for (int i = 0; i < async_ids.length; i++) {
				w.write("case "+async_ids[i]+":");
				w.newline();
			}
			w.newline(4); w.begin(0);
			w.write("return "+className+"::ArrayCopySwitch(h, __arg);");
			w.end(); w.newline();
		}
		w.end(); w.newline();
		w.write("}");
		w.end(); w.newline();
		w.write("return NULL;"); w.newline();
		w.write("}"); w.newline();

		w.write("void AsyncSwitch(x10_async_handler_t h, void* arg, int niter) {");
		w.newline(4); w.begin(0);
		w.write("switch (h) {");
		w.newline(4); w.begin(0);
		for (Iterator k = context.classesWithAsyncSwitches.keySet().iterator(); k.hasNext(); ) {
			String className = (String) k.next();
			int[] async_ids = (int[]) context.classesWithAsyncSwitches.get(className);
			for (int i = 0; i < async_ids.length; i++) {
				w.write("case "+async_ids[i]+":");
				w.newline();
			}
			w.newline(4); w.begin(0);
			w.write(className+"::AsyncSwitch(h, arg, niter);");
			w.newline();
			w.write("break;");
			w.end(); w.newline();
		}
		w.end(); w.newline();
		w.write("}");
		w.end(); w.newline();
		w.write("}"); w.newline();
	}

	/* (non-Javadoc)
	 * @see polyglot.visit.Translator#writeHeader(polyglot.ast.SourceFile, polyglot.util.CodeWriter)
	 */
	protected void writeHeader(SourceFile sfn, StreamWrapper sw) {
		WriterStreams wstreams = sw.ws;
		ClassifiedStream w = sw.cs;
		ClassifiedStream h = wstreams.getCurStream(WriterStreams.StreamClass.Header);

		String guard = wstreams.getFile(WriterStreams.StreamClass.Header).getName().replace(".", "_").replace(File.separator, "_").replace("-", "_").toUpperCase();
		h.write("#ifndef __"+guard); h.newline();
		h.write("#define __"+guard); h.newline();
		h.forceNewline(0);
		h.write("#include <x10lang.h>"); h.newline();
		h.forceNewline(0);

		w.write("#include \""+wstreams.getFile(WriterStreams.StreamClass.Header).getName()+"\""); w.newline();
		w.forceNewline(0);
		w.write("using namespace x10;"); w.newline();
		w.forceNewline(0);

		boolean newline = false;

		for (Iterator i = sfn.imports().iterator(); i.hasNext(); ) {
			Import imp = (Import) i.next();
			sw.pushCurrentStream(h);
			imp.del().translate(sw, this);
			sw.popCurrentStream();
			newline = true;
		}

		if (newline) {
			w.newline(0);
			h.newline(0);
		}

		if (sfn.package_() != null) {
			w.write("using namespace ");
			sw.pushCurrentStream(w);
			sfn.package_().del().translate(sw, this);
			sw.popCurrentStream();
			w.write(";");
			w.newline();
		}

		w.write("static "+make_ref("Exception")+" EXCEPTION = NULL;"); w.newline();
		w.write("#include \""+wstreams.getFile(WriterStreams.StreamClass.Closures).getName() + "\""); w.newline();
		w.forceNewline(0);

		if (sfn.package_() != null) {
			w.write("namespace ");
			sw.pushCurrentStream(w);
			sfn.package_().del().translate(sw, this);
			sw.popCurrentStream();
			w.write(" {");
			w.newline(0);
		}
		ClassifiedStream z = wstreams.getNewStream(WriterStreams.StreamClass.Closures); // HACK. We are creating an empty new closure stream, to put all the header information required in the closure. [Krishna.]
	}

	/** Write the footer for a source file. */
	protected void writeFooter(SourceFile sfn, StreamWrapper sw) {
		WriterStreams wstreams = sw.ws;
		ClassifiedStream w = sw.cs;
		ClassifiedStream h = wstreams.getCurStream(WriterStreams.StreamClass.Header);

		if (sfn.package_() != null) {
			w.newline(0);
			w.write("} // namespace ");
			sw.pushCurrentStream(w);
			sfn.package_().del().translate(sw, this);
			sw.popCurrentStream();
			w.newline(0);
		}

		h.newline(0);
		String guard = wstreams.getFile(WriterStreams.StreamClass.Header).getName().replace(".", "_").replace(File.separator, "_").toUpperCase();
		h.write("#endif // __"+guard); h.newline();
		h.forceNewline(0);

		// The declarations below are intentionally outside of the guard
		if (sfn.package_() != null) {
			h.write("namespace ");
			sw.pushCurrentStream(h);
			sfn.package_().del().translate(sw, this);
			sw.popCurrentStream();
			h.write(" {");
			h.newline(0);
		}
		for (Iterator i = sfn.decls().iterator(); i.hasNext(); ) {
			TopLevelDecl decl = (TopLevelDecl) i.next();
			if (decl.flags().flags().isPublic()) {
				h.write("class "+Emitter.mangled_non_method_name(decl.name().toString())+";");
				h.newline();
			}
		}
		if (sfn.package_() != null) {
			h.newline(0);
			h.write("} // namespace ");
			sw.pushCurrentStream(h);
			sfn.package_().del().translate(sw, this);
			sw.popCurrentStream();
			h.newline(0);
		}
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
			if (!System.getProperty("x10.postcompile", "TRUE").equals("FALSE"))
				return postCompile(options, job.compiler(), eq);
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

	public static boolean postCompile(Options options, Compiler compiler, ErrorQueue eq) {
		// FIXME: [IP] HACK
		final String post_compiler =
			(options.post_compiler != null && options.post_compiler.contains("javac")) ?
					"g++" :
					options.post_compiler;
		final String X10LANG = System.getenv("X10LANG")==null?"../../x10lang":System.getenv("X10LANG").replace(File.separatorChar, '/');
		final String X10LIB = System.getenv("X10LIB")==null?"../../../x10.lib":System.getenv("X10LIB").replace(File.separatorChar, '/');
		// These go before the files
		final String[] preArgs = new String[] {
			"-I"+X10LIB+"/include",
			"-I"+X10LANG,
			"-I.",
		};
		// These go after the files
		final String[] postArgs = new String[] {
			"-L"+X10LIB+"/lib",
			"-L"+X10LANG,
			"-lx10lang",
			"-lx10",
			"-ldl",
			"-lm",
		};
		if (post_compiler != null && !options.output_stdout) {
			Runtime runtime = Runtime.getRuntime();
			QuotedStringTokenizer st = new QuotedStringTokenizer(post_compiler);
			int pc_size = st.countTokens();
			String[] cxxCmd = new String[pc_size+preArgs.length+postArgs.length+compiler.outputFiles().size()];
			int j = 0;
			for (int i = 0; i < pc_size; i++) {
				String token = st.nextToken();
				// The first '#' marks the place where the filenames go
				if (token.equals("#")) {
					cxxCmd[j++] = "-U___DUMMY___"; // don't want to resize the array
					break;
				}
				cxxCmd[j++] = token;
			}

			for (int i = 0; i < preArgs.length; i++) {
				cxxCmd[j++] = preArgs[i];
			}

			Iterator iter = compiler.outputFiles().iterator();
			for (; iter.hasNext(); j++) {
				String file = (String) iter.next();
				cxxCmd[j] = file.replace(File.separatorChar,'/');
			}

			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				// The second '#' delimits the libraries that have to go at the end
				if (token.equals("#")) {
					cxxCmd[j++] = "-U___DUMMY___"; // don't want to resize the array
					break;
				}
				cxxCmd[j++] = token;
			}

			for (int i = 0; i < postArgs.length; i++) {
				cxxCmd[j++] = postArgs[i];
			}

			while (st.hasMoreTokens()) {
				cxxCmd[j++] = st.nextToken();
			}

			if (Report.should_report("postcompile", 1)) {
				StringBuffer cmdStr = new StringBuffer();
				for (int i = 0; i < cxxCmd.length; i++)
					cmdStr.append(cxxCmd[i]+" ");
				Report.report(1, "Executing post-compiler " + cmdStr);
			}

			try {
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
					String[] rmCmd = new String[1+compiler.outputFiles().size()];
					rmCmd[0] = "rm";
					iter = compiler.outputFiles().iterator();
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
