

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
            4,5,7,6,4,5,11,4,3,8,
            6,2,5,10,4,5,4,6,9,6,
            6,6,4,11,5,4
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
            1,1,1,1,1,1,1,1,89,123,
            41,167,169,153,98,97,22,75,47,56,
            171,82,172,26,87,61,40,91,64,177,
            81,31,103,173,107,25,106,180,181,12,
            182,117,185,184,186,71,187,118,68,192,
            194,196,108,90,122,128,195,119,198,199,
            204,205,203,211,206,215,216,218,219,221,
            223,222,132,224,137,232,236,238,240,140,
            142,239,241,233,246,248,250,251,254,252,
            258,253,145,261,263,264,266,272,54,274,
            267,277,134,279,280,278,282,285,283,286,
            294,291,295,297,298,299,301,302,305,307,
            150,310,311,158,312,317,318,323,321,325,
            327,329,330,332,333,164,337,334,340,338,
            349,351,353,354,355,347,358,359,362,367,
            368,370,371,375,377,378,380,381,382,384,
            385,386,387,390,157,398,400,395,401,402,
            407,405,411,413,415,417,409,419,421,423,
            422,427,424,433,434,437,438,440,442,443,
            446,445,447,450,453,336,454,457,458,460,
            463,462,467,468,466,470,475,476,477,481,
            480,482,483,484,495,494,498,500,501,503,
            506,509,511,510,513,512,519,520,521,523,
            524,525,526,528,533,534,530,532,545,542,
            547,549,550,485,552,553,558,559,555,563,
            566,564,568,571,573,574,575,581,583,586,
            576,588,589,587,591,594,595,596,600,597,
            601,605,613,603,615,617,619,620,606,621,
            628,629,631,634,635,637,638,640,643,642,
            646,650,651,652,654,609,658,661,662,666,
            667,669,656,671,673,674,676,675,682,678,
            686,683,691,395,395
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
            9,0,22,2,0,0,5,3,3,4,
            0,6,11,12,9,11,11,16,8,0,
            0,20,3,4,20,6,0,7,8,3,
            11,12,6,0,8,0,1,11,3,4,
            0,1,9,0,4,2,11,0,8,9,
            0,4,2,10,0,1,9,3,15,16,
            0,0,2,13,14,11,0,17,7,0,
            0,2,6,7,4,14,0,0,8,18,
            3,4,0,1,8,0,0,0,12,23,
            20,9,6,8,9,9,0,0,0,3,
            2,0,0,16,8,8,5,0,7,2,
            23,0,15,0,3,4,0,10,2,0,
            1,0,0,4,0,1,10,25,4,0,
            17,10,0,4,21,6,0,0,1,3,
            4,20,5,0,12,2,0,1,0,1,
            0,0,0,10,4,4,0,1,6,0,
            0,0,3,0,0,0,0,7,4,3,
            7,0,7,0,0,0,5,0,0,2,
            19,6,0,0,0,0,8,4,6,16,
            0,7,2,19,0,0,11,0,0,4,
            0,0,0,0,10,0,3,10,7,11,
            8,0,0,2,14,0,1,0,0,0,
            0,4,3,3,12,0,1,0,10,0,
            0,0,0,0,7,6,3,0,1,7,
            0,11,0,0,13,0,0,2,5,7,
            10,0,1,0,8,2,0,0,0,0,
            1,0,0,1,0,0,10,9,11,5,
            0,6,11,0,0,2,0,0,0,2,
            0,0,6,3,0,1,0,9,2,0,
            0,0,22,4,3,21,0,0,17,3,
            0,11,0,1,0,1,0,10,0,0,
            2,0,0,0,3,0,0,0,18,0,
            8,5,5,10,15,19,0,8,0,1,
            0,1,0,0,0,3,21,0,0,6,
            3,0,1,9,18,7,0,0,2,0,
            0,1,5,4,0,1,0,0,2,0,
            0,0,3,0,0,0,0,10,5,0,
            5,2,8,12,0,9,16,0,1,0,
            0,0,8,4,0,5,0,1,0,8,
            0,1,0,9,0,3,0,3,0,3,
            0,0,0,0,6,3,0,1,8,8,
            22,8,0,0,1,3,0,0,2,0,
            1,0,0,6,0,0,0,1,7,0,
            8,6,0,0,2,11,0,0,9,0,
            1,0,0,10,2,0,0,0,12,0,
            9,5,5,8,0,0,0,8,21,0,
            0,0,0,0,0,9,12,3,13,9,
            8,10,13,0,0,12,2,0,5,0,
            0,2,0,1,7,0,1,7,0,0,
            0,0,0,2,5,7,6,5,0,0,
            0,1,0,0,0,0,2,0,1,0,
            7,0,0,0,15,17,14,5,0,6,
            9,0,13,18,0,1,0,6,0,0,
            1,0,0,7,0,7,5,0,0,2,
            6,3,0,0,12,0,1,0,6,6,
            0,4,0,0,0,0,2,4,3,7,
            0,1,0,1,14,0,0,0,0,1,
            0,6,5,0,0,0,0,2,12,0,
            0,2,0,10,0,0,4,11,0,15,
            20,7,0,13,0,1,0,5,0,0,
            0,13,4,7,19,5,7,0,0,1,
            0,1,5,0,0,1,0,0,5,0,
            1,0,0,7,2,0,5,2,11,0,
            0,0,1,0,5,0,3,0,1,9,
            0,0,0,2,4,0,0,2,0,3,
            0,3,0,0,0,0,6,0,1,24,
            6,0,0,10,9,0,14,6,0,4,
            0,0,0,0,0,13,0,7,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            395,104,95,108,90,106,100,99,97,101,
            96,395,92,105,103,107,93,102,91,98,
            176,395,94,121,395,395,117,171,142,139,
            395,140,120,122,141,170,138,119,164,395,
            395,118,154,151,445,152,395,109,467,129,
            150,153,126,395,128,395,132,127,133,130,
            395,149,241,395,148,159,131,395,146,147,
            395,190,186,160,395,125,191,124,161,158,
            395,395,163,185,184,123,395,183,135,395,
            395,195,145,144,157,419,395,395,156,136,
            113,114,395,165,115,395,395,395,116,143,
            155,166,168,172,173,169,395,395,395,179,
            201,395,395,427,178,188,196,395,197,198,
            194,395,189,395,217,216,395,199,219,395,
            226,395,395,225,395,236,220,394,237,395,
            245,228,395,260,472,261,395,22,263,297,
            296,227,264,395,112,273,395,110,395,111,
            395,395,395,274,134,137,395,162,167,395,
            395,395,174,395,395,395,395,175,180,187,
            181,395,182,395,395,395,192,395,395,202,
            177,193,395,395,395,395,203,204,206,464,
            395,205,207,200,395,395,208,395,13,210,
            395,395,395,395,209,395,218,211,215,212,
            214,395,395,221,213,395,222,395,395,395,
            395,223,224,230,231,395,460,395,229,395,
            395,395,395,395,481,232,234,395,444,235,
            395,233,395,395,446,395,395,239,238,478,
            440,395,240,395,243,242,395,395,395,395,
            247,395,395,470,395,395,244,246,248,250,
            395,251,249,395,395,252,395,395,395,463,
            395,395,255,257,28,258,395,256,259,395,
            395,395,253,418,265,254,395,395,425,266,
            395,262,395,268,395,410,395,267,395,395,
            269,395,395,395,271,395,395,395,411,395,
            272,275,277,276,270,456,395,403,395,401,
            395,400,395,395,395,278,322,395,395,279,
            282,395,284,280,281,283,395,395,285,395,
            395,448,286,287,395,480,395,395,288,395,
            395,395,290,395,395,395,395,289,293,395,
            294,434,437,292,395,295,291,395,471,395,
            395,395,299,298,395,300,395,302,395,301,
            395,304,395,303,395,305,33,306,395,307,
            395,395,395,395,461,310,395,312,309,311,
            308,313,395,395,457,314,395,395,416,395,
            413,395,395,315,395,395,395,320,316,395,
            317,318,395,395,405,319,395,395,321,395,
            323,395,395,404,452,395,395,395,402,395,
            324,325,449,326,395,395,395,327,399,395,
            395,46,395,395,395,329,466,351,328,330,
            332,442,331,395,395,438,333,395,435,395,
            395,476,395,334,477,395,473,475,395,395,
            73,395,395,337,432,433,335,336,395,395,
            395,426,395,395,395,395,421,395,459,395,
            341,395,395,19,339,338,340,343,395,344,
            346,395,345,342,395,409,395,347,395,395,
            350,395,395,348,395,349,451,395,395,397,
            353,352,395,395,450,395,355,395,354,356,
            395,357,395,395,395,395,361,360,363,359,
            395,430,395,429,358,395,395,395,395,365,
            395,362,364,395,395,395,395,407,458,395,
            395,454,395,412,395,395,369,367,395,366,
            415,398,395,453,395,447,395,368,395,395,
            395,431,371,370,436,373,372,395,395,375,
            395,465,374,395,395,377,395,395,376,395,
            406,395,395,378,396,395,380,443,379,395,
            395,395,474,395,381,395,383,395,424,382,
            395,395,395,385,384,395,395,386,395,387,
            395,388,395,395,395,395,469,395,391,389,
            462,395,395,420,390,395,422,479,395,392,
            395,395,395,395,395,439,395,455
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
           NUM_STATES        = 305,
           NT_OFFSET         = 27,
           LA_STATE_OFFSET   = 481,
           MAX_LA            = 1,
           NUM_RULES         = 86,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 29,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 87,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 25,
           EOLT_SYMBOL       = 28,
           ACCEPT_ACTION     = 394,
           ERROR_ACTION      = 395;

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
