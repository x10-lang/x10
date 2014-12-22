grammar X10Parser;

@parser::header {
  package x10.parserGen;
} 

import X10Lexer;



accept:
      compilationUnit
    ;

modifiersopt:
      modifiers?
    ;
modifiers:
      modifier+
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
      modifiersopt
    | methodModifiersopt property
    | methodModifiersopt modifier
    ;
typeDefDeclaration:
      modifiersopt 'type' identifier typeParametersopt whereClauseopt '=' type ';'
    | modifiersopt 'type' identifier typeParametersopt '(' formalParameterList ')' whereClauseopt '=' type ';'
    ;
properties:
      '(' propertyList ')'
    ;
propertyList:
      property
    | propertyList ',' property
    ;
property:
      annotationsopt identifier resultType
    ;
methodDeclaration:
      methodModifiersopt 'def' identifier typeParametersopt formalParameters whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    | binaryOperatorDeclaration
    | prefixOperatorDeclaration
    | applyOperatorDeclaration
    | setOperatorDeclaration
    | conversionOperatorDeclaration
    ;
binaryOperatorDeclaration:
      methodModifiersopt 'operator' typeParametersopt '(' formalParameter ')' binOp '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    | methodModifiersopt 'operator' typeParametersopt 'this' binOp '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    | methodModifiersopt 'operator' typeParametersopt ( formalParameter ) binOp 'this' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    ;
prefixOperatorDeclaration:
      methodModifiersopt 'operator' typeParametersopt prefixOp '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    | methodModifiersopt 'operator' typeParametersopt prefixOp 'this' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    ;
applyOperatorDeclaration:
      methodModifiersopt 'operator' 'this' typeParametersopt formalParameters whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    ;
setOperatorDeclaration:
      methodModifiersopt 'operator' 'this' typeParametersopt formalParameters '=' '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    ;
conversionOperatorDeclaration:
      explicitConversionOperatorDeclaration
    | implicitConversionOperatorDeclaration
    ;
explicitConversionOperatorDeclaration:
      methodModifiersopt 'operator' typeParametersopt '(' formalParameter ')' 'as' type whereClauseopt oBSOLETE_Offersopt throwsopt methodBody
    | methodModifiersopt 'operator' typeParametersopt '(' formalParameter ')' 'as' '?' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    ;
implicitConversionOperatorDeclaration:
      methodModifiersopt 'operator' typeParametersopt '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    ;
propertyMethodDeclaration:
      methodModifiersopt identifier typeParametersopt formalParameters whereClauseopt hasResultTypeopt methodBody
    | methodModifiersopt identifier whereClauseopt hasResultTypeopt methodBody
    ;
explicitConstructorInvocation:
      'this' '(' argumentListopt ')' ';'
    | 'this' typeArguments '(' argumentListopt ')' ';'
    | 'super' typeArgumentsopt '(' argumentListopt ')' ';'
    | primary '.' 'this' typeArgumentsopt '(' argumentListopt ')' ';'
    | primary '.' 'super' typeArgumentsopt '(' argumentListopt ')' ';'
    ;
interfaceDeclaration:
      modifiersopt 'interface' identifier typeParamsWithVarianceopt propertiesopt whereClauseopt extendsInterfacesopt interfaceBody
    ;
classInstanceCreationExpression:
      'new' typeName typeArgumentsopt '(' argumentListopt ')' classBodyopt
    | primary '.' 'new' identifier typeArgumentsopt '(' argumentListopt ')' classBodyopt
    | fullyQualifiedName '.' 'new' identifier typeArgumentsopt '(' argumentListopt ')' classBodyopt
    ;
assignPropertyCall:
      'property' typeArgumentsopt '(' argumentListopt ')' ';'
    ;
type:
      functionType
    | constrainedType
    | void_
    | type annotations
    ;
functionType:
      typeParametersopt '(' formalParameterListopt ')' whereClauseopt oBSOLETE_Offersopt '=>' type
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
      '{' fUTURE_ExistentialListopt constraintConjunctionopt '}'
    ;
typeParamsWithVariance:
      '[' typeParamWithVarianceList ']'
    ;
typeParameters:
      '[' typeParameterList ']'
    ;
formalParameters:
      '(' formalParameterListopt ')'
    ;
constraintConjunction:
      expression
    | constraintConjunction ',' expression
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
constraintConjunctionopt:
      constraintConjunction?
    ;
fUTURE_ExistentialListopt:
      fUTURE_ExistentialList?
    ;
