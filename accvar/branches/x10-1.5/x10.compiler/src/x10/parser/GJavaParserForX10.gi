%options fp=JavaParser
%options package=javaparser
%options template=btParserTemplateF.gi
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
    EQUAL ::= =  
    EQUAL_EQUAL ::= ==  
    GREATER ::= >
    ELLIPSIS ::= ...
%End

%Start
    CompilationUnit
%End

%Rules
    identifier ::= IDENTIFIER$ident
        /.$BeginJava
                    ident.setKind($sym_type.TK_IDENTIFIER);
                    setResult(id(getRhsFirstTokenIndex($ident)));
          $EndJava
        ./

    -- Chapter 4

--
-- See X10 definition
--
--    Type ::= PrimitiveType
--           | ReferenceType

--   PrimitiveType ::= NumericType
--                    | boolean
--        /.$BeginJava
--                    setResult(nf.CanonicalTypeNode(pos(), ts.Boolean()));
--          $EndJava
--        ./

    NumericType ::= IntegralType
                  | FloatingPointType

    IntegralType ::= byte
        /.$BeginJava
                    setResult(nf.CanonicalTypeNode(pos(), ts.Byte()));
          $EndJava
        ./
                   | char
        /.$BeginJava
                    setResult(nf.CanonicalTypeNode(pos(), ts.Char()));
          $EndJava
        ./
                   | short
        /.$BeginJava
                    setResult(nf.CanonicalTypeNode(pos(), ts.Short()));
          $EndJava
        ./
                   | int
        /.$BeginJava
                    setResult(nf.CanonicalTypeNode(pos(), ts.Int()));
          $EndJava
        ./
                   | long
        /.$BeginJava
                    setResult(nf.CanonicalTypeNode(pos(), ts.Long()));
          $EndJava
        ./

    FloatingPointType ::= float
        /.$BeginJava
                    setResult(nf.CanonicalTypeNode(pos(), ts.Float()));
          $EndJava
        ./
                        | double
        /.$BeginJava
                    setResult(nf.CanonicalTypeNode(pos(), ts.Double()));
          $EndJava
        ./

    ReferenceType ::= ClassOrInterfaceType
                    | ArrayType

--
-- PC: This rules is subsumed by the rule:
--
--    ClassOrInterfaceType ::= TypeName DepParametersopt
--
--    ClassOrInterfaceType ::= ClassType
--        /.$NoAction./
    --
    -- Remove an obvious conflict.
    --
    --                       | InterfaceType
    --
    -- ClassType ::= TypeName TypeArgumentsopt

    InterfaceType ::= TypeName TypeArgumentsopt

    TypeName ::= identifier
        /.$BeginJava
                    setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
          $EndJava
        ./
               | TypeName . identifier
        /.$BeginJava
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      TypeName,
                                      nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
          $EndJava
        ./

    ClassName ::= TypeName

    TypeVariable ::= identifier

    ArrayType ::= Type '[' Annotationsopt ']'
        /.$BeginJava
                    setResult(nf.array(Type, pos(), 1));
          $EndJava
        ./

    TypeParameter ::= TypeVariable TypeBoundopt
        /.$BadAction./

    TypeBound ::= extends ClassOrInterfaceType AdditionalBoundListopt
        /.$BadAction./

    AdditionalBoundList ::= AdditionalBound
        /.$BadAction./
                          | AdditionalBoundList AdditionalBound
        /.$BadAction./

    AdditionalBound ::= & InterfaceType
        /.$BadAction./

    TypeArguments ::= < ActualTypeArgumentList >
        /.$BadAction./

    ActualTypeArgumentList ::= ActualTypeArgument
        /.$BadAction./
                             | ActualTypeArgumentList , ActualTypeArgument
        /.$BadAction./

