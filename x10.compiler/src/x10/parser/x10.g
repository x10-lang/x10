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
     *  (C) Copyright IBM Corporation 2006-2010.
     */
    /*****************************************************
     * WARNING!  THIS IS A GENERATED FILE.  DO NOT EDIT! *
     *****************************************************/
    ./
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

    RANGE ::= '..'
    ARROW ::= '->'
    DARROW ::= '=>'
    SUBTYPE ::= '<:'
    SUPERTYPE ::= ':>'
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
    next
--    null
--    offer
--    offers
--    operator
--    package
--    private
--    property
--    protected
--    public
    resume
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

    ExpressionName ::= AmbiguousName . ErrorId
            /.$BeginJava
			r.rule_ExpressionName0(AmbiguousName);
            $EndJava./

    MethodName ::= AmbiguousName . ErrorId
            /.$BeginJava
			r.rule_MethodName0(AmbiguousName);
            $EndJava./

    PackageOrTypeName ::= PackageOrTypeName . ErrorId
            /.$BeginJava
			r.rule_PackageOrTypeName0(PackageOrTypeName);
            $EndJava./

    AmbiguousName ::= AmbiguousName . ErrorId
            /.$BeginJava
			r.rule_AmbiguousName0(AmbiguousName);
            $EndJava./

    FieldAccess ::= Primary . ErrorId
        /.$BeginJava
			r.rule_FieldAccess0(Primary);
        $EndJava./
                  | super . ErrorId
        /.$BeginJava
			r.rule_FieldAccess1();
        $EndJava./
                  | ClassName . super$sup . ErrorId
        /.$BeginJava
			r.rule_FieldAccess2(ClassName);
        $EndJava./

    MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation0(MethodPrimaryPrefix,ArgumentListopt);
        $EndJava./
                       | MethodSuperPrefix ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation1(MethodSuperPrefix,ArgumentListopt);
        $EndJava./
                       | MethodClassNameSuperPrefix ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation2(MethodClassNameSuperPrefix,ArgumentListopt);
        $EndJava./

    MethodPrimaryPrefix ::= Primary . ErrorId$ErrorId
        /.$BeginJava
			r.rule_MethodPrimaryPrefix0(Primary);
        $EndJava./
    MethodSuperPrefix ::= super . ErrorId$ErrorId
        /.$BeginJava
			r.rule_MethodSuperPrefix0();
        $EndJava./
    MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
        /.$BeginJava
			r.rule_MethodClassNameSuperPrefix0(ClassName);
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
--                   | extern
--        /.$BeginJava
--                    setResult(new FlagModifier(pos(), FlagModifier.EXTERN));
--          $EndJava
--        ./
                   | final
        /.$BeginJava
			r.rule_Modifier3();
        $EndJava./
