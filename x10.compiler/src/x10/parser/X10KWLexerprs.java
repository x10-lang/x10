/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
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

    public final static int NUM_STATES = 203;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 53;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 333;
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
            10,9,5,6,3,4,5,6,8,7,
            7,8,9,6,6,4,6,6,5,6,
            4,5,6,9,4,3,4,3,3,4,
            4,5,7
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
            1,1,1,1,1,66,101,117,69,18,
            24,120,40,67,46,28,31,127,48,68,
            82,51,14,61,84,79,86,118,58,90,
            128,130,96,131,135,134,97,138,141,139,
            143,146,147,148,102,154,155,156,161,162,
            157,166,169,172,170,173,106,109,178,64,
            108,180,182,174,186,187,189,192,193,195,
            194,197,36,203,204,206,207,27,208,210,
            209,213,211,217,221,223,226,116,218,231,
            227,115,234,235,237,240,241,245,246,247,
            248,253,250,255,256,258,260,263,269,266,
            158,272,273,274,275,277,278,284,287,289,
            290,291,296,282,297,298,300,302,306,309,
            311,312,313,315,317,318,319,322,320,323,
            328,324,329,333,334,335,341,342,344,348,
            345,351,353,355,354,363,72,364,367,358,
            368,370,369,373,374,375,376,378,382,384,
            388,392,383,396,398,401,402,404,407,410,
            412,408,415,416,417,420,421,381,422,425,
            427,428,429,430,432,433,434,443,440,445,
            447,451,452,454,456,457,458,460,270,270
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,0,
            10,11,12,0,14,15,3,0,18,19,
            3,21,22,0,1,2,0,0,2,12,
            0,1,5,16,4,0,13,10,11,0,
            14,18,3,4,17,0,1,0,13,4,
            0,4,13,6,4,8,6,0,13,9,
            0,4,2,0,1,5,0,0,0,6,
            13,0,4,16,6,12,9,11,0,19,
            14,0,1,0,1,0,8,6,3,0,
            23,8,3,22,9,0,0,8,3,4,
            0,0,6,2,8,0,5,0,0,4,
            2,6,5,5,0,0,0,0,4,0,
            1,6,8,16,9,25,0,0,12,0,
            0,14,6,0,0,8,2,0,0,9,
            0,1,0,14,7,0,0,0,2,11,
            3,9,19,0,0,0,0,0,3,5,
            0,0,2,18,3,0,10,14,0,0,
            2,0,0,0,9,18,5,0,1,0,
            11,0,10,10,5,0,0,1,0,1,
            9,0,0,0,0,2,0,1,6,5,
            15,10,0,0,2,0,0,0,0,0,
            0,3,0,1,11,9,0,0,13,9,
            0,1,0,1,8,0,0,2,21,20,
            0,1,6,0,0,1,0,1,5,0,
            0,24,2,4,0,0,0,0,1,0,
            5,7,0,7,0,0,4,0,4,0,
            1,6,0,0,2,0,17,10,0,1,
            5,0,0,0,0,3,0,0,7,3,
            7,0,8,0,1,8,0,1,0,0,
            0,0,4,4,4,0,0,0,3,0,
            3,0,21,4,3,0,1,11,0,1,
            0,0,0,1,0,5,0,0,0,0,
            9,0,0,0,10,8,5,0,0,13,
            7,12,0,0,0,8,3,3,20,7,
            0,0,20,0,0,17,5,0,8,2,
            0,7,0,0,0,12,3,0,1,7,
            10,7,0,0,2,2,0,0,0,0,
            2,5,0,0,0,0,3,0,11,10,
            0,0,0,0,9,5,12,0,1,17,
            9,0,15,10,12,0,1,0,7,2,
            0,0,1,0,4,2,0,0,2,0,
            1,0,1,6,0,0,0,1,4,0,
            0,0,7,2,0,6,0,0,0,0,
            1,0,0,0,7,3,16,13,10,0,
            7,15,0,1,0,1,0,16,2,10,
            0,0,2,0,1,0,0,0,2,0,
            0,6,0,0,5,0,15,0,11,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            270,79,69,71,83,70,73,81,75,270,
            74,78,76,270,72,80,118,270,67,82,
            88,68,77,270,93,92,270,270,177,89,
            270,107,103,87,106,270,91,104,297,270,
            178,90,96,97,105,270,101,270,173,102,
            270,111,95,109,117,110,115,270,100,116,
            270,127,119,5,163,120,270,270,270,162,
            126,270,86,326,85,164,113,99,270,121,
            98,270,114,270,123,270,124,287,329,270,
            112,122,128,240,328,270,270,129,132,133,
            270,30,137,145,138,270,146,2,270,158,
            159,157,166,160,270,270,270,270,191,270,
            94,187,190,165,188,269,270,270,84,270,
            270,125,108,270,270,130,135,270,270,134,
            270,140,270,131,139,270,270,270,143,141,
            144,142,136,270,270,270,270,270,149,148,
            270,270,150,305,294,270,151,147,270,270,
            153,270,270,270,152,207,154,270,161,270,
            155,270,156,331,167,270,270,327,270,325,
            168,270,270,270,270,171,270,172,170,321,
            330,169,270,270,174,270,270,270,270,270,
            270,181,270,182,316,176,270,270,175,306,
            270,184,270,185,183,270,270,186,179,180,
            270,296,295,270,270,193,270,288,192,15,
            270,189,195,194,270,270,270,270,280,270,
            197,196,270,198,270,270,199,270,201,270,
            204,202,270,270,205,270,200,203,270,332,
            206,270,270,270,270,319,270,270,208,211,
            209,270,210,270,213,212,270,214,270,270,
            270,270,215,216,217,270,270,270,218,270,
            220,270,219,221,222,270,223,303,270,224,
            270,270,270,290,270,225,270,270,270,270,
            226,270,270,270,227,229,282,270,270,228,
            232,281,270,270,270,231,234,235,230,274,
            270,52,279,270,270,233,323,270,236,318,
            270,317,270,270,270,320,238,270,304,314,
            315,237,270,270,239,241,37,270,270,270,
            299,308,270,270,270,21,245,270,242,243,
            270,270,270,270,246,289,293,270,250,244,
            248,270,247,249,277,270,276,270,278,273,
            270,270,252,270,251,253,270,270,254,270,
            311,270,310,255,270,270,270,258,256,270,
            270,270,257,286,270,333,270,270,270,270,
            262,270,270,270,260,309,292,259,261,270,
            263,283,270,264,270,284,270,312,271,265,
            270,270,324,270,302,270,270,270,267,270,
            270,266,270,270,298,270,313,270,301
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
