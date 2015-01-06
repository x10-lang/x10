grammar X10Parser;

@parser::header {
  package x10.parserGen;
} 

import X10Lexer;


accept:
      compilationUnit
    ;

modifier:
      'abstract'
    | annotation
    | 'atomic'
    | 'final'
    | 'native'
    | 'private'
    | 'protected'
    | 'public'
    | 'static'
    | 'transient'
    | 'clocked'
    ;
methodModifier:
      modifier
    | 'property'
    ;
typeDefDeclaration:
      modifier* 'type' identifier typeParameters? whereClause? '=' type ';'
    | modifier* 'type' identifier typeParameters? '(' formalParameterList ')' whereClause? '=' type ';'
    ;
properties:
      '(' propertyList ')'
    ;
propertyList:
      property
    | propertyList ',' property
    ;
property:
      annotations? identifier resultType
    ;
methodDeclaration:
      methodModifier* 'def' identifier typeParameters? formalParameters whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | binaryOperatorDeclaration
    | prefixOperatorDeclaration
    | applyOperatorDeclaration
    | setOperatorDeclaration
    | conversionOperatorDeclaration
    ;
binaryOperatorDeclaration:
      methodModifier* 'operator' typeParameters? '(' formalParameter ')' binOp '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | methodModifier* 'operator' typeParameters? 'this' binOp '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | methodModifier* 'operator' typeParameters? '(' formalParameter ')' binOp 'this' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
prefixOperatorDeclaration:
      methodModifier* 'operator' typeParameters? prefixOp '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | methodModifier* 'operator' typeParameters? prefixOp 'this' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
applyOperatorDeclaration:
      methodModifier* 'operator' 'this' typeParameters? formalParameters whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
setOperatorDeclaration:
      methodModifier* 'operator' 'this' typeParameters? formalParameters '=' '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
conversionOperatorDeclaration:
      explicitConversionOperatorDeclaration
    | implicitConversionOperatorDeclaration
    ;
explicitConversionOperatorDeclaration:
      methodModifier* 'operator' typeParameters? '(' formalParameter ')' 'as' type whereClause? oBSOLETE_Offers? throws_? methodBody
    | methodModifier* 'operator' typeParameters? '(' formalParameter ')' 'as' '?' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
implicitConversionOperatorDeclaration:
      methodModifier* 'operator' typeParameters? '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
propertyMethodDeclaration:
      methodModifier* identifier typeParameters? formalParameters whereClause? hasResultType? methodBody
    | methodModifier* identifier whereClause? hasResultType? methodBody
    ;
explicitConstructorInvocation:
      'this' typeArguments? '(' argumentList? ')' ';'
    | 'super' typeArguments? '(' argumentList? ')' ';'
    | primary '.' 'this' typeArguments? '(' argumentList? ')' ';'
    | primary '.' 'super' typeArguments? '(' argumentList? ')' ';'
    ;
interfaceDeclaration:
      modifier* 'interface' identifier typeParamsWithVariance? properties? whereClause? extendsInterfaces? interfaceBody
    ;
classInstanceCreationExpression:
      'new' typeName typeArguments? '(' argumentList? ')' classBody?
    | primary '.' 'new' identifier typeArguments? '(' argumentList? ')' classBody?
    | fullyQualifiedName '.' 'new' identifier typeArguments? '(' argumentList? ')' classBody?
    ;
assignPropertyCall:
      'property' typeArguments? '(' argumentList? ')' ';'
    ;
type:
      functionType
    | constrainedType
    | void_
    | type annotations
    ;
functionType:
      typeParameters? '(' formalParameterList? ')' whereClause? oBSOLETE_Offers? '=>' type
    ;
classType:
      namedType
    ;
constrainedType:
      namedType
    ;
void_:
      'void'
    ;
simpleNamedType:
      typeName
    | primary '.' identifier
    | simpleNamedType typeArguments? arguments? depParameters? '.' identifier
    ;
parameterizedNamedType:
      simpleNamedType arguments
    | simpleNamedType typeArguments
    | simpleNamedType typeArguments arguments
    ;
depNamedType:
      simpleNamedType depParameters
    | parameterizedNamedType depParameters
    ;
namedTypeNoConstraints:
      simpleNamedType
    | parameterizedNamedType
    ;
namedType:
      namedTypeNoConstraints
    | depNamedType
    ;
depParameters:
      '{' /* fUTURE_ExistentialList? */ constraintConjunction? '}'
    ;
