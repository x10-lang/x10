package polyglot.ext.x10;

import java.io.Reader;
import java.util.List;

import x10.parser.*;

import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.visit.X10Boxer;
import polyglot.ext.x10.visit.X10Qualifier;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Pass;
import polyglot.frontend.VisitorPass;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.util.CodeWriter;
import polyglot.main.Report;
import polyglot.visit.DumpAst;

/**
 * Extension information for x10 extension.
 */
public class ExtensionInfo extends polyglot.ext.jl.ExtensionInfo {
    static {
        // force Topics to load
        Topics t = new Topics();
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
    	Option option = new Option(source.name());
        X10Lexer x10_lexer = new X10Lexer(option); // Create the lexer
        X10Parser x10_parser = new X10Parser(x10_lexer, ts, nf, source, eq); // Create the parser
        x10_lexer.lexer(x10_parser);
        return x10_parser; // Parse the token stream to produce an AST
    }

    protected NodeFactory createNodeFactory() {
        return new X10NodeFactory_c();
    }

    protected TypeSystem createTypeSystem() {
        return new X10TypeSystem_c();
    }

    public static final Pass.ID CAST_REWRITE = new Pass.ID("cast-rewrite");
    public static final Pass.ID SPECIAL_QUALIFIER = new Pass.ID("this/super-qualifier");
 
    public List passes(Job job) {
        List passes = super.passes(job);
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

}
