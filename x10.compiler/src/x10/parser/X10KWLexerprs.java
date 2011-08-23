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

    public final static int NUM_STATES = 209;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 53;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 339;
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

    public final static int ACCEPT_ACTION = 275;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 276;
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
            5,9,4,3,4,3,3,4,4,5,
            7,7,10
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
            1,1,1,1,1,66,97,114,116,70,
            18,24,126,29,55,37,39,43,73,46,
            72,82,53,128,58,130,84,132,27,133,
            64,86,134,135,87,136,137,141,93,10,
            143,146,148,150,151,98,152,154,161,163,
            167,162,169,171,174,175,176,102,105,155,
            65,104,177,181,180,184,185,190,192,188,
            195,197,203,206,199,209,196,204,212,111,
            96,213,214,218,216,220,222,224,112,227,
            229,231,115,234,235,240,238,243,246,244,
            247,255,248,28,257,258,250,262,265,259,
            268,270,266,272,273,274,277,280,278,287,
            289,291,292,293,298,282,299,300,305,306,
            309,311,312,313,315,317,318,319,322,320,
            323,324,329,330,331,334,341,340,342,335,
            349,345,353,354,355,356,358,357,364,365,
            367,369,372,371,374,375,376,377,380,383,
            384,387,390,385,399,401,402,393,404,407,
            408,411,413,415,416,417,418,422,423,425,
            427,426,431,433,434,432,435,438,440,444,
            448,445,452,450,456,460,457,463,461,466,
            465,469,470,473,276,276
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,0,
            10,11,12,13,14,15,7,0,18,19,
            3,21,22,0,1,2,0,0,0,3,
            13,3,4,16,11,9,0,1,0,11,
            4,18,0,1,17,0,4,11,10,4,
            12,6,0,8,0,17,4,0,6,2,
            0,9,5,0,0,1,12,4,14,0,
            6,0,0,4,11,6,19,13,6,16,
            9,0,1,0,1,0,0,6,3,3,
            4,8,0,8,23,0,0,0,6,2,
            8,0,5,0,0,4,2,6,5,5,
            0,0,2,0,0,0,21,6,4,16,
            9,25,8,10,14,0,1,0,13,0,
            3,0,0,0,0,0,0,8,2,8,
            0,8,0,1,9,0,14,0,14,0,
            0,0,2,0,0,1,9,12,5,19,
            0,0,0,3,2,14,0,18,0,3,
            0,10,2,0,0,0,0,9,5,0,
            0,5,2,0,0,10,12,0,9,0,
            1,0,1,10,0,0,0,10,0,15,
            6,5,0,0,2,0,1,12,0,11,
            2,0,0,0,11,0,3,0,1,0,
            9,0,1,0,9,2,0,8,0,1,
            0,0,20,0,0,1,6,0,5,0,
            1,4,0,0,2,0,0,0,5,0,
            24,4,7,7,0,1,0,0,0,10,
            4,0,1,6,0,0,2,0,1,0,
            12,0,0,0,5,3,0,0,7,0,
            7,0,3,18,8,8,0,1,0,1,
            0,0,0,0,4,4,4,0,0,0,
            3,3,21,4,0,0,1,3,0,1,
            0,0,0,1,0,5,0,0,0,0,
            9,0,0,0,10,8,5,11,0,0,
            0,8,13,0,0,7,3,7,20,0,
            0,0,20,3,0,6,17,13,0,8,
            2,7,0,0,0,0,0,0,3,2,
            7,7,10,0,0,2,0,1,0,5,
            0,0,2,0,0,0,0,3,22,0,
            12,10,0,0,0,9,0,1,13,0,
            17,9,0,10,15,3,7,13,0,1,
            0,0,2,0,1,4,0,0,2,2,
            0,1,0,1,0,0,0,0,1,4,
            6,0,0,7,0,0,0,6,2,5,
            0,0,0,0,0,1,11,0,16,0,
            7,10,3,0,0,15,14,0,1,0,
            7,0,1,16,10,0,0,2,9,0,
            0,2,0,1,0,0,6,2,0,0,
            0,15,0,0,5,11,0,5,0,0,
            12,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            276,80,70,72,84,71,74,82,76,276,
            75,67,79,77,73,81,141,276,68,83,
            90,69,78,276,95,94,276,276,276,333,
            91,98,99,89,93,332,276,103,276,97,
            104,92,276,108,201,276,107,102,105,112,
            303,110,276,111,276,106,118,276,116,120,
            276,117,121,276,5,164,101,129,100,276,
            163,276,276,88,128,87,122,165,109,330,
            114,276,115,276,125,276,276,293,130,134,
            135,124,276,131,113,276,276,30,139,146,
            140,276,147,2,276,159,160,158,167,161,
            276,276,179,276,276,276,181,188,192,166,
            189,275,191,85,180,276,96,276,86,276,
            119,276,276,276,276,276,276,123,137,126,
            276,132,276,142,136,276,127,276,133,276,
            276,276,145,276,276,162,144,143,149,138,
            276,276,276,150,151,148,276,310,276,300,
            276,152,154,276,276,276,276,153,155,276,
            276,168,169,276,276,157,156,276,170,276,
            331,276,329,335,276,276,276,171,276,334,
            172,326,276,276,173,276,174,321,276,175,
            176,276,276,276,177,276,183,276,184,276,
            178,276,186,276,311,187,276,185,276,302,
            276,276,182,276,276,194,301,15,193,276,
            294,195,276,276,196,276,276,276,198,276,
            190,200,197,199,276,286,276,276,276,204,
            202,276,205,203,276,276,206,276,336,276,
            207,276,276,276,208,324,276,276,209,276,
            210,276,212,327,211,213,276,214,276,215,
            276,276,276,276,216,217,218,276,276,276,
            219,221,220,222,276,276,224,223,276,225,
            276,276,276,296,276,226,276,276,276,276,
            227,276,276,276,228,230,288,229,276,276,
            276,232,287,276,276,233,235,280,231,276,
            276,276,285,236,276,237,234,325,276,238,
            323,322,276,276,276,276,276,276,240,241,
            319,239,320,276,36,243,276,309,276,313,
            276,276,305,276,276,276,21,247,242,276,
            244,245,276,276,276,248,276,252,299,276,
            246,250,276,251,249,338,253,283,276,282,
            276,276,279,276,255,254,276,276,256,257,
            276,316,276,315,276,276,276,276,261,259,
            258,276,276,260,276,276,276,337,292,295,
            276,276,8,276,276,266,262,276,298,276,
            264,265,314,276,276,289,263,276,268,276,
            267,276,290,317,269,276,276,277,270,276,
            276,328,276,308,276,276,271,272,276,276,
            276,318,276,276,304,273,276,339,276,276,
            307
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