typeParamsWithVariance:
      '[' typeParamWithVarianceList ']'
    ;
typeParameters:
      '[' typeParameterList ']'
    ;
formalParameters:
      '(' formalParameterList? ')'
    ;
constraintConjunction:
      expression (',' expression)*
    ;
hasZeroConstraint:
      type 'haszero'
    ;
isRefConstraint:
      type 'isref'
    ;
subtypeConstraint:
      type '<:' type
    | type ':>' type
    ;
whereClause:
      depParameters
    ;
// fUTURE_ExistentialList:
//       formalParameter (';' formalParameter)*
//     ;
classDeclaration:
      modifier* 'class' identifier typeParamsWithVariance? properties? whereClause? superExtends? interfaces? classBody
    ;
structDeclaration:
      modifier* 'struct' identifier typeParamsWithVariance? properties? whereClause? interfaces? classBody
    ;
constructorDeclaration:
      modifier* 'def' 'this' typeParameters? formalParameters whereClause? oBSOLETE_Offers? throws_? hasResultType? constructorBody
    ;
superExtends:
      'extends' classType
    ;
varKeyword:
      'val'
    | 'var'
    ;
fieldDeclaration:
      modifier* varKeyword fieldDeclarators ';'
    | modifier* fieldDeclarators ';'
    ;
statement:
      annotationStatement
    | expressionStatement
    ;
annotationStatement:
      annotations? nonExpressionStatement
    ;
nonExpressionStatement:
      block
    | emptyStatement
    | assertStatement
    | switchStatement
    | doStatement
    | breakStatement
    | continueStatement
    | returnStatement
    | throwStatement
    | tryStatement
    | labeledStatement
    | ifThenStatement
    | ifThenElseStatement
    | whileStatement
    | forStatement
    | asyncStatement
    | atStatement
    | atomicStatement
    | whenStatement
    | atEachStatement
    | finishStatement
    | assignPropertyCall
    | oBSOLETE_OfferStatement
    ;
oBSOLETE_OfferStatement:
      'offer' expression ';'
    ;
ifThenStatement:
      'if' '(' expression ')' statement
    ;
ifThenElseStatement:
      'if' '(' expression ')' statement 'else' statement
    ;
emptyStatement:
      ';'
    ;
labeledStatement:
      identifier ':' loopStatement
    ;
loopStatement:
      forStatement
    | whileStatement
    | doStatement
    | atEachStatement
    ;
expressionStatement:
      statementExpression ';'
    ;
statementExpression:
      assignment
    | methodInvocation
    | classInstanceCreationExpression
    | conditionalExpression
    ;
assertStatement:
      'assert' expression ';'
    | 'assert' expression ':' expression ';'
    ;
switchStatement:
      'switch' '(' expression ')' switchBlock
    ;
switchBlock:
      '{' switchBlockStatementGroup* switchLabel* '}'
    ;
switchBlockStatementGroup:
      switchLabel+ blockStatements
    ;
switchLabel:
      'case' constantExpression ':'
    | 'default' ':'
    ;
whileStatement:
      'while' '(' expression ')' statement
    ;
doStatement:
      'do' statement 'while' '(' expression ')' ';'
    ;
forStatement:
      basicForStatement
    | enhancedForStatement
    ;
basicForStatement:
      'for' '(' forInit? ';' expression? ';' forUpdate? ')' statement
    ;
forInit:
      statementExpressionList
    | localVariableDeclaration
    ;
forUpdate:
      statementExpressionList
    ;
statementExpressionList:
      statementExpression (',' statementExpression)*
    ;
breakStatement:
      'break' identifier? ';'
    ;
continueStatement:
      'continue' identifier? ';'
    ;
returnStatement:
      'return' expression? ';'
    ;
throwStatement:
      'throw' expression ';'
    ;
tryStatement:
      'try' block catches
    | 'try' block catches? finallyBlock
    ;
catches:
      catchClause+
    ;
catchClause:
      'catch' '(' formalParameter ')' block
    ;
finallyBlock:
      'finally' block
    ;
clockedClause:
      'clocked' arguments
    ;
asyncStatement:
      'async' clockedClause? statement
    | 'clocked' 'async' statement
    ;
atStatement:
      'at' '(' expression ')' statement
    ;
atomicStatement:
      'atomic' statement
    ;
whenStatement:
      'when' '(' expression ')' statement
    ;
atEachStatement:
      'ateach' '(' loopIndex 'in' expression ')' clockedClause? statement
    | 'ateach' '(' expression ')' statement
    ;
