

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
            7,6,6,7,3,6,4,5,6,4,
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
            81,136,147,102,28,149,145,24,84,23,
            155,59,71,158,69,159,33,26,90,43,
            57,51,160,58,129,85,163,95,77,98,
            162,164,169,100,171,109,172,174,105,176,
            177,179,101,180,113,79,184,185,115,186,
            189,193,197,198,195,202,204,205,208,207,
            45,211,120,218,209,125,221,126,223,128,
            217,226,222,230,232,233,228,240,235,241,
            244,245,236,249,251,255,257,258,259,261,
            263,264,268,271,265,270,275,276,277,280,
            281,287,282,135,289,290,293,295,131,297,
            299,291,303,305,308,306,141,310,313,312,
            316,317,324,326,327,330,331,319,332,333,
            338,345,339,336,347,348,351,353,356,343,
            357,359,360,362,361,363,371,372,373,375,
            376,378,382,383,387,388,389,390,391,394,
            396,398,400,403,405,410,146,408,406,414,
            416,418,419,421,422,423,424,426,428,429,
            435,432,439,442,443,446,436,448,453,454,
            455,456,458,461,469,459,467,472,473,474,
            475,480,481,479,483,484,485,487,490,492,
            494,499,500,501,506,508,509,511,516,517,
            518,520,521,524,528,525,531,533,534,535,
            537,543,545,548,550,536,554,549,491,556,
            557,558,551,563,564,565,568,571,569,574,
            577,578,579,581,585,587,588,591,593,590,
            598,599,595,602,605,607,609,611,612,613,
            17,615,616,621,622,623,627,625,633,635,
            362,362
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,0,17,18,19,
            20,21,0,0,2,0,3,0,1,4,
            7,6,0,11,12,3,4,15,16,7,
            24,9,0,11,0,3,4,3,23,7,
            0,7,2,11,12,5,0,0,0,2,
            10,3,4,7,8,15,8,17,0,11,
            0,1,16,3,6,0,0,7,0,3,
            2,11,14,0,0,1,18,11,10,0,
            1,8,16,9,0,12,7,0,4,0,
            0,0,3,9,0,8,9,8,0,8,
            2,7,0,9,0,15,2,5,6,0,
            6,2,14,23,0,0,1,0,0,10,
            0,7,7,3,0,0,8,10,4,9,
            0,7,2,16,0,0,0,23,0,1,
            10,6,6,8,0,1,12,0,0,0,
            25,0,0,0,7,7,4,6,0,10,
            0,0,9,0,3,0,0,7,0,0,
            5,2,4,0,0,0,2,19,0,0,
            17,15,0,8,0,7,0,0,6,2,
            4,0,19,0,0,11,0,0,0,1,
            0,10,6,3,11,8,0,0,14,2,
            0,0,0,3,2,0,10,0,3,0,
            1,0,0,12,0,0,4,6,11,0,
            0,1,3,0,0,10,0,13,0,6,
            0,7,2,5,0,1,0,0,0,2,
            0,1,0,0,0,9,8,0,5,0,
            0,4,2,11,0,0,0,2,4,0,
            0,0,3,2,20,9,0,1,0,0,
            0,22,0,1,0,7,0,3,0,1,
            11,21,0,1,0,0,10,0,18,0,
            3,0,0,8,5,0,0,1,0,8,
            5,17,10,0,1,0,0,1,3,0,
            0,0,0,4,3,0,18,0,0,9,
            2,9,0,6,0,1,0,0,1,14,
            0,1,0,7,2,0,0,15,0,0,
            0,0,0,5,2,10,5,8,12,9,
            0,0,0,1,0,0,1,0,8,8,
            3,0,0,9,3,3,0,0,0,0,
            0,4,3,0,1,0,8,0,8,0,
            3,2,0,8,0,0,20,0,4,0,
            1,4,10,0,1,0,11,0,0,2,
            0,0,0,0,9,0,1,0,0,2,
            10,0,0,12,0,0,13,9,0,5,
            22,0,0,5,22,0,1,0,13,8,
            8,20,0,0,0,0,9,0,0,2,
            0,9,8,10,6,5,0,12,0,1,
            4,0,0,0,0,2,5,5,0,0,
            0,1,0,0,0,2,0,1,6,0,
            0,0,14,0,5,21,17,4,0,0,
            0,1,18,12,6,0,16,0,0,4,
            0,1,13,6,6,0,0,0,3,0,
            0,5,2,0,0,1,3,0,9,12,
            0,4,0,0,0,0,0,7,2,6,
            5,7,0,1,0,1,14,0,0,0,
            0,4,3,0,1,0,0,0,2,2,
            12,11,0,0,0,10,2,0,0,6,
            0,1,5,0,6,13,0,0,0,6,
            0,1,5,5,0,1,0,0,1,0,
            0,5,0,1,0,19,6,0,0,2,
            2,0,1,9,0,16,0,3,0,1,
            0,0,0,2,0,0,4,7,4,13,
            0,0,0,1,0,0,0,0,7,14,
            10,5,0,0,0,3,0,13,4,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            362,97,88,101,93,99,92,83,90,94,
            89,82,85,98,96,86,362,100,84,91,
            87,95,362,362,113,362,106,362,103,135,
            107,134,362,112,114,132,130,111,110,129,
            355,131,362,128,362,142,140,198,133,139,
            362,197,147,138,141,148,362,362,362,152,
            149,119,116,145,144,146,118,150,362,117,
            362,122,143,123,125,362,362,120,362,160,
            181,121,386,362,362,154,126,159,182,362,
            137,108,412,155,362,109,136,362,157,362,
            362,362,167,158,362,161,162,166,362,435,
            170,173,362,174,362,394,185,179,180,362,
            427,200,169,177,362,362,208,362,362,201,
            362,205,207,244,362,362,153,211,239,243,
            362,238,250,210,362,362,362,204,362,104,
            251,292,102,291,362,115,105,362,362,362,
            361,362,362,362,124,127,156,163,362,151,
            362,362,164,362,171,362,362,168,362,362,
            175,178,176,362,362,362,184,165,362,362,
            172,433,362,186,362,187,362,362,188,190,
            189,362,183,13,362,191,362,362,362,203,
            362,192,196,199,193,195,362,362,194,202,
            362,362,362,206,209,362,212,362,213,362,
            429,362,362,214,362,362,215,440,216,362,
            362,411,217,362,362,407,362,413,362,218,
            362,219,221,220,362,222,362,362,362,224,
            362,226,362,362,362,223,225,362,228,362,
            362,229,230,227,362,362,362,432,233,362,
            362,362,235,237,231,234,28,236,362,362,
            362,232,22,241,362,385,362,242,362,246,
            240,392,362,377,362,362,245,362,378,362,
            248,362,362,249,252,362,362,368,362,370,
            254,247,253,362,367,362,362,256,255,362,
            362,362,362,257,260,362,259,362,362,258,
            264,261,362,262,362,263,362,362,415,265,
            362,439,362,266,267,362,362,269,362,362,
            362,362,362,271,401,268,272,404,270,273,
            362,362,362,276,362,362,278,362,274,275,
            279,362,33,277,280,281,362,362,362,362,
            362,430,284,362,286,362,283,362,285,362,
            288,383,362,287,362,362,282,362,290,362,
            380,293,289,362,295,362,294,362,362,372,
            362,362,362,362,296,362,298,362,362,420,
            371,362,362,369,362,362,421,299,362,300,
            297,362,362,416,366,362,438,362,431,302,
            303,301,362,46,362,362,304,362,362,307,
            362,305,306,409,400,402,362,405,362,308,
            309,362,362,362,362,311,399,310,362,362,
            362,393,362,362,362,388,362,428,315,362,
            362,362,314,19,317,312,313,318,362,362,
            362,376,316,425,424,362,382,362,362,320,
            362,323,319,321,322,362,362,362,324,362,
            362,419,364,362,362,328,326,362,325,418,
            362,327,362,362,362,362,362,329,333,331,
            336,332,362,397,362,396,330,362,362,362,
            362,334,335,362,337,362,362,362,374,423,
            426,338,362,362,362,379,339,362,362,365,
            362,414,340,362,341,422,362,362,362,342,
            362,345,343,344,362,434,362,362,347,362,
            362,346,362,373,362,403,348,362,362,363,
            410,362,437,350,362,349,362,351,362,391,
            362,362,362,353,362,362,354,352,436,398,
            362,362,362,357,362,362,362,362,356,389,
            387,358,362,362,362,359,362,406,417
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
           NUM_STATES        = 280,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 440,
           MAX_LA            = 1,
           NUM_RULES         = 78,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 79,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 25,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 361,
           ERROR_ACTION      = 362;

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
