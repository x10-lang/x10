

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
            4,8,5,5,8,6,6,5,7,7,
            6,7,6,4,5,6,4,3,8,2,
            5,9,5,4
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
            1,1,1,1,1,1,77,105,28,133,
            144,146,24,78,23,150,62,74,118,33,
            147,37,61,29,46,72,54,153,152,86,
            157,89,80,98,17,55,41,100,159,99,
            161,160,107,162,165,168,93,169,112,109,
            170,173,176,179,181,182,184,185,186,192,
            193,194,195,196,120,198,122,207,210,206,
            200,128,212,126,215,216,217,220,221,219,
            223,222,231,224,234,236,237,238,243,239,
            248,245,251,253,254,255,256,257,260,262,
            263,266,267,269,271,273,131,276,278,279,
            281,140,285,286,92,291,288,293,294,139,
            297,299,300,301,307,310,312,313,314,303,
            317,316,318,325,328,329,331,333,335,338,
            339,340,341,343,344,342,351,355,356,357,
            361,362,364,365,370,366,371,372,375,379,
            380,384,382,387,390,391,392,394,395,396,
            398,402,404,323,405,408,409,411,414,413,
            417,418,419,423,425,426,428,432,433,430,
            435,442,441,448,445,450,451,457,437,452,
            460,453,462,463,464,472,470,466,468,476,
            479,477,481,484,485,488,487,494,495,496,
            497,499,501,504,507,508,510,513,515,518,
            519,520,526,523,528,529,530,531,534,536,
            537,538,542,550,543,548,541,553,554,561,
            559,565,563,567,569,571,574,570,579,575,
            581,577,587,583,586,591,592,590,340,340
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,0,12,13,14,15,0,17,18,19,
            20,21,0,0,2,9,3,0,0,1,
            7,4,0,11,12,7,0,15,16,3,
            0,9,6,7,8,0,14,11,3,0,
            18,6,7,0,0,2,11,12,5,19,
            0,0,8,10,3,4,6,6,15,9,
            17,0,11,0,1,4,3,0,7,0,
            7,4,3,23,11,0,1,16,0,12,
            11,0,0,8,6,16,8,0,0,0,
            2,4,3,4,0,8,0,15,0,18,
            2,0,14,7,8,23,5,0,10,0,
            9,0,3,2,7,0,7,0,1,25,
            0,10,0,1,7,10,6,7,0,0,
            2,16,3,0,1,0,0,8,10,0,
            1,0,0,7,2,4,0,12,0,0,
            0,0,6,3,0,7,5,0,0,0,
            2,0,0,6,2,0,17,2,0,15,
            0,0,4,0,0,0,2,7,19,6,
            9,0,0,0,0,0,11,0,4,0,
            3,10,3,11,9,0,0,14,2,0,
            1,0,7,2,0,0,0,1,0,0,
            0,0,0,0,10,3,6,12,9,11,
            0,1,9,0,13,0,0,0,0,2,
            7,5,0,1,0,10,8,0,4,2,
            0,1,0,0,0,0,0,2,5,0,
            6,0,0,11,2,0,0,6,0,3,
            0,1,0,8,2,0,20,0,0,1,
            0,22,7,3,0,0,1,0,11,21,
            0,1,0,0,10,3,0,4,0,0,
            0,5,0,4,17,5,0,1,10,0,
            1,0,0,0,3,0,0,0,6,3,
            18,8,0,8,0,1,9,0,0,2,
            0,1,0,1,0,7,2,0,0,0,
            0,0,0,0,22,5,4,10,5,8,
            0,12,2,15,0,0,0,1,4,4,
            0,0,1,0,0,0,3,3,8,0,
            0,0,3,0,0,4,6,3,0,0,
            1,0,4,0,3,20,0,4,2,0,
            0,0,1,0,0,0,6,0,1,10,
            6,0,9,0,0,2,11,0,0,8,
            0,1,0,0,10,2,0,0,0,12,
            8,5,0,5,0,0,4,0,4,0,
            22,0,0,4,0,8,0,20,13,8,
            0,0,10,2,0,5,12,0,1,0,
            0,0,0,9,5,5,0,21,2,0,
            1,0,0,0,2,0,14,0,17,0,
            9,0,1,6,5,0,0,12,0,1,
            0,18,6,0,0,1,0,0,13,9,
            3,5,9,0,0,0,0,2,0,3,
            0,1,8,0,6,12,0,0,0,0,
            7,2,0,1,0,1,9,0,0,0,
            14,3,0,6,5,0,1,0,0,0,
            0,2,2,0,12,0,0,0,10,2,
            0,0,0,16,11,9,5,0,13,0,
            1,9,0,0,1,0,9,5,0,19,
            0,1,0,5,0,1,0,1,0,0,
            0,9,2,0,0,2,0,8,0,1,
            0,1,0,7,16,0,0,13,2,0,
            0,0,1,0,0,0,0,0,0,14,
            10,0,13,0,0,0,24,0,0,0,
            0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            340,92,83,96,85,94,88,78,89,87,
            84,340,80,93,91,81,340,95,79,86,
            82,90,340,340,107,156,100,340,340,131,
            101,410,340,106,108,130,340,105,104,126,
            340,119,124,123,125,340,364,122,136,340,
            120,134,133,340,340,141,132,135,142,158,
            340,340,157,143,113,112,129,110,140,128,
            144,340,111,340,116,138,117,340,139,340,
            114,102,153,127,115,340,147,137,340,103,
            152,340,340,148,150,390,151,340,340,340,
            163,154,160,159,340,155,340,372,340,356,
            174,340,162,166,167,170,172,340,175,340,
            173,340,191,193,118,340,190,340,200,339,
            340,194,340,97,199,203,230,229,340,340,
            241,202,235,340,98,340,340,234,242,340,
            109,340,340,121,145,146,340,99,340,340,
            340,340,149,164,340,161,168,340,340,340,
            171,340,340,169,177,340,165,178,340,408,
            340,340,179,340,340,340,183,180,176,182,
            181,340,13,340,340,340,184,340,188,340,
            192,185,198,186,189,340,340,187,195,340,
            196,340,197,201,340,340,340,404,340,340,
            340,340,340,340,204,208,206,205,414,207,
            340,389,209,340,391,340,340,340,340,212,
            210,211,340,213,340,385,214,340,216,215,
            340,217,340,340,340,340,340,221,219,340,
            220,340,340,218,407,340,340,224,340,226,
            28,227,340,225,228,340,222,340,22,232,
            340,223,363,233,340,340,237,340,231,370,
            340,355,340,340,236,239,340,240,340,340,
            340,243,340,348,238,245,340,346,244,340,
            345,340,340,340,246,340,340,340,247,250,
            249,248,340,251,340,253,252,340,340,254,
            340,393,340,413,340,255,256,340,340,340,
            340,340,340,340,284,260,382,257,261,262,
            340,259,379,258,340,340,340,411,263,264,
            340,340,266,340,340,340,267,268,265,33,
            340,340,269,340,340,271,405,272,340,340,
            274,340,273,340,276,270,340,275,361,340,
            340,340,358,340,340,340,278,340,282,277,
            280,340,279,340,340,350,281,340,340,283,
            340,285,340,340,349,398,340,340,340,347,
            286,287,340,394,340,340,289,340,290,340,
            344,340,46,293,340,291,340,288,406,292,
            340,340,387,294,340,380,383,340,295,340,
            340,340,340,378,377,296,340,298,297,340,
            371,340,340,340,366,340,300,19,299,340,
            301,340,403,304,303,340,340,401,340,354,
            340,302,306,340,340,309,340,340,305,307,
            310,397,308,340,340,340,340,342,340,312,
            340,314,311,340,313,396,340,340,340,340,
            315,318,340,375,340,374,317,340,340,340,
            316,320,340,319,321,340,322,340,340,340,
            340,352,400,340,402,340,340,340,357,324,
            340,340,340,360,323,343,325,340,399,340,
            392,326,340,340,329,340,327,328,340,381,
            340,409,340,330,340,331,340,351,340,340,
            340,332,341,340,340,388,340,333,340,412,
            340,369,340,334,395,340,340,376,335,340,
            340,340,337,340,340,340,340,340,340,367,
            365,340,384,340,340,340,336
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
           NUM_STATES        = 262,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 414,
           MAX_LA            = 1,
           NUM_RULES         = 74,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 75,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 25,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 339,
           ERROR_ACTION      = 340;

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
