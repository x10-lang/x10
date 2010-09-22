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

    public final static int NUM_STATES = 224;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 364;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 68;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 69;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 25;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 295;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 296;
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
            5,6,8,7,7,8,9,6,6,6,
            4,4,10,6,6,5,6,4,5,6,
            9,4,3,4,3,3,4,5
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
            71,8,129,18,41,24,134,46,30,28,
            43,140,62,56,53,73,61,143,32,79,
            82,111,67,81,137,144,91,92,75,96,
            145,93,148,149,151,152,12,97,153,104,
            156,157,162,165,167,166,169,177,168,172,
            178,105,112,180,182,118,121,183,187,189,
            190,192,195,196,199,198,201,205,207,208,
            210,211,213,214,220,122,215,217,226,228,
            231,218,221,232,234,240,100,243,239,245,
            246,130,248,250,254,256,261,252,257,264,
            265,268,267,269,272,273,277,280,283,274,
            36,285,286,287,288,294,292,298,299,300,
            302,304,306,310,311,305,314,316,319,317,
            323,325,326,327,330,333,123,331,335,338,
            340,339,341,346,347,343,353,350,351,358,
            361,362,363,364,368,369,371,373,374,378,
            381,384,385,386,388,389,392,393,394,395,
            399,397,400,402,408,407,410,411,420,412,
            414,422,423,428,430,431,434,437,439,435,
            441,443,442,449,444,451,453,452,455,456,
            459,461,462,463,460,472,467,473,480,470,
            482,485,488,489,475,491,494,496,497,498,
            501,502,503,504,296,296
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,0,8,9,
            10,0,12,13,14,15,16,0,18,8,
            3,21,22,0,1,2,3,0,1,0,
            3,0,25,2,11,0,5,10,11,16,
            0,12,0,14,4,0,15,16,3,4,
            8,16,0,13,12,0,11,17,3,7,
            0,0,20,3,9,10,0,7,7,3,
            10,10,0,1,0,23,0,11,0,1,
            0,0,10,17,4,4,12,9,7,9,
            0,0,0,3,4,0,0,2,7,0,
            5,9,10,0,0,2,7,3,5,10,
            0,0,16,2,10,24,5,0,1,23,
            0,0,0,2,14,5,4,10,0,0,
            8,0,3,0,1,14,0,17,9,0,
            1,13,0,0,0,9,4,0,0,1,
            0,0,0,6,2,0,0,14,7,15,
            4,0,12,2,0,0,0,0,0,14,
            4,0,5,8,10,7,0,0,2,0,
            1,0,0,12,3,8,0,5,0,0,
            1,0,1,7,0,0,8,0,0,2,
            0,1,8,5,0,10,0,0,2,0,
            0,1,0,0,0,11,0,0,11,0,
            0,12,2,11,11,0,7,0,1,4,
            0,0,15,0,1,19,22,7,0,0,
            9,2,0,1,0,0,1,0,10,0,
            1,0,5,0,1,0,0,6,3,15,
            0,5,2,0,0,1,0,0,0,6,
            3,0,0,0,3,9,0,1,5,0,
            8,2,0,1,0,0,0,0,20,4,
            6,0,6,0,1,4,9,0,0,0,
            1,0,1,0,0,0,3,9,3,0,
            0,7,3,0,4,0,0,20,0,4,
            4,3,0,1,0,0,0,3,3,0,
            0,5,0,1,0,22,7,0,0,0,
            0,11,0,9,5,0,0,2,6,0,
            0,13,0,4,4,9,19,0,6,19,
            0,0,0,0,2,5,9,0,0,6,
            0,1,0,0,13,8,8,0,6,6,
            0,4,2,0,0,0,2,0,0,1,
            5,0,0,0,0,2,0,10,0,0,
            8,0,6,12,21,7,0,0,7,0,
            0,0,13,0,20,8,7,6,8,0,
            1,0,0,2,18,3,13,0,1,0,
            0,2,2,0,0,2,0,1,0,1,
            0,0,0,0,10,3,6,6,0,1,
            0,0,0,2,0,0,13,5,0,0,
            0,0,0,1,6,11,0,17,8,0,
            9,0,0,18,0,4,17,8,6,0,
            1,0,1,0,0,19,2,0,0,2,
            0,3,18,0,1,0,0,0,2,9,
            0,0,0,0,7,10,0,5,8,0,
            0,0,0,12,0,0,0,0,0,0,
            0,0,0,0,21,0,0,0,0,0,
            0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            296,84,74,88,76,75,86,296,79,80,
            78,296,83,81,77,87,72,296,85,151,
            90,82,73,296,97,96,98,296,107,296,
            108,296,295,125,95,296,126,106,105,94,
            296,104,296,103,92,296,127,124,102,101,
            109,220,296,93,326,296,100,91,116,118,
            296,296,110,122,115,114,296,121,113,132,
            120,112,296,119,296,117,296,131,296,129,
            296,296,313,359,133,362,141,128,361,134,
            296,296,296,138,137,296,296,142,140,296,
            143,146,145,33,296,154,200,168,155,199,
            296,296,333,169,167,139,170,5,174,152,
            2,296,296,189,130,176,244,173,296,296,
            245,296,204,296,99,190,296,175,203,296,
            111,89,296,296,296,135,123,296,296,148,
            296,296,296,147,153,296,296,136,150,144,
            157,296,149,158,296,296,296,296,296,156,
            160,296,164,161,159,162,296,296,163,296,
            171,296,296,165,172,166,296,177,296,296,
            360,296,358,178,296,296,363,296,296,181,
            296,182,179,354,296,180,296,296,184,296,
            296,347,296,296,296,183,296,296,185,296,
            296,348,334,186,187,296,188,296,194,193,
            296,296,195,296,197,192,191,336,296,296,
            196,198,296,325,296,25,202,296,324,296,
            206,296,205,296,314,15,296,209,207,201,
            296,210,208,296,296,306,296,296,296,211,
            212,296,296,296,215,213,296,217,219,296,
            216,218,296,364,296,296,296,296,214,352,
            221,296,222,296,224,225,223,296,296,296,
            228,296,229,296,296,296,230,227,231,296,
            296,234,232,296,233,296,296,226,296,236,
            238,237,296,239,296,296,296,240,241,296,
            296,242,296,317,296,235,243,296,296,296,
            296,246,296,247,308,296,296,304,250,296,
            296,307,296,251,252,249,248,296,300,305,
            59,296,296,296,351,356,253,296,296,350,
            296,345,296,296,353,254,346,296,344,255,
            296,256,257,296,296,41,259,296,296,332,
            338,296,296,296,296,328,296,260,296,296,
            262,22,264,261,258,323,296,296,265,296,
            296,296,320,296,263,316,267,303,268,296,
            269,296,296,299,266,270,302,296,271,296,
            296,272,273,296,296,274,296,341,296,340,
            296,296,296,296,275,277,276,278,296,279,
            296,296,296,312,296,296,322,315,296,296,
            296,296,296,284,281,280,296,319,282,296,
            283,296,296,309,296,339,342,288,286,296,
            287,296,310,296,296,285,297,296,296,357,
            296,289,343,296,331,296,296,296,292,290,
            296,296,296,296,349,291,296,327,293,296,
            296,296,296,330,296,296,296,296,296,296,
            296,296,296,296,335
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
