package x10.uide.parser;

import org.eclipse.uide.parser.ILexer;
import x10.parser.X10Lexer;

import com.ibm.lpg.LexStream;
import com.ibm.lpg.Monitor;
import com.ibm.lpg.PrsStream;

public class LexerDelegate implements ILexer
{
	X10Lexer myLexer = new X10Lexer();

	public int [] getKeywordKinds() { return myLexer.getKeywordKinds(); }
	public LexStream getLexStream() { return myLexer.getLexStream(); }
	public void initialize(char [] content, String filename) { myLexer.initialize(content, filename); }
	public void lexer(Monitor monitor, PrsStream prsStream) { myLexer.lexer(monitor, prsStream); }
}