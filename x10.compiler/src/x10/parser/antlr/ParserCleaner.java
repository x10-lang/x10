/**
 * 
 */
package x10.parser.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.dfa.DFA;

import x10.parser.antlr.generated.X10Lexer;
import x10.parser.antlr.generated.X10Parser;

/**
 * @author lmandel
 *
 */
public class ParserCleaner {

    private static class X10LexerClearable extends X10Lexer {
        private X10LexerClearable(CharStream input) {
            super(input);
        }
        public static void clearDFA() {
            for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
                _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
            }
        }
    }
    
    private static class X10ParserClearable extends X10Parser {
        private X10ParserClearable(TokenStream input) {
            super(input);
        }
        public static void clearDFA() {
            for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
                _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
            }
        }
    }

    public static void clearDFA() {
        X10LexerClearable.clearDFA();
        X10ParserClearable.clearDFA();
    }
}
