
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
//Intended for the Feb 2005 X10 release.

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
     if (prsStream.getKind(btParser.getToken(1)) != X10Parsersym.TK_IDENTIFIER
         && (Report.should_report("parser", 2))) {
         Report.report(2,"Parser turning keyword " +
                       prsStream.getName(btParser.getToken(1)) +
                       " at " +
                       prsStream.getLine(btParser.getToken(1)) +
                       ":" +
                       prsStream.getColumn(btParser.getToken(1)) +
                       " into an identifier");
      }
                break;
            }
     
            //
            // Rule 2:  PrimitiveType ::= NumericType
            //
            case 2:
                break;
 
            //
            // Rule 3:  PrimitiveType ::= boolean
            //
            case 3: {
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Boolean()));
                break;
            }
     
            //
            // Rule 4:  NumericType ::= IntegralType
            //
            case 4:
                break;
 
            //
            // Rule 5:  NumericType ::= FloatingPointType
            //
            case 5:
                break;
 
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
            // Rule 13:  ClassType ::= TypeName
            //
            case 13: {
//vj                    assert(btParser.getSym(2) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toType());
                break;
            }
     
            //
            // Rule 14:  InterfaceType ::= TypeName
            //
            case 14: {
//vj                    assert(btParser.getSym(2) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toType());
                break;
            }
     
            //
            // Rule 15:  TypeName ::= identifier
            //
            case 15: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 16:  TypeName ::= TypeName . identifier
            //
            case 16: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  a,
                                  b.getIdentifier()));
                break;
            }
     
            //
            // Rule 17:  ClassName ::= TypeName
            //
            case 17:
                break;
 
            //
            // Rule 18:  ArrayType ::= Type [ ]
            //
            case 18: {
                TypeNode a = (TypeNode) btParser.getSym(1);
                btParser.setSym1(nf.array(a, pos(), 1));
                break;
            }
     
            //
            // Rule 19:  PackageName ::= identifier
            //
            case 19: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 20:  PackageName ::= PackageName . identifier
            //
            case 20: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  a,
                                  b.getIdentifier()));
                break;
            }
     
            //
            // Rule 21:  ExpressionName ::= identifier
            //
            case 21: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
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
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  a,
                                  b.getIdentifier()));
                break;
            }
     
            //
            // Rule 24:  MethodName ::= identifier
            //
            case 24: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 25:  MethodName ::= AmbiguousName . identifier
            //
            case 25: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  a,
                                  b.getIdentifier()));
                break;
            }
     
            //
            // Rule 26:  PackageOrTypeName ::= identifier
            //
            case 26: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 27:  PackageOrTypeName ::= PackageOrTypeName . identifier
            //
            case 27: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  a,
                                  b.getIdentifier()));
                break;
            }
     
            //
            // Rule 28:  AmbiguousName ::= identifier
            //
            case 28: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 29:  AmbiguousName ::= AmbiguousName . identifier
            //
            case 29: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(new Name(nf,
                                  ts,
                                  pos(btParser.getFirstToken(), btParser.getLastToken()),
                                  a,
                                  b.getIdentifier()));
               break;
            }
     
            //
            // Rule 30:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 30: {
                PackageNode a = (PackageNode) btParser.getSym(1);
                List b = (List) btParser.getSym(2),
                     c = (List) btParser.getSym(3);
                // Add import x10.lang.* by default.
                Name x10 = new Name(nf, ts, pos(), "x10");
                Name x10Lang = new Name(nf, ts, pos(), x10, "lang");
                Import x10LangImport = 
                nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.PACKAGE, x10Lang.toString());
                b.add(x10LangImport);
                btParser.setSym1(nf.SourceFile(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b, c));
                break;
            }
     
            //
            // Rule 31:  ImportDeclarations ::= ImportDeclaration
            //
            case 31: {
                List l = new TypedList(new LinkedList(), Import.class, false);
                Import a = (Import) btParser.getSym(1);
                l.add(a);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 32:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 32: {
                List l = (TypedList) btParser.getSym(1);
                Import b = (Import) btParser.getSym(2);
                if (b != null)
                    l.add(b);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 33:  TypeDeclarations ::= TypeDeclaration
            //
            case 33: {
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                TopLevelDecl a = (TopLevelDecl) btParser.getSym(1);
                if (a != null)
                    l.add(a);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 34:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 34: {
                List l = (TypedList) btParser.getSym(1);
                TopLevelDecl b = (TopLevelDecl) btParser.getSym(2);
                if (b != null)
                    l.add(b);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 35:  PackageDeclaration ::= package PackageName ;
            //
            case 35: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(a.toPackage());
                break;
            }
     
            //
            // Rule 36:  ImportDeclaration ::= SingleTypeImportDeclaration
            //
            case 36:
                break;
 
            //
            // Rule 37:  ImportDeclaration ::= TypeImportOnDemandDeclaration
            //
            case 37:
                break;
 
            //
            // Rule 38:  ImportDeclaration ::= SingleStaticImportDeclaration
            //
            case 38:
                break;
 
            //
            // Rule 39:  ImportDeclaration ::= StaticImportOnDemandDeclaration
            //
            case 39:
                break;
 
            //
            // Rule 40:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 40: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.CLASS, a.toString()));
                break;
            }
     
            //
            // Rule 41:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 41: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.PACKAGE, a.toString()));
                break;
            }
     
            //
            // Rule 42:  SingleStaticImportDeclaration ::= import static TypeName . identifier ;
            //
            case 42:
                bad_rule = 42;
                break;
 
            //
            // Rule 43:  StaticImportOnDemandDeclaration ::= import static TypeName . * ;
            //
            case 43:
                bad_rule = 43;
                break;
 
            //
            // Rule 44:  TypeDeclaration ::= ClassDeclaration
            //
            case 44:
                break;
 
            //
            // Rule 45:  TypeDeclaration ::= InterfaceDeclaration
            //
            case 45:
                break;
 
            //
            // Rule 46:  TypeDeclaration ::= ;
            //
            case 46: {
                btParser.setSym1(null);
                break;
            }
     
            //
            // Rule 47:  ClassDeclaration ::= NormalClassDeclaration
            //
            case 47:
                break;
 
            //
            // Rule 48:  NormalClassDeclaration ::= ClassModifiersopt class identifier Superopt Interfacesopt ClassBody
            //
            case 48: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
//vj                    assert(btParser.getSym(4) == null);
                TypeNode c = (TypeNode) btParser.getSym(4);
                // by default extend x10.lang.Object
                if (c == null) {
                  c= new Name(nf, ts, pos(), "x10.lang.Object").toType();
                }
                List d = (List) btParser.getSym(5);
                ClassBody e = (ClassBody) btParser.getSym(6);
                btParser.setSym1(a.isValue()
                             ? nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), 
                                                 a, b.getIdentifier(), c, d, e) 
                             : nf.ClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), 
                                            a, b.getIdentifier(), c, d, e));
                break;
            }
     
            //
            // Rule 49:  ClassModifiers ::= ClassModifier
            //
            case 49:
                break;
 
            //
            // Rule 50:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 50: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 51:  ClassModifier ::= public
            //
            case 51: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 52:  ClassModifier ::= protected
            //
            case 52: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 53:  ClassModifier ::= private
            //
            case 53: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 54:  ClassModifier ::= abstract
            //
            case 54: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 55:  ClassModifier ::= static
            //
            case 55: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 56:  ClassModifier ::= final
            //
            case 56: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 57:  ClassModifier ::= strictfp
            //
            case 57: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 58:  Super ::= extends ClassType
            //
            case 58: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 59:  Interfaces ::= implements InterfaceTypeList
            //
            case 59: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 60:  InterfaceTypeList ::= InterfaceType
            //
            case 60: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 61:  InterfaceTypeList ::= InterfaceTypeList , InterfaceType
            //
            case 61: {
                List l = (TypedList) btParser.getSym(1);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 62:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 62: {
                btParser.setSym1(nf.ClassBody(pos(btParser.getFirstToken(), btParser.getLastToken()), (List) btParser.getSym(2)));
                break;
            }
     
            //
            // Rule 63:  ClassBodyDeclarations ::= ClassBodyDeclaration
            //
            case 63:
                break;
 
            //
            // Rule 64:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 64: {
                List a = (List) btParser.getSym(1),
                     b = (List) btParser.getSym(2);
                a.addAll(b);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 65:  ClassBodyDeclaration ::= ClassMemberDeclaration
            //
            case 65:
                break;
 
            //
            // Rule 66:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 66: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Block a = (Block) btParser.getSym(1);
                l.add(nf.Initializer(pos(), Flags.NONE, a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 67:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 67: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Block a = (Block) btParser.getSym(1);
                l.add(nf.Initializer(pos(), Flags.STATIC, a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 68:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 68: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 69:  ClassMemberDeclaration ::= FieldDeclaration
            //
            case 69:
                break;
 
            //
            // Rule 70:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 70: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 71:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 71: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 72:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 72: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 73:  ClassMemberDeclaration ::= ;
            //
            case 73: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 74:  FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators ;
            //
            case 74: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Flags a = (Flags) btParser.getSym(1);
                TypeNode b = (TypeNode) btParser.getSym(2);
                List c = (List) btParser.getSym(3);
                for (Iterator i = c.iterator(); i.hasNext();)
                {
                    X10VarDeclarator d = (X10VarDeclarator) i.next();
                    if (d.hasExplodedVars())
                      // TODO: Report this exception correctly.
                      throw new Error("Field Declarations may not have exploded variables." + pos());
                    d.setFlag(a);
                    l.add(nf.FieldDecl(pos(btParser.getFirstToken(2), btParser.getLastToken()),
                                       d.flags,
                                       nf.array(b, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), d.dims),
                                       d.name,
                                       d.init));
                }
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 75:  VariableDeclarators ::= VariableDeclarator
            //
            case 75: {
                List l = new TypedList(new LinkedList(), X10VarDeclarator.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 76:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 76: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 77:  VariableDeclarator ::= VariableDeclaratorId
            //
            case 77:
                break;
 
            //
            // Rule 78:  VariableDeclarator ::= VariableDeclaratorId = VariableInitializer
            //
            case 78: {
                X10VarDeclarator a = (X10VarDeclarator) btParser.getSym(1);
                Expr b = (Expr) btParser.getSym(3);
                a.init = b; 
                // btParser.setSym1(a); 
                break;
            }
     
            //
            // Rule 79:  VariableDeclaratorId ::= identifier
            //
            case 79: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new X10VarDeclarator(pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 80:  VariableDeclaratorId ::= VariableDeclaratorId [ ]
            //
            case 80: {
                X10VarDeclarator a = (X10VarDeclarator) btParser.getSym(1);
                a.dims++;
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 81:  VariableDeclaratorId ::= identifier [ IdentifierList ]
            //
            case 81: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                List paramList = (List) btParser.getSym(3);
                btParser.setSym1(new X10VarDeclarator(pos(), a.getIdentifier(), paramList));
                break;
            }
     
            //
            // Rule 82:  VariableDeclaratorId ::= [ IdentifierList ]
            //
            case 82: {
                String name = polyglot.ext.x10.visit.X10PrettyPrinterVisitor.getId();
                List paramList = (List) btParser.getSym(2);
                btParser.setSym1(new X10VarDeclarator(pos(), name, paramList));
                break;
            }
     
            //
            // Rule 83:  VariableInitializer ::= Expression
            //
            case 83:
                break;
 
            //
            // Rule 84:  VariableInitializer ::= ArrayInitializer
            //
            case 84:
                break;
 
            //
            // Rule 85:  FieldModifiers ::= FieldModifier
            //
            case 85:
                break;
 
            //
            // Rule 86:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 86: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 87:  FieldModifier ::= public
            //
            case 87: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 88:  FieldModifier ::= protected
            //
            case 88: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 89:  FieldModifier ::= private
            //
            case 89: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 90:  FieldModifier ::= static
            //
            case 90: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 91:  FieldModifier ::= final
            //
            case 91: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 92:  FieldModifier ::= transient
            //
            case 92: {
                btParser.setSym1(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 93:  FieldModifier ::= volatile
            //
            case 93: {
                btParser.setSym1(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 94:  MethodDeclaration ::= MethodHeader MethodBody
            //
            case 94: {
                MethodDecl a = (MethodDecl) btParser.getSym(1);
                List l = a.formals();
                List s = new TypedList(new LinkedList(), Stmt.class, false);
                Block b = (Block) btParser.getSym(2);
                
                for (Iterator i = l.iterator(); i.hasNext(); ) {
                   X10Formal d = (X10Formal) i.next();
                   if (d.hasExplodedVars())
                     s.addAll( d.explode());
                }
                if (! s.isEmpty()) {
                  s.addAll(b.statements());
                  b = b.statements(s);
                }
                Flags f = a.flags();
                if (f.contains(Flags.ATOMIC)) {
                   List ss = new TypedList(new LinkedList(), Stmt.class, false);
                   ss.add(nf.Atomic(pos(), nf.Here(pos()), b));
                   b = b.statements(ss);
                   a = a.flags(f.clear(Flags.ATOMIC));
                }
                btParser.setSym1(a.body(b));
                break;
            }
     
            //
            // Rule 95:  MethodHeader ::= MethodModifiersopt ResultType MethodDeclarator Throwsopt
            //
            case 95: {
                Flags a = (Flags) btParser.getSym(1);
                TypeNode b = (TypeNode) btParser.getSym(2);
                Object[] o = (Object []) btParser.getSym(3);
                    Name c = (Name) o[0];
                    List d = (List) o[1];
                    Integer e = (Integer) o[2];
                List f = (List) btParser.getSym(4);

                if (b.type() == ts.Void() && e.intValue() > 0)
                {
                    // TODO: error!!!
                    assert(false);
                }

                btParser.setSym1(nf.MethodDecl(pos(btParser.getFirstToken(2), btParser.getLastToken(3)),
                                       a,
                                       nf.array((TypeNode) b, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), e.intValue()),
                                       c.toString(),
                                       d,
                                       f,
                                       null));
                break;
            }
     
            //
            // Rule 96:  ResultType ::= Type
            //
            case 96:
                break;
 
            //
            // Rule 97:  ResultType ::= void
            //
            case 97: {
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 98:  MethodDeclarator ::= identifier ( FormalParameterListopt )
            //
            case 98: {
                Object[] a = new Object[3];
                a[0] =  new Name(nf, ts, pos(), id(btParser.getToken(1)).getIdentifier());
                a[1] = btParser.getSym(3);
                a[2] = new Integer(0);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 99:  MethodDeclarator ::= MethodDeclarator [ ]
            //
            case 99: {
                Object[] a = (Object []) btParser.getSym(1);
                a[2] = new Integer(((Integer) a[2]).intValue() + 1);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 100:  FormalParameterList ::= LastFormalParameter
            //
            case 100: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 101:  FormalParameterList ::= FormalParameters , LastFormalParameter
            //
            case 101: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 102:  FormalParameters ::= FormalParameter
            //
            case 102: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 103:  FormalParameters ::= FormalParameters , FormalParameter
            //
            case 103: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 104:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 104: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                X10VarDeclarator b = (X10VarDeclarator) btParser.getSym(3);
                b.setFlag(f);
                btParser.setSym1(nf.Formal(pos(), nf.array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b));
                break;
            }
     
            //
            // Rule 106:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 106: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 107:  VariableModifier ::= final
            //
            case 107: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 108:  LastFormalParameter ::= VariableModifiersopt Type ...opt VariableDeclaratorId
            //
            case 108: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                assert(btParser.getSym(3) == null);
                X10VarDeclarator b = (X10VarDeclarator) btParser.getSym(4);
                b.setFlag(f);
                btParser.setSym1(nf.Formal(pos(), nf.array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b));
                break;
            }
     
            //
            // Rule 109:  MethodModifiers ::= MethodModifier
            //
            case 109:
                break;
 
            //
            // Rule 110:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 110: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 111:  MethodModifier ::= public
            //
            case 111: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 112:  MethodModifier ::= protected
            //
            case 112: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 113:  MethodModifier ::= private
            //
            case 113: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 114:  MethodModifier ::= abstract
            //
            case 114: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 115:  MethodModifier ::= static
            //
            case 115: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 116:  MethodModifier ::= final
            //
            case 116: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 117:  MethodModifier ::= synchronized
            //
            case 117: {
                btParser.setSym1(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 118:  MethodModifier ::= native
            //
            case 118: {
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 119:  MethodModifier ::= strictfp
            //
            case 119: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 120:  Throws ::= throws ExceptionTypeList
            //
            case 120: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 121:  ExceptionTypeList ::= ExceptionType
            //
            case 121: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 122:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 122: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 123:  ExceptionType ::= ClassType
            //
            case 123:
                break;
 
            //
            // Rule 124:  MethodBody ::= Block
            //
            case 124:
                break;
 
            //
            // Rule 125:  MethodBody ::= ;
            //
            case 125:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 126:  InstanceInitializer ::= Block
            //
            case 126:
                break;
 
            //
            // Rule 127:  StaticInitializer ::= static Block
            //
            case 127: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 128:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 128: {
                Flags m = (Flags) btParser.getSym(1);
                Object[] o = (Object []) btParser.getSym(2);
                    Name a = (Name) o[1];
                    List b = (List) o[2];
                List c = (List) btParser.getSym(3);
                Block d = (Block) btParser.getSym(4);

                btParser.setSym1(nf.ConstructorDecl(pos(), m, a.toString(), b, c, d));
                break;
            }
     
            //
            // Rule 129:  ConstructorDeclarator ::= SimpleTypeName ( FormalParameterListopt )
            //
            case 129: {
                Object[] a = new Object[3];
                a[1] = btParser.getSym(1);
                a[2] = btParser.getSym(3);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 130:  SimpleTypeName ::= identifier
            //
            case 130: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 131:  ConstructorModifiers ::= ConstructorModifier
            //
            case 131:
                break;
 
            //
            // Rule 132:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 132: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 133:  ConstructorModifier ::= public
            //
            case 133: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 134:  ConstructorModifier ::= protected
            //
            case 134: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 135:  ConstructorModifier ::= private
            //
            case 135: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 136:  ConstructorBody ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 136: {
                Stmt a = (Stmt) btParser.getSym(2);
                List l;
                if (a == null)
                    l = (List) btParser.getSym(3);
                else
                {
                    l = new TypedList(new LinkedList(), Stmt.class, false);
                     List l2 = (List) btParser.getSym(3);
                    l.add(a);
                    l.addAll(l2);
                }
                btParser.setSym1(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 137:  ExplicitConstructorInvocation ::= this ( ArgumentListopt ) ;
            //
            case 137: {
//vj                    assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.ThisCall(pos(), b));
                break;
            }
     
            //
            // Rule 138:  ExplicitConstructorInvocation ::= super ( ArgumentListopt ) ;
            //
            case 138: {
//vj                    assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.SuperCall(pos(), b));
                break;
            }
     
            //
            // Rule 139:  ExplicitConstructorInvocation ::= Primary . this ( ArgumentListopt ) ;
            //
            case 139: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.ThisCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 140:  ExplicitConstructorInvocation ::= Primary . super ( ArgumentListopt ) ;
            //
            case 140: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.SuperCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 141:  Arguments ::= ( ArgumentListopt )
            //
            case 141: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 142:  InterfaceDeclaration ::= NormalInterfaceDeclaration
            //
            case 142:
                break;
 
            //
            // Rule 143:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier ExtendsInterfacesopt InterfaceBody
            //
            case 143: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(4);
                ClassBody d = (ClassBody) btParser.getSym(5);
                btParser.setSym1(nf.ClassDecl(pos(), a.Interface(), b.getIdentifier(), null, c, d));
                break;
            }
     
            //
            // Rule 144:  InterfaceModifiers ::= InterfaceModifier
            //
            case 144:
                break;
 
            //
            // Rule 145:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 145: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 146:  InterfaceModifier ::= public
            //
            case 146: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 147:  InterfaceModifier ::= protected
            //
            case 147: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 148:  InterfaceModifier ::= private
            //
            case 148: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 149:  InterfaceModifier ::= abstract
            //
            case 149: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 150:  InterfaceModifier ::= static
            //
            case 150: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 151:  InterfaceModifier ::= strictfp
            //
            case 151: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 152:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 152: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 153:  ExtendsInterfaces ::= ExtendsInterfaces , InterfaceType
            //
            case 153: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 154:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 154: {
                List a = (List)btParser.getSym(2);
                btParser.setSym1(nf.ClassBody(pos(), a));
                break;
            }
     
            //
            // Rule 155:  InterfaceMemberDeclarations ::= InterfaceMemberDeclaration
            //
            case 155:
                break;
 
            //
            // Rule 156:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 156: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 157:  InterfaceMemberDeclaration ::= ConstantDeclaration
            //
            case 157:
                break;
 
            //
            // Rule 158:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 158: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 159:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 159: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 160:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 160: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 161:  InterfaceMemberDeclaration ::= ;
            //
            case 161: {
                btParser.setSym1(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 162:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 162: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Flags a = (Flags) btParser.getSym(1);
                TypeNode b = (TypeNode) btParser.getSym(2);
                List c = (List) btParser.getSym(3);
                for (Iterator i = c.iterator(); i.hasNext();)
                {
                    X10VarDeclarator d = (X10VarDeclarator) i.next();
                    if (d.hasExplodedVars())
                      // TODO: Report this exception correctly.
                      throw new Error("Field Declarations may not have exploded variables." + pos());
                    l.add(nf.FieldDecl(pos(btParser.getFirstToken(2), btParser.getLastToken()),
                                       a,
                                       nf.array(b, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), d.dims),
                                       d.name,
                                       d.init));
                }
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 163:  ConstantModifiers ::= ConstantModifier
            //
            case 163:
                break;
 
            //
            // Rule 164:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 164: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 165:  ConstantModifier ::= public
            //
            case 165: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 166:  ConstantModifier ::= static
            //
            case 166: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 167:  ConstantModifier ::= final
            //
            case 167: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 168:  AbstractMethodDeclaration ::= AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt ;
            //
            case 168: {
                Flags a = (Flags) btParser.getSym(1);
                TypeNode b = (TypeNode) btParser.getSym(2);
                Object[] o = (Object []) btParser.getSym(3);
                    Name c = (Name) o[0];
                    List d = (List) o[1];
                    Integer e = (Integer) o[2];
                List f = (List) btParser.getSym(4);

                if (b.type() == ts.Void() && e.intValue() > 0)
                {
                    // TODO: error!!!
                    assert(false);
                }

                btParser.setSym1(nf.MethodDecl(pos(btParser.getFirstToken(2), btParser.getLastToken(3)),
                                       a,
                                       nf.array((TypeNode) b, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), e.intValue()),
                                       c.toString(),
                                       d,
                                       f,
                                       null));
                break;
            }
     
            //
            // Rule 169:  AbstractMethodModifiers ::= AbstractMethodModifier
            //
            case 169:
                break;
 
            //
            // Rule 170:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 170: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 171:  AbstractMethodModifier ::= public
            //
            case 171: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 172:  AbstractMethodModifier ::= abstract
            //
            case 172: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 173:  ArrayInitializer ::= { VariableInitializersopt ,opt }
            //
            case 173: {
                List a = (List) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.ArrayInit(pos()));
                else btParser.setSym1(nf.ArrayInit(pos(), a));
                break;
            }
     
            //
            // Rule 174:  VariableInitializers ::= VariableInitializer
            //
            case 174: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 175:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 175: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 176:  Block ::= { BlockStatementsopt }
            //
            case 176: {
                List l = (List) btParser.getSym(2);
                btParser.setSym1(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 177:  BlockStatements ::= BlockStatement
            //
            case 177: {
                List l = new TypedList(new LinkedList(), Stmt.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 178:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 178: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 179:  BlockStatement ::= LocalVariableDeclarationStatement
            //
            case 179:
                break;
 
            //
            // Rule 180:  BlockStatement ::= ClassDeclaration
            //
            case 180: {
                ClassDecl a = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 181:  BlockStatement ::= Statement
            //
            case 181: {
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 182:  LocalVariableDeclarationStatement ::= LocalVariableDeclaration ;
            //
            case 182:
                break;
 
            //
            // Rule 183:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 183: {
                Flags flags = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                List b = (List) btParser.getSym(3);

                List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                List s = new TypedList(new LinkedList(), Stmt.class, false);
                for (Iterator i = b.iterator(); i.hasNext(); )
                {
                    X10VarDeclarator d = (X10VarDeclarator) i.next();
                    d.setFlag( flags ); 
                    // use d.flags below and not flags, setFlag may change it.
                    l.add(nf.LocalDecl(d.pos,  d.flags,
                                       nf.array(a, pos(d), d.dims), d.name, d.init));
                    if (d.hasExplodedVars())
                       s.addAll( d.explode() );
                }
                l.addAll(s); 
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 184:  Statement ::= StatementWithoutTrailingSubstatement
            //
            case 184:
                break;
 
            //
            // Rule 185:  Statement ::= LabeledStatement
            //
            case 185:
                break;
 
            //
            // Rule 186:  Statement ::= IfThenStatement
            //
            case 186:
                break;
 
            //
            // Rule 187:  Statement ::= IfThenElseStatement
            //
            case 187:
                break;
 
            //
            // Rule 188:  Statement ::= WhileStatement
            //
            case 188:
                break;
 
            //
            // Rule 189:  Statement ::= ForStatement
            //
            case 189:
                break;
 
            //
            // Rule 190:  StatementWithoutTrailingSubstatement ::= Block
            //
            case 190:
                break;
 
            //
            // Rule 191:  StatementWithoutTrailingSubstatement ::= EmptyStatement
            //
            case 191:
                break;
 
            //
            // Rule 192:  StatementWithoutTrailingSubstatement ::= ExpressionStatement
            //
            case 192:
                break;
 
            //
            // Rule 193:  StatementWithoutTrailingSubstatement ::= AssertStatement
            //
            case 193:
                break;
 
            //
            // Rule 194:  StatementWithoutTrailingSubstatement ::= SwitchStatement
            //
            case 194:
                break;
 
            //
            // Rule 195:  StatementWithoutTrailingSubstatement ::= DoStatement
            //
            case 195:
                break;
 
            //
            // Rule 196:  StatementWithoutTrailingSubstatement ::= BreakStatement
            //
            case 196:
                break;
 
            //
            // Rule 197:  StatementWithoutTrailingSubstatement ::= ContinueStatement
            //
            case 197:
                break;
 
            //
            // Rule 198:  StatementWithoutTrailingSubstatement ::= ReturnStatement
            //
            case 198:
                break;
 
            //
            // Rule 199:  StatementWithoutTrailingSubstatement ::= SynchronizedStatement
            //
            case 199:
                break;
 
            //
            // Rule 200:  StatementWithoutTrailingSubstatement ::= ThrowStatement
            //
            case 200:
                break;
 
            //
            // Rule 201:  StatementWithoutTrailingSubstatement ::= TryStatement
            //
            case 201:
                break;
 
            //
            // Rule 202:  StatementNoShortIf ::= StatementWithoutTrailingSubstatement
            //
            case 202:
                break;
 
            //
            // Rule 203:  StatementNoShortIf ::= LabeledStatementNoShortIf
            //
            case 203:
                break;
 
            //
            // Rule 204:  StatementNoShortIf ::= IfThenElseStatementNoShortIf
            //
            case 204:
                break;
 
            //
            // Rule 205:  StatementNoShortIf ::= WhileStatementNoShortIf
            //
            case 205:
                break;
 
            //
            // Rule 206:  StatementNoShortIf ::= ForStatementNoShortIf
            //
            case 206:
                break;
 
            //
            // Rule 207:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 207: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.If(pos(), a, b));
                break;
            }
     
            //
            // Rule 208:  IfThenElseStatement ::= if ( Expression ) StatementNoShortIf else Statement
            //
            case 208: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 209:  IfThenElseStatementNoShortIf ::= if ( Expression ) StatementNoShortIf else StatementNoShortIf
            //
            case 209: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 210:  EmptyStatement ::= ;
            //
            case 210: {
                btParser.setSym1(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 211:  LabeledStatement ::= identifier : Statement
            //
            case 211: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 212:  LabeledStatementNoShortIf ::= identifier : StatementNoShortIf
            //
            case 212: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 213:  ExpressionStatement ::= StatementExpression ;
            //
            case 213: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Eval(pos(), a));
                break;
            }
     
            //
            // Rule 214:  StatementExpression ::= Assignment
            //
            case 214:
                break;
 
            //
            // Rule 215:  StatementExpression ::= PreIncrementExpression
            //
            case 215:
                break;
 
            //
            // Rule 216:  StatementExpression ::= PreDecrementExpression
            //
            case 216:
                break;
 
            //
            // Rule 217:  StatementExpression ::= PostIncrementExpression
            //
            case 217:
                break;
 
            //
            // Rule 218:  StatementExpression ::= PostDecrementExpression
            //
            case 218:
                break;
 
            //
            // Rule 219:  StatementExpression ::= MethodInvocation
            //
            case 219:
                break;
 
            //
            // Rule 220:  StatementExpression ::= ClassInstanceCreationExpression
            //
            case 220:
                break;
 
            //
            // Rule 221:  AssertStatement ::= assert Expression ;
            //
            case 221: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Assert(pos(), a));
                break;
            }
     
            //
            // Rule 222:  AssertStatement ::= assert Expression : Expression ;
            //
            case 222: {
                Expr a = (Expr) btParser.getSym(2),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Assert(pos(), a, b));
                break;
            }
     
            //
            // Rule 223:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 223: {
                Expr a = (Expr) btParser.getSym(3);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.Switch(pos(), a, b));
                break;
            }
     
            //
            // Rule 224:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 224: {
                List l = (List) btParser.getSym(2),
                     l2 = (List) btParser.getSym(3);
                l.addAll(l2);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 225:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroup
            //
            case 225:
                break;
 
            //
            // Rule 226:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 226: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 227:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 227: {
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);

                List l1 = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l1);
                l.add(nf.SwitchBlock(pos(), l2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 228:  SwitchLabels ::= SwitchLabel
            //
            case 228: {
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 229:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 229: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 230:  SwitchLabel ::= case ConstantExpression :
            //
            case 230: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Case(pos(), a));
                break;
            }
     
            //
            // Rule 231:  SwitchLabel ::= default :
            //
            case 231: {
                btParser.setSym1(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 232:  WhileStatement ::= while ( Expression ) Statement
            //
            case 232: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 233:  WhileStatementNoShortIf ::= while ( Expression ) StatementNoShortIf
            //
            case 233: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 234:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 234: {
                Stmt a = (Stmt) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Do(pos(), a, b));
                break;
            }
     
            //
            // Rule 235:  ForStatement ::= BasicForStatement
            //
            case 235:
                break;
 
            //
            // Rule 236:  ForStatement ::= EnhancedForStatement
            //
            case 236:
                break;
 
            //
            // Rule 237:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 237: {
                List a = (List) btParser.getSym(3);
                Expr b = (Expr) btParser.getSym(5);
                List c = (List) btParser.getSym(7);
                Stmt d = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), a, b, c, d));
                break;
            }
     
            //
            // Rule 238:  ForStatementNoShortIf ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) StatementNoShortIf
            //
            case 238: {
                List a = (List) btParser.getSym(3);
                Expr b = (Expr) btParser.getSym(5);
                List c = (List) btParser.getSym(7);
                Stmt d = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), a, b, c, d));
                break;
            }
     
            //
            // Rule 239:  ForStatementNoShortIf ::= EnhancedForStatementNoShortIf
            //
            case 239:
                break;
 
            //
            // Rule 240:  ForInit ::= StatementExpressionList
            //
            case 240:
                break;
 
            //
            // Rule 241:  ForInit ::= LocalVariableDeclaration
            //
            case 241: {
                List l = new TypedList(new LinkedList(), ForInit.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 242:  ForUpdate ::= StatementExpressionList
            //
            case 242:
                break;
 
            //
            // Rule 243:  StatementExpressionList ::= StatementExpression
            //
            case 243: {
                List l = new TypedList(new LinkedList(), Eval.class, false);
                Expr a = (Expr) btParser.getSym(1);
                l.add(nf.Eval(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 244:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 244: {
                List l = (List) btParser.getSym(1);
                Expr a = (Expr) btParser.getSym(3);
                l.add(nf.Eval(pos(), a));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 245:  BreakStatement ::= break identifieropt ;
            //
            case 245: {
                Name a = (Name) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.Break(pos()));
                else btParser.setSym1(nf.Break(pos(), a.toString()));
                break;
            }
     
            //
            // Rule 246:  ContinueStatement ::= continue identifieropt ;
            //
            case 246: {
                Name a = (Name) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.Continue(pos()));
                else btParser.setSym1(nf.Continue(pos(), a.toString()));
                break;
            }
     
            //
            // Rule 247:  ReturnStatement ::= return Expressionopt ;
            //
            case 247: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Return(pos(), a));
                break;
            }
     
            //
            // Rule 248:  ThrowStatement ::= throw Expression ;
            //
            case 248: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Throw(pos(), a));
                break;
            }
     
            //
            // Rule 249:  SynchronizedStatement ::= synchronized ( Expression ) Block
            //
            case 249: {
                Expr a = (Expr) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Synchronized(pos(), a, b));
                break;
            }
     
            //
            // Rule 250:  TryStatement ::= try Block Catches
            //
            case 250: {
                Block a = (Block) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Try(pos(), a, b));
                break;
            }
     
            //
            // Rule 251:  TryStatement ::= try Block Catchesopt Finally
            //
            case 251: {
                Block a = (Block) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                Block c = (Block) btParser.getSym(4);
                btParser.setSym1(nf.Try(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 252:  Catches ::= CatchClause
            //
            case 252: {
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 253:  Catches ::= Catches CatchClause
            //
            case 253: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 254:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 254: {
                Formal a = (Formal) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Catch(pos(), a, b));
                break;
            }
     
            //
            // Rule 255:  Finally ::= finally Block
            //
            case 255: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 256:  Primary ::= PrimaryNoNewArray
            //
            case 256:
                break;
 
            //
            // Rule 257:  Primary ::= ArrayCreationExpression
            //
            case 257:
                break;
 
            //
            // Rule 258:  PrimaryNoNewArray ::= Literal
            //
            case 258:
                break;
 
            //
            // Rule 259:  PrimaryNoNewArray ::= Type . class
            //
            case 259: {
                Object o = btParser.getSym(1);
                if (o instanceof Name)
                {
                    Name a = (Name) o;
                    btParser.setSym1(nf.ClassLit(pos(), a.toType()));
                }
                else if (o instanceof TypeNode)
                {
                    TypeNode a = (TypeNode) o;
                    btParser.setSym1(nf.ClassLit(pos(), a));
                }
                else if (o instanceof CanonicalTypeNode)
                {
                    CanonicalTypeNode a = (CanonicalTypeNode) o;
                    btParser.setSym1(nf.ClassLit(pos(), a));
                }
                else assert(false);
                break;
            }
     
            //
            // Rule 260:  PrimaryNoNewArray ::= void . class
            //
            case 260: {
                btParser.setSym1(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(btParser.getToken(1)), ts.Void())));
                break;
            }
     
            //
            // Rule 261:  PrimaryNoNewArray ::= this
            //
            case 261: {
                btParser.setSym1(nf.This(pos()));
                break;
            }
     
            //
            // Rule 262:  PrimaryNoNewArray ::= ClassName . this
            //
            case 262: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(nf.This(pos(), a.toType()));
                break;
            }
     
            //
            // Rule 263:  PrimaryNoNewArray ::= ( Expression )
            //
            case 263: {
                Expr e = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.ParExpr(pos(), e));
                break;
            }
     
            //
            // Rule 264:  PrimaryNoNewArray ::= ClassInstanceCreationExpression
            //
            case 264:
                break;
 
            //
            // Rule 265:  PrimaryNoNewArray ::= FieldAccess
            //
            case 265:
                break;
 
            //
            // Rule 266:  PrimaryNoNewArray ::= MethodInvocation
            //
            case 266:
                break;
 
            //
            // Rule 267:  PrimaryNoNewArray ::= ArrayAccess
            //
            case 267:
                break;
 
            //
            // Rule 268:  Literal ::= IntegerLiteral
            //
            case 268: {
                polyglot.lex.IntegerLiteral a = int_lit(btParser.getToken(1));
                btParser.setSym1(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 269:  Literal ::= LongLiteral
            //
            case 269: {
                polyglot.lex.LongLiteral a = long_lit(btParser.getToken(1));
                btParser.setSym1(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 270:  Literal ::= FloatingPointLiteral
            //
            case 270: {
                polyglot.lex.FloatLiteral a = float_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 271:  Literal ::= DoubleLiteral
            //
            case 271: {
                polyglot.lex.DoubleLiteral a = double_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 272:  Literal ::= BooleanLiteral
            //
            case 272: {
                polyglot.lex.BooleanLiteral a = boolean_lit(btParser.getToken(1));
                btParser.setSym1(nf.BooleanLit(pos(), a.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 273:  Literal ::= CharacterLiteral
            //
            case 273: {
                polyglot.lex.CharacterLiteral a = char_lit(btParser.getToken(1));
                btParser.setSym1(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 274:  Literal ::= StringLiteral
            //
            case 274: {
                polyglot.lex.StringLiteral a = string_lit(btParser.getToken(1));
                btParser.setSym1(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 275:  Literal ::= null
            //
            case 275: {
                btParser.setSym1(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 276:  BooleanLiteral ::= true
            //
            case 276:
                break;
 
            //
            // Rule 277:  BooleanLiteral ::= false
            //
            case 277:
                break;
 
            //
            // Rule 278:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
            //
            case 278: {
//vj                    assert(btParser.getSym(2) == null);
                TypeNode a = (TypeNode) btParser.getSym(2);
//vj                    assert(btParser.getSym(4) == null);
                List b = (List) btParser.getSym(4);
                ClassBody c = (ClassBody) btParser.getSym(6);
                if (c == null)
                     btParser.setSym1(nf.New(pos(), a, b));
                else btParser.setSym1(nf.New(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 279:  ClassInstanceCreationExpression ::= Primary . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 279: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                Name b = new Name(nf, ts, pos(), id(btParser.getToken(4)).getIdentifier());
//vj                    assert(btParser.getSym(4) == null);
                List c = (List) btParser.getSym(6);
                ClassBody d = (ClassBody) btParser.getSym(8);
                if (d == null)
                     btParser.setSym1(nf.New(pos(), a, b.toType(), c));
                else btParser.setSym1(nf.New(pos(), a, b.toType(), c, d));
                break;
            }
     
            //
            // Rule 280:  ClassInstanceCreationExpression ::= AmbiguousName . new identifier ( ArgumentListopt ) ClassBodyopt
            //
            case 280: {
                Name a = (Name) btParser.getSym(1);
//vj                    assert(btParser.getSym(4) == null);
                Name b = new Name(nf, ts, pos(), id(btParser.getToken(4)).getIdentifier());
//vj                    assert(btParser.getSym(6) == null);
                List c = (List) btParser.getSym(6);
                ClassBody d = (ClassBody) btParser.getSym(8);
                if (d == null)
                     btParser.setSym1(nf.New(pos(), a.toExpr(), b.toType(), c));
                else btParser.setSym1(nf.New(pos(), a.toExpr(), b.toType(), c, d));
                break;
            }
     
            //
            // Rule 281:  ArgumentList ::= Expression
            //
            case 281: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 282:  ArgumentList ::= ArgumentList , Expression
            //
            case 282: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 283:  FieldAccess ::= Primary . identifier
            //
            case 283: {
                Expr a = (Expr) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(), a, b.getIdentifier()));
                break;
            }
     
            //
            // Rule 284:  FieldAccess ::= super . identifier
            //
            case 284: {
                polyglot.lex.Identifier a = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken())), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 285:  FieldAccess ::= ClassName . super . identifier
            //
            case 285: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier()));
                break;
            }
     
            //
            // Rule 286:  MethodInvocation ::= MethodName ( ArgumentListopt )
            //
            case 286: {
                Name a = (Name) btParser.getSym(1);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Call(pos(), a.prefix == null ? null : a.prefix.toReceiver(), a.name, b));
                break;
            }
     
            //
            // Rule 287:  MethodInvocation ::= Primary . identifier ( ArgumentListopt )
            //
            case 287: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), a, b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 288:  MethodInvocation ::= super . identifier ( ArgumentListopt )
            //
            case 288: {
//vj                    assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken())), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 289:  MethodInvocation ::= ClassName . super . identifier ( ArgumentListopt )
            //
            case 289: {
                Name a = (Name) btParser.getSym(1);
//vj                    assert(btParser.getSym(5) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(5));
                List c = (List) btParser.getSym(7);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 290:  PostfixExpression ::= Primary
            //
            case 290:
                break;
 
            //
            // Rule 291:  PostfixExpression ::= ExpressionName
            //
            case 291: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 292:  PostfixExpression ::= PostIncrementExpression
            //
            case 292:
                break;
 
            //
            // Rule 293:  PostfixExpression ::= PostDecrementExpression
            //
            case 293:
                break;
 
            //
            // Rule 294:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 294: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 295:  PostDecrementExpression ::= PostfixExpression --
            //
            case 295: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 296:  UnaryExpression ::= PreIncrementExpression
            //
            case 296:
                break;
 
            //
            // Rule 297:  UnaryExpression ::= PreDecrementExpression
            //
            case 297:
                break;
 
            //
            // Rule 298:  UnaryExpression ::= + UnaryExpression
            //
            case 298: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.POS, a));
                break;
            }
     
            //
            // Rule 299:  UnaryExpression ::= - UnaryExpression
            //
            case 299: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NEG, a));
                break;
            }
     
            //
            // Rule 301:  PreIncrementExpression ::= ++ UnaryExpression
            //
            case 301: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_INC, a));
                break;
            }
     
            //
            // Rule 302:  PreDecrementExpression ::= -- UnaryExpression
            //
            case 302: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_DEC, a));
                break;
            }
     
            //
            // Rule 303:  UnaryExpressionNotPlusMinus ::= PostfixExpression
            //
            case 303:
                break;
 
            //
            // Rule 304:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 304: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.BIT_NOT, a));
                break;
            }
     
            //
            // Rule 305:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 305: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NOT, a));
                break;
            }
     
            //
            // Rule 307:  MultiplicativeExpression ::= UnaryExpression
            //
            case 307:
                break;
 
            //
            // Rule 308:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 308: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MUL, b));
                break;
            }
     
            //
            // Rule 309:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 309: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.DIV, b));
                break;
            }
     
            //
            // Rule 310:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 310: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MOD, b));
                break;
            }
     
            //
            // Rule 311:  AdditiveExpression ::= MultiplicativeExpression
            //
            case 311:
                break;
 
            //
            // Rule 312:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 312: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.ADD, b));
                break;
            }
     
            //
            // Rule 313:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 313: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SUB, b));
                break;
            }
     
            //
            // Rule 314:  ShiftExpression ::= AdditiveExpression
            //
            case 314:
                break;
 
            //
            // Rule 315:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 315: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHL, b));
                break;
            }
     
            //
            // Rule 316:  ShiftExpression ::= ShiftExpression > > AdditiveExpression
            //
            case 316: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHR, b));
                break;
            }
     
            //
            // Rule 317:  ShiftExpression ::= ShiftExpression > > > AdditiveExpression
            //
            case 317: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Binary(pos(), a, Binary.USHR, b));
                break;
            }
     
            //
            // Rule 318:  RelationalExpression ::= ShiftExpression
            //
            case 318:
                break;
 
            //
            // Rule 319:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 319: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LT, b));
                break;
            }
     
            //
            // Rule 320:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 320: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GT, b));
                break;
            }
     
            //
            // Rule 321:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 321: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LE, b));
                break;
            }
     
            //
            // Rule 322:  RelationalExpression ::= RelationalExpression > = ShiftExpression
            //
            case 322: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GE, b));
                break;
            }
     
            //
            // Rule 323:  EqualityExpression ::= RelationalExpression
            //
            case 323:
                break;
 
            //
            // Rule 324:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 324: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.EQ, b));
                break;
            }
     
            //
            // Rule 325:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 325: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.NE, b));
                break;
            }
     
            //
            // Rule 326:  AndExpression ::= EqualityExpression
            //
            case 326:
                break;
 
            //
            // Rule 327:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 327: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_AND, b));
                break;
            }
     
            //
            // Rule 328:  ExclusiveOrExpression ::= AndExpression
            //
            case 328:
                break;
 
            //
            // Rule 329:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 329: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_XOR, b));
                break;
            }
     
            //
            // Rule 330:  InclusiveOrExpression ::= ExclusiveOrExpression
            //
            case 330:
                break;
 
            //
            // Rule 331:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 331: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_OR, b));
                break;
            }
     
            //
            // Rule 332:  ConditionalAndExpression ::= InclusiveOrExpression
            //
            case 332:
                break;
 
            //
            // Rule 333:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 333: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_AND, b));
                break;
            }
     
            //
            // Rule 334:  ConditionalOrExpression ::= ConditionalAndExpression
            //
            case 334:
                break;
 
            //
            // Rule 335:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 335: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_OR, b));
                break;
            }
     
            //
            // Rule 336:  ConditionalExpression ::= ConditionalOrExpression
            //
            case 336:
                break;
 
            //
            // Rule 337:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 337: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3),
                     c = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Conditional(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 338:  AssignmentExpression ::= ConditionalExpression
            //
            case 338:
                break;
 
            //
            // Rule 339:  AssignmentExpression ::= Assignment
            //
            case 339:
                break;
 
            //
            // Rule 340:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 340: {
                Expr a = (Expr) btParser.getSym(1);
                Assign.Operator b = (Assign.Operator) btParser.getSym(2);
                Expr c = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Assign(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 341:  LeftHandSide ::= ExpressionName
            //
            case 341: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 342:  LeftHandSide ::= FieldAccess
            //
            case 342:
                break;
 
            //
            // Rule 343:  LeftHandSide ::= ArrayAccess
            //
            case 343:
                break;
 
            //
            // Rule 344:  AssignmentOperator ::= =
            //
            case 344: {
                btParser.setSym1(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 345:  AssignmentOperator ::= *=
            //
            case 345: {
                btParser.setSym1(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 346:  AssignmentOperator ::= /=
            //
            case 346: {
                btParser.setSym1(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 347:  AssignmentOperator ::= %=
            //
            case 347: {
                btParser.setSym1(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 348:  AssignmentOperator ::= +=
            //
            case 348: {
                btParser.setSym1(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 349:  AssignmentOperator ::= -=
            //
            case 349: {
                btParser.setSym1(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 350:  AssignmentOperator ::= <<=
            //
            case 350: {
                btParser.setSym1(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 351:  AssignmentOperator ::= > > =
            //
            case 351: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 352:  AssignmentOperator ::= > > > =
            //
            case 352: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 353:  AssignmentOperator ::= &=
            //
            case 353: {
                btParser.setSym1(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 354:  AssignmentOperator ::= ^=
            //
            case 354: {
                btParser.setSym1(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 355:  AssignmentOperator ::= |=
            //
            case 355: {
                btParser.setSym1(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 356:  Expression ::= AssignmentExpression
            //
            case 356:
                break;
 
            //
            // Rule 357:  ConstantExpression ::= Expression
            //
            case 357:
                break;
 
            //
            // Rule 358:  Catchesopt ::= $Empty
            //
            case 358: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 359:  Catchesopt ::= Catches
            //
            case 359:
                break;
 
            //
            // Rule 360:  identifieropt ::= $Empty
            //
            case 360:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 361:  identifieropt ::= identifier
            //
            case 361: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 362:  ForUpdateopt ::= $Empty
            //
            case 362: {
                btParser.setSym1(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 363:  ForUpdateopt ::= ForUpdate
            //
            case 363:
                break;
 
            //
            // Rule 364:  Expressionopt ::= $Empty
            //
            case 364:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 365:  Expressionopt ::= Expression
            //
            case 365:
                break;
 
            //
            // Rule 366:  ForInitopt ::= $Empty
            //
            case 366: {
                btParser.setSym1(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 367:  ForInitopt ::= ForInit
            //
            case 367:
                break;
 
            //
            // Rule 368:  SwitchLabelsopt ::= $Empty
            //
            case 368: {
                btParser.setSym1(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 369:  SwitchLabelsopt ::= SwitchLabels
            //
            case 369:
                break;
 
            //
            // Rule 370:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 370: {
                btParser.setSym1(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 371:  SwitchBlockStatementGroupsopt ::= SwitchBlockStatementGroups
            //
            case 371:
                break;
 
            //
            // Rule 372:  VariableModifiersopt ::= $Empty
            //
            case 372: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 373:  VariableModifiersopt ::= VariableModifiers
            //
            case 373:
                break;
 
            //
            // Rule 374:  VariableInitializersopt ::= $Empty
            //
            case 374:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 375:  VariableInitializersopt ::= VariableInitializers
            //
            case 375:
                break;
 
            //
            // Rule 376:  AbstractMethodModifiersopt ::= $Empty
            //
            case 376: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 377:  AbstractMethodModifiersopt ::= AbstractMethodModifiers
            //
            case 377:
                break;
 
            //
            // Rule 378:  ConstantModifiersopt ::= $Empty
            //
            case 378: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 379:  ConstantModifiersopt ::= ConstantModifiers
            //
            case 379:
                break;
 
            //
            // Rule 380:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 380: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 381:  InterfaceMemberDeclarationsopt ::= InterfaceMemberDeclarations
            //
            case 381:
                break;
 
            //
            // Rule 382:  ExtendsInterfacesopt ::= $Empty
            //
            case 382: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 383:  ExtendsInterfacesopt ::= ExtendsInterfaces
            //
            case 383:
                break;
 
            //
            // Rule 384:  InterfaceModifiersopt ::= $Empty
            //
            case 384: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 385:  InterfaceModifiersopt ::= InterfaceModifiers
            //
            case 385:
                break;
 
            //
            // Rule 386:  ClassBodyopt ::= $Empty
            //
            case 386:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 387:  ClassBodyopt ::= ClassBody
            //
            case 387:
                break;
 
            //
            // Rule 388:  ,opt ::= $Empty
            //
            case 388:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 389:  ,opt ::= ,
            //
            case 389:
                break;
 
            //
            // Rule 390:  ArgumentListopt ::= $Empty
            //
            case 390: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 391:  ArgumentListopt ::= ArgumentList
            //
            case 391:
                break;
 
            //
            // Rule 392:  BlockStatementsopt ::= $Empty
            //
            case 392: {
                btParser.setSym1(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 393:  BlockStatementsopt ::= BlockStatements
            //
            case 393:
                break;
 
            //
            // Rule 394:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 394:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 395:  ExplicitConstructorInvocationopt ::= ExplicitConstructorInvocation
            //
            case 395:
                break;
 
            //
            // Rule 396:  ConstructorModifiersopt ::= $Empty
            //
            case 396: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 397:  ConstructorModifiersopt ::= ConstructorModifiers
            //
            case 397:
                break;
 
            //
            // Rule 398:  ...opt ::= $Empty
            //
            case 398:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 399:  ...opt ::= ...
            //
            case 399:
                break;
 
            //
            // Rule 400:  FormalParameterListopt ::= $Empty
            //
            case 400: {
                btParser.setSym1(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 401:  FormalParameterListopt ::= FormalParameterList
            //
            case 401:
                break;
 
            //
            // Rule 402:  Throwsopt ::= $Empty
            //
            case 402: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 403:  Throwsopt ::= Throws
            //
            case 403:
                break;
 
            //
            // Rule 404:  MethodModifiersopt ::= $Empty
            //
            case 404: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 405:  MethodModifiersopt ::= MethodModifiers
            //
            case 405:
                break;
 
            //
            // Rule 406:  FieldModifiersopt ::= $Empty
            //
            case 406: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 407:  FieldModifiersopt ::= FieldModifiers
            //
            case 407:
                break;
 
            //
            // Rule 408:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 408: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 409:  ClassBodyDeclarationsopt ::= ClassBodyDeclarations
            //
            case 409:
                break;
 
            //
            // Rule 410:  Interfacesopt ::= $Empty
            //
            case 410: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 411:  Interfacesopt ::= Interfaces
            //
            case 411:
                break;
 
            //
            // Rule 412:  Superopt ::= $Empty
            //
            case 412:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 413:  Superopt ::= Super
            //
            case 413:
                break;
 
            //
            // Rule 414:  ClassModifiersopt ::= $Empty
            //
            case 414: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 415:  ClassModifiersopt ::= ClassModifiers
            //
            case 415:
                break;
 
            //
            // Rule 416:  TypeDeclarationsopt ::= $Empty
            //
            case 416: {
                btParser.setSym1(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 417:  TypeDeclarationsopt ::= TypeDeclarations
            //
            case 417:
                break;
 
            //
            // Rule 418:  ImportDeclarationsopt ::= $Empty
            //
            case 418: {
                btParser.setSym1(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 419:  ImportDeclarationsopt ::= ImportDeclarations
            //
            case 419:
                break;
 
            //
            // Rule 420:  PackageDeclarationopt ::= $Empty
            //
            case 420:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 421:  PackageDeclarationopt ::= PackageDeclaration
            //
            case 421:
                break;
 
            //
            // Rule 422:  Type ::= DataType PlaceTypeSpecifieropt
            //
            case 422: {
           // Just parse the placetype and drop it for now.
                    break;
            }
         
            //
            // Rule 423:  Type ::= nullable Type
            //
            case 423: {
             TypeNode a = (TypeNode) btParser.getSym(2);
             btParser.setSym1(nf.Nullable(pos(), a));
                       break;
            }
           
            //
            // Rule 424:  Type ::= future < Type >
            //
            case 424: {
              TypeNode a = (TypeNode) btParser.getSym(3);
              btParser.setSym1(nf.Future(pos(), a));
                        break;
            }
           
            //
            // Rule 425:  DataType ::= PrimitiveType
            //
            case 425:
                break; 
            
               
            //
            // Rule 426:  DataType ::= ClassOrInterfaceType
            //
            case 426:
                break; 
            
               
            //
            // Rule 427:  DataType ::= ArrayType
            //
            case 427:
                break; 
             
            //
            // Rule 428:  PlaceTypeSpecifier ::= @ PlaceType
            //
            case 428:
                break; 
 
            //
            // Rule 429:  PlaceType ::= placelocal
            //
            case 429:
                break; 
 
            //
            // Rule 430:  PlaceType ::= activitylocal
            //
            case 430:
                break; 
 
            //
            // Rule 431:  PlaceType ::= current
            //
            case 431:
                break; 
 
            //
            // Rule 432:  PlaceType ::= PlaceExpression
            //
            case 432:
                break; 
 
            //
            // Rule 433:  ClassOrInterfaceType ::= TypeName DepParametersopt
            //
            case 433: { 
            Name a = (Name) btParser.getSym(1);
            TypeNode t = a.toType();
            DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
            btParser.setSym1(b == null ? t : nf.ParametricTypeNode(pos(), t, null, b));
                    break;
            }
        
            //
            // Rule 434:  DepParameters ::= ( DepParameterExpr )
            //
            case 434:
                break; 
        
            //
            // Rule 435:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 435: {
             List a = (List) btParser.getSym(1);                           
             Expr b = (Expr) btParser.getSym(2);
             btParser.setSym1(nf.DepParameterExpr(pos(),a,b));
                    break;
            }
        
            //
            // Rule 436:  DepParameterExpr ::= WhereClause
            //
            case 436: {
             Expr b = (Expr) btParser.getSym(1);
             btParser.setSym1(nf.DepParameterExpr(pos(), null, b));
                    break;
            }
        
            //
            // Rule 437:  WhereClause ::= : Expression
            //
            case 437:
                break; 
 
            //
            // Rule 439:  X10ArrayType ::= Type [ . ]
            //
            case 439: {
                TypeNode a = (TypeNode) btParser.getSym(1);
                TypeNode t = nf.X10ArrayTypeNode(pos(), a, false, null);
                btParser.setSym1(t);
                break;
            }
     
            //
            // Rule 440:  X10ArrayType ::= Type reference [ . ]
            //
            case 440: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, false, null));
                    break;
            }
        
            //
            // Rule 441:  X10ArrayType ::= Type value [ . ]
            //
            case 441: {
             TypeNode a = (TypeNode) btParser.getSym(1);
                btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, true, null));
                    break;
            }
        
            //
            // Rule 442:  X10ArrayType ::= Type [ DepParameterExpr ]
            //
            case 442: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, false, b));
                    break;
            }
        
            //
            // Rule 443:  X10ArrayType ::= Type reference [ DepParameterExpr ]
            //
            case 443: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, false, b));
                    break;
            }
        
            //
            // Rule 444:  X10ArrayType ::= Type value [ DepParameterExpr ]
            //
            case 444: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, true, b));
                    break;
            }
        
            //
            // Rule 445:  MethodModifier ::= atomic
            //
            case 445: {
                btParser.setSym1(Flags.ATOMIC);
                break;
            }
     
            //
            // Rule 446:  MethodModifier ::= extern
            //
            case 446: {
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 447:  ClassDeclaration ::= ValueClassDeclaration
            //
            case 447:
                break; 
 
            //
            // Rule 448:  ValueClassDeclaration ::= ClassModifiersopt value identifier Superopt Interfacesopt ClassBody
            //
            case 448: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                TypeNode c = (TypeNode) btParser.getSym(4);
                List d = (List) btParser.getSym(5);
                ClassBody e = (ClassBody) btParser.getSym(6);
                btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
                break;
            }  
            //
            // Rule 449:  ValueClassDeclaration ::= ClassModifiersopt value class identifier Superopt Interfacesopt ClassBody
            //
            case 449: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(4));
                TypeNode c = (TypeNode) btParser.getSym(5);
                List d = (List) btParser.getSym(6);
                ClassBody e = (ClassBody) btParser.getSym(7);
                btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
                break;
            }   
            //
            // Rule 450:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt [ ] ArrayInitializer
            //
            case 450: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                ArrayInit d = (ArrayInit) btParser.getSym(6);
                // btParser.setSym1(nf.ArrayConstructor(pos(), a, false, null, d));
                btParser.setSym1(nf.NewArray(pos(), a, 1, d));
                break;
            }
     
            //
            // Rule 451:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt [ Expression ]
            //
            case 451: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                boolean unsafe = (btParser.getSym(3) != null);
                Expr c = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, false, c, null));
                break;
            }
     
            //
            // Rule 452:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt [ Expression ] Expression
            //
            case 452: {
     TypeNode a = (TypeNode) btParser.getSym(2);
     boolean unsafe = (btParser.getSym(3) != null);
     Expr distr = (Expr) btParser.getSym(5);
     Expr initializer = (Expr) btParser.getSym(7);
    btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, false, distr, initializer));
                break;
            }
     
            //
            // Rule 453:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt [ Expression ] ( FormalParameter ) MethodBody
            //
            case 453: {
     TypeNode a = (TypeNode) btParser.getSym(2);
     boolean unsafe = (btParser.getSym(3) != null);
     Expr distr = (Expr) btParser.getSym(5);
     X10Formal f = (X10Formal) btParser.getSym(8);
     Block body = (Block) btParser.getSym(10);
     New initializer = makeInitializer( pos(btParser.getFirstToken(7), btParser.getLastToken(10)), 
                       a, f, body );
    btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, false, distr, initializer));
                break;
            }
     
            //
            // Rule 454:  ArrayCreationExpression ::= new ArrayBaseType value Unsafeopt [ Expression ]
            //
            case 454: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                boolean unsafe = (btParser.getSym(3) != null);
                Expr c = (Expr) btParser.getSym(6);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, true, c, null));
                break;
            }
     
            //
            // Rule 455:  ArrayCreationExpression ::= new ArrayBaseType value Unsafeopt [ Expression ] Expression
            //
            case 455: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                boolean unsafe = (btParser.getSym(4) != null);
                Expr c = (Expr) btParser.getSym(6);
                Expr d = (Expr) btParser.getSym(8);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, true, c, d));
                break;
            }
     
            //
            // Rule 456:  ArrayCreationExpression ::= new ArrayBaseType value Unsafeopt [ Expression ] ( FormalParameter ) MethodBody
            //
            case 456: {
     TypeNode a = (TypeNode) btParser.getSym(2);
     boolean unsafe = (btParser.getSym(4) != null);
     Expr distr = (Expr) btParser.getSym(6);
     X10Formal f = (X10Formal) btParser.getSym(9);
     Block body = (Block) btParser.getSym(11);
     New initializer = makeInitializer( pos(btParser.getFirstToken(8), btParser.getLastToken(11)), 
                       a, f, body );
    btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, true, distr, initializer));
                break;
            }
                
            //
            // Rule 457:  ArrayBaseType ::= PrimitiveType
            //
            case 457:
                break;
          
            
            //
            // Rule 458:  ArrayBaseType ::= ClassOrInterfaceType
            //
            case 458:
                break;
          
            //
            // Rule 459:  ArrayAccess ::= ExpressionName [ ArgumentList ]
            //
            case 459: {
           Name e = (Name) btParser.getSym(1);
           List b = (List) btParser.getSym(3);
           if (b.size() == 1)
           btParser.setSym1(nf.X10ArrayAccess1(pos(), e.toExpr(), (Expr) b.get(0)));
           else btParser.setSym1(nf.X10ArrayAccess(pos(), e.toExpr(), b));
                     break;
            }
         
            //
            // Rule 460:  ArrayAccess ::= PrimaryNoNewArray [ ArgumentList ]
            //
            case 460: { 
           Expr a = (Expr) btParser.getSym(1);
           List b = (List) btParser.getSym(3);
           if (b.size() == 1)
           btParser.setSym1(nf.X10ArrayAccess1(pos(), a, (Expr) b.get(0)));
           else btParser.setSym1(nf.X10ArrayAccess(pos(), a, b));
                    break;
            }
        
            //
            // Rule 461:  Statement ::= NowStatement
            //
            case 461:
                break; 
 
            //
            // Rule 462:  Statement ::= ClockedStatement
            //
            case 462:
                break; 
 
            //
            // Rule 463:  Statement ::= AsyncStatement
            //
            case 463:
                break; 
 
            //
            // Rule 464:  Statement ::= AtomicStatement
            //
            case 464:
                break; 
 
            //
            // Rule 465:  Statement ::= WhenStatement
            //
            case 465:
                break; 
 
            //
            // Rule 466:  Statement ::= ForEachStatement
            //
            case 466:
                break; 
 
            //
            // Rule 467:  Statement ::= AtEachStatement
            //
            case 467:
                break; 
 
            //
            // Rule 468:  Statement ::= FinishStatement
            //
            case 468:
                break; 
 
            //
            // Rule 469:  StatementWithoutTrailingSubstatement ::= NextStatement
            //
            case 469:
                break; 
 
            //
            // Rule 470:  StatementWithoutTrailingSubstatement ::= AwaitStatement
            //
            case 470:
                break; 
 
            //
            // Rule 471:  StatementNoShortIf ::= NowStatementNoShortIf
            //
            case 471:
                break; 
 
            //
            // Rule 472:  StatementNoShortIf ::= ClockedStatementNoShortIf
            //
            case 472:
                break; 
 
            //
            // Rule 473:  StatementNoShortIf ::= AsyncStatementNoShortIf
            //
            case 473:
                break; 
 
            //
            // Rule 474:  StatementNoShortIf ::= AtomicStatementNoShortIf
            //
            case 474:
                break; 
 
            //
            // Rule 475:  StatementNoShortIf ::= WhenStatementNoShortIf
            //
            case 475:
                break; 
 
            //
            // Rule 476:  StatementNoShortIf ::= ForEachStatementNoShortIf
            //
            case 476:
                break; 
 
            //
            // Rule 477:  StatementNoShortIf ::= AtEachStatementNoShortIf
            //
            case 477:
                break; 
 
            //
            // Rule 478:  StatementNoShortIf ::= FinishStatementNoShortIf
            //
            case 478:
                break; 
 
            //
            // Rule 479:  NowStatement ::= now ( Clock ) Statement
            //
            case 479: {
                Name a = (Name) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Now(pos(), a.toExpr(), b));
                break;
            }
     
            //
            // Rule 480:  ClockedStatement ::= clocked ( ClockList ) Statement
            //
            case 480: {
                List a = (List) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), a, b));
                break;
            }
     
            //
            // Rule 481:  AsyncStatement ::= async PlaceExpressionSingleListopt Statement
            //
            case 481: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Async(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 482:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 482: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Atomic(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 483:  WhenStatement ::= when ( Expression ) Statement
            //
            case 483: {
                Expr e = (Expr) btParser.getSym(3);
                Stmt s = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.When(pos(), e,s));
                break;
            }
     
            //
            // Rule 484:  WhenStatement ::= WhenStatement or ( Expression ) Statement
            //
            case 484: {
                When w = (When) btParser.getSym(1);
                Expr e = (Expr) btParser.getSym(4);
                Stmt s = (Stmt) btParser.getSym(6);
                When.Branch wb = nf.WhenBranch(pos(btParser.getFirstToken(2), btParser.getLastToken(6)), e, s);
                w.add(wb);
                btParser.setSym1(w);
                break;
            }
     
            //
            // Rule 485:  ForEachStatement ::= foreach ( FormalParameter : Expression ) Statement
            //
            case 485: {
       X10Formal f = (X10Formal) btParser.getSym(3);
       Formal ff =  f.flags(f.flags().Final()); // make it final
       Expr e = (Expr) btParser.getSym(5);
       Stmt s = (Stmt) btParser.getSym(7);
       X10Loop x = nf.ForEach(pos(), ff, e, 
                              f.hasExplodedVars() 
                              ? nf.Block(pos(), f.explode(s))
                              : s);
       btParser.setSym1(x);
              break;
            }  
            //
            // Rule 486:  AtEachStatement ::= ateach ( FormalParameter : Expression ) Statement
            //
            case 486: {
       X10Formal f = (X10Formal) btParser.getSym(3);
       Formal ff = f.flags(f.flags().Final()); // make it final
       Expr e = (Expr) btParser.getSym(5);
       Stmt s = (Stmt) btParser.getSym(7);
       X10Loop x = nf.AtEach(pos(), ff, e, 
                              f.hasExplodedVars() 
                              ? nf.Block(pos(), f.explode(s))
                              : s);
       btParser.setSym1(x);
              break;
            }  
            //
            // Rule 487:  EnhancedForStatement ::= for ( FormalParameter : Expression ) Statement
            //
            case 487: {
      X10Formal f = (X10Formal) btParser.getSym(3);
       Formal ff = f.flags(f.flags().Final()); // make it final
       Expr e = (Expr) btParser.getSym(5);
       Stmt s = (Stmt) btParser.getSym(7);
       X10Loop x = nf.ForLoop(pos(), ff, e, 
                              f.hasExplodedVars() 
                              ? nf.Block(pos(), f.explode(s))
                              : s);
       btParser.setSym1(x);           
              break;
            }  
            //
            // Rule 488:  FinishStatement ::= finish Statement
            //
            case 488: {
                Stmt b = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Finish(pos(),  b));
                break;
            }
     
            //
            // Rule 489:  NowStatementNoShortIf ::= now ( Clock ) StatementNoShortIf
            //
            case 489: {
                Name a = (Name) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Now(pos(), a.toExpr(), b));
                break;
            }
     
            //
            // Rule 490:  ClockedStatementNoShortIf ::= clocked ( ClockList ) StatementNoShortIf
            //
            case 490: {
                List a = (List) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), a, b));
                break;
            }
     
            //
            // Rule 491:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt StatementNoShortIf
            //
            case 491: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Async(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 492:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 492: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Atomic(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 493:  WhenStatementNoShortIf ::= when ( Expression ) StatementNoShortIf
            //
            case 493: {
                Expr e = (Expr) btParser.getSym(3);
                Stmt s = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.When(pos(), e,s));
                break;
            }
     
            //
            // Rule 494:  WhenStatementNoShortIf ::= WhenStatement or ( Expression ) StatementNoShortIf
            //
            case 494: {
                When w = (When) btParser.getSym(1);
                Expr e = (Expr) btParser.getSym(4);
                Stmt s = (Stmt) btParser.getSym(6);
                When.Branch wb = nf.WhenBranch(pos(btParser.getFirstToken(2), btParser.getLastToken(6)), e, s);
                w.add(wb);
                btParser.setSym1(w);
                break;
            }
     
            //
            // Rule 495:  ForEachStatementNoShortIf ::= foreach ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 495: {
       X10Formal f = (X10Formal) btParser.getSym(3);
       Formal ff = f.flags(f.flags().Final()); // make it final
       Expr e = (Expr) btParser.getSym(5);
       Stmt s = (Stmt) btParser.getSym(7);
       X10Loop x = nf.ForEach(pos(), ff, e, 
                              f.hasExplodedVars() 
                              ? nf.Block(pos(), f.explode(s))
                              : s);
       btParser.setSym1(x);
              break;
            }  
            //
            // Rule 496:  AtEachStatementNoShortIf ::= ateach ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 496: {
       X10Formal f = (X10Formal) btParser.getSym(3);
       Formal ff = f.flags(f.flags().Final()); // make it final
       Expr e = (Expr) btParser.getSym(5);
       Stmt s = (Stmt) btParser.getSym(7);
       X10Loop x = nf.AtEach(pos(), ff, e, 
                              f.hasExplodedVars() 
                              ? nf.Block(pos(), f.explode(s))
                              : s);
       btParser.setSym1(x);
              break;
            }  
            //
            // Rule 497:  EnhancedForStatementNoShortIf ::= for ( FormalParameter : Expression ) StatementNoShortIf
            //
            case 497: {
       X10Formal f = (X10Formal) btParser.getSym(3);
       Formal ff = f.flags(f.flags().Final()); // make it final
       Expr e = (Expr) btParser.getSym(5);
       Stmt s = (Stmt) btParser.getSym(7);
       X10Loop x = nf.ForLoop(pos(), ff, e, 
                              f.hasExplodedVars() 
                              ? nf.Block(pos(), f.explode(s))
                              : s);
       btParser.setSym1(x);
               break;
            }  
            //
            // Rule 498:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 498: {
                Stmt b = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Finish(pos(),  b));
                break;
            }
     
            //
            // Rule 499:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 499: {
        btParser.setSym1(btParser.getSym(2));
                break;
            }
    
        
            //
            // Rule 500:  PlaceExpression ::= Expression
            //
            case 500:
                break; 
     
            //
            // Rule 501:  NextStatement ::= next ;
            //
            case 501: {
                btParser.setSym1(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 502:  AwaitStatement ::= await Expression ;
            //
            case 502: { 
         Expr e = (Expr) btParser.getSym(2);
         btParser.setSym1(nf.Await(pos(), e));
                 break;
            }
     
            //
            // Rule 503:  ClockList ::= Clock
            //
            case 503: {
                Name c = (Name) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(c.toExpr());
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 504:  ClockList ::= ClockList , Clock
            //
            case 504: {
                List l = (List) btParser.getSym(1);
                Name c = (Name) btParser.getSym(3);
                l.add(c.toExpr());
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 505:  Clock ::= identifier
            //
            case 505: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 506:  CastExpression ::= ( Type ) UnaryExpressionNotPlusMinus
            //
            case 506: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Cast(pos(), a, b));
                break;
            }
     
            //
            // Rule 507:  MethodInvocation ::= Primary -> identifier ( ArgumentListopt )
            //
            case 507: { 
          Expr a = (Expr) btParser.getSym(1);
          polyglot.lex.Identifier b = id(btParser.getToken(3));
          List c = (List) btParser.getSym(5);
          btParser.setSym1(nf.RemoteCall(pos(), a, b.getIdentifier(), c));
                 break;
            } 
     
            //
            // Rule 508:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 508: {
                Expr a = (Expr) btParser.getSym(1);
                TypeNode b = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Instanceof(pos(), a, b));
                break;
            }
     
            //
            // Rule 509:  IdentifierList ::= IdentifierList , identifier
            //
            case 509: { 
       List l = (List) btParser.getSym(1);
       polyglot.lex.Identifier a = id(btParser.getToken(3));
       l.add(new Name(nf, ts, pos(), a.getIdentifier()));
       btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 510:  IdentifierList ::= identifier
            //
            case 510: {
       polyglot.lex.Identifier a = id(btParser.getToken(1));
       List l = new TypedList(new LinkedList(), Name.class, false);
       l.add(new Name(nf, ts, pos(), a.getIdentifier()));
       btParser.setSym1(l);
                 break;
            }
     
            //
            // Rule 511:  Primary ::= FutureExpression
            //
            case 511:
                break; 
 
            //
            // Rule 512:  Primary ::= [ ArgumentList ]
            //
            case 512: {
       Name x10 = new Name(nf, ts, pos(), "x10");
        Name x10Lang = new Name(nf, ts, pos(), x10, "lang");
        Name x10LangRegion = new Name(nf, ts, pos(), x10Lang, "region");
        Name x10LangRegionFactory = new Name(nf, ts, pos(), x10LangRegion, "factory");
        Name x10LangRegionFactoryRegion = new Name(nf, ts, pos(), x10LangRegionFactory, "region");
        Name x10LangPoint = new Name(nf, ts, pos(), x10Lang, "point");
        Name x10LangPointFactory = new Name(nf, ts, pos(), x10LangPoint, "factory");
        Name x10LangPointFactoryPoint = new Name(nf, ts, pos(), x10LangPointFactory, "point");

        List a = (List) btParser.getSym(2);
        Tuple tuple  = nf.Tuple(pos(), x10LangPointFactoryPoint, x10LangRegionFactoryRegion, a);
        btParser.setSym1(tuple);
               break;
            }
     
            //
            // Rule 513:  AssignmentExpression ::= Expression -> Expression
            //
            case 513: {
        Expr a = (Expr) btParser.getSym(1);
        Expr b = (Expr) btParser.getSym(3);
        //System.out.println("Distribution:" + a + "|" + b + "|");
        // x10.lang.region.factory.region(  ArgumentList )
        // Construct the MethodName
        Name x10 = new Name(nf, ts, pos(), "x10");
        Name x10Lang = new Name(nf, ts, pos(), x10, "lang");

        Name x10LangDistribution = new Name(nf, ts, pos(), x10Lang, "distribution");
        Name x10LangDistributionFactory = 
             new Name(nf, ts, pos(), x10LangDistribution, "factory");
        Name x10LangDistributionFactoryConstant = 
            new Name(nf, ts, pos(), x10LangDistributionFactory, "constant");
        List l = new TypedList(new LinkedList(), Expr.class, false);
        l.add(a);
        l.add(b);
        Call call = nf.Call(pos(), x10LangDistributionFactoryConstant.prefix.toReceiver(), "constant", l);
        btParser.setSym1(call);
               break;
            }
     
            //
            // Rule 514:  Primary ::= Expression : Expression
            //
            case 514: {

        Expr a = (Expr) btParser.getSym(1);
        Expr b = (Expr) btParser.getSym(3);
        Name x10 = new Name(nf, ts, pos(), "x10");
        Name x10Lang = new Name(nf, ts, pos(), x10, "lang");

        Name x10LangRegion = new Name(nf, ts, pos(), x10Lang, "region");
        Name x10LangRegionFactory = new Name(nf, ts, pos(), x10LangRegion, "factory");
        Name x10LangRegionFactoryRegion = new Name(nf, ts, pos(), x10LangRegionFactory, "region");
        List l = new TypedList(new LinkedList(), Expr.class, false);
        l.add(a);
        l.add(b);
        Call regionCall = nf.Call( pos(), x10LangRegionFactoryRegion.prefix.toReceiver(), "region", l  );
        btParser.setSym1(regionCall);
               break;
            }
     
            //
            // Rule 515:  FutureExpression ::= future PlaceExpressionSingleListopt { Expression }
            //
            case 515: {
                Expr e1 = (Expr) btParser.getSym(2),
                     e2 = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Future(pos(), (e1 == null ? nf.Here(pos(btParser.getFirstToken())) : e1), e2));
                break;
            }
     
            //
            // Rule 516:  FieldModifier ::= mutable
            //
            case 516: {
                btParser.setSym1(Flags.MUTABLE);
                break;
            }
     
            //
            // Rule 517:  FieldModifier ::= const
            //
            case 517: {
                btParser.setSym1(Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL));
                break;
            }
     
            //
            // Rule 518:  PlaceTypeSpecifieropt ::= $Empty
            //
            case 518:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 519:  PlaceTypeSpecifieropt ::= PlaceTypeSpecifier
            //
            case 519:
                break; 
 
            //
            // Rule 520:  DepParametersopt ::= $Empty
            //
            case 520:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 521:  DepParametersopt ::= DepParameters
            //
            case 521:
                break; 
 
            //
            // Rule 522:  WhereClauseopt ::= $Empty
            //
            case 522:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 523:  WhereClauseopt ::= WhereClause
            //
            case 523:
                break; 
 
            //
            // Rule 524:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 524:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 525:  PlaceExpressionSingleListopt ::= PlaceExpressionSingleList
            //
            case 525:
                break; 
 
            //
            // Rule 526:  ArgumentListopt ::= $Empty
            //
            case 526:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 527:  ArgumentListopt ::= ArgumentList
            //
            case 527:
                break; 
 
            //
            // Rule 528:  DepParametersopt ::= $Empty
            //
            case 528:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 529:  DepParametersopt ::= DepParameters
            //
            case 529:
                break; 
 
            //
            // Rule 530:  Unsafeopt ::= $Empty
            //
            case 530:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 531:  Unsafeopt ::= unsafe
            //
            case 531: { btParser.setSym1(nf.Here(pos(btParser.getFirstToken(1))));           break;
            } 
    
            default:
                break;
        }
        return;
    }
}

