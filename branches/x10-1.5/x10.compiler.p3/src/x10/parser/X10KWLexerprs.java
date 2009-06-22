
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

    public final static int NUM_STATES = 259;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 415;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 76;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 77;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 26;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 338;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 339;
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
            9,6,6,6,8,5,6,12,4,5,
            6,9,4,3,8,5
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
            1,1,1,1,1,1,1,1,79,21,
            144,59,149,142,151,34,28,152,42,67,
            49,24,56,86,91,92,61,66,44,79,
            155,159,95,33,162,164,165,97,98,166,
            168,173,172,177,169,178,181,105,99,182,
            183,185,106,111,187,190,192,75,194,186,
            195,112,197,202,203,119,71,122,204,205,
            209,123,129,211,207,214,213,221,223,225,
            224,227,231,232,233,237,239,240,133,242,
            244,138,246,252,238,254,255,256,261,257,
            264,263,267,268,271,125,272,276,270,281,
            283,277,284,285,137,289,18,292,290,294,
            295,298,300,296,301,306,307,310,312,313,
            314,318,319,320,323,324,325,333,335,328,
            336,143,339,341,340,344,345,348,350,349,
            351,357,358,361,363,368,366,371,372,373,
            376,378,379,380,381,385,390,388,393,395,
            394,397,401,400,402,403,411,413,416,414,
            418,420,421,422,424,426,428,429,432,431,
            438,437,441,444,445,446,449,450,451,452,
            455,453,454,467,463,470,456,471,473,472,
            478,480,479,484,483,485,486,487,491,492,
            494,497,503,505,509,510,511,513,518,520,
            522,523,526,528,530,493,531,529,535,537,
            539,538,540,547,550,552,542,554,555,556,
            558,560,565,568,562,570,573,574,575,577,
            578,579,580,585,583,590,581,339,339
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
            3,14,0,0,11,12,9,4,11,16,
            17,0,10,0,3,2,26,14,0,8,
            9,3,11,10,6,0,0,9,0,16,
            0,18,7,3,4,0,0,9,13,4,
            0,11,6,3,0,20,2,12,0,5,
            2,11,17,0,10,0,1,17,3,23,
            0,0,1,3,0,1,0,0,0,9,
            9,3,8,7,0,0,2,9,3,4,
            0,0,16,16,10,4,6,6,0,0,
            23,0,0,1,0,4,8,9,0,8,
            6,9,0,9,24,3,0,0,10,2,
            8,0,0,0,1,17,4,10,0,7,
            0,0,4,12,0,19,2,7,0,1,
            9,0,1,0,0,0,2,0,0,6,
            5,0,0,6,2,4,0,0,10,2,
            0,0,0,7,0,0,0,7,7,0,
            8,0,3,0,0,10,0,4,14,13,
            6,0,0,0,0,14,0,5,0,13,
            0,3,0,0,10,9,3,14,6,18,
            0,11,0,0,0,3,0,7,4,6,
            0,0,0,2,8,3,0,0,0,0,
            1,0,1,0,1,0,10,10,18,11,
            5,0,1,0,0,0,0,2,5,5,
            0,1,0,0,8,2,0,0,1,0,
            0,0,1,11,4,0,0,2,9,13,
            0,1,0,0,0,3,3,11,0,0,
            6,0,1,0,0,0,7,0,1,0,
            0,2,9,8,10,0,0,1,3,0,
            22,0,0,0,1,4,7,0,0,0,
            20,3,0,0,0,1,0,0,6,6,
            13,19,0,6,0,0,4,3,0,0,
            0,22,2,0,0,10,8,0,0,0,
            0,12,4,10,5,5,0,0,1,12,
            0,1,0,19,8,0,4,0,1,4,
            0,0,0,3,2,0,1,0,0,0,
            0,0,3,3,0,1,8,0,11,0,
            1,20,0,0,0,2,0,10,4,0,
            0,0,0,4,8,5,5,0,16,7,
            0,1,0,0,2,0,1,0,1,0,
            0,0,9,0,5,0,6,0,0,1,
            0,0,7,12,7,4,0,0,15,2,
            0,5,12,0,0,0,2,7,0,0,
            0,0,0,0,0,0,7,5,15,8,
            6,13,0,8,14,20,0,5,2,0,
            0,0,0,4,21,3,5,0,0,0,
            10,2,0,0,0,0,0,9,5,12,
            0,0,0,0,12,10,0,11,2,0,
            9,17,0,1,0,15,13,3,0,0,
            0,19,0,5,2,6,6,0,1,0,
            1,0,0,2,2,0,1,0,0,0,
            0,1,5,4,0,7,0,0,0,0,
            1,0,5,0,8,7,0,1,14,0,
            1,0,1,0,0,0,2,0,17,0,
            7,0,3,8,0,8,2,0,1,0,
            9,2,0,0,0,2,0,0,0,0,
            0,7,0,6,0,1,0,15,10,0,
            1,0,13,0,0,15,0,0,0,0,
            0,25,0,21,0,0,0,0,0,0,
            0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            339,89,97,98,80,90,87,86,92,85,
            91,84,93,88,96,95,83,339,82,235,
            339,81,94,339,123,122,124,339,108,339,
            106,236,339,339,121,125,107,365,105,120,
            119,339,148,339,110,141,338,104,339,113,
            112,117,111,142,118,339,339,116,339,140,
            339,139,127,134,135,339,339,100,389,136,
            339,133,114,188,10,126,176,137,339,178,
            143,187,138,339,177,339,128,413,129,115,
            339,339,132,131,339,147,339,339,339,130,
            381,164,146,152,339,339,162,165,170,169,
            339,339,364,395,163,346,172,181,339,339,
            153,339,3,195,339,189,185,186,8,190,
            225,194,339,224,171,209,339,339,196,212,
            210,339,339,339,99,197,255,213,339,254,
            339,339,101,102,339,233,144,103,339,145,
            109,339,149,339,339,339,151,339,339,150,
            154,339,339,155,156,157,339,339,159,160,
            339,339,339,158,339,339,339,161,166,339,
            167,339,174,339,339,348,339,179,168,173,
            180,339,339,339,339,175,339,184,339,182,
            339,193,339,339,192,388,199,191,200,183,
            339,198,339,339,339,201,339,371,202,396,
            339,339,339,362,203,205,339,339,339,48,
            208,339,382,339,211,339,206,215,204,207,
            214,339,375,339,339,339,339,218,216,217,
            339,219,339,339,220,221,339,339,366,339,
            339,339,226,222,223,339,6,227,228,367,
            339,360,339,5,339,229,231,230,339,339,
            232,339,412,339,339,339,237,339,341,339,
            339,239,238,240,408,339,339,243,242,339,
            234,339,339,339,415,245,244,339,339,339,
            241,247,339,339,339,384,339,339,249,361,
            246,374,339,252,339,339,250,251,339,339,
            339,248,378,339,339,253,256,339,339,339,
            339,376,405,377,259,260,339,339,262,258,
            339,263,339,257,261,339,264,339,265,266,
            339,339,339,267,269,339,370,339,339,339,
            339,339,272,273,339,274,271,339,270,339,
            275,268,339,339,339,354,339,276,278,339,
            339,339,339,281,279,280,340,339,277,402,
            339,369,339,339,282,339,394,339,359,339,
            46,339,283,339,284,339,285,339,339,288,
            339,339,356,357,287,289,339,339,286,290,
            339,403,406,339,339,339,391,291,339,339,
            339,339,339,339,339,339,294,401,351,296,
            297,293,339,302,295,292,339,300,299,339,
            70,339,339,301,298,303,353,339,339,339,
            410,373,339,339,339,339,339,304,305,352,
            339,339,339,339,358,383,339,306,309,339,
            307,386,339,310,339,355,308,311,339,339,
            339,319,339,312,380,313,314,339,398,339,
            399,339,339,315,316,339,317,339,339,339,
            339,379,318,397,339,320,339,339,339,339,
            324,339,323,339,321,322,339,325,404,339,
            414,339,326,339,339,339,372,339,343,339,
            327,339,330,328,339,329,331,339,393,339,
            332,333,339,339,339,411,339,339,339,339,
            339,334,339,368,339,350,339,400,390,339,
            336,339,392,339,339,407,339,339,339,339,
            339,335,339,363
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