enhancedForStatement:
      'for' '(' loopIndex 'in' expression ')' statement
    | 'for' '(' expression ')' statement
    ;
finishStatement:
      'finish' statement
    | 'clocked' 'finish' statement
    ;
castExpression:
      primary
    | expressionName
    | castExpression 'as' type
    ;
typeParamWithVarianceList:
      typeParameter
    | oBSOLETE_TypeParamWithVariance
    | typeParamWithVarianceList ',' typeParameter
    | typeParamWithVarianceList ',' oBSOLETE_TypeParamWithVariance
    ;
typeParameterList:
      typeParameter (',' typeParameter)*
    ;
oBSOLETE_TypeParamWithVariance:
      '+' typeParameter
    | '-' typeParameter
    ;
typeParameter:
      identifier
    ;
closureExpression:
      formalParameters whereClause? hasResultType? oBSOLETE_Offers? '=>' closureBody
    ;
lastExpression:
      expression
    ;
closureBody:
      expression
    | annotations? '{' blockStatements? lastExpression '}'
    | annotations? block
    ;
atExpression:
      annotations? 'at' '(' expression ')' closureBody
    ;
oBSOLETE_FinishExpression:
      'finish' '(' expression ')' block
    ;
typeName:
      identifier
    | typeName '.' identifier
    ;
className:
      typeName
    ;
typeArguments:
      '[' typeArgumentList ']'
    ;
typeArgumentList:
      type (',' type)*
    ;
packageName:
      identifier
    | packageName '.' identifier
    ;
expressionName:
      identifier
    | fullyQualifiedName '.' identifier
    ;
methodName:
      identifier
    | fullyQualifiedName '.' identifier
    ;
packageOrTypeName:
      identifier
    | packageOrTypeName '.' identifier
    ;
fullyQualifiedName:
      identifier
    | fullyQualifiedName '.' identifier
    ;
compilationUnit:
      packageDeclaration? importDeclaration* typeDeclaration*
    ;
packageDeclaration:
      annotations? 'package' packageName ';'
    ;
importDeclaration:
      singleTypeImportDeclaration
    | typeImportOnDemandDeclaration
    ;
singleTypeImportDeclaration:
      'import' typeName ';'
    ;
typeImportOnDemandDeclaration:
      'import' packageOrTypeName '.' '*' ';'
    ;
typeDeclaration:
      classDeclaration
    | structDeclaration
    | interfaceDeclaration
    | typeDefDeclaration
    | ';'
    ;
interfaces:
      'implements' interfaceTypeList
    ;
interfaceTypeList:
      type (',' type)*
    ;
classBody:
      '{' classMemberDeclaration* '}'
    ;
classMemberDeclaration:
      interfaceMemberDeclaration
    | constructorDeclaration
    ;
formalDeclarators:
      formalDeclarator (',' formalDeclarator)*
    ;
fieldDeclarators:
      fieldDeclarator (',' fieldDeclarator)*
    ;
variableDeclaratorsWithType:
      variableDeclaratorWithType (',' variableDeclaratorWithType)*
    ;
variableDeclarators:
      variableDeclarator (',' variableDeclarator)*
    ;
homeVariableList:
      homeVariable (',' homeVariable)
    ;
homeVariable:
      identifier
    | 'this'
    ;
variableInitializer:
      expression
    ;
resultType:
      ':' type
    ;
hasResultType:
      resultType
    | '<:' type
    ;
formalParameterList:
      formalParameter (',' formalParameter)*
    ;
loopIndexDeclarator:
      identifier hasResultType?
    | '[' identifierList ']' hasResultType?
    | identifier '[' identifierList ']' hasResultType?
    ;
loopIndex:
      modifier* loopIndexDeclarator
    | modifier* varKeyword loopIndexDeclarator
    ;
formalParameter:
      modifier* formalDeclarator
    | modifier* varKeyword formalDeclarator
    | type
    ;
oBSOLETE_Offers:
      'offers' type
    ;
throws_:
      'throws' throwsList
    ;
throwsList:
      type (',' type)*
    ;
methodBody:
      '=' lastExpression ';'
    | '=' annotations? '{' blockStatements? lastExpression '}'
    | '=' annotations? block
    | annotations? block
    | ';'
    ;
constructorBody:
      '=' constructorBlock
    | constructorBlock
    | '=' explicitConstructorInvocation
    | '=' assignPropertyCall
    | ';'
    ;
