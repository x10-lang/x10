%options fp=X10Parser,states
%options list
%options la=6
%options variables=nt
%options conflicts
%options softkeywords
%options package=org.eclipse.imp.x10dt.formatter.parser
%options template=btParserTemplate.gi
%options import_terminals="x10Lexer.gi"

--%include
--   "MissingId.gi"
--%End

%Notice
%End

%Globals
    %End

%Import
    GJavaParserForX10.gi

    %DropSymbols
        ActualTypeArgumentList
        ActualTypeArgument
        AdditionalBound
        AdditionalBoundList
        AdditionalBoundListopt
        -- Annotation
        -- Annotations
        -- Annotationsopt
        AnnotationTypeBody
        AnnotationTypeDeclaration
        AnnotationTypeElementDeclaration
        AnnotationTypeElementDeclarations
        AnnotationTypeElementDeclarationsopt
        SingleStaticImportDeclaration
        StaticImportOnDemandDeclaration
        TypeArguments
        TypeArgumentsopt
        TypeVariable
        TypeParameter
        TypeBoundopt
        TypeBound
        Wildcard
        WildcardBoundsOpt
        WildcardBounds
        TypeParameterList
        TypeParametersopt
        TypeParameters
        EnumDeclaration
        EnumBody
        EnumBodyDeclarationsopt
        EnumConstantsopt
        EnumConstants
        EnumConstant
        EnumBodyDeclarations
        DefaultValueopt
        DefaultValue
        ElementValue
        NormalAnnotation
        MarkerAnnotation
        SingleElementAnnotation
        ElementValuePairsopt
        ElementValuePairs
        ElementValuePair
        ElementValueArrayInitializer
        ElementValuesopt
        ElementValues

    %DropRules
        ImportDeclaration ::= SingleStaticImportDeclaration
                            | StaticImportOnDemandDeclaration
        SwitchLabel ::= case EnumConstant :
        ReferenceType ::= TypeVariable
        ClassType ::= TypeName TypeArgumentsopt
        InterfaceType ::= TypeName TypeArgumentsopt
        PackageDeclaration ::= Annotationsopt package PackageName ;
        ClassDeclaration ::= EnumDeclaration
        NormalClassDeclaration ::= ClassModifiersopt class identifier TypeParametersopt Superopt Interfacesopt ClassBody
        -- ClassModifier ::= Annotation
        -- FieldModifier ::= Annotation
        --                 | volatile
        FieldModifier ::= volatile
        MethodHeader ::= MethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt
        MethodDeclaration ::= MethodHeader MethodBody
        -- VariableModifier ::= Annotations
        -- MethodModifier ::= Annotations
        --                  | synchronized
        MethodModifier ::= synchronized
        SynchronizedStatement ::= synchronized ( Expression ) Block
        ConstructorDeclarator ::= TypeParametersopt SimpleTypeName ( FormalParameterListopt )
        FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators ;
        -- ConstructorModifier ::= Annotations
        ExplicitConstructorInvocation ::= TypeArgumentsopt this ( ArgumentListopt ) ;
                                        | TypeArgumentsopt super ( ArgumentListopt ) ;
                                        | Primary . TypeArgumentsopt this ( ArgumentListopt ) ;
                                        | Primary . TypeArgumentsopt super ( ArgumentListopt ) ;
        InterfaceDeclaration ::= AnnotationTypeDeclaration
        NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier TypeParametersopt ExtendsInterfacesopt InterfaceBody
        -- InterfaceModifier ::= Annotation
        -- ConstantModifier ::= Annotation
        AbstractMethodDeclaration ::= AbstractMethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt ;
        -- AbstractMethodModifier ::= Annotations
        ClassInstanceCreationExpression ::=  new TypeArgumentsopt ClassOrInterfaceType TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
                                          | Primary . new TypeArgumentsopt identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
                                          | AmbiguousName . new TypeArgumentsopt identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        MethodInvocation ::= Primary . TypeArgumentsopt identifier ( ArgumentListopt )
                           | super . TypeArgumentsopt identifier ( ArgumentListopt )
                           | ClassName . super . TypeArgumentsopt identifier ( ArgumentListopt )
                           | TypeName . TypeArguments identifier ( ArgumentListopt )