--                   | global
--        /.$BeginJava
--                    setResult(new FlagModifier(pos(), FlagModifier.GLOBAL));
--          $EndJava
--        ./
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

    TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
        /.$BeginJava
			r.rule_TypeDefDeclaration0(Modifiersopt,Identifier,TypeParametersopt,FormalParametersopt,WhereClauseopt,Type);
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
    
    
    Property ::=  Annotationsopt Identifier ResultType
        /.$BeginJava
			r.rule_Property0(Annotationsopt,Identifier,ResultType);
        $EndJava./

    MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt  Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration0(MethodModifiersopt,Identifier,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                

                                            
        $EndJava./
      | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt  Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration1(MethodModifiersopt,TypeParametersopt,fp1,BinOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                    
        $EndJava./
      | MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt  Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration2(MethodModifiersopt,TypeParametersopt,PrefixOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                    
        $EndJava./
      | MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt  Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration3(MethodModifiersopt,TypeParametersopt,BinOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                   
        $EndJava./
      | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt   Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration4(MethodModifiersopt,TypeParametersopt,fp1,BinOp,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
        $EndJava./
      | MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt  Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration5(MethodModifiersopt,TypeParametersopt,PrefixOp,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                
        $EndJava./
      | MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt   Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration6(MethodModifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                  
        $EndJava./
      | MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt  Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration7(MethodModifiersopt,TypeParametersopt,FormalParameters,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                     
        $EndJava./

        --  todo: Type WhereClauseopt   is ambiguous!
      | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt  Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration8(MethodModifiersopt,TypeParametersopt,fp1,Type,WhereClauseopt,Offersopt,MethodBody);
                                                     
        $EndJava./
      | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt   Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration9(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                     
        $EndJava./
      | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt  Offersopt MethodBody
        /.$BeginJava
			r.rule_MethodDeclaration10(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                     
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

    NormalInterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
        /.$BeginJava
			r.rule_NormalInterfaceDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,ExtendsInterfacesopt,InterfaceBody);
        $EndJava./

    ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
			r.rule_ClassInstanceCreationExpression0(TypeName,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
        $EndJava./
                 | new TypeName '[' Type ']' '[' ArgumentListopt ']'
        /.$BeginJava
			r.rule_ClassInstanceCreationExpression1(TypeName,Type,ArgumentListopt);
        $EndJava./
                                      | Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
			r.rule_ClassInstanceCreationExpression2(Primary,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
        $EndJava./
                                      | AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
			r.rule_ClassInstanceCreationExpression3(AmbiguousName,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
        $EndJava./
                       
      AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
       /.$BeginJava
			r.rule_AssignPropertyCall0(TypeArgumentsopt,ArgumentListopt);
        $EndJava./

    -------------------------------------- Section:::Types
    Type ::= FunctionType
           |  ConstrainedType
           |  VoidType

    FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt  Offersopt => Type
        /.$BeginJava
			r.rule_FunctionType0(TypeParametersopt,FormalParameterListopt,WhereClauseopt,Offersopt,Type);
        $EndJava./

    ClassType ::= NamedType

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    InterfaceType ::= FunctionType | NamedType | ( Type )
    
    AnnotatedType ::= Type Annotations
        /.$BeginJava
			r.rule_AnnotatedType0(Type,Annotations);
        $EndJava./

    ConstrainedType ::=  NamedType
           | AnnotatedType
           | ( Type )
        /.$BeginJava
			r.rule_ConstrainedType2(Type);
        $EndJava./

    VoidType ::= void
        /.$BeginJava
			r.rule_VoidType0();
        $EndJava./


    SimpleNamedType ::= TypeName
        /.$BeginJava
			r.rule_SimpleNamedType0(TypeName);
        $EndJava./
                      | Primary . Identifier
        /.$BeginJava
			r.rule_SimpleNamedType1(Primary,Identifier);
        $EndJava./
                      | DepNamedType . Identifier
        /.$BeginJava
			r.rule_SimpleNamedType2(DepNamedType,Identifier);
        $EndJava./

    DepNamedType ::= SimpleNamedType DepParameters
        /.$BeginJava
			r.rule_DepNamedType0(SimpleNamedType,DepParameters);
        $EndJava./
                | SimpleNamedType Arguments
        /.$BeginJava
			r.rule_DepNamedType1(SimpleNamedType,Arguments);
        $EndJava./
                | SimpleNamedType Arguments DepParameters
        /.$BeginJava
			r.rule_DepNamedType2(SimpleNamedType,Arguments,DepParameters);
        $EndJava./
                | SimpleNamedType TypeArguments
        /.$BeginJava
			r.rule_DepNamedType3(SimpleNamedType,TypeArguments);
        $EndJava./
                | SimpleNamedType TypeArguments DepParameters
        /.$BeginJava
			r.rule_DepNamedType4(SimpleNamedType,TypeArguments,DepParameters);
        $EndJava./
                | SimpleNamedType TypeArguments Arguments
        /.$BeginJava
			r.rule_DepNamedType5(SimpleNamedType,TypeArguments,Arguments);
        $EndJava./
                | SimpleNamedType TypeArguments Arguments DepParameters
        /.$BeginJava
			r.rule_DepNamedType6(SimpleNamedType,TypeArguments,Arguments,DepParameters);
        $EndJava./

    NamedType ::= SimpleNamedType
                | DepNamedType
        
    DepParameters ::= { ExistentialListopt Conjunctionopt }
         /.$BeginJava
			r.rule_DepParameters0(ExistentialListopt,Conjunctionopt);
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

    Conjunction ::= Expression
        /.$BeginJava
			r.rule_Conjunction0(Expression);
        $EndJava./
                  | Conjunction , Expression
         /.$BeginJava
			r.rule_Conjunction1(Conjunction,Expression);
        $EndJava./

    HasZeroConstraint ::= Type$t1 haszero
         /.$BeginJava
			r.rule_HasZeroConstraint0(t1);
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

    Conjunctionopt ::= %Empty
          /.$BeginJava
			r.rule_Conjunctionopt0();
          $EndJava./
          | Conjunction
          /.$BeginJava
			r.rule_Conjunctionopt1(Conjunction);
        $EndJava./

    ExistentialListopt ::= %Empty
          /.$BeginJava
			r.rule_ExistentialListopt0();
          $EndJava./
          | ExistentialList ;
          /.$BeginJava
			r.rule_ExistentialListopt1(ExistentialList);
        $EndJava./

       ExistentialList ::= FormalParameter
        /.$BeginJava
			r.rule_ExistentialList0(FormalParameter);
        $EndJava./
                          | ExistentialList ; FormalParameter
        /.$BeginJava
			r.rule_ExistentialList1(ExistentialList,FormalParameter);
        $EndJava./


    ------------------------------------- Section ::: Classes
    ClassDeclaration ::= StructDeclaration
                       | NormalClassDeclaration
        
    NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
        /.$BeginJava
			r.rule_NormalClassDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Superopt,Interfacesopt,ClassBody);
        $EndJava./


    StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
        /.$BeginJava
			r.rule_StructDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Interfacesopt,ClassBody);
        $EndJava./

    ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt  Offersopt ConstructorBody
        /.$BeginJava
			r.rule_ConstructorDeclaration0(Modifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ConstructorBody);
                                                               
        $EndJava./
       
     Super ::= extends ClassType
        /.$BeginJava
			r.rule_Super0(ClassType);
        $EndJava./
    
    FieldKeyword ::= val
        /.$BeginJava
			r.rule_FieldKeyword0();
        $EndJava./
                   | var
        /.$BeginJava
			r.rule_FieldKeyword1();
        $EndJava./
                   
                   
                   
    VarKeyword ::= val 
        /.$BeginJava
			r.rule_VarKeyword0();
        $EndJava./
                   | var 
        /.$BeginJava
			r.rule_VarKeyword1();
        $EndJava./
                    
                   
    FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
        /.$BeginJava
			r.rule_FieldDeclaration0(Modifiersopt,FieldKeyword,FieldDeclarators);
        
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
--                | ForEachStatement
                | AtEachStatement
                | FinishStatement
                | NextStatement
                | ResumeStatement
--                | AwaitStatement
                | AssignPropertyCall
                | OfferStatement
    
   OfferStatement ::= offer Expression ;
         /.$BeginJava
			r.rule_OfferStatement0(Expression);
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
--                    | ForEachStatement
    
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

   ClockedClause ::= clocked ( ClockList )
        /.$BeginJava
			r.rule_ClockedClause0(ClockList);
        $EndJava./
        

    AsyncStatement ::= async ClockedClauseopt Statement
        /.$BeginJava
			r.rule_AsyncStatement0(ClockedClauseopt,Statement);
        $EndJava./
         | clocked async Statement
        /.$BeginJava
			r.rule_AsyncStatement1(Statement);
        $EndJava./


    AtStatement ::= at PlaceExpressionSingleList Statement
        /.$BeginJava
			r.rule_AtStatement0(PlaceExpressionSingleList,Statement);
        $EndJava./

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

--    ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
--        /.$BeginJava
--                    FlagsNode fn = LoopIndex.flags();
--                    if (! fn.flags().isFinal()) {
--                        syntaxError("Enhanced foreach loop may not have var loop index. " + LoopIndex, LoopIndex.position());
--                        fn = fn.flags(fn.flags().Final());
--                        LoopIndex = LoopIndex.flags(fn);
--                    }
--                    setResult(nf.ForEach(pos(),
--                                 LoopIndex,
--                                  Expression,
--                                  ClockedClauseopt,
--                                  Statement));
--          $EndJava
--        ./ 
--         | clocked foreach ( LoopIndex in Expression ) Statement
--        /.$BeginJava
--                    FlagsNode fn = LoopIndex.flags();
--                    if (! fn.flags().isFinal()) {
--                        syntaxError("Enhanced foreach loop may not have var loop index" + LoopIndex, LoopIndex.position());
--                        fn = fn.flags(fn.flags().Final());
--                        LoopIndex = LoopIndex.flags(fn);
--                    }
--                    setResult(nf.ForEach(pos(),
--                                  LoopIndex,
--                                  Expression,
--                                  Statement));
--          $EndJava
--        ./ 
--         | foreach ( Expression ) Statement
--        /.$BeginJava
--                    Id name = nf.Id(pos(), Name.makeFresh());
--                    TypeNode type = nf.UnknownTypeNode(pos());
--                    setResult(nf.ForEach(pos(),
--                            nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
--                            Expression,
--                            new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
--                            Statement));
--          $EndJava
--        ./ 
--         | clocked foreach ( Expression ) Statement
--        /.$BeginJava
--                    Id name = nf.Id(pos(), Name.makeFresh());
--                    TypeNode type = nf.UnknownTypeNode(pos());
--                    setResult(nf.ForEach(pos(),
--                            nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
--                            Expression,
--                            Statement));
--          $EndJava
--        ./ 

    AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
        /.$BeginJava
			r.rule_AtEachStatement0(LoopIndex,Expression,ClockedClauseopt,Statement);
        $EndJava./
    EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
        /.$BeginJava
			r.rule_EnhancedForStatement0(LoopIndex,Expression,Statement);
        $EndJava./

    FinishStatement ::= finish Statement
        /.$BeginJava
			r.rule_FinishStatement0(Statement);
        $EndJava./
                | clocked finish Statement
        /.$BeginJava
			r.rule_FinishStatement1(Statement);
        $EndJava./
    PlaceExpressionSingleList ::= ( PlaceExpression )
        /.$BeginJava
			r.rule_PlaceExpressionSingleList0(PlaceExpression);
        $EndJava./

    PlaceExpression ::= Expression

    NextStatement ::= next ;
        /.$BeginJava
			r.rule_NextStatement0();
        $EndJava./
        
        ResumeStatement ::= resume ;
        /.$BeginJava
			r.rule_ResumeStatement0();
        $EndJava./

 ClockList ::= Clock
        /.$BeginJava
			r.rule_ClockList0(Clock);
        $EndJava./
                | ClockList , Clock
        /.$BeginJava
			r.rule_ClockList1(ClockList,Clock);
        $EndJava./

    -- The type-checker will ensure that the identifier names a variable declared as a clock.
    Clock ::= Expression
        /.$BeginJava
			r.rule_Clock0(Expression);
        $EndJava./
--
--      Clock ::= Identifier
--        /.$BeginJava
--                    setResult(new X10ParsedName(nf, ts, pos(), Identifier).toExpr());
--          $EndJava
--        ./

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
     TypeParamWithVarianceList ::= TypeParamWithVariance
        /.$BeginJava
			r.rule_TypeParamWithVarianceList0(TypeParamWithVariance);
        $EndJava./
                      | TypeParamWithVarianceList , TypeParamWithVariance
        /.$BeginJava
			r.rule_TypeParamWithVarianceList1(TypeParamWithVarianceList,TypeParamWithVariance);
        $EndJava./
        
     TypeParameterList ::= TypeParameter
        /.$BeginJava
			r.rule_TypeParameterList0(TypeParameter);
        $EndJava./
                      | TypeParameterList , TypeParameter
        /.$BeginJava
			r.rule_TypeParameterList1(TypeParameterList,TypeParameter);
        $EndJava./
        
    TypeParamWithVariance ::= Identifier
        /.$BeginJava
			r.rule_TypeParamWithVariance0(Identifier);
        $EndJava./
                   | + Identifier
        /.$BeginJava
			r.rule_TypeParamWithVariance1(Identifier);
        $EndJava./
                   | - Identifier
        /.$BeginJava
			r.rule_TypeParamWithVariance2(Identifier);
        $EndJava./
        
    TypeParameter ::= Identifier
        /.$BeginJava
			r.rule_TypeParameter0(Identifier);
        $EndJava./

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    RegionExpression ::= Expression
--
--    RegionExpressionList ::= RegionExpression
--        /.$BeginJava
--                    List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
--                    l.add(RegionExpression);
--                    setResult(l);
--          $EndJava
--        ./
--               | RegionExpressionList , RegionExpression
--        /.$BeginJava
--                    RegionExpressionList.add(RegionExpression);
--                    //setResult(RegionExpressionList);
--          $EndJava
--        ./

    ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt  Offersopt => ClosureBody
        /.$BeginJava
			r.rule_ClosureExpression0(FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ClosureBody);
        $EndJava./

    LastExpression ::= Expression
        /.$BeginJava
			r.rule_LastExpression0(Expression);
        $EndJava./

    ClosureBody ::= ConditionalExpression
        /.$BeginJava
			r.rule_ClosureBody0(ConditionalExpression);
        $EndJava./
                  | Annotationsopt { BlockStatementsopt LastExpression }
        /.$BeginJava
			r.rule_ClosureBody1(Annotationsopt,BlockStatementsopt,LastExpression);
        $EndJava./
                  | Annotationsopt Block
        /.$BeginJava
			r.rule_ClosureBody2(Annotationsopt,Block);
        $EndJava./
                  
                  
    AtExpression ::= at PlaceExpressionSingleList ClosureBody
        /.$BeginJava
			r.rule_AtExpression0(PlaceExpressionSingleList,ClosureBody);
        $EndJava./

    FinishExpression ::= finish ( Expression ) Block
        /.$BeginJava
			r.rule_FinishExpression0(Expression,Block);
        $EndJava./
        
    ---------------------------------------- All the opts...

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    DepParametersopt ::= %Empty
--        /.$NullAction./
--                       | DepParameters

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    PropertyListopt ::=  %Empty
--        /.$BeginJavppa
--                    setResult(new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false));
--          $EndJava
--        ./
--                       | PropertyList
                       
    WhereClauseopt ::= %Empty
        /.$NullAction./
                     | WhereClause


--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    ClassModifiersopt ::= %Empty
--        /.$BeginJava
--             setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
--          $EndJava ./
--          | ClassModifiers
--          
--    TypeDefModifiersopt ::= %Empty
--        /.$BeginJava
--             setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
--          $EndJava ./
--          | TypeDefModifiers
          
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
                     | AmbiguousName . Identifier
        /.$BeginJava
			r.rule_ExpressionName2(AmbiguousName,Identifier);
        $EndJava./

    MethodName ::=? Identifier
        /.$BeginJava
			r.rule_MethodName1(Identifier);
        $EndJava./
                 | AmbiguousName . Identifier
        /.$BeginJava
			r.rule_MethodName2(AmbiguousName,Identifier);
        $EndJava./

    PackageOrTypeName ::= Identifier
        /.$BeginJava
			r.rule_PackageOrTypeName1(Identifier);
        $EndJava./
                        | PackageOrTypeName . Identifier
        /.$BeginJava
			r.rule_PackageOrTypeName2(PackageOrTypeName,Identifier);
        $EndJava./

    AmbiguousName ::=? Identifier
        /.$BeginJava
			r.rule_AmbiguousName1(Identifier);
        $EndJava./
                    | AmbiguousName . Identifier
        /.$BeginJava
			r.rule_AmbiguousName2(AmbiguousName,Identifier);
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
                      | InterfaceDeclaration
                      | TypeDefDeclaration
                      | ;
        /.$BeginJava
			r.rule_TypeDeclaration3();
        $EndJava./

    -- Chapter 8

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    ClassModifiers ::= ClassModifier
--        /.$BeginJava
--                    List<Node> l = new LinkedList<Node>();
--                    l.addAll(ClassModifier);
--                    setResult(l);
--          $EndJava
--        ./
--                     | ClassModifiers ClassModifier
--        /.$BeginJava
--                    ClassModifiers.addAll(ClassModifier);
--          $EndJava
--        ./
--
--    ClassModifier ::= Annotation
--        /.$BeginJava
--                    setResult(Collections.singletonList(Annotation));
--          $EndJava
--        ./
--                    | public
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
--          $EndJava
--        ./
--                    | protected
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
--          $EndJava
--        ./
--                    | private
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
--          $EndJava
--        ./
--                    | abstract
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
--          $EndJava
--        ./
--                    | static
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
--          $EndJava
--        ./
--                    | final
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
--          $EndJava
--        ./
--                    | safe
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
--          $EndJava
--        ./
--        
--    TypeDefModifiers ::= TypeDefModifier
--        /.$BeginJava
--                    List<Node> l = new LinkedList<Node>();
--                    l.addAll(TypeDefModifier);
--                    setResult(l);
--          $EndJava
--        ./
--                     | TypeDefModifiers TypeDefModifier
--        /.$BeginJava
--                    TypeDefModifiers.addAll(TypeDefModifier);
--          $EndJava
--        ./
--
--    TypeDefModifier ::= Annotation
--        /.$BeginJava
--                    setResult(Collections.singletonList(Annotation));
--          $EndJava
--        ./
--                    | public
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
--          $EndJava
--        ./
--                    | protected
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
--          $EndJava
--        ./
--                    | private
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
--          $EndJava
--        ./
--                    | abstract
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
--          $EndJava
--        ./
--                    | static
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
--          $EndJava
--        ./
--                    | final
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
--          $EndJava
--        ./
        
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
    ClassBody ::= { ClassBodyDeclarationsopt }
        /.$BeginJava
			r.rule_ClassBody0(ClassBodyDeclarationsopt);
        $EndJava./

    ClassBodyDeclarations ::= ClassBodyDeclaration
                            | ClassBodyDeclarations ClassBodyDeclaration
        /.$BeginJava
			r.rule_ClassBodyDeclarations1(ClassBodyDeclarations,ClassBodyDeclaration);
        $EndJava./

    ClassBodyDeclaration ::= ClassMemberDeclaration
--                           | InstanceInitializer
--        /.$BeginJava
--                    List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
--                    l.add(InstanceInitializer);
--                    setResult(l);
--          $EndJava
--        ./
--                           | StaticInitializer
--        /.$BeginJava
--                    List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
--                    l.add(StaticInitializer);
--                    setResult(l);
--          $EndJava
--        ./
                           | ConstructorDeclaration
        /.$BeginJava
			r.rule_ClassBodyDeclaration1(ConstructorDeclaration);
        $EndJava./

    ClassMemberDeclaration ::= FieldDeclaration
                             | MethodDeclaration
        /.$BeginJava
			r.rule_ClassMemberDeclaration1(MethodDeclaration);
        $EndJava./
                             | PropertyMethodDeclaration
        /.$BeginJava
			r.rule_ClassMemberDeclaration2(PropertyMethodDeclaration);
        $EndJava./
                             | TypeDefDeclaration
        /.$BeginJava
			r.rule_ClassMemberDeclaration3(TypeDefDeclaration);
        $EndJava./
                             | ClassDeclaration
        /.$BeginJava
			r.rule_ClassMemberDeclaration4(ClassDeclaration);
        $EndJava./
                             | InterfaceDeclaration
        /.$BeginJava
			r.rule_ClassMemberDeclaration5(InterfaceDeclaration);
        $EndJava./
                             | ;
        /.$BeginJava
			r.rule_ClassMemberDeclaration6();
        $EndJava./
    
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
    
    VariableInitializer ::= Expression
    
--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    FieldModifiers ::= FieldModifier
--        /.$BeginJava
--                    List<Node> l = new LinkedList<Node>();
--                    l.addAll(FieldModifier);
--                    setResult(l);
--          $EndJava
--        ./
--                     | FieldModifiers FieldModifier
--        /.$BeginJava
--                    FieldModifiers.addAll(FieldModifier);
--          $EndJava
--        ./
--    
--    FieldModifier ::= Annotation
--        /.$BeginJava
--                    setResult(Collections.singletonList(Annotation));
--          $EndJava
--        ./
--                    | public
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
--          $EndJava
--        ./
--                    | protected
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
--          $EndJava
--        ./
--                    | private
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
--          $EndJava
--        ./
--                    | static
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
--          $EndJava
--        ./
--                    | global
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
--          $EndJava
--        ./
    
    ResultType ::= : Type
     /.$BeginJava
			r.rule_ResultType0(Type);
        $EndJava./
    HasResultType ::= : Type
     /.$BeginJava
			r.rule_HasResultType0(Type);
        $EndJava./
                  | '<:' Type
     /.$BeginJava
			r.rule_HasResultType1(Type);
        $EndJava./

--
-- This duplicated rule is not needed!
--       
--    FormalParameters ::= ( FormalParameterList )
--        /.$BeginJava
--                    setResult(FormalParameterList);
--          $EndJava
--        ./
    
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
    
--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    VariableModifiers ::= VariableModifier
--        /.$BeginJava
--                    List<Node> l = new LinkedList<Node>();
--                    l.addAll(VariableModifier);
--                    setResult(l);
--          $EndJava
--        ./
--                        | VariableModifiers VariableModifier
--        /.$BeginJava
--                    VariableModifiers.addAll(VariableModifier);
--          $EndJava
--        ./
--    
--    VariableModifier ::= Annotation
--        /.$BeginJava
--                    setResult(Collections.singletonList(Annotation));
--          $EndJava
--        ./
--                       | shared
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
--          $EndJava
--        ./
    
    --
    -- See above
    --    
--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    MethodModifiers ::= MethodModifier
--        /.$BeginJava
--                    List<Node> l = new LinkedList<Node>();
--                    l.addAll(MethodModifier);
--                    setResult(l);
--          $EndJava
--        ./
--                      | MethodModifiers MethodModifier
--        /.$BeginJava
--                    MethodModifiers.addAll(MethodModifier);
--          $EndJava
--        ./
--    
--    MethodModifier ::= Annotation
--        /.$BeginJava
--                    setResult(Collections.singletonList(Annotation));
--          $EndJava
--        ./
--                     | public
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
--          $EndJava
--        ./
--                     | protected
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
--          $EndJava
--        ./
--                     | private
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
--          $EndJava
--        ./
--                     | abstract
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
--          $EndJava
--        ./
--                     | static
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
--          $EndJava
--        ./
--                     | final
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
--          $EndJava
--        ./
--                     | native
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
--          $EndJava
--        ./
--                     | atomic
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
--          $EndJava
--        ./
--                     | extern
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
--          $EndJava
--        ./
--                     | safe
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
--          $EndJava
--        ./
--                     | sequential
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
--          $EndJava
--        ./
--                     | nonblocking
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
--          $EndJava
--        ./
--                     | incomplete
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
--          $EndJava
--        ./
--                     | property
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
--          $EndJava
--        ./
--                     | global
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
--          $EndJava
--        ./
--                     | proto
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROTO)));
--          $EndJava
--        ./

    
     Offers ::= offers Type
        /.$BeginJava
			r.rule_Offers0(Type);
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
      
--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    SimpleTypeName ::= Identifier
--        /.$BeginJava
--                    setResult(new X10ParsedName(nf, ts, pos(), Identifier));
--          $EndJava
--        ./

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    ConstructorModifiers ::= ConstructorModifier
--        /.$BeginJava
--                    List<Node> l = new LinkedList<Node>();
--                    l.addAll(ConstructorModifier);
--                    setResult(l);
--          $EndJava
--        ./
--                           | ConstructorModifiers ConstructorModifier
--        /.$BeginJava
--                    ConstructorModifiers.addAll(ConstructorModifier);
--          $EndJava
--        ./
--    
--    ConstructorModifier ::= Annotation
--        /.$BeginJava
--                    setResult(Collections.singletonList(Annotation));
--          $EndJava
--        ./
--                          | public
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
--          $EndJava
--        ./
--                          | protected
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
--          $EndJava
--        ./
--                          | private
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
--          $EndJava
--        ./
--                          | native
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
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
    
    Arguments ::= ( ArgumentListopt )
        /.$BeginJava
			r.rule_Arguments0(ArgumentListopt);
        $EndJava./
    
    -- chapter 9
    
    InterfaceDeclaration ::= NormalInterfaceDeclaration
    
--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    InterfaceModifiers ::= InterfaceModifier
--        /.$BeginJava
--                    List<Node> l = new LinkedList<Node>();
--                    l.addAll(InterfaceModifier);
--                    setResult(l);
--          $EndJava
--        ./
--                         | InterfaceModifiers InterfaceModifier
--        /.$BeginJava
--                    InterfaceModifiers.addAll(InterfaceModifier);
--          $EndJava
--        ./
--    
--    InterfaceModifier ::= Annotation
--        /.$BeginJava
--                    setResult(Collections.singletonList(Annotation));
--          $EndJava
--        ./
--                        | public
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
--          $EndJava
--        ./
--                        | protected
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
--          $EndJava
--        ./
--                        | private
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
--          $EndJava
--        ./
--                        | abstract
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
--          $EndJava
--        ./
--                        | static
--        /.$BeginJava
--                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
--          $EndJava
--        ./
    
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
                                 | ClassDeclaration
        /.$BeginJava
			r.rule_InterfaceMemberDeclaration3(ClassDeclaration);
        $EndJava./
                                 | InterfaceDeclaration
        /.$BeginJava
			r.rule_InterfaceMemberDeclaration4(InterfaceDeclaration);
        $EndJava./
                                 | TypeDefDeclaration
        /.$BeginJava
			r.rule_InterfaceMemberDeclaration5(TypeDefDeclaration);
        $EndJava./
                                 | ;
        /.$BeginJava
			r.rule_InterfaceMemberDeclaration6();
        $EndJava./
    
    Annotations ::= Annotation
        /.$BeginJava
			r.rule_Annotations0(Annotation);
        $EndJava./
                  | Annotations Annotation
        /.$BeginJava
			r.rule_Annotations1(Annotations,Annotation);
        $EndJava./
    
    Annotation ::= @ NamedType
        /.$BeginJava
			r.rule_Annotation0(NamedType);
        $EndJava./
    
--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    SimpleName ::= Identifier
--        /.$BeginJava
--                    setResult(new X10ParsedName(nf, ts, pos(), Identifier));
--          $EndJava
--        ./
        
    Identifier ::= IDENTIFIER$ident
        /.$BeginJava
			r.rule_Identifier0();
        $EndJava./

    -- Chapter 10
    
--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    VariableInitializers ::= VariableInitializer
--        /.$BeginJava
--                    List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
--                    l.add(VariableInitializer);
--                    setResult(l);
--          $EndJava
--        ./
--                           | VariableInitializers , VariableInitializer
--        /.$BeginJava
--                    VariableInitializers.add(VariableInitializer);
--                    //setResult(VariableInitializers);
--          $EndJava
--        ./
    
    --
    -- See Chapter 8
    
    -- Chapter 11
    
    -- Chapter 12
    
    -- Chapter 13
    
    -- Chapter 14
    
    Block ::= { BlockStatementsopt }
        /.$BeginJava
			r.rule_Block0(BlockStatementsopt);
        $EndJava./
    
    BlockStatements ::= BlockStatement
        /.$BeginJava
			r.rule_BlockStatements0(BlockStatement);
        $EndJava./
                      | BlockStatements BlockStatement
        /.$BeginJava
			r.rule_BlockStatements1(BlockStatements,BlockStatement);
        $EndJava./
    
    BlockStatement ::= LocalVariableDeclarationStatement
                     | ClassDeclaration
        /.$BeginJava
			r.rule_BlockStatement1(ClassDeclaration);
        $EndJava./
                     | TypeDefDeclaration
        /.$BeginJava
			r.rule_BlockStatement2(TypeDefDeclaration);
        $EndJava./
                     | Statement
        /.$BeginJava
			r.rule_BlockStatement3(Statement);
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
              | MethodSelection
              | OperatorFunction
                        
    OperatorFunction ::= TypeName . +
            /.$BeginJava
			r.rule_OperatorFunction0(TypeName);
        $EndJava./
                       | TypeName . -
            /.$BeginJava
			r.rule_OperatorFunction1(TypeName);
        $EndJava./
                       | TypeName . *
            /.$BeginJava
			r.rule_OperatorFunction2(TypeName);
        $EndJava./
                       | TypeName . /
            /.$BeginJava
			r.rule_OperatorFunction3(TypeName);
        $EndJava./
                       | TypeName . '%'
            /.$BeginJava
			r.rule_OperatorFunction4(TypeName);
        $EndJava./
                       | TypeName . &
            /.$BeginJava
			r.rule_OperatorFunction5(TypeName);
        $EndJava./
                       | TypeName . '|'
            /.$BeginJava
			r.rule_OperatorFunction6(TypeName);
        $EndJava./
                       | TypeName . ^
            /.$BeginJava
			r.rule_OperatorFunction7(TypeName);
        $EndJava./
                       | TypeName . <<
            /.$BeginJava
			r.rule_OperatorFunction8(TypeName);
        $EndJava./
                       | TypeName . >>
            /.$BeginJava
			r.rule_OperatorFunction9(TypeName);
        $EndJava./
                       | TypeName . >>>
            /.$BeginJava
			r.rule_OperatorFunction10(TypeName);
        $EndJava./
                       | TypeName . <
            /.$BeginJava
			r.rule_OperatorFunction11(TypeName);
        $EndJava./
                       | TypeName . <=
            /.$BeginJava
			r.rule_OperatorFunction12(TypeName);
        $EndJava./
                       | TypeName . >=
            /.$BeginJava
			r.rule_OperatorFunction13(TypeName);
        $EndJava./
                       | TypeName . >
            /.$BeginJava
			r.rule_OperatorFunction14(TypeName);
        $EndJava./
                       | TypeName . ==
            /.$BeginJava
			r.rule_OperatorFunction15(TypeName);
        $EndJava./
                       | TypeName . !=
            /.$BeginJava
			r.rule_OperatorFunction16(TypeName);
        $EndJava./

    Literal ::= IntegerLiteral$lit
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
              | UnsignedIntegerLiteral$lit
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
                  | Primary . class$c
        /.$BeginJava
			r.rule_FieldAccess6(Primary);
        $EndJava./
                  | super . class$c
        /.$BeginJava
			r.rule_FieldAccess7();
        $EndJava./
                  | ClassName . super$sup . class$c
        /.$BeginJava
			r.rule_FieldAccess8(ClassName);
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
                       | ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation6(ClassName,Identifier,TypeArgumentsopt,ArgumentListopt);
        $EndJava./
                       | Primary TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
			r.rule_MethodInvocation7(Primary,TypeArgumentsopt,ArgumentListopt);
        $EndJava./
        
    MethodSelection ::= MethodName .  ( FormalParameterListopt )
        /.$BeginJava
			r.rule_MethodSelection0(MethodName,FormalParameterListopt);
        $EndJava./
                       | Primary . Identifier .  ( FormalParameterListopt )
        /.$BeginJava
			r.rule_MethodSelection1(Primary,Identifier,FormalParameterListopt);
        $EndJava./
                       | super . Identifier .  ( FormalParameterListopt )
        /.$BeginJava
			r.rule_MethodSelection2(Identifier,FormalParameterListopt);
        $EndJava./
                       | ClassName . super$sup . Identifier .  ( FormalParameterListopt )
        /.$BeginJava
			r.rule_MethodSelection3(ClassName,Identifier,FormalParameterListopt);
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
                      | + UnaryExpressionNotPlusMinus
        /.$BeginJava
			r.rule_UnannotatedUnaryExpression2(UnaryExpressionNotPlusMinus);
        $EndJava./
                      | - UnaryExpressionNotPlusMinus
        /.$BeginJava
			r.rule_UnannotatedUnaryExpression3(UnaryExpressionNotPlusMinus);
        $EndJava./
                      | UnaryExpressionNotPlusMinus

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
                                  | ~ UnaryExpression
        /.$BeginJava
			r.rule_UnaryExpressionNotPlusMinus1(UnaryExpression);
        $EndJava./
                                  | ! UnaryExpression
        /.$BeginJava
			r.rule_UnaryExpressionNotPlusMinus2(UnaryExpression);
        $EndJava./
    
    RangeExpression ::= UnaryExpression
                      | RangeExpression$expr1 .. UnaryExpression$expr2
        /.$BeginJava
			r.rule_RangeExpression1(expr1,expr2);
        $EndJava./
    
    MultiplicativeExpression ::= RangeExpression
                               | MultiplicativeExpression * RangeExpression
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
    
    AdditiveExpression ::= MultiplicativeExpression
                         | AdditiveExpression + MultiplicativeExpression
        /.$BeginJava
			r.rule_AdditiveExpression1(AdditiveExpression,MultiplicativeExpression);
        $EndJava./
                         | AdditiveExpression - MultiplicativeExpression
        /.$BeginJava
			r.rule_AdditiveExpression2(AdditiveExpression,MultiplicativeExpression);
        $EndJava./
    
    ShiftExpression ::= AdditiveExpression
                      | ShiftExpression << AdditiveExpression
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
    
    RelationalExpression ::= ShiftExpression
                           | HasZeroConstraint
                           | SubtypeConstraint
                           | RelationalExpression < ShiftExpression
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
                           | RelationalExpression instanceof Type
        /.$BeginJava
			r.rule_RelationalExpression7(RelationalExpression,Type);
        $EndJava./
                           | RelationalExpression in ShiftExpression
        /.$BeginJava
			r.rule_RelationalExpression8(RelationalExpression,ShiftExpression);
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
    
    AndExpression ::= EqualityExpression
                    | AndExpression & EqualityExpression
        /.$BeginJava
			r.rule_AndExpression1(AndExpression,EqualityExpression);
        $EndJava./
    
    ExclusiveOrExpression ::= AndExpression
                            | ExclusiveOrExpression ^ AndExpression
        /.$BeginJava
			r.rule_ExclusiveOrExpression1(ExclusiveOrExpression,AndExpression);
        $EndJava./
    
    InclusiveOrExpression ::= ExclusiveOrExpression
                            | InclusiveOrExpression '|' ExclusiveOrExpression
        /.$BeginJava
			r.rule_InclusiveOrExpression1(InclusiveOrExpression,ExclusiveOrExpression);
        $EndJava./
    
    ConditionalAndExpression ::= InclusiveOrExpression
                               | ConditionalAndExpression && InclusiveOrExpression
        /.$BeginJava
			r.rule_ConditionalAndExpression1(ConditionalAndExpression,InclusiveOrExpression);
        $EndJava./
    
    ConditionalOrExpression ::= ConditionalAndExpression
                              | ConditionalOrExpression || ConditionalAndExpression
        /.$BeginJava
			r.rule_ConditionalOrExpression1(ConditionalOrExpression,ConditionalAndExpression);
        $EndJava./
    
    
    ConditionalExpression ::= ConditionalOrExpression
                            | ClosureExpression
                            | AtExpression
                            | FinishExpression
                            | ConditionalOrExpression ? Expression : ConditionalExpression
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
        
      | '..'
        /.$BeginJava
			r.rule_BinOp19();
        $EndJava./
      | '->'
        /.$BeginJava
			r.rule_BinOp20();
        $EndJava./
      | 'in'
        /.$BeginJava
			r.rule_BinOp21();
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

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    VariableModifiersopt ::= %Empty
--        /.$BeginJava
--                    setResult(Collections.<Node>emptyList());
--          $EndJava
--        ./
--                           | VariableModifiers

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    VariableInitializersopt ::= %Empty
--        /.$NullAction./
--                              | VariableInitializers

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

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    InterfaceModifiersopt ::= %Empty
--        /.$BeginJava
--                    setResult(Collections.<Node>emptyList());
--          $EndJava
--        ./
--                            | InterfaceModifiers

    ClassBodyopt ::= %Empty
        /.$NullAction./
                   | ClassBody

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    Argumentsopt ::= %Empty
--        /.$BeginJava
--                    setResult(new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false));
--          $EndJava
--        ./
--                   | Arguments

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

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    ConstructorModifiersopt ::= %Empty
--        /.$BeginJava
--                    setResult(Collections.<Node>emptyList());
--          $EndJava
--        ./
--                              | ConstructorModifiers

    FormalParameterListopt ::= %Empty
        /.$BeginJava
			r.rule_FormalParameterListopt0();
        $EndJava./
                             | FormalParameterList

--    Throwsopt ::= %Empty
--        /.$BeginJava
--                    setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
--          $EndJava
--        ./
--                | Throws
     Offersopt ::= %Empty
        /.$BeginJava
			r.rule_Offersopt0();
        $EndJava./
                | Offers

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    MethodModifiersopt ::= %Empty
--        /.$BeginJava
--                    setResult(Collections.<Node>emptyList());
--          $EndJava
--        ./
--                         | MethodModifiers

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    TypeModifieropt ::= %Empty
--        /.$BeginJava
--                    setResult(Collections.<Node>emptyList());
--          $EndJava
--        ./
--                         | TypeModifier

--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    FieldModifiersopt ::= %Empty
--        /.$BeginJava
--                    setResult(Collections.<Node>emptyList());
--          $EndJava
--        ./
--                        | FieldModifiers

    ClassBodyDeclarationsopt ::= %Empty
        /.$BeginJava
			r.rule_ClassBodyDeclarationsopt0();
        $EndJava./
                               | ClassBodyDeclarations

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
                            
--
-- This is a useless nonterminal that is not used anywhere else in the grammar.
--
--    ResultTypeopt ::= %Empty
--        /.$NullAction./
--                            | ResultType
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
%End

%Types
	Object ::= ExpressionStatement | ClosureExpression | PackageOrTypeName | Property | CastExpression | TypeParameter | FieldDeclarator | OperatorFunction | AmbiguousName | VariableDeclaratorWithType | Finally | AnnotationStatement | TypeDeclarations | IdentifierList | TypeImportOnDemandDeclaration | BreakStatement | PlaceExpressionSingleList | ConditionalOrExpression | LocalVariableDeclaration | InterfaceMemberDeclarationsopt | InterfaceTypeList | AtomicStatement | PackageName | RelationalExpression | BlockStatement | UnaryExpression | ExclusiveOrExpression | ClockedClauseopt | AdditiveExpression | AssignPropertyCall | MultiplicativeExpression | ClosureBody | TryStatement | FormalParameterList | UnannotatedUnaryExpression | SwitchBlock | VariableDeclarator | TypeParamWithVarianceList | NonExpressionStatement | UnaryExpressionNotPlusMinus | Interfacesopt | ConditionalExpression | SwitchLabel | MethodSuperPrefix | VariableDeclarators | BlockStatementsopt | BlockStatements | StatementExpression | Expression | TypeParameterList | TypeParamWithVariance | VariableDeclaratorsWithType | Block | ResultType | MethodSelection | ForUpdate | FunctionType | Conjunction | TypeParamsWithVariance | HasZeroConstraint | ExistentialListopt | Annotation | BinOp | EqualityExpression | Modifiersopt | PostfixExpression | BooleanLiteral | ArgumentList | FormalParametersopt | ExtendsInterfacesopt | LoopStatement | Primary | FormalDeclarators | InterfaceDeclaration | RangeExpression | SingleTypeImportDeclaration | DepNamedType | ImportDeclaration | ClassBodyDeclaration | InterfaceBody | WhereClauseopt | LabeledStatement | TypeArgumentList | NormalClassDeclaration | SimpleNamedType | PreIncrementExpression | LoopIndex | Arguments | Literal | PlaceExpression | TypeDeclaration | ArgumentListopt | TypeArguments | Superopt | ClassBodyDeclarationsopt | HasResultTypeopt | Statement | LeftHandSide | TypeName | Offers | Super | NormalInterfaceDeclaration | SwitchLabelsopt | Propertiesopt | MethodClassNameSuperPrefix | FieldAccess | MethodName | ForInit | OfferStatement | Expressionopt | ExplicitConstructorInvocationopt | AtEachStatement | Offersopt | TypeDeclarationsopt | ClassBodyDeclarations | WhereClause | InterfaceMemberDeclaration | PackageDeclaration | InterfaceMemberDeclarations | MethodInvocation | PreDecrementExpression | PrefixOp | ConstrainedType | VoidType | WhileStatement | Clock | Modifier | ExpressionName | TypeParamsWithVarianceopt | FormalParameterListopt | Conjunctionopt | ClassBody | ForStatement | Identifier | ClassName | AssignmentOperator | ForUpdateopt | AndExpression | FinishExpression | ReturnStatement | SubtypeConstraint | Catchesopt | MethodDeclaration | AssertStatement | DepParameters | DoStatement | PostDecrementExpression | AssignmentExpression | NamedType | ExplicitConstructorInvocation | FormalParameter | BasicForStatement | Properties | ClockList | SwitchStatement | LocalVariableDeclarationStatement | ThrowStatement | StatementExpressionList | ContinueStatement | SwitchBlockStatementGroups | TypeDefDeclaration | PropertyMethodDeclaration | ExtendsInterfaces | SwitchBlockStatementGroup | TypeParametersopt | ClassBodyopt | AtStatement | ConstructorBody | WhenStatement | AsyncStatement | MethodBody | FieldDeclaration | PackageDeclarationopt | VariableInitializer | ShiftExpression | Interfaces | ClassMemberDeclaration | IfThenStatement | StructDeclaration | ConstructorBlock | InclusiveOrExpression | FieldKeyword | HasResultType | PropertyList | ConditionalAndExpression | SwitchLabels | ImportDeclarationsopt | IfThenElseStatement | Identifieropt | AnnotatedType | MethodPrimaryPrefix | ConstructorDeclaration | PostIncrementExpression | ResumeStatement | Catches | SwitchBlockStatementGroupsopt | FieldDeclarators | CatchClause | ConstantExpression | FormalParameters | ClassInstanceCreationExpression | NextStatement | AtExpression | Type | CompilationUnit | Assignment | MethodModifiersopt | LastExpression | VarKeyword | TypeArgumentsopt | Annotationsopt | LoopIndexDeclarator | FinishStatement | Annotations | ImportDeclarations | TypeParameters | EnhancedForStatement | EmptyStatement | ClassType | FormalDeclarator | ExistentialList | ForInitopt | ClockedClause | ClassDeclaration
%End
