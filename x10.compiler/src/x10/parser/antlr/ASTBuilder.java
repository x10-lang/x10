package x10.parser.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.FileSource;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import x10.parserGen.X10Lexer;
import x10.parserGen.X10Parser;

public class ASTBuilder implements polyglot.frontend.Parser {
	
	private final X10Parser p;
    private final X10Lexer lexer;
    
    private ErrorQueue eq;
    private TypeSystem ts;
    private NodeFactory nf;
    private FileSource srce;
    
	public ASTBuilder(TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q){
		ANTLRInputStream input = new ANTLRInputStream(source.toString());
		lexer = new X10Lexer(input);
	    CommonTokenStream tokens = new CommonTokenStream(lexer);
	    p = new X10Parser(tokens);
		ts = t;
		nf = n;
		srce = source;
		eq = q;
	}

	@Override
	public Node parse() {
		ParseTree tree = p.accept();
		ParseTreeWalker walker = new ParseTreeWalker();
		ASTListener builder = new ASTListener();
        walker.walk(builder, tree);
		return builder.get(tree);
	}
}