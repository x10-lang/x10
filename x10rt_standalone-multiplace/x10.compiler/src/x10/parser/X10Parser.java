
//#line 18 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
//
// Licensed Material
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

import lpg.runtime.*;

//#line 28 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.io.File;

import x10.ast.X10Binary_c;
import x10.ast.X10Unary_c;
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
import x10.ast.AddFlags;
import x10.ast.AnnotationNode;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.Here;
import x10.ast.DepParameterExpr;
import x10.ast.Tuple;
import x10.ast.When;
import x10.ast.X10Formal;
import x10.ast.X10Formal_c;
import x10.ast.X10Loop;
import x10.ast.X10Call;
import x10.ast.ConstantDistMaker;
import x10.ast.TypeDecl;
import x10.ast.TypeParamNode;
import x10.ast.X10NodeFactory;
import x10.types.ParameterType;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.ast.PropertyDecl;
import x10.ast.RegionMaker;
import x10.ast.X10Binary_c;
import x10.ast.X10Cast_c;
import x10.ast.X10Unary_c;
import x10.ast.X10IntLit_c;
import x10.extension.X10Ext;
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
import x10.types.X10Flags;
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
    

    //#line 322 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
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
        int radix;
        int start_index;
        int end_index;
        
        end_index = s.length();

        while (end_index > 0) {
            char lastCh = s.charAt(end_index - 1);
            if (lastCh != 'l' && lastCh != 'L' && lastCh != 'u' && lastCh != 'U') {
                    break;
            }
            end_index--;
        }

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

    private polyglot.lex.LongLiteral int_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i),  x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.LongLiteral long_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i), x, X10Parsersym.TK_LongLiteral);
    }
    private polyglot.lex.LongLiteral ulong_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i), x, X10Parsersym.TK_UnsignedLongLiteral);
    }
    private polyglot.lex.LongLiteral uint_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i), x, X10Parsersym.TK_UnsignedIntegerLiteral);
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
               //#line 8 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 6 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 18 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 16 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 28 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 26 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 38 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 36 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 48 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 46 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 58 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 56 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 68 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 66 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 74 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 78 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 85 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 92 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 98 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 109 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 107 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 117 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 115 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 120 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 892 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 890 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 890 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 890 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 890 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 890 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 890 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 892 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 904 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 902 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 902 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 902 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 902 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 902 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 904 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 917 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 915 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 917 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 19:  PropertyList ::= Property
            //
            case 19: {
               //#line 922 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 920 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 922 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 20:  PropertyList ::= PropertyList , Property
            //
            case 20: {
               //#line 929 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 927 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 927 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 929 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 21:  Property ::= Annotationsopt Identifier ResultType
            //
            case 21: {
               //#line 936 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 934 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 934 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 934 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 936 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List annotations = extractAnnotations(Annotationsopt);
                PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), ResultType, Identifier);
                cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 22:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 22: {
               //#line 945 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 943 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 943 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 943 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 943 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 943 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 943 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 943 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 943 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 945 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 975 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 973 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 973 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 973 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 973 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 973 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 973 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 973 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 973 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 973 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 975 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 992 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 990 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 990 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 990 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 990 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 990 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 990 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 990 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 990 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 992 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 1009 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1007 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1007 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1007 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1007 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1007 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1007 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1007 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1007 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1009 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 1027 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1025 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1025 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1025 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1025 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1025 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1025 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1025 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1025 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1027 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 1046 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1044 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1044 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1044 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1044 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1044 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1044 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1044 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1046 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 1063 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1061 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1061 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1061 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1061 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1061 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1061 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1061 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1063 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 1080 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1078 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1078 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1078 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1078 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1078 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1078 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1078 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1078 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1080 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 1097 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1095 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1095 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1095 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1095 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1095 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1095 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1095 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1097 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(11)),
          extractFlags(MethodModifiersopt),
          Type,
          nf.Id(pos(), X10Cast_c.operator_as),
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
               //#line 1114 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1112 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1112 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1112 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1112 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1112 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1112 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1112 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1114 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(12)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(), X10Cast_c.operator_as),
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
            // Rule 32:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 32: {
               //#line 1131 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1129 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1129 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1129 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1129 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1129 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1129 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(9);
                //#line 1129 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1131 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(10)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(), X10Cast_c.implicit_operator_as),
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
            // Rule 33:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 33: {
               //#line 1149 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1147 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1147 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1147 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1147 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1147 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1147 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1147 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1147 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1149 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 34:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier WhereClauseopt ResultTypeopt MethodBody
            //
            case 34: {
               //#line 1164 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1162 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1162 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1162 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(4);
                //#line 1162 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 1162 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 1164 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 35:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 35: {
               //#line 1180 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1178 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1178 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1180 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 36:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 36: {
               //#line 1185 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1183 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1183 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1185 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 37:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 37: {
               //#line 1190 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1188 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1188 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1188 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1190 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 38:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 38: {
               //#line 1195 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1193 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1193 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1193 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1195 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 39:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 39: {
               //#line 1201 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1199 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 1199 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1199 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1199 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1199 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1199 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1199 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1201 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 40:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 40: {
               //#line 1222 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1220 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1220 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1220 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1220 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1222 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 41:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 41: {
               //#line 1229 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1227 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1227 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1227 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1227 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1227 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1229 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 42:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 42: {
               //#line 1237 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1235 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1235 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1235 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1235 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1235 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1237 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 43:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 43: {
               //#line 1246 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1244 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1244 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1246 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 46:  Type ::= proto ConstrainedType
            //
            case 46: {
               //#line 1255 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1253 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ConstrainedType = (TypeNode) getRhsSym(2);
                //#line 1255 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
        AddFlags tn = (AddFlags) ConstrainedType;
        tn.addFlags(X10Flags.PROTO);
        setResult(tn);
                      break;
            }
    
            //
            // Rule 47:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt => Type
            //
            case 47: {
               //#line 1263 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1261 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1261 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1261 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1261 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1261 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1263 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt));
                      break;
            }
    
            //
            // Rule 52:  AnnotatedType ::= Type Annotations
            //
            case 52: {
               //#line 1272 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1270 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1270 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1272 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn);
                      break;
            }
    
            //
            // Rule 55:  ConstrainedType ::= ( Type )
            //
            case 55: {
               //#line 1282 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1280 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1282 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 57:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 57: {
               //#line 1296 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1294 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1294 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1294 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1294 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1294 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1296 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
            TypeNode type = nf.X10AmbTypeNode(pos(), Primary, Identifier);
            // TODO: place constraint
            if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                type = nf.AmbDepTypeNode(pos(), Primary, Identifier, TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                      break;
            }
    
            //
            // Rule 58:  NamedType ::= TypeName TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 58: {
               //#line 1307 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1305 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1305 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1305 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1305 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1307 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 59:  DepParameters ::= { ExistentialListopt Conjunction }
            //
            case 59: {
               //#line 1333 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1331 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1331 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(3);
                //#line 1333 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
                      break;
            }
    
            //
            // Rule 60:  DepParameters ::= ! PlaceType
            //
            case 60: {
               //#line 1338 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1336 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1338 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 61:  DepParameters ::= !
            //
            case 61: {
               //#line 1344 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 1344 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 62:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 62: {
               //#line 1350 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1348 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1348 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(4);
                //#line 1348 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(5);
                //#line 1350 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 63:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 63: {
               //#line 1356 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1354 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(3);
                //#line 1354 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(4);
                //#line 1356 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 64:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 64: {
               //#line 1364 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1362 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1364 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 65:  TypeParameters ::= [ TypeParameterList ]
            //
            case 65: {
               //#line 1370 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1368 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1370 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 66:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 66: {
               //#line 1375 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1373 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1375 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 67:  Conjunction ::= Expression
            //
            case 67: {
               //#line 1381 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1379 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1381 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 68:  Conjunction ::= Conjunction , Expression
            //
            case 68: {
               //#line 1388 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1386 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1386 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1388 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 69:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 69: {
               //#line 1394 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1392 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1392 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1394 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 70:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 70: {
               //#line 1399 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1397 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1397 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1399 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 71:  WhereClause ::= DepParameters
            //
            case 71: {
               //#line 1405 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1403 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1405 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 72:  ExistentialListopt ::= $Empty
            //
            case 72: {
               //#line 1411 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 1411 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 73:  ExistentialListopt ::= ExistentialList ;
            //
            case 73: {
               //#line 1416 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1414 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1416 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 74:  ExistentialList ::= FormalParameter
            //
            case 74: {
               //#line 1422 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1420 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1422 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 75:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 75: {
               //#line 1429 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1427 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1427 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1429 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 78:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 78: {
               //#line 1440 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1438 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1438 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1438 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1438 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1438 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1438 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1438 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1438 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1440 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 79:  StructDeclaration ::= ClassModifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 79: {
               //#line 1456 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1454 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1454 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1454 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1454 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1454 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1454 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1454 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1456 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
    checkTypeName(Identifier);
                List TypeParametersopt = TypeParamsWithVarianceopt;
    List props = Propertiesopt;
    DepParameterExpr ci = WhereClauseopt;
    ClassDecl cd = (nf.X10ClassDecl(pos(getLeftSpan(), getRightSpan()),
    extractFlags(ClassModifiersopt, X10Flags.STRUCT), Identifier,  TypeParametersopt,
    props, ci, null, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ClassModifiersopt));
    setResult(cd);
                      break;
            }
    
            //
            // Rule 80:  ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt ConstructorBody
            //
            case 80: {
               //#line 1470 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1468 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1468 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1468 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1468 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1468 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1468 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1468 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(9);
                //#line 1470 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 81:  Super ::= extends ClassType
            //
            case 81: {
               //#line 1486 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1484 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1486 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 82:  FieldKeyword ::= val
            //
            case 82: {
               //#line 1492 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 1492 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 83:  FieldKeyword ::= var
            //
            case 83: {
               //#line 1497 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 1497 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 84:  FieldKeyword ::= const
            //
            case 84: {
               //#line 1502 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 1502 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 85:  VarKeyword ::= val
            //
            case 85: {
               //#line 1510 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 1510 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 86:  VarKeyword ::= var
            //
            case 86: {
               //#line 1515 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 1515 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 87:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 87: {
               //#line 1522 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1520 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1520 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1520 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1522 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 88:  FieldDeclaration ::= FieldModifiersopt FieldDeclarators ;
            //
            case 88: {
               //#line 1546 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1544 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1544 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1546 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 91:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 91: {
               //#line 1577 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1575 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 1575 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 1577 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                if (NonExpressionStatement.ext() instanceof X10Ext && Annotationsopt instanceof List) {
                    NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations((List) Annotationsopt);
                }
                setResult(NonExpressionStatement);
                      break;
            }
    
            //
            // Rule 118:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 118: {
               //#line 1613 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1611 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1611 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1613 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 119:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 119: {
               //#line 1619 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1617 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1617 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1617 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1619 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 120:  EmptyStatement ::= ;
            //
            case 120: {
               //#line 1625 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 1625 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 121:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 121: {
               //#line 1631 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1629 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1629 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 1631 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 127:  ExpressionStatement ::= StatementExpression ;
            //
            case 127: {
               //#line 1643 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1641 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1643 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 135:  AssertStatement ::= assert Expression ;
            //
            case 135: {
               //#line 1674 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1672 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1674 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 136:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 136: {
               //#line 1679 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1677 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1677 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1679 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 137:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 137: {
               //#line 1685 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1683 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1683 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1685 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 138:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 138: {
               //#line 1691 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1689 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1689 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1691 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 140:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 140: {
               //#line 1699 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1697 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1697 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1699 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 141:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 141: {
               //#line 1706 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1704 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1704 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1706 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 142:  SwitchLabels ::= SwitchLabel
            //
            case 142: {
               //#line 1715 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1713 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1715 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 143:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 143: {
               //#line 1722 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1720 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1720 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1722 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 144:  SwitchLabel ::= case ConstantExpression :
            //
            case 144: {
               //#line 1729 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1727 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1729 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 145:  SwitchLabel ::= default :
            //
            case 145: {
               //#line 1734 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 1734 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 146:  WhileStatement ::= while ( Expression ) Statement
            //
            case 146: {
               //#line 1740 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1738 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1738 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1740 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 147:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 147: {
               //#line 1746 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1744 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1744 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1746 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 150:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 150: {
               //#line 1755 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1753 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1753 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1753 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1753 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1755 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 152:  ForInit ::= LocalVariableDeclaration
            //
            case 152: {
               //#line 1762 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1760 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1762 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 154:  StatementExpressionList ::= StatementExpression
            //
            case 154: {
               //#line 1772 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1770 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1772 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 155:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 155: {
               //#line 1779 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1777 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1777 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1779 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 156:  BreakStatement ::= break Identifieropt ;
            //
            case 156: {
               //#line 1785 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1783 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1785 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 157:  ContinueStatement ::= continue Identifieropt ;
            //
            case 157: {
               //#line 1791 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1789 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1791 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 158:  ReturnStatement ::= return Expressionopt ;
            //
            case 158: {
               //#line 1797 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1795 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1797 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 159:  ThrowStatement ::= throw Expression ;
            //
            case 159: {
               //#line 1803 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1801 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1803 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 160:  TryStatement ::= try Block Catches
            //
            case 160: {
               //#line 1809 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1807 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1807 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1809 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 161:  TryStatement ::= try Block Catchesopt Finally
            //
            case 161: {
               //#line 1814 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1812 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1812 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1812 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1814 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 162:  Catches ::= CatchClause
            //
            case 162: {
               //#line 1820 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1818 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1820 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 163:  Catches ::= Catches CatchClause
            //
            case 163: {
               //#line 1827 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1825 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1825 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1827 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 164:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 164: {
               //#line 1834 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1832 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1832 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1834 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 165:  Finally ::= finally Block
            //
            case 165: {
               //#line 1840 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1838 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1840 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 166:  NowStatement ::= now ( Clock ) Statement
            //
            case 166: {
               //#line 1846 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1844 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1844 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1846 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
              setResult(nf.Now(pos(), Clock, Statement));
                      break;
            }
    
            //
            // Rule 167:  ClockedClause ::= clocked ( ClockList )
            //
            case 167: {
               //#line 1852 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1850 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1852 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 168:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 168: {
               //#line 1858 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1856 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1856 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1856 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1858 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 169:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 169: {
               //#line 1867 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1865 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1865 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1867 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 170:  AtomicStatement ::= atomic Statement
            //
            case 170: {
               //#line 1873 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1871 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1873 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 171:  WhenStatement ::= when ( Expression ) Statement
            //
            case 171: {
               //#line 1880 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1878 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1878 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1880 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 172:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 172: {
               //#line 1885 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1883 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1883 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1883 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1883 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1885 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 173:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 173: {
               //#line 1892 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1890 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1890 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1890 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1890 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1892 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 174:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 174: {
               //#line 1906 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1904 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1904 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1904 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1904 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1906 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 175:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 175: {
               //#line 1920 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1918 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1918 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1918 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1920 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 176:  FinishStatement ::= finish Statement
            //
            case 176: {
               //#line 1933 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1931 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1933 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 177:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 177: {
               //#line 1939 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1937 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1939 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 179:  NextStatement ::= next ;
            //
            case 179: {
               //#line 1947 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 1947 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 180:  AwaitStatement ::= await Expression ;
            //
            case 180: {
               //#line 1953 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1951 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1953 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 181:  ClockList ::= Clock
            //
            case 181: {
               //#line 1959 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1957 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1959 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 182:  ClockList ::= ClockList , Clock
            //
            case 182: {
               //#line 1966 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1964 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1964 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1966 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 183:  Clock ::= Expression
            //
            case 183: {
               //#line 1974 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1972 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1974 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 184:  CastExpression ::= CastExpression as Type
            //
            case 184: {
               //#line 1988 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 1986 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 1986 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1988 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 186:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 186: {
               //#line 2002 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2000 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2002 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 187:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 187: {
               //#line 2009 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2007 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 2007 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2009 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 188:  TypeParameterList ::= TypeParameter
            //
            case 188: {
               //#line 2016 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2014 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2016 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 189:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 189: {
               //#line 2023 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2021 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 2021 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2023 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 190:  TypeParamWithVariance ::= Identifier
            //
            case 190: {
               //#line 2030 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2028 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2030 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 191:  TypeParamWithVariance ::= + Identifier
            //
            case 191: {
               //#line 2035 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2033 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2035 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 192:  TypeParamWithVariance ::= - Identifier
            //
            case 192: {
               //#line 2040 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2038 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2040 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 193:  TypeParameter ::= Identifier
            //
            case 193: {
               //#line 2046 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2044 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2046 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 194:  Primary ::= here
            //
            case 194: {
               //#line 2052 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2052 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 196:  RegionExpressionList ::= RegionExpression
            //
            case 196: {
               //#line 2060 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2058 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2060 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 197:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 197: {
               //#line 2067 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2065 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2065 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2067 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 198:  Primary ::= [ ArgumentListopt ]
            //
            case 198: {
               //#line 2074 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2072 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 2074 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 199:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 199: {
               //#line 2081 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2079 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2079 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2081 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 200:  ClosureExpression ::= FormalParameters WhereClauseopt ResultTypeopt Throwsopt => ClosureBody
            //
            case 200: {
               //#line 2087 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2085 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(1);
                //#line 2085 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2085 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2085 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(4);
                //#line 2085 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2087 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 201:  LastExpression ::= Expression
            //
            case 201: {
               //#line 2094 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2092 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2094 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 202:  ClosureBody ::= CastExpression
            //
            case 202: {
               //#line 2100 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2098 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2100 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), CastExpression, true)));
                      break;
            }
    
            //
            // Rule 203:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 203: {
               //#line 2105 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2103 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2103 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2103 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2105 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 204:  ClosureBody ::= Annotationsopt Block
            //
            case 204: {
               //#line 2115 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2113 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2113 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2115 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 205:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 205: {
               //#line 2124 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2122 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2122 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2124 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 206:  AsyncExpression ::= async ClosureBody
            //
            case 206: {
               //#line 2130 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2128 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2130 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 207:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 207: {
               //#line 2135 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2133 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2133 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2135 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 208:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 208: {
               //#line 2140 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2138 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2138 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2140 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 209:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 209: {
               //#line 2145 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2143 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2143 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2143 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2145 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 210:  FutureExpression ::= future ClosureBody
            //
            case 210: {
               //#line 2151 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2149 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2151 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 211:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 211: {
               //#line 2156 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2154 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2154 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2156 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 212:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 212: {
               //#line 2161 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2159 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2159 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2161 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 213:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 213: {
               //#line 2166 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2164 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2164 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2164 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2166 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 214:  DepParametersopt ::= $Empty
            //
            case 214:
                setResult(null);
                break;

            //
            // Rule 216:  PropertyListopt ::= $Empty
            //
            case 216: {
               //#line 2177 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2177 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 218:  WhereClauseopt ::= $Empty
            //
            case 218:
                setResult(null);
                break;

            //
            // Rule 220:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 220:
                setResult(null);
                break;

            //
            // Rule 222:  ClassModifiersopt ::= $Empty
            //
            case 222: {
               //#line 2192 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2192 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 224:  TypeDefModifiersopt ::= $Empty
            //
            case 224: {
               //#line 2198 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2198 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 226:  Unsafeopt ::= $Empty
            //
            case 226:
                setResult(null);
                break;

            //
            // Rule 227:  Unsafeopt ::= unsafe
            //
            case 227: {
               //#line 2206 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2206 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                // any value distinct from null
                setResult(this);
                      break;
            }
    
            //
            // Rule 228:  ClockedClauseopt ::= $Empty
            //
            case 228: {
               //#line 2213 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2213 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 230:  identifier ::= IDENTIFIER$ident
            //
            case 230: {
               //#line 2224 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2222 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2224 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 231:  TypeName ::= Identifier
            //
            case 231: {
               //#line 2231 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2229 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2231 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 232:  TypeName ::= TypeName . Identifier
            //
            case 232: {
               //#line 2236 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2234 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2234 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2236 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 234:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 234: {
               //#line 2248 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2246 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2248 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 235:  TypeArgumentList ::= Type
            //
            case 235: {
               //#line 2255 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2253 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2255 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 236:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 236: {
               //#line 2262 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2260 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2260 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2262 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 237:  PackageName ::= Identifier
            //
            case 237: {
               //#line 2272 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2270 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2272 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 238:  PackageName ::= PackageName . Identifier
            //
            case 238: {
               //#line 2277 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2275 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2275 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2277 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 239:  ExpressionName ::= Identifier
            //
            case 239: {
               //#line 2293 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2291 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2293 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 240:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 240: {
               //#line 2298 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2296 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2296 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2298 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 241:  MethodName ::= Identifier
            //
            case 241: {
               //#line 2308 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2306 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2308 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 242:  MethodName ::= AmbiguousName . Identifier
            //
            case 242: {
               //#line 2313 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2311 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2311 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2313 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 243:  PackageOrTypeName ::= Identifier
            //
            case 243: {
               //#line 2323 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2321 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2323 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 244:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 244: {
               //#line 2328 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2326 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2326 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2328 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 245:  AmbiguousName ::= Identifier
            //
            case 245: {
               //#line 2338 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2336 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2338 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 246:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 246: {
               //#line 2343 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2341 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2341 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2343 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 247:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 247: {
               //#line 2355 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2353 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2353 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2353 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2355 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 248:  ImportDeclarations ::= ImportDeclaration
            //
            case 248: {
               //#line 2371 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2369 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2371 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 249:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 249: {
               //#line 2378 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2376 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2376 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2378 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 250:  TypeDeclarations ::= TypeDeclaration
            //
            case 250: {
               //#line 2386 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2384 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2386 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 251:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 251: {
               //#line 2394 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2392 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2392 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2394 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 252:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 252: {
               //#line 2402 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2400 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2400 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2402 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn);
                      break;
            }
    
            //
            // Rule 255:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 255: {
               //#line 2416 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2414 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2416 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 256:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 256: {
               //#line 2422 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2420 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2422 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 260:  TypeDeclaration ::= ;
            //
            case 260: {
               //#line 2437 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2437 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 261:  ClassModifiers ::= ClassModifier
            //
            case 261: {
               //#line 2445 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2443 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2445 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 262:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 262: {
               //#line 2452 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2450 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2450 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2452 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 263:  ClassModifier ::= Annotation
            //
            case 263: {
               //#line 2458 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2456 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2458 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 264:  ClassModifier ::= public
            //
            case 264: {
               //#line 2463 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2463 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 265:  ClassModifier ::= protected
            //
            case 265: {
               //#line 2468 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2468 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 266:  ClassModifier ::= private
            //
            case 266: {
               //#line 2473 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2473 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 267:  ClassModifier ::= abstract
            //
            case 267: {
               //#line 2478 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2478 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 268:  ClassModifier ::= static
            //
            case 268: {
               //#line 2483 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2483 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 269:  ClassModifier ::= final
            //
            case 269: {
               //#line 2488 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2488 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 270:  ClassModifier ::= strictfp
            //
            case 270: {
               //#line 2493 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2493 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 271:  ClassModifier ::= safe
            //
            case 271: {
               //#line 2498 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2498 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 272:  TypeDefModifiers ::= TypeDefModifier
            //
            case 272: {
               //#line 2504 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2502 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2504 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 273:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 273: {
               //#line 2511 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2509 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2509 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2511 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 274:  TypeDefModifier ::= Annotation
            //
            case 274: {
               //#line 2517 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2515 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2517 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 275:  TypeDefModifier ::= public
            //
            case 275: {
               //#line 2522 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2522 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 276:  TypeDefModifier ::= protected
            //
            case 276: {
               //#line 2527 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2527 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 277:  TypeDefModifier ::= private
            //
            case 277: {
               //#line 2532 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2532 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 278:  TypeDefModifier ::= abstract
            //
            case 278: {
               //#line 2537 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2537 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 279:  TypeDefModifier ::= static
            //
            case 279: {
               //#line 2542 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2542 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 280:  TypeDefModifier ::= final
            //
            case 280: {
               //#line 2547 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2547 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 281:  Interfaces ::= implements InterfaceTypeList
            //
            case 281: {
               //#line 2556 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2554 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2556 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 282:  InterfaceTypeList ::= Type
            //
            case 282: {
               //#line 2562 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2560 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2562 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 283:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 283: {
               //#line 2569 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2567 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2567 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2569 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 284:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 284: {
               //#line 2579 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2577 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2579 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 286:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 286: {
               //#line 2586 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2584 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2584 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2586 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 288:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 288: {
               //#line 2608 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2606 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2608 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 290: {
               //#line 2617 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2615 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2617 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 291:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 291: {
               //#line 2624 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2622 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2624 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 292:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 292: {
               //#line 2631 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2629 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2631 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 293:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 293: {
               //#line 2638 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2636 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2638 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 294:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 294: {
               //#line 2645 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2643 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2645 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  ClassMemberDeclaration ::= ;
            //
            case 295: {
               //#line 2652 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2652 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 296:  FormalDeclarators ::= FormalDeclarator
            //
            case 296: {
               //#line 2659 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2657 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2659 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 297: {
               //#line 2666 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2664 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2664 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2666 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 298:  FieldDeclarators ::= FieldDeclarator
            //
            case 298: {
               //#line 2673 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2671 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2673 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 299: {
               //#line 2680 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2678 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2678 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2680 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 300:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 300: {
               //#line 2688 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2686 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2688 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 301: {
               //#line 2695 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2693 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2693 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2695 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 302:  VariableDeclarators ::= VariableDeclarator
            //
            case 302: {
               //#line 2702 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2700 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2702 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 303:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 303: {
               //#line 2709 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2707 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2707 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2709 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 305:  FieldModifiers ::= FieldModifier
            //
            case 305: {
               //#line 2718 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2716 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2718 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 306:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 306: {
               //#line 2725 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2723 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2723 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2725 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 307:  FieldModifier ::= Annotation
            //
            case 307: {
               //#line 2731 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2729 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2731 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 308:  FieldModifier ::= public
            //
            case 308: {
               //#line 2736 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2736 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 309:  FieldModifier ::= protected
            //
            case 309: {
               //#line 2741 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2741 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 310:  FieldModifier ::= private
            //
            case 310: {
               //#line 2746 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2746 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 311:  FieldModifier ::= static
            //
            case 311: {
               //#line 2751 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2751 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 312:  FieldModifier ::= transient
            //
            case 312: {
               //#line 2756 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2756 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.TRANSIENT)));
                      break;
            }
    
            //
            // Rule 313:  FieldModifier ::= volatile
            //
            case 313: {
               //#line 2761 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2761 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.VOLATILE)));
                      break;
            }
    
            //
            // Rule 314:  FieldModifier ::= global
            //
            case 314: {
               //#line 2766 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2766 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 315:  ResultType ::= : Type
            //
            case 315: {
               //#line 2772 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2770 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2772 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 316:  FormalParameters ::= ( FormalParameterList )
            //
            case 316: {
               //#line 2778 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2776 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2778 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(FormalParameterList);
                      break;
            }
    
            //
            // Rule 317:  FormalParameterList ::= FormalParameter
            //
            case 317: {
               //#line 2784 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2782 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2784 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 318:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 318: {
               //#line 2791 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2789 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2789 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2791 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 319:  LoopIndexDeclarator ::= Identifier ResultTypeopt
            //
            case 319: {
               //#line 2797 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2795 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2795 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2797 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 320:  LoopIndexDeclarator ::= ( IdentifierList ) ResultTypeopt
            //
            case 320: {
               //#line 2802 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2800 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2800 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2802 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 321:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt
            //
            case 321: {
               //#line 2807 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2805 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2805 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2805 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2807 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 322:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 322: {
               //#line 2813 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2811 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2811 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2813 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 2836 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2834 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2834 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2834 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2836 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 2860 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2858 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2858 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2860 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 2884 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2882 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2882 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2882 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2884 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 2908 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2906 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2908 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh()), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 327:  VariableModifiers ::= VariableModifier
            //
            case 327: {
               //#line 2916 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2914 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2916 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 328:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 328: {
               //#line 2923 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2921 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2921 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2923 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 329:  VariableModifier ::= Annotation
            //
            case 329: {
               //#line 2929 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2927 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2929 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 330:  VariableModifier ::= shared
            //
            case 330: {
               //#line 2934 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2934 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 331:  MethodModifiers ::= MethodModifier
            //
            case 331: {
               //#line 2943 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2941 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2943 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 332:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 332: {
               //#line 2950 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2948 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2948 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2950 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 333:  MethodModifier ::= Annotation
            //
            case 333: {
               //#line 2956 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 2954 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2956 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 334:  MethodModifier ::= public
            //
            case 334: {
               //#line 2961 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2961 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 335:  MethodModifier ::= protected
            //
            case 335: {
               //#line 2966 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2966 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 336:  MethodModifier ::= private
            //
            case 336: {
               //#line 2971 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2971 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 337:  MethodModifier ::= abstract
            //
            case 337: {
               //#line 2976 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2976 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 338:  MethodModifier ::= static
            //
            case 338: {
               //#line 2981 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2981 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 339:  MethodModifier ::= final
            //
            case 339: {
               //#line 2986 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2986 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 340:  MethodModifier ::= native
            //
            case 340: {
               //#line 2991 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2991 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= strictfp
            //
            case 341: {
               //#line 2996 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 2996 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= atomic
            //
            case 342: {
               //#line 3001 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3001 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= extern
            //
            case 343: {
               //#line 3006 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3006 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= safe
            //
            case 344: {
               //#line 3011 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3011 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= sequential
            //
            case 345: {
               //#line 3016 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3016 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= nonblocking
            //
            case 346: {
               //#line 3021 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3021 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= incomplete
            //
            case 347: {
               //#line 3026 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3026 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 348:  MethodModifier ::= property
            //
            case 348: {
               //#line 3031 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3031 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 349:  MethodModifier ::= global
            //
            case 349: {
               //#line 3036 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3036 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 350:  MethodModifier ::= proto
            //
            case 350: {
               //#line 3041 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3041 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROTO)));
                      break;
            }
    
            //
            // Rule 351:  Throws ::= throws ExceptionTypeList
            //
            case 351: {
               //#line 3048 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3046 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3048 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 352:  ExceptionTypeList ::= ExceptionType
            //
            case 352: {
               //#line 3054 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3052 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3054 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 353:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 353: {
               //#line 3061 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3059 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3059 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3061 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 355:  MethodBody ::= = LastExpression ;
            //
            case 355: {
               //#line 3069 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3067 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3069 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 356:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 356: {
               //#line 3074 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3072 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3072 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(4);
                //#line 3072 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3074 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 357:  MethodBody ::= = Annotationsopt Block
            //
            case 357: {
               //#line 3082 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3080 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3080 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3082 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 358:  MethodBody ::= Annotationsopt Block
            //
            case 358: {
               //#line 3087 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3085 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 3085 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3087 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 359:  MethodBody ::= ;
            //
            case 359:
                setResult(null);
                break;

            //
            // Rule 360:  SimpleTypeName ::= Identifier
            //
            case 360: {
               //#line 3107 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3105 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3107 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 361:  ConstructorModifiers ::= ConstructorModifier
            //
            case 361: {
               //#line 3113 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3111 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3113 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 362:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 362: {
               //#line 3120 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3118 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3118 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3120 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 363:  ConstructorModifier ::= Annotation
            //
            case 363: {
               //#line 3126 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3124 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3126 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 364:  ConstructorModifier ::= public
            //
            case 364: {
               //#line 3131 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3131 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 365:  ConstructorModifier ::= protected
            //
            case 365: {
               //#line 3136 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3136 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 366:  ConstructorModifier ::= private
            //
            case 366: {
               //#line 3141 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3141 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 367:  ConstructorModifier ::= native
            //
            case 367: {
               //#line 3146 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3146 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 368:  ConstructorBody ::= = ConstructorBlock
            //
            case 368: {
               //#line 3152 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3150 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3152 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 369:  ConstructorBody ::= ConstructorBlock
            //
            case 369: {
               //#line 3157 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3155 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3157 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 370:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 370: {
               //#line 3162 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3160 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3162 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 371:  ConstructorBody ::= = AssignPropertyCall
            //
            case 371: {
               //#line 3170 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3168 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3170 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.SuperCall(pos(), Collections.EMPTY_LIST));
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 372:  ConstructorBody ::= ;
            //
            case 372:
                setResult(null);
                break;

            //
            // Rule 373:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 373: {
               //#line 3182 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3180 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3180 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3182 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 374:  Arguments ::= ( ArgumentListopt )
            //
            case 374: {
               //#line 3199 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3197 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3199 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 376:  InterfaceModifiers ::= InterfaceModifier
            //
            case 376: {
               //#line 3209 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3207 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3209 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 377:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 377: {
               //#line 3216 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3214 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3214 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3216 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 378:  InterfaceModifier ::= Annotation
            //
            case 378: {
               //#line 3222 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3220 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3222 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 379:  InterfaceModifier ::= public
            //
            case 379: {
               //#line 3227 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3227 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 380:  InterfaceModifier ::= protected
            //
            case 380: {
               //#line 3232 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3232 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 381:  InterfaceModifier ::= private
            //
            case 381: {
               //#line 3237 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3237 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 382:  InterfaceModifier ::= abstract
            //
            case 382: {
               //#line 3242 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3242 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 383:  InterfaceModifier ::= static
            //
            case 383: {
               //#line 3247 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3247 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 384:  InterfaceModifier ::= strictfp
            //
            case 384: {
               //#line 3252 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3252 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 385:  ExtendsInterfaces ::= extends Type
            //
            case 385: {
               //#line 3258 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3256 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3258 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 386:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 386: {
               //#line 3265 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3263 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3263 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3265 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 387:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 387: {
               //#line 3274 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3272 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3274 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 389:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 389: {
               //#line 3281 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3279 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3279 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3281 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 390:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 390: {
               //#line 3288 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3286 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3288 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 391:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 391: {
               //#line 3295 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3293 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3295 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 392:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 392: {
               //#line 3302 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3300 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3302 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 393:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 393: {
               //#line 3309 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3307 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3309 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 394:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 394: {
               //#line 3316 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3314 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3316 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 395:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 395: {
               //#line 3323 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3321 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3323 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 396:  InterfaceMemberDeclaration ::= ;
            //
            case 396: {
               //#line 3330 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3330 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 397:  Annotations ::= Annotation
            //
            case 397: {
               //#line 3336 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3334 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3336 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 398:  Annotations ::= Annotations Annotation
            //
            case 398: {
               //#line 3343 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3341 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3341 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3343 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 399:  Annotation ::= @ NamedType
            //
            case 399: {
               //#line 3349 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3347 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3349 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 400:  SimpleName ::= Identifier
            //
            case 400: {
               //#line 3355 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3353 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3355 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 401:  Identifier ::= identifier
            //
            case 401: {
               //#line 3361 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3359 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3361 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 402:  VariableInitializers ::= VariableInitializer
            //
            case 402: {
               //#line 3369 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3367 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3369 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 403:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 403: {
               //#line 3376 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3374 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3374 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3376 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 404:  Block ::= { BlockStatementsopt }
            //
            case 404: {
               //#line 3394 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3392 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3394 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 405:  BlockStatements ::= BlockStatement
            //
            case 405: {
               //#line 3400 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3398 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3400 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 406:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 406: {
               //#line 3407 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3405 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3405 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3407 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 408:  BlockStatement ::= ClassDeclaration
            //
            case 408: {
               //#line 3415 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3413 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3415 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 409:  BlockStatement ::= TypeDefDeclaration
            //
            case 409: {
               //#line 3422 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3420 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3422 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 410:  BlockStatement ::= Statement
            //
            case 410: {
               //#line 3429 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3427 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3429 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 411:  IdentifierList ::= Identifier
            //
            case 411: {
               //#line 3437 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3435 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3437 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 412:  IdentifierList ::= IdentifierList , Identifier
            //
            case 412: {
               //#line 3444 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3442 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3442 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3444 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 413:  FormalDeclarator ::= Identifier ResultType
            //
            case 413: {
               //#line 3450 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3448 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3448 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3450 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 414:  FormalDeclarator ::= ( IdentifierList ) ResultType
            //
            case 414: {
               //#line 3455 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3453 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3453 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3455 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 415:  FormalDeclarator ::= Identifier ( IdentifierList ) ResultType
            //
            case 415: {
               //#line 3460 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3458 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3458 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3458 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3460 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 416:  FieldDeclarator ::= Identifier ResultType
            //
            case 416: {
               //#line 3466 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3464 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3464 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3466 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, ResultType, null });
                      break;
            }
    
            //
            // Rule 417:  FieldDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 417: {
               //#line 3471 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3469 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3469 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3469 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3471 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 418:  VariableDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 418: {
               //#line 3477 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3475 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3475 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3475 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3477 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 419:  VariableDeclarator ::= ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 419: {
               //#line 3482 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3480 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3480 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3480 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3482 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 420:  VariableDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 420: {
               //#line 3487 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3485 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3485 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3485 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3485 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3487 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 421:  VariableDeclaratorWithType ::= Identifier ResultType = VariableInitializer
            //
            case 421: {
               //#line 3493 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3491 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3491 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3491 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3493 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 422:  VariableDeclaratorWithType ::= ( IdentifierList ) ResultType = VariableInitializer
            //
            case 422: {
               //#line 3498 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3496 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3496 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3496 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3498 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 423:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) ResultType = VariableInitializer
            //
            case 423: {
               //#line 3503 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3501 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3501 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3501 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3501 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3503 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 425:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 425: {
               //#line 3511 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3509 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3509 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3509 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3511 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
                        	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name),  Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
                        	index++;
                        }
                    }
                l.addAll(s); 
                setResult(l);
                      break;
            }
    
            //
            // Rule 426:  LocalVariableDeclaration ::= VariableModifiersopt VariableDeclaratorsWithType
            //
            case 426: {
               //#line 3544 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3542 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3542 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3544 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
                        	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name),  Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
                        	index++;
                        }
                    }
                l.addAll(s); 
                setResult(l);
                      break;
            }
    
            //
            // Rule 427:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 427: {
               //#line 3578 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3576 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3576 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3576 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3578 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
                        	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name),  Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
                        	index++;
                        }
                    }
                l.addAll(s); 
                setResult(l);
                      break;
            }
    
            //
            // Rule 429:  Primary ::= TypeName . class
            //
            case 429: {
               //#line 3619 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3617 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3619 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                if (TypeName instanceof ParsedName)
                {
                    ParsedName a = (ParsedName) TypeName;
                    setResult(nf.ClassLit(pos(), a.toType()));
                }
                else assert(false);
                      break;
            }
    
            //
            // Rule 430:  Primary ::= self
            //
            case 430: {
               //#line 3629 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3629 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 431:  Primary ::= this
            //
            case 431: {
               //#line 3634 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3634 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 432:  Primary ::= ClassName . this
            //
            case 432: {
               //#line 3639 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3637 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3639 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 433:  Primary ::= ( Expression )
            //
            case 433: {
               //#line 3644 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3642 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3644 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 439:  OperatorFunction ::= TypeName . +
            //
            case 439: {
               //#line 3655 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3653 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3655 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.ADD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 440:  OperatorFunction ::= TypeName . -
            //
            case 440: {
               //#line 3666 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3664 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3666 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SUB, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 441:  OperatorFunction ::= TypeName . *
            //
            case 441: {
               //#line 3677 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3675 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3677 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MUL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 442:  OperatorFunction ::= TypeName . /
            //
            case 442: {
               //#line 3688 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3686 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3688 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.DIV, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 443:  OperatorFunction ::= TypeName . %
            //
            case 443: {
               //#line 3699 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3697 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3699 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MOD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 444:  OperatorFunction ::= TypeName . &
            //
            case 444: {
               //#line 3710 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3708 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3710 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_AND, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 445:  OperatorFunction ::= TypeName . |
            //
            case 445: {
               //#line 3721 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3719 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3721 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_OR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 446:  OperatorFunction ::= TypeName . ^
            //
            case 446: {
               //#line 3732 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3730 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3732 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_XOR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 447:  OperatorFunction ::= TypeName . <<
            //
            case 447: {
               //#line 3743 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3741 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3743 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 448:  OperatorFunction ::= TypeName . >>
            //
            case 448: {
               //#line 3754 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3752 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3754 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 449:  OperatorFunction ::= TypeName . >>>
            //
            case 449: {
               //#line 3765 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3763 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3765 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.USHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 450:  OperatorFunction ::= TypeName . <
            //
            case 450: {
               //#line 3776 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3774 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3776 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 451:  OperatorFunction ::= TypeName . <=
            //
            case 451: {
               //#line 3787 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3785 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3787 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 452:  OperatorFunction ::= TypeName . >=
            //
            case 452: {
               //#line 3798 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3796 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3798 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 453:  OperatorFunction ::= TypeName . >
            //
            case 453: {
               //#line 3809 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3807 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3809 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 454:  OperatorFunction ::= TypeName . ==
            //
            case 454: {
               //#line 3820 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3818 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3820 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.EQ, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 455:  OperatorFunction ::= TypeName . !=
            //
            case 455: {
               //#line 3831 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3829 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3831 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.NE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 456:  Literal ::= IntegerLiteral$lit
            //
            case 456: {
               //#line 3844 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3842 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3844 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 457:  Literal ::= LongLiteral$lit
            //
            case 457: {
               //#line 3850 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3848 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3850 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 458:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 458: {
               //#line 3856 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3854 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3856 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 459:  Literal ::= UnsignedLongLiteral$lit
            //
            case 459: {
               //#line 3862 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3860 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3862 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 460:  Literal ::= FloatingPointLiteral$lit
            //
            case 460: {
               //#line 3868 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3866 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3868 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 461:  Literal ::= DoubleLiteral$lit
            //
            case 461: {
               //#line 3874 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3872 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3874 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 462:  Literal ::= BooleanLiteral
            //
            case 462: {
               //#line 3880 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3878 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3880 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 463:  Literal ::= CharacterLiteral$lit
            //
            case 463: {
               //#line 3885 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3883 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3885 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 464:  Literal ::= StringLiteral$str
            //
            case 464: {
               //#line 3891 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3889 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3891 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 465:  Literal ::= null
            //
            case 465: {
               //#line 3897 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 3897 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 466:  BooleanLiteral ::= true$trueLiteral
            //
            case 466: {
               //#line 3903 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3901 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3903 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 467:  BooleanLiteral ::= false$falseLiteral
            //
            case 467: {
               //#line 3908 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3906 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3908 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 468:  ArgumentList ::= Expression
            //
            case 468: {
               //#line 3917 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3915 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3917 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 469:  ArgumentList ::= ArgumentList , Expression
            //
            case 469: {
               //#line 3924 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3922 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3922 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3924 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 470:  FieldAccess ::= Primary . Identifier
            //
            case 470: {
               //#line 3930 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3928 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3928 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3930 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 471:  FieldAccess ::= super . Identifier
            //
            case 471: {
               //#line 3935 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3933 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3935 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 472:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 472: {
               //#line 3940 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3938 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3938 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3938 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3940 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 473:  FieldAccess ::= Primary . class$c
            //
            case 473: {
               //#line 3945 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3943 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3943 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3945 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 474:  FieldAccess ::= super . class$c
            //
            case 474: {
               //#line 3950 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3948 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3950 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 475:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 475: {
               //#line 3955 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3953 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3953 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3953 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 3955 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 476:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 476: {
               //#line 3961 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3959 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3959 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3959 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3961 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 477:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 477: {
               //#line 3968 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3966 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3966 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3966 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3966 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3968 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 478:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 478: {
               //#line 3973 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3971 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3971 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3971 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3973 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 479:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 479: {
               //#line 3978 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3976 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3976 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3976 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3976 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3976 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3978 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 480:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 480: {
               //#line 3983 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 3981 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3981 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3981 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3983 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
            // Rule 481:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 481: {
               //#line 4003 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4001 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4001 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 4003 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, Collections.EMPTY_LIST, actuals), true))));
                      break;
            }
    
            //
            // Rule 482:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 482: {
               //#line 4016 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4014 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4014 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4014 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4016 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                  List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), Primary, Identifier, Collections.EMPTY_LIST, actuals), true))));
                      break;
            }
    
            //
            // Rule 483:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 483: {
               //#line 4028 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4026 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4026 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4028 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, Collections.EMPTY_LIST, actuals), true))));
                      break;
            }
    
            //
            // Rule 484:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 484: {
               //#line 4040 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4038 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4038 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4038 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4038 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(8);
                //#line 4040 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, 
                                                          Collections.EMPTY_LIST, actuals), true))));
                      break;
            }
    
            //
            // Rule 486:  PostfixExpression ::= ExpressionName
            //
            case 486: {
               //#line 4055 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4053 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4055 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 489:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 489: {
               //#line 4063 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4061 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4063 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 490:  PostDecrementExpression ::= PostfixExpression --
            //
            case 490: {
               //#line 4069 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4067 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4069 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 493:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 493: {
               //#line 4077 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4075 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4077 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 494:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 494: {
               //#line 4082 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4080 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4082 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 496:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 496: {
               //#line 4089 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4087 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4089 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 497:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 497: {
               //#line 4095 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4093 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4095 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 499:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 499: {
               //#line 4102 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4100 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4102 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 500:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 500: {
               //#line 4107 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4105 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4105 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4107 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e);
                      break;
            }
    
            //
            // Rule 501:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 501: {
               //#line 4114 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4112 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4114 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 503:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 503: {
               //#line 4121 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4119 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4119 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4121 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 504:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 504: {
               //#line 4126 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4124 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4124 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4126 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 505:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 505: {
               //#line 4131 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4129 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4129 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4131 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 507:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 507: {
               //#line 4138 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4136 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4136 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4138 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 508:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 508: {
               //#line 4143 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4141 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4141 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4143 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 510:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 510: {
               //#line 4150 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4148 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4148 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4150 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 511:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 511: {
               //#line 4155 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4153 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4153 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4155 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 512:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 512: {
               //#line 4160 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4158 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4158 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4160 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 514:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 514: {
               //#line 4167 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4165 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4165 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4167 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 517:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 517: {
               //#line 4176 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4174 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4174 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4176 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 518:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 518: {
               //#line 4181 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4179 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4179 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4181 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 519:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 519: {
               //#line 4186 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4184 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4184 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4186 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 520:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 520: {
               //#line 4191 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4189 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4189 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4191 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 521:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 521: {
               //#line 4196 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4194 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4194 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4196 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 522:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 522: {
               //#line 4201 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4199 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4199 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4201 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 524:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 524: {
               //#line 4208 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4206 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4206 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4208 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 525:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 525: {
               //#line 4213 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4211 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4211 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4213 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 526:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 526: {
               //#line 4218 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4216 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4216 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4218 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 528:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 528: {
               //#line 4225 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4223 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4223 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4225 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 530:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 530: {
               //#line 4232 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4230 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4230 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4232 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 532:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 532: {
               //#line 4239 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4237 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4237 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4239 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 534:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 534: {
               //#line 4246 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4244 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4244 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4246 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 536:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 536: {
               //#line 4253 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4251 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4251 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4253 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 542:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 542: {
               //#line 4265 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4263 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4263 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4263 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4265 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 545:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 545: {
               //#line 4274 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4272 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4272 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4272 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4274 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 546:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 546: {
               //#line 4279 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4277 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4277 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4277 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4277 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4279 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 547:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 547: {
               //#line 4284 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4282 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4282 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4282 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4282 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4284 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 548:  LeftHandSide ::= ExpressionName
            //
            case 548: {
               //#line 4290 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4288 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4290 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 550:  AssignmentOperator ::= =
            //
            case 550: {
               //#line 4297 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4297 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 551:  AssignmentOperator ::= *=
            //
            case 551: {
               //#line 4302 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4302 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 552:  AssignmentOperator ::= /=
            //
            case 552: {
               //#line 4307 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4307 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 553:  AssignmentOperator ::= %=
            //
            case 553: {
               //#line 4312 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4312 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 554:  AssignmentOperator ::= +=
            //
            case 554: {
               //#line 4317 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4317 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 555:  AssignmentOperator ::= -=
            //
            case 555: {
               //#line 4322 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4322 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 556:  AssignmentOperator ::= <<=
            //
            case 556: {
               //#line 4327 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4327 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 557:  AssignmentOperator ::= >>=
            //
            case 557: {
               //#line 4332 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4332 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 558:  AssignmentOperator ::= >>>=
            //
            case 558: {
               //#line 4337 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4337 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= &=
            //
            case 559: {
               //#line 4342 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4342 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 560:  AssignmentOperator ::= ^=
            //
            case 560: {
               //#line 4347 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4347 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 561:  AssignmentOperator ::= |=
            //
            case 561: {
               //#line 4352 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4352 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 564:  PrefixOp ::= +
            //
            case 564: {
               //#line 4363 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4363 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 565:  PrefixOp ::= -
            //
            case 565: {
               //#line 4368 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4368 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 566:  PrefixOp ::= !
            //
            case 566: {
               //#line 4373 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4373 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 567:  PrefixOp ::= ~
            //
            case 567: {
               //#line 4378 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4378 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 568:  BinOp ::= +
            //
            case 568: {
               //#line 4384 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4384 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 569:  BinOp ::= -
            //
            case 569: {
               //#line 4389 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4389 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 570:  BinOp ::= *
            //
            case 570: {
               //#line 4394 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4394 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 571:  BinOp ::= /
            //
            case 571: {
               //#line 4399 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4399 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 572:  BinOp ::= %
            //
            case 572: {
               //#line 4404 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4404 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 573:  BinOp ::= &
            //
            case 573: {
               //#line 4409 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4409 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 574:  BinOp ::= |
            //
            case 574: {
               //#line 4414 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4414 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 575:  BinOp ::= ^
            //
            case 575: {
               //#line 4419 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4419 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 576:  BinOp ::= &&
            //
            case 576: {
               //#line 4424 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4424 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 577:  BinOp ::= ||
            //
            case 577: {
               //#line 4429 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4429 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 578:  BinOp ::= <<
            //
            case 578: {
               //#line 4434 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4434 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 579:  BinOp ::= >>
            //
            case 579: {
               //#line 4439 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4439 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 580:  BinOp ::= >>>
            //
            case 580: {
               //#line 4444 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4444 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 581:  BinOp ::= >=
            //
            case 581: {
               //#line 4449 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4449 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 582:  BinOp ::= <=
            //
            case 582: {
               //#line 4454 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4454 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 583:  BinOp ::= >
            //
            case 583: {
               //#line 4459 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4459 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 584:  BinOp ::= <
            //
            case 584: {
               //#line 4464 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4464 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 585:  BinOp ::= ==
            //
            case 585: {
               //#line 4472 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4472 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 586:  BinOp ::= !=
            //
            case 586: {
               //#line 4477 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4477 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 587:  Catchesopt ::= $Empty
            //
            case 587: {
               //#line 4486 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4486 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 4495 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                //#line 4493 "/Users/tardieu/workspace/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4495 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 591:  ForUpdateopt ::= $Empty
            //
            case 591: {
               //#line 4501 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4501 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 4512 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4512 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 597:  SwitchLabelsopt ::= $Empty
            //
            case 597: {
               //#line 4519 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4519 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 599:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 599: {
               //#line 4526 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4526 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 601:  VariableModifiersopt ::= $Empty
            //
            case 601: {
               //#line 4533 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4533 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 4544 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4544 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 607:  ExtendsInterfacesopt ::= $Empty
            //
            case 607: {
               //#line 4551 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4551 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 609:  InterfaceModifiersopt ::= $Empty
            //
            case 609: {
               //#line 4558 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4558 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 4569 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4569 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 615:  ArgumentListopt ::= $Empty
            //
            case 615: {
               //#line 4576 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4576 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 617:  BlockStatementsopt ::= $Empty
            //
            case 617: {
               //#line 4583 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4583 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
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
               //#line 4594 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4594 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 623:  FormalParameterListopt ::= $Empty
            //
            case 623: {
               //#line 4601 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4601 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 625:  Throwsopt ::= $Empty
            //
            case 625: {
               //#line 4608 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4608 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 627:  MethodModifiersopt ::= $Empty
            //
            case 627: {
               //#line 4615 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4615 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 629:  TypeModifieropt ::= $Empty
            //
            case 629: {
               //#line 4622 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4622 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 631:  FieldModifiersopt ::= $Empty
            //
            case 631: {
               //#line 4629 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4629 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 633:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 633: {
               //#line 4636 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4636 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 635:  Interfacesopt ::= $Empty
            //
            case 635: {
               //#line 4643 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4643 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 637:  Superopt ::= $Empty
            //
            case 637:
                setResult(null);
                break;

            //
            // Rule 639:  TypeParametersopt ::= $Empty
            //
            case 639: {
               //#line 4654 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4654 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 641:  FormalParametersopt ::= $Empty
            //
            case 641: {
               //#line 4661 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4661 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 643:  Annotationsopt ::= $Empty
            //
            case 643: {
               //#line 4668 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4668 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 645:  TypeDeclarationsopt ::= $Empty
            //
            case 645: {
               //#line 4675 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4675 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 647:  ImportDeclarationsopt ::= $Empty
            //
            case 647: {
               //#line 4682 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4682 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 649:  PackageDeclarationopt ::= $Empty
            //
            case 649:
                setResult(null);
                break;

            //
            // Rule 651:  ResultTypeopt ::= $Empty
            //
            case 651:
                setResult(null);
                break;

            //
            // Rule 653:  TypeArgumentsopt ::= $Empty
            //
            case 653: {
               //#line 4697 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4697 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 655:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 655: {
               //#line 4704 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4704 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 657:  Propertiesopt ::= $Empty
            //
            case 657: {
               //#line 4711 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                
                //#line 4711 "/Users/tardieu/cvs/org.eclipse.imp.lpg.metatooling/templates/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 659:  ,opt ::= $Empty
            //
            case 659:
                setResult(null);
                break;

    
            default:
                break;
        }
        return;
    }
}

