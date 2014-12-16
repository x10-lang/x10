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

package x10cpp.visit;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import polyglot.ast.Assert;
import polyglot.ast.Block;
import polyglot.ast.Branch;
import polyglot.ast.Catch;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.CompoundStmt;
import polyglot.ast.ConstructorCall.Kind;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Eval;
import polyglot.ast.FieldDecl;
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.If;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Return;
import polyglot.ast.SourceCollection;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.SwitchBlock;
import polyglot.ast.Term;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.Try;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.frontend.TargetFactory;

import polyglot.main.Options;
import polyglot.main.Reporter;

import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SimpleCodeWriter;
import polyglot.util.StdErrorQueue;
import polyglot.util.StringUtil;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.Translator;
import x10.ast.Closure;
import x10.ast.ForLoop;
import x10.ast.X10ClassDecl;
import x10.ast.X10ConstructorCall;
import x10.ast.X10ConstructorDecl;
import x10.extension.X10Ext;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.util.ClassifiedStream;
import x10.util.StreamWrapper;
import x10.util.WriterStreams;
import x10.visit.StaticNestedClassRemover;
import x10cpp.Configuration;
import x10cpp.X10CPPCompilerOptions;
import x10cpp.X10CPPJobExt;
import x10cpp.debug.LineNumberMap;
import x10cpp.postcompiler.AIX_CXXCommandBuilder;
import x10cpp.postcompiler.CXXCommandBuilder;
import x10cpp.postcompiler.Cygwin_CXXCommandBuilder;
import x10cpp.postcompiler.Linux_CXXCommandBuilder;
import x10cpp.postcompiler.PostCompileProperties;
import x10cpp.postcompiler.SharedLibProperties;
import x10cpp.postcompiler.SunOS_CXXCommandBuilder;
import x10cpp.types.X10CPPContext_c;
import static x10cpp.visit.ASTQuery.getCppRep;
import static x10cpp.visit.ASTQuery.getStringPropertyInit;
import static x10cpp.visit.SharedVarsMethods.*;

public class X10CPPTranslator extends Translator {
	
	public X10CPPTranslator(Job job, TypeSystem ts, NodeFactory nf, TargetFactory tf) {
		super(job, ts, nf, tf);
	}
    
	/** Return the dir where classes in the given package will be compiled.  Does not include output directory prefix.
	 * Accepts null input.  Note that if appending more paths to this directory, it is necessary to use '/' as a
	 * separator regardless of the platform. */
	public static String packagePath (String pkg) {
		return (pkg==null ? "" : pkg.replace('.', '/') + '/');
	}
	
	/** Return the filename of the c++ file for the given class.  Does not include output directory prefix.
	 * If no package then give null.  Note that if further processing the returned value, it is necessary to use '/' as a
	 * separator regardless of the platform. */
	public static String outputFileName (String pkg, String c, String ext) {
		return packagePath(pkg) + Emitter.mangled_non_method_name(c) + "." + ext;
	}
	
	/** Return the c++ file for the given class. */
	public static File outputFile (Options opts, String pkg, String c, String ext) {
		return new File(opts.output_directory, outputFileName(pkg, c, ext));
	}
    
	private static int adjustSLNForNode(int outputLine, Node n) {
	    // FIXME: Debugger HACK: adjust for loops
	    if (n instanceof For || n instanceof ForLoop)
	        return outputLine + 1;
	    return outputLine;
	}

	private static int adjustELNForNode(int outputLine, Node n) {
	    if (n instanceof For || n instanceof ForLoop)
	        return outputLine - 2;
	    if (n instanceof Return && ((Return) n).expr() == null)
	        return outputLine;
	    if (n instanceof Eval || n instanceof Branch || n instanceof Try)
	        return outputLine;
	    if (n instanceof Block)
	        return outputLine;
	    return outputLine - 1;
	}

	static final String FILE_TO_LINE_NUMBER_MAP = "FileToLineNumberMap";

