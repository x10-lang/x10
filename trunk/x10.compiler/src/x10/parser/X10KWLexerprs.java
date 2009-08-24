
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

    public final static int NUM_STATES = 265;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 427;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 79;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 80;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 26;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 347;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 348;
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
            10,6,10,9,6,3,4,8,7,7,
            9,5,6,6,6,6,8,6,5,6,
            12,4,5,6,9,4,3,8,5
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
            1,1,1,1,1,1,1,1,1,1,
            1,82,138,81,141,154,143,159,84,28,
            156,42,85,52,24,30,90,94,95,61,
            63,44,160,166,169,162,101,48,172,170,
            174,71,89,175,178,180,179,184,186,187,
            190,104,113,191,188,193,117,49,21,200,
            194,68,201,202,204,115,206,195,209,123,
            66,125,210,216,214,126,130,218,221,222,
            225,223,229,231,235,233,236,238,241,244,
            242,246,248,133,250,253,135,255,258,260,
            262,263,264,271,139,265,273,274,275,277,
            144,278,280,283,285,289,290,291,293,295,
            296,18,297,299,303,302,304,309,306,310,
            313,319,317,321,323,324,328,326,331,332,
            333,335,336,341,344,346,338,103,349,350,
            351,355,356,357,360,364,361,370,362,372,
            377,379,380,382,384,374,388,391,385,389,
            397,399,152,403,395,393,406,411,407,408,
            415,412,109,417,420,422,426,423,429,431,
            432,433,434,435,436,440,438,446,448,451,
            454,447,452,457,456,461,462,463,464,465,
            469,466,478,479,482,481,483,468,488,484,
            489,492,495,498,499,502,500,503,504,506,
            505,513,517,515,522,523,524,527,531,533,
            534,537,539,540,541,542,546,547,548,550,
            549,551,562,564,566,508,568,569,570,572,
            553,573,577,580,582,559,586,583,587,589,
            590,592,593,595,597,599,348,348
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
            3,14,5,13,11,12,0,8,11,16,
            17,0,13,0,3,2,5,0,0,20,
            9,0,11,10,3,7,5,10,7,16,
            0,18,0,3,4,0,4,0,3,2,
            0,11,24,6,12,0,11,10,8,17,
            0,1,17,0,0,5,16,4,0,0,
            1,7,3,0,0,1,3,14,5,5,
            0,1,0,0,16,2,4,23,0,9,
            8,23,0,10,0,3,0,5,4,3,
            4,7,0,15,0,0,1,5,4,0,
            5,9,0,9,0,3,2,0,0,10,
            0,9,0,0,10,5,17,9,5,11,
            7,0,1,0,12,0,5,4,0,0,
            5,0,1,26,5,0,8,2,0,0,
            2,0,1,0,0,2,7,0,0,0,
            6,2,4,0,7,0,0,0,2,0,
            0,8,0,0,0,10,9,8,8,0,
            0,0,3,0,4,0,14,14,0,0,
            7,10,18,0,6,0,3,0,13,2,
            0,0,0,14,0,10,5,3,0,7,
            0,11,0,3,0,0,8,0,4,7,
            0,0,2,0,9,0,3,0,1,0,
            1,10,0,1,0,18,11,0,1,0,
            6,0,0,0,0,2,2,6,6,10,
            0,1,0,0,0,1,0,0,1,0,
            4,2,0,11,0,1,13,5,0,0,
            0,3,0,3,0,0,0,1,0,7,
            11,0,0,0,1,0,8,5,0,0,
            2,10,0,19,9,3,0,22,0,1,
            0,1,0,0,8,0,4,0,1,20,
            0,0,0,3,0,0,1,0,13,7,
            0,7,19,0,4,0,3,10,0,0,
            0,2,7,22,0,0,0,9,0,0,
            0,0,12,0,10,6,6,4,12,0,
            9,0,1,0,19,6,0,1,0,0,
            1,0,4,0,0,4,3,0,0,2,
            0,1,0,20,0,11,0,9,0,3,
            0,3,0,1,10,0,0,0,16,4,
            0,0,2,6,0,9,0,6,4,0,
            1,0,0,2,8,0,1,5,0,1,
            0,0,0,0,0,0,6,0,7,0,
            1,4,8,8,12,0,0,0,15,2,
            0,0,2,0,8,0,0,12,2,6,
            0,0,0,0,0,0,15,0,0,8,
            3,7,9,13,6,20,14,0,0,2,
            0,0,0,0,6,4,21,0,0,9,
            2,0,10,6,0,12,5,0,0,0,
            6,0,0,0,0,0,2,0,5,12,
            11,10,0,1,0,17,0,15,13,3,
            6,0,0,0,17,2,0,1,7,7,
            0,1,0,0,2,2,0,1,0,0,
            0,0,1,4,6,0,0,0,0,0,
            0,1,0,8,6,3,9,8,0,19,
            14,0,1,0,1,0,1,0,0,0,
            2,0,0,15,2,8,0,1,9,0,
            9,0,0,2,5,0,0,2,0,0,
            8,0,0,1,0,7,0,1,0,10,
            0,0,0,0,13,0,0,0,0,0,
            0,25,0,15,0,21,0,0,0,0,
            0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            348,92,100,101,83,88,93,90,89,95,
            94,87,96,91,99,98,86,348,85,242,
            348,84,97,348,127,126,128,348,112,348,
            110,243,111,178,125,129,348,131,109,124,
            123,348,398,348,114,145,116,348,348,130,
            117,348,115,146,121,177,120,153,122,144,
            348,143,348,138,139,348,140,10,193,181,
            348,137,176,183,141,348,192,182,157,142,
            348,103,425,348,348,102,373,374,348,348,
            132,118,133,348,348,136,135,108,134,390,
            348,152,348,348,404,167,263,119,348,151,
            262,158,348,168,348,169,348,170,355,175,
            174,186,348,413,348,3,200,191,194,8,
            199,190,348,195,348,215,218,348,348,201,
            348,216,348,348,219,104,202,227,231,226,
            232,348,283,348,106,348,410,105,348,348,
            113,348,150,347,147,348,107,148,348,348,
            149,348,154,348,348,156,155,348,348,348,
            159,161,162,348,160,348,348,348,165,348,
            348,163,348,348,348,164,172,166,171,348,
            348,348,179,348,184,348,173,180,348,348,
            185,357,188,348,189,348,198,348,187,203,
            348,348,348,196,348,197,397,205,348,206,
            348,204,348,207,348,348,380,348,208,405,
            348,348,371,348,209,348,211,48,214,348,
            391,212,348,217,348,210,213,348,384,348,
            220,348,348,348,348,224,228,222,223,221,
            348,225,348,348,348,375,348,348,233,348,
            230,234,348,229,348,369,376,235,348,6,
            5,236,348,238,348,348,348,424,348,239,
            237,348,348,348,350,348,244,245,348,348,
            246,420,348,240,247,249,348,241,348,250,
            348,252,348,348,251,348,253,348,427,248,
            348,348,348,255,348,348,393,348,254,257,
            348,370,383,348,258,348,259,261,348,348,
            348,387,260,256,348,348,348,264,348,348,
            348,348,385,348,386,267,268,417,266,348,
            270,348,271,348,265,269,348,272,348,348,
            274,348,273,348,348,275,276,348,348,278,
            348,379,348,277,348,279,348,280,348,281,
            348,282,348,284,285,348,348,348,286,287,
            348,348,363,289,348,288,348,349,290,348,
            378,348,348,291,412,348,403,292,348,368,
            348,46,348,348,348,348,293,348,294,348,
            297,298,365,296,366,348,348,348,295,416,
            348,348,299,348,300,348,348,418,400,414,
            348,348,348,348,348,348,360,348,348,303,
            312,306,305,302,411,301,304,348,348,308,
            348,348,73,348,309,310,307,348,348,311,
            382,348,422,362,348,361,313,348,348,348,
            314,348,348,348,348,348,318,348,316,367,
            315,392,348,319,348,395,348,364,317,320,
            321,348,348,348,352,389,348,407,322,323,
            348,408,348,348,324,325,348,326,348,348,
            348,348,388,406,327,348,348,348,348,348,
            348,333,348,329,332,339,330,331,348,328,
            415,348,334,348,426,348,335,348,348,348,
            381,348,348,409,340,336,348,402,337,348,
            338,348,348,342,341,348,348,423,348,348,
            343,348,348,359,348,377,348,345,348,399,
            348,348,348,348,401,348,348,348,348,348,
            348,344,348,419,348,372
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
