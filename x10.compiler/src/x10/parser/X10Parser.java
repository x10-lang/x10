
//
// This is the grammar specification from the Final Draft of the generic spec.
// It has been modified by Philippe Charles and Vijay Saraswat for use with 
// X10. 
// (1) Removed TypeParameters from class/interface/method declarations
// (2) Removed TypeParameters from types.
// (3) Removed Annotations -- cause conflicts with @ used in places.
// (4) Removed EnumDeclarations.
// 12/28/2004// 12/25/2004
// This is the basic X10 grammar specification without support for generic types.
// Intended for the Feb 2005 X10 release.
//

package x10.parser;

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
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import polyglot.util.TypedList;

import com.ibm.lpg.BacktrackingParser;
import com.ibm.lpg.BadParseException;
import com.ibm.lpg.BadParseSymFileException;
import com.ibm.lpg.DiagnoseParser;
import com.ibm.lpg.LexStream;
import com.ibm.lpg.NotBacktrackParseTableException;
import com.ibm.lpg.NullExportedSymbolsException;
import com.ibm.lpg.NullTerminalSymbolsException;
import com.ibm.lpg.ParseTable;
import com.ibm.lpg.PrsStream;
import com.ibm.lpg.RuleAction;
import com.ibm.lpg.UndefinedEofSymbolException;
import com.ibm.lpg.UnimplementedTerminalsException;

import com.ibm.lpg.*;

public class X10Parser extends PrsStream implements RuleAction, Parser
{
    X10Parser prsStream;
    LexStream lexStream;
    ParseTable prs;
    BacktrackingParser btParser;

    public X10Parser(LexStream lexStream)
    {
        super(lexStream);
        this.lexStream = lexStream;
        this.prsStream = this;
        this.prs = new X10Parserprs();

        try
        {
            prsStream.remapTerminalSymbols(orderedTerminalSymbols(), X10Parserprs.EOFT_SYMBOL);
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
            System.out.println("The Lexer does not implement the Eof symbol " +
                               X10Parsersym.orderedTerminalSymbols[X10Parserprs.EOFT_SYMBOL]);
            throw new Error(e);
        } 
    }

    public String[] orderedTerminalSymbols() { return X10Parsersym.orderedTerminalSymbols; }
        
    public polyglot.ast.Node parser()
    {
        try
        {
            btParser = new BacktrackingParser(this, prs, this);
        }
        catch (NotBacktrackParseTableException e)
        {
            System.out.println("****Error: Regenerate X10Parserprs.java with -BACKTRACK option");
            throw new Error(e);
        }
        catch (BadParseSymFileException e)
        {
            System.out.println("****Error: Bad Parser Symbol File -- X10Parsersym.java");
            throw new Error(e);
        }

        try
        {
            return (polyglot.ast.Node) btParser.parse();
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

        
    public X10Parser(LexStream lexStream, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
    {
        this(lexStream);
        this.ts = (X10TypeSystem) t;
        this.nf = (X10NodeFactory) n;
        this.source = source;
        this.eq = q;
    }

    public void reportError(int errorCode, String locationInfo, int leftToken, int rightToken, String tokenText)
    {
        if (errorCode == DELETION_CODE ||
            errorCode == MISPLACED_CODE) tokenText = "";
        if (! tokenText.equals("")) tokenText += ' ';
        eq.enqueue(ErrorInfo.SYNTAX_ERROR, locationInfo + tokenText + errorMsgText[errorCode]);
    }

    public polyglot.ast.Node parse() {
        try
        {
            SourceFile sf = (SourceFile) parser();
            if (bad_rule != 0)
                throw new RuntimeException("Rule " + bad_rule + " has not yet been implemented");

            if (sf != null)
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
        return prsStream.getFileName();
    }

    private Position pos()
    {
        int i = btParser.getFirstToken(),
            j = btParser.getLastToken();
        return new Position(prsStream.getFileName(),
                            prsStream.getLine(i),
                            prsStream.getColumn(i),
                            prsStream.getEndLine(j),
                            prsStream.getEndColumn(j));
    }

    private Position pos(int i)
    {
        return new Position(prsStream.getFileName(),
                            prsStream.getLine(i),
                            prsStream.getColumn(i),
                            prsStream.getEndLine(i),
                            prsStream.getEndColumn(i));
    }

    private Position pos(int i, int j)
    {
        return new Position(prsStream.getFileName(),
                            prsStream.getLine(i),
                            prsStream.getColumn(i),
                            prsStream.getEndLine(j),
                            prsStream.getEndColumn(j));
    }

    /**
     * Return the source position of the declaration.
     */
    public Position pos (VarDeclarator n)
    {
      if (n == null) return null;
      return n.pos;
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

    /** Pretend to have parsed new <T>Array.pointwiseOp 
     * { public <T> apply(Formal) MethodBody } 
     * instead of (Formal) MethodBody. Note that Formal may have 
     * exploded vars.
     * @author vj
    */

    private New makeInitializer( Position pos, TypeNode resultType, 
                                 X10Formal f, Block body ) {
      if (f.hasExplodedVars()) {
        List s = new TypedList(new LinkedList(), Stmt.class, false);
        s.addAll( f.explode() );
        s.addAll( body.statements() );
        body = body.statements( s );
      }
      Flags flags = Flags.PUBLIC;
      // resulttype is a.
      List l1 = new TypedList(new LinkedList(), X10Formal.class, false);
      l1.add(f);
      TypeNode appResultType = resultType;
      if (resultType instanceof AmbTypeNode) {
        Name x10 = new Name(nf, ts, pos, "x10");
        Name x10CG = new Name(nf, ts, pos, x10, "compilergenerated");
        Name x10CGP1 = new Name(nf, ts, pos, x10CG, "Parameter1");
        appResultType = x10CGP1.toType();
      }
      MethodDecl decl = nf.MethodDecl(pos, flags, appResultType, 
                                    "apply", l1,
                                      new LinkedList(), body);
      //  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
      String prefix = (resultType instanceof AmbTypeNode) ? "generic" : resultType.toString();
      Name x10 = new Name(nf, ts, pos, "x10");
      Name x10Lang = new Name(nf, ts, pos, x10, "lang");
      Name tArray
          = new Name(nf, ts, pos, x10Lang, prefix + "Array");
      Name tXArray
      = new Name(nf, ts, pos, x10Lang, "x10.lang." + prefix + "Array");
      Name tArrayPointwiseOp = new Name(nf, ts, pos, tArray, "pointwiseOp");
      List classDecl = new TypedList(new LinkedList(), MethodDecl.class, false);
      classDecl.add( decl );
      TypeNode t = (resultType instanceof AmbTypeNode) ?
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
      if (f.hasExplodedVars()) {
        List s = new TypedList(new LinkedList(), Stmt.class, false);
        s.addAll( f.explode() );
        s.addAll( body.statements() );
        body = body.statements( s );
      }
      Flags flags = Flags.PUBLIC;
      // resulttype is a.
      List l1 = new TypedList(new LinkedList(), X10Formal.class, false);
      l1.add(f);
      MethodDecl decl = nf.MethodDecl(pos, flags, resultType, 
                                    "apply", l1,
                                      new LinkedList(), body);
      //  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
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
        long x = parseLong(prsStream.getName(i), radix);
        return new IntegerLiteral(pos(i), (int) x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.IntegerLiteral int_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new IntegerLiteral(pos(i), (int) x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.LongLiteral long_lit(int i, int radix)
    {
        long x = parseLong(prsStream.getName(i), radix);
        return new LongLiteral(pos(i), x, X10Parsersym.TK_LongLiteral);
    }

    private polyglot.lex.LongLiteral long_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i), x, X10Parsersym.TK_LongLiteral);
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
                    if (x > 255)
                        eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                                   "Illegal character literal " + s, pos(i));
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
                        if (c > 255)
                            eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                                       "Illegal character (" + s.substring(j, n) + ") in string literal " + s, pos(i));
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


    int bad_rule = 0;

    public void ruleAction(int ruleNumber)
    {
        if (bad_rule != 0)
            return;

        switch (ruleNumber)
        {
 
            //
            // Rule 1:  identifier ::= IDENTIFIER
            //
            case 1: {
                
                btParser.setSym1(id(btParser.getToken(1)));
                break;
            }
     
            //
            // Rule 3:  PrimitiveType ::= boolean
            //
            case 3: {
                
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Boolean()));
                break;
            }
     
            //
            // Rule 6:  IntegralType ::= byte
            //
            case 6: {
                
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Byte()));
                break;
            }
     
            //
            // Rule 7:  IntegralType ::= char
            //
            case 7: {
                
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Char()));
                break;
            }
     
