

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
            4,8,5,5,8,6,6,5,5,7,
            7,6,6,7,3,6,4,5,6,4,
            3,8,2,5,9,6,5,4
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
            81,136,48,26,147,153,145,24,87,23,
            158,56,65,160,70,162,33,71,29,43,
            77,47,163,58,164,89,165,72,80,95,
            167,166,174,102,176,101,177,28,108,179,
            181,182,97,186,118,109,178,190,121,194,
            183,198,200,201,195,204,205,208,210,209,
            126,217,128,219,211,127,223,131,225,133,
            218,229,224,230,233,234,235,239,238,243,
            245,240,247,250,252,258,253,261,255,264,
            266,267,268,269,270,272,274,276,279,280,
            282,284,287,106,291,286,292,295,137,296,
            300,302,304,69,307,308,142,309,311,312,
            316,318,323,325,326,329,330,314,331,333,
            337,343,338,335,345,346,350,352,348,355,
            356,357,359,360,362,365,369,372,375,373,
            378,380,381,385,386,387,390,389,391,397,
            399,400,405,401,402,412,151,408,410,416,
            414,418,419,422,424,425,426,427,429,433,
            434,430,437,444,446,452,435,445,455,456,
            457,458,459,463,467,471,472,473,474,460,
            465,483,477,485,486,480,492,490,487,494,
            500,495,502,505,506,507,513,515,516,497,
            517,519,520,524,527,525,529,530,535,538,
            540,542,544,545,552,536,546,550,554,555,
            558,559,561,564,567,570,574,575,563,577,
            580,584,586,587,589,590,594,595,596,599,
            600,602,603,568,605,606,609,611,612,614,
            352,352
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
            20,21,0,0,2,0,3,0,0,1,
            5,8,0,11,12,3,8,15,16,7,
            8,9,0,11,17,3,0,0,2,7,
            8,4,6,11,12,0,10,0,3,2,
            5,15,7,17,0,1,11,3,0,0,
            0,0,8,4,4,11,0,7,7,0,
            9,5,3,14,8,17,0,18,0,1,
            11,5,16,23,0,16,0,9,12,5,
            0,0,2,9,3,0,5,0,0,0,
            2,15,7,8,14,8,9,0,10,23,
            0,4,2,6,4,0,0,0,3,2,
            0,1,0,8,8,0,0,10,8,3,
            0,0,10,2,0,9,0,1,16,23,
            0,10,0,1,4,5,12,0,1,0,
            25,0,0,0,0,0,0,8,5,8,
            4,7,10,0,9,0,0,0,0,3,
            0,0,0,8,6,0,0,2,7,0,
            8,2,19,0,0,15,19,0,5,0,
            0,4,2,0,0,11,7,0,0,0,
            0,1,4,10,5,11,0,0,0,3,
            2,14,0,0,0,3,2,10,0,0,
            1,3,0,0,0,12,4,0,0,0,
            7,3,0,1,0,11,0,8,4,0,
            13,0,0,2,0,6,10,0,1,5,
            0,9,2,0,1,0,0,0,0,0,
            2,0,6,0,7,0,11,2,0,0,
            7,0,3,0,1,0,0,9,2,20,
            0,0,1,22,0,0,11,3,8,0,
            1,0,21,0,1,10,0,0,0,3,
            0,0,5,0,6,0,5,0,1,18,
            10,6,0,1,0,0,1,3,0,0,
            0,18,0,3,0,7,0,0,9,2,
            4,9,0,1,0,0,1,0,14,0,
            1,0,8,2,0,0,0,10,0,0,
            0,0,6,5,0,6,2,12,0,15,
            9,0,0,5,0,1,5,0,1,0,
            0,9,3,3,0,0,0,3,0,0,
            0,3,0,7,5,5,0,1,0,0,
            0,0,3,5,0,20,2,0,7,0,
            10,0,1,0,7,0,1,0,0,2,
            11,0,9,0,0,0,0,1,0,0,
            2,10,0,0,0,12,0,0,13,6,
            22,9,6,0,0,0,22,13,5,20,
            5,0,1,9,0,0,0,0,0,0,
            2,5,0,9,0,10,0,1,6,12,
            0,0,0,0,4,2,0,6,6,0,
            21,17,0,1,0,0,0,2,4,0,
            14,0,1,0,0,6,0,18,12,0,
            7,0,1,4,0,0,0,13,12,4,
            4,7,0,1,0,0,0,3,0,0,
            2,6,3,0,0,9,0,1,0,0,
            7,0,8,4,0,0,2,0,1,0,
            1,0,14,0,0,0,3,12,7,0,
            6,0,1,0,0,2,2,0,0,10,
            0,16,0,0,4,2,0,0,11,0,
            1,13,6,0,0,8,0,4,4,0,
            1,19,6,0,1,0,0,1,0,0,
            1,6,4,0,0,0,2,2,0,0,
            1,0,0,1,0,0,2,9,0,16,
            0,0,1,0,13,0,0,0,0,0,
            10,0,14,0,0,0,13,0,0,24,
            0,0,0,0,0,0,0,0,0,0,
            0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            352,97,88,101,92,90,99,93,83,94,
            89,82,85,98,96,86,352,100,84,91,
            87,95,352,352,113,352,106,352,352,137,
            425,107,352,112,114,132,136,111,110,130,
            129,131,352,128,172,142,352,352,147,140,
            139,102,148,138,141,352,149,352,119,152,
            118,146,116,150,352,122,117,123,352,352,
            352,352,120,125,134,121,352,135,157,352,
            158,144,160,376,145,247,352,126,352,154,
            159,108,143,133,352,402,352,155,109,161,
            352,352,170,162,167,352,166,352,352,352,
            181,384,239,238,169,173,174,352,182,177,
            352,180,185,179,417,352,352,352,198,200,
            352,208,352,197,205,352,352,201,207,244,
            352,352,211,250,352,243,352,103,210,204,
            352,251,352,104,291,290,105,352,115,352,
            351,352,352,352,352,352,352,124,153,127,
            163,156,151,352,164,352,352,352,352,171,
            352,352,352,168,175,352,352,178,176,352,
            187,184,165,352,352,423,183,352,186,352,
            352,188,190,352,13,191,189,352,352,352,
            352,203,196,192,195,193,352,352,352,199,
            202,194,352,352,352,206,209,212,352,352,
            419,213,352,352,352,214,430,352,352,352,
            215,217,352,401,352,216,352,219,218,352,
            403,352,352,221,352,220,397,352,222,225,
            352,223,224,352,226,352,352,352,352,352,
            230,352,228,352,229,352,227,422,352,352,
            233,352,235,28,236,352,352,234,237,231,
            352,22,241,232,352,352,240,242,375,352,
            246,352,382,352,367,245,352,352,352,248,
            352,352,249,352,252,352,360,352,358,368,
            253,254,352,357,352,352,256,255,352,352,
            352,259,352,260,352,257,352,352,258,264,
            262,261,352,263,352,352,405,352,265,352,
            429,352,266,267,352,352,352,268,352,352,
            352,352,271,394,352,272,391,270,352,269,
            273,352,352,274,352,426,275,352,277,352,
            352,276,278,279,33,352,352,280,352,352,
            352,283,352,420,282,284,352,285,352,352,
            352,352,287,286,352,281,373,352,289,352,
            288,352,370,352,292,352,294,352,352,362,
            293,352,295,352,352,352,352,297,352,352,
            410,361,352,352,352,359,352,352,411,299,
            296,298,406,352,352,352,356,421,301,300,
            302,352,428,303,352,46,352,352,352,352,
            306,305,352,304,352,399,352,307,392,395,
            352,352,352,352,390,309,352,389,308,352,
            310,311,352,383,352,352,352,378,313,352,
            312,352,418,19,352,315,352,314,415,352,
            316,352,366,414,352,352,352,317,408,319,
            320,318,352,321,352,352,352,322,352,352,
            354,409,324,352,352,323,352,326,352,352,
            325,352,327,329,352,352,330,352,387,352,
            386,352,328,352,352,352,332,416,331,352,
            333,352,334,352,352,364,413,352,352,369,
            352,372,352,352,355,336,352,352,335,352,
            404,412,337,352,352,346,352,338,339,352,
            341,393,340,352,424,352,352,343,352,352,
            363,342,344,352,352,352,353,400,352,352,
            427,352,352,381,352,352,347,345,352,407,
            352,352,349,352,388,352,352,352,352,352,
            377,352,379,352,352,352,396,352,352,348
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
           NUM_STATES        = 270,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 430,
           MAX_LA            = 1,
           NUM_RULES         = 78,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 79,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 25,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 351,
           ERROR_ACTION      = 352;

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
