package x10.parser.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.FileSource;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import x10.parserGen.X10ParserLexer;
import x10.parserGen.X10ParserParser;

public class X10Parser implements polyglot.frontend.Parser {
	
	private final X10ParserParser p;
    private final X10ParserLexer lexer;
    
    private ErrorQueue eq;
    private TypeSystem ts;
    private NodeFactory nf;
    private FileSource srce;
    
    
	public X10Parser(TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q){
		ANTLRInputStream input = new ANTLRInputStream(source.toString());
		lexer = new X10ParserLexer(input);
	    CommonTokenStream tokens = new CommonTokenStream(lexer);
	    p = new X10ParserParser(tokens);
		ts = t;
		nf = n;
		srce = source;
		eq = q;
	}

	@Override
	public Node parse() {
		ParseTree tree = p.accept();
		NodeVisitor v = new NodeVisitor(ts, nf, srce, eq);
		return tree.accept(v);
	}
}