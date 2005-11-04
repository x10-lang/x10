package polyglot.ext.x10;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

import polyglot.ast.NodeFactory;
import polyglot.ext.jl.JLScheduler;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.visit.X10Boxer;
import polyglot.ext.x10.visit.X10Qualifier;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.VisitorPass;
import polyglot.frontend.goals.CodeGenerated;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.VisitorGoal;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.visit.NodeVisitor;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;

/**
 * Extension information for x10 extension.
 */
public class ExtensionInfo extends polyglot.ext.jl.ExtensionInfo {
    static final boolean DEBUG_ = false;
    static {
        // force Topics to load
        Topics t = new Topics();
    }
    
    public static String clock = "clock";

    static {
        Report.topics.add(clock);
    }

    public polyglot.main.Version version() {
    	return new Version();
    }
    public String defaultFileExtension() {
        return "x10";
    }

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
        return new X10NodeFactory_c();
    }

    protected TypeSystem createTypeSystem() {
        return new X10TypeSystem_c();
    }

    protected Scheduler createScheduler() {
        return new X10Scheduler(this);
    }

    static class X10Scheduler extends JLScheduler {
	X10Scheduler(ExtensionInfo extInfo) {
	    super(extInfo);
	}
	public Goal CodeGenerated(final Job job) {
//	    System.out.println("creating CodeGenerated Goal for " + job.source().name());
	    try {
		Goal g= internGoal(new X10CodeGenerated(job));
		return g;
	    } catch (CyclicDependencyException e) {
		throw new IllegalStateException(e.getMessage());
	    }
	}
    }

    static class X10CodeGenerated extends CodeGenerated {
	private X10CodeGenerated(Job job) throws CyclicDependencyException {
	    super(job);
	    addPrerequisiteGoal(new X10Boxed(job()), job.extensionInfo().scheduler());
	    addPrerequisiteGoal(new X10Qualified(job()), job.extensionInfo().scheduler());
	}
	public void setState(int state) {
	    super.setState(state);
//	    if (state == Goal.REACHED)
//		System.out.println("Reached CodeGenerated goal.");
	}
    }

    static class X10Boxed extends VisitorGoal {
	public X10Boxed(Job job) throws CyclicDependencyException {
	    super(job, new X10Boxer(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory()));
	    addPrerequisiteGoal(job.extensionInfo().scheduler().TypeChecked(job), job.extensionInfo().scheduler());
	}
	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
//	    System.out.println("Creating pass for X10Boxed goal...");
	    return super.createPass(extInfo);
	}
    }

    static class X10Qualified extends VisitorGoal {
	public X10Qualified(Job job) throws CyclicDependencyException {
	    super(job, new X10Qualifier(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory()));
	    addPrerequisiteGoal(job.extensionInfo().scheduler().TypeChecked(job), job.extensionInfo().scheduler());
	}
	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
//	    System.out.println("Creating pass for X10Qualified goal...");
	    return super.createPass(extInfo);
	}
    }

//    public static final Pass.ID CAST_REWRITE = new Pass.ID("cast-rewrite");
//    public static final Pass.ID SPECIAL_QUALIFIER = new Pass.ID("this/super-qualifier");
//    public static final Pass.ID ASYNC_ELIMINATION = new Pass.ID("async-elimination");
//    public static final Pass.ID ATOMIC_ELIMINATION = new Pass.ID("atomic-elimination");

//    public List passes(Job job) {
//        List passes = super.passes(job);
//        
//        if (DEBUG_) {
//            System.out.println("polyglot.ext.x10.ExtensionInfo: disabled passes: " + getOptions().disable_passes);
//        }
//        
//        // The elimination of synchronization may lead JiT's to promote local variables
//        // to memory where it it not permissible -- example would be AsyncTest1, which could fail
//        // if the atomic elimination is enabled.
//        
//        // beforePass(passes, Pass.PRE_OUTPUT_ALL,
//        //        new VisitorPass(ATOMIC_ELIMINATION,
//        //                job, new AtomicElimination()));
//        
//        // Schedule elimination of async / future after the atomic elimination 
//        // because atomic eliminiation might create additional opportunities.
//        //
//        // This transformation can render the results of the sampling mechanism
//        // incorrect because remote accesses may 'look like' local access in the 
//        // program.
//        //
//        // Moreover, it will mislead the BAD_PLACE_RUNTIME_CHECK - hence we 
//        // disable it.
//        // beforePass(passes, Pass.PRE_OUTPUT_ALL,
//        //      new VisitorPass(ASYNC_ELIMINATION,
//        //                 job, new AsyncElimination()));
//        
//        beforePass(passes, Pass.PRE_OUTPUT_ALL,
//                new VisitorPass(CAST_REWRITE,
//                        job, new X10Boxer(job, ts, nf)));
//        
//        beforePass(passes, Pass.PRE_OUTPUT_ALL,
//                new VisitorPass(SPECIAL_QUALIFIER,
//                        job, new X10Qualifier(job, ts, nf)));
//        
//        if (Report.should_report("debug", 6)) {
//			beforePass(passes, Pass.PRE_OUTPUT_ALL, new VisitorPass(Pass.DUMP, job,
//					new DumpAst(new CodeWriter(System.out, 1))));
//        }
//        return passes;
//    }
    
    protected Options createOptions() {
        return new X10CompilerOptions(this);
    }

}