%End

%Define
    --
    -- Definition of macros used in the parser template
    --
    $ast_class /.polyglot.ast.Node./
    $additional_interfaces /., Parser./
%End

%Terminals
--     RANGE ::= '..'
    ARROW ::= '->'
%End

%Keywords
    --
    -- All X10 keywords are soft
    --
    activitylocal
    async
    ateach 
    atomic 
    await
    boxed
    clocked
    compilertest
    current 
    extern
    finish
    foreach 
    fun
    future 
    here
    local
    method
    mutable
    next
    nonblocking
    now
    nullable 
    or
    placelocal
    reference
    safe
    self
    sequential
    unsafe
    value
    when

    --
    -- All Java Primitive types are soft
    --
    boolean
    byte
    char
    short
    int
    long
    float
    double
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

%Rules -- Overridden rules from GJavaParser
    ClassType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
        

    InterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
        
    PackageDeclaration ::= package PackageName ;
        
    NormalClassDeclaration ::= X10ClassModifiersopt class identifier PropertyListopt  Superopt Interfacesopt ClassBody
        
    X10ClassModifiers ::= X10ClassModifier
                             | X10ClassModifiers X10ClassModifier
        
    X10ClassModifier ::= ClassModifier 
                            | safe
        
    PropertyList ::= ( Properties WhereClauseopt )
                  | ( WhereClause )
                   

       Properties ::= Property
                                  | Properties , Property
            
    
    Property ::=  Type identifier
             --     MethodDeclaration ::= MethodHeader MethodBody
            MethodDeclaration ::= ThisClauseopt MethodModifiersopt ResultType MethodDeclarator Throwsopt MethodBody
        
    ExplicitConstructorInvocation ::= this ( ArgumentListopt ) ;
                                            | super ( ArgumentListopt ) ;
                                            | Primary . this ( ArgumentListopt ) ;
                                            | Primary . super ( ArgumentListopt ) ;
        
    NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier PropertyListopt ExtendsInterfacesopt InterfaceBody
        
    AbstractMethodDeclaration ::= ThisClauseopt AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt ;
        
    ClassInstanceCreationExpression ::=  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
                                              | Primary . new identifier  ( ArgumentListopt ) ClassBodyopt
                                              | AmbiguousName . new identifier ( ArgumentListopt ) ClassBodyopt
        
    MethodInvocation ::= Primary .  identifier ( ArgumentListopt )
                               | super .  identifier ( ArgumentListopt )
                               | ClassName . super$sup .  identifier ( ArgumentListopt )
                
                       
      AssignPropertyCall ::= property ( ArgumentList ) 
%End

%Rules

    -------------------------------------- Section:::Types

    Type ::= DataType 
           | SpecialType
           | AnnotatedType
           
    AnnotatedType ::= Type Annotations
        
    SpecialType ::= nullable < Type > DepParametersopt
                    | future < Type > 
                    

    --
    -- TODO: 12/28/2004 ... This rule is a temporary patch that allows
    --                      us to work with the current Polyglot base.
    --                      It should be removed as there are no primitive
    --                      types in X10.
    DataType ::=  PrimitiveType
               |  ReferenceType

    PrimitiveType ::= NumericType DepParametersopt
                        | boolean DepParametersopt
           PlaceTypeSpecifier ::= ! PlaceType

    PlaceType ::= any
                | current 
                | PlaceExpression

    ClassOrInterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt 
        
    DepParameters ::= ( DepParameterExpr )
        
    DepParameterExpr ::= ArgumentList WhereClauseopt
                        | WhereClause
        
    WhereClause ::= : ConstExpression
        
