grammar X10;

@parser::header {
  package x10.parserGen;
  
  import polyglot.parse.*;
  import polyglot.ast.*;
  import x10.ast.*;
}

import X10_Lexer;

modifiersopt:
        modifier*
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
methodModifiersopt:
        methodModifier*
    ;
methodModifier:
      modifier
    | 'property'
    ;
typeDefDeclaration:
      modifiersopt 'type' identifier typeParameters? whereClause? '=' type ';'                                      #typeDef1
    | modifiersopt 'type' identifier typeParameters? '(' formalParameterList ')' whereClause? '=' type ';'          #typeDef2
    ;
properties:
      '(' propertyList ')'
    ;
propertyList:
      property                                       #propertyList1
    | propertyList ',' property                      #propertyList2
    ;
property:
      annotationsopt identifier resultType
    ;
methodDeclaration:
      methodModifiersopt 'def' identifier typeParameters? formalParameters whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | binaryOperatorDeclaration
    | prefixOperatorDeclaration
    | applyOperatorDeclaration
    | setOperatorDeclaration
    | conversionOperatorDeclaration
    ;
binaryOperatorDeclaration:
      methodModifiersopt 'operator' typeParameters? '(' formalParameter ')' binOp '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | methodModifiersopt 'operator' typeParameters? 'this' binOp '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | methodModifiersopt 'operator' typeParameters? '(' formalParameter ')' binOp 'this' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
prefixOperatorDeclaration:
      methodModifiersopt 'operator' typeParameters? prefixOp '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | methodModifiersopt 'operator' typeParameters? prefixOp 'this' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
applyOperatorDeclaration:
      methodModifiersopt 'operator' 'this' typeParameters? formalParameters whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
setOperatorDeclaration:
      methodModifiersopt 'operator' 'this' typeParameters? formalParameters '=' '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
conversionOperatorDeclaration:
      explicitConversionOperatorDeclaration
    | implicitConversionOperatorDeclaration
    ;
explicitConversionOperatorDeclaration:
      methodModifiersopt 'operator' typeParameters? '(' formalParameter ')' 'as' type whereClause? oBSOLETE_Offers? throws_? methodBody
    | methodModifiersopt 'operator' typeParameters? '(' formalParameter ')' 'as' '?' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
implicitConversionOperatorDeclaration:
      methodModifiersopt 'operator' typeParameters? '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
propertyMethodDeclaration:
      methodModifiersopt identifier typeParameters? formalParameters whereClause? hasResultType? methodBody
    | methodModifiersopt identifier whereClause? hasResultType? methodBody
    ;
explicitConstructorInvocation:
      'this' typeArguments? '(' argumentList? ')' ';'
    | 'super' typeArguments? '(' argumentList? ')' ';'
    | primary '.' 'this' typeArguments? '(' argumentList? ')' ';'
    | primary '.' 'super' typeArguments? '(' argumentList? ')' ';'
    ;
interfaceDeclaration:
      modifiersopt 'interface' identifier typeParamsWithVariance? properties? whereClause? extendsInterfaces? interfaceBody
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
      modifiersopt 'class' identifier typeParamsWithVariance? properties? whereClause? superExtends? interfaces? classBody
    ;
structDeclaration:
      modifiersopt 'struct' identifier typeParamsWithVariance? properties? whereClause? interfaces? classBody
    ;
constructorDeclaration:
      modifiersopt 'def' 'this' typeParameters? formalParameters whereClause? oBSOLETE_Offers? throws_? hasResultType? constructorBody
    ;
superExtends:
      'extends' classType
    ;
varKeyword:
      'val'
    | 'var'
    ;
fieldDeclaration:
      modifiersopt varKeyword fieldDeclarators ';'
    | modifiersopt fieldDeclarators ';'
    ;
statement:
      annotationStatement
    | expressionStatement
    ;
annotationStatement:
      annotationsopt nonExpressionStatement
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
      '{' switchBlockStatementGroupsopt switchLabelsopt '}'
    ;
switchBlockStatementGroupsopt:
        switchBlockStatementGroup*
    ;
switchBlockStatementGroup:
      switchLabel+ blockStatements
    ;
switchLabelsopt:
        switchLabel*
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
    | annotationsopt '{' blockStatements? lastExpression '}'
    | annotationsopt block
    ;
atExpression:
      annotationsopt 'at' '(' expression ')' closureBody
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
packageName returns [ParsedName ast]:
      identifier							#packageName0
    | packageName '.' identifier            #packageName1
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
compilationUnit returns [SourceFile ast]:
      packageDeclaration? importDeclarationsopt typeDeclarationsopt
    ;
packageDeclaration returns [PackageNode ast]:
      annotationsopt 'package' packageName ';'
    ;
importDeclarationsopt returns [List<Import> ast]:
        importDeclaration*
    ;
importDeclaration returns [Import ast]:
      'import' typeName ';'                    #singleTypeImportDeclaration
    | 'import' packageOrTypeName '.' '*' ';'   #typeImportOnDemandDeclaration
    ;
typeDeclarationsopt returns [List<TopLevelDecl> ast]:
        typeDeclaration*
    ;
typeDeclaration returns [TopLevelDecl ast]:
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
      '{' classMemberDeclarationsopt '}'
    ;
classMemberDeclarationsopt:
        classMemberDeclaration*
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
      homeVariable (',' homeVariable)*
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
      modifiersopt loopIndexDeclarator
    | modifiersopt varKeyword loopIndexDeclarator
    ;
formalParameter:
      modifiersopt formalDeclarator
    | modifiersopt varKeyword formalDeclarator
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
    | '=' annotationsopt '{' blockStatements? lastExpression '}'
    | '=' annotationsopt block
    | annotationsopt block
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
      '{' interfaceMemberDeclarationsopt '}'
    ;
interfaceMemberDeclarationsopt:
        interfaceMemberDeclaration*
    ;
interfaceMemberDeclaration:
      methodDeclaration
    | propertyMethodDeclaration
    | fieldDeclaration
    | typeDeclaration
    ;
annotationsopt returns [List<AnnotationNode> ast]:
      annotation*
    ;
annotations:
      annotation+
    ;
annotation:
      '@' namedTypeNoConstraints
    ;
identifier returns [Id ast]: 
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
      modifiersopt varKeyword variableDeclarators
    | modifiersopt variableDeclaratorsWithType
    | modifiersopt varKeyword formalDeclarators
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
    | primary '.' identifier typeArguments? '(' argumentList? ')'
    | 'super' '.' identifier typeArguments? '(' argumentList? ')'
    | className '.' 'super' '.' identifier typeArguments? '(' argumentList? ')'
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
    | type '==' type // Danger some type equalities can be capture by the previous rule
    | conditionalExpression ('~'|'!~') conditionalExpression
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

