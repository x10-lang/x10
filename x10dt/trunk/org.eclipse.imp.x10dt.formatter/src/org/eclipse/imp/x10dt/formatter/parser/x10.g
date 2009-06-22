%options fp=X10Parser,states
%options list
%options la=6
%options variables=nt
%options conflicts
%options softkeywords
%options package=x10.parser
%options template=btParserTemplateF.gi
%options import_terminals="X10Lexer.gi"

%include
    "MissingId.gi"
%End

%Notice
/.
//#line $next_line "$input_file$"
//
// Licensed Material
// (C) Copyright IBM Corp, 2006
//
./
%End

%Globals
    /.
    //#line $next_line "$input_file$"
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.Iterator;
    import java.util.LinkedList;
    import java.util.List;
    import java.util.Arrays;
    import java.io.File;

    import polyglot.ext.x10.ast.X10Binary_c;
    import polyglot.ext.x10.ast.X10Unary_c;
    import polyglot.types.QName;
    import polyglot.types.Name;
    import polyglot.ast.AmbExpr;
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
    import polyglot.ast.ConstructorCall;
    import polyglot.ast.ConstructorDecl;
    import polyglot.ast.Eval;
    import polyglot.ast.Expr;
    import polyglot.ast.Field;
    import polyglot.ast.FloatLit;
    import polyglot.ast.ForInit;
    import polyglot.ast.ForUpdate;
    import polyglot.ast.Formal;
    import polyglot.ast.Id;
    import polyglot.ast.Import;
    import polyglot.ast.Initializer;
    import polyglot.ast.IntLit;
    import polyglot.ast.LocalDecl;
    import polyglot.ast.MethodDecl;
    import polyglot.ast.FieldDecl;
    import polyglot.ast.New;
    import polyglot.ast.NodeFactory;
    import polyglot.ast.PackageNode;
    import polyglot.ast.Receiver;
    import polyglot.ast.SourceFile;
    import polyglot.ast.Stmt;
    import polyglot.ast.SwitchElement;
    import polyglot.ast.TopLevelDecl;
    import polyglot.ast.TypeNode;
    import polyglot.ast.Unary;
    import polyglot.ast.FlagsNode;
    import polyglot.parse.ParsedName;
    import polyglot.ext.x10.ast.AnnotationNode;
    import polyglot.ext.x10.ast.Closure;
    import polyglot.ext.x10.ast.ClosureCall;
    import polyglot.ext.x10.ast.Here;
    import polyglot.ext.x10.ast.DepParameterExpr;
    import polyglot.ext.x10.ast.Tuple;
    import polyglot.ext.x10.ast.When;
    import polyglot.ext.x10.ast.X10Formal;
    import polyglot.ext.x10.ast.X10Formal_c;
    import polyglot.ext.x10.ast.X10Loop;
    import polyglot.ext.x10.ast.X10Call;
    import polyglot.ext.x10.ast.ConstantDistMaker;
    import polyglot.ext.x10.ast.TypeDecl;
    import polyglot.ext.x10.ast.TypeParamNode;
    import polyglot.ext.x10.ast.TypePropertyNode;
    import polyglot.ext.x10.ast.X10NodeFactory;
    import polyglot.ext.x10.types.TypeProperty;
    import polyglot.ext.x10.types.X10TypeSystem;
    import polyglot.ext.x10.types.X10TypeSystem_c;
    import polyglot.ext.x10.ast.PropertyDecl;
    import polyglot.ext.x10.ast.RegionMaker;
    import polyglot.ext.x10.extension.X10Ext;
    import polyglot.frontend.FileSource;
    import polyglot.frontend.Source;
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
    import polyglot.types.SemanticException;
    import polyglot.types.Type;
    import polyglot.types.TypeSystem;
    import polyglot.util.ErrorInfo;
    import polyglot.util.ErrorQueue;
    import polyglot.util.InternalCompilerError;
    import polyglot.util.Position;
    import polyglot.util.TypedList;
    import polyglot.util.CollectionUtil;

    import lpg.runtime.BacktrackingParser;
    import lpg.runtime.BadParseException;
    import lpg.runtime.BadParseSymFileException;
    import lpg.runtime.DiagnoseParser;
    import lpg.runtime.LexStream;
    import lpg.runtime.NotBacktrackParseTableException;
    import lpg.runtime.NullExportedSymbolsException;
    import lpg.runtime.NullTerminalSymbolsException;
    import lpg.runtime.ParseTable;
    import lpg.runtime.PrsStream;
    import lpg.runtime.RuleAction;
    import lpg.runtime.UndefinedEofSymbolException;
    import lpg.runtime.UnimplementedTerminalsException;
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
    $additional_interfaces /., Parser, ParseErrorCodes./
%End

%Identifier
    IDENTIFIER
%End

%Start
    CompilationUnit
%End

%SoftKeywords
    abstract
    as
    assert
    async
    at
    ateach
    atomic
    await
    break
    case
    catch
    class
    clocked
    const
    continue
    def
    default
    do
    else
    extends
    extern
    false
    final
    finally
    finish
    for
    foreach
    goto
    has
    here
    if
    implements
    import
    incomplete
    instanceof
    interface
    local
    native
    new
    next
    nonblocking
    now
    null
    or
    package
    private
    protected
    property
    public
    return
    safe
    self
    sequential
    shared
    static
    strictfp
--    super
    switch
--    this
    throw
    throws
    transient
    true
    try
    type
    unsafe
    val
    value
    var
    volatile
    when
    while
%End

%Headers
    /.
        //#line $next_line "$input_file$"
        private ErrorQueue eq;
        private X10TypeSystem ts;
        private X10NodeFactory nf;
        private Source source;//BRT
        private boolean unrecoverableSyntaxError = false;

        public void initialize(TypeSystem t, NodeFactory n, Source source, ErrorQueue q)
        {
            this.ts = (X10TypeSystem) t;
            this.nf = (X10NodeFactory) n;
            this.source = source;
            this.eq = q;
        }
        
        public $action_type(ILexStream lexStream, TypeSystem t, NodeFactory n, Source source, ErrorQueue q)
        {
            this(lexStream);
            initialize((X10TypeSystem) t,
                       (X10NodeFactory) n,
                       source,
                       q);
            prsStream.setMessageHandler(new MessageHandler(q));
        }

public static class MessageHandler implements IMessageHandler {
    ErrorQueue eq;

    public MessageHandler(ErrorQueue eq) {
        this.eq = eq;
    }

