lexer grammar X10Lexer;

@lexer::header {
  package x10.parserGen;
}

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


IDENTIFIER: [A-Za-z_] [0-9A-Za-z_]* ; // XXX TODO

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
ExponentPart:
      ('e'|'E') ('+'|'-')? Digits
    ;
FloatingTypeSuffix:
      'f' |  'F'
    ;
DoubleLiteral:
      Digits '.' Digits? ExponentPart? DoubleTypeSuffix?
    | '.' Digits ExponentPart? DoubleTypeSuffix?
    | Digits ExponentPart DoubleTypeSuffix?
    | Digits ExponentPart? DoubleTypeSuffix
    ;
DoubleTypeSuffix:
      'd' | 'D'
    ;
Digits:
      [0-9]+
    ;
IntegerLiteral: Digits; // XXX TODO


CharacterLiteral
    :   '\'' SingleCharacter '\''
//    |   '\'' EscapeSequence '\''
    ;
fragment
SingleCharacter
    :   ~['\\]
    ;


StringLiteral
    :   '"' .*? '"' // XXX TODO
    ;

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


WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;