--
-- See X10 definition
--
--    ActualTypeArgument ::= ReferenceType
--                         | Wildcard

    Wildcard ::= ? WildcardBoundsOpt
        /.$BadAction./

    WildcardBounds ::= extends ReferenceType
        /.$BadAction./
                     | super ReferenceType
        /.$BadAction./

    -- Chapter 5

    -- Chapter 6

    PackageName ::= identifier
        /.$BeginJava
                    setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
          $EndJava
        ./
                  | PackageName . identifier
        /.$BeginJava
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      PackageName,
                                      nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
          $EndJava
        ./

    --
    -- See Chapter 4
    --
    -- TypeName ::= identifier
    --           | PackageOrTypeName . identifier
    --
    ExpressionName ::=? identifier
        /.$BeginJava
                    setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
          $EndJava
        ./
                     | AmbiguousName . identifier
        /.$BeginJava
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
          $EndJava
        ./

    MethodName ::=? identifier
        /.$BeginJava
                    setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
          $EndJava
        ./
                 | AmbiguousName . identifier
        /.$BeginJava
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
          $EndJava
        ./

    PackageOrTypeName ::= identifier
        /.$BeginJava
                    setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
          $EndJava
        ./
                        | PackageOrTypeName . identifier
        /.$BeginJava
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      PackageOrTypeName,
                                      nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
          $EndJava
        ./

    AmbiguousName ::=? identifier
        /.$BeginJava
                    setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
          $EndJava
        ./
                    | AmbiguousName . identifier
        /.$BeginJava
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
         $EndJava
        ./

    -- Chapter 7

    CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
        /.$BeginJava
                    // Add import x10.lang.* by default.
                    int token_pos = (ImportDeclarationsopt.size() == 0
                                         ? TypeDeclarationsopt.size() == 0
                                               ? prsStream.getSize() - 1
                                               : prsStream.getPrevious(getRhsFirstTokenIndex($TypeDeclarationsopt))
                                     : getRhsLastTokenIndex($ImportDeclarationsopt)
                                );
                    Import x10LangImport = 
                    nf.Import(pos(token_pos), Import.PACKAGE, "x10.lang");
                    ImportDeclarationsopt.add(x10LangImport);
                    setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()), PackageDeclarationopt, ImportDeclarationsopt, TypeDeclarationsopt));
          $EndJava
        ./

    ImportDeclarations ::= ImportDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Import.class, false);
                    l.add(ImportDeclaration);
                    setResult(l);
          $EndJava
        ./
                         | ImportDeclarations ImportDeclaration
        /.$BeginJava
                    if (ImportDeclaration != null)
                        ImportDeclarations.add(ImportDeclaration);
                    //setResult(l);
          $EndJava
        ./

    TypeDeclarations ::= TypeDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                    if (TypeDeclaration != null)
                        l.add(TypeDeclaration);
                    setResult(l);
          $EndJava
        ./
                       | TypeDeclarations TypeDeclaration
        /.$BeginJava
                    if (TypeDeclaration != null)
                        TypeDeclarations.add(TypeDeclaration);
                    //setResult(l);
          $EndJava
        ./

    PackageDeclaration ::= Annotationsopt package PackageName ;

    ImportDeclaration ::= SingleTypeImportDeclaration
                        | TypeImportOnDemandDeclaration
                        | SingleStaticImportDeclaration
                        | StaticImportOnDemandDeclaration

    SingleTypeImportDeclaration ::= import TypeName ;
        /.$BeginJava
                    setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, TypeName.toString()));
          $EndJava
        ./

    TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
        /.$BeginJava
                    setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, PackageOrTypeName.toString()));
          $EndJava
        ./
    
    SingleStaticImportDeclaration ::= import static TypeName . identifier ;
        /.$BadAction./

    StaticImportOnDemandDeclaration ::= import static TypeName . * ;
        /.$BadAction./

    TypeDeclaration ::= ClassDeclaration
                      | InterfaceDeclaration
                      | ;
        /.$BeginJava
                    setResult(null);
          $EndJava
        ./

    -- Chapter 8

    ClassDeclaration ::= NormalClassDeclaration
                       | EnumDeclaration

    NormalClassDeclaration ::= ClassModifiersopt class identifier TypeParametersopt Superopt Interfacesopt ClassBody

    ClassModifiers ::= ClassModifier
        /.$BeginJava
                    List l = new LinkedList();
                    l.addAll(ClassModifier);
                    setResult(l);
          $EndJava
        ./
                     | ClassModifiers ClassModifier
        /.$BeginJava
                    ClassModifiers.addAll(ClassModifier);
          $EndJava
        ./

    ClassModifier ::= Annotation
        /.$BeginJava
                    setResult(Collections.singletonList(Annotation));
          $EndJava
        ./
                    | public
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PUBLIC));
          $EndJava
        ./
                    | protected
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PROTECTED));
          $EndJava
        ./
                    | private
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PRIVATE));
          $EndJava
        ./
                    | abstract
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.ABSTRACT));
          $EndJava
        ./
                    | static
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.STATIC));
          $EndJava
        ./
                    | final
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.FINAL));
          $EndJava
        ./
                    | strictfp
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.STRICTFP));
          $EndJava
        ./

    TypeParameters ::= < TypeParameterList >
        /.$BadAction./
    
    TypeParameterList ::= TypeParameter
        /.$BadAction./
                        | TypeParameterList , TypeParameter
        /.$BadAction./

     Super ::= extends ClassType
        /.$BeginJava
                    setResult(ClassType);
          $EndJava
        ./

    --
    -- See Chapter 4
    --
    --ClassType ::= TypeName TypeArgumentsopt
    --
    Interfaces ::= implements InterfaceTypeList
        /.$BeginJava
                    setResult(InterfaceTypeList);
          $EndJava
        ./

    InterfaceTypeList ::= InterfaceType
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), TypeNode.class, false);
                    l.add(InterfaceType);
                    setResult(l);
          $EndJava
        ./
                        | InterfaceTypeList , InterfaceType
        /.$BeginJava
                    InterfaceTypeList.add(InterfaceType);
                    setResult(InterfaceTypeList);
          $EndJava
        ./

    --
    -- See Chapter 4
    --
    --InterfaceType ::= TypeName TypeArgumentsopt
    --
    ClassBody ::= { ClassBodyDeclarationsopt }
        /.$BeginJava
                    setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
          $EndJava
        ./

    ClassBodyDeclarations ::= ClassBodyDeclaration
                            | ClassBodyDeclarations ClassBodyDeclaration
        /.$BeginJava
                    ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                    // setResult(a);
          $EndJava
        ./

    ClassBodyDeclaration ::= ClassMemberDeclaration
                           | InstanceInitializer
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(nf.Initializer(pos(), Flags.NONE, InstanceInitializer));
                    setResult(l);
          $EndJava
        ./
                           | StaticInitializer
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(nf.Initializer(pos(), Flags.STATIC, StaticInitializer));
                    setResult(l);
          $EndJava
        ./
                           | ConstructorDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(ConstructorDeclaration);
                    setResult(l);
          $EndJava
        ./

    ClassMemberDeclaration ::= FieldDeclaration
                             | MethodDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(MethodDeclaration);
                    setResult(l);
          $EndJava
        ./
                             | ClassDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(ClassDeclaration);
                    setResult(l);
          $EndJava
        ./
                             | InterfaceDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(InterfaceDeclaration);
                    setResult(l);
          $EndJava
        ./
                             | ;
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    setResult(l);
          $EndJava
        ./
    
    FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators ;

    
    VariableDeclarators ::= VariableDeclarator
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), X10VarDeclarator.class, false);
                    l.add(VariableDeclarator);
                    setResult(l);
          $EndJava
        ./
                          | VariableDeclarators , VariableDeclarator
        /.$BeginJava
                    VariableDeclarators.add(VariableDeclarator);
                    // setResult(VariableDeclarators);
          $EndJava
        ./
    
    VariableDeclarator ::= TraditionalVariableDeclaratorId
                         | VariableDeclaratorId = VariableInitializer
        /.$BeginJava
                    VariableDeclaratorId.init = VariableInitializer;
                    VariableDeclaratorId.position(pos(((JPGPosition) VariableDeclaratorId.pos), ((JPGPosition) VariableInitializer.position())));
                    // setResult(VariableDeclaratorId);
          $EndJava
        ./
    
    TraditionalVariableDeclaratorId ::= identifier
        /.$BeginJava
                    setResult(new X10VarDeclarator(pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
          $EndJava
        ./
                           | TraditionalVariableDeclaratorId '[' ']'
        /.$BeginJava
                    TraditionalVariableDeclaratorId.dims++;
                    TraditionalVariableDeclaratorId.position(pos());
                    // setResult(a);
          $EndJava
        ./

    VariableDeclaratorId ::= TraditionalVariableDeclaratorId
                           | identifier '[' IdentifierList ']'
        /.$BeginJava
                    setResult(new X10VarDeclarator(pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier()), IdentifierList));
          $EndJava
        ./
                           | '[' IdentifierList ']'
        /.$BeginJava
                    setResult(new X10VarDeclarator(pos(), IdentifierList));
          $EndJava
        ./
    
    VariableInitializer ::= Expression
                          | ArrayInitializer
    
    FieldModifiers ::= FieldModifier
        /.$BeginJava
                    List l = new LinkedList();
                    l.addAll(FieldModifier);
                    setResult(l);
          $EndJava
        ./
                     | FieldModifiers FieldModifier
        /.$BeginJava
                    FieldModifiers.addAll(FieldModifier);
          $EndJava
        ./
    
    FieldModifier ::= Annotation
        /.$BeginJava
                    setResult(Collections.singletonList(Annotation));
          $EndJava
        ./
                    | public
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PUBLIC));
          $EndJava
        ./
                    | protected
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PROTECTED));
          $EndJava
        ./
                    | private
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PRIVATE));
          $EndJava
        ./
                    | static
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.STATIC));
          $EndJava
        ./
                    | final
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.FINAL));
          $EndJava
        ./
                    | transient
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.TRANSIENT));
          $EndJava
        ./
                    | volatile
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.VOLATILE));
          $EndJava
        ./
    
    MethodDeclaration ::= MethodHeader MethodBody
      
    
    MethodHeader ::= MethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt
    
    ResultType ::= Type
                 | void
        /.$BeginJava
                    setResult(nf.CanonicalTypeNode(pos(), ts.Void()));
          $EndJava
        ./
    
    -- MethodDeclarator ::= identifier ( FormalParameterListopt )
    --    /.$BeginJava
    --                 Object[] a = new Object[3];
    --               a[0] = new Name(nf, ts, pos(), nf.Id(pos(getLeftSpan()), identifier.getIdentifier()));
    --                 a[1] = FormalParameterListopt;
    --                a[2] = new Integer(0);
    --                 setResult(a);
    --      $EndJava
    --     ./
    --                  | MethodDeclarator [ ]
     --    /.$BeginJava
      --              MethodDeclarator[2] = new Integer(((Integer) MethodDeclarator[2]).intValue() + 1);
      --               // setResult(MethodDeclarator);
     --     $EndJava
     --    ./
    
    FormalParameterList ::= LastFormalParameter
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Formal.class, false);
                    l.add(LastFormalParameter);
                    setResult(l);
          $EndJava
        ./
                          | FormalParameters , LastFormalParameter
        /.$BeginJava
                    FormalParameters.add(LastFormalParameter);
                    // setResult(FormalParameters);
          $EndJava
        ./
    
    FormalParameters ::= FormalParameter
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Formal.class, false);
                    l.add(FormalParameter);
                    setResult(l);
          $EndJava
        ./
                       | FormalParameters , FormalParameter
        /.$BeginJava
                    FormalParameters.add(FormalParameter);
                    // setResult(FormalParameters);
          $EndJava
        ./
    
    FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
        /.$BeginJava
                Formal f;
	        if (VariableDeclaratorId != null)
                    f = nf.X10Formal(pos(), extractFlags(VariableModifiersopt), nf.array(Type, pos(getRhsFirstTokenIndex($Type), getRhsLastTokenIndex($Type)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names());
                else
                    f = nf.Formal(pos(), extractFlags(VariableModifiersopt), nf.array(Type, pos(getRhsFirstTokenIndex($Type), getRhsLastTokenIndex($Type)), 1), nf.Id(pos(), ""));
                f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
                setResult(f);
          $EndJava
        ./
    
    VariableModifiers ::= VariableModifier
        /.$BeginJava
                    List l = new LinkedList();
                    l.addAll(VariableModifier);
                    setResult(l);
          $EndJava
        ./
                        | VariableModifiers VariableModifier
        /.$BeginJava
                    VariableModifiers.addAll(VariableModifier);
          $EndJava
        ./
    
    VariableModifier ::= final
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.FINAL));
          $EndJava
        ./
                       | Annotation
        /.$BeginJava
                    setResult(Collections.singletonList(Annotation));
          $EndJava
        ./
    
    LastFormalParameter ::= VariableModifiersopt Type ...opt$opt VariableDeclaratorId
        /.$BeginJava
                    assert(opt == null);
                    Formal f = nf.X10Formal(pos(), extractFlags(VariableModifiersopt), nf.array(Type, pos(getRhsFirstTokenIndex($Type), getRhsLastTokenIndex($Type)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names());
                    f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
                    setResult(f);
          $EndJava
        ./

    --
    -- See above
    --
    --VariableDeclaratorId ::= identifier
    --                       | VariableDeclaratorId [ ]
    --    
    MethodModifiers ::= MethodModifier
        /.$BeginJava
                    List l = new LinkedList();
                    l.addAll(MethodModifier);
                    setResult(l);
          $EndJava
        ./
                      | MethodModifiers MethodModifier
        /.$BeginJava
                    MethodModifiers.addAll(MethodModifier);
          $EndJava
        ./
    
    MethodModifier ::= Annotation
        /.$BeginJava
                    setResult(Collections.singletonList(Annotation));
          $EndJava
        ./
                     | public
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PUBLIC));
          $EndJava
        ./
                     | protected
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PROTECTED));
          $EndJava
        ./
                     | private
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PRIVATE));
          $EndJava
        ./
                     | abstract
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.ABSTRACT));
          $EndJava
        ./
                     | static
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.STATIC));
          $EndJava
        ./
                     | final
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.FINAL));
          $EndJava
        ./
                     | synchronized
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.SYNCHRONIZED));
          $EndJava
        ./
                     | native
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.NATIVE));
          $EndJava
        ./
                     | strictfp
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.STRICTFP));
          $EndJava
        ./
    
    Throws ::= throws ExceptionTypeList
        /.$BeginJava
                    setResult(ExceptionTypeList);
          $EndJava
        ./
    
    ExceptionTypeList ::= ExceptionType
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), TypeNode.class, false);
                    l.add(ExceptionType);
                    setResult(l);
          $EndJava
        ./
                        | ExceptionTypeList , ExceptionType
        /.$BeginJava
                    ExceptionTypeList.add(ExceptionType);
                    // setResult(ExceptionTypeList);
          $EndJava
        ./
    
    ExceptionType ::= ClassType
