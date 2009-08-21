%options fp=JavaParser
%options package=org.eclipse.imp.x10dt.formatter.parser
%options template=btParserTemplate.gi
%options import_terminals=GJavaLexer.gi

%Notice
/.
//
// This is the grammar specification from the Final Draft of the generic spec.
// It has been modified by Philippe Charles and Vijay Saraswat for use with 
// X10. 
// (1) Removed TypeParameters from class/interface/method declarations
// (2) Removed TypeParameters from types.
// (3) Removed Annotations -- cause conflicts with @ used in places.
// (4) Removed EnumDeclarations.
// 12/28/2004
./
%End 

%Identifier
    IDENTIFIER
%End

%Terminals

    IntegerLiteral        -- the usual
    LongLiteral           -- IntegerLiteral followed by 'l' or 'L'
    FloatingPointLiteral  --
                          -- FloatingPointLiteral ::= Digits . Digits? ExponentPart? FloatingTypeSuffix?
                          --                        | . Digits ExponentPart? FloatingTypeSuffix?
                          --                        | Digits ExponentPart FloatingTypeSuffix?
                          --                        | Digits ExponentPart? FloatingTypeSuffix
                          --
                          -- ExponentPart ::= ('e'|'E') ('+'|'-')? Digits
                          -- FloatingTypeSuffix ::= 'f' |  'F'
                          --
    DoubleLiteral         -- See FloatingPointLiteral except that
                          -- FloatingTypeSuffix ::= 'd' | 'D'
                          --
    CharacterLiteral      -- the usual
    StringLiteral         -- the usual

    MINUS_MINUS ::= '--'
    OR ::= '|' 
    MINUS ::= -
    MINUS_EQUAL ::= -=
    NOT ::= !
    NOT_EQUAL ::= !=
    REMAINDER ::= '%'
    REMAINDER_EQUAL ::= '%='
    AND ::= &
    AND_AND ::= && 
    AND_EQUAL ::= &= 
    LPAREN ::= (
    RPAREN ::= )
    MULTIPLY ::= *
    MULTIPLY_EQUAL ::= *=
    COMMA ::= ,
    DOT ::= .
    DIVIDE ::= / 
    DIVIDE_EQUAL ::= /= 
    COLON ::= :
    SEMICOLON ::= ;
    QUESTION ::= ?
    AT ::= @  
    LBRACKET ::= '['
    RBRACKET ::= ']'
    XOR ::= ^ 
    XOR_EQUAL ::= ^=
    LBRACE ::= {
    OR_OR ::= || 
    OR_EQUAL ::= |=  
    RBRACE ::= }  
    TWIDDLE ::= ~  
    PLUS ::= + 
    PLUS_PLUS ::= ++
    PLUS_EQUAL ::= +=
    LESS ::= <  
    LEFT_SHIFT ::= << 
    LEFT_SHIFT_EQUAL ::= <<= 
    LESS_EQUAL ::= <=
    GREATER_EQUAL ::= >=
    EQUAL ::= =  
    EQUAL_EQUAL ::= ==  
    GREATER ::= >
    ELLIPSIS ::= ...
    ELLIPSIS_OPT ::= ...opt
    COMMA_OPT ::= ,opt
%End

%Start
    CompilationUnit
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
    identifier ::= IDENTIFIER$ident
        
    -- Chapter 4

--
-- See X10 definition
--
--    Type ::= PrimitiveType
--           | ReferenceType

--   PrimitiveType ::= NumericType
--                    | boolean
--        
    NumericType ::= IntegralType
                  | FloatingPointType

    IntegralType ::= byte
                           | char
                           | short
                           | int
                           | long
        
    FloatingPointType ::= float
                                | double
        
    ReferenceType ::= ClassOrInterfaceType
                    | ArrayType

