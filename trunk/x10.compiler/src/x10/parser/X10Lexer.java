


package x10.parser;

import java.util.*;
import com.ibm.lpg.*;

public class X10Lexer implements RuleAction, X10Parsersym
{
    X10LexerStream lexStream;
    PrsStream prsStream;
    ParseTable prs;
    LexParser lexParser;

    public X10Lexer(X10LexerStream lexStream)
    {
	this.lexStream = lexStream;
	this.prsStream = new PrsStream(lexStream);
	this.prs = new X10Lexerprs();
	this.lexParser = new LexParser((TokenStream)lexStream, prs, (RuleAction)this);
    }

    public PrsStream lexer()
    {
        IToken badToken = new Token(0, 0, 0);
        prsStream.addToken(badToken);

    	lexParser.parseCharacters();

	int i = lexStream.getStreamIndex();
	IToken eofToken = new Token(i, i, TK_EOF_TOKEN);
	prsStream.addToken(eofToken);
	prsStream.setSize();

	return prsStream;
    }

    X10KWLexer kwLexer;

    public PrsStream x10Lexer()
    {
        kwLexer = new X10KWLexer(lexStream.getInputChars());
	return lexer();
    }

    void makeToken(int startOffset, int endOffset, int kind)
    {
	Token t  = new Token(startOffset, endOffset, kind);
	prsStream.addToken(t);
	printValue(t);
    }

    void skipToken(int startOffset, int endOffset)
    {
	Token t  = new Token(startOffset, endOffset, 0);
	printValue(t);
    }

    void checkForKeyWord(int startOffset, int endOffset, int kind)
    {
	int kwKind = kwLexer.lexer(startOffset, endOffset);
	Token t  = new Token(startOffset, endOffset, (kwKind != 0 ? kwKind : kind));
	prsStream.addToken(t);
	printValue(t);
    }

    void singleCharOperator(int kind)
    {
	Token t  = new Token(lexParser.getToken(1), lexParser.getToken(1), kind);
	prsStream.addToken(t);
	printValue(t);
    }

    void doubleCharOperator(int kind)
    {
	Token t  = new Token(lexParser.getToken(1), lexParser.getToken(2), kind);
	prsStream.addToken(t);
	printValue(t);
    }

    void printValue( Token t )
    {
	if (lexStream.printTokens())
	{
		System.out.print(t.getValue(lexStream.getInputChars()));
	}
    }


    public void ruleAction( int ruleNumber)
    {
	switch(ruleNumber)
	{

 
    //
    // Rule 3:  Token ::= Identifier
    //
   	    case 3:
               lexParser.resetStateStack();
               break;  
 
    //
    // Rule 4:  Token ::= StringLiteral
    //
   	    case 4:
               lexParser.resetStateStack();
               break;  
 
    //
    // Rule 5:  Token ::= CharLiteral
    //
   	    case 5:
               lexParser.resetStateStack();
               break;  
 
    //
    // Rule 6:  Token ::= IntegerLiteral
    //
   	    case 6:
               lexParser.resetStateStack();
               break;  
 
    //
    // Rule 7:  Token ::= FloatingPointLiteral
    //
   	    case 7:
               lexParser.resetStateStack();
               break;  
 
    //
    // Rule 8:  Token ::= MLComment
    //
   	    case 8:
               lexParser.resetStateStack();
               break;  
 
    //
    // Rule 9:  Token ::= SLComment
    //
   	    case 9:
               lexParser.resetStateStack();
               break;  
 
    //
    // Rule 10:  Token ::= Operator
    //
   	    case 10:
               lexParser.resetStateStack();
               break;  
 
    //
    // Rule 11:  Token ::= WhiteSpace
    //
   	    case 11:
               lexParser.resetStateStack();
               break;  
 
    //
    // Rule 15:  ID ::= Ident
    //
            case 15:
            { 
	        checkForKeyWord(lexParser.getToken(1), lexParser.getSym(1), TK_Identifier);
	  
            }
            break; 
	 
    //
    // Rule 16:  WhiteSpace ::= WS
    //
            case 16:
            { 
	        skipToken(lexParser.getToken(1), lexParser.getSym(1));
	  
            }
            break; 
	 
    //
    // Rule 17:  WS ::= WSChar
    //
            case 17:
            { 
	        lexParser.setSym1(lexParser.getToken(1));
	  
            }
            break; 
	 
    //
    // Rule 18:  WS ::= WS WSChar
    //
            case 18:
            { 
	        lexParser.setSym1(lexParser.getToken(2));
	  
            }
            break; 
	 
    //
    // Rule 19:  Ident ::= Letter
    //
            case 19:
            { 
	        lexParser.setSym1(lexParser.getToken(1));
	  
            }
            break; 
	 
    //
    // Rule 20:  Ident ::= Ident Letter
    //
            case 20:
            { 
	        lexParser.setSym1(lexParser.getToken(2));
	  
            }
            break; 
	 
    //
    // Rule 21:  Ident ::= Ident Digit
    //
            case 21:
            { 
	        lexParser.setSym1(lexParser.getToken(2));
	  
            }
            break; 
	 
    //
    // Rule 22:  StringLiteral ::= DoubleQuote SLBody DoubleQuote
    //
            case 22:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getToken(3), TK_StringLiteral);
	  
            }
            break; 
	 
