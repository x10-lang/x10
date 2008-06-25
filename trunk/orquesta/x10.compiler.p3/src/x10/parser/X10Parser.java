
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


    //#line 300 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
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
            // Rule 16:  PackageDeclaration ::= package PackageName ;
            //
            case 16: {
                //#line 774 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageName = (Name) getRhsSym(2);
                //#line 776 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(PackageName.toPackage());
                break;
            }
     
            //
            // Rule 17:  TypeDefDeclaration ::= TypeDefModifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 17: {
                //#line 780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 782 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 18:  TypeDefDeclaration ::= TypeDefModifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt ;
            //
            case 18: {
                //#line 792 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 792 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 792 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 792 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 792 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 794 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 19:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 19: {
                //#line 805 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 805 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 805 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 805 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 805 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 805 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 805 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 805 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 807 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 20:  Properties ::= ( PropertyList )
            //
            case 20: {
                //#line 822 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 824 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
   setResult(PropertyList);
           break;
            }  
            //
            // Rule 21:  PropertyList ::= Property
            //
            case 21: {
                //#line 827 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 829 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                break;
            }
     
            //
            // Rule 22:  PropertyList ::= PropertyList , Property
            //
            case 22: {
                //#line 834 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 834 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 836 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                PropertyList.add(Property);
                break;
            }
     
            //
            // Rule 23:  Property ::= Annotationsopt Identifier : Type
            //
            case 23: {
                //#line 841 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 841 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 841 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(4);
                //#line 843 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), Type, Identifier));
                break;
            }
     
            //
            // Rule 24:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 24: {
                //#line 847 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 847 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 847 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 847 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 847 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 847 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 847 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 847 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 849 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                break;
            }
     
            //
            // Rule 25:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 25: {
                //#line 864 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 864 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 866 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 26:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 26: {
                //#line 869 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 869 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 871 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 27:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 27: {
                //#line 874 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 874 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 874 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 876 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 28:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 28: {
                //#line 879 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 879 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 879 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 881 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 29:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypePropertiesopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 29: {
                //#line 885 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 885 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 885 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 885 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 885 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 885 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 885 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 887 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 30:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 30: {
                //#line 905 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(2);
                //#line 905 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 905 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 905 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 907 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 31:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 31: {
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 914 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Name b = new X10Name(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 32:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 32: {
                //#line 920 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 920 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 920 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 920 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 920 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 922 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Name b = new X10Name(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 33:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 33: {
                //#line 929 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 929 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 931 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 36:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt => Type
            //
            case 36: {
                //#line 941 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 941 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 941 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 941 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 941 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 943 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt));
                break;
            }
     
            //
            // Rule 39:  AnnotatedType ::= Type Annotations
            //
            case 39: {
                //#line 950 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 950 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 952 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn);
                break;
            }
     
            //
            // Rule 42:  ConstrainedType ::= ( Type )
            //
            case 42: {
                //#line 960 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 962 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Type);
                break;
            }
     
            //
            // Rule 47:  ClassOrInterfaceType ::= TypeName TypeArgumentsopt Argumentsopt DepParametersopt PlaceTypeSpecifieropt
            //
            case 47: {
                //#line 985 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 985 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 985 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 985 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 985 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(5);
                //#line 987 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                type = nf.AmbDepTypeNode(pos(), type, TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                break;
            }
     
            //
            // Rule 48:  DepParameters ::= { Conjunction }
            //
            case 48: {
                //#line 1010 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(2);
                //#line 1012 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.DepParameterExpr(pos(), Collections.EMPTY_LIST, Conjunction));
                break;
            }
     
            //
            // Rule 49:  DepParameters ::= { ExistentialList ; Conjunction }
            //
            case 49: {
                //#line 1015 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(2);
                //#line 1015 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(4);
                //#line 1017 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialList, Conjunction));
                break;
            }
     
            //
            // Rule 50:  TypeProperties ::= [ TypePropertyList ]
            //
            case 50: {
                //#line 1021 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertyList = (List) getRhsSym(2);
                //#line 1023 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(TypePropertyList);
                break;
            }
     
            //
            // Rule 51:  TypeParameters ::= [ TypeParameterList ]
            //
            case 51: {
                //#line 1027 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1029 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(TypeParameterList);
                break;
            }
     
            //
            // Rule 52:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 52: {
                //#line 1032 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1034 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(FormalParameterListopt);
                break;
            }
     
            //
            // Rule 53:  Conjunction ::= Expression
            //
            case 53: {
                //#line 1038 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1040 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Expression);
                break;
            }
     
            //
            // Rule 54:  Conjunction ::= Conjunction , Expression
            //
            case 54: {
                //#line 1043 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(1);
                //#line 1043 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1045 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), Conjunction, Binary.COND_AND, Expression));
                break;
            }
     
            //
            // Rule 55:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 55: {
                //#line 1049 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1049 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1051 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2));
                break;
            }
     
            //
            // Rule 56:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 56: {
                //#line 1054 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1054 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1056 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1));
                break;
            }
     
            //
            // Rule 57:  WhereClause ::= DepParameters
            //
            case 57: {
                //#line 1060 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1062 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(DepParameters);
                break;
            }
       
            //
            // Rule 58:  ExistentialListopt ::= $Empty
            //
            case 58: {
                
                //#line 1068 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(new ArrayList());
                break;
            }
       
            //
            // Rule 59:  ExistentialListopt ::= ExistentialList
            //
            case 59: {
                //#line 1071 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1073 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(ExistentialList);
                break;
            }
     
            //
            // Rule 60:  ExistentialList ::= FormalParameter
            //
            case 60: {
                //#line 1077 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1079 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                setResult(l);
                break;
            }
     
            //
            // Rule 61:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 61: {
                //#line 1084 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1084 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1086 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 63:  ValueClassDeclaration ::= ClassModifiersopt value Identifier TypePropertiesopt PropertyListopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 63: {
                //#line 1094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 1094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List PropertyListopt = (List) getRhsSym(5);
                //#line 1094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1096 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
    checkTypeName(Identifier);
    List props = PropertyListopt;
    DepParameterExpr ci = WhereClauseopt;
    ClassDecl cd = (nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
    extractFlags(ClassModifiersopt), Identifier, 
    TypePropertiesopt, props, ci, Superopt, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ClassModifiersopt));
    setResult(cd);
                break;
            }
     
            //
            // Rule 64:  ValueClassDeclaration ::= ClassModifiersopt value class Identifier TypePropertiesopt PropertyListopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 64: {
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(5);
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List PropertyListopt = (List) getRhsSym(6);
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(8);
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(9);
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(10);
                //#line 1108 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                checkTypeName(Identifier);
    List props = PropertyListopt;
    DepParameterExpr ci = WhereClauseopt;
    ClassDecl cd = (nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                                extractFlags(ClassModifiersopt), Identifier, 
                                TypePropertiesopt, props, ci, Superopt, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ClassModifiersopt));
    setResult(cd);
                break;
            }
     
            //
            // Rule 65:  ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt ConstructorBody
            //
            case 65: {
                //#line 1119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(9);
                //#line 1121 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 66:  Super ::= extends ClassType
            //
            case 66: {
                //#line 1135 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1137 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ClassType);
                break;
            }
     
            //
            // Rule 67:  FieldKeyword ::= val
            //
            case 67: {
                
                //#line 1143 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 68:  FieldKeyword ::= var
            //
            case 68: {
                
                //#line 1148 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                break;
            }
     
            //
            // Rule 69:  FieldKeyword ::= const
            //
            case 69: {
                
                //#line 1153 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                break;
            }
     
            //
            // Rule 70:  VarKeyword ::= val
            //
            case 70: {
                
                //#line 1161 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 71:  VarKeyword ::= var
            //
            case 71: {
                
                //#line 1166 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                break;
            }
     
            //
            // Rule 72:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 72: {
                //#line 1171 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1171 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1171 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1173 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 102:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 102: {
                //#line 1228 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1228 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1230 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.If(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 103:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 103: {
                //#line 1234 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1234 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1234 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1236 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                break;
            }
     
            //
            // Rule 104:  EmptyStatement ::= ;
            //
            case 104: {
                
                //#line 1242 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 105:  LabeledStatement ::= Identifier : Statement
            //
            case 105: {
                //#line 1246 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1246 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1248 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Labeled(pos(), Identifier, Statement));
                break;
            }
     
            //
            // Rule 106:  ExpressionStatement ::= StatementExpression ;
            //
            case 106: {
                //#line 1252 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1254 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 114:  AssertStatement ::= assert Expression ;
            //
            case 114: {
                //#line 1266 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1268 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assert(pos(), Expression));
                break;
            }
     
            //
            // Rule 115:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 115: {
                //#line 1271 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1271 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1273 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                break;
            }
     
            //
            // Rule 116:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 116: {
                //#line 1277 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1277 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1279 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                break;
            }
     
            //
            // Rule 117:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 117: {
                //#line 1283 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1283 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1285 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                break;
            }
     
            //
            // Rule 119:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 119: {
                //#line 1291 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1291 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1293 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                break;
            }
     
            //
            // Rule 120:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 120: {
                //#line 1298 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1298 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1300 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                break;
            }
     
            //
            // Rule 121:  SwitchLabels ::= SwitchLabel
            //
            case 121: {
                //#line 1307 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1309 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                break;
            }
     
            //
            // Rule 122:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 122: {
                //#line 1314 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1314 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1316 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                break;
            }
     
            //
            // Rule 123:  SwitchLabel ::= case ConstantExpression :
            //
            case 123: {
                //#line 1321 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1323 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                break;
            }
     
            //
            // Rule 124:  SwitchLabel ::= default :
            //
            case 124: {
                
                //#line 1328 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 125:  WhileStatement ::= while ( Expression ) Statement
            //
            case 125: {
                //#line 1332 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1332 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1334 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.While(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 126:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 126: {
                //#line 1338 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1338 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1340 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                break;
            }
     
            //
            // Rule 129:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 129: {
                //#line 1347 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1347 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1347 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1347 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1349 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                break;
            }
     
            //
            // Rule 131:  ForInit ::= LocalVariableDeclaration
            //
            case 131: {
                //#line 1354 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1356 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 133:  StatementExpressionList ::= StatementExpression
            //
            case 133: {
                //#line 1364 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1366 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                break;
            }
     
            //
            // Rule 134:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 134: {
                //#line 1371 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1371 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1373 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                //setResult(StatementExpressionList);
                break;
            }
     
            //
            // Rule 135:  BreakStatement ::= break Identifieropt ;
            //
            case 135: {
                //#line 1378 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1380 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Break(pos(), Identifieropt));
                break;
            }
     
            //
            // Rule 136:  ContinueStatement ::= continue Identifieropt ;
            //
            case 136: {
                //#line 1384 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1386 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                break;
            }
     
            //
            // Rule 137:  ReturnStatement ::= return Expressionopt ;
            //
            case 137: {
                //#line 1390 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1392 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Return(pos(), Expressionopt));
                break;
            }
     
            //
            // Rule 138:  ThrowStatement ::= throw Expression ;
            //
            case 138: {
                //#line 1396 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1398 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Throw(pos(), Expression));
                break;
            }
     
            //
            // Rule 139:  TryStatement ::= try Block Catches
            //
            case 139: {
                //#line 1402 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1402 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1404 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Try(pos(), Block, Catches));
                break;
            }
     
            //
            // Rule 140:  TryStatement ::= try Block Catchesopt Finally
            //
            case 140: {
                //#line 1407 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1407 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1407 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1409 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                break;
            }
     
            //
            // Rule 141:  Catches ::= CatchClause
            //
            case 141: {
                //#line 1413 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1415 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                break;
            }
     
            //
            // Rule 142:  Catches ::= Catches CatchClause
            //
            case 142: {
                //#line 1420 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1420 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1422 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                break;
            }
     
            //
            // Rule 143:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 143: {
                //#line 1427 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1427 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1429 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                break;
            }
     
            //
            // Rule 144:  Finally ::= finally Block
            //
            case 144: {
                //#line 1433 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1435 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 145:  NowStatement ::= now ( Clock ) Statement
            //
            case 145: {
                //#line 1439 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1439 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1441 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Now(pos(), Clock, Statement));
                break;
            }
     
            //
            // Rule 146:  ClockedClause ::= clocked ( ClockList )
            //
            case 146: {
                //#line 1445 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1447 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 147:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 147: {
                //#line 1451 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1451 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1451 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1453 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                break;
            }
     
            //
            // Rule 148:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 148: {
                //#line 1460 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1460 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1462 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                               ? nf.Here(pos(getLeftSpan()))
                                               : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 149:  WhenStatement ::= when ( Expression ) Statement
            //
            case 149: {
                //#line 1469 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1469 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1471 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.When(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 150:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 150: {
                //#line 1474 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1474 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1474 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1474 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1476 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 151:  ForEachStatement ::= foreach ( FormalParameter in Expression ) ClockedClauseopt Statement
            //
            case 151: {
                //#line 1481 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1481 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1481 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1481 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1483 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FlagsNode fn = FormalParameter.flags();
                Flags f = fn.flags();
                f = f.Final();
                fn = fn.flags(f);
                setResult(nf.ForEach(pos(),
                              FormalParameter.flags(fn),
                              Expression,
                              ClockedClauseopt,
                              Statement));
                break;
            }
     
            //
            // Rule 152:  AtEachStatement ::= ateach ( FormalParameter in Expression ) ClockedClauseopt Statement
            //
            case 152: {
                //#line 1495 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1495 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1495 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1495 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1497 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FlagsNode fn = FormalParameter.flags();
                Flags f = fn.flags();
                f = f.Final();
                fn = fn.flags(f);
                setResult(nf.AtEach(pos(),
                             FormalParameter.flags(fn),
                             Expression,
                             ClockedClauseopt,
                             Statement));
                break;
            }
     
            //
            // Rule 153:  EnhancedForStatement ::= for ( FormalParameter in Expression ) Statement
            //
            case 153: {
                //#line 1509 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1509 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1509 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1511 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FlagsNode fn = FormalParameter.flags();
                Flags f = fn.flags();
                f = f.Final();
                fn = fn.flags(f);
                setResult(nf.ForLoop(pos(),
                        FormalParameter.flags(fn),
                        Expression,
                        Statement));
                break;
            }
     
            //
            // Rule 154:  FinishStatement ::= finish Statement
            //
            case 154: {
                //#line 1522 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1524 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Finish(pos(),  Statement));
                break;
            }
     
            //
            // Rule 155:  AnnotationStatement ::= Annotations Statement
            //
            case 155: {
                //#line 1529 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 1529 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1531 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (Statement.ext() instanceof X10Ext && Annotations instanceof List) {
                    Statement = (Stmt) ((X10Ext) Statement.ext()).annotations((List) Annotations);
                }
                setResult(Statement);
                break;
            }
     
            //
            // Rule 156:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 156: {
                //#line 1538 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1540 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(PlaceExpression);
                break;
            }
     
            //
            // Rule 158:  NextStatement ::= next ;
            //
            case 158: {
                
                //#line 1548 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 159:  AwaitStatement ::= await Expression ;
            //
            case 159: {
                //#line 1552 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1554 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Await(pos(), Expression));
                break;
            }
     
            //
            // Rule 160:  ClockList ::= Clock
            //
            case 160: {
                //#line 1558 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1560 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                break;
            }
     
            //
            // Rule 161:  ClockList ::= ClockList , Clock
            //
            case 161: {
                //#line 1565 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1565 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1567 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 162:  Clock ::= Expression
            //
            case 162: {
                //#line 1573 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1575 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
    setResult(Expression);
                break;
            }
     
            //
            // Rule 163:  CastExpression ::= ConditionalExpression as Type
            //
            case 163: {
                //#line 1587 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1587 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1589 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Cast(pos(), Type, ConditionalExpression));
                break;
            }
     
            //
            // Rule 164:  CastExpression ::= ConditionalExpression to Type
            //
            case 164: {
                //#line 1592 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1592 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1594 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Cast(pos(), Type, ConditionalExpression));
                break;
            }
     
            //
            // Rule 165:  CastExpression ::= ConditionalExpression ! Expression
            //
            case 165: {
                //#line 1597 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1597 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1599 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.PlaceCast(pos(), Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 167:  TypePropertyList ::= TypeProperty
            //
            case 167: {
                //#line 1606 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypePropertyNode TypeProperty = (TypePropertyNode) getRhsSym(1);
                //#line 1608 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypePropertyNode.class, false);
                l.add(TypeProperty);
                setResult(l);
                break;
            }
     
            //
            // Rule 168:  TypePropertyList ::= TypePropertyList , TypeProperty
            //
            case 168: {
                //#line 1613 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypePropertyList = (List) getRhsSym(1);
                //#line 1613 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypePropertyNode TypeProperty = (TypePropertyNode) getRhsSym(3);
                //#line 1615 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypePropertyList.add(TypeProperty);
                setResult(TypePropertyList);
                break;
            }
     
            //
            // Rule 169:  TypeParameterList ::= TypeParameter
            //
            case 169: {
                //#line 1620 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 1622 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 170:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 170: {
                //#line 1627 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 1627 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 1629 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                break;
            }
     
            //
            // Rule 171:  TypeProperty ::= Identifier
            //
            case 171: {
                //#line 1634 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1636 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.INVARIANT));
                break;
            }
     
            //
            // Rule 172:  TypeProperty ::= + Identifier
            //
            case 172: {
                //#line 1639 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1641 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.COVARIANT));
                break;
            }
     
            //
            // Rule 173:  TypeProperty ::= - Identifier
            //
            case 173: {
                //#line 1644 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1646 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.CONTRAVARIANT));
                break;
            }
     
            //
            // Rule 174:  TypeParameter ::= Identifier
            //
            case 174: {
                //#line 1650 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1652 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                break;
            }
     
            //
            // Rule 175:  Primary ::= here
            //
            case 175: {
                
                //#line 1658 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                break;
            }
     
            //
            // Rule 178:  RegionExpression ::= Expression$expr1 .. Expression$expr2
            //
            case 178: {
                //#line 1665 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 1665 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 1667 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Call regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                break;
            }
     
            //
            // Rule 179:  RegionExpressionList ::= RegionExpression
            //
            case 179: {
                //#line 1672 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 1674 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                break;
            }
     
            //
            // Rule 180:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 180: {
                //#line 1679 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 1679 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 1681 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                break;
            }
     
            //
            // Rule 181:  Primary ::= [ RegionExpressionList ]
            //
            case 181: {
                //#line 1686 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(2);
                //#line 1688 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Receiver x10LangPointFactory = nf.ReceiverFromQualifiedName(pos(), "x10.lang.point.factory");
                Receiver x10LangRegionFactory = nf.ReceiverFromQualifiedName(pos(), "x10.lang.region.factory");
                Tuple tuple = nf.Tuple(pos(), x10LangPointFactory, x10LangRegionFactory, RegionExpressionList);
                setResult(tuple);
                break;
            }
     
            //
            // Rule 182:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 182: {
                //#line 1695 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 1695 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 1697 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ConstantDistMaker call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                break;
            }
     
            //
            // Rule 183:  ClosureExpression ::= TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt => ClosureBody
            //
            case 183: {
                //#line 1702 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(1);
                //#line 1702 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(2);
                //#line 1702 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 1702 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 1702 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 1702 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 1704 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Closure(pos(), TypeParametersopt, FormalParameters, WhereClauseopt, 
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt, Throwsopt, ClosureBody));
                break;
            }
     
            //
            // Rule 185:  LastExpression ::= Expression
            //
            case 185: {
                //#line 1710 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1712 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                break;
            }
     
            //
            // Rule 186:  ClosureBody ::= ClosureExpression
            //
            case 186: {
                //#line 1716 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ClosureExpression = (Expr) getRhsSym(1);
                //#line 1718 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), ClosureExpression, true)));
                break;
            }
     
            //
            // Rule 187:  ClosureBody ::= { BlockStatementsopt LastExpression }
            //
            case 187: {
                //#line 1721 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 1721 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(3);
                //#line 1723 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 189:  FutureExpression ::= future ClosureBody
            //
            case 189: {
                //#line 1731 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 1733 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                break;
            }
     
            //
            // Rule 190:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 190: {
                //#line 1736 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1736 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 1738 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                break;
            }
     
            //
            // Rule 191:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 191: {
                //#line 1741 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1741 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 1743 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                break;
            }
     
            //
            // Rule 192:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 192: {
                //#line 1746 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1746 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 1746 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 1748 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                break;
            }
     
            //
            // Rule 193:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 193:
                setResult(null);
                break;
 
            //
            // Rule 195:  DepParametersopt ::= $Empty
            //
            case 195:
                setResult(null);
                break;
 
            //
            // Rule 197:  PropertyListopt ::= $Empty
            //
            case 197: {
                
                //#line 1763 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                break;
            }
     
            //
            // Rule 199:  WhereClauseopt ::= $Empty
            //
            case 199:
                setResult(null);
                break;
 
            //
            // Rule 201:  ObjectKindopt ::= $Empty
            //
            case 201:
                setResult(null);
                break;
 
            //
            // Rule 203:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 203:
                setResult(null);
                break;
 
            //
            // Rule 205:  ClassModifiersopt ::= $Empty
            //
            case 205: {
                
                //#line 1782 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                break;
            }  
            //
            // Rule 207:  TypeDefModifiersopt ::= $Empty
            //
            case 207: {
                
                //#line 1788 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                break;
            }  
            //
            // Rule 209:  Unsafeopt ::= $Empty
            //
            case 209:
                setResult(null);
                break;
 
            //
            // Rule 210:  Unsafeopt ::= unsafe
            //
            case 210: {
                
                //#line 1796 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 211:  ClockedClauseopt ::= $Empty
            //
            case 211: {
                
                //#line 1803 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 213:  identifier ::= IDENTIFIER$ident
            //
            case 213: {
                //#line 1812 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 1814 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 214:  TypeName ::= Identifier
            //
            case 214: {
                //#line 1819 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1821 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 215:  TypeName ::= TypeName . Identifier
            //
            case 215: {
                //#line 1824 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 1824 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1826 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 217:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 217: {
                //#line 1836 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 1838 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(TypeArgumentList);
                break;
            }
     
            //
            // Rule 218:  TypeArgumentList ::= Type
            //
            case 218: {
                //#line 1843 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1845 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                break;
            }
     
            //
            // Rule 219:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 219: {
                //#line 1850 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 1850 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1852 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeArgumentList.add(Type);
                break;
            }
     
            //
            // Rule 220:  PackageName ::= Identifier
            //
            case 220: {
                //#line 1860 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1862 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 221:  PackageName ::= PackageName . Identifier
            //
            case 221: {
                //#line 1865 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageName = (Name) getRhsSym(1);
                //#line 1865 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1867 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 222:  ExpressionName ::= Identifier
            //
            case 222: {
                //#line 1881 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1883 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 223:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 223: {
                //#line 1886 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 1886 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1888 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 224:  MethodName ::= Identifier
            //
            case 224: {
                //#line 1896 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1898 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 225:  MethodName ::= AmbiguousName . Identifier
            //
            case 225: {
                //#line 1901 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 1901 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1903 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 226:  PackageOrTypeName ::= Identifier
            //
            case 226: {
                //#line 1911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1913 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 227:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 227: {
                //#line 1916 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageOrTypeName = (Name) getRhsSym(1);
                //#line 1916 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1918 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                break;
            }
     
            //
            // Rule 228:  AmbiguousName ::= Identifier
            //
            case 228: {
                //#line 1926 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1928 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 229:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 229: {
                //#line 1931 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 1931 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1933 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
               break;
            }
     
            //
            // Rule 230:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 230: {
                //#line 1943 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 1943 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 1943 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 1945 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 231:  ImportDeclarations ::= ImportDeclaration
            //
            case 231: {
                //#line 1959 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 1961 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 232:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 232: {
                //#line 1966 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 1966 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 1968 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 233:  TypeDeclarations ::= TypeDeclaration
            //
            case 233: {
                //#line 1974 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(1);
                //#line 1976 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 234:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 234: {
                //#line 1982 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 1982 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(2);
                //#line 1984 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 238:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 238: {
                //#line 1997 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(2);
                //#line 1999 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, TypeName.toString()));
                break;
            }
     
            //
            // Rule 239:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 239: {
                //#line 2003 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageOrTypeName = (Name) getRhsSym(2);
                //#line 2005 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, PackageOrTypeName.toString()));
                break;
            }
     
            //
            // Rule 243:  TypeDeclaration ::= ;
            //
            case 243: {
                
                //#line 2020 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(null);
                break;
            }
     
            //
            // Rule 245:  ClassModifiers ::= ClassModifier
            //
            case 245: {
                //#line 2028 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2030 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 246:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 246: {
                //#line 2035 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2035 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2037 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClassModifiers.addAll(ClassModifier);
                break;
            }
     
            //
            // Rule 247:  ClassModifier ::= Annotation
            //
            case 247: {
                //#line 2041 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2043 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 248:  ClassModifier ::= public
            //
            case 248: {
                
                //#line 2048 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 249:  ClassModifier ::= protected
            //
            case 249: {
                
                //#line 2053 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 250:  ClassModifier ::= private
            //
            case 250: {
                
                //#line 2058 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 251:  ClassModifier ::= abstract
            //
            case 251: {
                
                //#line 2063 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 252:  ClassModifier ::= static
            //
            case 252: {
                
                //#line 2068 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 253:  ClassModifier ::= final
            //
            case 253: {
                
                //#line 2073 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 254:  ClassModifier ::= strictfp
            //
            case 254: {
                
                //#line 2078 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                break;
            }
     
            //
            // Rule 255:  ClassModifier ::= safe
            //
            case 255: {
                
                //#line 2083 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                break;
            }
     
            //
            // Rule 256:  TypeDefModifiers ::= TypeDefModifier
            //
            case 256: {
                //#line 2087 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2089 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 257:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 257: {
                //#line 2094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2096 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                break;
            }
     
            //
            // Rule 258:  TypeDefModifier ::= Annotation
            //
            case 258: {
                //#line 2100 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2102 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 259:  TypeDefModifier ::= public
            //
            case 259: {
                
                //#line 2107 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 260:  TypeDefModifier ::= protected
            //
            case 260: {
                
                //#line 2112 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 261:  TypeDefModifier ::= private
            //
            case 261: {
                
                //#line 2117 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 262:  TypeDefModifier ::= abstract
            //
            case 262: {
                
                //#line 2122 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 263:  TypeDefModifier ::= static
            //
            case 263: {
                
                //#line 2127 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 264:  TypeDefModifier ::= final
            //
            case 264: {
                
                //#line 2132 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 265:  Interfaces ::= implements InterfaceTypeList
            //
            case 265: {
                //#line 2139 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2141 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 266:  InterfaceTypeList ::= InterfaceType
            //
            case 266: {
                //#line 2145 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode InterfaceType = (TypeNode) getRhsSym(1);
                //#line 2147 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 267:  InterfaceTypeList ::= InterfaceTypeList , InterfaceType
            //
            case 267: {
                //#line 2152 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2152 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                //#line 2154 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceTypeList.add(InterfaceType);
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 268:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 268: {
                //#line 2162 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2164 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                break;
            }
     
            //
            // Rule 270:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 270: {
                //#line 2169 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2169 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2171 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                break;
            }
     
            //
            // Rule 272:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 272: {
                //#line 2177 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block InstanceInitializer = (Block) getRhsSym(1);
                //#line 2179 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InstanceInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 273:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 273: {
                //#line 2184 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block StaticInitializer = (Block) getRhsSym(1);
                //#line 2186 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(StaticInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 274:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 274: {
                //#line 2191 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2193 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 276:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 276: {
                //#line 2200 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                MethodDecl MethodDeclaration = (MethodDecl) getRhsSym(1);
                //#line 2202 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 277:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 277: {
                //#line 2207 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2209 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 278:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 278: {
                //#line 2214 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2216 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 279:  ClassMemberDeclaration ::= ;
            //
            case 279: {
                
                //#line 2223 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                break;
            }
     
            //
            // Rule 280:  FormalDeclarators ::= FormalDeclarator
            //
            case 280: {
                //#line 2228 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2230 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 281:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 281: {
                //#line 2235 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2235 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2237 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FormalDeclarators.add(FormalDeclarator);
                break;
            }
     
            //
            // Rule 282:  FieldDeclarators ::= FieldDeclarator
            //
            case 282: {
                //#line 2242 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2244 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 283:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 283: {
                //#line 2249 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2249 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2251 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                break;
            }
     
            //
            // Rule 284:  VariableDeclarators ::= VariableDeclarator
            //
            case 284: {
                //#line 2257 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2259 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 285:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 285: {
                //#line 2264 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2264 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2266 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                break;
            }
     
            //
            // Rule 287:  FieldModifiers ::= FieldModifier
            //
            case 287: {
                //#line 2273 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2275 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 288:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 288: {
                //#line 2280 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2280 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2282 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FieldModifiers.addAll(FieldModifier);
                break;
            }
     
            //
            // Rule 289:  FieldModifier ::= Annotation
            //
            case 289: {
                //#line 2286 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2288 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 290:  FieldModifier ::= public
            //
            case 290: {
                
                //#line 2293 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 291:  FieldModifier ::= protected
            //
            case 291: {
                
                //#line 2298 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 292:  FieldModifier ::= private
            //
            case 292: {
                
                //#line 2303 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 293:  FieldModifier ::= transient
            //
            case 293: {
                
                //#line 2308 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.TRANSIENT)));
                break;
            }
     
            //
            // Rule 294:  FieldModifier ::= volatile
            //
            case 294: {
                
                //#line 2313 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.VOLATILE)));
                break;
            }
     
            //
            // Rule 295:  ResultType ::= : Type
            //
            case 295: {
                //#line 2317 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2319 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Type);
                break;
            }
     
            //
            // Rule 296:  FormalParameters ::= ( FormalParameterList )
            //
            case 296: {
                //#line 2323 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2325 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(FormalParameterList);
                break;
            }
     
            //
            // Rule 297:  FormalParameterList ::= FormalParameter
            //
            case 297: {
                //#line 2329 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2331 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 298:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 298: {
                //#line 2336 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2336 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2338 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FormalParameterList.add(FormalParameter);
                break;
            }
     
            //
            // Rule 299:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 299: {
                //#line 2342 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2342 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2344 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 300:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 300: {
                //#line 2364 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2364 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2364 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2366 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 301:  FormalParameter ::= Type
            //
            case 301: {
                //#line 2386 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2388 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), ""), Collections.EMPTY_LIST);
            setResult(f);
                break;
            }
     
            //
            // Rule 302:  VariableModifiers ::= VariableModifier
            //
            case 302: {
                //#line 2394 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2396 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 303:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 303: {
                //#line 2401 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2401 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2403 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableModifiers.addAll(VariableModifier);
                break;
            }
     
            //
            // Rule 304:  VariableModifier ::= Annotation
            //
            case 304: {
                //#line 2407 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2409 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 305:  MethodModifiers ::= MethodModifier
            //
            case 305: {
                //#line 2416 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2418 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 306:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 306: {
                //#line 2423 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2423 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2425 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                MethodModifiers.addAll(MethodModifier);
                break;
            }
     
            //
            // Rule 307:  MethodModifier ::= Annotation
            //
            case 307: {
                //#line 2429 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2431 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 308:  MethodModifier ::= public
            //
            case 308: {
                
                //#line 2436 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 309:  MethodModifier ::= protected
            //
            case 309: {
                
                //#line 2441 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 310:  MethodModifier ::= private
            //
            case 310: {
                
                //#line 2446 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 311:  MethodModifier ::= abstract
            //
            case 311: {
                
                //#line 2451 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 312:  MethodModifier ::= static
            //
            case 312: {
                
                //#line 2456 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 313:  MethodModifier ::= final
            //
            case 313: {
                
                //#line 2461 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                break;
            }
     
            //
            // Rule 314:  MethodModifier ::= native
            //
            case 314: {
                
                //#line 2466 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                break;
            }
     
            //
            // Rule 315:  MethodModifier ::= strictfp
            //
            case 315: {
                
                //#line 2471 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                break;
            }
     
            //
            // Rule 316:  MethodModifier ::= atomic
            //
            case 316: {
                
                //#line 2476 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                break;
            }
     
            //
            // Rule 317:  MethodModifier ::= extern
            //
            case 317: {
                
                //#line 2481 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                break;
            }
     
            //
            // Rule 318:  MethodModifier ::= safe
            //
            case 318: {
                
                //#line 2486 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                break;
            }
     
            //
            // Rule 319:  MethodModifier ::= sequential
            //
            case 319: {
                
                //#line 2491 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                break;
            }
     
            //
            // Rule 320:  MethodModifier ::= local
            //
            case 320: {
                
                //#line 2496 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.LOCAL)));
                break;
            }
     
            //
            // Rule 321:  MethodModifier ::= nonblocking
            //
            case 321: {
                
                //#line 2501 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                break;
            }
     
            //
            // Rule 322:  Throws ::= throws ExceptionTypeList
            //
            case 322: {
                //#line 2506 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 2508 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 323:  ExceptionTypeList ::= ExceptionType
            //
            case 323: {
                //#line 2512 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 2514 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                break;
            }
     
            //
            // Rule 324:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 324: {
                //#line 2519 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 2519 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 2521 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExceptionTypeList.add(ExceptionType);
                // setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 326:  MethodBody ::= = LastExpression ;
            //
            case 326: {
                //#line 2528 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 2530 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Block(pos(), LastExpression));
                break;
            }
     
            //
            // Rule 327:  MethodBody ::= = { BlockStatementsopt LastExpression }
            //
            case 327: {
                //#line 2533 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2533 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2535 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 328:  MethodBody ::= = Block
            //
            case 328: {
                //#line 2541 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2543 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 329:  MethodBody ::= ;
            //
            case 329:
                setResult(null);
                break;
 
            //
            // Rule 330:  InstanceInitializer ::= Block
            //
            case 330: {
                //#line 2549 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(1);
                //#line 2551 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(), Flags.NONE), Block));
                break;
            }
     
            //
            // Rule 331:  StaticInitializer ::= static Block
            //
            case 331: {
                //#line 2555 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2557 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(getLeftSpan()), Flags.STATIC), Block));
                break;
            }
     
            //
            // Rule 332:  SimpleTypeName ::= Identifier
            //
            case 332: {
                //#line 2561 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2563 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 333:  ConstructorModifiers ::= ConstructorModifier
            //
            case 333: {
                //#line 2567 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 2569 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 334:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 334: {
                //#line 2574 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 2574 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 2576 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                break;
            }
     
            //
            // Rule 335:  ConstructorModifier ::= Annotation
            //
            case 335: {
                //#line 2580 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2582 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 336:  ConstructorModifier ::= public
            //
            case 336: {
                
                //#line 2587 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 337:  ConstructorModifier ::= protected
            //
            case 337: {
                
                //#line 2592 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 338:  ConstructorModifier ::= private
            //
            case 338: {
                
                //#line 2597 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 339:  ConstructorBody ::= = ConstructorBlock
            //
            case 339: {
                //#line 2601 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 2603 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ConstructorBlock);
                break;
            }
     
            //
            // Rule 340:  ConstructorBody ::= ;
            //
            case 340:
                setResult(null);
                break;
 
            //
            // Rule 341:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 341: {
                //#line 2609 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 2609 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2611 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 342:  ConstructorBlock ::= ExplicitConstructorInvocation
            //
            case 342: {
                //#line 2625 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(1);
                //#line 2627 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 343:  ConstructorBlock ::= AssignPropertyCall
            //
            case 343: {
                //#line 2633 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(1);
                //#line 2635 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 344:  Arguments ::= ( ArgumentListopt )
            //
            case 344: {
                //#line 2642 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 2644 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ArgumentListopt);
                break;
            }
     
            //
            // Rule 346:  InterfaceModifiers ::= InterfaceModifier
            //
            case 346: {
                //#line 2652 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 2654 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 347:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 347: {
                //#line 2659 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 2659 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 2661 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                break;
            }
     
            //
            // Rule 348:  InterfaceModifier ::= Annotation
            //
            case 348: {
                //#line 2665 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2667 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 349:  InterfaceModifier ::= public
            //
            case 349: {
                
                //#line 2672 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                break;
            }
     
            //
            // Rule 350:  InterfaceModifier ::= protected
            //
            case 350: {
                
                //#line 2677 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                break;
            }
     
            //
            // Rule 351:  InterfaceModifier ::= private
            //
            case 351: {
                
                //#line 2682 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                break;
            }
     
            //
            // Rule 352:  InterfaceModifier ::= abstract
            //
            case 352: {
                
                //#line 2687 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                break;
            }
     
            //
            // Rule 353:  InterfaceModifier ::= static
            //
            case 353: {
                
                //#line 2692 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                break;
            }
     
            //
            // Rule 354:  InterfaceModifier ::= strictfp
            //
            case 354: {
                
                //#line 2697 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                break;
            }
     
            //
            // Rule 355:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 355: {
                //#line 2701 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode InterfaceType = (TypeNode) getRhsSym(2);
                //#line 2703 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 356:  ExtendsInterfaces ::= ExtendsInterfaces , InterfaceType
            //
            case 356: {
                //#line 2708 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 2708 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                //#line 2710 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExtendsInterfaces.add(InterfaceType);
                // setResult(ExtendsInterfaces);
                break;
            }
     
            //
            // Rule 357:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 357: {
                //#line 2718 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 2720 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                break;
            }
     
            //
            // Rule 359:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 359: {
                //#line 2725 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 2725 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 2727 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                break;
            }
     
            //
            // Rule 360:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 360: {
                //#line 2732 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                MethodDecl MethodDeclaration = (MethodDecl) getRhsSym(1);
                //#line 2734 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 361:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 361: {
                //#line 2739 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2741 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 362:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 362: {
                //#line 2746 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2748 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 363:  InterfaceMemberDeclaration ::= ;
            //
            case 363: {
                
                //#line 2755 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 364:  Annotations ::= Annotation
            //
            case 364: {
                //#line 2759 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2761 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                break;
            }
     
            //
            // Rule 365:  Annotations ::= Annotations Annotation
            //
            case 365: {
                //#line 2766 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 2766 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 2768 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Annotations.add(Annotation);
                break;
            }
     
            //
            // Rule 366:  Annotation ::= @ InterfaceType
            //
            case 366: {
                //#line 2772 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode InterfaceType = (TypeNode) getRhsSym(2);
                //#line 2774 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.AnnotationNode(pos(), InterfaceType));
                break;
            }
     
            //
            // Rule 367:  SimpleName ::= Identifier
            //
            case 367: {
                //#line 2778 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2780 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10Name(nf, ts, pos(), Identifier));
                break;
            }
     
            //
            // Rule 368:  Identifier ::= identifier
            //
            case 368: {
                //#line 2784 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 2786 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 369:  ArrayInitializer ::= [ VariableInitializersopt ,opt$opt ]
            //
            case 369: {
                //#line 2792 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializersopt = (List) getRhsSym(2);
                //#line 2792 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object opt = (Object) getRhsSym(3);
                //#line 2794 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (VariableInitializersopt == null)
                     setResult(nf.ArrayInit(pos()));
                else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
                break;
            }
     
            //
            // Rule 370:  VariableInitializers ::= VariableInitializer
            //
            case 370: {
                //#line 2800 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 2802 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 371:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 371: {
                //#line 2807 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 2807 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 2809 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                break;
            }
     
            //
            // Rule 372:  Block ::= { BlockStatementsopt }
            //
            case 372: {
                //#line 2825 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 2827 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                break;
            }
     
            //
            // Rule 373:  BlockStatements ::= BlockStatement
            //
            case 373: {
                //#line 2831 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 2833 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                break;
            }
     
            //
            // Rule 374:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 374: {
                //#line 2838 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 2838 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 2840 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                break;
            }
     
            //
            // Rule 376:  BlockStatement ::= ClassDeclaration
            //
            case 376: {
                //#line 2846 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2848 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                break;
            }
     
            //
            // Rule 377:  BlockStatement ::= Statement
            //
            case 377: {
                //#line 2853 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 2855 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                break;
            }
     
            //
            // Rule 378:  IdentifierList ::= Identifier
            //
            case 378: {
                //#line 2861 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2863 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 379:  IdentifierList ::= IdentifierList , Identifier
            //
            case 379: {
                //#line 2868 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 2868 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2870 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                IdentifierList.add(Identifier);
                break;
            }
     
            //
            // Rule 380:  FormalDeclarator ::= Identifier : Type
            //
            case 380: {
                //#line 2874 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2874 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2876 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, Type, null });
                break;
            }
     
            //
            // Rule 381:  FormalDeclarator ::= ( IdentifierList ) : Type
            //
            case 381: {
                //#line 2879 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2879 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(5);
                //#line 2881 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, Type, null });
                break;
            }
     
            //
            // Rule 382:  FormalDeclarator ::= Identifier ( IdentifierList ) : Type
            //
            case 382: {
                //#line 2884 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2884 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2884 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(6);
                //#line 2886 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, Type, null });
                break;
            }
     
            //
            // Rule 383:  FieldDeclarator ::= Identifier WhereClauseopt : Type
            //
            case 383: {
                //#line 2890 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2890 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2890 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(4);
                //#line 2892 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, WhereClauseopt, Type, null });
                break;
            }
     
            //
            // Rule 384:  FieldDeclarator ::= Identifier WhereClauseopt ResultTypeopt = VariableInitializer
            //
            case 384: {
                //#line 2895 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2895 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2895 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2895 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(5);
                //#line 2897 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, WhereClauseopt, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 385:  VariableDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 385: {
                //#line 2901 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2901 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2901 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 2903 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 386:  VariableDeclarator ::= ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 386: {
                //#line 2906 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2906 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2906 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 2908 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 387:  VariableDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 387: {
                //#line 2911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2911 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 2913 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, VariableInitializer });
                break;
            }
     
            //
            // Rule 389:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 389: {
                //#line 2919 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2919 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2919 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 2921 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                        	explodedFormals.add(nf.LocalDecl(id.position(), fn, nf.UnknownTypeNode(name.position()), id, init != null ? nf.X10ArrayAccess1(init.position(), nf.Local(init.position(), name), nf.IntLit(init.position(), IntLit.INT, index)) : null));
                        	index++;
                        }
                        l.add(ld);
                    }
                l.addAll(s); 
                setResult(l);
                break;
            }
     
            //
            // Rule 390:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 390: {
                //#line 2951 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2951 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2951 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 2953 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                        	explodedFormals.add(nf.LocalDecl(id.position(), fn, nf.UnknownTypeNode(name.position()), id, init != null ? nf.X10ArrayAccess1(init.position(), nf.Local(init.position(), name), nf.IntLit(init.position(), IntLit.INT, index)) : null));
                        	index++;
                        }
                        l.add(ld);
                    }
                l.addAll(s); 
                setResult(l);
                break;
            }
     
            //
            // Rule 393:  Primary ::= TypeName . class
            //
            case 393: {
                //#line 2993 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 2995 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (TypeName instanceof Name)
                {
                    Name a = (Name) TypeName;
                    setResult(nf.ClassLit(pos(), a.toType()));
                }
                else assert(false);
                break;
            }
     
            //
            // Rule 394:  Primary ::= self
            //
            case 394: {
                
                //#line 3005 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Self(pos()));
                break;
            }
     
            //
            // Rule 395:  Primary ::= this
            //
            case 395: {
                
                //#line 3010 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 396:  Primary ::= ClassName . this
            //
            case 396: {
                //#line 3013 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3015 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 397:  Primary ::= ( Expression )
            //
            case 397: {
                //#line 3018 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3020 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ParExpr(pos(), Expression));
                break;
            }
     
            //
            // Rule 402:  OperatorFunction ::= TypeName . +
            //
            case 402: {
                //#line 3028 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3030 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 403:  OperatorFunction ::= TypeName . -
            //
            case 403: {
                //#line 3039 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3041 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 404:  OperatorFunction ::= TypeName . *
            //
            case 404: {
                //#line 3050 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3052 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 405:  OperatorFunction ::= TypeName . /
            //
            case 405: {
                //#line 3061 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3063 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 406:  OperatorFunction ::= TypeName . %
            //
            case 406: {
                //#line 3072 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3074 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 407:  OperatorFunction ::= TypeName . &
            //
            case 407: {
                //#line 3083 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3085 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 408:  OperatorFunction ::= TypeName . |
            //
            case 408: {
                //#line 3094 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3096 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 409:  OperatorFunction ::= TypeName . ^
            //
            case 409: {
                //#line 3105 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3107 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 410:  OperatorFunction ::= TypeName . <<
            //
            case 410: {
                //#line 3116 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3118 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 411:  OperatorFunction ::= TypeName . >>
            //
            case 411: {
                //#line 3127 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3129 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 412:  OperatorFunction ::= TypeName . >>>
            //
            case 412: {
                //#line 3138 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3140 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 413:  OperatorFunction ::= TypeName . <
            //
            case 413: {
                //#line 3149 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3151 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 414:  OperatorFunction ::= TypeName . <=
            //
            case 414: {
                //#line 3160 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3162 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 415:  OperatorFunction ::= TypeName . >=
            //
            case 415: {
                //#line 3171 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3173 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 416:  OperatorFunction ::= TypeName . >
            //
            case 416: {
                //#line 3182 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3184 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 417:  OperatorFunction ::= TypeName . ==
            //
            case 417: {
                //#line 3193 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3195 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 418:  OperatorFunction ::= TypeName . !=
            //
            case 418: {
                //#line 3204 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 3206 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 419:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 419: {
                //#line 3217 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                //#line 3219 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 420:  Literal ::= LongLiteral$LongLiteral
            //
            case 420: {
                //#line 3223 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken LongLiteral = (IToken) getRhsIToken(1);
                //#line 3225 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 421:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 421: {
                //#line 3229 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                //#line 3231 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 422:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 422: {
                //#line 3235 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                //#line 3237 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 423:  Literal ::= BooleanLiteral
            //
            case 423: {
                //#line 3241 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3243 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 424:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 424: {
                //#line 3246 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                //#line 3248 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 425:  Literal ::= StringLiteral$str
            //
            case 425: {
                //#line 3252 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3254 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 426:  Literal ::= null
            //
            case 426: {
                
                //#line 3260 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 427:  BooleanLiteral ::= true$trueLiteral
            //
            case 427: {
                //#line 3264 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3266 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 428:  BooleanLiteral ::= false$falseLiteral
            //
            case 428: {
                //#line 3269 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3271 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 429:  ArgumentList ::= Expression
            //
            case 429: {
                //#line 3278 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3280 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                break;
            }
     
            //
            // Rule 430:  ArgumentList ::= ArgumentList , Expression
            //
            case 430: {
                //#line 3285 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3285 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3287 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ArgumentList.add(Expression);
                break;
            }
     
            //
            // Rule 431:  FieldAccess ::= Primary . Identifier
            //
            case 431: {
                //#line 3291 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3291 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3293 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                break;
            }
     
            //
            // Rule 432:  FieldAccess ::= super . Identifier
            //
            case 432: {
                //#line 3296 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3298 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), Identifier));
                break;
            }
     
            //
            // Rule 433:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 433: {
                //#line 3301 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3301 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3301 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3303 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                break;
            }
     
            //
            // Rule 434:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 434: {
                //#line 3307 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name MethodName = (Name) getRhsSym(1);
                //#line 3307 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3307 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3309 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 435:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 435: {
                //#line 3314 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3314 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3314 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3314 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3316 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 436:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 436: {
                //#line 3319 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3319 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3319 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3321 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 437:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 437: {
                //#line 3324 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3324 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3324 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3324 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3324 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3326 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 438:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 438: {
                //#line 3329 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3329 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3329 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3331 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ClosureCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                break;
            }
     
            //
            // Rule 439:  MethodSelection ::= MethodName . TypeParametersopt ( FormalParameterListopt )
            //
            case 439: {
                //#line 3335 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name MethodName = (Name) getRhsSym(1);
                //#line 3335 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 3335 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(5);
                //#line 3337 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 440:  MethodSelection ::= Primary . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 440: {
                //#line 3348 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3348 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3348 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3348 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3350 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 441:  MethodSelection ::= super . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 441: {
                //#line 3360 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3360 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3360 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3362 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 442:  MethodSelection ::= ClassName . super$sup . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 442: {
                //#line 3372 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 3372 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3372 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3372 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(7);
                //#line 3372 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(9);
                //#line 3374 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 445:  PostfixExpression ::= ExpressionName
            //
            case 445: {
                //#line 3387 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 3389 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 448:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 448: {
                //#line 3395 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 3397 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 449:  PostDecrementExpression ::= PostfixExpression --
            //
            case 449: {
                //#line 3401 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 3403 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 452:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 452: {
                //#line 3409 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3411 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 453:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 453: {
                //#line 3414 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3416 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 455:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 455: {
                //#line 3421 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3423 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 456:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 456: {
                //#line 3427 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 3429 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 458:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 458: {
                //#line 3434 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 3436 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 459:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 459: {
                //#line 3439 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3439 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 3441 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e);
                break;
            }
     
            //
            // Rule 460:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 460: {
                //#line 3446 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 3448 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 462:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 462: {
                //#line 3453 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 3453 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 3455 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                break;
            }
     
            //
            // Rule 463:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 463: {
                //#line 3458 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 3458 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 3460 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                break;
            }
     
            //
            // Rule 464:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 464: {
                //#line 3463 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 3463 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 3465 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                break;
            }
     
            //
            // Rule 466:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 466: {
                //#line 3470 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 3470 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 3472 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 467:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 467: {
                //#line 3475 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 3475 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 3477 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 469:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 469: {
                //#line 3482 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 3482 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 3484 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                break;
            }
     
            //
            // Rule 470:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 470: {
                //#line 3487 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 3487 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 3489 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 471:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 471: {
                //#line 3492 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 3492 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 3494 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 474:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 474: {
                //#line 3500 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3500 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3502 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
                break;
            }
     
            //
            // Rule 475:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 475: {
                //#line 3505 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3505 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3507 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
                break;
            }
     
            //
            // Rule 476:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 476: {
                //#line 3510 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3510 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3512 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
                break;
            }
     
            //
            // Rule 477:  RelationalExpression ::= RelationalExpression >= ShiftExpression
            //
            case 477: {
                //#line 3515 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3515 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 3517 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
                break;
            }
     
            //
            // Rule 478:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 478: {
                //#line 3520 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 3520 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3522 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                break;
            }
     
            //
            // Rule 480:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 480: {
                //#line 3533 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 3533 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 3535 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                break;
            }
     
            //
            // Rule 481:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 481: {
                //#line 3538 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 3538 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 3540 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                break;
            }
     
            //
            // Rule 483:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 483: {
                //#line 3545 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 3545 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 3547 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                break;
            }
     
            //
            // Rule 485:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 485: {
                //#line 3552 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 3552 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 3554 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                break;
            }
     
            //
            // Rule 487:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 487: {
                //#line 3559 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 3559 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 3561 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 489:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 489: {
                //#line 3566 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 3566 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 3568 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                break;
            }
     
            //
            // Rule 491:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 491: {
                //#line 3573 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 3573 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 3575 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                break;
            }
     
            //
            // Rule 493:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 493: {
                //#line 3580 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 3580 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3580 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 3582 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 496:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 496: {
                //#line 3589 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 3589 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 3589 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 3591 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 497:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 497: {
                //#line 3594 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name e1 = (Name) getRhsSym(1);
                //#line 3594 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 3594 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 3594 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 3596 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
             //   setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                setResult(nf.Assign(pos(), nf.X10ArrayAccess(pos(), e1.toExpr(), ArgumentList), AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 498:  LeftHandSide ::= ExpressionName
            //
            case 498: {
                //#line 3601 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 3603 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 500:  AssignmentOperator ::= =
            //
            case 500: {
                
                //#line 3610 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 501:  AssignmentOperator ::= *=
            //
            case 501: {
                
                //#line 3615 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 502:  AssignmentOperator ::= /=
            //
            case 502: {
                
                //#line 3620 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 503:  AssignmentOperator ::= %=
            //
            case 503: {
                
                //#line 3625 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 504:  AssignmentOperator ::= +=
            //
            case 504: {
                
                //#line 3630 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 505:  AssignmentOperator ::= -=
            //
            case 505: {
                
                //#line 3635 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 506:  AssignmentOperator ::= <<=
            //
            case 506: {
                
                //#line 3640 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 507:  AssignmentOperator ::= >>=
            //
            case 507: {
                
                //#line 3645 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 508:  AssignmentOperator ::= >>>=
            //
            case 508: {
                
                //#line 3650 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 509:  AssignmentOperator ::= &=
            //
            case 509: {
                
                //#line 3655 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 510:  AssignmentOperator ::= ^=
            //
            case 510: {
                
                //#line 3660 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 511:  AssignmentOperator ::= |=
            //
            case 511: {
                
                //#line 3665 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 514:  Catchesopt ::= $Empty
            //
            case 514: {
                
                //#line 3678 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 516:  Identifieropt ::= $Empty
            //
            case 516:
                setResult(null);
                break;
 
            //
            // Rule 517:  Identifieropt ::= Identifier
            //
            case 517: {
                //#line 3685 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3687 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Identifier);
                break;
            }
     
            //
            // Rule 518:  ForUpdateopt ::= $Empty
            //
            case 518: {
                
                //#line 3693 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 520:  Expressionopt ::= $Empty
            //
            case 520:
                setResult(null);
                break;
 
            //
            // Rule 522:  ForInitopt ::= $Empty
            //
            case 522: {
                
                //#line 3704 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 524:  SwitchLabelsopt ::= $Empty
            //
            case 524: {
                
                //#line 3711 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 526:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 526: {
                
                //#line 3718 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 528:  VariableModifiersopt ::= $Empty
            //
            case 528: {
                
                //#line 3725 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 530:  VariableInitializersopt ::= $Empty
            //
            case 530:
                setResult(null);
                break;
 
            //
            // Rule 532:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 532: {
                
                //#line 3736 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 534:  ExtendsInterfacesopt ::= $Empty
            //
            case 534: {
                
                //#line 3743 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 536:  InterfaceModifiersopt ::= $Empty
            //
            case 536: {
                
                //#line 3750 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 538:  ClassBodyopt ::= $Empty
            //
            case 538:
                setResult(null);
                break;
 
            //
            // Rule 540:  Argumentsopt ::= $Empty
            //
            case 540: {
                
                //#line 3761 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 542:  ArgumentListopt ::= $Empty
            //
            case 542: {
                
                //#line 3768 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 544:  BlockStatementsopt ::= $Empty
            //
            case 544: {
                
                //#line 3775 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 546:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 546:
                setResult(null);
                break;
 
            //
            // Rule 548:  ConstructorModifiersopt ::= $Empty
            //
            case 548: {
                
                //#line 3786 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 550:  FormalParameterListopt ::= $Empty
            //
            case 550: {
                
                //#line 3793 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 552:  Throwsopt ::= $Empty
            //
            case 552: {
                
                //#line 3800 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 554:  MethodModifiersopt ::= $Empty
            //
            case 554: {
                
                //#line 3807 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 556:  FieldModifiersopt ::= $Empty
            //
            case 556: {
                
                //#line 3814 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 558:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 558: {
                
                //#line 3821 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 560:  Interfacesopt ::= $Empty
            //
            case 560: {
                
                //#line 3828 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 562:  Superopt ::= $Empty
            //
            case 562: {
                
                //#line 3835 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
               setResult(nf.TypeNodeFromQualifiedName(pos(), "x10.lang.Object"));
                break;
            }
     
            //
            // Rule 564:  TypeParametersopt ::= $Empty
            //
            case 564: {
                
                //#line 3842 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                break;
            }
     
            //
            // Rule 566:  FormalParametersopt ::= $Empty
            //
            case 566: {
                
                //#line 3849 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 568:  Annotationsopt ::= $Empty
            //
            case 568: {
                
                //#line 3856 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                break;
            }
     
            //
            // Rule 570:  TypeDeclarationsopt ::= $Empty
            //
            case 570: {
                
                //#line 3863 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 572:  ImportDeclarationsopt ::= $Empty
            //
            case 572: {
                
                //#line 3870 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 574:  PackageDeclarationopt ::= $Empty
            //
            case 574:
                setResult(null);
                break;
 
            //
            // Rule 576:  ResultTypeopt ::= $Empty
            //
            case 576:
                setResult(null);
                break;
 
            //
            // Rule 578:  TypeArgumentsopt ::= $Empty
            //
            case 578: {
                
                //#line 3885 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 580:  TypePropertiesopt ::= $Empty
            //
            case 580: {
                
                //#line 3892 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypePropertyNode.class, false));
                break;
            }
     
            //
            // Rule 582:  Propertiesopt ::= $Empty
            //
            case 582: {
                
                //#line 3899 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                break;
            }
     
            //
            // Rule 584:  ,opt ::= $Empty
            //
            case 584:
                setResult(null);
                break;

    
            default:
                break;
        }
        return;
    }
}