--
-- PC: This rules is subsumed by the rule:
--
--    ClassOrInterfaceType ::= TypeName DepParametersopt
--
--    ClassOrInterfaceType ::= ClassType
--            --
    -- Remove an obvious conflict.
    --
    --                       | InterfaceType
    --
    -- ClassType ::= TypeName TypeArgumentsopt

    InterfaceType ::= TypeName TypeArgumentsopt

    TypeName ::= identifier
                       | TypeName . identifier
        
    ClassName ::= TypeName

    TypeVariable ::= identifier

    ArrayType ::= Type '[' Annotationsopt ']'
        
    TypeParameter ::= TypeVariable TypeBoundopt
        
    TypeBound ::= extends ClassOrInterfaceType AdditionalBoundListopt
        
    AdditionalBoundList ::= AdditionalBound
                                  | AdditionalBoundList AdditionalBound
        
    AdditionalBound ::= & InterfaceType
        
    TypeArguments ::= < ActualTypeArgumentList >
        
    ActualTypeArgumentList ::= ActualTypeArgument
                                     | ActualTypeArgumentList , ActualTypeArgument
        
--
-- See X10 definition
--
--    ActualTypeArgument ::= ReferenceType
--                         | Wildcard

    Wildcard ::= ? WildcardBoundsOpt
        
    WildcardBounds ::= extends ReferenceType
                             | super ReferenceType
        
    -- Chapter 5

    -- Chapter 6

    PackageName ::= identifier
                          | PackageName . identifier
        
    --
    -- See Chapter 4
    --
    -- TypeName ::= identifier
    --           | PackageOrTypeName . identifier
    --
    ExpressionName ::=? identifier
                             | AmbiguousName . identifier
        
    MethodName ::=? identifier
                         | AmbiguousName . identifier
        
    PackageOrTypeName ::= identifier
                                | PackageOrTypeName . identifier
        
    AmbiguousName ::=? identifier
                            | AmbiguousName . identifier
        
    -- Chapter 7

    CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
        
    ImportDeclarations ::= ImportDeclaration
                                 | ImportDeclarations ImportDeclaration
        
    TypeDeclarations ::= TypeDeclaration
                               | TypeDeclarations TypeDeclaration
        
    PackageDeclaration ::= Annotationsopt package PackageName ;

    ImportDeclaration ::= SingleTypeImportDeclaration
                        | TypeImportOnDemandDeclaration
                        | SingleStaticImportDeclaration
                        | StaticImportOnDemandDeclaration

    SingleTypeImportDeclaration ::= import TypeName ;
        
    TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            
    SingleStaticImportDeclaration ::= import static TypeName . identifier ;
        
    StaticImportOnDemandDeclaration ::= import static TypeName . * ;
        
    TypeDeclaration ::= ClassDeclaration
                      | InterfaceDeclaration
                      | ;
        
    -- Chapter 8

    ClassDeclaration ::= NormalClassDeclaration
                       | EnumDeclaration

    NormalClassDeclaration ::= ClassModifiersopt class identifier TypeParametersopt Superopt Interfacesopt ClassBody

    ClassModifiers ::= ClassModifier
                             | ClassModifiers ClassModifier
        
    ClassModifier ::= Annotation
                            | public
                            | protected
                            | private
                            | abstract
                            | static
                            | final
                            | strictfp
        
    TypeParameters ::= < TypeParameterList >
            
    TypeParameterList ::= TypeParameter
                                | TypeParameterList , TypeParameter
        
     Super ::= extends ClassType
        
    --
    -- See Chapter 4
    --
    --ClassType ::= TypeName TypeArgumentsopt
    --
    Interfaces ::= implements InterfaceTypeList
        
    InterfaceTypeList ::= InterfaceType
                                | InterfaceTypeList , InterfaceType
        
    --
    -- See Chapter 4
    --
    --InterfaceType ::= TypeName TypeArgumentsopt
    --
    ClassBody ::= { ClassBodyDeclarationsopt }
        
    ClassBodyDeclarations ::= ClassBodyDeclaration
                            | ClassBodyDeclarations ClassBodyDeclaration
        
    ClassBodyDeclaration ::= ClassMemberDeclaration
                           | InstanceInitializer
                                   | StaticInitializer
                                   | ConstructorDeclaration
        
    ClassMemberDeclaration ::= FieldDeclaration
                             | MethodDeclaration
                                     | ClassDeclaration
                                     | InterfaceDeclaration
                                     | ;
            
    FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators ;

    
    VariableDeclarators ::= VariableDeclarator
                                  | VariableDeclarators , VariableDeclarator
            
    VariableDeclarator ::= TraditionalVariableDeclaratorId
                         | VariableDeclaratorId = VariableInitializer
            
    TraditionalVariableDeclaratorId ::= identifier
                                   | TraditionalVariableDeclaratorId '[' ']'
        
    VariableDeclaratorId ::= TraditionalVariableDeclaratorId
                           | identifier '[' IdentifierList ']'
                                   | '[' IdentifierList ']'
            
    VariableInitializer ::= Expression
                          | ArrayInitializer
    
    FieldModifiers ::= FieldModifier
                             | FieldModifiers FieldModifier
            
    FieldModifier ::= Annotation
                            | public
                            | protected
                            | private
                            | static
                            | final
                            | transient
                            | volatile
            
    MethodDeclaration ::= MethodHeader MethodBody
      
    
    MethodHeader ::= MethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt
    
    ResultType ::= Type
                 | void
            
    -- MethodDeclarator ::= identifier ( FormalParameterListopt )
    --        --                  | MethodDeclarator [ ]
     --        
    FormalParameterList ::= LastFormalParameter
                                  | FormalParameters , LastFormalParameter
            
    FormalParameters ::= FormalParameter
                               | FormalParameters , FormalParameter
            
    FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            
    VariableModifiers ::= VariableModifier
                                | VariableModifiers VariableModifier
            
    VariableModifier ::= final
                               | Annotation
            
    LastFormalParameter ::= VariableModifiersopt Type EllipsisOpt VariableDeclaratorId
        
    --
    -- See above
    --
    --VariableDeclaratorId ::= identifier
    --                       | VariableDeclaratorId [ ]
    --    
    MethodModifiers ::= MethodModifier
                              | MethodModifiers MethodModifier
            
    MethodModifier ::= Annotation
                             | public
                             | protected
                             | private
                             | abstract
                             | static
                             | final
                             | synchronized
                             | native
                             | strictfp
            
    Throws ::= throws ExceptionTypeList
            
    ExceptionTypeList ::= ExceptionType
                                | ExceptionTypeList , ExceptionType
            
    ExceptionType ::= ClassType
