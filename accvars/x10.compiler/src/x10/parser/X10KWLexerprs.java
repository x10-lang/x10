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

    public final static int NUM_STATES = 203;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 53;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 333;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 63;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 55;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 64;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 25;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 54;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 269;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 270;
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
            10,9,6,3,4,4,5,6,8,7,
            7,8,9,6,6,6,4,6,6,5,
            6,4,5,9,4,3,4,3,3,4,
            4,5,7
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
            1,1,1,1,1,66,103,127,71,17,
            24,131,28,64,37,33,79,133,44,18,
            83,51,134,53,85,105,61,123,56,87,
            135,141,94,142,99,10,100,145,146,69,
            144,92,148,107,149,154,156,162,157,36,
            158,166,165,169,172,111,116,173,62,119,
            171,175,177,178,179,185,188,183,190,192,
            196,191,199,200,203,204,207,88,205,208,
            206,217,211,219,222,223,225,120,209,231,
            229,122,234,236,240,242,244,243,247,248,
            253,256,249,257,258,251,262,266,269,267,
            271,273,274,275,276,277,279,283,285,287,
            290,294,296,298,292,300,303,301,307,309,
            311,312,314,317,318,320,319,325,322,323,
            324,326,331,334,336,338,337,344,346,349,
            350,351,354,355,359,364,353,365,368,369,
            371,372,374,377,380,378,379,381,385,387,
            389,392,395,400,402,403,409,405,411,414,
            416,412,419,420,423,425,421,428,430,426,
            429,434,435,439,436,442,443,447,449,451,
            454,455,458,460,462,463,386,464,270,270
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,0,
            10,0,12,13,14,15,0,0,18,19,
            4,21,22,0,1,2,9,0,19,13,
            3,4,0,17,11,0,0,1,11,3,
            23,18,10,0,12,10,3,11,16,6,
            0,8,0,3,2,0,6,5,3,9,
            0,0,1,0,4,0,11,6,0,9,
            0,19,17,3,13,12,6,14,0,1,
            12,3,0,1,0,1,0,0,6,2,
            4,0,8,0,8,0,3,4,0,0,
            2,14,0,5,0,6,0,8,2,18,
            0,5,8,3,23,0,6,2,0,0,
            5,0,0,5,3,6,0,25,9,8,
            0,1,0,0,0,17,14,4,6,13,
            0,0,8,0,0,0,1,0,0,2,
            9,7,9,0,14,0,0,0,5,4,
            4,0,14,2,0,0,9,2,0,5,
            0,0,0,1,0,5,0,0,0,1,
            12,10,0,9,0,1,10,0,6,0,
            0,0,15,2,5,0,1,10,0,0,
            2,11,0,0,0,0,0,0,0,4,
            0,12,0,11,11,9,0,1,0,9,
            2,0,0,1,0,21,2,20,0,8,
            0,1,24,0,6,0,1,0,5,0,
            1,0,0,0,3,2,0,0,0,7,
            0,5,0,1,7,0,0,0,3,3,
            10,0,1,6,16,0,0,2,0,1,
            0,5,0,0,0,0,0,4,0,7,
            4,7,0,8,0,1,0,1,18,0,
            8,0,3,0,16,0,3,0,3,0,
            0,4,0,4,4,3,0,1,0,1,
            0,0,21,0,1,5,0,0,0,0,
            9,0,0,0,0,0,10,8,11,5,
            0,8,7,0,13,0,0,0,20,4,
            7,4,20,0,8,0,16,2,0,0,
            0,1,0,0,0,7,13,0,0,10,
            7,7,4,0,0,2,2,0,0,1,
            0,0,5,0,22,2,0,0,0,0,
            0,10,12,4,0,0,0,9,0,1,
            13,0,16,9,0,15,10,12,7,0,
            1,0,0,2,0,3,2,13,0,1,
            0,0,2,0,1,0,1,6,0,0,
            0,3,0,1,0,0,7,0,0,0,
            6,2,5,0,0,0,11,17,0,1,
            7,0,0,15,10,4,0,1,0,7,
            0,1,17,0,0,2,2,0,10,0,
            1,0,0,0,2,0,0,6,5,0,
            0,0,15,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            270,79,69,83,71,70,73,81,75,270,
            74,270,78,76,72,80,270,270,67,82,
            88,68,77,270,93,92,112,270,136,89,
            97,96,270,87,91,270,270,101,95,102,
            111,90,103,270,297,151,110,100,104,108,
            270,109,270,116,118,270,114,119,126,115,
            270,5,163,270,329,270,125,162,270,328,
            270,120,326,86,164,99,85,98,270,106,
            141,105,270,113,270,122,270,270,287,178,
            127,270,121,270,128,270,132,131,270,270,
            134,179,270,135,270,137,30,138,145,304,
            270,146,123,158,143,270,157,159,2,270,
            160,270,270,166,191,187,270,269,188,190,
            270,94,270,270,270,165,124,117,107,84,
            270,270,129,270,270,270,140,270,270,144,
            133,139,142,270,130,270,270,270,148,149,
            294,270,147,150,270,270,152,153,270,154,
            270,270,270,161,270,167,270,270,270,327,
            155,156,270,168,270,325,331,270,170,270,
            270,270,330,171,322,270,172,169,270,270,
            174,173,270,270,270,270,270,270,270,182,
            270,317,270,175,176,177,270,183,270,306,
            305,270,270,185,270,180,186,181,270,184,
            270,296,189,270,295,270,193,270,192,270,
            288,15,270,270,194,195,270,270,270,196,
            270,197,270,280,198,270,270,270,199,201,
            203,270,204,202,200,270,270,205,270,332,
            270,206,270,270,270,270,270,320,270,207,
            210,208,270,209,270,213,270,214,323,270,
            212,270,215,270,211,270,216,270,217,270,
            270,218,270,220,222,221,270,223,270,224,
            270,270,219,270,290,225,270,270,270,270,
            226,270,270,270,270,270,227,229,228,282,
            270,231,232,270,281,270,270,270,230,234,
            274,235,279,270,236,270,233,319,270,270,
            270,315,270,270,270,318,321,270,270,316,
            314,237,238,270,270,239,241,37,270,303,
            270,270,308,270,240,299,270,270,21,270,
            270,243,242,245,270,270,270,246,270,250,
            293,270,244,248,270,247,249,301,278,270,
            276,270,270,273,270,251,253,277,270,252,
            270,270,254,270,311,270,310,255,270,270,
            270,256,270,258,270,270,257,270,270,270,
            333,286,289,270,270,270,259,292,270,262,
            260,270,270,283,261,309,270,264,270,263,
            270,284,312,270,270,271,324,270,265,270,
            302,270,270,270,267,270,270,266,298,270,
            270,270,313
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