    //
    // Rule 23:  StringLiteral ::= DoubleQuote DoubleQuote
    //
            case 23:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getToken(2), TK_StringLiteral);
	  
            }
            break; 
	 
    //
    // Rule 24:  CharLiteral ::= SingleQuote NotSQ SingleQuote
    //
            case 24:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getToken(3), TK_CharacterLiteral);
	  
            }
            break; 
	 
    //
    // Rule 29:  IntLiteral ::= Integer
    //
            case 29:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getSym(1), TK_IntegerLiteral);
	  
            }
            break; 
	 
    //
    // Rule 30:  IntLiteral ::= Integer LetterLl
    //
            case 30:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getToken(2), TK_IntegerLiteral);
	  
            }
            break; 
	 
    //
    // Rule 31:  Integer ::= Digit
    //
            case 31:
            { 
	        lexParser.setSym1(lexParser.getToken(1));
	  
            }
            break; 
	 
    //
    // Rule 32:  Integer ::= Integer Digit
    //
            case 32:
            { 
	        lexParser.setSym1(lexParser.getToken(2));
	  
            }
            break; 
	 
    //
    // Rule 33:  HexLiteral ::= 0 LetterXx HexDigits
    //
            case 33:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getSym(3), TK_IntegerLiteral);
	  
            }
            break; 
	 
    //
    // Rule 34:  HexLiteral ::= 0 LetterXx HexDigits LetterLl
    //
            case 34:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getToken(4), TK_IntegerLiteral);
	  
            }
            break; 
	 
    //
    // Rule 35:  HexDigits ::= HexDigit
    //
            case 35:
            { 
	        lexParser.setSym1(lexParser.getToken(1));
	  
            }
            break; 
	 
    //
    // Rule 36:  HexDigits ::= HexDigits HexDigit
    //
            case 36:
            { 
	        lexParser.setSym1(lexParser.getToken(2));
	  
            }
            break; 
	 
    //
    // Rule 37:  FloatingPointLiteral ::= Decimal
    //
            case 37:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getSym(1), TK_FloatingPointLiteral);
	  
            }
            break; 
	 
    //
    // Rule 38:  FloatingPointLiteral ::= Decimal LetterForD
    //
            case 38:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getToken(2), TK_FloatingPointLiteral);
	  
            }
            break; 
	 
    //
    // Rule 39:  FloatingPointLiteral ::= Decimal Exponent
    //
            case 39:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getSym(2), TK_FloatingPointLiteral);
	  
            }
            break; 
	 
    //
    // Rule 40:  FloatingPointLiteral ::= Decimal Exponent LetterForD
    //
            case 40:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getToken(3), TK_FloatingPointLiteral);
	  
            }
            break; 
	 
    //
    // Rule 41:  FloatingPointLiteral ::= Integer Exponent
    //
            case 41:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getSym(2), TK_FloatingPointLiteral);
	  
            }
            break; 
	 
    //
    // Rule 42:  FloatingPointLiteral ::= Integer Exponent LetterForD
    //
            case 42:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getToken(3), TK_FloatingPointLiteral);
	  
            }
            break; 
	 
    //
    // Rule 43:  FloatingPointLiteral ::= Integer LetterForD
    //
            case 43:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getToken(2), TK_FloatingPointLiteral);
	  
            }
            break; 
	 
    //
    // Rule 44:  Decimal ::= Dot Integer
    //
            case 44:
            { 
                lexParser.setSym1(lexParser.getSym(2));
	  
            }
            break; 
	 
    //
    // Rule 45:  Decimal ::= Integer Dot
    //
            case 45:
            { 
                lexParser.setSym1(lexParser.getToken(2));
	  
            }
            break; 
	 
    //
    // Rule 46:  Decimal ::= Integer Dot Integer
    //
            case 46:
            { 
                lexParser.setSym1(lexParser.getSym(3));
	  
            }
            break; 
	 
    //
    // Rule 47:  Exponent ::= LetterEe Integer
    //
            case 47:
            { 
                lexParser.setSym1(lexParser.getSym(2));
	  
            }
            break; 
	 
    //
    // Rule 48:  Exponent ::= LetterEe Plus Integer
    //
            case 48:
            { 
               lexParser.setSym1(lexParser.getSym(3));
	  
            }
            break; 
	 
    //
    // Rule 49:  Exponent ::= LetterEe Minus Integer
    //
            case 49:
            { 
                lexParser.setSym1(lexParser.getSym(3));
	  
            }
            break; 
	 
    //
    // Rule 50:  MLComment ::= Slash Star Inside Stars Slash
    //
            case 50:
            { 
	        skipToken(lexParser.getToken(1), lexParser.getToken(5));
	  
            }
            break; 
	 
    //
    // Rule 57:  SLComment ::= SLC
    //
            case 57:
            { 
		skipToken(lexParser.getToken(1), lexParser.getSym(1));
	  
            }
            break; 
	 
    //
    // Rule 58:  SLC ::= Slash Slash
    //
            case 58:
            { 
		lexParser.setSym1(lexParser.getToken(2));
	  
            }
            break; 
	 
    //
    // Rule 59:  SLC ::= SLC NotEol
    //
            case 59:
            { 
		lexParser.setSym1(lexParser.getToken(2));
	  
            }
            break; 
	 
    //
    // Rule 171:  SSOperator ::= Plus
    //
            case 171:
            { 
                singleCharOperator(TK_PLUS);
	  
            }
            break; 
	 
    //
    // Rule 172:  SSOperator ::= Minus
    //
            case 172:
            { 
                singleCharOperator(TK_MINUS);
	  
            }
            break; 
	 
    //
    // Rule 173:  SSOperator ::= Star
    //
            case 173:
            { 
                singleCharOperator(TK_MULTIPLY);
	  
            }
            break; 
	 
    //
    // Rule 174:  SSOperator ::= Slash
    //
            case 174:
            { 
                singleCharOperator(TK_DIVIDE);
	  
            }
            break; 
	 
    //
    // Rule 175:  SSOperator ::= LeftParen
    //
            case 175:
            { 
                singleCharOperator(TK_LPAREN);
	  
            }
            break; 
	 
    //
    // Rule 176:  SSOperator ::= RightParen
    //
            case 176:
            { 
                singleCharOperator(TK_RPAREN);
	  
            }
            break; 
	 
    //
    // Rule 177:  SSOperator ::= Equal
    //
            case 177:
            { 
                singleCharOperator(TK_EQUAL);
	  
            }
            break; 
	 
    //
    // Rule 178:  SSOperator ::= Comma
    //
            case 178:
            { 
                singleCharOperator(TK_COMMA);
	  
            }
            break; 
	 
    //
    // Rule 179:  SSOperator ::= Colon
    //
            case 179:
            { 
                singleCharOperator(TK_COLON);
	  
            }
            break; 
	 
    //
    // Rule 180:  SSOperator ::= SemiColon
    //
            case 180:
            { 
                singleCharOperator(TK_SEMICOLON);
	  
            }
            break; 
	 
    //
    // Rule 181:  SSOperator ::= Caret
    //
            case 181:
            { 
                singleCharOperator(TK_XOR);
	  
            }
            break; 
	 
    //
    // Rule 182:  SSOperator ::= Percent
    //
            case 182:
            { 
                singleCharOperator(TK_REMAINDER);
	  
            }
            break; 
	 
    //
    // Rule 183:  SSOperator ::= Tilde
    //
            case 183:
            { 
                singleCharOperator(TK_TWIDDLE);
	  
            }
            break; 
	 
    //
    // Rule 184:  SSOperator ::= VerticalBar
    //
            case 184:
            { 
                singleCharOperator(TK_OR);
	  
            }
            break; 
	 
    //
    // Rule 185:  SSOperator ::= Ampersand
    //
            case 185:
            { 
                singleCharOperator(TK_AND);
	  
            }
            break; 
	 
    //
    // Rule 186:  SSOperator ::= LessThan
    //
            case 186:
            { 
                singleCharOperator(TK_LESS);
	  
            }
            break; 
	 
    //
    // Rule 187:  SSOperator ::= GreaterThan
    //
            case 187:
            { 
                singleCharOperator(TK_GREATER);
	  
            }
            break; 
	 
    //
    // Rule 188:  SSOperator ::= Dot
    //
            case 188:
            { 
                singleCharOperator(TK_DOT);
	  
            }
            break; 
	 
    //
    // Rule 189:  SSOperator ::= Exclamation
    //
            case 189:
            { 
                singleCharOperator(TK_NOT);
	  
            }
            break; 
	 
    //
    // Rule 190:  SSOperator ::= LeftBracket
    //
            case 190:
            { 
                singleCharOperator(TK_LBRACKET);
	  
            }
            break; 
	 
    //
    // Rule 191:  SSOperator ::= RightBracket
    //
            case 191:
            { 
                singleCharOperator(TK_RBRACKET);
	  
            }
            break; 
	 
    //
    // Rule 192:  SSOperator ::= LeftBrace
    //
            case 192:
            { 
                singleCharOperator(TK_LBRACE);
	  
            }
            break; 
	 
    //
    // Rule 193:  SSOperator ::= RightBrace
    //
            case 193:
            { 
                singleCharOperator(TK_RBRACE);
	  
            }
            break; 
	 
    //
    // Rule 194:  SSOperator ::= QuestionMark
    //
            case 194:
            { 
                singleCharOperator(TK_QUESTION);
	  
            }
            break; 
	 
    //
    // Rule 195:  SSOperator ::= AtSign
    //
            case 195:
            { 
                singleCharOperator(TK_AT);
	  
            }
            break; 
	 
    //
    // Rule 196:  MSOperator ::= Plus Plus
    //
            case 196:
            { 
                 doubleCharOperator(TK_PLUS_PLUS);
          
            }
            break; 
	 
    //
    // Rule 197:  MSOperator ::= Minus Minus
    //
            case 197:
            { 
                 doubleCharOperator(TK_MINUS_MINUS);
          
            }
            break; 
	 
    //
    // Rule 198:  MSOperator ::= Equal Equal
    //
            case 198:
            { 
                 doubleCharOperator(TK_EQUAL_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 199:  MSOperator ::= LessThan Equal
    //
            case 199:
            { 
                 doubleCharOperator(TK_LESS_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 200:  MSOperator ::= Exclamation Equal
    //
            case 200:
            { 
                 doubleCharOperator(TK_NOT_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 201:  MSOperator ::= LessThan LessThan
    //
            case 201:
            { 
                 doubleCharOperator(TK_LEFT_SHIFT);
          
            }
            break; 
	 
    //
    // Rule 202:  MSOperator ::= Plus Equal
    //
            case 202:
            { 
                 doubleCharOperator(TK_PLUS_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 203:  MSOperator ::= Minus Equal
    //
            case 203:
            { 
                 doubleCharOperator(TK_MINUS_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 204:  MSOperator ::= Star Equal
    //
            case 204:
            { 
                 doubleCharOperator(TK_MULTIPLY_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 205:  MSOperator ::= Slash Equal
    //
            case 205:
            { 
                 doubleCharOperator(TK_DIVIDE_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 206:  MSOperator ::= Ampersand Equal
    //
            case 206:
            { 
                 doubleCharOperator(TK_AND_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 207:  MSOperator ::= VerticalBar Equal
    //
            case 207:
            { 
                 doubleCharOperator(TK_OR_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 208:  MSOperator ::= Caret Equal
    //
            case 208:
            { 
                 doubleCharOperator(TK_XOR_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 209:  MSOperator ::= Percent Equal
    //
            case 209:
            { 
                 doubleCharOperator(TK_REMAINDER_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 210:  MSOperator ::= LessThan LessThan Equal
    //
            case 210:
            { 
                 makeToken(lexParser.getToken(1), lexParser.getToken(3), TK_LEFT_SHIFT_EQUAL);
          
            }
            break; 
	 
    //
    // Rule 211:  MSOperator ::= VerticalBar VerticalBar
    //
            case 211:
            { 
                 doubleCharOperator(TK_OR_OR);
          
            }
            break; 
	 
    //
    // Rule 212:  MSOperator ::= Ampersand Ampersand
    //
            case 212:
            { 
                 doubleCharOperator(TK_AND_AND);
          
            }
            break; 
	 
    //
    // Rule 213:  MSOperator ::= Dot Dot Dot
    //
            case 213:
            { 
                 makeToken(lexParser.getToken(1), lexParser.getToken(3), TK_ELLIPSIS);
          
            }
            break; 
	 
    //
    // Rule 363:  Token ::= IntLiteralAndRange
    //
   	    case 363:
               lexParser.resetStateStack();
               break;  
 
    //
    // Rule 364:  IntLiteralAndRange ::= Integer Dot Dot
    //
            case 364:
            { 
	        makeToken(lexParser.getToken(1), lexParser.getSym(1), TK_IntegerLiteral);
                makeToken(lexParser.getToken(2), lexParser.getToken(3), TK_RANGE);
	  
            }
            break; 
	 
    //
    // Rule 365:  MSOperator ::= Dot Dot
    //
            case 365:
            { 
                 makeToken(lexParser.getToken(1), lexParser.getToken(2), TK_RANGE);
          
            }
            break; 
	    
	    default:
	        break;
	}
	return;
    }

}