--
--pc
--
-- TypeVariable is subsumed by ClassType
--
--                    | TypeVariable
--            
    MethodBody ::= Block
                 | ;
            
    InstanceInitializer ::= Block
    
    StaticInitializer ::= static Block
            
  -- ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
   --        
--   ConstructorDeclarator ::= TypeParametersopt SimpleTypeName ( FormalParameterListopt )
    
    SimpleTypeName ::= identifier
        
    ConstructorModifiers ::= ConstructorModifier
                                   | ConstructorModifiers ConstructorModifier
            
    ConstructorModifier ::= Annotation
                                  | public
                                  | protected
                                  | private
            
    ConstructorBody ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            
    ExplicitConstructorInvocation ::= TypeArgumentsopt this ( ArgumentListopt ) ;
                                    | TypeArgumentsopt super ( ArgumentListopt ) ;
                                    | Primary . TypeArgumentsopt this ( ArgumentListopt ) ;
                                    | Primary . TypeArgumentsopt super ( ArgumentListopt ) ;
    
    EnumDeclaration ::= ClassModifiersopt enum identifier Interfacesopt EnumBody
            
    EnumBody ::= { EnumConstantsopt ,opt$opt EnumBodyDeclarationsopt }
            
    EnumConstants ::= EnumConstant
                            | EnumConstants , EnumConstant
            
    EnumConstant ::= Annotationsopt identifier Argumentsopt ClassBodyopt
    
    Arguments ::= ( ArgumentListopt )
            
    EnumBodyDeclarations ::= ; ClassBodyDeclarationsopt
            
    -- chapter 9
    
    InterfaceDeclaration ::= NormalInterfaceDeclaration
                           | AnnotationTypeDeclaration
            
    NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier TypeParametersopt ExtendsInterfacesopt InterfaceBody
    
    InterfaceModifiers ::= InterfaceModifier
                                 | InterfaceModifiers InterfaceModifier
            
    InterfaceModifier ::= Annotation
                                | public
                                | protected
                                | private
                                | abstract
                                | static
                                | strictfp
            
    ExtendsInterfaces ::= extends InterfaceType
                                | ExtendsInterfaces , InterfaceType
            
    --
    -- See Chapter 4
    --
    --InterfaceType ::= TypeName TypeArgumentsOpt
    
    InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            
    InterfaceMemberDeclarations ::= InterfaceMemberDeclaration
                                  | InterfaceMemberDeclarations InterfaceMemberDeclaration
            
    InterfaceMemberDeclaration ::= ConstantDeclaration
                                 | AbstractMethodDeclaration
                                         | ClassDeclaration
                                         | InterfaceDeclaration
                                         | ;
            
    ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            
    ConstantModifiers ::= ConstantModifier
                                | ConstantModifiers ConstantModifier
            
    ConstantModifier ::= Annotation
                               | public
                               | static
                               | final
            
    AbstractMethodDeclaration ::= AbstractMethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt ;
    
    AbstractMethodModifiers ::= AbstractMethodModifier
                                      | AbstractMethodModifiers AbstractMethodModifier
            
    AbstractMethodModifier ::= Annotation
                               | public
                               | abstract
            
    AnnotationTypeDeclaration ::= InterfaceModifiersopt @ interface identifier AnnotationTypeBody
            
    AnnotationTypeBody ::= { AnnotationTypeElementDeclarationsopt }
            
    AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclaration
                                                | AnnotationTypeElementDeclarations AnnotationTypeElementDeclaration
            
    AnnotationTypeElementDeclaration ::= AbstractMethodModifiersopt Type identifier ( ) DefaultValueopt ;
                                               | ConstantDeclaration
                                               | ClassDeclaration
                                               | InterfaceDeclaration
                                               | EnumDeclaration
                                               | AnnotationTypeDeclaration
                                               | ;
            
    DefaultValue ::= default ElementValue
            
    Annotations ::= Annotation
                          | Annotations Annotation
            
    Annotation ::= @ InterfaceType
            
    NormalAnnotation ::= @ InterfaceType
            
    ElementValuePairs ::= ElementValuePair
                                | ElementValuePairs , ElementValuePair
            
    ElementValuePair ::= SimpleName = ElementValue
            
    SimpleName ::= identifier
        
    ElementValue ::= ConditionalExpression
                           | Annotation
                           | ElementValueArrayInitializer
            
    ElementValueArrayInitializer ::= { ElementValuesopt ,opt$opt }
            
    ElementValues ::= ElementValue
                            | ElementValues , ElementValue
            
    MarkerAnnotation ::= @ TypeName
            
    SingleElementAnnotation ::= @ TypeName ( ElementValue )
            
    -- Chapter 10
    
    ArrayInitializer ::= { VariableInitializersopt ,opt$opt }
            
    VariableInitializers ::= VariableInitializer
                                   | VariableInitializers , VariableInitializer
            
    --
    -- See Chapter 8
    --
    --VariableInitializer ::= Expression
    --                      | ArrayInitializer
    
    -- Chapter 11
    
    -- Chapter 12
    
    -- Chapter 13
    
    -- Chapter 14
    
    Block ::= { BlockStatementsopt }
            
    BlockStatements ::= BlockStatement
                              | BlockStatements BlockStatement
            
    BlockStatement ::= LocalVariableDeclarationStatement
                     | ClassDeclaration
                             | Statement
            
    LocalVariableDeclarationStatement ::= LocalVariableDeclaration ;
    
    LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            
    --
    -- See Chapter 8
    --
    --VariableDeclarators ::= VariableDeclarator
    --                      | VariableDeclarators , VariableDeclarator
    --
    --VariableDeclarator ::= VariableDeclaratorId
    --                     | VariableDeclaratorId = VariableInitializer
    --
    --VariableDeclaratorId ::= identifier
    --                       | VariableDeclaratorId [ ]
    --
    --VariableInitializer ::= Expression
    --                      | ArrayInitializer
    
    Statement ::= StatementWithoutTrailingSubstatement
                | LabeledStatement
                | IfThenStatement
                | IfThenElseStatement
                | WhileStatement
                | ForStatement

    
    StatementWithoutTrailingSubstatement ::= Block
                                           | EmptyStatement
                                           | ExpressionStatement
                                           | AssertStatement
                                           | SwitchStatement
                                           | DoStatement
                                           | BreakStatement
                                           | ContinueStatement
                                           | ReturnStatement
                                           | SynchronizedStatement
                                           | ThrowStatement
                                           | TryStatement
    
    StatementNoShortIf ::= StatementWithoutTrailingSubstatement
                         | LabeledStatementNoShortIf
                         | IfThenElseStatementNoShortIf
                         | WhileStatementNoShortIf
                         | ForStatementNoShortIf
    
    IfThenStatement ::= if ( Expression ) Statement
            
    IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
            
    IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf$true_stmt else StatementNoShortIf$false_stmt
            
    EmptyStatement ::= ;
            
    LabeledStatement ::= identifier : Statement
            
    LabeledStatementNoShortIf ::= identifier : StatementNoShortIf
            ExpressionStatement ::= StatementExpression ;
            
    StatementExpression ::= Assignment
                          | PreIncrementExpression
                          | PreDecrementExpression
                          | PostIncrementExpression
                          | PostDecrementExpression
                          | MethodInvocation
                          | ClassInstanceCreationExpression
    
    --
    -- Already specified above
    --
    --IfThenStatement ::= if ( Expression ) Statement
    --
    --IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
    --
    --IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf else StatementNoShortIf
    
    AssertStatement ::= assert Expression ;
                              | assert Expression$expr1 : Expression$expr2 ;
            
    SwitchStatement ::= switch ( Expression ) SwitchBlock
            
    SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            
    SwitchBlockStatementGroups ::= SwitchBlockStatementGroup
                                 | SwitchBlockStatementGroups SwitchBlockStatementGroup
            
    SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            
    SwitchLabels ::= SwitchLabel
                           | SwitchLabels SwitchLabel
            
    SwitchLabel ::= case ConstantExpression :
                          | case EnumConstant :
                          | default :
        
    EnumConstant ::= identifier
            
    WhileStatement ::= while ( Expression ) Statement
            
    WhileStatementNoShortIf ::= while ( Expression ) StatementNoShortIf
            
    DoStatement ::= do Statement while ( Expression ) ;
            
    ForStatement ::= BasicForStatement
                   | EnhancedForStatement
    
    BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            
    ForStatementNoShortIf ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) StatementNoShortIf
            
    ForInit ::= StatementExpressionList
              | LocalVariableDeclaration
            
    ForUpdate ::= StatementExpressionList
    
    StatementExpressionList ::= StatementExpression
                                      | StatementExpressionList , StatementExpression
            
