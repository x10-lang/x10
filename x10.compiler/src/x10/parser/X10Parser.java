
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
        String s = prsStream.getName(i);
        if (s.length() == 1) {
            char x = s.charAt(0);
            return new CharacterLiteral(pos(i), x, X10Parsersym.TK_CharacterLiteral);
        }
        else {
            eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                       "Illegal character literal \'" + s + "\'", pos(i));
            return null;
        }
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

    /**
     * Return a TypeNode representing a <code>dims</code>-dimensional
     * array of <code>n</code>.
     */
    public TypeNode array(TypeNode n, Position pos, int dims)
    {
        if (dims > 0)
        {
            if (n instanceof CanonicalTypeNode)
            {
                Type t = ((CanonicalTypeNode) n).type ();
                return nf.CanonicalTypeNode (pos, ts.arrayOf (t, dims));
            }
            return nf.ArrayTypeNode (pos, array (n, pos, dims - 1));
        }
        else
        {
            return n;
        }
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
                if (prsStream.getKind(btParser.getToken(1)) != X10Parsersym.TK_IDENTIFIER)
                {
                    System.out.println("Parser turning keyword " +
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
                btParser.setSym1(array(a, pos(), 1));
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
                    VarDeclarator d = (VarDeclarator) i.next();
                    l.add(nf.FieldDecl(pos(btParser.getFirstToken(2), btParser.getLastToken()),
                                       a,
                                       array(b, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), d.dims),
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
                List l = new TypedList(new LinkedList(), VarDeclarator.class, false);
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
                VarDeclarator a = (VarDeclarator) btParser.getSym(1);
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
                btParser.setSym1(new VarDeclarator(pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 96:  VariableDeclaratorId ::= VariableDeclaratorId LBRACKET RBRACKET
            //
            case 96: {
                VarDeclarator a = (VarDeclarator) btParser.getSym(1);
                a.dims++;
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 97:  VariableInitializer ::= Expression
            //
            case 97:
                break;
 
            //
            // Rule 98:  VariableInitializer ::= ArrayInitializer
            //
            case 98:
                break;
 
            //
            // Rule 99:  FieldModifiers ::= FieldModifier
            //
            case 99:
                break;
 
            //
            // Rule 100:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 100: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 101:  FieldModifier ::= public
            //
            case 101: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 102:  FieldModifier ::= protected
            //
            case 102: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 103:  FieldModifier ::= private
            //
            case 103: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 104:  FieldModifier ::= static
            //
            case 104: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 105:  FieldModifier ::= final
            //
            case 105: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 106:  FieldModifier ::= transient
            //
            case 106: {
                btParser.setSym1(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 107:  FieldModifier ::= volatile
            //
            case 107: {
                btParser.setSym1(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 108:  MethodDeclaration ::= MethodHeader MethodBody
            //
            case 108: {
                MethodDecl a = (MethodDecl) btParser.getSym(1);
                Block b = (Block) btParser.getSym(2);
                btParser.setSym1(a.body(b));
                break;
            }
     
            //
            // Rule 109:  MethodHeader ::= MethodModifiersopt ResultType MethodDeclarator Throwsopt
            //
            case 109: {
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
                                       array((TypeNode) b, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), e.intValue()),
                                       c.toString(),
                                       d,
                                       f,
                                       null));
                break;
            }
     
            //
            // Rule 110:  ResultType ::= Type
            //
            case 110:
                break;
 
            //
            // Rule 111:  ResultType ::= void
            //
            case 111: {
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 112:  MethodDeclarator ::= identifier LPAREN FormalParameterListopt RPAREN
            //
            case 112: {
                Object[] a = new Object[3];
                a[0] =  new Name(nf, ts, pos(), id(btParser.getToken(1)).getIdentifier());
                a[1] = btParser.getSym(3);
                a[2] = new Integer(0);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 113:  MethodDeclarator ::= MethodDeclarator LBRACKET RBRACKET
            //
            case 113: {
                Object[] a = (Object []) btParser.getSym(1);
                a[2] = new Integer(((Integer) a[2]).intValue() + 1);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 114:  FormalParameterList ::= LastFormalParameter
            //
            case 114: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 115:  FormalParameterList ::= FormalParameters COMMA LastFormalParameter
            //
            case 115: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 116:  FormalParameters ::= FormalParameter
            //
            case 116: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 117:  FormalParameters ::= FormalParameters COMMA FormalParameter
            //
            case 117: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 118:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 118: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                VarDeclarator b = (VarDeclarator) btParser.getSym(3);
                btParser.setSym1(nf.Formal(pos(), f, array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b.name));
                break;
            }
     
            //
            // Rule 120:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 120: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 121:  VariableModifier ::= final
            //
            case 121: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 122:  LastFormalParameter ::= VariableModifiersopt Type ...opt VariableDeclaratorId
            //
            case 122: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                assert(btParser.getSym(3) == null);
                VarDeclarator b = (VarDeclarator) btParser.getSym(4);
                btParser.setSym1(nf.Formal(pos(), f, array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b.name));
                break;
            }
     
            //
            // Rule 123:  MethodModifiers ::= MethodModifier
            //
            case 123:
                break;
 
            //
            // Rule 124:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 124: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 125:  MethodModifier ::= public
            //
            case 125: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 126:  MethodModifier ::= protected
            //
            case 126: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 127:  MethodModifier ::= private
            //
            case 127: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 128:  MethodModifier ::= abstract
            //
            case 128: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 129:  MethodModifier ::= static
            //
            case 129: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 130:  MethodModifier ::= final
            //
            case 130: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 131:  MethodModifier ::= synchronized
            //
            case 131: {
                btParser.setSym1(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 132:  MethodModifier ::= native
            //
            case 132: {
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 133:  MethodModifier ::= strictfp
            //
            case 133: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 134:  Throws ::= throws ExceptionTypeList
            //
            case 134: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 135:  ExceptionTypeList ::= ExceptionType
            //
            case 135: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 136:  ExceptionTypeList ::= ExceptionTypeList COMMA ExceptionType
            //
            case 136: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 137:  ExceptionType ::= ClassType
            //
            case 137:
                break;
 
            //
            // Rule 138:  ExceptionType ::= TypeVariable
            //
            case 138:
                break;
 
            //
            // Rule 139:  MethodBody ::= Block
            //
            case 139:
                break;
 
            //
            // Rule 140:  MethodBody ::= SEMICOLON
            //
            case 140:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 141:  InstanceInitializer ::= Block
            //
            case 141:
                break;
 
            //
            // Rule 142:  StaticInitializer ::= static Block
            //
            case 142: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 143:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 143: {
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
            // Rule 144:  ConstructorDeclarator ::= SimpleTypeName LPAREN FormalParameterListopt RPAREN
            //
            case 144: {
//vj                    assert(btParser.getSym(1) == null);
                Object[] a = new Object[3];
//vj                    a[0] = btParser.getSym(1);
                a[1] = btParser.getSym(1);
                a[2] = btParser.getSym(3);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 145:  SimpleTypeName ::= identifier
            //
            case 145: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 146:  ConstructorModifiers ::= ConstructorModifier
            //
            case 146:
                break;
 
            //
            // Rule 147:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 147: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 148:  ConstructorModifier ::= public
            //
            case 148: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 149:  ConstructorModifier ::= protected
            //
            case 149: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 150:  ConstructorModifier ::= private
            //
            case 150: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 151:  ConstructorBody ::= LBRACE ExplicitConstructorInvocationopt BlockStatementsopt RBRACE
            //
            case 151: {
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
            // Rule 152:  ExplicitConstructorInvocation ::= this LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 152: {
//vj                    assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.ThisCall(pos(), b));
                break;
            }
     
            //
            // Rule 153:  ExplicitConstructorInvocation ::= super LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 153: {
//vj                    assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.SuperCall(pos(), b));
                break;
            }
     
            //
            // Rule 154:  ExplicitConstructorInvocation ::= Primary DOT this LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 154: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.ThisCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 155:  ExplicitConstructorInvocation ::= Primary DOT super LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 155: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.SuperCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 156:  EnumDeclaration ::= ClassModifiersopt enum identifier Interfacesopt EnumBody
            //
            case 156:
                bad_rule = 156;
                break;
 
            //
            // Rule 157:  EnumBody ::= LBRACE EnumConstantsopt ,opt EnumBodyDeclarationsopt RBRACE
            //
            case 157:
                bad_rule = 157;
                break;
 
            //
            // Rule 158:  EnumConstants ::= EnumConstant
            //
            case 158:
                bad_rule = 158;
                break;
 
            //
            // Rule 159:  EnumConstants ::= EnumConstants COMMA EnumConstant
            //
            case 159:
                bad_rule = 159;
                break;
 
            //
            // Rule 160:  EnumConstant ::= identifier Argumentsopt ClassBodyopt
            //
            case 160:
                bad_rule = 160;
                break;
 
            //
            // Rule 161:  Arguments ::= LPAREN ArgumentListopt RPAREN
            //
            case 161: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 162:  EnumBodyDeclarations ::= SEMICOLON ClassBodyDeclarationsopt
            //
            case 162:
                bad_rule = 162;
                break;
 
            //
            // Rule 163:  InterfaceDeclaration ::= NormalInterfaceDeclaration
            //
            case 163:
                break;
 
            //
            // Rule 164:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier ExtendsInterfacesopt InterfaceBody
            //
            case 164: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
//vj                    assert(btParser.getSym(4) == null);
                List c = (List) btParser.getSym(4);
                ClassBody d = (ClassBody) btParser.getSym(5);
                btParser.setSym1(nf.ClassDecl(pos(), a.Interface(), b.getIdentifier(), null, c, d));
                break;
            }
     
            //
            // Rule 165:  InterfaceModifiers ::= InterfaceModifier
            //
            case 165:
                break;
 
            //
            // Rule 166:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 166: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 167:  InterfaceModifier ::= public
            //
            case 167: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 168:  InterfaceModifier ::= protected
            //
            case 168: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 169:  InterfaceModifier ::= private
            //
            case 169: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 170:  InterfaceModifier ::= abstract
            //
            case 170: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 171:  InterfaceModifier ::= static
            //
            case 171: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 172:  InterfaceModifier ::= strictfp
            //
            case 172: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 173:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 173: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 174:  ExtendsInterfaces ::= ExtendsInterfaces COMMA InterfaceType
            //
            case 174: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 175:  InterfaceBody ::= LBRACE InterfaceMemberDeclarationsopt RBRACE
            //
            case 175: {
                List a = (List)btParser.getSym(2);
                btParser.setSym1(nf.ClassBody(pos(), a));
                break;
            }
     
            //
            // Rule 176:  InterfaceMemberDeclarations ::= InterfaceMemberDeclaration
            //
            case 176:
                break;
 
            //
            // Rule 177:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 177: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 178:  InterfaceMemberDeclaration ::= ConstantDeclaration
            //
            case 178:
                break;
 
            //
            // Rule 179:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 179: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 180:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 180: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 181:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 181: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 182:  InterfaceMemberDeclaration ::= SEMICOLON
            //
            case 182: {
                btParser.setSym1(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 183:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 183: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Flags a = (Flags) btParser.getSym(1);
                TypeNode b = (TypeNode) btParser.getSym(2);
                List c = (List) btParser.getSym(3);
                for (Iterator i = c.iterator(); i.hasNext();)
                {
                    VarDeclarator d = (VarDeclarator) i.next();
                    l.add(nf.FieldDecl(pos(btParser.getFirstToken(2), btParser.getLastToken()),
                                       a,
                                       array(b, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), d.dims),
                                       d.name,
                                       d.init));
                }
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 184:  ConstantModifiers ::= ConstantModifier
            //
            case 184:
                break;
 
            //
            // Rule 185:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 185: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 186:  ConstantModifier ::= public
            //
            case 186: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 187:  ConstantModifier ::= static
            //
            case 187: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 188:  ConstantModifier ::= final
            //
            case 188: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 189:  AbstractMethodDeclaration ::= AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt SEMICOLON
            //
            case 189: {
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
                                       array((TypeNode) b, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), e.intValue()),
                                       c.toString(),
                                       d,
                                       f,
                                       null));
                break;
            }
     
            //
            // Rule 190:  AbstractMethodModifiers ::= AbstractMethodModifier
            //
            case 190:
                break;
 
            //
            // Rule 191:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 191: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 192:  AbstractMethodModifier ::= public
            //
            case 192: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 193:  AbstractMethodModifier ::= abstract
            //
            case 193: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 194:  AnnotationTypeDeclaration ::= InterfaceModifiersopt AT interface identifier AnnotationTypeBody
            //
            case 194:
                bad_rule = 194;
                break;
 
            //
            // Rule 195:  AnnotationTypeBody ::= LBRACE AnnotationTypeElementDeclarationsopt RBRACE
            //
            case 195:
                bad_rule = 195;
                break;
 
            //
            // Rule 196:  AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclaration
            //
            case 196:
                bad_rule = 196;
                break;
 
            //
            // Rule 197:  AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclarations AnnotationTypeElementDeclaration
            //
            case 197:
                bad_rule = 197;
                break;
 
            //
            // Rule 198:  AnnotationTypeElementDeclaration ::= AbstractMethodModifiersopt Type identifier LPAREN RPAREN DefaultValueopt SEMICOLON
            //
            case 198:
                bad_rule = 198;
                break;
 
            //
            // Rule 199:  AnnotationTypeElementDeclaration ::= ConstantDeclaration
            //
            case 199:
                bad_rule = 199;
                break;
 
            //
            // Rule 200:  AnnotationTypeElementDeclaration ::= ClassDeclaration
            //
            case 200:
                bad_rule = 200;
                break;
 
            //
            // Rule 201:  AnnotationTypeElementDeclaration ::= InterfaceDeclaration
            //
            case 201:
                bad_rule = 201;
                break;
 
            //
            // Rule 202:  AnnotationTypeElementDeclaration ::= EnumDeclaration
            //
            case 202:
                bad_rule = 202;
                break;
 
            //
            // Rule 203:  AnnotationTypeElementDeclaration ::= AnnotationTypeDeclaration
            //
            case 203:
                bad_rule = 203;
                break;
 
            //
            // Rule 204:  AnnotationTypeElementDeclaration ::= SEMICOLON
            //
            case 204:
                bad_rule = 204;
                break;
 
            //
            // Rule 205:  DefaultValue ::= default ElementValue
            //
            case 205:
                bad_rule = 205;
                break;
 
            //
            // Rule 206:  Annotations ::= Annotation
            //
            case 206:
                bad_rule = 206;
                break;
 
            //
            // Rule 207:  Annotations ::= Annotations Annotation
            //
            case 207:
                bad_rule = 207;
                break;
 
            //
            // Rule 208:  Annotation ::= NormalAnnotation
            //
            case 208:
                bad_rule = 208;
                break;
 
            //
            // Rule 209:  Annotation ::= MarkerAnnotation
            //
            case 209:
                bad_rule = 209;
                break;
 
            //
            // Rule 210:  Annotation ::= SingleElementAnnotation
            //
            case 210:
                bad_rule = 210;
                break;
 
            //
            // Rule 211:  NormalAnnotation ::= AT TypeName LPAREN ElementValuePairsopt RPAREN
            //
            case 211:
                bad_rule = 211;
                break;
 
            //
            // Rule 212:  ElementValuePairs ::= ElementValuePair
            //
            case 212:
                bad_rule = 212;
                break;
 
            //
            // Rule 213:  ElementValuePairs ::= ElementValuePairs COMMA ElementValuePair
            //
            case 213:
                bad_rule = 213;
                break;
 
            //
            // Rule 214:  ElementValuePair ::= SimpleName EQUAL ElementValue
            //
            case 214:
                bad_rule = 214;
                break;
 
            //
            // Rule 215:  SimpleName ::= identifier
            //
            case 215: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 216:  ElementValue ::= ConditionalExpression
            //
            case 216:
                bad_rule = 216;
                break;
 
            //
            // Rule 217:  ElementValue ::= Annotation
            //
            case 217:
                bad_rule = 217;
                break;
 
            //
            // Rule 218:  ElementValue ::= ElementValueArrayInitializer
            //
            case 218:
                bad_rule = 218;
                break;
 
            //
            // Rule 219:  ElementValueArrayInitializer ::= LBRACE ElementValuesopt ,opt RBRACE
            //
            case 219:
                bad_rule = 219;
                break;
 
            //
            // Rule 220:  ElementValues ::= ElementValue
            //
            case 220:
                bad_rule = 220;
                break;
 
            //
            // Rule 221:  ElementValues ::= ElementValues COMMA ElementValue
            //
            case 221:
                bad_rule = 221;
                break;
 
            //
            // Rule 222:  MarkerAnnotation ::= AT TypeName
            //
            case 222:
                bad_rule = 222;
                break;
 
            //
            // Rule 223:  SingleElementAnnotation ::= AT TypeName LPAREN ElementValue RPAREN
            //
            case 223:
                bad_rule = 223;
                break;
 
            //
            // Rule 224:  ArrayInitializer ::= LBRACE VariableInitializersopt ,opt RBRACE
            //
            case 224: {
                List a = (List) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.ArrayInit(pos()));
                else btParser.setSym1(nf.ArrayInit(pos(), a));
                break;
            }
     
            //
            // Rule 225:  VariableInitializers ::= VariableInitializer
            //
            case 225: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 226:  VariableInitializers ::= VariableInitializers COMMA VariableInitializer
            //
            case 226: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 227:  Block ::= LBRACE BlockStatementsopt RBRACE
            //
            case 227: {
                List l = (List) btParser.getSym(2);
                btParser.setSym1(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 228:  BlockStatements ::= BlockStatement
            //
            case 228: {
                List l = new TypedList(new LinkedList(), Stmt.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 229:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 229: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 230:  BlockStatement ::= LocalVariableDeclarationStatement
            //
            case 230:
                break;
 
            //
            // Rule 231:  BlockStatement ::= ClassDeclaration
            //
            case 231: {
                ClassDecl a = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 232:  BlockStatement ::= Statement
            //
            case 232: {
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 233:  LocalVariableDeclarationStatement ::= LocalVariableDeclaration SEMICOLON
            //
            case 233:
                break;
 
            //
            // Rule 234:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 234: {
                Flags flags = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                List b = (List) btParser.getSym(3);

                List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                for (Iterator i = b.iterator(); i.hasNext(); )
                {
                    VarDeclarator d = (VarDeclarator) i.next();
                    l.add(nf.LocalDecl(pos(d), flags, array(a, pos(d), d.dims), d.name, d.init));
                }

                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 235:  Statement ::= StatementWithoutTrailingSubstatement
            //
            case 235:
                break;
 
            //
            // Rule 236:  Statement ::= LabeledStatement
            //
            case 236:
                break;
 
            //
            // Rule 237:  Statement ::= IfThenStatement
            //
            case 237:
                break;
 
            //
            // Rule 238:  Statement ::= IfThenElseStatement
            //
            case 238:
                break;
 
            //
            // Rule 239:  Statement ::= WhileStatement
            //
            case 239:
                break;
 
            //
            // Rule 240:  Statement ::= ForStatement
            //
            case 240:
                break;
 
            //
            // Rule 241:  StatementWithoutTrailingSubstatement ::= Block
            //
            case 241:
                break;
 
            //
            // Rule 242:  StatementWithoutTrailingSubstatement ::= EmptyStatement
            //
            case 242:
                break;
 
            //
            // Rule 243:  StatementWithoutTrailingSubstatement ::= ExpressionStatement
            //
            case 243:
                break;
 
            //
            // Rule 244:  StatementWithoutTrailingSubstatement ::= AssertStatement
            //
            case 244:
                break;
 
            //
            // Rule 245:  StatementWithoutTrailingSubstatement ::= SwitchStatement
            //
            case 245:
                break;
 
            //
            // Rule 246:  StatementWithoutTrailingSubstatement ::= DoStatement
            //
            case 246:
                break;
 
            //
            // Rule 247:  StatementWithoutTrailingSubstatement ::= BreakStatement
            //
            case 247:
                break;
 
            //
            // Rule 248:  StatementWithoutTrailingSubstatement ::= ContinueStatement
            //
            case 248:
                break;
 
            //
            // Rule 249:  StatementWithoutTrailingSubstatement ::= ReturnStatement
            //
            case 249:
                break;
 
            //
            // Rule 250:  StatementWithoutTrailingSubstatement ::= SynchronizedStatement
            //
            case 250:
                break;
 
            //
            // Rule 251:  StatementWithoutTrailingSubstatement ::= ThrowStatement
            //
            case 251:
                break;
 
            //
            // Rule 252:  StatementWithoutTrailingSubstatement ::= TryStatement
            //
            case 252:
                break;
 
            //
            // Rule 253:  StatementNoShortIf ::= StatementWithoutTrailingSubstatement
            //
            case 253:
                break;
 
            //
            // Rule 254:  StatementNoShortIf ::= LabeledStatementNoShortIf
            //
            case 254:
                break;
 
            //
            // Rule 255:  StatementNoShortIf ::= IfThenElseStatementNoShortIf
            //
            case 255:
                break;
 
            //
            // Rule 256:  StatementNoShortIf ::= WhileStatementNoShortIf
            //
            case 256:
                break;
 
            //
            // Rule 257:  StatementNoShortIf ::= ForStatementNoShortIf
            //
            case 257:
                break;
 
            //
            // Rule 258:  IfThenStatement ::= if LPAREN Expression RPAREN Statement
            //
            case 258: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.If(pos(), a, b));
                break;
            }
     
            //
            // Rule 259:  IfThenElseStatement ::= if LPAREN Expression RPAREN StatementNoShortIf else Statement
            //
            case 259: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 260:  IfThenElseStatementNoShortIf ::= if LPAREN Expression RPAREN StatementNoShortIf else StatementNoShortIf
            //
            case 260: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 261:  EmptyStatement ::= SEMICOLON
            //
            case 261: {
                btParser.setSym1(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 262:  LabeledStatement ::= identifier COLON Statement
            //
            case 262: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 263:  LabeledStatementNoShortIf ::= identifier COLON StatementNoShortIf
            //
            case 263: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 264:  ExpressionStatement ::= StatementExpression SEMICOLON
            //
            case 264: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Eval(pos(), a));
                break;
            }
     
            //
            // Rule 265:  StatementExpression ::= Assignment
            //
            case 265:
                break;
 
            //
            // Rule 266:  StatementExpression ::= PreIncrementExpression
            //
            case 266:
                break;
 
            //
            // Rule 267:  StatementExpression ::= PreDecrementExpression
            //
            case 267:
                break;
 
            //
            // Rule 268:  StatementExpression ::= PostIncrementExpression
            //
            case 268:
                break;
 
            //
            // Rule 269:  StatementExpression ::= PostDecrementExpression
            //
            case 269:
                break;
 
            //
            // Rule 270:  StatementExpression ::= MethodInvocation
            //
            case 270:
                break;
 
            //
            // Rule 271:  StatementExpression ::= ClassInstanceCreationExpression
            //
            case 271:
                break;
 
            //
            // Rule 272:  AssertStatement ::= assert Expression SEMICOLON
            //
            case 272: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Assert(pos(), a));
                break;
            }
     
            //
            // Rule 273:  AssertStatement ::= assert Expression COLON Expression SEMICOLON
            //
            case 273: {
                Expr a = (Expr) btParser.getSym(2),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Assert(pos(), a, b));
                break;
            }
     
            //
            // Rule 274:  SwitchStatement ::= switch LPAREN Expression RPAREN SwitchBlock
            //
            case 274: {
                Expr a = (Expr) btParser.getSym(3);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.Switch(pos(), a, b));
                break;
            }
     
            //
            // Rule 275:  SwitchBlock ::= LBRACE SwitchBlockStatementGroupsopt SwitchLabelsopt RBRACE
            //
            case 275: {
                List l = (List) btParser.getSym(2),
                     l2 = (List) btParser.getSym(3);
                l.addAll(l2);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 276:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroup
            //
            case 276:
                break;
 
            //
            // Rule 277:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 277: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 278:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 278: {
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);

                List l1 = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l1);
                l.add(nf.SwitchBlock(pos(), l2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 279:  SwitchLabels ::= SwitchLabel
            //
            case 279: {
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 280:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 280: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 281:  SwitchLabel ::= case ConstantExpression COLON
            //
            case 281: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Case(pos(), a));
                break;
            }
     
            //
            // Rule 282:  SwitchLabel ::= case EnumConstant COLON
            //
            case 282:
                bad_rule = 282;
                break;
 
            //
            // Rule 283:  SwitchLabel ::= default COLON
            //
            case 283: {
                btParser.setSym1(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 284:  EnumConstant ::= identifier
            //
            case 284:
                bad_rule = 284;
                break;
 
            //
            // Rule 285:  WhileStatement ::= while LPAREN Expression RPAREN Statement
            //
            case 285: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 286:  WhileStatementNoShortIf ::= while LPAREN Expression RPAREN StatementNoShortIf
            //
            case 286: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 287:  DoStatement ::= do Statement while LPAREN Expression RPAREN SEMICOLON
            //
            case 287: {
                Stmt a = (Stmt) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Do(pos(), a, b));
                break;
            }
     
            //
            // Rule 288:  ForStatement ::= BasicForStatement
            //
            case 288:
                break;
 
            //
            // Rule 289:  ForStatement ::= EnhancedForStatement
            //
            case 289:
                break;
 
            //
            // Rule 290:  BasicForStatement ::= for LPAREN ForInitopt SEMICOLON Expressionopt SEMICOLON ForUpdateopt RPAREN Statement
            //
            case 290: {
                List a = (List) btParser.getSym(3);
                Expr b = (Expr) btParser.getSym(5);
                List c = (List) btParser.getSym(7);
                Stmt d = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), a, b, c, d));
                break;
            }
     
            //
            // Rule 291:  ForStatementNoShortIf ::= for LPAREN ForInitopt SEMICOLON Expressionopt SEMICOLON ForUpdateopt RPAREN StatementNoShortIf
            //
            case 291: {
                List a = (List) btParser.getSym(3);
                Expr b = (Expr) btParser.getSym(5);
                List c = (List) btParser.getSym(7);
                Stmt d = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), a, b, c, d));
                break;
            }
     
            //
            // Rule 292:  ForInit ::= StatementExpressionList
            //
            case 292:
                break;
 
            //
            // Rule 293:  ForInit ::= LocalVariableDeclaration
            //
            case 293: {
                List l = new TypedList(new LinkedList(), ForInit.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 294:  ForUpdate ::= StatementExpressionList
            //
            case 294:
                break;
 
            //
            // Rule 295:  StatementExpressionList ::= StatementExpression
            //
            case 295: {
                List l = new TypedList(new LinkedList(), Eval.class, false);
                Expr a = (Expr) btParser.getSym(1);
                l.add(nf.Eval(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 296:  StatementExpressionList ::= StatementExpressionList COMMA StatementExpression
            //
            case 296: {
                List l = (List) btParser.getSym(1);
                Expr a = (Expr) btParser.getSym(3);
                l.add(nf.Eval(pos(), a));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 297:  BreakStatement ::= break identifieropt SEMICOLON
            //
            case 297: {
                Name a = (Name) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.Break(pos()));
                else btParser.setSym1(nf.Break(pos(), a.toString()));
                break;
            }
     
            //
            // Rule 298:  ContinueStatement ::= continue identifieropt SEMICOLON
            //
            case 298: {
                Name a = (Name) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.Continue(pos()));
                else btParser.setSym1(nf.Continue(pos(), a.toString()));
                break;
            }
     
            //
            // Rule 299:  ReturnStatement ::= return Expressionopt SEMICOLON
            //
            case 299: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Return(pos(), a));
                break;
            }
     
            //
            // Rule 300:  ThrowStatement ::= throw Expression SEMICOLON
            //
            case 300: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Throw(pos(), a));
                break;
            }
     
            //
            // Rule 301:  SynchronizedStatement ::= synchronized LPAREN Expression RPAREN Block
            //
            case 301: {
                Expr a = (Expr) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Synchronized(pos(), a, b));
                break;
            }
     
            //
            // Rule 302:  TryStatement ::= try Block Catches
            //
            case 302: {
                Block a = (Block) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Try(pos(), a, b));
                break;
            }
     
            //
            // Rule 303:  TryStatement ::= try Block Catchesopt Finally
            //
            case 303: {
                Block a = (Block) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                Block c = (Block) btParser.getSym(4);
                btParser.setSym1(nf.Try(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 304:  Catches ::= CatchClause
            //
            case 304: {
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 305:  Catches ::= Catches CatchClause
            //
            case 305: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 306:  CatchClause ::= catch LPAREN FormalParameter RPAREN Block
            //
            case 306: {
                Formal a = (Formal) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Catch(pos(), a, b));
                break;
            }
     
            //
            // Rule 307:  Finally ::= finally Block
            //
            case 307: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 308:  Primary ::= PrimaryNoNewArray
            //
            case 308:
                break;
 
            //
            // Rule 309:  Primary ::= ArrayCreationExpression
            //
            case 309:
                break;
 
            //
            // Rule 310:  PrimaryNoNewArray ::= Literal
            //
            case 310:
                break;
 
            //
            // Rule 311:  PrimaryNoNewArray ::= Type DOT class
            //
            case 311: {
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
            // Rule 312:  PrimaryNoNewArray ::= void DOT class
            //
            case 312: {
                btParser.setSym1(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(btParser.getToken(1)), ts.Void())));
                break;
            }
     
            //
            // Rule 313:  PrimaryNoNewArray ::= this
            //
            case 313: {
                btParser.setSym1(nf.This(pos()));
                break;
            }
     
            //
            // Rule 314:  PrimaryNoNewArray ::= ClassName DOT this
            //
            case 314: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(nf.This(pos(), a.toType()));
                break;
            }
     
            //
            // Rule 315:  PrimaryNoNewArray ::= LPAREN Expression RPAREN
            //
            case 315: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 316:  PrimaryNoNewArray ::= ClassInstanceCreationExpression
            //
            case 316:
                break;
 
            //
            // Rule 317:  PrimaryNoNewArray ::= FieldAccess
            //
            case 317:
                break;
 
            //
            // Rule 318:  PrimaryNoNewArray ::= MethodInvocation
            //
            case 318:
                break;
 
            //
            // Rule 319:  PrimaryNoNewArray ::= ArrayAccess
            //
            case 319:
                break;
 
            //
            // Rule 320:  Literal ::= IntegerLiteral
            //
            case 320: {
                polyglot.lex.IntegerLiteral a = int_lit(btParser.getToken(1));
                btParser.setSym1(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 321:  Literal ::= LongLiteral
            //
            case 321: {
                polyglot.lex.LongLiteral a = long_lit(btParser.getToken(1));
                btParser.setSym1(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 322:  Literal ::= FloatingPointLiteral
            //
            case 322: {
                polyglot.lex.FloatLiteral a = float_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 323:  Literal ::= DoubleLiteral
            //
            case 323: {
                polyglot.lex.DoubleLiteral a = double_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 324:  Literal ::= BooleanLiteral
            //
            case 324: {
                polyglot.lex.BooleanLiteral a = boolean_lit(btParser.getToken(1));
                btParser.setSym1(nf.BooleanLit(pos(), a.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 325:  Literal ::= CharacterLiteral
            //
            case 325: {
                polyglot.lex.CharacterLiteral a = char_lit(btParser.getToken(1));
                btParser.setSym1(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 326:  Literal ::= StringLiteral
            //
            case 326: {
                String s = prsStream.getName(btParser.getToken(1));
                btParser.setSym1(nf.StringLit(pos(), s.substring(1, s.length() - 1)));
                break;
            }
     
            //
            // Rule 327:  Literal ::= null
            //
            case 327: {
                btParser.setSym1(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 328:  BooleanLiteral ::= true
            //
            case 328:
                break;
 
            //
            // Rule 329:  BooleanLiteral ::= false
            //
            case 329:
                break;
 
            //
            // Rule 330:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 330: {
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
            // Rule 331:  ClassInstanceCreationExpression ::= Primary DOT new identifier LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 331: {
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
            // Rule 332:  ClassInstanceCreationExpression ::= AmbiguousName DOT new identifier LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 332: {
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
            // Rule 333:  ArgumentList ::= Expression
            //
            case 333: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 334:  ArgumentList ::= ArgumentList COMMA Expression
            //
            case 334: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 335:  DimExprs ::= DimExpr
            //
            case 335: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 336:  DimExprs ::= DimExprs DimExpr
            //
            case 336: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 337:  DimExpr ::= LBRACKET Expression RBRACKET
            //
            case 337: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(a.position(pos()));
                break;
            }
     
            //
            // Rule 338:  Dims ::= LBRACKET RBRACKET
            //
            case 338: {
                btParser.setSym1(new Integer(1));
                break;
            }
     
            //
            // Rule 339:  Dims ::= Dims LBRACKET RBRACKET
            //
            case 339: {
                Integer a = (Integer) btParser.getSym(1);
                btParser.setSym1(new Integer(a.intValue() + 1));
                break;
            }
     
            //
            // Rule 340:  FieldAccess ::= Primary DOT identifier
            //
            case 340: {
                Expr a = (Expr) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(), a, b.getIdentifier()));
                break;
            }
     
            //
            // Rule 341:  FieldAccess ::= super DOT identifier
            //
            case 341: {
                polyglot.lex.Identifier a = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken())), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 342:  FieldAccess ::= ClassName DOT super DOT identifier
            //
            case 342: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier()));
                break;
            }
     
            //
            // Rule 343:  MethodInvocation ::= MethodName LPAREN ArgumentListopt RPAREN
            //
            case 343: {
                Name a = (Name) btParser.getSym(1);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Call(pos(), a.prefix == null ? null : a.prefix.toReceiver(), a.name, b));
                break;
            }
     
            //
            // Rule 344:  MethodInvocation ::= Primary DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 344: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), a, b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 345:  MethodInvocation ::= super DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 345: {
//vj                    assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken())), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 346:  MethodInvocation ::= ClassName DOT super DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 346: {
                Name a = (Name) btParser.getSym(1);
//vj                    assert(btParser.getSym(5) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(5));
                List c = (List) btParser.getSym(7);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 347:  PostfixExpression ::= Primary
            //
            case 347:
                break;
 
            //
            // Rule 348:  PostfixExpression ::= ExpressionName
            //
            case 348: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 349:  PostfixExpression ::= PostIncrementExpression
            //
            case 349:
                break;
 
            //
            // Rule 350:  PostfixExpression ::= PostDecrementExpression
            //
            case 350:
                break;
 
            //
            // Rule 351:  PostIncrementExpression ::= PostfixExpression PLUS_PLUS
            //
            case 351: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 352:  PostDecrementExpression ::= PostfixExpression MINUS_MINUS
            //
            case 352: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 353:  UnaryExpression ::= PreIncrementExpression
            //
            case 353:
                break;
 
            //
            // Rule 354:  UnaryExpression ::= PreDecrementExpression
            //
            case 354:
                break;
 
            //
            // Rule 355:  UnaryExpression ::= PLUS UnaryExpression
            //
            case 355: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.POS, a));
                break;
            }
     
            //
            // Rule 356:  UnaryExpression ::= MINUS UnaryExpression
            //
            case 356: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NEG, a));
                break;
            }
     
            //
            // Rule 358:  PreIncrementExpression ::= PLUS_PLUS UnaryExpression
            //
            case 358: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_INC, a));
                break;
            }
     
            //
            // Rule 359:  PreDecrementExpression ::= MINUS_MINUS UnaryExpression
            //
            case 359: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_DEC, a));
                break;
            }
     
            //
            // Rule 360:  UnaryExpressionNotPlusMinus ::= PostfixExpression
            //
            case 360:
                break;
 
            //
            // Rule 361:  UnaryExpressionNotPlusMinus ::= TWIDDLE UnaryExpression
            //
            case 361: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.BIT_NOT, a));
                break;
            }
     
            //
            // Rule 362:  UnaryExpressionNotPlusMinus ::= NOT UnaryExpression
            //
            case 362: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NOT, a));
                break;
            }
     
            //
            // Rule 364:  MultiplicativeExpression ::= UnaryExpression
            //
            case 364:
                break;
 
            //
            // Rule 365:  MultiplicativeExpression ::= MultiplicativeExpression MULTIPLY UnaryExpression
            //
            case 365: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MUL, b));
                break;
            }
     
            //
            // Rule 366:  MultiplicativeExpression ::= MultiplicativeExpression DIVIDE UnaryExpression
            //
            case 366: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.DIV, b));
                break;
            }
     
            //
            // Rule 367:  MultiplicativeExpression ::= MultiplicativeExpression REMAINDER UnaryExpression
            //
            case 367: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MOD, b));
                break;
            }
     
            //
            // Rule 368:  AdditiveExpression ::= MultiplicativeExpression
            //
            case 368:
                break;
 
            //
            // Rule 369:  AdditiveExpression ::= AdditiveExpression PLUS MultiplicativeExpression
            //
            case 369: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.ADD, b));
                break;
            }
     
            //
            // Rule 370:  AdditiveExpression ::= AdditiveExpression MINUS MultiplicativeExpression
            //
            case 370: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SUB, b));
                break;
            }
     
            //
            // Rule 371:  ShiftExpression ::= AdditiveExpression
            //
            case 371:
                break;
 
            //
            // Rule 372:  ShiftExpression ::= ShiftExpression LEFT_SHIFT AdditiveExpression
            //
            case 372: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHL, b));
                break;
            }
     
            //
            // Rule 373:  ShiftExpression ::= ShiftExpression GREATER GREATER AdditiveExpression
            //
            case 373: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHR, b));
                break;
            }
     
            //
            // Rule 374:  ShiftExpression ::= ShiftExpression GREATER GREATER GREATER AdditiveExpression
            //
            case 374: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Binary(pos(), a, Binary.USHR, b));
                break;
            }
     
            //
            // Rule 375:  RelationalExpression ::= ShiftExpression
            //
            case 375:
                break;
 
            //
            // Rule 376:  RelationalExpression ::= RelationalExpression LESS ShiftExpression
            //
            case 376: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LT, b));
                break;
            }
     
            //
            // Rule 377:  RelationalExpression ::= RelationalExpression GREATER ShiftExpression
            //
            case 377: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GT, b));
                break;
            }
     
            //
            // Rule 378:  RelationalExpression ::= RelationalExpression LESS_EQUAL ShiftExpression
            //
            case 378: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LE, b));
                break;
            }
     
            //
            // Rule 379:  RelationalExpression ::= RelationalExpression GREATER EQUAL ShiftExpression
            //
            case 379: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GE, b));
                break;
            }
     
            //
            // Rule 380:  EqualityExpression ::= RelationalExpression
            //
            case 380:
                break;
 
            //
            // Rule 381:  EqualityExpression ::= EqualityExpression EQUAL_EQUAL RelationalExpression
            //
            case 381: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.EQ, b));
                break;
            }
     
            //
            // Rule 382:  EqualityExpression ::= EqualityExpression NOT_EQUAL RelationalExpression
            //
            case 382: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.NE, b));
                break;
            }
     
            //
            // Rule 383:  AndExpression ::= EqualityExpression
            //
            case 383:
                break;
 
            //
            // Rule 384:  AndExpression ::= AndExpression AND EqualityExpression
            //
            case 384: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_AND, b));
                break;
            }
     
            //
            // Rule 385:  ExclusiveOrExpression ::= AndExpression
            //
            case 385:
                break;
 
            //
            // Rule 386:  ExclusiveOrExpression ::= ExclusiveOrExpression XOR AndExpression
            //
            case 386: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_XOR, b));
                break;
            }
     
            //
            // Rule 387:  InclusiveOrExpression ::= ExclusiveOrExpression
            //
            case 387:
                break;
 
            //
            // Rule 388:  InclusiveOrExpression ::= InclusiveOrExpression OR ExclusiveOrExpression
            //
            case 388: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_OR, b));
                break;
            }
     
            //
            // Rule 389:  ConditionalAndExpression ::= InclusiveOrExpression
            //
            case 389:
                break;
 
            //
            // Rule 390:  ConditionalAndExpression ::= ConditionalAndExpression AND_AND InclusiveOrExpression
            //
            case 390: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_AND, b));
                break;
            }
     
            //
            // Rule 391:  ConditionalOrExpression ::= ConditionalAndExpression
            //
            case 391:
                break;
 
            //
            // Rule 392:  ConditionalOrExpression ::= ConditionalOrExpression OR_OR ConditionalAndExpression
            //
            case 392: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_OR, b));
                break;
            }
     
            //
            // Rule 393:  ConditionalExpression ::= ConditionalOrExpression
            //
            case 393:
                break;
 
            //
            // Rule 394:  ConditionalExpression ::= ConditionalOrExpression QUESTION Expression COLON ConditionalExpression
            //
            case 394: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3),
                     c = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Conditional(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 395:  AssignmentExpression ::= ConditionalExpression
            //
            case 395:
                break;
 
            //
            // Rule 396:  AssignmentExpression ::= Assignment
            //
            case 396:
                break;
 
            //
            // Rule 397:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 397: {
                Expr a = (Expr) btParser.getSym(1);
                Assign.Operator b = (Assign.Operator) btParser.getSym(2);
                Expr c = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Assign(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 398:  LeftHandSide ::= ExpressionName
            //
            case 398: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 399:  LeftHandSide ::= FieldAccess
            //
            case 399:
                break;
 
            //
            // Rule 400:  LeftHandSide ::= ArrayAccess
            //
            case 400:
                break;
 
            //
            // Rule 401:  AssignmentOperator ::= EQUAL
            //
            case 401: {
                btParser.setSym1(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 402:  AssignmentOperator ::= MULTIPLY_EQUAL
            //
            case 402: {
                btParser.setSym1(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 403:  AssignmentOperator ::= DIVIDE_EQUAL
            //
            case 403: {
                btParser.setSym1(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 404:  AssignmentOperator ::= REMAINDER_EQUAL
            //
            case 404: {
                btParser.setSym1(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 405:  AssignmentOperator ::= PLUS_EQUAL
            //
            case 405: {
                btParser.setSym1(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 406:  AssignmentOperator ::= MINUS_EQUAL
            //
            case 406: {
                btParser.setSym1(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 407:  AssignmentOperator ::= LEFT_SHIFT_EQUAL
            //
            case 407: {
                btParser.setSym1(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 408:  AssignmentOperator ::= GREATER GREATER EQUAL
            //
            case 408: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 409:  AssignmentOperator ::= GREATER GREATER GREATER EQUAL
            //
            case 409: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 410:  AssignmentOperator ::= AND_EQUAL
            //
            case 410: {
                btParser.setSym1(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 411:  AssignmentOperator ::= XOR_EQUAL
            //
            case 411: {
                btParser.setSym1(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 412:  AssignmentOperator ::= OR_EQUAL
            //
            case 412: {
                btParser.setSym1(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 413:  Expression ::= AssignmentExpression
            //
            case 413:
                break;
 
            //
            // Rule 414:  ConstantExpression ::= Expression
            //
            case 414:
                break;
 
            //
            // Rule 415:  Dimsopt ::=
            //
            case 415: {
                btParser.setSym1(new Integer(0));
                break;
            }
     
            //
            // Rule 416:  Dimsopt ::= Dims
            //
            case 416:
                break;
 
            //
            // Rule 417:  Catchesopt ::=
            //
            case 417: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 418:  Catchesopt ::= Catches
            //
            case 418:
                break;
 
            //
            // Rule 419:  identifieropt ::=
            //
            case 419:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 420:  identifieropt ::= identifier
            //
            case 420: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 421:  ForUpdateopt ::=
            //
            case 421: {
                btParser.setSym1(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 422:  ForUpdateopt ::= ForUpdate
            //
            case 422:
                break;
 
            //
            // Rule 423:  Expressionopt ::=
            //
            case 423:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 424:  Expressionopt ::= Expression
            //
            case 424:
                break;
 
            //
            // Rule 425:  ForInitopt ::=
            //
            case 425: {
                btParser.setSym1(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 426:  ForInitopt ::= ForInit
            //
            case 426:
                break;
 
            //
            // Rule 427:  SwitchLabelsopt ::=
            //
            case 427: {
                btParser.setSym1(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 428:  SwitchLabelsopt ::= SwitchLabels
            //
            case 428:
                break;
 
            //
            // Rule 429:  SwitchBlockStatementGroupsopt ::=
            //
            case 429: {
                btParser.setSym1(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 430:  SwitchBlockStatementGroupsopt ::= SwitchBlockStatementGroups
            //
            case 430:
                break;
 
            //
            // Rule 431:  VariableModifiersopt ::=
            //
            case 431: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 432:  VariableModifiersopt ::= VariableModifiers
            //
            case 432:
                break;
 
            //
            // Rule 433:  VariableInitializersopt ::=
            //
            case 433:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 434:  VariableInitializersopt ::= VariableInitializers
            //
            case 434:
                break;
 
            //
            // Rule 435:  ElementValuesopt ::=
            //
            case 435:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 436:  ElementValuesopt ::= ElementValues
            //
            case 436:
                bad_rule = 436;
                break;
 
            //
            // Rule 437:  ElementValuePairsopt ::=
            //
            case 437:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 438:  ElementValuePairsopt ::= ElementValuePairs
            //
            case 438:
                bad_rule = 438;
                break;
 
            //
            // Rule 439:  DefaultValueopt ::=
            //
            case 439:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 440:  DefaultValueopt ::= DefaultValue
            //
            case 440:
                break;
 
            //
            // Rule 441:  AnnotationTypeElementDeclarationsopt ::=
            //
            case 441:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 442:  AnnotationTypeElementDeclarationsopt ::= AnnotationTypeElementDeclarations
            //
            case 442:
                bad_rule = 442;
                break;
 
            //
            // Rule 443:  AbstractMethodModifiersopt ::=
            //
            case 443: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 444:  AbstractMethodModifiersopt ::= AbstractMethodModifiers
            //
            case 444:
                break;
 
            //
            // Rule 445:  ConstantModifiersopt ::=
            //
            case 445: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 446:  ConstantModifiersopt ::= ConstantModifiers
            //
            case 446:
                break;
 
            //
            // Rule 447:  InterfaceMemberDeclarationsopt ::=
            //
            case 447: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 448:  InterfaceMemberDeclarationsopt ::= InterfaceMemberDeclarations
            //
            case 448:
                break;
 
            //
            // Rule 449:  ExtendsInterfacesopt ::=
            //
            case 449: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 450:  ExtendsInterfacesopt ::= ExtendsInterfaces
            //
            case 450:
                break;
 
            //
            // Rule 451:  InterfaceModifiersopt ::=
            //
            case 451: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 452:  InterfaceModifiersopt ::= InterfaceModifiers
            //
            case 452:
                break;
 
            //
            // Rule 453:  ClassBodyopt ::=
            //
            case 453:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 454:  ClassBodyopt ::= ClassBody
            //
            case 454:
                break;
 
            //
            // Rule 455:  Argumentsopt ::=
            //
            case 455:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 456:  Argumentsopt ::= Arguments
            //
            case 456:
                bad_rule = 456;
                break;
 
            //
            // Rule 457:  EnumBodyDeclarationsopt ::=
            //
            case 457:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 458:  EnumBodyDeclarationsopt ::= EnumBodyDeclarations
            //
            case 458:
                bad_rule = 458;
                break;
 
            //
            // Rule 459:  ,opt ::=
            //
            case 459:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 460:  ,opt ::= COMMA
            //
            case 460:
                break;
 
            //
            // Rule 461:  EnumConstantsopt ::=
            //
            case 461:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 462:  EnumConstantsopt ::= EnumConstants
            //
            case 462:
                bad_rule = 462;
                break;
 
            //
            // Rule 463:  ArgumentListopt ::=
            //
            case 463: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 464:  ArgumentListopt ::= ArgumentList
            //
            case 464:
                break;
 
            //
            // Rule 465:  BlockStatementsopt ::=
            //
            case 465: {
                btParser.setSym1(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 466:  BlockStatementsopt ::= BlockStatements
            //
            case 466:
                break;
 
            //
            // Rule 467:  ExplicitConstructorInvocationopt ::=
            //
            case 467:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 468:  ExplicitConstructorInvocationopt ::= ExplicitConstructorInvocation
            //
            case 468:
                break;
 
            //
            // Rule 469:  ConstructorModifiersopt ::=
            //
            case 469: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 470:  ConstructorModifiersopt ::= ConstructorModifiers
            //
            case 470:
                break;
 
            //
            // Rule 471:  ...opt ::=
            //
            case 471:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 472:  ...opt ::= ELLIPSIS
            //
            case 472:
                break;
 
            //
            // Rule 473:  FormalParameterListopt ::=
            //
            case 473: {
                btParser.setSym1(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 474:  FormalParameterListopt ::= FormalParameterList
            //
            case 474:
                break;
 
            //
            // Rule 475:  Throwsopt ::=
            //
            case 475: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 476:  Throwsopt ::= Throws
            //
            case 476:
                break;
 
            //
            // Rule 477:  MethodModifiersopt ::=
            //
            case 477: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 478:  MethodModifiersopt ::= MethodModifiers
            //
            case 478:
                break;
 
            //
            // Rule 479:  FieldModifiersopt ::=
            //
            case 479: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 480:  FieldModifiersopt ::= FieldModifiers
            //
            case 480:
                break;
 
            //
            // Rule 481:  ClassBodyDeclarationsopt ::=
            //
            case 481: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 482:  ClassBodyDeclarationsopt ::= ClassBodyDeclarations
            //
            case 482:
                break;
 
            //
            // Rule 483:  Interfacesopt ::=
            //
            case 483: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 484:  Interfacesopt ::= Interfaces
            //
            case 484:
                break;
 
            //
            // Rule 485:  Superopt ::=
            //
            case 485:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 486:  Superopt ::= Super
            //
            case 486:
                break;
 
            //
            // Rule 487:  TypeParametersopt ::=
            //
            case 487:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 488:  TypeParametersopt ::= TypeParameters
            //
            case 488:
                break;
 
            //
            // Rule 489:  ClassModifiersopt ::=
            //
            case 489: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 490:  ClassModifiersopt ::= ClassModifiers
            //
            case 490:
                break;
 
            //
            // Rule 491:  Annotationsopt ::=
            //
            case 491:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 492:  Annotationsopt ::= Annotations
            //
            case 492:
                bad_rule = 492;
                break;
 
            //
            // Rule 493:  TypeDeclarationsopt ::=
            //
            case 493: {
                btParser.setSym1(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 494:  TypeDeclarationsopt ::= TypeDeclarations
            //
            case 494:
                break;
 
            //
            // Rule 495:  ImportDeclarationsopt ::=
            //
            case 495: {
                btParser.setSym1(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 496:  ImportDeclarationsopt ::= ImportDeclarations
            //
            case 496:
                break;
 
            //
            // Rule 497:  PackageDeclarationopt ::=
            //
            case 497:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 498:  PackageDeclarationopt ::= PackageDeclaration
            //
            case 498:
                break;
 
            //
            // Rule 499:  WildcardBoundsOpt ::=
            //
            case 499:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 500:  WildcardBoundsOpt ::= WildcardBounds
            //
            case 500:
                bad_rule = 500;
                break;
 
            //
            // Rule 501:  AdditionalBoundListopt ::=
            //
            case 501:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 502:  AdditionalBoundListopt ::= AdditionalBoundList
            //
            case 502:
                bad_rule = 502;
                break;
 
            //
            // Rule 503:  TypeBoundopt ::=
            //
            case 503:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 504:  TypeBoundopt ::= TypeBound
            //
            case 504:
                bad_rule = 504;
                break;
 
            //
            // Rule 505:  TypeArgumentsopt ::=
            //
            case 505:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 506:  TypeArgumentsopt ::= TypeArguments
            //
            case 506:
                bad_rule = 506;
                break;
 
            //
            // Rule 507:  Type ::= DataType PlaceTypeSpecifieropt
            //
            case 507: {
                assert(btParser.getSym(2) == null);
                //btParser.setSym1();
                    break;
            }
         
            //
            // Rule 508:  Type ::= nullable LESS Type GREATER
            //
            case 508: {
                TypeNode a = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Nullable(pos(), a));
                          break;
            }
              
            //
            // Rule 509:  Type ::= future LESS Type GREATER
            //
            case 509: {
                TypeNode a = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Future(pos(), a));
                          break;
            }

              
            //
            // Rule 510:  Type ::= boxed LESS Type GREATER
            //
            case 510:
                bad_rule = 510;
                break; 
 
            //
            // Rule 511:  Type ::= fun LESS Type COMMA Type GREATER
            //
            case 511:
                bad_rule = 511;
                break; 
 
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
                bad_rule = 515;
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
                Expr c = (Expr) btParser.getSym(5);
                Expr d = (Expr) btParser.getSym(7);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, false, c, d));
                break;
            }
     
            //
            // Rule 543:  ArrayCreationExpression ::= new ArrayBaseType value Unsafeopt LBRACKET Expression RBRACKET
            //
            case 543: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                boolean unsafe = (btParser.getSym(3) != null);
                Expr c = (Expr) btParser.getSym(6);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, true, c, null));
                break;
            }
     
            //
            // Rule 544:  ArrayCreationExpression ::= new ArrayBaseType value Unsafeopt LBRACKET Expression RBRACKET Expression
            //
            case 544: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                boolean unsafe = (btParser.getSym(4) != null);
                Expr c = (Expr) btParser.getSym(6);
                Expr d = (Expr) btParser.getSym(8);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, unsafe, true, c, d));
                break;
            }
                
            //
            // Rule 545:  ArrayBaseType ::= PrimitiveType
            //
            case 545:
                break;
          
            
            //
            // Rule 546:  ArrayBaseType ::= ClassOrInterfaceType
            //
            case 546:
                break;
          
            //
            // Rule 547:  ArrayAccess ::= ExpressionName LBRACKET ArgumentList RBRACKET
            //
            case 547: {
           Name e = (Name) btParser.getSym(1);
           List b = (List) btParser.getSym(3);
           if (b.size() == 1)
           btParser.setSym1(nf.X10ArrayAccess1(pos(), e.toExpr(), (Expr) b.get(0)));
           else btParser.setSym1(nf.X10ArrayAccess(pos(), e.toExpr(), b));
                     break;
            }
         
            //
            // Rule 548:  ArrayAccess ::= PrimaryNoNewArray LBRACKET ArgumentList RBRACKET
            //
            case 548: { 
           Expr a = (Expr) btParser.getSym(1);
           List b = (List) btParser.getSym(3);
           if (b.size() == 1)
           btParser.setSym1(nf.X10ArrayAccess1(pos(), a, (Expr) b.get(0)));
           else btParser.setSym1(nf.X10ArrayAccess(pos(), a, b));
                    break;
            }
        
            //
            // Rule 549:  Statement ::= NowStatement
            //
            case 549:
                break; 
 
            //
            // Rule 550:  Statement ::= ClockedStatement
            //
            case 550:
                break; 
 
            //
            // Rule 551:  Statement ::= AsyncStatement
            //
            case 551:
                break; 
 
            //
            // Rule 552:  Statement ::= AtomicStatement
            //
            case 552:
                break; 
 
            //
            // Rule 553:  Statement ::= WhenStatement
            //
            case 553:
                break; 
 
            //
            // Rule 554:  Statement ::= ForEachStatement
            //
            case 554:
                break; 
 
            //
            // Rule 555:  Statement ::= AtEachStatement
            //
            case 555:
                break; 
 
            //
            // Rule 556:  Statement ::= FinishStatement
            //
            case 556:
                break; 
 
            //
            // Rule 557:  StatementWithoutTrailingSubstatement ::= NextStatement
            //
            case 557:
                break; 
 
            //
            // Rule 558:  StatementWithoutTrailingSubstatement ::= AwaitStatement
            //
            case 558:
                break; 
 
            //
            // Rule 559:  StatementNoShortIf ::= NowStatementNoShortIf
            //
            case 559:
                break; 
 
            //
            // Rule 560:  StatementNoShortIf ::= ClockedStatementNoShortIf
            //
            case 560:
                break; 
 
            //
            // Rule 561:  StatementNoShortIf ::= AsyncStatementNoShortIf
            //
            case 561:
                break; 
 
            //
            // Rule 562:  StatementNoShortIf ::= AtomicStatementNoShortIf
            //
            case 562:
                break; 
 
            //
            // Rule 563:  StatementNoShortIf ::= WhenStatementNoShortIf
            //
            case 563:
                break; 
 
            //
            // Rule 564:  StatementNoShortIf ::= ForEachStatementNoShortIf
            //
            case 564:
                break; 
 
            //
            // Rule 565:  StatementNoShortIf ::= AtEachStatementNoShortIf
            //
            case 565:
                break; 
 
            //
            // Rule 566:  StatementNoShortIf ::= FinishStatementNoShortIf
            //
            case 566:
                break; 
 
            //
            // Rule 567:  NowStatement ::= now LPAREN Clock RPAREN Statement
            //
            case 567: {
                Name a = (Name) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Now(pos(), a.toExpr(), b));
                break;
            }
     
            //
            // Rule 568:  ClockedStatement ::= clocked LPAREN ClockList RPAREN Statement
            //
            case 568: {
                List a = (List) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), a, b));
                break;
            }
     
            //
            // Rule 569:  AsyncStatement ::= async PlaceExpressionSingleListopt Statement
            //
            case 569: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Async(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 570:  AsyncStatement ::= async LPAREN here RPAREN Statement
            //
            case 570: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Async(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 571:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 571: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Atomic(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 572:  AtomicStatement ::= atomic LPAREN here RPAREN Statement
            //
            case 572: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Atomic(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 573:  WhenStatement ::= when LPAREN Expression RPAREN Statement
            //
            case 573: {
                Expr e = (Expr) btParser.getSym(3);
                Stmt s = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.When(pos(), e,s));
                break;
            }
     
            //
            // Rule 574:  WhenStatement ::= WhenStatement or LPAREN Expression RPAREN Statement
            //
            case 574: {
                When w = (When) btParser.getSym(1);
                Expr e = (Expr) btParser.getSym(4);
                Stmt s = (Stmt) btParser.getSym(6);
                w.add(new When_c.Branch_c(e,s));
                btParser.setSym1(w);
                break;
            }
     
            //
            // Rule 575:  ForEachStatement ::= foreach LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 575: { 
               Formal f = (Formal) btParser.getSym(3);
               f = f.flags(f.flags().Final());
               Expr  e = (Expr) btParser.getSym(5);
               Stmt s =  (Stmt) btParser.getSym(7);
               X10Loop x = nf.ForEach(pos(), f, e, s);
               btParser.setSym1(x);
                 break;
            }
     
            //
            // Rule 576:  AtEachStatement ::= ateach LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 576: { 
               Formal f = (Formal) btParser.getSym(3);
               f = f.flags(f.flags().Final());
               Expr  e = (Expr) btParser.getSym(5);
               Stmt s =  (Stmt) btParser.getSym(7);
               X10Loop x = nf.AtEach(pos(), f, e, s);
               btParser.setSym1(x);
                 break;
            }
     
            //
            // Rule 577:  EnhancedForStatement ::= for LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 577: {
               Formal f = (Formal) btParser.getSym(3);
               f = f.flags(f.flags().Final());
               Expr  e = (Expr) btParser.getSym(5);
               Stmt s =  (Stmt) btParser.getSym(7);
               X10Loop x = nf.ForLoop(pos(), f, e, s);
               btParser.setSym1(x);
               break;
            }  
            //
            // Rule 578:  FinishStatement ::= finish Statement
            //
            case 578: {
                Stmt b = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Finish(pos(),  b));
                break;
            }
     
            //
            // Rule 579:  NowStatementNoShortIf ::= now LPAREN Clock RPAREN StatementNoShortIf
            //
            case 579: {
                Name a = (Name) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Now(pos(), a.toExpr(), b));
                break;
            }
     
            //
            // Rule 580:  ClockedStatementNoShortIf ::= clocked LPAREN ClockList RPAREN StatementNoShortIf
            //
            case 580: {
                List a = (List) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), a, b));
                break;
            }
     
            //
            // Rule 581:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt StatementNoShortIf
            //
            case 581: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Async(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 582:  AsyncStatementNoShortIf ::= async LPAREN here RPAREN StatementNoShortIf
            //
            case 582: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Async(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 583:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 583: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Atomic(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 584:  AtomicStatementNoShortIf ::= atomic LPAREN here RPAREN StatementNoShortIf
            //
            case 584: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Atomic(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 585:  WhenStatementNoShortIf ::= when LPAREN Expression RPAREN StatementNoShortIf
            //
            case 585: {
                Expr e = (Expr) btParser.getSym(3);
                Stmt s = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.When(pos(), e,s));
                break;
            }
     
            //
            // Rule 586:  WhenStatementNoShortIf ::= WhenStatement or LPAREN Expression RPAREN StatementNoShortIf
            //
            case 586: {
                When w = (When) btParser.getSym(1);
                Expr e = (Expr) btParser.getSym(4);
                Stmt s = (Stmt) btParser.getSym(6);
                w.add(new When_c.Branch_c(e,s));
                btParser.setSym1(w);
                break;
            }
     
            //
            // Rule 587:  ForEachStatementNoShortIf ::= foreach LPAREN FormalParameter COLON Expression RPAREN StatementNoShortIf
            //
            case 587: { 
               Formal f = (Formal) btParser.getSym(3);
               f = f.flags(f.flags().Final());
               Expr  e = (Expr) btParser.getSym(5);
               Stmt s =  (Stmt) btParser.getSym(7);
               X10Loop x = nf.ForEach(pos(), f, e, s);
               btParser.setSym1(x);
                 break;
            }
     
            //
            // Rule 588:  AtEachStatementNoShortIf ::= ateach LPAREN FormalParameter COLON Expression RPAREN StatementNoShortIf
            //
            case 588: { 
               Formal f = (Formal) btParser.getSym(3);
               f = f.flags(f.flags().Final());
               Expr  e = (Expr) btParser.getSym(5);
               Stmt s =  (Stmt) btParser.getSym(7);
               X10Loop x = nf.AtEach(pos(), f, e, s);
               btParser.setSym1(x);
                 break;
            }
     
            //
            // Rule 589:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 589: {
                Stmt b = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Finish(pos(),  b));
                break;
            }
     
            //
            // Rule 590:  PlaceExpressionSingleList ::= LPAREN PlaceExpression RPAREN
            //
            case 590: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 591:  PlaceExpression ::= here
            //
            case 591: {
                  btParser.setSym1(nf.Here(pos(btParser.getFirstToken())));
                break;
            }
     
            //
            // Rule 592:  PlaceExpression ::= this
            //
            case 592: {
                btParser.setSym1(nf.Field(pos(btParser.getFirstToken()), nf.This(pos(btParser.getFirstToken())), "place"));
                break;
            }
     
            //
            // Rule 593:  PlaceExpression ::= Expression
            //
            case 593:
                break;
     
            //
            // Rule 594:  PlaceExpression ::= ArrayAccess
            //
            case 594:
                bad_rule = 594;
                break; 
 
            //
            // Rule 595:  NextStatement ::= next SEMICOLON
            //
            case 595: {
                btParser.setSym1(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 596:  AwaitStatement ::= await Expression SEMICOLON
            //
            case 596: { 
         Expr e = (Expr) btParser.getSym(2);
         btParser.setSym1(nf.Await(pos(), e));
                 break;
            }
     
            //
            // Rule 597:  ClockList ::= Clock
            //
            case 597: {
                Name c = (Name) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(c.toExpr());
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 598:  ClockList ::= ClockList COMMA Clock
            //
            case 598: {
                List l = (List) btParser.getSym(1);
                Name c = (Name) btParser.getSym(3);
                l.add(c.toExpr());
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 599:  Clock ::= identifier
            //
            case 599: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 600:  CastExpression ::= LPAREN Type RPAREN UnaryExpressionNotPlusMinus
            //
            case 600: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Cast(pos(), a, b));
                break;
            }
     
            //
            // Rule 601:  MethodInvocation ::= Primary ARROW identifier LPAREN ArgumentListopt RPAREN
            //
            case 601: { 
          Expr a = (Expr) btParser.getSym(1);
          polyglot.lex.Identifier b = id(btParser.getToken(3));
          List c = (List) btParser.getSym(5);
          btParser.setSym1(nf.RemoteCall(pos(), a, b.getIdentifier(), c));
                 break;
            } 
     
            //
            // Rule 602:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 602: {
                Expr a = (Expr) btParser.getSym(1);
                TypeNode b = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Instanceof(pos(), a, b));
                break;
            }
     
            //
            // Rule 603:  ExpressionName ::= here
            //
            case 603: {
          btParser.setSym1(new Name(nf, ts, pos(), "here"){
              public Expr toExpr() {
                 return nf.Here(pos);
              }
           });

                  break;
            }
       
            //
            // Rule 604:  Primary ::= FutureExpression
            //
            case 604:
                break; 
 
            //
            // Rule 605:  FutureExpression ::= future PlaceExpressionSingleListopt LBRACE Expression RBRACE
            //
            case 605: {
                Expr e1 = (Expr) btParser.getSym(2),
                     e2 = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Future(pos(), (e1 == null ? nf.Here(pos(btParser.getFirstToken())) : e1), e2));
                break;
            }
     
            //
            // Rule 606:  FutureExpression ::= future LPAREN here RPAREN LBRACE Expression RBRACE
            //
            case 606: {
                Expr e2 = (Expr) btParser.getSym(6);
                btParser.setSym1(nf.Future(pos(), nf.Here(pos(btParser.getFirstToken(3))), e2));
                break;
            }
     
            //
            // Rule 607:  FunExpression ::= fun Type LPAREN FormalParameterListopt RPAREN LBRACE Expression RBRACE
            //
            case 607:
                bad_rule = 607;
                break; 
 
            //
            // Rule 608:  MethodInvocation ::= MethodName LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 608:
                bad_rule = 608;
                break; 
 
            //
            // Rule 609:  MethodInvocation ::= Primary DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 609:
                bad_rule = 609;
                break; 
 
            //
            // Rule 610:  MethodInvocation ::= super DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 610:
                bad_rule = 610;
                break; 
 
            //
            // Rule 611:  MethodInvocation ::= ClassName DOT super DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 611:
                bad_rule = 611;
                break; 
 
            //
            // Rule 612:  MethodInvocation ::= TypeName DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 612:
                bad_rule = 612;
                break; 
 
            //
            // Rule 613:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 613:
                bad_rule = 613;
                break; 
 
            //
            // Rule 614:  ClassInstanceCreationExpression ::= Primary DOT new identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 614:
                bad_rule = 614;
                break; 
 
            //
            // Rule 615:  ClassInstanceCreationExpression ::= AmbiguousName DOT new identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 615:
                bad_rule = 615;
                break; 
 
            //
            // Rule 616:  PlaceTypeSpecifieropt ::=
            //
            case 616:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 617:  PlaceTypeSpecifieropt ::= PlaceTypeSpecifier
            //
            case 617:
                break; 
 
            //
            // Rule 618:  DepParametersopt ::=
            //
            case 618:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 619:  DepParametersopt ::= DepParameters
            //
            case 619:
                break; 
 
            //
            // Rule 620:  WhereClauseopt ::=
            //
            case 620:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 621:  WhereClauseopt ::= WhereClause
            //
            case 621:
                break; 
 
            //
            // Rule 622:  ObjectKindopt ::=
            //
            case 622:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 623:  ObjectKindopt ::= ObjectKind
            //
            case 623:
                break; 
 
            //
            // Rule 624:  ArrayInitializeropt ::=
            //
            case 624:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 625:  ArrayInitializeropt ::= ArrayInitializer
            //
            case 625:
                break; 
 
            //
            // Rule 626:  ConcreteDistributionopt ::=
            //
            case 626:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 627:  ConcreteDistributionopt ::= ConcreteDistribution
            //
            case 627:
                break; 
 
            //
            // Rule 628:  PlaceExpressionSingleListopt ::=
            //
            case 628:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 629:  PlaceExpressionSingleListopt ::= PlaceExpressionSingleList
            //
            case 629:
                break; 
 
            //
            // Rule 630:  ArgumentListopt ::=
            //
            case 630:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 631:  ArgumentListopt ::= ArgumentList
            //
            case 631:
                break; 
 
            //
            // Rule 632:  DepParametersopt ::=
            //
            case 632:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 633:  DepParametersopt ::= DepParameters
            //
            case 633:
                break; 
 
            //
            // Rule 634:  Unsafeopt ::=
            //
            case 634:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 635:  Unsafeopt ::= unsafe
            //
            case 635: { btParser.setSym1(nf.Here(pos(btParser.getFirstToken(1))));           break;
            } 
    
            default:
                break;
        }
        return;
    }
}

