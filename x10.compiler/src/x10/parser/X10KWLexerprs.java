/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
/****************************************************************************
 * WARNING!  THIS JAVA FILE IS AUTO-GENERATED FROM x10/parser/X10KWLexer.gi *
 ****************************************************************************/

package x10.parser;

public class X10KWLexerprs implements lpg.runtime.ParseTable, X10KWLexersym {
    public final static int ERROR_SYMBOL = 0;
    public final int getErrorSymbol() { return ERROR_SYMBOL; }

    public final static int SCOPE_UBOUND = 0;
    public final int getScopeUbound() { return SCOPE_UBOUND; }

    public final static int SCOPE_SIZE = 0;
    public final int getScopeSize() { return SCOPE_SIZE; }

    public final static int MAX_NAME_LENGTH = 0;
    public final int getMaxNameLength() { return MAX_NAME_LENGTH; }

    public final static int NUM_STATES = 200;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 53;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 328;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 62;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 55;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 63;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 25;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 54;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 265;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 266;
    public final int getErrorAction() { return ERROR_ACTION; }

    public final static boolean BACKTRACK = false;
    public final boolean getBacktrack() { return BACKTRACK; }

    public final int getStartSymbol() { return lhs(0); }
    public final boolean isValidForParser() { return X10KWLexersym.isValidForParser; }


    public interface IsNullable {
        public final static byte isNullable[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0
        };
    };
    public final static byte isNullable[] = IsNullable.isNullable;
    public final boolean isNullable(int index) { return isNullable[index] != 0; }

    public interface ProsthesesIndex {
        public final static byte prosthesesIndex[] = {0,
            2,1
        };
    };
    public final static byte prosthesesIndex[] = ProsthesesIndex.prosthesesIndex;
    public final int prosthesesIndex(int index) { return prosthesesIndex[index]; }

    public interface IsKeyword {
        public final static byte isKeyword[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0
        };
    };
    public final static byte isKeyword[] = IsKeyword.isKeyword;
    public final boolean isKeyword(int index) { return isKeyword[index] != 0; }