--    EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
--            
    BreakStatement ::= break identifieropt ;
            
    ContinueStatement ::= continue identifieropt ;
            
    ReturnStatement ::= return Expressionopt ;
            
    ThrowStatement ::= throw Expression ;
            
    SynchronizedStatement ::= synchronized ( Expression ) Block
            
    TryStatement ::= try Block Catches
                           | try Block Catchesopt Finally
            
    Catches ::= CatchClause
                      | Catches CatchClause
            
    CatchClause ::= catch ( FormalParameter ) Block
            
    Finally ::= finally Block
            
    --
    -- See Chapter 8
    --
    --FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
    --
    --VariableDeclaratorId ::= identifier
    --                       | VariableDeclaratorId [ ]
    
    -- Chapter 15
    
    Primary ::= PrimaryNoNewArray
              | ArrayCreationExpression
    
    PrimaryNoNewArray ::= Literal
                        | Type . class
                                | void . class
                                | this
                                | ClassName . this
                                | ( Expression )
                                | ClassInstanceCreationExpression
                        | FieldAccess
                        | MethodInvocation
                        | ArrayAccess
    
    Literal ::= IntegerLiteral$IntegerLiteral
                      | LongLiteral$LongLiteral
                      | FloatingPointLiteral$FloatLiteral
                      | DoubleLiteral$DoubleLiteral
                      | BooleanLiteral
                      | CharacterLiteral$CharacterLiteral
                      | StringLiteral$str
                      | null
        
    BooleanLiteral ::= true$trueLiteral
                             | false$falseLiteral
        
    --
    -- The following case appeared to be missing from the spec:
    --
    --                                | identifier . new TypeArgumentsopt identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
    --
    ClassInstanceCreationExpression ::=  new TypeArgumentsopt ClassOrInterfaceType TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
                                      | Primary . new TypeArgumentsopt identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
                                      | AmbiguousName . new TypeArgumentsopt identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
    
    ArgumentList ::= Expression
                           | ArgumentList , Expression
        
