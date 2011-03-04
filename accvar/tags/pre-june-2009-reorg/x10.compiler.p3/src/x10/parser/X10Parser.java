
//#line 18 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
//
// Licensed Material
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

import lpg.runtime.*;

//#line 28 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
import java.util.Arrays;
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
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.ast.PropertyDecl;
import polyglot.ext.x10.ast.RegionMaker;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10Unary_c;
import polyglot.ext.x10.extension.X10Ext;
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
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
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

public class X10Parser implements RuleAction, Parser, ParseErrorCodes
{
    private PrsStream prsStream = null;
    
    private boolean unimplementedSymbolsWarning = false;

    private static ParseTable prsTable = new X10Parserprs();
    public ParseTable getParseTable() { return prsTable; }

    private BacktrackingParser btParser = null;
    public BacktrackingParser getParser() { return btParser; }

    private void setResult(Object object) { btParser.setSym1(object); }
    public Object getRhsSym(int i) { return btParser.getSym(i); }

    public int getRhsTokenIndex(int i) { return btParser.getToken(i); }
    public IToken getRhsIToken(int i) { return prsStream.getIToken(getRhsTokenIndex(i)); }
    
    public int getRhsFirstTokenIndex(int i) { return btParser.getFirstToken(i); }
    public IToken getRhsFirstIToken(int i) { return prsStream.getIToken(getRhsFirstTokenIndex(i)); }

    public int getRhsLastTokenIndex(int i) { return btParser.getLastToken(i); }
    public IToken getRhsLastIToken(int i) { return prsStream.getIToken(getRhsLastTokenIndex(i)); }

    public int getLeftSpan() { return btParser.getFirstToken(); }
    public IToken getLeftIToken()  { return prsStream.getIToken(getLeftSpan()); }

    public int getRightSpan() { return btParser.getLastToken(); }
    public IToken getRightIToken() { return prsStream.getIToken(getRightSpan()); }

    public int getRhsErrorTokenIndex(int i)
    {
        int index = btParser.getToken(i);
        IToken err = prsStream.getIToken(index);
        return (err instanceof ErrorToken ? index : 0);
    }
    public ErrorToken getRhsErrorIToken(int i)
    {
        int index = btParser.getToken(i);
        IToken err = prsStream.getIToken(index);
        return (ErrorToken) (err instanceof ErrorToken ? err : null);
    }

    public void reset(ILexStream lexStream)
    {
        prsStream = new PrsStream(lexStream);
        btParser.reset(prsStream);

        try
        {
            prsStream.remapTerminalSymbols(orderedTerminalSymbols(), prsTable.getEoftSymbol());
        }
        catch(NullExportedSymbolsException e) {
        }
        catch(NullTerminalSymbolsException e) {
        }
        catch(UnimplementedTerminalsException e)
        {
            if (unimplementedSymbolsWarning) {
                java.util.ArrayList unimplemented_symbols = e.getSymbols();
                System.out.println("The Lexer will not scan the following token(s):");
                for (int i = 0; i < unimplemented_symbols.size(); i++)
                {
                    Integer id = (Integer) unimplemented_symbols.get(i);
                    System.out.println("    " + X10Parsersym.orderedTerminalSymbols[id.intValue()]);               
                }
                System.out.println();
            }
        }
        catch(UndefinedEofSymbolException e)
        {
            throw new Error(new UndefinedEofSymbolException
                                ("The Lexer does not implement the Eof symbol " +
                                 X10Parsersym.orderedTerminalSymbols[prsTable.getEoftSymbol()]));
        } 
    }
    
    public X10Parser()
    {
        try
        {
            btParser = new BacktrackingParser(prsStream, prsTable, (RuleAction) this);
        }
        catch (NotBacktrackParseTableException e)
        {
            throw new Error(new NotBacktrackParseTableException
                                ("Regenerate X10Parserprs.java with -BACKTRACK option"));
        }
        catch (BadParseSymFileException e)
        {
            throw new Error(new BadParseSymFileException("Bad Parser Symbol File -- X10Parsersym.java"));
        }
    }
    
    public X10Parser(ILexStream lexStream)
    {
        this();
        reset(lexStream);
    }
    
    public int numTokenKinds() { return X10Parsersym.numTokenKinds; }
    public String[] orderedTerminalSymbols() { return X10Parsersym.orderedTerminalSymbols; }
    public String getTokenKindName(int kind) { return X10Parsersym.orderedTerminalSymbols[kind]; }
    public int getEOFTokenKind() { return prsTable.getEoftSymbol(); }
    public IPrsStream getIPrsStream() { return prsStream; }

    /**
     * @deprecated replaced by {@link #getIPrsStream()}
     *
     */
    public PrsStream getPrsStream() { return prsStream; }

    /**
     * @deprecated replaced by {@link #getIPrsStream()}
     *
     */
    public PrsStream getParseStream() { return prsStream; }

    public polyglot.ast.Node parser()
    {
        return parser(null, 0);
    }
    
    public polyglot.ast.Node parser(Monitor monitor)
    {
        return parser(monitor, 0);
    }
    
    public polyglot.ast.Node parser(int error_repair_count)
    {
        return parser(null, error_repair_count);
    }

    public polyglot.ast.Node parser(Monitor monitor, int error_repair_count)
    {
        btParser.setMonitor(monitor);
        
        try
        {
            return (polyglot.ast.Node) btParser.fuzzyParse(error_repair_count);
        }
        catch (BadParseException e)
        {
            prsStream.reset(e.error_token); // point to error token

            DiagnoseParser diagnoseParser = new DiagnoseParser(prsStream, prsTable);
            diagnoseParser.diagnose(e.error_token);
        }

        return null;
    }

    //
    // Additional entry points, if any
    //
    

    //#line 316 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
    private ErrorQueue eq;
    private X10TypeSystem ts;
    private X10NodeFactory nf;
    private FileSource source;
    private boolean unrecoverableSyntaxError = false;

    public void initialize(TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
    {
        this.ts = (X10TypeSystem) t;
        this.nf = (X10NodeFactory) n;
        this.source = source;
        this.eq = q;
    }
    
    public X10Parser(ILexStream lexStream, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
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
    

    public polyglot.ast.Node parse() {
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
        return new Identifier(pos(i), prsStream.getName(i), X10Parsersym.TK_IDENTIFIER);
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
        return new LongLiteral(pos(i),  x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.LongLiteral int_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i),  x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.LongLiteral long_lit(int i, int radix)
    {
        long x = parseLong(prsStream.getName(i), radix);
        return new LongLiteral(pos(i), x, X10Parsersym.TK_LongLiteral);
    }

    private polyglot.lex.LongLiteral long_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i), x, X10Parsersym.TK_LongLiteral);
    }

