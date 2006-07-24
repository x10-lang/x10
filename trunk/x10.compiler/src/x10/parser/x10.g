%options fp=X10Parser,states
%options list
%options la=6
%options variables=nt
%options conflicts
%options softkeywords
%options package=x10.parser
%options template=uide/btParserTemplate.gi
%options import_terminals="X10Lexer.gi"

$Notice
/.
//
// Licensed Material
// (C) Copyright IBM Corp, 2006
//
./
$End

$Globals
    /.
    import java.util.Collections;
    import java.util.Iterator;
    import java.util.LinkedList;
    import java.util.List;

    import polyglot.ast.AmbTypeNode;
    import polyglot.ast.ArrayInit;
    import polyglot.ast.Assign;
    import polyglot.ast.Binary;
    import polyglot.ast.Block;
    import polyglot.ast.Call;
    import polyglot.ast.CanonicalTypeNode;
    import polyglot.ast.Case;
    import polyglot.ast.Catch;
    import polyglot.ast.ClassBody;
    import polyglot.ast.ClassDecl;
    import polyglot.ast.ClassMember;
    import polyglot.ast.Eval;
    import polyglot.ast.Expr;
    import polyglot.ast.FloatLit;
    import polyglot.ast.ForInit;
    import polyglot.ast.ForUpdate;
    import polyglot.ast.Formal;
    import polyglot.ast.Import;
    import polyglot.ast.IntLit;
    import polyglot.ast.LocalDecl;
    import polyglot.ast.ConstructorDecl;
    import polyglot.ast.MethodDecl;
    import polyglot.ast.New;
    import polyglot.ast.NodeFactory;
    import polyglot.ast.PackageNode;
    import polyglot.ast.SourceFile;
    import polyglot.ast.Stmt;
    import polyglot.ast.SwitchElement;
    import polyglot.ast.TopLevelDecl;
    import polyglot.ast.TypeNode;
    import polyglot.ast.Unary;
    import polyglot.ext.jl.parse.Name;
    import polyglot.ext.x10.ast.Here;
    import polyglot.ext.x10.ast.DepParameterExpr;
    import polyglot.ext.x10.ast.Tuple;
    import polyglot.ext.x10.ast.When;
    import polyglot.ext.x10.ast.X10Formal;
    import polyglot.ext.x10.ast.X10Formal_c;
    import polyglot.ext.x10.ast.X10Loop;
    import polyglot.ext.x10.ast.X10NodeFactory;
    import polyglot.ext.x10.types.X10TypeSystem;
    import polyglot.ext.x10.types.X10TypeSystem_c;
    import polyglot.ext.x10.ast.PropertyDecl;
    import polyglot.frontend.FileSource;
    import polyglot.frontend.Parser;
    import polyglot.lex.BooleanLiteral;
    import polyglot.lex.CharacterLiteral;
    import polyglot.lex.DoubleLiteral;
    import polyglot.lex.FloatLiteral;
    import polyglot.lex.Identifier;
    import polyglot.lex.IntegerLiteral;
    import polyglot.lex.LongLiteral;
    import polyglot.lex.NullLiteral;
    import polyglot.lex.Operator;
    import polyglot.lex.StringLiteral;
    import polyglot.main.Report;
    import polyglot.parse.VarDeclarator;
    import polyglot.types.Flags;
    import polyglot.ext.x10.types.X10Flags;
    import polyglot.ext.x10.ast.X10TypeNode;
    import polyglot.types.SemanticException;
    import polyglot.types.Type;
    import polyglot.types.TypeSystem;
    import polyglot.util.ErrorInfo;
    import polyglot.util.ErrorQueue;
    import polyglot.util.InternalCompilerError;
    import polyglot.util.Position;
    import polyglot.util.TypedList;

    import lpg.lpgjavaruntime.BacktrackingParser;
    import lpg.lpgjavaruntime.BadParseException;
    import lpg.lpgjavaruntime.BadParseSymFileException;
    import lpg.lpgjavaruntime.DiagnoseParser;
    import lpg.lpgjavaruntime.LexStream;
    import lpg.lpgjavaruntime.NotBacktrackParseTableException;
    import lpg.lpgjavaruntime.NullExportedSymbolsException;
    import lpg.lpgjavaruntime.NullTerminalSymbolsException;
    import lpg.lpgjavaruntime.ParseTable;
    import lpg.lpgjavaruntime.PrsStream;
    import lpg.lpgjavaruntime.RuleAction;
    import lpg.lpgjavaruntime.UndefinedEofSymbolException;
    import lpg.lpgjavaruntime.UnimplementedTerminalsException;
    ./
$End

$Import
    GJavaParserForX10.gi

    $DropSymbols
        ActualTypeArgumentList
        ActualTypeArgument
        AdditionalBound
        AdditionalBoundList
        AdditionalBoundListopt
        Annotation
        Annotations
        Annotationsopt
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

    $DropRules
        ImportDeclaration ::= SingleStaticImportDeclaration
                            | StaticImportOnDemandDeclaration
        SwitchLabel ::= case EnumConstant :
        ReferenceType ::= TypeVariable
        ClassType ::= TypeName TypeArgumentsopt
        InterfaceType ::= TypeName TypeArgumentsopt
        PackageDeclaration ::= Annotationsopt package PackageName ;
        ClassDeclaration ::= EnumDeclaration
        NormalClassDeclaration ::= ClassModifiersopt class identifier TypeParametersopt Superopt Interfacesopt ClassBody
        ClassModifier ::= Annotation
        FieldModifier ::= Annotation
                        | volatile
        MethodHeader ::= MethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt
        VariableModifier ::= Annotations
        MethodModifier ::= Annotations
                         | synchronized
        SynchronizedStatement ::= synchronized ( Expression ) Block
        ConstructorDeclarator ::= TypeParametersopt SimpleTypeName ( FormalParameterListopt )
        FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators ;
        ConstructorModifier ::= Annotations
        ExplicitConstructorInvocation ::= TypeArgumentsopt this ( ArgumentListopt ) ;
                                        | TypeArgumentsopt super ( ArgumentListopt ) ;
                                        | Primary . TypeArgumentsopt this ( ArgumentListopt ) ;
                                        | Primary . TypeArgumentsopt super ( ArgumentListopt ) ;
        InterfaceDeclaration ::= AnnotationTypeDeclaration
        NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier TypeParametersopt ExtendsInterfacesopt InterfaceBody
        InterfaceModifier ::= Annotation
        ConstantModifier ::= Annotation
        AbstractMethodDeclaration ::= AbstractMethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt ;
        AbstractMethodModifier ::= Annotations
        ClassInstanceCreationExpression ::=  new TypeArgumentsopt ClassOrInterfaceType TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
                                          | Primary . new TypeArgumentsopt identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
                                          | AmbiguousName . new TypeArgumentsopt identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        MethodInvocation ::= Primary . TypeArgumentsopt identifier ( ArgumentListopt )
                           | super . TypeArgumentsopt identifier ( ArgumentListopt )
                           | ClassName . super . TypeArgumentsopt identifier ( ArgumentListopt )
                           | TypeName . TypeArguments identifier ( ArgumentListopt )
$End

$Define
    --
    -- Definition of macros used in the parser template
    --
    $ast_class /.polyglot.ast.Node./
    $additional_interfaces /., Parser./
