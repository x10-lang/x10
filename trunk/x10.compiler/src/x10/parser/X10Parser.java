//
// 12/25/2004
// This is the basic X10 grammar specification without support for generic types.
// Intended for the Feb 2005 X10 release.
////
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
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
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
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
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
    
    //
    // Report error message for given error_token.
    //
    public final void reportErrorTokenMessage(int error_token, String msg)
    {
        int firsttok = super.getFirstErrorToken(error_token),
            lasttok = super.getLastErrorToken(error_token);
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
            return (polyglot.ast.Node) btParser.parse(error_repair_count);
        }
        catch (BadParseException e)
        {
            reset(e.error_token); // point to error token
            DiagnoseParser diagnoseParser = new DiagnoseParser(this, prs);
            diagnoseParser.diagnose(e.error_token);
        }

        return null;
    }

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

    /** Pretend to have parsed new <T>Array.pointwiseOp
     * { public <T> apply(Formal) MethodBody }
     * instead of (Formal) MethodBody. Note that Formal may have
     * exploded vars.
     * @author vj
    */

    private New makeInitializer( Position pos, TypeNode resultType,
                                 X10Formal f, Block body ) {
      Flags flags = Flags.PUBLIC;
      // resulttype is a.
      List l1 = new TypedList(new LinkedList(), X10Formal.class, false);
      l1.add(f);
      TypeNode appResultType = resultType;
      if (!(resultType instanceof CanonicalTypeNode)) {
        Name x10 = new Name(nf, ts, pos, "x10");
        Name x10CG = new Name(nf, ts, pos, x10, "compilergenerated");
        Name x10CGP1 = new Name(nf, ts, pos, x10CG, "Parameter1");
        appResultType = x10CGP1.toType();
      }
      MethodDecl decl = nf.MethodDecl(pos, flags, appResultType,
                                    "apply", l1,
                                      new LinkedList(), body);
      //  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
      String prefix = !(resultType instanceof CanonicalTypeNode) ?
                        "generic" : resultType.toString();
      Name x10 = new Name(nf, ts, pos, "x10");
      Name x10Lang = new Name(nf, ts, pos, x10, "lang");
      Name tArray
          = new Name(nf, ts, pos, x10Lang, prefix + "Array");
      Name tXArray
      = new Name(nf, ts, pos, x10Lang, "x10.lang." + prefix + "Array");
      Name tArrayPointwiseOp = new Name(nf, ts, pos, tArray, "pointwiseOp");
      List classDecl = new TypedList(new LinkedList(), MethodDecl.class, false);
      classDecl.add( decl );
      TypeNode t = !(resultType instanceof CanonicalTypeNode) ?
               (TypeNode) nf.GenericArrayPointwiseOpTypeNode(pos, resultType) :
               (TypeNode) tArrayPointwiseOp.toType();

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
            // Rule 1:  identifier ::= IDENTIFIER$id
            //
            case 1: {
                IToken id = (IToken) getRhsIToken(1);
                setResult(id(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 3:  PrimitiveType ::= boolean
            //
            case 3: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Boolean()));
                break;
            }
     
            //
            // Rule 6:  IntegralType ::= byte
            //
            case 6: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Byte()));
                break;
            }
     
            //
            // Rule 7:  IntegralType ::= char
            //
            case 7: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Char()));
                break;
            }
     
            //
            // Rule 8:  IntegralType ::= short
            //
            case 8: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Short()));
                break;
            }
     
            //
            // Rule 9:  IntegralType ::= int
            //
            case 9: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Int()));
                break;
            }
     
            //
            // Rule 10:  IntegralType ::= long
            //
            case 10: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Long()));
                break;
            }
     
            //
            // Rule 11:  FloatingPointType ::= float
            //
            case 11: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Float()));
                break;
            }
     
            //
            // Rule 12:  FloatingPointType ::= double
            //
            case 12: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Double()));
                break;
            }
     
            //
            // Rule 15:  TypeName ::= identifier
            //
            case 15: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 16:  TypeName ::= TypeName . identifier
            //
            case 16: {
                Name TypeName = (Name) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 18:  ArrayType ::= Type [ ]
            //
            case 18: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                setResult(nf.array(Type, pos(), 1));
                break;
            }
     
            //
            // Rule 19:  PackageName ::= identifier
            //
            case 19: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 20:  PackageName ::= PackageName . identifier
            //
            case 20: {
                Name PackageName = (Name) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 21:  ExpressionName ::= identifier
            //
            case 21: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 22:  ExpressionName ::= AmbiguousName . identifier
            //
            case 22: {
                Name AmbiguousName = (Name) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 23:  MethodName ::= identifier
            //
            case 23: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 24:  MethodName ::= AmbiguousName . identifier
            //
            case 24: {
                Name AmbiguousName = (Name) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 25:  PackageOrTypeName ::= identifier
            //
            case 25: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 26:  PackageOrTypeName ::= PackageOrTypeName . identifier
            //
            case 26: {
                Name PackageOrTypeName = (Name) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 27:  AmbiguousName ::= identifier
            //
            case 27: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 28:  AmbiguousName ::= AmbiguousName . identifier
            //
            case 28: {
                Name AmbiguousName = (Name) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(new Name(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  identifier.getIdentifier()));
               break;
            }
     
            //
            // Rule 29:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 29: {
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                List ImportDeclarationsopt = (List) getRhsSym(2);
                List TypeDeclarationsopt = (List) getRhsSym(3);
                // Add import x10.lang.* by default.
                Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");
                Import x10LangImport = 
                nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, x10Lang.toString());
                ImportDeclarationsopt.add(x10LangImport);
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()), PackageDeclarationopt, ImportDeclarationsopt, TypeDeclarationsopt));
                break;
            }
     
            //
            // Rule 30:  ImportDeclarations ::= ImportDeclaration
            //
            case 30: {
                Import ImportDeclaration = (Import) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 31:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 31: {
                List ImportDeclarations = (List) getRhsSym(1);
                Import ImportDeclaration = (Import) getRhsSym(2);
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 32:  TypeDeclarations ::= TypeDeclaration
            //
            case 32: {
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 33:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 33: {
                List TypeDeclarations = (List) getRhsSym(1);
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(2);
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 36:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 36: {
                Name TypeName = (Name) getRhsSym(2);
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, TypeName.toString()));
                break;
            }
     
            //
            // Rule 37:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 37: {
                Name PackageOrTypeName = (Name) getRhsSym(2);
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, PackageOrTypeName.toString()));
                break;
            }
     
            //
            // Rule 40:  TypeDeclaration ::= ;
            //
            case 40: {
                
                setResult(null);
                break;
            }
     
            //
            // Rule 43:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 43: {
                Flags ClassModifiers = (Flags) getRhsSym(1);
                Flags ClassModifier = (Flags) getRhsSym(2);
                setResult(ClassModifiers.set(ClassModifier));
                break;
            }
     
            //
            // Rule 44:  ClassModifier ::= public
            //
            case 44: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 45:  ClassModifier ::= protected
            //
            case 45: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 46:  ClassModifier ::= private
            //
            case 46: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 47:  ClassModifier ::= abstract
            //
            case 47: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 48:  ClassModifier ::= static
            //
            case 48: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 49:  ClassModifier ::= final
            //
            case 49: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 50:  ClassModifier ::= strictfp
            //
            case 50: {
                
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 51:  Super ::= extends ClassType
            //
            case 51: {
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                setResult(ClassType);
                break;
            }
     
            //
            // Rule 52:  Interfaces ::= implements InterfaceTypeList
            //
            case 52: {
                List InterfaceTypeList = (List) getRhsSym(2);
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 53:  InterfaceTypeList ::= InterfaceType
            //
            case 53: {
                TypeNode InterfaceType = (TypeNode) getRhsSym(1);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 54:  InterfaceTypeList ::= InterfaceTypeList , InterfaceType
            //
            case 54: {
                List InterfaceTypeList = (List) getRhsSym(1);
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                InterfaceTypeList.add(InterfaceType);
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 55:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 55: {
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                break;
            }
     
            //
            // Rule 57:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 57: {
                List ClassBodyDeclarations = (List) getRhsSym(1);
                List ClassBodyDeclaration = (List) getRhsSym(2);
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                break;
            }
     
            //
            // Rule 59:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 59: {
                Block InstanceInitializer = (Block) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.NONE, InstanceInitializer));
                setResult(l);
                break;
            }
     
            //
            // Rule 60:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 60: {
                Block StaticInitializer = (Block) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.STATIC, StaticInitializer));
                setResult(l);
                break;
            }
     
            //
            // Rule 61:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 61: {
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 63:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 63: {
                MethodDecl MethodDeclaration = (MethodDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 64:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 64: {
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 65:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 65: {
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 66:  ClassMemberDeclaration ::= ;
            //
            case 66: {
                
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                break;
            }
     
            //
            // Rule 67:  FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators ;
            //
            case 67: {
                Flags FieldModifiersopt = (Flags) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(2);
                List VariableDeclarators = (List) getRhsSym(3);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                for (Iterator i = VariableDeclarators.iterator(); i.hasNext();)
                {
                    X10VarDeclarator d = (X10VarDeclarator) i.next();
                    if (d.hasExplodedVars())
                      // TODO: Report this exception correctly.
                      throw new Error("Field Declarations may not have exploded variables." + pos());
                    d.setFlag(FieldModifiersopt);
                    l.add(nf.FieldDecl(d.position(),
                                       d.flags,
                                       nf.array(Type, Type.position(), d.dims),
                                       d.name,
                                       d.init));
                }
                setResult(l);
                break;
            }
     
            //
            // Rule 68:  VariableDeclarators ::= VariableDeclarator
            //
            case 68: {
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(1);
                List l = new TypedList(new LinkedList(), X10VarDeclarator.class, false);
                l.add(VariableDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 69:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 69: {
                List VariableDeclarators = (List) getRhsSym(1);
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(3);
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                break;
            }
     
            //
            // Rule 71:  VariableDeclarator ::= VariableDeclaratorId = VariableInitializer
            //
            case 71: {
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                Expr VariableInitializer = (Expr) getRhsSym(3);
                VariableDeclaratorId.init = VariableInitializer;
                VariableDeclaratorId.position(pos());
                // setResult(VariableDeclaratorId); 
                break;
            }
     
            //
            // Rule 72:  TraditionalVariableDeclaratorId ::= identifier
            //
            case 72: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new X10VarDeclarator(pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 73:  TraditionalVariableDeclaratorId ::= TraditionalVariableDeclaratorId [ ]
            //
            case 73: {
                X10VarDeclarator TraditionalVariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                TraditionalVariableDeclaratorId.dims++;
                TraditionalVariableDeclaratorId.position(pos());
                // setResult(a);
                break;
            }
     
            //
            // Rule 75:  VariableDeclaratorId ::= identifier [ IdentifierList ]
            //
            case 75: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                List IdentifierList = (List) getRhsSym(3);
                setResult(new X10VarDeclarator(pos(), identifier.getIdentifier(), IdentifierList));
                break;
            }
     
            //
            // Rule 76:  VariableDeclaratorId ::= [ IdentifierList ]
            //
            case 76: {
                List IdentifierList = (List) getRhsSym(2);
                String name = polyglot.ext.x10.visit.X10PrettyPrinterVisitor.getId();
                setResult(new X10VarDeclarator(pos(), name, IdentifierList));
                break;
            }
     
            //
            // Rule 80:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 80: {
                Flags FieldModifiers = (Flags) getRhsSym(1);
                Flags FieldModifier = (Flags) getRhsSym(2);
                setResult(FieldModifiers.set(FieldModifier));
                break;
            }
     
            //
            // Rule 81:  FieldModifier ::= public
            //
            case 81: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 82:  FieldModifier ::= protected
            //
            case 82: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 83:  FieldModifier ::= private
            //
            case 83: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 84:  FieldModifier ::= static
            //
            case 84: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 85:  FieldModifier ::= final
            //
            case 85: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 86:  FieldModifier ::= transient
            //
            case 86: {
                
                setResult(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 87:  MethodDeclaration ::= MethodHeader MethodBody
            //
            case 87: {
                MethodDecl MethodHeader = (MethodDecl) getRhsSym(1);
                Block MethodBody = (Block) getRhsSym(2);
                List l = MethodHeader.formals();
                Flags f = MethodHeader.flags();
                if (f.contains(X10Flags.ATOMIC)) {
                     List ss = new TypedList(new LinkedList(), Stmt.class, false);
                     ss.add(nf.Atomic(pos(), nf.Here(pos()), MethodBody));
                     MethodBody = MethodBody.statements(ss);
                     MethodHeader = MethodHeader.flags(f.clear(X10Flags.ATOMIC));
                }
                setResult(MethodHeader.body(MethodBody));
                break;
            }
     
            //
            // Rule 89:  ResultType ::= void
            //
            case 89: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 90:  MethodDeclarator ::= identifier ( FormalParameterListopt )
            //
            case 90: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                List FormalParameterListopt = (List) getRhsSym(3);
                Object[] a = new Object[3];
                a[0] = new Name(nf, ts, pos(), identifier.getIdentifier());
                a[1] = FormalParameterListopt;
                a[2] = new Integer(0);
                setResult(a);
                break;
            }
     
            //
            // Rule 91:  MethodDeclarator ::= MethodDeclarator [ ]
            //
            case 91: {
                Object[] MethodDeclarator = (Object[]) getRhsSym(1);
                MethodDeclarator[2] = new Integer(((Integer) MethodDeclarator[2]).intValue() + 1);
                // setResult(MethodDeclarator);
                break;
            }
     
            //
            // Rule 92:  FormalParameterList ::= LastFormalParameter
            //
            case 92: {
                Formal LastFormalParameter = (Formal) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(LastFormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 93:  FormalParameterList ::= FormalParameters , LastFormalParameter
            //
            case 93: {
                List FormalParameters = (List) getRhsSym(1);
                Formal LastFormalParameter = (Formal) getRhsSym(3);
                FormalParameters.add(LastFormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 94:  FormalParameters ::= FormalParameter
            //
            case 94: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 95:  FormalParameters ::= FormalParameters , FormalParameter
            //
            case 95: {
                List FormalParameters = (List) getRhsSym(1);
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                FormalParameters.add(FormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 96:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 96: {
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(2);
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(3);
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names()));
                break;
            }
     
            //
            // Rule 98:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 98: {
                Flags VariableModifiers = (Flags) getRhsSym(1);
                Flags VariableModifier = (Flags) getRhsSym(2);
                setResult(VariableModifiers.set(VariableModifier));
                break;
            }
     
            //
            // Rule 99:  VariableModifier ::= final
            //
            case 99: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 100:  LastFormalParameter ::= VariableModifiersopt Type ...opt$opt VariableDeclaratorId
            //
            case 100: {
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(2);
                Object opt = (Object) getRhsSym(3);
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(4);
                assert(opt == null);
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names()));
                break;
            }
     
            //
            // Rule 102:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 102: {
                Flags MethodModifiers = (Flags) getRhsSym(1);
                Flags MethodModifier = (Flags) getRhsSym(2);
                setResult(MethodModifiers.set(MethodModifier));
                break;
            }
     
            //
            // Rule 103:  MethodModifier ::= public
            //
            case 103: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 104:  MethodModifier ::= protected
            //
            case 104: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 105:  MethodModifier ::= private
            //
            case 105: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 106:  MethodModifier ::= abstract
            //
            case 106: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 107:  MethodModifier ::= static
            //
            case 107: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 108:  MethodModifier ::= final
            //
            case 108: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 109:  MethodModifier ::= native
            //
            case 109: {
                
                setResult(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 110:  MethodModifier ::= strictfp
            //
            case 110: {
                
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 111:  Throws ::= throws ExceptionTypeList
            //
            case 111: {
                List ExceptionTypeList = (List) getRhsSym(2);
                setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 112:  ExceptionTypeList ::= ExceptionType
            //
            case 112: {
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                break;
            }
     
            //
            // Rule 113:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 113: {
                List ExceptionTypeList = (List) getRhsSym(1);
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                ExceptionTypeList.add(ExceptionType);
                // setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 116:  MethodBody ::= ;
            //
            case 116:
                setResult(null);
                break;
 
            //
            // Rule 118:  StaticInitializer ::= static Block
            //
            case 118: {
                Block Block = (Block) getRhsSym(2);
                setResult(Block);
                break;
            }
     
            //
            // Rule 119:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 119: {
                Flags ConstructorModifiersopt = (Flags) getRhsSym(1);
                Object[] ConstructorDeclarator = (Object[]) getRhsSym(2);
                List Throwsopt = (List) getRhsSym(3);
                Block ConstructorBody = (Block) getRhsSym(4);
                Name a = (Name) ConstructorDeclarator[1];
                List b = (List) ConstructorDeclarator[2];

                setResult(nf.ConstructorDecl(pos(), ConstructorModifiersopt, a.toString(), b, Throwsopt, ConstructorBody));
                break;
            }
     
            //
            // Rule 120:  SimpleTypeName ::= identifier
            //
            case 120: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 122:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 122: {
                Flags ConstructorModifiers = (Flags) getRhsSym(1);
                Flags ConstructorModifier = (Flags) getRhsSym(2);
                setResult(ConstructorModifiers.set(ConstructorModifier));
                break;
            }
     
            //
            // Rule 123:  ConstructorModifier ::= public
            //
            case 123: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 124:  ConstructorModifier ::= protected
            //
            case 124: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 125:  ConstructorModifier ::= private
            //
            case 125: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 126:  ConstructorBody ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 126: {
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                List BlockStatementsopt = (List) getRhsSym(3);
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
            // Rule 127:  Arguments ::= ( ArgumentListopt )
            //
            case 127: {
                List ArgumentListopt = (List) getRhsSym(2);
                setResult(ArgumentListopt);
                break;
            }
     
            //
            // Rule 130:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 130: {
                Flags InterfaceModifiers = (Flags) getRhsSym(1);
                Flags InterfaceModifier = (Flags) getRhsSym(2);
                setResult(InterfaceModifiers.set(InterfaceModifier));
                break;
            }
     
            //
            // Rule 131:  InterfaceModifier ::= public
            //
            case 131: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 132:  InterfaceModifier ::= protected
            //
            case 132: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 133:  InterfaceModifier ::= private
            //
            case 133: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 134:  InterfaceModifier ::= abstract
            //
            case 134: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 135:  InterfaceModifier ::= static
            //
            case 135: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 136:  InterfaceModifier ::= strictfp
            //
            case 136: {
                
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 137:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 137: {
                TypeNode InterfaceType = (TypeNode) getRhsSym(2);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 138:  ExtendsInterfaces ::= ExtendsInterfaces , InterfaceType
            //
            case 138: {
                List ExtendsInterfaces = (List) getRhsSym(1);
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                ExtendsInterfaces.add(InterfaceType);
                // setResult(ExtendsInterfaces);
                break;
            }
     
            //
            // Rule 139:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 139: {
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                break;
            }
     
            //
            // Rule 141:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 141: {
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                break;
            }
     
            //
            // Rule 143:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 143: {
                MethodDecl AbstractMethodDeclaration = (MethodDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(AbstractMethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 144:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 144: {
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 145:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 145: {
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 146:  InterfaceMemberDeclaration ::= ;
            //
            case 146: {
                
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 147:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 147: {
                Flags ConstantModifiersopt = (Flags) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(2);
                List VariableDeclarators = (List) getRhsSym(3);
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
            // Rule 149:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 149: {
                Flags ConstantModifiers = (Flags) getRhsSym(1);
                Flags ConstantModifier = (Flags) getRhsSym(2);
                setResult(ConstantModifiers.set(ConstantModifier));
                break;
            }
     
            //
            // Rule 150:  ConstantModifier ::= public
            //
            case 150: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 151:  ConstantModifier ::= static
            //
            case 151: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 152:  ConstantModifier ::= final
            //
            case 152: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 154:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 154: {
                Flags AbstractMethodModifiers = (Flags) getRhsSym(1);
                Flags AbstractMethodModifier = (Flags) getRhsSym(2);
                setResult(AbstractMethodModifiers.set(AbstractMethodModifier));
                break;
            }
     
            //
            // Rule 155:  AbstractMethodModifier ::= public
            //
            case 155: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 156:  AbstractMethodModifier ::= abstract
            //
            case 156: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 157:  SimpleName ::= identifier
            //
            case 157: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 158:  ArrayInitializer ::= { VariableInitializersopt ,opt$opt }
            //
            case 158: {
                List VariableInitializersopt = (List) getRhsSym(2);
                Object opt = (Object) getRhsSym(3);
                if (VariableInitializersopt == null)
                     setResult(nf.ArrayInit(pos()));
                else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
                break;
            }
     
            //
            // Rule 159:  VariableInitializers ::= VariableInitializer
            //
            case 159: {
                Expr VariableInitializer = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 160:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 160: {
                List VariableInitializers = (List) getRhsSym(1);
                Expr VariableInitializer = (Expr) getRhsSym(3);
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                break;
            }
     
            //
            // Rule 161:  Block ::= { BlockStatementsopt }
            //
            case 161: {
                List BlockStatementsopt = (List) getRhsSym(2);
                setResult(nf.Block(pos(), BlockStatementsopt));
                break;
            }
     
            //
            // Rule 162:  BlockStatements ::= BlockStatement
            //
            case 162: {
                List BlockStatement = (List) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                break;
            }
     
            //
            // Rule 163:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 163: {
                List BlockStatements = (List) getRhsSym(1);
                List BlockStatement = (List) getRhsSym(2);
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                break;
            }
     
            //
            // Rule 165:  BlockStatement ::= ClassDeclaration
            //
            case 165: {
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                break;
            }
     
            //
            // Rule 166:  BlockStatement ::= Statement
            //
            case 166: {
                Stmt Statement = (Stmt) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                break;
            }
     
            //
            // Rule 168:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 168: {
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(2);
                List VariableDeclarators = (List) getRhsSym(3);
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
            // Rule 192:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 192: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
                setResult(nf.If(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 193:  IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
            //
            case 193: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                Stmt Statement = (Stmt) getRhsSym(7);
                setResult(nf.If(pos(), Expression, StatementNoShortIf, Statement));
                break;
            }
     
            //
            // Rule 194:  IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf$true_stmt else StatementNoShortIf$false_stmt
            //
            case 194: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt true_stmt = (Stmt) getRhsSym(5);
                Stmt false_stmt = (Stmt) getRhsSym(7);
                setResult(nf.If(pos(), Expression, true_stmt, false_stmt));
                break;
            }
     
            //
            // Rule 195:  EmptyStatement ::= ;
            //
            case 195: {
                
                setResult(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 196:  LabeledStatement ::= identifier : Statement
            //
            case 196: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                Stmt Statement = (Stmt) getRhsSym(3);
                setResult(nf.Labeled(pos(), identifier.getIdentifier(), Statement));
                break;
            }
     
            //
            // Rule 197:  LabeledStatementNoShortIf ::= identifier : StatementNoShortIf
            //
            case 197: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(3);
                setResult(nf.Labeled(pos(), identifier.getIdentifier(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 198:  ExpressionStatement ::= StatementExpression ;
            //
            case 198: {
                Expr StatementExpression = (Expr) getRhsSym(1);
                setResult(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 206:  AssertStatement ::= assert Expression ;
            //
            case 206: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.Assert(pos(), Expression));
                break;
            }
     
            //
            // Rule 207:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 207: {
                Expr expr1 = (Expr) getRhsSym(2);
                Expr expr2 = (Expr) getRhsSym(4);
                setResult(nf.Assert(pos(), expr1, expr2));
                break;
            }
     
            //
            // Rule 208:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 208: {
                Expr Expression = (Expr) getRhsSym(3);
                List SwitchBlock = (List) getRhsSym(5);
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                break;
            }
     
            //
            // Rule 209:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 209: {
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                List SwitchLabelsopt = (List) getRhsSym(3);
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                break;
            }
     
            //
            // Rule 211:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 211: {
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                break;
            }
     
            //
            // Rule 212:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 212: {
                List SwitchLabels = (List) getRhsSym(1);
                List BlockStatements = (List) getRhsSym(2);
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                break;
            }
     
            //
            // Rule 213:  SwitchLabels ::= SwitchLabel
            //
            case 213: {
                Case SwitchLabel = (Case) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                break;
            }
     
            //
            // Rule 214:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 214: {
                List SwitchLabels = (List) getRhsSym(1);
                Case SwitchLabel = (Case) getRhsSym(2);
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                break;
            }
     
            //
            // Rule 215:  SwitchLabel ::= case ConstantExpression :
            //
            case 215: {
                Expr ConstantExpression = (Expr) getRhsSym(2);
                setResult(nf.Case(pos(), ConstantExpression));
                break;
            }
     
            //
            // Rule 216:  SwitchLabel ::= default :
            //
            case 216: {
                
                setResult(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 217:  WhileStatement ::= while ( Expression ) Statement
            //
            case 217: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
                setResult(nf.While(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 218:  WhileStatementNoShortIf ::= while ( Expression ) StatementNoShortIf
            //
            case 218: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                setResult(nf.While(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 219:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 219: {
                Stmt Statement = (Stmt) getRhsSym(2);
                Expr Expression = (Expr) getRhsSym(5);
                setResult(nf.Do(pos(), Statement, Expression));
                break;
            }
     
            //
            // Rule 222:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 222: {
                List ForInitopt = (List) getRhsSym(3);
                Expr Expressionopt = (Expr) getRhsSym(5);
                List ForUpdateopt = (List) getRhsSym(7);
                Stmt Statement = (Stmt) getRhsSym(9);
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                break;
            }
     
            //
            // Rule 223:  ForStatementNoShortIf ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) StatementNoShortIf
            //
            case 223: {
                List ForInitopt = (List) getRhsSym(3);
                Expr Expressionopt = (Expr) getRhsSym(5);
                List ForUpdateopt = (List) getRhsSym(7);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(9);
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 225:  ForInit ::= LocalVariableDeclaration
            //
            case 225: {
                List LocalVariableDeclaration = (List) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 227:  StatementExpressionList ::= StatementExpression
            //
            case 227: {
                Expr StatementExpression = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                break;
            }
     
            //
            // Rule 228:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 228: {
                List StatementExpressionList = (List) getRhsSym(1);
                Expr StatementExpression = (Expr) getRhsSym(3);
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                //setResult(StatementExpressionList);
                break;
            }
     
            //
            // Rule 229:  BreakStatement ::= break identifieropt ;
            //
            case 229: {
                Name identifieropt = (Name) getRhsSym(2);
                if (identifieropt == null)
                     setResult(nf.Break(pos()));
                else setResult(nf.Break(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 230:  ContinueStatement ::= continue identifieropt ;
            //
            case 230: {
                Name identifieropt = (Name) getRhsSym(2);
                if (identifieropt == null)
                     setResult(nf.Continue(pos()));
                else setResult(nf.Continue(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 231:  ReturnStatement ::= return Expressionopt ;
            //
            case 231: {
                Expr Expressionopt = (Expr) getRhsSym(2);
                setResult(nf.Return(pos(), Expressionopt));
                break;
            }
     
            //
            // Rule 232:  ThrowStatement ::= throw Expression ;
            //
            case 232: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.Throw(pos(), Expression));
                break;
            }
     
            //
            // Rule 233:  TryStatement ::= try Block Catches
            //
            case 233: {
                Block Block = (Block) getRhsSym(2);
                List Catches = (List) getRhsSym(3);
                setResult(nf.Try(pos(), Block, Catches));
                break;
            }
     
            //
            // Rule 234:  TryStatement ::= try Block Catchesopt Finally
            //
            case 234: {
                Block Block = (Block) getRhsSym(2);
                List Catchesopt = (List) getRhsSym(3);
                Block Finally = (Block) getRhsSym(4);
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                break;
            }
     
            //
            // Rule 235:  Catches ::= CatchClause
            //
            case 235: {
                Catch CatchClause = (Catch) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                break;
            }
     
            //
            // Rule 236:  Catches ::= Catches CatchClause
            //
            case 236: {
                List Catches = (List) getRhsSym(1);
                Catch CatchClause = (Catch) getRhsSym(2);
                Catches.add(CatchClause);
                //setResult(Catches);
                break;
            }
     
            //
            // Rule 237:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 237: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                Block Block = (Block) getRhsSym(5);
                setResult(nf.Catch(pos(), FormalParameter, Block));
                break;
            }
     
            //
            // Rule 238:  Finally ::= finally Block
            //
            case 238: {
                Block Block = (Block) getRhsSym(2);
                setResult(Block);
                break;
            }
     
            //
            // Rule 242:  PrimaryNoNewArray ::= Type . class
            //
            case 242: {
                TypeNode Type = (TypeNode) getRhsSym(1);
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
            // Rule 243:  PrimaryNoNewArray ::= void . class
            //
            case 243: {
                
                setResult(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(getLeftSpan()), ts.Void())));
                break;
            }
     
            //
            // Rule 244:  PrimaryNoNewArray ::= this
            //
            case 244: {
                
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 245:  PrimaryNoNewArray ::= ClassName . this
            //
            case 245: {
                Name ClassName = (Name) getRhsSym(1);
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 246:  PrimaryNoNewArray ::= ( Expression )
            //
            case 246: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.ParExpr(pos(), Expression));
                break;
            }
     
            //
            // Rule 251:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 251: {
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.IntegerLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 252:  Literal ::= LongLiteral$LongLiteral
            //
            case 252: {
                IToken LongLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 253:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 253: {
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 254:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 254: {
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 255:  Literal ::= BooleanLiteral
            //
            case 255: {
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 256:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 256: {
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 257:  Literal ::= StringLiteral$str
            //
            case 257: {
                IToken str = (IToken) getRhsIToken(1);
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 258:  Literal ::= null
            //
            case 258: {
                
                setResult(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 259:  BooleanLiteral ::= true$trueLiteral
            //
            case 259: {
                IToken trueLiteral = (IToken) getRhsIToken(1);
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 260:  BooleanLiteral ::= false$falseLiteral
            //
            case 260: {
                IToken falseLiteral = (IToken) getRhsIToken(1);
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 261:  ArgumentList ::= Expression
            //
            case 261: {
                Expr Expression = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                break;
            }
     
            //
            // Rule 262:  ArgumentList ::= ArgumentList , Expression
            //
            case 262: {
                List ArgumentList = (List) getRhsSym(1);
                Expr Expression = (Expr) getRhsSym(3);
                ArgumentList.add(Expression);
                //setResult(ArgumentList);
                break;
            }
     
            //
            // Rule 263:  DimExprs ::= DimExpr
            //
            case 263: {
                Expr DimExpr = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                setResult(l);
                break;
            }
     
            //
            // Rule 264:  DimExprs ::= DimExprs DimExpr
            //
            case 264: {
                List DimExprs = (List) getRhsSym(1);
                Expr DimExpr = (Expr) getRhsSym(2);
                DimExprs.add(DimExpr);
                //setResult(DimExprs);
                break;
            }
     
            //
            // Rule 265:  DimExpr ::= [ Expression ]
            //
            case 265: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(Expression.position(pos()));
                break;
            }
     
            //
            // Rule 266:  Dims ::= [ ]
            //
            case 266: {
                
                setResult(new Integer(1));
                break;
            }
     
            //
            // Rule 267:  Dims ::= Dims [ ]
            //
            case 267: {
                Integer Dims = (Integer) getRhsSym(1);
                setResult(new Integer(Dims.intValue() + 1));
                break;
            }
     
            //
            // Rule 268:  FieldAccess ::= Primary . identifier
            //
            case 268: {
                Expr Primary = (Expr) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(nf.Field(pos(), Primary, identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 269:  FieldAccess ::= super . identifier
            //
            case 269: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 270:  FieldAccess ::= ClassName . super$sup . identifier
            //
            case 270: {
                Name ClassName = (Name) getRhsSym(1);
                IToken sup = (IToken) getRhsIToken(3);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 271:  MethodInvocation ::= MethodName ( ArgumentListopt )
            //
            case 271: {
                Name MethodName = (Name) getRhsSym(1);
                List ArgumentListopt = (List) getRhsSym(3);
                setResult(nf.Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, ArgumentListopt));
                break;
            }
     
            //
            // Rule 273:  PostfixExpression ::= ExpressionName
            //
            case 273: {
                Name ExpressionName = (Name) getRhsSym(1);
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 276:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 276: {
                Expr PostfixExpression = (Expr) getRhsSym(1);
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 277:  PostDecrementExpression ::= PostfixExpression --
            //
            case 277: {
                Expr PostfixExpression = (Expr) getRhsSym(1);
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 280:  UnaryExpression ::= + UnaryExpression
            //
            case 280: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpression));
                break;
            }
     
            //
            // Rule 281:  UnaryExpression ::= - UnaryExpression
            //
            case 281: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpression));
                break;
            }
     
            //
            // Rule 283:  PreIncrementExpression ::= ++ UnaryExpression
            //
            case 283: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpression));
                break;
            }
     
            //
            // Rule 284:  PreDecrementExpression ::= -- UnaryExpression
            //
            case 284: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpression));
                break;
            }
     
            //
            // Rule 286:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 286: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 287:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 287: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 290:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 290: {
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                Expr UnaryExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                break;
            }
     
            //
            // Rule 291:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 291: {
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                Expr UnaryExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                break;
            }
     
            //
            // Rule 292:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 292: {
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                Expr UnaryExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                break;
            }
     
            //
            // Rule 294:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 294: {
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 295:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 295: {
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 297:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 297: {
                Expr ShiftExpression = (Expr) getRhsSym(1);
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                break;
            }
     
            //
            // Rule 298:  ShiftExpression ::= ShiftExpression > > AdditiveExpression
            //
            case 298: {
                Expr ShiftExpression = (Expr) getRhsSym(1);
                Expr AdditiveExpression = (Expr) getRhsSym(4);
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 299:  ShiftExpression ::= ShiftExpression > > > AdditiveExpression
            //
            case 299: {
                Expr ShiftExpression = (Expr) getRhsSym(1);
                Expr AdditiveExpression = (Expr) getRhsSym(5);
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 301:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 301: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
                break;
            }
     
            //
            // Rule 302:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 302: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
                break;
            }
     
            //
            // Rule 303:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 303: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
                break;
            }
     
            //
            // Rule 304:  RelationalExpression ::= RelationalExpression > = ShiftExpression
            //
            case 304: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(4);
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
                break;
            }
     
            //
            // Rule 306:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 306: {
                Expr EqualityExpression = (Expr) getRhsSym(1);
                Expr RelationalExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                break;
            }
     
            //
            // Rule 307:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 307: {
                Expr EqualityExpression = (Expr) getRhsSym(1);
                Expr RelationalExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                break;
            }
     
            //
            // Rule 309:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 309: {
                Expr AndExpression = (Expr) getRhsSym(1);
                Expr EqualityExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                break;
            }
     
            //
            // Rule 311:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 311: {
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                Expr AndExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                break;
            }
     
            //
            // Rule 313:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 313: {
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 315:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 315: {
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                break;
            }
     
            //
            // Rule 317:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 317: {
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                break;
            }
     
            //
            // Rule 319:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 319: {
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                Expr Expression = (Expr) getRhsSym(3);
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 322:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 322: {
                Expr LeftHandSide = (Expr) getRhsSym(1);
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 323:  LeftHandSide ::= ExpressionName
            //
            case 323: {
                Name ExpressionName = (Name) getRhsSym(1);
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 326:  AssignmentOperator ::= =
            //
            case 326: {
                
                setResult(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 327:  AssignmentOperator ::= *=
            //
            case 327: {
                
                setResult(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 328:  AssignmentOperator ::= /=
            //
            case 328: {
                
                setResult(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 329:  AssignmentOperator ::= %=
            //
            case 329: {
                
                setResult(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 330:  AssignmentOperator ::= +=
            //
            case 330: {
                
                setResult(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 331:  AssignmentOperator ::= -=
            //
            case 331: {
                
                setResult(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 332:  AssignmentOperator ::= <<=
            //
            case 332: {
                
                setResult(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 333:  AssignmentOperator ::= > > =
            //
            case 333: {
                
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 334:  AssignmentOperator ::= > > > =
            //
            case 334: {
                
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 335:  AssignmentOperator ::= &=
            //
            case 335: {
                
                setResult(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 336:  AssignmentOperator ::= ^=
            //
            case 336: {
                
                setResult(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 337:  AssignmentOperator ::= |=
            //
            case 337: {
                
                setResult(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 340:  Dimsopt ::= $Empty
            //
            case 340: {
                
                setResult(new Integer(0));
                break;
            }
     
            //
            // Rule 342:  Catchesopt ::= $Empty
            //
            case 342: {
                
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 344:  identifieropt ::= $Empty
            //
            case 344:
                setResult(null);
                break;
 
            //
            // Rule 345:  identifieropt ::= identifier
            //
            case 345: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 346:  ForUpdateopt ::= $Empty
            //
            case 346: {
                
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 348:  Expressionopt ::= $Empty
            //
            case 348:
                setResult(null);
                break;
 
            //
            // Rule 350:  ForInitopt ::= $Empty
            //
            case 350: {
                
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 352:  SwitchLabelsopt ::= $Empty
            //
            case 352: {
                
                setResult(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 354:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 354: {
                
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 356:  VariableModifiersopt ::= $Empty
            //
            case 356: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 358:  VariableInitializersopt ::= $Empty
            //
            case 358:
                setResult(null);
                break;
 
            //
            // Rule 360:  AbstractMethodModifiersopt ::= $Empty
            //
            case 360: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 362:  ConstantModifiersopt ::= $Empty
            //
            case 362: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 364:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 364: {
                
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 366:  ExtendsInterfacesopt ::= $Empty
            //
            case 366: {
                
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 368:  InterfaceModifiersopt ::= $Empty
            //
            case 368: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 370:  ClassBodyopt ::= $Empty
            //
            case 370:
                setResult(null);
                break;
 
            //
            // Rule 372:  Argumentsopt ::= $Empty
            //
            case 372:
                setResult(null);
                break;
 
            //
            // Rule 373:  Argumentsopt ::= Arguments
            //
            case 373:
                throw new Error("No action specified for rule " + 373);
 
            //
            // Rule 374:  ,opt ::= $Empty
            //
            case 374:
                setResult(null);
                break;
 
            //
            // Rule 376:  ArgumentListopt ::= $Empty
            //
            case 376: {
                
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 378:  BlockStatementsopt ::= $Empty
            //
            case 378: {
                
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 380:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 380:
                setResult(null);
                break;
 
            //
            // Rule 382:  ConstructorModifiersopt ::= $Empty
            //
            case 382: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 384:  ...opt ::= $Empty
            //
            case 384:
                setResult(null);
                break;
 
            //
            // Rule 386:  FormalParameterListopt ::= $Empty
            //
            case 386: {
                
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 388:  Throwsopt ::= $Empty
            //
            case 388: {
                
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 390:  MethodModifiersopt ::= $Empty
            //
            case 390: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 392:  FieldModifiersopt ::= $Empty
            //
            case 392: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 394:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 394: {
                
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 396:  Interfacesopt ::= $Empty
            //
            case 396: {
                
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 398:  Superopt ::= $Empty
            //
            case 398: {
                
               setResult(new Name(nf, ts, pos(), "x10.lang.Object").toType());
                break;
            }
     
            //
            // Rule 400:  ClassModifiersopt ::= $Empty
            //
            case 400: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 402:  TypeDeclarationsopt ::= $Empty
            //
            case 402: {
                
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 404:  ImportDeclarationsopt ::= $Empty
            //
            case 404: {
                
                setResult(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 406:  PackageDeclarationopt ::= $Empty
            //
            case 406:
                setResult(null);
                break;
 
            //
            // Rule 408:  ClassType ::= TypeName
            //
            case 408: {
                Name TypeName = (Name) getRhsSym(1);
                setResult(TypeName.toType());
                break;
            }
     
            //
            // Rule 409:  InterfaceType ::= TypeName
            //
            case 409: {
                Name TypeName = (Name) getRhsSym(1);
                setResult(TypeName.toType());
                break;
            }
     
            //
            // Rule 410:  PackageDeclaration ::= package PackageName ;
            //
            case 410: {
                Name PackageName = (Name) getRhsSym(2);
                setResult(PackageName.toPackage());
                break;
            }
     
            //
            // Rule 411:  NormalClassDeclaration ::= ClassModifiersopt class identifier Superopt Interfacesopt ClassBody
            //
            case 411: {
                Flags ClassModifiersopt = (Flags) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                TypeNode Superopt = (TypeNode) getRhsSym(4);
                List Interfacesopt = (List) getRhsSym(5);
                ClassBody ClassBody = (ClassBody) getRhsSym(6);
                checkTypeName(identifier);
                setResult(X10Flags.isValue(ClassModifiersopt)
                             ? nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                                                 ClassModifiersopt, identifier.getIdentifier(), Superopt, Interfacesopt, ClassBody)
                             : nf.ClassDecl(pos(getLeftSpan(), getRightSpan()),
                                            ClassModifiersopt, identifier.getIdentifier(), Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 412:  MethodHeader ::= MethodModifiersopt ResultType MethodDeclarator Throwsopt
            //
            case 412: {
                Flags MethodModifiersopt = (Flags) getRhsSym(1);
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                Object[] MethodDeclarator = (Object[]) getRhsSym(3);
                List Throwsopt = (List) getRhsSym(4);
                Name c = (Name) MethodDeclarator[0];
                List d = (List) MethodDeclarator[1];
                Integer e = (Integer) MethodDeclarator[2];

                if (ResultType.type() == ts.Void() && e.intValue() > 0)
                {
                    // TODO: error!!!
                    assert(false);
                }

                setResult(nf.MethodDecl(pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(3)),
                                       MethodModifiersopt,
                                       nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), e.intValue()),
                                       c.toString(),
                                       d,
                                       Throwsopt,
                                       null));
                break;
            }
     
            //
            // Rule 413:  ConstructorDeclarator ::= SimpleTypeName ( FormalParameterListopt )
            //
            case 413: {
                Name SimpleTypeName = (Name) getRhsSym(1);
                List FormalParameterListopt = (List) getRhsSym(3);
                Object[] a = new Object[3];
                a[1] = SimpleTypeName;
                a[2] = FormalParameterListopt;
                setResult(a);
                break;
            }
     
            //
            // Rule 414:  ExplicitConstructorInvocation ::= this ( ArgumentListopt ) ;
            //
            case 414: {
                List ArgumentListopt = (List) getRhsSym(3);
                setResult(nf.ThisCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 415:  ExplicitConstructorInvocation ::= super ( ArgumentListopt ) ;
            //
            case 415: {
                List ArgumentListopt = (List) getRhsSym(3);
                setResult(nf.SuperCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 416:  ExplicitConstructorInvocation ::= Primary . this ( ArgumentListopt ) ;
            //
            case 416: {
                Expr Primary = (Expr) getRhsSym(1);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.ThisCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 417:  ExplicitConstructorInvocation ::= Primary . super ( ArgumentListopt ) ;
            //
            case 417: {
                Expr Primary = (Expr) getRhsSym(1);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.SuperCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 418:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier ExtendsInterfacesopt InterfaceBody
            //
            case 418: {
                Flags InterfaceModifiersopt = (Flags) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                List ExtendsInterfacesopt = (List) getRhsSym(4);
                ClassBody InterfaceBody = (ClassBody) getRhsSym(5);
                checkTypeName(identifier);
                setResult(nf.ClassDecl(pos(),
                                    InterfaceModifiersopt.Interface(),
                                    identifier.getIdentifier(),
                                    null,
                                    ExtendsInterfacesopt,
                                    InterfaceBody));
                break;
            }
     
            //
            // Rule 419:  AbstractMethodDeclaration ::= AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt ;
            //
            case 419: {
                Flags AbstractMethodModifiersopt = (Flags) getRhsSym(1);
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                Object[] MethodDeclarator = (Object[]) getRhsSym(3);
                List Throwsopt = (List) getRhsSym(4);
                Name c = (Name) MethodDeclarator[0];
                List d = (List) MethodDeclarator[1];
                Integer e = (Integer) MethodDeclarator[2];

                if (ResultType.type() == ts.Void() && e.intValue() > 0)
                {
                    // TODO: error!!!
                    assert(false);
                }

                setResult(nf.MethodDecl(pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(3)),
                                       AbstractMethodModifiersopt ,
                                       nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), e.intValue()),
                                       c.toString(),
                                       d,
                                       Throwsopt,
                                       null));
                break;
            }
     
            //
            // Rule 420:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
            //
            case 420: {
                TypeNode ClassOrInterfaceType = (TypeNode) getRhsSym(2);
                List ArgumentListopt = (List) getRhsSym(4);
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(6);
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt));
                else setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 421:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 421: {
                Expr Primary = (Expr) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                List ArgumentListopt = (List) getRhsSym(6);
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(8);
                Name b = new Name(nf, ts, pos(), identifier.getIdentifier());
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), Primary, b.toType(), ArgumentListopt));
                else setResult(nf.New(pos(), Primary, b.toType(), ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 422:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 422: {
                Name AmbiguousName = (Name) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                List ArgumentListopt = (List) getRhsSym(6);
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(8);
                Name b = new Name(nf, ts, pos(), identifier.getIdentifier());
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt));
                else setResult(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 423:  MethodInvocation ::= Primary . identifier ( ArgumentListopt )
            //
            case 423: {
                Expr Primary = (Expr) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.Call(pos(), Primary, identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 424:  MethodInvocation ::= super . identifier ( ArgumentListopt )
            //
            case 424: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 425:  MethodInvocation ::= ClassName . super$sup . identifier ( ArgumentListopt )
            //
            case 425: {
                Name ClassName = (Name) getRhsSym(1);
                IToken sup = (IToken) getRhsIToken(3);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                List ArgumentListopt = (List) getRhsSym(7);
                setResult(nf.Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 426:  Type ::= DataType PlaceTypeSpecifieropt
            //
            case 426: {
                TypeNode DataType = (TypeNode) getRhsSym(1);
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(2);
                // Just parse the placetype and drop it for now.
                break;
            }
     
            //
            // Rule 427:  Type ::= nullable Type
            //
            case 427: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                setResult(nf.Nullable(pos(), Type));
                break;
            }
     
            //
            // Rule 428:  Type ::= future < Type >
            //
            case 428: {
                TypeNode Type = (TypeNode) getRhsSym(3);
                setResult(nf.Future(pos(), Type));
                break;
            }
     
            //
            // Rule 429:  Type ::= ( Type )
            //
            case 429: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                setResult(Type);
                break;
            }
     
            //
            // Rule 438:  ClassOrInterfaceType ::= TypeName PlaceTypeSpecifieropt DepParametersopt
            //
            case 438: {
                Name TypeName = (Name) getRhsSym(1);
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(2);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(3);
                setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : nf.ParametricTypeNode(pos(), TypeName.toType(), null, DepParametersopt));
                break;
            }
     
            //
            // Rule 439:  DepParameters ::= ( DepParameterExpr )
            //
            case 439: {
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(2);
                setResult(DepParameterExpr);
                break;
            }
     
            //
            // Rule 440:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 440: {
                List ArgumentList = (List) getRhsSym(1);
                Expr WhereClauseopt = (Expr) getRhsSym(2);
                setResult(nf.DepParameterExpr(pos(), ArgumentList, WhereClauseopt));
                break;
            }
     
            //
            // Rule 441:  DepParameterExpr ::= WhereClause
            //
            case 441: {
                Expr WhereClause = (Expr) getRhsSym(1);
                setResult(nf.DepParameterExpr(pos(), null, WhereClause));
                break;
            }
     
            //
            // Rule 442:  WhereClause ::= : Expression
            //
            case 442: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(Expression);
                break;
            }
     
            //
            // Rule 444:  X10ArrayType ::= Type [ . ]
            //
            case 444: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, null));
                break;
            }
     
            //
            // Rule 445:  X10ArrayType ::= Type reference [ . ]
            //
            case 445: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, null));
                break;
            }
     
            //
            // Rule 446:  X10ArrayType ::= Type value [ . ]
            //
            case 446: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, null));
                break;
            }
     
            //
            // Rule 447:  X10ArrayType ::= Type [ DepParameterExpr ]
            //
            case 447: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(3);
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
                break;
            }
     
            //
            // Rule 448:  X10ArrayType ::= Type reference [ DepParameterExpr ]
            //
            case 448: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(4);
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
                break;
            }
     
            //
            // Rule 449:  X10ArrayType ::= Type value [ DepParameterExpr ]
            //
            case 449: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(4);
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, DepParameterExpr));
                break;
            }
     
            //
            // Rule 450:  ObjectKind ::= value
            //
            case 450:
                throw new Error("No action specified for rule " + 450);
 
            //
            // Rule 451:  ObjectKind ::= reference
            //
            case 451:
                throw new Error("No action specified for rule " + 451);
 
            //
            // Rule 452:  MethodModifier ::= atomic
            //
            case 452: {
                
                setResult(X10Flags.ATOMIC);
                break;
            }
     
            //
            // Rule 453:  MethodModifier ::= extern
            //
            case 453: {
                
                setResult(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 455:  ValueClassDeclaration ::= ClassModifiersopt value identifier Superopt Interfacesopt ClassBody
            //
            case 455: {
                Flags ClassModifiersopt = (Flags) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                TypeNode Superopt = (TypeNode) getRhsSym(4);
                List Interfacesopt = (List) getRhsSym(5);
                ClassBody ClassBody = (ClassBody) getRhsSym(6);
                checkTypeName(identifier);
                setResult(nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                                             ClassModifiersopt, identifier.getIdentifier(), Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 456:  ValueClassDeclaration ::= ClassModifiersopt value class identifier Superopt Interfacesopt ClassBody
            //
            case 456: {
                Flags ClassModifiersopt = (Flags) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                TypeNode Superopt = (TypeNode) getRhsSym(5);
                List Interfacesopt = (List) getRhsSym(6);
                ClassBody ClassBody = (ClassBody) getRhsSym(7);
                checkTypeName(identifier);
                setResult(nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                                            ClassModifiersopt, identifier.getIdentifier(), Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 457:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt Dims ArrayInitializer
            //
            case 457: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Unsafeopt = (Object) getRhsSym(3);
                Integer Dims = (Integer) getRhsSym(4);
                ArrayInit ArrayInitializer = (ArrayInit) getRhsSym(5);
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Dims.intValue(), ArrayInitializer));
                break;
            }
     
            //
            // Rule 458:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr Dims
            //
            case 458: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Unsafeopt = (Object) getRhsSym(3);
                Expr DimExpr = (Expr) getRhsSym(4);
                Integer Dims = (Integer) getRhsSym(5);
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Collections.singletonList(DimExpr), Dims.intValue()));
                break;
            }
     
            //
            // Rule 459:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr DimExprs Dimsopt
            //
            case 459: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Unsafeopt = (Object) getRhsSym(3);
                Expr DimExpr = (Expr) getRhsSym(4);
                List DimExprs = (List) getRhsSym(5);
                Integer Dimsopt = (Integer) getRhsSym(6);
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                l.addAll(DimExprs);
                setResult(nf.NewArray(pos(), ArrayBaseType, l, Dimsopt.intValue()));
                break;
            }
     
            //
            // Rule 460:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ]
            //
            case 460: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Valueopt = (Object) getRhsSym(3);
                Object Unsafeopt = (Object) getRhsSym(4);
                Expr Expression = (Expr) getRhsSym(6);
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, null));
                break;
            }
     
            //
            // Rule 461:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression$distr ] Expression$initializer
            //
            case 461: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Valueopt = (Object) getRhsSym(3);
                Object Unsafeopt = (Object) getRhsSym(4);
                Expr distr = (Expr) getRhsSym(6);
                Expr initializer = (Expr) getRhsSym(8);
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, distr, initializer));
                break;
            }
     
            //
            // Rule 462:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ] ($lparen FormalParameter ) MethodBody
            //
            case 462: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Valueopt = (Object) getRhsSym(3);
                Object Unsafeopt = (Object) getRhsSym(4);
                Expr Expression = (Expr) getRhsSym(6);
                IToken lparen = (IToken) getRhsIToken(8);
                X10Formal FormalParameter = (X10Formal) getRhsSym(9);
                Block MethodBody = (Block) getRhsSym(11);
                New initializer = makeInitializer( pos(getRhsFirstTokenIndex(8), getRightSpan()), ArrayBaseType, FormalParameter, MethodBody );
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, initializer));
                break;
            }
     
            //
            // Rule 463:  Valueopt ::= $Empty
            //
            case 463:
                setResult(null);
                break;
 
            //
            // Rule 464:  Valueopt ::= value
            //
            case 464: {
                
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 467:  ArrayBaseType ::= nullable Type
            //
            case 467: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                setResult(nf.Nullable(pos(), Type));
                break;
            }
     
            //
            // Rule 468:  ArrayBaseType ::= future < Type >
            //
            case 468: {
                TypeNode Type = (TypeNode) getRhsSym(3);
                setResult(nf.Future(pos(), Type));
                break;
            }
     
            //
            // Rule 469:  ArrayBaseType ::= ( Type )
            //
            case 469: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                setResult(Type);
                break;
            }
     
            //
            // Rule 470:  ArrayAccess ::= ExpressionName [ ArgumentList ]
            //
            case 470: {
                Name ExpressionName = (Name) getRhsSym(1);
                List ArgumentList = (List) getRhsSym(3);
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), ExpressionName.toExpr(), (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), ExpressionName.toExpr(), ArgumentList));
                break;
            }
     
            //
            // Rule 471:  ArrayAccess ::= PrimaryNoNewArray [ ArgumentList ]
            //
            case 471: {
                Expr PrimaryNoNewArray = (Expr) getRhsSym(1);
                List ArgumentList = (List) getRhsSym(3);
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), PrimaryNoNewArray, (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), PrimaryNoNewArray, ArgumentList));
                break;
            }
     
            //
            // Rule 488:  NowStatement ::= now ( Clock ) Statement
            //
            case 488: {
                Expr Clock = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
              setResult(nf.Now(pos(), Clock, Statement));
                break;
            }
     
            //
            // Rule 489:  ClockedClause ::= clocked ( ClockList )
            //
            case 489: {
                List ClockList = (List) getRhsSym(3);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 490:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 490: {
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                List ClockedClauseopt = (List) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(4);
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                break;
            }
     
            //
            // Rule 491:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 491: {
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                Stmt Statement = (Stmt) getRhsSym(3);
              setResult(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                               ? nf.Here(pos(getLeftSpan()))
                                               : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 492:  WhenStatement ::= when ( Expression ) Statement
            //
            case 492: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
                setResult(nf.When(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 493:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 493: {
                When WhenStatement = (When) getRhsSym(1);
                IToken or = (IToken) getRhsIToken(2);
                Expr Expression = (Expr) getRhsSym(4);
                Stmt Statement = (Stmt) getRhsSym(6);
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 494:  ForEachStatement ::= foreach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 494: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                Expr Expression = (Expr) getRhsSym(5);
                List ClockedClauseopt = (List) getRhsSym(7);
                Stmt Statement = (Stmt) getRhsSym(8);
                setResult(nf.ForEach(pos(),
                              FormalParameter.flags(FormalParameter.flags().Final()),
                              Expression,
                              ClockedClauseopt,
                              Statement));
                break;
            }
     
            //
            // Rule 495:  AtEachStatement ::= ateach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 495: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                Expr Expression = (Expr) getRhsSym(5);
                List ClockedClauseopt = (List) getRhsSym(7);
                Stmt Statement = (Stmt) getRhsSym(8);
                setResult(nf.AtEach(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             ClockedClauseopt,
                             Statement));
                break;
            }
     
            //
            // Rule 496:  EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
            //
            case 496: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                Expr Expression = (Expr) getRhsSym(5);
                Stmt Statement = (Stmt) getRhsSym(7);
                setResult(nf.ForLoop(pos(),
                        FormalParameter.flags(FormalParameter.flags().Final()),
                        Expression,
                        Statement));
                break;
            }
     
            //
            // Rule 497:  FinishStatement ::= finish Statement
            //
            case 497: {
                Stmt Statement = (Stmt) getRhsSym(2);
                setResult(nf.Finish(pos(),  Statement));
                break;
            }
     
            //
            // Rule 498:  NowStatementNoShortIf ::= now ( Clock ) StatementNoShortIf
            //
            case 498: {
                Expr Clock = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                setResult(nf.Now(pos(), Clock, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 499:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt ClockedClauseopt StatementNoShortIf
            //
            case 499: {
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                List ClockedClauseopt = (List) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(4);
                setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(getLeftSpan()))
                                                : PlaceExpressionSingleListopt),
                                            ClockedClauseopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 500:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 500: {
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 501:  WhenStatementNoShortIf ::= when ( Expression ) StatementNoShortIf
            //
            case 501: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                setResult(nf.When(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 502:  WhenStatementNoShortIf ::= WhenStatement or$or ( Expression ) StatementNoShortIf
            //
            case 502: {
                When WhenStatement = (When) getRhsSym(1);
                IToken or = (IToken) getRhsIToken(2);
                Expr Expression = (Expr) getRhsSym(4);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(6);
                WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, StatementNoShortIf);
                setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 503:  ForEachStatementNoShortIf ::= foreach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 503: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                Expr Expression = (Expr) getRhsSym(5);
                List ClockedClauseopt = (List) getRhsSym(7);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(8);
                setResult(nf.ForEach(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             ClockedClauseopt,
                             StatementNoShortIf));

                break;
            }
     
            //
            // Rule 504:  AtEachStatementNoShortIf ::= ateach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 504: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                Expr Expression = (Expr) getRhsSym(5);
                List ClockedClauseopt = (List) getRhsSym(7);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(8);
                setResult(nf.AtEach(pos(),
                            FormalParameter.flags(FormalParameter.flags().Final()),
                            Expression,
                            ClockedClauseopt,
                            StatementNoShortIf));
                break;
            }
     
            //
            // Rule 505:  EnhancedForStatementNoShortIf ::= for ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 505: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                Expr Expression = (Expr) getRhsSym(5);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(7);
                  setResult(nf.ForLoop(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression,
                             StatementNoShortIf));
                break;
            }
     
            //
            // Rule 506:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 506: {
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                setResult(nf.Finish(pos(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 507:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 507: {
                Expr PlaceExpression = (Expr) getRhsSym(2);
              setResult(PlaceExpression);
                break;
            }
     
            //
            // Rule 509:  NextStatement ::= next ;
            //
            case 509: {
                
                setResult(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 510:  AwaitStatement ::= await Expression ;
            //
            case 510: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.Await(pos(), Expression));
                break;
            }
     
            //
            // Rule 511:  ClockList ::= Clock
            //
            case 511: {
                Expr Clock = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                break;
            }
     
            //
            // Rule 512:  ClockList ::= ClockList , Clock
            //
            case 512: {
                List ClockList = (List) getRhsSym(1);
                Expr Clock = (Expr) getRhsSym(3);
                ClockList.add(Clock);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 514:  CastExpression ::= ( Type ) UnaryExpressionNotPlusMinus
            //
            case 514: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(4);
                setResult(nf.Cast(pos(), Type, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 515:  CastExpression ::= ( @ Expression ) UnaryExpressionNotPlusMinus
            //
            case 515: {
                Expr Expression = (Expr) getRhsSym(3);
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(5);
                setResult(nf.PlaceCast(pos(), Expression, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 516:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 516: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(3);
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                break;
            }
     
            //
            // Rule 517:  IdentifierList ::= identifier
            //
            case 517: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Name.class, false);
                l.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                setResult(l);
                break;
            }
     
            //
            // Rule 518:  IdentifierList ::= IdentifierList , identifier
            //
            case 518: {
                List IdentifierList = (List) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                IdentifierList.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                setResult(IdentifierList);
                break;
            }
     
            //
            // Rule 519:  Primary ::= here
            //
            case 519: {
                
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
            // Rule 522:  RegionExpression ::= Expression$expr1 : Expression$expr2
            //
            case 522: {
                Expr expr1 = (Expr) getRhsSym(1);
                Expr expr2 = (Expr) getRhsSym(3);
                Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");

                Name x10LangRegion = new Name(nf, ts, pos(), x10Lang, "region");
                Name x10LangRegionFactory = new Name(nf, ts, pos(), x10LangRegion, "factory");
                Name x10LangRegionFactoryRegion = new Name(nf, ts, pos(), x10LangRegionFactory, "region");
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(expr1);
                l.add(expr2);
                Call regionCall = nf.Call( pos(), x10LangRegionFactoryRegion.prefix.toReceiver(), "region", l  );
                setResult(regionCall);
                break;
            }
     
            //
            // Rule 523:  RegionExpressionList ::= RegionExpression
            //
            case 523: {
                Expr RegionExpression = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                break;
            }
     
            //
            // Rule 524:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 524: {
                List RegionExpressionList = (List) getRhsSym(1);
                Expr RegionExpression = (Expr) getRhsSym(3);
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                break;
            }
     
            //
            // Rule 525:  Primary ::= [ RegionExpressionList ]
            //
            case 525: {
                List RegionExpressionList = (List) getRhsSym(2);
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
            // Rule 526:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 526: {
                Expr expr1 = (Expr) getRhsSym(1);
                Expr expr2 = (Expr) getRhsSym(3);
                //System.out.println("Distribution:" + a + "|" + b + "|");
                // x10.lang.region.factory.region(  ArgumentList )
                // Construct the MethodName
                Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");

                Name x10LangDistribution = new Name(nf, ts, pos(), x10Lang, "dist");
                Name x10LangDistributionFactory = new Name(nf, ts, pos(), x10LangDistribution, "factory");
                Name x10LangDistributionFactoryConstant = new Name(nf, ts, pos(), x10LangDistributionFactory, "constant");
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(expr1);
                l.add(expr2);
                Call call = nf.Call(pos(), x10LangDistributionFactoryConstant.prefix.toReceiver(), "constant", l);
                setResult(call);
                break;
            }
     
            //
            // Rule 527:  FutureExpression ::= future PlaceExpressionSingleListopt { Expression }
            //
            case 527: {
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                Expr Expression = (Expr) getRhsSym(4);
                setResult(nf.Future(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(getLeftSpan()))
                                                : PlaceExpressionSingleListopt), Expression));
                break;
            }
     
            //
            // Rule 528:  FieldModifier ::= mutable
            //
            case 528: {
                
                setResult(X10Flags.MUTABLE);
                break;
            }
     
            //
            // Rule 529:  FieldModifier ::= const
            //
            case 529: {
                
                setResult(Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL));
                break;
            }
     
            //
            // Rule 530:  FunExpression ::= fun Type ( FormalParameterListopt ) { Expression }
            //
            case 530:
                throw new Error("No action specified for rule " + 530);
 
            //
            // Rule 531:  MethodInvocation ::= MethodName ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 531:
                throw new Error("No action specified for rule " + 531); 
 
            //
            // Rule 532:  MethodInvocation ::= Primary . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 532:
                throw new Error("No action specified for rule " + 532); 
 
            //
            // Rule 533:  MethodInvocation ::= super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 533:
                throw new Error("No action specified for rule " + 533); 
 
            //
            // Rule 534:  MethodInvocation ::= ClassName . super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 534:
                throw new Error("No action specified for rule " + 534); 
 
            //
            // Rule 535:  MethodInvocation ::= TypeName . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 535:
                throw new Error("No action specified for rule " + 535); 
 
            //
            // Rule 536:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 536:
                throw new Error("No action specified for rule " + 536); 
 
            //
            // Rule 537:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 537:
                throw new Error("No action specified for rule " + 537); 
 
            //
            // Rule 538:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 538:
                throw new Error("No action specified for rule " + 538); 
 
            //
            // Rule 539:  MethodModifier ::= synchronized
            //
            case 539: {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "\"synchronized\" is an invalid X10 Method Modifier");
                setResult(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 540:  FieldModifier ::= volatile
            //
            case 540: {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "\"volatile\" is an invalid X10 Field Modifier");
                setResult(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 541:  SynchronizedStatement ::= synchronized ( Expression ) Block
            //
            case 541: {
                Expr Expression = (Expr) getRhsSym(3);
                Block Block = (Block) getRhsSym(5);
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "Synchronized Statement is invalid in X10");
                setResult(nf.Synchronized(pos(), Expression, Block));
                break;
            }
     
            //
            // Rule 542:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 542:
                setResult(null);
                break;
 
            //
            // Rule 544:  DepParametersopt ::= $Empty
            //
            case 544:
                setResult(null);
                break;
 
            //
            // Rule 546:  WhereClauseopt ::= $Empty
            //
            case 546:
                setResult(null);
                break;
 
            //
            // Rule 548:  ObjectKindopt ::= $Empty
            //
            case 548:
                setResult(null);
                break;
 
            //
            // Rule 550:  ArrayInitializeropt ::= $Empty
            //
            case 550:
                setResult(null);
                break;
 
            //
            // Rule 552:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 552:
                setResult(null);
                break;
 
            //
            // Rule 554:  ArgumentListopt ::= $Empty
            //
            case 554:
                setResult(null);
                break;
 
            //
            // Rule 556:  DepParametersopt ::= $Empty
            //
            case 556:
                setResult(null);
                break;
 
            //
            // Rule 558:  Unsafeopt ::= $Empty
            //
            case 558:
                setResult(null);
                break;
 
            //
            // Rule 559:  Unsafeopt ::= unsafe
            //
            case 559: {
                
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 560:  ParamIdopt ::= $Empty
            //
            case 560:
                setResult(null);
                break;
 
            //
            // Rule 561:  ParamIdopt ::= identifier
            //
            case 561: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 562:  ClockedClauseopt ::= $Empty
            //
            case 562: {
                
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
        
            default:
                break;
        }
        return;
    }
}

