
//#line 18 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
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

//#line 28 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
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


    //#line 284 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
    private ErrorQueue eq;
    private X10TypeSystem ts;
    private X10NodeFactory nf;
    private FileSource source;
    private boolean unrecoverableSyntaxError = false;

    public X10Parser(LexStream lexStream, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
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

            if ((!unrecoverableSyntaxError) && (sf != null))
                return sf.source(source);

            eq.enqueue(ErrorInfo.SYNTAX_ERROR, "Unable to parse " + source.name() + ".", new Position(null, file(), 1, 1, 1, 1));
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
                //#line 6 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Name TypeName = (Name) getRhsSym(1);
                //#line 8 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 16 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Name PackageName = (Name) getRhsSym(1);
                //#line 18 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 26 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 28 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 36 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 38 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 46 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Name PackageOrTypeName = (Name) getRhsSym(1);
                //#line 48 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 56 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 58 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 66 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                break;
            }
     
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
                
                //#line 74 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                break;
            }
     
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
                //#line 78 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 78 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                break;
            }
     
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
                //#line 85 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
                //#line 92 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
                //#line 98 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 107 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 115 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                break;
            }
     
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
                //#line 120 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 120 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 94 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 96 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 19:  IntegralType ::= byte
            //
            case 19: {
                
                //#line 121 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Byte()));
                break;
            }
     
            //
            // Rule 20:  IntegralType ::= char
            //
            case 20: {
                
                //#line 126 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Char()));
                break;
            }
     
            //
            // Rule 21:  IntegralType ::= short
            //
            case 21: {
                
                //#line 131 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Short()));
                break;
            }
     
            //
            // Rule 22:  IntegralType ::= int
            //
            case 22: {
                
                //#line 136 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Int()));
                break;
            }
     
            //
            // Rule 23:  IntegralType ::= long
            //
            case 23: {
                
                //#line 141 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Long()));
                break;
            }
     
            //
            // Rule 24:  FloatingPointType ::= float
            //
            case 24: {
                
                //#line 147 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Float()));
                break;
            }
     
            //
            // Rule 25:  FloatingPointType ::= double
            //
            case 25: {
                
                //#line 152 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Double()));
                break;
            }
     
            //
            // Rule 28:  TypeName ::= identifier
            //
            case 28: {
                //#line 175 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 177 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 29:  TypeName ::= TypeName . identifier
            //
            case 29: {
                //#line 180 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name TypeName = (Name) getRhsSym(1);
                //#line 180 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 182 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 31:  ArrayType ::= Type [ ]
            //
            case 31: {
                //#line 194 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 196 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.array(Type, pos(), 1));
                break;
            }
     
            //
            // Rule 32:  PackageName ::= identifier
            //
            case 32: {
                //#line 241 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 243 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 33:  PackageName ::= PackageName . identifier
            //
            case 33: {
                //#line 246 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name PackageName = (Name) getRhsSym(1);
                //#line 246 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 248 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
                //#line 262 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 264 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 35:  ExpressionName ::= AmbiguousName . identifier
            //
            case 35: {
                //#line 267 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 267 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 269 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
                //#line 277 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 279 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 37:  MethodName ::= AmbiguousName . identifier
            //
            case 37: {
                //#line 282 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 282 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 284 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
                //#line 292 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 294 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 39:  PackageOrTypeName ::= PackageOrTypeName . identifier
            //
            case 39: {
                //#line 297 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name PackageOrTypeName = (Name) getRhsSym(1);
                //#line 297 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 299 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
                //#line 307 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 309 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 41:  AmbiguousName ::= AmbiguousName . identifier
            //
            case 41: {
                //#line 312 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 312 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 314 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
                //#line 324 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 324 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 324 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 326 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
                //#line 340 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 342 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 44:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 44: {
                //#line 347 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 347 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 349 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 45:  TypeDeclarations ::= TypeDeclaration
            //
            case 45: {
                //#line 355 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(1);
                //#line 357 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
                //#line 363 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 363 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(2);
                //#line 365 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 49:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 49: {
                //#line 378 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name TypeName = (Name) getRhsSym(2);
                //#line 380 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, TypeName.toString()));
                break;
            }
     
            //
            // Rule 50:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 50: {
                //#line 384 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name PackageOrTypeName = (Name) getRhsSym(2);
                //#line 386 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, PackageOrTypeName.toString()));
                break;
            }
     
            //
            // Rule 53:  TypeDeclaration ::= ;
            //
            case 53: {
                
                //#line 400 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(null);
                break;
            }
     
            //
            // Rule 56:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 56: {
                //#line 412 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ClassModifiers = (Flags) getRhsSym(1);
                //#line 412 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ClassModifier = (Flags) getRhsSym(2);
                //#line 414 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ClassModifiers.set(ClassModifier));
                break;
            }
     
            //
            // Rule 57:  ClassModifier ::= public
            //
            case 57: {
                
                //#line 422 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 58:  ClassModifier ::= protected
            //
            case 58: {
                
                //#line 427 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 59:  ClassModifier ::= private
            //
            case 59: {
                
                //#line 432 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 60:  ClassModifier ::= abstract
            //
            case 60: {
                
                //#line 437 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 61:  ClassModifier ::= static
            //
            case 61: {
                
                //#line 442 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 62:  ClassModifier ::= final
            //
            case 62: {
                
                //#line 447 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 63:  ClassModifier ::= strictfp
            //
            case 63: {
                
                //#line 452 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 64:  Super ::= extends ClassType
            //
            case 64: {
                //#line 464 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 466 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ClassType);
                break;
            }
     
            //
            // Rule 65:  Interfaces ::= implements InterfaceTypeList
            //
            case 65: {
                //#line 475 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 477 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 66:  InterfaceTypeList ::= InterfaceType
            //
            case 66: {
                //#line 481 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(1);
                //#line 483 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 67:  InterfaceTypeList ::= InterfaceTypeList , InterfaceType
            //
            case 67: {
                //#line 488 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 488 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                //#line 490 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                InterfaceTypeList.add(InterfaceType);
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 68:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 68: {
                //#line 500 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 502 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                break;
            }
     
            //
            // Rule 70:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 70: {
                //#line 507 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 507 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 509 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                break;
            }
     
            //
            // Rule 72:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 72: {
                //#line 515 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block InstanceInitializer = (Block) getRhsSym(1);
                //#line 517 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.NONE, InstanceInitializer));
                setResult(l);
                break;
            }
     
            //
            // Rule 73:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 73: {
                //#line 522 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block StaticInitializer = (Block) getRhsSym(1);
                //#line 524 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.STATIC, StaticInitializer));
                setResult(l);
                break;
            }
     
            //
            // Rule 74:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 74: {
                //#line 529 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 531 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 76:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 76: {
                //#line 538 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                MethodDecl MethodDeclaration = (MethodDecl) getRhsSym(1);
                //#line 540 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 77:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 77: {
                //#line 545 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 547 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 78:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 78: {
                //#line 552 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 554 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 79:  ClassMemberDeclaration ::= ;
            //
            case 79: {
                
                //#line 561 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                break;
            }
     
            //
            // Rule 80:  VariableDeclarators ::= VariableDeclarator
            //
            case 80: {
                //#line 569 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(1);
                //#line 571 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), X10VarDeclarator.class, false);
                l.add(VariableDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 81:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 81: {
                //#line 576 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 576 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(3);
                //#line 578 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                break;
            }
     
            //
            // Rule 83:  VariableDeclarator ::= VariableDeclaratorId = VariableInitializer
            //
            case 83: {
                //#line 584 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                //#line 584 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 586 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VariableDeclaratorId.init = VariableInitializer;
                VariableDeclaratorId.position(pos());
                // setResult(VariableDeclaratorId); 
                break;
            }
     
            //
            // Rule 84:  TraditionalVariableDeclaratorId ::= identifier
            //
            case 84: {
                //#line 592 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 594 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new X10VarDeclarator(pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 85:  TraditionalVariableDeclaratorId ::= TraditionalVariableDeclaratorId [ ]
            //
            case 85: {
                //#line 597 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator TraditionalVariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                //#line 599 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TraditionalVariableDeclaratorId.dims++;
                TraditionalVariableDeclaratorId.position(pos());
                // setResult(a);
                break;
            }
     
            //
            // Rule 87:  VariableDeclaratorId ::= identifier [ IdentifierList ]
            //
            case 87: {
                //#line 606 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 606 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List IdentifierList = (List) getRhsSym(3);
                //#line 608 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new X10VarDeclarator(pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier()), IdentifierList));
                break;
            }
     
            //
            // Rule 88:  VariableDeclaratorId ::= [ IdentifierList ]
            //
            case 88: {
                //#line 611 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List IdentifierList = (List) getRhsSym(2);
                //#line 613 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new X10VarDeclarator(pos(), IdentifierList));
                break;
            }
     
            //
            // Rule 92:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 92: {
                //#line 621 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags FieldModifiers = (Flags) getRhsSym(1);
                //#line 621 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags FieldModifier = (Flags) getRhsSym(2);
                //#line 623 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(FieldModifiers.set(FieldModifier));
                break;
            }
     
            //
            // Rule 93:  FieldModifier ::= public
            //
            case 93: {
                
                //#line 631 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 94:  FieldModifier ::= protected
            //
            case 94: {
                
                //#line 636 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 95:  FieldModifier ::= private
            //
            case 95: {
                
                //#line 641 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 96:  FieldModifier ::= static
            //
            case 96: {
                
                //#line 646 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 97:  FieldModifier ::= final
            //
            case 97: {
                
                //#line 651 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 98:  FieldModifier ::= transient
            //
            case 98: {
                
                //#line 656 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 100:  ResultType ::= void
            //
            case 100: {
                
                //#line 673 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 101:  FormalParameterList ::= LastFormalParameter
            //
            case 101: {
                //#line 693 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Formal LastFormalParameter = (Formal) getRhsSym(1);
                //#line 695 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(LastFormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 102:  FormalParameterList ::= FormalParameters , LastFormalParameter
            //
            case 102: {
                //#line 700 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List FormalParameters = (List) getRhsSym(1);
                //#line 700 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Formal LastFormalParameter = (Formal) getRhsSym(3);
                //#line 702 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                FormalParameters.add(LastFormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 103:  FormalParameters ::= FormalParameter
            //
            case 103: {
                //#line 707 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 709 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 104:  FormalParameters ::= FormalParameters , FormalParameter
            //
            case 104: {
                //#line 714 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List FormalParameters = (List) getRhsSym(1);
                //#line 714 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 716 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                FormalParameters.add(FormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 105:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 105: {
                //#line 721 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                //#line 721 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 721 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(3);
                //#line 723 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
        if (VariableDeclaratorId != null)
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names()));
            else
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), 1), nf.Id(pos(), ""), new AmbExpr[0]));
                break;
            }
     
            //
            // Rule 107:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 107: {
                //#line 731 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags VariableModifiers = (Flags) getRhsSym(1);
                //#line 731 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags VariableModifier = (Flags) getRhsSym(2);
                //#line 733 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(VariableModifiers.set(VariableModifier));
                break;
            }
     
            //
            // Rule 108:  VariableModifier ::= final
            //
            case 108: {
                
                //#line 739 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 109:  LastFormalParameter ::= VariableModifiersopt Type ...opt$opt VariableDeclaratorId
            //
            case 109: {
                //#line 745 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                //#line 745 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 745 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Object opt = (Object) getRhsSym(3);
                //#line 745 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(4);
                //#line 747 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                assert(opt == null);
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names()));
                break;
            }
     
            //
            // Rule 111:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 111: {
                //#line 759 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags MethodModifiers = (Flags) getRhsSym(1);
                //#line 759 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags MethodModifier = (Flags) getRhsSym(2);
                //#line 761 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(MethodModifiers.set(MethodModifier));
                break;
            }
     
            //
            // Rule 112:  MethodModifier ::= public
            //
            case 112: {
                
                //#line 769 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 113:  MethodModifier ::= protected
            //
            case 113: {
                
                //#line 774 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 114:  MethodModifier ::= private
            //
            case 114: {
                
                //#line 779 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 115:  MethodModifier ::= abstract
            //
            case 115: {
                
                //#line 784 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 116:  MethodModifier ::= static
            //
            case 116: {
                
                //#line 789 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 117:  MethodModifier ::= final
            //
            case 117: {
                
                //#line 794 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 118:  MethodModifier ::= native
            //
            case 118: {
                
                //#line 804 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 119:  MethodModifier ::= strictfp
            //
            case 119: {
                
                //#line 809 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 120:  Throws ::= throws ExceptionTypeList
            //
            case 120: {
                //#line 813 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 815 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 121:  ExceptionTypeList ::= ExceptionType
            //
            case 121: {
                //#line 819 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 821 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                break;
            }
     
            //
            // Rule 122:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 122: {
                //#line 826 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 826 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 828 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ExceptionTypeList.add(ExceptionType);
                // setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 125:  MethodBody ::= ;
            //
            case 125:
                setResult(null);
                break;
 
            //
            // Rule 127:  StaticInitializer ::= static Block
            //
            case 127: {
                //#line 848 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 850 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 128:  SimpleTypeName ::= identifier
            //
            case 128: {
                //#line 865 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 867 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 130:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 130: {
                //#line 872 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ConstructorModifiers = (Flags) getRhsSym(1);
                //#line 872 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ConstructorModifier = (Flags) getRhsSym(2);
                //#line 874 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ConstructorModifiers.set(ConstructorModifier));
                break;
            }
     
            //
            // Rule 131:  ConstructorModifier ::= public
            //
            case 131: {
                
                //#line 882 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 132:  ConstructorModifier ::= protected
            //
            case 132: {
                
                //#line 887 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 133:  ConstructorModifier ::= private
            //
            case 133: {
                
                //#line 892 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 134:  ConstructorBody ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 134: {
                //#line 896 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 896 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 898 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
            // Rule 135:  Arguments ::= ( ArgumentListopt )
            //
            case 135: {
                //#line 931 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 933 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ArgumentListopt);
                break;
            }
     
            //
            // Rule 138:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 138: {
                //#line 949 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags InterfaceModifiers = (Flags) getRhsSym(1);
                //#line 949 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags InterfaceModifier = (Flags) getRhsSym(2);
                //#line 951 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(InterfaceModifiers.set(InterfaceModifier));
                break;
            }
     
            //
            // Rule 139:  InterfaceModifier ::= public
            //
            case 139: {
                
                //#line 959 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 140:  InterfaceModifier ::= protected
            //
            case 140: {
                
                //#line 964 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 141:  InterfaceModifier ::= private
            //
            case 141: {
                
                //#line 969 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 142:  InterfaceModifier ::= abstract
            //
            case 142: {
                
                //#line 974 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 143:  InterfaceModifier ::= static
            //
            case 143: {
                
                //#line 979 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 144:  InterfaceModifier ::= strictfp
            //
            case 144: {
                
                //#line 984 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 145:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 145: {
                //#line 988 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(2);
                //#line 990 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 146:  ExtendsInterfaces ::= ExtendsInterfaces , InterfaceType
            //
            case 146: {
                //#line 995 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 995 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                //#line 997 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ExtendsInterfaces.add(InterfaceType);
                // setResult(ExtendsInterfaces);
                break;
            }
     
            //
            // Rule 147:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 147: {
                //#line 1007 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 1009 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                break;
            }
     
            //
            // Rule 149:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 149: {
                //#line 1014 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 1014 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 1016 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                break;
            }
     
            //
            // Rule 151:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 151: {
                //#line 1022 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                MethodDecl AbstractMethodDeclaration = (MethodDecl) getRhsSym(1);
                //#line 1024 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(AbstractMethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 152:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 152: {
                //#line 1029 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 1031 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 153:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 153: {
                //#line 1036 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 1038 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 154:  InterfaceMemberDeclaration ::= ;
            //
            case 154: {
                
                //#line 1045 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 155:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 155: {
                //#line 1049 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ConstantModifiersopt = (Flags) getRhsSym(1);
                //#line 1049 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1049 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 1051 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                for (Iterator i = VariableDeclarators.iterator(); i.hasNext();)
                {
                    X10VarDeclarator d = (X10VarDeclarator) i.next();
                    if (d.hasExplodedVars())
                      // TODO: Report this exception correctly.
                      throw new Error("Field Declarations may not have exploded variables." + pos());
                    l.add(nf.FieldDecl(pos(getRhsFirstTokenIndex(2), getRightSpan()),
                                       ConstantModifiersopt,
                                       nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), d.dims),
                                       d.name,
                                       d.init));
                }
                setResult(l);
                break;
            }
     
            //
            // Rule 157:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 157: {
                //#line 1069 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ConstantModifiers = (Flags) getRhsSym(1);
                //#line 1069 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ConstantModifier = (Flags) getRhsSym(2);
                //#line 1071 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ConstantModifiers.set(ConstantModifier));
                break;
            }
     
            //
            // Rule 158:  ConstantModifier ::= public
            //
            case 158: {
                
                //#line 1079 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 159:  ConstantModifier ::= static
            //
            case 159: {
                
                //#line 1084 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 160:  ConstantModifier ::= final
            //
            case 160: {
                
                //#line 1089 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 162:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 162: {
                //#line 1096 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags AbstractMethodModifiers = (Flags) getRhsSym(1);
                //#line 1096 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags AbstractMethodModifier = (Flags) getRhsSym(2);
                //#line 1098 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(AbstractMethodModifiers.set(AbstractMethodModifier));
                break;
            }
     
            //
            // Rule 163:  AbstractMethodModifier ::= public
            //
            case 163: {
                
                //#line 1106 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 164:  AbstractMethodModifier ::= abstract
            //
            case 164: {
                
                //#line 1111 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 165:  SimpleName ::= identifier
            //
            case 165: {
                //#line 1167 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1169 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 166:  ArrayInitializer ::= { VariableInitializersopt ,opt$opt }
            //
            case 166: {
                //#line 1196 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableInitializersopt = (List) getRhsSym(2);
                //#line 1196 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Object opt = (Object) getRhsSym(3);
                //#line 1198 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                if (VariableInitializersopt == null)
                     setResult(nf.ArrayInit(pos()));
                else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
                break;
            }
     
            //
            // Rule 167:  VariableInitializers ::= VariableInitializer
            //
            case 167: {
                //#line 1204 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 1206 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 168:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 168: {
                //#line 1211 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 1211 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 1213 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                break;
            }
     
            //
            // Rule 169:  Block ::= { BlockStatementsopt }
            //
            case 169: {
                //#line 1232 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 1234 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                break;
            }
     
            //
            // Rule 170:  BlockStatements ::= BlockStatement
            //
            case 170: {
                //#line 1238 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatement = (List) getRhsSym(1);
                //#line 1240 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                break;
            }
     
            //
            // Rule 171:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 171: {
                //#line 1245 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatements = (List) getRhsSym(1);
                //#line 1245 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatement = (List) getRhsSym(2);
                //#line 1247 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                break;
            }
     
            //
            // Rule 173:  BlockStatement ::= ClassDeclaration
            //
            case 173: {
                //#line 1253 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 1255 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                break;
            }
     
            //
            // Rule 174:  BlockStatement ::= Statement
            //
            case 174: {
                //#line 1260 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 1262 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                break;
            }
     
            //
            // Rule 176:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 176: {
                //#line 1270 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                //#line 1270 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1270 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 1272 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                List s = new TypedList(new LinkedList(), Stmt.class, false);
                if (VariableDeclarators != null) {
                    for (Iterator i = VariableDeclarators.iterator(); i.hasNext(); )
                    {
                        X10VarDeclarator d = (X10VarDeclarator) i.next();
                        d.setFlag(VariableModifiersopt); 
                        // use d.flags below and not flags, setFlag may change it.
                        l.add(nf.LocalDecl(d.pos, d.flags,
                                           nf.array(Type, pos(d), d.dims), d.name, d.init));
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
            // Rule 200:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 200: {
                //#line 1333 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1333 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1335 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.If(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 201:  IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
            //
            case 201: {
                //#line 1339 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1339 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1339 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1341 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.If(pos(), Expression, StatementNoShortIf, Statement));
                break;
            }
     
            //
            // Rule 202:  IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf$true_stmt else StatementNoShortIf$false_stmt
            //
            case 202: {
                //#line 1345 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1345 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt true_stmt = (Stmt) getRhsSym(5);
                //#line 1345 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt false_stmt = (Stmt) getRhsSym(7);
                //#line 1347 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.If(pos(), Expression, true_stmt, false_stmt));
                break;
            }
     
            //
            // Rule 203:  EmptyStatement ::= ;
            //
            case 203: {
                
                //#line 1353 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 204:  LabeledStatement ::= identifier : Statement
            //
            case 204: {
                //#line 1357 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1357 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1359 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Labeled(pos(), nf.Id(pos(getLeftSpan()), identifier.getIdentifier()), Statement));
                break;
            }
     
            //
            // Rule 205:  LabeledStatementNoShortIf ::= identifier : StatementNoShortIf
            //
            case 205: {
                //#line 1363 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1363 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(3);
                //#line 1365 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Labeled(pos(), nf.Id(pos(getLeftSpan()), identifier.getIdentifier()), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 206:  ExpressionStatement ::= StatementExpression ;
            //
            case 206: {
                //#line 1368 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1370 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 214:  AssertStatement ::= assert Expression ;
            //
            case 214: {
                //#line 1391 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1393 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Assert(pos(), Expression));
                break;
            }
     
            //
            // Rule 215:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 215: {
                //#line 1396 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1396 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1398 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                break;
            }
     
            //
            // Rule 216:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 216: {
                //#line 1402 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1402 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1404 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                break;
            }
     
            //
            // Rule 217:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 217: {
                //#line 1408 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1408 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1410 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                break;
            }
     
            //
            // Rule 219:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 219: {
                //#line 1416 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1416 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1418 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                break;
            }
     
            //
            // Rule 220:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 220: {
                //#line 1423 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1423 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1425 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                break;
            }
     
            //
            // Rule 221:  SwitchLabels ::= SwitchLabel
            //
            case 221: {
                //#line 1432 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1434 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                break;
            }
     
            //
            // Rule 222:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 222: {
                //#line 1439 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1439 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1441 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                break;
            }
     
            //
            // Rule 223:  SwitchLabel ::= case ConstantExpression :
            //
            case 223: {
                //#line 1446 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1448 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                break;
            }
     
            //
            // Rule 224:  SwitchLabel ::= default :
            //
            case 224: {
                
                //#line 1455 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 225:  WhileStatement ::= while ( Expression ) Statement
            //
            case 225: {
                //#line 1462 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1462 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1464 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.While(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 226:  WhileStatementNoShortIf ::= while ( Expression ) StatementNoShortIf
            //
            case 226: {
                //#line 1468 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1468 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1470 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.While(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 227:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 227: {
                //#line 1474 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1474 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1476 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                break;
            }
     
            //
            // Rule 230:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 230: {
                //#line 1483 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1483 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1483 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1483 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1485 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                break;
            }
     
            //
            // Rule 231:  ForStatementNoShortIf ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) StatementNoShortIf
            //
            case 231: {
                //#line 1489 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1489 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1489 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1489 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(9);
                //#line 1491 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 233:  ForInit ::= LocalVariableDeclaration
            //
            case 233: {
                //#line 1496 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1498 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 235:  StatementExpressionList ::= StatementExpression
            //
            case 235: {
                //#line 1506 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1508 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                break;
            }
     
            //
            // Rule 236:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 236: {
                //#line 1513 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1513 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1515 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                //setResult(StatementExpressionList);
                break;
            }
     
            //
            // Rule 237:  BreakStatement ::= break identifieropt ;
            //
            case 237: {
                //#line 1523 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name identifieropt = (Name) getRhsSym(2);
                //#line 1525 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                if (identifieropt == null)
                     setResult(nf.Break(pos()));
                else setResult(nf.Break(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 238:  ContinueStatement ::= continue identifieropt ;
            //
            case 238: {
                //#line 1531 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name identifieropt = (Name) getRhsSym(2);
                //#line 1533 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                if (identifieropt == null)
                     setResult(nf.Continue(pos()));
                else setResult(nf.Continue(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 239:  ReturnStatement ::= return Expressionopt ;
            //
            case 239: {
                //#line 1539 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1541 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Return(pos(), Expressionopt));
                break;
            }
     
            //
            // Rule 240:  ThrowStatement ::= throw Expression ;
            //
            case 240: {
                //#line 1545 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1547 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Throw(pos(), Expression));
                break;
            }
     
            //
            // Rule 241:  TryStatement ::= try Block Catches
            //
            case 241: {
                //#line 1557 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 1557 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List Catches = (List) getRhsSym(3);
                //#line 1559 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Try(pos(), Block, Catches));
                break;
            }
     
            //
            // Rule 242:  TryStatement ::= try Block Catchesopt Finally
            //
            case 242: {
                //#line 1562 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 1562 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1562 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Finally = (Block) getRhsSym(4);
                //#line 1564 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                break;
            }
     
            //
            // Rule 243:  Catches ::= CatchClause
            //
            case 243: {
                //#line 1568 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1570 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                break;
            }
     
            //
            // Rule 244:  Catches ::= Catches CatchClause
            //
            case 244: {
                //#line 1575 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List Catches = (List) getRhsSym(1);
                //#line 1575 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1577 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                break;
            }
     
            //
            // Rule 245:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 245: {
                //#line 1582 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1582 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(5);
                //#line 1584 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                break;
            }
     
            //
            // Rule 246:  Finally ::= finally Block
            //
            case 246: {
                //#line 1588 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 1590 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 250:  PrimaryNoNewArray ::= Type . class
            //
            case 250: {
                //#line 1608 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1610 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
            // Rule 251:  PrimaryNoNewArray ::= void . class
            //
            case 251: {
                
                //#line 1629 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(getLeftSpan()), ts.Void())));
                break;
            }
     
            //
            // Rule 252:  PrimaryNoNewArray ::= this
            //
            case 252: {
                
                //#line 1635 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 253:  PrimaryNoNewArray ::= ClassName . this
            //
            case 253: {
                //#line 1638 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1640 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 254:  PrimaryNoNewArray ::= ( Expression )
            //
            case 254: {
                //#line 1643 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1645 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.ParExpr(pos(), Expression));
                break;
            }
     
            //
            // Rule 259:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 259: {
                //#line 1653 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                //#line 1655 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 260:  Literal ::= LongLiteral$LongLiteral
            //
            case 260: {
                //#line 1659 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken LongLiteral = (IToken) getRhsIToken(1);
                //#line 1661 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 261:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 261: {
                //#line 1665 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                //#line 1667 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 262:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 262: {
                //#line 1671 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                //#line 1673 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 263:  Literal ::= BooleanLiteral
            //
            case 263: {
                //#line 1677 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 1679 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 264:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 264: {
                //#line 1682 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                //#line 1684 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 265:  Literal ::= StringLiteral$str
            //
            case 265: {
                //#line 1688 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken str = (IToken) getRhsIToken(1);
                //#line 1690 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 266:  Literal ::= null
            //
            case 266: {
                
                //#line 1696 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 267:  BooleanLiteral ::= true$trueLiteral
            //
            case 267: {
                //#line 1700 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 1702 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 268:  BooleanLiteral ::= false$falseLiteral
            //
            case 268: {
                //#line 1705 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 1707 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 269:  ArgumentList ::= Expression
            //
            case 269: {
                //#line 1720 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1722 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                break;
            }
     
            //
            // Rule 270:  ArgumentList ::= ArgumentList , Expression
            //
            case 270: {
                //#line 1727 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ArgumentList = (List) getRhsSym(1);
                //#line 1727 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1729 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ArgumentList.add(Expression);
                //setResult(ArgumentList);
                break;
            }
     
            //
            // Rule 271:  DimExprs ::= DimExpr
            //
            case 271: {
                //#line 1763 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr DimExpr = (Expr) getRhsSym(1);
                //#line 1765 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                setResult(l);
                break;
            }
     
            //
            // Rule 272:  DimExprs ::= DimExprs DimExpr
            //
            case 272: {
                //#line 1770 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List DimExprs = (List) getRhsSym(1);
                //#line 1770 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr DimExpr = (Expr) getRhsSym(2);
                //#line 1772 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                DimExprs.add(DimExpr);
                //setResult(DimExprs);
                break;
            }
     
            //
            // Rule 273:  DimExpr ::= [ Expression ]
            //
            case 273: {
                //#line 1777 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1779 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Expression.position(pos()));
                break;
            }
     
            //
            // Rule 274:  Dims ::= [ ]
            //
            case 274: {
                
                //#line 1785 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Integer(1));
                break;
            }
     
            //
            // Rule 275:  Dims ::= Dims [ ]
            //
            case 275: {
                //#line 1788 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Integer Dims = (Integer) getRhsSym(1);
                //#line 1790 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Integer(Dims.intValue() + 1));
                break;
            }
     
            //
            // Rule 276:  FieldAccess ::= Primary . identifier
            //
            case 276: {
                //#line 1794 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1794 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1796 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 277:  FieldAccess ::= super . identifier
            //
            case 277: {
                //#line 1799 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1801 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 278:  FieldAccess ::= ClassName . super$sup . identifier
            //
            case 278: {
                //#line 1804 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1804 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 1804 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                //#line 1806 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRightSpan()), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 279:  MethodInvocation ::= MethodName ( ArgumentListopt )
            //
            case 279: {
                //#line 1810 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name MethodName = (Name) getRhsSym(1);
                //#line 1810 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 1812 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, ArgumentListopt));
                break;
            }
     
            //
            // Rule 281:  PostfixExpression ::= ExpressionName
            //
            case 281: {
                //#line 1835 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 1837 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 284:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 284: {
                //#line 1843 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 1845 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 285:  PostDecrementExpression ::= PostfixExpression --
            //
            case 285: {
                //#line 1849 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 1851 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 288:  UnaryExpression ::= + UnaryExpression
            //
            case 288: {
                //#line 1857 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1859 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpression));
                break;
            }
     
            //
            // Rule 289:  UnaryExpression ::= - UnaryExpression
            //
            case 289: {
                //#line 1862 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1864 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpression));
                break;
            }
     
            //
            // Rule 291:  PreIncrementExpression ::= ++ UnaryExpression
            //
            case 291: {
                //#line 1869 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1871 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpression));
                break;
            }
     
            //
            // Rule 292:  PreDecrementExpression ::= -- UnaryExpression
            //
            case 292: {
                //#line 1875 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1877 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpression));
                break;
            }
     
            //
            // Rule 294:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 294: {
                //#line 1882 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1884 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 295:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 295: {
                //#line 1887 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1889 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 298:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 298: {
                //#line 1901 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1901 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 1903 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                break;
            }
     
            //
            // Rule 299:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 299: {
                //#line 1906 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1906 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 1908 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                break;
            }
     
            //
            // Rule 300:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 300: {
                //#line 1911 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1911 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 1913 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                break;
            }
     
            //
            // Rule 302:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 302: {
                //#line 1918 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 1918 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 1920 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 303:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 303: {
                //#line 1923 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 1923 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 1925 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 305:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 305: {
                //#line 1930 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 1930 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 1932 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                break;
            }
     
            //
            // Rule 306:  ShiftExpression ::= ShiftExpression > > AdditiveExpression
            //
            case 306: {
                //#line 1935 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 1935 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(4);
                //#line 1937 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 307:  ShiftExpression ::= ShiftExpression > > > AdditiveExpression
            //
            case 307: {
                //#line 1941 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 1941 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(5);
                //#line 1943 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 309:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 309: {
                //#line 1949 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1949 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 1951 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
                break;
            }
     
            //
            // Rule 310:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 310: {
                //#line 1954 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1954 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 1956 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
                break;
            }
     
            //
            // Rule 311:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 311: {
                //#line 1959 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1959 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 1961 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
                break;
            }
     
            //
            // Rule 312:  RelationalExpression ::= RelationalExpression > = ShiftExpression
            //
            case 312: {
                //#line 1964 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1964 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(4);
                //#line 1966 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
                break;
            }
     
            //
            // Rule 314:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 314: {
                //#line 1980 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 1980 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 1982 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                break;
            }
     
            //
            // Rule 315:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 315: {
                //#line 1985 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 1985 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 1987 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                break;
            }
     
            //
            // Rule 317:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 317: {
                //#line 1992 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 1992 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 1994 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                break;
            }
     
            //
            // Rule 319:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 319: {
                //#line 1999 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1999 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 2001 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                break;
            }
     
            //
            // Rule 321:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 321: {
                //#line 2006 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 2006 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 2008 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 323:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 323: {
                //#line 2013 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 2013 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 2015 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                break;
            }
     
            //
            // Rule 325:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 325: {
                //#line 2020 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 2020 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 2022 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                break;
            }
     
            //
            // Rule 327:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 327: {
                //#line 2027 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 2027 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2027 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 2029 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 330:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 330: {
                //#line 2036 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 2036 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 2036 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 2038 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 331:  LeftHandSide ::= ExpressionName
            //
            case 331: {
                //#line 2042 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 2044 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 334:  AssignmentOperator ::= =
            //
            case 334: {
                
                //#line 2052 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 335:  AssignmentOperator ::= *=
            //
            case 335: {
                
                //#line 2057 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 336:  AssignmentOperator ::= /=
            //
            case 336: {
                
                //#line 2062 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 337:  AssignmentOperator ::= %=
            //
            case 337: {
                
                //#line 2067 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 338:  AssignmentOperator ::= +=
            //
            case 338: {
                
                //#line 2072 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 339:  AssignmentOperator ::= -=
            //
            case 339: {
                
                //#line 2077 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 340:  AssignmentOperator ::= <<=
            //
            case 340: {
                
                //#line 2082 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 341:  AssignmentOperator ::= > > =
            //
            case 341: {
                
                //#line 2087 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 342:  AssignmentOperator ::= > > > =
            //
            case 342: {
                
                //#line 2093 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 343:  AssignmentOperator ::= &=
            //
            case 343: {
                
                //#line 2099 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 344:  AssignmentOperator ::= ^=
            //
            case 344: {
                
                //#line 2104 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 345:  AssignmentOperator ::= |=
            //
            case 345: {
                
                //#line 2109 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 348:  Dimsopt ::= $Empty
            //
            case 348: {
                
                //#line 2122 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Integer(0));
                break;
            }
     
            //
            // Rule 350:  Catchesopt ::= $Empty
            //
            case 350: {
                
                //#line 2129 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 352:  identifieropt ::= $Empty
            //
            case 352:
                setResult(null);
                break;
 
            //
            // Rule 353:  identifieropt ::= identifier
            //
            case 353: {
                //#line 2136 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 2138 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 354:  ForUpdateopt ::= $Empty
            //
            case 354: {
                
                //#line 2144 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 356:  Expressionopt ::= $Empty
            //
            case 356:
                setResult(null);
                break;
 
            //
            // Rule 358:  ForInitopt ::= $Empty
            //
            case 358: {
                
                //#line 2155 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 360:  SwitchLabelsopt ::= $Empty
            //
            case 360: {
                
                //#line 2162 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 362:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 362: {
                
                //#line 2169 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 364:  VariableModifiersopt ::= $Empty
            //
            case 364: {
                
                //#line 2176 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 366:  VariableInitializersopt ::= $Empty
            //
            case 366:
                setResult(null);
                break;
 
            //
            // Rule 368:  AbstractMethodModifiersopt ::= $Empty
            //
            case 368: {
                
                //#line 2206 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 370:  ConstantModifiersopt ::= $Empty
            //
            case 370: {
                
                //#line 2213 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 372:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 372: {
                
                //#line 2220 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 374:  ExtendsInterfacesopt ::= $Empty
            //
            case 374: {
                
                //#line 2227 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 376:  InterfaceModifiersopt ::= $Empty
            //
            case 376: {
                
                //#line 2234 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 378:  ClassBodyopt ::= $Empty
            //
            case 378:
                setResult(null);
                break;
 
            //
            // Rule 380:  Argumentsopt ::= $Empty
            //
            case 380:
                setResult(null);
                break;
 
            //
            // Rule 381:  Argumentsopt ::= Arguments
            //
            case 381:
                throw new Error("No action specified for rule " + 381);
 
            //
            // Rule 382:  ,opt ::= $Empty
            //
            case 382:
                setResult(null);
                break;
 
            //
            // Rule 384:  ArgumentListopt ::= $Empty
            //
            case 384: {
                
                //#line 2264 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 386:  BlockStatementsopt ::= $Empty
            //
            case 386: {
                
                //#line 2271 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 388:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 388:
                setResult(null);
                break;
 
            //
            // Rule 390:  ConstructorModifiersopt ::= $Empty
            //
            case 390: {
                
                //#line 2282 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 392:  ...opt ::= $Empty
            //
            case 392:
                setResult(null);
                break;
 
            //
            // Rule 394:  FormalParameterListopt ::= $Empty
            //
            case 394: {
                
                //#line 2293 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 396:  Throwsopt ::= $Empty
            //
            case 396: {
                
                //#line 2300 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 398:  MethodModifiersopt ::= $Empty
            //
            case 398: {
                
                //#line 2307 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 400:  FieldModifiersopt ::= $Empty
            //
            case 400: {
                
                //#line 2314 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 402:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 402: {
                
                //#line 2321 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 404:  Interfacesopt ::= $Empty
            //
            case 404: {
                
                //#line 2328 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 406:  Superopt ::= $Empty
            //
            case 406: {
                
                //#line 2335 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
               setResult(nf.TypeNodeFromQualifiedName(pos(), "x10.lang.Object"));
                break;
            }
     
            //
            // Rule 408:  ClassModifiersopt ::= $Empty
            //
            case 408: {
                
                //#line 2346 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 410:  TypeDeclarationsopt ::= $Empty
            //
            case 410: {
                
                //#line 2358 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 412:  ImportDeclarationsopt ::= $Empty
            //
            case 412: {
                
                //#line 2365 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 414:  PackageDeclarationopt ::= $Empty
            //
            case 414:
                setResult(null);
                break;
 
            //
            // Rule 416:  ClassType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 416: {
                //#line 742 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 742 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 742 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                //#line 744 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                     setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 417:  InterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 417: {
                //#line 751 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 751 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 751 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                //#line 753 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                 setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 418:  PackageDeclaration ::= package PackageName ;
            //
            case 418: {
                //#line 759 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name PackageName = (Name) getRhsSym(2);
                //#line 761 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(PackageName.toPackage());
                break;
            }
     
            //
            // Rule 419:  NormalClassDeclaration ::= X10ClassModifiersopt class identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 419: {
                //#line 765 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Flags X10ClassModifiersopt = (X10Flags) getRhsSym(1);
                //#line 765 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 765 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                //#line 765 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(5);
                //#line 765 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(6);
                //#line 765 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(7);
                //#line 767 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
      checkTypeName(identifier);
      List/*<PropertyDecl>*/ props = PropertyListopt == null ? null
                  : (List) PropertyListopt[0];
      Expr ci = PropertyListopt == null ? null : (Expr) PropertyListopt[1];
      setResult(X10Flags.isValue(X10ClassModifiersopt)
         ? nf.ValueClassDecl(pos(),
              X10ClassModifiersopt, nf.Id(identifier.getPosition(), identifier.getIdentifier()), props, ci, Superopt, Interfacesopt, ClassBody)
         : nf.ClassDecl(pos(),
              X10ClassModifiersopt, nf.Id(identifier.getPosition(), identifier.getIdentifier()), props, ci, Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 421:  X10ClassModifiers ::= X10ClassModifiers X10ClassModifier
            //
            case 421: {
                //#line 780 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Flags X10ClassModifiers = (X10Flags) getRhsSym(1);
                //#line 780 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Flags X10ClassModifier = (X10Flags) getRhsSym(2);
                //#line 782 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
       X10Flags result = X10ClassModifiers.setX(X10ClassModifier);
                setResult(result);
               
                break;
            }
     
            //
            // Rule 422:  X10ClassModifier ::= ClassModifier
            //
            case 422: {
                //#line 788 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Flags ClassModifier = (Flags) getRhsSym(1);
                //#line 790 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.toX10Flags(ClassModifier));
                break;
            }
     
            //
            // Rule 423:  X10ClassModifier ::= safe
            //
            case 423: {
                
                //#line 795 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.SAFE);
                break;
            }
     
            //
            // Rule 424:  PropertyList ::= ( Properties WhereClauseopt )
            //
            case 424: {
                //#line 799 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List Properties = (List) getRhsSym(2);
                //#line 799 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(3);
                //#line 801 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
   Object[] result = new Object[2];
   result[0] = Properties;
   result[1] = WhereClauseopt;
   setResult(result);
           break;
            }  
            //
            // Rule 425:  PropertyList ::= ( WhereClause )
            //
            case 425: {
                //#line 806 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClause = (Expr) getRhsSym(2);
                //#line 808 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
   Object[] result = new Object[2];
   result[0] = null;
   result[1] = WhereClause;
   setResult(result);
           break;
            }  
            //
            // Rule 426:  Properties ::= Property
            //
            case 426: {
                //#line 815 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 817 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                break;
            }
     
            //
            // Rule 427:  Properties ::= Properties , Property
            //
            case 427: {
                //#line 822 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List Properties = (List) getRhsSym(1);
                //#line 822 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 824 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Properties.add(Property);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 428:  Property ::= Type identifier
            //
            case 428: {
                //#line 830 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 830 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(2);
                //#line 832 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
    
                setResult(nf.PropertyDecl(pos(), Flags.PUBLIC.Final(), Type,
                nf.Id(identifier.getPosition(), identifier.getIdentifier())));
              
                break;
            }
     
            //
            // Rule 429:  MethodDeclaration ::= ThisClauseopt MethodModifiersopt ResultType MethodDeclarator Throwsopt MethodBody
            //
            case 429: {
                //#line 845 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr ThisClauseopt = (DepParameterExpr) getRhsSym(1);
                //#line 845 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Flags MethodModifiersopt = (Flags) getRhsSym(2);
                //#line 845 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 845 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object[] MethodDeclarator = (Object[]) getRhsSym(4);
                //#line 845 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 845 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 847 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
      Name c = (MethodDeclarator != null) ? (Name) MethodDeclarator[0] : null;
      List d = (MethodDeclarator != null) ? (List) MethodDeclarator[1] : null;
      Integer e = (MethodDeclarator != null) ? (Integer) MethodDeclarator[2] : null;
      Expr where = (MethodDeclarator != null) ? (Expr) MethodDeclarator[3] : null;
      if (ResultType.type() == ts.Void() && e != null && e.intValue() > 0)
         {
           // TODO: error!!!
           System.err.println("Fix me - encountered method returning void but with non-zero rank?");
         }

       setResult(nf.MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(4)),
          ThisClauseopt,
          MethodModifiersopt,
          nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3)), e != null ? e.intValue() : 1),
          c != null ? c.name : nf.Id(pos(), ""),
          d,
          where,
          Throwsopt,
          MethodBody));
                break;
            }
     
            //
            // Rule 430:  ExplicitConstructorInvocation ::= this ( ArgumentListopt ) ;
            //
            case 430: {
                //#line 869 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 871 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ThisCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 431:  ExplicitConstructorInvocation ::= super ( ArgumentListopt ) ;
            //
            case 431: {
                //#line 874 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 876 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.SuperCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 432:  ExplicitConstructorInvocation ::= Primary . this ( ArgumentListopt ) ;
            //
            case 432: {
                //#line 879 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 879 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 881 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ThisCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 433:  ExplicitConstructorInvocation ::= Primary . super ( ArgumentListopt ) ;
            //
            case 433: {
                //#line 884 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 884 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 886 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.SuperCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 434:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier PropertyListopt ExtendsInterfacesopt InterfaceBody
            //
            case 434: {
                //#line 890 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Flags InterfaceModifiersopt = (Flags) getRhsSym(1);
                //#line 890 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 890 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                //#line 890 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(5);
                //#line 890 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(6);
                //#line 892 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
      checkTypeName(identifier);
      List/*<PropertyDecl>*/ props = PropertyListopt == null ? null 
                  : (List) PropertyListopt[0];
      Expr ci = PropertyListopt == null ? null : (Expr) PropertyListopt[1];
      setResult(nf.ClassDecl(pos(),
                   InterfaceModifiersopt.Interface(),
                   nf.Id(identifier.getPosition(), identifier.getIdentifier()),
                   props,
                   ci,
                   null,
                   ExtendsInterfacesopt,
                   InterfaceBody));
                break;
            }
     
            //
            // Rule 435:  AbstractMethodDeclaration ::= ThisClauseopt AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt ;
            //
            case 435: {
                //#line 907 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr ThisClauseopt = (DepParameterExpr) getRhsSym(1);
                //#line 907 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Flags AbstractMethodModifiersopt = (Flags) getRhsSym(2);
                //#line 907 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 907 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object[] MethodDeclarator = (Object[]) getRhsSym(4);
                //#line 907 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 909 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
     Name c = (Name) MethodDeclarator[0];
     List d = (List) MethodDeclarator[1];
     Integer e = (Integer) MethodDeclarator[2];
     Expr where = (Expr) MethodDeclarator[3];
     
     if (ResultType.type() == ts.Void() && e.intValue() > 0)
        {
          // TODO: error!!!
          assert(false);
        }

     setResult(nf.MethodDecl(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(4)),
                ThisClauseopt,
                AbstractMethodModifiersopt ,
                nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3)), e.intValue()),
                nf.Id(c.pos, c.toString()),
                d,
                where,
                Throwsopt,
                null));
                break;
            }
     
            //
            // Rule 436:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
            //
            case 436: {
                //#line 932 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassOrInterfaceType = (TypeNode) getRhsSym(2);
                //#line 932 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 932 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(6);
                //#line 934 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt));
                else setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 437:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 437: {
                //#line 939 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 939 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                //#line 939 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 939 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(8);
                //#line 941 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name b = new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), Primary, b.toType(), ArgumentListopt));
                else setResult(nf.New(pos(), Primary, b.toType(), ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 438:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 438: {
                //#line 947 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 947 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                //#line 947 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 947 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(8);
                //#line 949 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name b = new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt));
                else setResult(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 439:  MethodInvocation ::= Primary . identifier ( ArgumentListopt )
            //
            case 439: {
                //#line 956 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 956 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 956 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 958 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Call(pos(), Primary, nf.Id(identifier.getPosition(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 440:  MethodInvocation ::= super . identifier ( ArgumentListopt )
            //
            case 440: {
                //#line 961 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 961 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 963 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(identifier.getPosition(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 441:  MethodInvocation ::= ClassName . super$sup . identifier ( ArgumentListopt )
            //
            case 441: {
                //#line 966 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 966 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 966 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                //#line 966 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 968 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(identifier.getPosition(), identifier.getIdentifier()), ArgumentListopt));
                break;
            }
     
            //
            // Rule 442:  AssignPropertyCall ::= property ( ArgumentList )
            //
            case 442: {
                //#line 973 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 975 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.AssignPropertyCall(pos(),  ArgumentList));
                break;
            }
     
            //
            // Rule 445:  SpecialType ::= nullable < Type > DepParametersopt
            //
            case 445: {
                //#line 987 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 987 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(5);
                //#line 989 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10TypeNode t = nf.Nullable(pos(), Type);
                setResult(DepParametersopt == null ? t 
                : t.dep(null, DepParametersopt));
      
                break;
            }
     
            //
            // Rule 446:  SpecialType ::= future < Type >
            //
            case 446: {
                //#line 995 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 997 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Future(pos(), Type));
                break;
            }
     
            //
            // Rule 449:  PrimitiveType ::= NumericType DepParametersopt
            //
            case 449: {
                //#line 1010 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode NumericType = (TypeNode) getRhsSym(1);
                //#line 1010 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 1012 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
              //  System.out.println("Parser: parsed PrimitiveType |" + NumericType + "| |" + DepParametersopt +"|");
                setResult(DepParametersopt == null
                               ? NumericType
                               : ((X10TypeNode) NumericType).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 450:  PrimitiveType ::= boolean DepParametersopt
            //
            case 450: {
                //#line 1018 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 1020 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10TypeNode res = (X10TypeNode) nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(DepParametersopt==null 
                           ? res 
                           : res.dep(null, DepParametersopt));
               break;
            }
     
            //
            // Rule 455:  ClassOrInterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 455: {
                //#line 1032 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 1032 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 1032 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                //#line 1034 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
            X10TypeNode type;
            
            if (ts.isPrimitiveTypeName(TypeName.name.id())) {
                try {
                    type= (X10TypeNode) nf.CanonicalTypeNode(pos(), ts.primitiveForName(TypeName.name.id()));
                } catch (SemanticException e) {
                    throw new InternalCompilerError("Unable to create primitive type for '" + TypeName.name.id() + "'!");
                }
            } else
                type= (X10TypeNode) TypeName.toType();
            //  System.out.println("Parser: parsed ClassOrInterfaceType |" + TypeName + "| |" + DepParametersopt +"|");
                setResult(DepParametersopt == null
                               ? type
                               : type.dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 456:  DepParameters ::= ( DepParameterExpr )
            //
            case 456: {
                //#line 1051 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(2);
                //#line 1053 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(DepParameterExpr);
                break;
            }
     
            //
            // Rule 457:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 457: {
                //#line 1057 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 1057 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(2);
                //#line 1059 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.DepParameterExpr(pos(), ArgumentList, WhereClauseopt));
                break;
            }
     
            //
            // Rule 458:  DepParameterExpr ::= WhereClause
            //
            case 458: {
                //#line 1062 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClause = (Expr) getRhsSym(1);
                //#line 1064 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.DepParameterExpr(pos(), Collections.EMPTY_LIST, WhereClause));
                break;
            }
     
            //
            // Rule 459:  WhereClause ::= : ConstExpression
            //
            case 459: {
                //#line 1068 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExpression = (Expr) getRhsSym(2);
                //#line 1070 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstExpression);
                break;
            }
     
            //
            // Rule 460:  ConstPrimary ::= Literal
            //
            case 460: {
                //#line 1075 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.ast.Lit Literal = (polyglot.ast.Lit) getRhsSym(1);
                //#line 1077 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(Literal);
                break;
            }
     
            //
            // Rule 461:  ConstPrimary ::= Type . class
            //
            case 461: {
                //#line 1080 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1082 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
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
            // Rule 462:  ConstPrimary ::= void . class
            //
            case 462: {
                
                //#line 1101 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(getLeftSpan()), ts.Void())));
                break;
            }
     
            //
            // Rule 463:  ConstPrimary ::= this
            //
            case 463: {
                
                //#line 1107 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 464:  ConstPrimary ::= here
            //
            case 464: {
                
                //#line 1112 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Here(pos()));
                break;
            }
     
            //
            // Rule 465:  ConstPrimary ::= ClassName . this
            //
            case 465: {
                //#line 1115 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1117 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 466:  ConstPrimary ::= ( ConstExpression )
            //
            case 466: {
                //#line 1120 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExpression = (Expr) getRhsSym(2);
                //#line 1122 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstExpression);
                break;
            }
     
            //
            // Rule 468:  ConstPrimary ::= self
            //
            case 468: {
                
                //#line 1128 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Self(pos()));
                break;
            }
     
            //
            // Rule 469:  ConstPostfixExpression ::= ConstPrimary
            //
            case 469: {
                //#line 1134 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstPrimary = (Expr) getRhsSym(1);
                //#line 1136 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstPrimary);
                        break;
            }
            
            //
            // Rule 470:  ConstPostfixExpression ::= ExpressionName
            //
            case 470: {
                //#line 1139 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 1141 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 471:  ConstUnaryExpression ::= ConstPostfixExpression
            //
            case 471: {
                //#line 1144 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstPostfixExpression = (Expr) getRhsSym(1);
                //#line 1146 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstPostfixExpression);
                        break;
            }
            
            //
            // Rule 472:  ConstUnaryExpression ::= + ConstUnaryExpression
            //
            case 472: {
                //#line 1149 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(2);
                //#line 1151 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Unary(pos(), Unary.POS, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 473:  ConstUnaryExpression ::= - ConstUnaryExpression
            //
            case 473: {
                //#line 1154 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(2);
                //#line 1156 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Unary(pos(), Unary.NEG, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 474:  ConstUnaryExpression ::= ! ConstUnaryExpression
            //
            case 474: {
                //#line 1159 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(2);
                //#line 1161 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Unary(pos(), Unary.NOT, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 475:  ConstMultiplicativeExpression ::= ConstUnaryExpression
            //
            case 475: {
                //#line 1165 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(1);
                //#line 1167 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstUnaryExpression);
                break;
            }
     
            //
            // Rule 476:  ConstMultiplicativeExpression ::= ConstMultiplicativeExpression * ConstUnaryExpression
            //
            case 476: {
                //#line 1170 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1170 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(3);
                //#line 1172 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstMultiplicativeExpression, Binary.MUL, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 477:  ConstMultiplicativeExpression ::= ConstMultiplicativeExpression / ConstUnaryExpression
            //
            case 477: {
                //#line 1175 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1175 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(3);
                //#line 1177 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstMultiplicativeExpression, Binary.DIV, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 478:  ConstMultiplicativeExpression ::= ConstMultiplicativeExpression % ConstUnaryExpression
            //
            case 478: {
                //#line 1180 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1180 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(3);
                //#line 1182 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstMultiplicativeExpression, Binary.MOD, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 479:  ConstAdditiveExpression ::= ConstMultiplicativeExpression
            //
            case 479: {
                //#line 1186 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1188 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstMultiplicativeExpression);
                break;
            }
     
            //
            // Rule 480:  ConstAdditiveExpression ::= ConstAdditiveExpression + ConstMultiplicativeExpression
            //
            case 480: {
                //#line 1191 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(1);
                //#line 1191 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 1193 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstAdditiveExpression, Binary.ADD, ConstMultiplicativeExpression));
                break;
            }
     
            //
            // Rule 481:  ConstAdditiveExpression ::= ConstAdditiveExpression - ConstMultiplicativeExpression
            //
            case 481: {
                //#line 1196 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(1);
                //#line 1196 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 1198 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstAdditiveExpression, Binary.SUB, ConstMultiplicativeExpression));
                break;
            }
     
            //
            // Rule 482:  ConstRelationalExpression ::= ConstAdditiveExpression
            //
            case 482: {
                //#line 1203 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(1);
                //#line 1205 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstAdditiveExpression);
                break;
            }
     
            //
            // Rule 483:  ConstRelationalExpression ::= ConstRelationalExpression < ConstAdditiveExpression
            //
            case 483: {
                //#line 1208 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(1);
                //#line 1208 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(3);
                //#line 1210 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstRelationalExpression, Binary.LT, ConstAdditiveExpression));
                break;
            }
     
            //
            // Rule 484:  ConstRelationalExpression ::= ConstRelationalExpression > ConstAdditiveExpression
            //
            case 484: {
                //#line 1213 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(1);
                //#line 1213 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(3);
                //#line 1215 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstRelationalExpression, Binary.GT, ConstAdditiveExpression));
                break;
            }
     
            //
            // Rule 485:  ConstRelationalExpression ::= ConstRelationalExpression <= ConstAdditiveExpression
            //
            case 485: {
                //#line 1218 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(1);
                //#line 1218 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(3);
                //#line 1220 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstRelationalExpression, Binary.LE, ConstAdditiveExpression));
                break;
            }
     
            //
            // Rule 486:  ConstRelationalExpression ::= ConstRelationalExpression > = ConstAdditiveExpression
            //
            case 486: {
                //#line 1223 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(1);
                //#line 1223 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(4);
                //#line 1225 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstRelationalExpression, Binary.GE, ConstAdditiveExpression));
                break;
            }
     
            //
            // Rule 487:  ConstEqualityExpression ::= ConstRelationalExpression
            //
            case 487: {
                //#line 1229 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(1);
                //#line 1231 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstRelationalExpression);
                break;
            }
     
            //
            // Rule 488:  ConstEqualityExpression ::= ConstEqualityExpression == ConstRelationalExpression
            //
            case 488: {
                //#line 1234 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstEqualityExpression = (Expr) getRhsSym(1);
                //#line 1234 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(3);
                //#line 1236 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstEqualityExpression, Binary.EQ, ConstRelationalExpression));
                break;
            }
     
            //
            // Rule 489:  ConstEqualityExpression ::= ConstEqualityExpression != ConstRelationalExpression
            //
            case 489: {
                //#line 1239 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstEqualityExpression = (Expr) getRhsSym(1);
                //#line 1239 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(3);
                //#line 1241 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstEqualityExpression, Binary.NE, ConstRelationalExpression));
                break;
            }
     
            //
            // Rule 490:  ConstAndExpression ::= ConstEqualityExpression
            //
            case 490: {
                //#line 1245 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstEqualityExpression = (Expr) getRhsSym(1);
                //#line 1247 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstEqualityExpression);
                break;
            }
     
            //
            // Rule 491:  ConstAndExpression ::= ConstAndExpression && ConstEqualityExpression
            //
            case 491: {
                //#line 1250 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAndExpression = (Expr) getRhsSym(1);
                //#line 1250 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstEqualityExpression = (Expr) getRhsSym(3);
                //#line 1252 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstAndExpression, Binary.COND_AND, ConstEqualityExpression));
                break;
            }
     
            //
            // Rule 492:  ConstExclusiveOrExpression ::= ConstAndExpression
            //
            case 492: {
                //#line 1256 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAndExpression = (Expr) getRhsSym(1);
                //#line 1258 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstAndExpression);
                break;
            }
     
            //
            // Rule 493:  ConstExclusiveOrExpression ::= ConstExclusiveOrExpression ^ ConstAndExpression
            //
            case 493: {
                //#line 1261 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1261 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAndExpression = (Expr) getRhsSym(3);
                //#line 1263 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstExclusiveOrExpression, Binary.BIT_XOR, ConstAndExpression));
                break;
            }
     
            //
            // Rule 494:  ConstInclusiveOrExpression ::= ConstExclusiveOrExpression
            //
            case 494: {
                //#line 1267 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1269 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstExclusiveOrExpression);
                break;
            }
     
            //
            // Rule 495:  ConstInclusiveOrExpression ::= ConstInclusiveOrExpression || ConstExclusiveOrExpression
            //
            case 495: {
                //#line 1272 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstInclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1272 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 1274 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstInclusiveOrExpression, Binary.COND_OR, ConstExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 496:  ConstExpression ::= ConstInclusiveOrExpression
            //
            case 496: {
                //#line 1278 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstInclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1280 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstInclusiveOrExpression);
                break;
            }
     
            //
            // Rule 497:  ConstExpression ::= ConstInclusiveOrExpression ? ConstExpression$first : ConstExpression
            //
            case 497: {
                //#line 1283 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstInclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1283 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr first = (Expr) getRhsSym(3);
                //#line 1283 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExpression = (Expr) getRhsSym(5);
                //#line 1285 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Conditional(pos(), ConstInclusiveOrExpression, first, ConstExpression));
                break;
            }
     
            //
            // Rule 498:  ConstFieldAccess ::= ConstPrimary . identifier
            //
            case 498: {
                //#line 1290 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr ConstPrimary = (Expr) getRhsSym(1);
                //#line 1290 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1292 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Field(pos(), ConstPrimary, nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 499:  ConstFieldAccess ::= super . identifier
            //
            case 499: {
                //#line 1295 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1297 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 500:  ConstFieldAccess ::= ClassName . super$sup . identifier
            //
            case 500: {
                //#line 1300 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1300 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 1300 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                //#line 1302 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 502:  X10ArrayType ::= Type [ . ]
            //
            case 502: {
                //#line 1318 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1320 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, null));
                break;
            }
     
            //
            // Rule 503:  X10ArrayType ::= Type value [ . ]
            //
            case 503: {
                //#line 1323 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1325 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, null));
                break;
            }
     
            //
            // Rule 504:  X10ArrayType ::= Type [ DepParameterExpr ]
            //
            case 504: {
                //#line 1328 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1328 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(3);
                //#line 1330 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
                break;
            }
     
            //
            // Rule 505:  X10ArrayType ::= Type value [ DepParameterExpr ]
            //
            case 505: {
                //#line 1333 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1333 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(4);
                //#line 1335 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, DepParameterExpr));
                break;
            }
     
            //
            // Rule 506:  ObjectKind ::= value
            //
            case 506:
                throw new Error("No action specified for rule " + 506);
 
            //
            // Rule 507:  ObjectKind ::= reference
            //
            case 507:
                throw new Error("No action specified for rule " + 507);
 
            //
            // Rule 508:  MethodModifier ::= atomic
            //
            case 508: {
                
                //#line 1349 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.ATOMIC);
                break;
            }
     
            //
            // Rule 509:  MethodModifier ::= extern
            //
            case 509: {
                
                //#line 1354 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 510:  MethodModifier ::= safe
            //
            case 510: {
                
                //#line 1359 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.SAFE);
                break;
            }
     
            //
            // Rule 511:  MethodModifier ::= sequential
            //
            case 511: {
                
                //#line 1364 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.SEQUENTIAL);
                break;
            }
     
            //
            // Rule 512:  MethodModifier ::= local
            //
            case 512: {
                
                //#line 1369 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.LOCAL);
                break;
            }
     
            //
            // Rule 513:  MethodModifier ::= nonblocking
            //
            case 513: {
                
                //#line 1374 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.NON_BLOCKING);
                break;
            }
     
            //
            // Rule 515:  ValueClassDeclaration ::= X10ClassModifiersopt value identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 515: {
                //#line 1380 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Flags X10ClassModifiersopt = (X10Flags) getRhsSym(1);
                //#line 1380 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1380 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                //#line 1380 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(5);
                //#line 1380 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(6);
                //#line 1380 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(7);
                //#line 1382 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
    checkTypeName(identifier);
    List/*<PropertyDecl>*/ props = PropertyListopt==null ? null : (List) PropertyListopt[0];
    Expr ci = PropertyListopt==null ? null : (Expr) PropertyListopt[1];
    setResult(nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
    X10ClassModifiersopt, nf.Id(identifier.getPosition(), identifier.getIdentifier()), 
    props, ci, Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 516:  ValueClassDeclaration ::= X10ClassModifiersopt value class identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 516: {
                //#line 1390 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Flags X10ClassModifiersopt = (X10Flags) getRhsSym(1);
                //#line 1390 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                //#line 1390 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(5);
                //#line 1390 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(6);
                //#line 1390 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1390 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1392 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                checkTypeName(identifier);
    List/*<PropertyDecl>*/ props = PropertyListopt==null ? null : (List) PropertyListopt[0];
    Expr ci = PropertyListopt==null ? null : (Expr) PropertyListopt[1];
    setResult(nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                                X10ClassModifiersopt, nf.Id(identifier.getPosition(), identifier.getIdentifier()), 
                                props, ci, Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 517:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 517: {
                //#line 1401 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Flags ConstructorModifiersopt = (Flags) getRhsSym(1);
                //#line 1401 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object[] ConstructorDeclarator = (Object[]) getRhsSym(2);
                //#line 1401 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(3);
                //#line 1401 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(4);
                //#line 1403 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
     Name a = (Name) ConstructorDeclarator[1];
     DepParameterExpr c = (DepParameterExpr) ConstructorDeclarator[2];
     List b = (List) ConstructorDeclarator[3];
     Expr e = (Expr) ConstructorDeclarator[4];
     
       X10TypeNode resultType = (X10TypeNode) a.toType();        
       if (c != null) 
     resultType = resultType.dep(c);
     setResult(nf.ConstructorDecl(pos(), ConstructorModifiersopt,  nf.Id(a.pos, a.toString()), resultType, b, e, Throwsopt, ConstructorBody));
               break;
            }
    
            //
            // Rule 518:  ConstructorDeclarator ::= SimpleTypeName DepParametersopt ( FormalParameterListopt WhereClauseopt )
            //
            case 518: {
                //#line 1415 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name SimpleTypeName = (Name) getRhsSym(1);
                //#line 1415 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 1415 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 1415 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(5);
                //#line 1417 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
             Object[] a = new Object[5];
             a[1] = SimpleTypeName;
             a[2] = DepParametersopt;
             a[3] = FormalParameterListopt;
             a[4] = WhereClauseopt;
             setResult(a);
               break;
            }
    
            //
            // Rule 519:  ThisClause ::= this DepParameters
            //
            case 519: {
                //#line 1425 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(2);
                //#line 1427 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(DepParameters);
                break;
            }
     
            //
            // Rule 520:  Super ::= extends DataType
            //
            case 520: {
                //#line 1431 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode DataType = (TypeNode) getRhsSym(2);
                //#line 1433 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(DataType);
                break;
            }
     
            //
            // Rule 521:  MethodDeclarator ::= identifier ( FormalParameterListopt WhereClauseopt )
            //
            case 521: {
                //#line 1437 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1437 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1437 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(4);
                //#line 1439 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
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
            // Rule 522:  MethodDeclarator ::= MethodDeclarator [ ]
            //
            case 522: {
                //#line 1449 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object[] MethodDeclarator = (Object[]) getRhsSym(1);
                //#line 1451 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                MethodDeclarator[2] = new Integer(((Integer) MethodDeclarator[2]).intValue() + 1);
                // setResult(MethodDeclarator);
               break;
            }
     
            //
            // Rule 523:  FieldDeclaration ::= ThisClauseopt FieldModifiersopt Type VariableDeclarators ;
            //
            case 523: {
                //#line 1457 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr ThisClauseopt = (DepParameterExpr) getRhsSym(1);
                //#line 1457 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Flags FieldModifiersopt = (Flags) getRhsSym(2);
                //#line 1457 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1457 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(4);
                //#line 1459 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
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
                        d.setFlag(FieldModifiersopt);
                        l.add(nf.FieldDecl(p,
                                           ThisClauseopt,
                                           d.flags,
                                           nf.array(Type, Type.position(), d.dims),
                                           d.name,
                                           d.init));
                    }
                }
                setResult(l);
                break;
            }
     
            //
            // Rule 524:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt Dims ArrayInitializer
            //
            case 524: {
                //#line 1496 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1496 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(3);
                //#line 1496 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Integer Dims = (Integer) getRhsSym(4);
                //#line 1496 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                ArrayInit ArrayInitializer = (ArrayInit) getRhsSym(5);
                //#line 1498 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Dims.intValue(), ArrayInitializer));
                break;
            }
     
            //
            // Rule 525:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr Dims
            //
            case 525: {
                //#line 1502 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1502 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(3);
                //#line 1502 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr DimExpr = (Expr) getRhsSym(4);
                //#line 1502 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Integer Dims = (Integer) getRhsSym(5);
                //#line 1504 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Collections.singletonList(DimExpr), Dims.intValue()));
                break;
            }
     
            //
            // Rule 526:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr DimExprs Dimsopt
            //
            case 526: {
                //#line 1508 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1508 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(3);
                //#line 1508 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr DimExpr = (Expr) getRhsSym(4);
                //#line 1508 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List DimExprs = (List) getRhsSym(5);
                //#line 1508 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Integer Dimsopt = (Integer) getRhsSym(6);
                //#line 1510 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                l.addAll(DimExprs);
                setResult(nf.NewArray(pos(), ArrayBaseType, l, Dimsopt.intValue()));
                break;
            }
     
            //
            // Rule 527:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ]
            //
            case 527: {
                //#line 1517 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1517 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object Valueopt = (Object) getRhsSym(3);
                //#line 1517 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(4);
                //#line 1517 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(6);
                //#line 1519 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, null));
                break;
            }
     
            //
            // Rule 528:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression$distr ] Expression$initializer
            //
            case 528: {
                //#line 1522 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1522 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object Valueopt = (Object) getRhsSym(3);
                //#line 1522 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(4);
                //#line 1522 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr distr = (Expr) getRhsSym(6);
                //#line 1522 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr initializer = (Expr) getRhsSym(8);
                //#line 1524 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, distr, initializer));
                break;
            }
     
            //
            // Rule 529:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ] ($lparen FormalParameter ) MethodBody
            //
            case 529: {
                //#line 1527 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1527 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object Valueopt = (Object) getRhsSym(3);
                //#line 1527 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(4);
                //#line 1527 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(6);
                //#line 1527 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                IToken lparen = (IToken) getRhsIToken(8);
                //#line 1527 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(9);
                //#line 1527 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1529 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                // Expr initializer = makeInitializer( pos(getRhsFirstTokenIndex(8), getRightSpan()), ArrayBaseType, FormalParameter, MethodBody );
                List formals = new TypedList(new ArrayList(1), Formal.class, false);
                formals.add(FormalParameter);
                Closure closure = nf.Closure(MethodBody.position(), formals, ArrayBaseType, new TypedList(new ArrayList(), Type.class, true), MethodBody);
                List args= new TypedList(new ArrayList(), Expr.class, false);
                args.add(nf.Point(pos(), null)); // argument to this particular kind of closure is always an x10.lang.point
                ClosureCall call= nf.ClosureCall(pos(), closure, args);
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, call));
                break;
            }
     
            //
            // Rule 530:  Valueopt ::= $Empty
            //
            case 530:
                setResult(null);
                break;
 
            //
            // Rule 531:  Valueopt ::= value
            //
            case 531: {
                
                //#line 1544 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 535:  ArrayBaseType ::= ( Type )
            //
            case 535: {
                //#line 1552 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1554 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(Type);
                break;
            }
     
            //
            // Rule 536:  ArrayAccess ::= ExpressionName [ ArgumentList ]
            //
            case 536: {
                //#line 1558 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 1558 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 1560 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), ExpressionName.toExpr(), (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), ExpressionName.toExpr(), ArgumentList));
                break;
            }
     
            //
            // Rule 537:  ArrayAccess ::= PrimaryNoNewArray [ ArgumentList ]
            //
            case 537: {
                //#line 1565 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr PrimaryNoNewArray = (Expr) getRhsSym(1);
                //#line 1565 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 1567 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), PrimaryNoNewArray, (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), PrimaryNoNewArray, ArgumentList));
                break;
            }
     
            //
            // Rule 555:  NowStatement ::= now ( Clock ) Statement
            //
            case 555: {
                //#line 1595 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1595 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1597 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
              setResult(nf.Now(pos(), Clock, Statement));
                break;
            }
     
            //
            // Rule 556:  ClockedClause ::= clocked ( ClockList )
            //
            case 556: {
                //#line 1601 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1603 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 557:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 557: {
                //#line 1607 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1607 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1607 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1609 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                break;
            }
     
            //
            // Rule 558:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 558: {
                //#line 1617 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1617 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1619 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
              setResult(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                               ? nf.Here(pos(getLeftSpan()))
                                               : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 559:  WhenStatement ::= when ( Expression ) Statement
            //
            case 559: {
                //#line 1626 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1626 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1628 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.When(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 560:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 560: {
                //#line 1631 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1631 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1631 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1631 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1633 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 561:  ForEachStatement ::= foreach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 561: {
                //#line 1638 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1638 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1638 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1638 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1640 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ForEach(pos(),
                              FormalParameter.flags(FormalParameter.flags().Final()),
                              Expression,
                              ClockedClauseopt,
                              Statement));
                break;
            }
     
            //
            // Rule 562:  AtEachStatement ::= ateach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 562: {
                //#line 1648 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1648 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1648 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1648 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1650 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.AtEach(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             ClockedClauseopt,
                             Statement));
                break;
            }
     
            //
            // Rule 563:  EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
            //
            case 563: {
                //#line 1658 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1658 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1658 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1660 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ForLoop(pos(),
                        FormalParameter.flags(FormalParameter.flags().Final()),
                        Expression,
                        Statement));
                break;
            }
     
            //
            // Rule 564:  FinishStatement ::= finish Statement
            //
            case 564: {
                //#line 1667 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1669 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Finish(pos(),  Statement));
                break;
            }
     
            //
            // Rule 565:  NowStatementNoShortIf ::= now ( Clock ) StatementNoShortIf
            //
            case 565: {
                //#line 1674 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1674 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1676 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Now(pos(), Clock, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 566:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt ClockedClauseopt StatementNoShortIf
            //
            case 566: {
                //#line 1680 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1680 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1680 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(4);
                //#line 1682 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(getLeftSpan()))
                                                : PlaceExpressionSingleListopt),
                                            ClockedClauseopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 567:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 567: {
                //#line 1689 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                //#line 1691 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 568:  WhenStatementNoShortIf ::= when ( Expression ) StatementNoShortIf
            //
            case 568: {
                //#line 1695 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1695 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1697 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.When(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 569:  WhenStatementNoShortIf ::= WhenStatement or$or ( Expression ) StatementNoShortIf
            //
            case 569: {
                //#line 1700 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1700 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1700 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1700 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(6);
                //#line 1702 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, StatementNoShortIf);
                setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 570:  ForEachStatementNoShortIf ::= foreach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 570: {
                //#line 1707 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1707 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1707 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1707 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(8);
                //#line 1709 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ForEach(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             ClockedClauseopt,
                             StatementNoShortIf));

                break;
            }
     
            //
            // Rule 571:  AtEachStatementNoShortIf ::= ateach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 571: {
                //#line 1718 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1718 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1718 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1718 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(8);
                //#line 1720 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.AtEach(pos(),
                            FormalParameter.flags(FormalParameter.flags().Final()),
                            Expression,
                            ClockedClauseopt,
                            StatementNoShortIf));
                break;
            }
     
            //
            // Rule 572:  EnhancedForStatementNoShortIf ::= for ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 572: {
                //#line 1728 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1728 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1728 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(7);
                //#line 1730 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                  setResult(nf.ForLoop(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             StatementNoShortIf));
                break;
            }
     
            //
            // Rule 573:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 573: {
                //#line 1737 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                //#line 1739 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Finish(pos(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 574:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 574: {
                //#line 1744 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1746 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
              setResult(PlaceExpression);
                break;
            }
     
            //
            // Rule 576:  NextStatement ::= next ;
            //
            case 576: {
                
                //#line 1754 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 577:  AwaitStatement ::= await Expression ;
            //
            case 577: {
                //#line 1758 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1760 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Await(pos(), Expression));
                break;
            }
     
            //
            // Rule 578:  ClockList ::= Clock
            //
            case 578: {
                //#line 1764 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1766 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                break;
            }
     
            //
            // Rule 579:  ClockList ::= ClockList , Clock
            //
            case 579: {
                //#line 1771 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1771 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1773 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                ClockList.add(Clock);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 580:  Clock ::= Expression
            //
            case 580: {
                //#line 1779 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1781 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
    setResult(Expression);
                break;
            }
     
            //
            // Rule 581:  CastExpression ::= ( PrimitiveType ) UnaryExpression
            //
            case 581: {
                //#line 1793 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode PrimitiveType = (TypeNode) getRhsSym(2);
                //#line 1793 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(4);
                //#line 1795 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Cast(pos(), PrimitiveType, UnaryExpression));
                break;
            }
     
            //
            // Rule 582:  CastExpression ::= ( SpecialType ) UnaryExpressionNotPlusMinus
            //
            case 582: {
                //#line 1798 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode SpecialType = (TypeNode) getRhsSym(2);
                //#line 1798 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(4);
                //#line 1800 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Cast(pos(), SpecialType, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 583:  CastExpression ::= ( ReferenceType ) UnaryExpressionNotPlusMinus
            //
            case 583: {
                //#line 1803 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode ReferenceType = (TypeNode) getRhsSym(2);
                //#line 1803 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(4);
                //#line 1805 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Cast(pos(), ReferenceType, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 584:  CastExpression ::= ( @ Expression ) UnaryExpressionNotPlusMinus
            //
            case 584: {
                //#line 1808 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1808 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(5);
                //#line 1810 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.PlaceCast(pos(), Expression, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 585:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 585: {
                //#line 1820 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1820 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1822 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                break;
            }
     
            //
            // Rule 586:  IdentifierList ::= identifier
            //
            case 586: {
                //#line 1828 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1830 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List l = new TypedList(new LinkedList(), Name.class, false);
                l.add(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                setResult(l);
                break;
            }
     
            //
            // Rule 587:  IdentifierList ::= IdentifierList , identifier
            //
            case 587: {
                //#line 1835 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 1835 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1837 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                IdentifierList.add(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                setResult(IdentifierList);
                break;
            }
     
            //
            // Rule 588:  Primary ::= here
            //
            case 588: {
                
                //#line 1844 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
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
            // Rule 591:  RegionExpression ::= Expression$expr1 : Expression$expr2
            //
            case 591: {
                //#line 1860 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 1860 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 1862 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
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
            // Rule 592:  RegionExpressionList ::= RegionExpression
            //
            case 592: {
                //#line 1878 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 1880 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                break;
            }
     
            //
            // Rule 593:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 593: {
                //#line 1885 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 1885 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 1887 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                break;
            }
     
            //
            // Rule 594:  Primary ::= [ RegionExpressionList ]
            //
            case 594: {
                //#line 1892 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(2);
                //#line 1894 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Receiver x10LangPointFactory = nf.ReceiverFromQualifiedName(pos(), "x10.lang.point.factory");
                Receiver x10LangRegionFactory = nf.ReceiverFromQualifiedName(pos(), "x10.lang.region.factory");
                Tuple tuple = nf.Tuple(pos(), x10LangPointFactory, x10LangRegionFactory, RegionExpressionList);
                setResult(tuple);
                break;
            }
     
            //
            // Rule 595:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 595: {
                //#line 1901 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 1901 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 1903 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                ConstantDistMaker call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                break;
            }
     
            //
            // Rule 596:  FutureExpression ::= future PlaceExpressionSingleListopt { Expression }
            //
            case 596: {
                //#line 1908 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1908 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1910 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Future(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(getLeftSpan()))
                                                : PlaceExpressionSingleListopt), Expression));
                break;
            }
     
            //
            // Rule 597:  FieldModifier ::= mutable
            //
            case 597: {
                
                //#line 1918 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.MUTABLE);
                break;
            }
     
            //
            // Rule 598:  FieldModifier ::= const
            //
            case 598: {
                
                //#line 1923 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL));
                break;
            }
     
            //
            // Rule 599:  FunExpression ::= fun Type ( FormalParameterListopt ) { Expression }
            //
            case 599:
                throw new Error("No action specified for rule " + 599);
 
            //
            // Rule 600:  MethodInvocation ::= MethodName ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 600:
                throw new Error("No action specified for rule " + 600); 
 
            //
            // Rule 601:  MethodInvocation ::= Primary . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 601:
                throw new Error("No action specified for rule " + 601); 
 
            //
            // Rule 602:  MethodInvocation ::= super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 602:
                throw new Error("No action specified for rule " + 602); 
 
            //
            // Rule 603:  MethodInvocation ::= ClassName . super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 603:
                throw new Error("No action specified for rule " + 603); 
 
            //
            // Rule 604:  MethodInvocation ::= TypeName . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 604:
                throw new Error("No action specified for rule " + 604); 
 
            //
            // Rule 605:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 605:
                throw new Error("No action specified for rule " + 605); 
 
            //
            // Rule 606:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 606:
                throw new Error("No action specified for rule " + 606); 
 
            //
            // Rule 607:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 607:
                throw new Error("No action specified for rule " + 607); 
 
            //
            // Rule 608:  MethodModifier ::= synchronized
            //
            case 608: {
                //#line 1954 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, "\"synchronized\" is an invalid X10 Method Modifier",
                           getErrorPosition(getLeftSpan(), getRightSpan()));
                setResult(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 609:  FieldModifier ::= volatile
            //
            case 609: {
                //#line 1963 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, "\"volatile\" is an invalid X10 Field Modifier",
                           getErrorPosition(getLeftSpan(), getRightSpan()));
                setResult(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 610:  SynchronizedStatement ::= synchronized ( Expression ) Block
            //
            case 610: {
                //#line 1970 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1970 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1972 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, "Synchronized Statement is invalid in X10",
                           getErrorPosition(getLeftSpan(), getRightSpan()));
                setResult(nf.Synchronized(pos(), Expression, Block));
                break;
            }
     
            //
            // Rule 611:  ThisClauseopt ::= $Empty
            //
            case 611:
                setResult(null);
                break;
 
            //
            // Rule 613:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 613:
                setResult(null);
                break;
 
            //
            // Rule 615:  DepParametersopt ::= $Empty
            //
            case 615:
                setResult(null);
                break;
 
            //
            // Rule 617:  PropertyListopt ::= $Empty
            //
            case 617:
                setResult(null);
                break;
 
            //
            // Rule 619:  WhereClauseopt ::= $Empty
            //
            case 619:
                setResult(null);
                break;
 
            //
            // Rule 621:  ObjectKindopt ::= $Empty
            //
            case 621:
                setResult(null);
                break;
 
            //
            // Rule 623:  ArrayInitializeropt ::= $Empty
            //
            case 623:
                setResult(null);
                break;
 
            //
            // Rule 625:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 625:
                setResult(null);
                break;
 
            //
            // Rule 627:  X10ClassModifiersopt ::= $Empty
            //
            case 627: {
                
                //#line 2014 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
         setResult(X10Flags.toX10Flags(Flags.NONE));
                break;
            }  
            //
            // Rule 629:  Unsafeopt ::= $Empty
            //
            case 629:
                setResult(null);
                break;
 
            //
            // Rule 630:  Unsafeopt ::= unsafe
            //
            case 630: {
                
                //#line 2022 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 631:  ParamIdopt ::= $Empty
            //
            case 631:
                setResult(null);
                break;
 
            //
            // Rule 632:  ParamIdopt ::= identifier
            //
            case 632: {
                //#line 2029 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 2031 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(new Name(nf, ts, pos(), nf.Id(identifier.getPosition(), identifier.getIdentifier())));
                break;
            }
     
            //
            // Rule 633:  ClockedClauseopt ::= $Empty
            //
            case 633: {
                
                //#line 2037 "E:/RMF/Eclipse/Workspaces/safari/x10.compiler/src/x10/parser/x10.g"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

