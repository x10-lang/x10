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

    public final static int NUM_STATES = 235;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 385;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 73;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 74;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 25;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 311;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 312;
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
            5,4,2,8,3,3,3,2,2,10,
            6,6,6,5,7,6,6,7,6,4,
            5,4,11,2,4,4,10,4,8,6,
            5,4,5,5,5,8,7,2,4,7,
            5,5,7,3,4,6,2,10,6,10,
            9,6,3,4,8,7,7,9,5,6,
            6,6,6,5,6,4,5,6,4,3,
            5,5,6
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
            1,1,1,1,1,76,75,136,72,138,
            139,49,29,143,145,39,78,52,24,44,
            148,81,62,67,43,147,153,154,157,19,
            21,161,159,163,166,69,107,167,168,170,
            174,169,175,180,177,87,31,181,184,90,
            91,185,186,188,68,98,191,192,193,97,
            99,105,194,196,206,111,108,200,195,205,
            213,214,215,217,220,221,222,223,227,230,
            234,228,232,239,119,241,244,121,246,247,
            249,250,251,254,258,259,260,262,263,266,
            120,267,273,265,276,277,278,280,130,283,
            281,285,286,289,290,292,295,300,294,303,
            305,296,309,313,311,306,316,318,319,322,
            324,325,329,131,331,332,333,335,336,338,
            341,342,347,349,351,353,358,360,362,354,
            361,366,367,368,133,376,363,369,379,378,
            382,385,384,383,386,392,394,397,395,403,
            399,401,407,408,409,411,412,410,414,422,
            415,417,427,418,430,426,431,432,434,435,
            439,443,444,446,447,451,452,453,459,458,
            460,461,462,463,467,469,475,477,472,481,
            482,487,489,491,492,495,496,485,497,499,
            502,503,505,510,513,507,515,518,516,520,
            523,521,529,526,530,532,533,534,535,538,
            312,312
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,0,12,13,14,15,16,17,0,1,
            0,21,22,0,1,2,3,9,0,1,
            0,3,12,3,11,7,13,7,0,11,
            17,3,0,0,2,7,0,9,0,11,
            8,0,4,10,3,12,5,15,7,17,
            12,0,14,20,3,4,0,0,0,2,
            4,0,11,6,0,8,5,0,7,13,
            0,1,5,0,18,17,0,7,2,0,
            0,23,3,4,8,5,0,0,0,25,
            23,4,5,7,0,9,0,0,4,11,
            0,1,6,9,24,8,18,7,0,0,
            0,3,2,0,5,18,7,9,8,0,
            0,2,0,1,4,0,1,0,0,7,
            10,4,0,14,0,1,0,0,2,7,
            3,13,0,0,0,2,0,1,0,7,
            0,1,0,5,2,0,0,0,0,0,
            4,2,5,0,0,10,0,4,10,0,
            0,2,8,0,0,0,10,0,3,9,
            0,0,0,0,0,0,12,14,6,0,
            0,14,12,8,0,0,15,14,3,15,
            11,7,0,0,0,3,0,1,5,0,
            0,0,0,4,10,5,0,0,2,0,
            9,0,3,0,1,8,0,15,0,1,
            0,1,11,0,1,0,0,1,0,0,
            0,6,2,0,1,6,8,0,0,0,
            2,0,0,1,0,0,0,1,11,4,
            11,7,0,12,2,0,0,0,3,0,
            0,5,0,1,0,0,1,7,0,0,
            2,0,8,0,0,0,19,3,9,0,
            1,22,0,10,0,0,4,3,0,1,
            0,20,0,3,19,0,4,0,0,1,
            5,0,5,0,0,4,3,22,0,5,
            0,0,0,2,0,0,8,0,0,9,
            0,0,8,6,4,13,0,6,0,1,
            0,1,0,0,19,9,4,0,1,0,
            0,0,0,4,3,0,0,0,0,3,
            3,11,4,20,9,0,1,0,0,17,
            2,0,0,0,0,0,9,6,4,6,
            5,0,10,0,0,2,0,1,0,8,
            0,7,0,1,6,5,0,0,0,0,
            0,0,1,0,0,2,0,0,10,13,
            10,0,13,16,10,0,0,6,2,0,
            0,0,16,0,0,10,5,20,0,6,
            2,12,0,0,14,0,0,4,6,3,
            0,0,0,8,2,21,6,0,0,0,
            0,0,0,6,13,7,0,0,0,8,
            2,0,13,11,0,1,0,6,18,3,
            0,0,16,2,0,5,0,1,0,1,
            0,0,2,2,0,0,0,1,0,4,
            6,0,0,19,0,1,0,9,6,0,
            1,10,0,1,0,0,2,0,3,0,
            0,2,0,1,18,0,9,7,0,0,
            2,0,0,0,0,1,5,0,0,10,
            8,16,0,0,0,12,0,0,0,0,
            0,0,0,0,0,0,0,0,21,0,
            0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            312,86,93,94,77,83,87,81,88,89,
            82,312,85,84,92,79,91,80,312,142,
            312,78,90,312,120,119,121,141,312,105,
            312,103,143,160,118,104,122,161,312,102,
            117,108,312,312,135,110,312,111,312,109,
            136,312,336,124,115,359,116,133,114,134,
            100,312,101,123,128,129,312,9,312,171,
            130,312,127,173,312,172,96,312,97,131,
            312,126,112,312,132,365,312,350,158,312,
            312,148,165,164,159,167,312,312,312,311,
            113,319,318,178,312,177,312,8,180,179,
            3,186,149,181,166,187,382,185,312,312,
            312,201,204,312,216,188,215,202,205,312,
            312,224,312,261,244,312,95,312,312,371,
            243,98,312,225,312,107,312,312,137,106,
            125,99,312,312,312,139,312,140,312,138,
            312,144,312,145,146,312,312,312,312,312,
            150,152,151,312,312,147,312,153,154,312,
            312,156,155,312,312,312,157,312,169,162,
            312,312,312,312,312,312,168,163,176,312,
            312,170,174,183,312,312,175,182,184,190,
            189,357,312,312,312,191,312,193,192,312,
            312,312,312,194,340,366,312,312,334,312,
            195,312,197,312,332,198,312,196,44,200,
            312,351,199,312,203,312,312,344,312,312,
            312,206,209,312,210,208,207,312,312,312,
            212,312,312,337,312,312,312,217,211,214,
            213,219,312,338,218,5,312,312,220,312,
            312,221,312,381,312,312,314,226,312,312,
            227,312,378,312,312,312,222,230,228,312,
            231,223,312,232,312,312,233,234,312,383,
            312,229,312,236,343,312,235,312,312,353,
            238,312,333,312,312,239,240,237,312,241,
            312,312,312,347,312,312,242,312,312,245,
            312,312,346,247,376,345,312,248,312,250,
            312,251,312,312,246,249,252,312,253,312,
            312,312,312,254,255,312,312,312,312,259,
            260,257,264,256,258,312,262,312,312,263,
            326,312,312,312,312,312,265,266,267,313,
            358,72,373,312,312,268,312,364,312,385,
            42,269,312,331,270,271,312,312,312,312,
            312,312,274,312,312,375,312,312,328,329,
            273,312,377,272,275,312,312,374,361,312,
            312,312,323,312,312,278,280,276,312,372,
            282,277,312,312,279,67,312,284,283,285,
            312,312,312,380,342,281,325,312,312,312,
            312,312,312,287,324,286,312,312,312,352,
            289,312,330,288,312,290,312,292,355,291,
            312,312,327,349,312,293,312,368,312,369,
            312,312,294,295,312,312,312,348,312,367,
            296,312,312,297,312,301,312,298,300,312,
            302,299,312,303,312,312,341,312,305,312,
            312,306,312,363,316,312,304,307,312,312,
            308,312,312,312,312,322,339,312,312,309,
            360,370,312,312,312,362,312,312,312,312,
            312,312,312,312,312,312,312,312,335
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
