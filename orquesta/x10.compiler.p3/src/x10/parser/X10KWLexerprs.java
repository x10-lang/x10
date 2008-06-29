
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

public class X10KWLexerprs implements lpg.runtime.ParseTable, X10KWLexersym {

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
            5,4,8,3,3,3,2,2,3,2,
            10,6,6,5,7,6,6,7,6,4,
            5,4,11,3,2,4,4,10,6,5,
            4,8,6,5,4,5,5,5,8,7,
            2,4,7,5,5,7,3,4,2,10,
            6,10,9,6,3,4,7,7,9,6,
            6,6,8,5,6,12,4,5,6,9,
            4,3,8,5
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
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,77,37,20,138,
            108,17,65,24,148,33,149,38,79,55,
            62,28,45,89,70,43,51,150,151,159,
            91,57,157,154,161,93,101,164,105,165,
            97,94,166,168,169,172,174,177,181,178,
            85,104,182,76,185,188,186,111,189,190,
            193,114,72,119,195,194,199,120,124,200,
            203,207,213,214,216,217,222,205,215,225,
            226,228,229,231,233,232,242,235,239,234,
            246,128,251,253,133,255,256,248,260,132,
            261,263,266,267,269,270,271,273,276,277,
            142,279,282,283,284,285,287,290,291,293,
            300,297,302,304,305,307,308,310,313,312,
            317,326,315,324,328,330,332,335,336,334,
            139,340,343,342,346,347,350,351,353,358,
            349,362,367,354,363,370,372,376,378,374,
            380,381,383,382,318,386,387,392,396,394,
            389,403,400,406,407,409,410,413,414,415,
            399,420,421,422,429,424,425,430,432,433,
            434,438,435,443,447,448,449,440,458,459,
            452,460,464,454,467,468,470,471,474,475,
            476,477,479,481,489,490,496,491,499,501,
            502,505,507,508,509,480,497,512,515,518,
            519,524,526,528,517,530,532,531,535,537,
            540,543,541,546,547,549,552,553,554,555,
            556,561,566,557,559,327,327
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,0,17,18,0,
            1,21,22,0,1,2,3,0,1,13,
            3,0,0,1,11,3,0,0,15,16,
            3,9,0,11,0,8,9,3,11,7,
            0,9,2,9,0,13,0,3,16,5,
            10,0,26,9,0,15,10,6,18,0,
            6,0,3,12,3,0,7,2,0,4,
            11,20,11,5,0,10,2,16,0,1,
            0,1,0,0,10,3,0,9,8,7,
            0,23,6,0,0,5,3,0,15,5,
            0,15,9,0,7,5,23,7,0,0,
            1,8,9,0,24,7,8,0,9,0,
            3,0,0,10,2,8,5,0,0,16,
            9,0,10,2,6,7,9,0,0,0,
            0,2,2,0,7,14,0,9,0,1,
            0,8,6,0,0,0,2,0,0,4,
            2,0,5,0,14,12,0,0,7,6,
            0,0,2,6,0,0,10,0,0,0,
            5,7,0,0,0,14,4,10,0,0,
            12,3,0,10,0,1,0,18,14,3,
            11,9,0,0,0,0,0,5,3,6,
            4,0,8,2,0,0,2,0,0,1,
            0,0,0,0,0,5,11,3,0,12,
            8,0,0,2,11,0,1,0,10,18,
            0,1,0,1,0,0,1,10,4,0,
            0,1,0,4,2,0,0,1,0,0,
            0,3,0,3,9,0,0,5,0,1,
            11,0,0,0,0,1,0,6,2,0,
            0,9,0,10,19,3,0,8,22,0,
            1,0,6,0,0,1,0,0,7,0,
            20,0,0,4,0,4,0,0,12,7,
            13,4,19,0,8,0,1,0,5,0,
            1,0,5,0,0,0,22,3,7,0,
            5,0,0,10,2,0,0,8,0,0,
            0,1,0,0,13,10,7,0,0,7,
            3,0,0,2,11,19,0,1,20,0,
            8,0,3,0,3,0,1,0,1,0,
            0,0,0,10,2,0,0,7,0,8,
            4,0,7,0,15,0,1,6,0,0,
            7,13,0,4,2,0,0,1,0,0,
            1,6,0,0,0,17,4,9,5,0,
            0,0,1,0,0,6,6,13,0,0,
            2,0,0,0,0,6,12,0,5,0,
            8,4,0,20,2,14,0,0,0,10,
            4,0,0,0,7,21,8,0,0,0,
            3,2,4,0,13,12,0,0,2,0,
            0,4,9,0,0,0,0,1,0,0,
            0,3,13,10,4,11,16,8,0,0,
            0,1,17,5,5,0,0,2,0,1,
            0,0,2,2,0,1,0,0,0,0,
            4,0,1,6,0,19,0,0,0,1,
            6,4,14,0,1,0,1,0,1,0,
            0,0,16,2,0,6,0,3,8,0,
            0,2,0,1,8,0,0,2,0,9,
            2,0,0,0,0,0,0,5,0,6,
            0,1,0,17,10,0,1,12,0,0,
            0,0,0,0,0,17,25,21,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            327,88,95,96,89,86,85,78,90,84,
            83,82,87,91,94,81,327,93,80,327,
            97,79,92,327,106,105,107,327,124,100,
            125,327,327,111,104,109,327,327,103,102,
            113,110,327,108,327,116,115,127,114,132,
            327,335,137,126,327,133,327,120,134,121,
            138,327,326,119,327,136,144,123,135,327,
            101,327,130,376,182,10,131,170,327,172,
            129,122,181,117,327,171,165,399,327,128,
            327,143,327,327,166,149,327,368,142,148,
            327,118,155,327,327,151,167,327,382,153,
            327,351,168,327,99,175,156,333,327,327,
            189,179,180,7,150,183,184,327,188,327,
            208,327,327,190,211,209,217,327,327,191,
            216,327,212,227,250,251,98,327,327,327,
            327,139,140,327,352,228,327,112,327,141,
            327,146,145,327,327,327,154,327,327,157,
            159,327,158,327,147,152,327,327,160,161,
            327,327,163,164,327,327,162,327,327,327,
            174,173,327,327,327,169,178,336,327,327,
            176,187,327,186,327,198,327,177,185,193,
            192,375,327,327,327,327,327,194,195,358,
            196,327,199,197,327,327,200,327,327,353,
            327,327,327,327,327,383,201,204,327,354,
            202,327,327,349,206,47,207,327,205,203,
            327,369,327,210,327,327,362,214,213,327,
            327,218,327,215,219,327,327,347,327,5,
            4,221,327,223,220,327,327,224,327,398,
            222,327,327,327,327,329,327,229,231,327,
            327,230,327,394,225,234,327,232,226,327,
            235,327,236,327,327,401,327,327,237,327,
            233,327,327,240,327,241,327,327,238,391,
            239,270,361,327,242,327,243,327,245,327,
            371,327,348,327,327,327,244,247,246,327,
            248,327,327,249,365,327,327,252,327,327,
            327,255,327,327,363,364,254,327,327,256,
            257,327,327,259,260,253,327,357,258,327,
            261,327,262,327,263,327,264,327,265,327,
            327,327,327,266,341,327,327,268,327,269,
            328,327,271,327,267,327,356,388,327,327,
            272,392,327,389,273,327,327,381,327,327,
            346,274,327,45,327,278,276,275,277,327,
            327,327,280,327,327,343,279,344,327,327,
            378,327,327,327,327,283,282,327,286,68,
            285,387,327,281,288,284,327,327,327,396,
            289,327,327,327,290,287,291,327,327,327,
            292,360,340,327,339,294,327,327,295,327,
            327,296,293,327,327,327,327,298,327,327,
            327,299,345,370,300,297,373,308,327,327,
            327,384,342,301,302,327,327,367,327,385,
            327,327,303,304,327,305,327,327,327,327,
            306,327,366,307,327,309,327,327,327,312,
            310,311,390,327,313,327,400,327,314,327,
            327,327,330,359,327,315,327,317,316,327,
            327,319,327,380,318,327,327,321,327,320,
            397,327,327,327,327,327,327,355,327,323,
            327,338,327,386,377,327,324,379,327,327,
            327,327,327,327,327,393,322,350
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
    public final int getErrorSymbol() { return 0; }
    public final int getScopeUbound() { return 0; }
    public final int getScopeSize() { return 0; }
    public final int getMaxNameLength() { return 0; }

