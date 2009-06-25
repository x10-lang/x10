
//#line 18 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
//
// Licensed Material
// (C) Copyright IBM Corp, 2006
//

//
// This is the grammar specification from the Final Draft of the generic spec.
// It has been modified by Philippe Charles and Vijay Saraswat for use with 
// X10. 
// (1) Removed TypeParameters from class/interface/method declarations
// (2) Removed TypeParameters from types.
// (3) Removed Annotations -- cause conflicts with @ used in places.
// (4) Removed EnumDeclarations.
// 12/28/2004

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
import polyglot.ext.x10.ast.X10NodeFactory;
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


    //#line 288 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
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
        return new Identifier(pos(i), super.getName(i), X10Parsersym.TK_IDENTIFIER);
    }
    private String comment(int i) {
        String s = super.getName(i);
        if (s != null && s.startsWith("/**") && s.endsWith("*/")) {
            return s +"\n";
        }
        return null;
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

    private X10Flags extractFlags(List l) {
        X10Flags f = X10Flags.toX10Flags(Flags.NONE);
        for (Iterator i = l.iterator(); i.hasNext(); ) {
            Object o = i.next();
            if (o instanceof X10Flags) {
                f = f.setX((X10Flags) o);
            }
            else if (o instanceof Flags) {
                f = X10Flags.toX10Flags(f.set((Flags) o));
            }
        }
        return f;
    }

    /**
     * Pretend to have parsed
     * <code>
     * new Operator.Pointwise() { public <T> apply(Formal, <T> _) MethodBody }
     * </code>
     * instead of (Formal) MethodBody. Note that Formal may have
     * exploded vars.
     * @author vj
    */
    private Expr makeInitializer(Position pos, TypeNode resultType,
                                 X10Formal f, Block body) {
      Flags flags = Flags.PUBLIC;
      TypeNode appResultType = resultType;
      if (!(resultType instanceof CanonicalTypeNode)) {
        appResultType = nf.TypeNodeFromQualifiedName(pos, "x10.compilergenerated.Parameter1");
      }
      // resultType is a.
      List l1 = new TypedList(new LinkedList(), X10Formal.class, false);
      l1.add(f);
      l1.add(nf.Formal(pos, Flags.FINAL, appResultType, nf.Id(pos, "_")));
      MethodDecl decl = nf.MethodDecl(pos, flags, appResultType,
                                    nf.Id(pos, "apply"), l1,
                                      new LinkedList(), body);
      //  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
      TypeNode tOperatorPointwise = nf.TypeNodeFromQualifiedName(pos, "x10.array.Operator.Pointwise");
      List classDecl = new TypedList(new LinkedList(), MethodDecl.class, false);
      classDecl.add( decl );

      New initializer = nf.New(pos,
                               tOperatorPointwise,
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
                                      nf.Id(pos, "apply"), l1,
                                      new LinkedList(), body);
      //  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
      // [IP] FIXME: this will break if the result is not a canonical type
      Expr tArray = nf.ExprFromQualifiedName(pos, resultType.toString() + "Array");
      TypeNode tArrayPointwiseOp = nf.TypeNodeFromQualifiedName(pos, resultType.toString() + "Array.pointwiseOp");

      List classDecl = new TypedList(new LinkedList(), MethodDecl.class, false);
      classDecl.add( decl );
      New initializer = nf.New(pos, tArray,
                             tArrayPointwiseOp,
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
            // Rule 16:  identifier ::= IDENTIFIER$ident
            //
            case 16: {
                //#line 93 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 95 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 19:  IntegralType ::= byte
            //
            case 19: {
                
                //#line 120 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Byte()));
                break;
            }
     
            //
            // Rule 20:  IntegralType ::= char
            //
            case 20: {
                
                //#line 125 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Char()));
                break;
            }
     
            //
            // Rule 21:  IntegralType ::= short
            //
            case 21: {
                
                //#line 130 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Short()));
                break;
            }
     
            //
            // Rule 22:  IntegralType ::= int
            //
            case 22: {
                
                //#line 135 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Int()));
                break;
            }
     
            //
            // Rule 23:  IntegralType ::= long
            //
            case 23: {
                
                //#line 140 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Long()));
                break;
            }
     
            //
            // Rule 24:  FloatingPointType ::= float
            //
            case 24: {
                
                //#line 146 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Float()));
                break;
            }
     
            //
            // Rule 25:  FloatingPointType ::= double
            //
            case 25: {
                
                //#line 151 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Double()));
                break;
            }
     
            //
            // Rule 28:  TypeName ::= identifier
            //
            case 28: {
                //#line 174 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 176 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 29:  TypeName ::= TypeName . identifier
            //
            case 29: {
                //#line 179 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name TypeName = (Name) getRhsSym(1);
                //#line 179 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 181 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 31:  ArrayType ::= Type [ Annotationsopt ]
            //
            case 31: {
                //#line 193 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 193 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Object Annotationsopt = (Object) getRhsSym(3);
                //#line 195 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.array(Type, pos(), 1));
                break;
            }
     
            //
            // Rule 32:  PackageName ::= identifier
            //
            case 32: {
                //#line 239 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 241 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 33:  PackageName ::= PackageName . identifier
            //
            case 33: {
                //#line 244 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name PackageName = (Name) getRhsSym(1);
                //#line 244 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 246 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 34:  ExpressionName ::= identifier
            //
            case 34: {
                //#line 260 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 262 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 35:  ExpressionName ::= AmbiguousName . identifier
            //
            case 35: {
                //#line 265 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 265 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 267 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 36:  MethodName ::= identifier
            //
            case 36: {
                //#line 275 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 277 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 37:  MethodName ::= AmbiguousName . identifier
            //
            case 37: {
                //#line 280 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 280 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 282 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 38:  PackageOrTypeName ::= identifier
            //
            case 38: {
                //#line 290 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 292 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 39:  PackageOrTypeName ::= PackageOrTypeName . identifier
            //
            case 39: {
                //#line 295 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name PackageOrTypeName = (Name) getRhsSym(1);
                //#line 295 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 297 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 40:  AmbiguousName ::= identifier
            //
            case 40: {
                //#line 305 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 307 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 41:  AmbiguousName ::= AmbiguousName . identifier
            //
            case 41: {
                //#line 310 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 310 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 312 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
               break;
            }
     
            //
            // Rule 42:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 42: {
                //#line 322 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 322 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 322 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 324 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 43:  ImportDeclarations ::= ImportDeclaration
            //
            case 43: {
                //#line 338 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 340 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 44:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 44: {
                //#line 345 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 345 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 347 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 45:  TypeDeclarations ::= TypeDeclaration
            //
            case 45: {
                //#line 353 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(1);
                //#line 355 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 46:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 46: {
                //#line 361 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 361 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(2);
                //#line 363 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 49:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 49: {
                //#line 376 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name TypeName = (Name) getRhsSym(2);
                //#line 378 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, TypeName.toString()));
                break;
            }
     
            //
            // Rule 50:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 50: {
                //#line 382 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name PackageOrTypeName = (Name) getRhsSym(2);
                //#line 384 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, PackageOrTypeName.toString()));
                break;
            }
     
            //
            // Rule 53:  TypeDeclaration ::= ;
            //
            case 53: {
                
                //#line 398 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(null);
                break;
            }
     
            //
            // Rule 55:  ClassModifiers ::= ClassModifier
            //
            case 55: {
                //#line 409 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassModifier = (List) getRhsSym(1);
                //#line 411 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 56:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 56: {
                //#line 416 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 416 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassModifier = (List) getRhsSym(2);
                //#line 418 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClassModifiers.addAll(ClassModifier);
                break;
            }
     
            //
            // Rule 57:  ClassModifier ::= Annotation
            //
            case 57: {
                //#line 422 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 424 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 58:  ClassModifier ::= public
            //
            case 58: {
                
                //#line 429 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PUBLIC));
                break;
            }
     
            //
            // Rule 59:  ClassModifier ::= protected
            //
            case 59: {
                
                //#line 434 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PROTECTED));
                break;
            }
     
            //
            // Rule 60:  ClassModifier ::= private
            //
            case 60: {
                
                //#line 439 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PRIVATE));
                break;
            }
     
            //
            // Rule 61:  ClassModifier ::= abstract
            //
            case 61: {
                
                //#line 444 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.ABSTRACT));
                break;
            }
     
            //
            // Rule 62:  ClassModifier ::= static
            //
            case 62: {
                
                //#line 449 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.STATIC));
                break;
            }
     
            //
            // Rule 63:  ClassModifier ::= final
            //
            case 63: {
                
                //#line 454 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.FINAL));
                break;
            }
     
            //
            // Rule 64:  ClassModifier ::= strictfp
            //
            case 64: {
                
                //#line 459 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.STRICTFP));
                break;
            }
     
            //
            // Rule 65:  Super ::= extends ClassType
            //
            case 65: {
                //#line 471 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 473 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ClassType);
                break;
            }
     
            //
            // Rule 66:  Interfaces ::= implements InterfaceTypeList
            //
            case 66: {
                //#line 482 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 484 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 67:  InterfaceTypeList ::= InterfaceType
            //
            case 67: {
                //#line 488 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(1);
                //#line 490 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 68:  InterfaceTypeList ::= InterfaceTypeList , InterfaceType
            //
            case 68: {
                //#line 495 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 495 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                //#line 497 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceTypeList.add(InterfaceType);
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 69:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 69: {
                //#line 507 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 509 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                break;
            }
     
            //
            // Rule 71:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 71: {
                //#line 514 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 514 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 516 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                break;
            }
     
            //
            // Rule 73:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 73: {
                //#line 522 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block InstanceInitializer = (Block) getRhsSym(1);
                //#line 524 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.NONE, InstanceInitializer));
                setResult(l);
                break;
            }
     
            //
            // Rule 74:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 74: {
                //#line 529 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block StaticInitializer = (Block) getRhsSym(1);
                //#line 531 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.STATIC, StaticInitializer));
                setResult(l);
                break;
            }
     
            //
            // Rule 75:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 75: {
                //#line 536 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 538 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 77:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 77: {
                //#line 545 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                MethodDecl MethodDeclaration = (MethodDecl) getRhsSym(1);
                //#line 547 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 78:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 78: {
                //#line 552 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 554 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 79:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 79: {
                //#line 559 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 561 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 80:  ClassMemberDeclaration ::= ;
            //
            case 80: {
                
                //#line 568 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                break;
            }
     
            //
            // Rule 81:  VariableDeclarators ::= VariableDeclarator
            //
            case 81: {
                //#line 576 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(1);
                //#line 578 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), X10VarDeclarator.class, false);
                l.add(VariableDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 82:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 82: {
                //#line 583 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 583 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(3);
                //#line 585 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                break;
            }
     
            //
            // Rule 84:  VariableDeclarator ::= VariableDeclaratorId = VariableInitializer
            //
            case 84: {
                //#line 591 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                //#line 591 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 593 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableDeclaratorId.init = VariableInitializer;
                VariableDeclaratorId.position(pos(((JPGPosition) VariableDeclaratorId.pos), ((JPGPosition) VariableInitializer.position())));
                // setResult(VariableDeclaratorId);
                break;
            }
     
            //
            // Rule 85:  TraditionalVariableDeclaratorId ::= identifier
            //
            case 85: {
                //#line 599 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 601 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10VarDeclarator(pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 86:  TraditionalVariableDeclaratorId ::= TraditionalVariableDeclaratorId [ ]
            //
            case 86: {
                //#line 604 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator TraditionalVariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                //#line 606 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TraditionalVariableDeclaratorId.dims++;
                TraditionalVariableDeclaratorId.position(pos());
                // setResult(a);
                break;
            }
     
            //
            // Rule 88:  VariableDeclaratorId ::= identifier [ IdentifierList ]
            //
            case 88: {
                //#line 613 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 613 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List IdentifierList = (List) getRhsSym(3);
                //#line 615 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10VarDeclarator(pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier()), IdentifierList));
                break;
            }
     
            //
            // Rule 89:  VariableDeclaratorId ::= [ IdentifierList ]
            //
            case 89: {
                //#line 618 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List IdentifierList = (List) getRhsSym(2);
                //#line 620 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new X10VarDeclarator(pos(), IdentifierList));
                break;
            }
     
            //
            // Rule 92:  FieldModifiers ::= FieldModifier
            //
            case 92: {
                //#line 627 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List FieldModifier = (List) getRhsSym(1);
                //#line 629 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 93:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 93: {
                //#line 634 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 634 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List FieldModifier = (List) getRhsSym(2);
                //#line 636 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FieldModifiers.addAll(FieldModifier);
                break;
            }
     
            //
            // Rule 94:  FieldModifier ::= Annotation
            //
            case 94: {
                //#line 640 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 642 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 95:  FieldModifier ::= public
            //
            case 95: {
                
                //#line 647 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PUBLIC));
                break;
            }
     
            //
            // Rule 96:  FieldModifier ::= protected
            //
            case 96: {
                
                //#line 652 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PROTECTED));
                break;
            }
     
            //
            // Rule 97:  FieldModifier ::= private
            //
            case 97: {
                
                //#line 657 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PRIVATE));
                break;
            }
     
            //
            // Rule 98:  FieldModifier ::= static
            //
            case 98: {
                
                //#line 662 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.STATIC));
                break;
            }
     
            //
            // Rule 99:  FieldModifier ::= final
            //
            case 99: {
                
                //#line 667 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.FINAL));
                break;
            }
     
            //
            // Rule 100:  FieldModifier ::= transient
            //
            case 100: {
                
                //#line 672 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.TRANSIENT));
                break;
            }
     
            //
            // Rule 102:  ResultType ::= void
            //
            case 102: {
                
                //#line 689 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 103:  FormalParameterList ::= LastFormalParameter
            //
            case 103: {
                //#line 709 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Formal LastFormalParameter = (Formal) getRhsSym(1);
                //#line 711 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(LastFormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 104:  FormalParameterList ::= FormalParameters , LastFormalParameter
            //
            case 104: {
                //#line 716 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List FormalParameters = (List) getRhsSym(1);
                //#line 716 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Formal LastFormalParameter = (Formal) getRhsSym(3);
                //#line 718 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FormalParameters.add(LastFormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 105:  FormalParameters ::= FormalParameter
            //
            case 105: {
                //#line 723 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 725 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 106:  FormalParameters ::= FormalParameters , FormalParameter
            //
            case 106: {
                //#line 730 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List FormalParameters = (List) getRhsSym(1);
                //#line 730 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 732 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                FormalParameters.add(FormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 107:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 107: {
                //#line 737 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 737 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 737 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(3);
                //#line 739 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            Formal f;
        if (VariableDeclaratorId != null)
                f = nf.X10Formal(pos(), extractFlags(VariableModifiersopt), nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names());
            else
                f = nf.Formal(pos(), extractFlags(VariableModifiersopt), nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), 1), nf.Id(pos(), ""));
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
            setResult(f);
                break;
            }
     
            //
            // Rule 108:  VariableModifiers ::= VariableModifier
            //
            case 108: {
                //#line 749 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableModifier = (List) getRhsSym(1);
                //#line 751 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 109:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 109: {
                //#line 756 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 756 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableModifier = (List) getRhsSym(2);
                //#line 758 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableModifiers.addAll(VariableModifier);
                break;
            }
     
            //
            // Rule 110:  VariableModifier ::= final
            //
            case 110: {
                
                //#line 764 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.FINAL));
                break;
            }
     
            //
            // Rule 111:  VariableModifier ::= Annotation
            //
            case 111: {
                //#line 767 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 769 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 112:  LastFormalParameter ::= VariableModifiersopt Type ...opt$opt VariableDeclaratorId
            //
            case 112: {
                //#line 773 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 773 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 773 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Object opt = (Object) getRhsSym(3);
                //#line 773 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(4);
                //#line 775 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                assert(opt == null);
                Formal f = nf.X10Formal(pos(), extractFlags(VariableModifiersopt), nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names());
                f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
                setResult(f);
                break;
            }
     
            //
            // Rule 113:  MethodModifiers ::= MethodModifier
            //
            case 113: {
                //#line 788 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List MethodModifier = (List) getRhsSym(1);
                //#line 790 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 114:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 114: {
                //#line 795 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 795 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List MethodModifier = (List) getRhsSym(2);
                //#line 797 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                MethodModifiers.addAll(MethodModifier);
                break;
            }
     
            //
            // Rule 115:  MethodModifier ::= Annotation
            //
            case 115: {
                //#line 801 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 803 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 116:  MethodModifier ::= public
            //
            case 116: {
                
                //#line 808 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PUBLIC));
                break;
            }
     
            //
            // Rule 117:  MethodModifier ::= protected
            //
            case 117: {
                
                //#line 813 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PROTECTED));
                break;
            }
     
            //
            // Rule 118:  MethodModifier ::= private
            //
            case 118: {
                
                //#line 818 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PRIVATE));
                break;
            }
     
            //
            // Rule 119:  MethodModifier ::= abstract
            //
            case 119: {
                
                //#line 823 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.ABSTRACT));
                break;
            }
     
            //
            // Rule 120:  MethodModifier ::= static
            //
            case 120: {
                
                //#line 828 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.STATIC));
                break;
            }
     
            //
            // Rule 121:  MethodModifier ::= final
            //
            case 121: {
                
                //#line 833 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.FINAL));
                break;
            }
     
            //
            // Rule 122:  MethodModifier ::= native
            //
            case 122: {
                
                //#line 843 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.NATIVE));
                break;
            }
     
            //
            // Rule 123:  MethodModifier ::= strictfp
            //
            case 123: {
                
                //#line 848 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.STRICTFP));
                break;
            }
     
            //
            // Rule 124:  Throws ::= throws ExceptionTypeList
            //
            case 124: {
                //#line 852 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 854 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 125:  ExceptionTypeList ::= ExceptionType
            //
            case 125: {
                //#line 858 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 860 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                break;
            }
     
            //
            // Rule 126:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 126: {
                //#line 865 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 865 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 867 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExceptionTypeList.add(ExceptionType);
                // setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 129:  MethodBody ::= ;
            //
            case 129:
                setResult(null);
                break;
 
            //
            // Rule 131:  StaticInitializer ::= static Block
            //
            case 131: {
                //#line 887 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 889 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 132:  SimpleTypeName ::= identifier
            //
            case 132: {
                //#line 904 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 906 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 133:  ConstructorModifiers ::= ConstructorModifier
            //
            case 133: {
                //#line 910 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 912 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 134:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 134: {
                //#line 917 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 917 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 919 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                break;
            }
     
            //
            // Rule 135:  ConstructorModifier ::= Annotation
            //
            case 135: {
                //#line 923 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 925 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 136:  ConstructorModifier ::= public
            //
            case 136: {
                
                //#line 930 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PUBLIC));
                break;
            }
     
            //
            // Rule 137:  ConstructorModifier ::= protected
            //
            case 137: {
                
                //#line 935 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PROTECTED));
                break;
            }
     
            //
            // Rule 138:  ConstructorModifier ::= private
            //
            case 138: {
                
                //#line 940 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PRIVATE));
                break;
            }
     
            //
            // Rule 139:  ConstructorBody ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 139: {
                //#line 944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 944 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 946 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
            // Rule 140:  Arguments ::= ( ArgumentListopt )
            //
            case 140: {
                //#line 979 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 981 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ArgumentListopt);
                break;
            }
     
            //
            // Rule 142:  InterfaceModifiers ::= InterfaceModifier
            //
            case 142: {
                //#line 996 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 998 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 143:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 143: {
                //#line 1003 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 1003 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 1005 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                break;
            }
     
            //
            // Rule 144:  InterfaceModifier ::= Annotation
            //
            case 144: {
                //#line 1009 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 1011 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 145:  InterfaceModifier ::= public
            //
            case 145: {
                
                //#line 1016 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PUBLIC));
                break;
            }
     
            //
            // Rule 146:  InterfaceModifier ::= protected
            //
            case 146: {
                
                //#line 1021 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PROTECTED));
                break;
            }
     
            //
            // Rule 147:  InterfaceModifier ::= private
            //
            case 147: {
                
                //#line 1026 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PRIVATE));
                break;
            }
     
            //
            // Rule 148:  InterfaceModifier ::= abstract
            //
            case 148: {
                
                //#line 1031 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.ABSTRACT));
                break;
            }
     
            //
            // Rule 149:  InterfaceModifier ::= static
            //
            case 149: {
                
                //#line 1036 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.STATIC));
                break;
            }
     
            //
            // Rule 150:  InterfaceModifier ::= strictfp
            //
            case 150: {
                
                //#line 1041 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.STRICTFP));
                break;
            }
     
            //
            // Rule 151:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 151: {
                //#line 1045 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(2);
                //#line 1047 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 152:  ExtendsInterfaces ::= ExtendsInterfaces , InterfaceType
            //
            case 152: {
                //#line 1052 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 1052 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                //#line 1054 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExtendsInterfaces.add(InterfaceType);
                // setResult(ExtendsInterfaces);
                break;
            }
     
            //
            // Rule 153:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 153: {
                //#line 1064 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 1066 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                break;
            }
     
            //
            // Rule 155:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 155: {
                //#line 1071 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 1071 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 1073 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                break;
            }
     
            //
            // Rule 157:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 157: {
                //#line 1079 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                MethodDecl AbstractMethodDeclaration = (MethodDecl) getRhsSym(1);
                //#line 1081 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(AbstractMethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 158:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 158: {
                //#line 1086 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 1088 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 159:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 159: {
                //#line 1093 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 1095 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 160:  InterfaceMemberDeclaration ::= ;
            //
            case 160: {
                
                //#line 1102 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 161:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 161: {
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ConstantModifiersopt = (List) getRhsSym(1);
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1106 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 1108 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                for (Iterator i = VariableDeclarators.iterator(); i.hasNext();)
                {
                    X10VarDeclarator d = (X10VarDeclarator) i.next();
                    if (d.hasExplodedVars())
                      // TODO: Report this exception correctly.
                      throw new Error("Field Declarations may not have exploded variables." + pos());
                    FieldDecl fd = nf.FieldDecl(pos(getRhsFirstTokenIndex(2), getRightSpan()),
                                       extractFlags(ConstantModifiersopt),
                                       nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), d.dims),
                                       d.name,
                                       d.init);
                    fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(ConstantModifiersopt));
                    l.add(fd);
                }
                setResult(l);
                break;
            }
     
            //
            // Rule 162:  ConstantModifiers ::= ConstantModifier
            //
            case 162: {
                //#line 1127 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ConstantModifier = (List) getRhsSym(1);
                //#line 1129 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(ConstantModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 163:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 163: {
                //#line 1134 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ConstantModifiers = (List) getRhsSym(1);
                //#line 1134 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ConstantModifier = (List) getRhsSym(2);
                //#line 1136 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ConstantModifiers.addAll(ConstantModifier);
                break;
            }
     
            //
            // Rule 164:  ConstantModifier ::= Annotation
            //
            case 164: {
                //#line 1140 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 1142 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 165:  ConstantModifier ::= public
            //
            case 165: {
                
                //#line 1147 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PUBLIC));
                break;
            }
     
            //
            // Rule 166:  ConstantModifier ::= static
            //
            case 166: {
                
                //#line 1152 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.STATIC));
                break;
            }
     
            //
            // Rule 167:  ConstantModifier ::= final
            //
            case 167: {
                
                //#line 1157 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.FINAL));
                break;
            }
     
            //
            // Rule 168:  AbstractMethodModifiers ::= AbstractMethodModifier
            //
            case 168: {
                //#line 1163 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List AbstractMethodModifier = (List) getRhsSym(1);
                //#line 1165 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new LinkedList();
                l.addAll(AbstractMethodModifier);
                setResult(l);
                break;
            }
     
            //
            // Rule 169:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 169: {
                //#line 1170 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List AbstractMethodModifiers = (List) getRhsSym(1);
                //#line 1170 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List AbstractMethodModifier = (List) getRhsSym(2);
                //#line 1172 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                AbstractMethodModifiers.addAll(AbstractMethodModifier);
                break;
            }
     
            //
            // Rule 170:  AbstractMethodModifier ::= Annotation
            //
            case 170: {
                //#line 1176 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 1178 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Annotation));
                break;
            }
     
            //
            // Rule 171:  AbstractMethodModifier ::= public
            //
            case 171: {
                
                //#line 1183 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PUBLIC));
                break;
            }
     
            //
            // Rule 172:  AbstractMethodModifier ::= abstract
            //
            case 172: {
                
                //#line 1188 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.ABSTRACT));
                break;
            }
     
            //
            // Rule 173:  Annotations ::= Annotation
            //
            case 173: {
                //#line 1221 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 1223 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                break;
            }
     
            //
            // Rule 174:  Annotations ::= Annotations Annotation
            //
            case 174: {
                //#line 1228 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List Annotations = (List) getRhsSym(1);
                //#line 1228 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 1230 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Annotations.add(Annotation);
                break;
            }
     
            //
            // Rule 175:  Annotation ::= @ InterfaceType
            //
            case 175: {
                //#line 1234 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(2);
                //#line 1236 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.AnnotationNode(pos(), InterfaceType));
                break;
            }
     
            //
            // Rule 176:  SimpleName ::= identifier
            //
            case 176: {
                //#line 1251 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1253 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 177:  ArrayInitializer ::= { VariableInitializersopt ,opt$opt }
            //
            case 177: {
                //#line 1280 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableInitializersopt = (List) getRhsSym(2);
                //#line 1280 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Object opt = (Object) getRhsSym(3);
                //#line 1282 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (VariableInitializersopt == null)
                     setResult(nf.ArrayInit(pos()));
                else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
                break;
            }
     
            //
            // Rule 178:  VariableInitializers ::= VariableInitializer
            //
            case 178: {
                //#line 1288 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 1290 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 179:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 179: {
                //#line 1295 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 1295 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 1297 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                break;
            }
     
            //
            // Rule 180:  Block ::= { BlockStatementsopt }
            //
            case 180: {
                //#line 1316 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 1318 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                break;
            }
     
            //
            // Rule 181:  BlockStatements ::= BlockStatement
            //
            case 181: {
                //#line 1322 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatement = (List) getRhsSym(1);
                //#line 1324 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                break;
            }
     
            //
            // Rule 182:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 182: {
                //#line 1329 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatements = (List) getRhsSym(1);
                //#line 1329 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatement = (List) getRhsSym(2);
                //#line 1331 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                break;
            }
     
            //
            // Rule 184:  BlockStatement ::= ClassDeclaration
            //
            case 184: {
                //#line 1337 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 1339 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                break;
            }
     
            //
            // Rule 185:  BlockStatement ::= Statement
            //
            case 185: {
                //#line 1344 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 1346 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                break;
            }
     
            //
            // Rule 187:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 187: {
                //#line 1354 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 1354 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1354 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 1356 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                break;
            }
     
            //
            // Rule 211:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 211: {
                //#line 1420 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1420 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1422 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.If(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 212:  IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
            //
            case 212: {
                //#line 1426 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1426 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1426 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1428 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.If(pos(), Expression, StatementNoShortIf, Statement));
                break;
            }
     
            //
            // Rule 213:  IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf$true_stmt else StatementNoShortIf$false_stmt
            //
            case 213: {
                //#line 1432 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1432 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt true_stmt = (Stmt) getRhsSym(5);
                //#line 1432 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt false_stmt = (Stmt) getRhsSym(7);
                //#line 1434 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.If(pos(), Expression, true_stmt, false_stmt));
                break;
            }
     
            //
            // Rule 214:  EmptyStatement ::= ;
            //
            case 214: {
                
                //#line 1440 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 215:  LabeledStatement ::= identifier : Statement
            //
            case 215: {
                //#line 1444 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1444 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1446 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Labeled(pos(), nf.Id(pos(getLeftSpan()), identifier.getIdentifier()), Statement));
                break;
            }
     
            //
            // Rule 216:  LabeledStatementNoShortIf ::= identifier : StatementNoShortIf
            //
            case 216: {
                //#line 1450 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1450 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(3);
                //#line 1452 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Labeled(pos(), nf.Id(pos(getLeftSpan()), identifier.getIdentifier()), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 217:  ExpressionStatement ::= StatementExpression ;
            //
            case 217: {
                //#line 1455 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1457 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 225:  AssertStatement ::= assert Expression ;
            //
            case 225: {
                //#line 1478 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1480 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assert(pos(), Expression));
                break;
            }
     
            //
            // Rule 226:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 226: {
                //#line 1483 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1483 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1485 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                break;
            }
     
            //
            // Rule 227:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 227: {
                //#line 1489 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1489 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1491 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                break;
            }
     
            //
            // Rule 228:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 228: {
                //#line 1495 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1495 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1497 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                break;
            }
     
            //
            // Rule 230:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 230: {
                //#line 1503 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1503 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1505 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                break;
            }
     
            //
            // Rule 231:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 231: {
                //#line 1510 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1510 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1512 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                break;
            }
     
            //
            // Rule 232:  SwitchLabels ::= SwitchLabel
            //
            case 232: {
                //#line 1519 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1521 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                break;
            }
     
            //
            // Rule 233:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 233: {
                //#line 1526 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1526 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1528 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                break;
            }
     
            //
            // Rule 234:  SwitchLabel ::= case ConstantExpression :
            //
            case 234: {
                //#line 1533 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1535 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                break;
            }
     
            //
            // Rule 235:  SwitchLabel ::= default :
            //
            case 235: {
                
                //#line 1542 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 236:  WhileStatement ::= while ( Expression ) Statement
            //
            case 236: {
                //#line 1549 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1549 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1551 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.While(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 237:  WhileStatementNoShortIf ::= while ( Expression ) StatementNoShortIf
            //
            case 237: {
                //#line 1555 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1555 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1557 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.While(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 238:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 238: {
                //#line 1561 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1561 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1563 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                break;
            }
     
            //
            // Rule 241:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 241: {
                //#line 1570 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1570 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1570 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1570 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1572 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                break;
            }
     
            //
            // Rule 242:  ForStatementNoShortIf ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) StatementNoShortIf
            //
            case 242: {
                //#line 1576 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1576 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1576 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1576 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(9);
                //#line 1578 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 244:  ForInit ::= LocalVariableDeclaration
            //
            case 244: {
                //#line 1583 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1585 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 246:  StatementExpressionList ::= StatementExpression
            //
            case 246: {
                //#line 1593 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1595 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                break;
            }
     
            //
            // Rule 247:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 247: {
                //#line 1600 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1600 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1602 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                //setResult(StatementExpressionList);
                break;
            }
     
            //
            // Rule 248:  BreakStatement ::= break identifieropt ;
            //
            case 248: {
                //#line 1610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name identifieropt = (Name) getRhsSym(2);
                //#line 1612 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (identifieropt == null)
                     setResult(nf.Break(pos()));
                else setResult(nf.Break(pos(), nf.Id(identifieropt.pos, identifieropt.toString())));
                break;
            }
     
            //
            // Rule 249:  ContinueStatement ::= continue identifieropt ;
            //
            case 249: {
                //#line 1618 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name identifieropt = (Name) getRhsSym(2);
                //#line 1620 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (identifieropt == null)
                     setResult(nf.Continue(pos()));
                else setResult(nf.Continue(pos(), nf.Id(identifieropt.pos, identifieropt.toString())));
                break;
            }
     
            //
            // Rule 250:  ReturnStatement ::= return Expressionopt ;
            //
            case 250: {
                //#line 1626 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1628 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Return(pos(), Expressionopt));
                break;
            }
     
            //
            // Rule 251:  ThrowStatement ::= throw Expression ;
            //
            case 251: {
                //#line 1632 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1634 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Throw(pos(), Expression));
                break;
            }
     
            //
            // Rule 252:  TryStatement ::= try Block Catches
            //
            case 252: {
                //#line 1644 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 1644 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List Catches = (List) getRhsSym(3);
                //#line 1646 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Try(pos(), Block, Catches));
                break;
            }
     
            //
            // Rule 253:  TryStatement ::= try Block Catchesopt Finally
            //
            case 253: {
                //#line 1649 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 1649 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1649 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Finally = (Block) getRhsSym(4);
                //#line 1651 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                break;
            }
     
            //
            // Rule 254:  Catches ::= CatchClause
            //
            case 254: {
                //#line 1655 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1657 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                break;
            }
     
            //
            // Rule 255:  Catches ::= Catches CatchClause
            //
            case 255: {
                //#line 1662 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List Catches = (List) getRhsSym(1);
                //#line 1662 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1664 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                break;
            }
     
            //
            // Rule 256:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 256: {
                //#line 1669 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1669 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(5);
                //#line 1671 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                break;
            }
     
            //
            // Rule 257:  Finally ::= finally Block
            //
            case 257: {
                //#line 1675 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 1677 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 261:  PrimaryNoNewArray ::= Type . class
            //
            case 261: {
                //#line 1695 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1697 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                break;
            }
     
            //
            // Rule 262:  PrimaryNoNewArray ::= void . class
            //
            case 262: {
                
                //#line 1716 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(getLeftSpan()), ts.Void())));
                break;
            }
     
            //
            // Rule 263:  PrimaryNoNewArray ::= self
            //
            case 263: {
                
                //#line 1722 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Self(pos()));
                break;
            }
     
            //
            // Rule 264:  PrimaryNoNewArray ::= this
            //
            case 264: {
                
                //#line 1727 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 265:  PrimaryNoNewArray ::= ClassName . this
            //
            case 265: {
                //#line 1730 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1732 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 266:  PrimaryNoNewArray ::= ( Expression )
            //
            case 266: {
                //#line 1735 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1737 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ParExpr(pos(), Expression));
                break;
            }
     
            //
            // Rule 271:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 271: {
                //#line 1745 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                //#line 1747 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 272:  Literal ::= LongLiteral$LongLiteral
            //
            case 272: {
                //#line 1751 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken LongLiteral = (IToken) getRhsIToken(1);
                //#line 1753 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 273:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 273: {
                //#line 1757 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                //#line 1759 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 274:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 274: {
                //#line 1763 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                //#line 1765 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 275:  Literal ::= BooleanLiteral
            //
            case 275: {
                //#line 1769 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 1771 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 276:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 276: {
                //#line 1774 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                //#line 1776 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 277:  Literal ::= StringLiteral$str
            //
            case 277: {
                //#line 1780 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken str = (IToken) getRhsIToken(1);
                //#line 1782 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 278:  Literal ::= null
            //
            case 278: {
                
                //#line 1788 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 279:  BooleanLiteral ::= true$trueLiteral
            //
            case 279: {
                //#line 1792 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 1794 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 280:  BooleanLiteral ::= false$falseLiteral
            //
            case 280: {
                //#line 1797 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 1799 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 281:  ArgumentList ::= Expression
            //
            case 281: {
                //#line 1812 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1814 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                break;
            }
     
            //
            // Rule 282:  ArgumentList ::= ArgumentList , Expression
            //
            case 282: {
                //#line 1819 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ArgumentList = (List) getRhsSym(1);
                //#line 1819 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1821 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ArgumentList.add(Expression);
                //setResult(ArgumentList);
                break;
            }
     
            //
            // Rule 283:  DimExprs ::= DimExpr
            //
            case 283: {
                //#line 1855 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr DimExpr = (Expr) getRhsSym(1);
                //#line 1857 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                setResult(l);
                break;
            }
     
            //
            // Rule 284:  DimExprs ::= DimExprs DimExpr
            //
            case 284: {
                //#line 1862 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List DimExprs = (List) getRhsSym(1);
                //#line 1862 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr DimExpr = (Expr) getRhsSym(2);
                //#line 1864 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                DimExprs.add(DimExpr);
                //setResult(DimExprs);
                break;
            }
     
            //
            // Rule 285:  DimExpr ::= [ Expression ]
            //
            case 285: {
                //#line 1869 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1871 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Expression.position(pos()));
                break;
            }
     
            //
            // Rule 286:  Dims ::= [ ]
            //
            case 286: {
                
                //#line 1877 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Integer(1));
                break;
            }
     
            //
            // Rule 287:  Dims ::= Dims [ ]
            //
            case 287: {
                //#line 1880 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Integer Dims = (Integer) getRhsSym(1);
                //#line 1882 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Integer(Dims.intValue() + 1));
                break;
            }
     
            //
            // Rule 288:  FieldAccess ::= Primary . identifier
            //
            case 288: {
                //#line 1886 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1886 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1888 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 289:  FieldAccess ::= super . identifier
            //
            case 289: {
                //#line 1891 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1893 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 290:  FieldAccess ::= ClassName . super$sup . identifier
            //
            case 290: {
                //#line 1896 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1896 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 1896 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                //#line 1898 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 291:  MethodInvocation ::= MethodName ( ArgumentListopt )
            //
            case 291: {
                //#line 1902 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name MethodName = (Name) getRhsSym(1);
                //#line 1902 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 1904 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, ArgumentListopt));
                break;
            }
     
            //
            // Rule 293:  PostfixExpression ::= ExpressionName
            //
            case 293: {
                //#line 1927 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 1929 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 296:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 296: {
                //#line 1935 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 1937 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 297:  PostDecrementExpression ::= PostfixExpression --
            //
            case 297: {
                //#line 1941 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 1943 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 300:  UnaryExpression ::= + UnaryExpression
            //
            case 300: {
                //#line 1949 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1951 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpression));
                break;
            }
     
            //
            // Rule 301:  UnaryExpression ::= - UnaryExpression
            //
            case 301: {
                //#line 1954 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1956 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpression));
                break;
            }
     
            //
            // Rule 303:  PreIncrementExpression ::= ++ UnaryExpression
            //
            case 303: {
                //#line 1961 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1963 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpression));
                break;
            }
     
            //
            // Rule 304:  PreDecrementExpression ::= -- UnaryExpression
            //
            case 304: {
                //#line 1967 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1969 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpression));
                break;
            }
     
            //
            // Rule 306:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 306: {
                //#line 1974 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1976 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 307:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 307: {
                //#line 1979 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1981 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 310:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 310: {
                //#line 1993 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1993 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 1995 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                break;
            }
     
            //
            // Rule 311:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 311: {
                //#line 1998 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1998 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 2000 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                break;
            }
     
            //
            // Rule 312:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 312: {
                //#line 2003 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 2003 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 2005 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                break;
            }
     
            //
            // Rule 314:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 314: {
                //#line 2010 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 2010 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 2012 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 315:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 315: {
                //#line 2015 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 2015 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 2017 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 317:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 317: {
                //#line 2022 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 2022 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 2024 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                break;
            }
     
            //
            // Rule 318:  ShiftExpression ::= ShiftExpression > > AdditiveExpression
            //
            case 318: {
                //#line 2027 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 2027 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(4);
                //#line 2029 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 319:  ShiftExpression ::= ShiftExpression > > > AdditiveExpression
            //
            case 319: {
                //#line 2033 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 2033 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(5);
                //#line 2035 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 321:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 321: {
                //#line 2041 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 2041 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 2043 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
                break;
            }
     
            //
            // Rule 322:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 322: {
                //#line 2046 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 2046 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 2048 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
                break;
            }
     
            //
            // Rule 323:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 323: {
                //#line 2051 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 2051 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 2053 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
                break;
            }
     
            //
            // Rule 324:  RelationalExpression ::= RelationalExpression > = ShiftExpression
            //
            case 324: {
                //#line 2056 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 2056 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(4);
                //#line 2058 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
                break;
            }
     
            //
            // Rule 326:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 326: {
                //#line 2072 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 2072 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 2074 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                break;
            }
     
            //
            // Rule 327:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 327: {
                //#line 2077 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 2077 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 2079 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                break;
            }
     
            //
            // Rule 329:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 329: {
                //#line 2084 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 2084 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 2086 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                break;
            }
     
            //
            // Rule 331:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 331: {
                //#line 2091 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 2091 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 2093 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                break;
            }
     
            //
            // Rule 333:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 333: {
                //#line 2098 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 2098 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 2100 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 335:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 335: {
                //#line 2105 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 2105 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 2107 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                break;
            }
     
            //
            // Rule 337:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 337: {
                //#line 2112 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 2112 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 2114 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                break;
            }
     
            //
            // Rule 339:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 339: {
                //#line 2119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 2119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2119 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 2121 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 342:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 342: {
                //#line 2128 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 2128 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 2128 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 2130 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 343:  LeftHandSide ::= ExpressionName
            //
            case 343: {
                //#line 2134 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 2136 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 346:  AssignmentOperator ::= =
            //
            case 346: {
                
                //#line 2144 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 347:  AssignmentOperator ::= *=
            //
            case 347: {
                
                //#line 2149 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 348:  AssignmentOperator ::= /=
            //
            case 348: {
                
                //#line 2154 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 349:  AssignmentOperator ::= %=
            //
            case 349: {
                
                //#line 2159 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 350:  AssignmentOperator ::= +=
            //
            case 350: {
                
                //#line 2164 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 351:  AssignmentOperator ::= -=
            //
            case 351: {
                
                //#line 2169 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 352:  AssignmentOperator ::= <<=
            //
            case 352: {
                
                //#line 2174 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 353:  AssignmentOperator ::= > > =
            //
            case 353: {
                
                //#line 2179 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 354:  AssignmentOperator ::= > > > =
            //
            case 354: {
                
                //#line 2185 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 355:  AssignmentOperator ::= &=
            //
            case 355: {
                
                //#line 2191 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 356:  AssignmentOperator ::= ^=
            //
            case 356: {
                
                //#line 2196 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 357:  AssignmentOperator ::= |=
            //
            case 357: {
                
                //#line 2201 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 360:  Dimsopt ::= $Empty
            //
            case 360: {
                
                //#line 2214 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Integer(0));
                break;
            }
     
            //
            // Rule 362:  Catchesopt ::= $Empty
            //
            case 362: {
                
                //#line 2221 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 364:  identifieropt ::= $Empty
            //
            case 364:
                setResult(null);
                break;
 
            //
            // Rule 365:  identifieropt ::= identifier
            //
            case 365: {
                //#line 2228 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 2230 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 366:  ForUpdateopt ::= $Empty
            //
            case 366: {
                
                //#line 2236 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 368:  Expressionopt ::= $Empty
            //
            case 368:
                setResult(null);
                break;
 
            //
            // Rule 370:  ForInitopt ::= $Empty
            //
            case 370: {
                
                //#line 2247 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 372:  SwitchLabelsopt ::= $Empty
            //
            case 372: {
                
                //#line 2254 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 374:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 374: {
                
                //#line 2261 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 376:  VariableModifiersopt ::= $Empty
            //
            case 376: {
                
                //#line 2268 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 378:  VariableInitializersopt ::= $Empty
            //
            case 378:
                setResult(null);
                break;
 
            //
            // Rule 380:  AbstractMethodModifiersopt ::= $Empty
            //
            case 380: {
                
                //#line 2298 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 382:  ConstantModifiersopt ::= $Empty
            //
            case 382: {
                
                //#line 2305 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 384:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 384: {
                
                //#line 2312 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 386:  ExtendsInterfacesopt ::= $Empty
            //
            case 386: {
                
                //#line 2319 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 388:  InterfaceModifiersopt ::= $Empty
            //
            case 388: {
                
                //#line 2326 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 390:  ClassBodyopt ::= $Empty
            //
            case 390:
                setResult(null);
                break;
 
            //
            // Rule 392:  Argumentsopt ::= $Empty
            //
            case 392: {
                
                //#line 2337 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 394:  ,opt ::= $Empty
            //
            case 394:
                setResult(null);
                break;
 
            //
            // Rule 396:  ArgumentListopt ::= $Empty
            //
            case 396: {
                
                //#line 2358 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
     
            //
            // Rule 398:  BlockStatementsopt ::= $Empty
            //
            case 398: {
                
                //#line 2365 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 400:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 400:
                setResult(null);
                break;
 
            //
            // Rule 402:  ConstructorModifiersopt ::= $Empty
            //
            case 402: {
                
                //#line 2376 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 404:  ...opt ::= $Empty
            //
            case 404:
                setResult(null);
                break;
 
            //
            // Rule 406:  FormalParameterListopt ::= $Empty
            //
            case 406: {
                
                //#line 2387 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 408:  Throwsopt ::= $Empty
            //
            case 408: {
                
                //#line 2394 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 410:  MethodModifiersopt ::= $Empty
            //
            case 410: {
                
                //#line 2401 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 412:  FieldModifiersopt ::= $Empty
            //
            case 412: {
                
                //#line 2408 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 414:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 414: {
                
                //#line 2415 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 416:  Interfacesopt ::= $Empty
            //
            case 416: {
                
                //#line 2422 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 418:  Superopt ::= $Empty
            //
            case 418: {
                
                //#line 2429 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
               setResult(nf.TypeNodeFromQualifiedName(pos(), "x10.lang.Object"));
                break;
            }
     
            //
            // Rule 420:  ClassModifiersopt ::= $Empty
            //
            case 420: {
                
                //#line 2440 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 422:  Annotationsopt ::= $Empty
            //
            case 422: {
                
                //#line 2447 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                break;
            }
     
            //
            // Rule 424:  TypeDeclarationsopt ::= $Empty
            //
            case 424: {
                
                //#line 2454 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 426:  ImportDeclarationsopt ::= $Empty
            //
            case 426: {
                
                //#line 2461 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 428:  PackageDeclarationopt ::= $Empty
            //
            case 428:
                setResult(null);
                break;
 
            //
            // Rule 430:  ClassType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 430: {
                //#line 786 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 786 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 786 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                //#line 788 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                     setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : nf.AmbDepTypeNode(pos(), TypeName.toType(), null, DepParametersopt));
                break;
            }
     
            //
            // Rule 431:  InterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 431: {
                //#line 795 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 795 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 795 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                //#line 797 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                 setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : nf.AmbDepTypeNode(pos(), TypeName.toType(), null, DepParametersopt));
                break;
            }
     
            //
            // Rule 432:  PackageDeclaration ::= package PackageName ;
            //
            case 432: {
                //#line 803 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name PackageName = (Name) getRhsSym(2);
                //#line 805 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(PackageName.toPackage());
                break;
            }
     
            //
            // Rule 433:  NormalClassDeclaration ::= X10ClassModifiersopt class identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 433: {
                //#line 809 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List X10ClassModifiersopt = (List) getRhsSym(1);
                //#line 809 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 809 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                //#line 809 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(5);
                //#line 809 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(6);
                //#line 809 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(7);
                //#line 811 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
      checkTypeName(identifier);
      List/*<PropertyDecl>*/ props = PropertyListopt == null ? null
                  : (List) PropertyListopt[0];
      Expr ci = PropertyListopt == null ? null : (Expr) PropertyListopt[1];
      Flags f = extractFlags(X10ClassModifiersopt);
      List annotations = extractAnnotations(X10ClassModifiersopt);
      ClassDecl cd = X10Flags.isValue(f)
         ? nf.ValueClassDecl(pos(),
              f, nf.Id(identifier.getPosition(), identifier.getIdentifier()), props, ci, Superopt, Interfacesopt, ClassBody)
         : nf.ClassDecl(pos(),
              f, nf.Id(identifier.getPosition(), identifier.getIdentifier()), props, ci, Superopt, Interfacesopt, ClassBody);
      cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(annotations);
      setResult(cd);
                break;
            }
     
            //
            // Rule 434:  X10ClassModifiers ::= X10ClassModifier
            //
            case 434: {
                //#line 827 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List X10ClassModifier = (List) getRhsSym(1);
                //#line 829 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
       List l = new LinkedList();
       l.addAll(X10ClassModifier);
       setResult(l);
                break;
            }
     
            //
            // Rule 435:  X10ClassModifiers ::= X10ClassModifiers X10ClassModifier
            //
            case 435: {
                //#line 834 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List X10ClassModifiers = (List) getRhsSym(1);
                //#line 834 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List X10ClassModifier = (List) getRhsSym(2);
                //#line 836 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
       X10ClassModifiers.addAll(X10ClassModifier);
                break;
            }
     
            //
            // Rule 436:  X10ClassModifier ::= ClassModifier
            //
            case 436: {
                //#line 840 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 842 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // Done by extractFlags
                // X10Flags.toX10Flags(ClassModifier));
                setResult(ClassModifier);
                break;
            }
     
            //
            // Rule 437:  X10ClassModifier ::= safe
            //
            case 437: {
                
                //#line 849 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(X10Flags.SAFE));
                break;
            }
     
            //
            // Rule 438:  PropertyList ::= ( Properties WhereClauseopt )
            //
            case 438: {
                //#line 853 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Properties = (List) getRhsSym(2);
                //#line 853 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(3);
                //#line 855 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
   Object[] result = new Object[2];
   result[0] = Properties;
   result[1] = WhereClauseopt;
   setResult(result);
           break;
            }  
            //
            // Rule 439:  PropertyList ::= ( WhereClause )
            //
            case 439: {
                //#line 860 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClause = (Expr) getRhsSym(2);
                //#line 862 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
   Object[] result = new Object[2];
   result[0] = null;
   result[1] = WhereClause;
   setResult(result);
           break;
            }  
            //
            // Rule 440:  Properties ::= Property
            //
            case 440: {
                //#line 869 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 871 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                break;
            }
     
            //
            // Rule 441:  Properties ::= Properties , Property
            //
            case 441: {
                //#line 876 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Properties = (List) getRhsSym(1);
                //#line 876 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 878 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Properties.add(Property);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 442:  Property ::= Type identifier
            //
            case 442: {
                //#line 884 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 884 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(2);
                //#line 886 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
    
                setResult(nf.PropertyDecl(pos(), Flags.PUBLIC.Final(), Type,
                nf.Id(identifier.getPosition(), identifier.getIdentifier())));
              
                break;
            }
     
            //
            // Rule 443:  MethodDeclaration ::= ThisClauseopt MethodModifiersopt ResultType MethodDeclarator Throwsopt MethodBody
            //
            case 443: {
                //#line 899 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr ThisClauseopt = (DepParameterExpr) getRhsSym(1);
                //#line 899 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(2);
                //#line 899 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 899 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] MethodDeclarator = (Object[]) getRhsSym(4);
                //#line 899 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 899 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 901 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
      Name c = (MethodDeclarator != null) ? (Name) MethodDeclarator[0] : null;
      List d = (MethodDeclarator != null) ? (List) MethodDeclarator[1] : null;
      Integer e = (MethodDeclarator != null) ? (Integer) MethodDeclarator[2] : null;
      Expr where = (MethodDeclarator != null) ? (Expr) MethodDeclarator[3] : null;
      if (ResultType.type() == ts.Void() && e != null && e.intValue() > 0)
         {
           // TODO: error!!!
           System.err.println("Fix me - encountered method returning void but with non-zero rank?");
         }

       MethodDecl md = nf.MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(6)),
          ThisClauseopt,
          extractFlags(MethodModifiersopt),
          nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3)), e != null ? e.intValue() : 1),
          c != null ? c.name : nf.Id(pos(), ""),
          d,
          where != null ? nf.DepParameterExpr(where.position(), where) : null,
          Throwsopt,
          MethodBody);
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                break;
            }
     
            //
            // Rule 444:  ExplicitConstructorInvocation ::= this ( ArgumentListopt ) ;
            //
            case 444: {
                //#line 925 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 927 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ThisCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 445:  ExplicitConstructorInvocation ::= super ( ArgumentListopt ) ;
            //
            case 445: {
                //#line 930 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 932 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SuperCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 446:  ExplicitConstructorInvocation ::= Primary . this ( ArgumentListopt ) ;
            //
            case 446: {
                //#line 935 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 935 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 937 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ThisCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 447:  ExplicitConstructorInvocation ::= Primary . super ( ArgumentListopt ) ;
            //
            case 447: {
                //#line 940 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 940 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 942 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.SuperCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 448:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier PropertyListopt ExtendsInterfacesopt InterfaceBody
            //
            case 448: {
                //#line 946 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 946 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 946 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                //#line 946 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(5);
                //#line 946 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(6);
                //#line 948 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
      checkTypeName(identifier);
      List/*<PropertyDecl>*/ props = PropertyListopt == null ? null 
                  : (List) PropertyListopt[0];
      Expr ci = PropertyListopt == null ? null : (Expr) PropertyListopt[1];
      ClassDecl cd = nf.ClassDecl(pos(),
                   extractFlags(InterfaceModifiersopt).Interface(),
                   nf.Id(identifier.getPosition(), identifier.getIdentifier()),
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
            // Rule 449:  AbstractMethodDeclaration ::= ThisClauseopt AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt ;
            //
            case 449: {
                //#line 965 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr ThisClauseopt = (DepParameterExpr) getRhsSym(1);
                //#line 965 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List AbstractMethodModifiersopt = (List) getRhsSym(2);
                //#line 965 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 965 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] MethodDeclarator = (Object[]) getRhsSym(4);
                //#line 965 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 967 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
     Name c = (Name) MethodDeclarator[0];
     List d = (List) MethodDeclarator[1];
     Integer e = (Integer) MethodDeclarator[2];
     Expr where = (Expr) MethodDeclarator[3];
     
     if (ResultType.type() == ts.Void() && e.intValue() > 0)
        {
          // TODO: error!!!
          assert(false);
        }

     MethodDecl md = nf.MethodDecl(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(4)),
                ThisClauseopt,
                extractFlags(AbstractMethodModifiersopt),
                nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3)), e.intValue()),
                nf.Id(c.pos, c.toString()),
                d,
                where != null ? nf.DepParameterExpr(where.position(), where) : null,
                Throwsopt,
                null);
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(AbstractMethodModifiersopt));
      setResult(md);
                break;
            }
     
            //
            // Rule 450:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
            //
            case 450: {
                //#line 992 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassOrInterfaceType = (TypeNode) getRhsSym(2);
                //#line 992 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 992 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(6);
                //#line 994 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt));
                else setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 451:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 451: {
                //#line 999 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 999 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                //#line 999 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 999 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(8);
                //#line 1001 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Name b = new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), Primary, b.toType(), ArgumentListopt));
                else setResult(nf.New(pos(), Primary, b.toType(), ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 452:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 452: {
                //#line 1007 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 1007 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                //#line 1007 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1007 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(8);
                //#line 1009 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Name b = new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt));
                else setResult(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 453:  MethodInvocation ::= Primary . identifier ( ArgumentListopt )
            //
            case 453: {
                //#line 1016 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1016 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1016 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1018 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), Primary, nf.Id(identifier.getPosition(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 454:  MethodInvocation ::= super . identifier ( ArgumentListopt )
            //
            case 454: {
                //#line 1021 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1021 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1023 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(identifier.getPosition(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 455:  MethodInvocation ::= ClassName . super$sup . identifier ( ArgumentListopt )
            //
            case 455: {
                //#line 1026 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1026 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 1026 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                //#line 1026 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1028 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(identifier.getPosition(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 456:  AssignPropertyCall ::= property ( ArgumentList )
            //
            case 456: {
                //#line 1033 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 1035 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.AssignPropertyCall(pos(),  ArgumentList));
                break;
            }
     
            //
            // Rule 460:  AnnotatedType ::= Type Annotations
            //
            case 460: {
                //#line 1048 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1048 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1050 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn);
                break;
            }
     
            //
            // Rule 461:  SpecialType ::= nullable < Type > DepParametersopt
            //
            case 461: {
                //#line 1056 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1056 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(5);
                //#line 1058 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeNode t = nf.Nullable(pos(), Type);
                setResult(DepParametersopt == null ? t 
                : nf.AmbDepTypeNode(pos(), t, null, DepParametersopt));
      
                break;
            }
     
            //
            // Rule 462:  SpecialType ::= future < Type >
            //
            case 462: {
                //#line 1064 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1066 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.FutureNode(pos(), Type));
                break;
            }
     
            //
            // Rule 465:  PrimitiveType ::= NumericType DepParametersopt
            //
            case 465: {
                //#line 1079 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode NumericType = (TypeNode) getRhsSym(1);
                //#line 1079 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 1081 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              //  System.out.println("Parser: parsed PrimitiveType |" + NumericType + "| |" + DepParametersopt +"|");
                setResult(DepParametersopt == null
                               ? NumericType
                               : nf.AmbDepTypeNode(pos(), NumericType, null, DepParametersopt));
                break;
            }
     
            //
            // Rule 466:  PrimitiveType ::= boolean DepParametersopt
            //
            case 466: {
                //#line 1087 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 1089 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                TypeNode res = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(DepParametersopt==null 
                           ? res 
                           : nf.AmbDepTypeNode(pos(), res, null, DepParametersopt));
               break;
            }
     
            //
            // Rule 471:  ClassOrInterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 471: {
                //#line 1101 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 1101 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 1101 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                //#line 1103 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            TypeNode type;
            
            if (ts.isPrimitiveTypeName(TypeName.name.id())) {
                try {
                    type= nf.CanonicalTypeNode(pos(), ts.primitiveForName(TypeName.name.id()));
                } catch (SemanticException e) {
                    throw new InternalCompilerError("Unable to create primitive type for '" + TypeName.name.id() + "'!");
                }
            } else
                type= TypeName.toType();
            //  System.out.println("Parser: parsed ClassOrInterfaceType |" + TypeName + "| |" + DepParametersopt +"|");
                setResult(DepParametersopt == null
                               ? type
                               : nf.AmbDepTypeNode(pos(), type, null, DepParametersopt));
                break;
            }
     
            //
            // Rule 472:  DepParameters ::= ( DepParameterExpr )
            //
            case 472: {
                //#line 1120 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(2);
                //#line 1122 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(DepParameterExpr);
                break;
            }
     
            //
            // Rule 473:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 473: {
                //#line 1126 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 1126 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(2);
                //#line 1128 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.DepParameterExpr(pos(), ArgumentList, WhereClauseopt));
                break;
            }
     
            //
            // Rule 474:  DepParameterExpr ::= WhereClause
            //
            case 474: {
                //#line 1131 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClause = (Expr) getRhsSym(1);
                //#line 1133 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.DepParameterExpr(pos(), Collections.EMPTY_LIST, WhereClause));
                break;
            }
     
            //
            // Rule 475:  WhereClause ::= : Expression
            //
            case 475: {
                //#line 1137 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1139 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(Expression);
                break;
            }
       
            //
            // Rule 476:  WhereClause ::= : ExistentialList ; Expression
            //
            case 476: {
                //#line 1142 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(2);
                //#line 1142 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1145 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(nf.Existential(pos(), ExistentialList, Expression));
                break;
            }
       
            //
            // Rule 477:  ExistentialListopt ::= $Empty
            //
            case 477: {
                
                //#line 1151 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(new ArrayList());
                break;
            }
       
            //
            // Rule 478:  ExistentialListopt ::= ExistentialList
            //
            case 478: {
                //#line 1154 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1156 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            setResult(ExistentialList);
                break;
            }
     
            //
            // Rule 479:  ExistentialList ::= FormalParameter
            //
            case 479: {
                //#line 1160 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1162 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(Flags.FINAL));
                setResult(l);
                break;
            }
     
            //
            // Rule 480:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 480: {
                //#line 1167 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1167 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1169 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ExistentialList.add(FormalParameter.flags(Flags.FINAL));
                break;
            }
     
            //
            // Rule 482:  X10ArrayType ::= Type [ . ]
            //
            case 482: {
                //#line 1179 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1181 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, null));
                break;
            }
     
            //
            // Rule 483:  X10ArrayType ::= Type value [ . ]
            //
            case 483: {
                //#line 1184 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1186 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, null));
                break;
            }
     
            //
            // Rule 484:  X10ArrayType ::= Type [ DepParameterExpr ]
            //
            case 484: {
                //#line 1189 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1189 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(3);
                //#line 1191 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
                break;
            }
     
            //
            // Rule 485:  X10ArrayType ::= Type value [ DepParameterExpr ]
            //
            case 485: {
                //#line 1194 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1194 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(4);
                //#line 1196 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, DepParameterExpr));
                break;
            }
     
            //
            // Rule 486:  ObjectKind ::= value
            //
            case 486:
                throw new Error("No action specified for rule " + 486);
 
            //
            // Rule 487:  ObjectKind ::= reference
            //
            case 487:
                throw new Error("No action specified for rule " + 487);
 
            //
            // Rule 488:  MethodModifier ::= atomic
            //
            case 488: {
                
                //#line 1210 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(X10Flags.ATOMIC));
                break;
            }
     
            //
            // Rule 489:  MethodModifier ::= extern
            //
            case 489: {
                
                //#line 1215 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.NATIVE));
                break;
            }
     
            //
            // Rule 490:  MethodModifier ::= safe
            //
            case 490: {
                
                //#line 1220 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(X10Flags.SAFE));
                break;
            }
     
            //
            // Rule 491:  MethodModifier ::= sequential
            //
            case 491: {
                
                //#line 1225 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(X10Flags.SEQUENTIAL));
                break;
            }
     
            //
            // Rule 492:  MethodModifier ::= local
            //
            case 492: {
                
                //#line 1230 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(X10Flags.LOCAL));
                break;
            }
     
            //
            // Rule 493:  MethodModifier ::= nonblocking
            //
            case 493: {
                
                //#line 1235 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(X10Flags.NON_BLOCKING));
                break;
            }
     
            //
            // Rule 495:  ValueClassDeclaration ::= X10ClassModifiersopt value identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 495: {
                //#line 1241 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List X10ClassModifiersopt = (List) getRhsSym(1);
                //#line 1241 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1241 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                //#line 1241 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(5);
                //#line 1241 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(6);
                //#line 1241 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(7);
                //#line 1243 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
    checkTypeName(identifier);
    List/*<PropertyDecl>*/ props = PropertyListopt==null ? null : (List) PropertyListopt[0];
    Expr ci = PropertyListopt==null ? null : (Expr) PropertyListopt[1];
    ClassDecl cd = (nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
    extractFlags(X10ClassModifiersopt), nf.Id(identifier.getPosition(), identifier.getIdentifier()), 
    props, ci, Superopt, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(X10ClassModifiersopt));
    setResult(cd);
                break;
            }
     
            //
            // Rule 496:  ValueClassDeclaration ::= X10ClassModifiersopt value class identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 496: {
                //#line 1253 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List X10ClassModifiersopt = (List) getRhsSym(1);
                //#line 1253 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                //#line 1253 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(5);
                //#line 1253 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(6);
                //#line 1253 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1253 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1255 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                checkTypeName(identifier);
    List/*<PropertyDecl>*/ props = PropertyListopt==null ? null : (List) PropertyListopt[0];
    Expr ci = PropertyListopt==null ? null : (Expr) PropertyListopt[1];
    ClassDecl cd = (nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                                extractFlags(X10ClassModifiersopt), nf.Id(identifier.getPosition(), identifier.getIdentifier()), 
                                props, ci, Superopt, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(X10ClassModifiersopt));
    setResult(cd);
                break;
            }
     
            //
            // Rule 497:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 497: {
                //#line 1266 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1266 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] ConstructorDeclarator = (Object[]) getRhsSym(2);
                //#line 1266 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(3);
                //#line 1266 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(4);
                //#line 1268 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
     Name a = (Name) ConstructorDeclarator[1];
     DepParameterExpr c = (DepParameterExpr) ConstructorDeclarator[2];
     List b = (List) ConstructorDeclarator[3];
     Expr e = (Expr) ConstructorDeclarator[4];
     List n = (List) ConstructorDeclarator[5];
     
       TypeNode resultType = a.toType();        
       if (c != null) 
            resultType = nf.AmbDepTypeNode(pos((JPGPosition) a.pos, (JPGPosition) c.position()), resultType, null, c);
     if (resultType.ext() instanceof X10Ext && n != null) {
         resultType = (TypeNode) ((X10Ext) resultType.ext()).annotations(n);
     }
     ConstructorDecl cd = nf.ConstructorDecl(pos(),
                                             extractFlags(ConstructorModifiersopt),
                                             nf.Id(a.pos, a.toString()),
                                             resultType,
                                             b,
                                             e != null ? nf.DepParameterExpr(e.position(), e) : null,
                                             Throwsopt,
                                             ConstructorBody);
     cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ConstructorModifiersopt));
     setResult(cd);
               break;
            }
    
            //
            // Rule 498:  ConstructorDeclarator ::= SimpleTypeName DepParametersopt Annotationsopt ( FormalParameterListopt WhereClauseopt )
            //
            case 498: {
                //#line 1293 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name SimpleTypeName = (Name) getRhsSym(1);
                //#line 1293 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 1293 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(3);
                //#line 1293 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(5);
                //#line 1293 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(6);
                //#line 1295 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
             Object[] a = new Object[6];
             a[1] = SimpleTypeName;
             a[2] = DepParametersopt;
             a[3] = FormalParameterListopt;
             a[4] = WhereClauseopt;
             a[5] = Annotationsopt;
             setResult(a);
               break;
            }
    
            //
            // Rule 499:  ThisClause ::= this DepParameters
            //
            case 499: {
                //#line 1304 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(2);
                //#line 1306 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(DepParameters);
                break;
            }
     
            //
            // Rule 500:  Super ::= extends DataType
            //
            case 500: {
                //#line 1310 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode DataType = (TypeNode) getRhsSym(2);
                //#line 1312 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(DataType);
                break;
            }
     
            //
            // Rule 501:  MethodDeclarator ::= identifier ( FormalParameterListopt WhereClauseopt )
            //
            case 501: {
                //#line 1316 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1316 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1316 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(4);
                //#line 1318 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
            //   System.out.println("Parsing methoddeclarator...");
                Object[] a = new Object[5];
               a[0] = new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                a[1] = FormalParameterListopt;
               a[2] = new Integer(0);
               a[3] = WhereClauseopt;
             
                setResult(a);
                break;
            }
     
            //
            // Rule 502:  MethodDeclarator ::= MethodDeclarator [ ]
            //
            case 502: {
                //#line 1328 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object[] MethodDeclarator = (Object[]) getRhsSym(1);
                //#line 1330 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                MethodDeclarator[2] = new Integer(((Integer) MethodDeclarator[2]).intValue() + 1);
                // setResult(MethodDeclarator);
               break;
            }
     
            //
            // Rule 503:  FieldDeclaration ::= ThisClauseopt FieldModifiersopt Type VariableDeclarators ;
            //
            case 503: {
                //#line 1336 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr ThisClauseopt = (DepParameterExpr) getRhsSym(1);
                //#line 1336 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(2);
                //#line 1336 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1336 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(4);
                //#line 1338 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                if (VariableDeclarators != null && VariableDeclarators.size() > 0) {
                    boolean gt1 = (VariableDeclarators.size() > 1);
                    for (Iterator i = VariableDeclarators.iterator(); i.hasNext();)
                    {
                        X10VarDeclarator d = (X10VarDeclarator) i.next();
                        if (d.hasExplodedVars())
                          // TODO: Report this exception correctly.
                          throw new Error("Field Declarations may not have exploded variables." + pos());
                        Position p = gt1 ? d.position() :
                        		pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(4));
                        d.setFlag(extractFlags(FieldModifiersopt));
                        FieldDecl fd = nf.FieldDecl(p,
                                           ThisClauseopt,
                                           d.flags,
                                           nf.array(Type, Type.position(), d.dims),
                                           d.name,
                                           d.init);
                        fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(FieldModifiersopt));
                        l.add(fd);
                    }
                }
                setResult(l);
                break;
            }
     
            //
            // Rule 504:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt Dims ArrayInitializer
            //
            case 504: {
                //#line 1377 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1377 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(3);
                //#line 1377 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Integer Dims = (Integer) getRhsSym(4);
                //#line 1377 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                ArrayInit ArrayInitializer = (ArrayInit) getRhsSym(5);
                //#line 1379 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Dims.intValue(), ArrayInitializer));
                break;
            }
     
            //
            // Rule 505:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr Dims
            //
            case 505: {
                //#line 1383 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1383 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(3);
                //#line 1383 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr DimExpr = (Expr) getRhsSym(4);
                //#line 1383 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Integer Dims = (Integer) getRhsSym(5);
                //#line 1385 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Collections.singletonList(DimExpr), Dims.intValue()));
                break;
            }
     
            //
            // Rule 506:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr DimExprs Dimsopt
            //
            case 506: {
                //#line 1389 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1389 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(3);
                //#line 1389 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr DimExpr = (Expr) getRhsSym(4);
                //#line 1389 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List DimExprs = (List) getRhsSym(5);
                //#line 1389 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Integer Dimsopt = (Integer) getRhsSym(6);
                //#line 1391 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                l.addAll(DimExprs);
                setResult(nf.NewArray(pos(), ArrayBaseType, l, Dimsopt.intValue()));
                break;
            }
     
            //
            // Rule 507:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ]
            //
            case 507: {
                //#line 1398 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1398 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object Valueopt = (Object) getRhsSym(3);
                //#line 1398 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(4);
                //#line 1398 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(6);
                //#line 1400 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, null));
                break;
            }
     
            //
            // Rule 508:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression$distr ] Expression$initializer
            //
            case 508: {
                //#line 1403 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1403 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object Valueopt = (Object) getRhsSym(3);
                //#line 1403 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(4);
                //#line 1403 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr distr = (Expr) getRhsSym(6);
                //#line 1403 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr initializer = (Expr) getRhsSym(8);
                //#line 1405 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, distr, initializer));
                break;
            }
     
            //
            // Rule 509:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ] ($lparen FormalParameter ) MethodBody
            //
            case 509: {
                //#line 1408 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1408 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object Valueopt = (Object) getRhsSym(3);
                //#line 1408 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(4);
                //#line 1408 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(6);
                //#line 1408 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken lparen = (IToken) getRhsIToken(8);
                //#line 1408 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(9);
                //#line 1408 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1410 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // Expr initializer = makeInitializer( pos(getRhsFirstTokenIndex(8), getRightSpan()), ArrayBaseType, FormalParameter, MethodBody );
                List formals = new TypedList(new ArrayList(1), Formal.class, false);
                formals.add(FormalParameter);
                Closure closure = nf.Closure(MethodBody.position(), formals, ArrayBaseType, new TypedList(new ArrayList(), Type.class, true), MethodBody);
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, closure));
                break;
            }
     
            //
            // Rule 510:  Valueopt ::= $Empty
            //
            case 510:
                setResult(null);
                break;
 
            //
            // Rule 511:  Valueopt ::= value
            //
            case 511: {
                
                //#line 1422 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 516:  ArrayBaseType ::= ( Type )
            //
            case 516: {
                //#line 1431 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1433 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Type);
                break;
            }
     
            //
            // Rule 517:  ArrayAccess ::= ExpressionName [ ArgumentList ]
            //
            case 517: {
                //#line 1437 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 1437 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 1439 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), ExpressionName.toExpr(), (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), ExpressionName.toExpr(), ArgumentList));
                break;
            }
     
            //
            // Rule 518:  ArrayAccess ::= PrimaryNoNewArray [ ArgumentList ]
            //
            case 518: {
                //#line 1444 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PrimaryNoNewArray = (Expr) getRhsSym(1);
                //#line 1444 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 1446 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), PrimaryNoNewArray, (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), PrimaryNoNewArray, ArgumentList));
                break;
            }
     
            //
            // Rule 538:  NowStatement ::= now ( Clock ) Statement
            //
            case 538: {
                //#line 1477 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1477 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1479 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Now(pos(), Clock, Statement));
                break;
            }
     
            //
            // Rule 539:  ClockedClause ::= clocked ( ClockList )
            //
            case 539: {
                //#line 1483 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1485 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 540:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 540: {
                //#line 1489 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1489 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1489 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1491 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                break;
            }
     
            //
            // Rule 541:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 541: {
                //#line 1499 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1499 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1501 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                               ? nf.Here(pos(getLeftSpan()))
                                               : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 542:  WhenStatement ::= when ( Expression ) Statement
            //
            case 542: {
                //#line 1508 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1508 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1510 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.When(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 543:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 543: {
                //#line 1513 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1513 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1513 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1513 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1515 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 544:  ForEachStatement ::= foreach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 544: {
                //#line 1520 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1520 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1520 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1520 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1522 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ForEach(pos(),
                              FormalParameter.flags(FormalParameter.flags().Final()),
                              Expression,
                              ClockedClauseopt,
                              Statement));
                break;
            }
     
            //
            // Rule 545:  AtEachStatement ::= ateach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 545: {
                //#line 1530 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1530 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1530 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1530 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1532 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.AtEach(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             ClockedClauseopt,
                             Statement));
                break;
            }
     
            //
            // Rule 546:  EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
            //
            case 546: {
                //#line 1540 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1540 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1540 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1542 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ForLoop(pos(),
                        FormalParameter.flags(FormalParameter.flags().Final()),
                        Expression,
                        Statement));
                break;
            }
     
            //
            // Rule 547:  FinishStatement ::= finish Statement
            //
            case 547: {
                //#line 1549 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1551 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Finish(pos(),  Statement));
                break;
            }
     
            //
            // Rule 548:  AnnotationStatement ::= Annotations Statement
            //
            case 548: {
                //#line 1556 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 1556 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1558 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (Statement.ext() instanceof X10Ext && Annotations instanceof List) {
                    Statement = (Stmt) ((X10Ext) Statement.ext()).annotations((List) Annotations);
                }
                setResult(Statement);
                break;
            }
     
            //
            // Rule 549:  NowStatementNoShortIf ::= now ( Clock ) StatementNoShortIf
            //
            case 549: {
                //#line 1566 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1566 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1568 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Now(pos(), Clock, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 550:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt ClockedClauseopt StatementNoShortIf
            //
            case 550: {
                //#line 1572 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1572 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1572 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(4);
                //#line 1574 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(getLeftSpan()))
                                                : PlaceExpressionSingleListopt),
                                            ClockedClauseopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 551:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 551: {
                //#line 1581 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                //#line 1583 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 552:  WhenStatementNoShortIf ::= when ( Expression ) StatementNoShortIf
            //
            case 552: {
                //#line 1587 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1587 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1589 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.When(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 553:  WhenStatementNoShortIf ::= WhenStatement or$or ( Expression ) StatementNoShortIf
            //
            case 553: {
                //#line 1592 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1592 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1592 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1592 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(6);
                //#line 1594 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, StatementNoShortIf);
                setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 554:  ForEachStatementNoShortIf ::= foreach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 554: {
                //#line 1599 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1599 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1599 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1599 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(8);
                //#line 1601 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.ForEach(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             ClockedClauseopt,
                             StatementNoShortIf));

                break;
            }
     
            //
            // Rule 555:  AtEachStatementNoShortIf ::= ateach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 555: {
                //#line 1610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1610 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(8);
                //#line 1612 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.AtEach(pos(),
                            FormalParameter.flags(FormalParameter.flags().Final()),
                            Expression,
                            ClockedClauseopt,
                            StatementNoShortIf));
                break;
            }
     
            //
            // Rule 556:  EnhancedForStatementNoShortIf ::= for ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 556: {
                //#line 1620 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1620 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1620 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(7);
                //#line 1622 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                  setResult(nf.ForLoop(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             StatementNoShortIf));
                break;
            }
     
            //
            // Rule 557:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 557: {
                //#line 1629 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                //#line 1631 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Finish(pos(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 558:  AnnotationStatementNoShortIf ::= Annotations StatementNoShortIf
            //
            case 558: {
                //#line 1636 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 1636 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                //#line 1638 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                if (StatementNoShortIf.ext() instanceof X10Ext && Annotations instanceof List) {
                    StatementNoShortIf = (Stmt) ((X10Ext) StatementNoShortIf.ext()).annotations((List) Annotations);
                }
                setResult(StatementNoShortIf);
                break;
            }
     
            //
            // Rule 559:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 559: {
                //#line 1646 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1648 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
              setResult(PlaceExpression);
                break;
            }
     
            //
            // Rule 561:  NextStatement ::= next ;
            //
            case 561: {
                
                //#line 1656 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 562:  AwaitStatement ::= await Expression ;
            //
            case 562: {
                //#line 1660 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1662 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Await(pos(), Expression));
                break;
            }
     
            //
            // Rule 563:  ClockList ::= Clock
            //
            case 563: {
                //#line 1666 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1668 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                break;
            }
     
            //
            // Rule 564:  ClockList ::= ClockList , Clock
            //
            case 564: {
                //#line 1673 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1673 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1675 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 565:  Clock ::= Expression
            //
            case 565: {
                //#line 1681 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1683 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
    setResult(Expression);
                break;
            }
     
            //
            // Rule 566:  CastExpression ::= ( PrimitiveType ) UnaryExpression
            //
            case 566: {
                //#line 1695 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode PrimitiveType = (TypeNode) getRhsSym(2);
                //#line 1695 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(4);
                //#line 1697 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Cast(pos(), PrimitiveType, UnaryExpression));
                break;
            }
     
            //
            // Rule 567:  CastExpression ::= ( SpecialType ) UnaryExpressionNotPlusMinus
            //
            case 567: {
                //#line 1700 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode SpecialType = (TypeNode) getRhsSym(2);
                //#line 1700 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(4);
                //#line 1702 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Cast(pos(), SpecialType, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 568:  CastExpression ::= ( ReferenceType ) UnaryExpressionNotPlusMinus
            //
            case 568: {
                //#line 1705 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode ReferenceType = (TypeNode) getRhsSym(2);
                //#line 1705 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(4);
                //#line 1707 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Cast(pos(), ReferenceType, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 569:  CastExpression ::= ( ! Expression ) UnaryExpressionNotPlusMinus
            //
            case 569: {
                //#line 1710 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1710 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(5);
                //#line 1712 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.PlaceCast(pos(), Expression, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 570:  CastExpression ::= ( AnnotatedType ) UnaryExpression
            //
            case 570: {
                //#line 1715 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode AnnotatedType = (TypeNode) getRhsSym(2);
                //#line 1715 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(4);
                //#line 1717 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Cast(pos(), AnnotatedType, UnaryExpression));
                break;
            }
     
            //
            // Rule 571:  CastExpression ::= ( Annotations ) UnaryExpressionNotPlusMinus
            //
            case 571: {
                //#line 1720 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1720 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(4);
                //#line 1722 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Expr e = UnaryExpressionNotPlusMinus;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e);
                break;
            }
     
            //
            // Rule 572:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 572: {
                //#line 1734 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1734 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1736 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                break;
            }
     
            //
            // Rule 573:  IdentifierList ::= identifier
            //
            case 573: {
                //#line 1742 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1744 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Name.class, false);
                l.add(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                setResult(l);
                break;
            }
     
            //
            // Rule 574:  IdentifierList ::= IdentifierList , identifier
            //
            case 574: {
                //#line 1749 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 1749 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1751 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                IdentifierList.add(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                setResult(IdentifierList);
                break;
            }
     
            //
            // Rule 575:  Primary ::= here
            //
            case 575: {
                
                //#line 1758 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
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
                break;
            }
     
            //
            // Rule 578:  RegionExpression ::= Expression$expr1 : Expression$expr2
            //
            case 578: {
                //#line 1774 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 1774 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 1776 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                /*Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");

                Name x10LangRegion = new Name(nf, ts, pos(), x10Lang, "region");
                Name x10LangRegionFactory = new Name(nf, ts, pos(), x10LangRegion, "factory");
                Name x10LangRegionFactoryRegion = new Name(nf, ts, pos(), x10LangRegionFactory, "region");
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(expr1);
                l.add(expr2);
                 Call regionCall = nf.Call( pos(), x10LangRegionFactoryRegion.prefix.toReceiver(), "region", l  );
                */
                Call regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                break;
            }
     
            //
            // Rule 579:  RegionExpressionList ::= RegionExpression
            //
            case 579: {
                //#line 1792 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 1794 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                break;
            }
     
            //
            // Rule 580:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 580: {
                //#line 1799 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 1799 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 1801 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                break;
            }
     
            //
            // Rule 581:  Primary ::= [ RegionExpressionList ]
            //
            case 581: {
                //#line 1806 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(2);
                //#line 1808 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                Receiver x10LangPointFactory = nf.ReceiverFromQualifiedName(pos(), "x10.lang.point.factory");
                Receiver x10LangRegionFactory = nf.ReceiverFromQualifiedName(pos(), "x10.lang.region.factory");
                Tuple tuple = nf.Tuple(pos(), x10LangPointFactory, x10LangRegionFactory, RegionExpressionList);
                setResult(tuple);
                break;
            }
     
            //
            // Rule 582:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 582: {
                //#line 1815 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 1815 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 1817 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                ConstantDistMaker call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                break;
            }
     
            //
            // Rule 583:  FutureExpression ::= future PlaceExpressionSingleListopt { Expression }
            //
            case 583: {
                //#line 1822 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1822 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1824 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(nf.Future(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(getLeftSpan()))
                                                : PlaceExpressionSingleListopt), Expression));
                break;
            }
     
            //
            // Rule 584:  FieldModifier ::= mutable
            //
            case 584: {
                
                //#line 1832 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(X10Flags.MUTABLE));
                break;
            }
     
            //
            // Rule 585:  FieldModifier ::= const
            //
            case 585: {
                
                //#line 1837 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(Collections.singletonList(Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL)));
                break;
            }
     
            //
            // Rule 586:  FunExpression ::= fun Type ( FormalParameterListopt ) { Expression }
            //
            case 586:
                throw new Error("No action specified for rule " + 586);
 
            //
            // Rule 587:  MethodInvocation ::= MethodName ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 587:
                throw new Error("No action specified for rule " + 587); 
 
            //
            // Rule 588:  MethodInvocation ::= Primary . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 588:
                throw new Error("No action specified for rule " + 588); 
 
            //
            // Rule 589:  MethodInvocation ::= super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 589:
                throw new Error("No action specified for rule " + 589); 
 
            //
            // Rule 590:  MethodInvocation ::= ClassName . super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 590:
                throw new Error("No action specified for rule " + 590); 
 
            //
            // Rule 591:  MethodInvocation ::= TypeName . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 591:
                throw new Error("No action specified for rule " + 591); 
 
            //
            // Rule 592:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 592:
                throw new Error("No action specified for rule " + 592); 
 
            //
            // Rule 593:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 593:
                throw new Error("No action specified for rule " + 593); 
 
            //
            // Rule 594:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 594:
                throw new Error("No action specified for rule " + 594); 
 
            //
            // Rule 595:  MethodModifier ::= synchronized
            //
            case 595: {
                //#line 1868 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, "\"synchronized\" is an invalid X10 Method Modifier",
                           getErrorPosition(getLeftSpan(), getRightSpan()));
                setResult(Collections.singletonList(Flags.SYNCHRONIZED));
                break;
            }
     
            //
            // Rule 596:  FieldModifier ::= volatile
            //
            case 596: {
                //#line 1877 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, "\"volatile\" is an invalid X10 Field Modifier",
                           getErrorPosition(getLeftSpan(), getRightSpan()));
                setResult(Collections.singletonList(Flags.VOLATILE));
                break;
            }
     
            //
            // Rule 597:  SynchronizedStatement ::= synchronized ( Expression ) Block
            //
            case 597: {
                //#line 1884 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1884 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1886 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, "Synchronized Statement is invalid in X10",
                           getErrorPosition(getLeftSpan(), getRightSpan()));
                setResult(nf.Synchronized(pos(), Expression, Block));
                break;
            }
     
            //
            // Rule 598:  ThisClauseopt ::= $Empty
            //
            case 598:
                setResult(null);
                break;
 
            //
            // Rule 600:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 600:
                setResult(null);
                break;
 
            //
            // Rule 602:  DepParametersopt ::= $Empty
            //
            case 602:
                setResult(null);
                break;
 
            //
            // Rule 604:  PropertyListopt ::= $Empty
            //
            case 604:
                setResult(null);
                break;
 
            //
            // Rule 606:  WhereClauseopt ::= $Empty
            //
            case 606:
                setResult(null);
                break;
 
            //
            // Rule 608:  ObjectKindopt ::= $Empty
            //
            case 608:
                setResult(null);
                break;
 
            //
            // Rule 610:  ArrayInitializeropt ::= $Empty
            //
            case 610:
                setResult(null);
                break;
 
            //
            // Rule 612:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 612:
                setResult(null);
                break;
 
            //
            // Rule 614:  X10ClassModifiersopt ::= $Empty
            //
            case 614: {
                
                //#line 1928 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
         setResult(Collections.singletonList(X10Flags.toX10Flags(Flags.NONE)));
                break;
            }  
            //
            // Rule 616:  Unsafeopt ::= $Empty
            //
            case 616:
                setResult(null);
                break;
 
            //
            // Rule 617:  Unsafeopt ::= unsafe
            //
            case 617: {
                
                //#line 1936 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 618:  ParamIdopt ::= $Empty
            //
            case 618:
                setResult(null);
                break;
 
            //
            // Rule 619:  ParamIdopt ::= identifier
            //
            case 619: {
                //#line 1943 "/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1945 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 620:  ClockedClauseopt ::= $Empty
            //
            case 620: {
                
                //#line 1951 "/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates/btParserTemplate.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

