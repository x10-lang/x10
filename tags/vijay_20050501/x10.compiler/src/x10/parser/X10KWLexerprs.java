

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
            3,8,2,5,9,5,4
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
            1,1,1,1,1,1,1,1,1,80,
            131,48,137,148,150,24,79,23,152,56,
            65,155,12,157,33,70,86,43,74,47,
            158,164,28,154,88,69,92,166,126,159,
            99,167,98,58,169,105,170,172,173,104,
            177,110,107,174,181,106,184,182,189,185,
            195,191,196,198,31,199,200,81,201,118,
            208,211,121,213,122,215,124,214,207,220,
            218,222,225,227,226,231,228,234,235,237,
            238,246,241,249,244,252,254,255,256,257,
            258,260,262,264,267,268,270,272,275,125,
            279,274,280,283,136,284,288,290,292,295,
            296,297,141,298,300,302,303,313,315,317,
            318,321,322,305,324,325,330,332,336,329,
            339,341,343,344,311,346,347,348,349,350,
            360,357,363,364,366,368,370,371,376,372,
            377,378,380,381,386,389,388,394,390,395,
            397,142,399,401,403,405,407,408,410,411,
            413,415,416,419,418,425,422,426,429,434,
            424,436,439,440,443,441,444,449,451,351,
            454,456,457,458,461,463,466,465,467,468,
            471,475,473,476,477,474,488,484,490,492,
            493,496,498,500,501,503,506,507,515,511,
            508,513,521,524,526,528,517,530,531,529,
            533,534,537,538,542,543,545,546,551,554,
            547,558,559,560,561,564,567,568,570,571,
            574,577,580,576,583,581,586,588,589,592,
            593,595,597,599,346,346
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,0,12,13,14,15,5,17,18,19,
            20,21,0,0,2,14,3,0,1,18,
            0,8,0,11,12,3,9,15,16,7,
            8,9,0,11,14,3,0,0,2,7,
            8,4,6,11,12,0,10,0,3,4,
            3,15,7,17,0,1,11,3,0,0,
            0,3,8,0,5,11,7,4,0,11,
            0,8,4,3,16,0,1,0,8,16,
            12,0,23,8,7,4,9,0,0,2,
            9,3,4,0,0,0,0,2,2,0,
            5,14,8,9,5,6,10,0,15,2,
            0,0,1,0,0,0,23,10,8,8,
            0,7,8,10,9,0,0,1,3,16,
            0,0,2,23,9,4,5,0,1,0,
            10,0,1,0,0,25,0,0,0,2,
            7,12,8,0,8,0,0,4,0,0,
            5,0,0,0,8,6,0,19,2,7,
            0,0,2,0,0,17,15,4,0,8,
            0,7,19,5,0,0,2,0,0,0,
            0,11,4,3,5,10,0,0,11,2,
            0,1,0,0,0,3,2,0,12,0,
            1,0,5,10,0,0,0,0,7,3,
            0,1,5,0,0,11,0,0,13,2,
            0,8,6,0,10,0,1,4,0,9,
            2,0,1,0,0,0,0,0,2,0,
            6,0,7,0,11,2,0,0,7,0,
            3,0,1,0,0,9,2,20,0,0,
            1,22,0,0,11,3,8,0,1,0,
            21,0,1,10,0,0,0,0,3,0,
            4,0,0,6,0,4,0,18,6,10,
            0,17,0,1,0,1,0,0,1,3,
            0,0,18,0,0,15,3,7,0,0,
            9,0,1,9,5,0,8,2,0,1,
            0,1,0,0,2,0,0,0,0,0,
            0,4,6,10,6,5,0,12,9,0,
            4,2,0,0,1,0,4,0,1,0,
            0,0,3,3,9,0,0,0,3,0,
            0,4,3,7,4,0,1,0,0,0,
            3,20,4,0,0,2,0,1,0,10,
            0,7,0,1,0,7,0,0,2,0,
            0,11,0,9,0,0,1,0,0,10,
            2,0,12,0,0,0,9,13,0,22,
            6,6,4,0,22,0,13,4,0,0,
            0,20,0,0,9,2,4,9,0,10,
            0,1,12,0,6,0,0,0,2,6,
            0,6,0,1,0,0,0,0,2,5,
            0,1,0,0,0,0,0,17,21,14,
            6,5,7,0,12,18,13,0,1,0,
            7,0,0,1,5,0,5,0,3,0,
            0,0,0,6,2,0,0,0,3,9,
            0,12,0,7,0,1,0,5,8,3,
            0,14,2,0,1,0,1,0,0,0,
            0,1,0,0,7,6,0,0,2,2,
            12,0,0,10,0,0,0,2,16,5,
            0,5,11,0,1,13,6,0,0,0,
            0,1,5,0,1,6,0,0,1,0,
            0,1,6,0,5,0,0,19,2,0,
            0,2,0,1,9,0,1,0,0,16,
            2,0,0,13,0,8,0,1,0,0,
            0,0,0,0,10,0,14,0,0,0,
            0,13,0,0,0,24,0,0,0,0,
            0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            346,95,86,99,88,90,97,91,81,92,
            87,346,83,96,94,84,122,98,82,89,
            85,93,346,346,110,370,103,346,150,123,
            346,104,346,109,111,129,151,108,107,127,
            126,128,346,125,190,139,346,346,144,137,
            136,419,145,135,138,346,146,346,116,115,
            167,143,113,147,346,119,114,120,346,346,
            346,156,117,346,131,118,132,141,346,155,
            346,142,105,194,396,346,134,346,193,140,
            106,346,130,133,153,157,154,346,346,166,
            158,163,162,346,346,346,346,181,177,346,
            411,165,169,170,176,175,178,346,378,196,
            346,346,204,346,346,346,173,197,201,203,
            346,234,233,207,160,346,346,100,239,206,
            346,346,245,200,238,284,285,346,101,346,
            246,346,112,346,346,345,346,346,346,148,
            152,102,121,346,124,346,346,149,346,346,
            159,346,346,346,164,171,346,161,174,172,
            346,346,180,346,346,168,417,182,346,183,
            346,185,179,184,346,346,186,13,346,346,
            346,187,191,195,192,188,346,346,189,198,
            346,199,346,346,346,202,205,346,209,346,
            413,346,423,208,346,346,346,346,210,212,
            346,395,213,346,346,211,346,346,397,216,
            346,214,215,346,391,346,217,220,346,218,
            219,346,221,346,346,346,346,346,225,346,
            223,346,224,346,222,416,346,346,228,346,
            230,28,231,346,346,229,232,226,346,22,
            236,227,346,346,235,237,369,346,241,346,
            376,346,361,240,346,346,346,346,243,346,
            244,346,346,247,346,354,346,362,249,248,
            346,242,346,352,346,351,346,346,251,250,
            346,346,254,346,346,263,255,252,346,346,
            253,346,258,256,257,346,260,259,346,399,
            346,422,346,346,261,346,346,346,346,346,
            346,388,265,262,266,384,346,264,267,346,
            268,385,346,346,420,346,269,346,271,346,
            346,346,272,273,270,33,346,346,274,346,
            346,276,277,414,278,346,279,346,346,346,
            281,275,280,346,346,367,346,364,346,282,
            346,283,346,288,346,286,346,346,356,346,
            346,287,346,289,346,346,291,346,346,355,
            404,346,353,346,346,346,292,405,346,290,
            293,400,295,346,350,346,415,296,346,46,
            346,294,346,346,297,300,299,298,346,393,
            346,301,389,346,386,346,346,346,303,383,
            346,302,346,377,346,346,346,346,372,307,
            346,412,346,346,346,19,346,305,304,306,
            309,408,310,346,409,308,311,346,360,346,
            312,346,346,315,313,346,314,346,316,346,
            346,346,346,403,348,346,346,346,318,317,
            346,402,346,319,346,320,346,323,321,326,
            346,322,324,346,381,346,380,346,346,346,
            346,328,346,346,325,327,346,346,358,407,
            410,346,346,363,346,346,346,330,366,349,
            346,332,329,346,398,406,331,346,346,346,
            346,335,333,346,418,334,346,346,337,346,
            346,357,336,346,338,346,346,387,347,346,
            346,394,346,421,339,346,375,346,346,401,
            341,346,346,382,346,340,346,343,346,346,
            346,346,346,346,371,346,373,346,346,346,
            346,390,346,346,346,342
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
           NUM_STATES        = 265,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 423,
           MAX_LA            = 1,
           NUM_RULES         = 77,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 78,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 25,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 345,
           ERROR_ACTION      = 346;

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