    public void handleMessage(int errorCode, int[] msgLocation,
                              int[] errorLocation, String filename,
                              String[] errorInfo) {

        File file = new File(filename);

        int l0 = msgLocation[2];
        int c0 = msgLocation[3];
        int l1 = msgLocation[4];
        int c1 = msgLocation[5];
        int o0 = msgLocation[0];
        int o1 = msgLocation[0] + msgLocation[1];

        Position pos = new Position(file.getPath(),
                    file.getPath(), l0, c0, l1, c1+1, o0, o1);

        String msg = "";
        String info = "";

        for (String s : errorInfo) {
            info += s;
        }

        switch (errorCode) {
            case LEX_ERROR_CODE:
                msg = "Unexpected character ignored: " + info;
                break;
            case ERROR_CODE:
                msg = "Parse terminated at this token: " + info;
                break;
            case BEFORE_CODE:
                msg = "Token " + info + " expected before this input";
                break;
            case INSERTION_CODE:
                msg = "Token " + info + " expected after this input";
                break;
            case INVALID_CODE:
                msg = "Unexpected input discarded: " + info;
                break;
            case SUBSTITUTION_CODE:
                msg = "Token " + info + " expected instead of this input";
                break;
            case DELETION_CODE:
                msg = "Unexpected input ignored: " + info;
                break;
            case MERGE_CODE:
                msg = "Merging token(s) to recover: " + info;
                break;
            case MISPLACED_CODE:
                msg = "Misplaced constructs(s): " + info;
                break;
            case SCOPE_CODE:
                msg = "Token(s) inserted to complete scope: " + info;
                break;
            case EOF_CODE:
                msg = "Reached after this token: " + info;
                break;
            case INVALID_TOKEN_CODE:
                msg = "Invalid token: " + info;
                break;
            case ERROR_RULE_WARNING_CODE:
                msg = "Ignored token: " + info;
                break;
            case NO_MESSAGE_CODE:
                msg = "Syntax error";
                break;
        }

        eq.enqueue(ErrorInfo.SYNTAX_ERROR, msg, pos);
    }
    }
    
        public String getErrorLocation(int lefttok, int righttok)
        {
            return prsStream.getFileName() + ':' +
                   prsStream.getLine(lefttok) + ":" + prsStream.getColumn(lefttok) + ":" +
                   prsStream.getEndLine(righttok) + ":" + prsStream.getEndColumn(righttok) + ": ";
        }

        public Position getErrorPosition(int lefttok, int righttok)
        {
            return new Position(null, prsStream.getFileName(),
                   prsStream.getLine(lefttok), prsStream.getColumn(lefttok),
                   prsStream.getEndLine(righttok), prsStream.getEndColumn(righttok));
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
                      rightToken.getEndLine(), rightToken.getEndColumn(),
                      leftToken.getStartOffset(), rightToken.getEndOffset());
                this.leftIToken = null; // BRT -- was null, need to keep leftToken for later reference
                this.rightIToken = null;  // BRT -- was null, need to keep rightToken for later reference
            }

            public IToken getLeftIToken() { return leftIToken; }
            public IToken getRightIToken() { return rightIToken; }

            public String toText()
            {
                if (leftIToken == null) return "...";
                IPrsStream prsStream = leftIToken.getPrsStream();
                return new String(prsStream.getInputChars(), offset(), endOffset() - offset() + 1);
            }
        }
        
        public void syntaxError(String msg, Position pos) {
                    unrecoverableSyntaxError = true;
                    eq.enqueue(ErrorInfo.SYNTAX_ERROR, msg, pos);
                }   
        

        public $ast_class parse() {
            try
            {
                SourceFile sf = (SourceFile) parser();

                if (sf != null)
                {
                    if (! unrecoverableSyntaxError)
                        return sf.source(source);
                    eq.enqueue(ErrorInfo.SYNTAX_ERROR, "Unable to parse " + source.name() + ".", new Position(null, file(), 1, 1, 1, 1));
                }   
            }
            catch (RuntimeException e) {
                // Let the Compiler catch and report it.
                throw e;
            }
            catch (Exception e) {
                // Used by cup to indicate a non-recoverable error.
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, e.getMessage(), new Position(null, file(), 1, 1, 1, 1));
            }

            return null;
        }

        public String file()
        {
            return prsStream.getFileName();
        }

        public JPGPosition pos()
        {
            return new JPGPosition("",
                                   prsStream.getFileName(),
                                   prsStream.getIToken(getLeftSpan()),
                                   prsStream.getIToken(getRightSpan()));
        }

        public JPGPosition pos(int i)
        {
            return new JPGPosition("",
                                   prsStream.getFileName(),
                                   prsStream.getIToken(i),
                                   prsStream.getIToken(i));
        }

        public JPGPosition pos(int i, int j)
        {
            return new JPGPosition("",
                                   prsStream.getFileName(),
                                   prsStream.getIToken(i),
                                   prsStream.getIToken(j));
        }

        /**
         * Return the source position of the declaration.
         */
        public JPGPosition pos (VarDeclarator n)
        {
          if (n == null) return null;
          return (JPGPosition) n.pos;
        }

        public JPGPosition pos(JPGPosition start, JPGPosition end) {
            return new JPGPosition(start.path(), start.file(), start.leftIToken, end.rightIToken);
        }

        private void checkTypeName(Id identifier) {
            String filename = file();
            String idname = identifier.id().toString();
            int dot = filename.lastIndexOf('.'),
                slash = filename.lastIndexOf('/', dot);
            if (slash == -1)
                slash = filename.lastIndexOf('\\', dot);
            String clean_filename = (slash >= 0 && dot >= 0 ? filename.substring(slash+1, dot) : "");
            if ((! clean_filename.equals(idname)) && clean_filename.equalsIgnoreCase(idname))
                eq.enqueue(ErrorInfo.SYNTAX_ERROR,
                           "This type name does not match the name of the containing file: " + filename.substring(slash+1),
                           identifier.position());
       }


        private polyglot.lex.Operator op(int i) {
            return new Operator(pos(i), prsStream.getName(i), prsStream.getKind(i));
        }

        private polyglot.lex.Identifier id(int i) {
            return new Identifier(pos(i), prsStream.getName(i), $sym_type.TK_IDENTIFIER);
        }
        private String comment(int i) {
            String s = prsStream.getName(i);
            if (s != null && s.startsWith("/**") && s.endsWith("*/")) {
                return s +"\n";
            }
            return null;
        }

        private List<Formal> toFormals(List<Formal> l) { return l; }

        private List<Expr> toActuals(List<Formal> l) {
            List<Expr> l2 = new ArrayList<Expr>();
            for (Formal f : l) {
                l2.add(nf.Local(f.position(), f.name()));
            }
            return l2;
        }

        private List<TypeParamNode> toTypeParams(List<TypeParamNode> l) { return l; }

        private List<TypeNode> toTypeArgs(List<TypeParamNode> l) {
            List<TypeNode> l2 = new ArrayList<TypeNode>();
            for (TypeParamNode f : l) {
                l2.add(nf.AmbTypeNode(f.position(), null, f.name()));
            }
            return l2;
        }

                
        private List extractAnnotations(List l) {
            List l2 = new LinkedList();
            for (Iterator i = l.iterator(); i.hasNext(); ) {
                Object o = i.next();
                if (o instanceof AnnotationNode) {
                    l2.add((AnnotationNode) o);
                }
            }
            return l2;
        }
    
        private FlagsNode extractFlags(List l, Flags f) {
            FlagsNode fn = extractFlags(l);
            fn = fn.flags(fn.flags().set(f));
            return fn;
        }
        
        private FlagsNode extractFlags(List l1, List l2) {
            List l = new ArrayList();
            l.addAll(l1);
            l.addAll(l2);
            return extractFlags(l);
        }
        
        private FlagsNode extractFlags(List l) {
            Position pos = null;
            X10Flags xf = X10Flags.toX10Flags(Flags.NONE);
            for (Iterator i = l.iterator(); i.hasNext(); ) {
                Object o = i.next();
                if (o instanceof FlagsNode) {
                    FlagsNode fn = (FlagsNode) o;
                    pos = pos == null ? fn.position() : new Position(pos, fn.position());
                    Flags f = fn.flags();
                    if (f instanceof X10Flags) {
                        xf = xf.setX((X10Flags) f);
                    }
                    else {
                        xf = X10Flags.toX10Flags(xf.set(f));
                    }
                }
            }
            return nf.FlagsNode(pos == null ? Position.COMPILER_GENERATED : pos, xf);
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

        private polyglot.lex.LongLiteral int_lit(int i, int radix)
        {
            long x = parseLong(prsStream.getName(i), radix);
            return new LongLiteral(pos(i),  x, $sym_type.TK_IntegerLiteral);
        }

        private polyglot.lex.LongLiteral int_lit(int i)
        {
            long x = parseLong(prsStream.getName(i));
            return new LongLiteral(pos(i),  x, $sym_type.TK_IntegerLiteral);
        }

        private polyglot.lex.LongLiteral long_lit(int i, int radix)
        {
            long x = parseLong(prsStream.getName(i), radix);
            return new LongLiteral(pos(i), x, $sym_type.TK_LongLiteral);
        }

        private polyglot.lex.LongLiteral long_lit(int i)
        {
            long x = parseLong(prsStream.getName(i));
            return new LongLiteral(pos(i), x, $sym_type.TK_LongLiteral);
        }

        private polyglot.lex.FloatLiteral float_lit(int i)
        {
            try {
                String s = prsStream.getName(i);
                int end_index = (s.charAt(s.length() - 1) == 'f' || s.charAt(s.length() - 1) == 'F'
                                                           ? s.length() - 1
                                                           : s.length());
                float x = Float.parseFloat(s.substring(0, end_index));
                return new FloatLiteral(pos(i), x, $sym_type.TK_FloatingPointLiteral);
            }
            catch (NumberFormatException e) {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                           "Illegal float literal \"" + prsStream.getName(i) + "\"", pos(i));
                return null;
            }
        }

        private polyglot.lex.DoubleLiteral double_lit(int i)
        {
            try {
                String s = prsStream.getName(i);
                int end_index = (s.charAt(s.length() - 1) == 'd' || s.charAt(s.length() - 1) == 'D'
                                                           ? s.length() - 1
                                                           : s.length());
                double x = Double.parseDouble(s.substring(0, end_index));
                return new DoubleLiteral(pos(i), x, $sym_type.TK_DoubleLiteral);
            }
            catch (NumberFormatException e) {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                           "Illegal float literal \"" + prsStream.getName(i) + "\"", pos(i));
                return null;
            }
        }

        private polyglot.lex.CharacterLiteral char_lit(int i)
        {
            char x;
            String s = prsStream.getName(i);
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
            return new BooleanLiteral(pos(i), prsStream.getKind(i) == $sym_type.TK_true, prsStream.getKind(i));
        }

        private polyglot.lex.StringLiteral string_lit(int i)
        {
            String s = prsStream.getName(i);
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
%End

%Rules
    TypeDefDeclaration ::= TypeDefModifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
        /.$BeginJava
                    FlagsNode f = extractFlags(TypeDefModifiersopt);
                    List annotations = extractAnnotations(TypeDefModifiersopt);
                    for (Formal v : (List<Formal>) FormalParametersopt) {
                        if (!v.flags().flags().isFinal()) throw new InternalCompilerError("Type definition parameters must be final.", v.position()); 
                    }
                    TypeDecl cd = nf.TypeDecl(pos(), f, Identifier, TypeParametersopt, FormalParametersopt, WhereClauseopt, Type);
                    cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
                    setResult(cd);
          $EndJava
        ./
                         | TypeDefModifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt ;
        /.$BeginJava
                    FlagsNode f = extractFlags(TypeDefModifiersopt);
                    List annotations = extractAnnotations(TypeDefModifiersopt);
                    for (Formal v : (List<Formal>) FormalParametersopt) {
                        if (!v.flags().flags().isFinal()) throw new InternalCompilerError("Type definition parameters must be final.", v.position()); 
                    }
                    TypeDecl cd = nf.TypeDecl(pos(), f, Identifier, TypeParametersopt, FormalParametersopt, WhereClauseopt, null);
                    cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
                    setResult(cd);
          $EndJava
        ./
        
    Properties ::= ( PropertyList )
      /.$BeginJava
       setResult(PropertyList);
     $EndJava ./

       PropertyList ::= Property
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                    l.add(Property);
                    setResult(l);
          $EndJava
        ./
                          | PropertyList , Property
        /.$BeginJava
                    PropertyList.add(Property);
          $EndJava
        ./
    
    
    Property ::=  Annotationsopt Identifier : Type
        /.$BeginJava
                    List annotations = extractAnnotations(Annotationsopt);
                    PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), Type, Identifier);
                    cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                    setResult(cd);
          $EndJava
        ./

    MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
        /.$BeginJava
           if (Identifier.id().toString().equals("this")) {
                       ConstructorDecl cd = nf.X10ConstructorDecl(pos(),
                                                 extractFlags(MethodModifiersopt),
                                                 nf.Id(pos(3), "this"),
                                                 ResultTypeopt,
                                                 TypeParametersopt,
                                                 FormalParameters,
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 MethodBody);
         cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(MethodModifiersopt));
         setResult(cd);
              }
              else {
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              Identifier,
              TypeParametersopt,
              FormalParameters,
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          }
          $EndJava
        ./
      | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
        /.$BeginJava
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              nf.Id(pos(getRhsFirstTokenIndex($BinOp)), X10Binary_c.binaryMethodName(BinOp)),
              TypeParametersopt,
              Arrays.<Formal>asList(fp1, fp2),
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          if (! md.flags().flags().isStatic())
              syntaxError("Binary operator with two parameters must be static.", md.position());
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./
      | MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
        /.$BeginJava
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              nf.Id(pos(getRhsFirstTokenIndex($PrefixOp)), X10Unary_c.unaryMethodName(PrefixOp)),
              TypeParametersopt,
              Collections.<Formal>singletonList(fp2),
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          if (! md.flags().flags().isStatic())
              syntaxError("Unary operator with two parameters must be static.", md.position());
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./
      | MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
        /.$BeginJava
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              nf.Id(pos(getRhsFirstTokenIndex($BinOp)), X10Binary_c.binaryMethodName(BinOp)),
              TypeParametersopt,
              Collections.<Formal>singletonList(fp2),
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          if (md.flags().flags().isStatic())
              syntaxError("Binary operator with this parameter cannot be static.", md.position());
              
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./
      | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt ResultTypeopt Throwsopt MethodBody
        /.$BeginJava
           Name op = X10Binary_c.invBinaryMethodName(BinOp);
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              nf.Id(pos(getRhsFirstTokenIndex($BinOp)), X10Binary_c.invBinaryMethodName(BinOp)),
              TypeParametersopt,
              Collections.<Formal>singletonList(fp1),
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          if (md.flags().flags().isStatic())
              syntaxError("Binary operator with this parameter cannot be static.", md.position());
              
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./
      | MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt ResultTypeopt Throwsopt MethodBody
        /.$BeginJava
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              nf.Id(pos(getRhsFirstTokenIndex($PrefixOp)), X10Unary_c.unaryMethodName(PrefixOp)),
              TypeParametersopt,
              Collections.EMPTY_LIST,
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          if (md.flags().flags().isStatic())
              syntaxError("Unary operator with this parameter cannot be static.", md.position());
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./
      | MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
        /.$BeginJava
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              nf.Id(pos(), Name.make("apply")),
              TypeParametersopt,
              FormalParameters,
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          if (md.flags().flags().isStatic())
              syntaxError("Apply operator cannot be static.", md.position());
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./
      | MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
        /.$BeginJava
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              nf.Id(pos(), Name.make("set")),
              TypeParametersopt,
              CollectionUtil.append(Collections.singletonList(fp2), FormalParameters),
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          if (md.flags().flags().isStatic())
              syntaxError("Set operator cannot be static.", md.position());
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./
      | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Throwsopt MethodBody
        /.$BeginJava
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt),
              Type,
              nf.Id(pos(), Name.make("$convert")),
              TypeParametersopt,
              Collections.<Formal>singletonList(fp1),
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          if (! md.flags().flags().isStatic())
              syntaxError("Conversion operator must be static.", md.position());
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./
      | MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt ResultTypeopt Throwsopt MethodBody
        /.$BeginJava
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              nf.Id(pos(), Name.make("$convert")),
              TypeParametersopt,
              Collections.<Formal>singletonList(fp1),
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          if (! md.flags().flags().isStatic())
              syntaxError("Conversion operator must be static.", md.position());
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./
        

    PropertyMethodDeclaration ::= MethodModifiersopt property Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
        /.$BeginJava
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt, X10Flags.PROPERTY),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              Identifier,
              TypeParametersopt,
              FormalParameters,
              WhereClauseopt,
              Throwsopt,
              MethodBody);
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./
                                | MethodModifiersopt property Identifier WhereClauseopt ResultTypeopt MethodBody
        /.$BeginJava
           MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex($MethodModifiersopt), getRhsLastTokenIndex($MethodBody)),
              extractFlags(MethodModifiersopt, X10Flags.PROPERTY),
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
              Identifier,
              Collections.EMPTY_LIST,
              Collections.EMPTY_LIST,
              WhereClauseopt,
              Collections.EMPTY_LIST,
              MethodBody);
          md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
          setResult(md);
          $EndJava
        ./

    ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
        /.$BeginJava
                    setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
          $EndJava
        ./
                                    | super TypeArgumentsopt ( ArgumentListopt ) ;
        /.$BeginJava
                    setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
          $EndJava
        ./
                                    | Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
        /.$BeginJava
                    setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
          $EndJava
        ./
                                    | Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
        /.$BeginJava
                    setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
          $EndJava
        ./

    NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypePropertiesopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
        /.$BeginJava
          checkTypeName(Identifier);
          List TypeParametersopt = Collections.EMPTY_LIST;
          List/*<PropertyDecl>*/ props = Propertiesopt;
          DepParameterExpr ci = WhereClauseopt;
          FlagsNode fn = extractFlags(InterfaceModifiersopt, Flags.INTERFACE);
          ClassDecl cd = nf.X10ClassDecl(pos(),
                       fn,
                       Identifier,
                       TypeParametersopt,
                       TypePropertiesopt,
                       props,
                       ci,
                       null,
                       ExtendsInterfacesopt,
                       InterfaceBody);
          cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(InterfaceModifiersopt));
          setResult(cd);
          $EndJava
        ./

    ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
                    if (ClassBodyopt == null)
                         setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                    else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
          $EndJava
        ./
                                      | Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
                    ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                    if (ClassBodyopt == null)
                         setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                    else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
          $EndJava
        ./
                                      | AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
        /.$BeginJava
                    ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                    if (ClassBodyopt == null)
                         setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                    else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
          $EndJava
        ./
                       
      AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
       /.$BeginJava
                    setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
          $EndJava
        ./

    -------------------------------------- Section:::Types

        
    Type ::= FunctionType
           | ConstrainedType
           
    FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt => Type
        /.$BeginJava
                    setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt));
          $EndJava
        ./

    ClassType ::= NamedType
    InterfaceType ::= FunctionType | NamedType | ( Type )
    
    AnnotatedType ::= Type Annotations
        /.$BeginJava
                    TypeNode tn = Type;
                    tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                    setResult(tn);
          $EndJava
        ./

    ConstrainedType ::=  NamedType
           | PathType
           | AnnotatedType
           | ( Type )
        /.$BeginJava
                    setResult(Type);
          $EndJava
        ./

    PlaceType ::= any
        /.$BeginJava
                    setResult(null);
          $EndJava
        ./
                | current 
        /.$BeginJava
                    setResult(nf.Binary(pos(),
                                        nf.Field(pos(), nf.This(pos()), nf.Id(pos(), "loc")), Binary.EQ,
                                        nf.Field(pos(), nf.Self(pos()), nf.Id(pos(), "$current"))));
          $EndJava
        ./
                | PlaceExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(),
                                        nf.Field(pos(), nf.This(pos()), nf.Id(pos(), "loc")), Binary.EQ,
                                        PlaceExpression));
          $EndJava
        ./

    NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt 
        /.$BeginJava
                TypeNode type = nf.X10AmbTypeNode(pos(), Primary, Identifier);
                // TODO: place constraint
                if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                    type = nf.AmbDepTypeNode(pos(), Primary, Identifier, TypeArgumentsopt, Argumentsopt, DepParametersopt);
                }
                setResult(type);
          $EndJava
        ./

    NamedType ::= TypeName TypeArgumentsopt Argumentsopt DepParametersopt 
        /.$BeginJava
                TypeNode type;
                
                if (TypeName.name.id().toString().equals("void")) {
                    type = nf.CanonicalTypeNode(pos(), ts.Void());
                } else
                if (ts.isPrimitiveTypeName(TypeName.name.id())) {
                    try {
                        type = nf.CanonicalTypeNode(pos(), ts.primitiveForName(TypeName.name.id()));
                    }
                    catch (SemanticException e) {
                        throw new InternalCompilerError("Unable to create primitive type for '" + TypeName.name.id() + "'!");
                    }
                } else {
                    type = TypeName.toType();
                }
                // TODO: place constraint
                if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                    type = nf.AmbDepTypeNode(pos(), TypeName.prefix != null ? TypeName.prefix.toPrefix() : null, TypeName.name, TypeArgumentsopt, Argumentsopt, DepParametersopt);
                }
                setResult(type);
          $EndJava
        ./
        
        
    DepParameters ::= { ExistentialListopt Conjunction }
         /.$BeginJava
                    setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
          $EndJava
        ./
                    | { ExistentialListopt Conjunction } !
         /.$BeginJava
                    setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
          $EndJava
        ./
                    | { ExistentialListopt Conjunction } ! PlaceType
         /.$BeginJava
                    if (PlaceType != null)
                        setResult(nf.DepParameterExpr(pos(), ExistentialListopt, nf.Binary(pos(), Conjunction, Binary.COND_AND, PlaceType)));
	            else
			setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
          $EndJava
        ./

    TypeProperties ::= '[' TypePropertyList ']'
        /.$BeginJava
                    setResult(TypePropertyList);
          $EndJava
        ./
        
    TypeParameters ::= '[' TypeParameterList ']'
        /.$BeginJava
                    setResult(TypeParameterList);
          $EndJava
        ./
    FormalParameters ::= ( FormalParameterListopt )
        /.$BeginJava
                    setResult(FormalParameterListopt);
          $EndJava
        ./

    Conjunction ::= Expression
        /.$BeginJava
                    setResult(Expression);
          $EndJava
        ./
                  | Conjunction , Expression
         /.$BeginJava
                    setResult(nf.Binary(pos(), Conjunction, Binary.COND_AND, Expression));
          $EndJava
        ./
        
    SubtypeConstraint ::= Type$t1 <: Type$t2
         /.$BeginJava
                    setResult(nf.SubtypeTest(pos(), t1, t2, false));
          $EndJava
        ./
                        | Type$t1 :> Type$t2
         /.$BeginJava
                    setResult(nf.SubtypeTest(pos(), t2, t1, false));
          $EndJava
        ./
                        
    WhereClause ::= DepParameters
            /.$BeginJava
                setResult(DepParameters);
          $EndJava
          ./

    ExistentialListopt ::= %Empty
          /.$BeginJava
                setResult(new ArrayList());
          $EndJava
          ./
          | ExistentialList ;
          /.$BeginJava
                setResult(ExistentialList);
          $EndJava
        ./

       ExistentialList ::= FormalParameter
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Formal.class, false);
                    l.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                    setResult(l);
          $EndJava
        ./
                          | ExistentialList ; FormalParameter
        /.$BeginJava
                    ExistentialList.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
          $EndJava
        ./


    ------------------------------------- Section ::: Classes
    ClassDeclaration ::= ValueClassDeclaration
                       | NormalClassDeclaration
        
    NormalClassDeclaration ::= ClassModifiersopt class Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
        /.$BeginJava
          checkTypeName(Identifier);
                    List TypeParametersopt = Collections.EMPTY_LIST;
          List/*<PropertyDecl>*/ props = Propertiesopt;
          DepParameterExpr ci = WhereClauseopt;
          FlagsNode f = extractFlags(ClassModifiersopt);
          List annotations = extractAnnotations(ClassModifiersopt);
          ClassDecl cd = nf.X10ClassDecl(pos(),
                  f, Identifier, TypeParametersopt, TypePropertiesopt, props, ci, Superopt, Interfacesopt, ClassBody);
          cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(annotations);
          setResult(cd);
          $EndJava
        ./

    ValueClassDeclaration ::= ClassModifiersopt value Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
        /.$BeginJava
        checkTypeName(Identifier);
                    List TypeParametersopt = Collections.EMPTY_LIST;
        List props = Propertiesopt;
        DepParameterExpr ci = WhereClauseopt;
        ClassDecl cd = (nf.X10ClassDecl(pos(getLeftSpan(), getRightSpan()),
        extractFlags(ClassModifiersopt, X10Flags.VALUE), Identifier,  TypeParametersopt,
        TypePropertiesopt, props, ci, Superopt, Interfacesopt, ClassBody));
        cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ClassModifiersopt));
        setResult(cd);
          $EndJava
        ./

    ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt ConstructorBody
       /.$BeginJava
         ConstructorDecl cd = nf.X10ConstructorDecl(pos(),
                                                 extractFlags(ConstructorModifiersopt),
                                                 nf.Id(pos(3), "this"),
                                                 ResultTypeopt,
                                                 TypeParametersopt,
                                                 FormalParameters,
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 ConstructorBody);
         cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ConstructorModifiersopt));
         setResult(cd);
         $EndJava
       ./
       
     Super ::= extends ClassType
        /.$BeginJava
                    setResult(ClassType);
          $EndJava
        ./
    
    FieldKeyword ::= val
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
          $EndJava
        ./
                   | var 
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
          $EndJava
        ./
                   | const 
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
          $EndJava
        ./
                   
                   
                   
    VarKeyword ::= val 
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
          $EndJava
        ./
                   | var 
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
          $EndJava
        ./
                    
                   
    FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
        /.$BeginJava
                        FlagsNode fn = extractFlags(FieldModifiersopt, FieldKeyword);
        
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                        for (Iterator i = FieldDeclarators.iterator(); i.hasNext(); )
                        {
                            Object[] o = (Object[]) i.next();
                            Position pos = (Position) o[0];
                            Id name = (Id) o[1];
                            if (name == null) name = nf.Id(pos, Name.makeFresh());
                            List exploded = (List) o[2];
                            TypeNode type = (TypeNode) o[3];
                            if (type == null) type = nf.UnknownTypeNode(name.position());
                            Expr init = (Expr) o[4];
                            FieldDecl ld = nf.FieldDecl(pos, fn,
                                               type, name, init);
                            ld = (FieldDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(FieldModifiersopt));
                            l.add(ld);
                        }
                    setResult(l);
          $EndJava
        ./
        
                   
                       | FieldModifiersopt FieldDeclarators ;
        /.$BeginJava
                        List FieldKeyword = Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL));
                        FlagsNode fn = extractFlags(FieldModifiersopt, FieldKeyword);
        
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                        for (Iterator i = FieldDeclarators.iterator(); i.hasNext(); )
                        {
                            Object[] o = (Object[]) i.next();
                            Position pos = (Position) o[0];
                            Id name = (Id) o[1];
                            if (name == null) name = nf.Id(pos, Name.makeFresh());
                            List exploded = (List) o[2];
                            TypeNode type = (TypeNode) o[3];
                            if (type == null) type = nf.UnknownTypeNode(name.position());
                            Expr init = (Expr) o[4];
                            FieldDecl ld = nf.FieldDecl(pos, fn,
                                               type, name, init);
                            ld = (FieldDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(FieldModifiersopt));
                            l.add(ld);
                        }
                    setResult(l);
          $EndJava
        ./
        
    --------------------------------------- Section :: Statement

    Statement ::= NonExpressionStatement
                | ExpressionStatement

    NonExpressionStatement ::= Block
                | EmptyStatement
                | AssertStatement
                | SwitchStatement
                | DoStatement
                | BreakStatement
                | ContinueStatement
                | ReturnStatement
                | SynchronizedStatement
                | ThrowStatement
                | TryStatement
                | LabeledStatement
                | IfThenStatement
                | IfThenElseStatement
                | WhileStatement
                | ForStatement
                | NowStatement
                | AsyncStatement
                | AtStatement
                | AtomicStatement
                | WhenStatement
                | ForEachStatement
                | AtEachStatement
                | FinishStatement
                | AnnotationStatement
                | NextStatement
                | AwaitStatement
                | AssignPropertyCall
    
    IfThenStatement ::= if ( Expression ) Statement
        /.$BeginJava
                    setResult(nf.If(pos(), Expression, Statement));
          $EndJava
        ./
    
    IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
        /.$BeginJava
                    setResult(nf.If(pos(), Expression, s1, s2));
          $EndJava
        ./
    
    EmptyStatement ::= ;
        /.$BeginJava
                    setResult(nf.Empty(pos()));
          $EndJava
        ./
    
    LabeledStatement ::= Identifier : LoopStatement
        /.$BeginJava
                    setResult(nf.Labeled(pos(), Identifier, LoopStatement));
          $EndJava
        ./
        
    LoopStatement ::= ForStatement
                    | WhileStatement
                    | DoStatement
                    | AtEachStatement
                    | ForEachStatement
    
    ExpressionStatement ::= StatementExpression ;
        /.$BeginJava
                    boolean eval = true;
                    if (StatementExpression instanceof X10Call) {
                        X10Call c = (X10Call) StatementExpression;
                        if (c.name().id().toString().equals("property") && c.target() == null) {
                            setResult(nf.AssignPropertyCall(c.position(),c.typeArguments(), c.arguments()));
                            eval = false;
                        }
                        if (c.name().id().toString().equals("super") && c.target() instanceof Expr) {
                            setResult(nf.X10SuperCall(c.position(), (Expr) c.target(), c.typeArguments(), c.arguments()));
                            eval = false;
                       }
                       if (c.name().id().toString().equals("this") && c.target() instanceof Expr) {
                            setResult(nf.X10ThisCall(c.position(), (Expr) c.target(), c.typeArguments(), c.arguments()));
                            eval = false;
                       }
                    }
                        
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
                  | default :
        /.$BeginJava
                    setResult(nf.Default(pos()));
          $EndJava
        ./

    WhileStatement ::= while ( Expression ) Statement
        /.$BeginJava
                    setResult(nf.While(pos(), Expression, Statement));
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
          $EndJava
        ./
    
    BreakStatement ::= break Identifieropt ;
        /.$BeginJava
                    setResult(nf.Break(pos(), Identifieropt));
          $EndJava
        ./
    
    ContinueStatement ::= continue Identifieropt ;
        /.$BeginJava
                    setResult(nf.Continue(pos(), Identifieropt));
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

    AtStatement ::= at PlaceExpressionSingleList Statement
        /.$BeginJava
                  setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
          $EndJava
        ./

    AtomicStatement ::= atomic Statement
        /.$BeginJava
                  setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
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

    ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
        /.$BeginJava
                    FlagsNode fn = LoopIndex.flags();
                    Flags f = fn.flags();
                    f = f.Final();
                    fn = fn.flags(f);
                    setResult(nf.ForEach(pos(),
                                  LoopIndex.flags(fn),
                                  Expression,
                                  ClockedClauseopt,
                                  Statement));
          $EndJava
        ./

    AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
        /.$BeginJava
                    FlagsNode fn = LoopIndex.flags();
                    Flags f = fn.flags();
                    f = f.Final();
                    fn = fn.flags(f);
                    setResult(nf.AtEach(pos(),
                                 LoopIndex.flags(fn),
                                 Expression,
                                 ClockedClauseopt,
                                 Statement));
          $EndJava
        ./

    EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
        /.$BeginJava
                    FlagsNode fn = LoopIndex.flags();
                    Flags f = fn.flags();
                    f = f.Final();
                    fn = fn.flags(f);
                    setResult(nf.ForLoop(pos(),
                            LoopIndex.flags(fn),
                            Expression,
                            Statement));
          $EndJava
        ./

    FinishStatement ::= finish Statement
        /.$BeginJava
                    setResult(nf.Finish(pos(),  Statement));
          $EndJava
        ./


    AnnotationStatement ::= Annotations Statement
        /.$BeginJava
                    if (Statement.ext() instanceof X10Ext && Annotations instanceof List) {
                        Statement = (Stmt) ((X10Ext) Statement.ext()).annotations((List) Annotations);
                    }
                    setResult(Statement);
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
                /.$BeginJava
        setResult(Expression);
          $EndJava
        ./
