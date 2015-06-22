/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
lexer grammar X10_Lexer;

@lexer::header {
  package x10.parser.antlr.generated;
}

@lexer::members {

  /** Hidden channel for white spaces */
  public static final int WHITESPACES = 1;
  /** Hidden channel for X10Doc comments */
  public static final int DOCCOMMENTS = 2;
  /** Hidden channel for simple comments */
  public static final int COMMENTS = 3;

  boolean isDecimal() {
      int next = _input.LA(1);
      return (next != '.') &&
          !('a' <= next && next <= 'z') &&
          !('A' <= next && next <= 'Z') &&
          (next != '_') && (next != '`');
      // return ('0' <= next && next <= '9') ||
      //     next == ' ' || next == '\t' || next == '\r' || next == '\n' || next == '\u000C';
  }
}

MINUS_MINUS: '--';
OR: '|';
MINUS: '-';
MINUS_EQUAL: '-=';
NOT: '!';
NOT_EQUAL: '!=';
REMAINDER: '%';
REMAINDER_EQUAL: '%=';
AND: '&';
AND_AND: '&&';
AND_EQUAL: '&=';
LPAREN: '(';
RPAREN: ')';
MULTIPLY: '*';
MULTIPLY_EQUAL: '*=';
COMMA: ',';
DOT: '.';
DIVIDE: '/';
DIVIDE_EQUAL: '/=';
COLON: ':';
SEMICOLON: ';';
QUESTION: '?';
ATsymbol: '@';
LBRACKET: '[';
RBRACKET: ']';
XOR: '^';
XOR_EQUAL: '^=';
LBRACE: '{';
OR_OR: '||';
OR_EQUAL: '|=';
RBRACE: '}';
TWIDDLE: '~';
PLUS: '+';
PLUS_PLUS: '++';
PLUS_EQUAL: '+=';
LESS: '<';
LEFT_SHIFT: '<<';
LEFT_SHIFT_EQUAL: '<<=';
RIGHT_SHIFT: '>>';
RIGHT_SHIFT_EQUAL: '>>=';
UNSIGNED_RIGHT_SHIFT: '>>>';
UNSIGNED_RIGHT_SHIFT_EQUAL: '>>>=';
LESS_EQUAL: '<=';
EQUAL: '=';
EQUAL_EQUAL: '==';
GREATER: '>';
GREATER_EQUAL: '>=';
ELLIPSIS: '...';

RANGE: '..';
ARROW: '->';
DARROW: '=>';
SUBTYPE: '<:';
SUPERTYPE: ':>';
STARSTAR: '**';
NTWIDDLE: '!~';
LARROW: '<-';
FUNNEL: '-<';
LFUNNEL: '>-';
DIAMOND: '<>';
BOWTIE: '><';
RANGE_EQUAL: '..=';
ARROW_EQUAL: '->=';
STARSTAR_EQUAL: '**=';
TWIDDLE_EQUAL: '~=';
LARROW_EQUAL: '<-=';
FUNNEL_EQUAL: '-<=';
LFUNNEL_EQUAL: '>-=';
DIAMOND_EQUAL: '<>=';
BOWTIE_EQUAL: '><=';

ABSTRACT: 'abstract';
AS: 'as';
ASSERT: 'assert';
ASYNC: 'async';
AT: 'at';
ATHOME: 'athome';
ATEACH: 'ateach';
ATOMIC: 'atomic';
BREAK: 'break';
CASE: 'case';
CATCH: 'catch';
CLASS: 'class';
CLOCKED: 'clocked';
CONTINUE: 'continue';
DEF: 'def';
DEFAULT: 'default';
DO: 'do';
ELSE: 'else';
EXTENDS: 'extends';
FALSE: 'false';
FINAL: 'final';
FINALLY: 'finally';
FINISH: 'finish';
FOR: 'for';
GOTO: 'goto';
HASZERO: 'haszero';
HERE: 'here';
IF: 'if';
IMPLEMENTS: 'implements';
IMPORT: 'import';
IN: 'in';
INSTANCEOF: 'instanceof';
INTERFACE: 'interface';
ISREF: 'isref';
NATIVE: 'native';
NEW: 'new';
NULL: 'null';
OFFER: 'offer';
OFFERS: 'offers';
OPERATOR: 'operator';
PACKAGE: 'package';
PRIVATE: 'private';
PROPERTY: 'property';
PROTECTED: 'protected';
PUBLIC: 'public';
RETURN: 'return';
SELF: 'self';
STATIC: 'static';
STRUCT: 'struct';
SUPER: 'super';
SWITCH: 'switch';
THIS: 'this';
THROW: 'throw';
THROWS: 'throws';
TRANSIENT: 'transient';
TRUE: 'true';
TRY: 'try';
TYPE: 'type';
VAL: 'val';
VAR: 'var';
VOID: 'void';
WHEN: 'when';
WHILE: 'while';


