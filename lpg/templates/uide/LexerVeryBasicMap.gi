$Headers
    --
    -- Additional methods for the action class not provided in the template
    --
    /.
        //
        // The Lexer contains an array of characters as the input stream to be parsed.
        // There are methods to retrieve and classify characters.
        // The lexparser "token" is implemented simply as the index of the next character in the array.
        // The Lexer extends the abstract class LpgLexStream with an implementation of the abstract
        // method getKind.  The template defines the Lexer class and the lexer() method.
        // A driver creates the action class, "Lexer", passing an Option object to the constructor.
        //
        boolean printTokens;
        private final static int ECLIPSE_TAB_VALUE = 4;

        public int [] getKeywordKinds() { return keywords; }

        public $action_type(String filename) throws java.io.IOException
        {
            this(filename, ECLIPSE_TAB_VALUE);
        }

        public void initialize(char [] content, String filename)
        {
            super.initialize(content, filename);
        }
        
        final void makeToken(int kind)
        {
            int startOffset = getLeftSpan(),
                endOffset = getRightSpan();
            makeToken(startOffset, endOffset, kind);
            if (printTokens) printValue(startOffset, endOffset);
        }

        final void makeComment(int kind)
        {
            int startOffset = getLeftSpan(),
                endOffset = getRightSpan();
            super.getPrsStream().makeAdjunct(startOffset, endOffset, kind);
        }

        final void skipToken()
        {
            if (printTokens) printValue(getLeftSpan(), getRightSpan());
        }
        
        final void printValue(int startOffset, int endOffset)
        {
            String s = new String(getInputChars(), startOffset, endOffset - startOffset + 1);
            System.out.print(s);
        }

        //
        //
        //
        public final static int tokenKind[] =
        {
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_HT,
            Char_LF,
            Char_CtlCharNotWS,
            Char_FF,
            Char_CR,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_CtlCharNotWS,
            Char_Space,
            Char_Exclamation,
            Char_DoubleQuote,
            Char_Sharp,
            Char_DollarSign,
            Char_Percent,
            Char_Ampersand,
            Char_SingleQuote,
            Char_LeftParen,
            Char_RightParen,
            Char_Star,
            Char_Plus,
            Char_Comma,
            Char_Minus,
            Char_Dot,
            Char_Slash,
            Char_0,
            Char_1,
            Char_2,
            Char_3,
            Char_4,
            Char_5,
            Char_6,
            Char_7,
            Char_8,
            Char_9,
            Char_Colon,
            Char_SemiColon,
            Char_LessThan,
            Char_Equal,
            Char_GreaterThan,
            Char_QuestionMark,
            Char_AtSign,
            Char_A,
            Char_B,
            Char_C,
            Char_D,
            Char_E,
            Char_F,
            Char_G,
            Char_H,
            Char_I,
            Char_J,
            Char_K,
            Char_L,
            Char_M,
            Char_N,
            Char_O,
            Char_P,
            Char_Q,
            Char_R,
            Char_S,
            Char_T,
            Char_U,
            Char_V,
            Char_W,
            Char_X,
            Char_Y,
            Char_Z,
            Char_LeftBracket,
            Char_BackSlash,
            Char_RightBracket,
            Char_Caret,
            Char__,
            Char_BackQuote,
            Char_a,
            Char_b,
            Char_c,
            Char_d,
            Char_e,
            Char_f,
            Char_g,
            Char_h,
            Char_i,
            Char_j,
            Char_k,
            Char_l,
            Char_m,
            Char_n,
            Char_o,
            Char_p,
            Char_q,
            Char_r,
            Char_s,
            Char_t,
            Char_u,
            Char_v,
            Char_w,
            Char_x,
            Char_y,
            Char_z,
            Char_LeftBrace,
            Char_VerticalBar,
            Char_RightBrace,
            Char_Tilde,
            Char_AfterASCII, // for all chars in range 128..65534
            Char_EOF         // for '\uffff' or 65535 
        };
                
        public final int getKind(int i)  // Classify character at ith location
        {
            char c = (i >= getStreamLength() ? '\uffff' : getCharValue(i));
            return (c < 128 // ASCII Character
                      ? tokenKind[c]
                      : c == '\uffff'
                           ? Char_EOF
                           : Char_AfterASCII);
        }
    ./
$End
