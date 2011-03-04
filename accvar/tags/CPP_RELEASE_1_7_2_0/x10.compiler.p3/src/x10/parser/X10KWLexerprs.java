
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

public class X10KWLexerprs implements lpg.runtime.ParseTable, X10KWLexersym {
    public final static int ERROR_SYMBOL = 0;
    public final int getErrorSymbol() { return ERROR_SYMBOL; }

    public final static int SCOPE_UBOUND = 0;
    public final int getScopeUbound() { return SCOPE_UBOUND; }

    public final static int SCOPE_SIZE = 0;
    public final int getScopeSize() { return SCOPE_SIZE; }

    public final static int MAX_NAME_LENGTH = 0;
    public final int getMaxNameLength() { return MAX_NAME_LENGTH; }

    public final static int NUM_STATES = 253;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 409;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 1;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 76;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 2;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 29;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 77;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 26;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 332;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 333;
    public final int getErrorAction() { return ERROR_ACTION; }

    public final static boolean BACKTRACK = false;
    public final boolean getBacktrack() { return BACKTRACK; }

    public final int getStartSymbol() { return lhs(0); }
    public final boolean isValidForParser() { return X10KWLexersym.isValidForParser; }


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
            5,4,2,8,3,3,3,2,2,3,
            2,10,6,6,6,5,7,6,6,7,
            6,4,5,4,11,3,2,4,4,10,
            6,5,4,8,6,5,4,5,5,5,
            8,7,2,4,7,5,5,7,3,4,
            2,10,6,10,9,6,3,4,7,7,
            9,6,6,6,8,5,6,12,4,5,
            6,9,4,3,8,5
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
            1,1,1,1,1,1,1,1,79,44,
            132,129,109,141,144,145,28,147,42,59,
            63,24,54,86,90,91,73,43,46,148,
            152,157,94,155,160,159,96,81,162,163,
            168,171,172,164,176,173,105,108,177,181,
            182,30,110,185,183,187,78,188,190,191,
            114,193,194,199,49,68,119,195,203,202,
            121,125,207,208,211,210,215,220,219,221,
            224,226,227,231,232,233,98,235,237,126,
            239,245,240,247,248,249,254,256,257,258,
            260,261,263,131,265,268,271,274,276,272,
            278,282,277,284,18,285,287,289,290,291,
            293,297,299,301,302,307,308,309,310,313,
            315,316,317,320,325,324,330,331,139,332,
            333,334,338,339,342,344,343,345,351,352,
            355,357,362,360,365,366,367,370,372,373,
            374,375,379,384,382,387,389,388,391,395,
            394,396,397,405,407,402,409,412,413,414,
            415,416,418,420,424,422,427,431,432,433,
            435,438,440,442,441,443,444,447,445,454,
            457,459,460,461,464,465,469,472,473,475,
            21,476,477,479,480,482,483,490,493,492,
            498,499,500,506,508,501,510,513,511,515,
            517,519,521,520,522,525,523,531,536,538,
            527,533,541,542,545,546,552,547,551,555,
            556,559,558,562,563,564,566,567,570,572,
            575,333,333
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,16,0,18,2,
            0,21,22,0,1,2,3,0,1,0,
            3,14,3,4,11,12,9,17,11,16,
            17,0,0,0,3,0,4,2,0,8,
            9,9,11,0,12,10,8,9,0,17,
            7,16,0,18,6,3,13,0,6,26,
            3,9,0,20,0,3,4,0,11,2,
            0,23,5,11,17,0,1,10,3,0,
            0,1,3,0,1,0,16,0,9,9,
            3,8,7,23,0,8,2,0,0,0,
            3,16,4,0,10,6,9,4,0,6,
            0,1,4,0,0,0,8,2,0,9,
            0,0,1,24,10,10,6,9,0,9,
            0,17,4,0,0,7,0,0,4,2,
            7,0,12,2,0,9,0,1,0,0,
            2,0,0,0,10,6,5,0,6,2,
            0,0,0,10,4,0,0,2,7,7,
            0,0,0,7,0,3,0,0,8,0,
            0,4,0,0,0,14,6,13,0,10,
            14,0,0,5,3,13,0,0,14,0,
            0,18,10,3,0,6,9,11,0,0,
            0,7,3,0,6,0,0,2,8,3,
            0,0,0,1,0,1,0,1,0,0,
            10,18,11,5,0,1,0,0,0,10,
            2,5,5,0,1,0,0,0,2,0,
            0,1,0,8,0,1,4,0,11,2,
            0,0,13,0,1,0,0,0,3,9,
            3,0,11,0,0,1,0,6,0,0,
            0,1,0,7,2,19,0,9,0,10,
            0,0,1,3,8,22,0,0,0,0,
            1,4,0,7,0,0,0,0,20,0,
            1,6,6,0,0,13,3,19,4,0,
            0,0,0,0,2,6,22,0,0,8,
            10,0,0,0,0,12,4,10,5,5,
            0,0,1,12,0,1,0,19,8,0,
            4,0,1,4,0,0,0,3,2,0,
            1,0,0,0,0,0,3,3,0,1,
            8,0,11,0,1,20,0,0,0,2,
            0,10,4,0,0,0,0,4,8,5,
            5,0,16,7,0,1,0,1,0,1,
            9,0,0,0,0,0,5,0,6,0,
            1,0,7,0,7,12,0,4,2,15,
            0,0,0,12,0,5,2,0,7,0,
            0,0,0,0,0,0,0,15,7,6,
            8,5,13,0,14,2,0,20,0,0,
            0,5,4,0,0,21,3,8,0,5,
            10,0,0,2,0,0,0,5,0,0,
            12,0,0,0,2,10,12,11,9,0,
            1,0,0,15,13,3,5,0,0,0,
            0,2,2,6,6,0,1,0,1,0,
            0,2,0,1,0,5,0,1,0,0,
            0,0,0,1,0,7,0,8,7,5,
            0,1,0,19,14,0,1,0,1,7,
            0,0,2,17,0,0,0,1,3,8,
            0,0,8,2,0,0,2,0,0,9,
            2,0,0,0,7,0,0,1,6,0,
            15,0,1,10,0,0,0,0,13,0,
            0,0,0,0,0,0,25,0,0,15,
            21,0,0,0,0,0,0,0,0,0,
            0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            333,89,97,98,80,90,87,86,92,85,
            91,84,93,88,96,95,83,333,82,232,
            333,81,94,333,122,121,123,333,107,333,
            105,233,168,167,120,124,106,381,104,119,
            118,333,333,333,109,333,135,140,333,112,
            111,342,110,333,136,141,183,184,333,137,
            126,139,333,138,113,116,384,333,117,332,
            186,115,333,125,333,133,134,11,185,174,
            333,114,176,132,407,333,127,175,128,333,
            333,131,130,333,146,333,390,333,129,376,
            206,145,150,151,333,207,160,333,333,333,
            162,359,101,333,161,170,163,340,333,179,
            3,193,187,333,8,333,188,209,333,192,
            333,333,99,169,194,210,222,100,333,221,
            333,195,251,333,333,250,333,333,360,142,
            103,333,102,143,333,108,333,144,333,333,
            149,333,333,333,147,148,152,333,153,154,
            333,333,333,157,155,333,333,158,156,159,
            333,333,333,164,333,172,333,333,165,333,
            333,177,333,333,333,166,178,171,333,343,
            173,333,333,182,191,180,333,333,189,333,
            333,181,190,197,333,198,383,196,333,333,
            333,366,199,333,391,333,333,357,200,202,
            333,333,49,205,333,377,333,208,333,333,
            203,201,204,211,333,370,333,333,333,212,
            215,213,214,333,216,333,333,333,218,333,
            333,361,333,217,333,223,220,333,219,224,
            333,6,362,333,355,333,333,5,226,225,
            228,333,227,333,333,406,333,229,333,333,
            333,335,333,234,236,230,333,235,333,402,
            333,333,240,239,237,231,333,333,333,333,
            409,242,333,241,333,333,333,333,238,333,
            379,245,356,333,333,243,247,369,246,333,
            333,333,333,333,373,248,244,333,333,252,
            249,333,333,333,333,371,399,372,255,256,
            333,333,258,254,333,259,333,253,257,333,
            260,333,261,262,333,333,333,263,265,333,
            365,333,333,333,333,333,268,269,333,270,
            267,333,266,333,271,264,333,333,333,349,
            333,272,274,333,333,333,333,277,275,276,
            334,333,273,396,333,364,333,389,333,354,
            278,333,47,333,333,333,279,333,280,333,
            283,333,351,333,282,352,333,284,285,281,
            333,333,333,400,333,397,386,333,286,333,
            333,333,333,333,333,333,333,346,289,292,
            291,395,288,333,290,294,333,287,333,333,
            70,295,296,333,333,293,298,297,333,348,
            404,333,333,368,333,333,333,299,333,333,
            347,333,333,333,303,378,353,300,301,333,
            304,333,333,350,302,305,306,333,333,333,
            333,375,309,307,308,333,392,333,393,333,
            333,310,333,311,333,312,333,374,333,333,
            333,333,333,318,333,314,333,315,316,317,
            333,319,333,313,398,333,408,333,320,321,
            333,333,367,337,333,333,333,388,324,322,
            333,333,323,325,333,333,327,333,333,326,
            405,333,333,333,328,333,333,345,363,333,
            394,333,330,385,333,333,333,333,387,333,
            333,333,333,333,333,333,329,333,333,401,
            358
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
    public final int originalState(int state) { return 0; }
    public final int asi(int state) { return 0; }
    public final int nasi(int state) { return 0; }
    public final int inSymbol(int state) { return 0; }

    /**
     * assert(! goto_default);
     */
    public final int ntAction(int state, int sym) {
        return baseAction[state + sym];
    }

    /**
     * assert(! shift_default);
     */
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
