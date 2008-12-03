%Headers
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
        $kw_lexer_class kwLexer;
        boolean printTokens;
        private final static int ECLIPSE_TAB_VALUE = 4;

        public int [] getKeywordKinds() { return kwLexer.getKeywordKinds(); }

        public $action_type(String filename) throws java.io.IOException
        {
            this(filename, ECLIPSE_TAB_VALUE);
            this.kwLexer = new $kw_lexer_class(getInputChars(), $_IDENTIFIER);
        }

        public void initialize(char [] content, String filename)
        {
            super.initialize(content, filename);
            if (this.kwLexer == null)
                 this.kwLexer = new $kw_lexer_class(getInputChars(), $_IDENTIFIER);
            else this.kwLexer.setInputChars(getInputChars());
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
        
        final void checkForKeyWord()
        {
            int startOffset = getLeftSpan(),
                endOffset = getRightSpan(),
                kwKind = kwLexer.lexer(startOffset, endOffset);
            makeToken(startOffset, endOffset, kwKind);
            if (printTokens) printValue(startOffset, endOffset);
        }
        
        //
        // This flavor of checkForKeyWord is necessary when the default kind
        // (which is returned when the keyword filter doesn't match) is something
        // other than _IDENTIFIER.
        //
        final void checkForKeyWord(int defaultKind)
        {
            int startOffset = getLeftSpan(),
                endOffset = getRightSpan(),
                kwKind = kwLexer.lexer(startOffset, endOffset);
            if (kwKind == $_IDENTIFIER)
              kwKind = defaultKind;
            makeToken(startOffset, endOffset, kwKind);
            if (printTokens) printValue(startOffset, endOffset);
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
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 000    0x00
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 001    0x01
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 002    0x02
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 003    0x03
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 004    0x04
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 005    0x05
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 006    0x06
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 007    0x07
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 008    0x08
            $sym_type.$prefix$HT$suffix$,              // 009    0x09
            $sym_type.$prefix$LF$suffix$,              // 010    0x0A
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 011    0x0B
            $sym_type.$prefix$FF$suffix$,              // 012    0x0C
            $sym_type.$prefix$CR$suffix$,              // 013    0x0D
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 014    0x0E
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 015    0x0F
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 016    0x10
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 017    0x11
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 018    0x12
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 019    0x13
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 020    0x14
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 021    0x15
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 022    0x16
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 023    0x17
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 024    0x18
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 025    0x19
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 026    0x1A
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 027    0x1B
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 028    0x1C
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 029    0x1D
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 030    0x1E
            $sym_type.$prefix$CtlCharNotWS$suffix$,    // 031    0x1F
            $sym_type.$prefix$Space$suffix$,           // 032    0x20
            $sym_type.$prefix$Exclamation$suffix$,     // 033    0x21
            $sym_type.$prefix$DoubleQuote$suffix$,     // 034    0x22
            $sym_type.$prefix$Sharp$suffix$,           // 035    0x23
            $sym_type.$prefix$DollarSign$suffix$,      // 036    0x24
            $sym_type.$prefix$Percent$suffix$,         // 037    0x25
            $sym_type.$prefix$Ampersand$suffix$,       // 038    0x26
            $sym_type.$prefix$SingleQuote$suffix$,     // 039    0x27
            $sym_type.$prefix$LeftParen$suffix$,       // 040    0x28
            $sym_type.$prefix$RightParen$suffix$,      // 041    0x29
            $sym_type.$prefix$Star$suffix$,            // 042    0x2A
            $sym_type.$prefix$Plus$suffix$,            // 043    0x2B
            $sym_type.$prefix$Comma$suffix$,           // 044    0x2C
            $sym_type.$prefix$Minus$suffix$,           // 045    0x2D
            $sym_type.$prefix$Dot$suffix$,             // 046    0x2E
            $sym_type.$prefix$Slash$suffix$,           // 047    0x2F
            $sym_type.$prefix$0$suffix$,               // 048    0x30
            $sym_type.$prefix$1$suffix$,               // 049    0x31
            $sym_type.$prefix$2$suffix$,               // 050    0x32
            $sym_type.$prefix$3$suffix$,               // 051    0x33
            $sym_type.$prefix$4$suffix$,               // 052    0x34
            $sym_type.$prefix$5$suffix$,               // 053    0x35
            $sym_type.$prefix$6$suffix$,               // 054    0x36
            $sym_type.$prefix$7$suffix$,               // 055    0x37
            $sym_type.$prefix$8$suffix$,               // 056    0x38
            $sym_type.$prefix$9$suffix$,               // 057    0x39
            $sym_type.$prefix$Colon$suffix$,           // 058    0x3A
            $sym_type.$prefix$SemiColon$suffix$,       // 059    0x3B
            $sym_type.$prefix$LessThan$suffix$,        // 060    0x3C
            $sym_type.$prefix$Equal$suffix$,           // 061    0x3D
            $sym_type.$prefix$GreaterThan$suffix$,     // 062    0x3E
            $sym_type.$prefix$QuestionMark$suffix$,    // 063    0x3F
            $sym_type.$prefix$AtSign$suffix$,          // 064    0x40
            $sym_type.$prefix$A$suffix$,               // 065    0x41
            $sym_type.$prefix$B$suffix$,               // 066    0x42
            $sym_type.$prefix$C$suffix$,               // 067    0x43
            $sym_type.$prefix$D$suffix$,               // 068    0x44
            $sym_type.$prefix$E$suffix$,               // 069    0x45
            $sym_type.$prefix$F$suffix$,               // 070    0x46
            $sym_type.$prefix$G$suffix$,               // 071    0x47
            $sym_type.$prefix$H$suffix$,               // 072    0x48
            $sym_type.$prefix$I$suffix$,               // 073    0x49
            $sym_type.$prefix$J$suffix$,               // 074    0x4A
            $sym_type.$prefix$K$suffix$,               // 075    0x4B
            $sym_type.$prefix$L$suffix$,               // 076    0x4C
            $sym_type.$prefix$M$suffix$,               // 077    0x4D
            $sym_type.$prefix$N$suffix$,               // 078    0x4E
            $sym_type.$prefix$O$suffix$,               // 079    0x4F
            $sym_type.$prefix$P$suffix$,               // 080    0x50
            $sym_type.$prefix$Q$suffix$,               // 081    0x51
            $sym_type.$prefix$R$suffix$,               // 082    0x52
            $sym_type.$prefix$S$suffix$,               // 083    0x53
            $sym_type.$prefix$T$suffix$,               // 084    0x54
            $sym_type.$prefix$U$suffix$,               // 085    0x55
            $sym_type.$prefix$V$suffix$,               // 086    0x56
            $sym_type.$prefix$W$suffix$,               // 087    0x57
            $sym_type.$prefix$X$suffix$,               // 088    0x58
            $sym_type.$prefix$Y$suffix$,               // 089    0x59
            $sym_type.$prefix$Z$suffix$,               // 090    0x5A
            $sym_type.$prefix$LeftBracket$suffix$,     // 091    0x5B
            $sym_type.$prefix$BackSlash$suffix$,       // 092    0x5C
            $sym_type.$prefix$RightBracket$suffix$,    // 093    0x5D
            $sym_type.$prefix$Caret$suffix$,           // 094    0x5E
            $sym_type.$prefix$_$suffix$,               // 095    0x5F
            $sym_type.$prefix$BackQuote$suffix$,       // 096    0x60
            $sym_type.$prefix$a$suffix$,               // 097    0x61
            $sym_type.$prefix$b$suffix$,               // 098    0x62
            $sym_type.$prefix$c$suffix$,               // 099    0x63
            $sym_type.$prefix$d$suffix$,               // 100    0x64
            $sym_type.$prefix$e$suffix$,               // 101    0x65
            $sym_type.$prefix$f$suffix$,               // 102    0x66
            $sym_type.$prefix$g$suffix$,               // 103    0x67
            $sym_type.$prefix$h$suffix$,               // 104    0x68
            $sym_type.$prefix$i$suffix$,               // 105    0x69
            $sym_type.$prefix$j$suffix$,               // 106    0x6A
            $sym_type.$prefix$k$suffix$,               // 107    0x6B
            $sym_type.$prefix$l$suffix$,               // 108    0x6C
            $sym_type.$prefix$m$suffix$,               // 109    0x6D
            $sym_type.$prefix$n$suffix$,               // 110    0x6E
            $sym_type.$prefix$o$suffix$,               // 111    0x6F
            $sym_type.$prefix$p$suffix$,               // 112    0x70
            $sym_type.$prefix$q$suffix$,               // 113    0x71
            $sym_type.$prefix$r$suffix$,               // 114    0x72
            $sym_type.$prefix$s$suffix$,               // 115    0x73
            $sym_type.$prefix$t$suffix$,               // 116    0x74
            $sym_type.$prefix$u$suffix$,               // 117    0x75
            $sym_type.$prefix$v$suffix$,               // 118    0x76
            $sym_type.$prefix$w$suffix$,               // 119    0x77
            $sym_type.$prefix$x$suffix$,               // 120    0x78
            $sym_type.$prefix$y$suffix$,               // 121    0x79
            $sym_type.$prefix$z$suffix$,               // 122    0x7A
            $sym_type.$prefix$LeftBrace$suffix$,       // 123    0x7B
            $sym_type.$prefix$VerticalBar$suffix$,     // 124    0x7C
            $sym_type.$prefix$RightBrace$suffix$,      // 125    0x7D
            $sym_type.$prefix$Tilde$suffix$,           // 126    0x7E

            $sym_type.$prefix$AfterASCII$suffix$,      // for all chars in range 128..65534
            $sym_type.$prefix$EOF$suffix$              // for '\uffff' or 65535 
        };
                
        public final int getKind(int i)  // Classify character at ith location
        {
            int c = (i >= getStreamLength() ? '\uffff' : getCharValue(i));
            return (c < 128 // ASCII Character
                      ? tokenKind[c]
                      : c == '\uffff'
                           ? $sym_type.$prefix$EOF$suffix$
                           : $sym_type.$prefix$AfterASCII$suffix$);
        }
    ./
%End
