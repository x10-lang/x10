
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
     // Report.report(1,"X10Parser: GOLDEN resultType=|" + resultType);
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
    // Report.report(1,"X10Parser: GOLDEN type =|" + t +"|");
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
            // Rule 65:  FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators ;
            //
            case 65: {
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
            // Rule 66:  VariableDeclarators ::= VariableDeclarator
            //
            case 66: {
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(1);
                List l = new TypedList(new LinkedList(), X10VarDeclarator.class, false);
                l.add(VariableDeclarator);
                setResult(l);
                break;
            }
     
            //
            // Rule 67:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 67: {
                List VariableDeclarators = (List) getRhsSym(1);
                VarDeclarator VariableDeclarator = (VarDeclarator) getRhsSym(3);
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                break;
            }
     
            //
            // Rule 69:  VariableDeclarator ::= VariableDeclaratorId = VariableInitializer
            //
            case 69: {
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                Expr VariableInitializer = (Expr) getRhsSym(3);
                VariableDeclaratorId.init = VariableInitializer;
                VariableDeclaratorId.position(pos());
                // setResult(VariableDeclaratorId); 
                break;
            }
     
            //
            // Rule 70:  TraditionalVariableDeclaratorId ::= identifier
            //
            case 70: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new X10VarDeclarator(pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 71:  TraditionalVariableDeclaratorId ::= TraditionalVariableDeclaratorId [ ]
            //
            case 71: {
                X10VarDeclarator TraditionalVariableDeclaratorId = (X10VarDeclarator) getRhsSym(1);
                TraditionalVariableDeclaratorId.dims++;
                TraditionalVariableDeclaratorId.position(pos());
                // setResult(a);
                break;
            }
     
            //
            // Rule 73:  VariableDeclaratorId ::= identifier [ IdentifierList ]
            //
            case 73: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                List IdentifierList = (List) getRhsSym(3);
                setResult(new X10VarDeclarator(pos(), identifier.getIdentifier(), IdentifierList));
                break;
            }
     
            //
            // Rule 74:  VariableDeclaratorId ::= [ IdentifierList ]
            //
            case 74: {
                List IdentifierList = (List) getRhsSym(2);
                String name = polyglot.ext.x10.visit.X10PrettyPrinterVisitor.getId();
                setResult(new X10VarDeclarator(pos(), name, IdentifierList));
                break;
            }
     
            //
            // Rule 78:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 78: {
                Flags FieldModifiers = (Flags) getRhsSym(1);
                Flags FieldModifier = (Flags) getRhsSym(2);
                setResult(FieldModifiers.set(FieldModifier));
                break;
            }
     
            //
            // Rule 79:  FieldModifier ::= public
            //
            case 79: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 80:  FieldModifier ::= protected
            //
            case 80: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 81:  FieldModifier ::= private
            //
            case 81: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 82:  FieldModifier ::= static
            //
            case 82: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 83:  FieldModifier ::= final
            //
            case 83: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 84:  FieldModifier ::= transient
            //
            case 84: {
                
                setResult(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 85:  MethodDeclaration ::= MethodHeader MethodBody
            //
            case 85: {
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
            // Rule 87:  ResultType ::= void
            //
            case 87: {
                
                setResult(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 88:  FormalParameterList ::= LastFormalParameter
            //
            case 88: {
                Formal LastFormalParameter = (Formal) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(LastFormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 89:  FormalParameterList ::= FormalParameters , LastFormalParameter
            //
            case 89: {
                List FormalParameters = (List) getRhsSym(1);
                Formal LastFormalParameter = (Formal) getRhsSym(3);
                FormalParameters.add(LastFormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 90:  FormalParameters ::= FormalParameter
            //
            case 90: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                break;
            }
     
            //
            // Rule 91:  FormalParameters ::= FormalParameters , FormalParameter
            //
            case 91: {
                List FormalParameters = (List) getRhsSym(1);
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                FormalParameters.add(FormalParameter);
                // setResult(FormalParameters);
                break;
            }
     
            //
            // Rule 92:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 92: {
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(2);
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(3);
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names()));
                break;
            }
     
            //
            // Rule 94:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 94: {
                Flags VariableModifiers = (Flags) getRhsSym(1);
                Flags VariableModifier = (Flags) getRhsSym(2);
                setResult(VariableModifiers.set(VariableModifier));
                break;
            }
     
            //
            // Rule 95:  VariableModifier ::= final
            //
            case 95: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 96:  LastFormalParameter ::= VariableModifiersopt Type ...opt$opt VariableDeclaratorId
            //
            case 96: {
                Flags VariableModifiersopt = (Flags) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(2);
                Object opt = (Object) getRhsSym(3);
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) getRhsSym(4);
                assert(opt == null);
                setResult(nf.Formal(pos(), VariableModifiersopt, nf.array(Type, pos(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2)), VariableDeclaratorId.dims), VariableDeclaratorId.name, VariableDeclaratorId.names()));
                break;
            }
     
            //
            // Rule 98:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 98: {
                Flags MethodModifiers = (Flags) getRhsSym(1);
                Flags MethodModifier = (Flags) getRhsSym(2);
                setResult(MethodModifiers.set(MethodModifier));
                break;
            }
     
            //
            // Rule 99:  MethodModifier ::= public
            //
            case 99: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 100:  MethodModifier ::= protected
            //
            case 100: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 101:  MethodModifier ::= private
            //
            case 101: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 102:  MethodModifier ::= abstract
            //
            case 102: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 103:  MethodModifier ::= static
            //
            case 103: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 104:  MethodModifier ::= final
            //
            case 104: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 105:  MethodModifier ::= native
            //
            case 105: {
                
                setResult(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 106:  MethodModifier ::= strictfp
            //
            case 106: {
                
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 107:  Throws ::= throws ExceptionTypeList
            //
            case 107: {
                List ExceptionTypeList = (List) getRhsSym(2);
                setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 108:  ExceptionTypeList ::= ExceptionType
            //
            case 108: {
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                break;
            }
     
            //
            // Rule 109:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 109: {
                List ExceptionTypeList = (List) getRhsSym(1);
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                ExceptionTypeList.add(ExceptionType);
                // setResult(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 112:  MethodBody ::= ;
            //
            case 112:
                setResult(null);
                break;
 
            //
            // Rule 114:  StaticInitializer ::= static Block
            //
            case 114: {
                Block Block = (Block) getRhsSym(2);
                setResult(Block);
                break;
            }
     
            //
            // Rule 115:  SimpleTypeName ::= identifier
            //
            case 115: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 117:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 117: {
                Flags ConstructorModifiers = (Flags) getRhsSym(1);
                Flags ConstructorModifier = (Flags) getRhsSym(2);
                setResult(ConstructorModifiers.set(ConstructorModifier));
                break;
            }
     
            //
            // Rule 118:  ConstructorModifier ::= public
            //
            case 118: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 119:  ConstructorModifier ::= protected
            //
            case 119: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 120:  ConstructorModifier ::= private
            //
            case 120: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 121:  ConstructorBody ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 121: {
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
            // Rule 122:  Arguments ::= ( ArgumentListopt )
            //
            case 122: {
                List ArgumentListopt = (List) getRhsSym(2);
                setResult(ArgumentListopt);
                break;
            }
     
            //
            // Rule 125:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 125: {
                Flags InterfaceModifiers = (Flags) getRhsSym(1);
                Flags InterfaceModifier = (Flags) getRhsSym(2);
                setResult(InterfaceModifiers.set(InterfaceModifier));
                break;
            }
     
            //
            // Rule 126:  InterfaceModifier ::= public
            //
            case 126: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 127:  InterfaceModifier ::= protected
            //
            case 127: {
                
                setResult(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 128:  InterfaceModifier ::= private
            //
            case 128: {
                
                setResult(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 129:  InterfaceModifier ::= abstract
            //
            case 129: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 130:  InterfaceModifier ::= static
            //
            case 130: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 131:  InterfaceModifier ::= strictfp
            //
            case 131: {
                
                setResult(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 132:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 132: {
                TypeNode InterfaceType = (TypeNode) getRhsSym(2);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                setResult(l);
                break;
            }
     
            //
            // Rule 133:  ExtendsInterfaces ::= ExtendsInterfaces , InterfaceType
            //
            case 133: {
                List ExtendsInterfaces = (List) getRhsSym(1);
                TypeNode InterfaceType = (TypeNode) getRhsSym(3);
                ExtendsInterfaces.add(InterfaceType);
                // setResult(ExtendsInterfaces);
                break;
            }
     
            //
            // Rule 134:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 134: {
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                break;
            }
     
            //
            // Rule 136:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 136: {
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                break;
            }
     
            //
            // Rule 138:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 138: {
                MethodDecl AbstractMethodDeclaration = (MethodDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(AbstractMethodDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 139:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 139: {
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 140:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 140: {
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                break;
            }
     
            //
            // Rule 141:  InterfaceMemberDeclaration ::= ;
            //
            case 141: {
                
                setResult(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 142:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 142: {
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
            // Rule 144:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 144: {
                Flags ConstantModifiers = (Flags) getRhsSym(1);
                Flags ConstantModifier = (Flags) getRhsSym(2);
                setResult(ConstantModifiers.set(ConstantModifier));
                break;
            }
     
            //
            // Rule 145:  ConstantModifier ::= public
            //
            case 145: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 146:  ConstantModifier ::= static
            //
            case 146: {
                
                setResult(Flags.STATIC);
                break;
            }
     
            //
            // Rule 147:  ConstantModifier ::= final
            //
            case 147: {
                
                setResult(Flags.FINAL);
                break;
            }
     
            //
            // Rule 149:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 149: {
                Flags AbstractMethodModifiers = (Flags) getRhsSym(1);
                Flags AbstractMethodModifier = (Flags) getRhsSym(2);
                setResult(AbstractMethodModifiers.set(AbstractMethodModifier));
                break;
            }
     
            //
            // Rule 150:  AbstractMethodModifier ::= public
            //
            case 150: {
                
                setResult(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 151:  AbstractMethodModifier ::= abstract
            //
            case 151: {
                
                setResult(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 152:  SimpleName ::= identifier
            //
            case 152: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 153:  ArrayInitializer ::= { VariableInitializersopt ,opt$opt }
            //
            case 153: {
                List VariableInitializersopt = (List) getRhsSym(2);
                Object opt = (Object) getRhsSym(3);
                if (VariableInitializersopt == null)
                     setResult(nf.ArrayInit(pos()));
                else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
                break;
            }
     
            //
            // Rule 154:  VariableInitializers ::= VariableInitializer
            //
            case 154: {
                Expr VariableInitializer = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                break;
            }
     
            //
            // Rule 155:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 155: {
                List VariableInitializers = (List) getRhsSym(1);
                Expr VariableInitializer = (Expr) getRhsSym(3);
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                break;
            }
     
            //
            // Rule 156:  Block ::= { BlockStatementsopt }
            //
            case 156: {
                List BlockStatementsopt = (List) getRhsSym(2);
                setResult(nf.Block(pos(), BlockStatementsopt));
                break;
            }
     
            //
            // Rule 157:  BlockStatements ::= BlockStatement
            //
            case 157: {
                List BlockStatement = (List) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                break;
            }
     
            //
            // Rule 158:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 158: {
                List BlockStatements = (List) getRhsSym(1);
                List BlockStatement = (List) getRhsSym(2);
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                break;
            }
     
            //
            // Rule 160:  BlockStatement ::= ClassDeclaration
            //
            case 160: {
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                break;
            }
     
            //
            // Rule 161:  BlockStatement ::= Statement
            //
            case 161: {
                Stmt Statement = (Stmt) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                break;
            }
     
            //
            // Rule 163:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 163: {
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
            // Rule 187:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 187: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
                setResult(nf.If(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 188:  IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
            //
            case 188: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                Stmt Statement = (Stmt) getRhsSym(7);
                setResult(nf.If(pos(), Expression, StatementNoShortIf, Statement));
                break;
            }
     
            //
            // Rule 189:  IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf$true_stmt else StatementNoShortIf$false_stmt
            //
            case 189: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt true_stmt = (Stmt) getRhsSym(5);
                Stmt false_stmt = (Stmt) getRhsSym(7);
                setResult(nf.If(pos(), Expression, true_stmt, false_stmt));
                break;
            }
     
            //
            // Rule 190:  EmptyStatement ::= ;
            //
            case 190: {
                
                setResult(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 191:  LabeledStatement ::= identifier : Statement
            //
            case 191: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                Stmt Statement = (Stmt) getRhsSym(3);
                setResult(nf.Labeled(pos(), identifier.getIdentifier(), Statement));
                break;
            }
     
            //
            // Rule 192:  LabeledStatementNoShortIf ::= identifier : StatementNoShortIf
            //
            case 192: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(3);
                setResult(nf.Labeled(pos(), identifier.getIdentifier(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 193:  ExpressionStatement ::= StatementExpression ;
            //
            case 193: {
                Expr StatementExpression = (Expr) getRhsSym(1);
                setResult(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 201:  AssertStatement ::= assert Expression ;
            //
            case 201: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.Assert(pos(), Expression));
                break;
            }
     
            //
            // Rule 202:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 202: {
                Expr expr1 = (Expr) getRhsSym(2);
                Expr expr2 = (Expr) getRhsSym(4);
                setResult(nf.Assert(pos(), expr1, expr2));
                break;
            }
     
            //
            // Rule 203:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 203: {
                Expr Expression = (Expr) getRhsSym(3);
                List SwitchBlock = (List) getRhsSym(5);
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                break;
            }
     
            //
            // Rule 204:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 204: {
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                List SwitchLabelsopt = (List) getRhsSym(3);
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                break;
            }
     
            //
            // Rule 206:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 206: {
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                break;
            }
     
            //
            // Rule 207:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 207: {
                List SwitchLabels = (List) getRhsSym(1);
                List BlockStatements = (List) getRhsSym(2);
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                break;
            }
     
            //
            // Rule 208:  SwitchLabels ::= SwitchLabel
            //
            case 208: {
                Case SwitchLabel = (Case) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                break;
            }
     
            //
            // Rule 209:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 209: {
                List SwitchLabels = (List) getRhsSym(1);
                Case SwitchLabel = (Case) getRhsSym(2);
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                break;
            }
     
            //
            // Rule 210:  SwitchLabel ::= case ConstantExpression :
            //
            case 210: {
                Expr ConstantExpression = (Expr) getRhsSym(2);
                setResult(nf.Case(pos(), ConstantExpression));
                break;
            }
     
            //
            // Rule 211:  SwitchLabel ::= default :
            //
            case 211: {
                
                setResult(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 212:  WhileStatement ::= while ( Expression ) Statement
            //
            case 212: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
                setResult(nf.While(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 213:  WhileStatementNoShortIf ::= while ( Expression ) StatementNoShortIf
            //
            case 213: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                setResult(nf.While(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 214:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 214: {
                Stmt Statement = (Stmt) getRhsSym(2);
                Expr Expression = (Expr) getRhsSym(5);
                setResult(nf.Do(pos(), Statement, Expression));
                break;
            }
     
            //
            // Rule 217:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 217: {
                List ForInitopt = (List) getRhsSym(3);
                Expr Expressionopt = (Expr) getRhsSym(5);
                List ForUpdateopt = (List) getRhsSym(7);
                Stmt Statement = (Stmt) getRhsSym(9);
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                break;
            }
     
            //
            // Rule 218:  ForStatementNoShortIf ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) StatementNoShortIf
            //
            case 218: {
                List ForInitopt = (List) getRhsSym(3);
                Expr Expressionopt = (Expr) getRhsSym(5);
                List ForUpdateopt = (List) getRhsSym(7);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(9);
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 220:  ForInit ::= LocalVariableDeclaration
            //
            case 220: {
                List LocalVariableDeclaration = (List) getRhsSym(1);
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                break;
            }
     
            //
            // Rule 222:  StatementExpressionList ::= StatementExpression
            //
            case 222: {
                Expr StatementExpression = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                break;
            }
     
            //
            // Rule 223:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 223: {
                List StatementExpressionList = (List) getRhsSym(1);
                Expr StatementExpression = (Expr) getRhsSym(3);
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                //setResult(StatementExpressionList);
                break;
            }
     
            //
            // Rule 224:  BreakStatement ::= break identifieropt ;
            //
            case 224: {
                Name identifieropt = (Name) getRhsSym(2);
                if (identifieropt == null)
                     setResult(nf.Break(pos()));
                else setResult(nf.Break(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 225:  ContinueStatement ::= continue identifieropt ;
            //
            case 225: {
                Name identifieropt = (Name) getRhsSym(2);
                if (identifieropt == null)
                     setResult(nf.Continue(pos()));
                else setResult(nf.Continue(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 226:  ReturnStatement ::= return Expressionopt ;
            //
            case 226: {
                Expr Expressionopt = (Expr) getRhsSym(2);
                setResult(nf.Return(pos(), Expressionopt));
                break;
            }
     
            //
            // Rule 227:  ThrowStatement ::= throw Expression ;
            //
            case 227: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.Throw(pos(), Expression));
                break;
            }
     
            //
            // Rule 228:  TryStatement ::= try Block Catches
            //
            case 228: {
                Block Block = (Block) getRhsSym(2);
                List Catches = (List) getRhsSym(3);
                setResult(nf.Try(pos(), Block, Catches));
                break;
            }
     
            //
            // Rule 229:  TryStatement ::= try Block Catchesopt Finally
            //
            case 229: {
                Block Block = (Block) getRhsSym(2);
                List Catchesopt = (List) getRhsSym(3);
                Block Finally = (Block) getRhsSym(4);
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                break;
            }
     
            //
            // Rule 230:  Catches ::= CatchClause
            //
            case 230: {
                Catch CatchClause = (Catch) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                break;
            }
     
            //
            // Rule 231:  Catches ::= Catches CatchClause
            //
            case 231: {
                List Catches = (List) getRhsSym(1);
                Catch CatchClause = (Catch) getRhsSym(2);
                Catches.add(CatchClause);
                //setResult(Catches);
                break;
            }
     
            //
            // Rule 232:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 232: {
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                Block Block = (Block) getRhsSym(5);
                setResult(nf.Catch(pos(), FormalParameter, Block));
                break;
            }
     
            //
            // Rule 233:  Finally ::= finally Block
            //
            case 233: {
                Block Block = (Block) getRhsSym(2);
                setResult(Block);
                break;
            }
     
            //
            // Rule 237:  PrimaryNoNewArray ::= Type . class
            //
            case 237: {
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
            // Rule 238:  PrimaryNoNewArray ::= void . class
            //
            case 238: {
                
                setResult(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(getLeftSpan()), ts.Void())));
                break;
            }
     
            //
            // Rule 239:  PrimaryNoNewArray ::= this
            //
            case 239: {
                
                setResult(nf.This(pos()));
                break;
            }
     
            //
            // Rule 240:  PrimaryNoNewArray ::= ClassName . this
            //
            case 240: {
                Name ClassName = (Name) getRhsSym(1);
                setResult(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 241:  PrimaryNoNewArray ::= ( Expression )
            //
            case 241: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.ParExpr(pos(), Expression));
                break;
            }
     
            //
            // Rule 246:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 246: {
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.IntegerLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 247:  Literal ::= LongLiteral$LongLiteral
            //
            case 247: {
                IToken LongLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 248:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 248: {
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 249:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 249: {
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 250:  Literal ::= BooleanLiteral
            //
            case 250: {
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 251:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 251: {
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 252:  Literal ::= StringLiteral$str
            //
            case 252: {
                IToken str = (IToken) getRhsIToken(1);
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 253:  Literal ::= null
            //
            case 253: {
                
                setResult(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 254:  BooleanLiteral ::= true$trueLiteral
            //
            case 254: {
                IToken trueLiteral = (IToken) getRhsIToken(1);
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 255:  BooleanLiteral ::= false$falseLiteral
            //
            case 255: {
                IToken falseLiteral = (IToken) getRhsIToken(1);
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                break;
            }
     
            //
            // Rule 256:  ArgumentList ::= Expression
            //
            case 256: {
                Expr Expression = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                break;
            }
     
            //
            // Rule 257:  ArgumentList ::= ArgumentList , Expression
            //
            case 257: {
                List ArgumentList = (List) getRhsSym(1);
                Expr Expression = (Expr) getRhsSym(3);
                ArgumentList.add(Expression);
                //setResult(ArgumentList);
                break;
            }
     
            //
            // Rule 258:  DimExprs ::= DimExpr
            //
            case 258: {
                Expr DimExpr = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                setResult(l);
                break;
            }
     
            //
            // Rule 259:  DimExprs ::= DimExprs DimExpr
            //
            case 259: {
                List DimExprs = (List) getRhsSym(1);
                Expr DimExpr = (Expr) getRhsSym(2);
                DimExprs.add(DimExpr);
                //setResult(DimExprs);
                break;
            }
     
            //
            // Rule 260:  DimExpr ::= [ Expression ]
            //
            case 260: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(Expression.position(pos()));
                break;
            }
     
            //
            // Rule 261:  Dims ::= [ ]
            //
            case 261: {
                
                setResult(new Integer(1));
                break;
            }
     
            //
            // Rule 262:  Dims ::= Dims [ ]
            //
            case 262: {
                Integer Dims = (Integer) getRhsSym(1);
                setResult(new Integer(Dims.intValue() + 1));
                break;
            }
     
            //
            // Rule 263:  FieldAccess ::= Primary . identifier
            //
            case 263: {
                Expr Primary = (Expr) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(nf.Field(pos(), Primary, identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 264:  FieldAccess ::= super . identifier
            //
            case 264: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 265:  FieldAccess ::= ClassName . super$sup . identifier
            //
            case 265: {
                Name ClassName = (Name) getRhsSym(1);
                IToken sup = (IToken) getRhsIToken(3);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 266:  MethodInvocation ::= MethodName ( ArgumentListopt )
            //
            case 266: {
                Name MethodName = (Name) getRhsSym(1);
                List ArgumentListopt = (List) getRhsSym(3);
                setResult(nf.Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, ArgumentListopt));
                break;
            }
     
            //
            // Rule 268:  PostfixExpression ::= ExpressionName
            //
            case 268: {
                Name ExpressionName = (Name) getRhsSym(1);
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 271:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 271: {
                Expr PostfixExpression = (Expr) getRhsSym(1);
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 272:  PostDecrementExpression ::= PostfixExpression --
            //
            case 272: {
                Expr PostfixExpression = (Expr) getRhsSym(1);
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 275:  UnaryExpression ::= + UnaryExpression
            //
            case 275: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpression));
                break;
            }
     
            //
            // Rule 276:  UnaryExpression ::= - UnaryExpression
            //
            case 276: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpression));
                break;
            }
     
            //
            // Rule 278:  PreIncrementExpression ::= ++ UnaryExpression
            //
            case 278: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpression));
                break;
            }
     
            //
            // Rule 279:  PreDecrementExpression ::= -- UnaryExpression
            //
            case 279: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpression));
                break;
            }
     
            //
            // Rule 281:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 281: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 282:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 282: {
                Expr UnaryExpression = (Expr) getRhsSym(2);
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 285:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 285: {
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                Expr UnaryExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                break;
            }
     
            //
            // Rule 286:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 286: {
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                Expr UnaryExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                break;
            }
     
            //
            // Rule 287:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 287: {
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                Expr UnaryExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                break;
            }
     
            //
            // Rule 289:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 289: {
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 290:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 290: {
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 292:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 292: {
                Expr ShiftExpression = (Expr) getRhsSym(1);
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                break;
            }
     
            //
            // Rule 293:  ShiftExpression ::= ShiftExpression > > AdditiveExpression
            //
            case 293: {
                Expr ShiftExpression = (Expr) getRhsSym(1);
                Expr AdditiveExpression = (Expr) getRhsSym(4);
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 294:  ShiftExpression ::= ShiftExpression > > > AdditiveExpression
            //
            case 294: {
                Expr ShiftExpression = (Expr) getRhsSym(1);
                Expr AdditiveExpression = (Expr) getRhsSym(5);
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 296:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 296: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
                break;
            }
     
            //
            // Rule 297:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 297: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
                break;
            }
     
            //
            // Rule 298:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 298: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
                break;
            }
     
            //
            // Rule 299:  RelationalExpression ::= RelationalExpression > = ShiftExpression
            //
            case 299: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                Expr ShiftExpression = (Expr) getRhsSym(4);
                // TODO: make sure that there is no space after the ">" signs
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
                break;
            }
     
            //
            // Rule 301:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 301: {
                Expr EqualityExpression = (Expr) getRhsSym(1);
                Expr RelationalExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                break;
            }
     
            //
            // Rule 302:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 302: {
                Expr EqualityExpression = (Expr) getRhsSym(1);
                Expr RelationalExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                break;
            }
     
            //
            // Rule 304:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 304: {
                Expr AndExpression = (Expr) getRhsSym(1);
                Expr EqualityExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                break;
            }
     
            //
            // Rule 306:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 306: {
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                Expr AndExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                break;
            }
     
            //
            // Rule 308:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 308: {
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 310:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 310: {
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                break;
            }
     
            //
            // Rule 312:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 312: {
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                break;
            }
     
            //
            // Rule 314:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 314: {
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                Expr Expression = (Expr) getRhsSym(3);
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 317:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 317: {
                Expr LeftHandSide = (Expr) getRhsSym(1);
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 318:  LeftHandSide ::= ExpressionName
            //
            case 318: {
                Name ExpressionName = (Name) getRhsSym(1);
                setResult(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 321:  AssignmentOperator ::= =
            //
            case 321: {
                
                setResult(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 322:  AssignmentOperator ::= *=
            //
            case 322: {
                
                setResult(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 323:  AssignmentOperator ::= /=
            //
            case 323: {
                
                setResult(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 324:  AssignmentOperator ::= %=
            //
            case 324: {
                
                setResult(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 325:  AssignmentOperator ::= +=
            //
            case 325: {
                
                setResult(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 326:  AssignmentOperator ::= -=
            //
            case 326: {
                
                setResult(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 327:  AssignmentOperator ::= <<=
            //
            case 327: {
                
                setResult(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 328:  AssignmentOperator ::= > > =
            //
            case 328: {
                
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 329:  AssignmentOperator ::= > > > =
            //
            case 329: {
                
                // TODO: make sure that there is no space after the ">" signs
                setResult(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 330:  AssignmentOperator ::= &=
            //
            case 330: {
                
                setResult(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 331:  AssignmentOperator ::= ^=
            //
            case 331: {
                
                setResult(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 332:  AssignmentOperator ::= |=
            //
            case 332: {
                
                setResult(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 335:  Dimsopt ::= $Empty
            //
            case 335: {
                
                setResult(new Integer(0));
                break;
            }
     
            //
            // Rule 337:  Catchesopt ::= $Empty
            //
            case 337: {
                
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 339:  identifieropt ::= $Empty
            //
            case 339:
                setResult(null);
                break;
 
            //
            // Rule 340:  identifieropt ::= identifier
            //
            case 340: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 341:  ForUpdateopt ::= $Empty
            //
            case 341: {
                
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 343:  Expressionopt ::= $Empty
            //
            case 343:
                setResult(null);
                break;
 
            //
            // Rule 345:  ForInitopt ::= $Empty
            //
            case 345: {
                
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 347:  SwitchLabelsopt ::= $Empty
            //
            case 347: {
                
                setResult(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 349:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 349: {
                
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 351:  VariableModifiersopt ::= $Empty
            //
            case 351: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 353:  VariableInitializersopt ::= $Empty
            //
            case 353:
                setResult(null);
                break;
 
            //
            // Rule 355:  AbstractMethodModifiersopt ::= $Empty
            //
            case 355: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 357:  ConstantModifiersopt ::= $Empty
            //
            case 357: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 359:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 359: {
                
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 361:  ExtendsInterfacesopt ::= $Empty
            //
            case 361: {
                
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 363:  InterfaceModifiersopt ::= $Empty
            //
            case 363: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 365:  ClassBodyopt ::= $Empty
            //
            case 365:
                setResult(null);
                break;
 
            //
            // Rule 367:  Argumentsopt ::= $Empty
            //
            case 367:
                setResult(null);
                break;
 
            //
            // Rule 368:  Argumentsopt ::= Arguments
            //
            case 368:
                throw new Error("No action specified for rule " + 368);
 
            //
            // Rule 369:  ,opt ::= $Empty
            //
            case 369:
                setResult(null);
                break;
 
            //
            // Rule 371:  ArgumentListopt ::= $Empty
            //
            case 371: {
                
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 373:  BlockStatementsopt ::= $Empty
            //
            case 373: {
                
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 375:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 375:
                setResult(null);
                break;
 
            //
            // Rule 377:  ConstructorModifiersopt ::= $Empty
            //
            case 377: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 379:  ...opt ::= $Empty
            //
            case 379:
                setResult(null);
                break;
 
            //
            // Rule 381:  FormalParameterListopt ::= $Empty
            //
            case 381: {
                
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 383:  Throwsopt ::= $Empty
            //
            case 383: {
                
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 385:  MethodModifiersopt ::= $Empty
            //
            case 385: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 387:  FieldModifiersopt ::= $Empty
            //
            case 387: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 389:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 389: {
                
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 391:  Interfacesopt ::= $Empty
            //
            case 391: {
                
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 393:  Superopt ::= $Empty
            //
            case 393: {
                
               setResult(new Name(nf, ts, pos(), "x10.lang.Object").toType());
                break;
            }
     
            //
            // Rule 395:  ClassModifiersopt ::= $Empty
            //
            case 395: {
                
                setResult(Flags.NONE);
                break;
            }
     
            //
            // Rule 397:  TypeDeclarationsopt ::= $Empty
            //
            case 397: {
                
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 399:  ImportDeclarationsopt ::= $Empty
            //
            case 399: {
                
                setResult(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 401:  PackageDeclarationopt ::= $Empty
            //
            case 401:
                setResult(null);
                break;
 
            //
            // Rule 403:  ClassType ::= TypeName DepParametersopt
            //
            case 403: {
                Name TypeName = (Name) getRhsSym(1);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                     setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 404:  InterfaceType ::= TypeName DepParametersopt
            //
            case 404: {
                Name TypeName = (Name) getRhsSym(1);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                 setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 405:  PackageDeclaration ::= package PackageName ;
            //
            case 405: {
                Name PackageName = (Name) getRhsSym(2);
                setResult(PackageName.toPackage());
                break;
            }
     
            //
            // Rule 406:  NormalClassDeclaration ::= ClassModifiersopt class identifier DepParametersopt Superopt Interfacesopt ClassBody
            //
            case 406: {
                Flags ClassModifiersopt = (Flags) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                TypeNode Superopt = (TypeNode) getRhsSym(5);
                List Interfacesopt = (List) getRhsSym(6);
                ClassBody ClassBody = (ClassBody) getRhsSym(7);
    // vj TODO: Add processing of DepParametersopt
                checkTypeName(identifier);
                setResult(X10Flags.isValue(ClassModifiersopt)
                             ? nf.ValueClassDecl(pos(getLeftSpan(), getRightSpan()),
                                                 ClassModifiersopt, identifier.getIdentifier(), Superopt, Interfacesopt, ClassBody)
                             : nf.ClassDecl(pos(getLeftSpan(), getRightSpan()),
                                            ClassModifiersopt, identifier.getIdentifier(), Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 407:  MethodHeader ::= MethodModifiersopt ResultType MethodDeclarator Throwsopt
            //
            case 407: {
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
            // Rule 408:  ExplicitConstructorInvocation ::= this ( ArgumentListopt ) ;
            //
            case 408: {
                List ArgumentListopt = (List) getRhsSym(3);
                setResult(nf.ThisCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 409:  ExplicitConstructorInvocation ::= super ( ArgumentListopt ) ;
            //
            case 409: {
                List ArgumentListopt = (List) getRhsSym(3);
                setResult(nf.SuperCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 410:  ExplicitConstructorInvocation ::= Primary . this ( ArgumentListopt ) ;
            //
            case 410: {
                Expr Primary = (Expr) getRhsSym(1);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.ThisCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 411:  ExplicitConstructorInvocation ::= Primary . super ( ArgumentListopt ) ;
            //
            case 411: {
                Expr Primary = (Expr) getRhsSym(1);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.SuperCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 412:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier ExtendsInterfacesopt InterfaceBody
            //
            case 412: {
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
            // Rule 413:  AbstractMethodDeclaration ::= AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt ;
            //
            case 413: {
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
            // Rule 414:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
            //
            case 414: {
                TypeNode ClassOrInterfaceType = (TypeNode) getRhsSym(2);
                List ArgumentListopt = (List) getRhsSym(4);
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(6);
                if (ClassBodyopt == null)
                     setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt));
                else setResult(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 415:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 415: {
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
            // Rule 416:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 416: {
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
            // Rule 417:  MethodInvocation ::= Primary . identifier ( ArgumentListopt )
            //
            case 417: {
                Expr Primary = (Expr) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.Call(pos(), Primary, identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 418:  MethodInvocation ::= super . identifier ( ArgumentListopt )
            //
            case 418: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                List ArgumentListopt = (List) getRhsSym(5);
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 419:  MethodInvocation ::= ClassName . super$sup . identifier ( ArgumentListopt )
            //
            case 419: {
                Name ClassName = (Name) getRhsSym(1);
                IToken sup = (IToken) getRhsIToken(3);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(5);
                List ArgumentListopt = (List) getRhsSym(7);
                setResult(nf.Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 420:  Type ::= DataType PlaceTypeSpecifieropt
            //
            case 420: {
                TypeNode DataType = (TypeNode) getRhsSym(1);
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(2);
                // Just parse the placetype and drop it for now.
                break;
            }
     
            //
            // Rule 421:  Type ::= nullable Type
            //
            case 421: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                setResult(nf.Nullable(pos(), Type));
                break;
            }
     
            //
            // Rule 422:  Type ::= future < Type >
            //
            case 422: {
                TypeNode Type = (TypeNode) getRhsSym(3);
                setResult(nf.Future(pos(), Type));
                break;
            }
     
            //
            // Rule 423:  Type ::= ( Type ) DepParametersopt
            //
            case 423: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
          // System.out.println("Parser: parsed (Type) DepParmetersopt |" + Type + "| |" + DepParametersopt +"|");
                setResult(DepParametersopt == null ? Type 
                : ((X10TypeNode) Type).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 427:  PrimitiveType ::= NumericType DepParametersopt
            //
            case 427: {
                TypeNode NumericType = (TypeNode) getRhsSym(1);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
               // System.out.println("Parser: parsed PrimitiveType |" + NumericType + "| |" + DepParametersopt +"|");
                setResult(DepParametersopt == null
                               ? NumericType
                               : ((X10TypeNode) NumericType).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 428:  PrimitiveType ::= boolean DepParametersopt
            //
            case 428: {
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                X10TypeNode res = (X10TypeNode) nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(DepParametersopt==null 
                           ? res 
                           : res.dep(null, DepParametersopt));
               break;
            }
     
            //
            // Rule 432:  ClassOrInterfaceType ::= TypeName PlaceTypeSpecifieropt DepParametersopt
            //
            case 432: {
                Name TypeName = (Name) getRhsSym(1);
                Object PlaceTypeSpecifieropt = (Object) getRhsSym(2);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(3);
                setResult(DepParametersopt == null
                               ? TypeName.toType()
                               : ((X10TypeNode) TypeName.toType()).dep(null, DepParametersopt));
                break;
            }
     
            //
            // Rule 433:  DepParameters ::= ( DepParameterExpr )
            //
            case 433: {
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(2);
                setResult(DepParameterExpr);
                break;
            }
     
            //
            // Rule 434:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 434: {
                List ArgumentList = (List) getRhsSym(1);
                Expr WhereClauseopt = (Expr) getRhsSym(2);
                setResult(nf.DepParameterExpr(pos(), ArgumentList, WhereClauseopt));
                break;
            }
     
            //
            // Rule 435:  DepParameterExpr ::= WhereClause
            //
            case 435: {
                Expr WhereClause = (Expr) getRhsSym(1);
                setResult(nf.DepParameterExpr(pos(), Collections.EMPTY_LIST, WhereClause));
                break;
            }
     
            //
            // Rule 436:  WhereClause ::= : Expression
            //
            case 436: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(Expression);
                break;
            }
     
            //
            // Rule 438:  X10ArrayType ::= Type [ . ]
            //
            case 438: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, null));
                break;
            }
     
            //
            // Rule 439:  X10ArrayType ::= Type reference [ . ]
            //
            case 439: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, null));
                break;
            }
     
            //
            // Rule 440:  X10ArrayType ::= Type value [ . ]
            //
            case 440: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, null));
                break;
            }
     
            //
            // Rule 441:  X10ArrayType ::= Type [ DepParameterExpr ]
            //
            case 441: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(3);
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
                break;
            }
     
            //
            // Rule 442:  X10ArrayType ::= Type reference [ DepParameterExpr ]
            //
            case 442: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(4);
                setResult(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
                break;
            }
     
            //
            // Rule 443:  X10ArrayType ::= Type value [ DepParameterExpr ]
            //
            case 443: {
                TypeNode Type = (TypeNode) getRhsSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) getRhsSym(4);
                setResult(nf.X10ArrayTypeNode(pos(), Type, true, DepParameterExpr));
                break;
            }
     
            //
            // Rule 444:  ObjectKind ::= value
            //
            case 444:
                throw new Error("No action specified for rule " + 444);
 
            //
            // Rule 445:  ObjectKind ::= reference
            //
            case 445:
                throw new Error("No action specified for rule " + 445);
 
            //
            // Rule 446:  MethodModifier ::= atomic
            //
            case 446: {
                
                setResult(X10Flags.ATOMIC);
                break;
            }
     
            //
            // Rule 447:  MethodModifier ::= extern
            //
            case 447: {
                
                setResult(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 449:  ValueClassDeclaration ::= ClassModifiersopt value identifier Superopt Interfacesopt ClassBody
            //
            case 449: {
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
            // Rule 450:  ValueClassDeclaration ::= ClassModifiersopt value class identifier Superopt Interfacesopt ClassBody
            //
            case 450: {
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
            // Rule 451:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 451: {
                Flags ConstructorModifiersopt = (Flags) getRhsSym(1);
                Object[] ConstructorDeclarator = (Object[]) getRhsSym(2);
                List Throwsopt = (List) getRhsSym(3);
                Block ConstructorBody = (Block) getRhsSym(4);
                Name a = (Name) ConstructorDeclarator[1];
                DepParameterExpr c = (DepParameterExpr) ConstructorDeclarator[2];
                List b = (List) ConstructorDeclarator[3];

               setResult(nf.ConstructorDecl(pos(), ConstructorModifiersopt, a.toString(), b, Throwsopt, ConstructorBody));
               break;
            }
    
            //
            // Rule 452:  ConstructorDeclarator ::= SimpleTypeName DepParametersopt ( FormalParameterListopt )
            //
            case 452: {
                Name SimpleTypeName = (Name) getRhsSym(1);
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(2);
                List FormalParameterListopt = (List) getRhsSym(4);
             Object[] a = new Object[4];
             a[1] = SimpleTypeName;
             a[2] = DepParametersopt;
             a[3] = FormalParameterListopt;
             setResult(a);
               break;
            }
    
            //
            // Rule 454:  MethodDeclarator ::= ThisClauseopt identifier ( FormalParameterListopt )
            //
            case 454: {
                TypeNode ThisClauseopt = (TypeNode) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(2);
                List FormalParameterListopt = (List) getRhsSym(4);
                // vj: TODO -- add processing of ThisClause, the this-restriction.
                Object[] a = new Object[3];
               a[0] = new Name(nf, ts, pos(), identifier.getIdentifier());
                a[1] = FormalParameterListopt;
               a[2] = new Integer(0);
                setResult(a);
                break;
            }
     
            //
            // Rule 455:  MethodDeclarator ::= MethodDeclarator [ ]
            //
            case 455: {
                Object[] MethodDeclarator = (Object[]) getRhsSym(1);
                MethodDeclarator[2] = new Integer(((Integer) MethodDeclarator[2]).intValue() + 1);
                // setResult(MethodDeclarator);
               break;
            }
     
            //
            // Rule 456:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt Dims ArrayInitializer
            //
            case 456: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Unsafeopt = (Object) getRhsSym(3);
                Integer Dims = (Integer) getRhsSym(4);
                ArrayInit ArrayInitializer = (ArrayInit) getRhsSym(5);
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Dims.intValue(), ArrayInitializer));
                break;
            }
     
            //
            // Rule 457:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr Dims
            //
            case 457: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Unsafeopt = (Object) getRhsSym(3);
                Expr DimExpr = (Expr) getRhsSym(4);
                Integer Dims = (Integer) getRhsSym(5);
                // setResult(nf.ArrayConstructor(pos(), a, false, null, d));
                setResult(nf.NewArray(pos(), ArrayBaseType, Collections.singletonList(DimExpr), Dims.intValue()));
                break;
            }
     
            //
            // Rule 458:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt DimExpr DimExprs Dimsopt
            //
            case 458: {
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
            // Rule 459:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ]
            //
            case 459: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Valueopt = (Object) getRhsSym(3);
                Object Unsafeopt = (Object) getRhsSym(4);
                Expr Expression = (Expr) getRhsSym(6);
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, Expression, null));
                break;
            }
     
            //
            // Rule 460:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression$distr ] Expression$initializer
            //
            case 460: {
                TypeNode ArrayBaseType = (TypeNode) getRhsSym(2);
                Object Valueopt = (Object) getRhsSym(3);
                Object Unsafeopt = (Object) getRhsSym(4);
                Expr distr = (Expr) getRhsSym(6);
                Expr initializer = (Expr) getRhsSym(8);
                setResult(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, Valueopt != null, distr, initializer));
                break;
            }
     
            //
            // Rule 461:  ArrayCreationExpression ::= new ArrayBaseType Valueopt Unsafeopt [ Expression ] ($lparen FormalParameter ) MethodBody
            //
            case 461: {
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
            // Rule 462:  Valueopt ::= $Empty
            //
            case 462:
                setResult(null);
                break;
 
            //
            // Rule 463:  Valueopt ::= value
            //
            case 463: {
                
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 466:  ArrayBaseType ::= nullable Type
            //
            case 466: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                setResult(nf.Nullable(pos(), Type));
                break;
            }
     
            //
            // Rule 467:  ArrayBaseType ::= future < Type >
            //
            case 467: {
                TypeNode Type = (TypeNode) getRhsSym(3);
                setResult(nf.Future(pos(), Type));
                break;
            }
     
            //
            // Rule 468:  ArrayBaseType ::= ( Type )
            //
            case 468: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                setResult(Type);
                break;
            }
     
            //
            // Rule 469:  ArrayAccess ::= ExpressionName [ ArgumentList ]
            //
            case 469: {
                Name ExpressionName = (Name) getRhsSym(1);
                List ArgumentList = (List) getRhsSym(3);
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), ExpressionName.toExpr(), (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), ExpressionName.toExpr(), ArgumentList));
                break;
            }
     
            //
            // Rule 470:  ArrayAccess ::= PrimaryNoNewArray [ ArgumentList ]
            //
            case 470: {
                Expr PrimaryNoNewArray = (Expr) getRhsSym(1);
                List ArgumentList = (List) getRhsSym(3);
                if (ArgumentList.size() == 1)
                     setResult(nf.X10ArrayAccess1(pos(), PrimaryNoNewArray, (Expr) ArgumentList.get(0)));
                else setResult(nf.X10ArrayAccess(pos(), PrimaryNoNewArray, ArgumentList));
                break;
            }
     
            //
            // Rule 487:  NowStatement ::= now ( Clock ) Statement
            //
            case 487: {
                Expr Clock = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
              setResult(nf.Now(pos(), Clock, Statement));
                break;
            }
     
            //
            // Rule 488:  ClockedClause ::= clocked ( ClockList )
            //
            case 488: {
                List ClockList = (List) getRhsSym(3);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 489:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 489: {
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
            // Rule 490:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 490: {
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                Stmt Statement = (Stmt) getRhsSym(3);
              setResult(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                               ? nf.Here(pos(getLeftSpan()))
                                               : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 491:  WhenStatement ::= when ( Expression ) Statement
            //
            case 491: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt Statement = (Stmt) getRhsSym(5);
                setResult(nf.When(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 492:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 492: {
                When WhenStatement = (When) getRhsSym(1);
                IToken or = (IToken) getRhsIToken(2);
                Expr Expression = (Expr) getRhsSym(4);
                Stmt Statement = (Stmt) getRhsSym(6);
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 493:  ForEachStatement ::= foreach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 493: {
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
            // Rule 494:  AtEachStatement ::= ateach ( FormalParameter : Expression ) ClockedClauseopt Statement
            //
            case 494: {
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
            // Rule 495:  EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
            //
            case 495: {
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
            // Rule 496:  FinishStatement ::= finish Statement
            //
            case 496: {
                Stmt Statement = (Stmt) getRhsSym(2);
                setResult(nf.Finish(pos(),  Statement));
                break;
            }
     
            //
            // Rule 497:  NowStatementNoShortIf ::= now ( Clock ) StatementNoShortIf
            //
            case 497: {
                Expr Clock = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                setResult(nf.Now(pos(), Clock, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 498:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt ClockedClauseopt StatementNoShortIf
            //
            case 498: {
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
            // Rule 499:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 499: {
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 500:  WhenStatementNoShortIf ::= when ( Expression ) StatementNoShortIf
            //
            case 500: {
                Expr Expression = (Expr) getRhsSym(3);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(5);
                setResult(nf.When(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 501:  WhenStatementNoShortIf ::= WhenStatement or$or ( Expression ) StatementNoShortIf
            //
            case 501: {
                When WhenStatement = (When) getRhsSym(1);
                IToken or = (IToken) getRhsIToken(2);
                Expr Expression = (Expr) getRhsSym(4);
                Stmt StatementNoShortIf = (Stmt) getRhsSym(6);
                WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, StatementNoShortIf);
                setResult(WhenStatement);
                break;
            }
     
            //
            // Rule 502:  ForEachStatementNoShortIf ::= foreach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 502: {
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
            // Rule 503:  AtEachStatementNoShortIf ::= ateach ( FormalParameter : Expression ) ClockedClauseopt StatementNoShortIf
            //
            case 503: {
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
            // Rule 504:  EnhancedForStatementNoShortIf ::= for ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 504: {
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
            // Rule 505:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 505: {
                Stmt StatementNoShortIf = (Stmt) getRhsSym(2);
                setResult(nf.Finish(pos(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 506:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 506: {
                Expr PlaceExpression = (Expr) getRhsSym(2);
              setResult(PlaceExpression);
                break;
            }
     
            //
            // Rule 508:  NextStatement ::= next ;
            //
            case 508: {
                
                setResult(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 509:  AwaitStatement ::= await Expression ;
            //
            case 509: {
                Expr Expression = (Expr) getRhsSym(2);
                setResult(nf.Await(pos(), Expression));
                break;
            }
     
            //
            // Rule 510:  ClockList ::= Clock
            //
            case 510: {
                Expr Clock = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                break;
            }
     
            //
            // Rule 511:  ClockList ::= ClockList , Clock
            //
            case 511: {
                List ClockList = (List) getRhsSym(1);
                Expr Clock = (Expr) getRhsSym(3);
                ClockList.add(Clock);
                setResult(ClockList);
                break;
            }
     
            //
            // Rule 513:  CastExpression ::= ( Type ) UnaryExpressionNotPlusMinus
            //
            case 513: {
                TypeNode Type = (TypeNode) getRhsSym(2);
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(4);
                setResult(nf.Cast(pos(), Type, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 514:  CastExpression ::= ( @ Expression ) UnaryExpressionNotPlusMinus
            //
            case 514: {
                Expr Expression = (Expr) getRhsSym(3);
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(5);
                setResult(nf.PlaceCast(pos(), Expression, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 515:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 515: {
                Expr RelationalExpression = (Expr) getRhsSym(1);
                TypeNode Type = (TypeNode) getRhsSym(3);
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                break;
            }
     
            //
            // Rule 516:  IdentifierList ::= identifier
            //
            case 516: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Name.class, false);
                l.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                setResult(l);
                break;
            }
     
            //
            // Rule 517:  IdentifierList ::= IdentifierList , identifier
            //
            case 517: {
                List IdentifierList = (List) getRhsSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(3);
                IdentifierList.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                setResult(IdentifierList);
                break;
            }
     
            //
            // Rule 518:  Primary ::= here
            //
            case 518: {
                
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
            // Rule 521:  RegionExpression ::= Expression$expr1 : Expression$expr2
            //
            case 521: {
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
            // Rule 522:  RegionExpressionList ::= RegionExpression
            //
            case 522: {
                Expr RegionExpression = (Expr) getRhsSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                break;
            }
     
            //
            // Rule 523:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 523: {
                List RegionExpressionList = (List) getRhsSym(1);
                Expr RegionExpression = (Expr) getRhsSym(3);
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                break;
            }
     
            //
            // Rule 524:  Primary ::= [ RegionExpressionList ]
            //
            case 524: {
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
            // Rule 525:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 525: {
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
            // Rule 526:  FutureExpression ::= future PlaceExpressionSingleListopt { Expression }
            //
            case 526: {
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                Expr Expression = (Expr) getRhsSym(4);
                setResult(nf.Future(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(getLeftSpan()))
                                                : PlaceExpressionSingleListopt), Expression));
                break;
            }
     
            //
            // Rule 527:  FieldModifier ::= mutable
            //
            case 527: {
                
                setResult(X10Flags.MUTABLE);
                break;
            }
     
            //
            // Rule 528:  FieldModifier ::= const
            //
            case 528: {
                
                setResult(Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL));
                break;
            }
     
            //
            // Rule 529:  FunExpression ::= fun Type ( FormalParameterListopt ) { Expression }
            //
            case 529:
                throw new Error("No action specified for rule " + 529);
 
            //
            // Rule 530:  MethodInvocation ::= MethodName ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 530:
                throw new Error("No action specified for rule " + 530); 
 
            //
            // Rule 531:  MethodInvocation ::= Primary . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 531:
                throw new Error("No action specified for rule " + 531); 
 
            //
            // Rule 532:  MethodInvocation ::= super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 532:
                throw new Error("No action specified for rule " + 532); 
 
            //
            // Rule 533:  MethodInvocation ::= ClassName . super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 533:
                throw new Error("No action specified for rule " + 533); 
 
            //
            // Rule 534:  MethodInvocation ::= TypeName . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 534:
                throw new Error("No action specified for rule " + 534); 
 
            //
            // Rule 535:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 535:
                throw new Error("No action specified for rule " + 535); 
 
            //
            // Rule 536:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 536:
                throw new Error("No action specified for rule " + 536); 
 
            //
            // Rule 537:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 537:
                throw new Error("No action specified for rule " + 537); 
 
            //
            // Rule 538:  MethodModifier ::= synchronized
            //
            case 538: {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "\"synchronized\" is an invalid X10 Method Modifier");
                setResult(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 539:  FieldModifier ::= volatile
            //
            case 539: {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "\"volatile\" is an invalid X10 Field Modifier");
                setResult(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 540:  SynchronizedStatement ::= synchronized ( Expression ) Block
            //
            case 540: {
                Expr Expression = (Expr) getRhsSym(3);
                Block Block = (Block) getRhsSym(5);
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, getErrorLocation(getLeftSpan(), getRightSpan()) +
                                                   "Synchronized Statement is invalid in X10");
                setResult(nf.Synchronized(pos(), Expression, Block));
                break;
            }
     
            //
            // Rule 541:  ThisClauseopt ::= $Empty
            //
            case 541:
                setResult(null);
                break;
 
            //
            // Rule 543:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 543:
                setResult(null);
                break;
 
            //
            // Rule 545:  DepParametersopt ::= $Empty
            //
            case 545:
                setResult(null);
                break;
 
            //
            // Rule 547:  WhereClauseopt ::= $Empty
            //
            case 547:
                setResult(null);
                break;
 
            //
            // Rule 549:  ObjectKindopt ::= $Empty
            //
            case 549:
                setResult(null);
                break;
 
            //
            // Rule 551:  ArrayInitializeropt ::= $Empty
            //
            case 551:
                setResult(null);
                break;
 
            //
            // Rule 553:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 553:
                setResult(null);
                break;
 
            //
            // Rule 555:  ArgumentListopt ::= $Empty
            //
            case 555:
                setResult(null);
                break;
 
            //
            // Rule 557:  DepParametersopt ::= $Empty
            //
            case 557:
                setResult(null);
                break;
 
            //
            // Rule 559:  Unsafeopt ::= $Empty
            //
            case 559:
                setResult(null);
                break;
 
            //
            // Rule 560:  Unsafeopt ::= unsafe
            //
            case 560: {
                
                // any value distinct from null
                setResult(this);
                break;
            }
     
            //
            // Rule 561:  ParamIdopt ::= $Empty
            //
            case 561:
                setResult(null);
                break;
 
            //
            // Rule 562:  ParamIdopt ::= identifier
            //
            case 562: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                setResult(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 563:  ClockedClauseopt ::= $Empty
            //
            case 563: {
                
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

