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

    public final static int NUM_STATES = 271;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 437;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 81;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 82;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 26;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 355;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 356;
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
            5,4,2,8,3,3,3,2,3,2,
            10,6,6,6,5,7,6,6,7,6,
            4,5,4,11,3,2,4,4,10,6,
            4,8,6,5,4,5,5,5,8,7,
            2,4,7,5,5,7,3,4,6,2,
            10,6,10,9,6,3,4,8,7,7,
            9,5,6,6,6,6,8,6,5,6,
            12,4,5,6,9,4,3,8,5,5,
            6
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
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,84,112,90,38,32,150,51,
            54,28,161,42,81,67,24,49,97,60,
            105,71,76,46,162,158,164,168,171,20,
            83,173,174,50,176,99,78,177,180,179,
            183,182,188,189,192,109,114,193,194,195,
            120,116,196,199,198,77,203,205,210,121,
            206,211,213,103,85,126,217,215,219,128,
            132,221,225,226,227,230,228,235,237,238,
            241,242,246,247,249,250,252,255,257,134,
            259,262,143,264,267,269,270,272,273,280,
            146,274,282,283,284,286,142,287,289,292,
            298,300,301,296,294,305,151,306,308,309,
            310,311,315,318,320,321,322,325,329,331,
            334,326,338,332,342,347,336,343,348,351,
            353,356,355,360,131,362,363,364,366,367,
            368,371,370,373,376,379,384,387,390,391,
            393,395,382,399,396,400,403,405,154,411,
            404,18,414,415,409,416,420,421,422,425,
            426,429,427,438,434,439,441,443,445,446,
            447,448,449,452,454,455,461,464,460,465,
            469,470,471,474,475,476,477,479,481,478,
            480,489,493,492,494,495,500,501,504,505,
            508,509,511,512,513,516,518,517,525,519,
            530,532,534,535,536,542,544,546,547,550,
            552,553,537,554,558,560,561,562,565,563,
            567,575,577,569,573,580,579,583,584,588,
            591,594,595,585,596,601,602,604,603,605,
            606,608,613,611,356,356
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,16,0,18,0,
            1,21,22,0,1,2,3,0,1,10,
            3,0,5,16,11,4,13,0,11,16,
            17,0,5,6,3,0,5,2,0,0,
            0,10,11,0,9,6,8,4,8,0,
            12,16,3,18,5,12,0,14,20,3,
            0,5,6,3,4,0,0,0,2,4,
            0,11,0,7,0,9,6,3,13,0,
            1,9,17,16,5,11,0,1,0,3,
            23,17,0,23,0,1,8,5,0,5,
            2,0,10,0,16,0,3,9,5,0,
            0,6,3,4,4,0,6,0,1,4,
            0,0,5,0,4,10,3,26,8,24,
            9,0,0,10,2,0,5,6,17,0,
            0,9,2,0,1,10,11,0,5,2,
            0,0,13,0,14,5,5,0,5,2,
            0,1,0,0,1,0,0,2,0,0,
            2,0,0,7,12,6,4,0,0,8,
            2,0,0,0,0,0,9,0,0,8,
            8,3,0,10,0,0,4,12,14,0,
            0,14,0,9,0,6,0,12,0,7,
            0,3,2,9,0,0,0,0,18,0,
            14,5,3,6,0,11,0,0,1,3,
            0,0,8,18,4,0,0,6,0,0,
            2,0,3,0,0,10,0,1,0,1,
            9,0,1,0,18,11,0,1,0,0,
            7,0,0,0,2,2,7,9,7,0,
            1,0,0,0,1,0,0,1,0,4,
            2,0,11,0,12,0,5,0,1,0,
            0,6,3,3,0,0,1,0,0,0,
            0,1,19,5,0,8,2,0,9,0,
            0,0,1,3,0,0,22,10,0,1,
            0,0,8,0,4,0,3,0,1,20,
            0,0,0,12,19,4,0,0,6,3,
            0,1,0,6,0,0,4,22,3,0,
            6,0,0,0,2,0,0,0,9,0,
            0,10,0,4,9,0,13,7,0,7,
            13,0,7,0,1,19,0,1,10,0,
            0,1,0,4,0,0,4,3,0,0,
            2,20,0,0,0,3,11,3,0,10,
            0,1,9,0,0,0,2,4,10,0,
            0,0,7,4,0,0,0,7,0,1,
            0,6,8,0,1,9,15,0,0,2,
            0,1,0,5,0,0,0,0,0,7,
            6,0,1,0,0,8,8,4,13,0,
            0,15,2,0,0,2,7,13,0,0,
            0,2,8,0,0,0,0,0,0,0,
            0,2,8,15,6,12,10,7,0,14,
            20,0,0,0,0,7,4,3,21,0,
            0,10,9,0,0,2,7,0,0,5,
            0,0,0,13,7,0,0,0,0,1,
            9,13,5,11,0,0,2,17,12,0,
            15,0,3,0,0,0,0,2,7,6,
            6,0,1,0,1,0,0,2,2,0,
            1,0,0,0,1,19,4,0,7,0,
            0,0,0,1,0,8,0,1,0,8,
            10,7,0,14,0,1,0,1,0,0,
            8,2,0,0,0,17,3,0,10,2,
            0,1,10,0,0,0,2,2,5,15,
            0,0,0,0,0,0,1,0,8,6,
            0,9,0,1,0,0,12,0,0,0,
            0,0,0,0,0,15,25,0,21,0,
            0,0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            356,94,102,103,85,90,92,95,91,96,
            97,89,93,98,101,100,88,356,87,356,
            157,86,99,356,131,130,132,356,116,156,
            114,356,115,294,129,108,133,356,113,128,
            127,356,107,106,118,356,120,149,356,356,
            356,121,119,356,150,161,135,382,110,356,
            406,148,139,147,138,111,356,112,134,125,
            356,124,126,142,143,356,10,356,187,144,
            356,141,356,189,356,188,122,198,145,356,
            105,158,146,412,104,197,356,136,356,137,
            164,433,356,123,356,140,163,196,356,397,
            173,356,195,356,381,356,175,174,176,356,
            356,183,181,180,363,356,362,3,205,199,
            356,8,204,356,271,200,222,355,270,182,
            206,356,356,223,225,356,238,239,207,356,
            356,226,248,356,291,234,233,356,418,152,
            356,356,109,356,249,117,151,356,153,154,
            356,155,356,356,160,356,356,162,356,356,
            167,356,356,165,159,166,168,356,356,169,
            171,356,356,356,356,356,170,356,356,172,
            177,185,356,178,356,356,190,184,179,356,
            356,186,356,365,356,191,356,192,356,194,
            356,203,208,202,356,356,356,356,193,356,
            201,404,211,212,356,209,356,356,214,213,
            356,356,387,210,215,356,356,413,356,356,
            379,356,218,356,356,216,47,221,356,398,
            219,356,224,356,217,220,356,391,356,356,
            227,356,356,356,231,235,229,228,230,356,
            232,356,356,356,383,356,356,240,356,237,
            241,356,236,356,384,356,242,356,377,356,
            5,245,243,244,356,356,432,356,356,356,
            356,358,246,251,356,250,252,356,428,356,
            356,356,256,255,356,356,247,253,356,258,
            356,356,257,356,259,356,260,356,435,254,
            356,356,356,261,390,262,356,356,265,263,
            356,400,356,378,356,356,266,264,267,356,
            268,356,356,356,394,356,356,356,269,356,
            356,272,356,425,393,356,392,275,356,276,
            274,356,277,356,279,273,356,280,278,356,
            356,282,356,281,356,356,283,284,356,356,
            286,285,356,356,356,289,287,290,356,288,
            356,292,293,356,356,356,371,295,296,356,
            356,356,297,298,356,356,80,357,356,386,
            356,405,420,356,411,437,421,356,356,299,
            356,376,356,300,45,356,356,356,356,301,
            302,356,305,356,356,373,304,306,374,356,
            356,303,424,356,356,307,422,426,356,356,
            356,408,308,356,356,356,356,356,356,356,
            356,316,311,368,314,310,313,419,356,312,
            309,356,356,73,356,317,318,320,315,356,
            356,319,430,356,356,389,370,356,356,321,
            356,356,356,369,322,356,356,356,356,327,
            399,375,324,323,356,356,326,402,325,356,
            372,356,328,356,356,356,356,396,329,330,
            331,356,415,356,416,356,356,332,333,356,
            334,356,356,356,395,336,414,356,335,356,
            356,356,356,341,356,337,356,342,356,339,
            338,340,356,423,356,434,356,343,356,356,
            344,388,356,356,356,360,347,356,345,348,
            356,410,346,356,356,356,350,431,349,417,
            356,356,356,356,356,356,367,356,351,385,
            356,407,356,353,356,356,409,356,356,356,
            356,356,356,356,356,427,352,356,380
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
