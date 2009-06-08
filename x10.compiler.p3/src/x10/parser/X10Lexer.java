
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006-2008
//

package x10.parser;

import lpg.runtime.*;
import java.util.*;

public class X10Lexer implements RuleAction
{
    private X10LexerLpgLexStream lexStream;
    
    private static ParseTable prs = new X10Lexerprs();
    public ParseTable getParseTable() { return prs; }

    private LexParser lexParser = new LexParser();
    public LexParser getParser() { return lexParser; }

    public int getToken(int i) { return lexParser.getToken(i); }
    public int getRhsFirstTokenIndex(int i) { return lexParser.getFirstToken(i); }
    public int getRhsLastTokenIndex(int i) { return lexParser.getLastToken(i); }

    public int getLeftSpan() { return lexParser.getToken(1); }
    public int getRightSpan() { return lexParser.getLastToken(); }

    public void resetKeywordLexer()
    {
        if (kwLexer == null)
              this.kwLexer = new X10KWLexer(lexStream.getInputChars(), X10Parsersym.TK_IDENTIFIER);
        else this.kwLexer.setInputChars(lexStream.getInputChars());
    }

    public void reset(String filename, int tab) throws java.io.IOException
    {
        lexStream = new X10LexerLpgLexStream(filename, tab);
        lexParser.reset((ILexStream) lexStream, prs, (RuleAction) this);
        resetKeywordLexer();
    }

    public void reset(char[] input_chars, String filename)
    {
        reset(input_chars, filename, 1);
    }
    
    public void reset(char[] input_chars, String filename, int tab)
    {
        lexStream = new X10LexerLpgLexStream(input_chars, filename, tab);
        lexParser.reset((ILexStream) lexStream, prs, (RuleAction) this);
        resetKeywordLexer();
    }
    
    public X10Lexer(String filename, int tab) throws java.io.IOException 
    {
        reset(filename, tab);
    }

    public X10Lexer(char[] input_chars, String filename, int tab)
    {
        reset(input_chars, filename, tab);
    }

    public X10Lexer(char[] input_chars, String filename)
    {
        reset(input_chars, filename, 1);
    }

    public X10Lexer() {}

    public ILexStream getILexStream() { return lexStream; }

    /**
     * @deprecated replaced by {@link #getILexStream()}
     */
    public ILexStream getLexStream() { return lexStream; }

    private void initializeLexer(IPrsStream prsStream, int start_offset, int end_offset)
    {
        if (lexStream.getInputChars() == null)
            throw new NullPointerException("LexStream was not initialized");
        lexStream.setPrsStream(prsStream);
        prsStream.makeToken(start_offset, end_offset, 0); // Token list must start with a bad token
    }

    private void addEOF(IPrsStream prsStream, int end_offset)
    {
        prsStream.makeToken(end_offset, end_offset, X10Parsersym.TK_EOF_TOKEN); // and end with the end of file token
        prsStream.setStreamLength(prsStream.getSize());
    }

    public void lexer(IPrsStream prsStream)
    {
        lexer(null, prsStream);
    }
    
    public void lexer(Monitor monitor, IPrsStream prsStream)
    {
        initializeLexer(prsStream, 0, -1);
        lexParser.parseCharacters(monitor);  // Lex the input characters
        addEOF(prsStream, lexStream.getStreamIndex());
    }

    public void lexer(IPrsStream prsStream, int start_offset, int end_offset)
    {
        lexer(null, prsStream, start_offset, end_offset);
    }
    
    public void lexer(Monitor monitor, IPrsStream prsStream, int start_offset, int end_offset)
    {
        if (start_offset <= 1)
             initializeLexer(prsStream, 0, -1);
        else initializeLexer(prsStream, start_offset - 1, start_offset - 1);

        lexParser.parseCharacters(monitor, start_offset, end_offset);

        addEOF(prsStream, (end_offset >= lexStream.getStreamIndex() ? lexStream.getStreamIndex() : end_offset + 1));
    }