--
--      Clock ::= Identifier
--        /.$BeginJava
--                    setResult(new X10ParsedName(nf, ts, pos(), Identifier).toExpr());
--          $EndJava
--        ./


    CastExpression ::=
         CastExpression as Type
        /.$BeginJava
                    setResult(nf.X10Cast(pos(), Type, CastExpression));
          $EndJava
        ./
       | ConditionalExpression ! Expression
        /.$BeginJava
                    setResult(nf.PlaceCast(pos(), Expression, ConditionalExpression));
          $EndJava
        ./
        | ConditionalExpression

    
     --------------------------------------- Section :: Expression
     TypePropertyList ::= TypeProperty
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), TypePropertyNode.class, false);
                    l.add(TypeProperty);
                    setResult(l);
          $EndJava
        ./
                      | TypePropertyList , TypeProperty
        /.$BeginJava
                    TypePropertyList.add(TypeProperty);
                    setResult(TypePropertyList);
          $EndJava
        ./
        
     TypeParameterList ::= TypeParameter
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                    l.add(TypeParameter);
                    setResult(l);
          $EndJava
        ./
                      | TypeParameterList , TypeParameter
        /.$BeginJava
                    TypeParameterList.add(TypeParameter);
                    setResult(TypeParameterList);
          $EndJava
        ./
        
    TypeProperty ::= Identifier
        /.$BeginJava
                    setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.INVARIANT));
          $EndJava
        ./
                   | + Identifier
        /.$BeginJava
                    setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.COVARIANT));
          $EndJava
        ./
                   | - Identifier
        /.$BeginJava
                    setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.CONTRAVARIANT));
          $EndJava
        ./
        
    TypeParameter ::= Identifier
        /.$BeginJava
                    setResult(nf.TypeParamNode(pos(), Identifier));
          $EndJava
        ./

    Primary ::= here
        /.$BeginJava
                    setResult(((X10NodeFactory) nf).Here(pos()));
          $EndJava
        ./

    RegionExpression ::= Expression

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

    Primary ::= '[' ArgumentListopt ']'
        /.$BeginJava
                    Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                    setResult(tuple);
          $EndJava
        ./

    AssignmentExpression ::= Expression$expr1 '->' Expression$expr2
        /.$BeginJava
                    Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                    setResult(call);
          $EndJava
        ./

    ClosureExpression ::= TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt => ClosureBody
        /.$BeginJava
                    setResult(nf.Closure(pos(), TypeParametersopt, FormalParameters, WhereClauseopt, 
              ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt, Throwsopt, ClosureBody));
          $EndJava
        ./
                       
    LastExpression ::= Expression
        /.$BeginJava
                    setResult(nf.X10Return(pos(), Expression, true));
          $EndJava
        ./

    ClosureBody ::= CastExpression
        /.$BeginJava
                    setResult(nf.Block(pos(), nf.X10Return(pos(), CastExpression, true)));
          $EndJava
        ./
                  | Annotationsopt { BlockStatementsopt LastExpression }
        /.$BeginJava
                    List<Stmt> l = new ArrayList<Stmt>();
                    l.addAll(BlockStatementsopt);
                    l.add(LastExpression);
                    Block b = nf.Block(pos(), l);
                    b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                    setResult(b);
          $EndJava
        ./
                  | Annotationsopt Block
        /.$BeginJava
                    Block b = Block;
                    b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                    setResult(b);
          $EndJava
        ./
                  
                  
    AtExpression ::= at PlaceExpressionSingleList ClosureBody
        /.$BeginJava
                    setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
          $EndJava
        ./

    AsyncExpression ::= async ClosureBody
        /.$BeginJava
                    setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
          $EndJava
        ./
                       | async PlaceExpressionSingleList ClosureBody
        /.$BeginJava
                    setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
          $EndJava
        ./
                       | async '[' Type ']' ClosureBody
        /.$BeginJava
                    setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
          $EndJava
        ./
                       | async '[' Type ']' PlaceExpressionSingleList ClosureBody
        /.$BeginJava
                    setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
          $EndJava
        ./

    FutureExpression ::= future ClosureBody
        /.$BeginJava
                    setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
          $EndJava
        ./
                       | future PlaceExpressionSingleList ClosureBody
        /.$BeginJava
                    setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
          $EndJava
        ./
                       | future '[' Type ']' ClosureBody
        /.$BeginJava
                    setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
          $EndJava
        ./
                       | future '[' Type ']' PlaceExpressionSingleList ClosureBody
        /.$BeginJava
                    setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
          $EndJava
        ./

    ---------------------------------------- All the opts...

    DepParametersopt ::= %Empty
        /.$NullAction./
                       | DepParameters
    PropertyListopt ::=  %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
          $EndJava
        ./
                       | PropertyList
                       
    WhereClauseopt ::= %Empty
        /.$NullAction./
                     | WhereClause

    ObjectKindopt ::= %Empty
        /.$NullAction./
                    | ObjectKind

    PlaceExpressionSingleListopt ::= %Empty
        /.$NullAction./
                                   | PlaceExpressionSingleList

    ClassModifiersopt ::= %Empty
        /.$BeginJava
             setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
          $EndJava ./
          | ClassModifiers
          
    TypeDefModifiersopt ::= %Empty
        /.$BeginJava
             setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
          $EndJava ./
          | TypeDefModifiers
          
    Unsafeopt ::= %Empty
        /.$NullAction./
                | unsafe
        /.$BeginJava
                    // any value distinct from null
                    setResult(this);
          $EndJava
        ./

    ClockedClauseopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Expr.class, false));
          $EndJava
        ./
                       | ClockedClause


    ------------------------------------------------------------
    --- All the Java-derived rules

    identifier ::= IDENTIFIER$ident
        /.$BeginJava
                    ident.setKind($sym_type.TK_IDENTIFIER);
                    setResult(id(getRhsFirstTokenIndex($ident)));
          $EndJava
        ./

    TypeName ::= Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf, ts, pos(), Identifier));
          $EndJava
        ./
               | TypeName . Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      TypeName,
                                      Identifier));
          $EndJava
        ./

    ClassName ::= TypeName

    TypeArguments ::= '[' TypeArgumentList ']'
        /.$BeginJava
                    setResult(TypeArgumentList);
          $EndJava
        ./

    
    TypeArgumentList ::= Type
        /.$BeginJava
                    List l = new ArrayList();
                    l.add(Type);
                    setResult(l);
          $EndJava
        ./
                       | TypeArgumentList , Type
        /.$BeginJava
                    TypeArgumentList.add(Type);
          $EndJava
        ./
        
    

    -- Chapter 6

    PackageName ::= Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf, ts, pos(), Identifier));
          $EndJava
        ./
                  | PackageName . Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      PackageName,
                                      Identifier));
          $EndJava
        ./

    --
    -- See Chapter 4
    --
    -- TypeName ::= Identifier
    --           | PackageOrTypeName . Identifier
    --
    ExpressionName ::=? Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf, ts, pos(), Identifier));
          $EndJava
        ./
                     | AmbiguousName . Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      Identifier));
          $EndJava
        ./

    MethodName ::=? Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf, ts, pos(), Identifier));
          $EndJava
        ./
                 | AmbiguousName . Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      Identifier));
          $EndJava
        ./

    PackageOrTypeName ::= Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf, ts, pos(), Identifier));
          $EndJava
        ./
                        | PackageOrTypeName . Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      PackageOrTypeName,
                                      Identifier));
          $EndJava
        ./

    AmbiguousName ::=? Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf, ts, pos(), Identifier));
          $EndJava
        ./
                    | AmbiguousName . Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      Identifier));
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
//                    Import x10LangImport = 
//                    nf.Import(pos(token_pos), Import.PACKAGE, QName.make("x10.lang"));
//                    ImportDeclarationsopt.add(x10LangImport);
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
        /.$BeginJava
                    PackageNode pn = PackageName.toPackage();
                    pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                    setResult(pn);
          $EndJava
        ./
    

    ImportDeclaration ::= SingleTypeImportDeclaration
                        | TypeImportOnDemandDeclaration