$End

$Terminals
--     RANGE ::= '..'
    ARROW ::= '->'
$End

$Keywords
    --
    -- All X10 keywords are soft
    --
    await
    current
    local
    method
    next
    now
    nullable
    or
    reference
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
$End

$Headers
    /.
        private ErrorQueue eq;
        private X10TypeSystem ts;
        private X10NodeFactory nf;
        private FileSource source;
        private boolean unrecoverableSyntaxError = false;

        public $action_type(LexStream lexStream, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
        {
            this(lexStream);
            this.ts = (X10TypeSystem) t;
            this.nf = (X10NodeFactory) n;
            this.source = source;
            this.eq = q;
        }

        public String getErrorLocation(int lefttok, int righttok)
        {
            return getFileName() + ':' +
                   getLine(lefttok) + ":" + getColumn(lefttok) + ":" +
                   getEndLine(righttok) + ":" + getEndColumn(righttok) + ": ";
        }

        // RMF 11/7/2005 - N.B. This class has to be serializable, since it shows up inside Type objects,
        // which Polyglot serializes to save processing when loading class files generated from source
        // by Polyglot itself.
        public static class JPGPosition extends Position
        {
            private static final long serialVersionUID= -1593187800129872262L;
            private final transient IToken leftIToken,
                                           rightIToken;

            public JPGPosition(String path, String filename, IToken leftToken, IToken rightToken)
            {
                super(path, filename,
                      leftToken.getLine(), leftToken.getColumn(),
                      rightToken.getEndLine(), rightToken.getEndColumn());
                this.leftIToken = leftToken;
                this.rightIToken = rightToken;
            }

            public IToken getLeftIToken() { return leftIToken; }
            public IToken getRightIToken() { return rightIToken; }

            public String toText()
            {
                PrsStream prsStream = leftIToken.getPrsStream();
                return new String(prsStream.getInputChars(),
                                  leftIToken.getStartOffset(),
                                  rightIToken.getEndOffset() - leftIToken.getStartOffset() + 1);
            }
        }

        public void reportError(int errorCode, String locationInfo, int leftToken, int rightToken, String tokenText)
        {
            if (errorCode == DELETION_CODE ||
                errorCode == MISPLACED_CODE) tokenText = "";
            if (! tokenText.equals("")) tokenText += ' ';
            eq.enqueue(ErrorInfo.SYNTAX_ERROR, locationInfo + tokenText + errorMsgText[errorCode],
                       new JPGPosition("",
                                       getFileName(),
                                       super.getIToken(leftToken),
                                       super.getIToken(rightToken)));
        }

        public $ast_class parse() {
            try
            {
                SourceFile sf = (SourceFile) parser();

                if ((!unrecoverableSyntaxError) && (sf != null))
                    return sf.source(source);

                eq.enqueue(ErrorInfo.SYNTAX_ERROR, "Unable to parse " + source.name() + ".");
            }
            catch (RuntimeException e) {
                // Let the Compiler catch and report it.
                throw e;
            }
            catch (Exception e) {
                // Used by cup to indicate a non-recoverable error.
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, e.getMessage());
            }

            return null;
        }

        public String file()
        {
            return super.getFileName();
        }

        public JPGPosition pos()
        {
            return new JPGPosition("",
                                   super.getFileName(),
                                   super.getIToken(getLeftSpan()),
                                   super.getIToken(getRightSpan()));
        }

        public JPGPosition pos(int i)
        {
            return new JPGPosition("",
                                   super.getFileName(),
                                   super.getIToken(i),
                                   super.getIToken(i));
        }

        public JPGPosition pos(int i, int j)
        {
            return new JPGPosition("",
                                   super.getFileName(),
                                   super.getIToken(i),
                                   super.getIToken(j));
        }

        /**
         * Return the source position of the declaration.
         */
        public JPGPosition pos (VarDeclarator n)
        {
          if (n == null) return null;
          return (JPGPosition) n.pos;
        }

        private void checkTypeName(polyglot.lex.Identifier identifier) {
            String filename = file(),
                   idname = identifier.getIdentifier();
            int dot = filename.lastIndexOf('.'),
                slash = filename.lastIndexOf('/', dot);
            if (slash == -1)
                slash = filename.lastIndexOf('\\', dot);
            String clean_filename = (slash >= 0 && dot >= 0 ? filename.substring(slash+1, dot) : "");
            if ((! clean_filename.equals(idname)) && clean_filename.equalsIgnoreCase(idname))
                eq.enqueue(ErrorInfo.SYNTAX_ERROR,
                           "This type name does not match the name of the containing file: " + filename.substring(slash+1),
                           identifier.getPosition());
       }


        private polyglot.lex.Operator op(int i) {
            return new Operator(pos(i), super.getName(i), super.getKind(i));
        }

        private polyglot.lex.Identifier id(int i) {
            return new Identifier(pos(i), super.getName(i), $sym_type.TK_IDENTIFIER);
        }
        private String comment(int i) {
            String s = super.getName(i);
            if (s != null && s.startsWith("/**") && s.endsWith("*/")) {
                return s +"\n";
            }
            return null;
        }

        /** Pretend to have parsed new <T>Array.pointwiseOp
         * { public <T> apply(Formal) MethodBody }
         * instead of (Formal) MethodBody. Note that Formal may have
         * exploded vars.
         * @author vj
        */

        private New makeInitializer( Position pos, TypeNode resultType,
                                     X10Formal f, Block body ) {
          Flags flags = Flags.PUBLIC;
          // resulttype is a.
          List l1 = new TypedList(new LinkedList(), X10Formal.class, false);
          l1.add(f);
          TypeNode appResultType = resultType;
          if (!(resultType instanceof CanonicalTypeNode)) {
            Name x10 = new Name(nf, ts, pos, "x10");
            Name x10CG = new Name(nf, ts, pos, x10, "compilergenerated");
            Name x10CGP1 = new Name(nf, ts, pos, x10CG, "Parameter1");
            appResultType = x10CGP1.toType();
          }
          MethodDecl decl = nf.MethodDecl(pos, flags, appResultType,
                                        "apply", l1,
                                          new LinkedList(), body);
          //  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
          String prefix = !(resultType instanceof CanonicalTypeNode) ?
                            "generic" : resultType.toString();
          Name x10 = new Name(nf, ts, pos, "x10");
          Name x10Lang = new Name(nf, ts, pos, x10, "lang");
          Name tArray
              = new Name(nf, ts, pos, x10Lang, prefix + "Array");
          Name tXArray
          = new Name(nf, ts, pos, x10Lang, "x10.lang." + prefix + "Array");
          Name tArrayPointwiseOp = new Name(nf, ts, pos, tArray, "pointwiseOp");
          List classDecl = new TypedList(new LinkedList(), MethodDecl.class, false);
          classDecl.add( decl );
          TypeNode t = !(resultType instanceof CanonicalTypeNode) ?
                   (TypeNode) nf.GenericArrayPointwiseOpTypeNode(pos, resultType) :
                   (TypeNode) tArrayPointwiseOp.toType();

          New initializer = nf.New(pos,
                          t,
                          new LinkedList(),
                                 nf.ClassBody( pos, classDecl ) );
          return initializer;
        }

        /** Pretend to have parsed new <T>Array.pointwiseOp
         * { public <T> apply(Formal) MethodBody }
         * instead of (Formal) MethodBody. Note that Formal may have
         * exploded vars.
         * @author vj
        */
        private New XXmakeInitializer( Position pos, TypeNode resultType,
                                     X10Formal f, Block body ) {
          Flags flags = Flags.PUBLIC;
          // resulttype is a.
          List l1 = new TypedList(new LinkedList(), X10Formal.class, false);
          l1.add(f);
          MethodDecl decl = nf.MethodDecl(pos, flags, resultType,
                                        "apply", l1,
                                          new LinkedList(), body);
          //  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
          // [IP] FIXME: this will break if the result is not a canonical type
          Name tArray = new Name(nf, ts, pos, resultType.toString() + "Array");
          Name tArrayPointwiseOp = new Name(nf, ts, pos, tArray, "pointwiseOp");
          List classDecl = new TypedList(new LinkedList(), MethodDecl.class, false);
          classDecl.add( decl );
          New initializer = nf.New(pos, tArray.toExpr(),
                                 tArrayPointwiseOp.toType(),
                                 new LinkedList(),
                                 nf.ClassBody( pos, classDecl ) );
          return initializer;
        }

        /* Roll our own integer parser.  We can't use Long.parseLong because
         * it doesn't handle numbers greater than 0x7fffffffffffffff correctly.
         */
        private long parseLong(String s, int radix)
        {
            long x = 0L;

            s = s.toLowerCase();

            for (int i = 0; i < s.length(); i++) {
                int c = s.charAt(i);

                if (c < '0' || c > '9') {
                    c = c - 'a' + 10;
                }
                else {
                    c = c - '0';
                }

                x *= radix;
                x += c;
            }

            return x;
        }

        private long parseLong(String s)
        {
            int radix,
                start_index,
                end_index = (s.charAt(s.length() - 1) == 'l' || s.charAt(s.length() - 1) == 'L'
                                                       ? s.length() - 1
                                                       : s.length());
            if (s.charAt(0) == '0')
            {
               if (s.length() > 1 && (s.charAt(1) == 'x' || s.charAt(1) == 'X'))
               {
                   radix = 16;
                   start_index = 2;
               }
               else
               {
                   radix = 8;
                   start_index = 0;
               }
            }
            else
            {
                radix = 10;
                start_index = 0;
            }

            return parseLong(s.substring(start_index, end_index), radix);
        }

        private polyglot.lex.IntegerLiteral int_lit(int i, int radix)
        {
            long x = parseLong(super.getName(i), radix);
            return new IntegerLiteral(pos(i), (int) x, $sym_type.TK_IntegerLiteral);
        }

        private polyglot.lex.IntegerLiteral int_lit(int i)
        {
            long x = parseLong(super.getName(i));
            return new IntegerLiteral(pos(i), (int) x, $sym_type.TK_IntegerLiteral);
        }

        private polyglot.lex.LongLiteral long_lit(int i, int radix)
        {
            long x = parseLong(super.getName(i), radix);
            return new LongLiteral(pos(i), x, $sym_type.TK_LongLiteral);
        }

        private polyglot.lex.LongLiteral long_lit(int i)
        {
            long x = parseLong(super.getName(i));
            return new LongLiteral(pos(i), x, $sym_type.TK_LongLiteral);
        }

        private polyglot.lex.FloatLiteral float_lit(int i)
        {
            try {
                String s = super.getName(i);
                int end_index = (s.charAt(s.length() - 1) == 'f' || s.charAt(s.length() - 1) == 'F'
                                                           ? s.length() - 1
                                                           : s.length());
                float x = Float.parseFloat(s.substring(0, end_index));
                return new FloatLiteral(pos(i), x, $sym_type.TK_FloatingPointLiteral);
            }
            catch (NumberFormatException e) {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                           "Illegal float literal \"" + super.getName(i) + "\"", pos(i));
                return null;
            }
        }

        private polyglot.lex.DoubleLiteral double_lit(int i)
        {
            try {
                String s = super.getName(i);
                int end_index = (s.charAt(s.length() - 1) == 'd' || s.charAt(s.length() - 1) == 'D'
                                                           ? s.length() - 1
                                                           : s.length());
                double x = Double.parseDouble(s.substring(0, end_index));
                return new DoubleLiteral(pos(i), x, $sym_type.TK_DoubleLiteral);
            }
            catch (NumberFormatException e) {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                           "Illegal float literal \"" + super.getName(i) + "\"", pos(i));
                return null;
            }
        }

        private polyglot.lex.CharacterLiteral char_lit(int i)
        {
            char x;
            String s = super.getName(i);
            if (s.charAt(1) == '\\') {
                switch(s.charAt(2)) {
                    case 'u':
                        x = (char) parseLong(s.substring(3, s.length() - 1), 16);
                        break;
                    case 'b':
                        x = '\b';
                        break;
                    case 't':
                        x = '\t';
                        break;
                    case 'n':
                        x = '\n';
                        break;
                    case 'f':
                        x = '\f';
                        break;
                    case 'r':
                        x = '\r';
                        break;
                    case '\"':
                        x = '\"';
                        break;
                    case '\'':
                        x = '\'';
                        break;
                    case '\\':
                        x = '\\';
                        break;
                    default:
                        x = (char) parseLong(s.substring(2, s.length() - 1), 8);
                        if (x > 255) {
                            unrecoverableSyntaxError = true;
                            eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                                       "Illegal character literal " + s, pos(i));
                        }
                }
            }
            else {
                assert(s.length() == 3);
                x = s.charAt(1);
            }

            return new CharacterLiteral(pos(i), x, $sym_type.TK_CharacterLiteral);
        }

        private polyglot.lex.BooleanLiteral boolean_lit(int i)
        {
            return new BooleanLiteral(pos(i), super.getKind(i) == $sym_type.TK_true, super.getKind(i));
        }

        private polyglot.lex.StringLiteral string_lit(int i)
        {
            String s = super.getName(i);
            char x[] = new char[s.length()];
            int j = 1,
                k = 0;
            while(j < s.length() - 1) {
                if (s.charAt(j) != '\\')
                    x[k++] = s.charAt(j++);
                else {
                    switch(s.charAt(j + 1)) {
                        case 'u':
                            x[k++] = (char) parseLong(s.substring(j + 2, j + 6), 16);
                            j += 6;
                            break;
                        case 'b':
                            x[k++] = '\b';
                            j += 2;
                            break;
                        case 't':
                            x[k++] = '\t';
                            j += 2;
                            break;
                        case 'n':
                            x[k++] = '\n';
                            j += 2;
                            break;
                        case 'f':
                            x[k++] = '\f';
                            j += 2;
                            break;
                        case 'r':
                            x[k++] = '\r';
                            j += 2;
                            break;
                        case '\"':
                            x[k++] = '\"';
                            j += 2;
                            break;
                        case '\'':
                            x[k++] = '\'';
                            j += 2;
                            break;
                        case '\\':
                            x[k++] = '\\';
                            j += 2;
                            break;
                        default:
                        {
                            int n = j + 1;
                            for (int l = 0; l < 3 && Character.isDigit(s.charAt(n)); l++)
                                n++;
                            char c = (char) parseLong(s.substring(j + 1, n), 8);
                            if (c > 255) {
                                unrecoverableSyntaxError = true;
                                eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                                           "Illegal character (" + s.substring(j, n) + ") in string literal " + s, pos(i));
                            }
                            x[k++] = c;
                            j = n;
                        }
                    }
                }
            }

            return new StringLiteral(pos(i), new String(x, 0, k), $sym_type.TK_StringLiteral);
        }

        private polyglot.lex.NullLiteral null_lit(int i)
        {
            return new NullLiteral(pos(i), $sym_type.TK_null);
        }

    ./
