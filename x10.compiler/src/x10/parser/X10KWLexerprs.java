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

    public final static int NUM_STATES = 214;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 53;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 346;
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

    public final static int ACCEPT_ACTION = 281;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 282;
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
            7,7,10,6
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
            1,1,1,1,1,1,67,100,113,111,
            114,65,24,17,123,30,62,31,38,25,
            126,39,68,78,49,64,52,128,129,80,
            130,83,131,59,44,133,134,85,135,139,
            142,90,143,146,82,149,150,152,93,153,
            155,156,161,165,164,157,170,171,173,177,
            99,102,178,60,101,175,181,182,183,186,
            188,189,193,191,199,195,202,206,197,209,
            210,212,215,108,213,214,216,217,220,221,
            225,230,109,227,235,233,112,237,240,243,
            245,246,247,252,250,253,256,259,261,262,
            263,266,269,274,264,278,280,270,282,281,
            283,286,292,287,296,298,300,301,302,307,
            288,308,312,314,318,320,322,315,325,303,
            323,328,329,331,330,332,333,337,338,339,
            341,348,342,352,353,350,359,354,355,364,
            366,367,369,368,373,376,377,379,382,383,
            380,384,386,388,389,393,395,400,403,394,
            408,410,413,415,419,414,421,424,426,422,
            429,430,431,434,435,436,437,442,443,445,
            444,396,446,448,454,455,449,456,460,462,
            465,468,469,472,474,475,476,478,481,483,
            282,282
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,0,1,2,19,
            20,21,22,0,0,1,3,11,4,0,
            0,1,3,4,4,19,13,0,0,16,
            11,11,4,0,6,7,3,10,0,12,
            7,0,4,2,6,18,5,9,0,0,
            1,0,4,0,0,6,3,0,4,11,
            6,20,13,12,16,14,9,0,1,0,
            1,0,0,6,0,3,7,3,4,0,
            23,9,0,12,2,6,7,5,0,0,
            0,0,4,2,6,5,5,0,0,2,
            0,0,0,0,6,4,16,9,7,7,
            10,14,0,1,25,0,13,0,0,0,
            0,6,0,0,0,7,7,10,0,7,
            2,0,0,9,14,0,1,14,0,0,
            8,0,0,2,0,0,0,9,3,5,
            0,20,2,0,0,9,14,3,19,0,
            0,2,0,10,0,5,0,0,1,5,
            0,0,0,2,12,0,10,0,0,1,
            0,9,0,1,0,10,0,17,0,5,
            10,0,15,2,6,0,1,11,0,0,
            2,0,0,0,0,0,0,1,3,0,
            0,12,11,9,0,1,0,7,9,0,
            17,2,0,21,0,1,0,0,6,0,
            1,5,0,1,0,0,0,2,4,0,
            24,0,0,1,8,0,5,8,0,4,
            0,0,0,0,4,0,1,6,0,0,
            2,0,10,0,1,12,18,0,1,0,
            0,0,0,3,5,0,0,0,19,8,
            8,0,7,7,3,0,1,0,1,0,
            0,0,0,4,4,4,0,0,21,3,
            3,0,10,0,0,4,3,0,1,0,
            1,0,0,9,0,1,5,0,0,0,
            0,0,0,11,7,5,0,0,0,7,
            0,0,13,3,8,17,8,0,17,0,
            3,0,0,0,0,18,15,6,0,7,
            2,8,13,0,10,0,0,0,0,3,
            2,8,0,8,2,0,0,1,0,0,
            5,0,0,0,2,0,3,0,0,22,
            12,10,0,0,0,0,9,18,13,0,
            1,9,0,15,10,10,13,0,1,0,
            8,2,0,0,0,2,4,3,0,1,
            0,0,2,0,1,0,1,6,0,0,
            0,1,4,0,0,0,0,8,2,6,
            5,0,0,0,0,0,1,0,0,1,
            16,8,11,0,0,0,3,15,14,0,
            1,0,8,16,0,10,2,0,0,2,
            9,0,1,0,0,0,2,0,0,6,
            0,0,0,15,0,5,11,5,0,12,
            0,0,0,0,0,0,0,0,0,0,
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
            282,82,72,74,86,73,76,78,84,68,
            77,69,81,79,75,83,282,98,97,70,
            85,71,80,282,282,111,93,96,110,282,
            282,106,101,102,107,95,94,282,282,92,
            100,105,115,282,113,114,134,108,282,309,
            135,282,121,123,119,109,124,120,282,5,
            168,282,133,282,282,167,122,282,91,132,
            90,125,169,104,336,103,117,282,118,282,
            129,282,282,299,282,339,128,138,139,282,
            116,338,30,147,150,143,144,151,282,282,
            2,282,163,164,162,171,165,282,282,184,
            282,282,282,282,193,197,170,194,196,87,
            88,185,282,99,281,282,89,282,282,282,
            282,112,282,282,282,127,130,126,282,136,
            141,282,282,140,131,282,146,137,282,282,
            145,282,282,149,282,282,282,148,154,153,
            282,142,155,282,282,157,152,306,316,282,
            282,158,282,156,282,159,282,282,166,172,
            282,282,282,174,160,282,161,282,282,337,
            282,175,282,335,282,341,282,173,282,332,
            176,282,340,178,177,282,179,180,282,282,
            181,282,282,282,282,282,282,189,188,282,
            282,327,182,183,282,191,282,190,317,282,
            187,192,282,186,282,308,282,282,307,282,
            199,198,282,300,15,282,282,201,200,282,
            195,282,282,292,202,282,203,204,282,205,
            282,282,282,282,207,282,210,208,282,282,
            211,282,209,282,212,213,206,282,342,282,
            282,282,282,330,214,282,282,282,333,215,
            216,282,217,219,218,282,220,282,221,282,
            282,282,282,222,223,224,282,282,226,225,
            227,282,234,282,282,228,229,282,230,282,
            231,282,282,233,282,302,232,282,282,282,
            282,282,282,235,236,294,282,282,282,238,
            282,282,293,241,239,237,286,282,291,282,
            242,282,282,282,282,240,346,243,282,244,
            329,328,331,282,326,282,282,282,282,246,
            247,325,282,245,249,36,282,315,282,282,
            319,282,282,282,311,282,253,21,282,248,
            250,251,282,282,282,282,254,252,305,282,
            258,256,282,255,257,271,289,282,288,282,
            259,285,282,282,282,262,260,344,282,261,
            282,282,263,282,322,282,321,264,282,282,
            282,267,265,282,282,282,282,266,298,343,
            301,282,282,282,8,282,272,282,282,274,
            304,270,268,282,282,282,320,295,269,282,
            296,282,273,323,282,275,283,282,282,334,
            276,282,314,282,282,282,278,282,282,277,
            282,282,282,324,282,310,279,345,282,313
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
