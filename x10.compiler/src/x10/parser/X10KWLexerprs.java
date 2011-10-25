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

    public final static int LA_STATE_OFFSET = 332;
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

    public final static int ACCEPT_ACTION = 269;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 270;
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
            10,9,6,6,3,4,5,6,8,7,
            7,8,9,6,6,4,6,6,5,6,
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
            1,1,1,1,65,46,118,67,25,17,
            122,31,66,39,119,38,82,98,47,63,
            26,51,124,59,87,125,24,76,62,89,
            129,131,96,132,134,94,84,135,138,140,
            143,144,145,148,101,149,150,153,154,161,
            159,162,166,167,170,173,102,107,174,68,
            110,177,176,178,179,186,189,184,191,195,
            196,201,192,204,199,203,208,43,207,209,
            211,212,214,219,218,220,222,111,225,229,
            231,113,233,239,241,243,232,244,245,247,
            252,255,248,256,257,260,261,265,268,271,
            264,272,273,274,278,280,282,287,291,293,
            294,295,285,299,300,302,304,305,310,312,
            303,314,315,317,318,321,319,325,322,324,
            332,326,328,335,339,344,337,338,347,351,
            352,353,354,355,363,356,364,367,368,357,
            370,373,374,375,380,377,379,382,385,387,
            390,392,386,400,402,403,407,409,410,413,
            415,417,418,419,424,422,420,427,429,430,
            433,434,395,437,435,440,439,448,442,453,
            455,456,445,460,462,463,464,466,270,270
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,0,14,15,0,1,2,19,
            20,21,22,0,0,0,1,4,4,13,
            0,6,9,3,4,19,12,0,0,1,
            16,3,0,13,2,0,0,10,11,3,
            0,13,6,3,8,18,6,15,0,9,
            2,0,0,5,3,0,0,0,1,3,
            25,9,6,6,13,0,11,16,20,12,
            15,0,1,0,3,23,0,1,0,6,
            15,8,4,0,8,0,8,0,3,4,
            0,0,2,6,3,5,0,6,2,0,
            0,5,0,20,5,3,6,0,0,9,
            8,0,1,0,0,16,8,4,0,12,
            0,0,8,0,0,2,8,0,1,0,
            9,7,0,0,0,15,2,0,0,0,
            11,9,0,0,5,2,4,10,0,0,
            0,0,19,15,4,0,0,2,10,0,
            9,5,0,0,1,0,0,0,0,0,
            11,5,10,0,9,0,1,10,0,1,
            0,0,14,10,0,0,6,2,0,5,
            0,1,0,0,13,2,0,0,0,11,
            0,0,1,0,4,13,9,0,0,0,
            1,0,9,2,0,17,8,21,0,1,
            0,0,0,2,17,0,6,5,0,1,
            0,1,0,0,0,3,0,0,24,5,
            7,0,1,7,0,0,0,3,3,0,
            0,1,6,0,0,18,2,0,1,10,
            0,0,0,0,0,5,4,0,7,0,
            7,0,19,4,0,8,0,1,4,8,
            0,1,0,0,0,3,3,3,0,0,
            1,0,0,0,0,4,3,5,4,0,
            1,0,1,0,0,1,0,0,0,21,
            0,0,9,0,0,0,10,0,8,5,
            13,0,7,12,0,17,0,0,0,8,
            17,7,4,0,8,18,0,4,2,12,
            0,0,0,0,0,0,0,7,4,7,
            7,10,0,0,2,2,0,0,1,0,
            14,5,0,0,0,2,0,22,0,0,
            11,0,10,4,0,0,0,9,12,0,
            1,0,18,9,0,14,10,12,7,0,
            1,0,0,2,10,3,0,1,0,0,
            2,2,0,1,0,1,0,0,0,0,
            3,0,6,0,1,7,0,6,0,0,
            2,5,0,0,0,16,0,1,0,0,
            7,0,13,4,0,7,14,0,1,0,
            16,10,0,1,0,0,2,2,14,0,
            1,0,0,0,2,0,0,6,0,0,
            5,0,0,0,11,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            270,79,68,83,70,69,72,81,75,74,
            73,78,76,270,80,71,270,93,92,66,
            82,67,77,270,270,270,114,328,88,91,
            270,287,327,97,96,90,89,270,270,101,
            87,102,270,95,177,270,270,104,297,111,
            270,100,109,117,110,105,115,178,270,116,
            119,270,270,120,127,270,270,5,163,86,
            269,113,85,162,126,270,99,325,121,164,
            98,270,107,270,106,112,270,123,270,137,
            125,138,128,270,122,270,129,270,133,132,
            30,270,145,108,158,146,270,157,159,2,
            270,160,270,136,166,191,187,270,270,188,
            190,270,94,270,270,165,103,118,270,84,
            270,270,124,270,270,135,130,270,140,270,
            134,139,270,270,270,131,143,270,270,270,
            141,142,270,270,148,150,149,144,270,270,
            270,270,305,147,294,270,270,153,151,270,
            152,154,270,270,161,270,270,270,270,270,
            155,167,156,270,168,270,326,330,270,324,
            270,270,329,169,270,270,170,171,270,321,
            270,172,270,270,173,174,270,270,270,316,
            270,270,182,270,181,175,176,270,270,270,
            185,270,306,186,270,180,183,179,270,296,
            270,270,270,195,184,270,295,192,270,193,
            270,288,15,270,270,194,270,270,189,197,
            196,270,280,198,270,270,270,199,201,270,
            270,204,202,270,270,200,205,270,331,203,
            270,270,270,270,270,206,319,270,207,270,
            208,270,322,210,270,209,270,212,217,211,
            270,213,270,270,270,214,215,216,270,270,
            219,270,270,270,270,220,221,225,222,270,
            223,270,224,270,270,290,270,270,270,218,
            270,270,226,270,270,270,227,270,229,282,
            228,270,232,281,270,230,270,270,270,231,
            279,274,234,270,236,233,270,235,318,320,
            270,270,270,270,270,270,270,317,238,314,
            237,315,270,270,239,241,37,270,304,270,
            303,308,270,270,270,299,270,240,21,270,
            242,270,243,245,270,270,270,246,293,270,
            250,270,244,248,270,247,249,277,278,270,
            276,270,270,273,261,251,270,252,270,270,
            253,254,270,311,270,310,270,270,270,270,
            256,270,255,270,258,257,270,332,270,270,
            286,289,270,270,270,292,270,262,270,270,
            260,270,259,309,270,263,283,270,264,270,
            312,265,270,284,270,270,271,323,313,270,
            302,270,270,270,267,270,270,266,270,270,
            298,270,270,270,301
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
