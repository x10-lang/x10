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
typeDefDeclaration returns [TypeDecl ast]:
      modifiersopt 'type' identifier typeParameters? whereClause? '=' type ';'                                      #typeDef1
    | modifiersopt 'type' identifier typeParameters? '(' formalParameterList ')' whereClause? '=' type ';'          #typeDef2
    ;
properties returns [List<PropertyDecl> ast]:
      '(' property (',' property)* ')'
    ;
property returns [PropertyDecl ast]:
      annotationsopt identifier resultType
    ;
methodDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'def' identifier typeParameters? formalParameters whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | binaryOperatorDeclaration
    | prefixOperatorDeclaration
    | applyOperatorDeclaration
    | setOperatorDeclaration
    | conversionOperatorDeclaration
    ;
binaryOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' typeParameters? '(' formalParameter ')' binOp '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | methodModifiersopt 'operator' typeParameters? 'this' binOp '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | methodModifiersopt 'operator' typeParameters? '(' formalParameter ')' binOp 'this' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
prefixOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' typeParameters? prefixOp '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    | methodModifiersopt 'operator' typeParameters? prefixOp 'this' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
applyOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' 'this' typeParameters? formalParameters whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
setOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' 'this' typeParameters? formalParameters '=' '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
conversionOperatorDeclaration returns [MethodDecl ast]:
      explicitConversionOperatorDeclaration
    | implicitConversionOperatorDeclaration
    ;
explicitConversionOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' typeParameters? '(' formalParameter ')' 'as' type whereClause? oBSOLETE_Offers? throws_? methodBody
    | methodModifiersopt 'operator' typeParameters? '(' formalParameter ')' 'as' '?' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
implicitConversionOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' typeParameters? '(' formalParameter ')' whereClause? oBSOLETE_Offers? throws_? hasResultType? methodBody
    ;
propertyMethodDeclaration returns [MethodDecl ast]:
      methodModifiersopt identifier typeParameters? formalParameters whereClause? hasResultType? methodBody
    | methodModifiersopt identifier whereClause? hasResultType? methodBody
    ;
explicitConstructorInvocation returns [ConstructorCall ast]:
      'this' typeArguments? '(' argumentList? ')' ';'
    | 'super' typeArguments? '(' argumentList? ')' ';'
    | primary '.' 'this' typeArguments? '(' argumentList? ')' ';'
    | primary '.' 'super' typeArguments? '(' argumentList? ')' ';'
    ;
interfaceDeclaration returns [ClassDecl ast]:
      modifiersopt 'interface' identifier typeParamsWithVariance? properties? whereClause? extendsInterfaces? interfaceBody
    ;
assignPropertyCall returns [Stmt ast]:
      'property' typeArguments? '(' argumentList? ')' ';'
    ;
type returns [TypeNode ast]:
      functionType
    | constrainedType
    | void_
    | type annotations
    ;
functionType returns [TypeNode ast]:
      typeParameters? '(' formalParameterList? ')' whereClause? oBSOLETE_Offers? '=>' type
    ;
classType returns [TypeNode ast]:
      namedType
    ;
constrainedType returns [TypeNode ast]:
      namedType
    ;
void_ returns [CanonicalTypeNode ast]:
      'void'
    ;
simpleNamedType returns [TypeNode ast]:
      typeName
    | primary '.' identifier
    | simpleNamedType typeArguments? arguments? depParameters? '.' identifier
    ;
parameterizedNamedType returns [TypeNode ast]:
      simpleNamedType arguments
    | simpleNamedType typeArguments
    | simpleNamedType typeArguments arguments
    ;
depNamedType returns [TypeNode ast]:
      simpleNamedType depParameters
    | parameterizedNamedType depParameters
    ;
namedTypeNoConstraints returns [TypeNode ast]:
      simpleNamedType
    | parameterizedNamedType
    ;
namedType returns [TypeNode ast]:
      namedTypeNoConstraints
    | depNamedType
    ;
depParameters returns [DepParameterExpr ast]:
      '{' /* fUTURE_ExistentialList? */ constraintConjunction? '}'
    ;
typeParamsWithVariance returns [List<TypeParamNode> ast]:
      '[' typeParamWithVarianceList ']'
    ;
typeParameters returns [List<TypeParamNode> ast]:
      '[' typeParameterList ']'
    ;
formalParameters returns [List<Formal> ast]:
      '(' formalParameterList? ')'
    ;
constraintConjunction returns [List<Expr> ast]:
      expression (',' expression)*
    ;
hasZeroConstraint returns [HasZeroTest ast]:
      type 'haszero'
    ;
isRefConstraint returns [IsRefTest ast]:
      type 'isref'
    ;