    /**
     * If a parse stream was not passed to this Lexical analyser then we
     * simply report a lexical error. Otherwise, we produce a bad token.
     */
    public void reportLexicalError(int startLoc, int endLoc) {
        IPrsStream prs_stream = lexStream.getPrsStream();
        if (prs_stream == null)
            lexStream.reportLexicalError(startLoc, endLoc);
        else {
            //
            // Remove any token that may have been processed that fall in the
            // range of the lexical error... then add one error token that spans
            // the error range.
            //
            for (int i = prs_stream.getSize() - 1; i > 0; i--) {
                if (prs_stream.getStartOffset(i) >= startLoc)
                     prs_stream.removeLastToken();
                else break;
            }
            prs_stream.makeToken(startLoc, endLoc, 0); // add an error token to the prsStream
        }        
    }

    //
    // The Lexer contains an array of characters as the input stream to be parsed.
    // There are methods to retrieve and classify characters.
    // The lexparser "token" is implemented simply as the index of the next character in the array.
    // The Lexer extends the abstract class LpgLexStream with an implementation of the abstract
    // method getKind.  The template defines the Lexer class and the lexer() method.
    // A driver creates the action class, "Lexer", passing an Option object to the constructor.
    //
    X10KWLexer kwLexer;
    boolean printTokens;
    private final static int ECLIPSE_TAB_VALUE = 4;

    public int [] getKeywordKinds() { return kwLexer.getKeywordKinds(); }

    public X10Lexer(String filename) throws java.io.IOException
    {
        this(filename, ECLIPSE_TAB_VALUE);
        this.kwLexer = new X10KWLexer(lexStream.getInputChars(), X10Parsersym.TK_IDENTIFIER);
    }

    /**
     * @deprecated function replaced by {@link #reset(char [] content, String filename)}
     */
    public void initialize(char [] content, String filename)
    {
        reset(content, filename);
    }
    
    final void makeToken(int left_token, int right_token, int kind)
    {
        lexStream.makeToken(left_token, right_token, kind);
    }
    
    final void makeToken(int kind)
    {
        int startOffset = getLeftSpan(),
            endOffset = getRightSpan();
        lexStream.makeToken(startOffset, endOffset, kind);
        if (printTokens) printValue(startOffset, endOffset);
    }

    final void makeComment(int kind)
    {
        int startOffset = getLeftSpan(),
            endOffset = getRightSpan();
        lexStream.getIPrsStream().makeAdjunct(startOffset, endOffset, kind);
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
        lexStream.makeToken(startOffset, endOffset, kwKind);
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
        if (kwKind == X10Parsersym.TK_IDENTIFIER)
            kwKind = defaultKind;
        lexStream.makeToken(startOffset, endOffset, kwKind);
        if (printTokens) printValue(startOffset, endOffset);
    }
    
    final void printValue(int startOffset, int endOffset)
    {
        String s = new String(lexStream.getInputChars(), startOffset, endOffset - startOffset + 1);
        System.out.print(s);
    }

