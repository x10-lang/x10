package x10.parser.antlr;

import org.antlr.v4.runtime.ANTLRErrorListener;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import polyglot.frontend.Compiler;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorInfo;

public class ParserErrorListener extends BaseErrorListener implements
		ANTLRErrorListener {
	private Compiler compiler;
	private String file;

	public ParserErrorListener(Compiler c, String f) {
		super();
		compiler = c;
		file = f;
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
			Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		compiler.errorQueue().enqueue(
				ErrorInfo.SYNTAX_ERROR,
				file + ": line " + line + ", chararcter " + charPositionInLine
						+ ": " + msg);
	}

}
