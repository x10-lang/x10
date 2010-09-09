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
            8,2,6,5,2,6,6,5,5,4,
            5,5,7,5,8,3,7,2,4,7,
            6,5,5,7,6,3,7,6,6,4,
            4,2,10,6,2,10,10,9,5,6,
            3,4,11,4,5,6,8,2,7,7,
            8,9,5,6,6,4,4,10,6,6,
            6,5,6,4,5,6,4,3,4,3,
            3,4,5
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
            1,1,1,1,1,76,118,31,62,34,
            24,75,53,55,29,142,61,144,77,39,
            83,85,63,148,43,19,89,150,79,87,
            12,151,95,153,100,154,155,158,101,161,
            159,162,49,103,102,168,163,72,171,172,
            175,179,178,183,181,184,188,192,182,193,
            104,112,195,198,115,109,197,199,202,206,
            208,210,211,213,220,214,216,222,223,224,
            227,229,230,121,231,232,233,238,241,242,
            243,247,249,254,248,258,126,259,261,262,
            263,266,129,264,269,275,278,128,273,280,
            283,284,287,282,286,292,293,296,298,301,
            294,299,304,307,309,314,316,319,312,139,
            324,326,327,328,332,333,320,334,337,340,
            335,344,345,347,349,351,352,356,357,137,
            359,362,363,364,366,367,368,373,369,376,
            379,377,384,386,389,390,391,393,395,396,
            398,401,399,408,400,411,412,415,416,310,
            414,418,423,419,426,427,429,430,433,434,
            436,437,440,445,441,448,449,453,455,458,
            462,465,467,463,469,471,472,454,476,473,
            479,480,481,482,484,485,487,489,492,494,
            495,499,501,503,506,507,509,512,513,517,
            518,520,521,522,525,528,531,532,534,535,
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
            9,21,22,0,1,2,3,9,0,1,
            0,3,0,0,11,7,13,4,0,11,
            17,3,0,13,2,7,13,9,0,11,
            8,18,0,5,0,3,4,15,4,17,
            0,0,0,11,3,3,12,5,14,7,
            10,0,12,2,0,1,0,6,0,8,
            20,5,0,7,0,1,0,5,0,11,
            4,7,4,5,0,9,18,3,4,0,
            0,0,0,0,5,23,3,7,0,9,
            7,0,10,2,0,1,8,0,17,8,
            0,7,2,24,23,0,18,0,0,2,
            5,3,7,0,14,8,0,9,0,1,
            4,0,25,0,1,7,10,0,7,0,
            0,4,0,0,0,3,2,0,0,1,
            0,0,0,14,14,12,6,0,6,2,
            0,0,15,12,0,4,2,0,0,2,
            0,0,0,0,14,7,4,0,5,2,
            10,0,0,12,0,1,0,0,0,8,
            3,0,10,5,8,0,1,0,1,0,
            0,10,0,0,2,0,7,2,8,0,
            1,0,0,0,11,4,0,1,0,0,
            0,0,0,11,5,12,4,0,1,11,
            0,0,0,0,2,5,0,0,0,2,
            19,3,22,0,1,9,15,0,0,1,
            0,0,0,0,7,0,1,7,0,1,
            0,8,0,11,0,1,15,0,6,0,
            3,0,0,0,1,0,0,8,6,3,
            9,0,0,0,3,0,1,0,0,2,
            0,1,10,0,6,20,0,4,0,0,
            17,0,6,0,1,0,1,9,0,0,
            9,12,4,0,1,0,0,0,3,3,
            3,0,0,0,0,4,0,5,5,0,
            4,22,3,0,0,1,0,4,0,3,
            0,0,4,3,20,0,0,1,0,8,
            5,0,0,0,2,0,0,0,0,11,
            9,0,0,8,2,0,0,9,0,13,
            4,6,19,0,6,0,19,4,0,0,
            0,2,0,8,0,0,6,0,0,0,
            0,13,4,6,10,10,6,0,16,2,
            0,0,2,0,0,0,1,0,0,8,
            21,7,0,10,2,0,0,1,0,0,
            5,14,0,0,6,0,0,5,20,0,
            0,5,13,10,0,1,6,0,0,10,
            2,16,0,0,0,3,2,0,5,2,
            13,0,0,2,0,1,0,1,0,7,
            0,0,0,3,6,0,1,6,0,0,
            0,0,2,0,0,13,0,8,0,1,
            6,0,11,0,0,9,18,4,0,16,
            0,1,0,1,6,0,0,1,0,18,
            2,0,0,19,3,10,0,0,1,0,
            0,0,2,2,0,9,7,0,16,5,
            0,0,1,0,0,0,0,10,0,0,
            0,8,12,0,0,0,0,0,0,0,
            0,0,0,0,0,21,0,0,0,0,
            0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            312,90,79,94,81,85,92,83,80,86,
            84,312,89,87,82,93,91,77,312,138,
            143,88,78,312,104,102,105,137,312,114,
            312,115,312,312,101,113,103,98,312,112,
            100,125,312,95,134,123,99,124,312,122,
            135,97,312,158,312,109,108,136,360,133,
            312,312,312,107,96,131,111,130,110,129,
            117,35,344,163,312,106,312,165,312,164,
            118,121,312,120,312,128,312,127,312,140,
            141,330,383,382,312,142,380,146,145,312,
            312,312,312,312,149,126,179,153,2,154,
            178,312,159,180,5,185,187,312,353,181,
            312,184,199,148,160,312,186,312,312,221,
            212,217,211,312,200,222,312,216,312,241,
            259,312,311,312,119,365,260,312,116,312,
            312,132,312,312,312,147,151,312,312,156,
            312,312,312,139,144,150,155,312,162,161,
            312,312,152,157,312,167,168,312,312,170,
            312,312,312,312,166,169,171,312,173,174,
            172,312,312,176,312,182,312,312,312,175,
            183,312,177,189,188,312,381,312,379,312,
            312,384,312,312,191,312,190,194,376,312,
            192,312,312,312,193,195,312,368,312,312,
            312,312,312,196,198,369,203,312,204,197,
            312,312,312,312,354,356,312,312,312,209,
            202,207,201,312,208,206,205,312,312,343,
            312,312,312,312,210,26,215,342,312,219,
            312,218,312,214,312,331,213,16,223,312,
            220,312,312,312,322,312,312,224,225,226,
            227,312,312,312,229,312,231,312,312,232,
            312,385,230,312,234,228,312,374,312,312,
            233,312,235,312,237,312,238,236,312,312,
            240,275,239,312,242,312,312,312,243,244,
            245,312,312,312,312,246,312,247,351,312,
            249,248,250,312,312,253,312,252,312,254,
            312,312,255,256,251,312,312,334,312,257,
            258,312,312,312,326,312,312,312,312,261,
            262,312,312,324,320,312,312,264,312,323,
            266,265,263,312,316,65,321,267,312,312,
            312,373,312,378,312,312,372,312,312,312,
            312,375,270,366,268,367,269,312,371,271,
            312,45,273,312,312,312,352,312,312,358,
            272,274,312,276,346,312,312,340,312,312,
            341,277,23,312,279,312,312,280,278,312,
            312,282,337,333,312,284,319,312,312,283,
            315,281,312,312,312,285,286,312,293,287,
            318,312,312,288,312,362,312,361,312,289,
            312,312,312,291,290,312,294,292,312,312,
            312,312,329,312,312,339,312,332,312,298,
            296,312,295,312,312,297,336,359,312,325,
            312,301,312,302,300,312,312,327,312,363,
            313,312,312,299,304,303,312,312,350,312,
            312,312,307,308,312,305,306,312,364,370,
            312,312,348,312,312,312,312,309,312,312,
            312,345,349,312,312,312,312,312,312,312,
            312,312,312,312,312,355
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
