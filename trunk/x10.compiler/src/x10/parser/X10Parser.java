
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
           System.out.println("Recognized comment |" + s + "|");
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

    private polyglot.lex.IntegerLiteral int_lit(int i, int radix)
    {
        long x = parseLong(prsStream.getName(i), radix);
        return new IntegerLiteral(pos(i), (int) x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.LongLiteral long_lit(int i, int radix)
    {
        long x = parseLong(prsStream.getName(i), radix);
        return new LongLiteral(pos(i), x, X10Parsersym.TK_LongLiteral);
    }

    private polyglot.lex.FloatLiteral float_lit(int i)
    {
        try {
            float x = Float.parseFloat(prsStream.getName(i));
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
            double x = Double.parseDouble(prsStream.getName(i));
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
                    System.out.println("Turning keyword " +
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
            // Rule 15:  ClassOrInterfaceType ::= ClassType
            //
            case 15:
                break;
 
            //
            // Rule 16:  ClassType ::= TypeName
            //
            case 16: {
//vj                    assert(btParser.getSym(2) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toType());
                break;
            }
     
            //
            // Rule 17:  InterfaceType ::= TypeName
            //
            case 17: {
//vj                    assert(btParser.getSym(2) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toType());
                break;
            }
     
            //
            // Rule 18:  TypeName ::= identifier
            //
            case 18: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 19:  TypeName ::= TypeName DOT identifier
            //
            case 19: {
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
            // Rule 20:  ClassName ::= TypeName
            //
            case 20:
                break;
 
            //
            // Rule 21:  TypeVariable ::= identifier
            //
            case 21:
                break;
 
            //
            // Rule 22:  ArrayType ::= Type LBRACKET RBRACKET
            //
            case 22: {
                TypeNode a = (TypeNode) btParser.getSym(1);
                btParser.setSym1(array(a, pos(), 1));
                break;
            }
     
            //
            // Rule 23:  TypeParameter ::= TypeVariable TypeBoundopt
            //
            case 23:
                bad_rule = 23;
                break;
 
            //
            // Rule 24:  TypeBound ::= extends ClassOrInterfaceType AdditionalBoundListopt
            //
            case 24:
                bad_rule = 24;
                break;
 
            //
            // Rule 25:  AdditionalBoundList ::= AdditionalBound
            //
            case 25:
                bad_rule = 25;
                break;
 
            //
            // Rule 26:  AdditionalBoundList ::= AdditionalBoundList AdditionalBound
            //
            case 26:
                bad_rule = 26;
                break;
 
            //
            // Rule 27:  AdditionalBound ::= AND InterfaceType
            //
            case 27:
                bad_rule = 27;
                break;
 
            //
            // Rule 28:  TypeArguments ::= LESS ActualTypeArgumentList GREATER
            //
            case 28:
                bad_rule = 28;
                break;
 
            //
            // Rule 29:  ActualTypeArgumentList ::= ActualTypeArgument
            //
            case 29:
                bad_rule = 29;
                break;
 
            //
            // Rule 30:  ActualTypeArgumentList ::= ActualTypeArgumentList COMMA ActualTypeArgument
            //
            case 30:
                bad_rule = 30;
                break;
 
            //
            // Rule 31:  Wildcard ::= QUESTION WildcardBoundsOpt
            //
            case 31:
                bad_rule = 31;
                break;
 
            //
            // Rule 32:  WildcardBounds ::= extends ReferenceType
            //
            case 32:
                bad_rule = 32;
                break;
 
            //
            // Rule 33:  WildcardBounds ::= super ReferenceType
            //
            case 33:
                bad_rule = 33;
                break;
 
            //
            // Rule 34:  PackageName ::= identifier
            //
            case 34: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 35:  PackageName ::= PackageName DOT identifier
            //
            case 35: {
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
            // Rule 36:  ExpressionName ::= identifier
            //
            case 36: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 37:  ExpressionName ::= AmbiguousName DOT identifier
            //
            case 37: {
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
            // Rule 38:  MethodName ::= identifier
            //
            case 38: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 39:  MethodName ::= AmbiguousName DOT identifier
            //
            case 39: {
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
            // Rule 40:  PackageOrTypeName ::= identifier
            //
            case 40: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 41:  PackageOrTypeName ::= PackageOrTypeName DOT identifier
            //
            case 41: {
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
            // Rule 42:  AmbiguousName ::= identifier
            //
            case 42: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 43:  AmbiguousName ::= AmbiguousName DOT identifier
            //
            case 43: {
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
            // Rule 44:  CompilationUnit ::= Commentsopt PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 44: {
                Object comment = btParser.getSym(1);
                PackageNode a = (PackageNode) btParser.getSym(2);
                List b = (List) btParser.getSym(3),
                     c = (List) btParser.getSym(4);
                Node n = nf.SourceFile(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b, c);
                if (comment != null) {
                  System.out.println("Read in comment |" + comment + "|");
                  n.setComment(comment.toString());
                }
                btParser.setSym1(n);
                break;
            }
     
            //
            // Rule 45:  Comments ::= Comment
            //
            case 45: {
         Object comment = comment(btParser.getToken(1));
         btParser.setSym1(comment);
                   break;
            }
       
            //
            // Rule 46:  Comments ::= Comments Comment
            //
            case 46: {
        btParser.setSym1(btParser.getSym(2));
                  break;
            }
       
            //
            // Rule 47:  ImportDeclarations ::= ImportDeclaration
            //
            case 47: {
                List l = new TypedList(new LinkedList(), Import.class, false);
                Import a = (Import) btParser.getSym(1);
                l.add(a);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 48:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 48: {
                List l = (TypedList) btParser.getSym(1);
                Import b = (Import) btParser.getSym(2);
                if (b != null)
                    l.add(b);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 49:  TypeDeclarations ::= Commentsopt TypeDeclaration
            //
            case 49: {
                Object comment = btParser.getSym(1);
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                TopLevelDecl a = (TopLevelDecl) btParser.getSym(2);
                if (a != null)
                    l.add(a);
                if (comment != null) {
                    System.out.println("Comment2 recognized:|" + comment + "|"); 
                    a.setComment(comment.toString());
                }
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 50:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 50: {
                List l = (TypedList) btParser.getSym(1);
                TopLevelDecl b = (TopLevelDecl) btParser.getSym(2);
                if (b != null)
                    l.add(b);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 51:  PackageDeclaration ::= package PackageName SEMICOLON
            //
            case 51: {
//vj                    assert(btParser.getSym(1) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(a.toPackage());
                break;
            }
     
            //
            // Rule 52:  ImportDeclaration ::= SingleTypeImportDeclaration
            //
            case 52:
                break;
 
            //
            // Rule 53:  ImportDeclaration ::= TypeImportOnDemandDeclaration
            //
            case 53:
                break;
 
            //
            // Rule 54:  ImportDeclaration ::= SingleStaticImportDeclaration
            //
            case 54:
                break;
 
            //
            // Rule 55:  ImportDeclaration ::= StaticImportOnDemandDeclaration
            //
            case 55:
                break;
 
            //
            // Rule 56:  SingleTypeImportDeclaration ::= import TypeName SEMICOLON
            //
            case 56: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.CLASS, a.toString()));
                break;
            }
     
            //
            // Rule 57:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName DOT MULTIPLY SEMICOLON
            //
            case 57: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.PACKAGE, a.toString()));
                break;
            }
     
            //
            // Rule 58:  SingleStaticImportDeclaration ::= import static TypeName DOT identifier SEMICOLON
            //
            case 58:
                bad_rule = 58;
                break;
 
            //
            // Rule 59:  StaticImportOnDemandDeclaration ::= import static TypeName DOT MULTIPLY SEMICOLON
            //
            case 59:
                bad_rule = 59;
                break;
 
            //
            // Rule 60:  TypeDeclaration ::= ClassDeclaration
            //
            case 60:
                break;
 
            //
            // Rule 61:  TypeDeclaration ::= InterfaceDeclaration
            //
            case 61:
                break;
 
            //
            // Rule 62:  TypeDeclaration ::= SEMICOLON
            //
            case 62: {
                btParser.setSym1(null);
                break;
            }
     
            //
            // Rule 63:  ClassDeclaration ::= NormalClassDeclaration
            //
            case 63:
                break;
 
            //
            // Rule 64:  NormalClassDeclaration ::= Commentsopt ClassModifiersopt class identifier Superopt Interfacesopt ClassBody
            //
            case 64: {
                Object comment = btParser.getSym(1);
                Flags a = (Flags) btParser.getSym(2);
                polyglot.lex.Identifier b = id(btParser.getToken(4));
//vj                    assert(btParser.getSym(4) == null);
                TypeNode c = (TypeNode) btParser.getSym(5);
                List d = (List) btParser.getSym(6);
                ClassBody e = (ClassBody) btParser.getSym(7);
                Node n = 
                   a.isValue() ?
                     nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), 
                                       a, b.getIdentifier(), c, d, e) 
                    : nf.ClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), 
                                       a, b.getIdentifier(), c, d, e);
                if (comment != null)
                  n.setComment(comment.toString());
                btParser.setSym1(n);
                break;
            }
     
            //
            // Rule 65:  ClassModifiers ::= ClassModifier
            //
            case 65:
                break;
 
            //
            // Rule 66:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 66: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 67:  ClassModifier ::= public
            //
            case 67: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 68:  ClassModifier ::= protected
            //
            case 68: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 69:  ClassModifier ::= private
            //
            case 69: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 70:  ClassModifier ::= abstract
            //
            case 70: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 71:  ClassModifier ::= static
            //
            case 71: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 72:  ClassModifier ::= final
            //
            case 72: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 73:  ClassModifier ::= strictfp
            //
            case 73: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 74:  TypeParameters ::= LESS TypeParameterList GREATER
            //
            case 74:
                bad_rule = 74;
                break;
 
            //
            // Rule 75:  TypeParameterList ::= TypeParameter
            //
            case 75:
                bad_rule = 75;
                break;
 
            //
            // Rule 76:  TypeParameterList ::= TypeParameterList COMMA TypeParameter
            //
            case 76:
                bad_rule = 76;
                break;
 
            //
            // Rule 77:  Super ::= extends ClassType
            //
            case 77: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 78:  Interfaces ::= implements InterfaceTypeList
            //
            case 78: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 79:  InterfaceTypeList ::= InterfaceType
            //
            case 79: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 80:  InterfaceTypeList ::= InterfaceTypeList COMMA InterfaceType
            //
            case 80: {
                List l = (TypedList) btParser.getSym(1);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 81:  ClassBody ::= LBRACE ClassBodyDeclarationsopt RBRACE
            //
            case 81: {
                btParser.setSym1(nf.ClassBody(pos(btParser.getFirstToken(), btParser.getLastToken()), (List) btParser.getSym(2)));
                break;
            }
     
            //
            // Rule 82:  ClassBodyDeclarations ::= ClassBodyDeclaration
            //
            case 82:
                break;
 
            //
            // Rule 83:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 83: {
                List a = (List) btParser.getSym(1),
                     b = (List) btParser.getSym(2);
                a.addAll(b);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 84:  ClassBodyDeclaration ::= ClassMemberDeclaration
            //
            case 84:
                break;
 
            //
            // Rule 85:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 85: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Block a = (Block) btParser.getSym(1);
                l.add(nf.Initializer(pos(), Flags.NONE, a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 86:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 86: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Block a = (Block) btParser.getSym(1);
                l.add(nf.Initializer(pos(), Flags.STATIC, a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 87:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 87: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 88:  ClassMemberDeclaration ::= FieldDeclaration
            //
            case 88:
                break;
 
            //
            // Rule 89:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 89: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 90:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 90: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 91:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 91: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 92:  ClassMemberDeclaration ::= SEMICOLON
            //
            case 92: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 93:  FieldDeclaration ::= Commentsopt FieldModifiersopt Type VariableDeclarators SEMICOLON
            //
            case 93: {
                Object comment = btParser.getSym(1);
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Flags a = (Flags) btParser.getSym(2);
                TypeNode b = (TypeNode) btParser.getSym(3);
                List c = (List) btParser.getSym(4);
                for (Iterator i = c.iterator(); i.hasNext();)
                {
                    VarDeclarator d = (VarDeclarator) i.next();
                    l.add(nf.FieldDecl(pos(btParser.getFirstToken(2), btParser.getLastToken()),
                                       a,
                                       array(b, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), d.dims),
                                       d.name,
                                       d.init));
                }
                if  (comment != null) 
                    ((Node) l.get(0)).setComment(comment.toString());
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 94:  VariableDeclarators ::= VariableDeclarator
            //
            case 94: {
                List l = new TypedList(new LinkedList(), VarDeclarator.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 95:  VariableDeclarators ::= VariableDeclarators COMMA VariableDeclarator
            //
            case 95: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 96:  VariableDeclarator ::= VariableDeclaratorId
            //
            case 96:
                break;
 
            //
            // Rule 97:  VariableDeclarator ::= VariableDeclaratorId EQUAL VariableInitializer
            //
            case 97: {
                VarDeclarator a = (VarDeclarator) btParser.getSym(1);
                Expr b = (Expr) btParser.getSym(3);
                a.init = b; 
                // btParser.setSym1(a); 
                break;
            }
     
            //
            // Rule 98:  VariableDeclaratorId ::= identifier
            //
            case 98: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new VarDeclarator(pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 99:  VariableDeclaratorId ::= VariableDeclaratorId LBRACKET RBRACKET
            //
            case 99: {
                VarDeclarator a = (VarDeclarator) btParser.getSym(1);
                a.dims++;
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 100:  VariableInitializer ::= Expression
            //
            case 100:
                break;
 
            //
            // Rule 101:  VariableInitializer ::= ArrayInitializer
            //
            case 101:
                break;
 
            //
            // Rule 102:  FieldModifiers ::= FieldModifier
            //
            case 102:
                break;
 
            //
            // Rule 103:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 103: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 104:  FieldModifier ::= public
            //
            case 104: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 105:  FieldModifier ::= protected
            //
            case 105: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 106:  FieldModifier ::= private
            //
            case 106: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 107:  FieldModifier ::= static
            //
            case 107: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 108:  FieldModifier ::= final
            //
            case 108: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 109:  FieldModifier ::= transient
            //
            case 109: {
                btParser.setSym1(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 110:  FieldModifier ::= volatile
            //
            case 110: {
                btParser.setSym1(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 111:  MethodDeclaration ::= Commentsopt MethodHeader MethodBody
            //
            case 111: {
                Object comment = btParser.getSym(1);
                MethodDecl a = (MethodDecl) btParser.getSym(2);
                Block b = (Block) btParser.getSym(3);
                if (comment != null) 
                  a.setComment(comment.toString());
                btParser.setSym1(a.body(b));
                break;
            }
     
            //
            // Rule 112:  MethodHeader ::= MethodModifiersopt ResultType MethodDeclarator Throwsopt
            //
            case 112: {
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
            // Rule 113:  ResultType ::= Type
            //
            case 113:
                break;
 
            //
            // Rule 114:  ResultType ::= void
            //
            case 114: {
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 115:  MethodDeclarator ::= identifier LPAREN FormalParameterListopt RPAREN
            //
            case 115: {
                Object[] a = new Object[3];
                a[0] =  new Name(nf, ts, pos(), id(btParser.getToken(1)).getIdentifier());
                a[1] = btParser.getSym(3);
                a[2] = new Integer(0);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 116:  MethodDeclarator ::= MethodDeclarator LBRACKET RBRACKET
            //
            case 116: {
                Object[] a = (Object []) btParser.getSym(1);
                a[2] = new Integer(((Integer) a[2]).intValue() + 1);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 117:  FormalParameterList ::= LastFormalParameter
            //
            case 117: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 118:  FormalParameterList ::= FormalParameters COMMA LastFormalParameter
            //
            case 118: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 119:  FormalParameters ::= FormalParameter
            //
            case 119: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 120:  FormalParameters ::= FormalParameters COMMA FormalParameter
            //
            case 120: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 121:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 121: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                VarDeclarator b = (VarDeclarator) btParser.getSym(3);
                btParser.setSym1(nf.Formal(pos(), f, array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b.name));
                break;
            }
     
            //
            // Rule 123:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 123: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 124:  VariableModifier ::= final
            //
            case 124: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 125:  LastFormalParameter ::= VariableModifiersopt Type ...opt VariableDeclaratorId
            //
            case 125: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                assert(btParser.getSym(3) == null);
                VarDeclarator b = (VarDeclarator) btParser.getSym(4);
                btParser.setSym1(nf.Formal(pos(), f, array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b.name));
                break;
            }
     
            //
            // Rule 126:  MethodModifiers ::= MethodModifier
            //
            case 126:
                break;
 
            //
            // Rule 127:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 127: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 128:  MethodModifier ::= public
            //
            case 128: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 129:  MethodModifier ::= protected
            //
            case 129: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 130:  MethodModifier ::= private
            //
            case 130: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 131:  MethodModifier ::= abstract
            //
            case 131: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 132:  MethodModifier ::= static
            //
            case 132: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 133:  MethodModifier ::= final
            //
            case 133: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 134:  MethodModifier ::= synchronized
            //
            case 134: {
                btParser.setSym1(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 135:  MethodModifier ::= native
            //
            case 135: {
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 136:  MethodModifier ::= strictfp
            //
            case 136: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 137:  Throws ::= throws ExceptionTypeList
            //
            case 137: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 138:  ExceptionTypeList ::= ExceptionType
            //
            case 138: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 139:  ExceptionTypeList ::= ExceptionTypeList COMMA ExceptionType
            //
            case 139: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 140:  ExceptionType ::= ClassType
            //
            case 140:
                break;
 
            //
            // Rule 141:  ExceptionType ::= TypeVariable
            //
            case 141:
                break;
 
            //
            // Rule 142:  MethodBody ::= Block
            //
            case 142:
                break;
 
            //
            // Rule 143:  MethodBody ::= SEMICOLON
            //
            case 143:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 144:  InstanceInitializer ::= Block
            //
            case 144:
                break;
 
            //
            // Rule 145:  StaticInitializer ::= static Block
            //
            case 145: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 146:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 146: {
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
            // Rule 147:  ConstructorDeclarator ::= SimpleTypeName LPAREN FormalParameterListopt RPAREN
            //
            case 147: {
//vj                    assert(btParser.getSym(1) == null);
                Object[] a = new Object[3];
//vj                    a[0] = btParser.getSym(1);
                a[1] = btParser.getSym(1);
                a[2] = btParser.getSym(3);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 148:  SimpleTypeName ::= identifier
            //
            case 148: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 149:  ConstructorModifiers ::= ConstructorModifier
            //
            case 149:
                break;
 
            //
            // Rule 150:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 150: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 151:  ConstructorModifier ::= public
            //
            case 151: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 152:  ConstructorModifier ::= protected
            //
            case 152: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 153:  ConstructorModifier ::= private
            //
            case 153: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 154:  ConstructorBody ::= LBRACE ExplicitConstructorInvocationopt BlockStatementsopt RBRACE
            //
            case 154: {
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
            // Rule 155:  ExplicitConstructorInvocation ::= this LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 155: {
//vj                    assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.ThisCall(pos(), b));
                break;
            }
     
            //
            // Rule 156:  ExplicitConstructorInvocation ::= super LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 156: {
//vj                    assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.SuperCall(pos(), b));
                break;
            }
     
            //
            // Rule 157:  ExplicitConstructorInvocation ::= Primary DOT this LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 157: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.ThisCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 158:  ExplicitConstructorInvocation ::= Primary DOT super LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 158: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.SuperCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 159:  EnumDeclaration ::= ClassModifiersopt enum identifier Interfacesopt EnumBody
            //
            case 159:
                bad_rule = 159;
                break;
 
            //
            // Rule 160:  EnumBody ::= LBRACE EnumConstantsopt ,opt EnumBodyDeclarationsopt RBRACE
            //
            case 160:
                bad_rule = 160;
                break;
 
            //
            // Rule 161:  EnumConstants ::= EnumConstant
            //
            case 161:
                bad_rule = 161;
                break;
 
            //
            // Rule 162:  EnumConstants ::= EnumConstants COMMA EnumConstant
            //
            case 162:
                bad_rule = 162;
                break;
 
            //
            // Rule 163:  EnumConstant ::= identifier Argumentsopt ClassBodyopt
            //
            case 163:
                bad_rule = 163;
                break;
 
            //
            // Rule 164:  Arguments ::= LPAREN ArgumentListopt RPAREN
            //
            case 164: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 165:  EnumBodyDeclarations ::= SEMICOLON ClassBodyDeclarationsopt
            //
            case 165:
                bad_rule = 165;
                break;
 
            //
            // Rule 166:  InterfaceDeclaration ::= NormalInterfaceDeclaration
            //
            case 166:
                break;
 
            //
            // Rule 167:  NormalInterfaceDeclaration ::= Commentsopt InterfaceModifiersopt interface identifier ExtendsInterfacesopt InterfaceBody
            //
            case 167: {
                Object comment = btParser.getSym(1);
                Flags a = (Flags) btParser.getSym(2);
                polyglot.lex.Identifier b = id(btParser.getToken(4));
//vj                    assert(btParser.getSym(4) == null);
                List c = (List) btParser.getSym(5);
                ClassBody d = (ClassBody) btParser.getSym(6);
                Node n = nf.ClassDecl(pos(), a.Interface(), b.getIdentifier(), 
                                      null, c, d);
                if (comment != null) n.setComment( comment.toString() );
                btParser.setSym1(n);
                break;
            }
     
            //
            // Rule 168:  InterfaceModifiers ::= InterfaceModifier
            //
            case 168:
                break;
 
            //
            // Rule 169:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 169: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 170:  InterfaceModifier ::= public
            //
            case 170: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 171:  InterfaceModifier ::= protected
            //
            case 171: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 172:  InterfaceModifier ::= private
            //
            case 172: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 173:  InterfaceModifier ::= abstract
            //
            case 173: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 174:  InterfaceModifier ::= static
            //
            case 174: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 175:  InterfaceModifier ::= strictfp
            //
            case 175: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 176:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 176: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 177:  ExtendsInterfaces ::= ExtendsInterfaces COMMA InterfaceType
            //
            case 177: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 178:  InterfaceBody ::= LBRACE InterfaceMemberDeclarationsopt RBRACE
            //
            case 178: {
                List a = (List)btParser.getSym(2);
                btParser.setSym1(nf.ClassBody(pos(), a));
                break;
            }
     
            //
            // Rule 179:  InterfaceMemberDeclarations ::= InterfaceMemberDeclaration
            //
            case 179:
                break;
 
            //
            // Rule 180:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 180: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 181:  InterfaceMemberDeclaration ::= ConstantDeclaration
            //
            case 181:
                break;
 
            //
            // Rule 182:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 182: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 183:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 183: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 184:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 184: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 185:  InterfaceMemberDeclaration ::= SEMICOLON
            //
            case 185: {
                btParser.setSym1(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 186:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 186: {
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
            // Rule 187:  ConstantModifiers ::= ConstantModifier
            //
            case 187:
                break;
 
            //
            // Rule 188:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 188: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 189:  ConstantModifier ::= public
            //
            case 189: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 190:  ConstantModifier ::= static
            //
            case 190: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 191:  ConstantModifier ::= final
            //
            case 191: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 192:  AbstractMethodDeclaration ::= AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt SEMICOLON
            //
            case 192: {
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
            // Rule 193:  AbstractMethodModifiers ::= AbstractMethodModifier
            //
            case 193:
                break;
 
            //
            // Rule 194:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 194: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 195:  AbstractMethodModifier ::= public
            //
            case 195: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 196:  AbstractMethodModifier ::= abstract
            //
            case 196: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 197:  AnnotationTypeDeclaration ::= InterfaceModifiersopt AT interface identifier AnnotationTypeBody
            //
            case 197:
                bad_rule = 197;
                break;
 
            //
            // Rule 198:  AnnotationTypeBody ::= LBRACE AnnotationTypeElementDeclarationsopt RBRACE
            //
            case 198:
                bad_rule = 198;
                break;
 
            //
            // Rule 199:  AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclaration
            //
            case 199:
                bad_rule = 199;
                break;
 
            //
            // Rule 200:  AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclarations AnnotationTypeElementDeclaration
            //
            case 200:
                bad_rule = 200;
                break;
 
            //
            // Rule 201:  AnnotationTypeElementDeclaration ::= AbstractMethodModifiersopt Type identifier LPAREN RPAREN DefaultValueopt SEMICOLON
            //
            case 201:
                bad_rule = 201;
                break;
 
            //
            // Rule 202:  AnnotationTypeElementDeclaration ::= ConstantDeclaration
            //
            case 202:
                bad_rule = 202;
                break;
 
            //
            // Rule 203:  AnnotationTypeElementDeclaration ::= ClassDeclaration
            //
            case 203:
                bad_rule = 203;
                break;
 
            //
            // Rule 204:  AnnotationTypeElementDeclaration ::= InterfaceDeclaration
            //
            case 204:
                bad_rule = 204;
                break;
 
            //
            // Rule 205:  AnnotationTypeElementDeclaration ::= EnumDeclaration
            //
            case 205:
                bad_rule = 205;
                break;
 
            //
            // Rule 206:  AnnotationTypeElementDeclaration ::= AnnotationTypeDeclaration
            //
            case 206:
                bad_rule = 206;
                break;
 
            //
            // Rule 207:  AnnotationTypeElementDeclaration ::= SEMICOLON
            //
            case 207:
                bad_rule = 207;
                break;
 
            //
            // Rule 208:  DefaultValue ::= default ElementValue
            //
            case 208:
                bad_rule = 208;
                break;
 
            //
            // Rule 209:  Annotations ::= Annotation
            //
            case 209:
                bad_rule = 209;
                break;
 
            //
            // Rule 210:  Annotations ::= Annotations Annotation
            //
            case 210:
                bad_rule = 210;
                break;
 
            //
            // Rule 211:  Annotation ::= NormalAnnotation
            //
            case 211:
                bad_rule = 211;
                break;
 
            //
            // Rule 212:  Annotation ::= MarkerAnnotation
            //
            case 212:
                bad_rule = 212;
                break;
 
            //
            // Rule 213:  Annotation ::= SingleElementAnnotation
            //
            case 213:
                bad_rule = 213;
                break;
 
            //
            // Rule 214:  NormalAnnotation ::= AT TypeName LPAREN ElementValuePairsopt RPAREN
            //
            case 214:
                bad_rule = 214;
                break;
 
            //
            // Rule 215:  ElementValuePairs ::= ElementValuePair
            //
            case 215:
                bad_rule = 215;
                break;
 
            //
            // Rule 216:  ElementValuePairs ::= ElementValuePairs COMMA ElementValuePair
            //
            case 216:
                bad_rule = 216;
                break;
 
            //
            // Rule 217:  ElementValuePair ::= SimpleName EQUAL ElementValue
            //
            case 217:
                bad_rule = 217;
                break;
 
            //
            // Rule 218:  SimpleName ::= identifier
            //
            case 218: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 219:  ElementValue ::= ConditionalExpression
            //
            case 219:
                bad_rule = 219;
                break;
 
            //
            // Rule 220:  ElementValue ::= Annotation
            //
            case 220:
                bad_rule = 220;
                break;
 
            //
            // Rule 221:  ElementValue ::= ElementValueArrayInitializer
            //
            case 221:
                bad_rule = 221;
                break;
 
            //
            // Rule 222:  ElementValueArrayInitializer ::= LBRACE ElementValuesopt ,opt RBRACE
            //
            case 222:
                bad_rule = 222;
                break;
 
            //
            // Rule 223:  ElementValues ::= ElementValue
            //
            case 223:
                bad_rule = 223;
                break;
 
            //
            // Rule 224:  ElementValues ::= ElementValues COMMA ElementValue
            //
            case 224:
                bad_rule = 224;
                break;
 
            //
            // Rule 225:  MarkerAnnotation ::= AT TypeName
            //
            case 225:
                bad_rule = 225;
                break;
 
            //
            // Rule 226:  SingleElementAnnotation ::= AT TypeName LPAREN ElementValue RPAREN
            //
            case 226:
                bad_rule = 226;
                break;
 
            //
            // Rule 227:  ArrayInitializer ::= LBRACE VariableInitializersopt ,opt RBRACE
            //
            case 227: {
                List a = (List) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.ArrayInit(pos()));
                else btParser.setSym1(nf.ArrayInit(pos(), a));
                break;
            }
     
            //
            // Rule 228:  VariableInitializers ::= VariableInitializer
            //
            case 228: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 229:  VariableInitializers ::= VariableInitializers COMMA VariableInitializer
            //
            case 229: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 230:  Block ::= LBRACE BlockStatementsopt RBRACE
            //
            case 230: {
                List l = (List) btParser.getSym(2);
                btParser.setSym1(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 231:  BlockStatements ::= BlockStatement
            //
            case 231: {
                List l = new TypedList(new LinkedList(), Stmt.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 232:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 232: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 233:  BlockStatement ::= LocalVariableDeclarationStatement
            //
            case 233:
                break;
 
            //
            // Rule 234:  BlockStatement ::= ClassDeclaration
            //
            case 234: {
                ClassDecl a = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 235:  BlockStatement ::= Statement
            //
            case 235: {
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 236:  LocalVariableDeclarationStatement ::= LocalVariableDeclaration SEMICOLON
            //
            case 236:
                break;
 
            //
            // Rule 237:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 237: {
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
            // Rule 238:  Statement ::= StatementWithoutTrailingSubstatement
            //
            case 238:
                break;
 
            //
            // Rule 239:  Statement ::= LabeledStatement
            //
            case 239:
                break;
 
            //
            // Rule 240:  Statement ::= IfThenStatement
            //
            case 240:
                break;
 
            //
            // Rule 241:  Statement ::= IfThenElseStatement
            //
            case 241:
                break;
 
            //
            // Rule 242:  Statement ::= WhileStatement
            //
            case 242:
                break;
 
            //
            // Rule 243:  Statement ::= ForStatement
            //
            case 243:
                break;
 
            //
            // Rule 244:  StatementWithoutTrailingSubstatement ::= Block
            //
            case 244:
                break;
 
            //
            // Rule 245:  StatementWithoutTrailingSubstatement ::= EmptyStatement
            //
            case 245:
                break;
 
            //
            // Rule 246:  StatementWithoutTrailingSubstatement ::= ExpressionStatement
            //
            case 246:
                break;
 
            //
            // Rule 247:  StatementWithoutTrailingSubstatement ::= AssertStatement
            //
            case 247:
                break;
 
            //
            // Rule 248:  StatementWithoutTrailingSubstatement ::= SwitchStatement
            //
            case 248:
                break;
 
            //
            // Rule 249:  StatementWithoutTrailingSubstatement ::= DoStatement
            //
            case 249:
                break;
 
            //
            // Rule 250:  StatementWithoutTrailingSubstatement ::= BreakStatement
            //
            case 250:
                break;
 
            //
            // Rule 251:  StatementWithoutTrailingSubstatement ::= ContinueStatement
            //
            case 251:
                break;
 
            //
            // Rule 252:  StatementWithoutTrailingSubstatement ::= ReturnStatement
            //
            case 252:
                break;
 
            //
            // Rule 253:  StatementWithoutTrailingSubstatement ::= SynchronizedStatement
            //
            case 253:
                break;
 
            //
            // Rule 254:  StatementWithoutTrailingSubstatement ::= ThrowStatement
            //
            case 254:
                break;
 
            //
            // Rule 255:  StatementWithoutTrailingSubstatement ::= TryStatement
            //
            case 255:
                break;
 
            //
            // Rule 256:  StatementNoShortIf ::= StatementWithoutTrailingSubstatement
            //
            case 256:
                break;
 
            //
            // Rule 257:  StatementNoShortIf ::= LabeledStatementNoShortIf
            //
            case 257:
                break;
 
            //
            // Rule 258:  StatementNoShortIf ::= IfThenElseStatementNoShortIf
            //
            case 258:
                break;
 
            //
            // Rule 259:  StatementNoShortIf ::= WhileStatementNoShortIf
            //
            case 259:
                break;
 
            //
            // Rule 260:  StatementNoShortIf ::= ForStatementNoShortIf
            //
            case 260:
                break;
 
            //
            // Rule 261:  IfThenStatement ::= if LPAREN Expression RPAREN Statement
            //
            case 261: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.If(pos(), a, b));
                break;
            }
     
            //
            // Rule 262:  IfThenElseStatement ::= if LPAREN Expression RPAREN StatementNoShortIf else Statement
            //
            case 262: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 263:  IfThenElseStatementNoShortIf ::= if LPAREN Expression RPAREN StatementNoShortIf else StatementNoShortIf
            //
            case 263: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 264:  EmptyStatement ::= SEMICOLON
            //
            case 264: {
                btParser.setSym1(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 265:  LabeledStatement ::= identifier COLON Statement
            //
            case 265: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 266:  LabeledStatementNoShortIf ::= identifier COLON StatementNoShortIf
            //
            case 266: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 267:  ExpressionStatement ::= StatementExpression SEMICOLON
            //
            case 267: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Eval(pos(), a));
                break;
            }
     
            //
            // Rule 268:  StatementExpression ::= Assignment
            //
            case 268:
                break;
 
            //
            // Rule 269:  StatementExpression ::= PreIncrementExpression
            //
            case 269:
                break;
 
            //
            // Rule 270:  StatementExpression ::= PreDecrementExpression
            //
            case 270:
                break;
 
            //
            // Rule 271:  StatementExpression ::= PostIncrementExpression
            //
            case 271:
                break;
 
            //
            // Rule 272:  StatementExpression ::= PostDecrementExpression
            //
            case 272:
                break;
 
            //
            // Rule 273:  StatementExpression ::= MethodInvocation
            //
            case 273:
                break;
 
            //
            // Rule 274:  StatementExpression ::= ClassInstanceCreationExpression
            //
            case 274:
                break;
 
            //
            // Rule 275:  AssertStatement ::= assert Expression SEMICOLON
            //
            case 275: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Assert(pos(), a));
                break;
            }
     
            //
            // Rule 276:  AssertStatement ::= assert Expression COLON Expression SEMICOLON
            //
            case 276: {
                Expr a = (Expr) btParser.getSym(2),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Assert(pos(), a, b));
                break;
            }
     
            //
            // Rule 277:  SwitchStatement ::= switch LPAREN Expression RPAREN SwitchBlock
            //
            case 277: {
                Expr a = (Expr) btParser.getSym(3);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.Switch(pos(), a, b));
                break;
            }
     
            //
            // Rule 278:  SwitchBlock ::= LBRACE SwitchBlockStatementGroupsopt SwitchLabelsopt RBRACE
            //
            case 278: {
                List l = (List) btParser.getSym(2),
                     l2 = (List) btParser.getSym(3);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 279:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroup
            //
            case 279:
                break;
 
            //
            // Rule 280:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 280: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 281:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 281: {
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);

                List l1 = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l1);
                l.add(nf.SwitchBlock(pos(), l2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 282:  SwitchLabels ::= SwitchLabel
            //
            case 282: {
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 283:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 283: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 284:  SwitchLabel ::= case ConstantExpression COLON
            //
            case 284: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Case(pos(), a));
                break;
            }
     
            //
            // Rule 285:  SwitchLabel ::= case EnumConstant COLON
            //
            case 285:
                bad_rule = 285;
                break;
 
            //
            // Rule 286:  SwitchLabel ::= default COLON
            //
            case 286: {
                btParser.setSym1(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 287:  EnumConstant ::= identifier
            //
            case 287:
                bad_rule = 287;
                break;
 
            //
            // Rule 288:  WhileStatement ::= while LPAREN Expression RPAREN Statement
            //
            case 288: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 289:  WhileStatementNoShortIf ::= while LPAREN Expression RPAREN StatementNoShortIf
            //
            case 289: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 290:  DoStatement ::= do Statement while LPAREN Expression RPAREN SEMICOLON
            //
            case 290: {
                Stmt a = (Stmt) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Do(pos(), a, b));
                break;
            }
     
            //
            // Rule 291:  ForStatement ::= BasicForStatement
            //
            case 291:
                break;
 
            //
            // Rule 292:  ForStatement ::= EnhancedForStatement
            //
            case 292:
                break;
 
            //
            // Rule 293:  BasicForStatement ::= for LPAREN ForInitopt SEMICOLON Expressionopt SEMICOLON ForUpdateopt RPAREN Statement
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
            // Rule 294:  ForStatementNoShortIf ::= for LPAREN ForInitopt SEMICOLON Expressionopt SEMICOLON ForUpdateopt RPAREN StatementNoShortIf
            //
            case 294: {
                List a = (List) btParser.getSym(3);
                Expr b = (Expr) btParser.getSym(5);
                List c = (List) btParser.getSym(7);
                Stmt d = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), a, b, c, d));
                break;
            }
     
            //
            // Rule 295:  ForInit ::= StatementExpressionList
            //
            case 295:
                break;
 
            //
            // Rule 296:  ForInit ::= LocalVariableDeclaration
            //
            case 296: {
                List l = new TypedList(new LinkedList(), ForInit.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 297:  ForUpdate ::= StatementExpressionList
            //
            case 297:
                break;
 
            //
            // Rule 298:  StatementExpressionList ::= StatementExpression
            //
            case 298: {
                List l = new TypedList(new LinkedList(), Eval.class, false);
                Expr a = (Expr) btParser.getSym(1);
                l.add(nf.Eval(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 299:  StatementExpressionList ::= StatementExpressionList COMMA StatementExpression
            //
            case 299: {
                List l = (List) btParser.getSym(1);
                Expr a = (Expr) btParser.getSym(3);
                l.add(nf.Eval(pos(), a));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 300:  BreakStatement ::= break identifieropt SEMICOLON
            //
            case 300: {
                Name a = (Name) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.Break(pos()));
                else btParser.setSym1(nf.Break(pos(), a.toString()));
                break;
            }
     
            //
            // Rule 301:  ContinueStatement ::= continue identifieropt SEMICOLON
            //
            case 301: {
                Name a = (Name) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.Continue(pos()));
                else btParser.setSym1(nf.Continue(pos(), a.toString()));
                break;
            }
     
            //
            // Rule 302:  ReturnStatement ::= return Expressionopt SEMICOLON
            //
            case 302: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Return(pos(), a));
                break;
            }
     
            //
            // Rule 303:  ThrowStatement ::= throw Expression SEMICOLON
            //
            case 303: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Throw(pos(), a));
                break;
            }
     
            //
            // Rule 304:  SynchronizedStatement ::= synchronized LPAREN Expression RPAREN Block
            //
            case 304: {
                Expr a = (Expr) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Synchronized(pos(), a, b));
                break;
            }
     
            //
            // Rule 305:  TryStatement ::= try Block Catches
            //
            case 305: {
                Block a = (Block) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Try(pos(), a, b));
                break;
            }
     
            //
            // Rule 306:  TryStatement ::= try Block Catchesopt Finally
            //
            case 306: {
                Block a = (Block) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                Block c = (Block) btParser.getSym(4);
                btParser.setSym1(nf.Try(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 307:  Catches ::= CatchClause
            //
            case 307: {
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 308:  Catches ::= Catches CatchClause
            //
            case 308: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 309:  CatchClause ::= catch LPAREN FormalParameter RPAREN Block
            //
            case 309: {
                Formal a = (Formal) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Catch(pos(), a, b));
                break;
            }
     
            //
            // Rule 310:  Finally ::= finally Block
            //
            case 310: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 311:  Primary ::= PrimaryNoNewArray
            //
            case 311:
                break;
 
            //
            // Rule 312:  Primary ::= ArrayCreationExpression
            //
            case 312:
                break;
 
            //
            // Rule 313:  PrimaryNoNewArray ::= Literal
            //
            case 313:
                break;
 
            //
            // Rule 314:  PrimaryNoNewArray ::= Type DOT class
            //
            case 314: {
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
            // Rule 315:  PrimaryNoNewArray ::= void DOT class
            //
            case 315: {
                btParser.setSym1(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(btParser.getToken(1)), ts.Void())));
                break;
            }
     
            //
            // Rule 316:  PrimaryNoNewArray ::= this
            //
            case 316: {
                btParser.setSym1(nf.This(pos()));
                break;
            }
     
            //
            // Rule 317:  PrimaryNoNewArray ::= ClassName DOT this
            //
            case 317: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(nf.This(pos(), a.toType()));
                break;
            }
     
            //
            // Rule 318:  PrimaryNoNewArray ::= LPAREN Expression RPAREN
            //
            case 318: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 319:  PrimaryNoNewArray ::= ClassInstanceCreationExpression
            //
            case 319:
                break;
 
            //
            // Rule 320:  PrimaryNoNewArray ::= FieldAccess
            //
            case 320:
                break;
 
            //
            // Rule 321:  PrimaryNoNewArray ::= MethodInvocation
            //
            case 321:
                break;
 
            //
            // Rule 322:  PrimaryNoNewArray ::= ArrayAccess
            //
            case 322:
                break;
 
            //
            // Rule 323:  Literal ::= IntegerLiteral
            //
            case 323: {
                // TODO: remove any prefix (such as 0x)
                polyglot.lex.IntegerLiteral a = int_lit(btParser.getToken(1), 10);
                btParser.setSym1(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 324:  Literal ::= LongLiteral
            //
            case 324: {
                // TODO: remove any suffix (such as L) or prefix (such as 0x)
                polyglot.lex.LongLiteral a = long_lit(btParser.getToken(1), 10);
                btParser.setSym1(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 325:  Literal ::= FloatingPointLiteral
            //
            case 325: {
                // TODO: remove any suffix (such as F)
                polyglot.lex.FloatLiteral a = float_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 326:  Literal ::= DoubleLiteral
            //
            case 326: {
                // TODO: remove any suffix (such as D)
                polyglot.lex.DoubleLiteral a = double_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 327:  Literal ::= BooleanLiteral
            //
            case 327: {
                polyglot.lex.BooleanLiteral a = boolean_lit(btParser.getToken(1));
                btParser.setSym1(nf.BooleanLit(pos(), a.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 328:  Literal ::= CharacterLiteral
            //
            case 328: {
                polyglot.lex.CharacterLiteral a = char_lit(btParser.getToken(1));
                btParser.setSym1(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 329:  Literal ::= StringLiteral
            //
            case 329: {
                polyglot.lex.StringLiteral a = string_lit(btParser.getToken(1));
                btParser.setSym1(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 330:  Literal ::= null
            //
            case 330: {
                btParser.setSym1(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 331:  BooleanLiteral ::= true
            //
            case 331:
                break;
 
            //
            // Rule 332:  BooleanLiteral ::= false
            //
            case 332:
                break;
 
            //
            // Rule 333:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 333: {
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
            // Rule 334:  ClassInstanceCreationExpression ::= Primary DOT new identifier LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 334: {
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
            // Rule 335:  ClassInstanceCreationExpression ::= AmbiguousName DOT new identifier LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 335: {
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
            // Rule 336:  ArgumentList ::= Expression
            //
            case 336: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 337:  ArgumentList ::= ArgumentList COMMA Expression
            //
            case 337: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 338:  DimExprs ::= DimExpr
            //
            case 338: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 339:  DimExprs ::= DimExprs DimExpr
            //
            case 339: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 340:  DimExpr ::= LBRACKET Expression RBRACKET
            //
            case 340: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(a.position(pos()));
                break;
            }
     
            //
            // Rule 341:  Dims ::= LBRACKET RBRACKET
            //
            case 341: {
                btParser.setSym1(new Integer(1));
                break;
            }
     
            //
            // Rule 342:  Dims ::= Dims LBRACKET RBRACKET
            //
            case 342: {
                Integer a = (Integer) btParser.getSym(1);
                btParser.setSym1(new Integer(a.intValue() + 1));
                break;
            }
     
            //
            // Rule 343:  FieldAccess ::= Primary DOT identifier
            //
            case 343: {
                Expr a = (Expr) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(), a, b.getIdentifier()));
                break;
            }
     
            //
            // Rule 344:  FieldAccess ::= super DOT identifier
            //
            case 344: {
                polyglot.lex.Identifier a = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken())), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 345:  FieldAccess ::= ClassName DOT super DOT identifier
            //
            case 345: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier()));
                break;
            }
     
            //
            // Rule 346:  MethodInvocation ::= MethodName LPAREN ArgumentListopt RPAREN
            //
            case 346: {
                Name a = (Name) btParser.getSym(1);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Call(pos(), a.prefix == null ? null : a.prefix.toReceiver(), a.name, b));
                break;
            }
     
            //
            // Rule 347:  MethodInvocation ::= Primary DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 347: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), a, b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 348:  MethodInvocation ::= super DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 348: {
//vj                    assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken())), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 349:  MethodInvocation ::= ClassName DOT super DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 349: {
                Name a = (Name) btParser.getSym(1);
//vj                    assert(btParser.getSym(5) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(5));
                List c = (List) btParser.getSym(7);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 350:  PostfixExpression ::= Primary
            //
            case 350:
                break;
 
            //
            // Rule 351:  PostfixExpression ::= ExpressionName
            //
            case 351: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 352:  PostfixExpression ::= PostIncrementExpression
            //
            case 352:
                break;
 
            //
            // Rule 353:  PostfixExpression ::= PostDecrementExpression
            //
            case 353:
                break;
 
            //
            // Rule 354:  PostIncrementExpression ::= PostfixExpression PLUS_PLUS
            //
            case 354: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 355:  PostDecrementExpression ::= PostfixExpression MINUS_MINUS
            //
            case 355: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 356:  UnaryExpression ::= PreIncrementExpression
            //
            case 356:
                break;
 
            //
            // Rule 357:  UnaryExpression ::= PreDecrementExpression
            //
            case 357:
                break;
 
            //
            // Rule 358:  UnaryExpression ::= PLUS UnaryExpression
            //
            case 358: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.POS, a));
                break;
            }
     
            //
            // Rule 359:  UnaryExpression ::= MINUS UnaryExpression
            //
            case 359: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NEG, a));
                break;
            }
     
            //
            // Rule 361:  PreIncrementExpression ::= PLUS_PLUS UnaryExpression
            //
            case 361: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_INC, a));
                break;
            }
     
            //
            // Rule 362:  PreDecrementExpression ::= MINUS_MINUS UnaryExpression
            //
            case 362: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_DEC, a));
                break;
            }
     
            //
            // Rule 363:  UnaryExpressionNotPlusMinus ::= PostfixExpression
            //
            case 363:
                break;
 
            //
            // Rule 364:  UnaryExpressionNotPlusMinus ::= TWIDDLE UnaryExpression
            //
            case 364: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.BIT_NOT, a));
                break;
            }
     
            //
            // Rule 365:  UnaryExpressionNotPlusMinus ::= NOT UnaryExpression
            //
            case 365: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NOT, a));
                break;
            }
     
            //
            // Rule 367:  MultiplicativeExpression ::= UnaryExpression
            //
            case 367:
                break;
 
            //
            // Rule 368:  MultiplicativeExpression ::= MultiplicativeExpression MULTIPLY UnaryExpression
            //
            case 368: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MUL, b));
                break;
            }
     
            //
            // Rule 369:  MultiplicativeExpression ::= MultiplicativeExpression DIVIDE UnaryExpression
            //
            case 369: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.DIV, b));
                break;
            }
     
            //
            // Rule 370:  MultiplicativeExpression ::= MultiplicativeExpression REMAINDER UnaryExpression
            //
            case 370: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MOD, b));
                break;
            }
     
            //
            // Rule 371:  AdditiveExpression ::= MultiplicativeExpression
            //
            case 371:
                break;
 
            //
            // Rule 372:  AdditiveExpression ::= AdditiveExpression PLUS MultiplicativeExpression
            //
            case 372: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.ADD, b));
                break;
            }
     
            //
            // Rule 373:  AdditiveExpression ::= AdditiveExpression MINUS MultiplicativeExpression
            //
            case 373: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SUB, b));
                break;
            }
     
            //
            // Rule 374:  ShiftExpression ::= AdditiveExpression
            //
            case 374:
                break;
 
            //
            // Rule 375:  ShiftExpression ::= ShiftExpression LEFT_SHIFT AdditiveExpression
            //
            case 375: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHL, b));
                break;
            }
     
            //
            // Rule 376:  ShiftExpression ::= ShiftExpression GREATER GREATER AdditiveExpression
            //
            case 376: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHR, b));
                break;
            }
     
            //
            // Rule 377:  ShiftExpression ::= ShiftExpression GREATER GREATER GREATER AdditiveExpression
            //
            case 377: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Binary(pos(), a, Binary.USHR, b));
                break;
            }
     
            //
            // Rule 378:  RelationalExpression ::= ShiftExpression
            //
            case 378:
                break;
 
            //
            // Rule 379:  RelationalExpression ::= RelationalExpression LESS ShiftExpression
            //
            case 379: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LT, b));
                break;
            }
     
            //
            // Rule 380:  RelationalExpression ::= RelationalExpression GREATER ShiftExpression
            //
            case 380: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GT, b));
                break;
            }
     
            //
            // Rule 381:  RelationalExpression ::= RelationalExpression LESS_EQUAL ShiftExpression
            //
            case 381: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LE, b));
                break;
            }
     
            //
            // Rule 382:  RelationalExpression ::= RelationalExpression GREATER EQUAL ShiftExpression
            //
            case 382: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GE, b));
                break;
            }
     
            //
            // Rule 383:  EqualityExpression ::= RelationalExpression
            //
            case 383:
                break;
 
            //
            // Rule 384:  EqualityExpression ::= EqualityExpression EQUAL_EQUAL RelationalExpression
            //
            case 384: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.EQ, b));
                break;
            }
     
            //
            // Rule 385:  EqualityExpression ::= EqualityExpression NOT_EQUAL RelationalExpression
            //
            case 385: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.NE, b));
                break;
            }
     
            //
            // Rule 386:  AndExpression ::= EqualityExpression
            //
            case 386:
                break;
 
            //
            // Rule 387:  AndExpression ::= AndExpression AND EqualityExpression
            //
            case 387: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_AND, b));
                break;
            }
     
            //
            // Rule 388:  ExclusiveOrExpression ::= AndExpression
            //
            case 388:
                break;
 
            //
            // Rule 389:  ExclusiveOrExpression ::= ExclusiveOrExpression XOR AndExpression
            //
            case 389: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_XOR, b));
                break;
            }
     
            //
            // Rule 390:  InclusiveOrExpression ::= ExclusiveOrExpression
            //
            case 390:
                break;
 
            //
            // Rule 391:  InclusiveOrExpression ::= InclusiveOrExpression OR ExclusiveOrExpression
            //
            case 391: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_OR, b));
                break;
            }
     
            //
            // Rule 392:  ConditionalAndExpression ::= InclusiveOrExpression
            //
            case 392:
                break;
 
            //
            // Rule 393:  ConditionalAndExpression ::= ConditionalAndExpression AND_AND InclusiveOrExpression
            //
            case 393: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_AND, b));
                break;
            }
     
            //
            // Rule 394:  ConditionalOrExpression ::= ConditionalAndExpression
            //
            case 394:
                break;
 
            //
            // Rule 395:  ConditionalOrExpression ::= ConditionalOrExpression OR_OR ConditionalAndExpression
            //
            case 395: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_OR, b));
                break;
            }
     
            //
            // Rule 396:  ConditionalExpression ::= ConditionalOrExpression
            //
            case 396:
                break;
 
            //
            // Rule 397:  ConditionalExpression ::= ConditionalOrExpression QUESTION Expression COLON ConditionalExpression
            //
            case 397: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3),
                     c = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Conditional(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 398:  AssignmentExpression ::= ConditionalExpression
            //
            case 398:
                break;
 
            //
            // Rule 399:  AssignmentExpression ::= Assignment
            //
            case 399:
                break;
 
            //
            // Rule 400:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 400: {
                Expr a = (Expr) btParser.getSym(1);
                Assign.Operator b = (Assign.Operator) btParser.getSym(2);
                Expr c = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Assign(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 401:  LeftHandSide ::= ExpressionName
            //
            case 401: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 402:  LeftHandSide ::= FieldAccess
            //
            case 402:
                break;
 
            //
            // Rule 403:  LeftHandSide ::= ArrayAccess
            //
            case 403:
                break;
 
            //
            // Rule 404:  AssignmentOperator ::= EQUAL
            //
            case 404: {
                btParser.setSym1(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 405:  AssignmentOperator ::= MULTIPLY_EQUAL
            //
            case 405: {
                btParser.setSym1(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 406:  AssignmentOperator ::= DIVIDE_EQUAL
            //
            case 406: {
                btParser.setSym1(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 407:  AssignmentOperator ::= REMAINDER_EQUAL
            //
            case 407: {
                btParser.setSym1(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 408:  AssignmentOperator ::= PLUS_EQUAL
            //
            case 408: {
                btParser.setSym1(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 409:  AssignmentOperator ::= MINUS_EQUAL
            //
            case 409: {
                btParser.setSym1(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 410:  AssignmentOperator ::= LEFT_SHIFT_EQUAL
            //
            case 410: {
                btParser.setSym1(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 411:  AssignmentOperator ::= GREATER GREATER EQUAL
            //
            case 411: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 412:  AssignmentOperator ::= GREATER GREATER GREATER EQUAL
            //
            case 412: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 413:  AssignmentOperator ::= AND_EQUAL
            //
            case 413: {
                btParser.setSym1(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 414:  AssignmentOperator ::= XOR_EQUAL
            //
            case 414: {
                btParser.setSym1(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 415:  AssignmentOperator ::= OR_EQUAL
            //
            case 415: {
                btParser.setSym1(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 416:  Expression ::= AssignmentExpression
            //
            case 416:
                break;
 
            //
            // Rule 417:  ConstantExpression ::= Expression
            //
            case 417:
                break;
 
            //
            // Rule 418:  Dimsopt ::=
            //
            case 418: {
                btParser.setSym1(new Integer(0));
                break;
            }
     
            //
            // Rule 419:  Dimsopt ::= Dims
            //
            case 419:
                break;
 
            //
            // Rule 420:  Catchesopt ::=
            //
            case 420: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 421:  Catchesopt ::= Catches
            //
            case 421:
                break;
 
            //
            // Rule 422:  identifieropt ::=
            //
            case 422:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 423:  identifieropt ::= identifier
            //
            case 423: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 424:  ForUpdateopt ::=
            //
            case 424: {
                btParser.setSym1(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 425:  ForUpdateopt ::= ForUpdate
            //
            case 425:
                break;
 
            //
            // Rule 426:  Expressionopt ::=
            //
            case 426:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 427:  Expressionopt ::= Expression
            //
            case 427:
                break;
 
            //
            // Rule 428:  ForInitopt ::=
            //
            case 428: {
                btParser.setSym1(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 429:  ForInitopt ::= ForInit
            //
            case 429:
                break;
 
            //
            // Rule 430:  SwitchLabelsopt ::=
            //
            case 430: {
                btParser.setSym1(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 431:  SwitchLabelsopt ::= SwitchLabels
            //
            case 431:
                break;
 
            //
            // Rule 432:  SwitchBlockStatementGroupsopt ::=
            //
            case 432: {
                btParser.setSym1(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 433:  SwitchBlockStatementGroupsopt ::= SwitchBlockStatementGroups
            //
            case 433:
                break;
 
            //
            // Rule 434:  VariableModifiersopt ::=
            //
            case 434: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 435:  VariableModifiersopt ::= VariableModifiers
            //
            case 435:
                break;
 
            //
            // Rule 436:  VariableInitializersopt ::=
            //
            case 436:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 437:  VariableInitializersopt ::= VariableInitializers
            //
            case 437:
                break;
 
            //
            // Rule 438:  ElementValuesopt ::=
            //
            case 438:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 439:  ElementValuesopt ::= ElementValues
            //
            case 439:
                bad_rule = 439;
                break;
 
            //
            // Rule 440:  ElementValuePairsopt ::=
            //
            case 440:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 441:  ElementValuePairsopt ::= ElementValuePairs
            //
            case 441:
                bad_rule = 441;
                break;
 
            //
            // Rule 442:  DefaultValueopt ::=
            //
            case 442:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 443:  DefaultValueopt ::= DefaultValue
            //
            case 443:
                break;
 
            //
            // Rule 444:  AnnotationTypeElementDeclarationsopt ::=
            //
            case 444:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 445:  AnnotationTypeElementDeclarationsopt ::= AnnotationTypeElementDeclarations
            //
            case 445:
                bad_rule = 445;
                break;
 
            //
            // Rule 446:  AbstractMethodModifiersopt ::=
            //
            case 446: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 447:  AbstractMethodModifiersopt ::= AbstractMethodModifiers
            //
            case 447:
                break;
 
            //
            // Rule 448:  ConstantModifiersopt ::=
            //
            case 448: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 449:  ConstantModifiersopt ::= ConstantModifiers
            //
            case 449:
                break;
 
            //
            // Rule 450:  InterfaceMemberDeclarationsopt ::=
            //
            case 450: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 451:  InterfaceMemberDeclarationsopt ::= InterfaceMemberDeclarations
            //
            case 451:
                break;
 
            //
            // Rule 452:  ExtendsInterfacesopt ::=
            //
            case 452: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 453:  ExtendsInterfacesopt ::= ExtendsInterfaces
            //
            case 453:
                break;
 
            //
            // Rule 454:  InterfaceModifiersopt ::=
            //
            case 454: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 455:  InterfaceModifiersopt ::= InterfaceModifiers
            //
            case 455:
                break;
 
            //
            // Rule 456:  ClassBodyopt ::=
            //
            case 456:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 457:  ClassBodyopt ::= ClassBody
            //
            case 457:
                break;
 
            //
            // Rule 458:  Argumentsopt ::=
            //
            case 458:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 459:  Argumentsopt ::= Arguments
            //
            case 459:
                bad_rule = 459;
                break;
 
            //
            // Rule 460:  EnumBodyDeclarationsopt ::=
            //
            case 460:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 461:  EnumBodyDeclarationsopt ::= EnumBodyDeclarations
            //
            case 461:
                bad_rule = 461;
                break;
 
            //
            // Rule 462:  ,opt ::=
            //
            case 462:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 463:  ,opt ::= COMMA
            //
            case 463:
                break;
 
            //
            // Rule 464:  EnumConstantsopt ::=
            //
            case 464:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 465:  EnumConstantsopt ::= EnumConstants
            //
            case 465:
                bad_rule = 465;
                break;
 
            //
            // Rule 466:  ArgumentListopt ::=
            //
            case 466: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 467:  ArgumentListopt ::= ArgumentList
            //
            case 467:
                break;
 
            //
            // Rule 468:  BlockStatementsopt ::=
            //
            case 468: {
                btParser.setSym1(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 469:  BlockStatementsopt ::= BlockStatements
            //
            case 469:
                break;
 
            //
            // Rule 470:  ExplicitConstructorInvocationopt ::=
            //
            case 470:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 471:  ExplicitConstructorInvocationopt ::= ExplicitConstructorInvocation
            //
            case 471:
                break;
 
            //
            // Rule 472:  ConstructorModifiersopt ::=
            //
            case 472: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 473:  ConstructorModifiersopt ::= ConstructorModifiers
            //
            case 473:
                break;
 
            //
            // Rule 474:  ...opt ::=
            //
            case 474:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 475:  ...opt ::= ELLIPSIS
            //
            case 475:
                break;
 
            //
            // Rule 476:  FormalParameterListopt ::=
            //
            case 476: {
                btParser.setSym1(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 477:  FormalParameterListopt ::= FormalParameterList
            //
            case 477:
                break;
 
            //
            // Rule 478:  Throwsopt ::=
            //
            case 478: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 479:  Throwsopt ::= Throws
            //
            case 479:
                break;
 
            //
            // Rule 480:  MethodModifiersopt ::=
            //
            case 480: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 481:  MethodModifiersopt ::= MethodModifiers
            //
            case 481:
                break;
 
            //
            // Rule 482:  FieldModifiersopt ::=
            //
            case 482: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 483:  FieldModifiersopt ::= FieldModifiers
            //
            case 483:
                break;
 
            //
            // Rule 484:  ClassBodyDeclarationsopt ::=
            //
            case 484: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 485:  ClassBodyDeclarationsopt ::= ClassBodyDeclarations
            //
            case 485:
                break;
 
            //
            // Rule 486:  Interfacesopt ::=
            //
            case 486: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 487:  Interfacesopt ::= Interfaces
            //
            case 487:
                break;
 
            //
            // Rule 488:  Superopt ::=
            //
            case 488:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 489:  Superopt ::= Super
            //
            case 489:
                break;
 
            //
            // Rule 490:  TypeParametersopt ::=
            //
            case 490:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 491:  TypeParametersopt ::= TypeParameters
            //
            case 491:
                break;
 
            //
            // Rule 492:  ClassModifiersopt ::=
            //
            case 492: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 493:  ClassModifiersopt ::= ClassModifiers
            //
            case 493:
                break;
 
            //
            // Rule 494:  Annotationsopt ::=
            //
            case 494:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 495:  Annotationsopt ::= Annotations
            //
            case 495:
                bad_rule = 495;
                break;
 
            //
            // Rule 496:  TypeDeclarationsopt ::=
            //
            case 496: {
                btParser.setSym1(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 497:  TypeDeclarationsopt ::= TypeDeclarations
            //
            case 497:
                break;
 
            //
            // Rule 498:  ImportDeclarationsopt ::=
            //
            case 498: {
                btParser.setSym1(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 499:  ImportDeclarationsopt ::= ImportDeclarations
            //
            case 499:
                break;
 
            //
            // Rule 500:  PackageDeclarationopt ::=
            //
            case 500:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 501:  PackageDeclarationopt ::= PackageDeclaration
            //
            case 501:
                break;
 
            //
            // Rule 502:  WildcardBoundsOpt ::=
            //
            case 502:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 503:  WildcardBoundsOpt ::= WildcardBounds
            //
            case 503:
                bad_rule = 503;
                break;
 
            //
            // Rule 504:  AdditionalBoundListopt ::=
            //
            case 504:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 505:  AdditionalBoundListopt ::= AdditionalBoundList
            //
            case 505:
                bad_rule = 505;
                break;
 
            //
            // Rule 506:  TypeBoundopt ::=
            //
            case 506:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 507:  TypeBoundopt ::= TypeBound
            //
            case 507:
                bad_rule = 507;
                break;
 
            //
            // Rule 508:  TypeArgumentsopt ::=
            //
            case 508:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 509:  TypeArgumentsopt ::= TypeArguments
            //
            case 509:
                bad_rule = 509;
                break;
 
            //
            // Rule 510:  Commentsopt ::=
            //
            case 510:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 511:  Commentsopt ::= Comments
            //
            case 511:
                break;
 
            //
            // Rule 512:  Type ::= DataType PlaceTypeSpecifieropt
            //
            case 512: {
                assert(btParser.getSym(2) == null);
                //btParser.setSym1();
                    break;
            }
         
            //
            // Rule 513:  Type ::= nullable LESS Type GREATER
            //
            case 513: {
                TypeNode a = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Nullable(pos(), a));
                          break;
            }
              
            //
            // Rule 514:  Type ::= future LESS Type GREATER
            //
            case 514: {
                TypeNode a = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Future(pos(), a));
                          break;
            }

              
            //
            // Rule 515:  Type ::= boxed LESS Type GREATER
            //
            case 515:
                bad_rule = 515;
                break; 
 
            //
            // Rule 516:  Type ::= fun LESS Type COMMA Type GREATER
            //
            case 516:
                bad_rule = 516;
                break; 
 
            //
            // Rule 517:  DataType ::= PrimitiveType
            //
            case 517:
                break; 
            
               
            //
            // Rule 518:  DataType ::= ClassOrInterfaceType
            //
            case 518:
                break; 
            
               
            //
            // Rule 519:  DataType ::= ArrayType
            //
            case 519:
                break; 
             
            //
            // Rule 520:  PlaceTypeSpecifier ::= AT PlaceType
            //
            case 520:
                bad_rule = 520;
                break; 
 
            //
            // Rule 521:  PlaceType ::= place
            //
            case 521:
                bad_rule = 521;
                break; 
 
            //
            // Rule 522:  PlaceType ::= activity
            //
            case 522:
                bad_rule = 522;
                break; 
 
            //
            // Rule 523:  PlaceType ::= method
            //
            case 523:
                bad_rule = 523;
                break; 
 
            //
            // Rule 524:  PlaceType ::= current
            //
            case 524:
                bad_rule = 524;
                break; 
 
            //
            // Rule 525:  PlaceType ::= PlaceExpression
            //
            case 525:
                bad_rule = 525;
                break; 
 
            //
            // Rule 526:  ClassOrInterfaceType ::= TypeName DepParametersopt
            //
            case 526: { 
            Name a = (Name) btParser.getSym(1);
            TypeNode t = a.toType();
            DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
            btParser.setSym1(nf.ParametricTypeNode(pos(), t, b));
                    break;
            }
        
            //
            // Rule 527:  DepParameters ::= LPAREN DepParameterExpr RPAREN
            //
            case 527:
                break; 
        
            //
            // Rule 528:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 528: {
             List a = (List) btParser.getSym(1);                           
             Expr b = (Expr) btParser.getSym(2);
             btParser.setSym1(nf.DepParameterExpr(pos(),a,b));
                    break;
            }
        
            //
            // Rule 529:  DepParameterExpr ::= WhereClause
            //
            case 529: {
             Expr b = (Expr) btParser.getSym(1);
             btParser.setSym1(nf.DepParameterExpr(pos(), null, b));
                    break;
            }
        
            //
            // Rule 530:  WhereClause ::= COLON Expression
            //
            case 530:
                break; 
 
            //
            // Rule 532:  X10ArrayType ::= Type LBRACKET DOT RBRACKET
            //
            case 532: {
                TypeNode a = (TypeNode) btParser.getSym(1);
                TypeNode t = nf.X10ArrayTypeNode(pos(), a, false, null);
                btParser.setSym1(t);
                break;
            }
     
            //
            // Rule 533:  X10ArrayType ::= Type reference LBRACKET DOT RBRACKET
            //
            case 533: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, false, null));
                    break;
            }
        
            //
            // Rule 534:  X10ArrayType ::= Type value LBRACKET DOT RBRACKET
            //
            case 534: {
             TypeNode a = (TypeNode) btParser.getSym(1);
                btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, true, null));
                    break;
            }
        
            //
            // Rule 535:  X10ArrayType ::= Type LBRACKET DepParameterExpr RBRACKET
            //
            case 535: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, false, b));
                    break;
            }
        
            //
            // Rule 536:  X10ArrayType ::= Type reference LBRACKET DepParameterExpr RBRACKET
            //
            case 536: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, false, b));
                    break;
            }
        
            //
            // Rule 537:  X10ArrayType ::= Type value LBRACKET DepParameterExpr RBRACKET
            //
            case 537: {
             TypeNode a = (TypeNode) btParser.getSym(1);
             DepParameterExpr b = (DepParameterExpr) btParser.getSym(2);
             btParser.setSym1(nf.X10ArrayTypeNode(pos(), a, true, b));
                    break;
            }
        
            //
            // Rule 538:  ObjectKind ::= value
            //
            case 538:
                bad_rule = 538;
                break; 
 
            //
            // Rule 539:  ObjectKind ::= reference
            //
            case 539:
                bad_rule = 539;
                break; 
 
            //
            // Rule 540:  MethodModifier ::= atomic
            //
            case 540: {
                btParser.setSym1(Flags.ATOMIC);
                break;
            }
     
            //
            // Rule 541:  MethodModifier ::= extern
            //
            case 541: {
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 542:  ClassDeclaration ::= ValueClassDeclaration
            //
            case 542:
                break; 
 
            //
            // Rule 543:  ValueClassDeclaration ::= ClassModifiersopt value identifier Superopt Interfacesopt ClassBody
            //
            case 543: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                TypeNode c = (TypeNode) btParser.getSym(4);
                List d = (List) btParser.getSym(5);
                ClassBody e = (ClassBody) btParser.getSym(6);
                btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
                break;
            }  
            //
            // Rule 544:  ValueClassDeclaration ::= ClassModifiersopt value class identifier Superopt Interfacesopt ClassBody
            //
            case 544: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(4));
                TypeNode c = (TypeNode) btParser.getSym(5);
                List d = (List) btParser.getSym(6);
                ClassBody e = (ClassBody) btParser.getSym(7);
                btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
                break;
            }   
            //
            // Rule 545:  ArrayCreationExpression ::= new ArrayBaseType LBRACKET RBRACKET ArrayInitializer
            //
            case 545: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                ArrayInit d = (ArrayInit) btParser.getSym(5);
                // btParser.setSym1(nf.ArrayConstructor(pos(), a, false, null, d));
                btParser.setSym1(nf.NewArray(pos(), a, 1, d));
                break;
            }
     
            //
            // Rule 546:  ArrayCreationExpression ::= new ArrayBaseType LBRACKET Expression RBRACKET
            //
            case 546: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Expr c = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, false, c, null));
                break;
            }
     
            //
            // Rule 547:  ArrayCreationExpression ::= new ArrayBaseType LBRACKET Expression RBRACKET Expression
            //
            case 547: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Expr c = (Expr) btParser.getSym(4);
                Expr d = (Expr) btParser.getSym(6);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, false, c, d));
                break;
            }
     
            //
            // Rule 548:  ArrayCreationExpression ::= new ArrayBaseType value LBRACKET Expression RBRACKET
            //
            case 548: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Expr c = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, true, c, null));
                break;
            }
     
            //
            // Rule 549:  ArrayCreationExpression ::= new ArrayBaseType value LBRACKET Expression RBRACKET Expression
            //
            case 549: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Expr c = (Expr) btParser.getSym(5);
                Expr d = (Expr) btParser.getSym(7);
                btParser.setSym1(nf.ArrayConstructor(pos(), a, true, c, d));
                break;
            }
                
            //
            // Rule 550:  ArrayBaseType ::= PrimitiveType
            //
            case 550:
                break;
          
            
            //
            // Rule 551:  ArrayBaseType ::= ClassOrInterfaceType
            //
            case 551:
                break;
          
            //
            // Rule 552:  ArrayAccess ::= ExpressionName LBRACKET ArgumentList RBRACKET
            //
            case 552: {
           Name e = (Name) btParser.getSym(1);
           List b = (List) btParser.getSym(3);
           if (b.size() == 1)
           btParser.setSym1(nf.X10ArrayAccess1(pos(), e.toExpr(), (Expr) b.get(0)));
           else btParser.setSym1(nf.X10ArrayAccess(pos(), e.toExpr(), b));
                     break;
            }
         
            //
            // Rule 553:  ArrayAccess ::= PrimaryNoNewArray LBRACKET ArgumentList RBRACKET
            //
            case 553: { 
           Expr a = (Expr) btParser.getSym(1);
           List b = (List) btParser.getSym(3);
           if (b.size() == 1)
           btParser.setSym1(nf.X10ArrayAccess1(pos(), a, (Expr) b.get(0)));
           else btParser.setSym1(nf.X10ArrayAccess(pos(), a, b));
                    break;
            }
        
            //
            // Rule 554:  Statement ::= NowStatement
            //
            case 554:
                break; 
 
            //
            // Rule 555:  Statement ::= ClockedStatement
            //
            case 555:
                break; 
 
            //
            // Rule 556:  Statement ::= AsyncStatement
            //
            case 556:
                break; 
 
            //
            // Rule 557:  Statement ::= AtomicStatement
            //
            case 557:
                break; 
 
            //
            // Rule 558:  Statement ::= WhenStatement
            //
            case 558:
                break; 
 
            //
            // Rule 559:  Statement ::= ForEachStatement
            //
            case 559:
                break; 
 
            //
            // Rule 560:  Statement ::= AtEachStatement
            //
            case 560:
                break; 
 
            //
            // Rule 561:  Statement ::= FinishStatement
            //
            case 561:
                break; 
 
            //
            // Rule 562:  StatementWithoutTrailingSubstatement ::= NextStatement
            //
            case 562:
                break; 
 
            //
            // Rule 563:  StatementWithoutTrailingSubstatement ::= AwaitStatement
            //
            case 563:
                break; 
 
            //
            // Rule 564:  StatementNoShortIf ::= NowStatementNoShortIf
            //
            case 564:
                break; 
 
            //
            // Rule 565:  StatementNoShortIf ::= ClockedStatementNoShortIf
            //
            case 565:
                break; 
 
            //
            // Rule 566:  StatementNoShortIf ::= AsyncStatementNoShortIf
            //
            case 566:
                break; 
 
            //
            // Rule 567:  StatementNoShortIf ::= AtomicStatementNoShortIf
            //
            case 567:
                break; 
 
            //
            // Rule 568:  StatementNoShortIf ::= WhenStatementNoShortIf
            //
            case 568:
                break; 
 
            //
            // Rule 569:  StatementNoShortIf ::= ForEachStatementNoShortIf
            //
            case 569:
                break; 
 
            //
            // Rule 570:  StatementNoShortIf ::= AtEachStatementNoShortIf
            //
            case 570:
                break; 
 
            //
            // Rule 571:  StatementNoShortIf ::= FinishStatementNoShortIf
            //
            case 571:
                break; 
 
            //
            // Rule 572:  NowStatement ::= now LPAREN Clock RPAREN Statement
            //
            case 572: {
                Name a = (Name) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Now(pos(), a.toExpr(), b));
                break;
            }
     
            //
            // Rule 573:  ClockedStatement ::= clocked LPAREN ClockList RPAREN Statement
            //
            case 573: {
                List a = (List) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), a, b));
                break;
            }
     
            //
            // Rule 574:  AsyncStatement ::= async PlaceExpressionSingleListopt Statement
            //
            case 574: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Async(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 575:  AsyncStatement ::= async LPAREN here RPAREN Statement
            //
            case 575: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Async(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 576:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Statement
            //
            case 576: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Atomic(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 577:  AtomicStatement ::= atomic LPAREN here RPAREN Statement
            //
            case 577: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Atomic(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 578:  WhenStatement ::= when LPAREN Expression RPAREN Statement
            //
            case 578: {
                Expr e = (Expr) btParser.getSym(3);
                Stmt s = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.When(pos(), e,s));
                break;
            }
     
            //
            // Rule 579:  WhenStatement ::= WhenStatement or LPAREN Expression RPAREN Statement
            //
            case 579: {
                When w = (When) btParser.getSym(1);
                Expr e = (Expr) btParser.getSym(4);
                Stmt s = (Stmt) btParser.getSym(6);
                w.add(new When_c.Branch_c(e,s));
                btParser.setSym1(w);
                break;
            }
     
            //
            // Rule 580:  ForEachStatement ::= foreach LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 580: { 
               Formal f = (Formal) btParser.getSym(3);
               Expr  e = (Expr) btParser.getSym(5);
               Stmt s =  (Stmt) btParser.getSym(7);
               X10Loop x = nf.ForEach(pos(), f, e, s);
               btParser.setSym1(x);
                 break;
            }
     
            //
            // Rule 581:  AtEachStatement ::= ateach LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 581: { 
               Formal f = (Formal) btParser.getSym(3);
               Expr  e = (Expr) btParser.getSym(5);
               Stmt s =  (Stmt) btParser.getSym(7);
               X10Loop x = nf.AtEach(pos(), f, e, s);
               btParser.setSym1(x);
                 break;
            }
     
            //
            // Rule 582:  EnhancedForStatement ::= for LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 582: {
               Formal f = (Formal) btParser.getSym(3);
               Expr  e = (Expr) btParser.getSym(5);
               Stmt s =  (Stmt) btParser.getSym(7);
               X10Loop x = nf.ForLoop(pos(), f, e, s);
               btParser.setSym1(x);
               break;
            }  
            //
            // Rule 583:  FinishStatement ::= finish Statement
            //
            case 583: {
                Stmt b = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Finish(pos(),  b));
                break;
            }
     
            //
            // Rule 584:  NowStatementNoShortIf ::= now LPAREN Clock RPAREN StatementNoShortIf
            //
            case 584: {
                Name a = (Name) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Now(pos(), a.toExpr(), b));
                break;
            }
     
            //
            // Rule 585:  ClockedStatementNoShortIf ::= clocked LPAREN ClockList RPAREN StatementNoShortIf
            //
            case 585: {
                List a = (List) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), a, b));
                break;
            }
     
            //
            // Rule 586:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt StatementNoShortIf
            //
            case 586: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Async(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 587:  AsyncStatementNoShortIf ::= async LPAREN here RPAREN StatementNoShortIf
            //
            case 587: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Async(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 588:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 588: {
                Expr e = (Expr) btParser.getSym(2);
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Atomic(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 589:  AtomicStatementNoShortIf ::= atomic LPAREN here RPAREN StatementNoShortIf
            //
            case 589: {
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.Atomic(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 590:  WhenStatementNoShortIf ::= when LPAREN Expression RPAREN StatementNoShortIf
            //
            case 590: {
                Expr e = (Expr) btParser.getSym(3);
                Stmt s = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.When(pos(), e,s));
                break;
            }
     
            //
            // Rule 591:  WhenStatementNoShortIf ::= WhenStatement or LPAREN Expression RPAREN StatementNoShortIf
            //
            case 591: {
                When w = (When) btParser.getSym(1);
                Expr e = (Expr) btParser.getSym(4);
                Stmt s = (Stmt) btParser.getSym(6);
                w.add(new When_c.Branch_c(e,s));
                btParser.setSym1(w);
                break;
            }
     
            //
            // Rule 592:  ForEachStatementNoShortIf ::= foreach LPAREN FormalParameter COLON Expression RPAREN StatementNoShortIf
            //
            case 592: { 
               Formal f = (Formal) btParser.getSym(3);
               Expr  e = (Expr) btParser.getSym(5);
               Stmt s =  (Stmt) btParser.getSym(7);
               X10Loop x = nf.ForEach(pos(), f, e, s);
               btParser.setSym1(x);
                 break;
            }
     
            //
            // Rule 593:  AtEachStatementNoShortIf ::= ateach LPAREN FormalParameter COLON Expression RPAREN StatementNoShortIf
            //
            case 593: { 
               Formal f = (Formal) btParser.getSym(3);
               Expr  e = (Expr) btParser.getSym(5);
               Stmt s =  (Stmt) btParser.getSym(7);
               X10Loop x = nf.AtEach(pos(), f, e, s);
               btParser.setSym1(x);
                 break;
            }
     
            //
            // Rule 594:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 594: {
                Stmt b = (Stmt) btParser.getSym(2);
                btParser.setSym1(nf.Finish(pos(),  b));
                break;
            }
     
            //
            // Rule 595:  PlaceExpressionSingleList ::= LPAREN PlaceExpression RPAREN
            //
            case 595: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 596:  PlaceExpression ::= here
            //
            case 596: {
                  btParser.setSym1(nf.Here(pos(btParser.getFirstToken())));
                break;
            }
     
            //
            // Rule 597:  PlaceExpression ::= this
            //
            case 597: {
                btParser.setSym1(nf.Field(pos(btParser.getFirstToken()), nf.This(pos(btParser.getFirstToken())), "place"));
                break;
            }
     
            //
            // Rule 598:  PlaceExpression ::= Expression
            //
            case 598:
                break;
     
            //
            // Rule 599:  PlaceExpression ::= ArrayAccess
            //
            case 599:
                bad_rule = 599;
                break; 
 
            //
            // Rule 600:  NextStatement ::= next SEMICOLON
            //
            case 600: {
                btParser.setSym1(nf.Next(pos()));
                break;
            }
     
            //
            // Rule 601:  AwaitStatement ::= await Expression SEMICOLON
            //
            case 601: { 
         Expr e = (Expr) btParser.getSym(2);
         btParser.setSym1(nf.Await(pos(), e));
                 break;
            }
     
            //
            // Rule 602:  ClockList ::= Clock
            //
            case 602: {
                Name c = (Name) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(c.toExpr());
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 603:  ClockList ::= ClockList COMMA Clock
            //
            case 603: {
                List l = (List) btParser.getSym(1);
                Name c = (Name) btParser.getSym(3);
                l.add(c.toExpr());
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 604:  Clock ::= identifier
            //
            case 604: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 605:  CastExpression ::= LPAREN Type RPAREN UnaryExpressionNotPlusMinus
            //
            case 605: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Cast(pos(), a, b));
                break;
            }
     
            //
            // Rule 606:  MethodInvocation ::= Primary ARROW identifier LPAREN ArgumentListopt RPAREN
            //
            case 606: { 
          Expr a = (Expr) btParser.getSym(1);
          polyglot.lex.Identifier b = id(btParser.getToken(3));
          List c = (List) btParser.getSym(5);
          btParser.setSym1(nf.RemoteCall(pos(), a, b.getIdentifier(), c));
                 break;
            } 
     
            //
            // Rule 607:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 607: {
                Expr a = (Expr) btParser.getSym(1);
                TypeNode b = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Instanceof(pos(), a, b));
                break;
            }
     
            //
            // Rule 608:  ExpressionName ::= here
            //
            case 608: {
          btParser.setSym1(new Name(nf, ts, pos(), "here"){
              public Expr toExpr() {
                 return nf.Here(pos);
              }
           });

                  break;
            }
       
            //
            // Rule 609:  Primary ::= FutureExpression
            //
            case 609:
                break; 
 
            //
            // Rule 610:  FutureExpression ::= future PlaceExpressionSingleListopt LBRACE Expression RBRACE
            //
            case 610: {
                Expr e1 = (Expr) btParser.getSym(2),
                     e2 = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Future(pos(), (e1 == null ? nf.Here(pos(btParser.getFirstToken())) : e1), e2));
                break;
            }
     
            //
            // Rule 611:  FutureExpression ::= future LPAREN here RPAREN LBRACE Expression RBRACE
            //
            case 611: {
                Expr e2 = (Expr) btParser.getSym(6);
                btParser.setSym1(nf.Future(pos(), nf.Here(pos(btParser.getFirstToken(3))), e2));
                break;
            }
     
            //
            // Rule 612:  FunExpression ::= fun Type LPAREN FormalParameterListopt RPAREN LBRACE Expression RBRACE
            //
            case 612:
                bad_rule = 612;
                break; 
 
            //
            // Rule 613:  MethodInvocation ::= MethodName LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 613:
                bad_rule = 613;
                break; 
 
            //
            // Rule 614:  MethodInvocation ::= Primary DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 614:
                bad_rule = 614;
                break; 
 
            //
            // Rule 615:  MethodInvocation ::= super DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 615:
                bad_rule = 615;
                break; 
 
            //
            // Rule 616:  MethodInvocation ::= ClassName DOT super DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 616:
                bad_rule = 616;
                break; 
 
            //
            // Rule 617:  MethodInvocation ::= TypeName DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 617:
                bad_rule = 617;
                break; 
 
            //
            // Rule 618:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 618:
                bad_rule = 618;
                break; 
 
            //
            // Rule 619:  ClassInstanceCreationExpression ::= Primary DOT new identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 619:
                bad_rule = 619;
                break; 
 
            //
            // Rule 620:  ClassInstanceCreationExpression ::= AmbiguousName DOT new identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 620:
                bad_rule = 620;
                break; 
 
            //
            // Rule 621:  PlaceTypeSpecifieropt ::=
            //
            case 621:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 622:  PlaceTypeSpecifieropt ::= PlaceTypeSpecifier
            //
            case 622:
                break; 
 
            //
            // Rule 623:  DepParametersopt ::=
            //
            case 623:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 624:  DepParametersopt ::= DepParameters
            //
            case 624:
                break; 
 
            //
            // Rule 625:  WhereClauseopt ::=
            //
            case 625:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 626:  WhereClauseopt ::= WhereClause
            //
            case 626:
                break; 
 
            //
            // Rule 627:  ObjectKindopt ::=
            //
            case 627:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 628:  ObjectKindopt ::= ObjectKind
            //
            case 628:
                break; 
 
            //
            // Rule 629:  ArrayInitializeropt ::=
            //
            case 629:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 630:  ArrayInitializeropt ::= ArrayInitializer
            //
            case 630:
                break; 
 
            //
            // Rule 631:  ConcreteDistributionopt ::=
            //
            case 631:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 632:  ConcreteDistributionopt ::= ConcreteDistribution
            //
            case 632:
                break; 
 
            //
            // Rule 633:  PlaceExpressionSingleListopt ::=
            //
            case 633:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 634:  PlaceExpressionSingleListopt ::= PlaceExpressionSingleList
            //
            case 634:
                break; 
 
            //
            // Rule 635:  ArgumentListopt ::=
            //
            case 635:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 636:  ArgumentListopt ::= ArgumentList
            //
            case 636:
                break; 
 
            //
            // Rule 637:  DepParametersopt ::=
            //
            case 637:
                btParser.setSym1(null);
                break; 
 
            //
            // Rule 638:  DepParametersopt ::= DepParameters
            //
            case 638:
                break; 
    
            default:
                break;
        }
        return;
    }
}

