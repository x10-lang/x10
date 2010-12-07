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
/*****************************************************
 * WARNING!  THIS IS A GENERATED FILE.  DO NOT EDIT! *
 *****************************************************/

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

    public final static int NUM_STATES = 198;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 53;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 324;
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

    public final static int ACCEPT_ACTION = 262;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 263;
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
            4,5,9,4,3,4,3,3,4,5,
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
            1,1,1,64,70,104,121,28,24,84,
            27,60,33,37,75,125,45,68,39,55,
            126,61,81,12,115,56,65,10,131,83,
            133,88,134,92,135,136,138,139,79,144,
            96,140,147,151,156,157,149,160,162,163,
            165,166,103,107,170,50,108,167,173,174,
            178,180,175,183,186,187,192,94,194,188,
            195,197,198,114,199,201,205,202,204,210,
            211,214,216,111,217,222,224,119,225,226,
            228,232,234,233,237,238,245,240,247,248,
            249,252,254,257,250,260,261,262,263,265,
            268,270,271,274,276,280,281,282,286,288,
            289,291,292,297,299,301,295,302,305,307,
            308,310,312,311,313,314,319,320,323,328,
            321,331,334,335,337,338,340,342,345,350,
            343,351,354,355,357,358,360,361,363,364,
            366,370,371,372,373,379,376,381,384,390,
            392,393,396,398,400,401,402,405,403,409,
            411,412,415,413,417,418,419,421,427,432,
            433,425,439,441,442,430,447,445,449,450,
            453,263,263
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,0,
            10,0,12,13,14,4,16,8,18,19,
            9,21,22,0,1,2,0,0,0,3,
            4,4,0,1,11,3,0,11,0,1,
            13,18,15,11,0,7,10,3,12,0,
            1,7,8,17,0,0,7,3,3,0,
            0,7,2,9,0,5,11,0,4,0,
            15,12,8,14,0,1,9,3,0,19,
            0,1,0,0,1,3,4,0,8,2,
            23,0,5,0,25,0,18,2,7,8,
            5,23,0,0,11,3,0,0,2,7,
            0,5,5,0,0,2,13,7,0,9,
            0,3,15,3,0,0,8,14,14,4,
            0,7,0,0,0,0,1,0,0,0,
            6,9,0,0,14,2,0,9,0,12,
            0,5,19,14,4,0,0,2,10,0,
            4,0,0,2,0,0,0,5,9,0,
            1,5,0,0,0,10,12,0,1,0,
            1,9,0,10,10,0,0,0,2,7,
            5,0,1,0,0,2,0,0,0,12,
            0,0,1,0,0,11,9,11,4,0,
            0,2,9,0,1,0,0,2,8,21,
            20,0,1,0,0,0,1,0,1,5,
            7,0,0,0,3,2,0,0,6,0,
            24,5,3,6,0,1,0,0,0,0,
            3,0,1,0,5,2,0,1,10,0,
            0,0,0,17,0,4,6,0,6,0,
            0,4,8,0,1,0,1,18,8,0,
            0,0,3,3,3,0,17,0,0,4,
            0,0,4,3,0,4,0,1,0,1,
            0,0,1,9,0,5,0,0,21,0,
            0,0,0,0,10,8,5,11,0,0,
            0,8,0,13,6,6,4,0,8,20,
            0,4,20,0,0,2,0,0,1,0,
            6,0,0,13,0,6,10,6,4,0,
            0,2,2,0,0,1,0,0,5,0,
            0,2,0,0,22,0,4,10,12,0,
            0,0,0,1,9,0,13,17,0,9,
            0,10,2,0,6,16,3,0,13,0,
            1,0,0,2,2,0,1,0,1,0,
            0,0,0,3,0,1,7,6,0,7,
            0,0,0,2,0,5,0,0,0,1,
            0,0,6,15,0,11,0,10,16,0,
            4,0,0,1,10,15,0,6,0,1,
            0,0,2,2,0,16,0,1,0,0,
            2,7,0,0,0,0,0,5,0,0,
            0,12,0,0,0,0,0,0,0,0,
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
            263,77,67,81,69,68,79,71,73,263,
            72,263,76,74,70,321,78,125,65,80,
            320,66,75,263,90,89,263,263,263,94,
            93,85,263,98,88,99,263,92,263,110,
            86,87,84,97,263,279,100,107,289,5,
            159,105,106,101,263,263,158,113,122,263,
            263,111,115,112,263,116,121,263,123,263,
            318,96,124,95,263,103,109,102,263,117,
            263,119,263,263,91,128,127,263,118,130,
            108,263,131,263,262,29,296,141,133,134,
            142,139,263,263,168,154,263,2,155,153,
            263,156,161,263,263,173,82,182,263,183,
            263,186,160,83,263,263,185,174,120,114,
            263,104,263,263,263,263,136,263,263,263,
            135,129,263,263,126,140,263,138,263,137,
            263,144,132,143,145,263,263,146,147,263,
            286,263,263,149,263,263,263,150,148,263,
            157,162,263,263,263,152,151,263,319,263,
            317,163,263,322,164,263,263,263,166,165,
            314,263,167,263,263,169,263,263,263,309,
            263,263,178,263,263,170,172,171,177,263,
            263,297,298,263,180,263,263,181,179,175,
            176,263,288,263,263,263,188,263,280,187,
            287,14,263,263,189,190,263,263,191,263,
            184,192,194,193,263,272,263,263,263,263,
            196,263,198,263,200,199,263,323,197,263,
            263,263,263,195,263,312,201,263,202,263,
            263,204,203,263,207,263,208,315,206,263,
            263,263,209,210,211,263,205,263,263,212,
            263,263,214,215,263,216,263,217,263,218,
            263,263,282,220,263,219,263,263,213,263,
            263,263,263,263,221,223,274,222,263,263,
            263,225,263,273,226,267,227,263,229,224,
            263,228,271,263,263,311,263,263,307,263,
            310,263,263,313,263,306,308,230,231,263,
            263,232,234,36,263,295,263,263,300,263,
            263,291,263,263,233,20,238,236,235,263,
            263,263,263,243,239,263,285,237,263,241,
            263,242,266,263,270,240,244,263,269,263,
            245,263,263,246,247,263,303,263,302,263,
            263,263,263,249,263,251,248,250,263,324,
            263,263,263,278,263,281,263,263,263,255,
            263,263,253,284,263,252,263,254,275,263,
            301,263,263,257,258,304,263,256,263,276,
            263,263,264,316,263,305,263,294,263,263,
            260,259,263,263,263,263,263,290,263,263,
            263,293
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
