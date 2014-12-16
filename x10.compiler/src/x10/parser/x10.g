%options fp=X10Parser,states
%options list
%options la=6
%options variables=nt
%options conflicts
%options softkeywords
%options package=x10.parser
%options template=btParserTemplateF.gi
%options import_terminals="X10Lexer.gi"

%Notice
    /./*
     *  This file is part of the X10 project (http://x10-lang.org).
     *
     *  This file is licensed to You under the Eclipse Public License (EPL);
     *  You may not use this file except in compliance with the License.
     *  You may obtain a copy of the License at
     *      http://www.opensource.org/licenses/eclipse-1.0.php
     *
     *  (C) Copyright IBM Corporation 2006-2014.
     */
    /********************************************************************
     * WARNING!  THIS JAVA FILE IS AUTO-GENERATED FROM $input_file *
     ********************************************************************/
    ./
%End

%Terminals

    IntLiteral             -- IntegerLiteral followed by 'n' or 'N'
    LongLiteral            -- IntegerLiteral optionally followed by 'l' or 'L'
    ByteLiteral            -- IntegerLiteral followed by 'y' or 'Y'
    ShortLiteral           -- IntegerLiteral followed by 's' or 'S'
    UnsignedIntLiteral     -- IntegerLiteral followed by 'u' or 'U' and 'n' or 'N' (in any order)
    UnsignedLongLiteral    -- IntegerLiteral followed by 'u' or 'U' and optionally 'l' or 'L' (in any order)
    UnsignedByteLiteral    -- IntegerLiteral followed by 'u' or 'U' and 'y' or 'Y' (in any order)
    UnsignedShortLiteral   -- IntegerLiteral followed by 'u' or 'U' and 's' or 'S' (in any order)
    FloatingPointLiteral   --
                           -- FloatingPointLiteral ::= Digits . Digits? ExponentPart? FloatingTypeSuffix?
                           --                        | . Digits ExponentPart? FloatingTypeSuffix?
                           --                        | Digits ExponentPart FloatingTypeSuffix?
                           --                        | Digits ExponentPart? FloatingTypeSuffix
                           --
                           -- ExponentPart ::= ('e'|'E') ('+'|'-')? Digits
                           -- FloatingTypeSuffix ::= 'f' |  'F'
                           --
    DoubleLiteral          -- See FloatingPointLiteral except that
                           -- FloatingTypeSuffix ::= 'd' | 'D'
                           --
    CharacterLiteral       -- the usual
    StringLiteral          -- the usual

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
    RIGHT_SHIFT ::= >>
    RIGHT_SHIFT_EQUAL ::= >>= 
    UNSIGNED_RIGHT_SHIFT ::= >>> 
    UNSIGNED_RIGHT_SHIFT_EQUAL ::= >>>= 
    LESS_EQUAL ::= <=
    EQUAL ::= =  
    EQUAL_EQUAL ::= ==  
    GREATER ::= >
    GREATER_EQUAL ::= >=
    ELLIPSIS ::= ...

    RANGE ::= ..
    ARROW ::= '->'
    DARROW ::= =>
    SUBTYPE ::= <:
    SUPERTYPE ::= :>
    STARSTAR ::= **
    NTWIDDLE ::= !~
    LARROW ::= '<-'
    FUNNEL ::= '-<'
    LFUNNEL ::= '>-'
    DIAMOND ::= <>
    BOWTIE ::= ><
    RANGE_EQUAL ::= ..=
    ARROW_EQUAL ::= '->='
    STARSTAR_EQUAL ::= **=
    TWIDDLE_EQUAL ::= ~=
    LARROW_EQUAL ::= '<-='
    FUNNEL_EQUAL ::= '-<='
    LFUNNEL_EQUAL ::= '>-='
    DIAMOND_EQUAL ::= <>=
    BOWTIE_EQUAL ::= ><=
%End

%Define
    --
    -- Definition of macros used in the parser template
    --
    $ast_class /.polyglot.ast.Node./
%End

%Identifier
    IDENTIFIER
%End

%Start
    CompilationUnit
%End

%SoftKeywords
--    abstract
--    as
--    assert
--    async
--    at
--    ateach
--    atomic
--    break
--    case
--    catch
--    class
--    clocked
--    continue
--    def
--    default
--    do
--    else
--    extends
--    false
--    final
--    finally
--    finish
--    for
--    goto
--    haszero
--    here
--    if
--    implements
--    import
--    in
--    instanceof
--    interface
--    native
--    new
--    next
--    null
--    offer
--    offers
--    operator
--    package
--    private
--    property
--    protected
--    public
--    resume
--    return
--    self
--    static
--    struct
--    super
--    switch
--    this
--    throw
--    transient
--    true
--    try
--    type
--    val
--    var
--    when
--    while
%End

%Headers
    /.
        public x10.parser.X10SemanticRules r;
    ./
%End

%Recover
    ErrorId
%End

%Rules

-- Begin: Error recovery rules
    TypeName ::= TypeName . ErrorId
            /.$BeginJava
			r.rule_TypeName0(TypeName);
            $EndJava./

    PackageName ::= PackageName . ErrorId
            /.$BeginJava
			r.rule_PackageName0(PackageName);
            $EndJava./

    ExpressionName ::= FullyQualifiedName . ErrorId
            /.$BeginJava
			r.rule_ExpressionName0(FullyQualifiedName);
            $EndJava./

    MethodName ::= FullyQualifiedName . ErrorId
            /.$BeginJava
			r.rule_MethodName0(FullyQualifiedName);
            $EndJava./

    PackageOrTypeName ::= PackageOrTypeName . ErrorId
            /.$BeginJava
			r.rule_PackageOrTypeName0(PackageOrTypeName);
            $EndJava./

    FullyQualifiedName ::= FullyQualifiedName . ErrorId
            /.$BeginJava
			r.rule_FullyQualifiedName0(FullyQualifiedName);
            $EndJava./

    FieldAccess ::= ErrorPrimaryPrefix
        /.$BeginJava
			r.rule_FieldAccess0(ErrorPrimaryPrefix);
        $EndJava./
                  | ErrorSuperPrefix
        /.$BeginJava
			r.rule_FieldAccess1(ErrorSuperPrefix);
        $EndJava./
                  | ErrorClassNameSuperPrefix
        /.$BeginJava
			r.rule_FieldAccess2(ErrorClassNameSuperPrefix);
        $EndJava./

    MethodInvocation ::= ErrorPrimaryPrefix ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation0(ErrorPrimaryPrefix,ArgumentListopt);
        $EndJava./
                       | ErrorSuperPrefix ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation1(ErrorSuperPrefix,ArgumentListopt);
        $EndJava./
                       | ErrorClassNameSuperPrefix ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation2(ErrorClassNameSuperPrefix,ArgumentListopt);
        $EndJava./

    ErrorPrimaryPrefix ::= Primary . ErrorId
        /.$BeginJava
			r.rule_ErrorPrimaryPrefix0(Primary);
        $EndJava./
    ErrorSuperPrefix ::= super . ErrorId
        /.$BeginJava
			r.rule_ErrorSuperPrefix0();
        $EndJava./
    ErrorClassNameSuperPrefix ::= ClassName . super . ErrorId
        /.$BeginJava
			r.rule_ErrorClassNameSuperPrefix0(ClassName);
        $EndJava./

