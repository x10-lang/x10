package polyglot.ext.x10;

import java.io.Reader;
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.parse.Grm;
import polyglot.ext.x10.parse.Lexer_c;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.visit.X10Boxer;
import polyglot.frontend.CupParser;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Pass;
import polyglot.frontend.VisitorPass;
import polyglot.lex.Lexer;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;

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

    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
        Lexer lexer = new Lexer_c(reader, source.name(), eq);
        Grm grm = new Grm(lexer, ts, nf, eq);
        return new CupParser(grm, source, eq);
    }

    protected NodeFactory createNodeFactory() {
        return new X10NodeFactory_c();
    }

    protected TypeSystem createTypeSystem() {
        return new X10TypeSystem_c();
    }

    public static final Pass.ID DE_ASYNCANDFUTUREIFICATION =
        new Pass.ID("de-asyncandfutureification");
    public static final Pass.ID CAST_REWRITE = new Pass.ID("cast-rewrite");
 
    public List passes(Job job) {
        List passes = super.passes(job);
        beforePass(passes, Pass.PRE_OUTPUT_ALL,
                new VisitorPass(CAST_REWRITE,
                        job, new X10Boxer(job, ts, nf)));
        return passes;
    }

}
