grammar X10;

@parser::header {
  package x10.parserGen;
  
  import x10.parser.antlr.ASTBuilder.Modifier;
  import polyglot.parse.*;
  import polyglot.ast.*;
  import x10.ast.*;
}

import X10_Lexer;

modifiersopt returns [List<Modifier> ast]:
        modifier*
    ;
modifier returns [Modifier ast]:
      'abstract'   #modifierAbstract
    | annotation   #modifierAnnotation
    | 'atomic'     #modifierAtomic
    | 'final'      #modifierFinal
    | 'native'     #modifierNative
    | 'private'    #modifierPrivate
    | 'protected'  #modifierProtected
    | 'public'     #modifierPublic
    | 'static'     #modifierStatic
    | 'transient'  #modifierTransient
    | 'clocked'    #modifierClocked
    ;
methodModifiersopt returns [List<Modifier> ast]:
        methodModifier*
    ;
methodModifier returns [Modifier ast]:
      modifier     #methodModifierModifier
    | 'property'   #methodModifierProperty
    ;
typeDefDeclaration returns [TypeDecl ast]:
      modifiersopt 'type' identifier typeParametersopt ('(' formalParameterList ')')? whereClauseopt '=' type ';'
    ;
propertiesopt returns [List<PropertyDecl> ast]:
        ('(' property (',' property)* ')')?
    ;
property returns [PropertyDecl ast]:
      annotationsopt identifier resultType
    ;
methodDeclaration returns [ProcedureDecl ast]:
      methodModifiersopt 'def' identifier typeParametersopt formalParameters whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody    #methodDeclarationMethod
    | binaryOperatorDeclaration        #methodDeclarationBinaryOp
    | prefixOperatorDeclaration        #methodDeclarationPrefixOp
    | applyOperatorDeclaration         #methodDeclarationApplyOp
    | setOperatorDeclaration           #methodDeclarationSetOp
    | conversionOperatorDeclaration    #methodDeclarationConversionOp
    ;
binaryOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' typeParametersopt '(' fp1=formalParameter ')' binOp '(' fp2=formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody     #binaryOperatorDecl
    | methodModifiersopt 'operator' typeParametersopt 'this' binOp '(' fp2=formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody                          #binaryOperatorDeclThisLeft
    | methodModifiersopt 'operator' typeParametersopt '(' fp1=formalParameter ')' binOp 'this' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody                          #binaryOperatorDeclThisRight
    ;
prefixOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' typeParametersopt prefixOp '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody     #prefixOperatorDecl
    | methodModifiersopt 'operator' typeParametersopt prefixOp 'this' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody                      #prefixOperatorDeclThis
    ;
applyOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' 'this' typeParametersopt formalParameters whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    ;
setOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' 'this' typeParametersopt formalParameters '=' '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    ;
conversionOperatorDeclaration returns [MethodDecl ast]:
      explicitConversionOperatorDeclaration     #conversionOperatorDeclarationExplicit
    | implicitConversionOperatorDeclaration     #conversionOperatorDeclarationImplicit
    ;
explicitConversionOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' typeParametersopt '(' formalParameter ')' 'as' type whereClauseopt oBSOLETE_Offersopt throwsopt methodBody                     #explicitConversionOperatorDecl0
    | methodModifiersopt 'operator' typeParametersopt '(' formalParameter ')' 'as' '?' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody     #explicitConversionOperatorDecl1
    ;
implicitConversionOperatorDeclaration returns [MethodDecl ast]:
      methodModifiersopt 'operator' typeParametersopt '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody
    ;
propertyMethodDeclaration returns [MethodDecl ast]:
      methodModifiersopt identifier typeParametersopt formalParameters whereClauseopt hasResultTypeopt methodBody     #propertyMethodDecl0
    | methodModifiersopt identifier whereClauseopt hasResultTypeopt methodBody                                        #propertyMethodDecl1 // This case cannot occur. It corresponds to a field declaration.
    ;
explicitConstructorInvocation returns [ConstructorCall ast]:
      'this' typeArgumentsopt '(' argumentListopt ')' ';'                  #explicitConstructorInvocationThis
    | 'super' typeArgumentsopt '(' argumentListopt ')' ';'                 #explicitConstructorInvocationSuper
    | primary '.' 'this' typeArgumentsopt '(' argumentListopt ')' ';'      #explicitConstructorInvocationPrimaryThis
    | primary '.' 'super' typeArgumentsopt '(' argumentListopt ')' ';'     #explicitConstructorInvocationPrimarySuper
    ;
interfaceDeclaration returns [ClassDecl ast]:
      modifiersopt 'interface' identifier typeParamsWithVarianceopt propertiesopt whereClauseopt extendsInterfacesopt interfaceBody
    ;
assignPropertyCall returns [Stmt ast]:
      'property' typeArgumentsopt '(' argumentListopt ')' ';'
    ;
type returns [TypeNode ast]:
      functionType         #typeFunctionType
    | constrainedType      #typeConstrainedType
    | void_                #typeVoid
    | type annotations     #typeAnnotations
    ;
functionType returns [TypeNode ast]:
      typeParametersopt '(' formalParameterListopt ')' whereClauseopt oBSOLETE_Offersopt '=>' type
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
simpleNamedType returns [AmbTypeNode ast]:
      typeName                                                                       #simpleNamedType0
    | primary '.' identifier                                                         #simpleNamedType1
    | simpleNamedType typeArgumentsopt argumentsopt depParameters? '.' identifier    #simpleNamedType2
    ;
parameterizedNamedType returns [AmbTypeNode ast]:
      simpleNamedType typeArguments? arguments?
    ;
depNamedType returns [TypeNode ast]:
      parameterizedNamedType depParameters
    ;
namedTypeNoConstraints returns [TypeNode ast]:
      parameterizedNamedType
    ;
namedType returns [TypeNode ast]:
      depNamedType                               #namedType1
    | namedTypeNoConstraints                     #namedType0
    ;
depParameters returns [DepParameterExpr ast]:
      '{' /* fUTURE_ExistentialList? */ constraintConjunctionopt '}'
    ;
typeParamsWithVarianceopt returns [List<TypeParamNode> ast]:
      ('[' typeParamWithVarianceList ']')?
    ;
typeParametersopt returns [List<TypeParamNode> ast]:
      ('[' typeParameterList ']')?
    ;
formalParameters returns [List<Formal> ast]:
      '(' formalParameterListopt ')'
    ;
constraintConjunctionopt returns [List<Expr> ast]:
      (expression (',' expression)*)?
    ;
hasZeroConstraint returns [HasZeroTest ast]:
      type 'haszero'
    ;
isRefConstraint returns [IsRefTest ast]:
      type 'isref'
    ;
subtypeConstraint returns [SubtypeTest ast]:
      t1=type '<:' t2=type     #subtypeConstraint0
    | t1=type ':>' t2=type     #subtypeConstraint1
    ;
whereClauseopt returns [DepParameterExpr ast]:
      depParameters?
    ;
// fUTURE_ExistentialList:
//       formalParameter (';' formalParameter)*
//     ;
classDeclaration returns [ClassDecl ast]:
      modifiersopt 'class' identifier typeParamsWithVarianceopt propertiesopt whereClauseopt superExtendsopt interfacesopt classBody
    ;
structDeclaration returns [ClassDecl ast]:
      modifiersopt 'struct' identifier typeParamsWithVarianceopt propertiesopt whereClauseopt interfacesopt classBody
    ;
constructorDeclaration returns [ConstructorDecl ast]:
      modifiersopt 'def' id='this' typeParametersopt formalParameters whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt constructorBody
    ;
superExtendsopt returns [TypeNode ast]:
      ('extends' classType)?
    ;
varKeyword returns [List<FlagsNode> ast]:
      'val'     #varKeyword0
    | 'var'     #varKeyword1
    ;
fieldDeclaration returns [List<ClassMember> ast]:
      modifiersopt varKeyword? fieldDeclarators ';'
    ;
statement returns [Stmt ast]:
      annotationStatement     #statement0
    | expressionStatement     #statement1
    ;
annotationStatement returns [Stmt ast]:
      annotationsopt nonExpressionStatement
    ;
nonExpressionStatement returns [Stmt ast]:
      block                        #nonExpressionStatemen0
    | emptyStatement               #nonExpressionStatemen1
    | assertStatement              #nonExpressionStatemen2
    | switchStatement              #nonExpressionStatemen3
    | doStatement                  #nonExpressionStatemen4
    | breakStatement               #nonExpressionStatemen5
    | continueStatement            #nonExpressionStatemen6
    | returnStatement              #nonExpressionStatemen7
    | throwStatement               #nonExpressionStatemen8
    | tryStatement                 #nonExpressionStatemen9
    | labeledStatement             #nonExpressionStatemen10
    | ifThenStatement              #nonExpressionStatemen11
    | whileStatement               #nonExpressionStatemen13
    | forStatement                 #nonExpressionStatemen14
    | asyncStatement               #nonExpressionStatemen15
    | atStatement                  #nonExpressionStatemen16
    | atomicStatement              #nonExpressionStatemen17
    | whenStatement                #nonExpressionStatemen18
    | atEachStatement              #nonExpressionStatemen19
    | finishStatement              #nonExpressionStatemen20
    | assignPropertyCall           #nonExpressionStatemen21
    | oBSOLETE_OfferStatement      #nonExpressionStatemen22
    ;
oBSOLETE_OfferStatement returns [Offer ast]:
      'offer' expression ';'
    ;
ifThenStatement returns [If ast]:
      'if' '(' expression ')' s1=statement ('else' s2=statement)?
    ;
emptyStatement returns [Empty ast]:
      ';'
    ;
labeledStatement returns [Labeled ast]:
      identifier ':' loopStatement
    ;
loopStatement returns [Stmt ast]:
      forStatement        #loopStatement0
    | whileStatement      #loopStatement1
    | doStatement         #loopStatement2
    | atEachStatement     #loopStatement3
    ;
expressionStatement returns [Eval ast]:
      expression ';'
    ;
assertStatement returns [Assert ast]:
      'assert' expression ';'                         #assertStatement0
    | 'assert' e1=expression ':' e2=expression ';'    #assertStatement1
    ;
switchStatement returns [Switch ast]:
      'switch' '(' expression ')' switchBlock
    ;
switchBlock returns [List<SwitchElement> ast]:
      '{' switchBlockStatementGroupsopt switchLabelsopt '}'
    ;
switchBlockStatementGroupsopt returns [List<SwitchElement> ast]:
        switchBlockStatementGroup*
    ;
switchBlockStatementGroup returns [List<SwitchElement> ast]:
      switchLabels blockStatements
    ;
switchLabelsopt returns [List<Case> ast]:
        switchLabels?
    ;
switchLabels returns [List<Case> ast]:
        switchLabel+
    ;
switchLabel returns [Case ast]:
      'case' constantExpression ':'    #switchLabel0
    | 'default' ':'                    #switchLabel1
    ;
whileStatement returns [While ast]:
      'while' '(' expression ')' statement
    ;
doStatement returns [Do ast]:
      'do' statement 'while' '(' expression ')' ';'
    ;
forStatement returns [Loop ast]:
      basicForStatement        #forStatement0
    | enhancedForStatement     #forStatement1
    ;
basicForStatement returns [For ast]:
      'for' '(' forInitopt ';' expressionopt ';' forUpdateopt ')' statement
    ;
forInit returns [List<? extends ForInit> ast]:
      statementExpressionList     #forInit0
    | localVariableDeclaration    #forInit1
    ;
forUpdate returns [List<? extends ForUpdate> ast]:
      statementExpressionList
    ;
statementExpressionList returns [List<? extends Eval> ast]:
      expression (',' expression)*
    ;
breakStatement returns [Branch ast]:
      'break' identifieropt ';'
    ;
continueStatement returns [Branch ast]:
      'continue' identifieropt ';'
    ;
returnStatement returns [Return ast]:
      'return' expressionopt ';'
    ;
throwStatement returns [Throw ast]:
      'throw' expression ';'
    ;
tryStatement returns [Try ast]:
      'try' block catches                    #tryStatement0
    | 'try' block catchesopt finallyBlock    #tryStatement1
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
clockedClauseopt returns [List<Expr> ast]:
      ('clocked' arguments)?
    ;
asyncStatement returns [Async ast]:
      'async' clockedClauseopt statement    #asyncStatement0
    | 'clocked' 'async' statement           #asyncStatement1
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
      'ateach' '(' loopIndex 'in' expression ')' clockedClauseopt statement     #atEachStatement0
    | 'ateach' '(' expression ')' statement                                     #atEachStatement1
    ;
enhancedForStatement returns [X10Loop ast]:
      'for' '(' loopIndex 'in' expression ')' statement     #enhancedForStatement0
    | 'for' '(' expression ')' statement                    #enhancedForStatement1
    ;
finishStatement returns [Finish ast]:
      'finish' statement                #finishStatement0
    | 'clocked' 'finish' statement      #finishStatement1
    ;
castExpression returns [Expr ast]:
      primary                          #castExpression0
    | expressionName                   #castExpression1
    | castExpression 'as' type         #castExpression2
    ;
typeParamWithVarianceList returns [List<TypeParamNode> ast]:
      typeParameter                                                     #typeParamWithVarianceList0
    | oBSOLETE_TypeParamWithVariance                                    #typeParamWithVarianceList1
    | typeParamWithVarianceList ',' typeParameter                       #typeParamWithVarianceList2
    | typeParamWithVarianceList ',' oBSOLETE_TypeParamWithVariance      #typeParamWithVarianceList3
    ;
typeParameterList returns [List<TypeParamNode> ast]:
      typeParameter (',' typeParameter)*
    ;
oBSOLETE_TypeParamWithVariance returns [TypeParamNode ast]:
      '+' typeParameter     #oBSOLETE_TypeParamWithVariance0
    | '-' typeParameter     #oBSOLETE_TypeParamWithVariance1
    ;
typeParameter returns [TypeParamNode ast]:
      identifier
    ;
closureExpression returns [Closure ast]:
      formalParameters whereClauseopt hasResultTypeopt oBSOLETE_Offersopt '=>' closureBody
    ;
lastExpression returns [Return ast]:
      expression
    ;
closureBody returns [Block ast]:
      expression                                               #closureBody0
    | annotationsopt '{' blockStatementsopt lastExpression '}'   #closureBody1
    | annotationsopt block                                     #closureBody2
    ;
atExpression returns [AtExpr ast]:
      annotationsopt 'at' '(' expression ')' closureBody
    ;
oBSOLETE_FinishExpression returns [FinishExpr ast]:
      'finish' '(' expression ')' block
    ;
typeName returns [ParsedName ast]:
      identifier                #typeName0
    | typeName '.' identifier   #typeName1
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
      identifier                            #expressionName0
    | fullyQualifiedName '.' identifier     #expressionName1
    ;
methodName returns [ParsedName ast]:
      identifier                            #methodName0
    | fullyQualifiedName '.' identifier     #methodName1
    ;
packageOrTypeName returns [ParsedName ast]:
      identifier                            #packageOrTypeName0
    | packageOrTypeName '.' identifier      #packageOrTypeName1
    ;
fullyQualifiedName returns [ParsedName ast]:
      identifier                            #fullyQualifiedName0
    | fullyQualifiedName '.' identifier     #fullyQualifiedName1
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
      'import' typeName ';'                    #importDeclaration0 // singleTypeImportDeclaration
    | 'import' packageOrTypeName '.' '*' ';'   #importDeclaration1 // typeImportOnDemandDeclaration
    ;
typeDeclarationsopt returns [List<TopLevelDecl> ast]:
        typeDeclaration*
    ;
typeDeclaration returns [TopLevelDecl ast]:
      classDeclaration         #typeDeclaration0
    | structDeclaration        #typeDeclaration1
    | interfaceDeclaration     #typeDeclaration2
    | typeDefDeclaration       #typeDeclaration3
    | ';'                      #typeDeclaration4
    ;
interfacesopt returns [List<TypeNode> ast]:
      ('implements' type (',' type)*)?
    ;
classBody returns [ClassBody ast]:
      '{' classMemberDeclaration* '}'
    ;
classMemberDeclaration returns [List<ClassMember> ast]:
      interfaceMemberDeclaration     #classMemberDeclaration0
    | constructorDeclaration         #classMemberDeclaration1
    ;
formalDeclarators returns [List<Object[]> ast]:
      formalDeclarator (',' formalDeclarator)*
    ;
fieldDeclarators returns [List<Object[]> ast]:
      fieldDeclarator (',' fieldDeclarator)*
    ;
variableDeclaratorsWithType returns [List<Object[]> ast]:
      variableDeclaratorWithType (',' variableDeclaratorWithType)*
    ;
variableDeclarators returns [List<Object[]> ast]:
      variableDeclarator (',' variableDeclarator)*
    ;
variableInitializer returns [Expr ast]:
      expression
    ;
resultType returns [TypeNode ast]:
      ':' type
    ;
hasResultType returns [TypeNode ast]:
      resultType     #hasResultType0
    | '<:' type      #hasResultType1
    ;
formalParameterList returns [List<Formal> ast]:
      formalParameter (',' formalParameter)*
    ;
loopIndexDeclarator returns [Object[] ast]:
      identifier hasResultTypeopt                             #loopIndexDeclarator0
    | '[' identifierList ']' hasResultTypeopt                 #loopIndexDeclarator1
    | identifier '[' identifierList ']' hasResultTypeopt      #loopIndexDeclarator2
    ;
loopIndex returns [X10Formal ast]:
      modifiersopt loopIndexDeclarator                #loopIndex0
    | modifiersopt varKeyword loopIndexDeclarator     #loopIndex1
    ;
formalParameter returns [X10Formal ast]:
      modifiersopt formalDeclarator                #formalParameter0
    | modifiersopt varKeyword formalDeclarator     #formalParameter1
    | type                                         #formalParameter2
    ;
oBSOLETE_Offersopt returns [TypeNode ast]:
      ('offers' type)?
    ;
throwsopt returns [List<TypeNode> ast]:
      ('throws' type (',' type)*)?
    ;
methodBody returns [Block ast]:
      '=' lastExpression ';'                                         #methodBody0
    | '=' annotationsopt '{' blockStatementsopt lastExpression '}'   #methodBody1
    | '='? annotationsopt block                                      #methodBody2
    | ';'                                                            #methodBody3
    ;
constructorBody returns [Block ast]:
      '='? constructorBlock                 #constructorBody0
    | '=' explicitConstructorInvocation     #constructorBody1
    | '=' assignPropertyCall                #constructorBody2
    | ';'                                   #constructorBody3
    ;
constructorBlock returns [Block ast]:
      '{' explicitConstructorInvocation? blockStatementsopt '}'
    ;
arguments returns [List<Expr> ast]:
      '(' argumentList ')'
    ;
extendsInterfacesopt returns [List<TypeNode> ast]:
      ('extends' type (',' type)*)?
    ;
interfaceBody returns [ClassBody ast]:
      '{' interfaceMemberDeclarationsopt '}'
    ;
interfaceMemberDeclarationsopt returns [List<ClassMember> ast]:
        interfaceMemberDeclaration*
    ;
interfaceMemberDeclaration returns [List<ClassMember> ast]:
      methodDeclaration             #interfaceMemberDeclaration0
    | fieldDeclaration              #interfaceMemberDeclaration2
    | propertyMethodDeclaration     #interfaceMemberDeclaration1
    | typeDeclaration               #interfaceMemberDeclaration3
    ;
annotationsopt returns [List<AnnotationNode> ast]:
      annotations?
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
      '{' blockStatementsopt '}'
    ;
blockStatements returns [List<Stmt> ast]:
      blockInteriorStatement+
    ;
blockInteriorStatement returns [List<Stmt> ast]:
      localVariableDeclarationStatement     #blockInteriorStatement0
    | classDeclaration                      #blockInteriorStatement1
    | structDeclaration                     #blockInteriorStatement2
    | typeDefDeclaration                    #blockInteriorStatement3
    | statement                             #blockInteriorStatement4
    ;
identifierList returns [List<Id> ast]:
      identifier (',' identifier)*
    ;
formalDeclarator returns [Object[] ast]:
      identifier resultType                            #formalDeclarator0
    | '[' identifierList ']' resultType                #formalDeclarator1
    | identifier '[' identifierList ']' resultType     #formalDeclarator2
    ;
fieldDeclarator returns [Object[] ast]:
      identifier hasResultType                              #fieldDeclarator0
    | identifier hasResultTypeopt '=' variableInitializer   #fieldDeclarator1
    ;
variableDeclarator returns [Object[] ast]:
      identifier hasResultTypeopt '=' variableInitializer                          #variableDeclarator0
    | '[' identifierList ']' hasResultTypeopt '=' variableInitializer              #variableDeclarator1
    | identifier '[' identifierList ']' hasResultTypeopt '=' variableInitializer   #variableDeclarator2
    ;
variableDeclaratorWithType returns [Object[] ast]:
      identifier hasResultType '=' variableInitializer                             #variableDeclaratorWithType0
    | '[' identifierList ']' hasResultType '=' variableInitializer                 #variableDeclaratorWithType1
    | identifier '[' identifierList ']' hasResultType '=' variableInitializer      #variableDeclaratorWithType2
    ;
localVariableDeclarationStatement returns [List<Stmt> ast]:
      localVariableDeclaration ';'
    ;
localVariableDeclaration returns [List<LocalDecl> ast]:
      modifiersopt varKeyword variableDeclarators      #localVariableDeclaration0
    | modifiersopt variableDeclaratorsWithType         #localVariableDeclaration1
    | modifiersopt varKeyword formalDeclarators        #localVariableDeclaration2
    ;
primary returns [Expr ast]:
      'here'                              #primary0
    | '[' argumentListopt ']'             #primary1
    | literal                             #primary2
    | 'self'                              #primary3
    | 'this'                              #primary4
    | className '.' 'this'                #primary5
    | '(' expression ')'                  #primary6
    // classInstanceCreationExpression
    | 'new' typeName typeArgumentsopt '(' argumentListopt ')' classBodyopt                             #primary7
    | primary '.' 'new' identifier typeArgumentsopt '(' argumentListopt ')' classBodyopt               #primary8
    | fullyQualifiedName '.' 'new' identifier typeArgumentsopt '(' argumentListopt ')' classBodyopt    #primary9
    // fieldAccess
    | primary '.' identifier                    #primary10
    | s='super' '.' identifier                  #primary11
    | className '.' s='super' '.' identifier    #primary12
    // methodInvocation
    | methodName typeArgumentsopt '(' argumentListopt ')'                                     #primary13
    | primary '.' identifier typeArgumentsopt '(' argumentListopt ')'                         #primary14
    | s='super' '.' identifier typeArgumentsopt '(' argumentListopt ')'                       #primary15
    | className '.' s='super' '.' identifier typeArgumentsopt '(' argumentListopt ')'         #primary16
    | primary typeArgumentsopt '(' argumentListopt ')'                                        #primary17
    | className '.' 'operator' 'as' '[' type ']' typeArgumentsopt '(' argumentListopt ')'     #primary18
    | className '.' 'operator' '[' type ']' typeArgumentsopt '(' argumentListopt ')'          #primary19
    // operatorPrefix
    | 'operator' binOp typeArgumentsopt '(' argumentListopt ')'                                       #primary20
    | fullyQualifiedName '.' 'operator' binOp typeArgumentsopt '(' argumentListopt ')'                #primary21
    | primary '.' 'operator' binOp typeArgumentsopt '(' argumentListopt ')'                           #primary22
    | s='super' '.' 'operator' binOp typeArgumentsopt '(' argumentListopt ')'                         #primary23
    | className '.' s='super' '.' 'operator' binOp typeArgumentsopt '(' argumentListopt ')'           #primary24
    | 'operator' '(' ')' binOp typeArgumentsopt '(' argumentListopt ')'                               #primary25
    | fullyQualifiedName '.' 'operator' '(' ')' binOp typeArgumentsopt '(' argumentListopt ')'        #primary26
    | primary '.' 'operator' '(' ')' binOp typeArgumentsopt '(' argumentListopt ')'                   #primary27
    | s='super' '.' 'operator' '(' ')' binOp typeArgumentsopt '(' argumentListopt ')'                 #primary28
    | className '.' s='super' '.' 'operator' '(' ')' binOp typeArgumentsopt '(' argumentListopt ')'   #primary29
    | 'operator' parenthesisOp typeArgumentsopt '(' argumentListopt ')'                                     #primary30
    | fullyQualifiedName '.' 'operator' parenthesisOp typeArgumentsopt '(' argumentListopt ')'              #primary31
    | primary '.' 'operator' parenthesisOp typeArgumentsopt '(' argumentListopt ')'                         #primary32
    | s='super' '.' 'operator' parenthesisOp typeArgumentsopt '(' argumentListopt ')'                       #primary33
    | className '.' s='super' '.' 'operator' parenthesisOp typeArgumentsopt '(' argumentListopt ')'         #primary34
    | 'operator' parenthesisOp '=' typeArgumentsopt '(' argumentListopt ')'                                 #primary35
    | fullyQualifiedName '.' 'operator' parenthesisOp '=' typeArgumentsopt '(' argumentListopt ')'          #primary36
    | primary '.' 'operator' parenthesisOp '=' typeArgumentsopt '(' argumentListopt ')'                     #primary37
    | s='super' '.' 'operator' parenthesisOp '=' typeArgumentsopt '(' argumentListopt ')'                   #primary38
    | className '.' s='super' '.' 'operator' parenthesisOp '=' typeArgumentsopt '(' argumentListopt ')'     #primary39
    ;
literal returns [Lit ast]:
      IntLiteral             #IntLiteral
    | LongLiteral            #LongLiteral
    | ByteLiteral            #ByteLiteral
    | UnsignedByteLiteral    #UnsignedByteLiteral
    | ShortLiteral           #ShortLiteral
    | UnsignedShortLiteral   #UnsignedShortLiteral
    | UnsignedIntLiteral     #UnsignedIntLiteral
    | UnsignedLongLiteral    #UnsignedLongLiteral
    | FloatingPointLiteral   #FloatingPointLiteral
    | DoubleLiteral          #DoubleLiteral
    | booleanLiteral         #Literal10
    | CharacterLiteral       #CharacterLiteral
    | StringLiteral          #StringLiteral
    | 'null'                 #NullLiteral
    ;
booleanLiteral returns [BooleanLit ast]:
      'true'
    | 'false'
    ;
argumentList returns [List<Expr> ast]:
      expression (',' expression)*
    ;
fieldAccess returns [Field ast]:
      primary '.' identifier                   #fieldAccess0
    | s='super' '.' identifier                 #fieldAccess1
    | className '.' s='super' '.' identifier   #fieldAccess2
    ;
conditionalExpression returns [Expr ast]:
      castExpression                                                            #conditionalExpression0
    | conditionalExpression op=('++'|'--')                                      #conditionalExpression1
    | annotations conditionalExpression                                         #conditionalExpression2
    | op=('+'|'-'|'++'|'--') conditionalExpression                              #conditionalExpression3
    | op=('~'|'!'|'^'|'|'|'&'|'*'|'/'|'%') conditionalExpression                #conditionalExpression4
    | e1=conditionalExpression '..' e2=conditionalExpression                    #conditionalExpression5
    | e1=conditionalExpression op=('*'|'/'|'%'|'**') e2=conditionalExpression   #conditionalExpression6
    | e1=conditionalExpression op=('+'|'-') e2=conditionalExpression            #conditionalExpression7
    | hasZeroConstraint                                                         #conditionalExpression8
    | isRefConstraint                                                           #conditionalExpression9
    | subtypeConstraint                                                         #conditionalExpression10
    | e1=conditionalExpression op=('<<'|'>>'|'>>>'|'->'|'<-'|'-<'|'>-'|'!'|'<>'|'><') e2=conditionalExpression   #conditionalExpression11
    | conditionalExpression 'instanceof' type                                   #conditionalExpression12
    | e1=conditionalExpression op=('<'|'>'|'<='|'>=') e2=conditionalExpression  #conditionalExpression13
    | e1=conditionalExpression op=('=='|'!=') e2=conditionalExpression          #conditionalExpression14
 //   | t1=type '==' t2=type                                                      #conditionalExpression15 // Danger some type equalities can be capture by the previous rule
    | e1=conditionalExpression op=('~'|'!~') e2=conditionalExpression           #conditionalExpression16
    | e1=conditionalExpression '&' e2=conditionalExpression                     #conditionalExpression17
    | e1=conditionalExpression '^' e2=conditionalExpression                     #conditionalExpression18
    | e1=conditionalExpression '|' e2=conditionalExpression                     #conditionalExpression19
    | e1=conditionalExpression '&&' e2=conditionalExpression                    #conditionalExpression20
    | e1=conditionalExpression '||' e2=conditionalExpression                    #conditionalExpression21
    | closureExpression                                                         #conditionalExpression22
    | atExpression                                                              #conditionalExpression23
    | oBSOLETE_FinishExpression                                                 #conditionalExpression24
    | <assoc=right> e1=conditionalExpression '?' e2=conditionalExpression ':' e3=conditionalExpression   #conditionalExpression25
    | type                                                                      #conditionalExpression26
    ;

assignmentExpression returns [Expr ast]:
      assignment                #assignmentExpression0
    | conditionalExpression     #assignmentExpression1
    ;
assignment returns [Expr ast]:
      leftHandSide assignmentOperator assignmentExpression                             #assignment0
    | expressionName '(' argumentListopt ')' assignmentOperator assignmentExpression   #assignment1
    | primary '(' argumentListopt ')' assignmentOperator assignmentExpression          #assignment2
    ;
leftHandSide returns [Expr ast]:
      expressionName      #leftHandSide0
    | fieldAccess         #leftHandSide1
    ;
expression returns [Expr ast]:
      assignmentExpression
    ;
constantExpression returns [Expr ast]:
      expression
    ;
assignmentOperator returns [Assign.Operator ast]:
      '='    #assignmentOperator0
    | '*='   #assignmentOperator1
    | '/='   #assignmentOperator2
    | '%='   #assignmentOperator3
    | '+='   #assignmentOperator4
    | '-='   #assignmentOperator5
    | '<<='  #assignmentOperator6
    | '>>='  #assignmentOperator7
    | '>>>=' #assignmentOperator8
    | '&='   #assignmentOperator9
    | '^='   #assignmentOperator10
    | '|='   #assignmentOperator11
    | '..='  #assignmentOperator12
    | '->='  #assignmentOperator13
    | '<-='  #assignmentOperator14
    | '-<='  #assignmentOperator15
    | '>-='  #assignmentOperator16
    | '**='  #assignmentOperator17
    | '<>='  #assignmentOperator18
    | '><='  #assignmentOperator19
    | '~='   #assignmentOperator20
    ;
prefixOp returns [Unary.Operator ast]:
      '+'  #prefixOp0
    | '-'  #prefixOp1
    | '!'  #prefixOp2
    | '~'  #prefixOp3
    | '^'  #prefixOp4
    | '|'  #prefixOp5
    | '&'  #prefixOp6
    | '*'  #prefixOp7
    | '/'  #prefixOp8
    | '%'  #prefixOp9
    ;
binOp returns [Binary.Operator ast]:
      '+'  #binOp0
    | '-'  #binOp1
    | '*'  #binOp2
    | '/'  #binOp3
    | '%'  #binOp4
    | '&'  #binOp5
    | '|'  #binOp6
    | '^'  #binOp7
    | '&&' #binOp8
    | '||' #binOp9
    | '<<' #binOp10
    | '>>' #binOp11
    | '>>>' #binOp12
    | '>=' #binOp13
    | '<=' #binOp14
    | '>'  #binOp15
    | '<'  #binOp16
    | '==' #binOp17
    | '!=' #binOp18
    | '..' #binOp19
    | '->' #binOp20
    | '<-' #binOp21
    | '-<' #binOp22
    | '>-' #binOp23
    | '**' #binOp24
    | '~'  #binOp25
    | '!~' #binOp26
    | '!'  #binOp27
    | '<>' #binOp28
    | '><' #binOp29
    ;
parenthesisOp:
       '(' ')'
    ;

hasResultTypeopt returns [TypeNode ast]:
      hasResultType?
    ;
typeArgumentsopt returns [List<TypeNode> ast]:
      typeArguments?
    ;
argumentListopt returns [List<Expr> ast]:
      argumentList?
    ;
argumentsopt returns [List<Expr> ast]:
      arguments?
    ;
identifieropt returns [Id ast]:
      identifier?
    ;
forInitopt returns [List<? extends ForInit> ast]:
      forInit?
    ;
forUpdateopt returns [List<? extends ForUpdate> ast]:
      forUpdate?
    ;
expressionopt returns [Expr ast]:
      expression?
    ;
catchesopt returns [List<Catch> ast]:
      catches?
    ;
blockStatementsopt returns [List<Stmt> ast]:
      blockStatements?
    ;
classBodyopt returns [ClassBody ast]:
      classBody?
    ;
formalParameterListopt returns [List<Formal> ast]:
        formalParameterList?
    ;