	public void print(Node parent, Node n, CodeWriter w_) {
		if (w_ == null)
			return; // FIXME HACK
		StreamWrapper w = (StreamWrapper) w_;
		assert (n != null);
		final int line = n.position().line();
		final int column = n.position().column();
		final String file = n.position().file();
		if (line > 0 &&
				((n instanceof Stmt && !(n instanceof Block) && !(n instanceof Catch)) ||
				 (n instanceof FieldDecl) ||
				 (n instanceof MethodDecl) ||
				 (n instanceof ConstructorDecl) ||
				 (n instanceof ClassDecl)))
		{
		    Position lastLine = ((X10CPPContext_c)context).lastLine;
		    if (lastLine == null || lastLine.line() != line || !lastLine.file().equals(file)) {
		        ((X10CPPContext_c)context).lastLine = n.position();
		        w.forceNewline(0);
		        w.writeln("//#line " + line + " \"" + file + "\"");
		    }
		}
		
		X10CPPCompilerOptions opts = (X10CPPCompilerOptions) job.extensionInfo().getOptions();
		
		final int startLine = w.currentStream().getStreamLineNumber(); // for debug info

		// FIXME: [IP] Some nodes have no del() -- warn in that case
		super.print(parent, n, w_);

		final int endLine = w.currentStream().getStreamLineNumber() - w.currentStream().getOmittedLines(); // for debug info

		if (opts.x10_config.DEBUG && opts.x10_config.DEBUG_ENABLE_LINEMAPS && line > 0 &&
		    ((n instanceof Stmt && !(n instanceof SwitchBlock) && !(n instanceof Catch)) ||
		     (n instanceof ClassMember)))
		{
		    final String cppFile = w.getStreamName(w.currentStream().ext);
		    String key = w.getStreamName(StreamWrapper.CC);
		    X10CPPContext_c c = (X10CPPContext_c) context;
		    Map<String, LineNumberMap> fileToLineNumberMap =
		        c.<Map<String, LineNumberMap>>findData(FILE_TO_LINE_NUMBER_MAP);
		    if (fileToLineNumberMap != null) {
		        final LineNumberMap lineNumberMap = fileToLineNumberMap.get(key);
		        // [DC] avoid NPE when writing to .cu files
		        // [DC] avoid NPE when parent is null, e.g. generating initialisation for cuda shm
		        if (lineNumberMap != null && parent != null) {
		            final MemberDef def =
		            	(n instanceof ConstructorDecl) ?
		            		((ConstructorDecl) n).constructorDef() :
			                (n instanceof Block) ?
			                    (parent instanceof MethodDecl) ? ((MethodDecl) parent).methodDef() 
			                : null
			             : null;
		            final int lastX10Line = (n instanceof ConstructorDecl)? n.position().endLine() : parent.position().endLine();
		            if (n instanceof Stmt || n instanceof ProcedureDecl)
	                {
		                final int adjustedStartLine = adjustSLNForNode(startLine, n);
		                final int adjustedEndLine = adjustELNForNode(endLine, n);
		                final int fixedEndLine = adjustedEndLine < adjustedStartLine ? adjustedStartLine : adjustedEndLine;
		                //final boolean generated = n.position().isCompilerGenerated(); // || ((parent instanceof Closure) && (!(n instanceof ProcedureDecl) || (n instanceof ProcedureDecl && ((ProcedureDecl)n).reachable())));
		                final boolean isBlock = (n instanceof polyglot.ast.Block_c);
		                final boolean addLastLine = ((n instanceof ConstructorDecl) || (n instanceof Block && !((Block)n).statements().isEmpty() && 
		                		((Block)n).position().endLine() != ((Block)n).statements().get(((Block)n).statements().size()-1).position().endLine()));
		                w.currentStream().registerCommitListener(new ClassifiedStream.CommitListener() {
		                    public void run(ClassifiedStream s) {
		                        int cppStartLine = s.getStartLineOffset()+adjustedStartLine;
		                        int cppEndLine = s.getStartLineOffset()+fixedEndLine;
		                        if (def != null && !def.position().isCompilerGenerated())
		                        {
		                            lineNumberMap.addMethodMapping(def, cppFile, cppStartLine, cppEndLine, lastX10Line);
		                            lineNumberMap.put(cppFile, cppStartLine, cppEndLine, file, line, column);
		                            if (addLastLine)
		                        		lineNumberMap.put(cppFile, cppEndLine, cppEndLine, file, lastX10Line, column);
		                        }
		                        else if (!isBlock)
		                        {
		                        	lineNumberMap.put(cppFile, cppStartLine, cppEndLine, file, line, column);
		                        	if (addLastLine)
		                        		lineNumberMap.put(cppFile, cppEndLine, cppEndLine, file, lastX10Line, column);
		                        }
		                    }
		                });
		            }
		            if (parent instanceof For && n instanceof LocalDecl)
		            {
		            	Term t = ((For)parent).body().firstChild();
		            	if (t instanceof LocalDecl)
		            		lineNumberMap.rememberLoopVariable(((LocalDecl)t).name().toString(), ((LocalDecl)n).name().toString(), line, lastX10Line);
		            }
		            if (n instanceof FieldDecl && !c.inTemplate() && !((FieldDecl)n).flags().flags().isStatic() && !n.position().isCompilerGenerated()) // the c.inTemplate() skips mappings for templates, which don't have a fixed size.
		            	lineNumberMap.addClassMemberVariable(((FieldDecl)n).name().toString(), ((FieldDecl)n).type().toString(), Emitter.mangled_non_method_name(context.currentClass().toString()), context.currentClass().isX10Struct(), false, false);
		            else if (n instanceof LocalDecl && !((LocalDecl)n).position().isCompilerGenerated())
		            {
		            	X10ClassType t = ((LocalDecl)n).type().type().toClass();
		            	lineNumberMap.addLocalVariableMapping(((LocalDecl)n).name().toString(), ((LocalDecl)n).type().toString(), line, lastX10Line, file, false, -1, (t==null?false:t.isX10Struct()));
		            }
		            else if (def != null)
		            {
		            	// include method arguments in the local variable tables
		            	ProcedureDecl defSource;
		            	if (n instanceof ConstructorDecl)
		            	{
		            		defSource = (ProcedureDecl)n;
		            		
		            		// add the hack reference to the outer class
		            		if (n instanceof X10ConstructorDecl)
		            		{
		            			X10ConstructorDecl cd = (X10ConstructorDecl)n;
		            			String thisClass = cd.returnType().toString();
		            			if (cd.returnType().toString().contains("$"))
			            		{
			            			String parentClass = thisClass.substring(0, thisClass.lastIndexOf('$'));
			            			List<Formal> args = defSource.formals();
					            	if (args.size() == 1)
					            	{
					            		Formal arg = args.get(0);
					            		if (arg.type().toString().equals(parentClass))
					            		{
					            			X10ClassType t = arg.type().type().toClass();
					            			lineNumberMap.addClassMemberVariable(arg.name().toString(), parentClass, Emitter.mangled_non_method_name(thisClass), (t==null?false:t.isX10Struct()), true, false);
					            		}
					            	}
			            		}
		            			if (cd.body().statements().size() > 0)
		            			{
			            			Stmt s = cd.body().statements().get(0);
			            			if (s instanceof X10ConstructorCall && ((X10ConstructorCall)s).kind().equals(Kind.SUPER))
			            			{
			            				String superClass = ((X10ConstructorCall)s).constructorInstance().returnType().toString();
			            				lineNumberMap.addClassMemberVariable(superClass, superClass, Emitter.mangled_non_method_name(thisClass), ((X10ConstructorCall)s).constructorInstance().returnType().toClass().isX10Struct(), false, true);
			            			}
		            			}
		            		}
		            	}
		            	else
		            		defSource = (ProcedureDecl)parent;
		            	List<Formal> args = defSource.formals();
		            	for (int i=0; i<args.size(); i++)
		            	{
		            		Formal arg = args.get(i);
		            		if (!arg.position().isCompilerGenerated())
		            		{
		            		    X10ClassType t = arg.type().type().toClass();
		            			lineNumberMap.addLocalVariableMapping(arg.name().toString(), arg.type().toString(), line, lastX10Line, file, false, -1, (t==null?false:t.isX10Struct()));
		            		}
		            	}
		            	// include "this" for non-static methods
		            	if (!def.flags().isStatic() && defSource.reachable() != null && defSource.reachable() && !c.inTemplate())
		            	{
		            		boolean isStruct = context.currentClass().isX10Struct();
		            		if (!defSource.position().isCompilerGenerated())
		            			lineNumberMap.addLocalVariableMapping("this", Emitter.mangled_non_method_name(context.currentClass().toString()), line, lastX10Line, file, true, -1, isStruct);
		            		lineNumberMap.addClassMemberVariable(null, null, Emitter.mangled_non_method_name(context.currentClass().toString()), isStruct, false, false);
		            	}
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

    private static void maybeCopyTo (String file, String src_path_, String dest_path_, Compiler compiler, boolean noPostCompiler) {
		File src_path = new File(src_path_);
    	File dest_path = new File(dest_path_);
    	// don't copy if the two dirs are the same...
    	if (src_path.equals(dest_path)) return;
        // don't copy if the file path is absolute...
    	if (new File(file).isAbsolute()) return;
    	// TODO: fix issue with NativeCPPInclude
        // TODO: disambiguate #include <header.h> vs #include "header.h"
        // TODO: system header with relative path should not be copied
    	if (!dest_path.exists()) dest_path.mkdir();
    	assert src_path.isDirectory() : src_path_+" is not a directory";
    	assert dest_path.isDirectory() : dest_path_+" is not a directory";
    	try {
    		dest_path.mkdirs();
			FileInputStream src = new FileInputStream(new File(src_path_+file));
	    	FileOutputStream dest = new FileOutputStream(new File(dest_path_+file));
	    	int b;
	    	while ((b = src.read()) != -1) {
	    		dest.write(b);
	    	}
    	} catch (IOException e) {
    	    if (!noPostCompiler) {
    	        compiler.errorQueue().enqueue(ErrorInfo.WARNING, "Failed to copy "+file + " from "+src_path_+" to "+dest_path_);
    	    }
    	}
    }

	/* (non-Javadoc)
	 * @see polyglot.visit.Translator#translateSource(polyglot.ast.SourceFile)
	 */
	protected boolean translateSource(SourceFile sfn) {

		int outputWidth = job.compiler().outputWidth();

		try {

			String pkg = null;
			if (sfn.package_() != null) {
				Package p = sfn.package_().package_().get();
				pkg = p.fullName().toString();
			}

			X10CPPContext_c c = (X10CPPContext_c) context;
			X10CPPCompilerOptions opts = (X10CPPCompilerOptions) job.extensionInfo().getOptions();
			TypeSystem xts = typeSystem();

			if (opts.x10_config.DEBUG && opts.x10_config.DEBUG_ENABLE_LINEMAPS)
				c.addData(FILE_TO_LINE_NUMBER_MAP, CollectionFactory.newHashMap());

			// Use the source file name as the basename for the output .cc file
			String fname = sfn.source().name();
			fname = fname.substring(0, fname.lastIndexOf(".x10"));
			boolean generatedCode = false;
            WriterStreams fstreams = new WriterStreams(fname, pkg, job, tf);

            if (opts.x10_config.DEBUG && opts.x10_config.DEBUG_ENABLE_LINEMAPS) {
                Map<String, LineNumberMap> fileToLineNumberMap =
                    c.<Map<String, LineNumberMap>>getData(FILE_TO_LINE_NUMBER_MAP);
                fileToLineNumberMap.put(fstreams.getStreamName(StreamWrapper.CC), new LineNumberMap());
            }
    
			for (TopLevelDecl decl : sfn.decls()) {
				if (!(decl instanceof X10ClassDecl))
					continue;
				X10ClassDecl cd = (X10ClassDecl) decl;
				// Skip output of all files for a native rep class.
		        X10Ext ext = (X10Ext) cd.ext();
		        try {
		            String path = new File(cd.position().file()).getParent();
		            if (path==null) path = ""; else path = path + '/';
		            String out_path = opts.output_directory.toString();
		            if (out_path==null) out_path = ""; else out_path = out_path + '/';
		            String pkg_ = packagePath(pkg);
		            List<X10ClassType> as = ext.annotationMatching(xts.systemResolver().findOne(QName.make("x10.compiler.NativeCPPInclude")));
		            for (Type at : as) {
		                ASTQuery.assertNumberOfInitializers(at, 1);
		                String include = getStringPropertyInit(at, 0);
		                job.compiler().addOutputFile(sfn, pkg_+include);
		                maybeCopyTo(include, path, out_path+pkg_, job.compiler(), opts.post_compiler == null);
		            }
		            as = ext.annotationMatching(xts.systemResolver().findOne(QName.make("x10.compiler.NativeCPPOutputFile")));
		            for (Type at : as) {
		                ASTQuery.assertNumberOfInitializers(at, 1);
		                String file = getStringPropertyInit(at, 0);
		                job.compiler().addOutputFile(sfn, pkg_+file);
		                maybeCopyTo(file, path, out_path+pkg_, job.compiler(), opts.post_compiler == null);
		            }
		            as = ext.annotationMatching(xts.systemResolver().findOne(QName.make("x10.compiler.NativeCPPCompilationUnit")));
		            for (Type at : as) {
		                ASTQuery.assertNumberOfInitializers(at, 1);
		                String compilation_unit = getStringPropertyInit(at, 0);
		                job.compiler().addOutputFile(sfn, pkg_+compilation_unit);
		                opts.compilationUnits().add(pkg_+compilation_unit);
		                maybeCopyTo(compilation_unit, path, out_path+pkg_, job.compiler(), opts.post_compiler == null);
		            }
		        } catch (SemanticException e) {
		            assert false : e;
		        }

	        	if (getCppRep((X10ClassDef)cd.classDef()) != null) {
					continue;
				}
	      
                generatedCode = true;
                
	        	// Use the name of the class to get the name of the header files.
				String className = cd.classDef().name().toString();
				WriterStreams wstreams = new WriterStreams(className, pkg, job, tf);
				StreamWrapper sw = new StreamWrapper(fstreams, wstreams, outputWidth);
				// [DC] TODO: This hack is to ensure the .h is always generated.
                sw.getNewStream(StreamWrapper.Header, true);
				String header = wstreams.getStreamName(StreamWrapper.Header);
				job.compiler().addOutputFile(sfn, header);
				
				if (opts.x10_config.DEBUG && opts.x10_config.DEBUG_ENABLE_LINEMAPS) {
					Map<String, LineNumberMap> fileToLineNumberMap =
					    c.<Map<String, LineNumberMap>>getData(FILE_TO_LINE_NUMBER_MAP);
					fileToLineNumberMap.put(header, new LineNumberMap());
				}
				
                ClassifiedStream tmp = sw.getNewStream(StreamWrapper.CC, false);
				tmp.writeln("/*************************************************/");
                tmp.writeln("/* START of "+className+" */");
                
				translateTopLevelDecl(sw, sfn, decl);

                ClassifiedStream tmp2 = sw.getNewStream(StreamWrapper.CC, false);
                tmp2.writeln("/* END of "+className+" */");
                tmp2.writeln("/*************************************************/");
				
				wstreams.commitStreams();
			}
			
			if (generatedCode) {
			    String cc = fstreams.getStreamName(StreamWrapper.CC);
			    job.compiler().addOutputFile(sfn, cc);
                opts.compilationUnits().add(cc);
                
                if (opts.x10_config.DEBUG && opts.x10_config.DEBUG_ENABLE_LINEMAPS) {
                    Map<String, LineNumberMap> fileToLineNumberMap =
                        c.<Map<String, LineNumberMap>>getData(FILE_TO_LINE_NUMBER_MAP);
                    ClassifiedStream debugStream = fstreams.getNewStream(StreamWrapper.CC, false);
                    printLineNumberMapForCPPDebugger(debugStream, fstreams.getStreamName(StreamWrapper.CC), fileToLineNumberMap);
                }
               
                fstreams.commitStreams();
			}			    

			return true;
		}
		catch (IOException e) {
			job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR,
					"I/O error while translating: " + e.getMessage());
			return false;
		}
	}

	private void printLineNumberMapForCPPDebugger(ClassifiedStream stream, String streamName, Map<String, LineNumberMap> fileToLineNumberMap) {
	    final LineNumberMap map = fileToLineNumberMap.get(streamName);
	    stream.registerCommitListener(new ClassifiedStream.CommitListener() {
	        public void run(ClassifiedStream s) {
//	            if (map.isEmpty())
//	                return;
	            s.forceNewline();
	            map.exportForCPPDebugger(s, map);
	        }
	    });
	}

	private void printLineNumberMap(StreamWrapper sw, String pkg, String className, final String ext, Map<String, LineNumberMap> fileToLineNumberMap) {
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


	public static final String postcompile = "postcompile";

	/**
	 * The post-compiler option has the following structure:
	 * "[pre-command with options (usually g++)] [(#|%) [post-options (usually extra files)] [(#|%) [library options]]]".
	 * Using '%' instead of '#' to delimit a section will cause the default values in that section to be omitted.
	 */
	public static boolean postCompile(X10CPPCompilerOptions options, Compiler compiler, ErrorQueue eq) {
		if (eq.hasErrors())
			return false;

		if (options.post_compiler != null && !options.output_stdout) {
			// use set to avoid duplicates
			Set<String> compilationUnits = CollectionFactory.newHashSet(options.compilationUnits());

			if (options.buildX10Lib == null) {

				try {
				    final File file = outputFile(options, null, options.x10cpp_config.MAIN_STUB_NAME, "cc");
				    ExtensionInfo ext = compiler.sourceExtension();
				    SimpleCodeWriter sw = new SimpleCodeWriter(ext.targetFactory().outputWriter(file),
				            compiler.outputWidth());
				    List<MethodDef> mainMethods = new ArrayList<MethodDef>();
				    for (Job job : ext.scheduler().commandLineJobs()) {
				        mainMethods.addAll(getMainMethods(job));
				    }
				    if (mainMethods.size() < 1) {
				        // If there are no main() methods in the command-line jobs, try other files
				        for (Job job : ext.scheduler().jobs()) {
				            mainMethods.addAll(getMainMethods(job));
				        }
				    }
				    if (mainMethods.size() < 1) {
				        eq.enqueue(ErrorInfo.SEMANTIC_ERROR, "No main method found");
				        return false;
				    } else if (mainMethods.size() > 1) {
				        eq.enqueue(ErrorInfo.SEMANTIC_ERROR,
				                "Multiple main() methods found, please specify MAIN_CLASS:"+listMethods(mainMethods));
				        return false;
				    }
				    assert (mainMethods.size() == 1);
				    X10ClassType container = (X10ClassType) Types.get(mainMethods.get(0).container());
				    MessagePassingCodeGenerator.processMain(container, sw, options);
				    sw.flush();
				    sw.close();
				    compilationUnits.add(file.getName());
				}
				catch (IOException e) {
				    eq.enqueue(ErrorInfo.IO_ERROR, "I/O error while translating: " + e.getMessage());
				    return false;
				}
			}
			
			PostCompileProperties x10rt = loadX10RTProperties(options);
		    SharedLibProperties shared_lib_props = loadSharedLibProperties();
			CXXCommandBuilder ccb = CXXCommandBuilder.getCXXCommandBuilder(options, x10rt, shared_lib_props, eq);
			String[] cxxCmd = ccb.buildCXXCommandLine(compilationUnits);

			if (!doPostCompile(options, eq, compilationUnits, cxxCmd)) return false;
			
			if (options.buildX10Lib != null) {
			    if (shared_lib_props.staticLib) {
			        ArrayList<String> arCmd = new ArrayList<String>();
			        arCmd.add(shared_lib_props.ar);
			        arCmd.add(shared_lib_props.arFlags);
			        arCmd.add(ccb.targetFilePath().getPath());
			        
			        ArrayList<String> objFiles = new ArrayList<String>();
			        for (String file : compiler.flatOutputFiles()) {
			            int lastPeriod = file.lastIndexOf('.');
			            if (-1 == lastPeriod) continue;
			            String suffix = file.substring(lastPeriod+1, file.length());
			            if (suffix.equals("cc") || suffix.equals("c") || suffix.equals("cpp") || suffix.equals("cxx")) {
                            objFiles.add(file.substring(file.lastIndexOf(File.separatorChar)+1, lastPeriod)+".o");   
			            }
			        }
			        if (!objFiles.isEmpty()) {
			            arCmd.addAll(objFiles);
			            boolean savedKeepOutputFiles = options.keep_output_files;
			            options.keep_output_files = false;
			            try {
			                if (!doPostCompile(options, eq, objFiles, arCmd.toArray(new String[arCmd.size()]))) return false;
			            } finally {
			                options.keep_output_files = savedKeepOutputFiles;
			            }
			        }
			    }
			    
			    
				if (!emitPropertiesFile(options, ccb)) return false;
			}

			// FIXME: [IP] HACK: Prevent the java post-compiler from running
			options.post_compiler = null;
		}
		return true;
	}
	
	private static String quoteSeparated (List<String> list)
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String s : list) {
			sb.append((first?"":" ")+"\""+s+"\"");
			first = false;
		}
		return sb.toString();
	}
	
	public static boolean emitPropertiesFile(X10CPPCompilerOptions options, CXXCommandBuilder ccb) {
		try {
			File f = new File(options.buildX10Lib + "/"+options.executable_path+".properties");
			PrintStream dest = new PrintStream(new FileOutputStream(f));
	    	dest.println("X10LIB_PLATFORM="+ccb.getPlatform());
	    	dest.println("X10LIB_TIMESTAMP="+"UNSUPPORTED");
			dest.println("X10LIB_CXX="+ccb.getPostCompiler());
	    	dest.println("X10LIB_CXXFLAGS="+quoteSeparated(options.extraPreArgs));
			dest.println("X10LIB_LDFLAGS=");
	    	dest.println("X10LIB_LDLIBS=-l"+options.executable_path+" "+quoteSeparated(options.extraPostArgs));
	    	dest.println("X10LIB_SRC_JAR="+options.executable_path+".jar");
	    	dest.close();
	    	return true;
		} catch (IOException e) {
			System.out.println(e);
			return false;
		}
	}

	public static PostCompileProperties loadX10RTProperties(X10CPPCompilerOptions options) {
	    // TODO: get options.distPath external to this method
	    String dp = System.getProperty("x10.dist");
	    options.setDistPath(dp);

	    // TODO: get properties file external to this method and pass it as an argument
	    String rtimpl = System.getenv("X10RT_IMPL");
	    
	    // allow the user to give an explicit path, otherwise look in etc
	    if (!rtimpl.endsWith(".properties")) {
	        rtimpl = dp + "/etc/x10rt_"+rtimpl+".properties";
	    }
	    Properties x10rt = loadPropertyFile(rtimpl);
	    PostCompileProperties x10rt_props = new PostCompileProperties(x10rt);
	    return x10rt_props;
	}
	
	public static SharedLibProperties loadSharedLibProperties() {
	    String dp = System.getProperty("x10.dist");
	    return new SharedLibProperties(loadPropertyFile(dp+"/etc/sharedlib.properties"));
	}
	
    private static Properties loadPropertyFile(String filename) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filename));
        } catch(IOException e) {
            throw new InternalCompilerError("Unable to load property file "+filename+" "+ e.getMessage(), e);
        }
        return properties;
    }
	
	private static List<MethodDef> getMainMethods(Job job) {
	    X10CPPCompilerOptions opts = (X10CPPCompilerOptions) job.extensionInfo().getOptions();
	    X10CPPJobExt jobext = (X10CPPJobExt) job.ext();
	    if (opts.x10_config.MAIN_CLASS != null) {
	        if (opts.x10_config.MAIN_CLASS.length() == 0) {
	            return Collections.<MethodDef>emptyList();
	        }
	        QName mainClass = QName.make(opts.x10_config.MAIN_CLASS);
	        try {
	            ClassType mct = (ClassType) job.extensionInfo().typeSystem().forName(mainClass);
	            QName pkgName = mct.package_() == null ? null : mct.package_().fullName();
	            mainClass = QName.make(pkgName, StaticNestedClassRemover.mangleName(mct.def()));
	        } catch (SemanticException e) { }
	        for (MethodDef md : jobext.mainMethods()) {
	            QName containerName = ((X10ClassType) Types.get(md.container())).fullName();
	            if (containerName.equals(mainClass)) {
	                return Collections.singletonList(md);
	            }
	        }
	        return Collections.<MethodDef>emptyList();
	    } else {
	        return jobext.mainMethods();
	    }
	}

	private static String listMethods(List<MethodDef> mainMethods) {
	    StringBuilder sb = new StringBuilder();
	    for (MethodDef md : mainMethods) {
            sb.append("\n\t").append(md.toString());
        }
	    return sb.toString();
	}

    public static boolean doPostCompile(Options options, ErrorQueue eq, Collection<String> outputFiles, String[] cxxCmd) {
        Reporter reporter = options.reporter;
        if (reporter.should_report(postcompile, 1)) {
        	StringBuffer cmdStr = new StringBuffer();
        	for (int i = 0; i < cxxCmd.length; i++)
        		cmdStr.append(cxxCmd[i]+" ");
        	reporter.report(1, "Executing post-compiler " + cmdStr);
        }

        try {
            Runtime runtime = Runtime.getRuntime();
        	Process proc = runtime.exec(cxxCmd, null, options.output_directory);

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

        	if (!options.keep_output_files && outputFiles != null) {
        		String[] rmCmd = new String[1+outputFiles.size()];
        		rmCmd[0] = "rm";
        		Iterator<String> iter = outputFiles.iterator();
        		for (int i = 1; iter.hasNext(); i++)
        			rmCmd[i] = iter.next();
        		runtime.exec(rmCmd, null, options.output_directory);
        	}

        	if (output != null) {
        		eq.enqueue((proc.exitValue() > 0) ? ErrorInfo.POST_COMPILER_ERROR : ErrorInfo.WARNING, output);
        	}
        	if (proc.exitValue() > 0) {
        		eq.enqueue(ErrorInfo.POST_COMPILER_ERROR,"Non-zero return code: " + proc.exitValue());
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

		for (SourceFile sfn : sc.sources()) {
			okay &= translateSource(sfn);
		}

		if (true)
			throw new InternalCompilerError("Don't yet know how to translate source collections");
		return okay;
	}
}