-- May want to permit ArrayAccess as well.      
    ConstPrimary ::= Literal
               
                        | Type . class
                                | void . class
                                | this
                                | here
                                | ClassName . this
                                | ( ConstExpression )
               
                   | ConstFieldAccess
                   | self 
               
 
                
    ConstPostfixExpression  ::= 
                    ConstPrimary 
                                  |  ExpressionName
            ConstUnaryExpression ::= ConstPostfixExpression
                      | + ConstUnaryExpression 
                          | - ConstUnaryExpression
                          | ! ConstUnaryExpression
        
    ConstMultiplicativeExpression ::= ConstUnaryExpression
                                       | ConstMultiplicativeExpression * ConstUnaryExpression
                                       | ConstMultiplicativeExpression / ConstUnaryExpression
                                       | ConstMultiplicativeExpression % ConstUnaryExpression
        
    ConstAdditiveExpression ::= ConstMultiplicativeExpression
                                 | ConstAdditiveExpression + ConstMultiplicativeExpression
                                 | ConstAdditiveExpression - ConstMultiplicativeExpression
        
    ConstRelationalExpression ::=
                   ConstAdditiveExpression
                         | ConstRelationalExpression < ConstAdditiveExpression
                         | ConstRelationalExpression > ConstAdditiveExpression
                         | ConstRelationalExpression <= ConstAdditiveExpression
                         | ConstRelationalExpression >= ConstAdditiveExpression
            ConstEqualityExpression ::= 
                   ConstRelationalExpression
                         | ConstEqualityExpression == ConstRelationalExpression
                         | ConstEqualityExpression != ConstRelationalExpression
        
    ConstAndExpression ::= ConstEqualityExpression
                            | ConstAndExpression && ConstEqualityExpression
        
    ConstExclusiveOrExpression ::= ConstAndExpression
                                    | ConstExclusiveOrExpression ^ ConstAndExpression
        
    ConstInclusiveOrExpression ::= ConstExclusiveOrExpression
                                    | ConstInclusiveOrExpression || ConstExclusiveOrExpression
        
    ConstExpression ::= ConstInclusiveOrExpression
                                    | ConstInclusiveOrExpression ? ConstExpression$first : ConstExpression
        

    ConstFieldAccess ::= ConstPrimary . identifier
                          | super . identifier
                          | ClassName . super$sup . identifier
        
--    PropAccess ::= ConstTerm . identifier
--        
-- Not supporting jagged arrays for now.
-- We will support a special translation for the case in which
-- the expr in DepParameterExpr is of type int.

    ArrayType ::= X10ArrayType

    X10ArrayType ::= Type '[' . ']' -- X10ArrayTypeNode
             | Type value  '[' . ']'
             | Type '[' DepParameterExpr ']'
             | Type value '[' DepParameterExpr ']'
        
    -- Default is reference.
    ObjectKind ::= value
                         | reference
        

    ------------------------------------- Section ::: Classes
    MethodModifier ::= atomic
                             | extern
                             | safe
                               | sequential
                               | local
                               | nonblocking
        
    ClassDeclaration ::= ValueClassDeclaration

    ValueClassDeclaration ::= X10ClassModifiersopt value identifier PropertyListopt  Superopt Interfacesopt ClassBody
              | X10ClassModifiersopt value class identifier PropertyListopt Superopt Interfacesopt ClassBody
        
 ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
              
    ConstructorDeclarator ::=  SimpleTypeName DepParametersopt Annotationsopt ( FormalParameterListopt WhereClauseopt )
           ThisClause ::= this DepParameters 
          
     Super ::= extends DataType
            
    MethodDeclarator ::=  identifier ( FormalParameterListopt  WhereClauseopt )
                            | MethodDeclarator '[' ']'
        
     
    FieldDeclaration ::= ThisClauseopt FieldModifiersopt Type  VariableDeclarators ;
        
    --------------------------------------- Section ::: Arrays
    -- The dependent type array([D][:E])<T> is written as
    -- T[D:E].
    -- TODO: Support value 1D local arrays.
    ArrayCreationExpression ::=