$End

$Rules -- Overridden rules from GJavaParser
    ClassType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
        /.$BeginJava
                         setResult(DepParametersopt == null
                                   ? TypeName.toType()
                                   : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
          $EndJava
        ./


    InterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
        /.$BeginJava
                     setResult(DepParametersopt == null
                                   ? TypeName.toType()
                                   : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
          $EndJava
        ./

    PackageDeclaration ::= package PackageName ;
        /.$BeginJava
                    setResult(PackageName.toPackage());
          $EndJava
        ./

    NormalClassDeclaration ::= ClassModifiersopt class identifier PropertyListopt  Superopt Interfacesopt ClassBody
        /.$BeginJava
          checkTypeName(identifier);
          //Report.report(1, "Parser: Golden Creating class with properties |" + PropertyListopt + "|");
          List/*<PropertyDecl>*/ props = PropertyListopt == null ? null
                      : (List) PropertyListopt[0];
          Expr ci = PropertyListopt == null ? null : (Expr) PropertyListopt[1];
          setResult(X10Flags.isValue(ClassModifiersopt)
             ? nf.ValueClassDecl(pos(),
                  ClassModifiersopt, identifier.getIdentifier(), props, ci, Superopt, Interfacesopt, ClassBody)
             : nf.ClassDecl(pos(),
                  ClassModifiersopt, identifier.getIdentifier(), props, ci, Superopt, Interfacesopt, ClassBody));
          $EndJava
        ./

    PropertyList ::= ( Properties WhereClauseopt )
    /.$BeginJava
       Object[] result = new Object[2];
       result[0] = Properties;
       result[1] = WhereClauseopt;
       setResult(result);
     $EndJava ./

       Properties ::= Property
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                    l.add(Property);
                    setResult(l);
          $EndJava
        ./
                          | Properties , Property
        /.$BeginJava
                    Properties.add(Property);
                    // setResult(FormalParameters);
          $EndJava
        ./


    Property ::=  Type identifier
        /.$BeginJava

                    setResult(nf.PropertyDecl(pos(), Flags.PUBLIC.Final(), Type,
                    identifier.getIdentifier()));

          $EndJava
        ./
    MethodHeader ::= MethodModifiersopt ResultType MethodDeclarator Throwsopt
        /.$BeginJava
          Name c = (Name) MethodDeclarator[0];
          List d = (List) MethodDeclarator[1];
          Integer e = (Integer) MethodDeclarator[2];
          Expr where = (Expr) MethodDeclarator[3];
          TypeNode thisClause = (TypeNode) MethodDeclarator[4];
          if (ResultType.type() == ts.Void() && e.intValue() > 0)
             {
               // TODO: error!!!
               assert(false);
             }

           setResult(nf.MethodDecl(pos(getRhsFirstTokenIndex($ResultType), getRhsLastTokenIndex($MethodDeclarator)),
              thisClause,
              MethodModifiersopt,
              nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex($ResultType), getRhsLastTokenIndex($ResultType)), e.intValue()),
              c.toString(),
              d,
              where,
              Throwsopt,
              null));
          $EndJava
        ./

    ExplicitConstructorInvocation ::= this ( ArgumentListopt ) ;
        /.$BeginJava
                    setResult(nf.ThisCall(pos(), ArgumentListopt));
          $EndJava
        ./
                                    | super ( ArgumentListopt ) ;
        /.$BeginJava
                    setResult(nf.SuperCall(pos(), ArgumentListopt));
          $EndJava
        ./
                                    | Primary . this ( ArgumentListopt ) ;
        /.$BeginJava
                    setResult(nf.ThisCall(pos(), Primary, ArgumentListopt));
          $EndJava
        ./
                                    | Primary . super ( ArgumentListopt ) ;
        /.$BeginJava
                    setResult(nf.SuperCall(pos(), Primary, ArgumentListopt));
          $EndJava
        ./

    NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier PropertyListopt ExtendsInterfacesopt InterfaceBody
        /.$BeginJava
          checkTypeName(identifier);
          List/*<PropertyDecl>*/ props = PropertyListopt == null ? null
                      : (List) PropertyListopt[0];
          Expr ci = PropertyListopt == null ? null : (Expr) PropertyListopt[1];
          setResult(nf.ClassDecl(pos(),
                       InterfaceModifiersopt.Interface(),
                       identifier.getIdentifier(),
                       props,
                       ci,
                       null,
                       ExtendsInterfacesopt,
                       InterfaceBody));
          $EndJava
        ./

    AbstractMethodDeclaration ::= AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt ;
        /.$BeginJava
         Name c = (Name) MethodDeclarator[0];
         List d = (List) MethodDeclarator[1];
         Integer e = (Integer) MethodDeclarator[2];
         Expr where = (Expr) MethodDeclarator[3];
         TypeNode thisClause = (TypeNode) MethodDeclarator[4];
         if (ResultType.type() == ts.Void() && e.intValue() > 0)
            {
              // TODO: error!!!
              assert(false);
            }

         setResult(nf.MethodDecl(pos(getRhsFirstTokenIndex($ResultType), getRhsLastTokenIndex($MethodDeclarator)),
                    thisClause,
                    AbstractMethodModifiersopt ,
                    nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex($ResultType), getRhsLastTokenIndex($ResultType)), e.intValue()),
                    c.toString(),
                    d,
                    where,
                    Throwsopt,
                    null));
          $EndJava
        ./

    ClassInstanceCreationExpression ::=  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
                    if (ClassBodyopt == null)
                         setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt));
                    else setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt, ClassBodyopt));
          $EndJava
        ./
                                      | Primary . new identifier  ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
                    Name b = new Name(nf, ts, pos(), identifier.getIdentifier());
                    if (ClassBodyopt == null)
                         setResult(nf.New(pos(), Primary, b.toType(), ArgumentListopt));
                    else setResult(nf.New(pos(), Primary, b.toType(), ArgumentListopt, ClassBodyopt));
          $EndJava
        ./
                                      | AmbiguousName . new identifier ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
                    Name b = new Name(nf, ts, pos(), identifier.getIdentifier());
                    if (ClassBodyopt == null)
                         setResult(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt));
                    else setResult(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt, ClassBodyopt));
          $EndJava
        ./

    MethodInvocation ::= Primary .  identifier ( ArgumentListopt )
        /.$BeginJava
                    setResult(nf.Call(pos(), Primary, identifier.getIdentifier(), ArgumentListopt));
          $EndJava
        ./
                       | super .  identifier ( ArgumentListopt )
        /.$BeginJava
                    setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), identifier.getIdentifier(), ArgumentListopt));
          $EndJava
        ./
                       | ClassName . super$sup .  identifier ( ArgumentListopt )
        /.$BeginJava
                    setResult(nf.Call(pos(), nf.Super(pos(getRhsFirstTokenIndex($sup)), ClassName.toType()), identifier.getIdentifier(), ArgumentListopt));
          $EndJava
        ./
