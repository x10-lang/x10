
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//

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

    public final static int NUM_STATES = 253;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 407;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 75;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 76;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 26;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 331;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 332;
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
            5,4,8,3,3,3,2,2,3,2,
            10,6,6,6,5,7,6,6,7,6,
            4,5,4,11,3,2,4,4,10,6,
            5,4,8,6,5,4,5,5,5,8,
            7,2,4,7,5,5,7,3,4,2,
            10,6,10,9,6,3,4,7,7,9,
            6,6,6,8,5,6,12,4,5,6,
            9,4,3,8,5
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
            1,1,1,1,1,1,1,78,44,132,
            129,109,141,144,145,28,147,42,59,63,
            24,54,86,90,91,73,43,46,148,152,
            157,94,155,160,159,96,81,162,163,168,
            171,172,164,176,173,105,108,177,181,182,
            30,110,185,183,187,78,188,190,191,114,
            193,194,199,49,68,119,195,203,202,121,
            125,207,208,211,210,215,220,219,221,224,
            226,227,231,232,233,98,235,237,126,239,
            245,240,247,248,249,254,256,257,258,260,
            261,263,131,265,268,271,274,276,272,278,
            282,277,284,18,285,287,289,290,291,293,
            297,299,301,302,307,308,309,310,313,315,
            316,317,320,325,324,330,331,139,332,333,
            334,338,339,342,344,343,345,351,352,355,
            357,362,360,365,366,367,370,372,373,374,
            375,379,384,382,387,389,388,391,395,394,
            396,397,405,407,402,409,412,413,414,415,
            416,418,420,424,422,427,431,432,433,435,
            438,440,442,441,443,444,447,445,454,457,
            459,460,461,464,465,469,472,473,475,21,
            476,477,479,480,482,483,490,493,492,498,
            499,500,506,508,501,510,513,511,515,517,
            519,521,520,522,525,523,531,536,538,527,
            533,541,542,545,546,552,547,551,555,556,
            559,558,562,563,564,566,567,570,572,575,
            332,332
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,16,0,18,2,
            0,21,22,0,1,2,3,0,1,0,
            3,14,3,4,11,12,9,17,11,16,
            17,0,0,0,3,0,4,2,0,8,
            9,9,11,0,12,10,8,9,0,17,
            7,16,0,18,6,3,13,0,6,26,
            3,9,0,20,0,3,4,0,11,2,
            0,23,5,11,17,0,1,10,3,0,
            0,1,3,0,1,0,16,0,9,9,
            3,8,7,23,0,8,2,0,0,0,
            3,16,4,0,10,6,9,4,0,6,
            0,1,4,0,0,0,8,2,0,9,
            0,0,1,24,10,10,6,9,0,9,
            0,17,4,0,0,7,0,0,4,2,
            7,0,12,2,0,9,0,1,0,0,
            2,0,0,0,10,6,5,0,6,2,
            0,0,0,10,4,0,0,2,7,7,
            0,0,0,7,0,3,0,0,8,0,
            0,4,0,0,0,14,6,13,0,10,
            14,0,0,5,3,13,0,0,14,0,
            0,18,10,3,0,6,9,11,0,0,
            0,7,3,0,6,0,0,2,8,3,
            0,0,0,1,0,1,0,1,0,0,
            10,18,11,5,0,1,0,0,0,10,
            2,5,5,0,1,0,0,0,2,0,
            0,1,0,8,0,1,4,0,11,2,
            0,0,13,0,1,0,0,0,3,9,
            3,0,11,0,0,1,0,6,0,0,
            0,1,0,7,2,19,0,9,0,10,
            0,0,1,3,8,22,0,0,0,0,
            1,4,0,7,0,0,0,0,20,0,
            1,6,6,0,0,13,3,19,4,0,
            0,0,0,0,2,6,22,0,0,8,
            10,0,0,0,0,12,4,10,5,5,
            0,0,1,12,0,1,0,19,8,0,
            4,0,1,4,0,0,0,3,2,0,
            1,0,0,0,0,0,3,3,0,1,
            8,0,11,0,1,20,0,0,0,2,
            0,10,4,0,0,0,0,4,8,5,
            5,0,16,7,0,1,0,1,0,1,
            9,0,0,0,0,0,5,0,6,0,
            1,0,7,0,7,12,0,4,2,15,
            0,0,0,12,0,5,2,0,7,0,
            0,0,0,0,0,0,0,15,7,6,
            8,5,13,0,14,2,0,20,0,0,
            0,5,4,0,0,21,3,8,0,5,
            10,0,0,2,0,0,0,5,0,0,
            12,0,0,0,2,10,12,11,9,0,
            1,0,0,15,13,3,5,0,0,0,
            0,2,2,6,6,0,1,0,1,0,
            0,2,0,1,0,5,0,1,0,0,
            0,0,0,1,0,7,0,8,7,5,
            0,1,0,19,14,0,1,0,1,7,
            0,0,2,17,0,0,0,1,3,8,
            0,0,8,2,0,0,2,0,0,9,
            2,0,0,0,7,0,0,1,6,0,
            15,0,1,10,0,0,0,0,13,0,
            0,0,0,0,0,0,25,0,0,15,
            21,0,0,0,0,0,0,0,0,0,
            0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            332,88,96,97,79,89,86,85,91,84,
            90,83,92,87,95,94,82,332,81,231,
            332,80,93,332,121,120,122,332,106,332,
            104,232,167,166,119,123,105,379,103,118,
            117,332,332,332,108,332,134,139,332,111,
            110,340,109,332,135,140,182,183,332,136,
            125,138,332,137,112,115,382,332,116,331,
            185,114,332,124,332,132,133,10,184,173,
            332,113,175,131,405,332,126,174,127,332,
            332,130,129,332,145,332,388,332,128,374,
            205,144,149,150,332,206,159,332,332,332,
            161,357,100,332,160,169,162,338,332,178,
            332,192,186,332,7,332,187,208,332,191,
            332,332,98,168,193,209,221,99,332,220,
            332,194,250,332,332,249,332,332,358,141,
            102,332,101,142,332,107,332,143,332,332,
            148,332,332,332,146,147,151,332,152,153,
            332,332,332,156,154,332,332,157,155,158,
            332,332,332,163,332,171,332,332,164,332,
            332,176,332,332,332,165,177,170,332,341,
            172,332,332,181,190,179,332,332,188,332,
            332,180,189,196,332,197,381,195,332,332,
            332,364,198,332,389,332,332,355,199,201,
            332,332,48,204,332,375,332,207,332,332,
            202,200,203,210,332,368,332,332,332,211,
            214,212,213,332,215,332,332,332,217,332,
            332,359,332,216,332,222,219,332,218,223,
            332,5,360,332,353,332,332,4,225,224,
            227,332,226,332,332,404,332,228,332,332,
            332,334,332,233,235,229,332,234,332,400,
            332,332,239,238,236,230,332,332,332,332,
            407,241,332,240,332,332,332,332,237,332,
            377,244,354,332,332,242,246,367,245,332,
            332,332,332,332,371,247,243,332,332,251,
            248,332,332,332,332,369,397,370,254,255,
            332,332,257,253,332,258,332,252,256,332,
            259,332,260,261,332,332,332,262,264,332,
            363,332,332,332,332,332,267,268,332,269,
            266,332,265,332,270,263,332,332,332,347,
            332,271,273,332,332,332,332,276,274,275,
            333,332,272,394,332,362,332,387,332,352,
            277,332,46,332,332,332,278,332,279,332,
            282,332,349,332,281,350,332,283,284,280,
            332,332,332,398,332,395,384,332,285,332,
            332,332,332,332,332,332,332,344,288,291,
            290,393,287,332,289,293,332,286,332,332,
            69,294,295,332,332,292,297,296,332,346,
            402,332,332,366,332,332,332,298,332,332,
            345,332,332,332,302,376,351,299,300,332,
            303,332,332,348,301,304,305,332,332,332,
            332,373,308,306,307,332,390,332,391,332,
            332,309,332,310,332,311,332,372,332,332,
            332,332,332,317,332,313,332,314,315,316,
            332,318,332,312,396,332,406,332,319,320,
            332,332,365,335,332,332,332,386,323,321,
            332,332,322,324,332,332,326,332,332,325,
            403,332,332,332,327,332,332,343,361,332,
            392,332,329,383,332,332,332,332,385,332,
            332,332,332,332,332,332,328,332,332,399,
            356
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
