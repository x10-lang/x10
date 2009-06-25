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
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.visit.AssignPropertyChecker;
import polyglot.ext.x10.visit.CastRewriter;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.ext.x10.visit.X10Boxer;
import polyglot.ext.x10.visit.X10Caster;
import polyglot.ext.x10.visit.X10ImplicitDeclarationExpander;
import polyglot.ext.x10.visit.X10MLVerifier;
import polyglot.ext.x10.visit.X10Translator;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.frontend.FragmentGoal;
import polyglot.frontend.Goal;
import polyglot.frontend.GoalSet;
import polyglot.frontend.JLScheduler;
import polyglot.frontend.Job;
import polyglot.frontend.OutputPass;
import polyglot.frontend.Parser;
import polyglot.frontend.Pass;
import polyglot.frontend.RuleBasedGoalSet;
import polyglot.frontend.Scheduler;
import polyglot.frontend.SourceGoal_c;
import polyglot.frontend.VisitorGoal;
import polyglot.frontend.VisitorPass;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.visit.PruningVisitor;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;
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
    
    protected Map<String,CompilerPlugin> plugins;

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
//           goals.add(TypesInitializedForCommandLine());
           goals.add(ImportTableInitialized(job));

           if (job.source() != null && job.source().path().endsWith(XML_FILE_DOT_EXTENSION)) {
               goals.add(X10MLTypeChecked(job));
           }

           // Do not include LoadPlugins in list.  It would cause prereqs to be added 
//           goals.add(LoadPlugins());
           goals.add(PropagateAnnotations(job));
           goals.add(LoadJobPlugins(job));
           goals.add(RegisterPlugins(job));
           
           goals.add(FragmentAST(job));
           goals.add(TypeChecked(job));
           goals.add(ReassembleAST(job));

           
           goals.add(X10Boxed(job));
           goals.add(X10Casted(job));
           goals.add(X10ExprFlattened(job));
           
           goals.add(ReachabilityChecked(job));
           goals.add(ExceptionsChecked(job));
           goals.add(ExitPathsChecked(job));
           goals.add(InitializationsChecked(job));
           goals.add(ConstructorCallsChecked(job));
           goals.add(ForwardReferencesChecked(job));
           
           goals.add(PropertyAssignmentsChecked(job));
           goals.add(X10Expanded(job));

           goals.add(Serialized(job));
           goals.add(CodeGenerated(job));
           goals.add(End(job));
           
           return goals;
       }
       public Goal LoadPlugins() {
           return new LoadPlugins().intern(this);
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
       
       public Goal CodeGenerated(Job job) {
           return new SourceGoal_c("CodeGenerated", job) {
               public Pass createPass() {
                   TypeSystem ts = extInfo.typeSystem();
                   NodeFactory nf = extInfo.nodeFactory();
                   return new OutputPass(this, job, new X10Translator(job, ts, nf, extInfo.targetFactory()));
               }
           }.intern(this);
       }

       public Goal ClassInvariantDef(Job job, ClassDef def) {
           return new ClassInvariantDef("ClassInvariantDef", job, def).intern(this);
       }
       
       public static class ClassInvariantDef extends FragmentGoal {
           public ClassInvariantDef(String name, Job job, ClassDef def) {
               super(name, job, def, new TypeChecker(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory(), def));
           }

           @Override
           public GoalSet createRequiredView() {
               return new RuleBasedGoalSet() {
                   public boolean contains(Goal g) {
                       return ClassInvariantDef.super.defaultRequiredView().contains(g) ||
                       g instanceof LookupGlobalType ||
                       g instanceof LookupGlobalTypeDefAndSetFlags ||
                       g instanceof FieldConstantsChecked;
                   }

                   public String toString() { return "DefGoalRuleSet(" + ClassInvariantDef.this + ")"; }
               };
           }
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
    
    public Map<String,CompilerPlugin> plugins() {
    	if (plugins == null) {
    		return Collections.emptyMap();
    	}
    	return plugins;
    }
    
	public void addPlugin(String pluginName, CompilerPlugin plugin) {
		if (plugins == null) {
			plugins = new HashMap<String,CompilerPlugin>();
		}
		plugins.put(pluginName, plugin);
	}
	
	public CompilerPlugin getPlugin(String pluginName) {
		if (plugins == null) {
			return null;
		}
		
		return plugins.get(pluginName);
	}
}