subtypeConstraint returns [SubtypeTest ast]:
      type '<:' type
    | type ':>' type
    ;
whereClause returns [DepParameterExpr ast]:
      depParameters
    ;
// fUTURE_ExistentialList:
//       formalParameter (';' formalParameter)*
//     ;
classDeclaration returns [ClassDecl ast]:
      modifiersopt 'class' identifier typeParamsWithVariance? properties? whereClause? superExtends? interfaces? classBody
    ;
structDeclaration returns [ClassDecl ast]:
      modifiersopt 'struct' identifier typeParamsWithVariance? properties? whereClause? interfaces? classBody
    ;
constructorDeclaration returns [ConstructorDecl ast]:
      modifiersopt 'def' 'this' typeParameters? formalParameters whereClause? oBSOLETE_Offers? throws_? hasResultType? constructorBody
    ;
superExtends returns [TypeNode ast]:
      'extends' classType
    ;
varKeyword returns [FlagsNode ast]:
      'val'
    | 'var'
    ;
fieldDeclaration returns [List<ClassMember> ast]:
      modifiersopt varKeyword fieldDeclarators ';'
    | modifiersopt fieldDeclarators ';'
    ;
statement returns [Stmt ast]:
      annotationStatement
    | expressionStatement
    ;
annotationStatement returns [Stmt ast]:
      annotationsopt nonExpressionStatement
    ;
nonExpressionStatement returns [Stmt ast]:
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
oBSOLETE_OfferStatement returns [Offer ast]:
      'offer' expression ';'
    ;
ifThenStatement returns [If ast]:
      'if' '(' expression ')' statement
    ;
ifThenElseStatement returns [If ast]:
      'if' '(' expression ')' statement 'else' statement
    ;
emptyStatement returns [Empty ast]:
      ';'
    ;
labeledStatement returns [Labeled ast]:
      identifier ':' loopStatement
    ;
loopStatement returns [Stmt ast]:
      forStatement
    | whileStatement
    | doStatement
    | atEachStatement
    ;
expressionStatement returns [Eval ast]:
      statementExpression ';'
    ;
statementExpression returns [Expr ast]:
      assignment
    | conditionalExpression
    ;
assertStatement returns [Assert ast]:
      'assert' expression ';'
    | 'assert' expression ':' expression ';'
    ;
switchStatement returns [Switch ast]:
      'switch' '(' expression ')' switchBlock
    ;
switchBlock returns [List<Stmt> ast]:
      '{' switchBlockStatementGroupsopt switchLabelsopt '}'
    ;
switchBlockStatementGroupsopt returns [List<Stmt> ast]:
        switchBlockStatementGroup*
    ;
switchBlockStatementGroup returns [List<SwitchElement> ast]:
      switchLabel+ blockStatements
    ;
switchLabelsopt returns [List<Case> ast]:
        switchLabel*
    ;
switchLabel returns [Case ast]:
      'case' constantExpression ':'
    | 'default' ':'
    ;
whileStatement returns [While ast]:
      'while' '(' expression ')' statement
    ;
doStatement returns [Do ast]:
      'do' statement 'while' '(' expression ')' ';'
    ;
forStatement returns [For ast]:
      basicForStatement
    | enhancedForStatement
    ;
basicForStatement returns [For ast]:
      'for' '(' forInit? ';' expression? ';' forUpdate? ')' statement
    ;
forInit returns [List<ForInit> ast]:
      statementExpressionList
    | localVariableDeclaration
    ;
forUpdate returns [List<ForUpdate> ast]:
      statementExpressionList
    ;
statementExpressionList returns [List<Eval> ast]:
      statementExpression (',' statementExpression)*
    ;
breakStatement returns [Branch ast]:
      'break' identifier? ';'
    ;
continueStatement returns [Branch ast]:
      'continue' identifier? ';'
    ;
returnStatement returns [Return ast]:
      'return' expression? ';'
    ;
throwStatement returns [Throw ast]:
      'throw' expression ';'
    ;
tryStatement returns [Try ast]:
      'try' block catches
    | 'try' block catches? finallyBlock
    ;
catches returns [List<Catch> ast]:
      catchClause+
    ;
catchClause returns [Catch ast]:
      'catch' '(' formalParameter ')' block
    ;
finallyBlock returns [Block ast]:
      'finally' block
    ;
clockedClause returns [List<Expr> ast]:
      'clocked' arguments
    ;
asyncStatement returns [Async ast]:
      'async' clockedClause? statement
    | 'clocked' 'async' statement
    ;
atStatement returns [AtStmt ast]:
      'at' '(' expression ')' statement
    ;
atomicStatement returns [Atomic ast]:
      'atomic' statement
    ;