--              new ArrayBaseType [ Expression ] -- Expression may be an int.
--        --       |
              new ArrayBaseType Unsafeopt Dims ArrayInitializer  -- produce a LocalArray
                |     new ArrayBaseType Unsafeopt DimExpr Dims  -- produce a LocalArray
                |     new ArrayBaseType Unsafeopt DimExpr DimExprs Dimsopt  -- produce a LocalArray
                |     new ArrayBaseType Valueopt Unsafeopt '['  Expression  ']' -- Expression may be a distribution or an int
                |     new ArrayBaseType Valueopt Unsafeopt '['  Expression$distr  ']' Expression$initializer
                |     new ArrayBaseType Valueopt Unsafeopt '['  Expression  ']' ($lparen FormalParameter ) MethodBody
        
    Valueopt ::= %Empty
                       | value
        
    ArrayBaseType ::= PrimitiveType
                    | ClassOrInterfaceType
                    | SpecialType
                    | ArrayType
                    | ( Type )
        
    ArrayAccess ::= ExpressionName '[' ArgumentList ']'
                          | PrimaryNoNewArray '[' ArgumentList ']'
        
    --------------------------------------- Section :: Statement
    Statement ::= NowStatement
                | AsyncStatement
                | AtomicStatement
                | WhenStatement
                | ForEachStatement
                | AtEachStatement
                | FinishStatement
                | AnnotationStatement


    StatementWithoutTrailingSubstatement ::= NextStatement
                                           | AwaitStatement
                                           | AssignPropertyCall

    StatementNoShortIf ::= NowStatementNoShortIf
                         | AsyncStatementNoShortIf
                         | AtomicStatementNoShortIf
                         | WhenStatementNoShortIf
                         | ForEachStatementNoShortIf
                         | AtEachStatementNoShortIf
                         | FinishStatementNoShortIf
                         | AnnotationStatementNoShortIf


    NowStatement ::= now ( Clock ) Statement
        
    ClockedClause ::= clocked ( ClockList )
        
    AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
        

    AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
        

    WhenStatement  ::= when ( Expression ) Statement
                             | WhenStatement or$or ( Expression ) Statement
        
    ForEachStatement ::= foreach ( FormalParameter : Expression ) ClockedClauseopt Statement
        
    AtEachStatement ::= ateach ( FormalParameter : Expression ) ClockedClauseopt Statement
        
    EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
        
    FinishStatement ::= finish Statement
        

    AnnotationStatement ::= Annotations Statement
        

    NowStatementNoShortIf ::= now ( Clock ) StatementNoShortIf
        
    AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt ClockedClauseopt StatementNoShortIf
        
    AtomicStatementNoShortIf ::= atomic StatementNoShortIf
        
    WhenStatementNoShortIf  ::= when ( Expression ) StatementNoShortIf
                                      | WhenStatement or$or ( Expression ) StatementNoShortIf
        
    ForEachStatementNoShortIf ::= foreach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
        
    AtEachStatementNoShortIf ::= ateach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
        
    EnhancedForStatementNoShortIf ::= for ( FormalParameter : Expression ) StatementNoShortIf
        
    FinishStatementNoShortIf ::= finish StatementNoShortIf
        

    AnnotationStatementNoShortIf ::= Annotations StatementNoShortIf
        

    PlaceExpressionSingleList ::= ( PlaceExpression )
        
    PlaceExpression ::= Expression

    NextStatement ::= next ;
        
    AwaitStatement ::= await Expression ;
        
    ClockList ::= Clock
                        | ClockList , Clock
        
    -- The type-checker will ensure that the identifier names a variable declared as a clock.
    Clock ::= Expression
                --
--      Clock ::= identifier
--        

    CastExpression ::=
         ( PrimitiveType ) UnaryExpression
               | ( SpecialType ) UnaryExpressionNotPlusMinus
               | ( ReferenceType ) UnaryExpressionNotPlusMinus
               | ( ! Expression ) UnaryExpressionNotPlusMinus
               | ( AnnotatedType ) UnaryExpression
               | ( Annotations ) UnaryExpressionNotPlusMinus
        
