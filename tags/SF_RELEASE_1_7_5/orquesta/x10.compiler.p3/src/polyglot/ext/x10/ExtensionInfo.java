// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004,2005,2006. All Rights Reserved. 
// Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  Schedule Contract with IBM Corp. 
//                                                                             
// --------------------------------------------------------------------------- 


package polyglot.ext.x10;


import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.plugin.CompilerPlugin;
import polyglot.ext.x10.plugin.LoadJobPlugins;
import polyglot.ext.x10.plugin.LoadPlugins;
import polyglot.ext.x10.plugin.RegisterPlugins;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10.types.X10SourceClassResolver;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.visit.AssignPropertyChecker;
import polyglot.ext.x10.visit.CastRewriter;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.ext.x10.visit.X10Boxer;
import polyglot.ext.x10.visit.X10Caster;
import polyglot.ext.x10.visit.X10ImplicitDeclarationExpander;
import polyglot.ext.x10.visit.X10InitChecker;
import polyglot.ext.x10.visit.X10MLVerifier;
import polyglot.ext.x10.visit.X10Translator;
import polyglot.frontend.AllBarrierGoal;
import polyglot.frontend.BarrierGoal;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.frontend.Goal;
import polyglot.frontend.JLScheduler;
import polyglot.frontend.Job;
import polyglot.frontend.OutputGoal;
import polyglot.frontend.Parser;
import polyglot.frontend.Scheduler;
import polyglot.frontend.VisitorGoal;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.types.LoadedClassResolver;
import polyglot.types.MemberClassResolver;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.TopLevelResolver;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.visit.PruningVisitor;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;

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
            X10Lexer x10_lexer = new X10Lexer(source.path());
            X10Parser x10_parser = new X10Parser(x10_lexer, ts, nf, source, eq); // Create the parser
            x10_lexer.lexer(x10_parser);
            return x10_parser; // Parse the token stream to produce an AST
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not parse " + source.path());
    }

    protected void initTypeSystem() {
	        try {
	            LoadedClassResolver lr;
	            lr = new X10SourceClassResolver(compiler, this, getOptions().constructFullClasspath(),
	                                         compiler.loader(), true,
	                                         getOptions().compile_command_line_only,
	                                         getOptions().ignore_mod_times);

	            TopLevelResolver r = lr;

	            // Resolver to handle lookups of member classes.
	            if (true || TypeSystem.SERIALIZE_MEMBERS_WITH_CONTAINER) {
	                MemberClassResolver mcr = new MemberClassResolver(ts, lr, true);
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
       X10Scheduler(ExtensionInfo extInfo) {
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
           
           goals.add(ConformanceChecked(job));
           goals.add(X10Boxed(job));
           goals.add(X10Casted(job));
           
           goals.add(ReachabilityChecked(job));
           goals.add(ExceptionsChecked(job));
           goals.add(ExitPathsChecked(job));
           goals.add(InitializationsChecked(job));
           goals.add(ConstructorCallsChecked(job));
           goals.add(ForwardReferencesChecked(job));
           
           goals.add(PropertyAssignmentsChecked(job));
           goals.add(X10Expanded(job));

           goals.add(Serialized(job));
           goals.add(CodeGenBarrier());
           goals.add(CodeGenerated(job));
           goals.add(End(job));
           
           return goals;
       }
       
       public Goal CodeGenBarrier() {
           return new AllBarrierGoal("CodeGenBarrier", this) {
               @Override
               public Goal prereqForJob(Job job) {
                   return Serialized(job);
               }
           };
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
    	   return new OutputGoal(job, new X10Translator(job, ts, nf, extInfo.targetFactory()));
       }

       public Goal X10MLTypeChecked(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("X10MLTypeChecked", job, new X10MLVerifier(job, ts, nf)).intern(this);
       }
       
       public Goal X10Boxed(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("X10Boxed", job, new X10Boxer(job, ts, nf)).intern(this);
       }
       
       public Goal X10Casted(Job job) {
           TypeSystem ts = extInfo.typeSystem();
           NodeFactory nf = extInfo.nodeFactory();
           return new VisitorGoal("X10Casted", job, new X10Caster(job, ts, nf)).intern(this);
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
