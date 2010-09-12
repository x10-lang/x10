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

    public final static int NUM_STATES = 221;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 359;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 67;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 68;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 25;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 291;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 292;
    public final int getErrorAction() { return ERROR_ACTION; }

    public final static boolean BACKTRACK = false;
    public final boolean getBacktrack() { return BACKTRACK; }

    public final int getStartSymbol() { return lhs(0); }
    public final boolean isValidForParser() { return X10KWLexersym.isValidForParser; }


    public interface IsNullable {
        public final static byte isNullable[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0
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
            0,0,0,0,0,0,0
        };
    };
    public final static byte isKeyword[] = IsKeyword.isKeyword;
    public final boolean isKeyword(int index) { return isKeyword[index] != 0; }

    public interface BaseCheck {
        public final static byte baseCheck[] = {0,
            8,2,6,5,2,6,6,5,5,4,
            5,5,7,8,3,7,2,4,7,6,
            5,5,7,6,3,7,6,4,4,2,
            10,6,2,10,9,6,3,4,11,4,
            5,6,8,7,7,8,9,6,6,4,
            4,10,6,6,5,6,4,5,6,9,
            4,3,4,3,3,4,5
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
            1,1,1,1,1,1,1,1,1,70,
            112,7,126,42,24,133,44,62,29,53,
            135,69,58,72,80,28,18,35,82,88,
            70,54,89,130,138,96,97,140,63,141,
            77,142,143,146,148,153,85,159,102,149,
            158,164,145,165,167,170,171,172,168,175,
            106,105,177,181,114,111,183,186,187,188,
            195,190,193,198,199,204,200,207,202,208,
            210,213,215,118,216,217,218,223,220,227,
            228,231,232,235,117,243,229,234,245,122,
            247,250,252,254,256,259,260,261,262,267,
            265,248,272,271,276,279,282,280,273,285,
            287,292,284,294,296,298,301,303,305,306,
            310,311,312,299,316,314,319,324,326,327,
            328,331,332,45,322,335,338,339,340,341,
            343,342,349,350,348,358,356,359,361,364,
            363,369,370,373,375,379,382,13,385,381,
            386,371,388,390,391,392,394,398,395,401,
            402,404,405,410,414,416,406,418,423,424,
            427,428,431,434,436,432,438,441,440,446,
            439,448,449,451,450,454,455,456,458,468,
            459,466,461,473,474,464,481,477,483,484,
            486,488,489,491,492,493,495,496,498,500,
            292,292
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,0,7,8,9,
            10,11,0,13,14,15,16,0,18,13,
            20,4,22,0,1,2,3,0,0,1,
            3,3,20,6,0,12,2,10,10,16,
            12,0,8,0,0,4,3,4,4,15,
            16,7,0,0,13,12,3,0,17,7,
            3,0,0,11,2,12,9,10,0,0,
            17,0,11,21,6,14,0,6,10,0,
            1,0,1,14,0,9,10,0,0,10,
            9,4,4,6,23,0,0,9,3,4,
            16,0,6,2,0,0,2,23,3,8,
            0,0,8,0,1,10,0,0,8,2,
            24,0,6,10,3,0,10,17,3,0,
            9,14,0,1,0,1,25,0,9,0,
            0,0,0,1,0,0,5,0,0,0,
            11,14,0,6,10,15,11,0,0,7,
            2,4,14,0,0,2,0,0,4,0,
            0,0,2,7,0,6,0,1,11,8,
            0,7,0,3,0,0,0,0,1,0,
            8,6,0,7,0,1,7,0,0,0,
            2,0,10,0,1,8,0,0,2,0,
            1,12,0,12,0,0,0,0,11,0,
            6,4,0,1,12,6,0,0,0,2,
            0,0,1,0,0,19,2,22,10,9,
            0,15,0,1,0,1,0,0,15,0,
            1,0,1,0,8,0,3,2,0,0,
            0,0,1,5,0,5,0,8,21,3,
            0,0,0,9,3,0,1,7,0,0,
            2,0,1,0,0,0,0,8,16,5,
            4,0,9,0,1,0,5,0,0,4,
            0,1,0,1,0,0,9,3,3,0,
            0,0,3,0,4,0,3,6,0,4,
            22,0,4,0,1,0,0,0,3,3,
            0,0,1,12,0,8,6,0,0,0,
            0,0,0,9,2,0,8,0,0,0,
            9,4,13,5,5,0,19,0,0,19,
            0,4,0,0,9,2,8,5,0,0,
            0,1,0,13,0,7,7,5,0,5,
            0,0,4,2,0,0,2,0,8,0,
            0,0,2,0,0,10,7,0,11,6,
            0,0,5,0,0,0,6,13,7,0,
            6,0,21,0,1,0,7,0,13,2,
            5,18,0,0,1,3,0,0,2,2,
            0,0,2,0,1,0,1,0,0,0,
            0,10,5,3,5,0,1,0,0,0,
            0,13,2,0,0,0,8,0,0,5,
            0,12,7,0,17,0,9,0,1,4,
            7,18,0,0,1,17,0,5,2,19,
            0,1,0,0,2,0,3,0,0,1,
            0,0,0,2,0,0,9,0,6,0,
            10,7,0,18,0,8,11,0,0,0,
            0,0,0,0,0,0,0,0,0,20,
            0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            292,83,73,87,75,85,292,78,74,79,
            77,82,292,80,76,86,71,292,84,88,
            81,122,72,292,96,95,97,292,292,106,
            121,107,254,120,292,94,124,119,105,93,
            104,292,125,292,292,91,101,100,240,126,
            123,241,292,292,92,99,131,292,90,108,
            115,292,292,322,141,130,114,113,292,292,
            354,292,103,109,112,102,292,117,111,292,
            118,292,128,129,292,144,143,292,292,309,
            127,357,132,356,116,292,292,133,137,136,
            329,33,139,152,292,292,167,150,166,153,
            2,292,168,5,172,165,292,292,174,186,
            138,292,197,171,201,292,196,173,89,292,
            200,187,292,98,292,110,291,292,134,292,
            292,292,292,146,292,292,145,292,292,292,
            140,135,292,148,157,142,147,292,292,149,
            151,155,154,292,292,156,292,292,158,292,
            292,292,161,159,292,160,292,169,163,162,
            292,164,292,170,292,292,292,292,355,292,
            175,176,292,358,292,353,177,292,292,292,
            179,292,178,292,180,349,292,292,182,292,
            342,181,292,183,292,292,292,292,343,292,
            185,190,292,191,184,332,292,292,292,330,
            292,292,194,292,292,189,195,188,320,193,
            292,192,292,321,25,199,292,292,198,292,
            203,292,310,15,202,292,204,205,292,292,
            292,292,302,206,292,208,292,207,211,209,
            292,292,292,210,212,292,214,213,292,292,
            215,292,359,292,292,292,292,216,217,218,
            347,292,220,292,221,292,219,292,292,222,
            292,224,292,225,292,292,223,226,227,292,
            292,292,228,292,229,292,233,230,292,232,
            231,292,234,292,235,292,292,292,236,237,
            292,292,313,242,292,238,239,292,292,292,
            292,292,292,243,300,292,304,292,292,292,
            245,247,303,246,296,292,244,292,58,301,
            292,248,292,292,249,346,351,345,292,292,
            292,328,292,348,292,250,341,340,292,251,
            41,292,252,253,292,292,255,292,334,292,
            292,292,324,292,292,256,258,292,257,319,
            22,292,260,292,292,292,261,316,312,292,
            263,292,259,292,265,292,264,292,298,295,
            299,262,292,292,267,266,292,292,268,269,
            292,292,270,292,337,292,336,292,292,292,
            292,271,272,273,274,292,275,292,292,292,
            292,318,308,292,292,292,311,292,292,277,
            292,276,278,292,315,292,279,292,280,335,
            284,305,292,292,283,338,292,282,293,281,
            292,306,292,292,352,292,285,292,292,327,
            292,292,292,288,292,292,286,292,344,292,
            287,289,292,339,292,323,326,292,292,292,
            292,292,292,292,292,292,292,292,292,331
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