$End

$Rules

    -------------------------------------- Section:::Types

    Type ::= DataType
        /.$BeginJava
                  setResult(DataType);
          $EndJava
        ./
            | nullable Type
        /.$BeginJava
                    setResult(nf.Nullable(pos(), Type));
          $EndJava
        ./
            | future < Type >
        /.$BeginJava
                    setResult(nf.Future(pos(), Type));
          $EndJava
        ./
            | ( Type ) DepParametersopt
             /.$BeginJava
               //System.out.println("Parser: parsed (Type) DepParmetersopt |" + Type + "| |" + DepParametersopt +"|");
                    setResult(DepParametersopt == null ? Type
                    : ((X10TypeNode) Type).dep(null, DepParametersopt));
          $EndJava
        ./

    --
    -- TODO: 12/28/2004 ... This rule is a temporary patch that allows
    --                      us to work with the current Polyglot base.
    --                      It should be removed as there are no primitive
    --                      types in X10.
    DataType ::=  PrimitiveType

    DataType ::= ClassOrInterfaceType
               | ArrayType

    PrimitiveType ::= NumericType DepParametersopt
     /.$BeginJava
                  //  System.out.println("Parser: parsed PrimitiveType |" + NumericType + "| |" + DepParametersopt +"|");
                    setResult(DepParametersopt == null
                                   ? NumericType
                                   : ((X10TypeNode) NumericType).dep(null, DepParametersopt));
          $EndJava
        ./
                   | boolean DepParametersopt
       /.$BeginJava
                    X10TypeNode res = (X10TypeNode) nf.CanonicalTypeNode(pos(), ts.Boolean());
                    setResult(DepParametersopt == null
                               ? res
                               : res.dep(null, DepParametersopt));
         $EndJava
        ./
    PlaceTypeSpecifier ::= @ PlaceType

    PlaceType ::= any
                | current
                | PlaceExpression

    ClassOrInterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
        /.$BeginJava
					X10TypeNode type;

					if (ts.isPrimitiveTypeName(TypeName.name)) {
						try {
							type= (X10TypeNode) nf.CanonicalTypeNode(pos(), ts.primitiveForName(TypeName.name));
						} catch (SemanticException e) {
							throw new InternalCompilerError("Unable to create primitive type for '" + TypeName.name + "'!");
						}
					} else
						type= (X10TypeNode) TypeName.toType();
               //  System.out.println("Parser: parsed ClassOrInterfaceType |" + TypeName + "| |" + DepParametersopt +"|");
                    setResult(DepParametersopt == null
                                   ? type
                                   : type.dep(null, DepParametersopt));
          $EndJava
        ./

    DepParameters ::= ( DepParameterExpr )
        /.$BeginJava
                    setResult(DepParameterExpr);
          $EndJava
        ./

    DepParameterExpr ::= ArgumentList WhereClauseopt
        /.$BeginJava
                    setResult(nf.DepParameterExpr(pos(), ArgumentList, WhereClauseopt));
          $EndJava
        ./
                | WhereClause
        /.$BeginJava
                    setResult(nf.DepParameterExpr(pos(), Collections.EMPTY_LIST, WhereClause));
          $EndJava
        ./

    WhereClause ::= : Expression
        /.$BeginJava
                    setResult(Expression);
          $EndJava
        ./