    //
    //
    //
    static class X10LexerLpgLexStream extends LpgLexStream
    {
    public final static int tokenKind[] =
    {
        X10Lexersym.Char_CtlCharNotWS,    // 000    0x00
        X10Lexersym.Char_CtlCharNotWS,    // 001    0x01
        X10Lexersym.Char_CtlCharNotWS,    // 002    0x02
        X10Lexersym.Char_CtlCharNotWS,    // 003    0x03
        X10Lexersym.Char_CtlCharNotWS,    // 004    0x04
        X10Lexersym.Char_CtlCharNotWS,    // 005    0x05
        X10Lexersym.Char_CtlCharNotWS,    // 006    0x06
        X10Lexersym.Char_CtlCharNotWS,    // 007    0x07
        X10Lexersym.Char_CtlCharNotWS,    // 008    0x08
        X10Lexersym.Char_HT,              // 009    0x09
        X10Lexersym.Char_LF,              // 010    0x0A
        X10Lexersym.Char_CtlCharNotWS,    // 011    0x0B
        X10Lexersym.Char_FF,              // 012    0x0C
        X10Lexersym.Char_CR,              // 013    0x0D
        X10Lexersym.Char_CtlCharNotWS,    // 014    0x0E
        X10Lexersym.Char_CtlCharNotWS,    // 015    0x0F
        X10Lexersym.Char_CtlCharNotWS,    // 016    0x10
        X10Lexersym.Char_CtlCharNotWS,    // 017    0x11
        X10Lexersym.Char_CtlCharNotWS,    // 018    0x12
        X10Lexersym.Char_CtlCharNotWS,    // 019    0x13
        X10Lexersym.Char_CtlCharNotWS,    // 020    0x14
        X10Lexersym.Char_CtlCharNotWS,    // 021    0x15
        X10Lexersym.Char_CtlCharNotWS,    // 022    0x16
        X10Lexersym.Char_CtlCharNotWS,    // 023    0x17
        X10Lexersym.Char_CtlCharNotWS,    // 024    0x18
        X10Lexersym.Char_CtlCharNotWS,    // 025    0x19
        X10Lexersym.Char_CtlCharNotWS,    // 026    0x1A
        X10Lexersym.Char_CtlCharNotWS,    // 027    0x1B
        X10Lexersym.Char_CtlCharNotWS,    // 028    0x1C
        X10Lexersym.Char_CtlCharNotWS,    // 029    0x1D
        X10Lexersym.Char_CtlCharNotWS,    // 030    0x1E
        X10Lexersym.Char_CtlCharNotWS,    // 031    0x1F
        X10Lexersym.Char_Space,           // 032    0x20
        X10Lexersym.Char_Exclamation,     // 033    0x21
        X10Lexersym.Char_DoubleQuote,     // 034    0x22
        X10Lexersym.Char_Sharp,           // 035    0x23
        X10Lexersym.Char_DollarSign,      // 036    0x24
        X10Lexersym.Char_Percent,         // 037    0x25
        X10Lexersym.Char_Ampersand,       // 038    0x26
        X10Lexersym.Char_SingleQuote,     // 039    0x27
        X10Lexersym.Char_LeftParen,       // 040    0x28
        X10Lexersym.Char_RightParen,      // 041    0x29
        X10Lexersym.Char_Star,            // 042    0x2A
        X10Lexersym.Char_Plus,            // 043    0x2B
        X10Lexersym.Char_Comma,           // 044    0x2C
        X10Lexersym.Char_Minus,           // 045    0x2D
        X10Lexersym.Char_Dot,             // 046    0x2E
        X10Lexersym.Char_Slash,           // 047    0x2F
        X10Lexersym.Char_0,               // 048    0x30
        X10Lexersym.Char_1,               // 049    0x31
        X10Lexersym.Char_2,               // 050    0x32
        X10Lexersym.Char_3,               // 051    0x33
        X10Lexersym.Char_4,               // 052    0x34
        X10Lexersym.Char_5,               // 053    0x35
        X10Lexersym.Char_6,               // 054    0x36
        X10Lexersym.Char_7,               // 055    0x37
        X10Lexersym.Char_8,               // 056    0x38
        X10Lexersym.Char_9,               // 057    0x39
        X10Lexersym.Char_Colon,           // 058    0x3A
        X10Lexersym.Char_SemiColon,       // 059    0x3B
        X10Lexersym.Char_LessThan,        // 060    0x3C
        X10Lexersym.Char_Equal,           // 061    0x3D
        X10Lexersym.Char_GreaterThan,     // 062    0x3E
        X10Lexersym.Char_QuestionMark,    // 063    0x3F
        X10Lexersym.Char_AtSign,          // 064    0x40
        X10Lexersym.Char_A,               // 065    0x41
        X10Lexersym.Char_B,               // 066    0x42
        X10Lexersym.Char_C,               // 067    0x43
        X10Lexersym.Char_D,               // 068    0x44
        X10Lexersym.Char_E,               // 069    0x45
        X10Lexersym.Char_F,               // 070    0x46
        X10Lexersym.Char_G,               // 071    0x47
        X10Lexersym.Char_H,               // 072    0x48
        X10Lexersym.Char_I,               // 073    0x49
        X10Lexersym.Char_J,               // 074    0x4A
        X10Lexersym.Char_K,               // 075    0x4B
        X10Lexersym.Char_L,               // 076    0x4C
        X10Lexersym.Char_M,               // 077    0x4D
        X10Lexersym.Char_N,               // 078    0x4E
        X10Lexersym.Char_O,               // 079    0x4F
        X10Lexersym.Char_P,               // 080    0x50
        X10Lexersym.Char_Q,               // 081    0x51
        X10Lexersym.Char_R,               // 082    0x52
        X10Lexersym.Char_S,               // 083    0x53
        X10Lexersym.Char_T,               // 084    0x54
        X10Lexersym.Char_U,               // 085    0x55
        X10Lexersym.Char_V,               // 086    0x56
        X10Lexersym.Char_W,               // 087    0x57
        X10Lexersym.Char_X,               // 088    0x58
        X10Lexersym.Char_Y,               // 089    0x59
        X10Lexersym.Char_Z,               // 090    0x5A
        X10Lexersym.Char_LeftBracket,     // 091    0x5B
        X10Lexersym.Char_BackSlash,       // 092    0x5C
        X10Lexersym.Char_RightBracket,    // 093    0x5D
        X10Lexersym.Char_Caret,           // 094    0x5E
        X10Lexersym.Char__,               // 095    0x5F
        X10Lexersym.Char_BackQuote,       // 096    0x60
        X10Lexersym.Char_a,               // 097    0x61
        X10Lexersym.Char_b,               // 098    0x62
        X10Lexersym.Char_c,               // 099    0x63
        X10Lexersym.Char_d,               // 100    0x64
        X10Lexersym.Char_e,               // 101    0x65
        X10Lexersym.Char_f,               // 102    0x66
        X10Lexersym.Char_g,               // 103    0x67
        X10Lexersym.Char_h,               // 104    0x68
        X10Lexersym.Char_i,               // 105    0x69
        X10Lexersym.Char_j,               // 106    0x6A
        X10Lexersym.Char_k,               // 107    0x6B
        X10Lexersym.Char_l,               // 108    0x6C
        X10Lexersym.Char_m,               // 109    0x6D
        X10Lexersym.Char_n,               // 110    0x6E
        X10Lexersym.Char_o,               // 111    0x6F
        X10Lexersym.Char_p,               // 112    0x70
        X10Lexersym.Char_q,               // 113    0x71
        X10Lexersym.Char_r,               // 114    0x72
        X10Lexersym.Char_s,               // 115    0x73
        X10Lexersym.Char_t,               // 116    0x74
        X10Lexersym.Char_u,               // 117    0x75
        X10Lexersym.Char_v,               // 118    0x76
        X10Lexersym.Char_w,               // 119    0x77
        X10Lexersym.Char_x,               // 120    0x78
        X10Lexersym.Char_y,               // 121    0x79
        X10Lexersym.Char_z,               // 122    0x7A
        X10Lexersym.Char_LeftBrace,       // 123    0x7B
        X10Lexersym.Char_VerticalBar,     // 124    0x7C
        X10Lexersym.Char_RightBrace,      // 125    0x7D
        X10Lexersym.Char_Tilde,           // 126    0x7E

        X10Lexersym.Char_AfterASCII,      // for all chars in range 128..65534
        X10Lexersym.Char_EOF              // for '\uffff' or 65535 
    };
            