fUTURE_ExistentialList:
      formalParameter (';' formalParameter)*
    ;
classDeclaration:
      modifiersopt 'class' identifier typeParamsWithVarianceopt propertiesopt whereClauseopt superExtendsopt interfacesopt classBody
    ;
structDeclaration:
      modifiersopt 'struct' identifier typeParamsWithVarianceopt propertiesopt whereClauseopt interfacesopt classBody
    ;
constructorDeclaration:
      modifiersopt 'def' 'this' typeParametersopt formalParameters whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt constructorBody
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
      '{' switchBlockStatementGroupsopt switchLabelsopt '}'
    ;
switchBlockStatementGroups:
      switchBlockStatementGroup+
    ;
switchBlockStatementGroup:
      switchLabels blockStatements
    ;
switchLabels:
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
      'for' '(' forInitopt ';' expressionopt ';' forUpdateopt ')' statement
    ;
forInit:
      statementExpressionList
    | localVariableDeclaration
    ;
forUpdateExpression:
      assignment
    | methodInvocation
    | classInstanceCreationExpression
    | conditionalExpression
    ;
forUpdateExpressionList:
      forUpdateExpression (',' forUpdateExpression)*
    ;
forUpdate:
      forUpdateExpressionList
    ;
statementExpressionList:
      statementExpression (',' statementExpression)*
    ;
breakStatement:
      'break' identifieropt ';'
    ;
continueStatement:
      'continue' identifieropt ';'
    ;
returnStatement:
      'return' expressionopt ';'
    ;
throwStatement:
      'throw' expression ';'
    ;
tryStatement:
      'try' block catches
    | 'try' block catchesopt finallyBlock
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
      'async' clockedClauseopt statement
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
      'ateach' '(' loopIndex 'in' expression ')' clockedClauseopt statement
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
      formalParameters whereClauseopt hasResultTypeopt oBSOLETE_Offersopt '=>' closureBody
    ;
lastExpression:
      expression
    ;
closureBody:
      expression
    | annotationsopt '{' blockStatementsopt lastExpression '}'
    | annotationsopt block
    ;
atExpression:
      annotationsopt 'at' '(' expression ')' closureBody
    ;
oBSOLETE_FinishExpression:
      'finish' '(' expression ')' block
    ;
whereClauseopt:
      whereClause?
    ;
clockedClauseopt:
      clockedClause?
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
      packageDeclarationopt typeDeclarationsopt
    | packageDeclarationopt importDeclarations typeDeclarationsopt
    ;
importDeclarations:
      importDeclaration
    | importDeclarations importDeclaration
    ;
typeDeclarations:
      typeDeclaration
    | typeDeclarations typeDeclaration
    ;
packageDeclaration:
      annotationsopt 'package' packageName ';'
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
      '{' classMemberDeclarationsopt '}'
    ;
classMemberDeclarations:
      classMemberDeclaration+
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
atCaptureDeclarators:
      atCaptureDeclarator (',' atCaptureDeclarator)*
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
      identifier hasResultTypeopt
    | '[' identifierList ']' hasResultTypeopt
    | identifier '[' identifierList ']' hasResultTypeopt
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
    | '=' annotationsopt '{' blockStatementsopt lastExpression '}'
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
      '{' explicitConstructorInvocationopt blockStatementsopt '}'
    ;
arguments:
      '(' argumentList ')'
    ;
extendsInterfaces:
      'extends' type (',' type)
    ;
interfaceBody:
      '{' interfaceMemberDeclarationsopt '}'
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
      '{' blockStatementsopt '}'
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
    | identifier hasResultTypeopt '=' variableInitializer
    ;
variableDeclarator:
      identifier hasResultTypeopt '=' variableInitializer
    | '[' identifierList ']' hasResultTypeopt '=' variableInitializer
    | identifier '[' identifierList ']' hasResultTypeopt '=' variableInitializer
    ;
variableDeclaratorWithType:
      identifier hasResultType '=' variableInitializer
    | '[' identifierList ']' hasResultType '=' variableInitializer
    | identifier '[' identifierList ']' hasResultType '=' variableInitializer
    ;
atCaptureDeclarator:
      modifiersopt varKeywordopt variableDeclarator
    | identifier
    | 'this'
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
    | '[' argumentListopt ']'
    | literal
    | 'self'
    | 'this'
    | className '.' 'this'
    | '(' expression ')'
