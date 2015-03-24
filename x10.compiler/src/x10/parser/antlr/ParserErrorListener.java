package x10.parser.antlr;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import polyglot.frontend.Compiler;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;

public class ParserErrorListener extends BaseErrorListener implements ANTLRErrorListener {

    private ErrorQueue eq;
    private String file;

    public ParserErrorListener(ErrorQueue q, String f) {
        super();
        eq = q;
        file = f;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        Position pos;
        if (offendingSymbol instanceof Token) {
            pos = pos((Token) offendingSymbol);
        } else {
            pos = new Position(null, file, line, charPositionInLine);
        }
        syntaxError(msg, pos);
    }

    public void syntaxError(String msg, Position pos) {
        eq.enqueue(ErrorInfo.SYNTAX_ERROR, msg, pos);
    }

    /** Returns the position of a given token. */
    private Position pos(Token t) {
        int eofOffset = t.getInputStream().size() - 1;
        int line = t.getLine();
        int column = t.getCharPositionInLine();
        int offset = Math.min(t.getStartIndex(), eofOffset);
        int endLine = line;
        int endOffset = Math.max(t.getStopIndex(), offset);
        int endColumn = column + endOffset - offset;
        return new Position("", file, line, column, endLine, endColumn, offset, endOffset);
    }

}