    public final int getKind(int i)  // Classify character at ith location
    {
        int c = (i >= getStreamLength() ? '\uffff' : getCharValue(i));
        return (c < 128 // ASCII Character
                  ? tokenKind[c]
                  : c == '\uffff'
                       ? X10Lexersym.Char_EOF
                       : X10Lexersym.Char_AfterASCII);
    }

    public String[] orderedExportedSymbols() { return X10Parsersym.orderedTerminalSymbols; }

    public X10LexerLpgLexStream(String filename, int tab) throws java.io.IOException
    {
        super(filename, tab);
    }

    public X10LexerLpgLexStream(char[] input_chars, String filename, int tab)
    {
        super(input_chars, filename, tab);
    }

    public X10LexerLpgLexStream(char[] input_chars, String filename)
    {
        super(input_chars, filename, 1);
    }
    }

    public void makeX10Token(int startLoc, int endLoc, int kind)
    {
        if (kind == X10Parsersym.TK_IDENTIFIER)
        {
            int index = lexStream.getIPrsStream().getSize() - 1;
            IToken token = lexStream.getIPrsStream().getIToken(index);
            if (token.getKind() == X10Parsersym.TK_DoubleLiteral && lexStream.getInputChars()[token.getEndOffset()] == '.')
            {
                token.setEndOffset(token.getEndOffset() - 1);
                lexStream.getIPrsStream().makeToken(token.getEndOffset(), token.getEndOffset(), X10Parsersym.TK_DOT);
            }
        }
        lexStream.makeToken(startLoc, endLoc, kind);
    }
    
