package x10.parser.antlr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.FileSource;
import polyglot.frontend.Compiler;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import x10.parserGen.X10Lexer;
import x10.parserGen.X10Parser;
import x10.parserGen.X10Parser.AcceptContext;

public class ASTBuilder implements polyglot.frontend.Parser {
	
	private final X10Parser p;
    private final X10Lexer lexer;
    
    private Compiler compiler;
    private ErrorQueue eq;
    private TypeSystem ts;
    private NodeFactory nf;
    private FileSource srce;
    
	public ASTBuilder(Compiler c, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q){
		compiler = c;
		ts = t;
		nf = n;
		srce = source;
		eq = q;
		
		String file = source.toString();
		ANTLRInputStream input;
			try {
				input = new ANTLRInputStream(new FileInputStream(file));
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
		    p.addErrorListener(new ParserErrorListener(compiler, file));
	}

	@Override
	public Node parse() {
		AcceptContext tree = p.accept();
		ParseTreeWalker walker = new ParseTreeWalker();
		ASTListener builder = new ASTListener();
        walker.walk(builder, tree);
		return builder.get(tree);
	}
}