whenStatement returns [When ast]:
      'when' '(' expression ')' statement
    ;
atEachStatement returns [X10Loop ast]:
      'ateach' '(' loopIndex 'in' expression ')' clockedClause? statement
    | 'ateach' '(' expression ')' statement
    ;
enhancedForStatement returns [X10Loop ast]:
      'for' '(' loopIndex 'in' expression ')' statement
    | 'for' '(' expression ')' statement
    ;
finishStatement returns [Finish ast]:
      'finish' statement
    | 'clocked' 'finish' statement
    ;
castExpression returns [Expr ast]:
      primary
    | expressionName
    | castExpression 'as' type
    ;
typeParamWithVarianceList returns [List<TypeParamNode> ast]:
      typeParameter
    | oBSOLETE_TypeParamWithVariance
    | typeParamWithVarianceList ',' typeParameter
    | typeParamWithVarianceList ',' oBSOLETE_TypeParamWithVariance
    ;
typeParameterList returns [List<TypeParamNode> ast]:
      typeParameter (',' typeParameter)*
    ;
oBSOLETE_TypeParamWithVariance returns [TypeParamNode ast]:
      '+' typeParameter
    | '-' typeParameter
    ;
typeParameter returns [TypeParamNode ast]:
      identifier
    ;
closureExpression returns [Closure ast]:
      formalParameters whereClause? hasResultType? oBSOLETE_Offers? '=>' closureBody
    ;
lastExpression returns [Return ast]:
      expression
    ;
closureBody returns [Block ast]:
      expression
    | annotationsopt '{' blockStatements? lastExpression '}'
    | annotationsopt block
    ;
atExpression returns [AtExpr ast]:
      annotationsopt 'at' '(' expression ')' closureBody
    ;
oBSOLETE_FinishExpression returns [FinishExpr ast]:
      'finish' '(' expression ')' block
    ;
typeName returns [ParsedName ast]:
      identifier
    | typeName '.' identifier
    ;
className returns [ParsedName ast]:
      typeName
    ;
typeArguments returns [List<TypeNode> ast]:
      '[' type (',' type)* ']'
    ;
packageName returns [ParsedName ast]:
      identifier                            #packageName0
    | packageName '.' identifier            #packageName1
    ;
expressionName returns [ParsedName ast]:
      identifier
    | fullyQualifiedName '.' identifier
    ;
methodName returns [ParsedName ast]:
      identifier
    | fullyQualifiedName '.' identifier
    ;
packageOrTypeName returns [ParsedName ast]:
      identifier
    | packageOrTypeName '.' identifier
    ;
fullyQualifiedName returns [ParsedName ast]:
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
interfaces returns [List<TypeNode> ast]:
      'implements' interfaceTypeList
    ;
interfaceTypeList returns [List<TypeNode> ast]:
      type (',' type)*
    ;
classBody returns [ClassBody ast]:
      '{' classMemberDeclarationsopt '}'
    ;
classMemberDeclarationsopt returns [List<ClassMember> ast]:
        classMemberDeclaration*
    ;
classMemberDeclaration returns [ClassMember ast]:
      interfaceMemberDeclaration
    | constructorDeclaration
    ;
formalDeclarators returns [List<Object> ast]:
      formalDeclarator (',' formalDeclarator)*
    ;
fieldDeclarators returns [List<Object> ast]:
      fieldDeclarator (',' fieldDeclarator)*
    ;
variableDeclaratorsWithType returns [List<Object[]> ast]:
      variableDeclaratorWithType (',' variableDeclaratorWithType)*
    ;
variableDeclarators returns [List<Object[]> ast]:
      variableDeclarator (',' variableDeclarator)*
    ;
homeVariableList returns [List<Node> ast]:
      homeVariable (',' homeVariable)*
    ;
homeVariable returns [Node ast]:
      identifier
    | 'this'
    ;
variableInitializer returns [Expr ast]:
      expression
    ;
resultType returns [TypeNode ast]:
      ':' type
    ;
hasResultType returns [TypeNode astx]:
      resultType
    | '<:' type
    ;
formalParameterList returns [List<Formal> ast]:
      formalParameter (',' formalParameter)*
    ;
loopIndexDeclarator returns [Object[] ast]:
      identifier hasResultType?
    | '[' identifierList ']' hasResultType?
    | identifier '[' identifierList ']' hasResultType?
    ;
loopIndex returns [Formal ast]:
      modifiersopt loopIndexDeclarator
    | modifiersopt varKeyword loopIndexDeclarator
    ;
formalParameter returns [Formal ast]:
      modifiersopt formalDeclarator
    | modifiersopt varKeyword formalDeclarator
    | type
    ;
