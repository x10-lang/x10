
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
            8,6,7,5,4,4,5,4,5,5,
            8,7,2,6,4,4,7,5,5,7,
            5,3,4,2,10,6,10,3,9,4,
            6,3,4,7,7,9,6,6,5,6,
            8,5,6,12,4,5,6,9,4,3,
            4,8,5,5,13,6,6,5,5,7,
            12,7,6,6,7,3,6,4,5,6,
            7,4,11,3,8,2,10,9,4,4,
            10,6,5,4
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
            1,1,1,1,1,1,87,33,150,161,
            28,170,119,31,80,24,172,63,45,168,
            65,174,40,72,82,50,79,58,176,177,
            178,182,84,183,90,19,96,98,149,184,
            185,91,100,188,107,193,191,106,192,111,
            197,101,200,121,123,199,203,112,204,207,
            208,211,217,209,216,210,222,223,129,132,
            224,136,228,232,130,234,140,236,142,229,
            240,241,242,245,247,249,248,255,55,250,
            256,257,260,262,263,265,268,267,271,275,
            277,278,280,282,283,287,288,143,285,292,
            294,295,296,299,300,303,308,144,309,301,
            313,315,154,311,322,317,324,319,326,327,
            328,156,330,331,332,337,338,344,346,350,
            348,353,342,354,355,356,359,363,366,367,
            368,369,371,374,373,377,380,378,385,384,
            387,386,396,389,399,400,402,403,405,406,
            410,412,413,416,417,418,419,422,424,426,
            428,431,433,438,159,436,434,442,444,446,
            447,448,450,451,452,454,456,459,458,464,
            462,465,466,472,475,477,478,479,482,483,
            485,486,487,495,496,500,498,504,505,506,
            507,510,513,514,515,518,517,519,523,524,
            526,528,533,536,534,542,545,546,550,547,
            555,535,553,557,558,560,561,565,568,570,
            573,574,563,577,578,584,579,587,583,590,
            592,591,598,593,596,600,601,602,606,608,
            609,611,613,612,620,617,624,625,626,628,
            629,631,627,636,638,639,641,642,651,643,
            647,653,654,656,658,660,662,664,666,668,
            669,670,674,677,679,680,682,683,685,688,
            686,690,692,697,694,699,701,702,706,401,
            401
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
            20,3,22,0,1,2,3,0,1,11,
            0,0,0,3,11,12,18,7,11,0,
            17,18,3,4,0,1,7,3,9,0,
            11,7,3,4,0,11,7,0,26,2,
            11,12,0,6,0,3,4,10,14,5,
            8,0,15,11,17,4,5,13,0,0,
            16,0,1,0,1,7,8,8,7,0,
            0,12,9,4,23,0,18,0,9,0,
            0,4,3,8,9,0,0,8,2,19,
            0,0,7,2,9,5,5,17,0,13,
            0,24,0,23,2,5,6,17,0,0,
            12,0,10,5,3,0,7,2,7,0,
            1,0,0,0,16,10,7,4,0,0,
            7,10,23,0,5,0,3,2,0,18,
            0,13,9,5,22,10,8,0,8,0,
            1,0,1,0,7,0,0,0,2,2,
            7,0,0,0,0,10,4,0,5,8,
            0,0,0,9,7,3,0,6,0,0,
            4,2,0,0,2,15,0,0,0,0,
            0,8,5,7,4,0,0,19,2,11,
            11,0,0,0,0,10,3,0,0,2,
            8,0,1,0,13,0,3,2,10,0,
            0,0,3,3,0,1,0,0,0,0,
            1,5,4,12,0,0,0,3,11,0,
            5,0,0,7,0,1,0,0,2,10,
            0,1,11,6,0,13,0,0,2,0,
            1,0,0,9,0,8,0,0,6,2,
            4,0,11,0,0,0,2,4,0,0,
            0,3,0,1,9,21,15,0,0,2,
            0,11,0,1,0,7,0,3,0,20,
            10,0,1,0,1,0,0,0,3,0,
            0,0,16,15,8,6,0,0,1,8,
            10,0,6,0,1,0,19,0,3,0,
            1,4,0,0,0,0,3,16,0,1,
            5,9,0,9,2,0,0,0,0,1,
            0,1,0,0,7,2,0,0,13,0,
            1,15,10,0,0,0,0,0,0,12,
            6,8,6,17,9,0,8,2,0,0,
            1,0,0,1,0,0,8,3,3,0,
            9,0,0,4,3,0,0,0,0,4,
            3,0,1,0,8,0,8,0,3,2,
            0,8,0,0,22,0,4,0,1,4,
            10,0,1,0,11,0,0,0,2,0,
            0,0,9,0,9,0,1,0,0,10,
            2,0,12,0,0,0,9,14,21,6,
            6,0,21,8,0,1,0,0,0,8,
            4,0,0,22,0,0,0,9,0,5,
            9,14,10,8,0,0,2,0,12,0,
            1,6,5,0,0,0,0,4,2,0,
            6,6,0,0,0,1,0,0,0,7,
            2,5,0,0,1,0,0,0,15,20,
            13,6,0,0,0,0,4,3,16,12,
            5,0,1,0,0,0,0,14,4,0,
            5,5,0,4,0,1,0,0,6,0,
            0,2,0,3,0,1,9,0,12,0,
            1,4,0,0,2,13,0,0,0,1,
            7,5,0,0,7,2,0,1,6,0,
            0,0,0,4,3,0,6,0,1,0,
            0,0,2,2,12,0,1,0,0,10,
            0,0,0,18,2,5,0,6,11,0,
            1,5,14,0,0,0,0,0,0,5,
            0,1,9,6,6,0,1,0,0,1,
            0,0,0,6,19,5,0,21,2,8,
            0,1,0,0,2,0,3,0,1,0,
            18,0,3,0,9,0,1,0,0,0,
            2,2,9,0,7,14,0,4,0,0,
            4,0,0,4,0,0,5,0,1,0,
            1,0,7,0,10,13,0,0,0,6,
            0,0,2,25,3,0,10,0,0,4,
            0,20,14,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            401,103,94,107,99,98,105,89,96,100,
            95,88,91,102,104,106,90,92,401,97,
            101,170,93,401,117,122,118,401,110,169,
            401,401,401,113,121,123,451,114,109,401,
            120,119,141,139,401,131,138,132,140,401,
            137,129,151,149,401,130,148,401,400,156,
            147,150,401,157,401,128,125,158,452,134,
            127,401,159,126,155,144,143,425,401,401,
            135,401,146,401,164,154,153,115,145,401,
            401,116,165,167,142,401,152,401,168,401,
            401,174,180,171,172,401,401,179,183,178,
            401,401,186,199,187,189,467,433,401,182,
            401,173,401,191,195,194,193,475,401,401,
            112,401,196,211,213,401,220,215,212,401,
            223,401,401,401,210,216,222,257,401,401,
            256,226,219,401,108,401,262,269,401,225,
            401,175,261,314,248,270,313,401,477,401,
            111,401,124,401,133,401,401,401,161,162,
            136,401,401,401,401,160,166,401,176,163,
            401,401,401,177,181,184,401,188,401,401,
            190,192,401,401,198,185,401,401,401,13,
            401,200,202,201,203,401,401,197,204,205,
            207,401,401,401,401,206,214,401,401,217,
            209,401,218,401,208,401,221,224,227,401,
            401,401,228,229,401,469,401,401,401,401,
            450,485,231,230,401,401,401,233,232,401,
            234,401,401,235,401,480,401,401,238,446,
            401,239,236,237,401,481,401,401,241,401,
            243,401,401,240,401,242,401,401,245,247,
            246,401,244,401,401,401,473,251,401,401,
            401,253,28,254,252,249,250,401,401,255,
            401,258,22,259,401,424,401,260,401,431,
            263,401,264,401,416,401,401,401,266,401,
            401,401,417,265,267,271,401,401,407,409,
            272,401,273,401,406,401,268,401,274,401,
            275,276,401,401,401,401,279,278,401,282,
            281,277,401,280,283,401,401,401,401,454,
            401,484,401,401,286,287,401,401,284,401,
            290,285,288,401,401,401,401,401,401,291,
            292,443,293,289,294,401,295,440,401,401,
            297,401,401,299,401,401,296,300,301,401,
            298,33,401,302,303,401,401,401,401,470,
            306,401,308,401,305,401,307,401,310,422,
            401,309,401,401,304,401,312,401,419,315,
            311,401,317,401,316,401,401,401,411,401,
            401,401,318,401,319,401,321,401,401,410,
            459,401,408,401,401,401,322,460,320,323,
            455,401,405,325,401,483,401,401,401,326,
            327,401,46,324,401,401,401,328,401,330,
            329,471,448,331,401,401,332,401,444,401,
            333,441,439,401,401,401,401,334,336,401,
            438,335,401,401,401,432,401,401,401,338,
            427,341,401,401,468,401,401,401,339,337,
            340,343,19,401,401,401,344,351,342,465,
            464,401,415,401,401,401,401,345,346,401,
            347,349,401,348,401,350,401,401,458,401,
            401,403,401,353,401,472,352,401,457,401,
            355,354,401,401,356,358,401,401,401,436,
            357,359,401,401,360,361,401,435,362,401,
            401,401,401,363,364,401,365,401,366,401,
            401,401,413,463,466,401,367,401,401,418,
            401,401,401,421,369,404,401,370,368,401,
            453,371,461,401,401,401,401,401,401,373,
            401,376,372,374,375,401,476,401,401,379,
            401,401,401,378,442,380,401,377,402,381,
            401,412,401,401,449,401,383,401,479,401,
            382,401,385,401,384,401,430,401,401,401,
            388,389,386,401,387,437,401,390,401,401,
            482,401,401,478,401,401,392,401,393,401,
            395,401,394,401,426,428,401,401,401,397,
            401,401,462,391,398,401,396,401,401,456,
            401,474,445
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
           NUM_STATES        = 313,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 485,
           MAX_LA            = 1,
           NUM_RULES         = 84,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 85,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 26,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 400,
           ERROR_ACTION      = 401;

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