constructorBlock:
      '{' explicitConstructorInvocation? blockStatements? '}'
    ;
arguments:
      '(' argumentList ')'
    ;
extendsInterfaces:
      'extends' type (',' type)*
    ;
interfaceBody:
      '{' interfaceMemberDeclarations? '}'
    ;
interfaceMemberDeclarations:
      interfaceMemberDeclaration+
    ;
interfaceMemberDeclaration:
      methodDeclaration
    | propertyMethodDeclaration
    | fieldDeclaration
    | typeDeclaration
    ;
annotations:
      annotation+
    ;
annotation:
      '@' namedTypeNoConstraints
    ;
identifier:
      IDENTIFIER
    ;
block:
      '{' blockStatements? '}'
    ;
blockStatements:
      blockInteriorStatement+
    ;
blockInteriorStatement:
      localVariableDeclarationStatement
    | classDeclaration
    | structDeclaration
    | typeDefDeclaration
    | statement
    ;
identifierList:
      identifier (',' identifier)*
    ;
formalDeclarator:
      identifier resultType
    | '[' identifierList ']' resultType
    | identifier '[' identifierList ']' resultType
    ;
fieldDeclarator:
      identifier hasResultType
    | identifier hasResultType? '=' variableInitializer
    ;
variableDeclarator:
      identifier hasResultType? '=' variableInitializer
    | '[' identifierList ']' hasResultType? '=' variableInitializer
    | identifier '[' identifierList ']' hasResultType? '=' variableInitializer
    ;
variableDeclaratorWithType:
      identifier hasResultType '=' variableInitializer
    | '[' identifierList ']' hasResultType '=' variableInitializer
    | identifier '[' identifierList ']' hasResultType '=' variableInitializer
    ;
localVariableDeclarationStatement:
      localVariableDeclaration ';'
    ;
localVariableDeclaration:
      modifier* varKeyword variableDeclarators
    | modifier* variableDeclaratorsWithType
    | modifier* varKeyword formalDeclarators
    ;
primary:
      'here'
    | '[' argumentList? ']'
    | literal
    | 'self'
    | 'this'
    | className '.' 'this'
    | '(' expression ')'
//    | classInstanceCreationExpression
    | 'new' typeName typeArguments? '(' argumentList? ')' classBody?
    | primary '.' 'new' identifier typeArguments? '(' argumentList? ')' classBody?
    | fullyQualifiedName '.' 'new' identifier typeArguments? '(' argumentList? ')' classBody?
//    | fieldAccess
    | primary '.' identifier
    | 'super' '.' identifier
    | className '.' 'super' '.' identifier
//    | methodInvocation
    | methodName typeArguments? '(' argumentList? ')'
    | primary '.' identifier '(' argumentList? ')'
    | primary '.' identifier typeArguments '(' argumentList? ')'
    | 'super' '.' identifier '(' argumentList? ')'
    | 'super' '.' identifier typeArguments '(' argumentList? ')'
    | className '.' 'super' '.' identifier '(' argumentList? ')'
    | className '.' 'super' '.' identifier typeArguments '(' argumentList? ')'
    | primary typeArguments? '(' argumentList? ')'
//    | operatorPrefix typeArguments? '(' argumentList? ')' // XXX TODO
    | className '.' 'operator' 'as' '[' type ']' typeArguments? '(' argumentList? ')'
    | className '.' 'operator' '[' type ']' typeArguments? '(' argumentList? ')'

    ;
literal:
      IntLiteral
    | LongLiteral
    | ByteLiteral
    | UnsignedByteLiteral
    | ShortLiteral
    | UnsignedShortLiteral
    | UnsignedIntLiteral
    | UnsignedLongLiteral
    | FloatingPointLiteral
    | DoubleLiteral
    | BooleanLiteral
    | CharacterLiteral
    | StringLiteral
    | 'null'
    ;
BooleanLiteral:
      'true'
    | 'false'
    ;
argumentList:
      expression (',' expression)*
    ;
fieldAccess:
      primary '.' identifier
    | 'super' '.' identifier
    | className '.' 'super' '.' identifier
    ;