--
-- The rules below were specified however, from some examples,
-- it would appear that ClassOrInterfaceType is expected instead
-- of TypeName
--
--                              | new TypeName DimExprs Dimsopt
--                              | new TypeName Dims ArrayInitializer
--
--    ArrayCreationExpression ::= new PrimitiveType DimExprs Dimsopt
--        --                              | new ClassOrInterfaceType DimExprs Dimsopt
--        --                              | new PrimitiveType Dims ArrayInitializer
--        --                              | new ClassOrInterfaceType Dims ArrayInitializer
--            
    DimExprs ::= DimExpr
                       | DimExprs DimExpr
            
    DimExpr ::= '[' Expression ']'
            
    Dims ::= '[' ']'
                   | Dims '[' ']'
            
    FieldAccess ::= Primary . identifier
                          | super . identifier
                          | ClassName . super$sup . identifier
            
    MethodInvocation ::= MethodName ( ArgumentListopt )
                               | Primary . TypeArgumentsopt identifier ( ArgumentListopt )
                       | super . TypeArgumentsopt identifier ( ArgumentListopt )
                       | ClassName . super . TypeArgumentsopt identifier ( ArgumentListopt )
                       | TypeName . TypeArguments identifier ( ArgumentListopt )

    --
    -- See above
    --
    --ArgumentList ::= Expression
    --               | ArgumentList , Expression
    --
