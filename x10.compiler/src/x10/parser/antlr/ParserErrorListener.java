package x10.parser.antlr;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import polyglot.frontend.Compiler;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;

public class ParserErrorListener extends BaseErrorListener implements
        ANTLRErrorListener {

    private ErrorQueue eq;
    private String file;
    private boolean unrecoverableSyntaxError = false;

    public ParserErrorListener(ErrorQueue q, String f) {
        super();
        eq = q;
        file = f;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        Position pos = new Position(null, file, line, charPositionInLine);

        syntaxError(msg, pos);
    }

    public void syntaxError(String msg, Position pos) {
        syntaxError(msg, pos, false);
    }

    public void syntaxError(String msg, Position pos, boolean unrecoverable) {
        unrecoverableSyntaxError = unrecoverable;
        eq.enqueue(ErrorInfo.SYNTAX_ERROR, msg, pos);
    }
}