IDENTIFIER:
      Letter LetterOrDigit*
    | '`' NotBQ* '`'
    ;

fragment
Letter:
      [a-zA-Z_$]
    | [\u0080-\ufffe]
    ;
fragment
LetterOrDigit:
      [a-zA-Z0-9_$]
    | [\u0080-\ufffe]
    ;
fragment
NotBQ:
      ~[`\\]
    | EscapeSequence
    | '\\' '`'
    ;

IntLiteral:
      IntegerLiteral [nN]
    ;
LongLiteral:
      IntegerLiteral [lL]?
    ;
ByteLiteral:
      IntegerLiteral [yY]
    ;
ShortLiteral:
      IntegerLiteral [sS]
    ;
UnsignedIntLiteral:
      IntegerLiteral (([uU][nN]) | [nN]([uU]))
    ;
UnsignedLongLiteral:
      IntegerLiteral (([uU][lL]?) | [lL?]([uU]))
    ;
UnsignedByteLiteral:
      IntegerLiteral (([uU][yY]) | [yY]([uU]))
    ;
UnsignedShortLiteral:
      IntegerLiteral (([uU][sS]) | [sS]([uU]))
    ;
FloatingPointLiteral:
      Digits '.' Digits? ExponentPart? FloatingTypeSuffix
    | '.' Digits ExponentPart? FloatingTypeSuffix
    | Digits ExponentPart FloatingTypeSuffix
    | Digits ExponentPart? FloatingTypeSuffix
    ;

fragment
ExponentPart:
      ('e'|'E') ('+'|'-')? Digits
    ;

fragment
FloatingTypeSuffix:
      'f' |  'F'
    ;

DoubleLiteral:
      Digits '.' Digits? ExponentPart DoubleTypeSuffix?
    | Digits '.' Digits? DoubleTypeSuffix
    | Digits '.' {isDecimal()}? Digits?
    | '.' Digits ExponentPart? DoubleTypeSuffix?
    | Digits ExponentPart DoubleTypeSuffix?
    | Digits ExponentPart? DoubleTypeSuffix
    ;

fragment
DoubleTypeSuffix:
      'd' | 'D'
    ;

fragment IntegerLiteral:
      DecimalNumeral
    | HexNumeral
    | OctalNumeral
    | BinaryNumeral
    ;
fragment
DecimalNumeral
    :   '0'
    |   NonZeroDigit (Digits? | Underscores Digits)
    ;

fragment
Digits
    :   Digit (DigitOrUnderscore* Digit)?
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
DigitOrUnderscore
    :   Digit
    |   '_'
    ;

fragment
Underscores
    :   '_'+
    ;

fragment
HexNumeral
    :   '0' [xX] HexDigits
    ;

fragment
HexDigits
    :   HexDigit (HexDigitOrUnderscore* HexDigit)?
    ;

fragment
HexDigit
    :   [0-9a-fA-F]
    ;

fragment
HexDigitOrUnderscore
    :   HexDigit
    |   '_'
    ;

fragment
OctalNumeral
    :   '0' Underscores? OctalDigits
    ;

fragment
OctalDigits
    :   OctalDigit (OctalDigitOrUnderscore* OctalDigit)?
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
OctalDigitOrUnderscore
    :   OctalDigit
    |   '_'
    ;

fragment
BinaryNumeral
    :   '0' [bB] BinaryDigits
    ;

fragment
BinaryDigits
    :   BinaryDigit (BinaryDigitOrUnderscore* BinaryDigit)?
    ;

fragment
BinaryDigit
    :   [01]
    ;

fragment
BinaryDigitOrUnderscore
    :   BinaryDigit
    |   '_'
    ;

CharacterLiteral
    :   '\'' NotQ '\''
    ;
fragment
NotQ
    :   ~['\\]
    | EscapeSequence
    ;
fragment
EscapeSequence
    :   '\\' [btnfr"'\\]
    |   OctalEscape
    |   UnicodeEscape
    ;


StringLiteral
    :   '"' StringCharacters? '"'
    ;

fragment
StringCharacters
    :   StringCharacter+
    ;

fragment
StringCharacter
    :   ~["\\]
    |   EscapeSequence
    ;


fragment
OctalEscape
    :   '\\' OctalDigit
    |   '\\' OctalDigit OctalDigit
    |   '\\' ZeroToThree OctalDigit OctalDigit
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment
ZeroToThree
    :   [0-3]
    ;

WS  :  [ \t\r\n\u000C]+ -> channel(1) // WHITESPACES
    ;

DOCCOMMENT
    :  '/**' .*? '*/' -> channel(2) // DOCCOMMENTS
    ;

COMMENT
    :   '/*' .*? '*/' -> channel(3) // COMMENTS
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> channel(3) //COMMENTS
    ;