--
--pc
--
-- TypeVariable is subsumed by ClassType
--
--                    | TypeVariable
--        /.$NoAction./
    
    MethodBody ::= Block
                 | ;
        /.$NullAction./
    
    InstanceInitializer ::= Block
    
    StaticInitializer ::= static Block
        /.$BeginJava
                    setResult(Block);
          $EndJava
        ./
    
  -- ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
   --    /.$BeginJava
   --                 Name a = (Name) ConstructorDeclarator[1];
   --                 List b = (List) ConstructorDeclarator[2];
--
   --                setResult(nf.ConstructorDecl(pos(), extractFlags(ConstructorModifiersopt), a.toString(), b, Throwsopt, ConstructorBody));
    --     $EndJava
    --   ./
    
--   ConstructorDeclarator ::= TypeParametersopt SimpleTypeName ( FormalParameterListopt )
    
    SimpleTypeName ::= identifier
        /.$BeginJava
                    setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
          $EndJava
        ./

    ConstructorModifiers ::= ConstructorModifier
        /.$BeginJava
                    List l = new LinkedList();
                    l.addAll(ConstructorModifier);
                    setResult(l);
          $EndJava
        ./
                           | ConstructorModifiers ConstructorModifier
        /.$BeginJava
                    ConstructorModifiers.addAll(ConstructorModifier);
          $EndJava
        ./
    
    ConstructorModifier ::= Annotation
        /.$BeginJava
                    setResult(Collections.singletonList(Annotation));
          $EndJava
        ./
                          | public
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PUBLIC));
          $EndJava
        ./
                          | protected
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PROTECTED));
          $EndJava
        ./
                          | private
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PRIVATE));
          $EndJava
        ./
    
    ConstructorBody ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
        /.$BeginJava
                    List l;
                    l = new TypedList(new LinkedList(), Stmt.class, false);
                    if (ExplicitConstructorInvocationopt == null)
                    {
                        l.add(nf.SuperCall(pos(), Collections.EMPTY_LIST));
                    }
                    else
                    {
                        l.add(ExplicitConstructorInvocationopt);
                    }
                    l.addAll(BlockStatementsopt);
                    setResult(nf.Block(pos(), l));
          $EndJava
        ./
    
    ExplicitConstructorInvocation ::= TypeArgumentsopt this ( ArgumentListopt ) ;
                                    | TypeArgumentsopt super ( ArgumentListopt ) ;
                                    | Primary . TypeArgumentsopt this ( ArgumentListopt ) ;
                                    | Primary . TypeArgumentsopt super ( ArgumentListopt ) ;
    
    EnumDeclaration ::= ClassModifiersopt enum identifier Interfacesopt EnumBody
        /.$BadAction./
    
    EnumBody ::= { EnumConstantsopt ,opt$opt EnumBodyDeclarationsopt }
        /.$BadAction./
    
    EnumConstants ::= EnumConstant
        /.$BadAction./
                    | EnumConstants , EnumConstant
        /.$BadAction./
    
    EnumConstant ::= Annotationsopt identifier Argumentsopt ClassBodyopt
    
    Arguments ::= ( ArgumentListopt )
        /.$BeginJava
                    setResult(ArgumentListopt);
          $EndJava
        ./
    
    EnumBodyDeclarations ::= ; ClassBodyDeclarationsopt
        /.$BadAction./
    
    -- chapter 9
    
    InterfaceDeclaration ::= NormalInterfaceDeclaration
                           | AnnotationTypeDeclaration
        /.$BadAction./
    
    NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier TypeParametersopt ExtendsInterfacesopt InterfaceBody
    
    InterfaceModifiers ::= InterfaceModifier
        /.$BeginJava
                    List l = new LinkedList();
                    l.addAll(InterfaceModifier);
                    setResult(l);
          $EndJava
        ./
                         | InterfaceModifiers InterfaceModifier
        /.$BeginJava
                    InterfaceModifiers.addAll(InterfaceModifier);
          $EndJava
        ./
    
    InterfaceModifier ::= Annotation
        /.$BeginJava
                    setResult(Collections.singletonList(Annotation));
          $EndJava
        ./
                        | public
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PUBLIC));
          $EndJava
        ./
                        | protected
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PROTECTED));
          $EndJava
        ./
                        | private
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PRIVATE));
          $EndJava
        ./
                        | abstract
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.ABSTRACT));
          $EndJava
        ./
                        | static
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.STATIC));
          $EndJava
        ./
                        | strictfp
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.STRICTFP));
          $EndJava
        ./
    
    ExtendsInterfaces ::= extends InterfaceType
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), TypeNode.class, false);
                    l.add(InterfaceType);
                    setResult(l);
          $EndJava
        ./
                        | ExtendsInterfaces , InterfaceType
        /.$BeginJava
                    ExtendsInterfaces.add(InterfaceType);
                    // setResult(ExtendsInterfaces);
          $EndJava
        ./
    
    --
    -- See Chapter 4
    --
    --InterfaceType ::= TypeName TypeArgumentsOpt
    
    InterfaceBody ::= { InterfaceMemberDeclarationsopt }
        /.$BeginJava
                    setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
          $EndJava
        ./
    
    InterfaceMemberDeclarations ::= InterfaceMemberDeclaration
                                  | InterfaceMemberDeclarations InterfaceMemberDeclaration
        /.$BeginJava
                    InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                    // setResult(l);
          $EndJava
        ./
    
    InterfaceMemberDeclaration ::= ConstantDeclaration
                                 | AbstractMethodDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(AbstractMethodDeclaration);
                    setResult(l);
          $EndJava
        ./
                                 | ClassDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(ClassDeclaration);
                    setResult(l);
          $EndJava
        ./
                                 | InterfaceDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(InterfaceDeclaration);
                    setResult(l);
          $EndJava
        ./
                                 | ;
        /.$BeginJava
                    setResult(Collections.EMPTY_LIST);
          $EndJava
        ./
    
    ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    for (Iterator i = VariableDeclarators.iterator(); i.hasNext();)
                    {
                        X10VarDeclarator d = (X10VarDeclarator) i.next();
                        if (d.hasExplodedVars())
                          // TODO: Report this exception correctly.
                          throw new Error("Field Declarations may not have exploded variables." + pos());
                        FieldDecl fd = nf.FieldDecl(pos(getRhsFirstTokenIndex($Type), getRightSpan()),
                                           extractFlags(ConstantModifiersopt),
                                           nf.array(Type, pos(getRhsFirstTokenIndex($Type), getRhsLastTokenIndex($Type)), d.dims),
                                           d.name,
                                           d.init);
                        fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(ConstantModifiersopt));
                        l.add(fd);
                    }
                    setResult(l);
          $EndJava
        ./
    
    ConstantModifiers ::= ConstantModifier
        /.$BeginJava
                    List l = new LinkedList();
                    l.addAll(ConstantModifier);
                    setResult(l);
          $EndJava
        ./
                        | ConstantModifiers ConstantModifier
        /.$BeginJava
                    ConstantModifiers.addAll(ConstantModifier);
          $EndJava
        ./
    
    ConstantModifier ::= Annotation
        /.$BeginJava
                    setResult(Collections.singletonList(Annotation));
          $EndJava
        ./
                       | public
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PUBLIC));
          $EndJava
        ./
                       | static
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.STATIC));
          $EndJava
        ./
                       | final
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.FINAL));
          $EndJava
        ./
    
    AbstractMethodDeclaration ::= AbstractMethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt ;
    
    AbstractMethodModifiers ::= AbstractMethodModifier
        /.$BeginJava
                    List l = new LinkedList();
                    l.addAll(AbstractMethodModifier);
                    setResult(l);
          $EndJava
        ./
                              | AbstractMethodModifiers AbstractMethodModifier
        /.$BeginJava
                    AbstractMethodModifiers.addAll(AbstractMethodModifier);
          $EndJava
        ./
    
    AbstractMethodModifier ::= Annotation
        /.$BeginJava
                    setResult(Collections.singletonList(Annotation));
          $EndJava
        ./
                       | public
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.PUBLIC));
          $EndJava
        ./
                       | abstract
        /.$BeginJava
                    setResult(Collections.singletonList(Flags.ABSTRACT));
          $EndJava
        ./
    
    AnnotationTypeDeclaration ::= InterfaceModifiersopt @ interface identifier AnnotationTypeBody
        /.$BadAction./
    
    AnnotationTypeBody ::= { AnnotationTypeElementDeclarationsopt }
        /.$BadAction./
    
    AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclaration
        /.$BadAction./
                                        | AnnotationTypeElementDeclarations AnnotationTypeElementDeclaration
        /.$BadAction./
    
    AnnotationTypeElementDeclaration ::= AbstractMethodModifiersopt Type identifier ( ) DefaultValueopt ;
        /.$BadAction./
                                       | ConstantDeclaration
        /.$BadAction./
                                       | ClassDeclaration
        /.$BadAction./
                                       | InterfaceDeclaration
        /.$BadAction./
                                       | EnumDeclaration
        /.$BadAction./
                                       | AnnotationTypeDeclaration
        /.$BadAction./
                                       | ;
        /.$BadAction./
    
    DefaultValue ::= default ElementValue
        /.$BadAction./
    
    Annotations ::= Annotation
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                    l.add(Annotation);
                    setResult(l);
          $EndJava
        ./
                  | Annotations Annotation
        /.$BeginJava
                    Annotations.add(Annotation);
          $EndJava
        ./
    
    Annotation ::= @ InterfaceType
        /.$BeginJava
                    setResult(nf.AnnotationNode(pos(), InterfaceType));
          $EndJava
        ./
    
    NormalAnnotation ::= @ InterfaceType
        /.$BadAction./
    
    ElementValuePairs ::= ElementValuePair
        /.$BadAction./
                        | ElementValuePairs , ElementValuePair
        /.$BadAction./
    
    ElementValuePair ::= SimpleName = ElementValue
        /.$BadAction./
    
    SimpleName ::= identifier
        /.$BeginJava
                    setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
          $EndJava
        ./

    ElementValue ::= ConditionalExpression
        /.$BadAction./
                   | Annotation
        /.$BadAction./
                   | ElementValueArrayInitializer
        /.$BadAction./
    
    ElementValueArrayInitializer ::= { ElementValuesopt ,opt$opt }
        /.$BadAction./
    
    ElementValues ::= ElementValue
        /.$BadAction./
                    | ElementValues , ElementValue
        /.$BadAction./
    
    MarkerAnnotation ::= @ TypeName
        /.$BadAction./
    
    SingleElementAnnotation ::= @ TypeName ( ElementValue )
        /.$BadAction./
    
    -- Chapter 10
    
    ArrayInitializer ::= { VariableInitializersopt ,opt$opt }
        /.$BeginJava
                    if (VariableInitializersopt == null)
                         setResult(nf.ArrayInit(pos()));
                    else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
          $EndJava
        ./
    
    VariableInitializers ::= VariableInitializer
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Expr.class, false);
                    l.add(VariableInitializer);
                    setResult(l);
          $EndJava
        ./
                           | VariableInitializers , VariableInitializer
        /.$BeginJava
                    VariableInitializers.add(VariableInitializer);
                    //setResult(VariableInitializers);
          $EndJava
        ./
    
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
        /.$BeginJava
                    setResult(nf.Block(pos(), BlockStatementsopt));
          $EndJava
        ./
    
    BlockStatements ::= BlockStatement
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Stmt.class, false);
                    l.addAll(BlockStatement);
                    setResult(l);
          $EndJava
        ./
                      | BlockStatements BlockStatement
        /.$BeginJava
                    BlockStatements.addAll(BlockStatement);
                    //setResult(l);
          $EndJava
        ./
    
    BlockStatement ::= LocalVariableDeclarationStatement
                     | ClassDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Stmt.class, false);
                    l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                    setResult(l);
          $EndJava
        ./
                     | Statement
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Stmt.class, false);
                    l.add(Statement);
                    setResult(l);
          $EndJava
        ./
    
    LocalVariableDeclarationStatement ::= LocalVariableDeclaration ;
    
    LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                    List s = new TypedList(new LinkedList(), Stmt.class, false);
                    if (VariableDeclarators != null) {
                        for (Iterator i = VariableDeclarators.iterator(); i.hasNext(); )
                        {
                            X10VarDeclarator d = (X10VarDeclarator) i.next();
                            d.setFlag(extractFlags(VariableModifiersopt)); 
                            // use d.flags below and not flags, setFlag may change it.
                            LocalDecl ld = nf.LocalDecl(d.pos, d.flags,
                                               nf.array(Type, pos(d), d.dims), d.name, d.init);
                            ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(VariableModifiersopt));
                            l.add(ld);
                            // [IP] TODO: Add X10Local with exploded variables
                            if (d.hasExplodedVars())
                               s.addAll(X10Formal_c.explode(nf, ts, d.name, pos(d), d.flags, d.names()));
                        }
                    }
                    l.addAll(s); 
                    setResult(l);
          $EndJava
        ./
    
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
        /.$BeginJava
                    setResult(nf.If(pos(), Expression, Statement));
          $EndJava
        ./
    
    IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
        /.$BeginJava
                    setResult(nf.If(pos(), Expression, StatementNoShortIf, Statement));
          $EndJava
        ./
    
    IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf$true_stmt else StatementNoShortIf$false_stmt
        /.$BeginJava
                    setResult(nf.If(pos(), Expression, true_stmt, false_stmt));
          $EndJava
        ./
    
    EmptyStatement ::= ;
        /.$BeginJava
                    setResult(nf.Empty(pos()));
          $EndJava
        ./
    
    LabeledStatement ::= identifier : Statement
        /.$BeginJava
                    setResult(nf.Labeled(pos(), nf.Id(pos(getLeftSpan()), identifier.getIdentifier()), Statement));
          $EndJava
        ./
    
    LabeledStatementNoShortIf ::= identifier : StatementNoShortIf
        /.$BeginJava
                    setResult(nf.Labeled(pos(), nf.Id(pos(getLeftSpan()), identifier.getIdentifier()), StatementNoShortIf));
          $EndJava
        ./
    ExpressionStatement ::= StatementExpression ;
        /.$BeginJava
                    setResult(nf.Eval(pos(), StatementExpression));
          $EndJava
        ./
    
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
        /.$BeginJava
                    setResult(nf.Assert(pos(), Expression));
          $EndJava
        ./
                      | assert Expression$expr1 : Expression$expr2 ;
        /.$BeginJava
                    setResult(nf.Assert(pos(), expr1, expr2));
          $EndJava
        ./
    
    SwitchStatement ::= switch ( Expression ) SwitchBlock
        /.$BeginJava
                    setResult(nf.Switch(pos(), Expression, SwitchBlock));
          $EndJava
        ./
    
    SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
        /.$BeginJava
                    SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                    setResult(SwitchBlockStatementGroupsopt);
          $EndJava
        ./
    
    SwitchBlockStatementGroups ::= SwitchBlockStatementGroup
                                 | SwitchBlockStatementGroups SwitchBlockStatementGroup
        /.$BeginJava
                    SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                    // setResult(SwitchBlockStatementGroups);
          $EndJava
        ./
    
    SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                    l.addAll(SwitchLabels);
                    l.add(nf.SwitchBlock(pos(), BlockStatements));
                    setResult(l);
          $EndJava
        ./
    
    SwitchLabels ::= SwitchLabel
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Case.class, false);
                    l.add(SwitchLabel);
                    setResult(l);
          $EndJava
        ./
                   | SwitchLabels SwitchLabel
        /.$BeginJava
                    SwitchLabels.add(SwitchLabel);
                    //setResult(SwitchLabels);
          $EndJava
        ./
    
    SwitchLabel ::= case ConstantExpression :
        /.$BeginJava
                    setResult(nf.Case(pos(), ConstantExpression));
          $EndJava
        ./
                  | case EnumConstant :
        /.$BadAction./
                  | default :
        /.$BeginJava
                    setResult(nf.Default(pos()));
          $EndJava
        ./

    EnumConstant ::= identifier
        /.$BadAction./
    
    WhileStatement ::= while ( Expression ) Statement
        /.$BeginJava
                    setResult(nf.While(pos(), Expression, Statement));
          $EndJava
        ./
    
    WhileStatementNoShortIf ::= while ( Expression ) StatementNoShortIf
        /.$BeginJava
                    setResult(nf.While(pos(), Expression, StatementNoShortIf));
          $EndJava
        ./
    
    DoStatement ::= do Statement while ( Expression ) ;
        /.$BeginJava
                    setResult(nf.Do(pos(), Statement, Expression));
          $EndJava
        ./
    
    ForStatement ::= BasicForStatement
                   | EnhancedForStatement
    
    BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
        /.$BeginJava
                    setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
          $EndJava
        ./
    
    ForStatementNoShortIf ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) StatementNoShortIf
        /.$BeginJava
                    setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, StatementNoShortIf));
          $EndJava
        ./
    
    ForInit ::= StatementExpressionList
              | LocalVariableDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ForInit.class, false);
                    l.addAll(LocalVariableDeclaration);
                    //setResult(l);
          $EndJava
        ./
    
    ForUpdate ::= StatementExpressionList
    
    StatementExpressionList ::= StatementExpression
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Eval.class, false);
                    l.add(nf.Eval(pos(), StatementExpression));
                    setResult(l);
          $EndJava
        ./
                              | StatementExpressionList , StatementExpression
        /.$BeginJava
                    StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                    //setResult(StatementExpressionList);
          $EndJava
        ./
    
