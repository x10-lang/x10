
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

import java.util.*;
import polyglot.ast.*;
import polyglot.lex.*;
import polyglot.util.*;
import polyglot.parse.*;
import polyglot.types.*;
import polyglot.*;
import polyglot.ast.Assert;
import polyglot.ext.x10.ast.*;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.jl.parse.Name;
import polyglot.frontend.Parser;
import polyglot.frontend.FileSource;
import polyglot.main.Report;

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
      List paramList = new TypedList(new LinkedList(), X10Formal.class, false);
      paramList.add( f );
      MethodDecl decl = nf.MethodDecl(pos, flags, resultType, 
                                    "apply", paramList, new LinkedList(), body);
      //  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
      List classBodyDecl = new LinkedList();
      classBodyDecl.add( decl );
      Name tArray = new Name(nf, ts, pos, resultType.toString() + "Array");
      Name tArrayPointwiseOp = new Name(nf, ts, pos, tArray, "pointwiseOp");
      New initializer = nf.New(pos, tArrayPointwiseOp.toType(), new LinkedList(), 
                             nf.ClassBody( pos, classBodyDecl) );
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
        return new StringLiteral(pos(i), prsStream.getName(i), X10Parsersym.TK_StringLiteral);
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
         Report.report(2, "Parser turning keyword " +
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
            // Rule 13:  ReferenceType ::= ClassOrInterfaceType
            //
            case 13:
                break;
 
            //
            // Rule 14:  ReferenceType ::= ArrayType
            //
            case 14:
                break;
 
            //
            // Rule 15:  ClassType ::= TypeName
            //
            case 15: {
//vj                    assert(btParser.getSym(2) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toType());
                break;
            }
     
            //
            // Rule 16:  InterfaceType ::= TypeName
            //
            case 16: {
//vj                    assert(btParser.getSym(2) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toType());
                break;
            }
     
            //
            // Rule 17:  TypeName ::= identifier
            //
            case 17: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 18:  TypeName ::= TypeName DOT identifier
            //
            case 18: {
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
            // Rule 19:  ClassName ::= TypeName
            //
            case 19:
                break;
 
            //
            // Rule 20:  TypeVariable ::= identifier
            //
            case 20:
                break;
 
            //
            // Rule 21:  ArrayType ::= Type LBRACKET RBRACKET
            //
            case 21: {
                TypeNode a = (TypeNode) btParser.getSym(1);
                btParser.setSym1(nf.array(a, pos(), 1));
                break;
            }
     
            //
            // Rule 22:  TypeParameter ::= TypeVariable TypeBoundopt
            //
            case 22:
                bad_rule = 22;
                break;
 
            //
            // Rule 23:  TypeBound ::= extends ClassOrInterfaceType AdditionalBoundListopt
            //
            case 23:
                bad_rule = 23;
                break;
 
            //
            // Rule 24:  AdditionalBoundList ::= AdditionalBound
            //
            case 24:
                bad_rule = 24;
                break;
 
            //
            // Rule 25:  AdditionalBoundList ::= AdditionalBoundList AdditionalBound
            //
            case 25:
                bad_rule = 25;
                break;
 
            //
            // Rule 26:  AdditionalBound ::= AND InterfaceType
            //
            case 26:
                bad_rule = 26;
                break;
 
            //
            // Rule 27:  TypeArguments ::= LESS ActualTypeArgumentList GREATER
            //
            case 27:
                bad_rule = 27;
                break;
 
            //
            // Rule 28:  ActualTypeArgumentList ::= ActualTypeArgument
            //
            case 28:
                bad_rule = 28;
                break;
 
            //
            // Rule 29:  ActualTypeArgumentList ::= ActualTypeArgumentList COMMA ActualTypeArgument
            //
            case 29:
                bad_rule = 29;
                break;
 
            //
            // Rule 30:  Wildcard ::= QUESTION WildcardBoundsOpt
            //
            case 30:
                bad_rule = 30;
                break;
 
            //
            // Rule 31:  WildcardBounds ::= extends ReferenceType
            //
            case 31:
                bad_rule = 31;
                break;
 
            //
            // Rule 32:  WildcardBounds ::= super ReferenceType
            //
            case 32:
                bad_rule = 32;
                break;
 
            //
            // Rule 33:  PackageName ::= identifier
            //
            case 33: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 34:  PackageName ::= PackageName DOT identifier
            //
            case 34: {
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
            // Rule 35:  ExpressionName ::= identifier
            //
            case 35: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 36:  ExpressionName ::= AmbiguousName DOT identifier
            //
            case 36: {
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
            // Rule 37:  MethodName ::= identifier
            //
            case 37: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 38:  MethodName ::= AmbiguousName DOT identifier
            //
            case 38: {
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
            // Rule 39:  PackageOrTypeName ::= identifier
            //
            case 39: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 40:  PackageOrTypeName ::= PackageOrTypeName DOT identifier
            //
            case 40: {
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
            // Rule 41:  AmbiguousName ::= identifier
            //
            case 41: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 42:  AmbiguousName ::= AmbiguousName DOT identifier
            //
            case 42: {
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
            // Rule 43:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 43: {
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
            // Rule 44:  ImportDeclarations ::= ImportDeclaration
            //
            case 44: {
                List l = new TypedList(new LinkedList(), Import.class, false);
                Import a = (Import) btParser.getSym(1);
                l.add(a);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 45:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 45: {
                List l = (TypedList) btParser.getSym(1);
                Import b = (Import) btParser.getSym(2);
                if (b != null)
                    l.add(b);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 46:  TypeDeclarations ::= TypeDeclaration
            //
            case 46: {
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                TopLevelDecl a = (TopLevelDecl) btParser.getSym(1);
                if (a != null)
                    l.add(a);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 47:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 47: {
                List l = (TypedList) btParser.getSym(1);
                TopLevelDecl b = (TopLevelDecl) btParser.getSym(2);
                if (b != null)
                    l.add(b);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 48:  PackageDeclaration ::= package PackageName SEMICOLON
            //
            case 48: {
//vj                    assert(btParser.getSym(1) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(a.toPackage());
                break;
            }
     
            //
            // Rule 49:  ImportDeclaration ::= SingleTypeImportDeclaration
            //
            case 49:
                break;
 
            //
            // Rule 50:  ImportDeclaration ::= TypeImportOnDemandDeclaration
            //
            case 50:
                break;
 
            //
            // Rule 51:  ImportDeclaration ::= SingleStaticImportDeclaration
            //
            case 51:
                break;
 
            //
            // Rule 52:  ImportDeclaration ::= StaticImportOnDemandDeclaration
            //
            case 52:
                break;
 
            //
            // Rule 53:  SingleTypeImportDeclaration ::= import TypeName SEMICOLON
            //
            case 53: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.CLASS, a.toString()));
                break;
            }
     
            //
            // Rule 54:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName DOT MULTIPLY SEMICOLON
            //
            case 54: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.PACKAGE, a.toString()));
                break;
            }
     
            //
            // Rule 55:  SingleStaticImportDeclaration ::= import static TypeName DOT identifier SEMICOLON
            //
            case 55:
                bad_rule = 55;
                break;
 
            //
            // Rule 56:  StaticImportOnDemandDeclaration ::= import static TypeName DOT MULTIPLY SEMICOLON
            //
            case 56:
                bad_rule = 56;
                break;
 
            //
            // Rule 57:  TypeDeclaration ::= ClassDeclaration
            //
            case 57:
                break;
 
            //
            // Rule 58:  TypeDeclaration ::= InterfaceDeclaration
            //
            case 58:
                break;
 
            //
            // Rule 59:  TypeDeclaration ::= SEMICOLON
            //
            case 59: {
                btParser.setSym1(null);
                break;
            }
     
            //
            // Rule 60:  ClassDeclaration ::= NormalClassDeclaration
            //
            case 60:
                break;
 
            //
            // Rule 61:  NormalClassDeclaration ::= ClassModifiersopt class identifier Superopt Interfacesopt ClassBody
            //
            case 61: {
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
            // Rule 62:  ClassModifiers ::= ClassModifier
            //
            case 62:
                break;
 
            //
            // Rule 63:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 63: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 64:  ClassModifier ::= public
            //
            case 64: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 65:  ClassModifier ::= protected
            //
            case 65: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 66:  ClassModifier ::= private
            //
            case 66: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 67:  ClassModifier ::= abstract
            //
            case 67: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 68:  ClassModifier ::= static
            //
            case 68: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 69:  ClassModifier ::= final
            //
            case 69: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 70:  ClassModifier ::= strictfp
            //
            case 70: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 71:  TypeParameters ::= LESS TypeParameterList GREATER
            //
            case 71:
                bad_rule = 71;
                break;
 
            //
            // Rule 72:  TypeParameterList ::= TypeParameter
            //
            case 72:
                bad_rule = 72;
                break;
 
            //
            // Rule 73:  TypeParameterList ::= TypeParameterList COMMA TypeParameter
            //
            case 73:
                bad_rule = 73;
                break;
 
            //
            // Rule 74:  Super ::= extends ClassType
            //
            case 74: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 75:  Interfaces ::= implements InterfaceTypeList
            //
            case 75: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 76:  InterfaceTypeList ::= InterfaceType
            //
            case 76: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 77:  InterfaceTypeList ::= InterfaceTypeList COMMA InterfaceType
            //
            case 77: {
                List l = (TypedList) btParser.getSym(1);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 78:  ClassBody ::= LBRACE ClassBodyDeclarationsopt RBRACE
            //
            case 78: {
                btParser.setSym1(nf.ClassBody(pos(btParser.getFirstToken(), btParser.getLastToken()), (List) btParser.getSym(2)));
                break;
            }
     
            //
            // Rule 79:  ClassBodyDeclarations ::= ClassBodyDeclaration
            //
            case 79:
                break;
 
            //
            // Rule 80:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 80: {
                List a = (List) btParser.getSym(1),
                     b = (List) btParser.getSym(2);
                a.addAll(b);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 81:  ClassBodyDeclaration ::= ClassMemberDeclaration
            //
            case 81:
                break;
 
            //
            // Rule 82:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 82: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Block a = (Block) btParser.getSym(1);
                l.add(nf.Initializer(pos(), Flags.NONE, a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 83:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 83: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Block a = (Block) btParser.getSym(1);
                l.add(nf.Initializer(pos(), Flags.STATIC, a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 84:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 84: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 85:  ClassMemberDeclaration ::= FieldDeclaration
            //
            case 85:
                break;
 
            //
            // Rule 86:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 86: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 87:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 87: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 88:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 88: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 89:  ClassMemberDeclaration ::= SEMICOLON
            //
            case 89: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 90:  FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators SEMICOLON
            //
            case 90: {
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
            // Rule 91:  VariableDeclarators ::= VariableDeclarator
            //
            case 91: {
                List l = new TypedList(new LinkedList(), X10VarDeclarator.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 92:  VariableDeclarators ::= VariableDeclarators COMMA VariableDeclarator
            //
            case 92: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 93:  VariableDeclarator ::= VariableDeclaratorId
            //
            case 93:
                break;
 
            //
            // Rule 94:  VariableDeclarator ::= VariableDeclaratorId EQUAL VariableInitializer
            //
            case 94: {
                X10VarDeclarator a = (X10VarDeclarator) btParser.getSym(1);
                Expr b = (Expr) btParser.getSym(3);
                a.init = b; 
                // btParser.setSym1(a); 
                break;
            }
     
            //
            // Rule 95:  VariableDeclaratorId ::= identifier
            //
            case 95: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new X10VarDeclarator(pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 96:  VariableDeclaratorId ::= VariableDeclaratorId LBRACKET RBRACKET
            //
            case 96: {
                X10VarDeclarator a = (X10VarDeclarator) btParser.getSym(1);
                a.dims++;
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 97:  VariableDeclaratorId ::= identifier LBRACKET IdentifierList RBRACKET
            //
            case 97: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                List paramList = (List) btParser.getSym(3);
                btParser.setSym1(new X10VarDeclarator(pos(), a.getIdentifier(), paramList));
                break;
            }
     
            //
            // Rule 98:  VariableDeclaratorId ::= LBRACKET IdentifierList RBRACKET
            //
            case 98: {
                String name = polyglot.ext.x10.visit.X10PrettyPrinterVisitor.getId();
                List paramList = (List) btParser.getSym(2);
                btParser.setSym1(new X10VarDeclarator(pos(), name, paramList));
                break;
            }
     
            //
            // Rule 99:  VariableInitializer ::= Expression
            //
            case 99:
                break;
 
            //
            // Rule 100:  VariableInitializer ::= ArrayInitializer
            //
            case 100:
                break;
 
            //
            // Rule 101:  FieldModifiers ::= FieldModifier
            //
            case 101:
                break;
 
            //
            // Rule 102:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 102: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 103:  FieldModifier ::= public
            //
            case 103: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 104:  FieldModifier ::= protected
            //
            case 104: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 105:  FieldModifier ::= private
            //
            case 105: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 106:  FieldModifier ::= static
            //
            case 106: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 107:  FieldModifier ::= final
            //
            case 107: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 108:  FieldModifier ::= transient
            //
            case 108: {
                btParser.setSym1(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 109:  FieldModifier ::= volatile
            //
            case 109: {
                btParser.setSym1(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 110:  MethodDeclaration ::= MethodHeader MethodBody
            //
            case 110: {
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
                btParser.setSym1(a.body(b));
                break;
            }
     
            //
            // Rule 111:  MethodHeader ::= MethodModifiersopt ResultType MethodDeclarator Throwsopt
            //
            case 111: {
                Flags a = (Flags) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
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
            // Rule 112:  ResultType ::= Type
            //
            case 112:
                break;
 
            //
            // Rule 113:  ResultType ::= void
            //
            case 113: {
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 114:  MethodDeclarator ::= identifier LPAREN FormalParameterListopt RPAREN
            //
            case 114: {
                Object[] a = new Object[3];
                a[0] =  new Name(nf, ts, pos(), id(btParser.getToken(1)).getIdentifier());
                a[1] = btParser.getSym(3);
                a[2] = new Integer(0);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 115:  MethodDeclarator ::= MethodDeclarator LBRACKET RBRACKET
            //
            case 115: {
                Object[] a = (Object []) btParser.getSym(1);
                a[2] = new Integer(((Integer) a[2]).intValue() + 1);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 116:  FormalParameterList ::= LastFormalParameter
            //
            case 116: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 117:  FormalParameterList ::= FormalParameters COMMA LastFormalParameter
            //
            case 117: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 118:  FormalParameters ::= FormalParameter
            //
            case 118: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 119:  FormalParameters ::= FormalParameters COMMA FormalParameter
            //
            case 119: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 120:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 120: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                X10VarDeclarator b = (X10VarDeclarator) btParser.getSym(3);
                b.setFlag(f);
                btParser.setSym1(nf.Formal(pos(), nf.array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b));
                break;
            }
     
            //
            // Rule 122:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 122: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 123:  VariableModifier ::= final
            //
            case 123: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 124:  LastFormalParameter ::= VariableModifiersopt Type ...opt VariableDeclaratorId
            //
            case 124: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                assert(btParser.getSym(3) == null);
                X10VarDeclarator b = (X10VarDeclarator) btParser.getSym(4);
                b.setFlag(f);
                btParser.setSym1(nf.Formal(pos(), nf.array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b));
                break;
            }
     
            //
            // Rule 125:  MethodModifiers ::= MethodModifier
            //
            case 125:
                break;
 
            //
            // Rule 126:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 126: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 127:  MethodModifier ::= public
            //
            case 127: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 128:  MethodModifier ::= protected
            //
            case 128: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 129:  MethodModifier ::= private
            //
            case 129: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 130:  MethodModifier ::= abstract
            //
            case 130: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 131:  MethodModifier ::= static
            //
            case 131: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 132:  MethodModifier ::= final
            //
            case 132: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 133:  MethodModifier ::= synchronized
            //
            case 133: {
                btParser.setSym1(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 134:  MethodModifier ::= native
            //
            case 134: {
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 135:  MethodModifier ::= strictfp
            //
            case 135: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 136:  Throws ::= throws ExceptionTypeList
            //
            case 136: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 137:  ExceptionTypeList ::= ExceptionType
            //
            case 137: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 138:  ExceptionTypeList ::= ExceptionTypeList COMMA ExceptionType
            //
            case 138: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 139:  ExceptionType ::= ClassType
            //
            case 139:
                break;
 
            //
            // Rule 140:  ExceptionType ::= TypeVariable
            //
            case 140:
                break;
 
            //
            // Rule 141:  MethodBody ::= Block
            //
            case 141:
                break;
 
            //
            // Rule 142:  MethodBody ::= SEMICOLON
            //
            case 142:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 143:  InstanceInitializer ::= Block
            //
            case 143:
                break;
 
            //
            // Rule 144:  StaticInitializer ::= static Block
            //
            case 144: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 145:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 145: {
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
            // Rule 146:  ConstructorDeclarator ::= SimpleTypeName LPAREN FormalParameterListopt RPAREN
            //
            case 146: {
//vj                    assert(btParser.getSym(1) == null);
                Object[] a = new Object[3];
//vj                    a[0] = btParser.getSym(1);
                a[1] = btParser.getSym(1);
                a[2] = btParser.getSym(3);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 147:  SimpleTypeName ::= identifier
            //
            case 147: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 148:  ConstructorModifiers ::= ConstructorModifier
            //
            case 148:
                break;
 
            //
            // Rule 149:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 149: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 150:  ConstructorModifier ::= public
            //
            case 150: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 151:  ConstructorModifier ::= protected
            //
            case 151: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 152:  ConstructorModifier ::= private
            //
            case 152: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 153:  ConstructorBody ::= LBRACE ExplicitConstructorInvocationopt BlockStatementsopt RBRACE
            //
            case 153: {
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
            // Rule 154:  ExplicitConstructorInvocation ::= this LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 154: {
//vj                    assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.ThisCall(pos(), b));
                break;
            }
     
            //
            // Rule 155:  ExplicitConstructorInvocation ::= super LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 155: {
//vj                    assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.SuperCall(pos(), b));
                break;
            }
     
            //
            // Rule 156:  ExplicitConstructorInvocation ::= Primary DOT this LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 156: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.ThisCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 157:  ExplicitConstructorInvocation ::= Primary DOT super LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 157: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.SuperCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 158:  EnumDeclaration ::= ClassModifiersopt enum identifier Interfacesopt EnumBody
            //
            case 158:
                bad_rule = 158;
                break;
 
            //
            // Rule 159:  EnumBody ::= LBRACE EnumConstantsopt ,opt EnumBodyDeclarationsopt RBRACE
            //
            case 159:
                bad_rule = 159;
                break;
 
            //
            // Rule 160:  EnumConstants ::= EnumConstant
            //
            case 160:
                bad_rule = 160;
                break;
 
            //
            // Rule 161:  EnumConstants ::= EnumConstants COMMA EnumConstant
            //
            case 161:
                bad_rule = 161;
                break;
 
            //
            // Rule 162:  EnumConstant ::= identifier Argumentsopt ClassBodyopt
            //
            case 162:
                bad_rule = 162;
                break;
 
            //
            // Rule 163:  Arguments ::= LPAREN ArgumentListopt RPAREN
            //
            case 163: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 164:  EnumBodyDeclarations ::= SEMICOLON ClassBodyDeclarationsopt
            //
            case 164:
                bad_rule = 164;
                break;
 
            //
            // Rule 165:  InterfaceDeclaration ::= NormalInterfaceDeclaration
            //
            case 165:
                break;
 
            //
            // Rule 166:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier ExtendsInterfacesopt InterfaceBody
            //
            case 166: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
//vj                    assert(btParser.getSym(4) == null);
                List c = (List) btParser.getSym(4);
                ClassBody d = (ClassBody) btParser.getSym(5);
                btParser.setSym1(nf.ClassDecl(pos(), a.Interface(), b.getIdentifier(), null, c, d));
                break;
            }
     
            //
            // Rule 167:  InterfaceModifiers ::= InterfaceModifier
            //
            case 167:
                break;
 
            //
            // Rule 168:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 168: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 169:  InterfaceModifier ::= public
            //
            case 169: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 170:  InterfaceModifier ::= protected
            //
            case 170: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 171:  InterfaceModifier ::= private
            //
            case 171: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 172:  InterfaceModifier ::= abstract
            //
            case 172: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 173:  InterfaceModifier ::= static
            //
            case 173: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 174:  InterfaceModifier ::= strictfp
            //
            case 174: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 175:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 175: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 176:  ExtendsInterfaces ::= ExtendsInterfaces COMMA InterfaceType
            //
            case 176: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 177:  InterfaceBody ::= LBRACE InterfaceMemberDeclarationsopt RBRACE
            //
            case 177: {
                List a = (List)btParser.getSym(2);
                btParser.setSym1(nf.ClassBody(pos(), a));
                break;
            }
     
            //
            // Rule 178:  InterfaceMemberDeclarations ::= InterfaceMemberDeclaration
            //
            case 178:
                break;
 
            //
            // Rule 179:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 179: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 180:  InterfaceMemberDeclaration ::= ConstantDeclaration
            //
            case 180:
                break;
 
            //
            // Rule 181:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 181: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 182:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 182: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 183:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 183: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 184:  InterfaceMemberDeclaration ::= SEMICOLON
            //
            case 184: {
                btParser.setSym1(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 185:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 185: {
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
            // Rule 186:  ConstantModifiers ::= ConstantModifier
            //
            case 186:
                break;
 
            //
            // Rule 187:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 187: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 188:  ConstantModifier ::= public
            //
            case 188: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 189:  ConstantModifier ::= static
            //
            case 189: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 190:  ConstantModifier ::= final
            //
            case 190: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 191:  AbstractMethodDeclaration ::= AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt SEMICOLON
            //
            case 191: {
                Flags a = (Flags) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
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
            // Rule 192:  AbstractMethodModifiers ::= AbstractMethodModifier
            //
            case 192:
                break;
 
            //
            // Rule 193:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 193: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 194:  AbstractMethodModifier ::= public
            //
            case 194: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 195:  AbstractMethodModifier ::= abstract
            //
            case 195: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 196:  AnnotationTypeDeclaration ::= InterfaceModifiersopt AT interface identifier AnnotationTypeBody
            //
            case 196:
                bad_rule = 196;
                break;
 
            //
            // Rule 197:  AnnotationTypeBody ::= LBRACE AnnotationTypeElementDeclarationsopt RBRACE
            //
            case 197:
                bad_rule = 197;
                break;
 
            //
            // Rule 198:  AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclaration
            //
            case 198:
                bad_rule = 198;
                break;
 
            //
            // Rule 199:  AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclarations AnnotationTypeElementDeclaration
            //
            case 199:
                bad_rule = 199;
                break;
 
            //
            // Rule 200:  AnnotationTypeElementDeclaration ::= AbstractMethodModifiersopt Type identifier LPAREN RPAREN DefaultValueopt SEMICOLON
            //
            case 200:
                bad_rule = 200;
                break;
 
            //
            // Rule 201:  AnnotationTypeElementDeclaration ::= ConstantDeclaration
            //
            case 201:
                bad_rule = 201;
                break;
 
            //
            // Rule 202:  AnnotationTypeElementDeclaration ::= ClassDeclaration
            //
            case 202:
                bad_rule = 202;
                break;
 
            //
            // Rule 203:  AnnotationTypeElementDeclaration ::= InterfaceDeclaration
            //
            case 203:
                bad_rule = 203;
                break;
 
            //
            // Rule 204:  AnnotationTypeElementDeclaration ::= EnumDeclaration
            //
            case 204:
                bad_rule = 204;
                break;
 
            //
            // Rule 205:  AnnotationTypeElementDeclaration ::= AnnotationTypeDeclaration
            //
            case 205:
                bad_rule = 205;
                break;
 
            //
            // Rule 206:  AnnotationTypeElementDeclaration ::= SEMICOLON
            //
            case 206:
                bad_rule = 206;
                break;
 
            //
            // Rule 207:  DefaultValue ::= default ElementValue
            //
            case 207:
                bad_rule = 207;
                break;
 
            //
            // Rule 208:  Annotations ::= Annotation
            //
            case 208:
                bad_rule = 208;
                break;
 
            //
            // Rule 209:  Annotations ::= Annotations Annotation
            //
            case 209:
                bad_rule = 209;
                break;
 
            //
            // Rule 210:  Annotation ::= NormalAnnotation
            //
            case 210:
                bad_rule = 210;
                break;
 
            //
            // Rule 211:  Annotation ::= MarkerAnnotation
            //
            case 211:
                bad_rule = 211;
                break;
 
            //
            // Rule 212:  Annotation ::= SingleElementAnnotation
            //
            case 212:
                bad_rule = 212;
                break;
 
            //
            // Rule 213:  NormalAnnotation ::= AT TypeName LPAREN ElementValuePairsopt RPAREN
            //
            case 213:
                bad_rule = 213;
                break;
 
            //
            // Rule 214:  ElementValuePairs ::= ElementValuePair
            //
            case 214:
                bad_rule = 214;
                break;
 
            //
            // Rule 215:  ElementValuePairs ::= ElementValuePairs COMMA ElementValuePair
            //
            case 215:
                bad_rule = 215;
                break;
 
            //
            // Rule 216:  ElementValuePair ::= SimpleName EQUAL ElementValue
            //
            case 216:
                bad_rule = 216;
                break;
 
            //
            // Rule 217:  SimpleName ::= identifier
            //
            case 217: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 218:  ElementValue ::= ConditionalExpression
            //
            case 218:
                bad_rule = 218;
                break;
 
            //
            // Rule 219:  ElementValue ::= Annotation
            //
            case 219:
                bad_rule = 219;
                break;
 
            //
            // Rule 220:  ElementValue ::= ElementValueArrayInitializer
            //
            case 220:
                bad_rule = 220;
                break;
 
            //
            // Rule 221:  ElementValueArrayInitializer ::= LBRACE ElementValuesopt ,opt RBRACE
            //
            case 221:
                bad_rule = 221;
                break;
 
            //
            // Rule 222:  ElementValues ::= ElementValue
            //
            case 222:
                bad_rule = 222;
                break;
 
            //
            // Rule 223:  ElementValues ::= ElementValues COMMA ElementValue
            //
            case 223:
                bad_rule = 223;
                break;
 
            //
            // Rule 224:  MarkerAnnotation ::= AT TypeName
            //
            case 224:
                bad_rule = 224;
                break;
 
            //
            // Rule 225:  SingleElementAnnotation ::= AT TypeName LPAREN ElementValue RPAREN
            //
            case 225:
                bad_rule = 225;
                break;
 
            //
            // Rule 226:  ArrayInitializer ::= LBRACE VariableInitializersopt ,opt RBRACE
            //
            case 226: {
                List a = (List) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.ArrayInit(pos()));
                else btParser.setSym1(nf.ArrayInit(pos(), a));
                break;
            }
     
            //
            // Rule 227:  VariableInitializers ::= VariableInitializer
            //
            case 227: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 228:  VariableInitializers ::= VariableInitializers COMMA VariableInitializer
            //
            case 228: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 229:  Block ::= LBRACE BlockStatementsopt RBRACE
            //
            case 229: {
                List l = (List) btParser.getSym(2);
                btParser.setSym1(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 230:  BlockStatements ::= BlockStatement
            //
            case 230: {
                List l = new TypedList(new LinkedList(), Stmt.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 231:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 231: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 232:  BlockStatement ::= LocalVariableDeclarationStatement
            //
            case 232:
                break;
 
            //
            // Rule 233:  BlockStatement ::= ClassDeclaration
            //
            case 233: {
                ClassDecl a = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 234:  BlockStatement ::= Statement
            //
            case 234: {
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 235:  LocalVariableDeclarationStatement ::= LocalVariableDeclaration SEMICOLON
            //
            case 235:
                break;
 
            //
            // Rule 236:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 236: {
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
            // Rule 237:  Statement ::= StatementWithoutTrailingSubstatement
            //
            case 237:
                break;
 
            //
            // Rule 238:  Statement ::= LabeledStatement
            //
            case 238:
                break;
 
            //
            // Rule 239:  Statement ::= IfThenStatement
            //
            case 239:
                break;
 
            //
            // Rule 240:  Statement ::= IfThenElseStatement
            //
            case 240:
                break;
 
            //
            // Rule 241:  Statement ::= WhileStatement
            //
            case 241:
                break;
 
            //
            // Rule 242:  Statement ::= ForStatement
            //
            case 242:
                break;
 
            //
            // Rule 243:  StatementWithoutTrailingSubstatement ::= Block
            //
            case 243:
                break;
 
            //
            // Rule 244:  StatementWithoutTrailingSubstatement ::= EmptyStatement
            //
            case 244:
                break;
 
            //
            // Rule 245:  StatementWithoutTrailingSubstatement ::= ExpressionStatement
            //
            case 245:
                break;
 
            //
            // Rule 246:  StatementWithoutTrailingSubstatement ::= AssertStatement
            //
            case 246:
                break;
 
            //
            // Rule 247:  StatementWithoutTrailingSubstatement ::= SwitchStatement
            //
            case 247:
                break;
 
            //
            // Rule 248:  StatementWithoutTrailingSubstatement ::= DoStatement
            //
            case 248:
                break;
 
            //
            // Rule 249:  StatementWithoutTrailingSubstatement ::= BreakStatement
            //
            case 249:
                break;
 
            //
            // Rule 250:  StatementWithoutTrailingSubstatement ::= ContinueStatement
            //
            case 250:
                break;
 
            //
            // Rule 251:  StatementWithoutTrailingSubstatement ::= ReturnStatement
            //
            case 251:
                break;
 
            //
            // Rule 252:  StatementWithoutTrailingSubstatement ::= SynchronizedStatement
            //
            case 252:
                break;
 
            //
            // Rule 253:  StatementWithoutTrailingSubstatement ::= ThrowStatement
            //
            case 253:
                break;
 
            //
            // Rule 254:  StatementWithoutTrailingSubstatement ::= TryStatement
            //
            case 254:
                break;
 
            //
            // Rule 255:  StatementNoShortIf ::= StatementWithoutTrailingSubstatement
            //
            case 255:
                break;
 
            //
            // Rule 256:  StatementNoShortIf ::= LabeledStatementNoShortIf
            //
            case 256:
                break;
 
            //
            // Rule 257:  StatementNoShortIf ::= IfThenElseStatementNoShortIf
            //
            case 257:
                break;
 
            //
            // Rule 258:  StatementNoShortIf ::= WhileStatementNoShortIf
            //
            case 258:
                break;
 
            //
            // Rule 259:  StatementNoShortIf ::= ForStatementNoShortIf
            //
            case 259:
                break;
 
            //
            // Rule 260:  IfThenStatement ::= if LPAREN Expression RPAREN Statement
            //
            case 260: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.If(pos(), a, b));
                break;
            }
     
            //
            // Rule 261:  IfThenElseStatement ::= if LPAREN Expression RPAREN StatementNoShortIf else Statement
            //
            case 261: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 262:  IfThenElseStatementNoShortIf ::= if LPAREN Expression RPAREN StatementNoShortIf else StatementNoShortIf
            //
            case 262: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 263:  EmptyStatement ::= SEMICOLON
            //
            case 263: {
                btParser.setSym1(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 264:  LabeledStatement ::= identifier COLON Statement
            //
            case 264: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 265:  LabeledStatementNoShortIf ::= identifier COLON StatementNoShortIf
            //
            case 265: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 266:  ExpressionStatement ::= StatementExpression SEMICOLON
            //
            case 266: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Eval(pos(), a));
                break;
            }
     
            //
            // Rule 267:  StatementExpression ::= Assignment
            //
            case 267:
                break;
 
            //
            // Rule 268:  StatementExpression ::= PreIncrementExpression
            //
            case 268:
                break;
 
            //
            // Rule 269:  StatementExpression ::= PreDecrementExpression
            //
            case 269:
                break;
 
            //
            // Rule 270:  StatementExpression ::= PostIncrementExpression
            //
            case 270:
                break;
 
            //
            // Rule 271:  StatementExpression ::= PostDecrementExpression
            //
            case 271:
                break;
 
            //
            // Rule 272:  StatementExpression ::= MethodInvocation
            //
            case 272:
                break;
 
            //
            // Rule 273:  StatementExpression ::= ClassInstanceCreationExpression
            //
            case 273:
                break;
 
            //
            // Rule 274:  AssertStatement ::= assert Expression SEMICOLON
            //
            case 274: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Assert(pos(), a));
                break;
            }
     
            //
            // Rule 275:  AssertStatement ::= assert Expression COLON Expression SEMICOLON
            //
            case 275: {
                Expr a = (Expr) btParser.getSym(2),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Assert(pos(), a, b));
                break;
            }
     
            //
            // Rule 276:  SwitchStatement ::= switch LPAREN Expression RPAREN SwitchBlock
            //
            case 276: {
                Expr a = (Expr) btParser.getSym(3);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.Switch(pos(), a, b));
                break;
            }
     
            //
            // Rule 277:  SwitchBlock ::= LBRACE SwitchBlockStatementGroupsopt SwitchLabelsopt RBRACE
            //
            case 277: {
                List l = (List) btParser.getSym(2),
                     l2 = (List) btParser.getSym(3);
                l.addAll(l2);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 278:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroup
            //
            case 278:
                break;
 
            //
            // Rule 279:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 279: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 280:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 280: {
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);

                List l1 = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l1);
                l.add(nf.SwitchBlock(pos(), l2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 281:  SwitchLabels ::= SwitchLabel
            //
            case 281: {
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 282:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 282: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 283:  SwitchLabel ::= case ConstantExpression COLON
            //
            case 283: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Case(pos(), a));
                break;
            }
     
            //
            // Rule 284:  SwitchLabel ::= case EnumConstant COLON
            //
            case 284:
                bad_rule = 284;
                break;
 
            //
            // Rule 285:  SwitchLabel ::= default COLON
            //
            case 285: {
                btParser.setSym1(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 286:  EnumConstant ::= identifier
            //
            case 286:
                bad_rule = 286;
                break;
 
            //
            // Rule 287:  WhileStatement ::= while LPAREN Expression RPAREN Statement
            //
            case 287: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 288:  WhileStatementNoShortIf ::= while LPAREN Expression RPAREN StatementNoShortIf
            //
            case 288: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 289:  DoStatement ::= do Statement while LPAREN Expression RPAREN SEMICOLON
            //
            case 289: {
                Stmt a = (Stmt) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Do(pos(), a, b));
                break;
            }
     
            //
            // Rule 290:  ForStatement ::= BasicForStatement
            //
            case 290:
                break;
 
            //
            // Rule 291:  ForStatement ::= EnhancedForStatement
            //
            case 291:
                break;
 
            //
            // Rule 292:  BasicForStatement ::= for LPAREN ForInitopt SEMICOLON Expressionopt SEMICOLON ForUpdateopt RPAREN Statement
            //
            case 292: {
                List a = (List) btParser.getSym(3);
                Expr b = (Expr) btParser.getSym(5);
                List c = (List) btParser.getSym(7);
                Stmt d = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), a, b, c, d));
                break;
            }
     
            //
            // Rule 293:  ForStatementNoShortIf ::= for LPAREN ForInitopt SEMICOLON Expressionopt SEMICOLON ForUpdateopt RPAREN StatementNoShortIf
            //
            case 293: {
                List a = (List) btParser.getSym(3);
                Expr b = (Expr) btParser.getSym(5);
                List c = (List) btParser.getSym(7);
                Stmt d = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), a, b, c, d));
                break;
            }
     
            //
            // Rule 294:  ForInit ::= StatementExpressionList
            //
            case 294:
                break;
 
            //
            // Rule 295:  ForInit ::= LocalVariableDeclaration
            //
            case 295: {
                List l = new TypedList(new LinkedList(), ForInit.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 296:  ForUpdate ::= StatementExpressionList
            //
            case 296:
                break;
 
            //
            // Rule 297:  StatementExpressionList ::= StatementExpression
            //
            case 297: {
                List l = new TypedList(new LinkedList(), Eval.class, false);
                Expr a = (Expr) btParser.getSym(1);
                l.add(nf.Eval(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 298:  StatementExpressionList ::= StatementExpressionList COMMA StatementExpression
            //
            case 298: {
                List l = (List) btParser.getSym(1);
                Expr a = (Expr) btParser.getSym(3);
                l.add(nf.Eval(pos(), a));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 299:  BreakStatement ::= break identifieropt SEMICOLON
            //
            case 299: {
                Name a = (Name) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.Break(pos()));
                else btParser.setSym1(nf.Break(pos(), a.toString()));
                break;
            }
     
            //
            // Rule 300:  ContinueStatement ::= continue identifieropt SEMICOLON
            //
            case 300: {
                Name a = (Name) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.Continue(pos()));
                else btParser.setSym1(nf.Continue(pos(), a.toString()));
                break;
            }
     
            //
            // Rule 301:  ReturnStatement ::= return Expressionopt SEMICOLON
            //
            case 301: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Return(pos(), a));
                break;
            }
     
            //
            // Rule 302:  ThrowStatement ::= throw Expression SEMICOLON
            //
            case 302: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Throw(pos(), a));
                break;
            }
     
            //
            // Rule 303:  SynchronizedStatement ::= synchronized LPAREN Expression RPAREN Block
            //
            case 303: {
                Expr a = (Expr) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Synchronized(pos(), a, b));
                break;
            }
     
            //
            // Rule 304:  TryStatement ::= try Block Catches
            //
            case 304: {
                Block a = (Block) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Try(pos(), a, b));
                break;
            }
     
            //
            // Rule 305:  TryStatement ::= try Block Catchesopt Finally
            //
            case 305: {
                Block a = (Block) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                Block c = (Block) btParser.getSym(4);
                btParser.setSym1(nf.Try(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 306:  Catches ::= CatchClause
            //
            case 306: {
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 307:  Catches ::= Catches CatchClause
            //
            case 307: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 308:  CatchClause ::= catch LPAREN FormalParameter RPAREN Block
            //
            case 308: {
                Formal a = (Formal) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Catch(pos(), a, b));
                break;
            }
     
            //
            // Rule 309:  Finally ::= finally Block
            //
            case 309: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 310:  Primary ::= PrimaryNoNewArray
            //
            case 310:
                break;
 
            //
            // Rule 311:  Primary ::= ArrayCreationExpression
            //
            case 311:
                break;
 
            //
            // Rule 312:  PrimaryNoNewArray ::= Literal
            //
            case 312:
                break;
 
            //
            // Rule 313:  PrimaryNoNewArray ::= Type DOT class
            //
            case 313: {
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
            // Rule 314:  PrimaryNoNewArray ::= void DOT class
            //
            case 314: {
                btParser.setSym1(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(btParser.getToken(1)), ts.Void())));
                break;
            }
     
            //
            // Rule 315:  PrimaryNoNewArray ::= this
            //
            case 315: {
                btParser.setSym1(nf.This(pos()));
                break;
            }
     
            //
            // Rule 316:  PrimaryNoNewArray ::= ClassName DOT this
            //
            case 316: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(nf.This(pos(), a.toType()));
                break;
            }
     
            //
            // Rule 317:  PrimaryNoNewArray ::= LPAREN Expression RPAREN
            //
            case 317: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 318:  PrimaryNoNewArray ::= ClassInstanceCreationExpression
            //
            case 318:
                break;
 
            //
            // Rule 319:  PrimaryNoNewArray ::= FieldAccess
            //
            case 319:
                break;
 
            //
            // Rule 320:  PrimaryNoNewArray ::= MethodInvocation
            //
            case 320:
                break;
 
            //
            // Rule 321:  PrimaryNoNewArray ::= ArrayAccess
            //
            case 321:
                break;
 
            //
            // Rule 322:  Literal ::= IntegerLiteral
            //
            case 322: {
                polyglot.lex.IntegerLiteral a = int_lit(btParser.getToken(1));
                btParser.setSym1(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 323:  Literal ::= LongLiteral
            //
            case 323: {
                polyglot.lex.LongLiteral a = long_lit(btParser.getToken(1));
                btParser.setSym1(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 324:  Literal ::= FloatingPointLiteral
            //
            case 324: {
                polyglot.lex.FloatLiteral a = float_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 325:  Literal ::= DoubleLiteral
            //
            case 325: {
                polyglot.lex.DoubleLiteral a = double_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 326:  Literal ::= BooleanLiteral
            //
            case 326: {
                polyglot.lex.BooleanLiteral a = boolean_lit(btParser.getToken(1));
                btParser.setSym1(nf.BooleanLit(pos(), a.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 327:  Literal ::= CharacterLiteral
            //
            case 327: {
                polyglot.lex.CharacterLiteral a = char_lit(btParser.getToken(1));
                btParser.setSym1(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 328:  Literal ::= StringLiteral
            //
            case 328: {
                String s = prsStream.getName(btParser.getToken(1));
                btParser.setSym1(nf.StringLit(pos(), s.substring(1, s.length() - 1)));
                break;
            }
     
            //
            // Rule 329:  Literal ::= null
            //
            case 329: {
                btParser.setSym1(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 330:  BooleanLiteral ::= true
            //
            case 330:
                break;
 
            //
            // Rule 331:  BooleanLiteral ::= false
            //
            case 331:
                break;
 
            //
            // Rule 332:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 332: {
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
            // Rule 333:  ClassInstanceCreationExpression ::= Primary DOT new identifier LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 333: {
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
            // Rule 334:  ClassInstanceCreationExpression ::= AmbiguousName DOT new identifier LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 334: {
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
            // Rule 335:  ArgumentList ::= Expression
            //
            case 335: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 336:  ArgumentList ::= ArgumentList COMMA Expression
            //
            case 336: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 337:  DimExprs ::= DimExpr
            //
            case 337: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 338:  DimExprs ::= DimExprs DimExpr
            //
            case 338: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 339:  DimExpr ::= LBRACKET Expression RBRACKET
            //
            case 339: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(a.position(pos()));
                break;
            }
     
            //
            // Rule 340:  Dims ::= LBRACKET RBRACKET
            //
            case 340: {
                btParser.setSym1(new Integer(1));
                break;
            }
     
            //
            // Rule 341:  Dims ::= Dims LBRACKET RBRACKET
            //
            case 341: {
                Integer a = (Integer) btParser.getSym(1);
                btParser.setSym1(new Integer(a.intValue() + 1));
                break;
            }
     
            //
            // Rule 342:  FieldAccess ::= Primary DOT identifier
            //
            case 342: {
                Expr a = (Expr) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(), a, b.getIdentifier()));
                break;
            }
     
            //
            // Rule 343:  FieldAccess ::= super DOT identifier
            //
            case 343: {
                polyglot.lex.Identifier a = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken())), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 344:  FieldAccess ::= ClassName DOT super DOT identifier
            //
            case 344: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier()));
                break;
            }
     
            //
            // Rule 345:  MethodInvocation ::= MethodName LPAREN ArgumentListopt RPAREN
            //
            case 345: {
                Name a = (Name) btParser.getSym(1);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Call(pos(), a.prefix == null ? null : a.prefix.toReceiver(), a.name, b));
                break;
            }
     
            //
            // Rule 346:  MethodInvocation ::= Primary DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 346: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), a, b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 347:  MethodInvocation ::= super DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 347: {
//vj                    assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken())), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 348:  MethodInvocation ::= ClassName DOT super DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 348: {
                Name a = (Name) btParser.getSym(1);
//vj                    assert(btParser.getSym(5) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(5));
                List c = (List) btParser.getSym(7);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 349:  PostfixExpression ::= Primary
            //
            case 349:
                break;
 
            //
            // Rule 350:  PostfixExpression ::= ExpressionName
            //
            case 350: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 351:  PostfixExpression ::= PostIncrementExpression
            //
            case 351:
                break;
 
            //
            // Rule 352:  PostfixExpression ::= PostDecrementExpression
            //
            case 352:
                break;
 
            //
            // Rule 353:  PostIncrementExpression ::= PostfixExpression PLUS_PLUS
            //
            case 353: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 354:  PostDecrementExpression ::= PostfixExpression MINUS_MINUS
            //
            case 354: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 355:  UnaryExpression ::= PreIncrementExpression
            //
            case 355:
                break;
 
            //
            // Rule 356:  UnaryExpression ::= PreDecrementExpression
            //
            case 356:
                break;
 
            //
            // Rule 357:  UnaryExpression ::= PLUS UnaryExpression
            //
            case 357: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.POS, a));
                break;
            }
     
            //
            // Rule 358:  UnaryExpression ::= MINUS UnaryExpression
            //
            case 358: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NEG, a));
                break;
            }
     
            //
            // Rule 360:  PreIncrementExpression ::= PLUS_PLUS UnaryExpression
            //
            case 360: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_INC, a));
                break;
            }
     
            //
            // Rule 361:  PreDecrementExpression ::= MINUS_MINUS UnaryExpression
            //
            case 361: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_DEC, a));
                break;
            }
     
            //
            // Rule 362:  UnaryExpressionNotPlusMinus ::= PostfixExpression
            //
            case 362:
                break;
 
            //
            // Rule 363:  UnaryExpressionNotPlusMinus ::= TWIDDLE UnaryExpression
            //
            case 363: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.BIT_NOT, a));
                break;
            }
     
            //
            // Rule 364:  UnaryExpressionNotPlusMinus ::= NOT UnaryExpression
            //
            case 364: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NOT, a));
                break;
            }
     
            //
            // Rule 366:  MultiplicativeExpression ::= UnaryExpression
            //
            case 366:
                break;
 
            //
            // Rule 367:  MultiplicativeExpression ::= MultiplicativeExpression MULTIPLY UnaryExpression
            //
            case 367: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MUL, b));
                break;
            }
     
            //
            // Rule 368:  MultiplicativeExpression ::= MultiplicativeExpression DIVIDE UnaryExpression
            //
            case 368: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.DIV, b));
                break;
            }
     
            //
            // Rule 369:  MultiplicativeExpression ::= MultiplicativeExpression REMAINDER UnaryExpression
            //
            case 369: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MOD, b));
                break;
            }
     
            //
            // Rule 370:  AdditiveExpression ::= MultiplicativeExpression
            //
            case 370:
                break;
 
            //
            // Rule 371:  AdditiveExpression ::= AdditiveExpression PLUS MultiplicativeExpression
            //
            case 371: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.ADD, b));
                break;
            }
     
            //
            // Rule 372:  AdditiveExpression ::= AdditiveExpression MINUS MultiplicativeExpression
            //
            case 372: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SUB, b));
                break;
            }
     
            //
            // Rule 373:  ShiftExpression ::= AdditiveExpression
            //
            case 373:
                break;
 
            //
            // Rule 374:  ShiftExpression ::= ShiftExpression LEFT_SHIFT AdditiveExpression
            //
            case 374: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHL, b));
                break;
            }
     
            //
            // Rule 375:  ShiftExpression ::= ShiftExpression GREATER GREATER AdditiveExpression
            //
            case 375: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHR, b));
                break;
            }
     
            //
            // Rule 376:  ShiftExpression ::= ShiftExpression GREATER GREATER GREATER AdditiveExpression
            //
            case 376: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Binary(pos(), a, Binary.USHR, b));
                break;
            }
     
            //
            // Rule 377:  RelationalExpression ::= ShiftExpression
            //
            case 377:
                break;
 
            //
            // Rule 378:  RelationalExpression ::= RelationalExpression LESS ShiftExpression
            //
            case 378: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LT, b));
                break;
            }
     
            //
            // Rule 379:  RelationalExpression ::= RelationalExpression GREATER ShiftExpression
            //
            case 379: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GT, b));
                break;
            }
     
            //
            // Rule 380:  RelationalExpression ::= RelationalExpression LESS_EQUAL ShiftExpression
            //
            case 380: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LE, b));
                break;
            }
     
            //
            // Rule 381:  RelationalExpression ::= RelationalExpression GREATER EQUAL ShiftExpression
            //
            case 381: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GE, b));
                break;
            }
     
            //
            // Rule 382:  EqualityExpression ::= RelationalExpression
            //
            case 382:
                break;
 
            //
            // Rule 383:  EqualityExpression ::= EqualityExpression EQUAL_EQUAL RelationalExpression
            //
            case 383: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.EQ, b));
                break;
            }
     
            //
            // Rule 384:  EqualityExpression ::= EqualityExpression NOT_EQUAL RelationalExpression
            //
            case 384: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.NE, b));
                break;
            }
     
            //
            // Rule 385:  AndExpression ::= EqualityExpression
            //
            case 385:
                break;
 
            //
            // Rule 386:  AndExpression ::= AndExpression AND EqualityExpression
            //
            case 386: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_AND, b));
                break;
            }
     
            //
            // Rule 387:  ExclusiveOrExpression ::= AndExpression
            //
            case 387:
                break;
 
            //
            // Rule 388:  ExclusiveOrExpression ::= ExclusiveOrExpression XOR AndExpression
            //
            case 388: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_XOR, b));
                break;
            }
     
            //
            // Rule 389:  InclusiveOrExpression ::= ExclusiveOrExpression
            //
            case 389:
                break;
 
            //
            // Rule 390:  InclusiveOrExpression ::= InclusiveOrExpression OR ExclusiveOrExpression
            //
            case 390: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_OR, b));
                break;
            }
     
            //
            // Rule 391:  ConditionalAndExpression ::= InclusiveOrExpression
            //
            case 391:
                break;
 
            //
            // Rule 392:  ConditionalAndExpression ::= ConditionalAndExpression AND_AND InclusiveOrExpression
            //
            case 392: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_AND, b));
                break;
            }
     
            //
            // Rule 393:  ConditionalOrExpression ::= ConditionalAndExpression
            //
            case 393:
                break;
 
            //
            // Rule 394:  ConditionalOrExpression ::= ConditionalOrExpression OR_OR ConditionalAndExpression
            //
            case 394: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_OR, b));
                break;
            }
     
            //
            // Rule 395:  ConditionalExpression ::= ConditionalOrExpression
            //
            case 395:
                break;
 
            //
            // Rule 396:  ConditionalExpression ::= ConditionalOrExpression QUESTION Expression COLON ConditionalExpression
            //
            case 396: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3),
                     c = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Conditional(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 397:  AssignmentExpression ::= ConditionalExpression
            //
            case 397:
                break;
 
            //
            // Rule 398:  AssignmentExpression ::= Assignment
            //
            case 398:
                break;
 
            //
            // Rule 399:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 399: {
                Expr a = (Expr) btParser.getSym(1);
                Assign.Operator b = (Assign.Operator) btParser.getSym(2);
                Expr c = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Assign(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 400:  LeftHandSide ::= ExpressionName
            //
            case 400: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 401:  LeftHandSide ::= FieldAccess
            //
            case 401:
                break;
 
            //
            // Rule 402:  LeftHandSide ::= ArrayAccess
            //
            case 402:
                break;
 
            //
            // Rule 403:  AssignmentOperator ::= EQUAL
            //
            case 403: {
                btParser.setSym1(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 404:  AssignmentOperator ::= MULTIPLY_EQUAL
            //
            case 404: {
                btParser.setSym1(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 405:  AssignmentOperator ::= DIVIDE_EQUAL
            //
            case 405: {
                btParser.setSym1(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 406:  AssignmentOperator ::= REMAINDER_EQUAL
            //
            case 406: {
                btParser.setSym1(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 407:  AssignmentOperator ::= PLUS_EQUAL
            //
            case 407: {
                btParser.setSym1(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 408:  AssignmentOperator ::= MINUS_EQUAL
            //
            case 408: {
                btParser.setSym1(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 409:  AssignmentOperator ::= LEFT_SHIFT_EQUAL
            //
            case 409: {
                btParser.setSym1(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 410:  AssignmentOperator ::= GREATER GREATER EQUAL
            //
            case 410: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 411:  AssignmentOperator ::= GREATER GREATER GREATER EQUAL
            //
            case 411: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 412:  AssignmentOperator ::= AND_EQUAL
            //
            case 412: {
                btParser.setSym1(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 413:  AssignmentOperator ::= XOR_EQUAL
            //
            case 413: {
                btParser.setSym1(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 414:  AssignmentOperator ::= OR_EQUAL
            //
            case 414: {
                btParser.setSym1(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 415:  Expression ::= AssignmentExpression
            //
            case 415:
                break;
 
            //
            // Rule 416:  ConstantExpression ::= Expression
            //
            case 416:
                break;
 
            //
            // Rule 417:  Dimsopt ::=
            //
            case 417: {
                btParser.setSym1(new Integer(0));
                break;
            }
     
            //
            // Rule 418:  Dimsopt ::= Dims
            //
            case 418:
                break;
 
            //
            // Rule 419:  Catchesopt ::=
            //
            case 419: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 420:  Catchesopt ::= Catches
            //
            case 420:
                break;
 
            //
            // Rule 421:  identifieropt ::=
            //
            case 421:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 422:  identifieropt ::= identifier
            //
            case 422: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 423:  ForUpdateopt ::=
            //
            case 423: {
                btParser.setSym1(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 424:  ForUpdateopt ::= ForUpdate
            //
            case 424:
                break;
 
            //
            // Rule 425:  Expressionopt ::=
            //
            case 425:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 426:  Expressionopt ::= Expression
            //
            case 426:
                break;
 
            //
            // Rule 427:  ForInitopt ::=
            //
            case 427: {
                btParser.setSym1(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 428:  ForInitopt ::= ForInit
            //
            case 428:
                break;
 
            //
            // Rule 429:  SwitchLabelsopt ::=
            //
            case 429: {
                btParser.setSym1(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 430:  SwitchLabelsopt ::= SwitchLabels
            //
            case 430:
                break;
 
            //
            // Rule 431:  SwitchBlockStatementGroupsopt ::=
            //
            case 431: {
                btParser.setSym1(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 432:  SwitchBlockStatementGroupsopt ::= SwitchBlockStatementGroups
            //
            case 432:
                break;
 
            //
            // Rule 433:  VariableModifiersopt ::=
            //
            case 433: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 434:  VariableModifiersopt ::= VariableModifiers
            //
            case 434:
                break;
 
            //
            // Rule 435:  VariableInitializersopt ::=
            //
            case 435:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 436:  VariableInitializersopt ::= VariableInitializers
            //
            case 436:
                break;
 
            //
            // Rule 437:  ElementValuesopt ::=
            //
            case 437:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 438:  ElementValuesopt ::= ElementValues
            //
            case 438:
                bad_rule = 438;
                break;
 
            //
            // Rule 439:  ElementValuePairsopt ::=
            //
            case 439:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 440:  ElementValuePairsopt ::= ElementValuePairs
            //
            case 440:
                bad_rule = 440;
                break;
 
            //
            // Rule 441:  DefaultValueopt ::=
            //
            case 441:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 442:  DefaultValueopt ::= DefaultValue
            //
            case 442:
                break;
 
            //
            // Rule 443:  AnnotationTypeElementDeclarationsopt ::=
            //
            case 443:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 444:  AnnotationTypeElementDeclarationsopt ::= AnnotationTypeElementDeclarations
            //
            case 444:
                bad_rule = 444;
                break;
 
            //
            // Rule 445:  AbstractMethodModifiersopt ::=
            //
            case 445: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 446:  AbstractMethodModifiersopt ::= AbstractMethodModifiers
            //
            case 446:
                break;
 
            //
            // Rule 447:  ConstantModifiersopt ::=
            //
            case 447: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 448:  ConstantModifiersopt ::= ConstantModifiers
            //
            case 448:
                break;
 
            //
            // Rule 449:  InterfaceMemberDeclarationsopt ::=
            //
            case 449: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 450:  InterfaceMemberDeclarationsopt ::= InterfaceMemberDeclarations
            //
            case 450:
                break;
 
            //
            // Rule 451:  ExtendsInterfacesopt ::=
            //
            case 451: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 452:  ExtendsInterfacesopt ::= ExtendsInterfaces
            //
            case 452:
                break;
 
            //
            // Rule 453:  InterfaceModifiersopt ::=
            //
            case 453: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 454:  InterfaceModifiersopt ::= InterfaceModifiers
            //
            case 454:
                break;
 
            //
            // Rule 455:  ClassBodyopt ::=
            //
            case 455:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 456:  ClassBodyopt ::= ClassBody
            //
            case 456:
                break;
 
            //
            // Rule 457:  Argumentsopt ::=
            //
            case 457:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 458:  Argumentsopt ::= Arguments
            //
            case 458:
                bad_rule = 458;
                break;
 
            //
            // Rule 459:  EnumBodyDeclarationsopt ::=
            //
            case 459:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 460:  EnumBodyDeclarationsopt ::= EnumBodyDeclarations
            //
            case 460:
                bad_rule = 460;
                break;
 
            //
            // Rule 461:  ,opt ::=
            //
            case 461:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 462:  ,opt ::= COMMA
            //
            case 462:
                break;
 
            //
            // Rule 463:  EnumConstantsopt ::=
            //
            case 463:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 464:  EnumConstantsopt ::= EnumConstants
            //
            case 464:
                bad_rule = 464;
                break;
 
            //
            // Rule 465:  ArgumentListopt ::=
            //
            case 465: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 466:  ArgumentListopt ::= ArgumentList
            //
            case 466:
                break;
 
            //
            // Rule 467:  BlockStatementsopt ::=
            //
            case 467: {
                btParser.setSym1(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 468:  BlockStatementsopt ::= BlockStatements
            //
            case 468:
                break;
 
            //
            // Rule 469:  ExplicitConstructorInvocationopt ::=
            //
            case 469:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 470:  ExplicitConstructorInvocationopt ::= ExplicitConstructorInvocation
            //
            case 470:
                break;
 
            //
            // Rule 471:  ConstructorModifiersopt ::=
            //
            case 471: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 472:  ConstructorModifiersopt ::= ConstructorModifiers
            //
            case 472:
                break;
 
            //
            // Rule 473:  ...opt ::=
            //
            case 473:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 474:  ...opt ::= ELLIPSIS
            //
            case 474:
                break;
 
            //
            // Rule 475:  FormalParameterListopt ::=
            //
            case 475: {
                btParser.setSym1(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 476:  FormalParameterListopt ::= FormalParameterList
            //
            case 476:
                break;
 
            //
            // Rule 477:  Throwsopt ::=
            //
            case 477: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 478:  Throwsopt ::= Throws
            //
            case 478:
                break;
 
            //
            // Rule 479:  MethodModifiersopt ::=
            //
            case 479: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 480:  MethodModifiersopt ::= MethodModifiers
            //
            case 480:
                break;
 
            //
            // Rule 481:  FieldModifiersopt ::=
            //
            case 481: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 482:  FieldModifiersopt ::= FieldModifiers
            //
            case 482:
                break;
 
            //
            // Rule 483:  ClassBodyDeclarationsopt ::=
            //
            case 483: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 484:  ClassBodyDeclarationsopt ::= ClassBodyDeclarations
            //
            case 484:
                break;
 
            //
            // Rule 485:  Interfacesopt ::=
            //
            case 485: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 486:  Interfacesopt ::= Interfaces
            //
            case 486:
                break;
 
            //
            // Rule 487:  Superopt ::=
            //
            case 487:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 488:  Superopt ::= Super
            //
            case 488:
                break;
 
            //
            // Rule 489:  TypeParametersopt ::=
            //
            case 489:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 490:  TypeParametersopt ::= TypeParameters
            //
            case 490:
                break;
 
            //
            // Rule 491:  ClassModifiersopt ::=
            //
            case 491: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 492:  ClassModifiersopt ::= ClassModifiers
            //
            case 492:
                break;
 
            //
            // Rule 493:  Annotationsopt ::=
            //
            case 493:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 494:  Annotationsopt ::= Annotations
            //
            case 494:
                bad_rule = 494;
                break;
 
            //
            // Rule 495:  TypeDeclarationsopt ::=
            //
            case 495: {
                btParser.setSym1(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 496:  TypeDeclarationsopt ::= TypeDeclarations
            //
            case 496:
                break;
 
            //
            // Rule 497:  ImportDeclarationsopt ::=
            //
            case 497: {
                btParser.setSym1(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 498:  ImportDeclarationsopt ::= ImportDeclarations
            //
            case 498:
                break;
 
            //
            // Rule 499:  PackageDeclarationopt ::=
            //
            case 499:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 500:  PackageDeclarationopt ::= PackageDeclaration
            //
            case 500:
                break;
 
            //
            // Rule 501:  WildcardBoundsOpt ::=
            //
            case 501:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 502:  WildcardBoundsOpt ::= WildcardBounds
            //
            case 502:
                bad_rule = 502;
                break;
 
            //
            // Rule 503:  AdditionalBoundListopt ::=
            //
            case 503:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 504:  AdditionalBoundListopt ::= AdditionalBoundList
            //
            case 504:
                bad_rule = 504;
                break;
 
            //
            // Rule 505:  TypeBoundopt ::=
            //
            case 505:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 506:  TypeBoundopt ::= TypeBound
            //
            case 506:
                bad_rule = 506;
                break;
 
            //
            // Rule 507:  TypeArgumentsopt ::=
            //
            case 507:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 508:  TypeArgumentsopt ::= TypeArguments
            //
            case 508:
                bad_rule = 508;
                break;
 
            //
            // Rule 509:  Type ::= DataType PlaceTypeSpecifieropt
            //
            case 509: {
           // Just parse the placetype and drop it for now.
                    break;
            }
         
            //
            // Rule 510:  Type ::= nullable LESS Type GREATER
            //
            case 510: {
                TypeNode a = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Nullable(pos(), a));
                          break;
            }
              
            //
            // Rule 511:  Type ::= future LESS Type GREATER
            //
            case 511: {
                TypeNode a = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Future(pos(), a));
                          break;
            }

              
            //
            // Rule 512:  DataType ::= PrimitiveType
            //
            case 512:
                break; 
            
               
            //
            // Rule 513:  DataType ::= ClassOrInterfaceType
            //
            case 513:
                break; 
            
               
            //
            // Rule 514:  DataType ::= ArrayType
            //
            case 514:
                break; 
             
            //
            // Rule 515:  PlaceTypeSpecifier ::= AT PlaceType
            //
            case 515:
                break; 
 
            //
            // Rule 516:  PlaceType ::= place
            //
            case 516:
                break; 
 
            //
            // Rule 517:  PlaceType ::= activity
            //
            case 517:
                break; 
 
            //
            // Rule 518:  PlaceType ::= method
            //
            case 518:
                break; 
 
            //
            // Rule 519:  PlaceType ::= current
            //
            case 519:
                break; 
 
            //
            // Rule 520:  PlaceType ::= PlaceExpression
            //
            case 520:
                break; 
 
            //
            // Rule 521:  ClassOrInterfaceType ::= TypeName DepParametersopt
            //
            case 521: { 
            Name a = (Name) btParser.getSym(1);
            TypeNode t = a.toType();
            DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
            btParser.setSym1(b == null ? t : nf.ParametricTypeNode(pos(), t, b));
                    break;
            }
        
            //
            // Rule 522:  DepParameters ::= LPAREN DepParameterExpr RPAREN
            //
            case 522:
                break; 
        
            //
            // Rule 523:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 523: {
             List a = (List) btParser.getSym(1);                           
             Expr b = (Expr) btParser.getSym(2);
             btParser.setSym1(nf.DepParameterExpr(pos(),a,b));
                    break;
            }
        
            //
            // Rule 524:  DepParameterExpr ::= WhereClause
            //
            case 524: {
             Expr b = (Expr) btParser.getSym(1);
             btParser.setSym1(nf.DepParameterExpr(pos(), null, b));
                    break;
            }
        
            //
            // Rule 525:  WhereClause ::= COLON Expression
            //
            case 525:
                break; 
 
            //
            // Rule 527:  X10ArrayType ::= Type LBRACKET DOT RBRACKET
            //
            case 527: {
                TypeNode a = (TypeNode) btParser.getSym(1);
                TypeNode t = nf.X10ArrayTypeNode(pos(), a, false, null);
                btParser.setSym1(t);
                break;
            }
     
            //
            // Rule 528:  X10ArrayType ::= Type reference LBRACKET DOT RBRACKET
            //
            case 528: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, false, null));
                    break;
            }
        
            //
            // Rule 529:  X10ArrayType ::= Type value LBRACKET DOT RBRACKET
            //
            case 529: {
             TypeNode a = (TypeNode) btParser.getSym(1);
                btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, true, null));
                    break;
            }
        
            //
            // Rule 530:  X10ArrayType ::= Type LBRACKET DepParameterExpr RBRACKET
            //
            case 530: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, false, b));
                    break;
            }
        
            //
            // Rule 531:  X10ArrayType ::= Type reference LBRACKET DepParameterExpr RBRACKET
            //
            case 531: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, false, b));
                    break;
            }
        
            //
            // Rule 532:  X10ArrayType ::= Type value LBRACKET DepParameterExpr RBRACKET
            //
            case 532: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, true, b));
                    break;
            }
        
            //
            // Rule 533:  ObjectKind ::= value
            //
            case 533:
                bad_rule = 533;
                break; 
 
            //
            // Rule 534:  ObjectKind ::= reference
            //
            case 534:
                bad_rule = 534;
                break; 
 
            //
            // Rule 535:  MethodModifier ::= atomic
            //
            case 535: {
                btParser.setSym1(Flags.ATOMIC);
                break;
            }
     
            //
            // Rule 536:  MethodModifier ::= extern
            //
            case 536: {
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 537:  ClassDeclaration ::= ValueClassDeclaration
            //
            case 537:
                break; 
 
            //
            // Rule 538:  ValueClassDeclaration ::= ClassModifiersopt value identifier Superopt Interfacesopt ClassBody
            //
            case 538: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                TypeNode c = (TypeNode) btParser.getSym(4);
                List d = (List) btParser.getSym(5);
                ClassBody e = (ClassBody) btParser.getSym(6);
                btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
                break;
            }  
            //
            // Rule 539:  ValueClassDeclaration ::= ClassModifiersopt value class identifier Superopt Interfacesopt ClassBody
            //
            case 539: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(4));
                TypeNode c = (TypeNode) btParser.getSym(5);
                List d = (List) btParser.getSym(6);
                ClassBody e = (ClassBody) btParser.getSym(7);
                btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
                break;
            }   
            //
            // Rule 540:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt LBRACKET RBRACKET ArrayInitializer
            //
            case 540: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                ArrayInit d = (ArrayInit) btParser.getSym(6);
                // btParser.setSym1(nf.ArrayConstructor(pos(), a, false, null, d));
                btParser.setSym1(nf.NewArray(pos(), a, 1, d));
                break;
            }
     
            //
            // Rule 541:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt LBRACKET Expression RBRACKET
            //
            case 541: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                boolean unsafe = (btParser.getSym(3) != null);
                Expr c = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, false, c, null));
                break;
            }
     
            //
            // Rule 542:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt LBRACKET Expression RBRACKET Expression
            //
            case 542: {
     TypeNode a = (TypeNode) btParser.getSym(2);
     boolean unsafe = (btParser.getSym(3) != null);
     Expr distr = (Expr) btParser.getSym(5);
     Expr initializer = (Expr) btParser.getSym(7);
    btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, false, distr, initializer));
                break;
            }
     
            //
            // Rule 543:  ArrayCreationExpression ::= new ArrayBaseType Unsafeopt LBRACKET Expression RBRACKET LPAREN FormalParameter RPAREN MethodBody
            //
            case 543: {
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
            // Rule 544:  ArrayCreationExpression ::= new ArrayBaseType value Unsafeopt LBRACKET Expression RBRACKET
            //
            case 544: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                boolean unsafe = (btParser.getSym(3) != null);
                Expr c = (Expr) btParser.getSym(6);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, true, c, null));
                break;
            }
     
            //
            // Rule 545:  ArrayCreationExpression ::= new ArrayBaseType value Unsafeopt LBRACKET Expression RBRACKET Expression
            //
            case 545: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                boolean unsafe = (btParser.getSym(4) != null);
                Expr c = (Expr) btParser.getSym(6);
                Expr d = (Expr) btParser.getSym(8);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, true, c, d));
                break;
            }
               
            //
            // Rule 546:  X10ArrayInitializer ::= Expression
            //
            case 546:
                break;
                     // Sigh this is not trivial to do just yet :-(
           
            //
            // Rule 547:  X10ArrayInitializer ::= LPAREN FormalParameter RPAREN MethodBody
            //
            case 547:
                bad_rule = 547;
                break;
                     
            //
            // Rule 548:  ArrayBaseType ::= PrimitiveType
            //
            case 548:
                break;
          
            
            //
            // Rule 549:  ArrayBaseType ::= ClassOrInterfaceType
            //
            case 549:
                break;
          
            //
            // Rule 550:  ArrayAccess ::= ExpressionName LBRACKET ArgumentList RBRACKET
            //
            case 550: {
           Name e = (Name) btParser.getSym(1);
           List b = (List) btParser.getSym(3);
           if (b.size() == 1)
           btParser.setSym1(nf.X10ArrayAccess1(pos(), e.toExpr(), (Expr) b.get(0)));
           else btParser.setSym1(nf.X10ArrayAccess(pos(), e.toExpr(), b));
                     break;
            }
         
            //
            // Rule 551:  ArrayAccess ::= PrimaryNoNewArray LBRACKET ArgumentList RBRACKET
            //
            case 551: { 
           Expr a = (Expr) btParser.getSym(1);
           List b = (List) btParser.getSym(3);
           if (b.size() == 1)
           btParser.setSym1(nf.X10ArrayAccess1(pos(), a, (Expr) b.get(0)));
           else btParser.setSym1(nf.X10ArrayAccess(pos(), a, b));
                    break;
            }
        
            //
            // Rule 552:  Statement ::= NowStatement
            //
            case 552:
                break; 
 
            //
            // Rule 553:  Statement ::= ClockedStatement
            //
            case 553:
                break; 
 
            //
            // Rule 554:  Statement ::= AsyncStatement
            //
            case 554:
                break; 
 
            //
            // Rule 555:  Statement ::= AtomicStatement
            //
            case 555:
                break; 
 
            //
            // Rule 556:  Statement ::= WhenStatement
            //
            case 556:
                break; 
 
            //
            // Rule 557:  Statement ::= ForEachStatement
            //
            case 557:
                break; 
 
            //
            // Rule 558:  Statement ::= AtEachStatement
            //
            case 558:
                break; 
 
            //
            // Rule 559:  Statement ::= FinishStatement
            //
            case 559:
                break; 
 
            //
            // Rule 560:  StatementWithoutTrailingSubstatement ::= NextStatement
            //
            case 560:
                break; 
 
            //
            // Rule 561:  StatementWithoutTrailingSubstatement ::= AwaitStatement
            //
            case 561:
                break; 
 
            //
            // Rule 562:  StatementNoShortIf ::= NowStatementNoShortIf
            //
            case 562:
                break; 
 
            //
            // Rule 563:  StatementNoShortIf ::= ClockedStatementNoShortIf
            //
            case 563:
                break; 
 
            //
            // Rule 564:  StatementNoShortIf ::= AsyncStatementNoShortIf
            //
            case 564:
                break; 
 
            //
            // Rule 565:  StatementNoShortIf ::= AtomicStatementNoShortIf
            //
            case 565:
                break; 
 
            //
            // Rule 566:  StatementNoShortIf ::= WhenStatementNoShortIf
            //
            case 566:
                break; 
 
            //
            // Rule 567:  StatementNoShortIf ::= ForEachStatementNoShortIf
            //
            case 567:
                break; 
 
            //
            // Rule 568:  StatementNoShortIf ::= AtEachStatementNoShortIf
            //
            case 568:
                break; 
 
            //
            // Rule 569:  StatementNoShortIf ::= FinishStatementNoShortIf
            //
            case 569:
                break; 
 
            //
            // Rule 570:  NowStatement ::= now LPAREN Clock RPAREN Statement
            //
            case 570: {
                Name a = (Name) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Now(pos(), a.toExpr(), b));
                break;
            }
     
            //
            // Rule 571:  ClockedStatement ::= clocked LPAREN ClockList RPAREN Statement
            //
            case 571: {
                List a = (List) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), a, b));
                break;
            }
     
            //
            // Rule 572:  AsyncStatement ::= async PlaceExpressionSingleListopt Statement
            //
            case 572: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Async(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 573:  AsyncStatement ::= async LPAREN here RPAREN Statement
            //
            case 573: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Async(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 574:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 574: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Atomic(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 575:  AtomicStatement ::= atomic LPAREN here RPAREN Statement
            //
            case 575: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Atomic(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 576:  WhenStatement ::= when LPAREN Expression RPAREN Statement
            //
            case 576: {
                Expr e = (Expr) btParser.getSym(3);
                Stmt s = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.When(pos(), e,s));
                break;
            }
     
            //
            // Rule 577:  WhenStatement ::= WhenStatement or LPAREN Expression RPAREN Statement
            //
            case 577: {
                When w = (When) btParser.getSym(1);
                Expr e = (Expr) btParser.getSym(4);
                Stmt s = (Stmt) btParser.getSym(6);
                When.Branch wb = nf.WhenBranch(pos(btParser.getFirstToken(2), btParser.getLastToken(6)), e, s);
                w.add(wb);
                btParser.setSym1(w);
                break;
            }
     
            //
            // Rule 578:  ForEachStatement ::= foreach LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 578: {
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
            // Rule 579:  AtEachStatement ::= ateach LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 579: {
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
            // Rule 580:  EnhancedForStatement ::= for LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 580: {
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
            // Rule 581:  FinishStatement ::= finish Statement
            //
            case 581: {
                Stmt b = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Finish(pos(),  b));
                break;
            }
     
            //
            // Rule 582:  NowStatementNoShortIf ::= now LPAREN Clock RPAREN StatementNoShortIf
            //
            case 582: {
                Name a = (Name) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Now(pos(), a.toExpr(), b));
                break;
            }
     
            //
            // Rule 583:  ClockedStatementNoShortIf ::= clocked LPAREN ClockList RPAREN StatementNoShortIf
            //
            case 583: {
                List a = (List) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), a, b));
                break;
            }
     
            //
            // Rule 584:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt StatementNoShortIf
            //
            case 584: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Async(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 585:  AsyncStatementNoShortIf ::= async LPAREN here RPAREN StatementNoShortIf
            //
            case 585: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Async(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 586:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 586: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Atomic(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 587:  AtomicStatementNoShortIf ::= atomic LPAREN here RPAREN StatementNoShortIf
            //
            case 587: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Atomic(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 588:  WhenStatementNoShortIf ::= when LPAREN Expression RPAREN StatementNoShortIf
            //
            case 588: {
                Expr e = (Expr) btParser.getSym(3);
                Stmt s = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.When(pos(), e,s));
                break;
            }
     
            //
            // Rule 589:  WhenStatementNoShortIf ::= WhenStatement or LPAREN Expression RPAREN StatementNoShortIf
            //
            case 589: {
                When w = (When) btParser.getSym(1);
                Expr e = (Expr) btParser.getSym(4);
                Stmt s = (Stmt) btParser.getSym(6);
                When.Branch wb = nf.WhenBranch(pos(btParser.getFirstToken(2), btParser.getLastToken(6)), e, s);
                w.add(wb);
                btParser.setSym1(w);
                break;
            }
     
            //
            // Rule 590:  ForEachStatementNoShortIf ::= foreach LPAREN FormalParameter COLON Expression RPAREN StatementNoShortIf
            //
            case 590: {
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
            // Rule 591:  AtEachStatementNoShortIf ::= ateach LPAREN FormalParameter COLON Expression RPAREN StatementNoShortIf
            //
            case 591: {
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
            // Rule 592:  EnhancedForStatementNoShortIf ::= for LPAREN FormalParameter COLON Expression RPAREN StatementNoShortIf
            //
            case 592: {
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
            // Rule 593:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 593: {
                Stmt b = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Finish(pos(),  b));
                break;
            }
     
            //
            // Rule 594:  PlaceExpressionSingleList ::= LPAREN PlaceExpression RPAREN
            //
            case 594: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 595:  PlaceExpression ::= here
            //
            case 595: {
                  btParser.setSym1(nf.Here(pos(btParser.getFirstToken())));
                break;
            }
     
            //
            // Rule 596:  PlaceExpression ::= this
            //
            case 596: {
                btParser.setSym1(nf.Field(pos(btParser.getFirstToken()), nf.This(pos(btParser.getFirstToken())), "place"));
                break;
            }
     
            //
            // Rule 597:  PlaceExpression ::= Expression
            //
            case 597:
                break;
     
            //
            // Rule 598:  PlaceExpression ::= ArrayAccess
            //
            case 598:
                bad_rule = 598;
                break; 
 
            //
            // Rule 599:  NextStatement ::= next SEMICOLON
            //
            case 599: {
                btParser.setSym1(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 600:  AwaitStatement ::= await Expression SEMICOLON
            //
            case 600: { 
         Expr e = (Expr) btParser.getSym(2);
         btParser.setSym1(nf.Await(pos(), e));
                 break;
            }
     
            //
            // Rule 601:  ClockList ::= Clock
            //
            case 601: {
                Name c = (Name) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(c.toExpr());
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 602:  ClockList ::= ClockList COMMA Clock
            //
            case 602: {
                List l = (List) btParser.getSym(1);
                Name c = (Name) btParser.getSym(3);
                l.add(c.toExpr());
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 603:  Clock ::= identifier
            //
            case 603: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 604:  CastExpression ::= LPAREN Type RPAREN UnaryExpressionNotPlusMinus
            //
            case 604: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Cast(pos(), a, b));
                break;
            }
     
            //
            // Rule 605:  MethodInvocation ::= Primary ARROW identifier LPAREN ArgumentListopt RPAREN
            //
            case 605: { 
          Expr a = (Expr) btParser.getSym(1);
          polyglot.lex.Identifier b = id(btParser.getToken(3));
          List c = (List) btParser.getSym(5);
          btParser.setSym1(nf.RemoteCall(pos(), a, b.getIdentifier(), c));
                 break;
            } 
     
            //
            // Rule 606:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 606: {
                Expr a = (Expr) btParser.getSym(1);
                TypeNode b = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Instanceof(pos(), a, b));
                break;
            }
     
            //
            // Rule 607:  ExpressionName ::= here
            //
            case 607: {
          btParser.setSym1(new Name(nf, ts, pos(), "here"){
              public Expr toExpr() {
                 return ((X10NodeFactory) nf).Here(pos);
              }
           });

                  break;
            }
       
            //
            // Rule 608:  IdentifierList ::= IdentifierList COMMA identifier
            //
            case 608: { 
       List l = (List) btParser.getSym(1);
       polyglot.lex.Identifier a = id(btParser.getToken(3));
       l.add(new Name(nf, ts, pos(), a.getIdentifier()));
       btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 609:  IdentifierList ::= identifier
            //
            case 609: {
       List l = new LinkedList();
       polyglot.lex.Identifier a = id(btParser.getToken(1));
       l.add(new Name(nf, ts, pos(), a.getIdentifier()));
       btParser.setSym1(l);
                 break;
            }
     
            //
            // Rule 610:  Primary ::= FutureExpression
            //
            case 610:
                break; 
 
            //
            // Rule 611:  Primary ::= LBRACKET ArgumentList RBRACKET
            //
            case 611: {
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
            // Rule 612:  AssignmentExpression ::= Expression ARROW Expression
            //
            case 612: {
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
        List l = new LinkedList();
        l.add(a);
        l.add(b);
        Call call = nf.Call(pos(), x10LangDistributionFactoryConstant.prefix.toReceiver(), "constant", l);
        btParser.setSym1(call);
               break;
            }
     
            //
            // Rule 613:  Primary ::= Expression COLON Expression
            //
            case 613: {

        Expr a = (Expr) btParser.getSym(1);
        Expr b = (Expr) btParser.getSym(3);
        Name x10 = new Name(nf, ts, pos(), "x10");
        Name x10Lang = new Name(nf, ts, pos(), x10, "lang");

        Name x10LangRegion = new Name(nf, ts, pos(), x10Lang, "region");
        Name x10LangRegionFactory = new Name(nf, ts, pos(), x10LangRegion, "factory");
        Name x10LangRegionFactoryRegion = new Name(nf, ts, pos(), x10LangRegionFactory, "region");
        List l = new LinkedList();
        l.add(a);
        l.add(b);
        Call regionCall = nf.Call(pos(), x10LangRegionFactoryRegion.prefix.toReceiver(), "region", l);
        btParser.setSym1(regionCall);
               break;
            }
     
            //
            // Rule 614:  FutureExpression ::= future PlaceExpressionSingleListopt LBRACE Expression RBRACE
            //
            case 614: {
                Expr e1 = (Expr) btParser.getSym(2),
                     e2 = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Future(pos(), (e1 == null ? nf.Here(pos(btParser.getFirstToken())) : e1), e2));
                break;
            }
     
            //
            // Rule 615:  FutureExpression ::= future LPAREN here RPAREN LBRACE Expression RBRACE
            //
            case 615: {
                Expr e2 = (Expr) btParser.getSym(6);
                btParser.setSym1(nf.Future(pos(), nf.Here(pos(btParser.getFirstToken(3))), e2));
                break;
            }
     
            //
            // Rule 616:  FieldModifier ::= mutable
            //
            case 616: {
                btParser.setSym1(Flags.MUTABLE);
                break;
            }
     
            //
            // Rule 617:  FieldModifier ::= const
            //
            case 617: {
                btParser.setSym1(Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL));
                break;
            }
     
            //
            // Rule 618:  FunExpression ::= fun Type LPAREN FormalParameterListopt RPAREN LBRACE Expression RBRACE
            //
            case 618:
                bad_rule = 618;
                break; 
 
            //
            // Rule 619:  MethodInvocation ::= MethodName LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 619:
                bad_rule = 619;
                break; 
 
            //
            // Rule 620:  MethodInvocation ::= Primary DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 620:
                bad_rule = 620;
                break; 
 
            //
            // Rule 621:  MethodInvocation ::= super DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 621:
                bad_rule = 621;
                break; 
 
            //
            // Rule 622:  MethodInvocation ::= ClassName DOT super DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 622:
                bad_rule = 622;
                break; 
 
            //
            // Rule 623:  MethodInvocation ::= TypeName DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 623:
                bad_rule = 623;
                break; 
 
            //
            // Rule 624:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 624:
                bad_rule = 624;
                break; 
 
            //
            // Rule 625:  ClassInstanceCreationExpression ::= Primary DOT new identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 625:
                bad_rule = 625;
                break; 
 
            //
            // Rule 626:  ClassInstanceCreationExpression ::= AmbiguousName DOT new identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 626:
                bad_rule = 626;
                break; 
 
            //
            // Rule 627:  PlaceTypeSpecifieropt ::=
            //
            case 627:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 628:  PlaceTypeSpecifieropt ::= PlaceTypeSpecifier
            //
            case 628:
                break; 
 
            //
            // Rule 629:  DepParametersopt ::=
            //
            case 629:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 630:  DepParametersopt ::= DepParameters
            //
            case 630:
                break; 
 
            //
            // Rule 631:  WhereClauseopt ::=
            //
            case 631:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 632:  WhereClauseopt ::= WhereClause
            //
            case 632:
                break; 
 
            //
            // Rule 633:  ObjectKindopt ::=
            //
            case 633:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 634:  ObjectKindopt ::= ObjectKind
            //
            case 634:
                break; 
 
            //
            // Rule 635:  ArrayInitializeropt ::=
            //
            case 635:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 636:  ArrayInitializeropt ::= ArrayInitializer
            //
            case 636:
                break; 
 
            //
            // Rule 637:  PlaceExpressionSingleListopt ::=
            //
            case 637:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 638:  PlaceExpressionSingleListopt ::= PlaceExpressionSingleList
            //
            case 638:
                break; 
 
            //
            // Rule 639:  ArgumentListopt ::=
            //
            case 639:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 640:  ArgumentListopt ::= ArgumentList
            //
            case 640:
                break; 
 
            //
            // Rule 641:  DepParametersopt ::=
            //
            case 641:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 642:  DepParametersopt ::= DepParameters
            //
            case 642:
                break; 
 
            //
            // Rule 643:  Unsafeopt ::=
            //
            case 643:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 644:  Unsafeopt ::= unsafe
            //
            case 644: { btParser.setSym1(nf.Here(pos(btParser.getFirstToken(1))));           break;
            } 
 
            //
            // Rule 645:  ParamIdopt ::=
            //
            case 645:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 646:  ParamIdopt ::= identifier
            //
            case 646: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
        
            default:
                break;
        }
        return;
    }
}

