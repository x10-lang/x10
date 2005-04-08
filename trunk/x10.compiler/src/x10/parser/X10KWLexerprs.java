

package x10.parser;

import com.ibm.lpg.*;

class X10KWLexerprs implements ParseTable, X10KWLexersym {

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
            8,6,7,5,4,4,5,4,5,5,
            8,7,2,6,4,4,7,5,5,7,
            5,3,4,2,10,6,10,3,9,4,
            6,3,4,7,7,9,6,6,5,6,
            8,5,6,12,4,5,6,9,4,3,
            4,8,5,5,13,6,6,5,5,7,
            6,6,7,3,6,4,5,6,7,4,
            3,8,2,10,9,6,5,4
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
            81,24,82,133,27,155,145,80,86,23,
            158,58,67,160,71,161,33,26,93,43,
            73,50,162,163,164,167,95,170,99,74,
            100,112,63,132,53,171,105,29,175,102,
            176,177,178,97,184,115,114,180,185,121,
            188,181,191,197,198,192,195,202,204,206,
            209,207,208,125,216,219,128,221,129,223,
            131,222,226,227,228,233,231,235,237,238,
            242,243,245,246,247,251,253,259,252,262,
            263,265,267,268,270,271,272,276,277,280,
            279,283,284,289,285,134,293,291,294,297,
            140,296,303,298,307,309,310,311,144,312,
            314,318,326,328,330,331,315,334,321,335,
            336,337,340,344,347,348,349,350,352,354,
            355,358,359,361,364,362,366,376,367,377,
            379,372,384,386,387,388,382,392,393,394,
            395,403,400,405,407,410,411,412,147,414,
            416,421,417,423,419,424,428,430,431,433,
            434,438,437,439,441,442,449,452,440,453,
            455,458,459,460,461,466,469,471,474,475,
            477,479,467,480,485,483,487,489,490,493,
            497,498,499,500,501,506,509,511,512,518,
            519,502,520,522,525,530,523,532,534,535,
            536,537,538,543,546,549,551,550,555,552,
            558,559,560,561,565,566,568,567,572,577,
            578,579,580,581,584,589,586,591,594,595,
            597,600,601,604,605,607,608,611,609,616,
            615,620,621,623,624,627,628,626,631,637,
            638,363,363
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,16,0,18,19,
            20,21,0,0,2,0,0,1,0,4,
            5,3,0,11,12,3,4,11,16,17,
            8,9,0,11,0,3,4,22,25,0,
            8,2,0,11,12,3,7,0,6,10,
            3,4,0,6,15,16,0,1,11,3,
            0,9,0,0,8,5,3,11,6,0,
            8,0,3,13,11,0,5,8,18,17,
            17,6,0,1,0,1,0,12,0,0,
            8,0,4,9,0,6,2,9,9,8,
            9,0,16,0,0,2,5,13,22,5,
            0,7,2,10,0,5,2,0,0,1,
            0,0,0,0,10,8,8,4,6,0,
            10,8,3,0,0,2,0,17,9,22,
            19,5,6,10,0,1,12,0,1,0,
            0,0,0,0,2,2,0,8,8,0,
            0,10,6,4,0,0,0,0,8,0,
            0,4,7,0,0,2,2,0,8,15,
            0,0,16,6,0,5,0,0,19,2,
            4,0,11,0,10,0,0,0,0,3,
            3,6,11,5,0,0,13,2,0,1,
            0,0,0,3,2,0,0,0,3,3,
            0,10,0,1,0,5,0,0,4,12,
            3,0,0,1,0,0,0,11,0,5,
            0,0,0,8,2,14,10,7,0,1,
            9,0,0,2,0,1,0,0,6,0,
            0,0,2,4,7,0,0,11,0,0,
            4,2,0,0,0,3,2,9,0,1,
            0,20,0,0,1,0,0,0,23,3,
            8,11,0,1,21,10,0,1,0,0,
            0,0,3,0,0,18,6,0,4,6,
            0,10,0,15,7,0,1,0,1,0,
            0,1,3,0,0,0,0,3,18,0,
            1,5,9,0,9,2,0,0,0,0,
            1,0,1,0,0,2,8,0,0,13,
            0,0,15,0,10,0,0,7,7,6,
            12,0,6,16,9,0,0,2,0,1,
            9,0,6,0,1,0,0,0,3,3,
            3,0,0,0,0,4,3,0,6,0,
            6,20,0,1,0,6,0,3,2,0,
            0,0,1,0,4,0,0,4,0,10,
            0,1,0,0,2,9,11,0,10,0,
            0,1,0,0,2,12,0,0,0,0,
            0,0,9,14,7,7,6,6,0,1,
            23,0,0,14,0,4,20,0,0,0,
            0,9,2,9,6,0,0,10,0,1,
            0,12,7,0,0,5,0,4,0,0,
            2,7,0,7,0,1,0,21,0,0,
            2,5,0,1,15,13,0,0,0,0,
            0,0,4,7,5,0,1,18,0,12,
            0,0,4,12,14,5,5,0,0,0,
            3,0,0,2,0,7,4,3,9,0,
            1,0,1,0,0,0,0,0,0,2,
            5,8,0,1,8,0,1,13,0,0,
            0,0,4,3,0,1,7,0,0,0,
            0,2,2,12,0,0,0,0,10,2,
            5,0,1,7,17,11,0,0,0,0,
            0,5,5,0,1,0,7,7,0,1,
            0,1,7,0,0,1,0,19,5,0,
            0,2,2,0,0,1,0,0,0,3,
            0,1,9,17,0,0,8,2,4,0,
            0,14,0,0,4,0,0,0,1,0,
            0,0,7,10,8,13,0,0,0,3,
            0,4,0,24,14,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            363,97,88,101,93,92,90,99,83,94,
            89,82,85,96,98,100,86,363,84,91,
            87,95,363,363,114,363,363,104,363,136,
            135,173,363,113,115,133,131,103,112,111,
            130,132,363,129,363,143,141,134,362,363,
            140,148,363,139,142,169,149,363,168,150,
            120,117,363,119,151,147,363,123,118,124,
            363,166,363,363,121,126,162,122,145,363,
            146,363,107,387,161,363,102,108,127,144,
            413,109,363,138,363,156,363,110,363,363,
            137,363,159,157,363,163,172,160,164,175,
            176,363,395,363,363,183,165,171,179,182,
            363,181,187,184,363,427,201,363,363,209,
            363,363,363,363,202,206,208,241,436,363,
            212,240,246,363,363,252,363,211,245,205,
            167,294,293,253,363,105,106,363,116,363,
            363,363,363,363,153,154,363,125,128,363,
            363,152,155,158,363,363,363,363,170,363,
            363,178,177,363,363,180,186,363,189,174,
            363,363,434,188,363,190,363,363,185,192,
            191,13,193,363,194,363,363,363,363,199,
            200,197,195,198,363,363,196,203,363,204,
            363,363,363,207,210,363,363,363,214,215,
            363,213,363,429,363,441,363,363,217,216,
            219,363,363,412,363,363,363,218,363,220,
            363,363,363,221,223,414,408,222,363,224,
            225,363,363,226,363,228,363,363,227,363,
            363,363,232,231,230,363,363,229,363,363,
            235,433,363,363,363,237,239,236,28,238,
            363,233,363,22,243,363,363,363,234,244,
            386,242,363,248,393,247,363,378,363,363,
            363,363,250,363,363,379,251,363,258,371,
            363,254,363,249,255,363,369,363,368,363,
            363,257,256,363,363,363,363,261,260,363,
            264,263,259,363,262,265,363,363,363,363,
            416,363,440,363,363,269,268,363,363,266,
            363,363,267,363,270,363,363,273,274,405,
            272,363,276,271,275,363,363,402,363,278,
            279,363,277,363,280,363,363,33,281,282,
            283,363,363,363,363,430,286,363,285,363,
            287,284,363,288,363,289,363,290,384,363,
            363,363,381,363,292,363,363,295,363,291,
            363,297,363,363,373,298,296,363,372,363,
            363,299,363,363,421,370,363,363,363,363,
            363,363,300,422,301,417,303,304,363,439,
            367,363,363,431,363,305,302,46,363,363,
            363,306,309,307,308,363,363,410,363,310,
            363,406,403,363,363,401,363,311,363,363,
            313,400,363,312,363,394,363,314,363,363,
            389,317,363,428,315,316,363,363,19,363,
            363,363,320,319,424,363,377,318,363,425,
            363,363,322,419,321,323,324,363,363,363,
            325,363,363,365,363,420,328,327,326,363,
            432,363,329,363,363,363,363,363,363,334,
            332,330,363,398,333,363,397,331,363,363,
            363,363,335,336,363,338,337,363,363,363,
            363,375,423,426,363,363,363,363,380,340,
            366,363,415,341,383,339,363,363,363,363,
            363,342,343,363,346,363,344,345,363,435,
            363,348,347,363,363,374,363,404,349,363,
            363,364,411,363,363,438,363,363,363,352,
            363,392,351,350,363,363,353,354,355,363,
            363,399,363,363,437,363,363,363,358,363,
            363,363,359,388,357,390,363,363,363,360,
            363,418,363,356,407
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
           NUM_STATES        = 281,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 441,
           MAX_LA            = 1,
           NUM_RULES         = 78,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 79,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 25,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 362,
           ERROR_ACTION      = 363;

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
