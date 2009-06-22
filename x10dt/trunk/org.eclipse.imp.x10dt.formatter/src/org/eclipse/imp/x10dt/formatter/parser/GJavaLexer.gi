--
-- The Java Lexer
--
%Options la=2,list
%Options fp=JavaLexer
%options single_productions
%options package=org.eclipse.imp.x10dt.formatter.parser
%options template=LexerTemplate.gi
%options filter=GJavaKWLexer.gi

%Include
    LexerBasicMapF.gi
%End



%Globals
    /.import java.util.*;./
%End
    
%Define
    --
    -- Definition of macro used in the included file LexerBasicMapB.g
    --
    $kw_lexer_class /.$GJavaKWLexer./

%End

%Export

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

%Notice
/.
////////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2007 IBM Corporation.
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
//Contributors:
//    Philippe Charles (pcharles@us.ibm.com) - initial API and implementation

////////////////////////////////////////////////////////////////////////////////
./
%End

%Rules

    Token ::= Identifier
        /.$BeginAction
                    checkForKeyWord();
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
                    makeComment($_MlComment);
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
%End
