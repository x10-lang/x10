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

package x10;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import polyglot.ast.NodeFactory;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.AllBarrierGoal;
import polyglot.frontend.BarrierGoal;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileResource;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.JLScheduler;
import polyglot.frontend.Job;
import polyglot.frontend.OutputGoal;
import polyglot.frontend.Parser;
import polyglot.frontend.Scheduler;
import polyglot.frontend.SourceGoal_c;
import polyglot.frontend.TargetFactory;
import polyglot.frontend.VisitorGoal;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.types.MemberClassResolver;
import polyglot.types.MethodDef;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.TopLevelResolver;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.PruningVisitor;
import polyglot.visit.TypeChecker;
import x10.ast.X10NodeFactory_c;
import x10.optimizations.Optimizer;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;
import x10.plugin.CompilerPlugin;
import x10.plugin.LoadJobPlugins;
import x10.plugin.LoadPlugins;
import x10.plugin.RegisterPlugins;
import x10.query.QueryEngine;
import x10.types.X10SourceClassResolver;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.visit.AssignPropertyChecker;
import x10.visit.CastRewriter;
import x10.visit.CheckNativeAnnotationsVisitor;
import x10.visit.Desugarer;
import x10.visit.ExprFlattener;
import x10.visit.FieldInitializerMover;
import x10.visit.Inliner;
import x10.visit.NativeClassVisitor;
import x10.visit.RewriteAtomicMethodVisitor;
import x10.visit.RewriteExternVisitor;
import x10.visit.StaticNestedClassRemover;
import x10.visit.WSCodeGenerator;
import x10.visit.X10Caster;
import x10.visit.X10ImplicitDeclarationExpander;
import x10.visit.X10InitChecker;
import x10.visit.X10InnerClassRemover;
import x10.visit.X10MLVerifier;
import x10.visit.X10Translator;
import x10.visit.X10TypeChecker;

/**
 * Extension information for x10 extension.
 */
public class ExtensionInfo extends polyglot.frontend.ParserlessJLExtensionInfo {
    static final boolean DEBUG_ = false;
    static {
        // force Topics to load
        Topics t = new Topics();
    }
    
    protected Map<QName,CompilerPlugin> plugins;

	public static final String XML_FILE_EXTENSION = "x10ml";
	public static final String XML_FILE_DOT_EXTENSION = "." + XML_FILE_EXTENSION;
  
    public static String clock = "clock";

    static {
        Report.topics.add(clock);
    }

    public polyglot.main.Version version() {
    	return new Version();
    }
    public String[] fileExtensions() {
    	return new String[] { "x10", XML_FILE_EXTENSION };
    }
//    public String defaultFileExtension() {
//        return "x10";
//    }

    public String compilerName() {
        return "x10c";
    }

    //
    // Replace the Flex/Cup parser with a JikesPG parser
    //
    //    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) { 
    //        Lexer lexer = new Lexer_c(reader, source.name(), eq);
    //        Grm grm = new Grm(lexer, ts, nf, eq);
    //        return new CupParser(grm, source, eq);
    //    }
    //
    
    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
    // ###
//        if (source.path().endsWith(XML_FILE_DOT_EXTENSION)) {
//        	return new DomParser(reader, (X10TypeSystem) ts, (X10NodeFactory) nf, source, eq);
//        }

    	try {
            //
            // X10Lexer may be invoked using one of two constructors.
            // One constructor takes as argument a string representing
            // a (fully-qualified) filename; the other constructor takes
            // as arguments a (file) Reader and a string representing the
            // name of the file in question. Invoking the first
            // constructor is more efficient because a buffered File is created
            // from that string and read with one (read) operation. However,
            // we depend on Polyglot to provide us with a fully qualified
            // name for the file. In Version 1.3.0, source.name() yielded a
            // fully-qualied name. In 1.3.2, source.path() yields a fully-
            // qualified name. If this assumption still holds then the 
            // first constructor will work.
            // The advantage of using the Reader constructor is that it
            // will always work, though not as efficiently.
            //
            // X10Lexer x10_lexer = new X10Lexer(reader, source.name());
            //
            // FIXME: HACK! Unwrap the escaping unicode reader, because LPG will do its own decoding
//          if (reader instanceof polyglot.lex.EscapedUnicodeReader)
//              reader = ((polyglot.lex.EscapedUnicodeReader)reader).getSource();
            X10Lexer x10_lexer =
                // Optimization: it's faster to read from a file
                source instanceof FileSource && ((FileSource) source).resource().getClass() == FileResource.class ?
                                new X10Lexer(source.path()) :
                                new X10Lexer(reader, source.toString());
            X10Parser x10_parser = new X10Parser(x10_lexer.getILexStream(), ts, nf, source, eq); // Create the parser
            x10_lexer.lexer(x10_parser.getIPrsStream());
            return x10_parser; // Parse the token stream to produce an AST
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not parse " + source.path());
    }

