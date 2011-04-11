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

    public final static int NUM_STATES = 199;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 53;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 325;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 61;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 55;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 62;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 25;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 54;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 263;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 264;
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
            7
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
            1,1,1,64,71,61,63,17,24,125,
            27,67,35,34,79,128,46,68,83,47,
            28,55,85,123,88,116,59,10,132,133,
            95,135,39,120,94,136,137,141,142,87,
            146,101,145,149,153,156,160,155,161,166,
            162,163,169,104,109,171,64,108,173,175,
            176,43,180,182,177,185,188,190,194,189,
            196,197,199,200,115,201,203,206,204,207,
            211,213,215,112,218,220,224,119,226,227,
            232,234,236,239,235,241,243,247,245,249,
            251,253,254,256,259,261,264,265,225,267,
            268,269,270,279,283,285,286,287,271,277,
            291,293,297,299,302,304,305,306,308,281,
            311,292,310,313,316,320,317,321,322,323,
            326,331,332,333,334,337,342,343,348,351,
            340,354,355,345,357,359,361,358,366,364,
            365,367,371,373,377,372,381,384,386,387,
            391,394,395,398,400,402,403,404,409,407,
            389,412,414,415,418,419,420,421,423,425,
            424,434,427,440,436,442,432,445,443,448,
            451,452,264,264
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,0,
            10,11,12,4,14,15,0,8,18,19,
            4,21,22,0,1,2,0,0,12,3,
            4,4,16,0,0,1,13,3,0,13,
            2,18,0,10,11,0,0,13,3,3,
            17,6,6,8,0,9,2,15,0,5,
            0,3,0,0,1,3,0,0,6,6,
            0,13,12,19,16,12,9,11,0,1,
            14,3,0,1,0,1,0,0,6,0,
            23,4,8,0,0,25,9,3,4,6,
            0,8,2,0,18,5,3,0,0,6,
            2,0,5,5,0,0,2,6,0,0,
            9,3,0,16,0,1,8,0,14,14,
            8,0,0,6,0,0,0,1,19,8,
            0,0,7,9,0,0,14,2,0,0,
            9,11,0,5,0,0,4,2,14,0,
            0,0,0,4,10,0,5,2,0,9,
            0,1,0,11,0,0,0,5,10,0,
            1,0,1,9,0,10,10,0,0,0,
            6,2,5,0,1,0,0,2,0,0,
            0,13,0,0,1,0,0,11,9,4,
            0,13,0,1,0,9,2,0,8,0,
            1,21,20,0,0,0,0,1,4,6,
            5,0,1,0,0,0,3,2,0,5,
            0,24,0,1,0,7,0,7,0,3,
            0,3,0,0,1,0,6,2,0,1,
            0,17,10,0,0,5,0,0,0,0,
            0,7,4,7,4,8,0,8,0,1,
            0,18,0,1,0,0,0,3,3,3,
            0,0,0,13,4,3,0,21,0,1,
            4,0,1,0,0,0,1,0,5,0,
            0,20,0,9,5,0,0,10,8,0,
            0,0,0,7,12,0,4,8,7,4,
            0,0,0,0,2,20,0,17,8,0,
            7,0,0,12,0,1,10,0,7,7,
            0,4,2,0,0,2,0,0,0,5,
            0,22,2,0,0,0,0,11,10,4,
            0,0,0,9,17,12,0,1,7,9,
            0,15,10,0,1,0,0,2,0,3,
            0,1,12,0,0,2,2,0,1,0,
            1,0,0,0,16,3,0,6,0,1,
            7,0,6,0,0,2,5,0,0,0,
            0,1,0,0,0,7,0,13,4,10,
            7,0,15,0,1,0,10,2,16,0,
            1,0,0,2,0,1,15,0,6,2,
            0,0,0,0,0,0,5,0,0,0,
            0,11,0,0,0,0,0,0,0,0,
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
            264,77,67,81,69,68,71,79,73,264,
            72,76,74,125,70,78,264,126,65,80,
            86,66,75,264,91,90,264,264,87,95,
            94,115,85,264,264,99,89,100,264,93,
            132,88,264,101,291,264,264,98,108,114,
            102,106,112,107,264,113,116,322,264,117,
            264,124,264,5,159,84,264,264,83,158,
            264,123,82,118,318,160,110,97,264,104,
            96,103,264,111,264,120,264,264,281,264,
            109,321,119,264,264,263,320,130,129,134,
            30,135,141,264,298,142,154,2,264,153,
            155,264,162,156,264,264,173,182,264,264,
            183,186,264,161,264,92,185,264,174,122,
            121,264,264,105,264,264,264,137,133,127,
            264,264,136,131,264,264,128,140,264,264,
            139,138,264,144,264,264,145,146,143,264,
            264,264,264,288,147,264,150,149,264,148,
            264,157,264,151,264,264,264,163,152,264,
            319,264,317,164,264,323,165,264,264,264,
            166,167,314,264,168,264,264,170,264,264,
            264,169,264,264,178,264,264,309,172,177,
            264,171,264,180,264,299,181,264,179,264,
            290,175,176,264,264,264,264,188,312,289,
            187,264,282,15,264,264,189,190,264,192,
            264,184,264,274,264,191,264,193,264,194,
            264,196,264,264,199,264,197,200,264,324,
            264,195,198,264,264,201,264,264,264,264,
            264,202,205,203,212,204,264,206,264,207,
            264,315,264,208,264,264,264,209,210,211,
            264,264,264,222,214,215,264,213,264,217,
            216,264,218,264,264,264,284,264,219,264,
            264,224,264,220,276,264,264,221,223,264,
            264,264,264,226,275,264,228,225,268,229,
            264,264,264,264,311,273,264,227,230,264,
            310,264,264,313,264,297,308,264,307,231,
            264,232,233,264,36,235,264,264,264,301,
            264,234,293,264,21,264,264,236,237,239,
            264,264,264,240,238,287,264,244,272,242,
            264,241,243,264,270,264,264,267,264,245,
            264,246,271,264,264,247,248,264,304,264,
            303,264,264,264,286,250,264,249,264,252,
            251,264,325,264,264,280,283,264,264,264,
            264,256,264,264,264,254,264,253,302,255,
            257,264,277,264,258,264,259,265,305,264,
            278,264,264,316,264,296,306,264,260,261,
            264,264,264,264,264,264,292,264,264,264,
            264,295
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
