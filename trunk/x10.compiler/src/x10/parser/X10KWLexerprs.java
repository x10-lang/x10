
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
            3,4,8,5,5,13,6,6,5,5,
            7,12,7,6,6,7,3,6,4,5,
            6,7,4,11,3,8,2,10,9,4,
            4,10,6,5,4
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
            1,1,1,1,1,1,1,88,138,144,
            52,86,170,161,29,88,24,174,63,70,
            102,72,159,30,57,90,42,76,49,172,
            176,177,180,95,181,99,44,22,101,183,
            184,188,187,106,190,111,191,192,107,193,
            114,195,117,201,121,120,202,204,127,208,
            209,210,212,211,218,221,219,222,226,118,
            131,224,141,223,233,137,236,146,238,142,
            237,241,242,243,248,246,250,252,253,257,
            258,260,261,262,264,266,267,270,274,280,
            273,283,278,286,288,289,290,148,291,293,
            292,297,298,295,302,303,308,310,75,311,
            306,315,318,153,320,324,48,326,322,328,
            329,332,155,333,334,336,338,346,348,351,
            352,339,355,341,356,357,360,361,367,363,
            368,370,371,373,375,379,380,381,384,385,
            387,388,391,401,398,402,405,407,409,411,
            394,414,386,415,413,419,420,421,422,429,
            425,433,432,427,438,439,163,441,443,446,
            444,448,449,450,452,455,456,458,459,461,
            464,468,465,469,474,478,481,466,479,483,
            485,486,490,491,488,499,498,502,504,506,
            507,511,508,516,512,513,519,522,520,524,
            525,526,530,528,536,537,539,538,545,547,
            549,553,550,558,560,561,562,556,540,564,
            569,571,572,576,577,566,581,580,582,587,
            590,592,594,595,597,596,604,598,602,606,
            607,611,614,608,612,617,621,620,624,628,
            629,630,631,632,634,636,642,637,644,640,
            650,648,654,655,646,657,661,664,665,668,
            670,666,672,675,671,682,683,686,687,677,
            688,689,693,695,696,699,700,678,703,704,
            708,710,714,715,406,406
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
            20,0,22,0,1,2,3,6,0,0,
            9,3,3,4,11,12,8,8,9,16,
            11,0,19,0,3,4,3,0,0,8,
            2,0,11,12,11,7,0,6,10,16,
            4,5,0,15,17,3,4,19,6,0,
            1,0,3,11,0,0,5,8,4,23,
            11,6,8,8,13,0,1,0,17,0,
            1,16,0,6,0,1,11,8,0,12,
            0,0,4,9,4,0,0,9,3,8,
            0,6,2,0,8,9,0,0,5,0,
            0,2,5,13,24,5,0,7,2,10,
            0,5,19,3,17,19,0,0,8,23,
            0,0,2,0,8,0,1,0,5,2,
            10,10,0,8,0,3,2,16,0,23,
            0,9,0,26,10,18,8,5,6,0,
            1,0,12,0,1,0,0,2,2,0,
            0,10,0,0,4,6,0,0,5,0,
            0,0,0,3,0,13,9,8,4,7,
            0,0,2,0,18,2,15,0,0,0,
            0,0,2,6,5,4,8,0,0,18,
            0,0,0,0,2,0,3,0,11,11,
            10,6,0,1,13,0,0,0,3,2,
            0,0,0,3,3,0,10,0,1,0,
            5,0,0,4,12,3,0,0,1,0,
            0,0,11,0,5,0,0,1,8,0,
            14,10,0,0,11,2,7,0,13,0,
            1,9,0,6,2,0,1,0,0,0,
            0,0,0,4,0,7,0,0,11,2,
            4,0,0,9,3,0,15,0,1,0,
            0,2,22,21,0,1,11,0,8,0,
            3,0,20,0,1,0,1,0,0,10,
            3,0,0,0,6,0,15,0,0,7,
            0,6,4,10,7,0,1,0,1,18,
            0,0,1,3,0,0,0,17,3,0,
            0,1,0,9,5,9,0,0,2,0,
            0,1,0,1,0,13,2,8,0,0,
            0,1,15,0,0,0,0,0,10,4,
            0,7,6,0,7,12,3,0,19,9,
            0,0,2,6,0,1,0,6,0,1,
            0,1,0,0,0,9,3,3,0,0,
            0,0,4,3,0,6,0,6,0,1,
            6,0,0,2,22,3,10,0,0,1,
            0,4,0,0,4,0,1,0,0,0,
            2,0,9,11,0,0,9,0,0,1,
            0,10,2,0,0,0,12,0,0,4,
            21,14,9,0,7,7,21,0,0,6,
            0,1,0,6,0,0,22,0,0,0,
            0,9,14,9,5,10,6,0,0,12,
            2,0,1,0,7,0,0,0,5,4,
            0,0,0,7,7,0,6,2,0,0,
            8,0,1,0,0,0,2,0,5,0,
            1,20,13,15,7,0,0,0,0,0,
            4,2,17,5,0,1,0,12,0,0,
            4,14,0,5,5,0,4,0,1,0,
            0,0,3,0,9,0,3,7,0,1,
            0,0,1,12,4,0,0,2,13,0,
            0,0,0,2,8,5,0,8,2,0,
            1,0,1,0,0,0,0,0,4,3,
            7,0,7,0,1,0,0,0,2,12,
            0,0,2,0,1,10,0,16,11,0,
            0,5,2,0,1,14,7,0,0,0,
            0,0,5,0,5,0,0,9,7,0,
            7,0,1,0,1,0,7,0,18,0,
            1,16,5,0,0,1,0,21,2,6,
            0,16,2,0,0,0,3,0,1,0,
            0,0,3,9,0,1,0,0,8,14,
            9,0,0,2,2,0,0,0,0,4,
            4,4,0,5,0,0,1,20,0,0,
            1,25,0,0,10,13,8,0,0,0,
            7,2,10,0,0,0,3,0,4,0,
            0,14,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            406,104,95,108,100,99,97,106,90,101,
            96,89,92,103,105,107,406,91,98,93,
            102,406,94,406,118,123,119,172,406,406,
            173,114,142,140,122,124,115,139,141,120,
            138,406,121,406,152,150,171,406,406,149,
            157,406,148,151,170,158,406,483,159,457,
            145,144,406,160,422,129,126,156,128,406,
            132,406,133,127,406,406,135,130,259,143,
            131,154,258,155,430,406,111,406,136,406,
            147,153,406,116,406,165,110,146,406,117,
            406,406,168,166,175,406,406,169,181,134,
            406,180,184,406,187,188,406,406,190,406,
            406,196,212,183,174,195,406,194,200,197,
            406,473,481,214,211,438,406,406,213,192,
            406,406,216,406,221,406,224,406,109,249,
            217,227,406,223,406,264,271,226,406,220,
            406,263,406,405,272,248,137,317,316,406,
            112,406,113,406,125,406,406,162,163,406,
            406,161,406,406,167,164,406,406,177,406,
            406,406,406,185,406,176,178,182,191,189,
            406,406,193,406,179,199,186,406,406,406,
            406,406,205,201,203,204,202,406,13,198,
            406,406,406,406,218,406,215,406,206,208,
            207,210,406,219,209,406,406,406,222,225,
            406,406,406,229,230,406,228,406,475,406,
            491,406,406,232,231,234,406,406,456,406,
            406,406,233,406,235,406,406,486,236,406,
            458,452,406,406,237,239,238,406,487,406,
            240,241,406,243,242,406,244,406,406,406,
            406,406,406,247,406,246,406,406,245,479,
            253,406,406,254,255,406,252,28,256,406,
            406,257,250,251,22,261,260,406,429,406,
            262,406,436,406,266,406,421,406,406,265,
            268,406,406,406,269,406,267,406,406,273,
            406,414,278,274,275,406,412,406,411,270,
            406,406,277,276,406,406,406,280,281,406,
            406,284,406,279,283,282,406,406,285,406,
            406,460,406,490,406,286,289,288,406,406,
            406,292,287,406,406,406,406,406,290,305,
            406,294,449,406,295,293,303,406,291,296,
            406,406,446,297,406,299,406,298,406,301,
            406,302,406,406,33,300,304,306,406,406,
            406,406,476,309,406,308,406,310,406,311,
            312,406,406,427,307,313,314,406,406,424,
            406,315,406,406,318,406,320,406,406,406,
            416,406,321,319,406,406,322,406,406,324,
            406,415,465,406,406,406,413,406,406,330,
            323,466,325,406,326,461,410,406,406,328,
            406,489,406,329,406,47,327,406,406,406,
            406,331,477,332,333,454,334,406,406,450,
            335,406,336,406,447,406,406,406,445,337,
            406,406,406,444,339,406,338,340,406,406,
            342,406,437,406,406,406,432,406,345,406,
            474,341,344,343,347,406,19,406,406,406,
            348,408,346,470,406,420,406,471,406,406,
            350,349,406,351,353,406,352,406,354,406,
            406,406,355,406,356,406,357,464,406,478,
            406,406,359,463,358,406,406,360,362,406,
            406,406,406,365,361,363,406,364,366,406,
            441,406,440,406,406,406,406,406,368,369,
            367,406,370,406,371,406,406,406,418,472,
            406,406,469,406,372,423,406,426,373,406,
            406,409,374,406,459,467,375,406,406,406,
            406,406,376,406,378,406,406,377,379,406,
            380,406,381,406,482,406,383,406,448,406,
            384,443,385,406,406,417,406,382,407,386,
            406,387,455,406,406,406,388,406,485,406,
            406,406,390,389,406,435,406,406,392,442,
            391,406,406,393,394,406,406,406,406,395,
            488,484,406,397,406,406,398,480,406,406,
            400,396,406,406,431,433,399,406,406,406,
            402,468,401,406,406,406,403,406,462,406,
            406,451
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
           NUM_STATES        = 317,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 491,
           MAX_LA            = 1,
           NUM_RULES         = 85,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 86,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 26,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 405,
           ERROR_ACTION      = 406;

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