--
-- See X10 definition
--
--    ArrayAccess ::= ExpressionName [ Expression ]
--                  | PrimaryNoNewArray [ Expression ]
    
    PostfixExpression ::= Primary
                        | ExpressionName
                                | PostIncrementExpression
                        | PostDecrementExpression
    
    PostIncrementExpression ::= PostfixExpression ++
            
    PostDecrementExpression ::= PostfixExpression '--'
            
    UnaryExpression ::= PreIncrementExpression
                      | PreDecrementExpression
                      | + UnaryExpression
                              | - UnaryExpression
                              | UnaryExpressionNotPlusMinus
    
    PreIncrementExpression ::= ++ UnaryExpression
            
    PreDecrementExpression ::= '--' UnaryExpression
            
    UnaryExpressionNotPlusMinus ::= PostfixExpression
                                  | ~ UnaryExpression
                                          | ! UnaryExpression
                                          | CastExpression
    
--
-- See X10 definition
--
--    CastExpression ::= ( PrimitiveType Dimsopt ) UnaryExpression
--                     | ( ReferenceType ) UnaryExpressionNotPlusMinus
    
    MultiplicativeExpression ::= UnaryExpression
                               | MultiplicativeExpression * UnaryExpression
                                       | MultiplicativeExpression / UnaryExpression
                                       | MultiplicativeExpression '%' UnaryExpression
            
    AdditiveExpression ::= MultiplicativeExpression
                         | AdditiveExpression + MultiplicativeExpression
                                 | AdditiveExpression - MultiplicativeExpression
            
    ShiftExpression ::= AdditiveExpression
                      | ShiftExpression << AdditiveExpression
                              | ShiftExpression > > AdditiveExpression
                              | ShiftExpression > > > AdditiveExpression
            
    RelationalExpression ::= ShiftExpression
                           | RelationalExpression < ShiftExpression
                                   | RelationalExpression > ShiftExpression
                                   | RelationalExpression <= ShiftExpression
                                   | RelationalExpression > = ShiftExpression
        --
