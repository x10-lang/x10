
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
// Without support for clocks.

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
            System.exit(12);
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
            System.exit(1);
        }
        catch (BadParseSymFileException e)
        {
            System.out.println("****Error: Bad Parser Symbol File -- X10Parsersym.java");
            System.exit(1);
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
            if (actions_stopped && prsStream.getSize() > 2)
            {
                List b = new TypedList(new LinkedList(), Import.class, false),
                     c = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                sf = nf.SourceFile(pos(1, prsStream.getSize() - 2), null, b, c);
            }

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

    private boolean actions_stopped = false;

    public void ruleAction(int ruleNumber)
    {
        if (actions_stopped) return;

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
                System.err.println("Rule " + 23 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 24:  TypeBound ::= extends ClassOrInterfaceType AdditionalBoundListopt
            //
            case 24:
                System.err.println("Rule " + 24 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 25:  AdditionalBoundList ::= AdditionalBound
            //
            case 25:
                System.err.println("Rule " + 25 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 26:  AdditionalBoundList ::= AdditionalBoundList AdditionalBound
            //
            case 26:
                System.err.println("Rule " + 26 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 27:  AdditionalBound ::= AND InterfaceType
            //
            case 27:
                System.err.println("Rule " + 27 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 28:  TypeArguments ::= LESS ActualTypeArgumentList GREATER
            //
            case 28:
                System.err.println("Rule " + 28 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 29:  ActualTypeArgumentList ::= ActualTypeArgument
            //
            case 29:
                System.err.println("Rule " + 29 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 30:  ActualTypeArgumentList ::= ActualTypeArgumentList COMMA ActualTypeArgument
            //
            case 30:
                System.err.println("Rule " + 30 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 31:  Wildcard ::= QUESTION WildcardBoundsOpt
            //
            case 31:
                System.err.println("Rule " + 31 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 32:  WildcardBounds ::= extends ReferenceType
            //
            case 32:
                System.err.println("Rule " + 32 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 33:  WildcardBounds ::= super ReferenceType
            //
            case 33:
                System.err.println("Rule " + 33 + " not yet implemented");
                actions_stopped = true;
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
            // Rule 44:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 44: {
                PackageNode a = (PackageNode) btParser.getSym(1);
                List b = (List) btParser.getSym(2),
                     c = (List) btParser.getSym(3);
                btParser.setSym1(nf.SourceFile(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b, c));
                break;
            }
     
            //
            // Rule 45:  ImportDeclarations ::= ImportDeclaration
            //
            case 45: {
                List l = new TypedList(new LinkedList(), Import.class, false);
                Import a = (Import) btParser.getSym(1);
                l.add(a);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 46:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 46: {
                List l = (TypedList) btParser.getSym(1);
                TopLevelDecl b = (TopLevelDecl) btParser.getSym(2);
                if (b != null)
                    l.add(b);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 47:  TypeDeclarations ::= TypeDeclaration
            //
            case 47: {
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                TopLevelDecl a = (TopLevelDecl) btParser.getSym(1);
                if (a != null)
                    l.add(a);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 48:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 48: {
                List l = (TypedList) btParser.getSym(1);
                TopLevelDecl b = (TopLevelDecl) btParser.getSym(2);
                if (b != null)
                    l.add(b);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 49:  PackageDeclaration ::= package PackageName SEMICOLON
            //
            case 49: {
//vj                    assert(btParser.getSym(1) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(a.toPackage());
                break;
            }
     
            //
            // Rule 50:  ImportDeclaration ::= SingleTypeImportDeclaration
            //
            case 50:
                break;
 
            //
            // Rule 51:  ImportDeclaration ::= TypeImportOnDemandDeclaration
            //
            case 51:
                break;
 
            //
            // Rule 52:  ImportDeclaration ::= SingleStaticImportDeclaration
            //
            case 52:
                break;
 
            //
            // Rule 53:  ImportDeclaration ::= StaticImportOnDemandDeclaration
            //
            case 53:
                break;
 
            //
            // Rule 54:  SingleTypeImportDeclaration ::= import TypeName SEMICOLON
            //
            case 54: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.CLASS, a.toString()));
                break;
            }
     
            //
            // Rule 55:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName DOT MULTIPLY SEMICOLON
            //
            case 55: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.PACKAGE, a.toString()));
                break;
            }
     
            //
            // Rule 56:  SingleStaticImportDeclaration ::= import static TypeName DOT identifier SEMICOLON
            //
            case 56:
                System.err.println("Rule " + 56 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 57:  StaticImportOnDemandDeclaration ::= import static TypeName DOT MULTIPLY SEMICOLON
            //
            case 57:
                System.err.println("Rule " + 57 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 58:  TypeDeclaration ::= ClassDeclaration
            //
            case 58:
                break;
 
            //
            // Rule 59:  TypeDeclaration ::= InterfaceDeclaration
            //
            case 59:
                break;
 
            //
            // Rule 60:  TypeDeclaration ::= SEMICOLON
            //
            case 60: {
                btParser.setSym1(null);
                break;
            }
     
            //
            // Rule 61:  ClassDeclaration ::= NormalClassDeclaration
            //
            case 61:
                break;
 
            //
            // Rule 62:  NormalClassDeclaration ::= ClassModifiersopt class identifier Superopt Interfacesopt ClassBody
            //
            case 62: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
//vj                    assert(btParser.getSym(4) == null);
                TypeNode c = (TypeNode) btParser.getSym(4);
                List d = (List) btParser.getSym(5);
                ClassBody e = (ClassBody) btParser.getSym(6);
                if (a.isValue())
                     btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
                else btParser.setSym1(nf.ClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
                break;
            }
     
            //
            // Rule 63:  ClassModifiers ::= ClassModifier
            //
            case 63:
                break;
 
            //
            // Rule 64:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 64: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 65:  ClassModifier ::= public
            //
            case 65: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 66:  ClassModifier ::= protected
            //
            case 66: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 67:  ClassModifier ::= private
            //
            case 67: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 68:  ClassModifier ::= abstract
            //
            case 68: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 69:  ClassModifier ::= static
            //
            case 69: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 70:  ClassModifier ::= final
            //
            case 70: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 71:  ClassModifier ::= strictfp
            //
            case 71: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 72:  TypeParameters ::= LESS TypeParameterList GREATER
            //
            case 72:
                System.err.println("Rule " + 72 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 73:  TypeParameterList ::= TypeParameter
            //
            case 73:
                System.err.println("Rule " + 73 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 74:  TypeParameterList ::= TypeParameterList COMMA TypeParameter
            //
            case 74:
                System.err.println("Rule " + 74 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 75:  Super ::= extends ClassType
            //
            case 75: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 76:  Interfaces ::= implements InterfaceTypeList
            //
            case 76: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 77:  InterfaceTypeList ::= InterfaceType
            //
            case 77: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 78:  InterfaceTypeList ::= InterfaceTypeList COMMA InterfaceType
            //
            case 78: {
                List l = (TypedList) btParser.getSym(1);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 79:  ClassBody ::= LBRACE ClassBodyDeclarationsopt RBRACE
            //
            case 79: {
                btParser.setSym1(nf.ClassBody(pos(btParser.getFirstToken(), btParser.getLastToken()), (List) btParser.getSym(2)));
                break;
            }
     
            //
            // Rule 80:  ClassBodyDeclarations ::= ClassBodyDeclaration
            //
            case 80:
                break;
 
            //
            // Rule 81:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 81: {
                List a = (List) btParser.getSym(1),
                     b = (List) btParser.getSym(2);
                a.addAll(b);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 82:  ClassBodyDeclaration ::= ClassMemberDeclaration
            //
            case 82:
                break;
 
            //
            // Rule 83:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 83: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Block a = (Block) btParser.getSym(1);
                l.add(nf.Initializer(pos(), Flags.NONE, a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 84:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 84: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Block a = (Block) btParser.getSym(1);
                l.add(nf.Initializer(pos(), Flags.STATIC, a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 85:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 85: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 86:  ClassMemberDeclaration ::= FieldDeclaration
            //
            case 86:
                break;
 
            //
            // Rule 87:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 87: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 88:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 88: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 89:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 89: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 90:  ClassMemberDeclaration ::= SEMICOLON
            //
            case 90: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 91:  FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators SEMICOLON
            //
            case 91: {
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
            // Rule 92:  VariableDeclarators ::= VariableDeclarator
            //
            case 92: {
                List l = new TypedList(new LinkedList(), VarDeclarator.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 93:  VariableDeclarators ::= VariableDeclarators COMMA VariableDeclarator
            //
            case 93: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 94:  VariableDeclarator ::= VariableDeclaratorId
            //
            case 94:
                break;
 
            //
            // Rule 95:  VariableDeclarator ::= VariableDeclaratorId EQUAL VariableInitializer
            //
            case 95: {
                VarDeclarator a = (VarDeclarator) btParser.getSym(1);
                Expr b = (Expr) btParser.getSym(3);
                a.init = b;
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 96:  VariableDeclaratorId ::= identifier
            //
            case 96: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new VarDeclarator(pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 97:  VariableDeclaratorId ::= VariableDeclaratorId LBRACKET RBRACKET
            //
            case 97: {
                VarDeclarator a = (VarDeclarator) btParser.getSym(1);
                a.dims++;
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 98:  VariableInitializer ::= Expression
            //
            case 98:
                break;
 
            //
            // Rule 99:  VariableInitializer ::= ArrayInitializer
            //
            case 99:
                break;
 
            //
            // Rule 100:  FieldModifiers ::= FieldModifier
            //
            case 100:
                break;
 
            //
            // Rule 101:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 101: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 102:  FieldModifier ::= public
            //
            case 102: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 103:  FieldModifier ::= protected
            //
            case 103: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 104:  FieldModifier ::= private
            //
            case 104: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 105:  FieldModifier ::= static
            //
            case 105: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 106:  FieldModifier ::= final
            //
            case 106: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 107:  FieldModifier ::= transient
            //
            case 107: {
                btParser.setSym1(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 108:  FieldModifier ::= volatile
            //
            case 108: {
                btParser.setSym1(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 109:  MethodDeclaration ::= MethodHeader MethodBody
            //
            case 109: {
                MethodDecl a = (MethodDecl) btParser.getSym(1);
                Block b = (Block) btParser.getSym(2);
                btParser.setSym1(a.body(b));
                break;
            }
     
            //
            // Rule 110:  MethodHeader ::= MethodModifiersopt ResultType MethodDeclarator Throwsopt
            //
            case 110: {
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
            // Rule 111:  ResultType ::= Type
            //
            case 111:
                break;
 
            //
            // Rule 112:  ResultType ::= void
            //
            case 112: {
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 113:  MethodDeclarator ::= identifier LPAREN FormalParameterListopt RPAREN
            //
            case 113: {
                Object[] a = new Object[3];
                a[0] =  new Name(nf, ts, pos(), id(btParser.getToken(1)).getIdentifier());
                a[1] = btParser.getSym(3);
                a[2] = new Integer(0);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 114:  MethodDeclarator ::= MethodDeclarator LBRACKET RBRACKET
            //
            case 114: {
                Object[] a = (Object []) btParser.getSym(1);
                a[2] = new Integer(((Integer) a[2]).intValue() + 1);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 115:  FormalParameterList ::= LastFormalParameter
            //
            case 115: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 116:  FormalParameterList ::= FormalParameters COMMA LastFormalParameter
            //
            case 116: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 117:  FormalParameters ::= FormalParameter
            //
            case 117: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 118:  FormalParameters ::= FormalParameters COMMA FormalParameter
            //
            case 118: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 119:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 119: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                VarDeclarator b = (VarDeclarator) btParser.getSym(3);
                btParser.setSym1(nf.Formal(pos(), f, array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b.name));
                break;
            }
     
            //
            // Rule 121:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 121: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 122:  VariableModifier ::= final
            //
            case 122: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 123:  LastFormalParameter ::= VariableModifiersopt Type ...opt VariableDeclaratorId
            //
            case 123: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                assert(btParser.getSym(3) == null);
                VarDeclarator b = (VarDeclarator) btParser.getSym(4);
                btParser.setSym1(nf.Formal(pos(), f, array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b.name));
                break;
            }
     
            //
            // Rule 124:  MethodModifiers ::= MethodModifier
            //
            case 124:
                break;
 
            //
            // Rule 125:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 125: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 126:  MethodModifier ::= public
            //
            case 126: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 127:  MethodModifier ::= protected
            //
            case 127: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 128:  MethodModifier ::= private
            //
            case 128: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 129:  MethodModifier ::= abstract
            //
            case 129: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 130:  MethodModifier ::= static
            //
            case 130: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 131:  MethodModifier ::= final
            //
            case 131: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 132:  MethodModifier ::= synchronized
            //
            case 132: {
                btParser.setSym1(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 133:  MethodModifier ::= native
            //
            case 133: {
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 134:  MethodModifier ::= strictfp
            //
            case 134: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 135:  Throws ::= throws ExceptionTypeList
            //
            case 135: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 136:  ExceptionTypeList ::= ExceptionType
            //
            case 136: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 137:  ExceptionTypeList ::= ExceptionTypeList COMMA ExceptionType
            //
            case 137: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 138:  ExceptionType ::= ClassType
            //
            case 138:
                break;
 
            //
            // Rule 139:  ExceptionType ::= TypeVariable
            //
            case 139:
                break;
 
            //
            // Rule 140:  MethodBody ::= Block
            //
            case 140:
                break;
 
            //
            // Rule 141:  MethodBody ::= SEMICOLON
            //
            case 141:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 142:  InstanceInitializer ::= Block
            //
            case 142:
                break;
 
            //
            // Rule 143:  StaticInitializer ::= static Block
            //
            case 143: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 144:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 144: {
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
            // Rule 145:  ConstructorDeclarator ::= SimpleTypeName LPAREN FormalParameterListopt RPAREN
            //
            case 145: {
//vj                    assert(btParser.getSym(1) == null);
                Object[] a = new Object[3];
//vj                    a[0] = btParser.getSym(1);
                a[1] = btParser.getSym(1);
                a[2] = btParser.getSym(3);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 146:  SimpleTypeName ::= identifier
            //
            case 146: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 147:  ConstructorModifiers ::= ConstructorModifier
            //
            case 147:
                break;
 
            //
            // Rule 148:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 148: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 149:  ConstructorModifier ::= public
            //
            case 149: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 150:  ConstructorModifier ::= protected
            //
            case 150: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 151:  ConstructorModifier ::= private
            //
            case 151: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 152:  ConstructorBody ::= LBRACE ExplicitConstructorInvocationopt BlockStatementsopt RBRACE
            //
            case 152: {
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
            // Rule 153:  ExplicitConstructorInvocation ::= this LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 153: {
//vj                    assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.ThisCall(pos(), b));
                break;
            }
     
            //
            // Rule 154:  ExplicitConstructorInvocation ::= super LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 154: {
//vj                    assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.SuperCall(pos(), b));
                break;
            }
     
            //
            // Rule 155:  ExplicitConstructorInvocation ::= Primary DOT this LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 155: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.ThisCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 156:  ExplicitConstructorInvocation ::= Primary DOT super LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 156: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.SuperCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 157:  EnumDeclaration ::= ClassModifiersopt enum identifier Interfacesopt EnumBody
            //
            case 157:
                System.err.println("Rule " + 157 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 158:  EnumBody ::= LBRACE EnumConstantsopt ,opt EnumBodyDeclarationsopt RBRACE
            //
            case 158:
                System.err.println("Rule " + 158 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 159:  EnumConstants ::= EnumConstant
            //
            case 159:
                System.err.println("Rule " + 159 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 160:  EnumConstants ::= EnumConstants COMMA EnumConstant
            //
            case 160:
                System.err.println("Rule " + 160 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 161:  EnumConstant ::= identifier Argumentsopt ClassBodyopt
            //
            case 161:
                System.err.println("Rule " + 161 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 162:  Arguments ::= LPAREN ArgumentListopt RPAREN
            //
            case 162: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 163:  EnumBodyDeclarations ::= SEMICOLON ClassBodyDeclarationsopt
            //
            case 163:
                System.err.println("Rule " + 163 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 164:  InterfaceDeclaration ::= NormalInterfaceDeclaration
            //
            case 164:
                break;
 
            //
            // Rule 165:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier ExtendsInterfacesopt InterfaceBody
            //
            case 165: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
//vj                    assert(btParser.getSym(4) == null);
                List c = (List) btParser.getSym(4);
                ClassBody d = (ClassBody) btParser.getSym(5);
                btParser.setSym1(nf.ClassDecl(pos(), a.Interface(), b.getIdentifier(), null, c, d));
                break;
            }
     
            //
            // Rule 166:  InterfaceModifiers ::= InterfaceModifier
            //
            case 166:
                break;
 
            //
            // Rule 167:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 167: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 168:  InterfaceModifier ::= public
            //
            case 168: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 169:  InterfaceModifier ::= protected
            //
            case 169: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 170:  InterfaceModifier ::= private
            //
            case 170: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 171:  InterfaceModifier ::= abstract
            //
            case 171: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 172:  InterfaceModifier ::= static
            //
            case 172: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 173:  InterfaceModifier ::= strictfp
            //
            case 173: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 174:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 174: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 175:  ExtendsInterfaces ::= ExtendsInterfaces COMMA InterfaceType
            //
            case 175: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 176:  InterfaceBody ::= LBRACE InterfaceMemberDeclarationsopt RBRACE
            //
            case 176: {
                List a = (List)btParser.getSym(2);
                btParser.setSym1(nf.ClassBody(pos(), a));
                break;
            }
     
            //
            // Rule 177:  InterfaceMemberDeclarations ::= InterfaceMemberDeclaration
            //
            case 177:
                break;
 
            //
            // Rule 178:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 178: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 179:  InterfaceMemberDeclaration ::= ConstantDeclaration
            //
            case 179:
                break;
 
            //
            // Rule 180:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 180: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 181:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 181: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 182:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 182: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 183:  InterfaceMemberDeclaration ::= SEMICOLON
            //
            case 183: {
                btParser.setSym1(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 184:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 184: {
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
            // Rule 185:  ConstantModifiers ::= ConstantModifier
            //
            case 185:
                break;
 
            //
            // Rule 186:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 186: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 187:  ConstantModifier ::= public
            //
            case 187: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 188:  ConstantModifier ::= static
            //
            case 188: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 189:  ConstantModifier ::= final
            //
            case 189: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 190:  AbstractMethodDeclaration ::= AbstractMethodModifiersopt ResultType MethodDeclarator Throwsopt SEMICOLON
            //
            case 190: {
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
            // Rule 191:  AbstractMethodModifiers ::= AbstractMethodModifier
            //
            case 191:
                break;
 
            //
            // Rule 192:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 192: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 193:  AbstractMethodModifier ::= public
            //
            case 193: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 194:  AbstractMethodModifier ::= abstract
            //
            case 194: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 195:  AnnotationTypeDeclaration ::= InterfaceModifiersopt AT interface identifier AnnotationTypeBody
            //
            case 195:
                System.err.println("Rule " + 195 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 196:  AnnotationTypeBody ::= LBRACE AnnotationTypeElementDeclarationsopt RBRACE
            //
            case 196:
                System.err.println("Rule " + 196 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 197:  AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclaration
            //
            case 197:
                System.err.println("Rule " + 197 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 198:  AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclarations AnnotationTypeElementDeclaration
            //
            case 198:
                System.err.println("Rule " + 198 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 199:  AnnotationTypeElementDeclaration ::= AbstractMethodModifiersopt Type identifier LPAREN RPAREN DefaultValueopt SEMICOLON
            //
            case 199:
                System.err.println("Rule " + 199 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 200:  AnnotationTypeElementDeclaration ::= ConstantDeclaration
            //
            case 200:
                System.err.println("Rule " + 200 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 201:  AnnotationTypeElementDeclaration ::= ClassDeclaration
            //
            case 201:
                System.err.println("Rule " + 201 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 202:  AnnotationTypeElementDeclaration ::= InterfaceDeclaration
            //
            case 202:
                System.err.println("Rule " + 202 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 203:  AnnotationTypeElementDeclaration ::= EnumDeclaration
            //
            case 203:
                System.err.println("Rule " + 203 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 204:  AnnotationTypeElementDeclaration ::= AnnotationTypeDeclaration
            //
            case 204:
                System.err.println("Rule " + 204 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 205:  AnnotationTypeElementDeclaration ::= SEMICOLON
            //
            case 205:
                System.err.println("Rule " + 205 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 206:  DefaultValue ::= default ElementValue
            //
            case 206:
                System.err.println("Rule " + 206 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 207:  Annotations ::= Annotation
            //
            case 207:
                System.err.println("Rule " + 207 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 208:  Annotations ::= Annotations Annotation
            //
            case 208:
                System.err.println("Rule " + 208 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 209:  Annotation ::= NormalAnnotation
            //
            case 209:
                System.err.println("Rule " + 209 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 210:  Annotation ::= MarkerAnnotation
            //
            case 210:
                System.err.println("Rule " + 210 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 211:  Annotation ::= SingleElementAnnotation
            //
            case 211:
                System.err.println("Rule " + 211 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 212:  NormalAnnotation ::= AT TypeName LPAREN ElementValuePairsopt RPAREN
            //
            case 212:
                System.err.println("Rule " + 212 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 213:  ElementValuePairs ::= ElementValuePair
            //
            case 213:
                System.err.println("Rule " + 213 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 214:  ElementValuePairs ::= ElementValuePairs COMMA ElementValuePair
            //
            case 214:
                System.err.println("Rule " + 214 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 215:  ElementValuePair ::= SimpleName EQUAL ElementValue
            //
            case 215:
                System.err.println("Rule " + 215 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 216:  SimpleName ::= identifier
            //
            case 216: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 217:  ElementValue ::= ConditionalExpression
            //
            case 217:
                System.err.println("Rule " + 217 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 218:  ElementValue ::= Annotation
            //
            case 218:
                System.err.println("Rule " + 218 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 219:  ElementValue ::= ElementValueArrayInitializer
            //
            case 219:
                System.err.println("Rule " + 219 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 220:  ElementValueArrayInitializer ::= LBRACE ElementValuesopt ,opt RBRACE
            //
            case 220:
                System.err.println("Rule " + 220 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 221:  ElementValues ::= ElementValue
            //
            case 221:
                System.err.println("Rule " + 221 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 222:  ElementValues ::= ElementValues COMMA ElementValue
            //
            case 222:
                System.err.println("Rule " + 222 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 223:  MarkerAnnotation ::= AT TypeName
            //
            case 223:
                System.err.println("Rule " + 223 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 224:  SingleElementAnnotation ::= AT TypeName LPAREN ElementValue RPAREN
            //
            case 224:
                System.err.println("Rule " + 224 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 225:  ArrayInitializer ::= LBRACE VariableInitializersopt ,opt RBRACE
            //
            case 225: {
                List a = (List) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.ArrayInit(pos()));
                else btParser.setSym1(nf.ArrayInit(pos(), a));
                break;
            }
     
            //
            // Rule 226:  VariableInitializers ::= VariableInitializer
            //
            case 226: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 227:  VariableInitializers ::= VariableInitializers COMMA VariableInitializer
            //
            case 227: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 228:  Block ::= LBRACE BlockStatementsopt RBRACE
            //
            case 228: {
                List l = (List) btParser.getSym(2);
                btParser.setSym1(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 229:  BlockStatements ::= BlockStatement
            //
            case 229: {
                List l = new TypedList(new LinkedList(), Stmt.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 230:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 230: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 231:  BlockStatement ::= LocalVariableDeclarationStatement
            //
            case 231:
                break;
 
            //
            // Rule 232:  BlockStatement ::= ClassDeclaration
            //
            case 232: {
                ClassDecl a = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 233:  BlockStatement ::= Statement
            //
            case 233: {
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 234:  LocalVariableDeclarationStatement ::= LocalVariableDeclaration SEMICOLON
            //
            case 234:
                break;
 
            //
            // Rule 235:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 235: {
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
            // Rule 236:  Statement ::= StatementWithoutTrailingSubstatement
            //
            case 236:
                break;
 
            //
            // Rule 237:  Statement ::= LabeledStatement
            //
            case 237:
                break;
 
            //
            // Rule 238:  Statement ::= IfThenStatement
            //
            case 238:
                break;
 
            //
            // Rule 239:  Statement ::= IfThenElseStatement
            //
            case 239:
                break;
 
            //
            // Rule 240:  Statement ::= WhileStatement
            //
            case 240:
                break;
 
            //
            // Rule 241:  Statement ::= ForStatement
            //
            case 241:
                break;
 
            //
            // Rule 242:  StatementWithoutTrailingSubstatement ::= Block
            //
            case 242:
                break;
 
            //
            // Rule 243:  StatementWithoutTrailingSubstatement ::= EmptyStatement
            //
            case 243:
                break;
 
            //
            // Rule 244:  StatementWithoutTrailingSubstatement ::= ExpressionStatement
            //
            case 244:
                break;
 
            //
            // Rule 245:  StatementWithoutTrailingSubstatement ::= AssertStatement
            //
            case 245:
                break;
 
            //
            // Rule 246:  StatementWithoutTrailingSubstatement ::= SwitchStatement
            //
            case 246:
                break;
 
            //
            // Rule 247:  StatementWithoutTrailingSubstatement ::= DoStatement
            //
            case 247:
                break;
 
            //
            // Rule 248:  StatementWithoutTrailingSubstatement ::= BreakStatement
            //
            case 248:
                break;
 
            //
            // Rule 249:  StatementWithoutTrailingSubstatement ::= ContinueStatement
            //
            case 249:
                break;
 
            //
            // Rule 250:  StatementWithoutTrailingSubstatement ::= ReturnStatement
            //
            case 250:
                break;
 
            //
            // Rule 251:  StatementWithoutTrailingSubstatement ::= SynchronizedStatement
            //
            case 251:
                break;
 
            //
            // Rule 252:  StatementWithoutTrailingSubstatement ::= ThrowStatement
            //
            case 252:
                break;
 
            //
            // Rule 253:  StatementWithoutTrailingSubstatement ::= TryStatement
            //
            case 253:
                break;
 
            //
            // Rule 254:  StatementNoShortIf ::= StatementWithoutTrailingSubstatement
            //
            case 254:
                break;
 
            //
            // Rule 255:  StatementNoShortIf ::= LabeledStatementNoShortIf
            //
            case 255:
                break;
 
            //
            // Rule 256:  StatementNoShortIf ::= IfThenElseStatementNoShortIf
            //
            case 256:
                break;
 
            //
            // Rule 257:  StatementNoShortIf ::= WhileStatementNoShortIf
            //
            case 257:
                break;
 
            //
            // Rule 258:  StatementNoShortIf ::= ForStatementNoShortIf
            //
            case 258:
                break;
 
            //
            // Rule 259:  IfThenStatement ::= if LPAREN Expression RPAREN Statement
            //
            case 259: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.If(pos(), a, b));
                break;
            }
     
            //
            // Rule 260:  IfThenElseStatement ::= if LPAREN Expression RPAREN StatementNoShortIf else Statement
            //
            case 260: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 261:  IfThenElseStatementNoShortIf ::= if LPAREN Expression RPAREN StatementNoShortIf else StatementNoShortIf
            //
            case 261: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 262:  EmptyStatement ::= SEMICOLON
            //
            case 262: {
                btParser.setSym1(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 263:  LabeledStatement ::= identifier COLON Statement
            //
            case 263: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 264:  LabeledStatementNoShortIf ::= identifier COLON StatementNoShortIf
            //
            case 264: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 265:  ExpressionStatement ::= StatementExpression SEMICOLON
            //
            case 265: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Eval(pos(), a));
                break;
            }
     
            //
            // Rule 266:  StatementExpression ::= Assignment
            //
            case 266:
                break;
 
            //
            // Rule 267:  StatementExpression ::= PreIncrementExpression
            //
            case 267:
                break;
 
            //
            // Rule 268:  StatementExpression ::= PreDecrementExpression
            //
            case 268:
                break;
 
            //
            // Rule 269:  StatementExpression ::= PostIncrementExpression
            //
            case 269:
                break;
 
            //
            // Rule 270:  StatementExpression ::= PostDecrementExpression
            //
            case 270:
                break;
 
            //
            // Rule 271:  StatementExpression ::= MethodInvocation
            //
            case 271:
                break;
 
            //
            // Rule 272:  StatementExpression ::= ClassInstanceCreationExpression
            //
            case 272:
                break;
 
            //
            // Rule 273:  AssertStatement ::= assert Expression SEMICOLON
            //
            case 273: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Assert(pos(), a));
                break;
            }
     
            //
            // Rule 274:  AssertStatement ::= assert Expression COLON Expression SEMICOLON
            //
            case 274: {
                Expr a = (Expr) btParser.getSym(2),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Assert(pos(), a, b));
                break;
            }
     
            //
            // Rule 275:  SwitchStatement ::= switch LPAREN Expression RPAREN SwitchBlock
            //
            case 275: {
                Expr a = (Expr) btParser.getSym(3);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.Switch(pos(), a, b));
                break;
            }
     
            //
            // Rule 276:  SwitchBlock ::= LBRACE SwitchBlockStatementGroupsopt SwitchLabelsopt RBRACE
            //
            case 276: {
                List l = (List) btParser.getSym(2),
                     l2 = (List) btParser.getSym(3);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 277:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroup
            //
            case 277:
                break;
 
            //
            // Rule 278:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 278: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 279:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 279: {
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);

                List l1 = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l1);
                l.add(nf.SwitchBlock(pos(), l2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 280:  SwitchLabels ::= SwitchLabel
            //
            case 280: {
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 281:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 281: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 282:  SwitchLabel ::= case ConstantExpression COLON
            //
            case 282: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Case(pos(), a));
                break;
            }
     
            //
            // Rule 283:  SwitchLabel ::= case EnumConstant COLON
            //
            case 283:
                System.err.println("Rule " + 283 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 284:  SwitchLabel ::= default COLON
            //
            case 284: {
                btParser.setSym1(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 285:  EnumConstant ::= identifier
            //
            case 285:
                System.err.println("Rule " + 285 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 286:  WhileStatement ::= while LPAREN Expression RPAREN Statement
            //
            case 286: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 287:  WhileStatementNoShortIf ::= while LPAREN Expression RPAREN StatementNoShortIf
            //
            case 287: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 288:  DoStatement ::= do Statement while LPAREN Expression RPAREN SEMICOLON
            //
            case 288: {
                Stmt a = (Stmt) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Do(pos(), a, b));
                break;
            }
     
            //
            // Rule 289:  ForStatement ::= BasicForStatement
            //
            case 289:
                break;
 
            //
            // Rule 290:  ForStatement ::= EnhancedForStatement
            //
            case 290:
                System.err.println("Rule " + 290 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 291:  BasicForStatement ::= for LPAREN ForInitopt SEMICOLON Expressionopt SEMICOLON ForUpdateopt RPAREN Statement
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
            // Rule 292:  ForStatementNoShortIf ::= for LPAREN ForInitopt SEMICOLON Expressionopt SEMICOLON ForUpdateopt RPAREN StatementNoShortIf
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
            // Rule 293:  ForInit ::= StatementExpressionList
            //
            case 293:
                break;
 
            //
            // Rule 294:  ForInit ::= LocalVariableDeclaration
            //
            case 294: {
                List l = new TypedList(new LinkedList(), ForInit.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 295:  ForUpdate ::= StatementExpressionList
            //
            case 295:
                break;
 
            //
            // Rule 296:  StatementExpressionList ::= StatementExpression
            //
            case 296: {
                List l = new TypedList(new LinkedList(), Eval.class, false);
                Expr a = (Expr) btParser.getSym(1);
                l.add(nf.Eval(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 297:  StatementExpressionList ::= StatementExpressionList COMMA StatementExpression
            //
            case 297: {
                List l = (List) btParser.getSym(1);
                Expr a = (Expr) btParser.getSym(3);
                l.add(nf.Eval(pos(), a));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 298:  EnhancedForStatement ::= for LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 298:
                System.err.println("Rule " + 298 + " not yet implemented");
                actions_stopped = true;
                break;
 
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
                // TODO: remove any prefix (such as 0x)
                polyglot.lex.IntegerLiteral a = int_lit(btParser.getToken(1), 10);
                btParser.setSym1(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 323:  Literal ::= LongLiteral
            //
            case 323: {
                // TODO: remove any suffix (such as L) or prefix (such as 0x)
                polyglot.lex.LongLiteral a = long_lit(btParser.getToken(1), 10);
                btParser.setSym1(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 324:  Literal ::= FloatingPointLiteral
            //
            case 324: {
                // TODO: remove any suffix (such as F)
                polyglot.lex.FloatLiteral a = float_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 325:  Literal ::= DoubleLiteral
            //
            case 325: {
                // TODO: remove any suffix (such as D)
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
                polyglot.lex.StringLiteral a = string_lit(btParser.getToken(1));
                btParser.setSym1(nf.StringLit(pos(), a.getValue()));
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
            // Rule 337:  ArrayCreationExpression ::= new PrimitiveType DimExprs Dimsopt
            //
            case 337: {
                CanonicalTypeNode a = (CanonicalTypeNode) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                Integer c = (Integer) btParser.getSym(4);
                btParser.setSym1(nf.NewArray(pos(), a, b, c.intValue()));
                break;
            }
     
            //
            // Rule 338:  ArrayCreationExpression ::= new ClassOrInterfaceType DimExprs Dimsopt
            //
            case 338: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                Integer c = (Integer) btParser.getSym(4);
                btParser.setSym1(nf.NewArray(pos(), a, b, c.intValue()));
                break;
            }
     
            //
            // Rule 339:  ArrayCreationExpression ::= new PrimitiveType Dims ArrayInitializer
            //
            case 339: {
                CanonicalTypeNode a = (CanonicalTypeNode) btParser.getSym(2);
                Integer b = (Integer) btParser.getSym(3);
                ArrayInit c = (ArrayInit) btParser.getSym(4);
                btParser.setSym1(nf.NewArray(pos(), a, b.intValue(), c));
                break;
            }
     
            //
            // Rule 340:  ArrayCreationExpression ::= new ClassOrInterfaceType Dims ArrayInitializer
            //
            case 340: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Integer b = (Integer) btParser.getSym(3);
                ArrayInit c = (ArrayInit) btParser.getSym(4);
                btParser.setSym1(nf.NewArray(pos(), a, b.intValue(), c));
                break;
            }
     
            //
            // Rule 341:  DimExprs ::= DimExpr
            //
            case 341: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 342:  DimExprs ::= DimExprs DimExpr
            //
            case 342: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 343:  DimExpr ::= LBRACKET Expression RBRACKET
            //
            case 343: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(a.position(pos()));
                break;
            }
     
            //
            // Rule 344:  Dims ::= LBRACKET RBRACKET
            //
            case 344: {
                btParser.setSym1(new Integer(0));
                break;
            }
     
            //
            // Rule 345:  Dims ::= Dims LBRACKET RBRACKET
            //
            case 345: {
                Integer a = (Integer) btParser.getSym(1);
                btParser.setSym1(new Integer(a.intValue() + 1));
                break;
            }
     
            //
            // Rule 346:  FieldAccess ::= Primary DOT identifier
            //
            case 346: {
                Expr a = (Expr) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(), a, b.getIdentifier()));
                break;
            }
     
            //
            // Rule 347:  FieldAccess ::= super DOT identifier
            //
            case 347: {
                polyglot.lex.Identifier a = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken())), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 348:  FieldAccess ::= ClassName DOT super DOT identifier
            //
            case 348: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier()));
                break;
            }
     
            //
            // Rule 349:  MethodInvocation ::= MethodName LPAREN ArgumentListopt RPAREN
            //
            case 349: {
                Name a = (Name) btParser.getSym(1);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Call(pos(), a.prefix == null ? null : a.prefix.toReceiver(), a.name, b));
                break;
            }
     
            //
            // Rule 350:  MethodInvocation ::= Primary DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 350: {
                Expr a = (Expr) btParser.getSym(1);
//vj                    assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), a, b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 351:  MethodInvocation ::= super DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 351: {
//vj                    assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                List c = (List) btParser.getSym(5);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken())), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 352:  MethodInvocation ::= ClassName DOT super DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 352: {
                Name a = (Name) btParser.getSym(1);
//vj                    assert(btParser.getSym(5) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(5));
                List c = (List) btParser.getSym(7);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 353:  MethodInvocation ::= TypeName DOT identifier LPAREN ArgumentListopt RPAREN
            //
            case 353:
                System.err.println("Rule " + 353 + " not yet implemented");
                actions_stopped = true;
                break;
     
            //
            // Rule 356:  PostfixExpression ::= Primary
            //
            case 356:
                break;
 
            //
            // Rule 357:  PostfixExpression ::= ExpressionName
            //
            case 357: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 358:  PostfixExpression ::= PostIncrementExpression
            //
            case 358:
                break;
 
            //
            // Rule 359:  PostfixExpression ::= PostDecrementExpression
            //
            case 359:
                break;
 
            //
            // Rule 360:  PostIncrementExpression ::= PostfixExpression PLUS_PLUS
            //
            case 360: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 361:  PostDecrementExpression ::= PostfixExpression MINUS_MINUS
            //
            case 361: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 362:  UnaryExpression ::= PreIncrementExpression
            //
            case 362:
                break;
 
            //
            // Rule 363:  UnaryExpression ::= PreDecrementExpression
            //
            case 363:
                break;
 
            //
            // Rule 364:  UnaryExpression ::= PLUS UnaryExpression
            //
            case 364: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.POS, a));
                break;
            }
     
            //
            // Rule 365:  UnaryExpression ::= MINUS UnaryExpression
            //
            case 365: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NEG, a));
                break;
            }
     
            //
            // Rule 367:  PreIncrementExpression ::= PLUS_PLUS UnaryExpression
            //
            case 367: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_INC, a));
                break;
            }
     
            //
            // Rule 368:  PreDecrementExpression ::= MINUS_MINUS UnaryExpression
            //
            case 368: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_DEC, a));
                break;
            }
     
            //
            // Rule 369:  UnaryExpressionNotPlusMinus ::= PostfixExpression
            //
            case 369:
                break;
 
            //
            // Rule 370:  UnaryExpressionNotPlusMinus ::= TWIDDLE UnaryExpression
            //
            case 370: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.BIT_NOT, a));
                break;
            }
     
            //
            // Rule 371:  UnaryExpressionNotPlusMinus ::= NOT UnaryExpression
            //
            case 371: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NOT, a));
                break;
            }
     
            //
            // Rule 373:  MultiplicativeExpression ::= UnaryExpression
            //
            case 373:
                break;
 
            //
            // Rule 374:  MultiplicativeExpression ::= MultiplicativeExpression MULTIPLY UnaryExpression
            //
            case 374: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MUL, b));
                break;
            }
     
            //
            // Rule 375:  MultiplicativeExpression ::= MultiplicativeExpression DIVIDE UnaryExpression
            //
            case 375: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.DIV, b));
                break;
            }
     
            //
            // Rule 376:  MultiplicativeExpression ::= MultiplicativeExpression REMAINDER UnaryExpression
            //
            case 376: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MOD, b));
                break;
            }
     
            //
            // Rule 377:  AdditiveExpression ::= MultiplicativeExpression
            //
            case 377:
                break;
 
            //
            // Rule 378:  AdditiveExpression ::= AdditiveExpression PLUS MultiplicativeExpression
            //
            case 378: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.ADD, b));
                break;
            }
     
            //
            // Rule 379:  AdditiveExpression ::= AdditiveExpression MINUS MultiplicativeExpression
            //
            case 379: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SUB, b));
                break;
            }
     
            //
            // Rule 380:  ShiftExpression ::= AdditiveExpression
            //
            case 380:
                break;
 
            //
            // Rule 381:  ShiftExpression ::= ShiftExpression LEFT_SHIFT AdditiveExpression
            //
            case 381: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHL, b));
                break;
            }
     
            //
            // Rule 382:  ShiftExpression ::= ShiftExpression GREATER GREATER AdditiveExpression
            //
            case 382: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHR, b));
                break;
            }
     
            //
            // Rule 383:  ShiftExpression ::= ShiftExpression GREATER GREATER GREATER AdditiveExpression
            //
            case 383: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Binary(pos(), a, Binary.USHR, b));
                break;
            }
     
            //
            // Rule 384:  RelationalExpression ::= ShiftExpression
            //
            case 384:
                break;
 
            //
            // Rule 385:  RelationalExpression ::= RelationalExpression LESS ShiftExpression
            //
            case 385: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LT, b));
                break;
            }
     
            //
            // Rule 386:  RelationalExpression ::= RelationalExpression GREATER ShiftExpression
            //
            case 386: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GT, b));
                break;
            }
     
            //
            // Rule 387:  RelationalExpression ::= RelationalExpression LESS_EQUAL ShiftExpression
            //
            case 387: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LE, b));
                break;
            }
     
            //
            // Rule 388:  RelationalExpression ::= RelationalExpression GREATER EQUAL ShiftExpression
            //
            case 388: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GE, b));
                break;
            }
     
            //
            // Rule 389:  EqualityExpression ::= RelationalExpression
            //
            case 389:
                break;
 
            //
            // Rule 390:  EqualityExpression ::= EqualityExpression EQUAL_EQUAL RelationalExpression
            //
            case 390: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.EQ, b));
                break;
            }
     
            //
            // Rule 391:  EqualityExpression ::= EqualityExpression NOT_EQUAL RelationalExpression
            //
            case 391: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.NE, b));
                break;
            }
     
            //
            // Rule 392:  AndExpression ::= EqualityExpression
            //
            case 392:
                break;
 
            //
            // Rule 393:  AndExpression ::= AndExpression AND EqualityExpression
            //
            case 393: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_AND, b));
                break;
            }
     
            //
            // Rule 394:  ExclusiveOrExpression ::= AndExpression
            //
            case 394:
                break;
 
            //
            // Rule 395:  ExclusiveOrExpression ::= ExclusiveOrExpression XOR AndExpression
            //
            case 395: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_XOR, b));
                break;
            }
     
            //
            // Rule 396:  InclusiveOrExpression ::= ExclusiveOrExpression
            //
            case 396:
                break;
 
            //
            // Rule 397:  InclusiveOrExpression ::= InclusiveOrExpression OR ExclusiveOrExpression
            //
            case 397: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_OR, b));
                break;
            }
     
            //
            // Rule 398:  ConditionalAndExpression ::= InclusiveOrExpression
            //
            case 398:
                break;
 
            //
            // Rule 399:  ConditionalAndExpression ::= ConditionalAndExpression AND_AND InclusiveOrExpression
            //
            case 399: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_AND, b));
                break;
            }
     
            //
            // Rule 400:  ConditionalOrExpression ::= ConditionalAndExpression
            //
            case 400:
                break;
 
            //
            // Rule 401:  ConditionalOrExpression ::= ConditionalOrExpression OR_OR ConditionalAndExpression
            //
            case 401: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_OR, b));
                break;
            }
     
            //
            // Rule 402:  ConditionalExpression ::= ConditionalOrExpression
            //
            case 402:
                break;
 
            //
            // Rule 403:  ConditionalExpression ::= ConditionalOrExpression QUESTION Expression COLON ConditionalExpression
            //
            case 403: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3),
                     c = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Conditional(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 404:  AssignmentExpression ::= ConditionalExpression
            //
            case 404:
                break;
 
            //
            // Rule 405:  AssignmentExpression ::= Assignment
            //
            case 405:
                break;
 
            //
            // Rule 406:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 406: {
                Expr a = (Expr) btParser.getSym(1);
                Assign.Operator b = (Assign.Operator) btParser.getSym(2);
                Expr c = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Assign(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 407:  LeftHandSide ::= ExpressionName
            //
            case 407: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 408:  LeftHandSide ::= FieldAccess
            //
            case 408:
                break;
 
            //
            // Rule 409:  LeftHandSide ::= ArrayAccess
            //
            case 409:
                break;
 
            //
            // Rule 410:  AssignmentOperator ::= EQUAL
            //
            case 410: {
                btParser.setSym1(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 411:  AssignmentOperator ::= MULTIPLY_EQUAL
            //
            case 411: {
                btParser.setSym1(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 412:  AssignmentOperator ::= DIVIDE_EQUAL
            //
            case 412: {
                btParser.setSym1(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 413:  AssignmentOperator ::= REMAINDER_EQUAL
            //
            case 413: {
                btParser.setSym1(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 414:  AssignmentOperator ::= PLUS_EQUAL
            //
            case 414: {
                btParser.setSym1(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 415:  AssignmentOperator ::= MINUS_EQUAL
            //
            case 415: {
                btParser.setSym1(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 416:  AssignmentOperator ::= LEFT_SHIFT_EQUAL
            //
            case 416: {
                btParser.setSym1(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 417:  AssignmentOperator ::= GREATER GREATER EQUAL
            //
            case 417: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 418:  AssignmentOperator ::= GREATER GREATER GREATER EQUAL
            //
            case 418: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 419:  AssignmentOperator ::= AND_EQUAL
            //
            case 419: {
                btParser.setSym1(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 420:  AssignmentOperator ::= XOR_EQUAL
            //
            case 420: {
                btParser.setSym1(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 421:  AssignmentOperator ::= OR_EQUAL
            //
            case 421: {
                btParser.setSym1(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 422:  Expression ::= AssignmentExpression
            //
            case 422:
                break;
 
            //
            // Rule 423:  ConstantExpression ::= Expression
            //
            case 423:
                break;
 
            //
            // Rule 424:  Dimsopt ::=
            //
            case 424: {
                btParser.setSym1(new Integer(0));
                break;
            }
     
            //
            // Rule 425:  Dimsopt ::= Dims
            //
            case 425:
                break;
 
            //
            // Rule 426:  Catchesopt ::=
            //
            case 426: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 427:  Catchesopt ::= Catches
            //
            case 427:
                break;
 
            //
            // Rule 428:  identifieropt ::=
            //
            case 428:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 429:  identifieropt ::= identifier
            //
            case 429: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 430:  ForUpdateopt ::=
            //
            case 430: {
                btParser.setSym1(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 431:  ForUpdateopt ::= ForUpdate
            //
            case 431:
                break;
 
            //
            // Rule 432:  Expressionopt ::=
            //
            case 432:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 433:  Expressionopt ::= Expression
            //
            case 433:
                break;
 
            //
            // Rule 434:  ForInitopt ::=
            //
            case 434: {
                btParser.setSym1(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 435:  ForInitopt ::= ForInit
            //
            case 435:
                break;
 
            //
            // Rule 436:  SwitchLabelsopt ::=
            //
            case 436: {
                btParser.setSym1(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 437:  SwitchLabelsopt ::= SwitchLabels
            //
            case 437:
                break;
 
            //
            // Rule 438:  SwitchBlockStatementGroupsopt ::=
            //
            case 438: {
                btParser.setSym1(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 439:  SwitchBlockStatementGroupsopt ::= SwitchBlockStatementGroups
            //
            case 439:
                break;
 
            //
            // Rule 440:  VariableModifiersopt ::=
            //
            case 440: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 441:  VariableModifiersopt ::= VariableModifiers
            //
            case 441:
                break;
 
            //
            // Rule 442:  VariableInitializersopt ::=
            //
            case 442:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 443:  VariableInitializersopt ::= VariableInitializers
            //
            case 443:
                break;
 
            //
            // Rule 444:  ElementValuesopt ::=
            //
            case 444:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 445:  ElementValuesopt ::= ElementValues
            //
            case 445:
                System.err.println("Rule " + 445 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 446:  ElementValuePairsopt ::=
            //
            case 446:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 447:  ElementValuePairsopt ::= ElementValuePairs
            //
            case 447:
                System.err.println("Rule " + 447 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 448:  DefaultValueopt ::=
            //
            case 448:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 449:  DefaultValueopt ::= DefaultValue
            //
            case 449:
                break;
 
            //
            // Rule 450:  AnnotationTypeElementDeclarationsopt ::=
            //
            case 450:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 451:  AnnotationTypeElementDeclarationsopt ::= AnnotationTypeElementDeclarations
            //
            case 451:
                System.err.println("Rule " + 451 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 452:  AbstractMethodModifiersopt ::=
            //
            case 452: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 453:  AbstractMethodModifiersopt ::= AbstractMethodModifiers
            //
            case 453:
                break;
 
            //
            // Rule 454:  ConstantModifiersopt ::=
            //
            case 454: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 455:  ConstantModifiersopt ::= ConstantModifiers
            //
            case 455:
                break;
 
            //
            // Rule 456:  InterfaceMemberDeclarationsopt ::=
            //
            case 456: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 457:  InterfaceMemberDeclarationsopt ::= InterfaceMemberDeclarations
            //
            case 457:
                break;
 
            //
            // Rule 458:  ExtendsInterfacesopt ::=
            //
            case 458: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 459:  ExtendsInterfacesopt ::= ExtendsInterfaces
            //
            case 459:
                break;
 
            //
            // Rule 460:  InterfaceModifiersopt ::=
            //
            case 460: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 461:  InterfaceModifiersopt ::= InterfaceModifiers
            //
            case 461:
                break;
 
            //
            // Rule 462:  ClassBodyopt ::=
            //
            case 462:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 463:  ClassBodyopt ::= ClassBody
            //
            case 463:
                break;
 
            //
            // Rule 464:  Argumentsopt ::=
            //
            case 464:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 465:  Argumentsopt ::= Arguments
            //
            case 465:
                System.err.println("Rule " + 465 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 466:  EnumBodyDeclarationsopt ::=
            //
            case 466:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 467:  EnumBodyDeclarationsopt ::= EnumBodyDeclarations
            //
            case 467:
                System.err.println("Rule " + 467 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 468:  ,opt ::=
            //
            case 468:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 469:  ,opt ::= COMMA
            //
            case 469:
                break;
 
            //
            // Rule 470:  EnumConstantsopt ::=
            //
            case 470:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 471:  EnumConstantsopt ::= EnumConstants
            //
            case 471:
                System.err.println("Rule " + 471 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 472:  ArgumentListopt ::=
            //
            case 472: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 473:  ArgumentListopt ::= ArgumentList
            //
            case 473:
                break;
 
            //
            // Rule 474:  BlockStatementsopt ::=
            //
            case 474: {
                btParser.setSym1(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 475:  BlockStatementsopt ::= BlockStatements
            //
            case 475:
                break;
 
            //
            // Rule 476:  ExplicitConstructorInvocationopt ::=
            //
            case 476:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 477:  ExplicitConstructorInvocationopt ::= ExplicitConstructorInvocation
            //
            case 477:
                break;
 
            //
            // Rule 478:  ConstructorModifiersopt ::=
            //
            case 478: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 479:  ConstructorModifiersopt ::= ConstructorModifiers
            //
            case 479:
                break;
 
            //
            // Rule 480:  ...opt ::=
            //
            case 480:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 481:  ...opt ::= ELLIPSIS
            //
            case 481:
                break;
 
            //
            // Rule 482:  FormalParameterListopt ::=
            //
            case 482: {
                btParser.setSym1(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 483:  FormalParameterListopt ::= FormalParameterList
            //
            case 483:
                break;
 
            //
            // Rule 484:  Throwsopt ::=
            //
            case 484: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 485:  Throwsopt ::= Throws
            //
            case 485:
                break;
 
            //
            // Rule 486:  MethodModifiersopt ::=
            //
            case 486: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 487:  MethodModifiersopt ::= MethodModifiers
            //
            case 487:
                break;
 
            //
            // Rule 488:  FieldModifiersopt ::=
            //
            case 488: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 489:  FieldModifiersopt ::= FieldModifiers
            //
            case 489:
                break;
 
            //
            // Rule 490:  ClassBodyDeclarationsopt ::=
            //
            case 490: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 491:  ClassBodyDeclarationsopt ::= ClassBodyDeclarations
            //
            case 491:
                break;
 
            //
            // Rule 492:  Interfacesopt ::=
            //
            case 492: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 493:  Interfacesopt ::= Interfaces
            //
            case 493:
                break;
 
            //
            // Rule 494:  Superopt ::=
            //
            case 494:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 495:  Superopt ::= Super
            //
            case 495:
                break;
 
            //
            // Rule 496:  TypeParametersopt ::=
            //
            case 496:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 497:  TypeParametersopt ::= TypeParameters
            //
            case 497:
                break;
 
            //
            // Rule 498:  ClassModifiersopt ::=
            //
            case 498: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 499:  ClassModifiersopt ::= ClassModifiers
            //
            case 499:
                break;
 
            //
            // Rule 500:  Annotationsopt ::=
            //
            case 500:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 501:  Annotationsopt ::= Annotations
            //
            case 501:
                System.err.println("Rule " + 501 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 502:  TypeDeclarationsopt ::=
            //
            case 502: {
                btParser.setSym1(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 503:  TypeDeclarationsopt ::= TypeDeclarations
            //
            case 503:
                break;
 
            //
            // Rule 504:  ImportDeclarationsopt ::=
            //
            case 504: {
                btParser.setSym1(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 505:  ImportDeclarationsopt ::= ImportDeclarations
            //
            case 505:
                break;
 
            //
            // Rule 506:  PackageDeclarationopt ::=
            //
            case 506:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 507:  PackageDeclarationopt ::= PackageDeclaration
            //
            case 507:
                break;
 
            //
            // Rule 508:  WildcardBoundsOpt ::=
            //
            case 508:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 509:  WildcardBoundsOpt ::= WildcardBounds
            //
            case 509:
                System.err.println("Rule " + 509 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 510:  AdditionalBoundListopt ::=
            //
            case 510:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 511:  AdditionalBoundListopt ::= AdditionalBoundList
            //
            case 511:
                System.err.println("Rule " + 511 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 512:  TypeBoundopt ::=
            //
            case 512:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 513:  TypeBoundopt ::= TypeBound
            //
            case 513:
                System.err.println("Rule " + 513 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 514:  TypeArgumentsopt ::=
            //
            case 514:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 515:  TypeArgumentsopt ::= TypeArguments
            //
            case 515:
                System.err.println("Rule " + 515 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 516:  Type ::= DataType PlaceTypeSpecifieropt
            //
            case 516:
                System.err.println("Rule " + 516 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 517:  Type ::= nullable LESS Type GREATER
            //
            case 517:
                System.err.println("Rule " + 517 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 518:  Type ::= future LESS Type GREATER
            //
            case 518:
                System.err.println("Rule " + 518 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 519:  DataType ::= ClassOrInterfaceType
            //
            case 519:
                System.err.println("Rule " + 519 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 520:  DataType ::= ArrayType
            //
            case 520:
                System.err.println("Rule " + 520 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 521:  DataType ::= PrimitiveType
            //
            case 521:
                System.err.println("Rule " + 521 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 522:  PlaceTypeSpecifier ::= AT PlaceType
            //
            case 522:
                System.err.println("Rule " + 522 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 523:  PlaceType ::= place
            //
            case 523:
                System.err.println("Rule " + 523 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 524:  PlaceType ::= activity
            //
            case 524:
                System.err.println("Rule " + 524 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 525:  PlaceType ::= method
            //
            case 525:
                System.err.println("Rule " + 525 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 526:  PlaceType ::= current
            //
            case 526:
                System.err.println("Rule " + 526 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 527:  PlaceType ::= PlaceExpression
            //
            case 527:
                System.err.println("Rule " + 527 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 528:  ClassOrInterfaceType ::= TypeName DepParameters
            //
            case 528:
                System.err.println("Rule " + 528 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 529:  DepParameters ::= LPAREN DepParameterExpr RPAREN
            //
            case 529:
                System.err.println("Rule " + 529 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 530:  DepParameterExpr ::= ArgumentList WhereClauseopt
            //
            case 530:
                System.err.println("Rule " + 530 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 531:  DepParameterExpr ::= WhereClause
            //
            case 531:
                System.err.println("Rule " + 531 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 532:  WhereClause ::= COLON Expression
            //
            case 532:
                System.err.println("Rule " + 532 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 533:  ArrayType ::= Type ObjectKind LBRACKET RBRACKET
            //
            case 533:
                System.err.println("Rule " + 533 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 534:  ArrayType ::= Type LBRACKET DistributionType RBRACKET
            //
            case 534:
                System.err.println("Rule " + 534 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 535:  ArrayType ::= Type ObjectKind LBRACKET DistributionType RBRACKET
            //
            case 535:
                System.err.println("Rule " + 535 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 536:  ObjectKind ::= value
            //
            case 536:
                System.err.println("Rule " + 536 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 537:  ObjectKind ::= reference
            //
            case 537:
                System.err.println("Rule " + 537 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 538:  DistributionType ::= Expression
            //
            case 538:
                System.err.println("Rule " + 538 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 539:  DistributionType ::= WhereClause
            //
            case 539:
                System.err.println("Rule " + 539 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 540:  MethodModifier ::= atomic
            //
            case 540:
                System.err.println("Rule " + 540 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 541:  MethodModifier ::= extern
            //
            case 541:
                System.err.println("Rule " + 541 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 542:  ClassDeclaration ::= ValueClassDeclaration
            //
            case 542:
                System.err.println("Rule " + 542 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 543:  ValueClassDeclaration ::= ClassModifiersopt value identifier Superopt Interfacesopt ClassBody
            //
            case 543:
                System.err.println("Rule " + 543 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 544:  VariableDeclaratorId ::= LBRACKET IdentifierList RBRACKET
            //
            case 544:
                System.err.println("Rule " + 544 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 545:  IdentifierList ::= IdentifierList COMMA identifier
            //
            case 545:
                System.err.println("Rule " + 545 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 546:  IdentifierList ::= identifier
            //
            case 546:
                System.err.println("Rule " + 546 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 547:  ArrayCreationExpression ::= new ArrayConcreteType ArrayInitializeropt
            //
            case 547:
                System.err.println("Rule " + 547 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 548:  ArrayConcreteType ::= Type ObjectKind LBRACKET RBRACKET
            //
            case 548:
                System.err.println("Rule " + 548 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 549:  ArrayConcreteType ::= Type LBRACKET ConcreteDistribution RBRACKET
            //
            case 549:
                System.err.println("Rule " + 549 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 550:  ArrayConcreteType ::= Type ObjectKind LBRACKET ConcreteDistribution RBRACKET
            //
            case 550:
                System.err.println("Rule " + 550 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 551:  ConcreteDistribution ::= Expression
            //
            case 551:
                System.err.println("Rule " + 551 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 552:  ArrayInitializer ::= COLON Expression
            //
            case 552:
                System.err.println("Rule " + 552 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 553:  ArrayAccess ::= ExpressionName LBRACKET Expression COMMA ArgumentList RBRACKET
            //
            case 553:
                System.err.println("Rule " + 553 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 554:  ArrayAccess ::= PrimaryNoNewArray LBRACKET Expression COMMA ArgumentList RBRACKET
            //
            case 554:
                System.err.println("Rule " + 554 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 555:  Statement ::= NowStatement
            //
            case 555:
                System.err.println("Rule " + 555 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 556:  Statement ::= ClockedStatement
            //
            case 556:
                System.err.println("Rule " + 556 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 557:  Statement ::= AsyncStatement
            //
            case 557:
                System.err.println("Rule " + 557 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 558:  Statement ::= AtomicStatement
            //
            case 558:
                System.err.println("Rule " + 558 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 559:  Statement ::= WhenStatement
            //
            case 559:
                System.err.println("Rule " + 559 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 560:  Statement ::= ForEachStatement
            //
            case 560:
                System.err.println("Rule " + 560 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 561:  Statement ::= AtEachStatement
            //
            case 561:
                System.err.println("Rule " + 561 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 562:  Statement ::= FinishStatement
            //
            case 562:
                System.err.println("Rule " + 562 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 563:  StatementWithoutTrailingSubstatement ::= NextStatement
            //
            case 563:
                System.err.println("Rule " + 563 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 564:  StatementWithoutTrailingSubstatement ::= AwaitStatement
            //
            case 564:
                System.err.println("Rule " + 564 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 565:  StatementNoShortIf ::= NowStatementNoShortIf
            //
            case 565:
                System.err.println("Rule " + 565 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 566:  StatementNoShortIf ::= ClockedStatementNoShortIf
            //
            case 566:
                System.err.println("Rule " + 566 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 567:  StatementNoShortIf ::= AsyncStatementNoShortIf
            //
            case 567:
                System.err.println("Rule " + 567 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 568:  StatementNoShortIf ::= AtomicStatementNoShortIf
            //
            case 568:
                System.err.println("Rule " + 568 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 569:  StatementNoShortIf ::= WhenStatementNoShortIf
            //
            case 569:
                System.err.println("Rule " + 569 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 570:  StatementNoShortIf ::= ForEachStatementNoShortIf
            //
            case 570:
                System.err.println("Rule " + 570 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 571:  StatementNoShortIf ::= AtEachStatementNoShortIf
            //
            case 571:
                System.err.println("Rule " + 571 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 572:  StatementNoShortIf ::= FinishStatementNoShortIf
            //
            case 572:
                System.err.println("Rule " + 572 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 573:  NowStatement ::= now LPAREN Clock RPAREN Statement
            //
            case 573:
                System.err.println("Rule " + 573 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 574:  ClockedStatement ::= clocked LPAREN ClockList RPAREN Statement
            //
            case 574:
                System.err.println("Rule " + 574 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 575:  AsyncStatement ::= async PlaceExpressionSingleListopt Statement
            //
            case 575:
                System.err.println("Rule " + 575 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 576:  AtomicStatement ::= atomic Statement
            //
            case 576:
                System.err.println("Rule " + 576 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 577:  WhenStatement ::= when LPAREN Expression RPAREN Statement
            //
            case 577:
                System.err.println("Rule " + 577 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 578:  WhenStatement ::= WhenStatement or LPAREN Expression RPAREN Statement
            //
            case 578:
                System.err.println("Rule " + 578 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 579:  ForEachStatement ::= foreach LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 579:
                System.err.println("Rule " + 579 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 580:  AtEachStatement ::= ateach LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 580:
                System.err.println("Rule " + 580 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 581:  FinishStatement ::= finish Statement
            //
            case 581:
                System.err.println("Rule " + 581 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 582:  NowStatementNoShortIf ::= now LPAREN Clock RPAREN StatementNoShortIf
            //
            case 582:
                System.err.println("Rule " + 582 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 583:  ClockedStatementNoShortIf ::= clocked LPAREN ClockList RPAREN StatementNoShortIf
            //
            case 583:
                System.err.println("Rule " + 583 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 584:  AsyncStatementNoShortIf ::= async PlaceExpressionSingleListopt StatementNoShortIf
            //
            case 584:
                System.err.println("Rule " + 584 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 585:  AtomicStatementNoShortIf ::= atomic StatementNoShortIf
            //
            case 585:
                System.err.println("Rule " + 585 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 586:  WhenStatementNoShortIf ::= when LPAREN Expression RPAREN StatementNoShortIf
            //
            case 586:
                System.err.println("Rule " + 586 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 587:  WhenStatementNoShortIf ::= WhenStatement or LPAREN Expression RPAREN StatementNoShortIf
            //
            case 587:
                System.err.println("Rule " + 587 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 588:  ForEachStatementNoShortIf ::= foreach LPAREN FormalParameter COLON Expression RPAREN StatementNoShortIf
            //
            case 588:
                System.err.println("Rule " + 588 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 589:  AtEachStatementNoShortIf ::= ateach LPAREN FormalParameter COLON Expression RPAREN StatementNoShortIf
            //
            case 589:
                System.err.println("Rule " + 589 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 590:  FinishStatementNoShortIf ::= finish StatementNoShortIf
            //
            case 590:
                System.err.println("Rule " + 590 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 591:  PlaceExpressionSingleList ::= LPAREN PlaceExpression RPAREN
            //
            case 591:
                System.err.println("Rule " + 591 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 592:  PlaceExpression ::= ExpressionName
            //
            case 592:
                System.err.println("Rule " + 592 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 593:  PlaceExpression ::= ArrayAccess
            //
            case 593:
                System.err.println("Rule " + 593 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 594:  PlaceExpression ::= here
            //
            case 594:
                System.err.println("Rule " + 594 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 595:  NextStatement ::= next SEMICOLON
            //
            case 595:
                System.err.println("Rule " + 595 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 596:  AwaitStatement ::= await Expression SEMICOLON
            //
            case 596:
                System.err.println("Rule " + 596 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 597:  ClockList ::= Clock
            //
            case 597:
                System.err.println("Rule " + 597 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 598:  ClockList ::= ClockList COMMA Clock
            //
            case 598:
                System.err.println("Rule " + 598 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 599:  Clock ::= identifier
            //
            case 599:
                System.err.println("Rule " + 599 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 600:  CastExpression ::= LPAREN Type RPAREN UnaryExpressionNotPlusMinus
            //
            case 600:
                System.err.println("Rule " + 600 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 601:  MethodInvocation ::= Primary ARROW identifier LPAREN ArgumentListopt RPAREN
            //
            case 601:
                System.err.println("Rule " + 601 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 603:  Primary ::= FutureExpression
            //
            case 603:
                System.err.println("Rule " + 603 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 604:  FutureExpression ::= future PlaceExpressionSingleListopt LBRACE Expression RBRACE
            //
            case 604:
                System.err.println("Rule " + 604 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 605:  FunExpression ::= fun Type LPAREN FormalParameterListopt RPAREN LBRACE Expression RBRACE
            //
            case 605:
                System.err.println("Rule " + 605 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 606:  MethodInvocation ::= MethodName LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 606:
                System.err.println("Rule " + 606 + " not yet implemented");
                actions_stopped = true;
                break;
     
            //
            // Rule 607:  MethodInvocation ::= Primary DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 607:
                System.err.println("Rule " + 607 + " not yet implemented");
                actions_stopped = true;
                break;
     
            //
            // Rule 608:  MethodInvocation ::= super DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 608:
                System.err.println("Rule " + 608 + " not yet implemented");
                actions_stopped = true;
                break;
     
            //
            // Rule 609:  MethodInvocation ::= ClassName DOT super DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 609:
                System.err.println("Rule " + 609 + " not yet implemented");
                actions_stopped = true;
                break;
     
            //
            // Rule 610:  MethodInvocation ::= TypeName DOT identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN
            //
            case 610:
                System.err.println("Rule " + 610 + " not yet implemented");
                actions_stopped = true;
                break;
     
            //
            // Rule 611:  ClassInstanceCreationExpression ::= new ClassOrInterfaceType LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 611:
                System.err.println("Rule " + 611 + " not yet implemented");
                actions_stopped = true;
                break;
     
            //
            // Rule 612:  ClassInstanceCreationExpression ::= Primary DOT new identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 612:
                System.err.println("Rule " + 612 + " not yet implemented");
                actions_stopped = true;
                break;
     
            //
            // Rule 613:  ClassInstanceCreationExpression ::= AmbiguousName DOT new identifier LPAREN ArgumentListopt RPAREN LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 613:
                System.err.println("Rule " + 613 + " not yet implemented");
                actions_stopped = true;
                break;
     
            //
            // Rule 614:  PlaceTypeSpecifieropt ::=
            //
            case 614:
                break; 
 
            //
            // Rule 615:  PlaceTypeSpecifieropt ::= PlaceTypeSpecifier
            //
            case 615:
                System.err.println("Rule " + 615 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 616:  DepParametersopt ::=
            //
            case 616:
                break; 
 
            //
            // Rule 617:  DepParametersopt ::= DepParameters
            //
            case 617:
                System.err.println("Rule " + 617 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 618:  WhereClauseopt ::=
            //
            case 618:
                break; 
 
            //
            // Rule 619:  WhereClauseopt ::= WhereClause
            //
            case 619:
                System.err.println("Rule " + 619 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 620:  ObjectKindopt ::=
            //
            case 620:
                break; 
 
            //
            // Rule 621:  ObjectKindopt ::= ObjectKind
            //
            case 621:
                System.err.println("Rule " + 621 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 622:  DistributionTypeopt ::=
            //
            case 622:
                break; 
 
            //
            // Rule 623:  DistributionTypeopt ::= DistributionType
            //
            case 623:
                System.err.println("Rule " + 623 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 624:  ArrayInitializeropt ::=
            //
            case 624:
                break; 
 
            //
            // Rule 625:  ArrayInitializeropt ::= ArrayInitializer
            //
            case 625:
                System.err.println("Rule " + 625 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 626:  ConcreteDistributionopt ::=
            //
            case 626:
                break; 
 
            //
            // Rule 627:  ConcreteDistributionopt ::= ConcreteDistribution
            //
            case 627:
                System.err.println("Rule " + 627 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 628:  PlaceExpressionSingleListopt ::=
            //
            case 628:
                break; 
 
            //
            // Rule 629:  PlaceExpressionSingleListopt ::= PlaceExpressionSingleList
            //
            case 629:
                System.err.println("Rule " + 629 + " not yet implemented");
                actions_stopped = true;
                break; 
 
            //
            // Rule 630:  ArgumentListopt ::=
            //
            case 630:
                break; 
 
            //
            // Rule 631:  ArgumentListopt ::= ArgumentList
            //
            case 631:
                System.err.println("Rule " + 631 + " not yet implemented");
                actions_stopped = true;
                break; 
    
            default:
                break;
        }
        return;
    }
}

