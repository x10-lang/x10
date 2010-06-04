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

    public final static int NUM_STATES = 270;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 434;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 80;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 81;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 26;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 353;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 354;
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
            12,4,5,6,9,4,3,8,5,5
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
            1,1,83,142,78,85,154,159,62,54,
            28,155,42,86,43,24,55,95,97,103,
            61,56,34,161,18,162,163,169,105,164,
            171,175,173,178,99,101,82,181,182,185,
            186,188,190,191,110,113,193,195,196,119,
            87,197,200,202,74,207,205,206,121,208,
            213,214,126,77,128,218,217,219,129,133,
            223,224,226,228,234,230,238,239,240,243,
            245,248,249,250,253,254,255,258,136,260,
            262,138,265,268,270,271,274,273,276,21,
            282,279,283,285,287,141,288,292,293,296,
            299,300,301,304,305,57,308,306,310,311,
            312,316,317,319,322,328,324,330,333,335,
            334,340,336,342,344,343,345,349,356,350,
            358,360,352,149,359,367,363,364,370,371,
            372,373,375,378,381,386,391,393,394,396,
            380,388,399,398,402,403,404,151,412,405,
            410,414,417,416,420,424,422,421,423,432,
            433,435,439,441,442,444,447,445,448,449,
            452,454,461,456,462,466,467,470,471,473,
            430,472,477,476,479,481,482,459,491,492,
            494,495,496,497,501,502,503,507,509,510,
            511,514,518,517,520,519,533,521,527,534,
            536,537,538,544,546,548,549,552,554,555,
            539,556,560,562,563,565,564,566,569,574,
            577,579,580,581,582,584,586,585,594,592,
            598,590,599,602,603,606,604,607,608,611,
            615,618,354,354
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,16,0,18,2,
            0,21,22,0,1,2,3,0,1,9,
            3,11,5,0,11,2,13,0,11,16,
            17,0,0,10,3,3,5,5,6,16,
            9,18,11,0,0,0,0,4,2,4,
            0,0,8,3,4,12,12,14,13,8,
            14,11,17,0,20,2,0,0,1,3,
            7,0,5,10,0,0,0,11,7,5,
            6,6,6,17,0,1,0,3,0,3,
            0,5,0,1,0,1,8,5,23,0,
            24,2,0,9,16,3,16,5,0,10,
            0,3,4,23,4,0,6,0,0,1,
            5,4,0,5,9,0,9,0,3,2,
            0,0,10,0,9,5,6,10,0,17,
            0,1,4,0,0,5,8,4,0,5,
            0,0,0,0,2,5,5,26,0,1,
            0,13,0,10,0,1,0,0,6,2,
            0,0,12,2,0,0,6,0,4,0,
            0,2,0,8,0,0,0,10,8,0,
            8,0,3,9,0,0,0,0,12,14,
            4,6,0,0,10,14,0,0,0,12,
            7,3,0,0,2,0,10,0,0,0,
            18,14,5,0,11,6,3,0,0,0,
            1,3,0,18,0,8,4,0,0,0,
            6,2,0,0,0,3,9,0,1,0,
            1,0,1,10,0,11,18,0,1,0,
            0,7,0,0,2,0,1,7,0,10,
            7,0,0,2,0,1,0,0,1,11,
            4,0,0,2,12,0,1,5,0,0,
            0,3,3,0,0,0,6,0,1,0,
            0,0,1,8,5,0,0,2,0,0,
            10,0,19,0,3,9,22,0,1,0,
            1,8,0,0,0,0,4,3,20,0,
            1,0,0,0,0,4,3,12,0,0,
            6,0,19,4,6,0,1,0,0,0,
            3,10,0,0,22,6,0,9,2,0,
            0,0,0,10,0,13,4,0,7,0,
            0,7,3,13,7,0,1,0,19,9,
            0,1,0,0,1,0,4,0,0,4,
            2,0,0,0,0,3,3,20,11,0,
            9,0,1,0,10,0,0,4,2,0,
            0,0,0,0,9,16,7,4,7,0,
            8,0,0,1,0,15,2,6,0,1,
            0,0,1,0,0,5,0,0,0,20,
            7,0,6,0,1,0,8,13,0,8,
            0,0,15,2,4,0,0,2,13,0,
            0,0,0,7,2,0,0,8,0,21,
            0,0,0,12,8,15,6,9,7,14,
            0,0,2,0,0,0,0,4,7,3,
            0,0,0,9,2,10,0,7,0,0,
            0,5,0,0,13,7,0,0,0,0,
            0,1,13,10,5,0,0,17,11,3,
            12,15,0,0,2,0,0,0,0,2,
            7,6,6,0,1,0,1,0,0,2,
            2,0,1,0,0,0,1,19,4,0,
            7,0,0,0,0,0,1,8,0,1,
            7,9,8,0,1,14,0,1,0,0,
            0,0,2,0,0,0,2,8,3,0,
            9,0,9,0,1,17,5,0,0,2,
            2,0,0,0,15,0,0,0,1,8,
            0,6,0,10,0,1,0,0,12,0,
            0,0,0,0,0,0,0,25,0,0,
            0,21,15,0,0,0,0,0,0,0,
            0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            354,93,101,102,84,89,91,94,90,96,
            95,88,92,97,100,99,87,354,86,151,
            354,85,98,354,130,129,131,354,115,233,
            113,232,114,354,128,148,132,354,112,127,
            126,354,354,149,117,124,119,123,125,147,
            120,146,118,354,354,354,354,380,247,143,
            354,354,134,141,142,110,404,111,144,109,
            248,140,145,10,133,186,354,354,104,197,
            188,354,103,187,354,354,354,196,164,106,
            105,121,182,431,354,135,354,136,354,138,
            354,137,354,139,354,156,162,395,122,354,
            181,172,354,155,379,174,410,175,354,173,
            354,180,179,163,361,354,360,354,3,204,
            195,198,8,203,194,354,199,354,221,224,
            354,354,205,354,222,237,238,225,354,206,
            354,289,269,354,354,416,268,107,354,116,
            354,354,354,354,153,150,152,353,354,154,
            354,108,354,157,354,159,354,354,160,161,
            354,354,158,166,354,354,165,354,167,354,
            354,170,354,168,354,354,354,169,171,354,
            176,354,184,177,354,354,354,354,183,178,
            189,190,354,354,363,185,354,354,354,191,
            193,202,354,354,207,354,201,354,354,354,
            192,200,402,354,208,211,210,354,354,354,
            213,212,354,209,354,385,214,354,354,354,
            411,377,354,354,354,217,215,47,220,354,
            396,354,223,218,354,219,216,354,389,354,
            354,226,354,354,230,354,231,228,354,227,
            229,354,354,234,354,381,354,354,239,235,
            236,354,354,240,382,354,375,241,354,5,
            354,242,243,354,354,354,244,354,430,354,
            354,354,356,249,250,354,354,251,354,354,
            426,354,245,354,254,252,246,354,255,354,
            257,256,354,354,354,354,258,259,253,354,
            433,354,354,354,354,434,261,260,354,354,
            263,354,388,264,376,354,398,354,354,354,
            265,267,354,354,262,266,354,270,392,354,
            354,354,354,391,354,390,423,354,273,354,
            354,274,282,272,275,354,277,354,271,276,
            354,278,354,354,280,354,279,354,354,281,
            284,354,354,354,354,287,288,283,285,354,
            286,354,290,354,291,354,354,293,369,354,
            354,354,354,354,294,292,295,296,355,354,
            418,354,354,384,354,419,297,403,354,409,
            354,354,374,354,354,298,45,354,354,307,
            299,354,300,354,303,354,371,372,354,302,
            354,354,301,422,304,354,354,305,424,354,
            354,354,354,420,406,354,354,306,354,313,
            354,354,354,308,309,366,312,311,417,310,
            354,354,314,354,354,73,354,316,315,318,
            354,354,354,317,387,428,354,368,354,354,
            354,319,354,354,367,320,354,354,354,354,
            354,325,373,397,322,354,354,400,321,326,
            323,370,354,354,324,354,354,354,354,394,
            327,328,329,354,413,354,414,354,354,330,
            331,354,332,354,354,354,393,334,412,354,
            333,354,354,354,354,354,339,335,354,340,
            338,336,337,354,432,421,354,341,354,354,
            354,354,386,354,354,354,346,342,345,354,
            343,354,344,354,408,358,347,354,354,348,
            429,354,354,354,415,354,354,354,365,349,
            354,383,354,405,354,351,354,354,407,354,
            354,354,354,354,354,354,354,350,354,354,
            354,378,425
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
