
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
import polyglot.ext.x10.ast.PropertyDecl;
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
            // Rule 4:  IntegralType ::= byte
            //
            case 4: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Byte()));
                break;
            }
     
            //
            // Rule 5:  IntegralType ::= char
            //
            case 5: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Char()));
                break;
            }
     
            //
            // Rule 6:  IntegralType ::= short
            //
            case 6: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Short()));
                break;
            }
     
            //
            // Rule 7:  IntegralType ::= int
            //
            case 7: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Int()));
                break;
            }
     
            //
            // Rule 8:  IntegralType ::= long
            //
            case 8: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Long()));
                break;
            }
     
            //
            // Rule 9:  FloatingPointType ::= float
            //
            case 9: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Float()));
                break;
            }
     
            //
            // Rule 10:  FloatingPointType ::= double
            //
            case 10: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Double()));
                break;
            }
     
            //
            // Rule 13:  TypeName ::= identifier
            //
            case 13: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 14:  TypeName ::= TypeName . identifier
            //
            case 14: {
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
            // Rule 16:  ArrayType ::= Type [ ]
            //
            case 16: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                setResult(nf.array(Type, pos(), 1));
                break;
            }
     
            //
            // Rule 17:  PackageName ::= identifier
            //
            case 17: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 18:  PackageName ::= PackageName . identifier
            //
            case 18: {
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
            // Rule 19:  ExpressionName ::= identifier
            //
            case 19: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 20:  ExpressionName ::= AmbiguousName . identifier
            //
            case 20: {
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
            // Rule 21:  MethodName ::= identifier
            //
            case 21: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 22:  MethodName ::= AmbiguousName . identifier
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
            // Rule 23:  PackageOrTypeName ::= identifier
            //
            case 23: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 24:  PackageOrTypeName ::= PackageOrTypeName . identifier
            //
            case 24: {
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
            // Rule 25:  AmbiguousName ::= identifier
            //
            case 25: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 26:  AmbiguousName ::= AmbiguousName . identifier
            //
            case 26: {
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
            // Rule 27:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 27: {
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
            // Rule 28:  ImportDeclarations ::= ImportDeclaration
            //
            case 28: {
                Import ImportDeclaration = (Import) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 29:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 29: {
                List ImportDeclarations = (List) getRhsSym(1);
                Import ImportDeclaration = (Import) getRhsSym(2);
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 30:  TypeDeclarations ::= TypeDeclaration
            //
            case 30: {
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 31:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 31: {
                List TypeDeclarations = (List) getRhsSym(1);
                ClassDecl TypeDeclaration = (ClassDecl) getRhsSym(2);
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 34:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 34: {
                Name TypeName = (Name) getRhsSym(2);
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, TypeName.toString()));
                break;
            }
     
            //
            // Rule 35:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 35: {
                Name PackageOrTypeName = (Name) getRhsSym(2);
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, PackageOrTypeName.toString()));
                break;
            }
     
            //
            // Rule 38:  TypeDeclaration ::= ;
            //
            case 38: {
                
                setResult(null);
                break;
            }
     
            //
            // Rule 41:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 41: {
                Flags ClassModifiers = (Flags) getRhsSym(1);
                Flags ClassModifier = (Flags) getRhsSym(2);
                setResult(ClassModifiers.set(ClassModifier));
                break;
            }
     
            //
            // Rule 42:  ClassModifier ::= public
            //
            case 42: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 43:  ClassModifier ::= protected
            //
            case 43: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 44:  ClassModifier ::= private
            //
            case 44: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 45:  ClassModifier ::= abstract
            //
            case 45: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 46:  ClassModifier ::= static
            //
            case 46: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 47:  ClassModifier ::= final
            //
            case 47: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 48:  ClassModifier ::= strictfp
            //
            case 48: {
                
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 49:  Super ::= extends ClassType
            //
            case 49: {
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                setResult(ClassType);
                break;
            }
     
            //
            // Rule 50:  Interfaces ::= implements InterfaceTypeList
            //
            case 50: {
                List InterfaceTypeList = (List) getRhsSym(2);
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 51:  InterfaceTypeList ::= InterfaceType
            //
            case 51: {
                TypeNode InterfaceType = (TypeNode) getRhsSym(1);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 52:  InterfaceTypeList ::= InterfaceTypeList , InterfaceType
            //
            case 52: {
                List InterfaceTypeList = (List) getRhsSym(1);
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                InterfaceTypeList.add(InterfaceType);
                setResult(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 53:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 53: {
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                break;
            }
     
            //
            // Rule 55:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 55: {
                List ClassBodyDeclarations = (List) getRhsSym(1);
                List ClassBodyDeclaration = (List) getRhsSym(2);
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                break;
            }
     
            //
            // Rule 57:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 57: {
                Block InstanceInitializer = (Block) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.NONE, InstanceInitializer));
                setResult(l);
                break;
            }
     
            //
            // Rule 58:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 58: {
                Block StaticInitializer = (Block) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.STATIC, StaticInitializer));
                setResult(l);
                break;
            }
     
            //
            // Rule 59:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 59: {
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 61:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 61: {
                MethodDecl MethodDeclaration = (MethodDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 62:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 62: {
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 63:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 63: {
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 64:  ClassMemberDeclaration ::= ;
            //
            case 64: {
                
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                break;
            }
     
            //
            // Rule 65:  VariableDeclarators ::= VariableDeclarator
            //
            case 65: {
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(1);
                List l = new TypedList(new LinkedList(), X10VarDeclarator.class, false);
                l.add(VariableDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 66:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 66: {
                List VariableDeclarators = (List) getRhsSym(1);
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(3);
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                break;
            }
     
            //
            // Rule 68:  VariableDeclarator ::= VariableDeclaratorId = VariableInitializer
            //
            case 68: {
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                Expr VariableInitializer = (Expr) getRhsSym(3);
                VariableDeclaratorId.init = VariableInitializer;
                VariableDeclaratorId.position(pos());
                // setResult(VariableDeclaratorId); 
                break;
            }
     
            //
            // Rule 69:  TraditionalVariableDeclaratorId ::= identifier
            //
            case 69: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new X10VarDeclarator(pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 70:  TraditionalVariableDeclaratorId ::= TraditionalVariableDeclaratorId [ ]
            //
            case 70: {
                X10VarDeclarator TraditionalVariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                TraditionalVariableDeclaratorId.dims++;
                TraditionalVariableDeclaratorId.position(pos());
                // setResult(a);
                break;
            }
     
            //
            // Rule 72:  VariableDeclaratorId ::= identifier [ IdentifierList ]
            //
            case 72: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                List IdentifierList = (List) getRhsSym(3);
                setResult(new X10VarDeclarator(pos(), identifier.getIdentifier(), IdentifierList));
                break;
            }
     
            //
            // Rule 73:  VariableDeclaratorId ::= [ IdentifierList ]
            //
            case 73: {
                List IdentifierList = (List) getRhsSym(2);
                String name = polyglot.ext.x10.visit.X10PrettyPrinterVisitor.getId();
                setResult(new X10VarDeclarator(pos(), name, IdentifierList));
                break;
            }
     
            //
            // Rule 77:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 77: {
                Flags FieldModifiers = (Flags) getRhsSym(1);
                Flags FieldModifier = (Flags) getRhsSym(2);
                setResult(FieldModifiers.set(FieldModifier));
                break;
            }
     
            //
            // Rule 78:  FieldModifier ::= public
            //
            case 78: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 79:  FieldModifier ::= protected
            //
            case 79: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 80:  FieldModifier ::= private
            //
            case 80: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 81:  FieldModifier ::= static
            //
            case 81: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 82:  FieldModifier ::= final
            //
            case 82: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 83:  FieldModifier ::= transient
            //
            case 83: {
                
                setResult(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 84:  MethodDeclaration ::= MethodHeader MethodBody
            //
            case 84: {
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
            // Rule 86:  ResultType ::= void
            //
            case 86: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 87:  FormalParameterList ::= LastFormalParameter
            //
            case 87: {
                Formal LastFormalParameter = (Formal) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(LastFormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 88:  FormalParameterList ::= FormalParameters , LastFormalParameter
            //
            case 88: {
                List FormalParameters = (List) getRhsSym(1);
                Formal LastFormalParameter = (Formal) getRhsSym(3);
                FormalParameters.add(LastFormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 89:  FormalParameters ::= FormalParameter
            //
            case 89: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 90:  FormalParameters ::= FormalParameters , FormalParameter
            //
            case 90: {
                List FormalParameters = (List) getRhsSym(1);
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                FormalParameters.add(FormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 91:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 91: {
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(2);
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(3);
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names()));
                break;
            }
     
            //
            // Rule 93:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 93: {
                Flags VariableModifiers = (Flags) getRhsSym(1);
                Flags VariableModifier = (Flags) getRhsSym(2);
                setResult(VariableModifiers.set(VariableModifier));
                break;
            }
     
            //
            // Rule 94:  VariableModifier ::= final
            //
            case 94: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 95:  LastFormalParameter ::= VariableModifiersopt Type ...opt$opt VariableDeclaratorId
            //
            case 95: {
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(2);
                Object opt = (Object) getRhsSym(3);
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(4);
                assert(opt == null);
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names()));
                break;
            }
     
            //
            // Rule 97:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 97: {
                Flags MethodModifiers = (Flags) getRhsSym(1);
                Flags MethodModifier = (Flags) getRhsSym(2);
                setResult(MethodModifiers.set(MethodModifier));
                break;
            }
     
            //
            // Rule 98:  MethodModifier ::= public
            //
            case 98: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 99:  MethodModifier ::= protected
            //
            case 99: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 100:  MethodModifier ::= private
            //
            case 100: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 101:  MethodModifier ::= abstract
            //
            case 101: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 102:  MethodModifier ::= static
            //
            case 102: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 103:  MethodModifier ::= final
            //
            case 103: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 104:  MethodModifier ::= native
            //
            case 104: {
                
                setResult(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 105:  MethodModifier ::= strictfp
            //
            case 105: {
                
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 106:  Throws ::= throws ExceptionTypeList
            //
            case 106: {
                List ExceptionTypeList = (List) getRhsSym(2);
                setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 107:  ExceptionTypeList ::= ExceptionType
            //
            case 107: {
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                break;
            }
     
            //
            // Rule 108:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 108: {
                List ExceptionTypeList = (List) getRhsSym(1);
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                ExceptionTypeList.add(ExceptionType);
                // setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 111:  MethodBody ::= ;
            //
            case 111:
                setResult(null);
                break;
 
            //
            // Rule 113:  StaticInitializer ::= static Block
            //
            case 113: {
                Block Block = (Block) getRhsSym(2);
                setResult(Block);
                break;
            }
     
            //
            // Rule 114:  SimpleTypeName ::= identifier
            //
            case 114: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 116:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 116: {
                Flags ConstructorModifiers = (Flags) getRhsSym(1);
                Flags ConstructorModifier = (Flags) getRhsSym(2);
                setResult(ConstructorModifiers.set(ConstructorModifier));
                break;
            }
     
            //
            // Rule 117:  ConstructorModifier ::= public
            //
            case 117: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 118:  ConstructorModifier ::= protected
            //
            case 118: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 119:  ConstructorModifier ::= private
            //
            case 119: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 120:  ConstructorBody ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 120: {
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
            // Rule 121:  Arguments ::= ( ArgumentListopt )
            //
            case 121: {
                List ArgumentListopt = (List) getRhsSym(2);
                setResult(ArgumentListopt);
                break;
            }
     
            //
            // Rule 124:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 124: {
                Flags InterfaceModifiers = (Flags) getRhsSym(1);
                Flags InterfaceModifier = (Flags) getRhsSym(2);
                setResult(InterfaceModifiers.set(InterfaceModifier));
                break;
            }
     
            //
            // Rule 125:  InterfaceModifier ::= public
            //
            case 125: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 126:  InterfaceModifier ::= protected
            //
            case 126: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 127:  InterfaceModifier ::= private
            //
            case 127: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 128:  InterfaceModifier ::= abstract
            //
            case 128: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 129:  InterfaceModifier ::= static
            //
            case 129: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 130:  InterfaceModifier ::= strictfp
            //
            case 130: {
                
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 131:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 131: {
                TypeNode InterfaceType = (TypeNode) getRhsSym(2);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 132:  ExtendsInterfaces ::= ExtendsInterfaces , InterfaceType
            //
            case 132: {
                List ExtendsInterfaces = (List) getRhsSym(1);
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                ExtendsInterfaces.add(InterfaceType);
                // setResult(ExtendsInterfaces);
                break;
            }
     
            //
            // Rule 133:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 133: {
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                break;
            }
     
            //
            // Rule 135:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 135: {
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                break;
            }
     
            //
            // Rule 137:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 137: {
                MethodDecl AbstractMethodDeclaration = (MethodDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(AbstractMethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 138:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 138: {
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 139:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 139: {
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 140:  InterfaceMemberDeclaration ::= ;
            //
            case 140: {
                
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 141:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 141: {
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
            // Rule 143:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 143: {
                Flags ConstantModifiers = (Flags) getRhsSym(1);
                Flags ConstantModifier = (Flags) getRhsSym(2);
                setResult(ConstantModifiers.set(ConstantModifier));
                break;
            }
     
            //
            // Rule 144:  ConstantModifier ::= public
            //
            case 144: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 145:  ConstantModifier ::= static
            //
            case 145: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 146:  ConstantModifier ::= final
            //
            case 146: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 148:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 148: {
                Flags AbstractMethodModifiers = (Flags) getRhsSym(1);
                Flags AbstractMethodModifier = (Flags) getRhsSym(2);
                setResult(AbstractMethodModifiers.set(AbstractMethodModifier));
                break;
            }
     
            //
            // Rule 149:  AbstractMethodModifier ::= public
            //
            case 149: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 150:  AbstractMethodModifier ::= abstract
            //
            case 150: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 151:  SimpleName ::= identifier
            //
            case 151: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 152:  ArrayInitializer ::= { VariableInitializersopt ,opt$opt }
            //
            case 152: {
                List VariableInitializersopt = (List) getRhsSym(2);
                Object opt = (Object) getRhsSym(3);
                if (VariableInitializersopt == null)
                     setResult(nf.ArrayInit(pos()));
                else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
                break;
            }
     
            //
            // Rule 153:  VariableInitializers ::= VariableInitializer
            //
            case 153: {
                Expr VariableInitializer = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 154:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 154: {
                List VariableInitializers = (List) getRhsSym(1);
                Expr VariableInitializer = (Expr) getRhsSym(3);
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                break;
            }
     
            //
            // Rule 155:  Block ::= { BlockStatementsopt }
            //
            case 155: {
                List BlockStatementsopt = (List) getRhsSym(2);
                setResult(nf.Block(pos(), BlockStatementsopt));
                break;
            }
     
            //
            // Rule 156:  BlockStatements ::= BlockStatement
            //
            case 156: {
                List BlockStatement = (List) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                break;
            }
     
            //
            // Rule 157:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 157: {
                List BlockStatements = (List) getRhsSym(1);
                List BlockStatement = (List) getRhsSym(2);
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                break;
            }
     
            //
            // Rule 159:  BlockStatement ::= ClassDeclaration
            //
            case 159: {
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                break;
            }
     
            //
            // Rule 160:  BlockStatement ::= Statement
            //
            case 160: {
                Stmt Statement = (Stmt) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                break;
            }
     
            //
            // Rule 162:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 162: {
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
            // Rule 186:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 186: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
                setResult(nf.If(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 187:  IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
            //
            case 187: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                Stmt Statement = (Stmt) getRhsSym(7);
                setResult(nf.If(pos(), Expression, StatementNoShortIf, Statement));
                break;
            }
     
            //
            // Rule 188:  IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf$true_stmt else StatementNoShortIf$false_stmt
            //
            case 188: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt true_stmt = (Stmt) getRhsSym(5);
                Stmt false_stmt = (Stmt) getRhsSym(7);
                setResult(nf.If(pos(), Expression, true_stmt, false_stmt));
                break;
            }
     
            //
            // Rule 189:  EmptyStatement ::= ;
            //
            case 189: {
                
                setResult(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 190:  LabeledStatement ::= identifier : Statement
            //
            case 190: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                Stmt Statement = (Stmt) getRhsSym(3);
                setResult(nf.Labeled(pos(), identifier.getIdentifier(), Statement));
                break;
            }
     
            //
            // Rule 191:  LabeledStatementNoShortIf ::= identifier : StatementNoShortIf
            //
            case 191: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(3);
                setResult(nf.Labeled(pos(), identifier.getIdentifier(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 192:  ExpressionStatement ::= StatementExpression ;
            //
            case 192: {
                Expr StatementExpression = (Expr) getRhsSym(1);
                setResult(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 200:  AssertStatement ::= assert Expression ;
            //
            case 200: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.Assert(pos(), Expression));
                break;
            }
     
            //
            // Rule 201:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 201: {
                Expr expr1 = (Expr) getRhsSym(2);
                Expr expr2 = (Expr) getRhsSym(4);
                setResult(nf.Assert(pos(), expr1, expr2));
                break;
            }
     
            //
            // Rule 202:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 202: {
                Expr Expression = (Expr) getRhsSym(3);
                List SwitchBlock = (List) getRhsSym(5);
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                break;
            }
     
            //
            // Rule 203:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 203: {
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                List SwitchLabelsopt = (List) getRhsSym(3);
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                break;
            }
     
            //
            // Rule 205:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 205: {
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                break;
            }
     
            //
            // Rule 206:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 206: {
                List SwitchLabels = (List) getRhsSym(1);
                List BlockStatements = (List) getRhsSym(2);
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                break;
            }
     
            //
            // Rule 207:  SwitchLabels ::= SwitchLabel
            //
            case 207: {
                Case SwitchLabel = (Case) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                break;
            }
     
            //
            // Rule 208:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 208: {
                List SwitchLabels = (List) getRhsSym(1);
                Case SwitchLabel = (Case) getRhsSym(2);
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                break;
            }
     
            //
            // Rule 209:  SwitchLabel ::= case ConstantExpression :
            //
            case 209: {
                Expr ConstantExpression = (Expr) getRhsSym(2);
                setResult(nf.Case(pos(), ConstantExpression));
                break;
            }
     
            //
            // Rule 210:  SwitchLabel ::= default :
            //
            case 210: {
                
                setResult(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 211:  WhileStatement ::= while ( Expression ) Statement
            //
            case 211: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
                setResult(nf.While(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 212:  WhileStatementNoShortIf ::= while ( Expression ) StatementNoShortIf
            //
            case 212: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                setResult(nf.While(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 213:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 213: {
                Stmt Statement = (Stmt) getRhsSym(2);
                Expr Expression = (Expr) getRhsSym(5);
                setResult(nf.Do(pos(), Statement, Expression));
                break;
            }
     
            //
            // Rule 216:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 216: {
                List ForInitopt = (List) getRhsSym(3);
                Expr Expressionopt = (Expr) getRhsSym(5);
                List ForUpdateopt = (List) getRhsSym(7);
                Stmt Statement = (Stmt) getRhsSym(9);
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                break;
            }
     
            //
            // Rule 217:  ForStatementNoShortIf ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) StatementNoShortIf
            //
            case 217: {
                List ForInitopt = (List) getRhsSym(3);
                Expr Expressionopt = (Expr) getRhsSym(5);
                List ForUpdateopt = (List) getRhsSym(7);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(9);
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 219:  ForInit ::= LocalVariableDeclaration
            //
            case 219: {
                List LocalVariableDeclaration = (List) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 221:  StatementExpressionList ::= StatementExpression
            //
            case 221: {
                Expr StatementExpression = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                break;
            }
     
            //
            // Rule 222:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 222: {
                List StatementExpressionList = (List) getRhsSym(1);
                Expr StatementExpression = (Expr) getRhsSym(3);
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                //setResult(StatementExpressionList);
                break;
            }
     
            //
            // Rule 223:  BreakStatement ::= break identifieropt ;
            //
            case 223: {
                Name identifieropt = (Name) getRhsSym(2);
                if (identifieropt == null)
                     setResult(nf.Break(pos()));
                else setResult(nf.Break(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 224:  ContinueStatement ::= continue identifieropt ;
            //
            case 224: {
                Name identifieropt = (Name) getRhsSym(2);
                if (identifieropt == null)
                     setResult(nf.Continue(pos()));
                else setResult(nf.Continue(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 225:  ReturnStatement ::= return Expressionopt ;
            //
            case 225: {
                Expr Expressionopt = (Expr) getRhsSym(2);
                setResult(nf.Return(pos(), Expressionopt));
                break;
            }
     
            //
            // Rule 226:  ThrowStatement ::= throw Expression ;
            //
            case 226: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.Throw(pos(), Expression));
                break;
            }
     
            //
            // Rule 227:  TryStatement ::= try Block Catches
            //
            case 227: {
                Block Block = (Block) getRhsSym(2);
                List Catches = (List) getRhsSym(3);
                setResult(nf.Try(pos(), Block, Catches));
                break;
            }
     
            //
            // Rule 228:  TryStatement ::= try Block Catchesopt Finally
            //
            case 228: {
                Block Block = (Block) getRhsSym(2);
                List Catchesopt = (List) getRhsSym(3);
                Block Finally = (Block) getRhsSym(4);
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                break;
            }
     
            //
            // Rule 229:  Catches ::= CatchClause
            //
            case 229: {
                Catch CatchClause = (Catch) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                break;
            }
     
            //
            // Rule 230:  Catches ::= Catches CatchClause
            //
            case 230: {
                List Catches = (List) getRhsSym(1);
                Catch CatchClause = (Catch) getRhsSym(2);
                Catches.add(CatchClause);
                //setResult(Catches);
                break;
            }
     
            //
            // Rule 231:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 231: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                Block Block = (Block) getRhsSym(5);
                setResult(nf.Catch(pos(), FormalParameter, Block));
                break;
            }
     
            //
            // Rule 232:  Finally ::= finally Block
            //
            case 232: {
                Block Block = (Block) getRhsSym(2);
                setResult(Block);
                break;
            }
     
            //
            // Rule 236:  PrimaryNoNewArray ::= Type . class
            //
            case 236: {
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
            // Rule 237:  PrimaryNoNewArray ::= void . class
            //
            case 237: {
                
                setResult(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(getLeftSpan()), ts.Void())));
                break;
            }
     
            //
            // Rule 238:  PrimaryNoNewArray ::= this
            //
            case 238: {
                
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 239:  PrimaryNoNewArray ::= ClassName . this
            //
            case 239: {
                Name ClassName = (Name) getRhsSym(1);
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 240:  PrimaryNoNewArray ::= ( Expression )
            //
            case 240: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.ParExpr(pos(), Expression));
                break;
            }
     
            //
            // Rule 245:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 245: {
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.IntegerLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 246:  Literal ::= LongLiteral$LongLiteral
            //
            case 246: {
                IToken LongLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 247:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 247: {
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 248:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 248: {
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 249:  Literal ::= BooleanLiteral
            //
            case 249: {
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 250:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 250: {
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 251:  Literal ::= StringLiteral$str
            //
            case 251: {
                IToken str = (IToken) getRhsIToken(1);
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 252:  Literal ::= null
            //
            case 252: {
                
                setResult(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 253:  BooleanLiteral ::= true$trueLiteral
            //
            case 253: {
                IToken trueLiteral = (IToken) getRhsIToken(1);
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 254:  BooleanLiteral ::= false$falseLiteral
            //
            case 254: {
                IToken falseLiteral = (IToken) getRhsIToken(1);
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 255:  ArgumentList ::= Expression
            //
            case 255: {
                Expr Expression = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                break;
            }
     
            //
            // Rule 256:  ArgumentList ::= ArgumentList , Expression
            //
            case 256: {
                List ArgumentList = (List) getRhsSym(1);
                Expr Expression = (Expr) getRhsSym(3);
                ArgumentList.add(Expression);
                //setResult(ArgumentList);
                break;
            }
     
            //
            // Rule 257:  DimExprs ::= DimExpr
            //
            case 257: {
                Expr DimExpr = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                setResult(l);
                break;
            }
     
            //
            // Rule 258:  DimExprs ::= DimExprs DimExpr
            //
            case 258: {
                List DimExprs = (List) getRhsSym(1);
                Expr DimExpr = (Expr) getRhsSym(2);
                DimExprs.add(DimExpr);
                //setResult(DimExprs);
                break;
            }
     
            //
            // Rule 259:  DimExpr ::= [ Expression ]
            //
            case 259: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(Expression.position(pos()));
                break;
            }
     
            //
            // Rule 260:  Dims ::= [ ]
            //
            case 260: {
                
                setResult(new Integer(1));
                break;
            }
     
            //
            // Rule 261:  Dims ::= Dims [ ]
            //
            case 261: {
                Integer Dims = (Integer) getRhsSym(1);
                setResult(new Integer(Dims.intValue() + 1));
                break;
            }
     
            //
            // Rule 262:  FieldAccess ::= Primary . identifier
            //
            case 262: {
                Expr Primary = (Expr) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(nf.Field(pos(), Primary, identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 263:  FieldAccess ::= super . identifier
            //
            case 263: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 264:  FieldAccess ::= ClassName . super$sup . identifier
            //
            case 264: {
                Name ClassName = (Name) getRhsSym(1);
                IToken sup = (IToken) getRhsIToken(3);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 265:  MethodInvocation ::= MethodName ( ArgumentListopt )
            //
            case 265: {
                Name MethodName = (Name) getRhsSym(1);
                List ArgumentListopt = (List) getRhsSym(3);
                setResult(nf.Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, ArgumentListopt));
                break;
            }
     
            //
            // Rule 267:  PostfixExpression ::= ExpressionName
            //
            case 267: {
                Name ExpressionName = (Name) getRhsSym(1);
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 270:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 270: {
                Expr PostfixExpression = (Expr) getRhsSym(1);
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 271:  PostDecrementExpression ::= PostfixExpression --
            //
            case 271: {
                Expr PostfixExpression = (Expr) getRhsSym(1);
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 274:  UnaryExpression ::= + UnaryExpression
            //
            case 274: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpression));
                break;
            }
     
            //
            // Rule 275:  UnaryExpression ::= - UnaryExpression
            //
            case 275: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpression));
                break;
            }
     
            //
            // Rule 277:  PreIncrementExpression ::= ++ UnaryExpression
            //
            case 277: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpression));
                break;
            }
     
            //
            // Rule 278:  PreDecrementExpression ::= -- UnaryExpression
            //
            case 278: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpression));
                break;
            }
     
            //
            // Rule 280:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 280: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 281:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 281: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 284:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 284: {
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                Expr UnaryExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                break;
            }
     
            //
            // Rule 285:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 285: {
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                Expr UnaryExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                break;
            }
     
            //
            // Rule 286:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 286: {
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                Expr UnaryExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                break;
            }
     
            //
            // Rule 288:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 288: {
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 289:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 289: {
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 291:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 291: {
                Expr ShiftExpression = (Expr) getRhsSym(1);
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                break;
            }
     
            //
            // Rule 292:  ShiftExpression ::= ShiftExpression > > AdditiveExpression
            //
            case 292: {
                Expr ShiftExpression = (Expr) getRhsSym(1);
                Expr AdditiveExpression = (Expr) getRhsSym(4);
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 293:  ShiftExpression ::= ShiftExpression > > > AdditiveExpression
            //
            case 293: {
                Expr ShiftExpression = (Expr) getRhsSym(1);
                Expr AdditiveExpression = (Expr) getRhsSym(5);
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 295:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 295: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
                break;
            }
     
            //
            // Rule 296:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 296: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
                break;
            }
     
            //
            // Rule 297:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 297: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
                break;
            }
     
            //
            // Rule 298:  RelationalExpression ::= RelationalExpression > = ShiftExpression
            //
            case 298: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(4);
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
                break;
            }
     
            //
            // Rule 300:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 300: {
                Expr EqualityExpression = (Expr) getRhsSym(1);
                Expr RelationalExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                break;
            }
     
            //
            // Rule 301:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 301: {
                Expr EqualityExpression = (Expr) getRhsSym(1);
                Expr RelationalExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                break;
            }
     
            //
            // Rule 303:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 303: {
                Expr AndExpression = (Expr) getRhsSym(1);
                Expr EqualityExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                break;
            }
     
            //
            // Rule 305:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 305: {
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                Expr AndExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                break;
            }
     
            //
            // Rule 307:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 307: {
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 309:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 309: {
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                break;
            }
     
            //
            // Rule 311:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 311: {
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                break;
            }
     
            //
            // Rule 313:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 313: {
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                Expr Expression = (Expr) getRhsSym(3);
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 316:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 316: {
                Expr LeftHandSide = (Expr) getRhsSym(1);
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 317:  LeftHandSide ::= ExpressionName
            //
            case 317: {
                Name ExpressionName = (Name) getRhsSym(1);
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 320:  AssignmentOperator ::= =
            //
            case 320: {
                
                setResult(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 321:  AssignmentOperator ::= *=
            //
            case 321: {
                
                setResult(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 322:  AssignmentOperator ::= /=
            //
            case 322: {
                
                setResult(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 323:  AssignmentOperator ::= %=
            //
            case 323: {
                
                setResult(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 324:  AssignmentOperator ::= +=
            //
            case 324: {
                
                setResult(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 325:  AssignmentOperator ::= -=
            //
            case 325: {
                
                setResult(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 326:  AssignmentOperator ::= <<=
            //
            case 326: {
                
                setResult(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 327:  AssignmentOperator ::= > > =
            //
            case 327: {
                
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 328:  AssignmentOperator ::= > > > =
            //
            case 328: {
                
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 329:  AssignmentOperator ::= &=
            //
            case 329: {
                
                setResult(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 330:  AssignmentOperator ::= ^=
            //
            case 330: {
                
                setResult(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 331:  AssignmentOperator ::= |=
            //
            case 331: {
                
                setResult(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 334:  Dimsopt ::= $Empty
            //
            case 334: {
                
                setResult(new Integer(0));
                break;
            }
     
            //
            // Rule 336:  Catchesopt ::= $Empty
            //
            case 336: {
                
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 338:  identifieropt ::= $Empty
            //
            case 338:
                setResult(null);
                break;
 
            //
            // Rule 339:  identifieropt ::= identifier
            //
            case 339: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 340:  ForUpdateopt ::= $Empty
            //
            case 340: {
                
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 342:  Expressionopt ::= $Empty
            //
            case 342:
                setResult(null);
                break;
 
            //
            // Rule 344:  ForInitopt ::= $Empty
            //
            case 344: {
                
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 346:  SwitchLabelsopt ::= $Empty
            //
            case 346: {
                
                setResult(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 348:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 348: {
                
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 350:  VariableModifiersopt ::= $Empty
            //
            case 350: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 352:  VariableInitializersopt ::= $Empty
            //
            case 352:
                setResult(null);
                break;
 
            //
            // Rule 354:  AbstractMethodModifiersopt ::= $Empty
            //
            case 354: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 356:  ConstantModifiersopt ::= $Empty
            //
            case 356: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 358:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 358: {
                
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 360:  ExtendsInterfacesopt ::= $Empty
            //
            case 360: {
                
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 362:  InterfaceModifiersopt ::= $Empty
            //
            case 362: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 364:  ClassBodyopt ::= $Empty
            //
            case 364:
                setResult(null);
                break;
 
            //
            // Rule 366:  Argumentsopt ::= $Empty
            //
            case 366:
                setResult(null);
                break;
 
            //
            // Rule 367:  Argumentsopt ::= Arguments
            //
            case 367:
                throw new Error("No action specified for rule " + 367);
 
            //
            // Rule 368:  ,opt ::= $Empty
            //
            case 368:
                setResult(null);
                break;
 
            //
            // Rule 370:  ArgumentListopt ::= $Empty
            //
            case 370: {
                
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 372:  BlockStatementsopt ::= $Empty
            //
            case 372: {
                
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 374:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 374:
                setResult(null);
                break;
 
            //
            // Rule 376:  ConstructorModifiersopt ::= $Empty
            //
            case 376: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 378:  ...opt ::= $Empty
            //
            case 378:
                setResult(null);
                break;
 
            //
            // Rule 380:  FormalParameterListopt ::= $Empty
            //
            case 380: {
                
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 382:  Throwsopt ::= $Empty
            //
            case 382: {
                
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 384:  MethodModifiersopt ::= $Empty
            //
            case 384: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 386:  FieldModifiersopt ::= $Empty
            //
            case 386: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 388:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 388: {
                
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 390:  Interfacesopt ::= $Empty
            //
            case 390: {
                
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 392:  Superopt ::= $Empty
            //
            case 392: {
                
               setResult(new Name(nf, ts, pos(), "x10.lang.Object").toType());
                break;
            }
     
            //
            // Rule 394:  ClassModifiersopt ::= $Empty
            //
            case 394: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 396:  TypeDeclarationsopt ::= $Empty
            //
            case 396: {
                
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 398:  ImportDeclarationsopt ::= $Empty
            //
            case 398: {
                
                setResult(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 400:  PackageDeclarationopt ::= $Empty
            //
            case 400:
                setResult(null);
                break;
 
            //
            // Rule 402:  ClassType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 402: {
                Name TypeName = (Name) getRhsSym(1);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                     setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 403:  InterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 403: {
                Name TypeName = (Name) getRhsSym(1);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
                 setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 404:  PackageDeclaration ::= package PackageName ;
            //
            case 404: {
                Name PackageName = (Name) getRhsSym(2);
                setResult(PackageName.toPackage());
                break;
            }
     
            //
            // Rule 405:  NormalClassDeclaration ::= ClassModifiersopt class identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 405: {
                Flags ClassModifiersopt = (Flags) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                TypeNode Superopt = (TypeNode) getRhsSym(5);
                List Interfacesopt = (List) getRhsSym(6);
                ClassBody ClassBody = (ClassBody) getRhsSym(7);
      checkTypeName(identifier);
      //Report.report(1, "Parser: Golden Creating class with properties |" + PropertyListopt + "|");
      List/*<PropertyDecl>*/ props = PropertyListopt == null ? null 
                  : (List) PropertyListopt[0];
      Expr ci = PropertyListopt == null ? null : (Expr) PropertyListopt[1];
      setResult(X10Flags.isValue(ClassModifiersopt)
         ? nf.ValueClassDecl(pos(),
              ClassModifiersopt, identifier.getIdentifier(), props, ci, Superopt, Interfacesopt, ClassBody)
         : nf.ClassDecl(pos(),
              ClassModifiersopt, identifier.getIdentifier(), props, ci, Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 406:  PropertyList ::= ( Properties WhereClauseopt )
            //
            case 406: {
                List Properties = (List) getRhsSym(2);
                Expr WhereClauseopt = (Expr) getRhsSym(3);
   Object[] result = new Object[2];
   result[0] = Properties;
   result[1] = WhereClauseopt;
   setResult(result);
           break;
            }  
            //
            // Rule 407:  Properties ::= Property
            //
            case 407: {
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                break;
            }
     
            //
            // Rule 408:  Properties ::= Properties , Property
            //
            case 408: {
                List Properties = (List) getRhsSym(1);
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                Properties.add(Property);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 409:  Property ::= Type identifier
            //
            case 409: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(2);
    
                setResult(nf.PropertyDecl(pos(), Flags.PUBLIC.Final(), Type,
                identifier.getIdentifier()));
              
                break;
            }
     
            //
            // Rule 410:  MethodHeader ::= MethodModifiersopt ResultType MethodDeclarator Throwsopt
            //
            case 410: {
                Flags MethodModifiersopt = (Flags) getRhsSym(1);
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                Object[] MethodDeclarator = (Object[]) getRhsSym(3);
                List Throwsopt = (List) getRhsSym(4);
      Name c = (Name) MethodDeclarator[0];
      List d = (List) MethodDeclarator[1];
      Integer e = (Integer) MethodDeclarator[2];
      Expr where = (Expr) MethodDeclarator[3];
      TypeNode thisClause = (TypeNode) MethodDeclarator[4];
      if (ResultType.type() == ts.Void() && e.intValue() > 0)
         {
           // TODO: error!!!
           assert(false);
         }

       setResult(nf.MethodDecl(pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(3)),
          thisClause,
          MethodModifiersopt,
          nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), e.intValue()),
          c.toString(),
          d,
          where,
          Throwsopt,
          null));
                break;
            }
     
            //
            // Rule 411:  ExplicitConstructorInvocation ::= this ( ArgumentListopt ) ;
            //
            case 411: {
                List ArgumentListopt = (List) getRhsSym(3);
                setResult(nf.ThisCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 412:  ExplicitConstructorInvocation ::= super ( ArgumentListopt ) ;
            //
            case 412: {
                List ArgumentListopt = (List) getRhsSym(3);
                setResult(nf.SuperCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 413:  ExplicitConstructorInvocation ::= Primary . this ( ArgumentListopt ) ;
            //
            case 413: {
                Expr Primary = (Expr) getRhsSym(1);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.ThisCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 414:  ExplicitConstructorInvocation ::= Primary . super ( ArgumentListopt ) ;
            //
            case 414: {
                Expr Primary = (Expr) getRhsSym(1);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.SuperCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 415:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier PropertyListopt ExtendsInterfacesopt InterfaceBody
            //
            case 415: {
                Flags InterfaceModifiersopt = (Flags) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                List ExtendsInterfacesopt = (List) getRhsSym(5);
                ClassBody InterfaceBody = (ClassBody) getRhsSym(6);
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
            // Rule 416:  AbstractMethodDeclaration ::= AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt ;
            //
            case 416: {
                Flags AbstractMethodModifiersopt = (Flags) getRhsSym(1);
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                Object[] MethodDeclarator = (Object[]) getRhsSym(3);
                List Throwsopt = (List) getRhsSym(4);
     Name c = (Name) MethodDeclarator[0];
     List d = (List) MethodDeclarator[1];
     Integer e = (Integer) MethodDeclarator[2];
     Expr where = (Expr) MethodDeclarator[3];
     TypeNode thisClause = (TypeNode) MethodDeclarator[4];
     if (ResultType.type() == ts.Void() && e.intValue() > 0)
        {
          // TODO: error!!!
          assert(false);
        }

     setResult(nf.MethodDecl(pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(3)),
                thisClause,
                AbstractMethodModifiersopt ,
                nf.array((TypeNode) ResultType, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), e.intValue()),
                c.toString(),
                d,
                where,
                Throwsopt,
                null));
                break;
            }
     
            //
            // Rule 417:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
            //
            case 417: {
                TypeNode ClassOrInterfaceType = (TypeNode) getRhsSym(2);
                List ArgumentListopt = (List) getRhsSym(4);
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(6);
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt));
                else setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 418:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 418: {
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
            // Rule 419:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 419: {
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
            // Rule 420:  MethodInvocation ::= Primary . identifier ( ArgumentListopt )
            //
            case 420: {
                Expr Primary = (Expr) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.Call(pos(), Primary, identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 421:  MethodInvocation ::= super . identifier ( ArgumentListopt )
            //
            case 421: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 422:  MethodInvocation ::= ClassName . super$sup . identifier ( ArgumentListopt )
            //
            case 422: {
                Name ClassName = (Name) getRhsSym(1);
                IToken sup = (IToken) getRhsIToken(3);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                List ArgumentListopt = (List) getRhsSym(7);
                setResult(nf.Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 423:  Type ::= DataType
            //
            case 423: {
                TypeNode DataType = (TypeNode) getRhsSym(1);
              setResult(DataType);
                break;
            }
     
            //
            // Rule 424:  Type ::= nullable Type
            //
            case 424: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                setResult(nf.Nullable(pos(), Type));
                break;
            }
     
            //
            // Rule 425:  Type ::= future < Type >
            //
            case 425: {
                TypeNode Type = (TypeNode) getRhsSym(3);
                setResult(nf.Future(pos(), Type));
                break;
            }
     
            //
            // Rule 426:  Type ::= ( Type ) DepParametersopt
            //
            case 426: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
           //System.out.println("Parser: parsed (Type) DepParmetersopt |" + Type + "| |" + DepParametersopt +"|");
                setResult(DepParametersopt == null ? Type 
                : ((X10TypeNode) Type).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 430:  PrimitiveType ::= NumericType DepParametersopt
            //
            case 430: {
                TypeNode NumericType = (TypeNode) getRhsSym(1);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
              //  System.out.println("Parser: parsed PrimitiveType |" + NumericType + "| |" + DepParametersopt +"|");
                setResult(DepParametersopt == null
                               ? NumericType
                               : ((X10TypeNode) NumericType).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 431:  PrimitiveType ::= boolean DepParametersopt
            //
            case 431: {
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                X10TypeNode res = (X10TypeNode) nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(DepParametersopt==null 
                           ? res 
                           : res.dep(null, DepParametersopt));
               break;
            }
     
            //
            // Rule 436:  ClassOrInterfaceType ::= TypeName DepParametersopt PlaceTypeSpecifieropt
            //
            case 436: {
                Name TypeName = (Name) getRhsSym(1);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(3);
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
            // Rule 437:  DepParameters ::= ( DepParameterExpr )
            //
            case 437: {
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(2);
                setResult(DepParameterExpr);
                break;
            }
     
            //
            // Rule 438:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 438: {
                List ArgumentList = (List) getRhsSym(1);
                Expr WhereClauseopt = (Expr) getRhsSym(2);
                setResult(nf.DepParameterExpr(pos(), ArgumentList, WhereClauseopt));
                break;
            }
     
            //
            // Rule 439:  DepParameterExpr ::= WhereClause
            //
            case 439: {
                Expr WhereClause = (Expr) getRhsSym(1);
                setResult(nf.DepParameterExpr(pos(), Collections.EMPTY_LIST, WhereClause));
                break;
            }
     
            //
            // Rule 440:  WhereClause ::= : Expression
            //
            case 440: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(Expression);
                break;
            }
     
            //
            // Rule 442:  X10ArrayType ::= Type [ . ]
            //
            case 442: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, null));
                break;
            }
     
            //
            // Rule 443:  X10ArrayType ::= Type value [ . ]
            //
            case 443: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, null));
                break;
            }
     
            //
            // Rule 444:  X10ArrayType ::= Type [ DepParameterExpr ]
            //
            case 444: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(3);
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
                break;
            }
     
            //
            // Rule 445:  X10ArrayType ::= Type value [ DepParameterExpr ]
            //
            case 445: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(4);
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, DepParameterExpr));
                break;
            }
     
            //
            // Rule 446:  ObjectKind ::= value
            //
            case 446:
                throw new Error("No action specified for rule " + 446);
 
            //
            // Rule 447:  ObjectKind ::= reference
            //
            case 447:
                throw new Error("No action specified for rule " + 447);
 
            //
            // Rule 448:  MethodModifier ::= atomic
            //
            case 448: {
                
                setResult(X10Flags.ATOMIC);
                break;
            }
     
            //
            // Rule 449:  MethodModifier ::= extern
            //
            case 449: {
                
                setResult(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 451:  ValueClassDeclaration ::= ClassModifiersopt value identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 451: {
                Flags ClassModifiersopt = (Flags) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                Object[] PropertyListopt = (Object[]) getRhsSym(4);
                TypeNode Superopt = (TypeNode) getRhsSym(5);
                List Interfacesopt = (List) getRhsSym(6);
                ClassBody ClassBody = (ClassBody) getRhsSym(7);
    checkTypeName(identifier);
    List/*<PropertyDecl>*/ props = PropertyListopt==null ? null : (List) PropertyListopt[0];
    Expr ci = PropertyListopt==null ? null : (Expr) PropertyListopt[1];
    setResult(nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
    ClassModifiersopt, identifier.getIdentifier(), 
    props, ci, Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 452:  ValueClassDeclaration ::= ClassModifiersopt value class identifier PropertyListopt Superopt Interfacesopt ClassBody
            //
            case 452: {
                Flags ClassModifiersopt = (Flags) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(4);
                Object[] PropertyListopt = (Object[]) getRhsSym(5);
                TypeNode Superopt = (TypeNode) getRhsSym(6);
                List Interfacesopt = (List) getRhsSym(7);
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                checkTypeName(identifier);
    List/*<PropertyDecl>*/ props = PropertyListopt==null ? null : (List) PropertyListopt[0];
    Expr ci = PropertyListopt==null ? null : (Expr) PropertyListopt[1];
    setResult(nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                              ClassModifiersopt, identifier.getIdentifier(), 
                              props, ci, Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 453:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 453: {
                Flags ConstructorModifiersopt = (Flags) getRhsSym(1);
                Object[] ConstructorDeclarator = (Object[]) getRhsSym(2);
                List Throwsopt = (List) getRhsSym(3);
                Block ConstructorBody = (Block) getRhsSym(4);
     Name a = (Name) ConstructorDeclarator[1];
     DepParameterExpr c = (DepParameterExpr) ConstructorDeclarator[2];
     List b = (List) ConstructorDeclarator[3];
     Expr e = (Expr) ConstructorDeclarator[4];              
     setResult(nf.ConstructorDecl(pos(), ConstructorModifiersopt, a.toString(), c, b, e, Throwsopt, ConstructorBody));
               break;
            }
    
            //
            // Rule 454:  ConstructorDeclarator ::= SimpleTypeName DepParametersopt ( FormalParameterListopt WhereClauseopt )
            //
            case 454: {
                Name SimpleTypeName = (Name) getRhsSym(1);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                List FormalParameterListopt = (List) getRhsSym(4);
                Expr WhereClauseopt = (Expr) getRhsSym(5);
             Object[] a = new Object[5];
             a[1] = SimpleTypeName;
             a[2] = DepParametersopt;
             a[3] = FormalParameterListopt;
             a[4] = WhereClauseopt;
             setResult(a);
               break;
            }
    
            //
            // Rule 456:  MethodDeclarator ::= ThisClauseopt identifier ( FormalParameterListopt WhereClauseopt )
            //
            case 456: {
                TypeNode ThisClauseopt = (TypeNode) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(2);
                List FormalParameterListopt = (List) getRhsSym(4);
                Expr WhereClauseopt = (Expr) getRhsSym(5);
                // vj: TODO -- add processing of ThisClause, the this-restriction.
                Object[] a = new Object[5];
               a[0] = new Name(nf, ts, pos(), identifier.getIdentifier());
                a[1] = FormalParameterListopt;
               a[2] = new Integer(0);
               a[3] = WhereClauseopt;
               a[4] = ThisClauseopt;
                setResult(a);
                break;
            }
     
            //
            // Rule 457:  MethodDeclarator ::= MethodDeclarator [ ]
            //
            case 457: {
                Object[] MethodDeclarator = (Object[]) getRhsSym(1);
                MethodDeclarator[2] = new Integer(((Integer) MethodDeclarator[2]).intValue() + 1);
                // setResult(MethodDeclarator);
               break;
            }
     
            //
            // Rule 458:  FieldDeclaration ::= FieldModifiersopt ThisClauseopt Type VariableDeclarators ;
            //
            case 458: {
                Flags FieldModifiersopt = (Flags) getRhsSym(1);
                TypeNode ThisClauseopt = (TypeNode) getRhsSym(2);
                TypeNode Type = (TypeNode) getRhsSym(3);
                List VariableDeclarators = (List) getRhsSym(4);
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
            // Rule 459:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt Dims ArrayInitializer
            //
            case 459: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Unsafeopt = (Object) getRhsSym(3);
                Integer Dims = (Integer) getRhsSym(4);
                ArrayInit ArrayInitializer = (ArrayInit) getRhsSym(5);
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Dims.intValue(), ArrayInitializer));
                break;
            }
     
            //
            // Rule 460:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr Dims
            //
            case 460: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Unsafeopt = (Object) getRhsSym(3);
                Expr DimExpr = (Expr) getRhsSym(4);
                Integer Dims = (Integer) getRhsSym(5);
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Collections.singletonList(DimExpr), Dims.intValue()));
                break;
            }
     
            //
            // Rule 461:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr DimExprs Dimsopt
            //
            case 461: {
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
            // Rule 462:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ]
            //
            case 462: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Valueopt = (Object) getRhsSym(3);
                Object Unsafeopt = (Object) getRhsSym(4);
                Expr Expression = (Expr) getRhsSym(6);
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, null));
                break;
            }
     
            //
            // Rule 463:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression$distr ] Expression$initializer
            //
            case 463: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Valueopt = (Object) getRhsSym(3);
                Object Unsafeopt = (Object) getRhsSym(4);
                Expr distr = (Expr) getRhsSym(6);
                Expr initializer = (Expr) getRhsSym(8);
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, distr, initializer));
                break;
            }
     
            //
            // Rule 464:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ] ($lparen FormalParameter ) MethodBody
            //
            case 464: {
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
            // Rule 465:  Valueopt ::= $Empty
            //
            case 465:
                setResult(null);
                break;
 
            //
            // Rule 466:  Valueopt ::= value
            //
            case 466: {
                
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 469:  ArrayBaseType ::= nullable Type
            //
            case 469: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                setResult(nf.Nullable(pos(), Type));
                break;
            }
     
            //
            // Rule 470:  ArrayBaseType ::= future < Type >
            //
            case 470: {
                TypeNode Type = (TypeNode) getRhsSym(3);
                setResult(nf.Future(pos(), Type));
                break;
            }
     
            //
            // Rule 471:  ArrayBaseType ::= ( Type )
            //
            case 471: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                setResult(Type);
                break;
            }
     
            //
            // Rule 472:  ArrayAccess ::= ExpressionName [ ArgumentList ]
            //
            case 472: {
                Name ExpressionName = (Name) getRhsSym(1);
                List ArgumentList = (List) getRhsSym(3);
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), ExpressionName.toExpr(), (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), ExpressionName.toExpr(), ArgumentList));
                break;
            }
     
            //
            // Rule 473:  ArrayAccess ::= PrimaryNoNewArray [ ArgumentList ]
            //
            case 473: {
                Expr PrimaryNoNewArray = (Expr) getRhsSym(1);
                List ArgumentList = (List) getRhsSym(3);
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), PrimaryNoNewArray, (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), PrimaryNoNewArray, ArgumentList));
                break;
            }
     
            //
            // Rule 490:  NowStatement ::= now ( Clock ) Statement
            //
            case 490: {
                Expr Clock = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
              setResult(nf.Now(pos(), Clock, Statement));
                break;
            }
     
            //
            // Rule 491:  ClockedClause ::= clocked ( ClockList )
            //
            case 491: {
                List ClockList = (List) getRhsSym(3);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 492:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 492: {
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
            // Rule 493:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 493: {
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                Stmt Statement = (Stmt) getRhsSym(3);
              setResult(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                               ? nf.Here(pos(getLeftSpan()))
                                               : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 494:  WhenStatement ::= when ( Expression ) Statement
            //
            case 494: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
                setResult(nf.When(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 495:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 495: {
                When WhenStatement = (When) getRhsSym(1);
                IToken or = (IToken) getRhsIToken(2);
                Expr Expression = (Expr) getRhsSym(4);
                Stmt Statement = (Stmt) getRhsSym(6);
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 496:  ForEachStatement ::= foreach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 496: {
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
            // Rule 497:  AtEachStatement ::= ateach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 497: {
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
            // Rule 498:  EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
            //
            case 498: {
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
            // Rule 499:  FinishStatement ::= finish Statement
            //
            case 499: {
                Stmt Statement = (Stmt) getRhsSym(2);
                setResult(nf.Finish(pos(),  Statement));
                break;
            }
     
            //
            // Rule 500:  NowStatementNoShortIf ::= now ( Clock ) StatementNoShortIf
            //
            case 500: {
                Expr Clock = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                setResult(nf.Now(pos(), Clock, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 501:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt ClockedClauseopt StatementNoShortIf
            //
            case 501: {
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
            // Rule 502:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 502: {
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 503:  WhenStatementNoShortIf ::= when ( Expression ) StatementNoShortIf
            //
            case 503: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                setResult(nf.When(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 504:  WhenStatementNoShortIf ::= WhenStatement or$or ( Expression ) StatementNoShortIf
            //
            case 504: {
                When WhenStatement = (When) getRhsSym(1);
                IToken or = (IToken) getRhsIToken(2);
                Expr Expression = (Expr) getRhsSym(4);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(6);
                WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, StatementNoShortIf);
                setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 505:  ForEachStatementNoShortIf ::= foreach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 505: {
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
            // Rule 506:  AtEachStatementNoShortIf ::= ateach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 506: {
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
            // Rule 507:  EnhancedForStatementNoShortIf ::= for ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 507: {
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
            // Rule 508:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 508: {
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                setResult(nf.Finish(pos(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 509:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 509: {
                Expr PlaceExpression = (Expr) getRhsSym(2);
              setResult(PlaceExpression);
                break;
            }
     
            //
            // Rule 511:  NextStatement ::= next ;
            //
            case 511: {
                
                setResult(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 512:  AwaitStatement ::= await Expression ;
            //
            case 512: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.Await(pos(), Expression));
                break;
            }
     
            //
            // Rule 513:  ClockList ::= Clock
            //
            case 513: {
                Expr Clock = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                break;
            }
     
            //
            // Rule 514:  ClockList ::= ClockList , Clock
            //
            case 514: {
                List ClockList = (List) getRhsSym(1);
                Expr Clock = (Expr) getRhsSym(3);
                ClockList.add(Clock);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 516:  CastExpression ::= ( Type ) UnaryExpressionNotPlusMinus
            //
            case 516: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(4);
                setResult(nf.Cast(pos(), Type, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 517:  CastExpression ::= ( @ Expression ) UnaryExpressionNotPlusMinus
            //
            case 517: {
                Expr Expression = (Expr) getRhsSym(3);
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(5);
                setResult(nf.PlaceCast(pos(), Expression, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 518:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 518: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(3);
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                break;
            }
     
            //
            // Rule 519:  IdentifierList ::= identifier
            //
            case 519: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Name.class, false);
                l.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                setResult(l);
                break;
            }
     
            //
            // Rule 520:  IdentifierList ::= IdentifierList , identifier
            //
            case 520: {
                List IdentifierList = (List) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                IdentifierList.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                setResult(IdentifierList);
                break;
            }
     
            //
            // Rule 521:  Primary ::= here
            //
            case 521: {
                
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
            // Rule 524:  RegionExpression ::= Expression$expr1 : Expression$expr2
            //
            case 524: {
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
            // Rule 525:  RegionExpressionList ::= RegionExpression
            //
            case 525: {
                Expr RegionExpression = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                break;
            }
     
            //
            // Rule 526:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 526: {
                List RegionExpressionList = (List) getRhsSym(1);
                Expr RegionExpression = (Expr) getRhsSym(3);
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                break;
            }
     
            //
            // Rule 527:  Primary ::= [ RegionExpressionList ]
            //
            case 527: {
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
            // Rule 528:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 528: {
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
            // Rule 529:  FutureExpression ::= future PlaceExpressionSingleListopt { Expression }
            //
            case 529: {
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                Expr Expression = (Expr) getRhsSym(4);
                setResult(nf.Future(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(getLeftSpan()))
                                                : PlaceExpressionSingleListopt), Expression));
                break;
            }
     
            //
            // Rule 530:  FieldModifier ::= mutable
            //
            case 530: {
                
                setResult(X10Flags.MUTABLE);
                break;
            }
     
            //
            // Rule 531:  FieldModifier ::= const
            //
            case 531: {
                
                setResult(Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL));
                break;
            }
     
            //
            // Rule 532:  FunExpression ::= fun Type ( FormalParameterListopt ) { Expression }
            //
            case 532:
                throw new Error("No action specified for rule " + 532);
 
            //
            // Rule 533:  MethodInvocation ::= MethodName ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 533:
                throw new Error("No action specified for rule " + 533); 
 
            //
            // Rule 534:  MethodInvocation ::= Primary . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 534:
                throw new Error("No action specified for rule " + 534); 
 
            //
            // Rule 535:  MethodInvocation ::= super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 535:
                throw new Error("No action specified for rule " + 535); 
 
            //
            // Rule 536:  MethodInvocation ::= ClassName . super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 536:
                throw new Error("No action specified for rule " + 536); 
 
            //
            // Rule 537:  MethodInvocation ::= TypeName . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 537:
                throw new Error("No action specified for rule " + 537); 
 
            //
            // Rule 538:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 538:
                throw new Error("No action specified for rule " + 538); 
 
            //
            // Rule 539:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 539:
                throw new Error("No action specified for rule " + 539); 
 
            //
            // Rule 540:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 540:
                throw new Error("No action specified for rule " + 540); 
 
            //
            // Rule 541:  MethodModifier ::= synchronized
            //
            case 541: {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "\"synchronized\" is an invalid X10 Method Modifier");
                setResult(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 542:  FieldModifier ::= volatile
            //
            case 542: {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "\"volatile\" is an invalid X10 Field Modifier");
                setResult(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 543:  SynchronizedStatement ::= synchronized ( Expression ) Block
            //
            case 543: {
                Expr Expression = (Expr) getRhsSym(3);
                Block Block = (Block) getRhsSym(5);
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "Synchronized Statement is invalid in X10");
                setResult(nf.Synchronized(pos(), Expression, Block));
                break;
            }
     
            //
            // Rule 544:  ThisClauseopt ::= $Empty
            //
            case 544:
                setResult(null);
                break;
 
            //
            // Rule 546:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 546:
                setResult(null);
                break;
 
            //
            // Rule 548:  DepParametersopt ::= $Empty
            //
            case 548:
                setResult(null);
                break;
 
            //
            // Rule 550:  PropertyListopt ::= $Empty
            //
            case 550:
                setResult(null);
                break;
 
            //
            // Rule 552:  WhereClauseopt ::= $Empty
            //
            case 552:
                setResult(null);
                break;
 
            //
            // Rule 554:  ObjectKindopt ::= $Empty
            //
            case 554:
                setResult(null);
                break;
 
            //
            // Rule 556:  ArrayInitializeropt ::= $Empty
            //
            case 556:
                setResult(null);
                break;
 
            //
            // Rule 558:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 558:
                setResult(null);
                break;
 
            //
            // Rule 560:  ArgumentListopt ::= $Empty
            //
            case 560:
                setResult(null);
                break;
 
            //
            // Rule 562:  DepParametersopt ::= $Empty
            //
            case 562:
                setResult(null);
                break;
 
            //
            // Rule 564:  Unsafeopt ::= $Empty
            //
            case 564:
                setResult(null);
                break;
 
            //
            // Rule 565:  Unsafeopt ::= unsafe
            //
            case 565: {
                
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 566:  ParamIdopt ::= $Empty
            //
            case 566:
                setResult(null);
                break;
 
            //
            // Rule 567:  ParamIdopt ::= identifier
            //
            case 567: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 568:  ClockedClauseopt ::= $Empty
            //
            case 568: {
                
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

