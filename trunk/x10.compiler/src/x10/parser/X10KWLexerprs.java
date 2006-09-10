
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

public class X10KWLexerprs implements lpg.lpgjavaruntime.ParseTable, X10KWLexersym {

    public interface IsNullable {
        public final static byte isNullable[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte isNullable[] = IsNullable.isNullable;
    public final boolean isNullable(int index) { return isNullable[index] != 0; }

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
            12,7,6,6,7,3,6,4,5,6,
            7,4,3,8,2,10,9,4,6,5,
            4
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
            1,1,1,84,149,19,114,84,47,157,
            30,87,23,163,57,69,160,58,165,28,
            75,88,42,73,49,166,27,170,175,43,
            63,93,83,100,167,177,178,179,103,181,
            110,182,186,107,188,183,191,104,190,111,
            120,193,197,124,202,196,203,205,206,211,
            213,214,215,216,125,134,217,133,224,218,
            128,227,138,229,140,232,233,234,235,238,
            241,240,243,246,76,250,245,253,255,256,
            257,258,266,259,262,270,271,273,274,277,
            276,279,282,283,286,285,289,290,295,291,
            144,297,298,300,303,153,302,307,113,313,
            310,315,316,317,145,320,319,322,324,331,
            333,335,339,337,342,328,343,345,348,349,
            356,347,352,353,362,364,357,366,368,369,
            371,370,372,373,384,374,383,389,387,391,
            394,395,399,380,401,403,404,406,409,412,
            413,418,414,415,421,154,423,425,428,426,
            430,431,432,434,437,438,441,442,445,448,
            440,450,452,451,459,461,463,465,466,468,
            471,469,472,476,480,482,485,487,488,489,
            496,490,493,499,497,502,501,504,505,507,
            512,514,515,516,525,517,527,519,528,531,
            533,537,529,538,540,545,549,551,552,546,
            544,556,558,559,564,567,569,571,570,577,
            560,562,575,579,582,586,583,588,589,590,
            591,598,593,601,595,603,604,605,611,607,
            615,617,618,619,621,625,626,622,629,632,
            633,636,634,638,641,644,645,646,649,651,
            652,655,656,654,659,658,666,667,671,381,
            381
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,16,17,0,19,
            20,21,0,1,2,7,0,0,2,0,
            3,4,3,11,12,8,9,8,11,17,
            18,0,0,1,3,4,0,1,0,8,
            2,9,11,12,6,0,0,0,10,3,
            4,5,0,15,7,17,4,11,0,1,
            13,3,0,16,0,0,8,5,4,11,
            8,7,0,0,1,3,0,0,1,14,
            18,5,0,11,11,8,4,23,12,0,
            18,9,0,0,5,3,0,5,9,0,
            0,2,0,0,8,9,6,7,5,0,
            17,2,13,0,0,2,23,0,16,10,
            7,7,0,0,2,8,3,0,1,0,
            16,8,10,0,0,8,2,4,0,10,
            23,8,0,0,10,3,0,18,5,0,
            7,9,0,1,0,0,0,8,12,0,
            4,2,8,25,0,10,0,0,0,5,
            0,0,0,7,3,0,9,0,8,0,
            0,2,0,6,4,0,0,19,2,17,
            15,0,0,8,0,0,5,2,4,7,
            0,19,0,0,0,0,0,0,1,3,
            5,11,10,0,11,2,0,13,0,3,
            2,0,0,0,0,3,3,0,1,0,
            0,10,0,4,0,0,12,7,3,0,
            1,7,0,11,0,0,0,0,0,2,
            8,0,6,2,10,0,1,9,13,0,
            0,1,0,0,5,0,0,2,0,6,
            4,0,0,11,0,0,4,2,0,0,
            0,3,2,9,0,1,0,0,20,0,
            1,0,0,22,8,3,0,1,11,0,
            21,10,0,1,0,0,0,3,0,0,
            5,0,0,0,15,6,5,0,10,6,
            0,1,0,1,0,19,0,3,0,1,
            4,0,0,16,0,3,0,0,0,1,
            9,0,0,9,7,0,0,2,2,13,
            8,0,1,0,1,0,15,0,0,0,
            0,0,0,0,5,10,6,6,5,0,
            12,9,0,0,17,2,0,5,0,1,
            0,1,0,0,0,9,3,3,0,20,
            0,3,0,0,4,0,3,5,0,1,
            5,0,0,0,0,3,5,0,4,2,
            0,1,0,10,0,0,4,0,1,0,
            0,0,2,0,9,11,0,0,9,0,
            0,0,1,10,0,6,2,0,12,0,
            0,0,0,22,14,5,9,6,0,22,
            0,1,0,5,0,0,4,0,0,20,
            0,0,0,5,9,0,9,2,14,0,
            10,0,1,12,0,6,0,0,0,0,
            4,7,0,6,6,0,0,2,0,1,
            0,0,2,0,0,1,0,15,7,13,
            21,0,6,0,0,0,0,4,0,16,
            4,7,4,12,0,1,0,0,0,14,
            0,1,0,7,7,3,0,0,0,0,
            12,2,6,0,0,0,9,3,0,1,
            0,0,1,8,4,0,13,0,0,0,
            2,0,7,0,1,8,0,1,0,0,
            0,12,4,3,0,6,0,1,0,18,
            2,0,0,2,10,0,1,0,0,0,
            0,2,0,11,0,7,6,0,1,7,
            0,14,0,0,0,1,0,7,6,6,
            0,1,6,19,0,1,0,0,0,1,
            0,0,5,7,0,0,2,2,0,1,
            9,0,0,0,3,0,1,0,18,2,
            0,8,2,0,0,0,14,4,0,4,
            0,0,1,0,0,0,1,0,0,0,
            10,13,8,10,6,0,0,2,24,3,
            0,14,0,0,4,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            381,100,91,104,96,93,102,95,86,97,
            92,85,88,99,101,103,87,89,381,94,
            90,98,381,114,118,105,381,381,157,381,
            137,135,110,117,119,134,136,111,133,116,
            115,381,381,160,147,145,381,108,381,144,
            152,161,143,146,153,381,381,381,154,124,
            121,123,381,155,130,151,162,122,381,127,
            405,128,381,131,381,381,125,149,140,126,
            150,139,381,381,107,166,381,381,142,432,
            148,112,381,165,106,141,163,138,113,381,
            431,164,381,381,167,174,381,173,168,381,
            381,177,381,381,180,181,186,187,456,381,
            413,188,176,381,381,192,184,381,397,189,
            447,204,381,381,208,213,206,381,216,381,
            203,205,209,381,381,215,260,248,381,219,
            212,247,381,381,261,253,381,218,302,381,
            303,252,381,120,381,381,381,129,109,381,
            169,158,132,380,381,156,381,381,381,159,
            381,381,381,170,178,381,171,381,175,381,
            381,185,381,182,183,381,381,172,191,454,
            179,381,381,194,381,381,193,197,196,195,
            381,190,381,13,381,381,381,381,211,207,
            202,198,199,381,200,210,381,201,381,214,
            217,381,381,381,381,221,222,381,449,381,
            381,220,381,224,381,381,223,462,226,381,
            430,227,381,225,381,381,381,381,381,230,
            228,381,229,233,426,381,231,232,459,381,
            381,235,381,381,234,381,381,239,381,237,
            238,381,381,236,381,381,242,453,381,381,
            381,244,246,243,28,245,381,381,240,22,
            250,381,381,241,404,251,381,255,249,381,
            411,254,381,396,381,381,381,257,381,381,
            258,381,381,381,256,262,389,381,263,264,
            381,387,381,386,381,259,381,265,381,266,
            267,381,381,269,381,270,381,381,381,273,
            268,381,381,271,272,381,381,274,278,275,
            277,381,434,381,461,381,276,381,381,381,
            381,381,381,381,423,279,282,283,285,381,
            281,284,381,381,280,420,381,286,381,287,
            381,289,381,381,381,288,290,291,33,293,
            381,292,381,381,450,381,295,294,381,297,
            296,381,381,381,381,299,298,381,301,402,
            381,399,381,300,381,381,304,381,306,381,
            381,381,391,381,307,305,381,381,308,381,
            381,381,310,390,381,312,439,381,388,381,
            381,381,381,309,440,314,311,435,381,385,
            381,460,381,315,381,381,316,381,381,313,
            46,381,381,319,317,381,318,320,451,381,
            428,381,321,424,381,421,381,381,381,381,
            322,419,381,418,323,381,381,324,381,412,
            381,381,407,381,381,448,381,326,328,327,
            325,381,330,19,381,381,381,331,381,329,
            333,444,335,445,381,395,381,381,381,332,
            381,337,381,334,336,338,381,381,381,381,
            437,383,438,381,381,381,339,340,381,452,
            381,381,342,343,341,381,344,381,381,381,
            347,381,345,381,416,346,381,415,381,381,
            381,446,348,349,381,350,381,351,381,401,
            393,381,381,443,398,381,352,381,381,381,
            381,354,381,353,381,384,355,381,433,356,
            381,441,381,381,381,360,381,357,358,359,
            381,455,361,422,381,362,381,381,381,392,
            381,381,364,363,381,381,382,429,381,458,
            366,381,381,381,367,381,410,381,365,369,
            381,368,370,381,381,381,417,371,381,457,
            381,381,373,381,381,381,375,381,381,381,
            406,408,374,376,377,381,381,442,372,378,
            381,425,381,381,436
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
           NUM_STATES        = 296,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 462,
           MAX_LA            = 1,
           NUM_RULES         = 81,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 82,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 25,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 380,
           ERROR_ACTION      = 381;

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
