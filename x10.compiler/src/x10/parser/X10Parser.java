
//
// This is the grammar specification from the Final Draft of the generic spec.
////
// This is the X10 grammar specification based on the Final Draft of the Java generic spec.
//

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
            // Rule 14:  ReferenceType ::= TypeVariable
            //
            case 14:
                break;
 
            //
            // Rule 15:  ReferenceType ::= ArrayType
            //
            case 15:
                break;
 
            //
            // Rule 16:  ClassOrInterfaceType ::= ClassType
            //
            case 16:
                break;
 
            //
            // Rule 17:  ClassType ::= TypeName TypeArgumentsopt
            //
            case 17: {
                assert(btParser.getSym(2) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toType());
                break;
            }
     
            //
            // Rule 18:  InterfaceType ::= TypeName TypeArgumentsopt
            //
            case 18: {
                assert(btParser.getSym(2) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toType());
                break;
            }
     
            //
            // Rule 19:  TypeName ::= identifier
            //
            case 19: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 20:  TypeName ::= TypeName DOT identifier
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
            // Rule 21:  ClassName ::= TypeName
            //
            case 21:
                break;
 
            //
            // Rule 22:  TypeVariable ::= identifier
            //
            case 22:
                break;
 
            //
            // Rule 23:  ArrayType ::= Type LBRACKET RBRACKET
            //
            case 23: {
                TypeNode a = (TypeNode) btParser.getSym(1);
                btParser.setSym1(array(a, pos(), 1));
                break;
            }
     
            //
            // Rule 24:  TypeParameter ::= TypeVariable TypeBoundopt
            //
            case 24:
                System.err.println("Rule " + 24 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 25:  TypeBound ::= extends ClassOrInterfaceType AdditionalBoundListopt
            //
            case 25:
                System.err.println("Rule " + 25 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 26:  AdditionalBoundList ::= AdditionalBound
            //
            case 26:
                System.err.println("Rule " + 26 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 27:  AdditionalBoundList ::= AdditionalBoundList AdditionalBound
            //
            case 27:
                System.err.println("Rule " + 27 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 28:  AdditionalBound ::= AND InterfaceType
            //
            case 28:
                System.err.println("Rule " + 28 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 29:  TypeArguments ::= LESS ActualTypeArgumentList GREATER
            //
            case 29:
                System.err.println("Rule " + 29 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 30:  ActualTypeArgumentList ::= ActualTypeArgument
            //
            case 30:
                System.err.println("Rule " + 30 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 31:  ActualTypeArgumentList ::= ActualTypeArgumentList COMMA ActualTypeArgument
            //
            case 31:
                System.err.println("Rule " + 31 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 32:  Wildcard ::= QUESTION WildcardBoundsOpt
            //
            case 32:
                System.err.println("Rule " + 32 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 33:  WildcardBounds ::= extends ReferenceType
            //
            case 33:
                System.err.println("Rule " + 33 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 34:  WildcardBounds ::= super ReferenceType
            //
            case 34:
                System.err.println("Rule " + 34 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 35:  PackageName ::= identifier
            //
            case 35: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 36:  PackageName ::= PackageName DOT identifier
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
            // Rule 37:  ExpressionName ::= identifier
            //
            case 37: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 38:  ExpressionName ::= AmbiguousName DOT identifier
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
            // Rule 39:  MethodName ::= identifier
            //
            case 39: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 40:  MethodName ::= AmbiguousName DOT identifier
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
            // Rule 41:  PackageOrTypeName ::= identifier
            //
            case 41: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 42:  PackageOrTypeName ::= PackageOrTypeName DOT identifier
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
            // Rule 43:  AmbiguousName ::= identifier
            //
            case 43: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 44:  AmbiguousName ::= AmbiguousName DOT identifier
            //
            case 44: {
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
            // Rule 45:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 45: {
                PackageNode a = (PackageNode) btParser.getSym(1);
                List b = (List) btParser.getSym(2),
                     c = (List) btParser.getSym(3);
                btParser.setSym1(nf.SourceFile(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b, c));
                break;
            }
     
            //
            // Rule 46:  ImportDeclarations ::= ImportDeclaration
            //
            case 46: {
                List l = new TypedList(new LinkedList(), Import.class, false);
                Import a = (Import) btParser.getSym(1);
                l.add(a);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 47:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
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
            // Rule 48:  TypeDeclarations ::= TypeDeclaration
            //
            case 48: {
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                TopLevelDecl a = (TopLevelDecl) btParser.getSym(1);
                if (a != null)
                    l.add(a);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 49:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 49: {
                List l = (TypedList) btParser.getSym(1);
                TopLevelDecl b = (TopLevelDecl) btParser.getSym(2);
                if (b != null)
                    l.add(b);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 50:  PackageDeclaration ::= Annotationsopt package PackageName SEMICOLON
            //
            case 50: {
                assert(btParser.getSym(1) == null); // generic not yet supported
                Name a = (Name) btParser.getSym(3);
                btParser.setSym1(a.toPackage());
                break;
            }
     
            //
            // Rule 51:  ImportDeclaration ::= SingleTypeImportDeclaration
            //
            case 51:
                break;
 
            //
            // Rule 52:  ImportDeclaration ::= TypeImportOnDemandDeclaration
            //
            case 52:
                break;
 
            //
            // Rule 53:  ImportDeclaration ::= SingleStaticImportDeclaration
            //
            case 53:
                break;
 
            //
            // Rule 54:  ImportDeclaration ::= StaticImportOnDemandDeclaration
            //
            case 54:
                break;
 
            //
            // Rule 55:  SingleTypeImportDeclaration ::= import TypeName SEMICOLON
            //
            case 55: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.CLASS, a.toString()));
                break;
            }
     
            //
            // Rule 56:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName DOT MULTIPLY SEMICOLON
            //
            case 56: {
                Name a = (Name) btParser.getSym(2);
                btParser.setSym1(nf.Import(pos(btParser.getFirstToken(), btParser.getLastToken()), Import.PACKAGE, a.toString()));
                break;
            }
     
            //
            // Rule 57:  SingleStaticImportDeclaration ::= import static TypeName DOT identifier SEMICOLON
            //
            case 57:
                System.err.println("Rule " + 57 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 58:  StaticImportOnDemandDeclaration ::= import static TypeName DOT MULTIPLY SEMICOLON
            //
            case 58:
                System.err.println("Rule " + 58 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 59:  TypeDeclaration ::= ClassDeclaration
            //
            case 59:
                break;
 
            //
            // Rule 60:  TypeDeclaration ::= InterfaceDeclaration
            //
            case 60:
                break;
 
            //
            // Rule 61:  TypeDeclaration ::= SEMICOLON
            //
            case 61: {
                btParser.setSym1(null);
                break;
            }
     
            //
            // Rule 62:  ClassDeclaration ::= NormalClassDeclaration
            //
            case 62:
                break;
 
            //
            // Rule 63:  ClassDeclaration ::= EnumDeclaration
            //
            case 63:
                System.err.println("Rule " + 63 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 64:  NormalClassDeclaration ::= ClassModifiersopt class identifier TypeParametersopt Superopt Interfacesopt ClassBody
            //
            case 64: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                assert(btParser.getSym(4) == null);
                TypeNode c = (TypeNode) btParser.getSym(5);
                List d = (List) btParser.getSym(6);
                ClassBody e = (ClassBody) btParser.getSym(7);
                if (a.isValue())
                     btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
                else btParser.setSym1(nf.ClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
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
            // Rule 67:  ClassModifier ::= Annotation
            //
            case 67:
                System.err.println("Rule " + 67 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 68:  ClassModifier ::= public
            //
            case 68: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 69:  ClassModifier ::= protected
            //
            case 69: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 70:  ClassModifier ::= private
            //
            case 70: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 71:  ClassModifier ::= abstract
            //
            case 71: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 72:  ClassModifier ::= static
            //
            case 72: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 73:  ClassModifier ::= final
            //
            case 73: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 74:  ClassModifier ::= strictfp
            //
            case 74: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 75:  TypeParameters ::= LESS TypeParameterList GREATER
            //
            case 75:
                System.err.println("Rule " + 75 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 76:  TypeParameterList ::= TypeParameter
            //
            case 76:
                System.err.println("Rule " + 76 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 77:  TypeParameterList ::= TypeParameterList COMMA TypeParameter
            //
            case 77:
                System.err.println("Rule " + 77 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 78:  Super ::= extends ClassType
            //
            case 78: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 79:  Interfaces ::= implements InterfaceTypeList
            //
            case 79: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 80:  InterfaceTypeList ::= InterfaceType
            //
            case 80: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 81:  InterfaceTypeList ::= InterfaceTypeList COMMA InterfaceType
            //
            case 81: {
                List l = (TypedList) btParser.getSym(1);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 82:  ClassBody ::= LBRACE ClassBodyDeclarationsopt RBRACE
            //
            case 82: {
                btParser.setSym1(nf.ClassBody(pos(btParser.getFirstToken(), btParser.getLastToken()), (List) btParser.getSym(2)));
                break;
            }
     
            //
            // Rule 83:  ClassBodyDeclarations ::= ClassBodyDeclaration
            //
            case 83:
                break;
 
            //
            // Rule 84:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 84: {
                List a = (List) btParser.getSym(1),
                     b = (List) btParser.getSym(2);
                a.addAll(b);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 85:  ClassBodyDeclaration ::= ClassMemberDeclaration
            //
            case 85:
                break;
 
            //
            // Rule 86:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 86: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Block a = (Block) btParser.getSym(1);
                l.add(nf.Initializer(pos(), Flags.NONE, a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 87:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 87: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                Block a = (Block) btParser.getSym(1);
                l.add(nf.Initializer(pos(), Flags.STATIC, a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 88:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 88: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 89:  ClassMemberDeclaration ::= FieldDeclaration
            //
            case 89:
                break;
 
            //
            // Rule 90:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 90: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 91:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 91: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 92:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 92: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 93:  ClassMemberDeclaration ::= SEMICOLON
            //
            case 93: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 94:  FieldDeclaration ::= FieldModifiersopt Type VariableDeclarators SEMICOLON
            //
            case 94: {
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
            // Rule 95:  VariableDeclarators ::= VariableDeclarator
            //
            case 95: {
                List l = new TypedList(new LinkedList(), VarDeclarator.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 96:  VariableDeclarators ::= VariableDeclarators COMMA VariableDeclarator
            //
            case 96: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 97:  VariableDeclarator ::= VariableDeclaratorId
            //
            case 97:
                break;
 
            //
            // Rule 98:  VariableDeclarator ::= VariableDeclaratorId EQUAL VariableInitializer
            //
            case 98: {
                VarDeclarator a = (VarDeclarator) btParser.getSym(1);
                Expr b = (Expr) btParser.getSym(3);
                a.init = b;
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 99:  VariableDeclaratorId ::= identifier
            //
            case 99: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new VarDeclarator(pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 100:  VariableDeclaratorId ::= VariableDeclaratorId LBRACKET RBRACKET
            //
            case 100: {
                VarDeclarator a = (VarDeclarator) btParser.getSym(1);
                a.dims++;
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 101:  VariableInitializer ::= Expression
            //
            case 101:
                break;
 
            //
            // Rule 102:  VariableInitializer ::= ArrayInitializer
            //
            case 102:
                break;
 
            //
            // Rule 103:  FieldModifiers ::= FieldModifier
            //
            case 103:
                break;
 
            //
            // Rule 104:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 104: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 105:  FieldModifier ::= Annotation
            //
            case 105:
                System.err.println("Rule " + 105 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 106:  FieldModifier ::= public
            //
            case 106: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 107:  FieldModifier ::= protected
            //
            case 107: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 108:  FieldModifier ::= private
            //
            case 108: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 109:  FieldModifier ::= static
            //
            case 109: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 110:  FieldModifier ::= final
            //
            case 110: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 111:  FieldModifier ::= transient
            //
            case 111: {
                btParser.setSym1(Flags.TRANSIENT);
                break;
            }
     
            //
            // Rule 112:  FieldModifier ::= volatile
            //
            case 112: {
                btParser.setSym1(Flags.VOLATILE);
                break;
            }
     
            //
            // Rule 113:  MethodDeclaration ::= MethodHeader MethodBody
            //
            case 113: {
                MethodDecl a = (MethodDecl) btParser.getSym(1);
                Block b = (Block) btParser.getSym(2);
                btParser.setSym1(a.body(b));
                break;
            }
     
            //
            // Rule 114:  MethodHeader ::= MethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt
            //
            case 114: {
                Flags a = (Flags) btParser.getSym(1);
                assert(btParser.getSym(2) == null);
                TypeNode b = (TypeNode) btParser.getSym(3);
                Object[] o = (Object []) btParser.getSym(4);
                    Name c = (Name) o[0];
                    List d = (List) o[1];
                    Integer e = (Integer) o[2];
                List f = (List) btParser.getSym(5);

                if (b.type() == ts.Void() && e.intValue() > 0)
                {
                    // TODO: error!!!
                }

                btParser.setSym1(nf.MethodDecl(pos(btParser.getFirstToken(3), btParser.getLastToken(4)),
                                       a,
                                       array((TypeNode) b, pos(btParser.getFirstToken(3), btParser.getLastToken(3)), e.intValue()),
                                       c.toString(),
                                       d,
                                       f,
                                       null));
                break;
            }
     
            //
            // Rule 115:  ResultType ::= Type
            //
            case 115:
                break;
 
            //
            // Rule 116:  ResultType ::= void
            //
            case 116: {
                btParser.setSym1(nf.CanonicalTypeNode(pos(), ts.Void()));
                break;
            }
     
            //
            // Rule 117:  MethodDeclarator ::= identifier LPAREN FormalParameterListopt RPAREN
            //
            case 117: {
                Object[] a = new Object[3];
                a[0] =  new Name(nf, ts, pos(), id(btParser.getToken(1)).getIdentifier());
                a[1] = btParser.getSym(3);
                a[2] = new Integer(0);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 118:  MethodDeclarator ::= MethodDeclarator LBRACKET RBRACKET
            //
            case 118: {
                Object[] a = (Object []) btParser.getSym(1);
                a[2] = new Integer(((Integer) a[2]).intValue() + 1);
                // btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 119:  FormalParameterList ::= LastFormalParameter
            //
            case 119: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 120:  FormalParameterList ::= FormalParameters COMMA LastFormalParameter
            //
            case 120: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 121:  FormalParameters ::= FormalParameter
            //
            case 121: {
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 122:  FormalParameters ::= FormalParameters COMMA FormalParameter
            //
            case 122: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 123:  FormalParameter ::= VariableModifiersopt Type VariableDeclaratorId
            //
            case 123: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                VarDeclarator b = (VarDeclarator) btParser.getSym(3);
                btParser.setSym1(nf.Formal(pos(), f, array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b.name));
                break;
            }
     
            //
            // Rule 125:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 125: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 126:  VariableModifier ::= final
            //
            case 126: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 127:  VariableModifier ::= Annotations
            //
            case 127:
                System.err.println("Rule " + 127 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 128:  LastFormalParameter ::= VariableModifiersopt Type ...opt VariableDeclaratorId
            //
            case 128: {
                Flags f = (Flags) btParser.getSym(1);
                TypeNode a = (TypeNode) btParser.getSym(2);
                assert(btParser.getSym(3) == null);
                VarDeclarator b = (VarDeclarator) btParser.getSym(4);
                btParser.setSym1(nf.Formal(pos(), f, array(a, pos(btParser.getFirstToken(2), btParser.getLastToken(2)), b.dims), b.name));
                break;
            }
     
            //
            // Rule 129:  MethodModifiers ::= MethodModifier
            //
            case 129:
                break;
 
            //
            // Rule 130:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 130: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 131:  MethodModifier ::= Annotations
            //
            case 131:
                System.err.println("Rule " + 131 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 132:  MethodModifier ::= public
            //
            case 132: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 133:  MethodModifier ::= protected
            //
            case 133: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 134:  MethodModifier ::= private
            //
            case 134: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 135:  MethodModifier ::= abstract
            //
            case 135: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 136:  MethodModifier ::= static
            //
            case 136: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 137:  MethodModifier ::= final
            //
            case 137: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 138:  MethodModifier ::= synchronized
            //
            case 138: {
                btParser.setSym1(Flags.SYNCHRONIZED);
                break;
            }
     
            //
            // Rule 139:  MethodModifier ::= native
            //
            case 139: {
                btParser.setSym1(Flags.NATIVE);
                break;
            }
     
            //
            // Rule 140:  MethodModifier ::= strictfp
            //
            case 140: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 141:  Throws ::= throws ExceptionTypeList
            //
            case 141: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 142:  ExceptionTypeList ::= ExceptionType
            //
            case 142: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 143:  ExceptionTypeList ::= ExceptionTypeList COMMA ExceptionType
            //
            case 143: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 144:  ExceptionType ::= ClassType
            //
            case 144:
                break;
 
            //
            // Rule 145:  ExceptionType ::= TypeVariable
            //
            case 145:
                break;
 
            //
            // Rule 146:  MethodBody ::= Block
            //
            case 146:
                break;
 
            //
            // Rule 147:  MethodBody ::= SEMICOLON
            //
            case 147:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 148:  InstanceInitializer ::= Block
            //
            case 148:
                break;
 
            //
            // Rule 149:  StaticInitializer ::= static Block
            //
            case 149: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 150:  ConstructorDeclaration ::= ConstructorModifiersopt ConstructorDeclarator Throwsopt ConstructorBody
            //
            case 150: {
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
            // Rule 151:  ConstructorDeclarator ::= TypeParametersopt SimpleTypeName LPAREN FormalParameterListopt RPAREN
            //
            case 151: {
                assert(btParser.getSym(1) == null);
                Object[] a = new Object[3];
                a[0] = btParser.getSym(1);
                a[1] = btParser.getSym(2);
                a[2] = btParser.getSym(4);
                btParser.setSym1(a);
                break;
            }
     
            //
            // Rule 152:  SimpleTypeName ::= identifier
            //
            case 152: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 153:  ConstructorModifiers ::= ConstructorModifier
            //
            case 153:
                break;
 
            //
            // Rule 154:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 154: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 155:  ConstructorModifier ::= Annotations
            //
            case 155:
                System.err.println("Rule " + 155 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 156:  ConstructorModifier ::= public
            //
            case 156: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 157:  ConstructorModifier ::= protected
            //
            case 157: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 158:  ConstructorModifier ::= private
            //
            case 158: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 159:  ConstructorBody ::= LBRACE ExplicitConstructorInvocationopt BlockStatementsopt RBRACE
            //
            case 159: {
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
            // Rule 160:  ExplicitConstructorInvocation ::= TypeArgumentsopt this LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 160: {
                assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(4);
                btParser.setSym1(nf.ThisCall(pos(), b));
                break;
            }
     
            //
            // Rule 161:  ExplicitConstructorInvocation ::= TypeArgumentsopt super LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 161: {
                assert(btParser.getSym(1) == null);
                List b = (List) btParser.getSym(4);
                btParser.setSym1(nf.SuperCall(pos(), b));
                break;
            }
     
            //
            // Rule 162:  ExplicitConstructorInvocation ::= Primary DOT TypeArgumentsopt this LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 162: {
                Expr a = (Expr) btParser.getSym(1);
                assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(4);
                btParser.setSym1(nf.ThisCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 163:  ExplicitConstructorInvocation ::= Primary DOT TypeArgumentsopt super LPAREN ArgumentListopt RPAREN SEMICOLON
            //
            case 163: {
                Expr a = (Expr) btParser.getSym(1);
                assert(btParser.getSym(2) == null);
                List b = (List) btParser.getSym(4);
                btParser.setSym1(nf.SuperCall(pos(), a, b));
                break;
            }
     
            //
            // Rule 164:  EnumDeclaration ::= ClassModifiersopt enum identifier Interfacesopt EnumBody
            //
            case 164:
                System.err.println("Rule " + 164 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 165:  EnumBody ::= LBRACE EnumConstantsopt ,opt EnumBodyDeclarationsopt RBRACE
            //
            case 165:
                System.err.println("Rule " + 165 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 166:  EnumConstants ::= EnumConstant
            //
            case 166:
                System.err.println("Rule " + 166 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 167:  EnumConstants ::= EnumConstants COMMA EnumConstant
            //
            case 167:
                System.err.println("Rule " + 167 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 168:  EnumConstant ::= Annotationsopt identifier Argumentsopt ClassBodyopt
            //
            case 168:
                System.err.println("Rule " + 168 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 169:  Arguments ::= LPAREN ArgumentListopt RPAREN
            //
            case 169: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 170:  EnumBodyDeclarations ::= SEMICOLON ClassBodyDeclarationsopt
            //
            case 170:
                System.err.println("Rule " + 170 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 171:  InterfaceDeclaration ::= NormalInterfaceDeclaration
            //
            case 171:
                break;
 
            //
            // Rule 172:  InterfaceDeclaration ::= AnnotationTypeDeclaration
            //
            case 172:
                System.err.println("Rule " + 172 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 173:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface identifier TypeParametersopt ExtendsInterfacesopt InterfaceBody
            //
            case 173: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                assert(btParser.getSym(4) == null);
                List c = (List) btParser.getSym(5);
                ClassBody d = (ClassBody) btParser.getSym(6);
                btParser.setSym1(nf.ClassDecl(pos(), a.Interface(), b.getIdentifier(), null, c, d));
                break;
            }
     
            //
            // Rule 174:  InterfaceModifiers ::= InterfaceModifier
            //
            case 174:
                break;
 
            //
            // Rule 175:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 175: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 176:  InterfaceModifier ::= Annotation
            //
            case 176:
                System.err.println("Rule " + 176 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 177:  InterfaceModifier ::= public
            //
            case 177: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 178:  InterfaceModifier ::= protected
            //
            case 178: {
                btParser.setSym1(Flags.PROTECTED);
                break;
            }
     
            //
            // Rule 179:  InterfaceModifier ::= private
            //
            case 179: {
                btParser.setSym1(Flags.PRIVATE);
                break;
            }
     
            //
            // Rule 180:  InterfaceModifier ::= abstract
            //
            case 180: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 181:  InterfaceModifier ::= static
            //
            case 181: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 182:  InterfaceModifier ::= strictfp
            //
            case 182: {
                btParser.setSym1(Flags.STRICTFP);
                break;
            }
     
            //
            // Rule 183:  ExtendsInterfaces ::= extends InterfaceType
            //
            case 183: {
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(btParser.getSym(2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 184:  ExtendsInterfaces ::= ExtendsInterfaces COMMA InterfaceType
            //
            case 184: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 185:  InterfaceBody ::= LBRACE InterfaceMemberDeclarationsopt RBRACE
            //
            case 185: {
                List a = (List)btParser.getSym(2);
                btParser.setSym1(nf.ClassBody(pos(), a));
                break;
            }
     
            //
            // Rule 186:  InterfaceMemberDeclarations ::= InterfaceMemberDeclaration
            //
            case 186:
                break;
 
            //
            // Rule 187:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 187: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 188:  InterfaceMemberDeclaration ::= ConstantDeclaration
            //
            case 188:
                break;
 
            //
            // Rule 189:  InterfaceMemberDeclaration ::= AbstractMethodDeclaration
            //
            case 189: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 190:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 190: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 191:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 191: {
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 192:  InterfaceMemberDeclaration ::= SEMICOLON
            //
            case 192: {
                btParser.setSym1(Collections.EMPTY_LIST);
                break;
            }
     
            //
            // Rule 193:  ConstantDeclaration ::= ConstantModifiersopt Type VariableDeclarators
            //
            case 193: {
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
            // Rule 194:  ConstantModifiers ::= ConstantModifier
            //
            case 194:
                break;
 
            //
            // Rule 195:  ConstantModifiers ::= ConstantModifiers ConstantModifier
            //
            case 195: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 196:  ConstantModifier ::= Annotation
            //
            case 196:
                System.err.println("Rule " + 196 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 197:  ConstantModifier ::= public
            //
            case 197: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 198:  ConstantModifier ::= static
            //
            case 198: {
                btParser.setSym1(Flags.STATIC);
                break;
            }
     
            //
            // Rule 199:  ConstantModifier ::= final
            //
            case 199: {
                btParser.setSym1(Flags.FINAL);
                break;
            }
     
            //
            // Rule 200:  AbstractMethodDeclaration ::= AbstractMethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt SEMICOLON
            //
            case 200: {
                Flags a = (Flags) btParser.getSym(1);
                assert(btParser.getSym(2) == null);
                TypeNode b = (TypeNode) btParser.getSym(3);
                Object[] o = (Object []) btParser.getSym(4);
                    Name c = (Name) o[0];
                    List d = (List) o[1];
                    Integer e = (Integer) o[2];
                List f = (List) btParser.getSym(5);

                if (b.type() == ts.Void() && e.intValue() > 0)
                {
                    // TODO: error!!!
                }

                btParser.setSym1(nf.MethodDecl(pos(btParser.getFirstToken(3), btParser.getLastToken(4)),
                                       a,
                                       array((TypeNode) b, pos(btParser.getFirstToken(3), btParser.getLastToken(3)), e.intValue()),
                                       c.toString(),
                                       d,
                                       f,
                                       null));
                break;
            }
     
            //
            // Rule 201:  AbstractMethodModifiers ::= AbstractMethodModifier
            //
            case 201:
                break;
 
            //
            // Rule 202:  AbstractMethodModifiers ::= AbstractMethodModifiers AbstractMethodModifier
            //
            case 202: {
                Flags a = (Flags) btParser.getSym(1),
                      b = (Flags) btParser.getSym(2);
                btParser.setSym1(a.set(b));
                break;
            }
     
            //
            // Rule 203:  AbstractMethodModifier ::= Annotations
            //
            case 203:
                System.err.println("Rule " + 203 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 204:  AbstractMethodModifier ::= public
            //
            case 204: {
                btParser.setSym1(Flags.PUBLIC);
                break;
            }
     
            //
            // Rule 205:  AbstractMethodModifier ::= abstract
            //
            case 205: {
                btParser.setSym1(Flags.ABSTRACT);
                break;
            }
     
            //
            // Rule 206:  AnnotationTypeDeclaration ::= InterfaceModifiersopt AT interface identifier AnnotationTypeBody
            //
            case 206:
                System.err.println("Rule " + 206 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 207:  AnnotationTypeBody ::= LBRACE AnnotationTypeElementDeclarationsopt RBRACE
            //
            case 207:
                System.err.println("Rule " + 207 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 208:  AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclaration
            //
            case 208:
                System.err.println("Rule " + 208 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 209:  AnnotationTypeElementDeclarations ::= AnnotationTypeElementDeclarations AnnotationTypeElementDeclaration
            //
            case 209:
                System.err.println("Rule " + 209 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 210:  AnnotationTypeElementDeclaration ::= AbstractMethodModifiersopt Type identifier LPAREN RPAREN DefaultValueopt SEMICOLON
            //
            case 210:
                System.err.println("Rule " + 210 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 211:  AnnotationTypeElementDeclaration ::= ConstantDeclaration
            //
            case 211:
                System.err.println("Rule " + 211 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 212:  AnnotationTypeElementDeclaration ::= ClassDeclaration
            //
            case 212:
                System.err.println("Rule " + 212 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 213:  AnnotationTypeElementDeclaration ::= InterfaceDeclaration
            //
            case 213:
                System.err.println("Rule " + 213 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 214:  AnnotationTypeElementDeclaration ::= EnumDeclaration
            //
            case 214:
                System.err.println("Rule " + 214 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 215:  AnnotationTypeElementDeclaration ::= AnnotationTypeDeclaration
            //
            case 215:
                System.err.println("Rule " + 215 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 216:  AnnotationTypeElementDeclaration ::= SEMICOLON
            //
            case 216:
                System.err.println("Rule " + 216 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 217:  DefaultValue ::= default ElementValue
            //
            case 217:
                System.err.println("Rule " + 217 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 218:  Annotations ::= Annotation
            //
            case 218:
                System.err.println("Rule " + 218 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 219:  Annotations ::= Annotations Annotation
            //
            case 219:
                System.err.println("Rule " + 219 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 220:  Annotation ::= NormalAnnotation
            //
            case 220:
                System.err.println("Rule " + 220 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 221:  Annotation ::= MarkerAnnotation
            //
            case 221:
                System.err.println("Rule " + 221 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 222:  Annotation ::= SingleElementAnnotation
            //
            case 222:
                System.err.println("Rule " + 222 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 223:  NormalAnnotation ::= AT TypeName LPAREN ElementValuePairsopt RPAREN
            //
            case 223:
                System.err.println("Rule " + 223 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 224:  ElementValuePairs ::= ElementValuePair
            //
            case 224:
                System.err.println("Rule " + 224 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 225:  ElementValuePairs ::= ElementValuePairs COMMA ElementValuePair
            //
            case 225:
                System.err.println("Rule " + 225 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 226:  ElementValuePair ::= SimpleName EQUAL ElementValue
            //
            case 226:
                System.err.println("Rule " + 226 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 227:  SimpleName ::= identifier
            //
            case 227: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 228:  ElementValue ::= ConditionalExpression
            //
            case 228:
                System.err.println("Rule " + 228 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 229:  ElementValue ::= Annotation
            //
            case 229:
                System.err.println("Rule " + 229 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 230:  ElementValue ::= ElementValueArrayInitializer
            //
            case 230:
                System.err.println("Rule " + 230 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 231:  ElementValueArrayInitializer ::= LBRACE ElementValuesopt ,opt RBRACE
            //
            case 231:
                System.err.println("Rule " + 231 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 232:  ElementValues ::= ElementValue
            //
            case 232:
                System.err.println("Rule " + 232 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 233:  ElementValues ::= ElementValues COMMA ElementValue
            //
            case 233:
                System.err.println("Rule " + 233 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 234:  MarkerAnnotation ::= AT TypeName
            //
            case 234:
                System.err.println("Rule " + 234 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 235:  SingleElementAnnotation ::= AT TypeName LPAREN ElementValue RPAREN
            //
            case 235:
                System.err.println("Rule " + 235 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 236:  ArrayInitializer ::= LBRACE VariableInitializersopt ,opt RBRACE
            //
            case 236: {
                List a = (List) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.ArrayInit(pos()));
                else btParser.setSym1(nf.ArrayInit(pos(), a));
                break;
            }
     
            //
            // Rule 237:  VariableInitializers ::= VariableInitializer
            //
            case 237: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 238:  VariableInitializers ::= VariableInitializers COMMA VariableInitializer
            //
            case 238: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 239:  Block ::= LBRACE BlockStatementsopt RBRACE
            //
            case 239: {
                List l = (List) btParser.getSym(2);
                btParser.setSym1(nf.Block(pos(), l));
                break;
            }
     
            //
            // Rule 240:  BlockStatements ::= BlockStatement
            //
            case 240: {
                List l = new TypedList(new LinkedList(), Stmt.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 241:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 241: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 242:  BlockStatement ::= LocalVariableDeclarationStatement
            //
            case 242:
                break;
 
            //
            // Rule 243:  BlockStatement ::= ClassDeclaration
            //
            case 243: {
                ClassDecl a = (ClassDecl) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 244:  BlockStatement ::= Statement
            //
            case 244: {
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 245:  LocalVariableDeclarationStatement ::= LocalVariableDeclaration SEMICOLON
            //
            case 245:
                break;
 
            //
            // Rule 246:  LocalVariableDeclaration ::= VariableModifiersopt Type VariableDeclarators
            //
            case 246: {
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
            // Rule 247:  Statement ::= StatementWithoutTrailingSubstatement
            //
            case 247:
                break;
 
            //
            // Rule 248:  Statement ::= LabeledStatement
            //
            case 248:
                break;
 
            //
            // Rule 249:  Statement ::= IfThenStatement
            //
            case 249:
                break;
 
            //
            // Rule 250:  Statement ::= IfThenElseStatement
            //
            case 250:
                break;
 
            //
            // Rule 251:  Statement ::= WhileStatement
            //
            case 251:
                break;
 
            //
            // Rule 252:  Statement ::= ForStatement
            //
            case 252:
                break;
 
            //
            // Rule 253:  StatementWithoutTrailingSubstatement ::= Block
            //
            case 253:
                break;
 
            //
            // Rule 254:  StatementWithoutTrailingSubstatement ::= EmptyStatement
            //
            case 254:
                break;
 
            //
            // Rule 255:  StatementWithoutTrailingSubstatement ::= ExpressionStatement
            //
            case 255:
                break;
 
            //
            // Rule 256:  StatementWithoutTrailingSubstatement ::= AssertStatement
            //
            case 256:
                break;
 
            //
            // Rule 257:  StatementWithoutTrailingSubstatement ::= SwitchStatement
            //
            case 257:
                break;
 
            //
            // Rule 258:  StatementWithoutTrailingSubstatement ::= DoStatement
            //
            case 258:
                break;
 
            //
            // Rule 259:  StatementWithoutTrailingSubstatement ::= BreakStatement
            //
            case 259:
                break;
 
            //
            // Rule 260:  StatementWithoutTrailingSubstatement ::= ContinueStatement
            //
            case 260:
                break;
 
            //
            // Rule 261:  StatementWithoutTrailingSubstatement ::= ReturnStatement
            //
            case 261:
                break;
 
            //
            // Rule 262:  StatementWithoutTrailingSubstatement ::= SynchronizedStatement
            //
            case 262:
                break;
 
            //
            // Rule 263:  StatementWithoutTrailingSubstatement ::= ThrowStatement
            //
            case 263:
                break;
 
            //
            // Rule 264:  StatementWithoutTrailingSubstatement ::= TryStatement
            //
            case 264:
                break;
 
            //
            // Rule 265:  StatementNoShortIf ::= StatementWithoutTrailingSubstatement
            //
            case 265:
                break;
 
            //
            // Rule 266:  StatementNoShortIf ::= LabeledStatementNoShortIf
            //
            case 266:
                break;
 
            //
            // Rule 267:  StatementNoShortIf ::= IfThenElseStatementNoShortIf
            //
            case 267:
                break;
 
            //
            // Rule 268:  StatementNoShortIf ::= WhileStatementNoShortIf
            //
            case 268:
                break;
 
            //
            // Rule 269:  StatementNoShortIf ::= ForStatementNoShortIf
            //
            case 269:
                break;
 
            //
            // Rule 270:  IfThenStatement ::= if LPAREN Expression RPAREN Statement
            //
            case 270: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.If(pos(), a, b));
                break;
            }
     
            //
            // Rule 271:  IfThenElseStatement ::= if LPAREN Expression RPAREN StatementNoShortIf else Statement
            //
            case 271: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 272:  IfThenElseStatementNoShortIf ::= if LPAREN Expression RPAREN StatementNoShortIf else StatementNoShortIf
            //
            case 272: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                Stmt c = (Stmt) btParser.getSym(7);
                btParser.setSym1(nf.If(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 273:  EmptyStatement ::= SEMICOLON
            //
            case 273: {
                btParser.setSym1(nf.Empty(pos()));
                break;
            }
     
            //
            // Rule 274:  LabeledStatement ::= identifier COLON Statement
            //
            case 274: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 275:  LabeledStatementNoShortIf ::= identifier COLON StatementNoShortIf
            //
            case 275: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                Stmt b = (Stmt) btParser.getSym(3);
                btParser.setSym1(nf.Labeled(pos(), a.getIdentifier(), b));
                break;
            }
     
            //
            // Rule 276:  ExpressionStatement ::= StatementExpression SEMICOLON
            //
            case 276: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Eval(pos(), a));
                break;
            }
     
            //
            // Rule 277:  StatementExpression ::= Assignment
            //
            case 277:
                break;
 
            //
            // Rule 278:  StatementExpression ::= PreIncrementExpression
            //
            case 278:
                break;
 
            //
            // Rule 279:  StatementExpression ::= PreDecrementExpression
            //
            case 279:
                break;
 
            //
            // Rule 280:  StatementExpression ::= PostIncrementExpression
            //
            case 280:
                break;
 
            //
            // Rule 281:  StatementExpression ::= PostDecrementExpression
            //
            case 281:
                break;
 
            //
            // Rule 282:  StatementExpression ::= MethodInvocation
            //
            case 282:
                break;
 
            //
            // Rule 283:  StatementExpression ::= ClassInstanceCreationExpression
            //
            case 283:
                break;
 
            //
            // Rule 284:  AssertStatement ::= assert Expression SEMICOLON
            //
            case 284: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Assert(pos(), a));
                break;
            }
     
            //
            // Rule 285:  AssertStatement ::= assert Expression COLON Expression SEMICOLON
            //
            case 285: {
                Expr a = (Expr) btParser.getSym(2),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Assert(pos(), a, b));
                break;
            }
     
            //
            // Rule 286:  SwitchStatement ::= switch LPAREN Expression RPAREN SwitchBlock
            //
            case 286: {
                Expr a = (Expr) btParser.getSym(3);
                List b = (List) btParser.getSym(5);
                btParser.setSym1(nf.Switch(pos(), a, b));
                break;
            }
     
            //
            // Rule 287:  SwitchBlock ::= LBRACE SwitchBlockStatementGroupsopt SwitchLabelsopt RBRACE
            //
            case 287: {
                List l = (List) btParser.getSym(2),
                     l2 = (List) btParser.getSym(3);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 288:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroup
            //
            case 288:
                break;
 
            //
            // Rule 289:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 289: {
                List l = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l2);
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 290:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 290: {
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);

                List l1 = (List) btParser.getSym(1),
                     l2 = (List) btParser.getSym(2);
                l.addAll(l1);
                l.add(nf.SwitchBlock(pos(), l2));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 291:  SwitchLabels ::= SwitchLabel
            //
            case 291: {
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 292:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 292: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 293:  SwitchLabel ::= case ConstantExpression COLON
            //
            case 293: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Case(pos(), a));
                break;
            }
     
            //
            // Rule 294:  SwitchLabel ::= case EnumConstant COLON
            //
            case 294:
                System.err.println("Rule " + 294 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 295:  SwitchLabel ::= default COLON
            //
            case 295: {
                btParser.setSym1(nf.Default(pos()));
                break;
            }
     
            //
            // Rule 296:  EnumConstant ::= identifier
            //
            case 296:
                System.err.println("Rule " + 296 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 297:  WhileStatement ::= while LPAREN Expression RPAREN Statement
            //
            case 297: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 298:  WhileStatementNoShortIf ::= while LPAREN Expression RPAREN StatementNoShortIf
            //
            case 298: {
                Expr a = (Expr) btParser.getSym(3);
                Stmt b = (Stmt) btParser.getSym(5);
                btParser.setSym1(nf.While(pos(), a, b));
                break;
            }
     
            //
            // Rule 299:  DoStatement ::= do Statement while LPAREN Expression RPAREN SEMICOLON
            //
            case 299: {
                Stmt a = (Stmt) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Do(pos(), a, b));
                break;
            }
     
            //
            // Rule 300:  ForStatement ::= BasicForStatement
            //
            case 300:
                break;
 
            //
            // Rule 301:  ForStatement ::= EnhancedForStatement
            //
            case 301:
                System.err.println("Rule " + 301 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 302:  BasicForStatement ::= for LPAREN ForInitopt SEMICOLON Expressionopt SEMICOLON ForUpdateopt RPAREN Statement
            //
            case 302: {
                List a = (List) btParser.getSym(3);
                Expr b = (Expr) btParser.getSym(5);
                List c = (List) btParser.getSym(7);
                Stmt d = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), a, b, c, d));
                break;
            }
     
            //
            // Rule 303:  ForStatementNoShortIf ::= for LPAREN ForInitopt SEMICOLON Expressionopt SEMICOLON ForUpdateopt RPAREN StatementNoShortIf
            //
            case 303: {
                List a = (List) btParser.getSym(3);
                Expr b = (Expr) btParser.getSym(5);
                List c = (List) btParser.getSym(7);
                Stmt d = (Stmt) btParser.getSym(9);
                btParser.setSym1(nf.For(pos(), a, b, c, d));
                break;
            }
     
            //
            // Rule 304:  ForInit ::= StatementExpressionList
            //
            case 304:
                break;
 
            //
            // Rule 305:  ForInit ::= LocalVariableDeclaration
            //
            case 305: {
                List l = new TypedList(new LinkedList(), ForInit.class, false),
                     l2 = (List) btParser.getSym(1);
                l.addAll(l2);
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 306:  ForUpdate ::= StatementExpressionList
            //
            case 306:
                break;
 
            //
            // Rule 307:  StatementExpressionList ::= StatementExpression
            //
            case 307: {
                List l = new TypedList(new LinkedList(), Eval.class, false);
                Expr a = (Expr) btParser.getSym(1);
                l.add(nf.Eval(pos(), a));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 308:  StatementExpressionList ::= StatementExpressionList COMMA StatementExpression
            //
            case 308: {
                List l = (List) btParser.getSym(1);
                Expr a = (Expr) btParser.getSym(3);
                l.add(nf.Eval(pos(), a));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 309:  EnhancedForStatement ::= for LPAREN FormalParameter COLON Expression RPAREN Statement
            //
            case 309:
                System.err.println("Rule " + 309 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 310:  BreakStatement ::= break identifieropt SEMICOLON
            //
            case 310: {
                Name a = (Name) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.Break(pos()));
                else btParser.setSym1(nf.Break(pos(), a.toString()));
                break;
            }
     
            //
            // Rule 311:  ContinueStatement ::= continue identifieropt SEMICOLON
            //
            case 311: {
                Name a = (Name) btParser.getSym(2);
                if (a == null)
                     btParser.setSym1(nf.Continue(pos()));
                else btParser.setSym1(nf.Continue(pos(), a.toString()));
                break;
            }
     
            //
            // Rule 312:  ReturnStatement ::= return Expressionopt SEMICOLON
            //
            case 312: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Return(pos(), a));
                break;
            }
     
            //
            // Rule 313:  ThrowStatement ::= throw Expression SEMICOLON
            //
            case 313: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Throw(pos(), a));
                break;
            }
     
            //
            // Rule 314:  SynchronizedStatement ::= synchronized LPAREN Expression RPAREN Block
            //
            case 314: {
                Expr a = (Expr) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Synchronized(pos(), a, b));
                break;
            }
     
            //
            // Rule 315:  TryStatement ::= try Block Catches
            //
            case 315: {
                Block a = (Block) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Try(pos(), a, b));
                break;
            }
     
            //
            // Rule 316:  TryStatement ::= try Block Catchesopt Finally
            //
            case 316: {
                Block a = (Block) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                Block c = (Block) btParser.getSym(4);
                btParser.setSym1(nf.Try(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 317:  Catches ::= CatchClause
            //
            case 317: {
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 318:  Catches ::= Catches CatchClause
            //
            case 318: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 319:  CatchClause ::= catch LPAREN FormalParameter RPAREN Block
            //
            case 319: {
                Formal a = (Formal) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Catch(pos(), a, b));
                break;
            }
     
            //
            // Rule 320:  Finally ::= finally Block
            //
            case 320: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 321:  Primary ::= PrimaryNoNewArray
            //
            case 321:
                break;
 
            //
            // Rule 322:  Primary ::= ArrayCreationExpression
            //
            case 322:
                break;
 
            //
            // Rule 323:  PrimaryNoNewArray ::= Literal
            //
            case 323:
                break;
 
            //
            // Rule 324:  PrimaryNoNewArray ::= Type DOT class
            //
            case 324: {
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
            // Rule 325:  PrimaryNoNewArray ::= void DOT class
            //
            case 325: {
                btParser.setSym1(nf.ClassLit(pos(),
                                     nf.CanonicalTypeNode(pos(btParser.getToken(1)), ts.Void())));
                break;
            }
     
            //
            // Rule 326:  PrimaryNoNewArray ::= this
            //
            case 326: {
                btParser.setSym1(nf.This(pos()));
                break;
            }
     
            //
            // Rule 327:  PrimaryNoNewArray ::= ClassName DOT this
            //
            case 327: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(nf.This(pos(), a.toType()));
                break;
            }
     
            //
            // Rule 328:  PrimaryNoNewArray ::= LPAREN Expression RPAREN
            //
            case 328: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 329:  PrimaryNoNewArray ::= ClassInstanceCreationExpression
            //
            case 329:
                break;
 
            //
            // Rule 330:  PrimaryNoNewArray ::= FieldAccess
            //
            case 330:
                break;
 
            //
            // Rule 331:  PrimaryNoNewArray ::= MethodInvocation
            //
            case 331:
                break;
 
            //
            // Rule 332:  PrimaryNoNewArray ::= ArrayAccess
            //
            case 332:
                break;
 
            //
            // Rule 333:  Literal ::= IntegerLiteral
            //
            case 333: {
                // TODO: remove any prefix (such as 0x)
                polyglot.lex.IntegerLiteral a = int_lit(btParser.getToken(1), 10);
                btParser.setSym1(nf.IntLit(pos(), IntLit.INT, a.getValue().intValue()));
                break;
            }
     
            //
            // Rule 334:  Literal ::= LongLiteral
            //
            case 334: {
                // TODO: remove any suffix (such as L) or prefix (such as 0x)
                polyglot.lex.LongLiteral a = long_lit(btParser.getToken(1), 10);
                btParser.setSym1(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                break;
            }
     
            //
            // Rule 335:  Literal ::= FloatingPointLiteral
            //
            case 335: {
                // TODO: remove any suffix (such as F)
                polyglot.lex.FloatLiteral a = float_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                break;
            }
     
            //
            // Rule 336:  Literal ::= DoubleLiteral
            //
            case 336: {
                // TODO: remove any suffix (such as D)
                polyglot.lex.DoubleLiteral a = double_lit(btParser.getToken(1));
                btParser.setSym1(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                break;
            }
     
            //
            // Rule 337:  Literal ::= BooleanLiteral
            //
            case 337: {
                polyglot.lex.BooleanLiteral a = boolean_lit(btParser.getToken(1));
                btParser.setSym1(nf.BooleanLit(pos(), a.getValue().booleanValue()));
                break;
            }
     
            //
            // Rule 338:  Literal ::= CharacterLiteral
            //
            case 338: {
                polyglot.lex.CharacterLiteral a = char_lit(btParser.getToken(1));
                btParser.setSym1(nf.CharLit(pos(), a.getValue().charValue()));
                break;
            }
     
            //
            // Rule 339:  Literal ::= StringLiteral
            //
            case 339: {
                polyglot.lex.StringLiteral a = string_lit(btParser.getToken(1));
                btParser.setSym1(nf.StringLit(pos(), a.getValue()));
                break;
            }
     
            //
            // Rule 340:  Literal ::= null
            //
            case 340: {
                btParser.setSym1(nf.NullLit(pos()));
                break;
            }
     
            //
            // Rule 341:  BooleanLiteral ::= true
            //
            case 341:
                break;
 
            //
            // Rule 342:  BooleanLiteral ::= false
            //
            case 342:
                break;
 
            //
            // Rule 343:  ClassInstanceCreationExpression ::= new TypeArgumentsopt ClassOrInterfaceType TypeArgumentsopt LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 343: {
                assert(btParser.getSym(2) == null);
                TypeNode a = (TypeNode) btParser.getSym(3);
                assert(btParser.getSym(4) == null);
                List b = (List) btParser.getSym(6);
                ClassBody c = (ClassBody) btParser.getSym(8);
                if (c == null)
                     btParser.setSym1(nf.New(pos(), a, b));
                else btParser.setSym1(nf.New(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 344:  ClassInstanceCreationExpression ::= Primary DOT new TypeArgumentsopt identifier TypeArgumentsopt LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 344: {
                Expr a = (Expr) btParser.getSym(1);
                assert(btParser.getSym(4) == null);
                Name b = new Name(nf, ts, pos(), id(btParser.getToken(5)).getIdentifier());
                assert(btParser.getSym(6) == null);
                List c = (List) btParser.getSym(8);
                ClassBody d = (ClassBody) btParser.getSym(10);
                if (d == null)
                     btParser.setSym1(nf.New(pos(), a, b.toType(), c));
                else btParser.setSym1(nf.New(pos(), a, b.toType(), c, d));
                break;
            }
     
            //
            // Rule 345:  ClassInstanceCreationExpression ::= AmbiguousName DOT new TypeArgumentsopt identifier TypeArgumentsopt LPAREN ArgumentListopt RPAREN ClassBodyopt
            //
            case 345: {
                Name a = (Name) btParser.getSym(1);
                assert(btParser.getSym(4) == null);
                Name b = new Name(nf, ts, pos(), id(btParser.getToken(5)).getIdentifier());
                assert(btParser.getSym(6) == null);
                List c = (List) btParser.getSym(8);
                ClassBody d = (ClassBody) btParser.getSym(10);
                if (d == null)
                     btParser.setSym1(nf.New(pos(), a.toExpr(), b.toType(), c));
                else btParser.setSym1(nf.New(pos(), a.toExpr(), b.toType(), c, d));
                break;
            }
     
            //
            // Rule 346:  ArgumentList ::= Expression
            //
            case 346: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 347:  ArgumentList ::= ArgumentList COMMA Expression
            //
            case 347: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(3));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 348:  ArrayCreationExpression ::= new PrimitiveType DimExprs Dimsopt
            //
            case 348: {
                CanonicalTypeNode a = (CanonicalTypeNode) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                Integer c = (Integer) btParser.getSym(4);
                btParser.setSym1(nf.NewArray(pos(), a, b, c.intValue()));
                break;
            }
     
            //
            // Rule 349:  ArrayCreationExpression ::= new ClassOrInterfaceType DimExprs Dimsopt
            //
            case 349: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                List b = (List) btParser.getSym(3);
                Integer c = (Integer) btParser.getSym(4);
                btParser.setSym1(nf.NewArray(pos(), a, b, c.intValue()));
                break;
            }
     
            //
            // Rule 350:  ArrayCreationExpression ::= new PrimitiveType Dims ArrayInitializer
            //
            case 350: {
                CanonicalTypeNode a = (CanonicalTypeNode) btParser.getSym(2);
                Integer b = (Integer) btParser.getSym(3);
                ArrayInit c = (ArrayInit) btParser.getSym(4);
                btParser.setSym1(nf.NewArray(pos(), a, b.intValue(), c));
                break;
            }
     
            //
            // Rule 351:  ArrayCreationExpression ::= new ClassOrInterfaceType Dims ArrayInitializer
            //
            case 351: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Integer b = (Integer) btParser.getSym(3);
                ArrayInit c = (ArrayInit) btParser.getSym(4);
                btParser.setSym1(nf.NewArray(pos(), a, b.intValue(), c));
                break;
            }
     
            //
            // Rule 352:  DimExprs ::= DimExpr
            //
            case 352: {
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(btParser.getSym(1));
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 353:  DimExprs ::= DimExprs DimExpr
            //
            case 353: {
                List l = (List) btParser.getSym(1);
                l.add(btParser.getSym(2));
                //btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 354:  DimExpr ::= LBRACKET Expression RBRACKET
            //
            case 354: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(a.position(pos()));
                break;
            }
     
            //
            // Rule 355:  Dims ::= LBRACKET RBRACKET
            //
            case 355: {
                btParser.setSym1(new Integer(0));
                break;
            }
     
            //
            // Rule 356:  Dims ::= Dims LBRACKET RBRACKET
            //
            case 356: {
                Integer a = (Integer) btParser.getSym(1);
                btParser.setSym1(new Integer(a.intValue() + 1));
                break;
            }
     
            //
            // Rule 357:  FieldAccess ::= Primary DOT identifier
            //
            case 357: {
                Expr a = (Expr) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(), a, b.getIdentifier()));
                break;
            }
     
            //
            // Rule 358:  FieldAccess ::= super DOT identifier
            //
            case 358: {
                polyglot.lex.Identifier a = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken())), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 359:  FieldAccess ::= ClassName DOT super DOT identifier
            //
            case 359: {
                Name a = (Name) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                btParser.setSym1(nf.Field(pos(btParser.getLastToken()), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier()));
                break;
            }
     
            //
            // Rule 360:  MethodInvocation ::= MethodName LPAREN ArgumentListopt RPAREN
            //
            case 360: {
                Name a = (Name) btParser.getSym(1);
                List b = (List) btParser.getSym(3);
                btParser.setSym1(nf.Call(pos(), a.prefix == null ? null : a.prefix.toReceiver(), a.name, b));
                break;
            }
     
            //
            // Rule 361:  MethodInvocation ::= Primary DOT TypeArgumentsopt identifier LPAREN ArgumentListopt RPAREN
            //
            case 361: {
                Expr a = (Expr) btParser.getSym(1);
                assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(4));
                List c = (List) btParser.getSym(6);
                btParser.setSym1(nf.Call(pos(), a, b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 362:  MethodInvocation ::= super DOT TypeArgumentsopt identifier LPAREN ArgumentListopt RPAREN
            //
            case 362: {
                assert(btParser.getSym(3) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(4));
                List c = (List) btParser.getSym(6);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken())), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 363:  MethodInvocation ::= ClassName DOT super DOT TypeArgumentsopt identifier LPAREN ArgumentListopt RPAREN
            //
            case 363: {
                Name a = (Name) btParser.getSym(1);
                assert(btParser.getSym(5) == null);
                polyglot.lex.Identifier b = id(btParser.getToken(6));
                List c = (List) btParser.getSym(8);
                btParser.setSym1(nf.Call(pos(), nf.Super(pos(btParser.getFirstToken(3)), a.toType()), b.getIdentifier(), c));
                break;
            }
     
            //
            // Rule 365:  PostfixExpression ::= Primary
            //
            case 365:
                break;
 
            //
            // Rule 366:  PostfixExpression ::= ExpressionName
            //
            case 366: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 367:  PostfixExpression ::= PostIncrementExpression
            //
            case 367:
                break;
 
            //
            // Rule 368:  PostfixExpression ::= PostDecrementExpression
            //
            case 368:
                break;
 
            //
            // Rule 369:  PostIncrementExpression ::= PostfixExpression PLUS_PLUS
            //
            case 369: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_INC));
                break;
            }
     
            //
            // Rule 370:  PostDecrementExpression ::= PostfixExpression MINUS_MINUS
            //
            case 370: {
                Expr a = (Expr) btParser.getSym(1);
                btParser.setSym1(nf.Unary(pos(), a, Unary.POST_DEC));
                break;
            }
     
            //
            // Rule 371:  UnaryExpression ::= PreIncrementExpression
            //
            case 371:
                break;
 
            //
            // Rule 372:  UnaryExpression ::= PreDecrementExpression
            //
            case 372:
                break;
 
            //
            // Rule 373:  UnaryExpression ::= PLUS UnaryExpression
            //
            case 373: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.POS, a));
                break;
            }
     
            //
            // Rule 374:  UnaryExpression ::= MINUS UnaryExpression
            //
            case 374: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NEG, a));
                break;
            }
     
            //
            // Rule 376:  PreIncrementExpression ::= PLUS_PLUS UnaryExpression
            //
            case 376: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_INC, a));
                break;
            }
     
            //
            // Rule 377:  PreDecrementExpression ::= MINUS_MINUS UnaryExpression
            //
            case 377: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.PRE_DEC, a));
                break;
            }
     
            //
            // Rule 378:  UnaryExpressionNotPlusMinus ::= PostfixExpression
            //
            case 378:
                break;
 
            //
            // Rule 379:  UnaryExpressionNotPlusMinus ::= TWIDDLE UnaryExpression
            //
            case 379: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.BIT_NOT, a));
                break;
            }
     
            //
            // Rule 380:  UnaryExpressionNotPlusMinus ::= NOT UnaryExpression
            //
            case 380: {
                Expr a = (Expr) btParser.getSym(2);
                btParser.setSym1(nf.Unary(pos(), Unary.NOT, a));
                break;
            }
     
            //
            // Rule 382:  MultiplicativeExpression ::= UnaryExpression
            //
            case 382:
                break;
 
            //
            // Rule 383:  MultiplicativeExpression ::= MultiplicativeExpression MULTIPLY UnaryExpression
            //
            case 383: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MUL, b));
                break;
            }
     
            //
            // Rule 384:  MultiplicativeExpression ::= MultiplicativeExpression DIVIDE UnaryExpression
            //
            case 384: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.DIV, b));
                break;
            }
     
            //
            // Rule 385:  MultiplicativeExpression ::= MultiplicativeExpression REMAINDER UnaryExpression
            //
            case 385: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.MOD, b));
                break;
            }
     
            //
            // Rule 386:  AdditiveExpression ::= MultiplicativeExpression
            //
            case 386:
                break;
 
            //
            // Rule 387:  AdditiveExpression ::= AdditiveExpression PLUS MultiplicativeExpression
            //
            case 387: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.ADD, b));
                break;
            }
     
            //
            // Rule 388:  AdditiveExpression ::= AdditiveExpression MINUS MultiplicativeExpression
            //
            case 388: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SUB, b));
                break;
            }
     
            //
            // Rule 389:  ShiftExpression ::= AdditiveExpression
            //
            case 389:
                break;
 
            //
            // Rule 390:  ShiftExpression ::= ShiftExpression LEFT_SHIFT AdditiveExpression
            //
            case 390: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHL, b));
                break;
            }
     
            //
            // Rule 391:  ShiftExpression ::= ShiftExpression GREATER GREATER AdditiveExpression
            //
            case 391: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.SHR, b));
                break;
            }
     
            //
            // Rule 392:  ShiftExpression ::= ShiftExpression GREATER GREATER GREATER AdditiveExpression
            //
            case 392: {
                // TODO: make sure that there is no space between the ">" signs
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Binary(pos(), a, Binary.USHR, b));
                break;
            }
     
            //
            // Rule 393:  RelationalExpression ::= ShiftExpression
            //
            case 393:
                break;
 
            //
            // Rule 394:  RelationalExpression ::= RelationalExpression LESS ShiftExpression
            //
            case 394: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LT, b));
                break;
            }
     
            //
            // Rule 395:  RelationalExpression ::= RelationalExpression GREATER ShiftExpression
            //
            case 395: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GT, b));
                break;
            }
     
            //
            // Rule 396:  RelationalExpression ::= RelationalExpression LESS_EQUAL ShiftExpression
            //
            case 396: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.LE, b));
                break;
            }
     
            //
            // Rule 397:  RelationalExpression ::= RelationalExpression GREATER EQUAL ShiftExpression
            //
            case 397: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Binary(pos(), a, Binary.GE, b));
                break;
            }
     
            //
            // Rule 398:  EqualityExpression ::= RelationalExpression
            //
            case 398:
                break;
 
            //
            // Rule 399:  EqualityExpression ::= EqualityExpression EQUAL_EQUAL RelationalExpression
            //
            case 399: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.EQ, b));
                break;
            }
     
            //
            // Rule 400:  EqualityExpression ::= EqualityExpression NOT_EQUAL RelationalExpression
            //
            case 400: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.NE, b));
                break;
            }
     
            //
            // Rule 401:  AndExpression ::= EqualityExpression
            //
            case 401:
                break;
 
            //
            // Rule 402:  AndExpression ::= AndExpression AND EqualityExpression
            //
            case 402: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_AND, b));
                break;
            }
     
            //
            // Rule 403:  ExclusiveOrExpression ::= AndExpression
            //
            case 403:
                break;
 
            //
            // Rule 404:  ExclusiveOrExpression ::= ExclusiveOrExpression XOR AndExpression
            //
            case 404: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_XOR, b));
                break;
            }
     
            //
            // Rule 405:  InclusiveOrExpression ::= ExclusiveOrExpression
            //
            case 405:
                break;
 
            //
            // Rule 406:  InclusiveOrExpression ::= InclusiveOrExpression OR ExclusiveOrExpression
            //
            case 406: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.BIT_OR, b));
                break;
            }
     
            //
            // Rule 407:  ConditionalAndExpression ::= InclusiveOrExpression
            //
            case 407:
                break;
 
            //
            // Rule 408:  ConditionalAndExpression ::= ConditionalAndExpression AND_AND InclusiveOrExpression
            //
            case 408: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_AND, b));
                break;
            }
     
            //
            // Rule 409:  ConditionalOrExpression ::= ConditionalAndExpression
            //
            case 409:
                break;
 
            //
            // Rule 410:  ConditionalOrExpression ::= ConditionalOrExpression OR_OR ConditionalAndExpression
            //
            case 410: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Binary(pos(), a, Binary.COND_OR, b));
                break;
            }
     
            //
            // Rule 411:  ConditionalExpression ::= ConditionalOrExpression
            //
            case 411:
                break;
 
            //
            // Rule 412:  ConditionalExpression ::= ConditionalOrExpression QUESTION Expression COLON ConditionalExpression
            //
            case 412: {
                Expr a = (Expr) btParser.getSym(1),
                     b = (Expr) btParser.getSym(3),
                     c = (Expr) btParser.getSym(5);
                btParser.setSym1(nf.Conditional(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 413:  AssignmentExpression ::= ConditionalExpression
            //
            case 413:
                break;
 
            //
            // Rule 414:  AssignmentExpression ::= Assignment
            //
            case 414:
                break;
 
            //
            // Rule 415:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 415: {
                Expr a = (Expr) btParser.getSym(1);
                Assign.Operator b = (Assign.Operator) btParser.getSym(2);
                Expr c = (Expr) btParser.getSym(3);
                btParser.setSym1(nf.Assign(pos(), a, b, c));
                break;
            }
     
            //
            // Rule 416:  LeftHandSide ::= ExpressionName
            //
            case 416: {
                Name a = (Name) btParser.getSym(1);
                btParser.setSym1(a.toExpr());
                break;
            }
     
            //
            // Rule 417:  LeftHandSide ::= FieldAccess
            //
            case 417:
                break;
 
            //
            // Rule 418:  LeftHandSide ::= ArrayAccess
            //
            case 418:
                break;
 
            //
            // Rule 419:  AssignmentOperator ::= EQUAL
            //
            case 419: {
                btParser.setSym1(Assign.ASSIGN);
                break;
            }
     
            //
            // Rule 420:  AssignmentOperator ::= MULTIPLY_EQUAL
            //
            case 420: {
                btParser.setSym1(Assign.MUL_ASSIGN);
                break;
            }
     
            //
            // Rule 421:  AssignmentOperator ::= DIVIDE_EQUAL
            //
            case 421: {
                btParser.setSym1(Assign.DIV_ASSIGN);
                break;
            }
     
            //
            // Rule 422:  AssignmentOperator ::= REMAINDER_EQUAL
            //
            case 422: {
                btParser.setSym1(Assign.MOD_ASSIGN);
                break;
            }
     
            //
            // Rule 423:  AssignmentOperator ::= PLUS_EQUAL
            //
            case 423: {
                btParser.setSym1(Assign.ADD_ASSIGN);
                break;
            }
     
            //
            // Rule 424:  AssignmentOperator ::= MINUS_EQUAL
            //
            case 424: {
                btParser.setSym1(Assign.SUB_ASSIGN);
                break;
            }
     
            //
            // Rule 425:  AssignmentOperator ::= LEFT_SHIFT_EQUAL
            //
            case 425: {
                btParser.setSym1(Assign.SHL_ASSIGN);
                break;
            }
     
            //
            // Rule 426:  AssignmentOperator ::= GREATER GREATER EQUAL
            //
            case 426: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.SHR_ASSIGN);
                break;
            }
     
            //
            // Rule 427:  AssignmentOperator ::= GREATER GREATER GREATER EQUAL
            //
            case 427: {
                // TODO: make sure that there is no space between the ">" signs
                btParser.setSym1(Assign.USHR_ASSIGN);
                break;
            }
     
            //
            // Rule 428:  AssignmentOperator ::= AND_EQUAL
            //
            case 428: {
                btParser.setSym1(Assign.BIT_AND_ASSIGN);
                break;
            }
     
            //
            // Rule 429:  AssignmentOperator ::= XOR_EQUAL
            //
            case 429: {
                btParser.setSym1(Assign.BIT_XOR_ASSIGN);
                break;
            }
     
            //
            // Rule 430:  AssignmentOperator ::= OR_EQUAL
            //
            case 430: {
                btParser.setSym1(Assign.BIT_OR_ASSIGN);
                break;
            }
     
            //
            // Rule 431:  Expression ::= AssignmentExpression
            //
            case 431:
                break;
 
            //
            // Rule 432:  ConstantExpression ::= Expression
            //
            case 432:
                break;
 
            //
            // Rule 433:  Dimsopt ::=
            //
            case 433: {
                btParser.setSym1(new Integer(0));
                break;
            }
     
            //
            // Rule 434:  Dimsopt ::= Dims
            //
            case 434:
                break;
 
            //
            // Rule 435:  Catchesopt ::=
            //
            case 435: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 436:  Catchesopt ::= Catches
            //
            case 436:
                break;
 
            //
            // Rule 437:  identifieropt ::=
            //
            case 437:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 438:  identifieropt ::= identifier
            //
            case 438: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 439:  ForUpdateopt ::=
            //
            case 439: {
                btParser.setSym1(new TypedList(new LinkedList(), ForUpdate.class, false));
                break;
            }
     
            //
            // Rule 440:  ForUpdateopt ::= ForUpdate
            //
            case 440:
                break;
 
            //
            // Rule 441:  Expressionopt ::=
            //
            case 441:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 442:  Expressionopt ::= Expression
            //
            case 442:
                break;
 
            //
            // Rule 443:  ForInitopt ::=
            //
            case 443: {
                btParser.setSym1(new TypedList(new LinkedList(), ForInit.class, false));
                break;
            }
     
            //
            // Rule 444:  ForInitopt ::= ForInit
            //
            case 444:
                break;
 
            //
            // Rule 445:  SwitchLabelsopt ::=
            //
            case 445: {
                btParser.setSym1(new TypedList(new LinkedList(), Case.class, false));
                break;
            }
     
            //
            // Rule 446:  SwitchLabelsopt ::= SwitchLabels
            //
            case 446:
                break;
 
            //
            // Rule 447:  SwitchBlockStatementGroupsopt ::=
            //
            case 447: {
                btParser.setSym1(new TypedList(new LinkedList(), SwitchElement.class, false));
                break;
            }
     
            //
            // Rule 448:  SwitchBlockStatementGroupsopt ::= SwitchBlockStatementGroups
            //
            case 448:
                break;
 
            //
            // Rule 449:  VariableModifiersopt ::=
            //
            case 449: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 450:  VariableModifiersopt ::= VariableModifiers
            //
            case 450:
                break;
 
            //
            // Rule 451:  VariableInitializersopt ::=
            //
            case 451:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 452:  VariableInitializersopt ::= VariableInitializers
            //
            case 452:
                break;
 
            //
            // Rule 453:  ElementValuesopt ::=
            //
            case 453:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 454:  ElementValuesopt ::= ElementValues
            //
            case 454:
                System.err.println("Rule " + 454 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 455:  ElementValuePairsopt ::=
            //
            case 455:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 456:  ElementValuePairsopt ::= ElementValuePairs
            //
            case 456:
                System.err.println("Rule " + 456 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 457:  DefaultValueopt ::=
            //
            case 457:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 458:  DefaultValueopt ::= DefaultValue
            //
            case 458:
                break;
 
            //
            // Rule 459:  AnnotationTypeElementDeclarationsopt ::=
            //
            case 459:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 460:  AnnotationTypeElementDeclarationsopt ::= AnnotationTypeElementDeclarations
            //
            case 460:
                System.err.println("Rule " + 460 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 461:  AbstractMethodModifiersopt ::=
            //
            case 461: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 462:  AbstractMethodModifiersopt ::= AbstractMethodModifiers
            //
            case 462:
                break;
 
            //
            // Rule 463:  ConstantModifiersopt ::=
            //
            case 463: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 464:  ConstantModifiersopt ::= ConstantModifiers
            //
            case 464:
                break;
 
            //
            // Rule 465:  InterfaceMemberDeclarationsopt ::=
            //
            case 465: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 466:  InterfaceMemberDeclarationsopt ::= InterfaceMemberDeclarations
            //
            case 466:
                break;
 
            //
            // Rule 467:  ExtendsInterfacesopt ::=
            //
            case 467: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 468:  ExtendsInterfacesopt ::= ExtendsInterfaces
            //
            case 468:
                break;
 
            //
            // Rule 469:  InterfaceModifiersopt ::=
            //
            case 469: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 470:  InterfaceModifiersopt ::= InterfaceModifiers
            //
            case 470:
                break;
 
            //
            // Rule 471:  ClassBodyopt ::=
            //
            case 471:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 472:  ClassBodyopt ::= ClassBody
            //
            case 472:
                break;
 
            //
            // Rule 473:  Argumentsopt ::=
            //
            case 473:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 474:  Argumentsopt ::= Arguments
            //
            case 474:
                System.err.println("Rule " + 474 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 475:  EnumBodyDeclarationsopt ::=
            //
            case 475:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 476:  EnumBodyDeclarationsopt ::= EnumBodyDeclarations
            //
            case 476:
                System.err.println("Rule " + 476 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 477:  ,opt ::=
            //
            case 477:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 478:  ,opt ::= COMMA
            //
            case 478:
                break;
 
            //
            // Rule 479:  EnumConstantsopt ::=
            //
            case 479:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 480:  EnumConstantsopt ::= EnumConstants
            //
            case 480:
                System.err.println("Rule " + 480 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 481:  ArgumentListopt ::=
            //
            case 481: {
                btParser.setSym1(new TypedList(new LinkedList(), Catch.class, false));
                break;
            }
     
            //
            // Rule 482:  ArgumentListopt ::= ArgumentList
            //
            case 482:
                break;
 
            //
            // Rule 483:  BlockStatementsopt ::=
            //
            case 483: {
                btParser.setSym1(new TypedList(new LinkedList(), Stmt.class, false));
                break;
            }
     
            //
            // Rule 484:  BlockStatementsopt ::= BlockStatements
            //
            case 484:
                break;
 
            //
            // Rule 485:  ExplicitConstructorInvocationopt ::=
            //
            case 485:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 486:  ExplicitConstructorInvocationopt ::= ExplicitConstructorInvocation
            //
            case 486:
                break;
 
            //
            // Rule 487:  ConstructorModifiersopt ::=
            //
            case 487: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 488:  ConstructorModifiersopt ::= ConstructorModifiers
            //
            case 488:
                break;
 
            //
            // Rule 489:  ...opt ::=
            //
            case 489:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 490:  ...opt ::= ELLIPSIS
            //
            case 490:
                break;
 
            //
            // Rule 491:  FormalParameterListopt ::=
            //
            case 491: {
                btParser.setSym1(new TypedList(new LinkedList(), Formal.class, false));
                break;
            }
     
            //
            // Rule 492:  FormalParameterListopt ::= FormalParameterList
            //
            case 492:
                break;
 
            //
            // Rule 493:  Throwsopt ::=
            //
            case 493: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 494:  Throwsopt ::= Throws
            //
            case 494:
                break;
 
            //
            // Rule 495:  MethodModifiersopt ::=
            //
            case 495: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 496:  MethodModifiersopt ::= MethodModifiers
            //
            case 496:
                break;
 
            //
            // Rule 497:  FieldModifiersopt ::=
            //
            case 497: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 498:  FieldModifiersopt ::= FieldModifiers
            //
            case 498:
                break;
 
            //
            // Rule 499:  ClassBodyDeclarationsopt ::=
            //
            case 499: {
                btParser.setSym1(new TypedList(new LinkedList(), ClassMember.class, false));
                break;
            }
     
            //
            // Rule 500:  ClassBodyDeclarationsopt ::= ClassBodyDeclarations
            //
            case 500:
                break;
 
            //
            // Rule 501:  Interfacesopt ::=
            //
            case 501: {
                btParser.setSym1(new TypedList(new LinkedList(), TypeNode.class, false));
                break;
            }
     
            //
            // Rule 502:  Interfacesopt ::= Interfaces
            //
            case 502:
                break;
 
            //
            // Rule 503:  Superopt ::=
            //
            case 503:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 504:  Superopt ::= Super
            //
            case 504:
                break;
 
            //
            // Rule 505:  TypeParametersopt ::=
            //
            case 505:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 506:  TypeParametersopt ::= TypeParameters
            //
            case 506:
                break;
 
            //
            // Rule 507:  ClassModifiersopt ::=
            //
            case 507: {
                btParser.setSym1(Flags.NONE);
                break;
            }
     
            //
            // Rule 508:  ClassModifiersopt ::= ClassModifiers
            //
            case 508:
                break;
 
            //
            // Rule 509:  Annotationsopt ::=
            //
            case 509:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 510:  Annotationsopt ::= Annotations
            //
            case 510:
                System.err.println("Rule " + 510 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 511:  TypeDeclarationsopt ::=
            //
            case 511: {
                btParser.setSym1(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                break;
            }
     
            //
            // Rule 512:  TypeDeclarationsopt ::= TypeDeclarations
            //
            case 512:
                break;
 
            //
            // Rule 513:  ImportDeclarationsopt ::=
            //
            case 513: {
                btParser.setSym1(new TypedList(new LinkedList(), Import.class, false));
                break;
            }
     
            //
            // Rule 514:  ImportDeclarationsopt ::= ImportDeclarations
            //
            case 514:
                break;
 
            //
            // Rule 515:  PackageDeclarationopt ::=
            //
            case 515:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 516:  PackageDeclarationopt ::= PackageDeclaration
            //
            case 516:
                break;
 
            //
            // Rule 517:  WildcardBoundsOpt ::=
            //
            case 517:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 518:  WildcardBoundsOpt ::= WildcardBounds
            //
            case 518:
                System.err.println("Rule " + 518 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 519:  AdditionalBoundListopt ::=
            //
            case 519:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 520:  AdditionalBoundListopt ::= AdditionalBoundList
            //
            case 520:
                System.err.println("Rule " + 520 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 521:  TypeBoundopt ::=
            //
            case 521:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 522:  TypeBoundopt ::= TypeBound
            //
            case 522:
                System.err.println("Rule " + 522 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 523:  TypeArgumentsopt ::=
            //
            case 523:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 524:  TypeArgumentsopt ::= TypeArguments
            //
            case 524:
                System.err.println("Rule " + 524 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 525:  NormalClassDeclaration ::= ClassModifiersopt value identifier TypeParametersopt Superopt Interfacesopt ClassBody
            //
            case 525: {
                Flags a = (Flags) btParser.getSym(1);
                polyglot.lex.Identifier b = id(btParser.getToken(3));
                assert(btParser.getSym(4) == null);
                TypeNode c = (TypeNode) btParser.getSym(5);
                List d = (List) btParser.getSym(6);
                ClassBody e = (ClassBody) btParser.getSym(7);
                btParser.setSym1(nf.ValueClassDecl(pos(btParser.getFirstToken(), btParser.getLastToken()), a, b.getIdentifier(), c, d, e));
                break;
            }
     
            //
            // Rule 526:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 526: {
                Expr a = (Expr) btParser.getSym(1);
                TypeNode b = (TypeNode) btParser.getSym(3);
                btParser.setSym1(nf.Instanceof(pos(), a, b));
                break;
            }
     
            //
            // Rule 527:  ClassModifier ::= value
            //
            case 527: {
                btParser.setSym1(Flags.VALUE);
                break;
            }
     
            //
            // Rule 528:  ClassModifier ::= reference
            //
            case 528: {
                btParser.setSym1(Flags.REFERENCE);
                break;
            }
     
            //
            // Rule 529:  MethodModifier ::= atomic
            //
            case 529: {
                btParser.setSym1(Flags.ATOMIC);
                break;
            }
     
            //
            // Rule 530:  MethodModifier ::= pure
            //
            case 530: {
                btParser.setSym1(Flags.PURE);
                break;
            }
     
            //
            // Rule 531:  ActualTypeArgument ::= Type
            //
            case 531:
                System.err.println("Rule " + 531 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 532:  CastExpression ::= LPAREN Type RPAREN UnaryExpressionNotPlusMinus
            //
            case 532: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                Expr b = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Cast(pos(), a, b));
                break;
            }
     
            //
            // Rule 533:  TypeParameter ::= TypeVariable TypeBoundopt PlaceTypeSpecifieropt
            //
            case 533:
                System.err.println("Rule " + 533 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 534:  Type ::= DataType PlaceTypeSpecifieropt
            //
            case 534: {
                assert(btParser.getSym(2) == null);
                //btParser.setSym1();
                break;
            }
     
            //
            // Rule 535:  Type ::= future Type
            //
            case 535: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                btParser.setSym1(nf.Future(pos(), a));
                break;
            }
     
            //
            // Rule 536:  Type ::= nullable Type
            //
            case 536: {
                TypeNode a = (TypeNode) btParser.getSym(2);
                btParser.setSym1(nf.Nullable(pos(), a));
                break;
            }
     
            //
            // Rule 537:  DataType ::= PrimitiveType
            //
            case 537:
                break;
 
            //
            // Rule 538:  DataType ::= ClassOrInterfaceType
            //
            case 538:
                break;
 
            //
            // Rule 539:  DataType ::= ArrayType
            //
            case 539:
                break;
 
            //
            // Rule 540:  PlaceTypeSpecifier ::= AT PlaceType
            //
            case 540:
                System.err.println("Rule " + 540 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 541:  PlaceType ::= here
            //
            case 541:
                System.err.println("Rule " + 541 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 542:  PlaceType ::= placelocal
            //
            case 542:
                System.err.println("Rule " + 542 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 543:  PlaceType ::= threadlocal
            //
            case 543:
                System.err.println("Rule " + 543 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 544:  PlaceType ::= methodlocal
            //
            case 544:
                System.err.println("Rule " + 544 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 545:  PlaceType ::= current
            //
            case 545:
                System.err.println("Rule " + 545 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 546:  PlaceType ::= PlaceExpression
            //
            case 546:
                System.err.println("Rule " + 546 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 547:  VariableModifier ::= clocked LPAREN ClockValue RPAREN
            //
            case 547:
                System.err.println("Rule " + 547 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 548:  FieldModifier ::= clocked LPAREN ClockValue RPAREN
            //
            case 548:
                System.err.println("Rule " + 548 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 549:  MethodHeader ::= MethodModifiersopt TypeParametersopt ResultType MethodDeclarator Throwsopt RunsAtopt RunsOnopt
            //
            case 549:
                System.err.println("Rule " + 549 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 550:  RunsAt ::= runsat PlaceExpression
            //
            case 550:
                System.err.println("Rule " + 550 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 551:  RunsOn ::= runson ClockList
            //
            case 551:
                System.err.println("Rule " + 551 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 552:  ClockValue ::= identifier
            //
            case 552:
                System.err.println("Rule " + 552 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 553:  Primary ::= FutureExpression
            //
            case 553:
                break;
 
            //
            // Rule 554:  Primary ::= Region
            //
            case 554:
                System.err.println("Rule " + 554 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 555:  FutureExpression ::= future PlaceExpressionSingleListopt LBRACE Expression RBRACE
            //
            case 555: {
                Expr e1 = (Expr) btParser.getSym(2),
                     e2 = (Expr) btParser.getSym(4);
                btParser.setSym1(nf.Future(pos(), (e1 == null ? nf.Here(pos(btParser.getFirstToken())) : e1), e2));
                break;
            }
     
            //
            // Rule 556:  FutureExpression ::= future LPAREN here RPAREN LBRACE Expression RBRACE
            //
            case 556: {
                Expr e2 = (Expr) btParser.getSym(6);
                btParser.setSym1(nf.Future(pos(), nf.Here(pos(btParser.getFirstToken(3))), e2));
                break;
            }
     
            //
            // Rule 557:  Statement ::= NowStatement
            //
            case 557:
                break;
 
            //
            // Rule 558:  Statement ::= ClockedStatement
            //
            case 558:
                break;
 
            //
            // Rule 559:  Statement ::= AsyncStatement
            //
            case 559:
                break;
 
            //
            // Rule 560:  Statement ::= AtomicStatement
            //
            case 560:
                break;
 
            //
            // Rule 561:  Statement ::= WhenStatement
            //
            case 561:
                break;
 
            //
            // Rule 562:  Statement ::= AwaitStatement
            //
            case 562:
                break;
 
            //
            // Rule 563:  Statement ::= OneachStatement
            //
            case 563:
                break;
 
            //
            // Rule 564:  StatementWithoutTrailingSubstatement ::= NextStatement
            //
            case 564:
                break;
 
            //
            // Rule 565:  StatementWithoutTrailingSubstatement ::= ContinueStatement
            //
            case 565:
                break;
 
            //
            // Rule 566:  StatementWithoutTrailingSubstatement ::= DropStatement
            //
            case 566:
                break;
 
            //
            // Rule 567:  EnhancedForStatement ::= foreach LPAREN identifierList COLON Range RPAREN Statement
            //
            case 567:
                System.err.println("Rule " + 567 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 568:  EnhancedForStatement ::= ateach LPAREN identifierList COLON Range RPAREN Statement
            //
            case 568:
                System.err.println("Rule " + 568 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 569:  identifierList ::= identifier
            //
            case 569:
                System.err.println("Rule " + 569 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 570:  identifierList ::= identifierList COMMA identifier
            //
            case 570:
                System.err.println("Rule " + 570 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 571:  NowStatement ::= now LPAREN Clock RPAREN Block
            //
            case 571: {
                Name a = (Name) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Now(pos(), a.toExpr(), b));
                break;
            }
     
            //
            // Rule 572:  ClockedStatement ::= clocked LPAREN Clock RPAREN Block
            //
            case 572: {
                Name a = (Name) btParser.getSym(3);
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Clocked(pos(), a.toExpr(), b));
                break;
            }
     
            //
            // Rule 573:  AsyncStatement ::= async PlaceExpressionSingleListopt Block
            //
            case 573: {
                Expr e = (Expr) btParser.getSym(2);
                Block b = (Block) btParser.getSym(3);
                btParser.setSym1(nf.Async(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 574:  AsyncStatement ::= async LPAREN here RPAREN Block
            //
            case 574: {
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Async(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 575:  AtomicStatement ::= atomic PlaceExpressionSingleListopt Block
            //
            case 575: {
                Expr e = (Expr) btParser.getSym(2);
                Block b = (Block) btParser.getSym(3);
                btParser.setSym1(nf.Atomic(pos(), (e == null ? nf.Here(pos(btParser.getFirstToken())) : e), b));
                break;
            }
     
            //
            // Rule 576:  AtomicStatement ::= atomic LPAREN here RPAREN Block
            //
            case 576: {
                Block b = (Block) btParser.getSym(5);
                btParser.setSym1(nf.Atomic(pos(), nf.Here(pos(btParser.getFirstToken())), b));
                break;
            }
     
            //
            // Rule 577:  WhenStatement ::= when LPAREN Expression RPAREN LBRACE Statement RBRACE
            //
            case 577: {
                Expr e = (Expr) btParser.getSym(3);
                Stmt s = (Stmt) btParser.getSym(6);

                List exprs = new TypedList(new LinkedList(), Expr.class, false);
                exprs.add(e);
                List stmts = new TypedList(new LinkedList(), Stmt.class, false);
                stmts.add(s);
                btParser.setSym1(nf.When(pos(), exprs, stmts));
                break;
            }
     
            //
            // Rule 578:  WhenStatement ::= WhenStatement or LPAREN Expression RPAREN LBRACE Statement RBRACE
            //
            case 578: {
                When w = (When) btParser.getSym(1);
                Expr e = (Expr) btParser.getSym(4);
                Stmt s = (Stmt) btParser.getSym(7);
                btParser.setSym1((w.append(e)).append(s));
                break;
            }
     
            //
            // Rule 583:  PlaceExpressionList ::= LPAREN PlaceExpressions RPAREN
            //
            case 583:
                System.err.println("Rule " + 583 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 584:  PlaceExpressionSingleList ::= LPAREN PlaceExpression RPAREN
            //
            case 584: {
                btParser.setSym1(btParser.getSym(2));
                break;
            }
     
            //
            // Rule 585:  PlaceExpressions ::= PlaceExpression
            //
            case 585:
                System.err.println("Rule " + 585 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 586:  PlaceExpressions ::= PlaceExpressions COMMA PlaceExpression
            //
            case 586:
                System.err.println("Rule " + 586 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 587:  NextStatement ::= next ClockList SEMICOLON
            //
            case 587: {
                List l = (List) btParser.getSym(2);
                btParser.setSym1(nf.Next(pos(), l));
                break;
            }
     
            //
            // Rule 588:  DropStatement ::= drop ClockList SEMICOLON
            //
            case 588: {
                List l = (List) btParser.getSym(2);
                btParser.setSym1(nf.Drop(pos(), l));
                break;
            }
     
            //
            // Rule 589:  ClockList ::= Clock
            //
            case 589: {
                Name c = (Name) btParser.getSym(1);
                List l = new TypedList(new LinkedList(), String.class, false);
                l.add(c.toExpr());
                btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 590:  ClockList ::= ClockList COMMA Clock
            //
            case 590: {
                List l = (List) btParser.getSym(1);
                Name c = (Name) btParser.getSym(3);
                l.add(c.toExpr());
                // btParser.setSym1(l);
                break;
            }
     
            //
            // Rule 591:  Clock ::= identifier
            //
            case 591: {
                polyglot.lex.Identifier a = id(btParser.getToken(1));
                btParser.setSym1(new Name(nf, ts, pos(), a.getIdentifier()));
                break;
            }
     
            //
            // Rule 592:  PlaceExpression ::= Expression
            //
            case 592:
                break;
 
            //
            // Rule 593:  PlaceExpression ::= Expression DOT place
            //
            case 593:
                System.err.println("Rule " + 593 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 596:  ReductionMethod ::= identifier
            //
            case 596:
                System.err.println("Rule " + 596 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 597:  ReductionMethod ::= PLUS
            //
            case 597:
                System.err.println("Rule " + 597 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 598:  ReductionMethod ::= MULTIPLY
            //
            case 598:
                System.err.println("Rule " + 598 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 600:  ArrayInitializer ::= Argumentopt Block
            //
            case 600:
                System.err.println("Rule " + 600 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 601:  Argument ::= LPAREN FormalParameter RPAREN
            //
            case 601:
                System.err.println("Rule " + 601 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 602:  Region ::= MinRange Expression Strideopt
            //
            case 602:
                System.err.println("Rule " + 602 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 603:  Region ::= LBRACKET RangeListopt RBRACKET
            //
            case 603:
                System.err.println("Rule " + 603 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 604:  RangeList ::= Range
            //
            case 604:
                System.err.println("Rule " + 604 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 605:  RangeList ::= RangeList COMMA Range
            //
            case 605:
                System.err.println("Rule " + 605 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 606:  Range ::= MinRangeopt Expression Strideopt
            //
            case 606:
                System.err.println("Rule " + 606 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 607:  MinRange ::= Expression RANGE
            //
            case 607:
                System.err.println("Rule " + 607 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 608:  Stride ::= COLON Expression
            //
            case 608:
                System.err.println("Rule " + 608 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 609:  ArrayAccess ::= ExpressionName LBRACKET ExpressionList RBRACKET
            //
            case 609:
                System.err.println("Rule " + 609 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 610:  ArrayAccess ::= PrimaryNoNewArray LBRACKET ExpressionList RBRACKET
            //
            case 610:
                System.err.println("Rule " + 610 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 611:  ExpressionList ::= Expression
            //
            case 611:
                System.err.println("Rule " + 611 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 612:  ExpressionList ::= ExpressionList COMMA Expression
            //
            case 612:
                System.err.println("Rule " + 612 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 613:  PlaceTypeSpecifieropt ::=
            //
            case 613:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 614:  PlaceTypeSpecifieropt ::= PlaceTypeSpecifier
            //
            case 614:
                System.err.println("Rule " + 614 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 615:  RunsAtopt ::=
            //
            case 615:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 616:  RunsAtopt ::= RunsAt
            //
            case 616:
                System.err.println("Rule " + 616 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 617:  RunsOnopt ::=
            //
            case 617:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 618:  RunsOnopt ::= RunsOn
            //
            case 618:
                System.err.println("Rule " + 618 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 619:  PlaceExpressionSingleListopt ::=
            //
            case 619:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 620:  PlaceExpressionSingleListopt ::= PlaceExpressionSingleList
            //
            case 620:
                break;
 
            //
            // Rule 621:  PlaceExpressionListopt ::=
            //
            case 621:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 622:  PlaceExpressionListopt ::= PlaceExpressionList
            //
            case 622:
                System.err.println("Rule " + 622 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 623:  RangeListopt ::=
            //
            case 623:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 624:  RangeListopt ::= RangeList
            //
            case 624:
                System.err.println("Rule " + 624 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 625:  Strideopt ::=
            //
            case 625:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 626:  Strideopt ::= Stride
            //
            case 626:
                System.err.println("Rule " + 626 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 627:  ArrayInitializeropt ::=
            //
            case 627:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 628:  ArrayInitializeropt ::= ArrayInitializer
            //
            case 628:
                System.err.println("Rule " + 628 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 629:  Argumentopt ::=
            //
            case 629:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 630:  Argumentopt ::= Argument
            //
            case 630:
                System.err.println("Rule " + 630 + " not yet implemented");
                actions_stopped = true;
                break;
 
            //
            // Rule 631:  MinRangeopt ::=
            //
            case 631:
                btParser.setSym1(null);
                break;
 
            //
            // Rule 632:  MinRangeopt ::= MinRange
            //
            case 632:
                System.err.println("Rule " + 632 + " not yet implemented");
                actions_stopped = true;
                break;
    
            default:
                break;
        }
        return;
    }
}

