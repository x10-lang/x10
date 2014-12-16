--
-- The X10 Lexer
--
%Options la=2,verbose
%Options fp=X10Lexer
%options single_productions
%options package=x10.parser
%options template=LexerTemplateF.gi
%options filter=X10KWLexer.gi

%Notice
    /./*
     *  This file is part of the X10 project (http://x10-lang.org).
     *
     *  This file is licensed to You under the Eclipse Public License (EPL);
     *  You may not use this file except in compliance with the License.
     *  You may obtain a copy of the License at
     *      http://www.opensource.org/licenses/eclipse-1.0.php
     *
     *  (C) Copyright IBM Corporation 2006-2014.
     */
    /**************************************************************************
     * WARNING!  THIS JAVA FILE IS AUTO-GENERATED FROM $input_file *
     **************************************************************************/
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
                if (token.getKind() == X10Parsersym.TK_DoubleLiteral ||
                    token.getKind() == X10Parsersym.TK_FloatingPointLiteral ||
                    token.getKind() == X10Parsersym.TK_PseudoDoubleLiteral)
                {
                    char[] input = lexStream.getInputChars();
                    int end = token.getEndOffset();
                    int dot = end;
                    boolean valid = true;
                    for (; dot > token.getStartOffset() && input[dot] != '.'; dot--)
                        if (!Character.isJavaIdentifierPart(input[dot]))
                            valid = false;
                    if (valid && dot > token.getStartOffset())
                    {
                        token.setEndOffset(dot - 1);
                        token.setKind(X10Parsersym.TK_LongLiteral);
                        lexStream.getIPrsStream().makeToken(dot, dot, X10Parsersym.TK_DOT);
                        if (dot < end)
                        {
                            if (startLoc == end + 1)
                            {
                                // No spaces -- merge in with the following identifier
                                startLoc = dot + 1;
                            }
                            else
                            {
                                lexStream.getIPrsStream().makeToken(dot + 1, end, X10Parsersym.TK_IDENTIFIER);
                            }
                        }
                    }
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

        final void makeQuotedIdentifier()
        {
            int startOffset = getLeftSpan()+1,
                endOffset = getRightSpan()-1;
            makeX10Token(startOffset, endOffset, $_IDENTIFIER);
            if (printTokens) printValue(startOffset, endOffset);
        }

        public $action_type(java.io.Reader reader, String filename) throws java.io.IOException
        {
            this(reader, filename, ECLIPSE_TAB_VALUE);
        }

        public $action_type(java.io.Reader reader, String filename, int tab) throws java.io.IOException
        {
            ArrayList<char[]> buffers = new ArrayList<char[]>();
            int size = 0;
            while (true)
            {
                char block[]= new char[8192];
                int n = reader.read(block, 0, block.length);
                if (n < 0)
                    break;
                size += n;
                buffers.add(block);
            }

            char buffer[] = new char[size];
            for (int i = 0; i < buffers.size(); i++)
            {
                char block[] = buffers.get(i);
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
    STARSTAR
    NTWIDDLE
    LARROW
    FUNNEL
    LFUNNEL
    DIAMOND
    BOWTIE

    RANGE_EQUAL
    ARROW_EQUAL
    STARSTAR_EQUAL
    TWIDDLE_EQUAL
    LARROW_EQUAL
    FUNNEL_EQUAL
    LFUNNEL_EQUAL
    DIAMOND_EQUAL
    BOWTIE_EQUAL

    IDENTIFIER

    SlComment
    MlComment
    DocComment
    IntLiteral
    LongLiteral
    ByteLiteral
    ShortLiteral
    UnsignedIntLiteral
    UnsignedLongLiteral
    UnsignedByteLiteral
    UnsignedShortLiteral
    FloatingPointLiteral
    DoubleLiteral
    PseudoDoubleLiteral
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
    Token ::= '`' QuotedIdentifierBody '`'
        /.$BeginAction
                    makeQuotedIdentifier();
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
    Token ::= IntLiteral
        /.$BeginAction
                    makeToken($_IntLiteral);
          $EndAction
        ./
    Token ::= LongLiteral
        /.$BeginAction
                    makeToken($_LongLiteral);
          $EndAction
        ./
    Token ::= ByteLiteral
        /.$BeginAction
                    makeToken($_ByteLiteral);
          $EndAction
        ./
    Token ::= ShortLiteral
        /.$BeginAction
                    makeToken($_ShortLiteral);
          $EndAction
        ./
    Token ::= UnsignedIntLiteral
        /.$BeginAction
                    makeToken($_UnsignedIntLiteral);
          $EndAction
        ./
    Token ::= UnsignedLongLiteral
        /.$BeginAction
                    makeToken($_UnsignedLongLiteral);
          $EndAction
        ./
    Token ::= UnsignedByteLiteral
        /.$BeginAction
                    makeToken($_UnsignedByteLiteral);
          $EndAction
        ./
    Token ::= UnsignedShortLiteral
        /.$BeginAction
                    makeToken($_UnsignedShortLiteral);
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
    Token ::= PseudoDoubleLiteral
        /.$BeginAction
                    makeToken($_PseudoDoubleLiteral);
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

    IntLiteral ::= IntegerLiteral LetterNn

    LongLiteral -> IntegerLiteral
                 | IntegerLiteral LetterLl

    ByteLiteral ::= IntegerLiteral LetterYy

    ShortLiteral ::= IntegerLiteral LetterSs
    
    UnsignedIntLiteral -> IntegerLiteral LetterUu LetterNn
                        | IntegerLiteral LetterNn LetterUu

    UnsignedLongLiteral -> IntegerLiteral LetterUu LetterLl
                         | IntegerLiteral LetterLl LetterUu
                         | IntegerLiteral LetterUu

    UnsignedByteLiteral -> IntegerLiteral LetterUu LetterYy
                         | IntegerLiteral LetterYy LetterUu

    UnsignedShortLiteral -> IntegerLiteral LetterUu LetterSs
                         | IntegerLiteral LetterSs LetterUu

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

    PseudoDoubleLiteral -> Decimal LetterEe

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

    QuotedIdentifierBody -> %empty
                          | QuotedIdentifierBody NotBQ

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

    LetterNn -> 'N'
              | 'n'

    LetterLl -> 'L'
              | 'l'

    LetterYy -> 'Y'
              | 'y'

    LetterSs -> 'S'
              | 's'

    LetterUu -> 'U'
              | 'u'

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

    SpecialNotDQ -> '+' | '-' | '*' | '(' | ')' | "'" | '!' | '@' | '`' | '~' |
                    '%' | '&' | '^' | ':' | ';' | '/' | '|' | '{' | '}' |
                    '[' | ']' | '?' | ',' | '.' | '<' | '>' | '=' | '#'

    SpecialNotSQ -> '+' | '-' | '*' | '(' | ')' | '"' | '!' | '@' | '`' | '~' |
                    '%' | '&' | '^' | ':' | ';' | '/' | '|' | '{' | '}' |
                    '[' | ']' | '?' | ',' | '.' | '<' | '>' | '=' | '#'

    SpecialNotBQ -> '+' | '-' | '*' | '(' | ')' | '"' | '!' | '@' | "'" | '~' |
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

    NotBQ -> Letter
           | Digit
           | SpecialNotBQ
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
                    | '\' '`'
                    | '\' '\'

     --- X10 Tokens

     Token ::= LongLiteralAndRange
 
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

    Token ::= '*' '*'
        /.$BeginAction
                    makeToken($_STARSTAR);
          $EndAction
        ./

    Token ::= '!' '~'
        /.$BeginAction
                    makeToken($_NTWIDDLE);
          $EndAction
        ./

    Token ::= '<' '-'
        /.$BeginAction
                    makeToken($_LARROW);
          $EndAction
        ./

    Token ::= '-' '<'
        /.$BeginAction
                    makeToken($_FUNNEL);
          $EndAction
        ./

    Token ::= '>' '-'
        /.$BeginAction
                    makeToken($_LFUNNEL);
          $EndAction
        ./

    Token ::= '<' '>'
        /.$BeginAction
                    makeToken($_DIAMOND);
          $EndAction
        ./

    Token ::= '>' '<'
        /.$BeginAction
                    makeToken($_BOWTIE);
          $EndAction
        ./

    Token ::= '.' '.' '='
        /.$BeginAction
                    makeToken($_RANGE_EQUAL);
          $EndAction
        ./
    Token ::= '-' '>' '='
        /.$BeginAction
                    makeToken($_ARROW_EQUAL);
          $EndAction
        ./
    Token ::= '*' '*' '='
        /.$BeginAction
                    makeToken($_STARSTAR_EQUAL);
          $EndAction
        ./
    Token ::= '~' '='
        /.$BeginAction
                    makeToken($_TWIDDLE_EQUAL);
          $EndAction
        ./
    Token ::= '<' '-' '='
        /.$BeginAction
                    makeToken($_LARROW_EQUAL);
          $EndAction
        ./
    Token ::= '-' '<' '='
        /.$BeginAction
                    makeToken($_FUNNEL_EQUAL);
          $EndAction
        ./
    Token ::= '>' '-' '='
        /.$BeginAction
                    makeToken($_LFUNNEL_EQUAL);
          $EndAction
        ./
    Token ::= '<' '>' '='
        /.$BeginAction
                    makeToken($_DIAMOND_EQUAL);
          $EndAction
        ./

    Token ::= '>' '<' '='
        /.$BeginAction
                    makeToken($_BOWTIE_EQUAL);
          $EndAction
        ./


    LongLiteralAndRange ::= Integer '.' '.'
         /.$BeginAction
                     makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), $_LongLiteral);
                     makeToken(getToken(2), getToken(3), $_RANGE);
           $EndAction
         ./
%End

