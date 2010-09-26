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

    public final static int NUM_STATES = 196;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 322;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 61;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 62;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 24;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 260;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 261;
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
            5,5,7,8,3,7,2,4,7,5,
            5,7,6,3,4,4,2,10,6,2,
            10,9,6,3,4,4,5,6,8,7,
            7,8,9,6,6,6,4,6,6,5,
            6,4,5,9,4,3,4,3,3,4,
            5
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
            1,1,1,64,99,36,28,17,24,68,
            34,59,40,44,118,120,47,52,66,55,
            70,27,78,80,121,61,82,127,128,87,
            131,92,132,93,137,140,133,136,81,146,
            105,138,149,154,155,157,150,160,163,161,
            165,9,106,169,164,112,109,171,172,175,
            177,181,179,183,185,187,191,186,194,195,
            198,199,200,115,201,203,202,204,211,212,
            213,215,224,116,217,221,95,225,227,232,
            234,236,229,239,240,241,245,243,83,249,
            247,253,255,258,260,251,261,264,263,270,
            271,262,272,273,281,283,284,285,289,276,
            290,292,296,298,301,302,303,305,307,308,
            291,312,309,313,319,316,320,324,325,327,
            328,332,334,337,338,339,342,344,349,352,
            341,355,346,358,356,360,362,361,365,366,
            367,372,373,370,375,376,383,387,391,386,
            393,396,398,394,400,401,405,402,408,409,
            410,412,414,415,416,419,422,425,427,423,
            433,436,437,430,440,442,443,444,446,261,
            261
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,0,9,
            10,3,12,13,14,15,0,17,10,19,
            4,21,22,0,1,2,0,0,2,13,
            3,5,16,0,11,0,3,4,15,0,
            1,15,3,0,11,19,0,0,13,3,
            11,0,9,7,0,12,10,3,0,8,
            0,18,8,3,10,0,1,0,1,0,
            12,11,14,4,23,10,16,0,1,0,
            0,0,0,4,7,4,0,8,7,3,
            4,0,0,2,0,15,5,3,0,7,
            18,7,10,23,0,0,2,2,0,5,
            5,0,1,5,0,0,2,0,1,0,
            0,10,24,8,16,10,0,0,14,10,
            0,0,0,7,14,0,0,0,8,0,
            1,14,6,8,12,0,0,2,0,0,
            19,14,4,0,0,2,0,8,4,0,
            0,2,0,0,0,9,3,5,0,1,
            0,0,12,9,0,5,0,1,0,8,
            0,1,0,9,0,0,0,9,2,5,
            0,1,10,0,0,2,11,0,0,0,
            0,0,0,0,1,4,12,8,11,11,
            0,0,0,2,0,1,0,1,8,7,
            0,21,20,0,0,2,0,1,0,5,
            10,0,1,0,6,0,3,2,0,0,
            0,1,0,5,0,6,0,3,0,7,
            0,3,0,1,0,9,2,0,1,0,
            0,0,0,0,5,15,6,4,6,0,
            0,0,0,1,4,0,7,0,7,18,
            0,1,0,0,0,3,3,3,0,0,
            0,0,4,4,3,0,21,0,1,4,
            0,0,0,1,0,5,0,0,0,8,
            20,0,0,9,7,0,5,11,0,0,
            2,13,7,0,0,6,0,0,4,6,
            4,0,20,0,7,2,0,0,0,1,
            0,0,6,0,13,0,9,6,0,6,
            5,0,4,2,0,0,2,0,1,0,
            0,0,22,2,0,0,0,12,9,0,
            1,0,0,8,0,0,0,13,18,8,
            6,9,0,17,2,0,0,2,13,3,
            0,1,0,0,2,0,1,0,1,0,
            0,0,3,10,0,1,6,0,0,0,
            2,0,5,0,0,0,1,16,0,6,
            11,0,0,9,0,4,0,1,17,0,
            6,9,0,1,16,0,0,2,2,0,
            1,0,0,0,2,0,17,0,0,0,
            5,10,0,0,0,12,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            261,77,67,81,69,68,79,73,261,72,
            71,153,76,74,70,65,261,78,152,80,
            85,66,75,261,90,89,261,261,115,86,
            83,116,84,261,88,261,94,93,87,261,
            98,114,99,261,92,117,261,261,82,106,
            97,261,100,105,261,288,104,112,261,108,
            261,101,111,122,110,261,109,261,91,261,
            96,121,95,113,107,278,317,261,119,261,
            261,261,261,320,118,123,261,319,124,128,
            127,261,261,130,261,295,131,185,261,134,
            195,184,133,139,30,261,141,154,2,142,
            155,5,159,161,261,261,173,261,102,261,
            261,158,260,183,160,182,261,261,174,103,
            261,261,261,125,120,261,261,261,129,261,
            136,126,135,138,137,261,261,140,261,261,
            132,143,144,261,261,145,261,147,285,261,
            261,148,261,261,261,146,157,149,261,156,
            261,261,150,151,261,162,261,318,261,163,
            261,316,261,321,261,261,261,164,166,313,
            261,167,165,261,261,169,168,261,261,261,
            261,261,261,261,178,177,308,172,170,171,
            261,261,261,296,261,180,261,287,297,179,
            261,175,176,261,261,181,261,187,261,186,
            286,261,279,15,190,261,188,189,261,261,
            261,271,261,191,261,192,261,193,261,194,
            261,196,261,198,261,197,199,261,322,261,
            261,261,261,261,200,314,201,311,202,261,
            261,261,261,207,204,261,203,261,206,205,
            261,208,261,261,261,209,210,211,261,261,
            261,261,212,214,215,261,213,261,217,216,
            261,261,261,281,261,218,261,261,261,219,
            223,261,261,220,222,261,273,221,261,261,
            269,272,224,261,261,225,261,261,226,265,
            227,261,270,261,228,310,261,261,261,306,
            261,261,309,261,312,37,307,305,261,229,
            299,261,230,231,261,261,233,261,294,261,
            261,261,232,290,261,21,261,234,235,261,
            241,261,261,237,261,261,261,284,236,239,
            268,240,261,238,264,261,261,244,267,242,
            261,243,261,261,245,261,302,261,301,261,
            261,261,247,246,261,249,248,261,261,261,
            277,261,280,261,261,261,253,283,261,251,
            250,261,261,252,261,300,261,255,274,261,
            254,256,261,275,303,261,261,262,315,261,
            293,261,261,261,258,261,304,261,261,261,
            289,257,261,261,261,292
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
