


package x10.parser;

import java.util.*;
import com.ibm.lpg.*;

public class X10Lexer extends LpgLexStream implements RuleAction, X10Parsersym, X10Lexersym
{
    private PrsStream prsStream;
    private ParseTable prs;
    private LexParser lexParser;

    public X10Lexer(char[] input_chars, String filename, int tab)
    {
        super(input_chars, filename, tab);
        prs = new X10Lexerprs();
        lexParser = new LexParser(this, prs, this);
    }

    public X10Lexer(char[] input_chars, String filename)
    {
        this(input_chars, filename, 1);
    }

    public String[] orderedExportedSymbols() { return X10Parsersym.orderedTerminalSymbols; }

    public void lexer(PrsStream prsStream)
    {
        this.prsStream = prsStream;

        prsStream.makeToken(0, 0, 0); // Token list must start with a bad token
            
        lexParser.parseCharacters();  // Lex the input characters
            
        int i = getStreamIndex();
        prsStream.makeToken(i, i, TK_EOF_TOKEN); // and end with the end of file token
        prsStream.setSize();
            
        return;
    }
    //
    // The Lexer contains an array of characters as the input stream to be parsed.
    // There are methods to retrieve and classify characters.
    // The lexparser "token" is implemented simply as the index of the next character in the array.
    // The Lexer extends the abstract class LpgLexStream with an implementation of the abstract
    // method getKind.  The template defines the Lexer class and the lexer() method.
    // A driver creates the action class, "Lexer", passing an Option object to the constructor.
    // The Option object gives access to the input character arrary, the file name and other options.
    //
    Option option;
    X10KWLexer kwLexer;
    boolean printTokens;
    private final static int ECLIPSE_TAB_VALUE = 4;

    public X10Lexer(Option option)
    {
        this(option.getInputChars(), option.getFileName(), ECLIPSE_TAB_VALUE);
        this.option = option;
        printTokens = option.printTokens();
        kwLexer = new X10KWLexer(getInputChars(), TK_IDENTIFIER);
    }

    final void makeDocComment()
    {
        int startOffset = lexParser.getToken(1),
            endOffset = lexParser.getLastToken();
        skipToken();
    }

    final void makeToken(int kind)
    {
        int startOffset = lexParser.getToken(1),
            endOffset = lexParser.getLastToken();
        prsStream.makeToken(startOffset, endOffset, kind);
        if (printTokens) printValue(startOffset, endOffset);
    }

    final void skipToken()
    {
        if (printTokens) printValue(lexParser.getToken(1), lexParser.getLastToken());
    }
    
    final void checkForKeyWord()
    {
        int startOffset = lexParser.getToken(1),
            endOffset = lexParser.getLastToken(),
        kwKind = kwLexer.lexer(startOffset, endOffset);
        prsStream.makeToken(startOffset, endOffset, kwKind);
        if(printTokens) printValue(startOffset, endOffset);
    }
    
    final void printValue(int startOffset, int endOffset)
    {
        String s = new String(getInputChars(), startOffset, endOffset - startOffset + 1);
        System.out.print(s);
    }

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

