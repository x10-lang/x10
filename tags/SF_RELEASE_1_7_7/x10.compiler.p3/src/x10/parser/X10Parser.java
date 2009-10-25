
//#line 18 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
//
// Licensed Material
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

import lpg.runtime.*;

//#line 28 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
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
    

    //#line 316 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
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
               //#line 8 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 6 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 18 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 16 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 28 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 26 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 38 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 36 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 48 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 46 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 58 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 56 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 68 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 66 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 74 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 78 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 85 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 92 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 98 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 109 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 107 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 117 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 115 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 120 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 879 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 877 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 877 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 877 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 877 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 877 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 877 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 879 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 891 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 889 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 889 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 889 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 889 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 889 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 891 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 904 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 902 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 904 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 19:  PropertyList ::= Property
            //
            case 19: {
               //#line 909 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 907 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 909 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 20:  PropertyList ::= PropertyList , Property
            //
            case 20: {
               //#line 916 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 914 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 914 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 916 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 21:  Property ::= Annotationsopt Identifier : Type
            //
            case 21: {
               //#line 923 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 921 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 921 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 921 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(4);
                //#line 923 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 932 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 930 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 930 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 930 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 930 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 930 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 930 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 930 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 930 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 932 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 962 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 960 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 960 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 960 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 960 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 960 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 960 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 960 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 960 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 960 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 962 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 979 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 977 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 977 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 977 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 977 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 977 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 977 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 977 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 977 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 979 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 996 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 996 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1014 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1012 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1012 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1012 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1012 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1012 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1012 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1012 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1012 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1014 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1033 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1031 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1031 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1031 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1031 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1031 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1031 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1031 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1033 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1050 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1048 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1048 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1048 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1048 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1048 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1048 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1048 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1050 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1067 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1067 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1084 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1082 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1082 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1082 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1082 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1082 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1082 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1082 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1084 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1101 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1099 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1099 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1099 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1099 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1099 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1099 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1099 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1101 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(12)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(), Name.make("\044convert")),
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
               //#line 1120 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1120 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1135 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1133 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1133 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1133 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(4);
                //#line 1133 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 1133 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 1135 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1151 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1149 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1149 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1151 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 35:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 35: {
               //#line 1156 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1154 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1154 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1156 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 36:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 36: {
               //#line 1161 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1159 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1159 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1159 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1161 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 37:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 37: {
               //#line 1166 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1164 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1164 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1164 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1166 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 38:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 38: {
               //#line 1172 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1170 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 1170 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1170 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1170 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1170 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1170 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1170 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1172 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1193 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1191 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1191 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1191 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1191 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1193 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 40:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 40: {
               //#line 1200 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1198 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1198 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1198 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1198 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1198 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1200 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1208 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1206 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1206 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1206 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1206 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1206 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1208 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1217 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1215 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1215 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1217 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 45:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt => Type
            //
            case 45: {
               //#line 1229 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1227 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1227 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1227 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1227 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1227 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1229 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt));
                      break;
            }
    
            //
            // Rule 50:  AnnotatedType ::= Type Annotations
            //
            case 50: {
               //#line 1238 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1236 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1236 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1238 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn);
                      break;
            }
    
            //
            // Rule 53:  ConstrainedType ::= ( Type )
            //
            case 53: {
               //#line 1248 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1246 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1248 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 54:  PlaceType ::= PlaceExpression
            //
            case 54: {
               //#line 1254 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1252 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(1);
                //#line 1254 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(),
                                    nf.Field(pos(), nf.This(pos()), nf.Id(pos(), "loc")), Binary.EQ,
                                    PlaceExpression));
                      break;
            }
    
            //
            // Rule 55:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 55: {
               //#line 1262 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1260 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1260 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1260 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1260 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1260 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1262 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1273 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1271 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1271 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1271 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1271 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1273 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1299 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1297 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1297 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(3);
                //#line 1299 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
                      break;
            }
    
            //
            // Rule 58:  DepParameters ::= { ExistentialListopt Conjunction } !
            //
            case 58: {
               //#line 1304 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1302 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1302 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(3);
                //#line 1304 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
                      break;
            }
    
            //
            // Rule 59:  DepParameters ::= { ExistentialListopt Conjunction } ! PlaceType
            //
            case 59: {
               //#line 1309 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1307 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1307 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(3);
                //#line 1307 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(6);
                //#line 1309 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1318 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1316 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1318 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 61:  TypeParameters ::= [ TypeParameterList ]
            //
            case 61: {
               //#line 1324 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1322 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1324 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 62:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 62: {
               //#line 1329 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1327 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1329 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 63:  Conjunction ::= Expression
            //
            case 63: {
               //#line 1335 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1333 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1335 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 64:  Conjunction ::= Conjunction , Expression
            //
            case 64: {
               //#line 1342 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1340 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1340 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1342 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 65:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 65: {
               //#line 1348 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1346 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1346 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1348 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 66:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 66: {
               //#line 1353 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1351 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1351 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1353 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 67:  WhereClause ::= DepParameters
            //
            case 67: {
               //#line 1359 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1357 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1359 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 68:  ExistentialListopt ::= $Empty
            //
            case 68: {
               //#line 1365 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1365 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 69:  ExistentialListopt ::= ExistentialList ;
            //
            case 69: {
               //#line 1370 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1368 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1370 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 70:  ExistentialList ::= FormalParameter
            //
            case 70: {
               //#line 1376 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1374 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1376 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 71:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 71: {
               //#line 1383 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1381 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1381 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1383 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 74:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 74: {
               //#line 1394 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1392 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1392 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1392 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1392 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1392 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1392 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1392 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1392 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1394 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1409 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1409 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1423 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1421 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1421 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1421 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1421 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1421 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1421 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1421 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(9);
                //#line 1423 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1439 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1437 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1439 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 78:  FieldKeyword ::= val
            //
            case 78: {
               //#line 1445 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1445 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 79:  FieldKeyword ::= var
            //
            case 79: {
               //#line 1450 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1450 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 80:  FieldKeyword ::= const
            //
            case 80: {
               //#line 1455 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1455 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 81:  VarKeyword ::= val
            //
            case 81: {
               //#line 1463 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1463 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 82:  VarKeyword ::= var
            //
            case 82: {
               //#line 1468 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1468 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 83:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 83: {
               //#line 1475 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1473 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1473 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1473 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1475 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1499 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1497 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1497 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1499 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1558 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1556 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1556 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1558 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 115:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 115: {
               //#line 1564 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1562 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1562 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1562 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1564 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 116:  EmptyStatement ::= ;
            //
            case 116: {
               //#line 1570 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1570 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 117:  LabeledStatement ::= Identifier : LabelableStatement
            //
            case 117: {
               //#line 1576 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1574 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1574 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt LabelableStatement = (Stmt) getRhsSym(3);
                //#line 1576 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LabelableStatement));
                      break;
            }
    
            //
            // Rule 124:  ExpressionStatement ::= StatementExpression ;
            //
            case 124: {
               //#line 1589 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1587 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1589 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 132:  AssertStatement ::= assert Expression ;
            //
            case 132: {
               //#line 1620 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1618 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1620 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 133:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 133: {
               //#line 1625 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1623 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1623 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1625 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 134:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 134: {
               //#line 1631 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1629 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1629 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1631 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 135:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 135: {
               //#line 1637 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1635 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1635 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1637 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 137:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 137: {
               //#line 1645 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1643 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1643 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1645 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 138:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 138: {
               //#line 1652 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1650 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1650 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1652 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 139:  SwitchLabels ::= SwitchLabel
            //
            case 139: {
               //#line 1661 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1659 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1661 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 140:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 140: {
               //#line 1668 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1666 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1666 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1668 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 141:  SwitchLabel ::= case ConstantExpression :
            //
            case 141: {
               //#line 1675 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1673 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1675 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 142:  SwitchLabel ::= default :
            //
            case 142: {
               //#line 1680 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1680 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 143:  WhileStatement ::= while ( Expression ) Statement
            //
            case 143: {
               //#line 1686 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1684 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1684 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1686 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 144:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 144: {
               //#line 1692 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1690 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1690 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1692 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 147:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 147: {
               //#line 1701 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1699 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1699 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1699 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1699 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1701 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 149:  ForInit ::= LocalVariableDeclaration
            //
            case 149: {
               //#line 1708 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1706 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1708 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 151:  StatementExpressionList ::= StatementExpression
            //
            case 151: {
               //#line 1718 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1716 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1718 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 152:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 152: {
               //#line 1725 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1723 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1723 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1725 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 153:  BreakStatement ::= break Identifieropt ;
            //
            case 153: {
               //#line 1731 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1729 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1731 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 154:  ContinueStatement ::= continue Identifieropt ;
            //
            case 154: {
               //#line 1737 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1735 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1737 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 155:  ReturnStatement ::= return Expressionopt ;
            //
            case 155: {
               //#line 1743 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1741 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1743 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 156:  ThrowStatement ::= throw Expression ;
            //
            case 156: {
               //#line 1749 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1747 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1749 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 157:  TryStatement ::= try Block Catches
            //
            case 157: {
               //#line 1755 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1753 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1753 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1755 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 158:  TryStatement ::= try Block Catchesopt Finally
            //
            case 158: {
               //#line 1760 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1758 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1758 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1758 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1760 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 159:  Catches ::= CatchClause
            //
            case 159: {
               //#line 1766 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1764 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1766 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 160:  Catches ::= Catches CatchClause
            //
            case 160: {
               //#line 1773 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1771 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1771 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1773 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 161:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 161: {
               //#line 1780 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1778 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1778 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1780 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 162:  Finally ::= finally Block
            //
            case 162: {
               //#line 1786 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1784 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1786 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 163:  NowStatement ::= now ( Clock ) Statement
            //
            case 163: {
               //#line 1792 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1790 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1790 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1792 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Now(pos(), Clock, Statement));
                      break;
            }
    
            //
            // Rule 164:  ClockedClause ::= clocked ( ClockList )
            //
            case 164: {
               //#line 1798 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1796 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1798 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 165:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 165: {
               //#line 1804 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1802 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1802 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1802 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1804 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 166:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 166: {
               //#line 1813 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1811 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1811 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1813 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 167:  AtomicStatement ::= atomic Statement
            //
            case 167: {
               //#line 1819 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1817 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1819 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 168:  WhenStatement ::= when ( Expression ) Statement
            //
            case 168: {
               //#line 1826 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1824 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1824 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1826 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 169:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 169: {
               //#line 1831 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1829 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1829 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1829 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1829 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1831 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 170:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 170: {
               //#line 1838 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1836 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1836 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1836 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1836 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1838 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 171:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 171: {
               //#line 1852 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1850 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1850 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1850 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1850 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1852 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 172:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 172: {
               //#line 1866 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1864 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1864 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1864 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1866 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 173:  FinishStatement ::= finish Statement
            //
            case 173: {
               //#line 1879 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1877 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1879 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 174:  AnnotationStatement ::= Annotations Statement
            //
            case 174: {
               //#line 1886 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1884 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 1884 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1886 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                if (Statement.ext() instanceof X10Ext && Annotations instanceof List) {
                    Statement = (Stmt) ((X10Ext) Statement.ext()).annotations((List) Annotations);
                }
                setResult(Statement);
                      break;
            }
    
            //
            // Rule 175:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 175: {
               //#line 1895 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1893 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1895 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 177:  NextStatement ::= next ;
            //
            case 177: {
               //#line 1903 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1903 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 178:  AwaitStatement ::= await Expression ;
            //
            case 178: {
               //#line 1909 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1907 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1909 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 179:  ClockList ::= Clock
            //
            case 179: {
               //#line 1915 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1913 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1915 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 180:  ClockList ::= ClockList , Clock
            //
            case 180: {
               //#line 1922 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1920 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1920 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1922 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 181:  Clock ::= Expression
            //
            case 181: {
               //#line 1930 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1928 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1930 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 182:  CastExpression ::= CastExpression as Type
            //
            case 182: {
               //#line 1944 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1942 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 1942 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1944 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 183:  CastExpression ::= ConditionalExpression ! Expression
            //
            case 183: {
               //#line 1949 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1947 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1947 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1949 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.PlaceCast(pos(), Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 185:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 185: {
               //#line 1958 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1956 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 1958 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 186:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 186: {
               //#line 1965 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1963 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 1963 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 1965 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 187:  TypeParameterList ::= TypeParameter
            //
            case 187: {
               //#line 1972 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1970 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 1972 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 188:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 188: {
               //#line 1979 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1977 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 1977 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 1979 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 189:  TypeParamWithVariance ::= Identifier
            //
            case 189: {
               //#line 1986 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1984 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1986 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 190:  TypeParamWithVariance ::= + Identifier
            //
            case 190: {
               //#line 1991 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1989 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1991 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 191:  TypeParamWithVariance ::= - Identifier
            //
            case 191: {
               //#line 1996 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1996 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 192:  TypeParameter ::= Identifier
            //
            case 192: {
               //#line 2002 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2000 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2002 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 193:  Primary ::= here
            //
            case 193: {
               //#line 2008 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2008 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 195:  RegionExpressionList ::= RegionExpression
            //
            case 195: {
               //#line 2016 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2014 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2016 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 196:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 196: {
               //#line 2023 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2021 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2021 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2023 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 197:  Primary ::= [ ArgumentListopt ]
            //
            case 197: {
               //#line 2030 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2028 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 2030 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 198:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 198: {
               //#line 2037 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2035 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2035 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2037 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 199:  ClosureExpression ::= TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt => ClosureBody
            //
            case 199: {
               //#line 2044 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2042 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(1);
                //#line 2042 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(2);
                //#line 2042 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 2042 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2042 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 2042 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2044 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), TypeParametersopt, FormalParameters, WhereClauseopt, 
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 200:  LastExpression ::= Expression
            //
            case 200: {
               //#line 2051 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2049 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2051 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 201:  ClosureBody ::= CastExpression
            //
            case 201: {
               //#line 2057 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2055 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2057 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), CastExpression, true)));
                      break;
            }
    
            //
            // Rule 202:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 202: {
               //#line 2062 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2060 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2060 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2060 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2062 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 203:  ClosureBody ::= Annotationsopt Block
            //
            case 203: {
               //#line 2072 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2070 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2070 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2072 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 204:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 204: {
               //#line 2081 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2079 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2079 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2081 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 205:  AsyncExpression ::= async ClosureBody
            //
            case 205: {
               //#line 2087 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2085 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2087 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 206:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 206: {
               //#line 2092 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2090 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2090 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2092 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 207:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 207: {
               //#line 2097 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2095 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2095 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2097 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 208:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 208: {
               //#line 2102 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2100 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2100 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2100 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2102 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 209:  FutureExpression ::= future ClosureBody
            //
            case 209: {
               //#line 2108 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2106 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2108 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 210:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 210: {
               //#line 2113 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2111 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2111 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2113 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 211:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 211: {
               //#line 2118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2116 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2116 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 212:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 212: {
               //#line 2123 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2121 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2121 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2121 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2123 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 213:  DepParametersopt ::= $Empty
            //
            case 213:
                setResult(null);
                break;

            //
            // Rule 215:  PropertyListopt ::= $Empty
            //
            case 215: {
               //#line 2134 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2134 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 217:  WhereClauseopt ::= $Empty
            //
            case 217:
                setResult(null);
                break;

            //
            // Rule 219:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 219:
                setResult(null);
                break;

            //
            // Rule 221:  ClassModifiersopt ::= $Empty
            //
            case 221: {
               //#line 2149 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2149 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 223:  TypeDefModifiersopt ::= $Empty
            //
            case 223: {
               //#line 2155 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2155 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 225:  Unsafeopt ::= $Empty
            //
            case 225:
                setResult(null);
                break;

            //
            // Rule 226:  Unsafeopt ::= unsafe
            //
            case 226: {
               //#line 2163 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2163 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                // any value distinct from null
                setResult(this);
                      break;
            }
    
            //
            // Rule 227:  ClockedClauseopt ::= $Empty
            //
            case 227: {
               //#line 2170 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2170 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 229:  identifier ::= IDENTIFIER$ident
            //
            case 229: {
               //#line 2181 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2179 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2181 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 230:  TypeName ::= Identifier
            //
            case 230: {
               //#line 2188 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2186 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2188 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 231:  TypeName ::= TypeName . Identifier
            //
            case 231: {
               //#line 2193 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2191 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2191 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2193 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 233:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 233: {
               //#line 2205 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2203 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2205 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 234:  TypeArgumentList ::= Type
            //
            case 234: {
               //#line 2212 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2210 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2212 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 235:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 235: {
               //#line 2219 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2217 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2217 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2219 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 236:  PackageName ::= Identifier
            //
            case 236: {
               //#line 2229 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2227 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2229 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 237:  PackageName ::= PackageName . Identifier
            //
            case 237: {
               //#line 2234 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2232 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2232 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2234 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 238:  ExpressionName ::= Identifier
            //
            case 238: {
               //#line 2250 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2248 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2250 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 239:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 239: {
               //#line 2255 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2253 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2253 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2255 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 240:  MethodName ::= Identifier
            //
            case 240: {
               //#line 2265 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2263 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2265 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 241:  MethodName ::= AmbiguousName . Identifier
            //
            case 241: {
               //#line 2270 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2268 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2268 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2270 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 242:  PackageOrTypeName ::= Identifier
            //
            case 242: {
               //#line 2280 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2278 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2280 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 243:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 243: {
               //#line 2285 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2283 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2283 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2285 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 244:  AmbiguousName ::= Identifier
            //
            case 244: {
               //#line 2295 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2293 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2295 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 245:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 245: {
               //#line 2300 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2298 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2298 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2300 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 246:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 246: {
               //#line 2312 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2310 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2310 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2310 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2312 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 247:  ImportDeclarations ::= ImportDeclaration
            //
            case 247: {
               //#line 2328 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2326 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2328 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 248:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 248: {
               //#line 2335 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2333 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2333 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2335 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 249:  TypeDeclarations ::= TypeDeclaration
            //
            case 249: {
               //#line 2343 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2341 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2343 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 250:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 250: {
               //#line 2351 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2349 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2349 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2351 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 251:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 251: {
               //#line 2359 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2357 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2357 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2359 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn);
                      break;
            }
    
            //
            // Rule 254:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 254: {
               //#line 2373 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2371 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2373 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 255:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 255: {
               //#line 2379 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2377 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2379 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 259:  TypeDeclaration ::= ;
            //
            case 259: {
               //#line 2394 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2394 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 260:  ClassModifiers ::= ClassModifier
            //
            case 260: {
               //#line 2402 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2400 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2402 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 261:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 261: {
               //#line 2409 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2409 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 262:  ClassModifier ::= Annotation
            //
            case 262: {
               //#line 2415 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2413 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2415 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 263:  ClassModifier ::= public
            //
            case 263: {
               //#line 2420 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2420 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 264:  ClassModifier ::= protected
            //
            case 264: {
               //#line 2425 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2425 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 265:  ClassModifier ::= private
            //
            case 265: {
               //#line 2430 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2430 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 266:  ClassModifier ::= abstract
            //
            case 266: {
               //#line 2435 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2435 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 267:  ClassModifier ::= static
            //
            case 267: {
               //#line 2440 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2440 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 268:  ClassModifier ::= final
            //
            case 268: {
               //#line 2445 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2445 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 269:  ClassModifier ::= strictfp
            //
            case 269: {
               //#line 2450 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2450 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 270:  ClassModifier ::= safe
            //
            case 270: {
               //#line 2455 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2455 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 271:  ClassModifier ::= value
            //
            case 271: {
               //#line 2460 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2460 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.VALUE)));
                      break;
            }
    
            //
            // Rule 272:  TypeDefModifiers ::= TypeDefModifier
            //
            case 272: {
               //#line 2466 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2464 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2466 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 273:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 273: {
               //#line 2473 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2471 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2471 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2473 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 274:  TypeDefModifier ::= Annotation
            //
            case 274: {
               //#line 2479 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2477 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2479 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 275:  TypeDefModifier ::= public
            //
            case 275: {
               //#line 2484 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2484 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 276:  TypeDefModifier ::= protected
            //
            case 276: {
               //#line 2489 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2489 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 277:  TypeDefModifier ::= private
            //
            case 277: {
               //#line 2494 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2494 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 278:  TypeDefModifier ::= abstract
            //
            case 278: {
               //#line 2499 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2499 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 279:  TypeDefModifier ::= static
            //
            case 279: {
               //#line 2504 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2504 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 280:  TypeDefModifier ::= final
            //
            case 280: {
               //#line 2509 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2509 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 281:  Interfaces ::= implements InterfaceTypeList
            //
            case 281: {
               //#line 2518 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2516 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2518 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 282:  InterfaceTypeList ::= Type
            //
            case 282: {
               //#line 2524 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2522 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2524 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 283:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 283: {
               //#line 2531 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2529 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2529 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2531 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 284:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 284: {
               //#line 2541 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2539 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2541 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 286:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 286: {
               //#line 2548 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2546 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2546 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2548 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 288:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 288: {
               //#line 2556 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2554 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Initializer InstanceInitializer = (Initializer) getRhsSym(1);
                //#line 2556 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InstanceInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 289:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 289: {
               //#line 2563 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2561 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Initializer StaticInitializer = (Initializer) getRhsSym(1);
                //#line 2563 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(StaticInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 290: {
               //#line 2570 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2568 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2570 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 292:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 292: {
               //#line 2579 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2577 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2579 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 293:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 293: {
               //#line 2586 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2584 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2586 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 294:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 294: {
               //#line 2593 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2591 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2593 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 295: {
               //#line 2600 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2598 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2600 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 296:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 296: {
               //#line 2607 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2605 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2607 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  ClassMemberDeclaration ::= ;
            //
            case 297: {
               //#line 2614 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2614 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 298:  FormalDeclarators ::= FormalDeclarator
            //
            case 298: {
               //#line 2621 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2619 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2621 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 299: {
               //#line 2628 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2626 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2626 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2628 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 300:  FieldDeclarators ::= FieldDeclarator
            //
            case 300: {
               //#line 2635 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2633 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2635 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 301: {
               //#line 2642 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2640 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2640 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2642 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 302:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 302: {
               //#line 2650 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2648 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2650 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 303:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 303: {
               //#line 2657 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2655 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2655 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2657 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 304:  VariableDeclarators ::= VariableDeclarator
            //
            case 304: {
               //#line 2664 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2662 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2664 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 305:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 305: {
               //#line 2671 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2669 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2669 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2671 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 307:  FieldModifiers ::= FieldModifier
            //
            case 307: {
               //#line 2680 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2678 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2680 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 308:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 308: {
               //#line 2687 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2685 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2685 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2687 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 309:  FieldModifier ::= Annotation
            //
            case 309: {
               //#line 2693 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2691 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2693 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 310:  FieldModifier ::= public
            //
            case 310: {
               //#line 2698 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2698 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 311:  FieldModifier ::= protected
            //
            case 311: {
               //#line 2703 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2703 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 312:  FieldModifier ::= private
            //
            case 312: {
               //#line 2708 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2708 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 313:  FieldModifier ::= static
            //
            case 313: {
               //#line 2713 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2713 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 314:  FieldModifier ::= transient
            //
            case 314: {
               //#line 2718 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2718 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.TRANSIENT)));
                      break;
            }
    
            //
            // Rule 315:  FieldModifier ::= volatile
            //
            case 315: {
               //#line 2723 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2723 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.VOLATILE)));
                      break;
            }
    
            //
            // Rule 316:  ResultType ::= : Type
            //
            case 316: {
               //#line 2729 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2727 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2729 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 317:  FormalParameters ::= ( FormalParameterList )
            //
            case 317: {
               //#line 2735 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2733 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2735 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterList);
                      break;
            }
    
            //
            // Rule 318:  FormalParameterList ::= FormalParameter
            //
            case 318: {
               //#line 2741 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2739 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2741 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 319:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 319: {
               //#line 2748 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2746 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2746 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2748 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 320:  LoopIndexDeclarator ::= Identifier ResultTypeopt
            //
            case 320: {
               //#line 2754 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2752 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2752 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2754 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 321:  LoopIndexDeclarator ::= ( IdentifierList ) ResultTypeopt
            //
            case 321: {
               //#line 2759 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2757 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2757 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2759 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 322:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt
            //
            case 322: {
               //#line 2764 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2762 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2762 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2762 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2764 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 323:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 323: {
               //#line 2770 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2768 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2768 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2770 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 324:  LoopIndex ::= VariableModifiersopt VarKeyword LoopIndexDeclarator
            //
            case 324: {
               //#line 2793 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2791 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2791 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2791 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2793 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 325:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 325: {
               //#line 2817 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2815 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2815 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2817 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 326:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 326: {
               //#line 2841 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2839 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2839 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2839 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2841 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 327:  FormalParameter ::= Type
            //
            case 327: {
               //#line 2865 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2863 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2865 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh()), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 328:  VariableModifiers ::= VariableModifier
            //
            case 328: {
               //#line 2873 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2871 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2873 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 329:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 329: {
               //#line 2880 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2878 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2878 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2880 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 330:  VariableModifier ::= Annotation
            //
            case 330: {
               //#line 2886 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2884 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2886 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 331:  VariableModifier ::= shared
            //
            case 331: {
               //#line 2891 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2891 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 332:  MethodModifiers ::= MethodModifier
            //
            case 332: {
               //#line 2900 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2898 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2900 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 333:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 333: {
               //#line 2907 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2905 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2905 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2907 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 334:  MethodModifier ::= Annotation
            //
            case 334: {
               //#line 2913 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2911 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2913 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 335:  MethodModifier ::= public
            //
            case 335: {
               //#line 2918 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2918 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 336:  MethodModifier ::= protected
            //
            case 336: {
               //#line 2923 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2923 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 337:  MethodModifier ::= private
            //
            case 337: {
               //#line 2928 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2928 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 338:  MethodModifier ::= abstract
            //
            case 338: {
               //#line 2933 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2933 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 339:  MethodModifier ::= static
            //
            case 339: {
               //#line 2938 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2938 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 340:  MethodModifier ::= final
            //
            case 340: {
               //#line 2943 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2943 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= native
            //
            case 341: {
               //#line 2948 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2948 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= strictfp
            //
            case 342: {
               //#line 2953 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2953 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= atomic
            //
            case 343: {
               //#line 2958 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2958 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= extern
            //
            case 344: {
               //#line 2963 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2963 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= safe
            //
            case 345: {
               //#line 2968 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2968 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= sequential
            //
            case 346: {
               //#line 2973 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2973 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= local
            //
            case 347: {
               //#line 2978 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2978 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.LOCAL)));
                      break;
            }
    
            //
            // Rule 348:  MethodModifier ::= nonblocking
            //
            case 348: {
               //#line 2983 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2983 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 349:  MethodModifier ::= incomplete
            //
            case 349: {
               //#line 2988 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2988 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 350:  MethodModifier ::= property
            //
            case 350: {
               //#line 2993 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2993 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 351:  Throws ::= throws ExceptionTypeList
            //
            case 351: {
               //#line 3000 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2998 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3000 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 352:  ExceptionTypeList ::= ExceptionType
            //
            case 352: {
               //#line 3006 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3004 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3006 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 353:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 353: {
               //#line 3013 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3011 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3011 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3013 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 355:  MethodBody ::= = LastExpression ;
            //
            case 355: {
               //#line 3021 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3019 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3021 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 356:  MethodBody ::= = { BlockStatementsopt LastExpression }
            //
            case 356: {
               //#line 3026 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3024 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3024 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 3026 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 357:  MethodBody ::= = Block
            //
            case 357: {
               //#line 3034 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3032 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3034 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 358:  MethodBody ::= Block
            //
            case 358: {
               //#line 3039 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3037 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(1);
                //#line 3039 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 359:  MethodBody ::= ;
            //
            case 359:
                setResult(null);
                break;

            //
            // Rule 360:  InstanceInitializer ::= Block
            //
            case 360: {
               //#line 3047 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3045 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(1);
                //#line 3047 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(), Flags.NONE), Block));
                      break;
            }
    
            //
            // Rule 361:  StaticInitializer ::= static Block
            //
            case 361: {
               //#line 3053 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3051 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3053 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(getLeftSpan()), Flags.STATIC), Block));
                      break;
            }
    
            //
            // Rule 362:  SimpleTypeName ::= Identifier
            //
            case 362: {
               //#line 3059 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3057 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3059 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 363:  ConstructorModifiers ::= ConstructorModifier
            //
            case 363: {
               //#line 3065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3063 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 364:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 364: {
               //#line 3072 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3070 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3070 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3072 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 365:  ConstructorModifier ::= Annotation
            //
            case 365: {
               //#line 3078 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3076 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3078 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 366:  ConstructorModifier ::= public
            //
            case 366: {
               //#line 3083 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3083 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 367:  ConstructorModifier ::= protected
            //
            case 367: {
               //#line 3088 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3088 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 368:  ConstructorModifier ::= private
            //
            case 368: {
               //#line 3093 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3093 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 369:  ConstructorModifier ::= native
            //
            case 369: {
               //#line 3098 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3098 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 370:  ConstructorBody ::= = ConstructorBlock
            //
            case 370: {
               //#line 3104 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3102 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3104 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 371:  ConstructorBody ::= ConstructorBlock
            //
            case 371: {
               //#line 3109 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3107 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3109 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 372:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 372: {
               //#line 3114 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3112 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3114 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 373:  ConstructorBody ::= = AssignPropertyCall
            //
            case 373: {
               //#line 3122 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3120 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3122 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.SuperCall(pos(), Collections.EMPTY_LIST));
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 374:  ConstructorBody ::= ;
            //
            case 374:
                setResult(null);
                break;

            //
            // Rule 375:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 375: {
               //#line 3134 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3132 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3132 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3134 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 376:  Arguments ::= ( ArgumentListopt )
            //
            case 376: {
               //#line 3151 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3149 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3151 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 378:  InterfaceModifiers ::= InterfaceModifier
            //
            case 378: {
               //#line 3161 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3159 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3161 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 379:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 379: {
               //#line 3168 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3166 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3166 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3168 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 380:  InterfaceModifier ::= Annotation
            //
            case 380: {
               //#line 3174 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3172 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3174 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 381:  InterfaceModifier ::= public
            //
            case 381: {
               //#line 3179 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3179 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 382:  InterfaceModifier ::= protected
            //
            case 382: {
               //#line 3184 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3184 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 383:  InterfaceModifier ::= private
            //
            case 383: {
               //#line 3189 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3189 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 384:  InterfaceModifier ::= abstract
            //
            case 384: {
               //#line 3194 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3194 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 385:  InterfaceModifier ::= static
            //
            case 385: {
               //#line 3199 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3199 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 386:  InterfaceModifier ::= strictfp
            //
            case 386: {
               //#line 3204 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3204 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 387:  ExtendsInterfaces ::= extends Type
            //
            case 387: {
               //#line 3210 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3208 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3210 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 388:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 388: {
               //#line 3217 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3215 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3215 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3217 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 389:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 389: {
               //#line 3226 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3224 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3226 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 391:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 391: {
               //#line 3233 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3231 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3231 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3233 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 392:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 392: {
               //#line 3240 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3238 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3240 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 393:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 393: {
               //#line 3247 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3245 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3247 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 394:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 394: {
               //#line 3254 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3252 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3254 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 395:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 395: {
               //#line 3261 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3259 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3261 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 396:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 396: {
               //#line 3268 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3266 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3268 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 397:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 397: {
               //#line 3275 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3273 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3275 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 398:  InterfaceMemberDeclaration ::= ;
            //
            case 398: {
               //#line 3282 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3282 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 399:  Annotations ::= Annotation
            //
            case 399: {
               //#line 3288 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3286 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3288 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 400:  Annotations ::= Annotations Annotation
            //
            case 400: {
               //#line 3295 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3293 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3293 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3295 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 401:  Annotation ::= @ NamedType
            //
            case 401: {
               //#line 3301 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3299 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3301 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 402:  SimpleName ::= Identifier
            //
            case 402: {
               //#line 3307 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3305 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3307 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 403:  Identifier ::= identifier
            //
            case 403: {
               //#line 3313 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3311 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3313 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 404:  VariableInitializers ::= VariableInitializer
            //
            case 404: {
               //#line 3321 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3319 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3321 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 405:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 405: {
               //#line 3328 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3326 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3326 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3328 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 406:  Block ::= { BlockStatementsopt }
            //
            case 406: {
               //#line 3346 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3344 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3346 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 407:  BlockStatements ::= BlockStatement
            //
            case 407: {
               //#line 3352 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3350 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3352 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 408:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 408: {
               //#line 3359 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3357 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3357 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3359 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 410:  BlockStatement ::= ClassDeclaration
            //
            case 410: {
               //#line 3367 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3365 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3367 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 411:  BlockStatement ::= TypeDefDeclaration
            //
            case 411: {
               //#line 3374 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3372 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3374 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 412:  BlockStatement ::= Statement
            //
            case 412: {
               //#line 3381 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3379 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3381 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 413:  IdentifierList ::= Identifier
            //
            case 413: {
               //#line 3389 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3387 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3389 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 414:  IdentifierList ::= IdentifierList , Identifier
            //
            case 414: {
               //#line 3396 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3394 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3394 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3396 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 415:  FormalDeclarator ::= Identifier : Type
            //
            case 415: {
               //#line 3402 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3400 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3400 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3402 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, Type, null });
                      break;
            }
    
            //
            // Rule 416:  FormalDeclarator ::= ( IdentifierList ) : Type
            //
            case 416: {
               //#line 3407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3405 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3405 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(5);
                //#line 3407 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, Type, null });
                      break;
            }
    
            //
            // Rule 417:  FormalDeclarator ::= Identifier ( IdentifierList ) : Type
            //
            case 417: {
               //#line 3412 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3410 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3410 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3410 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(6);
                //#line 3412 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, Type, null });
                      break;
            }
    
            //
            // Rule 418:  FieldDeclarator ::= Identifier : Type
            //
            case 418: {
               //#line 3418 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3416 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3416 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3418 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, Type, null });
                      break;
            }
    
            //
            // Rule 419:  FieldDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 419: {
               //#line 3423 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3421 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3421 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3421 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3423 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 420:  VariableDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 420: {
               //#line 3429 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3427 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3427 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3427 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3429 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 421:  VariableDeclarator ::= ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 421: {
               //#line 3434 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3432 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3432 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3432 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3434 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 422:  VariableDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 422: {
               //#line 3439 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3437 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3437 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3437 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3437 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3439 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 423:  VariableDeclaratorWithType ::= Identifier ResultType = VariableInitializer
            //
            case 423: {
               //#line 3445 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3443 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3443 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3443 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3445 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 424:  VariableDeclaratorWithType ::= ( IdentifierList ) ResultType = VariableInitializer
            //
            case 424: {
               //#line 3450 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3448 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3448 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3448 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3450 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 425:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) ResultType = VariableInitializer
            //
            case 425: {
               //#line 3455 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3453 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3453 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3453 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3453 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3455 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 427:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 427: {
               //#line 3463 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3461 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3461 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3461 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3463 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 428:  LocalVariableDeclaration ::= VariableModifiersopt VariableDeclaratorsWithType
            //
            case 428: {
               //#line 3496 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3494 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3494 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3496 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 429:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 429: {
               //#line 3530 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3528 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3528 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3528 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3530 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 431:  Primary ::= TypeName . class
            //
            case 431: {
               //#line 3571 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3569 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3571 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeName instanceof ParsedName)
                {
                    ParsedName a = (ParsedName) TypeName;
                    setResult(nf.ClassLit(pos(), a.toType()));
                }
                else assert(false);
                      break;
            }
    
            //
            // Rule 432:  Primary ::= self
            //
            case 432: {
               //#line 3581 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3581 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 433:  Primary ::= this
            //
            case 433: {
               //#line 3586 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3586 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 434:  Primary ::= ClassName . this
            //
            case 434: {
               //#line 3591 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3589 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3591 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 435:  Primary ::= ( Expression )
            //
            case 435: {
               //#line 3596 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3594 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3596 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 441:  OperatorFunction ::= TypeName . +
            //
            case 441: {
               //#line 3607 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3605 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3607 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 442:  OperatorFunction ::= TypeName . -
            //
            case 442: {
               //#line 3618 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3616 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3618 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 443:  OperatorFunction ::= TypeName . *
            //
            case 443: {
               //#line 3629 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3627 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3629 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 444:  OperatorFunction ::= TypeName . /
            //
            case 444: {
               //#line 3640 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3638 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3640 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 445:  OperatorFunction ::= TypeName . %
            //
            case 445: {
               //#line 3651 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3649 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3651 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 446:  OperatorFunction ::= TypeName . &
            //
            case 446: {
               //#line 3662 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3660 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3662 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 447:  OperatorFunction ::= TypeName . |
            //
            case 447: {
               //#line 3673 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3671 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3673 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 448:  OperatorFunction ::= TypeName . ^
            //
            case 448: {
               //#line 3684 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3682 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3684 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 449:  OperatorFunction ::= TypeName . <<
            //
            case 449: {
               //#line 3695 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3693 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3695 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 450:  OperatorFunction ::= TypeName . >>
            //
            case 450: {
               //#line 3706 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3704 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3706 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 451:  OperatorFunction ::= TypeName . >>>
            //
            case 451: {
               //#line 3717 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3715 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3717 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 452:  OperatorFunction ::= TypeName . <
            //
            case 452: {
               //#line 3728 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3726 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3728 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 453:  OperatorFunction ::= TypeName . <=
            //
            case 453: {
               //#line 3739 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3737 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3739 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 454:  OperatorFunction ::= TypeName . >=
            //
            case 454: {
               //#line 3750 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3748 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3750 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 455:  OperatorFunction ::= TypeName . >
            //
            case 455: {
               //#line 3761 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3759 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3761 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 456:  OperatorFunction ::= TypeName . ==
            //
            case 456: {
               //#line 3772 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3770 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3772 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 457:  OperatorFunction ::= TypeName . !=
            //
            case 457: {
               //#line 3783 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3781 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3783 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 458:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 458: {
               //#line 3796 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3794 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                //#line 3796 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 459:  Literal ::= LongLiteral$LongLiteral
            //
            case 459: {
               //#line 3802 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3800 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken LongLiteral = (IToken) getRhsIToken(1);
                //#line 3802 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 460:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 460: {
               //#line 3808 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3806 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                //#line 3808 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 461:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 461: {
               //#line 3814 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3812 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                //#line 3814 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 462:  Literal ::= BooleanLiteral
            //
            case 462: {
               //#line 3820 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3818 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3820 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 463:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 463: {
               //#line 3825 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3823 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                //#line 3825 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 464:  Literal ::= StringLiteral$str
            //
            case 464: {
               //#line 3831 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3829 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3831 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 465:  Literal ::= null
            //
            case 465: {
               //#line 3837 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3837 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 466:  BooleanLiteral ::= true$trueLiteral
            //
            case 466: {
               //#line 3843 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3841 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3843 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 467:  BooleanLiteral ::= false$falseLiteral
            //
            case 467: {
               //#line 3848 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3846 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3848 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 468:  ArgumentList ::= Expression
            //
            case 468: {
               //#line 3857 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3855 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3857 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 469:  ArgumentList ::= ArgumentList , Expression
            //
            case 469: {
               //#line 3864 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3862 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3862 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3864 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 470:  FieldAccess ::= Primary . Identifier
            //
            case 470: {
               //#line 3870 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3868 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3868 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3870 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 471:  FieldAccess ::= super . Identifier
            //
            case 471: {
               //#line 3875 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3873 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3875 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 472:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 472: {
               //#line 3880 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3878 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3878 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3878 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3880 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 473:  FieldAccess ::= Primary . class$c
            //
            case 473: {
               //#line 3885 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3883 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3883 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3885 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 474:  FieldAccess ::= super . class$c
            //
            case 474: {
               //#line 3890 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3888 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3890 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 475:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 475: {
               //#line 3895 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3893 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3893 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3893 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 3895 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 476:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 476: {
               //#line 3901 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3899 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3899 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3899 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3901 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 477:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 477: {
               //#line 3908 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3906 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3906 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3906 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3906 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3908 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 478:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 478: {
               //#line 3913 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3911 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3911 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3911 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3913 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 479:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 479: {
               //#line 3918 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3916 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3916 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3916 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3916 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3916 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3918 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 480:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 480: {
               //#line 3923 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3921 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3921 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3921 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3923 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 481:  MethodSelection ::= MethodName . TypeParametersopt ( FormalParameterListopt )
            //
            case 481: {
               //#line 3943 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3941 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3941 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 3941 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(5);
                //#line 3943 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 482:  MethodSelection ::= Primary . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 482: {
               //#line 3956 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3954 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3954 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3954 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3954 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3956 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 483:  MethodSelection ::= super . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 483: {
               //#line 3968 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3966 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3966 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3966 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3968 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 484:  MethodSelection ::= ClassName . super$sup . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 484: {
               //#line 3980 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3978 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3978 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3978 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3978 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(7);
                //#line 3978 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(9);
                //#line 3980 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 486:  PostfixExpression ::= ExpressionName
            //
            case 486: {
               //#line 3994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3992 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 3994 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 489:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 489: {
               //#line 4002 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4000 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4002 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 490:  PostDecrementExpression ::= PostfixExpression --
            //
            case 490: {
               //#line 4008 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4006 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4008 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 493:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 493: {
               //#line 4016 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4014 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4016 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 494:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 494: {
               //#line 4021 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4019 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4021 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 496:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 496: {
               //#line 4028 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4026 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4028 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 497:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 497: {
               //#line 4034 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4032 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4034 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 499:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 499: {
               //#line 4041 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4039 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4041 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 500:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 500: {
               //#line 4046 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4044 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4044 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4046 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e);
                      break;
            }
    
            //
            // Rule 501:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 501: {
               //#line 4053 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4051 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4053 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 503:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 503: {
               //#line 4060 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4058 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4058 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4060 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 504:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 504: {
               //#line 4065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4063 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4063 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4065 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 505:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 505: {
               //#line 4070 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4068 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4068 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4070 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 507:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 507: {
               //#line 4077 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4075 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4075 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4077 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 508:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 508: {
               //#line 4082 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4080 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4080 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4082 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 510:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 510: {
               //#line 4089 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4087 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4087 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4089 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 511:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 511: {
               //#line 4094 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4092 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4092 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4094 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 512:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 512: {
               //#line 4099 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4097 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4097 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4099 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 514:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 514: {
               //#line 4106 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4104 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4104 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4106 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 517:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 517: {
               //#line 4115 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4113 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4113 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4115 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 518:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 518: {
               //#line 4120 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4118 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4120 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 519:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 519: {
               //#line 4125 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4123 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4123 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4125 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 520:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 520: {
               //#line 4130 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4128 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4128 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4130 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 521:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 521: {
               //#line 4135 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4133 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4133 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4135 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 522:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 522: {
               //#line 4140 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4138 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4138 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4140 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 524:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 524: {
               //#line 4147 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4145 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4145 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4147 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 525:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 525: {
               //#line 4152 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4150 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4150 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4152 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 526:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 526: {
               //#line 4157 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4155 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4155 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4157 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 528:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 528: {
               //#line 4164 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4162 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4162 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4164 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 530:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 530: {
               //#line 4171 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4169 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4169 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4171 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 532:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 532: {
               //#line 4178 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4176 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4176 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4178 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 534:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 534: {
               //#line 4185 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4183 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4183 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4185 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 536:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 536: {
               //#line 4192 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4190 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4190 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4192 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 542:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 542: {
               //#line 4204 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4202 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4202 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4202 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4204 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 545:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 545: {
               //#line 4213 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4211 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4211 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4211 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4213 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 546:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 546: {
               //#line 4218 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4216 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4216 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4216 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4216 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4218 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 547:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 547: {
               //#line 4223 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4221 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4221 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4221 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4221 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4223 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 548:  LeftHandSide ::= ExpressionName
            //
            case 548: {
               //#line 4229 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4227 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4229 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 550:  AssignmentOperator ::= =
            //
            case 550: {
               //#line 4236 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4236 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 551:  AssignmentOperator ::= *=
            //
            case 551: {
               //#line 4241 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4241 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 552:  AssignmentOperator ::= /=
            //
            case 552: {
               //#line 4246 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4246 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 553:  AssignmentOperator ::= %=
            //
            case 553: {
               //#line 4251 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4251 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 554:  AssignmentOperator ::= +=
            //
            case 554: {
               //#line 4256 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4256 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 555:  AssignmentOperator ::= -=
            //
            case 555: {
               //#line 4261 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4261 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 556:  AssignmentOperator ::= <<=
            //
            case 556: {
               //#line 4266 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4266 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 557:  AssignmentOperator ::= >>=
            //
            case 557: {
               //#line 4271 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4271 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 558:  AssignmentOperator ::= >>>=
            //
            case 558: {
               //#line 4276 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4276 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= &=
            //
            case 559: {
               //#line 4281 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4281 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 560:  AssignmentOperator ::= ^=
            //
            case 560: {
               //#line 4286 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4286 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 561:  AssignmentOperator ::= |=
            //
            case 561: {
               //#line 4291 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4291 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 564:  PrefixOp ::= +
            //
            case 564: {
               //#line 4302 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4302 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 565:  PrefixOp ::= -
            //
            case 565: {
               //#line 4307 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4307 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 566:  PrefixOp ::= !
            //
            case 566: {
               //#line 4312 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4312 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 567:  PrefixOp ::= ~
            //
            case 567: {
               //#line 4317 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4317 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 568:  BinOp ::= +
            //
            case 568: {
               //#line 4323 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4323 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 569:  BinOp ::= -
            //
            case 569: {
               //#line 4328 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4328 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 570:  BinOp ::= *
            //
            case 570: {
               //#line 4333 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4333 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 571:  BinOp ::= /
            //
            case 571: {
               //#line 4338 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4338 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 572:  BinOp ::= %
            //
            case 572: {
               //#line 4343 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4343 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 573:  BinOp ::= &
            //
            case 573: {
               //#line 4348 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4348 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 574:  BinOp ::= |
            //
            case 574: {
               //#line 4353 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4353 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 575:  BinOp ::= ^
            //
            case 575: {
               //#line 4358 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4358 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 576:  BinOp ::= &&
            //
            case 576: {
               //#line 4363 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4363 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 577:  BinOp ::= ||
            //
            case 577: {
               //#line 4368 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4368 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 578:  BinOp ::= <<
            //
            case 578: {
               //#line 4373 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4373 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 579:  BinOp ::= >>
            //
            case 579: {
               //#line 4378 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4378 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 580:  BinOp ::= >>>
            //
            case 580: {
               //#line 4383 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4383 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 581:  BinOp ::= >=
            //
            case 581: {
               //#line 4388 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4388 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 582:  BinOp ::= <=
            //
            case 582: {
               //#line 4393 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4393 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 583:  BinOp ::= >
            //
            case 583: {
               //#line 4398 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4398 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 584:  BinOp ::= <
            //
            case 584: {
               //#line 4403 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4403 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 585:  BinOp ::= ==
            //
            case 585: {
               //#line 4411 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4411 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 586:  BinOp ::= !=
            //
            case 586: {
               //#line 4416 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4416 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 587:  Catchesopt ::= $Empty
            //
            case 587: {
               //#line 4425 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4425 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 589:  Identifieropt ::= $Empty
            //
            case 589:
                setResult(null);
                break;

            //
            // Rule 590:  Identifieropt ::= Identifier
            //
            case 590: {
               //#line 4434 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4432 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/x10.compiler.p3/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4434 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 591:  ForUpdateopt ::= $Empty
            //
            case 591: {
               //#line 4440 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4440 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 593:  Expressionopt ::= $Empty
            //
            case 593:
                setResult(null);
                break;

            //
            // Rule 595:  ForInitopt ::= $Empty
            //
            case 595: {
               //#line 4451 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4451 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 597:  SwitchLabelsopt ::= $Empty
            //
            case 597: {
               //#line 4458 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4458 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 599:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 599: {
               //#line 4465 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4465 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 601:  VariableModifiersopt ::= $Empty
            //
            case 601: {
               //#line 4472 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4472 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 603:  VariableInitializersopt ::= $Empty
            //
            case 603:
                setResult(null);
                break;

            //
            // Rule 605:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 605: {
               //#line 4483 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4483 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 607:  ExtendsInterfacesopt ::= $Empty
            //
            case 607: {
               //#line 4490 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4490 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 609:  InterfaceModifiersopt ::= $Empty
            //
            case 609: {
               //#line 4497 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4497 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 611:  ClassBodyopt ::= $Empty
            //
            case 611:
                setResult(null);
                break;

            //
            // Rule 613:  Argumentsopt ::= $Empty
            //
            case 613: {
               //#line 4508 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4508 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 615:  ArgumentListopt ::= $Empty
            //
            case 615: {
               //#line 4515 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4515 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 617:  BlockStatementsopt ::= $Empty
            //
            case 617: {
               //#line 4522 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4522 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 619:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 619:
                setResult(null);
                break;

            //
            // Rule 621:  ConstructorModifiersopt ::= $Empty
            //
            case 621: {
               //#line 4533 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4533 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 623:  FormalParameterListopt ::= $Empty
            //
            case 623: {
               //#line 4540 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4540 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 625:  Throwsopt ::= $Empty
            //
            case 625: {
               //#line 4547 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4547 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 627:  MethodModifiersopt ::= $Empty
            //
            case 627: {
               //#line 4554 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4554 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 629:  FieldModifiersopt ::= $Empty
            //
            case 629: {
               //#line 4561 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4561 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 631:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 631: {
               //#line 4568 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4568 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 633:  Interfacesopt ::= $Empty
            //
            case 633: {
               //#line 4575 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4575 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 635:  Superopt ::= $Empty
            //
            case 635:
                setResult(null);
                break;

            //
            // Rule 637:  TypeParametersopt ::= $Empty
            //
            case 637: {
               //#line 4586 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4586 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 639:  FormalParametersopt ::= $Empty
            //
            case 639: {
               //#line 4593 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4593 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 641:  Annotationsopt ::= $Empty
            //
            case 641: {
               //#line 4600 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4600 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 643:  TypeDeclarationsopt ::= $Empty
            //
            case 643: {
               //#line 4607 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4607 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 645:  ImportDeclarationsopt ::= $Empty
            //
            case 645: {
               //#line 4614 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4614 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 647:  PackageDeclarationopt ::= $Empty
            //
            case 647:
                setResult(null);
                break;

            //
            // Rule 649:  ResultTypeopt ::= $Empty
            //
            case 649:
                setResult(null);
                break;

            //
            // Rule 651:  TypeArgumentsopt ::= $Empty
            //
            case 651: {
               //#line 4629 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4629 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 653:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 653: {
               //#line 4636 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4636 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 655:  Propertiesopt ::= $Empty
            //
            case 655: {
               //#line 4643 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4643 "/Users/rmfuhrer/eclipse/workspaces/x10-refactoring-1.7-3.4/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 657:  ,opt ::= $Empty
            //
            case 657:
                setResult(null);
                break;

    
            default:
                break;
        }
        return;
    }
}

