
//#line 18 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
//
// Licensed Material
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

import lpg.runtime.*;

//#line 28 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

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
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FloatLit;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
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
import polyglot.parse.Name;
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

public class X10Parser extends PrsStream implements RuleAction, Parser
{
    private static ParseTable prs = new X10Parserprs();
    private BacktrackingParser btParser;

    public BacktrackingParser getParser() { return btParser; }
    private void setResult(Object object) { btParser.setSym1(object); }
    public Object getRhsSym(int i) { return btParser.getSym(i); }

    public int getRhsTokenIndex(int i) { return btParser.getToken(i); }
    public IToken getRhsIToken(int i) { return super.getIToken(getRhsTokenIndex(i)); }
    
    public int getRhsFirstTokenIndex(int i) { return btParser.getFirstToken(i); }
    public IToken getRhsFirstIToken(int i) { return super.getIToken(getRhsFirstTokenIndex(i)); }

    public int getRhsLastTokenIndex(int i) { return btParser.getLastToken(i); }
    public IToken getRhsLastIToken(int i) { return super.getIToken(getRhsLastTokenIndex(i)); }

    public int getLeftSpan() { return btParser.getFirstToken(); }
    public IToken getLeftIToken()  { return super.getIToken(getLeftSpan()); }

    public int getRightSpan() { return btParser.getLastToken(); }
    public IToken getRightIToken() { return super.getIToken(getRightSpan()); }

    public int getRhsErrorTokenIndex(int i)
    {
        int index = btParser.getToken(i);
        IToken err = super.getIToken(index);
        return (err instanceof ErrorToken ? index : 0);
    }
    public ErrorToken getRhsErrorIToken(int i)
    {
        int index = btParser.getToken(i);
        IToken err = super.getIToken(index);
        return (ErrorToken) (err instanceof ErrorToken ? err : null);
    }

    public X10Parser(ILexStream lexStream)
    {
        super(lexStream);

        try
        {
            super.remapTerminalSymbols(orderedTerminalSymbols(), X10Parserprs.EOFT_SYMBOL);
        }
        catch(NullExportedSymbolsException e) {
        }
        catch(NullTerminalSymbolsException e) {
        }
        catch(UnimplementedTerminalsException e)
        {
            java.util.ArrayList unimplemented_symbols = e.getSymbols();
            System.out.println("The Lexer will not scan the following token(s):");
            for (int i = 0; i < unimplemented_symbols.size(); i++)
            {
                Integer id = (Integer) unimplemented_symbols.get(i);
                System.out.println("    " + X10Parsersym.orderedTerminalSymbols[id.intValue()]);               
            }
            System.out.println();                        
        }
        catch(UndefinedEofSymbolException e)
        {
            throw new Error(new UndefinedEofSymbolException
                                ("The Lexer does not implement the Eof symbol " +
                                 X10Parsersym.orderedTerminalSymbols[X10Parserprs.EOFT_SYMBOL]));
        } 
    }

    public String[] orderedTerminalSymbols() { return X10Parsersym.orderedTerminalSymbols; }
    public String getTokenKindName(int kind) { return X10Parsersym.orderedTerminalSymbols[kind]; }
    public int getEOFTokenKind() { return X10Parserprs.EOFT_SYMBOL; }
    public PrsStream getParseStream() { return (PrsStream) this; }

    /**
     * When constructing a SAFARI parser, a handler for error messages
     * can be passed to the parser.
     */
    /*
    private IMessageHandler handler = null;
    public void setMessageHandler(IMessageHandler handler)
    {
        this.handler = handler;
    }
    
    //
    // Redirect syntax error message to proper recipient.
    //
    public void reportError(int error_code, String location_info, int left_token, int right_token, String token_text)
    {
        if (this.handler == null)
            super.reportError(error_code,
                              location_info,
                              left_token,
                              right_token,
                              token_text);
        else 
        {
            int start_offset = super.getStartOffset(left_token),
                end_offset = (right_token > left_token 
                                          ? super.getEndOffset(right_token)
                                          : super.getEndOffset(left_token));

            String msg = ((error_code == DELETION_CODE ||
                           error_code == MISPLACED_CODE ||
                           token_text.equals(""))
                                       ? ""
                                       : (token_text + " ")) +
                         errorMsgText[error_code];

            handler.handleMessage(start_offset,
                                  end_offset - start_offset + 1,
                                  msg);
        }
    }

    //
    // Report error message for given error_token.
    //
    public final void reportErrorTokenMessage(int error_token, String msg)
    {
        if (this.handler == null)
        {
            int firsttok = super.getFirstRealToken(error_token),
                lasttok = super.getLastRealToken(error_token);
            String location = super.getFileName() + ':' +
                              (firsttok > lasttok
                                        ? (super.getEndLine(lasttok) + ":" + super.getEndColumn(lasttok))
                                        : (super.getLine(error_token) + ":" +
                                           super.getColumn(error_token) + ":" +
                                           super.getEndLine(error_token) + ":" +
                                           super.getEndColumn(error_token)))
                              + ": ";
            super.reportError((firsttok > lasttok ? ParseErrorCodes.INSERTION_CODE : ParseErrorCodes.SUBSTITUTION_CODE), location, msg);
        }
        else 
        {
            handler.handleMessage(super.getStartOffset(error_token),
                                  super.getTokenLength(error_token),
                                  msg);
        }
    }
    */

    public polyglot.ast.Node parser()
    {
        return parser(null, Integer.MAX_VALUE);
    }
    
    public polyglot.ast.Node parser(Monitor monitor)
    {
        return parser(monitor, Integer.MAX_VALUE);
    }
    
    public polyglot.ast.Node parser(int error_repair_count)
    {
        return parser(null, error_repair_count);
    }