--    EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
--        /.$BadAction./
    
    BreakStatement ::= break identifieropt ;
        /.$BeginJava
                    if (identifieropt == null)
                         setResult(nf.Break(pos()));
                    else setResult(nf.Break(pos(), identifieropt.toString()));
          $EndJava
        ./
    
    ContinueStatement ::= continue identifieropt ;
        /.$BeginJava
                    if (identifieropt == null)
                         setResult(nf.Continue(pos()));
                    else setResult(nf.Continue(pos(), identifieropt.toString()));
          $EndJava
        ./
    
    ReturnStatement ::= return Expressionopt ;
        /.$BeginJava
                    setResult(nf.Return(pos(), Expressionopt));
          $EndJava
        ./
    
    ThrowStatement ::= throw Expression ;
        /.$BeginJava
                    setResult(nf.Throw(pos(), Expression));
          $EndJava
        ./
    
    SynchronizedStatement ::= synchronized ( Expression ) Block
        /.$BeginJava
                    setResult(nf.Synchronized(pos(), Expression, Block));
          $EndJava
        ./
    
    TryStatement ::= try Block Catches
        /.$BeginJava
                    setResult(nf.Try(pos(), Block, Catches));
          $EndJava
        ./
                   | try Block Catchesopt Finally
        /.$BeginJava
                    setResult(nf.Try(pos(), Block, Catchesopt, Finally));
          $EndJava
        ./
    
    Catches ::= CatchClause
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Catch.class, false);
                    l.add(CatchClause);
                    setResult(l);
          $EndJava
        ./
              | Catches CatchClause
        /.$BeginJava
                    Catches.add(CatchClause);
                    //setResult(Catches);
          $EndJava
        ./
    
    CatchClause ::= catch ( FormalParameter ) Block
        /.$BeginJava
                    setResult(nf.Catch(pos(), FormalParameter, Block));
          $EndJava
        ./
    
    Finally ::= finally Block
        /.$BeginJava
                    setResult(Block);
          $EndJava
        ./
    
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
        /.$BeginJava
                    if (Type instanceof Name)
                    {
                        Name a = (Name) Type;
                        setResult(nf.ClassLit(pos(), a.toType()));
                    }
                    else if (Type instanceof TypeNode)
                    {
                        setResult(nf.ClassLit(pos(), Type));
                    }
                    else if (Type instanceof CanonicalTypeNode)
                    {
                        CanonicalTypeNode a = (CanonicalTypeNode) Type;
                        setResult(nf.ClassLit(pos(), a));
                    }
                    else assert(false);
          $EndJava
        ./
                        | void . class
        /.$BeginJava
                    setResult(nf.ClassLit(pos(),
                                         nf.CanonicalTypeNode(pos(getLeftSpan()), ts.Void())));
          $EndJava
        ./
                        | this
        /.$BeginJava
                    setResult(nf.This(pos()));
          $EndJava
        ./
                        | ClassName . this
        /.$BeginJava
                    setResult(nf.This(pos(), ClassName.toType()));
          $EndJava
        ./
                        | ( Expression )
        /.$BeginJava
                    setResult(nf.ParExpr(pos(), Expression));
          $EndJava
        ./
                        | ClassInstanceCreationExpression
                        | FieldAccess
                        | MethodInvocation
                        | ArrayAccess
    
    Literal ::= IntegerLiteral$IntegerLiteral
        /.$BeginJava
                    polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex($IntegerLiteral));
                    setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
          $EndJava
        ./
              | LongLiteral$LongLiteral
        /.$BeginJava
                    polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex($LongLiteral));
                    setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
          $EndJava
        ./
              | FloatingPointLiteral$FloatLiteral
        /.$BeginJava
                    polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex($FloatLiteral));
                    setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
          $EndJava
        ./
              | DoubleLiteral$DoubleLiteral
        /.$BeginJava
                    polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex($DoubleLiteral));
                    setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
          $EndJava
        ./
              | BooleanLiteral
        /.$BeginJava
                    setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
          $EndJava
        ./
              | CharacterLiteral$CharacterLiteral
        /.$BeginJava
                    polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex($CharacterLiteral));
                    setResult(nf.CharLit(pos(), a.getValue().charValue()));
          $EndJava
        ./
              | StringLiteral$str
        /.$BeginJava
                    polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex($str));
                    setResult(nf.StringLit(pos(), a.getValue()));
          $EndJava
        ./
              | null
        /.$BeginJava
                    setResult(nf.NullLit(pos()));
          $EndJava
        ./

    BooleanLiteral ::= true$trueLiteral
        /.$BeginJava
                    setResult(boolean_lit(getRhsFirstTokenIndex($trueLiteral)));
          $EndJava
        ./
                     | false$falseLiteral
        /.$BeginJava
                    setResult(boolean_lit(getRhsFirstTokenIndex($falseLiteral)));
          $EndJava
        ./

    --
    -- The following case appeared to be missing from the spec:
    --
    --                                | identifier . new TypeArgumentsopt identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
    --
    ClassInstanceCreationExpression ::=  new TypeArgumentsopt ClassOrInterfaceType TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
                                      | Primary . new TypeArgumentsopt identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
                                      | AmbiguousName . new TypeArgumentsopt identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
    
    ArgumentList ::= Expression
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Expr.class, false);
                    l.add(Expression);
                    setResult(l);
          $EndJava
        ./
                   | ArgumentList , Expression
        /.$BeginJava
                    ArgumentList.add(Expression);
                    //setResult(ArgumentList);
          $EndJava
        ./

