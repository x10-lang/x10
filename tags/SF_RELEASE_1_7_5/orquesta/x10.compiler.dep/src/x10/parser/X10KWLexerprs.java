
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

public class X10KWLexerprs implements lpg.runtime.ParseTable, X10KWLexersym {

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
            6,3,4,7,7,9,8,6,6,5,
            6,8,5,6,12,4,5,6,9,4,
            3,4,8,5,5,3,13,6,6,5,
            5,7,12,7,6,6,7,3,6,4,
            5,6,7,4,11,3,8,2,10,9,
            4,4,10,6,5,4
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
            1,1,1,1,1,1,1,1,89,44,
            142,167,88,125,34,29,16,24,174,71,
            30,168,63,177,49,62,95,61,78,40,
            178,179,180,183,91,150,97,87,104,105,
            184,186,187,190,48,192,81,195,123,107,
            194,112,199,111,202,114,118,193,205,122,
            206,209,210,214,219,213,216,220,222,223,
            132,133,225,138,230,233,135,236,144,238,
            227,146,237,241,243,245,248,250,252,251,
            258,253,259,263,264,265,266,269,270,273,
            274,278,281,283,286,287,284,289,293,151,
            291,294,298,299,300,301,304,305,308,316,
            155,311,306,320,323,157,312,327,324,329,
            331,332,333,334,162,336,338,340,337,349,
            353,342,355,357,351,358,359,363,364,365,
            368,367,371,373,376,378,382,383,385,388,
            374,390,392,394,391,403,396,406,407,409,
            410,413,416,417,395,421,415,422,423,425,
            427,430,432,436,440,434,441,446,165,448,
            443,449,451,453,455,435,458,459,461,463,
            464,467,466,472,470,473,478,481,485,489,
            474,490,491,494,496,497,483,505,502,510,
            508,512,514,517,515,518,506,519,524,528,
            525,530,531,532,536,534,539,542,543,544,
            552,550,555,557,559,562,565,566,567,547,
            569,572,576,570,580,582,578,583,585,587,
            589,592,597,599,601,602,604,603,611,593,
            609,613,614,615,618,620,621,622,626,625,
            629,633,634,636,637,639,640,642,644,648,
            649,651,652,654,656,660,663,664,665,668,
            670,672,674,675,676,680,682,684,691,683,
            688,694,695,696,697,698,702,704,705,700,
            707,709,711,713,718,722,408,408
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,0,16,17,18,19,
            20,6,22,0,1,2,3,12,0,0,
            1,3,3,0,11,12,8,8,15,0,
            11,2,19,0,5,12,7,0,0,10,
            3,3,4,6,0,16,8,9,19,11,
            0,0,0,3,4,4,5,5,8,26,
            0,11,12,3,4,13,6,0,0,17,
            0,11,2,6,23,8,0,0,1,3,
            0,1,15,13,0,1,0,11,11,9,
            4,15,8,0,0,9,0,0,4,6,
            0,0,9,0,8,9,5,0,5,2,
            7,0,0,2,0,1,5,10,24,19,
            19,0,0,23,0,3,5,0,16,2,
            8,0,8,0,1,0,5,10,17,0,
            0,8,2,4,0,10,0,23,4,3,
            15,0,8,2,0,9,0,0,18,5,
            6,10,6,0,1,8,0,0,0,0,
            2,2,0,0,8,0,0,10,6,0,
            5,0,0,0,0,9,13,3,0,8,
            7,0,4,2,0,0,2,18,0,0,
            18,6,0,0,5,0,8,4,0,0,
            2,0,0,11,0,10,0,3,6,0,
            11,2,0,1,13,0,0,0,3,2,
            0,15,0,3,0,3,10,0,1,0,
            0,0,0,0,5,4,12,0,0,1,
            3,11,0,0,0,0,14,5,0,0,
            1,8,0,0,10,2,11,0,1,7,
            0,13,0,0,2,0,0,1,0,9,
            0,6,0,0,11,7,4,0,0,0,
            0,2,4,0,0,0,3,0,1,9,
            0,0,22,16,21,0,11,2,8,0,
            1,10,0,0,20,3,0,1,0,1,
            0,0,0,0,3,0,0,0,6,0,
            17,0,7,7,3,6,16,10,0,1,
            0,18,0,1,0,1,0,0,0,9,
            4,3,0,0,0,1,0,0,5,2,
            0,9,0,0,17,0,1,0,1,13,
            8,0,0,2,0,12,16,0,1,0,
            0,0,10,0,0,0,7,6,4,9,
            7,6,0,19,2,0,0,1,0,0,
            1,6,0,1,0,0,0,9,3,3,
            0,0,0,3,0,4,0,3,6,0,
            1,0,6,0,0,0,22,6,3,0,
            0,2,0,10,4,0,1,0,0,1,
            0,4,0,11,0,21,2,0,0,9,
            0,9,0,0,1,0,0,10,2,0,
            12,0,0,0,9,0,14,0,7,7,
            0,21,0,6,0,1,6,14,0,0,
            0,22,4,0,12,0,0,0,9,9,
            5,0,6,10,0,0,2,0,7,0,
            1,0,5,0,0,4,0,0,0,2,
            7,7,6,0,0,20,8,0,1,0,
            0,0,2,0,5,0,1,13,0,16,
            7,0,0,0,0,4,0,5,17,0,
            12,0,1,4,0,9,0,14,0,5,
            4,0,1,5,0,0,0,3,0,0,
            2,0,7,4,3,0,1,0,12,0,
            1,0,0,2,0,8,0,0,0,5,
            2,0,0,2,8,13,0,1,0,1,
            0,0,0,0,12,4,3,7,0,7,
            0,1,0,0,0,2,2,0,1,0,
            0,0,10,15,0,0,5,2,0,1,
            11,7,0,0,14,0,0,5,0,0,
            5,0,9,0,1,7,7,0,0,1,
            0,0,1,0,18,0,15,7,5,0,
            1,6,0,0,0,2,2,0,21,0,
            3,0,1,0,0,0,3,15,9,0,
            1,0,0,0,9,2,4,0,14,8,
            0,4,2,0,0,0,0,0,4,0,
            5,0,1,0,0,1,0,10,0,13,
            0,8,0,0,2,7,10,0,25,20,
            3,0,0,0,14,4,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            408,105,96,109,101,100,98,107,91,102,
            97,90,93,104,106,408,108,92,99,94,
            103,117,95,408,119,124,120,118,408,408,
            133,115,134,408,123,125,116,131,121,408,
            132,158,122,408,160,114,159,408,408,161,
            183,143,141,182,408,162,140,142,157,139,
            408,408,408,153,151,146,145,136,150,407,
            408,149,152,130,127,432,129,408,408,137,
            408,128,186,155,144,156,408,408,112,173,
            408,167,154,185,408,148,408,172,111,168,
            170,459,147,408,408,171,408,408,177,174,
            408,408,175,408,189,190,192,408,197,198,
            196,408,408,202,408,113,476,199,176,440,
            484,408,408,194,408,216,214,408,188,218,
            215,408,223,408,226,408,110,219,213,408,
            408,225,251,169,408,229,408,222,261,266,
            228,408,260,273,408,265,408,408,250,319,
            318,274,486,408,126,135,408,408,408,408,
            164,165,408,408,138,408,408,163,166,408,
            179,408,408,408,408,180,178,187,408,184,
            191,408,193,195,408,408,201,181,408,408,
            200,203,408,408,205,408,204,206,408,13,
            207,408,408,208,408,209,408,217,212,408,
            210,220,408,221,211,408,408,408,224,227,
            408,464,408,231,408,232,230,408,478,408,
            408,408,408,408,494,234,233,408,408,458,
            236,235,408,408,408,408,460,237,408,408,
            489,238,408,408,454,241,239,408,242,240,
            408,490,408,408,244,408,408,246,408,243,
            408,245,408,408,247,248,249,408,408,408,
            408,482,255,408,408,408,257,28,258,256,
            408,408,252,254,253,408,262,259,431,22,
            263,267,408,408,438,264,408,268,408,423,
            408,408,408,408,270,408,408,408,271,408,
            424,408,275,277,278,416,269,276,408,414,
            408,272,408,413,408,279,408,408,408,281,
            280,283,408,408,408,286,408,408,285,287,
            408,284,408,408,282,408,462,408,493,288,
            290,408,408,291,408,295,289,408,294,408,
            408,408,292,408,408,408,296,451,307,298,
            297,299,408,293,448,408,408,301,408,408,
            303,300,408,304,408,408,408,302,305,306,
            33,408,408,308,408,479,408,311,310,408,
            313,408,312,408,408,408,309,314,315,408,
            408,429,408,316,317,408,426,408,408,322,
            408,320,408,321,408,325,418,408,408,323,
            408,324,408,408,326,408,408,417,468,408,
            415,408,408,408,327,408,469,408,328,463,
            408,412,408,330,408,492,331,480,408,408,
            408,329,332,47,452,408,408,408,333,334,
            335,408,336,456,408,408,337,408,449,408,
            338,408,447,408,408,339,408,408,408,342,
            446,341,340,408,408,343,344,408,439,408,
            408,408,434,408,347,408,477,346,408,345,
            349,19,408,408,408,350,408,473,348,408,
            474,408,422,352,408,358,408,351,408,353,
            354,408,356,355,408,408,408,357,408,408,
            410,408,467,360,359,408,481,408,466,408,
            361,408,408,362,408,363,408,408,408,365,
            367,408,408,368,366,364,408,443,408,442,
            408,408,408,408,475,370,371,369,408,372,
            408,373,408,408,408,420,472,408,374,408,
            408,408,425,428,408,408,411,376,408,461,
            375,377,408,408,470,408,408,378,408,408,
            380,408,379,408,383,381,382,408,408,485,
            408,408,386,408,450,408,445,385,387,408,
            419,388,408,408,408,409,457,408,384,408,
            390,408,488,408,408,408,392,389,391,408,
            437,408,408,408,393,395,397,408,444,394,
            408,491,396,408,408,408,408,408,487,408,
            399,408,400,408,408,402,408,433,408,435,
            408,401,408,408,471,404,403,408,398,483,
            405,408,408,408,453,465
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
           NUM_STATES        = 318,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 494,
           MAX_LA            = 1,
           NUM_RULES         = 86,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 87,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 26,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 407,
           ERROR_ACTION      = 408;

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