--                        | SingleStaticImportDeclaration
--                        | StaticImportOnDemandDeclaration

    SingleTypeImportDeclaration ::= import TypeName ;
        /.$BeginJava
                    setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
          $EndJava
        ./

    TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
        /.$BeginJava
                    setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
          $EndJava
        ./
    
--    SingleStaticImportDeclaration ::= import static TypeName . Identifier ;
--        /.$BadAction./

--    StaticImportOnDemandDeclaration ::= import static TypeName . * ;
--        /.$BadAction./

    TypeDeclaration ::= ClassDeclaration
                      | InterfaceDeclaration
                      | TypeDefDeclaration
                      | ;
        /.$BeginJava
                    setResult(null);
          $EndJava
        ./

    -- Chapter 8

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
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
          $EndJava
        ./
                    | protected
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
          $EndJava
        ./
                    | private
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
          $EndJava
        ./
                    | abstract
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
          $EndJava
        ./
                    | static
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
          $EndJava
        ./
                    | final
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
          $EndJava
        ./
                    | strictfp
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
          $EndJava
        ./
                    | safe
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
          $EndJava
        ./
                    | value
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.VALUE)));
          $EndJava
        ./
        
    TypeDefModifiers ::= TypeDefModifier
        /.$BeginJava
                    List l = new LinkedList();
                    l.addAll(TypeDefModifier);
                    setResult(l);
          $EndJava
        ./
                     | TypeDefModifiers TypeDefModifier
        /.$BeginJava
                    TypeDefModifiers.addAll(TypeDefModifier);
          $EndJava
        ./

    TypeDefModifier ::= Annotation
        /.$BeginJava
                    setResult(Collections.singletonList(Annotation));
          $EndJava
        ./
                    | public
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
          $EndJava
        ./
                    | protected
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
          $EndJava
        ./
                    | private
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
          $EndJava
        ./
                    | abstract
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
          $EndJava
        ./
                    | static
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
          $EndJava
        ./
                    | final
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
          $EndJava
        ./
        
    --
    -- See Chapter 4
    --
    Interfaces ::= implements InterfaceTypeList
        /.$BeginJava
                    setResult(InterfaceTypeList);
          $EndJava
        ./

    InterfaceTypeList ::= Type
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), TypeNode.class, false);
                    l.add(Type);
                    setResult(l);
          $EndJava
        ./
                        | InterfaceTypeList , Type
        /.$BeginJava
                    InterfaceTypeList.add(Type);
                    setResult(InterfaceTypeList);
          $EndJava
        ./

    --
    -- See Chapter 4
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
                    l.add(InstanceInitializer);
                    setResult(l);
          $EndJava
        ./
                           | StaticInitializer
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(StaticInitializer);
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
                             | PropertyMethodDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(PropertyMethodDeclaration);
                    setResult(l);
          $EndJava
        ./
                             | TypeDefDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(TypeDefDeclaration);
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
    
    FormalDeclarators ::= FormalDeclarator
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Object[].class, false);
                    l.add(FormalDeclarator);
                    setResult(l);
          $EndJava
        ./
                          | FormalDeclarators , FormalDeclarator
        /.$BeginJava
                    FormalDeclarators.add(FormalDeclarator);
          $EndJava
        ./
    
    
    FieldDeclarators ::= FieldDeclarator
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Object[].class, false);
                    l.add(FieldDeclarator);
                    setResult(l);
          $EndJava
        ./
                          | FieldDeclarators , FieldDeclarator
        /.$BeginJava
                    FieldDeclarators.add(FieldDeclarator);
                    // setResult(FieldDeclarators);
          $EndJava
        ./
    
    
    VariableDeclaratorsWithType ::= VariableDeclaratorWithType
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Object[].class, false);
                    l.add(VariableDeclaratorWithType);
                    setResult(l);
          $EndJava
        ./
                          | VariableDeclaratorsWithType , VariableDeclaratorWithType
        /.$BeginJava
                    VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                    // setResult(VariableDeclaratorsWithType);
          $EndJava
        ./
    
    VariableDeclarators ::= VariableDeclarator
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Object[].class, false);
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
    
    VariableInitializer ::= Expression
    
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
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
          $EndJava
        ./
                    | protected
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
          $EndJava
        ./
                    | private
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
          $EndJava
        ./
                    | static
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
          $EndJava
        ./
                    | transient
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.TRANSIENT)));
          $EndJava
        ./
                    | volatile
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.VOLATILE)));
          $EndJava
        ./
    
    ResultType ::= : Type
     /.$BeginJava
                    setResult(Type);
          $EndJava
        ./
       
    FormalParameters ::= ( FormalParameterList )
        /.$BeginJava
                    setResult(FormalParameterList);
          $EndJava
        ./
    
    FormalParameterList ::= FormalParameter
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Formal.class, false);
                    l.add(FormalParameter);
                    setResult(l);
          $EndJava
        ./
                       | FormalParameterList , FormalParameter
        /.$BeginJava
                    FormalParameterList.add(FormalParameter);
          $EndJava
        ./
        
     LoopIndexDeclarator ::= Identifier ResultTypeopt
        /.$BeginJava
                    setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, null });
          $EndJava
        ./
                         | ( IdentifierList ) ResultTypeopt
        /.$BeginJava
                    setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, null });
          $EndJava
        ./
                         | Identifier ( IdentifierList ) ResultTypeopt
        /.$BeginJava
                    setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, null });
          $EndJava
        ./
        
    LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
        /.$BeginJava
                Formal f;
                            	FlagsNode fn = extractFlags(VariableModifiersopt, Flags.FINAL);
                Object[] o = LoopIndexDeclarator;
                Position pos = (Position) o[0];
                Id name = (Id) o[1];
                boolean unnamed = name == null;
                if (name == null) name = nf.Id(pos, Name.makeFresh());
                   List exploded = (List) o[2];
                            DepParameterExpr guard = (DepParameterExpr) o[3];
                            TypeNode type = (TypeNode) o[4];
                            if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                            List explodedFormals = new ArrayList();
                            for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                            	Id id = (Id) i.next();
                            	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
                            }
                f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
                f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
                setResult(f);
          $EndJava
        ./
                      | VariableModifiersopt VarKeyword LoopIndexDeclarator
        /.$BeginJava
                Formal f;
                            	FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
                Object[] o = LoopIndexDeclarator;
                Position pos = (Position) o[0];
                Id name = (Id) o[1];
                boolean unnamed = name == null;
                if (name == null) name = nf.Id(pos, Name.makeFresh());
                   List exploded = (List) o[2];
                            DepParameterExpr guard = (DepParameterExpr) o[3];
                            TypeNode type = (TypeNode) o[4];
                                                        if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                                                        List explodedFormals = new ArrayList();
                            for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                            	Id id = (Id) i.next();
                            	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
                            }
                f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
                f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
                setResult(f);
          $EndJava
        ./
    
    FormalParameter ::= VariableModifiersopt FormalDeclarator
        /.$BeginJava
                Formal f;
                            	FlagsNode fn = extractFlags(VariableModifiersopt, Flags.FINAL);
                Object[] o = FormalDeclarator;
                Position pos = (Position) o[0];
                Id name = (Id) o[1];
                boolean unnamed = name == null;
                if (name == null) name = nf.Id(pos, Name.makeFresh());
                   List exploded = (List) o[2];
                            DepParameterExpr guard = (DepParameterExpr) o[3];
                            TypeNode type = (TypeNode) o[4];
                            if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                            Expr init = (Expr) o[5];
                            List explodedFormals = new ArrayList();
                            for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                            	Id id = (Id) i.next();
                            	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
                            }
                f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
                f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
                setResult(f);
          $EndJava
        ./
                      | VariableModifiersopt VarKeyword FormalDeclarator
        /.$BeginJava
                Formal f;
                            	FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
                Object[] o = FormalDeclarator;
                Position pos = (Position) o[0];
                Id name = (Id) o[1];
                boolean unnamed = name == null;
                if (name == null) name = nf.Id(pos, Name.makeFresh());
                   List exploded = (List) o[2];
                            DepParameterExpr guard = (DepParameterExpr) o[3];
                            TypeNode type = (TypeNode) o[4];
                                                        if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                            Expr init = (Expr) o[5];
                                                        List explodedFormals = new ArrayList();
                            for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                            	Id id = (Id) i.next();
                            	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
                            }
                f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
                f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
                setResult(f);
          $EndJava
        ./
                      | Type
        /.$BeginJava
                Formal f;
                f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh()), Collections.EMPTY_LIST, true);
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
    
    VariableModifier ::= Annotation
        /.$BeginJava
                    setResult(Collections.singletonList(Annotation));
          $EndJava
        ./
                       | shared
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
          $EndJava
        ./
    
    --
    -- See above
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
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
          $EndJava
        ./
                     | protected
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
          $EndJava
        ./
                     | private
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
          $EndJava
        ./
                     | abstract
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
          $EndJava
        ./
                     | static
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
          $EndJava
        ./
                     | final
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
          $EndJava
        ./
                     | native
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
          $EndJava
        ./
                     | strictfp
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
          $EndJava
        ./
                     | atomic
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
          $EndJava
        ./
                     | extern
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
          $EndJava
        ./
                     | safe
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
          $EndJava
        ./
                     | sequential
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
          $EndJava
        ./
                     | local
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.LOCAL)));
          $EndJava
        ./
                     | nonblocking
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
          $EndJava
        ./
                     | incomplete
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
          $EndJava
        ./
                     | property
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
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
          $EndJava
        ./
    
    ExceptionType ::= ClassType
        
    MethodBody ::= = LastExpression ;
        /.$BeginJava
                    setResult(nf.Block(pos(), LastExpression));
          $EndJava
        ./
                  | = { BlockStatementsopt LastExpression }
        /.$BeginJava
                    List l = new ArrayList();
                    l.addAll(BlockStatementsopt);
                    l.add(LastExpression);
                    setResult(nf.Block(pos(), l));
          $EndJava
        ./
                  | = Block
        /.$BeginJava
                    setResult(Block);
          $EndJava
        ./
                  | Block
        /.$BeginJava
                    setResult(Block);
          $EndJava
        ./
                      | ;
        /.$NullAction./
    
    InstanceInitializer ::= Block
        /.$BeginJava
                    setResult(nf.Initializer(pos(), nf.FlagsNode(pos(), Flags.NONE), Block));
          $EndJava
        ./
    
    StaticInitializer ::= static Block
        /.$BeginJava
                    setResult(nf.Initializer(pos(), nf.FlagsNode(pos(getLeftSpan()), Flags.STATIC), Block));
          $EndJava
        ./
      
    SimpleTypeName ::= Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf, ts, pos(), Identifier));
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
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
          $EndJava
        ./
                          | protected
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
          $EndJava
        ./
                          | private
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
          $EndJava
        ./
                          | native
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
          $EndJava
        ./
    
    ConstructorBody ::= = ConstructorBlock
        /.$BeginJava
                    setResult(ConstructorBlock);
          $EndJava
        ./
                      | ConstructorBlock
        /.$BeginJava
                    setResult(ConstructorBlock);
          $EndJava
        ./
                    | = ExplicitConstructorInvocation
        /.$BeginJava
                    List l;
                    l = new TypedList(new LinkedList(), Stmt.class, false);
                    l.add(ExplicitConstructorInvocation);
                    setResult(nf.Block(pos(), l));
          $EndJava
        ./
                    | = AssignPropertyCall
        /.$BeginJava
                    List l;
                    l = new TypedList(new LinkedList(), Stmt.class, false);
                    l.add(nf.SuperCall(pos(), Collections.EMPTY_LIST));
                    l.add(AssignPropertyCall);
                    setResult(nf.Block(pos(), l));
          $EndJava
        ./
                      | ;
        /.$NullAction./

    ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
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
    
    Arguments ::= ( ArgumentListopt )
        /.$BeginJava
                    setResult(ArgumentListopt);
          $EndJava
        ./
    
    -- chapter 9
    
    InterfaceDeclaration ::= NormalInterfaceDeclaration
    
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
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
          $EndJava
        ./
                        | protected
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
          $EndJava
        ./
                        | private
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
          $EndJava
        ./
                        | abstract
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
          $EndJava
        ./
                        | static
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
          $EndJava
        ./
                        | strictfp
        /.$BeginJava
                    setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
          $EndJava
        ./
    
    ExtendsInterfaces ::= extends Type
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), TypeNode.class, false);
                    l.add(Type);
                    setResult(l);
          $EndJava
        ./
                        | ExtendsInterfaces , Type
        /.$BeginJava
                    ExtendsInterfaces.add(Type);
          $EndJava
        ./
    
    --
    -- See Chapter 4

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
    
    InterfaceMemberDeclaration ::= MethodDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(MethodDeclaration);
                    setResult(l);
          $EndJava
        ./
                                 | PropertyMethodDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(PropertyMethodDeclaration);
                    setResult(l);
          $EndJava
        ./
                                 | FieldDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.addAll(FieldDeclaration);
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
                                 | TypeDefDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    l.add(TypeDefDeclaration);
                    setResult(l);
          $EndJava
        ./
                                 | ;
        /.$BeginJava
                    setResult(Collections.EMPTY_LIST);
          $EndJava
        ./
    
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
    
    Annotation ::= @ NamedType
        /.$BeginJava
                    setResult(nf.AnnotationNode(pos(), NamedType));
          $EndJava
        ./
    
    SimpleName ::= Identifier
        /.$BeginJava
                    setResult(new X10ParsedName(nf, ts, pos(), Identifier));
          $EndJava
        ./
        
    Identifier ::= identifier
        /.$BeginJava
                    setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
          $EndJava
        ./

    -- Chapter 10
    
    ArrayInitializer ::= '[' VariableInitializersopt ,opt$opt ']'
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
                     | TypeDefDeclaration
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Stmt.class, false);
                    l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
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
    
    IdentifierList ::= Identifier
        /.$BeginJava
                    List l = new TypedList(new LinkedList(), Id.class, false);
                    l.add(Identifier);
                    setResult(l);
          $EndJava
        ./
                     | IdentifierList , Identifier
        /.$BeginJava
                    IdentifierList.add(Identifier);
          $EndJava
        ./
                    
    FormalDeclarator ::= Identifier : Type
        /.$BeginJava
                    setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, Type, null });
          $EndJava
        ./
                         | ( IdentifierList ) : Type
        /.$BeginJava
                    setResult(new Object[] { pos(), null, IdentifierList, null, Type, null });
          $EndJava
        ./
                         | Identifier ( IdentifierList ) : Type
        /.$BeginJava
                    setResult(new Object[] { pos(), Identifier, IdentifierList, null, Type, null });
          $EndJava
        ./
    
    FieldDeclarator ::= Identifier : Type
        /.$BeginJava
                    setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, Type, null });
          $EndJava
        ./
                         | Identifier ResultTypeopt = VariableInitializer
        /.$BeginJava
                    setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, ResultTypeopt, VariableInitializer });
          $EndJava
        ./
                    
    VariableDeclarator ::= Identifier ResultTypeopt = VariableInitializer
        /.$BeginJava
                    setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, VariableInitializer });
          $EndJava
        ./
                         | ( IdentifierList ) ResultTypeopt = VariableInitializer
        /.$BeginJava
                    setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, VariableInitializer });
          $EndJava
        ./
                         | Identifier ( IdentifierList ) ResultTypeopt = VariableInitializer
        /.$BeginJava
                    setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, VariableInitializer });
          $EndJava
        ./
                    
    VariableDeclaratorWithType ::= Identifier ResultType = VariableInitializer
        /.$BeginJava
                    setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, VariableInitializer });
          $EndJava
        ./
                         | ( IdentifierList ) ResultType = VariableInitializer
        /.$BeginJava
                    setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, VariableInitializer });
          $EndJava
        ./
                         | Identifier ( IdentifierList ) ResultType = VariableInitializer
        /.$BeginJava
                    setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, VariableInitializer });
          $EndJava
        ./
    
    LocalVariableDeclarationStatement ::= LocalVariableDeclaration ;
    
    LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
        /.$BeginJava
                    FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
        
                    List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                    List s = new TypedList(new LinkedList(), Stmt.class, false);
                        for (Iterator i = VariableDeclarators.iterator(); i.hasNext(); )
                        {
                            Object[] o = (Object[]) i.next();
                            Position pos = (Position) o[0];
                            Id name = (Id) o[1];
                            if (name == null) name = nf.Id(pos, Name.makeFresh());
                            List exploded = (List) o[2];
                            DepParameterExpr guard = (DepParameterExpr) o[3];
                            TypeNode type = (TypeNode) o[4];
                            if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                            Expr init = (Expr) o[5];
                            LocalDecl ld = nf.LocalDecl(pos, fn,
                                               type, name, init);
                            ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(VariableModifiersopt));
                            int index = 0;
                            l.add(ld);
                            for (Iterator j = exploded.iterator(); j.hasNext(); ) {
                            	Id id = (Id) j.next();
                            	TypeNode tni = nf.UnknownTypeNode(id.position());
                            	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name), Collections.EMPTY_LIST, Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
                            	index++;
                            }
                        }
                    l.addAll(s); 
                    setResult(l);
          $EndJava
        ./
                               | VariableModifiersopt VariableDeclaratorsWithType
        /.$BeginJava
                    FlagsNode fn = extractFlags(VariableModifiersopt, Flags.FINAL);
        
                    List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                    List s = new TypedList(new LinkedList(), Stmt.class, false);
                        for (Iterator i = VariableDeclaratorsWithType.iterator(); i.hasNext(); )
                        {
                            Object[] o = (Object[]) i.next();
                            Position pos = (Position) o[0];
                            Id name = (Id) o[1];
                            if (name == null) name = nf.Id(pos, Name.makeFresh());
                            List exploded = (List) o[2];
                            DepParameterExpr guard = (DepParameterExpr) o[3];
                            TypeNode type = (TypeNode) o[4];
                            if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                            Expr init = (Expr) o[5];
                            LocalDecl ld = nf.LocalDecl(pos, fn,
                                               type, name, init);
                            ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(VariableModifiersopt));
                            int index = 0;
                            l.add(ld);
                            for (Iterator j = exploded.iterator(); j.hasNext(); ) {
                            	Id id = (Id) j.next();
                            	// HACK: if the local is non-final, assume the type is point and the component is int
                            	TypeNode tni = nf.UnknownTypeNode(id.position());
                            	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name), Collections.EMPTY_LIST, Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
                            	index++;
                            }
                        }
                    l.addAll(s); 
                    setResult(l);
          $EndJava
        ./
                  | VariableModifiersopt VarKeyword FormalDeclarators
        /.$BeginJava
                    FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
        
                    List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                    List s = new TypedList(new LinkedList(), Stmt.class, false);
                        for (Iterator i = FormalDeclarators.iterator(); i.hasNext(); )
                        {
                            Object[] o = (Object[]) i.next();
                            Position pos = (Position) o[0];
                            Id name = (Id) o[1];
                            if (name == null) name = nf.Id(pos, Name.makeFresh());
                            List exploded = (List) o[2];
                            DepParameterExpr guard = (DepParameterExpr) o[3];
                            TypeNode type = (TypeNode) o[4];
                                                        if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                            Expr init = (Expr) o[5];
                            LocalDecl ld = nf.LocalDecl(pos, fn,
                                               type, name, init);
                            ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(VariableModifiersopt));
                            int index = 0;
                            l.add(ld);
                            for (Iterator j = exploded.iterator(); j.hasNext(); ) {
                            	Id id = (Id) j.next();
                            	// HACK: if the local is non-final, assume the type is point and the component is int
                            	TypeNode tni = nf.UnknownTypeNode(id.position());
                            	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name), Collections.EMPTY_LIST, Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
                            	index++;
                            }
                        }
                    l.addAll(s); 
                    setResult(l);
          $EndJava
        ./
    
    --
    -- See Chapter 8

    -- Chapter 15
    
    Primary ::= Literal
                        | TypeName . class
        /.$BeginJava
                    if (TypeName instanceof ParsedName)
                    {
                        ParsedName a = (ParsedName) TypeName;
                        setResult(nf.ClassLit(pos(), a.toType()));
                    }
                    else assert(false);
          $EndJava
        ./
                        | self
        /.$BeginJava
                    setResult(nf.Self(pos()));
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
                        | MethodSelection
                        | OperatorFunction
                        
    OperatorFunction ::= TypeName . +
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.ADD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . -
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.SUB, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . *
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.MUL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . /
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.DIV, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . '%'
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.MOD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . &
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.BIT_AND, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . '|'
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.BIT_OR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . ^
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.BIT_XOR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . <<
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.SHL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . >>
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.SHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . >>>
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.USHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . <
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.LT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . <=
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.LE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . >=
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.GE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . >
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.GT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . ==
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.EQ, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       | TypeName . !=
            /.$BeginJava
                    List<Formal> formals = new ArrayList<Formal>();
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                    formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                    TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                    setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                               Binary.NE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
          $EndJava
        ./
                       

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
          $EndJava
        ./

    FieldAccess ::= Primary . Identifier
        /.$BeginJava
                    setResult(nf.Field(pos(), Primary, Identifier));
          $EndJava
        ./
                  | super . Identifier
        /.$BeginJava
                    setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), Identifier));
          $EndJava
        ./
                  | ClassName . super$sup . Identifier
        /.$BeginJava
                    setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex($sup)), ClassName.toType()), Identifier));
          $EndJava
        ./
                  | Primary . class$c
        /.$BeginJava
                    setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex($c)), "class")));
          $EndJava
        ./
                  | super . class$c
        /.$BeginJava
                    setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex($c)), "class")));
          $EndJava
        ./
                  | ClassName . super$sup . class$c
        /.$BeginJava
                    setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex($sup)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex($c)), "class")));
          $EndJava
        ./
    
    MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
                    setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                                 ? null
                                                                 : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
          $EndJava
        ./
                       | Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
                    setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
          $EndJava
        ./
                       | super . Identifier TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
                    setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
          $EndJava
        ./
                       | ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
                    setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex($sup)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
          $EndJava
        ./
                       | Primary TypeArgumentsopt ( ArgumentListopt )
        /.$BeginJava
                    if (Primary instanceof Field) {
                        Field f = (Field) Primary;
                        setResult(nf.X10Call(pos(), f.target(), f.name(), TypeArgumentsopt, ArgumentListopt));
                    }
                    else if (Primary instanceof AmbExpr) {
                        AmbExpr f = (AmbExpr) Primary;
                        setResult(nf.X10Call(pos(), null, f.name(), TypeArgumentsopt, ArgumentListopt));
                    }
                    else if (Primary instanceof Here) {
                        Here f = (Here) Primary;
                        setResult(nf.X10Call(pos(), null, nf.Id(Primary.position(), Name.make("here")), TypeArgumentsopt, ArgumentListopt));
                    }
                    else {
                        setResult(nf.ClosureCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                    }
          $EndJava
        ./
        
    MethodSelection ::= MethodName . TypeParametersopt ( FormalParameterListopt )
        /.$BeginJava
                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                    List<Formal> formals = toFormals(FormalParameterListopt);
                    List<Expr> actuals = toActuals(FormalParameterListopt);
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(), nf.X10Call(pos(), MethodName.prefix == null
                                                                 ? null
                                                                 : MethodName.prefix.toReceiver(), MethodName.name, typeArgs, actuals), true))));
          $EndJava
        ./
                       | Primary . Identifier . TypeParametersopt ( FormalParameterListopt )
        /.$BeginJava
                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                    List<Formal> formals = toFormals(FormalParameterListopt);
                    List<Expr> actuals = toActuals(FormalParameterListopt);
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(),
                                                   nf.X10Call(pos(), Primary, Identifier, typeArgs, actuals), true))));
          $EndJava
        ./
                       | super . Identifier . TypeParametersopt ( FormalParameterListopt )
        /.$BeginJava
                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                    List<Formal> formals = toFormals(FormalParameterListopt);
                    List<Expr> actuals = toActuals(FormalParameterListopt);
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(),
                                                   nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, typeArgs, actuals), true))));
          $EndJava
        ./
                       | ClassName . super$sup . Identifier . TypeParametersopt ( FormalParameterListopt )
        /.$BeginJava
                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                    List<Formal> formals = toFormals(FormalParameterListopt);
                    List<Expr> actuals = toActuals(FormalParameterListopt);
                    TypeNode tn = nf.UnknownTypeNode(pos());
                    setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                         nf.X10Return(pos(),
                                                   nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex($sup)), ClassName.toType()), Identifier, typeArgs, actuals), true))));
          $EndJava
        ./

    PostfixExpression ::= Primary
                        | ArrayInitailizer
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
                      | + UnaryExpressionNotPlusMinus
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
          $EndJava
        ./
                      | - UnaryExpressionNotPlusMinus
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
          $EndJava
        ./
                      | UnaryExpressionNotPlusMinus
    
    PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
          $EndJava
        ./
    
    PreDecrementExpression ::= '--' UnaryExpressionNotPlusMinus
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
          $EndJava
        ./
    
    UnaryExpressionNotPlusMinus ::= PostfixExpression
                                  | ~ UnaryExpression
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
          $EndJava
        ./
                      | Annotations UnaryExpression
        /.$BeginJava
                    Expr e = UnaryExpression;
                    e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                    setResult(e);
          $EndJava
        ./
                                  | ! UnaryExpression
        /.$BeginJava
                    setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
          $EndJava
        ./
    
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
                      | ShiftExpression >> AdditiveExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
          $EndJava
        ./
                      | ShiftExpression >>> AdditiveExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
          $EndJava
        ./
    
    RangeExpression ::= ShiftExpression
                      | ShiftExpression$expr1 .. ShiftExpression$expr2
        /.$BeginJava
                    Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                    setResult(regionCall);
          $EndJava
        ./
    
    RelationalExpression ::= RangeExpression
                           | SubtypeConstraint
                           | RelationalExpression < RangeExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
          $EndJava
        ./
                           | RelationalExpression > RangeExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
          $EndJava
        ./
                           | RelationalExpression <= RangeExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
          $EndJava
        ./
                           | RelationalExpression >= RangeExpression
        /.$BeginJava
                    setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
          $EndJava
        ./
                           | RelationalExpression instanceof Type
        /.$BeginJava
                    setResult(nf.Instanceof(pos(), RelationalExpression, Type));
          $EndJava
        ./
                           | RelationalExpression in ShiftExpression
        /.$BeginJava
                    setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
          $EndJava
        ./
    
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
                         | Type$t1 == Type$t2
        /.$BeginJava
                    setResult(nf.SubtypeTest(pos(), t1, t2, true));
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
                            | ClosureExpression
                            | FutureExpression
                            | AsyncExpression
                            | AtExpression
                            | ConditionalOrExpression ? Expression : ConditionalExpression
        /.$BeginJava
                    setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
          $EndJava
        ./
    
    AssignmentExpression ::= Assignment
                           | CastExpression
    
    Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
        /.$BeginJava
                    setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
          $EndJava
        ./
                 | ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
        /.$BeginJava
                    setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
          $EndJava
        ./
                 | Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
        /.$BeginJava
                    setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
          $EndJava
        ./
    
    LeftHandSide ::= ExpressionName
        /.$BeginJava
                    setResult(ExpressionName.toExpr());
          $EndJava
        ./
                   | FieldAccess
    
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
                         | >>=
        /.$BeginJava
                    setResult(Assign.SHR_ASSIGN);
          $EndJava
        ./
                         | >>>=
        /.$BeginJava
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
    Catchesopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Catch.class, false));
          $EndJava
        ./
                 | Catches

    Identifieropt ::= %Empty
        /.$NullAction./
                    | Identifier
        /.$BeginJava
                    setResult(Identifier);
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
        /.$NullAction./
               | Super

    TypeParametersopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
          $EndJava
        ./
                        | TypeParameters
                        
    FormalParametersopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), Formal.class, false));
          $EndJava
        ./
                        | FormalParameters

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
                            
    ResultTypeopt ::= %Empty
        /.$NullAction./
                            | ResultType
        
    TypeArgumentsopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), TypeNode.class, false));
          $EndJava
        ./
                       | TypeArguments
        
    TypePropertiesopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), TypePropertyNode.class, false));
          $EndJava
        ./
                       | TypeProperties

    Propertiesopt ::= %Empty
        /.$BeginJava
                    setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
          $EndJava
        ./
                       | Properties

    ,opt ::= %Empty
        /.$NullAction./
                       | ,

    PrefixOp ::= +
        /.$BeginJava
                    setResult(Unary.POS);
          $EndJava
        ./
      | -
        /.$BeginJava
                    setResult(Unary.NEG);
          $EndJava
        ./
      | !
        /.$BeginJava
                    setResult(Unary.NOT);
          $EndJava
        ./
      | ~
        /.$BeginJava
                    setResult(Unary.BIT_NOT);
          $EndJava
        ./
        
    BinOp ::= +
        /.$BeginJava
                    setResult(Binary.ADD);
          $EndJava
        ./
      | -
        /.$BeginJava
                    setResult(Binary.SUB);
          $EndJava
        ./
      | *
        /.$BeginJava
                    setResult(Binary.MUL);
          $EndJava
        ./
      | /
        /.$BeginJava
                    setResult(Binary.DIV);
          $EndJava
        ./
      | %
        /.$BeginJava
                    setResult(Binary.MOD);
          $EndJava
        ./
      | &
        /.$BeginJava
                    setResult(Binary.BIT_AND);
          $EndJava
        ./
      | '|'
        /.$BeginJava
                    setResult(Binary.BIT_OR);
          $EndJava
        ./
      | ^
        /.$BeginJava
                    setResult(Binary.BIT_XOR);
          $EndJava
        ./
      | &&
        /.$BeginJava
                    setResult(Binary.COND_AND);
          $EndJava
        ./
      | '||'
        /.$BeginJava
                    setResult(Binary.COND_OR);
          $EndJava
        ./
      | <<
        /.$BeginJava
                    setResult(Binary.SHL);
          $EndJava
        ./
      | >>
        /.$BeginJava
                    setResult(Binary.SHR);
          $EndJava
        ./
      | >>>
        /.$BeginJava
                    setResult(Binary.USHR);
          $EndJava
        ./
      | >=
        /.$BeginJava
                    setResult(Binary.GE);
          $EndJava
        ./
      | <=
        /.$BeginJava
                    setResult(Binary.LE);
          $EndJava
        ./
      | >
        /.$BeginJava
                    setResult(Binary.GT);
          $EndJava
        ./
      | <
        /.$BeginJava
                    setResult(Binary.LT);
          $EndJava
        ./
        
      -- FIXME: == and != shouldn't be allowed to be overridden.
              
      | ==
        /.$BeginJava
                    setResult(Binary.EQ);
          $EndJava
        ./
      | !=
        /.$BeginJava
                    setResult(Binary.NE);
          $EndJava
        ./
