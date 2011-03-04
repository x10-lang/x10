
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

    public final static int NUM_STATES = 254;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 27;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 410;
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

    public final static int EOFT_SYMBOL = 25;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 28;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 333;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 334;
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
            5,4,2,8,3,3,3,2,3,2,
            10,6,6,6,5,7,6,6,7,6,
            4,5,4,11,3,2,4,4,10,6,
            5,4,8,6,5,4,5,5,5,8,
            7,2,4,7,5,5,7,3,4,2,
            10,6,10,9,6,3,4,8,7,7,
            9,6,6,6,8,5,6,9,4,5,
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
            1,1,1,1,1,1,1,1,79,136,
            142,144,145,146,101,60,18,147,31,69,
            40,24,44,70,77,78,48,54,30,148,
            156,151,81,155,164,160,167,84,85,168,
            170,171,174,172,177,180,179,89,95,184,
            33,103,90,183,187,186,55,192,191,197,
            109,193,194,201,111,65,118,198,202,204,
            119,94,206,209,211,215,216,220,218,224,
            226,227,232,233,229,228,240,125,242,244,
            124,247,248,250,252,251,255,257,261,260,
            264,265,268,127,269,273,267,278,235,274,
            280,281,282,117,139,288,284,286,290,291,
            295,294,300,301,305,303,307,308,309,313,
            315,314,316,320,323,327,329,330,333,131,
            334,319,337,338,340,342,343,344,348,354,
            356,358,363,361,366,359,368,371,367,374,
            377,381,382,383,387,389,375,391,392,393,
            345,396,399,401,402,405,408,404,411,413,
            415,416,417,418,419,422,424,430,431,425,
            426,439,435,436,440,443,445,447,444,446,
            454,458,455,459,461,464,467,469,465,471,
            474,475,477,479,480,481,483,486,485,493,
            500,489,495,502,504,509,507,513,515,516,
            519,521,522,491,523,525,527,529,530,533,
            535,540,531,542,544,545,546,550,553,547,
            557,551,563,559,564,566,567,569,570,571,
            572,574,334,334
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,16,0,1,19,
            3,21,22,0,1,2,3,10,11,0,
            0,2,0,3,11,5,13,8,15,0,
            10,11,3,0,15,6,14,0,19,10,
            3,4,9,0,0,12,2,4,11,0,
            17,7,8,4,0,0,13,3,0,0,
            1,18,3,14,6,11,0,0,1,3,
            0,1,18,0,0,5,10,10,0,0,
            2,23,9,0,0,6,8,3,15,15,
            0,8,0,0,10,3,4,23,0,9,
            0,18,4,24,6,5,0,0,0,1,
            10,4,5,0,0,2,0,3,10,5,
            0,8,6,17,4,0,10,21,0,9,
            2,0,1,0,0,0,0,0,4,2,
            0,1,14,10,0,0,10,2,13,0,
            25,0,8,0,1,6,0,0,2,0,
            0,0,2,0,7,6,0,4,0,0,
            9,2,0,0,8,0,0,9,5,3,
            0,0,0,0,12,4,0,0,8,14,
            0,0,6,0,12,0,3,7,0,8,
            0,14,19,3,0,0,11,0,10,0,
            6,4,3,0,9,0,0,0,0,6,
            5,0,0,2,0,3,8,3,11,0,
            1,0,1,0,1,19,0,0,1,0,
            0,0,2,7,0,1,0,8,7,0,
            0,5,2,0,0,1,0,0,0,1,
            11,4,0,0,2,12,10,0,1,0,
            0,0,3,0,11,0,6,0,1,0,
            0,1,9,0,0,10,2,8,5,0,
            0,20,0,3,0,1,0,0,0,1,
            4,9,0,0,0,0,17,3,0,0,
            2,6,0,1,12,6,0,20,0,0,
            4,3,0,0,21,6,0,0,5,0,
            8,0,0,0,0,8,4,0,7,13,
            7,7,5,0,1,0,1,0,0,20,
            0,4,0,1,4,0,0,0,3,2,
            0,1,0,0,0,17,0,11,5,3,
            0,0,0,1,3,5,0,1,0,15,
            0,0,0,2,4,0,8,5,0,4,
            0,0,1,0,0,7,2,0,1,9,
            0,1,0,10,0,0,0,0,0,7,
            6,0,1,0,0,0,9,9,13,0,
            0,2,16,9,0,0,13,7,0,0,
            2,16,0,0,0,0,0,12,9,5,
            7,17,6,0,0,2,14,0,0,2,
            0,7,4,0,0,5,0,22,0,3,
            0,8,2,0,0,7,0,13,0,0,
            0,7,0,10,0,0,2,8,0,13,
            0,11,0,1,0,7,18,12,16,0,
            6,0,3,0,0,2,0,6,0,1,
            20,5,0,1,0,0,2,2,0,1,
            0,0,0,1,0,4,0,7,0,0,
            0,5,0,1,0,1,7,9,14,0,
            1,0,1,0,0,0,0,2,2,0,
            0,21,0,9,5,3,0,1,0,1,
            10,18,0,0,2,0,0,2,0,0,
            0,0,1,0,6,9,0,8,0,16,
            0,0,12,0,0,0,0,0,0,0,
            0,0,0,0,0,22,0,0,0,0,
            0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            334,89,97,98,80,92,87,90,91,86,
            85,84,88,93,96,83,95,334,108,82,
            106,94,81,334,122,121,123,107,105,334,
            334,140,334,110,120,113,124,141,119,334,
            112,111,117,334,139,118,166,334,138,116,
            133,134,126,334,10,384,174,135,132,334,
            125,176,175,360,334,334,136,186,334,334,
            127,137,128,104,114,185,334,334,131,130,
            334,146,408,334,334,145,129,376,334,334,
            161,115,151,8,334,170,162,163,359,390,
            334,194,334,334,164,168,167,152,334,103,
            334,195,341,169,179,183,334,334,3,193,
            184,187,188,334,334,210,334,207,192,208,
            334,211,222,231,253,334,221,232,334,252,
            233,334,99,334,334,334,334,334,101,142,
            334,144,234,100,334,334,109,143,102,334,
            333,334,147,334,148,149,334,334,150,334,
            334,334,155,334,153,154,334,156,334,334,
            157,159,334,334,158,334,334,160,165,172,
            334,334,334,334,171,177,334,334,343,173,
            334,334,178,334,180,334,191,182,334,190,
            334,189,181,197,334,334,196,334,383,334,
            198,200,199,334,366,334,334,334,334,391,
            201,334,334,357,334,203,204,226,205,48,
            206,334,377,334,209,202,334,334,370,334,
            334,334,215,212,334,216,334,213,214,334,
            334,217,218,334,334,361,334,334,334,223,
            219,220,334,6,224,362,225,334,355,5,
            334,334,228,334,227,334,229,334,407,334,
            334,336,235,334,334,236,237,403,238,334,
            334,230,334,240,334,241,334,334,334,410,
            243,242,334,334,334,334,239,245,334,334,
            373,247,334,379,244,356,334,369,334,334,
            248,249,334,334,246,250,334,334,254,334,
            251,334,334,334,334,372,400,334,256,371,
            257,278,258,334,259,334,260,334,334,255,
            334,261,334,262,263,334,334,334,264,266,
            334,365,334,334,334,265,334,267,268,269,
            334,334,334,272,271,270,334,273,334,275,
            334,334,334,349,276,334,274,277,334,279,
            334,334,364,334,334,335,280,334,389,397,
            334,354,334,281,46,334,334,334,334,282,
            283,334,286,334,334,334,351,285,352,334,
            334,287,284,288,334,334,401,398,334,334,
            386,346,334,334,334,334,334,290,291,293,
            396,289,294,334,334,296,292,334,334,297,
            334,298,299,70,334,300,334,295,334,301,
            334,405,368,334,334,348,334,347,334,334,
            334,303,334,302,334,334,306,378,334,353,
            334,304,334,307,334,309,381,305,350,334,
            310,334,308,334,334,375,334,311,334,393,
            317,312,334,394,334,334,313,314,334,315,
            334,334,334,374,334,392,334,316,334,334,
            334,318,334,321,334,322,320,319,399,334,
            409,334,324,334,334,334,334,367,328,334,
            334,323,334,325,326,327,334,388,334,402,
            329,338,334,334,330,334,334,406,334,334,
            334,334,345,334,363,331,334,385,334,395,
            334,334,387,334,334,334,334,334,334,334,
            334,334,334,334,334,358
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
