
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
            5,4,8,3,3,3,2,2,3,2,
            6,6,5,7,6,6,7,6,4,5,
            4,11,3,2,4,4,10,6,5,4,
            8,6,5,4,5,5,5,8,7,2,
            4,7,5,5,7,3,4,2,10,6,
            10,9,6,3,4,7,7,9,6,6,
            6,8,5,6,12,4,5,6,9,4,
            3,8,5
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
            1,1,1,1,1,76,29,93,66,127,
            102,139,24,144,31,146,43,36,62,69,
            74,68,79,17,47,49,148,151,154,84,
            152,158,157,159,83,87,160,163,165,92,
            82,168,169,176,170,179,180,182,181,99,
            100,186,104,187,188,190,109,191,193,195,
            112,45,116,194,196,199,124,120,204,206,
            207,209,214,219,220,221,212,224,225,228,
            229,232,230,235,234,243,237,244,236,248,
            30,250,254,126,256,257,259,260,130,263,
            265,269,271,261,273,275,147,277,131,278,
            280,281,285,286,288,282,292,293,297,300,
            301,295,302,305,307,309,311,312,319,321,
            308,323,324,327,329,333,335,337,137,338,
            339,340,342,325,347,348,350,354,356,359,
            361,362,363,364,370,373,368,365,376,382,
            378,383,386,387,375,394,392,388,396,399,
            400,402,406,407,409,410,411,412,413,419,
            420,425,414,422,426,431,428,433,98,440,
            437,441,444,445,447,449,448,456,452,453,
            457,460,463,464,468,467,469,470,472,473,
            474,481,484,487,489,490,493,495,496,497,
            498,499,502,504,506,509,513,515,507,517,
            520,519,524,525,528,533,529,531,535,536,
            538,540,541,542,551,543,545,319,319
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,0,17,18,3,
            20,5,22,0,1,2,3,11,0,0,
            0,1,3,3,11,0,7,0,15,16,
            10,11,0,8,0,3,0,3,0,7,
            2,5,10,11,26,11,10,9,23,13,
            16,0,16,15,3,0,18,0,0,8,
            3,10,4,0,1,10,3,10,0,1,
            12,0,0,0,1,3,0,5,10,21,
            7,0,0,1,8,4,15,0,0,0,
            2,0,3,0,23,2,15,9,0,10,
            24,0,9,5,13,0,8,20,7,0,
            5,10,7,0,1,0,0,2,9,0,
            0,5,2,10,9,16,0,8,0,10,
            4,5,4,0,14,0,0,0,5,2,
            0,0,2,0,1,10,0,0,0,0,
            9,4,0,7,0,19,2,0,0,0,
            8,12,14,6,5,0,8,2,0,0,
            0,0,4,2,4,0,0,0,9,0,
            0,5,0,0,0,0,9,8,0,14,
            6,3,12,0,9,0,0,14,0,3,
            18,0,1,0,11,10,8,4,0,0,
            0,3,2,0,0,6,2,0,0,0,
            7,0,1,0,0,0,0,8,11,3,
            12,7,0,0,2,0,11,0,1,0,
            1,18,9,0,1,0,0,1,0,0,
            0,6,0,1,0,6,2,9,0,1,
            0,11,0,3,0,3,0,0,1,0,
            0,0,8,4,0,0,1,0,7,2,
            10,0,0,9,0,3,0,1,22,0,
            0,0,1,4,0,5,0,0,0,0,
            0,0,21,19,6,5,12,6,0,13,
            0,1,0,0,0,7,0,1,0,22,
            8,8,0,5,0,3,0,0,0,0,
            2,0,8,19,7,9,0,0,1,0,
            9,5,13,0,5,0,3,2,0,1,
            0,0,0,0,0,3,3,0,7,0,
            1,11,0,1,0,0,9,0,4,15,
            5,0,0,2,7,0,0,0,6,0,
            5,0,6,0,1,0,5,2,0,0,
            13,0,1,4,6,0,0,1,0,0,
            0,0,0,0,6,10,4,8,0,0,
            1,0,4,13,0,0,2,0,17,4,
            0,0,0,12,21,8,0,7,6,0,
            0,2,6,0,0,5,0,0,0,3,
            7,0,0,9,6,0,0,2,2,0,
            13,10,0,0,12,6,0,0,0,0,
            1,0,0,0,3,13,9,11,6,16,
            0,8,2,0,1,17,0,1,0,0,
            2,2,0,1,0,0,0,0,0,4,
            6,0,1,0,7,0,0,4,0,1,
            14,6,0,1,0,1,0,19,0,0,
            4,2,16,0,0,7,3,0,0,2,
            0,7,0,1,0,0,2,0,10,0,
            0,0,0,4,0,8,0,17,0,9,
            0,1,0,12,0,0,0,0,0,0,
            25,17,20,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            319,87,94,95,84,77,88,89,85,82,
            83,81,86,90,93,80,319,92,79,129,
            78,130,91,319,105,104,106,128,319,319,
            319,110,206,108,103,319,207,319,102,101,
            109,107,319,116,319,112,319,180,319,115,
            136,131,114,113,318,179,327,137,117,132,
            390,319,133,135,119,319,134,319,319,120,
            126,118,122,319,123,97,124,125,319,127,
            367,319,319,319,142,148,319,147,359,121,
            141,319,319,96,150,154,373,319,319,319,
            164,319,166,10,155,169,342,165,319,167,
            149,319,170,325,99,319,173,282,177,7,
            181,178,182,319,187,319,319,209,188,319,
            319,98,224,186,210,189,319,215,319,214,
            247,248,100,319,225,319,319,319,343,138,
            319,319,139,319,140,111,319,319,319,319,
            143,144,319,145,319,222,153,319,319,319,
            152,151,146,156,159,319,157,158,319,319,
            319,319,160,162,163,319,319,319,161,319,
            319,171,319,319,319,319,328,172,319,168,
            176,185,174,319,184,319,319,183,319,191,
            175,319,196,319,190,366,192,349,319,319,
            319,193,195,319,319,194,198,319,319,319,
            197,319,344,319,319,319,319,374,199,202,
            345,200,319,319,340,319,204,46,205,319,
            360,201,203,319,208,319,319,353,319,319,
            5,211,319,216,319,213,217,212,319,338,
            319,219,4,218,319,220,319,319,389,319,
            319,319,221,226,319,319,321,319,229,228,
            227,319,319,385,319,231,319,232,223,319,
            319,319,392,233,319,234,319,319,319,319,
            319,319,230,352,237,382,235,238,319,236,
            319,240,319,319,319,239,319,362,319,241,
            242,339,319,243,319,244,319,319,319,319,
            356,319,245,250,249,246,319,319,252,319,
            355,251,354,319,253,319,254,255,319,348,
            319,319,319,319,319,258,259,319,257,319,
            260,256,319,261,319,319,262,319,379,263,
            264,319,319,332,265,319,319,319,266,319,
            267,319,320,319,347,319,268,269,319,319,
            383,319,372,270,380,319,319,337,319,44,
            319,319,319,319,272,271,334,273,319,319,
            276,319,275,335,319,319,369,319,274,279,
            319,319,319,278,277,281,319,280,378,319,
            319,283,284,319,67,285,319,319,319,287,
            286,319,319,387,331,319,319,351,290,319,
            330,288,319,319,289,291,319,319,319,319,
            293,319,319,319,294,336,361,292,295,364,
            319,296,358,319,375,333,319,376,319,319,
            297,298,319,299,319,319,319,319,319,301,
            300,319,357,319,302,319,319,304,319,306,
            381,305,319,391,319,307,319,303,319,319,
            308,350,322,319,319,309,310,319,319,312,
            319,311,319,371,319,319,388,319,313,319,
            319,319,319,315,319,346,319,377,319,368,
            319,316,319,370,319,319,319,319,319,319,
            314,384,341
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
           NUM_STATES        = 242,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 392,
           MAX_LA            = 1,
           NUM_RULES         = 73,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 74,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 26,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 318,
           ERROR_ACTION      = 319;

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