            //
            // Rule 8:  IntegralType ::= short
            //
            case 8: {
                
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Short()));
                break;
            }
     
            //
            // Rule 9:  IntegralType ::= int
            //
            case 9: {
                
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Int()));
                break;
            }
     
            //
            // Rule 10:  IntegralType ::= long
            //
            case 10: {
                
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Long()));
                break;
            }
     
            //
            // Rule 11:  FloatingPointType ::= float
            //
            case 11: {
                
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Float()));
                break;
            }
     
            //
            // Rule 12:  FloatingPointType ::= double
            //
            case 12: {
                
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Double()));
                break;
            }
     
            //
            // Rule 15:  TypeName ::= identifier
            //
            case 15: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 16:  TypeName ::= TypeName . identifier
            //
            case 16: {
                Name TypeName = (Name) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  TypeName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 18:  ArrayType ::= Type [ ]
            //
            case 18: {
                TypeNode Type = (TypeNode) btParser.getSym(1);
                btParser.setSym1(nf.array(Type, pos(), 1));
                break;
            }
     
            //
            // Rule 19:  PackageName ::= identifier
            //
            case 19: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 20:  PackageName ::= PackageName . identifier
            //
            case 20: {
                Name PackageName = (Name) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  PackageName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 21:  ExpressionName ::= identifier
            //
            case 21: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 22:  ExpressionName ::= here
            //
            case 22: {
                
                btParser.setSym1(new Name(nf, ts, pos(), "here"){
                            public Expr toExpr() {
                              return ((X10NodeFactory) nf).Here(pos);
                            }
                         });
                break;
            }
     
            //
            // Rule 23:  ExpressionName ::= AmbiguousName . identifier
            //
            case 23: {
                Name AmbiguousName = (Name) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  AmbiguousName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 24:  MethodName ::= identifier
            //
            case 24: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 25:  MethodName ::= AmbiguousName . identifier
            //
            case 25: {
                Name AmbiguousName = (Name) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  AmbiguousName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 26:  PackageOrTypeName ::= identifier
            //
            case 26: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 27:  PackageOrTypeName ::= PackageOrTypeName . identifier
            //
            case 27: {
                Name PackageOrTypeName = (Name) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  PackageOrTypeName,
                                  identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 28:  AmbiguousName ::= identifier
            //
            case 28: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 29:  AmbiguousName ::= AmbiguousName . identifier
            //
            case 29: {
                Name AmbiguousName = (Name) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  AmbiguousName,
                                  identifier.getIdentifier()));
               break;
            }
     
            //
            // Rule 30:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 30: {
                PackageNode PackageDeclarationopt = (PackageNode) btParser.getSym(1);
                List ImportDeclarationsopt = (List) btParser.getSym(2);
                List TypeDeclarationsopt = (List) btParser.getSym(3);
                // Add import x10.lang.* by default.
                Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");
                Import x10LangImport = 
                nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.PACKAGE, x10Lang.toString());
                ImportDeclarationsopt.add(x10LangImport);
                btParser.setSym1(nf.SourceFile(pos(btParser.getFirstToken(), btParser.getLastToken()), PackageDeclarationopt, ImportDeclarationsopt, TypeDeclarationsopt));
                break;
            }
     
            //
            // Rule 31:  ImportDeclarations ::= ImportDeclaration
            //
            case 31: {
                Import ImportDeclaration = (Import) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 32:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 32: {
                List ImportDeclarations = (List) btParser.getSym(1);
                Import ImportDeclaration = (Import) btParser.getSym(2);
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 33:  TypeDeclarations ::= TypeDeclaration
            //
            case 33: {
                ClassDecl TypeDeclaration = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 34:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 34: {
                List TypeDeclarations = (List) btParser.getSym(1);
                ClassDecl TypeDeclaration = (ClassDecl) btParser.getSym(2);
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 37:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 37: {
                Name TypeName = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.CLASS, TypeName.toString()));
                break;
            }
     
            //
            // Rule 38:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 38: {
                Name PackageOrTypeName = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.PACKAGE, PackageOrTypeName.toString()));
                break;
            }
     
            //
            // Rule 41:  TypeDeclaration ::= ;
            //
            case 41: {
                
                btParser.setSym1(null);
                break;
            }
     
            //
            // Rule 44:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 44: {
                Flags ClassModifiers = (Flags) btParser.getSym(1);
                Flags ClassModifier = (Flags) btParser.getSym(2);
                btParser.setSym1(ClassModifiers.set(ClassModifier));
                break;
            }
     
            //
            // Rule 45:  ClassModifier ::= public
            //
            case 45: {
                
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 46:  ClassModifier ::= protected
            //
            case 46: {
                
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 47:  ClassModifier ::= private
            //
            case 47: {
                
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 48:  ClassModifier ::= abstract
            //
            case 48: {
                
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 49:  ClassModifier ::= static
            //
            case 49: {
                
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 50:  ClassModifier ::= final
            //
            case 50: {
                
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 51:  ClassModifier ::= strictfp
            //
            case 51: {
                
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 52:  Super ::= extends ClassType
            //
            case 52: {
                TypeNode ClassType = (TypeNode) btParser.getSym(2);
                btParser.setSym1(ClassType);
                break;
            }
     
            //
            // Rule 53:  Interfaces ::= implements InterfaceTypeList
            //
            case 53: {
                List InterfaceTypeList = (List) btParser.getSym(2);
                btParser.setSym1(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 54:  InterfaceTypeList ::= InterfaceType
            //
            case 54: {
                TypeNode InterfaceType = (TypeNode) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 55:  InterfaceTypeList ::= InterfaceTypeList , InterfaceType
            //
            case 55: {
                List InterfaceTypeList = (List) btParser.getSym(1);
                TypeNode InterfaceType = (TypeNode) btParser.getSym(3);
                InterfaceTypeList.add(InterfaceType);
                btParser.setSym1(InterfaceTypeList);
                break;
            }
     
            //
            // Rule 56:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 56: {
                List ClassBodyDeclarationsopt = (List) btParser.getSym(2);
                btParser.setSym1(nf.ClassBody(pos(btParser.getFirstToken(), btParser.getLastToken()), ClassBodyDeclarationsopt));
                break;
            }
     
            //
            // Rule 58:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 58: {
                List ClassBodyDeclarations = (List) btParser.getSym(1);
                List ClassBodyDeclaration = (List) btParser.getSym(2);
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 60:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 60: {
                Block InstanceInitializer = (Block) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.NONE, InstanceInitializer));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 61:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 61: {
                Block StaticInitializer = (Block) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(nf.Initializer(pos(), Flags.STATIC, StaticInitializer));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 62:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 62: {
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 64:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 64: {
                MethodDecl MethodDeclaration = (MethodDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 65:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 65: {
                ClassDecl ClassDeclaration = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 66:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 66: {
                ClassDecl InterfaceDeclaration = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 67:  ClassMemberDeclaration ::= ;
            //
            case 67: {
                
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 68:  FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators ;
            //
            case 68: {
                Flags FieldModifiersopt = (Flags) btParser.getSym(1);
                TypeNode Type = (TypeNode) btParser.getSym(2);
                List VariableDeclarators = (List) btParser.getSym(3);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                for (Iterator i = VariableDeclarators.iterator(); i.hasNext();)
                {
                    X10VarDeclarator d = (X10VarDeclarator) i.next();
                    if (d.hasExplodedVars())
                      // TODO: Report this exception correctly.
                      throw new Error("Field Declarations may not have exploded variables." + pos());
                    d.setFlag(FieldModifiersopt);
                    l.add(nf.FieldDecl(pos(btParser.getFirstToken(2), btParser.getLastToken()),
                                       d.flags,
                                       nf.array(Type, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), d.dims),
                                       d.name,
                                       d.init));
                }
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 69:  VariableDeclarators ::= VariableDeclarator
            //
            case 69: {
                VarDeclarator VariableDeclarator = (VarDeclarator) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), X10VarDeclarator.class, false);
                l.add(VariableDeclarator);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 70:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 70: {
                List VariableDeclarators = (List) btParser.getSym(1);
                VarDeclarator VariableDeclarator = (VarDeclarator) btParser.getSym(3);
                VariableDeclarators.add(VariableDeclarator);
                // btParser.setSym1(VariableDeclarators);
                break;
            }
     
            //
            // Rule 72:  VariableDeclarator ::= VariableDeclaratorId = VariableInitializer
            //
            case 72: {
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) btParser.getSym(1);
                Expr VariableInitializer = (Expr) btParser.getSym(3);
                VariableDeclaratorId.init = VariableInitializer;
                // btParser.setSym1(VariableDeclaratorId); 
                break;
            }
     
            //
            // Rule 73:  VariableDeclaratorId ::= identifier
            //
            case 73: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new X10VarDeclarator(pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 74:  VariableDeclaratorId ::= VariableDeclaratorId [ ]
            //
            case 74: {
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) btParser.getSym(1);
                VariableDeclaratorId.dims++;
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 75:  VariableDeclaratorId ::= identifier [ IdentifierList ]
            //
            case 75: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                List IdentifierList = (List) btParser.getSym(3);
                btParser.setSym1(new X10VarDeclarator(pos(), identifier.getIdentifier(), IdentifierList));
                break;
            }
     
            //
            // Rule 76:  VariableDeclaratorId ::= [ IdentifierList ]
            //
            case 76: {
                List IdentifierList = (List) btParser.getSym(2);
                String name = polyglot.ext.x10.visit.X10PrettyPrinterVisitor.getId();
                btParser.setSym1(new X10VarDeclarator(pos(), name, IdentifierList));
                break;
            }
     
            //
            // Rule 80:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 80: {
                Flags FieldModifiers = (Flags) btParser.getSym(1);
                Flags FieldModifier = (Flags) btParser.getSym(2);
                btParser.setSym1(FieldModifiers.set(FieldModifier));
                break;
            }
     
            //
            // Rule 81:  FieldModifier ::= public
            //
            case 81: {
                
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 82:  FieldModifier ::= protected
            //
            case 82: {
                
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 83:  FieldModifier ::= private
            //
            case 83: {
                
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 84:  FieldModifier ::= static
            //
            case 84: {
                
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 85:  FieldModifier ::= final
            //
            case 85: {
                
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 86:  FieldModifier ::= transient
            //
            case 86: {
                
                btParser.setSym1(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 87:  FieldModifier ::= volatile
            //
            case 87: {
                
                btParser.setSym1(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 88:  MethodDeclaration ::= MethodHeader MethodBody
            //
            case 88: {
                MethodDecl MethodHeader = (MethodDecl) btParser.getSym(1);
                Block MethodBody = (Block) btParser.getSym(2);
                List l = MethodHeader.formals();
                List s = new TypedList(new LinkedList(), Stmt.class, false);
                for (Iterator i = l.iterator(); i.hasNext(); ) {
                   X10Formal d = (X10Formal) i.next();
                   if (d.hasExplodedVars())
                     s.addAll( d.explode());
                }
                if (! s.isEmpty()) {
                    s.addAll(MethodBody.statements());
                    MethodBody = MethodBody.statements(s);
                }
                Flags f = MethodHeader.flags();
                if (f.contains(Flags.ATOMIC)) {
                     List ss = new TypedList(new LinkedList(), Stmt.class, false);
                     ss.add(nf.Atomic(pos(), nf.Here(pos()), MethodBody));
                     MethodBody = MethodBody.statements(ss);
                     MethodHeader = MethodHeader.flags(f.clear(Flags.ATOMIC));
                }
                btParser.setSym1(MethodHeader.body(MethodBody));
                break;
            }
     
            //
            // Rule 90:  ResultType ::= void
            //
            case 90: {
                
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 91:  MethodDeclarator ::= identifier ( FormalParameterListopt )
            //
            case 91: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                List FormalParameterListopt = (List) btParser.getSym(3);
                Object[] a = new Object[3];
                a[0] = new Name(nf, ts, pos(), identifier.getIdentifier());
                a[1] = FormalParameterListopt;
                a[2] = new Integer(0);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 92:  MethodDeclarator ::= MethodDeclarator [ ]
            //
            case 92: {
                Object[] MethodDeclarator = (Object[]) btParser.getSym(1);
                MethodDeclarator[2] = new Integer(((Integer) MethodDeclarator[2]).intValue() + 1);
                // btParser.setSym1(MethodDeclarator);
                break;
            }
     
            //
            // Rule 93:  FormalParameterList ::= LastFormalParameter
            //
            case 93: {
                Formal LastFormalParameter = (Formal) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(LastFormalParameter);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 94:  FormalParameterList ::= FormalParameters , LastFormalParameter
            //
            case 94: {
                List FormalParameters = (List) btParser.getSym(1);
                Formal LastFormalParameter = (Formal) btParser.getSym(3);
                FormalParameters.add(LastFormalParameter);
                // btParser.setSym1(FormalParameters);
                break;
            }
     
            //
            // Rule 95:  FormalParameters ::= FormalParameter
            //
            case 95: {
                X10Formal FormalParameter = (X10Formal) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 96:  FormalParameters ::= FormalParameters , FormalParameter
            //
            case 96: {
                List FormalParameters = (List) btParser.getSym(1);
                X10Formal FormalParameter = (X10Formal) btParser.getSym(3);
                FormalParameters.add(FormalParameter);
                // btParser.setSym1(FormalParameters);
                break;
            }
     
            //
            // Rule 97:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 97: {
                Flags VariableModifiersopt = (Flags) btParser.getSym(1);
                TypeNode Type = (TypeNode) btParser.getSym(2);
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) btParser.getSym(3);
                VariableDeclaratorId.setFlag(VariableModifiersopt);
                btParser.setSym1(nf.Formal(pos(), nf.array(Type, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), VariableDeclaratorId.dims), VariableDeclaratorId));
                break;
            }
     
            //
            // Rule 99:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 99: {
                Flags VariableModifiers = (Flags) btParser.getSym(1);
                Flags VariableModifier = (Flags) btParser.getSym(2);
                btParser.setSym1(VariableModifiers.set(VariableModifier));
                break;
            }
     
            //
            // Rule 100:  VariableModifier ::= final
            //
            case 100: {
                
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 101:  LastFormalParameter ::= VariableModifiersopt Type ...opt$opt VariableDeclaratorId
            //
            case 101: {
                Flags VariableModifiersopt = (Flags) btParser.getSym(1);
                TypeNode Type = (TypeNode) btParser.getSym(2);
                Object opt = (Object) btParser.getSym(3);
                X10VarDeclarator VariableDeclaratorId = (X10VarDeclarator) btParser.getSym(4);
                assert(opt == null);
                VariableDeclaratorId.setFlag(VariableModifiersopt);
                btParser.setSym1(nf.Formal(pos(), nf.array(Type, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), VariableDeclaratorId.dims), VariableDeclaratorId));
                break;
            }
     
            //
            // Rule 103:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 103: {
                Flags MethodModifiers = (Flags) btParser.getSym(1);
                Flags MethodModifier = (Flags) btParser.getSym(2);
                btParser.setSym1(MethodModifiers.set(MethodModifier));
                break;
            }
     
            //
            // Rule 104:  MethodModifier ::= public
            //
            case 104: {
                
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 105:  MethodModifier ::= protected
            //
            case 105: {
                
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 106:  MethodModifier ::= private
            //
            case 106: {
                
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 107:  MethodModifier ::= abstract
            //
            case 107: {
                
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 108:  MethodModifier ::= static
            //
            case 108: {
                
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 109:  MethodModifier ::= final
            //
            case 109: {
                
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 110:  MethodModifier ::= synchronized
            //
            case 110: {
                
                btParser.setSym1(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 111:  MethodModifier ::= native
            //
            case 111: {
                
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 112:  MethodModifier ::= strictfp
            //
            case 112: {
                
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 113:  Throws ::= throws ExceptionTypeList
            //
            case 113: {
                List ExceptionTypeList = (List) btParser.getSym(2);
                btParser.setSym1(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 114:  ExceptionTypeList ::= ExceptionType
            //
            case 114: {
                TypeNode ExceptionType = (TypeNode) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 115:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 115: {
                List ExceptionTypeList = (List) btParser.getSym(1);
                TypeNode ExceptionType = (TypeNode) btParser.getSym(3);
                ExceptionTypeList.add(ExceptionType);
                // btParser.setSym1(ExceptionTypeList);
                break;
            }
     
            //
            // Rule 118:  MethodBody ::= ;
            //
            case 118:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 120:  StaticInitializer ::= static Block
            //
            case 120: {
                Block Block = (Block) btParser.getSym(2);
                btParser.setSym1(Block);
                break;
            }
     
            //
            // Rule 121:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 121: {
                Flags ConstructorModifiersopt = (Flags) btParser.getSym(1);
                Object[] ConstructorDeclarator = (Object[]) btParser.getSym(2);
                List Throwsopt = (List) btParser.getSym(3);
                Block ConstructorBody = (Block) btParser.getSym(4);
                Name a = (Name) ConstructorDeclarator[1];
                List b = (List) ConstructorDeclarator[2];

                btParser.setSym1(nf.ConstructorDecl(pos(), ConstructorModifiersopt, a.toString(), b, Throwsopt, ConstructorBody));
                break;
            }
     
            //
            // Rule 122:  SimpleTypeName ::= identifier
            //
            case 122: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 124:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 124: {
                Flags ConstructorModifiers = (Flags) btParser.getSym(1);
                Flags ConstructorModifier = (Flags) btParser.getSym(2);
                btParser.setSym1(ConstructorModifiers.set(ConstructorModifier));
                break;
            }
     
            //
            // Rule 125:  ConstructorModifier ::= public
            //
            case 125: {
                
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 126:  ConstructorModifier ::= protected
            //
            case 126: {
                
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 127:  ConstructorModifier ::= private
            //
            case 127: {
                
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 128:  ConstructorBody ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 128: {
                Stmt ExplicitConstructorInvocationopt = (Stmt) btParser.getSym(2);
                List BlockStatementsopt = (List) btParser.getSym(3);
                List l;
                if (ExplicitConstructorInvocationopt == null)
                    l = BlockStatementsopt;
                else
                {
                    l = new TypedList(new LinkedList(), Stmt.class, false);
                    l.add(ExplicitConstructorInvocationopt);
                    l.addAll(BlockStatementsopt);
                }
                btParser.setSym1(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 129:  Arguments ::= ( ArgumentListopt )
            //
            case 129: {
                List ArgumentListopt = (List) btParser.getSym(2);
                btParser.setSym1(ArgumentListopt);
                break;
            }
     
            //
            // Rule 132:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 132: {
                Flags InterfaceModifiers = (Flags) btParser.getSym(1);
                Flags InterfaceModifier = (Flags) btParser.getSym(2);
                btParser.setSym1(InterfaceModifiers.set(InterfaceModifier));
                break;
            }
     
            //
            // Rule 133:  InterfaceModifier ::= public
            //
            case 133: {
                
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 134:  InterfaceModifier ::= protected
            //
            case 134: {
                
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 135:  InterfaceModifier ::= private
            //
            case 135: {
                
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 136:  InterfaceModifier ::= abstract
            //
            case 136: {
                
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 137:  InterfaceModifier ::= static
            //
            case 137: {
                
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 138:  InterfaceModifier ::= strictfp
            //
            case 138: {
                
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 139:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 139: {
                TypeNode InterfaceType = (TypeNode) btParser.getSym(2);
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(InterfaceType);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 140:  ExtendsInterfaces ::= ExtendsInterfaces , InterfaceType
            //
            case 140: {
                List ExtendsInterfaces = (List) btParser.getSym(1);
                TypeNode InterfaceType = (TypeNode) btParser.getSym(3);
                ExtendsInterfaces.add(InterfaceType);
                // btParser.setSym1(ExtendsInterfaces);
                break;
            }
     
            //
            // Rule 141:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 141: {
                List InterfaceMemberDeclarationsopt = (List) btParser.getSym(2);
                btParser.setSym1(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                break;
            }
     
            //
            // Rule 143:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 143: {
                List InterfaceMemberDeclarations = (List) btParser.getSym(1);
                List InterfaceMemberDeclaration = (List) btParser.getSym(2);
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 145:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 145: {
                MethodDecl AbstractMethodDeclaration = (MethodDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(AbstractMethodDeclaration);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 146:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 146: {
                ClassDecl ClassDeclaration = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 147:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 147: {
                ClassDecl InterfaceDeclaration = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 148:  InterfaceMemberDeclaration ::= ;
            //
            case 148: {
                
                btParser.setSym1(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 149:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 149: {
                Flags ConstantModifiersopt = (Flags) btParser.getSym(1);
                TypeNode Type = (TypeNode) btParser.getSym(2);
                List VariableDeclarators = (List) btParser.getSym(3);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                for (Iterator i = VariableDeclarators.iterator(); i.hasNext();)
                {
                    X10VarDeclarator d = (X10VarDeclarator) i.next();
                    if (d.hasExplodedVars())
                      // TODO: Report this exception correctly.
                      throw new Error("Field Declarations may not have exploded variables." + pos());
                    l.add(nf.FieldDecl(pos(btParser.getFirstToken(2), btParser.getLastToken()),
                                       ConstantModifiersopt,
                                       nf.array(Type, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), d.dims),
                                       d.name,
                                       d.init));
                }
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 151:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 151: {
                Flags ConstantModifiers = (Flags) btParser.getSym(1);
                Flags ConstantModifier = (Flags) btParser.getSym(2);
                btParser.setSym1(ConstantModifiers.set(ConstantModifier));
                break;
            }
     
            //
            // Rule 152:  ConstantModifier ::= public
            //
            case 152: {
                
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 153:  ConstantModifier ::= static
            //
            case 153: {
                
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 154:  ConstantModifier ::= final
            //
            case 154: {
                
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 156:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 156: {
                Flags AbstractMethodModifiers = (Flags) btParser.getSym(1);
                Flags AbstractMethodModifier = (Flags) btParser.getSym(2);
                btParser.setSym1(AbstractMethodModifiers.set(AbstractMethodModifier));
                break;
            }
     
            //
            // Rule 157:  AbstractMethodModifier ::= public
            //
            case 157: {
                
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 158:  AbstractMethodModifier ::= abstract
            //
            case 158: {
                
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 159:  SimpleName ::= identifier
            //
            case 159: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 160:  ArrayInitializer ::= { VariableInitializersopt ,opt$opt }
            //
            case 160: {
                List VariableInitializersopt = (List) btParser.getSym(2);
                Object opt = (Object) btParser.getSym(3);
                if (VariableInitializersopt == null)
                     btParser.setSym1(nf.ArrayInit(pos()));
                else btParser.setSym1(nf.ArrayInit(pos(), VariableInitializersopt));
                break;
            }
     
            //
            // Rule 161:  VariableInitializers ::= VariableInitializer
            //
            case 161: {
                Expr VariableInitializer = (Expr) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 162:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 162: {
                List VariableInitializers = (List) btParser.getSym(1);
                Expr VariableInitializer = (Expr) btParser.getSym(3);
                VariableInitializers.add(VariableInitializer);
                //btParser.setSym1(VariableInitializers);
                break;
            }
     
            //
            // Rule 163:  Block ::= { BlockStatementsopt }
            //
            case 163: {
                List BlockStatementsopt = (List) btParser.getSym(2);
                btParser.setSym1(nf.Block(pos(), BlockStatementsopt));
                break;
            }
     
            //
            // Rule 164:  BlockStatements ::= BlockStatement
            //
            case 164: {
                List BlockStatement = (List) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 165:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 165: {
                List BlockStatements = (List) btParser.getSym(1);
                List BlockStatement = (List) btParser.getSym(2);
                BlockStatements.addAll(BlockStatement);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 167:  BlockStatement ::= ClassDeclaration
            //
            case 167: {
                ClassDecl ClassDeclaration = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 168:  BlockStatement ::= Statement
            //
            case 168: {
                Stmt Statement = (Stmt) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 170:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 170: {
                Flags VariableModifiersopt = (Flags) btParser.getSym(1);
                TypeNode Type = (TypeNode) btParser.getSym(2);
                List VariableDeclarators = (List) btParser.getSym(3);
                List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                List s = new TypedList(new LinkedList(), Stmt.class, false);
                for (Iterator i = VariableDeclarators.iterator(); i.hasNext(); )
                {
                    X10VarDeclarator d = (X10VarDeclarator) i.next();
                    d.setFlag(VariableModifiersopt); 
                    // use d.flags below and not flags, setFlag may change it.
                    l.add(nf.LocalDecl(d.pos, d.flags,
                                       nf.array(Type, pos(d), d.dims), d.name, d.init));
                    if (d.hasExplodedVars())
                       s.addAll(d.explode());
                }
                l.addAll(s); 
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 194:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 194: {
                Expr Expression = (Expr) btParser.getSym(3);
                Stmt Statement = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.If(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 195:  IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
            //
            case 195: {
                Expr Expression = (Expr) btParser.getSym(3);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(5);
                Stmt Statement = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), Expression, StatementNoShortIf, Statement));
                break;
            }
     
            //
            // Rule 196:  IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf$true_stmt else StatementNoShortIf$false_stmt
            //
            case 196: {
                Expr Expression = (Expr) btParser.getSym(3);
                Stmt true_stmt = (Stmt) btParser.getSym(5);
                Stmt false_stmt = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), Expression, true_stmt, false_stmt));
                break;
            }
     
            //
            // Rule 197:  EmptyStatement ::= ;
            //
            case 197: {
                
                btParser.setSym1(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 198:  LabeledStatement ::= identifier : Statement
            //
            case 198: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                Stmt Statement = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), identifier.getIdentifier(), Statement));
                break;
            }
     
            //
            // Rule 199:  LabeledStatementNoShortIf ::= identifier : StatementNoShortIf
            //
            case 199: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), identifier.getIdentifier(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 200:  ExpressionStatement ::= StatementExpression ;
            //
            case 200: {
                Expr StatementExpression = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Eval(pos(), StatementExpression));
                break;
            }
     
            //
            // Rule 208:  AssertStatement ::= assert Expression ;
            //
            case 208: {
                Expr Expression = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Assert(pos(), Expression));
                break;
            }
     
            //
            // Rule 209:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 209: {
                Expr expr1 = (Expr) btParser.getSym(2);
                Expr expr2 = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Assert(pos(), expr1, expr2));
                break;
            }
     
            //
            // Rule 210:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 210: {
                Expr Expression = (Expr) btParser.getSym(3);
                List SwitchBlock = (List) btParser.getSym(5);
                btParser.setSym1(nf.Switch(pos(), Expression, SwitchBlock));
                break;
            }
     
            //
            // Rule 211:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 211: {
                List SwitchBlockStatementGroupsopt = (List) btParser.getSym(2);
                List SwitchLabelsopt = (List) btParser.getSym(3);
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                btParser.setSym1(SwitchBlockStatementGroupsopt);
                break;
            }
     
            //
            // Rule 213:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 213: {
                List SwitchBlockStatementGroups = (List) btParser.getSym(1);
                List SwitchBlockStatementGroup = (List) btParser.getSym(2);
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // btParser.setSym1(SwitchBlockStatementGroups);
                break;
            }
     
            //
            // Rule 214:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 214: {
                List SwitchLabels = (List) btParser.getSym(1);
                List BlockStatements = (List) btParser.getSym(2);
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 215:  SwitchLabels ::= SwitchLabel
            //
            case 215: {
                Case SwitchLabel = (Case) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 216:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 216: {
                List SwitchLabels = (List) btParser.getSym(1);
                Case SwitchLabel = (Case) btParser.getSym(2);
                SwitchLabels.add(SwitchLabel);
                //btParser.setSym1(SwitchLabels);
                break;
            }
     
            //
            // Rule 217:  SwitchLabel ::= case ConstantExpression :
            //
            case 217: {
                Expr ConstantExpression = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Case(pos(), ConstantExpression));
                break;
            }
     
            //
            // Rule 218:  SwitchLabel ::= default :
            //
            case 218: {
                
                btParser.setSym1(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 219:  WhileStatement ::= while ( Expression ) Statement
            //
            case 219: {
                Expr Expression = (Expr) btParser.getSym(3);
                Stmt Statement = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 220:  WhileStatementNoShortIf ::= while ( Expression ) StatementNoShortIf
            //
            case 220: {
                Expr Expression = (Expr) btParser.getSym(3);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 221:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 221: {
                Stmt Statement = (Stmt) btParser.getSym(2);
                Expr Expression = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Do(pos(), Statement, Expression));
                break;
            }
     
            //
            // Rule 224:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 224: {
                List ForInitopt = (List) btParser.getSym(3);
                Expr Expressionopt = (Expr) btParser.getSym(5);
                List ForUpdateopt = (List) btParser.getSym(7);
                Stmt Statement = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                break;
            }
     
            //
            // Rule 225:  ForStatementNoShortIf ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) StatementNoShortIf
            //
            case 225: {
                List ForInitopt = (List) btParser.getSym(3);
                Expr Expressionopt = (Expr) btParser.getSym(5);
                List ForUpdateopt = (List) btParser.getSym(7);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 227:  ForInit ::= LocalVariableDeclaration
            //
            case 227: {
                List LocalVariableDeclaration = (List) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 229:  StatementExpressionList ::= StatementExpression
            //
            case 229: {
                Expr StatementExpression = (Expr) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 230:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 230: {
                List StatementExpressionList = (List) btParser.getSym(1);
                Expr StatementExpression = (Expr) btParser.getSym(3);
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                //btParser.setSym1(StatementExpressionList);
                break;
            }
     
            //
            // Rule 231:  BreakStatement ::= break identifieropt ;
            //
            case 231: {
                Name identifieropt = (Name) btParser.getSym(2);
                if (identifieropt == null)
                     btParser.setSym1(nf.Break(pos()));
                else btParser.setSym1(nf.Break(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 232:  ContinueStatement ::= continue identifieropt ;
            //
            case 232: {
                Name identifieropt = (Name) btParser.getSym(2);
                if (identifieropt == null)
                     btParser.setSym1(nf.Continue(pos()));
                else btParser.setSym1(nf.Continue(pos(), identifieropt.toString()));
                break;
            }
     
            //
            // Rule 233:  ReturnStatement ::= return Expressionopt ;
            //
            case 233: {
                Expr Expressionopt = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Return(pos(), Expressionopt));
                break;
            }
     
            //
            // Rule 234:  ThrowStatement ::= throw Expression ;
            //
            case 234: {
                Expr Expression = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Throw(pos(), Expression));
                break;
            }
     
            //
            // Rule 235:  SynchronizedStatement ::= synchronized ( Expression ) Block
            //
            case 235: {
                Expr Expression = (Expr) btParser.getSym(3);
                Block Block = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Synchronized(pos(), Expression, Block));
                break;
            }
     
            //
            // Rule 236:  TryStatement ::= try Block Catches
            //
            case 236: {
                Block Block = (Block) btParser.getSym(2);
                List Catches = (List) btParser.getSym(3);
                btParser.setSym1(nf.Try(pos(), Block, Catches));
                break;
            }
     
            //
            // Rule 237:  TryStatement ::= try Block Catchesopt Finally
            //
            case 237: {
                Block Block = (Block) btParser.getSym(2);
                List Catchesopt = (List) btParser.getSym(3);
                Block Finally = (Block) btParser.getSym(4);
                btParser.setSym1(nf.Try(pos(), Block, Catchesopt, Finally));
                break;
            }
     
            //
            // Rule 238:  Catches ::= CatchClause
            //
            case 238: {
                Catch CatchClause = (Catch) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 239:  Catches ::= Catches CatchClause
            //
            case 239: {
                List Catches = (List) btParser.getSym(1);
                Catch CatchClause = (Catch) btParser.getSym(2);
                Catches.add(CatchClause);
                //btParser.setSym1(Catches);
                break;
            }
     
            //
            // Rule 240:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 240: {
                X10Formal FormalParameter = (X10Formal) btParser.getSym(3);
                Block Block = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Catch(pos(), FormalParameter, Block));
                break;
            }
     
            //
            // Rule 241:  Finally ::= finally Block
            //
            case 241: {
                Block Block = (Block) btParser.getSym(2);
                btParser.setSym1(Block);
                break;
            }
     
            //
            // Rule 245:  PrimaryNoNewArray ::= Type . class
            //
            case 245: {
                TypeNode Type = (TypeNode) btParser.getSym(1);
                if (Type instanceof Name)
                {
                    Name a = (Name) Type;
                    btParser.setSym1(nf.ClassLit(pos(), a.toType()));
                }
                else if (Type instanceof TypeNode)
                {
                    btParser.setSym1(nf.ClassLit(pos(), Type));
                }
                else if (Type instanceof CanonicalTypeNode)
                {
                    CanonicalTypeNode a = (CanonicalTypeNode) Type;
                    btParser.setSym1(nf.ClassLit(pos(), a));
                }
                else assert(false);
                break;
            }
     
            //
            // Rule 246:  PrimaryNoNewArray ::= void . class
            //
            case 246: {
                
                btParser.setSym1(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(btParser.getFirstToken()), ts.Void())));
                break;
            }
     
            //
            // Rule 247:  PrimaryNoNewArray ::= this
            //
            case 247: {
                
                btParser.setSym1(nf.This(pos()));
                break;
            }
     
            //
            // Rule 248:  PrimaryNoNewArray ::= ClassName . this
            //
            case 248: {
                Name ClassName = (Name) btParser.getSym(1);
                btParser.setSym1(nf.This(pos(), ClassName.toType()));
                break;
            }
     
            //
            // Rule 249:  PrimaryNoNewArray ::= ( Expression )
            //
            case 249: {
                Expr Expression = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.ParExpr(pos(), Expression));
                break;
            }
     
            //
            // Rule 254:  Literal ::= IntegerLiteral
            //
            case 254: {
                
                polyglot.lex.IntegerLiteral a = int_lit(btParser.getToken(1));
                btParser.setSym1(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 255:  Literal ::= LongLiteral
            //
            case 255: {
                
                polyglot.lex.LongLiteral a = long_lit(btParser.getToken(1));
                btParser.setSym1(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 256:  Literal ::= FloatingPointLiteral
            //
            case 256: {
                
                polyglot.lex.FloatLiteral a = float_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 257:  Literal ::= DoubleLiteral
            //
            case 257: {
                
                polyglot.lex.DoubleLiteral a = double_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 258:  Literal ::= BooleanLiteral
            //
            case 258: {
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) btParser.getSym(1);
                btParser.setSym1(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 259:  Literal ::= CharacterLiteral
            //
            case 259: {
                
                polyglot.lex.CharacterLiteral a = char_lit(btParser.getToken(1));
                btParser.setSym1(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 260:  Literal ::= StringLiteral
            //
            case 260: {
                
                polyglot.lex.StringLiteral a = string_lit(btParser.getToken(1));
                btParser.setSym1(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 261:  Literal ::= null
            //
            case 261: {
                
                btParser.setSym1(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 262:  BooleanLiteral ::= true
            //
            case 262: {
                
                btParser.setSym1(boolean_lit(btParser.getToken(1)));
                break;
            }
     
            //
            // Rule 263:  BooleanLiteral ::= false
            //
            case 263: {
                
                btParser.setSym1(boolean_lit(btParser.getToken(1)));
                break;
            }
     
            //
            // Rule 264:  ArgumentList ::= Expression
            //
            case 264: {
                Expr Expression = (Expr) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 265:  ArgumentList ::= ArgumentList , Expression
            //
            case 265: {
                List ArgumentList = (List) btParser.getSym(1);
                Expr Expression = (Expr) btParser.getSym(3);
                ArgumentList.add(Expression);
                //btParser.setSym1(ArgumentList);
                break;
            }
     
            //
            // Rule 266:  DimExprs ::= DimExpr
            //
            case 266: {
                Expr DimExpr = (Expr) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(DimExpr);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 267:  DimExprs ::= DimExprs DimExpr
            //
            case 267: {
                List DimExprs = (List) btParser.getSym(1);
                Expr DimExpr = (Expr) btParser.getSym(2);
                DimExprs.add(DimExpr);
                //btParser.setSym1(DimExprs);
                break;
            }
     
            //
            // Rule 268:  DimExpr ::= [ Expression ]
            //
            case 268: {
                Expr Expression = (Expr) btParser.getSym(2);
                btParser.setSym1(Expression.position(pos()));
                break;
            }
     
            //
            // Rule 269:  Dims ::= [ ]
            //
            case 269: {
                
                btParser.setSym1(new Integer(1));
                break;
            }
     
            //
            // Rule 270:  Dims ::= Dims [ ]
            //
            case 270: {
                Integer Dims = (Integer) btParser.getSym(1);
                btParser.setSym1(new Integer(Dims.intValue() + 1));
                break;
            }
     
            //
            // Rule 271:  FieldAccess ::= Primary . identifier
            //
            case 271: {
                Expr Primary = (Expr) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                btParser.setSym1(nf.Field(pos(), Primary, identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 272:  FieldAccess ::= super . identifier
            //
            case 272: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken())), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 273:  FieldAccess ::= ClassName . super . identifier
            //
            case 273: {
                Name ClassName = (Name) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(5);
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken(3)), ClassName.toType()), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 274:  MethodInvocation ::= MethodName ( ArgumentListopt )
            //
            case 274: {
                Name MethodName = (Name) btParser.getSym(1);
                List ArgumentListopt = (List) btParser.getSym(3);
                btParser.setSym1(nf.Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, ArgumentListopt));
                break;
            }
     
            //
            // Rule 276:  PostfixExpression ::= ExpressionName
            //
            case 276: {
                Name ExpressionName = (Name) btParser.getSym(1);
                btParser.setSym1(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 279:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 279: {
                Expr PostfixExpression = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 280:  PostDecrementExpression ::= PostfixExpression --
            //
            case 280: {
                Expr PostfixExpression = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 283:  UnaryExpression ::= + UnaryExpression
            //
            case 283: {
                Expr UnaryExpression = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.POS, UnaryExpression));
                break;
            }
     
            //
            // Rule 284:  UnaryExpression ::= - UnaryExpression
            //
            case 284: {
                Expr UnaryExpression = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NEG, UnaryExpression));
                break;
            }
     
            //
            // Rule 286:  PreIncrementExpression ::= ++ UnaryExpression
            //
            case 286: {
                Expr UnaryExpression = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_INC, UnaryExpression));
                break;
            }
     
            //
            // Rule 287:  PreDecrementExpression ::= -- UnaryExpression
            //
            case 287: {
                Expr UnaryExpression = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpression));
                break;
            }
     
            //
            // Rule 289:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 289: {
                Expr UnaryExpression = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 290:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 290: {
                Expr UnaryExpression = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                break;
            }
     
            //
            // Rule 293:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 293: {
                Expr MultiplicativeExpression = (Expr) btParser.getSym(1);
                Expr UnaryExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                break;
            }
     
            //
            // Rule 294:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 294: {
                Expr MultiplicativeExpression = (Expr) btParser.getSym(1);
                Expr UnaryExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                break;
            }
     
            //
            // Rule 295:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 295: {
                Expr MultiplicativeExpression = (Expr) btParser.getSym(1);
                Expr UnaryExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                break;
            }
     
            //
            // Rule 297:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 297: {
                Expr AdditiveExpression = (Expr) btParser.getSym(1);
                Expr MultiplicativeExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 298:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 298: {
                Expr AdditiveExpression = (Expr) btParser.getSym(1);
                Expr MultiplicativeExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                break;
            }
     
            //
            // Rule 300:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 300: {
                Expr ShiftExpression = (Expr) btParser.getSym(1);
                Expr AdditiveExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                break;
            }
     
            //
            // Rule 301:  ShiftExpression ::= ShiftExpression > > AdditiveExpression
            //
            case 301: {
                Expr ShiftExpression = (Expr) btParser.getSym(1);
                Expr AdditiveExpression = (Expr) btParser.getSym(4);
                // TODO: make sure that there is no space after the ">" signs
                btParser.setSym1(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 302:  ShiftExpression ::= ShiftExpression > > > AdditiveExpression
            //
            case 302: {
                Expr ShiftExpression = (Expr) btParser.getSym(1);
                Expr AdditiveExpression = (Expr) btParser.getSym(5);
                // TODO: make sure that there is no space after the ">" signs
                btParser.setSym1(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                break;
            }
     
            //
            // Rule 304:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 304: {
                Expr RelationalExpression = (Expr) btParser.getSym(1);
                Expr ShiftExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
                break;
            }
     
            //
            // Rule 305:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 305: {
                Expr RelationalExpression = (Expr) btParser.getSym(1);
                Expr ShiftExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
                break;
            }
     
            //
            // Rule 306:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 306: {
                Expr RelationalExpression = (Expr) btParser.getSym(1);
                Expr ShiftExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
                break;
            }
     
            //
            // Rule 307:  RelationalExpression ::= RelationalExpression > = ShiftExpression
            //
            case 307: {
                Expr RelationalExpression = (Expr) btParser.getSym(1);
                Expr ShiftExpression = (Expr) btParser.getSym(4);
                // TODO: make sure that there is no space after the ">" signs
                btParser.setSym1(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
                break;
            }
     
            //
            // Rule 309:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 309: {
                Expr EqualityExpression = (Expr) btParser.getSym(1);
                Expr RelationalExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                break;
            }
     
            //
            // Rule 310:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 310: {
                Expr EqualityExpression = (Expr) btParser.getSym(1);
                Expr RelationalExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                break;
            }
     
            //
            // Rule 312:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 312: {
                Expr AndExpression = (Expr) btParser.getSym(1);
                Expr EqualityExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                break;
            }
     
            //
            // Rule 314:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 314: {
                Expr ExclusiveOrExpression = (Expr) btParser.getSym(1);
                Expr AndExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                break;
            }
     
            //
            // Rule 316:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 316: {
                Expr InclusiveOrExpression = (Expr) btParser.getSym(1);
                Expr ExclusiveOrExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                break;
            }
     
            //
            // Rule 318:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 318: {
                Expr ConditionalAndExpression = (Expr) btParser.getSym(1);
                Expr InclusiveOrExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                break;
            }
     
            //
            // Rule 320:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 320: {
                Expr ConditionalOrExpression = (Expr) btParser.getSym(1);
                Expr ConditionalAndExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                break;
            }
     
            //
            // Rule 322:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 322: {
                Expr ConditionalOrExpression = (Expr) btParser.getSym(1);
                Expr Expression = (Expr) btParser.getSym(3);
                Expr ConditionalExpression = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                break;
            }
     
            //
            // Rule 325:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 325: {
                Expr LeftHandSide = (Expr) btParser.getSym(1);
                Assign.Operator AssignmentOperator = (Assign.Operator) btParser.getSym(2);
                Expr AssignmentExpression = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                break;
            }
     
            //
            // Rule 326:  LeftHandSide ::= ExpressionName
            //
            case 326: {
                Name ExpressionName = (Name) btParser.getSym(1);
                btParser.setSym1(ExpressionName.toExpr());
                break;
            }
     
            //
            // Rule 329:  AssignmentOperator ::= =
            //
            case 329: {
                
                btParser.setSym1(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 330:  AssignmentOperator ::= *=
            //
            case 330: {
                
                btParser.setSym1(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 331:  AssignmentOperator ::= /=
            //
            case 331: {
                
                btParser.setSym1(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 332:  AssignmentOperator ::= %=
            //
            case 332: {
                
                btParser.setSym1(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 333:  AssignmentOperator ::= +=
            //
            case 333: {
                
                btParser.setSym1(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 334:  AssignmentOperator ::= -=
            //
            case 334: {
                
                btParser.setSym1(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 335:  AssignmentOperator ::= <<=
            //
            case 335: {
                
                btParser.setSym1(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 336:  AssignmentOperator ::= > > =
            //
            case 336: {
                
                // TODO: make sure that there is no space after the ">" signs
                btParser.setSym1(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 337:  AssignmentOperator ::= > > > =
            //
            case 337: {
                
                // TODO: make sure that there is no space after the ">" signs
                btParser.setSym1(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 338:  AssignmentOperator ::= &=
            //
            case 338: {
                
                btParser.setSym1(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 339:  AssignmentOperator ::= ^=
            //
            case 339: {
                
                btParser.setSym1(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 340:  AssignmentOperator ::= |=
            //
            case 340: {
                
                btParser.setSym1(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 343:  Dimsopt ::= $Empty
            //
            case 343: {
                
                btParser.setSym1(new Integer(0));
                break;
            }
     
            //
            // Rule 345:  Catchesopt ::= $Empty
            //
            case 345: {
                
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 347:  identifieropt ::= $Empty
            //
            case 347:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 348:  identifieropt ::= identifier
            //
            case 348: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 349:  ForUpdateopt ::= $Empty
            //
            case 349: {
                
                btParser.setSym1(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 351:  Expressionopt ::= $Empty
            //
            case 351:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 353:  ForInitopt ::= $Empty
            //
            case 353: {
                
                btParser.setSym1(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 355:  SwitchLabelsopt ::= $Empty
            //
            case 355: {
                
                btParser.setSym1(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 357:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 357: {
                
                btParser.setSym1(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 359:  VariableModifiersopt ::= $Empty
            //
            case 359: {
                
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 361:  VariableInitializersopt ::= $Empty
            //
            case 361:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 363:  AbstractMethodModifiersopt ::= $Empty
            //
            case 363: {
                
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 365:  ConstantModifiersopt ::= $Empty
            //
            case 365: {
                
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 367:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 367: {
                
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 369:  ExtendsInterfacesopt ::= $Empty
            //
            case 369: {
                
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 371:  InterfaceModifiersopt ::= $Empty
            //
            case 371: {
                
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 373:  ClassBodyopt ::= $Empty
            //
            case 373:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 375:  Argumentsopt ::= $Empty
            //
            case 375:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 376:  Argumentsopt ::= Arguments
            //
            case 376:
                bad_rule = 376;
                break;
 
            //
            // Rule 377:  ,opt ::= $Empty
            //
            case 377:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 379:  ArgumentListopt ::= $Empty
            //
            case 379: {
                
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 381:  BlockStatementsopt ::= $Empty
            //
            case 381: {
                
                btParser.setSym1(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 383:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 383:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 385:  ConstructorModifiersopt ::= $Empty
            //
            case 385: {
                
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 387:  ...opt ::= $Empty
            //
            case 387:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 389:  FormalParameterListopt ::= $Empty
            //
            case 389: {
                
                btParser.setSym1(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 391:  Throwsopt ::= $Empty
            //
            case 391: {
                
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 393:  MethodModifiersopt ::= $Empty
            //
            case 393: {
                
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 395:  FieldModifiersopt ::= $Empty
            //
            case 395: {
                
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 397:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 397: {
                
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 399:  Interfacesopt ::= $Empty
            //
            case 399: {
                
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 401:  Superopt ::= $Empty
            //
            case 401: {
                
               btParser.setSym1(new Name(nf, ts, pos(), "x10.lang.Object").toType());
                break;
            }
     
            //
            // Rule 403:  ClassModifiersopt ::= $Empty
            //
            case 403: {
                
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 405:  TypeDeclarationsopt ::= $Empty
            //
            case 405: {
                
                btParser.setSym1(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 407:  ImportDeclarationsopt ::= $Empty
            //
            case 407: {
                
                btParser.setSym1(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 409:  PackageDeclarationopt ::= $Empty
            //
            case 409:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 411:  ClassType ::= TypeName
            //
            case 411: {
                Name TypeName = (Name) btParser.getSym(1);
                btParser.setSym1(TypeName.toType());
                break;
            }
     
            //
            // Rule 412:  InterfaceType ::= TypeName
            //
            case 412: {
                Name TypeName = (Name) btParser.getSym(1);
                btParser.setSym1(TypeName.toType());
                break;
            }
     
            //
            // Rule 413:  PackageDeclaration ::= package PackageName ;
            //
            case 413: {
                Name PackageName = (Name) btParser.getSym(2);
                btParser.setSym1(PackageName.toPackage());
                break;
            }
     
            //
            // Rule 414:  NormalClassDeclaration ::= ClassModifiersopt class identifier Superopt Interfacesopt ClassBody
            //
            case 414: {
                Flags ClassModifiersopt = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                TypeNode Superopt = (TypeNode) btParser.getSym(4);
                List Interfacesopt = (List) btParser.getSym(5);
                ClassBody ClassBody = (ClassBody) btParser.getSym(6);
                btParser.setSym1(ClassModifiersopt.isValue()
                             ? nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), 
                                                 ClassModifiersopt, identifier.getIdentifier(), Superopt, Interfacesopt, ClassBody) 
                             : nf.ClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), 
                                            ClassModifiersopt, identifier.getIdentifier(), Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 415:  MethodHeader ::= MethodModifiersopt ResultType MethodDeclarator Throwsopt
            //
            case 415: {
                Flags MethodModifiersopt = (Flags) btParser.getSym(1);
                TypeNode ResultType = (TypeNode) btParser.getSym(2);
                Object[] MethodDeclarator = (Object[]) btParser.getSym(3);
                List Throwsopt = (List) btParser.getSym(4);
                Name c = (Name) MethodDeclarator[0];
                List d = (List) MethodDeclarator[1];
                Integer e = (Integer) MethodDeclarator[2];

                if (ResultType.type() == ts.Void() && e.intValue() > 0)
                {
                    // TODO: error!!!
                    assert(false);
                }

                btParser.setSym1(nf.MethodDecl(pos(btParser.getFirstToken(2), btParser.getLastToken(3)),
                                       MethodModifiersopt,
                                       nf.array((TypeNode) ResultType, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), e.intValue()),
                                       c.toString(),
                                       d,
                                       Throwsopt,
                                       null));
                break;
            }
     
            //
            // Rule 416:  ConstructorDeclarator ::= SimpleTypeName ( FormalParameterListopt )
            //
            case 416: {
                Name SimpleTypeName = (Name) btParser.getSym(1);
                List FormalParameterListopt = (List) btParser.getSym(3);
                Object[] a = new Object[3];
                a[1] = SimpleTypeName;
                a[2] = FormalParameterListopt;
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 417:  ExplicitConstructorInvocation ::= this ( ArgumentListopt ) ;
            //
            case 417: {
                List ArgumentListopt = (List) btParser.getSym(3);
                btParser.setSym1(nf.ThisCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 418:  ExplicitConstructorInvocation ::= super ( ArgumentListopt ) ;
            //
            case 418: {
                List ArgumentListopt = (List) btParser.getSym(3);
                btParser.setSym1(nf.SuperCall(pos(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 419:  ExplicitConstructorInvocation ::= Primary . this ( ArgumentListopt ) ;
            //
            case 419: {
                Expr Primary = (Expr) btParser.getSym(1);
                List ArgumentListopt = (List) btParser.getSym(5);
                btParser.setSym1(nf.ThisCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 420:  ExplicitConstructorInvocation ::= Primary . super ( ArgumentListopt ) ;
            //
            case 420: {
                Expr Primary = (Expr) btParser.getSym(1);
                List ArgumentListopt = (List) btParser.getSym(5);
                btParser.setSym1(nf.SuperCall(pos(), Primary, ArgumentListopt));
                break;
            }
     
            //
            // Rule 421:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier ExtendsInterfacesopt InterfaceBody
            //
            case 421: {
                Flags InterfaceModifiersopt = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                List ExtendsInterfacesopt = (List) btParser.getSym(4);
                ClassBody InterfaceBody = (ClassBody) btParser.getSym(5);
                btParser.setSym1(nf.ClassDecl(pos(),
                                    InterfaceModifiersopt.Interface(),
                                    identifier.getIdentifier(),
                                    null,
                                    ExtendsInterfacesopt,
                                    InterfaceBody));
                break;
            }
     
            //
            // Rule 422:  AbstractMethodDeclaration ::= AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt ;
            //
            case 422: {
                Flags AbstractMethodModifiersopt = (Flags) btParser.getSym(1);
                TypeNode ResultType = (TypeNode) btParser.getSym(2);
                Object[] MethodDeclarator = (Object[]) btParser.getSym(3);
                List Throwsopt = (List) btParser.getSym(4);
                Name c = (Name) MethodDeclarator[0];
                List d = (List) MethodDeclarator[1];
                Integer e = (Integer) MethodDeclarator[2];

                if (ResultType.type() == ts.Void() && e.intValue() > 0)
                {
                    // TODO: error!!!
                    assert(false);
                }

                btParser.setSym1(nf.MethodDecl(pos(btParser.getFirstToken(2), btParser.getLastToken(3)),
                                       AbstractMethodModifiersopt ,
                                       nf.array((TypeNode) ResultType, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), e.intValue()),
                                       c.toString(),
                                       d,
                                       Throwsopt,
                                       null));
                break;
            }
     
            //
            // Rule 423:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
            //
            case 423: {
                TypeNode ClassOrInterfaceType = (TypeNode) btParser.getSym(2);
                List ArgumentListopt = (List) btParser.getSym(4);
                ClassBody ClassBodyopt = (ClassBody) btParser.getSym(6);
                if (ClassBodyopt == null)
                     btParser.setSym1(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt));
                else btParser.setSym1(nf.New(pos(), ClassOrInterfaceType, ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 424:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 424: {
                Expr Primary = (Expr) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(4);
                List ArgumentListopt = (List) btParser.getSym(6);
                ClassBody ClassBodyopt = (ClassBody) btParser.getSym(8);
                Name b = new Name(nf, ts, pos(), identifier.getIdentifier());
                if (ClassBodyopt == null)
                     btParser.setSym1(nf.New(pos(), Primary, b.toType(), ArgumentListopt));
                else btParser.setSym1(nf.New(pos(), Primary, b.toType(), ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 425:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 425: {
                Name AmbiguousName = (Name) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(4);
                List ArgumentListopt = (List) btParser.getSym(6);
                ClassBody ClassBodyopt = (ClassBody) btParser.getSym(8);
                Name b = new Name(nf, ts, pos(), identifier.getIdentifier());
                if (ClassBodyopt == null)
                     btParser.setSym1(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt));
                else btParser.setSym1(nf.New(pos(), AmbiguousName.toExpr(), b.toType(), ArgumentListopt, ClassBodyopt));
                break;
            }
     
            //
            // Rule 426:  MethodInvocation ::= Primary . identifier ( ArgumentListopt )
            //
            case 426: {
                Expr Primary = (Expr) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                List ArgumentListopt = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), Primary, identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 427:  MethodInvocation ::= super . identifier ( ArgumentListopt )
            //
            case 427: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                List ArgumentListopt = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken())), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 428:  MethodInvocation ::= ClassName . super . identifier ( ArgumentListopt )
            //
            case 428: {
                Name ClassName = (Name) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(5);
                List ArgumentListopt = (List) btParser.getSym(7);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken(3)), ClassName.toType()), identifier.getIdentifier(), ArgumentListopt));
                break;
            }
     
            //
            // Rule 429:  Type ::= DataType PlaceTypeSpecifieropt
            //
            case 429: {
                TypeNode DataType = (TypeNode) btParser.getSym(1);
                Object PlaceTypeSpecifieropt = (Object) btParser.getSym(2);
                // Just parse the placetype and drop it for now.
                break;
            }
     
            //
            // Rule 430:  Type ::= nullable Type
            //
            case 430: {
                TypeNode Type = (TypeNode) btParser.getSym(2);
                btParser.setSym1(nf.Nullable(pos(), Type));
                break;
            }
     
            //
            // Rule 431:  Type ::= future < Type >
            //
            case 431: {
                TypeNode Type = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Future(pos(), Type));
                break;
            }
     
            //
            // Rule 440:  ClassOrInterfaceType ::= TypeName DepParametersopt
            //
            case 440: {
                Name TypeName = (Name) btParser.getSym(1);
                DepParameterExpr DepParametersopt = (DepParameterExpr) btParser.getSym(2); 
                btParser.setSym1(DepParametersopt == null
                               ? TypeName.toType()
                               : nf.ParametricTypeNode(pos(), TypeName.toType(), null, DepParametersopt));
                break;
            }
     
            //
            // Rule 441:  DepParameters ::= ( DepParameterExpr )
            //
            case 441: {
                DepParameterExpr DepParameterExpr = (DepParameterExpr) btParser.getSym(2);
                btParser.setSym1(DepParameterExpr);
                break;
            }
     
            //
            // Rule 442:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 442: {
                List ArgumentList = (List) btParser.getSym(1);
                Expr WhereClauseopt = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.DepParameterExpr(pos(), ArgumentList, WhereClauseopt));
                break;
            }
     
            //
            // Rule 443:  DepParameterExpr ::= WhereClause
            //
            case 443: {
                Expr WhereClause = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.DepParameterExpr(pos(), null, WhereClause));
                break;
            }
     
            //
            // Rule 444:  WhereClause ::= : Expression
            //
            case 444: {
                Expr Expression = (Expr) btParser.getSym(2);
                btParser.setSym1(Expression);
                break;
            }
     
            //
            // Rule 446:  X10ArrayType ::= Type [ . ]
            //
            case 446: {
                TypeNode Type = (TypeNode) btParser.getSym(1);
                btParser.setSym1(nf.X10ArrayTypeNode(pos(), Type, false, null));
                break;
            }
     
            //
            // Rule 447:  X10ArrayType ::= Type reference [ . ]
            //
            case 447: {
                TypeNode Type = (TypeNode) btParser.getSym(1);
                btParser.setSym1(nf.X10ArrayTypeNode(pos(), Type, false, null));
                break;
            }
     
            //
            // Rule 448:  X10ArrayType ::= Type value [ . ]
            //
            case 448: {
                TypeNode Type = (TypeNode) btParser.getSym(1);
                btParser.setSym1(nf.X10ArrayTypeNode(pos(), Type, true, null));
                break;
            }
     
            //
            // Rule 449:  X10ArrayType ::= Type [ DepParameterExpr ]
            //
            case 449: {
                TypeNode Type = (TypeNode) btParser.getSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) btParser.getSym(3);
                btParser.setSym1(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
                break;
            }
     
            //
            // Rule 450:  X10ArrayType ::= Type reference [ DepParameterExpr ]
            //
            case 450: {
                TypeNode Type = (TypeNode) btParser.getSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) btParser.getSym(4);
                btParser.setSym1(nf.X10ArrayTypeNode(pos(), Type, false, DepParameterExpr));
                break;
            }
     
            //
            // Rule 451:  X10ArrayType ::= Type value [ DepParameterExpr ]
            //
            case 451: {
                TypeNode Type = (TypeNode) btParser.getSym(1);
                DepParameterExpr DepParameterExpr = (DepParameterExpr) btParser.getSym(4);
                DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
                btParser.setSym1(nf.X10ArrayTypeNode(pos(), Type, true, DepParameterExpr));
                break;
            }
     
            //
            // Rule 452:  ObjectKind ::= value
            //
            case 452:
                bad_rule = 452;
                break;
 
            //
            // Rule 453:  ObjectKind ::= reference
            //
            case 453:
                bad_rule = 453;
                break;
 
            //
            // Rule 454:  MethodModifier ::= atomic
            //
            case 454: {
                
                btParser.setSym1(Flags.ATOMIC);
                break;
            }
     
            //
            // Rule 455:  MethodModifier ::= extern
            //
            case 455: {
                
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 457:  ValueClassDeclaration ::= ClassModifiersopt value identifier Superopt Interfacesopt ClassBody
            //
            case 457: {
                Flags ClassModifiersopt = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                TypeNode Superopt = (TypeNode) btParser.getSym(4);
                List Interfacesopt = (List) btParser.getSym(5);
                ClassBody ClassBody = (ClassBody) btParser.getSym(6);
                btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()),
                                             ClassModifiersopt, identifier.getIdentifier(), Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 458:  ValueClassDeclaration ::= ClassModifiersopt value class identifier Superopt Interfacesopt ClassBody
            //
            case 458: {
                Flags ClassModifiersopt = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(4);
                TypeNode Superopt = (TypeNode) btParser.getSym(5);
                List Interfacesopt = (List) btParser.getSym(6);
                ClassBody ClassBody = (ClassBody) btParser.getSym(7);
              btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()),
                                           ClassModifiersopt, identifier.getIdentifier(), Superopt, Interfacesopt, ClassBody));
                break;
            }
     
            //
            // Rule 459:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt [ ] ArrayInitializer
            //
            case 459: {
                TypeNode ArrayBaseType = (TypeNode) btParser.getSym(2);
                Here Unsafeopt = (Here) btParser.getSym(3);
                ArrayInit ArrayInitializer = (ArrayInit) btParser.getSym(6);
                // btParser.setSym1(nf.ArrayConstructor(pos(), a, false, null, d));
                btParser.setSym1(nf.NewArray(pos(), ArrayBaseType, 1, ArrayInitializer));
                break;
            }
     
            //
            // Rule 460:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt [ Expression ]
            //
            case 460: {
                TypeNode ArrayBaseType = (TypeNode) btParser.getSym(2);
                Here Unsafeopt = (Here) btParser.getSym(3);
                Expr Expression = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, false, Expression, null));
                break;
            }
     
            //
            // Rule 461:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt [ Expression$distr ] Expression$initializer
            //
            case 461: {
                TypeNode ArrayBaseType = (TypeNode) btParser.getSym(2);
                Here Unsafeopt = (Here) btParser.getSym(3);
                Expr distr = (Expr) btParser.getSym(5);
                Expr initializer = (Expr) btParser.getSym(7);
                btParser.setSym1(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, false, distr, initializer));
                break;
            }
     
            //
            // Rule 462:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt [ Expression ] ( FormalParameter ) MethodBody
            //
            case 462: {
                TypeNode ArrayBaseType = (TypeNode) btParser.getSym(2);
                Here Unsafeopt = (Here) btParser.getSym(3);
                Expr Expression = (Expr) btParser.getSym(5);
                X10Formal FormalParameter = (X10Formal) btParser.getSym(8);
                Block MethodBody = (Block) btParser.getSym(10);
                New initializer = makeInitializer( pos(btParser.getFirstToken(7), btParser.getLastToken()), ArrayBaseType, FormalParameter, MethodBody );
                btParser.setSym1(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, false, Expression, initializer));
                break;
            }
     
            //
            // Rule 463:  ArrayCreationExpression ::= new ArrayBaseType value Unsafeopt [ Expression ]
            //
            case 463: {
                TypeNode ArrayBaseType = (TypeNode) btParser.getSym(2);
                Here Unsafeopt = (Here) btParser.getSym(4);
                Expr Expression = (Expr) btParser.getSym(6);
                btParser.setSym1(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, true, Expression, null));
                break;
            }
     
            //
            // Rule 464:  ArrayCreationExpression ::= new ArrayBaseType value Unsafeopt [ Expression$expr1 ] Expression$expr2
            //
            case 464: {
                TypeNode ArrayBaseType = (TypeNode) btParser.getSym(2);
                Here Unsafeopt = (Here) btParser.getSym(4);
                Expr expr1 = (Expr) btParser.getSym(6);
                Expr expr2 = (Expr) btParser.getSym(8);
                btParser.setSym1(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, true, expr1, expr2));
                break;
            }
     
            //
            // Rule 465:  ArrayCreationExpression ::= new ArrayBaseType value Unsafeopt [ Expression ] ( FormalParameter ) MethodBody
            //
            case 465: {
                TypeNode ArrayBaseType = (TypeNode) btParser.getSym(2);
                Here Unsafeopt = (Here) btParser.getSym(4);
                Expr Expression = (Expr) btParser.getSym(6);
                X10Formal FormalParameter = (X10Formal) btParser.getSym(9);
                Block MethodBody = (Block) btParser.getSym(11);
                New initializer = makeInitializer(pos(btParser.getFirstToken(8), btParser.getLastToken(11)), ArrayBaseType, FormalParameter, MethodBody);
                btParser.setSym1(nf.ArrayConstructor(pos(), ArrayBaseType, Unsafeopt != null, true, Expression, initializer));
                break;
            }
     
            //
            // Rule 468:  ArrayAccess ::= ExpressionName [ ArgumentList ]
            //
            case 468: {
                Name ExpressionName = (Name) btParser.getSym(1);
                List ArgumentList = (List) btParser.getSym(3);
                if (ArgumentList.size() == 1)
                     btParser.setSym1(nf.X10ArrayAccess1(pos(), ExpressionName.toExpr(), (Expr) ArgumentList.get(0)));
                else btParser.setSym1(nf.X10ArrayAccess(pos(), ExpressionName.toExpr(), ArgumentList));
                break;
            }
     
            //
            // Rule 469:  ArrayAccess ::= PrimaryNoNewArray [ ArgumentList ]
            //
            case 469: {
                Expr PrimaryNoNewArray = (Expr) btParser.getSym(1);
                List ArgumentList = (List) btParser.getSym(3); 
                if (ArgumentList.size() == 1)
                     btParser.setSym1(nf.X10ArrayAccess1(pos(), PrimaryNoNewArray, (Expr) ArgumentList.get(0)));
                else btParser.setSym1(nf.X10ArrayAccess(pos(), PrimaryNoNewArray, ArgumentList));
                break;
            }
     
            //
            // Rule 488:  NowStatement ::= now ( Clock ) Statement
            //
            case 488: {
                Name Clock = (Name) btParser.getSym(3);
                Stmt Statement = (Stmt) btParser.getSym(5);
              btParser.setSym1(nf.Now(pos(), Clock.toExpr(), Statement));
                break;
            }
     
            //
            // Rule 489:  ClockedStatement ::= clocked ( ClockList ) Statement
            //
            case 489: {
                List ClockList = (List) btParser.getSym(3);
                Stmt Statement = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), ClockList, Statement));
                break;
            }
     
            //
            // Rule 490:  AsyncStatement ::= async PlaceExpressionSingleListopt Statement
            //
            case 490: {
                Expr PlaceExpressionSingleListopt = (Expr) btParser.getSym(2);
                Stmt Statement = (Stmt) btParser.getSym(3);
              btParser.setSym1(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(btParser.getFirstToken()))
                                                                        : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 491:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 491: {
                Expr PlaceExpressionSingleListopt = (Expr) btParser.getSym(2);
                Stmt Statement = (Stmt) btParser.getSym(3);
              btParser.setSym1(nf.Atomic(pos(), (PlaceExpressionSingleListopt == null
                                               ? nf.Here(pos(btParser.getFirstToken()))
                                               : PlaceExpressionSingleListopt), Statement));
                break;
            }
     
            //
            // Rule 492:  WhenStatement ::= when ( Expression ) Statement
            //
            case 492: {
                Expr Expression = (Expr) btParser.getSym(3);
                Stmt Statement = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.When(pos(), Expression, Statement));
                break;
            }
     
            //
            // Rule 493:  WhenStatement ::= WhenStatement or ( Expression ) Statement
            //
            case 493: {
                When WhenStatement = (When) btParser.getSym(1);
                Expr Expression = (Expr) btParser.getSym(4);
                Stmt Statement = (Stmt) btParser.getSym(6);
              When.Branch wb = nf.WhenBranch(pos(btParser.getFirstToken(2), btParser.getLastToken()), Expression, Statement);
              WhenStatement.add(wb);
              btParser.setSym1(WhenStatement);
                break;
            }
     
            //
            // Rule 494:  ForEachStatement ::= foreach ( FormalParameter : Expression ) Statement
            //
            case 494: {
                X10Formal FormalParameter = (X10Formal) btParser.getSym(3);
                Expr Expression = (Expr) btParser.getSym(5);
                Stmt Statement = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.ForEach(pos(),
                              FormalParameter.flags(FormalParameter.flags().Final()),
                              Expression, 
                              FormalParameter.hasExplodedVars() 
                                  ? nf.Block(pos(), FormalParameter.explode(Statement))
                                  : Statement));
                break;
            }
     
            //
            // Rule 495:  AtEachStatement ::= ateach ( FormalParameter : Expression ) Statement
            //
            case 495: {
                X10Formal FormalParameter = (X10Formal) btParser.getSym(3);
                Expr Expression = (Expr) btParser.getSym(5);
                Stmt Statement = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.AtEach(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression, 
                             FormalParameter.hasExplodedVars() 
                                   ? nf.Block(pos(), FormalParameter.explode(Statement))
                                   : Statement));
                break;
            }
     
            //
            // Rule 496:  EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
            //
            case 496: {
                X10Formal FormalParameter = (X10Formal) btParser.getSym(3);
                Expr Expression = (Expr) btParser.getSym(5);
                Stmt Statement = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.ForLoop(pos(),
                        FormalParameter.flags(FormalParameter.flags().Final()),
                        Expression, 
                        FormalParameter.hasExplodedVars() 
                              ? nf.Block(pos(), FormalParameter.explode(Statement))
                              : Statement));           
                break;
            }
     
            //
            // Rule 497:  FinishStatement ::= finish Statement
            //
            case 497: {
                Stmt Statement = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Finish(pos(),  Statement));
                break;
            }
     
            //
            // Rule 498:  NowStatementNoShortIf ::= now ( Clock ) StatementNoShortIf
            //
            case 498: {
                Name Clock = (Name) btParser.getSym(3);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Now(pos(), Clock.toExpr(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 499:  ClockedStatementNoShortIf ::= clocked ( ClockList ) StatementNoShortIf
            //
            case 499: {
                List ClockList = (List) btParser.getSym(3);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), ClockList, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 500:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt StatementNoShortIf
            //
            case 500: {
                Expr PlaceExpressionSingleListopt = (Expr) btParser.getSym(2);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(btParser.getFirstToken()))
                                                : PlaceExpressionSingleListopt),
                                            StatementNoShortIf));
                break;
            }
     
            //
            // Rule 501:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 501: {
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Atomic(pos(), nf.Here(pos(btParser.getFirstToken())), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 502:  WhenStatementNoShortIf ::= when ( Expression ) StatementNoShortIf
            //
            case 502: {
                Expr Expression = (Expr) btParser.getSym(3);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.When(pos(), Expression, StatementNoShortIf));
                break;
            }
     
            //
            // Rule 503:  WhenStatementNoShortIf ::= WhenStatement or ( Expression ) StatementNoShortIf
            //
            case 503: {
                When WhenStatement = (When) btParser.getSym(1);
                Expr Expression = (Expr) btParser.getSym(4);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(6);
                WhenStatement.add(nf.WhenBranch(pos(btParser.getFirstToken(2), btParser.getLastToken()), Expression, StatementNoShortIf));
                btParser.setSym1(WhenStatement);
                break;
            }
     
            //
            // Rule 504:  ForEachStatementNoShortIf ::= foreach ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 504: {
                X10Formal FormalParameter = (X10Formal) btParser.getSym(3);
                Expr Expression = (Expr) btParser.getSym(5);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.ForEach(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression, 
                             FormalParameter.hasExplodedVars() 
                                   ? nf.Block(pos(), FormalParameter.explode(StatementNoShortIf))
                                   : StatementNoShortIf));
       
                break;
            }
     
            //
            // Rule 505:  AtEachStatementNoShortIf ::= ateach ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 505: {
                X10Formal FormalParameter = (X10Formal) btParser.getSym(3);
                Expr Expression = (Expr) btParser.getSym(5);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.AtEach(pos(),
                            FormalParameter.flags(FormalParameter.flags().Final()),
                            Expression, 
                            FormalParameter.hasExplodedVars() 
                                  ? nf.Block(pos(), FormalParameter.explode(StatementNoShortIf))
                                  : StatementNoShortIf));
                break;
            }
     
            //
            // Rule 506:  EnhancedForStatementNoShortIf ::= for ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 506: {
                X10Formal FormalParameter = (X10Formal) btParser.getSym(3);
                Expr Expression = (Expr) btParser.getSym(5);
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(7);
                  btParser.setSym1(nf.ForLoop(pos(),
                             FormalParameter.flags(FormalParameter.flags().Final()),
                             Expression, 
                             FormalParameter.hasExplodedVars() 
                                   ? nf.Block(pos(), FormalParameter.explode(StatementNoShortIf))
                                   : StatementNoShortIf));
                break;
            }
     
            //
            // Rule 507:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 507: {
                Stmt StatementNoShortIf = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Finish(pos(), StatementNoShortIf));
                break;
            }
     
            //
            // Rule 508:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 508: {
                Expr PlaceExpression = (Expr) btParser.getSym(2);
              btParser.setSym1(PlaceExpression);
                break;
            }
     
            //
            // Rule 510:  NextStatement ::= next ;
            //
            case 510: {
                
                btParser.setSym1(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 511:  AwaitStatement ::= await Expression ;
            //
            case 511: {
                Expr Expression = (Expr) btParser.getSym(2); 
                btParser.setSym1(nf.Await(pos(), Expression));
                break;
            }
     
            //
            // Rule 512:  ClockList ::= Clock
            //
            case 512: {
                Name Clock = (Name) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock.toExpr());
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 513:  ClockList ::= ClockList , Clock
            //
            case 513: {
                List ClockList = (List) btParser.getSym(1);
                Name Clock = (Name) btParser.getSym(3);
                ClockList.add(Clock.toExpr());
                btParser.setSym1(ClockList);
                break;
            }
     
            //
            // Rule 514:  Clock ::= identifier
            //
            case 514: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
     
            //
            // Rule 515:  CastExpression ::= ( Type ) UnaryExpressionNotPlusMinus
            //
            case 515: {
                TypeNode Type = (TypeNode) btParser.getSym(2);
                Expr UnaryExpressionNotPlusMinus = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Cast(pos(), Type, UnaryExpressionNotPlusMinus));
                break;
            }
     
            //
            // Rule 516:  MethodInvocation ::= Primary -> identifier ( ArgumentListopt )
            //
            case 516: {
                Expr Primary = (Expr) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3);
                List ArgumentListopt = (List) btParser.getSym(5); 
                 btParser.setSym1(nf.RemoteCall(pos(), Primary, identifier.getIdentifier(), ArgumentListopt));
                break;
            } 
     
            //
            // Rule 517:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 517: {
                Expr RelationalExpression = (Expr) btParser.getSym(1);
                TypeNode Type = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Instanceof(pos(), RelationalExpression, Type));
                break;
            }
     
            //
            // Rule 518:  IdentifierList ::= identifier
            //
            case 518: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Name.class, false);
                l.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 519:  IdentifierList ::= IdentifierList , identifier
            //
            case 519: {
                List IdentifierList = (List) btParser.getSym(1);
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(3); 
                IdentifierList.add(new Name(nf, ts, pos(), identifier.getIdentifier()));
                btParser.setSym1(IdentifierList);
                break;
            }
     
            //
            // Rule 521:  Primary ::= [ ArgumentList ]
            //
            case 521: {
                List ArgumentList = (List) btParser.getSym(2);
                Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");
                Name x10LangRegion = new Name(nf, ts, pos(), x10Lang, "region");
                Name x10LangRegionFactory = new Name(nf, ts, pos(), x10LangRegion, "factory");
                Name x10LangRegionFactoryRegion = new Name(nf, ts, pos(), x10LangRegionFactory, "region");
                Name x10LangPoint = new Name(nf, ts, pos(), x10Lang, "point");
                Name x10LangPointFactory = new Name(nf, ts, pos(), x10LangPoint, "factory");
                Name x10LangPointFactoryPoint = new Name(nf, ts, pos(), x10LangPointFactory, "point");

                Tuple tuple  = nf.Tuple(pos(), x10LangPointFactoryPoint, x10LangRegionFactoryRegion, ArgumentList);
                btParser.setSym1(tuple);
                break;
            }
     
            //
            // Rule 522:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 522: {
                Expr expr1 = (Expr) btParser.getSym(1);
                Expr expr2 = (Expr) btParser.getSym(3);
                //System.out.println("Distribution:" + a + "|" + b + "|");
                // x10.lang.region.factory.region(  ArgumentList )
                // Construct the MethodName
                Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");

                Name x10LangDistribution = new Name(nf, ts, pos(), x10Lang, "distribution");
                Name x10LangDistributionFactory = new Name(nf, ts, pos(), x10LangDistribution, "factory");
                Name x10LangDistributionFactoryConstant = new Name(nf, ts, pos(), x10LangDistributionFactory, "constant");
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(expr1);
                l.add(expr2);
                Call call = nf.Call(pos(), x10LangDistributionFactoryConstant.prefix.toReceiver(), "constant", l);
                btParser.setSym1(call);
                break;
            }
     
            //
            // Rule 523:  Primary ::= Expression$expr1 : Expression$expr2
            //
            case 523: {
                Expr expr1 = (Expr) btParser.getSym(1);
                Expr expr2 = (Expr) btParser.getSym(3);
                Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");

                Name x10LangRegion = new Name(nf, ts, pos(), x10Lang, "region");
                Name x10LangRegionFactory = new Name(nf, ts, pos(), x10LangRegion, "factory");
                Name x10LangRegionFactoryRegion = new Name(nf, ts, pos(), x10LangRegionFactory, "region");
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(expr1);
                l.add(expr2);
                Call regionCall = nf.Call( pos(), x10LangRegionFactoryRegion.prefix.toReceiver(), "region", l  );
                btParser.setSym1(regionCall);
                break;
            }
     
            //
            // Rule 524:  FutureExpression ::= future PlaceExpressionSingleListopt { Expression }
            //
            case 524: {
                Expr PlaceExpressionSingleListopt = (Expr) btParser.getSym(2);
                Expr Expression = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Future(pos(), (PlaceExpressionSingleListopt == null
                                                ? nf.Here(pos(btParser.getFirstToken()))
                                                : PlaceExpressionSingleListopt), Expression));
                break;
            }
     
            //
            // Rule 525:  FieldModifier ::= mutable
            //
            case 525: {
                
                btParser.setSym1(Flags.MUTABLE);
                break;
            }
     
            //
            // Rule 526:  FieldModifier ::= const
            //
            case 526: {
                
                btParser.setSym1(Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL));
                break;
            }
     
            //
            // Rule 527:  FunExpression ::= fun Type ( FormalParameterListopt ) { Expression }
            //
            case 527:
                bad_rule = 527;
                break;
 
            //
            // Rule 528:  MethodInvocation ::= MethodName ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 528:
                bad_rule = 528;
                break; 
 
            //
            // Rule 529:  MethodInvocation ::= Primary . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 529:
                bad_rule = 529;
                break; 
 
            //
            // Rule 530:  MethodInvocation ::= super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 530:
                bad_rule = 530;
                break; 
 
            //
            // Rule 531:  MethodInvocation ::= ClassName . super . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 531:
                bad_rule = 531;
                break; 
 
            //
            // Rule 532:  MethodInvocation ::= TypeName . identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 )
            //
            case 532:
                bad_rule = 532;
                break; 
 
            //
            // Rule 533:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 533:
                bad_rule = 533;
                break; 
 
            //
            // Rule 534:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 534:
                bad_rule = 534;
                break; 
 
            //
            // Rule 535:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt$args1 ) ( ArgumentListopt$args2 ) ClassBodyopt
            //
            case 535:
                bad_rule = 535;
                break; 
 
            //
            // Rule 536:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 536:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 538:  DepParametersopt ::= $Empty
            //
            case 538:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 540:  WhereClauseopt ::= $Empty
            //
            case 540:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 542:  ObjectKindopt ::= $Empty
            //
            case 542:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 544:  ArrayInitializeropt ::= $Empty
            //
            case 544:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 546:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 546:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 548:  ArgumentListopt ::= $Empty
            //
            case 548:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 550:  DepParametersopt ::= $Empty
            //
            case 550:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 552:  Unsafeopt ::= $Empty
            //
            case 552:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 553:  Unsafeopt ::= unsafe
            //
            case 553: {
                
                btParser.setSym1(nf.Here(pos(btParser.getFirstToken(1))));
                break;
            }
     
            //
            // Rule 554:  ParamIdopt ::= $Empty
            //
            case 554:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 555:  ParamIdopt ::= identifier
            //
            case 555: {
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) btParser.getSym(1);
                btParser.setSym1(new Name(nf, ts, pos(), identifier.getIdentifier()));
                break;
            }
        
            default:
                break;
        }
        return;
    }
}

