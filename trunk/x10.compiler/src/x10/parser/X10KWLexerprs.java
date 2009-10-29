
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

    public final static int NUM_STATES = 267;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 429;
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

    public final static int ACCEPT_ACTION = 349;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 350;
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
            4,8,6,5,4,5,5,5,8,7,
            2,4,7,5,5,7,3,4,6,2,
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
            1,82,30,81,83,160,21,103,86,28,
            70,34,84,47,24,46,92,94,101,62,
            63,42,166,18,168,169,149,48,163,174,
            176,177,88,99,178,180,181,184,187,188,
            190,191,106,109,193,194,196,114,118,200,
            202,197,69,204,206,208,120,207,212,215,
            123,61,128,217,218,221,130,129,223,110,
            219,229,232,234,236,238,242,239,243,246,
            248,251,249,255,226,135,256,260,141,258,
            262,267,268,269,270,273,136,278,271,279,
            283,281,148,287,289,290,295,297,298,293,
            285,55,153,302,305,306,307,308,312,311,
            315,316,321,318,323,325,328,327,332,329,
            334,336,338,339,340,343,347,349,351,156,
            353,352,356,357,359,360,363,364,366,374,
            371,375,382,379,385,387,388,377,393,389,
            390,398,402,158,403,396,394,407,409,408,
            413,414,415,416,418,421,423,428,432,425,
            434,436,438,437,439,440,442,445,447,441,
            455,456,459,460,461,462,451,465,467,463,
            470,474,481,469,482,485,486,487,473,494,
            493,489,502,495,498,503,140,506,507,508,
            509,511,510,519,514,521,525,526,527,533,
            535,537,538,541,543,544,545,546,522,548,
            551,553,552,554,556,565,567,558,563,570,
            569,573,574,579,583,576,585,586,588,589,
            591,592,593,594,595,599,604,600,350,350
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
            3,4,12,0,11,12,3,4,11,16,
            17,0,9,2,11,0,0,0,1,3,
            4,10,6,8,0,26,9,16,13,18,
            0,0,0,3,3,20,5,5,0,0,
            2,11,11,4,12,7,22,17,10,17,
            0,1,0,0,4,0,4,0,6,6,
            5,0,1,0,3,8,3,4,0,14,
            0,1,0,16,4,0,23,2,0,0,
            8,3,4,0,16,10,3,0,5,0,
            11,23,0,6,5,6,4,0,0,0,
            1,9,5,4,0,0,9,3,10,0,
            0,24,2,9,9,17,11,0,0,1,
            10,4,0,6,2,0,17,0,1,0,
            5,4,0,8,5,0,14,0,0,4,
            2,4,10,0,1,0,0,0,2,0,
            0,6,2,0,7,6,0,0,5,0,
            0,2,0,0,8,0,0,10,8,0,
            8,0,9,0,3,0,0,0,5,14,
            14,0,13,6,0,10,0,0,0,13,
            0,7,0,3,2,0,1,10,0,18,
            14,0,4,0,3,0,18,0,0,6,
            3,0,0,8,6,0,5,0,0,2,
            0,9,0,3,0,0,1,0,10,0,
            1,0,1,18,7,11,0,0,0,0,
            0,2,0,1,7,7,10,0,0,2,
            0,11,0,1,0,5,0,1,0,0,
            2,13,0,4,0,1,0,0,6,3,
            3,0,1,19,0,0,0,0,1,4,
            0,0,8,2,0,0,10,0,3,9,
            0,1,0,1,0,8,0,0,0,5,
            3,0,1,0,20,0,3,0,0,0,
            1,13,0,6,6,19,0,5,0,3,
            0,0,0,2,6,0,0,22,0,0,
            10,9,0,0,0,0,10,12,5,7,
            0,12,7,0,0,1,0,19,0,9,
            7,0,1,5,0,1,0,0,0,0,
            3,5,0,0,2,0,20,0,9,11,
            3,0,0,1,3,10,0,0,0,16,
            2,5,0,0,0,0,9,0,5,7,
            0,7,0,1,0,8,6,0,4,2,
            15,0,1,0,1,0,0,0,0,0,
            0,0,7,6,0,1,0,8,12,8,
            0,5,12,15,0,0,2,2,0,0,
            0,0,0,2,0,7,0,8,0,0,
            20,0,0,0,8,15,14,13,9,6,
            0,0,10,2,0,0,0,7,0,21,
            5,7,0,0,0,9,3,0,4,7,
            12,0,0,2,7,0,0,0,0,0,
            0,2,4,0,12,10,3,11,0,1,
            0,0,15,13,0,0,0,7,2,8,
            6,6,0,1,0,1,0,0,2,2,
            0,1,0,0,0,0,1,0,5,7,
            0,0,0,0,1,0,1,0,7,9,
            8,14,0,19,0,1,0,1,0,0,
            8,2,0,0,17,0,3,9,0,4,
            2,9,0,1,0,0,2,0,0,2,
            0,0,0,0,0,1,8,6,0,0,
            15,0,10,0,1,0,13,0,0,0,
            0,0,0,0,15,25,0,0,0,21,
            0,0,0,0,0,0,0,0,0,0,
            0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            350,92,100,101,88,83,90,93,89,95,
            94,87,96,91,99,98,86,350,85,149,
            350,84,97,350,128,127,129,350,113,350,
            111,112,107,350,126,130,115,117,110,125,
            124,350,118,146,116,350,350,350,154,122,
            121,147,123,132,350,349,153,145,400,144,
            350,350,350,194,139,131,140,141,10,350,
            183,193,138,114,142,185,242,427,184,143,
            350,103,350,350,102,350,105,350,104,119,
            376,350,133,350,134,159,136,135,350,109,
            350,137,350,375,391,350,120,169,350,350,
            108,171,172,350,406,170,177,350,176,350,
            205,160,350,179,357,356,192,350,8,3,
            201,191,195,200,350,350,196,217,202,350,
            350,178,220,218,229,203,228,350,350,152,
            221,233,350,234,243,350,396,350,285,350,
            265,412,350,264,106,350,244,350,350,148,
            151,150,155,350,156,350,350,350,158,350,
            350,157,163,350,161,162,350,350,164,350,
            350,167,350,350,165,350,350,166,168,350,
            173,350,174,350,181,350,350,350,186,175,
            182,350,180,187,350,359,350,350,350,188,
            350,190,350,199,204,47,216,198,350,189,
            197,350,398,350,207,350,206,350,350,208,
            209,350,350,381,407,350,210,350,350,373,
            350,211,350,213,350,350,392,350,214,350,
            219,350,385,212,222,215,350,350,350,350,
            350,226,350,227,224,225,223,350,350,230,
            350,231,350,377,350,232,350,235,350,350,
            236,378,350,237,350,371,350,5,240,238,
            239,350,426,241,350,350,350,350,352,246,
            350,350,245,247,350,350,422,350,250,248,
            350,251,350,253,350,252,350,350,350,254,
            255,350,429,350,249,350,257,350,350,350,
            394,256,350,259,372,384,350,260,350,261,
            350,350,350,388,262,350,350,258,350,350,
            263,266,350,350,350,350,387,386,419,269,
            350,268,270,350,350,273,350,267,350,272,
            271,350,274,275,350,276,350,350,350,350,
            278,277,350,350,280,350,279,350,282,281,
            283,350,350,286,284,287,350,350,350,288,
            365,289,350,350,350,350,290,350,292,291,
            350,351,350,380,350,414,399,350,294,293,
            415,350,405,350,370,350,350,45,350,350,
            350,350,295,296,350,299,350,367,368,298,
            350,300,420,297,350,350,418,301,350,350,
            350,350,350,402,350,416,350,302,350,350,
            303,350,73,350,305,362,306,304,307,308,
            350,350,424,310,350,350,350,413,350,309,
            312,311,350,350,350,313,314,350,315,364,
            363,350,350,383,316,350,350,350,350,350,
            350,320,318,350,369,393,322,317,350,321,
            350,350,366,319,350,350,350,323,390,331,
            324,325,350,409,350,410,350,350,326,327,
            350,328,350,350,350,350,389,350,408,329,
            350,350,350,350,335,350,336,350,334,332,
            333,417,350,330,350,428,350,337,350,350,
            338,382,350,350,354,350,341,339,350,343,
            342,340,350,404,350,350,344,350,350,425,
            350,350,350,350,350,361,345,379,350,350,
            411,350,401,350,347,350,403,350,350,350,
            350,350,350,350,421,346,350,350,350,374
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
