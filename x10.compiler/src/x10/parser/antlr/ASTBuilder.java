package x10.parser.antlr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JDialog;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.FileSource;
import polyglot.frontend.Compiler;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import x10.X10CompilerOptions;
import x10.parserGen.X10Lexer;
import x10.parserGen.X10Parser;
import x10.parserGen.X10Parser.CompilationUnitContext;

public class ASTBuilder implements polyglot.frontend.Parser {

    private final X10Parser p;
    private final X10Lexer lexer;

    private X10CompilerOptions compilerOpts;
    private ErrorQueue eq;
    private TypeSystem ts;
    private NodeFactory nf;
    private FileSource srce;

    public ASTBuilder(X10CompilerOptions opts, TypeSystem t, NodeFactory n,
            FileSource source, ErrorQueue q) {
        compilerOpts = opts;
        ts = t;
        nf = n;
        srce = source;
        eq = q;

        String fileName = source.toString();
        ANTLRInputStream input;
        try {
            input = new ANTLRInputStream(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            input = null;
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            input = null;
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        lexer = new X10Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        p = new X10Parser(tokens);
        p.removeErrorListeners();
        p.addErrorListener(new ParserErrorListener(eq, fileName));
    }

    @Override
    public Node parse() {
        CompilationUnitContext tree = p.compilationUnit();
        if (compilerOpts.x10_config.DISPLAY_PARSE_TREE) {
            Future<JDialog> dialogHdl = tree.inspect(p);
            try {
                JDialog dialog = dialogHdl.get();
                dialog.setTitle(srce.toString());
                Utils.waitForClose(dialog);
            } catch (Exception e) {
                eq.enqueue(ErrorInfo.WARNING, srce
                        + ": unable to display the parse tree.");
            }
        }
        ParseTreeWalker walker = new ParseTreeWalker();
        ParseTreeListener builder = new ParseTreeListener(nf);
        walker.walk(builder, tree);
        return tree.ast;
    }
}