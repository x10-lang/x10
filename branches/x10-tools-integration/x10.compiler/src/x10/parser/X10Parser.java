/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.parser;

import lpg.runtime.*;

//#line 32 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

import polyglot.types.QName;
import polyglot.types.Name;
import polyglot.ast.AmbExpr;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Case;
import polyglot.ast.Catch;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FloatLit;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.SwitchElement;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.FlagsNode;
import polyglot.parse.ParsedName;
import x10.ast.AddFlags;
import x10.ast.AnnotationNode;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.Here;
import x10.ast.DepParameterExpr;
import x10.ast.Tuple;
import x10.ast.When;
import x10.ast.X10Formal;
import x10.ast.X10Formal_c;
import x10.ast.X10Loop;
import x10.ast.X10Call;
import x10.ast.ConstantDistMaker;
import x10.ast.TypeDecl;
import x10.ast.TypeParamNode;
import x10.ast.X10NodeFactory;
import x10.types.ParameterType;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.ast.PropertyDecl;
import x10.ast.RegionMaker;
import x10.ast.X10Binary_c;
import x10.ast.X10Unary_c;
import x10.ast.X10IntLit_c;
import x10.ast.X10NodeFactory_c;
import x10.extension.X10Ext;
import polyglot.frontend.FileSource;
import polyglot.frontend.Parser;
import polyglot.lex.BooleanLiteral;
import polyglot.lex.CharacterLiteral;
import polyglot.lex.DoubleLiteral;
import polyglot.lex.FloatLiteral;
import polyglot.lex.Identifier;
import polyglot.lex.LongLiteral;
import polyglot.lex.NullLiteral;
import polyglot.lex.Operator;
import polyglot.lex.StringLiteral;
import polyglot.parse.VarDeclarator;
import polyglot.types.Flags;
import x10.types.X10Flags;
import x10.types.checker.Converter;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.util.CollectionUtil;

import lpg.runtime.BacktrackingParser;
import lpg.runtime.BadParseException;
import lpg.runtime.BadParseSymFileException;
import lpg.runtime.DiagnoseParser;
import lpg.runtime.IToken;
import lpg.runtime.NotBacktrackParseTableException;
import lpg.runtime.NullExportedSymbolsException;
import lpg.runtime.NullTerminalSymbolsException;
import lpg.runtime.ParseTable;
import lpg.runtime.PrsStream;
import lpg.runtime.RuleAction;
import lpg.runtime.UndefinedEofSymbolException;
import lpg.runtime.UnimplementedTerminalsException;

public class X10Parser implements RuleAction, Parser, ParseErrorCodes
{
    private PrsStream prsStream = null;
    
    private boolean unimplementedSymbolsWarning = false;

    private static ParseTable prsTable = new X10Parserprs();
    public ParseTable getParseTable() { return prsTable; }

    private BacktrackingParser btParser = null;
    public BacktrackingParser getParser() { return btParser; }

    private void setResult(Object object) { btParser.setSym1(object); }
    public Object getRhsSym(int i) { return btParser.getSym(i); }

    public int getRhsTokenIndex(int i) { return btParser.getToken(i); }
    public IToken getRhsIToken(int i) { return prsStream.getIToken(getRhsTokenIndex(i)); }
    
    public int getRhsFirstTokenIndex(int i) { return btParser.getFirstToken(i); }
    public IToken getRhsFirstIToken(int i) { return prsStream.getIToken(getRhsFirstTokenIndex(i)); }

    public int getRhsLastTokenIndex(int i) { return btParser.getLastToken(i); }
    public IToken getRhsLastIToken(int i) { return prsStream.getIToken(getRhsLastTokenIndex(i)); }

    public int getLeftSpan() { return btParser.getFirstToken(); }
    public IToken getLeftIToken()  { return prsStream.getIToken(getLeftSpan()); }

    public int getRightSpan() { return btParser.getLastToken(); }
    public IToken getRightIToken() { return prsStream.getIToken(getRightSpan()); }

    public int getRhsErrorTokenIndex(int i)
    {
        int index = btParser.getToken(i);
        IToken err = prsStream.getIToken(index);
        return (err instanceof ErrorToken ? index : 0);
    }
    public ErrorToken getRhsErrorIToken(int i)
    {
        int index = btParser.getToken(i);
        IToken err = prsStream.getIToken(index);
        return (ErrorToken) (err instanceof ErrorToken ? err : null);
    }

    public void reset(ILexStream lexStream)
    {
        prsStream = new PrsStream(lexStream);
        btParser.reset(prsStream);

        try
        {
            prsStream.remapTerminalSymbols(orderedTerminalSymbols(), prsTable.getEoftSymbol());
        }
        catch(NullExportedSymbolsException e) {
        }
        catch(NullTerminalSymbolsException e) {
        }
        catch(UnimplementedTerminalsException e)
        {
            if (unimplementedSymbolsWarning) {
                java.util.ArrayList unimplemented_symbols = e.getSymbols();
                System.out.println("The Lexer will not scan the following token(s):");
                for (int i = 0; i < unimplemented_symbols.size(); i++)
                {
                    Integer id = (Integer) unimplemented_symbols.get(i);
                    System.out.println("    " + X10Parsersym.orderedTerminalSymbols[id.intValue()]);               
                }
                System.out.println();
            }
        }
        catch(UndefinedEofSymbolException e)
        {
            throw new Error(new UndefinedEofSymbolException
                                ("The Lexer does not implement the Eof symbol " +
                                 X10Parsersym.orderedTerminalSymbols[prsTable.getEoftSymbol()]));
        } 
    }
    
    public X10Parser()
    {
        try
        {
            btParser = new BacktrackingParser(prsStream, prsTable, (RuleAction) this);
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
    }
    
    public X10Parser(ILexStream lexStream)
    {
        this();
        reset(lexStream);
    }
    
    public int numTokenKinds() { return X10Parsersym.numTokenKinds; }
    public String[] orderedTerminalSymbols() { return X10Parsersym.orderedTerminalSymbols; }
    public String getTokenKindName(int kind) { return X10Parsersym.orderedTerminalSymbols[kind]; }
    public int getEOFTokenKind() { return prsTable.getEoftSymbol(); }
    public IPrsStream getIPrsStream() { return prsStream; }

    /**
     * @deprecated replaced by {@link #getIPrsStream()}
     *
     */
    public PrsStream getPrsStream() { return prsStream; }

    /**
     * @deprecated replaced by {@link #getIPrsStream()}
     *
     */
    public PrsStream getParseStream() { return prsStream; }

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
        btParser.setMonitor(monitor);
        
        try
        {
            return (polyglot.ast.Node) btParser.fuzzyParse(error_repair_count);
        }
        catch (BadParseException e)
        {
            prsStream.reset(e.error_token); // point to error token

            DiagnoseParser diagnoseParser = new DiagnoseParser(prsStream, prsTable);
            diagnoseParser.diagnose(e.error_token);
        }

        return null;
    }

    //
    // Additional entry points, if any
    //
    

    //#line 312 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
    private ErrorQueue eq;
    private X10TypeSystem ts;
    private X10NodeFactory nf;
    private FileSource source;
    private boolean unrecoverableSyntaxError = false;

    public void initialize(TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
    {
        this.ts = (X10TypeSystem) t;
        this.nf = (X10NodeFactory) n;
        this.source = source;
        this.eq = q;
    }
    
    public X10Parser(ILexStream lexStream, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
    {
        this(lexStream);
        initialize((X10TypeSystem) t,
                   (X10NodeFactory) n,
                   source,
                   q);
        prsStream.setMessageHandler(new MessageHandler(q));
    }

public static class MessageHandler implements IMessageHandler {
ErrorQueue eq;

public MessageHandler(ErrorQueue eq) {
    this.eq = eq;
}

public void handleMessage(int errorCode, int[] msgLocation,
                          int[] errorLocation, String filename,
                          String[] errorInfo) {

    File file = new File(filename);

    int l0 = msgLocation[2];
    int c0 = msgLocation[3];
    int l1 = msgLocation[4];
    int c1 = msgLocation[5];
    int o0 = msgLocation[0];
    int o1 = msgLocation[0] + msgLocation[1];

    Position pos = new JPGPosition(file.getPath(),
                file.getPath(), l0, c0, l1, c1+1, o0, o1);

    String msg = "";
    String info = "";

    for (String s : errorInfo) {
        info += s;
    }

    switch (errorCode) {
        case LEX_ERROR_CODE:
            msg = "Unexpected character ignored: " + info;
            break;
        case ERROR_CODE:
            msg = "Parse terminated at this token: " + info;
            break;
        case BEFORE_CODE:
            msg = "Token " + info + " expected before this input";
            break;
        case INSERTION_CODE:
            msg = "Token " + info + " expected after this input";
            break;
        case INVALID_CODE:
            msg = "Unexpected input discarded: " + info;
            break;
        case SUBSTITUTION_CODE:
            msg = "Token " + info + " expected instead of this input";
            break;
        case DELETION_CODE:
            msg = "Unexpected input ignored: " + info;
            break;
        case MERGE_CODE:
            msg = "Merging token(s) to recover: " + info;
            break;
        case MISPLACED_CODE:
            msg = "Misplaced constructs(s): " + info;
            break;
        case SCOPE_CODE:
            msg = "Token(s) inserted to complete scope: " + info;
            break;
        case EOF_CODE:
            msg = "Reached after this token: " + info;
            break;
        case INVALID_TOKEN_CODE:
            msg = "Invalid token: " + info;
            break;
        case ERROR_RULE_WARNING_CODE:
            msg = "Ignored token: " + info;
            break;
        case NO_MESSAGE_CODE:
            msg = "Syntax error";
            break;
    }

    // FIXME: HACK! Prepend "Syntax error: " until we figure out how to get Polyglot to do it for us.
    if (errorCode != NO_MESSAGE_CODE) { msg = "Syntax error: " + msg; }

    eq.enqueue(ErrorInfo.SYNTAX_ERROR, msg, pos);
}
}

    public String getErrorLocation(int lefttok, int righttok)
    {
        return prsStream.getFileName() + ':' +
               prsStream.getLine(lefttok) + ":" + prsStream.getColumn(lefttok) + ":" +
               prsStream.getEndLine(righttok) + ":" + prsStream.getEndColumn(righttok) + ": ";
    }

    public Position getErrorPosition(int lefttok, int righttok)
    {
        return new JPGPosition(null, prsStream.getFileName(),
               prsStream.getIToken(lefttok), prsStream.getIToken(righttok));
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
                  rightToken.getEndLine(), rightToken.getEndColumn(),
                  leftToken.getStartOffset(), rightToken.getEndOffset());
            this.leftIToken = null; // BRT -- was null, need to keep leftToken for later reference
            this.rightIToken = null;  // BRT -- was null, need to keep rightToken for later reference
        }

        public JPGPosition(Position start, Position end)
        {
            super(start, end);
            this.leftIToken = (start instanceof JPGPosition) ? ((JPGPosition)start).leftIToken : null;
            this.rightIToken = (end instanceof JPGPosition) ? ((JPGPosition)end).rightIToken : null;
        }

        JPGPosition(String path, String filename, int line, int column, int endLine, int endColumn, int offset, int endOffset)
        {
            super(path, filename, line, column, endLine, endColumn, offset, endOffset);
            this.leftIToken = null;
            this.rightIToken = null;
        }

        private JPGPosition() {
            super(null, "Compiler Generated");
            this.leftIToken = null;
            this.rightIToken = null;
        }
        public static final JPGPosition COMPILER_GENERATED = (JPGPosition)(new JPGPosition().markCompilerGenerated());

        public IToken getLeftIToken() { return leftIToken; }
        public IToken getRightIToken() { return rightIToken; }

        public String toText()
        {
            if (leftIToken == null) return "...";
            IPrsStream prsStream = leftIToken.getIPrsStream();
            return new String(prsStream.getInputChars(), offset(), endOffset() - offset() + 1);
        }
    }
    
