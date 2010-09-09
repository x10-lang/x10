
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
            5,4,2,8,3,3,3,2,3,2,
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
            1,1,1,1,1,1,1,78,132,32,
            136,139,77,137,145,28,140,34,70,47,
            24,52,79,81,20,63,56,46,151,152,
            147,87,155,156,157,83,85,160,161,166,
            169,163,171,172,175,94,97,176,177,178,
            102,98,181,184,179,49,186,188,189,109,
            191,196,198,61,75,110,199,200,202,111,
            116,201,206,207,211,208,218,219,220,222,
            224,227,223,231,233,120,235,237,117,239,
            245,241,243,247,249,253,255,256,257,259,
            260,262,124,264,267,270,273,275,271,276,
            277,281,284,127,285,287,288,290,291,293,
            289,296,301,302,305,307,308,309,313,314,
            315,317,318,320,322,328,329,131,331,330,
            333,335,337,340,341,343,348,342,354,357,
            359,360,362,364,349,368,371,365,373,374,
            375,379,382,384,385,386,387,388,392,391,
            394,398,402,406,400,408,411,412,413,404,
            414,415,423,426,417,431,427,428,434,436,
            420,437,439,441,443,442,447,444,454,453,
            457,459,460,463,468,462,470,471,475,464,
            477,478,479,480,482,483,491,493,497,498,
            499,501,506,508,510,511,514,516,500,517,
            520,522,523,525,524,533,537,539,541,526,
            528,544,545,547,548,550,556,549,558,554,
            561,564,18,562,565,566,572,567,575,570,
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
            10,11,12,13,14,15,16,0,18,0,
            1,21,22,0,1,2,3,0,1,10,
            3,0,1,0,11,12,3,10,11,16,
            17,8,25,10,11,0,0,2,0,3,
            2,0,6,5,9,0,10,9,7,4,
            0,16,0,18,13,3,4,12,8,0,
            10,20,17,11,0,6,0,3,0,1,
            0,3,0,3,0,11,0,1,12,7,
            10,17,23,0,8,2,0,0,16,3,
            16,0,9,6,3,4,10,23,0,0,
            0,1,4,4,6,0,0,8,2,0,
            10,24,3,0,9,9,0,8,2,6,
            0,0,17,10,4,0,0,7,0,0,
            14,0,4,7,0,10,0,1,4,10,
            0,0,2,2,0,0,0,26,2,0,
            0,6,0,9,5,0,6,2,0,7,
            0,0,4,2,0,0,0,0,0,9,
            0,7,7,0,8,0,3,0,0,4,
            0,14,14,13,6,0,9,0,0,0,
            0,0,5,13,3,0,0,0,9,3,
            0,11,14,18,7,10,6,0,0,0,
            3,0,0,0,6,2,0,8,0,3,
            0,9,0,1,0,1,0,1,0,18,
            0,11,0,5,0,1,0,5,0,9,
            2,5,0,1,0,0,0,2,0,0,
            1,0,8,0,1,4,0,11,2,0,
            0,13,0,1,0,0,0,3,3,10,
            0,11,6,0,0,1,0,0,0,0,
            0,1,0,7,2,0,8,10,9,19,
            0,0,1,3,0,22,0,0,0,1,
            4,7,0,0,0,20,0,0,1,0,
            6,0,6,4,3,13,19,0,0,0,
            0,2,0,6,0,22,0,9,8,0,
            0,0,0,9,12,5,4,0,0,8,
            0,12,5,0,1,19,0,1,0,0,
            1,0,4,0,0,4,3,0,20,2,
            0,1,0,0,0,11,3,3,0,1,
            8,0,1,0,0,0,0,0,2,4,
            0,0,9,0,4,8,5,0,5,0,
            16,0,1,0,7,0,1,0,1,10,
            0,0,0,0,0,5,0,6,15,0,
            7,7,0,1,12,0,0,0,12,4,
            0,5,2,0,7,0,0,2,0,20,
            0,0,0,0,0,7,0,6,15,13,
            8,5,0,0,14,2,0,5,0,0,
            4,0,0,0,21,3,8,0,9,0,
            0,2,5,12,0,5,0,0,0,0,
            17,0,0,0,2,9,12,0,11,10,
            0,1,0,15,13,3,0,0,0,0,
            0,5,2,6,6,0,1,0,1,0,
            0,2,2,0,1,0,0,1,19,0,
            5,0,0,0,0,0,7,0,5,0,
            8,7,0,1,7,14,0,1,0,1,
            0,1,17,0,0,2,0,0,0,0,
            3,2,8,0,8,0,1,0,10,2,
            0,0,2,0,0,0,0,6,15,0,
            7,0,1,9,0,1,0,0,13,0,
            0,0,0,0,15,0,0,21,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            332,88,96,97,79,89,86,85,91,90,
            84,83,92,87,95,94,82,332,81,332,
            130,80,93,332,121,120,122,332,106,374,
            104,332,98,332,119,123,108,105,103,118,
            117,111,328,110,109,332,332,139,10,115,
            173,332,116,175,140,332,114,174,125,134,
            332,138,332,137,382,132,133,135,182,332,
            183,124,136,131,332,112,332,185,332,126,
            332,127,332,129,332,184,332,145,101,149,
            128,405,113,332,144,159,332,332,357,161,
            388,332,160,169,167,166,162,150,332,332,
            3,192,339,186,178,8,332,187,208,332,
            191,168,205,332,193,209,332,206,231,221,
            332,332,194,220,250,332,332,249,332,332,
            232,332,100,102,332,99,332,143,358,107,
            332,332,141,142,332,332,332,331,148,332,
            332,147,332,146,151,332,152,153,332,155,
            332,332,154,157,332,332,332,332,332,156,
            332,158,163,332,164,332,171,332,332,176,
            332,165,172,170,177,332,341,332,332,332,
            332,332,181,179,190,332,332,332,189,196,
            332,195,188,180,364,381,197,332,332,332,
            198,332,332,332,389,355,332,199,332,201,
            332,202,48,204,332,375,332,207,332,200,
            332,203,332,210,332,368,332,212,332,211,
            214,213,332,215,332,332,332,217,332,332,
            359,332,216,332,222,219,332,218,223,332,
            6,360,332,353,332,5,332,225,227,224,
            332,226,228,332,332,404,332,332,332,332,
            332,334,332,233,235,332,236,234,400,229,
            332,332,239,238,332,230,332,332,332,407,
            241,240,332,332,332,237,332,332,377,332,
            244,332,354,245,246,242,367,332,332,332,
            332,371,332,247,332,243,332,248,251,332,
            332,332,332,370,369,254,397,332,332,256,
            332,253,255,332,257,252,332,258,332,332,
            260,332,259,332,332,261,262,332,263,264,
            332,363,332,332,332,265,267,268,332,269,
            266,332,270,332,332,332,332,332,347,273,
            332,332,271,332,276,274,275,332,333,332,
            272,332,362,332,394,332,387,332,352,277,
            332,46,332,332,332,278,332,279,280,332,
            349,281,332,282,350,332,332,332,398,283,
            332,395,284,332,285,332,332,384,332,286,
            332,332,332,332,332,288,332,291,344,287,
            290,393,332,332,289,293,332,294,332,69,
            295,332,332,332,292,297,296,332,402,332,
            332,366,346,345,332,298,332,332,332,332,
            379,332,332,332,302,376,351,332,299,300,
            332,303,332,348,301,304,332,332,332,332,
            332,305,373,306,307,332,390,332,391,332,
            332,308,309,332,310,332,332,372,312,332,
            311,332,332,332,332,332,313,332,316,332,
            314,315,332,317,320,396,332,318,332,406,
            332,319,336,332,332,365,332,332,332,332,
            323,324,321,332,322,332,386,332,325,326,
            332,332,403,332,332,332,332,361,392,332,
            327,332,343,383,332,329,332,332,385,332,
            332,332,332,332,399,332,332,356
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