-- See X10 definition
--
--                           | RelationalExpression instanceof ReferenceType
--            
    EqualityExpression ::= RelationalExpression
                         | EqualityExpression == RelationalExpression
                                 | EqualityExpression != RelationalExpression
            
    AndExpression ::= EqualityExpression
                    | AndExpression & EqualityExpression
            
    ExclusiveOrExpression ::= AndExpression
                            | ExclusiveOrExpression ^ AndExpression
            
    InclusiveOrExpression ::= ExclusiveOrExpression
                            | InclusiveOrExpression '|' ExclusiveOrExpression
            
    ConditionalAndExpression ::= InclusiveOrExpression
                               | ConditionalAndExpression && InclusiveOrExpression
            
    ConditionalOrExpression ::= ConditionalAndExpression
                              | ConditionalOrExpression || ConditionalAndExpression
            
    ConditionalExpression ::= ConditionalOrExpression
                            | ConditionalOrExpression ? Expression : ConditionalExpression
            
    AssignmentExpression ::= ConditionalExpression
                           | Assignment
    
    Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            
    LeftHandSide ::= ExpressionName
                           | FieldAccess
                   | ArrayAccess
    
    AssignmentOperator ::= =
                                 | *=
                                 | /=
                                 | '%='
                                 | +=
                                 | -=
                                 | <<=
                                 | > > =
                                 | > > > =
                                 | &=
                                 | ^=
                                 | |=
            
    Expression ::= AssignmentExpression
    
    ConstantExpression ::= Expression
    
    --
    -- Optional rules
    --
    Dimsopt ::= %Empty
                      | Dims

    Catchesopt ::= %Empty
                         | Catches

    identifieropt ::= %Empty
                            | identifier
        
    ForUpdateopt ::= %Empty
                           | ForUpdate

    Expressionopt ::= %Empty
                            | Expression

    ForInitopt ::= %Empty
                         | ForInit

    SwitchLabelsopt ::= %Empty
                              | SwitchLabels

    SwitchBlockStatementGroupsopt ::= %Empty
                                            | SwitchBlockStatementGroups

    VariableModifiersopt ::= %Empty
                                   | VariableModifiers

    VariableInitializersopt ::= %Empty
                                      | VariableInitializers

    ElementValuesopt ::= %Empty
                               | ElementValues
        
    ElementValuePairsopt ::= %Empty
                                   | ElementValuePairs
        
    DefaultValueopt ::= %Empty
                              | DefaultValue

    AnnotationTypeElementDeclarationsopt ::= %Empty
                                                   | AnnotationTypeElementDeclarations
        
    AbstractMethodModifiersopt ::= %Empty
                                         | AbstractMethodModifiers

    ConstantModifiersopt ::= %Empty
                                   | ConstantModifiers

    InterfaceMemberDeclarationsopt ::= %Empty
                                             | InterfaceMemberDeclarations

    ExtendsInterfacesopt ::= %Empty
                                   | ExtendsInterfaces

    InterfaceModifiersopt ::= %Empty
                                    | InterfaceModifiers

    ClassBodyopt ::= %Empty
                           | ClassBody

    Argumentsopt ::= %Empty
                           | Arguments

    EnumBodyDeclarationsopt ::= %Empty
                                      | EnumBodyDeclarations
        
    CommaOpt ::= %Empty
                   | ,

    EnumConstantsopt ::= %Empty
                               | EnumConstants
        
    ArgumentListopt ::= %Empty
                              | ArgumentList

    BlockStatementsopt ::= %Empty
                                 | BlockStatements

    ExplicitConstructorInvocationopt ::= %Empty
                                               | ExplicitConstructorInvocation

    ConstructorModifiersopt ::= %Empty
                                      | ConstructorModifiers

    EllipsisOpt ::= %Empty
                     | ...

    FormalParameterListopt ::= %Empty
                                     | FormalParameterList

    Throwsopt ::= %Empty
                        | Throws

    MethodModifiersopt ::= %Empty
                                 | MethodModifiers

    FieldModifiersopt ::= %Empty
                                | FieldModifiers

    ClassBodyDeclarationsopt ::= %Empty
                                       | ClassBodyDeclarations

    Interfacesopt ::= %Empty
                            | Interfaces

    Superopt ::= %Empty
                       | Super

    TypeParametersopt ::= %Empty
                                | TypeParameters

    ClassModifiersopt ::= %Empty
                                | ClassModifiers

    Annotationsopt ::= %Empty
                             | Annotations

    TypeDeclarationsopt ::= %Empty
                                  | TypeDeclarations

    ImportDeclarationsopt ::= %Empty
                                    | ImportDeclarations

    PackageDeclarationopt ::= %Empty
                                    | PackageDeclaration

    WildcardBoundsOpt ::= %Empty
                                | WildcardBounds
        
    AdditionalBoundListopt ::= %Empty
                                     | AdditionalBoundList
        
    TypeBoundopt ::= %Empty
                           | TypeBound
        
    TypeArgumentsopt ::= %Empty
                               | TypeArguments
        %End