//    | classInstanceCreationExpression
    | 'new' typeName typeArgumentsopt '(' argumentListopt ')' classBodyopt
    | primary '.' 'new' identifier typeArgumentsopt '(' argumentListopt ')' classBodyopt
    | fullyQualifiedName '.' 'new' identifier typeArgumentsopt '(' argumentListopt ')' classBodyopt
//    | fieldAccess
    | primary '.' identifier
    | 'super' '.' identifier
    | className '.' 'super' '.' identifier
//    | methodInvocation
    | methodName typeArgumentsopt '(' argumentListopt ')'
    | primary '.' identifier '(' argumentListopt ')'
    | primary '.' identifier typeArguments '(' argumentListopt ')'
    | 'super' '.' identifier '(' argumentListopt ')'
    | 'super' '.' identifier typeArguments '(' argumentListopt ')'
    | className '.' 'super' '.' identifier '(' argumentListopt ')'
    | className '.' 'super' '.' identifier typeArguments '(' argumentListopt ')'
    | primary typeArgumentsopt '(' argumentListopt ')'
//    | operatorPrefix typeArgumentsopt '(' argumentListopt ')' // XXX TODO
    | className '.' 'operator' 'as' '[' type ']' typeArgumentsopt '(' argumentListopt ')'
    | className '.' 'operator' '[' type ']' typeArgumentsopt '(' argumentListopt ')'

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
      expression (',' expression)
    ;
fieldAccess:
      primary '.' identifier
    | 'super' '.' identifier
    | className '.' 'super' '.' identifier
    ;
methodInvocation:
      methodName typeArgumentsopt '(' argumentListopt ')'
    | primary '.' identifier '(' argumentListopt ')'
    | primary '.' identifier typeArguments '(' argumentListopt ')'
    | 'super' '.' identifier '(' argumentListopt ')'
    | 'super' '.' identifier typeArguments '(' argumentListopt ')'
    | className '.' 'super' '.' identifier '(' argumentListopt ')'
    | className '.' 'super' '.' identifier typeArguments '(' argumentListopt ')'
    | primary typeArgumentsopt '(' argumentListopt ')'
    | operatorPrefix typeArgumentsopt '(' argumentListopt ')'
    | className '.' 'operator' 'as' '[' type ']' typeArgumentsopt '(' argumentListopt ')'
    | className '.' 'operator' '[' type ']' typeArgumentsopt '(' argumentListopt ')'
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
    | expressionName '(' argumentListopt ')' assignmentOperator assignmentExpression
    | primary '(' argumentListopt ')' assignmentOperator assignmentExpression
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
    | '~ '
    | '!~'
    | '! '
    | '<>'
    | '><'
    ;
catchesopt:
      catches?
    ;
identifieropt:
      identifier?
    ;
forUpdateopt:
      forUpdate?
    ;
expressionopt:
      expression?
    ;
forInitopt:
      forInit?
    ;
switchLabelsopt:
      switchLabels
    ;
switchBlockStatementGroupsopt:
      switchBlockStatementGroups?
    ;
interfaceMemberDeclarationsopt:
      interfaceMemberDeclarations?
    ;
extendsInterfacesopt:
      extendsInterfaces?
    ;
classBodyopt:
      classBody?
    ;
argumentListopt:
      argumentList?
    ;
blockStatementsopt:
      blockStatements?
    ;
explicitConstructorInvocationopt:
      explicitConstructorInvocation?
    ;
formalParameterListopt:
      formalParameterList?
    ;
oBSOLETE_Offersopt:
      oBSOLETE_Offers?
    ;
throwsopt:
      throws_?
    ;
classMemberDeclarationsopt:
      classMemberDeclarations?
    ;
interfacesopt:
      interfaces?
    ;
superExtendsopt:
      superExtends?
    ;
typeParametersopt:
      typeParameters?
    ;
formalParametersopt:
      formalParameters?
    ;
annotationsopt:
      annotations?
    ;
typeDeclarationsopt:
      typeDeclarations?
    ;
importDeclarationsopt:
      importDeclarations?
    ;
packageDeclarationopt:
      packageDeclaration?
    ;
hasResultTypeopt:
      hasResultType?
    ;
typeArgumentsopt:
      typeArguments?
    ;
typeParamsWithVarianceopt:
      typeParamsWithVariance?
    ;
propertiesopt:
      properties?
    ;
varKeywordopt:
      varKeyword?
    ;
atCaptureDeclaratorsopt:
      atCaptureDeclarators?
    ;