    private polyglot.lex.FloatLiteral float_lit(int i)
    {
        try {
            String s = prsStream.getName(i);
            int end_index = (s.charAt(s.length() - 1) == 'f' || s.charAt(s.length() - 1) == 'F'
                                                       ? s.length() - 1
                                                       : s.length());
            float x = Float.parseFloat(s.substring(0, end_index));
            return new FloatLiteral(pos(i), x, X10Parsersym.TK_FloatingPointLiteral);
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
            return new DoubleLiteral(pos(i), x, X10Parsersym.TK_DoubleLiteral);
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

        return new CharacterLiteral(pos(i), x, X10Parsersym.TK_CharacterLiteral);
    }

    private polyglot.lex.BooleanLiteral boolean_lit(int i)
    {
        return new BooleanLiteral(pos(i), prsStream.getKind(i) == X10Parsersym.TK_true, prsStream.getKind(i));
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

        return new StringLiteral(pos(i), new String(x, 0, k), X10Parsersym.TK_StringLiteral);
    }

    private polyglot.lex.NullLiteral null_lit(int i)
    {
        return new NullLiteral(pos(i), X10Parsersym.TK_null);
    }


    public void ruleAction(int ruleNumber)
    {
        switch (ruleNumber)
        {

            //
            // Rule 1:  TypeName ::= TypeName . ErrorId
            //
            case 1: {
               //#line 8 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 6 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      TypeName,
                                      nf.Id(pos(getRightSpan()), "*")));
                          break;
            }
        
            //
            // Rule 2:  PackageName ::= PackageName . ErrorId
            //
            case 2: {
               //#line 18 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 16 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      PackageName,
                                      nf.Id(pos(getRightSpan()), "*")));
                          break;
            }
        
            //
            // Rule 3:  ExpressionName ::= AmbiguousName . ErrorId
            //
            case 3: {
               //#line 28 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 26 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      nf.Id(pos(getRightSpan()), "*")));
                          break;
            }
        
            //
            // Rule 4:  MethodName ::= AmbiguousName . ErrorId
            //
            case 4: {
               //#line 38 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 36 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      nf.Id(pos(getRightSpan()), "*")));
                          break;
            }
        
            //
            // Rule 5:  PackageOrTypeName ::= PackageOrTypeName . ErrorId
            //
            case 5: {
               //#line 48 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 46 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      PackageOrTypeName,
                                      nf.Id(pos(getRightSpan()), "*")));
                          break;
            }
        
            //
            // Rule 6:  AmbiguousName ::= AmbiguousName . ErrorId
            //
            case 6: {
               //#line 58 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 56 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      nf.Id(pos(getRightSpan()), "*")));
                         break;
            }
        
            //
            // Rule 7:  FieldAccess ::= Primary . ErrorId
            //
            case 7: {
               //#line 68 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 66 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 74 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 78 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 85 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 92 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 98 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName ClassName = (ParsedName) ((Object[]) MethodClassNameSuperPrefix)[0];
                JPGPosition super_pos = (JPGPosition) ((Object[]) MethodClassNameSuperPrefix)[1];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodClassNameSuperPrefix)[2];
                setResult(nf.Call(pos(), nf.Super(super_pos, ClassName.toType()), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 13:  MethodPrimaryPrefix ::= Primary . ErrorId$ErrorId
            //
            case 13: {
               //#line 109 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 107 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Object[] a = new Object[2];
                a[0] = Primary;
                a[1] = id(getRhsFirstTokenIndex(3));
                setResult(a);
                      break;
            }
    
            //
            // Rule 14:  MethodSuperPrefix ::= super . ErrorId$ErrorId
            //
            case 14: {
               //#line 117 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 115 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 120 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Object[] a = new Object[3];
                a[0] = ClassName;
                a[1] = pos(getRhsFirstTokenIndex(3));
                a[2] = id(getRhsFirstTokenIndex(5));
                setResult(a);
                      break;
            }
    
            //
            // Rule 16:  TypeDefDeclaration ::= TypeDefModifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 16: {
               //#line 879 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 879 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode f = extractFlags(TypeDefModifiersopt);
                List annotations = extractAnnotations(TypeDefModifiersopt);
                for (Formal v : (List<Formal>) FormalParametersopt) {
                    if (!v.flags().flags().isFinal()) throw new InternalCompilerError("Type definition parameters must be final.", v.position()); 
                }
                TypeDecl cd = nf.TypeDecl(pos(), f, Identifier, TypeParametersopt, FormalParametersopt, WhereClauseopt, Type);
                cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 17:  TypeDefDeclaration ::= TypeDefModifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt ;
            //
            case 17: {
               //#line 891 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 889 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 889 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 889 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 889 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 889 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 891 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode f = extractFlags(TypeDefModifiersopt);
                List annotations = extractAnnotations(TypeDefModifiersopt);
                for (Formal v : (List<Formal>) FormalParametersopt) {
                    if (!v.flags().flags().isFinal()) throw new InternalCompilerError("Type definition parameters must be final.", v.position()); 
                }
                TypeDecl cd = nf.TypeDecl(pos(), f, Identifier, TypeParametersopt, FormalParametersopt, WhereClauseopt, null);
                cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 18:  Properties ::= ( PropertyList )
            //
            case 18: {
               //#line 904 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 902 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 904 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 19:  PropertyList ::= Property
            //
            case 19: {
               //#line 909 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 907 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 909 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 20:  PropertyList ::= PropertyList , Property
            //
            case 20: {
               //#line 916 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 914 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 914 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 916 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 21:  Property ::= Annotationsopt Identifier : Type
            //
            case 21: {
               //#line 923 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 921 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 921 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 921 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(4);
                //#line 923 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List annotations = extractAnnotations(Annotationsopt);
                PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), Type, Identifier);
                cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 22:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 22: {
               //#line 932 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 930 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 930 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 930 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 930 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 930 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 930 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 930 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 930 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 932 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(9)),
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
                      break;
            }
    
            //
            // Rule 23:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 23: {
               //#line 962 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 960 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 960 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 960 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 960 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 960 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 960 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 960 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 960 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 960 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 962 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(14)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(7)), X10Binary_c.binaryMethodName(BinOp)),
          TypeParametersopt,
          Arrays.<Formal>asList(fp1, fp2),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Binary operator with two parameters must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 24:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 24: {
               //#line 979 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 979 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(11)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(4)), X10Unary_c.unaryMethodName(PrefixOp)),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp2),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Unary operator with two parameters must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 25:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 25: {
               //#line 996 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 994 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 994 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 994 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 994 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 994 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 994 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 994 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 994 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 996 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(12)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(5)), X10Binary_c.binaryMethodName(BinOp)),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp2),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Binary operator with this parameter cannot be static.", md.position());
          
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 26:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 26: {
               //#line 1014 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1012 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1012 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1012 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1012 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1012 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1012 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1012 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1012 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1014 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       Name op = X10Binary_c.invBinaryMethodName(BinOp);
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(12)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(7)), X10Binary_c.invBinaryMethodName(BinOp)),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp1),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Binary operator with this parameter cannot be static.", md.position());
          
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 27:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 27: {
               //#line 1033 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1031 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1031 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1031 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1031 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1031 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1031 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1031 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1033 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(9)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(4)), X10Unary_c.unaryMethodName(PrefixOp)),
          TypeParametersopt,
          Collections.EMPTY_LIST,
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Unary operator with this parameter cannot be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 28:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 28: {
               //#line 1050 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1048 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1048 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1048 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1048 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1048 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1048 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1048 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1050 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(9)),
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
                      break;
            }
    
            //
            // Rule 29:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 29: {
               //#line 1067 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1065 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1065 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1065 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1065 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1065 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1065 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1065 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1065 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1067 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(13)),
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
                      break;
            }
    
            //
            // Rule 30:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Throwsopt MethodBody
            //
            case 30: {
               //#line 1084 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1082 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1082 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1082 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1082 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1082 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1082 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1082 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1084 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(11)),
          extractFlags(MethodModifiersopt),
          Type,
          nf.Id(pos(), Name.make("\044convert")), // DOLLAR convert, but DOLLAR is special in lpg
          TypeParametersopt,
          Collections.<Formal>singletonList(fp1),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Conversion operator must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 31:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 31: {
               //#line 1101 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1099 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1099 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1099 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1099 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1099 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1099 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1099 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1101 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(12)),
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
                      break;
            }
    
            //
            // Rule 32:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 32: {
               //#line 1120 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1118 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1118 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1118 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1118 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1118 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1118 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1118 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1118 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1120 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(9)),
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
                      break;
            }
    
            //
            // Rule 33:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier WhereClauseopt ResultTypeopt MethodBody
            //
            case 33: {
               //#line 1135 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1133 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1133 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1133 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(4);
                //#line 1133 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 1133 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 1135 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(6)),
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
                      break;
            }
    
            //
            // Rule 34:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 34: {
               //#line 1151 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1149 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1149 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1151 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 35:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 35: {
               //#line 1156 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1154 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1154 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1156 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 36:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 36: {
               //#line 1161 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1159 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1159 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1159 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1161 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 37:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 37: {
               //#line 1166 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1164 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1164 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1164 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1166 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 38:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 38: {
               //#line 1172 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1170 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 1170 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1170 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1170 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1170 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1170 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1170 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1172 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
      checkTypeName(Identifier);
      List TypeParametersopt = TypeParamsWithVarianceopt;
      List/*<PropertyDecl>*/ props = Propertiesopt;
      DepParameterExpr ci = WhereClauseopt;
      FlagsNode fn = extractFlags(InterfaceModifiersopt, Flags.INTERFACE);
      ClassDecl cd = nf.X10ClassDecl(pos(),
                   fn,
                   Identifier,
                   TypeParametersopt,
                   props,
                   ci,
                   null,
                   ExtendsInterfacesopt,
                   InterfaceBody);
      cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(InterfaceModifiersopt));
      setResult(cd);
                      break;
            }
    
            //
            // Rule 39:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 39: {
               //#line 1193 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1191 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1191 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1191 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1191 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1193 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 40:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 40: {
               //#line 1200 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1198 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1198 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1198 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1198 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1198 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1200 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 41:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 41: {
               //#line 1208 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1206 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1206 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1206 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1206 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1206 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1208 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 42:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 42: {
               //#line 1217 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1215 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1215 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1217 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 45:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt => Type
            //
            case 45: {
               //#line 1229 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1227 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1227 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1227 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1227 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1227 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1229 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt));
                      break;
            }
    
            //
            // Rule 50:  AnnotatedType ::= Type Annotations
            //
            case 50: {
               //#line 1238 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1236 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1236 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1238 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn);
                      break;
            }
    
            //
            // Rule 53:  ConstrainedType ::= ( Type )
            //
            case 53: {
               //#line 1248 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1246 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1248 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 54:  PlaceType ::= PlaceExpression
            //
            case 54: {
               //#line 1254 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1252 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(1);
                //#line 1254 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(),
                                    nf.Field(pos(), nf.This(pos()), nf.Id(pos(), "loc")), Binary.EQ,
                                    PlaceExpression));
                      break;
            }
    
            //
            // Rule 55:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 55: {
               //#line 1262 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1260 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1260 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1260 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1260 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1260 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1262 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.X10AmbTypeNode(pos(), Primary, Identifier);
            // TODO: place constraint
            if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                type = nf.AmbDepTypeNode(pos(), Primary, Identifier, TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                      break;
            }
    
            //
            // Rule 56:  NamedType ::= TypeName TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 56: {
               //#line 1273 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1271 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1271 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1271 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1271 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1273 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 57:  DepParameters ::= { ExistentialListopt Conjunction }
            //
            case 57: {
               //#line 1299 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1297 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1297 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(3);
                //#line 1299 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
                      break;
            }
    
            //
            // Rule 58:  DepParameters ::= { ExistentialListopt Conjunction } !
            //
            case 58: {
               //#line 1304 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1302 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1302 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(3);
                //#line 1304 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
                      break;
            }
    
            //
            // Rule 59:  DepParameters ::= { ExistentialListopt Conjunction } ! PlaceType
            //
            case 59: {
               //#line 1309 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1307 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1307 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(3);
                //#line 1307 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(6);
                //#line 1309 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                if (PlaceType != null)
                    setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(PlaceType))));
            else
		setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
                      break;
            }
    
            //
            // Rule 60:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 60: {
               //#line 1318 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1316 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1318 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 61:  TypeParameters ::= [ TypeParameterList ]
            //
            case 61: {
               //#line 1324 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1322 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1324 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 62:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 62: {
               //#line 1329 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1327 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1329 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 63:  Conjunction ::= Expression
            //
            case 63: {
               //#line 1335 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1333 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1335 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 64:  Conjunction ::= Conjunction , Expression
            //
            case 64: {
               //#line 1342 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1340 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1340 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1342 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 65:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 65: {
               //#line 1348 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1346 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1346 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1348 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 66:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 66: {
               //#line 1353 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1351 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1351 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1353 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 67:  WhereClause ::= DepParameters
            //
            case 67: {
               //#line 1359 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1357 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1359 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 68:  ExistentialListopt ::= $Empty
            //
            case 68: {
               //#line 1365 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1365 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 69:  ExistentialListopt ::= ExistentialList ;
            //
            case 69: {
               //#line 1370 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1368 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1370 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 70:  ExistentialList ::= FormalParameter
            //
            case 70: {
               //#line 1376 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1374 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1376 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 71:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 71: {
               //#line 1383 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1381 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1381 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1383 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 74:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 74: {
               //#line 1394 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1392 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1392 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1392 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1392 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1392 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1392 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1392 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1392 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1394 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
      checkTypeName(Identifier);
                List TypeParametersopt = TypeParamsWithVarianceopt;
      List/*<PropertyDecl>*/ props = Propertiesopt;
      DepParameterExpr ci = WhereClauseopt;
      FlagsNode f = extractFlags(ClassModifiersopt);
      List annotations = extractAnnotations(ClassModifiersopt);
      ClassDecl cd = nf.X10ClassDecl(pos(),
              f, Identifier, TypeParametersopt, props, ci, Superopt, Interfacesopt, ClassBody);
      cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(annotations);
      setResult(cd);
                      break;
            }
    
            //
            // Rule 75:  ValueClassDeclaration ::= ClassModifiersopt value Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 75: {
               //#line 1409 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1407 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1407 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1407 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1407 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1407 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1407 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1407 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1407 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1409 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
    checkTypeName(Identifier);
                List TypeParametersopt = TypeParamsWithVarianceopt;
    List props = Propertiesopt;
    DepParameterExpr ci = WhereClauseopt;
    ClassDecl cd = (nf.X10ClassDecl(pos(getLeftSpan(), getRightSpan()),
    extractFlags(ClassModifiersopt, X10Flags.VALUE), Identifier,  TypeParametersopt,
    props, ci, Superopt, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ClassModifiersopt));
    setResult(cd);
                      break;
            }
    
            //
            // Rule 76:  ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt ConstructorBody
            //
            case 76: {
               //#line 1423 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1421 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1421 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1421 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1421 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1421 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1421 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1421 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(9);
                //#line 1423 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                     break;
            }
   
            //
            // Rule 77:  Super ::= extends ClassType
            //
            case 77: {
               //#line 1439 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1437 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1439 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 78:  FieldKeyword ::= val
            //
            case 78: {
               //#line 1445 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1445 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 79:  FieldKeyword ::= var
            //
            case 79: {
               //#line 1450 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1450 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 80:  FieldKeyword ::= const
            //
            case 80: {
               //#line 1455 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1455 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 81:  VarKeyword ::= val
            //
            case 81: {
               //#line 1463 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1463 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 82:  VarKeyword ::= var
            //
            case 82: {
               //#line 1468 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1468 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 83:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 83: {
               //#line 1475 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1473 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1473 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1473 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1475 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 84:  FieldDeclaration ::= FieldModifiersopt FieldDeclarators ;
            //
            case 84: {
               //#line 1499 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1497 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1497 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1499 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 114:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 114: {
               //#line 1558 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1556 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1556 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1558 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 115:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 115: {
               //#line 1564 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1562 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1562 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1562 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1564 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 116:  EmptyStatement ::= ;
            //
            case 116: {
               //#line 1570 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1570 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 117:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 117: {
               //#line 1576 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1574 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1574 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 1576 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 123:  ExpressionStatement ::= StatementExpression ;
            //
            case 123: {
               //#line 1588 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1586 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1588 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 131:  AssertStatement ::= assert Expression ;
            //
            case 131: {
               //#line 1619 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1617 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1619 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 132:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 132: {
               //#line 1624 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1622 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1622 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1624 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 133:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 133: {
               //#line 1630 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1628 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1628 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1630 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 134:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 134: {
               //#line 1636 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1634 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1634 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1636 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 136:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 136: {
               //#line 1644 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1642 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1642 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1644 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 137:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 137: {
               //#line 1651 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1649 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1649 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1651 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 138:  SwitchLabels ::= SwitchLabel
            //
            case 138: {
               //#line 1660 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1658 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1660 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 139:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 139: {
               //#line 1667 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1665 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1665 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1667 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 140:  SwitchLabel ::= case ConstantExpression :
            //
            case 140: {
               //#line 1674 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1672 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1674 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 141:  SwitchLabel ::= default :
            //
            case 141: {
               //#line 1679 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1679 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 142:  WhileStatement ::= while ( Expression ) Statement
            //
            case 142: {
               //#line 1685 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1683 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1683 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1685 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 143:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 143: {
               //#line 1691 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1689 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1689 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1691 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 146:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 146: {
               //#line 1700 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1698 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1698 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1698 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1698 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1700 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 148:  ForInit ::= LocalVariableDeclaration
            //
            case 148: {
               //#line 1707 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1705 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1707 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 150:  StatementExpressionList ::= StatementExpression
            //
            case 150: {
               //#line 1717 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1715 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1717 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 151:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 151: {
               //#line 1724 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1722 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1722 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1724 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 152:  BreakStatement ::= break Identifieropt ;
            //
            case 152: {
               //#line 1730 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1728 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1730 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 153:  ContinueStatement ::= continue Identifieropt ;
            //
            case 153: {
               //#line 1736 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1734 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1736 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 154:  ReturnStatement ::= return Expressionopt ;
            //
            case 154: {
               //#line 1742 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1740 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1742 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 155:  ThrowStatement ::= throw Expression ;
            //
            case 155: {
               //#line 1748 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1746 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1748 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 156:  TryStatement ::= try Block Catches
            //
            case 156: {
               //#line 1754 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1752 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1752 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1754 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 157:  TryStatement ::= try Block Catchesopt Finally
            //
            case 157: {
               //#line 1759 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1757 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1757 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1757 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1759 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 158:  Catches ::= CatchClause
            //
            case 158: {
               //#line 1765 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1763 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1765 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 159:  Catches ::= Catches CatchClause
            //
            case 159: {
               //#line 1772 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1770 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1770 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1772 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 160:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 160: {
               //#line 1779 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1777 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1777 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1779 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 161:  Finally ::= finally Block
            //
            case 161: {
               //#line 1785 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1783 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1785 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 162:  NowStatement ::= now ( Clock ) Statement
            //
            case 162: {
               //#line 1791 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1789 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1789 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1791 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Now(pos(), Clock, Statement));
                      break;
            }
    
            //
            // Rule 163:  ClockedClause ::= clocked ( ClockList )
            //
            case 163: {
               //#line 1797 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1795 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1797 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 164:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 164: {
               //#line 1803 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1801 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1801 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1801 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1803 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 165:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 165: {
               //#line 1812 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1810 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1810 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1812 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 166:  AtomicStatement ::= atomic Statement
            //
            case 166: {
               //#line 1818 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1816 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1818 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 167:  WhenStatement ::= when ( Expression ) Statement
            //
            case 167: {
               //#line 1825 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1823 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1823 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1825 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 168:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 168: {
               //#line 1830 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1828 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1828 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1828 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1828 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1830 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 169:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 169: {
               //#line 1837 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1835 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1835 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1835 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1835 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1837 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                Flags f = fn.flags();
                f = f.Final();
                fn = fn.flags(f);
                setResult(nf.ForEach(pos(),
                              LoopIndex.flags(fn),
                              Expression,
                              ClockedClauseopt,
                              Statement));
                      break;
            }
    
            //
            // Rule 170:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 170: {
               //#line 1851 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1849 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1849 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1849 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1849 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1851 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                Flags f = fn.flags();
                f = f.Final();
                fn = fn.flags(f);
                setResult(nf.AtEach(pos(),
                             LoopIndex.flags(fn),
                             Expression,
                             ClockedClauseopt,
                             Statement));
                      break;
            }
    
            //
            // Rule 171:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 171: {
               //#line 1865 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1863 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1863 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1863 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1865 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                Flags f = fn.flags();
                f = f.Final();
                fn = fn.flags(f);
                setResult(nf.ForLoop(pos(),
                        LoopIndex.flags(fn),
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 172:  FinishStatement ::= finish Statement
            //
            case 172: {
               //#line 1878 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1876 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1878 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 173:  AnnotationStatement ::= Annotations Statement
            //
            case 173: {
               //#line 1885 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1883 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 1883 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1885 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                if (Statement.ext() instanceof X10Ext && Annotations instanceof List) {
                    Statement = (Stmt) ((X10Ext) Statement.ext()).annotations((List) Annotations);
                }
                setResult(Statement);
                      break;
            }
    
            //
            // Rule 174:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 174: {
               //#line 1894 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1892 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1894 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 176:  NextStatement ::= next ;
            //
            case 176: {
               //#line 1902 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1902 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 177:  AwaitStatement ::= await Expression ;
            //
            case 177: {
               //#line 1908 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1906 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1908 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 178:  ClockList ::= Clock
            //
            case 178: {
               //#line 1914 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1912 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1914 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 179:  ClockList ::= ClockList , Clock
            //
            case 179: {
               //#line 1921 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1919 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1919 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1921 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 180:  Clock ::= Expression
            //
            case 180: {
               //#line 1929 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1927 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1929 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 181:  CastExpression ::= CastExpression as Type
            //
            case 181: {
               //#line 1943 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1941 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 1941 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1943 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 182:  CastExpression ::= ConditionalExpression ! Expression
            //
            case 182: {
               //#line 1948 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1946 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1946 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1948 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.PlaceCast(pos(), Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 184:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 184: {
               //#line 1957 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1955 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 1957 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 185:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 185: {
               //#line 1964 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1962 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 1962 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 1964 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 186:  TypeParameterList ::= TypeParameter
            //
            case 186: {
               //#line 1971 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1969 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 1971 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 187:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 187: {
               //#line 1978 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1976 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 1976 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 1978 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 188:  TypeParamWithVariance ::= Identifier
            //
            case 188: {
               //#line 1985 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1983 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1985 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 189:  TypeParamWithVariance ::= + Identifier
            //
            case 189: {
               //#line 1990 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1988 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1990 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 190:  TypeParamWithVariance ::= - Identifier
            //
            case 190: {
               //#line 1995 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1993 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1995 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 191:  TypeParameter ::= Identifier
            //
            case 191: {
               //#line 2001 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1999 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2001 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 192:  Primary ::= here
            //
            case 192: {
               //#line 2007 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2007 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 194:  RegionExpressionList ::= RegionExpression
            //
            case 194: {
               //#line 2015 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2013 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2015 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 195:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 195: {
               //#line 2022 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2020 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2020 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2022 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 196:  Primary ::= [ ArgumentListopt ]
            //
            case 196: {
               //#line 2029 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2027 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 2029 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 197:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 197: {
               //#line 2036 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2034 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2034 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2036 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 198:  ClosureExpression ::= TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt => ClosureBody
            //
            case 198: {
               //#line 2043 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2041 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(1);
                //#line 2041 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(2);
                //#line 2041 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 2041 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2041 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 2041 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2043 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), TypeParametersopt, FormalParameters, WhereClauseopt, 
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 199:  LastExpression ::= Expression
            //
            case 199: {
               //#line 2050 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2048 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2050 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 200:  ClosureBody ::= CastExpression
            //
            case 200: {
               //#line 2056 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2054 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2056 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), CastExpression, true)));
                      break;
            }
    
            //
            // Rule 201:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 201: {
               //#line 2061 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2059 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2059 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2059 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2061 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 202:  ClosureBody ::= Annotationsopt Block
            //
            case 202: {
               //#line 2071 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2069 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2069 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2071 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 203:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 203: {
               //#line 2080 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2078 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2078 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2080 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 204:  AsyncExpression ::= async ClosureBody
            //
            case 204: {
               //#line 2086 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2084 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2086 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 205:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 205: {
               //#line 2091 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2089 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2089 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2091 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 206:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 206: {
               //#line 2096 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2094 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2094 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2096 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 207:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 207: {
               //#line 2101 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2099 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2099 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2099 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2101 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 208:  FutureExpression ::= future ClosureBody
            //
            case 208: {
               //#line 2107 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2105 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2107 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 209:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 209: {
               //#line 2112 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2110 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2110 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2112 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 210:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 210: {
               //#line 2117 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2115 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2115 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2117 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 211:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 211: {
               //#line 2122 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2120 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2120 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2120 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2122 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 212:  DepParametersopt ::= $Empty
            //
            case 212:
                setResult(null);
                break;

            //
            // Rule 214:  PropertyListopt ::= $Empty
            //
            case 214: {
               //#line 2133 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2133 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 216:  WhereClauseopt ::= $Empty
            //
            case 216:
                setResult(null);
                break;

            //
            // Rule 218:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 218:
                setResult(null);
                break;

            //
            // Rule 220:  ClassModifiersopt ::= $Empty
            //
            case 220: {
               //#line 2148 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2148 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 222:  TypeDefModifiersopt ::= $Empty
            //
            case 222: {
               //#line 2154 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2154 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 224:  Unsafeopt ::= $Empty
            //
            case 224:
                setResult(null);
                break;

            //
            // Rule 225:  Unsafeopt ::= unsafe
            //
            case 225: {
               //#line 2162 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2162 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                // any value distinct from null
                setResult(this);
                      break;
            }
    
            //
            // Rule 226:  ClockedClauseopt ::= $Empty
            //
            case 226: {
               //#line 2169 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2169 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 228:  identifier ::= IDENTIFIER$ident
            //
            case 228: {
               //#line 2180 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2178 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2180 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 229:  TypeName ::= Identifier
            //
            case 229: {
               //#line 2187 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2185 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2187 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 230:  TypeName ::= TypeName . Identifier
            //
            case 230: {
               //#line 2192 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2190 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2190 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2192 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 232:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 232: {
               //#line 2204 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2202 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2204 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 233:  TypeArgumentList ::= Type
            //
            case 233: {
               //#line 2211 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2209 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2211 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 234:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 234: {
               //#line 2218 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2216 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2216 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2218 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 235:  PackageName ::= Identifier
            //
            case 235: {
               //#line 2228 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2226 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2228 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 236:  PackageName ::= PackageName . Identifier
            //
            case 236: {
               //#line 2233 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2231 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2231 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2233 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 237:  ExpressionName ::= Identifier
            //
            case 237: {
               //#line 2249 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2247 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2249 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 238:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 238: {
               //#line 2254 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2252 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2252 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2254 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 239:  MethodName ::= Identifier
            //
            case 239: {
               //#line 2264 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2262 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2264 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 240:  MethodName ::= AmbiguousName . Identifier
            //
            case 240: {
               //#line 2269 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2267 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2267 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2269 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 241:  PackageOrTypeName ::= Identifier
            //
            case 241: {
               //#line 2279 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2277 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2279 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 242:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 242: {
               //#line 2284 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2282 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2282 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2284 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 243:  AmbiguousName ::= Identifier
            //
            case 243: {
               //#line 2294 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2292 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2294 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 244:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 244: {
               //#line 2299 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2297 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2297 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2299 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 245:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 245: {
               //#line 2311 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2309 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2309 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2309 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2311 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                // Add import x10.lang.* by default.
                int token_pos = (ImportDeclarationsopt.size() == 0
                                     ? TypeDeclarationsopt.size() == 0
                                           ? prsStream.getSize() - 1
                                           : prsStream.getPrevious(getRhsFirstTokenIndex(3))
                                 : getRhsLastTokenIndex(2)
                            );
//                    Import x10LangImport = 
//                    nf.Import(pos(token_pos), Import.PACKAGE, QName.make("x10.lang"));
//                    ImportDeclarationsopt.add(x10LangImport);
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()), PackageDeclarationopt, ImportDeclarationsopt, TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 246:  ImportDeclarations ::= ImportDeclaration
            //
            case 246: {
               //#line 2327 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2325 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2327 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 247:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 247: {
               //#line 2334 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2332 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2332 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2334 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 248:  TypeDeclarations ::= TypeDeclaration
            //
            case 248: {
               //#line 2342 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2340 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2342 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 249:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 249: {
               //#line 2350 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2348 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2348 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2350 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 250:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 250: {
               //#line 2358 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2356 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2356 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2358 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn);
                      break;
            }
    
            //
            // Rule 253:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 253: {
               //#line 2372 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2370 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2372 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 254:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 254: {
               //#line 2378 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2376 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2378 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 258:  TypeDeclaration ::= ;
            //
            case 258: {
               //#line 2393 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2393 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 259:  ClassModifiers ::= ClassModifier
            //
            case 259: {
               //#line 2401 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2399 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2401 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 260:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 260: {
               //#line 2408 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2406 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2406 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2408 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 261:  ClassModifier ::= Annotation
            //
            case 261: {
               //#line 2414 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2412 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2414 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 262:  ClassModifier ::= public
            //
            case 262: {
               //#line 2419 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2419 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 263:  ClassModifier ::= protected
            //
            case 263: {
               //#line 2424 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2424 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 264:  ClassModifier ::= private
            //
            case 264: {
               //#line 2429 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2429 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 265:  ClassModifier ::= abstract
            //
            case 265: {
               //#line 2434 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2434 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 266:  ClassModifier ::= static
            //
            case 266: {
               //#line 2439 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2439 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 267:  ClassModifier ::= final
            //
            case 267: {
               //#line 2444 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2444 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 268:  ClassModifier ::= strictfp
            //
            case 268: {
               //#line 2449 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2449 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 269:  ClassModifier ::= safe
            //
            case 269: {
               //#line 2454 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2454 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 270:  ClassModifier ::= value
            //
            case 270: {
               //#line 2459 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2459 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.VALUE)));
                      break;
            }
    
            //
            // Rule 271:  TypeDefModifiers ::= TypeDefModifier
            //
            case 271: {
               //#line 2465 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2463 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2465 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 272:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 272: {
               //#line 2472 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2470 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2470 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2472 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 273:  TypeDefModifier ::= Annotation
            //
            case 273: {
               //#line 2478 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2476 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2478 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 274:  TypeDefModifier ::= public
            //
            case 274: {
               //#line 2483 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2483 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 275:  TypeDefModifier ::= protected
            //
            case 275: {
               //#line 2488 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2488 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 276:  TypeDefModifier ::= private
            //
            case 276: {
               //#line 2493 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2493 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 277:  TypeDefModifier ::= abstract
            //
            case 277: {
               //#line 2498 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2498 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 278:  TypeDefModifier ::= static
            //
            case 278: {
               //#line 2503 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2503 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 279:  TypeDefModifier ::= final
            //
            case 279: {
               //#line 2508 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2508 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 280:  Interfaces ::= implements InterfaceTypeList
            //
            case 280: {
               //#line 2517 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2515 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2517 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 281:  InterfaceTypeList ::= Type
            //
            case 281: {
               //#line 2523 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2521 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2523 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 282:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 282: {
               //#line 2530 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2528 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2528 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2530 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 283:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 283: {
               //#line 2540 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2538 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2540 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 285:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 285: {
               //#line 2547 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2545 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2545 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2547 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 287:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 287: {
               //#line 2555 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2553 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Initializer InstanceInitializer = (Initializer) getRhsSym(1);
                //#line 2555 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InstanceInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 288:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 288: {
               //#line 2562 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2560 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Initializer StaticInitializer = (Initializer) getRhsSym(1);
                //#line 2562 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(StaticInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 289:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 289: {
               //#line 2569 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2567 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2569 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 291:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 291: {
               //#line 2578 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2576 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2578 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 292:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 292: {
               //#line 2585 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2583 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2585 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 293:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 293: {
               //#line 2592 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2590 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2592 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 294:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 294: {
               //#line 2599 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2597 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2599 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 295: {
               //#line 2606 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2604 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2606 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 296:  ClassMemberDeclaration ::= ;
            //
            case 296: {
               //#line 2613 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2613 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  FormalDeclarators ::= FormalDeclarator
            //
            case 297: {
               //#line 2620 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2618 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2620 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 298:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 298: {
               //#line 2627 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2625 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2625 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2627 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 299:  FieldDeclarators ::= FieldDeclarator
            //
            case 299: {
               //#line 2634 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2632 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2634 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 300:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 300: {
               //#line 2641 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2639 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2639 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2641 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 301:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 301: {
               //#line 2649 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2647 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2649 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 302:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 302: {
               //#line 2656 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2654 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2654 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2656 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 303:  VariableDeclarators ::= VariableDeclarator
            //
            case 303: {
               //#line 2663 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2661 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2663 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 304:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 304: {
               //#line 2670 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2668 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2668 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2670 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 306:  FieldModifiers ::= FieldModifier
            //
            case 306: {
               //#line 2679 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2677 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2679 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 307:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 307: {
               //#line 2686 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2684 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2684 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2686 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 308:  FieldModifier ::= Annotation
            //
            case 308: {
               //#line 2692 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2690 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2692 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 309:  FieldModifier ::= public
            //
            case 309: {
               //#line 2697 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2697 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 310:  FieldModifier ::= protected
            //
            case 310: {
               //#line 2702 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2702 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 311:  FieldModifier ::= private
            //
            case 311: {
               //#line 2707 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2707 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 312:  FieldModifier ::= static
            //
            case 312: {
               //#line 2712 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2712 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 313:  FieldModifier ::= transient
            //
            case 313: {
               //#line 2717 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2717 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.TRANSIENT)));
                      break;
            }
    
            //
            // Rule 314:  FieldModifier ::= volatile
            //
            case 314: {
               //#line 2722 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2722 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.VOLATILE)));
                      break;
            }
    
            //
            // Rule 315:  ResultType ::= : Type
            //
            case 315: {
               //#line 2728 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2726 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2728 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 316:  FormalParameters ::= ( FormalParameterList )
            //
            case 316: {
               //#line 2734 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2732 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2734 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterList);
                      break;
            }
    
            //
            // Rule 317:  FormalParameterList ::= FormalParameter
            //
            case 317: {
               //#line 2740 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2738 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2740 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 318:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 318: {
               //#line 2747 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2745 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2745 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2747 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 319:  LoopIndexDeclarator ::= Identifier ResultTypeopt
            //
            case 319: {
               //#line 2753 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2751 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2751 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2753 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 320:  LoopIndexDeclarator ::= ( IdentifierList ) ResultTypeopt
            //
            case 320: {
               //#line 2758 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2756 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2756 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2758 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 321:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt
            //
            case 321: {
               //#line 2763 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2761 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2761 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2761 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2763 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 322:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 322: {
               //#line 2769 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2767 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2767 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2769 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 323:  LoopIndex ::= VariableModifiersopt VarKeyword LoopIndexDeclarator
            //
            case 323: {
               //#line 2792 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2790 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2790 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2790 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2792 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 324:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 324: {
               //#line 2816 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2814 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2814 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2816 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 325:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 325: {
               //#line 2840 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2838 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2838 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2838 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2840 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 326:  FormalParameter ::= Type
            //
            case 326: {
               //#line 2864 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2862 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2864 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh()), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 327:  VariableModifiers ::= VariableModifier
            //
            case 327: {
               //#line 2872 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2870 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2872 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 328:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 328: {
               //#line 2879 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2879 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 329:  VariableModifier ::= Annotation
            //
            case 329: {
               //#line 2885 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2883 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2885 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 330:  VariableModifier ::= shared
            //
            case 330: {
               //#line 2890 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2890 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 331:  MethodModifiers ::= MethodModifier
            //
            case 331: {
               //#line 2899 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2897 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2899 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 332:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 332: {
               //#line 2906 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2904 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2904 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2906 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 333:  MethodModifier ::= Annotation
            //
            case 333: {
               //#line 2912 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2910 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2912 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 334:  MethodModifier ::= public
            //
            case 334: {
               //#line 2917 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2917 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 335:  MethodModifier ::= protected
            //
            case 335: {
               //#line 2922 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2922 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 336:  MethodModifier ::= private
            //
            case 336: {
               //#line 2927 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2927 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 337:  MethodModifier ::= abstract
            //
            case 337: {
               //#line 2932 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2932 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 338:  MethodModifier ::= static
            //
            case 338: {
               //#line 2937 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2937 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 339:  MethodModifier ::= final
            //
            case 339: {
               //#line 2942 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2942 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 340:  MethodModifier ::= native
            //
            case 340: {
               //#line 2947 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2947 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= strictfp
            //
            case 341: {
               //#line 2952 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2952 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= atomic
            //
            case 342: {
               //#line 2957 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2957 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= extern
            //
            case 343: {
               //#line 2962 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2962 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= safe
            //
            case 344: {
               //#line 2967 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2967 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= sequential
            //
            case 345: {
               //#line 2972 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2972 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= local
            //
            case 346: {
               //#line 2977 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2977 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.LOCAL)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= nonblocking
            //
            case 347: {
               //#line 2982 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2982 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 348:  MethodModifier ::= incomplete
            //
            case 348: {
               //#line 2987 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2987 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 349:  MethodModifier ::= property
            //
            case 349: {
               //#line 2992 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2992 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 350:  Throws ::= throws ExceptionTypeList
            //
            case 350: {
               //#line 2999 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2997 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 2999 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 351:  ExceptionTypeList ::= ExceptionType
            //
            case 351: {
               //#line 3005 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3003 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3005 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 352:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 352: {
               //#line 3012 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3010 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3010 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3012 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 354:  MethodBody ::= = LastExpression ;
            //
            case 354: {
               //#line 3020 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3018 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3020 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 355:  MethodBody ::= = { BlockStatementsopt LastExpression }
            //
            case 355: {
               //#line 3025 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3023 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3023 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 3025 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 356:  MethodBody ::= = Block
            //
            case 356: {
               //#line 3033 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3031 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3033 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 357:  MethodBody ::= Block
            //
            case 357: {
               //#line 3038 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3036 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(1);
                //#line 3038 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 358:  MethodBody ::= ;
            //
            case 358:
                setResult(null);
                break;

            //
            // Rule 359:  InstanceInitializer ::= Block
            //
            case 359: {
               //#line 3046 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3044 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(1);
                //#line 3046 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(), Flags.NONE), Block));
                      break;
            }
    
            //
            // Rule 360:  StaticInitializer ::= static Block
            //
            case 360: {
               //#line 3052 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3050 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3052 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(getLeftSpan()), Flags.STATIC), Block));
                      break;
            }
    
            //
            // Rule 361:  SimpleTypeName ::= Identifier
            //
            case 361: {
               //#line 3058 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3056 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3058 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 362:  ConstructorModifiers ::= ConstructorModifier
            //
            case 362: {
               //#line 3064 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3062 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3064 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 363:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 363: {
               //#line 3071 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3069 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3069 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3071 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 364:  ConstructorModifier ::= Annotation
            //
            case 364: {
               //#line 3077 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3075 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3077 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 365:  ConstructorModifier ::= public
            //
            case 365: {
               //#line 3082 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3082 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 366:  ConstructorModifier ::= protected
            //
            case 366: {
               //#line 3087 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3087 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 367:  ConstructorModifier ::= private
            //
            case 367: {
               //#line 3092 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3092 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 368:  ConstructorModifier ::= native
            //
            case 368: {
               //#line 3097 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3097 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 369:  ConstructorBody ::= = ConstructorBlock
            //
            case 369: {
               //#line 3103 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3101 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3103 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 370:  ConstructorBody ::= ConstructorBlock
            //
            case 370: {
               //#line 3108 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3106 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3108 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 371:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 371: {
               //#line 3113 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3111 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3113 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 372:  ConstructorBody ::= = AssignPropertyCall
            //
            case 372: {
               //#line 3121 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3119 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3121 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.SuperCall(pos(), Collections.EMPTY_LIST));
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 373:  ConstructorBody ::= ;
            //
            case 373:
                setResult(null);
                break;

            //
            // Rule 374:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 374: {
               //#line 3133 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3131 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3131 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3133 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 375:  Arguments ::= ( ArgumentListopt )
            //
            case 375: {
               //#line 3150 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3148 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3150 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 377:  InterfaceModifiers ::= InterfaceModifier
            //
            case 377: {
               //#line 3160 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3158 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3160 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 378:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 378: {
               //#line 3167 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3165 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3165 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3167 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 379:  InterfaceModifier ::= Annotation
            //
            case 379: {
               //#line 3173 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3171 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3173 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 380:  InterfaceModifier ::= public
            //
            case 380: {
               //#line 3178 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3178 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 381:  InterfaceModifier ::= protected
            //
            case 381: {
               //#line 3183 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3183 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 382:  InterfaceModifier ::= private
            //
            case 382: {
               //#line 3188 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3188 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 383:  InterfaceModifier ::= abstract
            //
            case 383: {
               //#line 3193 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3193 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 384:  InterfaceModifier ::= static
            //
            case 384: {
               //#line 3198 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3198 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 385:  InterfaceModifier ::= strictfp
            //
            case 385: {
               //#line 3203 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3203 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 386:  ExtendsInterfaces ::= extends Type
            //
            case 386: {
               //#line 3209 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3207 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3209 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 387:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 387: {
               //#line 3216 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3214 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3214 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3216 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 388:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 388: {
               //#line 3225 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3223 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3225 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 390:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 390: {
               //#line 3232 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3230 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3230 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3232 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 391:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 391: {
               //#line 3239 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3237 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3239 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 392:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 392: {
               //#line 3246 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3244 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3246 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 393:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 393: {
               //#line 3253 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3251 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3253 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 394:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 394: {
               //#line 3260 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3258 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3260 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 395:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 395: {
               //#line 3267 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3265 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3267 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 396:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 396: {
               //#line 3274 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3272 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3274 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 397:  InterfaceMemberDeclaration ::= ;
            //
            case 397: {
               //#line 3281 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3281 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 398:  Annotations ::= Annotation
            //
            case 398: {
               //#line 3287 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3285 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3287 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 399:  Annotations ::= Annotations Annotation
            //
            case 399: {
               //#line 3294 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3292 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3292 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3294 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 400:  Annotation ::= @ NamedType
            //
            case 400: {
               //#line 3300 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3298 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3300 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 401:  SimpleName ::= Identifier
            //
            case 401: {
               //#line 3306 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3304 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3306 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 402:  Identifier ::= identifier
            //
            case 402: {
               //#line 3312 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3310 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3312 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 403:  VariableInitializers ::= VariableInitializer
            //
            case 403: {
               //#line 3320 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3318 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3320 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 404:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 404: {
               //#line 3327 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3325 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3325 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3327 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 405:  Block ::= { BlockStatementsopt }
            //
            case 405: {
               //#line 3345 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3343 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3345 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 406:  BlockStatements ::= BlockStatement
            //
            case 406: {
               //#line 3351 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3349 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3351 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 407:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 407: {
               //#line 3358 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3356 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3356 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3358 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 409:  BlockStatement ::= ClassDeclaration
            //
            case 409: {
               //#line 3366 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3364 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3366 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 410:  BlockStatement ::= TypeDefDeclaration
            //
            case 410: {
               //#line 3373 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3371 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3373 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 411:  BlockStatement ::= Statement
            //
            case 411: {
               //#line 3380 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3378 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3380 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 412:  IdentifierList ::= Identifier
            //
            case 412: {
               //#line 3388 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3386 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3388 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 413:  IdentifierList ::= IdentifierList , Identifier
            //
            case 413: {
               //#line 3395 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3393 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3393 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3395 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 414:  FormalDeclarator ::= Identifier : Type
            //
            case 414: {
               //#line 3401 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3399 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3399 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3401 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, Type, null });
                      break;
            }
    
            //
            // Rule 415:  FormalDeclarator ::= ( IdentifierList ) : Type
            //
            case 415: {
               //#line 3406 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3404 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3404 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(5);
                //#line 3406 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, Type, null });
                      break;
            }
    
            //
            // Rule 416:  FormalDeclarator ::= Identifier ( IdentifierList ) : Type
            //
            case 416: {
               //#line 3411 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3409 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3409 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3409 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(6);
                //#line 3411 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, Type, null });
                      break;
            }
    
            //
            // Rule 417:  FieldDeclarator ::= Identifier : Type
            //
            case 417: {
               //#line 3417 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3415 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3415 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3417 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, Type, null });
                      break;
            }
    
            //
            // Rule 418:  FieldDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 418: {
               //#line 3422 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3420 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3420 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3420 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3422 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 419:  VariableDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 419: {
               //#line 3428 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3426 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3426 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3426 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3428 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 420:  VariableDeclarator ::= ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 420: {
               //#line 3433 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3431 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3431 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3431 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3433 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 421:  VariableDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 421: {
               //#line 3438 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3436 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3436 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3436 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3436 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3438 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 422:  VariableDeclaratorWithType ::= Identifier ResultType = VariableInitializer
            //
            case 422: {
               //#line 3444 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3442 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3442 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3442 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3444 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 423:  VariableDeclaratorWithType ::= ( IdentifierList ) ResultType = VariableInitializer
            //
            case 423: {
               //#line 3449 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3447 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3447 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3447 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3449 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 424:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) ResultType = VariableInitializer
            //
            case 424: {
               //#line 3454 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3452 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3452 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3452 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3452 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3454 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 426:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 426: {
               //#line 3462 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3460 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3460 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3460 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3462 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 427:  LocalVariableDeclaration ::= VariableModifiersopt VariableDeclaratorsWithType
            //
            case 427: {
               //#line 3495 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3493 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3493 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3495 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 428:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 428: {
               //#line 3529 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3527 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3527 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3527 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3529 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 430:  Primary ::= TypeName . class
            //
            case 430: {
               //#line 3570 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3568 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3570 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeName instanceof ParsedName)
                {
                    ParsedName a = (ParsedName) TypeName;
                    setResult(nf.ClassLit(pos(), a.toType()));
                }
                else assert(false);
                      break;
            }
    
            //
            // Rule 431:  Primary ::= self
            //
            case 431: {
               //#line 3580 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3580 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 432:  Primary ::= this
            //
            case 432: {
               //#line 3585 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3585 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 433:  Primary ::= ClassName . this
            //
            case 433: {
               //#line 3590 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3588 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3590 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 434:  Primary ::= ( Expression )
            //
            case 434: {
               //#line 3595 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3593 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3595 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 440:  OperatorFunction ::= TypeName . +
            //
            case 440: {
               //#line 3606 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3604 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3606 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.ADD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 441:  OperatorFunction ::= TypeName . -
            //
            case 441: {
               //#line 3617 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3615 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3617 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SUB, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 442:  OperatorFunction ::= TypeName . *
            //
            case 442: {
               //#line 3628 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3626 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3628 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MUL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 443:  OperatorFunction ::= TypeName . /
            //
            case 443: {
               //#line 3639 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3637 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3639 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.DIV, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 444:  OperatorFunction ::= TypeName . %
            //
            case 444: {
               //#line 3650 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3648 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3650 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MOD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 445:  OperatorFunction ::= TypeName . &
            //
            case 445: {
               //#line 3661 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3659 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3661 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_AND, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 446:  OperatorFunction ::= TypeName . |
            //
            case 446: {
               //#line 3672 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3670 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3672 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_OR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 447:  OperatorFunction ::= TypeName . ^
            //
            case 447: {
               //#line 3683 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3681 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3683 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_XOR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 448:  OperatorFunction ::= TypeName . <<
            //
            case 448: {
               //#line 3694 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3692 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3694 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 449:  OperatorFunction ::= TypeName . >>
            //
            case 449: {
               //#line 3705 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3703 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3705 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 450:  OperatorFunction ::= TypeName . >>>
            //
            case 450: {
               //#line 3716 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3714 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3716 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.USHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 451:  OperatorFunction ::= TypeName . <
            //
            case 451: {
               //#line 3727 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3725 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3727 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 452:  OperatorFunction ::= TypeName . <=
            //
            case 452: {
               //#line 3738 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3736 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3738 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 453:  OperatorFunction ::= TypeName . >=
            //
            case 453: {
               //#line 3749 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3747 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3749 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 454:  OperatorFunction ::= TypeName . >
            //
            case 454: {
               //#line 3760 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3758 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3760 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 455:  OperatorFunction ::= TypeName . ==
            //
            case 455: {
               //#line 3771 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3769 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3771 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.EQ, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 456:  OperatorFunction ::= TypeName . !=
            //
            case 456: {
               //#line 3782 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3780 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3782 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.NE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 457:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 457: {
               //#line 3795 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3793 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                //#line 3795 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 458:  Literal ::= LongLiteral$LongLiteral
            //
            case 458: {
               //#line 3801 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3799 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken LongLiteral = (IToken) getRhsIToken(1);
                //#line 3801 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 459:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 459: {
               //#line 3807 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3805 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                //#line 3807 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 460:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 460: {
               //#line 3813 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3811 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                //#line 3813 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 461:  Literal ::= BooleanLiteral
            //
            case 461: {
               //#line 3819 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3817 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3819 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 462:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 462: {
               //#line 3824 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3822 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                //#line 3824 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 463:  Literal ::= StringLiteral$str
            //
            case 463: {
               //#line 3830 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3828 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3830 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 464:  Literal ::= null
            //
            case 464: {
               //#line 3836 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3836 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 465:  BooleanLiteral ::= true$trueLiteral
            //
            case 465: {
               //#line 3842 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3840 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3842 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 466:  BooleanLiteral ::= false$falseLiteral
            //
            case 466: {
               //#line 3847 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3845 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3847 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 467:  ArgumentList ::= Expression
            //
            case 467: {
               //#line 3856 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3854 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3856 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 468:  ArgumentList ::= ArgumentList , Expression
            //
            case 468: {
               //#line 3863 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3861 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3861 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3863 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 469:  FieldAccess ::= Primary . Identifier
            //
            case 469: {
               //#line 3869 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3867 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3867 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3869 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 470:  FieldAccess ::= super . Identifier
            //
            case 470: {
               //#line 3874 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3872 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3874 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 471:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 471: {
               //#line 3879 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3877 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3879 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 472:  FieldAccess ::= Primary . class$c
            //
            case 472: {
               //#line 3884 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3882 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3882 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3884 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 473:  FieldAccess ::= super . class$c
            //
            case 473: {
               //#line 3889 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3887 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3889 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 474:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 474: {
               //#line 3894 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3892 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3892 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3892 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 3894 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 475:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 475: {
               //#line 3900 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3898 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3898 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3898 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3900 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 476:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 476: {
               //#line 3907 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3905 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3905 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3905 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3905 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3907 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 477:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 477: {
               //#line 3912 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3910 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3910 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3910 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3912 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 478:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 478: {
               //#line 3917 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3915 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3915 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3915 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3915 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3915 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3917 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 479:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 479: {
               //#line 3922 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3920 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3920 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3920 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3922 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
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
                      break;
            }
    
            //
            // Rule 480:  MethodSelection ::= MethodName . TypeParametersopt ( FormalParameterListopt )
            //
            case 480: {
               //#line 3942 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3940 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3940 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 3940 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(5);
                //#line 3942 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, typeArgs, actuals), true))));
                      break;
            }
    
            //
            // Rule 481:  MethodSelection ::= Primary . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 481: {
               //#line 3955 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3953 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3953 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3953 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3953 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3955 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), Primary, Identifier, typeArgs, actuals), true))));
                      break;
            }
    
            //
            // Rule 482:  MethodSelection ::= super . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 482: {
               //#line 3967 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3965 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3965 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3965 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3967 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, typeArgs, actuals), true))));
                      break;
            }
    
            //
            // Rule 483:  MethodSelection ::= ClassName . super$sup . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 483: {
               //#line 3979 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(7);
                //#line 3977 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(9);
                //#line 3979 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, typeArgs, actuals), true))));
                      break;
            }
    
            //
            // Rule 485:  PostfixExpression ::= ExpressionName
            //
            case 485: {
               //#line 3993 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3991 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 3993 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 488:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 488: {
               //#line 4001 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3999 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4001 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 489:  PostDecrementExpression ::= PostfixExpression --
            //
            case 489: {
               //#line 4007 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4005 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4007 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 492:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 492: {
               //#line 4015 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4013 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4015 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 493:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 493: {
               //#line 4020 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4018 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4020 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 495:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 495: {
               //#line 4027 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4025 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4027 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 496:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 496: {
               //#line 4033 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4031 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4033 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 498:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 498: {
               //#line 4040 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4038 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4040 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 499:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 499: {
               //#line 4045 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4043 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4043 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4045 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e);
                      break;
            }
    
            //
            // Rule 500:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 500: {
               //#line 4052 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4050 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4052 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 502:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 502: {
               //#line 4059 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4057 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4057 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4059 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 503:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 503: {
               //#line 4064 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4062 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4062 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4064 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 504:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 504: {
               //#line 4069 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4067 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4067 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4069 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 506:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 506: {
               //#line 4076 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4074 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4074 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4076 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 507:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 507: {
               //#line 4081 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4079 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4079 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4081 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 509:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 509: {
               //#line 4088 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4086 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4086 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4088 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 510:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 510: {
               //#line 4093 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4091 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4091 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4093 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 511:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 511: {
               //#line 4098 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4096 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4096 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4098 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 513:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 513: {
               //#line 4105 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4103 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4103 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4105 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 516:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 516: {
               //#line 4114 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4112 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4112 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4114 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 517:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 517: {
               //#line 4119 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4117 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4117 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4119 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 518:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 518: {
               //#line 4124 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4122 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4122 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4124 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 519:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 519: {
               //#line 4129 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4127 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4127 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4129 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 520:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 520: {
               //#line 4134 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4132 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4132 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4134 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 521:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 521: {
               //#line 4139 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4137 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4137 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4139 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 523:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 523: {
               //#line 4146 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4144 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4144 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4146 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 524:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 524: {
               //#line 4151 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4149 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4149 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4151 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 525:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 525: {
               //#line 4156 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4154 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4154 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4156 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 527:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 527: {
               //#line 4163 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4161 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4161 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4163 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 529:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 529: {
               //#line 4170 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4168 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4168 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4170 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 531:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 531: {
               //#line 4177 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4175 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4175 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4177 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 533:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 533: {
               //#line 4184 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4182 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4182 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4184 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 535:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 535: {
               //#line 4191 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4189 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4189 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4191 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 541:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 541: {
               //#line 4203 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4201 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4201 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4201 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4203 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 544:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 544: {
               //#line 4212 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4210 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4210 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4210 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4212 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 545:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 545: {
               //#line 4217 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4215 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4215 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4215 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4215 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4217 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 546:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 546: {
               //#line 4222 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4220 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4220 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4220 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4220 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4222 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 547:  LeftHandSide ::= ExpressionName
            //
            case 547: {
               //#line 4228 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4226 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4228 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 549:  AssignmentOperator ::= =
            //
            case 549: {
               //#line 4235 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4235 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 550:  AssignmentOperator ::= *=
            //
            case 550: {
               //#line 4240 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4240 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 551:  AssignmentOperator ::= /=
            //
            case 551: {
               //#line 4245 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4245 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 552:  AssignmentOperator ::= %=
            //
            case 552: {
               //#line 4250 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4250 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 553:  AssignmentOperator ::= +=
            //
            case 553: {
               //#line 4255 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4255 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 554:  AssignmentOperator ::= -=
            //
            case 554: {
               //#line 4260 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4260 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 555:  AssignmentOperator ::= <<=
            //
            case 555: {
               //#line 4265 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4265 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 556:  AssignmentOperator ::= >>=
            //
            case 556: {
               //#line 4270 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4270 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 557:  AssignmentOperator ::= >>>=
            //
            case 557: {
               //#line 4275 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4275 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 558:  AssignmentOperator ::= &=
            //
            case 558: {
               //#line 4280 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4280 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= ^=
            //
            case 559: {
               //#line 4285 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4285 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 560:  AssignmentOperator ::= |=
            //
            case 560: {
               //#line 4290 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4290 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 563:  PrefixOp ::= +
            //
            case 563: {
               //#line 4301 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4301 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 564:  PrefixOp ::= -
            //
            case 564: {
               //#line 4306 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4306 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 565:  PrefixOp ::= !
            //
            case 565: {
               //#line 4311 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4311 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 566:  PrefixOp ::= ~
            //
            case 566: {
               //#line 4316 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4316 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 567:  BinOp ::= +
            //
            case 567: {
               //#line 4322 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4322 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 568:  BinOp ::= -
            //
            case 568: {
               //#line 4327 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4327 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 569:  BinOp ::= *
            //
            case 569: {
               //#line 4332 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4332 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 570:  BinOp ::= /
            //
            case 570: {
               //#line 4337 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4337 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 571:  BinOp ::= %
            //
            case 571: {
               //#line 4342 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4342 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 572:  BinOp ::= &
            //
            case 572: {
               //#line 4347 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4347 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 573:  BinOp ::= |
            //
            case 573: {
               //#line 4352 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4352 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 574:  BinOp ::= ^
            //
            case 574: {
               //#line 4357 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4357 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 575:  BinOp ::= &&
            //
            case 575: {
               //#line 4362 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4362 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 576:  BinOp ::= ||
            //
            case 576: {
               //#line 4367 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4367 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 577:  BinOp ::= <<
            //
            case 577: {
               //#line 4372 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4372 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 578:  BinOp ::= >>
            //
            case 578: {
               //#line 4377 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4377 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 579:  BinOp ::= >>>
            //
            case 579: {
               //#line 4382 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4382 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 580:  BinOp ::= >=
            //
            case 580: {
               //#line 4387 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4387 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 581:  BinOp ::= <=
            //
            case 581: {
               //#line 4392 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4392 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 582:  BinOp ::= >
            //
            case 582: {
               //#line 4397 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4397 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 583:  BinOp ::= <
            //
            case 583: {
               //#line 4402 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4402 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 584:  BinOp ::= ==
            //
            case 584: {
               //#line 4410 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4410 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 585:  BinOp ::= !=
            //
            case 585: {
               //#line 4415 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4415 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 586:  Catchesopt ::= $Empty
            //
            case 586: {
               //#line 4424 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4424 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 588:  Identifieropt ::= $Empty
            //
            case 588:
                setResult(null);
                break;

            //
            // Rule 589:  Identifieropt ::= Identifier
            //
            case 589: {
               //#line 4433 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4431 "/Users/nystrom/work/x10/1.7/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4433 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 590:  ForUpdateopt ::= $Empty
            //
            case 590: {
               //#line 4439 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4439 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 592:  Expressionopt ::= $Empty
            //
            case 592:
                setResult(null);
                break;

            //
            // Rule 594:  ForInitopt ::= $Empty
            //
            case 594: {
               //#line 4450 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4450 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 596:  SwitchLabelsopt ::= $Empty
            //
            case 596: {
               //#line 4457 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4457 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 598:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 598: {
               //#line 4464 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4464 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 600:  VariableModifiersopt ::= $Empty
            //
            case 600: {
               //#line 4471 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4471 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 602:  VariableInitializersopt ::= $Empty
            //
            case 602:
                setResult(null);
                break;

            //
            // Rule 604:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 604: {
               //#line 4482 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4482 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 606:  ExtendsInterfacesopt ::= $Empty
            //
            case 606: {
               //#line 4489 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4489 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 608:  InterfaceModifiersopt ::= $Empty
            //
            case 608: {
               //#line 4496 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4496 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 610:  ClassBodyopt ::= $Empty
            //
            case 610:
                setResult(null);
                break;

            //
            // Rule 612:  Argumentsopt ::= $Empty
            //
            case 612: {
               //#line 4507 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4507 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 614:  ArgumentListopt ::= $Empty
            //
            case 614: {
               //#line 4514 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4514 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 616:  BlockStatementsopt ::= $Empty
            //
            case 616: {
               //#line 4521 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4521 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 618:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 618:
                setResult(null);
                break;

            //
            // Rule 620:  ConstructorModifiersopt ::= $Empty
            //
            case 620: {
               //#line 4532 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4532 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 622:  FormalParameterListopt ::= $Empty
            //
            case 622: {
               //#line 4539 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4539 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 624:  Throwsopt ::= $Empty
            //
            case 624: {
               //#line 4546 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4546 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 626:  MethodModifiersopt ::= $Empty
            //
            case 626: {
               //#line 4553 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4553 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 628:  FieldModifiersopt ::= $Empty
            //
            case 628: {
               //#line 4560 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4560 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 630:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 630: {
               //#line 4567 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4567 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 632:  Interfacesopt ::= $Empty
            //
            case 632: {
               //#line 4574 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4574 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 634:  Superopt ::= $Empty
            //
            case 634:
                setResult(null);
                break;

            //
            // Rule 636:  TypeParametersopt ::= $Empty
            //
            case 636: {
               //#line 4585 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4585 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 638:  FormalParametersopt ::= $Empty
            //
            case 638: {
               //#line 4592 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4592 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 640:  Annotationsopt ::= $Empty
            //
            case 640: {
               //#line 4599 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4599 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 642:  TypeDeclarationsopt ::= $Empty
            //
            case 642: {
               //#line 4606 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4606 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 644:  ImportDeclarationsopt ::= $Empty
            //
            case 644: {
               //#line 4613 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4613 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 646:  PackageDeclarationopt ::= $Empty
            //
            case 646:
                setResult(null);
                break;

            //
            // Rule 648:  ResultTypeopt ::= $Empty
            //
            case 648:
                setResult(null);
                break;

            //
            // Rule 650:  TypeArgumentsopt ::= $Empty
            //
            case 650: {
               //#line 4628 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4628 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 652:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 652: {
               //#line 4635 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4635 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 654:  Propertiesopt ::= $Empty
            //
            case 654: {
               //#line 4642 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4642 "/Users/nystrom/work/x10/1.7/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 656:  ,opt ::= $Empty
            //
            case 656:
                setResult(null);
                break;

    
            default:
                break;
        }
        return;
    }
}