--
-- The rules below were specified however, from some examples,
-- it would appear that ClassOrInterfaceType is expected instead
-- of TypeName
--
--                              | new TypeName DimExprs Dimsopt
--                              | new TypeName Dims ArrayInitializer
--
--    ArrayCreationExpression ::= new PrimitiveType DimExprs Dimsopt
--        /.$BeginJava
--                    setResult(nf.NewArray(pos(), PrimitiveType, DimExprs, Dimsopt.intValue()));
--          $EndJava
--        ./
--                              | new ClassOrInterfaceType DimExprs Dimsopt
--        /.$BeginJava
--                    setResult(nf.NewArray(pos(), ClassOrInterfaceType, DimExprs, Dimsopt.intValue()));
--          $EndJava
--        ./
--                              | new PrimitiveType Dims ArrayInitializer
--        /.$BeginJava
--                    setResult(nf.NewArray(pos(), PrimitiveType, Dims.intValue(), ArrayInitializer));
--          $EndJava
--        ./
--                              | new ClassOrInterfaceType Dims ArrayInitializer
--        /.$BeginJava
--                    setResult(nf.NewArray(pos(), ClassOrInterfaceType, Dims.intValue(), ArrayInitializer));
--          $EndJava
--        ./
    
    DimExprs ::= DimExpr
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Expr.class, false);
                    l.add(DimExpr);
                    setResult(l);
          $EndJava
        ./
               | DimExprs DimExpr
        /.$BeginJava
                    DimExprs.add(DimExpr);
                    //setResult(DimExprs);
          $EndJava
        ./
    
    DimExpr ::= '[' Expression ']'
        /.$BeginJava
                    setResult(Expression.position(pos()));
          $EndJava
        ./
    
    Dims ::= '[' ']'
        /.$BeginJava
                    setResult(new Integer(1));
          $EndJava
        ./
           | Dims '[' ']'
        /.$BeginJava
                    setResult(new Integer(Dims.intValue() + 1));
          $EndJava
        ./
    
    FieldAccess ::= Primary . identifier
        /.$BeginJava
                    setResult(nf.Field(pos(), Primary, nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
          $EndJava
        ./
                  | super . identifier
        /.$BeginJava
                    setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
          $EndJava
        ./
                  | ClassName . super$sup . identifier
        /.$BeginJava
                    setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex($sup)), ClassName.toType()), nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
          $EndJava
        ./
    
    MethodInvocation ::= MethodName ( ArgumentListopt )
        /.$BeginJava
                    setResult(nf.Call(pos(), MethodName.prefix == null
                                                                 ? null
                                                                 : MethodName.prefix.toReceiver(), MethodName.name, ArgumentListopt));
          $EndJava
        ./
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
        /.$BeginJava
                    setResult(ExpressionName.toExpr());
          $EndJava
        ./
                        | PostIncrementExpression
                        | PostDecrementExpression
    
    PostIncrementExpression ::= PostfixExpression ++
        /.$BeginJava
                    setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
          $EndJava
        ./
    
    PostDecrementExpression ::= PostfixExpression '--'
        /.$BeginJava
                    setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
          $EndJava
        ./
    
    UnaryExpression ::= PreIncrementExpression
                      | PreDecrementExpression
                      | + UnaryExpression
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.POS, UnaryExpression));
          $EndJava
        ./
                      | - UnaryExpression
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.NEG, UnaryExpression));
          $EndJava
        ./
                      | UnaryExpressionNotPlusMinus
    
    PreIncrementExpression ::= ++ UnaryExpression
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpression));
          $EndJava
        ./
    
    PreDecrementExpression ::= '--' UnaryExpression
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpression));
          $EndJava
        ./
    
    UnaryExpressionNotPlusMinus ::= PostfixExpression
                                  | ~ UnaryExpression
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
          $EndJava
        ./
                                  | ! UnaryExpression
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
          $EndJava
        ./
                                  | CastExpression
    
