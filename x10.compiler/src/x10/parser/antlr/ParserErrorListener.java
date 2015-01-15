package x10.parser.antlr;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import polyglot.frontend.Compiler;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;

public class ParserErrorListener extends BaseErrorListener implements
        ANTLRErrorListener {
    private ErrorQueue eq;
    private String file;

    public ParserErrorListener(ErrorQueue q, String f) {
        super();
        eq = q;
        file = f;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        eq.enqueue(ErrorInfo.SYNTAX_ERROR, file + ": line " + line
                + ", chararcter " + charPositionInLine + ": " + msg);
    }

}