    public polyglot.ast.Node parser(Monitor monitor, int error_repair_count)
    {
        try
        {
            btParser = new BacktrackingParser(monitor, (TokenStream) this, prs, (RuleAction) this);
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

        try
        {
            return (polyglot.ast.Node) btParser.fuzzyParse(error_repair_count);
        }
        catch (BadParseException e)
        {
            reset(e.error_token); // point to error token
            DiagnoseParser diagnoseParser = new DiagnoseParser(this, prs);
            diagnoseParser.diagnose(e.error_token);
        }

        return null;
    }


    //#line 306 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
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

    public X10Parser(LexStream lexStream, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
    {
        this(lexStream);
        initialize((X10TypeSystem) t,
                   (X10NodeFactory) n,
                   source,
                   q);
        setMessageHandler(new MessageHandler(q));
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
        return getFileName() + ':' +
               getLine(lefttok) + ":" + getColumn(lefttok) + ":" +
               getEndLine(righttok) + ":" + getEndColumn(righttok) + ": ";
    }

    public Position getErrorPosition(int lefttok, int righttok)
    {
        return new Position(null, getFileName(),
               getLine(lefttok), getColumn(lefttok),
               getEndLine(righttok), getEndColumn(righttok));
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
            this.leftIToken = null;
            this.rightIToken = null;
        }

        public IToken getLeftIToken() { return leftIToken; }
        public IToken getRightIToken() { return rightIToken; }

        public String toText()
        {
        if (true) return "...";
            IPrsStream prsStream = leftIToken.getPrsStream();
            return new String(prsStream.getInputChars(), offset(), endOffset() - offset() + 1);
        }
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

    public JPGPosition pos(JPGPosition start, JPGPosition end) {
        return new JPGPosition(start.path(), start.file(), start.leftIToken, end.rightIToken);
    }

    private void checkTypeName(Id identifier) {
        String filename = file(),
               idname = identifier.id();
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
        return new Operator(pos(i), super.getName(i), super.getKind(i));
    }

    private polyglot.lex.Identifier id(int i) {
        return new Identifier(pos(i), super.getName(i), X10Parsersym.TK_IDENTIFIER);
    }
    private String comment(int i) {
        String s = super.getName(i);
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
            l2.add(nf.AmbTypeNode(f.position(), null, f.id()));
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
        long x = parseLong(super.getName(i), radix);
        return new LongLiteral(pos(i),  x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.LongLiteral int_lit(int i)
    {
        long x = parseLong(super.getName(i));
        return new LongLiteral(pos(i),  x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.LongLiteral long_lit(int i, int radix)
    {
        long x = parseLong(super.getName(i), radix);
        return new LongLiteral(pos(i), x, X10Parsersym.TK_LongLiteral);
    }

    private polyglot.lex.LongLiteral long_lit(int i)
    {
        long x = parseLong(super.getName(i));
        return new LongLiteral(pos(i), x, X10Parsersym.TK_LongLiteral);
    }

    private polyglot.lex.FloatLiteral float_lit(int i)
    {
        try {
            String s = super.getName(i);
            int end_index = (s.charAt(s.length() - 1) == 'f' || s.charAt(s.length() - 1) == 'F'
                                                       ? s.length() - 1
                                                       : s.length());
            float x = Float.parseFloat(s.substring(0, end_index));
            return new FloatLiteral(pos(i), x, X10Parsersym.TK_FloatingPointLiteral);
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
            return new DoubleLiteral(pos(i), x, X10Parsersym.TK_DoubleLiteral);
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

        return new CharacterLiteral(pos(i), x, X10Parsersym.TK_CharacterLiteral);
    }

    private polyglot.lex.BooleanLiteral boolean_lit(int i)
    {
        return new BooleanLiteral(pos(i), super.getKind(i) == X10Parsersym.TK_true, super.getKind(i));
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
                //#line 6 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Name TypeName = (Name) getRhsSym(1);
                //#line 8 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                    setResult(new Name(nf,
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
                //#line 16 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Name PackageName = (Name) getRhsSym(1);
                //#line 18 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                    setResult(new Name(nf,
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
                //#line 26 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 28 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                    setResult(new Name(nf,
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
                //#line 36 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 38 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                    setResult(new Name(nf,
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
                //#line 46 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Name PackageOrTypeName = (Name) getRhsSym(1);
                //#line 48 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                    setResult(new Name(nf,
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
                //#line 56 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 58 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                    setResult(new Name(nf,
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
                //#line 66 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                break;
            }
     
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
                
                //#line 74 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                break;
            }
     
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
                //#line 78 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 78 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                break;
            }
     
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
                //#line 85 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
                //#line 92 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
                //#line 98 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Name ClassName = (Name) ((Object[]) MethodClassNameSuperPrefix)[0];
                JPGPosition super_pos = (JPGPosition) ((Object[]) MethodClassNameSuperPrefix)[1];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodClassNameSuperPrefix)[2];
                setResult(nf.Call(pos(), nf.Super(super_pos, ClassName.toType()), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 13:  MethodPrimaryPrefix ::= Primary . ErrorId$ErrorId
            //
            case 13: {
                //#line 107 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                //#line 115 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                break;
            }
     
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
                //#line 120 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 120 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                //#line 861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 863 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                //#line 873 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 873 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 873 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 873 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 873 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 875 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                //#line 887 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 889 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
   setResult(PropertyList);
           break;
            }  
            //
            // Rule 19:  PropertyList ::= Property
            //
            case 19: {
                //#line 892 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 894 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                break;
            }
     
            //
            // Rule 20:  PropertyList ::= PropertyList , Property
            //
            case 20: {
                //#line 899 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 899 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 901 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                PropertyList.add(Property);
                break;
            }
     
            //
            // Rule 21:  Property ::= Annotationsopt Identifier : Type
            //
            case 21: {
                //#line 906 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 906 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 906 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(4);
                //#line 908 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), Type, Identifier));
                break;
            }
     
            //
            // Rule 22:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 22: {
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 914 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
       if (Identifier.id().equals("this")) {
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
            // Rule 23:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 23: {
                //#line 944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 946 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 24:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 24: {
                //#line 960 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 960 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 962 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 25:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 25: {
                //#line 965 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 965 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 967 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 26:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 26: {
                //#line 970 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 970 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 970 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 972 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 27:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 27: {
                //#line 975 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 975 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 975 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 977 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 28:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypePropertiesopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 28: {
                //#line 981 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 981 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 981 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 981 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 981 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 981 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 981 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 983 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
      checkTypeName(Identifier);
      List/*<PropertyDecl>*/ props = Propertiesopt;
      DepParameterExpr ci = WhereClauseopt;
      FlagsNode fn = extractFlags(InterfaceModifiersopt, Flags.INTERFACE);
      ClassDecl cd = nf.X10ClassDecl(pos(),
                   fn,
                   Identifier,
                   TypePropertiesopt,
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
            // Rule 29:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 29: {
                //#line 1001 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(2);
                //#line 1001 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1001 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1001 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1003 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 30:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 30: {
                //#line 1008 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1008 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1008 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1008 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1008 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1010 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Name b = new X10Name(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 31:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 31: {
                //#line 1016 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 1016 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1016 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1016 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1016 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1018 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Name b = new X10Name(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 32:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 32: {
                //#line 1025 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1025 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1027 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 35:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt => Type
            //
            case 35: {
                //#line 1037 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1037 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1037 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1037 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1037 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1039 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt));
                break;
            }
     
            //
            // Rule 40:  AnnotatedType ::= Type Annotations
            //
            case 40: {
                //#line 1046 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1046 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1048 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn);
                break;
            }
     
            //
            // Rule 44:  ConstrainedType ::= ( Type )
            //
            case 44: {
                //#line 1057 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1059 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Type);
                break;
            }
     
            //
            // Rule 49:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt PlaceTypeSpecifieropt
            //
            case 49: {
                //#line 1082 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1082 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1082 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1082 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1082 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1082 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(7);
                //#line 1084 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            TypeNode type = nf.X10AmbTypeNode(pos(), Primary, Identifier);
            // TODO: place constraint
            if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                type = nf.AmbDepTypeNode(pos(), Primary, Identifier, TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                break;
            }
     
            //
            // Rule 50:  NamedType ::= Primary . class$c TypeArgumentsopt Argumentsopt DepParametersopt PlaceTypeSpecifieropt
            //
            case 50: {
                //#line 1093 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1093 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 1093 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1093 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1093 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1093 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(7);
                //#line 1095 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            TypeNode type = nf.X10AmbTypeNode(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class"));
            // TODO: place constraint
            if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                type = nf.AmbDepTypeNode(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class"), TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                break;
            }
     
            //
            // Rule 51:  NamedType ::= TypeName TypeArgumentsopt Argumentsopt DepParametersopt PlaceTypeSpecifieropt
            //
            case 51: {
                //#line 1104 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 1104 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1104 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1104 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1104 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(5);
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            TypeNode type;
            
            if (TypeName.name.id().equals("void")) {
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
            // Rule 52:  DepParameters ::= { Conjunction }
            //
            case 52: {
                //#line 1129 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(2);
                //#line 1131 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.DepParameterExpr(pos(), Collections.EMPTY_LIST, Conjunction));
                break;
            }
     
            //
            // Rule 53:  DepParameters ::= { ExistentialList ; Conjunction }
            //
            case 53: {
                //#line 1134 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(2);
                //#line 1134 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(4);
                //#line 1136 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialList, Conjunction));
                break;
            }
     
            //
            // Rule 54:  TypeProperties ::= [ TypePropertyList ]
            //
            case 54: {
                //#line 1140 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertyList = (List) getRhsSym(2);
                //#line 1142 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(TypePropertyList);
                break;
            }
     
            //
            // Rule 55:  TypeParameters ::= [ TypeParameterList ]
            //
            case 55: {
                //#line 1146 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1148 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(TypeParameterList);
                break;
            }
     
            //
            // Rule 56:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 56: {
                //#line 1151 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1153 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(FormalParameterListopt);
                break;
            }
     
            //
            // Rule 57:  Conjunction ::= Expression
            //
            case 57: {
                //#line 1157 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1159 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Expression);
                break;
            }
     
            //
            // Rule 58:  Conjunction ::= Conjunction , Expression
            //
            case 58: {
                //#line 1162 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(1);
                //#line 1162 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1164 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), Conjunction, Binary.COND_AND, Expression));
                break;
            }
     
            //
            // Rule 59:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 59: {
                //#line 1168 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1168 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1170 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                break;
            }
     
            //
            // Rule 60:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 60: {
                //#line 1173 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1173 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1175 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                break;
            }
     
            //
            // Rule 61:  WhereClause ::= DepParameters
            //
            case 61: {
                //#line 1179 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1181 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(DepParameters);
                break;
            }
       
            //
            // Rule 62:  ExistentialListopt ::= $Empty
            //
            case 62: {
                
                //#line 1187 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(new ArrayList());
                break;
            }
       
            //
            // Rule 63:  ExistentialListopt ::= ExistentialList
            //
            case 63: {
                //#line 1190 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1192 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(ExistentialList);
                break;
            }
     
            //
            // Rule 64:  ExistentialList ::= FormalParameter
            //
            case 64: {
                //#line 1196 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1198 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                setResult(l);
                break;
            }
     
            //
            // Rule 65:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 65: {
                //#line 1203 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1203 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1205 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 68:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 68: {
                //#line 1214 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1214 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1214 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 1214 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1214 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1214 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1214 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1214 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1216 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
      checkTypeName(Identifier);
      List/*<PropertyDecl>*/ props = Propertiesopt;
      DepParameterExpr ci = WhereClauseopt;
      FlagsNode f = extractFlags(ClassModifiersopt);
      List annotations = extractAnnotations(ClassModifiersopt);
      ClassDecl cd = X10Flags.isValue(f.flags())
         ? nf.ValueClassDecl(pos(),
              f, Identifier, TypePropertiesopt, props, ci, Superopt, Interfacesopt, ClassBody)
         : nf.X10ClassDecl(pos(),
              f, Identifier, TypePropertiesopt, props, ci, Superopt, Interfacesopt, ClassBody);
      cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(annotations);
      setResult(cd);
                break;
            }
     
            //
            // Rule 69:  ValueClassDeclaration ::= ClassModifiersopt value Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 69: {
                //#line 1231 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1231 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1231 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 1231 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1231 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1231 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1231 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1231 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1233 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
    checkTypeName(Identifier);
    List props = Propertiesopt;
    DepParameterExpr ci = WhereClauseopt;
    ClassDecl cd = (nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
    extractFlags(ClassModifiersopt), Identifier, 
    TypePropertiesopt, props, ci, Superopt, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ClassModifiersopt));
    setResult(cd);
                break;
            }
     
            //
            // Rule 70:  ValueClassDeclaration ::= ClassModifiersopt value class Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 70: {
                //#line 1243 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1243 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1243 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(5);
                //#line 1243 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(6);
                //#line 1243 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1243 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(8);
                //#line 1243 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(9);
                //#line 1243 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(10);
                //#line 1245 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                checkTypeName(Identifier);
    List props = Propertiesopt;
    DepParameterExpr ci = WhereClauseopt;
    ClassDecl cd = (nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                                extractFlags(ClassModifiersopt), Identifier, 
                                TypePropertiesopt, props, ci, Superopt, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ClassModifiersopt));
    setResult(cd);
                break;
            }
     
            //
            // Rule 71:  ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt ConstructorBody
            //
            case 71: {
                //#line 1256 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1256 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1256 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1256 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1256 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1256 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1256 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(9);
                //#line 1258 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 72:  Super ::= extends ClassType
            //
            case 72: {
                //#line 1272 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1274 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ClassType);
                break;
            }
     
            //
            // Rule 73:  FieldKeyword ::= val
            //
            case 73: {
                
                //#line 1280 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 74:  FieldKeyword ::= var
            //
            case 74: {
                
                //#line 1285 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                break;
            }
     
            //
            // Rule 75:  FieldKeyword ::= const
            //
            case 75: {
                
                //#line 1290 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                break;
            }
     
            //
            // Rule 76:  VarKeyword ::= val
            //
            case 76: {
                
                //#line 1298 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 77:  VarKeyword ::= var
            //
            case 77: {
                
                //#line 1303 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                break;
            }
     
            //
            // Rule 78:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 78: {
                //#line 1308 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1308 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1308 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1310 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                    FlagsNode fn = extractFlags(FieldModifiersopt, FieldKeyword);
    
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    for (Iterator i = FieldDeclarators.iterator(); i.hasNext(); )
                    {
                        Object[] o = (Object[]) i.next();
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        List exploded = (List) o[2];
                        DepParameterExpr where = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                        if (type == null) type = nf.UnknownTypeNode(name.position());
                        Expr init = (Expr) o[5];
                        FieldDecl ld = nf.FieldDecl(pos, where, fn,
                                           type, name, init);
                        ld = (FieldDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(FieldModifiersopt));
                        l.add(ld);
                    }
                setResult(l);
                break;
            }
     
            //
            // Rule 108:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 108: {
                //#line 1365 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1365 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1367 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.If(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 109:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 109: {
                //#line 1371 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1371 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1371 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1373 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                break;
            }
     
            //
            // Rule 110:  EmptyStatement ::= ;
            //
            case 110: {
                
                //#line 1379 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 111:  LabeledStatement ::= Identifier : Statement
            //
            case 111: {
                //#line 1383 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1383 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1385 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Labeled(pos(), Identifier, Statement));
                break;
            }
     
            //
            // Rule 112:  ExpressionStatement ::= StatementExpression ;
            //
            case 112: {
                //#line 1389 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1391 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                boolean eval = true;
                if (StatementExpression instanceof X10Call) {
                    X10Call c = (X10Call) StatementExpression;
                    if (c.name().id().equals("property") && c.target() == null) {
                        setResult(nf.AssignPropertyCall(c.position(),c.typeArguments(), c.arguments()));
                        eval = false;
                    }
                    if (c.name().id().equals("super") && c.target() instanceof Expr) {
                        setResult(nf.X10SuperCall(c.position(), (Expr) c.target(), c.typeArguments(), c.arguments()));
                        eval = false;
                   }
                   if (c.name().id().equals("this") && c.target() instanceof Expr) {
                        setResult(nf.X10ThisCall(c.position(), (Expr) c.target(), c.typeArguments(), c.arguments()));
                        eval = false;
                   }
                }
                    
                setResult(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 120:  AssertStatement ::= assert Expression ;
            //
            case 120: {
                //#line 1420 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1422 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assert(pos(), Expression));
                break;
            }
     
            //
            // Rule 121:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 121: {
                //#line 1425 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1425 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1427 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                break;
            }
     
            //
            // Rule 122:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 122: {
                //#line 1431 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1431 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1433 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                break;
            }
     
            //
            // Rule 123:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 123: {
                //#line 1437 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1437 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1439 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                break;
            }
     
            //
            // Rule 125:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 125: {
                //#line 1445 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1445 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1447 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                break;
            }
     
            //
            // Rule 126:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 126: {
                //#line 1452 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1452 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1454 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                break;
            }
     
            //
            // Rule 127:  SwitchLabels ::= SwitchLabel
            //
            case 127: {
                //#line 1461 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1463 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                break;
            }
     
            //
            // Rule 128:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 128: {
                //#line 1468 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1468 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1470 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                break;
            }
     
            //
            // Rule 129:  SwitchLabel ::= case ConstantExpression :
            //
            case 129: {
                //#line 1475 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1477 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                break;
            }
     
            //
            // Rule 130:  SwitchLabel ::= default :
            //
            case 130: {
                
                //#line 1482 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 131:  WhileStatement ::= while ( Expression ) Statement
            //
            case 131: {
                //#line 1486 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1486 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1488 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.While(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 132:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 132: {
                //#line 1492 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1492 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1494 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                break;
            }
     
            //
            // Rule 135:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 135: {
                //#line 1501 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1501 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1501 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1501 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1503 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                break;
            }
     
            //
            // Rule 137:  ForInit ::= LocalVariableDeclaration
            //
            case 137: {
                //#line 1508 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1510 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 139:  StatementExpressionList ::= StatementExpression
            //
            case 139: {
                //#line 1518 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1520 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                break;
            }
     
            //
            // Rule 140:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 140: {
                //#line 1525 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1525 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1527 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 141:  BreakStatement ::= break Identifieropt ;
            //
            case 141: {
                //#line 1531 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1533 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Break(pos(), Identifieropt));
                break;
            }
     
            //
            // Rule 142:  ContinueStatement ::= continue Identifieropt ;
            //
            case 142: {
                //#line 1537 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1539 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                break;
            }
     
            //
            // Rule 143:  ReturnStatement ::= return Expressionopt ;
            //
            case 143: {
                //#line 1543 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1545 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Return(pos(), Expressionopt));
                break;
            }
     
            //
            // Rule 144:  ThrowStatement ::= throw Expression ;
            //
            case 144: {
                //#line 1549 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1551 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Throw(pos(), Expression));
                break;
            }
     
            //
            // Rule 145:  TryStatement ::= try Block Catches
            //
            case 145: {
                //#line 1555 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1555 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1557 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Try(pos(), Block, Catches));
                break;
            }
     
            //
            // Rule 146:  TryStatement ::= try Block Catchesopt Finally
            //
            case 146: {
                //#line 1560 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1560 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1560 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1562 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                break;
            }
     
            //
            // Rule 147:  Catches ::= CatchClause
            //
            case 147: {
                //#line 1566 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1568 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                break;
            }
     
            //
            // Rule 148:  Catches ::= Catches CatchClause
            //
            case 148: {
                //#line 1573 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1573 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1575 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                break;
            }
     
            //
            // Rule 149:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 149: {
                //#line 1580 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1580 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1582 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                break;
            }
     
            //
            // Rule 150:  Finally ::= finally Block
            //
            case 150: {
                //#line 1586 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1588 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 151:  NowStatement ::= now ( Clock ) Statement
            //
            case 151: {
                //#line 1592 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1592 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1594 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Now(pos(), Clock, Statement));
                break;
            }
     
            //
            // Rule 152:  ClockedClause ::= clocked ( ClockList )
            //
            case 152: {
                //#line 1598 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1600 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 153:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 153: {
                //#line 1604 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1604 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1604 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1606 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                break;
            }
     
            //
            // Rule 154:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 154: {
                //#line 1613 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1613 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1615 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                               ? nf.Here(pos(getLeftSpan()))
                                               : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 155:  WhenStatement ::= when ( Expression ) Statement
            //
            case 155: {
                //#line 1622 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1622 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1624 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.When(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 156:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 156: {
                //#line 1627 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1627 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1627 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1627 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1629 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 157:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 157: {
                //#line 1634 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1634 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1634 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1634 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1636 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 158:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 158: {
                //#line 1648 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1648 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1648 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1648 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1650 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 159:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 159: {
                //#line 1662 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1662 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1662 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1664 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 160:  FinishStatement ::= finish Statement
            //
            case 160: {
                //#line 1675 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1677 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Finish(pos(),  Statement));
                break;
            }
     
            //
            // Rule 161:  AnnotationStatement ::= Annotations Statement
            //
            case 161: {
                //#line 1682 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 1682 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1684 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (Statement.ext() instanceof X10Ext && Annotations instanceof List) {
                    Statement = (Stmt) ((X10Ext) Statement.ext()).annotations((List) Annotations);
                }
                setResult(Statement);
                break;
            }
     
            //
            // Rule 162:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 162: {
                //#line 1691 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1693 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(PlaceExpression);
                break;
            }
     
            //
            // Rule 164:  NextStatement ::= next ;
            //
            case 164: {
                
                //#line 1701 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 165:  AwaitStatement ::= await Expression ;
            //
            case 165: {
                //#line 1705 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1707 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Await(pos(), Expression));
                break;
            }
     
            //
            // Rule 166:  ClockList ::= Clock
            //
            case 166: {
                //#line 1711 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1713 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                break;
            }
     
            //
            // Rule 167:  ClockList ::= ClockList , Clock
            //
            case 167: {
                //#line 1718 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1718 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1720 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 168:  Clock ::= Expression
            //
            case 168: {
                //#line 1726 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1728 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
    setResult(Expression);
                break;
            }
     
            //
            // Rule 169:  CastExpression ::= ConditionalExpression as Type
            //
            case 169: {
                //#line 1740 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1740 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1742 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Cast(pos(), Type, ConditionalExpression));
                break;
            }
     
            //
            // Rule 170:  CastExpression ::= ConditionalExpression to Type
            //
            case 170: {
                //#line 1745 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1745 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1747 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Cast(pos(), Type, ConditionalExpression));
                break;
            }
     
            //
            // Rule 171:  CastExpression ::= ConditionalExpression ! Expression
            //
            case 171: {
                //#line 1750 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1750 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1752 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.PlaceCast(pos(), Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 173:  TypePropertyList ::= TypeProperty
            //
            case 173: {
                //#line 1759 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypePropertyNode TypeProperty = (TypePropertyNode) getRhsSym(1);
                //#line 1761 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypePropertyNode.class, false);
                l.add(TypeProperty);
                setResult(l);
                break;
            }
     
            //
            // Rule 174:  TypePropertyList ::= TypePropertyList , TypeProperty
            //
            case 174: {
                //#line 1766 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertyList = (List) getRhsSym(1);
                //#line 1766 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypePropertyNode TypeProperty = (TypePropertyNode) getRhsSym(3);
                //#line 1768 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypePropertyList.add(TypeProperty);
                setResult(TypePropertyList);
                break;
            }
     
            //
            // Rule 175:  TypeParameterList ::= TypeParameter
            //
            case 175: {
                //#line 1773 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 1775 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 176:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 176: {
                //#line 1780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 1780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 1782 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                break;
            }
     
            //
            // Rule 177:  TypeProperty ::= Identifier
            //
            case 177: {
                //#line 1787 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1789 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.INVARIANT));
                break;
            }
     
            //
            // Rule 178:  TypeProperty ::= + Identifier
            //
            case 178: {
                //#line 1792 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1794 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.COVARIANT));
                break;
            }
     
            //
            // Rule 179:  TypeProperty ::= - Identifier
            //
            case 179: {
                //#line 1797 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1799 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.CONTRAVARIANT));
                break;
            }
     
            //
            // Rule 180:  TypeParameter ::= Identifier
            //
            case 180: {
                //#line 1803 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1805 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                break;
            }
     
            //
            // Rule 181:  Primary ::= here
            //
            case 181: {
                
                //#line 1811 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                break;
            }
     
            //
            // Rule 183:  RegionExpressionList ::= RegionExpression
            //
            case 183: {
                //#line 1817 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 1819 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                break;
            }
     
            //
            // Rule 184:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 184: {
                //#line 1824 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 1824 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 1826 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                break;
            }
     
            //
            // Rule 185:  Primary ::= [ ArgumentList ]
            //
            case 185: {
                //#line 1831 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(2);
                //#line 1833 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentList);
                setResult(tuple);
                break;
            }
     
            //
            // Rule 186:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 186: {
                //#line 1838 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 1838 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 1840 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                break;
            }
     
            //
            // Rule 187:  ClosureExpression ::= TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt => ClosureBody
            //
            case 187: {
                //#line 1845 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(1);
                //#line 1845 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(2);
                //#line 1845 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 1845 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 1845 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 1845 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 1847 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Closure(pos(), TypeParametersopt, FormalParameters, WhereClauseopt, 
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt, Throwsopt, ClosureBody));
                break;
            }
     
