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

    public final static int NUM_STATES = 200;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 53;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 328;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 62;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 55;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 63;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 25;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 54;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 265;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 266;
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
            8,2,6,5,2,6,6,5,4,5,
            5,7,8,3,7,2,4,7,5,5,
            7,6,3,4,4,2,10,6,2,10,
            9,6,3,4,4,5,6,8,7,7,
            8,9,6,6,6,4,6,6,5,6,
            4,5,9,4,3,4,3,3,4,4,
            5,7
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
            1,1,1,1,65,45,85,60,17,24,
            131,28,29,37,34,65,129,47,69,73,
            49,123,57,81,10,75,121,61,83,137,
            138,90,27,95,139,96,140,141,143,144,
            88,147,105,148,151,150,157,160,161,163,
            165,168,166,169,109,113,174,101,114,176,
            177,170,71,182,184,187,188,189,190,198,
            191,201,193,196,200,204,120,206,208,210,
            215,209,217,212,221,223,117,224,229,226,
            125,231,234,237,239,241,240,244,245,252,
            247,254,255,256,259,261,264,257,267,268,
            269,270,272,275,277,278,281,283,287,288,
            289,293,295,296,298,299,304,306,308,302,
            309,312,314,315,317,319,318,320,321,326,
            327,330,335,328,338,341,342,344,345,347,
            349,352,357,350,358,361,362,364,365,367,
            368,370,371,373,377,378,379,380,388,383,
            391,395,399,401,402,405,407,390,409,410,
            413,411,415,417,419,423,420,424,426,427,
            425,429,432,439,437,442,444,448,445,451,
            449,453,454,457,266,266
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,0,
            10,0,12,13,14,15,0,8,18,19,
            4,21,22,0,1,2,0,0,0,13,
            3,4,16,0,11,9,0,1,11,3,
            12,18,14,10,0,12,0,11,0,3,
            17,3,0,7,8,7,0,9,2,0,
            0,5,3,3,0,1,7,3,0,25,
            0,11,0,1,0,19,16,9,4,7,
            0,1,0,9,0,15,4,0,8,0,
            8,23,3,4,0,0,2,13,0,5,
            0,1,7,8,0,18,2,7,0,5,
            23,3,0,0,2,7,0,5,5,0,
            0,2,0,7,0,9,4,3,0,16,
            0,1,8,14,14,7,0,0,0,0,
            0,1,0,0,8,6,0,0,2,0,
            0,14,9,4,12,5,0,19,2,0,
            0,14,0,4,0,0,2,0,0,0,
            10,9,5,0,1,0,0,12,10,10,
            5,0,1,0,1,9,0,0,0,0,
            0,2,0,5,7,0,10,0,1,0,
            0,11,2,0,12,0,11,0,0,0,
            11,0,9,4,0,1,0,9,2,8,
            0,1,0,0,2,0,21,20,0,1,
            0,0,7,0,1,5,0,1,0,0,
            0,3,2,0,0,6,0,24,5,3,
            6,0,1,0,0,0,0,3,0,1,
            0,5,2,0,1,10,0,0,0,0,
            17,0,4,6,0,6,0,0,4,8,
            0,1,0,1,18,8,0,0,0,3,
            3,3,0,17,0,0,4,0,0,4,
            3,0,4,0,1,0,1,0,0,1,
            9,0,5,0,0,21,0,0,0,0,
            0,10,8,5,11,0,0,0,8,0,
            13,6,6,4,0,8,20,0,4,20,
            0,0,2,0,0,1,0,6,0,0,
            13,0,6,10,6,4,0,0,2,2,
            0,0,1,0,0,5,0,0,2,0,
            0,22,0,4,10,12,0,0,0,0,
            1,9,0,13,17,0,9,0,10,0,
            0,15,2,6,0,13,7,3,0,1,
            0,0,2,2,0,1,0,1,0,0,
            0,3,0,1,0,6,0,7,0,0,
            2,5,0,0,0,0,0,1,0,6,
            16,0,4,11,15,10,0,6,0,1,
            16,0,1,0,0,2,10,0,0,2,
            0,1,0,0,2,7,0,0,0,15,
            0,5,0,0,0,12,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            266,78,68,82,70,69,80,72,74,266,
            73,266,77,75,71,79,266,122,66,81,
            87,67,76,266,92,91,266,266,266,88,
            96,95,86,266,90,132,266,100,94,101,
            98,89,97,102,266,292,266,99,266,109,
            103,115,266,107,108,113,266,114,117,266,
            266,118,85,125,266,105,84,104,266,265,
            266,124,266,112,266,119,321,111,324,282,
            266,121,266,323,266,325,126,266,120,266,
            127,110,131,130,266,266,133,83,266,134,
            5,162,136,137,29,299,144,161,266,145,
            142,157,266,2,158,156,266,159,164,266,
            266,176,266,185,266,186,116,189,266,163,
            266,93,188,177,123,106,266,266,266,266,
            266,139,266,266,128,138,266,266,143,266,
            266,129,141,148,140,147,266,135,149,266,
            266,146,266,289,266,266,152,266,266,266,
            150,151,153,266,160,266,266,154,155,326,
            165,266,322,266,320,166,266,266,266,266,
            266,169,266,317,168,266,167,266,170,266,
            266,171,172,266,312,266,173,266,266,266,
            174,266,175,180,266,181,266,301,300,182,
            266,183,266,266,184,266,178,179,266,291,
            266,266,290,266,191,190,266,283,14,266,
            266,192,193,266,266,194,266,187,195,197,
            196,266,275,266,266,266,266,199,266,201,
            266,203,202,266,327,200,266,266,266,266,
            198,266,315,204,266,205,266,266,207,206,
            266,210,266,211,318,209,266,266,266,212,
            213,214,266,208,266,266,215,266,266,217,
            218,266,219,266,220,266,221,266,266,285,
            223,266,222,266,266,216,266,266,266,266,
            266,224,226,277,225,266,266,266,228,266,
            276,229,270,230,266,232,227,266,231,274,
            266,266,314,266,266,310,266,313,266,266,
            316,266,309,311,233,234,266,266,235,237,
            36,266,298,266,266,303,266,266,294,266,
            266,236,20,241,239,238,266,266,266,266,
            246,242,266,288,240,266,244,266,245,266,
            266,243,269,273,266,272,251,247,266,248,
            266,266,249,250,266,306,266,305,266,266,
            266,252,266,254,266,253,266,328,266,266,
            281,284,266,266,266,266,266,258,266,256,
            287,266,304,255,278,257,266,259,266,260,
            307,266,279,266,266,267,261,266,266,319,
            266,297,266,266,263,262,266,266,266,308,
            266,293,266,266,266,296
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