--
-- See X10 definition
--
--    CastExpression ::= ( PrimitiveType Dimsopt ) UnaryExpression
--                     | ( ReferenceType ) UnaryExpressionNotPlusMinus
    
    MultiplicativeExpression ::= UnaryExpression
                               | MultiplicativeExpression * UnaryExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
          $EndJava
        ./
                               | MultiplicativeExpression / UnaryExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
          $EndJava
        ./
                               | MultiplicativeExpression '%' UnaryExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
          $EndJava
        ./
    
    AdditiveExpression ::= MultiplicativeExpression
                         | AdditiveExpression + MultiplicativeExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
          $EndJava
        ./
                         | AdditiveExpression - MultiplicativeExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
          $EndJava
        ./
    
    ShiftExpression ::= AdditiveExpression
                      | ShiftExpression << AdditiveExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
          $EndJava
        ./
                      | ShiftExpression > > AdditiveExpression
        /.$BeginJava
                    // TODO: make sure that there is no space after the ">" signs
                    setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
          $EndJava
        ./
                      | ShiftExpression > > > AdditiveExpression
        /.$BeginJava
                    // TODO: make sure that there is no space after the ">" signs
                    setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
          $EndJava
        ./
    
    RelationalExpression ::= ShiftExpression
                           | RelationalExpression < ShiftExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
          $EndJava
        ./
                           | RelationalExpression > ShiftExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
          $EndJava
        ./
                           | RelationalExpression <= ShiftExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
          $EndJava
        ./
                           | RelationalExpression > = ShiftExpression
        /.$BeginJava
                    // TODO: make sure that there is no space after the ">" signs
                    setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
          $EndJava
        ./