%End

%Types
    Object ::= ,opt
             | ObjectKind
             | ObjectKindopt
    Expr ::= PlaceType
    SourceFile ::= CompilationUnit
    polyglot.ast.Lit ::= Literal
    TypeNode ::= Type
    TypeNode ::= AnnotatedType
    TypeNode ::= NamedType
    TypeNode ::= ClassType
    ParsedName ::= SimpleName
    PackageNode ::= PackageDeclarationopt | PackageDeclaration
    List ::= ImportDeclarationsopt | ImportDeclarations
    List ::= TypeDeclarationsopt | TypeDeclarations
    Import ::= ImportDeclaration
    Import ::= SingleTypeImportDeclaration
    Import ::= TypeImportOnDemandDeclaration
    TopLevelDecl ::= TypeDeclaration
    List ::= ClassModifier
            | ClassModifiers
            | ClassModifiersopt
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
    List ::= VariableDeclarators | FormalDeclarators | FieldDeclarators
    List ::= VariableDeclaratorsWithType
    'Object[]' ::= VariableDeclarator
    'Object[]' ::= VariableDeclaratorWithType
    'Object[]' ::= FormalDeclarator
    'Object[]' ::= LoopIndexDeclarator
    'Object[]' ::= FieldDeclarator
    Expr ::= VariableInitializer
    ClassMember ::= MethodDeclaration 
    ClassMember ::= PropertyMethodDeclaration 
    List ::= FormalParameterListopt | FormalParameterList 
    List ::= FormalParametersopt | FormalParameters 
    List ::= ExistentialListopt | ExistentialList 
    X10Formal ::= FormalParameter
    X10Formal ::= LoopIndex
    Stmt ::= LoopStatement
    List ::= Throwsopt | Throws
    Block ::= MethodBody
    Initializer ::= StaticInitializer
    ConstructorDecl ::= ConstructorDeclaration
    Block ::= ConstructorBody
    Block ::= ConstructorBlock
    ConstructorCall ::= ExplicitConstructorInvocation
    ClassDecl ::= InterfaceDeclaration | NormalInterfaceDeclaration
    List ::= ExtendsInterfacesopt | ExtendsInterfaces
    ClassBody ::= InterfaceBody
    List ::= InterfaceMemberDeclarationsopt | InterfaceMemberDeclarations
    List ::= InterfaceMemberDeclaration
    ArrayInit ::= ArrayInitializer
    List ::= VariableInitializers
    Block ::= Block
    List ::= BlockStatementsopt | BlockStatements
    List ::= BlockStatement
    List ::= LocalVariableDeclarationStatement
    List ::= LocalVariableDeclaration
    Stmt ::= Statement
    Stmt ::= NonExpressionStatement
    Empty ::= EmptyStatement
    Labeled ::= LabeledStatement
    Stmt ::= ExpressionStatement
    Expr ::= StatementExpression
    If ::= IfThenStatement
    If ::= IfThenElseStatement
    Switch ::= SwitchStatement
    List ::= SwitchBlock | SwitchBlockStatementGroups
    List ::= SwitchBlockStatementGroup | SwitchLabels
    Case ::= SwitchLabel
    Expr ::= ConstantExpression
    While ::= WhileStatement
    Do ::= DoStatement
    For ::= ForStatement
    Stmt ::= AnnotationStatement
    List ::= ForInitopt | ForInit
    List ::= ForUpdateopt | ForUpdate
    List ::= StatementExpressionList
    polyglot.lex.Identifier ::= identifier
    Id ::= Identifieropt
    Branch ::= BreakStatement | ContinueStatement
    Return ::= ReturnStatement
    Throw ::= ThrowStatement
    Try ::= TryStatement
    List ::= Catchesopt | Catches
    Catch ::= CatchClause
    Block ::= Finally
    Assert ::= AssertStatement
    Expr ::= Primary
    Expr ::= OperatorFunction
    Expr ::= ClassInstanceCreationExpression
    List ::= ArgumentListopt | ArgumentList
    Field ::= FieldAccess 
    Call ::= MethodInvocation
    Expr ::= PostfixExpression
    Unary ::= PostIncrementExpression | PostDecrementExpression
    Expr ::= UnaryExpression | UnaryExpressionNotPlusMinus
    Unary ::= PreIncrementExpression | PreDecrementExpression
    Expr ::= CastExpression
    Expr ::= MultiplicativeExpression | AdditiveExpression
    Expr ::= ShiftExpression | RelationalExpression | EqualityExpression
    Expr ::= AndExpression | ExclusiveOrExpression | InclusiveOrExpression
    Expr ::= ConditionalAndExpression | ConditionalOrExpression
    Expr ::= ConditionalExpression | AssignmentExpression
    Expr ::= Assignment
    Expr ::= LeftHandSide
    Assign.Operator ::= AssignmentOperator
    Expr ::= Expressionopt | Expression

    ParsedName ::= TypeName
    ParsedName ::= ClassName
    ParsedName ::= PackageName
    ParsedName ::= ExpressionName
    ParsedName ::= AmbiguousName
    ParsedName ::= MethodName
    ParsedName ::= PackageOrTypeName
    Initializer ::= InstanceInitializer
    TypeNode ::= ResultType
    List ::= FormalParameters
    List ::= ExceptionTypeList
    TypeNode ::= ExceptionType
    ParsedName ::= SimpleTypeName
    Stmt ::= ExplicitConstructorInvocationopt
    List ::= Argumentsopt
           | Arguments
    List ::= VariableInitializersopt
    List ::= SwitchBlockStatementGroupsopt
    List ::= SwitchLabelsopt
    For ::= BasicForStatement
    For ::= EnhancedForStatement
    polyglot.lex.BooleanLiteral ::= BooleanLiteral
    TypeNode ::= ConstrainedType
    Expr ::= PlaceExpression
    DepParameterExpr ::= WhereClauseopt
    DepParameterExpr ::= WhereClause
    ClassDecl ::= ValueClassDeclaration
    Object ::= Unsafeopt
    Now ::= NowStatement
    Async ::= AsyncStatement
    AtStmt ::= AtStatement
    AtExpr ::= AtExpression
    Atomic ::= AtomicStatement
    When ::= WhenStatement
    ForEach ::= ForEachStatement
    AtEach ::= AtEachStatement
    Finish ::= FinishStatement
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
    Expr ::= AsyncExpression
    DepParameterExpr ::= DepParametersopt
                       | DepParameters
    List ::= Properties | Propertiesopt 
    List ::=  PropertyList | PropertyListopt
    PropertyDecl ::= Property
    List ::= Annotations | Annotationsopt
    AnnotationNode ::= Annotation
    Block ::= ClosureBody
    Stmt ::= LastExpression
    Expr ::= Conjunction
    Expr ::= ClosureExpression
    List ::=  TypeArguments | TypeArgumentsopt
    TypeParamNode ::= TypeParameter
    List ::=  TypeParameterList
    List ::=  TypeParameters | TypeParametersopt
    TypePropertyNode ::= TypeProperty
    TypeNode ::= ResultTypeopt
    List ::=  TypePropertyList
    List ::= TypeProperties | TypePropertiesopt
    List ::=  TypeArgumentList
    Id ::= Identifier
    List ::= IdentifierList
    List ::= VarKeyword | FieldKeyword
    List ::= TypeDefModifier | TypeDefModifiers | TypeDefModifiersopt
    Expr ::= MethodSelection
    Expr ::= SubtypeConstraint
    Expr ::= RangeExpression
    TypeDecl ::= TypeDefDeclaration
    Binary.Operator ::= BinOp
    Unary.Operator ::= PrefixOp
%End
