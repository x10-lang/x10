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

    public final static int NUM_STATES = 193;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 317;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 60;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 61;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 24;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 256;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 257;
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
            8,2,6,5,2,6,6,5,4,5,
            5,7,8,3,7,2,4,7,5,5,
            7,6,3,4,4,2,10,6,2,10,
            9,6,3,4,4,5,6,8,7,7,
            8,9,6,6,6,4,6,6,5,6,
            4,5,9,4,3,4,3,3,4,5
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
            1,1,63,40,120,9,28,24,124,27,
            56,33,37,127,29,45,61,71,50,47,
            57,79,83,96,63,85,130,121,90,131,
            16,132,88,134,141,136,138,82,143,97,
            144,149,150,155,147,154,161,160,157,164,
            101,107,166,113,103,170,168,171,177,181,
            174,67,179,185,188,186,190,191,193,194,
            198,115,195,199,196,201,207,208,211,212,
            220,65,223,106,119,209,225,227,229,231,
            230,234,235,237,240,242,244,245,248,250,
            255,246,253,257,217,258,260,261,262,266,
            268,274,277,278,282,272,270,283,286,284,
            292,290,294,296,298,299,301,300,304,303,
            305,306,311,312,315,322,314,323,327,324,
            328,331,333,334,338,341,335,344,345,347,
            349,351,352,353,355,356,358,359,362,365,
            363,372,373,376,377,380,381,386,388,384,
            390,391,395,392,398,399,400,402,404,405,
            408,406,412,413,422,415,425,427,428,417,
            431,434,435,436,438,257,257
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,0,9,
            10,3,12,13,14,0,16,2,18,19,
            5,21,22,0,1,2,0,0,0,3,
            4,4,0,1,11,3,0,11,10,0,
            13,18,15,11,0,9,0,3,12,0,
            4,7,3,17,10,0,0,8,2,10,
            0,5,0,24,0,3,0,12,8,14,
            0,1,8,11,10,19,10,15,0,1,
            10,0,0,23,0,7,4,0,4,0,
            8,7,3,4,7,0,0,10,2,18,
            0,5,0,3,23,0,0,5,2,14,
            10,5,0,1,0,10,2,15,0,0,
            0,3,10,0,1,7,0,1,14,0,
            0,0,13,0,14,0,7,0,8,6,
            0,1,0,0,2,8,0,12,0,0,
            19,2,4,0,0,9,0,14,4,0,
            0,8,2,0,5,0,1,0,12,0,
            0,0,9,0,5,8,0,1,0,9,
            0,1,9,5,0,0,2,0,1,0,
            0,2,0,0,0,0,11,0,0,4,
            0,1,12,11,11,8,0,0,0,2,
            0,0,1,5,8,21,0,7,20,0,
            4,2,0,1,0,1,0,1,0,0,
            0,3,2,0,0,6,0,1,5,0,
            6,0,3,0,0,0,3,0,1,0,
            5,2,0,9,0,1,0,0,17,0,
            0,0,6,6,4,0,7,0,1,0,
            18,0,7,0,1,4,0,0,17,3,
            3,0,0,0,3,0,4,4,3,0,
            21,0,1,0,5,0,1,0,0,0,
            0,8,0,0,0,0,9,7,5,11,
            0,0,7,0,0,13,6,6,4,20,
            7,0,0,0,20,4,0,0,2,6,
            0,1,0,0,0,13,9,0,6,6,
            0,4,2,0,0,2,0,1,0,5,
            0,0,0,2,0,0,22,0,0,9,
            12,0,0,8,0,1,8,13,6,17,
            9,0,0,16,2,0,0,1,3,0,
            0,2,2,0,13,0,1,0,1,0,
            0,0,3,10,0,1,6,0,0,0,
            2,0,5,0,0,0,15,0,1,6,
            11,0,0,9,0,4,0,16,6,0,
            15,0,1,9,0,1,0,0,2,2,
            0,1,16,0,0,0,2,0,0,0,
            0,0,5,10,0,0,0,12,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            257,76,66,80,68,67,78,72,257,71,
            70,82,75,73,69,257,77,128,64,79,
            129,65,74,257,89,88,257,257,257,93,
            92,84,257,97,87,98,257,91,102,257,
            85,86,83,96,257,99,257,105,283,257,
            112,104,111,100,103,257,257,110,113,109,
            257,114,257,256,257,120,257,95,107,94,
            257,108,180,119,179,115,162,312,257,117,
            273,257,257,106,257,116,315,257,121,257,
            314,122,126,125,132,257,29,131,139,290,
            257,140,2,151,137,257,257,158,152,118,
            150,153,5,156,257,281,170,157,257,257,
            257,182,155,257,90,181,257,101,171,257,
            257,257,81,257,124,257,123,257,127,133,
            257,134,257,257,138,136,257,135,257,257,
            130,143,142,257,257,144,257,141,280,257,
            257,145,146,257,147,257,154,257,148,257,
            257,257,149,257,159,160,257,313,257,316,
            257,311,161,308,257,257,163,257,164,257,
            257,166,257,257,257,257,165,257,257,174,
            257,175,303,167,168,169,257,257,257,291,
            257,257,177,183,292,172,257,176,173,257,
            306,178,257,282,257,184,257,274,14,257,
            257,185,186,257,257,187,257,266,188,257,
            189,257,190,257,257,257,192,257,194,257,
            196,195,257,193,257,317,257,257,191,257,
            257,257,197,198,200,257,199,257,203,257,
            309,257,202,257,204,208,257,257,201,205,
            206,257,257,257,207,257,210,212,211,257,
            209,257,213,257,214,257,276,257,257,257,
            257,215,257,257,257,257,216,218,268,217,
            257,257,220,257,257,267,221,261,222,219,
            224,257,257,257,265,223,257,257,305,304,
            257,301,257,257,257,307,302,257,300,225,
            257,226,227,257,36,229,257,289,257,294,
            257,257,257,285,257,20,228,257,257,231,
            230,257,257,233,257,237,235,279,264,232,
            236,257,257,234,260,257,257,239,238,257,
            257,240,241,257,263,257,297,257,296,257,
            257,257,243,242,257,245,244,257,257,257,
            272,257,275,257,257,257,278,257,249,247,
            246,257,257,248,257,295,257,269,250,257,
            298,257,251,252,257,270,257,257,258,310,
            257,288,299,257,257,257,254,257,257,257,
            257,257,284,253,257,257,257,287
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
