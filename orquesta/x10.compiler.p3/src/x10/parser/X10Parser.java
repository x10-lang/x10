
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
            this.leftIToken = leftToken;
            this.rightIToken = rightToken;
        }

        public IToken getLeftIToken() { return leftIToken; }
        public IToken getRightIToken() { return rightIToken; }

        public String toText()
        {
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
                //#line 860 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 860 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 860 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 860 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 860 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 860 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 862 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                //#line 872 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 872 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 872 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 872 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 872 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 874 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                //#line 886 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 888 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
   setResult(PropertyList);
           break;
            }  
            //
            // Rule 19:  PropertyList ::= Property
            //
            case 19: {
                //#line 891 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 893 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                break;
            }
     
            //
            // Rule 20:  PropertyList ::= PropertyList , Property
            //
            case 20: {
                //#line 898 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 898 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 900 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                PropertyList.add(Property);
                break;
            }
     
            //
            // Rule 21:  Property ::= Annotationsopt Identifier : Type
            //
            case 21: {
                //#line 905 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 905 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 905 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(4);
                //#line 907 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), Type, Identifier));
                break;
            }
     
            //
            // Rule 22:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 22: {
                //#line 911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 913 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 23:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 23: {
                //#line 943 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 943 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 945 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 24:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 24: {
                //#line 948 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 948 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 950 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 25:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 25: {
                //#line 953 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 953 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 953 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 955 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 26:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 26: {
                //#line 958 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 958 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 958 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 960 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 27:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypePropertiesopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 27: {
                //#line 964 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 964 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 964 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 964 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 964 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 964 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 964 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 966 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 28:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 28: {
                //#line 984 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(2);
                //#line 984 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 984 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 984 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 986 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 29:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 29: {
                //#line 991 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 991 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 991 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 991 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 991 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 993 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Name b = new X10Name(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 30:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 30: {
                //#line 999 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 999 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 999 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 999 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 999 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1001 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Name b = new X10Name(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 31:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 31: {
                //#line 1008 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1008 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1010 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 34:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt => Type
            //
            case 34: {
                //#line 1020 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1020 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1020 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1020 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1020 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1022 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt));
                break;
            }
     
            //
            // Rule 39:  AnnotatedType ::= Type Annotations
            //
            case 39: {
                //#line 1029 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1029 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1031 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn);
                break;
            }
     
            //
            // Rule 43:  ConstrainedType ::= ( Type )
            //
            case 43: {
                //#line 1040 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1042 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Type);
                break;
            }
     
            //
            // Rule 48:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt PlaceTypeSpecifieropt
            //
            case 48: {
                //#line 1065 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1065 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1065 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1065 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1065 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1065 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(7);
                //#line 1067 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            TypeNode type = nf.X10AmbTypeNode(pos(), Primary, Identifier);
            // TODO: place constraint
            if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                type = nf.AmbDepTypeNode(pos(), Primary, Identifier, TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                break;
            }
     
            //
            // Rule 49:  NamedType ::= Primary . class$c TypeArgumentsopt Argumentsopt DepParametersopt PlaceTypeSpecifieropt
            //
            case 49: {
                //#line 1076 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1076 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 1076 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1076 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1076 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1076 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(7);
                //#line 1078 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            TypeNode type = nf.X10AmbTypeNode(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class"));
            // TODO: place constraint
            if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                type = nf.AmbDepTypeNode(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class"), TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                break;
            }
     
            //
            // Rule 50:  NamedType ::= TypeName TypeArgumentsopt Argumentsopt DepParametersopt PlaceTypeSpecifieropt
            //
            case 50: {
                //#line 1087 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 1087 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1087 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1087 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1087 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(5);
                //#line 1089 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 51:  DepParameters ::= { Conjunction }
            //
            case 51: {
                //#line 1112 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(2);
                //#line 1114 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.DepParameterExpr(pos(), Collections.EMPTY_LIST, Conjunction));
                break;
            }
     
            //
            // Rule 52:  DepParameters ::= { ExistentialList ; Conjunction }
            //
            case 52: {
                //#line 1117 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(2);
                //#line 1117 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(4);
                //#line 1119 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialList, Conjunction));
                break;
            }
     
            //
            // Rule 53:  TypeProperties ::= [ TypePropertyList ]
            //
            case 53: {
                //#line 1123 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertyList = (List) getRhsSym(2);
                //#line 1125 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(TypePropertyList);
                break;
            }
     
            //
            // Rule 54:  TypeParameters ::= [ TypeParameterList ]
            //
            case 54: {
                //#line 1129 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1131 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(TypeParameterList);
                break;
            }
     
            //
            // Rule 55:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 55: {
                //#line 1134 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1136 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(FormalParameterListopt);
                break;
            }
     
            //
            // Rule 56:  Conjunction ::= Expression
            //
            case 56: {
                //#line 1140 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1142 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Expression);
                break;
            }
     
            //
            // Rule 57:  Conjunction ::= Conjunction , Expression
            //
            case 57: {
                //#line 1145 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(1);
                //#line 1145 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1147 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), Conjunction, Binary.COND_AND, Expression));
                break;
            }
     
            //
            // Rule 58:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 58: {
                //#line 1151 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1151 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1153 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                break;
            }
     
            //
            // Rule 59:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 59: {
                //#line 1156 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1156 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1158 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                break;
            }
     
            //
            // Rule 60:  WhereClause ::= DepParameters
            //
            case 60: {
                //#line 1162 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1164 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(DepParameters);
                break;
            }
       
            //
            // Rule 61:  ExistentialListopt ::= $Empty
            //
            case 61: {
                
                //#line 1170 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(new ArrayList());
                break;
            }
       
            //
            // Rule 62:  ExistentialListopt ::= ExistentialList
            //
            case 62: {
                //#line 1173 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1175 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(ExistentialList);
                break;
            }
     
            //
            // Rule 63:  ExistentialList ::= FormalParameter
            //
            case 63: {
                //#line 1179 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1181 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                setResult(l);
                break;
            }
     
            //
            // Rule 64:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 64: {
                //#line 1186 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1186 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1188 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 67:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 67: {
                //#line 1197 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1197 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1197 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 1197 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1197 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1197 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1197 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1197 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1199 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 68:  ValueClassDeclaration ::= ClassModifiersopt value Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
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
            // Rule 69:  ValueClassDeclaration ::= ClassModifiersopt value class Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 69: {
                //#line 1226 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1226 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1226 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(5);
                //#line 1226 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(6);
                //#line 1226 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1226 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(8);
                //#line 1226 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(9);
                //#line 1226 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(10);
                //#line 1228 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 70:  ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt ConstructorBody
            //
            case 70: {
                //#line 1239 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1239 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1239 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1239 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1239 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1239 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1239 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(9);
                //#line 1241 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 71:  Super ::= extends ClassType
            //
            case 71: {
                //#line 1255 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1257 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ClassType);
                break;
            }
     
            //
            // Rule 72:  FieldKeyword ::= val
            //
            case 72: {
                
                //#line 1263 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 73:  FieldKeyword ::= var
            //
            case 73: {
                
                //#line 1268 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                break;
            }
     
            //
            // Rule 74:  FieldKeyword ::= const
            //
            case 74: {
                
                //#line 1273 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                break;
            }
     
            //
            // Rule 75:  VarKeyword ::= val
            //
            case 75: {
                
                //#line 1281 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 76:  VarKeyword ::= var
            //
            case 76: {
                
                //#line 1286 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                break;
            }
     
            //
            // Rule 77:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 77: {
                //#line 1291 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1291 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1291 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1293 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 107:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 107: {
                //#line 1348 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1348 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1350 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.If(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 108:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 108: {
                //#line 1354 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1354 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1354 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1356 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                break;
            }
     
            //
            // Rule 109:  EmptyStatement ::= ;
            //
            case 109: {
                
                //#line 1362 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 110:  LabeledStatement ::= Identifier : Statement
            //
            case 110: {
                //#line 1366 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1366 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1368 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Labeled(pos(), Identifier, Statement));
                break;
            }
     
            //
            // Rule 111:  ExpressionStatement ::= StatementExpression ;
            //
            case 111: {
                //#line 1372 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1374 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 119:  AssertStatement ::= assert Expression ;
            //
            case 119: {
                //#line 1403 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1405 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assert(pos(), Expression));
                break;
            }
     
            //
            // Rule 120:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 120: {
                //#line 1408 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1408 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1410 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                break;
            }
     
            //
            // Rule 121:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 121: {
                //#line 1414 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1414 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1416 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                break;
            }
     
            //
            // Rule 122:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 122: {
                //#line 1420 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1420 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1422 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                break;
            }
     
            //
            // Rule 124:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 124: {
                //#line 1428 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1428 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1430 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                break;
            }
     
            //
            // Rule 125:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 125: {
                //#line 1435 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1435 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1437 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                break;
            }
     
            //
            // Rule 126:  SwitchLabels ::= SwitchLabel
            //
            case 126: {
                //#line 1444 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1446 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                break;
            }
     
            //
            // Rule 127:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 127: {
                //#line 1451 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1451 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1453 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                break;
            }
     
            //
            // Rule 128:  SwitchLabel ::= case ConstantExpression :
            //
            case 128: {
                //#line 1458 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1460 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                break;
            }
     
            //
            // Rule 129:  SwitchLabel ::= default :
            //
            case 129: {
                
                //#line 1465 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 130:  WhileStatement ::= while ( Expression ) Statement
            //
            case 130: {
                //#line 1469 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1469 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1471 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.While(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 131:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 131: {
                //#line 1475 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1475 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1477 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                break;
            }
     
            //
            // Rule 134:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 134: {
                //#line 1484 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1484 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1484 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1484 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1486 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                break;
            }
     
            //
            // Rule 136:  ForInit ::= LocalVariableDeclaration
            //
            case 136: {
                //#line 1491 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1493 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 138:  StatementExpressionList ::= StatementExpression
            //
            case 138: {
                //#line 1501 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1503 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                break;
            }
     
            //
            // Rule 139:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 139: {
                //#line 1508 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1508 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1510 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 140:  BreakStatement ::= break Identifieropt ;
            //
            case 140: {
                //#line 1514 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1516 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Break(pos(), Identifieropt));
                break;
            }
     
            //
            // Rule 141:  ContinueStatement ::= continue Identifieropt ;
            //
            case 141: {
                //#line 1520 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1522 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                break;
            }
     
            //
            // Rule 142:  ReturnStatement ::= return Expressionopt ;
            //
            case 142: {
                //#line 1526 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1528 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Return(pos(), Expressionopt));
                break;
            }
     
            //
            // Rule 143:  ThrowStatement ::= throw Expression ;
            //
            case 143: {
                //#line 1532 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1534 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Throw(pos(), Expression));
                break;
            }
     
            //
            // Rule 144:  TryStatement ::= try Block Catches
            //
            case 144: {
                //#line 1538 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1538 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1540 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Try(pos(), Block, Catches));
                break;
            }
     
            //
            // Rule 145:  TryStatement ::= try Block Catchesopt Finally
            //
            case 145: {
                //#line 1543 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1543 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1543 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1545 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                break;
            }
     
            //
            // Rule 146:  Catches ::= CatchClause
            //
            case 146: {
                //#line 1549 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1551 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                break;
            }
     
            //
            // Rule 147:  Catches ::= Catches CatchClause
            //
            case 147: {
                //#line 1556 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1556 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1558 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                break;
            }
     
            //
            // Rule 148:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 148: {
                //#line 1563 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1563 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1565 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                break;
            }
     
            //
            // Rule 149:  Finally ::= finally Block
            //
            case 149: {
                //#line 1569 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1571 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 150:  NowStatement ::= now ( Clock ) Statement
            //
            case 150: {
                //#line 1575 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1575 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1577 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Now(pos(), Clock, Statement));
                break;
            }
     
            //
            // Rule 151:  ClockedClause ::= clocked ( ClockList )
            //
            case 151: {
                //#line 1581 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1583 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 152:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 152: {
                //#line 1587 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1587 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1587 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1589 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                break;
            }
     
            //
            // Rule 153:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 153: {
                //#line 1596 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1596 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1598 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                               ? nf.Here(pos(getLeftSpan()))
                                               : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 154:  WhenStatement ::= when ( Expression ) Statement
            //
            case 154: {
                //#line 1605 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1605 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1607 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.When(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 155:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 155: {
                //#line 1610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1612 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 156:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 156: {
                //#line 1617 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1617 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1617 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1617 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1619 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 157:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 157: {
                //#line 1631 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1631 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1631 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1631 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1633 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 158:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 158: {
                //#line 1645 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1645 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1645 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1647 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 159:  FinishStatement ::= finish Statement
            //
            case 159: {
                //#line 1658 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1660 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Finish(pos(),  Statement));
                break;
            }
     
            //
            // Rule 160:  AnnotationStatement ::= Annotations Statement
            //
            case 160: {
                //#line 1665 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 1665 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1667 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (Statement.ext() instanceof X10Ext && Annotations instanceof List) {
                    Statement = (Stmt) ((X10Ext) Statement.ext()).annotations((List) Annotations);
                }
                setResult(Statement);
                break;
            }
     
            //
            // Rule 161:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 161: {
                //#line 1674 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1676 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(PlaceExpression);
                break;
            }
     
            //
            // Rule 163:  NextStatement ::= next ;
            //
            case 163: {
                
                //#line 1684 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 164:  AwaitStatement ::= await Expression ;
            //
            case 164: {
                //#line 1688 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1690 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Await(pos(), Expression));
                break;
            }
     
            //
            // Rule 165:  ClockList ::= Clock
            //
            case 165: {
                //#line 1694 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1696 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                break;
            }
     
            //
            // Rule 166:  ClockList ::= ClockList , Clock
            //
            case 166: {
                //#line 1701 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1701 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1703 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 167:  Clock ::= Expression
            //
            case 167: {
                //#line 1709 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1711 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
    setResult(Expression);
                break;
            }
     
            //
            // Rule 168:  CastExpression ::= ConditionalExpression as Type
            //
            case 168: {
                //#line 1723 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1723 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1725 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Cast(pos(), Type, ConditionalExpression));
                break;
            }
     
            //
            // Rule 169:  CastExpression ::= ConditionalExpression to Type
            //
            case 169: {
                //#line 1728 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1728 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1730 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Cast(pos(), Type, ConditionalExpression));
                break;
            }
     
            //
            // Rule 170:  CastExpression ::= ConditionalExpression ! Expression
            //
            case 170: {
                //#line 1733 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1733 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1735 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.PlaceCast(pos(), Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 172:  TypePropertyList ::= TypeProperty
            //
            case 172: {
                //#line 1742 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypePropertyNode TypeProperty = (TypePropertyNode) getRhsSym(1);
                //#line 1744 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypePropertyNode.class, false);
                l.add(TypeProperty);
                setResult(l);
                break;
            }
     
            //
            // Rule 173:  TypePropertyList ::= TypePropertyList , TypeProperty
            //
            case 173: {
                //#line 1749 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertyList = (List) getRhsSym(1);
                //#line 1749 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypePropertyNode TypeProperty = (TypePropertyNode) getRhsSym(3);
                //#line 1751 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypePropertyList.add(TypeProperty);
                setResult(TypePropertyList);
                break;
            }
     
            //
            // Rule 174:  TypeParameterList ::= TypeParameter
            //
            case 174: {
                //#line 1756 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 1758 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 175:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 175: {
                //#line 1763 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 1763 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 1765 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                break;
            }
     
            //
            // Rule 176:  TypeProperty ::= Identifier
            //
            case 176: {
                //#line 1770 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1772 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.INVARIANT));
                break;
            }
     
            //
            // Rule 177:  TypeProperty ::= + Identifier
            //
            case 177: {
                //#line 1775 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1777 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.COVARIANT));
                break;
            }
     
            //
            // Rule 178:  TypeProperty ::= - Identifier
            //
            case 178: {
                //#line 1780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1782 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.CONTRAVARIANT));
                break;
            }
     
            //
            // Rule 179:  TypeParameter ::= Identifier
            //
            case 179: {
                //#line 1786 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1788 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                break;
            }
     
            //
            // Rule 180:  Primary ::= here
            //
            case 180: {
                
                //#line 1794 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                break;
            }
     
            //
            // Rule 182:  RegionExpressionList ::= RegionExpression
            //
            case 182: {
                //#line 1800 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 1802 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                break;
            }
     
            //
            // Rule 183:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 183: {
                //#line 1807 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 1807 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 1809 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                break;
            }
     
            //
            // Rule 184:  Primary ::= [ ArgumentList ]
            //
            case 184: {
                //#line 1814 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(2);
                //#line 1816 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentList);
                setResult(tuple);
                break;
            }
     
            //
            // Rule 185:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 185: {
                //#line 1821 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 1821 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 1823 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                break;
            }
     
            //
            // Rule 186:  ClosureExpression ::= TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt => ClosureBody
            //
            case 186: {
                //#line 1828 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(1);
                //#line 1828 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(2);
                //#line 1828 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 1828 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 1828 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 1828 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 1830 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Closure(pos(), TypeParametersopt, FormalParameters, WhereClauseopt, 
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt, Throwsopt, ClosureBody));
                break;
            }
     
            //
            // Rule 187:  LastExpression ::= Expression
            //
            case 187: {
                //#line 1835 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1837 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                break;
            }
     
            //
            // Rule 188:  ClosureBody ::= CastExpression
            //
            case 188: {
                //#line 1841 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 1843 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), CastExpression, true)));
                break;
            }
     
            //
            // Rule 189:  ClosureBody ::= { BlockStatementsopt LastExpression }
            //
            case 189: {
                //#line 1846 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 1846 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(3);
                //#line 1848 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 191:  AsyncExpression ::= async ClosureBody
            //
            case 191: {
                //#line 1856 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 1858 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                break;
            }
     
            //
            // Rule 192:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 192: {
                //#line 1861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 1863 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                break;
            }
     
            //
            // Rule 193:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 193: {
                //#line 1866 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1866 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 1868 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                break;
            }
     
            //
            // Rule 194:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 194: {
                //#line 1871 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1871 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 1871 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 1873 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                break;
            }
     
            //
            // Rule 195:  FutureExpression ::= future ClosureBody
            //
            case 195: {
                //#line 1877 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 1879 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                break;
            }
     
            //
            // Rule 196:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 196: {
                //#line 1882 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1882 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 1884 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                break;
            }
     
            //
            // Rule 197:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 197: {
                //#line 1887 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1887 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 1889 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                break;
            }
     
            //
            // Rule 198:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 198: {
                //#line 1892 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1892 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 1892 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 1894 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                break;
            }
     
            //
            // Rule 199:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 199:
                setResult(null);
                break;
 
            //
            // Rule 201:  DepParametersopt ::= $Empty
            //
            case 201:
                setResult(null);
                break;
 
            //
            // Rule 203:  PropertyListopt ::= $Empty
            //
            case 203: {
                
                //#line 1909 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                break;
            }
     
            //
            // Rule 205:  WhereClauseopt ::= $Empty
            //
            case 205:
                setResult(null);
                break;
 
            //
            // Rule 207:  ObjectKindopt ::= $Empty
            //
            case 207:
                setResult(null);
                break;
 
            //
            // Rule 209:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 209:
                setResult(null);
                break;
 
            //
            // Rule 211:  ClassModifiersopt ::= $Empty
            //
            case 211: {
                
                //#line 1928 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                break;
            }  
            //
            // Rule 213:  TypeDefModifiersopt ::= $Empty
            //
            case 213: {
                
                //#line 1934 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                break;
            }  
            //
            // Rule 215:  Unsafeopt ::= $Empty
            //
            case 215:
                setResult(null);
                break;
 
            //
            // Rule 216:  Unsafeopt ::= unsafe
            //
            case 216: {
                
                //#line 1942 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 217:  ClockedClauseopt ::= $Empty
            //
            case 217: {
                
                //#line 1949 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 219:  identifier ::= IDENTIFIER$ident
            //
            case 219: {
                //#line 1958 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 1960 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 220:  TypeName ::= Identifier
            //
            case 220: {
                //#line 1965 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1967 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 221:  TypeName ::= TypeName . Identifier
            //
            case 221: {
                //#line 1970 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 1970 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1972 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 223:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 223: {
                //#line 1982 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 1984 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(TypeArgumentList);
                break;
            }
     
            //
            // Rule 224:  TypeArgumentList ::= Type
            //
            case 224: {
                //#line 1989 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1991 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                break;
            }
     
            //
            // Rule 225:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 225: {
                //#line 1996 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 1996 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1998 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeArgumentList.add(Type);
                break;
            }
     
            //
            // Rule 226:  PackageName ::= Identifier
            //
            case 226: {
                //#line 2006 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2008 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 227:  PackageName ::= PackageName . Identifier
            //
            case 227: {
                //#line 2011 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageName = (Name) getRhsSym(1);
                //#line 2011 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2013 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 228:  ExpressionName ::= Identifier
            //
            case 228: {
                //#line 2027 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2029 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 229:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 229: {
                //#line 2032 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 2032 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2034 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 230:  MethodName ::= Identifier
            //
            case 230: {
                //#line 2042 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2044 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 231:  MethodName ::= AmbiguousName . Identifier
            //
            case 231: {
                //#line 2047 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 2047 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2049 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 232:  PackageOrTypeName ::= Identifier
            //
            case 232: {
                //#line 2057 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2059 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 233:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 233: {
                //#line 2062 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageOrTypeName = (Name) getRhsSym(1);
                //#line 2062 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2064 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 234:  AmbiguousName ::= Identifier
            //
            case 234: {
                //#line 2072 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2074 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 235:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 235: {
                //#line 2077 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 2077 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2079 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
               break;
            }
     
            //
            // Rule 236:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 236: {
                //#line 2089 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2089 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2089 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2091 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 237:  ImportDeclarations ::= ImportDeclaration
            //
            case 237: {
                //#line 2105 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2107 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 238:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 238: {
                //#line 2112 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2112 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2114 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 239:  TypeDeclarations ::= TypeDeclaration
            //
            case 239: {
                //#line 2120 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2122 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 240:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 240: {
                //#line 2128 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2128 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(2);
                //#line 2130 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 241:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 241: {
                //#line 2136 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2136 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageName = (Name) getRhsSym(3);
                //#line 2138 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn);
                break;
            }
     
            //
            // Rule 244:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 244: {
                //#line 2150 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(2);
                //#line 2152 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, TypeName.toString()));
                break;
            }
     
            //
            // Rule 245:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 245: {
                //#line 2156 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageOrTypeName = (Name) getRhsSym(2);
                //#line 2158 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, PackageOrTypeName.toString()));
                break;
            }
     
            //
            // Rule 249:  TypeDeclaration ::= ;
            //
            case 249: {
                
                //#line 2173 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(null);
                break;
            }
     
            //
            // Rule 250:  ClassModifiers ::= ClassModifier
            //
            case 250: {
                //#line 2179 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2181 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 251:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 251: {
                //#line 2186 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2186 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2188 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClassModifiers.addAll(ClassModifier);
                break;
            }
     
            //
            // Rule 252:  ClassModifier ::= Annotation
            //
            case 252: {
                //#line 2192 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2194 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 253:  ClassModifier ::= public
            //
            case 253: {
                
                //#line 2199 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 254:  ClassModifier ::= protected
            //
            case 254: {
                
                //#line 2204 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 255:  ClassModifier ::= private
            //
            case 255: {
                
                //#line 2209 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 256:  ClassModifier ::= abstract
            //
            case 256: {
                
                //#line 2214 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 257:  ClassModifier ::= static
            //
            case 257: {
                
                //#line 2219 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 258:  ClassModifier ::= final
            //
            case 258: {
                
                //#line 2224 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 259:  ClassModifier ::= strictfp
            //
            case 259: {
                
                //#line 2229 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                break;
            }
     
            //
            // Rule 260:  ClassModifier ::= safe
            //
            case 260: {
                
                //#line 2234 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                break;
            }
     
            //
            // Rule 261:  TypeDefModifiers ::= TypeDefModifier
            //
            case 261: {
                //#line 2238 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2240 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 262:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 262: {
                //#line 2245 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2245 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2247 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                break;
            }
     
            //
            // Rule 263:  TypeDefModifier ::= Annotation
            //
            case 263: {
                //#line 2251 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2253 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 264:  TypeDefModifier ::= public
            //
            case 264: {
                
                //#line 2258 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 265:  TypeDefModifier ::= protected
            //
            case 265: {
                
                //#line 2263 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 266:  TypeDefModifier ::= private
            //
            case 266: {
                
                //#line 2268 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 267:  TypeDefModifier ::= abstract
            //
            case 267: {
                
                //#line 2273 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 268:  TypeDefModifier ::= static
            //
            case 268: {
                
                //#line 2278 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 269:  TypeDefModifier ::= final
            //
            case 269: {
                
                //#line 2283 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 270:  Interfaces ::= implements InterfaceTypeList
            //
            case 270: {
                //#line 2290 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2292 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 271:  InterfaceTypeList ::= Type
            //
            case 271: {
                //#line 2296 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2298 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                break;
            }
     
            //
            // Rule 272:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 272: {
                //#line 2303 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2303 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2305 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 273:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 273: {
                //#line 2313 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2315 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                break;
            }
     
            //
            // Rule 275:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 275: {
                //#line 2320 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2320 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2322 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                break;
            }
     
            //
            // Rule 277:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 277: {
                //#line 2328 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block InstanceInitializer = (Block) getRhsSym(1);
                //#line 2330 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InstanceInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 278:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 278: {
                //#line 2335 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block StaticInitializer = (Block) getRhsSym(1);
                //#line 2337 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(StaticInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 279:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 279: {
                //#line 2342 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2344 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 281:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 281: {
                //#line 2351 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2353 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 282:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 282: {
                //#line 2358 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2360 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 283:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 283: {
                //#line 2365 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2367 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 284:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 284: {
                //#line 2372 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2374 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 285:  ClassMemberDeclaration ::= ;
            //
            case 285: {
                
                //#line 2381 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                break;
            }
     
            //
            // Rule 286:  FormalDeclarators ::= FormalDeclarator
            //
            case 286: {
                //#line 2386 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2388 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 287:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 287: {
                //#line 2393 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2393 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2395 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FormalDeclarators.add(FormalDeclarator);
                break;
            }
     
            //
            // Rule 288:  FieldDeclarators ::= FieldDeclarator
            //
            case 288: {
                //#line 2400 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2402 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 289:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 289: {
                //#line 2407 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2407 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2409 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                break;
            }
     
            //
            // Rule 290:  VariableDeclarators ::= VariableDeclarator
            //
            case 290: {
                //#line 2415 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2417 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 291:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 291: {
                //#line 2422 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2422 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2424 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                break;
            }
     
            //
            // Rule 293:  FieldModifiers ::= FieldModifier
            //
            case 293: {
                //#line 2431 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2433 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 294:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 294: {
                //#line 2438 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2438 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2440 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FieldModifiers.addAll(FieldModifier);
                break;
            }
     
            //
            // Rule 295:  FieldModifier ::= Annotation
            //
            case 295: {
                //#line 2444 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2446 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 296:  FieldModifier ::= public
            //
            case 296: {
                
                //#line 2451 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 297:  FieldModifier ::= protected
            //
            case 297: {
                
                //#line 2456 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 298:  FieldModifier ::= private
            //
            case 298: {
                
                //#line 2461 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 299:  FieldModifier ::= static
            //
            case 299: {
                
                //#line 2466 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 300:  FieldModifier ::= transient
            //
            case 300: {
                
                //#line 2471 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.TRANSIENT)));
                break;
            }
     
            //
            // Rule 301:  FieldModifier ::= volatile
            //
            case 301: {
                
                //#line 2476 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.VOLATILE)));
                break;
            }
     
            //
            // Rule 302:  ResultType ::= : Type
            //
            case 302: {
                //#line 2480 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2482 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Type);
                break;
            }
     
            //
            // Rule 303:  FormalParameters ::= ( FormalParameterList )
            //
            case 303: {
                //#line 2486 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2488 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(FormalParameterList);
                break;
            }
     
            //
            // Rule 304:  FormalParameterList ::= FormalParameter
            //
            case 304: {
                //#line 2492 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2494 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 305:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 305: {
                //#line 2499 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2499 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2501 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FormalParameterList.add(FormalParameter);
                break;
            }
     
            //
            // Rule 306:  LoopIndexDeclarator ::= Identifier ResultTypeopt
            //
            case 306: {
                //#line 2505 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2505 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2507 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, null });
                break;
            }
     
            //
            // Rule 307:  LoopIndexDeclarator ::= ( IdentifierList ) ResultTypeopt
            //
            case 307: {
                //#line 2510 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2510 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2512 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, null });
                break;
            }
     
            //
            // Rule 308:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt
            //
            case 308: {
                //#line 2515 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2515 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2515 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2517 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, null });
                break;
            }
     
            //
            // Rule 309:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 309: {
                //#line 2521 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2521 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2523 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 310:  LoopIndex ::= VariableModifiersopt VarKeyword LoopIndexDeclarator
            //
            case 310: {
                //#line 2542 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2542 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2542 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2544 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 311:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 311: {
                //#line 2564 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2564 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2566 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 312:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 312: {
                //#line 2586 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2586 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2586 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2588 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 313:  FormalParameter ::= Type
            //
            case 313: {
                //#line 2608 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2610 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), ""), Collections.EMPTY_LIST);
            setResult(f);
                break;
            }
     
            //
            // Rule 314:  VariableModifiers ::= VariableModifier
            //
            case 314: {
                //#line 2616 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2618 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 315:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 315: {
                //#line 2623 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2623 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2625 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableModifiers.addAll(VariableModifier);
                break;
            }
     
            //
            // Rule 316:  VariableModifier ::= Annotation
            //
            case 316: {
                //#line 2629 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2631 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 317:  VariableModifier ::= shared
            //
            case 317: {
                
                //#line 2636 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                break;
            }
     
            //
            // Rule 318:  MethodModifiers ::= MethodModifier
            //
            case 318: {
                //#line 2643 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2645 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 319:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 319: {
                //#line 2650 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2650 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2652 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                MethodModifiers.addAll(MethodModifier);
                break;
            }
     
            //
            // Rule 320:  MethodModifier ::= Annotation
            //
            case 320: {
                //#line 2656 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2658 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 321:  MethodModifier ::= public
            //
            case 321: {
                
                //#line 2663 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 322:  MethodModifier ::= protected
            //
            case 322: {
                
                //#line 2668 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 323:  MethodModifier ::= private
            //
            case 323: {
                
                //#line 2673 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 324:  MethodModifier ::= abstract
            //
            case 324: {
                
                //#line 2678 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 325:  MethodModifier ::= static
            //
            case 325: {
                
                //#line 2683 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 326:  MethodModifier ::= final
            //
            case 326: {
                
                //#line 2688 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 327:  MethodModifier ::= native
            //
            case 327: {
                
                //#line 2693 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                break;
            }
     
            //
            // Rule 328:  MethodModifier ::= strictfp
            //
            case 328: {
                
                //#line 2698 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                break;
            }
     
            //
            // Rule 329:  MethodModifier ::= atomic
            //
            case 329: {
                
                //#line 2703 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                break;
            }
     
            //
            // Rule 330:  MethodModifier ::= extern
            //
            case 330: {
                
                //#line 2708 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                break;
            }
     
            //
            // Rule 331:  MethodModifier ::= safe
            //
            case 331: {
                
                //#line 2713 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                break;
            }
     
            //
            // Rule 332:  MethodModifier ::= sequential
            //
            case 332: {
                
                //#line 2718 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                break;
            }
     
            //
            // Rule 333:  MethodModifier ::= local
            //
            case 333: {
                
                //#line 2723 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.LOCAL)));
                break;
            }
     
            //
            // Rule 334:  MethodModifier ::= nonblocking
            //
            case 334: {
                
                //#line 2728 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                break;
            }
     
            //
            // Rule 335:  MethodModifier ::= incomplete
            //
            case 335: {
                
                //#line 2733 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                break;
            }
     
            //
            // Rule 336:  MethodModifier ::= property
            //
            case 336: {
                
                //#line 2738 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                break;
            }
     
            //
            // Rule 337:  Throws ::= throws ExceptionTypeList
            //
            case 337: {
                //#line 2743 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 2745 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 338:  ExceptionTypeList ::= ExceptionType
            //
            case 338: {
                //#line 2749 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 2751 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                break;
            }
     
            //
            // Rule 339:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 339: {
                //#line 2756 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 2756 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 2758 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExceptionTypeList.add(ExceptionType);
                break;
            }
     
            //
            // Rule 341:  MethodBody ::= = LastExpression ;
            //
            case 341: {
                //#line 2764 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 2766 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Block(pos(), LastExpression));
                break;
            }
     
            //
            // Rule 342:  MethodBody ::= = { BlockStatementsopt LastExpression }
            //
            case 342: {
                //#line 2769 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2769 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2771 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 343:  MethodBody ::= = Block
            //
            case 343: {
                //#line 2777 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2779 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 344:  MethodBody ::= ;
            //
            case 344:
                setResult(null);
                break;
 
            //
            // Rule 345:  InstanceInitializer ::= Block
            //
            case 345: {
                //#line 2785 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(1);
                //#line 2787 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(), Flags.NONE), Block));
                break;
            }
     
            //
            // Rule 346:  StaticInitializer ::= static Block
            //
            case 346: {
                //#line 2791 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2793 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(getLeftSpan()), Flags.STATIC), Block));
                break;
            }
     
            //
            // Rule 347:  SimpleTypeName ::= Identifier
            //
            case 347: {
                //#line 2797 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2799 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 348:  ConstructorModifiers ::= ConstructorModifier
            //
            case 348: {
                //#line 2803 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 2805 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 349:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 349: {
                //#line 2810 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 2810 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 2812 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                break;
            }
     
            //
            // Rule 350:  ConstructorModifier ::= Annotation
            //
            case 350: {
                //#line 2816 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2818 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 351:  ConstructorModifier ::= public
            //
            case 351: {
                
                //#line 2823 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 352:  ConstructorModifier ::= protected
            //
            case 352: {
                
                //#line 2828 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 353:  ConstructorModifier ::= private
            //
            case 353: {
                
                //#line 2833 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 354:  ConstructorBody ::= = ConstructorBlock
            //
            case 354: {
                //#line 2837 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 2839 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ConstructorBlock);
                break;
            }
     
            //
            // Rule 355:  ConstructorBody ::= ;
            //
            case 355:
                setResult(null);
                break;
 
            //
            // Rule 356:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 356: {
                //#line 2845 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 2845 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2847 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 357:  ConstructorBlock ::= ExplicitConstructorInvocation
            //
            case 357: {
                //#line 2861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(1);
                //#line 2863 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 358:  ConstructorBlock ::= AssignPropertyCall
            //
            case 358: {
                //#line 2869 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(1);
                //#line 2871 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 359:  Arguments ::= ( ArgumentListopt )
            //
            case 359: {
                //#line 2878 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 2880 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ArgumentListopt);
                break;
            }
     
            //
            // Rule 361:  InterfaceModifiers ::= InterfaceModifier
            //
            case 361: {
                //#line 2888 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 2890 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 362:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 362: {
                //#line 2895 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 2895 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 2897 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                break;
            }
     
            //
            // Rule 363:  InterfaceModifier ::= Annotation
            //
            case 363: {
                //#line 2901 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2903 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 364:  InterfaceModifier ::= public
            //
            case 364: {
                
                //#line 2908 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 365:  InterfaceModifier ::= protected
            //
            case 365: {
                
                //#line 2913 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 366:  InterfaceModifier ::= private
            //
            case 366: {
                
                //#line 2918 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 367:  InterfaceModifier ::= abstract
            //
            case 367: {
                
                //#line 2923 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 368:  InterfaceModifier ::= static
            //
            case 368: {
                
                //#line 2928 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 369:  InterfaceModifier ::= strictfp
            //
            case 369: {
                
                //#line 2933 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                break;
            }
     
            //
            // Rule 370:  ExtendsInterfaces ::= extends Type
            //
            case 370: {
                //#line 2937 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2939 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                break;
            }
     
            //
            // Rule 371:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 371: {
                //#line 2944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 2944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2946 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExtendsInterfaces.add(Type);
                break;
            }
     
            //
            // Rule 372:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 372: {
                //#line 2953 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 2955 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                break;
            }
     
            //
            // Rule 374:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 374: {
                //#line 2960 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 2960 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 2962 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                break;
            }
     
            //
            // Rule 375:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 375: {
                //#line 2967 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2969 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 376:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 376: {
                //#line 2974 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2976 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 377:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 377: {
                //#line 2981 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2983 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 378:  InterfaceMemberDeclaration ::= ;
            //
            case 378: {
                
                //#line 2990 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 379:  Annotations ::= Annotation
            //
            case 379: {
                //#line 2994 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2996 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                break;
            }
     
            //
            // Rule 380:  Annotations ::= Annotations Annotation
            //
            case 380: {
                //#line 3001 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3001 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3003 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Annotations.add(Annotation);
                break;
            }
     
            //
            // Rule 381:  Annotation ::= @ NamedType
            //
            case 381: {
                //#line 3007 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3009 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                break;
            }
     
            //
            // Rule 382:  SimpleName ::= Identifier
            //
            case 382: {
                //#line 3013 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3015 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 383:  Identifier ::= identifier
            //
            case 383: {
                //#line 3019 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3021 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 384:  ArrayInitializer ::= [ VariableInitializersopt ,opt$opt ]
            //
            case 384: {
                //#line 3027 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializersopt = (List) getRhsSym(2);
                //#line 3027 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object opt = (Object) getRhsSym(3);
                //#line 3029 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (VariableInitializersopt == null)
                     setResult(nf.ArrayInit(pos()));
                else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
                break;
            }
     
            //
            // Rule 385:  VariableInitializers ::= VariableInitializer
            //
            case 385: {
                //#line 3035 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3037 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 386:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 386: {
                //#line 3042 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3042 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3044 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                break;
            }
     
            //
            // Rule 387:  Block ::= { BlockStatementsopt }
            //
            case 387: {
                //#line 3060 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3062 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                break;
            }
     
            //
            // Rule 388:  BlockStatements ::= BlockStatement
            //
            case 388: {
                //#line 3066 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3068 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                break;
            }
     
            //
            // Rule 389:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 389: {
                //#line 3073 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3073 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3075 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                break;
            }
     
            //
            // Rule 391:  BlockStatement ::= ClassDeclaration
            //
            case 391: {
                //#line 3081 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3083 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                break;
            }
     
            //
            // Rule 392:  BlockStatement ::= Statement
            //
            case 392: {
                //#line 3088 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3090 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                break;
            }
     
            //
            // Rule 393:  IdentifierList ::= Identifier
            //
            case 393: {
                //#line 3096 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3098 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 394:  IdentifierList ::= IdentifierList , Identifier
            //
            case 394: {
                //#line 3103 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3103 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3105 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                IdentifierList.add(Identifier);
                break;
            }
     
            //
            // Rule 395:  FormalDeclarator ::= Identifier : Type
            //
            case 395: {
                //#line 3109 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3109 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3111 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, Type, null });
                break;
            }
     
            //
            // Rule 396:  FormalDeclarator ::= ( IdentifierList ) : Type
            //
            case 396: {
                //#line 3114 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3114 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(5);
                //#line 3116 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, Type, null });
                break;
            }
     
            //
            // Rule 397:  FormalDeclarator ::= Identifier ( IdentifierList ) : Type
            //
            case 397: {
                //#line 3119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(6);
                //#line 3121 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, Type, null });
                break;
            }
     
            //
            // Rule 398:  FieldDeclarator ::= Identifier WhereClauseopt : Type
            //
            case 398: {
                //#line 3125 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3125 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 3125 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(4);
                //#line 3127 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, WhereClauseopt, Type, null });
                break;
            }
     
            //
            // Rule 399:  FieldDeclarator ::= Identifier WhereClauseopt ResultTypeopt = VariableInitializer
            //
            case 399: {
                //#line 3130 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3130 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 3130 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 3130 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(5);
                //#line 3132 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, WhereClauseopt, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 400:  VariableDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 400: {
                //#line 3136 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3136 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3136 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3138 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 401:  VariableDeclarator ::= ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 401: {
                //#line 3141 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3141 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3141 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3143 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 402:  VariableDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 402: {
                //#line 3146 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3146 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3146 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3146 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3148 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 404:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 404: {
                //#line 3154 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3154 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3154 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3156 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 405:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 405: {
                //#line 3186 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3186 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3186 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3188 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 408:  Primary ::= TypeName . class
            //
            case 408: {
                //#line 3228 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3230 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (TypeName instanceof Name)
                {
                    Name a = (Name) TypeName;
                    setResult(nf.ClassLit(pos(), a.toType()));
                }
                else assert(false);
                break;
            }
     
            //
            // Rule 409:  Primary ::= self
            //
            case 409: {
                
                //#line 3240 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Self(pos()));
                break;
            }
     
            //
            // Rule 410:  Primary ::= this
            //
            case 410: {
                
                //#line 3245 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 411:  Primary ::= ClassName . this
            //
            case 411: {
                //#line 3248 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3250 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 412:  Primary ::= ( Expression )
            //
            case 412: {
                //#line 3253 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3255 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ParExpr(pos(), Expression));
                break;
            }
     
            //
            // Rule 418:  OperatorFunction ::= TypeName . +
            //
            case 418: {
                //#line 3264 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3266 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 419:  OperatorFunction ::= TypeName . -
            //
            case 419: {
                //#line 3275 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3277 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 420:  OperatorFunction ::= TypeName . *
            //
            case 420: {
                //#line 3286 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3288 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 421:  OperatorFunction ::= TypeName . /
            //
            case 421: {
                //#line 3297 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3299 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 422:  OperatorFunction ::= TypeName . %
            //
            case 422: {
                //#line 3308 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3310 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 423:  OperatorFunction ::= TypeName . &
            //
            case 423: {
                //#line 3319 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3321 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 424:  OperatorFunction ::= TypeName . |
            //
            case 424: {
                //#line 3330 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3332 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 425:  OperatorFunction ::= TypeName . ^
            //
            case 425: {
                //#line 3341 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3343 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 426:  OperatorFunction ::= TypeName . <<
            //
            case 426: {
                //#line 3352 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3354 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 427:  OperatorFunction ::= TypeName . >>
            //
            case 427: {
                //#line 3363 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3365 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 428:  OperatorFunction ::= TypeName . >>>
            //
            case 428: {
                //#line 3374 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3376 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 429:  OperatorFunction ::= TypeName . <
            //
            case 429: {
                //#line 3385 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3387 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 430:  OperatorFunction ::= TypeName . <=
            //
            case 430: {
                //#line 3396 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3398 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 431:  OperatorFunction ::= TypeName . >=
            //
            case 431: {
                //#line 3407 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3409 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 432:  OperatorFunction ::= TypeName . >
            //
            case 432: {
                //#line 3418 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3420 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 433:  OperatorFunction ::= TypeName . ==
            //
            case 433: {
                //#line 3429 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3431 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 434:  OperatorFunction ::= TypeName . !=
            //
            case 434: {
                //#line 3440 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3442 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 435:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 435: {
                //#line 3453 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                //#line 3455 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 436:  Literal ::= LongLiteral$LongLiteral
            //
            case 436: {
                //#line 3459 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken LongLiteral = (IToken) getRhsIToken(1);
                //#line 3461 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 437:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 437: {
                //#line 3465 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                //#line 3467 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 438:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 438: {
                //#line 3471 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                //#line 3473 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 439:  Literal ::= BooleanLiteral
            //
            case 439: {
                //#line 3477 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3479 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 440:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 440: {
                //#line 3482 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                //#line 3484 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 441:  Literal ::= StringLiteral$str
            //
            case 441: {
                //#line 3488 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3490 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 442:  Literal ::= null
            //
            case 442: {
                
                //#line 3496 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 443:  BooleanLiteral ::= true$trueLiteral
            //
            case 443: {
                //#line 3500 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3502 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 444:  BooleanLiteral ::= false$falseLiteral
            //
            case 444: {
                //#line 3505 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3507 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 445:  ArgumentList ::= Expression
            //
            case 445: {
                //#line 3514 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3516 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                break;
            }
     
            //
            // Rule 446:  ArgumentList ::= ArgumentList , Expression
            //
            case 446: {
                //#line 3521 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3521 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3523 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ArgumentList.add(Expression);
                break;
            }
     
            //
            // Rule 447:  FieldAccess ::= Primary . Identifier
            //
            case 447: {
                //#line 3527 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3527 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3529 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                break;
            }
     
            //
            // Rule 448:  FieldAccess ::= super . Identifier
            //
            case 448: {
                //#line 3532 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3534 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), Identifier));
                break;
            }
     
            //
            // Rule 449:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 449: {
                //#line 3537 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3537 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3537 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3539 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                break;
            }
     
            //
            // Rule 450:  FieldAccess ::= Primary . class$c
            //
            case 450: {
                //#line 3542 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3542 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3544 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                break;
            }
     
            //
            // Rule 451:  FieldAccess ::= super . class$c
            //
            case 451: {
                //#line 3547 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3549 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                break;
            }
     
            //
            // Rule 452:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 452: {
                //#line 3552 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3552 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3552 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 3554 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                break;
            }
     
            //
            // Rule 453:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 453: {
                //#line 3558 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name MethodName = (Name) getRhsSym(1);
                //#line 3558 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3558 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3560 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 454:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 454: {
                //#line 3565 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3565 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3565 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3565 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3567 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 455:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 455: {
                //#line 3570 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3570 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3570 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3572 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 456:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 456: {
                //#line 3575 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3575 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3575 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3575 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3575 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3577 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 457:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 457: {
                //#line 3580 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3580 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3580 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3582 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 458:  MethodSelection ::= MethodName . TypeParametersopt ( FormalParameterListopt )
            //
            case 458: {
                //#line 3596 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name MethodName = (Name) getRhsSym(1);
                //#line 3596 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 3596 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(5);
                //#line 3598 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 459:  MethodSelection ::= Primary . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 459: {
                //#line 3609 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3609 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3609 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3609 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3611 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 460:  MethodSelection ::= super . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 460: {
                //#line 3621 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3621 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3621 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3623 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 461:  MethodSelection ::= ClassName . super$sup . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 461: {
                //#line 3633 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3633 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3633 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3633 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(7);
                //#line 3633 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(9);
                //#line 3635 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 464:  PostfixExpression ::= ExpressionName
            //
            case 464: {
                //#line 3648 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 3650 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 467:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 467: {
                //#line 3656 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 3658 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 468:  PostDecrementExpression ::= PostfixExpression --
            //
            case 468: {
                //#line 3662 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 3664 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 471:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 471: {
                //#line 3670 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3672 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 472:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 472: {
                //#line 3675 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3677 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 474:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 474: {
                //#line 3682 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3684 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 475:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 475: {
                //#line 3688 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3690 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 477:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 477: {
                //#line 3695 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 3697 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 478:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 478: {
                //#line 3700 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3700 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 3702 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e);
                break;
            }
     
            //
            // Rule 479:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 479: {
                //#line 3707 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 3709 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 481:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 481: {
                //#line 3714 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 3714 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 3716 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                break;
            }
     
            //
            // Rule 482:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 482: {
                //#line 3719 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 3719 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 3721 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                break;
            }
     
            //
            // Rule 483:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 483: {
                //#line 3724 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 3724 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 3726 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                break;
            }
     
            //
            // Rule 485:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 485: {
                //#line 3731 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 3731 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 3733 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 486:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 486: {
                //#line 3736 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 3736 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 3738 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 488:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 488: {
                //#line 3743 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 3743 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 3745 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                break;
            }
     
            //
            // Rule 489:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 489: {
                //#line 3748 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 3748 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 3750 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 490:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 490: {
                //#line 3753 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 3753 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 3755 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 491:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 491: {
                //#line 3759 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 3759 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 3761 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                break;
            }
     
            //
            // Rule 495:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 495: {
                //#line 3769 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3769 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3771 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
                break;
            }
     
            //
            // Rule 496:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 496: {
                //#line 3774 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3774 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3776 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
                break;
            }
     
            //
            // Rule 497:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 497: {
                //#line 3779 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3779 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3781 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
                break;
            }
     
            //
            // Rule 498:  RelationalExpression ::= RelationalExpression >= ShiftExpression
            //
            case 498: {
                //#line 3784 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3784 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3786 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
                break;
            }
     
            //
            // Rule 499:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 499: {
                //#line 3789 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3789 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3791 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                break;
            }
     
            //
            // Rule 500:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 500: {
                //#line 3794 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3794 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3796 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                break;
            }
     
            //
            // Rule 502:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 502: {
                //#line 3801 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 3801 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 3803 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                break;
            }
     
            //
            // Rule 503:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 503: {
                //#line 3806 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 3806 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 3808 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                break;
            }
     
            //
            // Rule 504:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 504: {
                //#line 3811 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 3811 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 3813 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                break;
            }
     
            //
            // Rule 506:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 506: {
                //#line 3818 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 3818 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 3820 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                break;
            }
     
            //
            // Rule 508:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 508: {
                //#line 3825 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 3825 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 3827 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                break;
            }
     
            //
            // Rule 510:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 510: {
                //#line 3832 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 3832 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 3834 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 512:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 512: {
                //#line 3839 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 3839 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 3841 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                break;
            }
     
            //
            // Rule 514:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 514: {
                //#line 3846 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 3846 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 3848 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                break;
            }
     
            //
            // Rule 519:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 519: {
                //#line 3857 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 3857 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3857 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 3859 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 522:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 522: {
                //#line 3866 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 3866 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 3866 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 3868 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 523:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 523: {
                //#line 3871 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name e1 = (Name) getRhsSym(1);
                //#line 3871 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 3871 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 3871 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 3873 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 524:  LeftHandSide ::= ExpressionName
            //
            case 524: {
                //#line 3877 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 3879 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 526:  AssignmentOperator ::= =
            //
            case 526: {
                
                //#line 3886 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 527:  AssignmentOperator ::= *=
            //
            case 527: {
                
                //#line 3891 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 528:  AssignmentOperator ::= /=
            //
            case 528: {
                
                //#line 3896 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 529:  AssignmentOperator ::= %=
            //
            case 529: {
                
                //#line 3901 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 530:  AssignmentOperator ::= +=
            //
            case 530: {
                
                //#line 3906 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 531:  AssignmentOperator ::= -=
            //
            case 531: {
                
                //#line 3911 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 532:  AssignmentOperator ::= <<=
            //
            case 532: {
                
                //#line 3916 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 533:  AssignmentOperator ::= >>=
            //
            case 533: {
                
                //#line 3921 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 534:  AssignmentOperator ::= >>>=
            //
            case 534: {
                
                //#line 3926 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 535:  AssignmentOperator ::= &=
            //
            case 535: {
                
                //#line 3931 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 536:  AssignmentOperator ::= ^=
            //
            case 536: {
                
                //#line 3936 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 537:  AssignmentOperator ::= |=
            //
            case 537: {
                
                //#line 3941 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 540:  Catchesopt ::= $Empty
            //
            case 540: {
                
                //#line 3954 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 542:  Identifieropt ::= $Empty
            //
            case 542:
                setResult(null);
                break;
 
            //
            // Rule 543:  Identifieropt ::= Identifier
            //
            case 543: {
                //#line 3961 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3963 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Identifier);
                break;
            }
     
            //
            // Rule 544:  ForUpdateopt ::= $Empty
            //
            case 544: {
                
                //#line 3969 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 546:  Expressionopt ::= $Empty
            //
            case 546:
                setResult(null);
                break;
 
            //
            // Rule 548:  ForInitopt ::= $Empty
            //
            case 548: {
                
                //#line 3980 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 550:  SwitchLabelsopt ::= $Empty
            //
            case 550: {
                
                //#line 3987 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 552:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 552: {
                
                //#line 3994 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 554:  VariableModifiersopt ::= $Empty
            //
            case 554: {
                
                //#line 4001 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 556:  VariableInitializersopt ::= $Empty
            //
            case 556:
                setResult(null);
                break;
 
            //
            // Rule 558:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 558: {
                
                //#line 4012 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 560:  ExtendsInterfacesopt ::= $Empty
            //
            case 560: {
                
                //#line 4019 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 562:  InterfaceModifiersopt ::= $Empty
            //
            case 562: {
                
                //#line 4026 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 564:  ClassBodyopt ::= $Empty
            //
            case 564:
                setResult(null);
                break;
 
            //
            // Rule 566:  Argumentsopt ::= $Empty
            //
            case 566: {
                
                //#line 4037 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 568:  ArgumentListopt ::= $Empty
            //
            case 568: {
                
                //#line 4044 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 570:  BlockStatementsopt ::= $Empty
            //
            case 570: {
                
                //#line 4051 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 572:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 572:
                setResult(null);
                break;
 
            //
            // Rule 574:  ConstructorModifiersopt ::= $Empty
            //
            case 574: {
                
                //#line 4062 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 576:  FormalParameterListopt ::= $Empty
            //
            case 576: {
                
                //#line 4069 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 578:  Throwsopt ::= $Empty
            //
            case 578: {
                
                //#line 4076 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 580:  MethodModifiersopt ::= $Empty
            //
            case 580: {
                
                //#line 4083 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 582:  FieldModifiersopt ::= $Empty
            //
            case 582: {
                
                //#line 4090 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 584:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 584: {
                
                //#line 4097 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 586:  Interfacesopt ::= $Empty
            //
            case 586: {
                
                //#line 4104 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 588:  Superopt ::= $Empty
            //
            case 588: {
                
                //#line 4111 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
               setResult(nf.TypeNodeFromQualifiedName(pos(), "x10.lang.Object"));
                break;
            }
     
            //
            // Rule 590:  TypeParametersopt ::= $Empty
            //
            case 590: {
                
                //#line 4118 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                break;
            }
     
            //
            // Rule 592:  FormalParametersopt ::= $Empty
            //
            case 592: {
                
                //#line 4125 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 594:  Annotationsopt ::= $Empty
            //
            case 594: {
                
                //#line 4132 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                break;
            }
     
            //
            // Rule 596:  TypeDeclarationsopt ::= $Empty
            //
            case 596: {
                
                //#line 4139 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 598:  ImportDeclarationsopt ::= $Empty
            //
            case 598: {
                
                //#line 4146 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 600:  PackageDeclarationopt ::= $Empty
            //
            case 600:
                setResult(null);
                break;
 
            //
            // Rule 602:  ResultTypeopt ::= $Empty
            //
            case 602:
                setResult(null);
                break;
 
            //
            // Rule 604:  TypeArgumentsopt ::= $Empty
            //
            case 604: {
                
                //#line 4161 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 606:  TypePropertiesopt ::= $Empty
            //
            case 606: {
                
                //#line 4168 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypePropertyNode.class, false));
                break;
            }
     
            //
            // Rule 608:  Propertiesopt ::= $Empty
            //
            case 608: {
                
                //#line 4175 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                break;
            }
     
            //
            // Rule 610:  ,opt ::= $Empty
            //
            case 610:
                setResult(null);
                break;

    
            default:
                break;
        }
        return;
    }
}

