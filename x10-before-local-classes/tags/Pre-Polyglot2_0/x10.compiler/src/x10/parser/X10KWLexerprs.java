

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
            7,6,6,7,3,6,4,5,6,7,
            4,3,8,2,10,9,6,5,4
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
            1,82,134,152,62,82,30,108,84,77,
            23,141,65,26,154,18,160,38,74,43,
            48,46,56,162,163,164,168,94,169,92,
            64,90,171,170,174,102,175,86,178,180,
            104,107,183,184,106,185,109,116,186,189,
            117,190,194,196,200,206,192,197,209,210,
            211,212,121,213,125,215,221,130,223,131,
            225,133,228,229,230,231,234,236,237,239,
            244,240,245,248,249,251,252,258,262,255,
            265,266,268,259,271,273,278,272,57,275,
            281,282,284,285,288,293,140,290,294,298,
            300,136,286,301,304,307,309,310,311,146,
            312,315,318,316,327,329,320,331,333,334,
            335,336,338,340,341,348,344,345,349,351,
            354,357,361,362,363,364,365,367,368,374,
            379,380,381,383,384,386,390,391,395,366,
            396,397,398,401,399,405,409,403,410,416,
            149,418,412,419,421,424,425,427,428,429,
            431,432,434,439,438,435,441,442,445,456,
            454,449,452,459,460,463,464,465,469,472,
            475,478,479,480,486,466,477,489,483,493,
            494,491,500,498,495,502,504,505,511,513,
            508,515,519,522,523,524,526,527,530,531,
            534,539,537,541,542,543,545,551,553,556,
            546,557,558,561,563,564,565,566,570,569,
            571,576,579,585,583,587,572,590,592,598,
            600,602,603,588,605,596,608,609,612,614,
            616,617,622,618,624,627,620,628,629,635,
            633,636,634,638,646,648,368,368
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
            20,21,0,6,2,0,1,0,3,0,
            1,14,7,11,12,18,11,0,16,17,
            3,4,0,1,7,0,9,0,11,7,
            3,4,7,8,7,0,0,2,11,12,
            5,0,17,0,0,10,3,3,4,8,
            15,16,8,0,11,11,0,4,22,6,
            17,0,1,0,8,0,3,2,12,0,
            7,0,11,0,1,4,23,8,9,14,
            9,0,9,0,3,0,0,0,0,8,
            7,5,9,5,6,0,0,2,2,12,
            0,16,6,3,0,10,2,7,23,0,
            0,1,0,0,10,0,7,7,3,0,
            0,1,10,4,9,0,7,2,0,17,
            0,0,23,0,6,10,8,6,25,0,
            7,0,0,0,2,2,7,0,0,0,
            0,10,4,0,0,8,6,0,9,0,
            3,7,0,0,0,0,2,4,0,0,
            2,0,19,0,15,0,0,8,16,0,
            7,6,11,4,19,0,10,2,0,0,
            0,0,0,0,0,3,2,6,8,11,
            0,1,0,14,0,3,2,0,0,0,
            0,3,3,0,1,0,0,10,0,0,
            4,6,12,0,0,1,3,0,0,11,
            0,0,13,6,0,7,5,0,0,2,
            10,0,1,9,0,0,2,0,1,11,
            0,0,0,8,0,5,4,0,4,2,
            0,0,2,0,0,0,3,0,1,0,
            9,20,0,0,2,10,7,0,1,0,
            0,1,3,0,11,21,0,1,0,0,
            0,0,3,0,0,0,5,0,8,0,
            5,18,3,15,10,8,0,1,0,1,
            0,1,0,0,0,0,4,0,3,0,
            0,1,9,0,0,6,9,0,0,2,
            0,1,18,0,1,7,0,14,2,15,
            0,0,0,0,0,0,0,0,5,4,
            10,5,8,0,12,2,9,16,0,0,
            0,1,0,0,1,0,8,8,3,0,
            0,9,3,3,0,0,0,0,0,3,
            0,1,0,8,0,8,8,3,0,0,
            2,0,10,4,20,0,1,0,0,1,
            0,4,11,0,0,2,0,0,0,9,
            0,0,1,0,0,2,10,0,0,12,
            0,0,5,13,0,5,22,9,0,8,
            22,0,8,0,20,0,1,4,0,0,
            9,13,0,0,0,0,2,9,0,10,
            8,0,1,5,0,12,0,0,0,0,
            6,4,0,5,5,0,21,2,0,1,
            0,15,0,0,0,2,14,0,6,0,
            1,0,5,0,0,4,12,0,18,6,
            0,1,0,6,0,0,4,13,0,1,
            6,0,0,0,3,0,0,5,2,0,
            0,1,3,0,9,12,0,4,0,1,
            0,0,0,7,0,0,2,6,3,7,
            0,1,0,1,14,0,0,0,1,4,
            0,5,0,0,0,0,2,2,0,0,
            0,0,12,10,0,0,6,2,0,17,
            11,13,0,5,0,1,0,0,6,0,
            19,0,6,6,5,0,5,0,1,0,
            1,0,0,1,0,1,5,0,0,2,
            2,0,17,0,1,0,0,0,3,0,
            9,0,1,0,7,2,0,0,0,13,
            4,4,0,0,0,0,1,0,5,7,
            0,0,14,24,10,0,0,0,3,0,
            13,4,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            368,98,89,102,94,100,93,84,91,95,
            90,83,86,99,97,101,87,368,85,92,
            88,96,368,127,115,368,124,368,125,368,
            106,392,122,114,116,128,123,368,113,112,
            134,132,368,139,131,368,133,368,130,138,
            144,142,147,146,141,368,368,149,140,143,
            150,368,145,368,368,151,163,121,118,442,
            152,148,120,368,162,119,368,137,236,136,
            418,368,105,368,110,368,108,173,111,368,
            109,368,104,368,157,160,135,164,165,172,
            161,368,158,368,170,368,368,368,368,169,
            176,178,177,182,183,368,368,184,188,107,
            368,400,433,201,368,185,203,200,180,368,
            368,211,368,368,204,368,208,210,248,368,
            368,117,214,243,247,368,242,254,368,213,
            368,368,207,368,297,255,296,103,367,368,
            126,368,368,368,154,155,129,368,368,368,
            368,153,159,368,368,156,166,368,167,368,
            174,171,368,368,368,368,181,179,368,368,
            187,368,168,368,175,368,368,189,440,368,
            190,191,194,192,186,368,195,193,13,368,
            368,368,368,368,368,202,205,199,198,196,
            368,206,368,197,368,209,212,368,368,368,
            368,216,217,368,435,368,368,215,368,368,
            219,447,218,368,368,417,221,368,368,220,
            368,368,419,222,368,223,224,368,368,225,
            413,368,226,227,368,368,228,368,230,231,
            368,368,368,229,368,232,233,368,237,234,
            368,368,439,368,368,368,239,28,240,368,
            238,235,368,368,241,249,391,22,245,368,
            368,250,246,368,244,398,368,383,368,368,
            368,368,252,368,368,368,256,368,253,368,
            258,384,259,251,257,376,368,374,368,373,
            368,260,368,368,368,368,261,368,264,368,
            368,267,262,368,368,266,265,368,368,268,
            368,421,263,368,446,271,368,269,272,270,
            368,368,368,368,368,368,368,368,276,436,
            273,277,410,368,275,407,278,274,368,368,
            368,281,368,368,283,368,279,280,284,368,
            33,282,285,286,368,368,368,368,368,289,
            368,291,368,288,368,290,292,293,368,368,
            389,368,294,295,287,368,386,368,368,300,
            368,298,299,368,368,378,368,368,368,301,
            368,368,303,368,368,426,377,368,368,375,
            368,368,305,427,368,422,302,304,368,307,
            372,368,308,368,306,368,445,309,368,46,
            310,437,368,368,368,368,313,311,368,415,
            312,368,314,408,368,411,368,368,368,368,
            406,315,368,405,316,368,318,317,368,399,
            368,319,368,368,368,394,320,368,321,368,
            434,19,323,368,368,324,431,368,322,430,
            368,382,368,327,368,368,326,325,368,329,
            328,368,368,368,330,368,368,425,370,368,
            368,438,332,368,331,424,368,333,368,334,
            368,368,368,335,368,368,339,337,341,338,
            368,403,368,402,336,368,368,368,343,340,
            368,342,368,368,368,368,380,429,368,368,
            368,368,432,385,368,368,371,345,368,388,
            344,428,368,346,368,420,368,368,347,368,
            409,368,348,354,349,368,350,368,351,368,
            441,368,368,353,368,379,352,368,368,369,
            416,368,355,368,444,368,368,368,357,368,
            356,368,397,368,358,359,368,368,368,404,
            360,443,368,368,368,368,363,368,364,362,
            368,368,395,361,393,368,368,368,365,368,
            412,423
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
           NUM_STATES        = 285,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 447,
           MAX_LA            = 1,
           NUM_RULES         = 79,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 80,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 25,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 367,
           ERROR_ACTION      = 368;

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
