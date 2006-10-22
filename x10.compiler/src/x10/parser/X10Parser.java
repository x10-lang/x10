
//#line 18 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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

import lpg.lpgjavaruntime.*;

//#line 28 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import polyglot.ast.Import;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.SwitchElement;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ext.jl.parse.Name;
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

import lpg.lpgjavaruntime.BacktrackingParser;
import lpg.lpgjavaruntime.BadParseException;
import lpg.lpgjavaruntime.BadParseSymFileException;
import lpg.lpgjavaruntime.DiagnoseParser;
import lpg.lpgjavaruntime.LexStream;
import lpg.lpgjavaruntime.NotBacktrackParseTableException;
import lpg.lpgjavaruntime.NullExportedSymbolsException;
import lpg.lpgjavaruntime.NullTerminalSymbolsException;
import lpg.lpgjavaruntime.ParseTable;
import lpg.lpgjavaruntime.PrsStream;
import lpg.lpgjavaruntime.RuleAction;
import lpg.lpgjavaruntime.UndefinedEofSymbolException;
import lpg.lpgjavaruntime.UnimplementedTerminalsException;

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

    public X10Parser(LexStream lexStream)
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
    private IMessageHandler handler = null;
    public void setMessageHandler(IMessageHandler handler)
    {
        this.handler = handler;
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


    //#line 257 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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
                  rightToken.getEndLine(), rightToken.getEndColumn());
            this.leftIToken = leftToken;
            this.rightIToken = rightToken;
        }

        public IToken getLeftIToken() { return leftIToken; }
        public IToken getRightIToken() { return rightIToken; }

        public String toText()
        {
            PrsStream prsStream = leftIToken.getPrsStream();
            return new String(prsStream.getInputChars(),
                              leftIToken.getStartOffset(),
                              rightIToken.getEndOffset() - leftIToken.getStartOffset() + 1);
        }
    }

    public void reportError(int errorCode, String locationInfo, int leftToken, int rightToken, String tokenText)
    {
        if (errorCode == DELETION_CODE ||
            errorCode == MISPLACED_CODE) tokenText = "";
        if (! tokenText.equals("")) tokenText += ' ';
        eq.enqueue(ErrorInfo.SYNTAX_ERROR, locationInfo + tokenText + errorMsgText[errorCode],
                   new JPGPosition("",
                                   getFileName(),
                                   super.getIToken(leftToken),
                                   super.getIToken(rightToken)));
    }

    public polyglot.ast.Node parse() {
        try
        {
            SourceFile sf = (SourceFile) parser();

            if ((!unrecoverableSyntaxError) && (sf != null))
                return sf.source(source);

            eq.enqueue(ErrorInfo.SYNTAX_ERROR, "Unable to parse " + source.name() + ".");
        }
        catch (RuntimeException e) {
            // Let the Compiler catch and report it.
            throw e;
        }
        catch (Exception e) {
            // Used by cup to indicate a non-recoverable error.
            eq.enqueue(ErrorInfo.SYNTAX_ERROR, e.getMessage());
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
        Name x10 = new Name(nf, ts, pos, "x10");
        Name x10CG = new Name(nf, ts, pos, x10, "compilergenerated");
        Name x10CGP1 = new Name(nf, ts, pos, x10CG, "Parameter1");
        appResultType = x10CGP1.toType();
      }
      // resultType is a.
      List l1 = new TypedList(new LinkedList(), X10Formal.class, false);
      l1.add(f);
      l1.add(nf.Formal(pos, Flags.FINAL, appResultType, "_"));
      MethodDecl decl = nf.MethodDecl(pos, flags, appResultType,
                                    "apply", l1,
                                      new LinkedList(), body);
      //  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
      Name x10 = new Name(nf, ts, pos, "x10");
      Name x10Array = new Name(nf, ts, pos, x10, "array");
      Name tOperator = new Name(nf, ts, pos, x10Array, "Operator");
      Name tOperatorPointwise = new Name(nf, ts, pos, tOperator, "Pointwise");
      List classDecl = new TypedList(new LinkedList(), MethodDecl.class, false);
      classDecl.add( decl );
      TypeNode t = (TypeNode) tOperatorPointwise.toType();

      New initializer = nf.New(pos,
                               t,
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
                                    "apply", l1,
                                      new LinkedList(), body);
      //  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
      // [IP] FIXME: this will break if the result is not a canonical type
      Name tArray = new Name(nf, ts, pos, resultType.toString() + "Array");
      Name tArrayPointwiseOp = new Name(nf, ts, pos, tArray, "pointwiseOp");
      List classDecl = new TypedList(new LinkedList(), MethodDecl.class, false);
      classDecl.add( decl );
      New initializer = nf.New(pos, tArray.toExpr(),
                             tArrayPointwiseOp.toType(),
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

    private polyglot.lex.IntegerLiteral int_lit(int i, int radix)
    {
        long x = parseLong(super.getName(i), radix);
        return new IntegerLiteral(pos(i), (int) x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.IntegerLiteral int_lit(int i)
    {
        long x = parseLong(super.getName(i));
        return new IntegerLiteral(pos(i), (int) x, X10Parsersym.TK_IntegerLiteral);
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
                //#line 6 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Name TypeName = (Name) getRhsSym(1);
                //#line 8 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      TypeName,
                                      "*"));
                    break;
            }
         
            //
            // Rule 2:  PackageName ::= PackageName . ErrorId
            //
            case 2: {
                //#line 16 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Name PackageName = (Name) getRhsSym(1);
                //#line 18 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      PackageName,
                                      "*"));
                    break;
            }
         
            //
            // Rule 3:  ExpressionName ::= AmbiguousName . ErrorId
            //
            case 3: {
                //#line 26 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 28 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      "*"));
                    break;
            }
         
            //
            // Rule 4:  MethodName ::= AmbiguousName . ErrorId
            //
            case 4: {
                //#line 36 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 38 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      "*"));
                    break;
            }
         
            //
            // Rule 5:  PackageOrTypeName ::= PackageOrTypeName . ErrorId
            //
            case 5: {
                //#line 46 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Name PackageOrTypeName = (Name) getRhsSym(1);
                //#line 48 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      PackageOrTypeName,
                                      "*"));
                    break;
            }
         
            //
            // Rule 6:  AmbiguousName ::= AmbiguousName . ErrorId
            //
            case 6: {
                //#line 56 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 58 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                    setResult(new Name(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      "*"));
                   break;
            }
         
            //
            // Rule 7:  FieldAccess ::= Primary . ErrorId
            //
            case 7: {
                //#line 66 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                setResult(nf.Field(pos(), Primary, "*"));
                break;
            }
     
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
                
                //#line 73 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), "*"));
                break;
            }
     
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
                //#line 76 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 76 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 78 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), "*"));
                break;
            }
     
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
                //#line 82 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 82 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 84 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
                //#line 89 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 89 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 91 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
                //#line 95 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 95 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 97 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Name ClassName = (Name) ((Object[]) MethodClassNameSuperPrefix)[0];
                JPGPosition super_pos = (JPGPosition) ((Object[]) MethodClassNameSuperPrefix)[1];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodClassNameSuperPrefix)[2];
                setResult(nf.Call(pos(), nf.Super(super_pos, ClassName.toType()), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 13:  MethodPrimaryPrefix ::= Primary . ErrorId$ErrorId
            //
            case 13: {
                //#line 104 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 104 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 106 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 112 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 114 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                break;
            }
     
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
                //#line 117 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 117 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 117 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 119 "C:/eclipse/ws6/x10.compiler/src/x10/parser/MissingId.gi"
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
                //#line 94 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 96 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 19:  IntegralType ::= byte
            //
            case 19: {
                
                //#line 121 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Byte()));
                break;
            }
     
            //
            // Rule 20:  IntegralType ::= char
            //
            case 20: {
                
                //#line 126 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Char()));
                break;
            }
     
            //
            // Rule 21:  IntegralType ::= short
            //
            case 21: {
                
                //#line 131 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Short()));
                break;
            }
     
            //
            // Rule 22:  IntegralType ::= int
            //
            case 22: {
                
                //#line 136 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Int()));
                break;
            }
     
            //
            // Rule 23:  IntegralType ::= long
            //
            case 23: {
                
                //#line 141 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Long()));
                break;
            }
     
            //
            // Rule 24:  FloatingPointType ::= float
            //
            case 24: {
                
                //#line 147 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Float()));
                break;
            }
     
            //
            // Rule 25:  FloatingPointType ::= double
            //
            case 25: {
                
                //#line 152 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Double()));
                break;
            }
     
            //
            // Rule 28:  TypeName ::= identifier
            //
            case 28: {
                //#line 175 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 177 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 29:  TypeName ::= TypeName . identifier
            //
            case 29: {
                //#line 180 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name TypeName = (Name) getRhsSym(1);
                //#line 180 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 182 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 31:  ArrayType ::= Type [ ]
            //
            case 31: {
                //#line 194 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 196 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.array(Type, pos(), 1));
                break;
            }
     
            //
            // Rule 32:  PackageName ::= identifier
            //
            case 32: {
                //#line 241 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 243 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 33:  PackageName ::= PackageName . identifier
            //
            case 33: {
                //#line 246 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name PackageName = (Name) getRhsSym(1);
                //#line 246 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 248 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 34:  ExpressionName ::= identifier
            //
            case 34: {
                //#line 262 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 264 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 35:  ExpressionName ::= AmbiguousName . identifier
            //
            case 35: {
                //#line 267 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 267 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 269 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 36:  MethodName ::= identifier
            //
            case 36: {
                //#line 277 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 279 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 37:  MethodName ::= AmbiguousName . identifier
            //
            case 37: {
                //#line 282 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 282 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 284 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 38:  PackageOrTypeName ::= identifier
            //
            case 38: {
                //#line 292 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 294 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 39:  PackageOrTypeName ::= PackageOrTypeName . identifier
            //
            case 39: {
                //#line 297 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name PackageOrTypeName = (Name) getRhsSym(1);
                //#line 297 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 299 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 40:  AmbiguousName ::= identifier
            //
            case 40: {
                //#line 307 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 309 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 41:  AmbiguousName ::= AmbiguousName . identifier
            //
            case 41: {
                //#line 312 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 312 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 314 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  identifier.getIdentifier()));
               break;
            }
     
            //
            // Rule 42:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 42: {
                //#line 324 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 324 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 324 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 326 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // Add import x10.lang.* by default.
                Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");
                int token_pos = (ImportDeclarationsopt.size() == 0
                                     ? TypeDeclarationsopt.size() == 0
                                           ? super.getSize() - 1
                                           : getPrevious(getRhsFirstTokenIndex(3))
                                 : getRhsLastTokenIndex(2)
                            );
                Import x10LangImport = 
                nf.Import(pos(token_pos), Import.PACKAGE, x10Lang.toString());
                ImportDeclarationsopt.add(x10LangImport);
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()), PackageDeclarationopt, ImportDeclarationsopt, TypeDeclarationsopt));
                break;
            }
     
            //
            // Rule 43:  ImportDeclarations ::= ImportDeclaration
            //
            case 43: {
                //#line 342 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 344 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 44:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 44: {
                //#line 349 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 349 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 351 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 45:  TypeDeclarations ::= TypeDeclaration
            //
            case 45: {
                //#line 357 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(1);
                //#line 359 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
                //#line 365 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 365 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(2);
                //#line 367 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 49:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 49: {
                //#line 380 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name TypeName = (Name) getRhsSym(2);
                //#line 382 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, TypeName.toString()));
                break;
            }
     
            //
            // Rule 50:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 50: {
                //#line 386 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name PackageOrTypeName = (Name) getRhsSym(2);
                //#line 388 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, PackageOrTypeName.toString()));
                break;
            }
     
            //
            // Rule 53:  TypeDeclaration ::= ;
            //
            case 53: {
                
                //#line 402 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(null);
                break;
            }
     
            //
            // Rule 56:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 56: {
                //#line 414 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ClassModifiers = (Flags) getRhsSym(1);
                //#line 414 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ClassModifier = (Flags) getRhsSym(2);
                //#line 416 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ClassModifiers.set(ClassModifier));
                break;
            }
     
            //
            // Rule 57:  ClassModifier ::= public
            //
            case 57: {
                
                //#line 424 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 58:  ClassModifier ::= protected
            //
            case 58: {
                
                //#line 429 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 59:  ClassModifier ::= private
            //
            case 59: {
                
                //#line 434 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 60:  ClassModifier ::= abstract
            //
            case 60: {
                
                //#line 439 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 61:  ClassModifier ::= static
            //
            case 61: {
                
                //#line 444 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 62:  ClassModifier ::= final
            //
            case 62: {
                
                //#line 449 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 63:  ClassModifier ::= strictfp
            //
            case 63: {
                
                //#line 454 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 64:  Super ::= extends ClassType
            //
            case 64: {
                //#line 466 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 468 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ClassType);
                break;
            }
     
            //
            // Rule 65:  Interfaces ::= implements InterfaceTypeList
            //
            case 65: {
                //#line 477 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 479 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 66:  InterfaceTypeList ::= InterfaceType
            //
            case 66: {
                //#line 483 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(1);
                //#line 485 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 67:  InterfaceTypeList ::= InterfaceTypeList , InterfaceType
            //
            case 67: {
                //#line 490 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 490 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                //#line 492 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                InterfaceTypeList.add(InterfaceType);
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 68:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 68: {
                //#line 502 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 504 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                break;
            }
     
            //
            // Rule 70:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 70: {
                //#line 509 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 509 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 511 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                break;
            }
     
            //
            // Rule 72:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 72: {
                //#line 517 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block InstanceInitializer = (Block) getRhsSym(1);
                //#line 519 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.NONE, InstanceInitializer));
                setResult(l);
                break;
            }
     
            //
            // Rule 73:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 73: {
                //#line 524 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block StaticInitializer = (Block) getRhsSym(1);
                //#line 526 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.STATIC, StaticInitializer));
                setResult(l);
                break;
            }
     
            //
            // Rule 74:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 74: {
                //#line 531 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 533 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 76:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 76: {
                //#line 540 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                MethodDecl MethodDeclaration = (MethodDecl) getRhsSym(1);
                //#line 542 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 77:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 77: {
                //#line 547 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 549 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 78:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 78: {
                //#line 554 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 556 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 79:  ClassMemberDeclaration ::= ;
            //
            case 79: {
                
                //#line 563 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                break;
            }
     
            //
            // Rule 80:  VariableDeclarators ::= VariableDeclarator
            //
            case 80: {
                //#line 571 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(1);
                //#line 573 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), X10VarDeclarator.class, false);
                l.add(VariableDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 81:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 81: {
                //#line 578 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 578 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(3);
                //#line 580 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                break;
            }
     
            //
            // Rule 83:  VariableDeclarator ::= VariableDeclaratorId = VariableInitializer
            //
            case 83: {
                //#line 586 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                //#line 586 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 588 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VariableDeclaratorId.init = VariableInitializer;
                VariableDeclaratorId.position(pos());
                // setResult(VariableDeclaratorId); 
                break;
            }
     
            //
            // Rule 84:  TraditionalVariableDeclaratorId ::= identifier
            //
            case 84: {
                //#line 594 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 596 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new X10VarDeclarator(pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 85:  TraditionalVariableDeclaratorId ::= TraditionalVariableDeclaratorId [ ]
            //
            case 85: {
                //#line 599 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator TraditionalVariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                //#line 601 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TraditionalVariableDeclaratorId.dims++;
                TraditionalVariableDeclaratorId.position(pos());
                // setResult(a);
                break;
            }
     
            //
            // Rule 87:  VariableDeclaratorId ::= identifier [ IdentifierList ]
            //
            case 87: {
                //#line 608 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 608 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List IdentifierList = (List) getRhsSym(3);
                //#line 610 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new X10VarDeclarator(pos(), identifier.getIdentifier(), IdentifierList));
                break;
            }
     
            //
            // Rule 88:  VariableDeclaratorId ::= [ IdentifierList ]
            //
            case 88: {
                //#line 613 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List IdentifierList = (List) getRhsSym(2);
                //#line 615 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new X10VarDeclarator(pos(), IdentifierList));
                break;
            }
     
            //
            // Rule 92:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 92: {
                //#line 623 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags FieldModifiers = (Flags) getRhsSym(1);
                //#line 623 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags FieldModifier = (Flags) getRhsSym(2);
                //#line 625 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(FieldModifiers.set(FieldModifier));
                break;
            }
     
            //
            // Rule 93:  FieldModifier ::= public
            //
            case 93: {
                
                //#line 633 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 94:  FieldModifier ::= protected
            //
            case 94: {
                
                //#line 638 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 95:  FieldModifier ::= private
            //
            case 95: {
                
                //#line 643 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 96:  FieldModifier ::= static
            //
            case 96: {
                
                //#line 648 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 97:  FieldModifier ::= final
            //
            case 97: {
                
                //#line 653 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 98:  FieldModifier ::= transient
            //
            case 98: {
                
                //#line 658 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 99:  MethodDeclaration ::= MethodHeader MethodBody
            //
            case 99: {
                //#line 667 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                MethodDecl MethodHeader = (MethodDecl) getRhsSym(1);
                //#line 667 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block MethodBody = (Block) getRhsSym(2);
                //#line 669 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                JPGPosition old_pos = (JPGPosition) MethodHeader.position();
                setResult(MethodHeader.body(MethodBody)
                          .position(pos(old_pos.getLeftIToken().getTokenIndex(), getRightSpan())));
                break;
            }
     
            //
            // Rule 101:  ResultType ::= void
            //
            case 101: {
                
                //#line 680 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 102:  FormalParameterList ::= LastFormalParameter
            //
            case 102: {
                //#line 700 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Formal LastFormalParameter = (Formal) getRhsSym(1);
                //#line 702 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(LastFormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 103:  FormalParameterList ::= FormalParameters , LastFormalParameter
            //
            case 103: {
                //#line 707 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List FormalParameters = (List) getRhsSym(1);
                //#line 707 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Formal LastFormalParameter = (Formal) getRhsSym(3);
                //#line 709 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                FormalParameters.add(LastFormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 104:  FormalParameters ::= FormalParameter
            //
            case 104: {
                //#line 714 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 716 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 105:  FormalParameters ::= FormalParameters , FormalParameter
            //
            case 105: {
                //#line 721 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List FormalParameters = (List) getRhsSym(1);
                //#line 721 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 723 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                FormalParameters.add(FormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 106:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 106: {
                //#line 728 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                //#line 728 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 728 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(3);
                //#line 730 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names()));
                break;
            }
     
            //
            // Rule 108:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 108: {
                //#line 735 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags VariableModifiers = (Flags) getRhsSym(1);
                //#line 735 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags VariableModifier = (Flags) getRhsSym(2);
                //#line 737 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(VariableModifiers.set(VariableModifier));
                break;
            }
     
            //
            // Rule 109:  VariableModifier ::= final
            //
            case 109: {
                
                //#line 743 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 110:  LastFormalParameter ::= VariableModifiersopt Type ...opt$opt VariableDeclaratorId
            //
            case 110: {
                //#line 749 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                //#line 749 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 749 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Object opt = (Object) getRhsSym(3);
                //#line 749 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(4);
                //#line 751 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                assert(opt == null);
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names()));
                break;
            }
     
            //
            // Rule 112:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 112: {
                //#line 763 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags MethodModifiers = (Flags) getRhsSym(1);
                //#line 763 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags MethodModifier = (Flags) getRhsSym(2);
                //#line 765 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(MethodModifiers.set(MethodModifier));
                break;
            }
     
            //
            // Rule 113:  MethodModifier ::= public
            //
            case 113: {
                
                //#line 773 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 114:  MethodModifier ::= protected
            //
            case 114: {
                
                //#line 778 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 115:  MethodModifier ::= private
            //
            case 115: {
                
                //#line 783 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 116:  MethodModifier ::= abstract
            //
            case 116: {
                
                //#line 788 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 117:  MethodModifier ::= static
            //
            case 117: {
                
                //#line 793 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 118:  MethodModifier ::= final
            //
            case 118: {
                
                //#line 798 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 119:  MethodModifier ::= native
            //
            case 119: {
                
                //#line 808 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 120:  MethodModifier ::= strictfp
            //
            case 120: {
                
                //#line 813 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 121:  Throws ::= throws ExceptionTypeList
            //
            case 121: {
                //#line 817 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 819 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 122:  ExceptionTypeList ::= ExceptionType
            //
            case 122: {
                //#line 823 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 825 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                break;
            }
     
            //
            // Rule 123:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 123: {
                //#line 830 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 830 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 832 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ExceptionTypeList.add(ExceptionType);
                // setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 126:  MethodBody ::= ;
            //
            case 126:
                setResult(null);
                break;
 
            //
            // Rule 128:  StaticInitializer ::= static Block
            //
            case 128: {
                //#line 852 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 854 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 129:  SimpleTypeName ::= identifier
            //
            case 129: {
                //#line 869 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 871 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 131:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 131: {
                //#line 876 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ConstructorModifiers = (Flags) getRhsSym(1);
                //#line 876 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ConstructorModifier = (Flags) getRhsSym(2);
                //#line 878 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ConstructorModifiers.set(ConstructorModifier));
                break;
            }
     
            //
            // Rule 132:  ConstructorModifier ::= public
            //
            case 132: {
                
                //#line 886 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 133:  ConstructorModifier ::= protected
            //
            case 133: {
                
                //#line 891 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 134:  ConstructorModifier ::= private
            //
            case 134: {
                
                //#line 896 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 135:  ConstructorBody ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 135: {
                //#line 900 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 900 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 902 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l;
                if (ExplicitConstructorInvocationopt == null)
                    l = BlockStatementsopt;
                else
                {
                    l = new TypedList(new LinkedList(), Stmt.class, false);
                    l.add(ExplicitConstructorInvocationopt);
                    l.addAll(BlockStatementsopt);
                }
                setResult(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 136:  Arguments ::= ( ArgumentListopt )
            //
            case 136: {
                //#line 933 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 935 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ArgumentListopt);
                break;
            }
     
            //
            // Rule 139:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 139: {
                //#line 951 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags InterfaceModifiers = (Flags) getRhsSym(1);
                //#line 951 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags InterfaceModifier = (Flags) getRhsSym(2);
                //#line 953 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(InterfaceModifiers.set(InterfaceModifier));
                break;
            }
     
            //
            // Rule 140:  InterfaceModifier ::= public
            //
            case 140: {
                
                //#line 961 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 141:  InterfaceModifier ::= protected
            //
            case 141: {
                
                //#line 966 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 142:  InterfaceModifier ::= private
            //
            case 142: {
                
                //#line 971 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 143:  InterfaceModifier ::= abstract
            //
            case 143: {
                
                //#line 976 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 144:  InterfaceModifier ::= static
            //
            case 144: {
                
                //#line 981 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 145:  InterfaceModifier ::= strictfp
            //
            case 145: {
                
                //#line 986 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 146:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 146: {
                //#line 990 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(2);
                //#line 992 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 147:  ExtendsInterfaces ::= ExtendsInterfaces , InterfaceType
            //
            case 147: {
                //#line 997 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 997 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                //#line 999 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ExtendsInterfaces.add(InterfaceType);
                // setResult(ExtendsInterfaces);
                break;
            }
     
            //
            // Rule 148:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 148: {
                //#line 1009 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 1011 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                break;
            }
     
            //
            // Rule 150:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 150: {
                //#line 1016 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 1016 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 1018 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                break;
            }
     
            //
            // Rule 152:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 152: {
                //#line 1024 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                MethodDecl AbstractMethodDeclaration = (MethodDecl) getRhsSym(1);
                //#line 1026 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(AbstractMethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 153:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 153: {
                //#line 1031 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 1033 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 154:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 154: {
                //#line 1038 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 1040 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 155:  InterfaceMemberDeclaration ::= ;
            //
            case 155: {
                
                //#line 1047 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 156:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 156: {
                //#line 1051 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ConstantModifiersopt = (Flags) getRhsSym(1);
                //#line 1051 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1051 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 1053 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
            // Rule 158:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 158: {
                //#line 1071 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ConstantModifiers = (Flags) getRhsSym(1);
                //#line 1071 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags ConstantModifier = (Flags) getRhsSym(2);
                //#line 1073 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ConstantModifiers.set(ConstantModifier));
                break;
            }
     
            //
            // Rule 159:  ConstantModifier ::= public
            //
            case 159: {
                
                //#line 1081 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 160:  ConstantModifier ::= static
            //
            case 160: {
                
                //#line 1086 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 161:  ConstantModifier ::= final
            //
            case 161: {
                
                //#line 1091 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 163:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 163: {
                //#line 1098 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags AbstractMethodModifiers = (Flags) getRhsSym(1);
                //#line 1098 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags AbstractMethodModifier = (Flags) getRhsSym(2);
                //#line 1100 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(AbstractMethodModifiers.set(AbstractMethodModifier));
                break;
            }
     
            //
            // Rule 164:  AbstractMethodModifier ::= public
            //
            case 164: {
                
                //#line 1108 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 165:  AbstractMethodModifier ::= abstract
            //
            case 165: {
                
                //#line 1113 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 166:  SimpleName ::= identifier
            //
            case 166: {
                //#line 1169 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1171 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 167:  ArrayInitializer ::= { VariableInitializersopt ,opt$opt }
            //
            case 167: {
                //#line 1198 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableInitializersopt = (List) getRhsSym(2);
                //#line 1198 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Object opt = (Object) getRhsSym(3);
                //#line 1200 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                if (VariableInitializersopt == null)
                     setResult(nf.ArrayInit(pos()));
                else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
                break;
            }
     
            //
            // Rule 168:  VariableInitializers ::= VariableInitializer
            //
            case 168: {
                //#line 1206 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 1208 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 169:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 169: {
                //#line 1213 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 1213 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 1215 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                break;
            }
     
            //
            // Rule 170:  Block ::= { BlockStatementsopt }
            //
            case 170: {
                //#line 1234 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 1236 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                break;
            }
     
            //
            // Rule 171:  BlockStatements ::= BlockStatement
            //
            case 171: {
                //#line 1240 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatement = (List) getRhsSym(1);
                //#line 1242 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                break;
            }
     
            //
            // Rule 172:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 172: {
                //#line 1247 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatements = (List) getRhsSym(1);
                //#line 1247 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatement = (List) getRhsSym(2);
                //#line 1249 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                break;
            }
     
            //
            // Rule 174:  BlockStatement ::= ClassDeclaration
            //
            case 174: {
                //#line 1255 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 1257 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                break;
            }
     
            //
            // Rule 175:  BlockStatement ::= Statement
            //
            case 175: {
                //#line 1262 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 1264 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                break;
            }
     
            //
            // Rule 177:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 177: {
                //#line 1272 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                //#line 1272 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1272 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 1274 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                List s = new TypedList(new LinkedList(), Stmt.class, false);
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
                l.addAll(s); 
                setResult(l);
                break;
            }
     
            //
            // Rule 201:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 201: {
                //#line 1333 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1333 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1335 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.If(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 202:  IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
            //
            case 202: {
                //#line 1339 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1339 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1339 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1341 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.If(pos(), Expression, StatementNoShortIf, Statement));
                break;
            }
     
            //
            // Rule 203:  IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf$true_stmt else StatementNoShortIf$false_stmt
            //
            case 203: {
                //#line 1345 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1345 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt true_stmt = (Stmt) getRhsSym(5);
                //#line 1345 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt false_stmt = (Stmt) getRhsSym(7);
                //#line 1347 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.If(pos(), Expression, true_stmt, false_stmt));
                break;
            }
     
            //
            // Rule 204:  EmptyStatement ::= ;
            //
            case 204: {
                
                //#line 1353 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 205:  LabeledStatement ::= identifier : Statement
            //
            case 205: {
                //#line 1357 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1357 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1359 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Labeled(pos(), identifier.getIdentifier(), Statement));
                break;
            }
     
            //
            // Rule 206:  LabeledStatementNoShortIf ::= identifier : StatementNoShortIf
            //
            case 206: {
                //#line 1363 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1363 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(3);
                //#line 1365 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Labeled(pos(), identifier.getIdentifier(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 207:  ExpressionStatement ::= StatementExpression ;
            //
            case 207: {
                //#line 1368 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1370 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 215:  AssertStatement ::= assert Expression ;
            //
            case 215: {
                //#line 1391 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1393 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Assert(pos(), Expression));
                break;
            }
     
            //
            // Rule 216:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 216: {
                //#line 1396 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1396 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1398 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                break;
            }
     
            //
            // Rule 217:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 217: {
                //#line 1402 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1402 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1404 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                break;
            }
     
            //
            // Rule 218:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 218: {
                //#line 1408 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1408 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1410 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                break;
            }
     
            //
            // Rule 220:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 220: {
                //#line 1416 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1416 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1418 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                break;
            }
     
            //
            // Rule 221:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 221: {
                //#line 1423 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1423 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1425 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                break;
            }
     
            //
            // Rule 222:  SwitchLabels ::= SwitchLabel
            //
            case 222: {
                //#line 1432 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1434 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                break;
            }
     
            //
            // Rule 223:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 223: {
                //#line 1439 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1439 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1441 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                break;
            }
     
            //
            // Rule 224:  SwitchLabel ::= case ConstantExpression :
            //
            case 224: {
                //#line 1446 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1448 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                break;
            }
     
            //
            // Rule 225:  SwitchLabel ::= default :
            //
            case 225: {
                
                //#line 1455 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 226:  WhileStatement ::= while ( Expression ) Statement
            //
            case 226: {
                //#line 1462 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1462 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1464 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.While(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 227:  WhileStatementNoShortIf ::= while ( Expression ) StatementNoShortIf
            //
            case 227: {
                //#line 1468 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1468 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1470 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.While(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 228:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 228: {
                //#line 1474 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1474 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1476 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                break;
            }
     
            //
            // Rule 231:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 231: {
                //#line 1483 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1483 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1483 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1483 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1485 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                break;
            }
     
            //
            // Rule 232:  ForStatementNoShortIf ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) StatementNoShortIf
            //
            case 232: {
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(9);
                //#line 1491 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 234:  ForInit ::= LocalVariableDeclaration
            //
            case 234: {
                //#line 1496 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1498 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 236:  StatementExpressionList ::= StatementExpression
            //
            case 236: {
                //#line 1506 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1508 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                break;
            }
     
            //
            // Rule 237:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 237: {
                //#line 1513 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1513 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1515 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                //setResult(StatementExpressionList);
                break;
            }
     
            //
            // Rule 238:  BreakStatement ::= break identifieropt ;
            //
            case 238: {
                //#line 1523 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name identifieropt = (Name) getRhsSym(2);
                //#line 1525 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                if (identifieropt == null)
                     setResult(nf.Break(pos()));
                else setResult(nf.Break(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 239:  ContinueStatement ::= continue identifieropt ;
            //
            case 239: {
                //#line 1531 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name identifieropt = (Name) getRhsSym(2);
                //#line 1533 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                if (identifieropt == null)
                     setResult(nf.Continue(pos()));
                else setResult(nf.Continue(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 240:  ReturnStatement ::= return Expressionopt ;
            //
            case 240: {
                //#line 1539 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1541 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Return(pos(), Expressionopt));
                break;
            }
     
            //
            // Rule 241:  ThrowStatement ::= throw Expression ;
            //
            case 241: {
                //#line 1545 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1547 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Throw(pos(), Expression));
                break;
            }
     
            //
            // Rule 242:  TryStatement ::= try Block Catches
            //
            case 242: {
                //#line 1557 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 1557 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List Catches = (List) getRhsSym(3);
                //#line 1559 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Try(pos(), Block, Catches));
                break;
            }
     
            //
            // Rule 243:  TryStatement ::= try Block Catchesopt Finally
            //
            case 243: {
                //#line 1562 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 1562 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1562 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Finally = (Block) getRhsSym(4);
                //#line 1564 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                break;
            }
     
            //
            // Rule 244:  Catches ::= CatchClause
            //
            case 244: {
                //#line 1568 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1570 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                break;
            }
     
            //
            // Rule 245:  Catches ::= Catches CatchClause
            //
            case 245: {
                //#line 1575 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List Catches = (List) getRhsSym(1);
                //#line 1575 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1577 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                break;
            }
     
            //
            // Rule 246:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 246: {
                //#line 1582 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1582 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(5);
                //#line 1584 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                break;
            }
     
            //
            // Rule 247:  Finally ::= finally Block
            //
            case 247: {
                //#line 1588 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Block Block = (Block) getRhsSym(2);
                //#line 1590 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Block);
                break;
            }
     
            //
            // Rule 251:  PrimaryNoNewArray ::= Type . class
            //
            case 251: {
                //#line 1608 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1610 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
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
            // Rule 252:  PrimaryNoNewArray ::= void . class
            //
            case 252: {
                
                //#line 1629 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(getLeftSpan()), ts.Void())));
                break;
            }
     
            //
            // Rule 253:  PrimaryNoNewArray ::= this
            //
            case 253: {
                
                //#line 1635 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 254:  PrimaryNoNewArray ::= ClassName . this
            //
            case 254: {
                //#line 1638 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1640 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 255:  PrimaryNoNewArray ::= ( Expression )
            //
            case 255: {
                //#line 1643 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1645 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.ParExpr(pos(), Expression));
                break;
            }
     
            //
            // Rule 260:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 260: {
                //#line 1653 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                //#line 1655 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.IntegerLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 261:  Literal ::= LongLiteral$LongLiteral
            //
            case 261: {
                //#line 1659 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken LongLiteral = (IToken) getRhsIToken(1);
                //#line 1661 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 262:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 262: {
                //#line 1665 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                //#line 1667 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 263:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 263: {
                //#line 1671 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                //#line 1673 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 264:  Literal ::= BooleanLiteral
            //
            case 264: {
                //#line 1677 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 1679 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 265:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 265: {
                //#line 1682 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                //#line 1684 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 266:  Literal ::= StringLiteral$str
            //
            case 266: {
                //#line 1688 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken str = (IToken) getRhsIToken(1);
                //#line 1690 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 267:  Literal ::= null
            //
            case 267: {
                
                //#line 1696 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 268:  BooleanLiteral ::= true$trueLiteral
            //
            case 268: {
                //#line 1700 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 1702 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 269:  BooleanLiteral ::= false$falseLiteral
            //
            case 269: {
                //#line 1705 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 1707 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 270:  ArgumentList ::= Expression
            //
            case 270: {
                //#line 1720 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1722 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                break;
            }
     
            //
            // Rule 271:  ArgumentList ::= ArgumentList , Expression
            //
            case 271: {
                //#line 1727 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ArgumentList = (List) getRhsSym(1);
                //#line 1727 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1729 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                ArgumentList.add(Expression);
                //setResult(ArgumentList);
                break;
            }
     
            //
            // Rule 272:  DimExprs ::= DimExpr
            //
            case 272: {
                //#line 1763 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr DimExpr = (Expr) getRhsSym(1);
                //#line 1765 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                setResult(l);
                break;
            }
     
            //
            // Rule 273:  DimExprs ::= DimExprs DimExpr
            //
            case 273: {
                //#line 1770 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List DimExprs = (List) getRhsSym(1);
                //#line 1770 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr DimExpr = (Expr) getRhsSym(2);
                //#line 1772 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                DimExprs.add(DimExpr);
                //setResult(DimExprs);
                break;
            }
     
            //
            // Rule 274:  DimExpr ::= [ Expression ]
            //
            case 274: {
                //#line 1777 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1779 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Expression.position(pos()));
                break;
            }
     
            //
            // Rule 275:  Dims ::= [ ]
            //
            case 275: {
                
                //#line 1785 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Integer(1));
                break;
            }
     
            //
            // Rule 276:  Dims ::= Dims [ ]
            //
            case 276: {
                //#line 1788 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Integer Dims = (Integer) getRhsSym(1);
                //#line 1790 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Integer(Dims.intValue() + 1));
                break;
            }
     
            //
            // Rule 277:  FieldAccess ::= Primary . identifier
            //
            case 277: {
                //#line 1794 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1794 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1796 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Field(pos(), Primary, identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 278:  FieldAccess ::= super . identifier
            //
            case 278: {
                //#line 1799 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1801 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 279:  FieldAccess ::= ClassName . super$sup . identifier
            //
            case 279: {
                //#line 1804 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1804 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 1804 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                //#line 1806 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 280:  MethodInvocation ::= MethodName ( ArgumentListopt )
            //
            case 280: {
                //#line 1810 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name MethodName = (Name) getRhsSym(1);
                //#line 1810 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 1812 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, ArgumentListopt));
                break;
            }
     
            //
            // Rule 282:  PostfixExpression ::= ExpressionName
            //
            case 282: {
                //#line 1835 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 1837 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 285:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 285: {
                //#line 1843 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 1845 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 286:  PostDecrementExpression ::= PostfixExpression --
            //
            case 286: {
                //#line 1849 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 1851 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 289:  UnaryExpression ::= + UnaryExpression
            //
            case 289: {
                //#line 1857 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1859 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpression));
                break;
            }
     
            //
            // Rule 290:  UnaryExpression ::= - UnaryExpression
            //
            case 290: {
                //#line 1862 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1864 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpression));
                break;
            }
     
            //
            // Rule 292:  PreIncrementExpression ::= ++ UnaryExpression
            //
            case 292: {
                //#line 1869 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1871 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpression));
                break;
            }
     
            //
            // Rule 293:  PreDecrementExpression ::= -- UnaryExpression
            //
            case 293: {
                //#line 1875 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1877 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpression));
                break;
            }
     
            //
            // Rule 295:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 295: {
                //#line 1882 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1884 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 296:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 296: {
                //#line 1887 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 1889 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 299:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 299: {
                //#line 1901 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1901 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 1903 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                break;
            }
     
            //
            // Rule 300:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 300: {
                //#line 1906 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1906 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 1908 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                break;
            }
     
            //
            // Rule 301:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 301: {
                //#line 1911 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1911 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 1913 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                break;
            }
     
            //
            // Rule 303:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 303: {
                //#line 1918 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 1918 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 1920 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 304:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 304: {
                //#line 1923 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 1923 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 1925 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 306:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 306: {
                //#line 1930 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 1930 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 1932 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                break;
            }
     
            //
            // Rule 307:  ShiftExpression ::= ShiftExpression > > AdditiveExpression
            //
            case 307: {
                //#line 1935 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 1935 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(4);
                //#line 1937 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 308:  ShiftExpression ::= ShiftExpression > > > AdditiveExpression
            //
            case 308: {
                //#line 1941 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 1941 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AdditiveExpression = (Expr) getRhsSym(5);
                //#line 1943 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 310:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 310: {
                //#line 1949 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1949 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 1951 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
                break;
            }
     
            //
            // Rule 311:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 311: {
                //#line 1954 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1954 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 1956 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
                break;
            }
     
            //
            // Rule 312:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 312: {
                //#line 1959 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1959 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 1961 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
                break;
            }
     
            //
            // Rule 313:  RelationalExpression ::= RelationalExpression > = ShiftExpression
            //
            case 313: {
                //#line 1964 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1964 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ShiftExpression = (Expr) getRhsSym(4);
                //#line 1966 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
                break;
            }
     
            //
            // Rule 315:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 315: {
                //#line 1980 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 1980 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 1982 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                break;
            }
     
            //
            // Rule 316:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 316: {
                //#line 1985 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 1985 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 1987 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                break;
            }
     
            //
            // Rule 318:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 318: {
                //#line 1992 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 1992 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 1994 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                break;
            }
     
            //
            // Rule 320:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 320: {
                //#line 1999 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1999 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 2001 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                break;
            }
     
            //
            // Rule 322:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 322: {
                //#line 2006 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 2006 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 2008 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 324:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 324: {
                //#line 2013 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 2013 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 2015 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                break;
            }
     
            //
            // Rule 326:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 326: {
                //#line 2020 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 2020 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 2022 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                break;
            }
     
            //
            // Rule 328:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 328: {
                //#line 2027 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 2027 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2027 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 2029 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 331:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 331: {
                //#line 2036 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 2036 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 2036 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 2038 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 332:  LeftHandSide ::= ExpressionName
            //
            case 332: {
                //#line 2042 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 2044 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 335:  AssignmentOperator ::= =
            //
            case 335: {
                
                //#line 2052 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 336:  AssignmentOperator ::= *=
            //
            case 336: {
                
                //#line 2057 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 337:  AssignmentOperator ::= /=
            //
            case 337: {
                
                //#line 2062 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 338:  AssignmentOperator ::= %=
            //
            case 338: {
                
                //#line 2067 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 339:  AssignmentOperator ::= +=
            //
            case 339: {
                
                //#line 2072 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 340:  AssignmentOperator ::= -=
            //
            case 340: {
                
                //#line 2077 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 341:  AssignmentOperator ::= <<=
            //
            case 341: {
                
                //#line 2082 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 342:  AssignmentOperator ::= > > =
            //
            case 342: {
                
                //#line 2087 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 343:  AssignmentOperator ::= > > > =
            //
            case 343: {
                
                //#line 2093 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 344:  AssignmentOperator ::= &=
            //
            case 344: {
                
                //#line 2099 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 345:  AssignmentOperator ::= ^=
            //
            case 345: {
                
                //#line 2104 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 346:  AssignmentOperator ::= |=
            //
            case 346: {
                
                //#line 2109 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 349:  Dimsopt ::= $Empty
            //
            case 349: {
                
                //#line 2122 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Integer(0));
                break;
            }
     
            //
            // Rule 351:  Catchesopt ::= $Empty
            //
            case 351: {
                
                //#line 2129 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 353:  identifieropt ::= $Empty
            //
            case 353:
                setResult(null);
                break;
 
            //
            // Rule 354:  identifieropt ::= identifier
            //
            case 354: {
                //#line 2136 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 2138 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 355:  ForUpdateopt ::= $Empty
            //
            case 355: {
                
                //#line 2144 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 357:  Expressionopt ::= $Empty
            //
            case 357:
                setResult(null);
                break;
 
            //
            // Rule 359:  ForInitopt ::= $Empty
            //
            case 359: {
                
                //#line 2155 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 361:  SwitchLabelsopt ::= $Empty
            //
            case 361: {
                
                //#line 2162 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 363:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 363: {
                
                //#line 2169 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 365:  VariableModifiersopt ::= $Empty
            //
            case 365: {
                
                //#line 2176 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 367:  VariableInitializersopt ::= $Empty
            //
            case 367:
                setResult(null);
                break;
 
            //
            // Rule 369:  AbstractMethodModifiersopt ::= $Empty
            //
            case 369: {
                
                //#line 2206 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 371:  ConstantModifiersopt ::= $Empty
            //
            case 371: {
                
                //#line 2213 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 373:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 373: {
                
                //#line 2220 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 375:  ExtendsInterfacesopt ::= $Empty
            //
            case 375: {
                
                //#line 2227 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 377:  InterfaceModifiersopt ::= $Empty
            //
            case 377: {
                
                //#line 2234 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 379:  ClassBodyopt ::= $Empty
            //
            case 379:
                setResult(null);
                break;
 
            //
            // Rule 381:  Argumentsopt ::= $Empty
            //
            case 381:
                setResult(null);
                break;
 
            //
            // Rule 382:  Argumentsopt ::= Arguments
            //
            case 382:
                throw new Error("No action specified for rule " + 382);
 
            //
            // Rule 383:  ,opt ::= $Empty
            //
            case 383:
                setResult(null);
                break;
 
            //
            // Rule 385:  ArgumentListopt ::= $Empty
            //
            case 385: {
                
                //#line 2264 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 387:  BlockStatementsopt ::= $Empty
            //
            case 387: {
                
                //#line 2271 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 389:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 389:
                setResult(null);
                break;
 
            //
            // Rule 391:  ConstructorModifiersopt ::= $Empty
            //
            case 391: {
                
                //#line 2282 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 393:  ...opt ::= $Empty
            //
            case 393:
                setResult(null);
                break;
 
            //
            // Rule 395:  FormalParameterListopt ::= $Empty
            //
            case 395: {
                
                //#line 2293 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 397:  Throwsopt ::= $Empty
            //
            case 397: {
                
                //#line 2300 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 399:  MethodModifiersopt ::= $Empty
            //
            case 399: {
                
                //#line 2307 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 401:  FieldModifiersopt ::= $Empty
            //
            case 401: {
                
                //#line 2314 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 403:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 403: {
                
                //#line 2321 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 405:  Interfacesopt ::= $Empty
            //
            case 405: {
                
                //#line 2328 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 407:  Superopt ::= $Empty
            //
            case 407: {
                
                //#line 2335 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
               setResult(new Name(nf, ts, pos(), "x10.lang.Object").toType());
                break;
            }
     
            //
            // Rule 409:  ClassModifiersopt ::= $Empty
            //
            case 409: {
                
                //#line 2346 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 411:  TypeDeclarationsopt ::= $Empty
            //
            case 411: {
                
                //#line 2358 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 413:  ImportDeclarationsopt ::= $Empty
            //
            case 413: {
                
                //#line 2365 "C:/eclipse/ws6/x10.compiler/src/x10/parser/GJavaParserForX10.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 415:  PackageDeclarationopt ::= $Empty
            //
            case 415:
                setResult(null);
                break;
 
            //
            // Rule 417:  ClassType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 417: {
                //#line 727 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 727 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 727 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                //#line 729 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                     setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 418:  InterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 418: {
                //#line 736 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 736 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 736 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                //#line 738 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                 setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 419:  PackageDeclaration ::= package PackageName ;
            //
            case 419: {
                //#line 744 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name PackageName = (Name) getRhsSym(2);
                //#line 746 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(PackageName.toPackage());
                break;
            }
     
            //
            // Rule 420:  NormalClassDeclaration ::= X10ClassModifiersopt class identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 420: {
                //#line 750 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Flags X10ClassModifiersopt = (X10Flags) getRhsSym(1);
                //#line 750 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 750 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                //#line 750 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(5);
                //#line 750 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(6);
                //#line 750 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(7);
                //#line 752 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
      checkTypeName(identifier);
      List/*<PropertyDecl>*/ props = PropertyListopt == null ? null
                  : (List) PropertyListopt[0];
      Expr ci = PropertyListopt == null ? null : (Expr) PropertyListopt[1];
      setResult(X10Flags.isValue(X10ClassModifiersopt)
         ? nf.ValueClassDecl(pos(),
              X10ClassModifiersopt, identifier.getIdentifier(), props, ci, Superopt, Interfacesopt, ClassBody)
         : nf.ClassDecl(pos(),
              X10ClassModifiersopt, identifier.getIdentifier(), props, ci, Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 422:  X10ClassModifiers ::= X10ClassModifiers X10ClassModifier
            //
            case 422: {
                //#line 765 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Flags X10ClassModifiers = (X10Flags) getRhsSym(1);
                //#line 765 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Flags X10ClassModifier = (X10Flags) getRhsSym(2);
                //#line 767 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
       X10Flags result = X10ClassModifiers.setX(X10ClassModifier);
                setResult(result);
               
                break;
            }
     
            //
            // Rule 423:  X10ClassModifier ::= ClassModifier
            //
            case 423: {
                //#line 773 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Flags ClassModifier = (Flags) getRhsSym(1);
                //#line 775 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.toX10Flags(ClassModifier));
                break;
            }
     
            //
            // Rule 424:  X10ClassModifier ::= safe
            //
            case 424: {
                
                //#line 780 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.SAFE);
                break;
            }
     
            //
            // Rule 425:  PropertyList ::= ( Properties WhereClauseopt )
            //
            case 425: {
                //#line 784 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List Properties = (List) getRhsSym(2);
                //#line 784 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(3);
                //#line 786 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
   Object[] result = new Object[2];
   result[0] = Properties;
   result[1] = WhereClauseopt;
   setResult(result);
           break;
            }  
            //
            // Rule 426:  PropertyList ::= ( WhereClause )
            //
            case 426: {
                //#line 791 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClause = (Expr) getRhsSym(2);
                //#line 793 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
   Object[] result = new Object[2];
   result[0] = null;
   result[1] = WhereClause;
   setResult(result);
           break;
            }  
            //
            // Rule 427:  Properties ::= Property
            //
            case 427: {
                //#line 800 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 802 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                break;
            }
     
            //
            // Rule 428:  Properties ::= Properties , Property
            //
            case 428: {
                //#line 807 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List Properties = (List) getRhsSym(1);
                //#line 807 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 809 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Properties.add(Property);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 429:  Property ::= Type identifier
            //
            case 429: {
                //#line 815 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 815 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(2);
                //#line 817 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
    
                setResult(nf.PropertyDecl(pos(), Flags.PUBLIC.Final(), Type,
                identifier.getIdentifier()));
              
                break;
            }
     
            //
            // Rule 430:  MethodHeader ::= ThisClauseopt MethodModifiersopt ResultType MethodDeclarator Throwsopt
            //
            case 430: {
                //#line 823 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr ThisClauseopt = (DepParameterExpr) getRhsSym(1);
                //#line 823 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Flags MethodModifiersopt = (Flags) getRhsSym(2);
                //#line 823 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 823 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object[] MethodDeclarator = (Object[]) getRhsSym(4);
                //#line 823 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 825 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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
          MethodModifiersopt,
          nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3)), e.intValue()),
          c.toString(),
          d,
          where,
          Throwsopt,
          null));
                break;
            }
     
            //
            // Rule 431:  ExplicitConstructorInvocation ::= this ( ArgumentListopt ) ;
            //
            case 431: {
                //#line 847 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 849 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ThisCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 432:  ExplicitConstructorInvocation ::= super ( ArgumentListopt ) ;
            //
            case 432: {
                //#line 852 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 854 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.SuperCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 433:  ExplicitConstructorInvocation ::= Primary . this ( ArgumentListopt ) ;
            //
            case 433: {
                //#line 857 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 857 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 859 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ThisCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 434:  ExplicitConstructorInvocation ::= Primary . super ( ArgumentListopt ) ;
            //
            case 434: {
                //#line 862 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 862 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 864 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.SuperCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 435:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier PropertyListopt ExtendsInterfacesopt InterfaceBody
            //
            case 435: {
                //#line 868 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Flags InterfaceModifiersopt = (Flags) getRhsSym(1);
                //#line 868 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 868 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                //#line 868 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(5);
                //#line 868 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(6);
                //#line 870 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
      checkTypeName(identifier);
      List/*<PropertyDecl>*/ props = PropertyListopt == null ? null 
                  : (List) PropertyListopt[0];
      Expr ci = PropertyListopt == null ? null : (Expr) PropertyListopt[1];
      setResult(nf.ClassDecl(pos(),
                   InterfaceModifiersopt.Interface(),
                   identifier.getIdentifier(),
                   props,
                   ci,
                   null,
                   ExtendsInterfacesopt,
                   InterfaceBody));
                break;
            }
     
            //
            // Rule 436:  AbstractMethodDeclaration ::= ThisClauseopt AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt ;
            //
            case 436: {
                //#line 885 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr ThisClauseopt = (DepParameterExpr) getRhsSym(1);
                //#line 885 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Flags AbstractMethodModifiersopt = (Flags) getRhsSym(2);
                //#line 885 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 885 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object[] MethodDeclarator = (Object[]) getRhsSym(4);
                //#line 885 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 887 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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
                c.toString(),
                d,
                where,
                Throwsopt,
                null));
                break;
            }
     
            //
            // Rule 437:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
            //
            case 437: {
                //#line 910 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassOrInterfaceType = (TypeNode) getRhsSym(2);
                //#line 910 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 910 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(6);
                //#line 912 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt));
                else setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 438:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 438: {
                //#line 917 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 917 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                //#line 917 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 917 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(8);
                //#line 919 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name b = new Name(nf, ts, pos(), identifier.getIdentifier());
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), Primary, b.toType(), ArgumentListopt));
                else setResult(nf.New(pos(), Primary, b.toType(), ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 439:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 439: {
                //#line 925 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name AmbiguousName = (Name) getRhsSym(1);
                //#line 925 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                //#line 925 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 925 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(8);
                //#line 927 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name b = new Name(nf, ts, pos(), identifier.getIdentifier());
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt));
                else setResult(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 440:  MethodInvocation ::= Primary . identifier ( ArgumentListopt )
            //
            case 440: {
                //#line 934 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 934 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 934 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 936 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Call(pos(), Primary, identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 441:  MethodInvocation ::= super . identifier ( ArgumentListopt )
            //
            case 441: {
                //#line 939 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 939 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 941 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 442:  MethodInvocation ::= ClassName . super$sup . identifier ( ArgumentListopt )
            //
            case 442: {
                //#line 944 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 944 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 944 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                //#line 944 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 946 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 443:  Type ::= DataType
            //
            case 443: {
                //#line 955 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode DataType = (TypeNode) getRhsSym(1);
                //#line 957 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
              setResult(DataType);
                break;
            }
     
            //
            // Rule 444:  Type ::= nullable < Type > DepParametersopt
            //
            case 444: {
                //#line 960 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 960 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(5);
                //#line 962 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10TypeNode t = nf.Nullable(pos(), Type);
                setResult(DepParametersopt == null ? t 
                : t.dep(null, DepParametersopt));
      
                break;
            }
     
            //
            // Rule 445:  Type ::= future < Type >
            //
            case 445: {
                //#line 968 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 970 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Future(pos(), Type));
                break;
            }
     
            //
            // Rule 449:  PrimitiveType ::= NumericType DepParametersopt
            //
            case 449: {
                //#line 985 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode NumericType = (TypeNode) getRhsSym(1);
                //#line 985 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 987 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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
                //#line 993 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 995 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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
                //#line 1007 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name TypeName = (Name) getRhsSym(1);
                //#line 1007 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 1007 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                //#line 1009 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
            X10TypeNode type;
            
            if (ts.isPrimitiveTypeName(TypeName.name)) {
                try {
		type= (X10TypeNode) nf.CanonicalTypeNode(pos(), ts.primitiveForName(TypeName.name));
	    } catch (SemanticException e) {
		throw new InternalCompilerError("Unable to create primitive type for '" + TypeName.name + "'!");
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
                //#line 1026 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(2);
                //#line 1028 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(DepParameterExpr);
                break;
            }
     
            //
            // Rule 457:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 457: {
                //#line 1032 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 1032 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(2);
                //#line 1034 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.DepParameterExpr(pos(), ArgumentList, WhereClauseopt));
                break;
            }
     
            //
            // Rule 458:  DepParameterExpr ::= WhereClause
            //
            case 458: {
                //#line 1037 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClause = (Expr) getRhsSym(1);
                //#line 1039 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.DepParameterExpr(pos(), Collections.EMPTY_LIST, WhereClause));
                break;
            }
     
            //
            // Rule 459:  WhereClause ::= : ConstExpression
            //
            case 459: {
                //#line 1043 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExpression = (Expr) getRhsSym(2);
                //#line 1045 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstExpression);
                break;
            }
     
            //
            // Rule 460:  ConstPrimary ::= Literal
            //
            case 460: {
                //#line 1050 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.ast.Lit Literal = (polyglot.ast.Lit) getRhsSym(1);
                //#line 1052 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(Literal);
                break;
            }
     
            //
            // Rule 461:  ConstPrimary ::= Type . class
            //
            case 461: {
                //#line 1055 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1057 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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
                
                //#line 1076 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(getLeftSpan()), ts.Void())));
                break;
            }
     
            //
            // Rule 463:  ConstPrimary ::= this
            //
            case 463: {
                
                //#line 1082 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 464:  ConstPrimary ::= here
            //
            case 464: {
                
                //#line 1087 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Here(pos()));
                break;
            }
     
            //
            // Rule 465:  ConstPrimary ::= ClassName . this
            //
            case 465: {
                //#line 1090 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1092 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 466:  ConstPrimary ::= ( ConstExpression )
            //
            case 466: {
                //#line 1095 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExpression = (Expr) getRhsSym(2);
                //#line 1097 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstExpression);
                break;
            }
     
            //
            // Rule 468:  ConstPrimary ::= self
            //
            case 468: {
                
                //#line 1103 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Self(pos()));
                break;
            }
     
            //
            // Rule 469:  ConstPostfixExpression ::= ConstPrimary
            //
            case 469: {
                //#line 1109 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstPrimary = (Expr) getRhsSym(1);
                //#line 1111 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstPrimary);
                        break;
            }
            
            //
            // Rule 470:  ConstPostfixExpression ::= ExpressionName
            //
            case 470: {
                //#line 1114 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 1116 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 471:  ConstUnaryExpression ::= ConstPostfixExpression
            //
            case 471: {
                //#line 1119 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstPostfixExpression = (Expr) getRhsSym(1);
                //#line 1121 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstPostfixExpression);
                        break;
            }
            
            //
            // Rule 472:  ConstUnaryExpression ::= + ConstUnaryExpression
            //
            case 472: {
                //#line 1124 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(2);
                //#line 1126 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Unary(pos(), Unary.POS, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 473:  ConstUnaryExpression ::= - ConstUnaryExpression
            //
            case 473: {
                //#line 1129 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(2);
                //#line 1131 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Unary(pos(), Unary.NEG, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 474:  ConstUnaryExpression ::= ! ConstUnaryExpression
            //
            case 474: {
                //#line 1134 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(2);
                //#line 1136 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Unary(pos(), Unary.NOT, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 475:  ConstMultiplicativeExpression ::= ConstUnaryExpression
            //
            case 475: {
                //#line 1140 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(1);
                //#line 1142 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstUnaryExpression);
                break;
            }
     
            //
            // Rule 476:  ConstMultiplicativeExpression ::= ConstMultiplicativeExpression * ConstUnaryExpression
            //
            case 476: {
                //#line 1145 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1145 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(3);
                //#line 1147 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstMultiplicativeExpression, Binary.MUL, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 477:  ConstMultiplicativeExpression ::= ConstMultiplicativeExpression / ConstUnaryExpression
            //
            case 477: {
                //#line 1150 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1150 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(3);
                //#line 1152 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstMultiplicativeExpression, Binary.DIV, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 478:  ConstMultiplicativeExpression ::= ConstMultiplicativeExpression % ConstUnaryExpression
            //
            case 478: {
                //#line 1155 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1155 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstUnaryExpression = (Expr) getRhsSym(3);
                //#line 1157 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstMultiplicativeExpression, Binary.MOD, ConstUnaryExpression));
                break;
            }
     
            //
            // Rule 479:  ConstAdditiveExpression ::= ConstMultiplicativeExpression
            //
            case 479: {
                //#line 1161 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 1163 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstMultiplicativeExpression);
                break;
            }
     
            //
            // Rule 480:  ConstAdditiveExpression ::= ConstAdditiveExpression + ConstMultiplicativeExpression
            //
            case 480: {
                //#line 1166 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(1);
                //#line 1166 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 1168 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstAdditiveExpression, Binary.ADD, ConstMultiplicativeExpression));
                break;
            }
     
            //
            // Rule 481:  ConstAdditiveExpression ::= ConstAdditiveExpression - ConstMultiplicativeExpression
            //
            case 481: {
                //#line 1171 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(1);
                //#line 1171 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstMultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 1173 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstAdditiveExpression, Binary.SUB, ConstMultiplicativeExpression));
                break;
            }
     
            //
            // Rule 482:  ConstRelationalExpression ::= ConstAdditiveExpression
            //
            case 482: {
                //#line 1178 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(1);
                //#line 1180 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstAdditiveExpression);
                break;
            }
     
            //
            // Rule 483:  ConstRelationalExpression ::= ConstRelationalExpression < ConstAdditiveExpression
            //
            case 483: {
                //#line 1183 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(1);
                //#line 1183 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(3);
                //#line 1185 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstRelationalExpression, Binary.LT, ConstAdditiveExpression));
                break;
            }
     
            //
            // Rule 484:  ConstRelationalExpression ::= ConstRelationalExpression > ConstAdditiveExpression
            //
            case 484: {
                //#line 1188 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(1);
                //#line 1188 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(3);
                //#line 1190 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstRelationalExpression, Binary.GT, ConstAdditiveExpression));
                break;
            }
     
            //
            // Rule 485:  ConstRelationalExpression ::= ConstRelationalExpression <= ConstAdditiveExpression
            //
            case 485: {
                //#line 1193 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(1);
                //#line 1193 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(3);
                //#line 1195 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstRelationalExpression, Binary.LE, ConstAdditiveExpression));
                break;
            }
     
            //
            // Rule 486:  ConstRelationalExpression ::= ConstRelationalExpression > = ConstAdditiveExpression
            //
            case 486: {
                //#line 1198 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(1);
                //#line 1198 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAdditiveExpression = (Expr) getRhsSym(4);
                //#line 1200 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstRelationalExpression, Binary.GE, ConstAdditiveExpression));
                break;
            }
     
            //
            // Rule 487:  ConstEqualityExpression ::= ConstRelationalExpression
            //
            case 487: {
                //#line 1204 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(1);
                //#line 1206 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstRelationalExpression);
                break;
            }
     
            //
            // Rule 488:  ConstEqualityExpression ::= ConstEqualityExpression == ConstRelationalExpression
            //
            case 488: {
                //#line 1209 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstEqualityExpression = (Expr) getRhsSym(1);
                //#line 1209 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(3);
                //#line 1211 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstEqualityExpression, Binary.EQ, ConstRelationalExpression));
                break;
            }
     
            //
            // Rule 489:  ConstEqualityExpression ::= ConstEqualityExpression != ConstRelationalExpression
            //
            case 489: {
                //#line 1214 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstEqualityExpression = (Expr) getRhsSym(1);
                //#line 1214 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstRelationalExpression = (Expr) getRhsSym(3);
                //#line 1216 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstEqualityExpression, Binary.NE, ConstRelationalExpression));
                break;
            }
     
            //
            // Rule 490:  ConstAndExpression ::= ConstEqualityExpression
            //
            case 490: {
                //#line 1220 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstEqualityExpression = (Expr) getRhsSym(1);
                //#line 1222 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstEqualityExpression);
                break;
            }
     
            //
            // Rule 491:  ConstAndExpression ::= ConstAndExpression && ConstEqualityExpression
            //
            case 491: {
                //#line 1225 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAndExpression = (Expr) getRhsSym(1);
                //#line 1225 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstEqualityExpression = (Expr) getRhsSym(3);
                //#line 1227 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstAndExpression, Binary.COND_AND, ConstEqualityExpression));
                break;
            }
     
            //
            // Rule 492:  ConstExclusiveOrExpression ::= ConstAndExpression
            //
            case 492: {
                //#line 1231 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAndExpression = (Expr) getRhsSym(1);
                //#line 1233 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstAndExpression);
                break;
            }
     
            //
            // Rule 493:  ConstExclusiveOrExpression ::= ConstExclusiveOrExpression ^ ConstAndExpression
            //
            case 493: {
                //#line 1236 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1236 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstAndExpression = (Expr) getRhsSym(3);
                //#line 1238 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstExclusiveOrExpression, Binary.BIT_XOR, ConstAndExpression));
                break;
            }
     
            //
            // Rule 494:  ConstInclusiveOrExpression ::= ConstExclusiveOrExpression
            //
            case 494: {
                //#line 1242 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1244 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstExclusiveOrExpression);
                break;
            }
     
            //
            // Rule 495:  ConstInclusiveOrExpression ::= ConstInclusiveOrExpression || ConstExclusiveOrExpression
            //
            case 495: {
                //#line 1247 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstInclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1247 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 1249 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Binary(pos(), ConstInclusiveOrExpression, Binary.COND_OR, ConstExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 496:  ConstExpression ::= ConstInclusiveOrExpression
            //
            case 496: {
                //#line 1253 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstInclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1255 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ConstInclusiveOrExpression);
                break;
            }
     
            //
            // Rule 497:  ConstExpression ::= ConstInclusiveOrExpression ? ConstExpression$first : ConstExpression
            //
            case 497: {
                //#line 1258 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstInclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 1258 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr first = (Expr) getRhsSym(3);
                //#line 1258 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstExpression = (Expr) getRhsSym(5);
                //#line 1260 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Conditional(pos(), ConstInclusiveOrExpression, first, ConstExpression));
                break;
            }
     
            //
            // Rule 498:  ConstFieldAccess ::= ConstPrimary . identifier
            //
            case 498: {
                //#line 1265 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr ConstPrimary = (Expr) getRhsSym(1);
                //#line 1265 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1267 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Field(pos(), ConstPrimary, identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 499:  ConstFieldAccess ::= super . identifier
            //
            case 499: {
                //#line 1270 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1272 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 500:  ConstFieldAccess ::= ClassName . super$sup . identifier
            //
            case 500: {
                //#line 1275 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name ClassName = (Name) getRhsSym(1);
                //#line 1275 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 1275 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                //#line 1277 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 502:  X10ArrayType ::= Type [ . ]
            //
            case 502: {
                //#line 1293 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1295 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, null));
                break;
            }
     
            //
            // Rule 503:  X10ArrayType ::= Type value [ . ]
            //
            case 503: {
                //#line 1298 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1300 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, null));
                break;
            }
     
            //
            // Rule 504:  X10ArrayType ::= Type [ DepParameterExpr ]
            //
            case 504: {
                //#line 1303 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1303 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(3);
                //#line 1305 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
                break;
            }
     
            //
            // Rule 505:  X10ArrayType ::= Type value [ DepParameterExpr ]
            //
            case 505: {
                //#line 1308 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1308 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(4);
                //#line 1310 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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
                
                //#line 1324 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.ATOMIC);
                break;
            }
     
            //
            // Rule 509:  MethodModifier ::= extern
            //
            case 509: {
                
                //#line 1329 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 510:  MethodModifier ::= safe
            //
            case 510: {
                
                //#line 1334 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.SAFE);
                break;
            }
     
            //
            // Rule 511:  MethodModifier ::= sequential
            //
            case 511: {
                
                //#line 1339 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.SEQUENTIAL);
                break;
            }
     
            //
            // Rule 512:  MethodModifier ::= local
            //
            case 512: {
                
                //#line 1344 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.LOCAL);
                break;
            }
     
            //
            // Rule 513:  MethodModifier ::= nonblocking
            //
            case 513: {
                
                //#line 1349 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.NON_BLOCKING);
                break;
            }
     
            //
            // Rule 515:  ValueClassDeclaration ::= X10ClassModifiersopt value identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 515: {
                //#line 1355 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Flags X10ClassModifiersopt = (X10Flags) getRhsSym(1);
                //#line 1355 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1355 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                //#line 1355 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(5);
                //#line 1355 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(6);
                //#line 1355 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(7);
                //#line 1357 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
    checkTypeName(identifier);
    List/*<PropertyDecl>*/ props = PropertyListopt==null ? null : (List) PropertyListopt[0];
    Expr ci = PropertyListopt==null ? null : (Expr) PropertyListopt[1];
    setResult(nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
    X10ClassModifiersopt, identifier.getIdentifier(), 
    props, ci, Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 516:  ValueClassDeclaration ::= X10ClassModifiersopt value class identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 516: {
                //#line 1365 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Flags X10ClassModifiersopt = (X10Flags) getRhsSym(1);
                //#line 1365 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                //#line 1365 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object[] PropertyListopt = (Object[]) getRhsSym(5);
                //#line 1365 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(6);
                //#line 1365 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1365 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1367 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                checkTypeName(identifier);
    List/*<PropertyDecl>*/ props = PropertyListopt==null ? null : (List) PropertyListopt[0];
    Expr ci = PropertyListopt==null ? null : (Expr) PropertyListopt[1];
    setResult(nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                              X10ClassModifiersopt, identifier.getIdentifier(), 
                              props, ci, Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 517:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 517: {
                //#line 1376 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Flags ConstructorModifiersopt = (Flags) getRhsSym(1);
                //#line 1376 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object[] ConstructorDeclarator = (Object[]) getRhsSym(2);
                //#line 1376 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(3);
                //#line 1376 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(4);
                //#line 1378 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
     Name a = (Name) ConstructorDeclarator[1];
     DepParameterExpr c = (DepParameterExpr) ConstructorDeclarator[2];
     List b = (List) ConstructorDeclarator[3];
     Expr e = (Expr) ConstructorDeclarator[4];              
     setResult(nf.ConstructorDecl(pos(), ConstructorModifiersopt, a.toString(), c, b, e, Throwsopt, ConstructorBody));
               break;
            }
    
            //
            // Rule 518:  ConstructorDeclarator ::= SimpleTypeName DepParametersopt ( FormalParameterListopt WhereClauseopt )
            //
            case 518: {
                //#line 1386 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name SimpleTypeName = (Name) getRhsSym(1);
                //#line 1386 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                //#line 1386 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 1386 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(5);
                //#line 1388 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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
                //#line 1396 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(2);
                //#line 1398 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(DepParameters);
                break;
            }
     
            //
            // Rule 520:  MethodDeclarator ::= identifier ( FormalParameterListopt WhereClauseopt )
            //
            case 520: {
                //#line 1404 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1404 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1404 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr WhereClauseopt = (Expr) getRhsSym(4);
                //#line 1406 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
            //   System.out.println("Parsing methoddeclarator...");
                Object[] a = new Object[5];
               a[0] = new Name(nf, ts, pos(), identifier.getIdentifier());
                a[1] = FormalParameterListopt;
               a[2] = new Integer(0);
               a[3] = WhereClauseopt;
             
                setResult(a);
                break;
            }
     
            //
            // Rule 521:  MethodDeclarator ::= MethodDeclarator [ ]
            //
            case 521: {
                //#line 1416 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object[] MethodDeclarator = (Object[]) getRhsSym(1);
                //#line 1418 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                MethodDeclarator[2] = new Integer(((Integer) MethodDeclarator[2]).intValue() + 1);
                // setResult(MethodDeclarator);
               break;
            }
     
            //
            // Rule 522:  FieldDeclaration ::= ThisClauseopt FieldModifiersopt Type VariableDeclarators ;
            //
            case 522: {
                //#line 1424 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr ThisClauseopt = (DepParameterExpr) getRhsSym(1);
                //#line 1424 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Flags FieldModifiersopt = (Flags) getRhsSym(2);
                //#line 1424 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1424 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(4);
                //#line 1426 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                for (Iterator i = VariableDeclarators.iterator(); i.hasNext();)
                {
                    X10VarDeclarator d = (X10VarDeclarator) i.next();
                    if (d.hasExplodedVars())
                      // TODO: Report this exception correctly.
                      throw new Error("Field Declarations may not have exploded variables." + pos());
                    d.setFlag(FieldModifiersopt);
                    l.add(nf.FieldDecl(d.position(),
                                       ThisClauseopt,
                                       d.flags,
                                       nf.array(Type, Type.position(), d.dims),
                                       d.name,
                                       d.init));
                }
                setResult(l);
                break;
            }
     
            //
            // Rule 523:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt Dims ArrayInitializer
            //
            case 523: {
                //#line 1458 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1458 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(3);
                //#line 1458 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Integer Dims = (Integer) getRhsSym(4);
                //#line 1458 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                ArrayInit ArrayInitializer = (ArrayInit) getRhsSym(5);
                //#line 1460 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Dims.intValue(), ArrayInitializer));
                break;
            }
     
            //
            // Rule 524:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr Dims
            //
            case 524: {
                //#line 1464 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1464 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(3);
                //#line 1464 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr DimExpr = (Expr) getRhsSym(4);
                //#line 1464 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Integer Dims = (Integer) getRhsSym(5);
                //#line 1466 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Collections.singletonList(DimExpr), Dims.intValue()));
                break;
            }
     
            //
            // Rule 525:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr DimExprs Dimsopt
            //
            case 525: {
                //#line 1470 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1470 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(3);
                //#line 1470 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr DimExpr = (Expr) getRhsSym(4);
                //#line 1470 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List DimExprs = (List) getRhsSym(5);
                //#line 1470 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Integer Dimsopt = (Integer) getRhsSym(6);
                //#line 1472 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                l.addAll(DimExprs);
                setResult(nf.NewArray(pos(), ArrayBaseType, l, Dimsopt.intValue()));
                break;
            }
     
            //
            // Rule 526:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ]
            //
            case 526: {
                //#line 1479 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1479 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object Valueopt = (Object) getRhsSym(3);
                //#line 1479 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(4);
                //#line 1479 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(6);
                //#line 1481 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, null));
                break;
            }
     
            //
            // Rule 527:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression$distr ] Expression$initializer
            //
            case 527: {
                //#line 1484 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1484 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object Valueopt = (Object) getRhsSym(3);
                //#line 1484 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(4);
                //#line 1484 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr distr = (Expr) getRhsSym(6);
                //#line 1484 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr initializer = (Expr) getRhsSym(8);
                //#line 1486 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, distr, initializer));
                break;
            }
     
            //
            // Rule 528:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ] ($lparen FormalParameter ) MethodBody
            //
            case 528: {
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object Valueopt = (Object) getRhsSym(3);
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Object Unsafeopt = (Object) getRhsSym(4);
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(6);
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                IToken lparen = (IToken) getRhsIToken(8);
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(9);
                //#line 1489 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1491 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr initializer = makeInitializer( pos(getRhsFirstTokenIndex(8), getRightSpan()), ArrayBaseType, FormalParameter, MethodBody );
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, initializer));
                break;
            }
     
            //
            // Rule 529:  Valueopt ::= $Empty
            //
            case 529:
                setResult(null);
                break;
 
            //
            // Rule 530:  Valueopt ::= value
            //
            case 530: {
                
                //#line 1500 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 533:  ArrayBaseType ::= nullable < Type >
            //
            case 533: {
                //#line 1507 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1509 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Nullable(pos(), Type));
                break;
            }
     
            //
            // Rule 534:  ArrayBaseType ::= future < Type >
            //
            case 534: {
                //#line 1512 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1514 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Future(pos(), Type));
                break;
            }
     
            //
            // Rule 535:  ArrayBaseType ::= ( Type )
            //
            case 535: {
                //#line 1517 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1519 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(Type);
                break;
            }
     
            //
            // Rule 536:  ArrayAccess ::= ExpressionName [ ArgumentList ]
            //
            case 536: {
                //#line 1523 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name ExpressionName = (Name) getRhsSym(1);
                //#line 1523 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 1525 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), ExpressionName.toExpr(), (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), ExpressionName.toExpr(), ArgumentList));
                break;
            }
     
            //
            // Rule 537:  ArrayAccess ::= PrimaryNoNewArray [ ArgumentList ]
            //
            case 537: {
                //#line 1530 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr PrimaryNoNewArray = (Expr) getRhsSym(1);
                //#line 1530 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 1532 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), PrimaryNoNewArray, (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), PrimaryNoNewArray, ArgumentList));
                break;
            }
     
            //
            // Rule 554:  NowStatement ::= now ( Clock ) Statement
            //
            case 554: {
                //#line 1558 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1558 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1560 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
              setResult(nf.Now(pos(), Clock, Statement));
                break;
            }
     
            //
            // Rule 555:  ClockedClause ::= clocked ( ClockList )
            //
            case 555: {
                //#line 1564 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1566 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 556:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 556: {
                //#line 1570 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1570 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1570 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1572 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                break;
            }
     
            //
            // Rule 557:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 557: {
                //#line 1580 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1580 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1582 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
              setResult(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                               ? nf.Here(pos(getLeftSpan()))
                                               : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 558:  WhenStatement ::= when ( Expression ) Statement
            //
            case 558: {
                //#line 1589 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1589 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1591 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.When(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 559:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 559: {
                //#line 1594 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1594 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1594 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1594 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1596 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 560:  ForEachStatement ::= foreach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 560: {
                //#line 1601 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1601 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1601 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1601 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1603 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ForEach(pos(),
                              FormalParameter.flags(FormalParameter.flags().Final()),
                              Expression,
                              ClockedClauseopt,
                              Statement));
                break;
            }
     
            //
            // Rule 561:  AtEachStatement ::= ateach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 561: {
                //#line 1611 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1611 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1611 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1611 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1613 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.AtEach(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             ClockedClauseopt,
                             Statement));
                break;
            }
     
            //
            // Rule 562:  EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
            //
            case 562: {
                //#line 1621 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1621 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1621 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1623 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ForLoop(pos(),
                        FormalParameter.flags(FormalParameter.flags().Final()),
                        Expression,
                        Statement));
                break;
            }
     
            //
            // Rule 563:  FinishStatement ::= finish Statement
            //
            case 563: {
                //#line 1630 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1632 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Finish(pos(),  Statement));
                break;
            }
     
            //
            // Rule 564:  NowStatementNoShortIf ::= now ( Clock ) StatementNoShortIf
            //
            case 564: {
                //#line 1637 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1637 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1639 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Now(pos(), Clock, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 565:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt ClockedClauseopt StatementNoShortIf
            //
            case 565: {
                //#line 1643 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1643 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1643 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(4);
                //#line 1645 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(getLeftSpan()))
                                                : PlaceExpressionSingleListopt),
                                            ClockedClauseopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 566:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 566: {
                //#line 1652 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                //#line 1654 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 567:  WhenStatementNoShortIf ::= when ( Expression ) StatementNoShortIf
            //
            case 567: {
                //#line 1658 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1658 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                //#line 1660 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.When(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 568:  WhenStatementNoShortIf ::= WhenStatement or$or ( Expression ) StatementNoShortIf
            //
            case 568: {
                //#line 1663 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1663 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1663 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1663 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(6);
                //#line 1665 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, StatementNoShortIf);
                setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 569:  ForEachStatementNoShortIf ::= foreach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 569: {
                //#line 1670 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1670 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1670 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1670 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(8);
                //#line 1672 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.ForEach(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             ClockedClauseopt,
                             StatementNoShortIf));

                break;
            }
     
            //
            // Rule 570:  AtEachStatementNoShortIf ::= ateach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 570: {
                //#line 1681 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1681 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1681 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1681 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(8);
                //#line 1683 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.AtEach(pos(),
                            FormalParameter.flags(FormalParameter.flags().Final()),
                            Expression,
                            ClockedClauseopt,
                            StatementNoShortIf));
                break;
            }
     
            //
            // Rule 571:  EnhancedForStatementNoShortIf ::= for ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 571: {
                //#line 1691 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1691 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1691 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(7);
                //#line 1693 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                  setResult(nf.ForLoop(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             StatementNoShortIf));
                break;
            }
     
            //
            // Rule 572:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 572: {
                //#line 1700 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                //#line 1702 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Finish(pos(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 573:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 573: {
                //#line 1707 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1709 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
              setResult(PlaceExpression);
                break;
            }
     
            //
            // Rule 575:  NextStatement ::= next ;
            //
            case 575: {
                
                //#line 1717 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 576:  AwaitStatement ::= await Expression ;
            //
            case 576: {
                //#line 1721 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1723 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Await(pos(), Expression));
                break;
            }
     
            //
            // Rule 577:  ClockList ::= Clock
            //
            case 577: {
                //#line 1727 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1729 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                break;
            }
     
            //
            // Rule 578:  ClockList ::= ClockList , Clock
            //
            case 578: {
                //#line 1734 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1734 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1736 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                ClockList.add(Clock);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 579:  Clock ::= Expression
            //
            case 579: {
                //#line 1742 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1744 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
    setResult(Expression);
                break;
            }
     
            //
            // Rule 580:  CastExpression ::= ( Type ) UnaryExpressionNotPlusMinus
            //
            case 580: {
                //#line 1754 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1754 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(4);
                //#line 1756 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Cast(pos(), Type, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 581:  CastExpression ::= ( @ Expression ) UnaryExpressionNotPlusMinus
            //
            case 581: {
                //#line 1759 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1759 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(5);
                //#line 1761 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.PlaceCast(pos(), Expression, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 582:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 582: {
                //#line 1771 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 1771 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1773 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                break;
            }
     
            //
            // Rule 583:  IdentifierList ::= identifier
            //
            case 583: {
                //#line 1779 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1781 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List l = new TypedList(new LinkedList(), Name.class, false);
                l.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                setResult(l);
                break;
            }
     
            //
            // Rule 584:  IdentifierList ::= IdentifierList , identifier
            //
            case 584: {
                //#line 1786 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 1786 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                //#line 1788 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                IdentifierList.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                setResult(IdentifierList);
                break;
            }
     
            //
            // Rule 585:  Primary ::= here
            //
            case 585: {
                
                //#line 1795 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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
            // Rule 588:  RegionExpression ::= Expression$expr1 : Expression$expr2
            //
            case 588: {
                //#line 1811 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 1811 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 1813 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
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
            // Rule 589:  RegionExpressionList ::= RegionExpression
            //
            case 589: {
                //#line 1829 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 1831 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                break;
            }
     
            //
            // Rule 590:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 590: {
                //#line 1836 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 1836 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 1838 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                break;
            }
     
            //
            // Rule 591:  Primary ::= [ RegionExpressionList ]
            //
            case 591: {
                //#line 1843 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(2);
                //#line 1845 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");
                Name x10LangRegion = new Name(nf, ts, pos(), x10Lang, "region");
                Name x10LangRegionFactory = new Name(nf, ts, pos(), x10LangRegion, "factory");
                Name x10LangRegionFactoryRegion = new Name(nf, ts, pos(), x10LangRegionFactory, "region");
                Name x10LangPoint = new Name(nf, ts, pos(), x10Lang, "point");
                Name x10LangPointFactory = new Name(nf, ts, pos(), x10LangPoint, "factory");
                Name x10LangPointFactoryPoint = new Name(nf, ts, pos(), x10LangPointFactory, "point");

                Tuple tuple  = nf.Tuple(pos(), x10LangPointFactoryPoint, x10LangRegionFactoryRegion, RegionExpressionList);
                setResult(tuple);
                break;
            }
     
            //
            // Rule 592:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 592: {
                //#line 1859 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 1859 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 1861 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                ConstantDistMaker call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                break;
            }
     
            //
            // Rule 593:  FutureExpression ::= future PlaceExpressionSingleListopt { Expression }
            //
            case 593: {
                //#line 1866 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1866 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1868 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(nf.Future(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(getLeftSpan()))
                                                : PlaceExpressionSingleListopt), Expression));
                break;
            }
     
            //
            // Rule 594:  FieldModifier ::= mutable
            //
            case 594: {
                
                //#line 1876 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(X10Flags.MUTABLE);
                break;
            }
     
            //
            // Rule 595:  FieldModifier ::= const
            //
            case 595: {
                
                //#line 1881 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL));
                break;
            }
     
            //
            // Rule 596:  FunExpression ::= fun Type ( FormalParameterListopt ) { Expression }
            //
            case 596:
                throw new Error("No action specified for rule " + 596);
 
            //
            // Rule 597:  MethodInvocation ::= MethodName ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 597:
                throw new Error("No action specified for rule " + 597); 
 
            //
            // Rule 598:  MethodInvocation ::= Primary . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 598:
                throw new Error("No action specified for rule " + 598); 
 
            //
            // Rule 599:  MethodInvocation ::= super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 599:
                throw new Error("No action specified for rule " + 599); 
 
            //
            // Rule 600:  MethodInvocation ::= ClassName . super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 600:
                throw new Error("No action specified for rule " + 600); 
 
            //
            // Rule 601:  MethodInvocation ::= TypeName . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 601:
                throw new Error("No action specified for rule " + 601); 
 
            //
            // Rule 602:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 602:
                throw new Error("No action specified for rule " + 602); 
 
            //
            // Rule 603:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 603:
                throw new Error("No action specified for rule " + 603); 
 
            //
            // Rule 604:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 604:
                throw new Error("No action specified for rule " + 604); 
 
            //
            // Rule 605:  MethodModifier ::= synchronized
            //
            case 605: {
                //#line 1911 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "\"synchronized\" is an invalid X10 Method Modifier");
                setResult(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 606:  FieldModifier ::= volatile
            //
            case 606: {
                //#line 1920 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "\"volatile\" is an invalid X10 Field Modifier");
                setResult(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 607:  SynchronizedStatement ::= synchronized ( Expression ) Block
            //
            case 607: {
                //#line 1927 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1927 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1929 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "Synchronized Statement is invalid in X10");
                setResult(nf.Synchronized(pos(), Expression, Block));
                break;
            }
     
            //
            // Rule 608:  ThisClauseopt ::= $Empty
            //
            case 608:
                setResult(null);
                break;
 
            //
            // Rule 610:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 610:
                setResult(null);
                break;
 
            //
            // Rule 612:  DepParametersopt ::= $Empty
            //
            case 612:
                setResult(null);
                break;
 
            //
            // Rule 614:  PropertyListopt ::= $Empty
            //
            case 614:
                setResult(null);
                break;
 
            //
            // Rule 616:  WhereClauseopt ::= $Empty
            //
            case 616:
                setResult(null);
                break;
 
            //
            // Rule 618:  ObjectKindopt ::= $Empty
            //
            case 618:
                setResult(null);
                break;
 
            //
            // Rule 620:  ArrayInitializeropt ::= $Empty
            //
            case 620:
                setResult(null);
                break;
 
            //
            // Rule 622:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 622:
                setResult(null);
                break;
 
            //
            // Rule 624:  ArgumentListopt ::= $Empty
            //
            case 624:
                setResult(null);
                break;
 
            //
            // Rule 626:  X10ClassModifiersopt ::= $Empty
            //
            case 626: {
                
                //#line 1975 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
         setResult(X10Flags.toX10Flags(Flags.NONE));
                break;
            }  
            //
            // Rule 628:  DepParametersopt ::= $Empty
            //
            case 628:
                setResult(null);
                break;
 
            //
            // Rule 630:  Unsafeopt ::= $Empty
            //
            case 630:
                setResult(null);
                break;
 
            //
            // Rule 631:  Unsafeopt ::= unsafe
            //
            case 631: {
                
                //#line 1987 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 632:  ParamIdopt ::= $Empty
            //
            case 632:
                setResult(null);
                break;
 
            //
            // Rule 633:  ParamIdopt ::= identifier
            //
            case 633: {
                //#line 1994 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 1996 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 634:  ClockedClauseopt ::= $Empty
            //
            case 634: {
                
                //#line 2002 "C:/eclipse/ws6/x10.compiler/src/x10/parser/x10.g"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

