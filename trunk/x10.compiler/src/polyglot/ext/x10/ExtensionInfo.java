package polyglot.ext.x10;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import x10.parser.*;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.Version;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.visit.X10Boxer;
import polyglot.ext.x10.visit.X10Qualifier;
import polyglot.ext.x10.visit.AsyncElimination;
import polyglot.ext.x10.visit.AtomicElimination;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Pass;
import polyglot.frontend.VisitorPass;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.util.CodeWriter;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.visit.DumpAst;

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

//    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
//        Lexer lexer = new Lexer_c(reader, source.name(), eq);
//        Grm grm = new Grm(lexer, ts, nf, eq);
//        return new CupParser(grm, source, eq);
//    }
    
    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
        X10Lexer x10_lexer;
        try {
            x10_lexer = new X10Lexer(reader, source.name());
            X10Parser x10_parser = new X10Parser(x10_lexer, ts, nf, source, eq); // Create the parser
            x10_lexer.lexer(x10_parser);
            return x10_parser; // Parse the token stream to produce an AST
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // Create the lexer
        throw new IllegalStateException("Bad Parser");
    }

    protected NodeFactory createNodeFactory() {
        return new X10NodeFactory_c();
    }

    protected TypeSystem createTypeSystem() {
        return new X10TypeSystem_c();
    }

    public static final Pass.ID CAST_REWRITE = new Pass.ID("cast-rewrite");
    public static final Pass.ID SPECIAL_QUALIFIER = new Pass.ID("this/super-qualifier");
    public static final Pass.ID ASYNC_ELIMINATION = new Pass.ID("async-elimination");
    public static final Pass.ID ATOMIC_ELIMINATION = new Pass.ID("atomic-elimination");
    
    public List passes(Job job) {
        List passes = super.passes(job);
        
        if (DEBUG_) {
            System.out.println("polyglot.ext.x10.ExtensionInfo: disabled passes: " + getOptions().disable_passes);
        }
        
        // The elimination of synchronization may lead JiT's to promote local variables
        // to memory where it it not permissible -- example would be AsyncTest1, which could fail
        // if the atomic elimination is enabled.
        
        // beforePass(passes, Pass.PRE_OUTPUT_ALL,
        //        new VisitorPass(ATOMIC_ELIMINATION,
        //                job, new AtomicElimination()));
        
        // Schedule elimination of async / future after the atomic elimination 
        // because atomic eliminiation might create additional opportunities.
        //
        // This transformation can render the results of the sampling mechanism
        // incorrect because remote accesses may 'look like' local access in the 
        // program.
        //
        // Moreover, it will mislead the BAD_PLACE_RUNTIME_CHECK - hence we 
        // disable it.
        // beforePass(passes, Pass.PRE_OUTPUT_ALL,
        //      new VisitorPass(ASYNC_ELIMINATION,
        //                 job, new AsyncElimination()));
        
        beforePass(passes, Pass.PRE_OUTPUT_ALL,
                new VisitorPass(CAST_REWRITE,
                        job, new X10Boxer(job, ts, nf)));
        
        beforePass(passes, Pass.PRE_OUTPUT_ALL,
                new VisitorPass(SPECIAL_QUALIFIER,
                        job, new X10Qualifier(job, ts, nf)));
        
        if (Report.should_report("debug", 6)) {
			beforePass(passes, Pass.PRE_OUTPUT_ALL, new VisitorPass(Pass.DUMP, job,
					new DumpAst(new CodeWriter(System.out, 1))));
        }
        return passes;
    }
    
    protected Options createOptions() {
        return new X10CompilerOptions(this);
    }

}
