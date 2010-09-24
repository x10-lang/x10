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

    public final static int NUM_STATES = 205;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 337;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 64;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 65;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 24;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 272;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 273;
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
            10,6,2,10,9,6,3,4,4,5,
            6,8,7,7,8,9,6,6,6,4,
            6,6,5,6,4,5,9,4,3,4,
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
            1,1,1,1,1,1,67,108,118,28,
            17,24,133,34,36,40,46,135,66,49,
            47,68,53,8,27,74,79,114,61,80,
            137,138,87,139,92,140,85,141,142,144,
            148,81,149,98,150,67,158,153,161,159,
            162,170,166,167,173,99,105,174,177,111,
            101,176,178,183,186,188,184,190,191,195,
            198,194,201,202,204,206,209,113,207,210,
            208,218,213,221,224,225,227,116,233,231,
            222,235,122,237,243,245,247,238,248,251,
            249,252,257,258,259,261,262,267,263,269,
            272,273,274,275,276,281,283,284,285,290,
            294,296,297,298,302,286,304,309,310,315,
            317,318,319,322,325,120,323,305,311,327,
            328,330,333,336,337,339,338,347,340,346,
            352,349,353,356,358,360,361,367,363,368,
            371,372,374,375,377,378,380,382,381,383,
            389,391,392,393,395,400,401,403,404,410,
            413,416,419,421,417,423,424,428,412,431,
            432,433,434,436,438,439,440,442,446,443,
            451,453,455,457,461,460,464,466,467,468,
            470,273,273
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,0,8,9,
            10,4,12,13,14,15,0,17,18,0,
            4,21,22,0,1,2,0,0,2,13,
            3,5,16,0,11,0,3,4,15,0,
            1,15,3,17,11,0,0,12,0,14,
            11,3,0,7,9,3,8,12,10,7,
            0,0,10,3,19,0,0,0,1,23,
            4,11,7,0,1,10,16,10,0,0,
            0,8,4,4,0,7,0,8,0,3,
            4,0,8,2,10,15,5,0,0,2,
            0,3,5,23,0,5,2,0,10,5,
            0,1,0,0,2,0,16,0,0,0,
            10,0,7,4,3,10,14,14,9,8,
            13,24,0,1,0,1,0,0,0,0,
            0,0,1,0,8,7,6,0,0,0,
            2,14,0,0,7,12,17,0,0,2,
            0,0,10,14,4,0,0,9,7,0,
            5,2,0,0,1,0,0,0,12,3,
            5,9,0,0,7,0,1,0,1,0,
            0,9,9,0,0,5,2,0,1,10,
            0,0,2,0,11,0,0,0,0,0,
            0,4,0,12,11,7,11,0,1,7,
            0,0,2,0,0,1,0,21,2,20,
            0,8,0,1,0,1,0,0,17,2,
            10,5,0,1,0,1,0,0,0,3,
            0,0,1,6,6,5,0,0,0,3,
            0,0,0,3,2,8,0,1,0,1,
            9,0,0,0,0,0,5,19,4,6,
            0,6,0,0,0,0,4,15,8,0,
            1,0,8,0,1,0,0,0,3,3,
            3,0,19,0,0,4,21,4,0,0,
            0,3,8,4,0,1,0,0,0,3,
            3,0,0,5,0,1,0,0,7,0,
            20,5,0,11,2,0,0,0,0,0,
            13,4,6,8,6,0,0,8,0,20,
            4,0,0,2,6,0,1,0,13,0,
            0,9,0,6,4,6,0,0,2,2,
            0,0,1,0,0,5,0,0,2,0,
            0,0,0,9,22,12,7,6,0,7,
            0,0,0,13,0,1,19,9,7,0,
            0,9,0,0,2,6,3,0,18,0,
            1,0,0,13,2,0,0,2,0,1,
            0,1,0,0,13,3,10,0,1,6,
            0,0,0,0,2,0,5,0,0,0,
            1,0,0,6,11,0,16,9,6,4,
            0,1,0,18,0,1,0,16,2,0,
            0,9,2,0,1,0,0,0,2,0,
            0,0,0,0,5,10,0,18,0,12,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            273,80,70,84,72,71,82,273,76,75,
            74,117,79,77,73,68,273,83,81,273,
            88,69,78,273,93,92,273,273,119,89,
            86,120,87,273,91,273,97,96,90,273,
            101,118,102,121,95,273,273,99,273,98,
            100,110,273,112,103,116,109,303,108,115,
            273,273,114,126,104,273,273,273,113,111,
            148,125,107,273,123,106,332,290,273,273,
            273,122,335,127,273,334,273,128,273,132,
            131,273,138,134,137,310,135,33,273,145,
            2,159,146,143,273,167,160,273,158,161,
            5,165,273,273,179,273,166,273,273,273,
            164,273,189,230,193,188,180,124,231,192,
            85,272,273,94,273,105,273,273,273,273,
            273,273,140,273,129,133,139,273,273,273,
            144,130,273,273,142,141,136,273,273,149,
            273,273,150,147,151,273,273,152,153,273,
            155,154,273,273,162,273,273,273,156,163,
            168,157,273,273,169,273,333,273,331,273,
            273,336,170,273,273,328,172,273,173,171,
            273,273,175,273,174,273,273,273,273,273,
            273,183,273,323,176,178,177,273,184,312,
            273,273,311,273,273,186,273,181,187,182,
            273,185,273,302,25,191,273,273,190,197,
            301,194,273,195,273,291,15,273,273,196,
            273,273,283,198,200,199,273,273,273,201,
            273,273,273,204,207,202,273,206,273,337,
            205,273,273,273,273,273,208,203,326,209,
            273,210,273,273,273,273,212,329,211,273,
            215,273,214,273,216,273,273,273,217,218,
            219,273,213,273,273,220,221,222,273,273,
            273,223,233,224,273,225,273,273,273,226,
            227,273,273,228,273,294,273,273,229,273,
            234,285,273,232,281,273,273,273,273,273,
            284,237,236,235,277,273,273,239,273,282,
            238,273,273,325,324,273,321,273,327,273,
            273,322,273,320,241,240,273,273,242,244,
            40,273,309,273,273,314,273,273,305,273,
            273,273,22,246,243,245,300,248,273,249,
            273,273,273,297,273,253,247,293,251,273,
            273,252,273,273,276,280,254,273,250,273,
            255,273,273,279,256,273,273,257,273,317,
            273,316,273,273,299,259,258,273,261,260,
            273,273,273,273,289,273,292,273,273,273,
            265,273,273,263,262,273,296,264,266,315,
            273,267,273,286,273,287,273,318,274,273,
            273,268,330,273,308,273,273,273,270,273,
            273,273,273,273,304,269,273,319,273,307
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