    public final static int
           NUM_STATES        = 249,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 401,
           MAX_LA            = 1,
           NUM_RULES         = 74,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 75,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 26,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 326,
           ERROR_ACTION      = 327;

    public final static boolean BACKTRACK = false;

    public final int getNumStates() { return NUM_STATES; }
    public final int getNtOffset() { return NT_OFFSET; }
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }
    public final int getMaxLa() { return MAX_LA; }
    public final int getNumRules() { return NUM_RULES; }
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }
    public final int getNumSymbols() { return NUM_SYMBOLS; }
    public final int getSegmentSize() { return SEGMENT_SIZE; }
    public final int getStartState() { return START_STATE; }
    public final int getStartSymbol() { return lhs[0]; }
    public final int getIdentifierSymbol() { return IDENTIFIER_SYMBOL; }
    public final int getEoftSymbol() { return EOFT_SYMBOL; }
    public final int getEoltSymbol() { return EOLT_SYMBOL; }
    public final int getAcceptAction() { return ACCEPT_ACTION; }
    public final int getErrorAction() { return ERROR_ACTION; }
    public final boolean isValidForParser() { return isValidForParser; }
    public final boolean getBacktrack() { return BACKTRACK; }

    public final int originalState(int state) { return 0; }
    public final int asi(int state) { return 0; }
    public final int nasi(int state) { return 0; }
    public final int inSymbol(int state) { return 0; }

    public final int ntAction(int state, int sym) {
        return baseAction[state + sym];
    }

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
