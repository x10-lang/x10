/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
package x10.parser.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.dfa.DFA;

import x10.parser.antlr.generated.X10Lexer;
import x10.parser.antlr.generated.X10Parser;

/**
 * This class allows to cleanup internal data structures of the
 * ANTLR parser.
 *
 * @author Louis Mandel
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
