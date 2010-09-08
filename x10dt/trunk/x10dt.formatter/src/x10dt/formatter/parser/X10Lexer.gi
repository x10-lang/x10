--
-- The X10 Lexer
--
%Options la=2,list
%Options fp=X10Lexer
%options single_productions
%options package=x10.parser
%options template=LexerTemplateF.gi
%options filter=X10KWLexer.gi

%Notice
/.
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006-2008
//
./
%End

%Globals
    /.import java.util.*;./
%End

%Include
    LexerBasicMapF.gi
%End

%Define
    --
    -- Definition of macro used in the included file LexerBasicMapB.g
    --
    $kw_lexer_class /.$X10KWLexer./
%End

%Headers
    --
    -- Additional methods for the action class not provided in the template
    --
    /.
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

        public $action_type(java.io.Reader reader, String filename) throws java.io.IOException
        {
            this(reader, filename, ECLIPSE_TAB_VALUE);
        }

        public $action_type(java.io.Reader reader, String filename, int tab) throws java.io.IOException
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
            kwLexer = new $kw_lexer_class(lexStream.getInputChars(), $_IDENTIFIER);
        }
    ./
%End

%Export
    RANGE
    ARROW
    DARROW
    SUBTYPE
    SUPERTYPE

    IDENTIFIER

    SlComment
    MlComment
    DocComment
    IntegerLiteral
    LongLiteral
    FloatingPointLiteral
    DoubleLiteral
    CharacterLiteral
    StringLiteral
    PLUS_PLUS
    MINUS_MINUS
    EQUAL_EQUAL
    LESS_EQUAL
    GREATER_EQUAL
    NOT_EQUAL
    LEFT_SHIFT
    RIGHT_SHIFT
    UNSIGNED_RIGHT_SHIFT
    PLUS_EQUAL
    MINUS_EQUAL
    MULTIPLY_EQUAL
    DIVIDE_EQUAL
    AND_EQUAL
    OR_EQUAL
    XOR_EQUAL
    REMAINDER_EQUAL
    LEFT_SHIFT_EQUAL
    RIGHT_SHIFT_EQUAL
    UNSIGNED_RIGHT_SHIFT_EQUAL
    OR_OR
    AND_AND
    PLUS
    MINUS
    NOT
    REMAINDER
    XOR
    AND
    MULTIPLY
    OR
    TWIDDLE
    DIVIDE
    GREATER
    LESS
    LPAREN
    RPAREN
    LBRACE
    RBRACE
    LBRACKET
    RBRACKET
    SEMICOLON
    QUESTION
    AT
    COLON
    COMMA
    DOT
    EQUAL
    ELLIPSIS

%End

%Terminals
    CtlCharNotWS

    LF   CR   HT   FF

    a    b    c    d    e    f    g    h    i    j    k    l    m
    n    o    p    q    r    s    t    u    v    w    x    y    z
    _

    A    B    C    D    E    F    G    H    I    J    K    L    M
    N    O    P    Q    R    S    T    U    V    W    X    Y    Z

    0    1    2    3    4    5    6    7    8    9

    AfterASCII   ::= '\u0080..\ufffe'
    Space        ::= ' '
    LF           ::= NewLine
    CR           ::= Return
    HT           ::= HorizontalTab
    FF           ::= FormFeed
    DoubleQuote  ::= '"'
    SingleQuote  ::= "'"
    Percent      ::= '%'
    VerticalBar  ::= '|'
    Exclamation  ::= '!'
    AtSign       ::= '@'
    BackQuote    ::= '`'
    Tilde        ::= '~'
    Sharp        ::= '#'
    DollarSign   ::= '$'
    Ampersand    ::= '&'
    Caret        ::= '^'
    Colon        ::= ':'
    SemiColon    ::= ';'
    BackSlash    ::= '\'
    LeftBrace    ::= '{'
    RightBrace   ::= '}'
    LeftBracket  ::= '['
    RightBracket ::= ']'
    QuestionMark ::= '?'
    Comma        ::= ','
    Dot          ::= '.'
    LessThan     ::= '<'
    GreaterThan  ::= '>'
    Plus         ::= '+'
    Minus        ::= '-'
    Slash        ::= '/'
    Star         ::= '*'
    LeftParen    ::= '('
    RightParen   ::= ')'
    Equal        ::= '='

%End