-- Not supporting jagged arrays for now.
-- We will support a special translation for the case in which
-- the expr in DepParameterExpr is of type int.

    ArrayType ::= X10ArrayType

    X10ArrayType ::= Type [ . ] -- X10ArrayTypeNode
        /.$BeginJava
                    setResult(nf.X10ArrayTypeNode(pos(), Type, false, null));
          $EndJava
        ./
     | Type value  [ . ]
        /.$BeginJava
                    setResult(nf.X10ArrayTypeNode(pos(), Type, true, null));
          $EndJava
        ./
     | Type [ DepParameterExpr ]
        /.$BeginJava
                    setResult(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
          $EndJava
        ./
     | Type value [ DepParameterExpr ]
        /.$BeginJava
                    setResult(nf.X10ArrayTypeNode(pos(), Type, true, DepParameterExpr));
          $EndJava
        ./

    -- Default is reference.
    ObjectKind ::= value
        /.$BadAction./
                 | reference
        /.$BadAction./


    ------------------------------------- Section ::: Classes
    MethodModifier ::= atomic
        /.$BeginJava
                    setResult(X10Flags.ATOMIC);
          $EndJava
        ./
                     | extern
        /.$BeginJava
                    setResult(Flags.NATIVE);
          $EndJava
        ./

    ClassDeclaration ::= ValueClassDeclaration

    ValueClassDeclaration ::= ClassModifiersopt value identifier PropertyListopt  Superopt Interfacesopt ClassBody
        /.$BeginJava
        checkTypeName(identifier);
        List/*<PropertyDecl>*/ props = PropertyListopt==null ? null : (List) PropertyListopt[0];
        Expr ci = PropertyListopt==null ? null : (Expr) PropertyListopt[1];
        setResult(nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
        ClassModifiersopt, identifier.getIdentifier(),
        props, ci, Superopt, Interfacesopt, ClassBody));
          $EndJava
        ./
      | ClassModifiersopt value class identifier PropertyListopt Superopt Interfacesopt ClassBody
        /.$BeginJava
                    checkTypeName(identifier);
        List/*<PropertyDecl>*/ props = PropertyListopt==null ? null : (List) PropertyListopt[0];
        Expr ci = PropertyListopt==null ? null : (Expr) PropertyListopt[1];
        setResult(nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                                  ClassModifiersopt, identifier.getIdentifier(),
                                  props, ci, Superopt, Interfacesopt, ClassBody));
          $EndJava
        ./

 ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
       /.$BeginJava
         Name a = (Name) ConstructorDeclarator[1];
         DepParameterExpr c = (DepParameterExpr) ConstructorDeclarator[2];
         List b = (List) ConstructorDeclarator[3];
         Expr e = (Expr) ConstructorDeclarator[4];
         setResult(nf.ConstructorDecl(pos(), ConstructorModifiersopt, a.toString(), c, b, e, Throwsopt, ConstructorBody));
         $EndJava
       ./

    ConstructorDeclarator ::=  SimpleTypeName DepParametersopt ( FormalParameterListopt WhereClauseopt )
       /.$BeginJava
                 Object[] a = new Object[5];
                 a[1] = SimpleTypeName;
                 a[2] = DepParametersopt;
                 a[3] = FormalParameterListopt;
                 a[4] = WhereClauseopt;
                 setResult(a);
         $EndJava
       ./
    ThisClause ::= this DepParameters

    MethodDeclarator ::= ThisClauseopt identifier ( FormalParameterListopt  WhereClauseopt )
       /.$BeginJava
                    // vj: TODO -- add processing of ThisClause, the this-restriction.
                    Object[] a = new Object[5];
                   a[0] = new Name(nf, ts, pos(), identifier.getIdentifier());
                    a[1] = FormalParameterListopt;
                   a[2] = new Integer(0);
                   a[3] = WhereClauseopt;
                   a[4] = ThisClauseopt;
                    setResult(a);
          $EndJava
        ./
                     | MethodDeclarator [ ]
        /.$BeginJava
                    MethodDeclarator[2] = new Integer(((Integer) MethodDeclarator[2]).intValue() + 1);
                    // setResult(MethodDeclarator);
         $EndJava
        ./

    FieldDeclaration ::= FieldModifiersopt ThisClauseopt Type VariableDeclarators ;
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    for (Iterator i = VariableDeclarators.iterator(); i.hasNext();)
                    {
                        X10VarDeclarator d = (X10VarDeclarator) i.next();
                        if (d.hasExplodedVars())
                          // TODO: Report this exception correctly.
                          throw new Error("Field Declarations may not have exploded variables." + pos());
                        d.setFlag(FieldModifiersopt);
                        l.add(nf.FieldDecl(d.position(),
                                           ThisClauseopt,
                                           d.flags,
                                           nf.array(Type, Type.position(), d.dims),
                                           d.name,
                                           d.init));
                    }
                    setResult(l);
          $EndJava
        ./

    --------------------------------------- Section ::: Arrays
    -- The dependent type array([D][:E])<T> is written as
    -- T[D:E].
    -- TODO: Support value 1D local arrays.
    ArrayCreationExpression ::=