--
-- See X10 definition
--
--                           | RelationalExpression instanceof ReferenceType
--        /.$BeginJava
--                    setResult(nf.Instanceof(pos(), RelationalExpression, ReferenceType));
--          $EndJava
--        ./
    
    EqualityExpression ::= RelationalExpression
                         | EqualityExpression == RelationalExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
          $EndJava
        ./
                         | EqualityExpression != RelationalExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
          $EndJava
        ./
    
    AndExpression ::= EqualityExpression
                    | AndExpression & EqualityExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
          $EndJava
        ./
    
    ExclusiveOrExpression ::= AndExpression
                            | ExclusiveOrExpression ^ AndExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
          $EndJava
        ./
    
    InclusiveOrExpression ::= ExclusiveOrExpression
                            | InclusiveOrExpression '|' ExclusiveOrExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
          $EndJava
        ./
    
    ConditionalAndExpression ::= InclusiveOrExpression
                               | ConditionalAndExpression && InclusiveOrExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
          $EndJava
        ./
    
    ConditionalOrExpression ::= ConditionalAndExpression
                              | ConditionalOrExpression || ConditionalAndExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
          $EndJava
        ./
    
    ConditionalExpression ::= ConditionalOrExpression
                            | ConditionalOrExpression ? Expression : ConditionalExpression
        /.$BeginJava
                    setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
          $EndJava
        ./
    
    AssignmentExpression ::= ConditionalExpression
                           | Assignment
    
    Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
        /.$BeginJava
                    setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
          $EndJava
        ./
    
    LeftHandSide ::= ExpressionName
        /.$BeginJava
                    setResult(ExpressionName.toExpr());
          $EndJava
        ./
                   | FieldAccess
                   | ArrayAccess
    
    AssignmentOperator ::= =
        /.$BeginJava
                    setResult(Assign.ASSIGN);
          $EndJava
        ./
                         | *=
        /.$BeginJava
                    setResult(Assign.MUL_ASSIGN);
          $EndJava
        ./
                         | /=
        /.$BeginJava
                    setResult(Assign.DIV_ASSIGN);
          $EndJava
        ./
                         | '%='
        /.$BeginJava
                    setResult(Assign.MOD_ASSIGN);
          $EndJava
        ./
                         | +=
        /.$BeginJava
                    setResult(Assign.ADD_ASSIGN);
          $EndJava
        ./
                         | -=
        /.$BeginJava
                    setResult(Assign.SUB_ASSIGN);
          $EndJava
        ./
                         | <<=
        /.$BeginJava
                    setResult(Assign.SHL_ASSIGN);
          $EndJava
        ./
                         | > > =
        /.$BeginJava
                    // TODO: make sure that there is no space after the ">" signs
                    setResult(Assign.SHR_ASSIGN);
          $EndJava
        ./
                         | > > > =
        /.$BeginJava
                    // TODO: make sure that there is no space after the ">" signs
                    setResult(Assign.USHR_ASSIGN);
          $EndJava
        ./
                         | &=
        /.$BeginJava
                    setResult(Assign.BIT_AND_ASSIGN);
          $EndJava
        ./
                         | ^=
        /.$BeginJava
                    setResult(Assign.BIT_XOR_ASSIGN);
          $EndJava
        ./
                         | |=
        /.$BeginJava
                    setResult(Assign.BIT_OR_ASSIGN);
          $EndJava
        ./
    
    Expression ::= AssignmentExpression
    
    ConstantExpression ::= Expression
    
    --
    -- Optional rules
    --
    Dimsopt ::= %Empty
        /.$BeginJava
                    setResult(new Integer(0));
          $EndJava
        ./
              | Dims

    Catchesopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Catch.class, false));
          $EndJava
        ./
                 | Catches

    identifieropt ::= %Empty
        /.$NullAction./
                    | identifier
        /.$BeginJava
                    setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
          $EndJava
        ./

    ForUpdateopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
          $EndJava
        ./
                   | ForUpdate

    Expressionopt ::= %Empty
        /.$NullAction./
                    | Expression

    ForInitopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), ForInit.class, false));
          $EndJava
        ./
                 | ForInit

    SwitchLabelsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Case.class, false));
          $EndJava
        ./
                      | SwitchLabels

    SwitchBlockStatementGroupsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
          $EndJava
        ./
                                    | SwitchBlockStatementGroups

    VariableModifiersopt ::= %Empty
        /.$BeginJava
                    setResult(Collections.EMPTY_LIST);
          $EndJava
        ./
                           | VariableModifiers

    VariableInitializersopt ::= %Empty
        /.$NullAction./
                              | VariableInitializers

    ElementValuesopt ::= %Empty
        /.$NullAction./
                       | ElementValues
        /.$BadAction./

    ElementValuePairsopt ::= %Empty
        /.$NullAction./
                           | ElementValuePairs
        /.$BadAction./

    DefaultValueopt ::= %Empty
        /.$NullAction./
                      | DefaultValue

    AnnotationTypeElementDeclarationsopt ::= %Empty
        /.$NullAction./
                                           | AnnotationTypeElementDeclarations
        /.$BadAction./

    AbstractMethodModifiersopt ::= %Empty
        /.$BeginJava
                    setResult(Collections.EMPTY_LIST);
          $EndJava
        ./
                                 | AbstractMethodModifiers

    ConstantModifiersopt ::= %Empty
        /.$BeginJava
                    setResult(Collections.EMPTY_LIST);
          $EndJava
        ./
                           | ConstantModifiers

    InterfaceMemberDeclarationsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), ClassMember.class, false));
          $EndJava
        ./
                                     | InterfaceMemberDeclarations

    ExtendsInterfacesopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), TypeNode.class, false));
          $EndJava
        ./
                           | ExtendsInterfaces

    InterfaceModifiersopt ::= %Empty
        /.$BeginJava
                    setResult(Collections.EMPTY_LIST);
          $EndJava
        ./
                            | InterfaceModifiers

    ClassBodyopt ::= %Empty
        /.$NullAction./
                   | ClassBody

    Argumentsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Expr.class, false));
          $EndJava
        ./
                   | Arguments

    EnumBodyDeclarationsopt ::= %Empty
        /.$NullAction./
                              | EnumBodyDeclarations
        /.$BadAction./

    ,opt ::= %Empty
        /.$NullAction./
           | ,

    EnumConstantsopt ::= %Empty
        /.$NullAction./
                       | EnumConstants
        /.$BadAction./

    ArgumentListopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Expr.class, false));
          $EndJava
        ./
                      | ArgumentList

    BlockStatementsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Stmt.class, false));
          $EndJava
        ./
                         | BlockStatements

    ExplicitConstructorInvocationopt ::= %Empty
        /.$NullAction./
                                       | ExplicitConstructorInvocation

    ConstructorModifiersopt ::= %Empty
        /.$BeginJava
                    setResult(Collections.EMPTY_LIST);
          $EndJava
        ./
                              | ConstructorModifiers

    ...opt ::= %Empty
        /.$NullAction./
             | ...

    FormalParameterListopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Formal.class, false));
          $EndJava
        ./
                             | FormalParameterList

    Throwsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), TypeNode.class, false));
          $EndJava
        ./
                | Throws

    MethodModifiersopt ::= %Empty
        /.$BeginJava
                    setResult(Collections.EMPTY_LIST);
          $EndJava
        ./
                         | MethodModifiers

    FieldModifiersopt ::= %Empty
        /.$BeginJava
                    setResult(Collections.EMPTY_LIST);
          $EndJava
        ./
                        | FieldModifiers

    ClassBodyDeclarationsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), ClassMember.class, false));
          $EndJava
        ./
                               | ClassBodyDeclarations

    Interfacesopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), TypeNode.class, false));
          $EndJava
        ./
                    | Interfaces

    Superopt ::= %Empty
        /.$BeginJava
                   setResult(nf.TypeNodeFromQualifiedName(pos(), "x10.lang.Object"));
          $EndJava
        ./
               | Super

    TypeParametersopt ::= %Empty
        /.$NullAction./
                        | TypeParameters

    ClassModifiersopt ::= %Empty
        /.$BeginJava
                    setResult(Collections.EMPTY_LIST);
          $EndJava
        ./
                        | ClassModifiers

    Annotationsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
          $EndJava
        ./
                     | Annotations

    TypeDeclarationsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
          $EndJava
        ./
                          | TypeDeclarations

    ImportDeclarationsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Import.class, false));
          $EndJava
        ./
                            | ImportDeclarations

    PackageDeclarationopt ::= %Empty
        /.$NullAction./
                            | PackageDeclaration

    WildcardBoundsOpt ::= %Empty
        /.$NullAction./
                        | WildcardBounds
        /.$BadAction./

    AdditionalBoundListopt ::= %Empty
        /.$NullAction./
                             | AdditionalBoundList
        /.$BadAction./

    TypeBoundopt ::= %Empty
        /.$NullAction./
                   | TypeBound
        /.$BadAction./

    TypeArgumentsopt ::= %Empty
        /.$NullAction./
                       | TypeArguments
        /.$BadAction./
%End