-- End: Error recovery rules



    Modifiersopt ::= %Empty
        /.$BeginJava
			r.rule_Modifiersopt0();
        $EndJava./
                   | Modifiersopt Modifier
        /.$BeginJava
			r.rule_Modifiersopt1(Modifiersopt,Modifier);
        $EndJava./

    Modifier ::= abstract
        /.$BeginJava
			r.rule_Modifier0();
        $EndJava./
                   | Annotation
        /.$BeginJava
			r.rule_Modifier1(Annotation);
        $EndJava./
                   | atomic
        /.$BeginJava
			r.rule_Modifier2();
        $EndJava./
                   | final
        /.$BeginJava
			r.rule_Modifier3();
        $EndJava./
                   | native
        /.$BeginJava
			r.rule_Modifier4();
        $EndJava./
                   | private
        /.$BeginJava
			r.rule_Modifier5();
        $EndJava./
                   | protected
        /.$BeginJava
			r.rule_Modifier6();
        $EndJava./
                   | public
        /.$BeginJava
			r.rule_Modifier7();
        $EndJava./
                   | static
        /.$BeginJava
			r.rule_Modifier8();
        $EndJava./
                   | transient
        /.$BeginJava
			r.rule_Modifier9();
        $EndJava./
                   | clocked
        /.$BeginJava
			r.rule_Modifier10();
        $EndJava./

    MethodModifiersopt ::= Modifiersopt
                         | MethodModifiersopt property$property
        /.$BeginJava
			r.rule_MethodModifiersopt1(MethodModifiersopt);
        $EndJava./
                         | MethodModifiersopt Modifier
        /.$BeginJava
			r.rule_MethodModifiersopt2(MethodModifiersopt,Modifier);
        $EndJava./

    TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt WhereClauseopt = Type ;
        /.$BeginJava
			r.rule_TypeDefDeclaration0(Modifiersopt,Identifier,TypeParametersopt,WhereClauseopt,Type);
        $EndJava./
                         | Modifiersopt type Identifier TypeParametersopt ( FormalParameterList ) WhereClauseopt = Type ;
        /.$BeginJava
			r.rule_TypeDefDeclaration1(Modifiersopt,Identifier,TypeParametersopt,FormalParameterList,WhereClauseopt,Type);
        $EndJava./
        
    Properties ::= ( PropertyList )
      /.$BeginJava
			r.rule_Properties0(PropertyList);
     $EndJava ./

    PropertyList ::= Property
        /.$BeginJava
			r.rule_PropertyList0(Property);
        $EndJava./
                          | PropertyList , Property
        /.$BeginJava
			r.rule_PropertyList1(PropertyList,Property);
        $EndJava./
    
    
    Property ::= Annotationsopt Identifier ResultType
        /.$BeginJava
			r.rule_Property0(Annotationsopt,Identifier,ResultType);
        $EndJava./

    MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration0(MethodModifiersopt,Identifier,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./
                        | BinaryOperatorDeclaration
                        | PrefixOperatorDeclaration
                        | ApplyOperatorDeclaration
                        | SetOperatorDeclaration
                        | ConversionOperatorDeclaration

    BinaryOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration1(MethodModifiersopt,TypeParametersopt,fp1,BinOp,fp2,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./
                                | MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration3(MethodModifiersopt,TypeParametersopt,BinOp,fp2,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./
                                | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration4(MethodModifiersopt,TypeParametersopt,fp1,BinOp,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./

    PrefixOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration2(MethodModifiersopt,TypeParametersopt,PrefixOp,fp2,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./
                                | MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration5(MethodModifiersopt,TypeParametersopt,PrefixOp,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./

    ApplyOperatorDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration6(MethodModifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./

    SetOperatorDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration7(MethodModifiersopt,TypeParametersopt,FormalParameters,fp2,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./

    ConversionOperatorDeclaration ::= ExplicitConversionOperatorDeclaration
                                    | ImplicitConversionOperatorDeclaration

    --  TODO: "Type WhereClauseopt" is ambiguous!
    ExplicitConversionOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt OBSOLETE_Offersopt Throwsopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration8(MethodModifiersopt,TypeParametersopt,fp1,Type,WhereClauseopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./
                                            | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as '?' WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration9(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./

    ImplicitConversionOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration10(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,MethodBody);
        $EndJava./

    PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_PropertyMethodDeclaration0(MethodModifiersopt,Identifier,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,MethodBody);
                                                  
        $EndJava./
                                | MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
        /.$BeginJava
			r.rule_PropertyMethodDeclaration1(MethodModifiersopt,Identifier,WhereClauseopt,HasResultTypeopt,MethodBody);
                                                 
        $EndJava./

    ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
        /.$BeginJava
			r.rule_ExplicitConstructorInvocation0(TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                                    | super TypeArgumentsopt ( ArgumentListopt ) ;
        /.$BeginJava
			r.rule_ExplicitConstructorInvocation1(TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                                    | Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
        /.$BeginJava
			r.rule_ExplicitConstructorInvocation2(Primary,TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                                    | Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
        /.$BeginJava
			r.rule_ExplicitConstructorInvocation3(Primary,TypeArgumentsopt,ArgumentListopt);
        $EndJava./

    InterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
        /.$BeginJava
			r.rule_InterfaceDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,ExtendsInterfacesopt,InterfaceBody);
        $EndJava./

    ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
			r.rule_ClassInstanceCreationExpression0(TypeName,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
        $EndJava./
                                      | Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
			r.rule_ClassInstanceCreationExpression2(Primary,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
        $EndJava./
                                      | FullyQualifiedName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
			r.rule_ClassInstanceCreationExpression3(FullyQualifiedName,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
        $EndJava./
                       
    AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
        /.$BeginJava
			r.rule_AssignPropertyCall0(TypeArgumentsopt,ArgumentListopt);
        $EndJava./

    -------------------------------------- Section:::Types
    Type ::= FunctionType
           |  ConstrainedType
           |  Void

    FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt OBSOLETE_Offersopt => Type
        /.$BeginJava
			r.rule_FunctionType0(TypeParametersopt,FormalParameterListopt,WhereClauseopt,OBSOLETE_Offersopt,Type);
        $EndJava./

    ClassType ::= NamedType

    AnnotatedType ::= Type Annotations
        /.$BeginJava
			r.rule_AnnotatedType0(Type,Annotations);
        $EndJava./

    ConstrainedType ::=  NamedType
           | AnnotatedType

    Void ::= void
        /.$BeginJava
			r.rule_Void0();
        $EndJava./


    SimpleNamedType ::= TypeName
        /.$BeginJava
			r.rule_SimpleNamedType0(TypeName);
        $EndJava./
                      | Primary . Identifier
        /.$BeginJava
			r.rule_SimpleNamedType1(Primary,Identifier);
        $EndJava./
                      | ParameterizedNamedType . Identifier
        /.$BeginJava
			r.rule_SimpleNamedType2(ParameterizedNamedType,Identifier);
        $EndJava./
                      | DepNamedType . Identifier
        /.$BeginJava
			r.rule_SimpleNamedType3(DepNamedType,Identifier);
        $EndJava./

    ParameterizedNamedType ::= SimpleNamedType Arguments
        /.$BeginJava
			r.rule_ParameterizedNamedType0(SimpleNamedType,Arguments);
        $EndJava./
                | SimpleNamedType TypeArguments
        /.$BeginJava
			r.rule_ParameterizedNamedType1(SimpleNamedType,TypeArguments);
        $EndJava./
                | SimpleNamedType TypeArguments Arguments
        /.$BeginJava
			r.rule_ParameterizedNamedType2(SimpleNamedType,TypeArguments,Arguments);
        $EndJava./

    DepNamedType ::= SimpleNamedType DepParameters
        /.$BeginJava
			r.rule_DepNamedType0(SimpleNamedType,DepParameters);
        $EndJava./
                | ParameterizedNamedType DepParameters
        /.$BeginJava
			r.rule_DepNamedType1(ParameterizedNamedType,DepParameters);
        $EndJava./

    NamedTypeNoConstraints ::= SimpleNamedType
                             | ParameterizedNamedType

    NamedType ::= NamedTypeNoConstraints
                | DepNamedType

    DepParameters ::= { FUTURE_ExistentialListopt ConstraintConjunctionopt }
        /.$BeginJava
			r.rule_DepParameters0(FUTURE_ExistentialListopt,ConstraintConjunctionopt);
        $EndJava./


    TypeParamsWithVariance ::= '[' TypeParamWithVarianceList ']'
        /.$BeginJava
			r.rule_TypeParamsWithVariance0(TypeParamWithVarianceList);
        $EndJava./
        
    TypeParameters ::= '[' TypeParameterList ']'
        /.$BeginJava
			r.rule_TypeParameters0(TypeParameterList);
        $EndJava./

    FormalParameters ::= ( FormalParameterListopt )
        /.$BeginJava
			r.rule_FormalParameters0(FormalParameterListopt);
        $EndJava./

    ConstraintConjunction ::= Expression
        /.$BeginJava
			r.rule_ConstraintConjunction0(Expression);
        $EndJava./
                  | ConstraintConjunction , Expression
         /.$BeginJava
			r.rule_ConstraintConjunction1(ConstraintConjunction,Expression);
        $EndJava./

    HasZeroConstraint ::= Type$t1 haszero
         /.$BeginJava
			r.rule_HasZeroConstraint0(t1);
        $EndJava./

    IsRefConstraint ::= Type$t1 isref
         /.$BeginJava
			r.rule_IsRefConstraint0(t1);
        $EndJava./

    SubtypeConstraint ::= Type$t1 <: Type$t2
         /.$BeginJava
			r.rule_SubtypeConstraint0(t1,t2);
        $EndJava./
                        | Type$t1 :> Type$t2
         /.$BeginJava
			r.rule_SubtypeConstraint1(t1,t2);
        $EndJava./
                        
    WhereClause ::= DepParameters
            /.$BeginJava
			r.rule_WhereClause0(DepParameters);
          $EndJava./

    ConstraintConjunctionopt ::= %Empty
          /.$BeginJava
			r.rule_ConstraintConjunctionopt0();
          $EndJava./
          | ConstraintConjunction
          /.$BeginJava
			r.rule_ConstraintConjunctionopt1(ConstraintConjunction);
        $EndJava./

    FUTURE_ExistentialListopt ::= %Empty
        /.$BeginJava
			r.rule_FUTURE_ExistentialListopt0();
        $EndJava./
        -- TODO: re-enable this when adding support for named self
--                                | FUTURE_ExistentialList ;
--        /.$BeginJava
--			r.rule_FUTURE_ExistentialListopt1(FUTURE_ExistentialList);
--        $EndJava./

    FUTURE_ExistentialList ::= FormalParameter
        /.$BeginJava
			r.rule_FUTURE_ExistentialList0(FormalParameter);
        $EndJava./
                             | FUTURE_ExistentialList ; FormalParameter
        /.$BeginJava
			r.rule_FUTURE_ExistentialList1(FUTURE_ExistentialList,FormalParameter);
        $EndJava./


    ------------------------------------- Section ::: Classes
    ClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
        /.$BeginJava
			r.rule_NormalClassDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Superopt,Interfacesopt,ClassBody);
        $EndJava./


    StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
        /.$BeginJava
			r.rule_StructDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Interfacesopt,ClassBody);
        $EndJava./

    ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt OBSOLETE_Offersopt Throwsopt HasResultTypeopt ConstructorBody
        /.$BeginJava
			r.rule_ConstructorDeclaration0(Modifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,Throwsopt,ConstructorBody);
                                                               
        $EndJava./
       
    Super ::= extends ClassType
        /.$BeginJava
			r.rule_Super0(ClassType);
        $EndJava./
    
    VarKeyword ::= val 
        /.$BeginJava
			r.rule_VarKeyword0();
        $EndJava./
                   | var 
        /.$BeginJava
			r.rule_VarKeyword1();
        $EndJava./
    
    FieldDeclaration ::= Modifiersopt VarKeyword FieldDeclarators ;
        /.$BeginJava
			r.rule_FieldDeclaration0(Modifiersopt,VarKeyword,FieldDeclarators);
        $EndJava./
                       | Modifiersopt FieldDeclarators ;
        /.$BeginJava
			r.rule_FieldDeclaration1(Modifiersopt,FieldDeclarators);
        $EndJava./
        
        
    --------------------------------------- Section :: Statement

    Statement ::= AnnotationStatement
                | ExpressionStatement

    AnnotationStatement ::= Annotationsopt NonExpressionStatement
        /.$BeginJava
			r.rule_AnnotationStatement0(Annotationsopt,NonExpressionStatement);
        $EndJava./

    NonExpressionStatement ::= Block
                | EmptyStatement
                | AssertStatement
                | SwitchStatement
                | DoStatement
                | BreakStatement
                | ContinueStatement
                | ReturnStatement
                | ThrowStatement
                | TryStatement
                | LabeledStatement
                | IfThenStatement
                | IfThenElseStatement
                | WhileStatement
                | ForStatement
                | AsyncStatement
                | AtStatement
                | AtomicStatement
                | WhenStatement
                | AtEachStatement
                | FinishStatement
                | AssignPropertyCall
                | OBSOLETE_OfferStatement
    
    OBSOLETE_OfferStatement ::= offer Expression ;
         /.$BeginJava
			r.rule_OBSOLETE_OfferStatement0(Expression);
        $EndJava./
    
    IfThenStatement ::= if ( Expression ) Statement
        /.$BeginJava
			r.rule_IfThenStatement0(Expression,Statement);
        $EndJava./
    
    IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
        /.$BeginJava
			r.rule_IfThenElseStatement0(Expression,s1,s2);
        $EndJava./
    
    EmptyStatement ::= ;
        /.$BeginJava
			r.rule_EmptyStatement0();
        $EndJava./
    
    LabeledStatement ::= Identifier : LoopStatement
        /.$BeginJava
			r.rule_LabeledStatement0(Identifier,LoopStatement);
        $EndJava./
        
    LoopStatement ::= ForStatement
                    | WhileStatement
                    | DoStatement
                    | AtEachStatement
    
    ExpressionStatement ::= StatementExpression ;
        /.$BeginJava
			r.rule_ExpressionStatement0(StatementExpression);
        $EndJava./
    
    StatementExpression ::= Assignment
                          | PreIncrementExpression
                          | PreDecrementExpression
                          | PostIncrementExpression
                          | PostDecrementExpression
                          | MethodInvocation
                          | ClassInstanceCreationExpression
                          | OverloadableExpression

    OverloadableExpression ::= OverloadableUnaryExpressionPlusMinus
                             | OverloadableUnaryExpression
                             | OverloadableRangeExpression
                             | OverloadableMultiplicativeExpression
                             | OverloadableAdditiveExpression
                             | OverloadableShiftExpression
                             | OverloadableRelationalExpression
                             | OverloadableEqualityExpression
                             | OverloadableAndExpression
                             | OverloadableExclusiveOrExpression
                             | OverloadableInclusiveOrExpression
                             | OverloadableConditionalAndExpression
                             | OverloadableConditionalOrExpression
    
    AssertStatement ::= assert Expression ;
        /.$BeginJava
			r.rule_AssertStatement0(Expression);
        $EndJava./
                      | assert Expression$expr1 : Expression$expr2 ;
        /.$BeginJava
			r.rule_AssertStatement1(expr1,expr2);
        $EndJava./
    
    SwitchStatement ::= switch ( Expression ) SwitchBlock
        /.$BeginJava
			r.rule_SwitchStatement0(Expression,SwitchBlock);
        $EndJava./
    
    SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
        /.$BeginJava
			r.rule_SwitchBlock0(SwitchBlockStatementGroupsopt,SwitchLabelsopt);
        $EndJava./
    
    SwitchBlockStatementGroups ::= SwitchBlockStatementGroup
                                 | SwitchBlockStatementGroups SwitchBlockStatementGroup
        /.$BeginJava
			r.rule_SwitchBlockStatementGroups1(SwitchBlockStatementGroups,SwitchBlockStatementGroup);
        $EndJava./
    
    SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
        /.$BeginJava
			r.rule_SwitchBlockStatementGroup0(SwitchLabels,BlockStatements);
        $EndJava./
    
    SwitchLabels ::= SwitchLabel
        /.$BeginJava
			r.rule_SwitchLabels0(SwitchLabel);
        $EndJava./
                   | SwitchLabels SwitchLabel
        /.$BeginJava
			r.rule_SwitchLabels1(SwitchLabels,SwitchLabel);
        $EndJava./
    
    SwitchLabel ::= case ConstantExpression :
        /.$BeginJava
			r.rule_SwitchLabel0(ConstantExpression);
        $EndJava./
                  | default :
        /.$BeginJava
			r.rule_SwitchLabel1();
        $EndJava./

    WhileStatement ::= while ( Expression ) Statement
        /.$BeginJava
			r.rule_WhileStatement0(Expression,Statement);
        $EndJava./
    
    DoStatement ::= do Statement while ( Expression ) ;
        /.$BeginJava
			r.rule_DoStatement0(Statement,Expression);
        $EndJava./
    
    ForStatement ::= BasicForStatement
                   | EnhancedForStatement
    
    BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
        /.$BeginJava
			r.rule_BasicForStatement0(ForInitopt,Expressionopt,ForUpdateopt,Statement);
        $EndJava./
    
    ForInit ::= StatementExpressionList
              | LocalVariableDeclaration
        /.$BeginJava
			r.rule_ForInit1(LocalVariableDeclaration);
        $EndJava./
    
    ForUpdate ::= StatementExpressionList
    
    StatementExpressionList ::= StatementExpression
        /.$BeginJava
			r.rule_StatementExpressionList0(StatementExpression);
        $EndJava./
                              | StatementExpressionList , StatementExpression
        /.$BeginJava
			r.rule_StatementExpressionList1(StatementExpressionList,StatementExpression);
        $EndJava./
    
    BreakStatement ::= break Identifieropt ;
        /.$BeginJava
			r.rule_BreakStatement0(Identifieropt);
        $EndJava./
    
    ContinueStatement ::= continue Identifieropt ;
        /.$BeginJava
			r.rule_ContinueStatement0(Identifieropt);
        $EndJava./
    
    ReturnStatement ::= return Expressionopt ;
        /.$BeginJava
			r.rule_ReturnStatement0(Expressionopt);
        $EndJava./
    
    ThrowStatement ::= throw Expression ;
        /.$BeginJava
			r.rule_ThrowStatement0(Expression);
        $EndJava./
    
    TryStatement ::= try Block Catches
        /.$BeginJava
			r.rule_TryStatement0(Block,Catches);
        $EndJava./
                   | try Block Catchesopt Finally
        /.$BeginJava
			r.rule_TryStatement1(Block,Catchesopt,Finally);
        $EndJava./
    
    Catches ::= CatchClause
        /.$BeginJava
			r.rule_Catches0(CatchClause);
        $EndJava./
              | Catches CatchClause
        /.$BeginJava
			r.rule_Catches1(Catches,CatchClause);
        $EndJava./
    
    CatchClause ::= catch ( FormalParameter ) Block
        /.$BeginJava
			r.rule_CatchClause0(FormalParameter,Block);
        $EndJava./
    
    Finally ::= finally Block
        /.$BeginJava
			r.rule_Finally0(Block);
        $EndJava./

    ClockedClause ::= clocked Arguments
        /.$BeginJava
			r.rule_ClockedClause0(Arguments);
        $EndJava./
        

    AsyncStatement ::= async ClockedClauseopt Statement
        /.$BeginJava
			r.rule_AsyncStatement0(ClockedClauseopt,Statement);
        $EndJava./
         | clocked async Statement
        /.$BeginJava
			r.rule_AsyncStatement1(Statement);
        $EndJava./


    AtStatement ::= at ( Expression ) Statement
        /.$BeginJava
			r.rule_AtStatement0(Expression,Statement);
        $EndJava./
-- Begin XTENLANG-2660
--                  | at ( Expression ; * ) Statement
--        /.$BeginJava
--			r.rule_AtStatement0(Expression,Statement);
--        $EndJava./
--                  | at ( Expression ; AtCaptureDeclaratorsopt ) Statement
--        /.$BeginJava
--			r.rule_AtStatement1(Expression,AtCaptureDeclaratorsopt,Statement);
--        $EndJava./
--                  | athome ( HomeVariableList ) Statement
--        /.$BeginJava
--			r.rule_AtStatement2(HomeVariableList,Statement);
--        $EndJava./
--                  | athome ( HomeVariableList ; * ) Statement
--        /.$BeginJava
--			r.rule_AtStatement2(HomeVariableList,Statement);
--        $EndJava./
--                  | athome ( HomeVariableList ; AtCaptureDeclaratorsopt ) Statement
--        /.$BeginJava
--			r.rule_AtStatement3(HomeVariableList,AtCaptureDeclaratorsopt,Statement);
--        $EndJava./
--                  | athome Statement
--        /.$BeginJava
--			r.rule_AtStatement4(Statement);
--        $EndJava./
--                  | athome ( * ) Statement
--        /.$BeginJava
--			r.rule_AtStatement4(Statement);
--        $EndJava./
--                  | athome ( * ; * ) Statement
--        /.$BeginJava
--			r.rule_AtStatement4(Statement);
--        $EndJava./
--                  | athome ( * ; AtCaptureDeclaratorsopt ) Statement
--        /.$BeginJava
--			r.rule_AtStatement5(AtCaptureDeclaratorsopt,Statement);
--        $EndJava./
-- End XTENLANG-2660

    AtomicStatement ::= atomic Statement
        /.$BeginJava
			r.rule_AtomicStatement0(Statement);
        $EndJava./


    WhenStatement  ::= when ( Expression ) Statement
        /.$BeginJava
			r.rule_WhenStatement0(Expression,Statement);
        $EndJava./
--                     | WhenStatement or$or ( Expression ) Statement
--        /.$BeginJava
--                  WhenStatement.addBranch(pos(getRhsFirstTokenIndex($or), getRightSpan()), Expression, Statement);
--                  setResult(WhenStatement);
--          $EndJava
--        ./

    AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
        /.$BeginJava
			r.rule_AtEachStatement0(LoopIndex,Expression,ClockedClauseopt,Statement);
        $EndJava./
     | ateach ( Expression ) Statement
        /.$BeginJava
			r.rule_AtEachStatement1(Expression,Statement);
        $EndJava./
    EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
        /.$BeginJava
			r.rule_EnhancedForStatement0(LoopIndex,Expression,Statement);
        $EndJava./
       | for ( Expression ) Statement
        /.$BeginJava
			r.rule_EnhancedForStatement1(Expression,Statement);
        $EndJava./
        

    FinishStatement ::= finish Statement
        /.$BeginJava
			r.rule_FinishStatement0(Statement);
        $EndJava./
                | clocked finish Statement
        /.$BeginJava
			r.rule_FinishStatement1(Statement);
        $EndJava./

    CastExpression ::= Primary
                     | ExpressionName
        /.$BeginJava
			r.rule_CastExpression1(ExpressionName);
        $EndJava./
                     | CastExpression as Type
        /.$BeginJava
			r.rule_CastExpression2(CastExpression,Type);
        $EndJava./
    
     --------------------------------------- Section :: Expression
     TypeParamWithVarianceList ::= TypeParameter
        /.$BeginJava
			r.rule_TypeParamWithVarianceList0(TypeParameter);
        $EndJava./
                      | OBSOLETE_TypeParamWithVariance
        /.$BeginJava
			r.rule_TypeParamWithVarianceList1(OBSOLETE_TypeParamWithVariance);
        $EndJava./
                      | TypeParamWithVarianceList , TypeParameter
        /.$BeginJava
			r.rule_TypeParamWithVarianceList2(TypeParamWithVarianceList,TypeParameter);
        $EndJava./
                      | TypeParamWithVarianceList , OBSOLETE_TypeParamWithVariance
        /.$BeginJava
			r.rule_TypeParamWithVarianceList3(TypeParamWithVarianceList,OBSOLETE_TypeParamWithVariance);
        $EndJava./
        
     TypeParameterList ::= TypeParameter
        /.$BeginJava
			r.rule_TypeParameterList0(TypeParameter);
        $EndJava./
                      | TypeParameterList , TypeParameter
        /.$BeginJava
			r.rule_TypeParameterList1(TypeParameterList,TypeParameter);
        $EndJava./
        
    OBSOLETE_TypeParamWithVariance ::= + TypeParameter
        /.$BeginJava
			r.rule_OBSOLETE_TypeParamWithVariance0(TypeParameter);
        $EndJava./
                                     | - TypeParameter
        /.$BeginJava
			r.rule_OBSOLETE_TypeParamWithVariance1(TypeParameter);
        $EndJava./
        
    TypeParameter ::= Identifier
        /.$BeginJava
			r.rule_TypeParameter0(Identifier);
        $EndJava./

    ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt => ClosureBody
        /.$BeginJava
			r.rule_ClosureExpression0(FormalParameters,WhereClauseopt,HasResultTypeopt,OBSOLETE_Offersopt,ClosureBody);
        $EndJava./

    LastExpression ::= Expression
        /.$BeginJava
			r.rule_LastExpression0(Expression);
        $EndJava./

    ClosureBody ::= Expression
        /.$BeginJava
			r.rule_ClosureBody0(Expression);
        $EndJava./
                  | Annotationsopt { BlockStatementsopt LastExpression }
        /.$BeginJava
			r.rule_ClosureBody1(Annotationsopt,BlockStatementsopt,LastExpression);
        $EndJava./
                  | Annotationsopt Block
        /.$BeginJava
			r.rule_ClosureBody2(Annotationsopt,Block);
        $EndJava./
                  
                  
    AtExpression ::= Annotationsopt at ( Expression ) ClosureBody
        /.$BeginJava
			r.rule_AtExpression0(Annotationsopt,Expression,ClosureBody);
        $EndJava./
-- Begin XTENLANG-2660
--                   | at ( Expression ; * ) ClosureBody
--        /.$BeginJava
--			r.rule_AtExpression0(Expression,ClosureBody);
--        $EndJava./
--                   | at ( Expression ; AtCaptureDeclaratorsopt ) ClosureBody
--        /.$BeginJava
--			r.rule_AtExpression1(Expression,AtCaptureDeclaratorsopt,ClosureBody);
--        $EndJava./
--                   | athome ( HomeVariableList ) ClosureBody
--        /.$BeginJava
--			r.rule_AtExpression2(HomeVariableList,ClosureBody);
--        $EndJava./
--                   | athome ( HomeVariableList ; * ) ClosureBody
--        /.$BeginJava
--			r.rule_AtExpression2(HomeVariableList,ClosureBody);
--        $EndJava./
--                   | athome ( HomeVariableList ; AtCaptureDeclaratorsopt ) ClosureBody
--        /.$BeginJava
--			r.rule_AtExpression3(HomeVariableList,AtCaptureDeclaratorsopt,ClosureBody);
--        $EndJava./
--                   | athome ClosureBody
--        /.$BeginJava
--			r.rule_AtExpression4(ClosureBody);
--        $EndJava./
--                   | athome ( * ) ClosureBody
--        /.$BeginJava
--			r.rule_AtExpression4(ClosureBody);
--        $EndJava./
--                   | athome ( * ; * ) ClosureBody
--        /.$BeginJava
--			r.rule_AtExpression4(ClosureBody);
--        $EndJava./
--                   | athome ( * ; AtCaptureDeclaratorsopt ) ClosureBody
--        /.$BeginJava
--			r.rule_AtExpression5(AtCaptureDeclaratorsopt,ClosureBody);
--        $EndJava./
-- End XTENLANG-2660

    OBSOLETE_FinishExpression ::= finish ( Expression ) Block
        /.$BeginJava
			r.rule_OBSOLETE_FinishExpression0(Expression,Block);
        $EndJava./
        
    ---------------------------------------- All the opts...

    WhereClauseopt ::= %Empty
        /.$NullAction./
                     | WhereClause

    ClockedClauseopt ::= %Empty
        /.$BeginJava
			r.rule_ClockedClauseopt0();
        $EndJava./
                       | ClockedClause


    ------------------------------------------------------------
    --- All the Java-derived rules


    TypeName ::= Identifier
        /.$BeginJava
			r.rule_TypeName1(Identifier);
        $EndJava./
               | TypeName . Identifier
        /.$BeginJava
			r.rule_TypeName2(TypeName,Identifier);
        $EndJava./

    ClassName ::= TypeName

    TypeArguments ::= '[' TypeArgumentList ']'
        /.$BeginJava
			r.rule_TypeArguments0(TypeArgumentList);
        $EndJava./

    
    TypeArgumentList ::= Type
        /.$BeginJava
			r.rule_TypeArgumentList0(Type);
        $EndJava./
                       | TypeArgumentList , Type
        /.$BeginJava
			r.rule_TypeArgumentList1(TypeArgumentList,Type);
        $EndJava./
        
    

    -- Chapter 6

    PackageName ::= Identifier
        /.$BeginJava
			r.rule_PackageName1(Identifier);
        $EndJava./
                  | PackageName . Identifier
        /.$BeginJava
			r.rule_PackageName2(PackageName,Identifier);
        $EndJava./

    --
    -- See Chapter 4
    --
    -- TypeName ::= Identifier
    --           | PackageOrTypeName . Identifier
    --
    ExpressionName ::=? Identifier
        /.$BeginJava
			r.rule_ExpressionName1(Identifier);
        $EndJava./
                     | FullyQualifiedName . Identifier
        /.$BeginJava
			r.rule_ExpressionName2(FullyQualifiedName,Identifier);
        $EndJava./

    MethodName ::=? Identifier
        /.$BeginJava
			r.rule_MethodName1(Identifier);
        $EndJava./
                 | FullyQualifiedName . Identifier
        /.$BeginJava
			r.rule_MethodName2(FullyQualifiedName,Identifier);
        $EndJava./

    PackageOrTypeName ::= Identifier
        /.$BeginJava
			r.rule_PackageOrTypeName1(Identifier);
        $EndJava./
                        | PackageOrTypeName . Identifier
        /.$BeginJava
			r.rule_PackageOrTypeName2(PackageOrTypeName,Identifier);
        $EndJava./

    FullyQualifiedName ::=? Identifier
        /.$BeginJava
			r.rule_FullyQualifiedName1(Identifier);
        $EndJava./
                    | FullyQualifiedName . Identifier
        /.$BeginJava
			r.rule_FullyQualifiedName2(FullyQualifiedName,Identifier);
        $EndJava./

    -- Chapter 7

    CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
        /.$BeginJava
			r.rule_CompilationUnit0(PackageDeclarationopt,TypeDeclarationsopt);
        $EndJava./
                      | PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
        /.$BeginJava
			r.rule_CompilationUnit1(PackageDeclarationopt,ImportDeclarations,TypeDeclarationsopt);
        $EndJava./
                      | ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt  -- Extend grammar to accept this illegal construct so that we can fail gracefully
        /.$BeginJava
			r.rule_CompilationUnit2(ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
        $EndJava./
                      | PackageDeclaration ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt  -- Extend grammar to accept this illegal construct so that we can fail gracefully
        /.$BeginJava
			r.rule_CompilationUnit3(PackageDeclaration,ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
        $EndJava./

    ImportDeclarations ::= ImportDeclaration
        /.$BeginJava
			r.rule_ImportDeclarations0(ImportDeclaration);
        $EndJava./
                         | ImportDeclarations ImportDeclaration
        /.$BeginJava
			r.rule_ImportDeclarations1(ImportDeclarations,ImportDeclaration);
        $EndJava./

    TypeDeclarations ::= TypeDeclaration
        /.$BeginJava
			r.rule_TypeDeclarations0(TypeDeclaration);
        $EndJava./
                       | TypeDeclarations TypeDeclaration
        /.$BeginJava
			r.rule_TypeDeclarations1(TypeDeclarations,TypeDeclaration);
        $EndJava./

    PackageDeclaration ::= Annotationsopt package PackageName ;
        /.$BeginJava
			r.rule_PackageDeclaration0(Annotationsopt,PackageName);
        $EndJava./
    

    ImportDeclaration ::= SingleTypeImportDeclaration
                        | TypeImportOnDemandDeclaration
--                        | SingleStaticImportDeclaration
--                        | StaticImportOnDemandDeclaration

    SingleTypeImportDeclaration ::= import TypeName ;
        /.$BeginJava
			r.rule_SingleTypeImportDeclaration0(TypeName);
        $EndJava./

    TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
        /.$BeginJava
			r.rule_TypeImportOnDemandDeclaration0(PackageOrTypeName);
        $EndJava./
    
--    SingleStaticImportDeclaration ::= import static TypeName . Identifier ;
--        /.$BadAction./

--    StaticImportOnDemandDeclaration ::= import static TypeName . * ;
--        /.$BadAction./

    TypeDeclaration ::= ClassDeclaration
                      | StructDeclaration
                      | InterfaceDeclaration
                      | TypeDefDeclaration
                      | ;
        /.$BeginJava
			r.rule_TypeDeclaration3();
        $EndJava./

    --
    -- See Chapter 4
    --
    Interfaces ::= implements InterfaceTypeList
        /.$BeginJava
			r.rule_Interfaces0(InterfaceTypeList);
        $EndJava./

    InterfaceTypeList ::= Type
        /.$BeginJava
			r.rule_InterfaceTypeList0(Type);
        $EndJava./
                        | InterfaceTypeList , Type
        /.$BeginJava
			r.rule_InterfaceTypeList1(InterfaceTypeList,Type);
        $EndJava./

    --
    -- See Chapter 4
    --
    ClassBody ::= { ClassMemberDeclarationsopt }
        /.$BeginJava
			r.rule_ClassBody0(ClassMemberDeclarationsopt);
        $EndJava./

    ClassMemberDeclarations ::= ClassMemberDeclaration
        /.$BeginJava
			r.rule_ClassMemberDeclarations0(ClassMemberDeclaration);
        $EndJava./
                            | ClassMemberDeclarations ClassMemberDeclaration
        /.$BeginJava
			r.rule_ClassMemberDeclarations1(ClassMemberDeclarations,ClassMemberDeclaration);
        $EndJava./

    ClassMemberDeclaration ::= InterfaceMemberDeclaration
                             | ConstructorDeclaration
        /.$BeginJava
			r.rule_ClassMemberDeclaration1(ConstructorDeclaration);
        $EndJava./
--                             | InstanceInitializer
--        /.$BeginJava
--                    List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
--                    l.add(InstanceInitializer);
--                    setResult(l);
--          $EndJava
--        ./
--                             | StaticInitializer
--        /.$BeginJava
--                    List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
--                    l.add(StaticInitializer);
--                    setResult(l);
--          $EndJava
--        ./
    
    FormalDeclarators ::= FormalDeclarator
        /.$BeginJava
			r.rule_FormalDeclarators0(FormalDeclarator);
        $EndJava./
                          | FormalDeclarators , FormalDeclarator
        /.$BeginJava
			r.rule_FormalDeclarators1(FormalDeclarators,FormalDeclarator);
        $EndJava./
    
    
    FieldDeclarators ::= FieldDeclarator
        /.$BeginJava
			r.rule_FieldDeclarators0(FieldDeclarator);
        $EndJava./
                          | FieldDeclarators , FieldDeclarator
        /.$BeginJava
			r.rule_FieldDeclarators1(FieldDeclarators,FieldDeclarator);
        $EndJava./
    
    
    VariableDeclaratorsWithType ::= VariableDeclaratorWithType
        /.$BeginJava
			r.rule_VariableDeclaratorsWithType0(VariableDeclaratorWithType);
        $EndJava./
                          | VariableDeclaratorsWithType , VariableDeclaratorWithType
        /.$BeginJava
			r.rule_VariableDeclaratorsWithType1(VariableDeclaratorsWithType,VariableDeclaratorWithType);
        $EndJava./
    
    VariableDeclarators ::= VariableDeclarator
        /.$BeginJava
			r.rule_VariableDeclarators0(VariableDeclarator);
        $EndJava./
                          | VariableDeclarators , VariableDeclarator
        /.$BeginJava
			r.rule_VariableDeclarators1(VariableDeclarators,VariableDeclarator);
        $EndJava./
    
    AtCaptureDeclarators ::= AtCaptureDeclarator
        /.$BeginJava
			r.rule_AtCaptureDeclarators0(AtCaptureDeclarator);
        $EndJava./
                          | AtCaptureDeclarators , AtCaptureDeclarator
        /.$BeginJava
			r.rule_AtCaptureDeclarators1(AtCaptureDeclarators,AtCaptureDeclarator);
        $EndJava./
    
    HomeVariableList ::= HomeVariable
        /.$BeginJava
			r.rule_HomeVariableList0(HomeVariable);
        $EndJava./
                          | HomeVariableList , HomeVariable
        /.$BeginJava
			r.rule_HomeVariableList1(HomeVariableList,HomeVariable);
        $EndJava./
    
    HomeVariable ::= Identifier
        /.$BeginJava
			r.rule_HomeVariable0(Identifier);
        $EndJava./
                   | this
        /.$BeginJava
			r.rule_HomeVariable1();
        $EndJava./
    
    VariableInitializer ::= Expression
    
    ResultType ::= : Type
        /.$BeginJava
			r.rule_ResultType0(Type);
        $EndJava./
    HasResultType ::= ResultType
                  | '<:' Type
        /.$BeginJava
			r.rule_HasResultType1(Type);
        $EndJava./

    FormalParameterList ::= FormalParameter
        /.$BeginJava
			r.rule_FormalParameterList0(FormalParameter);
        $EndJava./
                       | FormalParameterList , FormalParameter
        /.$BeginJava
			r.rule_FormalParameterList1(FormalParameterList,FormalParameter);
        $EndJava./
        
     LoopIndexDeclarator ::= Identifier HasResultTypeopt
        /.$BeginJava
			r.rule_LoopIndexDeclarator0(Identifier,HasResultTypeopt);
        $EndJava./
                         | '[' IdentifierList ']' HasResultTypeopt
        /.$BeginJava
			r.rule_LoopIndexDeclarator1(IdentifierList,HasResultTypeopt);
        $EndJava./
                         | Identifier '[' IdentifierList ']' HasResultTypeopt
        /.$BeginJava
			r.rule_LoopIndexDeclarator2(Identifier,IdentifierList,HasResultTypeopt);
        $EndJava./
        
    LoopIndex ::= Modifiersopt LoopIndexDeclarator
        /.$BeginJava
			r.rule_LoopIndex0(Modifiersopt,LoopIndexDeclarator);
        $EndJava./
                      | Modifiersopt VarKeyword LoopIndexDeclarator
        /.$BeginJava
			r.rule_LoopIndex1(Modifiersopt,VarKeyword,LoopIndexDeclarator);
        $EndJava./
    
    FormalParameter ::= Modifiersopt FormalDeclarator
        /.$BeginJava
			r.rule_FormalParameter0(Modifiersopt,FormalDeclarator);
        $EndJava./
                      | Modifiersopt VarKeyword FormalDeclarator
        /.$BeginJava
			r.rule_FormalParameter1(Modifiersopt,VarKeyword,FormalDeclarator);
        $EndJava./
                      | Type
        /.$BeginJava
			r.rule_FormalParameter2(Type);
        $EndJava./

    OBSOLETE_Offers ::= offers Type
        /.$BeginJava
			r.rule_OBSOLETE_Offers0(Type);
        $EndJava./

    Throws ::= throws ThrowsList
      /.$BeginJava
			r.rule_Throws0(ThrowsList);
     $EndJava ./

    ThrowsList ::= Type
        /.$BeginJava
			r.rule_ThrowsList0(Type);
        $EndJava./
                          | ThrowsList , Type
        /.$BeginJava
			r.rule_ThrowsList1(ThrowsList,Type);
        $EndJava./
    

    MethodBody ::= = LastExpression ;
        /.$BeginJava
			r.rule_MethodBody0(LastExpression);
        $EndJava./
                  | = Annotationsopt { BlockStatementsopt LastExpression }
        /.$BeginJava
			r.rule_MethodBody1(Annotationsopt,BlockStatementsopt,LastExpression);
        $EndJava./
                  | = Annotationsopt Block
        /.$BeginJava
			r.rule_MethodBody2(Annotationsopt,Block);
        $EndJava./
                  | Annotationsopt Block
        /.$BeginJava
			r.rule_MethodBody3(Annotationsopt,Block);
        $EndJava./
                      | ;
        /.$NullAction./
    
--    InstanceInitializer ::= Block
--        /.$BeginJava
--                    setResult(nf.Initializer(pos(), nf.FlagsNode(pos(), Flags.NONE), Block));
--          $EndJava
--        ./
    
--    StaticInitializer ::= static Block
--        /.$BeginJava
--                    setResult(nf.Initializer(pos(), nf.FlagsNode(pos(getLeftSpan()), Flags.STATIC), Block));
--          $EndJava
--        ./
    
    ConstructorBody ::= = ConstructorBlock
        /.$BeginJava
			r.rule_ConstructorBody0(ConstructorBlock);
        $EndJava./
                      | ConstructorBlock
        /.$BeginJava
			r.rule_ConstructorBody1(ConstructorBlock);
        $EndJava./
                    | = ExplicitConstructorInvocation
        /.$BeginJava
			r.rule_ConstructorBody2(ExplicitConstructorInvocation);
        $EndJava./
                    | = AssignPropertyCall
        /.$BeginJava
			r.rule_ConstructorBody3(AssignPropertyCall);
        $EndJava./
                      | ;
        /.$NullAction./

    ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
        /.$BeginJava
			r.rule_ConstructorBlock0(ExplicitConstructorInvocationopt,BlockStatementsopt);
        $EndJava./
    
    Arguments ::= ( ArgumentList )
        /.$BeginJava
			r.rule_Arguments0(ArgumentList);
        $EndJava./
    
    
    ExtendsInterfaces ::= extends Type
        /.$BeginJava
			r.rule_ExtendsInterfaces0(Type);
        $EndJava./
                        | ExtendsInterfaces , Type
        /.$BeginJava
			r.rule_ExtendsInterfaces1(ExtendsInterfaces,Type);
        $EndJava./
    
    --
    -- See Chapter 4

    InterfaceBody ::= { InterfaceMemberDeclarationsopt }
        /.$BeginJava
			r.rule_InterfaceBody0(InterfaceMemberDeclarationsopt);
        $EndJava./
    
    InterfaceMemberDeclarations ::= InterfaceMemberDeclaration
        /.$BeginJava
			r.rule_InterfaceMemberDeclarations0(InterfaceMemberDeclaration);
        $EndJava./
                                  | InterfaceMemberDeclarations InterfaceMemberDeclaration
        /.$BeginJava
			r.rule_InterfaceMemberDeclarations1(InterfaceMemberDeclarations,InterfaceMemberDeclaration);
        $EndJava./
    
    InterfaceMemberDeclaration ::= MethodDeclaration
        /.$BeginJava
			r.rule_InterfaceMemberDeclaration0(MethodDeclaration);
        $EndJava./
                                 | PropertyMethodDeclaration
        /.$BeginJava
			r.rule_InterfaceMemberDeclaration1(PropertyMethodDeclaration);
        $EndJava./
                                 | FieldDeclaration
        /.$BeginJava
			r.rule_InterfaceMemberDeclaration2(FieldDeclaration);
        $EndJava./
                                 | TypeDeclaration
        /.$BeginJava
			r.rule_InterfaceMemberDeclaration3(TypeDeclaration);
        $EndJava./
    
    Annotations ::= Annotation
        /.$BeginJava
			r.rule_Annotations0(Annotation);
        $EndJava./
                  | Annotations Annotation
        /.$BeginJava
			r.rule_Annotations1(Annotations,Annotation);
        $EndJava./
    
    Annotation ::= @ NamedTypeNoConstraints
        /.$BeginJava
			r.rule_Annotation0(NamedTypeNoConstraints);
        $EndJava./
    
    Identifier ::= IDENTIFIER$ident
        /.$BeginJava
			r.rule_Identifier0();
        $EndJava./

    Block ::= { BlockStatementsopt }
        /.$BeginJava
			r.rule_Block0(BlockStatementsopt);
        $EndJava./
    
    BlockStatements ::= BlockInteriorStatement
        /.$BeginJava
			r.rule_BlockStatements0(BlockInteriorStatement);
        $EndJava./
                      | BlockStatements BlockInteriorStatement
        /.$BeginJava
			r.rule_BlockStatements1(BlockStatements,BlockInteriorStatement);
        $EndJava./
    
    BlockInteriorStatement ::= LocalVariableDeclarationStatement
                             | ClassDeclaration
        /.$BeginJava
			r.rule_BlockInteriorStatement1(ClassDeclaration);
        $EndJava./
                             | StructDeclaration
        /.$BeginJava
			r.rule_BlockInteriorStatement2(StructDeclaration);
        $EndJava./
                             | TypeDefDeclaration
        /.$BeginJava
			r.rule_BlockInteriorStatement3(TypeDefDeclaration);
        $EndJava./
                     | Statement
        /.$BeginJava
			r.rule_BlockInteriorStatement4(Statement);
        $EndJava./
    
    IdentifierList ::= Identifier
        /.$BeginJava
			r.rule_IdentifierList0(Identifier);
        $EndJava./
                     | IdentifierList , Identifier
        /.$BeginJava
			r.rule_IdentifierList1(IdentifierList,Identifier);
        $EndJava./
                    
    FormalDeclarator ::= Identifier ResultType
        /.$BeginJava
			r.rule_FormalDeclarator0(Identifier,ResultType);
        $EndJava./
                         | '[' IdentifierList ']' ResultType
        /.$BeginJava
			r.rule_FormalDeclarator1(IdentifierList,ResultType);
        $EndJava./
                         | Identifier '[' IdentifierList ']' ResultType
        /.$BeginJava
			r.rule_FormalDeclarator2(Identifier,IdentifierList,ResultType);
        $EndJava./
    
    FieldDeclarator ::= Identifier HasResultType
        /.$BeginJava
			r.rule_FieldDeclarator0(Identifier,HasResultType);
        $EndJava./
                         | Identifier HasResultTypeopt = VariableInitializer
        /.$BeginJava
			r.rule_FieldDeclarator1(Identifier,HasResultTypeopt,VariableInitializer);
        $EndJava./
                    
    VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
        /.$BeginJava
			r.rule_VariableDeclarator0(Identifier,HasResultTypeopt,VariableInitializer);
        $EndJava./
                         | '[' IdentifierList ']' HasResultTypeopt = VariableInitializer
        /.$BeginJava
			r.rule_VariableDeclarator1(IdentifierList,HasResultTypeopt,VariableInitializer);
        $EndJava./
                         | Identifier '[' IdentifierList ']' HasResultTypeopt = VariableInitializer
        /.$BeginJava
			r.rule_VariableDeclarator2(Identifier,IdentifierList,HasResultTypeopt,VariableInitializer);
        $EndJava./
                    
    VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
        /.$BeginJava
			r.rule_VariableDeclaratorWithType0(Identifier,HasResultType,VariableInitializer);
        $EndJava./
                         | '[' IdentifierList ']' HasResultType = VariableInitializer
        /.$BeginJava
			r.rule_VariableDeclaratorWithType1(IdentifierList,HasResultType,VariableInitializer);
        $EndJava./
                         | Identifier '[' IdentifierList ']' HasResultType = VariableInitializer
        /.$BeginJava
			r.rule_VariableDeclaratorWithType2(Identifier,IdentifierList,HasResultType,VariableInitializer);
        $EndJava./
    
    AtCaptureDeclarator ::= Modifiersopt VarKeywordopt VariableDeclarator
        /.$BeginJava
			r.rule_AtCaptureDeclarator0(Modifiersopt,VarKeywordopt,VariableDeclarator);
        $EndJava./
                         | Identifier
        /.$BeginJava
			r.rule_AtCaptureDeclarator1(Identifier);
        $EndJava./
                         | this
        /.$BeginJava
			r.rule_AtCaptureDeclarator2();
        $EndJava./
    
    LocalVariableDeclarationStatement ::= LocalVariableDeclaration ;
    
    LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
        /.$BeginJava
			r.rule_LocalVariableDeclaration0(Modifiersopt,VarKeyword,VariableDeclarators);
        $EndJava./
                               | Modifiersopt VariableDeclaratorsWithType
        /.$BeginJava
			r.rule_LocalVariableDeclaration1(Modifiersopt,VariableDeclaratorsWithType);
        $EndJava./
                               | Modifiersopt VarKeyword FormalDeclarators
        /.$BeginJava
			r.rule_LocalVariableDeclaration2(Modifiersopt,VarKeyword,FormalDeclarators);
        $EndJava./
    
    --
    -- See Chapter 8

    -- Chapter 15
    
    Primary ::= here
        /.$BeginJava
			r.rule_Primary0();
        $EndJava./
              | '[' ArgumentListopt ']'
        /.$BeginJava
			r.rule_Primary1(ArgumentListopt);
        $EndJava./

              | Literal
              | self
        /.$BeginJava
			r.rule_Primary3();
        $EndJava./
              | this
        /.$BeginJava
			r.rule_Primary4();
        $EndJava./
              | ClassName . this
        /.$BeginJava
			r.rule_Primary5(ClassName);
        $EndJava./
              | ( Expression )
        /.$BeginJava
			r.rule_Primary6(Expression);
        $EndJava./
              | ClassInstanceCreationExpression
              | FieldAccess
              | MethodInvocation

    Literal ::= IntLiteral$lit
        /.$BeginJava
			r.rule_Literal0();
        $EndJava./
              | LongLiteral$lit
        /.$BeginJava
			r.rule_Literal1();
        $EndJava./
              | ByteLiteral
        /.$BeginJava
			r.rule_LiteralByte();
        $EndJava./
              | UnsignedByteLiteral
        /.$BeginJava
			r.rule_LiteralUByte();
        $EndJava./
              | ShortLiteral
        /.$BeginJava
			r.rule_LiteralShort();
        $EndJava./
              | UnsignedShortLiteral
        /.$BeginJava
			r.rule_LiteralUShort();
        $EndJava./
              | UnsignedIntLiteral$lit
        /.$BeginJava
			r.rule_Literal2();
        $EndJava./
              | UnsignedLongLiteral$lit
        /.$BeginJava
			r.rule_Literal3();
        $EndJava./
              | FloatingPointLiteral$lit
        /.$BeginJava
			r.rule_Literal4();
        $EndJava./
              | DoubleLiteral$lit
        /.$BeginJava
			r.rule_Literal5();
        $EndJava./
              | BooleanLiteral
        /.$BeginJava
			r.rule_Literal6(BooleanLiteral);
        $EndJava./
              | CharacterLiteral$lit
        /.$BeginJava
			r.rule_Literal7();
        $EndJava./
              | StringLiteral$str
        /.$BeginJava
			r.rule_Literal8();
        $EndJava./
              | null
        /.$BeginJava
			r.rule_Literal9();
        $EndJava./

    BooleanLiteral ::= true$trueLiteral
        /.$BeginJava
			r.rule_BooleanLiteral0();
        $EndJava./
                     | false$falseLiteral
        /.$BeginJava
			r.rule_BooleanLiteral1();
        $EndJava./

    --
    -- The following case appeared to be missing from the spec:
    --
    ArgumentList ::= Expression
        /.$BeginJava
			r.rule_ArgumentList0(Expression);
        $EndJava./
                   | ArgumentList , Expression
        /.$BeginJava
			r.rule_ArgumentList1(ArgumentList,Expression);
        $EndJava./

    FieldAccess ::= Primary . Identifier
        /.$BeginJava
			r.rule_FieldAccess3(Primary,Identifier);
        $EndJava./
                  | super . Identifier
        /.$BeginJava
			r.rule_FieldAccess4(Identifier);
        $EndJava./
                  | ClassName . super$sup . Identifier
        /.$BeginJava
			r.rule_FieldAccess5(ClassName,Identifier);
        $EndJava./
    
    MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation3(MethodName,TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                       | Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation4(Primary,Identifier,TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                       | super . Identifier TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation5(Identifier,TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                       | ClassName . super . Identifier TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation6(ClassName,Identifier,TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                       | Primary TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation7(Primary,TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                       | OperatorPrefix TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation8(OperatorPrefix,TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                       | ClassName . operator as '[' Type ']' TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
			r.rule_OperatorPrefix25(ClassName,Type,TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                       | ClassName . operator '[' Type ']' TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
			r.rule_OperatorPrefix26(ClassName,Type,TypeArgumentsopt,ArgumentListopt);
        $EndJava./

    OperatorPrefix ::= operator BinOp
        /.$BeginJava
			r.rule_OperatorPrefix0(BinOp);
        $EndJava./
                       | FullyQualifiedName . operator BinOp
        /.$BeginJava
			r.rule_OperatorPrefix1(FullyQualifiedName,BinOp);
        $EndJava./
                       | Primary . operator BinOp
        /.$BeginJava
			r.rule_OperatorPrefix2(Primary,BinOp);
        $EndJava./
                       | super . operator BinOp
        /.$BeginJava
			r.rule_OperatorPrefix3(BinOp);
        $EndJava./
                       | ClassName . super . operator BinOp
        /.$BeginJava
			r.rule_OperatorPrefix4(ClassName,BinOp);
        $EndJava./
                       | operator ( ) BinOp
        /.$BeginJava
			r.rule_OperatorPrefix5(BinOp);
        $EndJava./
                       | FullyQualifiedName . operator ( ) BinOp
        /.$BeginJava
			r.rule_OperatorPrefix6(FullyQualifiedName,BinOp);
        $EndJava./
                       | Primary . operator ( ) BinOp
        /.$BeginJava
			r.rule_OperatorPrefix7(Primary,BinOp);
        $EndJava./
                       | super . operator ( ) BinOp
        /.$BeginJava
			r.rule_OperatorPrefix8(BinOp);
        $EndJava./
                       | ClassName . super . operator ( ) BinOp
        /.$BeginJava
			r.rule_OperatorPrefix9(ClassName,BinOp);
        $EndJava./
                       | operator PrefixOp
        /.$BeginJava
			r.rule_OperatorPrefix10(PrefixOp);
        $EndJava./
                       | FullyQualifiedName . operator PrefixOp
        /.$BeginJava
			r.rule_OperatorPrefix11(FullyQualifiedName,PrefixOp);
        $EndJava./
                       | Primary . operator PrefixOp
        /.$BeginJava
			r.rule_OperatorPrefix12(Primary,PrefixOp);
        $EndJava./
                       | super . operator PrefixOp
        /.$BeginJava
			r.rule_OperatorPrefix13(PrefixOp);
        $EndJava./
                       | ClassName . super . operator PrefixOp
        /.$BeginJava
			r.rule_OperatorPrefix14(ClassName,PrefixOp);
        $EndJava./
                       | operator ( )
        /.$BeginJava
			r.rule_OperatorPrefix15();
        $EndJava./
                       | FullyQualifiedName . operator ( )
        /.$BeginJava
			r.rule_OperatorPrefix16(FullyQualifiedName);
        $EndJava./
                       | Primary . operator ( )
        /.$BeginJava
			r.rule_OperatorPrefix17(Primary);
        $EndJava./
                       | super . operator ( )
        /.$BeginJava
			r.rule_OperatorPrefix18();
        $EndJava./
                       | ClassName . super . operator ( )
        /.$BeginJava
			r.rule_OperatorPrefix19(ClassName);
        $EndJava./
                       | operator ( ) =
        /.$BeginJava
			r.rule_OperatorPrefix20();
        $EndJava./
                       | FullyQualifiedName . operator ( ) =
        /.$BeginJava
			r.rule_OperatorPrefix21(FullyQualifiedName);
        $EndJava./
                       | Primary . operator ( ) =
        /.$BeginJava
			r.rule_OperatorPrefix22(Primary);
        $EndJava./
                       | super . operator ( ) =
        /.$BeginJava
			r.rule_OperatorPrefix23();
        $EndJava./
                       | ClassName . super . operator ( ) =
        /.$BeginJava
			r.rule_OperatorPrefix24(ClassName);
        $EndJava./

    PostfixExpression ::= CastExpression
                        | PostIncrementExpression
                        | PostDecrementExpression
    
    PostIncrementExpression ::= PostfixExpression ++
        /.$BeginJava
			r.rule_PostIncrementExpression0(PostfixExpression);
        $EndJava./
    
    PostDecrementExpression ::= PostfixExpression '--'
        /.$BeginJava
			r.rule_PostDecrementExpression0(PostfixExpression);
        $EndJava./
    
    UnannotatedUnaryExpression ::= PreIncrementExpression
                                 | PreDecrementExpression
                                 | OverloadableUnaryExpressionPlusMinus
                                 | UnaryExpressionNotPlusMinus

    OverloadableUnaryExpressionPlusMinus ::= + UnaryExpressionNotPlusMinus
        /.$BeginJava
			r.rule_UnannotatedUnaryExpression2(UnaryExpressionNotPlusMinus);
        $EndJava./
                                           | - UnaryExpressionNotPlusMinus
        /.$BeginJava
			r.rule_UnannotatedUnaryExpression3(UnaryExpressionNotPlusMinus);
        $EndJava./

    UnaryExpression ::= UnannotatedUnaryExpression
                      | Annotations UnannotatedUnaryExpression
        /.$BeginJava
			r.rule_UnaryExpression1(Annotations,UnannotatedUnaryExpression);
        $EndJava./
    
    PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
        /.$BeginJava
			r.rule_PreIncrementExpression0(UnaryExpressionNotPlusMinus);
        $EndJava./
    
    PreDecrementExpression ::= '--' UnaryExpressionNotPlusMinus
        /.$BeginJava
			r.rule_PreDecrementExpression0(UnaryExpressionNotPlusMinus);
        $EndJava./
    
    UnaryExpressionNotPlusMinus ::= PostfixExpression
                                  | OverloadableUnaryExpression

    OverloadableUnaryExpression ::= ~ UnaryExpression
        /.$BeginJava
			r.rule_UnaryExpressionNotPlusMinus1(UnaryExpression);
        $EndJava./
                                  | ! UnaryExpression
        /.$BeginJava
			r.rule_UnaryExpressionNotPlusMinus2(UnaryExpression);
        $EndJava./
                                  | ^ UnaryExpression
        /.$BeginJava
			r.rule_UnaryExpressionNotPlusMinus3(UnaryExpression);
        $EndJava./
                                  | '|' UnaryExpression
        /.$BeginJava
			r.rule_UnaryExpressionNotPlusMinus4(UnaryExpression);
        $EndJava./
                                  | & UnaryExpression
        /.$BeginJava
			r.rule_UnaryExpressionNotPlusMinus5(UnaryExpression);
        $EndJava./
                                  | * UnaryExpression
        /.$BeginJava
			r.rule_UnaryExpressionNotPlusMinus6(UnaryExpression);
        $EndJava./
                                  | / UnaryExpression
        /.$BeginJava
			r.rule_UnaryExpressionNotPlusMinus7(UnaryExpression);
        $EndJava./
                                  | '%' UnaryExpression
        /.$BeginJava
			r.rule_UnaryExpressionNotPlusMinus8(UnaryExpression);
        $EndJava./
    
    RangeExpression ::= UnaryExpression
                      | OverloadableRangeExpression

    OverloadableRangeExpression ::= RangeExpression .. UnaryExpression
        /.$BeginJava
			r.rule_RangeExpression1(RangeExpression,UnaryExpression);
        $EndJava./
    
    MultiplicativeExpression ::= RangeExpression
                               | OverloadableMultiplicativeExpression

    OverloadableMultiplicativeExpression ::= MultiplicativeExpression * RangeExpression
        /.$BeginJava
			r.rule_MultiplicativeExpression1(MultiplicativeExpression,RangeExpression);
        $EndJava./
                                           | MultiplicativeExpression / RangeExpression
        /.$BeginJava
			r.rule_MultiplicativeExpression2(MultiplicativeExpression,RangeExpression);
        $EndJava./
                                           | MultiplicativeExpression '%' RangeExpression
        /.$BeginJava
			r.rule_MultiplicativeExpression3(MultiplicativeExpression,RangeExpression);
        $EndJava./
                                           | MultiplicativeExpression ** RangeExpression
        /.$BeginJava
			r.rule_MultiplicativeExpression4(MultiplicativeExpression,RangeExpression);
        $EndJava./
    
    AdditiveExpression ::= MultiplicativeExpression
                         | OverloadableAdditiveExpression

    OverloadableAdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
        /.$BeginJava
			r.rule_AdditiveExpression1(AdditiveExpression,MultiplicativeExpression);
        $EndJava./
                                     | AdditiveExpression - MultiplicativeExpression
        /.$BeginJava
			r.rule_AdditiveExpression2(AdditiveExpression,MultiplicativeExpression);
        $EndJava./
    
    ShiftExpression ::= AdditiveExpression
                      | OverloadableShiftExpression

    OverloadableShiftExpression ::= ShiftExpression << AdditiveExpression
        /.$BeginJava
			r.rule_ShiftExpression1(ShiftExpression,AdditiveExpression);
        $EndJava./
                                  | ShiftExpression >> AdditiveExpression
        /.$BeginJava
			r.rule_ShiftExpression2(ShiftExpression,AdditiveExpression);
        $EndJava./
                                  | ShiftExpression >>> AdditiveExpression
        /.$BeginJava
			r.rule_ShiftExpression3(ShiftExpression,AdditiveExpression);
        $EndJava./
                                  | ShiftExpression$expr1 '->' AdditiveExpression$expr2
        /.$BeginJava
			r.rule_ShiftExpression4(expr1,expr2);
        $EndJava./
                                  | ShiftExpression$expr1 '<-' AdditiveExpression$expr2
        /.$BeginJava
			r.rule_ShiftExpression5(expr1,expr2);
        $EndJava./
                                  | ShiftExpression$expr1 '-<' AdditiveExpression$expr2
        /.$BeginJava
			r.rule_ShiftExpression6(expr1,expr2);
        $EndJava./
                                  | ShiftExpression$expr1 '>-' AdditiveExpression$expr2
        /.$BeginJava
			r.rule_ShiftExpression7(expr1,expr2);
        $EndJava./
                                  | ShiftExpression$expr1 '!' AdditiveExpression$expr2
        /.$BeginJava
			r.rule_ShiftExpression8(expr1,expr2);
        $EndJava./
                                  | ShiftExpression$expr1 <> AdditiveExpression$expr2
        /.$BeginJava
			r.rule_ShiftExpression9(expr1,expr2);
        $EndJava./
                                  | ShiftExpression$expr1 >< AdditiveExpression$expr2
        /.$BeginJava
			r.rule_ShiftExpression10(expr1,expr2);
        $EndJava./
    
    RelationalExpression ::= ShiftExpression
                           | HasZeroConstraint
                           | IsRefConstraint
                           | SubtypeConstraint
                           | OverloadableRelationalExpression
                           | RelationalExpression instanceof Type
        /.$BeginJava
			r.rule_RelationalExpression7(RelationalExpression,Type);
        $EndJava./

    OverloadableRelationalExpression ::= RelationalExpression < ShiftExpression
        /.$BeginJava
			r.rule_RelationalExpression3(RelationalExpression,ShiftExpression);
        $EndJava./
                                       | RelationalExpression > ShiftExpression
        /.$BeginJava
			r.rule_RelationalExpression4(RelationalExpression,ShiftExpression);
        $EndJava./
                                       | RelationalExpression <= ShiftExpression
        /.$BeginJava
			r.rule_RelationalExpression5(RelationalExpression,ShiftExpression);
        $EndJava./
                                       | RelationalExpression >= ShiftExpression
        /.$BeginJava
			r.rule_RelationalExpression6(RelationalExpression,ShiftExpression);
        $EndJava./
    
    EqualityExpression ::= RelationalExpression
                         | EqualityExpression == RelationalExpression
        /.$BeginJava
			r.rule_EqualityExpression1(EqualityExpression,RelationalExpression);
        $EndJava./
                         | EqualityExpression != RelationalExpression
        /.$BeginJava
			r.rule_EqualityExpression2(EqualityExpression,RelationalExpression);
        $EndJava./
                         | Type$t1 == Type$t2
        /.$BeginJava
			r.rule_EqualityExpression3(t1,t2);
        $EndJava./
                         | OverloadableEqualityExpression

    OverloadableEqualityExpression ::= EqualityExpression ~ RelationalExpression
        /.$BeginJava
			r.rule_EqualityExpression4(EqualityExpression,RelationalExpression);
        $EndJava./
                                     | EqualityExpression !~ RelationalExpression
        /.$BeginJava
			r.rule_EqualityExpression5(EqualityExpression,RelationalExpression);
        $EndJava./
    
    AndExpression ::= EqualityExpression
                    | OverloadableAndExpression

    OverloadableAndExpression ::= AndExpression & EqualityExpression
        /.$BeginJava
			r.rule_AndExpression1(AndExpression,EqualityExpression);
        $EndJava./
    
    ExclusiveOrExpression ::= AndExpression
                            | OverloadableExclusiveOrExpression

    OverloadableExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
        /.$BeginJava
			r.rule_ExclusiveOrExpression1(ExclusiveOrExpression,AndExpression);
        $EndJava./
    
    InclusiveOrExpression ::= ExclusiveOrExpression
                            | OverloadableInclusiveOrExpression

    OverloadableInclusiveOrExpression ::= InclusiveOrExpression '|' ExclusiveOrExpression
        /.$BeginJava
			r.rule_InclusiveOrExpression1(InclusiveOrExpression,ExclusiveOrExpression);
        $EndJava./
    
    ConditionalAndExpression ::= InclusiveOrExpression
                               | OverloadableConditionalAndExpression

    OverloadableConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
        /.$BeginJava
			r.rule_ConditionalAndExpression1(ConditionalAndExpression,InclusiveOrExpression);
        $EndJava./
    
    ConditionalOrExpression ::= ConditionalAndExpression
                              | OverloadableConditionalOrExpression

    OverloadableConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
        /.$BeginJava
			r.rule_ConditionalOrExpression1(ConditionalOrExpression,ConditionalAndExpression);
        $EndJava./
    
    
    ConditionalExpression ::= ConditionalOrExpression
                            | ClosureExpression
                            | AtExpression
                            | OBSOLETE_FinishExpression
                            | ConditionalOrExpression '?' Expression ':' ConditionalExpression
        /.$BeginJava
			r.rule_ConditionalExpression4(ConditionalOrExpression,Expression,ConditionalExpression);
        $EndJava./
    
    AssignmentExpression ::= Assignment
                           | ConditionalExpression
    
    Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
        /.$BeginJava
			r.rule_Assignment0(LeftHandSide,AssignmentOperator,AssignmentExpression);
        $EndJava./
                 | ExpressionName$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
        /.$BeginJava
			r.rule_Assignment1(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
        $EndJava./
                 | Primary$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
        /.$BeginJava
			r.rule_Assignment2(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
        $EndJava./
    
    LeftHandSide ::= ExpressionName
        /.$BeginJava
			r.rule_LeftHandSide0(ExpressionName);
        $EndJava./
                   | FieldAccess
    
    AssignmentOperator ::= =
        /.$BeginJava
			r.rule_AssignmentOperator0();
        $EndJava./
                         | *=
        /.$BeginJava
			r.rule_AssignmentOperator1();
        $EndJava./
                         | /=
        /.$BeginJava
			r.rule_AssignmentOperator2();
        $EndJava./
                         | '%='
        /.$BeginJava
			r.rule_AssignmentOperator3();
        $EndJava./
                         | +=
        /.$BeginJava
			r.rule_AssignmentOperator4();
        $EndJava./
                         | -=
        /.$BeginJava
			r.rule_AssignmentOperator5();
        $EndJava./
                         | <<=
        /.$BeginJava
			r.rule_AssignmentOperator6();
        $EndJava./
                         | >>=
        /.$BeginJava
			r.rule_AssignmentOperator7();
        $EndJava./
                         | >>>=
        /.$BeginJava
			r.rule_AssignmentOperator8();
        $EndJava./
                         | &=
        /.$BeginJava
			r.rule_AssignmentOperator9();
        $EndJava./
                         | ^=
        /.$BeginJava
			r.rule_AssignmentOperator10();
        $EndJava./
                         | |=
        /.$BeginJava
			r.rule_AssignmentOperator11();
        $EndJava./
                         | '..='
        /.$BeginJava
			r.rule_AssignmentOperator12();
        $EndJava./
                         | '->='
        /.$BeginJava
			r.rule_AssignmentOperator13();
        $EndJava./
                         | '<-='
        /.$BeginJava
			r.rule_AssignmentOperator14();
        $EndJava./
                         | '-<='
        /.$BeginJava
			r.rule_AssignmentOperator15();
        $EndJava./
                         | '>-='
        /.$BeginJava
			r.rule_AssignmentOperator16();
        $EndJava./
                         | **=
        /.$BeginJava
			r.rule_AssignmentOperator17();
        $EndJava./
                         | <>=
        /.$BeginJava
			r.rule_AssignmentOperator18();
        $EndJava./
                         | ><=
        /.$BeginJava
			r.rule_AssignmentOperator19();
        $EndJava./
                         | ~=
        /.$BeginJava
			r.rule_AssignmentOperator20();
        $EndJava./
    
    Expression ::= AssignmentExpression
    
    ConstantExpression ::= Expression


    PrefixOp ::= +
        /.$BeginJava
			r.rule_PrefixOp0();
        $EndJava./
      | -
        /.$BeginJava
			r.rule_PrefixOp1();
        $EndJava./
      | !
        /.$BeginJava
			r.rule_PrefixOp2();
        $EndJava./
      | ~
        /.$BeginJava
			r.rule_PrefixOp3();
        $EndJava./

    -- Non-standard X10 unary operators
      | ^
        /.$BeginJava
			r.rule_PrefixOp4();
        $EndJava./
      | '|'
        /.$BeginJava
			r.rule_PrefixOp5();
        $EndJava./
      | &
        /.$BeginJava
			r.rule_PrefixOp6();
        $EndJava./
      | *
        /.$BeginJava
			r.rule_PrefixOp7();
        $EndJava./
      | /
        /.$BeginJava
			r.rule_PrefixOp8();
        $EndJava./
      | '%'
        /.$BeginJava
			r.rule_PrefixOp9();
        $EndJava./
        
    BinOp ::= +
        /.$BeginJava
			r.rule_BinOp0();
        $EndJava./
      | -
        /.$BeginJava
			r.rule_BinOp1();
        $EndJava./
      | *
        /.$BeginJava
			r.rule_BinOp2();
        $EndJava./
      | /
        /.$BeginJava
			r.rule_BinOp3();
        $EndJava./
      | '%'
        /.$BeginJava
			r.rule_BinOp4();
        $EndJava./
      | &
        /.$BeginJava
			r.rule_BinOp5();
        $EndJava./
      | '|'
        /.$BeginJava
			r.rule_BinOp6();
        $EndJava./
      | ^
        /.$BeginJava
			r.rule_BinOp7();
        $EndJava./
      | &&
        /.$BeginJava
			r.rule_BinOp8();
        $EndJava./
      | '||'
        /.$BeginJava
			r.rule_BinOp9();
        $EndJava./
      | <<
        /.$BeginJava
			r.rule_BinOp10();
        $EndJava./
      | >>
        /.$BeginJava
			r.rule_BinOp11();
        $EndJava./
      | >>>
        /.$BeginJava
			r.rule_BinOp12();
        $EndJava./
      | >=
        /.$BeginJava
			r.rule_BinOp13();
        $EndJava./
      | <=
        /.$BeginJava
			r.rule_BinOp14();
        $EndJava./
      | >
        /.$BeginJava
			r.rule_BinOp15();
        $EndJava./
      | <
        /.$BeginJava
			r.rule_BinOp16();
        $EndJava./

-- FIXME: == and != shouldn't be allowed to be overridden.

      | ==
        /.$BeginJava
			r.rule_BinOp17();
        $EndJava./
      | !=
        /.$BeginJava
			r.rule_BinOp18();
        $EndJava./

    -- Non-standard X10 binary operators
      | '..'
        /.$BeginJava
			r.rule_BinOp19();
        $EndJava./
      | '->'
        /.$BeginJava
			r.rule_BinOp20();
        $EndJava./
      | '<-'
        /.$BeginJava
			r.rule_BinOp21();
        $EndJava./
      | '-<'
        /.$BeginJava
			r.rule_BinOp22();
        $EndJava./
      | '>-'
        /.$BeginJava
			r.rule_BinOp23();
        $EndJava./
      | **
        /.$BeginJava
			r.rule_BinOp24();
        $EndJava./
      | ~
        /.$BeginJava
			r.rule_BinOp25();
        $EndJava./
      | !~
        /.$BeginJava
			r.rule_BinOp26();
        $EndJava./
      | !
        /.$BeginJava
			r.rule_BinOp27();
        $EndJava./
      | <>
        /.$BeginJava
			r.rule_BinOp28();
        $EndJava./
      | ><
        /.$BeginJava
			r.rule_BinOp29();
        $EndJava./

    --
    -- Optional rules
    --
    Catchesopt ::= %Empty
        /.$BeginJava
			r.rule_Catchesopt0();
        $EndJava./
                 | Catches

    Identifieropt ::= %Empty
        /.$NullAction./
                    | Identifier
        /.$BeginJava
			r.rule_Identifieropt1(Identifier);
        $EndJava./

    ForUpdateopt ::= %Empty
        /.$BeginJava
			r.rule_ForUpdateopt0();
        $EndJava./
                   | ForUpdate

    Expressionopt ::= %Empty
        /.$NullAction./
                    | Expression

    ForInitopt ::= %Empty
        /.$BeginJava
			r.rule_ForInitopt0();
        $EndJava./
                 | ForInit

    SwitchLabelsopt ::= %Empty
        /.$BeginJava
			r.rule_SwitchLabelsopt0();
        $EndJava./
                      | SwitchLabels

    SwitchBlockStatementGroupsopt ::= %Empty
        /.$BeginJava
			r.rule_SwitchBlockStatementGroupsopt0();
        $EndJava./
                                    | SwitchBlockStatementGroups

    InterfaceMemberDeclarationsopt ::= %Empty
        /.$BeginJava
			r.rule_InterfaceMemberDeclarationsopt0();
        $EndJava./
                                     | InterfaceMemberDeclarations

    ExtendsInterfacesopt ::= %Empty
        /.$BeginJava
			r.rule_ExtendsInterfacesopt0();
        $EndJava./
                           | ExtendsInterfaces

    ClassBodyopt ::= %Empty
        /.$NullAction./
                   | ClassBody

    ArgumentListopt ::= %Empty
        /.$BeginJava
			r.rule_ArgumentListopt0();
        $EndJava./
                      | ArgumentList

    BlockStatementsopt ::= %Empty
        /.$BeginJava
			r.rule_BlockStatementsopt0();
        $EndJava./
                         | BlockStatements

    ExplicitConstructorInvocationopt ::= %Empty
        /.$NullAction./
                                       | ExplicitConstructorInvocation

    FormalParameterListopt ::= %Empty
        /.$BeginJava
			r.rule_FormalParameterListopt0();
        $EndJava./
                             | FormalParameterList

    OBSOLETE_Offersopt ::= %Empty
        /.$BeginJava
			r.rule_OBSOLETE_Offersopt0();
        $EndJava./
                | OBSOLETE_Offers

    Throwsopt ::= %Empty
        /.$BeginJava
			r.rule_Throwsopt0();
        $EndJava./
                | Throws

    ClassMemberDeclarationsopt ::= %Empty
        /.$BeginJava
			r.rule_ClassMemberDeclarationsopt0();
        $EndJava./
                               | ClassMemberDeclarations

    Interfacesopt ::= %Empty
        /.$BeginJava
			r.rule_Interfacesopt0();
        $EndJava./
                    | Interfaces

    Superopt ::= %Empty
        /.$NullAction./
               | Super

    TypeParametersopt ::= %Empty
        /.$BeginJava
			r.rule_TypeParametersopt0();
        $EndJava./
                        | TypeParameters
                        
    FormalParametersopt ::= %Empty
        /.$BeginJava
			r.rule_FormalParametersopt0();
        $EndJava./
                        | FormalParameters

    Annotationsopt ::= %Empty
        /.$BeginJava
			r.rule_Annotationsopt0();
        $EndJava./
                     | Annotations

    TypeDeclarationsopt ::= %Empty
        /.$BeginJava
			r.rule_TypeDeclarationsopt0();
        $EndJava./
                          | TypeDeclarations

    ImportDeclarationsopt ::= %Empty
        /.$BeginJava
			r.rule_ImportDeclarationsopt0();
        $EndJava./
                            | ImportDeclarations

    PackageDeclarationopt ::= %Empty
        /.$NullAction./
                            | PackageDeclaration
                            
    HasResultTypeopt ::= %Empty
        /.$NullAction./
                            | HasResultType
        
    TypeArgumentsopt ::= %Empty
        /.$BeginJava
			r.rule_TypeArgumentsopt0();
        $EndJava./
                       | TypeArguments
        
    TypeParamsWithVarianceopt ::= %Empty
        /.$BeginJava
			r.rule_TypeParamsWithVarianceopt0();
        $EndJava./
                       | TypeParamsWithVariance

    Propertiesopt ::= %Empty
        /.$BeginJava
			r.rule_Propertiesopt0();
        $EndJava./
                       | Properties

    VarKeywordopt ::= %Empty
        /.$NullAction./
                    | VarKeyword

    AtCaptureDeclaratorsopt ::= %Empty
        /.$BeginJava
			r.rule_AtCaptureDeclaratorsopt0();
        $EndJava./
                       | AtCaptureDeclarators

%End

%Types
	Object ::= ExpressionStatement | ClosureExpression | PackageOrTypeName | Property | CastExpression | TypeParameter | FieldDeclarator | FieldDeclarators | FullyQualifiedName | VariableDeclaratorWithType | VariableDeclaratorsWithType | Finally | AnnotationStatement | TypeDeclarations | IdentifierList | TypeImportOnDemandDeclaration | BreakStatement | ConditionalOrExpression | LocalVariableDeclaration | InterfaceMemberDeclarationsopt | InterfaceTypeList | AtomicStatement | PackageName | RelationalExpression | BlockInteriorStatement | UnaryExpression | ExclusiveOrExpression | ClockedClauseopt | AdditiveExpression | AssignPropertyCall | MultiplicativeExpression | ClosureBody | TryStatement | FormalParameterList | UnannotatedUnaryExpression | SwitchBlock | VariableDeclarator | VariableDeclarators | TypeParamWithVarianceList | NonExpressionStatement | UnaryExpressionNotPlusMinus | Interfacesopt | ConditionalExpression | SwitchLabel | BlockStatementsopt | BlockStatements | StatementExpression | OverloadableExpression | OverloadableUnaryExpressionPlusMinus | OverloadableUnaryExpression | OverloadableRangeExpression | OverloadableMultiplicativeExpression | OverloadableAdditiveExpression | OverloadableShiftExpression | OverloadableRelationalExpression | OverloadableEqualityExpression | OverloadableAndExpression | OverloadableExclusiveOrExpression | OverloadableInclusiveOrExpression | OverloadableConditionalAndExpression | OverloadableConditionalOrExpression | Expression | TypeParameterList | OBSOLETE_TypeParamWithVariance | Block | ResultType | ForUpdate | FunctionType | ConstraintConjunction | TypeParamsWithVariance | HasZeroConstraint | IsRefConstraint | FUTURE_ExistentialListopt | Annotation | BinOp | EqualityExpression | Modifiersopt | PostfixExpression | BooleanLiteral | ArgumentList | FormalParametersopt | ExtendsInterfacesopt | LoopStatement | Primary | InterfaceDeclaration | RangeExpression | SingleTypeImportDeclaration | DepNamedType | ImportDeclaration | InterfaceBody | WhereClauseopt | LabeledStatement | TypeArgumentList | ClassDeclaration | ParameterizedNamedType | SimpleNamedType | PreIncrementExpression | LoopIndex | Arguments | Literal | TypeDeclaration | ArgumentListopt | TypeArguments | Superopt | ClassMemberDeclarationsopt | HasResultTypeopt | Statement | LeftHandSide | TypeName | OBSOLETE_Offers | Super | SwitchLabelsopt | Propertiesopt | FieldAccess | MethodName | ForInit | OBSOLETE_OfferStatement | Expressionopt | ExplicitConstructorInvocationopt | AtEachStatement | OBSOLETE_Offersopt | TypeDeclarationsopt | ClassMemberDeclarations | WhereClause | InterfaceMemberDeclaration | PackageDeclaration | InterfaceMemberDeclarations | MethodInvocation | PreDecrementExpression | PrefixOp | ConstrainedType | Void | WhileStatement | Modifier | ExpressionName | TypeParamsWithVarianceopt | FormalParameterListopt | ConstraintConjunctionopt | ClassBody | ForStatement | Identifier | ClassName | AssignmentOperator | ForUpdateopt | AndExpression | OBSOLETE_FinishExpression | ReturnStatement | SubtypeConstraint | Catchesopt | MethodDeclaration | BinaryOperatorDeclaration | PrefixOperatorDeclaration | ApplyOperatorDeclaration | SetOperatorDeclaration | ConversionOperatorDeclaration | ExplicitConversionOperatorDeclaration | ImplicitConversionOperatorDeclaration | AssertStatement | DepParameters | DoStatement | PostDecrementExpression | AssignmentExpression | NamedType | NamedTypeNoConstraints | ExplicitConstructorInvocation | FormalParameter | BasicForStatement | Properties | SwitchStatement | LocalVariableDeclarationStatement | ThrowStatement | StatementExpressionList | ContinueStatement | SwitchBlockStatementGroups | TypeDefDeclaration | PropertyMethodDeclaration | ExtendsInterfaces | SwitchBlockStatementGroup | TypeParametersopt | ClassBodyopt | AtStatement | ConstructorBody | WhenStatement | AsyncStatement | MethodBody | FieldDeclaration | PackageDeclarationopt | VariableInitializer | ShiftExpression | Interfaces | ClassMemberDeclaration | IfThenStatement | StructDeclaration | ConstructorBlock | InclusiveOrExpression | HasResultType | PropertyList | ConditionalAndExpression | SwitchLabels | ImportDeclarationsopt | IfThenElseStatement | Identifieropt | AnnotatedType | ErrorPrimaryPrefix | ErrorSuperPrefix | ErrorClassNameSuperPrefix | ConstructorDeclaration | PostIncrementExpression | Catches | SwitchBlockStatementGroupsopt | CatchClause | ConstantExpression | FormalParameters | ClassInstanceCreationExpression | AtExpression | Type | CompilationUnit | Assignment | MethodModifiersopt | LastExpression | VarKeyword | TypeArgumentsopt | Annotationsopt | LoopIndexDeclarator | FinishStatement | Annotations | ImportDeclarations | TypeParameters | EnhancedForStatement | EmptyStatement | ClassType | FormalDeclarator | FormalDeclarators | FUTURE_ExistentialList | ForInitopt | ClockedClause | AtCaptureDeclaratorsopt | AtCaptureDeclarators | AtCaptureDeclarator | HomeVariableList | HomeVariable | VarKeywordopt | Throwsopt | Throws | ThrowsList
%End
