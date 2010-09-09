
//#line 18 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
//
// Licensed Material
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

import lpg.runtime.*;

//#line 28 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
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
    

    //#line 322 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
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
               //#line 8 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 6 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 18 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 16 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 28 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 26 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 38 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 36 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 48 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 46 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 58 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 56 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 68 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 66 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 74 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 78 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 85 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 92 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 98 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 109 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 107 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 117 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 115 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 120 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 892 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 890 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 890 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 890 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 890 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 890 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 890 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 892 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 904 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 902 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 902 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 902 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 902 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 902 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 904 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 917 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 915 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 917 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 19:  PropertyList ::= Property
            //
            case 19: {
               //#line 922 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 920 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 922 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 20:  PropertyList ::= PropertyList , Property
            //
            case 20: {
               //#line 929 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 927 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 927 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 929 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 21:  Property ::= Annotationsopt Identifier ResultType
            //
            case 21: {
               //#line 936 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 934 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 934 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 934 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 936 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 945 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 943 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 943 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 943 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 943 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 943 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 943 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 943 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 943 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 945 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 975 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 975 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 992 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 990 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 990 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 990 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 990 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 990 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 990 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 990 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 990 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 992 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1009 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1007 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1007 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1007 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1007 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1007 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1007 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1007 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1007 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1009 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1027 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1025 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1025 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1025 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1025 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1025 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1025 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1025 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1025 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1027 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1046 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1044 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1044 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1044 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1044 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1044 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1044 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1044 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1046 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1063 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1061 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1061 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1061 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1061 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1061 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1061 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1061 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1063 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1080 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1078 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1078 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1078 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1078 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1078 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1078 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1078 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1078 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1080 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1097 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1095 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1095 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1095 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1095 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1095 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1095 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1095 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1097 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1114 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1112 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1112 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1112 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1112 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1112 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1112 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1112 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1114 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1131 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1129 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1129 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1129 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1129 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1129 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1129 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(9);
                //#line 1129 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1131 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1149 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1147 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1147 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1147 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1147 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1147 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1147 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1147 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1147 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1149 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1164 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1162 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1162 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1162 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(4);
                //#line 1162 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 1162 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 1164 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1180 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1178 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1178 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1180 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 36:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 36: {
               //#line 1185 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1183 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1183 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1185 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 37:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 37: {
               //#line 1190 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1188 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1188 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1188 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1190 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 38:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 38: {
               //#line 1195 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1193 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1193 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1193 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1195 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 39:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 39: {
               //#line 1201 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1199 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 1199 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1199 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1199 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1199 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1199 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1199 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1201 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1222 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1220 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1220 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1220 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1220 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1222 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 41:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 41: {
               //#line 1229 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1227 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1227 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1227 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1227 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1227 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1229 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1237 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1235 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1235 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1235 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1235 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1235 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1237 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1246 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1244 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1244 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1246 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 46:  Type ::= proto ConstrainedType
            //
            case 46: {
               //#line 1255 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1253 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ConstrainedType = (TypeNode) getRhsSym(2);
                //#line 1255 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
        AddFlags tn = (AddFlags) ConstrainedType;
        tn.addFlags(X10Flags.PROTO);
        setResult(tn);
                      break;
            }
    
            //
            // Rule 47:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt => Type
            //
            case 47: {
               //#line 1263 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1261 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1261 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1261 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1261 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1261 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1263 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt));
                      break;
            }
    
            //
            // Rule 52:  AnnotatedType ::= Type Annotations
            //
            case 52: {
               //#line 1272 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1270 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1270 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1272 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn);
                      break;
            }
    
            //
            // Rule 55:  ConstrainedType ::= ( Type )
            //
            case 55: {
               //#line 1282 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1280 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1282 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 56:  PlaceType ::= PlaceExpression
            //
            case 56: {
               //#line 1288 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1286 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(1);
                //#line 1288 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(),
                                    nf.Field(pos(), nf.This(pos()), nf.Id(pos(), "loc")), Binary.EQ,
                                    PlaceExpression));
                      break;
            }
    
            //
            // Rule 57:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 57: {
               //#line 1296 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1294 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1294 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1294 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1294 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1294 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1296 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1307 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1305 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1305 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1305 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1305 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1307 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1333 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1331 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1331 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(3);
                //#line 1333 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
                      break;
            }
    
            //
            // Rule 60:  DepParameters ::= ! PlaceType
            //
            case 60: {
               //#line 1338 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1336 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1338 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 61:  DepParameters ::= !
            //
            case 61: {
               //#line 1344 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1344 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 62:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 62: {
               //#line 1350 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1348 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1348 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(4);
                //#line 1348 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(5);
                //#line 1350 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 63:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 63: {
               //#line 1356 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1354 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(3);
                //#line 1354 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(4);
                //#line 1356 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 64:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 64: {
               //#line 1364 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1362 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1364 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 65:  TypeParameters ::= [ TypeParameterList ]
            //
            case 65: {
               //#line 1370 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1368 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1370 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 66:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 66: {
               //#line 1375 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1373 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1375 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 67:  Conjunction ::= Expression
            //
            case 67: {
               //#line 1381 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1379 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1381 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 68:  Conjunction ::= Conjunction , Expression
            //
            case 68: {
               //#line 1388 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1386 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1386 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1388 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 69:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 69: {
               //#line 1394 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1392 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1392 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1394 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 70:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 70: {
               //#line 1399 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1397 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1397 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1399 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 71:  WhereClause ::= DepParameters
            //
            case 71: {
               //#line 1405 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1403 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1405 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 72:  ExistentialListopt ::= $Empty
            //
            case 72: {
               //#line 1411 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1411 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 73:  ExistentialListopt ::= ExistentialList ;
            //
            case 73: {
               //#line 1416 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1414 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1416 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 74:  ExistentialList ::= FormalParameter
            //
            case 74: {
               //#line 1422 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1420 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1422 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 75:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 75: {
               //#line 1429 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1427 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1427 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1429 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 78:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 78: {
               //#line 1440 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1438 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1438 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1438 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1438 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1438 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1438 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1438 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1438 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1440 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1456 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1454 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1454 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1454 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1454 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1454 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1454 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1454 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1456 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1470 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1468 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1468 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1468 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1468 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1468 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1468 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1468 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(9);
                //#line 1470 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1486 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1484 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1486 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 82:  FieldKeyword ::= val
            //
            case 82: {
               //#line 1492 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1492 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 83:  FieldKeyword ::= var
            //
            case 83: {
               //#line 1497 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1497 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 84:  FieldKeyword ::= const
            //
            case 84: {
               //#line 1502 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1502 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 85:  VarKeyword ::= val
            //
            case 85: {
               //#line 1510 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1510 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 86:  VarKeyword ::= var
            //
            case 86: {
               //#line 1515 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1515 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 87:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 87: {
               //#line 1522 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1520 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1520 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1520 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1522 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1546 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1544 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1544 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1546 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 118:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 118: {
               //#line 1605 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1603 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1603 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1605 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 119:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 119: {
               //#line 1611 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1609 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1609 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1609 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1611 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 120:  EmptyStatement ::= ;
            //
            case 120: {
               //#line 1617 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1617 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 121:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 121: {
               //#line 1623 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1621 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1621 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 1623 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 127:  ExpressionStatement ::= StatementExpression ;
            //
            case 127: {
               //#line 1635 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1633 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1635 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1666 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1664 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1666 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 136:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 136: {
               //#line 1671 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1669 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1669 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1671 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 137:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 137: {
               //#line 1677 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1675 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1675 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1677 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 138:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 138: {
               //#line 1683 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1681 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1681 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1683 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 140:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 140: {
               //#line 1691 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1689 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1689 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1691 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 141:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 141: {
               //#line 1698 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1696 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1696 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1698 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1707 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1705 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1707 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 143:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 143: {
               //#line 1714 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1712 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1712 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1714 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 144:  SwitchLabel ::= case ConstantExpression :
            //
            case 144: {
               //#line 1721 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1719 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1721 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 145:  SwitchLabel ::= default :
            //
            case 145: {
               //#line 1726 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1726 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 146:  WhileStatement ::= while ( Expression ) Statement
            //
            case 146: {
               //#line 1732 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1730 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1730 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1732 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 147:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 147: {
               //#line 1738 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1736 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1736 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1738 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 150:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 150: {
               //#line 1747 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1745 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1745 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1745 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1745 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1747 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 152:  ForInit ::= LocalVariableDeclaration
            //
            case 152: {
               //#line 1754 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1752 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1754 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 154:  StatementExpressionList ::= StatementExpression
            //
            case 154: {
               //#line 1764 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1762 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1764 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 155:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 155: {
               //#line 1771 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1769 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1769 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1771 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 156:  BreakStatement ::= break Identifieropt ;
            //
            case 156: {
               //#line 1777 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1775 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1777 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 157:  ContinueStatement ::= continue Identifieropt ;
            //
            case 157: {
               //#line 1783 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1781 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1783 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 158:  ReturnStatement ::= return Expressionopt ;
            //
            case 158: {
               //#line 1789 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1787 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1789 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 159:  ThrowStatement ::= throw Expression ;
            //
            case 159: {
               //#line 1795 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1793 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1795 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 160:  TryStatement ::= try Block Catches
            //
            case 160: {
               //#line 1801 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1799 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1799 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1801 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 161:  TryStatement ::= try Block Catchesopt Finally
            //
            case 161: {
               //#line 1806 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1804 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1804 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1804 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1806 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 162:  Catches ::= CatchClause
            //
            case 162: {
               //#line 1812 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1810 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1812 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 163:  Catches ::= Catches CatchClause
            //
            case 163: {
               //#line 1819 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1817 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1817 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1819 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 164:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 164: {
               //#line 1826 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1824 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1824 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1826 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 165:  Finally ::= finally Block
            //
            case 165: {
               //#line 1832 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1830 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1832 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 166:  NowStatement ::= now ( Clock ) Statement
            //
            case 166: {
               //#line 1838 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1836 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1836 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1838 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Now(pos(), Clock, Statement));
                      break;
            }
    
            //
            // Rule 167:  ClockedClause ::= clocked ( ClockList )
            //
            case 167: {
               //#line 1844 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1842 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1844 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 168:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 168: {
               //#line 1850 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1848 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1848 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1848 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1850 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1859 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1857 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1857 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1859 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 170:  AtomicStatement ::= atomic Statement
            //
            case 170: {
               //#line 1865 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1863 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1865 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 171:  WhenStatement ::= when ( Expression ) Statement
            //
            case 171: {
               //#line 1872 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1870 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1870 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1872 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 172:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 172: {
               //#line 1877 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1875 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1875 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1875 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1875 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1877 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 173:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 173: {
               //#line 1884 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1882 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1882 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1882 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1882 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1884 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1898 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1896 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1896 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1896 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1896 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1898 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1912 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1910 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1910 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1910 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1912 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1925 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1923 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1925 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 177:  AnnotationStatement ::= Annotations Statement
            //
            case 177: {
               //#line 1932 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1930 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 1930 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1932 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                if (Statement.ext() instanceof X10Ext && Annotations instanceof List) {
                    Statement = (Stmt) ((X10Ext) Statement.ext()).annotations((List) Annotations);
                }
                setResult(Statement);
                      break;
            }
    
            //
            // Rule 178:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 178: {
               //#line 1941 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1939 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1941 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 180:  NextStatement ::= next ;
            //
            case 180: {
               //#line 1949 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1949 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 181:  AwaitStatement ::= await Expression ;
            //
            case 181: {
               //#line 1955 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1953 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1955 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 182:  ClockList ::= Clock
            //
            case 182: {
               //#line 1961 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1959 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1961 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 183:  ClockList ::= ClockList , Clock
            //
            case 183: {
               //#line 1968 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1966 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1966 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1968 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 184:  Clock ::= Expression
            //
            case 184: {
               //#line 1976 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1974 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1976 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 185:  CastExpression ::= CastExpression as Type
            //
            case 185: {
               //#line 1990 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1988 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 1988 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1990 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 186:  CastExpression ::= ConditionalExpression ! Expression
            //
            case 186: {
               //#line 1995 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1993 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1993 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1995 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.PlaceCast(pos(), Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 188:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 188: {
               //#line 2004 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2002 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2004 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 189:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 189: {
               //#line 2011 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2009 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 2009 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2011 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 190:  TypeParameterList ::= TypeParameter
            //
            case 190: {
               //#line 2018 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2016 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2018 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 191:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 191: {
               //#line 2025 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2023 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 2023 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2025 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 192:  TypeParamWithVariance ::= Identifier
            //
            case 192: {
               //#line 2032 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2030 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2032 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 193:  TypeParamWithVariance ::= + Identifier
            //
            case 193: {
               //#line 2037 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2035 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2037 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 194:  TypeParamWithVariance ::= - Identifier
            //
            case 194: {
               //#line 2042 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2040 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2042 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 195:  TypeParameter ::= Identifier
            //
            case 195: {
               //#line 2048 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2046 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2048 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 196:  Primary ::= here
            //
            case 196: {
               //#line 2054 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2054 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 198:  RegionExpressionList ::= RegionExpression
            //
            case 198: {
               //#line 2062 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2060 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2062 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 199:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 199: {
               //#line 2069 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2067 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2067 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2069 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 200:  Primary ::= [ ArgumentListopt ]
            //
            case 200: {
               //#line 2076 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2074 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 2076 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 201:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 201: {
               //#line 2083 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2081 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2081 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2083 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 202:  ClosureExpression ::= FormalParameters WhereClauseopt ResultTypeopt Throwsopt => ClosureBody
            //
            case 202: {
               //#line 2089 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2087 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(1);
                //#line 2087 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2087 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2087 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(4);
                //#line 2087 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2089 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 203:  LastExpression ::= Expression
            //
            case 203: {
               //#line 2096 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2094 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2096 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 204:  ClosureBody ::= CastExpression
            //
            case 204: {
               //#line 2102 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2100 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2102 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), CastExpression, true)));
                      break;
            }
    
            //
            // Rule 205:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 205: {
               //#line 2107 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2105 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2105 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2105 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2107 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 206:  ClosureBody ::= Annotationsopt Block
            //
            case 206: {
               //#line 2117 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2115 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2115 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2117 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 207:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 207: {
               //#line 2126 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2124 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2124 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2126 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 208:  AsyncExpression ::= async ClosureBody
            //
            case 208: {
               //#line 2132 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2130 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2132 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 209:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 209: {
               //#line 2137 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2135 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2135 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2137 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 210:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 210: {
               //#line 2142 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2140 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2140 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2142 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 211:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 211: {
               //#line 2147 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2145 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2145 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2145 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2147 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 212:  FutureExpression ::= future ClosureBody
            //
            case 212: {
               //#line 2153 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2151 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2153 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 213:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 213: {
               //#line 2158 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2156 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2156 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2158 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 214:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 214: {
               //#line 2163 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2161 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2161 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2163 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 215:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 215: {
               //#line 2168 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2166 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2166 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2166 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2168 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 216:  DepParametersopt ::= $Empty
            //
            case 216:
                setResult(null);
                break;

            //
            // Rule 218:  PropertyListopt ::= $Empty
            //
            case 218: {
               //#line 2179 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2179 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 220:  WhereClauseopt ::= $Empty
            //
            case 220:
                setResult(null);
                break;

            //
            // Rule 222:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 222:
                setResult(null);
                break;

            //
            // Rule 224:  ClassModifiersopt ::= $Empty
            //
            case 224: {
               //#line 2194 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2194 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 226:  TypeDefModifiersopt ::= $Empty
            //
            case 226: {
               //#line 2200 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2200 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 228:  Unsafeopt ::= $Empty
            //
            case 228:
                setResult(null);
                break;

            //
            // Rule 229:  Unsafeopt ::= unsafe
            //
            case 229: {
               //#line 2208 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2208 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                // any value distinct from null
                setResult(this);
                      break;
            }
    
            //
            // Rule 230:  ClockedClauseopt ::= $Empty
            //
            case 230: {
               //#line 2215 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2215 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 232:  identifier ::= IDENTIFIER$ident
            //
            case 232: {
               //#line 2226 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2224 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2226 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 233:  TypeName ::= Identifier
            //
            case 233: {
               //#line 2233 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2231 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2233 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 234:  TypeName ::= TypeName . Identifier
            //
            case 234: {
               //#line 2238 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2236 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2236 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2238 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 236:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 236: {
               //#line 2250 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2248 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2250 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 237:  TypeArgumentList ::= Type
            //
            case 237: {
               //#line 2257 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2255 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2257 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 238:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 238: {
               //#line 2264 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2262 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2262 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2264 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 239:  PackageName ::= Identifier
            //
            case 239: {
               //#line 2274 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2272 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2274 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 240:  PackageName ::= PackageName . Identifier
            //
            case 240: {
               //#line 2279 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2277 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2277 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2279 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 241:  ExpressionName ::= Identifier
            //
            case 241: {
               //#line 2295 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2293 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2295 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 242:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 242: {
               //#line 2300 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2298 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2298 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2300 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 243:  MethodName ::= Identifier
            //
            case 243: {
               //#line 2310 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2308 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2310 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 244:  MethodName ::= AmbiguousName . Identifier
            //
            case 244: {
               //#line 2315 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2313 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2313 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2315 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 245:  PackageOrTypeName ::= Identifier
            //
            case 245: {
               //#line 2325 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2323 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2325 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 246:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 246: {
               //#line 2330 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2328 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2328 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2330 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 247:  AmbiguousName ::= Identifier
            //
            case 247: {
               //#line 2340 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2338 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2340 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 248:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 248: {
               //#line 2345 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2343 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2343 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2345 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 249:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 249: {
               //#line 2357 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2355 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2355 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2355 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2357 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 250:  ImportDeclarations ::= ImportDeclaration
            //
            case 250: {
               //#line 2373 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2371 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2373 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 251:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 251: {
               //#line 2380 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2378 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2378 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2380 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 252:  TypeDeclarations ::= TypeDeclaration
            //
            case 252: {
               //#line 2388 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2386 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2388 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 253:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 253: {
               //#line 2396 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2394 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2394 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2396 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 254:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 254: {
               //#line 2404 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2402 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2402 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2404 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn);
                      break;
            }
    
            //
            // Rule 257:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 257: {
               //#line 2418 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2416 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2418 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 258:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 258: {
               //#line 2424 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2422 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2424 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 262:  TypeDeclaration ::= ;
            //
            case 262: {
               //#line 2439 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2439 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 263:  ClassModifiers ::= ClassModifier
            //
            case 263: {
               //#line 2447 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2445 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2447 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 264:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 264: {
               //#line 2454 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2452 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2452 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2454 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 265:  ClassModifier ::= Annotation
            //
            case 265: {
               //#line 2460 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2458 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2460 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 266:  ClassModifier ::= public
            //
            case 266: {
               //#line 2465 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2465 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 267:  ClassModifier ::= protected
            //
            case 267: {
               //#line 2470 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2470 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 268:  ClassModifier ::= private
            //
            case 268: {
               //#line 2475 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2475 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 269:  ClassModifier ::= abstract
            //
            case 269: {
               //#line 2480 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2480 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 270:  ClassModifier ::= static
            //
            case 270: {
               //#line 2485 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2485 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 271:  ClassModifier ::= final
            //
            case 271: {
               //#line 2490 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2490 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 272:  ClassModifier ::= strictfp
            //
            case 272: {
               //#line 2495 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2495 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 273:  ClassModifier ::= safe
            //
            case 273: {
               //#line 2500 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2500 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 274:  TypeDefModifiers ::= TypeDefModifier
            //
            case 274: {
               //#line 2506 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2504 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2506 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 275:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 275: {
               //#line 2513 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2511 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2511 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2513 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 276:  TypeDefModifier ::= Annotation
            //
            case 276: {
               //#line 2519 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2517 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2519 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 277:  TypeDefModifier ::= public
            //
            case 277: {
               //#line 2524 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2524 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 278:  TypeDefModifier ::= protected
            //
            case 278: {
               //#line 2529 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2529 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 279:  TypeDefModifier ::= private
            //
            case 279: {
               //#line 2534 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2534 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 280:  TypeDefModifier ::= abstract
            //
            case 280: {
               //#line 2539 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2539 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 281:  TypeDefModifier ::= static
            //
            case 281: {
               //#line 2544 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2544 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 282:  TypeDefModifier ::= final
            //
            case 282: {
               //#line 2549 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2549 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 283:  Interfaces ::= implements InterfaceTypeList
            //
            case 283: {
               //#line 2558 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2556 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2558 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 284:  InterfaceTypeList ::= Type
            //
            case 284: {
               //#line 2564 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2562 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2564 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 285:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 285: {
               //#line 2571 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2569 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2569 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2571 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 286:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 286: {
               //#line 2581 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2579 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2581 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 288:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 288: {
               //#line 2588 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2586 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2586 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2588 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 290:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 290: {
               //#line 2610 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2608 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2610 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 292:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 292: {
               //#line 2619 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2617 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2619 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 293:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 293: {
               //#line 2626 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2624 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2626 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 294:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 294: {
               //#line 2633 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2631 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2633 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 295: {
               //#line 2640 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2638 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2640 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 296:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 296: {
               //#line 2647 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2645 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2647 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  ClassMemberDeclaration ::= ;
            //
            case 297: {
               //#line 2654 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2654 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 298:  FormalDeclarators ::= FormalDeclarator
            //
            case 298: {
               //#line 2661 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2659 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2661 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 299: {
               //#line 2668 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2666 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2666 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2668 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 300:  FieldDeclarators ::= FieldDeclarator
            //
            case 300: {
               //#line 2675 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2673 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2675 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 301: {
               //#line 2682 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2680 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2680 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2682 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 302:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 302: {
               //#line 2690 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2688 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2690 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 303:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 303: {
               //#line 2697 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2695 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2695 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2697 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 304:  VariableDeclarators ::= VariableDeclarator
            //
            case 304: {
               //#line 2704 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2702 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2704 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 305:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 305: {
               //#line 2711 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2709 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2709 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2711 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 307:  FieldModifiers ::= FieldModifier
            //
            case 307: {
               //#line 2720 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2718 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2720 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 308:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 308: {
               //#line 2727 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2725 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2725 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2727 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 309:  FieldModifier ::= Annotation
            //
            case 309: {
               //#line 2733 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2731 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2733 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 310:  FieldModifier ::= public
            //
            case 310: {
               //#line 2738 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2738 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 311:  FieldModifier ::= protected
            //
            case 311: {
               //#line 2743 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2743 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 312:  FieldModifier ::= private
            //
            case 312: {
               //#line 2748 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2748 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 313:  FieldModifier ::= static
            //
            case 313: {
               //#line 2753 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2753 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 314:  FieldModifier ::= transient
            //
            case 314: {
               //#line 2758 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2758 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.TRANSIENT)));
                      break;
            }
    
            //
            // Rule 315:  FieldModifier ::= volatile
            //
            case 315: {
               //#line 2763 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2763 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.VOLATILE)));
                      break;
            }
    
            //
            // Rule 316:  FieldModifier ::= global
            //
            case 316: {
               //#line 2768 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2768 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 317:  ResultType ::= : Type
            //
            case 317: {
               //#line 2774 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2772 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2774 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 318:  FormalParameters ::= ( FormalParameterList )
            //
            case 318: {
               //#line 2780 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2778 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2780 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterList);
                      break;
            }
    
            //
            // Rule 319:  FormalParameterList ::= FormalParameter
            //
            case 319: {
               //#line 2786 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2784 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2786 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 320:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 320: {
               //#line 2793 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2791 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2791 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2793 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 321:  LoopIndexDeclarator ::= Identifier ResultTypeopt
            //
            case 321: {
               //#line 2799 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2797 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2797 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2799 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 322:  LoopIndexDeclarator ::= ( IdentifierList ) ResultTypeopt
            //
            case 322: {
               //#line 2804 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2802 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2802 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2804 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 323:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt
            //
            case 323: {
               //#line 2809 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2807 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2807 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2807 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2809 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 324:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 324: {
               //#line 2815 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2813 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2813 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2815 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 325:  LoopIndex ::= VariableModifiersopt VarKeyword LoopIndexDeclarator
            //
            case 325: {
               //#line 2838 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2836 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2836 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2836 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2838 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 326:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 326: {
               //#line 2862 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2860 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2860 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2862 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 327:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 327: {
               //#line 2886 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2884 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2884 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2884 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2886 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 328:  FormalParameter ::= Type
            //
            case 328: {
               //#line 2910 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2908 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2910 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh()), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 329:  VariableModifiers ::= VariableModifier
            //
            case 329: {
               //#line 2918 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2916 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2918 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 330:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 330: {
               //#line 2925 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2923 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2923 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2925 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 331:  VariableModifier ::= Annotation
            //
            case 331: {
               //#line 2931 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2929 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2931 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 332:  VariableModifier ::= shared
            //
            case 332: {
               //#line 2936 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2936 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 333:  MethodModifiers ::= MethodModifier
            //
            case 333: {
               //#line 2945 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2943 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2945 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 334:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 334: {
               //#line 2952 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2950 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2950 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2952 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 335:  MethodModifier ::= Annotation
            //
            case 335: {
               //#line 2958 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2956 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2958 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 336:  MethodModifier ::= public
            //
            case 336: {
               //#line 2963 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2963 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 337:  MethodModifier ::= protected
            //
            case 337: {
               //#line 2968 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2968 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 338:  MethodModifier ::= private
            //
            case 338: {
               //#line 2973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 339:  MethodModifier ::= abstract
            //
            case 339: {
               //#line 2978 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2978 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 340:  MethodModifier ::= static
            //
            case 340: {
               //#line 2983 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2983 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= final
            //
            case 341: {
               //#line 2988 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2988 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= native
            //
            case 342: {
               //#line 2993 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2993 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= strictfp
            //
            case 343: {
               //#line 2998 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2998 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= atomic
            //
            case 344: {
               //#line 3003 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3003 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= extern
            //
            case 345: {
               //#line 3008 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3008 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= safe
            //
            case 346: {
               //#line 3013 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3013 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= sequential
            //
            case 347: {
               //#line 3018 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3018 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 348:  MethodModifier ::= nonblocking
            //
            case 348: {
               //#line 3023 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3023 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 349:  MethodModifier ::= incomplete
            //
            case 349: {
               //#line 3028 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3028 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 350:  MethodModifier ::= property
            //
            case 350: {
               //#line 3033 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3033 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 351:  MethodModifier ::= global
            //
            case 351: {
               //#line 3038 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3038 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 352:  MethodModifier ::= proto
            //
            case 352: {
               //#line 3043 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3043 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROTO)));
                      break;
            }
    
            //
            // Rule 353:  Throws ::= throws ExceptionTypeList
            //
            case 353: {
               //#line 3050 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3048 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3050 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 354:  ExceptionTypeList ::= ExceptionType
            //
            case 354: {
               //#line 3056 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3054 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3056 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 355:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 355: {
               //#line 3063 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3061 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3061 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3063 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 357:  MethodBody ::= = LastExpression ;
            //
            case 357: {
               //#line 3071 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3069 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3071 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 358:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 358: {
               //#line 3076 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3074 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3074 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(4);
                //#line 3074 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3076 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 359:  MethodBody ::= = Annotationsopt Block
            //
            case 359: {
               //#line 3084 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3082 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3082 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3084 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 360:  MethodBody ::= Annotationsopt Block
            //
            case 360: {
               //#line 3089 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3087 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 3087 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3089 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 361:  MethodBody ::= ;
            //
            case 361:
                setResult(null);
                break;

            //
            // Rule 362:  SimpleTypeName ::= Identifier
            //
            case 362: {
               //#line 3109 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3107 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3109 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 363:  ConstructorModifiers ::= ConstructorModifier
            //
            case 363: {
               //#line 3115 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3113 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3115 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 364:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 364: {
               //#line 3122 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3120 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3120 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3122 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 365:  ConstructorModifier ::= Annotation
            //
            case 365: {
               //#line 3128 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3126 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3128 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 366:  ConstructorModifier ::= public
            //
            case 366: {
               //#line 3133 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3133 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 367:  ConstructorModifier ::= protected
            //
            case 367: {
               //#line 3138 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3138 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 368:  ConstructorModifier ::= private
            //
            case 368: {
               //#line 3143 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3143 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 369:  ConstructorModifier ::= native
            //
            case 369: {
               //#line 3148 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3148 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 370:  ConstructorBody ::= = ConstructorBlock
            //
            case 370: {
               //#line 3154 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3152 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3154 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 371:  ConstructorBody ::= ConstructorBlock
            //
            case 371: {
               //#line 3159 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3157 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3159 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 372:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 372: {
               //#line 3164 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3162 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3164 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3172 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3170 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3172 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3184 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3182 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3182 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3184 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3201 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3199 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3201 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 378:  InterfaceModifiers ::= InterfaceModifier
            //
            case 378: {
               //#line 3211 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3209 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3211 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 379:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 379: {
               //#line 3218 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3216 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3216 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3218 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 380:  InterfaceModifier ::= Annotation
            //
            case 380: {
               //#line 3224 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3222 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3224 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 381:  InterfaceModifier ::= public
            //
            case 381: {
               //#line 3229 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3229 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 382:  InterfaceModifier ::= protected
            //
            case 382: {
               //#line 3234 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3234 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 383:  InterfaceModifier ::= private
            //
            case 383: {
               //#line 3239 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3239 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 384:  InterfaceModifier ::= abstract
            //
            case 384: {
               //#line 3244 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3244 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 385:  InterfaceModifier ::= static
            //
            case 385: {
               //#line 3249 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3249 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 386:  InterfaceModifier ::= strictfp
            //
            case 386: {
               //#line 3254 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3254 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 387:  ExtendsInterfaces ::= extends Type
            //
            case 387: {
               //#line 3260 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3258 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3260 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 388:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 388: {
               //#line 3267 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3265 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3265 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3267 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 389:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 389: {
               //#line 3276 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3274 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3276 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 391:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 391: {
               //#line 3283 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3281 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3281 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3283 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 392:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 392: {
               //#line 3290 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3288 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3290 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 393:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 393: {
               //#line 3297 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3295 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3297 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 394:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 394: {
               //#line 3304 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3302 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3304 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 395:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 395: {
               //#line 3311 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3309 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3311 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 396:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 396: {
               //#line 3318 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3316 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3318 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 397:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 397: {
               //#line 3325 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3323 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3325 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 398:  InterfaceMemberDeclaration ::= ;
            //
            case 398: {
               //#line 3332 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3332 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 399:  Annotations ::= Annotation
            //
            case 399: {
               //#line 3338 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3336 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3338 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 400:  Annotations ::= Annotations Annotation
            //
            case 400: {
               //#line 3345 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3343 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3343 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3345 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 401:  Annotation ::= @ NamedType
            //
            case 401: {
               //#line 3351 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3349 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3351 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 402:  SimpleName ::= Identifier
            //
            case 402: {
               //#line 3357 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3355 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3357 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 403:  Identifier ::= identifier
            //
            case 403: {
               //#line 3363 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3361 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3363 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 404:  VariableInitializers ::= VariableInitializer
            //
            case 404: {
               //#line 3371 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3369 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3371 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 405:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 405: {
               //#line 3378 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3376 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3376 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3378 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 406:  Block ::= { BlockStatementsopt }
            //
            case 406: {
               //#line 3396 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3394 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3396 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 407:  BlockStatements ::= BlockStatement
            //
            case 407: {
               //#line 3402 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3400 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3402 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 408:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 408: {
               //#line 3409 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3407 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3407 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3409 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 410:  BlockStatement ::= ClassDeclaration
            //
            case 410: {
               //#line 3417 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3415 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3417 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 411:  BlockStatement ::= TypeDefDeclaration
            //
            case 411: {
               //#line 3424 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3422 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3424 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 412:  BlockStatement ::= Statement
            //
            case 412: {
               //#line 3431 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3429 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3431 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 413:  IdentifierList ::= Identifier
            //
            case 413: {
               //#line 3439 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3437 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3439 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 414:  IdentifierList ::= IdentifierList , Identifier
            //
            case 414: {
               //#line 3446 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3444 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3444 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3446 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 415:  FormalDeclarator ::= Identifier ResultType
            //
            case 415: {
               //#line 3452 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3450 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3450 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3452 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 416:  FormalDeclarator ::= ( IdentifierList ) ResultType
            //
            case 416: {
               //#line 3457 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3455 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3455 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3457 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 417:  FormalDeclarator ::= Identifier ( IdentifierList ) ResultType
            //
            case 417: {
               //#line 3462 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3460 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3460 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3460 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3462 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 418:  FieldDeclarator ::= Identifier ResultType
            //
            case 418: {
               //#line 3468 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3466 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3466 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3468 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, ResultType, null });
                      break;
            }
    
            //
            // Rule 419:  FieldDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 419: {
               //#line 3473 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3471 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3471 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3471 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3473 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 420:  VariableDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 420: {
               //#line 3479 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3477 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3477 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3477 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3479 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 421:  VariableDeclarator ::= ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 421: {
               //#line 3484 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3482 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3482 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3482 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3484 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 422:  VariableDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 422: {
               //#line 3489 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3487 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3487 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3487 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3487 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3489 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 423:  VariableDeclaratorWithType ::= Identifier ResultType = VariableInitializer
            //
            case 423: {
               //#line 3495 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3493 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3493 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3493 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3495 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 424:  VariableDeclaratorWithType ::= ( IdentifierList ) ResultType = VariableInitializer
            //
            case 424: {
               //#line 3500 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3498 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3498 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3498 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3500 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 425:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) ResultType = VariableInitializer
            //
            case 425: {
               //#line 3505 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3503 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3503 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3503 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3503 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3505 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 427:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 427: {
               //#line 3513 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3511 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3511 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3511 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3513 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
                        LocalDecl ld = nf.LocalDecl(i.hasNext() ? pos : new Position(fn.position(), pos), fn,
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
            // Rule 428:  LocalVariableDeclaration ::= VariableModifiersopt VariableDeclaratorsWithType
            //
            case 428: {
               //#line 3546 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3544 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3544 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3546 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
                        LocalDecl ld = nf.LocalDecl(i.hasNext() ? pos : new Position(fn.position(), pos), fn,
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
            // Rule 429:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 429: {
               //#line 3580 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3578 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3578 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3578 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3580 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 431:  Primary ::= TypeName . class
            //
            case 431: {
               //#line 3621 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3619 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3621 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3631 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3631 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 433:  Primary ::= this
            //
            case 433: {
               //#line 3636 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3636 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 434:  Primary ::= ClassName . this
            //
            case 434: {
               //#line 3641 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3639 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3641 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 435:  Primary ::= ( Expression )
            //
            case 435: {
               //#line 3646 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3644 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3646 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 441:  OperatorFunction ::= TypeName . +
            //
            case 441: {
               //#line 3657 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3655 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3657 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 442:  OperatorFunction ::= TypeName . -
            //
            case 442: {
               //#line 3668 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3666 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3668 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 443:  OperatorFunction ::= TypeName . *
            //
            case 443: {
               //#line 3679 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3677 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3679 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 444:  OperatorFunction ::= TypeName . /
            //
            case 444: {
               //#line 3690 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3688 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3690 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 445:  OperatorFunction ::= TypeName . %
            //
            case 445: {
               //#line 3701 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3699 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3701 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 446:  OperatorFunction ::= TypeName . &
            //
            case 446: {
               //#line 3712 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3710 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3712 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 447:  OperatorFunction ::= TypeName . |
            //
            case 447: {
               //#line 3723 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3721 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3723 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 448:  OperatorFunction ::= TypeName . ^
            //
            case 448: {
               //#line 3734 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3732 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3734 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 449:  OperatorFunction ::= TypeName . <<
            //
            case 449: {
               //#line 3745 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3743 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3745 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 450:  OperatorFunction ::= TypeName . >>
            //
            case 450: {
               //#line 3756 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3754 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3756 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 451:  OperatorFunction ::= TypeName . >>>
            //
            case 451: {
               //#line 3767 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3765 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3767 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 452:  OperatorFunction ::= TypeName . <
            //
            case 452: {
               //#line 3778 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3776 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3778 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 453:  OperatorFunction ::= TypeName . <=
            //
            case 453: {
               //#line 3789 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3787 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3789 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 454:  OperatorFunction ::= TypeName . >=
            //
            case 454: {
               //#line 3800 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3798 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3800 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 455:  OperatorFunction ::= TypeName . >
            //
            case 455: {
               //#line 3811 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3809 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3811 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 456:  OperatorFunction ::= TypeName . ==
            //
            case 456: {
               //#line 3822 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3820 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3822 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 457:  OperatorFunction ::= TypeName . !=
            //
            case 457: {
               //#line 3833 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3831 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3833 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 458:  Literal ::= IntegerLiteral$lit
            //
            case 458: {
               //#line 3846 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3844 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3846 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 459:  Literal ::= LongLiteral$lit
            //
            case 459: {
               //#line 3852 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3850 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3852 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 460:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 460: {
               //#line 3858 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3856 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3858 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 461:  Literal ::= UnsignedLongLiteral$lit
            //
            case 461: {
               //#line 3864 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3862 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3864 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 462:  Literal ::= FloatingPointLiteral$lit
            //
            case 462: {
               //#line 3870 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3868 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3870 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 463:  Literal ::= DoubleLiteral$lit
            //
            case 463: {
               //#line 3876 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3874 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3876 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 464:  Literal ::= BooleanLiteral
            //
            case 464: {
               //#line 3882 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3880 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3882 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 465:  Literal ::= CharacterLiteral$lit
            //
            case 465: {
               //#line 3887 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3885 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3887 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 466:  Literal ::= StringLiteral$str
            //
            case 466: {
               //#line 3893 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3891 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3893 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 467:  Literal ::= null
            //
            case 467: {
               //#line 3899 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3899 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 468:  BooleanLiteral ::= true$trueLiteral
            //
            case 468: {
               //#line 3905 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3903 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3905 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 469:  BooleanLiteral ::= false$falseLiteral
            //
            case 469: {
               //#line 3910 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3908 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3910 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 470:  ArgumentList ::= Expression
            //
            case 470: {
               //#line 3919 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3917 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3919 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 471:  ArgumentList ::= ArgumentList , Expression
            //
            case 471: {
               //#line 3926 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3924 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3924 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3926 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 472:  FieldAccess ::= Primary . Identifier
            //
            case 472: {
               //#line 3932 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3930 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3930 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3932 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 473:  FieldAccess ::= super . Identifier
            //
            case 473: {
               //#line 3937 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3935 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3937 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 474:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 474: {
               //#line 3942 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3940 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3940 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3940 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3942 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 475:  FieldAccess ::= Primary . class$c
            //
            case 475: {
               //#line 3947 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3945 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3945 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3947 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 476:  FieldAccess ::= super . class$c
            //
            case 476: {
               //#line 3952 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3950 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3952 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 477:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 477: {
               //#line 3957 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3955 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3955 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3955 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 3957 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 478:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 478: {
               //#line 3963 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3961 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3961 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3961 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3963 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 479:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 479: {
               //#line 3970 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3968 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3968 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3968 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3968 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3970 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 480:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 480: {
               //#line 3975 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3973 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3975 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 481:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 481: {
               //#line 3980 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3978 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3978 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3978 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3978 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3978 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3980 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 482:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 482: {
               //#line 3985 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3983 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3983 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3983 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3985 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 483:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 483: {
               //#line 4005 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4003 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4003 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 4005 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 484:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 484: {
               //#line 4018 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4016 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4016 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4016 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4018 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 485:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 485: {
               //#line 4030 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4028 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4028 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4030 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 486:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 486: {
               //#line 4042 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4040 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4040 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4040 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4040 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(8);
                //#line 4042 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 488:  PostfixExpression ::= ExpressionName
            //
            case 488: {
               //#line 4057 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4055 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4057 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 491:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 491: {
               //#line 4065 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4063 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4065 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 492:  PostDecrementExpression ::= PostfixExpression --
            //
            case 492: {
               //#line 4071 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4069 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4071 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 495:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 495: {
               //#line 4079 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4077 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4079 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 496:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 496: {
               //#line 4084 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4082 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4084 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 498:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 498: {
               //#line 4091 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4089 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4091 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 499:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 499: {
               //#line 4097 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4095 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4097 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 501:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 501: {
               //#line 4104 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4102 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4104 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 502:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 502: {
               //#line 4109 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4107 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4107 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4109 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e);
                      break;
            }
    
            //
            // Rule 503:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 503: {
               //#line 4116 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4114 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4116 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 505:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 505: {
               //#line 4123 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4121 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4121 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4123 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 506:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 506: {
               //#line 4128 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4126 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4126 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4128 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 507:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 507: {
               //#line 4133 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4131 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4131 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4133 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 509:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 509: {
               //#line 4140 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4138 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4138 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4140 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 510:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 510: {
               //#line 4145 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4143 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4143 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4145 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 512:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 512: {
               //#line 4152 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4150 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4150 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4152 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 513:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 513: {
               //#line 4157 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4155 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4155 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4157 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 514:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 514: {
               //#line 4162 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4160 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4160 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4162 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 516:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 516: {
               //#line 4169 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4167 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4167 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4169 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 519:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 519: {
               //#line 4178 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4176 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4176 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4178 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 520:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 520: {
               //#line 4183 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4181 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4181 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4183 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 521:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 521: {
               //#line 4188 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4186 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4186 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4188 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 522:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 522: {
               //#line 4193 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4191 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4191 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4193 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 523:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 523: {
               //#line 4198 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4196 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4196 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4198 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 524:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 524: {
               //#line 4203 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4201 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4201 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4203 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 526:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 526: {
               //#line 4210 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4208 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4208 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4210 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 527:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 527: {
               //#line 4215 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4213 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4213 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4215 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 528:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 528: {
               //#line 4220 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4218 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4218 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4220 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 530:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 530: {
               //#line 4227 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4225 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4225 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4227 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 532:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 532: {
               //#line 4234 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4232 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4232 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4234 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 534:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 534: {
               //#line 4241 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4239 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4239 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4241 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 536:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 536: {
               //#line 4248 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4246 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4246 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4248 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 538:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 538: {
               //#line 4255 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4253 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4253 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4255 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 544:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 544: {
               //#line 4267 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4265 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4265 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4265 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4267 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 547:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 547: {
               //#line 4276 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4274 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4274 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4274 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4276 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 548:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 548: {
               //#line 4281 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4279 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4279 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4279 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4279 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4281 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 549:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 549: {
               //#line 4286 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4284 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4284 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4284 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4284 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4286 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 550:  LeftHandSide ::= ExpressionName
            //
            case 550: {
               //#line 4292 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4290 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4292 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 552:  AssignmentOperator ::= =
            //
            case 552: {
               //#line 4299 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4299 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 553:  AssignmentOperator ::= *=
            //
            case 553: {
               //#line 4304 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4304 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 554:  AssignmentOperator ::= /=
            //
            case 554: {
               //#line 4309 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4309 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 555:  AssignmentOperator ::= %=
            //
            case 555: {
               //#line 4314 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4314 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 556:  AssignmentOperator ::= +=
            //
            case 556: {
               //#line 4319 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4319 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 557:  AssignmentOperator ::= -=
            //
            case 557: {
               //#line 4324 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4324 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 558:  AssignmentOperator ::= <<=
            //
            case 558: {
               //#line 4329 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4329 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= >>=
            //
            case 559: {
               //#line 4334 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4334 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 560:  AssignmentOperator ::= >>>=
            //
            case 560: {
               //#line 4339 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4339 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 561:  AssignmentOperator ::= &=
            //
            case 561: {
               //#line 4344 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4344 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 562:  AssignmentOperator ::= ^=
            //
            case 562: {
               //#line 4349 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4349 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 563:  AssignmentOperator ::= |=
            //
            case 563: {
               //#line 4354 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4354 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 566:  PrefixOp ::= +
            //
            case 566: {
               //#line 4365 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4365 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 567:  PrefixOp ::= -
            //
            case 567: {
               //#line 4370 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4370 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 568:  PrefixOp ::= !
            //
            case 568: {
               //#line 4375 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4375 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 569:  PrefixOp ::= ~
            //
            case 569: {
               //#line 4380 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4380 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 570:  BinOp ::= +
            //
            case 570: {
               //#line 4386 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4386 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 571:  BinOp ::= -
            //
            case 571: {
               //#line 4391 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4391 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 572:  BinOp ::= *
            //
            case 572: {
               //#line 4396 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4396 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 573:  BinOp ::= /
            //
            case 573: {
               //#line 4401 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4401 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 574:  BinOp ::= %
            //
            case 574: {
               //#line 4406 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4406 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 575:  BinOp ::= &
            //
            case 575: {
               //#line 4411 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4411 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 576:  BinOp ::= |
            //
            case 576: {
               //#line 4416 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4416 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 577:  BinOp ::= ^
            //
            case 577: {
               //#line 4421 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4421 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 578:  BinOp ::= &&
            //
            case 578: {
               //#line 4426 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4426 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 579:  BinOp ::= ||
            //
            case 579: {
               //#line 4431 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4431 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 580:  BinOp ::= <<
            //
            case 580: {
               //#line 4436 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4436 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 581:  BinOp ::= >>
            //
            case 581: {
               //#line 4441 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4441 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 582:  BinOp ::= >>>
            //
            case 582: {
               //#line 4446 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4446 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 583:  BinOp ::= >=
            //
            case 583: {
               //#line 4451 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4451 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 584:  BinOp ::= <=
            //
            case 584: {
               //#line 4456 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4456 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 585:  BinOp ::= >
            //
            case 585: {
               //#line 4461 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4461 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 586:  BinOp ::= <
            //
            case 586: {
               //#line 4466 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4466 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 587:  BinOp ::= ==
            //
            case 587: {
               //#line 4474 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4474 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 588:  BinOp ::= !=
            //
            case 588: {
               //#line 4479 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4479 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 589:  Catchesopt ::= $Empty
            //
            case 589: {
               //#line 4488 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4488 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 591:  Identifieropt ::= $Empty
            //
            case 591:
                setResult(null);
                break;

            //
            // Rule 592:  Identifieropt ::= Identifier
            //
            case 592: {
               //#line 4497 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4495 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4497 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 593:  ForUpdateopt ::= $Empty
            //
            case 593: {
               //#line 4503 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4503 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 595:  Expressionopt ::= $Empty
            //
            case 595:
                setResult(null);
                break;

            //
            // Rule 597:  ForInitopt ::= $Empty
            //
            case 597: {
               //#line 4514 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4514 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 599:  SwitchLabelsopt ::= $Empty
            //
            case 599: {
               //#line 4521 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4521 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 601:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 601: {
               //#line 4528 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4528 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 603:  VariableModifiersopt ::= $Empty
            //
            case 603: {
               //#line 4535 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4535 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 605:  VariableInitializersopt ::= $Empty
            //
            case 605:
                setResult(null);
                break;

            //
            // Rule 607:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 607: {
               //#line 4546 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4546 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 609:  ExtendsInterfacesopt ::= $Empty
            //
            case 609: {
               //#line 4553 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4553 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 611:  InterfaceModifiersopt ::= $Empty
            //
            case 611: {
               //#line 4560 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4560 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 613:  ClassBodyopt ::= $Empty
            //
            case 613:
                setResult(null);
                break;

            //
            // Rule 615:  Argumentsopt ::= $Empty
            //
            case 615: {
               //#line 4571 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4571 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 617:  ArgumentListopt ::= $Empty
            //
            case 617: {
               //#line 4578 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4578 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 619:  BlockStatementsopt ::= $Empty
            //
            case 619: {
               //#line 4585 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4585 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 621:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 621:
                setResult(null);
                break;

            //
            // Rule 623:  ConstructorModifiersopt ::= $Empty
            //
            case 623: {
               //#line 4596 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4596 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 625:  FormalParameterListopt ::= $Empty
            //
            case 625: {
               //#line 4603 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4603 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 627:  Throwsopt ::= $Empty
            //
            case 627: {
               //#line 4610 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4610 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 629:  MethodModifiersopt ::= $Empty
            //
            case 629: {
               //#line 4617 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4617 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 631:  TypeModifieropt ::= $Empty
            //
            case 631: {
               //#line 4624 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4624 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 633:  FieldModifiersopt ::= $Empty
            //
            case 633: {
               //#line 4631 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4631 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 635:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 635: {
               //#line 4638 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4638 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 637:  Interfacesopt ::= $Empty
            //
            case 637: {
               //#line 4645 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4645 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 639:  Superopt ::= $Empty
            //
            case 639:
                setResult(null);
                break;

            //
            // Rule 641:  TypeParametersopt ::= $Empty
            //
            case 641: {
               //#line 4656 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4656 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 643:  FormalParametersopt ::= $Empty
            //
            case 643: {
               //#line 4663 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4663 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 645:  Annotationsopt ::= $Empty
            //
            case 645: {
               //#line 4670 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4670 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 647:  TypeDeclarationsopt ::= $Empty
            //
            case 647: {
               //#line 4677 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4677 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 649:  ImportDeclarationsopt ::= $Empty
            //
            case 649: {
               //#line 4684 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4684 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 651:  PackageDeclarationopt ::= $Empty
            //
            case 651:
                setResult(null);
                break;

            //
            // Rule 653:  ResultTypeopt ::= $Empty
            //
            case 653:
                setResult(null);
                break;

            //
            // Rule 655:  TypeArgumentsopt ::= $Empty
            //
            case 655: {
               //#line 4699 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4699 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 657:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 657: {
               //#line 4706 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4706 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 659:  Propertiesopt ::= $Empty
            //
            case 659: {
               //#line 4713 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4713 "/Users/rmfuhrer/eclipse/workspaces/x10-safety/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 661:  ,opt ::= $Empty
            //
            case 661:
                setResult(null);
                break;

    
            default:
                break;
        }
        return;
    }
}