    final void checkForX10KeyWord()
    {
        int startOffset = getLeftSpan(),
            endOffset = getRightSpan(),
            kwKind = kwLexer.lexer(startOffset, endOffset);
        makeX10Token(startOffset, endOffset, kwKind);
        if (printTokens) printValue(startOffset, endOffset);
    }

    public X10Lexer(java.io.Reader reader, String filename) throws java.io.IOException
    {
        this(reader, filename, ECLIPSE_TAB_VALUE);
    }

    public X10Lexer(java.io.Reader reader, String filename, int tab) throws java.io.IOException
    {
        ArrayList buffers = new ArrayList();
        int size = 0;
        while (true)
        {
            char block[]= new char[8192];
            int n = reader.read(block, 0, block.length);
            if (n < 0)
                break;
            size += n;
            buffers.add((Object) block);
        }

        char buffer[] = new char[size];
        for (int i = 0; i < buffers.size(); i++)
        {
            char block[] = (char []) buffers.get(i);
            int blocksize = (size / block.length > 0 ? block.length : size);
            size -= blocksize;
            System.arraycopy(block, 0, buffer, i * block.length, blocksize);
        }
        assert(size == 0);
    
        reset(buffer, filename, tab);
        kwLexer = new X10KWLexer(lexStream.getInputChars(), X10Parsersym.TK_IDENTIFIER);
    }