    public void syntaxError(String msg, Position pos) {
                unrecoverableSyntaxError = true;
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, msg, pos);
            }   
    

    public polyglot.ast.Node parse() {
        try
        {
            SourceFile sf = (SourceFile) parser();

            if (sf != null)
            {
                if (! unrecoverableSyntaxError)
                    return sf.source(source);
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, "Unable to parse " + source.name() + ".", new JPGPosition(null, file(), 1, 1, 1, 1, 0, 0).markCompilerGenerated());
            }   
        }
        catch (RuntimeException e) {
            // Let the Compiler catch and report it.
            throw e;
        }
        catch (Exception e) {
            // Used by cup to indicate a non-recoverable error.
            eq.enqueue(ErrorInfo.SYNTAX_ERROR, e.getMessage(), new JPGPosition(null, file(), 1, 1, 1, 1, 0, 0).markCompilerGenerated());
        }

        return null;
    }

    public String file()
    {
        return prsStream.getFileName();
    }

    public JPGPosition pos()
    {
        return new JPGPosition("",
                               prsStream.getFileName(),
                               prsStream.getIToken(getLeftSpan()),
                               prsStream.getIToken(getRightSpan()));
    }

    public JPGPosition pos(int i)
    {
        return new JPGPosition("",
                               prsStream.getFileName(),
                               prsStream.getIToken(i),
                               prsStream.getIToken(i));
    }

    public JPGPosition pos(int i, int j)
    {
        return new JPGPosition("",
                               prsStream.getFileName(),
                               prsStream.getIToken(i),
                               prsStream.getIToken(j));
    }

    /**
     * Return the source position of the declaration.
     */
    public JPGPosition pos (VarDeclarator n)
    {
      if (n == null) return null;
      return (JPGPosition) n.pos;
    }

    public JPGPosition pos(JPGPosition start, JPGPosition end) {
        return new JPGPosition(start.path(), start.file(), start.leftIToken, end.rightIToken);
    }

    private void checkTypeName(Id identifier) {
        String filename = file();
        String idname = identifier.id().toString();
        int dot = filename.lastIndexOf('.'),
            slash = filename.lastIndexOf('/', dot);
        if (slash == -1)
            slash = filename.lastIndexOf('\\', dot);
        String clean_filename = (slash >= 0 && dot >= 0 ? filename.substring(slash+1, dot) : "");
        if ((! clean_filename.equals(idname)) && clean_filename.equalsIgnoreCase(idname))
            eq.enqueue(ErrorInfo.SYNTAX_ERROR,
                       "This type name does not match the name of the containing file: " + filename.substring(slash+1),
                       identifier.position());
   }


    private polyglot.lex.Operator op(int i) {
        return new Operator(pos(i), prsStream.getName(i), prsStream.getKind(i));
    }

    private polyglot.lex.Identifier id(int i) {
        return new Identifier(pos(i), prsStream.getName(i), X10Parsersym.TK_IDENTIFIER);
    }
    private String comment(int i) {
        IToken[] adjuncts = prsStream.getTokenAt(i).getPrecedingAdjuncts();
        String s = null;
        for (IToken a : adjuncts) {
            String c = a.toString();
            if (c.startsWith("/**") && c.endsWith("*/")) {
                s = c;
            }
        }
        return s;
    }

    private List<Formal> toFormals(List<Formal> l) { return l; }

    private List<Expr> toActuals(List<Formal> l) {
        List<Expr> l2 = new ArrayList<Expr>();
        for (Formal f : l) {
            l2.add(nf.Local(f.position(), f.name()));
        }
        return l2;
    }

    private List<TypeParamNode> toTypeParams(List<TypeParamNode> l) { return l; }

    private List<TypeNode> toTypeArgs(List<TypeParamNode> l) {
        List<TypeNode> l2 = new ArrayList<TypeNode>();
        for (TypeParamNode f : l) {
            l2.add(nf.AmbTypeNode(f.position(), null, f.name()));
        }
        return l2;
    }

            
    private List extractAnnotations(List l) {
        List l2 = new LinkedList();
        for (Iterator i = l.iterator(); i.hasNext(); ) {
            Object o = i.next();
            if (o instanceof AnnotationNode) {
                l2.add((AnnotationNode) o);
            }
        }
        return l2;
    }

    private FlagsNode extractFlags(List l, Flags f) {
        FlagsNode fn = extractFlags(l);
        fn = fn.flags(fn.flags().set(f));
        return fn;
    }
    
    private FlagsNode extractFlags(List l1, List l2) {
        List l = new ArrayList();
        l.addAll(l1);
        l.addAll(l2);
        return extractFlags(l);
    }
    
    private FlagsNode extractFlags(List l) {
        Position pos = null;
        X10Flags xf = X10Flags.toX10Flags(Flags.NONE);
        for (Iterator i = l.iterator(); i.hasNext(); ) {
            Object o = i.next();
            if (o instanceof FlagsNode) {
                FlagsNode fn = (FlagsNode) o;
                pos = pos == null ? fn.position() : new JPGPosition(pos, fn.position());
                Flags f = fn.flags();
                if (f instanceof X10Flags) {
                    xf = xf.setX((X10Flags) f);
                }
                else {
                    xf = X10Flags.toX10Flags(xf.set(f));
                }
            }
        }
        return nf.FlagsNode(pos == null ? JPGPosition.COMPILER_GENERATED : pos, xf);
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
        int radix;
        int start_index;
        int end_index;
        
        end_index = s.length();

        while (end_index > 0) {
            char lastCh = s.charAt(end_index - 1);
            if (lastCh != 'l' && lastCh != 'L' && lastCh != 'u' && lastCh != 'U') {
                    break;
            }
            end_index--;
        }

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

    private polyglot.lex.LongLiteral int_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i),  x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.LongLiteral long_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i), x, X10Parsersym.TK_LongLiteral);
    }
    private polyglot.lex.LongLiteral ulong_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i), x, X10Parsersym.TK_UnsignedLongLiteral);
    }
    private polyglot.lex.LongLiteral uint_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i), x, X10Parsersym.TK_UnsignedIntegerLiteral);
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
            unrecoverableSyntaxError = true;
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
            unrecoverableSyntaxError = true;
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
            // Rule 1:  TypeName ::= TypeName . ErrorId
            //
            case 1: {
               //#line 8 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 6 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      TypeName,
                                      nf.Id(pos(getRightSpan()), "*")));
                          break;
            }
        
            //
            // Rule 2:  PackageName ::= PackageName . ErrorId
            //
            case 2: {
               //#line 18 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 16 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      PackageName,
                                      nf.Id(pos(getRightSpan()), "*")));
                          break;
            }
        
            //
            // Rule 3:  ExpressionName ::= AmbiguousName . ErrorId
            //
            case 3: {
               //#line 28 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 26 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      nf.Id(pos(getRightSpan()), "*")));
                          break;
            }
        
            //
            // Rule 4:  MethodName ::= AmbiguousName . ErrorId
            //
            case 4: {
               //#line 38 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 36 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      nf.Id(pos(getRightSpan()), "*")));
                          break;
            }
        
            //
            // Rule 5:  PackageOrTypeName ::= PackageOrTypeName . ErrorId
            //
            case 5: {
               //#line 48 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 46 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      PackageOrTypeName,
                                      nf.Id(pos(getRightSpan()), "*")));
                          break;
            }
        
            //
            // Rule 6:  AmbiguousName ::= AmbiguousName . ErrorId
            //
            case 6: {
               //#line 58 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 56 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                    setResult(new ParsedName(nf,
                                      ts,
                                      pos(getLeftSpan(), getRightSpan()),
                                      AmbiguousName,
                                      nf.Id(pos(getRightSpan()), "*")));
                         break;
            }
        
            //
            // Rule 7:  FieldAccess ::= Primary . ErrorId
            //
            case 7: {
               //#line 68 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 66 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 74 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 78 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 85 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 92 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 98 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName ClassName = (ParsedName) ((Object[]) MethodClassNameSuperPrefix)[0];
                JPGPosition super_pos = (JPGPosition) ((Object[]) MethodClassNameSuperPrefix)[1];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodClassNameSuperPrefix)[2];
                setResult(nf.Call(pos(), nf.Super(super_pos, ClassName.toType()), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 13:  MethodPrimaryPrefix ::= Primary . ErrorId$ErrorId
            //
            case 13: {
               //#line 109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 107 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Object[] a = new Object[2];
                a[0] = Primary;
                a[1] = id(getRhsFirstTokenIndex(3));
                setResult(a);
                      break;
            }
    
            //
            // Rule 14:  MethodSuperPrefix ::= super . ErrorId$ErrorId
            //
            case 14: {
               //#line 117 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 115 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 120 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Object[] a = new Object[3];
                a[0] = ClassName;
                a[1] = pos(getRhsFirstTokenIndex(3));
                a[2] = id(getRhsFirstTokenIndex(5));
                setResult(a);
                      break;
            }
    
            //
            // Rule 16:  TypeDefDeclaration ::= TypeDefModifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 16: {
               //#line 909 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 907 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 907 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 907 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 907 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 907 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 907 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 909 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode f = extractFlags(TypeDefModifiersopt);
                List annotations = extractAnnotations(TypeDefModifiersopt);
                for (Formal v : (List<Formal>) FormalParametersopt) {
                    if (!v.flags().flags().isFinal()) syntaxError("Type definition parameters must be final.", v.position());
                }
                TypeDecl cd = nf.TypeDecl(pos(), f, Identifier, TypeParametersopt, FormalParametersopt, WhereClauseopt, Type);
                cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 17:  Properties ::= ( PropertyList )
            //
            case 17: {
               //#line 922 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 920 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 922 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 18:  PropertyList ::= Property
            //
            case 18: {
               //#line 927 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 925 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 927 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 19:  PropertyList ::= PropertyList , Property
            //
            case 19: {
               //#line 934 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 932 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 932 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 934 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 20:  Property ::= Annotationsopt Identifier ResultType
            //
            case 20: {
               //#line 941 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 939 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 939 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 939 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 941 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List annotations = extractAnnotations(Annotationsopt);
                PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), ResultType, Identifier);
                cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 21:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 21: {
               //#line 950 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 950 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       ProcedureDecl pd;
       if (Identifier.id().toString().equals("this")) {
                   pd = nf.X10ConstructorDecl(pos(),
                                             extractFlags(MethodModifiersopt),
                                             Identifier,
                                             HasResultTypeopt,
                                             TypeParametersopt,
                                             FormalParameters,
                                             WhereClauseopt,
                                             Throwsopt,
                                             Offersopt,
                                             MethodBody);

          }
          else {
       pd = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          Identifier,
          TypeParametersopt,
          FormalParameters,
          WhereClauseopt,
          Throwsopt,
          Offersopt,
          MethodBody);
      }
      pd = (ProcedureDecl) ((X10Ext) pd.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(pd);
                      break;
            }
    
            //
            // Rule 22:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 22: {
               //#line 982 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(14);
                //#line 980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(15);
                //#line 982 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(7)), X10Binary_c.binaryMethodName(BinOp)),
          TypeParametersopt,
          Arrays.<Formal>asList(fp1, fp2),
          WhereClauseopt,
          Throwsopt,
          Offersopt,
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Binary operator with two parameters must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 23:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 23: {
               //#line 1000 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1000 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(4)), X10Unary_c.unaryMethodName(PrefixOp)),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp2),
          WhereClauseopt,
          Throwsopt,
          Offersopt,
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Unary operator with two parameters must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 24:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 24: {
               //#line 1018 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1018 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(5)), X10Binary_c.binaryMethodName(BinOp)),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp2),
          WhereClauseopt,
          Throwsopt,
          Offersopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Binary operator with this parameter cannot be static.", md.position());
          
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 25:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 25: {
               //#line 1037 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1037 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       Name op = X10Binary_c.invBinaryMethodName(BinOp);
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(7)), X10Binary_c.invBinaryMethodName(BinOp)),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp1),
          WhereClauseopt,
          Throwsopt,
          Offersopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Binary operator with this parameter cannot be static.", md.position());
          
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 26:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 26: {
               //#line 1057 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1055 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1055 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1055 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1055 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1055 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1055 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1055 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1055 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1057 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(4)), X10Unary_c.unaryMethodName(PrefixOp)),
          TypeParametersopt,
          Collections.EMPTY_LIST,
          WhereClauseopt,
          Throwsopt,
          Offersopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Unary operator with this parameter cannot be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 27:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 27: {
               //#line 1075 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1075 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(), Name.make("apply")),
          TypeParametersopt,
          FormalParameters,
          WhereClauseopt,
          Throwsopt,
          Offersopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Apply operator cannot be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 28:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 28: {
               //#line 1093 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1091 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1091 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1091 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1091 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1091 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1091 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1091 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1091 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(13);
                //#line 1091 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 1093 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(), Name.make("set")),
          TypeParametersopt,
          CollectionUtil.append(Collections.singletonList(fp2), FormalParameters),
          WhereClauseopt,
          Throwsopt,
          Offersopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Set operator cannot be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 29:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Throwsopt Offersopt MethodBody
            //
            case 29: {
               //#line 1111 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1111 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          Type,
          nf.Id(pos(), Converter.operator_as),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp1),
          WhereClauseopt,
          Throwsopt,
          Offersopt, 
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Conversion operator must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 30:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 30: {
               //#line 1129 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1129 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(), Converter.operator_as),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp1),
          WhereClauseopt,
          Throwsopt,
          Offersopt, 
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Conversion operator must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 31:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 31: {
               //#line 1147 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(9);
                //#line 1145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1147 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(), Converter.implicit_operator_as),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp1),
          WhereClauseopt,
          Throwsopt,
          Offersopt,
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Conversion operator must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 32:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
            //
            case 32: {
               //#line 1166 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(8);
                //#line 1166 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt, X10Flags.PROPERTY),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          Identifier,
          TypeParametersopt,
          FormalParameters,
          WhereClauseopt,
          Collections.EMPTY_LIST,
          null, // offersOpt
          MethodBody);
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 33:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 33: {
               //#line 1182 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1180 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1180 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1180 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(4);
                //#line 1180 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 1180 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 1182 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt, X10Flags.PROPERTY),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          Identifier,
          Collections.EMPTY_LIST,
          Collections.EMPTY_LIST,
          WhereClauseopt,
          Collections.EMPTY_LIST,
          null, // offersOpt
          MethodBody);
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 34:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 34: {
               //#line 1199 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1197 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1197 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1199 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 35:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 35: {
               //#line 1204 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1202 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1202 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1204 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 36:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 36: {
               //#line 1209 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1207 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1207 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1207 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1209 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 37:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 37: {
               //#line 1214 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1212 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1212 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1212 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1214 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 38:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 38: {
               //#line 1220 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 1218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1220 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
      checkTypeName(Identifier);
      List TypeParametersopt = TypeParamsWithVarianceopt;
      List/*<PropertyDecl>*/ props = Propertiesopt;
      DepParameterExpr ci = WhereClauseopt;
      FlagsNode fn = extractFlags(InterfaceModifiersopt, Flags.INTERFACE);
      ClassDecl cd = nf.X10ClassDecl(pos(),
                   fn,
                   Identifier,
                   TypeParametersopt,
                   props,
                   ci,
                   null,
                   ExtendsInterfacesopt,
                   InterfaceBody);
      cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(InterfaceModifiersopt));
      setResult(cd);
                      break;
            }
    
            //
            // Rule 39:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 39: {
               //#line 1241 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1239 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1239 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1239 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1239 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1241 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 40:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 40: {
               //#line 1248 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1246 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1246 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1246 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1246 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1246 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1248 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 41:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 41: {
               //#line 1256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1254 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1254 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1254 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1254 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1254 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 42:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 42: {
               //#line 1265 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1263 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1263 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1265 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 45:  Type ::= proto ConstrainedType
            //
            case 45: {
               //#line 1274 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1272 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ConstrainedType = (TypeNode) getRhsSym(2);
                //#line 1274 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
        AddFlags tn = (AddFlags) ConstrainedType;
        tn.addFlags(X10Flags.PROTO);
        setResult(ConstrainedType.position(pos()));
                      break;
            }
    
            //
            // Rule 46:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt Offersopt => Type
            //
            case 46: {
               //#line 1282 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1280 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1280 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1280 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1280 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1280 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(7);
                //#line 1280 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(9);
                //#line 1282 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt, Offersopt));
                      break;
            }
    
            //
            // Rule 51:  AnnotatedType ::= Type Annotations
            //
            case 51: {
               //#line 1291 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1289 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1289 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1291 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn.position(pos()));
                      break;
            }
    
            //
            // Rule 54:  ConstrainedType ::= ( Type )
            //
            case 54: {
               //#line 1301 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1299 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1301 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 56:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 56: {
               //#line 1315 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1313 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1313 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1313 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1313 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1313 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1315 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbTypeNode(pos(), Primary, Identifier);
            // TODO: place constraint
            if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                type = nf.AmbDepTypeNode(pos(), Primary, Identifier, TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                      break;
            }
    
            //
            // Rule 57:  NamedType ::= TypeName TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 57: {
               //#line 1326 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1324 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1324 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1324 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1324 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1326 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type;
            
            if (TypeName.name.id().toString().equals("void")) {
                type = nf.CanonicalTypeNode(pos(), ts.Void());
            } else {
                type = TypeName.toType();
            }
            // TODO: place constraint
            if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                type = nf.AmbDepTypeNode(pos(), TypeName.prefix != null ? TypeName.prefix.toPrefix() : null, TypeName.name, TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                      break;
            }
    
            //
            // Rule 58:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 58: {
               //#line 1344 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1342 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1342 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object Conjunctionopt = (Object) getRhsSym(3);
                //#line 1344 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, (List) Conjunctionopt));
                      break;
            }
    
            //
            // Rule 59:  DepParameters ::= ! PlaceType
            //
            case 59: {
               //#line 1349 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1347 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1349 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 60:  DepParameters ::= !
            //
            case 60: {
               //#line 1355 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1355 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 61:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 61: {
               //#line 1361 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1359 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1359 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(4);
                //#line 1359 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(5);
                //#line 1361 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 62:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 62: {
               //#line 1367 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1365 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(3);
                //#line 1365 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(4);
                //#line 1367 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 63:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 63: {
               //#line 1375 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1373 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1375 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 64:  TypeParameters ::= [ TypeParameterList ]
            //
            case 64: {
               //#line 1381 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1379 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1381 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 65:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 65: {
               //#line 1386 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1384 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1386 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 66:  Conjunction ::= Expression
            //
            case 66: {
               //#line 1392 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1390 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1392 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 67:  Conjunction ::= Conjunction , Expression
            //
            case 67: {
               //#line 1399 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1397 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1397 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1399 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 68:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 68: {
               //#line 1405 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1403 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1403 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1405 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 69:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 69: {
               //#line 1410 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1408 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1408 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1410 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 70:  WhereClause ::= DepParameters
            //
            case 70: {
               //#line 1416 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1414 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1416 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 71:  Conjunctionopt ::= $Empty
            //
            case 71: {
               //#line 1422 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1422 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                setResult(l);
                      break;
            }
      
            //
            // Rule 72:  Conjunctionopt ::= Conjunction
            //
            case 72: {
               //#line 1428 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1426 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1428 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 73:  ExistentialListopt ::= $Empty
            //
            case 73: {
               //#line 1434 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1434 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 74:  ExistentialListopt ::= ExistentialList ;
            //
            case 74: {
               //#line 1439 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1437 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1439 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 75:  ExistentialList ::= FormalParameter
            //
            case 75: {
               //#line 1445 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1443 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1445 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 76:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 76: {
               //#line 1452 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1450 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1450 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1452 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 79:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 79: {
               //#line 1463 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1463 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
      checkTypeName(Identifier);
                List TypeParametersopt = TypeParamsWithVarianceopt;
      List/*<PropertyDecl>*/ props = Propertiesopt;
      DepParameterExpr ci = WhereClauseopt;
      FlagsNode f = extractFlags(ClassModifiersopt);
      List annotations = extractAnnotations(ClassModifiersopt);
      ClassDecl cd = nf.X10ClassDecl(pos(),
              f, Identifier, TypeParametersopt, props, ci, Superopt, Interfacesopt, ClassBody);
      cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(annotations);
      setResult(cd);
                      break;
            }
    
            //
            // Rule 80:  StructDeclaration ::= ClassModifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 80: {
               //#line 1479 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1479 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
    checkTypeName(Identifier);
                List TypeParametersopt = TypeParamsWithVarianceopt;
    List props = Propertiesopt;
    DepParameterExpr ci = WhereClauseopt;
    ClassDecl cd = (nf.X10ClassDecl(pos(getLeftSpan(), getRightSpan()),
    extractFlags(ClassModifiersopt, X10Flags.STRUCT), Identifier,  TypeParametersopt,
    props, ci, null, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ClassModifiersopt));
    setResult(cd);
                      break;
            }
    
            //
            // Rule 81:  ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt ConstructorBody
            //
            case 81: {
               //#line 1493 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(10);
                //#line 1493 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
     ConstructorDecl cd = nf.X10ConstructorDecl(pos(),
                                             extractFlags(ConstructorModifiersopt),
                                             nf.Id(pos(getRhsFirstTokenIndex(3)), "this"),
                                             HasResultTypeopt,
                                             TypeParametersopt,
                                             FormalParameters,
                                             WhereClauseopt,
                                             Throwsopt,
                                             Offersopt,
                                             ConstructorBody);
     cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ConstructorModifiersopt));
     setResult(cd);
                     break;
            }
   
            //
            // Rule 82:  Super ::= extends ClassType
            //
            case 82: {
               //#line 1510 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1508 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1510 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 83:  FieldKeyword ::= val
            //
            case 83: {
               //#line 1516 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1516 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 84:  FieldKeyword ::= var
            //
            case 84: {
               //#line 1521 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1521 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 85:  FieldKeyword ::= const
            //
            case 85: {
               //#line 1526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 86:  VarKeyword ::= val
            //
            case 86: {
               //#line 1534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 87:  VarKeyword ::= var
            //
            case 87: {
               //#line 1539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 88:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 88: {
               //#line 1546 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1546 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                    FlagsNode fn = extractFlags(FieldModifiersopt, FieldKeyword);
    
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    for (Iterator i = FieldDeclarators.iterator(); i.hasNext(); )
                    {
                        Object[] o = (Object[]) i.next();
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        if (name == null) name = nf.Id(pos, Name.makeFresh());
                        List exploded = (List) o[2];
                        TypeNode type = (TypeNode) o[3];
                        if (type == null) type = nf.UnknownTypeNode(name.position());
                        Expr init = (Expr) o[4];
                        FieldDecl fd = nf.FieldDecl(pos, fn,
                                           type, name, init);
                        fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(FieldModifiersopt));
                        fd = (FieldDecl) ((X10Ext) fd.ext()).setComment(comment(getRhsFirstTokenIndex(1)));
                        l.add(fd);
                    }
                setResult(l);
                      break;
            }
    
            //
            // Rule 89:  FieldDeclaration ::= FieldModifiersopt FieldDeclarators ;
            //
            case 89: {
               //#line 1571 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1569 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1569 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1571 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                    List FieldKeyword = Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL));
                    FlagsNode fn = extractFlags(FieldModifiersopt, FieldKeyword);
    
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                    for (Iterator i = FieldDeclarators.iterator(); i.hasNext(); )
                    {
                        Object[] o = (Object[]) i.next();
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        if (name == null) name = nf.Id(pos, Name.makeFresh());
                        List exploded = (List) o[2];
                        TypeNode type = (TypeNode) o[3];
                        if (type == null) type = nf.UnknownTypeNode(name.position());
                        Expr init = (Expr) o[4];
                        FieldDecl fd = nf.FieldDecl(pos, fn,
                                           type, name, init);
                        fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(FieldModifiersopt));
                        fd = (FieldDecl) ((X10Ext) fd.ext()).setComment(comment(getRhsFirstTokenIndex(1)));
                        l.add(fd);
                    }
                setResult(l);
                      break;
            }
    
            //
            // Rule 92:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 92: {
               //#line 1603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1601 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 1601 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 1603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (NonExpressionStatement.ext() instanceof X10Ext && Annotationsopt instanceof List) {
                    NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations((List) Annotationsopt);
                }
                setResult(NonExpressionStatement.position(pos()));
                      break;
            }
    
            //
            // Rule 119:  OfferStatement ::= offer Expression ;
            //
            case 119: {
               //#line 1639 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1637 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1639 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 120:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 120: {
               //#line 1645 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1643 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1643 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1645 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 121:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 121: {
               //#line 1651 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1649 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1649 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1649 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1651 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 122:  EmptyStatement ::= ;
            //
            case 122: {
               //#line 1657 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1657 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 123:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 123: {
               //#line 1663 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1661 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1661 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 1663 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 129:  ExpressionStatement ::= StatementExpression ;
            //
            case 129: {
               //#line 1675 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1673 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1675 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 137:  AssertStatement ::= assert Expression ;
            //
            case 137: {
               //#line 1689 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1687 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1689 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 138:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 138: {
               //#line 1694 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1692 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1692 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1694 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 139:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 139: {
               //#line 1700 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1698 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1698 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1700 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 140:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 140: {
               //#line 1706 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1704 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1704 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1706 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 142:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 142: {
               //#line 1714 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1712 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1712 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1714 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 143:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 143: {
               //#line 1721 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1719 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1719 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1721 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 144:  SwitchLabels ::= SwitchLabel
            //
            case 144: {
               //#line 1730 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1728 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1730 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 145:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 145: {
               //#line 1737 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1735 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1735 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1737 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 146:  SwitchLabel ::= case ConstantExpression :
            //
            case 146: {
               //#line 1744 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1742 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1744 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 147:  SwitchLabel ::= default :
            //
            case 147: {
               //#line 1749 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1749 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 148:  WhileStatement ::= while ( Expression ) Statement
            //
            case 148: {
               //#line 1755 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1753 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1753 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1755 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 149:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 149: {
               //#line 1761 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1759 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1759 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1761 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 152:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 152: {
               //#line 1770 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1768 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1768 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1768 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1768 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1770 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 154:  ForInit ::= LocalVariableDeclaration
            //
            case 154: {
               //#line 1777 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1775 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1777 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 156:  StatementExpressionList ::= StatementExpression
            //
            case 156: {
               //#line 1787 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1785 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1787 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 157:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 157: {
               //#line 1794 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1792 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1792 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1794 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 158:  BreakStatement ::= break Identifieropt ;
            //
            case 158: {
               //#line 1800 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1798 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1800 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 159:  ContinueStatement ::= continue Identifieropt ;
            //
            case 159: {
               //#line 1806 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1804 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1806 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 160:  ReturnStatement ::= return Expressionopt ;
            //
            case 160: {
               //#line 1812 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1810 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1812 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 161:  ThrowStatement ::= throw Expression ;
            //
            case 161: {
               //#line 1818 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1816 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1818 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 162:  TryStatement ::= try Block Catches
            //
            case 162: {
               //#line 1824 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1822 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1822 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1824 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 163:  TryStatement ::= try Block Catchesopt Finally
            //
            case 163: {
               //#line 1829 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1827 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1827 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1827 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1829 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 164:  Catches ::= CatchClause
            //
            case 164: {
               //#line 1835 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1833 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1835 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 165:  Catches ::= Catches CatchClause
            //
            case 165: {
               //#line 1842 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1840 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1840 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1842 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 166:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 166: {
               //#line 1849 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1847 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1847 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1849 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 167:  Finally ::= finally Block
            //
            case 167: {
               //#line 1855 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1853 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1855 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 168:  ClockedClause ::= clocked ( ClockList )
            //
            case 168: {
               //#line 1861 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1859 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1861 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 169:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 169: {
               //#line 1867 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1865 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1865 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1865 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1867 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 170:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 170: {
               //#line 1876 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1874 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1874 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1876 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 171:  AtomicStatement ::= atomic Statement
            //
            case 171: {
               //#line 1882 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1880 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1882 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 172:  WhenStatement ::= when ( Expression ) Statement
            //
            case 172: {
               //#line 1889 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1887 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1887 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1889 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 173:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 173: {
               //#line 1894 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1892 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1892 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1892 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1892 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1894 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 174:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 174: {
               //#line 1901 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1899 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1899 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1899 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1899 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1901 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                Flags f = fn.flags();
                fn = fn.flags(f);
                if (! f.isFinal()) {
                      syntaxError("Enhanced for loop may not have var loop index" + LoopIndex, LoopIndex.position());
                }
                setResult(nf.ForEach(pos(),
                              LoopIndex.flags(fn),
                              Expression,
                              ClockedClauseopt,
                              Statement));
                      break;
            }
    
            //
            // Rule 175:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 175: {
               //#line 1917 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1915 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1915 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1915 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1915 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1917 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                Flags f = fn.flags();
                fn = fn.flags(f);
                if (! f.isFinal()) {
                      syntaxError("Enhanced for loop may not have var loop index" + LoopIndex, LoopIndex.position());
                }
                setResult(nf.AtEach(pos(),
                             LoopIndex.flags(fn),
                             Expression,
                             ClockedClauseopt,
                             Statement));
                      break;
            }
    
            //
            // Rule 176:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 176: {
               //#line 1933 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1931 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1931 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1931 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1933 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                Flags f = fn.flags();
                if (! f.isFinal()) {
                      syntaxError("Enhanced for loop may not have var loop index" + LoopIndex, LoopIndex.position());
                }
                setResult(nf.ForLoop(pos(),
                        LoopIndex.flags(fn),
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 177:  FinishStatement ::= finish Statement
            //
            case 177: {
               //#line 1947 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1945 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1947 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 178:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 178: {
               //#line 1953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 180:  NextStatement ::= next ;
            //
            case 180: {
               //#line 1961 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1961 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 181:  AwaitStatement ::= await Expression ;
            //
            case 181: {
               //#line 1967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1965 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 182:  ClockList ::= Clock
            //
            case 182: {
               //#line 1973 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1971 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1973 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 183:  ClockList ::= ClockList , Clock
            //
            case 183: {
               //#line 1980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1978 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1978 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 184:  Clock ::= Expression
            //
            case 184: {
               //#line 1988 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1986 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1988 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 186:  CastExpression ::= ExpressionName
            //
            case 186: {
               //#line 2002 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2000 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 2002 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 187:  CastExpression ::= CastExpression as Type
            //
            case 187: {
               //#line 2007 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2005 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2005 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2007 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 188:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 188: {
               //#line 2014 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2012 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2014 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 189:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 189: {
               //#line 2021 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2019 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 2019 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2021 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 190:  TypeParameterList ::= TypeParameter
            //
            case 190: {
               //#line 2028 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2028 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 191:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 191: {
               //#line 2035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2033 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 2033 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 192:  TypeParamWithVariance ::= Identifier
            //
            case 192: {
               //#line 2042 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2040 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2042 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 193:  TypeParamWithVariance ::= + Identifier
            //
            case 193: {
               //#line 2047 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2047 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 194:  TypeParamWithVariance ::= - Identifier
            //
            case 194: {
               //#line 2052 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2050 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2052 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 195:  TypeParameter ::= Identifier
            //
            case 195: {
               //#line 2058 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2056 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2058 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 197:  RegionExpressionList ::= RegionExpression
            //
            case 197: {
               //#line 2066 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2064 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2066 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 198:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 198: {
               //#line 2073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2071 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2071 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 199:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 199: {
               //#line 2080 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2078 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2078 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2080 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 200:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt => ClosureBody
            //
            case 200: {
               //#line 2086 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2084 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(1);
                //#line 2084 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2084 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2084 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(4);
                //#line 2084 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(5);
                //#line 2084 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2086 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 201:  LastExpression ::= Expression
            //
            case 201: {
               //#line 2093 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2091 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2093 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 202:  ClosureBody ::= ConditionalExpression
            //
            case 202: {
               //#line 2099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2097 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 2099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), ConditionalExpression, true)));
                      break;
            }
    
            //
            // Rule 203:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 203: {
               //#line 2104 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2102 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2102 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2102 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2104 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 204:  ClosureBody ::= Annotationsopt Block
            //
            case 204: {
               //#line 2114 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2112 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2112 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2114 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 205:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 205: {
               //#line 2123 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2121 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2121 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2123 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 206:  AsyncExpression ::= async ClosureBody
            //
            case 206: {
               //#line 2129 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2129 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 207:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 207: {
               //#line 2134 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2132 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2132 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2134 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 208:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 208: {
               //#line 2139 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2139 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 209:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 209: {
               //#line 2144 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2142 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2142 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2142 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2144 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 210:  FinishExpression ::= finish ( Expression ) Block
            //
            case 210: {
               //#line 2151 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2149 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2149 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2151 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 211:  FutureExpression ::= future ClosureBody
            //
            case 211: {
               //#line 2157 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2157 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 212:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 212: {
               //#line 2162 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2160 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2160 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2162 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 213:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 213: {
               //#line 2167 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2165 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2165 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2167 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 214:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 214: {
               //#line 2172 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2170 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2170 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2170 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2172 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 215:  DepParametersopt ::= $Empty
            //
            case 215:
                setResult(null);
                break;

            //
            // Rule 217:  PropertyListopt ::= $Empty
            //
            case 217: {
               //#line 2183 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2183 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 219:  WhereClauseopt ::= $Empty
            //
            case 219:
                setResult(null);
                break;

            //
            // Rule 221:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 221:
                setResult(null);
                break;

            //
            // Rule 223:  ClassModifiersopt ::= $Empty
            //
            case 223: {
               //#line 2198 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2198 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 225:  TypeDefModifiersopt ::= $Empty
            //
            case 225: {
               //#line 2204 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2204 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 227:  ClockedClauseopt ::= $Empty
            //
            case 227: {
               //#line 2210 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2210 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 229:  identifier ::= IDENTIFIER$ident
            //
            case 229: {
               //#line 2221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 230:  TypeName ::= Identifier
            //
            case 230: {
               //#line 2228 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2228 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 231:  TypeName ::= TypeName . Identifier
            //
            case 231: {
               //#line 2233 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2231 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2231 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2233 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 233:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 233: {
               //#line 2245 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2243 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2245 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 234:  TypeArgumentList ::= Type
            //
            case 234: {
               //#line 2252 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2250 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2252 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 235:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 235: {
               //#line 2259 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2257 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2257 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2259 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 236:  PackageName ::= Identifier
            //
            case 236: {
               //#line 2269 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2267 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2269 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 237:  PackageName ::= PackageName . Identifier
            //
            case 237: {
               //#line 2274 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2272 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2272 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2274 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 238:  ExpressionName ::= Identifier
            //
            case 238: {
               //#line 2290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2288 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 239:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 239: {
               //#line 2295 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2293 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2293 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2295 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 240:  MethodName ::= Identifier
            //
            case 240: {
               //#line 2305 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2303 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2305 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 241:  MethodName ::= AmbiguousName . Identifier
            //
            case 241: {
               //#line 2310 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2308 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2308 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2310 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 242:  PackageOrTypeName ::= Identifier
            //
            case 242: {
               //#line 2320 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2320 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 243:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 243: {
               //#line 2325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 244:  AmbiguousName ::= Identifier
            //
            case 244: {
               //#line 2335 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2333 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2335 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 245:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 245: {
               //#line 2340 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2338 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2338 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2340 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 246:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 246: {
               //#line 2352 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2350 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2350 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2350 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2352 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                // Add import x10.lang.* by default.
                int token_pos = (ImportDeclarationsopt.size() == 0
                                     ? TypeDeclarationsopt.size() == 0
                                           ? prsStream.getSize() - 1
                                           : prsStream.getPrevious(getRhsFirstTokenIndex(3))
                                 : getRhsLastTokenIndex(2)
                            );
//                    Import x10LangImport = 
//                    nf.Import(pos(token_pos), Import.PACKAGE, QName.make("x10.lang"));
//                    ImportDeclarationsopt.add(x10LangImport);
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()), PackageDeclarationopt, ImportDeclarationsopt, TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 247:  ImportDeclarations ::= ImportDeclaration
            //
            case 247: {
               //#line 2368 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2366 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2368 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 248:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 248: {
               //#line 2375 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2373 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2373 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2375 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 249:  TypeDeclarations ::= TypeDeclaration
            //
            case 249: {
               //#line 2383 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2381 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2383 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 250:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 250: {
               //#line 2391 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2389 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2389 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2391 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 251:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 251: {
               //#line 2399 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2397 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2397 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2399 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 254:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 254: {
               //#line 2413 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2411 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2413 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 255:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 255: {
               //#line 2419 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2417 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2419 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 259:  TypeDeclaration ::= ;
            //
            case 259: {
               //#line 2434 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2434 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 260:  ClassModifiers ::= ClassModifier
            //
            case 260: {
               //#line 2442 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2442 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 261:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 261: {
               //#line 2449 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2447 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2447 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2449 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 262:  ClassModifier ::= Annotation
            //
            case 262: {
               //#line 2455 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2453 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2455 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 263:  ClassModifier ::= public
            //
            case 263: {
               //#line 2460 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2460 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 264:  ClassModifier ::= protected
            //
            case 264: {
               //#line 2465 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2465 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 265:  ClassModifier ::= private
            //
            case 265: {
               //#line 2470 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2470 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 266:  ClassModifier ::= abstract
            //
            case 266: {
               //#line 2475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 267:  ClassModifier ::= static
            //
            case 267: {
               //#line 2480 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2480 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 268:  ClassModifier ::= final
            //
            case 268: {
               //#line 2485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 269:  ClassModifier ::= safe
            //
            case 269: {
               //#line 2490 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2490 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 270:  TypeDefModifiers ::= TypeDefModifier
            //
            case 270: {
               //#line 2496 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2494 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2496 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 271:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 271: {
               //#line 2503 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2503 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 272:  TypeDefModifier ::= Annotation
            //
            case 272: {
               //#line 2509 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2507 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2509 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 273:  TypeDefModifier ::= public
            //
            case 273: {
               //#line 2514 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2514 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 274:  TypeDefModifier ::= protected
            //
            case 274: {
               //#line 2519 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2519 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 275:  TypeDefModifier ::= private
            //
            case 275: {
               //#line 2524 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2524 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 276:  TypeDefModifier ::= abstract
            //
            case 276: {
               //#line 2529 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2529 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 277:  TypeDefModifier ::= static
            //
            case 277: {
               //#line 2534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 278:  TypeDefModifier ::= final
            //
            case 278: {
               //#line 2539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 279:  Interfaces ::= implements InterfaceTypeList
            //
            case 279: {
               //#line 2548 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2546 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2548 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 280:  InterfaceTypeList ::= Type
            //
            case 280: {
               //#line 2554 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2552 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2554 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 281:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 281: {
               //#line 2561 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2559 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2559 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2561 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 282:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 282: {
               //#line 2571 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2569 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2571 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 284:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 284: {
               //#line 2578 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2576 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2576 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2578 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 286:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 286: {
               //#line 2600 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2598 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2600 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 288:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 288: {
               //#line 2609 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2607 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2609 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 289:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 289: {
               //#line 2616 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2614 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2616 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 290: {
               //#line 2623 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2621 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2623 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 291:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 291: {
               //#line 2630 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2628 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2630 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 292:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 292: {
               //#line 2637 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2635 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2637 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 293:  ClassMemberDeclaration ::= ;
            //
            case 293: {
               //#line 2644 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2644 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 294:  FormalDeclarators ::= FormalDeclarator
            //
            case 294: {
               //#line 2651 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2649 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2651 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 295: {
               //#line 2658 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2656 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2656 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2658 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 296:  FieldDeclarators ::= FieldDeclarator
            //
            case 296: {
               //#line 2665 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2663 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2665 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 297: {
               //#line 2672 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2670 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2670 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2672 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 298:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 298: {
               //#line 2680 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2678 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2680 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 299: {
               //#line 2687 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2685 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2685 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2687 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 300:  VariableDeclarators ::= VariableDeclarator
            //
            case 300: {
               //#line 2694 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2692 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2694 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 301: {
               //#line 2701 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2699 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2699 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2701 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 303:  FieldModifiers ::= FieldModifier
            //
            case 303: {
               //#line 2710 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2708 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2710 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 304:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 304: {
               //#line 2717 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2715 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2715 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2717 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 305:  FieldModifier ::= Annotation
            //
            case 305: {
               //#line 2723 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2721 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2723 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 306:  FieldModifier ::= public
            //
            case 306: {
               //#line 2728 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2728 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 307:  FieldModifier ::= protected
            //
            case 307: {
               //#line 2733 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2733 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 308:  FieldModifier ::= private
            //
            case 308: {
               //#line 2738 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2738 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 309:  FieldModifier ::= static
            //
            case 309: {
               //#line 2743 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2743 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 310:  FieldModifier ::= global
            //
            case 310: {
               //#line 2748 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2748 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 311:  ResultType ::= : Type
            //
            case 311: {
               //#line 2754 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2752 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2754 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 312:  HasResultType ::= : Type
            //
            case 312: {
               //#line 2759 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2757 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2759 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 313:  HasResultType ::= <: Type
            //
            case 313: {
               //#line 2764 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2762 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2764 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 314:  FormalParameters ::= ( FormalParameterList )
            //
            case 314: {
               //#line 2770 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2768 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2770 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterList);
                      break;
            }
    
            //
            // Rule 315:  FormalParameterList ::= FormalParameter
            //
            case 315: {
               //#line 2776 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2774 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2776 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 316:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 316: {
               //#line 2783 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2781 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2781 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2783 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 317:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 317: {
               //#line 2789 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2787 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2787 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2789 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 318:  LoopIndexDeclarator ::= ( IdentifierList ) HasResultTypeopt
            //
            case 318: {
               //#line 2794 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2792 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2792 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2794 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 319:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt
            //
            case 319: {
               //#line 2799 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2797 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2797 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2797 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2799 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 320:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 320: {
               //#line 2805 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2803 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2803 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2805 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
                        	FlagsNode fn = extractFlags(VariableModifiersopt, Flags.FINAL);
            Object[] o = LoopIndexDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
            boolean unnamed = name == null;
            if (name == null) name = nf.Id(pos, Name.makeFresh());
               List exploded = (List) o[2];
                        DepParameterExpr guard = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                        if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                        List explodedFormals = new ArrayList();
                        for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                        	Id id = (Id) i.next();
                        	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
                        }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
            setResult(f);
                      break;
            }
    
            //
            // Rule 321:  LoopIndex ::= VariableModifiersopt VarKeyword LoopIndexDeclarator
            //
            case 321: {
               //#line 2828 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2826 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2826 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2826 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2828 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
                        	FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
            Object[] o = LoopIndexDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
            boolean unnamed = name == null;
            if (name == null) name = nf.Id(pos, Name.makeFresh());
               List exploded = (List) o[2];
                        DepParameterExpr guard = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                                                    if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                                                    List explodedFormals = new ArrayList();
                        for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                        	Id id = (Id) i.next();
                        	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
                        }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
            setResult(f);
                      break;
            }
    
            //
            // Rule 322:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 322: {
               //#line 2852 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2850 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2850 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2852 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
                        	FlagsNode fn = extractFlags(VariableModifiersopt, Flags.FINAL);
            Object[] o = FormalDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
            boolean unnamed = name == null;
            if (name == null) name = nf.Id(pos, Name.makeFresh());
               List exploded = (List) o[2];
                        DepParameterExpr guard = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                        if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                        Expr init = (Expr) o[5];
                        List explodedFormals = new ArrayList();
                        for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                        	Id id = (Id) i.next();
                        	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
                        }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
            setResult(f);
                      break;
            }
    
            //
            // Rule 323:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 323: {
               //#line 2876 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2874 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2874 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2874 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2876 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
                        	FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
            Object[] o = FormalDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
            boolean unnamed = name == null;
            if (name == null) name = nf.Id(pos, Name.makeFresh());
               List exploded = (List) o[2];
                        DepParameterExpr guard = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                                                    if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                        Expr init = (Expr) o[5];
                                                    List explodedFormals = new ArrayList();
                        for (Iterator i = exploded.iterator(); i.hasNext(); ) {
                        	Id id = (Id) i.next();
                        	explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
                        }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(VariableModifiersopt));
            setResult(f);
                      break;
            }
    
            //
            // Rule 324:  FormalParameter ::= Type
            //
            case 324: {
               //#line 2900 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2898 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2900 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 325:  VariableModifiers ::= VariableModifier
            //
            case 325: {
               //#line 2908 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2906 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2908 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 326:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 326: {
               //#line 2915 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2913 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2913 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2915 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 327:  VariableModifier ::= Annotation
            //
            case 327: {
               //#line 2921 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2919 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2921 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 328:  VariableModifier ::= shared
            //
            case 328: {
               //#line 2926 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2926 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 329:  MethodModifiers ::= MethodModifier
            //
            case 329: {
               //#line 2935 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2933 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2935 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 330:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 330: {
               //#line 2942 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2940 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2940 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2942 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 331:  MethodModifier ::= Annotation
            //
            case 331: {
               //#line 2948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2946 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 332:  MethodModifier ::= public
            //
            case 332: {
               //#line 2953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 333:  MethodModifier ::= protected
            //
            case 333: {
               //#line 2958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 334:  MethodModifier ::= private
            //
            case 334: {
               //#line 2963 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2963 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 335:  MethodModifier ::= abstract
            //
            case 335: {
               //#line 2968 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2968 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 336:  MethodModifier ::= static
            //
            case 336: {
               //#line 2973 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2973 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 337:  MethodModifier ::= final
            //
            case 337: {
               //#line 2978 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2978 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 338:  MethodModifier ::= native
            //
            case 338: {
               //#line 2983 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2983 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 339:  MethodModifier ::= atomic
            //
            case 339: {
               //#line 2988 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2988 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 340:  MethodModifier ::= extern
            //
            case 340: {
               //#line 2993 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2993 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= safe
            //
            case 341: {
               //#line 2998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= sequential
            //
            case 342: {
               //#line 3003 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3003 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= nonblocking
            //
            case 343: {
               //#line 3008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= incomplete
            //
            case 344: {
               //#line 3013 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3013 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= property
            //
            case 345: {
               //#line 3018 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3018 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= global
            //
            case 346: {
               //#line 3023 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3023 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= proto
            //
            case 347: {
               //#line 3028 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3028 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROTO)));
                      break;
            }
    
            //
            // Rule 348:  Throws ::= throws ExceptionTypeList
            //
            case 348: {
               //#line 3035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3033 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 349:  Offers ::= offers Type
            //
            case 349: {
               //#line 3040 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3038 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3040 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 350:  ExceptionTypeList ::= ExceptionType
            //
            case 350: {
               //#line 3046 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3044 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3046 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 351:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 351: {
               //#line 3053 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3051 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3051 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3053 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 353:  MethodBody ::= = LastExpression ;
            //
            case 353: {
               //#line 3061 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3059 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3061 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 354:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 354: {
               //#line 3066 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3064 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3064 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(4);
                //#line 3064 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3066 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 355:  MethodBody ::= = Annotationsopt Block
            //
            case 355: {
               //#line 3074 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3072 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3072 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3074 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 356:  MethodBody ::= Annotationsopt Block
            //
            case 356: {
               //#line 3079 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3077 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 3077 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3079 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 357:  MethodBody ::= ;
            //
            case 357:
                setResult(null);
                break;

            //
            // Rule 358:  SimpleTypeName ::= Identifier
            //
            case 358: {
               //#line 3099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3097 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 359:  ConstructorModifiers ::= ConstructorModifier
            //
            case 359: {
               //#line 3105 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3103 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3105 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 360:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 360: {
               //#line 3112 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3110 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3110 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3112 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 361:  ConstructorModifier ::= Annotation
            //
            case 361: {
               //#line 3118 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3116 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3118 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 362:  ConstructorModifier ::= public
            //
            case 362: {
               //#line 3123 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3123 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 363:  ConstructorModifier ::= protected
            //
            case 363: {
               //#line 3128 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3128 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 364:  ConstructorModifier ::= private
            //
            case 364: {
               //#line 3133 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3133 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 365:  ConstructorModifier ::= native
            //
            case 365: {
               //#line 3138 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3138 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 366:  ConstructorBody ::= = ConstructorBlock
            //
            case 366: {
               //#line 3144 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3142 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3144 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 367:  ConstructorBody ::= ConstructorBlock
            //
            case 367: {
               //#line 3149 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3147 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3149 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 368:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 368: {
               //#line 3154 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3152 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3154 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 369:  ConstructorBody ::= = AssignPropertyCall
            //
            case 369: {
               //#line 3162 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3160 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3162 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 370:  ConstructorBody ::= ;
            //
            case 370:
                setResult(null);
                break;

            //
            // Rule 371:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 371: {
               //#line 3173 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3171 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3171 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3173 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                if (ExplicitConstructorInvocationopt != null)
                {
                    l.add(ExplicitConstructorInvocationopt);
                }
                l.addAll(BlockStatementsopt);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 372:  Arguments ::= ( ArgumentListopt )
            //
            case 372: {
               //#line 3186 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3184 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3186 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 374:  InterfaceModifiers ::= InterfaceModifier
            //
            case 374: {
               //#line 3196 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3194 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3196 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 375:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 375: {
               //#line 3203 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3201 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3201 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3203 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 376:  InterfaceModifier ::= Annotation
            //
            case 376: {
               //#line 3209 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3207 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3209 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 377:  InterfaceModifier ::= public
            //
            case 377: {
               //#line 3214 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3214 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 378:  InterfaceModifier ::= protected
            //
            case 378: {
               //#line 3219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 379:  InterfaceModifier ::= private
            //
            case 379: {
               //#line 3224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 380:  InterfaceModifier ::= abstract
            //
            case 380: {
               //#line 3229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 381:  InterfaceModifier ::= static
            //
            case 381: {
               //#line 3234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 382:  ExtendsInterfaces ::= extends Type
            //
            case 382: {
               //#line 3240 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3238 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3240 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 383:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 383: {
               //#line 3247 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3245 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3245 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3247 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 384:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 384: {
               //#line 3256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3254 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 386:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 386: {
               //#line 3263 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3261 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3261 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3263 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 387:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 387: {
               //#line 3270 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3268 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3270 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 388:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 388: {
               //#line 3277 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3275 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3277 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 389:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 389: {
               //#line 3284 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3282 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3284 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 390:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 390: {
               //#line 3291 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3289 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3291 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 391:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 391: {
               //#line 3298 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3296 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3298 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 392:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 392: {
               //#line 3305 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3303 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3305 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 393:  InterfaceMemberDeclaration ::= ;
            //
            case 393: {
               //#line 3312 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3312 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 394:  Annotations ::= Annotation
            //
            case 394: {
               //#line 3318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3316 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 395:  Annotations ::= Annotations Annotation
            //
            case 395: {
               //#line 3325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 396:  Annotation ::= @ NamedType
            //
            case 396: {
               //#line 3331 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3329 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3331 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 397:  SimpleName ::= Identifier
            //
            case 397: {
               //#line 3337 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3335 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3337 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 398:  Identifier ::= identifier
            //
            case 398: {
               //#line 3343 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3341 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3343 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 399:  VariableInitializers ::= VariableInitializer
            //
            case 399: {
               //#line 3351 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3349 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3351 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 400:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 400: {
               //#line 3358 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3356 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3356 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3358 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 401:  Block ::= { BlockStatementsopt }
            //
            case 401: {
               //#line 3376 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3374 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3376 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 402:  BlockStatements ::= BlockStatement
            //
            case 402: {
               //#line 3382 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3380 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3382 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 403:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 403: {
               //#line 3389 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3387 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3387 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3389 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 405:  BlockStatement ::= ClassDeclaration
            //
            case 405: {
               //#line 3397 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3395 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3397 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 406:  BlockStatement ::= TypeDefDeclaration
            //
            case 406: {
               //#line 3404 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3402 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3404 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 407:  BlockStatement ::= Statement
            //
            case 407: {
               //#line 3411 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3409 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3411 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 408:  IdentifierList ::= Identifier
            //
            case 408: {
               //#line 3419 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3417 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3419 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 409:  IdentifierList ::= IdentifierList , Identifier
            //
            case 409: {
               //#line 3426 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3424 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3424 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3426 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 410:  FormalDeclarator ::= Identifier ResultType
            //
            case 410: {
               //#line 3432 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3430 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3430 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3432 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 411:  FormalDeclarator ::= ( IdentifierList ) ResultType
            //
            case 411: {
               //#line 3437 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3435 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3435 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3437 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 412:  FormalDeclarator ::= Identifier ( IdentifierList ) ResultType
            //
            case 412: {
               //#line 3442 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3442 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 413:  FieldDeclarator ::= Identifier HasResultType
            //
            case 413: {
               //#line 3448 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3446 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3446 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3448 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultType, null });
                      break;
            }
    
            //
            // Rule 414:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 414: {
               //#line 3453 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3451 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3451 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3451 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3453 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 415:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 415: {
               //#line 3459 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3457 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3457 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3457 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3459 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 416:  VariableDeclarator ::= ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 416: {
               //#line 3464 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3462 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3462 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3462 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3464 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 417:  VariableDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 417: {
               //#line 3469 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3467 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3467 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3467 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3467 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3469 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 418:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 418: {
               //#line 3475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3473 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3473 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3473 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 419:  VariableDeclaratorWithType ::= ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 419: {
               //#line 3480 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3478 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3478 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 3478 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3480 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 420:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 420: {
               //#line 3485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 3483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 422:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 422: {
               //#line 3493 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3493 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
    
                List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                List s = new TypedList(new LinkedList(), Stmt.class, false);
                    for (Iterator i = VariableDeclarators.iterator(); i.hasNext(); )
                    {
                        Object[] o = (Object[]) i.next();
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        if (name == null) name = nf.Id(pos, Name.makeFresh());
                        List exploded = (List) o[2];
                        DepParameterExpr guard = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                        if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                        Expr init = (Expr) o[5];
                        LocalDecl ld = nf.LocalDecl(pos, fn,
                                           type, name, init);
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(VariableModifiersopt));
                        int index = 0;
                        l.add(ld);
                        for (Iterator j = exploded.iterator(); j.hasNext(); ) {
                        	Id id = (Id) j.next();
                        	TypeNode tni = nf.UnknownTypeNode(id.position());
                        	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(JPGPosition.COMPILER_GENERATED, nf.Local(JPGPosition.COMPILER_GENERATED, name),  Collections.<Expr>singletonList(nf.IntLit(JPGPosition.COMPILER_GENERATED, IntLit.INT, index))) : null));
                        	index++;
                        }
                    }
                l.addAll(s); 
                setResult(l);
                      break;
            }
    
            //
            // Rule 423:  LocalVariableDeclaration ::= VariableModifiersopt VariableDeclaratorsWithType
            //
            case 423: {
               //#line 3526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3524 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3524 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = extractFlags(VariableModifiersopt, Flags.FINAL);
    
                List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                List s = new TypedList(new LinkedList(), Stmt.class, false);
                    for (Iterator i = VariableDeclaratorsWithType.iterator(); i.hasNext(); )
                    {
                        Object[] o = (Object[]) i.next();
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        if (name == null) name = nf.Id(pos, Name.makeFresh());
                        List exploded = (List) o[2];
                        DepParameterExpr guard = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                        if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                        Expr init = (Expr) o[5];
                        LocalDecl ld = nf.LocalDecl(pos, fn,
                                           type, name, init);
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(VariableModifiersopt));
                        int index = 0;
                        l.add(ld);
                        for (Iterator j = exploded.iterator(); j.hasNext(); ) {
                        	Id id = (Id) j.next();
                        	// HACK: if the local is non-final, assume the type is point and the component is int
                        	TypeNode tni = nf.UnknownTypeNode(id.position());
                        	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(JPGPosition.COMPILER_GENERATED, nf.Local(JPGPosition.COMPILER_GENERATED, name),  Collections.<Expr>singletonList(nf.IntLit(JPGPosition.COMPILER_GENERATED, IntLit.INT, index))) : null));
                        	index++;
                        }
                    }
                l.addAll(s); 
                setResult(l);
                      break;
            }
    
            //
            // Rule 424:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 424: {
               //#line 3560 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3558 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3558 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3558 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3560 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = extractFlags(VariableModifiersopt, VarKeyword);
    
                List l = new TypedList(new LinkedList(), LocalDecl.class, false);
                List s = new TypedList(new LinkedList(), Stmt.class, false);
                    for (Iterator i = FormalDeclarators.iterator(); i.hasNext(); )
                    {
                        Object[] o = (Object[]) i.next();
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        if (name == null) name = nf.Id(pos, Name.makeFresh());
                        List exploded = (List) o[2];
                        DepParameterExpr guard = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                                                    if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                        Expr init = (Expr) o[5];
                        LocalDecl ld = nf.LocalDecl(pos, fn,
                                           type, name, init);
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(VariableModifiersopt));
                        int index = 0;
                        l.add(ld);
                        for (Iterator j = exploded.iterator(); j.hasNext(); ) {
                        	Id id = (Id) j.next();
                        	// HACK: if the local is non-final, assume the type is point and the component is int
                        	TypeNode tni = nf.UnknownTypeNode(id.position());
                        // todo: fixme: do this desugaring after type-checking, and remove this code duplication 
                        	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(JPGPosition.COMPILER_GENERATED, nf.Local(JPGPosition.COMPILER_GENERATED, name),  Collections.<Expr>singletonList(nf.IntLit(JPGPosition.COMPILER_GENERATED, IntLit.INT, index))) : null));
                        	index++;
                        }
                    }
                l.addAll(s); 
                setResult(l);
                      break;
            }
    
            //
            // Rule 425:  Primary ::= here
            //
            case 425: {
               //#line 3601 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3601 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 426:  Primary ::= [ ArgumentListopt ]
            //
            case 426: {
               //#line 3607 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3605 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3607 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 428:  Primary ::= self
            //
            case 428: {
               //#line 3615 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3615 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 429:  Primary ::= this
            //
            case 429: {
               //#line 3620 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3620 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 430:  Primary ::= ClassName . this
            //
            case 430: {
               //#line 3625 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3623 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3625 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 431:  Primary ::= ( Expression )
            //
            case 431: {
               //#line 3630 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3628 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3630 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 437:  OperatorFunction ::= TypeName . +
            //
            case 437: {
               //#line 3641 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3639 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3641 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.ADD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 438:  OperatorFunction ::= TypeName . -
            //
            case 438: {
               //#line 3652 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3650 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3652 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SUB, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 439:  OperatorFunction ::= TypeName . *
            //
            case 439: {
               //#line 3663 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3661 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3663 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MUL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 440:  OperatorFunction ::= TypeName . /
            //
            case 440: {
               //#line 3674 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3672 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3674 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.DIV, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 441:  OperatorFunction ::= TypeName . %
            //
            case 441: {
               //#line 3685 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3683 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3685 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MOD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 442:  OperatorFunction ::= TypeName . &
            //
            case 442: {
               //#line 3696 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3694 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3696 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_AND, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 443:  OperatorFunction ::= TypeName . |
            //
            case 443: {
               //#line 3707 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3705 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3707 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_OR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 444:  OperatorFunction ::= TypeName . ^
            //
            case 444: {
               //#line 3718 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3716 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3718 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_XOR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 445:  OperatorFunction ::= TypeName . <<
            //
            case 445: {
               //#line 3729 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3727 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3729 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 446:  OperatorFunction ::= TypeName . >>
            //
            case 446: {
               //#line 3740 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3738 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3740 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 447:  OperatorFunction ::= TypeName . >>>
            //
            case 447: {
               //#line 3751 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3749 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3751 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.USHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 448:  OperatorFunction ::= TypeName . <
            //
            case 448: {
               //#line 3762 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3760 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3762 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 449:  OperatorFunction ::= TypeName . <=
            //
            case 449: {
               //#line 3773 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3771 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3773 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 450:  OperatorFunction ::= TypeName . >=
            //
            case 450: {
               //#line 3784 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3782 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3784 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 451:  OperatorFunction ::= TypeName . >
            //
            case 451: {
               //#line 3795 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3793 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3795 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 452:  OperatorFunction ::= TypeName . ==
            //
            case 452: {
               //#line 3806 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3804 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3806 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.EQ, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 453:  OperatorFunction ::= TypeName . !=
            //
            case 453: {
               //#line 3817 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3815 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3817 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.NE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 454:  Literal ::= IntegerLiteral$lit
            //
            case 454: {
               //#line 3830 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3828 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3830 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 455:  Literal ::= LongLiteral$lit
            //
            case 455: {
               //#line 3836 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3834 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3836 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 456:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 456: {
               //#line 3842 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3840 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3842 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 457:  Literal ::= UnsignedLongLiteral$lit
            //
            case 457: {
               //#line 3848 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3846 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3848 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 458:  Literal ::= FloatingPointLiteral$lit
            //
            case 458: {
               //#line 3854 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3852 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3854 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 459:  Literal ::= DoubleLiteral$lit
            //
            case 459: {
               //#line 3860 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3858 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3860 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 460:  Literal ::= BooleanLiteral
            //
            case 460: {
               //#line 3866 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3864 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3866 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 461:  Literal ::= CharacterLiteral$lit
            //
            case 461: {
               //#line 3871 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3869 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3871 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 462:  Literal ::= StringLiteral$str
            //
            case 462: {
               //#line 3877 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3875 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3877 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 463:  Literal ::= null
            //
            case 463: {
               //#line 3883 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3883 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 464:  BooleanLiteral ::= true$trueLiteral
            //
            case 464: {
               //#line 3889 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3887 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3889 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 465:  BooleanLiteral ::= false$falseLiteral
            //
            case 465: {
               //#line 3894 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3892 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3894 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 466:  ArgumentList ::= Expression
            //
            case 466: {
               //#line 3903 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3901 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3903 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 467:  ArgumentList ::= ArgumentList , Expression
            //
            case 467: {
               //#line 3910 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3908 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3908 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3910 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 468:  FieldAccess ::= Primary . Identifier
            //
            case 468: {
               //#line 3916 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3914 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3914 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3916 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 469:  FieldAccess ::= super . Identifier
            //
            case 469: {
               //#line 3921 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3919 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3921 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 470:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 470: {
               //#line 3926 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3924 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3924 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3924 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3926 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 471:  FieldAccess ::= Primary . class$c
            //
            case 471: {
               //#line 3931 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3929 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3929 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3931 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 472:  FieldAccess ::= super . class$c
            //
            case 472: {
               //#line 3936 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3934 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3936 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 473:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 473: {
               //#line 3941 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3939 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3939 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3939 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 3941 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 474:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 474: {
               //#line 3947 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3945 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3945 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3945 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3947 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 475:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 475: {
               //#line 3954 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3952 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3952 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3952 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3952 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3954 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 476:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 476: {
               //#line 3959 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3957 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3957 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3957 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3959 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 477:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 477: {
               //#line 3964 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3962 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3962 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3962 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3962 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3962 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3964 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 478:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 478: {
               //#line 3969 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3969 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (Primary instanceof Field) {
                    Field f = (Field) Primary;
                    setResult(nf.X10Call(pos(), f.target(), f.name(), TypeArgumentsopt, ArgumentListopt));
                }
                else if (Primary instanceof AmbExpr) {
                    AmbExpr f = (AmbExpr) Primary;
                    setResult(nf.X10Call(pos(), null, f.name(), TypeArgumentsopt, ArgumentListopt));
                }
                else if (Primary instanceof Here) {
                    Here f = (Here) Primary;
                    setResult(nf.X10Call(pos(), null, nf.Id(Primary.position(), Name.make("here")), TypeArgumentsopt, ArgumentListopt));
                }
                else {
                    setResult(nf.ClosureCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                }
                      break;
            }
    
            //
            // Rule 479:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 479: {
               //#line 3989 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3987 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3987 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 3989 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, Collections.EMPTY_LIST, actuals), true))));
                      break;
            }
    
            //
            // Rule 480:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 480: {
               //#line 4002 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4000 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4000 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4000 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4002 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                  List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), Primary, Identifier, Collections.EMPTY_LIST, actuals), true))));
                      break;
            }
    
            //
            // Rule 481:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 481: {
               //#line 4014 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4012 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4012 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4014 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, Collections.EMPTY_LIST, actuals), true))));
                      break;
            }
    
            //
            // Rule 482:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 482: {
               //#line 4026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4024 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4024 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4024 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4024 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(8);
                //#line 4026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, 
                                                          Collections.EMPTY_LIST, actuals), true))));
                      break;
            }
    
            //
            // Rule 486:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 486: {
               //#line 4044 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4042 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4044 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 487:  PostDecrementExpression ::= PostfixExpression --
            //
            case 487: {
               //#line 4050 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4048 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4050 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 490:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 490: {
               //#line 4058 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4056 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4058 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 491:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 491: {
               //#line 4063 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4061 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4063 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 493:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 493: {
               //#line 4070 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4068 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4070 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 494:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 494: {
               //#line 4076 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4074 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4076 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 496:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 496: {
               //#line 4083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4081 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 497:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 497: {
               //#line 4088 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4086 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4086 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4088 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 498:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 498: {
               //#line 4095 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4093 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4095 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 500:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 500: {
               //#line 4102 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4100 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4100 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4102 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 501:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 501: {
               //#line 4107 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4105 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4105 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4107 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 502:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 502: {
               //#line 4112 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4110 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4110 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4112 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 504:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 504: {
               //#line 4119 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4117 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4117 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4119 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 505:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 505: {
               //#line 4124 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4122 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4122 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4124 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 507:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 507: {
               //#line 4131 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4129 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4129 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4131 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 508:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 508: {
               //#line 4136 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4134 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4134 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4136 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 509:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 509: {
               //#line 4141 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4139 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4139 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4141 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 511:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 511: {
               //#line 4148 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4146 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4146 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4148 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 514:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 514: {
               //#line 4157 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4157 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 515:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 515: {
               //#line 4162 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4160 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4160 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4162 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 516:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 516: {
               //#line 4167 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4165 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4165 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4167 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 517:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 517: {
               //#line 4172 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4170 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4170 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4172 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 518:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 518: {
               //#line 4177 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4175 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4175 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4177 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 519:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 519: {
               //#line 4182 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4180 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4180 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4182 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 521:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 521: {
               //#line 4189 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4187 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4187 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4189 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 522:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 522: {
               //#line 4194 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4192 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4192 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4194 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 523:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 523: {
               //#line 4199 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4197 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4197 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4199 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 525:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 525: {
               //#line 4206 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4204 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4204 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4206 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 527:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 527: {
               //#line 4213 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4211 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4211 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4213 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 529:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 529: {
               //#line 4220 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4220 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 531:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 531: {
               //#line 4227 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4225 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4225 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4227 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 533:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 533: {
               //#line 4234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4232 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4232 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 540:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 540: {
               //#line 4247 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4245 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4245 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4245 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4247 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 543:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 543: {
               //#line 4256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4254 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4254 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4254 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 544:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 544: {
               //#line 4261 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4259 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4259 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4259 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4259 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4261 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 545:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 545: {
               //#line 4266 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4264 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4264 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4264 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4264 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4266 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 546:  LeftHandSide ::= ExpressionName
            //
            case 546: {
               //#line 4272 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4270 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4272 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 548:  AssignmentOperator ::= =
            //
            case 548: {
               //#line 4279 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4279 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 549:  AssignmentOperator ::= *=
            //
            case 549: {
               //#line 4284 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4284 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 550:  AssignmentOperator ::= /=
            //
            case 550: {
               //#line 4289 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4289 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 551:  AssignmentOperator ::= %=
            //
            case 551: {
               //#line 4294 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4294 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 552:  AssignmentOperator ::= +=
            //
            case 552: {
               //#line 4299 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4299 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 553:  AssignmentOperator ::= -=
            //
            case 553: {
               //#line 4304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 554:  AssignmentOperator ::= <<=
            //
            case 554: {
               //#line 4309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 555:  AssignmentOperator ::= >>=
            //
            case 555: {
               //#line 4314 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4314 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 556:  AssignmentOperator ::= >>>=
            //
            case 556: {
               //#line 4319 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4319 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 557:  AssignmentOperator ::= &=
            //
            case 557: {
               //#line 4324 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4324 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 558:  AssignmentOperator ::= ^=
            //
            case 558: {
               //#line 4329 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4329 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= |=
            //
            case 559: {
               //#line 4334 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4334 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 562:  PrefixOp ::= +
            //
            case 562: {
               //#line 4345 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4345 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 563:  PrefixOp ::= -
            //
            case 563: {
               //#line 4350 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4350 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 564:  PrefixOp ::= !
            //
            case 564: {
               //#line 4355 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4355 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 565:  PrefixOp ::= ~
            //
            case 565: {
               //#line 4360 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4360 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 566:  BinOp ::= +
            //
            case 566: {
               //#line 4366 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4366 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 567:  BinOp ::= -
            //
            case 567: {
               //#line 4371 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4371 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 568:  BinOp ::= *
            //
            case 568: {
               //#line 4376 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4376 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 569:  BinOp ::= /
            //
            case 569: {
               //#line 4381 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4381 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 570:  BinOp ::= %
            //
            case 570: {
               //#line 4386 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4386 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 571:  BinOp ::= &
            //
            case 571: {
               //#line 4391 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4391 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 572:  BinOp ::= |
            //
            case 572: {
               //#line 4396 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4396 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 573:  BinOp ::= ^
            //
            case 573: {
               //#line 4401 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4401 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 574:  BinOp ::= &&
            //
            case 574: {
               //#line 4406 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4406 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 575:  BinOp ::= ||
            //
            case 575: {
               //#line 4411 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4411 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 576:  BinOp ::= <<
            //
            case 576: {
               //#line 4416 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4416 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 577:  BinOp ::= >>
            //
            case 577: {
               //#line 4421 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4421 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 578:  BinOp ::= >>>
            //
            case 578: {
               //#line 4426 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4426 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 579:  BinOp ::= >=
            //
            case 579: {
               //#line 4431 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4431 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 580:  BinOp ::= <=
            //
            case 580: {
               //#line 4436 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4436 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 581:  BinOp ::= >
            //
            case 581: {
               //#line 4441 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4441 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 582:  BinOp ::= <
            //
            case 582: {
               //#line 4446 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4446 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 583:  BinOp ::= ==
            //
            case 583: {
               //#line 4454 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4454 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 584:  BinOp ::= !=
            //
            case 584: {
               //#line 4459 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4459 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 585:  Catchesopt ::= $Empty
            //
            case 585: {
               //#line 4468 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4468 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 587:  Identifieropt ::= $Empty
            //
            case 587:
                setResult(null);
                break;

            //
            // Rule 588:  Identifieropt ::= Identifier
            //
            case 588: {
               //#line 4477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 589:  ForUpdateopt ::= $Empty
            //
            case 589: {
               //#line 4483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 591:  Expressionopt ::= $Empty
            //
            case 591:
                setResult(null);
                break;

            //
            // Rule 593:  ForInitopt ::= $Empty
            //
            case 593: {
               //#line 4494 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4494 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 595:  SwitchLabelsopt ::= $Empty
            //
            case 595: {
               //#line 4501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 597:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 597: {
               //#line 4508 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4508 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 599:  VariableModifiersopt ::= $Empty
            //
            case 599: {
               //#line 4515 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4515 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 601:  VariableInitializersopt ::= $Empty
            //
            case 601:
                setResult(null);
                break;

            //
            // Rule 603:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 603: {
               //#line 4526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 605:  ExtendsInterfacesopt ::= $Empty
            //
            case 605: {
               //#line 4533 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4533 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 607:  InterfaceModifiersopt ::= $Empty
            //
            case 607: {
               //#line 4540 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4540 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 609:  ClassBodyopt ::= $Empty
            //
            case 609:
                setResult(null);
                break;

            //
            // Rule 611:  Argumentsopt ::= $Empty
            //
            case 611: {
               //#line 4551 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4551 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 613:  ArgumentListopt ::= $Empty
            //
            case 613: {
               //#line 4558 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4558 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 615:  BlockStatementsopt ::= $Empty
            //
            case 615: {
               //#line 4565 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4565 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 617:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 617:
                setResult(null);
                break;

            //
            // Rule 619:  ConstructorModifiersopt ::= $Empty
            //
            case 619: {
               //#line 4576 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4576 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 621:  FormalParameterListopt ::= $Empty
            //
            case 621: {
               //#line 4583 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4583 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 623:  Throwsopt ::= $Empty
            //
            case 623: {
               //#line 4590 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4590 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 625:  Offersopt ::= $Empty
            //
            case 625: {
               //#line 4596 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4596 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 627:  MethodModifiersopt ::= $Empty
            //
            case 627: {
               //#line 4603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 629:  TypeModifieropt ::= $Empty
            //
            case 629: {
               //#line 4610 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4610 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 631:  FieldModifiersopt ::= $Empty
            //
            case 631: {
               //#line 4617 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4617 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 633:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 633: {
               //#line 4624 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4624 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 635:  Interfacesopt ::= $Empty
            //
            case 635: {
               //#line 4631 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4631 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 637:  Superopt ::= $Empty
            //
            case 637:
                setResult(null);
                break;

            //
            // Rule 639:  TypeParametersopt ::= $Empty
            //
            case 639: {
               //#line 4642 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4642 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 641:  FormalParametersopt ::= $Empty
            //
            case 641: {
               //#line 4649 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4649 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 643:  Annotationsopt ::= $Empty
            //
            case 643: {
               //#line 4656 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4656 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 645:  TypeDeclarationsopt ::= $Empty
            //
            case 645: {
               //#line 4663 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4663 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 647:  ImportDeclarationsopt ::= $Empty
            //
            case 647: {
               //#line 4670 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4670 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 649:  PackageDeclarationopt ::= $Empty
            //
            case 649:
                setResult(null);
                break;

            //
            // Rule 651:  ResultTypeopt ::= $Empty
            //
            case 651:
                setResult(null);
                break;

            //
            // Rule 653:  HasResultTypeopt ::= $Empty
            //
            case 653:
                setResult(null);
                break;

            //
            // Rule 655:  TypeArgumentsopt ::= $Empty
            //
            case 655: {
               //#line 4688 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4688 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 657:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 657: {
               //#line 4695 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4695 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 659:  Propertiesopt ::= $Empty
            //
            case 659: {
               //#line 4702 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4702 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