--    MethodInvocation ::= Primary '->' identifier ( ArgumentListopt )
--        
    RelationalExpression ::= RelationalExpression instanceof Type
        
     --------------------------------------- Section :: Expression
     -- A list of identifiers containing at least one identifier.
     IdentifierList ::= identifier
                              | IdentifierList , identifier
        
    Primary ::= here
        
    Primary ::= FutureExpression

    RegionExpression ::= Expression
                       | Expression$expr1 : Expression$expr2
        
    RegionExpressionList ::= RegionExpression
                       | RegionExpressionList , RegionExpression
        
    Primary ::= '[' RegionExpressionList ']'
        
    AssignmentExpression ::= Expression$expr1 '->' Expression$expr2
        
    FutureExpression ::= future PlaceExpressionSingleListopt { Expression }
        
    FieldModifier ::= mutable
                            | const
        
    FunExpression ::= fun Type ( FormalParameterListopt ) { Expression }
            -- Simplified syntax to create a point.


 
    MethodInvocation ::= MethodName ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
                              | Primary . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
                              | super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
                              | ClassName . super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
                              | TypeName . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
       
    ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
                                             | Primary . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
                                             | AmbiguousName . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
      


    MethodModifier ::= synchronized
        
    FieldModifier ::= volatile

    SynchronizedStatement ::= synchronized ( Expression ) Block

    ---------------------------------------- All the opts...

ThisClauseopt ::= %Empty
                                   | ThisClause
                            
    PlaceTypeSpecifieropt ::= %Empty
                                   | PlaceTypeSpecifier

    DepParametersopt ::= %Empty
                               | DepParameters
    PropertyListopt ::=  %Empty
                               | PropertyList
                       
    WhereClauseopt ::= %Empty
                             | WhereClause

    ObjectKindopt ::= %Empty
                            | ObjectKind

    ArrayInitializeropt ::= %Empty
                                  | ArrayInitializer

    PlaceExpressionSingleListopt ::= %Empty
                                           | PlaceExpressionSingleList

    X10ClassModifiersopt ::= %Empty
                  | X10ClassModifiers
          
    Unsafeopt ::= %Empty
                        | unsafe
        
    ParamIdopt ::= %Empty
                         | identifier
        
    ClockedClauseopt ::= %Empty
                               | ClockedClause
%End