    public void ruleAction(int ruleNumber)
    {
        switch(ruleNumber)
        {

            //
            // Rule 1:  Token ::= Identifier
            //
            case 1: { 
                checkForX10KeyWord();
                  break;
            }
    
            //
            // Rule 2:  Token ::= " SLBody "
            //
            case 2: { 
                makeToken(X10Parsersym.TK_StringLiteral);
                  break;
            }
    
            //
            // Rule 3:  Token ::= ' NotSQ '
            //
            case 3: { 
                makeToken(X10Parsersym.TK_CharacterLiteral);
                  break;
            }
    
            //
            // Rule 4:  Token ::= IntegerLiteral
            //
            case 4: { 
                makeToken(X10Parsersym.TK_IntegerLiteral);
                  break;
            }
    
            //
            // Rule 5:  Token ::= LongLiteral
            //
            case 5: { 
                makeToken(X10Parsersym.TK_LongLiteral);
                  break;
            }
    
            //
            // Rule 6:  Token ::= FloatingPointLiteral
            //
            case 6: { 
                makeToken(X10Parsersym.TK_FloatingPointLiteral);
                  break;
            }
    
            //
            // Rule 7:  Token ::= DoubleLiteral
            //
            case 7: { 
                makeToken(X10Parsersym.TK_DoubleLiteral);
                  break;
            }
    
            //
            // Rule 10:  Token ::= WS
            //
            case 10: { 
                skipToken();
                  break;
            }
    
            //
            // Rule 11:  Token ::= +
            //
            case 11: { 
                makeToken(X10Parsersym.TK_PLUS);
                  break;
            }
    
            //
            // Rule 12:  Token ::= -
            //
            case 12: { 
                makeToken(X10Parsersym.TK_MINUS);
                  break;
            }
    
            //
            // Rule 13:  Token ::= *
            //
            case 13: { 
                makeToken(X10Parsersym.TK_MULTIPLY);
                  break;
            }
    
            //
            // Rule 14:  Token ::= /
            //
            case 14: { 
                makeToken(X10Parsersym.TK_DIVIDE);
                  break;
            }
    
            //
            // Rule 15:  Token ::= (
            //
            case 15: { 
                makeToken(X10Parsersym.TK_LPAREN);
                  break;
            }
    
            //
            // Rule 16:  Token ::= )
            //
            case 16: { 
                makeToken(X10Parsersym.TK_RPAREN);
                  break;
            }
    
            //
            // Rule 17:  Token ::= =
            //
            case 17: { 
                makeToken(X10Parsersym.TK_EQUAL);
                  break;
            }
    
            //
            // Rule 18:  Token ::= ,
            //
            case 18: { 
                makeToken(X10Parsersym.TK_COMMA);
                  break;
            }
    
            //
            // Rule 19:  Token ::= :
            //
            case 19: { 
                makeToken(X10Parsersym.TK_COLON);
                  break;
            }
    
            //
            // Rule 20:  Token ::= ;
            //
            case 20: { 
                makeToken(X10Parsersym.TK_SEMICOLON);
                  break;
            }
    
            //
            // Rule 21:  Token ::= ^
            //
            case 21: { 
                makeToken(X10Parsersym.TK_XOR);
                  break;
            }
    
            //
            // Rule 22:  Token ::= %
            //
            case 22: { 
                makeToken(X10Parsersym.TK_REMAINDER);
                  break;
            }
    
            //
            // Rule 23:  Token ::= ~
            //
            case 23: { 
                makeToken(X10Parsersym.TK_TWIDDLE);
                  break;
            }
    
            //
            // Rule 24:  Token ::= |
            //
            case 24: { 
                makeToken(X10Parsersym.TK_OR);
                  break;
            }
    
            //
            // Rule 25:  Token ::= &
            //
            case 25: { 
                makeToken(X10Parsersym.TK_AND);
                  break;
            }
    
            //
            // Rule 26:  Token ::= <
            //
            case 26: { 
                makeToken(X10Parsersym.TK_LESS);
                  break;
            }
    
            //
            // Rule 27:  Token ::= >
            //
            case 27: { 
                makeToken(X10Parsersym.TK_GREATER);
                  break;
            }
    
            //
            // Rule 28:  Token ::= .
            //
            case 28: { 
                makeToken(X10Parsersym.TK_DOT);
                  break;
            }
    
            //
            // Rule 29:  Token ::= !
            //
            case 29: { 
                makeToken(X10Parsersym.TK_NOT);
                  break;
            }
    
            //
            // Rule 30:  Token ::= [
            //
            case 30: { 
                makeToken(X10Parsersym.TK_LBRACKET);
                  break;
            }
    
            //
            // Rule 31:  Token ::= ]
            //
            case 31: { 
                makeToken(X10Parsersym.TK_RBRACKET);
                  break;
            }
    
            //
            // Rule 32:  Token ::= {
            //
            case 32: { 
                makeToken(X10Parsersym.TK_LBRACE);
                  break;
            }
    
            //
            // Rule 33:  Token ::= }
            //
            case 33: { 
                makeToken(X10Parsersym.TK_RBRACE);
                  break;
            }
    
            //
            // Rule 34:  Token ::= ?
            //
            case 34: { 
                makeToken(X10Parsersym.TK_QUESTION);
                  break;
            }
    
            //
            // Rule 35:  Token ::= @
            //
            case 35: { 
                makeToken(X10Parsersym.TK_AT);
                  break;
            }
    
            //
            // Rule 36:  Token ::= + +
            //
            case 36: { 
                makeToken(X10Parsersym.TK_PLUS_PLUS);
                  break;
            }
    
            //
            // Rule 37:  Token ::= - -
            //
            case 37: { 
                makeToken(X10Parsersym.TK_MINUS_MINUS);
                  break;
            }
    
            //
            // Rule 38:  Token ::= = =
            //
            case 38: { 
                makeToken(X10Parsersym.TK_EQUAL_EQUAL);
                  break;
            }
    
            //
            // Rule 39:  Token ::= < =
            //
            case 39: { 
                makeToken(X10Parsersym.TK_LESS_EQUAL);
                  break;
            }
    
            //
            // Rule 40:  Token ::= > =
            //
            case 40: { 
                makeToken(X10Parsersym.TK_GREATER_EQUAL);
                  break;
            }
    
            //
            // Rule 41:  Token ::= ! =
            //
            case 41: { 
                makeToken(X10Parsersym.TK_NOT_EQUAL);
                  break;
            }
    
            //
            // Rule 42:  Token ::= < <
            //
            case 42: { 
                makeToken(X10Parsersym.TK_LEFT_SHIFT);
                  break;
            }
    
            //
            // Rule 43:  Token ::= > >
            //
            case 43: { 
                makeToken(X10Parsersym.TK_RIGHT_SHIFT);
                  break;
            }
    
            //
            // Rule 44:  Token ::= > > >
            //
            case 44: { 
                makeToken(X10Parsersym.TK_UNSIGNED_RIGHT_SHIFT);
                  break;
            }
    
            //
            // Rule 45:  Token ::= + =
            //
            case 45: { 
                makeToken(X10Parsersym.TK_PLUS_EQUAL);
                  break;
            }
    
            //
            // Rule 46:  Token ::= - =
            //
            case 46: { 
                makeToken(X10Parsersym.TK_MINUS_EQUAL);
                  break;
            }
    
            //
            // Rule 47:  Token ::= * =
            //
            case 47: { 
                makeToken(X10Parsersym.TK_MULTIPLY_EQUAL);
                  break;
            }
    
            //
            // Rule 48:  Token ::= / =
            //
            case 48: { 
                makeToken(X10Parsersym.TK_DIVIDE_EQUAL);
                  break;
            }
    
            //
            // Rule 49:  Token ::= & =
            //
            case 49: { 
                makeToken(X10Parsersym.TK_AND_EQUAL);
                  break;
            }
    
            //
            // Rule 50:  Token ::= | =
            //
            case 50: { 
                makeToken(X10Parsersym.TK_OR_EQUAL);
                  break;
            }
    
            //
            // Rule 51:  Token ::= ^ =
            //
            case 51: { 
                makeToken(X10Parsersym.TK_XOR_EQUAL);
                  break;
            }
    
            //
            // Rule 52:  Token ::= % =
            //
            case 52: { 
                makeToken(X10Parsersym.TK_REMAINDER_EQUAL);
                  break;
            }
    
            //
            // Rule 53:  Token ::= < < =
            //
            case 53: { 
                makeToken(X10Parsersym.TK_LEFT_SHIFT_EQUAL);
                  break;
            }
    
            //
            // Rule 54:  Token ::= > > =
            //
            case 54: { 
                makeToken(X10Parsersym.TK_RIGHT_SHIFT_EQUAL);
                  break;
            }
    
            //
            // Rule 55:  Token ::= > > > =
            //
            case 55: { 
                makeToken(X10Parsersym.TK_UNSIGNED_RIGHT_SHIFT_EQUAL);
                  break;
            }
    
            //
            // Rule 56:  Token ::= | |
            //
            case 56: { 
                makeToken(X10Parsersym.TK_OR_OR);
                  break;
            }
    
            //
            // Rule 57:  Token ::= & &
            //
            case 57: { 
                makeToken(X10Parsersym.TK_AND_AND);
                  break;
            }
    
            //
            // Rule 58:  Token ::= . . .
            //
            case 58: { 
                makeToken(X10Parsersym.TK_ELLIPSIS);
                  break;
            }
    
            //
            // Rule 73:  MultiLineComment ::= / * Inside Stars /
            //
            case 73: { 
                if (lexStream.getKind(getRhsFirstTokenIndex(3)) == X10Lexersym.Char_Star && lexStream.getKind(lexStream.getNext(getRhsFirstTokenIndex(3))) != X10Lexersym.Char_Star)
                     makeComment(X10Parsersym.TK_DocComment);
                else makeComment(X10Parsersym.TK_MlComment);
                  break;
            }
    
            //
            // Rule 80:  SingleLineComment ::= SLC
            //
            case 80: { 
                makeComment(X10Parsersym.TK_SlComment);
                  break;
            }
    
            //
            // Rule 361:  Token ::= . .
            //
            case 361: { 
                  makeToken(X10Parsersym.TK_RANGE);
                    break;
            }
      
            //
            // Rule 362:  Token ::= - >
            //
            case 362: { 
                makeToken(X10Parsersym.TK_ARROW);
                  break;
            }
    
            //
            // Rule 363:  Token ::= = >
            //
            case 363: { 
                makeToken(X10Parsersym.TK_DARROW);
                  break;
            }
    
            //
            // Rule 364:  Token ::= < :
            //
            case 364: { 
                makeToken(X10Parsersym.TK_SUBTYPE);
                  break;
            }
    
            //
            // Rule 365:  Token ::= : >
            //
            case 365: { 
                makeToken(X10Parsersym.TK_SUPERTYPE);
                  break;
            }
    
            //
            // Rule 366:  IntLiteralAndRange ::= Integer . .
            //
            case 366: { 
                 makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), X10Parsersym.TK_IntegerLiteral);
                 makeToken(getToken(2), getToken(3), X10Parsersym.TK_RANGE);
                   break;
            }
     
    
            default:
                break;
        }
        return;
    }
}

