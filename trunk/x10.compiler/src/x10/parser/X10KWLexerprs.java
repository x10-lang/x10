

package x10.parser;

import com.ibm.lpg.*;
import java.io.*;

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
            4,8,5,5,6,6,5,7,7,12,
            4,4,5,7,6,4,5,4,3,8,
            2,5,5,4,9,6,5,4
        };
    };
    public final static byte baseCheck[] = BaseCheck.baseCheck;
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static byte rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };

    public interface BaseAction {
        public final static char baseAction[] = {0,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,81,
            47,29,119,145,88,92,20,98,48,24,
            149,57,153,34,61,54,38,67,75,152,
            102,154,100,76,106,160,105,151,113,161,
            163,80,135,164,112,166,170,171,107,172,
            21,122,175,180,181,184,185,187,189,194,
            190,195,198,199,200,202,203,208,125,214,
            127,210,176,216,133,64,218,219,221,223,
            224,227,225,232,229,236,237,238,239,243,
            246,248,249,126,251,256,252,254,258,262,
            259,265,267,268,269,271,272,276,280,136,
            273,283,140,65,284,286,290,288,292,295,
            297,298,300,301,148,304,306,310,308,317,
            319,321,322,302,325,326,332,327,334,336,
            338,339,341,342,343,344,345,346,351,358,
            356,360,361,366,363,368,370,371,375,376,
            377,379,381,382,387,389,391,395,390,397,
            399,401,403,402,404,408,411,412,413,416,
            417,418,421,419,423,424,432,435,425,437,
            440,441,442,447,444,443,452,455,456,457,
            459,458,462,465,467,468,469,470,477,472,
            474,481,483,484,486,488,490,497,499,500,
            489,503,506,504,510,508,513,515,516,521,
            523,517,527,528,531,529,519,533,534,537,
            540,536,544,545,546,551,549,555,556,557,
            561,565,563,567,571,558,572,575,578,579,
            581,583,584,586,587,591,590,594,595,597,
            599,596,601,603,347,347
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,0,12,13,14,15,16,17,18,0,
            0,2,22,0,1,5,3,7,0,6,
            11,12,4,0,11,16,3,0,19,6,
            3,8,9,6,11,8,0,0,11,12,
            3,4,0,0,1,8,0,4,11,6,
            0,5,9,0,0,5,0,3,8,13,
            4,25,6,10,0,0,2,21,3,0,
            16,2,19,23,10,19,11,0,14,0,
            3,0,13,6,19,4,17,0,1,0,
            3,0,1,12,0,0,0,8,9,4,
            9,0,0,9,9,3,4,6,0,1,
            9,0,16,2,0,0,0,3,2,23,
            6,10,0,1,0,0,10,3,6,0,
            1,6,17,8,0,20,7,0,0,2,
            0,0,0,0,6,4,12,10,6,0,
            0,8,0,0,5,0,6,5,18,0,
            0,0,7,2,0,0,1,14,8,0,
            0,2,2,0,0,16,0,4,0,0,
            6,5,18,0,0,2,8,0,0,0,
            11,0,0,6,10,0,4,0,10,0,
            11,2,5,0,13,0,3,0,0,1,
            0,6,0,0,0,5,0,10,0,3,
            8,0,1,5,11,0,0,0,0,15,
            2,6,0,1,7,0,10,0,0,2,
            0,0,4,0,9,0,1,0,0,9,
            7,0,11,2,0,8,0,0,0,2,
            0,0,0,3,8,0,1,9,6,0,
            22,2,0,0,20,0,3,0,17,0,
            1,0,1,11,0,10,0,0,2,0,
            0,0,3,0,4,0,0,0,21,0,
            7,14,18,4,7,10,0,1,0,1,
            0,0,21,3,0,0,0,3,2,8,
            5,0,1,0,1,0,1,0,0,2,
            0,0,0,0,0,0,0,4,10,7,
            0,7,2,12,9,0,16,0,1,0,
            0,6,0,4,4,0,1,0,1,0,
            0,9,3,3,0,0,0,3,0,0,
            0,0,4,3,8,4,0,1,0,0,
            0,2,4,3,0,1,0,22,0,1,
            0,0,0,0,8,5,4,0,1,8,
            0,0,0,2,11,0,0,0,0,9,
            0,1,0,0,0,10,0,9,12,7,
            7,0,20,9,0,4,0,20,4,0,
            0,0,0,0,4,9,0,5,2,10,
            7,0,1,12,0,0,0,0,0,5,
            2,0,7,7,0,1,0,0,0,0,
            2,0,5,0,17,14,0,1,7,13,
            0,8,0,0,1,0,0,0,0,0,
            21,9,5,8,5,15,0,1,0,0,
            12,3,0,0,2,0,7,0,3,0,
            1,8,0,6,0,0,0,2,0,5,
            0,1,0,1,8,13,0,0,0,3,
            0,1,0,0,7,0,0,19,2,0,
            12,2,10,0,0,0,11,14,0,5,
            0,1,7,5,0,0,0,0,15,5,
            0,1,0,7,0,1,0,1,11,7,
            0,0,1,18,0,5,2,0,0,2,
            0,1,0,0,1,0,0,9,2,0,
            0,6,2,0,0,0,0,15,0,1,
            0,6,0,0,10,9,13,5,0,0,
            0,0,0,24,0,15,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            347,95,86,99,88,90,82,97,91,92,
            87,347,83,94,98,96,84,93,89,347,
            347,109,85,347,119,177,120,176,347,117,
            108,110,418,347,118,107,129,347,106,126,
            141,127,128,138,125,139,347,347,137,140,
            116,115,347,347,136,113,347,133,114,135,
            347,122,134,347,347,131,347,239,132,371,
            143,346,144,207,347,347,145,123,155,347,
            409,167,206,130,146,142,154,347,147,347,
            102,347,166,103,397,104,165,347,112,347,
            111,347,149,105,347,347,347,152,153,156,
            150,347,347,159,157,162,161,170,347,100,
            171,347,379,178,347,347,347,197,199,174,
            196,179,347,205,347,347,200,168,204,22,
            237,234,220,235,347,421,238,347,347,247,
            347,347,347,347,121,148,101,248,124,347,
            347,151,347,347,158,347,163,164,160,347,
            347,347,172,175,347,347,202,169,173,347,
            347,181,182,347,347,416,347,183,347,347,
            184,185,180,347,347,187,186,347,347,13,
            188,347,347,190,189,347,194,347,191,347,
            192,201,195,347,193,347,198,347,347,413,
            347,203,347,347,347,425,347,208,347,211,
            209,347,396,212,210,347,347,347,347,398,
            215,213,347,216,214,347,392,347,347,218,
            347,347,219,347,217,347,222,347,347,221,
            224,347,223,226,347,225,347,347,347,415,
            347,347,347,231,229,28,232,230,370,347,
            227,233,347,347,228,347,240,347,377,347,
            242,347,362,236,347,241,347,347,243,347,
            347,347,245,347,246,347,347,347,363,347,
            249,244,408,355,251,250,347,353,347,352,
            347,347,254,252,347,347,347,255,258,253,
            256,347,257,347,400,347,424,347,347,259,
            347,347,347,347,347,347,347,389,260,263,
            347,264,386,262,265,347,261,347,420,347,
            347,266,347,267,268,347,419,347,270,347,
            347,269,271,272,33,347,347,273,347,347,
            347,347,275,276,414,277,347,278,347,347,
            347,368,279,280,347,410,347,274,347,365,
            347,347,347,347,281,282,283,347,286,284,
            347,347,347,357,285,347,347,347,347,287,
            347,289,347,347,347,356,347,290,354,291,
            401,347,288,294,347,292,347,351,293,46,
            347,347,347,347,296,295,347,423,297,394,
            387,347,298,390,347,347,347,347,347,385,
            300,347,384,299,347,378,347,347,347,347,
            373,347,304,19,301,302,347,412,306,303,
            347,307,347,347,361,347,347,347,347,347,
            305,309,311,310,312,308,57,313,347,347,
            402,314,347,347,349,347,403,347,315,347,
            317,316,347,318,347,347,347,321,347,320,
            347,382,347,381,322,319,347,347,347,323,
            347,325,347,347,324,347,347,367,359,347,
            411,406,364,347,347,347,327,326,347,350,
            347,399,328,329,347,347,347,347,405,330,
            347,332,347,331,347,417,347,334,336,333,
            347,347,358,388,347,335,348,347,347,395,
            347,422,347,347,376,347,347,337,339,347,
            347,338,340,347,347,347,347,383,347,343,
            347,344,347,347,372,342,374,407,347,347,
            347,347,347,341,347,391
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
           LA_STATE_OFFSET   = 425,
           MAX_LA            = 1,
           NUM_RULES         = 78,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 79,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 25,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 346,
           ERROR_ACTION      = 347;

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
