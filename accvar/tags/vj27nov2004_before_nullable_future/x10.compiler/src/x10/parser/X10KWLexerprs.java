

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
            4,4,5,7,6,4,5,11,4,3,
            8,2,5,10,5,4,9,6,6,6,
            11,5,4
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
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,86,61,155,44,164,60,
            97,65,22,68,46,52,21,64,28,26,
            75,58,36,83,74,166,167,93,169,106,
            85,108,170,171,172,110,95,173,177,90,
            109,178,119,149,181,182,27,184,114,122,
            185,188,192,195,196,199,201,207,202,198,
            210,211,212,215,216,219,126,217,125,226,
            230,218,133,131,227,232,233,235,238,236,
            239,240,245,248,138,249,250,256,251,255,
            260,261,263,136,265,266,268,270,271,278,
            272,275,281,282,283,285,287,289,291,140,
            295,297,147,143,298,300,302,305,306,309,
            311,312,314,316,150,315,319,318,325,320,
            330,333,334,335,337,338,341,343,346,347,
            351,354,349,355,357,360,361,362,364,365,
            367,158,375,377,378,379,382,380,384,363,
            388,390,392,394,395,397,396,401,398,405,
            409,411,407,415,417,418,419,420,421,425,
            427,428,430,432,433,435,437,438,441,439,
            442,443,451,452,453,455,459,457,462,463,
            468,471,472,475,477,479,480,481,489,482,
            485,492,490,495,494,497,502,498,506,507,
            508,509,513,516,519,523,526,514,527,528,
            531,532,533,535,540,542,541,543,544,550,
            553,556,558,559,560,564,561,551,566,568,
            570,575,569,574,578,579,581,588,590,591,
            592,582,593,597,602,605,607,608,610,611,
            612,615,618,619,620,623,628,630,624,634,
            636,639,642,643,645,626,594,638,647,649,
            651,654,660,662,655,663,379,379
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,0,12,13,14,15,16,17,18,19,
            0,0,22,2,4,0,0,0,3,4,
            5,4,11,12,9,0,11,16,3,4,
            5,20,16,0,1,0,11,12,3,23,
            5,0,1,8,3,4,11,0,1,0,
            0,4,11,0,0,8,9,0,1,6,
            3,12,8,0,0,2,12,14,11,5,
            6,18,0,10,0,25,4,3,15,0,
            8,2,0,1,0,11,0,23,4,3,
            4,9,20,14,20,0,17,0,0,0,
            5,3,3,0,9,8,9,8,0,6,
            7,0,4,2,0,0,2,9,3,4,
            0,10,0,1,10,0,4,0,1,0,
            10,4,0,4,5,3,0,1,0,0,
            20,2,17,7,0,7,21,0,16,10,
            3,4,8,0,1,0,0,2,0,0,
            0,0,0,5,8,6,0,0,6,9,
            0,0,6,0,0,2,5,0,0,2,
            19,0,15,2,0,0,16,0,0,4,
            0,0,8,19,6,5,0,10,2,0,
            0,0,11,4,0,0,0,0,0,3,
            10,4,11,8,6,0,0,2,14,0,
            1,0,0,1,0,0,10,0,0,0,
            6,3,5,12,0,1,11,0,0,0,
            0,1,13,6,0,0,7,2,10,0,
            0,2,0,9,0,0,1,0,8,0,
            0,0,10,9,0,5,7,0,11,2,
            0,0,0,2,0,5,0,3,0,1,
            0,9,2,22,0,21,0,0,4,0,
            3,0,1,17,0,0,1,11,0,10,
            0,0,2,0,0,0,3,0,0,0,
            1,7,18,8,0,8,15,19,10,0,
            1,7,0,0,0,3,0,0,5,3,
            0,1,0,6,2,0,0,1,0,4,
            0,1,18,0,0,2,0,3,10,0,
            0,0,0,0,0,3,0,7,2,8,
            7,12,16,9,0,1,0,0,0,0,
            4,0,1,0,1,8,8,0,9,0,
            3,0,3,0,0,0,0,0,5,3,
            0,1,8,8,0,8,0,3,0,1,
            0,5,2,22,0,1,0,0,0,0,
            0,1,6,5,0,8,0,0,2,0,
            11,0,0,9,0,1,0,0,0,10,
            0,0,0,12,7,9,8,7,21,8,
            0,0,0,21,0,13,0,0,0,9,
            9,0,0,2,10,13,8,0,12,7,
            0,0,2,6,0,1,0,6,0,0,
            0,0,6,5,0,0,7,7,0,0,
            2,0,1,0,0,2,0,0,17,15,
            6,0,1,14,7,0,0,0,0,1,
            5,0,0,0,18,0,9,5,0,13,
            7,6,0,1,6,0,0,0,3,2,
            0,0,0,3,0,1,5,5,12,0,
            0,0,0,0,5,4,0,4,6,0,
            0,2,0,1,14,0,1,0,0,0,
            0,3,5,0,1,0,7,0,0,0,
            20,2,12,0,0,10,2,0,0,11,
            0,0,15,6,4,7,13,0,1,0,
            0,0,0,0,4,6,0,6,5,7,
            19,0,1,7,0,1,0,0,1,0,
            0,0,1,7,0,6,2,0,0,0,
            2,11,0,0,7,0,7,0,1,0,
            0,9,3,0,1,0,13,0,0,4,
            2,0,0,2,0,3,0,3,0,24,
            0,14,0,0,0,5,10,9,5,0,
            1,0,0,0,0,4,0,13,6,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            379,101,92,105,87,97,96,103,94,98,
            93,379,89,102,100,104,90,99,88,95,
            379,379,91,116,129,379,379,379,137,134,
            135,132,115,117,136,379,133,114,149,146,
            147,113,411,379,106,379,145,148,124,184,
            121,379,127,123,128,125,122,379,144,379,
            379,143,126,379,379,141,142,379,120,130,
            119,108,111,379,379,153,112,403,118,140,
            139,131,379,154,379,378,152,164,155,379,
            151,177,379,158,379,163,379,138,172,109,
            110,159,150,176,429,379,175,379,379,379,
            161,178,171,379,162,165,166,170,379,187,
            186,379,180,188,379,379,209,181,207,206,
            379,189,379,215,210,379,214,379,224,379,
            217,225,379,247,248,252,22,250,379,379,
            216,260,233,251,379,182,455,379,441,261,
            282,281,451,379,107,379,379,156,379,379,
            379,379,379,160,157,167,379,379,173,168,
            379,379,174,379,379,185,183,379,379,191,
            169,379,179,192,379,379,449,379,379,194,
            379,379,193,190,195,196,379,199,197,379,
            379,13,198,200,379,379,379,379,379,208,
            201,213,202,204,205,379,379,211,203,379,
            212,379,379,445,379,379,218,379,379,379,
            462,222,220,219,379,428,221,379,379,379,
            379,228,430,223,379,379,226,227,424,379,
            379,230,379,229,379,379,235,379,231,379,
            379,379,232,234,379,238,237,379,236,239,
            379,379,379,448,379,242,379,244,28,245,
            379,243,246,240,379,241,379,379,402,379,
            253,379,255,409,379,379,394,249,379,254,
            379,379,256,379,379,379,258,379,379,379,
            385,262,395,259,379,387,257,440,263,379,
            384,264,379,379,379,265,379,379,266,268,
            379,270,379,269,271,379,379,432,379,272,
            379,461,267,379,379,273,379,275,274,379,
            379,379,379,379,379,289,379,278,418,421,
            279,277,276,280,379,454,379,379,379,379,
            283,379,286,379,288,284,285,379,287,33,
            290,379,291,379,379,379,379,379,446,294,
            379,296,293,295,379,297,379,298,379,442,
            379,299,400,292,379,397,379,379,379,379,
            379,304,300,302,379,301,379,379,389,379,
            303,379,379,305,379,307,379,379,379,388,
            379,379,379,386,309,308,310,433,306,311,
            379,379,379,383,46,312,379,379,379,313,
            314,379,379,317,426,315,316,379,422,419,
            379,379,458,459,379,318,379,457,73,379,
            379,379,417,319,379,379,416,320,379,379,
            321,379,410,379,379,405,379,379,322,323,
            325,379,444,324,327,19,379,379,379,393,
            328,379,379,379,326,379,330,331,379,329,
            435,332,57,334,333,379,379,379,335,381,
            379,379,379,336,379,339,337,338,434,379,
            379,379,379,379,340,341,379,344,343,379,
            379,345,379,414,342,379,413,379,379,379,
            379,347,346,379,349,379,348,379,379,379,
            399,391,443,379,379,396,438,379,379,351,
            379,379,350,382,353,352,437,379,431,379,
            379,379,379,379,355,354,379,356,453,357,
            420,379,359,358,379,450,379,379,361,379,
            379,379,390,360,379,362,380,379,379,379,
            427,363,379,379,364,379,365,379,456,379,
            379,366,367,379,408,379,415,379,379,368,
            369,379,379,370,379,371,379,372,379,373,
            379,406,379,379,379,447,404,374,460,379,
            375,379,379,379,379,376,379,423,439
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
           NUM_STATES        = 292,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 462,
           MAX_LA            = 1,
           NUM_RULES         = 83,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 84,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 25,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 378,
           ERROR_ACTION      = 379;

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