%Rules

    Token ::= Identifier
        /.$BeginAction
                    checkForX10KeyWord();
          $EndAction
        ./
    Token ::= '"' SLBody '"'
        /.$BeginAction
                    makeToken($_StringLiteral);
          $EndAction
        ./
    Token ::= "'" NotSQ "'"
        /.$BeginAction
                    makeToken($_CharacterLiteral);
          $EndAction
        ./
    Token ::= IntegerLiteral
        /.$BeginAction
                    makeToken($_IntegerLiteral);
          $EndAction
        ./
    Token ::= LongLiteral
        /.$BeginAction
                    makeToken($_LongLiteral);
          $EndAction
        ./
    Token ::= FloatingPointLiteral
        /.$BeginAction
                    makeToken($_FloatingPointLiteral);
          $EndAction
        ./
    Token ::= DoubleLiteral
        /.$BeginAction
                    makeToken($_DoubleLiteral);
          $EndAction
        ./

    Token ::= MultiLineComment

    Token ::= SingleLineComment

    Token ::= WS -- White Space is scanned but not added to output vector
        /.$BeginAction
                    skipToken();
          $EndAction
        ./
    Token ::= '+'
        /.$BeginAction
                    makeToken($_PLUS);
          $EndAction
        ./
    Token ::= '-'
        /.$BeginAction
                    makeToken($_MINUS);
          $EndAction
        ./

    Token ::= '*'
        /.$BeginAction
                    makeToken($_MULTIPLY);
          $EndAction
        ./

    Token ::= '/'
        /.$BeginAction
                    makeToken($_DIVIDE);
          $EndAction
        ./

    Token ::= '('
        /.$BeginAction
                    makeToken($_LPAREN);
          $EndAction
        ./

    Token ::= ')'
        /.$BeginAction
                    makeToken($_RPAREN);
          $EndAction
        ./

    Token ::= '='
        /.$BeginAction
                    makeToken($_EQUAL);
          $EndAction
        ./

    Token ::= ','
        /.$BeginAction
                    makeToken($_COMMA);
          $EndAction
        ./

    Token ::= ':'
        /.$BeginAction
                    makeToken($_COLON);
          $EndAction
        ./

    Token ::= ';'
        /.$BeginAction
                    makeToken($_SEMICOLON);
          $EndAction
        ./

    Token ::= '^'
        /.$BeginAction
                    makeToken($_XOR);
          $EndAction
        ./

    Token ::= '%'
        /.$BeginAction
                    makeToken($_REMAINDER);
          $EndAction
        ./

    Token ::= '~'
        /.$BeginAction
                    makeToken($_TWIDDLE);
          $EndAction
        ./

    Token ::= '|'
        /.$BeginAction
                    makeToken($_OR);
          $EndAction
        ./

    Token ::= '&'
        /.$BeginAction
                    makeToken($_AND);
          $EndAction
        ./

    Token ::= '<'
        /.$BeginAction
                    makeToken($_LESS);
          $EndAction
        ./

    Token ::= '>'
        /.$BeginAction
                    makeToken($_GREATER);
          $EndAction
        ./

    Token ::= '.'
        /.$BeginAction
                    makeToken($_DOT);
          $EndAction
        ./

    Token ::= '!'
        /.$BeginAction
                    makeToken($_NOT);
          $EndAction
        ./

    Token ::= '['
        /.$BeginAction
                    makeToken($_LBRACKET);
          $EndAction
        ./

    Token ::= ']'
        /.$BeginAction
                    makeToken($_RBRACKET);
          $EndAction
        ./

    Token ::= '{'
        /.$BeginAction
                    makeToken($_LBRACE);
          $EndAction
        ./

    Token ::= '}'
        /.$BeginAction
                    makeToken($_RBRACE);
          $EndAction
        ./

    Token ::= '?'
        /.$BeginAction
                    makeToken($_QUESTION);
          $EndAction
        ./

    Token ::= '@'
        /.$BeginAction
                    makeToken($_AT);
          $EndAction
        ./

    Token ::= '+' '+'
        /.$BeginAction
                    makeToken($_PLUS_PLUS);
          $EndAction
        ./

    Token ::= '-' '-'
        /.$BeginAction
                    makeToken($_MINUS_MINUS);
          $EndAction
        ./

    Token ::= '=' '='
        /.$BeginAction
                    makeToken($_EQUAL_EQUAL);
          $EndAction
        ./

    Token ::= '<' '='
        /.$BeginAction
                    makeToken($_LESS_EQUAL);
          $EndAction
        ./
        
    Token ::= '>' '='
        /.$BeginAction
                    makeToken($_GREATER_EQUAL);
          $EndAction
        ./

    Token ::= '!' '='
        /.$BeginAction
                    makeToken($_NOT_EQUAL);
          $EndAction
        ./

    Token ::= '<' '<'
        /.$BeginAction
                    makeToken($_LEFT_SHIFT);
          $EndAction
        ./
    Token ::= '>' '>'
        /.$BeginAction
                    makeToken($_RIGHT_SHIFT);
          $EndAction
        ./
    Token ::= '>' '>' '>'
        /.$BeginAction
                    makeToken($_UNSIGNED_RIGHT_SHIFT);
          $EndAction
        ./

    Token ::= '+' '='
        /.$BeginAction
                    makeToken($_PLUS_EQUAL);
          $EndAction
        ./

    Token ::= '-' '='
        /.$BeginAction
                    makeToken($_MINUS_EQUAL);
          $EndAction
        ./

    Token ::= '*' '='
        /.$BeginAction
                    makeToken($_MULTIPLY_EQUAL);
          $EndAction
        ./

    Token ::= '/' '='
        /.$BeginAction
                    makeToken($_DIVIDE_EQUAL);
          $EndAction
        ./

    Token ::= '&' '='
        /.$BeginAction
                    makeToken($_AND_EQUAL);
          $EndAction
        ./

    Token ::= '|' '='
        /.$BeginAction
                    makeToken($_OR_EQUAL);
          $EndAction
        ./

    Token ::= '^' '='
        /.$BeginAction
                    makeToken($_XOR_EQUAL);
          $EndAction
        ./

    Token ::= '%' '='
        /.$BeginAction
                    makeToken($_REMAINDER_EQUAL);
          $EndAction
        ./

    Token ::= '<' '<' '='
        /.$BeginAction
                    makeToken($_LEFT_SHIFT_EQUAL);
          $EndAction
        ./
    Token ::= '>' '>' '='
        /.$BeginAction
                    makeToken($_RIGHT_SHIFT_EQUAL);
          $EndAction
        ./
    Token ::= '>' '>' '>' '='
        /.$BeginAction
                    makeToken($_UNSIGNED_RIGHT_SHIFT_EQUAL);
          $EndAction
        ./

    Token ::= '|' '|'
        /.$BeginAction
                    makeToken($_OR_OR);
          $EndAction
        ./

    Token ::= '&' '&'
        /.$BeginAction
                    makeToken($_AND_AND);
          $EndAction
        ./

    Token ::= '.' '.' '.'
        /.$BeginAction
                    makeToken($_ELLIPSIS);
          $EndAction
        ./

    IntegerLiteral -> Integer
                    | '0' LetterXx HexDigits

    LongLiteral ::= IntegerLiteral LetterLl

    FloatingPointLiteral -> Decimal LetterFf
                          | Decimal Exponent LetterFf
                          | Integer Exponent LetterFf
                          | Integer LetterFf

    DoubleLiteral -> Decimal
                   | Decimal Exponent
                   | Integer Exponent
                   | Decimal LetterDd
                   | Decimal Exponent LetterDd
                   | Integer Exponent LetterDd
                   | Integer LetterDd

    MultiLineComment ::= '/' '*' Inside Stars '/'
        /.$BeginAction
                    if (lexStream.getKind(getRhsFirstTokenIndex(3)) == X10Lexersym.Char_Star && lexStream.getKind(lexStream.getNext(getRhsFirstTokenIndex(3))) != X10Lexersym.Char_Star)
                         makeComment($_DocComment);
                    else makeComment($_MlComment);
          $EndAction
        ./

    Inside ::= Inside Stars NotSlashOrStar
             | Inside '/'
             | Inside NotSlashOrStar
             | %empty

    Stars -> '*'
           | Stars '*'

    SingleLineComment ::= SLC
        /.$BeginAction
                    makeComment($_SlComment);
          $EndAction
        ./

    SLC ::= '/' '/'
          | SLC NotEol

    SLBody -> %empty
            | SLBody NotDQ

    Integer -> Digit
             | Integer Digit

    HexDigits -> HexDigit
               | HexDigits HexDigit

    Decimal ::= '.' Integer
              | Integer '.'
              | Integer '.' Integer

    Exponent ::= LetterEe Integer
               | LetterEe '+' Integer
               | LetterEe '-' Integer

    WSChar -> Space
            | LF
            | CR
            | HT
            | FF

    Letter -> LowerCaseLetter
            | UpperCaseLetter
            | _
            | '$'
            | '\u0080..\ufffe'

    LowerCaseLetter -> a | b | c | d | e | f | g | h | i | j | k | l | m |
                       n | o | p | q | r | s | t | u | v | w | x | y | z

    UpperCaseLetter -> A | B | C | D | E | F | G | H | I | J | K | L | M |
                       N | O | P | Q | R | S | T | U | V | W | X | Y | Z

    Digit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

    OctalDigit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7

    a..f -> a | b | c | d | e | f | A | B | C | D | E | F

    HexDigit -> Digit
              | a..f

    OctalDigits3 -> OctalDigit
                  | OctalDigit OctalDigit
                  | OctalDigit OctalDigit OctalDigit

    LetterFf -> 'F'
              | 'f'

    LetterDd -> 'D'
              | 'd'

    LetterLl ->  'L'
              | 'l'

    LetterEe -> 'E'
              | 'e'

    LetterXx -> 'X'
              | 'x'

    WS -> WSChar
        | WS WSChar

    Identifier -> Letter
                | Identifier Letter
                | Identifier Digit

    SpecialNotStar -> '+' | '-' | '/' | '(' | ')' | '"' | '!' | '@' | '`' | '~' |
                      '%' | '&' | '^' | ':' | ';' | "'" | '\' | '|' | '{' | '}' |
                      '[' | ']' | '?' | ',' | '.' | '<' | '>' | '=' | '#'

    SpecialNotSlash -> '+' | '-' | -- exclude the star as well
                       '(' | ')' | '"' | '!' | '@' | '`' | '~' |
                       '%' | '&' | '^' | ':' | ';' | "'" | '\' | '|' | '{' | '}' |
                       '[' | ']' | '?' | ',' | '.' | '<' | '>' | '=' | '#'

    SpecialNotDQ -> '+' | '-' | '/' | '(' | ')' | '*' | '!' | '@' | '`' | '~' |
                    '%' | '&' | '^' | ':' | ';' | "'" | '|' | '{' | '}' |
                    '[' | ']' | '?' | ',' | '.' | '<' | '>' | '=' | '#'

    SpecialNotSQ -> '+' | '-' | '*' | '(' | ')' | '"' | '!' | '@' | '`' | '~' |
                    '%' | '&' | '^' | ':' | ';' | '/' | '|' | '{' | '}' |
                    '[' | ']' | '?' | ',' | '.' | '<' | '>' | '=' | '#'

    NotSlashOrStar -> Letter
                    | Digit
                    | SpecialNotSlash
                    | WSChar

    Eol -> LF
         | CR

    NotEol -> Letter
            | Digit
            | Space
            | '*'
            | SpecialNotStar
            | HT
            | FF
            | CtlCharNotWS

    NotDQ -> Letter
           | Digit
           | SpecialNotDQ
           | Space
           | HT
           | FF
           | EscapeSequence
           | '\' u HexDigit HexDigit HexDigit HexDigit
           | '\' OctalDigit

    NotSQ -> Letter
           | Digit
           | SpecialNotSQ
           | Space
           | HT
           | FF
           | EscapeSequence
           | '\' u HexDigit HexDigit HexDigit HexDigit
           | '\' OctalDigits3

    EscapeSequence -> '\' b
                    | '\' t
                    | '\' n
                    | '\' f
                    | '\' r
                    | '\' '"'
                    | '\' "'"
                    | '\' '\'

     --- X10 Tokens

     Token ::= IntLiteralAndRange
 
     Token ::= '.' '.'
          /.$BeginAction
                      makeToken($_RANGE);
            $EndAction
          ./
 

    Token ::= '-' '>'
        /.$BeginAction
                    makeToken($_ARROW);
          $EndAction
        ./

    Token ::= '=' '>'
        /.$BeginAction
                    makeToken($_DARROW);
          $EndAction
        ./

    Token ::= '<' ':'
        /.$BeginAction
                    makeToken($_SUBTYPE);
          $EndAction
        ./
        
    Token ::= ':' '>'
        /.$BeginAction
                    makeToken($_SUPERTYPE);
          $EndAction
        ./

    IntLiteralAndRange ::= Integer '.' '.'
         /.$BeginAction
                     makeToken(getToken(1), getToken(1), $_IntegerLiteral);
                     makeToken(getToken(2), getToken(3), $_RANGE);
           $EndAction
         ./
%End