oBSOLETE_Offers returns [TypeNode ast]:
      'offers' type
    ;
throws_ returns [List<TypeNode> ast]:
      'throws' type (',' type)*
    ;
methodBody returns [Block ast]:
      '=' lastExpression ';'
    | '=' annotationsopt '{' blockStatements? lastExpression '}'
    | '=' annotationsopt block
    | annotationsopt block
    | ';'
    ;
constructorBody returns [Block ast]:
      '=' constructorBlock
    | constructorBlock
    | '=' explicitConstructorInvocation
    | '=' assignPropertyCall
    | ';'
    ;
constructorBlock returns [Block ast]:
      '{' explicitConstructorInvocation? blockStatements? '}'
    ;
arguments returns [List<Expr> ast]:
      '(' argumentList ')'
    ;
extendsInterfaces returns [List<TypeNode> ast]:
      'extends' type (',' type)*
    ;
interfaceBody returns [ClassBody ast]:
      '{' interfaceMemberDeclarationsopt '}'
    ;
interfaceMemberDeclarationsopt returns [List<ClassMember> ast]:
        interfaceMemberDeclaration*
    ;
interfaceMemberDeclaration returns [ClassMember ast]:
      methodDeclaration
    | propertyMethodDeclaration
    | fieldDeclaration
    | typeDeclaration
    ;
annotationsopt returns [List<AnnotationNode> ast]:
      annotation*
    ;
annotations returns [List<AnnotationNode> ast]:
      annotation+
    ;
annotation returns [AnnotationNode ast]:
      '@' namedTypeNoConstraints
    ;
identifier returns [Id ast]: 
      IDENTIFIER
    ;
block returns [Block ast]:
      '{' blockStatements? '}'
    ;
blockStatements returns [List<Stmt> ast]:
      blockInteriorStatement+
    ;
blockInteriorStatement returns [List<Stmt> ast]:
      localVariableDeclarationStatement
    | classDeclaration
    | structDeclaration
    | typeDefDeclaration
    | statement
    ;
identifierList returns [List<Id> ast]:
      identifier (',' identifier)*
    ;
formalDeclarator returns [Object[] ast]:
      identifier resultType
    | '[' identifierList ']' resultType
    | identifier '[' identifierList ']' resultType
    ;
fieldDeclarator returns [Object[] ast]:
      identifier hasResultType
    | identifier hasResultType? '=' variableInitializer
    ;
variableDeclarator returns [Object[] ast]:
      identifier hasResultType? '=' variableInitializer
    | '[' identifierList ']' hasResultType? '=' variableInitializer
    | identifier '[' identifierList ']' hasResultType? '=' variableInitializer
    ;
variableDeclaratorWithType returns [Object[] ast]:
      identifier hasResultType '=' variableInitializer
    | '[' identifierList ']' hasResultType '=' variableInitializer
    | identifier '[' identifierList ']' hasResultType '=' variableInitializer
    ;
localVariableDeclarationStatement returns [List<LocalDecl> ast]:
      localVariableDeclaration ';'
    ;
localVariableDeclaration returns [List<LocalDecl> ast]:
      modifiersopt varKeyword variableDeclarators
    | modifiersopt variableDeclaratorsWithType
    | modifiersopt varKeyword formalDeclarators
    ;
primary returns [Expr ast]:
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
    | className '.' 'operator' 'as' '[' type ']' typeArguments? '(' argumentList? ')'
    | className '.' 'operator' '[' type ']' typeArguments? '(' argumentList? ')'
//    | operatorPrefix typeArguments? '(' argumentList? ')'
    | 'operator' binOp
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
literal returns [Lit ast]:
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
argumentList returns [List<Expr> ast]:
      expression (',' expression)*
    ;
fieldAccess returns [Field ast]:
      primary '.' identifier
    | 'super' '.' identifier
    | className '.' 'super' '.' identifier
    ;
conditionalExpression returns [Expr ast]:
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

assignmentExpression returns [Expr ast]:
      assignment
    | conditionalExpression
    ;
assignment returns [Expr ast]:
      leftHandSide assignmentOperator assignmentExpression
    | expressionName '(' argumentList? ')' assignmentOperator assignmentExpression
    | primary '(' argumentList? ')' assignmentOperator assignmentExpression
    ;
leftHandSide returns [Expr ast]:
      expressionName
    | fieldAccess
    ;
assignmentOperator returns [Assign.Operator ast]:
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
expression returns [Expr ast]:
      assignmentExpression
    ;
constantExpression returns [Expr ast]:
      expression
    ;
prefixOp returns [Unary.Operator ast]:
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
binOp returns [Binary.Operator ast]:
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