--              new ArrayBaseType [ Expression ] -- Expression may be an int.
--        /.$BeginJava
--                    List l = new TypedList(new LinkedList(), Expr.class, false);
--                    l.add(Expression);
--                    setResult(nf.NewArray(pos(), ArrayBaseType, l));
--          $EndJava
--        ./
--       |
              new ArrayBaseType Unsafeopt Dims ArrayInitializer  -- produce a LocalArray
        /.$BeginJava
                    // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                    setResult(nf.NewArray(pos(), ArrayBaseType, Dims.intValue(), ArrayInitializer));
          $EndJava
        ./
        |     new ArrayBaseType Unsafeopt DimExpr Dims  -- produce a LocalArray
        /.$BeginJava
                    // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                    setResult(nf.NewArray(pos(), ArrayBaseType, Collections.singletonList(DimExpr), Dims.intValue()));
          $EndJava
        ./
        |     new ArrayBaseType Unsafeopt DimExpr DimExprs Dimsopt  -- produce a LocalArray
        /.$BeginJava
                    // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                    List l = new TypedList(new LinkedList(), Expr.class, false);
                    l.add(DimExpr);
                    l.addAll(DimExprs);
                    setResult(nf.NewArray(pos(), ArrayBaseType, l, Dimsopt.intValue()));
          $EndJava
        ./
        |     new ArrayBaseType Valueopt Unsafeopt [  Expression  ] -- Expression may be a distribution or an int
        /.$BeginJava
                    setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, null));
          $EndJava
        ./
        |     new ArrayBaseType Valueopt Unsafeopt [  Expression$distr  ] Expression$initializer
        /.$BeginJava
                    setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, distr, initializer));
          $EndJava
        ./
        |     new ArrayBaseType Valueopt Unsafeopt [  Expression  ] ($lparen FormalParameter ) MethodBody
        /.$BeginJava
                    New initializer = makeInitializer( pos(getRhsFirstTokenIndex($lparen), getRightSpan()), ArrayBaseType, FormalParameter, MethodBody );
                    setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, initializer));
          $EndJava
        ./

    Valueopt ::= $Empty
        /.$NullAction./
               | value
        /.$BeginJava
                    // any value distinct from null
                    setResult(this);
          $EndJava
        ./

    ArrayBaseType ::= PrimitiveType
                    | ClassOrInterfaceType
                    | nullable  Type
        /.$BeginJava
                    setResult(nf.Nullable(pos(), Type));
          $EndJava
        ./
            | future < Type >
        /.$BeginJava
                    setResult(nf.Future(pos(), Type));
          $EndJava
        ./
            | ( Type )
             /.$BeginJava
                    setResult(Type);
          $EndJava
        ./

    ArrayAccess ::= ExpressionName [ ArgumentList ]
        /.$BeginJava
                    if (ArgumentList.size() == 1)
                         setResult(nf.X10ArrayAccess1(pos(), ExpressionName.toExpr(), (Expr) ArgumentList.get(0)));
                    else setResult(nf.X10ArrayAccess(pos(), ExpressionName.toExpr(), ArgumentList));
          $EndJava
        ./
                  | PrimaryNoNewArray [ ArgumentList ]
        /.$BeginJava
                    if (ArgumentList.size() == 1)
                         setResult(nf.X10ArrayAccess1(pos(), PrimaryNoNewArray, (Expr) ArgumentList.get(0)));
                    else setResult(nf.X10ArrayAccess(pos(), PrimaryNoNewArray, ArgumentList));
          $EndJava
        ./

    --------------------------------------- Section :: Statement
    Statement ::= NowStatement
                | AsyncStatement
                | AtomicStatement
                | WhenStatement
                | ForEachStatement
                | AtEachStatement
                | FinishStatement

    StatementWithoutTrailingSubstatement ::= NextStatement
                                           | AwaitStatement

    StatementNoShortIf ::= NowStatementNoShortIf
                         | AsyncStatementNoShortIf
                         | AtomicStatementNoShortIf
                         | WhenStatementNoShortIf
                         | ForEachStatementNoShortIf
                         | AtEachStatementNoShortIf
                         | FinishStatementNoShortIf

    NowStatement ::= now ( Clock ) Statement
        /.$BeginJava
                  setResult(nf.Now(pos(), Clock, Statement));
          $EndJava
        ./

    ClockedClause ::= clocked ( ClockList )
        /.$BeginJava
                    setResult(ClockList);
          $EndJava
        ./

    AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
        /.$BeginJava
                  setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                            ? nf.Here(pos(getLeftSpan()))
                                                                            : PlaceExpressionSingleListopt),
                                             ClockedClauseopt, Statement));
          $EndJava
        ./


    AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
        /.$BeginJava
                  setResult(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                                   ? nf.Here(pos(getLeftSpan()))
                                                   : PlaceExpressionSingleListopt), Statement));
          $EndJava
        ./


    WhenStatement  ::= when ( Expression ) Statement
        /.$BeginJava
                    setResult(nf.When(pos(), Expression, Statement));
          $EndJava
        ./
                     | WhenStatement or$or ( Expression ) Statement
        /.$BeginJava
                  WhenStatement.addBranch(pos(getRhsFirstTokenIndex($or), getRightSpan()), Expression, Statement);
                  setResult(WhenStatement);
          $EndJava
        ./

    ForEachStatement ::= foreach ( FormalParameter : Expression ) ClockedClauseopt Statement
        /.$BeginJava
                    setResult(nf.ForEach(pos(),
                                  FormalParameter.flags(FormalParameter.flags().Final()),
                                  Expression,
                                  ClockedClauseopt,
                                  Statement));
          $EndJava
        ./

    AtEachStatement ::= ateach ( FormalParameter : Expression ) ClockedClauseopt Statement
        /.$BeginJava
                    setResult(nf.AtEach(pos(),
                                 FormalParameter.flags(FormalParameter.flags().Final()),
                                 Expression,
                                 ClockedClauseopt,
                                 Statement));
          $EndJava
        ./

    EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
        /.$BeginJava
                    setResult(nf.ForLoop(pos(),
                            FormalParameter.flags(FormalParameter.flags().Final()),
                            Expression,
                            Statement));
          $EndJava
        ./

    FinishStatement ::= finish Statement
        /.$BeginJava
                    setResult(nf.Finish(pos(),  Statement));
          $EndJava
        ./


    NowStatementNoShortIf ::= now ( Clock ) StatementNoShortIf
        /.$BeginJava
                    setResult(nf.Now(pos(), Clock, StatementNoShortIf));
          $EndJava
        ./

    AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt ClockedClauseopt StatementNoShortIf
        /.$BeginJava
                    setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                    ? nf.Here(pos(getLeftSpan()))
                                                    : PlaceExpressionSingleListopt),
                                                ClockedClauseopt, StatementNoShortIf));
          $EndJava
        ./

    AtomicStatementNoShortIf ::= atomic StatementNoShortIf
        /.$BeginJava
                    setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), StatementNoShortIf));
          $EndJava
        ./

    WhenStatementNoShortIf  ::= when ( Expression ) StatementNoShortIf
        /.$BeginJava
                    setResult(nf.When(pos(), Expression, StatementNoShortIf));
          $EndJava
        ./
                              | WhenStatement or$or ( Expression ) StatementNoShortIf
        /.$BeginJava
                    WhenStatement.addBranch(pos(getRhsFirstTokenIndex($or), getRightSpan()), Expression, StatementNoShortIf);
                    setResult(WhenStatement);
          $EndJava
        ./

    ForEachStatementNoShortIf ::= foreach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
        /.$BeginJava
                    setResult(nf.ForEach(pos(),
                                 FormalParameter.flags(FormalParameter.flags().Final()),
                                 Expression,
                                 ClockedClauseopt,
                                 StatementNoShortIf));

          $EndJava
        ./

    AtEachStatementNoShortIf ::= ateach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
        /.$BeginJava
                    setResult(nf.AtEach(pos(),
                                FormalParameter.flags(FormalParameter.flags().Final()),
                                Expression,
                                ClockedClauseopt,
                                StatementNoShortIf));
          $EndJava
        ./

    EnhancedForStatementNoShortIf ::= for ( FormalParameter : Expression ) StatementNoShortIf
        /.$BeginJava
                      setResult(nf.ForLoop(pos(),
                                 FormalParameter.flags(FormalParameter.flags().Final()),
                                 Expression,
                                 StatementNoShortIf));
          $EndJava
        ./

    FinishStatementNoShortIf ::= finish StatementNoShortIf
        /.$BeginJava
                    setResult(nf.Finish(pos(), StatementNoShortIf));
          $EndJava
        ./


    PlaceExpressionSingleList ::= ( PlaceExpression )
        /.$BeginJava
                  setResult(PlaceExpression);
          $EndJava
        ./

    PlaceExpression ::= Expression

    NextStatement ::= next ;
        /.$BeginJava
                    setResult(nf.Next(pos()));
          $EndJava
        ./

    AwaitStatement ::= await Expression ;
        /.$BeginJava
                    setResult(nf.Await(pos(), Expression));
          $EndJava
        ./

    ClockList ::= Clock
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Expr.class, false);
                    l.add(Clock);
                    setResult(l);
          $EndJava
        ./
                | ClockList , Clock
        /.$BeginJava
                    ClockList.add(Clock);
                    setResult(ClockList);
          $EndJava
        ./

    -- The type-checker will ensure that the identifier names a variable declared as a clock.
    Clock ::= Expression
