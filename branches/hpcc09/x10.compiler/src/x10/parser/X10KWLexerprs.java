
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

    public final static int NUM_STATES = 269;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 433;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 80;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 81;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 26;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 352;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 353;
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
            7,2,4,7,5,5,7,3,4,6,
            2,10,6,10,9,6,3,4,8,7,
            7,9,5,6,6,6,6,8,6,5,
            6,12,4,5,6,9,4,3,8,5
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
            1,1,83,30,81,83,160,21,103,86,
            28,70,34,84,47,24,46,92,94,101,
            62,63,42,166,18,168,169,149,48,163,
            174,176,177,88,99,178,180,181,184,187,
            188,190,191,106,109,193,194,196,114,118,
            200,202,197,69,204,206,208,120,207,212,
            215,123,61,128,217,218,221,130,129,223,
            110,219,229,232,234,236,238,242,239,243,
            246,248,251,249,255,226,135,256,260,141,
            258,262,267,268,269,270,273,136,278,271,
            279,283,281,148,287,289,290,295,297,293,
            298,299,302,55,153,306,303,308,309,313,
            315,316,310,320,326,324,328,331,334,322,
            338,333,340,335,342,344,351,349,353,355,
            358,156,360,345,359,362,363,364,366,365,
            367,377,378,379,385,383,389,391,392,381,
            397,393,398,402,400,404,158,405,408,410,
            412,413,414,420,416,422,419,424,425,435,
            428,437,439,440,442,444,433,446,447,448,
            451,453,454,457,460,463,464,465,467,468,
            471,473,475,474,476,478,477,490,479,491,
            493,487,500,494,495,504,505,508,499,140,
            510,512,513,514,516,517,521,524,525,530,
            531,533,538,540,542,543,546,548,549,534,
            550,552,556,557,559,558,561,563,568,571,
            573,574,575,576,578,580,579,588,591,584,
            592,594,593,597,598,599,600,602,605,610,
            606,353,353
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
            2,13,0,4,0,1,0,0,0,3,
            3,0,0,11,6,0,1,0,0,0,
            8,4,0,1,0,0,2,0,10,0,
            19,0,3,0,9,0,1,0,1,20,
            0,8,0,0,0,5,3,0,1,0,
            19,0,3,0,0,13,2,6,0,6,
            0,1,0,5,0,3,22,0,0,0,
            6,0,0,0,0,0,0,10,9,5,
            12,10,7,7,0,12,0,0,0,1,
            0,19,0,7,0,1,9,5,0,1,
            0,0,0,1,3,5,0,0,2,0,
            20,0,3,0,0,1,3,0,11,0,
            9,0,0,0,2,0,5,10,0,0,
            5,0,9,0,0,16,7,0,7,2,
            6,8,0,15,0,1,0,1,0,0,
            1,0,4,0,12,0,0,0,7,6,
            0,1,0,0,8,8,0,5,2,0,
            15,2,0,0,0,12,0,0,2,7,
            0,8,0,0,0,0,0,0,0,15,
            8,6,9,13,7,7,0,20,14,0,
            0,2,0,0,0,5,10,21,0,0,
            7,9,3,0,0,2,12,0,4,0,
            12,0,0,0,7,0,0,4,2,10,
            0,1,11,0,0,0,3,15,13,0,
            0,7,0,0,2,6,6,0,1,0,
            1,0,0,2,2,0,1,0,0,0,
            1,0,19,5,7,0,0,0,0,8,
            0,1,0,1,7,9,8,0,1,14,
            0,1,0,0,0,0,2,0,0,0,
            2,8,3,0,9,2,9,0,1,17,
            0,0,0,0,4,2,0,0,0,0,
            8,0,1,6,0,0,15,0,10,0,
            1,0,13,0,0,0,0,0,0,0,
            15,25,0,0,0,21,0,0,0,0,
            0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            353,93,101,102,89,84,91,94,90,96,
            95,88,97,92,100,99,87,353,86,150,
            353,85,98,353,129,128,130,353,114,353,
            112,113,108,353,127,131,116,118,111,126,
            125,353,119,147,117,353,353,353,155,123,
            122,148,124,133,353,352,154,146,404,145,
            353,353,353,196,140,132,141,142,10,353,
            184,195,139,115,143,186,245,431,185,144,
            353,104,353,353,103,353,106,353,105,120,
            379,353,134,353,135,160,137,136,353,110,
            353,138,353,378,395,353,121,170,353,353,
            109,172,173,353,410,171,178,353,177,353,
            207,161,353,180,360,189,194,353,8,3,
            203,193,197,202,353,353,198,219,204,353,
            353,179,222,220,231,205,230,353,353,153,
            223,235,353,236,246,353,400,353,288,353,
            268,416,353,267,107,353,247,353,353,149,
            152,151,156,353,157,353,353,353,159,353,
            353,158,164,353,162,163,353,353,165,353,
            353,168,353,353,166,353,353,167,169,353,
            174,353,175,353,182,353,353,353,187,176,
            183,353,181,188,353,362,353,353,353,190,
            353,192,353,201,206,48,218,200,353,191,
            199,353,402,353,209,353,208,353,353,210,
            211,353,353,385,411,353,212,353,353,376,
            353,213,353,215,353,353,396,353,216,353,
            221,353,389,214,224,217,353,353,353,353,
            353,228,353,229,226,227,225,353,353,232,
            353,233,353,380,353,234,353,237,353,353,
            238,381,6,239,353,374,353,5,353,240,
            242,353,353,241,243,353,430,353,353,353,
            248,249,353,355,353,353,250,353,426,353,
            244,353,253,353,251,353,254,353,256,252,
            353,255,353,353,353,257,258,353,433,353,
            388,353,260,353,353,259,392,262,353,375,
            353,398,353,263,353,264,261,353,353,353,
            265,353,353,353,353,353,353,266,269,423,
            390,391,272,273,353,271,353,353,353,276,
            353,270,353,274,353,277,275,278,353,279,
            353,353,353,384,281,280,353,353,283,353,
            282,353,286,353,353,289,287,353,284,353,
            285,353,353,353,368,353,292,290,353,353,
            295,353,293,353,353,291,294,353,354,296,
            403,418,353,419,353,383,353,409,353,353,
            373,353,297,46,371,353,353,353,298,299,
            353,302,353,353,370,301,353,303,422,353,
            300,304,353,353,353,424,353,353,406,420,
            353,305,353,353,353,353,353,353,353,365,
            308,311,310,307,417,314,74,306,309,353,
            353,313,353,353,353,315,428,312,353,353,
            367,316,317,353,353,387,366,353,318,353,
            372,353,353,353,319,353,353,321,323,397,
            353,324,320,353,353,353,325,369,322,353,
            353,326,353,353,394,327,328,353,413,353,
            414,353,353,329,330,353,331,353,353,353,
            393,353,333,412,332,353,353,353,353,334,
            353,338,353,339,337,335,336,353,432,421,
            353,340,353,353,353,353,386,353,353,353,
            345,341,344,353,342,347,343,353,408,357,
            353,353,353,353,346,429,353,353,353,353,
            348,353,364,382,353,353,415,353,405,353,
            350,353,407,353,353,353,353,353,353,353,
            425,349,353,353,353,377
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