            //
            // Rule 188:  LastExpression ::= Expression
            //
            case 188: {
                //#line 1852 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1854 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                break;
            }
     
            //
            // Rule 189:  ClosureBody ::= CastExpression
            //
            case 189: {
                //#line 1858 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 1860 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), CastExpression, true)));
                break;
            }
     
            //
            // Rule 190:  ClosureBody ::= { BlockStatementsopt LastExpression }
            //
            case 190: {
                //#line 1863 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 1863 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(3);
                //#line 1865 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 192:  AsyncExpression ::= async ClosureBody
            //
            case 192: {
                //#line 1873 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 1875 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                break;
            }
     
            //
            // Rule 193:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 193: {
                //#line 1878 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1878 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 1880 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                break;
            }
     
            //
            // Rule 194:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 194: {
                //#line 1883 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1883 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 1885 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                break;
            }
     
            //
            // Rule 195:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 195: {
                //#line 1888 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1888 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 1888 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 1890 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                break;
            }
     
            //
            // Rule 196:  FutureExpression ::= future ClosureBody
            //
            case 196: {
                //#line 1894 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 1896 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                break;
            }
     
            //
            // Rule 197:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 197: {
                //#line 1899 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1899 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 1901 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                break;
            }
     
            //
            // Rule 198:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 198: {
                //#line 1904 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1904 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 1906 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                break;
            }
     
            //
            // Rule 199:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 199: {
                //#line 1909 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1909 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 1909 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 1911 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                break;
            }
     
            //
            // Rule 200:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 200:
                setResult(null);
                break;
 
            //
            // Rule 202:  DepParametersopt ::= $Empty
            //
            case 202:
                setResult(null);
                break;
 
            //
            // Rule 204:  PropertyListopt ::= $Empty
            //
            case 204: {
                
                //#line 1926 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                break;
            }
     
            //
            // Rule 206:  WhereClauseopt ::= $Empty
            //
            case 206:
                setResult(null);
                break;
 
            //
            // Rule 208:  ObjectKindopt ::= $Empty
            //
            case 208:
                setResult(null);
                break;
 
            //
            // Rule 210:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 210:
                setResult(null);
                break;
 
            //
            // Rule 212:  ClassModifiersopt ::= $Empty
            //
            case 212: {
                
                //#line 1945 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                break;
            }  
            //
            // Rule 214:  TypeDefModifiersopt ::= $Empty
            //
            case 214: {
                
                //#line 1951 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                break;
            }  
            //
            // Rule 216:  Unsafeopt ::= $Empty
            //
            case 216:
                setResult(null);
                break;
 
            //
            // Rule 217:  Unsafeopt ::= unsafe
            //
            case 217: {
                
                //#line 1959 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 218:  ClockedClauseopt ::= $Empty
            //
            case 218: {
                
                //#line 1966 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 220:  identifier ::= IDENTIFIER$ident
            //
            case 220: {
                //#line 1975 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 1977 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 221:  TypeName ::= Identifier
            //
            case 221: {
                //#line 1982 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1984 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 222:  TypeName ::= TypeName . Identifier
            //
            case 222: {
                //#line 1987 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 1987 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1989 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 224:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 224: {
                //#line 1999 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2001 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(TypeArgumentList);
                break;
            }
     
            //
            // Rule 225:  TypeArgumentList ::= Type
            //
            case 225: {
                //#line 2006 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2008 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                break;
            }
     
            //
            // Rule 226:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 226: {
                //#line 2013 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2013 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2015 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeArgumentList.add(Type);
                break;
            }
     
            //
            // Rule 227:  PackageName ::= Identifier
            //
            case 227: {
                //#line 2023 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2025 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 228:  PackageName ::= PackageName . Identifier
            //
            case 228: {
                //#line 2028 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageName = (Name) getRhsSym(1);
                //#line 2028 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2030 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 229:  ExpressionName ::= Identifier
            //
            case 229: {
                //#line 2044 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2046 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 230:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 230: {
                //#line 2049 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 2049 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2051 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 231:  MethodName ::= Identifier
            //
            case 231: {
                //#line 2059 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2061 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 232:  MethodName ::= AmbiguousName . Identifier
            //
            case 232: {
                //#line 2064 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 2064 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2066 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 233:  PackageOrTypeName ::= Identifier
            //
            case 233: {
                //#line 2074 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2076 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 234:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 234: {
                //#line 2079 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageOrTypeName = (Name) getRhsSym(1);
                //#line 2079 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2081 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 235:  AmbiguousName ::= Identifier
            //
            case 235: {
                //#line 2089 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2091 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 236:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 236: {
                //#line 2094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 2094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2096 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
               break;
            }
     
            //
            // Rule 237:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 237: {
                //#line 2106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2108 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // Add import x10.lang.* by default.
                int token_pos = (ImportDeclarationsopt.size() == 0
                                     ? TypeDeclarationsopt.size() == 0
                                           ? super.getSize() - 1
                                           : getPrevious(getRhsFirstTokenIndex(3))
                                 : getRhsLastTokenIndex(2)
                            );
                Import x10LangImport = 
                nf.Import(pos(token_pos), Import.PACKAGE, "x10.lang");
                ImportDeclarationsopt.add(x10LangImport);
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()), PackageDeclarationopt, ImportDeclarationsopt, TypeDeclarationsopt));
                break;
            }
     
            //
            // Rule 238:  ImportDeclarations ::= ImportDeclaration
            //
            case 238: {
                //#line 2122 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2124 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 239:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 239: {
                //#line 2129 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2129 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2131 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 240:  TypeDeclarations ::= TypeDeclaration
            //
            case 240: {
                //#line 2137 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2139 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 241:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 241: {
                //#line 2145 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2145 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2147 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 242:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 242: {
                //#line 2153 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2153 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageName = (Name) getRhsSym(3);
                //#line 2155 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn);
                break;
            }
     
            //
            // Rule 245:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 245: {
                //#line 2167 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(2);
                //#line 2169 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, TypeName.toString()));
                break;
            }
     
            //
            // Rule 246:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 246: {
                //#line 2173 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageOrTypeName = (Name) getRhsSym(2);
                //#line 2175 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, PackageOrTypeName.toString()));
                break;
            }
     
            //
            // Rule 250:  TypeDeclaration ::= ;
            //
            case 250: {
                
                //#line 2190 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(null);
                break;
            }
     
            //
            // Rule 251:  ClassModifiers ::= ClassModifier
            //
            case 251: {
                //#line 2196 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2198 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 252:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 252: {
                //#line 2203 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2203 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2205 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClassModifiers.addAll(ClassModifier);
                break;
            }
     
            //
            // Rule 253:  ClassModifier ::= Annotation
            //
            case 253: {
                //#line 2209 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2211 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 254:  ClassModifier ::= public
            //
            case 254: {
                
                //#line 2216 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 255:  ClassModifier ::= protected
            //
            case 255: {
                
                //#line 2221 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 256:  ClassModifier ::= private
            //
            case 256: {
                
                //#line 2226 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 257:  ClassModifier ::= abstract
            //
            case 257: {
                
                //#line 2231 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 258:  ClassModifier ::= static
            //
            case 258: {
                
                //#line 2236 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 259:  ClassModifier ::= final
            //
            case 259: {
                
                //#line 2241 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 260:  ClassModifier ::= strictfp
            //
            case 260: {
                
                //#line 2246 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                break;
            }
     
            //
            // Rule 261:  ClassModifier ::= safe
            //
            case 261: {
                
                //#line 2251 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                break;
            }
     
            //
            // Rule 262:  TypeDefModifiers ::= TypeDefModifier
            //
            case 262: {
                //#line 2255 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2257 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 263:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 263: {
                //#line 2262 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2262 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2264 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                break;
            }
     
            //
            // Rule 264:  TypeDefModifier ::= Annotation
            //
            case 264: {
                //#line 2268 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2270 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 265:  TypeDefModifier ::= public
            //
            case 265: {
                
                //#line 2275 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 266:  TypeDefModifier ::= protected
            //
            case 266: {
                
                //#line 2280 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 267:  TypeDefModifier ::= private
            //
            case 267: {
                
                //#line 2285 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 268:  TypeDefModifier ::= abstract
            //
            case 268: {
                
                //#line 2290 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 269:  TypeDefModifier ::= static
            //
            case 269: {
                
                //#line 2295 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 270:  TypeDefModifier ::= final
            //
            case 270: {
                
                //#line 2300 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 271:  Interfaces ::= implements InterfaceTypeList
            //
            case 271: {
                //#line 2307 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2309 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 272:  InterfaceTypeList ::= Type
            //
            case 272: {
                //#line 2313 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2315 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                break;
            }
     
            //
            // Rule 273:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 273: {
                //#line 2320 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2320 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2322 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 274:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 274: {
                //#line 2330 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2332 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                break;
            }
     
            //
            // Rule 276:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 276: {
                //#line 2337 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2337 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2339 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                break;
            }
     
            //
            // Rule 278:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 278: {
                //#line 2345 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block InstanceInitializer = (Block) getRhsSym(1);
                //#line 2347 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InstanceInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 279:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 279: {
                //#line 2352 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block StaticInitializer = (Block) getRhsSym(1);
                //#line 2354 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(StaticInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 280:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 280: {
                //#line 2359 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2361 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 282:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 282: {
                //#line 2368 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2370 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 283:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 283: {
                //#line 2375 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2377 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 284:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 284: {
                //#line 2382 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2384 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 285:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 285: {
                //#line 2389 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2391 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 286:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 286: {
                //#line 2396 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2398 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 287:  ClassMemberDeclaration ::= ;
            //
            case 287: {
                
                //#line 2405 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                break;
            }
     
            //
            // Rule 288:  FormalDeclarators ::= FormalDeclarator
            //
            case 288: {
                //#line 2410 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2412 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 289:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 289: {
                //#line 2417 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2417 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2419 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FormalDeclarators.add(FormalDeclarator);
                break;
            }
     
            //
            // Rule 290:  FieldDeclarators ::= FieldDeclarator
            //
            case 290: {
                //#line 2424 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2426 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 291:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 291: {
                //#line 2431 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2431 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2433 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                break;
            }
     
            //
            // Rule 292:  VariableDeclarators ::= VariableDeclarator
            //
            case 292: {
                //#line 2439 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2441 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 293:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 293: {
                //#line 2446 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2446 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2448 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                break;
            }
     
            //
            // Rule 295:  FieldModifiers ::= FieldModifier
            //
            case 295: {
                //#line 2455 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2457 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 296:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 296: {
                //#line 2462 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2462 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2464 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FieldModifiers.addAll(FieldModifier);
                break;
            }
     
            //
            // Rule 297:  FieldModifier ::= Annotation
            //
            case 297: {
                //#line 2468 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2470 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 298:  FieldModifier ::= public
            //
            case 298: {
                
                //#line 2475 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 299:  FieldModifier ::= protected
            //
            case 299: {
                
                //#line 2480 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 300:  FieldModifier ::= private
            //
            case 300: {
                
                //#line 2485 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 301:  FieldModifier ::= static
            //
            case 301: {
                
                //#line 2490 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 302:  FieldModifier ::= transient
            //
            case 302: {
                
                //#line 2495 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.TRANSIENT)));
                break;
            }
     
            //
            // Rule 303:  FieldModifier ::= volatile
            //
            case 303: {
                
                //#line 2500 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.VOLATILE)));
                break;
            }
     
            //
            // Rule 304:  ResultType ::= : Type
            //
            case 304: {
                //#line 2504 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2506 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Type);
                break;
            }
     
            //
            // Rule 305:  FormalParameters ::= ( FormalParameterList )
            //
            case 305: {
                //#line 2510 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2512 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(FormalParameterList);
                break;
            }
     
            //
            // Rule 306:  FormalParameterList ::= FormalParameter
            //
            case 306: {
                //#line 2516 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2518 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 307:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 307: {
                //#line 2523 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2523 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2525 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FormalParameterList.add(FormalParameter);
                break;
            }
     
            //
            // Rule 308:  LoopIndexDeclarator ::= Identifier ResultTypeopt
            //
            case 308: {
                //#line 2529 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2529 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2531 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, null });
                break;
            }
     
            //
            // Rule 309:  LoopIndexDeclarator ::= ( IdentifierList ) ResultTypeopt
            //
            case 309: {
                //#line 2534 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2534 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2536 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, null });
                break;
            }
     
            //
            // Rule 310:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt
            //
            case 310: {
                //#line 2539 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2539 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2539 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2541 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, null });
                break;
            }
     
            //
            // Rule 311:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 311: {
                //#line 2545 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2545 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2547 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            Formal f;
                        	FlagsNode fn = extractFlags(VariableModifiersopt, Flags.FINAL);
            Object[] o = LoopIndexDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
               List exploded = (List) o[2];
                        DepParameterExpr where = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                        if (type == null) type = nf.UnknownTypeNode(name.position());
                        List explodedFormals = new ArrayList();
                        for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                        	Id id = (Id) i.next();
                        	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(name.position()), id));
                        }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
            setResult(f);
                break;
            }
     
            //
            // Rule 312:  LoopIndex ::= VariableModifiersopt VarKeyword LoopIndexDeclarator
            //
            case 312: {
                //#line 2566 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2566 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2566 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2568 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            Formal f;
                        	FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
            Object[] o = LoopIndexDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
               List exploded = (List) o[2];
                        DepParameterExpr where = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                                                    if (type == null) type = nf.UnknownTypeNode(name.position());
                                                    List explodedFormals = new ArrayList();
                        for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                        	Id id = (Id) i.next();
                        	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(name.position()), id));
                        }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
            setResult(f);
                break;
            }
     
            //
            // Rule 313:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 313: {
                //#line 2588 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2588 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2590 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            Formal f;
                        	FlagsNode fn = extractFlags(VariableModifiersopt, Flags.FINAL);
            Object[] o = FormalDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
               List exploded = (List) o[2];
                        DepParameterExpr where = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                        if (type == null) type = nf.UnknownTypeNode(name.position());
                        Expr init = (Expr) o[5];
                        List explodedFormals = new ArrayList();
                        for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                        	Id id = (Id) i.next();
                        	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(name.position()), id));
                        }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
            setResult(f);
                break;
            }
     
            //
            // Rule 314:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 314: {
                //#line 2610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2612 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            Formal f;
                        	FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
            Object[] o = FormalDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
               List exploded = (List) o[2];
                        DepParameterExpr where = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                                                    if (type == null) type = nf.UnknownTypeNode(name.position());
                        Expr init = (Expr) o[5];
                                                    List explodedFormals = new ArrayList();
                        for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                        	Id id = (Id) i.next();
                        	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(name.position()), id));
                        }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
            setResult(f);
                break;
            }
     
            //
            // Rule 315:  FormalParameter ::= Type
            //
            case 315: {
                //#line 2632 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2634 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), ""), Collections.EMPTY_LIST);
            setResult(f);
                break;
            }
     
            //
            // Rule 316:  VariableModifiers ::= VariableModifier
            //
            case 316: {
                //#line 2640 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2642 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 317:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 317: {
                //#line 2647 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2647 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2649 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableModifiers.addAll(VariableModifier);
                break;
            }
     
            //
            // Rule 318:  VariableModifier ::= Annotation
            //
            case 318: {
                //#line 2653 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2655 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 319:  VariableModifier ::= shared
            //
            case 319: {
                
                //#line 2660 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                break;
            }
     
            //
            // Rule 320:  MethodModifiers ::= MethodModifier
            //
            case 320: {
                //#line 2667 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2669 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 321:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 321: {
                //#line 2674 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2674 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2676 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                MethodModifiers.addAll(MethodModifier);
                break;
            }
     
            //
            // Rule 322:  MethodModifier ::= Annotation
            //
            case 322: {
                //#line 2680 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2682 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 323:  MethodModifier ::= public
            //
            case 323: {
                
                //#line 2687 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 324:  MethodModifier ::= protected
            //
            case 324: {
                
                //#line 2692 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 325:  MethodModifier ::= private
            //
            case 325: {
                
                //#line 2697 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 326:  MethodModifier ::= abstract
            //
            case 326: {
                
                //#line 2702 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 327:  MethodModifier ::= static
            //
            case 327: {
                
                //#line 2707 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 328:  MethodModifier ::= final
            //
            case 328: {
                
                //#line 2712 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 329:  MethodModifier ::= native
            //
            case 329: {
                
                //#line 2717 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                break;
            }
     
            //
            // Rule 330:  MethodModifier ::= strictfp
            //
            case 330: {
                
                //#line 2722 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                break;
            }
     
            //
            // Rule 331:  MethodModifier ::= atomic
            //
            case 331: {
                
                //#line 2727 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                break;
            }
     
            //
            // Rule 332:  MethodModifier ::= extern
            //
            case 332: {
                
                //#line 2732 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                break;
            }
     
            //
            // Rule 333:  MethodModifier ::= safe
            //
            case 333: {
                
                //#line 2737 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                break;
            }
     
            //
            // Rule 334:  MethodModifier ::= sequential
            //
            case 334: {
                
                //#line 2742 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                break;
            }
     
            //
            // Rule 335:  MethodModifier ::= local
            //
            case 335: {
                
                //#line 2747 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.LOCAL)));
                break;
            }
     
            //
            // Rule 336:  MethodModifier ::= nonblocking
            //
            case 336: {
                
                //#line 2752 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                break;
            }
     
            //
            // Rule 337:  MethodModifier ::= incomplete
            //
            case 337: {
                
                //#line 2757 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                break;
            }
     
            //
            // Rule 338:  MethodModifier ::= property
            //
            case 338: {
                
                //#line 2762 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                break;
            }
     
            //
            // Rule 339:  Throws ::= throws ExceptionTypeList
            //
            case 339: {
                //#line 2767 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 2769 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 340:  ExceptionTypeList ::= ExceptionType
            //
            case 340: {
                //#line 2773 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 2775 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                break;
            }
     
            //
            // Rule 341:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 341: {
                //#line 2780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 2780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 2782 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExceptionTypeList.add(ExceptionType);
                break;
            }
     
            //
            // Rule 343:  MethodBody ::= = LastExpression ;
            //
            case 343: {
                //#line 2788 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 2790 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Block(pos(), LastExpression));
                break;
            }
     
            //
            // Rule 344:  MethodBody ::= = { BlockStatementsopt LastExpression }
            //
            case 344: {
                //#line 2793 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2793 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2795 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 345:  MethodBody ::= = Block
            //
            case 345: {
                //#line 2801 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2803 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 346:  MethodBody ::= ;
            //
            case 346:
                setResult(null);
                break;
 
            //
            // Rule 347:  InstanceInitializer ::= Block
            //
            case 347: {
                //#line 2809 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(1);
                //#line 2811 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(), Flags.NONE), Block));
                break;
            }
     
            //
            // Rule 348:  StaticInitializer ::= static Block
            //
            case 348: {
                //#line 2815 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2817 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(getLeftSpan()), Flags.STATIC), Block));
                break;
            }
     
            //
            // Rule 349:  SimpleTypeName ::= Identifier
            //
            case 349: {
                //#line 2821 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2823 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 350:  ConstructorModifiers ::= ConstructorModifier
            //
            case 350: {
                //#line 2827 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 2829 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 351:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 351: {
                //#line 2834 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 2834 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 2836 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                break;
            }
     
            //
            // Rule 352:  ConstructorModifier ::= Annotation
            //
            case 352: {
                //#line 2840 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2842 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 353:  ConstructorModifier ::= public
            //
            case 353: {
                
                //#line 2847 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 354:  ConstructorModifier ::= protected
            //
            case 354: {
                
                //#line 2852 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 355:  ConstructorModifier ::= private
            //
            case 355: {
                
                //#line 2857 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 356:  ConstructorBody ::= = ConstructorBlock
            //
            case 356: {
                //#line 2861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 2863 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ConstructorBlock);
                break;
            }
     
            //
            // Rule 357:  ConstructorBody ::= ;
            //
            case 357:
                setResult(null);
                break;
 
            //
            // Rule 358:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 358: {
                //#line 2869 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 2869 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2871 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 359:  ConstructorBlock ::= ExplicitConstructorInvocation
            //
            case 359: {
                //#line 2885 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(1);
                //#line 2887 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 360:  ConstructorBlock ::= AssignPropertyCall
            //
            case 360: {
                //#line 2893 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(1);
                //#line 2895 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 361:  Arguments ::= ( ArgumentListopt )
            //
            case 361: {
                //#line 2902 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 2904 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ArgumentListopt);
                break;
            }
     
            //
            // Rule 363:  InterfaceModifiers ::= InterfaceModifier
            //
            case 363: {
                //#line 2912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 2914 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 364:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 364: {
                //#line 2919 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 2919 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 2921 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                break;
            }
     
            //
            // Rule 365:  InterfaceModifier ::= Annotation
            //
            case 365: {
                //#line 2925 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2927 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 366:  InterfaceModifier ::= public
            //
            case 366: {
                
                //#line 2932 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 367:  InterfaceModifier ::= protected
            //
            case 367: {
                
                //#line 2937 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 368:  InterfaceModifier ::= private
            //
            case 368: {
                
                //#line 2942 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 369:  InterfaceModifier ::= abstract
            //
            case 369: {
                
                //#line 2947 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 370:  InterfaceModifier ::= static
            //
            case 370: {
                
                //#line 2952 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 371:  InterfaceModifier ::= strictfp
            //
            case 371: {
                
                //#line 2957 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                break;
            }
     
            //
            // Rule 372:  ExtendsInterfaces ::= extends Type
            //
            case 372: {
                //#line 2961 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2963 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                break;
            }
     
            //
            // Rule 373:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 373: {
                //#line 2968 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 2968 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2970 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExtendsInterfaces.add(Type);
                break;
            }
     
            //
            // Rule 374:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 374: {
                //#line 2977 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 2979 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                break;
            }
     
            //
            // Rule 376:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 376: {
                //#line 2984 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 2984 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 2986 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                break;
            }
     
            //
            // Rule 377:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 377: {
                //#line 2991 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2993 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 378:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 378: {
                //#line 2998 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3000 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 379:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 379: {
                //#line 3005 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3007 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 380:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 380: {
                //#line 3012 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3014 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 381:  InterfaceMemberDeclaration ::= ;
            //
            case 381: {
                
                //#line 3021 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 382:  Annotations ::= Annotation
            //
            case 382: {
                //#line 3025 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3027 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                break;
            }
     
            //
            // Rule 383:  Annotations ::= Annotations Annotation
            //
            case 383: {
                //#line 3032 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3032 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3034 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Annotations.add(Annotation);
                break;
            }
     
            //
            // Rule 384:  Annotation ::= @ NamedType
            //
            case 384: {
                //#line 3038 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3040 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                break;
            }
     
            //
            // Rule 385:  SimpleName ::= Identifier
            //
            case 385: {
                //#line 3044 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3046 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 386:  Identifier ::= identifier
            //
            case 386: {
                //#line 3050 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3052 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 387:  ArrayInitializer ::= [ VariableInitializersopt ,opt$opt ]
            //
            case 387: {
                //#line 3058 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializersopt = (List) getRhsSym(2);
                //#line 3058 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object opt = (Object) getRhsSym(3);
                //#line 3060 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (VariableInitializersopt == null)
                     setResult(nf.ArrayInit(pos()));
                else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
                break;
            }
     
            //
            // Rule 388:  VariableInitializers ::= VariableInitializer
            //
            case 388: {
                //#line 3066 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3068 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 389:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 389: {
                //#line 3073 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3073 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3075 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                break;
            }
     
            //
            // Rule 390:  Block ::= { BlockStatementsopt }
            //
            case 390: {
                //#line 3091 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3093 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                break;
            }
     
            //
            // Rule 391:  BlockStatements ::= BlockStatement
            //
            case 391: {
                //#line 3097 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3099 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                break;
            }
     
            //
            // Rule 392:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 392: {
                //#line 3104 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3104 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3106 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                break;
            }
     
            //
            // Rule 394:  BlockStatement ::= ClassDeclaration
            //
            case 394: {
                //#line 3112 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3114 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                break;
            }
     
            //
            // Rule 395:  BlockStatement ::= Statement
            //
            case 395: {
                //#line 3119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3121 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                break;
            }
     
            //
            // Rule 396:  IdentifierList ::= Identifier
            //
            case 396: {
                //#line 3127 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3129 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 397:  IdentifierList ::= IdentifierList , Identifier
            //
            case 397: {
                //#line 3134 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3134 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3136 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                IdentifierList.add(Identifier);
                break;
            }
     
            //
            // Rule 398:  FormalDeclarator ::= Identifier : Type
            //
            case 398: {
                //#line 3140 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3140 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3142 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, Type, null });
                break;
            }
     
            //
            // Rule 399:  FormalDeclarator ::= ( IdentifierList ) : Type
            //
            case 399: {
                //#line 3145 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3145 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(5);
                //#line 3147 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, Type, null });
                break;
            }
     
            //
            // Rule 400:  FormalDeclarator ::= Identifier ( IdentifierList ) : Type
            //
            case 400: {
                //#line 3150 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3150 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3150 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(6);
                //#line 3152 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, Type, null });
                break;
            }
     
            //
            // Rule 401:  FieldDeclarator ::= Identifier WhereClauseopt : Type
            //
            case 401: {
                //#line 3156 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3156 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 3156 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(4);
                //#line 3158 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, WhereClauseopt, Type, null });
                break;
            }
     
            //
            // Rule 402:  FieldDeclarator ::= Identifier WhereClauseopt ResultTypeopt = VariableInitializer
            //
            case 402: {
                //#line 3161 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3161 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 3161 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 3161 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(5);
                //#line 3163 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, WhereClauseopt, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 403:  VariableDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 403: {
                //#line 3167 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3167 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3167 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3169 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 404:  VariableDeclarator ::= ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 404: {
                //#line 3172 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3172 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3172 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3174 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 405:  VariableDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 405: {
                //#line 3177 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3177 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3177 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3177 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3179 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 407:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 407: {
                //#line 3185 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3185 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3185 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3187 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
    
                List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                List s = new TypedList(new LinkedList(), Stmt.class, false);
                    for (Iterator i = VariableDeclarators.iterator(); i.hasNext(); )
                    {
                        Object[] o = (Object[]) i.next();
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        List exploded = (List) o[2];
                        DepParameterExpr where = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                                                    if (type == null) type = nf.UnknownTypeNode(name.position());
                        Expr init = (Expr) o[5];
                        LocalDecl ld = nf.LocalDecl(pos, fn,
                                           type, name, init);
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(VariableModifiersopt));
                                                                                List explodedFormals = new ArrayList();
                                                                                int index = 0;
                        for (Iterator j = exploded.iterator(); j.hasNext(); ) {
                        	Id id = (Id) j.next();
                        	explodedFormals.add(nf.LocalDecl(id.position(), fn, nf.UnknownTypeNode(name.position()), id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name), Collections.EMPTY_LIST, Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
                        	index++;
                        }
                        l.add(ld);
                    }
                l.addAll(s); 
                setResult(l);
                break;
            }
     
            //
            // Rule 408:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 408: {
                //#line 3217 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3217 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3217 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3219 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
    
                List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                List s = new TypedList(new LinkedList(), Stmt.class, false);
                    for (Iterator i = FormalDeclarators.iterator(); i.hasNext(); )
                    {
                        Object[] o = (Object[]) i.next();
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        List exploded = (List) o[2];
                        DepParameterExpr where = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                                                    if (type == null) type = nf.UnknownTypeNode(name.position());
                        Expr init = (Expr) o[5];
                        LocalDecl ld = nf.LocalDecl(pos, fn,
                                           type, name, init);
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(VariableModifiersopt));
                                                                                List explodedFormals = new ArrayList();
                                                                                int index = 0;
                        for (Iterator j = exploded.iterator(); j.hasNext(); ) {
                        	Id id = (Id) j.next();
                        	explodedFormals.add(nf.LocalDecl(id.position(), fn, nf.UnknownTypeNode(name.position()), id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name), Collections.EMPTY_LIST, Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
                        	index++;
                        }
                        l.add(ld);
                    }
                l.addAll(s); 
                setResult(l);
                break;
            }
     
            //
            // Rule 411:  Primary ::= TypeName . class
            //
            case 411: {
                //#line 3259 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3261 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (TypeName instanceof Name)
                {
                    Name a = (Name) TypeName;
                    setResult(nf.ClassLit(pos(), a.toType()));
                }
                else assert(false);
                break;
            }
     
            //
            // Rule 412:  Primary ::= self
            //
            case 412: {
                
                //#line 3271 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Self(pos()));
                break;
            }
     
            //
            // Rule 413:  Primary ::= this
            //
            case 413: {
                
                //#line 3276 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 414:  Primary ::= ClassName . this
            //
            case 414: {
                //#line 3279 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3281 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 415:  Primary ::= ( Expression )
            //
            case 415: {
                //#line 3284 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3286 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ParExpr(pos(), Expression));
                break;
            }
     
            //
            // Rule 421:  OperatorFunction ::= TypeName . +
            //
            case 421: {
                //#line 3295 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3297 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.ADD, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 422:  OperatorFunction ::= TypeName . -
            //
            case 422: {
                //#line 3306 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3308 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SUB, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 423:  OperatorFunction ::= TypeName . *
            //
            case 423: {
                //#line 3317 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3319 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MUL, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 424:  OperatorFunction ::= TypeName . /
            //
            case 424: {
                //#line 3328 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3330 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.DIV, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 425:  OperatorFunction ::= TypeName . %
            //
            case 425: {
                //#line 3339 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3341 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MOD, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 426:  OperatorFunction ::= TypeName . &
            //
            case 426: {
                //#line 3350 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3352 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_AND, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 427:  OperatorFunction ::= TypeName . |
            //
            case 427: {
                //#line 3361 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3363 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_OR, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 428:  OperatorFunction ::= TypeName . ^
            //
            case 428: {
                //#line 3372 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3374 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_XOR, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 429:  OperatorFunction ::= TypeName . <<
            //
            case 429: {
                //#line 3383 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3385 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHL, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 430:  OperatorFunction ::= TypeName . >>
            //
            case 430: {
                //#line 3394 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3396 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHR, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 431:  OperatorFunction ::= TypeName . >>>
            //
            case 431: {
                //#line 3405 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3407 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.USHR, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 432:  OperatorFunction ::= TypeName . <
            //
            case 432: {
                //#line 3416 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3418 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LT, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 433:  OperatorFunction ::= TypeName . <=
            //
            case 433: {
                //#line 3427 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3429 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LE, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 434:  OperatorFunction ::= TypeName . >=
            //
            case 434: {
                //#line 3438 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3440 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GE, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 435:  OperatorFunction ::= TypeName . >
            //
            case 435: {
                //#line 3449 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3451 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GT, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 436:  OperatorFunction ::= TypeName . ==
            //
            case 436: {
                //#line 3460 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3462 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.EQ, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 437:  OperatorFunction ::= TypeName . !=
            //
            case 437: {
                //#line 3471 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3473 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.NE, nf.Local(pos(), nf.Id(pos(), "y")))))));
                break;
            }
     
            //
            // Rule 438:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 438: {
                //#line 3484 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                //#line 3486 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 439:  Literal ::= LongLiteral$LongLiteral
            //
            case 439: {
                //#line 3490 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken LongLiteral = (IToken) getRhsIToken(1);
                //#line 3492 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 440:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 440: {
                //#line 3496 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                //#line 3498 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 441:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 441: {
                //#line 3502 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                //#line 3504 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 442:  Literal ::= BooleanLiteral
            //
            case 442: {
                //#line 3508 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3510 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 443:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 443: {
                //#line 3513 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                //#line 3515 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 444:  Literal ::= StringLiteral$str
            //
            case 444: {
                //#line 3519 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3521 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 445:  Literal ::= null
            //
            case 445: {
                
                //#line 3527 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 446:  BooleanLiteral ::= true$trueLiteral
            //
            case 446: {
                //#line 3531 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3533 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 447:  BooleanLiteral ::= false$falseLiteral
            //
            case 447: {
                //#line 3536 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3538 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 448:  ArgumentList ::= Expression
            //
            case 448: {
                //#line 3545 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3547 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                break;
            }
     
            //
            // Rule 449:  ArgumentList ::= ArgumentList , Expression
            //
            case 449: {
                //#line 3552 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3552 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3554 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ArgumentList.add(Expression);
                break;
            }
     
            //
            // Rule 450:  FieldAccess ::= Primary . Identifier
            //
            case 450: {
                //#line 3558 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3558 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3560 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                break;
            }
     
            //
            // Rule 451:  FieldAccess ::= super . Identifier
            //
            case 451: {
                //#line 3563 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3565 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), Identifier));
                break;
            }
     
            //
            // Rule 452:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 452: {
                //#line 3568 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3568 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3568 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3570 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                break;
            }
     
            //
            // Rule 453:  FieldAccess ::= Primary . class$c
            //
            case 453: {
                //#line 3573 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3573 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3575 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                break;
            }
     
            //
            // Rule 454:  FieldAccess ::= super . class$c
            //
            case 454: {
                //#line 3578 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3580 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                break;
            }
     
            //
            // Rule 455:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 455: {
                //#line 3583 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3583 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3583 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 3585 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                break;
            }
     
            //
            // Rule 456:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 456: {
                //#line 3589 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name MethodName = (Name) getRhsSym(1);
                //#line 3589 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3589 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3591 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 457:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 457: {
                //#line 3596 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3596 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3596 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3596 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3598 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 458:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 458: {
                //#line 3601 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3601 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3601 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3603 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 459:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 459: {
                //#line 3606 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3606 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3606 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3606 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3606 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3608 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 460:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 460: {
                //#line 3611 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3611 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3611 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3613 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (Primary instanceof Field) {
                    Field f = (Field) Primary;
                    setResult(nf.X10Call(pos(), f.target(), f.name(), TypeArgumentsopt, ArgumentListopt));
                }
                else if (Primary instanceof AmbExpr) {
                    AmbExpr f = (AmbExpr) Primary;
                    setResult(nf.X10Call(pos(), null, f.name(), TypeArgumentsopt, ArgumentListopt));
                }
                else {
                    setResult(nf.ClosureCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                }
                break;
            }
     
            //
            // Rule 461:  MethodSelection ::= MethodName . TypeParametersopt ( FormalParameterListopt )
            //
            case 461: {
                //#line 3627 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name MethodName = (Name) getRhsSym(1);
                //#line 3627 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 3627 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(5);
                //#line 3629 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(), nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, typeArgs, actuals)))));
                break;
            }
     
            //
            // Rule 462:  MethodSelection ::= Primary . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 462: {
                //#line 3640 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3640 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3640 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3640 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3642 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(),
                                               nf.X10Call(pos(), Primary, Identifier, typeArgs, actuals)))));
                break;
            }
     
            //
            // Rule 463:  MethodSelection ::= super . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 463: {
                //#line 3652 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3652 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3652 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3654 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, typeArgs, actuals)))));
                break;
            }
     
            //
            // Rule 464:  MethodSelection ::= ClassName . super$sup . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 464: {
                //#line 3664 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3664 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3664 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3664 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(7);
                //#line 3664 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(9);
                //#line 3666 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, typeArgs, actuals)))));
                break;
            }
     
            //
            // Rule 467:  PostfixExpression ::= ExpressionName
            //
            case 467: {
                //#line 3679 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 3681 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 470:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 470: {
                //#line 3687 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 3689 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 471:  PostDecrementExpression ::= PostfixExpression --
            //
            case 471: {
                //#line 3693 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 3695 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 474:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 474: {
                //#line 3701 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3703 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 475:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 475: {
                //#line 3706 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3708 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 477:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 477: {
                //#line 3713 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3715 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 478:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 478: {
                //#line 3719 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3721 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 480:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 480: {
                //#line 3726 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 3728 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 481:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 481: {
                //#line 3731 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3731 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 3733 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e);
                break;
            }
     
            //
            // Rule 482:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 482: {
                //#line 3738 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 3740 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 484:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 484: {
                //#line 3745 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 3745 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 3747 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                break;
            }
     
            //
            // Rule 485:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 485: {
                //#line 3750 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 3750 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 3752 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                break;
            }
     
            //
            // Rule 486:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 486: {
                //#line 3755 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 3755 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 3757 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                break;
            }
     
            //
            // Rule 488:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 488: {
                //#line 3762 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 3762 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 3764 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 489:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 489: {
                //#line 3767 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 3767 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 3769 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 491:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 491: {
                //#line 3774 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 3774 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 3776 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                break;
            }
     
            //
            // Rule 492:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 492: {
                //#line 3779 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 3779 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 3781 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 493:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 493: {
                //#line 3784 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 3784 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 3786 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 494:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 494: {
                //#line 3790 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 3790 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 3792 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                break;
            }
     
            //
            // Rule 498:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 498: {
                //#line 3800 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3800 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3802 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
                break;
            }
     
            //
            // Rule 499:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 499: {
                //#line 3805 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3805 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3807 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
                break;
            }
     
            //
            // Rule 500:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 500: {
                //#line 3810 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3810 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3812 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
                break;
            }
     
            //
            // Rule 501:  RelationalExpression ::= RelationalExpression >= ShiftExpression
            //
            case 501: {
                //#line 3815 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3815 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3817 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
                break;
            }
     
            //
            // Rule 502:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 502: {
                //#line 3820 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3820 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3822 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                break;
            }
     
            //
            // Rule 503:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 503: {
                //#line 3825 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3825 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3827 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                break;
            }
     
            //
            // Rule 505:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 505: {
                //#line 3832 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 3832 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 3834 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                break;
            }
     
            //
            // Rule 506:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 506: {
                //#line 3837 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 3837 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 3839 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                break;
            }
     
            //
            // Rule 507:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 507: {
                //#line 3842 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 3842 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 3844 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                break;
            }
     
            //
            // Rule 509:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 509: {
                //#line 3849 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 3849 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 3851 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                break;
            }
     
            //
            // Rule 511:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 511: {
                //#line 3856 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 3856 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 3858 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                break;
            }
     
            //
            // Rule 513:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 513: {
                //#line 3863 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 3863 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 3865 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 515:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 515: {
                //#line 3870 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 3870 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 3872 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                break;
            }
     
            //
            // Rule 517:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 517: {
                //#line 3877 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 3877 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 3879 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                break;
            }
     
            //
            // Rule 522:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 522: {
                //#line 3888 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 3888 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3888 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 3890 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 525:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 525: {
                //#line 3897 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 3897 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 3897 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 3899 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 526:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 526: {
                //#line 3902 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name e1 = (Name) getRhsSym(1);
                //#line 3902 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 3902 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 3902 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 3904 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 527:  LeftHandSide ::= ExpressionName
            //
            case 527: {
                //#line 3908 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 3910 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 529:  AssignmentOperator ::= =
            //
            case 529: {
                
                //#line 3917 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 530:  AssignmentOperator ::= *=
            //
            case 530: {
                
                //#line 3922 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 531:  AssignmentOperator ::= /=
            //
            case 531: {
                
                //#line 3927 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 532:  AssignmentOperator ::= %=
            //
            case 532: {
                
                //#line 3932 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 533:  AssignmentOperator ::= +=
            //
            case 533: {
                
                //#line 3937 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 534:  AssignmentOperator ::= -=
            //
            case 534: {
                
                //#line 3942 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 535:  AssignmentOperator ::= <<=
            //
            case 535: {
                
                //#line 3947 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 536:  AssignmentOperator ::= >>=
            //
            case 536: {
                
                //#line 3952 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 537:  AssignmentOperator ::= >>>=
            //
            case 537: {
                
                //#line 3957 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 538:  AssignmentOperator ::= &=
            //
            case 538: {
                
                //#line 3962 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 539:  AssignmentOperator ::= ^=
            //
            case 539: {
                
                //#line 3967 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 540:  AssignmentOperator ::= |=
            //
            case 540: {
                
                //#line 3972 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 543:  Catchesopt ::= $Empty
            //
            case 543: {
                
                //#line 3985 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 545:  Identifieropt ::= $Empty
            //
            case 545:
                setResult(null);
                break;
 
            //
            // Rule 546:  Identifieropt ::= Identifier
            //
            case 546: {
                //#line 3992 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3994 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Identifier);
                break;
            }
     
            //
            // Rule 547:  ForUpdateopt ::= $Empty
            //
            case 547: {
                
                //#line 4000 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 549:  Expressionopt ::= $Empty
            //
            case 549:
                setResult(null);
                break;
 
            //
            // Rule 551:  ForInitopt ::= $Empty
            //
            case 551: {
                
                //#line 4011 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 553:  SwitchLabelsopt ::= $Empty
            //
            case 553: {
                
                //#line 4018 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 555:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 555: {
                
                //#line 4025 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 557:  VariableModifiersopt ::= $Empty
            //
            case 557: {
                
                //#line 4032 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 559:  VariableInitializersopt ::= $Empty
            //
            case 559:
                setResult(null);
                break;
 
            //
            // Rule 561:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 561: {
                
                //#line 4043 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 563:  ExtendsInterfacesopt ::= $Empty
            //
            case 563: {
                
                //#line 4050 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 565:  InterfaceModifiersopt ::= $Empty
            //
            case 565: {
                
                //#line 4057 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 567:  ClassBodyopt ::= $Empty
            //
            case 567:
                setResult(null);
                break;
 
            //
            // Rule 569:  Argumentsopt ::= $Empty
            //
            case 569: {
                
                //#line 4068 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 571:  ArgumentListopt ::= $Empty
            //
            case 571: {
                
                //#line 4075 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 573:  BlockStatementsopt ::= $Empty
            //
            case 573: {
                
                //#line 4082 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 575:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 575:
                setResult(null);
                break;
 
            //
            // Rule 577:  ConstructorModifiersopt ::= $Empty
            //
            case 577: {
                
                //#line 4093 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 579:  FormalParameterListopt ::= $Empty
            //
            case 579: {
                
                //#line 4100 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 581:  Throwsopt ::= $Empty
            //
            case 581: {
                
                //#line 4107 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 583:  MethodModifiersopt ::= $Empty
            //
            case 583: {
                
                //#line 4114 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 585:  FieldModifiersopt ::= $Empty
            //
            case 585: {
                
                //#line 4121 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 587:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 587: {
                
                //#line 4128 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 589:  Interfacesopt ::= $Empty
            //
            case 589: {
                
                //#line 4135 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 591:  Superopt ::= $Empty
            //
            case 591: {
                
                //#line 4142 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
               setResult(nf.TypeNodeFromQualifiedName(pos(), "x10.lang.Object"));
                break;
            }
     
            //
            // Rule 593:  TypeParametersopt ::= $Empty
            //
            case 593: {
                
                //#line 4149 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                break;
            }
     
            //
            // Rule 595:  FormalParametersopt ::= $Empty
            //
            case 595: {
                
                //#line 4156 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 597:  Annotationsopt ::= $Empty
            //
            case 597: {
                
                //#line 4163 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                break;
            }
     
            //
            // Rule 599:  TypeDeclarationsopt ::= $Empty
            //
            case 599: {
                
                //#line 4170 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 601:  ImportDeclarationsopt ::= $Empty
            //
            case 601: {
                
                //#line 4177 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 603:  PackageDeclarationopt ::= $Empty
            //
            case 603:
                setResult(null);
                break;
 
            //
            // Rule 605:  ResultTypeopt ::= $Empty
            //
            case 605:
                setResult(null);
                break;
 
            //
            // Rule 607:  TypeArgumentsopt ::= $Empty
            //
            case 607: {
                
                //#line 4192 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 609:  TypePropertiesopt ::= $Empty
            //
            case 609: {
                
                //#line 4199 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypePropertyNode.class, false));
                break;
            }
     
            //
            // Rule 611:  Propertiesopt ::= $Empty
            //
            case 611: {
                
                //#line 4206 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                break;
            }
     
            //
            // Rule 613:  ,opt ::= $Empty
            //
            case 613:
                setResult(null);
                break;

    
            default:
                break;
        }
        return;
    }
}