methodInvocation:
      methodName typeArguments? '(' argumentList? ')'
    | primary '.' identifier '(' argumentList? ')'
    | primary '.' identifier typeArguments '(' argumentList? ')'
    | 'super' '.' identifier '(' argumentList? ')'
    | 'super' '.' identifier typeArguments '(' argumentList? ')'
    | className '.' 'super' '.' identifier '(' argumentList? ')'
    | className '.' 'super' '.' identifier typeArguments '(' argumentList? ')'
    | primary typeArguments? '(' argumentList? ')'
    | operatorPrefix typeArguments? '(' argumentList? ')'
    | className '.' 'operator' 'as' '[' type ']' typeArguments? '(' argumentList? ')'
    | className '.' 'operator' '[' type ']' typeArguments? '(' argumentList? ')'
    ;
operatorPrefix:
      'operator' binOp
    | fullyQualifiedName '.' 'operator' binOp
    | primary '.' 'operator' binOp
    | 'super' '.' 'operator' binOp
    | className '.' 'super' '.' 'operator' binOp
    | 'operator' '(' ')' binOp
    | fullyQualifiedName '.' 'operator' '(' ')' binOp
    | primary '.' 'operator' '(' ')' binOp
    | 'super' '.' 'operator' '(' ')' binOp
    | className '.' 'super' '.' 'operator' '(' ')' binOp
    | 'operator' '(' ')'
    | fullyQualifiedName '.' 'operator' '(' ')'
    | primary '.' 'operator' '(' ')'
    | 'super' '.' 'operator' '(' ')'
    | className '.' 'super' '.' 'operator' '(' ')'
    | 'operator' '(' ')' '='
    | fullyQualifiedName '.' 'operator' '(' ')' '='
    | primary '.' 'operator' '(' ')' '='
    | 'super' '.' 'operator' '(' ')' '='
    | className '.' 'super' '.' 'operator' '(' ')' '='
    ;
conditionalExpression:
      castExpression
    | conditionalExpression ('++'|'--')
    | annotations conditionalExpression
    | ('+'|'-'|'++'|'--') conditionalExpression
    | ('~'|'!'|'^'|'|'|'&'|'*'|'/'|'%') conditionalExpression
    | conditionalExpression '..' conditionalExpression
    | conditionalExpression ('*'|'/'|'%'|'**') conditionalExpression
    | conditionalExpression ('+'|'-') conditionalExpression
    | hasZeroConstraint
    | isRefConstraint
    | subtypeConstraint
    | conditionalExpression ('<<'|'>>'|'>>>'|'->'|'<-'|'-<'|'>-'|'!'|'<>'|'><') conditionalExpression
    | conditionalExpression 'instanceof' type
    | conditionalExpression ('<'|'>'|'<='|'>=') conditionalExpression
    | conditionalExpression ('=='|'!=') conditionalExpression
    | conditionalExpression ('~'|'!~') conditionalExpression
    | type '==' type
    | conditionalExpression '&' conditionalExpression
    | conditionalExpression '^' conditionalExpression
    | conditionalExpression '|' conditionalExpression
    | conditionalExpression '&&' conditionalExpression
    | conditionalExpression '||' conditionalExpression
    | closureExpression
    | atExpression
    | oBSOLETE_FinishExpression
    | conditionalExpression '?' conditionalExpression ':' conditionalExpression
    ;

assignmentExpression:
      assignment
    | conditionalExpression
    ;
assignment:
      leftHandSide assignmentOperator assignmentExpression
    | expressionName '(' argumentList? ')' assignmentOperator assignmentExpression
    | primary '(' argumentList? ')' assignmentOperator assignmentExpression
    ;
leftHandSide:
      expressionName
    | fieldAccess
    ;
assignmentOperator:
      '='
    | '*='
    | '/='
    | '%='
    | '+='
    | '-='
    | '<<='
    | '>>='
    | '>>>='
    | '&='
    | '^='
    | '|='
    | '..='
    | '->='
    | '<-='
    | '-<='
    | '>-='
    | '**='
    | '<>='
    | '><='
    | '~='
    ;
expression:
      assignmentExpression
    ;
constantExpression:
      expression
    ;
prefixOp:
      '+'
    | '-'
    | '!'
    | '~'
    | '^'
    | '|'
    | '&'
    | '*'
    | '/'
    | '%'
    ;
binOp:
      '+'
    | '-'
    | '*'
    | '/'
    | '%'
    | '&'
    | '|'
    | '^'
    | '&&'
    | '||'
    | '<<'
    | '>>'
    | '>>>'
    | '>='
    | '<='
    | '>'
    | '<'
    | '=='
    | '!='
    | '..'
    | '->'
    | '<-'
    | '-<'
    | '>-'
    | '**'
    | '~'
    | '!~'
    | '!'
    | '<>'
    | '><'
    ;

