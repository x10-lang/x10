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

    public final static int NUM_STATES = 204;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 53;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 336;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 64;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 55;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 65;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 25;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 54;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 271;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 272;
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
            6,4,5,9,4,3,4,3,3,3,
            4,4,5,7
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
            1,1,1,1,1,1,67,12,18,38,
            17,24,132,36,62,48,44,79,130,50,
            75,85,61,67,27,87,83,90,122,52,
            73,138,128,93,139,100,124,101,141,144,
            140,149,89,151,111,150,154,156,155,161,
            162,166,167,163,170,171,103,115,173,177,
            65,114,179,176,178,28,186,189,191,192,
            193,194,202,195,205,197,200,204,208,121,
            210,212,214,219,213,221,216,225,227,118,
            228,233,230,126,235,238,241,243,245,244,
            248,249,256,251,258,259,260,261,263,266,
            269,272,265,273,276,275,278,284,282,285,
            289,291,294,296,300,301,287,302,304,309,
            310,314,312,316,318,320,321,323,322,324,
            326,327,328,334,333,335,344,346,337,338,
            352,353,355,356,358,360,363,368,361,369,
            372,373,375,376,378,379,381,384,382,388,
            389,390,392,396,394,404,399,406,410,412,
            413,416,418,420,421,422,425,423,429,431,
            432,433,435,437,438,439,441,445,447,451,
            454,455,457,460,461,465,463,467,468,472,
            272,272
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
            4,21,22,0,1,2,0,0,2,13,
            13,5,6,17,11,0,25,0,3,4,
            3,18,15,0,7,19,11,0,1,0,
            3,0,3,10,3,12,7,8,11,16,
            0,0,11,3,0,1,0,7,17,9,
            4,7,0,12,0,14,4,13,0,1,
            8,3,0,9,0,1,0,1,0,0,
            8,7,0,4,8,3,4,23,9,0,
            0,2,0,0,5,3,18,7,8,7,
            0,23,2,0,0,5,2,0,5,5,
            0,0,2,0,7,0,9,0,3,0,
            17,0,1,8,14,14,7,0,0,0,
            0,14,19,0,1,8,6,9,0,0,
            0,12,2,0,0,0,2,9,5,4,
            0,0,0,14,4,0,0,5,2,0,
            0,10,0,1,9,0,0,0,0,0,
            10,12,6,5,9,0,1,10,0,1,
            0,0,0,0,0,2,0,5,7,0,
            10,0,1,0,0,11,2,0,12,0,
            11,0,0,0,11,0,9,4,0,1,
            0,9,2,8,0,1,0,0,2,0,
            21,20,0,1,0,0,7,0,1,5,
            0,1,0,0,0,3,2,0,0,6,
            0,24,5,3,6,0,1,0,0,0,
            0,3,0,1,0,0,7,2,0,1,
            10,0,0,16,0,0,5,0,6,4,
            6,0,18,0,0,8,0,4,0,1,
            0,1,8,0,0,0,3,16,3,0,
            0,0,3,0,4,4,3,21,0,0,
            1,0,4,0,1,0,5,0,1,0,
            0,0,0,0,9,0,0,0,5,10,
            8,11,0,0,0,8,0,0,13,6,
            6,20,0,0,8,0,20,4,16,4,
            13,0,0,2,0,0,1,0,6,0,
            0,0,0,6,10,6,4,0,0,2,
            2,0,0,1,0,0,5,0,0,2,
            0,0,22,0,4,10,12,0,0,0,
            9,0,1,0,16,0,13,9,0,10,
            2,6,15,0,1,0,13,0,3,0,
            1,0,0,2,2,0,1,0,1,0,
            0,0,0,3,0,1,7,6,0,7,
            0,0,0,2,0,5,0,0,0,1,
            0,0,6,11,0,17,0,10,4,15,
            0,1,6,0,0,1,0,17,2,0,
            0,2,0,10,0,1,0,0,2,7,
            0,0,0,0,0,15,5,0,0,12,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            272,80,70,84,72,71,82,74,76,272,
            75,272,79,77,73,81,272,272,68,83,
            89,69,78,272,94,93,272,272,120,90,
            85,121,119,88,92,272,271,272,98,97,
            87,91,333,272,86,122,96,272,102,272,
            103,272,111,104,128,299,109,110,101,105,
            272,272,127,117,5,165,272,115,328,116,
            118,164,272,100,272,99,129,166,272,107,
            130,106,272,113,272,114,272,124,272,272,
            125,289,272,331,123,134,133,112,330,272,
            272,136,272,272,137,160,306,139,140,159,
            30,145,147,2,272,148,161,272,168,162,
            272,272,180,272,189,272,190,272,193,272,
            167,272,95,192,181,126,108,272,272,272,
            272,132,138,272,142,131,141,135,272,272,
            272,143,146,272,272,272,152,144,150,151,
            272,272,272,149,296,272,272,156,155,272,
            272,153,272,163,154,272,272,272,272,272,
            158,157,332,169,170,272,329,334,272,327,
            272,272,272,272,272,173,272,324,172,272,
            171,272,174,272,272,175,176,272,319,272,
            177,272,272,272,178,272,179,184,272,185,
            272,308,307,186,272,187,272,272,188,272,
            182,183,272,298,272,272,297,272,195,194,
            272,290,15,272,272,196,197,272,272,198,
            272,191,199,201,200,272,282,272,272,272,
            272,203,272,206,272,272,204,207,272,335,
            205,272,272,202,272,272,208,272,209,322,
            210,272,325,272,272,211,272,212,272,215,
            272,216,214,272,272,272,217,213,218,272,
            272,272,219,272,220,222,223,221,272,272,
            225,272,224,272,226,272,227,272,292,272,
            272,272,272,272,228,272,272,272,284,229,
            231,230,272,272,272,233,272,272,283,234,
            276,232,272,272,238,272,281,236,235,237,
            323,272,272,321,272,272,317,272,320,272,
            272,272,272,316,318,239,240,272,272,241,
            243,37,272,305,272,272,310,272,272,301,
            272,21,242,272,247,245,244,272,272,272,
            248,272,252,272,246,272,295,250,272,251,
            275,280,249,272,278,272,279,272,253,272,
            254,272,272,255,256,272,313,272,312,272,
            272,272,272,258,272,260,257,259,272,336,
            272,272,272,288,272,291,272,272,272,264,
            272,272,262,261,272,294,272,263,311,285,
            272,266,265,272,272,286,272,314,273,272,
            272,326,272,267,272,304,272,272,269,268,
            272,272,272,272,272,315,300,272,272,303
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