    public void ruleAction( int ruleNumber)
    {
        switch(ruleNumber)
        {
 
            //
            // Rule 1:  Token ::= Identifier
            //
            case 1: { 
                checkForKeyWord();
                break;
            }
     
            //
            // Rule 2:  Token ::= DoubleQuote SLBody DoubleQuote
            //
            case 2: { 
                makeToken(TK_StringLiteral);
                break;
            }
     
            //
            // Rule 3:  Token ::= SingleQuote NotSQ SingleQuote
            //
            case 3: { 
                makeToken(TK_CharacterLiteral);
                break;
            }
     
            //
            // Rule 4:  Token ::= IntegerLiteral
            //
            case 4: { 
                makeToken(TK_IntegerLiteral);
                break;
            }
     
            //
            // Rule 5:  Token ::= FloatingPointLiteral
            //
            case 5: { 
                makeToken(TK_FloatingPointLiteral);
                break;
            }
     
            //
            // Rule 6:  Token ::= DoubleLiteral
            //
            case 6: { 
                makeToken(TK_DoubleLiteral);
                break;
            }
     
            //
            // Rule 7:  Token ::= Slash Star Inside Stars Slash
            //
            case 7: { 
                if (getKind(lexParser.getFirstToken(3)) == Char_Star && getKind(getNext(lexParser.getFirstToken(3))) != Char_Star)
                    makeDocComment();
                break;
            }
     
            //
            // Rule 8:  Token ::= SLC
            //
            case 8: { 
                skipToken();
                break;
            }
     
            //
            // Rule 9:  Token ::= WS
            //
            case 9: { 
                skipToken();
                break;
            }
     
            //
            // Rule 10:  Token ::= Plus
            //
            case 10: { 
                makeToken(TK_PLUS);
                break;
            }
     
            //
            // Rule 11:  Token ::= Minus
            //
            case 11: { 
                makeToken(TK_MINUS);
                break;
            }
     
            //
            // Rule 12:  Token ::= Star
            //
            case 12: { 
                makeToken(TK_MULTIPLY);
                break;
            }
     
            //
            // Rule 13:  Token ::= Slash
            //
            case 13: { 
                makeToken(TK_DIVIDE);
                break;
            }
     
            //
            // Rule 14:  Token ::= LeftParen
            //
            case 14: { 
                makeToken(TK_LPAREN);
                break;
            }
     
            //
            // Rule 15:  Token ::= RightParen
            //
            case 15: { 
                makeToken(TK_RPAREN);
                break;
            }
     
            //
            // Rule 16:  Token ::= Equal
            //
            case 16: { 
                makeToken(TK_EQUAL);
                break;
            }
     
            //
            // Rule 17:  Token ::= Comma
            //
            case 17: { 
                makeToken(TK_COMMA);
                break;
            }
     
            //
            // Rule 18:  Token ::= Colon
            //
            case 18: { 
                makeToken(TK_COLON);
                break;
            }
     
            //
            // Rule 19:  Token ::= SemiColon
            //
            case 19: { 
                makeToken(TK_SEMICOLON);
                break;
            }
     
            //
            // Rule 20:  Token ::= Caret
            //
            case 20: { 
                makeToken(TK_XOR);
                break;
            }
     
            //
            // Rule 21:  Token ::= Percent
            //
            case 21: { 
                makeToken(TK_REMAINDER);
                break;
            }
     
            //
            // Rule 22:  Token ::= Tilde
            //
            case 22: { 
                makeToken(TK_TWIDDLE);
                break;
            }
     
            //
            // Rule 23:  Token ::= VerticalBar
            //
            case 23: { 
                makeToken(TK_OR);
                break;
            }
     
            //
            // Rule 24:  Token ::= Ampersand
            //
            case 24: { 
                makeToken(TK_AND);
                break;
            }
     
            //
            // Rule 25:  Token ::= LessThan
            //
            case 25: { 
                makeToken(TK_LESS);
                break;
            }
     
            //
            // Rule 26:  Token ::= GreaterThan
            //
            case 26: { 
                makeToken(TK_GREATER);
                break;
            }
     
            //
            // Rule 27:  Token ::= Dot
            //
            case 27: { 
                makeToken(TK_DOT);
                break;
            }
     
            //
            // Rule 28:  Token ::= Exclamation
            //
            case 28: { 
                makeToken(TK_NOT);
                break;
            }
     
            //
            // Rule 29:  Token ::= LeftBracket
            //
            case 29: { 
                makeToken(TK_LBRACKET);
                break;
            }
     
            //
            // Rule 30:  Token ::= RightBracket
            //
            case 30: { 
                makeToken(TK_RBRACKET);
                break;
            }
     
            //
            // Rule 31:  Token ::= LeftBrace
            //
            case 31: { 
                makeToken(TK_LBRACE);
                break;
            }
     
            //
            // Rule 32:  Token ::= RightBrace
            //
            case 32: { 
                makeToken(TK_RBRACE);
                break;
            }
     
            //
            // Rule 33:  Token ::= QuestionMark
            //
            case 33: { 
                makeToken(TK_QUESTION);
                break;
            }
     
            //
            // Rule 34:  Token ::= AtSign
            //
            case 34: { 
                makeToken(TK_AT);
                break;
            }
     
            //
            // Rule 35:  Token ::= Plus Plus
            //
            case 35: { 
                makeToken(TK_PLUS_PLUS);
                break;
            }
     
            //
            // Rule 36:  Token ::= Minus Minus
            //
            case 36: { 
                makeToken(TK_MINUS_MINUS);
                break;
            }
     
            //
            // Rule 37:  Token ::= Equal Equal
            //
            case 37: { 
                makeToken(TK_EQUAL_EQUAL);
                break;
            }
     
            //
            // Rule 38:  Token ::= LessThan Equal
            //
            case 38: { 
                makeToken(TK_LESS_EQUAL);
                break;
            }
     
            //
            // Rule 39:  Token ::= Exclamation Equal
            //
            case 39: { 
                makeToken(TK_NOT_EQUAL);
                break;
            }
     
            //
            // Rule 40:  Token ::= LessThan LessThan
            //
            case 40: { 
                makeToken(TK_LEFT_SHIFT);
                break;
            }
     
            //
            // Rule 41:  Token ::= Plus Equal
            //
            case 41: { 
                makeToken(TK_PLUS_EQUAL);
                break;
            }
     
            //
            // Rule 42:  Token ::= Minus Equal
            //
            case 42: { 
                makeToken(TK_MINUS_EQUAL);
                break;
            }
     
            //
            // Rule 43:  Token ::= Star Equal
            //
            case 43: { 
                makeToken(TK_MULTIPLY_EQUAL);
                break;
            }
     
            //
            // Rule 44:  Token ::= Slash Equal
            //
            case 44: { 
                makeToken(TK_DIVIDE_EQUAL);
                break;
            }
     
            //
            // Rule 45:  Token ::= Ampersand Equal
            //
            case 45: { 
                makeToken(TK_AND_EQUAL);
                break;
            }
     
            //
            // Rule 46:  Token ::= VerticalBar Equal
            //
            case 46: { 
                makeToken(TK_OR_EQUAL);
                break;
            }
     
            //
            // Rule 47:  Token ::= Caret Equal
            //
            case 47: { 
                makeToken(TK_XOR_EQUAL);
                break;
            }
     
            //
            // Rule 48:  Token ::= Percent Equal
            //
            case 48: { 
                makeToken(TK_REMAINDER_EQUAL);
                break;
            }
     
            //
            // Rule 49:  Token ::= LessThan LessThan Equal
            //
            case 49: { 
                makeToken(TK_LEFT_SHIFT_EQUAL);
                break;
            }
     
            //
            // Rule 50:  Token ::= VerticalBar VerticalBar
            //
            case 50: { 
                makeToken(TK_OR_OR);
                break;
            }
     
            //
            // Rule 51:  Token ::= Ampersand Ampersand
            //
            case 51: { 
                makeToken(TK_AND_AND);
                break;
            }
     
            //
            // Rule 52:  Token ::= Dot Dot Dot
            //
            case 52: { 
                makeToken(TK_ELLIPSIS);
                break;
            }
     
            //
            // Rule 354:  Token ::= Dot Dot
            //
            case 354: { 
                 makeToken(TK_RANGE);
                 break;
            }
      
            //
            // Rule 355:  Token ::= Minus GreaterThan
            //
            case 355: { 
                makeToken(TK_ARROW);
                break;
            }
     
            //
            // Rule 356:  IntLiteralAndRange ::= Integer Dot Dot
            //
            case 356: { 
                makeToken(lexParser.getToken(1), lexParser.getLastToken(1), TK_IntegerLiteral);
                makeToken(lexParser.getToken(2), lexParser.getToken(3), TK_RANGE);
                break;
            }
        
            default:
                break;
        }
        return;
    }
}