--
--      Clock ::= identifier
--        /.$BeginJava
--                    setResult(new Name(nf, ts, pos(), identifier.getIdentifier()).toExpr());
--          $EndJava
--        ./

    CastExpression ::= ( Type ) UnaryExpressionNotPlusMinus
        /.$BeginJava
                    setResult(nf.Cast(pos(), Type, UnaryExpressionNotPlusMinus));
          $EndJava
        ./
       | ( @ Expression ) UnaryExpressionNotPlusMinus
        /.$BeginJava
                    setResult(nf.PlaceCast(pos(), Expression, UnaryExpressionNotPlusMinus));
          $EndJava
        ./

--    MethodInvocation ::= Primary '->' identifier ( ArgumentListopt )
--        /.$BeginJava
--                     setResult(nf.RemoteCall(pos(), Primary, identifier.getIdentifier(), ArgumentListopt));
--          $EndJava
--        ./

    RelationalExpression ::= RelationalExpression instanceof Type
        /.$BeginJava
                    setResult(nf.Instanceof(pos(), RelationalExpression, Type));
          $EndJava
        ./

     --------------------------------------- Section :: Expression
     -- A list of identifiers containing at least one identifier.
     IdentifierList ::= identifier
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Name.class, false);
                    l.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                    setResult(l);
          $EndJava
        ./
                      | IdentifierList , identifier
        /.$BeginJava
                    IdentifierList.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                    setResult(IdentifierList);
          $EndJava
        ./

    Primary ::= here
        /.$BeginJava
                    setResult(((X10NodeFactory) nf).Here(pos()));
