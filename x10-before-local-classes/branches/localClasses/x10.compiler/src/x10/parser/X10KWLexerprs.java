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

    public final static int NUM_STATES = 241;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 393;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 74;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 75;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 25;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 318;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 319;
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
            5,5,7,5,8,3,7,2,4,7,
            6,5,5,7,6,3,7,6,6,4,
            4,2,10,6,2,10,10,9,5,6,
            3,4,11,4,5,6,8,2,7,7,
            8,9,5,6,6,4,4,10,6,6,
            6,5,6,4,5,6,9,4,3,4,
            3,3,4,5
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
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,77,19,20,105,
            34,24,78,53,55,29,128,54,119,80,
            39,88,89,65,143,43,61,94,146,73,
            82,148,149,97,151,102,152,153,156,21,
            159,161,157,104,166,95,168,167,75,172,
            174,177,176,180,181,184,187,188,189,191,
            195,103,113,198,202,116,114,200,196,206,
            209,211,207,213,214,217,223,218,225,226,
            221,228,233,231,236,125,235,237,239,244,
            246,238,247,248,252,259,261,262,124,264,
            266,267,268,270,130,272,274,276,278,134,
            283,284,285,286,290,288,294,296,295,300,
            303,306,302,308,309,250,311,312,318,320,
            323,316,137,328,330,331,332,336,337,324,
            338,341,344,339,348,349,351,353,355,356,
            360,361,141,363,366,367,368,370,371,372,
            373,379,377,380,381,388,390,393,382,394,
            397,297,398,399,404,405,402,412,409,413,
            416,418,419,417,422,421,426,423,427,433,
            436,437,439,438,440,441,445,448,451,452,
            453,458,459,462,466,467,470,472,474,476,
            475,477,479,485,480,487,488,489,490,492,
            494,495,497,498,502,506,503,509,511,513,
            516,517,519,524,527,523,528,531,529,533,
            540,541,534,537,544,547,548,319,319
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,0,12,13,14,15,16,17,0,0,
            0,21,22,0,1,2,3,7,0,1,
            10,3,13,0,11,7,13,4,0,11,
            17,3,0,25,2,7,13,0,10,11,
            8,18,0,0,0,3,4,15,4,17,
            0,1,9,11,0,12,12,3,14,5,
            10,7,0,20,0,3,2,0,1,0,
            6,0,8,11,5,4,7,0,0,1,
            18,10,5,0,0,7,0,4,5,3,
            4,0,0,0,0,3,5,3,5,7,
            23,17,0,0,2,0,1,23,0,1,
            8,8,7,0,0,24,2,0,5,0,
            7,18,3,0,7,2,0,1,14,10,
            0,8,0,7,4,0,4,0,0,9,
            0,0,0,3,2,0,0,10,0,14,
            0,1,14,12,6,0,0,0,12,2,
            15,0,6,0,9,0,0,4,2,0,
            0,2,7,0,4,14,0,0,0,2,
            0,5,9,0,0,0,8,0,1,0,
            5,0,12,9,3,0,0,8,0,1,
            0,1,0,0,9,9,0,0,2,7,
            0,8,0,1,0,0,2,0,11,4,
            0,11,0,1,0,0,0,0,0,12,
            5,11,4,0,1,0,0,0,2,0,
            5,0,15,4,3,19,22,10,0,1,
            0,0,2,0,1,0,0,0,7,0,
            1,0,7,0,1,0,1,0,11,8,
            3,15,0,0,0,0,1,0,6,0,
            6,8,3,0,0,0,0,10,3,0,
            1,0,0,9,2,0,1,0,0,8,
            0,0,16,20,6,0,6,0,1,0,
            1,10,0,0,17,10,4,0,1,0,
            0,0,3,3,3,0,0,0,0,4,
            0,5,5,0,4,22,3,0,0,1,
            0,4,0,3,0,0,4,3,20,0,
            0,1,0,8,5,0,0,0,2,0,
            0,0,0,11,2,10,0,8,0,0,
            0,0,6,13,4,6,19,0,10,0,
            19,4,0,0,13,2,0,0,0,10,
            8,0,6,0,0,4,9,9,0,6,
            6,0,0,2,2,0,0,0,0,1,
            0,0,0,8,7,0,0,2,12,21,
            9,5,0,1,14,0,0,0,0,0,
            0,6,20,5,0,5,9,0,1,13,
            0,0,0,9,2,16,6,0,0,1,
            3,0,0,2,13,0,0,2,2,0,
            1,0,1,0,0,0,0,3,0,0,
            7,6,6,5,0,1,0,0,0,0,
            2,0,13,0,0,8,0,0,1,6,
            11,0,0,9,18,0,10,16,0,4,
            0,1,0,1,6,0,0,1,0,18,
            2,19,0,0,9,2,0,0,0,3,
            0,1,0,0,2,7,0,10,16,0,
            0,2,9,0,1,5,0,0,12,0,
            0,0,0,0,8,0,0,0,0,0,
            0,0,0,0,0,0,0,0,21,0,
            0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            319,91,80,95,82,86,93,84,81,85,
            87,319,90,88,83,94,92,78,319,319,
            319,89,79,319,105,103,106,155,319,115,
            156,116,96,319,102,114,104,99,319,113,
            101,126,319,318,135,124,100,319,125,123,
            136,98,319,319,319,110,109,137,367,134,
            319,139,118,108,319,351,112,132,111,131,
            138,130,319,119,35,142,165,319,107,319,
            167,319,166,141,122,143,121,319,319,129,
            388,144,128,319,319,337,319,391,390,148,
            147,319,319,319,319,181,151,97,160,180,
            127,360,319,2,182,5,187,162,319,120,
            183,189,186,319,319,150,202,319,215,319,
            214,188,220,319,117,224,319,245,203,219,
            319,225,319,372,263,319,133,319,319,264,
            319,319,319,149,153,319,319,145,319,140,
            319,158,146,152,157,319,319,319,159,163,
            154,319,164,319,161,319,319,169,170,319,
            319,172,171,319,173,168,319,319,319,176,
            319,175,174,319,319,319,177,319,184,319,
            191,319,178,179,185,319,319,190,319,389,
            319,387,319,319,392,192,319,319,194,193,
            319,383,319,195,319,319,197,319,196,198,
            319,199,319,375,319,319,319,319,319,376,
            201,200,206,319,207,319,319,319,361,319,
            363,319,208,381,210,205,204,209,319,211,
            319,319,212,319,350,319,319,319,213,26,
            218,319,349,319,222,319,338,16,217,221,
            223,216,319,319,319,319,329,319,226,319,
            228,227,229,319,319,319,319,230,232,319,
            234,319,319,233,235,319,393,319,319,236,
            319,319,378,231,238,319,239,319,241,319,
            242,240,319,319,237,244,243,319,246,319,
            319,319,247,248,249,319,319,319,319,250,
            319,251,358,319,253,252,254,319,319,257,
            319,256,319,258,319,319,259,260,255,319,
            319,341,319,261,262,319,319,319,333,319,
            319,319,319,265,327,266,319,331,319,319,
            319,319,269,330,270,323,267,319,268,319,
            328,271,65,319,382,380,319,319,319,272,
            385,319,379,319,319,275,273,374,319,373,
            274,319,319,276,278,45,319,319,319,359,
            319,319,319,365,279,319,319,353,280,277,
            281,348,319,347,282,319,319,319,23,319,
            319,284,283,285,319,287,340,319,289,344,
            319,319,319,288,322,286,326,319,319,291,
            290,319,319,292,325,319,319,293,294,319,
            369,319,368,319,319,319,319,297,319,319,
            295,296,298,299,319,300,319,319,319,319,
            336,319,346,319,319,339,319,319,305,302,
            301,319,319,303,343,319,304,332,319,366,
            319,308,319,309,307,319,319,334,319,370,
            320,306,319,319,310,386,319,319,319,311,
            319,357,319,319,314,313,319,312,371,319,
            319,315,316,319,355,377,319,319,356,319,
            319,319,319,319,352,319,319,319,319,319,
            319,319,319,319,319,319,319,319,362
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