    protected HashSet<String> manifest = new HashSet<String>();
    public boolean manifestContains(String path) {
        path = path.replace(File.separatorChar, '/');
        // FIXME: HACK! Try all prefixes
        int s = 0;
        while ((s = path.indexOf('/')) != -1) {
            if (manifest.contains(path))
                return true;
            path = path.substring(s+1);
        }
        if (manifest.contains(path))
            return true;
        return false;
    }
    
    static class ExceptionComparator implements Comparator<SemanticException> {
    	public int compare(SemanticException a, SemanticException b) {
    		int r = (a.getClass().toString().compareToIgnoreCase(b.getClass().toString()));
    		if (r != 0)
    			return r;
    		Position pa  = a.position();
    		if (pa == null)
    			return -1;
    		Position pb = b.position();
    		if (pb==null)
    			return 1;
    		if (pa.line() < pb.line())
    			return -1;
    		if (pb.line() < pa.line())
    			return 1;
    		if (pa.column() < pb.column())
    			return -1;
    		if (pb.line() < pa.line())
    			return 1;
    		return 0;
    	}
    }
    Set<SemanticException> errors = new TreeSet<SemanticException>(new ExceptionComparator());
    
    public Set<SemanticException> errorSet() {
    	return errors;
    }
    
    protected void initTypeSystem() {
        try {
            TopLevelResolver r = new X10SourceClassResolver(compiler, this, getOptions().constructFullClasspath(),
                                                            getOptions().compile_command_line_only,
                                                            getOptions().ignore_mod_times);
            // FIXME: [IP] HACK
            if (Configuration.MANIFEST != null) {
                try {
                    FileReader fr = new FileReader(Configuration.MANIFEST);
                    BufferedReader br = new BufferedReader(fr);
                    String file = "";
                    while ((file = br.readLine()) != null)
                        if (file.endsWith(".x10") || file.endsWith(".jar")) // FIXME: hard-codes the source extension.
                            manifest.add(file);
                } catch (IOException e) { }
            } else {
                for (File f : getOptions().source_path) {
                    if (f.getName().endsWith("x10.jar")) {
                        try {
                            JarFile jf = new JarFile(f);
                            manifest.add("x10.jar");
                            Enumeration<JarEntry> entries = jf.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry je = entries.nextElement();
                                if (je.getName().endsWith(".x10")) { // FIXME: hard-codes the source extension.
                                    manifest.add(je.getName());
                                }
                            }
                        } catch (IOException e) { }
                    }
                }
            }
            // Resolver to handle lookups of member classes.
            if (true || TypeSystem.SERIALIZE_MEMBERS_WITH_CONTAINER) {
                MemberClassResolver mcr = new MemberClassResolver(ts, r, true);
                r = mcr;
            }

            ts.initialize(r, this);
        }
        catch (SemanticException e) {
            throw new InternalCompilerError(
                    "Unable to initialize type system: " + e.getMessage(), e);
        }
    }

    protected NodeFactory createNodeFactory() {
        return new X10NodeFactory_c(this);
    }

    protected TypeSystem createTypeSystem() {
        return new X10TypeSystem_c();
    }

    public void initCompiler(Compiler compiler) {
	super.initCompiler(compiler);
	QueryEngine.init(this);
    }

    // =================================
    // X10-specific goals and scheduling
    // =================================
    protected Scheduler createScheduler() {
        return new X10Scheduler(this);
    }
    
    public static class X10Scheduler extends JLScheduler {
       public X10Scheduler(ExtensionInfo extInfo) {
		   super(extInfo);
	   }

       public List<Goal> goals(Job job) {
           List<Goal> goals = new ArrayList<Goal>();

           goals.add(Parsed(job));
           goals.add(TypesInitialized(job));
           goals.add(ImportTableInitialized(job));

           if (job.source() != null && job.source().path().endsWith(XML_FILE_DOT_EXTENSION)) {
               goals.add(X10MLTypeChecked(job));
           }
           
           goals.add(CastRewritten(job));

           // Do not include LoadPlugins in list.  It would cause prereqs to be added 
//           goals.add(LoadPlugins());
           goals.add(PropagateAnnotations(job));
           goals.add(LoadJobPlugins(job));
           goals.add(RegisterPlugins(job));
           
           goals.add(PreTypeCheck(job));
           goals.add(TypesInitializedForCommandLine());
           goals.add(TypeChecked(job));
           goals.add(ReassembleAST(job));
           
        //   goals.add(X10Boxed(job));
           goals.add(X10Casted(job));
           goals.add(MoveFieldInitializers(job));
           goals.add(ConformanceChecked(job));

           goals.add(X10RewriteExtern(job));
           goals.add(X10RewriteAtomicMethods(job));
           
           // Data-flow analyses
           goals.add(ReachabilityChecked(job));
           goals.add(ExceptionsChecked(job));
           goals.add(ExitPathsChecked(job));
           goals.add(InitializationsChecked(job));
           goals.add(ConstructorCallsChecked(job));
           goals.add(ForwardReferencesChecked(job));
           goals.add(PropertyAssignmentsChecked(job));
           goals.add(X10Expanded(job));

           goals.add(NativeClassVisitor(job));

           goals.add(Serialized(job));
//           goals.add(CodeGenBarrier());
           goals.add(CheckNativeAnnotations(job));
           
           if (x10.Configuration.WORK_STEALING) {
               goals.add(WSCodeGenerator(job));
           }
           goals.add(InnerClassRemover(job));
           goals.addAll(Optimizer.goals(this, job));
           goals.add(Desugarer(job));
           goals.add(CodeGenerated(job));
           goals.add(End(job));
           
           // the barrier will handle prereqs on its own
           CodeGenerated(job).addPrereq(CodeGenBarrier());
           
           return goals;
       }
       
       public Goal CodeGenBarrier() {
    	   if (Globals.Options().compile_command_line_only) {
    		   return new BarrierGoal("CodeGenBarrier", commandLineJobs()) {
    			   @Override
    			   public Goal prereqForJob(Job job) {
    				   return Serialized(job);
    			   }
    		   };
    	   }
    	   else {
    		    return new AllBarrierGoal("CodeGenBarrier", this) {
    		    	@Override
    		    	public Goal prereqForJob(Job job) {
    		    	    if (!scheduler.commandLineJobs().contains(job) &&
    		    	            ((ExtensionInfo) extInfo).manifestContains(job.source().path()))
    		    	    {
    		    	        return null;
    		    	    }
    		    	    return Serialized(job);
    		    	}
    		    };
    	    }
       }
       
       public Goal Serialized(Job job) {
           Compiler compiler = job.extensionInfo().compiler();
           X10TypeSystem ts = (X10TypeSystem) job.extensionInfo().typeSystem();
           NodeFactory nf = job.extensionInfo().nodeFactory();
           TargetFactory tf = job.extensionInfo().targetFactory();
           return new SourceGoal_c("Serialized", job) {
                public boolean runTask() {
                   return true;
                }
           }.intern(this);
       }


//       @Override
//       public Goal ImportTableInitialized(Job job) {
//	   TypeSystem ts = job.extensionInfo().typeSystem();
//	   NodeFactory nf = job.extensionInfo().nodeFactory();
//	   Goal g = new VisitorGoal("ImportTableInitialized", job, new X10InitImportsVisitor(job, ts, nf));
//	   Goal g2 = g.intern(this);
//	   if (g == g2) {
//	       g.addPrereq(TypesInitializedForCommandLine());
//	   }
//	   return g2;
//       }

       public Goal LoadPlugins() {
           return new LoadPlugins((ExtensionInfo) extInfo).intern(this);
       }

       public Goal LoadJobPlugins(Job job) {
           return new LoadJobPlugins(job).intern(this);
       }

       public Goal RegisterPlugins(Job job) {
           Goal g = new RegisterPlugins(job);
           Goal g2 = g.intern(this);
           if (g == g2) {
               g.addPrereq(LoadPlugins());
           }
           return g2;
       }
       
       public Goal PropagateAnnotations(Job job) {
           // ###
           return new VisitorGoal("PropagateAnnotations", job, new PruningVisitor()).intern(this);
       }

       @Override
       public Goal InitializationsChecked(Job job) {
	   TypeSystem ts = job.extensionInfo().typeSystem();
	   NodeFactory nf = job.extensionInfo().nodeFactory();
	   return new VisitorGoal("InitializationsChecked", job, new X10InitChecker(job, ts, nf)).intern(this);
       }

       @Override
       public Goal CodeGenerated(Job job) {
    	   TypeSystem ts = extInfo.typeSystem();
    	   NodeFactory nf = extInfo.nodeFactory();
    	   return new OutputGoal(job, new X10Translator(job, ts, nf, extInfo.targetFactory())).intern(this);
       }

       public Goal X10MLTypeChecked(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("X10MLTypeChecked", job, new X10MLVerifier(job, ts, nf)).intern(this);
       }
      
       @Override
       public Goal TypeChecked(Job job) {
       	TypeSystem ts = job.extensionInfo().typeSystem();
       	NodeFactory nf = job.extensionInfo().nodeFactory();
       	return new VisitorGoal("TypeChecked", job, new X10TypeChecker(job, ts, nf, job.nodeMemo())).intern(this);
       }

       public Goal X10Casted(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("X10Casted", job, new X10Caster(job, ts, nf)).intern(this);
       }

       public Goal MoveFieldInitializers(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("MoveFieldInitializers", job, new FieldInitializerMover(job, ts, nf)).intern(this);
       }

       public Goal X10RewriteExtern(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("X10RewriteExtern", job, new RewriteExternVisitor(job, ts, nf)).intern(this);
       }
       public Goal X10RewriteAtomicMethods(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("X10RewriteAtomicMethods", job, new RewriteAtomicMethodVisitor(job, ts, nf)).intern(this);
       }
       
       public Goal X10ExprFlattened(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("X10ExprFlattened", job, new ExprFlattener(job, ts, nf)).intern(this);
       }

       // after disambiguation, before type elaboration
       public Goal CastRewritten(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("CastRewritten", job, new CastRewriter(job, ts, nf)).intern(this);
       }

       
       public Goal PropertyAssignmentsChecked(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("PropertyAssignmentsChecked", job, new AssignPropertyChecker(job, ts, nf)).intern(this);
       }
       
       public Goal X10Expanded(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("X10Expanded", job, new X10ImplicitDeclarationExpander(job, ts, nf)).intern(this);
       }
       
       public Goal CheckNativeAnnotations(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("CheckNativeAnnotations", job, new CheckNativeAnnotationsVisitor(job, ts, nf, "java")).intern(this);
       }
       
       public Goal NativeClassVisitor(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("NativeClassVisitor", job, new NativeClassVisitor(job, ts, nf, "java")).intern(this);
       }
       
       public Goal WSCodeGenerator(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("WSCodeGenerator", job, new WSCodeGenerator(job, ts, nf)).intern(this);
       }
       
       public Goal Desugarer(Job job) {
    	   TypeSystem ts = extInfo.typeSystem();
    	   NodeFactory nf = extInfo.nodeFactory();
    	   return new VisitorGoal("Desugarer", job, new Desugarer(job, ts, nf)).intern(this);
       }
       
       public Goal InnerClassRemover(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("InnerClassRemover", job, new X10InnerClassRemover(job, ts, nf)).intern(this);
       }
       public Goal StaticNestedClassRemover(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("StaticNestedClassRemover", job, new StaticNestedClassRemover(job, ts, nf)).intern(this);
       }
    }
    
    protected Options createOptions() {
    	return new X10CompilerOptions(this);
    }
    
    public Map<QName,CompilerPlugin> plugins() {
    	if (plugins == null) {
    		return Collections.emptyMap();
    	}
    	return plugins;
    }
    
	public void addPlugin(QName pluginName, CompilerPlugin plugin) {
		if (plugins == null) {
			plugins = new HashMap<QName,CompilerPlugin>();
		}
		plugins.put(pluginName, plugin);
	}
	
	public CompilerPlugin getPlugin(QName pluginName) {
		if (plugins == null) {
			return null;
		}
		
		return plugins.get(pluginName);
	}
}
