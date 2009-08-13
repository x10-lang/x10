
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

    public final static int LA_STATE_OFFSET = 425;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 78;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 79;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 26;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 346;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 347;
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
            9,6,6,6,6,8,6,5,6,12,
            4,5,6,9,4,3,8,5
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
            81,143,74,144,94,147,152,33,28,151,
            42,79,49,24,30,81,85,89,61,59,
            44,155,154,162,57,92,153,165,167,168,
            96,87,171,172,173,177,178,180,182,183,
            97,105,185,187,188,113,114,192,194,189,
            68,195,198,200,115,201,204,206,100,66,
            120,209,210,212,125,118,216,213,219,226,
            221,227,230,232,231,234,237,239,242,240,
            246,247,127,251,253,132,244,258,256,260,
            262,261,268,137,270,264,271,273,276,133,
            277,279,282,285,287,283,289,291,293,295,
            18,296,299,300,301,302,308,304,305,315,
            319,314,323,326,309,331,316,333,321,327,
            335,337,340,345,339,341,141,347,350,349,
            353,354,355,357,360,365,366,359,374,376,
            378,379,381,369,383,384,387,389,390,391,
            392,396,401,398,393,406,405,404,411,414,
            412,415,419,420,422,425,423,431,428,434,
            435,436,437,438,439,444,442,450,453,456,
            449,443,460,459,463,464,466,465,468,473,
            467,469,477,481,483,484,486,490,487,491,
            496,498,497,500,501,504,505,506,508,514,
            518,507,522,523,524,526,532,534,536,537,
            540,542,525,543,544,546,549,548,550,551,
            552,559,564,566,555,561,568,571,573,574,
            576,583,575,585,579,586,589,590,591,592,
            593,595,599,600,603,347,347
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
            3,14,0,6,11,12,4,8,11,16,
            17,0,13,0,3,2,14,6,0,20,
            9,3,11,10,6,7,0,1,0,16,
            0,18,4,3,4,0,0,0,3,2,
            12,11,5,0,1,17,11,10,0,6,
            0,1,17,3,0,7,0,3,0,1,
            6,0,1,0,6,0,0,4,2,0,
            9,23,16,8,0,6,10,3,9,23,
            6,16,0,0,0,3,4,0,4,0,
            7,7,0,4,0,1,0,10,9,3,
            6,0,0,2,17,9,0,24,6,7,
            0,10,0,0,4,9,0,11,8,6,
            0,0,0,0,0,2,6,0,12,8,
            6,0,10,2,0,1,0,0,26,2,
            0,0,0,7,2,5,0,0,7,0,
            4,0,0,2,0,8,0,0,0,10,
            8,0,8,0,0,9,3,0,4,0,
            0,14,14,0,13,0,7,10,0,0,
            5,0,0,13,3,0,0,2,0,10,
            0,18,14,11,6,0,0,7,3,0,
            0,0,3,0,8,4,0,7,0,0,
            2,0,9,0,3,0,0,1,5,10,
            0,1,0,1,18,0,11,0,1,0,
            0,0,2,0,5,10,5,0,1,0,
            0,2,0,1,11,0,0,1,0,4,
            2,0,0,13,0,1,0,6,0,3,
            0,3,0,11,0,0,1,7,0,0,
            0,0,1,0,0,6,8,0,0,2,
            10,19,9,0,0,0,22,3,0,1,
            0,8,0,1,20,0,0,19,13,4,
            0,1,0,7,0,3,0,1,0,0,
            0,7,22,4,0,7,0,3,0,0,
            10,2,0,0,0,9,0,0,0,0,
            12,5,10,4,0,0,12,9,0,5,
            5,3,19,0,1,0,1,0,0,1,
            0,4,0,0,4,2,0,1,0,0,
            0,0,0,3,3,0,1,0,9,11,
            0,1,20,0,0,0,2,10,16,4,
            0,0,9,0,0,5,5,4,0,0,
            1,0,0,2,0,1,8,0,6,15,
            0,1,5,0,0,0,0,0,0,1,
            7,0,0,0,8,8,12,4,0,0,
            15,2,0,12,2,0,8,15,0,0,
            5,2,0,0,0,0,0,0,0,0,
            2,8,0,9,7,13,0,5,20,14,
            0,5,0,0,4,0,0,21,3,0,
            0,9,2,10,5,0,0,0,12,0,
            0,6,5,0,0,0,0,0,12,3,
            10,6,0,0,11,2,17,0,1,15,
            13,0,0,0,0,0,5,2,4,7,
            7,0,1,0,1,0,0,2,2,0,
            1,0,0,0,1,0,5,0,0,0,
            0,0,1,8,0,5,9,8,0,1,
            0,19,14,0,1,0,1,0,8,2,
            0,17,0,0,0,0,3,2,0,9,
            6,9,0,1,0,0,2,2,0,0,
            0,0,0,15,0,1,8,7,0,0,
            1,10,0,0,0,13,0,0,0,0,
            0,0,0,0,25,0,0,15,0,21,
            0,0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            347,91,99,100,82,92,87,89,88,94,
            93,86,95,90,98,97,85,347,84,241,
            347,83,96,347,126,125,127,347,111,347,
            109,242,347,110,124,128,373,130,108,123,
            122,347,397,347,113,144,107,115,347,129,
            116,120,114,145,119,121,347,149,347,143,
            347,142,139,137,138,347,347,10,192,180,
            140,136,182,347,102,141,191,181,347,101,
            347,131,423,132,347,117,347,134,347,135,
            133,347,151,347,389,347,347,104,166,347,
            150,118,403,156,347,190,167,168,189,157,
            169,372,347,347,347,174,173,8,354,347,
            176,185,347,193,3,199,347,200,194,214,
            198,347,347,217,201,215,347,175,230,231,
            347,218,347,347,262,226,347,225,261,103,
            347,347,347,347,347,147,112,347,105,106,
            146,347,152,148,347,153,347,347,346,155,
            347,347,347,154,160,158,347,347,159,347,
            161,347,347,164,347,162,347,347,347,163,
            165,347,170,347,347,171,178,347,183,347,
            347,172,179,347,177,347,184,356,347,347,
            188,347,347,186,197,347,347,202,347,196,
            347,187,195,203,396,347,347,205,204,347,
            347,347,206,347,379,207,347,404,347,347,
            370,347,208,347,210,347,48,213,219,211,
            347,390,347,216,209,347,212,347,383,347,
            347,347,223,347,221,220,222,347,224,347,
            347,227,347,374,228,347,347,232,347,229,
            233,347,6,375,347,368,347,234,5,235,
            347,237,347,236,347,347,422,238,347,347,
            347,347,349,347,347,244,243,347,347,245,
            418,239,246,347,347,347,240,248,347,249,
            347,250,347,251,247,347,347,382,253,252,
            347,425,347,256,347,254,347,392,347,347,
            347,369,255,257,347,259,347,258,347,347,
            260,386,347,347,347,263,347,347,347,347,
            384,266,385,415,347,347,265,269,347,267,
            268,275,264,347,270,347,271,347,347,273,
            347,272,347,347,274,277,347,378,347,347,
            347,347,347,280,281,347,282,347,279,278,
            347,283,276,347,347,347,362,284,285,286,
            347,347,287,347,347,288,348,289,347,347,
            377,347,347,290,347,402,410,347,291,411,
            347,367,292,46,347,347,347,347,347,296,
            293,347,347,347,364,295,365,297,347,347,
            294,414,347,416,298,347,299,359,347,347,
            412,399,347,347,347,347,347,347,347,347,
            307,302,347,304,305,301,347,409,300,303,
            347,308,347,72,309,347,347,306,311,347,
            347,310,381,420,361,347,347,347,360,347,
            347,312,313,347,347,347,347,347,366,319,
            391,315,347,347,314,317,394,347,318,363,
            316,347,347,347,347,347,320,388,405,321,
            322,347,406,347,407,347,347,323,324,347,
            325,347,347,347,387,347,326,347,347,347,
            347,347,332,328,347,331,329,330,347,333,
            347,327,413,347,424,347,334,347,335,380,
            347,351,347,347,347,347,338,339,347,336,
            340,337,347,401,347,347,341,421,347,347,
            347,347,347,408,347,358,342,376,347,347,
            344,398,347,347,347,400,347,347,347,347,
            347,347,347,347,343,347,347,417,347,371
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