%Types
    Object ::= CommaOpt
             | EllipsisOpt
             | PlaceTypeSpecifieropt
             | PlaceTypeSpecifier
             | PlaceType
             | ObjectKind
             | ObjectKindopt
             | FunExpression

    SourceFile ::= CompilationUnit
    polyglot.ast.Lit ::= Literal
    TypeNode ::= Type
    TypeNode ::= SpecialType
    TypeNode ::= AnnotatedType
    TypeNode ::= PrimitiveType | NumericType
    TypeNode ::= IntegralType | FloatingPointType
    TypeNode ::= ReferenceType
    TypeNode ::= ClassOrInterfaceType
    TypeNode ::= ClassType | InterfaceType
    TypeNode ::= ArrayType
    Name ::= SimpleName
    PackageNode ::= PackageDeclarationopt | PackageDeclaration
    List ::= ImportDeclarationsopt | ImportDeclarations
    List ::= TypeDeclarationsopt | TypeDeclarations
    Import ::= ImportDeclaration
    Import ::= SingleTypeImportDeclaration
    Import ::= TypeImportOnDemandDeclaration
    ClassDecl ::= TypeDeclaration
    List ::= X10ClassModifier
            | X10ClassModifiers
            | X10ClassModifiersopt
    List ::= AbstractMethodModifier
            | AbstractMethodModifiers
            | AbstractMethodModifiersopt
    List ::= ClassModifier
            | ClassModifiers
            | ClassModifiersopt
    List ::= ConstantModifier
            | ConstantModifiers
            | ConstantModifiersopt
    List ::= ConstructorModifier
            | ConstructorModifiers
            | ConstructorModifiersopt
    List ::= FieldModifier
            | FieldModifiers
            | FieldModifiersopt
    List ::= InterfaceModifier
            | InterfaceModifiers
            | InterfaceModifiersopt
    List ::= MethodModifier
            | MethodModifiers
            | MethodModifiersopt
    List ::= VariableModifier
            | VariableModifiers
            | VariableModifiersopt
    ClassDecl ::= ClassDeclaration | NormalClassDeclaration
    TypeNode ::= Super | Superopt
    List ::= Interfaces | Interfacesopt | InterfaceTypeList
    ClassBody ::= ClassBody | ClassBodyopt
    List ::= ClassBodyDeclarations | ClassBodyDeclarationsopt
    List ::= ClassBodyDeclaration | ClassMemberDeclaration
    List ::= FieldDeclaration
    List ::= VariableDeclarators
    VarDeclarator ::= VariableDeclarator
    X10VarDeclarator ::= VariableDeclaratorId | TraditionalVariableDeclaratorId
    Expr ::= VariableInitializer
    MethodDecl ::= MethodDeclaration 
    List ::= FormalParameterListopt | FormalParameterList 
    X10Formal ::= FormalParameter
    List ::= Throwsopt | Throws
    Block ::= MethodBody
    Block ::= StaticInitializer
    ConstructorDecl ::= ConstructorDeclaration
    Block ::= ConstructorBody
    ConstructorCall ::= ExplicitConstructorInvocation
    ClassDecl ::= InterfaceDeclaration | NormalInterfaceDeclaration
    List ::= ExtendsInterfacesopt | ExtendsInterfaces
    ClassBody ::= InterfaceBody
    List ::= InterfaceMemberDeclarationsopt | InterfaceMemberDeclarations
    List ::= InterfaceMemberDeclaration
    List ::= ConstantDeclaration
    MethodDecl ::= AbstractMethodDeclaration
    ArrayInit ::= ArrayInitializer
                | ArrayInitializeropt
    List ::= VariableInitializers
    Block ::= Block
    List ::= BlockStatementsopt | BlockStatements
    List ::= BlockStatement
    List ::= LocalVariableDeclarationStatement
    List ::= LocalVariableDeclaration
    Stmt ::= Statement | StatementNoShortIf
    Stmt ::= StatementWithoutTrailingSubstatement
    Empty ::= EmptyStatement
    Labeled ::= LabeledStatement | LabeledStatementNoShortIf
    Stmt ::= ExpressionStatement
    Expr ::= StatementExpression
    If ::= IfThenStatement
    If ::= IfThenElseStatement | IfThenElseStatementNoShortIf
    Switch ::= SwitchStatement
    List ::= SwitchBlock | SwitchBlockStatementGroups
    List ::= SwitchBlockStatementGroup | SwitchLabels
    Case ::= SwitchLabel
    While ::= WhileStatement | WhileStatementNoShortIf
    Do ::= DoStatement
    For ::= ForStatement | ForStatementNoShortIf
    Stmt ::= AnnotationStatement | AnnotationStatementNoShortIf
    List ::= ForInitopt | ForInit
    List ::= ForUpdateopt | ForUpdate
    List ::= StatementExpressionList
    polyglot.lex.Identifier ::= identifier
    Name ::= identifieropt
    Branch ::= BreakStatement | ContinueStatement
    Return ::= ReturnStatement
    Throw ::= ThrowStatement
    Synchronized ::= SynchronizedStatement
    Try ::= TryStatement
    List ::= Catchesopt | Catches
    Catch ::= CatchClause
    Block ::= Finally
    Assert ::= AssertStatement
    Expr ::= Primary | PrimaryNoNewArray
    Expr ::= ConstPrimary
    Expr ::= ClassInstanceCreationExpression
    List ::= ArgumentListopt | ArgumentList
    NewArray ::= ArrayCreationExpression
    List ::= DimExprs
    Expr ::= DimExpr
    Integer ::= Dimsopt | Dims
    Field ::= FieldAccess 
    Expr ::= ConstFieldAccess
    Call ::= MethodInvocation
    ArrayAccess ::= ArrayAccess
    Expr ::= PostfixExpression
    Expr ::= ConstPostfixExpression
    Unary ::= PostIncrementExpression | PostDecrementExpression
    Expr ::= UnaryExpression | UnaryExpressionNotPlusMinus
    Unary ::= PreIncrementExpression | PreDecrementExpression
    Expr ::= ConstUnaryExpression 
    Cast ::= CastExpression
    Expr ::= MultiplicativeExpression | AdditiveExpression
    Expr ::= ConstMultiplicativeExpression | ConstAdditiveExpression
    Expr ::= ShiftExpression | RelationalExpression | EqualityExpression
    Expr ::= ConstRelationalExpression | ConstEqualityExpression
    Expr ::= AndExpression | ExclusiveOrExpression | InclusiveOrExpression
    Expr ::= ConstAndExpression | ConstExclusiveOrExpression | ConstInclusiveOrExpression
    Expr ::= ConditionalAndExpression | ConditionalOrExpression
    Expr ::= ConditionalExpression | AssignmentExpression
    Expr ::= ConstExpression
    Expr ::= Assignment
    Expr ::= LeftHandSide
    Assign.Operator ::= AssignmentOperator
    Expr ::= Expressionopt | Expression
    Expr ::= ConstantExpression

    Name ::= TypeName
    Name ::= ClassName
    Name ::= PackageName
    Name ::= ExpressionName
    Name ::= AmbiguousName
    Name ::= MethodName
    Name ::= PackageOrTypeName
    Block ::= InstanceInitializer
    List ::= IdentifierList
    TypeNode ::= ResultType
    'Object[]' ::= MethodDeclarator
    Formal ::= LastFormalParameter
    List ::= FormalParameters
    List ::= ExceptionTypeList
    TypeNode ::= ExceptionType
    'Object[]' ::= ConstructorDeclarator
    Name ::= SimpleTypeName
    Stmt ::= ExplicitConstructorInvocationopt
    List ::= Argumentsopt
           | Arguments
    List ::= VariableInitializersopt
    List ::= SwitchBlockStatementGroupsopt
    List ::= SwitchLabelsopt
    For ::= BasicForStatement
    For ::= EnhancedForStatement
          | EnhancedForStatementNoShortIf
    polyglot.lex.BooleanLiteral ::= BooleanLiteral
    TypeNode ::= DataType
    Expr ::= PlaceExpression
    Expr ::= WhereClauseopt
    Expr ::= WhereClause
    TypeNode ::= X10ArrayType
    ClassDecl ::= ValueClassDeclaration
    TypeNode ::= ArrayBaseType
    Object ::= Unsafeopt
    Object ::= Valueopt
    Now ::= NowStatement
          | NowStatementNoShortIf
    Async ::= AsyncStatement
            | AsyncStatementNoShortIf
    Atomic ::= AtomicStatement
             | AtomicStatementNoShortIf
    When ::= WhenStatement
           | WhenStatementNoShortIf
    ForEach ::= ForEachStatement
              | ForEachStatementNoShortIf
    AtEach ::= AtEachStatement
             | AtEachStatementNoShortIf
    Finish ::= FinishStatement
             | FinishStatementNoShortIf
    Next ::= NextStatement
    Await ::= AwaitStatement
    Expr ::= Clock
    List ::= ClockList
           | ClockedClause
           | ClockedClauseopt
    Expr ::= RegionExpression
    List ::= RegionExpressionList
    Expr ::= PlaceExpressionSingleListopt
           | PlaceExpressionSingleList
    Stmt ::= AssignPropertyCall
    Future ::= FutureExpression
    polyglot.lex.Identifier ::= ParamIdopt
    DepParameterExpr ::= DepParametersopt
                       | DepParameters
                       | DepParameterExpr
    List ::= Properties 
    'Object[]' ::=  PropertyList | PropertyListopt
    PropertyDecl ::= Property
    DepParameterExpr ::= ThisClause | ThisClauseopt
    List ::= Annotations
    AnnotationNode ::= Annotation
%End