    public interface BaseCheck {
        public final static byte baseCheck[] = {0,
            8,2,6,5,2,6,6,6,5,4,
            5,5,7,8,3,7,2,4,7,5,
            5,7,6,3,4,4,2,10,6,2,
            10,9,6,3,4,5,6,8,7,7,
            8,9,6,6,4,6,6,5,6,4,
            5,6,9,4,3,4,3,3,4,4,
            5,7
        };
    };
    public final static byte baseCheck[] = BaseCheck.baseCheck;
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static byte rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };

    public interface BaseAction {
        public final static char baseAction[] = {
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,65,71,61,63,17,24,
            125,27,67,35,34,79,128,46,68,83,
            47,28,55,85,123,88,116,59,10,132,
            133,95,135,39,120,94,136,137,141,142,
            87,146,101,145,149,153,156,160,155,161,
            166,162,163,169,104,109,171,64,108,173,
            175,176,43,180,182,177,185,188,190,194,
            189,196,197,199,200,115,201,203,206,204,
            207,211,213,215,112,218,220,224,119,226,
            227,232,234,236,239,235,241,243,247,245,
            249,251,253,254,256,259,261,264,265,225,
            267,268,269,270,279,283,285,286,287,271,
            277,291,293,297,299,302,304,305,306,308,
            281,311,292,310,313,316,320,317,321,322,
            323,326,331,332,333,340,334,343,344,347,
            346,355,348,356,359,360,352,362,365,349,
            369,368,374,371,375,377,378,381,382,389,
            391,392,396,398,399,402,404,406,407,408,
            413,411,409,416,418,419,422,423,424,426,
            428,429,431,435,439,440,443,446,442,450,
            447,452,455,456,266,266
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,0,
            10,11,12,4,14,15,0,8,18,19,
            4,21,22,0,1,2,0,0,12,3,
            4,4,16,0,0,1,13,3,0,13,
            2,18,0,10,11,0,0,13,3,3,
            17,6,6,8,0,9,2,15,0,5,
            0,3,0,0,1,3,0,0,6,6,
            0,13,12,19,16,12,9,11,0,1,
            14,3,0,1,0,1,0,0,6,0,
            23,4,8,0,0,25,9,3,4,6,
            0,8,2,0,18,5,3,0,0,6,
            2,0,5,5,0,0,2,6,0,0,
            9,3,0,16,0,1,8,0,14,14,
            8,0,0,6,0,0,0,1,19,8,
            0,0,7,9,0,0,14,2,0,0,
            9,11,0,5,0,0,4,2,14,0,
            0,0,0,4,10,0,5,2,0,9,
            0,1,0,11,0,0,0,5,10,0,
            1,0,1,9,0,10,10,0,0,0,
            6,2,5,0,1,0,0,2,0,0,
            0,13,0,0,1,0,0,11,9,4,
            0,13,0,1,0,9,2,0,8,0,
            1,21,20,0,0,0,0,1,4,6,
            5,0,1,0,0,0,3,2,0,5,
            0,24,0,1,0,7,0,7,0,3,
            0,3,0,0,1,0,6,2,0,1,
            0,17,10,0,0,5,0,0,0,0,
            0,7,4,7,4,8,0,8,0,1,
            0,18,0,1,0,0,0,3,3,3,
            0,0,0,13,4,3,0,21,0,1,
            4,0,1,0,0,0,1,0,5,0,
            0,20,0,9,5,0,0,10,8,0,
            0,0,0,7,12,0,4,8,7,4,
            0,0,0,0,0,20,5,17,8,0,
            7,2,0,0,12,0,0,0,0,4,
            7,0,10,7,0,0,2,2,0,0,
            1,0,11,5,0,17,2,0,0,22,
            0,10,4,0,0,0,0,0,1,12,
            0,0,9,9,0,15,10,7,0,1,
            0,0,2,12,3,0,1,0,0,2,
            2,0,1,0,1,0,0,0,0,3,
            0,6,0,1,7,0,6,0,0,2,
            5,0,0,0,16,0,1,0,0,7,
            0,13,4,10,0,1,15,7,0,0,
            1,0,0,16,2,0,0,2,10,0,
            1,0,6,2,0,0,15,0,0,0,
            5,0,0,0,0,11,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            266,78,68,82,70,69,72,80,74,266,
            73,77,75,126,71,79,266,127,66,81,
            87,67,76,266,92,91,266,266,88,96,
            95,116,86,266,266,100,90,101,266,94,
            133,89,266,102,293,266,266,99,109,115,
            103,107,113,108,266,114,117,325,266,118,
            266,125,266,5,160,85,266,266,84,159,
            266,124,83,119,321,161,111,98,266,105,
            97,104,266,112,266,121,266,266,283,266,
            110,324,120,266,266,265,323,131,130,135,
            30,136,142,266,300,143,155,2,266,154,
            156,266,163,157,266,266,174,183,266,266,
            184,187,266,162,266,93,186,266,175,123,
            122,266,266,106,266,266,266,138,134,128,
            266,266,137,132,266,266,129,141,266,266,
            140,139,266,145,266,266,146,147,144,266,
            266,266,266,290,148,266,151,150,266,149,
            266,158,266,152,266,266,266,164,153,266,
            322,266,320,165,266,326,166,266,266,266,
            167,168,316,266,169,266,266,171,266,266,
            266,170,266,266,179,266,266,311,173,178,
            266,172,266,181,266,301,182,266,180,266,
            292,176,177,266,266,266,266,189,314,291,
            188,266,284,15,266,266,190,191,266,193,
            266,185,266,276,266,192,266,194,266,195,
            266,197,266,266,200,266,198,201,266,327,
            266,196,199,266,266,202,266,266,266,266,
            266,204,207,205,214,206,266,208,266,209,
            266,203,266,210,266,266,266,211,212,213,
            266,266,266,224,216,217,266,215,266,219,
            218,266,220,266,266,266,286,266,221,266,
            266,226,266,222,278,266,266,223,225,266,
            266,266,266,228,277,266,230,227,270,231,
            266,51,266,266,266,275,318,229,232,266,
            312,313,266,266,315,266,266,266,266,234,
            309,266,310,233,266,266,235,237,36,266,
            299,266,238,303,266,240,295,266,266,236,
            266,239,241,21,266,266,266,266,246,289,
            266,266,242,244,266,243,245,274,266,272,
            266,266,269,273,247,266,248,266,266,249,
            250,266,306,266,305,266,266,266,266,252,
            266,251,266,254,253,266,328,266,266,282,
            285,266,266,266,288,266,258,266,266,256,
            266,255,304,257,266,260,279,259,266,266,
            280,266,266,307,267,266,266,319,261,266,
            298,266,262,263,266,266,308,266,266,266,
            294,266,266,266,266,297
        };
    };
    public final static char termAction[] = TermAction.termAction;
    public final int termAction(int index) { return termAction[index]; }
    public final int asb(int index) { return 0; }
    public final int asr(int index) { return 0; }
    public final int nasb(int index) { return 0; }
    public final int nasr(int index) { return 0; }
    public final int terminalIndex(int index) { return 0; }
    public final int nonterminalIndex(int index) { return 0; }
    public final int scopePrefix(int index) { return 0;}
    public final int scopeSuffix(int index) { return 0;}
    public final int scopeLhs(int index) { return 0;}
    public final int scopeLa(int index) { return 0;}
    public final int scopeStateSet(int index) { return 0;}
    public final int scopeRhs(int index) { return 0;}
    public final int scopeState(int index) { return 0;}
    public final int inSymb(int index) { return 0;}
    public final String name(int index) { return null; }
    public final int originalState(int state) { return 0; }
    public final int asi(int state) { return 0; }
    public final int nasi(int state) { return 0; }
    public final int inSymbol(int state) { return 0; }

    /**
     * assert(! goto_default);
     */
    public final int ntAction(int state, int sym) {
        return baseAction[state + sym];
    }

    /**
     * assert(! shift_default);
     */
    public final int tAction(int state, int sym) {
        int i = baseAction[state],
            k = i + sym;
        return termAction[termCheck[k] == sym ? k : i];
    }
    public final int lookAhead(int la_state, int sym) {
        int k = la_state + sym;
        return termAction[termCheck[k] == sym ? k : la_state];
    }
}