//
// A "here" expression used to be treated as an ExpressionName instead
// of as a primary.
//
//                    setResult(new Name(nf, ts, pos(), "here"){
//                                public Expr toExpr() {
//                                  return ((X10NodeFactory) nf).Here(pos);
//                                }
//                             });
          $EndJava
        ./

    Primary ::= FutureExpression

    RegionExpression ::= Expression
                       | Expression$expr1 : Expression$expr2
        /.$BeginJava
                    Name x10 = new Name(nf, ts, pos(), "x10");
                    Name x10Lang = new Name(nf, ts, pos(), x10, "lang");

                    Name x10LangRegion = new Name(nf, ts, pos(), x10Lang, "region");
                    Name x10LangRegionFactory = new Name(nf, ts, pos(), x10LangRegion, "factory");
                    Name x10LangRegionFactoryRegion = new Name(nf, ts, pos(), x10LangRegionFactory, "region");
                    List l = new TypedList(new LinkedList(), Expr.class, false);
                    l.add(expr1);
                    l.add(expr2);
                    Call regionCall = nf.Call( pos(), x10LangRegionFactoryRegion.prefix.toReceiver(), "region", l  );
                    setResult(regionCall);
          $EndJava
        ./

    RegionExpressionList ::= RegionExpression
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Expr.class, false);
                    l.add(RegionExpression);
                    setResult(l);
          $EndJava
        ./
               | RegionExpressionList , RegionExpression
        /.$BeginJava
                    RegionExpressionList.add(RegionExpression);
                    //setResult(RegionExpressionList);
          $EndJava
        ./

    Primary ::= [ RegionExpressionList ]
        /.$BeginJava
                    Name x10 = new Name(nf, ts, pos(), "x10");
                    Name x10Lang = new Name(nf, ts, pos(), x10, "lang");
                    Name x10LangRegion = new Name(nf, ts, pos(), x10Lang, "region");
                    Name x10LangRegionFactory = new Name(nf, ts, pos(), x10LangRegion, "factory");
                    Name x10LangRegionFactoryRegion = new Name(nf, ts, pos(), x10LangRegionFactory, "region");
                    Name x10LangPoint = new Name(nf, ts, pos(), x10Lang, "point");
                    Name x10LangPointFactory = new Name(nf, ts, pos(), x10LangPoint, "factory");
                    Name x10LangPointFactoryPoint = new Name(nf, ts, pos(), x10LangPointFactory, "point");

                    Tuple tuple  = nf.Tuple(pos(), x10LangPointFactoryPoint, x10LangRegionFactoryRegion, RegionExpressionList);
                    setResult(tuple);
          $EndJava
        ./

    AssignmentExpression ::= Expression$expr1 '->' Expression$expr2
        /.$BeginJava
                    //System.out.println("Distribution:" + a + "|" + b + "|");
                    // x10.lang.region.factory.region(  ArgumentList )
                    // Construct the MethodName
                    Name x10 = new Name(nf, ts, pos(), "x10");
                    Name x10Lang = new Name(nf, ts, pos(), x10, "lang");

                    Name x10LangDistribution = new Name(nf, ts, pos(), x10Lang, "dist");
                    Name x10LangDistributionFactory = new Name(nf, ts, pos(), x10LangDistribution, "factory");
                    Name x10LangDistributionFactoryConstant = new Name(nf, ts, pos(), x10LangDistributionFactory, "constant");
                    List l = new TypedList(new LinkedList(), Expr.class, false);
                    l.add(expr1);
                    l.add(expr2);
                    Call call = nf.Call(pos(), x10LangDistributionFactoryConstant.prefix.toReceiver(), "constant", l);
                    setResult(call);
          $EndJava
        ./

    FutureExpression ::= future PlaceExpressionSingleListopt { Expression }
        /.$BeginJava
                    setResult(nf.Future(pos(), (PlaceExpressionSingleListopt == null
                                                    ? nf.Here(pos(getLeftSpan()))
                                                    : PlaceExpressionSingleListopt), Expression));
          $EndJava
        ./

    FieldModifier ::= mutable
        /.$BeginJava
                    setResult(X10Flags.MUTABLE);
          $EndJava
        ./
                    | const
        /.$BeginJava
                    setResult(Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL));
          $EndJava
        ./

    FunExpression ::= fun Type ( FormalParameterListopt ) { Expression }
        /.$BadAction./
    -- Simplified syntax to create a point.


    MethodInvocation ::= MethodName ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
       /.$BadAction ./
                       | Primary . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
       /.$BadAction ./
                       | super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
       /.$BadAction ./
                       | ClassName . super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
       /.$BadAction ./
                       | TypeName . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
       /.$BadAction ./

    ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
       /.$BadAction ./
                                      | Primary . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
       /.$BadAction ./
                                      | AmbiguousName . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
       /.$BadAction ./


    MethodModifier ::= synchronized
        /.$BeginAction
                    unrecoverableSyntaxError = true;
                    eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                       "\"synchronized\" is an invalid X10 Method Modifier");
                    setResult(Flags.SYNCHRONIZED);
          $EndAction
        ./

    FieldModifier ::= volatile
        /.$BeginAction
                    unrecoverableSyntaxError = true;
                    eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                       "\"volatile\" is an invalid X10 Field Modifier");
                    setResult(Flags.VOLATILE);
          $EndAction
        ./

    SynchronizedStatement ::= synchronized ( Expression ) Block
        /.$BeginJava
                    unrecoverableSyntaxError = true;
                    eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                       "Synchronized Statement is invalid in X10");
                    setResult(nf.Synchronized(pos(), Expression, Block));
          $EndJava
        ./

    ---------------------------------------- All the opts...

ThisClauseopt ::= $Empty
       /.$NullAction./
                            | ThisClause

    PlaceTypeSpecifieropt ::= $Empty
       /.$NullAction./
                            | PlaceTypeSpecifier

    DepParametersopt ::= $Empty
        /.$NullAction./
                       | DepParameters
    PropertyListopt ::=  $Empty
        /.$NullAction./
                       | PropertyList

    WhereClauseopt ::= $Empty
        /.$NullAction./
                     | WhereClause

    ObjectKindopt ::= $Empty
        /.$NullAction./
                    | ObjectKind

    ArrayInitializeropt ::= $Empty
        /.$NullAction./
                          | ArrayInitializer

    PlaceExpressionSingleListopt ::= $Empty
        /.$NullAction./
                                   | PlaceExpressionSingleList

    ArgumentListopt ::= $Empty
        /.$NullAction./
                      | ArgumentList

    DepParametersopt ::= $Empty
        /.$NullAction./
                       | DepParameters

    Unsafeopt ::= $Empty
        /.$NullAction./
                | unsafe
        /.$BeginJava
                    // any value distinct from null
                    setResult(this);
          $EndJava
        ./

    ParamIdopt ::= $Empty
        /.$NullAction./
                 | identifier
        /.$BeginJava
                    setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
          $EndJava
        ./

    ClockedClauseopt ::= $Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Expr.class, false));
          $EndJava
        ./
                       | ClockedClause
$End

$Types
    Object ::= ,opt
             | ...opt
             | PlaceTypeSpecifieropt
             | PlaceTypeSpecifier
             | PlaceType
             | ObjectKind
             | ObjectKindopt
             | FunExpression

    SourceFile ::= CompilationUnit
    polyglot.ast.Lit ::= Literal
    TypeNode ::= ThisClause | ThisClauseopt
    TypeNode ::= Type
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
    Flags ::= AbstractMethodModifier
            | AbstractMethodModifiers
            | AbstractMethodModifiersopt
    Flags ::= ClassModifier
            | ClassModifiers
            | ClassModifiersopt
    Flags ::= ConstantModifier
            | ConstantModifiers
            | ConstantModifiersopt
    Flags ::= ConstructorModifier
            | ConstructorModifiers
            | ConstructorModifiersopt
    Flags ::= FieldModifier
            | FieldModifiers
            | FieldModifiersopt
    Flags ::= InterfaceModifier
            | InterfaceModifiers
            | InterfaceModifiersopt
    Flags ::= MethodModifier
            | MethodModifiers
            | MethodModifiersopt
    Flags ::= VariableModifier
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
    MethodDecl ::= MethodDeclaration | MethodHeader
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
    Expr ::= ClassInstanceCreationExpression
    List ::= ArgumentListopt | ArgumentList
    NewArray ::= ArrayCreationExpression
    List ::= DimExprs
    Expr ::= DimExpr
    Integer ::= Dimsopt | Dims
    Field ::= FieldAccess
    Call ::= MethodInvocation
    ArrayAccess ::= ArrayAccess
    Expr ::= PostfixExpression
    Unary ::= PostIncrementExpression | PostDecrementExpression
    Expr ::= UnaryExpression | UnaryExpressionNotPlusMinus
    Unary ::= PreIncrementExpression | PreDecrementExpression
    Cast ::= CastExpression
    Expr ::= MultiplicativeExpression | AdditiveExpression
    Expr ::= ShiftExpression | RelationalExpression | EqualityExpression
    Expr ::= AndExpression | ExclusiveOrExpression | InclusiveOrExpression
    Expr ::= ConditionalAndExpression | ConditionalOrExpression
    Expr ::= ConditionalExpression | AssignmentExpression
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
    Object[] ::= MethodDeclarator
    Formal ::= LastFormalParameter
    List ::= FormalParameters
    List ::= ExceptionTypeList
    TypeNode ::= ExceptionType
    Object[] ::= ConstructorDeclarator
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
    Future ::= FutureExpression
    polyglot.lex.Identifier ::= ParamIdopt
    DepParameterExpr ::= DepParametersopt
                       | DepParameters
                       | DepParameterExpr
    List ::= Properties
    Object[] ::=  PropertyList | PropertyListopt
    PropertyDecl ::= Property
$End
